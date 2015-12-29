package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.ScanOrderStockDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.bean.order.AuditPackageBean;
@Repository
public class ScanOrderStockMapper extends AbstractDaoSupport implements ScanOrderStockDao {

	@Override
	public int getOrderStockQueryCount(String condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	@Override
	public List<AuditPackageBean> getOrderStockQueryList(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
