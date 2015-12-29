package mmb.rec.oper.service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mmb.finance.balance.BalanceService;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.rec.oper.bean.PromotionProductBean;
import mmb.rec.sys.easyui.Json;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;
import adultadmin.action.stock.PrintPackageAction;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.OrderImportLogBean;
import adultadmin.bean.OrderStockStatusBean;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.UserOrderProductHistoryBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.order.UserOrderPackageTypeBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.IUserOrderService;
import adultadmin.util.Arith;
import adultadmin.util.CodeUtil;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.order.CouponDetail;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.order.UserInfo;
import com.jd.open.api.sdk.request.order.OrderFbpGetRequest;
import com.jd.open.api.sdk.request.order.OrderFbpSearchRequest;
import com.jd.open.api.sdk.response.order.OrderFbpGetResponse;
import com.jd.open.api.sdk.response.order.OrderFbpSearchResponse;
import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceSaleBaseDataService;

public class OuterOrderService {
	String specialCitys = "北京，上海，天津，重庆";
	private static int jdfr = 67501;
//	private final static String[] jdProductCodes = new String[]{"1074112714","1078460344","1078460347","1108176493","1108176494","1173359080","1173359081","1160711817","1160711818","1160660188","1381835601"};
//	private final static String[] mmbProductCodes = new String[]{"2005180086","2005180701","2005180702","2009180087","2009180848","2005193885","2005193886","2005193890","2005193889","2005167533","2005226167"};
	public static int orderLimitCount = 999999; // 订单末尾位数参数
    public static int orderCodeLength = 6 ;     // 订单末尾位数长度
    public static String noSku = "换货单中存在原销售单没有的商品！";
    public static String optionalFields = "order_id,order_total_price,order_start_time,order_end_time,order_seller_price,order_payment,freight_price,order_state,order_remark,return_order,consignee_info,item_info_list,coupon_detail_list";
	public boolean getJDOrders(String startDate, String endDate, int pageSize, int maxPage, DbOperation dbop) throws JdException, SQLException {
		DbOperation dbOpSlave = new DbOperation();
		dbOpSlave.init(DbOperation.DB_SLAVE);
		try {
			
			JdClient client=new DefaultJdClient(OrderStockBean.SERVER_URL,OrderStockBean.accessToken,OrderStockBean.appKey,OrderStockBean.appSecret);
			OrderFbpSearchRequest request = new OrderFbpSearchRequest();
			for (int page = 1; page <= maxPage; page++) {
				request.setStartDate( startDate ); 
				request.setEndDate( endDate ); 
				request.setPage( page + ""); 
				request.setPageSize( pageSize + "" ); 
				request.setOptionalFields(optionalFields);
				request.setTimestamp(DateUtil.getNow());
				OrderFbpSearchResponse response = client.execute(request);
				if (response == null) {
					System.out.println(startDate + "至" + endDate + "第" + page + "页京东订单search为空，");
					return false;
				}
				
				if (response.getOrderInfoResult() == null) {
					System.out.println(startDate + "至" + endDate + "第" + page + "页京东订单search的orderinforesult为空，" + response.getZhDesc());
					return false;
				}
				
				List<OrderSearchInfo> orderSearchInfoList = response.getOrderInfoResult().getOrderInfoList();
				if (orderSearchInfoList == null || orderSearchInfoList.size() == 0) {
					break;
				} else {
					int size = orderSearchInfoList.size();
					for (int i = 0; i < size; i ++) {
						OrderSearchInfo osInfo = orderSearchInfoList.get(i);
						if (!osInfo.getOrderState().equals("WanCheng")) {
							continue;
						} else {
							dbop.startTransaction();
							int count = 0;
							ResultSet rs = dbop.executeQuery("select count(*) from mmb_outer_relation where outer_swap_order_code='" + osInfo.getOrderId() + "'"); 
							if (rs.next()) {
								count = rs.getInt(1);
							}
							if (count > 0) {
								rs.close();
								continue;
							}
							rs = dbop.executeQuery("select count(id) from outer_abnormal_info where outer_order_code = '" + osInfo.getOrderId() + "'");
							if (rs.next()) {
								count = rs.getInt(1);
							}
							if (count > 0) {
								rs.close();
								continue;
							}
							String result = addOuterOrderInfo(osInfo, dbop, dbOpSlave);
							if (result != null) {
								dbop.rollbackTransaction();
								dbop.startTransaction();
								dbop.executeUpdate("INSERT INTO `outer_abnormal_info` (`outer_order_code`, `source_id`, `keep_time`, `keep_user_id`, `keep_user_name`, `status`, `reason`) "
										+ "VALUES ('" + osInfo.getOrderId() + "', '4', '" + DateUtil.getNow() + "', '0', '无', '1', '" + result + "')");
							}
							dbop.commitTransaction();
						}
					}
					//数量不够一页停止
					if (size < pageSize) {
						break;
					}
				}
			}
		} catch (Exception e) {
			if (!dbop.getConn().getAutoCommit()) {
				dbop.rollbackTransaction();
			}
			e.printStackTrace();
			return false;
		} finally {
			dbOpSlave.release();
		}
		return true;
	}
	
	public String addOuterOrderInfo(OrderSearchInfo osInfo, DbOperation dbop,DbOperation dbOpSalve) {
		String endTime = osInfo.getOrderEndTime();
		UserInfo userInfo = osInfo.getConsigneeInfo();
		voOrder order = new voOrder();
		order.setName(userInfo.getFullname());
		order.setPhone(userInfo.getTelephone());
		order.setPhone2(userInfo.getMobile());
		order.setAddress(userInfo.getFullAddress());
		order.setPhoneStatus(5 + "");
		order.setBuyMode(2);
		order.setStatus(14);
		order.setPackageNum(osInfo.getOrderId());
		order.setFlat(4);
//		order.setFlag();
		order.setOperator("");
		order.setCode("Q" + CodeUtil.getSerialCode());
		order.setFr(jdfr);
		order.setDeliverType(2);
		order.setDiscount(1);
		order.setRemitType(0);
		order.setPrepayDeliver(0);
		order.setAreano(0);
		order.setPrePayType(0);
		order.setIsOlduser(0);
		order.setSuffix(0);
		order.setWebRemark("");
		order.setRemark("", order.getPhoneStatus(), "", osInfo.getOrderId(),osInfo.getFreightPrice());
		order.setDprice(StringUtil.toFloat(osInfo.getOrderSellerPrice()) + StringUtil.toFloat(osInfo.getFreightPrice()));
		order.setPrice(StringUtil.toFloat(osInfo.getOrderTotalPrice()) + StringUtil.toFloat(osInfo.getFreightPrice()));
		order.setPostage(Float.valueOf(osInfo.getFreightPrice()));
		order.setConsigner("");

        int id = 0;
        IAdminService service = ServiceFactory.createAdminService(dbop);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOperation());
        WareService wareService = new WareService(dbop);
        UserOrderServiceImpl userOrderService = new UserOrderServiceImpl(IBaseService.CONN_IN_SERVICE,service.getDbOperation());
        try {
        	List<HashMap<String, String>> outer_mmb_sku = new ArrayList<HashMap<String, String>>();
			ResultSet rs2 = dbop.executeQuery("select outer_sku,mmb_sku from mmb_outer_relation_sku");
			while (rs2.next()) {
				HashMap<String, String>  map = new HashMap<String, String>();
				map.put("outer",rs2.getString(1));
				map.put("mmb", rs2.getString(2));
				outer_mmb_sku.add(map);
			}
			rs2.close();
        	String outerSaleOrderCode = osInfo.getOrderId();
        	Map<String, Float> itemPriceMap = new HashMap<String, Float>();
        	//销售订单
        	if (osInfo.getReturnOrder().equals("0")) {
        		List<CouponDetail> couponDetailList = osInfo.getCouponDetailList();
				List<ItemInfo> outeritemList = osInfo.getItemInfoList();
				float itemCouponPrice = 0f;
        		for (ItemInfo outeritem : outeritemList) {
        			itemCouponPrice = 0f;
					for (CouponDetail cd : couponDetailList) {
						if (cd.getSkuId().equals(outeritem.getSkuId()) && this.isOurCoupon(cd.getCouponType())) {
							itemCouponPrice = Arith.div(StringUtil.toFloat(cd.getCouponPrice()), StringUtil.toFloat(outeritem.getItemTotal()));
						}
					}
					itemPriceMap.put(outeritem.getSkuId(), Arith.sub(StringUtil.toFloat(outeritem.getJdPrice()),itemCouponPrice));
				}
        	} else if (osInfo.getReturnOrder().equals("1") || osInfo.getReturnOrder().equals("2")) {
        		//换货单
        		String remark = osInfo.getOrderRemark();
        		
        		String result = "";  
                Pattern pattern = Pattern.compile("原订单号:[0-9]{0,}");  
                Matcher matcher = pattern.matcher(remark);  
                if (matcher.find()) {  
                    result = matcher.group(0);//只取第一组  
                }
                if(result.split(":").length<=1){
                    System.out.println(DateUtil.getNow()+"京东拽单："+result);
                }
                String outerOrderCode = "";
                if(result.split(":").length<=1){
                    System.out.println(DateUtil.getNow()+"京东拽单："+result);
                }
        		ResultSet rs = dbop.executeQuery("select outer_sale_order_code from mmb_outer_relation where outer_swap_order_code='" + result.split(":")[1] + "'"); 
				if (rs.next()) {
					outerOrderCode = rs.getString(1);
				}
				if (outerOrderCode.equals("")) {
					rs.close();
					return "没有找到原销售单！";
				}
				rs.close();
				float sellerPrice = 0f;
				float couponPrice = 0f;
				JdClient client=new DefaultJdClient(OrderStockBean.SERVER_URL,OrderStockBean.accessToken,OrderStockBean.appKey,OrderStockBean.appSecret); 
				OrderFbpGetRequest request=new OrderFbpGetRequest();
				request.setOrderId( outerOrderCode );
				request.setOptionalFields( optionalFields );
				OrderFbpGetResponse response=client.execute(request);
				if (response == null) {
					return "京东订单get为空";
				}
				
				if (response.getOrderDetailInfo() == null) {
					return "京东订单search的OrderDetailInfo为空";
				}
				OrderInfo oi = response.getOrderDetailInfo().getOrderInfo();
				if (oi == null) {
					return "该订单不存在！";
				}
				List<CouponDetail> couponDetailList = oi.getCouponDetailList();
				List<ItemInfo> swapitemList = osInfo.getItemInfoList();
				List<ItemInfo> outeritemList = oi.getItemInfoList();
				//换货单是否存在原销售单没有的商品
				boolean flag = parentNoSku(swapitemList, outeritemList);
				if (flag) {
					return noSku;
				}
				float itemCouponPrice = 0f;
				for (ItemInfo outeritem : outeritemList) {
					for (ItemInfo swapitem : swapitemList) {
						if (outeritem.getSkuId().equals(swapitem.getSkuId()) ) {
							itemCouponPrice = 0f;
							for (CouponDetail cd : couponDetailList) {
								if (cd.getSkuId().equals(outeritem.getSkuId()) && this.isOurCoupon(cd.getCouponType())) {
									itemCouponPrice = Arith.div(StringUtil.toFloat(cd.getCouponPrice()), StringUtil.toFloat(outeritem.getItemTotal()));
									couponPrice = Arith.add(couponPrice, Arith.mul(Arith.div(StringUtil.toFloat(cd.getCouponPrice()), StringUtil.toFloat(outeritem.getItemTotal())), StringUtil.toFloat(swapitem.getItemTotal())));
								}
							}
							itemPriceMap.put(swapitem.getSkuId(), Arith.round(Arith.sub(StringUtil.toFloat(outeritem.getJdPrice()), itemCouponPrice),2));
							sellerPrice = Arith.add(sellerPrice, Arith.mul(StringUtil.toFloat(outeritem.getJdPrice()), StringUtil.toFloat(swapitem.getItemTotal())));
						}
					}
				}
				osInfo.setOrderSellerPrice(Arith.round(Arith.sub(sellerPrice, couponPrice), 2) + "");
				order.setDprice(StringUtil.toFloat(osInfo.getOrderSellerPrice()) + StringUtil.toFloat(osInfo.getFreightPrice()));
				order.setPrice(sellerPrice  + StringUtil.toFloat(osInfo.getFreightPrice()));
				outerSaleOrderCode = oi.getOrderId();
        	} else {
        		return "京东订单类型错误，return_order为" + osInfo.getReturnOrder();
        	}
        	String postCode = "";
        	ResultSet rs = service.getDbOperation().executeQuery("select postcode from city_postcode cp where cp.city like '" + (specialCitys.contains(userInfo.getProvince()) ? userInfo.getProvince() : userInfo.getCity()) + "%'");
            if (rs.next()) {
            	postCode = rs.getString(1);
            }
            order.setPostcode(postCode);
            id = service.addOrder(order);
            if (id <= 0) {
            	return "添加订单失败！";
            }
            order.setId(id);
            
            service.getDbOperation().executeUpdate("update user_order set create_datetime='" + osInfo.getOrderStartTime() + "',stockout_remark='京东：" + osInfo.getOrderId() + "' where id=" + order.getId());
            
            if (!dbop.executeUpdate("INSERT INTO `mmb_outer_relation` (`outer_sale_order_code`, `outer_swap_order_code`, `mmb_order_code`, `mmb_order_id`) "
    				+ "VALUES ('" + outerSaleOrderCode + "', '" + osInfo.getOrderId() + "', '" + order.getCode() + "', " + id + ")")) {
            	return "添加订单关系失败！";
    		}
            int provinceId = -1;
            int cityId = -1;
            int areaId = -1;
            rs = service.getDbOperation().executeQuery("select id from provinces where name like '" + userInfo.getProvince() + "%'");
            if (rs.next()) {
            	provinceId = rs.getInt(1);
            }
            
            rs = service.getDbOperation().executeQuery("select id from province_city where province_id=" + provinceId + " and city like '" + (specialCitys.contains(userInfo.getProvince()) ? userInfo.getProvince() : userInfo.getCity()) + "%'");
            if (rs.next()) {
            	cityId = rs.getInt(1);
            }
            
            rs = service.getDbOperation().executeQuery("select id from city_area where city_id=" + cityId + " and area = '" + (specialCitys.contains(userInfo.getProvince()) ? userInfo.getCity() : userInfo.getCounty()) + "'");
            if (rs.next()) {
            	areaId = rs.getInt(1);
            }
            
            //新增订单扩展信息
            voOrderExtendInfo extendInfo = new voOrderExtendInfo();
            extendInfo.setId(id);
            extendInfo.setOrderCode(order.getCode());
            extendInfo.setOrderPrice(order.getDprice());
            extendInfo.setPayMode(order.getBuyMode());
            extendInfo.setAddId1(provinceId);
            extendInfo.setAddId2(cityId);
            extendInfo.setAddId3(areaId);
            extendInfo.setAddId4(-1);
            extendInfo.setAdd5(areaId == -1 ? userInfo.getCounty() :"");
            if(!service.addOrderExtendInfo(extendInfo)){
            	return "添加订单扩展信息失败！";
            }
            List<ItemInfo> itemList = osInfo.getItemInfoList();
            int count = outer_mmb_sku.size();
            for (ItemInfo item : itemList) {
            	if (item.getOuterSkuId().equals("")) {
            		for (int i = 0 ; i < count; i ++) {
            			if (item.getSkuId().equals(outer_mmb_sku.get(i).get("outer"))) {
            				item.setOuterSkuId(outer_mmb_sku.get(i).get("mmb"));
            				break;
            			}
            		}
            	}
            	if (item.getOuterSkuId().equals("")) {
            		return "商品编号为空！";
            	}
            	voProduct product = wareService.getProduct(item.getOuterSkuId());
    			if(product == null){
    				return "商品不存在！";
    			}
    			
    			if (itemPriceMap.get(item.getSkuId()) == null) {
    				return "商品价格不存在！";
    			}
    			PromotionProductBean ppb = new PromotionProductBean();
    			ppb.setCount(StringUtil.StringToId(item.getItemTotal()));
    			ppb.setMmbprice(product.getPrice());
    			ppb.setDisprice(itemPriceMap.get(item.getSkuId()));
    			ppb.setProductId(product.getId());
    			ppb.setType(0);
    			ppb.setPromotionId(0);
    			ppb.setProductCode(product.getCode());
    			//添加到user_order_product表
    			String result = this.addOrderProduct(id, product.getCode(),ppb.getCount(),ppb.getMmbprice(),ppb.getDisprice(), wareService);
    			if (result != null) {
            		return result;
            	}

    			// 添加记录到user_order_promotion_product
    			int productId = product.getId();
    			int ptype = ppb.getType();
    			float addpricebyapib = ppb.getMmbprice();
    			float adddpricebyapib = ppb.getDisprice();
    			int point = ppb.getPoint();
    			int promotionId = ppb.getPromotionId();

    			result = this.addOrderPromotionProduct(id, productId,0, 
    					promotionId,ppb.getCount(), ptype, addpricebyapib,adddpricebyapib, point, wareService);
    			if (result != null) {
            		return result;
            	}
            }
            
            userOrderService.logUserOrderProduct(order, UserOrderProductHistoryBean.TYPE_NORMAL);
            
            Json j = this.addOrderStock(endTime, order, wareService, dbOpSalve);
            if (!j.isSuccess()) {
            	return j.getMsg();
            }
            OrderStockBean osBean = (OrderStockBean) j.getObj();
            
          //获取出库单对应的商品的列表
			String condition = "order_stock_id = " + osBean.getId();
			ArrayList<OrderStockProductBean> ospbList = stockService.getOrderStockProductList(condition, 0, -1, "id");
			//添加order_stock_product_cargo,audit_package 
			boolean result = this.addOrderStockProductCargo(osBean, ospbList, wareService);
			if(!result){
				return "货位库存不足";
			}
            // 添加核对包裹记录audit_package
			AuditPackageBean apBean = new AuditPackageBean();
			apBean.setOrderId(order.getId());
			apBean.setOrderCode(order.getCode());
			apBean.setSortingDatetime(osInfo.getOrderEndTime());
			apBean.setSortingUserName("");
			apBean.setAreano(osBean.getStockArea());
			apBean.setDeliver(osBean.getDeliver());
			apBean.setStatus(AuditPackageBean.STATUS3);
			apBean.setCheckUserName("");
			apBean.setAuditPackageUserName("");
			apBean.setPackageCode(order.getPackageNum());
			if (!stockService.addAuditPackage(apBean)) {
				return "添加核对包裹信息失败！";
			}
			
			//计算系统生成物流成本
			PrintPackageAction ppa1 = new PrintPackageAction();
			String resultString = ppa1.calLogisticsCost(stockService, order, apBean);
			if (resultString != null) {
				return resultString;
			}
			
			//出库完成
			resultString = this.completeOrderStock(osBean, wareService);
			if (resultString != null) {
				return resultString;
			}
        }catch(Exception e){
        	e.printStackTrace();
        	return "系统异常，请联系管理员！";
        }
        return null;
	}
	
	public String addOuterOrderInfo(UserGroupBean group,OrderInfo osInfo, DbOperation dbop,DbOperation dbOpSalve, int before, int after) {
		String endTime = osInfo.getOrderEndTime();
		UserInfo userInfo = osInfo.getConsigneeInfo();
		voOrder order = new voOrder();
		order.setName(userInfo.getFullname());
		order.setPhone(userInfo.getTelephone());
		order.setPhone2(userInfo.getMobile());
		order.setAddress(userInfo.getFullAddress());
		order.setPhoneStatus(5 + "");
		order.setBuyMode(2);
		order.setStatus(14);
		order.setPackageNum(osInfo.getOrderId());
		order.setFlat(4);
//		order.setFlag();
		order.setOperator("");
		order.setCode("Q" + CodeUtil.getSerialCode());
		order.setFr(jdfr);
		order.setDeliverType(2);
		order.setDiscount(1);
		order.setRemitType(0);
		order.setPrepayDeliver(0);
		order.setAreano(0);
		order.setPrePayType(0);
		order.setIsOlduser(0);
		order.setSuffix(0);
		order.setWebRemark("");
		order.setRemark("", order.getPhoneStatus(), "", osInfo.getOrderId(),osInfo.getFreightPrice());
		order.setDprice(StringUtil.toFloat(osInfo.getOrderSellerPrice()) + StringUtil.toFloat(osInfo.getFreightPrice()));
		order.setPrice(StringUtil.toFloat(osInfo.getOrderTotalPrice()) + StringUtil.toFloat(osInfo.getFreightPrice()));
		order.setPostage(Float.valueOf(osInfo.getFreightPrice()));
		order.setConsigner("");

        int id = 0;
        IAdminService service = ServiceFactory.createAdminService(dbop);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOperation());
        WareService wareService = new WareService(dbop);
        UserOrderServiceImpl userOrderService = new UserOrderServiceImpl(IBaseService.CONN_IN_SERVICE,service.getDbOperation());
        try {
        	List<HashMap<String, String>> outer_mmb_sku = new ArrayList<HashMap<String, String>>();
			ResultSet rs2 = dbop.executeQuery("select outer_sku,mmb_sku from mmb_outer_relation_sku");
			while (rs2.next()) {
				HashMap<String, String>  map = new HashMap<String, String>();
				map.put("outer",rs2.getString(1));
				map.put("mmb", rs2.getString(2));
				outer_mmb_sku.add(map);
			}
			rs2.close();
        	String outerSaleOrderCode = osInfo.getOrderId();
        	Map<String, Float> itemPriceMap = new HashMap<String, Float>();
        	//销售订单
        	if (osInfo.getReturnOrder().equals("0")) {
        		List<CouponDetail> couponDetailList = osInfo.getCouponDetailList();
				List<ItemInfo> outeritemList = osInfo.getItemInfoList();
				float itemCouponPrice = 0f;
        		for (ItemInfo outeritem : outeritemList) {
        			itemCouponPrice = 0f;
					for (CouponDetail cd : couponDetailList) {
						if (cd.getSkuId().equals(outeritem.getSkuId()) && this.isOurCoupon(cd.getCouponType())) {
							itemCouponPrice = Arith.div(StringUtil.toFloat(cd.getCouponPrice()), StringUtil.toFloat(outeritem.getItemTotal()));
						}
					}
					itemPriceMap.put(outeritem.getSkuId(), Arith.sub(StringUtil.toFloat(outeritem.getJdPrice()),itemCouponPrice));
				}
        	} else if (osInfo.getReturnOrder().equals("1") || osInfo.getReturnOrder().equals("2")) {
        		//换货单
        		String remark = osInfo.getOrderRemark();
        		
        		String result = "";  
                Pattern pattern = Pattern.compile("原订单号:[0-9]{0,}");  
                Matcher matcher = pattern.matcher(remark);  
                if (matcher.find()) {  
                    result = matcher.group(0);//只取第一组  
                }
                String outerOrderCode = "";
        		ResultSet rs = dbop.executeQuery("select outer_sale_order_code from mmb_outer_relation where outer_swap_order_code='" + result.split(":")[1] + "'"); 
				if (rs.next()) {
					outerOrderCode = rs.getString(1);
				}
				if (outerOrderCode.equals("")) {
					rs.close();
					return "没有找到原销售单！";
				}
				rs.close();
				float sellerPrice = 0f;
				float couponPrice = 0f;
				JdClient client=new DefaultJdClient(OrderStockBean.SERVER_URL,OrderStockBean.accessToken,OrderStockBean.appKey,OrderStockBean.appSecret); 
				OrderFbpGetRequest request=new OrderFbpGetRequest();
				request.setOrderId( outerOrderCode );
				request.setOptionalFields( optionalFields );
				OrderFbpGetResponse response=client.execute(request);
				if (response == null) {
					return "京东订单get为空";
				}
				
				if (response.getOrderDetailInfo() == null) {
					return "京东订单search的OrderDetailInfo为空";
				}
				OrderInfo oi = response.getOrderDetailInfo().getOrderInfo();
				if (oi == null) {
					return "该订单不存在！";
				}
				List<CouponDetail> couponDetailList = oi.getCouponDetailList();
				List<ItemInfo> swapitemList = osInfo.getItemInfoList();
				List<ItemInfo> outeritemList = oi.getItemInfoList();
				boolean flag = parentNoSku(swapitemList, outeritemList);
				if (flag) {
					if (!group.isFlag(2212)) {
						return "订单中存在与原销售单不存在商品，你没有输入商品价格权限！";
					}
					if (before > 0 && after > 0) {
						order.setDprice(after);
						order.setPrice(before);
						
						float mmbAllPrice = 0f;
						int count = outer_mmb_sku.size();
						for (ItemInfo swapitem : swapitemList) {
							if (swapitem.getOuterSkuId().equals("")) {
			            		for (int i = 0 ; i < count; i ++) {
			            			if (swapitem.getSkuId().equals(outer_mmb_sku.get(i).get("outer"))) {
			            				swapitem.setOuterSkuId(outer_mmb_sku.get(i).get("mmb"));
			            				break;
			            			}
			            		}
			            	}
			            	if (swapitem.getOuterSkuId().equals("")) {
			            		return "商品编号为空！";
			            	}
			            	voProduct product = wareService.getProduct(swapitem.getOuterSkuId());
			    			if(product == null){
			    				return "商品不存在！";
			    			}
							mmbAllPrice = Arith.add(Arith.mul(product.getPrice(), StringUtil.toFloat(swapitem.getItemTotal())),mmbAllPrice);
						}
						for (ItemInfo swapitem : swapitemList) {
							if (swapitem.getOuterSkuId().equals("")) {
			            		for (int i = 0 ; i < count; i ++) {
			            			if (swapitem.getSkuId().equals(outer_mmb_sku.get(i).get("outer"))) {
			            				swapitem.setOuterSkuId(outer_mmb_sku.get(i).get("mmb"));
			            				break;
			            			}
			            		}
			            	}
			            	if (swapitem.getOuterSkuId().equals("")) {
			            		return "商品编号为空！";
			            	}
			            	voProduct product = wareService.getProduct(swapitem.getOuterSkuId());
			    			if(product == null){
			    				return "商品不存在！";
			    			}
							itemPriceMap.put(swapitem.getSkuId(), Arith.round(Arith.mul(Arith.div(after,mmbAllPrice), product.getPrice()),2));
						}
					} else {
						return "折扣前和折扣后价格必须大于0！";
					}
				} else {
					float itemCouponPrice = 0f;
					for (ItemInfo outeritem : outeritemList) {
						for (ItemInfo swapitem : swapitemList) {
							if (outeritem.getSkuId().equals(swapitem.getSkuId()) ) {
								itemCouponPrice = 0f;
								for (CouponDetail cd : couponDetailList) {
									if (cd.getSkuId().equals(outeritem.getSkuId()) && this.isOurCoupon(cd.getCouponType())) {
										itemCouponPrice = Arith.div(StringUtil.toFloat(cd.getCouponPrice()), StringUtil.toFloat(outeritem.getItemTotal()));
										couponPrice = Arith.add(couponPrice, Arith.mul(Arith.div(StringUtil.toFloat(cd.getCouponPrice()), StringUtil.toFloat(outeritem.getItemTotal())), StringUtil.toFloat(swapitem.getItemTotal())));
									}
								}
								itemPriceMap.put(swapitem.getSkuId(), Arith.round(Arith.sub(StringUtil.toFloat(outeritem.getJdPrice()), itemCouponPrice),2));
								sellerPrice = Arith.add(sellerPrice, Arith.mul(StringUtil.toFloat(outeritem.getJdPrice()), StringUtil.toFloat(swapitem.getItemTotal())));
							}
						}
					}
					osInfo.setOrderSellerPrice(Arith.round(Arith.sub(sellerPrice, couponPrice), 2) + "");
					order.setDprice(StringUtil.toFloat(osInfo.getOrderSellerPrice()) + StringUtil.toFloat(osInfo.getFreightPrice()));
					order.setPrice(sellerPrice  + StringUtil.toFloat(osInfo.getFreightPrice()));
				}
				outerSaleOrderCode = oi.getOrderId();
        	} else {
        		return "京东订单类型错误，return_order为" + osInfo.getReturnOrder();
        	}
        	String postCode = "";
        	ResultSet rs = service.getDbOperation().executeQuery("select postcode from city_postcode cp where cp.city like '" + (specialCitys.contains(userInfo.getProvince()) ? userInfo.getProvince() : userInfo.getCity()) + "%'");
            if (rs.next()) {
            	postCode = rs.getString(1);
            }
            order.setPostcode(postCode);
            id = service.addOrder(order);
            if (id <= 0) {
            	return "添加订单失败！";
            }
            order.setId(id);
            
            service.getDbOperation().executeUpdate("update user_order set create_datetime='" + osInfo.getOrderStartTime() + "',stockout_remark='京东：" + osInfo.getOrderId() + "' where id=" + order.getId());
            
            if (!dbop.executeUpdate("INSERT INTO `mmb_outer_relation` (`outer_sale_order_code`, `outer_swap_order_code`, `mmb_order_code`, `mmb_order_id`) "
    				+ "VALUES ('" + outerSaleOrderCode + "', '" + osInfo.getOrderId() + "', '" + order.getCode() + "', " + id + ")")) {
            	return "添加订单关系失败！";
    		}
            int provinceId = -1;
            int cityId = -1;
            int areaId = -1;
            rs = service.getDbOperation().executeQuery("select id from provinces where name like '" + userInfo.getProvince() + "%'");
            if (rs.next()) {
            	provinceId = rs.getInt(1);
            }
            
            rs = service.getDbOperation().executeQuery("select id from province_city where province_id=" + provinceId + " and city like '" + (specialCitys.contains(userInfo.getProvince()) ? userInfo.getProvince() : userInfo.getCity()) + "%'");
            if (rs.next()) {
            	cityId = rs.getInt(1);
            }
            
            rs = service.getDbOperation().executeQuery("select id from city_area where city_id=" + cityId + " and area = '" + (specialCitys.contains(userInfo.getProvince()) ? userInfo.getCity() : userInfo.getCounty()) + "'");
            if (rs.next()) {
            	areaId = rs.getInt(1);
            }
            
            //新增订单扩展信息
            voOrderExtendInfo extendInfo = new voOrderExtendInfo();
            extendInfo.setId(id);
            extendInfo.setOrderCode(order.getCode());
            extendInfo.setOrderPrice(order.getDprice());
            extendInfo.setPayMode(order.getBuyMode());
            extendInfo.setAddId1(provinceId);
            extendInfo.setAddId2(cityId);
            extendInfo.setAddId3(areaId);
            extendInfo.setAddId4(-1);
            extendInfo.setAdd5(areaId == -1 ? userInfo.getCounty() :"");
            if(!service.addOrderExtendInfo(extendInfo)){
            	return "添加订单扩展信息失败！";
            }
            int count = outer_mmb_sku.size();
            List<ItemInfo> itemList = osInfo.getItemInfoList();
            for (ItemInfo item : itemList) {
            	if (item.getOuterSkuId().equals("")) {
            		for (int i = 0 ; i < count; i ++) {
            			if (item.getSkuId().equals(outer_mmb_sku.get(i).get("outer"))) {
            				item.setOuterSkuId(outer_mmb_sku.get(i).get("mmb"));
            				break;
            			}
            		}
            	}
            	if (item.getOuterSkuId().equals("")) {
            		return "商品编号为空！";
            	}
            	voProduct product = wareService.getProduct(item.getOuterSkuId());
    			if(product == null){
    				return "商品不存在！";
    			}
    			
    			if (itemPriceMap.get(item.getSkuId()) == null) {
    				return "商品价格不存在！";
    			}
    			PromotionProductBean ppb = new PromotionProductBean();
    			ppb.setCount(StringUtil.StringToId(item.getItemTotal()));
    			ppb.setMmbprice(product.getPrice());
    			ppb.setDisprice(itemPriceMap.get(item.getSkuId()));
    			ppb.setProductId(product.getId());
    			ppb.setType(0);
    			ppb.setPromotionId(0);
    			ppb.setProductCode(product.getCode());
    			//添加到user_order_product表
    			String result = this.addOrderProduct(id, product.getCode(),ppb.getCount(),ppb.getMmbprice(),ppb.getDisprice(), wareService);
    			if (result != null) {
            		return result;
            	}

    			// 添加记录到user_order_promotion_product
    			int productId = product.getId();
    			int ptype = ppb.getType();
    			float addpricebyapib = ppb.getMmbprice();
    			float adddpricebyapib = ppb.getDisprice();
    			int point = ppb.getPoint();
    			int promotionId = ppb.getPromotionId();

    			result = this.addOrderPromotionProduct(id, productId,0, 
    					promotionId,ppb.getCount(), ptype, addpricebyapib,adddpricebyapib, point, wareService);
    			if (result != null) {
            		return result;
            	}
            }
            
            userOrderService.logUserOrderProduct(order, UserOrderProductHistoryBean.TYPE_NORMAL);
            
            Json j = this.addOrderStock(endTime, order, wareService, dbOpSalve);
            if (!j.isSuccess()) {
            	return j.getMsg();
            }
            OrderStockBean osBean = (OrderStockBean) j.getObj();
            
          //获取出库单对应的商品的列表
			String condition = "order_stock_id = " + osBean.getId();
			ArrayList<OrderStockProductBean> ospbList = stockService.getOrderStockProductList(condition, 0, -1, "id");
			//添加order_stock_product_cargo,audit_package 
			boolean result = this.addOrderStockProductCargo(osBean, ospbList, wareService);
			if(!result){
				return "货位库存不足";
			}
            // 添加核对包裹记录audit_package
			AuditPackageBean apBean = new AuditPackageBean();
			apBean.setOrderId(order.getId());
			apBean.setOrderCode(order.getCode());
			apBean.setSortingDatetime(osInfo.getOrderEndTime());
			apBean.setSortingUserName("");
			apBean.setAreano(osBean.getStockArea());
			apBean.setDeliver(osBean.getDeliver());
			apBean.setStatus(AuditPackageBean.STATUS3);
			apBean.setCheckUserName("");
			apBean.setAuditPackageUserName("");
			apBean.setPackageCode(order.getPackageNum());
			if (!stockService.addAuditPackage(apBean)) {
				return "添加核对包裹信息失败！";
			}
			
			//计算系统生成物流成本
			PrintPackageAction ppa1 = new PrintPackageAction();
			String resultString = ppa1.calLogisticsCost(stockService, order, apBean);
			if (resultString != null) {
				return resultString;
			}
			
			//出库完成
			resultString = this.completeOrderStock(osBean, wareService);
			if (resultString != null) {
				return resultString;
			}
        }catch(Exception e){
        	e.printStackTrace();
        	return "系统异常，请联系管理员！";
        }
        return null;
	}
	
	public String completeOrderStock(OrderStockBean bean, WareService wareService) {
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		DbOperation db = service.getDbOp();
		try {

			String condition = "order_stock_id = " + bean.getId();
			ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");
	
			if(!service.updateOrderStock("status = " + OrderStockBean.STATUS3 + ", last_oper_time = now()", "id = " + bean.getId())){
				return "订单出库状态更新操作失败！";
			}
	
			voOrder order = wareService.getOrder("code='" + bean.getOrderCode() + "'");
			if(order.getStatus() == 3){ //如果订单是代发货状态，才自动修改订单的状态为已发货或已结算，并添加订单状态修改日志
				int oriStatus = order.getStatus();
				int status = 6;
				switch(order.getBuyMode()){
				case 0:
					status = 6;
					break;
				case 1:
				case 2:
					//邮购"已到款"和上门自取"已发货"订单做了出货时,订单状态自动修改成"已妥投",以前是"已结算"
					status = 14;
					break;
				default:
					status = 6;
				}
				// 如果该订单状态是“待发货”（邮购是“已到款”），则将该订单状态改为“已发货”
				// 非邮购订单变为 已发货
				if(!service.getDbOp().executeUpdate("update user_order set status=" + status + " where status=3 and code='" + bean.getOrderCode() + "'")){
					return "订单状态更新操作失败！";
				}
				order.setStatus(status);
	
				//订单发货状态 改为 发货成功
				if(!service.getDbOp().executeUpdate("update user_order set stockout_deal=" + OrderStockStatusBean.STATUS_STOCKOUT_SUCCESS + " where code='" + bean.getOrderCode() + "'")){
					return "订单发货状态更新操作失败！";
				}
				order.setStockoutDeal(OrderStockStatusBean.STATUS_STOCKOUT_SUCCESS);
	
				StringBuilder logContent = new StringBuilder();
				// 如果修改了订单的状态，就记录操作日志
				logContent.append("[订单状态:");
				logContent.append(oriStatus);
				logContent.append("->");
				logContent.append(status);
				logContent.append("]");
				if(logContent.length() > 0){
					OrderAdminLogBean oalog = new OrderAdminLogBean();
					oalog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
					oalog.setUserId(0);
					oalog.setUsername("");
					oalog.setOrderId(order.getId());
					oalog.setOrderCode(order.getCode());
					oalog.setCreateDatetime(bean.getCreateDatetime());
					oalog.setContent(logContent.toString());
					wareService.addOrderAdminStatusLog(logService, oriStatus, status, -1, -1, oalog);
					if(!logService.addOrderAdminLog(oalog)){
						return "订单日志添加操作失败！";
					}
				}
			}
			OrderStockProductBean sh = null;
			voProduct product = null;
			String set = null;
			List<BaseProductInfo> baseProductInfoList = new ArrayList<BaseProductInfo>();
			Iterator itr = shList.iterator();
			while (itr.hasNext()) {
				sh = (OrderStockProductBean) itr.next();
				//出库
				product = wareService.getProduct(sh.getProductId());
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
				ProductStockBean ps = psService.getProductStock("id=" + sh.getStockoutId());
	
				set = "status = " + OrderStockProductBean.DEALED
				+ ", remark = concat(remark, '(操作前锁定库存" + ps.getLockCount()
				+ ",操作后锁定库存" + (ps.getLockCount() - sh.getStockoutCount())
				+ ")'), deal_datetime = now()";
				if(!service.updateOrderStockProduct(set, "id = " + sh.getId())){
					return "出库商品信息更新失败！";
				}
				if(!psService.updateProductLockCount(sh.getStockoutId(), -sh.getStockoutCount())){
					return "库存操作失败，可能是库存不足，请与管理员联系！";
				}
	
				//更新货位库存记录
				product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
				List ospcList = service.getOrderStockProductCargoList("order_stock_product_id = "+sh.getId(), -1, -1, "id asc");
				if(ospcList == null||ospcList.size() == 0){
					return "订单出库无相应货位信息，请与管理员联系！";
				}
				Iterator ospcIter = ospcList.listIterator();
				while(ospcIter.hasNext()){
					OrderStockProductCargoBean ospc = (OrderStockProductCargoBean)ospcIter.next();
					CargoProductStockBean cps = null;
					cps = cargoService.getCargoProductStock("id = "+ospc.getCargoProductStockId());
					if(!cargoService.updateCargoProductStockLockCount(cps.getId(), -ospc.getCount())){
						return "货位库存操作失败，货位库存不足！";
					}
	
					//货位出库卡片
					cps = cargoService.getCargoAndProductStock("cps.id = "+cps.getId());
					CargoStockCardBean csc = new CargoStockCardBean();
					csc.setCardType(CargoStockCardBean.CARDTYPE_ORDERSTOCK);
					csc.setCode(bean.getOrderCode());
					csc.setCreateDatetime(DateUtil.getNow());
					csc.setStockType(bean.getStockType());
					csc.setStockArea(bean.getStockArea());
					csc.setProductId(sh.getProductId());
					csc.setStockId(cps.getId());
					csc.setStockOutCount(ospc.getCount());
					csc.setStockOutPriceSum((new BigDecimal(ospc.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					csc.setCurrentStock(product.getStock(bean.getStockArea(), bean.getStockType()) + product.getLockCount(bean.getStockArea(), bean.getStockType()));
					csc.setAllStock(product.getStockAll() + product.getLockCountAll());
					csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
					csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
					csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
					csc.setStockPrice(product.getPrice5());
					csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
					if(!cargoService.addCargoStockCard(csc)){
						return "添加货位进销存卡片失败！";
					}
				}
	
	
				// 审核通过，就加 进销存卡片
				product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
	
				// 出库卡片
				StockCardBean sc = new StockCardBean();
				sc.setCardType(StockCardBean.CARDTYPE_ORDERSTOCK);
				sc.setCode(bean.getOrderCode());
				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(bean.getStockType());
				sc.setStockArea(bean.getStockArea());
				sc.setProductId(sh.getProductId());
				sc.setStockId(sh.getStockoutId());
				sc.setStockOutCount(sh.getStockoutCount());
				//						sc.setStockOutPriceSum(stockOutPrice);
				sc.setStockOutPriceSum((new BigDecimal(sh.getStockoutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
				sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
				sc.setStockAllArea(product.getStock(bean.getStockArea()) + product.getLockCount(bean.getStockArea()));
				sc.setStockAllType(product.getStockAllType(bean.getStockType()) + product.getLockCountAllType(bean.getStockType()));
				sc.setAllStock(product.getStockAll() + product.getLockCountAll());
				sc.setStockPrice(product.getPrice5());
				sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
				if(!psService.addStockCard(sc)){
					return "添加进销存卡片失败！";
				}
				
				BaseProductInfo baseProductInfo = new BaseProductInfo();
				baseProductInfo.setId(sh.getProductId());
				baseProductInfo.setOutCount(sh.getStockoutCount());
				baseProductInfo.setOutPrice(product.getPrice5());
				baseProductInfo.setProductStockId(sh.getStockoutId());
				baseProductInfoList.add(baseProductInfo);
			}
			
			//根据业务类型采集财务基础数据
			FinanceSaleBaseDataService financeBaseDataService = 
					FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(
							FinanceStockCardBean.CARDTYPE_ORDERSTOCK, db.getConn());
			financeBaseDataService.acquireFinanceSaleBaseData(order.getCode(), 1, "", 
					DateUtil.getNow(), bean.getStockType(), bean.getStockArea(), baseProductInfoList);
			
			//修改核对包裹记录
			if(!service.updateAuditPackage("check_datetime='"+bean.getCreateDatetime()+"',check_user_name='',status="+AuditPackageBean.STATUS3, "order_id="+order.getId())){
				return "核对包裹记录更新操作失败！";
			}
			int checkStatus=this.importPackage(order,order.getPackageNum(),db);
			if(checkStatus==4){
				return "快递公司错误！";
			}else if(checkStatus==5){
				return "没有设置结算周期！";
			} else if (checkStatus == 6) {
				return "数据异常！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "异常";
		}
		return null;
	}
		
	public int importPackage(voOrder order,String packageCode,DbOperation dbOp){
		
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService balanceService=ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, dbOp);
		BalanceService bService = new BalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		//cxq---
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, dbOp);
		int checkStatus=6;
		//----------开始导入包裹单
		// 结算单 相关数据的设置
		//设置 结算单的 包裹单号、结算周期、结算来源
		int deliver = order.getDeliver();
		int balanceType = 0;
		OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
		// 根据 快递公司，确定结算来源
		if(voOrder.deliverToBalanceTypeMap.containsKey(deliver+"")){
			balanceType=Integer.parseInt(voOrder.deliverToBalanceTypeMap.get(deliver+"").toString());
		}  else {
			// 如果没有找到 相应的快递公司，就报错
			checkStatus=4;
			return checkStatus;
		}
		
		
		// 根据当前日期 计算 订单所属的结算周期
		MailingBalanceBean mb = balanceService.getMailingBalance("order_id=" + order.getId());
		String stockoutDate = StringUtil.cutString(mb.getStockoutDatetime(), 10);
		StringBuilder qbuf = new StringBuilder();
		qbuf.append("balance_type=").append(balanceType).append(" and ");
		qbuf.append("balance_cycle_start <= '").append(stockoutDate).append("' and balance_cycle_end >= '").append(stockoutDate).append("' ");
		qbuf.delete(0, qbuf.length());
		qbuf.append("balance_status=").append(MailingBalanceBean.BALANCE_STATUS_UNDEAL).append(", ");
		qbuf.append("packagenum='").append(packageCode).append("' ");
		if(!balanceService.updateMailingBalance(qbuf.toString(), "order_id=" + order.getId())){
			checkStatus=6;
			return checkStatus;
		}
		
		// 异常结算数据
		if(!bService.updateFinanceMailingBalanceBean("balance_status = " + MailingBalanceBean.BALANCE_STATUS_UNDEAL + ", packagenum = '" + packageCode + "' ", "order_id=" + order.getId())){
			checkStatus=6;
			return checkStatus;
		}
		
		
		// 结算单 相关数据的设置
		order.setPackageNum(packageCode);
		order.setStockoutRemark("");
		if(!wareService.modifyOrder(order)){
			checkStatus=6;
			return checkStatus;
		}
		order.setStockOper(stockService.getStockOperation("type=0 and status=2 and order_code='" + order.getCode() + "'"));
		StringBuilder buf = new StringBuilder();
		order.setProducts(buf.toString());
		//添加日志
		OrderImportLogBean log = new OrderImportLogBean();
		log.setContent(order.getCode()+"\t"+packageCode);
		log.setCreateDatetime(osBean.getCreateDatetime());
		log.setUserId(0);
		log.setType(0);
		if(!logService.addOrderImportLog(log)){
			checkStatus=6;
			return checkStatus;
		}
			
		if(!stockService.updateAuditPackage("package_code='"+packageCode+"',status=4,audit_package_datetime='"+osBean.getCreateDatetime()+"',audit_package_user_name=''", "order_id="+order.getId())){
			checkStatus=6;
			return checkStatus;
		}
		
		//推送订单包裹信息
		AuditPackageBean auditPackage = stockService.getAuditPackage("order_id = "+order.getId());
		order.setAuditPakcageBean(auditPackage);
		order.setOrderExtendInfo(wareService.getOrderExtendInfo(order.getId()));
		osBean.setOrder(order);
		if(!wareService.pushDeliverOrder(osBean)){
			checkStatus=6;
			return checkStatus;
		}

		//修改发货信息表（财务统计用表）包裹单号
		frfService.updateFinanceSellBean("packagenum = '" + StringUtil.toSql(packageCode) + "'", "order_id =" + order.getId());
		checkStatus=7;
		//-----结束导入包裹单
		return checkStatus;
	}
	
	public Json addOrderStock(String endTime, voOrder order, WareService wareService,DbOperation dbOpSlave) {
		Json j = new Json();
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
		try {
			List orderProductList = wareService.getOrderProducts(order.getId());
			List productList = new ArrayList();
			HashMap<Integer, voOrderProduct> map = new HashMap<Integer, voOrderProduct>();
			Iterator productIter = orderProductList.listIterator();
			while(productIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)productIter.next();
				voProduct product = wareService.getProduct(vop.getProductId());
				if(product.getIsPackage() == 1){ // 如果这个产品是套装
					List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					if(ppList == null || ppList.size() == 0){
						j.setMsg("套装产品信息异常！");
						return j;
					}
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voOrderProduct tempVOP = new voOrderProduct();
						if (map.get(tempVOP.getProductId()) != null) {
							tempVOP.setCount(ppBean.getProductCount() + map.get(tempVOP.getProductId()).getCount());
						} else {
							tempVOP.setCount(ppBean.getProductCount());
						}
						voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
						tempVOP.setProductId(ppBean.getProductId());
						tempVOP.setCode(tempProduct.getCode());
						tempVOP.setName(tempProduct.getName());
						tempVOP.setPrice(tempProduct.getPrice());
						tempVOP.setOriname(tempProduct.getOriname());
						tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
						tempVOP.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
						map.put(tempVOP.getProductId(), tempVOP);
					}
				} else {
					vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
					vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
					if (map.get(vop.getProductId()) != null) {
						vop.setCount(vop.getCount() + map.get(vop.getProductId()).getCount());
					}
					map.put(vop.getProductId(), vop);
				}
			}
			int orderStockArea = ProductStockBean.AREA_JD;
			
			for (int i : map.keySet()) {
				productList.add(map.get(i));
			}
			//判断是否缺货
			Iterator itr = productList.iterator();
			voOrderProduct op = null;
			while (itr.hasNext()) {
				op = (voOrderProduct) itr.next();
				if (op.getCargoStock(orderStockArea, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {//判断货位是否足量
					j.setMsg("货位库存不足！");
					return j;
				}else if (op.getStock(orderStockArea, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
					j.setMsg("库存不足！");
					return j;
				}
			}
	
			String name = order.getCode() + "_" + endTime.substring(0, 10) + "_出货";
			service.getDbOp().startTransaction();
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			
			OrderStockBean bean = new OrderStockBean();
			bean.setCreateDatetime(endTime);
			bean.setName(name);
			bean.setRemark("");
			bean.setStatus(OrderStockBean.STATUS1);
			bean.setOrderCode(order.getCode());
			bean.setOrderId(order.getId());
			bean.setCode("CK" + sdf.format(cal.getTime()));//此处设定order_stock的初始编号
			bean.setStockArea(orderStockArea);
			bean.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
			bean.setProductCount(0);
			
			orderProductList = wareService.getOrderProducts(order.getId());
			Iterator detailIter = orderProductList.listIterator();
			while(detailIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)detailIter.next();
				voProduct product = wareService.getProduct(vop.getProductId());
				if(product.getIsPackage() == 1){ // 如果这个产品是套装
					List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voOrderProduct tempVOP = new voOrderProduct();
						tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
						voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
						tempVOP.setProductId(ppBean.getProductId());
						tempVOP.setCode(tempProduct.getCode());
						tempVOP.setName(tempProduct.getName());
						tempVOP.setPrice(tempProduct.getPrice());
						tempVOP.setOriname(tempProduct.getOriname());
						order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + tempProduct.getBzzhongliang());
					}
				} else {
					order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + product.getBzzhongliang());
				}
			}
			
			//文齐辉修改2011/05/27添加订单出库时自动分配订单的产品分类
			//只计算订单产品，不计赠品 ,如果分类==0
			int productType=getProductType(orderProductList, service.getDbOp());//订单产品分类
			bean.setProductType(productType);
			bean.setLastOperTime(endTime);
			bean.setStatusStock(OrderStockBean.STATUS1_STOCK);
			bean.setCreateUserId(0);
			wareService.getDbOp().executeUpdate("update user_order set order_type=" + productType  + " where id=" + order.getId());
			//根据地址分配快递公司
			int deliver= 48;
			wareService.modifyOrder("deliver="+deliver,"id="+order.getId());
			bean.setDeliver(deliver);
	    	
			//*****
			if (!service.addOrderStock(bean)) {
				j.setMsg("添加出库单失败！");
				return j;
			}
			int id = service.getDbOp().getLastInsertId();
			bean.setId(id);
			
			//此处修改order_stock.code
			String newCode = null;
			if(id > orderLimitCount){
				String strId = String.valueOf(id);
				newCode = strId.substring(strId.length()- orderCodeLength, strId.length());
			} else {
				DecimalFormat df2 = new DecimalFormat("000000");
				newCode = df2.format(id);
			}
	
			StringBuilder updateBuf = new StringBuilder();
			updateBuf.append("update order_stock set code=concat(code,'").append(newCode).append("') where id=").append(id);
			wareService.getDbOp().executeUpdate(updateBuf.toString());
			
			wareService.deleteOrderProductsSplit(bean.getOrderId());
	
			//添加订单中商品的出货记录
			itr = productList.iterator();
			op = null;
			OrderStockProductBean sh = null;
			while (itr.hasNext()) {
				op = (voOrderProduct) itr.next();
				voOrderProduct vop = wareService.getOrderProductSplit(bean.getOrderId(), op.getCode());
				if(vop != null){
					// 如果已经有了这个商品，则增加数量
					if(!wareService.updateOrderProductSplit("count=(count + " + op.getCount() + ") ", vop.getId())){
						j.setMsg("更新split信息失败！");
						return j;
					}
	
				} else {
					if(!wareService.addOrderProductSplit(bean.getOrderId(), op.getCode(), op.getCount())){
						j.setMsg("添加split信息失败！");
						return j;
					}
				}
				sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + op.getProductId());
				if(sh != null){
					sh.setStockoutCount(sh.getStockoutCount() + op.getCount());
					if (!service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId())) {
						j.setMsg("更新商品出库数量失败！");
						return j;
					}
				} else {
					sh = new OrderStockProductBean();
					sh.setCreateDatetime(endTime);
					sh.setDealDatetime(null);
					sh.setOrderStockId(id);
					sh.setProductCode(op.getCode());
					sh.setProductId(op.getProductId());
					sh.setRemark("");
					sh.setStatus(OrderStockProductBean.UNDEAL);
					ProductStockBean ps = psService.getProductStock("product_id=" + op.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
					sh.setStockoutId(ps.getId());
					sh.setStockoutCount(op.getCount());
					sh.setStockArea(bean.getStockArea());
					sh.setStockType(bean.getStockType());
					if(!service.addOrderStockProduct(sh)){
						j.setMsg("添加商品出库信息失败！");
						return j;
					}
				}
			}
			
			//计算订单中的sku数量，印刷品除外
	    	int skuCount=getProductCount(order,wareService.getDbOp());
	    	if(!service.updateOrderStock("product_count="+skuCount, "id="+bean.getId())){
	    		j.setMsg("添加出库单商品数量失败！");
				return j;
			}
	    	wareService.modifyOrder("product_count="+skuCount,"id="+order.getId());
	    	
			// 订单 确认申请出货
			String condition = "order_stock_id = " + bean.getId() + " and status = " + OrderStockProductBean.UNDEAL;
			ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");
			voProduct product = null;
			String set = null;
			itr = shList.iterator();
	
			bean.setOrder(wareService.getOrder("code='" + bean.getOrderCode() + "'"));
			if (bean.getOrder() == null) {
				j.setMsg("订单信息不存在！");
				return j;
			}
			orderProductList = wareService.getOrderProductsSplit(bean.getOrder().getId());
			Iterator iter = orderProductList.listIterator();
			while (iter.hasNext()) {
				voOrderProduct vop = (voOrderProduct) iter.next();
				vop.setPsList(psService.getProductStockList("product_id="
						+ vop.getProductId(), -1, -1, null));
				vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
			}
			bean.setStatusStock(OrderStockBean.STATUS2_FROM_STOCK);
	
			while (itr.hasNext()) {
				sh = (OrderStockProductBean) itr.next();
				// 出库
				product = wareService.getProduct(sh.getProductId());
				ProductStockBean ps = psService.getProductStock("id="
						+ sh.getStockoutId());
	
				if (sh.getStockoutCount() > ps.getStock()) {
					if(order.getStockoutDeal() == 7){
						service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
						service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+endTime+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+endTime+"',stockout_deal = 4");
					}
				}
				set = "status = " + OrderStockProductBean.DEALED
				+ ", remark = concat(remark, '(操作前库存"
				+ ps.getStock() + "锁定库存" + ps.getLockCount()
				+ ",操作后库存"
				+ (ps.getStock() - sh.getStockoutCount()) + "锁定库存"
				+ (ps.getLockCount() + sh.getStockoutCount())
				+ ")'), deal_datetime = now()";
				service.updateOrderStockProduct(set, "id = " + sh.getId());
				if (!psService.updateProductStockCount(sh.getStockoutId(),
						-sh.getStockoutCount())) {
					j.setMsg("减少可用量失败！");
					return j;
				}
				if (!psService.updateProductLockCount(sh.getStockoutId(),
						sh.getStockoutCount())) {
					j.setMsg("增加锁定量失败！");
					return j;
				}
	
				// 订单商品表、订单赠品表 中记录的价格是 出库瞬间的 库存价格
				service.getDbOp().executeUpdate("update user_order_product set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
				service.getDbOp().executeUpdate("update user_order_present set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
				service.getDbOp().executeUpdate("update user_order_product_split set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
				service.getDbOp().executeUpdate("update user_order_present_split set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
			}
	
			service.updateOrderStock("status_stock="
					+ bean.getStatusStock() + ", last_oper_time = now()",
					"id = " + bean.getId());
	
			service.updateOrderStock("status = " + OrderStockBean.STATUS2
					+ ", last_oper_time = now()", "id = " + bean.getId());
	
			service.getDbOp().executeUpdate(
					"update user_order set stockout = 1, confirm_datetime=now() where code = '"
					+ bean.getOrderCode() + "'");
			if (bean.getOrder().getAddress().indexOf("电话通知") == -1) {
				service.getDbOp().executeUpdate(
						"update user_order set address=concat(address, '(电话通知)') where code = '"
						+ bean.getOrderCode() + "'");
			}
			order = wareService.getOrder("code='" + bean.getOrderCode() + "'");
	
			service.getDbOp().executeUpdate("update user_order set stockout_deal=2 where id=" + order.getId());
	
			int stockoutStatus = 2;
	
			service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal="+stockoutStatus+" where id=" + order.getId());
			j.setObj(bean);
			j.setSuccess(true);
			return j;
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg("异常");
			return j;
		}
	}
	
	/**
	 * 得到订单商品分类
	 * @return
	 */
	public int getProductType(List orderProductList,DbOperation dbOp){
		IUserOrderService userOrderService = ServiceFactory.createUserOrderService(IBaseService.CONN_IN_SERVICE, dbOp);
		Iterator tmpIter = orderProductList.iterator();
		float tmpPrice=0f;
		int orderPType=0;
		while(tmpIter.hasNext()){
			voOrderProduct vop = (voOrderProduct)tmpIter.next();
			UserOrderPackageTypeBean uopType = userOrderService.getUserOrderPackageType("product_catalog = "+vop.getParentId1());
			if(uopType!=null){
				float productPrice = vop.getCount()*vop.getDiscountPrice();
				if(productPrice>tmpPrice){
					tmpPrice = productPrice;
					orderPType = uopType.getTypeId();
				}
			}
		}
		return orderPType;
	}
	
	/**
	 * 得到订单商品数量
	 */
	public int getProductCount(voOrder order,DbOperation dbOp){
		String dmSql ="select type_id as id from user_order_package_type where name ='印刷品'";
    	ResultSet rsDm=dbOp.executeQuery(dmSql);
    	String isDm = "";
    	int skuCount = 0;
    	try {
			if (rsDm.next()) {
				isDm = " AND (e.product_type_id<>"+rsDm.getInt("id") +" or e.product_type_id is null)";
			}
			rsDm.close();
			String skuCountSql ="SELECT COUNT(d.id) AS skuCount FROM user_order a " +
		            			"JOIN order_stock b ON a.id=b.order_id " +
		            			"JOIN order_stock_product c ON b.id=c.order_stock_id " +
		            			"JOIN product d ON c.product_id=d.id " +
	    	                    "LEFT JOIN product_ware_property e ON d.id=e.product_id " +
	    	                    "WHERE b.status<>3 AND a.id="+ order.getId()+isDm;
	    	ResultSet rsSkuCount=dbOp.executeQuery(skuCountSql);
	    	
	    	if (rsSkuCount.next()) {
	    		skuCount = rsSkuCount.getInt("skuCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return skuCount;
	}
	
	/**
	 * 添加order_stock_product_cargo
	 */
	public boolean addOrderStockProductCargo(OrderStockBean osBean,List<OrderStockProductBean> ospbList,WareService wareService) throws Exception{
		boolean result = true;
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		boolean stockCheck = true;
		// 查找区域下库存货位
		HashMap osMap = new HashMap();
		int flag=0;
		for (int k = 0; k < ospbList.size(); k++) {
			OrderStockProductBean ospBean = (OrderStockProductBean) ospbList.get(k);
			String sql1 = "select cps.id,cps.cargo_id,ci.whole_code,ci.stock_area_id,cps.stock_count "+
					" from cargo_product_stock cps "+
					" join cargo_info ci on ci.id=cps.cargo_id "+
					" where cps.product_id=" + ospBean.getProductId() + " and ci.area_id = "+ospBean.getStockArea() + 
					" and ci.store_type=4 and cps.stock_count >= " + ospBean.getStockoutCount();
			List cargoProductCargoList = new ArrayList();
			ResultSet rs1 = siService.getDbOp().executeQuery(sql1);
			while (rs1.next()) {
				CargoProductStockBean ospcBean = new CargoProductStockBean();
				ospcBean.setId(rs1.getInt("cps.id"));
				ospcBean.setCargoId(rs1.getInt("cps.cargo_id"));
				CargoInfoBean ciBean = new CargoInfoBean();
				ciBean.setWholeCode(rs1.getString("ci.whole_code"));
				ciBean.setAreaId(rs1.getInt("ci.stock_area_id"));
				ospcBean.setCargoInfo(ciBean);
				ospcBean.setStockCount(rs1.getInt("cps.stock_count"));
				cargoProductCargoList.add(ospcBean);
			}
			rs1.close();
			if (cargoProductCargoList == null || cargoProductCargoList.size() == 0) {
				flag=1;
				break;
			}
			osMap.put(ospBean, cargoProductCargoList);
		}
		if(flag == 1){
			result = false;
			return result;
		}
		HashMap map = confirmCargo(osMap);
		Iterator outIter = map.entrySet().iterator();
		// 遍历每一个出库的产品
		while (outIter.hasNext()) {
			Map.Entry entry = (Map.Entry) outIter.next();
			OrderStockProductBean outOrderProduct = (OrderStockProductBean) entry.getKey();
			List cpsOutList = (List) entry.getValue();
			voProduct product = wareService.getProduct(outOrderProduct.getProductId());
			product.setCargoPSList(cargoService.getCargoAndProductStockList("cps.product_id = " + product.getId(), -1, -1, "cps.id asc"));
			if (cpsOutList == null || cpsOutList.size() == 0) {
				stockCheck = false;
				break;
			}
			// 更新货位库存锁定记录
			int totalCount = outOrderProduct.getStockoutCount();
			int index = 0;
			int stockOutCount = 0;
			// 遍历每一个货位
			do {
				CargoProductStockBean cps = (CargoProductStockBean) cpsOutList.get(index);
				// 如果该商品要出库的数量大于等于该货位库存的可发货数量
				if (totalCount >= cps.getStockCount()) {
					stockOutCount = cps.getStockCount();
				} else {
					stockOutCount = totalCount;
				}
				totalCount -= cps.getStockCount();
				index++;
				// 添加订单出库货位信息记录
				OrderStockProductCargoBean ospc = new OrderStockProductCargoBean();
				ospc.setOrderStockId(osBean.getId());
				ospc.setOrderStockProductId(outOrderProduct.getId());
				ospc.setCount(stockOutCount);
				ospc.setCargoProductStockId(cps.getId());
				ospc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
				stockService.addOrderStockProductCargo(ospc);

				// 更新货位库存
				cargoService.updateCargoProductStockCount(cps.getId(), -stockOutCount);
				cargoService.updateCargoProductStockLockCount(cps.getId(), stockOutCount);
			}while (totalCount > 0 && index < cpsOutList.size());
		}
		return result;
	}
	
	public String addOrderProduct(int orderId, String code, int count,float price,float dprice, WareService wareService) {
		PreparedStatement pst = null;
		try {
			int productId = wareService.getProductIdByCode(code);
			pst = wareService.getDbOp().getConn()
			.prepareStatement("insert into user_order_product (order_id,product_id,count,product_price,discount_price)values(?,?,?,?,?)");
			pst.setInt(1, orderId);
			pst.setInt(2, productId);
			pst.setInt(3, count);
			pst.setFloat(4, price);
			pst.setFloat(5, dprice);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return "添加订单商品异常！";
		} finally {
			DbUtil.closeStatement(pst);
		}
		return null;
	}
	
	public HashMap confirmCargo(HashMap osMap) {
		// 根据cargoListMap中的数据求出该订单中可发货数量最多的区，并锁定该区中满足可发货量的库存最少的货位
		HashMap areaMap = new HashMap();// 区域发货map key:货位编号前八位 value 该区域的可发货数量
		HashMap cargoMap = new HashMap();// 函数返回值 key:orderStockProductBean
											// value:cargoProductStockList
		Iterator iter = osMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			OrderStockProductBean orderStockProductBean = (OrderStockProductBean) entry.getKey();
			List cargoProductStockList = (List) entry.getValue();
			if (cargoProductStockList != null && cargoProductStockList.size() > 0) {
				for (int i = 0; i < cargoProductStockList.size(); i++) {
					CargoProductStockBean cargoProductStockBean = (CargoProductStockBean) cargoProductStockList.get(i);
					String areaCode = cargoProductStockBean.getCargoInfo().getWholeCode().substring(0, 7);
					// 计算出每个区域可发货的数量存入areaMap中，key:区域编号 value:区域可发货数量
					if (areaMap.size() == 0) {
						areaMap.put(areaCode, 1);
					} else if (areaMap.containsKey(areaCode)) {
						areaMap.put(areaCode, StringUtil.toInt(areaMap.get(areaCode).toString()) + 1);
					} else {
						areaMap.put(areaCode, 1);
					}
				}
			}
			// 将areaMap按照可发货数量排序存入areaSortList中
		}
		List areaSortList = new ArrayList(areaMap.entrySet());
		Collections.sort(areaSortList, new Comparator() {
			public int compare(Object o1, Object o2) {
				return (((Map.Entry) o2).getValue()).toString().compareTo(((Map.Entry) o1).getValue().toString());
			}
		});
		//System.out.println("区域排序列表" + areaSortList);
		// 获取每个产品发货的货位
		Iterator iterOsMap = osMap.entrySet().iterator();
		while (iterOsMap.hasNext()) {// 遍历每个商品
			Map.Entry entry = (Map.Entry) iterOsMap.next();// key：orderStockProductBean,value:cpsList
			OrderStockProductBean orderStockProductBean = (OrderStockProductBean) entry.getKey();// 发货产品BEAN
			List cargoProductStockList = (List) entry.getValue();// 发货产品货位列表
			for (int i = 0; i < areaSortList.size(); i++) {// 遍历最高发货量区域列表
				Map.Entry areaSortMap = (Map.Entry) areaSortList.get(i);// areaSortMap
				for (int j = 0; j < cargoProductStockList.size(); j++) {// 遍历每个商品所有的可发货货位
					CargoProductStockBean cargoProductStockBean = (CargoProductStockBean) cargoProductStockList.get(j);
					List list = new ArrayList();
					list.add(cargoProductStockBean);
					String areaCode = cargoProductStockBean.getCargoInfo().getWholeCode().substring(0, 7);
					// key
					// 货位编号前七位
					// value：每个区可发SKU种类值
					if (areaCode.equals(areaSortMap.getKey().toString())) {
						if (!cargoMap.containsKey(orderStockProductBean)) {
							cargoMap.put(orderStockProductBean, list);
							for (int k = 0; k < list.size(); k++) {
								CargoProductStockBean cargoProductStockBean1 = (CargoProductStockBean) list.get(k);
							}
						}
						break;
					}
				}
			}
		}
		return cargoMap;
	}
	
	public static boolean isOurCoupon(String coupon) {
		String[] ourCouponStrings = new String[]{"20-", "30-", "35-", "100-"};
		for (int i=0; i < ourCouponStrings.length; i ++) {
			if (coupon.startsWith(ourCouponStrings[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean parentNoSku(List<ItemInfo> swapitemList,List<ItemInfo> outeritemList){
		boolean flag = false;
		for (ItemInfo swapitem : swapitemList) {
			for (ItemInfo outeritem : outeritemList) {
				if(swapitem.getSkuId().equals(outeritem.getSkuId())) {
					flag = true;
					break;
				}
			}
			if (flag) {
				break;
			}
		}
		return !flag;
	}
	
	public String addOrderPromotionProduct(int orderId ,int productId, int userId,int promotionId,int count, int flag,float price,float dprice,int point, WareService wareService) {
        String query = "insert into user_order_promotion_product (order_id,user_id,product_id,promotion_id,count,flag,product_price,discount_price,point,update_time)values(?,?,?,?,?,?,?,?,?,now())";
        try {
	        if (wareService.getDbOp().prepareStatement(query)) {
	            PreparedStatement pst = wareService.getDbOp().getPStmt();
	          	pst.setInt(1, orderId);
                pst.setInt(2, userId);
                pst.setInt(3, productId);
                pst.setInt(4, promotionId );
                pst.setInt(5, count);
                pst.setInt(6, flag);
                pst.setFloat(7, price);
                pst.setFloat(8, dprice);
                pst.setInt(9, point);
              //执行
                wareService.getDbOp().executePstmt();
           } 
       }catch (SQLException e) {
            e.printStackTrace();
            return "添加订单商品促销信息异常！";
       } finally{
       }
        return null;
	}
}
