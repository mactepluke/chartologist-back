package co.syngleton.chartomancer.util.datatabletool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public final class DataTableTool {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String FAILED_INTEGRITY_CHECK_MESSAGE = "COULD NOT GENERATE DATA: table did not pass integrity check.";

    private DataTableTool() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * This method appends the data (omitting the header) to an existing file (same path, name and suffix);
     * if the file does not exist, it creates it and write both the header and the body;
     * if the file exists but has the wrong header, then it is left untouched.
     *
     * @param filePath
     * @param table
     */
    public static boolean writeDataTableToFile(String filePath, PrintableDataTable table) {

        BufferedReader reader = getReaderIfFileExists(filePath, table);

        if (reader == null) {
            createInNewFile(filePath, table);
            return true;
        }

        String line = readLineAndAppendNewLine(reader);

        if (line == null) {
            return false;
        }

        String header = generatePrintableHeader(table);

        if (line.equals(header)) {
            appendToExistingFile(filePath, table);
            return true;
        }

        return false;
    }

    private static BufferedReader getReaderIfFileExists(String filePath, PrintableDataTable table) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath + table.getFileSuffix()))) {
            return reader;

        } catch (IOException e) {
            return null;
        }
    }

    private static void createInNewFile(String filePath, PrintableDataTable table) {

        writeToFile(filePath + table.getFileSuffix(), generatePrintableHeaderAndData(table));
    }

    private static String readLineAndAppendNewLine(BufferedReader reader) {

        try {
            return reader.readLine() + NEW_LINE;
        } catch (IOException e) {
            return null;
        }
    }

    private static void appendToExistingFile(String filePath, PrintableDataTable table) {

        writeToFile(filePath + table.getFileSuffix(), generatePrintableData(table));
    }

    public static String generatePrintableHeaderAndData(PrintableDataTable table) {

        StringBuilder printableTableBuilder = new StringBuilder();

        if (PrintableDataTable.checkDataTableIntegrity(table)) {

            printableTableBuilder
                    .append(generatePrintableHeader(table))
                    .append(generatePrintableData(table));

            return printableTableBuilder.toString();
        }

        return FAILED_INTEGRITY_CHECK_MESSAGE;
    }

    private static String generatePrintableHeader(PrintableDataTable table) {

        return String.valueOf(generateNewSeparatedLine(table.getHeader(), table.getRowValuesSeparator()));
    }

    private static String generatePrintableData(PrintableDataTable table) {

        StringBuilder stringBuilder = new StringBuilder();

        List<PrintableData> printableData = table.getPrintableData();

        if (printableData != null) {
            printableData.forEach(
                    entry -> {
                        List<String> strings = stringifyListOfSerializables(entry.toRow());
                        stringBuilder.append(generateNewSeparatedLine(strings, table.getRowValuesSeparator()));
                    }
            );
        }

        return stringBuilder.toString();
    }

    private static List<String> stringifyListOfSerializables(List<Serializable> serializables) {

        return serializables.stream().map(Serializable::toString).toList();
    }

    private static StringBuilder generateNewSeparatedLine(List<String> element, char separator) {

        StringBuilder stringBuilder = new StringBuilder();

        element.forEach(value -> stringBuilder.append(value).append(separator));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append(NEW_LINE);

        return stringBuilder;
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
