package by.bahdan.paymentplatform.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentResponse(
    @JsonProperty("final_price") val finalPrice: String,
    val points: Int
)
