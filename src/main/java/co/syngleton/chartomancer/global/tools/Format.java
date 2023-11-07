package co.syngleton.chartomancer.global.tools;

import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Math.round;

@Log4j2
public final class Format {

    private Format() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static float roundTwoDigits(float number) {
        return (round(number * 100) / 100f);
    }

    public static double roundTwoDigits(double number) {
        return (round(number * 100) / 100d);
    }

    public static float roundAccordingly(float number) {

        return roundAccordingly(number, number);
    }

    public static float roundAccordingly(float number, float reference) {

        if (reference > 1000) {
            return roundNDigits(number, 0);
        }

        if (reference > 100) {
            return roundNDigits(number, 1);
        }

        if (reference > 10) {
            return roundNDigits(number, 2);
        }

        if (reference > 1) {
            return roundNDigits(number, 3);
        }

        if (reference > 0.1) {
            return roundNDigits(number, 4);
        }

        if (reference > 0.01) {
            return roundNDigits(number, 5);
        }

        if (reference > 0.001) {
            return roundNDigits(number, 6);
        }

        if (reference > 0.0001) {
            return roundNDigits(number, 7);
        }

        return roundNDigits(number, 5);
    }

    public static float roundNDigits(float number, int decimals) {
        if (decimals <= 0) {
            return (float) round(number);
        }
        if (decimals > 8) {
            decimals = 8;
        }
        double op = Math.pow(10, decimals);
        return (float) (round(number * op) / op);
    }

    public static double roundAccordingly(double number) {

        return roundAccordingly(number, number);
    }

    public static double roundAccordingly(double number, double reference) {

        if (reference > 1000) {
            return roundNDigits(number, 0);
        }

        if (reference > 100) {
            return roundNDigits(number, 1);
        }

        if (reference > 10) {
            return roundNDigits(number, 2);
        }

        if (reference > 1) {
            return roundNDigits(number, 3);
        }

        if (reference > 0.1) {
            return roundNDigits(number, 4);
        }

        if (reference > 0.01) {
            return roundNDigits(number, 5);
        }

        if (reference > 0.001) {
            return roundNDigits(number, 6);
        }

        if (reference > 0.0001) {
            return roundNDigits(number, 7);
        }

        return roundNDigits(number, 5);
    }

    public static double roundNDigits(double number, int decimals) {
        if (decimals <= 0) {
            return (double) round(number);
        }
        if (decimals > 8) {
            decimals = 8;
        }
        double op = Math.pow(10, decimals);
        return (round(number * op) / op);
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

    public static int streamline(int number, int min, int max) {

        if (number < min) {
            number = min;
        }

        if (number > max) {
            number = max;
        }
        return number;
    }

    public static float streamline(float number, float min, float max) {

        if (number < min) {
            number = min;
        }

        if (number > max) {
            number = max;
        }
        return number;
    }

    public static double streamline(double number, double min, double max) {

        if (number < min) {
            number = min;
        }

        if (number > max) {
            number = max;
        }
        return number;
    }

    public static int setIfZero(int value, int newValue) {
        if (value == 0) {
            value = newValue;
        }
        return value;
    }

    public static String trimToMax(String string, int maxLength) {

        if (string == null || string.isEmpty() || string.equals("null") || string.equals("undefined")) {
            string = "";
        }

        if (string.length() > maxLength) {
            string = string.substring(0, maxLength);
        }
        return string;
    }

    public static String toFrenchDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public static String toFileNameCompatibleDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss"));
    }
}
