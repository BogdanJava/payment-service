package by.bahdan.paymentplatform.service

import by.bahdan.paymentplatform.exception.InvalidPaymentMethodException
import by.bahdan.paymentplatform.exception.InvalidRequestBodyParamException
import by.bahdan.paymentplatform.exception.PlatformException
import by.bahdan.paymentplatform.exception.PriceModifierOutOfPriceRangeException
import by.bahdan.paymentplatform.model.*
import by.bahdan.paymentplatform.repository.PaymentRequestRepository
import by.bahdan.paymentplatform.utils.parseISODate
import by.bahdan.paymentplatform.utils.toISODateString
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Service
class PaymentService(
    val paymentMethods: List<PaymentMethod>,
    val objectMapper: ObjectMapper,
    val repo: PaymentRequestRepository
) {

    private data class SalesPoints(
        var sales: Double,
        var points: Int
    )

    fun getHourlySalesData(salesRequestDTO: GetSalesRequestDTO): HourlySalesData {
        val salesRequest = salesRequestDTO.validateAndParse()

        val payments = repo.getPaymentsWithinRange(salesRequest.startDateTime, salesRequest.endDateTime)
            .sortedBy { it.datetime }

        val salesInfo = payments.map {
            val calendar = Calendar.getInstance()
            calendar.time = it.datetime
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time to it
        }
            .groupBy { it.first }
            .map { dateGroup ->
                dateGroup.key to dateGroup.value.map { SalesPoints(it.second.finalPrice, it.second.points) }
                    .reduce { acc, salesPoints ->
                        acc.points += salesPoints.points
                        acc.sales += salesPoints.sales
                        acc
                    }
            }.map {
                HourlySalesDataEntry(
                    datetime = it.first.toISODateString(),
                    sales = BigDecimal.valueOf(it.second.sales).setScale(2, RoundingMode.HALF_UP).toString(),
                    points = it.second.points
                )
            }

        return HourlySalesData(salesInfo)
    }

    fun makePayment(dto: GraphqlPaymentRequestDTO): PaymentResponse {
        val paymentRequest = validateAndParseDTO(dto)
        return savePayment(paymentRequest)
    }

    fun makePayment(dto: RestPaymentRequestDTO): PaymentResponse {
        val paymentRequest = validateAndParseDTO(dto)
        return savePayment(paymentRequest)
    }

    private fun savePayment(paymentRequest: PaymentRequest): PaymentResponse {
        paymentRequest.id = UUID.randomUUID().toString()
        val paymentRequestResult = repo.save(paymentRequest)
        return PaymentResponse(
            finalPrice = paymentRequestResult.finalPrice.toString(),
            points = paymentRequestResult.points
        )
    }

    private fun <T : Any?> validateAndParseDTO(dto: PaymentRequestDTO<T>): PaymentRequest {
        return try {
            val paymentMethod = paymentMethods.find { it.name == dto.paymentMethod }
                ?: throw InvalidPaymentMethodException(dto.paymentMethod)
            PaymentRequest(
                customerId = dto.customerId.toLong(),
                price = if (dto.price.toDouble() > 0) dto.price.toDouble() else
                    throw InvalidRequestBodyParamException("price"),
                priceModifier = if (paymentMethod.finalPriceRange.within(dto.priceModifier)) dto.priceModifier else
                    throw PriceModifierOutOfPriceRangeException(dto.priceModifier, paymentMethod),
                paymentMethod = paymentMethod,
                datetime = try {
                    parseISODate(dto.datetime)
                } catch (e: Exception) {
                    throw InvalidRequestBodyParamException("datetime")
                },
                additionalItem = if (paymentMethod.method.additionalItemType != null) try {
                    val additionalItem = objectMapper.convertValue(dto.additionalItem, paymentMethod.method.additionalItemType)
                    additionalItem.validate()
                    additionalItem
                } catch (e: IllegalArgumentException) {
                    throw InvalidRequestBodyParamException("additional_item")
                } else null
            )
        } catch (e: PlatformException) {
            throw e
        } catch (e: RuntimeException) {
            throw PlatformException(
                responseCode = 400,
                message = "Payment request parsing error",
                responseErrorMessage = "error_parsing_payment_request"
            )
        }
    }
}