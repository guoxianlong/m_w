package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.cargo.model.HelpContext;
import mmb.tms.dao.MailingBatchDao;
import mmb.tms.model.MailingBatch;

@Repository
public class MailingBatchMapper extends AbstractDaoSupport implements MailingBatchDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(MailingBatch record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(MailingBatch record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MailingBatch selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(MailingBatch record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(MailingBatch record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMailingBatchCount(String condition) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public List<MailingBatch> getMailAttachments(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

}
