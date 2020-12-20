package codes.naukkis.banksapi.services.spankki

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.model.*
import codes.naukkis.banksapi.services.HttpClientProvider
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime
import java.util.logging.Level
import java.util.logging.Logger


@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/spankki")
class SpankkiAccountService(private val config: Config) {
    var logger: Logger = Logger.getLogger(SpankkiAccountService::class.java.name)
    private val httpClient = HttpClientProvider(config, Bank.S_PANKKI).httpClient
    val consentRequestUrl = "https://s-pankki-api-sandbox.crosskey.io/open-banking/v3.1/aisp/account-access-consents"

    @GetMapping("/accounts-request", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createAccountRequest(): String? {
        val accessToken = SpankkiAuthController(config).getAccessToken()
        val dataField = SpankkiConsentRequestDataField(LocalDateTime.now().plusDays(1L).toString() + "Z",
            arrayOf("ReadAccountsBasic", "ReadAccountsDetail", "ReadBalances"),
            LocalDateTime.now().minusDays(5).toString() + "Z",
            LocalDateTime.now().toString() + "Z")

        val requestBody = SpankkiConsentRequestBody(dataField, Risk())
        val objectMapper = ObjectMapper()
        val requestBodyJson = objectMapper.writeValueAsString(requestBody)

        val request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
            .uri(URI.create(consentRequestUrl))
            .setHeader("Authorization", "Bearer ${accessToken.access_token}")
            .setHeader("Content-Type", "application/json; charset=UTF-8")
            .setHeader("x-fapi-financial-id", "s-pankki")
            .setHeader("X-API-Key", config.spankkiApiKey)
            .build()
        logger.log(Level.INFO, "begin auth")
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, response.body())
        val consentRequestResponse = parseResponseBodyToMap(response)
       return consentRequestResponse.data.toString()
    }

    @GetMapping("/accounts/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun accounts(): String {
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://s-pankki-api-sandbox.crosskey.io/open-banking/v3.1/aisp/accounts"))
            .setHeader("Authorization", "Bearer ${SpankkiAuthController(config).getAccessToken()}")
            .setHeader("Content-Type", "application/json; charset=UTF-8")
            .setHeader("x-fapi-financial-id", "s-pankki")
            .setHeader("X-API-Key", config.spankkiApiKey)
            .build()


        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "accounts requested")
        return r.body()
    }

    private fun parseResponseBodyToMap(response: HttpResponse<String>): ConsentRequestResponse {
        val objectMapper = ObjectMapper()
        try {
            return objectMapper.readValue(response.body())
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return ConsentRequestResponse(ConsentData("fial"))
    }
}