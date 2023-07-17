package co.syngleton.chartomancer.util.datatabletool;

import java.io.Serializable;
import java.util.List;

public interface PrintableData {
    /**
     * MUST BE IMPLEMENTED
     * This method will list the values that will be converted into String elements of a row of the PrintableDataTable.
     * The object that implements PrintableData is therefore returning selected elements destined to populate one of
     * the table rows, in the given order.
     * @return a list of serializable objects, typically class fields or other results of getter methods.
     */
    List<Serializable> toRow();
}
