package adultadmin.service.impl;

import java.util.List;

import adultadmin.bean.barcode.BatchBarcodePrintlogBean;
import adultadmin.bean.barcode.ConsigPrintlogBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.util.db.DbOperation;

public class BatchBarcodeServiceImpl extends BaseServiceImpl  implements IBatchBarcodeService {
	
	public BatchBarcodeServiceImpl(int useConnType,DbOperation dbOp){
		this.useConnType=useConnType;
		this.dbOp=dbOp;
	}
	
	public BatchBarcodeServiceImpl(){
		this.useConnType=CONN_IN_SERVICE;
	}
	
	public boolean addBatchBarcodePrintlog(
			BatchBarcodePrintlogBean batchBarcodePrintlogBean) {
		return addXXX(batchBarcodePrintlogBean, "batch_barcode_printlog");
	}

	public boolean updateBatchBarcodePrintlog(String set, String condition) {
		return updateXXX(set, condition, "batch_barcode_printlog");
	}

	public List getBatchBarcodePrintlogList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "batch_barcode_printlog", "adultadmin.bean.barcode.BatchBarcodePrintlogBean");
	}

	public BatchBarcodePrintlogBean getBatchBarcodePrintlog(String condition) {
		return (BatchBarcodePrintlogBean)getXXX(condition, "batch_barcode_printlog", "adultadmin.bean.barcode.BatchBarcodePrintlogBean");
	}

	public int getBatchBarcodePrintlogCount(String condition) {
		return getXXXCount(condition, "batch_barcode_printlog", "id");
	}

	public boolean addOrderCustomer(OrderCustomerBean orderCustomerBean) {
		return addXXX(orderCustomerBean, "order_customer");
	}

	public boolean updateOrderCustomer(String set, String condition) {
		return updateXXX(set, condition,"order_customer");
	}
	
	public boolean deleteOrderCustomer(String condition) {
		return deleteXXX(condition, "order_customer");
	}

	public List getOrderCustomerList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_customer", "adultadmin.bean.barcode.OrderCustomerBean");
	}

	public OrderCustomerBean getOrderCustomerBean(String condition) {
		return (OrderCustomerBean)getXXX(condition, "order_customer", "adultadmin.bean.barcode.OrderCustomerBean");
	}
	
	public boolean addConsigPrintlog(ConsigPrintlogBean consigPrintlogBean) {
		return addXXX(consigPrintlogBean, "consig_printlog");
	}
	
	public boolean updateConsigPrintlog(String set, String condition) {
		return updateXXX(set, condition,"consig_printlog");
	}
	
	public boolean deleteConsigPrintlog(String condition) {
		return deleteXXX(condition, "consig_printlog");
	}
	
	public List getConsigPrintlogList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "consig_printlog", "adultadmin.bean.barcode.ConsigPrintlogBean");
	}
	
	public ConsigPrintlogBean getConsigPrintlogBean(String condition) {
		return (ConsigPrintlogBean)getXXX(condition, "consig_printlog", "adultadmin.bean.barcode.ConsigPrintlogBean");
	}
}
