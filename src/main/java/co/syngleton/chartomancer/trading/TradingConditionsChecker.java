package co.syngleton.chartomancer.trading;

import lombok.Builder;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.*;


@Log4j2
@ToString
@Builder
public final class TradingConditionsChecker implements Serializable {
    private final int maxTrades;
    private final int maxBlankTrades;
    private final double minimumAccountBalance;
    private final double maximumAccountBalance;

    public ConditionsChain checkIfConditions() {
        return new ConditionsChain();
    }

    enum Option {
        LOG_DENIAL_REASON,
    }

    public final class ConditionsChain {
        private final Set<Boolean> additionalConditions;
        private boolean accountIsLiquidated;
        private boolean maxNumberOfTradesReached;
        private boolean maxNumberOfBlankTradesReached;
        private boolean accountBalanceBelowMinimum;
        private boolean maximumAccountBalanceExceeded;
        private boolean endOfGraphReached;

        private ConditionsChain() {
            this.additionalConditions = new HashSet<>();
        }

        ConditionsChain whenAppliedTo(TradingAccount account, int blankTradeCount) {

            Objects.requireNonNull(account, "Trading account cannot be null.");

            this.accountIsLiquidated = account.isLiquidated();
            this.maxNumberOfTradesReached = maxTrades != 0 && account.getNumberOfTrades() >= maxTrades;
            this.maxNumberOfBlankTradesReached = maxBlankTrades != 0 && blankTradeCount >= maxBlankTrades;
            this.accountBalanceBelowMinimum = minimumAccountBalance != 0 && account.getBalance() < minimumAccountBalance;
            this.maximumAccountBalanceExceeded = maximumAccountBalance != 0 && account.getBalance() > maximumAccountBalance;

            return this;
        }

        ConditionsChain withNextCandleAndLimit(int nextOpenCandle, int openCandleLimit) {
            this.endOfGraphReached = nextOpenCandle >= openCandleLimit;

            return this;
        }

        ConditionsChain andIf(boolean condition) {
            additionalConditions.add(condition);
            return this;
        }

        private void logDenialReason() {

            String denialReason = "Reason for last condition not met: ";

            if (endOfGraphReached) {
                log.info(denialReason + "end of graph reached.");
                return;
            }
            if (accountIsLiquidated) {
                log.info(denialReason + "account is liquidated.");
                return;
            }
            if (maxNumberOfTradesReached) {
                log.info(denialReason + "maximum number of trades reached, (maxTrades=" + maxTrades + ")");
                return;
            }
            if (maxNumberOfBlankTradesReached) {
                log.info(denialReason + "maximum number of blank trades reached, (maxBlankTrades=" + maxBlankTrades + ")");
                return;
            }
            if (accountBalanceBelowMinimum) {
                log.info(denialReason + "account balance below minimum, (minimumAccountBalance=" + minimumAccountBalance + ")");
                return;
            }
            if (maximumAccountBalanceExceeded) {
                log.info(denialReason + "maximum account balance exceeded, (maximumAccountBalance=" + maximumAccountBalance + ")");
            }
        }

        boolean doAllowToContinue(Option... options) {

            manageOptions(options);

            return evaluateContinueCondition();
        }

        private void manageOptions(Option... options) {

            Set<Option> optionsSet = EnumSet.noneOf(Option.class);
            Collections.addAll(optionsSet, options);

            for (Option option : optionsSet) {
                if (option == Option.LOG_DENIAL_REASON) {
                    logDenialReason();
                }
            }
        }

        private boolean evaluateContinueCondition() {

            return !accountIsLiquidated
                    && !maxNumberOfTradesReached
                    && !maxNumberOfBlankTradesReached
                    && !accountBalanceBelowMinimum
                    && !maximumAccountBalanceExceeded
                    && !endOfGraphReached
                    && allAdditionalConditionsAreTrue();
        }

        private boolean allAdditionalConditionsAreTrue() {
            for (boolean value : this.additionalConditions) {
                if (!value) {
                    return false;
                }
            }
            return true;
        }
    }

}
