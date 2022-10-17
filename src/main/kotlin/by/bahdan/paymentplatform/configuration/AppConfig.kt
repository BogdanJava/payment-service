package by.bahdan.paymentplatform.configuration

import by.bahdan.paymentplatform.model.PaymentMethod
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(
    value = ["by.bahdan.paymentplatform.repository"]
)
class AppConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
    }

    @Bean
    fun paymentMethods(mapper: ObjectMapper): List<PaymentMethod> {
        val resource = this::class.java.classLoader
            .getResourceAsStream("payment_methods.json")
        return mapper.readValue(resource, object : TypeReference<List<PaymentMethod>>() {})
    }
}
