package mmb.stock.area.service;

import java.util.List;
import java.util.Map;

import mmb.stock.area.dao.StockAreaDao;
import mmb.stock.area.model.StockArea;
import mmb.ware.WareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.db.DbOperation;
@Service
public class StockAreaSercive {
	
	@Autowired
	private StockAreaDao stockAreaDao;
	
	public List<StockArea> getStockAreaList(Map<String,String> map){
		List<StockArea> list =stockAreaDao.getStockAreaList(map);
		return list;
		
	}
	
	public List<Map<String,Object>> getStockAreaSubTempList(Map<String,String> map){
		List<Map<String,Object>> list = stockAreaDao.getStockAreaSubTempList(map);
		return list;
		
	}
	
	public int addStockArea(StockArea stockArea){
		return stockAreaDao.insert(stockArea);
	}
	
	public int editStockArea(StockArea StockArea){
		
		return stockAreaDao.updateByPrimaryKey(StockArea);
	}
	
	public int getStockAreaCount(Map<String,String> map){
		
		return stockAreaDao.getStockAreaCount(map);
		
	}
	
	public List<Map<String,Object>> getStockTypeNameList(Map<String,String> map){
		
		return stockAreaDao.getStockTypeList(map);
	}
	
	public List<Map<String,Object>> getStockTypeByCount(Map<String,String> map){
		
		return stockAreaDao.getStockTypeByCount(map);
	}
}
