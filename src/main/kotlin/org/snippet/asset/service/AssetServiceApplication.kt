package org.snippet.asset.service

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class AssetServiceApplication {

    @Bean
    fun docs(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Asset API").version("v1")
            ).servers(emptyList())
    }
}

fun main(args: Array<String>) {
	runApplication<AssetServiceApplication>(*args)
}

