package by.bahdan.paymentplatform.exception

import by.bahdan.paymentplatform.model.AdditionalItem
import by.bahdan.paymentplatform.model.PaymentMethod

open class PlatformException(
    override val message: String?,
    val responseErrorMessage: String,
    val responseCode: Int
) : RuntimeException(message)

class InvalidPaymentMethodException(paymentMethodName: String) : PlatformException(
    message = "Invalid payment method: $paymentMethodName",
    responseErrorMessage = "invalid_payment_method",
    responseCode = 400
)

class InvalidRequestBodyParamException(paramName: String) : PlatformException(
    message = "Invalid parameter: $paramName",
    responseErrorMessage = "invalid_param_$paramName",
    responseCode = 400
)

class PriceModifierOutOfPriceRangeException(priceModifier: Double, paymentMethod: PaymentMethod) : PlatformException(
    message = "Price modifier $priceModifier is outside the price modifier range " +
            "{${paymentMethod.finalPriceRange.minModifier}:${paymentMethod.finalPriceRange.maxModifier}} " +
            "of the following payment method: ${paymentMethod.name}",
    responseErrorMessage = "invalid_price_modifier",
    responseCode = 400
)

class InvalidDateRangeException : PlatformException(
    message = "Invalid date range",
    responseCode = 400,
    responseErrorMessage = "invalid_date_range"
)

class AdditionalItemValidationException(additionalItem: AdditionalItem) : PlatformException(
    message = "Additional item validation error",
    responseCode = 400,
    responseErrorMessage = "invalid_additional_item_${additionalItem.name()}"
)