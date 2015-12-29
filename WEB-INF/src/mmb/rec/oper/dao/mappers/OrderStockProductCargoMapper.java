package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.OrderStockProductCargoDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.bean.order.OrderStockProductCargoBean;
@Repository
public class OrderStockProductCargoMapper extends AbstractDaoSupport implements OrderStockProductCargoDao {

	@Override
	public int addOrderStockProductCargo(
			OrderStockProductCargoBean orderStockProductCargo) {
		getSession().insert(orderStockProductCargo);
		return orderStockProductCargo.getId();
	}

	@Override
	public OrderStockProductCargoBean getOrderStockProductCargo(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<OrderStockProductCargoBean> getOrderStockProductCargoList(
			Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<OrderStockProductCargoBean> getOrderStockProductCargoListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
