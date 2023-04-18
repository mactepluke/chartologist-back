package com.syngleton.chartomancy.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class MiscUtils {

    private static final ModelMapper mapper = new ModelMapper();

    private MiscUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static ModelMapper getMapper() {
        return mapper;
    }

    static {
        // full matching of names in classes
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
}