/*
 * Created on 2007-11-14
 *
 */
package adultadmin.action.stock;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceSellBean;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.IMEI.IMEIUserOrderBean;
import mmb.stock.aftersale.AfStockService;
import mmb.stock.aftersale.AfterSaleDetectProductBean;
import mmb.stock.aftersale.AfterSaleOperationListBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.ReturnPackageLogService;
import mmb.stock.stat.ReturnedPackageBean;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockHistoryBean;
import adultadmin.bean.stock.StockOperationBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.CookieUtil;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceSaleBaseDataService;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：  
 */
public class StockAction {

	public static byte[] stockLock = new byte[0];
	public Log stockLog = LogFactory.getLog("stock.Log");


    /**
     * 作者：李北金
     * 
     * 创建日期：2007-11-20
     * 
     * 说明：产品的人员操作记录
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void stockAdminHistory(HttpServletRequest request,
            HttpServletResponse response) {
        int operId = StringUtil.StringToId(request.getParameter("operId"));
        String logType = StringUtil.dealParam(request.getParameter("logType"));
        if(logType == null || logType.length() == 0){
        	logType = String.valueOf(StockAdminHistoryBean.STOCK_OPERATION);
        }
        DbOperation dbOp = new DbOperation();
        dbOp.init(DbOperation.DB_SLAVE);
        IStockService service = ServiceFactory.createStockService(
                IBaseService.CONN_IN_SERVICE, dbOp);
        try {
            String condition = "id = " + operId;
            StockOperationBean bean = service.getStockOperation(condition);
            condition = "log_id = " + operId + " and ( log_type=" + StockAdminHistoryBean.STOCK_OPERATION + " || log_type in ("
                    + logType
                    + ")) ";//and deleted = 0";
            List list = service
                    .getStockAdminHistoryList(condition, 0, -1, "id");

            request.setAttribute("list", list);
            request.setAttribute("bean", bean);
        } finally {
            service.releaseAll();
        }
    }


    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2008-2-18
     * 
     * 说明：退货入库操作记录列表
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void cancelStockinList(HttpServletRequest request,
            HttpServletResponse response) {
        int countPerPage = 20;
        DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
        IStockService service = ServiceFactory.createStockService(
                IBaseService.CONN_IN_SERVICE, dbOp);
        WareService wareService = new WareService(dbOp);
        String orderCode = StringUtil.dealParam(request.getParameter("orderCode"));
        int cancelType = StringUtil.StringToId(request.getParameter("cancelType"));
        try {
        	//该员工可操作的区域列表
        	List areaList = CargoDeptAreaService. getCargoDeptAreaList(request);
        	String areaString = new String();
			if (areaList != null) {
				for (int i = 0; i < areaList.size(); i++) {
					if (i == areaList.size()-1) {
						areaString += (String) areaList.get(i);

					} else {
						areaString += areaList.get(i) + ",";

					}
				}
			}
            String condition = "";
            if(cancelType > 0){
            	switch(cancelType){
            		case 1:
            			condition = "type = " + StockOperationBean.CANCEL_STOCKIN;
            			break;
            		case 2:
            			condition = "type = " + StockOperationBean.CANCEL_EXCHANGE;
            			break;
            		case 3:
            			condition = "type = " + StockOperationBean.BAD_EXCHANGE;
            			break;
            		default:
            			condition = "type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ") ";
            	}
            } else {
            	condition = "type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ") ";
            }
            if(orderCode != null && orderCode.length() > 0){
            	condition += " and order_code='" + orderCode + "'";
            }
            if(areaString!=null && areaString.length()>0){
            	condition+=" and area in("+areaString+")";
            }else{
            	condition+=" and id<0";
            }
            //总数
            int totalCount = service.getStockOperationCount(condition);
            //页码
            int pageIndex = StringUtil.StringToId(request
                    .getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,
                    countPerPage);
            List list = service.getStockOperationList(condition, paging
                    .getCurrentPageIndex()
                    * countPerPage, countPerPage, "id desc");
            Iterator iter = list.listIterator();
            while(iter.hasNext()){
            	StockOperationBean so = (StockOperationBean)iter.next();
            	so.setOrder(wareService.getOrder("code='" + so.getOrderCode() + "'"));
            	if(so.getType() == StockOperationBean.CANCEL_EXCHANGE){
            		so.getOrder().setNewOrder(wareService.getOrder("id=" + so.getOrder().getNewOrderId()));
            	}
            }
            String prefixUrl = "cancelStockinList.jsp";
            if(orderCode != null && orderCode.length() > 0){
            	prefixUrl += "?orderCode=" + orderCode;
            }
            paging.setPrefixUrl(prefixUrl);
            request.setAttribute("paging", paging);
            request.setAttribute("list", list);
            request.setAttribute("areaList", areaList);
        } finally {
            service.releaseAll();
        }
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2008-2-20
     * 
     * 说明：增加退货入库操作
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public  void addCancelStockin(HttpServletRequest request,
            HttpServletResponse response) {
    	voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
    	synchronized(stockLock){
	    	String orderCode = StringUtil.dealParam(request.getParameter("orderCode"));
	    	int type = StringUtil.StringToId(request.getParameter("type"));
	        if (StringUtil.convertNull(orderCode).equalsIgnoreCase("")) {
	            request.setAttribute("tip", "请输入订单编号！");
	            request.setAttribute("result", "failure");
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
	
	        String name = orderCode + "_" + DateUtil.getNow().substring(0, 10) + "_" + title;
	
	        WareService wareService = new WareService();
	        IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

	       
	        try {
	        	voOrder order = wareService.getOrder("code = '" + orderCode + "'");
	 	        if (order == null) {
	 	            request.setAttribute("tip", "这个订单不存在！");
	 	            request.setAttribute("result", "failure");
//	 	            adminService.close();
	 	            return;
	 	        }
	 	        OrderStockBean stockBean = service.getOrderStock("order_id = " + order.getId());
				if(!CargoDeptAreaService. hasCargoDeptArea(request, stockBean.getStockArea(), stockBean.getStockType())){
	   				request.setAttribute("tip", "只能添加‘用户所属的库地区’的订单");
	   				request.setAttribute("result", "failure");
					return;
				}
	        	// 从新的订单出货表中 查找订单的出货情况
	        	OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and status<>" + OrderStockBean.STATUS5 + " and order_code = '" + orderCode + "'");
			    if (oper == null || oper.getStatus() != OrderStockBean.STATUS3) {
			        request.setAttribute("tip", "这个订单还没有进行出库操作！");
			        request.setAttribute("result", "failure");
			        request.setAttribute("oper", oper);
			        return;
			    } else {
			    	//从旧的退换货入库表中 查找订单的退货情况
			    	if(service.getStockOperation("status <> " + StockOperationBean.STATUS2 + " and type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ") and order_code = '" + orderCode + "'") != null){
			    		request.setAttribute("tip", "这个订单正在进行退货入库操作中！");
				        request.setAttribute("result", "failure");
				        request.setAttribute("oper", oper);
				        return;
			    	}
			    }
			    // 从新的订单出货 体系中 查找 出货记录
			    List operHisList = service.getOrderStockProductList("order_stock_id=" + oper.getId(), -1, -1, null);
	
	            service.getDbOp().startTransaction();
	            int id = service.getNumber("id", "stock_operation", "max", "id > 0") + 1;
	            StockOperationBean bean = new StockOperationBean();
	            bean.setCreateDatetime(DateUtil.getNow());
	            bean.setId(id);
	            bean.setName(name);
	            bean.setOrderCode(orderCode);
	            bean.setRemark("");
	            bean.setStatus(StockOperationBean.STATUS1);
	            bean.setType(stockType);
	            bean.setGroupId(0);
	            bean.setStockType(ProductStockBean.STOCKTYPE_RETURN);
	            bean.setUserId(user.getId());
	            bean.setUserName(user.getUsername());
	            /*
	            if(type == 2){
	            	bean.setArea(StockOperationBean.BJ);
	            } else {
	            	bean.setArea(oper.getArea());
	            }*/
	            bean.setArea(oper.getStockArea());
	            bean.setLastOperTime(DateUtil.getNow());
	            if (!service.addStockOperation(bean)) {
	                request.setAttribute("tip", "添加失败！");
	                request.setAttribute("result", "failure");
	                service.getDbOp().rollbackTransaction();
	                return;
	            }
	
	            Iterator itr = operHisList.iterator();
	            int shId = 0;
	            OrderStockProductBean stockHis = null;
	            StockHistoryBean sh = null;
	            while (itr.hasNext()) {
	                stockHis = (OrderStockProductBean) itr.next();
	                shId = service.getNumber("id", "stock_history", "max", "id > 0") + 1;
	                sh = new StockHistoryBean();
	                sh.setCreateDatetime(DateUtil.getNow());
	                sh.setDealDatetime(null);
	                sh.setId(shId);
	                sh.setOperId(id);
	                sh.setOperType(stockType);
	                sh.setProductCode(stockHis.getProductCode());
	                sh.setProductId(stockHis.getProductId());
	                sh.setRemark("");
	                sh.setStatus(StockHistoryBean.UNDEAL);
//	                sh.setStockGd(0);
	                sh.setStockBj(stockHis.getStockoutCount());
	                sh.setStockType(StockHistoryBean.IN);
	                service.addStockHistory(sh);
	            }
	
	            //log记录
	            voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
	            if (admin == null) {
	                request.setAttribute("tip", "当前没有登录，添加失败！");
	                request.setAttribute("result", "failure");
	                service.getDbOp().rollbackTransaction();
	                return;
	            }
	
	            StockAdminHistoryBean log = new StockAdminHistoryBean();
	            log.setAdminId(admin.getId());
	            log.setAdminName(admin.getUsername());
	            log.setLogId(id);
	            log.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
	            log.setOperDatetime(DateUtil.getNow());
	            log.setRemark("新建" + title + "操作：" + bean.getName());
	            log.setType(StockAdminHistoryBean.CREATE);
	            service.addStockAdminHistory(log);
	            
	         
				
	            service.getDbOp().commitTransaction();
	        } catch (Exception e) {
	        	service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2008-2-20
     * 
     * 说明：查看退货入库
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void cancelStockin(HttpServletRequest request,
            HttpServletResponse response) {
    	
    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	//user = null;
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewBatchPrice = group.isFlag(290);
		
        int countPerPage = 50;
        int id = StringUtil.StringToId(request.getParameter("id"));
        DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
        IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        WareService wareService = new WareService(service.getDbOp());
        try {
            StockOperationBean bean = service.getStockOperation("id = " + id
                    + " and type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ")");

            //相关的入库记录
            String condition = "oper_id = " + bean.getId()
                    + " and stock_type = " + StockHistoryBean.IN;
            //总数
            int totalCount = service.getStockHistoryCount(condition);
            //页码
            int pageIndex = StringUtil.StringToId(request
                    .getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,
                    countPerPage);
            paging.setPrefixUrl("cancelStockin.jsp?id=" + id);
            ArrayList inList = service.getStockHistoryList(condition, paging
                    .getCurrentPageIndex()
                    * countPerPage, countPerPage, "id desc");
            bean.setOrder(wareService.getOrder("code='" + bean.getOrderCode() + "'"));
            //相关的产品
            condition = "a.id in (select distinct product_id from stock_history where oper_id = "
                    + bean.getId() + ")";
            List productList = new ArrayList();
            Iterator iter = inList.listIterator();
            int productCountBj = 0;
            int productCountGd = 0;
            OrderStockBean soOrderStock = null;
            HashMap batchMap = new HashMap();
            if(bean.getStatus() == StockOperationBean.STATUS1){
            	soOrderStock = service.getOrderStock(" status<>" + StockOperationBean.STATUS4 + " and status<>" + StockOperationBean.STATUS5 + " and order_code='" + bean.getOrderCode() + "'");
            } else {
            	soOrderStock = service.getOrderStock(" status=" + StockOperationBean.STATUS5 + " and order_code='" + bean.getOrderCode() + "'");
            }
            while (iter.hasNext()) {
                StockHistoryBean sh = (StockHistoryBean) iter.next();
                productCountBj += sh.getStockBj();
                productCountGd += sh.getStockGd();
                voProduct product = wareService.getProduct(sh.getProductId());
    			product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
                if(soOrderStock != null){
	                OrderStockProductBean shOrderStock = service.getOrderStockProduct("order_stock_id=" + soOrderStock.getId() + " and product_id=" + sh.getProductId());
	                if(shOrderStock != null){
	                	product.setBuyCount(shOrderStock.getStockoutCount());
	                }
                }
                if (product != null) {
                    productList.add(product);
                }
                
                //获取相关批次信息
                List batchLogList = service.getStockBatchLogList("code = '"+bean.getOrderCode()+"' and product_id="+product.getId()+" and remark='退货入库'", -1, -1, "id");
                Iterator batchLogIter = batchLogList.listIterator();
                String batch = "";
                while(batchLogIter.hasNext()){
                	StockBatchLogBean batchLog = (StockBatchLogBean)batchLogIter.next();
                	batch = batch + batchLog.getBatchCode()+"("+batchLog.getBatchCount()+")";
                	if(viewBatchPrice){
                		batch = batch+"/"+batchLog.getBatchPrice();
                	}
                	batch = batch+"</br>";
                }
                batchMap.put(Integer.valueOf(product.getId()), batch);
            }
            //退换货操作 中的 更换的商品列表
            if(bean.getType() == StockOperationBean.CANCEL_EXCHANGE || bean.getType() == StockOperationBean.BAD_EXCHANGE){
            	List exchangeProductList = null;
            	int exchangeProductCount = 0;
            	if(bean.getOrder() != null){
            		exchangeProductList = new ArrayList();
            		List outList = service.getStockHistoryList("oper_id=" + bean.getId() + " and stock_type = " + StockHistoryBean.OUT, -1, -1, null);
            		iter = outList.iterator();
            		while(iter.hasNext()){
            			StockHistoryBean sh = (StockHistoryBean) iter.next();
            			voProduct product = wareService.getProduct(sh.getProductId());
            			product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
            			product.setBuyCount(sh.getStockBj());
                        if (product != null) {
                        	exchangeProductList.add(product);
                        }
                        exchangeProductCount += product.getBuyCount();
            		}
            		request.setAttribute("exchangeProductCount", String.valueOf(exchangeProductCount));
            		request.setAttribute("exchangeProductList", exchangeProductList);
            	}
            }
            request.setAttribute("paging", paging);
            request.setAttribute("bean", bean);
            request.setAttribute("inList", inList);
            request.setAttribute("productCountGd", String
                    .valueOf(productCountGd));
            request.setAttribute("productCountBj", String
                    .valueOf(productCountBj));
            request.setAttribute("productList", productList);
            request.setAttribute("batchMap", batchMap);
        } catch(Exception e){
        	e.printStackTrace();
        	request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
        }finally {
            service.releaseAll();
        }
    }


    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2008-2-20
     * 
     * 说明：确认退货入库操作
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     * @throws SQLException 
     */
//    public void completeCancelStockin(HttpServletRequest request,
//            HttpServletResponse response) throws SQLException {
//    	
//    	voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
//        if (admin == null) {
//            request.setAttribute("tip", "当前没有登录，添加失败！");
//            request.setAttribute("result", "failure");
//            return;
//        }
//    	
//        synchronized(StockAction.stockLock){
//	        int operId = StringUtil.StringToId(request.getParameter("operId"));
//	        WareService wareService = new WareService();
//	        IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//	        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
//	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
//	        DbOperation db = service.getDbOp();
//	    	DbOperation redb=new DbOperation();
//	    	redb.init("adult_slave");
//	    	IStockService reservice = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, redb);
//    		PreparedStatement pstmt = null;
//    		ResultSet rs = null;
//    		ResultSet rers = null;
//    		PreparedStatement reps = null;
//    		ResultSet rs0 = null;
//	        try {
//	            StockOperationBean bean = service.getStockOperation("id = "
//	                    + operId + " and type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ")");
//	            if (bean.getStatus() != StockOperationBean.STATUS1) {
//	                request.setAttribute("tip", "该操作已经完成，不能再更改！");
//	                request.setAttribute("result", "failure");
//	                return;
//	            }
//
//	            int productId = StringUtil.StringToId(request.getParameter("productId"));
//	            String condition = "oper_id = " + bean.getId() + " and status = "
//	                    + StockHistoryBean.UNDEAL + " and stock_type = "
//	                    + StockHistoryBean.IN;
//	            if (productId > 0) {
//	                condition += " and product_id = " + productId;
//	            }
//	
//	            ArrayList shList = service.getStockHistoryList(condition, 0, -1, "id");
//	            Iterator itr = shList.iterator();
//	            //开始事务
//	            service.getDbOp().startTransaction();
//	            StockHistoryBean sh = null;
//	            voProduct product = null;
//	            String set = null;
//	            int count = 0;
//	            while (itr.hasNext()) {
//	                sh = (StockHistoryBean) itr.next();
//	                //烂货退换，入库数量可以为0
//	            	if(!(bean.getType() == StockOperationBean.BAD_EXCHANGE) && sh.getStockBj() == 0 && sh.getStockGd() == 0){
//	            		request.setAttribute("tip", "入库数量为0，无法入库！");
//	                    request.setAttribute("result", "failure");
//	                    service.getDbOp().rollbackTransaction();
//	                    return;
//	            	}
//	                count++;
//	                product = wareService.getProduct(sh.getProductId());
//	                product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
//	                set = "status = " + StockHistoryBean.DEALED
//	                        + ", remark = '操作前" + ProductStockBean.getAreaName(bean.getArea()) + "库存" + product.getStock(bean.getArea(), ProductStockBean.STOCKTYPE_RETURN) 
//	                        + ",操作后库存" + (product.getStock(bean.getArea(), ProductStockBean.STOCKTYPE_RETURN) + sh.getStockBj()) 
//	                        + "', deal_datetime = now()";
//	                service.updateStockHistory(set, "id = " + sh.getId());
//
//	                // 需要计算一下 库存价格
//	                voOrder order = wareService.getOrder("code='" + bean.getOrderCode() + "'");
//	                float price5 = 0;
//	                voOrderProduct orderProduct = wareService.getOrderProductSplit(order.getId(), product.getCode());
//	                if(orderProduct == null){
//	                	orderProduct = wareService.getOrderPresentSplit(order.getId(), product.getCode());
//	                }
//	                
//	                //出库库存价丢失补救****
//	                if(orderProduct.getPrice3() == 0){
//	                	
//	                	//获取出货前最后一条进销存记录
//	                	int outId = service.getNumber("id", "stock_card", null, "code = '"+bean.getOrderCode()+"' and card_type = "+StockCardBean.CARDTYPE_ORDERSTOCK+" and product_id = "+sh.getProductId());
//	                	int scId = service.getNumber("id", "stock_card", "max", "id < "+outId+" and product_id = "+sh.getProductId());
//	                	StockCardBean stockCard = psService.getStockCard("id = "+scId);
//	                	orderProduct.setPrice3(stockCard.getStockPrice());
//	                	
//	                }
//	                
//	                if(orderProduct != null){
//	                	int totalCount = product.getStockAll() + product.getLockCountAll();
//	                	StockBatchLogBean batchLog = service.getStockBatchLog("code='"+order.getCode()+"' and product_id="+orderProduct.getProductId());
//	                	if(batchLog==null){
//	                	}
//	                	price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * sh.getStockBj())) / (totalCount + sh.getStockBj()) * 1000))/1000;
//	                }
//
//	                // 更新库存
//	                //psService.updateProductStock("stock=(stock + " + (sh.getStockBj() + sh.getStockGd()) + ")", "product_id=" + sh.getProductId() + " and area=" + bean.getArea() + " and type=" + ProductStockBean.STOCKTYPE_RETURN);
//	                ProductStockBean ps = psService.getProductStock("product_id=" + sh.getProductId() + " and area=" + bean.getArea() + " and type=" + ProductStockBean.STOCKTYPE_RETURN);
//	                if(ps == null){
//	                	request.setAttribute("tip", "没有找到产品库存，操作失败！");
//	                    request.setAttribute("result", "failure");
//	                    return;
//	                }
//	                if(!psService.updateProductStockCount(ps.getId(), (sh.getStockBj() + sh.getStockGd()))){
//	                	request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
//	                    request.setAttribute("result", "failure");
//	                    return;
//	                }
//	                
//	                //更新货位库存2011-04-22
//					CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ps.getArea());
//					CargoProductStockBean cps = null;
//					List cocList = cargoService.getCargoAndProductStockList(
//										"ci.stock_type = "+ps.getType()+" and ci.area_id = "+inCargoArea.getId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2+
//										" and cps.product_id = "+sh.getProductId(), -1, -1, "ci.id desc");
//					if(cocList == null || cocList.size() == 0){//产品首次入库，无暂存区绑定货位库存信息
//						CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ps.getType()+" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2);
//						if(cargo == null){
//							request.setAttribute("tip", "目的退货库缓存区货位未设置，请先添加后再完成入库！");
//							request.setAttribute("result", "failure");
//							service.getDbOp().rollbackTransaction();
//							return;
//						}
//						cps = new CargoProductStockBean();
//						cps.setCargoId(cargo.getId());
//						cps.setProductId(sh.getProductId());
//						cps.setStockCount(sh.getStockBj() + sh.getStockGd());
//						cargoService.addCargoProductStock(cps);
//						cps.setId(cargoService.getDbOp().getLastInsertId());
//						
//						cargoService.updateCargoInfo("status = 0", "id = "+cargo.getId());
//					}else{
//						cps = (CargoProductStockBean)cocList.get(0);
//						cargoService.updateCargoProductStockCount(cps.getId(), (sh.getStockBj() + sh.getStockGd()));
//					}
//	                
//	                
//	                //获取相关批次信息，处理退货库批次
//	                int stockinCount = sh.getStockBj();
//	                int batchCount = 0;
//	                double stockinPrice = 0;
//	                List batchLogList = reservice.getStockBatchLogList("code='"+order.getCode()+"' and product_id="+sh.getProductId()+" and remark = '订单出货'", -1, -1, "id desc");
//	                if(batchLogList==null||batchLogList.size()==0){
//
//	                	String code = "X"+DateUtil.getNow().substring(0,10).replace("-", "");
//	                	StockBatchBean newBatch;
//	                	newBatch = service.getStockBatch("code like '" + code + "%' and product_id="+sh.getProductId());
//	                	if(newBatch == null){
//	                		//当日第一份批次记录，编号最后三位 001
//	                		code += "001";
//	                	}else {
//	                		//获取当日计划编号最大值
//	                		newBatch = service.getStockBatch("code like '" + code + "%' and product_id="+sh.getProductId()+" order by id desc limit 1"); 
//	                		String _code = newBatch.getCode();
//	                		int number = Integer.parseInt(_code.substring(_code.length()-3));
//	                		number++;
//	                		code += String.format("%03d",new Object[]{new Integer(number)});
//	                	}
//	                	int ticket = 0;	//如没有原始批次则新批次按有票记-0
//	                	int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), sh.getProductId(), ticket);
//	                	newBatch = new StockBatchBean();
//	                	newBatch.setCode(code);
//	                	newBatch.setProductId(sh.getProductId());
//	                	newBatch.setProductStockId(ps.getId());
//	                	newBatch.setStockArea(bean.getArea());
//	                	newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//	                	newBatch.setProductStockId(ps.getId());
//	                	newBatch.setCreateDateTime(DateUtil.getNow());
//	                	newBatch.setPrice(orderProduct.getPrice3());
//	                	newBatch.setBatchCount(stockinCount);
//	                	newBatch.setTicket(ticket);	
//
//	                	service.addStockBatch(newBatch);
//
//	                	//添加批次操作记录
//	                	StockBatchLogBean batchLog = new StockBatchLogBean();
//	                	batchLog.setCode(bean.getOrderCode());
//	                	batchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//	                	batchLog.setStockArea(bean.getArea());
//	                	batchLog.setBatchCode(newBatch.getCode());
//	                	batchLog.setBatchCount(stockinCount);
//	                	batchLog.setBatchPrice(newBatch.getPrice());
//	                	batchLog.setProductId(newBatch.getProductId());
//	                	batchLog.setRemark("退货入库");
//	                	batchLog.setCreateDatetime(DateUtil.getNow());
//	                	batchLog.setUserId(admin.getId());
//	                	service.addStockBatchLog(batchLog);
//	                	
//	                	stockinPrice = batchLog.getBatchCount()*batchLog.getBatchPrice();
//	                	
//	                	//财务产品信息表---liuruilan-----2012-11-01-----
//    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh.getProductId());
//    					if(fProduct == null){
//							request.setAttribute("tip", "查询异常，请与管理员联系！");
//							request.setAttribute("result", "failure");
//							return;
//						}
//    					int totalCount = product.getStockAll() + product.getLockCountAll();
//						float priceSum = Arith.mul(price5, totalCount);
//						
//						//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
//						float priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(batchLog.getBatchPrice(), stockinCount)), Arith.add(_count, stockinCount)), 2);
//						float priceSumHasticket = Arith.mul(priceHasticket,  stockinCount + _count);
//						set = "price =" + price5 + ", price_sum =" + priceSum + ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
//						frfService.updateFinanceProductBean(set, "product_id = " + product.getId());
//						
//					
//						
//						
//						//财务进销存卡片
//						product.setPsList(psService.getProductStockList("product_id=" +sh.getProductId(), -1, -1, null));
//    					int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), batchLog.getStockType(), ticket, sh.getProductId());
//    					int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, batchLog.getStockType(), ticket,sh.getProductId());
//						int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), -1,ticket,sh.getProductId());
//    					FinanceStockCardBean fsc = new FinanceStockCardBean();
//    					fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//    					fsc.setCode(order.getCode());
//    					fsc.setCreateDatetime(DateUtil.getNow());
//    					fsc.setStockType(batchLog.getStockType());
//    					fsc.setStockArea(batchLog.getStockArea());
//    					fsc.setProductId(sh.getProductId());
//    					fsc.setStockId(ps.getId());
//    					fsc.setStockInCount(-batchLog.getBatchCount());	
//    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//    					fsc.setStockAllArea(stockAllArea);
//    					fsc.setStockAllType(stockAllType);
//    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//    					fsc.setStockPrice(price5);
//    					
//    					fsc.setType(fsc.getCardType());
//    					fsc.setIsTicket(ticket);
//    					fsc.setStockBatchCode(batchLog.getBatchCode());
//    					fsc.setBalanceModeStockCount(stockinCount + _count);
//    					fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(batchLog.getBatchPrice(), stockinCount))));
//						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//    					double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());	//因为StockInPriceSum是负号，故用sub
//    					fsc.setAllStockPriceSum(tmpPrice);
//    					frfService.addFinanceStockCardBean(fsc);
//    					
//    					//将订单商品写入销售商品信息表--商品
//    					int supplierId = FinanceSellProductBean.querySupplier(db, batchLog.getBatchCode());
//    					//String sql1="select * from finance_sell where data_type=0 and order_id=?";
//    					//查找finance_sell表有没有销售单
//    					FinanceSellBean fsbean= frfService.getFinanceSellBean(" data_type=0 and order_id="+order.getId());
//    					FinanceSellBean fsbean1=null;
//    					List fspList=null;//销售订单里面所有产品
//    					
//    					if(fsbean!=null&&fsbean.getId()!=0){
//    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
//    						//如果已经有了销售未妥投订单不做任务操作，
//    						if(fsbean1!=null&&fsbean1.getId()!=0){
//    							
//    							//否则向finance_sell,finance_sell_product表添加
//    						}else{
//    							fsbean.setId(0);
//    							fsbean.setCreateDatetime(DateUtil.getNow());
//    							fsbean.setDataType(1);
//    							fsbean.setCount(-1);
//    							fsbean.setCarriage(-order.getPostage());
//    							fsbean.setPrice(Arith.round(Arith.mul(fsbean.getPrice(),-1),2));
//    							frfService.addFinanceSellBean(fsbean);//向finance_sell表添加未妥投退货记录
//    							int financeSellId=frfService.getDbOp().getLastInsertId();
//    							fspList=frfService.getFinanceSellProductBeanList(" (data_type=1 or data_type=0) and order_id="+order.getId(), -1, -1, null);
//    							if(fspList!=null&&fspList.size()>0){
//    								FinanceSellProductBean fspbean=null;
//    								for(int i=0;i<fspList.size();i++){
//    									fspbean=(FinanceSellProductBean)fspList.get(i);
//    									fspbean.setId(0);
//    									fspbean.setFinanceSellId(financeSellId);
//    									fspbean.setBuyCount(-fspbean.getBuyCount());
//    									if(fspbean.getDataType()==0){
//    										fspbean.setDataType(2);
//    									}
//    									if(fspbean.getDataType()==1){
//    										fspbean.setDataType(3);
//    									}
//    									fspbean.setCreateDatetime(DateUtil.getNow());
//    									frfService.addFinanceSellProductBean(fspbean);//向finance_sell_product表添加未妥投退货记录
//    									
//    								}
//    							}
//    						}
//    					}else{
//    						//如果finance_sell表中没有找到销售订单，要查看一下finance_sell表里面有没有未妥投退单，如果有则什么也没做
//    						//如果没有则向否则向finance_sell,finance_sell_product表添加记录
//    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
//    						if(fsbean1!=null&&fsbean1.getId()!=0){
//    							
//    							//否则向finance_sell,finance_sell_product表添加
//    						}else{
//    							
//    							int deliverType = 0;
//		    					if(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()) != null){
//		    						deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()).toString());
//		    					}
//		    					FinanceSellBean fsBean = new FinanceSellBean();
//		    					fsBean.setOrderId(order.getId());
//		    					fsBean.setCode(order.getCode());
//		    					fsBean.setPrice(-Arith.sub(order.getDprice(), order.getPostage()));	//退货金额为负
//		    					fsBean.setCarriage(-order.getPostage());	//“运费”和订单符号一致
//		    					fsBean.setCharge(0);
//		    					fsBean.setBuyMode(order.getBuyMode());
//		    					fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
//		    					fsBean.setDeliverType(deliverType);
//		    					fsBean.setCreateDatetime(DateUtil.getNow());
//		    					fsBean.setPackageNum(order.getPackageNum());
//		    					fsBean.setDataType(1);	//1-未妥投退回
//		    					fsBean.setCount(-1);
//		    					frfService.addFinanceSellBean(fsBean);
//    							int financeSellId=frfService.getDbOp().getLastInsertId();
//    							
//    							String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//    								+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
//    								+ "WHERE h.order_id = ? ";
//		    					
//		    					db.prepareStatement(sql);
//		    					pstmt = db.getPStmt();
//		    					pstmt.setInt(1, order.getId());
//		    					rs = pstmt.executeQuery();
//		    					while(rs.next()){
//		    						int pId = rs.getInt("id");
//		    					//	if(batchLog.getProductId() == pId){
//		    							
////		    							int _batchCount = batchLog.getBatchCount();
////										String batchCode = batchLog.getBatchCode();
////										StockBatchBean batch = service.getStockBatch("code = '" + batchCode + "'");
////										if( buyCount> batchCount){
////											buyCount -= batchCount;
////										}else{
////											buyCount = 0;
////										}
////										if(batch.getTicket() == 0){
////											price5 = fProduct.getPriceHasticket();
////										}
////										if(batch.getTicket() == 1){
////											price5 = fProduct.getPriceNoticket();
////										}
//										
//		    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//		    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//		    						db.prepareStatement(sql);
//		    						pstmt = db.getPStmt();
//		    						pstmt.setInt(1,pId);
//		    						rs0 = pstmt.executeQuery();
//		    						
//		    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
//		    						fspBean.setOrderId(order.getId());
//		    						fspBean.setProductId(rs.getInt("id"));
//		    						fspBean.setBuyCount(-rs.getInt("count"));
//		    						fspBean.setPrice(rs.getFloat("price"));
//		    						fspBean.setDprice(rs.getFloat("dprice"));
//		    						fspBean.setFinanceSellId(financeSellId);
//		    						fspBean.setPrice5(rs.getFloat("price5"));
//		    						if(rs0.next()){
//		    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//		    						}
//		    						fspBean.setParentId1(rs.getInt("parent_id1"));
//		    						fspBean.setParentId2(rs.getInt("parent_id2"));
//		    						fspBean.setParentId3(rs.getInt("parent_id3"));
//		    						fspBean.setCreateDatetime(DateUtil.getNow());
//		    						fspBean.setDataType(2);	//2-商品未妥投退回
//		    						fspBean.setBalanceMode(0);
//		    						fspBean.setSupplierId(supplierId);
//		    						
//		    						frfService.addFinanceSellProductBean(fspBean);
//		    						//}
//		    					}
//					
//							//赠品
//								sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//									+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
//									+ "WHERE h.order_id = ? ";
//								db.prepareStatement(sql);
//								pstmt = db.getPStmt();
//								pstmt.setInt(1, order.getId());
//								rs = pstmt.executeQuery();
//		    					while(rs.next()){
//		    						int pId = rs.getInt("id");
//		    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//		    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//		    						db.prepareStatement(sql);
//		    						pstmt = db.getPStmt();
//		    						pstmt.setInt(1,pId);
//		    						rs0 = pstmt.executeQuery();
//		    						
//		    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
//		    						fspBean.setOrderId(order.getId());
//		    						fspBean.setProductId(rs.getInt("id"));
//		    						fspBean.setBuyCount(-rs.getInt("count"));
//		    						fspBean.setPrice(rs.getFloat("price"));
//		    						fspBean.setDprice(rs.getFloat("dprice"));
//		    						fspBean.setFinanceSellId(financeSellId);
//		    						fspBean.setPrice5(rs.getFloat("price5"));
//		    						if(rs0.next()){
//		    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//		    						}
//		    						fspBean.setParentId1(rs.getInt("parent_id1"));
//		    						fspBean.setParentId2(rs.getInt("parent_id2"));
//		    						fspBean.setParentId3(rs.getInt("parent_id3"));
//		    						fspBean.setCreateDatetime(DateUtil.getNow());
//		    						fspBean.setDataType(3);	//3-赠品未妥投退回
//		    						fspBean.setBalanceMode(0);
//		    						fspBean.setSupplierId(supplierId);
//		    						
//		    						frfService.addFinanceSellProductBean(fspBean);
//		    					}
//		    					
//    							
//    						}
//    						
//    					}
//    					//-------------liuruilan-------------
//	                }else{
//	                	Iterator batchIter = batchLogList.listIterator();
//                		
//	                	while(batchIter.hasNext()&&stockinCount>0){
//	                		StockBatchLogBean batchLog = (StockBatchLogBean)batchIter.next(); 
//	                		StockBatchBean batch = reservice.getStockBatch("code = '"+batchLog.getBatchCode()+"' and product_id="+batchLog.getProductId()+" and stock_type="+ProductStockBean.STOCKTYPE_RETURN+" and stock_area="+bean.getArea());
//	                		int ticket = FinanceSellProductBean.queryTicket(redb, batchLog.getBatchCode());	//是否含票 
//	                		if(ticket == -1){
//	    						request.setAttribute("tip", "查询异常，请与管理员联系！");
//	    						request.setAttribute("result", "failure");
//	    						return;
//	    					}
//	                		int _count = FinanceProductBean.queryCountIfTicket(db, sh.getProductId(), ticket);
//	                		if(batch!=null){
//	                			if(stockinCount<=batchLog.getBatchCount()){
//	                				service.updateStockBatch("batch_count = batch_count+"+stockinCount, "id="+batch.getId());
//	                				batchCount = stockinCount;
//	                				stockinCount = 0;
//	                			}else{
//	                				service.updateStockBatch("batch_count = batch_count+"+batchLog.getBatchCount(), "id="+batch.getId());
//	                				stockinCount -= batchLog.getBatchCount();
//	                				batchCount = batchLog.getBatchCount();
//	                			}
//	                			
//	                			
//	                		}else{
//	                			StockBatchBean newBatch = new StockBatchBean();
//	                			newBatch.setCode(batchLog.getBatchCode());
//	                			newBatch.setProductId(sh.getProductId());
//	                			newBatch.setProductStockId(ps.getId());
//	                			newBatch.setStockArea(bean.getArea());
//	                			newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//	                			newBatch.setProductStockId(ps.getId());
//	                			newBatch.setCreateDateTime(service.getStockBatchCreateDatetime(batchLog.getBatchCode(),batchLog.getProductId()));
//	                			newBatch.setPrice(batchLog.getBatchPrice());
//	                			newBatch.setTicket(ticket);
//
//	                			if(stockinCount<=batchLog.getBatchCount()){
//	                				newBatch.setBatchCount(stockinCount);
//	                				batchCount = stockinCount;
//	                				stockinCount = 0;
//	                			}else{
//	                				newBatch.setBatchCount(batchLog.getBatchCount());
//	                				stockinCount -= batchLog.getBatchCount();
//	                				batchCount = batchLog.getBatchCount();
//	                			}
//
//	                			service.addStockBatch(newBatch);
//	                		}
//	                		
//	                		stockinPrice = stockinPrice + batchLog.getBatchPrice()*batchCount;
//
//	                		//添加批次操作记录
//	                		StockBatchLogBean newBatchLog = new StockBatchLogBean();
//	                		newBatchLog.setCode(bean.getOrderCode());
//	                		newBatchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//	                		newBatchLog.setStockArea(bean.getArea());
//	                		newBatchLog.setBatchCode(batchLog.getBatchCode());
//	                		newBatchLog.setBatchCount(batchCount);
//	                		newBatchLog.setBatchPrice(batchLog.getBatchPrice());
//	                		newBatchLog.setProductId(batchLog.getProductId());
//	                		newBatchLog.setRemark("退货入库");
//	                		newBatchLog.setCreateDatetime(DateUtil.getNow());
//	                		newBatchLog.setUserId(admin.getId());
//	                		service.addStockBatchLog(newBatchLog);
//	                		
//	                		//财务产品信息表---liuruilan-----2012-11-01-----
//	    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh.getProductId());
//	    					if(fProduct == null){
//								request.setAttribute("tip", "查询异常，请与管理员联系！");
//								request.setAttribute("result", "failure");
//								return;
//							}
//	    					int totalCount = product.getStockAll() + product.getLockCountAll();
//							float priceSum = Arith.mul(price5, totalCount);
//							float priceHasticket = 0;
//							float priceNoticket = 0;
//							float priceSumHasticket = 0;
//							float priceSumNoticket = 0;
//							set = "price =" + price5 + ", price_sum =" + priceSum;
//							float sqlPrice5=0;
//							if(ticket == 0){	//0-有票
//								//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
//								String sqlPrice="select price5 from finance_sell_product where product_id="+batchLog.getProductId()+" and data_type=0 and balance_mode="+ticket+" and order_id="+order.getId();
//								reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//								rers=reps.executeQuery();
//								boolean flag=false;//为false,finance_sell_product 没有记录，为true,finance_sell_product有记录
//								while(rers.next()){
//									flag=true;
//									sqlPrice5=rers.getFloat(1);
//								}
//								if(!flag){
//									//如果finance_sell_productm 没有记录要在user_order_product_split_history里面找
//									sqlPrice="select price5 from user_order_product_split_history psplit where product_id="+batchLog.getProductId()+" and order_id="+order.getId();
//									reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//									rers=reps.executeQuery();
//									while(rers.next()){
//										flag=true;
//										sqlPrice5=rers.getFloat("price5");
//									}
//									if(!flag){//如果user_order_product_split_history 没有记录要在user_order_present_split_history里面找
//										sqlPrice="select price5 from user_order_present_split_history psplit where product_id="+batchLog.getProductId()+" and order_id="+order.getId();
//										reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//										rers=reps.executeQuery();
//										while(rers.next()){
//											flag=true;
//											sqlPrice5=rers.getFloat("price5");
//										}
//									}
//								}
//								//-------------------------------获得出库价end --------------------------------------------
//								priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(sqlPrice5, batchCount)), Arith.add(_count, batchCount)), 2);
//								priceSumHasticket = Arith.mul(priceHasticket,  batchCount + _count);
//								set += ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
//							}
//							if(ticket == 1){	//1-无票
//								//获得出库金额
//								String sqlPrice="select price5 from finance_sell_product where product_id="+batchLog.getProductId()+" and data_type=0 and balance_mode="+ticket+" and order_id="+order.getId();
//								reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//								rers=reps.executeQuery();
//								while(rers.next()){
//									sqlPrice5=rers.getFloat(1);
//								}
//								
//								priceNoticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumNoticket(), Arith.mul(batchLog.getBatchPrice(), batchCount)), Arith.add(_count, batchCount)), 2);
//								priceSumNoticket = Arith.mul(priceNoticket,  batchCount + _count);
//								set += ", price_noticket =" + priceNoticket + ", price_sum_noticket =" + priceSumNoticket;
//							}
//							frfService.updateFinanceProductBean(set, "product_id = " + product.getId());
//							
//							
//							//财务进销存卡片
//	    					int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatchLog.getStockArea(), newBatchLog.getStockType(), ticket, sh.getProductId());
//	    					int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, newBatchLog.getStockType(), ticket,sh.getProductId());
//							int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatchLog.getStockArea(), -1,ticket,sh.getProductId());
//							product.setPsList(psService.getProductStockList("product_id=" +sh.getProductId(), -1, -1, null));
//							FinanceStockCardBean fsc = new FinanceStockCardBean();
//	    					fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//	    					fsc.setCode(order.getCode());
//	    					fsc.setCreateDatetime(DateUtil.getNow());
//	    					fsc.setStockType(newBatchLog.getStockType());
//	    					fsc.setStockArea(newBatchLog.getStockArea());
//	    					fsc.setProductId(sh.getProductId());
//	    					fsc.setStockId(ps.getId());
//	    					fsc.setStockInCount(-newBatchLog.getBatchCount());	
//	    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//	    					fsc.setStockAllArea(stockAllArea);
//	    					fsc.setStockAllType(stockAllType);
//	    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//	    					fsc.setStockPrice(price5);
//	    					
//	    					fsc.setType(fsc.getCardType());
//	    					fsc.setIsTicket(ticket);
//	    					fsc.setStockBatchCode(batchLog.getBatchCode());
//	    					fsc.setBalanceModeStockCount(batchCount + _count);
//	    					fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(sqlPrice5, batchCount))));
//	    					if(ticket == 0){
//	    						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//	    					}
//	    					if(ticket == 1){
//	    						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceNoticket)));
//	    					}
//	    					double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//	    					fsc.setAllStockPriceSum(tmpPrice);
//	    					frfService.addFinanceStockCardBean(fsc);
//	    					
//	    					//将订单商品写入销售商品信息表--商品
//	    					FinanceSellBean fsbean= frfService.getFinanceSellBean(" data_type=0 and order_id="+order.getId());
//	    					FinanceSellBean fsbean1=null;
//	    					List fspList=null;//销售订单里面所有产品
//	    					
//	    					if(fsbean!=null&&fsbean.getId()!=0){
//	    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
//	    						//如果已经有了销售未妥投订单不做任务操作，
//	    						if(fsbean1!=null&&fsbean1.getId()!=0){
//	    							
//	    							//否则向finance_sell,finance_sell_product表添加
//	    						}else{
//	    							fsbean.setId(0);
//	    							fsbean.setCreateDatetime(DateUtil.getNow());
//	    							fsbean.setDataType(1);
//	    							fsbean.setCount(-1);
//	    							fsbean.setCarriage(-order.getPostage());
//	    							fsbean.setPrice(Arith.round(Arith.mul(fsbean.getPrice(),-1),2));
//	    							frfService.addFinanceSellBean(fsbean);//向finance_sell表添加未妥投退货记录
//	    							int financeSellId=frfService.getDbOp().getLastInsertId();
//	    							fspList=frfService.getFinanceSellProductBeanList(" (data_type=1 or data_type=0) and order_id="+order.getId(), -1, -1, null);
//	    							if(fspList!=null&&fspList.size()>0){
//	    								FinanceSellProductBean fspbean=null;
//	    								for(int i=0;i<fspList.size();i++){
//	    									fspbean=(FinanceSellProductBean)fspList.get(i);
//	    									fspbean.setId(0);
//	    									fspbean.setFinanceSellId(financeSellId);
//	    									fspbean.setBuyCount(-fspbean.getBuyCount());
//	    									if(fspbean.getDataType()==0){
//	    										fspbean.setDataType(2);
//	    									}
//	    									if(fspbean.getDataType()==1){
//	    										fspbean.setDataType(3);
//	    									}
//	    									fspbean.setCreateDatetime(DateUtil.getNow());
//	    									frfService.addFinanceSellProductBean(fspbean);//向finance_sell_product表添加未妥投退货记录
//	    									
//	    								}
//	    							}
//	    						}
//	    					}else{
//	    						//如果finance_sell表中没有找到销售订单，要查看一下finance_sell表里面有没有未妥投退单，如果有则什么也没做
//	    						//如果没有则向否则向finance_sell,finance_sell_product表添加记录
//	    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+order.getId());
//	    						if(fsbean1!=null&&fsbean1.getId()!=0){
//	    							
//	    							//否则向finance_sell,finance_sell_product表添加
//	    						}else{
//	    							
//	    							
//	    							
//	    							int deliverType = 0;
//			    					if(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()) != null){
//			    						deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()).toString());
//			    					}
//			    					FinanceSellBean fsBean = new FinanceSellBean();
//			    					fsBean.setOrderId(order.getId());
//			    					fsBean.setCode(order.getCode());
//			    					fsBean.setPrice(-Arith.sub(order.getDprice(), order.getPostage()));	//退货金额为负
//			    					fsBean.setCarriage(-order.getPostage());	//“运费”和订单符号一致
//			    					fsBean.setCharge(0);
//			    					fsBean.setBuyMode(order.getBuyMode());
//			    					fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
//			    					fsBean.setDeliverType(deliverType);
//			    					fsBean.setCreateDatetime(DateUtil.getNow());
//			    					fsBean.setPackageNum(order.getPackageNum());
//			    					fsBean.setDataType(1);	//1-未妥投退回
//			    					fsBean.setCount(-1);
//			    					frfService.addFinanceSellBean(fsBean);
//			    					int financeSellId=frfService.getDbOp().getLastInsertId();
//	    							
//	    								int supplierId = FinanceSellProductBean.querySupplier(db, batchLog.getBatchCode());
//				    					String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//				    								+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
//				    								+ "WHERE h.order_id = ? ";
//				    					db.prepareStatement(sql);
//				    					pstmt = db.getPStmt();
//				    					pstmt.setInt(1, order.getId());
//				    					rs = pstmt.executeQuery();
//				    					while(rs.next()){
//				    						int pId = rs.getInt("id");
//				    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//				    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//				    						db.prepareStatement(sql);
//				    						pstmt = db.getPStmt();
//				    						pstmt.setInt(1,pId);
//				    						rs0 = pstmt.executeQuery();
//				    						
//				    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
//				    						fspBean.setOrderId(order.getId());
//				    						fspBean.setProductId(rs.getInt("id"));
//				    						fspBean.setFinanceSellId(financeSellId);
//				    						fspBean.setBuyCount(-rs.getInt("count"));
//				    						fspBean.setPrice(rs.getFloat("price"));
//				    						fspBean.setDprice(rs.getFloat("dprice"));
//				    						fspBean.setPrice5(rs.getFloat("price5"));
//				    						if(rs0.next()){
//				    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//				    						}
//				    						fspBean.setParentId1(rs.getInt("parent_id1"));
//				    						fspBean.setParentId2(rs.getInt("parent_id2"));
//				    						fspBean.setParentId3(rs.getInt("parent_id3"));
//				    						fspBean.setCreateDatetime(DateUtil.getNow());
//				    						fspBean.setDataType(2);	//2-商品未妥投退回
//				    						fspBean.setBalanceMode(ticket);
//				    						fspBean.setSupplierId(supplierId);
//				    						
//				    						frfService.addFinanceSellProductBean(fspBean);
//				    					}
//				    					
//				    					//赠品
//				    					sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//				    						+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
//				    						+ "WHERE h.order_id = ? ";
//				    					db.prepareStatement(sql);
//				    					pstmt = db.getPStmt();
//				    					pstmt.setInt(1, order.getId());
//				    					rs = pstmt.executeQuery();
//				    					while(rs.next()){
//				    						int pId = rs.getInt("id");
//				    						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//				    								"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//				    						db.prepareStatement(sql);
//				    						pstmt = db.getPStmt();
//				    						pstmt.setInt(1,pId);
//				    						rs0 = pstmt.executeQuery();
//				    						
//				    						FinanceSellProductBean fspBean = new FinanceSellProductBean();
//				    						fspBean.setOrderId(order.getId());
//				    						fspBean.setProductId(rs.getInt("id"));
//				    						fspBean.setBuyCount(-rs.getInt("count"));
//				    						fspBean.setPrice(rs.getFloat("price"));
//				    						fspBean.setDprice(rs.getFloat("dprice"));
//				    						fspBean.setFinanceSellId(financeSellId);
//				    						fspBean.setPrice5(rs.getFloat("price5"));
//				    						if(rs0.next()){
//				    							fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//				    						}
//				    						fspBean.setParentId1(rs.getInt("parent_id1"));
//				    						fspBean.setParentId2(rs.getInt("parent_id2"));
//				    						fspBean.setParentId3(rs.getInt("parent_id3"));
//				    						fspBean.setCreateDatetime(DateUtil.getNow());
//				    						fspBean.setDataType(3);	//3-赠品未妥投退回
//				    						fspBean.setBalanceMode(ticket);
//				    						fspBean.setSupplierId(supplierId);
//				    						
//				    						frfService.addFinanceSellProductBean(fspBean);
//				    					}
//				    					
//	    					//-------------liuruilan-------------
//	    						}
//	    					}
//	                	}
//	                }
//                //	int totalCount = product.getStock(ProductStockBean.AREA_BJ) + product.getStock(ProductStockBean.AREA_GF) + product.getStock(ProductStockBean.AREA_GS) + product.getLockCount(ProductStockBean.AREA_BJ) + product.getLockCount(ProductStockBean.AREA_GF) + product.getLockCount(ProductStockBean.AREA_GS);
//                	int totalCount = product.getStockAll() + product.getLockCountAll();
////                	price5 = ((float)Math.round((product.getPrice5() * totalCount + stockinPrice) /(totalCount + sh.getStockBj()) * 1000))/1000;
//                	price5 = ((float)Math.round((product.getPrice5() * totalCount + (orderProduct.getPrice3() * sh.getStockBj())) / (totalCount + sh.getStockBj()) * 1000))/1000;
//					service.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId());
//
//	                //log记录
//	                StockAdminHistoryBean log = new StockAdminHistoryBean();
//	                log.setAdminId(admin.getId());
//	                log.setAdminName(admin.getUsername());
//	                log.setLogId(bean.getId());
//	                log.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
//	                log.setOperDatetime(DateUtil.getNow());
//	                log.setRemark("退货入库操作：" + bean.getName() + ",将(" + product.getName() + ")入库");
//	                log.setType(StockAdminHistoryBean.CHANGE);
//	                service.addStockAdminHistory(log);
//
//					// 审核通过，就加 进销存卡片
//					product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
//					cps = cargoService.getCargoAndProductStock("cps.id = "+cps.getId());
//
//					// 入库卡片
//					StockCardBean sc = new StockCardBean();
//					int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
//					sc.setId(scId);
//					sc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//					//sc.setCode(bean.getCode());
//					sc.setCode(order.getCode());
//					sc.setCreateDatetime(DateUtil.getNow());
//					sc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//					sc.setStockArea(bean.getArea());
//					sc.setProductId(sh.getProductId());
//					sc.setStockId(ps.getId());
//					sc.setStockInCount(sh.getStockBj());
////					sc.setStockInPriceSum(stockinPrice);
//					sc.setStockInPriceSum((new BigDecimal(sh.getStockBj())).multiply(new BigDecimal(StringUtil.formatDouble2(orderProduct.getPrice3()))).doubleValue());
//					sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
//					sc.setStockAllArea(product.getStock(bean.getArea()) + product.getLockCount(bean.getArea()));
//					sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
//					sc.setAllStock(product.getStockAll() + product.getLockCountAll());
//					sc.setStockPrice(price5);
//					sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
//					psService.addStockCard(sc);
//					
//					
//					
//					//货位入库卡片
//					CargoStockCardBean csc = new CargoStockCardBean();
//					csc.setCardType(CargoStockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//					csc.setCode(order.getCode());
//					csc.setCreateDatetime(DateUtil.getNow());
//					csc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//					csc.setStockArea(bean.getArea());
//					csc.setProductId(sh.getProductId());
//					csc.setStockId(cps.getId());
//					csc.setStockInCount(sh.getStockBj());
//					csc.setStockInPriceSum((new BigDecimal(sh.getStockBj())).multiply(new BigDecimal(StringUtil.formatDouble2(orderProduct.getPrice3()))).doubleValue());
//					csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
//					csc.setAllStock(product.getStockAll() + product.getLockCountAll());
//					csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
//					csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
//					csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
//					csc.setStockPrice(price5);
//					csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
//					cargoService.addCargoStockCard(csc);
//					
//					//将订单数据写入发货信息表（财务统计用）---liuruilan---2012-9-13-----
////					FinanceSellBean fsBean = new FinanceSellBean();
////					fsBean.setOrderId(order.getId());
////					fsBean.setCode(order.getCode());
////					fsBean.setPrice(-order.getDprice());	//退货金额为负
////					fsBean.setCarriage(-order.getPostage());	//“运费”和订单符号一致
////					fsBean.setCharge(0);
////					fsBean.setBuyMode(order.getBuyMode());
////					fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
////					fsBean.setDeliverType(order.getDeliver());
////					fsBean.setCreateDatetime(DateUtil.getNow());
////					fsBean.setPackageNum(order.getPackageNum());
////					fsBean.setDataType(1);	//1-未妥投退回
////					fsBean.setCount(-1);
////					frfService.addFinanceSellBean(fsBean);
//					// ---------liuruilan-----2012-09-13-------
//					
//	                // 如果参数包含产品ID，则只进行一次入库操作。
//	                if(productId > 0){
//	                	break;
//	                }
//	            }
//
//	            if (count == 0) {
//	                request.setAttribute("tip", "该操作没有任何库存变动，不能执行！");
//	                request.setAttribute("result", "failure");
//	                return;
//	            }
//
//	            condition = "oper_id = " + bean.getId() + " and status = "
//	                    + StockHistoryBean.UNDEAL + " and stock_type=" + StockHistoryBean.IN;
//	            //都已经处理
//	            if (service.getStockHistoryCount(condition) == 0) {
//	                service.updateStockOperation("status = " + StockOperationBean.STATUS2 + ", last_oper_time = now()", "id = " + operId);
//	                //service.updateStockOperation("status = " + StockOperationBean.STATUS5 , " status=" + StockOperationBean.STATUS3 + " and order_code = '" + bean.getOrderCode() + "'");
//	                //service.updateOrderStock("status = " + OrderStockBean.STATUS5 , " status=" + OrderStockBean.STATUS3 + " and order_code = '" + bean.getOrderCode() + "'");
//	                // 退货操作后，把订单出货状态设置为 用户退货 
//	                service.updateOrderStock("status = " + OrderStockBean.STATUS9 , " status=" + OrderStockBean.STATUS3 + " and order_code = '" + bean.getOrderCode() + "'");
//	                service.getDbOp().executeUpdate("update user_order set status=11, stockout=0 where code = '" + bean.getOrderCode() + "' ");
//	                service.getDbOp().executeUpdate("update user_order set balance_status=3, stockout_deal=3 where code = '" + bean.getOrderCode() + "' ");
//
//	                //log记录
//	                StockAdminHistoryBean log = new StockAdminHistoryBean();
//	                log.setAdminId(admin.getId());
//	                log.setAdminName(admin.getUsername());
//	                log.setLogId(bean.getId());
//	                log.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
//	                log.setOperDatetime(DateUtil.getNow());
//	                log.setRemark("完成退货入库操作：" + bean.getName());
//	                log.setType(StockAdminHistoryBean.CHANGE);
//	                service.addStockAdminHistory(log);
//	            }
//
//	            //提交事务
//	            service.getDbOp().commitTransaction();
//	        } catch (Exception e){
//	        	if(!service.getDbOp().getConn().getAutoCommit())
//	        		service.getDbOp().rollbackTransaction();
//	        	e.printStackTrace();
//	        	stockLog.error(StringUtil.getExceptionInfo(e));
//	        } finally {
//	            redb.release();
//	            service.releaseAll();
//	        }
//    	}
//    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2008-2-25
     * 
     * 说明：删除退货入库操作
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void deleteCancelStockin(HttpServletRequest request,
            HttpServletResponse response) {
    	synchronized(stockLock){
	        int operId = StringUtil.StringToId(request.getParameter("id"));
	        IStockService service = ServiceFactory.createStockService(
	                IBaseService.CONN_IN_SERVICE, null);
	        try {
	            StockOperationBean bean = service.getStockOperation("id = "
	                    + operId + " and type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ")");
	            if (bean.getStatus() == StockOperationBean.STATUS2) {
	                request.setAttribute("tip", "该操作已经完成，不能再修改！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            String condition = "oper_id = " + bean.getId() + " and status = "
	                    + StockHistoryBean.DEALED;
	            if (service.getStockHistoryCount(condition) > 0) {
	                request.setAttribute("tip", "该操作已经有部分入库了，不能删除！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            service.getDbOp().startTransaction();
	            service.deleteStockOperation("id = " + operId);
	            service.deleteStockHistory("oper_id = " + operId);
	
	            //log记录
	            voUser admin = (voUser) request.getSession().getAttribute(
	                    IConstants.USER_VIEW_KEY);
	            if (admin == null) {
	                request.setAttribute("tip", "当前没有登录，添加失败！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            int logId = service.getNumber("id", "stock_admin_history", "max",
	                    "id > 0") + 1;
	            StockAdminHistoryBean log = new StockAdminHistoryBean();
	            log.setId(logId);
	            log.setAdminId(admin.getId());
	            log.setAdminName(admin.getUsername());
	            log.setLogId(bean.getId());
	            log.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
	            log.setOperDatetime(DateUtil.getNow());
	            log.setRemark("删除退货入库操作：" + bean.getName());
	            log.setType(StockAdminHistoryBean.DELETE);
	            service.addStockAdminHistory(log);
	
	            service.getDbOp().commitTransaction();
	        } finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 货位系统 入库操作 操作成功
     */
    public static int UPDATE_STOCKPOS_SUCCESS = 0;
    /**
     * 货位系统 入库操作 没有找到存放该商品的货位并且无法申请新的货位
     */
    public static int UPDATE_STOCKPOS_NOPOS = 1;
    /**
     * 货位系统 入库操作 没有找到货区商品匹配信息
     */
    public static int UPDATE_STOCKPOS_NOMATCH = 2;
    /**
     * 货位系统 入库操作 没有找到相应的货区信息
     */
    public static int UPDATE_STOCKPOS_NOTYPE = 3;

    /**
     * 货位系统 出货操作 操作成功
     */
    public static int STOCKPOSOUT_SUCCESS = 0;
    /**
     * 货位系统 出货操作 没有找到存放该商品的货位
     */
    public static int STOCKPOSOUT_NOPOS = 1;
    /**
     * 货位系统 出货操作 没有找到货区商品匹配信息
     */
    public static int STOCKPOSOUT_NOMATCH = 2;
    /**
     * 货位系统 出货操作 没有找到相应的货区
     */
    public static int STOCKPOSOUT_NOTYPE = 3;
    /**
     * 货位系统 出货操作 库存不足
     */
    public static int STOCKPOSOUT_STOCK_NOTENOUGH = 4;
    /**
     * 快速退货——发货清单预览
     */
    public void previewOrderStock(HttpServletRequest request,
            HttpServletResponse response) {
    	voUser admin = (voUser) request.getSession().getAttribute(
	            IConstants.USER_VIEW_KEY);
	    if (admin == null) {
	        request.setAttribute("tip", "当前没有登录，添加失败！");
	        request.setAttribute("result", "failure");
	        return;
	    }
	    UserGroupBean group = admin.getGroup();
		if(!group.isFlag(613)){
			request.setAttribute("tip", "您没有退货包裹入库权限！！");
			request.setAttribute("result", "failure");
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
    			request.setAttribute("tip", "你没有操作当前地区退货库的权限！");
  	            request.setAttribute("result", "failure");
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
    				request.setAttribute("tip", "这个订单不存在！");
      	            request.setAttribute("result", "failure");
      	            return;
    			}
    			if( order.getStatus() == 11 ) {
    				request.setAttribute("tip", "该订单已退回不可再退！");
    				request.setAttribute("result", "failure");
    				return;
    			}
    			apBean=service.getAuditPackage("order_id="+order.getId());
    		}else if(scanType.equals("2")){//扫描的是包裹单号
    			apBean=service.getAuditPackage("package_code='"+packageCode+"'");
    			if (apBean == null) {
     	            request.setAttribute("tip", "这个包裹单不存在！");
     	            request.setAttribute("result", "failure");
     	            return;
     	        }
    			int orderId=apBean.getOrderId();
    			order=wareService.getOrder(orderId);
    			if (order == null) {
     	            request.setAttribute("tip", "这个订单不存在！");
     	            request.setAttribute("result", "failure");
     	            return;
     	        }
    		}
    		orderCode=order.getCode();
 	        //查找订单的出货情况
 	        OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and status<>" + OrderStockBean.STATUS5 + " and order_code = '" + orderCode + "'");
 			
 	        if(oper != null && oper.getStatus() == OrderStockBean.STATUS8) {
				request.setAttribute("tip", "该订单包裹已经退回过！");
				request.setAttribute("result", "failure");
				request.setAttribute("oper", oper);
				return;
			}
 	        if (oper == null || oper.getStatus() != OrderStockBean.STATUS3) {
 				request.setAttribute("tip", "这个订单还没有进行出库操作！");
 			    request.setAttribute("result", "failure");
 			    request.setAttribute("oper", oper);
 			    return;
 			} /*else {
 				//从旧的退换货入库表中 查找订单的退货情况
 			    if(service.getStockOperation("status <> " + StockOperationBean.STATUS2 + " and type in (" + StockOperationBean.CANCEL_STOCKIN + "," + StockOperationBean.CANCEL_EXCHANGE + "," + StockOperationBean.BAD_EXCHANGE + ") and order_code = '" + orderCode + "'") != null){
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
    	} finally {
            service.releaseAll();
        }
    }
    
    /**
     * 快速退货入库
     *    原来的地区 是根据 出库记录来获取的 ，现在改为 不论订单再哪出库  选择退近哪个库  就是哪个库 
     * @param request
     * @param response
     * @throws SQLException 
     */
    public void quickCancelStock(HttpServletRequest request,
            HttpServletResponse response) throws SQLException {
//    	System.out.println("quickCancelStock");
    	voUser user = (voUser) request.getSession().getAttribute(
	            IConstants.USER_VIEW_KEY);
	    if (user == null) {
	        request.setAttribute("tip", "当前没有登录，添加失败！");
	        request.setAttribute("result", "failure");
	        return;
	    }
	    UserGroupBean group = user.getGroup();
		if(!group.isFlag(613)){
			request.setAttribute("tip", "您没有退货包裹入库权限！");
			request.setAttribute("result", "failure");
			return;
		}
	    int type = StringUtil.StringToId(request.getParameter("type"));
    	String orderId=StringUtil.convertNull(request.getParameter("orderId"));//订单Id
    	int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
    	
    	if( wareArea == -1 ) {
    		request.setAttribute("tip", "没有选择退货包裹的入库地区！");
	        request.setAttribute("result", "failure");
	        return;
    	}
    	if( !CargoDeptAreaService.hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN) ) {
			request.setAttribute("tip", "你没有操作当前地区退货库的权限！");
	        request.setAttribute("result", "failure");
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
        IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
        IMEIService IMEISericve = new IMEIService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
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
    			/**
    			 * 提交退货部分
    			 */
    			voOrder order=wareService.getOrder(Integer.parseInt(orderId));
    			if (order == null) {
    				request.setAttribute("tip", "这个订单不存在！");
    				request.setAttribute("result", "failure");
    				wareService.releaseAll();
    				return;
    			}
    			if( order.getStatus() == 11 ) {
    				request.setAttribute("tip", "该订单已退回不可再退！");
    				request.setAttribute("result", "failure");
    				wareService.releaseAll();
    				return;
    			}
    			ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + order.getCode() + "'");
    			if( rpBean == null ) {
    				request.setAttribute("tip", "在待退货包裹列表中没有找到对应该订单的记录！");
    				request.setAttribute("result", "failure");
    				wareService.releaseAll();
    				return;
    			}  else {
    				if( rpBean.getArea() != wareArea) {
    					request.setAttribute("tip", "所选地区和待退货地区不同！");
        				request.setAttribute("result", "failure");
        				wareService.releaseAll();
        				return;
    				}
    				if( rpBean.getStatus() == ReturnedPackageBean.STATUS_HAS_RETURN) {
    					request.setAttribute("tip", "退货包裹已经入库 ！");
        				request.setAttribute("result", "failure");
        				wareService.releaseAll();
        				return;
    				}
    			}
    			
    			String orderCode=order.getCode();
    			String name = orderCode + "_" + DateUtil.getNow().substring(0, 10) + "_" + title;
    			// 从订单出货表中 查找订单的出货情况
    			OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and status<>" + OrderStockBean.STATUS5 + " and order_code = '" + orderCode + "'");
    			
    			if(oper != null && oper.getStatus() == OrderStockBean.STATUS8) {
    				request.setAttribute("tip", "该订单包裹已经退回过！");
    				request.setAttribute("result", "failure");
    				request.setAttribute("oper", oper);
    				return;
    			}
    			
    			if (oper == null || oper.getStatus() != OrderStockBean.STATUS3) {
    				request.setAttribute("tip", "这个订单还没有进行出库操作！");
    				request.setAttribute("result", "failure");
    				request.setAttribute("oper", oper);
    				return;
    			}
    			// 从订单出货表中 查找 出货记录
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

    			//添加库存人员操作历史记录
    			StockAdminHistoryBean log = new StockAdminHistoryBean();
    			log.setAdminId(user.getId());
    			log.setAdminName(user.getUsername());
    			log.setLogId(rpBean.getId());
    			log.setLogType(StockAdminHistoryBean.CANCEL_STOCKIN);
    			log.setOperDatetime(DateUtil.getNow());
    			log.setRemark("新建" + title + "操作：" + name);
    			log.setType(StockAdminHistoryBean.CREATE);
    			if(!service.addStockAdminHistory(log)){
    				request.setAttribute("tip", "添加StockAdminHistoryBean失败！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
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
    						if(!IMEISericve.updateIMEI("status="+IMEIBean.IMEISTATUS2, "code='"+iuoBean.getImeiCode()+ "'")){
    							request.setAttribute("tip", "更改IMEI状态失败");
    							request.setAttribute("result", "failure");
    							service.getDbOp().rollbackTransaction();
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
    							request.setAttribute("tip", "添加IMEI日志失败");
    							request.setAttribute("result", "failure");
    							service.getDbOp().rollbackTransaction();
    							return;
    						}
    					}
    				}
    				//烂货退换，入库数量可以为0
    				if(!(stockType == StockOperationBean.BAD_EXCHANGE) && sh2.getStockBj() == 0 && sh2.getStockGd() == 0){
    					request.setAttribute("tip", "入库数量为0，无法入库！");
    					request.setAttribute("result", "failure");
    					service.getDbOp().rollbackTransaction();
    					return;
    				}
    				count++;
    				product = wareService.getProduct(sh2.getProductId());
    				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));

    				// 需要计算一下 库存价格
    				//voOrder order = adminService.getOrder("code='" + bean.getOrderCode() + "'");
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
    					if(stockCard!=null){
    						orderProduct.setPrice3(stockCard.getStockPrice());
    					}
    				}
    				//计算库存价格
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
    					request.setAttribute("tip", "没有找到产品库存，操作失败！");
    					request.setAttribute("result", "failure");
    					service.getDbOp().rollbackTransaction();
    					return;
    				}
    				if(!psService.updateProductStockCount(ps.getId(), (sh2.getStockBj() + sh2.getStockGd()))){
    					request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
    					request.setAttribute("result", "failure");
    					service.getDbOp().rollbackTransaction();
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
    						request.setAttribute("tip", "目的退货库缓存区货位未设置，请先添加后再完成入库！");
    						request.setAttribute("result", "failure");
    						service.getDbOp().rollbackTransaction();
    						return;
    					}
    					cps = new CargoProductStockBean();
    					cps.setCargoId(cargo.getId());
    					cps.setProductId(sh2.getProductId());
    					cps.setStockCount(sh2.getStockBj() + sh2.getStockGd());
    					if(!cargoService.addCargoProductStock(cps))
    					{
    					  service.getDbOp().rollbackTransaction();
    					  request.setAttribute("tip", "数据库操作失败");
    					  request.setAttribute("result", "failure");
    					  return;
    					}
    					cps.setId(cargoService.getDbOp().getLastInsertId());

    					if(!cargoService.updateCargoInfo("status = 0", "id = "+cargo.getId())){
    						request.setAttribute("tip", "更新货位状态失败！");
    						request.setAttribute("result", "failure");
    						service.getDbOp().rollbackTransaction();
    						return;
    					}
    				}else{
    					cps = (CargoProductStockBean)cocList.get(0);
    					if(!cargoService.updateCargoProductStockCount(cps.getId(), (sh2.getStockBj() + sh2.getStockGd()))){
    						request.setAttribute("tip", "更新货位库存失败！");
    						request.setAttribute("result", "failure");
    						service.getDbOp().rollbackTransaction();
    						return;
    					}
    				}

    				/**
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
    					newBatch.setTicket(ticket);

    					if(!service.addStockBatch(newBatch)){
    						request.setAttribute("tip", "添加库存批次信息失败！");
    						request.setAttribute("result", "failure");
    						service.getDbOp().rollbackTransaction();
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
    						request.setAttribute("tip", "添加库存批次日志信息失败！");
    						request.setAttribute("result", "failure");
    						service.getDbOp().rollbackTransaction();
    						return;
    					}

    					stockinPrice = batchLog.getBatchCount()*batchLog.getBatchPrice();
    					
    					//财务产品信息表---liuruilan-----2012-11-01-----
    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh2.getProductId());
    					if(fProduct == null){
							request.setAttribute("tip", "查询异常，请与管理员联系！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
    					int totalCount = product.getStockAll() + product.getLockCountAll();
						float priceSum = Arith.mul(price5, totalCount);
						
						//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
						float priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(batchLog.getBatchPrice(), stockinCount)), Arith.add(_count, stockinCount)), 2);
						float priceSumHasticket = Arith.mul(priceHasticket,  stockinCount + _count);
						set = "price =" + price5 + ", price_sum =" + priceSum + ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
						if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())){
							request.setAttribute("tip", "更新财务商品均价失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
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
    						request.setAttribute("tip", "添加财务进销存卡片失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
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
    							  request.setAttribute("tip", "向finance_sell表添加未妥投退货记录失败");
    							  request.setAttribute("result", "failure");
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
    										request.setAttribute("tip", "向finance_sell_product表添加未妥投退货记录失败！");
    										request.setAttribute("result", "failure");
    										service.getDbOp().rollbackTransaction();
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
		    						request.setAttribute("tip", "添加状态为未妥投退回FinanceSellBean失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
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
		    							request.setAttribute("tip", "添加状态为未妥投退回FinanceSellProductBean失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
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
		    							request.setAttribute("tip", "添加状态为未妥投退回FinanceSellProductBean赠品失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
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
	    						request.setAttribute("tip", "查询异常，请与管理员联系！");
	    						request.setAttribute("result", "failure");
	    						service.getDbOp().rollbackTransaction();
	    						return;
	    					}
	                		int _count = FinanceProductBean.queryCountIfTicket(db, sh2.getProductId(), ticket);
    						if(batch!=null){
    							if(stockinCount<=batchLog.getBatchCount()){
    								if(!service.updateStockBatch("batch_count = batch_count+"+stockinCount, "id="+batch.getId())){
    									request.setAttribute("tip", "更新批次数量失败！");
    		    						request.setAttribute("result", "failure");
    		    						service.getDbOp().rollbackTransaction();
    		    						return;
    								}
    								batchCount = stockinCount;
    								stockinCount = 0;
    							}else{
    								if(!service.updateStockBatch("batch_count = batch_count+"+batchLog.getBatchCount(), "id="+batch.getId())){
    									request.setAttribute("tip", "更新批次数量失败！");
    		    						request.setAttribute("result", "failure");
    		    						service.getDbOp().rollbackTransaction();
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
    							newBatch.setTicket(ticket);

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
    								request.setAttribute("tip", "添加批次信息失败！");
		    						request.setAttribute("result", "failure");
		    						service.getDbOp().rollbackTransaction();
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
    							request.setAttribute("tip", "添加批次操作日志失败！");
	    						request.setAttribute("result", "failure");
	    						service.getDbOp().rollbackTransaction();
	    						return;
    						}

    						//财务产品信息表---liuruilan-----2012-11-01-----
	    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh2.getProductId());
	    					if(fProduct == null){
								request.setAttribute("tip", "查询异常，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
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
								request.setAttribute("tip", "更新FinanceProductBean失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
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
	    						request.setAttribute("tip", "添加财务卡片失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
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
	    								request.setAttribute("tip", "向finance_sell表添加未妥投退货记录失败！");
	    								request.setAttribute("result", "failure");
	    								service.getDbOp().rollbackTransaction();
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
	    										request.setAttribute("tip", "向finance_sell_product表添加未妥投退货记录失败！");
	    	    								request.setAttribute("result", "failure");
	    	    								service.getDbOp().rollbackTransaction();
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
	    	    						request.setAttribute("tip", "添加状态为未妥投退回FinanceSellBean失败！");
	    								request.setAttribute("result", "failure");
	    								service.getDbOp().rollbackTransaction();
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
	    							request.setAttribute("tip", "添加状态为未妥投退回FinanceSellProductBean失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
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
	    							request.setAttribute("tip", "添加状态为未妥投退回FinanceSellProductBean赠品失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
	    						}
	    					}
	    					//将订单数据写入发货信息表（财务统计用）---liuruilan---2012-9-13-----
	    				
	    						}
	    						
	    					}
	    					//-------------liuruilan-------------
    					}

    				}**/

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
    					request.setAttribute("tip", "添加StockAdminHistoryBean失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
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
    					request.setAttribute("tip", "添加入库卡片失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
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
    					request.setAttribute("tip", "添加货位入库卡片失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
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
    				request.setAttribute("tip", "该操作没有任何库存变动，不能执行！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
    				return;
    			}

				//添加到退货包裹列表中---------------------柳波-----------------
    			//改为更新退货包裹列表----郝亚斌--
    			if( !updateReturnedPackage(rpBean, user, service, statService, order, ReturnedPackageBean.STATUS_HAS_RETURN) ) {
    				request.setAttribute("tip", "该操作没有任何库存变动，不能执行！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
    				return;
    			}
    			//添加到退货包裹列表中---------------------柳波-----------------
    			
    			
				//service.updateStockOperation("status = " + StockOperationBean.STATUS5 , " status=" + StockOperationBean.STATUS3 + " and order_code = '" + bean.getOrderCode() + "'");
				//service.updateOrderStock("status = " + OrderStockBean.STATUS5 , " status=" + OrderStockBean.STATUS3 + " and order_code = '" + bean.getOrderCode() + "'");
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
	    			request.setAttribute("tip", "更新订单日志变更失败！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
    				return;
	    		}
	    		//更新订单库存状态
				if(!service.updateOrderStock("status = " + OrderStockBean.STATUS9 , " status=" + OrderStockBean.STATUS3 + " and order_code = '" + orderCode + "'")){
					request.setAttribute("tip", "更新订单库存状态失败！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
    				return;
				}
				//更新订单状态
				if(!service.getDbOp().executeUpdate("update user_order set status=11, stockout=0 where code = '" + orderCode + "' ")){
					request.setAttribute("tip", "更新订单状态失败！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
    				return;
				}
				if(!service.getDbOp().executeUpdate("update user_order set balance_status=3, stockout_deal=3 where code = '" + orderCode + "' ")){
					request.setAttribute("tip", "更新订单balance_status状态失败！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
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
					request.setAttribute("tip", "添加StockAdminHistoryBean失败！");
    				request.setAttribute("result", "failure");
    				service.getDbOp().rollbackTransaction();
    				return;
				}
				//添加退货操作日志
				if (!packageLog.addReturnPackageLog("快速退货入库", user, order.getCode())) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加退货日志失败！");
					request.setAttribute("result", "failure");
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
								request.setAttribute("tip", "S单更改售后单状态失败！");
								request.setAttribute("result", "failure");
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
									request.setAttribute("tip", "S单更改物流售后处理单状态失败！");
									request.setAttribute("result", "failure");
									return;
								}
								
								if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_product_records aswpr,after_sale_operation_wsproduct_relational asowr, after_sale_operation_list asol set aswpr.status=8,aswpr.modify_user_id=" + user.getId() + ",aswpr.modify_user_name='" + user.getUsername() + "',aswpr.modify_datetime='" + DateUtil.getNow() + "' where aswpr.id=asowr.after_sale_wsproduct_record_id and asowr.after_sale_operation_id=asol.id and asol.order_id=" + order.getId())) {
									service.getDbOp().rollbackTransaction();
									request.setAttribute("tip", "S单更改销售售后处理单状态失败！");
									request.setAttribute("result", "failure");
									return;
								}
							}
						}
					}
				}
				
				// --------------------add start--------------------
				//根据业务类型采集财务基础数据，已经计算过库存均价
				try{
					FinanceSaleBaseDataService financeBaseDataService = FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN, service.getDbOp().getConn());
					financeBaseDataService.acquireFinanceAfterSaleBaseData(order.getCode(), order.getCode(), user.getId(), 
							DateUtil.getNow(), ProductStockBean.STOCKTYPE_RETURN, wareArea, 
							FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN, null);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				// --------------------add end----------------------
				
    			//提交事务
    			service.getDbOp().commitTransaction();
    			CookieUtil cu = new CookieUtil(request, response);
    			cu.setCookie("RETURN_QUICK_AREA_MARK_" + user.getId(), ""+wareArea, 60*60*24*30);
    			request.setAttribute("canceled", "success");//传给页面，表示退货入库成功
    		}
    	}catch (Exception e) {
        	e.printStackTrace();
        	service.getDbOp().rollbackTransaction();
		} finally {
    		dbOp.release();
            service.releaseAll();
            logService.releaseAll();
        }
    }

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
	
	private void updateFinanceSaleReturnBaseData(int areaId, List<BaseProductInfo> pList, voOrder order, voUser user, Connection con) throws Exception{
		//根据业务类型采集财务基础数据
		try{
			FinanceSaleBaseDataService financeBaseDataService = FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN, con);
			financeBaseDataService.acquireFinanceAfterSaleBaseData(order.getCode(), order.getCode(), 
					user.getId(), order.getCreateDatetime().toString(), 
					ProductStockBean.STOCKTYPE_RETURN, areaId, StockCardBean.CARDTYPE_CANCELORDERSTOCKIN, pList);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
