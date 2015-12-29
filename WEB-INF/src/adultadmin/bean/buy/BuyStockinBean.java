/*
 * Created on 2009-5-6
 *
 */
package adultadmin.bean.buy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voUser;


/**
 * @author Administrator
 * 
 * 说明：采购入库单Bean
 */
public class BuyStockinBean {

	/**
	 * 未处理  /配件 待确认
	 */
	public static final int STATUS0 = 0;

	/**
	 * 处理中 /配件 确认未通过
	 */
	public static final int STATUS1 = 1;

	/**
	 * 已编辑价格
	 */
	public static final int STATUS2 = 2;

	/**
	 * 入库处理中  /配件 待审核
	 */
	public static final int STATUS3 = 3;

	/**
	 * 入库已完成  /配件 审核通过
	 */
	public static final int STATUS4 = 4;
	
	/**
	 * 审核未通过  /配件 审核未通过
	 */
	public static final int STATUS5 = 5;
	
	/**
	 * 已审核
	 */
	public static final int STATUS6 = 6;
	
	/**
	 * 已关闭
	 */
	public static final int STATUS7 = 7;
	
	/**
	 * 已删除
	 */
	public static final int STATUS8 = 8;

	private String fittingStatusName;
	
	public int buyOrderId;
	public int getBuyOrderId() {
		return buyOrderId;
	}

	public void setBuyOrderId(int buyOrderId) {
		this.buyOrderId = buyOrderId;
	}

	public int id;                       //ID

	public String name;                  //名称

	public String code;                  //编号

	public int stockArea;                //所属库

	public int stockType;                //库别

	public String productType;           //产品线
	
	public String proxyName;             //代理商

	public int createUserId;             //添加用户ID

	public int auditingUserId;           //审核用户ID
	
	public voUser creatUser;             //添加计划用户
	public voUser auditingUser;          //审核用户

	public String createDatetime;        //添加时间

	public String confirmDatetime;       //确认时间

	public int buyStockId;               //所属进货单ID

	public int printCount;               //打印次数

	public String remark;                //备注

	public int status;                   //状态
	
	public int supplierId;				//供应商id
	
	public String payUser;               //付款人
	
	public float taxPoint;               //税点

	public BuyStockBean buyStock;        //对应进货单
	
	public int type;    //1:非我司仓 0：我司仓
	
	private List buyStockinProductList = new ArrayList();//入库单对应产品信息

	public String createUserName;
	
	public List stockinProductList = new ArrayList(); //对应的入库商品列

	private BuyOrderBean buyOrder; //反推对应的 采购订单
	
	public String affirmDatetime; //确认时间
	
	public  int affirmUserId; //确认人id
	
	
	public List getStockinProductList() {
		return stockinProductList;
	}

	public void setStockinProductList(List stockinProductList) {
		this.stockinProductList = stockinProductList;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public int getAuditingUserId() {
		return auditingUserId;
	}

	public void setAuditingUserId(int auditingUserId) {
		this.auditingUserId = auditingUserId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getConfirmDatetime() {
		return confirmDatetime;
	}

	public void setConfirmDatetime(String confirmDatetime) {
		this.confirmDatetime = confirmDatetime;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public BuyStockBean getBuyStock() {
		return buyStock;
	}

	public void setBuyStock(BuyStockBean buyStock) {
		this.buyStock = buyStock;
	}

	public int getBuyStockId() {
		return buyStockId;
	}

	public void setBuyStockId(int buyStockId) {
		this.buyStockId = buyStockId;
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
	
	public String getPayUser() {
		return payUser;
	}

	public void setPayUser(String payUser) {
		this.payUser = payUser;
	}

	public String getStatusName() {
		if (status == BuyStockinBean.STATUS0) {
			return "未处理";
		}
		if (status == BuyStockinBean.STATUS1) {
			return "处理中";
		}
		if (status == BuyStockinBean.STATUS2) {
			return "已编辑采购价格";
		}
		if (status == BuyStockinBean.STATUS3) {
			return "入库处理中";
		}
		if (status == BuyStockinBean.STATUS4) {
			return "入库已完成";
		}
		if (status == BuyStockinBean.STATUS5) {
			return "审核未通过";
		}
		if (status == BuyStockinBean.STATUS6) {
			return "已审核";
		}
		if (status == BuyStockinBean.STATUS7) {
			return "已关闭";
		}
		return "";
	}

	
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public voUser getCreatUser() {
		return creatUser;
	}

	public void setCreatUser(voUser creatUser) {
		this.creatUser = creatUser;
	}

	public voUser getAuditingUser() {
		return auditingUser;
	}

	public void setAuditingUser(voUser auditingUser) {
		this.auditingUser = auditingUser;
	}

	public float getTaxPoint() {
		return taxPoint;
	}

	public void setTaxPoint(float taxPoint) {
		this.taxPoint = taxPoint;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}


	public void setBuyOrder(BuyOrderBean buyOrder) {
		this.buyOrder = buyOrder;
	}

	public BuyOrderBean getBuyOrder() {
		return buyOrder;
	}


	public List getBuyStockinProductList() {
		return buyStockinProductList;
	}

	public void setBuyStockinProductList(List buyStockinProductList) {
		this.buyStockinProductList = buyStockinProductList;
	}

	public int getType() {
		return type;
	}
	public String getFittingStatusName() {
		switch (this.status) {
			case BuyStockinBean.STATUS0: {
				this.fittingStatusName = "待确认";
				break;
			}
			case BuyStockinBean.STATUS1: {
				this.fittingStatusName = "确认未通过";
				break;
			}
			case BuyStockinBean.STATUS3: {
				this.fittingStatusName = "待审核";
				break;
			}
			case BuyStockinBean.STATUS4: {
				this.fittingStatusName = "已完成";
				break;
			}
			case BuyStockinBean.STATUS5: {
				this.fittingStatusName = "审核未通过";
				break;
			}
			default :{
				this.fittingStatusName = "未知";
			}
		}
		return this.fittingStatusName;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	public void setFittingStatusName(String fittingStatusName) {
		this.fittingStatusName = fittingStatusName;
	}


}
