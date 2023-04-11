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

    public static String cutString(String string, int length) {
        if (string == null || string.isEmpty() || string.equals("null") || string.equals("undefined")) {
            string = "";
        }

        if (string.length() > length) {
            string = string.substring(0, length);
        }
        return string;
    }

    public static int streamlineInt(int number, int min, int max)    {

        if (number < min)   {
            number = min;
        }

        if (number > max)   {
            number = max;
        }
        return number;
    }

    public static int setIntIfZero(int value, int newValue)  {
        if (value == 0) {
            value = newValue;
        }
        return value;
    }

}
