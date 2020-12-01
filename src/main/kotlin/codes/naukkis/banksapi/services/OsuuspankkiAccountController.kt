package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.JwtGenerator
import codes.naukkis.banksapi.config.Config
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
class OsuuspankkiAccountController(private val config: Config) {
    private val opUrlAccounts = "https://sandbox.apis.op-palvelut.fi/accounts/v3/accounts"
    private val headers = mapOf("Accept" to "application/json",
            "Content-Type" to "application/json",
            "x-api-key" to config.opApiKey,
            "Authorization" to config.opStaticAuth,
            "accept" to "application/json")

    private val httpClient: HttpClient = HttpClientProvider(config).httpClient

    @GetMapping("/authflow")
    fun authFlow(): RedirectView? {
        val jwt = JwtGenerator(config).createJwtUsingStaticParams()
        val authUrl = buildAuthorizationRequestUrl(jwt)
        return RedirectView(authUrl)
    }

    fun buildAuthorizationRequestUrl(jwt: String): String {
        return "https://sandbox.apis.op-palvelut.fi/oauth/v1/authorize?request=${jwt}" +
                "&response_type=code" +
                "&client_id=" + config.opApiKey +
                "&scope=openid%20accounts%20accounts%3Atransactions" +
                "&redirect_uri=" + config.opRedirectUrlEncoded +
                "&state=1122-234"
    }

    @GetMapping("/accounts")
    fun accounts(@RequestParam accountId: String): ByteArray {
        val r = get("${opUrlAccounts}${accountId}",
                headers = headers)

        return r.content
    }

    @GetMapping("/accountsall")
    fun accountsAll(): String {
        val testaddress = "http://0.0.0.0:80/get"
        val basicHttpRequest = basicHttpRequest(opUrlAccounts)
        val response = httpClient.send(basicHttpRequest, HttpResponse.BodyHandlers.ofString())
        println(response.statusCode())
        println(response.headers())
        println(response.body())
        return response.body()
    }

    @GetMapping("/accounts/transactions")
    fun transactions(@RequestParam accountId: String): ByteArray {
        val r = get("${opUrlAccounts}/${accountId}/transactions",
                headers = headers)

        return r.content
    }

    private fun basicHttpRequest(url: String): HttpRequest {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Api-Key", config.opApiKey)
                .setHeader("Authorization", config.opStaticAuth)
                .setHeader("Accept", "application/json")
                .build()
    }

    private fun parseResponseBodyToMap(response: HttpResponse<String>): Map<String?, String?>? {
        val objectMapper = ObjectMapper()
        var responseBody: Map<String?, String?>? = null
        try {
            responseBody = objectMapper.readValue<Map<String?, String?>>(response.body(), object : TypeReference<Map<String?, String?>?>() {})
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return responseBody
    }

}