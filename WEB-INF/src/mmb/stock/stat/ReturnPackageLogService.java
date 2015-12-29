package mmb.stock.stat;


import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class ReturnPackageLogService extends BaseServiceImpl{

	private final Log logger = LogFactory.getLog(ReturnPackageLogService.class);
	
	
	public ReturnPackageLogService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ReturnPackageLogService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	
	/**
	 * 添加退货日志
	 * @param remark
	 * @param user
	 * @param vorder
	 * @return
	 */
	public boolean addReturnPackageLog(String remark, voUser user, String orderCode) {
			ReturnPackageLogBean bean = new ReturnPackageLogBean();
			bean.setOperId(user.getId());
			bean.setOperName(user.getUsername());
			bean.setOrderCode(orderCode);
			bean.setOperTime(DateUtil.getNow());
			bean.setRemark(remark);
			if(!addXXX(bean, "return_package_log")){
				return false;
			}
			return true;
	}

	
	/**
	 * 查询退货日志
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getReturnPackageLogList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "return_package_log", "mmb.stock.stat.ReturnPackageLogBean");
	}
	
	public int getReturnPackageLogCount(String condition) {
		return getXXXCount(condition, "return_package_log", "id");
	}

	public ReturnPackageLogBean getReturnPackageLog(String condition) {
		return (ReturnPackageLogBean) getXXX(condition, "return_package_log",
		"mmb.stock.stat.ReturnPackageLogBean");
	}

	public boolean updateReturnPackageLog(String set, String condition) {
		return updateXXX(set, condition, "return_package_log");
	}

	public boolean deleteReturnPackageLog(String condition) {
		return deleteXXX(condition, "return_package_log");
	}
	
}




