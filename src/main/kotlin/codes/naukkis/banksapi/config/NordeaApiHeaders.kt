package codes.naukkis.banksapi.config

import codes.naukkis.banksapi.getHttpDate
import codes.naukkis.banksapi.services.NordeaAuthController
import codes.naukkis.banksapi.services.NordeaSignatureGenerator
import java.net.URI
import java.net.http.HttpRequest
import java.util.HashMap

class NordeaApiHeaders(private val config: Config) {
        private val signatureGenerator = NordeaSignatureGenerator(config)

    fun setToUnsigned(builder: HttpRequest.Builder): HttpRequest.Builder {
        builder.setHeader("X-IBM-Client-ID", config.nordeaClientId)
            .setHeader("X-IBM-Client-Secret", config.nordeaClientSecret)
            .setHeader("X-Nordea-Originating-Date", getHttpDate())
            .setHeader("X-Nordea-Originating-Host", "api.nordeaopenbanking.com")
            .setHeader("Signature", "SKIP_SIGNATURE_VALIDATION_FOR_SANDBOX")

        return builder
    }

    fun createGetRequest(uri: URI): HttpRequest {
        val originatingDate = getHttpDate()

        return HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")
            .setHeader("X-IBM-Client-ID", config.nordeaClientId)
            .setHeader("X-IBM-Client-Secret", config.nordeaClientSecret)
            .setHeader("X-Nordea-Originating-Date", originatingDate)
            .setHeader("X-Nordea-Originating-Host", "api.nordeaopenbanking.com")
            .setHeader("Signature", signatureGenerator.createGetSignatureHeader(uri, createMandatoryHeaders(originatingDate)))
            .build()
    }

    fun createPUTRequest(uri: URI, jsonBody: String): HttpRequest.Builder {
        val originatingDate = getHttpDate()

        return HttpRequest.newBuilder()
            .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
            .uri(uri)
            .setHeader("Authorization", "Bearer ${NordeaAuthController(config).getAccessToken().access_token}")
            .setHeader("X-IBM-Client-ID", config.nordeaClientId)
            .setHeader("X-IBM-Client-Secret", config.nordeaClientSecret)
            .setHeader("X-Nordea-Originating-Date", getHttpDate())
            .setHeader("X-Nordea-Originating-Host", "api.nordeaopenbanking.com")
            .setHeader("Signature", signatureGenerator.createInsertSignature(uri, "PUT", jsonBody.toByteArray(), createMandatoryHeaders(originatingDate)))
            .setHeader("Digest", "")
    }

    private fun createMandatoryHeaders(originatingDate: String): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["x-nordea-originating-host"] = "api.nordeaopenbanking.com"
        headers["x-nordea-originating-date"] = originatingDate
        return headers
    }

}