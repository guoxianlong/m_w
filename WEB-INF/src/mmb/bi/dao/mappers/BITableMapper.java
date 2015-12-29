package mmb.bi.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.bi.dao.BITableDao;
import mmb.bi.model.BITableBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class BITableMapper extends AbstractDaoSupport implements BITableDao {

	/**
	 * 整体效能
	 */
	@Override
	public List<HashMap<String,String>> getOrderCountTable(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 作业环节
	 */
	@Override
	public List<BITableBean> getOperTypeTableList(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}	
}
