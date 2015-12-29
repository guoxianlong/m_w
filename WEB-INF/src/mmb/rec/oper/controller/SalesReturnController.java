package mmb.rec.oper.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.delivery.domain.PopBussiness;
import mmb.finance.stat.FinanceProductBean;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceSellBean;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.IMEI.IMEIUserOrderBean;
import mmb.stock.aftersale.AfStockService;
import mmb.stock.aftersale.AfterSaleDetectProductBean;
import mmb.stock.aftersale.AfterSaleOperationListBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.ClaimsVerificationBean;
import mmb.stock.stat.ClaimsVerificationProductBean;
import mmb.stock.stat.ClaimsVerificationService;
import mmb.stock.stat.OrderBillInfoBean;
import mmb.stock.stat.ProductWarePropertyService;
import mmb.stock.stat.ReturnPackageCheckBean;
import mmb.stock.stat.ReturnPackageCheckProductBean;
import mmb.stock.stat.ReturnPackageCountService;
import mmb.stock.stat.ReturnPackageLogService;
import mmb.stock.stat.ReturnedPackageBean;
import mmb.stock.stat.ReturnedPackageService;
import mmb.stock.stat.ReturnedPackageServiceImpl;
import mmb.stock.stat.StatService;
import mmb.stock.stat.WarehousingAbnormalService;
import mmb.stock.stat.formbean.ReturnedPackageFBean;
import mmb.ware.WareService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceSaleBaseDataService;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.ReturnsReasonBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stat.AbnormalRealProductBean;
import adultadmin.bean.stat.BsByAbnormalBean;
import adultadmin.bean.stat.WarehousingAbnormalBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockHistoryBean;
import adultadmin.bean.stock.StockOperationBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Arith;
import adultadmin.util.Constants;
import adultadmin.util.CookieUtil;
import adultadmin.util.DateUtil;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 说明：销售退货
 * 
 * @author 张晔
 *
 * 时间：2012.08.12
 */
@Controller
@RequestMapping("/SalesReturnController")
public class SalesReturnController {
	public static byte[] stockLock = new byte[0];
	public Log stockLog = LogFactory.getLog("stock.Log");
	public static String noSession = "/admin/rec/oper/salesReturned/noSession.jsp";
	
	/**
	 * 跳转到“快速退货”界面
	 * 
	 * 作者：张晔
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/quickCancelStockJSP")
	public String quickCancelStockJSP(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute(
	            IConstants.USER_VIEW_KEY);
	    if (user == null) {
	    	request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
	    }
	    UserGroupBean group = user.getGroup();
		if(!group.isFlag(613)){
			request.setAttribute("msg", "您没有退货包裹入库权限！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		return "admin/rec/oper/salesReturned/quickCancelStock";
	}
	
	/**
     * 快速退货——发货清单预览
	 *
	 * 作者：张晔
     */
	@RequestMapping("/previewOrderStock")
	@ResponseBody
    public void previewOrderStock(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=utf-8");
    	voUser admin = (voUser) request.getSession().getAttribute(
	            IConstants.USER_VIEW_KEY);
	    if (admin == null) {
	    	response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前没有登录，添加失败！\"}");
	    	return;
	    }
	    UserGroupBean group = admin.getGroup();
		if(!group.isFlag(613)){
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有退货包裹入库权限！\"}");
			return;
		}
	    
        String orderCode=StringUtil.convertNull(request.getParameter("orderCode"));//订单号
        String packageCode=StringUtil.convertNull(request.getParameter("packageCode"));//包裹单号
        String scanType=StringUtil.convertNull(request.getParameter("scanType"));//扫描类型，1是订单编号，2是包裹单号
        int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
        
        DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
        WareService wareService = new WareService(dbOp_slave);
        IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
        IBatchBarcodeService batchBarcodeService=ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
        try{
        	request.setAttribute("wareArea", wareArea);
    		voOrder order=null;
    		AuditPackageBean apBean=null;
    		if( !CargoDeptAreaService.hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN) ) {
    			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"你没有操作当前地区退货库的权限！\"}");
    			return;
    		}
    		
    		if(scanType.equals("1")){//扫描的是订单号
    			OrderStockBean orderStockBean = service.getOrderStock("code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
    			
    			if(orderStockBean != null){
    				order = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
    			}else{
    				order = wareService.getOrder("code='"+orderCode+"'");
    			}
    			if(order == null){
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"这个订单不存在！\"}");
    				return;
    			}
    			if( order.getStatus() == 11 ) {
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"该订单已退回不可再退！\"}");
    				return;
    			}
    			apBean=service.getAuditPackage("order_id="+order.getId());
    		}else if(scanType.equals("2")){//扫描的是包裹单号
    			apBean=service.getAuditPackage("package_code='"+packageCode+"'");
    			if (apBean == null) {
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"这个包裹单不存在！\"}");
    				return;
     	        }
    			int orderId=apBean.getOrderId();
    			order=wareService.getOrder(orderId);
    			if (order == null) {
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"这个订单不存在！\"}");
    				return;
     	        }
    		}
    		orderCode=order.getCode();
 	        // 从新的订单出货表中 查找订单的出货情况
 	        OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and status<>" + OrderStockBean.STATUS5 + " and order_code = '" + orderCode + "'");
 	        if(oper != null && oper.getStatus() == OrderStockBean.STATUS8) {
 	        	response.getWriter().write("{\"result\":\"failure\",\"tip\":\"该订单包裹已经退回过！\"}");
 	        	return;
			}
			
			if (oper == null || oper.getStatus() != OrderStockBean.STATUS3) {
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"这个订单还没有进行出库操作！\"}");
				return;
			} /*else {
 				//从旧的退换货入库表中 查找订单的退货情况
 			    if(service.getStockOperation("status <> " + StockOperationBean.STATUS2 + " and type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ") and order_code = \"" + orderCode + "\"") != null){
 			    	request.setAttribute("tip", "这个订单正在进行退货入库操作中！");
 				    request.setAttribute("result", "failure");
 				    request.setAttribute("oper", oper);
 				    return;
 			    }
 			}*/
    		OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+orderCode+"'");//客户信息
    		if(ocBean!=null){
    			order.setSerialNumber(ocBean.getSerialNumber());
    			order.setBatchNum(ocBean.getBatch());
    		}
    		OrderStockBean orderStock=service.getOrderStock("order_id="+order.getId()+" and status<>3");//出库记录
    		List ospList=service.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);//出库产品表
    		List productList=new ArrayList();//产品表
    		List ospcList=new ArrayList();//出库产品货位表
    		for(int i=0;i<ospList.size();i++){
    			OrderStockProductBean ospBean=(OrderStockProductBean)ospList.get(i);
    			List ospcList2=service.getOrderStockProductCargoList("order_stock_id="+orderStock.getId()+" and order_stock_product_id="+ospBean.getId(), -1, -1, null);
    			for(int j=0;j<ospcList2.size();j++){
    				OrderStockProductCargoBean ospcBean=(OrderStockProductCargoBean)ospcList2.get(j);
    				voProduct product=wareService.getProduct(ospBean.getProductId());
        			ospcList.add(ospcBean);
        			productList.add(product);
    			}
    		}
			request.setAttribute("apBean", apBean);
			request.setAttribute("order", order);
			request.setAttribute("ocBean", ocBean);
			request.setAttribute("productList", productList);
			request.setAttribute("ospcList", ospcList);
			request.setAttribute("preview", "1");
			response.getWriter().write(getPreviewOrderStock(request, response));
			return;
    	} finally {
            service.releaseAll();
        }
    }
	
	/**
	 * 发货清单预览
	 * @param request
	 * @param response
	 * @return
	 */
	public String getPreviewOrderStock(HttpServletRequest request,
            HttpServletResponse response) {
		StringBuffer sb = new StringBuffer();
		String reason = (String)request.getAttribute("reason");
		String scanType=StringUtil.convertNull(request.getParameter("scanType"));
		voOrder order=(voOrder)request.getAttribute("order");
		int wareArea = ((Integer)request.getAttribute("wareArea")).intValue();
		AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
		OrderCustomerBean ocBean=(OrderCustomerBean)request.getAttribute("ocBean");
		List productList=(List)request.getAttribute("productList");
		List ospcList=(List)request.getAttribute("ospcList");
		int index=0;//商品序号
		int totalCount=0;//商品总数量
		int totalPrice=0;//商品总金额
		int xiaoji=0;//小计
		if(productList!=null){
			for(int i=0;i<productList.size();i++){
				voProduct op=(voProduct)productList.get(i);
		    	OrderStockProductCargoBean ospcBean=(OrderStockProductCargoBean)ospcList.get(i);
		    	totalCount=totalCount+ospcBean.getCount();
		    	totalPrice+=ospcBean.getCount() * op.getPrice();
			}
		}
		sb.append("快速退货——预览发货清单，确认退货<br/><br/>");
		int pageCount=productList.size()%5==0?productList.size()/5:productList.size()/5+1;
		sb.append("订单编号：").append(order.getCode()).append("&nbsp;&nbsp");
		sb.append("包裹单号：").append(order.getPackageNum());
		for(int pageNum=0; pageNum < pageCount; pageNum++){ 
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"670\" border=\"1\" style=\"border: 1px solid;border-collapse:collapse;\">");
			sb.append("<tr>");
			sb.append("<td align=\"left\" style=\"font-size:12px;\">序号：<strong>").append(order.getSerialNumber()).append("</strong></td>");
			sb.append("<td align=\"left\" colspan=\"2\">订单时间：").append(order.getCreateDatetime().toString().substring(0,16)).append("</td>");
			sb.append("<td align=\"left\" >客户姓名：<strong style=\"font-size: 13px;\">").append(StringUtil.getString(ocBean.getName(),8)).append("</strong></td>");
			sb.append("<td align=\"left\" colspan=\"2\">快递公司：").append(order.getDeliverName()).append("</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td align=\"left\">商品序号</td>");
			sb.append("<td align=\"center\">&nbsp;&nbsp;</td>");
			sb.append("<td align=\"left\">货号</td>");
			sb.append("<td align=\"center\">数量</td>");
			sb.append("<td align=\"center\">单价</td>");
			sb.append("<td align=\"center\">金额</td>");
			sb.append("</tr>");
		    for(int i=pageNum*6;i<pageNum*6+6;i++){ 
		    	if(i<productList.size()){ 
			    	voProduct op=(voProduct)productList.get(i);
			    	OrderStockProductCargoBean ospcBean=(OrderStockProductCargoBean)ospcList.get(i);
			    	sb.append("<tr>");
			    	sb.append("<td>").append(index+1).append("</td>");
			    	sb.append("<td><strong>").append(ospcBean.getCargoWholeCode()).append("</strong></td>");
			    	sb.append("<td>").append(op.getCode()).append("</td>");
			    	sb.append("<td align=\"center\"><strong style=\"font-size: 13px;\">").append(ospcBean.getCount()).append("</strong></td>");
			    	sb.append("<td style=\"font-size: 10px; text-align: center;\">").append(NumberUtil.price(op.getPrice())).append("</td>");
			    	sb.append("<td style=\"font-size: 10px; text-align: center;\">").append(NumberUtil.price(ospcBean.getCount() * op.getPrice())).append("</td>");
			    	xiaoji+=ospcBean.getCount(); 
			    	sb.append("</tr>");
			    	sb.append("<tr>");
			    	sb.append("<td></td>");
			    	sb.append("<td align=\"left\"  colspan=\"5\" >").append(op.getOriname()).append("</td>");
			    	sb.append("</tr>");
			    	index++;
		    	}else{
		    		sb.append("<tr>");
		    		sb.append("<td>&nbsp;</td>");
		    		sb.append("<td>&nbsp;</td>");
		    		sb.append("<td>&nbsp;</td>");
		    		sb.append("<td align=\"center\">&nbsp;</td>");
		    		sb.append("<td style=\"font-size: 10px; text-align: right;\">&nbsp;</td>");
		    		sb.append("<td style=\"font-size: 10px; text-align: right;\">&nbsp;</td>");
		    		sb.append("</tr>");
		    		sb.append("<tr>");
		    		sb.append("<td>&nbsp;</td>");
		    		sb.append("<td align=\"left\"  colspan=\"5\" >&nbsp;</td>");
		    		sb.append("</tr>");
		    	}
		    }
		    sb.append("<tr>");
		    sb.append("<td colspan=\"2\" align=\"left\">");
		    if(xiaoji != 0 && index < productList.size()){
		    	sb.append("小计：");
		    	sb.append(xiaoji);
		    }
		    sb.append("商品总数：  ").append(totalCount);
		    sb.append("</td>");
		    sb.append("<td>运费：").append((int)order.getPostage()).append("元</td>");
		    sb.append("<td colspan=\"2\">付款方式：");
		    switch(order.getBuyMode()) 
	    	{
	    		case 0:
	    			sb.append("货到付款");
	    			break;
	    		case 1:
	    			sb.append("邮购");
	    			break;
	    		case 2:
	    			sb.append("上门自取");
	    			break;
	    	}
		    sb.append("</td>");
		    sb.append("<td>总金额：").append(NumberUtil.price(totalPrice)).append("元</td>");
		    sb.append("</tr>");
		    sb.append("</table><br/>");
		}
		sb.append("<form id=\"quickCancelStockForm\" method=\"post\" >");
		sb.append("<input type=\"hidden\" id=\"orderId\" name=\"orderId\" value=\"").append(order.getId()).append("\" />");
		sb.append("<input type=\"hidden\" name=\"reason\" value=\"").append(reason).append("\"/>");
		sb.append("<input type=\"hidden\" name=\"scanType\" value=\"").append(scanType).append("\"/>");
		sb.append("<input type=\"hidden\" name=\"wareArea\" value=\"").append(wareArea).append("\" />");
		sb.append("</from>");
		sb.append("注：如果确认退货，请单击‘确定’或再次扫描订单编号或包裹单号。反之，请单击‘取消’返回至上一页，继续扫描其他订单。");
		return sb.toString();
	}
	
	/**
	 * 快速退货页面的“库地区”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getWareAreaJSON")
	@ResponseBody
	public String getWareAreaJSON(HttpServletRequest request) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		StringBuffer sb = new StringBuffer();
		if( cdaList.size() == 0 ) { 
			return "{\"areaId\":\"-1\",\"areaName\":\"无地区权限\",\"selected\":true}";
  		}else{ 
  			for ( int i = 0; i < cdaList.size(); i ++ ) {
  				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
  				if (i == 0) {
  					sb.append("[");
  					sb.append("{\"areaId\":\"").append(areaId).append("\",\"areaName\":\"").append(ProductStockBean.areaMap.get(areaId)).append("\",\"selected\":true}");
  				} else {
  					sb.append("{\"areaId\":\"").append(areaId).append("\",\"areaName\":\"").append(ProductStockBean.areaMap.get(areaId)).append("\"}");
  				}
  				if (i == cdaList.size()-1) {
  					sb.append("]");
  				} else {
  					sb.append(",");
  				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 完成快速退货
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/quickCancelStock")
	@ResponseBody
	public void quickCancelStock(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser) request.getSession().getAttribute(
	            IConstants.USER_VIEW_KEY);
	    if (user == null) {
	    	response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前没有登录，添加失败！\"");
	    	return;
	    }
	    UserGroupBean group = user.getGroup();
		if(!group.isFlag(613)){
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有退货包裹入库权限！\"");
			return;
		}
	    int type = StringUtil.StringToId(request.getParameter("type"));
    	String orderId=StringUtil.convertNull(request.getParameter("orderId"));//订单Id
    	int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
    	
    	if( wareArea == -1 ) {
    		response.getWriter().write("{\"result\":\"failure\",\"tip\":\"没有选择退货包裹的入库地区！\"");
    		return;
    	}
    	if( !CargoDeptAreaService.hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN) ) {
    		response.getWriter().write("{\"result\":\"failure\",\"tip\":\"你没有操作当前地区退货库的权限！\"");
    		return;
		}
        int stockType = 0;
        String title = null;
        switch(type){
        	case 1:
        		stockType = StockOperationBean.CANCEL_STOCKIN;
        		title = "退货入库";
        		break;
        	case 2:
        		stockType = StockOperationBean.CANCEL_EXCHANGE;
        		title = "退换货";
        		break;
        	case 3:
            	stockType = StockOperationBean.BAD_EXCHANGE;
            	title = "烂货退换";
            	break;
        	default:
        		stockType = StockOperationBean.CANCEL_STOCKIN;
        		title = "退货入库";
        }
        DbOperation dbOp = new DbOperation();
        dbOp.init(DbOperation.DB_SLAVE);
        IStockService serviceSlave = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        WareService wareService = new WareService();
        IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
        FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IMEIService IMEISericve = new IMEIService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
        DbOperation db = service.getDbOp();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSet rers = null;
		PreparedStatement reps=null;
		ResultSet rs0 = null;
    	try{
    		synchronized(stockLock){
    			
    			try{
    				FinanceSaleBaseDataService financeBaseDataService = FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN, service.getDbOp().getConn());
    				financeBaseDataService.acquireFinanceAfterSaleBaseData(null, null, user.getId(), 
    						DateUtil.getNow(), ProductStockBean.STOCKTYPE_RETURN, wareArea, 
    						StockCardBean.CARDTYPE_CANCELORDERSTOCKIN, null);
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    			
    			/**
    			 * 提交退货部分
    			 */
    			voOrder order=wareService.getOrder(Integer.parseInt(orderId));
    			if (order == null) {
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"这个订单不存在！\"}");
    	    		return;
    			}
    			if( order.getStatus() == 11 ) {
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"该订单已退回不可再退！\"}");
    	    		return;
    			}
    			ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + order.getCode() + "'");
    			if( rpBean == null ) {
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"在待退货包裹列表中没有找到对应该订单的记录！\"}");
    	    		return;
    			}  else {
    				if( rpBean.getArea() != wareArea) {
    					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"所选地区和待退货地区不同！\"}");
    		    		return;
    				}
    				if( rpBean.getStatus() == ReturnedPackageBean.STATUS_HAS_RETURN) {
    					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"退货包裹已经入库 ！\"}");
    		    		return;
    				}
    			}
    			
    			String orderCode=order.getCode();
    			String name = orderCode + "_" + DateUtil.getNow().substring(0, 10) + "_" + title;
    			// 从新的订单出货表中 查找订单的出货情况
    			OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and status<>" + OrderStockBean.STATUS5 + " and order_code ='" + orderCode + "'");
    			
    			if(oper != null && oper.getStatus() == OrderStockBean.STATUS8) {
     	        	response.getWriter().write("{\"result\":\"failure\",\"tip\":\"该订单包裹已经退回过！\"}");
     	        	return;
    			}
    			
    			if (oper == null || oper.getStatus() != OrderStockBean.STATUS3) {
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"这个订单还没有进行出库操作！\"}");
    				return;
    			}
    			// 从新的订单出货 体系中 查找 出货记录
    			List operHisList = service.getOrderStockProductList("order_stock_id=" + oper.getId(), -1, -1, null);

    			service.getDbOp().startTransaction();
    			List<StockHistoryBean> stockHistoryBeans = new ArrayList<StockHistoryBean>();
    			Iterator itr = operHisList.iterator();
    			OrderStockProductBean stockHis = null;
    			StockHistoryBean sh = null;
    			while (itr.hasNext()) {
    				stockHis = (OrderStockProductBean) itr.next();
    				sh = new StockHistoryBean();
    				sh.setCreateDatetime(DateUtil.getNow());
    				sh.setDealDatetime(null);
    				sh.setOperId(rpBean.getId());
    				sh.setOperType(stockType);
    				sh.setProductCode(stockHis.getProductCode());
    				sh.setProductId(stockHis.getProductId());
    				sh.setRemark("");
    				sh.setStatus(StockHistoryBean.UNDEAL);
    				sh.setStockBj(stockHis.getStockoutCount());
    				sh.setStockType(StockHistoryBean.IN);
    				stockHistoryBeans.add(sh);
    			}

    			StockAdminHistoryBean log = new StockAdminHistoryBean();
    			log.setAdminId(user.getId());
    			log.setAdminName(user.getUsername());
    			log.setLogId(rpBean.getId());
    			log.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
    			log.setOperDatetime(DateUtil.getNow());
    			log.setRemark("新建" + title + "操作：" + name);
    			log.setType(StockAdminHistoryBean.CREATE);
    			if(!service.addStockAdminHistory(log)){
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加StockAdminHistoryBean失败！\"}");
    	    		return;
    			}

    			/**
    			 * 确认入库部分
    			 */


    			
    			Iterator itr2 = stockHistoryBeans.iterator();
    			StockHistoryBean sh2 = null;
    			voProduct product = null;
    			String set = null;
    			int count = 0;
    			while (itr2.hasNext()) {
    				sh2 = (StockHistoryBean) itr2.next();
    				//EMEI相关操作
    				List<IMEIUserOrderBean> iuoList = IMEISericve.getIMEIUserOrderList("order_id="+orderId+" and product_id="+sh2.getProductId(),-1,-1,"id");
    				if(iuoList!=null){
    					for(int i=0;i<iuoList.size();i++){
    						IMEIUserOrderBean iuoBean = (IMEIUserOrderBean)iuoList.get(i);
    						//修改IMEI状态
    						if(!IMEISericve.updateIMEI("status="+IMEIBean.IMEISTATUS2, "code='"+iuoBean.getImeiCode()+"'")){
            					service.getDbOp().rollbackTransaction();
            					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更改IMEI状态失败！\"}");
            					return;
        					}
    						//保存IMEI日志
    						IMEILogBean IMEILogBean = new IMEILogBean();
    						IMEILogBean.setContent("销售退货入库"+",地区:"+ProductStockBean.areaMap.get(rpBean.getArea()));
    						IMEILogBean.setCreateDatetime(DateUtil.getNow());
    						IMEILogBean.setIMEI(iuoBean.getImeiCode());
    						IMEILogBean.setOperCode(order.getCode());
    						IMEILogBean.setOperType(IMEILogBean.OPERTYPE4);
    						IMEILogBean.setUserId(user.getId());
    						IMEILogBean.setUserName(user.getUsername());
    						if(!IMEISericve.addIMEILog(IMEILogBean)){
        						service.getDbOp().rollbackTransaction();
        						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加IMEI日志失败\"}");
        						return;
        					}
    					}
    				}
    				//烂货退换，入库数量可以为0
    				if(!(stockType == StockOperationBean.BAD_EXCHANGE) && sh2.getStockBj() == 0 && sh2.getStockGd() == 0){
    					service.getDbOp().rollbackTransaction();
    					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"入库数量为0，无法入库！\"}");
    		    		return;
    				}
    				count++;
    				product = wareService.getProduct(sh2.getProductId());
    				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));

    				// 需要计算一下 库存价格
    				//voOrder order = adminService.getOrder("code=\"" + bean.getOrderCode() + "\"");
    				float price5 = 0;
    				voOrderProduct orderProduct = wareService.getOrderProductSplit(order.getId(), product.getCode());
    				if(orderProduct == null){
    					orderProduct = wareService.getOrderPresentSplit(order.getId(), product.getCode());
    				}

    				//出库库存价丢失补救****
    				if(orderProduct.getPrice3() == 0){

    					//获取出货前最后一条进销存记录
    					int outId = service.getNumber("id", "stock_card", null, "code = '"+orderCode+"' and card_type = "+StockCardBean.CARDTYPE_ORDERSTOCK+" and product_id = "+sh2.getProductId());
    					int scId = service.getNumber("id", "stock_card", "max", "id < "+outId+" and product_id = "+sh2.getProductId());
    					StockCardBean stockCard = psService.getStockCard("id = "+scId);
    					orderProduct.setPrice3(stockCard.getStockPrice());

    				}

    				if(orderProduct != null){
    				//	int totalCount = product.getStock(ProductStockBean.AREA_BJ) + product.getStock(ProductStockBean.AREA_GF) + product.getStock(ProductStockBean.AREA_GS) + product.getLockCount(ProductStockBean.AREA_BJ) + product.getLockCount(ProductStockBean.AREA_GF) + product.getLockCount(ProductStockBean.AREA_GS);
    					int totalCount = product.getStockAll() + product.getLockCountAll();
    					StockBatchLogBean batchLog = service.getStockBatchLog("code='"+order.getCode()+"' and product_id="+orderProduct.getProductId());
    					if(batchLog==null){
    					}
    					price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * sh2.getStockBj())) / (totalCount + sh2.getStockBj()) * 1000))/1000;
    				}

    				// 更新库存
    				//psService.updateProductStock("stock=(stock + " + (sh2.getStockBj() + sh2.getStockGd()) + ")", "product_id=" + sh2.getProductId() + " and area=" + bean.getArea() + " and type=" + ProductStockBean.STOCKTYPE_RETURN);
    				ProductStockBean ps = psService.getProductStock("product_id=" + sh2.getProductId() + " and area=" + wareArea + " and type=" + ProductStockBean.STOCKTYPE_RETURN);
    				if(ps == null){
    					service.getDbOp().rollbackTransaction();
    					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"没有找到产品库存，操作失败！\"}");
    		    		return;
    				}
    				if(!psService.updateProductStockCount(ps.getId(), (sh2.getStockBj() + sh2.getStockGd()))){
    					service.getDbOp().rollbackTransaction();
    					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"库存操作失败，可能是库存不足，请与管理员联系！\"}");
    		    		return;
    				}

    				//更新货位库存2011-04-22
    				CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ps.getArea());
    				CargoProductStockBean cps = null;
    				List cocList = cargoService.getCargoAndProductStockList(
    						"ci.stock_type = "+ps.getType()+" and ci.area_id = "+inCargoArea.getId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2+
    						" and cps.product_id = "+sh2.getProductId(), -1, -1, "ci.id desc");
    				if(cocList == null || cocList.size() == 0){//产品首次入库，无暂存区绑定货位库存信息
    					CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ps.getType()+" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2);
    					if(cargo == null){
    						service.getDbOp().rollbackTransaction();
    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"目的退货库缓存区货位未设置，请先添加后再完成入库！\"}");
    			    		return;
    					}
    					cps = new CargoProductStockBean();
    					cps.setCargoId(cargo.getId());
    					cps.setProductId(sh2.getProductId());
    					cps.setStockCount(sh2.getStockBj() + sh2.getStockGd());
    					if(!cargoService.addCargoProductStock(cps))
    					{
    					  service.getDbOp().rollbackTransaction();
    					  response.getWriter().write("{\"result\":\"failure\",\"tip\":\"数据库操作失败！\"}");
    					  return;
    					}
    					cps.setId(cargoService.getDbOp().getLastInsertId());

    					if(!cargoService.updateCargoInfo("status = 0", "id = "+cargo.getId())){
    						service.getDbOp().rollbackTransaction();
    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新货位状态失败！\"}");
    			    		return;
    					}
    				}else{
    					cps = (CargoProductStockBean)cocList.get(0);
    					if(!cargoService.updateCargoProductStockCount(cps.getId(), (sh2.getStockBj() + sh2.getStockGd()))){
    						service.getDbOp().rollbackTransaction();
    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新货位库存失败！\"}");
    			    		return;
    					}
    				}


    				//获取相关批次信息，处理退货库批次
    				int stockinCount = sh2.getStockBj();
    				int batchCount = 0;
    				double stockinPrice = 0;
    				List batchLogList = serviceSlave.getStockBatchLogList("code='"+order.getCode()+"' and product_id="+sh2.getProductId()+" and remark = '订单出货'", -1, -1, "id desc");
    				if(batchLogList==null||batchLogList.size()==0){

    					String code = "X"+DateUtil.getNow().substring(0,10).replace("-", "");
    					StockBatchBean newBatch;
    					newBatch = service.getStockBatch("code like '" + code + "%' and product_id="+sh2.getProductId());
    					int ticket = 0;
    					int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), sh2.getProductId(), ticket);
    					if(newBatch == null){
    						//当日第一份批次记录，编号最后三位 001
    						code += "001";
    					}else {
    						//获取当日计划编号最大值
    						newBatch = service.getStockBatch("code like '" + code + "%' and product_id="+sh2.getProductId()+" order by id desc limit 1"); 
    						String _code = newBatch.getCode();
    						int number = Integer.parseInt(_code.substring(_code.length()-3));
    						number++;
    						code += String.format("%03d",new Object[]{new Integer(number)});
    					}
    					newBatch = new StockBatchBean();
    					newBatch.setCode(code);
    					newBatch.setProductId(sh2.getProductId());
    					newBatch.setProductStockId(ps.getId());
    					newBatch.setStockArea(wareArea);
    					newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
    					newBatch.setProductStockId(ps.getId());
    					newBatch.setCreateDateTime(DateUtil.getNow());
    					newBatch.setPrice(orderProduct.getPrice3());
    					newBatch.setBatchCount(stockinCount);

    					if(!service.addStockBatch(newBatch)){
    						service.getDbOp().rollbackTransaction();
    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加库存批次信息失败！\"}");
    			    		return;
    					}

    					//添加批次操作记录
    					StockBatchLogBean batchLog = new StockBatchLogBean();
    					batchLog.setCode(orderCode);
    					batchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
    					batchLog.setStockArea(wareArea);
    					batchLog.setBatchCode(newBatch.getCode());
    					batchLog.setBatchCount(stockinCount);
    					batchLog.setBatchPrice(newBatch.getPrice());
    					batchLog.setProductId(newBatch.getProductId());
    					batchLog.setRemark("退货入库");
    					batchLog.setCreateDatetime(DateUtil.getNow());
    					batchLog.setUserId(user.getId());
    					if(!service.addStockBatchLog(batchLog)){
    						service.getDbOp().rollbackTransaction();
    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加库存批次日志信息失败！\"}");
    			    		return;
    					}

    					stockinPrice = batchLog.getBatchCount()*batchLog.getBatchPrice();
    					
    					//财务产品信息表---liuruilan-----2012-11-01-----
    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh2.getProductId());
    					if(fProduct == null){
							service.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"查询异常，请与管理员联系！\"}");
							return;
						}
    					int totalCount = product.getStockAll() + product.getLockCountAll();
						float priceSum = Arith.mul(price5, totalCount);
						
						//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
						float priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(batchLog.getBatchPrice(), stockinCount)), Arith.add(_count, stockinCount)), 2);
						float priceSumHasticket = Arith.mul(priceHasticket,  stockinCount + _count);
						set = "price =" + price5 + ", price_sum =" + priceSum + ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
						if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())){
							service.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新财务商品均价失败！\"}");
							return;
						}
												
						//财务进销存卡片
						product.setPsList(psService.getProductStockList("product_id=" + sh2.getProductId(), -1, -1, null));
    					int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), batchLog.getStockType(), ticket, sh2.getProductId());
    					int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, batchLog.getStockType(), ticket,sh2.getProductId());
						int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), -1,ticket,sh2.getProductId());
    					FinanceStockCardBean fsc = new FinanceStockCardBean();
    					fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
    					fsc.setCode(order.getCode());
    					fsc.setCreateDatetime(DateUtil.getNow());
    					fsc.setStockType(batchLog.getStockType());
    					fsc.setStockArea(batchLog.getStockArea());
    					fsc.setProductId(sh2.getProductId());
    					fsc.setStockId(ps.getId());
    					fsc.setStockInCount(-batchLog.getBatchCount());	
    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
    					fsc.setStockAllArea(stockAllArea);
    					fsc.setStockAllType(stockAllType);
    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
    					fsc.setStockPrice(price5);
    					
    					fsc.setType(fsc.getCardType());
    					fsc.setIsTicket(ticket);
    					fsc.setStockBatchCode(batchLog.getBatchCode());
    					fsc.setBalanceModeStockCount(stockinCount + _count);
    					fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(batchLog.getBatchPrice(), stockinCount))));
						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
    					double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
    					fsc.setAllStockPriceSum(tmpPrice);
    					if(!frfService.addFinanceStockCardBean(fsc)){
							service.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加财务进销存卡片失败！\"}");
				    		return;
    					}
    					
    					//将订单商品写入销售商品信息表--商品
    					
    					
    					
    					FinanceSellBean fsbean= frfService.getFinanceSellBean(" data_type=0 and order_id="+order.getId());
    					FinanceSellBean fsbean1=null;
    					List fspList=null;//销售订单里面所有产品
    					
    					if(fsbean!=null&&fsbean.getId()!=0){
    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
    						//如果已经有了销售未妥投订单不做任务操作，
    						if(fsbean1!=null&&fsbean1.getId()!=0){
    							
    							//否则向finance_sell,finance_sell_product表添加
    						}else{
    							fsbean.setId(0);
    							fsbean.setCreateDatetime(DateUtil.getNow());
    							fsbean.setDataType(1);
    							fsbean.setCount(-1);
    							fsbean.setPrice(Arith.round(Arith.mul(fsbean.getPrice(),-1),2));
    							fsbean.setCarriage(-order.getPostage());
    							//向finance_sell表添加未妥投退货记录
    							if(!frfService.addFinanceSellBean(fsbean))
    							{
    							  service.getDbOp().rollbackTransaction();
    							  response.getWriter().write("{\"result\":\"failure\",\"tip\":\"向finance_sell表添加未妥投退货记录失败！\"}");
    							  return;
    							}
    							int financeSellId=frfService.getDbOp().getLastInsertId();
    							fspList=frfService.getFinanceSellProductBeanList(" (data_type=1 or data_type=0) and order_id="+order.getId(), -1, -1, null);
    							if(fspList!=null&&fspList.size()>0){
    								FinanceSellProductBean fspbean=null;
    								for(int i=0;i<fspList.size();i++){
    									fspbean=(FinanceSellProductBean)fspList.get(i);
    									fspbean.setId(0);
    									fspbean.setFinanceSellId(financeSellId);
    									fspbean.setBuyCount(-fspbean.getBuyCount());
    									if(fspbean.getDataType()==0){
    										fspbean.setDataType(2);
    									}
    									if(fspbean.getDataType()==1){
    										fspbean.setDataType(3);
    									}
    									fspbean.setCreateDatetime(DateUtil.getNow());
    									if(!frfService.addFinanceSellProductBean(fspbean)){
    										service.getDbOp().rollbackTransaction();
    										response.getWriter().write("{\"result\":\"failure\",\"tip\":\"向finance_sell_product表添加未妥投退货记录失败！\"}");
    							    		return;
    									}//向finance_sell_product表添加未妥投退货记录
    									
    								}
    							}
    						}
    					}else{
    						//如果finance_sell表中没有找到销售订单，要查看一下finance_sell表里面有没有未妥投退单，如果有则什么也没做
    						//如果没有则向否则向finance_sell,finance_sell_product表添加记录
    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
    						if(fsbean1!=null&&fsbean1.getId()!=0){
    							
    							//否则向finance_sell,finance_sell_product表添加
    						}else{
    					
    							
    							int deliverType = 0;
		    					if(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()) != null){
		    						deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()).toString());
		    					}
		    					FinanceSellBean fsBean = new FinanceSellBean();
		    					fsBean.setOrderId(order.getId());
		    					fsBean.setCode(order.getCode());
		    					fsBean.setPrice(-Arith.sub(order.getDprice(), order.getPostage()));	//退货金额为负
		    					fsBean.setCarriage(-order.getPostage());	//“运费”和订单符号一致
		    					fsBean.setCharge(0);
		    					fsBean.setBuyMode(order.getBuyMode());
		    					fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
		    					fsBean.setDeliverType(deliverType);
		    					fsBean.setCreateDatetime(DateUtil.getNow());
		    					fsBean.setPackageNum(order.getPackageNum());
		    					fsBean.setDataType(1);	//1-未妥投退回
		    					fsBean.setCount(-1);
		    					if(!frfService.addFinanceSellBean(fsBean)){
									service.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加状态为未妥投退回FinanceSellBean失败！\"}");
						    		return;
		    					}
    							int financeSellId=frfService.getDbOp().getLastInsertId();
    							
    							int supplierId = FinanceSellProductBean.querySupplier(db, batchLog.getBatchCode());
		    					String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
		    								+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
		    								+ "WHERE h.order_id = ? ";
		    					db.prepareStatement(sql);
		    					pstmt = db.getPStmt();
		    					pstmt.setInt(1, order.getId());
		    					rs = pstmt.executeQuery();
		    					while(rs.next()){
		    						int pId = rs.getInt("id");
		    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
		    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
		    						db.prepareStatement(sql);
		    						pstmt = db.getPStmt();
		    						pstmt.setInt(1,pId);
		    						rs0 = pstmt.executeQuery();
		    						
		    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
		    						fspBean.setOrderId(order.getId());
		    						fspBean.setProductId(rs.getInt("id"));
		    						fspBean.setBuyCount(-rs.getInt("count"));
		    						fspBean.setPrice(rs.getFloat("price"));
		    						fspBean.setDprice(rs.getFloat("dprice"));
		    						fspBean.setFinanceSellId(financeSellId);
		    						fspBean.setPrice5(rs.getFloat("price5"));
		    						if(rs0.next()){
		    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
		    						}
		    						fspBean.setParentId1(rs.getInt("parent_id1"));
		    						fspBean.setParentId2(rs.getInt("parent_id2"));
		    						fspBean.setParentId3(rs.getInt("parent_id3"));
		    						fspBean.setCreateDatetime(DateUtil.getNow());
		    						fspBean.setDataType(2);	//2-商品未妥投退回
		    						fspBean.setBalanceMode(ticket);
		    						fspBean.setSupplierId(supplierId);
		    						
		    						if(!frfService.addFinanceSellProductBean(fspBean)){
										service.getDbOp().rollbackTransaction();
										response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加状态为未妥投退回FinanceSellProductBean失败！\"}");
							    		return;
		    						}
		    					}
    					
    					//赠品
		    					sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
		    						+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
		    						+ "WHERE h.order_id = ? ";
		    					db.prepareStatement(sql);
		    					pstmt = db.getPStmt();
		    					pstmt.setInt(1, order.getId());
		    					rs = pstmt.executeQuery();
		    					while(rs.next()){
		    						int pId = rs.getInt("id");
		    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
		    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
		    						db.prepareStatement(sql);
		    						pstmt = db.getPStmt();
		    						pstmt.setInt(1,pId);
		    						rs0 = pstmt.executeQuery();
		    						
		    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
		    						fspBean.setOrderId(order.getId());
		    						fspBean.setProductId(rs.getInt("id"));
		    						fspBean.setBuyCount(-rs.getInt("count"));
		    						fspBean.setPrice(rs.getFloat("price"));
		    						fspBean.setDprice(rs.getFloat("dprice"));
		    						fspBean.setPrice5(rs.getFloat("price5"));
		    						if(rs0.next()){
		    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
		    						}
		    						fspBean.setParentId1(rs.getInt("parent_id1"));
		    						fspBean.setParentId2(rs.getInt("parent_id2"));
		    						fspBean.setParentId3(rs.getInt("parent_id3"));
		    						fspBean.setCreateDatetime(DateUtil.getNow());
		    						fspBean.setFinanceSellId(financeSellId);
		    						fspBean.setDataType(3);	//3-赠品未妥投退回
		    						fspBean.setBalanceMode(ticket);
		    						fspBean.setSupplierId(supplierId);
		    						
		    						if(!frfService.addFinanceSellProductBean(fspBean)){
										service.getDbOp().rollbackTransaction();
										response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加状态为未妥投退回FinanceSellProductBean赠品失败！\"}");
							    		return;
		    						}
		    					}
		    					
		    					
    						  }
    						}
    					//-------------liuruilan-------------
    					
    				}else{
    					Iterator batchIter = batchLogList.listIterator();

    					while(batchIter.hasNext()&&stockinCount>0){
    						StockBatchLogBean batchLog = (StockBatchLogBean)batchIter.next(); 
    						StockBatchBean batch = service.getStockBatch("code = '"+batchLog.getBatchCode()+"' and product_id="+batchLog.getProductId()+" and stock_type="+ProductStockBean.STOCKTYPE_RETURN+" and stock_area="+wareArea);
    						int ticket = FinanceSellProductBean.queryTicket(db, batchLog.getBatchCode());	//是否含票 
	                		if(ticket == -1){
	    						service.getDbOp().rollbackTransaction();
	    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"查询异常，请与管理员联系！\"}");
	    			    		return;
	    					}
	                		int _count = FinanceProductBean.queryCountIfTicket(db, sh2.getProductId(), ticket);
    						if(batch!=null){
    							if(stockinCount<=batchLog.getBatchCount()){
    								if(!service.updateStockBatch("batch_count = batch_count+"+stockinCount, "id="+batch.getId())){
    		    						service.getDbOp().rollbackTransaction();
    		    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新批次数量失败！\"}");
    		    			    		return;
    								}
    								batchCount = stockinCount;
    								stockinCount = 0;
    							}else{
    								if(!service.updateStockBatch("batch_count = batch_count+"+batchLog.getBatchCount(), "id="+batch.getId())){
    		    						service.getDbOp().rollbackTransaction();
    		    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新批次数量失败！\"}");
    		    			    		return;
    								}
    								stockinCount -= batchLog.getBatchCount();
    								batchCount = batchLog.getBatchCount();
    							}

    						}else{
    							
    							StockBatchBean newBatch = new StockBatchBean();
    							newBatch.setCode(batchLog.getBatchCode());
    							newBatch.setProductId(sh2.getProductId());
    							newBatch.setProductStockId(ps.getId());
    							newBatch.setStockArea(wareArea);
    							newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
    							newBatch.setProductStockId(ps.getId());
    							newBatch.setCreateDateTime(service.getStockBatchCreateDatetime(batchLog.getBatchCode(),sh2.getProductId()));
    							newBatch.setPrice(batchLog.getBatchPrice());

    							if(stockinCount<=batchLog.getBatchCount()){
    								newBatch.setBatchCount(stockinCount);
    								batchCount = stockinCount;
    								stockinCount = 0;
    							}else{
    								newBatch.setBatchCount(batchLog.getBatchCount());
    								stockinCount -= batchLog.getBatchCount();
    								batchCount = batchLog.getBatchCount();
    							}

    							if(!service.addStockBatch(newBatch)){
		    						service.getDbOp().rollbackTransaction();
		    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新批次数量失败！\"}");
		    			    		return;
    							}
    						}

    						stockinPrice = stockinPrice + batchLog.getBatchPrice()*batchCount;

    						//添加批次操作记录
    						StockBatchLogBean newBatchLog = new StockBatchLogBean();
    						newBatchLog.setCode(orderCode);
    						newBatchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
    						newBatchLog.setStockArea(wareArea);
    						newBatchLog.setBatchCode(batchLog.getBatchCode());
    						newBatchLog.setBatchCount(batchCount);
    						newBatchLog.setBatchPrice(batchLog.getBatchPrice());
    						newBatchLog.setProductId(batchLog.getProductId());
    						newBatchLog.setRemark("退货入库");
    						newBatchLog.setCreateDatetime(DateUtil.getNow());
    						newBatchLog.setUserId(user.getId());
    						if(!service.addStockBatchLog(newBatchLog)){
	    						service.getDbOp().rollbackTransaction();
	    						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加批次操作日志失败！\"}");
	    			    		return;
    						}

    						//财务产品信息表---liuruilan-----2012-11-01-----
	    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh2.getProductId());
	    					if(fProduct == null){
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"查询异常，请与管理员联系！\"}");
					    		return;
							}
	    					int totalCount = product.getStockAll() + product.getLockCountAll();
							float priceSum = Arith.mul(price5, totalCount);
							float priceHasticket = 0;
							float priceNoticket = 0;
							float priceSumHasticket = 0;
							float priceSumNoticket = 0;
							set = "price =" + price5 + ", price_sum =" + priceSum;
							float sqlPrice5=0;
							if(ticket == 0){	//0-有票
								//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
								//获取出库价格
								String sqlPrice="select price5 from finance_sell_product where product_id="+batchLog.getProductId()+" and data_type=0 and balance_mode="+ticket+" and order_id="+order.getId();
								reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
								rers=reps.executeQuery();
								boolean flag=false;//为false,finance_sell_product 没有记录，为true,finance_sell_product有记录
								while(rers.next()){
									flag=true;
									sqlPrice5=rers.getFloat(1);
								}
								if(!flag){
									
									//如果finance_sell_productm 没有记录要在user_order_product_split_history里面找
									sqlPrice="select price5 from user_order_product_split_history psplit where product_id="+batchLog.getProductId()+" and order_id="+order.getId();
									reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
									rers=reps.executeQuery();
									while(rers.next()){
										flag=true;
										sqlPrice5=rers.getFloat("price5");
									}
									if(!flag){//如果user_order_product_split_history 没有记录要在user_order_present_split_history里面找
										sqlPrice="select price5 from user_order_present_split_history psplit where product_id="+batchLog.getProductId()+" and order_id="+order.getId();
										reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
										rers=reps.executeQuery();
										while(rers.next()){
											flag=true;
											sqlPrice5=rers.getFloat("price5");
										}
									}
								}
								//-------------------------------获得出库价end --------------------------------------------
								priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(sqlPrice5, batchCount)), Arith.add(_count, batchCount)), 2);
								priceSumHasticket = Arith.mul(priceHasticket,  batchCount + _count);
								set += ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
							}
							if(ticket == 1){	//1-无票
								
								String sqlPrice="select price5 from finance_sell_product where product_id="+batchLog.getProductId()+" and data_type=0 and balance_mode="+ticket+" and order_id="+order.getId();
								reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
								rers=reps.executeQuery();
								
								while(rers.next()){
									sqlPrice5=rers.getFloat(1);
								}
								priceNoticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumNoticket(), Arith.mul(batchLog.getBatchPrice(), batchCount)), Arith.add(_count, batchCount)), 2);
								priceSumNoticket = Arith.mul(priceNoticket,  batchCount + _count);
								set += ", price_noticket =" + priceNoticket + ", price_sum_noticket =" + priceSumNoticket;
							}
							if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())){
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新FinanceProductBean失败！\"}");
					    		return;
							}
							product.setPsList(psService.getProductStockList("product_id=" + sh2.getProductId(), -1, -1, null));
							
							//财务进销存卡片
							
							
	    					int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatchLog.getStockArea(), newBatchLog.getStockType(), ticket, sh2.getProductId());
	    					int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, newBatchLog.getStockType(), ticket, sh2.getProductId());
							int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatchLog.getStockArea(), -1,ticket, sh2.getProductId());
	    					FinanceStockCardBean fsc = new FinanceStockCardBean();
	    					fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
	    					fsc.setCode(order.getCode());
	    					fsc.setCreateDatetime(DateUtil.getNow());
	    					fsc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
	    					fsc.setStockArea(wareArea);
	    					fsc.setProductId(sh2.getProductId());
	    					fsc.setStockId(ps.getId());
	    					fsc.setStockInCount(-newBatchLog.getBatchCount());	
	    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
	    					fsc.setStockAllArea(stockAllArea);
	    					fsc.setStockAllType(stockAllType);
	    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
	    					fsc.setStockPrice(price5);
	    					
	    					fsc.setType(fsc.getCardType());
	    					fsc.setIsTicket(ticket);
	    					fsc.setStockBatchCode(batchLog.getBatchCode());
	    					fsc.setBalanceModeStockCount(batchCount + _count);
	    					fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(sqlPrice5, batchCount))));
	    					if(ticket == 0){
	    						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
	    					}
	    					if(ticket == 1){
	    						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceNoticket)));
	    					}
	    					double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumNoticket(), fProduct.getPriceSumHasticket()))), fsc.getStockInPriceSum());
	    					fsc.setAllStockPriceSum(tmpPrice);
	    					if(!frfService.addFinanceStockCardBean(fsc)){
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加财务卡片失败！\"}");
					    		return;
	    					}
	    					
	    					//将订单商品写入销售商品信息表--商品
	    					
	    					
	    					FinanceSellBean fsbean= frfService.getFinanceSellBean(" data_type=0 and order_id="+order.getId());
	    					FinanceSellBean fsbean1=null;
	    					List fspList=null;//销售订单里面所有产品
	    					
	    					if(fsbean!=null&&fsbean.getId()!=0){
	    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
	    						//如果已经有了销售未妥投订单不做任务操作，
	    						if(fsbean1!=null&&fsbean1.getId()!=0){
	    							
	    							//否则向finance_sell,finance_sell_product表添加
	    						}else{
	    							fsbean.setId(0);
	    							fsbean.setCreateDatetime(DateUtil.getNow());
	    							fsbean.setDataType(1);
	    							fsbean.setCount(-1);
	    							fsbean.setPrice(Arith.round(Arith.mul(fsbean.getPrice(),-1),2));
	    							fsbean.setCarriage(-order.getPostage());
	    							if(!frfService.addFinanceSellBean(fsbean)){
	    								service.getDbOp().rollbackTransaction();
	    								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"向finance_sell表添加未妥投退货记录失败！\"}");
	    					    		return;
	    							}//向finance_sell表添加未妥投退货记录
	    							int financeSellId=frfService.getDbOp().getLastInsertId();
	    							fspList=frfService.getFinanceSellProductBeanList(" (data_type=1 or data_type=0) and order_id="+order.getId(), -1, -1, null);
	    							if(fspList!=null&&fspList.size()>0){
	    								FinanceSellProductBean fspbean=null;
	    								for(int i=0;i<fspList.size();i++){
	    									fspbean=(FinanceSellProductBean)fspList.get(i);
	    									fspbean.setId(0);
	    									fspbean.setFinanceSellId(financeSellId);
	    									fspbean.setBuyCount(-fspbean.getBuyCount());
	    									if(fspbean.getDataType()==0){
	    										fspbean.setDataType(2);
	    									}
	    									if(fspbean.getDataType()==1){
	    										fspbean.setDataType(3);
	    									}
	    									fspbean.setCreateDatetime(DateUtil.getNow());
	    									if(!frfService.addFinanceSellProductBean(fspbean)){
	    	    								service.getDbOp().rollbackTransaction();
	    	    								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"向finance_sell表添加未妥投退货记录失败！\"}");
	    	    					    		return;
	    									}//向finance_sell_product表添加未妥投退货记录
	    									
	    								}
	    							}
	    						}
	    					}else{
	    						//如果finance_sell表中没有找到销售订单，要查看一下finance_sell表里面有没有未妥投退单，如果有则什么也没做
	    						//如果没有则向否则向finance_sell,finance_sell_product表添加记录
	    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
	    						if(fsbean1!=null&&fsbean1.getId()!=0){
	    							
	    							//否则向finance_sell,finance_sell_product表添加
	    						}else{
	    					
	    							int deliverType = 0;
	    	    					if(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()) != null){
	    	    						deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()).toString());
	    	    					}
	    	    					FinanceSellBean fsBean = new FinanceSellBean();
	    	    					fsBean.setOrderId(order.getId());
	    	    					fsBean.setCode(order.getCode());
	    	    					fsBean.setPrice(-Arith.sub(order.getDprice(), order.getPostage()));	//退货金额为负
	    	    					fsBean.setCarriage(-order.getPostage());	//“运费”和订单符号一致
	    	    					fsBean.setCharge(0);
	    	    					fsBean.setBuyMode(order.getBuyMode());
	    	    					fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
	    	    					fsBean.setDeliverType(deliverType);
	    	    					fsBean.setCreateDatetime(DateUtil.getNow());
	    	    					fsBean.setPackageNum(order.getPackageNum());
	    	    					fsBean.setDataType(1);	//1-未妥投退回
	    	    					fsBean.setCount(-1);
	    	    					if(!frfService.addFinanceSellBean(fsBean)){
	    								service.getDbOp().rollbackTransaction();
	    								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加状态为未妥投退回FinanceSellBean失败！\"}");
	    					    		return;
	    	    					}
	    	    					int financeSellId=frfService.getDbOp().getLastInsertId();
	    					
	    					int supplierId = FinanceSellProductBean.querySupplier(db, batchLog.getBatchCode());
	    					String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
	    								+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
	    								+ "WHERE h.order_id = ? ";
	    					db.prepareStatement(sql);
	    					pstmt = db.getPStmt();
	    					pstmt.setInt(1, order.getId());
	    					rs = pstmt.executeQuery();
	    					while(rs.next()){
	    						int pId = rs.getInt("id");
	    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
	    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
	    						db.prepareStatement(sql);
	    						pstmt = db.getPStmt();
	    						pstmt.setInt(1,pId);
	    						rs0 = pstmt.executeQuery();
	    						
	    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
	    						fspBean.setOrderId(order.getId());
	    						fspBean.setProductId(rs.getInt("id"));
	    						fspBean.setBuyCount(-rs.getInt("count"));
	    						fspBean.setPrice(rs.getFloat("price"));
	    						fspBean.setFinanceSellId(financeSellId);
	    						fspBean.setDprice(rs.getFloat("dprice"));
//	    						fspBean.setPrice5(sqlPrice5);
	    						fspBean.setPrice5(rs.getFloat("price5"));
	    						if(rs0.next()){
	    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
	    						}
	    						fspBean.setParentId1(rs.getInt("parent_id1"));
	    						fspBean.setParentId2(rs.getInt("parent_id2"));
	    						fspBean.setParentId3(rs.getInt("parent_id3"));
	    						fspBean.setCreateDatetime(DateUtil.getNow());
	    						fspBean.setDataType(2);	//2-商品未妥投退回
	    						fspBean.setBalanceMode(ticket);
	    						fspBean.setSupplierId(supplierId);
	    						
	    						if(!frfService.addFinanceSellProductBean(fspBean)){
									service.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加状态为未妥投退回FinanceSellProductBean失败！\"}");
						    		return;
	    						}
	    					}
	    					
	    					//赠品
	    					sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
	    						+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
	    						+ "WHERE h.order_id = ? ";
	    					db.prepareStatement(sql);
	    					pstmt = db.getPStmt();
	    					pstmt.setInt(1, order.getId());
	    					rs = pstmt.executeQuery();
	    					while(rs.next()){
	    						int pId = rs.getInt("id");
	    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
	    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
	    						db.prepareStatement(sql);
	    						pstmt = db.getPStmt();
	    						pstmt.setInt(1,pId);
	    						rs0 = pstmt.executeQuery();
	    						
	    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
	    						fspBean.setOrderId(order.getId());
	    						fspBean.setProductId(rs.getInt("id"));
	    						fspBean.setBuyCount(-rs.getInt("count"));
	    						fspBean.setPrice(rs.getFloat("price"));
	    						fspBean.setDprice(rs.getFloat("dprice"));
	    						fspBean.setFinanceSellId(financeSellId);
//	    						fspBean.setPrice5(sqlPrice5);
	    						fspBean.setPrice5(rs.getFloat("price5"));
	    						if(rs0.next()){
	    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
	    						}
	    						fspBean.setParentId1(rs.getInt("parent_id1"));
	    						fspBean.setParentId2(rs.getInt("parent_id2"));
	    						fspBean.setParentId3(rs.getInt("parent_id3"));
	    						fspBean.setCreateDatetime(DateUtil.getNow());
	    						fspBean.setDataType(3);	//3-赠品未妥投退回
	    						fspBean.setBalanceMode(ticket);
	    						fspBean.setSupplierId(supplierId);
	    						
	    						
	    						if(!frfService.addFinanceSellProductBean(fspBean)){
									service.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加状态为未妥投退回FinanceSellProductBean赠品失败！\"}");
						    		return;
	    						}
	    					}
	    					//将订单数据写入发货信息表（财务统计用）---liuruilan---2012-9-13-----
	    				
	    						}
	    						
	    					}
	    					//-------------liuruilan-------------
    					}

    				}
    				int totalCount = product.getStockAll() + product.getLockCountAll();
    				//            	price5 = ((float)Math.round((product.getPrice5() * totalCount + stockinPrice) /(totalCount + sh2.getStockBj()) * 1000))/1000;
    				price5 = ((float)Math.round((product.getPrice5() * totalCount + (orderProduct.getPrice3() * sh2.getStockBj())) / (totalCount + sh2.getStockBj()) * 1000))/1000;
    				if(!service.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId())){
						service.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新商品均价失败！\"}");
			    		return;
    				}

    				//log记录
    				StockAdminHistoryBean log2 = new StockAdminHistoryBean();
    				log2.setAdminId(user.getId());
    				log2.setAdminName(user.getUsername());
    				log2.setLogId(rpBean.getId());
    				log2.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
    				log2.setOperDatetime(DateUtil.getNow());
    				log2.setRemark("退货入库操作：" + name + ",将(" + product.getName() + ")入库");
    				log2.setType(StockAdminHistoryBean.CHANGE);
    				if(!service.addStockAdminHistory(log2)){
						service.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加StockAdminHistoryBean失败！\"}");
			    		return;
    				}

    				// 审核通过，就加 进销存卡片
    				product.setPsList(psService.getProductStockList("product_id=" + sh2.getProductId(), -1, -1, null));
    				cps = cargoService.getCargoAndProductStock("cps.id = "+cps.getId());

    				// 入库卡片
    				StockCardBean sc = new StockCardBean();
    				sc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
    				//sc.setCode(bean.getCode());
    				sc.setCode(order.getCode());
    				sc.setCreateDatetime(DateUtil.getNow());
    				sc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
    				sc.setStockArea(wareArea);
    				sc.setProductId(sh2.getProductId());
    				sc.setStockId(ps.getId());
    				sc.setStockInCount(sh2.getStockBj());
    				//				sc.setStockInPriceSum(stockinPrice);
    				sc.setStockInPriceSum((new BigDecimal(sh2.getStockBj())).multiply(new BigDecimal(StringUtil.formatDouble2(orderProduct.getPrice3()))).doubleValue());
    				sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
    				sc.setStockAllArea(product.getStock(wareArea) + product.getLockCount(wareArea));
    				sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
    				sc.setAllStock(product.getStockAll() + product.getLockCountAll());
    				sc.setStockPrice(price5);
    				sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
    				if(!psService.addStockCard(sc)){
						service.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加入库卡片失败！\"}");
			    		return;
    				}

    				//货位入库卡片
    				CargoStockCardBean csc = new CargoStockCardBean();
    				csc.setCardType(CargoStockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
    				csc.setCode(order.getCode());
    				csc.setCreateDatetime(DateUtil.getNow());
    				csc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
    				csc.setStockArea(wareArea);
    				csc.setProductId(sh2.getProductId());
    				csc.setStockId(cps.getId());
    				csc.setStockInCount(sh2.getStockBj());
    				csc.setStockInPriceSum((new BigDecimal(sh2.getStockBj())).multiply(new BigDecimal(StringUtil.formatDouble2(orderProduct.getPrice3()))).doubleValue());
    				csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
    				csc.setAllStock(product.getStockAll() + product.getLockCountAll());
    				csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
    				csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
    				csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
    				csc.setStockPrice(price5);
    				csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
    				if(!cargoService.addCargoStockCard(csc)){
						service.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加货位入库卡片失败！\"}");
			    		return;
    				}
//
//					//将订单数据写入发货信息表（财务统计用）---liuruilan---2012-9-13-----
//					FinanceSellBean fsBean = new FinanceSellBean();
//					fsBean.setOrderId(order.getId());
//					fsBean.setCode(order.getCode());
//					fsBean.setPrice(-order.getDprice());	//退货金额为负
//					fsBean.setCarriage(0);	//运费未计算
//					fsBean.setCharge(0);
//					fsBean.setBuyMode(order.getBuyMode());
//					fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
//					fsBean.setDeliverType(order.getDeliver());
//					fsBean.setCreateDatetime(DateUtil.getNow());
//					fsBean.setPackageNum(order.getPackageNum());
//					fsBean.setDataType(1);	//1-未妥投退回
//					fsBean.setCount(-1);
//					frfService.addFinanceSellBean(fsBean);
//					// ---------liuruilan-----2012-09-13-------
					
					//-------------将商品信息插入退货商品表 柳波-------------------
//					ReturnedProductBean productBean = new ReturnedProductBean();
//					productBean.setCount(sh2.getStockBj() + sh2.getStockGd());
//					productBean.setProductCode(product.getCode());
//					productBean.setProductName(product.getOriname());
//					productBean.setProductId(product.getId());
//					statService.dealReturnedProduct(productBean);
					//-------------将商品信息插入退货商品表 柳波-------------------
					
    				// 如果参数包含产品ID，则只进行一次入库操作。
    				/*if(productId > 0){
    					break;
    				}*/
    			}

    			if (count == 0) {
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"该操作没有任何库存变动，不能执行！\"}");
    	    		return;
    			}

				//添加到退货包裹列表中---------------------柳波-----------------
    			//改为更新退货包裹列表----郝亚斌--
    			if( !updateReturnedPackage(rpBean, user, service, statService, order, ReturnedPackageBean.STATUS_HAS_RETURN) ) {
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"该操作没有任何库存变动，不能执行！\"}");
    	    		return;
    			}
    			//添加到退货包裹列表中---------------------柳波-----------------
    			
    			
				//service.updateStockOperation("status = " + StockOperationBean.STATUS5 , " status=" + StockOperationBean.STATUS3 + " and order_code = \"" + bean.getOrderCode() + "\"");
				//service.updateOrderStock("status = " + OrderStockBean.STATUS5 , " status=" + OrderStockBean.STATUS3 + " and order_code = \"" + bean.getOrderCode() + "\"");
				// 退货操作后，把订单出货状态设置为 用户退货 
				//添加订单状态变更日志
				OrderAdminLogBean orderLog = new OrderAdminLogBean();
				orderLog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
				orderLog.setUserId(user.getId());
				orderLog.setUsername(user.getUsername());
				orderLog.setOrderId(order.getId());
				orderLog.setOrderCode(order.getCode());
				orderLog.setCreateDatetime(DateUtil.getNow());
	    		StringBuilder logContent = new StringBuilder();
	    		logContent.append("[订单状态:");
				logContent.append(order.getStatus());
				logContent.append("->");
				logContent.append(11);
				logContent.append("]");
				orderLog.setContent(logContent.toString());
				logService.addOrderAdminStatusLog(order.getStatus(), 11, -1, -1, orderLog);
	    		if(!logService.addOrderAdminLog(orderLog)){
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新订单日志变更失败！\"}");
    	    		return;
	    		}
				if(!service.updateOrderStock("status = " + OrderStockBean.STATUS9 , " status=" + OrderStockBean.STATUS3 + " and order_code = '" + orderCode + "'")){
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新订单库存状态失败！\"}");
    	    		return;
				}
				if(!service.getDbOp().executeUpdate("update user_order set status=11, stockout=0 where code = '" + orderCode + "' ")){
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新订单状态失败！\"}");
    	    		return;
				}
				if(!service.getDbOp().executeUpdate("update user_order set balance_status=3, stockout_deal=3 where code = '" + orderCode + "' ")){
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新订单balance_status状态失败！\"}");
    	    		return;
				}

				//log记录
				StockAdminHistoryBean log2 = new StockAdminHistoryBean();
				log2.setAdminId(user.getId());
				log2.setAdminName(user.getUsername());
				log2.setLogId(rpBean.getId());
				log2.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
				log2.setOperDatetime(DateUtil.getNow());
				log2.setRemark("完成退货入库操作：" + name);
				log2.setType(StockAdminHistoryBean.CHANGE);
				if(!service.addStockAdminHistory(log2)){
    				service.getDbOp().rollbackTransaction();
    				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加StockAdminHistoryBean失败！\"}");
    	    		return;
				}
				//添加退货操作日志
				if (!packageLog.addReturnPackageLog("快速退货入库", user, order.getCode())) {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加退货日志失败！\"}");
		    		return;
				}
				//S单单独处理
				if (order.getCode().startsWith("S")) {
					AfterSaleOperationListBean asolBean = afStockService.getAfterSaleOperationList("order_id=" + order.getId());
					if (asolBean != null) {
						AfterSaleOrderBean asoBean = afterSaleService.getAfterSaleOrder("id=" + asolBean.getAfterSaleOrderId());
						if (asoBean != null) {
							if (!afterSaleService.getDbOp().executeUpdate("update after_sale_order aso, after_sale_operation_list asol set aso.status=" + AfterSaleOrderBean.STATUS_售后未妥投 + " where aso.id=asol.after_sale_order_id and asol.order_id=" + order.getId())) {
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"S单更改售后单状态失败！\"}");
								return;
							}
							int afCount = 0;
							ResultSet afRS = afterSaleService.getDbOp().executeQuery("select count(*) from after_sale_detect_product asdp,after_sale_operation_wsproduct_relational asowr, after_sale_operation_list asol where asdp.id=asowr.after_sale_wsproduct_record_id and asowr.after_sale_operation_id=asol.id and asol.order_id=" + order.getId());
							while (afRS.next()) {
								afCount = afRS.getInt(1);
							}
							afRS.close();
							
							if (afCount > 0) {
								if (!afStockService.getDbOp().executeUpdate("update after_sale_detect_product asdp,after_sale_operation_wsproduct_relational asowr, after_sale_operation_list asol set asdp.status=" + AfterSaleDetectProductBean.STATUS19 + " where asdp.id=asowr.after_sale_wsproduct_record_id and asowr.after_sale_operation_id=asol.id and asol.order_id=" + order.getId())) {
									service.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\",\"tip\":\"S单更改物流售后处理单状态失败！\"}");
									return;
								}
								
								if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_product_records aswpr,after_sale_operation_wsproduct_relational asowr, after_sale_operation_list asol set aswpr.status=8,aswpr.modify_user_id=" + user.getId() + ",aswpr.modify_user_name='" + user.getUsername() + "',aswpr.modify_datetime='" + DateUtil.getNow() + "' where aswpr.id=asowr.after_sale_wsproduct_record_id and asowr.after_sale_operation_id=asol.id and asol.order_id=" + order.getId())) {
									service.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\",\"tip\":\"S单更改销售售后处理单状态失败！\"}");
									return;
								}
							}
						}
					}
				}
    			//提交事务
    			service.getDbOp().commitTransaction();
    			CookieUtil cu = new CookieUtil(request, response);
    			cu.setCookie("RETURN_QUICK_AREA_MARK_" + user.getId(), ""+wareArea, 60*60*24*30);
    			response.getWriter().write("{\"result\":\"success\",\"tip\":\"退货成功！\"}");//传给页面，表示退货入库成功
        		return;
    		}
    	}catch (Exception e) {
        	e.printStackTrace();
        	service.getDbOp().rollbackTransaction();
        	response.getWriter().write("{\"result\":\"failure\",\"tip\":\"退货异常！\"}");
    		return;
		} finally {
    		dbOp.release();
            service.releaseAll();
            logService.releaseAll();
        }
	}
	/**
	 * 更新退货包裹列表
	 * @param rpBean
	 * @param user
	 * @param service
	 * @param statService
	 * @param order
	 * @param status
	 * @return
	 */
	private boolean updateReturnedPackage(ReturnedPackageBean rpBean, voUser user, IStockService service,
			StatService statService, voOrder order, int status) {
		String packageCode = "";
		AuditPackageBean apb = service.getAuditPackage("order_id="+order.getId());
		if(apb != null){
			packageCode = apb.getPackageCode();
		}
		String condition = "package_code='"+packageCode+"', operator_id=" + user.getId() + ", operator_name='" + user.getUsername() + "', storage_status=" + ReturnedPackageBean.NORMALENTER + ", storage_time='" + DateUtil.getNow() + "', status=" + status;
		if(!statService.updateReturnedPackage(condition, "id=" + rpBean.getId())){
			return false;
		}
		return true;
	}
	
	
	/**
     * 查询包裹列表
	 *
	 * 作者：张晔
	 * @throws SQLException 
	 * @throws ServletException 
     */
	@RequestMapping("/queryPackage")
	@ResponseBody
    public EasyuiDataGridJson queryPackage(HttpServletRequest request,
            HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws IOException, SQLException, ServletException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
//			DbOperation dbOp = new DbOperation();
//			dbOp.init("adult_slave");
		StatService service = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		ReturnedPackageService retPService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, dbOp);
		ReturnedPackageFBean formBean = new ReturnedPackageFBean();
		formBean.setReturnedPackageStatus(StringUtil.toInt(request.getParameter("returnedPackageStatus")));
		formBean.setOrderCode(StringUtil.toSql(StringUtil.convertNull(request.getParameter("orderCode"))));
		formBean.setPackageCode(StringUtil.toSql(StringUtil.convertNull(request.getParameter("packageCode"))));
		formBean.setCvStatus(StringUtil.toInt(request.getParameter("cvStatus")));
		formBean.setDeliver(StringUtil.toInt(request.getParameter("deliver")));
		formBean.setStorageTime(StringUtil.convertNull(request.getParameter("storageTime")));
		formBean.setWareArea(StringUtil.toInt(request.getParameter("wareArea")));
		try{
			String tempSql = "";
			String tempSqlCount = "";
			if( formBean.getCvStatus() == -1 && formBean.getOrderStatus() == -1 ) {
				tempSql = "select rp.* from returned_package rp where rp.id <> 0";
				tempSqlCount = "select count(rp.id) from returned_package rp where rp.id <> 0"; 
			}
			if( formBean.getCvStatus() != -1 ) {
				tempSql = "select rp.* from returned_package rp, claims_verification cv where rp.claims_verification_id = cv.id";
				tempSqlCount = "select count(rp.id) from returned_package rp, claims_verification cv where rp.claims_verification_id = cv.id";
			} 
			/*if( formBean.getOrderStatus() != -1 ) {
				tempSql = "select rp.* from returned_package rp, user_order uo where rp.order_id = uo.id";
				tempSqlCount = "select count(rp.id) from returned_package rp, user_order uo where rp.order_id = uo.id";
			}
			if( formBean.getCvStatus() != -1 && formBean.getOrderStatus() != -1 ) {
				tempSql = "select rp.* from returned_package rp, user_order uo, claims_verification cv where rp.order_id = uo.id and rp.claims_verification_id = cv.id";
				tempSqlCount = "select count(rp.id) from returned_package rp, user_order uo, claims_verification cv where rp.order_id = uo.id and rp.claims_verification_id = cv.id";
			}*/
			sql.append(tempSql);
			sqlCount.append(tempSqlCount);
			Iterator itr = ProductStockBean.areaMap.keySet().iterator();
			String availAreaIds = "100";
			for(; itr.hasNext(); ) {
				availAreaIds += "," + itr.next();
			}
			if(formBean.getOrderCode()!=null && !formBean.getOrderCode().equals("")){
				sql.append(" and rp.order_code='");
				sql.append(formBean.getOrderCode());
				sql.append("'");
				sqlCount.append(" and rp.order_code='");
				sqlCount.append(formBean.getOrderCode());
				sqlCount.append("'");
				
			}
			if(formBean.getPackageCode() != null && !formBean.getPackageCode().equals("")){
				sql.append(" and rp.package_code='");
				sql.append(formBean.getPackageCode());
				sql.append("'");
				sqlCount.append(" and rp.package_code='");
				sqlCount.append(formBean.getPackageCode());
				sqlCount.append("'");
			}
			if(formBean.getStorageTime() != null && !formBean.getStorageTime().equals("")){
				String beginTime = formBean.getStorageTime() + " 00:00:00";
				String endTime = formBean.getStorageTime() + " 23:59:59";
				sql.append(" and rp.storage_time between '");
				sql.append(beginTime);
				sql.append("' and '");
				sql.append(endTime);
				sql.append("'");
				sqlCount.append(" and rp.storage_time between '");
				sqlCount.append(beginTime);
				sqlCount.append("' and '");
				sqlCount.append(endTime);
				sqlCount.append("'");
			}
			if(formBean.getDeliver() != -1){
				sql.append(" and rp.deliver=");
				sql.append(formBean.getDeliver());
				sqlCount.append(" and rp.deliver=");
				sqlCount.append(formBean.getDeliver());
			}
			if( formBean.getWareArea() == -1 ) {
				sql.append(" and rp.area in (" + availAreaIds + ")");
				sqlCount.append(" and rp.area in (" + availAreaIds + ")");
			} else {
				sql.append(" and rp.area = " + formBean.getWareArea());
				sqlCount.append(" and rp.area = " + formBean.getWareArea());
			}
			
			if( formBean.getCvStatus() != -1 ) {
				sql.append(" and cv.status=" + formBean.getCvStatus());
				sqlCount.append(" and cv.status=" + formBean.getCvStatus());
			}
			
			/*if( formBean.getOrderStatus() != -1 ) {
				sql.append(" and uo.status=" + formBean.getOrderStatus());
				sqlCount.append(" and uo.status=" + formBean.getOrderStatus());
				params.append("&orderStatus=" + formBean.getOrderStatus());
			}*/
			if( formBean.getReturnedPackageStatus() != -1 ) {
				sql.append(" and rp.status=" + formBean.getReturnedPackageStatus() );
				sqlCount.append(" and rp.status=" + formBean.getReturnedPackageStatus());
			}
			
			
			int totalCount = retPService.getReturnedPackageCountDirectly(sqlCount.toString());
			
			List<ReturnedPackageBean> packageList = retPService.getReturnedPackageListDirectly(sql.toString(), (easyuiDataGrid.getPage()-1)*easyuiDataGrid.getRows(), easyuiDataGrid.getRows(), "storage_time desc");
			int x = packageList.size();
			for( int i = 0 ; i < x; i ++ ) {
				ReturnedPackageBean rpBean = packageList.get(i);
				if( rpBean.getClaimsVerificationId() != 0 ) {
					ClaimsVerificationBean claimsVerificationBean = claimsVerificationService.getClaimsVerification("id=" + rpBean.getClaimsVerificationId());
					rpBean.setClaimsVerificationBean(claimsVerificationBean);
				}
				if( rpBean.getReasonId() != 0 ) {
					String sql2 = "select id,reason from returns_reason where id = " + rpBean.getReasonId();
					ResultSet rs = service.getDbOp().executeQuery(sql2);
					if(rs.next()) {
						ReturnsReasonBean bean = new ReturnsReasonBean();
						bean.setId(rs.getInt(1));
						bean.setReason(rs.getString(2));
						rpBean.setReturnsReasonBean(bean);
					}
				}
				voOrder vorder = wareService.getOrder(rpBean.getOrderId());
				if( vorder == null ) {
					rpBean.setOrderStatusName("");
				} else {
					rpBean.setOrderStatusName(vorder.getStatusName());
				}
				rpBean.setDeliverName(StringUtil.convertNull((String)voOrder.deliverMapAll.get(String.valueOf(rpBean.getDeliver()))));
				rpBean.setAreaName(StringUtil.convertNull((String)ProductStockBean.areaMap.get(rpBean.getArea())));
				rpBean.setImportTime(StringUtil.convertNull(rpBean.getImportTime()).equals("") ? "" : StringUtil.convertNull(rpBean.getImportTime()).substring(0,19));
				rpBean.setStorageTime(StringUtil.convertNull(rpBean.getStorageTime()).equals("") ? "" : StringUtil.convertNull(rpBean.getStorageTime()).substring(0,19));
				rpBean.setClaimsVerificationCode(rpBean.getClaimsVerificationBean() == null ? "" : rpBean.getClaimsVerificationBean().getCode());
				rpBean.setClaimsVerificationStatusName(rpBean.getClaimsVerificationBean() == null ? "" : rpBean.getClaimsVerificationBean().getStatusName());
				rpBean.setReturnedReason(rpBean.getReturnsReasonBean() == null ? "" : rpBean.getReturnsReasonBean().getReason());
			}
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long)totalCount);
			easyuiDataGridJson.setRows(packageList);
			return easyuiDataGridJson;
		}finally{
			service.releaseAll();
			wareService.releaseAll();
		}
	}
	
	/**
	 * 退货包裹列表的“退货包裹状态”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getReturnedPackageStatusJSON")
	@ResponseBody
	public String getReturnedPackageStatusJSON() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("{\"id\":\"-1\",\"name\":\"请选择\",\"selected\":true},");
		sb.append("{\"id\":\"0\",\"name\":\"待退回\"},");
		sb.append("{\"id\":\"1\",\"name\":\"已退回\"}");
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 退货包裹列表的“理赔单状态”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getCvStatusJSON")
	@ResponseBody
	public String getCvStatusJSON() {
		StringBuffer sb = new StringBuffer();
		Map map = ClaimsVerificationBean.getAllStatusName();
		sb.append("[");
		sb.append("{\"id\":\"-1\",\"name\":\"请选择\",\"selected\":true},");
		for (int i = 0 ; i < map.size(); i++) {
			sb.append("{\"id\":\"").append(i).append("\",\"name\":\"").append(map.get(i)).append("\"}");
			if (i == map.size()-1) {
			} else {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	
	/**
	 * 退货包裹列表的“库地区”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getWareAreaAllJSON")
	@ResponseBody
	public String getWareAreaAllJSON() {
		StringBuffer sb = new StringBuffer();
		HashMap map = ProductStockBean.areaMap;
		sb.append("[");
		sb.append("{\"id\":\"-1\",\"name\":\"请选择地区\",\"selected\":true},");
		for (int i = 0 ; i < map.size(); i++) {
			sb.append("{\"id\":\"").append(i).append("\",\"name\":\"").append(map.get(i)).append("\"}");
			if (i == map.size()-1) {
			} else {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 退货包裹列表的“快递公司”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getDeliverJSON")
	@ResponseBody
	public String getDeliverJSON(HttpServletRequest request) {
		int deliverId = StringUtil.toInt(request.getParameter("deliverId"));
		int popId = StringUtil.toInt(request.getParameter("popId"));
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if(popId == PopBussiness.POP_JD){
			sb.append("{\"id\":\"").append(1).append("\",\"name\":\"").append("京东快递").append("\"}");
			sb.append("]");
			return sb.toString();
		}
		
		Map map = voOrder.deliverMapAll;
		
		Iterator itr = map.keySet().iterator();
		for (int i = 0 ; i < map.size(); i++) {
			String key = (String)itr.next();
			if (key.equals(deliverId + "")) {
				sb.append("{\"id\":\"").append(key).append("\",\"name\":\"").append((String)map.get(key)).append("\",\"selected\":true}");
			} else {
				sb.append("{\"id\":\"").append(key).append("\",\"name\":\"").append((String)map.get(key)).append("\"}");
			}
			if (i == map.size()-1) {
			} else {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 张晔
	 * 
	 * 导出包裹列表
	 */
	@RequestMapping("/exportPackage")
	public void exportPackage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
		StatService service = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnedPackageService retPService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String tempSql = "";
			ReturnedPackageFBean formBean = new ReturnedPackageFBean();
			formBean.setReturnedPackageStatus(StringUtil.toInt(request.getParameter("returnedPackageStatus")));
			formBean.setOrderCode(StringUtil.toSql(StringUtil.convertNull(request.getParameter("orderCode"))));
			formBean.setPackageCode(StringUtil.toSql(StringUtil.convertNull(request.getParameter("packageCode"))));
			formBean.setCvStatus(StringUtil.toInt(request.getParameter("cvStatus")));
			formBean.setDeliver(StringUtil.toInt(request.getParameter("deliver")));
			formBean.setStorageTime(StringUtil.convertNull(request.getParameter("storageTime")));
			formBean.setWareArea(StringUtil.toInt(request.getParameter("wareArea")));
			if( formBean.getCvStatus() == -1 && formBean.getOrderStatus() == -1 ) {
				tempSql = "select rp.* from returned_package rp where rp.id <> 0";
			}
			if( formBean.getCvStatus() != -1 ) {
				tempSql = "select rp.* from returned_package rp, claims_verification cv where rp.claims_verification_id = cv.id";
			} 
			if( formBean.getOrderStatus() != -1 ) {
				tempSql = "select rp.* from returned_package rp, user_order uo where rp.order_id = uo.id";
			}
			if( formBean.getCvStatus() != -1 && formBean.getOrderStatus() != -1 ) {
				tempSql = "select rp.* from returned_package rp, user_order uo, claims_verification cv where rp.order_id = uo.id and rp.claims_verification_id = cv.id";
			}
			sql.append(tempSql);
			Iterator itr = ProductStockBean.areaMap.keySet().iterator();
			String availAreaIds = "100";
			for(; itr.hasNext(); ) {
				availAreaIds += "," + itr.next();
			}
			if(formBean.getOrderCode()!=null && !formBean.getOrderCode().equals("")){
				sql.append(" and rp.order_code='");
				sql.append(formBean.getOrderCode());
				sql.append("'");
				
			}
			if(formBean.getPackageCode() != null && !formBean.getPackageCode().equals("")){
				sql.append(" and rp.package_code='");
				sql.append(formBean.getPackageCode());
				sql.append("'");
			}
			if(formBean.getStorageTime() != null && !formBean.getStorageTime().equals("")){
				String beginTime = formBean.getStorageTime() + " 00:00:00";
				String endTime = formBean.getStorageTime() + " 23:59:59";
				sql.append(" and rp.storage_time between '");
				sql.append(beginTime);
				sql.append("' and '");
				sql.append(endTime);
				sql.append("'");
			}else{
				String beginTime = DateUtil.getNowDateStr() + " 00:00:00";
				String endTime = DateUtil.getNowDateStr() + " 23:59:59";
				sql.append(" and rp.storage_time between '");
				sql.append(beginTime);
				sql.append("' and '");
				sql.append(endTime);
				sql.append("'");
			}
			if(formBean.getDeliver() != -1){
				sql.append(" and rp.deliver=");
				sql.append(formBean.getDeliver());
			}
			if( formBean.getWareArea() == -1 ) {
				sql.append(" and rp.area in (" + availAreaIds + ")");
			} else {
				sql.append(" and rp.area = " + formBean.getWareArea());
			}
			
			if( formBean.getCvStatus() != -1 ) {
				sql.append(" and cv.status=" + formBean.getCvStatus());
			}
			
			if( formBean.getOrderStatus() != -1 ) {
				sql.append(" and uo.status=" + formBean.getOrderStatus());
			}
			
			
			List<ReturnedPackageBean> packageList = retPService.getReturnedPackageListDirectly(sql.toString(), -1, -1, "storage_time desc");
			int x = packageList.size();
			for( int i = 0 ; i < x; i ++ ) {
				ReturnedPackageBean rpBean = packageList.get(i);
				if( rpBean.getClaimsVerificationId() != 0 ) {
					ClaimsVerificationBean claimsVerificationBean = claimsVerificationService.getClaimsVerification("id=" + rpBean.getClaimsVerificationId());
					rpBean.setClaimsVerificationBean(claimsVerificationBean);
				}
				if( rpBean.getReasonId() != 0 ) {
					String sql2 = "select id,reason from returns_reason where id = " + rpBean.getReasonId();
					ResultSet rs = service.getDbOp().executeQuery(sql2);
					if(rs.next()) {
						ReturnsReasonBean bean = new ReturnsReasonBean();
						bean.setId(rs.getInt(1));
						bean.setReason(rs.getString(2));
						rpBean.setReturnsReasonBean(bean);
					}
				}
				
				voOrder vorder = wareService.getOrder(rpBean.getOrderId());
				if( vorder == null ) {
					rpBean.setOrderStatusName("");
				} else {
					rpBean.setOrderStatusName(vorder.getStatusName());
				}
			}
			XSSFWorkbook hwb = retPService.exportPackage(packageList);
		response.reset();
		response.addHeader("Content-Disposition", "attachment;filename="
				+ DateUtil.getNowDateStr() + StringUtil.toUtf8String("退货包裹列表") + ".xls");
		response.setContentType("application/msxls");
		hwb.write(response.getOutputStream());
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	
	
	/**
     * 录入退货原因——预查退货包裹 是否已经有原因了
	 *
	 * 作者：张晔
     */
	@RequestMapping("/getDoReturnedPackageHasReason")
	@ResponseBody
	public void getDoReturnedPackageHasReason(HttpServletRequest request, HttpServletResponse response) {
		
		String code = StringUtil.convertNull(request.getParameter("code"));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String json = "{ ";
		try {
			synchronized(Constants.LOCK){
				voOrder order = null;
				if( code.equals("") ) {
					json += "\"status\":\"failure\", \"tip\":\"请填写订单编号或包裹单号!\"";
				} else {
					Object result = claimsVerificationService.getOrderByPcodeOrOcode(code, wareService, service);
					if( result instanceof String ) {
						json += "\"status\":\"failure\", \"tip\":\"" + result + "\"";
					} else if ( result instanceof voOrder ) {
						order = (voOrder)result;
						ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + order.getCode() + "'");
						if( rpBean == null ) {
							json += "\"status\":\"failure\", \"tip\":\"" + "该订单不在三方物流交接单中！" + "\"";
						} else {
							if( rpBean.getReasonId() != 0 ) {
								json += "\"status\":\"success\", \"hasReason\":\"1\"";
							} else {
								json += "\"status\":\"success\", \"hasReason\":\"0\"";
							}
						}
					}
				}
				json += " }";
				response.setContentType("text/html; charset=utf-8");
				response.getWriter().write(json);
				return;
			}
		} catch(Exception e) {
			if(stockLog.isErrorEnabled()){
				stockLog.error("method:ReturnStorageAction.returnsReasonInput exception", e);
			}
		}finally {
			statService.releaseAll();
		}
	}
	
	/**
     * 修改退货包裹原因
	 *
	 * 作者：张晔
	 * @throws IOException 
	 * @throws ServletException 
     */
	@RequestMapping("/returnsReasonInput")
	@ResponseBody
	public void returnsReasonInput(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		response.setContentType("text/html; charset=utf-8");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(758)){
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有退货原因录入权限！\"}");
			return;
		}
		int wareArea = StringUtil.toInt(request.getParameter("wareArea1"));
		String returnsReasonCode = StringUtil.convertNull(request.getParameter("returnsReasonCode1"));
		String code = StringUtil.convertNull(request.getParameter("code1"));
		WareService wareService = new WareService();
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(Constants.LOCK){
				service.getDbOp().startTransaction();
				ReturnsReasonBean rrBean  = claimsVerificationService.getReturnsReasonByCode(StringUtil.toSql(returnsReasonCode));
				if( rrBean == null ) {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"退货原因条码有误！\"}");
					return;
				}
				voOrder order = null;
				if( code.equals("") ) {
				} else {
					Object result = claimsVerificationService.getOrderByPcodeOrOcode(code, wareService, service);
					if( result instanceof String ) {
						service.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\""+result+"！\"}");
						return;
					} else if ( result instanceof voOrder ) {
						order = (voOrder)result;
						ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + order.getCode() + "'");
						if( rpBean == null ) {
							service.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"该订单不在三方物流交接单中！\"}");
							return;
						} else {
							if( rpBean.getArea() != wareArea ) {
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"所选地区不是退货包裹的入库地区！\"}");
								return;
							} 
							if( !statService.updateReturnedPackage("reason_id = " + rrBean.getId(), "id=" + rpBean.getId())) {
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新数据库时操作失败！\"}");
								return;
							}
							if (!packageLog.addReturnPackageLog(rpBean.getReasonId() == 0 ? "添加退货原因" : "修改退货原因", user, order.getCode())) {
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加退货日志失败！\"}");
								return;
							}
						}
					}
				}
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
			}
		} catch(Exception e) {
			if(stockLog.isErrorEnabled()){
				stockLog.error("method:ReturnStorageAction.returnsReasonInput exception", e);
			}
			statService.getDbOp().rollbackTransaction();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\""+ e.getMessage()+"\"}");
			return;
		}finally {
			statService.releaseAll();
		}
		response.getWriter().write("{\"result\":\"success\",\"tip\":\"操作成功！\"}");
		return;
	}
	
	/**
     * 退货日志列表
	 *
	 * 作者：张晔
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws SQLException 
     */
	@RequestMapping("/returnPackageLog")
	@ResponseBody
    public EasyuiDataGridJson returnPackageLog(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, SQLException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		StatService service = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, db);
		ReturnPackageLogService packLogService = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String condition = "";
		EasyuiDataGridJson easyuiJson = new EasyuiDataGridJson();
		try {
			List l1 = new ArrayList();
			Map map = new HashMap();
			ResultSet rs = service.getDbOp().executeQuery("select order_code,package_code from returned_package where order_code='"+StringUtil.toSql(orderCode)+"' or package_code='"+StringUtil.toSql(orderCode)+ "'");
			if(rs.next()) {
				condition = "order_code='"+StringUtil.toSql(rs.getString(1))+"'";
				if (rs.getString(2) != null) {
					map.put("packageCode",rs.getString(2));
				}
			} else {
				condition = "1=2";
			}
			rs.close();
			l1.add(map);
			List list = packLogService.getReturnPackageLogList(condition,
					-1, -1, "oper_time asc");
			easyuiJson.setTotal((long)list.size());
			easyuiJson.setRows(list);
			easyuiJson.setFooter(l1);
			
			return easyuiJson;
		}finally{
			if(packLogService != null){
				packLogService.releaseAll();
			}
		}
	}
	
	/**
     * 导入第三方退货包裹
	 *
	 * 作者：张晔
	 * @throws IOException 
	 * @throws ServletException 
     */
	@RequestMapping("/importThirdPartyReturnedPackage")
	@ResponseBody
	public void importThirdPartyReturnedPackage(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException{
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		String info = StringUtil.dealParam(request.getParameter("importInfo"));
		String tip = "";
		voOrder vorder = null;
		int successCount = 0;
		int failCount = 0;
		int count = 0;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		response.setContentType("text/html; charset=utf-8");
		try {
			synchronized(stockLock) {
				if( wareArea == -1 ) {
					response.getWriter().write("{\"result\":\"firsttime\", \"totalCount\":\""+count+"\",\"failCount\":\""+failCount+"\",\"successCount\":\""+successCount+"\"}");
					return;
				}
				if( info == null || info.equals("")) {
					tip += "<br>" + "没有导入信息！";
					response.getWriter().write("{\"result\":\"failure\",\"errorMsg\":\""+tip+"\", \"totalCount\":\""+count+"\",\"failCount\":\""+failCount+"\",\"successCount\":\""+successCount+"\"}");
					return;
				} else {
					String line = null;
					BufferedReader br = new BufferedReader(new StringReader(info));
					while ((line = br.readLine()) != null) {
						statService.getDbOp().startTransaction();
						count ++;
						line.replace(" ", "");
						String[] cols = line.split("\t");
						if( cols != null && (cols.length == 1 || cols.length == 2)) {
							if( cols.length == 1 ) {
								// 这唯一的一列就是  订单号 或者包裹单号  只用修改对应的订单的状态
								Object temp = claimsVerificationService.getOrderByPcodeOrOcode(cols[0],wareService, istockService);
								if( temp instanceof String ) {
									tip += "<br>" + "订单号或包裹单号" + cols[0] + "不存在!";
									statService.getDbOp().rollbackTransaction();
									failCount += 1;
									continue;
								} 
								if( temp instanceof voOrder ) {
									vorder = (voOrder)temp;
									boolean isPCode = false;
									if( !cols[0].equals(vorder.getCode()) ) {
										isPCode = true;
									}
									OrderStockBean osBean = istockService.getOrderStock("order_code='"+ vorder.getCode()+ "' and status != "+OrderStockBean.STATUS4);
									if (osBean != null) {
										if (osBean.getStockArea() != wareArea) {
											if( isPCode ) {
												tip += "<br>" + "包裹单"+ cols[0] + "出库地区与所选地区不一致！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											} else {
												tip += "<br>" + "订单"+ vorder.getCode() + "出库地区与所选地区不一致！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}
										}
									} else {
										if( isPCode ) {
											tip += "<br>" + "包裹单"+ cols[0] + "的出库单不存在！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "<br>" + "订单"+ vorder.getCode() + "的出库单不存在！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									}
									ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
									if(vorder.getStatus() == 11 ) {
										//11 是 已退回
										if( isPCode ) {
											tip += "<br>" + "包裹单"+ cols[0] + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "<br>" + "订单"+ vorder.getCode() + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									} else if ( rpBean != null ) {
										if( isPCode ) {
											tip += "<br>" + "包裹单" + cols[0] + "已导入，请勿重复导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "<br>" + "订单" + vorder.getCode() + "已导入，请勿重复导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									} else {
										String addRet = claimsVerificationService.addReturnedPackageInfoBref(user,vorder, -1, wareArea, wareService);
										if( !addRet.equals("SUCCESS") ) {
											if( isPCode ) {
												tip += "<br>" + "包裹单" + cols[0] + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											} else {
												tip += "<br>" + "订单" + vorder.getCode() + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}
										} else {
											if (!packageLog.addReturnPackageLog("导入第三方物流交接单", user, vorder.getCode())) {
												if( isPCode ) {
													tip += "<br>" + "包裹单" + cols[0] + "添加退货日志失败！";
													statService.getDbOp().rollbackTransaction();
													failCount += 1;
													continue;
												} else {
													tip += "<br>" + "订单" + vorder.getCode() + "添加退货日志失败！";
													statService.getDbOp().rollbackTransaction();
													failCount += 1;
													continue;
												}
											}
											successCount += 1;
										}
									}
								}
							} else {
								// 如果是两列   添加可能的退货原因
								Object temp = claimsVerificationService.getOrderByPcodeOrOcode(cols[0],wareService, istockService);
								if( temp instanceof String ) {
									tip += "<br>" + "订单号或包裹单号" + cols[0] + "不存在!";
									statService.getDbOp().rollbackTransaction();
									failCount += 1;
									continue;
								} 
								if( temp instanceof voOrder ) {
									vorder = (voOrder)temp;
									boolean isPCode = false;
									if( !cols[0].equals(vorder.getCode()) ) {
										isPCode = true;
									}
									OrderStockBean osBean = istockService.getOrderStock("order_code='"+ vorder.getCode()+ "' and status != "+OrderStockBean.STATUS4);
									if (osBean != null) {
										if (osBean.getStockArea() != wareArea) {
											if( isPCode ) {
												tip += "<br>" + "包裹单"+ cols[0] + "出库地区与所选地区不一致！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											} else {
												tip += "<br>" + "订单"+ vorder.getCode() + "出库地区与所选地区不一致！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}
										}
									} else {
										if( isPCode ) {
											tip += "<br>" + "包裹单"+ cols[0] + "的出库单不存在！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "<br>" + "订单"+ vorder.getCode() + "的出库单不存在！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									}
									ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
									if(vorder.getStatus() == 11 ) {
										//11 是 已退回
										if( isPCode ) {
											tip += "<br>" + "包裹单"+ cols[0] + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "<br>" + "订单"+ vorder.getCode() + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									} else if ( rpBean != null ) {
										if( isPCode ) {
											tip += "<br>" + "包裹单" + cols[0] + "已导入，请勿重复导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "<br>" + "订单" + vorder.getCode() + "已导入，请勿重复导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									} else {
										int rId = -1;
										String reasonCode = cols[1];
										ReturnsReasonBean rrBean = claimsVerificationService.getReturnsReasonByCode(StringUtil.toSql(reasonCode));
										if( rrBean == null ) {
											if( isPCode ) {
												tip +="<br>" +  "包裹单" + cols[0] + "的导入退货原因条码有误！";
											} else {
												tip +="<br>" +  "订单" + vorder.getCode() + "的导入退货原因条码有误！";
											}
											rId = -1;
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											rId = rrBean.getId();
										}
										String addRet = claimsVerificationService.addReturnedPackageInfoBref(user,vorder, rId, wareArea, wareService);
										if( !addRet.equals("SUCCESS") ) {
											if( isPCode ) {
												tip += "<br>" + "包裹单" + cols[0] + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											} else {
												tip += "<br>" + "订单" + vorder.getCode() + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}
										} else {
											if (!packageLog.addReturnPackageLog("导入第三方物流交接单", user, vorder.getCode())) {
												if( isPCode ) {
													tip += "<br>" + "包裹单" + cols[0] + "添加退货日志失败！";
													statService.getDbOp().rollbackTransaction();
													failCount += 1;
													continue;
												} else {
													tip += "<br>" + "订单" + vorder.getCode() + "添加退货日志失败！";
													statService.getDbOp().rollbackTransaction();
													failCount += 1;
													continue;
												}
											}
											successCount += 1;
										}
										
									}
								}
							}
						} else {
							tip += "<br>" + "第" + count + "导入格式不正确！";
							statService.getDbOp().rollbackTransaction();
							failCount += 1;
							continue;
						}
						statService.getDbOp().commitTransaction();
						statService.getDbOp().getConn().setAutoCommit(true);
					}
					if( tip.length() == 0 ) {
						response.getWriter().write("{\"result\":\"success\",\"msg\":\"提交修改已全部操作成功！\", \"totalCount\":\""+count+"\",\"failCount\":\""+failCount+"\",\"successCount\":\""+successCount+"\"}");
						return;
					} else {
						response.getWriter().write("{\"result\":\"failure\",\"errorMsg\":\""+tip+"\", \"totalCount\":\""+count+"\",\"failCount\":\""+failCount+"\",\"successCount\":\""+successCount+"\"}");
						return;
					}
				}
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		response.getWriter().write("{\"result\":\"failure\",\"errorMsg\":\""+tip+"\", \"totalCount\":\""+count+"\",\"failCount\":\""+failCount+"\",\"successCount\":\""+successCount+"\"}");
		return;
	}
	
	/**
     * 入库作业统计和包裹核查统计，以type做区分，type为returnpackage时是入库作业统计，type为checkpackage时是包裹核查统计
	 *
	 * 作者：张晔
     */
	@RequestMapping("/getCountResult")
	@ResponseBody
	public void getCountResult(HttpServletRequest request, HttpServletResponse response)throws Exception{
		voUser user = (voUser) request.getSession().getAttribute(
	            IConstants.USER_VIEW_KEY);
	    if (user == null) {
	    	request.setAttribute("msg", "当前没有登录，添加失败！");
	    	request.getRequestDispatcher(noSession).forward(request, response);
	        return;
	    }
		String queryTime = StringUtil.convertNull(request.getParameter("startTime"));
		String operationId = StringUtil.convertNull(request.getParameter("operationid"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String areaId = StringUtil.convertNull(request.getParameter("wareArea"));
		String toPage = StringUtil.convertNull(request.getParameter("type"));
		ReturnPackageCountService rpcs = new ReturnPackageCountService();
		String result = "";
		int packageCount = 0;
		int productCount = 0;
		int skuCount = 0;
		StringBuffer returnStr = new StringBuffer();
		response.setContentType("text/html; charset=utf-8");
		try{
			if("returnpackage".equals(toPage))
				result = rpcs.getCountResult(StringUtil.toSql(queryTime), StringUtil.toSql(operationId), StringUtil.toSql(areaId), StringUtil.toSql(productCode));
			else if ("checkpackage".equals(toPage)) {
				result = rpcs.getCountResult(StringUtil.toSql(queryTime), StringUtil.toSql(operationId), StringUtil.toSql(areaId));
			} else {
				result = "";
			}
			if(result != null && !"".equals(result)){
				packageCount = Integer.valueOf(result.split(",")[0]);
				productCount = Integer.valueOf(result.split(",")[1]);
				skuCount = Integer.valueOf(result.split(",")[2]);
				returnStr.append("{\"result\":\"success\",\"tip\":\"");
				returnStr.append(queryTime).append("共入库").append(packageCount).append("个包裹，商品总计：").append(skuCount).append("个sku,").append(productCount).append("件商品");
				returnStr.append("\"}");
			} else {
				returnStr.append("{\"result\":\"failure\",\"tip\":\"统计出错！\"}");
			}
			response.getWriter().write(returnStr.toString());
			return;
		}finally{
			rpcs.releaseAll();
		}
	}
	
	/**
	 * 入库作业统计&包裹核查统计页面的“库地区”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getReturnPackageWareAreaJSON")
	@ResponseBody
	public String getReturnPackageWareAreaJSON(HttpServletRequest request) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		StringBuffer sb = new StringBuffer();
		if( cdaList.size() == 0 ) { 
			return "{\"areaId\":\"-1\",\"areaName\":\"无地区权限\",\"selected\":true}";
  		} else { 
  			sb.append("[");
  			sb.append("{\"areaId\":\"").append("").append("\",\"areaName\":\"请选择\",\"selected\":true},");
  			for ( int i = 0; i < cdaList.size(); i ++ ) {
  				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
  				sb.append("{\"areaId\":\"").append(areaId).append("\",\"areaName\":\"").append(ProductStockBean.areaMap.get(areaId)).append("\"}");
  				if (i == cdaList.size()-1) {
  					sb.append("]");
  				} else {
  					sb.append(",");
  				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 退货上架单统计页面的“产品线”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getProductLine")
	@ResponseBody
	public String getProductLine(HttpServletRequest request, HttpServletResponse response)throws Exception{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		ResultSet rs = null;
		try {
			//构建查询语句
	        String query = 	"select id,name from product_line";
	        rs = dbOp.executeQuery(query);
	        List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
	        while(rs.next()){
	        	EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
	        	bean.setId(rs.getInt(1) + "");
	        	bean.setText(rs.getString(2));
	        	list.add(bean);
			}
	        return JSONArray.fromObject(list).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return null;
	}
	
	/**
	 * 退货上架单统计页面的“地区”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getLimitArea")
	@ResponseBody
	public String getLimitArea(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<String, String> areaMap = ProductWarePropertyService.getWeraAreaMap(request);
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		for(Map.Entry<String, String> entry : areaMap.entrySet()){
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId(entry.getKey());
			bean.setText(entry.getValue());
			list.add(bean);
		}
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 * 退货上架单统计页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/returnProductUpStatistics")
	@ResponseBody
	public void returnProductUpStatistics(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，添加失败！");
	    	request.getRequestDispatcher(noSession).forward(request, response);
	        return;
		}
		response.setContentType("text/html; charset=utf-8");
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		String time = StringUtil.convertNull(request.getParameter("time")).trim();
		int productLine = StringUtil.toInt(request.getParameter("productLine"));
		String userId = StringUtil.convertNull(request.getParameter("userId")).trim();
		String productCode = StringUtil.convertNull(request.getParameter("productCode")).trim();
 		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		CargoServiceImpl cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuilder condition = new StringBuilder();
			if(time == null || "".equals(time)){
				time = DateUtil.getNowDateStr();
			}
			condition.append("left(co.create_datetime,10)='" + StringUtil.toSql(time) + "' and coc.type=0");
			if(areaId != -1){
				condition.append(" and co.stock_in_area=" + areaId);
			}
			if(productLine != -1){
				condition.append(" and plc.product_line_id=" + productLine);
			}
			if(!"".equals(userId)){
				if(userId.matches("[0-9]*")){
					CargoStaffBean staff = cargoService.getCargoStaff("code='" + userId + "'");
					if(staff == null){
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"物流员工编号不正确！\"}");
						return;
					}
					condition.append(" and co.create_user_id=" + staff.getUserId());
				}else{
					condition.append(" and co.create_user_name='" + userId + "'");
				}
			}
			if(!"".equals(productCode) && !"-1".equals(productCode)){
				condition.append(" and p.code='" + StringUtil.toSql(productCode) + "'");
			}
			//构建查询语句
	        String query = 	"select count(distinct co.id),count(distinct coc.product_id),sum(coc.stock_count) c from cargo_operation co " +
	        				"join cargo_operation_cargo coc on co.id = coc.oper_id join product p on p.id=coc.product_id " +
	        				"join product_line_catalog plc on plc.catalog_id=p.parent_id1 or p.parent_id2=plc.catalog_id " +
	        				"or p.parent_id3=plc.catalog_id";
	        query += " where " + condition;
	        ResultSet rs = null;
	        rs = dbOp.executeQuery(query);
	        StringBuffer result = new StringBuffer();
	        if(rs.next()){
	        	result.append("{\"result\":\"success\",\"tip\":\"");
	        	result.append(time).append("共生成").append(rs.getInt(1)).append("张退货上架单，商品总计：").append(rs.getInt(2)).append("个sku,").append(rs.getInt(3)).append("个商品");
			}else{
				result.append("{\"result\":\"failure\",\"tip\":\"");
				result.append(time).append("查询结果异常");
			}
	        if(rs != null){
	        	rs.close();
	        }
			result.append("\"}");
			response.getWriter().write(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"异常\"}");
		}finally{
			dbOp.release();
		}
		return;
	}
	
	
	/**
	 * 退货上架汇总单统计页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/returnProductUpShelfStatistics")
	@ResponseBody
	public void returnProductUpShelfStatistics(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，添加失败！");
	    	request.getRequestDispatcher(noSession).forward(request, response);
	        return;
		}
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		String time = StringUtil.convertNull(request.getParameter("time"));
		int productLine = StringUtil.toInt(request.getParameter("productLine"));
		String userName = StringUtil.convertNull(request.getParameter("userId"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		try {
			StringBuilder condition = new StringBuilder();
			if(time == null || "".equals(time)){
				time = DateUtil.getNowDateStr();
			}
			condition.append("left(rus.create_datetime,10)='" + StringUtil.toSql(time) + "'  and coc.type=0");
			if(areaId != -1){
				condition.append(" and co.stock_in_area=" + areaId);
			}
			if(productLine != -1){
				condition.append(" and plc.product_line_id=" + productLine);
			}
			if(!"".equals(userName)){
				condition.append(" and rus.create_user_name='" + userName + "'");
			}
			if(!"".equals(productCode) && !"-1".equals(productCode)){
				condition.append(" and p.code='" + StringUtil.toSql(productCode) + "'");
			}
			//构建查询语句
	        String query = 	"select count(distinct rus.code),count(distinct coc.product_id),sum(coc.stock_count) c from returned_up_shelf rus " +
	        				"join cargo_operation co on rus.code=co.source " +
	        				"join cargo_operation_cargo coc on co.id = coc.oper_id join product p on p.id=coc.product_id " +
	        				"join product_line_catalog plc on plc.catalog_id=p.parent_id1 or p.parent_id2=plc.catalog_id " +
	        				"or p.parent_id3=plc.catalog_id";
	        query += " where " + condition;
	        ResultSet rs = null;
	        rs = dbOp.executeQuery(query);
	        StringBuffer result = new StringBuffer();
	        if(rs.next()){
	        	result.append("{\"result\":\"success\",\"tip\":\"");
	        	result.append(time).append("共生成").append(rs.getInt(1)).append("张退货上架汇总单，商品总计：").append(rs.getInt(2)).append("个sku,").append(rs.getInt(3)).append("个商品");
			}else{
				result.append("{\"result\":\"failure\",\"tip\":\"");
				result.append(time).append("查询结果异常");
			}
	        if(rs != null){
	        	rs.close();
	        }
			result.append("\"}");
			response.getWriter().write(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"异常\"}");
		}finally{
			dbOp.release();
		}
		return;
	}
	
	/**
     * 退货原因列表
	 *
	 * 作者：张晔
     */
	@RequestMapping("/returnsReasonList")
	@ResponseBody
    public EasyuiDataGridJson returnsReasonList(HttpServletRequest request,
            HttpServletResponse response) throws IOException, SQLException, ServletException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		try {
			//此处不清楚flag=ajax时是干什么的，不进行处理
			if("ajax".equals(flag)){
				String sql = "select id,reason from returns_reason";
				List list = new ArrayList();
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					ReturnsReasonBean bean = new ReturnsReasonBean();
					bean.setId(rs.getInt(1));
					bean.setReason(rs.getString(2));
					list.add(bean);
				}
				easyuiDataGridJson.setTotal((long)list.size());
				easyuiDataGridJson.setRows(list);
				return easyuiDataGridJson;
			}else{
				//判断是否有编辑、删除权限
				String isAdd = "";
				String isDel = "";
				UserGroupBean group = user.getGroup();
				if (group.isFlag(673)) {
					isAdd = "true";
				} else {
					isAdd = "false";
				}
				if (group.isFlag(674)) {
					isDel = "true";
				} else {
					isDel = "false";
				}
				String sql = "select id,code,reason from returns_reason";
				List list = new ArrayList();
				Map map = new HashMap();
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					map = new HashMap();
					map.put("id", rs.getInt(1) + "");
					map.put("code", rs.getString(2));
					map.put("reason", rs.getString(3));
					map.put("isAdd", isAdd);
					map.put("isDel", isDel);
					list.add(map);
				}
				easyuiDataGridJson.setTotal((long)list.size());
				easyuiDataGridJson.setRows(list);
				return easyuiDataGridJson;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
	
	/**
	 * 退货原因添加
	 */
	@RequestMapping("/returnsReasonAdd")
	@ResponseBody
	public void returnsReasonAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
	    	request.getRequestDispatcher(noSession).forward(request, response);
	        return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(673) ) {
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有销售退货原因添加权限！\"}");
	        return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String reason = StringUtil.convertNull(request.getParameter("reason")).trim();
		try {
			if(reason.length()>20){
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"退货原因不能超过10个汉字！\"}");
		        return;
			}
			String sql = "select id,code,reason from returns_reason order by code DESC";
			List list = new ArrayList();
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				ReturnsReasonBean bean = new ReturnsReasonBean();
				bean.setId(rs.getInt(1));
				bean.setCode(rs.getString(2));
				bean.setReason(rs.getString(3));
				list.add(bean);
			}
			String newCode = "";
			if(list.size()>0){
				String code = ((ReturnsReasonBean)list.get(0)).getCode();
				if(code != null && !"".equals(code)){
					int count = Integer.parseInt(code.substring(2,5));
					count++;
					if(count <10){
						newCode = "TH00" + count;
					}else if(count<100){
						newCode = "TH0" + count;
					}else if(count < 1000){
						newCode = "TH" + count;
					}else{
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"条码不能再长了！\"}");
				        return;
					}
					for(int i=0;i<list.size();i++){
						ReturnsReasonBean bean =(ReturnsReasonBean)list.get(i);
						if(reason.equals(bean.getReason())){
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"退货原因不能重复！\"}");
					        return;
						}
					}
				}else{
					newCode = "TH001";
				}
			}
			sql = "insert into returns_reason (code,reason) values('" + newCode + "','"+ StringUtil.toSql(reason) +"')";
			if(service.getDbOp().executeUpdate(sql)){
				response.getWriter().write("{\"result\":\"success\",\"tip\":\"添加成功！\"}");
		        return;
			}else{
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加失败！\"}");
		        return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"异常！\"}");
		}finally{
			service.releaseAll();
		}
		return;
	}
	
	/**
	 * 退货原因修改
	 */
	@RequestMapping("/returnsReasonEdit")
	@ResponseBody
	public void returnsReasonEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
	    	request.getRequestDispatcher(noSession).forward(request, response);
	        return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(673) ) {
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有销售退货原因编辑权限！\"}");
	        return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String id = StringUtil.convertNull(request.getParameter("id"));
		String code = StringUtil.convertNull(request.getParameter("code")).trim();
		String reason = StringUtil.convertNull(request.getParameter("reason")).trim();
		try {
			if(!"".equals(reason) && !"".equals(id) && !"".equals(code)){
				if(code.length()>5){
					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"条码不能超过5位！\"}");
			        return;
				}
				if(reason.length()>20){
					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"退货原因不能超过10个汉字！\"}");
			        return;
				}
				if(code.matches("[TH]{2}[0-9]{3}|[th]{2}[0-9]{3}")==false){
					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"原因条码格式不正确！\"}");
			        return;
				}
				String sql = "select id,code,reason from returns_reason";
				List list = new ArrayList();
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					ReturnsReasonBean bean = new ReturnsReasonBean();
					bean.setId(rs.getInt(1));
					bean.setCode(rs.getString(2));
					bean.setReason(rs.getString(3));
					list.add(bean);
				}
				for(int i=0;i<list.size();i++){
					ReturnsReasonBean bean =(ReturnsReasonBean)list.get(i);
					if(Integer.parseInt(id) != bean.getId()){
						if(reason.equals(bean.getReason())){
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"原因名不能重复！\"}");
					        return;
						}
						if(bean.getCode()!=null && (bean.getCode().equals(code))){
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"原因条码不能重复！\"}");
					        return;
						}
					}
					if(reason.equals(bean.getReason())&& bean.getCode()!=null && bean.getCode().equals(code)){
						response.getWriter().write("{\"result\":\"failure\",\"tip\":\"数据未修改无需提交！\"}");
				        return;
					}
				}
				sql = "update returns_reason set reason='"+ StringUtil.toSql(reason) +"',code='"+ code +"' where id=" + StringUtil.toInt(id);
				if(service.getDbOp().executeUpdate(sql)){
					response.getWriter().write("{\"result\":\"success\",\"tip\":\"修改成功！\"}");
			        return;
				} else {
					response.getWriter().write("{\"result\":\"success\",\"tip\":\"修改失败！\"}");
			        return;
				}
			}
			//没有用处了
			else if(!"".equals(id)){
				String sql = "select id,code,reason from returns_reason where id=" + StringUtil.toInt(id);
				ResultSet rs = service.getDbOp().executeQuery(sql);
				if(rs.next()) {
					id = rs.getInt(1)+"";
					code = rs.getString(2);
					reason = rs.getString(3);
				}
				request.setAttribute("id", id);
				request.setAttribute("code", code);
				request.setAttribute("reason", reason);
				response.getWriter().write("{\"result\":\"getData\",\"tip\":\"\"}");
		        return;
			}else{
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"提交信息异常！\"}");
		        return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"操作异常！\"}");
	        return;
		}finally{
			service.releaseAll();
		}
	}
	
	/**
	 * 退货原因删除
	 *
	*/
	@RequestMapping("/returnsReasonDel")
	@ResponseBody
	public void returnsReasonDel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
	    	request.getRequestDispatcher(noSession).forward(request, response);
	        return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(674) ) {
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有销售退货原因删除权限！\"}");
	        return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String id = StringUtil.convertNull(request.getParameter("id"));
		try {
			String sql = "Delete from returns_reason where id="+ StringUtil.toInt(id);
			if(service.getDbOp().executeUpdate(sql)){
				response.getWriter().write("{\"result\":\"success\",\"tip\":\"操作成功！\"}");
		        return;
			}else{
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"删除失败！\"}");
		        return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"异常！\"}");
	        return;
		}finally{
			service.releaseAll();
		}
	}
	
	/**
	 * 退货原因打印
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/returnsReasonPrint")
	public String returnsReasonPrint(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
	    	request.getRequestDispatcher(noSession).forward(request, response);
	        return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String id = StringUtil.convertNull(request.getParameter("id"));
		String code = "";
		String reason = "";
		try {
			String sql = "select id,code,reason from returns_reason where id=" + StringUtil.toInt(id);
			ResultSet rs = service.getDbOp().executeQuery(sql);
			if(rs.next()) {
				id = rs.getInt(1)+"";
				code = rs.getString(2);
				reason = rs.getString(3);
			}
			if(rs != null){
				rs.close();
			}
			request.setAttribute("id", id);
			request.setAttribute("code", code);
			request.setAttribute("reason", reason);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "/admin/rec/oper/salesReturned/returnsReasonCodePrint";
	}
	
	/**
	 * 
	 * 查询包裹核查单
	 */
	@RequestMapping("/getReturnPackageCheckInfo")
	@ResponseBody
	public EasyuiDataGridJson getReturnPackageCheckInfo(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
		String orderCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("orderCode")));
		String packageCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("packageCode")));
		String userName = StringUtil.toSql(StringUtil.convertNull(request.getParameter("userName")));
		String checkTime = StringUtil.toSql(StringUtil.convertNull(request.getParameter("checkTime")));
		int type = StringUtil.toInt(request.getParameter("resultType"));
		int status = ProductWarePropertyService.toInt(request.getParameter("status"));
		
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		Iterator itr = ProductStockBean.areaMap.keySet().iterator();
		String availAreaIds = "100";
		for(; itr.hasNext(); ) {
			availAreaIds += "," + itr.next();
		}
		List returnPackageCheckList = new ArrayList();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			String tempSql = " id <> 0";
			sql.append(tempSql);
			sqlCount.append(tempSql);
			if(packageCode != null && !packageCode.equals("")){
				sql.append(" and package_code='");
				sql.append(packageCode);
				sql.append("'");
				sqlCount.append(" and package_code='");
				sqlCount.append(packageCode);
				sqlCount.append("'");
			}
			if(orderCode != null && !orderCode.equals("")){
				sql.append(" and order_code='");
				sql.append(orderCode);
				sql.append("'");
				sqlCount.append(" and order_code='");
				sqlCount.append(orderCode);
				sqlCount.append("'");
			}
			if (userName != null && !userName.equals("")) {
				sql.append(" and check_user_name='");
				sql.append(userName);
				sql.append("'");
				sqlCount.append(" and check_user_name='");
				sqlCount.append(userName);
				sqlCount.append("'");
			}
			 
			if( wareArea == -1 ) {
				sql.append(" and area in (" + availAreaIds + ")");
				sqlCount.append(" and area in (" + availAreaIds + ")");
			} else {
				sql.append(" and area = " + wareArea);
				sqlCount.append(" and area = " + wareArea);
			}
			
			if( checkTime != null && !checkTime.equals("")){
				String beginTime = checkTime + " 00:00:00";
				String endTime = checkTime + " 23:59:59";
				sql.append(" and check_time between '");
				sql.append(beginTime);
				sql.append("' and '");
				sql.append(endTime);
				sql.append("'");
				sqlCount.append(" and check_time between '");
				sqlCount.append(beginTime);
				sqlCount.append("' and '");
				sqlCount.append(endTime);
				sqlCount.append("'");
			}
			
			if (status != -1 ) {
				sql.append(" and status=");
				sql.append(status);
				sqlCount.append(" and status=");
				sqlCount.append(status);
			}
			
			if (type != -1 ) {
				sql.append(" and type=");
				sql.append(type);
				sqlCount.append(" and type=");
				sqlCount.append(type);
			}
			
			
			List footerList = new ArrayList();
			int totalCount = claimsVerificationService.getReturnPackageCheckCount(sqlCount.toString());
			returnPackageCheckList = claimsVerificationService.getReturnPackageCheckList(sql.toString(), (easyuiDataGrid.getPage()-1)*easyuiDataGrid.getRows(), easyuiDataGrid.getRows(), "id desc");
			int x = returnPackageCheckList.size();
			for ( int i = 0; i < x; i++) {
				ReturnPackageCheckBean rpcBean = (ReturnPackageCheckBean)returnPackageCheckList.get(i);
				if( rpcBean.getCheckResult() == ReturnPackageCheckBean.RESULT_ABNORMAL ) {
					List list = (List<ReturnPackageCheckProductBean>)claimsVerificationService.getReturnPackageCheckProductList("return_package_check_id = " + rpcBean.getId() , -1, -1, "product_id asc");
					rpcBean.setReturnPackageCheckProductList(list);
					OrderStockBean osBean = istockService.getOrderStock("order_code='"+ rpcBean.getOrderCode()+ "' and status!="+OrderStockBean.STATUS4);
					List orderStockProductList = new ArrayList();
					if( osBean != null ) {
						orderStockProductList = (List<OrderStockProductBean>)istockService.getOrderStockProductList("order_stock_id=" + osBean.getId(), -1, -1, "product_id asc");
					}
					rpcBean.setOrderStockProductList(orderStockProductList);
				}
			}
			
			easyuiDataGridJson.setTotal((long)totalCount);
			easyuiDataGridJson.setRows(returnPackageCheckList);
			easyuiDataGridJson.setFooter(footerList);
			return easyuiDataGridJson;
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 包裹和查表页面的“异常类型”
	 * 
	 */
	@RequestMapping("/getTypeName")
	@ResponseBody
	public String getTypeName(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<String, String> map = ReturnPackageCheckBean.getAllTypeName();
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("" + -1);
		bean.setText("全部");
		bean.setSelected(true);
		list.add(bean);
		for(Map.Entry<String, String> entry : map.entrySet()){
			bean = new EasyuiComBoBoxBean();
			bean.setId(entry.getKey());
			bean.setText(entry.getValue());
			list.add(bean);
		}
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 * 包裹和查表页面的“异常处理状态”
	 * 
	 */
	@RequestMapping("/getStatusName")
	@ResponseBody
	public String getStatusName(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<String, String> map = ReturnPackageCheckBean.getAllStatusName();
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("" + -1);
		bean.setText("全部");
		bean.setSelected(true);
		list.add(bean);
		for(Map.Entry<String, String> entry : map.entrySet()){
			bean = new EasyuiComBoBoxBean();
			bean.setId(entry.getKey());
			bean.setText(entry.getValue());
			list.add(bean);
		}
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 * 
	 * 查询包裹核查单的异常详情的实际扫描商品
	 */
	@RequestMapping("/getReturnPackageCheckInfoReal")
	@ResponseBody
	public EasyuiDataGridJson getReturnPackageCheckInfoReal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		int checkId = StringUtil.toInt(request.getParameter("checkId"));
		int checkResult = StringUtil.toInt(request.getParameter("checkResult"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List list = new ArrayList();
		try {
			if( checkResult == ReturnPackageCheckBean.RESULT_ABNORMAL ) {
				list = (List<ReturnPackageCheckProductBean>)claimsVerificationService.getReturnPackageCheckProductList("return_package_check_id = " + checkId , -1, -1, "product_id asc");
			}
			easyuiDataGridJson.setTotal((long)list.size());
			easyuiDataGridJson.setRows(list);
			return easyuiDataGridJson;
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 
	 * 查询包裹核查单的异常详情的出库单内商品
	 */
	@RequestMapping("/getReturnPackageCheckInfoOut")
	@ResponseBody
	public EasyuiDataGridJson getReturnPackageCheckInfoOut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		int checkResult = StringUtil.toInt(request.getParameter("checkResult"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List orderStockProductList = new ArrayList();
		try {
			if( checkResult == ReturnPackageCheckBean.RESULT_ABNORMAL ) {
				OrderStockBean osBean = istockService.getOrderStock("order_code='"+ StringUtil.toSql(orderCode)+ "' and status!="+OrderStockBean.STATUS4);
				if( osBean != null ) {
					orderStockProductList = (List<OrderStockProductBean>)istockService.getOrderStockProductList("order_stock_id=" + osBean.getId(), -1, -1, "product_id asc");
				}
			}
			easyuiDataGridJson.setTotal((long)orderStockProductList.size());
			easyuiDataGridJson.setRows(orderStockProductList);
			return easyuiDataGridJson;
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 
	 * 检查包裹
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("dealReturnPackageCheck")
	@ResponseBody
	public void dealReturnPackageCheck(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		UserGroupBean group = user.getGroup();
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int targetStatus = StringUtil.toInt(request.getParameter("targetStatus"));
		int targetId = StringUtil.toInt(request.getParameter("targetId"));
		try {
			synchronized ( stockLock ) {
				ReturnPackageCheckBean rpcBean = claimsVerificationService.getReturnPackageCheck("id=" + targetId);
				if( rpcBean != null ) {
					if( targetStatus == ReturnPackageCheckBean.STATUS_DEALING ) {
						//改为处理中
						if( !group.isFlag(771) ) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有异常处理操作--处理权限！\"}");
							return;
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_UNDEAL) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前包裹不是未处理状态，不能改为已处理！\"}");
							return;
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_DEALING, "id=" + rpcBean.getId())) {
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新状态时，数据库操作失败！\"}");
								return;
							} else {
								response.getWriter().write("{\"result\":\"success\",\"tip\":\"处理成功！\"}");
								return;
							}
						} 
					} else if( targetStatus == ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS ) {
						//审核通过
						if( !group.isFlag(772) ) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有异常处理操作--审核权限！\"}");
							return;
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_DEALING) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前包裹不是处理中状态，不能改为已审核！\"}");
							return;
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS, "id=" + rpcBean.getId())) {
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新状态时，数据库操作失败！\"}");
								return;
							} else {
								response.getWriter().write("{\"result\":\"success\",\"tip\":\"审核成功！\"}");
								return;
							}
						}
					} else if ( targetStatus == ReturnPackageCheckBean.STATUS_AUDIT_FAIL ) {
						//审核不通过
						if( !group.isFlag(772) ) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有异常处理操作--审核权限！\"}");
							return;
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_DEALING) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前包裹不是处理中状态，不能改为审核不通过！\"}");
							return;
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_AUDIT_FAIL, "id=" + rpcBean.getId())) {
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新状态时，数据库操作失败！\"}");
								return;
							} else {
								response.getWriter().write("{\"result\":\"success\",\"tip\":\"审核成功！\"}");
								return;
							}
						}
					} else if ( targetStatus == ReturnPackageCheckBean.STATUS_COMPLETE ) {
						//完成
						if( !group.isFlag(773) ) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"您没有异常处理操作--确认完成权限！\"}");
							return;
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS) {
							response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前包裹不是已审核状态，不能改为已完成！\"}");
							return;
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_COMPLETE, "id=" + rpcBean.getId())) {
								response.getWriter().write("{\"result\":\"failure\",\"tip\":\"更新状态时，数据库操作失败！\"}");
								return;
							} else {
								response.getWriter().write("{\"result\":\"success\",\"tip\":\"完成成功！\"}");
								return;
							}
						}
					}			
				} else {
					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"没有找到对应的包裹核查记录！\"}");
					return;
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"异常！\"}");
			return;
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 退货原因统计主页
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/returnsReasonStatistic")
	public String returnsReasonStatistic(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(672) ) {
			request.setAttribute("msg", "您没有销售退货原因统计查看权限！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		try {
			String sql = "select id,reason from returns_reason";
			List list = new ArrayList();
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				ReturnsReasonBean bean = new ReturnsReasonBean();
				bean.setId(rs.getInt(1));
				bean.setReason(rs.getString(2));
				list.add(bean);
			}
			if(rs != null){
				rs.close();
			}
			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		if("ajax".equals(flag)){
			return "";
		}else if("detail".equals(flag)){
			return "admin/rec/oper/salesReturned/returnsReasonStatisticDetailed";
		}else if ("day".equals(flag)){
			return "admin/rec/oper/salesReturned/returnsReasonStatisticChildDay";
		} else {
			return "admin/rec/oper/salesReturned/returnsReasonStatisticChildMon";
		}
	}
	
	/**
	 * 退货原因统计子页
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/returnsReasonStatisticChild")
	@ResponseBody
	public void returnsReasonStatisticChild(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(672) ) {
			request.setAttribute("msg", "您没有销售退货原因统计查看权限！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		try {
			List reasonList = new ArrayList();
			int x = 0;
			int y = 0;
			String sql_y = "select id,reason from returns_reason";
			ResultSet rs = service.getDbOp().executeQuery(sql_y);
			while (rs.next()) {
				ReturnsReasonBean bean = new ReturnsReasonBean();
				bean.setId(rs.getInt(1));
				bean.setReason(rs.getString(2));
				reasonList.add(bean);
			}
			if(rs != null){
				rs.close();
			}
			y = reasonList.size();
			if("day".equals(flag)){
				String endTime = DateUtil.formatDate(new Date());
				String startTime = endTime.substring(0,8)+"01";
				String sql_x = "SELECT  count( distinct left(storage_time,10)) FROM returned_package where left(storage_time,10)  between '" + startTime + "' and '" + endTime + "'";
				rs = service.getDbOp().executeQuery(sql_x);
				while (rs.next()) {
					x = rs.getInt(1);
				}
				if(rs != null){
					rs.close();
				}
				String statistics[][]=new String[x+1][y+2];
				statistics[0][0]= "日期";
				statistics[0][y+1]= "总退货量";
				for(int m=0;m<y;m++){
					statistics[0][m+1] = ((ReturnsReasonBean)reasonList.get(m)).getReason();
				}
				String sql = "SELECT  left(storage_time,10),count(order_id),rr.reason  FROM returned_package rp left join returns_reason rr on rp.reason_id=rr.id where left(storage_time,10) between  '"+ startTime + "' and '" + endTime + "' group by  left(storage_time,10),rr.reason";
				
				rs = service.getDbOp().executeQuery(sql);
				int n = 1;
				while (rs.next()) {
					if(rs.getString(1).equals(statistics[n-1][0])){
						n--;
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(rs.getString(3).equals(statistics[0][j])){
									statistics[n][j]= rs.getString(2).toString();
									if(statistics[n][y+1]!=null){
										statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(statistics[n][j])) + "";
									}else{
										statistics[n][y+1] = (0 + Integer.parseInt(statistics[n][j])) + "";
									}
									break;
								}
							}
						}else{
							statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(rs.getString(2))) +"";
						}
					}else{
						statistics[n][0]= rs.getString(1);
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(statistics[0][j].equals(rs.getString(3))){
									statistics[n][j]= rs.getString(2).toString();
									statistics[n][y+1] = statistics[n][j];
									break;
								}
							}
						}else{
							statistics[n][y+1] = rs.getString(2).toString();
						}
					}
					n++;
				}
				if(rs != null){
					rs.close();
				}
				Map returnMap = new HashMap();
				//所有的列
				List fieldAllList = new ArrayList();
				//第一行的列
				List fieldList = new ArrayList();
				//第二行的列，只有退货量和占比，一般的field为column+列号，占比为column+列号+percent
				List subFieldList = new ArrayList();
				List rowsList = new ArrayList();
				for (int i = 0 ; i < x+1 ; i ++) {
					Map rowsMap = new HashMap();
					for (int j = 0 ; j < y+2; j++) {
						if (i == 0) {
							//fieldmap放一个field
							//fieldList放所有fieldMap
							//returnMap第一个放fieldList
							Map fieldMap = new HashMap();
							if (j != 0 && j != y+1) {
								fieldMap.put("colspan", "2");
								fieldMap.put("title", statistics[i][j] + "");
								fieldMap.put("align", "center");
								Map subFieldMap = new HashMap();
								subFieldMap.put("field", "column"+j);
								subFieldMap.put("title", "退货量");
								subFieldMap.put("align", "center");
								subFieldMap.put("width", "70");
								subFieldList.add(subFieldMap);
								subFieldMap = new HashMap();
								subFieldMap.put("field", "column"+j+"percent");
								subFieldMap.put("title", "占比");
								subFieldMap.put("align", "center");
								subFieldMap.put("width", "70");
								subFieldList.add(subFieldMap);
							} else {
								fieldMap.put("field", "column"+j);
								fieldMap.put("title", statistics[i][j] + "");
								fieldMap.put("align", "center");
								fieldMap.put("rowspan", "2");
								fieldMap.put("width", "70");
							}
							fieldList.add(fieldMap);
						} else {
							//rowsMap放单条数据
							//rowsList放rowsMap
							rowsMap.put("column"+j, statistics[i][j] == null || statistics[i][j].equals("") ? "0" : ""+statistics[i][j]);
							if (j != 0 && j != y+1) {
								if (statistics[i][j] == null || statistics[i][j].equals("")) {
									rowsMap.put("column"+j+"percent", "0");
								} else {
									double number= StringUtil.toDouble(statistics[i][j]) / StringUtil.toDouble(statistics[i][y+1])*100 ;
									int index = (number+"").indexOf(".");
									String newNumber = (number+"").substring(0,index+2);
									rowsMap.put("column"+j+"percent", newNumber+"%");
								}
							}
						}
					}
					if (i != 0) {
						rowsList.add(rowsMap);
					}
				}
				EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
				easyuiDataGridJson.setTotal((long)rowsList.size());
				easyuiDataGridJson.setRows(rowsList);
				fieldAllList.add(fieldList);
				fieldAllList.add(subFieldList);
				returnMap.put("columns", fieldAllList);
				returnMap.put("data", easyuiDataGridJson);
				response.getWriter().write(JSONObject.fromObject(returnMap).toString());
				return;
			}else if("moon".equals(flag)){
				String endTime = DateUtil.formatDate(new Date());
				endTime = endTime.substring(0,7);
				String startTime = endTime.substring(0,5)+"01";
				String sql_x = "SELECT  count( distinct left(storage_time,7)) FROM returned_package where left(storage_time,7) between '" + startTime + "' and '" + endTime + "'";
				rs = service.getDbOp().executeQuery(sql_x);
				while (rs.next()) {
					x = rs.getInt(1);
				}
				if(rs != null){
					rs.close();
				}
				String statistics[][]=new String[x+1][y+2];
				statistics[0][0]= "日期";
				statistics[0][y+1]= "总退货量";
				for(int m=0;m<y;m++){
					statistics[0][m+1] = ((ReturnsReasonBean)reasonList.get(m)).getReason();
				}
				String sql = "SELECT  left(storage_time,7),count(order_id),rr.reason  FROM returned_package rp left join returns_reason rr on rp.reason_id=rr.id where left(storage_time,7) between '"+ startTime + "' and '" + endTime + "' group by  left(storage_time,7),rr.reason";
				rs = service.getDbOp().executeQuery(sql);
				int n = 1;
				while (rs.next()) {
					if(rs.getString(1).equals(statistics[n-1][0])){
						n--;
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(rs.getString(3).equals(statistics[0][j])){
									statistics[n][j]= rs.getString(2).toString();
									if(statistics[n][y+1]!=null){
										statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(statistics[n][j])) + "";
									}else{
										statistics[n][y+1] = (0 + Integer.parseInt(statistics[n][j])) + "";
									}
									break;
								}
							}
						}else{
							statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(rs.getString(2))) +"";
						}
					}else{
						statistics[n][0]= rs.getString(1);
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(statistics[0][j].equals(rs.getString(3))){
									statistics[n][j]= rs.getString(2).toString();
									statistics[n][y+1] = statistics[n][j];
									break;
								}
							}
						}else{
							statistics[n][y+1] = rs.getString(2).toString();
						}
					}
					n++;
				}
				if(rs != null){
					rs.close();
				}
				Map returnMap = new HashMap();
				//所有的列
				List fieldAllList = new ArrayList();
				//第一行的列
				List fieldList = new ArrayList();
				//第二行的列，只有退货量和占比，一般的field为column+列号，占比为column+列号+percent
				List subFieldList = new ArrayList();
				List rowsList = new ArrayList();
				for (int i = 0 ; i < x+1 ; i ++) {
					Map rowsMap = new HashMap();
					for (int j = 0 ; j < y+2; j++) {
						if (i == 0) {
							//fieldmap放一个field
							//fieldList放所有fieldMap
							//returnMap第一个放fieldList
							Map fieldMap = new HashMap();
							if (j != 0 && j != y+1) {
								fieldMap.put("colspan", "2");
								fieldMap.put("title", statistics[i][j] + "");
								fieldMap.put("align", "center");
								Map subFieldMap = new HashMap();
								subFieldMap.put("field", "column"+j);
								subFieldMap.put("title", "退货量");
								subFieldMap.put("align", "center");
								subFieldMap.put("width", "70");
								subFieldList.add(subFieldMap);
								subFieldMap = new HashMap();
								subFieldMap.put("field", "column"+j+"percent");
								subFieldMap.put("title", "占比");
								subFieldMap.put("align", "center");
								subFieldMap.put("width", "70");
								subFieldList.add(subFieldMap);
							} else {
								fieldMap.put("field", "column"+j);
								fieldMap.put("title", statistics[i][j] + "");
								fieldMap.put("align", "center");
								fieldMap.put("rowspan", "2");
								fieldMap.put("width", "70");
							}
							fieldList.add(fieldMap);
						} else {
							//rowsMap放单条数据
							//rowsList放rowsMap
							rowsMap.put("column"+j, statistics[i][j] == null || statistics[i][j].equals("") ? "0" : ""+statistics[i][j]);
							if (j != 0 && j != y+1) {
								if (statistics[i][j] == null || statistics[i][j].equals("")) {
									rowsMap.put("column"+j+"percent", "0");
								} else {
									double number= StringUtil.toDouble(statistics[i][j]) / StringUtil.toDouble(statistics[i][y+1])*100 ;
									int index = (number+"").indexOf(".");
									String newNumber = (number+"").substring(0,index+2);
									rowsMap.put("column"+j+"percent", newNumber+"%");
								}
							}
						}
					}
					if (i != 0) {
						rowsList.add(rowsMap);
					}
				}
				EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
				easyuiDataGridJson.setTotal((long)rowsList.size());
				easyuiDataGridJson.setRows(rowsList);
				fieldAllList.add(fieldList);
				fieldAllList.add(subFieldList);
				returnMap.put("columns", fieldAllList);
				returnMap.put("data", easyuiDataGridJson);
				response.getWriter().write(JSONObject.fromObject(returnMap).toString());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}finally{
			service.releaseAll();
		}
		return;
	}
	
	
	/**
	 * 退货原因统计查询页
	 */
	@RequestMapping("/returnsReasonStatisticDetail")
	@ResponseBody
	public String returnsReasonStatisticDetail(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(672) ) {
			request.setAttribute("msg", "您没有销售退货原因统计查看权限！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		String packageCode = StringUtil.convertNull(request.getParameter("packageCode"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String reasonId = StringUtil.convertNull(request.getParameter("reasonId"));
		try{
			if("query".equals(flag)){
				StringBuilder url = new StringBuilder();
				url.append("returnStorageAction.do?method=returnsReasonStatisticDetail&flag=query");
				StringBuffer totalSql = new StringBuffer();
				StringBuffer sql = new StringBuffer();
				totalSql.append(" select count(distinct order_code) from returned_package rp left join returns_reason rr on rp.reason_id=rr.id where rp.id>0 ");
				sql.append(" select order_code,package_code,deliver,storage_time,storage_status,rr.reason from returned_package rp left join returns_reason rr on rp.reason_id=rr.id where rp.id>0 ");
				if(!"".equals(orderCode)){
					sql.append(" and order_code='" + StringUtil.toSql(orderCode) + "'");
					totalSql.append(" and order_code='" + StringUtil.toSql(orderCode) + "'");
					url.append("&orderCode=" + orderCode);
				}
				if(!"".equals(packageCode)){
					sql.append(" and package_code='" + StringUtil.toSql(packageCode) + "'");
					totalSql.append(" and package_code='" + StringUtil.toSql(packageCode) + "'");
					url.append("&packageCode=" + packageCode);
				}
				if(!"".equals(startTime) && !"".equals(endTime)){
					sql.append(" and left(storage_time,10) between '" + StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
					totalSql.append(" and left(storage_time,10) between '" + StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
					url.append("&startTime=" + startTime + "&endTime=" + endTime);
				}
				if(!"".equals(reasonId)){
					sql.append(" and rr.id='" + StringUtil.toSql(reasonId) + "'");
					totalSql.append(" and rr.id='" + StringUtil.toSql(reasonId) + "'");
					url.append("&reasonId=" + reasonId);
				}
				sql.append(" order by left(storage_time,10)");
				int totalCount = 0;
				ResultSet rs = service.getDbOp().executeQuery(totalSql.toString());
				if(rs.next()){
					totalCount = rs.getInt(1);
				}
				sql.append(" limit "+ (easyuiDataGrid.getPage()-1)*easyuiDataGrid.getRows() +"," +easyuiDataGrid.getRows());
				List rpList = new ArrayList();
				rs = service.getDbOp().executeQuery(sql.toString());
				while (rs.next()){
					ReturnedPackageBean rpBean = new ReturnedPackageBean();
					rpBean.setOrderCode(rs.getString(1));
					rpBean.setPackageCode(rs.getString(2)==null || "".equals(rs.getString(2)) ? "未知" : rs.getString(2));
					rpBean.setDeliver(rs.getInt(3));
					rpBean.setDeliverName((String)voOrder.deliverMapAll.get(""+rs.getInt(3))==null ? "未知" : (String)voOrder.deliverMapAll.get(""+rs.getInt(3)));
					rpBean.setStorageTime(rs.getString(4));
					rpBean.setStorageStatus(rs.getInt(5));
					rpBean.setReasonName(rs.getString(6)==null ? "未知" : rs.getString(6));
					rpList.add(rpBean);
				}
				if(rs != null){
					rs.close();
				}
				EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
				easyuiDataGridJson.setTotal((long)totalCount);
				easyuiDataGridJson.setRows(rpList);
				return JSONObject.fromObject(easyuiDataGridJson).toString();
			}else{
				StringBuffer sql = new StringBuffer();
				sql.append(" select order_code,package_code,deliver,storage_time,storage_status,rr.reason from returned_package rp left join returns_reason rr on rp.reason_id=rr.id where rp.id>0 ");
				if(!"".equals(orderCode)){
					sql.append(" and order_code='" + StringUtil.toSql(orderCode) + "'");
				}
				if(!"".equals(packageCode)){
					sql.append(" and package_code='" + StringUtil.toSql(packageCode) + "'");
				}
				if(!"".equals(startTime)){
					sql.append(" and left(storage_time,10) between '" + StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
				}
				if(!"".equals(reasonId)){
					sql.append(" and rr.id='" + StringUtil.toSql(reasonId) + "'");
				}
				sql.append(" order by left(storage_time,10)");
				ResultSet rs = service.getDbOp().executeQuery(sql.toString());
				List rpList = new ArrayList();
				while (rs.next()){
					ReturnedPackageBean rpBean = new ReturnedPackageBean();
					rpBean.setOrderCode(rs.getString(1));
					rpBean.setPackageCode(rs.getString(2)==null || "".equals(rs.getString(2))  ? "未知" : rs.getString(2));
					rpBean.setDeliver(rs.getInt(3));
					rpBean.setDeliverName((String)voOrder.deliverMapAll.get(""+rs.getInt(3))==null ? "未知" : (String)voOrder.deliverMapAll.get(""+rs.getInt(3)));
					rpBean.setStorageTime(rs.getString(4));
					rpBean.setStorageStatus(rs.getInt(5));
					rpBean.setReasonName(rs.getString(6)==null ? "未知" : rs.getString(6));
					rpList.add(rpBean);
				}
				if(rs != null){
					rs.close();
				}
				request.setAttribute("rpList", rpList);
				request.getRequestDispatcher("/admin/rec/oper/salesReturned/exportReturnsReasonStatisticDetail.jsp").forward(request, response);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}finally{
			service.releaseAll();
		}
	}
	
	/**
	 * 统计明细页面的“销售退货原因”
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getReturnReasons")
	@ResponseBody
	public String getReturnReasons(HttpServletRequest request, HttpServletResponse response)throws Exception{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		ResultSet rs = null;
		try {
			//构建查询语句
	        String query = 	"select id,reason from returns_reason";
	        rs = dbOp.executeQuery(query);
	        List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
	        EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
	        bean.setId("");
        	bean.setText("全部");
        	bean.setSelected(true);
        	list.add(bean);
	        while(rs.next()){
	        	bean = new EasyuiComBoBoxBean();
	        	bean.setId(rs.getInt(1) + "");
	        	bean.setText(rs.getString(2));
	        	list.add(bean);
			}
	        return JSONArray.fromObject(list).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return null;
	}
	
	
	/**
	 * 异常入库单列表页面的“异常单状态”
	 * 
	 */
	@RequestMapping("/getAbnormalStatusName")
	@ResponseBody
	public String getAbnormalStatusName(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<Integer,String> map = new WarehousingAbnormalBean().getStatusMap();
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		for(Map.Entry<Integer, String> entry : map.entrySet()){
			bean = new EasyuiComBoBoxBean();
			bean.setId("" + entry.getKey());
			bean.setText(entry.getValue());
			if (entry.getKey() == -1) {
				bean.setSelected(true);
			}
			list.add(bean);
		}
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 *	异常入库单列表 
	 */ 
	@RequestMapping("/selectAbnormalList")
	@ResponseBody
	public EasyuiDataGridJson selectAbnormalList(HttpServletRequest request,
			HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(753)){
			request.setAttribute("msg", "对不起,你没有异常入库单访问权限!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		
		String bsbyResult = StringUtil.convertNull(request.getParameter("bsbyResult")).trim();
		String abnormalCode = StringUtil.convertNull(request.getParameter("abnormalCode")).trim();
		String startTime = StringUtil.convertNull(request.getParameter("startTime")).trim();
		String endTime = StringUtil.convertNull(request.getParameter("endTime")).trim();
		String operator = StringUtil.convertNull(request.getParameter("operator")).trim();
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode")).trim();
		String packageCode = StringUtil.convertNull(request.getParameter("packageCode")).trim();
		String deliver = StringUtil.convertNull(request.getParameter("deliver")).trim();
		String status = StringUtil.convertNull(request.getParameter("status")).trim();
		String area = StringUtil.convertNull(request.getParameter("wareArea")).trim();
		
		WarehousingAbnormalBean bean = null;
		ResultSet rs = null;
		try {
			StringBuilder condition = new StringBuilder();
			if (!"".equals(abnormalCode)) {
				condition.append(" and wa.code='"+ StringUtil.toSql(abnormalCode) + "'");
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				condition.append(" and left(wa.create_time,10) between '"+ StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
			}
			if (!"".equals(operator)) {
				if(operator.matches("^[0-9]+$")){
					bean = service.getWarehousingAbnormal("operator_id=" + operator);
				}else{
					bean = service.getWarehousingAbnormal("operator_name='"+ operator + "'");
				}
				if(bean != null){
					condition.append(" and wa.operator_id=" + bean.getOperatorId());
				}else{
					condition.append(" and 1=2");
				}
			}
			if (!"".equals(orderCode)) {
				condition.append(" and ap.order_code='"+ StringUtil.toSql(orderCode) + "'");
			}
			if (!"".equals(packageCode)) {
				condition.append(" and ap.package_code='"+ StringUtil.toSql(packageCode) + "'");
			}
			if (!"".equals(deliver) && !"-1".equals(deliver)) {
				condition.append(" and ap.deliver="+ deliver);
			}
			if (!"".equals(status) && !"-1".equals(status)) {
				condition.append(" and wa.status="+ status);
			}
			if ("".equals(area) || "-1".equals(area)){
				area = "";
				List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
				condition.append(" and (");
				for(String arg : cdaList){
					condition.append("wa.ware_area="+ arg + " or ");
					area = area.concat(arg  + ",");
				}
				condition.append(" 1=2) ");
			}else if(area.contains(",")){
				String areaId[] = area.split(",");
				area = "";
				condition.append(" and (");
				for(String arg : areaId){
					condition.append("wa.ware_area="+ arg + " or ");
					area = area.concat(arg  + ",");
				}
				condition.append(" 1=2) ");
			}else if("-2".equals(area)){
				condition.append(" and 1=2");
			}else{
				condition.append(" and wa.ware_area=" + area);
			}
			String query = "select count(distinct wa.id) from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					" where wa.id>0 "+ condition ;
			int totalCount = 0;
			rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				totalCount = rs.getInt(1);
			}
			query = "select distinct wa.code,wa.create_time,wa.status,ap.order_code,ap.package_code,ap.deliver,ap.sorting_datetime," +
					"wa.operator_name,wa.id from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id>0 "+ condition
					+ " order by wa.create_time DESC limit "  
					+ ((easyuiDataGrid.getPage()-1) * easyuiDataGrid.getRows())
					+ ","
					+ easyuiDataGrid.getRows();
			rs = service.getDbOp().executeQuery(query);
			String isAdd;
			String isEditAndDel;
			String isAudit;
			if (group.isFlag(754)) {
				isAdd = "true";
			} else {
				isAdd = "false";
			}
			//编辑和删除权限
			if(group.isFlag(755)){ 
				isEditAndDel = "true";
			} else {
				isEditAndDel = "false";
			}
			//审核权限
			if(group.isFlag(858)){
				isAudit = "true";
			} else {
				isAudit = "false";
			}
			List<WarehousingAbnormalBean> abnormalList = new ArrayList<WarehousingAbnormalBean>();
			while (rs.next()) {
				WarehousingAbnormalBean waBean = new WarehousingAbnormalBean();
				waBean.setCode(rs.getString(1));
				waBean.setCreateTime(rs.getString(2));
				waBean.setStatus(rs.getInt(3));
				waBean.setStatusName(WarehousingAbnormalBean.statusMap.get(waBean.getStatus()));
				waBean.setOrderCode(rs.getString(4));
				waBean.setPackageCode("".equals(rs.getString(5)) ? "无" : rs.getString(5));
				waBean.setDeliver(rs.getInt(6));
				waBean.setDeliverName((String) voOrder.deliverMapAll.get(waBean.getDeliver()+""));
				waBean.setSortingDatetime(rs.getString(7));
				waBean.setOperatorName(rs.getString(8));
				waBean.setId(rs.getInt(9));
				waBean.setIsAdd(isAdd);
				waBean.setIsEditAndDel(isEditAndDel);
				waBean.setIsAudit(isAudit);
				abnormalList.add(waBean);
			}
			for(WarehousingAbnormalBean abnormalBean : abnormalList){
				StringBuffer receiptsNumber = new StringBuffer();
				StringBuffer bsbyId = new StringBuffer();
				StringBuffer lookup = new StringBuffer();
				query ="select bba.abnormal_id,bbo.receipts_number,bbo.type,bbo.id,bbo.current_type,bbo.operator_id " +
						"from bsby_abnormal bba left join bsby_operationnote bbo on bba.bsby_id = bbo.id " +
						" where bba.abnormal_id=" + abnormalBean.getId();
				rs = service.getDbOp().executeQuery(query);
				while (rs.next()) {
					WarehousingAbnormalBean bsbyBean = new WarehousingAbnormalBean();
					bsbyBean.setId(rs.getInt(1));
					bsbyBean.setReceiptsNumber(rs.getString(2));
					bsbyBean.setBsbyType(rs.getInt(3));
					bsbyBean.setBsbyId(rs.getInt(4));
					bsbyBean.setBsbyStatus(rs.getInt(5));
					bsbyBean.setBsbyOperatorId(rs.getInt(6));
					int type = bsbyBean.getBsbyStatus();
		  		    if(bsbyBean.getReceiptsNumber() != null){
		  		    	if (receiptsNumber.length() > 0) {
		  		    		receiptsNumber.append(",");
		  		    		bsbyId.append(",");
		  		    		lookup.append(",");
		  		    	}
		  		    	receiptsNumber.append(bsbyBean.getReceiptsNumber());
		  		    	bsbyId.append(bsbyBean.getBsbyId());
						if((type==0||type==1||type==2||type==5)&&(user.getId()==bsbyBean.getBsbyOperatorId()||group.isFlag(413))){
							lookup.append("0");
						}else if(type==6&&(user.getId()==bsbyBean.getBsbyOperatorId()||group.isFlag(229))){
							lookup.append("0");
						}else {
							lookup.append("1");
						}
					}
				}
				abnormalBean.setReceiptsNumberString(receiptsNumber.toString());
				abnormalBean.setBsbyIdString(bsbyId.toString());
				abnormalBean.setLookupString(lookup.toString());
			}
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long) totalCount);
			easyuiDataGridJson.setRows(abnormalList);
			return easyuiDataGridJson;
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "程序异常!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
	}
	
	/**
	 *	查询商品相关信息 
	 */
	@RequestMapping("/findProductInfoList")
	@ResponseBody
	public String findProductInfoList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String code = StringUtil.convertNull(request.getParameter("code").trim());
		String wareArea = StringUtil.convertNull(request.getParameter("wareArea").trim());
		response.setContentType("text/html; charset=utf-8");
		ResultSet rs = null;
		try {
			String sql = null;
			String orderCode = null;
			String packageCode = null;
			sql = "select package_code from deliver_package_code where package_code='" + StringUtil.toSql(code) + "' and used=1";
			rs = service.getDbOp().executeQuery(sql);
			if(rs.next()){
				packageCode = rs.getString(1);
			}
			AuditPackageBean apBean = null;
			if(packageCode != null){
				apBean = service.getAuditPackage("package_code='" + packageCode + "'");
			}
			if(apBean != null){
				orderCode = apBean.getOrderCode();
				String result = checkOrder(orderCode,wareArea);
				if(!"ok".equals(result)){//调用checkOrder方法判断订单是否符合要求
					response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
					return null;
				}
			}else{
				String result = checkOrder(code,wareArea);
				if(!"ok".equals(result)){//调用checkOrder方法判断订单是否符合要求
					response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
					return null;
				}
				apBean = service.getAuditPackage("order_code='" + StringUtil.toSql(code) + "'");
				if (apBean == null) {
					response.getWriter().write("{\"tip\":\"查不到包裹单信息！\",\"result\":\"failure\"}");
					return null;
				}
			}
			//获取商品列表
			List<voProduct> vpList = getVOProductList(apBean.getOrderId());
			List fieldList = new ArrayList();
			Map fieldMap = new HashMap();
			fieldMap.put("field", "orderCodeName");
			fieldMap.put("title", "订单号");
			fieldMap.put("align", "center");
			fieldMap.put("width", "100");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "theOrderCode");
			fieldMap.put("title", apBean.getOrderCode());
			fieldMap.put("align", "center");
			fieldMap.put("width", "200");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "packageCodeName");
			fieldMap.put("title", "包裹单号");
			fieldMap.put("align", "center");
			fieldMap.put("width", "100");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "thePackageCode");
			fieldMap.put("title", apBean.getPackageCode() == null ? "" : apBean.getPackageCode());
			fieldMap.put("align", "center");
			fieldMap.put("width", "200");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "theDeliverName");
			fieldMap.put("title", "快递公司");
			fieldMap.put("align", "center");
			fieldMap.put("width", "100");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "theDeliver");
			fieldMap.put("title", voOrder.deliverMapAll.get(apBean.getDeliver()+""));
			fieldMap.put("align", "center");
			fieldMap.put("width", "200");
			fieldList.add(fieldMap);
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long) vpList.size());
			easyuiDataGridJson.setRows(vpList);
			Map returnMap = new HashMap();
			returnMap.put("columns", fieldList);
			returnMap.put("data", easyuiDataGridJson);
			request.getSession().setAttribute("vpList", vpList);
			request.getSession().setAttribute("apBean", apBean);
			request.getSession().setAttribute("wareArea", wareArea);
			response.getWriter().write(JSONObject.fromObject(returnMap).toString());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常\",\"result\":\"failure\"}");
			return null;
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
	}
	
	/**
	 * 	说明：1.判断订单编号是否符合条件 返回boolean型 条件为：1订单已退回2退货库与选择的库地区是否匹配
	 *	日期：2013-4-17
	 * 	作者：石远飞
	 * @throws SQLException 
	 */
	public String checkOrder(String orderCode,String wareArea) throws SQLException{
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);	
		String result = "ok";
		ResultSet rs = null;
		String query = null;
		try {
			query = "select uo.id,uo.code from user_order uo left join user_order_status uos on uo.status=uos.id left join " +
					 "returned_package rp on uo.id=rp.order_id where uo.code='" + StringUtil.toSql(orderCode) + "'";
				rs = service.getDbOp().executeQuery(query);
				if(!rs.next()){
					result = "订单或包裹单不存在！";
					return result;
				}
			query = "select uo.id,uo.code from user_order uo left join user_order_status uos on uo.status=uos.id left join " +
				 "returned_package rp on uo.id=rp.order_id where uo.code='" + StringUtil.toSql(orderCode) + "' and uo.status=11";
			rs = service.getDbOp().executeQuery(query);
			if(!rs.next()){
				result = "该订单未入库  不能添加异常入库单！";
				return result;
			}
			query = "select uo.id,uo.code from user_order uo left join user_order_status uos on uo.status=uos.id left join " +
				"returned_package rp on uo.id=rp.order_id where uo.code='" + StringUtil.toSql(orderCode) + "' and rp.area=" + wareArea;
			rs = service.getDbOp().executeQuery(query);
			if(!rs.next()){
				result = "该订单退货库与选择库地区不符！";
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return result;
	}
	
	/**
	 *	说明：1.根据订单号获取商品列表
	 *	日期：2013-4-17
	 *	作者：石远飞
	 */
	public List<voProduct> getVOProductList(int orderId) throws Exception {
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		List<voProduct> vpList = new ArrayList<voProduct>();
		ResultSet rs = null;
		try {
			OrderStockBean osBean = service.getOrderStock("order_id="+ orderId+" and status<>3");
			if (osBean != null) {
				String sql = "select p.code,p.name,p.oriname,osp.stockout_count from product p left join order_stock_product osp " +
						  "on p.code=osp.product_code where osp.order_stock_id="+ osBean.getId();
				rs = service.getDbOp().executeQuery(sql);
				while(rs.next()){
					voProduct vpBean = new voProduct();
					vpBean.setCode(rs.getString(1));
					vpBean.setName(rs.getString(2));
					vpBean.setOriname(rs.getString(3));
					vpBean.setCount(rs.getInt(4));
					vpList.add(vpBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return vpList;
	}
	
	/**
	 * 	说明：实际退回商品
	 */
	@RequestMapping("/addRealProduct")
	@ResponseBody
	public void addRealProduct(
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		response.setContentType("text/html; charset=utf-8");
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		String realCode = StringUtil.convertNull(request.getParameter("realCode").trim());
		String realCount = StringUtil.convertNull(request.getParameter("realCount").trim());
		try {
			List<voProduct>	rpList = new ArrayList<voProduct>();
			voProduct vpBean = wareService.getProduct(StringUtil.toSql(realCode));
			if (vpBean == null) {
				response.getWriter().write("{\"tip\":\"商品编号不正确!\",\"result\":\"failure\"}");
				return;
			}
			vpBean.setCount(Integer.parseInt(realCount));
			rpList.add(vpBean);
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long)1);
			easyuiDataGridJson.setRows(rpList);
			response.getWriter().write(JSONObject.fromObject(easyuiDataGridJson).toString());
			return;
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常!\",\"result\":\"failure\"}");
			return;
		} finally {
			wareService.releaseAll();
		}
	}
	
	/**
	 *	说明：1.生成异常入库单2保存实际退回商品列表
	 */
	@RequestMapping("/addWarehousingAbnormal")
	@ResponseBody
	public void addWarehousingAbnormal( HttpServletRequest request,HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(754)){
			response.getWriter().write("{\"tip\":\"对不起,你没有添加异常入库单权限!\",\"result\":\"failure\"}");
			return;
		}
		response.setContentType("text/html; charset=utf-8");
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, db);
		String abnormalId = null;
		AuditPackageBean apBean = (AuditPackageBean) request.getSession().getAttribute("apBean");
		String wareArea = (String) request.getSession().getAttribute("wareArea");
		List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
		voProduct vp = new voProduct();
		String rp = request.getParameter("bat");
		List<voProduct> rpList = new ArrayList<voProduct>();
		String[] theArray = rp.split(",");
		for (int i = 0; i < theArray.length; i++) {
			switch (i%5) {
			case 0: vp.setId(StringUtil.toInt(theArray[i]));
				break;
			case 1: vp.setCount(StringUtil.toInt(theArray[i]));
				break;
			case 2: vp.setOriname(theArray[i]);
				break;
			case 3: vp.setCode(theArray[i]);
				break;
			case 4: vp.setName(theArray[i]); rpList.add(vp);vp = new voProduct();
				break;
			default:
				break;
			}
		}
		try {
			String code = "YCR" + DateUtil.getNow().substring(2, 4)+ DateUtil.getNow().substring(5, 7)+ DateUtil.getNow().substring(8, 10);
			int maxid = service.getNumber("id", "warehousing_abnormal", "max","id > 0");
			WarehousingAbnormalBean lastCode = service.getWarehousingAbnormal("code like '" + code + "%'");
			if (lastCode == null) {
				// 当日第一份单据,编号最后5位 00001
				code += "00001";
			} else {
				// 获取当日计划编号最大值
				lastCode = service.getWarehousingAbnormal("id =" + maxid);
				String _code = lastCode.getCode();
				int number = Integer.parseInt(_code.substring(_code.length() - 5));
				if (number == 99999) {
					response.getWriter().write("{\"tip\":\"当天的异常入库单不能生成超过99999单!\",\"result\":\"failure\"}");
					return;
				}
				number++;
				code += String.format("%05d",new Object[] { new Integer(number) });
			}
			if(isTimeout(request)){ //校验session里的内容是否超时
				response.getWriter().write("{\"tip\":\"没有添加实际退回商品,或操作超时,请重新操作!\",\"result\":\"failure\"}");
				return;
			}
			if(rpList.size()==0){
				response.getWriter().write("{\"tip\":\"请输入实际退回商品!\",\"result\":\"failure\"}");
				return;
			}
			if(!checkAbnormalOrCompensate(listTurnMap(vpList), listTurnMap(rpList))){
				response.getWriter().write("{\"tip\":\"不符合异常入库单条件 或请走理赔单!\",\"result\":\"failure\"}");
				return;
			}
			WarehousingAbnormalBean waBean = new WarehousingAbnormalBean();
			waBean.setCode(code);
			waBean.setStatus(0);
			waBean.setOperatorId(user.getId());
			waBean.setOperatorName(user.getUsername());
			waBean.setCreateTime(DateUtil.getNow());
			waBean.setOrderId(apBean.getOrderId());
			waBean.setWareArea(Integer.parseInt(wareArea));
			// service插入异常入库单
			service.getDbOp().startTransaction(); //开启事务
			if (!service.addWarehousingAbnormal(waBean)) {
				service.getDbOp().rollbackTransaction();
				response.getWriter().write("{\"tip\":\"添加异常入库单失败!\",\"result\":\"failure\"}");
				return;
			}
			maxid = service.getNumber("id", "warehousing_abnormal", "max","id > 0");
			WarehousingAbnormalBean anormalBean = service.getWarehousingAbnormal("id=" + maxid);
			abnormalId = anormalBean.getId() + "";
			for(voProduct bean : rpList){
				AbnormalRealProductBean arpBean = new AbnormalRealProductBean();
				arpBean.setProductCode(bean.getCode());
				arpBean.setProductCount(bean.getCount());
				arpBean.setProductId(bean.getId());
				arpBean.setProductName(bean.getName());
				arpBean.setProductOriname(bean.getOriname());
				arpBean.setAbnormalId(anormalBean.getId());
				if(!service.addAbnormalRealProduct(arpBean)){
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"保存实际退回商品失败!\",\"result\":\"failure\"}");
					return;
				}
			}
			String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id = " + anormalBean.getId() ;
			ResultSet rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				if (!packageLog.addReturnPackageLog("添加异常入库单", user, rs.getString(1))) {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"添加退货日志失败！\",\"result\":\"failure\"}");
					return;
				}
			}
			service.getDbOp().commitTransaction(); //提交事务
			response.getWriter().write("{\"tip\":\"成功！\",\"result\":\"success\",\"abnormalId\":\""+abnormalId+"\"}");
			return;
		} catch (Exception e) {
			service.getDbOp().rollbackTransaction();
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常 !\",\"result\":\"failure\"}");
			return;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 	说明：1.检查session里的对象是超时
	 **/
	@SuppressWarnings("unchecked")
	public boolean isTimeout(HttpServletRequest request ) throws Exception {
		boolean flag = false;
		AuditPackageBean apBean = (AuditPackageBean) request.getSession().getAttribute("apBean");
		if(apBean == null){
			flag = true;
		}
		String wareArea = (String) request.getSession().getAttribute("wareArea");
		if(wareArea == null){
			flag = true;
		}
		List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
		if(vpList == null){
			flag = true;
		}
		return flag;
	}
	
	/**
	 *	说明：1.将list里的内容封装到map中 返回一个map
	 *	日期：2013-4-16
	 * 	作者：石远飞
	 */
	public Map<String,voProduct> listTurnMap(List<voProduct> list) {
		synchronized (stockLock) {
			Map<String, voProduct> map = new HashMap<String, voProduct>();
			if(list!=null && list.size()>0){
				for(voProduct bean:list){
					map.put(bean.getCode(), bean);
				}
			}
			return map;
		}
	}
	
	/**
	 * 	说明：1.验证是异常单还是理赔单
	 *	日期：2013-4-17
	 * 	作者：石远飞
	 */
	public boolean checkAbnormalOrCompensate(Map<String,voProduct> vpMap,Map<String,voProduct> rpMap) throws Exception {
		synchronized (stockLock) {
			boolean flag = false;
			if(!rpMap.isEmpty() && rpMap!= null && !vpMap.isEmpty() && vpMap != null){
				if(vpMap.size()>=rpMap.size()){
					for(Map.Entry<String, voProduct> rpEntry : rpMap.entrySet()){
						voProduct vpBean = vpMap.get(rpEntry.getKey());
						if(vpBean != null){
							if(rpEntry.getValue().getCount()>vpBean.getCount()){
								flag = true;
								break;
							}
						}else flag = true;
					}
				}else flag = true;
			}
			return flag;
		}
	}
	
	/**
	 *	说明：1.为打开编辑异常入库单页面做准备
	 */
	@RequestMapping("/readyEditWarehousingAbnormal")
	@ResponseBody
	public EasyuiDataGridJson readyEditWarehousingAbnormal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		response.setContentType("text/html; charset=utf-8");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(755)){
			response.getWriter().write("{\"tip\":\"对不起,你没有编辑异常入库单权限!\",\"result\":\"failure\"}");
			return null;
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull((String)request.getParameter("abnormalId"));
		ResultSet rs = null;
		try {
			
			WarehousingAbnormalBean anormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(anormalBean == null){
				response.getWriter().write("{\"tip\":\"异常入库单不存在!\",\"result\":\"failure\"}");
				return null;
			}
			anormalBean.setStatusName(anormalBean.getStatusName(anormalBean.getStatus()));
			AuditPackageBean apBean = service.getAuditPackage(" order_id=" + anormalBean.getOrderId());
			List<voProduct> vpList = getVOProductList(anormalBean.getOrderId());
			List<voProduct> rpList = new ArrayList<voProduct>();
			String query = " select product_code,product_name,product_count,product_oriname,product_id from abnormal_real_products" +
					" where abnormal_id=" + anormalBean.getId();
			rs = service.getDbOp().executeQuery(query);
			while(rs.next()){
				voProduct bean = new voProduct();
				bean.setCode(rs.getString(1));
				bean.setName(rs.getString(2));
				bean.setCount(rs.getInt(3));
				bean.setOriname(rs.getString(4));
				bean.setId(rs.getInt(5));
				rpList.add(bean);
			}
			List fieldList = new ArrayList();
			Map fieldMap = new HashMap();
			fieldMap.put("field", "orderCodeName");
			fieldMap.put("title", "订单号");
			fieldMap.put("align", "center");
			fieldMap.put("width", "100");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "theOrderCode");
			fieldMap.put("title", apBean.getOrderCode());
			fieldMap.put("align", "center");
			fieldMap.put("width", "200");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "packageCodeName");
			fieldMap.put("title", "包裹单号");
			fieldMap.put("align", "center");
			fieldMap.put("width", "100");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "thePackageCode");
			fieldMap.put("title", apBean.getPackageCode() == null ? "" : apBean.getPackageCode());
			fieldMap.put("align", "center");
			fieldMap.put("width", "200");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "theDeliverName");
			fieldMap.put("title", "快递公司");
			fieldMap.put("align", "center");
			fieldMap.put("width", "100");
			fieldList.add(fieldMap);
			fieldMap = new HashMap();
			fieldMap.put("field", "theDeliver");
			fieldMap.put("title", voOrder.deliverMapAll.get(apBean.getDeliver()+""));
			fieldMap.put("align", "center");
			fieldMap.put("width", "200");
			fieldList.add(fieldMap);
			EasyuiDataGridJson vpJson = new EasyuiDataGridJson();
			vpJson.setTotal((long) vpList.size());
			vpJson.setRows(vpList);
			EasyuiDataGridJson rpJson = new EasyuiDataGridJson();
			rpJson.setTotal((long) rpList.size());
			rpJson.setRows(rpList);
			Map returnMap = new HashMap();
			returnMap.put("columns", fieldList);
			returnMap.put("rpJson", rpJson);
			returnMap.put("anormalBean", anormalBean);
			String canSubmit = "false";
			String canAudit = "false";
			if (anormalBean.getStatus() == WarehousingAbnormalBean.STATUS0 && group.isFlag(755)) {
				canSubmit = "true";
			}
			if (anormalBean.getStatus() == WarehousingAbnormalBean.STATUS1 && group.isFlag(858)) {
				canAudit = "true";
			}
			returnMap.put("canSubmit", canSubmit);
			returnMap.put("canAudit", canAudit);
			List footer = new ArrayList();
			footer.add(returnMap);
			vpJson.setFooter(footer);
			request.getSession().setAttribute("vpList", vpList);
			request.getSession().setAttribute("apBean", apBean);
			request.getSession().setAttribute("wareArea", anormalBean.getWareArea()+"");
			return vpJson;
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常 !\",\"result\":\"failure\"}");
			return null;
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
	}
	
	/**
	 *	说明：1.更新异常入库单状态为已提交
	 */
	@RequestMapping("/statusToSubmitted")
	@ResponseBody
	public String statusToSubmitted(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		response.setContentType("text/html; charset=utf-8");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(755)){
			request.setAttribute("msg", "对不起,你没有提交异常入库单权限!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull(request.getParameter("abnormalId"));
		try {
			//添加异常入库单是否存在及状态是否为已提交的判断
			WarehousingAbnormalBean abnormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(abnormalBean == null){
				response.getWriter().write("{\"tip\":\"此异常入库单不存在!\",\"result\":\"failure\"}");
				return null;
			}
			if (abnormalBean.getStatus() != WarehousingAbnormalBean.STATUS0) {
				response.getWriter().write("{\"tip\":\"此异常入库单状态不是未处理!\",\"result\":\"failure\"}");
				return null;
			}
			if (!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS1, " id=" + abnormalId)) {
				response.getWriter().write("{\"tip\":\"更新异常入库单转状态失败!\",\"result\":\"failure\"}");
				return null;
			} else {
				String canAudit = "false";
				if (group.isFlag(858)) {
					canAudit = "true";
				}
				response.getWriter().write("{\"tip\":\"成功！\",\"result\":\"success\",\"statusName\":\""+WarehousingAbnormalBean.statusMap.get(WarehousingAbnormalBean.STATUS1)+"\",\"canAudit\":\""+canAudit+"\"}");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常 !\",\"result\":\"failure\"}");
			return null;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 	说明：1.更新异常入库单
	 **/
	@RequestMapping("/updateWarehousingAbnormal")
	@ResponseBody
	public String updateWarehousingAbnormal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		response.setContentType("text/html; charset=utf-8");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(755)){
			request.setAttribute("msg", "对不起,你没有更新异常入库单权限!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, db);
		String abnormalId = StringUtil.convertNull(request.getParameter("abnormalId"));
		List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
		voProduct vp = new voProduct();
		String rp = request.getParameter("bat");
		List<voProduct> rpList = new ArrayList<voProduct>();
		String[] theArray = rp.split(",");
		for (int i = 0; i < theArray.length; i++) {
			switch (i%5) {
			case 0: vp.setId(StringUtil.toInt(theArray[i]));
				break;
			case 1: vp.setCount(StringUtil.toInt(theArray[i]));
				break;
			case 2: vp.setOriname(theArray[i]);
				break;
			case 3: vp.setCode(theArray[i]);
				break;
			case 4: vp.setName(theArray[i]); rpList.add(vp);vp = new voProduct();
				break;
			default:
				break;
			}
		}
		try {
			if(rpList.size()==0){
				response.getWriter().write("{\"tip\":\"由于没有实际退回商品,请走理赔单!\",\"result\":\"failure\"}");
				return null;
			}
			WarehousingAbnormalBean abnormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(abnormalBean == null){
				response.getWriter().write("{\"tip\":\"此异常入库单不存在!\",\"result\":\"failure\"}");
				return null;
			}
			if(isTimeout(request)){ //校验session里的内容是否超时
				response.getWriter().write("{\"tip\":\"操作超时,请重新操作!\",\"result\":\"failure\"}");
				return null;
			}
			if(rpList.size()==0){
				response.getWriter().write("{\"tip\":\"请输入实际退回商品!\",\"result\":\"failure\"}");
				return null;
			}
			if(!checkAbnormalOrCompensate(listTurnMap(vpList), listTurnMap(rpList))){
				response.getWriter().write("{\"tip\":\"不符合异常入库单条件,或请走理赔单!\",\"result\":\"failure\"}");
				return null;
			}
			service.getDbOp().startTransaction(); //开启事务
			if (!service.updateWarehousingAbnormal(" operator_id=" + user.getId() +",operator_name='" + user.getUsername()
					+"',create_time='" + DateUtil.getNow() + "' ", " id=" + abnormalBean.getId())) {
				service.getDbOp().rollbackTransaction();
				response.getWriter().write("{\"tip\":\"更新异常入库单失败!\",\"result\":\"failure\"}");
				return null;
			}
			abnormalBean = service.getWarehousingAbnormal("id=" + abnormalBean.getId());
			List<AbnormalRealProductBean> list = service.getAbnormalRealProductList("abnormal_id=" + abnormalId, -1, -1, null);
			if(list != null && list.size() > 0){
				if(!service.delAbnormalRealProduct("abnormal_id=" + abnormalId)){
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"删除实际退回商品失败 !\",\"result\":\"failure\"}");
					return null;
				}
			}
			for(voProduct bean : rpList){
				AbnormalRealProductBean arpBean = new AbnormalRealProductBean();
				arpBean.setProductCode(bean.getCode());
				arpBean.setProductCount(bean.getCount());
				arpBean.setProductId(bean.getId());
				arpBean.setProductName(bean.getName());
				arpBean.setProductOriname(bean.getOriname());
				arpBean.setAbnormalId(abnormalBean.getId());
				if(!service.addAbnormalRealProduct(arpBean)){
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"保存实际退回商品失败!\",\"result\":\"failure\"}");
					return null;
				}
			}	
			String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id = " + abnormalBean.getId();
			ResultSet rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				if (!packageLog.addReturnPackageLog("修改异常入库单", user, rs.getString(1))) {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"添加退货日志失败！\",\"result\":\"failure\"}");
					return null;
				}
			}
			service.getDbOp().commitTransaction(); //提交事务
			response.getWriter().write("{\"tip\":\"成功！\",\"result\":\"success\"}");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常 !\",\"result\":\"failure\"}");
			return null;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 *	说明：1.更新异常入库单状态为未处理
	 */
	@RequestMapping("/statusToUntreated")
	@ResponseBody
	public String statusToUntreated(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		response.setContentType("text/html; charset=utf-8");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(858)){
			request.setAttribute("msg", "对不起,你没有异常入库单审核权限!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull(request.getParameter("abnormalId"));
		try {
			//添加异常入库单是否存在及状态是否为已提交的判断
			WarehousingAbnormalBean abnormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(abnormalBean == null){
				response.getWriter().write("{\"tip\":\"此异常入库单不存在!\",\"result\":\"failure\"}");
				return null;
			}
			if (abnormalBean.getStatus() != WarehousingAbnormalBean.STATUS1) {
				response.getWriter().write("{\"tip\":\"此异常入库单状态不是已提交!\",\"result\":\"failure\"}");
				return null;
			}
			if (!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS0, " id=" + abnormalId)) {
				response.getWriter().write("{\"tip\":\"更新异常入库单转状态失败!\",\"result\":\"failure\"}");
				return null;
			} else {
				String canSubmit = "false";
				if (group.isFlag(755)) {
					canSubmit = "true";
				}
				response.getWriter().write("{\"tip\":\"成功！\",\"result\":\"success\",\"statusName\":\""+WarehousingAbnormalBean.statusMap.get(WarehousingAbnormalBean.STATUS0)+"\",\"canSubmit\":\""+canSubmit+"\"}");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常 !\",\"result\":\"failure\"}");
			return null;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 	说明：1.生成报损报溢单然后关联异常入库单
	 */
	@RequestMapping("/submitWarehousingAbnormal")
	@ResponseBody
	public String submitWarehousingAbnormal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		response.setContentType("text/html; charset=utf-8");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(858)){
			request.setAttribute("msg", "对不起,你没有异常入库单审核权限!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, db);
		int wareArea = StringUtil.toInt((String)request.getSession().getAttribute("wareArea"));
		int abnormalId = StringUtil.toInt((String)request.getParameter("abnormalId"));
		Map<String,voProduct> vpMap = listTurnMap((ArrayList<voProduct>)request.getSession().getAttribute("vpList"));
		voProduct vp = new voProduct();
		String rp = request.getParameter("bat");
		List<voProduct> rpList = new ArrayList<voProduct>();
		String[] theArray = rp.split(",");
		for (int i = 0; i < theArray.length; i++) {
			switch (i%5) {
			case 0: vp.setId(StringUtil.toInt(theArray[i]));
				break;
			case 1: vp.setCount(StringUtil.toInt(theArray[i]));
				break;
			case 2: vp.setOriname(theArray[i]);
				break;
			case 3: vp.setCode(theArray[i]);
				break;
			case 4: vp.setName(theArray[i]); rpList.add(vp);vp = new voProduct();
				break;
			default:
				break;
			}
		}
		Map<String,voProduct> rpMap = listTurnMap(rpList);
		int bsbyCount = 0;
		String bsbyResult = null;
		try {
			//添加异常入库单是否存在及状态是否为已提交的判断
			WarehousingAbnormalBean abnormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(abnormalBean == null){
				response.getWriter().write("{\"tip\":\"此异常入库单不存在!\",\"result\":\"failure\"}");
				return null;
			}
			if (abnormalBean.getStatus() != WarehousingAbnormalBean.STATUS1) {
				response.getWriter().write("{\"tip\":\"此异常入库单状态不是已提交!\",\"result\":\"failure\"}");
				return null;
			}
			CargoInfoBean ciBean = cargoService.getCargoInfo(" area_id=" + wareArea + " and store_type=2 and stock_type=4");
			String cargoCode = null;
			if(ciBean != null){
				cargoCode = ciBean.getWholeCode();
			}else{
				response.getWriter().write("{\"tip\":\"找不到退货库货位!\",\"result\":\"failure\"}");
				return null;
			}
			//校验session里的内容是否超时
			if(isTimeout(request)){ 
				response.getWriter().write("{\"tip\":\"操作超时,请重新操作!\",\"result\":\"failure\"}");
				return null;
			}
			 //校验是否复核异常入库单条件
			if(!checkAbnormalOrCompensate(vpMap, rpMap)){
				response.getWriter().write("{\"tip\":\"不符合异常入库条件,或请走理赔单!\",\"result\":\"failure\"}");
				return null;
			}
			//开启事务
			service.getDbOp().startTransaction();
			for(Map.Entry<String, voProduct> vpEntry : vpMap.entrySet()){
				if(rpMap.containsKey(vpEntry.getKey())){
					voProduct vpBean = vpEntry.getValue();
					voProduct rpBean = rpMap.get(vpEntry.getKey());
					if(vpBean.getCount()>rpBean.getCount()){
						int count = vpBean.getCount() - rpBean.getCount();
						//生成报损单
						int bsbyId = addBsByOperationnote(user, wareArea, 0, 4);
						if(bsbyId == -1){ 
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\"报溢单与异常入库单关联失败!\",\"result\":\"failure\"}");
							return null;
						}
						//向报损单中添加商品
						String result = addByBsProduct(user, vpBean.getCode(), cargoCode, count, bsbyId);
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
							return null;
						}
						//审核报损单
						result = auditBsByOperationnote(bsbyId);
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
							return null;
						}
						//报损单与异常入库单关联
						BsByAbnormalBean bean = new BsByAbnormalBean();
						bean.setAbnormalId(abnormalId);
						bean.setBsbyId(bsbyId);
						if(service.addBsByAbnormal(bean)){
							if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
								service.getDbOp().rollbackTransaction(); 
								response.getWriter().write("{\"tip\":\"更新异常入库单状态失败!\",\"result\":\"failure\"}");
								return null;
							}
						}else{
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\"报溢单与异常入库单关联失败!\",\"result\":\"failure\"}");
							return null;
						}
						bsbyCount ++;
					}else if(vpBean.getCount() < rpBean.getCount()){
						int count = rpBean.getCount() - vpBean.getCount();
						//生成报溢单
						int bsbyId = addBsByOperationnote(user, wareArea, 1, 4);
						if(bsbyId == -1){ 
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\"报溢单与异常入库单关联失败!\",\"result\":\"failure\"}");
							return null;
						}
						//向报溢单中添加商品
						String result = addByBsProduct(user, vpBean.getCode(), cargoCode, count, bsbyId);	
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
							return null;
						}
						//审核报溢单
						result = auditBsByOperationnote(bsbyId);
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
							return null;
						}
						//报溢单与异常入库单关联
						BsByAbnormalBean bean = new BsByAbnormalBean();
						bean.setAbnormalId(abnormalId);
						bean.setBsbyId(bsbyId);
						if(service.addBsByAbnormal(bean)){
							if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
								service.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"tip\":\"更新异常入库单状态失败!\",\"result\":\"failure\"}");
								return null;
							}
						}else{
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\"报溢单与异常入库单关联失败!\",\"result\":\"failure\"}");
							return null;
						}
						bsbyCount ++;
					}
				}else{//实际退回商品列表中没有这种商品 所以报损
					voProduct vpBean = vpEntry.getValue();
					//报损数量
					int count = vpBean.getCount();
					//1生成报损单
					int bsbyId = addBsByOperationnote(user, wareArea, 0, 4);
					if(bsbyId == -1){ 
						service.getDbOp().rollbackTransaction(); 
						response.getWriter().write("{\"tip\":\"报损单与异常入库单关联失败!\",\"result\":\"failure\"}");
						return null;
					}
					//向报损单中添加商品
					String result = addByBsProduct(user, vpBean.getCode(), cargoCode, count, bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); 
						response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
						return null;
					}
					//审核报溢单
					result = auditBsByOperationnote(bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); 
						response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
						return null;
					}
					//报损单与异常入库单关联
					BsByAbnormalBean bean = new BsByAbnormalBean();
					bean.setAbnormalId(abnormalId);
					bean.setBsbyId(bsbyId);
					if(service.addBsByAbnormal(bean)){
						if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\"更新异常入库单状态失败!\",\"result\":\"failure\"}");
							return null;
						}
					}else{
						service.getDbOp().rollbackTransaction(); 
						response.getWriter().write("{\"tip\":\"报溢单与异常入库单关联失败!\",\"result\":\"failure\"}");
						return null;
					}
					bsbyCount ++;
				}
			}
			for(Map.Entry<String, voProduct> rpEntry : rpMap.entrySet()){
				//总商品列表中没有实际退回商品列表中的这个商品 所以报溢
				if(!vpMap.containsKey(rpEntry.getKey())){
					//报溢数量
					voProduct rpBean = rpEntry.getValue();
					int count = rpBean.getCount();
					//1生成报溢单
					int bsbyId = addBsByOperationnote(user, wareArea, 1, 4);
					if(bsbyId == -1){ //bsbyId为-1说明生成报损报溢单失败!
						service.getDbOp().rollbackTransaction(); //事务回滚
						response.getWriter().write("{\"tip\":\"报溢单与异常入库单关联失败!\",\"result\":\"failure\"}");
						return null;
					}
					//向报溢单中添加商品
					String result = addByBsProduct(user,rpBean.getCode(), cargoCode, count, bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); //事务回滚
						response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
						return null;
					}
					//审核报溢单
					result = auditBsByOperationnote(bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); 
						response.getWriter().write("{\"tip\":\""+result+"\",\"result\":\"failure\"}");
						return null;
					}
					//报溢单与异常入库单关联
					BsByAbnormalBean bean = new BsByAbnormalBean();
					bean.setAbnormalId(abnormalId);
					bean.setBsbyId(bsbyId);
					if(service.addBsByAbnormal(bean)){
						if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
							service.getDbOp().rollbackTransaction(); 
							response.getWriter().write("{\"tip\":\"更新异常入库单状态失败!\",\"result\":\"failure\"}");
							return null;
						}
					}else{
						service.getDbOp().rollbackTransaction(); 
						response.getWriter().write("{\"tip\":\"报溢单与异常入库单关联失败!\",\"result\":\"failure\"}");
						return null;
					}
					bsbyCount ++;
				}
			}
			String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id = " + abnormalId + "";
			ResultSet rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				if (!packageLog.addReturnPackageLog("提交异常入库单", user, rs.getString(1))) {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"添加退货日志失败！\",\"result\":\"failure\"}");
					return null;
				}
			}
			bsbyResult = "成功生成" + bsbyCount + "张报损报溢单！";
			service.getDbOp().commitTransaction(); //提交事务
			response.getWriter().write("{\"tip\":\"成功！\",\"result\":\"success\",\"statusName\":\""+WarehousingAbnormalBean.statusMap.get(WarehousingAbnormalBean.STATUS2)+"\",\"bsbyResult\":\""+bsbyResult+"\"}");
			return null;
		} catch (Exception e) {
			service.getDbOp().rollbackTransaction(); //事务回滚
			e.printStackTrace();
			response.getWriter().write("{\"tip\":\"程序异常!\",\"result\":\"failure\"}");
			return null;
		} finally {
			service.releaseAll();
			cargoService.releaseAll();
		}
	}
	
	/**
	 *	说明：1.审核报损报溢单
	 *	日期：2013-4-23
	 *	作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	public String auditBsByOperationnote(int bsbyId) throws Exception {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, null);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String result = "success";
		try {
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + bsbyId);
			if(bean == null){
				result = "报损报溢单不存在!";
				return result;
			}
			//报损单中的所有产品
			List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
			Iterator it = bsbyList.iterator();
			service.getDbOp().startTransaction();
			if(bean.getType() == 0){
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						result = "货位信息异常,操作失败,请与管理员联系!";
						return result;
					}
					String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
					+ "area = " + bean.getWarehouse_area() + " and type = "
					+ bean.getWarehouse_type();
					ProductStockBean psBean = psService.getProductStock(sql);
					//减少库存
					if(!psService.updateProductStockCount(psBean.getId(), -bsbyProductBean.getBsby_count())){
						result = "库存操作失败,可能是库存不足,请与管理员联系!";
						return result;
					}
					//增加库存锁定量
					if (!psService.updateProductLockCount(psBean.getId(), bsbyProductBean.getBsby_count())) {
						result = "库存操作失败,可能是库存不足,请与管理员联系!";
						return result;
					}

					//锁定货位库存
					//出库
					if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
						service.getDbOp().rollbackTransaction();
						result = "货位库存操作失败,货位库存不足!";
						return result;
					}
					if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
						service.getDbOp().rollbackTransaction();
						result = "货位库存操作失败，货位库存不足!";
						return result;
					}
				}
			}else if(bean.getType() == 1){
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						result = "货位信息异常,操作失败,请与管理员联系!";
						return result;
					}

					//锁定货位空间
					if(cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId()+" and status = 0")==null){
						result = "目的货位不存在或已被清空，操作失败，请与管理员联系!";
						return result;
						
					}
					if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count + "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
						service.getDbOp().rollbackTransaction();
						result = "操作失败!";
						return result;
					}
				}
			}
			if(!service.updateBsbyOperationnoteBean(" current_type=1", " id=" + bsbyId)){
				result = "更新报损报溢单状态是失败!";
				return result;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			result = "程序异常!";
			return result;
		} finally {
			service.releaseAll();
		}
		return result;
	}
	
	/**
	 *	说明：1.生成报损报溢单 返回一个报损报溢单id
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public int addBsByOperationnote(voUser user,int wareArea,
			int bsbyType,int wareType)throws Exception {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, null);
		String receipts_number = "";
		String title = "";// 日志的内容
		int typeString = 0;
		int bsbyId = -1;
		if (bsbyType == 0) {
			// 报损
			String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code);// BS+年月日+3位自动增长数
			title = "创建新的报损表" + receipts_number;
			typeString = 0;
		} else {
			String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code);// BY+年月日+3位自动增长数
			title = "创建新的报溢表" + receipts_number;
			typeString = 1;
		}
		String nowTime = DateUtil.getNow();
		BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
		bsbyOperationnoteBean.setAdd_time(nowTime);
		bsbyOperationnoteBean.setCurrent_type(0);
		bsbyOperationnoteBean.setOperator_id(user.getId());
		bsbyOperationnoteBean.setOperator_name(user.getUsername());
		bsbyOperationnoteBean.setReceipts_number(receipts_number);
		bsbyOperationnoteBean.setWarehouse_area(wareArea);
		bsbyOperationnoteBean.setWarehouse_type(wareType);
		bsbyOperationnoteBean.setType(typeString);
		bsbyOperationnoteBean.setIf_del(0);
		bsbyOperationnoteBean.setFinAuditId(0);
		bsbyOperationnoteBean.setFinAuditName("");
		bsbyOperationnoteBean.setFinAuditRemark("");
		bsbyOperationnoteBean.setRemark("异常入库单");
		try {
			service.getDbOp().startTransaction(); //开启事务
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
			bsbyOperationnoteBean.setId(maxid + 1);
			if (service.addBsbyOperationnoteBean(bsbyOperationnoteBean)) {
				bsbyId = Integer.valueOf(bsbyOperationnoteBean.getId());
				// 添加操作日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(nowTime);
				bsbyOperationRecordBean.setInformation(title);
				bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean)){
					service.getDbOp().rollbackTransaction();//事务回滚
				}
			}else{
				service.getDbOp().rollbackTransaction(); //事务回滚
			}
			service.getDbOp().commitTransaction(); //提交事务
		} catch (Exception e) {
			service.getDbOp().rollbackTransaction(); //事务回滚
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return bsbyId;
	}
	
	/**
	 * 	说明：1.向报损报溢单中添加商品 返回一个字符串判断是否成功及记录错误信息
	 * 	日期：2013-4-15
	 * 	作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	public String addByBsProduct(voUser user, String productCode,String cargoCode,
			int count,int bsbyId)throws Exception {
		String result = null;
		WareService wareService = new WareService();
		voProduct product = wareService.getProduct(productCode);
		if (product == null) {
			result = "[" + productCode+ "]不存在这个编号的产品!";
			return result;
		}
		if (product.getParentId1() == 106) {
			result = "[" + productCode+ "]该商品为新商品,请先修改该产品的分类";
			return result;
		}
		if (product.getIsPackage() == 1) {
			result = "[" + productCode+ "]该产品为套装产品,不能添加!";
			return result;
		}
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try {
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + bsbyId);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail){
				result =  "单据已提交审核,无法修改!";
				return result;
			}
			
			if (service.getBsbyProductBean("operation_id = " + bsbyId + " and product_code = " + productCode) != null) {
				result = "[" + productCode+ "]该产品已经添加,直接修改即可,不用重复添加!";
				return result;
			}
			if (service.getBsbyProductBean("operation_id = " + bsbyId ) != null) {
				result = "只能添加一个商品 所以不能重复提交!";
				return result;
			}
			BsbyOperationnoteBean ben = service.getBsbyOperationnoteBean("id=" + bsbyId);
			int x = getProductCount(product.getId(), ben.getWarehouse_area(), ben.getWarehouse_type());
			int n = updateProductCount(x, ben.getType(), count);
			if (n < 0 ) {
				result = "您所添加商品的库存不足!";
				return result;
			}
			//新货位管理判断
			CargoProductStockBean cps = null;
			if(ben.getType()==0){
		        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
		        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+ben.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	result = "货位号"+cargoCode+"无效,请重新输入!";
		            return result;
		        }
		        cps = (CargoProductStockBean)cpsOutList.get(0);
		        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cps.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	result = "合格库缓存区暂时不能进行报损报溢操作!";
		            return result;
		        }
		        if(count > cps.getStockCount()){
		        	result = "该货位"+cargoCode+"库存为" + cps.getStockCount() + ",库存不足!";
		            return result;
		        }
			}else{
				CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
				CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ben.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+cargoCode+"' and status <> "+CargoInfoBean.STATUS3);
		        if(cargo == null){
		        	result = "货位号"+cargoCode+"无效,请重新输入!";
		            return result;
		        }
		        if(cargo.getStatus() == CargoInfoBean.STATUS2){
		        	result = "货位"+cargoCode+"未开通,请重新输入!";
		            return result;
		        }
		        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cargo.getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	result = "合格库缓存区暂时不能进行报损报溢操作!";
		            return result;
		        }
				List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	if(cargo.getStatus() == CargoInfoBean.STATUS0 && (cargo.getStoreType() == CargoInfoBean.STORE_TYPE0||cargo.getStoreType() == CargoInfoBean.STORE_TYPE4)){
		        		result = "货位"+cargoCode+"被其他商品使用中,添加失败!";
			            return result;
		        	}
		        	cps = new CargoProductStockBean();
		        	cps.setCargoId(cargo.getId());
		        	cps.setProductId(product.getId());
		        	cps.setStockCount(0);
		        	cps.setStockLockCount(0);
		        	service.getDbOp().startTransaction(); //开启事务 需要连接检查
		        	if(!cargoService.addCargoProductStock(cps)){
		        		service.getDbOp().rollbackTransaction(); //事务回滚
		        		result = "生成报损报溢单数据库异常!(cargoService.addCargoProductStock(cps))";
						return result;
		        	}
		        	cps.setId(cargoService.getDbOp().getLastInsertId());
		        	if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId())){
		        		service.getDbOp().rollbackTransaction(); //事务回滚
		        		result = "生成报损报溢单数据库异常!(cargoService.updateCargoInfo())";
						return result;
		        	}
		        }else{
		        	cps = (CargoProductStockBean)cpsOutList.get(0);
		        }
			}
	        

			BsbyProductBean bsbyProductBean = new BsbyProductBean();
			bsbyProductBean.setBsby_count(count);
			bsbyProductBean.setOperation_id(bsbyId);
			bsbyProductBean.setProduct_code(productCode);
			bsbyProductBean.setProduct_id(product.getId());
			bsbyProductBean.setProduct_name(product.getName());
			bsbyProductBean.setOriname(product.getOriname());
			bsbyProductBean.setAfter_change(n);
			bsbyProductBean.setBefore_change(x);
			if(service.addBsbyProduct(bsbyProductBean)) {
				result = "success";
			}else{
				service.getDbOp().rollbackTransaction(); //事务回滚
				result = "商品添加失败!";
				return result;
			}
			BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
			bsbyCargo.setBsbyOperId(ben.getId());
			bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
			bsbyCargo.setCount(count);
			bsbyCargo.setCargoProductStockId(cps.getId());
			bsbyCargo.setCargoId(cps.getCargoId());
			service.addBsbyProductCargo(bsbyCargo);
			// 添加日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(user.getId());
			bsbyOperationRecordBean.setOperator_name(user.getUsername());
			bsbyOperationRecordBean.setTime(DateUtil.getNow());
			bsbyOperationRecordBean.setInformation("给单据(id):" + bsbyId+ "添加商品:" + productCode + "数量：" + count);
			bsbyOperationRecordBean.setOperation_id(bsbyId);
			if(!service.addBsbyOperationRecord(bsbyOperationRecordBean)){
				service.getDbOp().rollbackTransaction(); //事务回滚
				result = "日志添加失败!";
				return result;
			}
			service.getDbOp().commitTransaction(); //提交事务
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
			wareService.releaseAll();
			cargoService.releaseAll();
		}
		return result;
	}
	
	/**
	 *	说明：1.生成报损报溢单号
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public static String createCode(String code) {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0 and receipts_number like '" + code + "%'");
			BsbyOperationnoteBean plan;
			plan = service.getBuycode("receipts_number like '" + code + "%'");
			if (plan == null) {
				// 当日第一份计划,编号最后三位 001
				code += "0001";
			} else {
				// 获取当日计划编号最大值
				plan = service.getBuycode("id =" + maxid);
				String _code = plan.getReceipts_number();
				int number = Integer.parseInt(_code.substring(_code.length() - 4));
				number++;
				code += String.format("%04d", new Object[] { new Integer(number) });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

		return code;
	}
	
	/**
	 * 	说明：1.根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 
	 * 	日期：2013-04-17
	 * 	作者：石远飞
	 */
	public static int getProductCount(int productid, int area, int type) {
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		int x = 0;
		try {
			voProduct product = wareService.getProduct(productid);
			product.setPsList(psService.getProductStockList("product_id=" + productid, -1, -1, null));
			x = product.getStock(area, type);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return x;

	}
	/**
	 * 	说明：1.得到报损或者报溢后的产品的数量 
	 * 	日期：2013-4-15
	 * 	作者：石远飞
	 */
	public static int updateProductCount(int x, int type, int count) {
		int result = 0;
		if (type == 0) {
			// 报损
			result = x - count;
		} else {
			result = x + count;
		}
		return result;
	}
	
	/**	
	 *  说明：1.删除异常入库单
	 */
	@RequestMapping("/delWarehousingAbnormal")
	@ResponseBody
	public String delWarehousingAbnormal(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		response.setContentType("text/html; charset=utf-8");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(755)){
			request.setAttribute("msg", "对不起,你没有删除异常入库单权限!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String abnormalId = StringUtil.convertNull((String)request.getParameter("abnormalId"));
		try {
			//添加异常入库单是否存在及状态是否为已提交的判断
			WarehousingAbnormalBean abnormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(abnormalBean == null){
				response.getWriter().write("{\"tip\":\"此异常入库单不存在!\",\"result\":\"failure\"}");
				return null;
			}
			if (abnormalBean.getStatus() != WarehousingAbnormalBean.STATUS0) {
				response.getWriter().write("{\"tip\":\"此异常入库单状态不是未处理!\",\"result\":\"failure\"}");
				return null;
			}
			service.getDbOp().startTransaction();//开启事务
			String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id = " + abnormalId ;
			ResultSet rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				if (!packageLog.addReturnPackageLog("删除异常入库单", user, rs.getString(1))) {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"添加退货日志失败！\",\"result\":\"failure\"}");
					return null;
				}
			}
			if(!service.delWarehousingAbnormal("id=" + abnormalId)){
				service.getDbOp().rollbackTransaction();
				response.getWriter().write("{\"tip\":\"删除异常入库单失败 !\",\"result\":\"failure\"}");
				return null;
			}
			List<AbnormalRealProductBean> list = service.getAbnormalRealProductList("abnormal_id=" + abnormalId, -1, -1, null);
			if(list != null && list.size() > 0){
				if(!service.delAbnormalRealProduct("abnormal_id=" + abnormalId)){
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"tip\":\"删除实际退回商品失败 !\",\"result\":\"failure\"}");
					return null;
				}
			}
			service.getDbOp().commitTransaction();//提交事务
			response.getWriter().write("{\"tip\":\"删除成功！\",\"result\":\"success\"}");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			response.getWriter().write("{\"tip\":\"程序异常 !\",\"result\":\"failure\"}");
			return null;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 	说明：1.打印异常入库单详细信息
	 **/
	@RequestMapping("/printWarehousingAbnormal")
	public String printWarehousingAbnormal( HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(753) && !group.isFlag(754) && !group.isFlag(755)){
			request.setAttribute("msg", "对不起,你没有打印异常入库单权限!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull((String)request.getParameter("abnormalId"));
		ResultSet rs = null;
		try {
			WarehousingAbnormalBean anormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(anormalBean == null){
				request.setAttribute("msg", "异常入库单不存在!");
				request.getRequestDispatcher(noSession).forward(request, response);
				return null;
			}
			List<voProduct> vpList = getVOProductList(anormalBean.getOrderId());
			List<voProduct>	rpList = new ArrayList<voProduct>();
			String query = " select product_code,product_name,product_count,product_oriname,product_id from abnormal_real_products" +
						" where abnormal_id=" + anormalBean.getId();
			rs = service.getDbOp().executeQuery(query);
			while(rs.next()){
				voProduct bean = new voProduct();
				bean.setCode(rs.getString(1));
				bean.setName(rs.getString(2));
				bean.setCount(rs.getInt(3));
				bean.setOriname(rs.getString(4));
				bean.setId(rs.getInt(5));
				rpList.add(bean);
			}
			AuditPackageBean apBean = service.getAuditPackage(" order_id=" + anormalBean.getOrderId());
			query = "select distinct wa.code,wa.create_time,wa.status,ap.order_code,ap.package_code,ap.deliver,ap.sorting_datetime," +
					"wa.operator_name,wa.id from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id=" + abnormalId ;
			rs = service.getDbOp().executeQuery(query);
			WarehousingAbnormalBean abnormalBean = new WarehousingAbnormalBean();
			if (rs.next()) {
				abnormalBean.setCode(rs.getString(1));
				abnormalBean.setCreateTime(rs.getString(2));
				abnormalBean.setStatus(rs.getInt(3));
				abnormalBean.setOrderCode(rs.getString(4));
				abnormalBean.setPackageCode(rs.getString(5));
				abnormalBean.setDeliver(rs.getInt(6));
				abnormalBean.setSortingDatetime(rs.getString(7));
				abnormalBean.setOperatorName(rs.getString(8));
				abnormalBean.setId(rs.getInt(9));
			}
			List<WarehousingAbnormalBean> bsbyList = new ArrayList<WarehousingAbnormalBean>();
			query ="select bba.abnormal_id,bbo.receipts_number,bbo.type,bbo.id,bbo.current_type,bbo.operator_id " +
					"from bsby_abnormal bba left join bsby_operationnote bbo on bba.bsby_id = bbo.id " +
					" where bba.abnormal_id=" + abnormalBean.getId();
			rs = service.getDbOp().executeQuery(query);
			while (rs.next()) {
				WarehousingAbnormalBean bsbyBean = new WarehousingAbnormalBean();
				bsbyBean.setId(rs.getInt(1));
				bsbyBean.setReceiptsNumber(rs.getString(2));
				bsbyBean.setBsbyType(rs.getInt(3));
				bsbyBean.setBsbyId(rs.getInt(4));
				bsbyBean.setBsbyStatus(rs.getInt(5));
				bsbyBean.setBsbyOperatorId(rs.getInt(6));
				bsbyList.add(bsbyBean);
			}
			request.setAttribute("abnormalBean", abnormalBean);
			request.setAttribute("bsbyList", bsbyList);
			request.setAttribute("vpList", vpList);
			request.setAttribute("rpList", rpList);
			request.setAttribute("apBean", apBean);
			return "/admin/rec/oper/salesReturned/printWarehousingAbnormal";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "程序异常 !");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
	}
	
	/**
	 * 理赔核销页面的“状态”
	 * 
	 */
	@RequestMapping("/getCalimsVeriStatusName")
	@ResponseBody
	public String getCalimsVeriStatusName(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<Integer, String> map = ClaimsVerificationBean.getAllStatusName();
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("" + -1);
		bean.setText("请选择");
		bean.setSelected(true);
		list.add(bean);
		for(Map.Entry<Integer, String> entry : map.entrySet()){
			bean = new EasyuiComBoBoxBean();
			bean.setId("" + entry.getKey());
			bean.setText(entry.getValue());
			list.add(bean);
		}
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 * 
	 * 查询理赔核销单
	 */
	@RequestMapping("/getClaimsVerificationInfo")
	@ResponseBody
	public EasyuiDataGridJson getClaimsVerificationInfo( HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(745) ) {
			request.setAttribute("msg", "您没有理赔核销权限！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		String claimsCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("claimsCode")));
		String createTime1 = StringUtil.toSql(StringUtil.convertNull(request.getParameter("createTime1")));
		String createTime2 = StringUtil.toSql(StringUtil.convertNull(request.getParameter("createTime2")));
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		String orderCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("orderCode")));
		String packageCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("packageCode")));
		int deliver = ProductWarePropertyService.toInt(request.getParameter("deliver"));
		int status = ProductWarePropertyService.toInt(request.getParameter("status"));
		int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
		String bsCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("bsCode")));
		
		List claimsVerificationList = new ArrayList();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			
			if( productCode != null && !productCode.equals("")) {
				sql.append("select cv.* from claims_verification cv, claims_verification_product cvp where cvp.claims_verification_id = cv.id");
				sqlCount.append("select count(cv.id) from claims_verification cv, claims_verification_product cvp where cvp.claims_verification_id = cv.id");
			} else {
				sql.append("select cv.* from claims_verification cv where cv.id <> 0");
				sqlCount.append("select count(cv.id) from claims_verification cv where cv.id <> 0");
			}
			
			if( !claimsCode.equals("") ) {
			    sql.append(" and cv.code = '" + claimsCode + "'");
			    sqlCount.append(" and cv.code = '" + claimsCode + "'");
			}
			
			if( !bsCode.equals("") ) {
				sql.append(" and cv.bsby_codes like '%" + bsCode + "%'");
				sqlCount.append(" and cv.bsby_codes like '%" + bsCode + "%'");
			}
			
			if( wareArea != -1 ) {
			    sql.append(" and cv.ware_area = '" + wareArea + "'");
			    sqlCount.append(" and cv.ware_area = '" + wareArea + "'");
			}
			
			if( !orderCode.equals("") ) {
				sql.append(" and cv.order_code = '" + orderCode + "'");
				sqlCount.append(" and cv.order_code = '" + orderCode + "'");
			}
			
			if( !packageCode.equals("") ) {
				sql.append(" and cv.package_code = '" + packageCode + "'");
				sqlCount.append(" and cv.package_code = '" + packageCode + "'");
			}
			
			if( !createTime1.equals("") && !createTime2.equals("")) {
				String createTimeStart = createTime1 + " 00:00:00";
				String createTimeEnd = createTime2 + " 23:59:59";
	        	sql.append(" and").append(" cv.create_time between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
	        	sqlCount.append(" and").append(" cv.create_time between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
			}
 			
			if( deliver != -1 ) {
				sql.append(" and cv.deliver = " + deliver);
				sqlCount.append(" and cv.deliver = " + deliver);
			}
			
			if( status != -1 ) {
				sql.append(" and cv.status = " + status);
				sqlCount.append(" and cv.status = " + status);
			}
			
			if ( !productCode.equals("")) {
				sql.append(" and cvp.product_code = '" + productCode + "'");
				sqlCount.append(" and cvp.product_code = '" + productCode + "'");
			}
			String isAdd;
			if (group.isFlag(746)) {
				isAdd = "true";
			} else {
				isAdd = "false";
			}
			int totalCount = claimsVerificationService.getClaimsVerificationCount2(sqlCount.toString());
			claimsVerificationList = claimsVerificationService.getClaimsVerificationList2(sql.toString(), (easyuiDataGrid.getPage()-1)*easyuiDataGrid.getRows(), easyuiDataGrid.getRows(), "cv.create_time desc");
			int x = claimsVerificationList.size();
			for ( int i = 0; i < x; i++) {
				StringBuffer receiptsNumber = new StringBuffer();
				StringBuffer bsbyId = new StringBuffer();
				StringBuffer lookup = new StringBuffer();
				StringBuffer ifDel = new StringBuffer();
				ClaimsVerificationBean cvBean = (ClaimsVerificationBean) claimsVerificationList.get(i);
				//添加上对应的理赔单的信息的加入
				List<BsbyOperationnoteBean> list = new ArrayList<BsbyOperationnoteBean>();
				String bsbyCodes = cvBean.getBsbyCodes();
				if( bsbyCodes != null && !bsbyCodes.equals("")) {
					String [] codes = bsbyCodes.split(",");
					if( codes != null && codes.length > 0 ) {
						for( int j = 0; j < codes.length; j ++ ) {
							String code = codes[j];
							BsbyOperationnoteBean bsbyBean = bsbyService.getBsbyOperationnoteBean("receipts_number='" + code + "'");
							if(bsbyBean != null ) {
								int type = bsbyBean.getCurrent_type();
				  		    	if (receiptsNumber.length() > 0) {
				  		    		receiptsNumber.append(",");
				  		    		bsbyId.append(",");
				  		    		lookup.append(",");
				  		    		ifDel.append(",");
				  		    	}
				  		    	receiptsNumber.append(bsbyBean.getReceipts_number());
				  		    	bsbyId.append(bsbyBean.getId());
				  		    	if (bsbyBean.getIf_del() == 1) {
				  		    		ifDel.append("1");
				  		    	} else {
				  		    		ifDel.append("0");
				  		    	}
								if((type==0||type==1||type==2||type==5)&&(user.getId()==bsbyBean.getOperator_id()||group.isFlag(413))){
									lookup.append("0");
								}else if(type==6&&(user.getId()==bsbyBean.getOperator_id()||group.isFlag(229))){
									lookup.append("0");
								}else {
									lookup.append("1");
								}
							}
						}
					}
				}
				AuditPackageBean apBean = istockService
				.getAuditPackage("order_code='" + cvBean.getOrderCode() + "'");
				if( apBean == null ) {
					cvBean.setDeliverDate("");
				} else {
					if(apBean.getCheckDatetime() == null || apBean.getCheckDatetime().equals("")) {
						cvBean.setDeliverDate("");
						
					} else {
						cvBean.setDeliverDate(apBean.getCheckDatetime().substring(0, 11));
					}
				}
				cvBean.setReceiptsNumberString(receiptsNumber.toString());
				cvBean.setBsbyIdString(bsbyId.toString());
				cvBean.setLookupString(lookup.toString());
				cvBean.setIfDelString(ifDel.toString());
				cvBean.setDeliverCompanyName((String)voOrder.deliverMapAll.get(""+cvBean.getDeliver()));
				cvBean.setIsAdd(isAdd);
				cvBean.setWareAreaName((String)ProductStockBean.areaMap.get(new Integer(cvBean.getWareArea())));
			}
			
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long)totalCount);
			easyuiDataGridJson.setRows(claimsVerificationList);
			return easyuiDataGridJson;
		} catch(Exception e ) {
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 
	 * 根据订单号获得订单商品信息
	 * @return
	 */
	@RequestMapping("/getOrderInfoByCode")
	@ResponseBody
	public void getOrderInfoByCode( HttpServletRequest request, HttpServletResponse response) throws IOException {
	
		String orderCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("addOrderCode")));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null){
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有登录不可查询!\"}");
				return;
			}
			if( orderCode.equals("")) {
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"传入的订单号为空！\"}");
				return;
			}
			
			// 判断订单编号是否存在,首先按照order_code查询，然后按照code查询
			voOrder vorder = null;
			OrderStockBean orderStockBean = null;
			AuditPackageBean auditPackageBean = istockService
			.getAuditPackage("package_code='" + orderCode + "'");

			if( auditPackageBean != null ) {
				vorder = wareService.getOrder("code='" + auditPackageBean.getOrderCode()+"'");
				if( vorder == null ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有根据包裹单号找到订单信息！\"}");
					return;
				} else {
					orderStockBean = istockService.getOrderStock("order_id="+vorder.getId()+" and status!="+OrderStockBean.STATUS4);
					if( orderStockBean == null ) {
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到出库单信息！\"}");
						return;
					}
				}
			} else {
				orderStockBean = istockService.getOrderStock("code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
				if(orderStockBean != null){
					vorder = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
				} else {
					vorder = wareService.getOrder("code='"+orderCode+"'");
					if(vorder == null){
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到订单信息！\"}");
						return;
					} else {
						orderStockBean = istockService.getOrderStock("order_id="+vorder.getId()+" and status!="+OrderStockBean.STATUS4);
					}
				}
			}
			
			
			
			if (vorder == null ) {
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到完整订单信息!\"}");
				return;
			} else {
				//List list = istockService.getOrderStockProductList("order_stock_id="+orderStockBean.getId(), -1, -1, "id asc");
				if(vorder.getStatus() != 11 ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前订单不是已退回状态，不能操作！！\"}");
					return;
				}
				//得到该订单的所有出库商品...
				List<OrderStockProductBean> list = claimsVerificationService.getAllProductsByOrder(vorder.getId());
				Map<Integer,OrderStockProductBean> map = claimsVerificationService.getMergMap(list);
				list =  claimsVerificationService.mergeOrderStockProducts(map);
				request.getSession().setAttribute("OrderStockProductsMap_" + vorder.getId(), map);
				if( list != null && list.size() != 0 ) {
					List productList = new ArrayList();
					int x = list.size();
					String result = "{status:'success', order_id:'" + vorder.getId() + "', order_stock_id:'', productList:[";
					for( int i = 0; i < x; i++ ) {
						OrderStockProductBean ospBean = list.get(i);
						voProduct product = wareService.getProduct(ospBean.getProductId());
						ProductBarcodeVO bBean= bService.getProductBarcode("product_id="+ospBean.getProductId()+" and barcode_status=0");
						if (bBean != null) {
							product.setBarcode(bBean.getBarcode());
						} else {
							product.setBarcode("");
						}
						product.setStockoutCount(ospBean.getStockoutCount());
						product.setAddCount(0);
						productList.add(product);
					}
					Map resultMap = new HashMap();
					resultMap.put("result", "success");
					resultMap.put("orderId", vorder.getId());
					resultMap.put("orderStockId", "");
					resultMap.put("rows", productList);
					response.getWriter().write(JSONObject.fromObject(resultMap).toString());
					return;
				} else {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到订单商品信息！\"}");
					return;
				}
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"异常！\"}");
			return;
		} finally {
			statService.releaseAll();
		}
		
	}
	
	/**
	 * 
	 * 核准验证 添加理赔核销单 商品
	 * @return
	 */
	@RequestMapping("/checkClaimsVerificationProduct")
	@ResponseBody
	public void checkClaimsVerificationProduct(HttpServletRequest request, HttpServletResponse response) {
		
		String products = StringUtil.convertNull(request.getParameter("products"));
		String currentProducts = StringUtil.convertNull(request.getParameter("currentProducts"));
		int orderId = StringUtil.parstInt(request.getParameter("orderId"));
		int orderStockId = StringUtil.parstInt(request.getParameter("orderStockId"));
		
		voOrder vorder = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List productsList = new ArrayList();
		List currentProductsList = new ArrayList();
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null) {
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有登录不可查询！\"}");
				return;
			}
			if( products == null || "".equals(products)) {
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有选择任何商品！\"}");
				return;
			}
			String[] productsString = products.split(",");
			Map productsMap = new HashMap();
			for (int i = 0; i < productsString.length; i++) {
				switch (i%2) {
				case 0: productsMap.put("id", productsString[i]);
					break;
				case 1: productsMap.put("count", productsString[i]);
						productsMap.put("isExist", "1");
						productsList.add(productsMap);
						productsMap=new HashMap();
					break;
				default:
					break;
				}
			}
			if( currentProducts != null && !"".equals(currentProducts) ) {
				String[] currentProductsString = currentProducts.split(",");
				Map currentProductsMap = new HashMap();
				for (int i = 0; i < currentProductsString.length; i++) {
					switch (i%3) {
					case 0: currentProductsMap.put("id", currentProductsString[i]);
						break;
					case 1: currentProductsMap.put("count", currentProductsString[i]);
						break;
					case 2: currentProductsMap.put("isExist", currentProductsString[i]);
							currentProductsList.add(currentProductsMap);
							currentProductsMap=new HashMap();
						break;
					default:
						break;
					}
				}
				for ( int i = 0 ; i < currentProductsList.size(); i++ ) {
					String temp = (String)((Map)currentProductsList.get(i)).get("id");
					for( int j = 0; j < productsList.size(); j ++ ) {
						if( temp.equals((String)((Map)productsList.get(j)).get("id"))) {
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"勾选商品有已经添加过的！\"}");
							return;
						} 
					}
				}
			}
			productsList.addAll(currentProductsList);
			
			if ( orderId != 0 ){
				vorder = wareService.getOrder("id="+orderId);
				if( vorder == null ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到订单信息！\"}");
					return;
				}
				if(vorder.getStatus() != 11 ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前订单不是已退回状态，不能操作！\"}");
					return;
				}
			}
			//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
			ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + "," + ClaimsVerificationBean.CLAIMS_COMPLETE + ")" );
			if( cvBean2 != null ) {
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前订单已经有已提交或已审核的理赔核销单了！\"}");
				return;
			}
			String packageCode = "";
			List auditPackageList = istockService
			.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
			if( auditPackageList == null || auditPackageList.size() == 0 ) {
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到对应的包裹单信息！\"}");
				return;
			} else {
				for( int i = 0; i < auditPackageList.size(); i ++ ) {
					AuditPackageBean apBean = (AuditPackageBean) auditPackageList.get(i);
					packageCode += apBean.getPackageCode() + ",";
				}
				packageCode = packageCode.substring(0, (packageCode.length() - 1));
			}
			
			ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
			if( rpBean == null ) {
				response.getWriter().write("{\"result\":\"failure\", \"tip\":\"该订单未入库，不能添加理赔单！\"}");
				return;
			}
			String deliverCompany = "";
			if( voOrder.deliverMapAll.containsKey(""+vorder.getDeliver())) {
				deliverCompany = (String)voOrder.deliverMapAll.get(""+vorder.getDeliver());
			}
			
			String result = "{status:'success', currentOrderCode:'" + vorder.getCode() + "', packageCode:'" + packageCode + "', orderStockId:'', deliverCompany:'" + deliverCompany + "', currentOrderId:'" + vorder.getId() + "', productList:[";
			List easyuidataList = new ArrayList();
			int x = productsList.size();
			for( int i = 0 ; i < x; i ++ ) {
				Map productMap = new HashMap();
				int productId = StringUtil.parstInt((String)((Map)productsList.get(i)).get("id"));
				int count = StringUtil.parstInt((String)((Map)productsList.get(i)).get("count"));
				if( count <= 0 ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"传入商品数量有误！\"}");
					return;
				}
				voProduct product = wareService.getProduct(productId);
				if( product == null ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"所传商品信息有误！\"}");
					return;
				}
				//确认商品是否属于一个订单....  根据出库单来判断
				/*List<OrderStockProductBean> list = claimsVerificationService.getAllProductsByOrder(vorder.getId());
				Map<Integer,OrderStockProductBean> productsMap = claimsVerificationService.getMergMap(list);*/
				Map<Integer,OrderStockProductBean> productsOrderMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
				if( productsOrderMap == null ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"订单信息有误，请重新扫描订单！\"}");
					return;
				}
				if( !productsOrderMap.containsKey(product.getId()) ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "不属于这个订单！\"}");
					return;
				}
				
				String productLine = "";// 产品线
				String productLineNameCondition = "  (product_line_catalog.catalog_id = (" 
					+ product.getParentId1() + ") and product_line_catalog.catalog_type = 1) or (product_line_catalog.catalog_type = 2 and product_line_catalog.catalog_id "
					+ " = (" + product.getParentId2() + "))";
				voProductLine vpl = wareService.getProductLine(productLineNameCondition);
				if( vpl != null ){
					productLine = vpl.getName();
				}
				productMap.put("productId", product.getId());
				productMap.put("currentOrderCode", vorder.getCode());
				productMap.put("packageCode", packageCode );
				productMap.put("deliverCompany", deliverCompany);
				productMap.put("oriName", product.getOriname());
				productMap.put("productLine", productLine);
				productMap.put("productCode", product.getCode());
				productMap.put("count", count);
				productMap.put("isExist", (String)((Map)productsList.get(i)).get("isExist"));
				easyuidataList.add(productMap);
			}
			
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long) x);
			easyuiDataGridJson.setRows(easyuidataList);
			Map returnMap = new HashMap();
			returnMap.put("result", "success");
			returnMap.put("orderStockId", "");
			returnMap.put("currentOrderId", vorder.getId());
			returnMap.put("rows", easyuiDataGridJson);
			response.getWriter().write(JSONObject.fromObject(returnMap).toString());
			return;
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 
	 * 添加理赔核销单
	 * @return
	 */
	@RequestMapping("/addClaimsVerification")
	@ResponseBody
	public void addClaimsVerification(HttpServletRequest request, HttpServletResponse response) throws IOException {
	
		int orderId = StringUtil.parstInt(request.getParameter("currentOrderId"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		String currentProducts = StringUtil.convertNull(request.getParameter("currentProducts"));
		
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List currentProductsList = new ArrayList();
		try {
			synchronized (stockLock) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				statService.getDbOp().startTransaction();
				// 检验 订单是否存在 
				// 检验 订单 是否有 退货入库记录
				voUser user = (voUser)request.getSession().getAttribute("userView");
				if(user == null) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有登录不可添加!\"}");
					return;
				}
				UserGroupBean group = user.getGroup();
				if( !group.isFlag(746) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"您没有添加理赔核销权限!\"}");
					return;
				}
				if( wareArea == -1 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"未选择库地区!\"}");
					return;
				}
				if(!CargoDeptAreaService. hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN)){
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"'你没有对应地区的退货库操作权限!\"}");
					return;
				}
				if( currentProducts != null && !"".equals(currentProducts) ) {
					String[] currentProductsString = currentProducts.split(",");
					Map currentProductsMap = new HashMap();
					for (int i = 0; i < currentProductsString.length; i++) {
						switch (i%3) {
						case 0: currentProductsMap.put("id", currentProductsString[i]);
							break;
						case 1: currentProductsMap.put("count", currentProductsString[i]);
							break;
						case 2: currentProductsMap.put("isExist", currentProductsString[i]);
								currentProductsList.add(currentProductsMap);
								currentProductsMap=new HashMap();
							break;
						default:
							break;
						}
					}
				} else {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有添加任何商品!\"}");
					return;
				}
				if ( orderId != 0 ){
					vorder = wareService.getOrder("id="+orderId);
					if( vorder == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到订单信息！\"}");
						return;
					}
				}
				
				if(vorder.getStatus() != 11 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前订单不是已退回状态，不能操作！\"}");
					return;
				}
				
				//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
				ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + "," + ClaimsVerificationBean.CLAIMS_COMPLETE + ")" );
				if( cvBean2 != null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前订单已经有已提交或已审核的理赔核销单了！\"}");
					return;
				}
				
				String packageCode = "";
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到对应的包裹单信息！\"}");
					return;
				} else {
					for( int i = 0; i < 1; i ++ ) {
						AuditPackageBean apBean = (AuditPackageBean) auditPackageList.get(i);
						packageCode += apBean.getPackageCode() + ",";
					}
					packageCode = packageCode.substring(0, (packageCode.length() - 1));
				}
				
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode()+ "'");
				if( rpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"该订单未入库，不能添加理赔单！\"}");
					return;
				}
				ReturnedPackageBean rpBean2 = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "' and area="+wareArea);
				if( rpBean2 == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"该订单入的不是所选库地区，不能添加理赔单！\"}");
					return;
				}
				
				
				String code = "LP"+DateUtil.getNow().substring(2,10).replace("-", "");   
				int maxid = service.getNumber("id", "claims_verification", "max", "id > 0");
				//添加采购入库单
				ClaimsVerificationBean codeBean;
				codeBean = claimsVerificationService.getClaimsVerification("code like '" + code + "%'");
				if(codeBean == null){
					//当日第一份入库单，编号最后三位 001
					code += "00001";
				}else {
					//获取当日入库单编号最大值
					codeBean = claimsVerificationService.getClaimsVerification("id =" + maxid); 
					String _code = codeBean.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-5));
					number++;
					code += String.format("%05d",new Object[]{new Integer(number)});
				}
				
				ClaimsVerificationBean cvBean = new ClaimsVerificationBean();
				cvBean.setCode(code);
				cvBean.setCreateTime(DateUtil.getNow());
				cvBean.setCreateUserId(user.getId());
				cvBean.setCreateUserName(user.getUsername());
				cvBean.setOrderCode(vorder.getCode());
				cvBean.setPackageCode(packageCode);
				cvBean.setDeliver(vorder.getDeliver());
				cvBean.setStatus(ClaimsVerificationBean.CLAIMS_UNDEAL);
				cvBean.setWareArea(wareArea);
				cvBean.setType(ClaimsVerificationBean.TYPE_WHOLE);
				cvBean.setPrice(vorder.getDprice());
				cvBean.setIsTicket(ClaimsVerificationBean.TICKET_NO);
				if( !claimsVerificationService.addClaimsVerification(cvBean) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加理赔单时，数据库操作失败！\"}");
					return;
				}
				
				int id = statService.getDbOp().getLastInsertId();
				
				if (!packageLog.addReturnPackageLog("添加理赔单", user, vorder.getCode())) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加退货日志失败！\"}");
					return;
				}
				
				int x = currentProductsList.size();
				for(int i = 0; i < x; i++ ) {
					String productId = (String) ((Map)currentProductsList.get(i)).get("id");
					
					int count = StringUtil.parstInt((String) ((Map)currentProductsList.get(i)).get("count"));
					int exist = ProductWarePropertyService.toInt((String) ((Map)currentProductsList.get(i)).get("isExist"));
					int iProductId = StringUtil.parstInt(productId);
					voProduct product = wareService.getProduct(iProductId);
					if( product == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品信息有误！\"}");
						return;
					}
					// 检验 商品是否 属于订单
					Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
					if( productsMap == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"订单信息有误，请重新扫描订单！\"}");
						return;
					}
					if( !productsMap.containsKey(product.getId()) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "不属于这个订单！\"}");
						return;
					}
					OrderStockProductBean ospBean = productsMap.get(product.getId());
					if( count > ospBean.getStockoutCount() ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "数量大于了订单中的量！\"}");
						return;
					}
					
					if( exist == -1 ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "的有无参数有错误！\"}");
						return;
					}
					
					if( exist == 0) {
						// 无实物的处理
						//库存
						boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
						if( !stockCheck ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，添加理赔单失败！\"}");
							return;
						}
						//货位库存
						boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
						if( !cargoStockCheck ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，添加理赔单失败！\"}");
							return;
						}
					} else if ( exist == 1) {
						
					}
					
					ClaimsVerificationProductBean cvpBean = new ClaimsVerificationProductBean();
					cvpBean.setCount(count);
					cvpBean.setExist(exist);
					cvpBean.setClaimsVerificationId(id);
					cvpBean.setProductCode(product.getCode());
					if( !claimsVerificationService.addClaimsVerificationProduct(cvpBean)) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品加入理赔单时，数据库操作失败！\"}");
						return;
					}
				}
				// 检验商品 在退货库 数量 是否有 （在有实物的时候）
				// 检验
				
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
				request.getSession().setAttribute("OrderStockProductsMap_"+vorder.getId(), null);
				response.getWriter().write("{\"result\":\"success\", \"tip\":\"添加理赔单成功！\"}");
				return;
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"异常!\"}");
			return;
		} finally {
			statService.releaseAll();
		}
		
	}
	
	/**
	 * 进入编辑页前， 得到理赔单数据
	 */
	@RequestMapping("/foreEditClaimsVerification")
	public void foreEditClaimsVerification(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		UserGroupBean group = user.getGroup();
		int id = StringUtil.parstInt(request.getParameter("id"));
		int print = StringUtil.parstInt(request.getParameter("print"));
		voOrder vorder = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id="+id);
			if( cvBean == null ) {
				request.setAttribute("msg", "没有这个理赔核销单！");
				request.getRequestDispatcher(noSession).forward(request, response);
				return;
			} else {
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				for( int i = 0; i < cvpList.size(); i ++ ) {
					ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean)cvpList.get(i);
					voProduct product = wareService.getProduct(cvpBean.getProductCode());
					cvpBean.setProduct(product);
					
					String productLine = "";// 产品线
					String productLineNameCondition = "  (product_line_catalog.catalog_id = (" 
						+ product.getParentId1() + ") and product_line_catalog.catalog_type = 1) or (product_line_catalog.catalog_type = 2 and product_line_catalog.catalog_id "
						+ " = (" + product.getParentId2() + "))";
					voProductLine vpl = wareService.getProductLine(productLineNameCondition);
					if( vpl != null ){
						productLine = vpl.getName();
					}
					cvpBean.setProductLineName(productLine);
				}
				cvBean.setClaimsVerificationProductList(cvpList);
				
				vorder = wareService.getOrder("code='" + cvBean.getOrderCode() + "'");
				if( vorder == null ) {
					request.setAttribute("msg", "没有找到理赔单对应 的订单信息！");
					request.getRequestDispatcher(noSession).forward(request, response);
					return;
				}
				if(vorder.getStatus() != 11 ) {
					request.setAttribute("msg", "理赔单对应的订单不已是已退回状态，不能操作！");
					request.getRequestDispatcher(noSession).forward(request, response);
					return;
				}
				//得到该订单的所有出库商品...
				List<BsbyOperationnoteBean> bsList = new ArrayList<BsbyOperationnoteBean>();
				String bsbyCodes = cvBean.getBsbyCodes();
				if( bsbyCodes != null && !bsbyCodes.equals("")) {
					String [] codes = bsbyCodes.split(",");
					if( codes != null && codes.length > 0 ) {
						for( int j = 0; j < codes.length; j ++ ) {
							String code = codes[j];
							BsbyOperationnoteBean bsbyBean = bsbyService.getBsbyOperationnoteBean("receipts_number='" + code + "'");
							if(bsbyBean != null ) {
								bsList.add(bsbyBean);
							}
						}
					}
				}
				cvBean.setBsbyList(bsList);
				List<OrderStockProductBean> list = claimsVerificationService.getAllProductsByOrder(vorder.getId());
				Map<Integer,OrderStockProductBean> map = claimsVerificationService.getMergMap(list);
				list =  claimsVerificationService.mergeOrderStockProducts(map);
				request.getSession().setAttribute("OrderStockProductsMap_" + vorder.getId(), map);
			}
			
			request.setAttribute("claimsVerificationBean", cvBean);
			request.setAttribute("userOrder", vorder);
			if( print == 1 ) {
				request.getRequestDispatcher("/admin/rec/oper/salesReturned/printClaimsVerification.jsp").forward(request, response);
				return;
			}
			
			List easyuidataList = new ArrayList();
			List list = cvBean.getClaimsVerificationProductList();
			int x = list.size();
			for( int i = 0; i < x; i++ ) {
				ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) list.get(i);
				Map productMap = new HashMap();
				productMap.put("productId", cvpBean.getProduct().getId());
				productMap.put("currentOrderCode", vorder.getCode());
				productMap.put("packageCode", cvBean.getPackageCode() );
				productMap.put("deliverCompany", voOrder.deliverMapAll.get(""+vorder.getDeliver()));
				productMap.put("oriName", cvpBean.getProduct().getOriname());
				productMap.put("productLine", cvpBean.getProductLineName());
				productMap.put("productCode", cvpBean.getProduct().getCode());
				productMap.put("count", cvpBean.getCount());
				productMap.put("isExist", cvpBean.getExist());
				easyuidataList.add(productMap);
			}
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long) x);
			easyuiDataGridJson.setRows(easyuidataList);
			Map returnMap = new HashMap();
			returnMap.put("rows", easyuiDataGridJson);
			request.setAttribute("data", returnMap);
			if( cvBean.getStatus() > 0 ) {
				MailingBalanceBean mbBean = baService.getMailingBalance("order_id=" + vorder.getId());
				Map<Integer, String> moneyMap = claimsVerificationService.calculateAvailablePrice(cvBean, vorder, mbBean);
				request.setAttribute("orderPrice", moneyMap.get(0));
				request.setAttribute("skuPrice", moneyMap.get(1));
				request.setAttribute("mailPrice", moneyMap.get(2));
				List<BsbyOperationnoteBean> bsList = cvBean.getBsbyList();
				for (int i = 0;  i < bsList.size() ; i++) {
					BsbyOperationnoteBean bsbyBean = bsList.get(i);
					int type = bsbyBean.getCurrent_type();
					if((type==0||type==1||type==2||type==5)&&(user.getId()==bsbyBean.getOperator_id()||group.isFlag(413))){
						bsbyBean.setLookup("0");
					}else if(type==6&&(user.getId()==bsbyBean.getOperator_id()||group.isFlag(229))){
						bsbyBean.setLookup("0");
					}else {
						bsbyBean.setLookup("1");
					}
					bsbyBean.setCurrent_type_name((String)bsbyBean.current_typeMap.get(Integer.valueOf(bsbyBean.getCurrent_type())));
				}
				cvBean.setBsbyList(bsList);
				request.setAttribute("claimsVerificationBean", cvBean);
				request.getRequestDispatcher("/admin/rec/oper/salesReturned/auditClaimsVerification.jsp").forward(request, response);
				return;
			}
			request.getRequestDispatcher("/admin/rec/oper/salesReturned/editClaimsVerification.jsp").forward(request, response);
			return;
			
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 
	 * 删除理赔核销单
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/deleteClaimsVerification")
	@ResponseBody
	public void deleteClaimsVerification(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前没有登录，操作失败！\"}");
			return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(747) ) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"您没有编辑理赔核销权限！\"}");
			return;
		}
		int id = StringUtil.parstInt(request.getParameter("id"));
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (stockLock) {
				statService.getDbOp().startTransaction();
				ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id="+id);
				if( cvBean == null ) {
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有这个理赔核销单！\"}");
					return;
				} else {
					if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_UNDEAL ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单状态不是未处理，不可以删除!\"}");
						return;
					}
					List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
					if( !claimsVerificationService.deleteClaimsVerification("id =" + id)) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"在删除理赔单时，数据库操作失败！\"}");
						return;
					}
					if( cvpList == null || cvpList.size() == 0 ) {
					
					} else {
						if( !claimsVerificationService.deleteClaimsVerificationProduct("claims_verification_id = " + cvBean.getId()) ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"在删除理赔单商品时，数据库操作失败！\"}");
							return;
						}
					}
				}
				if (!packageLog.addReturnPackageLog("删除理赔单", user, cvBean.getOrderCode())) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加退货日志失败！\"}");
					return;
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);

				response.getWriter().write("{\"result\":\"success\", \"tip\":\"删除成功！\"}");
				return;
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"异常！\"}");
			return;
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 
	 * 编辑理赔核销单
	 * @return
	 */
	@RequestMapping("/editClaimsVerification")
	@ResponseBody
	public void editClaimsVerification(HttpServletRequest request, HttpServletResponse response) {
	
		int id = StringUtil.parstInt(request.getParameter("id"));
		int orderId = StringUtil.parstInt(request.getParameter("currentOrderId"));
		String currentProducts = StringUtil.convertNull(request.getParameter("currentProducts"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		try {
			synchronized (stockLock) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				statService.getDbOp().startTransaction();
				// 检验 订单是否存在 
				// 检验 订单 是否有 退货入库记录
				voUser user = (voUser)request.getSession().getAttribute("userView");
				if(user == null) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有登录不可添加\"}");
					return;
				}
				UserGroupBean group = user.getGroup();
				if( !group.isFlag(747) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"您没有编辑理赔核销权限!\"}");
					return;
				}
				if(!CargoDeptAreaService. hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN)){
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"你没有对应地区的退货库操作权限!\"}");
					return;
				}
				ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有这个理赔单!\"}");
					return;
				}
				//验证状态， 只有为 未处理的可以继续进行编辑的。
				if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_UNDEAL) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单状态不是未处理，不可以进行编辑！\"}");
					return;
				}
				
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				cvBean.setClaimsVerificationProductList(cvpList);
				
				List currentProductsList = new ArrayList();
				if( currentProducts != null && !"".equals(currentProducts) ) {
					String[] currentProductsString = currentProducts.split(",");
					Map currentProductsMap = new HashMap();
					for (int i = 0; i < currentProductsString.length; i++) {
						switch (i%3) {
						case 0: currentProductsMap.put("id", currentProductsString[i]);
							break;
						case 1: currentProductsMap.put("count", currentProductsString[i]);
							break;
						case 2: currentProductsMap.put("isExist", currentProductsString[i]);
								currentProductsList.add(currentProductsMap);
								currentProductsMap=new HashMap();
							break;
						default:
							break;
						}
					}
				} 
				if( (currentProductsList == null || currentProductsList.size() == 0) && cvpList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前没有任何修改！\"}");
					return;
				}
				//添加退货日志
				if (!packageLog.addReturnPackageLog("修改理赔单", user, cvBean.getOrderCode())) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加退货日志失败！\"}");
					return;
				}
				if( (currentProductsList == null || currentProductsList.size() == 0) && cvpList.size() > 0 ) {
					//这种就是删除了所有的商品后的情况
					if( !claimsVerificationService.deleteClaimsVerificationProduct("claims_verification_id=" + cvBean.getId())) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"在删除理赔单商品时，数据库操作失败！\"}");
						return;
					}
					statService.getDbOp().commitTransaction();
					statService.getDbOp().getConn().setAutoCommit(true);
					response.getWriter().write("{\"result\":\"success\", \"tip\":\"修改理赔单成功！\"}");
					return;
				}
				
				if ( orderId != 0 ){
					vorder = wareService.getOrder("id="+orderId);
					if( vorder == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到订单信息！\"}");
						return;
					}
					if(vorder.getStatus() != 11 ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前订单不是已退回状态，不能操作！\"}");
						return;
					}
				} 
				
				
				String packageCode = "";
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到对应的包裹单信息！\"}");
					return;
				} else {
					for( int i = 0; i < 1; i ++ ) {
						AuditPackageBean apBean = (AuditPackageBean) auditPackageList.get(i);
						packageCode += apBean.getPackageCode() + ",";
					}
					packageCode = packageCode.substring(0, (packageCode.length() - 1));
				}
				
				ReturnedPackageBean rpBean2 = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "' and area="+wareArea);
				if( rpBean2 == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"该订单入的不是所选库地区，不能添加理赔单！\"}");
					return;
				}
				
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
				if( rpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"该订单未入库，不能添加理赔单！\"}");
					return;
				}
				
				if( vorder.getCode().equals(cvBean.getOrderCode()) ) {
					// 需要对比 商品数量后 决定
					Map currentMap = new HashMap();
					Map submitMap = new HashMap();
					int x = currentProductsList.size();
					int y = cvpList.size();
					for( int i = 0; i < x; i++ ) {
						int tempPid = ProductWarePropertyService.toInt((String)((Map)currentProductsList.get(i)).get("id"));
						voProduct product = wareService.getProduct(tempPid);
						product.setAddCount(StringUtil.parstInt((String)((Map)currentProductsList.get(i)).get("count")));
						product.setIsExist(ProductWarePropertyService.toInt((String)((Map)currentProductsList.get(i)).get("isExist")));
						submitMap.put(product.getCode(), product);
					}
					for( int i = 0; i < y; i++ ) {
						ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) cvpList.get(i);
						voProduct product = wareService.getProduct(cvpBean.getProductCode());
						currentMap.put(cvpBean.getProductCode(), product);
					}
					Iterator itr = submitMap.keySet().iterator();
					for( ;itr.hasNext(); ) {
						String productCode = (String)itr.next();
						voProduct submitProduct = (voProduct)submitMap.get(productCode);
						int count = submitProduct.getAddCount();
						int exist = submitProduct.getIsExist();
						//实现编辑
						if( currentMap.containsKey(productCode)) {
							voProduct product = (voProduct) currentMap.get(productCode);
							if( exist == -1 ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "的有无参数有错误！\"}");
								return;
							}
							Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
							if( productsMap == null ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"订单信息有误，请重新扫描订单！\"}");
								return;
							}
							if(!productsMap.containsKey( product.getId())) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "不属于这个订单！\"}");
								return;
							}
							OrderStockProductBean ospBean = productsMap.get(product.getId());
							if( count > ospBean.getStockoutCount() ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "数量大于了订单中的量！\"}");
								return;
							}
							if( exist == 0) {
								// 无实物的处理
								boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
								if( !stockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，添加理赔单失败！\"}");
									return;
								}
								//货位库存
								boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
								if( !cargoStockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，添加理赔单失败！\"}");
									return;
								}
							} else if ( exist == 1) {
								//库存
							}
							if( !claimsVerificationService.updateClaimsVerificationProduct("count="+ count + ", exist="+ exist, "claims_verification_id="+id + " and product_code='"+ product.getCode() +"'")) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品在修改数据时， 数据库操作失败！\"}");
								return;
							}
						} else {
							//原来没有需要添加
							//add
							voProduct product = (voProduct)submitMap.get(productCode);
							Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
							if( productsMap == null ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"订单信息有误，请重新扫描订单！\"}");
								return;
							}
							if(!productsMap.containsKey( product.getId())) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "不属于这个订单！\"}");
								return;
							}
							
							OrderStockProductBean ospBean = productsMap.get(product.getId());
							if( count > ospBean.getStockoutCount() ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "数量大于了订单中的量！\"}");
								return;
							}
							if( exist == -1 ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "的有无参数有错误！\"}");
								return;
							}
							if( exist == 0) {
								// 无实物的处理
								//库存
								boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
								if( !stockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，修改理赔单失败！\"}");
									return;
								}
								//货位库存
								boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
								if( !cargoStockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，修改理赔单失败！\"}");
									return;
								}
							} else if ( exist == 1) {
							}
							ClaimsVerificationProductBean cvpBean = new ClaimsVerificationProductBean();
							cvpBean.setCount(count);
							cvpBean.setExist(exist);
							cvpBean.setClaimsVerificationId(id);
							cvpBean.setProductCode(product.getCode());
							if( !claimsVerificationService.addClaimsVerificationProduct(cvpBean)) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品加入理赔单时，数据库操作失败！\"}");
								return;
							}
						}
					}
					Iterator itr2 = currentMap.keySet().iterator();
					for( ;itr2.hasNext(); ) {
						String productCode = (String)itr2.next();
						if( !submitMap.containsKey(productCode)) {
							if( !claimsVerificationService.deleteClaimsVerificationProduct("claims_verification_id="+id + " and product_code='" + productCode +"'")) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"在删除理赔单商品时，数据库操作失败！\"}");
								return;
							}
						}
					}
					
				} else {
					// 首先要删除所有原来关联的 商品
					if( cvpList.size() != 0 ) {
						if( !claimsVerificationService.deleteClaimsVerificationProduct("claims_verification_id="+ id)) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"在删除原来理赔单商品时，数据库操作失败！\"}");
							return;
						}
					}
					//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
					ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + ")" );
					if( cvBean2 != null ) {
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前关联订单已经有已提交或已审核的理赔核销单了！\"}");
						return;
					}
					// 跟添加流程差不多了
					//修改原有的理赔单 关联的  订单
					if( !claimsVerificationService.updateClaimsVerification("order_code='" + vorder.getCode() + "', deliver="+ vorder.getDeliver()+", package_code='" + packageCode +"'", "id=" +id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"在删除理赔单商品时，数据库操作失败！更新理赔单时，数据库操作失败！\"}");
						return;
					}
					// 添加理赔单的商品
					int x = currentProductsList.size();
					for(int i = 0; i < x; i++ ) {
						String productId = (String)((Map)currentProductsList.get(i)).get("id");
						int count = StringUtil.parstInt((String)((Map)currentProductsList.get(i)).get("count"));
						int exist = ProductWarePropertyService.toInt((String)((Map)currentProductsList.get(i)).get("isExist"));
						int iProductId = StringUtil.parstInt(productId);
						voProduct product = wareService.getProduct(iProductId);
						if( product == null ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品信息有误！\"}");
							return;
						}
						// 检验 商品是否 属于订单
						Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
						if( productsMap == null ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"订单信息有误，请重新扫描订单！\"}");
							return;
						}
						if(!productsMap.containsKey( product.getId())) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "不属于这个订单！\"}");
							return;
						}
						OrderStockProductBean ospBean = productsMap.get(product.getId());
						if( count > ospBean.getStockoutCount() ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "数量大于了订单中的量！\"}");
							return;
						}
						if( exist == -1 ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品：" + product.getCode() +  "的有无参数有错误！\"}");
							return;
						}
						if( exist == 0) {
							// 无实物的处理
							//库存
							boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
							if( !stockCheck ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，修改理赔单失败！\"}");
								return;
							}
							//货位库存
							boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
							if( !cargoStockCheck ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品已出库，修改理赔单失败！\"}");
								return;
							}
						} else if ( exist == 1) {
							
						}
						ClaimsVerificationProductBean cvpBean = new ClaimsVerificationProductBean();
						cvpBean.setCount(count);
						cvpBean.setExist(exist);
						cvpBean.setClaimsVerificationId(id);
						cvpBean.setProductCode(product.getCode());
						if( !claimsVerificationService.addClaimsVerificationProduct(cvpBean)) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"" + product.getCode() +  "商品加入理赔单时，数据库操作失败！\"}");
							return;
						}
					}
				}
				// 检验商品 在退货库 数量 是否有 （在有实物的时候）
				if( cvBean.getWareArea() != wareArea ) {
					if( !claimsVerificationService.updateClaimsVerification("ware_area="+wareArea, "id=" +id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"更新理赔单时，数据库操作失败！\"}");
						return;
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
				response.getWriter().write("{\"result\":\"success\", \"tip\":\"修改理赔单成功！\"}");
				return;
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		
	}
	
	/**
	 * 
	 * 提交理赔核销单
	 * @return
	 */
	@RequestMapping("/confirmClaimsVerification")
	@ResponseBody
	public void confirmClaimsVerification(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding( "UTF-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前没有登录，操作失败！\"}");
			return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(748) ) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"您没有提交理赔核销权限！\"}");
			return;
		}
		
		int id = StringUtil.parstInt(request.getParameter("id"));
		
		ClaimsVerificationBean cvBean = null;
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (stockLock) {
				statService.getDbOp().startTransaction();
				//检验 要提交的 理赔单是否存在
				cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有这个理赔单！\"}");
					return;
				}
				//验证状态， 只有为 未处理的可以继续进行编辑的。
				if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_UNDEAL) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单状态不是未处理，不可以提交！\"}");
					return;
				}
				// 看订单是否存在
				vorder = wareService.getOrder("code='"+ cvBean.getOrderCode() +"'");
				if( vorder == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到理赔单对应的订单信息！\"}");
					return;
				}
				if( vorder.getStatus() != 11) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单对应的订单不是已退回状态，不能操作！\"}");
					return;
				}
				
				//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
				ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + ")");
				if( cvBean2 != null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前理赔单对应订单已经有已提交或已审核的理赔核销单！\"}");
					return;
				}
				
				//看订单包裹是否可以查到
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到对应的包裹单信息！\"}");
					return;
				}
				//看订单是否如果 退货库
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() +"'");
				if( rpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单对应的订单并未入过返厂库，请核对！\"}");
					return;
				}
				ReturnedPackageBean rpBean2 = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "' and area="+ cvBean.getWareArea());
				if( rpBean2 == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单对应的订单入的不是所选地区的返厂库，请核对！\"}");
					return;
				}
				if(rpBean2.getClaimsVerificationId() != 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单对应的退货包裹已经有处理中或已审核的理赔核销单！\"}");
					return;
				}
				// 获取理赔商品列表  如果列表大小为0 就不允许提交
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				cvBean.setClaimsVerificationProductList(cvpList);
				if( cvpList == null || cvpList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单中并没有添加任何商品，不可以提交!\"}");
					return;
				} else {
					// 正式进入 提交流程
					
					//1.对于 无实物的商品要进行 见可用库存 加冻结量的操作
					/*for( int i = 0; i < cvpList.size(); i ++ ) {
						ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) cvpList.get(i);
						voProduct product = adminService.getProduct(cvpBean.getProductCode());
						if( cvpBean.getExist() == 0 ) {
							if( cvpBean.getCount() > 0 ){
								//执行库存操作
								//这里要修改 为生成一个未审核的 报损单
								String lockPS = claimsVerificationService.updateLockProductStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN, product, cvpBean.getCount(), service);
								if( !lockPS.equals("success")) {
									request.setAttribute("tip", lockPS);
									request.setAttribute("result", "failure");
									statService.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								String lockCPS = claimsVerificationService.updateLockCargoStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, cvpBean.getCount(), cargoService);
								if( !lockCPS.equals("success")) {
									request.setAttribute("tip", lockCPS);
									request.setAttribute("result", "failure");
									statService.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							} else {
								request.setAttribute("tip", "商品"+ cvpBean.getProductCode() +"数量有误，不可以提交!");
								request.setAttribute("result", "failure");
								statService.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}*/
					for( int i = 0; i < cvpList.size(); i ++ ) {
						ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) cvpList.get(i);
						voProduct product = wareService.getProduct(cvpBean.getProductCode());
						if( cvpBean.getExist() == 0 ) {
							if( cvpBean.getCount() > 0 ){
								//如果无实物 要看看库存够不够
								boolean stockCheck = claimsVerificationService.checkProductStock(cvBean.getWareArea(), ProductStockBean.STOCKTYPE_RETURN, product, cvpBean.getCount(), service);
								if( !stockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\", \"tip\":\""+product.getCode() +  "商品已出库，提交理赔单失败\"}");
									return;
								}
								//货位库存
								boolean cargoStockCheck = claimsVerificationService.checkCargoStock(cvBean.getWareArea(), ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, cvpBean.getCount(), cargoService);
								if( !cargoStockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\", \"tip\":\""+product.getCode() +  "商品已出库，修改理赔单失败！\"}");
									return;
								}
							} else {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品"+ cvpBean.getProductCode() +"数量有误，不可以提交!！\"}");
								return;
							}
						}
					}
					String set = "status="+ClaimsVerificationBean.CLAIMS_CONFIRM +", confirm_user_name='" + user.getUsername() + "', confirm_user_id=" + user.getId() + ", confirm_time='" + DateUtil.getNow() + "'";
					if( !claimsVerificationService.updateClaimsVerification(set, "id=" + cvBean.getId())) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"修改理赔单状态时，数据库操作失败！\"}");
						return;
					}
					if( !statService.updateReturnedPackage("claims_verification_id=" + id, "id=" + rpBean.getId())) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"修改退货包裹时，数据库操作失败！\"}");
						return;
					}
					if (!packageLog.addReturnPackageLog("提交理赔单", user, vorder.getCode())) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加退货日志失败！\"}");
						return;
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		response.getWriter().write("{\"result\":\"success\", \"tip\":\"提交理赔单成功！\"}");
		return;
	}
	
	/**
	 * 
	 * 添加理赔结算信息
	 * @return
	 */
	@RequestMapping("/addClaimsInfo")
	@ResponseBody
	public void addClaimsInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前没有登录，操作失败！\"}");
			return;
		}
		
		int type = StringUtil.toInt(request.getParameter("cvType"));
		int isTicket = StringUtil.toInt(request.getParameter("cvIsTicket"));
		float price = StringUtil.toFloat(request.getParameter("cvPrice"));
		int id = StringUtil.toInt(request.getParameter("id"));
		if( type == -1  || type > 2) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔类型参数错误！\"}");
			return;
		}
		if( isTicket == -1 || isTicket > 1 ) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"开具发票参数类型错误！\"}");
			return;
		}
		if( id <= 0 ) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"找不到对应的理赔单!\"}");
			return;
		}
		if( price < 0 ) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"输入价格有误！\"}");
			return;
		}
		WareService wareService = new WareService(); 
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (stockLock) {
				claimsVerificationService.getDbOp().startTransaction();
				
				ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"找不到对应的理赔单!\"}");
					return;
				} else if( cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_COMPLETE) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单状态是已完成，不可以修改结算信息了！\"}");
					return;
				}
				
				if( !claimsVerificationService.updateClaimsVerification("price="+ price+ ", is_ticket=" + isTicket + ", type=" + type, "id=" + id)) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"修改理赔结算信息时，数据库操作失败！\"}");
					return;
				}
				//添加退货日志
				if (!packageLog.addReturnPackageLog("修改理赔信息", user, cvBean.getOrderCode())) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加退货日志失败！\"}");
					return;
				}
				claimsVerificationService.getDbOp().commitTransaction();
				claimsVerificationService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch ( Exception e ) {
			claimsVerificationService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			claimsVerificationService.releaseAll();
		}
		response.getWriter().write("{\"result\":\"success\", \"tip\":\"修改理赔单结算数据成功！\"}");
		return;
		
	}
	
	/**
	 * 
	 * 审核理赔核销单
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/auditClaimsVerification")
	@ResponseBody
	public void auditClaimsVerification(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前没有登录，操作失败！\"}");
			return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(749) ) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"您没有审核理赔核销权限！\"}");
			return;
		}
		int id = StringUtil.parstInt(request.getParameter("id"));
		int yesno = StringUtil.parstInt(request.getParameter("yesno"));
		ClaimsVerificationBean cvBean = null;
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (stockLock) {
				statService.getDbOp().startTransaction();
				//检验 要提交的 理赔单是否存在
				cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有这个理赔单！\"}");
					return;
				}
				//验证状态， 只有为 未处理的可以继续进行编辑的。
				if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_CONFIRM) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单状态不是已提交，不可以审核！\"}");
					return;
				}
				// 看订单是否存在
				vorder = wareService.getOrder("code='"+ cvBean.getOrderCode() +"'");
				if( vorder == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到理赔单对应的订单信息！\"}");
					return;
				}
				if( vorder.getStatus() != 11) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单对应的订单不是已退回状态，不能操作！\"}");
					return;
				}
				//看订单包裹是否可以查到
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到对应的包裹单信息！\"}");
					return;
				}
				//看订单是否入过 退货库
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode()+"' and claims_verification_id=" + cvBean.getId() + " and area = " + cvBean.getWareArea());
				if( rpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"未找到理赔单关联的退货包裹，不可以审核！\"}");
					return;
				}
				// 获取理赔商品列表  如果列表大小为0 就不允许提交
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				cvBean.setClaimsVerificationProductList(cvpList);
				if( cvpList == null || cvpList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单中并没有添加任何商品，不可以审核!\"}");
					return;
				} else {
					if( yesno == 0 ) {
						//审核不通过 ，
						String set = "status="+ClaimsVerificationBean.CLAIMS_AUDIT_FAIL +", audit_user_name='" + user.getUsername() + "', audit_user_id="+user.getId() + ", audit_time='" + DateUtil.getNow()+"'";
						if( !claimsVerificationService.updateClaimsVerification(set, "id=" + cvBean.getId())) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"修改理赔单状态时，数据库操作失败！\"}");
							return;
						}
						if( !statService.updateReturnedPackage("claims_verification_id = 0", "id=" + rpBean.getId())) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"修改理赔单状态时，数据库操作失败！\"}");
							return;
						}
						response.getWriter().write("{\"result\":\"success\", \"tip\":\"审核理赔单不通过操作成功！\"}");
					} else if ( yesno == 1) {
						//审核通过， 把理赔单加入到 退货包裹列表
						String bsbyCodes = "";
						for( int i = 0; i < cvpList.size(); i ++ ) {
							ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) cvpList.get(i);
							voProduct product = wareService.getProduct(cvpBean.getProductCode());
							if( cvpBean.getExist() == 0 ) {
								if( cvpBean.getCount() > 0 ){
									//在这一部分 需要加入 添加报损单的 地方.....
									Object result = claimsVerificationService.createBsOperation(CargoInfoBean.STOCKTYPE_RETURN, cvBean.getWareArea(), user);
									if( result instanceof String ) {
										statService.getDbOp().rollbackTransaction();
										response.getWriter().write("{\"result\":\"failure\", \"tip\":\""+result+"\"}");
										return;
									} else {
										//添加商品
										Object ciBean = claimsVerificationService.getCargoCodeForSure(cvBean.getWareArea(), ProductStockBean.STOCKTYPE_RETURN,product.getId(), CargoInfoBean.STORE_TYPE2, cargoService);
										if( ciBean instanceof String) {
											statService.getDbOp().rollbackTransaction();
											response.getWriter().write("{\"result\":\"failure\", \"tip\":\""+ciBean+"\"}");
											return;
										}
										BsbyOperationnoteBean bsbyOperationnoteBean = (BsbyOperationnoteBean)result;
										CargoInfoBean cargoInfo = (CargoInfoBean) ciBean;
										Object bsbyProductBean = claimsVerificationService.addProductToBsOperation(product, cvpBean.getCount(), bsbyOperationnoteBean, cvBean.getWareArea(),cargoInfo, user);
										if( bsbyProductBean instanceof String ) {
											statService.getDbOp().rollbackTransaction();
											response.getWriter().write("{\"result\":\"failure\", \"tip\":\""+bsbyProductBean+"\"}");
											return;
										}
										//一次审核 锁库存操作
										String res = claimsVerificationService.auditBsbyOperationnote(bsbyOperationnoteBean,user);
										if( !res.equals("SUCCESS")) {
											statService.getDbOp().rollbackTransaction();
											response.getWriter().write("{\"result\":\"failure\", \"tip\":\""+res+"\"}");
											return;
										}
										bsbyCodes += bsbyOperationnoteBean.getReceipts_number()+",";
									}
								} else {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{\"result\":\"failure\", \"tip\":\"商品"+ cvpBean.getProductCode() +"数量有误，不可以提交！\"}");
									return;
								}
							}
						}
						
						if( !statService.updateReturnedPackage("claims_verification_id=" + cvBean.getId(), "id=" + rpBean.getId())) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"取消退货包裹关联时，数据库操作失败！\"}");
							return;
						}
						//修改理赔单状态
						String set = "status="+ClaimsVerificationBean.CLAIMS_AUDIT +", audit_user_name='" + user.getUsername() + "', audit_user_id="+user.getId() + ", audit_time='" + DateUtil.getNow()+"'" + ", bsby_codes='" + bsbyCodes+"'";
						if( !claimsVerificationService.updateClaimsVerification(set, "id=" + cvBean.getId())) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"修改理赔单状态时，数据库操作失败！\"}");
							return;
						}
						if (!packageLog.addReturnPackageLog("审核理赔单", user, vorder.getCode())) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加退货日志失败！\"}");
							return;
						}
						response.getWriter().write("{\"result\":\"success\", \"tip\":\"审核理赔单通过操作成功！\"}");
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return;
	}
	
	/**
	 * 
	 * 完成理赔核销单
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/completeClaimsVerification")
	@ResponseBody
	public void completeClaimsVerification(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"当前没有登录，操作失败！\"}");
			return;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(759) ) {
			response.getWriter().write("{\"result\":\"failure\", \"tip\":\"您没有确认完成理赔单的权限！\"}");
			return;
		}
		
		int id = StringUtil.parstInt(request.getParameter("id"));
		ClaimsVerificationBean cvBean = null;
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (stockLock) {
				statService.getDbOp().startTransaction();
				//检验 要提交的 理赔单是否存在
				cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有这个理赔单！\"}");
					return;
				}
				//验证状态， 只有审核通过的可以完成
				if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_AUDIT) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单状态不是审核通过，不可以完成！\"}");
					return;
				}
				// 看订单是否存在
				vorder = wareService.getOrder("code='"+ cvBean.getOrderCode() +"'");
				if( vorder == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到理赔单对应的订单信息！\"}");
					return;
				}
				if( vorder.getStatus() != 11) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单对应的订单不是已退回状态，不能操作！\"}");
					return;
				}
				//看订单包裹是否可以查到
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"没有找到对应的包裹单信息！\"}");
					return;
				}
				//看订单是否入过 退货库
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
				if( rpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"该订单并未入过返厂库，请核对！\"}");
					return;
				}
				// 获取理赔商品列表  如果列表大小为0 就不允许提交
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				cvBean.setClaimsVerificationProductList(cvpList);
				if( cvpList == null || cvpList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单中并没有添加任何商品，不可以完成!\"}");
					return;
				} else {
					if( !claimsVerificationService.updateClaimsVerification("status=" + ClaimsVerificationBean.CLAIMS_COMPLETE+", complete_user_name='" + user.getUsername() + "', complete_user_id="+user.getId() + ", complete_time='" + DateUtil.getNow()+"'", "id=" + id)) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{\"result\":\"failure\", \"tip\":\"理赔单修改状态时，数据库操作失败！\"}");
						return;
					}
				}
				if (!packageLog.addReturnPackageLog("理赔单确认完成", user, vorder.getCode())) {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\", \"tip\":\"添加退货日志失败！\"}");
					return;
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		response.getWriter().write("{\"result\":\"success\", \"tip\":\"完成理赔单成功！\"}");
		return;
	}
	
	
	/**
	 * 
	 * 清空缓存包裹信息
	 * @return
	 */
	@RequestMapping("/clearPackageCache")
	public void clearPackageCache(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			HttpSession session = request.getSession();
			session.setAttribute("currentOrderStock", null);
			session.setAttribute("currentProductMap", null);
			session.setAttribute("orderBillInfoBean", null);
			session.setAttribute("scanMap", null);
			session.setAttribute("auditPackageBeanCV", null);
			session.setAttribute("orderInfoCV", null);
			session.setAttribute("rpBeanCV", null);
			response.getWriter().write("清空成功");
			return;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * 检查包裹
	 * @return
	 */
	@RequestMapping("/checkPackageAutomatic")
	public void checkPackageAutomatic(HttpServletRequest request, HttpServletResponse response) {
	
		String code = StringUtil.convertNull(request.getParameter("code"));
		String tip = "";
		String obib = "normal";
		voOrder vorder = null;
		OrderStockBean orderStockBean = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized ( stockLock ) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				voUser user = (voUser)request.getSession().getAttribute("userView");
				if(user == null) {
					tip = "没有登录，不可以继续操作。";
				}
				if( code.equals("") ) {
					tip = "提交值为空。";
				} else {
					HttpSession session = request.getSession();
					orderStockBean = (OrderStockBean)session.getAttribute("currentOrderStock");
					Map<Integer,String> map = (Map<Integer, String>) session.getAttribute("currentProductMap");
					if( orderStockBean == null ) {
						// 把code 当做包裹单号
						AuditPackageBean auditPackageBean = istockService.getAuditPackage("package_code='" + code + "'");
						if( auditPackageBean != null ) {
							vorder = wareService.getOrder("code='" + auditPackageBean.getOrderCode()+"'");
							if( vorder == null ) {
								tip = "包裹单号对应的订单未找到。";
							} else if ( vorder.getStatus() != 11 ) {
								tip = "包裹单对应订单状态不是已退回。";
							} else {
								
								orderStockBean = istockService.getOrderStock("order_id="+vorder.getId()+ " and status!="+OrderStockBean.STATUS4);
								if( orderStockBean == null ) {
									tip = "包裹单号对应的出库单未找到。";
								}else {
									List<OrderStockProductBean> ospList = (List<OrderStockProductBean>)istockService.getOrderStockProductList("order_stock_id=" + orderStockBean.getId(), -1, -1, "id asc");
									if( ospList == null || ospList.size() == 0 ) {
										tip = "没有找到该包裹中对应商品信息。";
									} else {
										OrderBillInfoBean obiBean = claimsVerificationService.getOrderStockOutInfo(vorder.getId());
										if( obiBean == null || obiBean.getOrderList().size() == 0 ) {
											tip = "没有找到对应订单的电子档发货清单。";
										} else {
											ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
											if( rpBean == null ) {
												tip = "没有找到退货包裹记录。";
											} else {
												orderStockBean.setOrderStockProductList(ospList);
												int x = ospList.size();
												map = new HashMap<Integer, String>();
												for( int i = 0 ; i < x; i++ ) {
													OrderStockProductBean ospBean = ospList.get(i);
													map.put(ospBean.getProductId(), "");
												}
												session.setAttribute("currentOrderStock", orderStockBean);
												session.setAttribute("currentProductMap", map);
												session.setAttribute("orderBillInfoBean", obiBean);
												session.setAttribute("auditPackageBeanCV", auditPackageBean);
												session.setAttribute("orderInfoCV", vorder);
												session.setAttribute("rpBeanCV", rpBean);
												obib = claimsVerificationService.getOrderBillInfoHTML(obiBean, request);
												tip = "扫描成功！包裹单号"+code;
											}
										}
									}
								}
							}
							
						} else {
							tip = "该包裹单号不存在。";
							obib = "clear";
						}
					} else {
						// 如果当前 session 里有包裹单查出来的订单信息 就不会再更改
						// 除非这个包裹的检查已经完成...
						Map<Integer,Integer> scanMap = (Map<Integer, Integer>)session.getAttribute("scanMap");
						if( scanMap == null ) {
							scanMap = new HashMap<Integer,Integer>();
						}
						ReturnedPackageBean rpBean = (ReturnedPackageBean) session.getAttribute("rpBeanCV");
						
						if( code.startsWith("B") || code.startsWith("D") || code.startsWith("S") || code.startsWith("T") || code.startsWith("Z") || code.startsWith("QCE") || code.startsWith("W")) {
							//订单号
							voOrder vorder2 = wareService.getOrder("code='" + code +"'");
							if( vorder2 == null ) {
								tip = "该订单号不存在！";
							} else {
								tip = "扫描成功，订单号" + code;
								if( orderStockBean.getOrderId() != vorder2.getId() ) { 
									tip += "r-n"+ "订单号和包裹单号不对应！";
								}
								//检查 商品数量和扫描数量 是否有差别
								List<OrderStockProductBean> list = orderStockBean.getOrderStockProductList(); 
								String temp = claimsVerificationService.checkPackageProductIntegrity(orderStockBean,list, scanMap, map);
								tip += temp;
								AuditPackageBean apBean = (AuditPackageBean)session.getAttribute("auditPackageBeanCV");
								voOrder order = (voOrder)session.getAttribute("orderInfoCV");
								session.setAttribute("currentOrderStock", null);
								session.setAttribute("currentProductMap", null);
								session.setAttribute("orderBillInfoBean", null);
								obib="clear";
								session.setAttribute("scanMap", null);
								session.setAttribute("auditPackageBeanCV", null);
								session.setAttribute("orderInfoCV", null);
								session.setAttribute("rpBeanCV", null);
								statService.getDbOp().startTransaction();
								if (!packageLog.addReturnPackageLog("核查包裹", user, order.getCode())) {
									packageLog.getDbOp().rollbackTransaction();
									tip += "r-n"+"添加退货日志失败！";
								}
								statService.getDbOp().commitTransaction();
								statService.getDbOp().getConn().setAutoCommit(true);
								Object result = claimsVerificationService.addReturnPackageCheckInfo(scanMap,map,orderStockBean, list, apBean, rpBean, order,user);
								if( result instanceof String ) {
									tip += "r-n" + result;
								}
								tip += "r-n"+"包裹信息缓存已清空。";
							}
							
						} else if( code.startsWith("CK") ) {
							//出库单号
							OrderStockBean orderStockBean2 = istockService.getOrderStock("code='"+code + "'"+ " and status!="+OrderStockBean.STATUS4);
							if( orderStockBean2 == null ) {
								tip = "该出库单号不存在！";
							} else {
								tip = "扫描成功，出库单号" + code;
								if( orderStockBean.getId() != orderStockBean2.getId() ) { 
									tip += "r-n"+"出库单号和包裹单号不对应！";
								}
								//检查 商品数量与扫描数量 是否有差别
								List<OrderStockProductBean> list = orderStockBean.getOrderStockProductList(); 
								String temp = claimsVerificationService.checkPackageProductIntegrity(orderStockBean,list, scanMap, map);
								tip += temp;
								AuditPackageBean apBean = (AuditPackageBean)session.getAttribute("auditPackageBeanCV");
								voOrder order = (voOrder)session.getAttribute("orderInfoCV");
								session.setAttribute("currentOrderStock", null);
								session.setAttribute("currentProductMap", null);
								session.setAttribute("orderBillInfoBean", null);
								obib="clear";
								session.setAttribute("scanMap", null);
								session.setAttribute("auditPackageBeanCV", null);
								session.setAttribute("orderInfoCV", null);
								session.setAttribute("rpBeanCV", null);
								statService.getDbOp().startTransaction();
								if (!packageLog.addReturnPackageLog("核查包裹", user, order.getCode())) {
									packageLog.getDbOp().rollbackTransaction();
									tip += "r-n"+"添加退货日志失败！";
								}
								statService.getDbOp().commitTransaction();
								statService.getDbOp().getConn().setAutoCommit(true);
								Object result = claimsVerificationService.addReturnPackageCheckInfo(scanMap,map,orderStockBean, list, apBean, rpBean, order,user);
								if( result instanceof String ) {
									tip += "r-n" + result;
								}
								tip += "r-n"+"包裹信息缓存已清空。";
							}
							
						} else if( code.matches("[0-9]{1,20}")) {
							//商品编号
							voProduct product = claimsVerificationService.getProuctByAnyCode(code, bService,wareService);
							if( product == null ) {
								tip = "该商品编号不存在！";
							} else {
								tip = "扫描成功！当前商品" + product.getCode();
								if( map.containsKey(product.getId())) {
									if( scanMap.containsKey(product.getId())) {
										int count = scanMap.get(product.getId());
										count ++;
										scanMap.put(product.getId(), count);
										session.setAttribute("scanMap", scanMap);
										tip += "已扫描数量"+count;
									} else {
										scanMap.put(product.getId(), 1);
										session.setAttribute("scanMap", scanMap);
										tip += "已扫描数量"+ 1;
									}
								} else {
									tip += "r-n" + "该商品不属于订单！";
									if( scanMap.containsKey(product.getId())) {
										int count = scanMap.get(product.getId());
										count ++;
										scanMap.put(product.getId(), count);
										session.setAttribute("scanMap", scanMap);
										tip += "已扫描数量"+count;
									} else {
										scanMap.put(product.getId(), 1);
										session.setAttribute("scanMap", scanMap);
										tip += "已扫描数量"+ 1;
									}
								}
							}
						} else {
							tip = "当前缓存中已有包裹信息，请先取消当前包裹！";
						}
					}
					
				}
				String result = "{'tip': '"+ tip + "', 'OBIB': '" + obib + "'}";
				//注意设定了 返回的tip中如果有中文句号 就会代表session中无缓存.....
				response.getWriter().write(result);
				return;
			}
				
			
		} catch ( Exception e ) {
			e.printStackTrace();
			statService.getDbOp().rollbackTransaction();
		} finally {
			statService.releaseAll();
		}
	}
}
