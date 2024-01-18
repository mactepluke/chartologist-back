package co.syngleton.chartomancer.shared_domain;

public interface Account extends Accountable {

    void credit(double amount);

    void debit(double amount);
}
