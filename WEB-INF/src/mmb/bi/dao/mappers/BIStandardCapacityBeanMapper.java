package mmb.bi.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.bi.dao.BIStandardCapacityBeanDao;
import mmb.bi.model.BIStandardCapacityBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

/**
 *标准产能Mapper
 */
@Repository
public class BIStandardCapacityBeanMapper extends AbstractDaoSupport implements BIStandardCapacityBeanDao {

	/**
	 * 插入
	 */
	@Override
	public int insert(BIStandardCapacityBean record) {
		getSession().insert(record);
		return record.getId();
	}

	/**
	 * 删除
	 */
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	/**
	 * 更新停用时间
	 */
	@Override
	public int updateStopTime(Map<String, String> paramMap) {
		return getSession().update(paramMap);
	}
	
	/**
	 * 查询列表数量
	 */
	@Override
	public int getListCount(String condition) {
		return ((Integer)getSession().selectOne(condition)).intValue();
	}

	/**
	 * 查询单个
	 */
	@Override
	public BIStandardCapacityBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	/**
	 * 查询列表
	 */
	@Override
	public List<BIStandardCapacityBean> selectList(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}
	
}
