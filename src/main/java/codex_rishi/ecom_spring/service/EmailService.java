//package codex_rishi.ecom_spring.service;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//
//    public void sendWelcomeEmail(String toEmail, String userName) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setTo(toEmail);
//            helper.setSubject("🎉 Welcome to SpringCart, " + userName + "!");
//
//            String htmlContent = """
//                <div style="font-family: Arial, sans-serif; background-color:#f7f7f7; padding:20px;">
//                    <div style="max-width:600px; margin:auto; background:white; border-radius:10px;
//                                padding:25px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
//
//                        <h2 style="color:#4CAF50; text-align:center;">
//                            🌟 Welcome to <strong>SpringCart</strong>!
//                        </h2>
//
//                        <p style="font-size:16px; color:#333;">
//                            Hi <strong>%s</strong>,<br><br>
//                            We're excited to have you join our shopping community!
//                            Your account has been successfully created.
//                        </p>
//
//                        <div style="text-align:center; margin:25px 0;">
//                            <a href="https://springcart.com"
//                               style="background:#4CAF50; padding:12px 22px;
//                                      color:white; text-decoration:none;
//                                      border-radius:5px; font-weight:bold;">
//                                Start Shopping →
//                            </a>
//                        </div>
//
//                        <p style="font-size:15px; color:#555;">
//                            Here's what you can do now:
//                            <ul>
//                                <li>Browse exclusive products 🛍️</li>
//                                <li>Track your orders in real-time 📦</li>
//                                <li>Access special discounts and offers 💰</li>
//                            </ul>
//                        </p>
//
//                        <hr style="border:none; border-top:1px solid #eee; margin:25px 0;">
//
//                        <p style="font-size:14px; color:#777; text-align:center;">
//                            If you have any questions, just reply to this email — we're always here to help!<br>
//                            <strong>— The SpringCart Team</strong>
//                        </p>
//                    </div>
//                </div>
//                """.formatted(userName);
//
//            helper.setText(htmlContent, true);
//            mailSender.send(message);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
//    public void sendOrderSuccessEmail(String toEmail, String userName, String orderId, BigDecimal amount) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setTo(toEmail);
//            helper.setSubject("✔️ Payment Successful – Your Order is Confirmed!");
//
//            String htmlContent = """
//            <div style="font-family: Arial, sans-serif; background-color:#f7f7f7; padding:20px;">
//                <div style="max-width:600px; margin:auto; background:white; border-radius:10px;
//                            padding:25px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
//                    <h2 style="color:#4CAF50; text-align:center;">🎉 Payment Successful!</h2>
//
//                    <p style="font-size:16px; color:#333;">
//                        Hi <strong>%s</strong>,<br><br>
//                        Your payment has been received and your order is now being processed.
//                    </p>
//
//                    <div style="background:#f0fff4; padding:15px 20px; border-radius:8px; margin:20px 0;">
//                        <p style="font-size:15px; color:#2e7d32;">
//                            <strong>Order ID:</strong> %s<br>
//                            <strong>Total Amount:</strong> ₹%s<br>
//                            <strong>Status:</strong> <span style="color:#4CAF50;">Confirmed ✔️</span>
//                        </p>
//                    </div>
//
//                    <div style="text-align:center; margin:25px 0;">
//                        <a href="https://springcart.com/orders"
//                           style="background:#4CAF50; padding:12px 22px; color:white; text-decoration:none;
//                                  border-radius:5px; font-weight:bold;">
//                            View Order Status →
//                        </a>
//                    </div>
//
//                    <p style="font-size:14px; color:#777; text-align:center;">
//                        Your items will be shipped soon.
//                        If you have any questions, reply to this email anytime!
//                        <br><strong>— SpringCart Team</strong>
//                    </p>
//                </div>
//            </div>
//            """.formatted(userName, orderId, amount);
//
//            helper.setText(htmlContent, true);
//            mailSender.send(message);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send order success email", e);
//        }
//    }
//    public void sendOrderFailureEmail(String toEmail, String userName, String orderId, String failureReason) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setTo(toEmail);
//            helper.setSubject("❌ Payment Failed – Action Needed");
//
//            String htmlContent = """
//            <div style="font-family: Arial, sans-serif; background-color:#f7f7f7; padding:20px;">
//                <div style="max-width:600px; margin:auto; background:white; border-radius:10px;
//                            padding:25px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
//
//                    <h2 style="color:#d32f2f; text-align:center;">⚠️ Payment Failed</h2>
//
//                    <p style="font-size:16px; color:#333;">
//                        Hi <strong>%s</strong>,<br><br>
//                        Unfortunately, your recent payment attempt was not completed.
//                    </p>
//
//                    <div style="background:#ffebee; padding:15px 20px; border-radius:8px; margin:20px 0;">
//                        <p style="font-size:15px; color:#b71c1c;">
//                            <strong>Order ID:</strong> %s<br>
//                            <strong>Reason:</strong> %s<br>
//                            <strong>Status:</strong> <span style="color:#d32f2f;">Failed ❌</span>
//                        </p>
//                    </div>
//
//                    <div style="text-align:center; margin:25px 0;">
//                        <a href="https://springcart.com/payment/retry/%s"
//                           style="background:#d32f2f; padding:12px 22px; color:white; text-decoration:none;
//                                  border-radius:5px; font-weight:bold;">
//                            Retry Payment →
//                        </a>
//                    </div>
//
//                    <p style="font-size:14px; color:#777; text-align:center;">
//                        If money was deducted, it will be refunded automatically within 3–5 working days.
//                        <br>Need help? Reply to this email.
//                        <br><strong>— SpringCart Support</strong>
//                    </p>
//                </div>
//            </div>
//            """.formatted(userName, orderId, failureReason, orderId);
//
//            helper.setText(htmlContent, true);
//            mailSender.send(message);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send order failure email", e);
//        }
//    }
//
//
//}
