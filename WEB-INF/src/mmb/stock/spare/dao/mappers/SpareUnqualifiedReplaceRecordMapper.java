package mmb.stock.spare.dao.mappers;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.spare.dao.SpareUnqualifiedReplaceRecordDao;
import mmb.stock.spare.model.SpareUnqualifiedReplaceRecord;
@Repository
public class SpareUnqualifiedReplaceRecordMapper extends AbstractDaoSupport implements SpareUnqualifiedReplaceRecordDao {

	@Override
	public int insert(SpareUnqualifiedReplaceRecord record) {
		getSession().insert(record);
		return record.getId();
	}

}
