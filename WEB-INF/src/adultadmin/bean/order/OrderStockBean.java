/*
 * Created on 2009-5-8
 *
 */
package adultadmin.bean.order;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import adultadmin.action.vo.voOrder;
import adultadmin.util.db.DbOperation;

/**
 * 出库单
 */
public class OrderStockBean {
	public static String SERVER_URL = "http://gw.api.jd.com/routerjson";
	public static String accessToken = "831b63b3-aee8-426a-9b37-361d9eb11937";//2015-02-02
	public static String appKey = "07D1C92EB30941D11BE1172A09D47D71";
	public static String appSecret = "8a621eaed0204fde87b3b2d1b86decc5";

	/**
	 * 处理中
	 */
	public static int STATUS1 = 0;
	/**
	 * 待出货
	 */
	public static int STATUS2 = 1;
	/**
	 * 已确认
	 */
	public static int STATUS3 = 2;
	/**
	 * 已删除
	 */
	public static int STATUS4 = 3;
	/**
	 * 退货删除
	 */
	public static int STATUS5 = 4;
	/**
	 * 复核
	 */
	public static int STATUS6 = 5;
	/**
	 * 实物未退回
	 */
	public static int STATUS7 = 6;
	/**
	 * 实物已退回
	 */
	public static int STATUS8 = 7;
	/**
	 * 用户退货
	 */
	public static int STATUS9 = 8;
	
	//特殊地址
    public static String specialAddr = "山东省烟台市长岛";
	
    /**
     * 订单处理中，而且库存充足
     */
    public static int STATUS1_STOCK = 1;

    /**
     * 订单处理中，但是缺货
     */
    public static int STATUS1_NO_STOCK = 2;

    /**
     * 当前地区库存满足，另一个地方不满足
     */
    public static int STATUS1_ONE_STOCK = 5;

    /**
     * 当前地区库存不满足，另一地区库存满足
     */
    public static int STATUS1_OTHER_STOCK = 6;

    /**
     * 订单出货是待出货；以前的出现过库存不足情况。
     */
    public static int STATUS2_FROM_NO_STOCK = 3;

    /**
     * 订单出货是待出货；库存一直充足。
     */
    public static int STATUS2_FROM_STOCK = 4;


	public int id;

	public String code;//出库单号

	public String name;//出库单名称

	public int orderId;//订单id

	public String orderCode;//订单编号

	public String createDatetime;//出库单创建时间

	public String lastOperTime;//出库单最后操作时间

	public String remark;//备注

	public int stockArea;//库存地区

	public int stockType;// 库存类型

	public int status;//出库单状态

	public int createUserId;// 创建者用户id

	public int statusStock;//库存状态

	public int realStatusStock;//????

	public int deliver;//快递公司id

	public voOrder order;
	
	public List<OrderStockProductBean> orderStockProductList = new ArrayList<OrderStockProductBean>();
	
	public List<OrderStockProductCargoBean> orderStockProductCargList = new ArrayList<OrderStockProductCargoBean>();

	//订单商品分类
	public int productType;
	
	//订单商品数量
	public int productCount;
	
	/**
	 * 方便easyui的属性
	 */
	public String orderStatusName;
	public String stockTypeName;
	public String orderAddress;
	public String stockAreaName;
	public String deliverName;
	
	private String packageCode; //运单号
	
	public String getOrderStatusName() {
		return orderStatusName;
	}

	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}

	public String getStockTypeName() {
		return stockTypeName;
	}

	public String getOrderAddress() {
		return orderAddress;
	}

	public void setOrderAddress(String orderAddress) {
		this.orderAddress = orderAddress;
	}

	public void setStockTypeName(String stockTypeName) {
		this.stockTypeName = stockTypeName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastOperTime() {
		return lastOperTime;
	}

	public void setLastOperTime(String lastOperTime) {
		this.lastOperTime = lastOperTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStockArea() {
		return stockArea;
	}

	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public voOrder getOrder() {
		return order;
	}

	public void setOrder(voOrder order) {
		this.order = order;
	}

	public int getRealStatusStock() {
		return realStatusStock;
	}

	public void setRealStatusStock(int realStatusStock) {
		this.realStatusStock = realStatusStock;
	}

	public int getStatusStock() {
		return statusStock;
	}

	public void setStatusStock(int statusStock) {
		this.statusStock = statusStock;
	}

    public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public String getStatusName() {
        if (status == OrderStockBean.STATUS1) {
			return "处理中";
		}
		if (status == OrderStockBean.STATUS2) {
			return "待出货";
		}
		if (status == OrderStockBean.STATUS3) {
			return "已出货";
		}
		if (status == OrderStockBean.STATUS4) {
			return "已删除";
		}
		if (status == OrderStockBean.STATUS5) {
			return "退单删除";
		}
		if (status == OrderStockBean.STATUS6) {
			return "复核";
		}
		if (status == OrderStockBean.STATUS7) {
			return "实物未退回";
		}
		if (status == OrderStockBean.STATUS8) {
			return "实物已退回";
		}
		if (status == OrderStockBean.STATUS9) {
			return "用户退货";
		}

        return "";
    }

    public static String getStockStatusName(int stockStatus){
    	String result = "";
    	switch(stockStatus){
    		case 0:
    			result = "三地库存充足";
    			break;
    		case 1:
    			result = "芳村缺货/广速缺货";
    			break;
    		case 2:
    			result = "北库缺货/广速缺货";
    			break;
    		case 3:
    			result = "三地库存都不足";
    			break;
    		case 4:
    			result = "北库缺货/芳村缺货";
    			break;
    		case 5:
    			result = "广速缺货";
    			break;
    		case 6:
    			result = "芳村缺货";
    			break;
    		case 7:
    			result = "北库缺货";
    			break;
    	}
    	return result;
    }

	public List<OrderStockProductBean> getOrderStockProductList() {
		return orderStockProductList;
	}

	public void setOrderStockProductList(
			List<OrderStockProductBean> orderStockProductList) {
		this.orderStockProductList = orderStockProductList;
	}

	public List<OrderStockProductCargoBean> getOrderStockProductCargList() {
		return orderStockProductCargList;
	}

	public void setOrderStockProductCargList(
			List<OrderStockProductCargoBean> orderStockProductCargList) {
		this.orderStockProductCargList = orderStockProductCargList;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public String getStockAreaName() {
		return stockAreaName;
	}

	public void setStockAreaName(String stockAreaName) {
		this.stockAreaName = stockAreaName;
	}

	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	
	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	/**
	 * 库地区优先发货列表
	 */
	public static HashMap<String, String> areaDeliverPriorityMap = new LinkedHashMap<String,String>();
	
	/**
	 * 初始化库地区优先发货列表
	 */
	static{
		if(areaDeliverPriorityMap.size()==0){
			initAreaDeliverPriorityMap();
		}
	}
	
	/**
	 * 初始化库地区优先发货列表
	 */
	public static String initAreaDeliverPriorityMap(){
		areaDeliverPriorityMap.clear();
		DbOperation dbOp=new DbOperation();
		dbOp.init("adult_slave");
		try{
			String deliverMapsql="select province,priority from area_delivery_priority";
			ResultSet rs=dbOp.executeQuery(deliverMapsql);
			while(rs.next()){
				String province=rs.getString(1);
				String priority=rs.getString(2);
				areaDeliverPriorityMap.put(province, priority);
			}
			rs.close();
			return "";
		}catch(Exception e){
			e.printStackTrace();
			return e.getStackTrace().toString();
		}finally{
			dbOp.release();
		}
	}
	
	/**
	 * 申请出库分配快递公司列表
	 * HashMap<Integer, List<HashMap<String, String>>>
	 *         地区id                 省份名称        单量限制,快递公司id
	 */
	public static HashMap<Integer, List<HashMap<String, String>>> deliverSendConfMap = new LinkedHashMap<Integer, List<HashMap<String, String>>>();
	public static HashMap<Integer, String> specialAddrMap = new LinkedHashMap<Integer,String>();
	public static HashMap<Integer, List<HashMap<String, String>>> areaDeliverMap = new LinkedHashMap<Integer,List<HashMap<String, String>>>();
	
	/**
	 * 初始化申请出库分配快递公司列表
	 */
	public static String initDeliverSentConfMap(){
		deliverSendConfMap.clear();
		specialAddrMap.clear();
		areaDeliverMap.clear();
		DbOperation dbOp=new DbOperation();
		dbOp.init("adult_slave");
		try{
			List<Integer> list = new ArrayList<Integer>();
			String sql="select id from stock_area where type = 1 order by id ";
			ResultSet rs = dbOp.executeQuery(sql);
			while(rs.next()){
				list.add(rs.getInt(1));
			}
			List<HashMap<String, String>> provinceList = null;
			HashMap<String, String> tempMap = null;
			for (int i : list) {
				provinceList = new ArrayList<HashMap<String, String>>();
				sql = "select case when p.name is null then '其他省' else p.name end as thekey,dsc.count_limit countLimit,dsc.deliver_id deliverId,dsc.whole_area wholeArea from deliver_send_conf dsc left join provinces p on dsc.province_id=p.id where dsc.area= " + i + " order by dsc.province_id,dsc.priority ";
				rs = dbOp.executeQuery(sql);
				while(rs.next()){
					tempMap = new HashMap<String, String>();
					tempMap.put(rs.getString(1), rs.getInt(2) + "," + rs.getInt(3) + "," + rs.getInt(4));
					provinceList.add(tempMap);
				}
				deliverSendConfMap.put(i, provinceList);
				specialAddrMap.put(i, specialAddr);
				
				provinceList = new ArrayList<HashMap<String, String>>();
				sql = "select dci.id,dci.name from deliver_send_conf dsc join deliver_corp_info dci on dsc.deliver_id=dci.id where dsc.area= " + i + " group by dsc.deliver_id ";
				rs = dbOp.executeQuery(sql);
				while(rs.next()){
					tempMap = new HashMap<String, String>();
					tempMap.put("deliverId", rs.getInt(1) + "");
					tempMap.put("deliverName", rs.getString(2));
					provinceList.add(tempMap);
				}
				areaDeliverMap.put(i, provinceList);
			}
			rs.close();
			return "";
		}catch(Exception e){
			e.printStackTrace();
			return e.getStackTrace().toString();
		}finally{
			dbOp.release();
		}
	}
	
	static{
		if(areaDeliverPriorityMap.size()==0){
			initAreaDeliverPriorityMap();
		}
		if(deliverSendConfMap.size()==0 || specialAddrMap .size() == 0 || areaDeliverMap.size()==0){
			initDeliverSentConfMap();
		}
	}
	
}
