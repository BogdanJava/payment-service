package by.bahdan.paymentplatform.configuration

import by.bahdan.paymentplatform.model.PaymentMethod
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.ConnectionString
import java.io.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(
    value = ["by.bahdan.paymentplatform.repository"]
)
class AppConfig {

    private data class MongoConnectionSetting(
        @JsonProperty("mongoPort") val port: String,
        @JsonProperty("mongoHost") val host: String,
        @JsonProperty("mongoUsername") val username: String,
        @JsonProperty("mongoPassword") val password: String
    ) {
        fun toMongoString(db: String, authDB: String) =
            "mongodb://$username:$password@$host:$port/$db?authSource=$authDB"
    }

    @Bean
    fun mongoConnectionSettings(
        mapper: ObjectMapper,
        @Value("\${spring.data.mongodb.authentication-database}") authDB: String,
        @Value("\${spring.data.mongodb.database}") db: String
    ): MongoClientSettingsBuilderCustomizer {
        val connectionSettings = fetchConnectionSettings(mapper)

        return MongoClientSettingsBuilderCustomizer {
            it.applyConnectionString(ConnectionString(connectionSettings.toMongoString(db, authDB)))
        }
    }

    private fun fetchConnectionSettings(mapper: ObjectMapper): MongoConnectionSetting {
        val settingsFile = File("/etc/db-connection-secrets")
        return if (settingsFile.exists()) {
            mapper.readValue(settingsFile, MongoConnectionSetting::class.java)
        } else {
            MongoConnectionSetting(
                port = System.getenv("MONGO_PORT") ?: throw RuntimeException(),
                host = System.getenv("MONGO_HOST") ?: throw RuntimeException(),
                username = System.getenv("MONGO_USERNAME") ?: throw RuntimeException(),
                password = System.getenv("MONGO_PASSWORD") ?: throw RuntimeException()
            )
        }
    }

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
