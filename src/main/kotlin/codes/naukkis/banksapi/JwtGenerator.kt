package codes.naukkis.banksapi

import codes.naukkis.banksapi.config.Config
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.io.InputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*
import java.util.Base64.getMimeDecoder
import java.util.regex.Pattern

class JwtGenerator(private val config: Config) {

    fun createJwt(authorizationId: String): String {
        val keyFactory = KeyFactory.getInstance("ECDSA")
        val pkcS8EncodedKeySpec = PKCS8EncodedKeySpec(loadPEM(config.opSigningKey))
        val privateKey = keyFactory.generatePrivate(pkcS8EncodedKeySpec)
        return Jwts.builder()
            .setHeaderParam("kid", config.opTppKid)
            .setHeaderParam("typ", "JWT")
            .setIssuedAt(Date(LocalDate.now().toEpochSecond(LocalTime.now(), ZoneOffset.UTC)))
            .setClaims(createClaims(authorizationId))
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }

    fun createJwtUsingStaticParams(): String {
        val keyFactory = KeyFactory.getInstance("RSA")
        val pkcS8EncodedKeySpec = PKCS8EncodedKeySpec(loadPEM(config.opStaticSigningKey))
        val privateKey = keyFactory.generatePrivate(pkcS8EncodedKeySpec)
        val compact = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(getStaticClaims())
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact()

        return compact
    }

    fun verify(authorizationId: String) {
        val jwtToken = createJwt(authorizationId)
        val keyFactory = KeyFactory.getInstance("EC")
        val pkcS8EncodedKeySpec = PKCS8EncodedKeySpec(loadPEM(config.opSigningKey))

        val privateKey = keyFactory.generatePrivate(pkcS8EncodedKeySpec)
        val generatePublic = keyFactory.generatePublic(pkcS8EncodedKeySpec)
        val body = Jwts.parserBuilder()
            .setSigningKey(privateKey)
            .build()
            .parseClaimsJws(jwtToken)
            .body
        println(body)
    }

    private fun createClaims(authorizationId: String): Map<String, Any> {
        return mapOf("aud" to "https://mtls-apis.psd2-sandbox.op.fi",
            "iss" to config.opTppId,
            "response_type" to "code id_token",
            "client_id" to config.opUuid,
            "redirect_uri" to config.opRedirectUrl,
            "scope" to "openid accounts",
            "state" to generateState(),
            "nonce" to generateState() + "nonsense",
            "max_age" to 86400,
            "claims" to nestedClaims(authorizationId))
    }

    private fun getStaticClaims(): Map<String, Any> {
        return mapOf("aud" to "op_open_oauth",
            "scope" to "openid accounts accounts:transactions",
            "iss" to config.opApiKey,
            "response_type" to "code",
            "state" to generateState(),
            "redirect_uri" to config.opRedirectUrl,
            "iat" to Instant.now().epochSecond,
            "exp" to Instant.now().epochSecond + 1000000,
            "client_id" to config.opApiKey)
    }

    private fun generateState() = "1122234"

    private fun nestedClaims(authorizationId: String): Map<String, Map<String, Any>> {
        return content(authorizationId)
    }

    private fun content(authorizationId: String): Map<String, Map<String, Any>> {
        return mapOf("userinfo" to userinfo(authorizationId),
            "id_token" to idToken(authorizationId))
    }

    private fun userinfo(authorizationId: String): Map<String, Map<String, Any>> {
        return mapOf("authorizationId" to authorizationId(authorizationId))
    }

    private fun idToken(authorizationId: String): Map<String, Any> {
        return mapOf("authorizationId" to authorizationId(authorizationId),
            "acr" to acr())
    }

    private fun authorizationId(authorizationId: String): Map<String, Any> {
        return mapOf("value" to authorizationId, "essential" to true)
    }

    private fun acr(): Map<String, Any> {
        return mapOf("essential" to true,
            "values" to arrayOf("urn:openbanking:psd2:sca"))
    }

    private fun loadPEM(resource: String): ByteArray? {
        val url: URL = javaClass.getResource(resource)
        val `in`: InputStream = url.openStream()
        val pem = String(`in`.readAllBytes(), StandardCharsets.UTF_8)
        val parse: Pattern = Pattern.compile("(?m)(?s)^---*BEGIN.*---*$(.*)^---*END.*---*$.*")
        val encoded: String = parse.matcher(pem).replaceFirst("$1")

        return getMimeDecoder().decode(encoded)
    }

}