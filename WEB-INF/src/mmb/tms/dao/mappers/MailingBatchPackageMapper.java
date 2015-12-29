package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.cargo.model.HelpContext;
import mmb.tms.dao.MailingBatchDao;
import mmb.tms.dao.MailingBatchPackageDao;
import mmb.tms.model.MailingBatch;
import mmb.tms.model.MailingBatchPackage;

@Repository
public class MailingBatchPackageMapper extends AbstractDaoSupport implements MailingBatchPackageDao {

	
	
	@Override
	public List<MailingBatchPackage> getMailAttachments(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(MailingBatchPackage record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(MailingBatchPackage record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MailingBatchPackage selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(MailingBatchPackage record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(MailingBatchPackage record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
