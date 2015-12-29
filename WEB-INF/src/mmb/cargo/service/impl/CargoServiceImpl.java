package mmb.cargo.service.impl;

import java.util.List;
import java.util.Map;

import mmb.cargo.model.CargoInfoCity;
import mmb.cargo.service.ICargoService;

import org.springframework.stereotype.Service;
/**
 * @Describe 货位service
 * @author syuf
 * @date 2014-01-21
 */
@Service
public class CargoServiceImpl implements ICargoService {

	@Override
	public List<CargoInfoCity> getCargoInfoCitys(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCargoInfoCityCount(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean addCargoInfoCity(CargoInfoCity city) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}}
