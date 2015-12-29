package mmb.bi.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.bi.dao.BISmsNumberBeanDao;
import mmb.bi.model.BISmsNumberBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

/**
 *定时短信手机号Mapper
 */
@Repository
public class BISmsNumberBeanMapper extends AbstractDaoSupport implements BISmsNumberBeanDao {

	/**
	 * 插入
	 */
	@Override
	public int insert(BISmsNumberBean record) {
		getSession().insert(record);
		return record.getId();
	}
	
	@Override
	public boolean cancelDeleteAll(String condition) {
		return getSession().delete(condition) > 0;
	}

	/**
	 * 更新
	 */
	@Override
	public int update(BISmsNumberBean record) {
		return getSession().update(record);
	}
 
	@Override
	public boolean checkAll(HashMap<String, String> paramMap) {
		return getSession().update(paramMap) > 0;
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
	public BISmsNumberBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	/**
	 * 查询列表
	 */
	@Override
	public List<BISmsNumberBean> selectList(String condition, int index, int count, String orderBy) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("condition", condition);
		map.put("orderBy", orderBy);
		map.put("index", index + "");
		map.put("count", count + "");		
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
