package by.bahdan.paymentplatform.controller

import by.bahdan.paymentplatform.model.*
import by.bahdan.paymentplatform.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController(
    val paymentService: PaymentService
) {

    // Setting as "POST" due to restTemplate limitations (cannot send body when the method is "GET")
    @PostMapping("/sales")
    fun salesInfo(@RequestBody getSalesRequestDTO: GetSalesRequestDTO): ResponseEntity<SalesPerHourResponse> {
        val salesResponse = paymentService.getHourlySalesData(getSalesRequestDTO)

        return ResponseEntity.ok(salesResponse)
    }

    @PostMapping("/payment")
    fun payment(@RequestBody paymentRequestDTO: PaymentRequestDTO): ResponseEntity<PaymentResponse> {
        val paymentResponse = paymentService.makePayment(paymentRequestDTO)

        return ResponseEntity.ok(paymentResponse)
    }
}