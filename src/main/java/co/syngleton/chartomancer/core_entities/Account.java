package co.syngleton.chartomancer.core_entities;

public interface Account extends Accountable {

    void credit(double amount);

    void debit(double amount);
}
