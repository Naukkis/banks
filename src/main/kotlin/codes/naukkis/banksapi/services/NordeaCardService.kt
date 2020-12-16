package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.config.NordeaApiHeaders
import codes.naukkis.banksapi.model.Bank.NORDEA
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
class NordeaCardService(private val config: Config) {
    var logger: Logger = Logger.getLogger(NordeaCardService::class.java.name)
    private val httpClient = HttpClientProvider(config, NORDEA).noRedirectHttpClient

    @GetMapping("/nordea/cards/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun cards(): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/cards"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "cards requested")
        return r.body()
    }

    @GetMapping("/nordea/cards/{cardId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun account(@PathVariable cardId: String): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/cards/${cardId}"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "card $cardId requested")
        return r.body()
    }

    @GetMapping("/nordea/cards/{cardId}/transactions", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun transactions(@PathVariable cardId: String): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/cards/${cardId}/transactions"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "transactions for card $cardId requested")
        return r.body()
    }
}