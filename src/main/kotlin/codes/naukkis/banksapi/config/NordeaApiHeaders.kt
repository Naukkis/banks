package codes.naukkis.banksapi.config

import codes.naukkis.banksapi.getHttpDate
import java.net.http.HttpRequest

class NordeaApiHeaders(private val config: Config) {

    fun setTo(builder: HttpRequest.Builder): HttpRequest.Builder {
        builder.setHeader("X-IBM-Client-ID", config.nordeaClientId)
            .setHeader("X-IBM-Client-Secret", config.nordeaClientSecret)
            .setHeader("X-Nordea-Originating-Date", getHttpDate())
            .setHeader("X-Nordea-Originating-Host", "api.nordeaopenbanking.com")
            .setHeader("Signature", "SKIP_SIGNATURE_VALIDATION_FOR_SANDBOX")
        //      .setHeader("Digest", "sha-256=qKEkYt43vgJXW0ibKcHuvm+GhtsOKa/yISq9xk5pVV0=")
        return builder
    }
}