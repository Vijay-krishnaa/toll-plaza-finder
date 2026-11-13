package com.tollfinder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI tollPlazaOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Toll Plaza Finder API")
            .description("REST API to find toll plazas between two Indian pincodes using Mappls routing.")
            .version("1.0.0"));
  }
}