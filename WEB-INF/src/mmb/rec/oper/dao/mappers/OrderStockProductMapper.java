package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;

import mmb.rec.oper.dao.OrderStockProductDao;
@Repository
public class OrderStockProductMapper extends AbstractDaoSupport implements OrderStockProductDao {

	@Override
	public int addOrderStockProduct(OrderStockProductBean orderStockProductBean) {
		getSession().insert(orderStockProductBean);
		return orderStockProductBean.getId();
	}

	@Override
	public OrderStockProductBean getOrderStockProduct(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<OrderStockProductBean> getOrderStockProductList(
			Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<OrderStockProductBean> getOrderStockProductListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
