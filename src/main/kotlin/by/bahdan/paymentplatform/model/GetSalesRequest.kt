package by.bahdan.paymentplatform.model

import by.bahdan.paymentplatform.exception.InvalidDateRangeException
import by.bahdan.paymentplatform.exception.InvalidRequestBodyParamException
import by.bahdan.paymentplatform.utils.parseISODate
import java.time.format.DateTimeParseException
import java.util.Date

data class GetSalesRequestDTO(
    val startDateTime: String,
    val endDateTime: String
) {

    fun validateAndParse(): GetSalesRequest {
        val salesRequest = GetSalesRequest(
            startDateTime = try { parseISODate(startDateTime) } catch (e: DateTimeParseException) {
                throw InvalidRequestBodyParamException("startDateTime")
            },
            endDateTime = try { parseISODate(endDateTime) } catch (e: DateTimeParseException) {
                throw InvalidRequestBodyParamException("endDateTime")
            }
        )

        if (salesRequest.endDateTime.before(salesRequest.startDateTime)) {
            throw InvalidDateRangeException()
        }

        return salesRequest
    }
}

data class GetSalesRequest(
    val startDateTime: Date,
    val endDateTime: Date
)
