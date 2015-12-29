package mmb.bi.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.bi.dao.BIChartBeanDao;
import mmb.bi.model.BIChartBean;

/**
 * BI 图表 Mapper
 * 
 * @author mengqy
 * 
 */
@Repository
public class BIChartBeanMapper extends AbstractDaoSupport implements BIChartBeanDao {

	/**
	 * 整体效能 单仓 按天查询
	 */
	@Override
	public List<BIChartBean> singleOrderCountByDay(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 单仓 按月查询
	 */
	@Override
	public List<BIChartBean> singleOrderCountByMonth(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 单仓 按年查询
	 */
	@Override
	public List<BIChartBean> singleOrderCountByYear(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 分仓对比 按天查询
	 */
	@Override
	public List<BIChartBean> multiOrderCountByDay(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 分仓对比 按月查询
	 */
	@Override
	public List<BIChartBean> multiOrderCountByMonth(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 分仓对比 按年查询
	 */
	@Override
	public List<BIChartBean> multiOrderCountByYear(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 在岗率 按天查询
	 */
	@Override
	public List<BIChartBean> onGuradPerByDay(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 在岗率 按月查询
	 */
	@Override
	public List<BIChartBean> onGuradPerByMonth(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 整体效能 在岗率 按年查询
	 */
	@Override
	public List<BIChartBean> onGuradPerByYear(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 作业环节 单仓 按天查询
	 */
	@Override
	public List<BIChartBean> singleOperTypeByDay(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 作业环节 单仓 按月查询
	 */
	@Override
	public List<BIChartBean> singleOperTypeByMonth(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 作业环节 单仓 按年查询
	 */
	@Override
	public List<BIChartBean> singleOperTypeByYear(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 作业环节 分仓对比 按天查询
	 */
	@Override
	public List<BIChartBean> multiOperTypeByDay(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 作业环节 分仓对比 按月查询
	 */
	@Override
	public List<BIChartBean> multiOperTypeByMonth(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	/**
	 * 作业环节 分仓对比 按年查询
	 */
	@Override
	public List<BIChartBean> multiOperTypeByYear(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
