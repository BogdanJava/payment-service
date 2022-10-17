package by.bahdan.paymentplatform.model

data class HourlySalesData(
    val sales: List<HourlySalesDataEntry>
)

data class HourlySalesDataEntry(
    val datetime: String,
    val sales: String,
    val points: Int
)
