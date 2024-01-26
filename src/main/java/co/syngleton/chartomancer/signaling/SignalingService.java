package co.syngleton.chartomancer.signaling;

interface SignalingService {

    void sendSignal(String subject, String body);
}
