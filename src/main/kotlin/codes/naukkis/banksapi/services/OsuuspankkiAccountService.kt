package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.JwtGenerator
import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.model.Bank
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/op")
class OsuuspankkiAccountService(private val config: Config) {
    private val opUrlAccounts = "https://sandbox.apis.op-palvelut.fi/accounts/v3/accounts"
    private val headers = mapOf("Accept" to "application/json",
            "Content-Type" to "application/json",
            "x-api-key" to config.opApiKey,
            "Authorization" to config.opStaticAuth,
            "accept" to "application/json")

    private val httpClient: HttpClient = HttpClientProvider(config, Bank.OSUUSPANKKI).httpClient

    @GetMapping("/accounts")
    fun accounts(@RequestParam accountId: String): ByteArray {
        val r = get("${opUrlAccounts}${accountId}",
                headers = headers)

        return r.content
    }

    @GetMapping("/accounts/all")
    fun accountsAll(): String {
        val basicHttpRequest = basicHttpRequest(opUrlAccounts)
        val response = httpClient.send(basicHttpRequest, HttpResponse.BodyHandlers.ofString())
        println(response.statusCode())
        println(response.headers())
        println(response.body())
        return response.body()
    }

    @GetMapping("/accounts/{accountId}/transactions")
    fun transactions(@PathVariable accountId: String): ByteArray {
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