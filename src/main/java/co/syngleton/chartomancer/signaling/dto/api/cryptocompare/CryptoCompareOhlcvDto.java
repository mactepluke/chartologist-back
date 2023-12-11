package co.syngleton.chartomancer.signaling.dto.api.cryptocompare;

public class CryptoCompareOhlcvDto {

    private String Response;
    private String Message;
    private boolean HasWarning;
    private int Type;
    private Object RateLimit;
    private CryptoCompareOhlcvData Data;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isHasWarning() {
        return HasWarning;
    }

    public void setHasWarning(boolean hasWarning) {
        HasWarning = hasWarning;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public Object getRateLimit() {
        return RateLimit;
    }

    public void setRateLimit(Object rateLimit) {
        RateLimit = rateLimit;
    }

    public CryptoCompareOhlcvData getData() {
        return Data;
    }

    public void setData(CryptoCompareOhlcvData data) {
        Data = data;
    }


}
