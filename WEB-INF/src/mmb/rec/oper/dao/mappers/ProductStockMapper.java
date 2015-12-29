package mmb.rec.oper.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import mmb.rec.oper.bean.StockBatchBean;
import mmb.rec.oper.bean.StockBatchLogBean;
import mmb.rec.oper.bean.StockCardBean;
import mmb.rec.oper.dao.ProductStockDao;
import mmb.util.LogUtil;

@Repository
public class ProductStockMapper extends AbstractDaoSupport implements ProductStockDao {
	public Log stockUpdateLog = LogFactory.getLog("stockUpdate.Log");
	@Override
	public int addProductStock(ProductStockBean productStockBean) {
		getSession().insert(productStockBean);
		return productStockBean.getId();
	}

	@Override
	public ProductStockBean getProductStock(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<ProductStockBean> getProductStockList(Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<ProductStockBean> getProductStockListSlave(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateStockCount(int id, int count) {
		String startTime = DateUtil.getNow();
		String log = "update product_stock set stock=(stock + "+count+") where id = "+id+" and stock >= "+count+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("count", count);
			map.put("count2", -count);
			return getSession().update(map) > 0;
		} catch(Exception e){
			e.printStackTrace();
			stockUpdateLog.error(StringUtil.getExceptionInfo(e));
		} finally {
			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新产品库存语句为："+log+",调用者为："+LogUtil.getInvokerName("adultadmin.mmb.rec.oper.dao.mappers.ProductStockMapper"));
		}
			return false;
	}

	@Override
	public boolean updateStockLockCount(int id, int count) {
		String startTime = DateUtil.getNow();
		String log = "update product_stock set lock_count=(lock_count + "+count+") where id = "+id+" and lock_count >= "+count+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("count", count);
			map.put("count2", -count);
			return getSession().update(map) > 0;
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新产品锁定量语句为："+log+",调用者为："+LogUtil.getInvokerName("adultadmin.mmb.rec.oper.dao.mappers.ProductStockMapper"));
		}
		return false;
	}

	@Override
	public int insertStockCardBean(StockCardBean bean) {
		getSession().insert(bean);
		return bean.getId();
	}

	@Override
	public List<StockBatchBean> getStockBatchBeanList(String condition, int index, int count, String orderBy) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("index", index);
		map.put("count", count);
		map.put("orderBy", orderBy);

		return getSession().selectList(map);
	}

	@Override
	public boolean deleteStockBatchBean(Integer id) {
		return getSession().delete(id) > 0;
	}

	@Override
	public boolean udpateStockBatchBean(String set, String condition) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("set", set);
		map.put("condition", condition);		
		return getSession().update(map) > 0;
	}

	@Override
	public boolean insertStockBatchLogBean(StockBatchLogBean bean) {
		getSession().insert(bean);		
		return bean.getId() > 0;
	}

}
