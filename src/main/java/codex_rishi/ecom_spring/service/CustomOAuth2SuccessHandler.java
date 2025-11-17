package codex_rishi.ecom_spring.service;

import codex_rishi.ecom_spring.model.Role;
import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    private UserRepository userRepository;

    private final Set<String> adminEmails = Set.of(
            "debangshubhattacharya4@gmail.com"
    );
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String imageUrl = oAuth2User.getAttribute("picture");

        Role role = adminEmails.contains(email) ? Role.ADMIN : Role.USER;

        // Save / update in DB
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = User.builder()
                    .email(email)
                    .name(name)
                    .imageUrl(imageUrl)
                    .role(role)
                    .build();
        } else {
            user.setName(name);
            user.setImageUrl(imageUrl);
            user.setRole(role);
        }
        userRepository.save(user);

        // ⭐ FIX: Attach ROLE to Spring Security session
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

        // Redirect
        response.sendRedirect("/");
    }

}
