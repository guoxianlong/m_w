package adultadmin.bean.barcode;

/**
 *  <code>OrderCustomerBean.java</code>
 *  <p>功能:发货单客户打印信息
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-4-2 下午06:15:25	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class OrderCustomerBean {
	/**
	 * id
	 */
	public int id;
	
	/**
	 * 打印时订单递增的序号
	 */
	public int serialNumber;
	
	/**
	 * 订单编号
	 */
	public String orderCode;
	
	/**
	 * 客户姓名
	 */
	public String name;
	
	/**
	 * 订单状态
	 */
	public int status;
	
	/**
	 * 描述
	 */
	public String remark;
	
	/**
	 * 批次
	 */
	public int batch;
	
	/**
	 * 订单存入时间
	 */
	public String orderDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}
	
	
}
