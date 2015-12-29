package mmb.bi.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.bi.dao.BIBaseCountBeanDao;
import mmb.bi.model.BIBaseCountBean; 

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

/**
 * 人力基础数据 Mapper
 */
@Repository
public class BIBaseCountBeanMapper extends AbstractDaoSupport implements BIBaseCountBeanDao {
 
	/**
	 * 插入
	 */
	@Override
	public int insert(BIBaseCountBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	/**
	 * 更新
	 */
	@Override
	public int updateByPrimaryKey(BIBaseCountBean record) {
		return getSession().update(record);		
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
	public BIBaseCountBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	/**
	 * 查询列表
	 */
	@Override
	public List<BIBaseCountBean> selectList(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 统计一天、一个仓库的 生效的人力基础数据
	 */
	@Override
	public BIBaseCountBean searchOne(Map<String, String> paramMap) {
		return getSession().selectOne(paramMap);
	}
	

}
