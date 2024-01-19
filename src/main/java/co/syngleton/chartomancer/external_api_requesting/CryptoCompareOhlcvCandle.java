package co.syngleton.chartomancer.external_api_requesting;

final class CryptoCompareOhlcvCandle {
    private long time;
    private float high;
    private float low;
    private float open;
    private float volumefrom;
    private float volumeto;
    private float close;
    private String conversionType;
    private String conversionSymbol;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getVolumefrom() {
        return volumefrom;
    }

    public void setVolumefrom(float volumefrom) {
        this.volumefrom = volumefrom;
    }

    public float getVolumeto() {
        return volumeto;
    }

    public void setVolumeto(float volumeto) {
        this.volumeto = volumeto;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public String getConversionType() {
        return conversionType;
    }

    public void setConversionType(String conversionType) {
        this.conversionType = conversionType;
    }

    public String getConversionSymbol() {
        return conversionSymbol;
    }

    public void setConversionSymbol(String conversionSymbol) {
        this.conversionSymbol = conversionSymbol;
    }
}
