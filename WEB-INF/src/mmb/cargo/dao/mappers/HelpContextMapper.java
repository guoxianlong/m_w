package mmb.cargo.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.cargo.dao.HelpContextDao;
import mmb.cargo.model.HelpContext;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class HelpContextMapper extends AbstractDaoSupport implements
		HelpContextDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {		
		
		return ((Integer)getSession().delete(id)).intValue();
	}

	@Override
	public int insert(HelpContext record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int updateByPrimaryKey(HelpContext record) {
		getSession().update(record);
		return record.getId();
	}

	@Override
	public int getHelpContextCount(Map<String,String> condition) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	@Override
	public List<HelpContext> getHelpConetxtList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public HelpContext getHelpContext(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

}
