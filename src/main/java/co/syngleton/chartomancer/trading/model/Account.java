package co.syngleton.chartomancer.trading.model;

public interface Account extends Accountable {

    void credit(double amount);

    void debit(double amount);
}
