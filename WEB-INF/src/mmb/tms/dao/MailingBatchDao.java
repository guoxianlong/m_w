package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.MailingBatch;

public interface MailingBatchDao {
    int deleteByPrimaryKey(Integer id);

    int insert(MailingBatch record);

    int insertSelective(MailingBatch record);

    MailingBatch selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MailingBatch record);

    int updateByPrimaryKey(MailingBatch record);

	List<MailingBatch> getMailAttachments(Map<String, String> condition);

	int getMailingBatchCount(String condition);
}