package mmb.rec.stat.bean;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class UpshelfStatService  extends BaseServiceImpl{

	private final Log logger = LogFactory.getLog(UpshelfStatService.class);
	
	
	public UpshelfStatService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public UpshelfStatService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public boolean addUpshelfStat(UpshelfStatBean bean) {
		return addXXX(bean, "upshelf_stat");
	}
	public List getUpshelfStatList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "upshelf_stat", "mmb.stock.stat.UpshelfStatBean");
	}
	
	public int getUpshelfStatCount(String condition) {
		return getXXXCount(condition, "upshelf_stat", "id");
	}

	public UpshelfStatBean getUpshelfStat(String condition) {
		return (UpshelfStatBean) getXXX(condition, "upshelf_stat",
		"mmb.stock.stat.UpshelfStatberBean");
	}

	public boolean updateUpshelfStat(String set, String condition) {
		return updateXXX(set, condition, "upshelf_stat");
	}

	public boolean deleteUpshelfStat(String condition) {
		return deleteXXX(condition, "upshelf_stat");
	}
	
}
