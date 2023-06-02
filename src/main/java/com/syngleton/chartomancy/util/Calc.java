package com.syngleton.chartomancy.util;

import static java.lang.Math.round;

public final class Calc {

    private Calc() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static int positivePercentage(int part, int total) {

        int number = round(((float) part / total) * 100);

        if (number < 0) {
            number = 0;
        }
        if (number > 100) {
            number = 100;
        }
        return number;
    }

    public static int positivePercentage(double part, double total) {

        int number = (int) round(((float) part / total) * 100);

        if (number < 0) {
            number = 0;
        }
        if (number > 100) {
            number = 100;
        }
        return number;
    }


    public static int relativePercentage(int part, int total) {

        if (part == 0) {
            return 0;
        }

        if (part == total) {
            return 100;
        }

        int number = round(((float) part / total) * 100);

        if (number < -100) {
            number = -100;
        }
        if (number > 100) {
            number = 100;
        }
        return number;
    }

    public static int relativePercentage(double part, double total) {

        if (part == 0) {
            return 0;
        }

        if (part == total) {
            return 100;
        }

        int number = (int) round(((float) part / total) * 100);

        if (number < -100) {
            number = -100;
        }
        if (number > 100) {
            number = 100;
        }
        return number;
    }

    public static int variationPercentage(int start, int end) {
        float result = 0;

        if ((start != 0) && (start != end)) {
            result = ((end - start) * 100) / (float) start;
        }
        return (int) result;
    }

    public static float variationPercentage(float start, float end) {
        float result = 0;

        if ((start != 0) && (start != end)) {
            result = ((end - start) * 100) / start;
        }
        return result;
    }

    public static double variationPercentage(double start, double end) {
        double result = 0;

        if ((start != 0) && (start != end)) {
            result = ((end - start) * 100) / start;
        }
        return result;
    }
}
