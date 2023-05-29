package com.syngleton.chartomancy.model.trading;

import com.syngleton.chartomancy.util.csv.CsvExportable;
import com.syngleton.chartomancy.util.csv.CsvRow;
import com.syngleton.chartomancy.util.Check;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

@Getter
public class TradingAccount implements CsvExportable {

    private final List<Trade> trades;
    private float balance;
    @Setter
    private boolean enabled;

    public TradingAccount() {
        this.trades = new ArrayList<>();
        this.balance = 0;
        this.enabled = true;
    }

    public void credit(float value) {
        balance = balance + abs(value);
    }

    public boolean debit(float value) {

        value = abs(value);

        if (balance < value) {
            return false;
        }

        balance = balance - value;

        return true;
    }

    @Override
    public String getCsvHeader() {

        if (Check.notNullNotEmpty(trades))   {
            return trades.get(0).extractCsvHeader();
        }
        return null;
    }

    @Override
    public List<CsvRow> getCsvRows() {
        return new ArrayList<>(trades);
    }


}
