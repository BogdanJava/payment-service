package by.bahdan.paymentplatform.model

import by.bahdan.paymentplatform.utils.toISODateString
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import kotlin.math.roundToInt

data class PaymentRequestDTO(
    @JsonProperty("customer_id")
    val customerId: String,

    val price: String,

    @JsonProperty("price_modifier")
    val priceModifier: Double,

    @JsonProperty("payment_method")
    val paymentMethod: String,

    val datetime: String,

    @JsonProperty("additional_item")
    val additionalItem: Map<String, Any>?
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
        get() = price * priceModifier

    val points: Int
        get() = (price * paymentMethod.pointsModifier).roundToInt()

    fun toResponseObject(): Map<String, Any> {
        return mapOf(
            "price" to finalPrice,
            "points" to points,
            "datetime" to datetime.toISODateString()
        )
    }
}




