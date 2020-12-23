package codes.naukkis.banksapi.services.handelsbanken

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.model.Bank
import codes.naukkis.banksapi.services.HttpClientProvider
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger

@Component
class HandelsbankenAuthService(private val config: Config) {
    var logger: Logger = Logger.getLogger(HandelsbankenAuthService::class.java.name)
    private val authUrl = "https://sandbox.handelsbanken.com/openbanking/oauth2/token/1.0"
    private val httpClient = HttpClientProvider(config, Bank.NORDEA).noRedirectHttpClient


    fun getAccessToAccounts(): String {
        val authorizationToken = getAuthorizationTokenForAccounts()
        if (authorizationToken != null) {
            val consentId = initiateConsent(authorizationToken)
            if (consentId != null) {
                return getAuthorizationRedirectUrl(consentId)
            }
        }
        return "fail"
    }

    private fun getAuthorizationTokenForAccounts(): String? {
        val params = java.util.Map.of(
            "grant_type", "client_credentials",
            "scope", "AIS",
            "client_id", config.handelsbankenApiKey)

        val request = HttpRequest.newBuilder()
            .POST(createFormData(params))
            .uri(URI.create(authUrl))
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setHeader("Accept", "application/json")
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        return parseResponseBodyToMap(response)!!["access_token"]
    }

    private fun initiateConsent(authorizationToken: String): String? {
        val params = "{\"access\":\"ALL_ACCOUNTS\"}"
        val consentsUrl = "https://sandbox.handelsbanken.com/openbanking/psd2/v1/consents"

        val request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(params))
            .uri(URI.create(consentsUrl))
            .setHeader("Content-Type", "application/json")
            .setHeader("Accept", "application/json")
            .setHeader("Country", "FI")
            .setHeader("X-IBM-Client-Id", config.handelsbankenApiKey)
            .setHeader("PSU-IP-Address", "127.0.0.1")
            .setHeader("TPP-Request-ID", "1234")
            .setHeader("TPP-Transaction-ID", "1234")
            .setHeader("Authorization", "Bearer ${config.handelsbankenStaticConsentId}")
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, response.body())
        return parseResponseBodyToMap(response)!!["consentId"]
    }

    private fun getAuthorizationRedirectUrl(consentId: String): String {
        return "https://sandbox.handelsbanken.com/openbanking/redirect/oauth2/authorize/1.0?" +
                "response_type=code" +
                "&scope=AIS%3A${consentId}" +
                "&cliend_id=${config.handelsbankenApiKey}" +
                "state=asd123" +
                "redirect_uri=http%3A%2F%2Flocalhost%2F8080%2Foauth%2Fhandelsbanken"
    }

    private fun parseResponseBodyToMap(response: HttpResponse<String>): Map<String?, String?>? {
        val objectMapper = ObjectMapper()
        var responseBody: Map<String?, String?>? = null
        try {
            responseBody = objectMapper.readValue<Map<String?, String?>>(response.body(),
                object : TypeReference<Map<String?, String?>?>() {})
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return responseBody
    }

    fun createFormData(data: Map<String, String>): HttpRequest.BodyPublisher {
        val builder = StringBuilder()
        for (key in data.keys) {
            if (builder.toString().length != 0) {
                builder.append("&")
            }
            builder.append(key)
            builder.append("=")
            builder.append(data[key])
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString())
    }
}