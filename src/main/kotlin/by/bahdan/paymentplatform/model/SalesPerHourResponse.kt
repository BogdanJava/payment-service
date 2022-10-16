package by.bahdan.paymentplatform.model


data class SalesPerHourResponse(
    val sales: List<SalesPerHour>
)

data class SalesPerHour(
    val datetime: String,
    val sales: String,
    val points: Int
)
