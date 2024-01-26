package co.syngleton.chartomancer.emailing;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
class DefaultEmailingService implements EmailingService {
    private final JavaMailSender mailSender;

    @Override
    public void sendEmails(Set<String> addresses, String subject, String body) {
        addresses.forEach(address -> sendEmail(address, subject, body));
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

}
