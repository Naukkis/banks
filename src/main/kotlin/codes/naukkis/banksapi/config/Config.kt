package codes.naukkis.banksapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration()
@PropertySource("classpath:bank.properties")
class Config {
    @Value("\${op.apikey}")
    lateinit var opApiKey: String
    @Value("\${op.staticauth}")
    lateinit var opStaticAuth: String
    @Value("\${nordea.clientId}")
    lateinit var nordeaClientId: String
    @Value("\${nordea.clientSecret}")
    lateinit var nordeaClientSecret: String
    @Value("\${nordea.redirectUrl}")
    lateinit var nordeaRedirectUrl: String
}