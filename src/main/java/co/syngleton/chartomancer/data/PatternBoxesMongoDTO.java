package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.domain.PatternBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "core_data")
class PatternBoxesMongoDTO {
    @Id
    private String id;
    private Set<PatternBox> patternBoxes;
    private Map<String, String> patternSettings;
}
