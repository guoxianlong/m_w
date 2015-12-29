package cn.mmb.delivery.domain.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.dao.DataAccessException;

import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;

public interface WayBillService {

	/**
	 * 获取待发送的数据
	* @Description: 
	* @author ahc
	 */
	public List<WayBill> getDeliverRelation (int deliverId) throws Exception;
	
	/**
	 * 发送接口信息
	* @Description: 
	* @author ahc
	 * @throws UnsupportedEncodingException 
	 */
	public List<String> sendWayBillInfo(List<WayBill> sendData) throws Exception ;
	
	
	/**
	 * 更新"快递公司关联表"
	* @Description: 
	* @author ahc
	 * @throws Exception 
	 */
	public void updateDeliverRelation(int deliverId,List<WayBill> list) throws DataAccessException;
	
	/**
	 * 添加"快递公司关联表"
	* @Description: 
	* @author ahc
	 * @throws Exception 
	 */
	public void addDeliverRelation(int deliverId,List<WayBill> list) throws DataAccessException;
	
	/**
	 * 更新"快递公司获取个性化信息"表
	* @Description: 
	* @author ahc
	 * @throws Exception 
	 */
	public int updateDeliverRelationForStatus(int deliverId,List<WayBill> list,String status) throws DataAccessException;
	

	/**
	 * 封装转换结果
	* @Description: 
	* @author ahc
	 * @param sendData 
	 * @throws Exception 
	 */
	public List<WayBill> parseToWayBill(List<WayBill> sendData, List<String> list) throws Exception;
	
	/**
	 * 封装转换结果
	* @Description: 
	* @author ahc
	 * @param sendData 
	 * @throws Exception 
	 */
	public List<WayBill> parseToWayBill(List<String> list) throws Exception;

	/**
	 * @param wayBill
	 * @return boolean
	 * @author anchao
	 * @throws Exception 
	 * @date 2015年8月10日 上午11:31:19
	 */
	public boolean printWayBill(WayBill wayBill) throws Exception;
	
	/**
	 * @param wayBill
	 * @return boolean
	 * @author anchao
	 * @throws Exception 
	 * @date 2015年8月11日 上午13:50:21
	 */
	public boolean printWayBillDq(WayBill wayBill)throws Exception;
	
	/** 
	 * @Description: 调用接口获取物流信息
	 * @return Object 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月11日 下午4:59:03 
	 */
	public <T> T getWayBillTrace();
	
	/** 
	 * @Description: 获取需要新加的面单信息
	 * @return List<WayBillTrace> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 上午9:40:14 
	 */
	public List<WayBillTrace> getNeedAddWayBillInfo(WayBillTrace wayBillTrace) throws Exception;
	
	/** 
	 * @Description: 更新面单信息
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 上午9:20:53 
	 */
	public String updateWayBillInfo(List<WayBillTrace> wayBillTrace);
	
	/**
	 * 取消订单接口
	* @Description: 
	* @author ahc
	 */
	public int cancelWayBill(int deliverId,String orderCode) throws Exception;
	
	/**
	 * 根据订单号获取待发送的面单信息
	* @Description: 
	* @author ahc
	 */
	public List<WayBill> getNeedWayBillInfo(int deliverId,String orderCode) throws Exception;
	
}
