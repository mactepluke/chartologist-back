package com.syngleton.chartomancy.service.patterns;

import com.syngleton.chartomancy.model.patterns.Pattern;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import com.syngleton.chartomancy.service.data.DataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class PatternService {

    private final PatternFactory patternFactory;
    private final DataService dataService;

    @Autowired
    public PatternService(PatternFactory patternFactory,
                          DataService dataService) {
        this.patternFactory = patternFactory;
        this.dataService = dataService;
    }

    public void create() {


        PatternParams.Builder builder = new PatternParams.Builder();

        log.debug("******************************* {}",dataService.getGraph().toString());
        PatternParams patternParams = builder
                .patternType(PatternTypes.BASIC)
                .granularity(100)
                .length(1)
                .graph(dataService.getGraph())
                .build();

        log.debug("----------------> {}", patternParams.toString());

        List<Pattern> patterns = patternFactory.create(patternParams);
    }
}
