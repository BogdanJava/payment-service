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

    @GetMapping("/sales")
    fun salesInfo(@RequestBody getSalesRequestDTO: GetSalesRequestDTO): ResponseEntity<SalesPerHourResponse> {
        val salesResponse = paymentService.getHourlySalesData(getSalesRequestDTO)

        return ResponseEntity.ok(salesResponse)
    }

    @GetMapping("/payments")
    fun getPayments(): ResponseEntity<List<Any>> {
        val payments = paymentService.getAll()

        return ResponseEntity.ok(payments.map { it.toResponseObject() })
    }

    @PostMapping("/payment")
    fun payment(@RequestBody paymentRequestDTO: PaymentRequestDTO): ResponseEntity<PaymentResponse> {
        val paymentResponse = paymentService.makePayment(paymentRequestDTO)

        return ResponseEntity.ok(paymentResponse)
    }
}