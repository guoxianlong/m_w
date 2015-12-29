package mmb.rec.checkOrderStat;

import java.util.ArrayList;

import adultadmin.service.impl.BaseServiceImpl;


public class CheckOrderStatJobService extends BaseServiceImpl{
	public ArrayList getCheckOrderStatList(String condition, int index, int count, String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "check_order_stat", "mmb.rec.checkOrderStat.CheckOrderStatJobBean");
		return (ArrayList) queryList;
	}
	public boolean addCheckOrderStatJobBean(CheckOrderStatJobBean bean) {
		return addXXX(bean, "check_order_stat");
	}
}
