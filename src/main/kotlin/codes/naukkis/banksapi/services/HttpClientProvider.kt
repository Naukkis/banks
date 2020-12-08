package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import codes.naukkis.banksapi.getHttpDate
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContextBuilder
import java.io.IOException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.security.*
import java.security.cert.CertificateException
import javax.net.ssl.SSLContext

class HttpClientProvider(private val config: Config) {
    val httpClient: HttpClient
    val noRedirectHttpClient: HttpClient

    init {
        httpClient = buildAndGetHttpClient(true)
        noRedirectHttpClient = buildAndGetHttpClient(false)
    }

   private  fun buildAndGetHttpClient(redirect: Boolean): HttpClient {
        try {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .sslContext(buildSslContext())
                    .followRedirects(when {
                        redirect -> HttpClient.Redirect.ALWAYS
                        else -> HttpClient.Redirect.NEVER
                    })
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
       return HttpClient.newHttpClient()
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