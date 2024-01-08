package co.syngleton.chartomancer.data.mongo_dto;

import co.syngleton.chartomancer.data.DataSettings;
import co.syngleton.chartomancer.model.PatternBox;
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
public class PatternBoxesMongoDTO {
    @Id
    private String id;
    private Set<PatternBox> patternBoxes;
    private DataSettings patternSettings;
}
