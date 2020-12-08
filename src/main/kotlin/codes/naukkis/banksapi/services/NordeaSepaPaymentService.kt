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
    private val httpClient = HttpClientProvider(config).noRedirectHttpClient

    @GetMapping("/payments", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun payments(): String {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/payments/sepa"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        logger.log(Level.INFO, "payments requested")
        return r.body()
    }

    @PostMapping("/payments")
    fun createPayment(
        @RequestParam(name = "amount") amount: String,
        @RequestParam(name = "creditorAccountNumber") creditorAccountNumber: String,
        @RequestParam(name = "debtorAccountNumber") debtorAccountNumber: String,
        @RequestParam(name = "message") message: String,
        @RequestParam(name = "creditorName") name: String,
        @RequestParam(name = "reference") reference: String
    ) {
        val accountType = "IBAN"
        val currency = "EUR"

        val debtor = Debtor(Account(debtorAccountNumber, currency, accountType), message)
        val creditor =
            Creditor(Account(creditorAccountNumber, currency, accountType), name, message, Reference(reference, "RF"))
        val sepaPayment = SepaPayment(creditor, debtor, amount, currency)

        val objectMapper = ObjectMapper()
        val sepaPaymentJson = objectMapper.writeValueAsString(sepaPayment)

        val requestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(sepaPaymentJson))
            .uri(URI.create("https://api.nordeaopenbanking.com/personal/v4/payments/sepa"))
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")
            .setHeader("Content-Type", "application/json; charset=UTF-8")
            .setHeader("Accept", "application/json")

        val request = NordeaApiHeaders(config).setTo(requestBuilder).build()
    }
}