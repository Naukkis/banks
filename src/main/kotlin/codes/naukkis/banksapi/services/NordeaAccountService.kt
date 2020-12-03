package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.getHttpDate
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
class NordeaAccountService(private val config: Config) {
    var logger: Logger = Logger.getLogger(NordeaAccountService::class.java.name)
    private val httpClient = HttpClientProvider(config).noRedirectHttpClient

    @GetMapping("/nordea/accounts", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun accounts(): String {
        val requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/accounts"))
                .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = setRegularHeaders(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "accounts requested")
        return r.body()
    }

    @GetMapping("/nordea/accounts/{accountId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun account(@RequestParam accountId: String): String {
        val requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/accounts/${accountId}"))
                .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = setRegularHeaders(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "account $accountId requested")
        return r.body()
    }

    @GetMapping("/nordea/accounts/{accountId}/transactions", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun transactions(@RequestParam accountId: String): String {
        val requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/accounts/${accountId}/transactions"))
                .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = setRegularHeaders(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "transactions for account $accountId requested")
        return r.body()
    }


    private fun setRegularHeaders(builder: HttpRequest.Builder): HttpRequest.Builder {
        builder.setHeader("X-IBM-Client-ID", config.nordeaClientId)
                .setHeader("X-IBM-Client-Secret", config.nordeaClientSecret)
                .setHeader("X-Nordea-Originating-Date", getHttpDate())
                .setHeader("X-Nordea-Originating-Host", "api.nordeaopenbanking.com")
                .setHeader("Signature", "SKIP_SIGNATURE_VALIDATION_FOR_SANDBOX")
        //      .setHeader("Digest", "sha-256=qKEkYt43vgJXW0ibKcHuvm+GhtsOKa/yISq9xk5pVV0=")
        return builder
    }
}