package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.config.NordeaApiHeaders
import codes.naukkis.banksapi.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level
import java.util.logging.Logger

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/nordea")
class NordeaSepaPaymentService(private val config: Config) {
    var logger: Logger = Logger.getLogger(NordeaSepaPaymentService::class.java.name)
    private val httpClient = HttpClientProvider(config, Bank.OSUUSPANKKI).noRedirectHttpClient

    @GetMapping("/payments", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun payments(): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/payments/sepa"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "payments requested")
        logger.log(Level.INFO, r.body())
        return r.body()
    }

    @GetMapping("/payments/{paymentId}")
    fun payment(@PathVariable paymentId: String): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/payments/sepa/${paymentId}"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "payments requested")
        return r.body()
    }

    @PostMapping("/payments")
    fun createPayment(@RequestBody paymentRequest: PaymentRequest): String {
        val accountType = "IBAN"
        val currency = "EUR"

        val debtorAccountNumber = paymentRequest.debtorAccountNumber.filter { !it.isWhitespace() }
        val debtor = Debtor(Account(accountType, currency, debtorAccountNumber), paymentRequest.message)

        val creditorAccountNumber = paymentRequest.recipientAccount.filter { !it.isWhitespace() }
        val creditor = Creditor(Account(accountType, currency, creditorAccountNumber),
            paymentRequest.recipientName,
            paymentRequest.message,
            Reference("RF11223344", "RF"))
        val sepaPayment = SepaPayment(creditor, debtor, paymentRequest.amount, currency, accountType)

        val objectMapper = ObjectMapper()
        val sepaPaymentJson = objectMapper.writeValueAsString(sepaPayment)

        val postPaymentUrl = "https://api.nordeaopenbanking.com/personal/v4/payments/sepa"

        val requestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(sepaPaymentJson))
            .uri(URI.create(postPaymentUrl))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")
            .setHeader("Content-Type", "application/json; charset=UTF-8")
            .setHeader("Accept", "application/json")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()
        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, r.body())
        return r.body();
    }

    @PutMapping("/payments")
    fun confirmPayments(@RequestParam(name = "payments") payments: String) {
        val requestBuilder = HttpRequest.newBuilder()
            .PUT(HttpRequest.BodyPublishers.ofString(payments))
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/payments/sepa/confirm"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")
            .setHeader("Content-Type", "application/json; charset=UTF-8")
            .setHeader("Accept", "application/json")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()
        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, r.body())
    }
}