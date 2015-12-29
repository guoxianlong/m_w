package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.stat.DeliverCorpInfoBean;

/**
 * 快递公司 数据库操作
 * @author 李宁
 * @date 2014-3-27下午2:25:12
 */
public interface DeliverCorpInfoDao {
	
	/**
	 * 新增快递公司
	 * @param deliverInfo
	 * @return 快递公司id
	 */
	public int insert(DeliverCorpInfoBean deliverInfo);
	
	/**
	 * 更新快递公司
	 * @param deliverInfo
	 * @return 更新的行数
	 */
	public int update(DeliverCorpInfoBean deliverInfo);
	
	/**
	 * 根据id获取快递公司信息
	 * @param id
	 * @return DeliverCorpInfoBean
	 */
	public DeliverCorpInfoBean getDeliverCorpInfoById(Integer id);
	
	/**
	 * 根据条件获取快递公司列表
	 * @param map
	 * @return List
	 */
	public List<DeliverCorpInfoBean> getDeliverCorpInfoList(Map<String,Integer> map);
	/**
	 * @describe 根据快递公司名字获取id
	 * @author syuf
	 * @date 2014-04-04
	 */
	public DeliverCorpInfoBean getDeliverCorpInfoByName(String name);
}
