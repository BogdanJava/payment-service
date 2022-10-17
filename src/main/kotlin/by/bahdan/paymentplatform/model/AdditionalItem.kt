package by.bahdan.paymentplatform.model

import by.bahdan.paymentplatform.exception.AdditionalItemValidationException
import by.bahdan.paymentplatform.utils.containsNonDigits
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.stream.Collectors

abstract class AdditionalItem {
    abstract fun validate()
    abstract fun name(): String
}

class LastFourDigits constructor(@JsonProperty("last_4") val digits: String) : AdditionalItem() {
    override fun validate() {
        if (digits.length != 4)
            throw AdditionalItemValidationException(this)
        if (digits.containsNonDigits())
            throw AdditionalItemValidationException(this)
    }

    override fun name(): String = "last_four_digits"

}

class CourierService constructor(@JsonProperty("service_name") val serviceName: String) : AdditionalItem() {
    override fun validate() {
    }

    override fun name(): String = "courier_service"
}

class ChequeInfo constructor(
    @JsonProperty("bank_name") val bankName: String,
    @JsonProperty("cheque_number") val chequeNumber: String
) : AdditionalItem() {
    override fun validate() {
    }

    override fun name(): String = "cheque_info"
}

class AccountInfo constructor(
    @JsonProperty("bank_name") val bankName: String,
    @JsonProperty("account_number") val accountNumber: String
) : AdditionalItem() {
    override fun validate() {
        if (accountNumber.containsNonDigits())
            throw AdditionalItemValidationException(this)
    }

    override fun name(): String = "account_info"
}