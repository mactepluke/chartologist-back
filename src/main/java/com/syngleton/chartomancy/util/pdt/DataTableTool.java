package com.syngleton.chartomancy.util.pdt;

import java.io.*;
import java.util.List;

public final class DataTableTool {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private DataTableTool() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * This method appends the data (omitting the header) to an existing file (same path, name and suffix);
     * if the file does not exist, it creates it and write both the header and the body;
     * if the file exists but has the wrong header, then it is left untouched.
     * @param filePath
     * @param table
     */
    public static boolean writeDataTableToFile(String filePath, PrintableDataTable table) {

        try (
                BufferedReader reader = new BufferedReader(new FileReader(filePath + table.getFileSuffix()))) {
                String line = reader.readLine() + NEW_LINE;
                String header = generatePrintableHeader(table);

                if (line.equals(header))    {
                    writeToFile(filePath + table.getFileSuffix(), generatePrintableData(table));
                    return true;
                } else {
                    return false;
                }

            } catch (IOException e) {
            writeToFile(filePath + table.getFileSuffix(), generatePrintableHeaderAndData(table));
            return true;
        }
    }

    public static String generatePrintableHeaderAndData(PrintableDataTable table) {

        StringBuilder printableTableBuilder = new StringBuilder();

        if (PrintableDataTable.checkDataTableIntegrity(table)) {

            printableTableBuilder
                    .append(generatePrintableHeader(table))
                    .append(generatePrintableData(table));
        } else {
            printableTableBuilder.append("COULD NOT GENERATE DATA: table did not pass integrity check.");
        }

        return printableTableBuilder.toString();
    }

    private static String generatePrintableHeader(PrintableDataTable table)  {

        StringBuilder stringBuilder = new StringBuilder();

        table.getHeader().forEach(
                title -> stringBuilder.append(title).append(table.getRowValuesSeparator())
        );
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append(NEW_LINE);

            return stringBuilder.toString();
    }

    private static String generatePrintableData(PrintableDataTable table) {

        StringBuilder stringBuilder = new StringBuilder();

        List<PrintableData> printableData = table.getPrintableData();

        if (printableData != null) {

            printableData.forEach(
                    entry ->
                    {
                        entry.toRow().forEach(
                                value -> stringBuilder.append(value).append(table.getRowValuesSeparator())
                        );
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append(NEW_LINE);
                    }
            );
        }

        return stringBuilder.toString();
    }

    private static synchronized void writeToFile(String filePath, String content) {

        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
