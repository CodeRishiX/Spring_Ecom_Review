package codex_rishi.ecom_spring.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        logger.info("========== ENTERED CustomOAuth2UserService.loadUser() ==========");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return oAuth2User;
    }
}
