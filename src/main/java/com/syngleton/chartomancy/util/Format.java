package com.syngleton.chartomancy.util;

import static java.lang.Math.round;

public final class Format {

    private Format() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static Float roundFloat(float number) {
        return (float) (round( ((double) number * 100) / 100));
    }

    public static Float roundFloat(float number, int decimals)  {
        if (decimals <= 0)   {
            decimals = 1;
        }
        if (decimals > 6)   {
            decimals = 6;
        }
        double op = Math.pow(10, decimals);
        return (float) (round(number * op) / op);
    }



}
