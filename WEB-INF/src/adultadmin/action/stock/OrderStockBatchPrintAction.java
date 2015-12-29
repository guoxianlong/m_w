package adultadmin.action.stock;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockPrintLogBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OrderStockBatchPrintAction extends BaseAction{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        IBatchBarcodeService batchBarcodeService=ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, stockService.getDbOp());
        try {
        	String date=request.getParameter("date");
        	List batchList=new ArrayList();
        	if(date==null){
        		date=DateUtil.getNowDateStr();
        	}
        	batchList=stockService.getOrderStockPrintLogList("time>'"+date+" 00:00:00' and time<'"
             		 +date+" 23:59:59' and type=1 group by batch", -1, -1, null);
        	List currentCountList=new ArrayList();
        	List oriCountList=new ArrayList();//打印时订单数
        	for(int i=0;i<batchList.size();i++){
        		OrderStockPrintLogBean bean=(OrderStockPrintLogBean)batchList.get(i);
        		int batch=bean.getBatch();
        		//客户信息记录，当前订单数
        		List customerList=batchBarcodeService.getOrderCustomerList("batch="+batch+" and order_date like'"+date+"%'", -1, -1, null);
        		if(customerList!=null){
        			for(int j=0;j<customerList.size();j++){
        				OrderCustomerBean ocBean=(OrderCustomerBean)customerList.get(j);
        				String orderCode=ocBean.getOrderCode();
        				OrderStockBean orderStock=stockService.getOrderStock("order_code='"+orderCode+"' and status in (2,5)");
        				if(orderStock==null){//该订单未分拣
        					customerList.remove(ocBean);
        					j--;
        				}
        			}
        			currentCountList.add(customerList.size()+"");
        		}
        		String orderCodes=bean.getRemark();
        		int oriCount=orderCodes.split(",").length;
        		if(oriCount <= 1){
        			oriCount = stockService.getOrderStockPrintLogCount("batch="+batch+" and time>'"+date+" 00:00:00' and time<'"+date+" 23:59:59' and type=1 group by batch");
        		}
        		oriCountList.add(oriCount+"");
        	}
        	request.setAttribute("oriCountList", oriCountList);
        	request.setAttribute("currentCountList", currentCountList);
        	request.setAttribute("batchList", batchList);
        }catch(Throwable e){
        	e.printStackTrace();
        } finally {
        	stockService.releaseAll();
        }
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}
