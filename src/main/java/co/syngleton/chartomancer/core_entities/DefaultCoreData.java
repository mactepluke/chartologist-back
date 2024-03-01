package co.syngleton.chartomancer.core_entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultCoreData extends AbstractCoreData {

    private DefaultCoreData() {
        super();
    }

    private DefaultCoreData(CoreDataSnapshot coreDataSnapshot) {
        super(coreDataSnapshot);
    }

    private DefaultCoreData(CoreData coreData) {
        super(coreData);
    }

    /**
     * This factory method is used instead of a constructor so the class cannot be extended outside its package,
     * while still being extensible within it.
     *
     * @return a new instance of the class
     */
    public static DefaultCoreData newInstance() {
        return new DefaultCoreData();
    }

    public static DefaultCoreData valueOf(CoreDataSnapshot coreDataSnapshot) {
        return new DefaultCoreData(coreDataSnapshot);
    }

    public static DefaultCoreData copyOf(CoreData coreData) {
        return new DefaultCoreData(coreData);
    }

    @Override
    public synchronized void mirror(@NonNull CoreData coreData) {
        DefaultCoreData defaultCoreData = (DefaultCoreData) coreData;
        this.graphs = defaultCoreData.graphs;
        this.patternBoxes = defaultCoreData.patternBoxes;
        this.tradingPatternBoxes = defaultCoreData.tradingPatternBoxes;
        this.patternSettings.putAll(defaultCoreData.patternSettings);
        this.tradingPatternSettings.putAll(defaultCoreData.tradingPatternSettings);
    }
}
