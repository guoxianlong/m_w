package mmb.stock.aftersale.dao.mappers;

import mmb.stock.aftersale.AfterSaleDetectLogBean;
import mmb.stock.aftersale.dao.AfterSaleDetectLogDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class AfterSaleDetectLogMapper extends AbstractDaoSupport implements AfterSaleDetectLogDao{

	@Override
	public int insert(AfterSaleDetectLogBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSaleDetectLog(String sql) {
		return getSession().insert(sql);
	}

}
