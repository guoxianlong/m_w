package adultadmin.service.infc;

import java.util.List;

import adultadmin.bean.barcode.BatchBarcodePrintlogBean;
import adultadmin.bean.barcode.ConsigPrintlogBean;
import adultadmin.bean.barcode.OrderCustomerBean;

/**
 *  <code>IBatchBarcodeService.java</code>
 *  <p>功能:批次条码Service接口
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-3-18 下午03:53:53	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
/**
 *  <code>IBatchBarcodeService.java</code>
 *  <p>功能:
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-3-18 下午06:21:24	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public interface IBatchBarcodeService  extends IBaseService{

	/**
	 * 功能:添加批次条码单据打印日志
	 * <p>作者文齐辉 2011-3-18 下午03:54:58
	 * @return
	 */
	public boolean addBatchBarcodePrintlog(BatchBarcodePrintlogBean batchBarcodePrintlogBean);
	
	/**
	 * 功能:修改批次条码单据打印日志
	 * <p>作者文齐辉 2011-3-18 下午03:56:20
	 * @param set
	 * @param condition
	 * @return
	 */
	public boolean updateBatchBarcodePrintlog(String set,String condition);
	
	/**
	 * 功能:查找批次条码打印日志List
	 * <p>作者文齐辉 2011-3-18 下午03:58:02
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getBatchBarcodePrintlogList(String condition,int index,int count,String orderBy);
	
	/**
	 * 功能:查找批次条码打印日志Bean
	 * <p>作者文齐辉 2011-3-18 下午03:59:18
	 * @param condition
	 * @return
	 */
	public BatchBarcodePrintlogBean getBatchBarcodePrintlog(String condition);
	
	/**
	 * 功能:返回所有行 
	 * <p>作者文齐辉 2011-3-18 下午06:21:31
	 * @param condition
	 * @return
	 */
	public int getBatchBarcodePrintlogCount(String condition);
	
	/**
	 * 功能:添加订单客户打印信息
	 * <p>作者文齐辉 2011-4-2 下午06:20:00
	 * @param orderCustomerBean
	 * @return
	 */
	public boolean addOrderCustomer(OrderCustomerBean orderCustomerBean);
	
	/**
	 * 功能:修改订单客户打印信息
	 * <p>作者文齐辉 2011-4-2 下午06:21:00
	 * @param set
	 * @param condition
	 * @return
	 */
	public boolean updateOrderCustomer(String set ,String condition);
	
	/**
	 * 功能：删除订单客户打印信息
	 * <p>作者文齐辉 2011-4-2 下午06:21:00
	 * @param set
	 * @param condition
	 * @return
	 */
	public boolean deleteOrderCustomer(String condition);
	
	/**
	 * 功能:查找订单打印客户信息
	 * <p>作者文齐辉 2011-4-2 下午06:12:25
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getOrderCustomerList(String condition,int index,int count,String orderBy);
	
	/**
	 * 功能:根据条件得到订单客户打印信息
	 * <p>作者文齐辉 2011-4-2 下午06:13:23
	 * @param condition
	 * @return
	 */
	public OrderCustomerBean getOrderCustomerBean(String condition);
	
	/**
	 * 功能:添加流水线打印日志信息
	 * <p>作者文齐辉 2011-4-13 下午04:59:58
	 * @param consigPrintlogBean
	 * @return
	 */
	public boolean addConsigPrintlog(ConsigPrintlogBean consigPrintlogBean);
	
	/**
	 * 功能:修改流水线打印日志信息
	 * <p>作者文齐辉 2011-4-13 下午05:00:18
	 * @param set
	 * @param condition
	 * @return
	 */
	public boolean updateConsigPrintlog(String set, String condition);
	
	/**
	 * 功能:删除流水线打印日志信息
	 * <p>作者文齐辉 2011-4-13 下午05:00:52
	 * @param condition
	 * @return
	 */
	public boolean deleteConsigPrintlog(String condition);
	
	/**
	 * 功能:查找流水线打印日志信息List
	 * <p>作者文齐辉 2011-4-13 下午05:01:05
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getConsigPrintlogList(String condition, int index, int count,
			String orderBy);
	
	/**
	 * 功能:查找流水线打印日志信息Bean
	 * <p>作者文齐辉 2011-4-13 下午05:01:20
	 * @param condition
	 * @return
	 */
	public ConsigPrintlogBean getConsigPrintlogBean(String condition);
	
}
