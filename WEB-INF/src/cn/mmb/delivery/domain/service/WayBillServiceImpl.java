package cn.mmb.delivery.domain.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;

@Service( value= "WayBillServiceImpl")
public class WayBillServiceImpl implements WayBillService{

	@Override
	public List<WayBill> getDeliverRelation(int deliverId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sendWayBillInfo(List<WayBill> sendData)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateDeliverRelation(int deliverId, List<WayBill> list)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDeliverRelation(int deliverId, List<WayBill> list)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int updateDeliverRelationForStatus(int deliverId,
			List<WayBill> list, String status) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<WayBill> parseToWayBill(List<WayBill> sendData,
			List<String> list) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WayBill> parseToWayBill(List<String> list) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean printWayBill(WayBill wayBill) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean printWayBillDq(WayBill wayBill) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T getWayBillTrace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WayBillTrace> getNeedAddWayBillInfo(WayBillTrace wayBillTrace)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateWayBillInfo(List<WayBillTrace> wayBillTrace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int cancelWayBill(int deliverId, String orderCode) throws Exception {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public List<WayBill> getNeedWayBillInfo(int deliverId, String orderCode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
