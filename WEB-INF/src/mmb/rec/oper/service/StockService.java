package mmb.rec.oper.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mmb.common.dao.CommonDao;
import mmb.common.service.CommonService;
import mmb.rec.oper.dao.ProductStockDao;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.ProductStockBean;
@Service
public class StockService {

	@Autowired
	public ProductStockDao productStockMapper;
	@Autowired
	public CommonDao commonMapper;
	
	//ProductStock
	public int addProductStock(ProductStockBean productStockBean) {
		return productStockMapper.addProductStock(productStockBean);
	}

	public int deleteProductStock(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("product_stock", condition);
		return commonMapper.deleteCommon(paramMap);
	}

	public int updateProductStock(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("product_stock", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	public int getProductStockCount(String condition) {
		Map<String,String> paramMap = CommonService.constructCountMap("product_stock", condition);
		return commonMapper.getCommonCount(paramMap);
	}

	public ProductStockBean getProductStock(String condition) {
		return productStockMapper.getProductStock(condition);
	}


	public List<ProductStockBean> getProductStockList(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return productStockMapper.getProductStockList(paramMap);
	}
	
}
