package co.syngleton.chartomancer.signaling;

public interface SignalingService {
    void sendSignal(String to, String subject, String body);
}
