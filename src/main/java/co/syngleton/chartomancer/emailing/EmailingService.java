package co.syngleton.chartomancer.emailing;

import java.util.Set;

public interface EmailingService {

    void sendEmails(Set<String> addresses, String subject, String body);
}
