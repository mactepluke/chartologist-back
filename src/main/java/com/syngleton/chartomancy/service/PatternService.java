package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.model.patterns.BasicPatternFactory;
import com.syngleton.chartomancy.model.patterns.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class PatternService {
    public void create() {

        BasicPatternFactory basicPatternFactory = new BasicPatternFactory();
        Pattern pattern = basicPatternFactory.create();
    }
}
