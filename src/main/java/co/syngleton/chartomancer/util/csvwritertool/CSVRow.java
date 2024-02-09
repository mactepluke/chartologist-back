package co.syngleton.chartomancer.util.csvwritertool;

import java.io.Serializable;
import java.util.List;

public interface CSVRow {
    /**
     * MUST BE IMPLEMENTED
     * This method will list the values that will be converted into String elements of a row of the CSVData.
     * The object that implements CSVRow is therefore returning selected elements destined to populate one of
     * the table rows, in the given order.
     *
     * @return a list of serializable objects, typically class fields or results of getter methods.
     */
    List<Serializable> toRow();
}
