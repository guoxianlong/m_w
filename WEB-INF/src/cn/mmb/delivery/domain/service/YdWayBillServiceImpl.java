package cn.mmb.delivery.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.util.Base64;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.util.DateUtil;
import adultadmin.util.HttpKit;
import adultadmin.util.MD5Util;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.YdWayBill;
import cn.mmb.delivery.domain.model.vo.BasicParamBean;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaper;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaperFactory;
import cn.mmb.delivery.infrastructrue.persistence.WayBillMapper;
@Service( value= "YdWayBillServiceImpl")
public class YdWayBillServiceImpl implements WayBillService {

	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private WayBillMapper wayBillMapper;
	
	@Resource(name ="ydConfig")
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
			wayBill = new YdWayBill();
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
			wayBill.setStockArea(Integer.parseInt(String.valueOf(mapOrder.get("stock_area"))));
			result.add(wayBill);
		}
		return result;
	}

	@Override
	public List<String> sendWayBillInfo(List<WayBill> sendData)
			throws Exception {
		WayBillAdaper wayBillAdaper =wayBillAdaperFactory.create("yd");

		String xml="";
		String url = basicParamBean.getUrl(); 
		String partnerId = basicParamBean.getPartnerid();
		String password = basicParamBean.getPassword();
		String version = basicParamBean.getVersion();
		String request = basicParamBean.getRequest();
		/**
		 * 遍历发送信息
		 */
		List<String> list = new ArrayList<String>();//用于存放返回的xml的集合
		for(WayBill waybill:sendData){
			String xmldata=wayBillAdaper.parseWayBillToXml(waybill);
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[韵达]-发送封装数据："+xmldata);
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("partnerid",partnerId);
			params.put("version",version);
			params.put("request",request);
			params.put("xmldata",Base64.getBASE64(xmldata));
			params.put("validation",MD5Util.getKeyedDigest(Base64.getBASE64(xmldata)+partnerId+password, ""));
			xml = HttpKit.post(url, params);
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[韵达]-接收封装数据："+xml);
			}
			list.add(xml);
		}
		return list;
	}

	@Override
	@Transactional
	public void updateDeliverRelation(int deliverId, List<WayBill> list) throws DataAccessException {
		wayBillMapper.addDeliverRelationInfo(deliverId, list);
		wayBillMapper.updateDeliverRelation(deliverId,list);

	}

	@Override
	public int updateDeliverRelationForStatus(int deliverId,
			List<WayBill> list, String status) throws DataAccessException {
		// TODO Auto-generated method stub
		return wayBillMapper.updateDeliverRelationForStatus(deliverId,list,status);
	}

	@Override
	public List<WayBill> parseToWayBill(List<WayBill> sendData,List<String> list) throws Exception {
		WayBillAdaper wayBillAdaper =WayBillAdaperFactory.getWayBillAByServiceName("YdWayBillAdaperImpl");
		return wayBillAdaper.parseXmlToWayBill(list);
	}

	@Override
	public List<WayBill> parseToWayBill(List<String> xmlList) throws Exception {
		List<WayBill> list = new ArrayList<WayBill>();
		
		String status = null;//状态
		String orderCode = null;//mmb订单编号
		for(String xml:xmlList){
			WayBill wayBill = new YdWayBill();
			try {
				Document doc = DocumentHelper.parseText(xml);
				Element root = doc.getRootElement();
				Iterator<?> iter = root.elementIterator("response");
				// 遍历orderMessage节点  
		        while (iter.hasNext()) {
		            Element recordEle = (Element) iter.next();  
		            status =recordEle.elementTextTrim("status");
		            orderCode =recordEle.elementTextTrim("order_serial_no");
		        }
				
			}catch(Exception e){
				log.error(DateUtil.getNow()+"-快递公司[韵达]-没有获取有效信息："+xml);
				e.printStackTrace();
			}
			
			if("1".equals(status)){//成功状态
				if(orderCode!=null && !"".equals(orderCode) && !"0".equals(orderCode)){
					wayBill.setOrderCode(orderCode);
					list.add(wayBill);
				}
			}
		}
		
		return list;
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
		WayBillAdaper wayBillAdaper =wayBillAdaperFactory.create("yd");

		String xml="";
		String url = basicParamBean.getUrlForCancel(); 
		String partnerId = basicParamBean.getPartnerid();
		String password = basicParamBean.getPassword();
		String version = basicParamBean.getVersion();
		String request = basicParamBean.getRequestForCancel();
		
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
		WayBill wayBill = new YdWayBill();
		wayBill.setId(id);
		wayBill.setOrderCode(orderCode);
		wayBill.setMailNo(packageCode);
		wayBillLs.add(wayBill);
		
		String xmldata=wayBillAdaper.parseWayBillToXmlCancel(wayBill);
		if(log.isWarnEnabled()){
			log.warn(DateUtil.getNow()+"-快递公司[韵达]-发送封装数据(取消订单)："+xmldata);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partnerid",partnerId);
		params.put("version",version);
		params.put("request",request);
		params.put("xmldata",Base64.getBASE64(xmldata));
		params.put("validation",MD5Util.getKeyedDigest(Base64.getBASE64(xmldata)+partnerId+password, ""));
		xml = HttpKit.post(url, params);
		
		String rStatus="4";
		try {
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[韵达]-接收封装数据(取消订单)："+xml);
			}
			list.add(xml);
			/**
			 * 封装结果
			 */
			List<WayBill> resultWayBill = this.parseToWayBill(list);
			
			//根据返回数据判断状态更新值
			if(resultWayBill!=null && resultWayBill.size()>0){
				rStatus="2";
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
	public List<WayBill> getNeedWayBillInfo(int deliverId,String orderCode)throws Exception {
		List<WayBill> result = new ArrayList<WayBill>();
		WayBill wayBill = null;
		Map<String,Object> mapOrder =wayBillMapper.getUserOrderInfo(" uo.code='"+orderCode+"'");
		wayBill = new YdWayBill();
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
		wayBill.setStockArea(Integer.parseInt(String.valueOf(mapOrder.get("stock_area"))));
		result.add(wayBill);
		return result;
	}

	@Override
	public void addDeliverRelation(int deliverId, List<WayBill> list)
			throws DataAccessException {
		
	}

}
