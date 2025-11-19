package codex_rishi.ecom_spring.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🎉 Welcome to SpringCart, " + userName + "!");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; background-color:#f7f7f7; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:white; border-radius:10px; 
                                padding:25px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">

                        <h2 style="color:#4CAF50; text-align:center;">
                            🌟 Welcome to <strong>SpringCart</strong>!
                        </h2>

                        <p style="font-size:16px; color:#333;">
                            Hi <strong>%s</strong>,<br><br>
                            We're excited to have you join our shopping community!  
                            Your account has been successfully created.
                        </p>

                        <div style="text-align:center; margin:25px 0;">
                            <a href="https://springcart.com"
                               style="background:#4CAF50; padding:12px 22px; 
                                      color:white; text-decoration:none; 
                                      border-radius:5px; font-weight:bold;">
                                Start Shopping →
                            </a>
                        </div>

                        <p style="font-size:15px; color:#555;">
                            Here's what you can do now:
                            <ul>
                                <li>Browse exclusive products 🛍️</li>
                                <li>Track your orders in real-time 📦</li>
                                <li>Access special discounts and offers 💰</li>
                            </ul>
                        </p>

                        <hr style="border:none; border-top:1px solid #eee; margin:25px 0;">

                        <p style="font-size:14px; color:#777; text-align:center;">
                            If you have any questions, just reply to this email — we're always here to help!<br>
                            <strong>— The SpringCart Team</strong>
                        </p>
                    </div>
                </div>
                """.formatted(userName);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
