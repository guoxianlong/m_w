package mmb.bi.dao;

import java.util.List;
import java.util.Map;

import mmb.bi.model.BIChartBean;

public interface BIChartBeanDao {
	
	List<BIChartBean> singleOrderCountByDay(Map<String, String> paramMap);

	List<BIChartBean> singleOrderCountByMonth(Map<String, String> paramMap);

	List<BIChartBean> singleOrderCountByYear(Map<String, String> paramMap);
	
	
	List<BIChartBean> multiOrderCountByDay(Map<String, String> paramMap);

	List<BIChartBean> multiOrderCountByMonth(Map<String, String> paramMap);

	List<BIChartBean> multiOrderCountByYear(Map<String, String> paramMap);


	List<BIChartBean> onGuradPerByDay(Map<String, String> paramMap);

	List<BIChartBean> onGuradPerByMonth(Map<String, String> paramMap);

	List<BIChartBean> onGuradPerByYear(Map<String, String> paramMap);

	

	List<BIChartBean> singleOperTypeByDay(Map<String, String> paramMap);

	List<BIChartBean> singleOperTypeByMonth(Map<String, String> paramMap);

	List<BIChartBean> singleOperTypeByYear(Map<String, String> paramMap);
	
	
	List<BIChartBean> multiOperTypeByDay(Map<String, String> paramMap);

	List<BIChartBean> multiOperTypeByMonth(Map<String, String> paramMap);

	List<BIChartBean> multiOperTypeByYear(Map<String, String> paramMap);

}
