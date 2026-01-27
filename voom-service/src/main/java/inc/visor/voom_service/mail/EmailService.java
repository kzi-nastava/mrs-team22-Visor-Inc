package inc.visor.voom_service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.activation.model.ActivationToken;
import inc.visor.voom_service.activation.service.ActivationTokenService;
import inc.visor.voom_service.auth.user.model.User;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final ActivationTokenService activationTokenService;

    @Value("${spring.mail.from:no-reply@voom.local}")
    private String from;

    public EmailService(JavaMailSender mailSender, ActivationTokenService activationTokenService) {
        this.mailSender = mailSender;
        this.activationTokenService = activationTokenService;
    }

    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendTestMail(String to) {
        send(
                to,
                "Voom test email",
                "If you see this email in MailHog, EmailService works."
        );
    }

    public void sendActivationEmail(String to, String activationLink) {
        send(
                to,
                "Activate your Voom account",
                """
      Your account has been created.

      Please activate your account by setting your password using the link below.
      This link is valid for 24 hours.

      %s
      """.formatted(activationLink)
        );
    }

    public void sendActivationEmail(User user) {
        ActivationToken activationToken
                = activationTokenService.createForUser(user);

        String activationLink
                = "http://localhost:4200/voom/activate?token=" + activationToken.getToken();

        send(
                user.getEmail(),
                "Activate your Voom account",
                """
      Your account has been created.

      Please activate your account by setting your password using the link below.
      This link is valid for 24 hours.

      %s
      """.formatted(activationLink)
        );
    }

    public void sendRideTrackingLink(String to, String addressInfo, String trackingUrl) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = """
            <div style="background-color: #ffffff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: auto; text-align: center; color: #1a1a1a;">
                
                <div style="padding: 40px 0 20px 0;">
                    <h1 style="color: #001C55; margin: 0; font-size: 36px; font-style: italic; font-weight: bold;">Voom</h1>
                </div>

                <div style="padding: 20px 40px;">
                    <h2 style="font-weight: 500; font-size: 24px;">You have been added to the ride!</h2>
                    
                    <p style="margin: 25px 0; font-size: 18px;">
                        📍 %s
                    </p>
                    
                    <p style="color: #4a4a4a; font-size: 16px;">You can follow the ride on the link below</p>
                    
                    <p style="margin: 30px 0;">
                        <a href="%s" style="color: #0047AB; text-decoration: underline; word-break: break-all; font-size: 16px;">
                            %s
                        </a>
                    </p>
                </div>

                <div style="background-color: #121F4B; color: #ffffff; padding: 25px; margin-top: 40px; font-size: 14px;">
                    Copyright © 2025 Visor Inc
                </div>
            </div>
            """.formatted(addressInfo, trackingUrl, trackingUrl);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("You have been added to a ride");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException("Error building tracking email", e);
        }
    }

    public void sendRideCompletionEmail(String to, String addressInfo) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = """
        <div style="background-color: #ffffff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: auto; text-align: center; color: #1a1a1a;">
            
            <div style="padding: 40px 0 20px 0;">
                <h1 style="color: #001C55; margin: 0; font-size: 36px; font-style: italic; font-weight: bold;">Voom</h1>
            </div>

            <div style="padding: 20px 40px;">
                <h2 style="font-weight: 500; font-size: 24px;">Your ride has been completed!</h2>
                
                <p style="margin: 25px 0; font-size: 18px;">
                    📍 %s
                </p>
                
                <div style="color: #4a4a4a; font-size: 16px; line-height: 1.5; margin-top: 30px;">
                    <p>Thank you for using our services.</p>
                    <p>Looking forward to another drive.</p>
                </div>
            </div>

            <div style="background-color: #121F4B; color: #ffffff; padding: 25px; margin-top: 40px; font-size: 14px;">
                Copyright © 2025 Visor Inc
            </div>
        </div>
        """.formatted(addressInfo);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your Voom ride is complete");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException("Error building completion email", e);
        }
    }

}
