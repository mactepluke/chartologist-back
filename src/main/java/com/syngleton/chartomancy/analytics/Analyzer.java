package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import com.syngleton.chartomancy.model.charting.patterns.*;
import com.syngleton.chartomancy.util.Calc;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Math.*;

@Log4j2
@Component
public class Analyzer {

    public float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope) {

        if (floatFollowingCandles.size() >= scope) {
            FloatCandle firstCandle = floatFollowingCandles.get(0);
            FloatCandle predictionCandle = floatFollowingCandles.get(scope - 1);

            return Calc.variationPercentage(firstCandle.open(), predictionCandle.close());

        } else {
            log.error("Unable to calculate price variation prediction : target candles size {} in smaller than scope {}",
                    floatFollowingCandles.size(),
                    scope);
            return 0;
        }
    }

    public int calculateMatchScore(PixelatedPattern pattern, List<PixelatedCandle> pixelatedCandlesToMatch) {

        int length = pattern.getLength();
        int granularity = pattern.getGranularity();
        int inkedPixels = 0;
        int matchScore = 0;

        if (length != pixelatedCandlesToMatch.size()) {
            log.error("Pattern size does not match graph sample size.");
            return 0;
        }

        for (var i = 0; i < length; i++) {

            for (int j = 0; j < granularity; j++) {

                byte patternPixel = pattern.getPixelatedCandles().get(i).candle()[j];

                if (patternPixel != 0) {
                    inkedPixels++;

                    byte pixelatedCandlesToMatchPixel = pixelatedCandlesToMatch.get(i).candle()[j];

                    if (pixelatedCandlesToMatchPixel != 0) {
                        matchScore++;
                    }
                }
            }
        }
        return Calc.positivePercentage(matchScore, inkedPixels);
    }

    public int calculateMatchScoreWithExponentialSmoothing(IntPattern pattern, List<IntCandle> intCandlesToMatch) {

        return calculateMatchScoreWithExponentialSmoothing(pattern.getIntCandles(), intCandlesToMatch);
    }

    public int calculateMatchScoreWithExponentialSmoothing(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch) {

        int length = intCandles.size();
        int matchScore = 0;
        int patternCandlesSurface = 0;

        if (length != intCandlesToMatch.size()) {
            log.error("Pattern size does not match graph sample size.");
            return 0;
        }

        for (var i = 0; i < length; i++) {

            patternCandlesSurface = patternCandlesSurface + (1 + i) * (
                    abs(intCandles.get(i).open() - intCandles.get(i).close())
                    + (intCandles.get(i).open() > intCandles.get(i).close() ?
                    intCandles.get(i).close() - intCandles.get(i).low() + intCandles.get(i).high() - intCandles.get(i).open()
                    : intCandles.get(i).open() - intCandles.get(i).low() + intCandles.get(i).high() - intCandles.get(i).close()));

            if ((intCandles.get(i).open() < intCandles.get(i).close()
                    && intCandlesToMatch.get(i).open() < intCandlesToMatch.get(i).close())
                    || (intCandles.get(i).open() > intCandles.get(i).close()
                    && intCandlesToMatch.get(i).open() > intCandlesToMatch.get(i).close())) {

                int bodyOverlap;
                int wickOverlap;

                if (intCandles.get(i).open() > intCandles.get(i).close()) {
                    wickOverlap = overlapAmount(
                            intCandles.get(i).high(),
                            intCandles.get(i).open(),
                            intCandlesToMatch.get(i).high(),
                            intCandlesToMatch.get(i).open()) * (1 + i)
                            + overlapAmount(
                            intCandles.get(i).close(),
                            intCandles.get(i).low(),
                            intCandlesToMatch.get(i).close(),
                            intCandlesToMatch.get(i).low()) * (1 + i);
                } else {
                    wickOverlap = overlapAmount(
                            intCandles.get(i).low(),
                            intCandles.get(i).open(),
                            intCandlesToMatch.get(i).low(),
                            intCandlesToMatch.get(i).open()) * (1 + i)
                            + overlapAmount(
                            intCandles.get(i).close(),
                            intCandles.get(i).high(),
                            intCandlesToMatch.get(i).close(),
                            intCandlesToMatch.get(i).high() * (1 + i)
                    );
                }
                bodyOverlap = overlapAmount(
                        intCandles.get(i).open(),
                        intCandles.get(i).close(),
                        intCandlesToMatch.get(i).open(),
                        intCandlesToMatch.get(i).close()) * (1 + i);

                matchScore = matchScore + bodyOverlap + wickOverlap;
            }
        }
        //TODO Refactor the threshold function to apply an exponential result
        int result = Calc.positivePercentage(matchScore, patternCandlesSurface);
        if (result < 30)    {
            result = 0;
        }
        return result;
    }

    private int applyThreshold(int result)  {
        return 0;
    }

    public int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch) {

        int length = intCandles.size();
        int matchScore = 0;
        int patternCandlesSurface = 0;

        if (length != intCandlesToMatch.size()) {
            log.error("Pattern size does not match graph sample size.");
            return 0;
        }

        for (var i = 0; i < length; i++) {

            patternCandlesSurface = patternCandlesSurface +
                    abs(intCandles.get(i).open() - intCandles.get(i).close())
                    + (intCandles.get(i).open() > intCandles.get(i).close() ?
                    intCandles.get(i).close() - intCandles.get(i).low() + intCandles.get(i).high() - intCandles.get(i).open()
                    : intCandles.get(i).open() - intCandles.get(i).low() + intCandles.get(i).high() - intCandles.get(i).close());

            if ((intCandles.get(i).open() < intCandles.get(i).close()
                    && intCandlesToMatch.get(i).open() < intCandlesToMatch.get(i).close())
                    || (intCandles.get(i).open() > intCandles.get(i).close()
                    && intCandlesToMatch.get(i).open() > intCandlesToMatch.get(i).close())) {

                int bodyOverlap;
                int wickOverlap;

                if (intCandles.get(i).open() > intCandles.get(i).close()) {
                    wickOverlap = overlapAmount(
                            intCandles.get(i).high(),
                            intCandles.get(i).open(),
                            intCandlesToMatch.get(i).high(),
                            intCandlesToMatch.get(i).open())
                            + overlapAmount(
                            intCandles.get(i).close(),
                            intCandles.get(i).low(),
                            intCandlesToMatch.get(i).close(),
                            intCandlesToMatch.get(i).low()
                    );
                } else {
                    wickOverlap = overlapAmount(
                            intCandles.get(i).low(),
                            intCandles.get(i).open(),
                            intCandlesToMatch.get(i).low(),
                            intCandlesToMatch.get(i).open())
                            + overlapAmount(
                            intCandles.get(i).close(),
                            intCandles.get(i).high(),
                            intCandlesToMatch.get(i).close(),
                            intCandlesToMatch.get(i).high()
                    );
                }
                bodyOverlap = overlapAmount(
                        intCandles.get(i).open(),
                        intCandles.get(i).close(),
                        intCandlesToMatch.get(i).open(),
                        intCandlesToMatch.get(i).close());

                matchScore = matchScore + bodyOverlap + wickOverlap;
            }
        }
        return Calc.positivePercentage(matchScore, patternCandlesSurface);
    }

    private int overlapAmount(int aStart, int aEnd, int bStart, int bEnd) {

        if (bStart > aEnd || aStart > bEnd) {
            return 0;
        } else {
            return min(aEnd, bEnd) - max(aStart, bStart);
        }
    }
}
