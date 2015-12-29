package mmb.stock.IMEI.dao.mappers;

import java.util.List;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.dao.IMEILogBeanDao;
@Repository
public class IMEILogBeanMapper extends AbstractDaoSupport implements IMEILogBeanDao {

	@Override
	public int insert(IMEILogBean record) {
		return getSession().insert(record);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IMEILogBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(IMEILogBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(IMEILogBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int batchInsertIMEILog(List<IMEILogBean> list) {
		return getSession().insert(list);
	}

}
