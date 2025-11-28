package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.model.Role;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Autowired
    private UserRepository userRepository;
//
//    @Autowired
//    private EmailService emailService;

    private final Set<String> adminEmails = Set.of(
            "debangshubhattacharya4@gmail.com"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("========== OAuth2 Authentication Success Handler Called ==========");

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String imageUrl = oAuth2User.getAttribute("picture");

        logger.info("OAuth2 login - Email: {}, Name: {}", email, name);

        Role role = adminEmails.contains(email) ? Role.ADMIN : Role.USER;

        // Check if user already exists
        User user = userRepository.findByEmail(email);
        boolean isNewUser = (user == null);

        logger.info("User lookup for '{}': {}", email, (isNewUser ? "NEW USER" : "EXISTING USER"));

        if (isNewUser) {
            logger.info(">>>>> CREATING NEW USER: {}", email);
            user = User.builder()
                    .email(email)
                    .name(name)
                    .imageUrl(imageUrl)
                    .role(role)
                    .build();
        } else {
            logger.info(">>>>> UPDATING EXISTING USER: {}", email);
            user.setName(name);
            user.setImageUrl(imageUrl);
            user.setRole(role);
        }

        // Save user to database
        userRepository.save(user);
        logger.info("User saved to database: {}", email);

        // Send welcome email ONLY for new users
        if (isNewUser) {
            logger.info(">>>>> ATTEMPTING TO SEND WELCOME EMAIL to: {}", email);
            try {
//                emailService.sendWelcomeEmail(email, name);
                logger.info(">>>>> ✅ WELCOME EMAIL SENT SUCCESSFULLY to: {}", email);
            } catch (Exception e) {
                logger.error(">>>>> ❌ FAILED TO SEND WELCOME EMAIL to: {}", email, e);
            }
        } else {
            logger.info(">>>>> Skipping welcome email (existing user login)");
        }


        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );

        OAuth2User newUser = new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email"
        );

        Authentication newAuth =
                new OAuth2AuthenticationToken(newUser, authorities, token.getAuthorizedClientRegistrationId());

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        logger.info(">>>>> Redirecting user to homepage");


        response.sendRedirect("/");
    }
}
