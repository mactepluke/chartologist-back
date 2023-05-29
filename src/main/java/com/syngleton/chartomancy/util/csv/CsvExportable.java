package com.syngleton.chartomancy.util.csv;

import java.util.List;

public interface CsvExportable {

    public String getCsvHeader();

    public List<CsvRow> getCsvRows();
}
