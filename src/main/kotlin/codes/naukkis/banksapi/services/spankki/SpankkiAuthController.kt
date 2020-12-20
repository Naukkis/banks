package codes.naukkis.banksapi.services.spankki

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.model.AccessToken
import codes.naukkis.banksapi.model.Bank
import codes.naukkis.banksapi.model.SpankkiAccessToken
import codes.naukkis.banksapi.services.HttpClientProvider
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger

@Component
@PropertySource("classpath:spankki.properties")
class SpankkiAuthController(private val config: Config) {
    var logger: Logger = Logger.getLogger(SpankkiAuthController::class.java.name)
    val mapper = jacksonObjectMapper()
    private val httpClient = HttpClientProvider(config, Bank.S_PANKKI).noRedirectHttpClient
    private val authUrl = "https://s-pankki-api-sandbox.crosskey.io/oidc/v1.0/token?"
    private val scope = "accounts"

    fun getAccessToken(): SpankkiAccessToken {
        return requestAuthToken()
    }

    fun requestAuthToken(): SpankkiAccessToken {
        val params = "client_id=${config.spankkiClientId}" +
                "&grant_type=client_credentials" +
                "&scope=${scope}"

        val request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .uri(URI.create(authUrl + params))
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        logger.log(Level.INFO, "begin auth $params")
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, response.body())
        val accessToken: SpankkiAccessToken = mapper.readValue(response.body().toString())
        return accessToken
    }

}