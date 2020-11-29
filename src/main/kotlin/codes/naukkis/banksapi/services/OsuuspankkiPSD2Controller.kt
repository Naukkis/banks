package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.JwtGenerator
import codes.naukkis.banksapi.config.Config
import khttp.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.net.http.HttpClient

@RestController
class OsuuspankkiPSD2Controller(private val config: Config) {
    private val opPSD2url = "https://mtls-apis.psd2-sandbox.op.fi/accounts-psd2"
    private val requestAuthorizationUrl = "https://authorize.psd2-sandbox.op.fi/oauth/authorize"
    private val headersStaticAuth = mapOf("Accept" to "application/json",
            "Content-Type" to "application/json",
            "x-api-key" to config.opApiKey,
            "Authorization" to config.opStaticAuth)

    private val httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build()

    @GetMapping("/accounts-psd2")
    fun accountsPsd2(@RequestParam accountId: String): ByteArray {
        val r = get("${opPSD2url}/v1/accounts/${accountId}",
                headers = headersStaticAuth)

        return r.content
    }

    @GetMapping("/accounts-psd2all")
    fun accountsPsd2All(): ByteArray {
        val r = get("${opPSD2url}/v1/accounts/",
                headers = headersStaticAuth)

        return r.content
    }

    @GetMapping("/paluu")
    fun paluu(@RequestParam(required = false) state: String, code: String, error: String): String {
        println("")
        println("")
        println("")
        println("$state $code $error")
        return "$state $code $error"
    }

    @GetMapping("/opauth")
    fun startAuthorizationFlow(): RedirectView? {
        val authorizationToken = OpAuthorizationHandler(config).authorizationId
        val jwt = JwtGenerator(config).createJwt(authorizationToken)
        val url = buildAuthorizationRequestUrl(jwt)

        println(jwt)
        return RedirectView(url)
    }

    fun buildAuthorizationRequestUrl(jwt: String): String {
        return "$requestAuthorizationUrl?request=${jwt}" +
                "&response_type=code" +
                "&client_id=" + config.opClientId +
                "&scope=openid%20accounts" +
                "&redirect_uri=" + config.opRedirectUrlEncoded +
                "&state=1122-234"
    }

}