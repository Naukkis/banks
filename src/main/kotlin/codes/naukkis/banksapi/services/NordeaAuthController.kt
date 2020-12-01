package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.AccessTokenResponse
import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.createFormData
import codes.naukkis.banksapi.getHttpDate
import codes.naukkis.banksapi.model.AccessToken
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger


@RestController
class NordeaAuthController(private val config: Config) {
    private val authUrl = "https://api.nordeaopenbanking.com/personal/v4/authorize?"
    private val tokenExchangeUrl = "https://api.nordeaopenbanking.com/personal/v4/authorize/token"
    private val scope = "ACCOUNTS_BASIC,ACCOUNTS_BALANCES,ACCOUNTS_DETAILS,ACCOUNTS_TRANSACTIONS,PAYMENTS_MULTIPLE"
    var logger: Logger = Logger.getLogger(NordeaAuthController::class.java.name)
    private val httpClient = HttpClientProvider(config).noRedirectHttpClient
    val mapper = jacksonObjectMapper()

    fun getAccessToken(): AccessToken {
        val authToken = requestAuthToken()
        return fetchAccessToken(authToken)
    }

    fun requestAuthToken(): String {
        val params = "state=asdfgadf" +
                "&client_id=${config.nordeaClientId}" +
                "&redirect_uri=${config.nordeaRedirectUrl}" +
                "&scope=${scope}" +
                "&duration=500" +
                "&country=FI"
        val request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(authUrl + params))
                .setHeader("Content-Type", "application/json")
                .setHeader("X-IBM-Client-Id", config.nordeaClientId)
                .build()
        logger.log(Level.INFO, "begin auth $params")
        val redirectUrl = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).headers().map().get("Location")?.get(0) // get redirect url containin access token
        return parseAuthCode(redirectUrl)!!
    }

    private fun parseAuthCode(redirectUrl: String?): String? {
        if (redirectUrl != null) {
            val regex = Regex("(code=)(\\S+)(&state)")
            return regex.find(redirectUrl)?.groupValues?.get(2)
        }
        return ""
    }

    fun fetchAccessToken(authCode: String): AccessToken {
        val params = mapOf("code" to authCode, "grant_type" to "authorization_code", "redirect_uri" to config.nordeaRedirectUrl)
        val requestBuilder = HttpRequest.newBuilder()
                .POST(createFormData(params))
                .uri(URI.create(tokenExchangeUrl))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")

        val request = setRegularHeaders(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        val accessToken: AccessToken = mapper.readValue(r.body().toString())
        logger.log(Level.INFO, "access token requested: ${accessTokenResponse.access_token}")
        return accessToken;
    }

    private fun setRegularHeaders(builder: HttpRequest.Builder): HttpRequest.Builder {
        builder.setHeader("X-IBM-Client-ID", config.nordeaClientId)
                .setHeader("X-IBM-Client-Secret", config.nordeaClientSecret)
                .setHeader("X-Nordea-Originating-Date", getHttpDate())
                .setHeader("X-Nordea-Originating-Host", "api.nordeaopenbanking.com")
                .setHeader("Signature", "SKIP_SIGNATURE_VALIDATION_FOR_SANDBOX")
        return builder
    }

    companion object {
        lateinit var accessTokenResponse: AccessTokenResponse
    }

}