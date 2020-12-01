package codes.naukkis.banksapi.model

class AccessToken(val access_token: String,
                  val expires_in: Number,
                  val token_type: String,
                  val refresh_token: String) {
}