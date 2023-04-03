package com.syngleton.chartomancy.service.patterns;

import com.syngleton.chartomancy.model.data.Graph;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import lombok.*;

//TODO Paramétrer cette classe avec mon lecteur de fichier .properties maison
@ToString
public final class PatternParams {

    @ToString.Exclude
    @Getter
    private final Graph graph;
    @Getter
    private final PatternTypes patternType;
    @Getter
    private final String name;
    @Getter
    private final int granularity;
    @Getter
    private final int length;

    private PatternParams(Builder builder) {
        this.graph = builder.graph;
        this.patternType = builder.patternType;
        this.name = builder.name;
        this.granularity = builder.granularity;
        this.length = builder.length;
    }

    public static class Builder {

        private Graph graph = null;
        private PatternTypes patternType = PatternTypes.BASIC;
        private String name = "";
        private int granularity;
        private int length;


        public Builder patternType(PatternTypes patternType) {
            this.patternType = patternType;
            return this;
        }

        public Builder graph(Graph graph) {
            this.graph = graph;
            return this;
        }

        public Builder name(String name) {
            if (!name.isEmpty()) {
                this.name = name;
            }
            return this;
        }

        public Builder granularity(int granularity) {

            this.granularity = granularity;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public PatternParams build() {
            return new PatternParams(this);
        }


    }
}
