package by.bahdan.paymentplatform.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.roundToInt

abstract class PaymentRequestDTO<T : Any?> {
    abstract val customerId: String
    abstract val price: String
    abstract val priceModifier: Double
    abstract val paymentMethod: String
    abstract val datetime: String
    abstract val additionalItem: T
}

data class RestPaymentRequestDTO(
    @JsonProperty("customer_id")
    override val customerId: String,

    override val price: String,

    @JsonProperty("price_modifier")
    override val priceModifier: Double,

    @JsonProperty("payment_method")
    override val paymentMethod: String,

    override val datetime: String,

    @JsonProperty("additional_item")
    override val additionalItem: Any?
) : PaymentRequestDTO<Any?>()

data class GraphqlPaymentRequestDTO(
    override val customerId: String,
    override val price: String,
    override val priceModifier: Double,
    override val paymentMethod: String,
    override val datetime: String,
    override val additionalItem: GraphqlAdditionalItemDTO?
) : PaymentRequestDTO<GraphqlAdditionalItemDTO?>()

data class GraphqlAdditionalItemDTO(
    @JsonProperty("last_4") val digits: String?,
    @JsonProperty("check_number") val checkNumber: String?,
    @JsonProperty("bank_name") val bankName: String?,
    @JsonProperty("service_name") val serviceName: String?,
    @JsonProperty("account_number") val accountNumber: String?
)

@Document
data class PaymentRequest(
    val customerId: Long,
    val price: Double,
    val priceModifier: Double,
    val paymentMethod: PaymentMethod,
    val datetime: Date,
    val additionalItem: AdditionalItem?,
) {
    @Id var id: String = ""

    val finalPrice: Double
        get() = BigDecimal.valueOf(price * priceModifier)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

    val points: Int
        get() = (price * paymentMethod.pointsModifier).roundToInt()

}




