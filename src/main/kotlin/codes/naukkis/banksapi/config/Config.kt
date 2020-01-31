package codes.naukkis.banksapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration()
@PropertySource("classpath:bank.properties")
class Config {
    @Value("\${op.apikey}")
    lateinit var apiKey: String
    @Value("\${op.staticauth}")
    lateinit var staticAuth: String

}