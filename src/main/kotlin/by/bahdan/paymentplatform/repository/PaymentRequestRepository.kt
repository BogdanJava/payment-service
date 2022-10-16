package by.bahdan.paymentplatform.repository

import by.bahdan.paymentplatform.model.PaymentRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface PaymentRequestRepository : MongoRepository<PaymentRequest, String> {

    @Query("{'datetime' : { \$gte: ?0, \$lte: ?1 } }")
    fun getPaymentsWithinRange(from: Date, to: Date): List<PaymentRequest>
}