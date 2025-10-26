package nl.demo.shakespeare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI shakespeareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shakespeare API")
                        .description("CRUD & search API voor Shakespeare plays")
                        .version("1.0.0"));
    }
}
