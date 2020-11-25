package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.JwtGenerator
import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.createFormData
import khttp.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@RestController
class OsuuspankkiPSD2Controller(private val config: Config) {
    private val opPSD2url = "https://mtls-apis.psd2-sandbox.op.fi/accounts-psd2"
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

    @GetMapping("/tppregistertoaccounts")
    fun tppRegisterToAccounts() {
        val headers = mapOf("Accept" to "application/json",
                "Content-Type" to "application/x-www-form-urlencoded",
                "x-api-key" to config.opApiKey,
                "Authorization" to "WJfbdQoSG6jPNsaoTm2k")

        val authorizationsUrl = "https://mtls-apis.psd2-sandbox.op.fi/accounts-psd2/v1/authorizations"

        val params = mapOf("expires" to "2020-11-24T11:24:13.889Z")

        val request = HttpRequest.newBuilder()
                .POST(createFormData(params))
                .uri(URI.create(authorizationsUrl))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("x-api-key", config.opApiKey)
                .setHeader("Authorization", "WJfbdQoSG6jPNsaoTm2k")
                .setHeader("x-fapi-financial-id", "test")
                .setHeader("Accept", "application/json")
                .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.body())

    }

    @GetMapping("/jwttoken")
    fun jwt(authorizationToken: String): String {
        val jwtstr = JwtGenerator(config).createJwt(authorizationToken)
        val opurl = "https://authorize.psd2-sandbox.op.fi/oauth/authorize"
        val httpbinurl = "http://httpbin.org/get"
        val url = "${opurl}?request=${jwtstr}" +
                "&response_type=code" +
                "&client_id=" + config.opClientId +
                "&scope=openid%20accounts" +
                "&redirect_uri=https%3A%2F%2Flocalhost%3A8443%2Fpaluu" +
                "&state=1122-234"
        return url
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
    fun redirectWithUsingRedirectView(): RedirectView? {
        val opJava = OpJava(config)
        val accessToken = opJava.accessToken
        val authorizationToken = opJava.registerTppIntent(accessToken)
        val jwt = jwt(authorizationToken)
        println(jwt)
        return RedirectView(jwt)
    }

    @GetMapping("/bearer")
    fun getBearer(): String? {
        val params = mapOf("grant_type" to "client_credentials",
                "scope" to "accounts",
                "client_id" to config.opClientId,
                "client_secret" to config.opClientSecret)
        val request = HttpRequest.newBuilder()
                .POST(createFormData(params))
                .uri(URI.create("https://mtls-apis.psd2-sandbox.op.fi/oauth/token"))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

        val r = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        return r.body()
    }

}