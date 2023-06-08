package com.syngleton.chartomancy.model.trading.interfaces;

public interface Account extends Accountable {

    void credit(double amount);

    void debit(double amount);
}
