package co.syngleton.chartomancer.charting;

abstract class HistoricalDataCsvReader implements HistoricalDataDAO {
    protected static final int READING_ATTEMPTS = 3;
    protected static final String NEW_LINE = System.lineSeparator();
    
    protected HistoricalDataCsvReader() {
    }
}
