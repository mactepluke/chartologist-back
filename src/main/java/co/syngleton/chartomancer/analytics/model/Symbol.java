package co.syngleton.chartomancer.analytics.model;

public enum Symbol {
    UNDEFINED("VOID", "VOID"),
    BTC_USD("BTC", "USD"),
    ETH_USD("BTC", "USD");

    public final String base;
    public final String quote;

    Symbol(String base, String quote) {
        this.base = base;
        this.quote = quote;
    }
}
