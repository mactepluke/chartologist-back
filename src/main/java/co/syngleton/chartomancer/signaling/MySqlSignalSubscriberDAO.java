package co.syngleton.chartomancer.signaling;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
class MySqlSignalSubscriberDAO implements SignalSubscribersDAO {
    public Set<SubscriberDTO> loadSignalSubscribers() {
        return null;
    }
}
