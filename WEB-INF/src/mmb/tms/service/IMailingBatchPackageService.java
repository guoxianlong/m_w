package mmb.tms.service;

import java.util.List;
import java.util.Map;

import mmb.tms.model.MailingBatchPackage;

public interface IMailingBatchPackageService {
	/**
	 * 
	 * @Description: 获取写入邮件的内容 
	 * @param @param condition
	 * @param @return
	 * @return List<MailingBatchPackage>
	 * @auth aohaichen
	 */
	public List<MailingBatchPackage> getMailAttachments(Map<String,String> condition);
	/**
	 * 
	 * @Description: 创建附件 
	 * @param @param filePath
	 * @param @param fileName
	 * @param @param data
	 * @return void
	 * @auth aohaichen
	 */
	public void CreateAttachments(String filePath,String fileName,List data);

	/**
	 * 
	 * @Description: 删除附件
	 * @param @param filePath
	 * @param @param fileName
	 * @return void
	 * @auth aohaichen
	 */
	public void DelAttachments(String filePath,String fileName);
}
