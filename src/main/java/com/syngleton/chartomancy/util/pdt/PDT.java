package com.syngleton.chartomancy.util.pdt;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public final class PDT {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private PDT() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void writeDataTableToFile(String filePath, PrintableDataTable content) {
        writeToFile(filePath + content.getFileSuffix(), generateDataToPrint(content));
    }

    public static String generateDataToPrint(PrintableDataTable table) {

        StringBuilder printableTableBuilder = new StringBuilder();

        if (PrintableDataTable.checkDataTableIntegrity(table)) {

            char separator = table.getRowValuesSeparator();

            table.getHeader().forEach(
                    title -> printableTableBuilder.append(title).append(separator)
            );
            printableTableBuilder.deleteCharAt(printableTableBuilder.length() - 1).append(NEW_LINE);

            List<PrintableData> printableData = table.getPrintableData();

            if (printableData != null) {

                printableData.forEach(
                        entry ->
                        {
                            entry.toRow().forEach(
                                    value -> printableTableBuilder.append(value).append(separator)
                            );
                            printableTableBuilder.deleteCharAt(printableTableBuilder.length() - 1).append(NEW_LINE);
                        }
                );
            }
        } else {
            printableTableBuilder.append("COULD NOT GENERATE DATA: table did not pass integrity check.");
        }

        return printableTableBuilder.toString();
    }

    private static void writeToFile(String filePath, String content) {

        try (FileWriter writer = new FileWriter(filePath)) {
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.print(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
