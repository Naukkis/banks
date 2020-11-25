package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import khttp.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class OsuuspankkiAccountController(private val config: Config) {
    private val opUrlAccounts = "https://sandbox.apis.op-palvelut.fi/accounts"
    private val headers = mapOf("Accept" to "application/json",
            "Content-Type" to "application/json",
            "x-api-key" to config.opApiKey,
            "Authorization" to config.opStaticAuth)

    private val opPSD2url = "https://mtls-apis.psd2-sandbox.op.fi/accounts-psd2"

    @GetMapping("/accounts")
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

    @GetMapping("/accounts-psd2")
    fun accountsPsd2(@RequestParam accountId: String): ByteArray {
        val r = get("${opPSD2url}/v1/accounts/${accountId}",
                headers = headers)

        return r.content
    }

    @GetMapping("/accounts-psd2all")
    fun accountsPsd2All(): ByteArray {
        val r = get("${opPSD2url}/v1/accounts/",
                headers = headers)

        return r.content
    }
}