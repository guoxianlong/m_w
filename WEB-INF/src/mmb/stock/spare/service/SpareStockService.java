package mmb.stock.spare.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mmb.rec.oper.dao.ProductStockDao;
import mmb.stock.spare.dao.SpareProductStockDao;
import mmb.stock.spare.model.SpareCargoProductStock;
import mmb.stock.spare.model.SpareProductStock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAreaBean;

@Service
public class SpareStockService {

	@Autowired
	private SpareProductStockDao stockDao;

	@Autowired
	private ProductStockDao productStockDao;

	/**
	 * 查询备用机库存列表
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<SpareProductStock> getProductStockList(String condition, int index, int count, String orderBy) {
		
		// 查询备用机列表
		if(condition.length()==0){
			condition = null;
		}
		List<SpareProductStock> list = stockDao.getList(condition, index, count, orderBy);
		if (list == null){
			return new ArrayList<SpareProductStock>();
		}		
		if(list.size() == 0)
			return list;

		// 转换列表成map
		HashMap<Integer, SpareProductStock> resultMap = new HashMap<Integer, SpareProductStock>();
		StringBuilder sbProductId = new StringBuilder();
		for (SpareProductStock tmp : list) {
			resultMap.put(Integer.valueOf(tmp.getId()), tmp);
			if (sbProductId.length() == 0)
				sbProductId.append(" product_id IN ( ");
			sbProductId.append(tmp.getId()).append(" ,");
		}
		if (sbProductId.length() > 0) {
			sbProductId.setLength(sbProductId.length() - 1);
			sbProductId.append(" ) ");
		}
		
		StringBuilder sbAreaId = new StringBuilder();
		List<StockAreaBean> areaList = ProductStockBean.getStockAreaByType(ProductStockBean.STOCKTYPE_SPARE);
		if(areaList!=null){
			for (StockAreaBean area : areaList) {
				if (sbAreaId.length() == 0)
					sbAreaId.append(" area IN ( ");
				sbAreaId.append(area.getId()).append(" ,");
			}
		}
		if (sbAreaId.length() == 0) {
			throw new RuntimeException("库类库配置有误");
		}
		sbAreaId.setLength(sbAreaId.length() - 1);
		sbAreaId.append(" ) ");

		// 查询备用机商品库存
		// 查询动态库存列表 为增加备用机库做准备
		StringBuilder sb = new StringBuilder();
		sb.append(sbProductId.toString()).append(" AND ").append(sbAreaId.toString()).append(" AND type = ").append(ProductStockBean.STOCKTYPE_SPARE);
		HashMap<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", sb.toString());
		conditionMap.put("count", "-1");
		conditionMap.put("index", "-1");
		conditionMap.put("orderBy", null);
		List<ProductStockBean> productStockList = productStockDao.getProductStockList(conditionMap);
		
		// 设置备用机商品库存
		if (productStockList != null) {
			for (ProductStockBean ps : productStockList) {
				if (resultMap.containsKey(Integer.valueOf(ps.getProductId())))
					resultMap.get(Integer.valueOf(ps.getProductId())).getStock().put(Integer.valueOf(ps.getArea()), Integer.valueOf(ps.getStock()));
			}
		}

		return list;
	}

	/**
	 * 查询列表数量
	 * @param condition
	 * @return
	 */
	public int getProductStockListCount(String condition) {
		if(condition.length()==0){
			condition=null;
		}
		int count = stockDao.getListCount(condition);
		return count;
	}
	
	/**
	 * 查询备用机库存列表
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<SpareCargoProductStock> getCargoProductStockList(String condition, int index, int count, String orderBy) {
		
		// 查询备用机列表
		List<SpareCargoProductStock> list = stockDao.getCargoList(condition, index, count, orderBy);
		if (list == null){
			return new ArrayList<SpareCargoProductStock>();
		}		
		return list;
	}

	/**
	 * 查询列表数量
	 * @param condition
	 * @return
	 */
	public int getCargoProductStockListCount(String condition) {
		int count = stockDao.getCargoListCount(condition);
		return count;
	}
	
	
}
