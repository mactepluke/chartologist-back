package com.syngleton.chartomancy.util;

import lombok.extern.log4j.Log4j2;
import java.util.Collection;
import java.util.Map;

@Log4j2
public final class Check {

    private Check() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static <T> boolean notNullNotEmpty(Collection<T> collection)    {
        return collection != null && !collection.isEmpty();
    }

    public static <K, V> boolean notNullNotEmpty(Map<K, V> collection)    {
        return collection != null && !collection.isEmpty();
    }

}