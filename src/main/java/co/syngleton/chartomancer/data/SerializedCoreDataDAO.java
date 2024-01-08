package co.syngleton.chartomancer.data.dao;

import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.data.CoreDataDAO;
import co.syngleton.chartomancer.data.DefaultCoreData;
import org.springframework.stereotype.Component;

import java.io.*;

@Component("serialized")
public class SerializedCoreDataDAO implements CoreDataDAO {

    public static final String DATA_SOURCE_PATH = "./core_data/";

    @Override
    public CoreData loadCoreDataFrom(String dataSourceName) {

        CoreData readData = new DefaultCoreData();

        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(DATA_SOURCE_PATH + dataSourceName))) {
            readData = (CoreData) is.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return readData;
    }

    @Override
    public boolean saveCoreDataTo(CoreData coreData, String dataSourceName) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(DATA_SOURCE_PATH + dataSourceName))) {
            os.writeObject(coreData);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}