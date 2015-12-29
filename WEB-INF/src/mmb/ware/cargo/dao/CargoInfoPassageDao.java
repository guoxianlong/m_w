package mmb.ware.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.ware.cargo.model.CargoInfoPassage;

public interface CargoInfoPassageDao {
	
    int insertCargoInfoPassage(CargoInfoPassage record);

    CargoInfoPassage selectCargoInfoPassage(String condition);

    CargoInfoPassage selectCargoInfoPassageSlave(String condition);

    List<CargoInfoPassage> selectCargoInfoPassageList(Map<String,String> map);
    
    List<CargoInfoPassage> selectCargoInfoPassageListSlave(Map<String,String> map);
    
}