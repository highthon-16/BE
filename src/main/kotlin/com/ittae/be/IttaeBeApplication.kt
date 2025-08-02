package com.ittae.be

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.dotenv

@SpringBootApplication
class IttaeBeApplication

fun main(args: Array<String>) {
    val dotenv = dotenv()
    System.setProperty("DB_URL", dotenv["DB_URL"])
    System.setProperty("DB_USERNAME", dotenv["DB_USERNAME"])
    System.setProperty("DB_PASSWORD", dotenv["DB_PASSWORD"])
    System.setProperty("JWT_SECRET", dotenv["JWT_SECRET"])
    System.setProperty("JWT_EXPIRATION", dotenv["JWT_EXPIRATION"])

    runApplication<IttaeBeApplication>(*args)
}
