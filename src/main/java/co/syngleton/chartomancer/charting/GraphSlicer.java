package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.core_entities.Graph;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface GraphSlicer {

    Graph getSlice(Graph graph, LocalDate startDate, LocalDate endDate);

    Graph getSlice(Graph graph, LocalDateTime startDate, LocalDateTime endDate);
}
