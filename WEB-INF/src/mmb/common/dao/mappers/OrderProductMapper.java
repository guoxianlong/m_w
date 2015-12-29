package mmb.common.dao.mappers;

import java.util.List;

import mmb.common.dao.OrderProductDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.action.vo.voOrderProduct;
@Repository
public class OrderProductMapper extends AbstractDaoSupport implements OrderProductDao {

	@Override
	public List<voOrderProduct> getOrderProductsSplit(int id) {
		return getSession(DynamicDataSource.SLAVE).selectList(id);
	}

	@Override
	public List<voOrderProduct> getOrderPresentsSplit(int id) {
		return getSession(DynamicDataSource.SLAVE).selectList(id);
	}

}
