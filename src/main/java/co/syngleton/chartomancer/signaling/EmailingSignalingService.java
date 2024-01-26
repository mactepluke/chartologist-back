package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.emailing.EmailingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
class EmailingSignalingService implements SignalingService {
    private final EmailingService emailingService;
    private final SignalSubscribersDAO signalSubscribersDAO;
    private final SignalingProperties signalingProperties;

    @Override
    public void sendSignal(String subject, String body) {

        Set<String> addresses = signalingProperties.getSubscribersAddresses();

        signalSubscribersDAO.loadSignalSubscribers().forEach(subscriber -> {
                    if (subscriber.isEnabled()) {
                        addresses.add(subscriber.getAddress());
                    }
                }
        );
        emailingService.sendEmails(addresses, subject, body);
    }

}
