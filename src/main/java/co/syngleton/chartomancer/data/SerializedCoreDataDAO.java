package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.CoreDataSnapshot;
import co.syngleton.chartomancer.core_entities.DefaultCoreData;
import org.springframework.stereotype.Component;

import java.io.*;

@Component("serialized")
final class SerializedCoreDataDAO implements CoreDataDAO {

    @Override
    public CoreData loadCoreDataFrom(String dataFilePath) {

        CoreDataSnapshot readData;

        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(dataFilePath))) {
            readData = (CoreDataSnapshot) is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new DefaultCoreData(readData);
    }

    @Override
    public boolean saveCoreDataTo(CoreData coreData, String dataFilePath) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(dataFilePath))) {
            os.writeObject(coreData.getSnapshot());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
