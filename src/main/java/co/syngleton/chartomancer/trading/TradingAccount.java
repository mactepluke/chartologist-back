package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.shared_domain.Account;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.datatabletool.PrintableData;
import co.syngleton.chartomancer.util.datatabletool.PrintableDataTable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

import static java.lang.Math.abs;

@Getter
public class TradingAccount implements PrintableDataTable, Account {

    private static final String DEFAULT_CURRENCY = "$";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String SEP = " | ";

    private final List<Trade> trades;
    private final String currency;
    @Setter
    boolean liquidated;
    private double balance;
    @Setter
    private boolean enabled;
    @Setter
    private String name;

    public TradingAccount() {
        this.trades = new ArrayList<>();
        this.balance = 0;
        this.enabled = true;
        this.liquidated = false;
        this.name = "untitled_account";
        this.currency = DEFAULT_CURRENCY;
    }

    public void credit(double value) {
        balance = Format.roundTwoDigits(balance + abs(value));
    }

    public void debit(double value) {

        value = abs(value);
        if (balance <= value) {
            balance = 0;
            liquidated = true;
        }
        balance = Format.roundTwoDigits(balance - value);
    }

    @Override
    public List<String> getHeader() {

        if (Check.isNotEmpty(trades)) {
            return trades.get(0).extractCsvHeader();
        }
        return Collections.emptyList();
    }

    @Override
    public List<PrintableData> getPrintableData() {
        return new ArrayList<>(trades);
    }

    public ProfitFactor getProfitFactorQualification() {
        return ProfitFactor.getQualification(getProfitFactor());
    }

    public double getProfitFactor() {
        return abs(Format.roundTwoDigits(trades.stream().filter(trade -> trade.getPnL() > 0).mapToDouble(Trade::getPnL).sum()
                / trades.stream().filter(trade -> trade.getPnL() < 0).mapToDouble(Trade::getPnL).sum()));
    }

    public long getTotalTradeDurationsInSeconds() {
        return trades.stream().mapToLong(Trade::getTradeDurationInSeconds).sum();
    }

    public String generatePrintableTradesStats() {

        double profitFactor = getProfitFactor();

        return NEW_LINE +
                "*********************     All" + SEP + "Longs" + SEP + "Shorts" + NEW_LINE +
                "Total PnL                  " + currency + " " + getTotalPnl() + SEP + currency + " " + getLongPnl() + SEP + currency + " " + getShortPnl() + NEW_LINE +
                "Total Return               " + getTotalReturnPercentage() + "%" + SEP + getLongReturnPercentage() + "%" + SEP + getShortReturnPercentage() + "%" + NEW_LINE +
                "Avg. return per trade      " + getAverageReturnPercentagePerTrade() + "%" + SEP + getAverageReturnPercentagePerLong() + "%" + SEP + getAverageReturnPercentagePerShort() + "%" + NEW_LINE +
                "Total # of trades          " + getNumberOfTrades() + SEP + getNumberOfLongs() + SEP + getNumberOfShorts() + NEW_LINE +
                "Batting avg.               " + getTotalBattingAveragePercentage() + "%" + SEP + getLongBattingAveragePercentage() + "%" + SEP + getShortBattingAveragePercentage() + "%" + NEW_LINE +
                "Win/Loss ratio             " + getTotalWinToLossRatio() + SEP + getLongWinToLossRatio() + SEP + getShortWinToLossRatio() + NEW_LINE +
                "Average PnL                " + currency + " " + getTotalAveragePnL() + SEP + currency + " " + getLongAveragePnL() + SEP + currency + " " + getShortAveragePnL() + NEW_LINE +
                "** Profit factor: " + profitFactor + ", qualification: " + ProfitFactor.getQualification(profitFactor) + NEW_LINE;
    }

    public double getTotalPnl() {
        return Format.roundTwoDigits(trades.stream().mapToDouble(Trade::getPnL).sum());
    }

    public double getLongPnl() {
        return Format.roundTwoDigits(trades.stream().filter(Trade::isSide).mapToDouble(Trade::getPnL).sum());
    }

    public double getShortPnl() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> !trade.isSide()).mapToDouble(Trade::getPnL).sum());
    }

    public double getTotalReturnPercentage() {
        return Format.roundTwoDigits(trades.stream().mapToDouble(Trade::getReturnPercentage).sum());
    }

    public double getLongReturnPercentage() {
        return Format.roundTwoDigits(trades.stream().filter(Trade::isSide).mapToDouble(Trade::getReturnPercentage).sum());
    }

    public double getShortReturnPercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> !trade.isSide()).mapToDouble(Trade::getReturnPercentage).sum());
    }

    public double getAverageReturnPercentagePerTrade() {
        return Format.roundTwoDigits(getTotalReturnPercentage() / getNumberOfTrades());
    }

    public double getAverageReturnPercentagePerLong() {
        return Format.roundTwoDigits(getLongReturnPercentage() / getNumberOfLongs());
    }

    public double getAverageReturnPercentagePerShort() {
        return Format.roundTwoDigits(getShortReturnPercentage() / getNumberOfShorts());
    }

    public long getNumberOfTrades() {
        return trades.size();
    }

    public long getNumberOfLongs() {
        return trades.stream().filter(Trade::isSide).count();
    }

    public long getNumberOfShorts() {
        return trades.stream().filter(trade -> !trade.isSide()).count();
    }

    public double getTotalBattingAveragePercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> trade.getPnL() > 0).count() / (double) getNumberOfTrades()) * 100;
    }

    public double getLongBattingAveragePercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> trade.getPnL() > 0 && trade.isSide()).count() / (double) getNumberOfLongs()) * 100;
    }

    public double getShortBattingAveragePercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> trade.getPnL() > 0 && !trade.isSide()).count() / (double) getNumberOfShorts()) * 100;
    }

    public double getTotalWinToLossRatio() {
        return Format.roundTwoDigits((double) trades.stream().filter(trade -> trade.getPnL() > 0).count()
                / trades.stream().filter(trade -> trade.getPnL() < 0).count());
    }

    public double getLongWinToLossRatio() {
        return Format.roundTwoDigits((double) trades.stream().filter(trade -> trade.getPnL() > 0 && trade.isSide()).count()
                / trades.stream().filter(trade -> trade.getPnL() < 0 && trade.isSide()).count());
    }

    public double getShortWinToLossRatio() {
        return Format.roundTwoDigits((double) trades.stream().filter(trade -> trade.getPnL() > 0 && !trade.isSide()).count()
                / trades.stream().filter(trade -> trade.getPnL() < 0 && !trade.isSide()).count());
    }

    public double getTotalAveragePnL() {

        double average = 0;

        OptionalDouble result = trades.stream().mapToDouble(Trade::getPnL).average();
        if (result.isPresent()) {
            average = result.getAsDouble();
        }

        return Format.roundTwoDigits(average);
    }

    public double getLongAveragePnL() {

        double average = 0;

        OptionalDouble result = trades.stream().filter(Trade::isSide).mapToDouble(Trade::getPnL).average();
        if (result.isPresent()) {
            average = result.getAsDouble();
        }

        return Format.roundTwoDigits(average);
    }

    public double getShortAveragePnL() {

        double average = 0;

        OptionalDouble result = trades.stream().filter(trade -> !trade.isSide()).mapToDouble(Trade::getPnL).average();
        if (result.isPresent()) {
            average = result.getAsDouble();
        }

        return Format.roundTwoDigits(average);
    }

}
