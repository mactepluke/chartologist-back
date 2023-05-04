package com.syngleton.chartomancy.util;

import lombok.extern.log4j.Log4j2;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    public static <T> boolean executeIfTrue(boolean condition, Predicate<T> function, T param)    {
        if (condition)  {
            return function.test(param);
        }
        return false;
    }

    public static <T> void executeIfTrue(boolean condition, Consumer<T> function, T param)    {
        if (condition)  {
            function.accept(param);
        }
    }
}
