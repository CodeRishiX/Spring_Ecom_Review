package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.model.User;
import codex_rishi.ecom_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/role")
    public ResponseEntity<String> getUserRole(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(400).body("Email not found in token");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        return ResponseEntity.ok("ROLE_" + user.getRole().name());
    }
}
