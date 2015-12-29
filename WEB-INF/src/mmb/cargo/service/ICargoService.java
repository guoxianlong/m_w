package mmb.cargo.service;

import java.util.List;
import java.util.Map;

import mmb.cargo.model.CargoInfoCity;

public interface ICargoService {
	
	public List<CargoInfoCity> getCargoInfoCitys(Map<String,String> condition);

	public int getCargoInfoCityCount(Map<String, String> condition);

	public boolean addCargoInfoCity(CargoInfoCity city) throws Exception;
	
}
