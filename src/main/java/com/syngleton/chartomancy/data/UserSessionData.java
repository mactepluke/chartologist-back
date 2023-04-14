package com.syngleton.chartomancy.data;

import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.model.Pattern;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserSessionData {

    private Graph graph = null;
    private List<Pattern> patterns = new ArrayList<>();
}
