package codex_rishi.ecom_spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI myCustomConfig() {
        return new OpenAPI().info(
                new Info()
                        .title("E-commerce application")
                        .description("By Rishi (API documentation for E-Commerce backend)")
        );
    }
}
