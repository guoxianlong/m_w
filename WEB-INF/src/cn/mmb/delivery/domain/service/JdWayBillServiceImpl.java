package cn.mmb.delivery.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.domain.model.JdWayBill;
import cn.mmb.delivery.domain.model.RfdWayBill;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaper;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaperFactory;
import cn.mmb.delivery.infrastructrue.persistence.DeliverDao;
import cn.mmb.delivery.infrastructrue.persistence.WayBillMapper;
import cn.mmb.hessian.HessianServlet;
import mmb.delivery.dao.DeliveryDao;
import mmb.delivery.domain.Waybill;
import mmb.delivery.service.impl.DeliveryServiceImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class JdWayBillServiceImpl implements WayBillService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private WayBillMapper wayBillMapper;
	@Resource
	private WayBillAdaperFactory wayBillAdaperFactory;
	@Resource
	private HessianServlet mosHessianAPI;
	@Resource
	private DeliverDao deliverDao;
	
	/**
	 * 获取需要发送的面单数据
	 * @param deliverId 快递公司id
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年8月10日 下午1:30:48
	 */
	@Override
	public List<WayBill> getDeliverRelation(int deliverId) throws Exception {
		List<WayBill> waybillList = new ArrayList<WayBill>();
		
		//获取需要向京东快递发送的面单数据
		List<JdWayBill> list = wayBillMapper.getNeedSendWaybillToJD(deliverId);
		for(int i=0; i<list.size(); i++){
			WayBill waybill = list.get(i);
			waybillList.add(waybill);
		}
		return waybillList;
	}

	@Override
	public List<String> sendWayBillInfo(List<WayBill> waybillList)throws Exception {
		List<String> list = new ArrayList<String>();
		WayBillAdaper wayBillAdaper = wayBillAdaperFactory.create("jd");
		
		//调用接口
		String[] paramList = new String[waybillList.size()];
		for(int i=0; i<waybillList.size(); i++){
			WayBill waybill = waybillList.get(i);
			waybill.setAddress(DeliveryServiceImpl.filterSpecialCharacters(waybill.getAddress())); //过滤特殊字符
			int carrier = -1;
			if(waybill.getStockArea() == ProductStockBean.AREA_WX){
				carrier = Waybill.CARRIER_MMB_WX;
			}else if(waybill.getStockArea() == ProductStockBean.AREA_CD){
				carrier = Waybill.CARRIER_MMB_CD;
			}
			paramList[i] = carrier + wayBillAdaper.parseWayBillToJson(waybill);
		}
		log.info("sendWayBillInfo send: " + JSONArray.fromObject(waybillList).toString());
		String jsonStr = mosHessianAPI.service("etmsWaybillSend", paramList);
		log.info("sendWayBillInfo return: " + jsonStr);
		list.add(jsonStr);
		
		return list;
	}
	
	
	@Override
	@Transactional
	public void updateDeliverRelation(int deliverId, List<WayBill> list)throws DataAccessException {
		//wayBillMapper.addDeliverRelationInfo(deliverId, list);
		wayBillMapper.updateDeliverRelation(deliverId,list);
	}


	@Override
	public List<WayBill> parseToWayBill(List<WayBill> sendData, List<String> resultList) throws Exception {
		String jsonStr = resultList.get(0);
		JSONObject json = JSONObject.fromObject(jsonStr);
		
		//判断返回状态是否成功
		int code = json.getInt("code");
		if(code != 200){
			throw new Exception("接口端出现异常！");
		}
		
		return sendData;
	}


	@Override
	public boolean printWayBill(WayBill wayBill) throws Exception {
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
		return null;
	}

	@Override
	public int cancelWayBill(int deliverId,String orderCode)throws Exception {
		List<String> list = new ArrayList<String>();
//		String packageCode =null;
		List<Map<String,Object>> deliverRelaList=wayBillMapper.getDeliverRelationList(" status in (0,1,3) and deliver_id="+deliverId+" and order_code='"+orderCode+"'");
		if(deliverRelaList.isEmpty()){
			throw new RuntimeException("订单["+orderCode+"]没有关联运单号的信息!");
		}
		String id="";
		for(Map<String,Object> map:deliverRelaList){
			id=String.valueOf(map.get("id"));
//			packageCode = (String) map.get("package_code");
		}
		list.add(id);
		/**
		 * 封装结果
		 */
		List<WayBill> resultWayBill = this.parseToWayBill(list);
		/**
		 * 更新关联表
		 */
		int result =this.updateDeliverRelationForStatus(deliverId, resultWayBill,"4");
		
		return result;
		
	}

	@Override
	public List<WayBill> parseToWayBill(List<String> list) throws Exception {
		WayBillAdaper wayBillAdaper =WayBillAdaperFactory.getWayBillAByServiceName("jdWayBillAdaperImpl");
		return wayBillAdaper.parseJsonToWayBillForCancel(list);
	}

	@Override
	public int updateDeliverRelationForStatus(int deliverId, List<WayBill> list,String status)throws DataAccessException {
		return wayBillMapper.updateDeliverRelationForStatus(deliverId,list,status);
		
	}

	@Override
	public List<WayBill> getNeedWayBillInfo(int deliverId,String orderCode) throws Exception {
		List<WayBill> result = new ArrayList<WayBill>();
		WayBill wayBill = null;
		Map<String,Object> mapOrder =wayBillMapper.getUserOrderInfoToJD(" uo.code='"+orderCode+"'");
		
		String packageCode =deliverDao.getDeliverPackageCode(deliverId, 0);
		if(packageCode==null || "".equals(packageCode)){
			throw new RuntimeException("快递公司包裹单号不足!");
		}
		int r =deliverDao.updateDeliverPackageCode(packageCode);
		wayBill = new JdWayBill();
		wayBill.setStockArea(Integer.parseInt(String.valueOf(mapOrder.get("stock_area"))));
		wayBill.setMailNo(packageCode);
		wayBill.setOrderCode(String.valueOf(mapOrder.get("code")));
		wayBill.setName(String.valueOf(mapOrder.get("name")));
		wayBill.setAddress(String.valueOf(mapOrder.get("address")));
		wayBill.setMobile(String.valueOf(mapOrder.get("mobile")));
		wayBill.setOrderType(String.valueOf(mapOrder.get("orderType")));
		wayBill.setdPrice(String.valueOf(mapOrder.get("dprice")));
		wayBill.setSender(String.valueOf(mapOrder.get("sender")));
		wayBill.setSenderMobile(String.valueOf(mapOrder.get("senderMobile")));
		wayBill.setSenderAddress(String.valueOf(mapOrder.get("senderAddress")));
		result.add(wayBill);
		
		return result;
	}

	@Override
	public void addDeliverRelation(int deliverId, List<WayBill> list)
			throws DataAccessException {
		if(!list.isEmpty()){
			wayBillMapper.addDeliverRelation(deliverId,list);
		}
	}

}
