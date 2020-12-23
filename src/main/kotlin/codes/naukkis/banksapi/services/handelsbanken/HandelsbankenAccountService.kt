package codes.naukkis.banksapi.services.handelsbanken

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.model.Bank
import codes.naukkis.banksapi.services.HttpClientProvider
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger


@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/handelsbanken")
class HandelsbankenAccountService(private val config: Config) {
    var logger: Logger = Logger.getLogger(HandelsbankenAccountService::class.java.name)
    private val httpClient = HttpClientProvider(config, Bank.NORDEA).noRedirectHttpClient

    @GetMapping("/accounts/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun accounts(): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://sandbox.handelsbanken.com/openbanking/psd2/v2/accounts"))
            .setHeader("X-IBM-Client-Id", config.handelsbankenApiKey)
            .setHeader("Authorization", "MV9QUk9GSUxFLUZJX1BSSVZBVEUx")
            .setHeader("PSU-IP-Address", "127.0.0.1")
            .setHeader("TPP-Transaction-ID", "127.0.0.1")
            .setHeader("TPP-Request-ID", "127.0.0.1")
            .setHeader("accept", "application/json")

        val response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "handelsbanken accounts requested")
        return response.body()
    }

    @GetMapping("/accounts/{accountId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun account(@PathVariable accountId: String): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://sandbox.handelsbanken.com/openbanking/psd2/v2/accounts/${accountId}?withBalance=true"))
            .setHeader("X-IBM-Client-Id", config.handelsbankenApiKey)
            .setHeader("Authorization", "MV9QUk9GSUxFLUZJX1BSSVZBVEUx")
            .setHeader("PSU-IP-Address", "127.0.0.1")
            .setHeader("TPP-Transaction-ID", "127.0.0.1")
            .setHeader("TPP-Request-ID", "127.0.0.1")
            .setHeader("accept", "application/json")

        val response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "handelsbanken account $accountId requested")
        return response.body()
    }

    @GetMapping("/accounts/{accountId}/transactions", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun transactions(@PathVariable accountId: String): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://sandbox.handelsbanken.com/openbanking/psd2/v2/accounts/${accountId}/transactions"))
            .setHeader("X-IBM-Client-Id", config.handelsbankenApiKey)
            .setHeader("Authorization", "MV9QUk9GSUxFLUZJX1BSSVZBVEUx")
            .setHeader("PSU-IP-Address", "127.0.0.1")
            .setHeader("TPP-Transaction-ID", "127.0.0.1")
            .setHeader("TPP-Request-ID", "127.0.0.1")
            .setHeader("accept", "application/json")

        val response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "handelsbanken account $accountId transactions requested")
        return response.body()
    }

    @GetMapping("/consent", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun consent(): RedirectView? {
        val redirectUrl = HandelsbankenAuthService(config).getAccessToAccounts()
        println(redirectUrl)
        return RedirectView(redirectUrl)
    }

    @GetMapping("/oauth")
    fun callback(
        @RequestParam(required = false) state: String,
        @RequestParam(required = false) code: String,
        @RequestParam(required = false) error: String,
        @RequestParam(required = false) error_description: String
    ) {
        println("")
        println("")
        println("")
        println("$state $code $error $error_description")
    }

}