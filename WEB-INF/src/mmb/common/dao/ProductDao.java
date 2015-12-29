package mmb.common.dao;

import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voProduct;

public interface ProductDao {
	
	public voProduct getProduct(String condition);

	public List<Map<String, String>> getProductNameAndCargo(String string);

	public List<Map<String, String>> getCargoProduct(String string);

	public List<Map<String, String>> getExceptProduct(String string);
	
}
