package codes.naukkis.banksapi.services

import codes.naukkis.banksapi.config.Config
import org.apache.commons.codec.digest.DigestUtils
import org.apache.logging.log4j.util.Strings.isNotEmpty
import org.tomitribe.auth.signatures.Signature
import org.tomitribe.auth.signatures.Signer
import java.net.URI
import java.security.Key
import java.security.KeyStore
import java.util.*


class NordeaSignatureGenerator(val config: Config) {
    private val keyId = config.nordeaClientId
    private val GET_HEADERS = listOf("(request-target)", "x-nordea-originating-host", "x-nordea-originating-date")
    private val INSERT_HEADERS =
        listOf("(request-target)", "x-nordea-originating-host", "x-nordea-originating-date", "content-type", "digest")
    private val key = loadKeyStore()

   private fun loadKeyStore(): Key {
        val keyStore = KeyStore.getInstance("PKCS12")
        val tppCert = this.javaClass.classLoader.getResourceAsStream(config.nordeaTppCertPath)
        keyStore.load(tppCert, config.nordeaTppCertPassword.toCharArray())
        return keyStore.getKey("1", config.nordeaTppCertPassword.toCharArray())
    }

    fun createGetSignatureHeader(requestURI: URI, headers: Map<String, String>): String {
        val path: String = getPath(requestURI)
        val signature = Signature(keyId, "rsa-sha256", null, GET_HEADERS)
        val signerToString = Signer(key, signature).sign("GET", path, headers).toString()
        // strip "Signature" from the beginning, as it should be used as a header
        return signerToString.substring(10, signerToString.length)
    }

    fun createInsertSignature(requestURI: URI, httpMethod: String, body: ByteArray, headers: MutableMap<String, String>): String {
        val path = getPath(requestURI)
        headers["Digest"] = calculateDigest(body)
        headers["Content-type"] = "applicaton/json"

        val signature = Signature(keyId, "rsa-sha256", null, INSERT_HEADERS)
        return Signer(key, signature).sign(httpMethod, path, headers).toString()
    }

    private fun calculateDigest(body: ByteArray?): String {
        return "SHA-256=" + String(Base64.getEncoder().encode(DigestUtils.sha256(body)))
    }

    private fun getPath(requestURI: URI): String {
        return requestURI.rawPath + getQueryIfNotEmpty(requestURI.rawQuery)
    }

    private fun getQueryIfNotEmpty(query: String?): String {
        return if (isNotEmpty(query)) "?$query" else ""
    }

}