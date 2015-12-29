package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.order.AuditPackageBean;

public interface ScanOrderStockDao {
	
	public int getOrderStockQueryCount(String condition);
	
	public List<AuditPackageBean> getOrderStockQueryList(Map<String,String> paramMap);

}
