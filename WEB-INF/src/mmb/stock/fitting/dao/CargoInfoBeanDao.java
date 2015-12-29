package mmb.stock.fitting.dao;

import mmb.stock.fitting.model.CargoInfoBean;
import mmb.stock.fitting.model.CargoOperationBean;
import mmb.stock.fitting.model.CargoOperationCargoBean;

public interface CargoInfoBeanDao {
	
    CargoInfoBean selectByPrimaryKey(Integer id);

    CargoInfoBean selectByCondition(String condition);

	boolean updateCargoInfoBean(String set, String condition);

	int insertCargoOperationCargoBean(CargoOperationCargoBean bean);

	public int insertCargoOperationBean(CargoOperationBean bean);
	
	CargoOperationBean selectCargoOperationByCondition(String condition);
}