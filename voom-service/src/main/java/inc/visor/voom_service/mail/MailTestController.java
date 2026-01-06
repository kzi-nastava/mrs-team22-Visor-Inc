package inc.visor.voom_service.mail;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/mail")
public class MailTestController {

  private final EmailService emailService;

  public MailTestController(EmailService emailService) {
    this.emailService = emailService;
  }

  @PostMapping
  public ResponseEntity<Void> send(@RequestParam String to) {
    emailService.sendTestMail(to);
    return ResponseEntity.ok().build();
  }
}
