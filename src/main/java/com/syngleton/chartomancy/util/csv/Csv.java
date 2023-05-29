package com.syngleton.chartomancy.util.csv;

public final class Csv {

    private Csv() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void writeCsvFile(String filePath, CsvExportable content)   {
        writeToFile(filePath, generateCsv(content));
    }

    public static String generateCsv(CsvExportable content)  {
        //TODO Implement this method

        return "";
    }

    private static void writeToFile(String filePath, String content)  {
        //TODO Implement this method
    }
}
