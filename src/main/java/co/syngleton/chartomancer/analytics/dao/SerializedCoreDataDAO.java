package co.syngleton.chartomancer.analytics.dao;

import co.syngleton.chartomancer.analytics.data.CoreData;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Component("serialized")
public class SerializedCoreDataDAO implements CoreDataDAO {

    @Override
    public CoreData loadCoreDataWithName(String dataSourceName) {

        CoreData readData = new CoreData();

        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(dataSourceName))) {
            readData = (CoreData) is.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return readData;
    }

    @Override
    public boolean saveCoreDataWithName(CoreData coreData, String dataSourceName) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(dataSourceName))) {
            os.writeObject(coreData);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
