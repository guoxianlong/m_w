package adultadmin.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class DeliverTimeInfoService {
	public static int jdFr=67501;//京东订单友链ID
	public static int tbFr=67510;//淘宝订单友链ID
	public static int neFr=67509;//19e订单友链ID
	public static int nmtFr=135011;//糯米团订单友链ID
	
	/**
	 * 获取配送信息
	 * @param orderId 订单id
	 * @return
	 * 返回map  如果为null，显示缺货提示     
	 * 		        不为null， 返回list里面放的map，其中值为
	 * 				   areaName     ——> 发货仓
	 *		           deliverName  ——> 快递公司
	 *		           deliverTime  ——> 平均配送时间
	 */
	public List deliverInfo(int orderId,List<String> orderStockAreaList) {
		DbOperation dbop = new DbOperation(DbOperation.DB_SLAVE);
		IAdminService adminService = ServiceFactory.createAdminService(dbop);		
		try {
			voOrder order = adminService.getOrder(orderId);
			if (order == null) {
				return null;
			}
			
			if (orderStockAreaList == null) {
				return null;
			}
			
			List stockinfo= new ArrayList();
			for (String stockArea : orderStockAreaList) {
				int orderStockArea = StringUtil.toInt(stockArea);
				if(orderStockArea==-1){//缺货
					return null;
				}
				
				String deliver="";
				if(checkOrderAmazom(order)){
					deliver = getDeliverAmazom(order, orderStockArea);
				} else {
					deliver = getDeliver(order, orderStockArea);
				}
				
				String[] delivers = deliver.split(",");
				
				String deliverName = "";
				for (String s : delivers ) {
					if (!deliverName.equals("")) {
						deliverName += "，";
					}
					deliverName += voOrder.deliverMapAll.get(s);
				}
				
				voOrderExtendInfo orderExtendInfo = adminService.getOrderExtendInfo(order.getId());
				if (orderExtendInfo == null) {
					return null;
				}
				
				String now = DateUtil.getNowDateStr();
				String yesterday = DateUtil.getBackFromDate(now, 1);
				
				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("select round(delivertime/ordercount/24, 1) delivertime from (select sum(odet.deliver_elspsed_time) delivertime,sum(odet.order_count) ordercount from order_deliver_elspsed_time odet where odet.create_date='");
				sqlBuffer.append(yesterday).append("' and odet.stock_area=").append(orderStockArea).append(" and odet.add_id1=").append(orderExtendInfo.getAddId1()).append(" and odet.add_id2= ").append(orderExtendInfo.getAddId2());
				if (orderExtendInfo.getAddId3() > 0) {
					sqlBuffer.append(" and odet.add_id3=").append(orderExtendInfo.getAddId3()); 
				}
				sqlBuffer.append(") tb");
				
				
				float days = 0;
				ResultSet rs = adminService.getDbOperation().executeQuery(sqlBuffer.toString());
				if (rs.next()) {
					days = rs.getFloat(1);
				}
				rs.close();
				
				String deliverDate = DateUtil.getBackFromDate(now, -(int)Math.ceil(days));
				SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
				String deliverday = sdf.format(DateUtil.parseDate(deliverDate));
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("areaName", (String)ProductStockBean.areaMap.get(orderStockArea));
				map.put("deliverName", deliverName);
				map.put("deliverTime", days > 0 ? days + "天（预计" + deliverday + DateUtil.getWeekOfDate(DateUtil.parseDate(deliverDate)) + "配送）" : "该区域可送达，暂无匹配数据");
				stockinfo.add(map);
			}
			return stockinfo;
		} catch (Exception e ) {
			e.printStackTrace();
		} finally {
			dbop.release();
		}
		return null;
	}
	
	
	/**
	 * 判断是不是亚马逊的EMS订单 2014-05-20
	 * @param order
	 * @return boolean
	 * @author syuf
	 */
	private boolean checkOrderAmazom(voOrder order) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		int number = -1;
		try {
			if(order.getFlat() == 3){
				ResultSet rs = dbOp.executeQuery("SELECT uop.order_ship FROM user_order_pop AS uop where type = 1 and order_id=" + order.getId());
				if(rs.next()){
					number = rs.getInt(1);
				}
				if(rs != null){
					rs.close();
				}
				if(number == 1){
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return false;
	}
	
	private String getDeliverAmazom(voOrder order, int stockArea) {
		int deliver = 0;
		if(stockArea== 4){//无锡分配快递公司
			deliver=37;//无锡邮政
		}else if(stockArea==3){//增城分配快递公司
			if (order.getAddress().substring(0,4).contains("广东")){//广东省内
				deliver = 11;
			} else {//光速省外
				deliver = 9;
			}
		}
		return deliver + "";
	}
	
	/**
	 * 分配快递公司
	 * @param order 订单
	 * @param stockArea 出库地区
	 * @return
	 */
	public String getDeliver(voOrder order,int stockArea){
		int deliver=0;
		String deliverStr = "";
		try{
			if(stockArea==0){//北京分配快递公司
				if(order.getAddress().substring(0,4).contains("河北省")){//全通物流
					deliver=33;
				}else{
					deliver=45;//北京宅急送
				}
			}else if(stockArea==3){//增城分配快递公司
					//自动分配快递公司  只有货到付款的才自动分配快递公司 start
					//取消自动分配快递公司时是否货到付款的规则 2013-7-26
					//京东订单，淘宝订单，19e订单，当申请出库分配快递公司时，必须指定EMS来配送
					if(order.getFr()==jdFr||order.getFr()==tbFr||order.getFr()==neFr||order.getFr()==nmtFr){
						if(order.getAddress().substring(0,4).contains("广东")){//广东省速递局
							deliver=11;
						}else{//广速省外
							deliver=9;
						}
					}else if(order.getAddress().substring(0,4).contains("福建省")){//通路速递
						deliver=14;
					}else if(order.getAddress().substring(0,4).contains("江苏")){//赛澳递江苏
						deliver=17;
					}else if(order.getAddress().substring(0,4).contains("上海")){//赛澳递上海
						deliver=18;
					}else if(order.getAddress().substring(0,4).contains("北京")
							||order.getAddress().substring(0,4).contains("天津")){//北京小红帽  2014-02-20
						deliver=41;
					}else if (order.getAddress().substring(0,4).contains("山东")
						&&!order.getAddress().contains("山东省烟台市长岛")){//山东海虹 2014-2-13
						deliver=42;
					}else if (order.getAddress().substring(0,4).contains("浙江")){//通路速递浙江，取消分配给如风达浙江2014-2-13
						deliver=20;
					}else if (order.getAddress().substring(0,4).contains("广东")){//银捷速递，通路速递广东
						deliver=21;//银捷速递每天限量700(2014-06-5,shiyaunfei)
						deliver=19;//通路速递广东
						deliverStr = "21,19";
					}else if(order.getAddress().substring(0,4).contains("广西")){//广西邮政
						deliver=25;
					}else if(order.getAddress().substring(0,4).contains("四川")){
						deliver=26;//宅急送四川，快优达暂停2014-1-6
					}else if(order.getAddress().substring(0,4).contains("重庆")){//宅急送重庆
						deliver=27;
					}else if(order.getAddress().substring(0,4).contains("江西")){//江西邮政
						deliver=28;
					}else if(order.getAddress().substring(0,4).contains("湖南")){//湖南邮政
						deliver=31;
					}else if(order.getAddress().substring(0,4).contains("湖北")){
						deliver=32;//湖北邮政，湖北星速取消，2014-2-11
					}else if(order.getAddress().substring(0,4).contains("河北")){//全通物流 2013-9-5取消吉林省
						deliver=33;
					}else if(order.getAddress().substring(0,4).contains("陕西")){//陕西邮政
						deliver=34;
					}else if(order.getAddress().substring(0,4).contains("贵州")){//贵州邮政
						deliver=35;
					}else if(order.getAddress().substring(0,4).contains("河南")){//河南大河速递
						deliver=36;
					}
					
					//2013-6-17取消广州宅急送分配
//					if(deliver==0){//前面没分快递公司，查询宅急送是否能送
//						int count=0;
//			        	if(DateUtil.compareTime(DateUtil.getNow(), StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00")==1){
//			        		count = service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00' and '" + DateUtil.getNow()+"' and deliver=10 and status<>3");
//						}else{
//							count = service.getOrderStockCount("create_datetime between '" + DateUtil.getLastDay()[0] + " 19:30:00' and '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00' and deliver=12 and status<>3");
//						}
//			        	if(count<1200){
//			        		String sql="select c.deliver_id from user_order a join user_order_extend_info b on a.code=b.order_code join deliver_area c " +
//									   " on ((b.add_id3=area_id and c.type=2) or (b.add_id4=area_id and c.type=3)) where a.code='"+order.getCode()+"'";
//							ResultSet rsDeliver=service.getDbOp().executeQuery(sql);
//							int deliverId = 0;
//							 if (rsDeliver.next()) {
//								 deliverId = rsDeliver.getInt("c.deliver_id");
//							}
//							rsDeliver.close();
//							if(deliverId!= 0){
//								deliver=deliverId;
//							}
//			        	}
//					}
					if(deliver==0){//没有分配快递公司，广东省内分配通路速递广东，省外分配给EMS
						if(order.getAddress().substring(0,4).contains("广东")){//通路速递广东(2013-04-22 zhaolin EMS->通路)
							deliver=19;
						}else{//广速省外
							deliver=9;
						}
					}
			}else if(stockArea==4){//无锡分配快递公司
					if(order.getFr()==jdFr||order.getFr()==tbFr||order.getFr()==neFr||order.getFr()==nmtFr){//无锡邮政
						deliver=37;
					}else if(order.getAddress().substring(0,4).contains("北京")
							||order.getAddress().substring(0,4).contains("天津")){//北京小红帽  2014-02-20
						deliver=41;
					}else if (order.getAddress().substring(0,4).contains("山东")
						&&!order.getAddress().contains("山东省烟台市长岛")){//山东海虹 2014-2-13
						deliver=42;
					}else if(order.getAddress().substring(0,4).contains("江苏")){//赛澳递江苏
						deliver=17;
					}else if(order.getAddress().substring(0,4).contains("上海")){//赛澳递上海
						deliver=18;
					}else if(order.getAddress().substring(0,4).contains("浙江")){//通路速递浙江，取消分配给如风达浙江2014-2-13
						deliver=20;
					}else if(order.getAddress().substring(0,4).contains("河北")){//全通物流 2013-9-5取消吉林省
						deliver=33;
					}else if(order.getAddress().substring(0,4).contains("陕西")){//陕西邮政
						deliver=34;
					}else if(order.getAddress().substring(0,4).contains("河南")){//河南大河速递
						deliver=36;
					}else if(order.getAddress().substring(0,4).contains("辽宁")){//辽宁邮政 2014-2-24
						deliver=43;
					}else if(order.getAddress().substring(0,4).contains("黑龙江")
							||order.getAddress().substring(0,4).contains("吉林")){//辽宁邮政省外 2014-3-6
						deliver=44;
					}else if(order.getAddress().substring(0,4).contains("四川")){
						deliver=26;//宅急送四川
					}else if (order.getAddress().substring(0,4).contains("广东")){//通路速递广东
						deliver=19;
					}else if (order.getAddress().substring(0,4).contains("安徽")){//合肥汇文
						deliver=46;
					}else{//非无锡配送范围，分配给上海无疆
						deliver=29;//上海无疆限量600单，2014-4-3 
						deliver=37;//无锡邮政
						deliverStr = "29,37";
					}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return deliverStr.equals("") ? deliver + "" : deliverStr;
	}

}
