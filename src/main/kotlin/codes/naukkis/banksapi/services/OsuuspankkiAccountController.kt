package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import khttp.get
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
class OsuuspankkiAccountController(private val config: Config) {
    private val opUrlAccounts = "https://sandbox.apis.op-palvelut.fi/accounts"
    private val headers = mapOf("Accept" to "application/json",
            "Content-Type" to "application/json",
            "x-api-key" to config.opApiKey,
            "Authorization" to config.opStaticAuth)

    @GetMapping("accounts")
    fun accounts(@RequestParam accountId: String): ByteArray {
        val r = get("${opUrlAccounts}/v3/accounts/${accountId}",
                headers = headers)

        return r.content
    }

    @GetMapping("/accountsall")
    fun accountsAll(): ByteArray {
        val r = get("${opUrlAccounts}/v3/accounts",
                headers = headers)

        return r.content
    }

    @GetMapping("/accounts/transactions")
    fun transactions(@RequestParam accountId: String): ByteArray {
        val r = get("${opUrlAccounts}/v3/accounts/${accountId}/transactions",
                headers = headers)

        return r.content
    }

}