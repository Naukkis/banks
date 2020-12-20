package codes.naukkis.banksapi.model

class SpankkiAccessToken(val access_token: String,
                         val expires_in: Number,
                         val token_type: String,
                         val scope: String) {
}