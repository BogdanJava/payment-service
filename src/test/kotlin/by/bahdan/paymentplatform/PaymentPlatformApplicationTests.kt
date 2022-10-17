package by.bahdan.paymentplatform

import by.bahdan.paymentplatform.exception.ExceptionResponseBody
import by.bahdan.paymentplatform.model.GetSalesRequestDTO
import by.bahdan.paymentplatform.model.PaymentResponse
import by.bahdan.paymentplatform.model.SalesPerHour
import by.bahdan.paymentplatform.model.SalesPerHourResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.support.GenericApplicationContext
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.stream.Stream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentPlatformApplicationTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var context: GenericApplicationContext

    @Autowired
    private lateinit var mapper: ObjectMapper

    private val baseUrl: String
        get() = "http://localhost:$port"

    @Test
    fun `context and port initialized`() {
        assertTrue(::context.isInitialized, "App context not initialized")
        assertTrue(port != 0, "Web app port not injected")
    }

    @BeforeEach
    fun resetMongoData(@Autowired mongoTemplate: MongoTemplate) {
        mongoTemplate.findAllAndRemove<Any>(Query(), "paymentRequest")
    }

    @ParameterizedTest
    @MethodSource("provideMakePaymentInput")
    fun `should save payment`(requestBody: Map<String, Any>, paymentResponse: PaymentResponse) {
        val request = HttpEntity(requestBody, HttpHeaders().also { it.set("Content-Type", "application/json") })
        val response = restTemplate.postForEntity("$baseUrl/payment", request, PaymentResponse::class.java)

        assertTrue(response.statusCode == HttpStatus.OK)
        assertThat(response.body!!.finalPrice, IsEqual(paymentResponse.finalPrice))
        assertThat(response.body!!.points, IsEqual(paymentResponse.points))
    }

    @Test
    fun `should return sales data`() {
        // create payment data
        val data = provideMakePaymentInput()
        data.forEach {
            val request = HttpEntity(it.get()[0], HttpHeaders().also { it.set("Content-Type", "application/json") })
            val response = restTemplate.postForEntity("$baseUrl/payment", request, PaymentResponse::class.java)
            assertTrue(response.statusCode == HttpStatus.OK)
        }

        val requestBody = mapper.writeValueAsString(GetSalesRequestDTO("2022-09-01T00:00:00Z", "2022-09-01T05:13:00Z"))
        val request = HttpEntity(requestBody, HttpHeaders().also { it.set("Content-Type", "application/json") })
        val response = restTemplate.exchange("$baseUrl/sales", HttpMethod.POST, request, SalesPerHourResponse::class.java)

        assertThat(response.statusCode, IsEqual(HttpStatus.OK))
        assertThat(response.body!!.sales.size, IsEqual(4))
        assertThat(response.body!!, IsEqual(expectedSalesData))
    }

    @Test
    fun `should return empty sales data`() {
        // create payment data
        val data = provideMakePaymentInput()
        data.forEach {
            val request = HttpEntity(it.get()[0], HttpHeaders().also { it.set("Content-Type", "application/json") })
            val response = restTemplate.postForEntity("$baseUrl/payment", request, PaymentResponse::class.java)
            assertTrue(response.statusCode == HttpStatus.OK)
        }

        val requestBody = mapper.writeValueAsString(GetSalesRequestDTO("2022-09-02T00:00:00Z", "2022-09-04T05:13:00Z"))
        val request = HttpEntity(requestBody, HttpHeaders().also { it.set("Content-Type", "application/json") })
        val response = restTemplate.exchange("$baseUrl/sales", HttpMethod.POST, request, SalesPerHourResponse::class.java)

        assertThat(response.statusCode, IsEqual(HttpStatus.OK))
        assertThat(response.body!!.sales.size, IsEqual(0))
    }

    @Test
    fun `should throw when incorrect additional item sent`() {
        val requestBody = mapOf(
            "customer_id" to "2",
            "price" to "1430.00",
            "price_modifier" to 0.98,
            "payment_method" to "VISA",
            "datetime" to "2022-09-01T01:34:00Z",
            "additional_item" to mapOf("invalid_param" to "qwerty")
        )
        val request = HttpEntity(requestBody, HttpHeaders().also { it.set(HttpHeaders.CONTENT_TYPE, "application/json") })
        val response = restTemplate.postForEntity("$baseUrl/payment", request, ExceptionResponseBody::class.java)

        assertThat(response.statusCode, IsEqual(HttpStatus.BAD_REQUEST))
        assertThat(response.body!!.errorMessage, IsEqual("invalid_param_additional_item"))
    }

    @Test
    fun `should throw when additional item is invalid`() {
        val requestBody = mapOf(
            "customer_id" to "2",
            "price" to "1430.00",
            "price_modifier" to 0.98,
            "payment_method" to "VISA",
            "datetime" to "2022-09-01T01:34:00Z",
            "additional_item" to mapOf("last_4" to "q123") // contains non digits
        )
        val request = HttpEntity(requestBody, HttpHeaders().also { it.set(HttpHeaders.CONTENT_TYPE, "application/json") })
        val response = restTemplate.postForEntity("$baseUrl/payment", request, ExceptionResponseBody::class.java)

        assertThat(response.statusCode, IsEqual(HttpStatus.BAD_REQUEST))
        assertThat(response.body!!.errorMessage, IsEqual("invalid_additional_item_last_four_digits"))
    }

    @Test
    fun `should throw when price modifier is out of range`() {
        val requestBody = mapOf(
            "customer_id" to "2",
            "price" to "1430.00",
            "price_modifier" to 1.01,
            "payment_method" to "VISA",
            "datetime" to "2022-09-01T01:34:00Z",
            "additional_item" to mapOf("last_4" to "1456")
        )
        val request = HttpEntity(requestBody, HttpHeaders().also { it.set(HttpHeaders.CONTENT_TYPE, "application/json") })
        val response = restTemplate.postForEntity("$baseUrl/payment", request, ExceptionResponseBody::class.java)

        assertThat(response.statusCode, IsEqual(HttpStatus.BAD_REQUEST))
        assertThat(response.body!!.errorMessage, IsEqual("invalid_price_modifier"))
    }

    companion object {

        val expectedSalesData = SalesPerHourResponse(
            sales = listOf(
                SalesPerHour(datetime="2022-09-01T00:00:00Z", sales="97.0", points=5),
                SalesPerHour(datetime="2022-09-01T01:00:00Z", sales="4631.4", points=43),
                SalesPerHour(datetime="2022-09-01T04:00:00Z", sales="5007.8", points=256),
                SalesPerHour(datetime="2022-09-01T05:00:00Z", sales="2140.0", points=21)
            )
        )
        @JvmStatic
        fun provideMakePaymentInput(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    mapOf("customer_id" to "2", "price" to "1430.00", "price_modifier" to 0.98, "payment_method" to "VISA", "datetime" to "2022-09-01T01:34:00Z", "additional_item" to mapOf("last_4" to "1235")),
                    PaymentResponse("1401.4", 43)
                ),
                Arguments.of(
                    mapOf("customer_id" to "1", "price" to "100.00", "price_modifier" to 0.97, "payment_method" to "CASH", "datetime" to "2022-09-01T00:00:00Z"),
                    PaymentResponse("97.0", 5)
                ),
                Arguments.of(
                    mapOf("customer_id" to "3", "price" to "3230.00", "price_modifier" to 1, "payment_method" to "BANK_TRANSFER", "datetime" to "2022-09-01T01:14:00Z", "additional_item" to mapOf("bank_name" to "MITSUI SUMITOMO", "account_number" to "4826730237")),
                    PaymentResponse("3230.0", 0)
                ),
                Arguments.of(
                    mapOf("customer_id" to "4", "price" to "5110.00", "price_modifier" to 0.98, "payment_method" to "JCB", "datetime" to "2022-09-01T04:11:00Z", "additional_item" to mapOf("last_4" to "2836")),
                    PaymentResponse("5007.8", 256)
                ),
                Arguments.of(
                    mapOf("customer_id" to "5", "price" to "2140.00", "price_modifier" to 1, "payment_method" to "GRAB_PAY", "datetime" to "2022-09-01T05:13:00Z", "additional_item" to mapOf("last_4" to "2836")),
                    PaymentResponse("2140.0", 21)
                )
            )
        }
    }

}
