package codes.naukkis.banksapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration()
@PropertySource("classpath:bank.properties")
class Config {
    @Value("\${nordea.tppCert}")
    lateinit var nordeaTppCertPath: String
    @Value("\${nordea.tppCertPassword}")
    lateinit var nordeaTppCertPassword: String
    @Value("\${nordea.clientId}")
    lateinit var nordeaClientId: String
    @Value("\${nordea.clientSecret}")
    lateinit var nordeaClientSecret: String
    @Value("\${nordea.redirectUrl}")
    lateinit var nordeaRedirectUrl: String
    @Value("\${op.apikey}")
    lateinit var opApiKey: String
    @Value("\${op.staticauth}")
    lateinit var opStaticAuth: String
    @Value("\${op.client_id}")
    lateinit var opClientId: String
    @Value("\${op.client_secret}")
    lateinit var opClientSecret: String
    @Value("\${op.tpp_id}")
    lateinit var opTppId: String
    @Value("\${op.unique_client_identifier}")
    lateinit var opUuid: String
    @Value("\${op.tpp_client_secret}")
    lateinit var opTppClientSecret: String
    @Value("\${op.tppCert}")
    lateinit var opTppCert: String
    @Value("\${op.tppCertPassword}")
    lateinit var opTppCertPassword: String
    @Value("\${op.redirectUrl}")
    lateinit var opRedirectUrl: String
    @Value("\${op.redirectUrlEncoded}")
    lateinit var opRedirectUrlEncoded: String
    @Value("\${op.tppKid}")
    lateinit var opTppKid: String
    @Value("\${op.signingKey}")
    lateinit var opSigningKey: String
    @Value("\${op.staticSigningKey}")
    lateinit var opStaticSigningKey: String
    @Value("\${op.staticAud}")
    lateinit var opStaticAud: String

    @Value("\${spankki.signingCert}")
    lateinit var spankkiSigningKey: String
    @Value("\${spankki.tlsCert}")
    lateinit var spankkiTlsCert: String
    @Value("\${spankki.certPassword}")
    lateinit var spankkiCertPassword: String
    @Value("\${spankki.clientId}")
    lateinit var spankkiClientId: String
    @Value("\${spankki.apiKey}")
    lateinit var spankkiApiKey: String

    @Value("\${handelsbanken.apiKey}")
    lateinit var handelsbankenApiKey: String
    @Value("\${handelsbanken.staticConsentId}")
    lateinit var handelsbankenStaticConsentId: String
}