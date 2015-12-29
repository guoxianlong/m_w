package mmb.stock.IMEI.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.dao.IMEIBeanDao;
import mmb.stock.IMEI.model.ImeiProductLog;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class IMEIBeanMapper extends AbstractDaoSupport implements IMEIBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return 0;
	}

	@Override
	public int insert(IMEIBean record) {
		return getSession().insert(record);
	}

	@Override
	public IMEIBean selectByPrimaryKey(Integer id) {
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(IMEIBean record) {
		return getSession().update(record);
	}

	@Override
	public int updateByPrimaryKey(IMEIBean record) {
		return getSession().update(record);
	}

	@Override
	public IMEIBean getIMEIByCondition(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectOne(conditionMap);
	}

	@Override
	public int updateIMEIByCondition(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession().update(map);
	}

	@Override
	public int batchInsertIMEI(List<IMEIBean> list) {
		return getSession().insert(list);
	}

	@Override
	public IMEIBean getIMEIBean(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

	@Override
	public int batchUpdateIMEIStatus(List<IMEIBean> list) {
		return getSession().update(list);
	}

	@Override
	public int getImeiProductId(int productId) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(productId)).intValue();
	}

	/**
	 * 保存数据到数据库
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	@Override
	public void saveImeiProductLog(List<ImeiProductLog> list) {
		this.getSession().insert(list);
	}

	/**
	 * 查询IMEI产品属性设置表
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@Override
	public List<ImeiProductLog> queryIMEIProductLog(int pageNo, int pageSize) {
		return this.getSession().selectList(null, new RowBounds(pageNo,pageSize));
	}

	/**
	 * 查询IMEI产品属性设置表总记录数
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	@Override
	public Integer queryIMEIProductLogCount(Map<String, String> map) {
		return this.getSession().selectOne(map);
	}

	/**
	 * 返回已经存在的productCodeList
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@Override
	public List<String> queryIMEIProductCode(List<String> productCodeList) {
		return this.getSession().selectList(productCodeList);
	}

	/**
	 * 根据code获取id
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@Override
	public List<Integer> queryProductId(List<String> productCodeList) {
		return this.getSession().selectList(productCodeList);
	}

	/**
	 * 保存数据
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@Override
	public void saveImeiProduct(List<Integer> productIdList) {
		this.getSession().insert(productIdList);
	}

}
