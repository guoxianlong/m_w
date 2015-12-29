package mmb.ware.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.ware.cargo.model.CargoInfoStockArea;

public interface CargoInfoStockAreaDao {

    int insertCargoInfoStockArea(CargoInfoStockArea record);

    CargoInfoStockArea selectCargoInfoStockArea(String condition);
    
    CargoInfoStockArea selectCargoInfoStockAreaSlave(String condition);
    
    List<CargoInfoStockArea> selectCargoInfoStockAreaList(Map<String,String> map);
    
    List<CargoInfoStockArea> selectCargoInfoStockAreaListSlave(Map<String,String> map);

}