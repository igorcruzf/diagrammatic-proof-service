package uff.br.tcc.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("unused")
class OpenAPIConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Diagrammatic Proof Service")
                    .version("1.0")
                    .description(
                        "Essa API implementa um procedimento de decisão para validade de inclusões no " +
                            "fragmento geométrico de BGL (Basic Graph Logic)."
                    )
            )
            .addServersItem(Server().url("/"))
    }
}
