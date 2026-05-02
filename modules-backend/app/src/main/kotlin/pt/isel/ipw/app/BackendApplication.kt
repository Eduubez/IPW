package pt.isel.ipw.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder






@SpringBootApplication(scanBasePackages = ["pt.isel.ipw"])
class BackendApplication {

	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}