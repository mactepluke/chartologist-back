package com.syngleton.chartomancy.util;


import com.syngleton.chartomancy.model.charting.ChartObject;
import com.syngleton.chartomancy.model.charting.PatternBox;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Set;

@Log4j2
public final class Check {

    private Check() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static <T> boolean notNullNotEmpty(Collection<T> collection)    {
        return collection != null && !collection.isEmpty();
    }

    public static <T> boolean matchesAnyChartObjectIn(ChartObject chartObject, Collection<T> chartObjectCollection) {
        if (notNullNotEmpty(chartObjectCollection)) {
            for (T collectionObject : chartObjectCollection) {
                if ((chartObject.matches((ChartObject) collectionObject))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> T getFirstMatchingChartObjectIn(ChartObject chartObject, Collection<T> chartObjectCollection) {
        if (notNullNotEmpty(chartObjectCollection)) {
            for (T collectionObject : chartObjectCollection) {
                if ((chartObject.matches((ChartObject) collectionObject))) {
                    return collectionObject;
                }
            }
        }
        return null;
    }

}