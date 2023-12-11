package co.syngleton.chartomancer.analytics.dao.mongo_dto;

import co.syngleton.chartomancer.analytics.model.Graph;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "core_data")
public class GraphsMongoDTO {
    @Id
    private String id;
    private Set<Graph> graphs;
}
