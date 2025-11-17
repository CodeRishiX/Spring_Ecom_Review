package codex_rishi.ecom_spring.config;

import codex_rishi.ecom_spring.service.CustomOAuth2SuccessHandler;
import codex_rishi.ecom_spring.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomOAuth2SuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for development (enable in production with proper token handling)
                .csrf(csrf -> csrf.disable())

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC pages
                        .requestMatchers(
                                "/", "/about", "/new-arrivals", "/product-details",
                                "/css/**", "/js/**", "/images/**", "/assets/**",
                                "/api/products", "/api/products/search",
                                "/api/product/*/image", "/api/New-Arrival",
                                "/api/productdetails/*",
                                "/api/auth/status", "/api/user/role"
                        ).permitAll()

                        // USER ACCESS — authenticated users (NOT admin-only!)
                        .requestMatchers(
                                "/cart",
                                "/api/cart/**"
                        ).authenticated()

                        // ADMIN ACCESS ONLY
                        .requestMatchers(
                                "/add-product",
                                "/update-product",
                                "/api/product/**"
                        ).hasRole("ADMIN")

                        // everything else requires login
                        .anyRequest().authenticated()
                )

                // OAuth2 Login Configuration
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google")    // Google OAuth2 login page
                        .defaultSuccessUrl("/", true)                 // Backup redirect (always go to home)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // Custom user service to save user to DB
                        )
                        .successHandler(successHandler)               // Custom success handler for post-login logic
                )

                // Logout Configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")                         // Logout endpoint
                        .logoutSuccessUrl("/")                        // Redirect to home after logout
                        .invalidateHttpSession(true)                  // Invalidate session
                        .clearAuthentication(true)                    // Clear authentication
                        .deleteCookies("JSESSIONID")                  // Delete session cookie
                );

        return http.build();
    }
}
