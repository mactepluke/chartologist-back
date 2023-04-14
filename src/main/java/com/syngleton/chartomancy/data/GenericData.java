package com.syngleton.chartomancy.data;

import com.syngleton.chartomancy.model.dataloading.Graph;
import com.syngleton.chartomancy.model.patterns.Pattern;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GenericData {

    private Graph graph = null;
    private List<Pattern> patterns = new ArrayList<>();

}
