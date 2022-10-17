package by.bahdan.paymentplatform.controller

import by.bahdan.paymentplatform.model.*
import by.bahdan.paymentplatform.service.PaymentService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController(
    val paymentService: PaymentService
) {

    // Setting as "POST" due to restTemplate limitations (cannot send body when the method is "GET")
    @PostMapping("/sales")
    fun salesInfo(@RequestBody getSalesRequestDTO: GetSalesRequestDTO): ResponseEntity<HourlySalesData> {
        val salesResponse = paymentService.getHourlySalesData(getSalesRequestDTO)

        return ResponseEntity.ok(salesResponse)
    }

    @PostMapping("/payment")
    fun payment(@RequestBody paymentRequestDTO: RestPaymentRequestDTO): ResponseEntity<PaymentResponse> {
        val paymentResponse = paymentService.makePayment(paymentRequestDTO)

        return ResponseEntity.ok(paymentResponse)
    }
}

@Controller
class GraphQLPaymentController(
    val paymentService: PaymentService
) {

    @QueryMapping
    fun hourlySalesData(@Argument salesInfoRequest: GetSalesRequestDTO): HourlySalesData {
        return paymentService.getHourlySalesData(salesInfoRequest)
    }

    @MutationMapping
    fun makePayment(@Argument payment: GraphqlPaymentRequestDTO): PaymentResponse {
        return paymentService.makePayment(payment)
    }

}