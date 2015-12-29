package mmb.ware.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.ware.cargo.model.CargoInfoArea;



public interface CargoInfoAreaDao {

    int insertCargoInfoArea(CargoInfoArea record);

    CargoInfoArea selectCargoInfoArea(String condition);

    CargoInfoArea selectCargoInfoAreaSlave(String condition);
    
    List<CargoInfoArea> selectCargoInfoAreaList(Map<String,String> map);

    List<CargoInfoArea> selectCargoInfoAreaListSlave(Map<String,String> map);
    CargoInfoArea selectByPrimaryKey(Integer id);
    
}