package com.syngleton.chartomancy.util.datatabletool;

import java.util.List;

public interface PrintableDataTable {

    /**
     * This method checks if the PrintableDataTable is structure in a way that allows it to be exploited.
     * It is used by the PDT class, but can also be used any time checks must be performed, to throw an exception, etc.
     * If not overriden
     *
     * @param table is the PrintableDataTable to check for integrity.
     * @return true if the table can be used to generate data, or false if it is empty or broken.
     */
    static boolean checkDataTableIntegrity(PrintableDataTable table) {

        return table != null
                && table.getRowValuesSeparator() != '\u0000'
                && table.getPrintableData() != null
                && table.getHeader() != null
                && !table.getHeader().isEmpty()
                && !table.getPrintableData().isEmpty()
                && table.getPrintableData().get(0).toRow().size() == table.getHeader().size();
    }

    /**
     * MUST BE IMPLEMENTED
     * This method is used to generate the PrintableDataTableHeader, therefore the size of the list should match the size
     * of each PrintableData "toRow()" return size.
     *
     * @return the list of names of the columns that will populate the PrintableDataTable.
     */
    List<String> getHeader();

    /**
     * MUST BE IMPLEMENTED
     * This method is used get the list of the rows of the table, which is a list of objects that implement
     * the 'PrintableData' interface that works with this interface. These objects are the 'row entries'.
     *
     * @return a list of rows of 'PrintableData' type, which are a list of serializable objects/
     */
    List<PrintableData> getPrintableData();

    /**
     * This method is used to separate the elements of each value of a given row in the generated PrintableDataTable.
     * As CSV is a most common format, this method overriding is optional and the default value returned will be the comma ','.
     *
     * @return the value of the separator.
     */
    default char getRowValuesSeparator() {
        return ',';
    }

    /**
     * This method is used when a file is created with the help of the PDT class method 'writeToFile(String filePath, String content)'.
     * By default, it appends ".csv" to the name of the file, but it should be overriden if the above methode 'getRowValuesSeparator()'
     * is overriden as well to change its separator, so the suffix matches the type of generated data if necessary.
     *
     * @return the value of the file suffix to append at the end of the file name.
     */
    default String getFileSuffix() {
        return ".csv";
    }
}
