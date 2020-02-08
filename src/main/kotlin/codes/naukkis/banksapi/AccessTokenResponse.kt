package codes.naukkis.banksapi

data class AccessTokenResponse(
        var access_token: String,
        var expires_in: Number,
        var token_type: String,
        var refresh_token: String) {
}