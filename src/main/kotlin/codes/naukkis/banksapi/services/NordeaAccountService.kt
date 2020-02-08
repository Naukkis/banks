package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.getHttpDate
import codes.naukkis.banksapi.services.NordeaAuthController.Companion.accessTokenResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger

@RestController
class NordeaAccountService(private val config: Config) {
    var logger: Logger = Logger.getLogger(NordeaAccountService::class.java.name)
    private val httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build()

    @GetMapping("/nordeaaccounts")
    fun accounts(): String {
        val requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/accounts"))
                .setHeader("Authorization", "Bearer ${accessTokenResponse.access_token}")

        val request = setRegularHeaders(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "accounts requested")
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