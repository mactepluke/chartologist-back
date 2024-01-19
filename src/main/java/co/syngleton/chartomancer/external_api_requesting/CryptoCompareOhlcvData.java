package co.syngleton.chartomancer.external_api_requesting;

import java.util.List;

public class CryptoCompareOhlcvData {
    private boolean Aggregated;
    private long TimeFrom;
    private float TimeTo;
    private List<CryptoCompareOhlcvCandle> Data;

    public boolean isAggregated() {
        return Aggregated;
    }

    public void setAggregated(boolean aggregated) {
        Aggregated = aggregated;
    }

    public long getTimeFrom() {
        return TimeFrom;
    }

    public void setTimeFrom(long timeFrom) {
        TimeFrom = timeFrom;
    }

    public float getTimeTo() {
        return TimeTo;
    }

    public void setTimeTo(float timeTo) {
        TimeTo = timeTo;
    }

    public List<CryptoCompareOhlcvCandle> getData() {
        return Data;
    }

    public void setData(List<CryptoCompareOhlcvCandle> data) {
        Data = data;
    }


}