package mmb.rec.stat.bean;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class CheckStockinStatService  extends BaseServiceImpl{

	private final Log logger = LogFactory.getLog(CheckStockinStatService.class);
	
	
	public CheckStockinStatService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public CheckStockinStatService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public boolean addCheckStockinStat(CheckStockinStatBean bean) {
		return addXXX(bean, "check_stockin_stat");
	}
	public List getCheckStockinStatList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "check_stockin_stat", "mmb.stock.stat.CheckStockinStatBean");
	}
	
	public int getCheckStockinStatCount(String condition) {
		return getXXXCount(condition, "check_stockin_stat", "id");
	}

	public CheckStockinStatBean getCheckStockinStat(String condition) {
		return (CheckStockinStatBean) getXXX(condition, "check_stockin_stat",
		"mmb.stock.stat.CheckStockinStatberBean");
	}

	public boolean updateCheckStockinStat(String set, String condition) {
		return updateXXX(set, condition, "check_stockin_stat");
	}

	public boolean deleteCheckStockinStat(String condition) {
		return deleteXXX(condition, "check_stockin_stat");
	}
	
}
