package app.web.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Testing game system Api",
        description = "API для системы тестирования",
        version = "1.0.0",
        contact = Contact(
            name = "Michael Baklykov",
            email = "baklykovmichael@gmail.com"
        )
    )
)
class OpenApiConfig {
}