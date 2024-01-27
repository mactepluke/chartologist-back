package co.syngleton.chartomancer.signaling;

import java.util.Set;

interface SignalSubscribersDAO {
    Set<SubscriberDTO> loadSignalSubscribers();
}
