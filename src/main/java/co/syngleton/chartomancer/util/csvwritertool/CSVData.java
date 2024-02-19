package co.syngleton.chartomancer.util.csvwritertool;

import java.util.List;
import java.util.Objects;

public interface CSVData {

    /**
     * This method checks if the PrintableDataTable is structure in a way that allows it to be exploited.
     * It is used by the Data Table Tool class.
     *
     * @param table is the PrintableDataTable to check for integrity.
     * @return true if the table can be used to generate data, or false if it is empty or broken.
     */
    static void checkIntegrity(CSVData table) {

        Objects.requireNonNull(table);

        final boolean separatorIsNotSet = table.getSeparator() == '\u0000';
        final boolean csvDataIsNotSet = table.getCSVData() == null;
        final boolean headerIsNotSet = table.getHeader() == null;
        final boolean headerIsEmpty = table.getHeader().isEmpty();
        final boolean csvDataIsEmpty = table.getCSVData().isEmpty();
        final boolean csvDataSizeIsNotEqualToHeaderSize = !csvDataIsEmpty && table.getCSVData().get(0).toRow().size() != table.getHeader().size();

        if (separatorIsNotSet
                || csvDataIsNotSet
                || headerIsNotSet
                || headerIsEmpty
                || csvDataIsEmpty
                || csvDataSizeIsNotEqualToHeaderSize) {
            throw new IllegalArgumentException(
                    "The table is not set properly:" + System.lineSeparator()
                            + "Separator is not set: " + separatorIsNotSet + System.lineSeparator()
                            + "CSV data is not set: " + csvDataIsNotSet + System.lineSeparator()
                            + "Header is not set: " + headerIsNotSet + System.lineSeparator()
                            + "Header is empty: " + headerIsEmpty + System.lineSeparator()
                            + "CSV data is empty: " + csvDataIsEmpty + System.lineSeparator()
                            + "CSV data size is not equal to header size: " + csvDataSizeIsNotEqualToHeaderSize + System.lineSeparator()
                            + "Separator: " + table.getSeparator() + System.lineSeparator()
                            + "Header: " + table.getHeader() + System.lineSeparator()
                            + "CSV data: " + table.getCSVData() + System.lineSeparator()
            );
        }


    }

    /**
     * MUST BE IMPLEMENTED
     * This method is used to generate the PrintableDataTableHeader, therefore the size of the list should match the size
     * of each CSVRow "toRow()" return size.
     *
     * @return the list of names of the columns that will populate the PrintableDataTable.
     */
    List<String> getHeader();

    /**
     * MUST BE IMPLEMENTED
     * This method is used get the list of the rows of the table, which is a list of objects that implement
     * the 'PrintableData' interface that works with this interface. These objects are the 'row entries'.
     *
     * @return a list of rows of 'CSVRow' type, which are a list of serializable objects/
     */
    List<CSVRow> getCSVData();

    /**
     * This method is used to separate the elements of each value of a given row in the generated PrintableDataTable.
     * As CSV is a most common format, this method overriding is optional and the default value returned will be the comma ','.
     *
     * @return the value of the separator.
     */
    default char getSeparator() {
        return ',';
    }

    /**
     * This method is used when a file is created with the help of the PDT class method 'writeToFile(String filePath, String content)'.
     * By default, it appends ".csv" to the name of the file, but it should be overriden if the above methode 'getSeparator()'
     * is overriden as well to change its separator, so the suffix matches the type of generated data if necessary.
     *
     * @return the value of the file suffix to append at the end of the file name.
     */
    default String getSuffix() {
        return ".csv";
    }

}
