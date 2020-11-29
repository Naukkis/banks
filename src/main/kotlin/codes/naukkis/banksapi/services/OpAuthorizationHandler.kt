package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContextBuilder
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpResponse
import java.security.*
import java.security.cert.CertificateException
import java.time.LocalDateTime
import javax.net.ssl.SSLContext

class OpAuthorizationHandler(private val config: Config) {
    private val httpClient: HttpClient?

    companion object {
        private const val TPP_AUTHENTICATION_URL = "https://mtls-apis.psd2-sandbox.op.fi/oauth/token"
        private const val ACCOUNT_REGISTER_REQUEST_URL = "https://mtls-apis.psd2-sandbox.op.fi/accounts-psd2/v1/authorizations"
    }

    init {
        httpClient = buildAndGetHttpClient()
    }

    @Throws(IOException::class, InterruptedException::class)
    fun fetchAuthorizationId(): String? {
        val accessToken = fetchAccessToken()
        return registerTppIntent(accessToken)
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun fetchAccessToken(): String? {
        val response = httpClient!!.send(buildAndGetAccessRequestToAccounts(), HttpResponse.BodyHandlers.ofString())
        return parseResponseBodyToMap(response)!!["access_token"]
    }

    private fun parseResponseBodyToMap(response: HttpResponse<String>): Map<String?, String?>? {
        val objectMapper = ObjectMapper()
        var responseBody: Map<String?, String?>? = null
        try {
            responseBody = objectMapper.readValue<Map<String?, String?>>(response.body(), object : TypeReference<Map<String?, String?>?>() {})
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return responseBody
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun registerTppIntent(accessToken: String?): String? {
        val registerRequest = getRegisterRequest(accessToken)
        val response = httpClient!!.send(registerRequest, HttpResponse.BodyHandlers.ofString())
        println(response.body())
        val responseBodyAsMap = parseResponseBodyToMap(response)
        // todo other fields: created, status, expires
        return responseBodyAsMap!!["authorizationId"]
    }

    private fun getRegisterRequest(accessToken: String?): HttpRequest {
        val params = java.util.Map.of(
                "expires", LocalDateTime.now().plusDays(1L).toString() + "Z",
                "transactionFrom", "2020-11-22",
                "transactionTo", "2020-11-24"
        )
        val objectMapper = ObjectMapper()
        var requestBody: String? = ""
        try {
            requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(params)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(ACCOUNT_REGISTER_REQUEST_URL))
                .setHeader("x-api-key", config.opApiKey)
                .setHeader("Authorization", "Bearer $accessToken")
                .setHeader("x-fapi-financial-id", "test")
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build()
    }

    private fun buildAndGetHttpClient(): HttpClient? {
        try {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .sslContext(buildSslContext())
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        }
        return null
    }

    private fun buildAndGetAccessRequestToAccounts(): HttpRequest {
        val params = java.util.Map.of(
                "grant_type", "client_credentials",
                "scope", "accounts",
                "client_id", config.opClientId,
                "client_secret", config.opClientSecret)
        return HttpRequest.newBuilder()
                .POST(createFormData(params))
                .uri(URI.create(TPP_AUTHENTICATION_URL))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
    }

    private fun createFormData(data: Map<String, String>): BodyPublisher {
        val builder = StringBuilder()
        for (key in data.keys) {
            if (builder.toString().length != 0) {
                builder.append("&")
            }
            builder.append(key)
            builder.append("=")
            builder.append(data[key])
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString())
    }

    @Throws(KeyStoreException::class, IOException::class, NoSuchAlgorithmException::class, CertificateException::class, UnrecoverableKeyException::class, KeyManagementException::class)
    private fun buildSslContext(): SSLContext {
        val clientStore = KeyStore.getInstance("PKCS12")
        val resource = this.javaClass.classLoader.getResourceAsStream(config.opTppCert)
        clientStore.load(resource, config.opTppCertPassword.toCharArray())
        val sslContextBuilder = SSLContextBuilder()
        sslContextBuilder.setProtocol("TLSv1.2")
        sslContextBuilder.loadKeyMaterial(clientStore, config.opTppCertPassword.toCharArray())
        sslContextBuilder.loadTrustMaterial(TrustAllStrategy.INSTANCE)
        return sslContextBuilder.build()
    }

}