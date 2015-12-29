package mmb.ware.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.ware.cargo.model.CargoInfoStorage;

public interface CargoInfoStorageDao {

    int insertCargoInfoStorage(CargoInfoStorage record);

    CargoInfoStorage selectCargoInfoStorage(String condition);

    CargoInfoStorage selectCargoInfoStorageSlave(String condition);
    
    List<CargoInfoStorage> selectCargoInfoStorageList(Map<String,String> map);
 
    List<CargoInfoStorage> selectCargoInfoStorageListSlave(Map<String,String> map);
    
}