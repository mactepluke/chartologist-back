package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.core_entities.Account;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.csvwritertool.CSVData;
import co.syngleton.chartomancer.util.csvwritertool.CSVRow;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.list.UnmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

import static java.lang.Math.abs;

public class TradingAccount implements CSVData, Account {

    private static final String DEFAULT_CURRENCY = "$";
    private static final String NEW_LINE = System.lineSeparator();
    private static final String SEP = " | ";

    private final List<Trade> trades;

    @Getter
    private final String currency;
    @Getter
    @Setter
    boolean liquidated;
    @Getter
    private double balance;
    @Getter
    @Setter
    private boolean enabled;
    @Getter
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

    public double getAverageFeePercentage() {
        return Format.roundTwoDigits(trades.stream().mapToDouble(Trade::getFeePercentage).average().orElse(0));
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public List<Trade> exportTrades() {
        return new UnmodifiableList<>(trades);
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
    public List<CSVRow> getCSVData() {
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
        return Format.roundTwoDigits(trades.stream().filter(Trade::isSideLong).mapToDouble(Trade::getPnL).sum());
    }

    public double getShortPnl() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> !trade.isSideLong()).mapToDouble(Trade::getPnL).sum());
    }

    public double getTotalReturnPercentage() {
        return Format.roundTwoDigits(trades.stream().mapToDouble(Trade::getReturnPercentage).sum());
    }

    public double getLongReturnPercentage() {
        return Format.roundTwoDigits(trades.stream().filter(Trade::isSideLong).mapToDouble(Trade::getReturnPercentage).sum());
    }

    public double getShortReturnPercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> !trade.isSideLong()).mapToDouble(Trade::getReturnPercentage).sum());
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
        return trades.stream().filter(Trade::isSideLong).count();
    }

    public long getNumberOfShorts() {
        return trades.stream().filter(trade -> !trade.isSideLong()).count();
    }

    public double getTotalBattingAveragePercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> trade.getPnL() > 0).count() / (double) getNumberOfTrades()) * 100;
    }

    public double getLongBattingAveragePercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> trade.getPnL() > 0 && trade.isSideLong()).count() / (double) getNumberOfLongs()) * 100;
    }

    public double getShortBattingAveragePercentage() {
        return Format.roundTwoDigits(trades.stream().filter(trade -> trade.getPnL() > 0 && !trade.isSideLong()).count() / (double) getNumberOfShorts()) * 100;
    }

    public double getTotalWinToLossRatio() {
        return Format.roundTwoDigits((double) trades.stream().filter(trade -> trade.getPnL() > 0).count()
                / trades.stream().filter(trade -> trade.getPnL() < 0).count());
    }

    public double getLongWinToLossRatio() {
        return Format.roundTwoDigits((double) trades.stream().filter(trade -> trade.getPnL() > 0 && trade.isSideLong()).count()
                / trades.stream().filter(trade -> trade.getPnL() < 0 && trade.isSideLong()).count());
    }

    public double getShortWinToLossRatio() {
        return Format.roundTwoDigits((double) trades.stream().filter(trade -> trade.getPnL() > 0 && !trade.isSideLong()).count()
                / trades.stream().filter(trade -> trade.getPnL() < 0 && !trade.isSideLong()).count());
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

        OptionalDouble result = trades.stream().filter(Trade::isSideLong).mapToDouble(Trade::getPnL).average();
        if (result.isPresent()) {
            average = result.getAsDouble();
        }

        return Format.roundTwoDigits(average);
    }

    public double getShortAveragePnL() {

        double average = 0;

        OptionalDouble result = trades.stream().filter(trade -> !trade.isSideLong()).mapToDouble(Trade::getPnL).average();
        if (result.isPresent()) {
            average = result.getAsDouble();
        }

        return Format.roundTwoDigits(average);
    }

}
