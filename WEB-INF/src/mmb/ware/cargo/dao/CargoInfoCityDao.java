package mmb.ware.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.ware.cargo.model.CargoInfoCity;

public interface CargoInfoCityDao {
	
    int insertCargoInfoCity(CargoInfoCity record);

    CargoInfoCity selectCargoInfoCity(String condition);
    
    CargoInfoCity selectCargoInfoCitySlave(String condition);
    
    List<CargoInfoCity> selectCargoInfoCityList(Map<String,String> map);
    
    List<CargoInfoCity> selectCargoInfoCityListSlave(Map<String,String> map);
    

}