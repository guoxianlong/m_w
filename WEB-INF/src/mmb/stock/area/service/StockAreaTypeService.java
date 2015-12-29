package mmb.stock.area.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.area.dao.StockAreaTypeDao;
import mmb.stock.area.model.StockAreaType;
import mmb.ware.WareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.db.DbOperation;
@Service
public class StockAreaTypeService {

	@Autowired
	private StockAreaTypeDao stockAreaTypeDao;
	
	public List<StockAreaType> getStockAreaTypes(Map<String,String> map){
		return stockAreaTypeDao.getStockAreaTypes(map);
	}
	
	public int deleteByCondition(Map<String,String> map){
		return stockAreaTypeDao.deleteByCondition(map);
	}
	
	public int insert(StockAreaType stockareaType){
		return stockAreaTypeDao.insert(stockareaType);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=RuntimeException.class)
	public void editStockAreaType(String id,String[] typeName) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("condition", "area_id= "+id);
		List<StockAreaType> list =this.getStockAreaTypes(map);
		if(list!=null){
			if(typeName==null || "".equals(typeName[0])){
				this.deleteByCondition(map);
			}else{
				this.deleteByCondition(map);
				for(int i = 0 ; i < typeName.length; i ++){
					StockAreaType stockAreaType = new StockAreaType();
					stockAreaType.setAreaId(Integer.parseInt(id));
					stockAreaType.setTypeId(Integer.parseInt(typeName[i]));
					stockAreaType.setStatus(1);
					this.insert(stockAreaType);
				}
			}
			
		}else{
			for(int i = 0 ; i < typeName.length; i ++){
				StockAreaType stockAreaType = new StockAreaType();
				stockAreaType.setAreaId(Integer.parseInt(id));
				stockAreaType.setTypeId(Integer.parseInt(typeName[i]));
				stockAreaType.setStatus(1);
				this.insert(stockAreaType);
			}
		}
	}
}
