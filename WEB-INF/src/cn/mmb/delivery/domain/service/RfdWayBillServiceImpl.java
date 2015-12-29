package cn.mmb.delivery.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import adultadmin.util.DateUtil;
import adultadmin.util.MD5Util;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.domain.model.RfdWayBill;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.vo.BasicParamBean;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaper;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaperFactory;
import cn.mmb.delivery.infrastructrue.persistence.WayBillMapper;
@Service( value= "RfdWayBillServiceImpl")
public class RfdWayBillServiceImpl implements WayBillService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private WayBillMapper wayBillMapper;
	
	@Resource(name ="rfdConfig")
	private BasicParamBean basicParamBean;
	
	@Resource
	private WayBillAdaperFactory wayBillAdaperFactory;

	@Override
	public List<WayBill> getDeliverRelation(int deliverId) throws Exception {
		List<Map<String,Object>> list=wayBillMapper.getDeliverRelationList(" status =0 and deliver_id="+deliverId);
		List<WayBill> result = new ArrayList<WayBill>();
		WayBill wayBill = null;
		for(Map<String,Object> map:list){
			String orderCode = (String) map.get("order_code");
			Map<String,Object> mapOrder =wayBillMapper.getUserOrderInfo(" uo.code='"+orderCode+"'");
			if(mapOrder == null || mapOrder.isEmpty()){
				continue;
			}
			wayBill = new RfdWayBill();
			wayBill.setOrderCode(orderCode);
			wayBill.setOrderType(mapOrder.get("buy_mode")+"");
			wayBill.setPostCode(mapOrder.get("postcode")+"");
			wayBill.setName(mapOrder.get("name")+"");
			wayBill.setMobile(StringUtil.concealPhone(mapOrder.get("phone")+""));
			wayBill.setProv(mapOrder.get("prov")+"");
			wayBill.setCity(mapOrder.get("city")+"");
			wayBill.setArea(mapOrder.get("area")+"");
			wayBill.setAddress(mapOrder.get("address")+"");
			wayBill.setItemsValue(mapOrder.get("dprice")+"");
			wayBill.setStockName(mapOrder.get("stock_name")+"");
			result.add(wayBill);
		}
		return result;
	}

	@Override
	public List<String> sendWayBillInfo(List<WayBill> sendData) throws Exception {
		WayBillAdaper wayBillAdaper =wayBillAdaperFactory.create("rfd");

		String indentity = basicParamBean.getIndentity();
		String key = basicParamBean.getKey();
		String url=basicParamBean.getUrl();
		
		/**
		 * 遍历发送信息
		 */
		List<String> list = new ArrayList<String>();//用于存放返回的xml的集合
		for(WayBill waybill:sendData){
			String jsonArray=wayBillAdaper.parseWayBillToXml(waybill);
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[如风达]-发送封装数据："+jsonArray);
			}
			
			String str = jsonArray.toString() + "|" + key; 
			String md5Str = MD5Util.md5Encode(str);
			String params = "?identity="+indentity+"&token="+md5Str;

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());        
			HttpEntity<String> formEntity = new HttpEntity<String>(jsonArray.toString(), headers);
			
			String result = restTemplate.postForObject(url+params, formEntity, String.class);
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[如风达]-接收封装数据："+result);
			}
			list.add(result);
		}
		return list;
	}

	@Override
	@Transactional
	public void updateDeliverRelation(int deliverId, List<WayBill> list)throws DataAccessException {
		if(!list.isEmpty()){
			wayBillMapper.updateDeliverRelation(deliverId,list);
		}
	}

	@Override
	public List<WayBill> parseToWayBill(List<WayBill> sendData,List<String> Jsonlist) throws Exception {
		WayBillAdaper wayBillAdaper =WayBillAdaperFactory.getWayBillAByServiceName("RfdWayBillAdaperImpl");
		return wayBillAdaper.parseJsonToWayBill(Jsonlist);
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
	public int cancelWayBill(int deliverId,String orderCode) throws Exception {
		WayBillAdaper wayBillAdaper =wayBillAdaperFactory.create("rfd");

		String indentity = basicParamBean.getIndentity();
		String key = basicParamBean.getKey();
		String url=basicParamBean.getUrlForCancel();
		
		/**
		 * 遍历发送信息
		 */
		List<String> list = new ArrayList<String>();//用于存放返回的xml的集合
		String packageCode =null;
		List<Map<String,Object>> deliverRelaList=wayBillMapper.getDeliverRelationList(" status in (0,1,3) and deliver_id="+deliverId+" and order_code='"+orderCode+"'");
		if(deliverRelaList.isEmpty()){
			throw new RuntimeException("订单["+orderCode+"]没有关联运单号的信息!");
		}
		String id="";
		for(Map<String,Object> map:deliverRelaList){
			id = String.valueOf(map.get("id"));
			packageCode = String.valueOf(map.get("package_code"));
		}
		
		List<WayBill> wayBillLs = new ArrayList<WayBill>();
		WayBill wayBill = new RfdWayBill();
		wayBill.setId(id);
		wayBillLs.add(wayBill);
		
		String jsonArray=wayBillAdaper.parseWayBillToJsonCancel(packageCode);
		if(log.isWarnEnabled()){
			log.warn(DateUtil.getNow()+"-快递公司[如风达]-发送封装数据(取消订单)："+jsonArray);
		}
		
		String str = jsonArray.toString() + "|" + key; 
		String md5Str = MD5Util.md5Encode(str);
		String params = "?identity="+indentity+"&token="+md5Str;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());        
		HttpEntity<String> formEntity = new HttpEntity<String>(jsonArray.toString(), headers);
		
		String JsonStr;
		String rStatus="4";
		try {
			JsonStr = restTemplate.postForObject(url+params, formEntity, String.class);
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[如风达]-接收封装数据(取消订单)："+JsonStr);
			}
			list.add(JsonStr);
			/**
			 * 封装结果
			 */
			List<WayBill> resultWayBill = this.parseToWayBill(list);
			
			//根据返回数据判断状态更新值
			if(resultWayBill!=null&&resultWayBill.size()>0){
				for(WayBill bill:resultWayBill){
					if(packageCode.equals(bill.getMailNo())){
						rStatus="2";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * 更新关联表
		 */
		return this.updateDeliverRelationForStatus(deliverId, wayBillLs,rStatus);
	}

	@Override
	public List<WayBill> parseToWayBill(List<String> list) throws Exception {
		WayBillAdaper wayBillAdaper =WayBillAdaperFactory.getWayBillAByServiceName("RfdWayBillAdaperImpl");
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
		Map<String,Object> mapOrder =wayBillMapper.getUserOrderInfo(" uo.code='"+orderCode+"'");
		
		wayBill = new RfdWayBill();
		wayBill.setOrderCode(orderCode);
		wayBill.setOrderType(mapOrder.get("buy_mode")+"");
		wayBill.setPostCode(mapOrder.get("postcode")+"");
		wayBill.setName(mapOrder.get("name")+"");
		wayBill.setMobile(StringUtil.concealPhone(mapOrder.get("phone")+""));
		wayBill.setProv(mapOrder.get("prov")+"");
		wayBill.setCity(mapOrder.get("city")+"");
		wayBill.setArea(mapOrder.get("area")+"");
		wayBill.setAddress(mapOrder.get("address")+"");
		wayBill.setItemsValue(mapOrder.get("dprice")+"");
		wayBill.setStockName(mapOrder.get("stock_name")+"");
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
