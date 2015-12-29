package mmb.rec.orderTransmit;

import java.util.ArrayList;

import adultadmin.service.impl.BaseServiceImpl;

public class OrderTransmitJobService extends BaseServiceImpl{
	public ArrayList getOrderTransimitJobList(String condition, int index, int count, String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "order_transmit_stat", "mmb.rec.orderTransmit.OrderTransmitJobBean");
		return (ArrayList) queryList;
	}
	public boolean addOrderTransimitJobBean(OrderTransmitJobBean bean) {
		return addXXX(bean, "order_transmit_stat");
	}
}
