package codes.naukkis.banksapi

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.security.Security


@SpringBootApplication
class BanksApiApplication

fun main(args: Array<String>) {
	Security.addProvider(BouncyCastleProvider())
	runApplication<BanksApiApplication>(*args)
}
