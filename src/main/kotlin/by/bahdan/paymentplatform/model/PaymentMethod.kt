package by.bahdan.paymentplatform.model

data class PaymentMethod(
    val name: String,
    val finalPriceRange: PriceRange,
    val pointsModifier: Double
) {

    val method: PaymentMethods
        get() = PaymentMethods.valueOf(name)
}

data class PriceRange(
    val minModifier: Double,
    val maxModifier: Double
) {

    fun within(priceModifier: Double): Boolean = priceModifier in minModifier..maxModifier
}

enum class PaymentMethods(val additionalItemType: Class<out AdditionalItem>?) {
    CASH(null),
    CASH_ON_DELIVERY(CourierService::class.java),
    VISA(LastFourDigits::class.java),
    MASTERCARD(LastFourDigits::class.java),
    AMEX(LastFourDigits::class.java),
    JCB(LastFourDigits::class.java),
    LINE_PAY(null),
    PAYPAY(null),
    POINTS(null),
    GRAB_PAY(null),
    BANK_TRANSFER(AccountInfo::class.java),
    CHEQUE(ChequeInfo::class.java)
}
