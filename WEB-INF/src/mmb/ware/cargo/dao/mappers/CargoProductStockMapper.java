package mmb.ware.cargo.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.util.LogUtil;
import mmb.ware.cargo.dao.CargoProductStockDao;
import mmb.ware.cargo.model.CargoProductStock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class CargoProductStockMapper extends AbstractDaoSupport implements CargoProductStockDao  {
	public Log stockUpdateLog = LogFactory.getLog("stockUpdate.Log");
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(CargoProductStock record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoProductStock selectByPrimaryKey(Integer id) {
		return getSession().selectOne(id);
	}
	@Override
	public CargoProductStock selectByPrimaryKeySlave(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public CargoProductStock selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}
	
	@Override
	public CargoProductStock selectByConditionSlave(String condition) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

	@Override
	public int selectCount(String condition) {
		return ((Integer) getSession().selectOne(condition)).intValue();
	}
	@Override
	public int selectCountSlave(String condition) {
		return ((Integer) getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	@Override
	public List<CargoProductStock> selectList(String condition, int index, int count, String orderBy) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("index", index);
		map.put("count", count);
		map.put("orderBy", orderBy);
		
		return getSession().selectList(map);
	}
	@Override
	public List<CargoProductStock> selectListSlave(String condition, int index, int count, String orderBy) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("index", index);
		map.put("count", count);
		map.put("orderBy", orderBy);
		
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("set", set);
		map.put("condition", condition);
		return getSession().update(map);
	}

	@Override
	public boolean updateCargoProductStockCount(int id, int count) {
		String startTime = DateUtil.getNow();
		String log = "update cargo_product_stock set stock_count=(stock_count + "+count+") where id = "+id+" and stock_count >= "+count+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();

		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("count", count);
			map.put("count2", -count);
			return getSession().update(map) > 0;
		} catch(Exception e){
			e.printStackTrace();
		} finally {

			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新货位库存语句为："+log+"，调用者为："+LogUtil.getInvokerName("adultadmin.mmb.ware.cargo.dao.mappers.CargoProductStockMapper"));
		}
		return false;
	}

	@Override
	public boolean updateCargoProductStockLockCount(int id, int count) {

		String startTime = DateUtil.getNow();
		String log = "update cargo_product_stock set stock_lock_count=(stock_lock_count + "+count+") where id = "+id+" and stock_lock_count >= "+count+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();

		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("count", count);
			map.put("count2", -count);
			return getSession().update(map) > 0;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新货位库存锁定量语句为："+log+"，调用者为："+LogUtil.getInvokerName("adultadmin.mmb.ware.cargo.dao.mappers.CargoProductStockMapper"));
		}
		return false;
	}
}
