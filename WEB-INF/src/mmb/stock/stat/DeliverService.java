package mmb.stock.stat;

import java.util.ArrayList;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class DeliverService extends BaseServiceImpl{
	public DeliverService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public DeliverService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	// 快递公司 deliver_corp_info表
	public boolean addDeliverCorpInfo(DeliverCorpInfoBean bean) {
		return addXXX(bean, "deliver_corp_info");
	}

	public ArrayList getDeliverCorpInfoList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "deliver_corp_info", "mmb.stock.stat.DeliverCorpInfoBean");
	}

	public int getDeliverCorpInfoCount(String condition) {
		return getXXXCount(condition, "deliver_corp_info", "id");
	}

	public DeliverCorpInfoBean getDeliverCorpInfo(String condition) {
		return (DeliverCorpInfoBean) getXXX(condition, "deliver_corp_info",
		"mmb.stock.stat.DeliverCorpInfoBean");
	}

	public boolean updateDeliverCorpInfo(String set, String condition) {
		return updateXXX(set, condition, "deliver_corp_info");
	}

	public boolean deleteDeliverCorpInfo(String condition) {
		return deleteXXX(condition, "deliver_corp_info");
	}
	// 结算公司 balance_type表
	public boolean addBalanceTypeInfo(BalanceTypeBean bean) {
		return addXXX(bean, "balance_type");
	}

	public ArrayList getBalanceTypeList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "balance_type", "mmb.stock.stat.BalanceTypeBean");
	}

	public int getBalanceTypeCount(String condition) {
		return getXXXCount(condition, "balance_type", "id");
	}

	public BalanceTypeBean getBalanceTypeInfo(String condition) {
		return (BalanceTypeBean) getXXX(condition, "balance_type",
		"mmb.stock.stat.BalanceTypeBean");
	}

	public boolean updateBalanceTypeInfo(String set, String condition) {
		return updateXXX(set, condition, "balance_type");
	}

	public boolean deleteBalanceTypeInfo(String condition) {
		return deleteXXX(condition, "balance_type");
	}
	// 快递公司可配送地区  deliver_area表
	public boolean addDeliverAreaInfo(DeliverAreaBean bean) {
		return addXXX(bean, "deliver_area");
	}

	public ArrayList getDeliverAreaList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "deliver_area", "mmb.stock.stat.DeliverAreaBean");
	}

	public int getDeliverAreaCount(String condition) {
		return getXXXCount(condition, "deliver_area", "id");
	}

	public DeliverAreaBean getDeliverAreaInfo(String condition) {
		return (DeliverAreaBean) getXXX(condition, "deliver_area",
		"mmb.stock.stat.DeliverAreaBean");
	}

	public boolean updateDeliverAreaInfo(String set, String condition) {
		return updateXXX(set, condition, "deliver_area");
	}

	public boolean deleteDeliverAreaInfo(String condition) {
		return deleteXXX(condition, "deliver_area");
	}
}
