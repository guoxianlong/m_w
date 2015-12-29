package mmb.aftersale;

/*
 * Created on 2009-8-25
 *
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import adultadmin.action.vo.voOrder;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2009-8-25
 * 
 * 说明：新钱包相关的Service
 */
public class WalletService extends BaseServiceImpl {
	public WalletService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public WalletService() {
		this.useConnType = CONN_IN_SERVICE;
	}

	// main_info

	public String mainInfoTableName = "wallet.main_info";

	public boolean addMainInfo(MainInfoBean bean) {
		return addXXX(bean, mainInfoTableName);
	}

	public boolean deleteMainInfo(String condition) {
		return deleteXXX(condition, mainInfoTableName);
	}

	public MainInfoBean getMainInfo(String condition) {
		return (MainInfoBean) getXXX(condition, mainInfoTableName,
				"mmb.aftersale.MainInfoBean");
	}

	public int getMainInfoCount(String condition) {
		return getXXXCount(condition, mainInfoTableName, "id");
	}

	public ArrayList getMainInfoList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, mainInfoTableName,
				"mmb.aftersale.MainInfoBean");
	}

	public boolean updateMainInfo(String set, String condition) {
		return updateXXX(set, condition, mainInfoTableName);
	}

	// done_order

	public String doneOrderTableName = "wallet.done_order";

	public boolean addDoneOrder(DoneOrderBean bean) {
		return addXXX(bean, doneOrderTableName);
	}

	public boolean deleteDoneOrder(String condition) {
		return deleteXXX(condition, doneOrderTableName);
	}

	public DoneOrderBean getDoneOrder(String condition) {
		return (DoneOrderBean) getXXX(condition, doneOrderTableName,
				"mmb.aftersale.DoneOrderBean");
	}

	public int getDoneOrderCount(String condition) {
		return getXXXCount(condition, doneOrderTableName, "id");
	}

	public ArrayList getDoneOrderList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, doneOrderTableName,
				"mmb.aftersale.DoneOrderBean");
	}

	public boolean updateDoneOrder(String set, String condition) {
		return updateXXX(set, condition, doneOrderTableName);
	}

	// oper_log

	public String operLogTableName = "wallet.oper_log";

	public boolean addOperLog(OperLogBean bean) {
		return addXXX(bean, operLogTableName);
	}

	public boolean deleteOperLog(String condition) {
		return deleteXXX(condition, operLogTableName);
	}

	public OperLogBean getOperLog(String condition) {
		return (OperLogBean) getXXX(condition, operLogTableName,
				"mmb.aftersale.OperLogBean");
	}

	public int getOperLogCount(String condition) {
		return getXXXCount(condition, operLogTableName, "id");
	}

	public ArrayList getOperLogList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, operLogTableName,
				"mmb.aftersale.OperLogBean");
	}

	public boolean updateOperLog(String set, String condition) {
		return updateXXX(set, condition, operLogTableName);
	}






	// refund_order

	public String refundOrderTableName = "wallet.refund_order";

	public boolean addRefundOrder(RefundOrderBean bean) {
		return addXXX(bean, refundOrderTableName);
	}

	public boolean deleteRefundOrder(String condition) {
		return deleteXXX(condition, refundOrderTableName);
	}

	public RefundOrderBean getRefundOrder(String condition) {
		return (RefundOrderBean) getXXX(condition, refundOrderTableName,
				"mmb.aftersale.RefundOrderBean");
	}

	public int getRefundOrderCount(String condition) {
		return getXXXCount(condition, refundOrderTableName, "id");
	}

	public ArrayList getRefundOrderList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				refundOrderTableName, "mmb.aftersale.RefundOrderBean");
	}

	public boolean updateRefundOrder(String set, String condition) {
		return updateXXX(set, condition, refundOrderTableName);
	}
// security_info
	
	public String securityInfoTableName = "wallet.security_info";

	public boolean addSecurityInfo(SecurityInfoBean bean) {
		return addXXX(bean, securityInfoTableName);
	}

	public boolean deleteSecurityInfo(String condition) {
		return deleteXXX(condition, securityInfoTableName);
	}

	public SecurityInfoBean getSecurityInfo(String condition) {
		return (SecurityInfoBean) getXXX(condition, securityInfoTableName,
				"mmb.aftersale.SecurityInfoBean");
	}

	public int getSecurityInfoCount(String condition) {
		return getXXXCount(condition, securityInfoTableName, "id");
	}

	public ArrayList getSecurityInfoList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				securityInfoTableName,
				"mmb.aftersale.SecurityInfoBean");
	}

	public boolean updateSecurityInfo(String set, String condition) {
		return updateXXX(set, condition, securityInfoTableName);
	}
}

