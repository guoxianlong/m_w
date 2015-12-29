package mmb.stock.stat;

import java.util.ArrayList;
import java.util.List;

import adultadmin.action.vo.UserInfoBean;
import adultadmin.action.vo.voOrder;
import adultadmin.bean.order.OrderStockProductCargoBean;

public class SortingBatchOrderBean {
//	分拣批次中订单 sorting_batch_order
//	id,int(11)
//	订单id order_id,int(11)////
//	订单编号 order_code,varchar(45)
//	快递公司 deliver,int(11)
//	商品分类 order_type,int(11)
//	分拣批次号 sorting_batch_id,int(11)////
//	分拣波次号 sorting_group_id,int(11)////
//	分拣状态 status,int(11)////
//	在波次中的序号 group_num,int(11)
    public int id;
    public int orderId;
    public String orderCode;
    public int deliver;
    public int orderType;
    public int sortingBatchId;
    public int sortingGroupId;
    public String sortingGroupCode;
	public String sortingBatchCode;
    public int status;
	public int deleteStatus;	//删除状态
    public int groupNum;
    public voOrder oOrder;
    public UserInfoBean uBean;
    public List productList;
    public int productCount;
    public String cargoCode;
    public String ckTime;//出库时间
    public int orderStockId;//出库单ID
    public String orderStockCode;//出库单编号
    public List<OrderStockProductCargoBean> orderStockProductCargoList;//批次订单中包含的商品货位列表
    public String groupCode=""; //波次中为订单分配的格子号
    public String productCode;
    public String statusName;
    public String deliverName;
    public String orderTypeName;
    public String address;
    public String productNames;//将多个产品拼成字符串显示
    public float Dprice;//订单价格
    public String name; //客户名称
    public String phone; //客户电话
    
    private List<SortingBatchOrderProductBean> sortingBatchOrderProductList = new ArrayList<SortingBatchOrderProductBean>();
    
    /**
	 * 未删除
	 */
	public static int DELETE_STATUS = 0;
	/**
	 * 已删除
	 */
	public static int DELETE_STATUS1 = 1;
    /**
	 * 未处理
	 */
	public static int STATUS0 = 0;
    /**
	 * 未打单
	 */
	public static int STATUS1 = 1;
	/**
	 * 分拣中
	 */
	public static int STATUS2 = 2;
	/**
	 * 分拣完成
	 */
	public static int STATUS3 = 3;
	public int buy_mode;//购买方式
    public String getStatusName(int status) {
		if (status == STATUS0) {
			return "未处理";
		}else if(status == STATUS1){
			return "处理中";
		}else if(status == STATUS2){
			return "分拣中";
		}else if(status == STATUS3){
			return "已完成";
		}else{
			return "";
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public int getDeliver() {
		return deliver;
	}
	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public int getSortingBatchId() {
		return sortingBatchId;
	}
	public void setSortingBatchId(int sortingBatchId) {
		this.sortingBatchId = sortingBatchId;
	}
	public int getSortingGroupId() {
		return sortingGroupId;
	}
	public void setSortingGroupId(int sortingGroupId) {
		this.sortingGroupId = sortingGroupId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getGroupNum() {
		return groupNum;
	}
	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}
	public voOrder getoOrder() {
		return oOrder;
	}
	public void setoOrder(voOrder oOrder) {
		this.oOrder = oOrder;
	}
	
	public UserInfoBean getuBean() {
		return uBean;
	}
	public void setuBean(UserInfoBean uBean) {
		this.uBean = uBean;
	}
	public List getProductList() {
		return productList;
	}
	public void setProductList(List productList) {
		this.productList = productList;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public String getSortingGroupCode() {
		return sortingGroupCode;
	}
	public void setSortingGroupCode(String sortingGroupCode) {
		this.sortingGroupCode = sortingGroupCode;
	}
	public String getSortingBatchCode() {
		return sortingBatchCode;
	}
	public void setSortingBatchCode(String sortingBatchCode) {
		this.sortingBatchCode = sortingBatchCode;
	}
	public String getCargoCode() {
		return cargoCode;
	}
	public void setCargoCode(String cargoCode) {
		this.cargoCode = cargoCode;
	}
	public String getCkTime() {
		return ckTime;
	}
	public void setCkTime(String ckTime) {
		this.ckTime = ckTime;
	}
	public int getDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(int deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public int getBuy_mode() {
		return buy_mode;
	}
	public void setBuy_mode(int buy_mode) {
		this.buy_mode = buy_mode;
	}
	public List<OrderStockProductCargoBean> getOrderStockProductCargoList() {
		return orderStockProductCargoList;
	}
	public void setOrderStockProductCargoList(
			List<OrderStockProductCargoBean> orderStockProductCargoList) {
		this.orderStockProductCargoList = orderStockProductCargoList;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public int getOrderStockId() {
		return orderStockId;
	}
	public void setOrderStockId(int orderStockId) {
		this.orderStockId = orderStockId;
	}
	public String getOrderStockCode() {
		return orderStockCode;
	}
	public void setOrderStockCode(String orderStockCode) {
		this.orderStockCode = orderStockCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	public String getOrderTypeName() {
		return orderTypeName;
	}
	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProductNames() {
		return productNames;
	}
	public void setProductNames(String productNames) {
		this.productNames = productNames;
	}
	public float getDprice() {
		return Dprice;
	}
	public void setDprice(float dprice) {
		Dprice = dprice;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public List<SortingBatchOrderProductBean> getSortingBatchOrderProductList() {
		return sortingBatchOrderProductList;
	}
	public void setSortingBatchOrderProductList(
			List<SortingBatchOrderProductBean> sortingBatchOrderProductList) {
		this.sortingBatchOrderProductList = sortingBatchOrderProductList;
	}
    
    
}
