package by.bahdan.paymentplatform.model

import com.fasterxml.jackson.annotation.JsonProperty

open class AdditionalItem

class LastFourDigits constructor(@JsonProperty("last_4") val digits: String) : AdditionalItem()
class CourierService constructor(@JsonProperty("service_name") val serviceName: String) : AdditionalItem()
class ChequeInfo constructor(
    @JsonProperty("bank_name") val bankName: String,
    @JsonProperty("cheque_number") val chequeNumber: String
) : AdditionalItem()

class AccountInfo constructor(
    @JsonProperty("bank_name") val bankName: String,
    @JsonProperty("account_number") val accountNumber: String
) : AdditionalItem()