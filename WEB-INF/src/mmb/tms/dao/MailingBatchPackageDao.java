package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.MailingBatchPackage;

public interface MailingBatchPackageDao {
    int deleteByPrimaryKey(Integer id);

    int insert(MailingBatchPackage record);

    int insertSelective(MailingBatchPackage record);

    MailingBatchPackage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MailingBatchPackage record);

    int updateByPrimaryKey(MailingBatchPackage record);

	List<MailingBatchPackage> getMailAttachments(Map<String, String> condition);
}