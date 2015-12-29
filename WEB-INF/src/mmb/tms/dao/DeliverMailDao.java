package mmb.tms.dao;

import java.util.HashMap;
import java.util.List;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.tms.model.DeliverMail;

/**
 * 邮件发送列表 数据库操作
 * @author 张小磊
 * @date 2014-3-27下午2:25:12
 */
public interface DeliverMailDao {
	/**
	 * 获取邮件发送列表
	 * @param map
	 * @return List
	 */
	public List<DeliverMail> getDeliverMailList(HashMap<String, String> map);
	
	public List<DeliverCorpInfoBean> getDeliverMailList1(HashMap<String, String> map);
	
	public List<DeliverMail> getDeliverPackageCodeList(HashMap<String, String> map);
	
	int getDeliverMailCount(HashMap<String, String> map);
	
	public DeliverMail getDeliverMailInfo(HashMap<String, String> map) ;
	
	public int updateDeliverMailStatus(DeliverMail bean);
	
	public int addDeliverMail(DeliverMail bean);
	
}
