package com.co.mercadolibre.geolocalizacion.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("API para la gestión de geolocalización basada en direcciones IP.");

        Contact myContact = new Contact();
        myContact.setName("Andres Vasquez");
        myContact.setEmail("andresdavid.vasquez@gmail.com");

        Info information = new Info()
                .title("Geolocalización API - MELI")
                .version("1.0")
                .description("API para la gestión de geolocalización basada en direcciones IP.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }

}