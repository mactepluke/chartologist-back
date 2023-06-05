package com.syngleton.chartomancy.model.trading;

import com.syngleton.chartomancy.util.Measurable;
import com.syngleton.chartomancy.util.pdt.PrintableDataTable;
import com.syngleton.chartomancy.util.pdt.PrintableData;
import com.syngleton.chartomancy.util.Check;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;

@Getter
public class TradingAccount implements PrintableDataTable, Measurable {

    private final List<Trade> trades;
    private double balance;
    @Setter
    private boolean enabled;
    @Setter
    private String name;
    @Setter
    boolean liquidated;

    public TradingAccount() {
        this.trades = new ArrayList<>();
        this.balance = 0;
        this.enabled = true;
        this.liquidated = false;
        this.name = "untitled_account";
    }

    public void credit(double value) {
        balance = balance + abs(value);
    }

    public void debit(double value) {

        value = abs(value);

        if (balance <= value) {
            balance = 0;
            liquidated = true;
        }

        balance = balance - value;
    }

    @Override
    public List<String> getHeader() {

        if (Check.notNullNotEmpty(trades))   {
            return trades.get(0).extractCsvHeader();
        }
        return Collections.emptyList();
    }

    @Override
    public List<PrintableData> getPrintableData() {
        return new ArrayList<>(trades);
    }

    @Override
    public double getMeasure() {
        return getBalance();
    }
}
