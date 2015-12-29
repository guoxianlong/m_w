package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.OrderStockDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.bean.order.OrderStockBean;
@Repository
public class OrderStockMapper extends AbstractDaoSupport implements OrderStockDao {

	@Override
	public int addOrderStock(OrderStockBean orderStockBean) {
		getSession().insert(orderStockBean);
		return orderStockBean.getId();
	}

	@Override
	public OrderStockBean getOrderStock(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<OrderStockBean> getOrderStockList(Map<String,String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<OrderStockBean> getOrderStockListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
