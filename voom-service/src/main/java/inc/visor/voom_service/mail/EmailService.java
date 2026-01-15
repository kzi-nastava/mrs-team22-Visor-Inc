package inc.visor.voom_service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:no-reply@voom.local}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

}
