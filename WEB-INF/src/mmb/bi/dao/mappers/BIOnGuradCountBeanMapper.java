package mmb.bi.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.bi.dao.BIOnGuradCountBeanDao;
import mmb.bi.model.BIBaseCountBean;
import mmb.bi.model.BIOnGuradCountBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

/**
 *在职人力Mapper
 */
@Repository
public class BIOnGuradCountBeanMapper extends AbstractDaoSupport implements BIOnGuradCountBeanDao {

	/**
	 * 插入
	 */
	@Override
	public int insert(BIOnGuradCountBean record) {
		getSession().insert(record);
		return record.getId();
	}

	/**
	 * 删除
	 */
	@Override
	public int cancelDelete(Map<String, String> paramMap) {
		return getSession().delete(paramMap);
	}

	/**
	 * 更新
	 */
	@Override
	public int update(BIOnGuradCountBean record) {
		return getSession().update(record);
	}

	/**
	 * 生效
	 */
	@Override
	public int check(Map<String, String> paramMap) {
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
	public BIOnGuradCountBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	/**
	 * 查询列表
	 */
	@Override
	public List<BIOnGuradCountBean> selectList(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
