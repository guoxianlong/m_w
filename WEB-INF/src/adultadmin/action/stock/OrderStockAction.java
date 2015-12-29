/*
 * Created on 2009-5-8
 *
 */
package adultadmin.action.stock;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.aftersale.OrderRefundService;
import mmb.finance.balance.BalanceService;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.rec.sys.easyui.Json;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.IMEI.IMEIUserOrderBean;
import mmb.stock.aftersale.AfStockService;
import mmb.stock.aftersale.AfterSaleDetectProductBean;
import mmb.stock.aftersale.AfterSaleOperationListBean;
import mmb.stock.aftersale.AfterSaleWarehourceProductRecordsBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.SecondSortingSplitService;
import mmb.stock.stat.SortingAbnormalBean;
import mmb.stock.stat.SortingAbnormalDisposeService;
import mmb.stock.stat.SortingAbnormalProductBean;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingBatchOrderProductBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderStockStatusBean;
import adultadmin.bean.PagingBean;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleRefundOrderBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import cn.mmb.delivery.application.WayBillApplication;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceSaleBaseDataService;
import com.mmb.framework.support.SpringHandler;

public class OrderStockAction extends DispatchAction{

	public static byte[] orderStockLock = new byte[0];
	public static byte[] packagenumLock = new byte[0];
	public Log stockLog = LogFactory.getLog("stock.Log");
	public static String CHANGE_STOCK_ARER_TIME = "2012-05-20 00:00:00"; 
	private String date = DateUtil.formatDate(new Date());
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-9
	 * 
	 * 说明：订单出货 列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void orderStockList(HttpServletRequest request, HttpServletResponse response) {
		int countPerPage = 100;
		String strArea = request.getParameter("area");
		int area = StringUtil.StringToId(strArea);
		String orderCode = StringUtil.dealParam(request.getParameter("orderCode"));
		String strStatus = request.getParameter("status");
		int status = StringUtil.StringToId(strStatus);
		String startDate = StringUtil.dealParam(request.getParameter("startDate"));
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));
		int allOrderStock = StringUtil.StringToId(request.getParameter("allOrderStock"));

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");	// add by zhoujun 3-11
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IStockService stockUpdateService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			StringBuffer buf = new StringBuffer();
			StringBuffer urlBuf = new StringBuffer();
			urlBuf.append("orderStockList.jsp?1=1");
			buf.append(" status <> ");
			buf.append(OrderStockBean.STATUS4);
			if(orderCode != null && orderCode.length() > 0){
				buf.append(" and order_code='");
				buf.append(orderCode);
				buf.append("' ");
				urlBuf.append("&orderCode=");
				urlBuf.append(orderCode);
			}
			if (strArea != null && area >= 0) {
				buf.append(" and stock_area = ");
				buf.append(area);
				urlBuf.append("&area=");
				urlBuf.append(area);
			}
			if (strStatus != null && status >= 0) {
				buf.append(" and status = ");
				buf.append(status);
				urlBuf.append("&status=");
				urlBuf.append(status);
			}
			if (startDate != null && startDate.length() > 0 && endDate != null && endDate.length() > 0) {
				buf.append(" and create_datetime >='");
				buf.append(startDate);
				buf.append("' and create_datetime <='");
				buf.append(endDate);
				buf.append(" 23:59:59");
				buf.append("' ");
				urlBuf.append("&startDate=");
				urlBuf.append(startDate);
				urlBuf.append("&endDate=");
				urlBuf.append(endDate);
			} else {	// 如果没有选择时间，默认最近5天，减少数据量
				buf.append(" and create_datetime >=date_add(now(),interval -5 day)");
			}
			String condition = buf.toString();
			//总数
			int totalCount = service.getOrderStockCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = null;
			if(allOrderStock == 0){
				list = service.getOrderStockList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "status, id desc");
			} else {
				list = service.getOrderStockList(condition, -1, -1, "status, id desc");
			}
			paging.setPrefixUrl(urlBuf.toString());
			Iterator itr = list.iterator();
			OrderStockBean oper = null;
			List list1 = new ArrayList();
			List list2 = new ArrayList();
			List list3 = new ArrayList();
			List list4 = new ArrayList();
			List list5 = new ArrayList();
			List list6 = new ArrayList();
			List list7 = new ArrayList();
			while (itr.hasNext()) {
				oper = (OrderStockBean) itr.next();
				oper.setOrder(adminService.getOrder("code = '" + oper.getOrderCode() + "'"));
				if (oper.getStatus() == OrderStockBean.STATUS1) {
					List orderProductList = adminService.getOrderProductsSplit(oper.getOrder().getId());
					List orderPresentList = adminService.getOrderPresentsSplit(oper.getOrder().getId());
					orderProductList.addAll(orderPresentList);
					Iterator iter = orderProductList.listIterator();
					while(iter.hasNext()){
						voOrderProduct vop = (voOrderProduct)iter.next();
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
					}
					int ss = checkStock(orderProductList);
					if(ss == 0){
						//三地库存都充足
						oper.setRealStatusStock(OrderStockBean.STATUS1_STOCK); //绿色
						list1.add(oper);
					} else if(ss == 3) {
						oper.setRealStatusStock(OrderStockBean.STATUS1_NO_STOCK);
						list2.add(oper);
					} else if (checkStockInArea(orderProductList, oper.getStockArea())) {
						oper.setRealStatusStock(OrderStockBean.STATUS1_ONE_STOCK);
						list7.add(oper);
					} else if (!checkStockInArea(orderProductList, oper.getStockArea())) {
						oper.setRealStatusStock(OrderStockBean.STATUS1_OTHER_STOCK);
						list6.add(oper);
					}
					if(!checkStockInArea(orderProductList, oper.getStockArea())){
						if (oper.getStatusStock() == OrderStockBean.STATUS1_STOCK) {
							oper.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
							stockUpdateService.updateOrderStock("status_stock=" + OrderStockBean.STATUS1_NO_STOCK, "id=" + oper.getId());
						}
					}
				} else if (oper.getStatus() == OrderStockBean.STATUS2) {
					if (oper.getStatusStock() == OrderStockBean.STATUS2_FROM_NO_STOCK) {
						list3.add(oper);
					} else {
						list4.add(oper);
					}
				} else {
					list5.add(oper);
				}
			}
			list.clear();
			if(allOrderStock == 2){
				list.addAll(list2);
			} else {
				list.addAll(list1);
				list.addAll(list7);
				list.addAll(list6);
				list.addAll(list2);
				list.addAll(list3);
				list.addAll(list4);
				list.addAll(list5);
			}

			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		} finally {
			service.releaseAll();
			stockUpdateService.releaseAll();
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-9
	 * 
	 * 说明：添加订单出货操作
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 * @param querzyUserCode 自动申请出口是传入 code 其他时候 请传入 null
	 */
	public void addOrderStock(HttpServletRequest request, HttpServletResponse response,StringBuilder querzyUserCode) {
		voUser user = null;
		if(request!=null){
			user=(voUser)request.getSession().getAttribute("userView");
		}else{
			user=new voUser();
			user.setId(0);
			user.setUsername("自动申请出库");
		}
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，添加失败！");
			request.setAttribute("result", "failure");
			return;
		}
		synchronized(orderStockLock){
			String orderCode=null;
			if(querzyUserCode==null){//自动申请出库的时候传过来的 订单号
				orderCode =StringUtil.dealParam(request.getParameter("orderCode"));
			}else{
				orderCode= querzyUserCode.toString();
			}
			if (StringUtil.convertNull(orderCode).equals("")) {
				request.setAttribute("tip", "请填写订单编号！");
				request.setAttribute("result", "failure");
				return;
			}

			DbOperation dbOpSMS = new DbOperation();
			dbOpSMS.init("sms");
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
			IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				voOrder order = adminService.getOrder("code = '" + orderCode + "'");
				if (order == null) {
					request.setAttribute("tip", "这个订单不存在！");
					request.setAttribute("result", "failure");
					adminService.close();
					return;
				}
				if (order.getStatus() != 3) {
					request.setAttribute("tip", "该状态下不能申请出库");
					request.setAttribute("result", "failure");
					adminService.close();
					return;
				}
				if (order.getStockout() == 1) {
					request.setAttribute("tip", "这个订单已出库！");
					request.setAttribute("result", "failure");
					adminService.close();
					return;
				}
				
				
				ResultSet tempRs = dbOpSMS.executeQuery("select add_time from "+DbOperation.SCHEMA_SMS+"send_message_order_id where type=2 and order_id="+order.getId()); 
				if(tempRs.next()){
					String addTime = tempRs.getString(1);
					int minuteSub = DateUtil.getMinuteSub(addTime,DateUtil.getNow());
					if(minuteSub>=59){
						request.setAttribute("tip", "该订单即将自动申请出库，不能手动申请出货！");
						request.setAttribute("result", "failure");
						return;
					}
				}
				
				List orderProductList = adminService.getOrderProducts(order.getId());
				List orderPresentList = adminService.getOrderPresents(order.getId());

				List productList = new ArrayList();
				OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and order_code = '" + orderCode + "'");
				if (oper != null) {
					request.setAttribute("tip", "这个订单已添加出库操作，直接修改即可！");
					request.setAttribute("result", "failure");
					request.setAttribute("oper", oper);
					return;
				}

				Iterator productIter = orderProductList.listIterator();
				while(productIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)productIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						if(ppList == null || ppList.size() == 0){
							request.setAttribute("tip", "套装产品"+product.getCode()+"信息异常，无法申请出库！");
							request.setAttribute("result", "failure");
							request.setAttribute("oper", oper);
							return;
						}
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						productList.add(vop);
					}
				}
				productIter = orderPresentList.listIterator();
				while(productIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)productIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						if(ppList == null || ppList.size() == 0){
							request.setAttribute("tip", "套装产品"+product.getCode()+"信息异常，无法申请出库！");
							request.setAttribute("result", "failure");
							request.setAttribute("oper", oper);
							return;
						}
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						productList.add(vop);
					}
				}

//				int stockStatus = checkStock(productList);

				//如果订单内的商品库存充足，而且该订单有赠品，则判断赠品库存是否充足
				//	            if (stockStatus == 0 && orderPresentList != null && orderPresentList.size() > 0) {
				//	                stockStatus = checkStock(orderPresentList); //检查订单内赠品的库存情况
				//	            }

				int orderStockArea = ProductStockBean.AREA_GF;
				orderStockArea=checkStock2(productList,order);

//				Date changStockAreaTime = DateUtil.parseDate(OrderStockAction.CHANGE_STOCK_ARER_TIME,DateUtil.normalTimeFormat);
//				if(System.currentTimeMillis()>changStockAreaTime.getTime()){   //优先增城发货
//					if(stockStatus == 2 || stockStatus == 7){
//						// 从广分发货
//						orderStockArea = ProductStockBean.AREA_GF;
//					} else if(stockStatus == 0 || stockStatus == 1 || stockStatus == 5 || stockStatus == 6){
//						// 从增城发货
//						orderStockArea = ProductStockBean.AREA_ZC;
//					} else {
//						// 提示库存不足
//						if(order.getStockoutDeal() == 7){
//							service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
//							service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
//						}
//						if(querzyUserCode!=null){//定时任务调用 直接返回
//							querzyUserCode.delete(0, querzyUserCode.length());
//							querzyUserCode.append("lackOrder");
//							return;
//						}
//						request.setAttribute("tip", "合格库库存不足，请查看调拨能否解决问题。");
//						request.setAttribute("result", "failure");
//						request.setAttribute("oper", oper);
//						return;
//					}
//				}else{ //优先芳村发货
//					if(stockStatus == 0 || stockStatus == 2 || stockStatus == 5 || stockStatus == 7){
//						// 从广分发货
//						orderStockArea = ProductStockBean.AREA_GF;
//					} else if(stockStatus == 1 || stockStatus == 6){
//						// 从增城发货
//						orderStockArea = ProductStockBean.AREA_ZC;
//					}else {
//						// 提示库存不足
//						if(order.getStockoutDeal() == 7){
//							service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
//							service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
//						}
//						if(querzyUserCode!=null){//定时任务调用 直接返回
//							querzyUserCode.delete(0, querzyUserCode.length());
//							querzyUserCode.append("lackOrder");
//							return;
//						}
//						request.setAttribute("tip", "合格库库存不足，请查看调拨能否解决问题。");
//						request.setAttribute("result", "failure");
//						request.setAttribute("oper", oper);
//						return;
//					}
//				}
//				if(order.getBuyMode()!=0||order.getAddress().contains("邮")){
//					if(stockStatus == 0 || stockStatus == 1 || stockStatus == 5 || stockStatus == 6){
//						orderStockArea=3;
//					}else{
//						orderStockArea=0;
//					}
//				}else{
//					orderStockArea=checkStock2(productList,order);
//				}
				if(orderStockArea==0){//缺货
					// 提示库存不足
					if(order.getStockoutDeal() == 7){
						service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
						service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
					}
					if(querzyUserCode!=null){//定时任务调用 直接返回
						querzyUserCode.delete(0, querzyUserCode.length());
						querzyUserCode.append("lackOrder");
						return;
					}
					request.setAttribute("tip", "合格库库存不足，请查看调拨能否解决问题。");
					request.setAttribute("result", "failure");
					request.setAttribute("oper", oper);
					return;
				}
				
				String name = orderCode + "_" + DateUtil.getNow().substring(0, 10) + "_出货";
				service.getDbOp().startTransaction();
				
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
				
				OrderStockBean bean = new OrderStockBean();
				bean.setCreateDatetime(DateUtil.getNow());
				bean.setName(name);
				bean.setOrderCode("");
				bean.setRemark("");
				bean.setStatus(OrderStockBean.STATUS1);
				bean.setOrderCode(orderCode);
				bean.setOrderId(order.getId());
//				bean.setCode(CodeUtil.getOrderStockCode());
				bean.setCode("CK" + sdf.format(cal.getTime()));//此处设定order_stock的初始编号
				bean.setStockArea(orderStockArea);
				bean.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
				List orderProductList2 = adminService.getOrderProductsSplit(order.getId());
				List orderPresentList2 = adminService.getOrderPresentsSplit(order.getId());
				orderProductList2.addAll(orderPresentList2);
				Iterator detailIter = orderProductList2.listIterator();
				while(detailIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)detailIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + tempProduct.getBzzhongliang());
						}
					} else {
						order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + product.getBzzhongliang());
					}
				}
				StringBuffer orderSB = new StringBuffer();
				//文齐辉修改2011/05/27添加订单出库时自动分配订单的产品分类
				//只计算订单产品，不计赠品 ,如果分类==0
//				if(order.getProductType()==0){
//					Iterator tmpIter = orderProductList.iterator();
//					float tmpPrice=0f;
//					int orderPType=0;
//					while(tmpIter.hasNext()){
//						voOrderProduct vop = (voOrderProduct)tmpIter.next();
//						UserOrderPackageTypeBean uopType = userOrderService.getUserOrderPackageType("product_catalog = "+vop.getParentId1());
//						if(uopType!=null){
//							float productPrice = vop.getCount()*vop.getDiscountPrice();
//							if(productPrice>tmpPrice){
//								tmpPrice = productPrice;
//								orderPType = uopType.getTypeId();
//							}
//						}
//					}
//					if(orderPType!=0)
//						orderSB.append("product_type = ").append(orderPType);
//				}
				if(orderSB.length()>0)
					adminService.modifyOrder(orderSB.toString(), "id=" + order.getId());

				bean.setLastOperTime(DateUtil.getNow());
				//检查订单中商品的库存情况
				if (checkStockInArea(orderProductList, bean.getStockArea())) {
					bean.setStatusStock(OrderStockBean.STATUS1_STOCK);
				} else {
					bean.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
				}
//				if (stockStatus == 0 && orderPresentList != null && orderPresentList.size() > 0) {
//					//检查订单中赠品的库存情况
//					if (checkStockInArea(orderPresentList, bean.getStockArea())) {
//						bean.setStatusStock(OrderStockBean.STATUS1_STOCK);
//					} else {
//						bean.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
//					}
//				}
				bean.setCreateUserId(user.getId());
				
				//根据地址分配快递公司
				int deliver=getDeliver(order.getBuyMode(), order.getAddress(),bean.getStockArea());
				if(deliver!=0){
					adminService.modifyOrder("deliver="+deliver,"id="+order.getId());
					bean.setDeliver(deliver);
				}
            	
				//*****
				if (!service.addOrderStock(bean)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				int id = service.getDbOp().getLastInsertId();
				bean.setId(id);
				
				//此处修改order_stock.code
				String newCode = null;
				if(id > 999999){
					String strId = String.valueOf(id);
					newCode = strId.substring(strId.length()- 6, strId.length());
				} else {
					DecimalFormat df2 = new DecimalFormat("000000");
					newCode = df2.format(id);
				}

				StringBuilder updateBuf = new StringBuilder();
				updateBuf.append("update order_stock set code=concat(code,'").append(newCode).append("') where id=").append(id);
				adminService.getDbOperation().executeUpdate(updateBuf.toString());
				
				adminService.deleteOrderProductsSplit(bean.getOrderId());
				adminService.deleteOrderPresentsSplit(bean.getOrderId());

				//添加订单中商品的出货记录
				Iterator itr = orderProductList.iterator();
				voOrderProduct op = null;
				int shId = 0;
				OrderStockProductBean sh = null;
				while (itr.hasNext()) {
					op = (voOrderProduct) itr.next();
					voProduct product = adminService.getProduct(op.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(op.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());

							voOrderProduct vop = adminService.getOrderProductSplit(bean.getOrderId(), tempVOP.getCode());
							if(vop != null){
								// 如果已经有了这个商品，则增加数量
								if(!adminService.updateOrderProductSplit("count=(count + " + tempVOP.getCount() + ") ", vop.getId())){
									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							} else {
								//如果还没有这个商品，则添加
								if(!adminService.addOrderProductSplit(bean.getOrderId(), tempVOP.getCode(), tempVOP.getCount())){
									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
							sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + tempVOP.getProductId());
							if(sh != null){
								sh.setStockoutCount(sh.getStockoutCount() + tempVOP.getCount());
								service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
							} else {
								sh = new OrderStockProductBean();
								sh.setCreateDatetime(DateUtil.getNow());
								sh.setDealDatetime(null);
								sh.setOrderStockId(id);
								sh.setProductCode(tempVOP.getCode());
								sh.setProductId(tempVOP.getProductId());
								sh.setRemark("");
								sh.setStatus(OrderStockProductBean.UNDEAL);
								ProductStockBean ps = null ;

								ps = psService.getProductStock("product_id=" + tempVOP.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());

								sh.setStockoutId(ps.getId());
								sh.setStockoutCount(tempVOP.getCount());
								sh.setStockArea(bean.getStockArea());
								sh.setStockType(bean.getStockType());
								if(!service.addOrderStockProduct(sh)){
									request.setAttribute("tip", "添加失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
						}
					} else {
						voOrderProduct vop = adminService.getOrderProductSplit(bean.getOrderId(), op.getCode());
						if(vop != null){
							// 如果已经有了这个商品，则增加数量
							if(!adminService.updateOrderProductSplit("count=(count + " + op.getCount() + ") ", vop.getId())){
								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}

						} else {
							if(!adminService.addOrderProductSplit(bean.getOrderId(), op.getCode(), op.getCount())){
								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
						sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + op.getProductId());
						if(sh != null){
							sh.setStockoutCount(sh.getStockoutCount() + op.getCount());
							service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
						} else {
							sh = new OrderStockProductBean();
							sh.setCreateDatetime(DateUtil.getNow());
							sh.setDealDatetime(null);
							sh.setOrderStockId(id);
							sh.setProductCode(op.getCode());
							sh.setProductId(op.getProductId());
							sh.setRemark("");
							sh.setStatus(OrderStockProductBean.UNDEAL);
							ProductStockBean ps = psService.getProductStock("product_id=" + op.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
							sh.setStockoutId(ps.getId());
							sh.setStockoutCount(op.getCount());
							sh.setStockArea(bean.getStockArea());
							sh.setStockType(bean.getStockType());
							if(!service.addOrderStockProduct(sh)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
					}
				}
				//添加订单中赠品的出货记录
				itr = orderPresentList.iterator();
				op = null;
				shId = 0;
				sh = null;
				while (itr.hasNext()) {
					op = (voOrderProduct) itr.next();
					voProduct product = adminService.getProduct(op.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(op.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());

							voOrderProduct vop = adminService.getOrderPresentSplit(bean.getOrderId(), tempVOP.getCode());
							if(vop != null){
								if(!adminService.updateOrderPresentSplit("count=(count + " + tempVOP.getCount() + ") ", vop.getId())){
									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							} else {
								if(!adminService.addOrderPresentSplit(bean.getOrderId(), tempVOP.getCode(), tempVOP.getCount())){
									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
							sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + tempVOP.getProductId());
							if(sh != null){
								sh.setStockoutCount(sh.getStockoutCount() + tempVOP.getCount());
								service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
							} else {
								sh = new OrderStockProductBean();
								sh.setCreateDatetime(DateUtil.getNow());
								sh.setDealDatetime(null);
								sh.setOrderStockId(id);
								sh.setProductCode(tempVOP.getCode());
								sh.setProductId(tempVOP.getProductId());
								sh.setRemark("");
								sh.setStatus(OrderStockProductBean.UNDEAL);
								ProductStockBean ps = psService.getProductStock("product_id=" + tempVOP.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
								sh.setStockoutId(ps.getId());
								sh.setStockoutCount(tempVOP.getCount());
								sh.setStockArea(bean.getStockArea());
								sh.setStockType(bean.getStockType());
								if(!service.addOrderStockProduct(sh)){
									request.setAttribute("tip", "添加失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
						}
					} else {
						voOrderProduct vop = adminService.getOrderPresentSplit(bean.getOrderId(), op.getCode());
						if(vop != null){
							// 如果已经有了这个商品，则增加数量
							if(!adminService.updateOrderPresentSplit("count=(count + " + op.getCount() + ") ", vop.getId())){
								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						} else {
							if(!adminService.addOrderPresentSplit(bean.getOrderId(), op.getCode(), op.getCount())){
								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
						sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + op.getProductId());
						if(sh != null){
							sh.setStockoutCount(sh.getStockoutCount() + op.getCount());
							service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
						} else {
							sh = new OrderStockProductBean();
							sh.setCreateDatetime(DateUtil.getNow());
							sh.setDealDatetime(null);
							sh.setOrderStockId(id);
							sh.setProductCode(op.getCode());
							sh.setProductId(op.getProductId());
							sh.setRemark("");
							sh.setStatus(OrderStockProductBean.UNDEAL);
							ProductStockBean ps = psService.getProductStock("product_id=" + op.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
							sh.setStockoutId(ps.getId());
							sh.setStockoutCount(op.getCount());
							sh.setStockArea(bean.getStockArea());
							sh.setStockType(bean.getStockType());
							if(!service.addOrderStockProduct(sh)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
					}

				}
				
				//计算订单中的sku数量，印刷品除外
            	String dmSql ="select type_id from user_order_package_type where name ='印刷品'";
            	ResultSet rsDm=service.getDbOp().executeQuery(dmSql);
            	String isDm = "";
            	if (rsDm.next()) {
            		isDm = " AND (e.product_type_id<>"+rsDm.getInt("type_id") +" or e.product_type_id is null)";
				}
            	rsDm.close();
				String skuCountSql ="SELECT COUNT(d.id) AS skuCount FROM user_order a " +
			            			"JOIN order_stock b ON a.id=b.order_id " +
			            			"JOIN order_stock_product c ON b.id=c.order_stock_id " +
			            			"JOIN product d ON c.product_id=d.id " +
            	                    "LEFT JOIN product_ware_property e ON d.id=e.product_id " +
            	                    "WHERE b.status<>3 AND a.id="+ order.getId()+isDm;
            	ResultSet rsSkuCount=service.getDbOp().executeQuery(skuCountSql);
            	int skuCount = 0;
            	if (rsSkuCount.next()) {
            		skuCount = rsSkuCount.getInt("skuCount");
				}
            	adminService.modifyOrder("product_count=" + skuCount, "id=" + order.getId());
            	rsSkuCount.close();
				// 订单 确认申请出货
				String condition = "order_stock_id = " + bean.getId() + " and status = " + OrderStockProductBean.UNDEAL;
				ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");
				voProduct product = null;
				String set = null;
				itr = shList.iterator();

				bean.setOrder(adminService.getOrder("code='" + bean.getOrderCode() + "'"));
				if (bean.getOrder() == null) {
					request.setAttribute("tip", "找不到对应的订单，不能修改！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				orderProductList = adminService.getOrderProductsSplit(bean.getOrder().getId());
				Iterator iter = orderProductList.listIterator();
				while (iter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) iter.next();
					vop.setPsList(psService.getProductStockList("product_id="
							+ vop.getProductId(), -1, -1, null));
				}
				if (checkStockInArea(orderProductList, bean.getStockArea())) {
					if (bean.getStatusStock() == OrderStockBean.STATUS1_NO_STOCK) {
						bean
						.setStatusStock(OrderStockBean.STATUS2_FROM_NO_STOCK);
					} else if (bean.getStatusStock() == OrderStockBean.STATUS1_STOCK) {
						bean.setStatusStock(OrderStockBean.STATUS2_FROM_STOCK);
					}
				} else {
					bean.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
				}

				while (itr.hasNext()) {
					sh = (OrderStockProductBean) itr.next();
					// 出库
					product = adminService.getProduct(sh.getProductId(),
							service.getDbOp());
					ProductStockBean ps = psService.getProductStock("id="
							+ sh.getStockoutId());

					if (sh.getStockoutCount() > ps.getStock()) {
						if(order.getStockoutDeal() == 7){
							service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
							service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
						}
						request.setAttribute("tip", product.getName()
								+ "的库存不足！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					set = "status = " + OrderStockProductBean.DEALED
					+ ", remark = concat(remark, '(操作前库存"
					+ ps.getStock() + "锁定库存" + ps.getLockCount()
					+ ",操作后库存"
					+ (ps.getStock() - sh.getStockoutCount()) + "锁定库存"
					+ (ps.getLockCount() + sh.getStockoutCount())
					+ ")'), deal_datetime = now()";
					service.updateOrderStockProduct(set, "id = " + sh.getId());
					if (!psService.updateProductStockCount(sh.getStockoutId(),
							-sh.getStockoutCount())) {
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					if (!psService.updateProductLockCount(sh.getStockoutId(),
							sh.getStockoutCount())) {
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					if (product.getParentId1() == 123 || product.getParentId1() == 143
							|| product.getParentId1() == 316 || product.getParentId1() == 317
							|| product.getParentId1() == 1385 || product.getParentId1() == 1425
							|| product.getParentId1() == 119 || product.getParentId1() == 340
							|| product.getParentId1() == 544 || product.getParentId1() == 545
							|| product.getParentId1() == 458 || product.getParentId1() == 459 
							|| product.getParentId1() == 401 || product.getParentId1() == 136 
							|| product.getParentId2() == 203 || product.getParentId2() == 204 
							|| product.getParentId2() == 205 || product.getParentId1() == 699
							|| product.getParentId1() == 145 || product.getParentId1() == 151
							|| product.getParentId1() == 197 || product.getParentId1() == 505
							|| product.getParentId1() == 163 || product.getParentId1() == 690
							|| product.getParentId1() == 908|| product.getParentId1() == 752
							|| product.getParentId1() == 803
							|| product.getParentId1() == 183 || product.getParentId1() == 184
							|| product.getParentId1() == 1093 || product.getParentId1() == 1094
							|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222) {
						psService.checkProductStatus(sh.getProductId());
					}

					// 订单商品表、订单赠品表 中记录的价格是 出库瞬间的 库存价格
					service.getDbOp().executeUpdate("update user_order_product set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_present set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_product_split set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_present_split set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_product_split_history set price5=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_present_split_history set price5=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
				}

				service.updateOrderStock("status_stock="
						+ bean.getStatusStock() + ", last_oper_time = now()",
						"id = " + bean.getId());

				service.updateOrderStock("status = " + OrderStockBean.STATUS2
						+ ", last_oper_time = now()", "id = " + bean.getId());

				service.getDbOp().executeUpdate(
						"update user_order set stockout = 1, confirm_datetime=now() where code = '"
						+ bean.getOrderCode() + "'");
				if (bean.getOrder().getAddress().indexOf("电话通知") == -1) {
					service.getDbOp().executeUpdate(
							"update user_order set address=concat(address, '(电话通知)') where code = '"
							+ bean.getOrderCode() + "'");
				}
				order = adminService.getOrder("code='" + bean.getOrderCode() + "'");
				// 如果该订单存在原订单
				if (order.getOriginOrderId() > 0) {
					// 将原订单的状态改为已取消, confirm_datetime 改为申请的日期
					service.getDbOp().executeUpdate(
							"update user_order set status=11 where id="
							+ order.getOriginOrderId());
					voOrder oriOrder = adminService.getOrder(order
							.getOriginOrderId());
				}

				// log记录

				StockAdminHistoryBean log = new StockAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(StockAdminHistoryBean.ORDER_STOCK_STATUS2);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("订单出货操作：" + bean.getName() + "：申请出货");
				log.setType(StockAdminHistoryBean.CHANGE);
				service.addStockAdminHistory(log);

				service.getDbOp().executeUpdate("update user_order set stockout_deal=2 where id=" + order.getId());

//				if(order.getBuyMode() != Constants.BUY_TYPE_NIFFER && !order.getCode().startsWith("S")){
//					if(userOrderService.getUserOrderProductHistoryCount("order_id=" + order.getId()) > 0){
//
//					} else {
//						userOrderService.logUserOrderProduct(order, UserOrderProductHistoryBean.TYPE_NORMAL);
//					}
//				}

				// 提交事务
				service.getDbOp().commitTransaction();
				service.getDbOp().startTransaction();
				int stockoutStatus = 2;

				//判断是否为缺货发货成功
				ResultSet rs = service.getDbOp().executeQuery("select stockout_deal,TIMESTAMPDIFF(SECOND, lack_datetime, '"+DateUtil.getNow()+"') t from user_order_lack_deal where id = " + order.getId());
				if(rs.next()){
					//int _stockoutStatus = rs.getInt(1);
					int time = rs.getInt(2);
					if(time > 7200){//_stockoutStatus >= 4 && 
						stockoutStatus = 8;
					}
				}
				StringBuilder logContent = new StringBuilder();
				//修改了 发货状态
				logContent.append("[发货状态:");
				logContent.append(order.getStockoutDeal());
				logContent.append("->");
				logContent.append(stockoutStatus);
				logContent.append("]");

				if(logContent.length() > 0){
					IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
					try{
						OrderAdminLogBean adminLog = new OrderAdminLogBean();
						adminLog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
						adminLog.setUserId(user.getId());
						adminLog.setUsername(user.getUsername());
						adminLog.setOrderId(order.getId());
						adminLog.setOrderCode(order.getCode());
						adminLog.setCreateDatetime(DateUtil.getNow());
						adminLog.setContent(logContent.toString());
						adminService.addOrderAdminStatusLog(logService, -1, -1, order.getStockoutDeal(), stockoutStatus, adminLog);
						logService.addOrderAdminLog(adminLog);
					} catch(Exception e) {
						e.printStackTrace();
					} finally {
						logService.releaseAll();
					}
				}

				service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal="+stockoutStatus+" where id=" + order.getId());
				service.getDbOp().commitTransaction();
				if(request!=null){
					request.setAttribute("soId", String.valueOf(id));
//					request.setAttribute("stockStatus", String.valueOf(stockStatus));
				}
			} catch (Exception e){
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				adminService.close();
				dbOpSMS.release();
			}
		}
	}
	
	/**
	 * 自动分配快递公司
	 * @param buyMode 购买方式
	 * @param address 订单地址
	 * @param stockArea 发货地区
	 * @return
	 */
	public int getDeliver(int buyMode,String address,int stockArea){
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
		int deliver=0;
		try{
			if(stockArea==3){//增城分配快递公司
				//自动分配快递公司  只有货到付款的才自动分配快递公司 start
	        	if(buyMode==0){
					if(address.substring(0,4).contains("福建省")){//通路速递
						deliver=14;
					}else if(address.substring(0,4).contains("江苏")){//赛澳递江苏
						deliver=17;
					}else if(address.substring(0,4).contains("上海")){//赛澳递上海
						deliver=18;
					}else if((address.substring(0,4).contains("北京")
							||address.substring(0,4).contains("天津")
							||address.substring(0,4).contains("山东"))
							&&(!address.contains("山东省烟台市长岛"))){//如风达
						deliver=16;
					}else if (address.substring(0,4).contains("浙江")){//如风达浙江，通路速递浙江
						//如风达浙江已发货数量
						int rfdCount= service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=22 and status<>3");
						if(rfdCount<400){//如风达浙江每天限量400
							deliver=22;
						}else{
							//通路速递浙江已发货数量
//							int tlCount=service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=20 and status<>3");
//							if(tlCount<300){//2013-7-17取消单量限制
								deliver=20;
//							}
						}
					}else if (address.substring(0,4).contains("广东")){//银捷速递，通路速递广东
						//银捷速递已发货数量
						int yjCount= service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=21 and status<>3");
						if(yjCount<300){//银捷速递每天限量300(2013-06-17,liangkun)
							deliver=21;
						}else{
							int emsCount= service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=11 and status<>3");
							if(emsCount<2000){//ems省内每天限量2000(2013-06-17,liangkun)
								deliver=11;
							}else{
							//通路速递广东已发货数量
//							int tlCount=service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=19 and status<>3");
//							if(tlCount<1200){//通路速递广东每天限量900-1200(2013-03-26,zhaolin)
								deliver=19;
//							}
							}
						}
					}else if(address.substring(0,4).contains("广西")){//广西邮政
						deliver=25;
					}else if(address.substring(0,4).contains("四川")){//宅急送四川
						deliver=26;
					}else if(address.substring(0,4).contains("重庆")){//宅急送重庆
						deliver=27;
					}else if(address.substring(0,4).contains("江西")){//江西邮政
						deliver=28;
					}else if(address.substring(0,4).contains("湖南")){//湖南邮政
						deliver=31;
					}else if(address.substring(0,4).contains("湖北")){//湖北邮政
						deliver=32;
					}else if(address.substring(0,4).contains("河北")
							||address.substring(0,4).contains("吉林")){//飞狐快递
						deliver=33;
					}else if(address.substring(0,4).contains("贵州")){//贵州邮政
						deliver=35;
					}else if(address.substring(0,4).contains("河南")){//河南大河速递
						deliver=36;
					}
					
					//2013-6-17取消广州宅急送分配
//					if(deliver==0){//前面没分快递公司，查询宅急送是否能送
//						int count=0;
//			        	if(DateUtil.compareTime(DateUtil.getNow(), StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00")==1){
//			        		count = service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00' and '" + DateUtil.getNow()+"' and deliver=10 and status<>3");
//						}else{
//							count = service.getOrderStockCount("create_datetime between '" + DateUtil.getLastDay()[0] + " 19:30:00' and '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00' and deliver=12 and status<>3");
//						}
//			        	if(count<1200){
//			        		String sql="select c.deliver_id from user_order a join user_order_extend_info b on a.code=b.order_code join deliver_area c " +
//									   " on ((b.add_id3=area_id and c.type=2) or (b.add_id4=area_id and c.type=3)) where a.code='"+order.getCode()+"'";
//							ResultSet rsDeliver=service.getDbOp().executeQuery(sql);
//							int deliverId = 0;
//							 if (rsDeliver.next()) {
//								 deliverId = rsDeliver.getInt("c.deliver_id");
//							}
//							rsDeliver.close();
//							if(deliverId!= 0){
//								deliver=deliverId;
//							}
//			        	}
//					}
					if(deliver==0){//没有分配快递公司，则分配给EMS
						if(address.substring(0,4).contains("广东")){//广东省速递局
							deliver=11;
						}else{//广速省外
							deliver=9;
						}
					}
				}else{//非货到付款的订单，分配给EMS省内或EMS省外
					if(address.substring(0,4).contains("广东")){//广东省速递局
						deliver=11;
					}else{//广速省外
						deliver=9;
					}
				}
			}else if(stockArea==4){//无锡分配快递公司
				if(buyMode==0&&!address.contains("邮")){
					if(address.substring(0,4).contains("山东")){//如风达
						deliver=16;
					}else if(address.substring(0,4).contains("江苏")){//赛澳递江苏
						deliver=17;
					}else if(address.substring(0,4).contains("上海")){//赛澳递上海
						deliver=18;
					}else if(address.substring(0,4).contains("安徽")){//2013-3-29改为分配上海无疆
						deliver=29;
					}else if(address.substring(0,4).contains("浙江")){//如风达浙江或通路速递浙江
						deliver=22;
					}else if(address.substring(0,4).contains("河北")
							||address.substring(0,4).contains("吉林")){//飞狐快递
						deliver=33;
					}else if(address.substring(0,4).contains("河南")){//河南大河速递
						deliver=36;
					}else{//非无锡配送范围，分配给上海无疆
						int shwjCount=service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=29 and status<>3");
						if(shwjCount<400){//上海无疆限量400单，其余分给无锡邮政
							deliver=29;
						}else{
							deliver=37;//无锡邮政
						}
					}
				}else if(buyMode==0){//货到付款
					int shwjCount=service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=29 and status<>3");
					if(shwjCount<400){//上海无疆限量400单，其余分给无锡邮政
						deliver=29;
					}else{
						deliver=37;//无锡邮政
					}
				}else{
					deliver=29;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		} finally{
			dbOp.release();
		}
		return deliver;
	}
	
	public void addOrderStock1(HttpServletRequest request, HttpServletResponse response,StringBuilder querzyUserCode) {
		voUser user = null;
		if(request!=null){
			user=(voUser)request.getSession().getAttribute("userView");
		}else{
			user=new voUser();
			user.setId(0);
			user.setUsername("自动申请出库");
		}
		if (user == null) {
			return;
		}
		synchronized(orderStockLock){
			String orderCode=null;
			if(querzyUserCode==null){//自动申请出库的时候传过来的 订单号
				orderCode =StringUtil.dealParam(request.getParameter("orderCode"));
			}else{
				orderCode= querzyUserCode.toString();
			}
			if (StringUtil.convertNull(orderCode).equals("")) {
				return;
			}

			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
			IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			//IUserOrderService userOrderService = ServiceFactory.createUserOrderService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				voOrder order = adminService.getOrder("code = '" + orderCode + "'");
				if (order == null) {
					adminService.close();
					return;
				}
				if (order.getStatus() != 3) {
					adminService.close();
					return;
				}
				if (order.getStockout() == 1) {
					adminService.close();
					return;
				}

				List orderProductList = adminService.getOrderProducts(order.getId());
				List orderPresentList = adminService.getOrderPresents(order.getId());

				List productList = new ArrayList();
				OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and order_code = '" + orderCode + "'");
				if (oper != null) {
					return;
				}

				Iterator productIter = orderProductList.listIterator();
				while(productIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)productIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						if(ppList == null || ppList.size() == 0){
//							request.setAttribute("tip", "套装产品"+product.getCode()+"信息异常，无法申请出库！");
							return;
						}
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						productList.add(vop);
					}
				}
				productIter = orderPresentList.listIterator();
				while(productIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)productIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						if(ppList == null || ppList.size() == 0){
							return;
						}
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						productList.add(vop);
					}
				}

//				int stockStatus = checkStock(productList);

				int orderStockArea = ProductStockBean.AREA_GF;
				orderStockArea=checkStock2(productList,order);

//				Date changStockAreaTime = DateUtil.parseDate(OrderStockAction.CHANGE_STOCK_ARER_TIME,DateUtil.normalTimeFormat);
//				if(System.currentTimeMillis()>changStockAreaTime.getTime()){   //优先增城发货
//					if(stockStatus == 2 || stockStatus == 7){
//						// 从广分发货
//						orderStockArea = ProductStockBean.AREA_GF;
//					} else if(stockStatus == 0 || stockStatus == 1 || stockStatus == 5 || stockStatus == 6){
//						// 从增城发货
//						orderStockArea = ProductStockBean.AREA_ZC;
//					} else {
//						// 提示库存不足
//						if(order.getStockoutDeal() == 7){
//							service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
//							service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
//						}
//						if(querzyUserCode!=null){//定时任务调用 直接返回
//							querzyUserCode.delete(0, querzyUserCode.length());
//							querzyUserCode.append("lackOrder");
//							return;
//						}
//						return;
//					}
//				}else{ //优先芳村发货
//					if(stockStatus == 0 || stockStatus == 2 || stockStatus == 5 || stockStatus == 7){
//						// 从广分发货
//						orderStockArea = ProductStockBean.AREA_GF;
//					} else if(stockStatus == 1 || stockStatus == 6){
//						// 从增城发货
//						orderStockArea = ProductStockBean.AREA_ZC;
//					} else {
//						// 提示库存不足
//						if(order.getStockoutDeal() == 7){
//							service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
//							service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
//						}
//						if(querzyUserCode!=null){//定时任务调用 直接返回
//							querzyUserCode.delete(0, querzyUserCode.length());
//							querzyUserCode.append("lackOrder");
//							return;
//						}
////						request.setAttribute("tip", "合格库库存不足，请查看调拨能否解决问题。");
//						return;
//					}
//				}
				
//				if(order.getBuyMode()!=0||order.getAddress().contains("邮")){
//					if(stockStatus == 0 || stockStatus == 1 || stockStatus == 5 || stockStatus == 6){
//						orderStockArea=3;
//					}else{
//						orderStockArea=0;
//					}
//				}else{
//					orderStockArea=checkStock2(productList,order);
//				}
				if(orderStockArea==0){//缺货
					// 提示库存不足
					if(order.getStockoutDeal() == 7){
						service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
						service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
					}
					if(querzyUserCode!=null){//定时任务调用 直接返回
						querzyUserCode.delete(0, querzyUserCode.length());
						querzyUserCode.append("lackOrder");
						return;
					}
					request.setAttribute("tip", "合格库库存不足，请查看调拨能否解决问题。");
					request.setAttribute("result", "failure");
					request.setAttribute("oper", oper);
					return;
				}

				String name = orderCode + "_" + DateUtil.getNow().substring(0, 10) + "_出货";
				service.getDbOp().startTransaction();

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
				
				OrderStockBean bean = new OrderStockBean();
				bean.setCreateDatetime(DateUtil.getNow());
				bean.setName(name);
				bean.setOrderCode("");
				bean.setRemark("");
				bean.setStatus(OrderStockBean.STATUS1);
				bean.setOrderCode(orderCode);
				bean.setOrderId(order.getId());
//				bean.setCode(CodeUtil.getOrderStockCode());
				bean.setCode("CK" + sdf.format(cal.getTime()));//此处设定order_stock的初始编号
				bean.setStockArea(orderStockArea);
				bean.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
				List orderProductList2 = adminService.getOrderProductsSplit(order.getId());
				List orderPresentList2 = adminService.getOrderPresentsSplit(order.getId());
				orderProductList2.addAll(orderPresentList2);
				Iterator detailIter = orderProductList2.listIterator();
				while(detailIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)detailIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + tempProduct.getBzzhongliang());
						}
					} else {
						order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + product.getBzzhongliang());
					}
				}
				StringBuffer orderSB = new StringBuffer();
				//文齐辉修改2011/05/27添加订单出库时自动分配订单的产品分类
				//只计算订单产品，不计赠品 ,如果分类==0
//				if(order.getProductType()==0){
//					Iterator tmpIter = orderProductList.iterator();
//					float tmpPrice=0f;
//					int orderPType=0;
//					while(tmpIter.hasNext()){
//						voOrderProduct vop = (voOrderProduct)tmpIter.next();
//						UserOrderPackageTypeBean uopType = userOrderService.getUserOrderPackageType("product_catalog = "+vop.getParentId1());
//						if(uopType!=null){
//							float productPrice = vop.getCount()*vop.getDiscountPrice();
//							if(productPrice>tmpPrice){
//								tmpPrice = productPrice;
//								orderPType = uopType.getTypeId();
//							}
//						}
//					}
//					if(orderPType!=0)
//						orderSB.append("product_type = ").append(orderPType);
//				}
				if(orderSB.length()>0)
					adminService.modifyOrder(orderSB.toString(), "id=" + order.getId());

				bean.setLastOperTime(DateUtil.getNow());
				//检查订单中商品的库存情况
				if (checkStockInArea(orderProductList, bean.getStockArea())) {
					bean.setStatusStock(OrderStockBean.STATUS1_STOCK);
				} else {
					bean.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
				}
				bean.setCreateUserId(user.getId());
				
				//根据地址分配快递公司
				int deliver=getDeliver(order.getBuyMode(), order.getAddress(),bean.getStockArea());
				if(deliver!=0){
					adminService.modifyOrder("deliver="+deliver,"id="+order.getId());
					bean.setDeliver(deliver);
				}
				
				if (!service.addOrderStock(bean)) {
//					request.setAttribute("tip", "添加失败！");
					service.getDbOp().rollbackTransaction();
					return;
				}
				int id = service.getDbOp().getLastInsertId();
				bean.setId(id);

				//此处修改order_stock.code
				String newCode = null;
				if(id > 999999){
					String strId = String.valueOf(id);
					newCode = strId.substring(strId.length()- 6, strId.length());
				} else {
					DecimalFormat df2 = new DecimalFormat("000000");
					newCode = df2.format(id);
				}

				StringBuilder updateBuf = new StringBuilder();
				updateBuf.append("update order_stock set code=concat(code,'").append(newCode).append("') where id=").append(id);
				adminService.getDbOperation().executeUpdate(updateBuf.toString());
				
				adminService.deleteOrderProductsSplit(bean.getOrderId());
				adminService.deleteOrderPresentsSplit(bean.getOrderId());

				//添加订单中商品的出货记录
				Iterator itr = orderProductList.iterator();
				voOrderProduct op = null;
				int shId = 0;
				OrderStockProductBean sh = null;
				while (itr.hasNext()) {
					op = (voOrderProduct) itr.next();
					voProduct product = adminService.getProduct(op.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(op.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());

							voOrderProduct vop = adminService.getOrderProductSplit(bean.getOrderId(), tempVOP.getCode());
							if(vop != null){
								// 如果已经有了这个商品，则增加数量
								if(!adminService.updateOrderProductSplit("count=(count + " + tempVOP.getCount() + ") ", vop.getId())){
//									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									service.getDbOp().rollbackTransaction();
									return;
								}
							} else {
								//如果还没有这个商品，则添加
								if(!adminService.addOrderProductSplit(bean.getOrderId(), tempVOP.getCode(), tempVOP.getCount())){
//									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
							sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + tempVOP.getProductId());
							if(sh != null){
								sh.setStockoutCount(sh.getStockoutCount() + tempVOP.getCount());
								service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
							} else {
								sh = new OrderStockProductBean();
								sh.setCreateDatetime(DateUtil.getNow());
								sh.setDealDatetime(null);
								sh.setOrderStockId(id);
								sh.setProductCode(tempVOP.getCode());
								sh.setProductId(tempVOP.getProductId());
								sh.setRemark("");
								sh.setStatus(OrderStockProductBean.UNDEAL);
								ProductStockBean ps = null ;

								// 售后换货订单也与普通订单一样，从合格库发货
								ps = psService.getProductStock("product_id=" + tempVOP.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
								sh.setStockoutId(ps.getId());
								sh.setStockoutCount(tempVOP.getCount());
								sh.setStockArea(bean.getStockArea());
								sh.setStockType(bean.getStockType());
								if(!service.addOrderStockProduct(sh)){
//									request.setAttribute("tip", "添加失败！");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
						}
					} else {
						voOrderProduct vop = adminService.getOrderProductSplit(bean.getOrderId(), op.getCode());
						if(vop != null){
							// 如果已经有了这个商品，则增加数量
							if(!adminService.updateOrderProductSplit("count=(count + " + op.getCount() + ") ", vop.getId())){
//								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								service.getDbOp().rollbackTransaction();
								return;
							}

						} else {
							if(!adminService.addOrderProductSplit(bean.getOrderId(), op.getCode(), op.getCount())){
//								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
						sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + op.getProductId());
						if(sh != null){
							sh.setStockoutCount(sh.getStockoutCount() + op.getCount());
							service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
						} else {
							sh = new OrderStockProductBean();
							sh.setCreateDatetime(DateUtil.getNow());
							sh.setDealDatetime(null);
							sh.setOrderStockId(id);
							sh.setProductCode(op.getCode());
							sh.setProductId(op.getProductId());
							sh.setRemark("");
							sh.setStatus(OrderStockProductBean.UNDEAL);
							ProductStockBean ps = psService.getProductStock("product_id=" + op.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
							sh.setStockoutId(ps.getId());
							sh.setStockoutCount(op.getCount());
							sh.setStockArea(bean.getStockArea());
							sh.setStockType(bean.getStockType());
							if(!service.addOrderStockProduct(sh)){
//								request.setAttribute("tip", "添加失败！");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
					}
				}
				//添加订单中赠品的出货记录
				itr = orderPresentList.iterator();
				op = null;
				shId = 0;
				sh = null;
				while (itr.hasNext()) {
					op = (voOrderProduct) itr.next();
					voProduct product = adminService.getProduct(op.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(op.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());

							voOrderProduct vop = adminService.getOrderPresentSplit(bean.getOrderId(), tempVOP.getCode());
							if(vop != null){
								if(!adminService.updateOrderPresentSplit("count=(count + " + tempVOP.getCount() + ") ", vop.getId())){
//									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									service.getDbOp().rollbackTransaction();
									return;
								}
							} else {
								if(!adminService.addOrderPresentSplit(bean.getOrderId(), tempVOP.getCode(), tempVOP.getCount())){
//									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
							sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + tempVOP.getProductId());
							if(sh != null){
								sh.setStockoutCount(sh.getStockoutCount() + tempVOP.getCount());
								service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
							} else {
								sh = new OrderStockProductBean();
								sh.setCreateDatetime(DateUtil.getNow());
								sh.setDealDatetime(null);
								sh.setOrderStockId(id);
								sh.setProductCode(tempVOP.getCode());
								sh.setProductId(tempVOP.getProductId());
								sh.setRemark("");
								sh.setStatus(OrderStockProductBean.UNDEAL);
								ProductStockBean ps = psService.getProductStock("product_id=" + tempVOP.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
								sh.setStockoutId(ps.getId());
								sh.setStockoutCount(tempVOP.getCount());
								sh.setStockArea(bean.getStockArea());
								sh.setStockType(bean.getStockType());
								if(!service.addOrderStockProduct(sh)){
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
						}
					} else {
						voOrderProduct vop = adminService.getOrderPresentSplit(bean.getOrderId(), op.getCode());
						if(vop != null){
							// 如果已经有了这个商品，则增加数量
							if(!adminService.updateOrderPresentSplit("count=(count + " + op.getCount() + ") ", vop.getId())){
//								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								service.getDbOp().rollbackTransaction();
								return;
							}
						} else {
							if(!adminService.addOrderPresentSplit(bean.getOrderId(), op.getCode(), op.getCount())){
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
						sh = service.getOrderStockProduct("order_stock_id=" + bean.getId() + " and product_id=" + op.getProductId());
						if(sh != null){
							sh.setStockoutCount(sh.getStockoutCount() + op.getCount());
							service.updateOrderStockProduct("stockout_count=" + sh.getStockoutCount(), "id=" + sh.getId());
						} else {
							sh = new OrderStockProductBean();
							sh.setCreateDatetime(DateUtil.getNow());
							sh.setDealDatetime(null);
							sh.setOrderStockId(id);
							sh.setProductCode(op.getCode());
							sh.setProductId(op.getProductId());
							sh.setRemark("");
							sh.setStatus(OrderStockProductBean.UNDEAL);
							ProductStockBean ps = psService.getProductStock("product_id=" + op.getProductId() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and area=" + bean.getStockArea());
							sh.setStockoutId(ps.getId());
							sh.setStockoutCount(op.getCount());
							sh.setStockArea(bean.getStockArea());
							sh.setStockType(bean.getStockType());
							if(!service.addOrderStockProduct(sh)){
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
					}

				}
				//计算订单中的sku数量，印刷品除外
            	String dmSql ="select type_id from user_order_package_type where name ='印刷品'";
            	ResultSet rsDm=service.getDbOp().executeQuery(dmSql);
            	String isDm = "";
            	if (rsDm.next()) {
            		isDm = " AND (e.product_type_id<>"+rsDm.getInt("type_id") +" or e.product_type_id is null)";
				}
            	rsDm.close();
				String skuCountSql ="SELECT COUNT(d.id) AS skuCount FROM user_order a " +
			            			"JOIN order_stock b ON a.id=b.order_id " +
			            			"JOIN order_stock_product c ON b.id=c.order_stock_id " +
			            			"JOIN product d ON c.product_id=d.id " +
            	                    "LEFT JOIN product_ware_property e ON d.id=e.product_id " +
            	                    "WHERE b.status<>3 AND a.id="+ order.getId()+isDm;
            	ResultSet rsSkuCount=service.getDbOp().executeQuery(skuCountSql);
            	int skuCount = 0;
            	if (rsSkuCount.next()) {
            		skuCount = rsSkuCount.getInt("skuCount");
				}
            	adminService.modifyOrder("product_count=" + skuCount, "id=" + order.getId());
            	rsSkuCount.close();
				// 订单 确认申请出货
				String condition = "order_stock_id = " + bean.getId() + " and status = " + OrderStockProductBean.UNDEAL;
				ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");
				voProduct product = null;
				String set = null;
				itr = shList.iterator();

				bean.setOrder(adminService.getOrder("code='" + bean.getOrderCode() + "'"));
				if (bean.getOrder() == null) {
//					request.setAttribute("tip", "找不到对应的订单，不能修改！");
					service.getDbOp().rollbackTransaction();
					return;
				}
				orderProductList = adminService.getOrderProductsSplit(bean.getOrder().getId());
				Iterator iter = orderProductList.listIterator();
				while (iter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) iter.next();
					vop.setPsList(psService.getProductStockList("product_id="
							+ vop.getProductId(), -1, -1, null));
				}
				if (checkStockInArea(orderProductList, bean.getStockArea())) {
					if (bean.getStatusStock() == OrderStockBean.STATUS1_NO_STOCK) {
						bean
						.setStatusStock(OrderStockBean.STATUS2_FROM_NO_STOCK);
					} else if (bean.getStatusStock() == OrderStockBean.STATUS1_STOCK) {
						bean.setStatusStock(OrderStockBean.STATUS2_FROM_STOCK);
					}
				} else {
					bean.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
				}

				while (itr.hasNext()) {
					sh = (OrderStockProductBean) itr.next();
					// 出库
					product = adminService.getProduct(sh.getProductId(),
							service.getDbOp());
					ProductStockBean ps = psService.getProductStock("id="
							+ sh.getStockoutId());

					if (sh.getStockoutCount() > ps.getStock()) {
						if(order.getStockoutDeal() == 7){
							service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
							service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
						}
//						request.setAttribute("tip", product.getName()+ "的库存不足！");
						service.getDbOp().rollbackTransaction();
						return;
					}
					set = "status = " + OrderStockProductBean.DEALED
					+ ", remark = concat(remark, '(操作前库存"
					+ ps.getStock() + "锁定库存" + ps.getLockCount()
					+ ",操作后库存"
					+ (ps.getStock() - sh.getStockoutCount()) + "锁定库存"
					+ (ps.getLockCount() + sh.getStockoutCount())
					+ ")'), deal_datetime = now()";
					service.updateOrderStockProduct(set, "id = " + sh.getId());
					if (!psService.updateProductStockCount(sh.getStockoutId(),
							-sh.getStockoutCount())) {
//						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						service.getDbOp().rollbackTransaction();
						return;
					}
					if (!psService.updateProductLockCount(sh.getStockoutId(),
							sh.getStockoutCount())) {
//						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						service.getDbOp().rollbackTransaction();
						return;
					}
					if (product.getParentId1() == 123 || product.getParentId1() == 143
							|| product.getParentId1() == 316 || product.getParentId1() == 317
							|| product.getParentId1() == 119 || product.getParentId1() == 340
							|| product.getParentId1() == 1385 || product.getParentId1() == 1425
							|| product.getParentId1() == 544 || product.getParentId1() == 545
							|| product.getParentId1() == 458 || product.getParentId1() == 459 
							|| product.getParentId1() == 401 || product.getParentId1() == 136 
							|| product.getParentId2() == 203 || product.getParentId2() == 204 
							|| product.getParentId2() == 205 || product.getParentId1() == 699
							|| product.getParentId1() == 145 || product.getParentId1() == 151
							|| product.getParentId1() == 197 || product.getParentId1() == 505
							|| product.getParentId1() == 163 || product.getParentId1() == 690
							|| product.getParentId1() == 908|| product.getParentId1() == 752
							|| product.getParentId1() == 803
							|| product.getParentId1() == 183 || product.getParentId1() == 184
							|| product.getParentId1() == 1093 || product.getParentId1() == 1094
							|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222) {
						psService.checkProductStatus(sh.getProductId());
					}

					// 订单商品表、订单赠品表 中记录的价格是 出库瞬间的 库存价格
					service.getDbOp().executeUpdate("update user_order_product set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_present set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_product_split set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
					service.getDbOp().executeUpdate("update user_order_present_split set price3=" + product.getPrice5() + " where order_id=" + bean.getOrder().getId() + " and product_id=" + product.getId());
				}

				service.updateOrderStock("status_stock="
						+ bean.getStatusStock() + ", last_oper_time = now()",
						"id = " + bean.getId());

				service.updateOrderStock("status = " + OrderStockBean.STATUS2
						+ ", last_oper_time = now()", "id = " + bean.getId());

				service.getDbOp().executeUpdate(
						"update user_order set stockout = 1, confirm_datetime=now() where code = '"
						+ bean.getOrderCode() + "'");
				if (bean.getOrder().getAddress().indexOf("电话通知") == -1) {
					service.getDbOp().executeUpdate(
							"update user_order set address=concat(address, '(电话通知)') where code = '"
							+ bean.getOrderCode() + "'");
				}
				order = adminService.getOrder("code='" + bean.getOrderCode() + "'");
				// 如果该订单存在原订单
				if (order.getOriginOrderId() > 0) {
					// 将原订单的状态改为已取消, confirm_datetime 改为申请的日期
					service.getDbOp().executeUpdate(
							"update user_order set status=11 where id="
							+ order.getOriginOrderId());
					voOrder oriOrder = adminService.getOrder(order
							.getOriginOrderId());
				}

				// log记录

				StockAdminHistoryBean log = new StockAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(StockAdminHistoryBean.ORDER_STOCK_STATUS2);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("订单出货操作：" + bean.getName() + "：申请出货");
				log.setType(StockAdminHistoryBean.CHANGE);
				service.addStockAdminHistory(log);

				service.getDbOp().executeUpdate("update user_order set stockout_deal=2 where id=" + order.getId());

//				if(order.getBuyMode() != Constants.BUY_TYPE_NIFFER && !order.getCode().startsWith("S")){
//					if(userOrderService.getUserOrderProductHistoryCount("order_id=" + order.getId()) > 0){
//
//					} else {
//						userOrderService.logUserOrderProduct(order, UserOrderProductHistoryBean.TYPE_NORMAL);
//					}
//				}

				// 提交事务
				service.getDbOp().commitTransaction();
				service.getDbOp().startTransaction();
				int stockoutStatus = 2;

				//判断是否为缺货发货成功
				ResultSet rs = service.getDbOp().executeQuery("select stockout_deal,TIMESTAMPDIFF(SECOND, lack_datetime, '"+DateUtil.getNow()+"') t from user_order_lack_deal where id = " + order.getId());
				if(rs.next()){
					//int _stockoutStatus = rs.getInt(1);
					int time = rs.getInt(2);
					if(time > 7200){//_stockoutStatus >= 4 && 
						stockoutStatus = 8;
					}
				}
				StringBuilder logContent = new StringBuilder();
				//修改了 发货状态
				logContent.append("[发货状态:");
				logContent.append(order.getStockoutDeal());
				logContent.append("->");
				logContent.append(stockoutStatus);
				logContent.append("]");

				if(logContent.length() > 0){
					IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
					try{
						OrderAdminLogBean adminLog = new OrderAdminLogBean();
						adminLog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
						adminLog.setUserId(user.getId());
						adminLog.setUsername(user.getUsername());
						adminLog.setOrderId(order.getId());
						adminLog.setOrderCode(order.getCode());
						adminLog.setCreateDatetime(DateUtil.getNow());
						adminLog.setContent(logContent.toString());
						adminService.addOrderAdminStatusLog(logService, -1, -1, order.getStockoutDeal(), stockoutStatus, adminLog);
						logService.addOrderAdminLog(adminLog);
					} catch(Exception e) {
						e.printStackTrace();
					} finally {
						logService.releaseAll();
					}
				}

				service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal="+stockoutStatus+" where id=" + order.getId());
				service.getDbOp().commitTransaction();
				if(request!=null){
					request.setAttribute("soId", String.valueOf(id));
//					request.setAttribute("stockStatus", String.valueOf(stockStatus));
				}
			} catch (Exception e){
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				adminService.close();
			}
		}
	}
	
	/**
	 * 作者：李北金
	 * 
	 * 创建日期：2007-11-19
	 * 
	 * 说明：检查订单商品库存是否充足。
	 * 
	 * 参数及返回值说明：
	 * 
	 * 0表示两地库存都充足。/三地库存充足
	 * 
	 * 1表示北京库存充足。/北库库存充足/广分缺货/广速缺货
	 * 
	 * 2表示广东库存充足。/广分库存充足/北库缺货/广速缺货
	 * 
	 * 3表示两地库存都不足。/三地库存都不足
	 * 
	 * 4表示广速库存充足/北库缺货/广分缺货
	 * 
	 * 5广速缺货
	 * 
	 * 6广分缺货
	 * 
	 * 7北库缺货
	 * 
	 * zt修改(20090509)
	 * 
	 * @param orderProductList
	 * @return
	 */
	public int checkStock(List orderProductList) {
		if (orderProductList == null) {
			return 3;
		}

		Iterator itr = orderProductList.iterator();
		boolean zc = true;
		boolean gd = false; //2012-05-30  芳村停止发货，只增城发货
		boolean gs = false;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				zc = false;
			}
			//            if (op.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
			//                gd = false;
			//            }
			//            if (op.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
			//                gs = false;
			//            }
		}
		if (zc && gd && gs) {
			return 0;
		}
		if (zc && !gd && !gs) {
			return 1;
		}
		if (gd && !zc && !gs) {
			return 2;
		}
		if (gs && !zc && !gd) {
			return 4;
		}
		if (zc && gd && !gs){
			return 5;
		}
		if (zc && gs && !gd){
			return 6;
		}
		if (gd && gs && !zc){
			return 7;
		}
		return 3;
	}
	
	/**
	 * 按照地址和库存确认发货地区
	 * @param orderProductList
	 * @return 0代表缺货，3代表增城，4代表无锡
	 */
	public int checkStock2(List orderProductList,voOrder order) {
		boolean zc = true;
		boolean wx = true;
		String address=order.getAddress();//订单地址
		int area=0;//属于哪个仓库的配送区域
		
		if(address.substring(0,4).contains("山东")
				||address.substring(0,4).contains("江苏")
				||address.substring(0,4).contains("上海")
				||address.substring(0,4).contains("安徽")
				||address.substring(0,4).contains("浙江")){//无锡发货范围
			area=4;
		}else{//增城发货范围
			area=3;
		}
		
		if(area==4){//无锡发货范围
			Iterator itr = orderProductList.iterator();
			voOrderProduct op = null;
			while (itr.hasNext()) {
				op = (voOrderProduct) itr.next();
				if (op.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
					wx = false;
				}
			}
			if(wx==false){//无锡缺货
				Iterator itr2 = orderProductList.iterator();
				voOrderProduct op2 = null;
				while (itr2.hasNext()) {
					op2 = (voOrderProduct) itr2.next();
					if (op2.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op2.getCount()) {
						zc = false;
					}
				}
				if(zc==false){//增城缺货
					return 0;//缺货，不能发货
				}else{
					return 3;//可以从增城发货
				}
			}else{
				return 4;//可以从无锡发货
			}
		}else if(area==3){//增城发货范围
			Iterator itr = orderProductList.iterator();
			voOrderProduct op = null;
			while (itr.hasNext()) {
				op = (voOrderProduct) itr.next();
				if (op.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
					zc = false;
				}
			}
			if(zc==false){//增城缺货
				Iterator itr2 = orderProductList.iterator();
				voOrderProduct op2 = null;
				while (itr2.hasNext()) {
					op2 = (voOrderProduct) itr2.next();
					if (op2.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) < op2.getCount()) {
						wx = false;
					}
				}
				if(wx==false){//无锡缺货
					return 0;//缺货，不能发货
				}else{
					return 4;//可以从无锡发货
				}
			}else{
				return 3;//可以从增城发货
			}
		}
		return 0;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-11
	 * 
	 * 说明：检查没有库存的地方
	 * 
	 * 参数及返回值说明：
	 * 
	 * 0表示两地库存都充足。/三地库存充足
	 * 
	 * 1北库库存不充足
	 * 
	 * 2广分库存不充足
	 * 
	 * 3广速库存不充足
	 * 
	 * 9三地库存都不足
	 * 
	 * @param orderProductList
	 * @return
	 */
	public int checkNoStock(List orderProductList) {
		if (orderProductList == null) {
			return 3;
		}

		Iterator itr = orderProductList.iterator();
		boolean bj = true;
		boolean gd = true;
		boolean gs = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				bj = false;
			}
			if (op.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				gd = false;
			}
			if (op.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				gs = false;
			}
		}
		if (bj && gd && gs) {
			return 0;
		}
		if (!bj) {
			return 1;
		}
		if (!gd) {
			return 2;
		}
		if (!gs) {
			return 3;
		}
		return 9;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-9
	 * 
	 * 说明：判断订单出货地区的库存是否充足
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderProductList
	 * @param area
	 *            0:北库 <br/>1:广分 <br/>2:广速<br/>
	 * @return
	 */
	public boolean checkStockInArea(List orderProductList, int area) {
		if (orderProductList == null) {
			return false;
		}

		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(area, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-9
	 * 
	 * 说明：订单出货操作
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void orderStock(HttpServletRequest request, HttpServletResponse response) {
		if("null".equals(request.getParameter("id"))){
			String url = (String) request.getAttribute("url");
			System.out.println("OrderStockAction.orderStock-"+url);  
			request.setAttribute("tip", "出库单编号不能为空！");
			request.setAttribute("result", "failure");
			return;
		}
		int id = StringUtil.StringToId(request.getParameter("id"));
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		boolean flag = false;
		try {
			OrderStockBean bean = service.getOrderStock("id = " + id);

			if (bean == null) {
				request.setAttribute("tip", "当前记录不存在或者已经处理！");
				request.setAttribute("result", "failure");
				return;
			}
			//相关的出库记录
			String condition = "order_stock_id = " + bean.getId();
			ArrayList outList = service.getOrderStockProductList(condition, 0, -1, "id");
			Iterator outIter = outList.listIterator();
			while (outIter.hasNext()) {
				OrderStockProductBean shb = (OrderStockProductBean) outIter.next();
				shb.setProduct(adminService.getProduct(shb.getProductId()));
				shb.getProduct().setPsList(psService.getProductStockList("product_id=" + shb.getProductId(), -1, -1, null));
				// 文齐辉添加查找产品条形码
				shb.setProductBarcodeVO(bcmService.getProductBarcode("product_id="+shb.getProductId()+" and barcode_status=0"));
				if(!flag){
					if(imeiService.selectImeiByProductId(shb.getProductId())){
						flag = true;
					}
				}
			}
			//相关的产品和赠品
			condition = "a.id in (select distinct product_id from order_stock_product where order_stock_id = " + bean.getId() + ")";
			voOrder order = adminService.getOrder("code = '" + bean.getOrderCode() + "'");
			bean.setOrder(order);
			List orderProductList = adminService.getOrderProductsSplit(order.getId());
			Iterator iter = orderProductList.listIterator();
			while(iter.hasNext()){
				voOrderProduct vop = (voOrderProduct)iter.next();
				vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
			}
			int stockStatus = checkStock(orderProductList); //检查订单内商品的库存情况
			List orderPresentList = adminService.getOrderPresentsSplit(order.getId());

			//如果订单内的商品库存充足，而且该订单有赠品，则判断赠品库存是否充足
			if (stockStatus == 0 && orderPresentList != null && orderPresentList.size() > 0) {
				iter = orderPresentList.listIterator();
				while(iter.hasNext()){
					voOrderProduct vop = (voOrderProduct)iter.next();
					vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
				}
				stockStatus = checkStock(orderPresentList); //检查订单内赠品的库存情况
			}
			
			// 如果是广速待出货， 想从广分发货，而广分又缺货的时候，提供一个连接，自动生成调拨单
			if(bean.getStockArea() == ProductStockBean.AREA_GS && bean.getStatus() == OrderStockBean.STATUS2 && (stockStatus == 4 || stockStatus == 6)){
				request.setAttribute("gfExchange", "gfExchange");
			}

			orderProductList.addAll(orderPresentList);

			Iterator detailIter = orderProductList.listIterator();
			while(detailIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)detailIter.next();
				voProduct product = adminService.getProduct(vop.getProductId());
				if(product.getIsPackage() == 1){ // 如果这个产品是套装
					List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voOrderProduct tempVOP = new voOrderProduct();
						tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
						tempVOP.setProductId(ppBean.getProductId());
						tempVOP.setCode(tempProduct.getCode());
						tempVOP.setName(tempProduct.getName());
						tempVOP.setPrice(tempProduct.getPrice());
						tempVOP.setOriname(tempProduct.getOriname());
						order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + tempProduct.getBzzhongliang());
					}
				} else {
					order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + product.getBzzhongliang());
				}
			}

			/*
			//########临时处理，如果大于等于50就全部从北京发，如果小于50， 根据物流对比结果来选择发货地点
			int gdCount = 0;
			gdCount = service.getOrderStockCount("type=0 and area=1 and status in (1,2) and left(last_oper_time,10)='" + DateUtil.getNow().substring(0,10) + "'");
			request.setAttribute("stockoutCountGD", String.valueOf(gdCount));
			//########两周后去掉
			 * 
			 */

			request.setAttribute("bean", bean);
			request.setAttribute("outList", outList);
			request.setAttribute("stockStatus", "" + stockStatus);
			request.setAttribute("flag",flag);
		} finally {
			//adminService.close();
			service.releaseAll();
		}
	}
	/**
	 * @return 复核出库功能批量验证商品imei码方法
	 * @author syuf
	 */
	public void batchCheckIMEI(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html; charset=utf-8");
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService service = new WareService(dbOp);
		IMEIService iService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		String imeiCodes = request.getParameter("imeiCodes");
		try {
			voProduct product = null;
			if(!"".equals(StringUtil.convertNull(imeiCodes))){
				Set<String> set = new HashSet<String>();
				String[] codes = imeiCodes.split("\n");
				if(codes != null && codes.length > 0){
					for(String code : codes){
						set.add(StringUtil.convertNull(code).trim());
					}
				}
				if(set.size() > 0){
					Map<String,String> map = new HashMap<String, String>();
					for(String code : set){
						String result = iService.batchCheckIMEI(code);
						if(result.startsWith("success_")){
							product = service.getProduct(StringUtil.toInt(result.split("_")[1]));
							map.put(result.split("_")[2],product.getCode());
						} else {
							j.setMsg(result);
							JSONObject json = JSONObject.fromObject(j);
							response.getWriter().write(json.toString());
							return;
						}
					}
					j.setSuccess(true);
					j.setObj(map);
				}
			} else {
				j.setMsg("未接收到数据!");
			}
			JSONObject json = JSONObject.fromObject(j);
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	/**
	 * @return 复核出库功能验证商品imei码方法
	 * @author syuf
	 */
	public void checkIMEI(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html; charset=utf-8");
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService service = new WareService(dbOp);
		IMEIService iService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		String code = request.getParameter("code");
		try {
			voProduct product = null;
			if(!"".equals(StringUtil.convertNull(code))){
				String result = iService.checkIMEI(code);
				Map<String ,String> map = new HashMap<String, String>();
				if(result.startsWith("success_")){
					product = service.getProduct(StringUtil.toInt(result.split("_")[1]));
					map.put("productCode", product.getCode());
					map.put("imeiId", result.split("_")[2]);
					j.setSuccess(true);
					j.setObj(map);
				} else if(result.startsWith("product_")){
					product = service.getProduct(StringUtil.toInt(result.split("_")[1]));
					map.put("productCode", product.getCode());
					map.put("imeiId", "");
					j.setSuccess(true);
					j.setObj(map);
				} else {
					j.setMsg(result);
				}
			} else {
				j.setMsg("未接收到数据!");
			}
			JSONObject json = JSONObject.fromObject(j);
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	public String queryPackageCode(DbOperation dbop ,int deliverId,int deliverType){
		String packageCode = null;
		ResultSet rs = null;
		try {
			//rs = dbop.executeQuery("select package_code from deliver_package_code where deliver="+deliverId+" and used=0 limit 1");
			
			  rs = dbop.executeQuery("select package_code from deliver_package_code where deliver="+deliverId+" and used=0 and deliver_type="+deliverType+" limit 1");
			
			
			if(rs.next()){
				packageCode = rs.getString("package_code");
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return packageCode;
	}
	/**
	 * 用于查询deliver_relation表中运单号
	* @Description: 
	* @author ahc
	 */
	public String queryPackageCodeForRelation(DbOperation dbop ,int deliverId,String orderCode){
		String packageCode = null;
		ResultSet rs = null;
		try {
			rs = dbop.executeQuery("select package_code from deliver_relation where deliver_id="+deliverId+" and status in(1,3) and order_code='"+orderCode+"'");
			if(rs.next()){
				packageCode = rs.getString("package_code");
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return packageCode;
	}
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-9
	 * 
	 * 说明：完成订单出货<br/>
	 * 现在改成了 复核通过，程序逻辑与 完成订单出货 一样
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void completeOrderStock(HttpServletRequest request, HttpServletResponse response) {
		 
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "尚未登录，操作失败");
			request.setAttribute("result", "failure");
			return;
		}
		HttpSession session = request.getSession();
		
		synchronized(session.getId()){
//			long time1=System.currentTimeMillis();///////////////////////////////////////////////////////////////////////////////////////
			int operId = StringUtil.StringToId(request.getParameter("operId"));
			String imeiIds = request.getParameter("imeiId");
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			//IAfterSalesService afService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
			BalanceService balanceService = new BalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SecondSortingSplitService secondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			DbOperation db = service.getDbOp();
			DbOperation redb=new DbOperation();
			redb.init(DbOperation.DB_SLAVE);
			IStockService reservice = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, redb);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			ResultSet rs0 = null;

			String key = operId + "stockout";
			try {
//				long time2=System.currentTimeMillis();System.out.println("time2-time1:"+(time2-time1)+" 开始-开始事务");/////////////////////////////////////
				OrderStockBean bean = service.getOrderStock("id = " + operId);
				if(!CargoDeptAreaService. hasCargoDeptArea(request, bean.getStockArea(), bean.getStockType())){
    				request.setAttribute("tip", "只能扫描‘用户所属的库地区’的订单");
    				request.setAttribute("result", "failure");
					return;
    			}
				if (bean.getStatus() == OrderStockBean.STATUS3) {
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}
//				if(request.getParameter("action")!=null&&request.getParameter("action").equals("completeOrderStock2")
//						&&bean.getDeliver()==11){
//					request.setAttribute("tip", "EMS省内订单不能在此复核！");
//					request.setAttribute("result", "failure");
//					return;
//				} 

				//开始事务
				service.getDbOp().startTransaction();
				
				String act = StringUtil.dealParam(request.getParameter("act"));

				Iterator itr = null;
				OrderStockProductBean sh = null;
				//修改出货地点
				if ("change".equals(act)) {
				}
				//申请出货
				else if ("stockout".equals(act)) {
				}
				//确认已出货
				else if ("confirm".equals(act)) {
					//List logList = new ArrayList();	//存放StockBatchLogBean（财务报表用）
					if (bean.getStatus() != OrderStockBean.STATUS6) {
						request.setAttribute("tip", "该操作的状态不是复核，不能确认出货！");
						request.setAttribute("result", "failure");
						service.releaseAll();
						return;
					}

					//log记录
					voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
					if (admin == null) {
						request.setAttribute("tip", "当前没有登录，添加失败！");
						request.setAttribute("result", "failure");
						return;
					}

					StockAdminHistoryBean log = new StockAdminHistoryBean();
					log.setAdminId(admin.getId());
					log.setAdminName(admin.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.ORDER_STOCK_STATUS3);
					log.setType(StockAdminHistoryBean.CHANGE);

					//添加扫描日志
					String scanProductBatchCode = StringUtil.convertNull(request.getParameter("scanProductBatcodeName"));
					//判断是否扫描进来
					if(scanProductBatchCode.length()>0){
						String tmp = null;
						BufferedReader reader = new BufferedReader(new StringReader(scanProductBatchCode));
						StringBuffer scanLog = new StringBuffer();
						scanLog.append("扫描复核出库：");
						log.setOperDatetime(DateUtil.getNow());
						int lineNum=0;//行数
						while((tmp=reader.readLine())!=null){
							lineNum++;
							String[] s = tmp .split(",");
							if(tmp.equals("reset")){
								scanLog.append("重新扫描：");
							}else{	
								if(s.length>2)
									scanLog.append("扫描").append(s[0]).append("(").append(s[1]).append(")，").append("手动修改(").append(s[2]).append(")</br>");
								else
									scanLog.append("扫描").append(s[0]).append("(").append(s[1]).append(")").append("</br>");
							}
							if(lineNum%2==0){//每5行添加一次日志
								log.setOperDatetime(DateUtil.getNow());
								log.setRemark(scanLog.toString());
								if(!service.addStockAdminHistory(log)){
									request.setAttribute("tip", "订单出库日志添加操作失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								scanLog = new StringBuffer();
							}
						}
						if(!scanLog.toString().equals("")){
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark(scanLog.toString());
							if(!service.addStockAdminHistory(log)){
								request.setAttribute("tip", "订单出库日志添加操作失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
					}
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("完成订单出货操作：" + bean.getName());
					if(!service.addStockAdminHistory(log)){
						request.setAttribute("tip", "订单出库日志添加操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
//					long time3=System.currentTimeMillis();System.out.println("time3-time2:"+(time3-time2)+" 扫描日志，订单日志");//////////////////////////////////
					String condition = "order_stock_id = " + bean.getId();
					ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");

					if(!service.updateOrderStock("status = " + OrderStockBean.STATUS3 + ", last_oper_time = now()", "id = " + bean.getId())){
						request.setAttribute("tip", "订单出库状态更新操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					//如果该订单是货到付款分成推广订单，改为“预计可以分成”
					//service.getDbOp().executeUpdate(
					//                "update user_order set cpa_status=1 where status=3 and code='"
					//                        + bean.getOrderCode()
					//                        + "' and fr > 9100000 and fr < 9999999 and cpa_status = 0 and buy_mode = 0");

					voOrder order = adminService.getOrder("code='" + bean.getOrderCode() + "'");
					if(order.getStatus() == 3){ //如果订单是代发货状态，才自动修改订单的状态为已发货或已结算，并添加订单状态修改日志
						int oriStatus = order.getStatus();
						int status = 6;
						switch(order.getBuyMode()){
						case 0:
							status = 6;
							break;
						case 1:
						case 2:
							//邮购"已到款"和上门自取"已发货"订单做了出货时,订单状态自动修改成"已妥投",以前是"已结算"
							//2014-12-01  14改为6
							status = 6;
							break;
						default:
							status = 6;
						}
						// 如果该订单状态是“待发货”（邮购是“已到款”），则将该订单状态改为“已发货”
						// 非邮购订单变为 已发货
						if(!service.getDbOp().executeUpdate("update user_order set status=" + status + " where status=3 and code='" + bean.getOrderCode() + "'")){
							request.setAttribute("tip", "订单状态更新操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						order.setStatus(status);
						// 邮购订单变为 已结算
						//service.getDbOp().executeUpdate("update user_order set status=12 where buy_mode in (1,2) and status=3 and code='" + bean.getOrderCode() + "'");

						//订单发货状态 改为 发货成功
						if(!service.getDbOp().executeUpdate("update user_order set stockout_deal=" + OrderStockStatusBean.STATUS_STOCKOUT_SUCCESS + " where code='" + bean.getOrderCode() + "'")){
							request.setAttribute("tip", "订单发货状态更新操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						order.setStockoutDeal(OrderStockStatusBean.STATUS_STOCKOUT_SUCCESS);

						StringBuilder logContent = new StringBuilder();
						// 如果修改了订单的状态，就记录操作日志
						logContent.append("[订单状态:");
						logContent.append(oriStatus);
						logContent.append("->");
						logContent.append(status);
						logContent.append("]");
						if(logContent.length() > 0){
							IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
							try{
								OrderAdminLogBean oalog = new OrderAdminLogBean();
								oalog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
								oalog.setUserId(admin.getId());
								oalog.setUsername(admin.getUsername());
								oalog.setOrderId(order.getId());
								oalog.setOrderCode(order.getCode());
								oalog.setCreateDatetime(DateUtil.getNow());
								oalog.setContent(logContent.toString());
								adminService.addOrderAdminStatusLog(logService, oriStatus, status, -1, -1, oalog);
								if(!logService.addOrderAdminLog(oalog)){
									request.setAttribute("tip", "订单日志添加操作失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							} catch(Exception e) {
								e.printStackTrace();
							} finally {
								logService.releaseAll();
							}
						}
					}
//					long time4=System.currentTimeMillis();System.out.println("time4-time3:"+(time4-time3)+" 出库单状态，订单状态，订单日志");//////////////////////////////////
					//订单批次波次状态修改
					SortingBatchOrderBean sbo=siService.getSortingBatchOrderInfo("delete_status<>1 and order_id="+order.getId());
					SortingBatchGroupBean sbg=null;//订单所属分拣波次
					if(sbo!=null){//该订单已经被添加进批次，修改分拣批次信息
						if(sbo.getStatus()!=SortingBatchOrderBean.STATUS2){
							request.setAttribute("tip", "已删除订单，需要重新分拣！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						
						sbg=siService.getSortingBatchGroupInfo("id="+sbo.getSortingGroupId());
						if(sbg!=null&&sbg.getSortingType()==1&&sbg.getSortingStatus()!=2){//波次分拣类型为PDA分拣且分拣状态不是分拣完成
							request.setAttribute("tip", "订单所属波次是PDA分拣且未分拣完成！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						
						if(!siService.updateSortingBatchOrderInfo("status=3", "order_id="+order.getId())){
							request.setAttribute("tip", "对波次中的订单"+order.getCode()+"状态更新操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						//在这里加 新的判断 订单是不是通过 pda 分拣的 如果是  要对它的分拣量和 完成量 进行限制 完成量小于分拣量的 不允许复合
		        		if( sbg.getSortingType() == 1 ) {
		        			String sql  = "count > sorting_count and sorting_batch_order_id =" + sbo.getId(); 
		        			int count = secondSortingSplitService.getSortingBatchOrderProductCount(sql);
		        			if( count > 0 ) {
		        				request.setAttribute("tip", "订单是pda分拣，且其商品存在已分拣数量不足，不能复合！");
		        				request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
		        				return;
		        			}
		        		}
						
						//分拣中订单数量
						if (sbo.getSortingGroupId() != 0) {
							int sortingOrderCount = siService.getSortingBatchOrderCount("delete_status<>1 and status<>3 and sorting_group_id=" + sbo.getSortingGroupId());// 计算该波次中未分拣完成的订单数量
							if (sortingOrderCount == 0) {
								if (!siService.updateSortingBatchGroupInfo("status=2,complete_datetime='" + DateUtil.getNow() + "'", "id=" + sbo.getSortingGroupId())) {
									request.setAttribute("tip", "对波次" + sbo.getSortingGroupCode() + "状态更新操作失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								int groupCount = siService.getSortingBatchGroupCount("status<>2 and status<>3 and sorting_batch_id=" + sbo.getSortingBatchId());// 计算该批次中未分拣完成的波次数量
								if (groupCount == 0) {
									if (!siService.updateSortingBatchInfo("status=4,complete_datetime='" + DateUtil.getNow() + "'", "id=" + sbo.getSortingBatchId())) {
										request.setAttribute("tip", "对批次" + sbo.getSortingBatchCode() + "状态更新操作失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
								}
							}
						} else {
							int sortingOrderCount = siService.getSortingBatchOrderCount("delete_status<>1 and status<>3 and sorting_batch_id=" + sbo.getSortingBatchId());// 计算该波次中未分拣完成的订单数量
							if (sortingOrderCount == 0) {
								if (!siService.updateSortingBatchInfo("status=4,complete_datetime='" + DateUtil.getNow() + "'", "id=" + sbo.getSortingBatchId())) {
									request.setAttribute("tip", "对批次" + sbo.getSortingBatchCode() + "状态更新操作失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
						}
					}
//					long time5=System.currentTimeMillis();System.out.println("time5-time4:"+(time5-time4)+" 分拣波次相关");/////////////////////////////////
					//查询作业货位
					CargoInfoBean operCargo=cargoService.getCargoInfo("area_id="+bean.getStockArea()+" and stock_type=0 and store_type="+CargoInfoBean.STORE_TYPE5);
					if(sbg!=null&&sbg.getSortingType()==1&&operCargo==null){//波次是PDA分拣且没有设置对应地区的作业货位
						request.setAttribute("tip", "没有设置该地区作业货位！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					
					voProduct product = null;
					String set = null;
					List<BaseProductInfo> baseProductInfoList = new ArrayList<BaseProductInfo>();
					itr = shList.iterator();
					while (itr.hasNext()) {
						sh = (OrderStockProductBean) itr.next();
						//出库
						product = adminService.getProduct(sh.getProductId(), service.getDbOp());
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
						ProductStockBean ps = psService.getProductStock("id=" + sh.getStockoutId());

						set = "status = " + OrderStockProductBean.DEALED
						+ ", remark = concat(remark, '(操作前锁定库存" + ps.getLockCount()
						+ ",操作后锁定库存" + (ps.getLockCount() - sh.getStockoutCount())
						+ ")'), deal_datetime = now()";
						if(!service.updateOrderStockProduct(set, "id = " + sh.getId())){
							request.setAttribute("tip", "数据库操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						//psService.updateProductStock("lock_count=(lock_count - " + sh.getStockoutCount() + ")", "id=" + sh.getStockoutId());
						if(!psService.updateProductLockCount(sh.getStockoutId(), -sh.getStockoutCount())){
							request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}

						//更新货位库存记录
						product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
						List ospcList = service.getOrderStockProductCargoList("order_stock_product_id = "+sh.getId(), -1, -1, "id asc");
						if(ospcList == null||ospcList.size() == 0){
							request.setAttribute("tip", "订单出库无相应货位信息，请与管理员联系！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						Iterator ospcIter = ospcList.listIterator();
						while(ospcIter.hasNext()){
							OrderStockProductCargoBean ospc = (OrderStockProductCargoBean)ospcIter.next();
							CargoProductStockBean cps = null;
							if(sbg!=null&&sbg.getSortingType()==1){//PDA分拣
								cps=cargoService.getCargoProductStock("product_id="+product.getId()+" and cargo_id="+operCargo.getId());
							}else{//手工分拣
								cps = cargoService.getCargoProductStock("id = "+ospc.getCargoProductStockId());
							}
							if(!cargoService.updateCargoProductStockLockCount(cps.getId(), -ospc.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}

							//货位出库卡片
							cps = cargoService.getCargoAndProductStock("cps.id = "+cps.getId());
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_ORDERSTOCK);
							csc.setCode(bean.getOrderCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(bean.getStockType());
							csc.setStockArea(bean.getStockArea());
							csc.setProductId(sh.getProductId());
							csc.setStockId(cps.getId());
							csc.setStockOutCount(ospc.getCount());
							csc.setStockOutPriceSum((new BigDecimal(ospc.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(bean.getStockArea(), bean.getStockType()) + product.getLockCount(bean.getStockArea(), bean.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
							csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
							csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							if(!cargoService.addCargoStockCard(csc)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}

						/*
						//更新批次记录、添加订单出货批次记录
						//这里修改了排序 使Q开头的批次（ 也就是因报溢产生的批次 ）放在前面
						List sbList = service.getStockBatchListWithSomeoneComeFirst("product_id="+sh.getProductId()+" and stock_type="+sh.getStockType()+" and stock_area="+sh.getStockArea(), -1, -1, "FIELD(codeType,'Q') Desc, create_datetime asc");
						float stockOutPrice = 0;
						if(sbList!=null&&sbList.size()!=0){
							int batchCount = sh.getStockoutCount();
							int index = 0;
							int stockOutCount = 0;
							do {
								StockBatchBean batch = (StockBatchBean)sbList.get(index);
								int ticket = batch.getTicket();	//是否含票 
								int _count = FinanceProductBean.queryCountIfTicket(db, sh.getProductId(), ticket);	//出库前库存总量
								if(batchCount>=batch.getBatchCount()){
									if(!service.deleteStockBatch("id="+batch.getId())){
										request.setAttribute("tip", "批次量更新操作失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
									stockOutCount = batch.getBatchCount();
								}else{
									if(!service.updateStockBatch("batch_count = batch_count-"+batchCount, "id="+batch.getId())){
										request.setAttribute("tip", "批次量更新操作失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
									stockOutCount = batchCount;
								}
								batchCount -= batch.getBatchCount();
								index++;

								//添加批次操作记录
								StockBatchLogBean batchLog = new StockBatchLogBean();
								batchLog.setCode(bean.getOrderCode());
								batchLog.setStockType(batch.getStockType());
								batchLog.setStockArea(batch.getStockArea());
								batchLog.setBatchCode(batch.getCode());
								batchLog.setBatchCount(stockOutCount);
								batchLog.setBatchPrice(batch.getPrice());
								batchLog.setProductId(batch.getProductId());
								batchLog.setRemark("订单出货");
								batchLog.setCreateDatetime(DateUtil.getNow());
								batchLog.setUserId(admin.getId());
								if(!service.addStockBatchLog(batchLog)){
									request.setAttribute("tip", "添加失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								logList.add(batchLog);
								
								//财务产品信息表---liuruilan-----
								FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh.getProductId());
								if(fProduct == null){
									request.setAttribute("tip", "查询异常，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								float price5 = product.getPrice5();
		    					int totalCount = product.getStockAll() + product.getLockCountAll();
								float priceSum = Arith.mul(price5, totalCount);
								float priceHasticket = fProduct.getPriceHasticket();
								float priceNoticket = fProduct.getPriceNoticket();
								float priceSumHasticket = 0;
								float priceSumNoticket = 0;
								set = "price_sum =" + priceSum;
								if(ticket == 0){	//0-有票
									//计算公式：(结存总额 + (发货时批次价 * 本批数量)) / (库存总数量 + 本批数量)
									priceSumHasticket = Arith.mul(priceHasticket,  _count - stockOutCount);
									set += ", price_sum_hasticket =" + priceSumHasticket;
								}
								if(ticket == 1){	//1-无票
									priceSumNoticket = Arith.mul(priceNoticket,  _count - stockOutCount);
									set += ", price_sum_noticket =" + priceSumNoticket;
								}
								if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())){
									request.setAttribute("tip", "数据库操作异常，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								
								//财务进销存卡片
								product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
								int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), bean.getStockArea(), bean.getStockType(), ticket, sh.getProductId());
								int stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, bean.getStockType(), ticket, sh.getProductId());
								int stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), bean.getStockArea(), -1, ticket, sh.getProductId());
								FinanceStockCardBean fsc = new FinanceStockCardBean();
								fsc.setCardType(StockCardBean.CARDTYPE_ORDERSTOCK);
								fsc.setCode(bean.getOrderCode());
								fsc.setCreateDatetime(DateUtil.getNow());
								fsc.setStockType(bean.getStockType());
								fsc.setStockArea(bean.getStockArea());
								fsc.setProductId(sh.getProductId());
								fsc.setStockId(sh.getStockoutId());
								fsc.setStockInCount(stockOutCount);
								fsc.setStockAllArea(stockAllArea);
								fsc.setStockAllType(stockAllType);
								fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
								fsc.setStockPrice(product.getPrice5());
								
								fsc.setCurrentStock(currentStock);
								fsc.setType(fsc.getCardType());
								fsc.setIsTicket(ticket);
								fsc.setStockBatchCode(batch.getCode());
								fsc.setBalanceModeStockCount(_count - stockOutCount);
								if(ticket == 0){
									fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockOutCount))));
									fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
								}
								if(ticket == 1){
									fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockOutCount))));
									fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
								}
								double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
								fsc.setAllStockPriceSum(tmpPrice);
								if(!frfService.addFinanceStockCardBean(fsc)){
									request.setAttribute("tip", "数据库操作异常，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								//---------------liuruilan-----------

								stockOutPrice = stockOutPrice + batch.getPrice() * stockOutCount;
							} while (batchCount>0&&index<sbList.size());

						}
						*/
							
						//更新库存价格
						//						int totalCount = product.getStock(ProductStockBean.AREA_BJ) + product.getStock(ProductStockBean.AREA_GF) + product.getStock(ProductStockBean.AREA_GS) + product.getLockCount(ProductStockBean.AREA_BJ) + product.getLockCount(ProductStockBean.AREA_GF) + product.getLockCount(ProductStockBean.AREA_GS);
						//						float price5 = ((float)Math.round((product.getPrice5() * totalCount - stockOutPrice) / (totalCount-sh.getStockoutCount()) * 1000))/1000;
						//						if(totalCount-sh.getStockoutCount() == 0){
						//							price5 = 0;
						//						}
						//						service.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId());

						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));

						// 出库卡片
						StockCardBean sc = new StockCardBean();
						sc.setCardType(StockCardBean.CARDTYPE_ORDERSTOCK);
						sc.setCode(bean.getOrderCode());
						sc.setCreateDatetime(DateUtil.getNow());
						sc.setStockType(bean.getStockType());
						sc.setStockArea(bean.getStockArea());
						sc.setProductId(sh.getProductId());
						sc.setStockId(sh.getStockoutId());
						sc.setStockOutCount(sh.getStockoutCount());
						//						sc.setStockOutPriceSum(stockOutPrice);
						sc.setStockOutPriceSum((new BigDecimal(sh.getStockoutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
						sc.setStockAllArea(product.getStock(bean.getStockArea()) + product.getLockCount(bean.getStockArea()));
						sc.setStockAllType(product.getStockAllType(bean.getStockType()) + product.getLockCountAllType(bean.getStockType()));
						sc.setAllStock(product.getStockAll() + product.getLockCountAll());
						sc.setStockPrice(product.getPrice5());
						sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!psService.addStockCard(sc)){
							request.setAttribute("tip", "添加失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						
						BaseProductInfo baseProductInfo = new BaseProductInfo();
						baseProductInfo.setId(sh.getProductId());
						baseProductInfo.setOutCount(sh.getStockoutCount());
						baseProductInfo.setOutPrice(product.getPrice5());
						baseProductInfo.setProductStockId(sh.getStockoutId());
						baseProductInfoList.add(baseProductInfo);
					}
					
					//根据业务类型采集财务基础数据
					FinanceSaleBaseDataService financeBaseDataService = 
							FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(
									FinanceStockCardBean.CARDTYPE_ORDERSTOCK, db.getConn());
					financeBaseDataService.acquireFinanceSaleBaseData(order.getCode(), user.getId(), "", 
							DateUtil.getNow(), bean.getStockType(), bean.getStockArea(), baseProductInfoList);
					
					
					
//					long time6=System.currentTimeMillis();System.out.println("time6-time5:"+(time6-time5)+" 库存，货位库存，进销存");//////////////////////////////////
					int deliver = order.getDeliver();
					/**
					int balanceType = 0;
					// 根据 快递公司，确定结算来源
					if(voOrder.deliverToBalanceTypeMap.containsKey(deliver+"")){
						balanceType=Integer.parseInt(voOrder.deliverToBalanceTypeMap.get(deliver+"").toString());
					}
					//添加一个空的订单结算信息， 以后导入结算信息的时候，直接在上面修改
					MailingBalanceBean mb = new MailingBalanceBean();
					mb.setOrderId(bean.getOrderId());
					mb.setOrderCode(bean.getOrderCode());
					mb.setPrice(order.getDprice());
					mb.setBalanceStatus(MailingBalanceBean.BALANCE_STATUS_UNDEFINE);
					mb.setOrderCreateDatetime(DateUtil.formatDate(order.getCreateDatetime()));
					mb.setStockoutDatetime(DateUtil.getNow());
					mb.setName(order.getName());
					mb.setBalanceCheck(MailingBalanceBean.BALANCE_NOIMPORT);
					mb.setPackagenum("");
					mb.setStockoutStatus(OrderStockBean.STATUS3);
					mb.setBuyMode(order.getBuyMode());
					mb.setBalanceType(balanceType);
					mb.setBalanceArea(-1);
					if(!baService.addMailingBalance(mb)){
						request.setAttribute("tip", "添加失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					
					// 异常结算数据表里添加一条空记录
					FinanceMailingBalanceBean fmb = new FinanceMailingBalanceBean();
					fmb.setOrderId(order.getId());
					fmb.setOrderCode(bean.getOrderCode());
					fmb.setOrderPrice(order.getDprice());
					fmb.setBalanceStatus(MailingBalanceBean.BALANCE_STATUS_UNDEFINE);
					fmb.setStockoutDatetime(DateUtil.getNow());
					fmb.setBalanceCheck(MailingBalanceBean.BALANCE_NOIMPORT);
					fmb.setPackageNum("");
					fmb.setBuyMode(order.getBuyMode());
					fmb.setBalanceType(balanceType);
					fmb.setBalanceArea(-1);
					fmb.setDataType(0);			// 0-正常
					fmb.setCreateDateTime(DateUtil.getNow());
					if(!balanceService.addFinanceMailingBalanceBean(fmb)){
						request.setAttribute("tip", "添加失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					*/
					
					//修改售后单状态
					if(order.getBuyMode() == Constants.BUY_TYPE_NIFFER || order.getCode().startsWith("S")){
						if(!OrderRefundService.modifyAfterSaleOrderStatus(order, admin,service.getDbOp())){
							request.setAttribute("tip", "售后换货单退款到钱包或者更新售后退款单状态操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
//					//修改售后处理单状态
//					List<AfterSaleWarehourceProductRecordsBean> list = afStockService.getAfterSaleWarehourceProductRecordsAndOperationList("asol.order_id=" + order.getId(),-1,-1,null);
//					if(list != null && list.size() > 0){
//						for(AfterSaleWarehourceProductRecordsBean b : list){
//							AfterSaleDetectProductBean detect = afStockService.getAfterSaleDetectProduct("id=" + b.getId());
//							if(detect != null){
//								if(!afStockService.updateAfterSaleDetectProduct("status=" + AfterSaleDetectProductBean.STATUS18, "id=" + detect.getId())){
//									request.setAttribute("tip", "修改处理单[" + detect.getCode() + "]状态失败!");
//									request.setAttribute("result", "failure");
//									service.getDbOp().rollbackTransaction();
//									return;
//								}
//								if(!afStockService.updateAfterSaleWarehourceProductRecords("status=7","id=" + detect.getId())){
//									request.setAttribute("tip", "修改销售后台售后处理单[" + detect.getCode() + "]状态失败!");
//									request.setAttribute("result", "failure");
//									service.getDbOp().rollbackTransaction();
//									return;
//								}
//							}
//						}
//					}
					//修改售后明细单状态
					AfterSaleOperationListBean listBean = afStockService.getAfterSaleOperationList("order_id=" + order.getId());
					if(listBean != null){
						if(!afStockService.updateAfterSaleOperationList("state=5","id=" + listBean.getId())){
							request.setAttribute("tip", "修改售后明细单[" + listBean.getCODE() + "]状态失败!");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					//修改核对包裹记录
					if(!service.updateAuditPackage("check_datetime='"+DateUtil.getNow()+"',check_user_name='"+user.getUsername()+"',status="+AuditPackageBean.STATUS3, "order_id="+order.getId())){
						request.setAttribute("tip", "核对包裹记录更新操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
//					long time7=System.currentTimeMillis();System.out.println("time7-time6:"+(time7-time6)+" 添加结算信息，查询四级地址");//////////////////////////////////
					synchronized (packagenumLock) {
					//处理宅急送或贵州邮政的包裹单号(新复核流程)
					if(order.getDeliver()==10||order.getDeliver()==26||order.getDeliver()==55||order.getDeliver()==27||order.getDeliver()==40||order.getDeliver()==45||order.getDeliver()==50){//新复核流程下的宅急送订单
						DbOperation tempDbop=new DbOperation();
						tempDbop.init("adult");
						int packageId=0;
						String packageCode=null;
						int deliverId=0;
						if(order.getDeliver()==10||order.getDeliver()==26||order.getDeliver()==55||order.getDeliver()==27||order.getDeliver()==45||order.getDeliver()==50){
							deliverId=10;
						}else if(order.getDeliver()==40){
							deliverId=40;
						}
						ResultSet tempRs=tempDbop.executeQuery("select id,package_code from deliver_package_code where deliver="+deliverId+" and used=0 limit 1");
						if(tempRs.next()){
							packageId=tempRs.getInt("id");
							packageCode=tempRs.getString("package_code");
						}
						tempRs.close();
						if(packageCode==null){
							request.setAttribute("tip", voOrder.deliverMapAll.get(order.getDeliver()+"")+"已经没有可用的包裹单！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							tempDbop.release();
							return;
						}
						//临时去掉提示
//						if(order.getDeliver()==35){//宅急送暂时去掉提示
//							ResultSet tempRs2=tempDbop.executeQuery("select count(id) from deliver_package_code where deliver="+order.getDeliver()+" and used=0");
//							if(tempRs2.next()){//剩余包裹单小于5000时提示
//								int count=tempRs2.getInt("count(id)");
//								if(count<5000){
//									request.setAttribute("tip", voOrder.deliverMapAll.get(order.getDeliver()+"")+"剩余包裹单已小于5000！");
//								}
//							}
//							tempRs2.close();
//						}
						
						tempDbop.executeUpdate("update deliver_package_code set used=1 where id="+packageId);
						tempDbop.release();
						
						OrderPackageAction orderPackage=new OrderPackageAction();
						int checkStatus=orderPackage.importPackage(order,packageCode,user,request,db,true);
						if(checkStatus==4){
							request.setAttribute("tip", "快递公司错误！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}else if(checkStatus==5){
							request.setAttribute("tip", "没有设置结算周期！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} else if (checkStatus == 6) {
							request.setAttribute("tip", "数据异常！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}else if(order.getDeliver()==12){//新复核流程下的顺丰订单
						DbOperation tempDbop=new DbOperation();
						tempDbop.init("adult_slave");
						//int packageId=0;
						String packageCode=null;
						ResultSet tempRs=tempDbop.executeQuery("select package_code from deliver_package_code2 where deliver=12 and order_id="+order.getId()+" limit 1");
						if(tempRs.next()){
							//packageId=tempRs.getInt("id");
							packageCode=tempRs.getString("package_code");
						}
						tempRs.close();
						if(packageCode==null){
							request.setAttribute("tip", "顺丰包裹单号错误！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							tempDbop.release();
							return;
						}
						tempDbop.release();
						OrderPackageAction orderPackage=new OrderPackageAction();
						int checkStatus=orderPackage.importPackage(order,packageCode,user,request,db,true);
						if(checkStatus==4){
							request.setAttribute("tip", "快递公司错误！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}else if(checkStatus==5){
							request.setAttribute("tip", "没有设置结算周期！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} else if (checkStatus == 6) {
							request.setAttribute("tip", "数据异常！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}else if(deliver==9||deliver==25||deliver==28||deliver==29||deliver==31||deliver==32||deliver==34||deliver==35||deliver==37||deliver==43||deliver==44||order.getDeliver()==51||deliver==52||deliver==54 || deliver==DeliverCorpInfoBean.DELIVER_ID_YD || deliver==DeliverCorpInfoBean.DELIVER_ID_YT_WX ||deliver==DeliverCorpInfoBean.DELIVER_ID_YT_CD || deliver==DeliverCorpInfoBean.DELIVER_ID_JD_CD || deliver==DeliverCorpInfoBean.DELIVER_ID_JD_WX || deliver==DeliverCorpInfoBean.DELIVER_ID_RFD_SD){//新复核流程EMS省外订单
						String packageCode=null;
						DbOperation tempDbop=new DbOperation();
						tempDbop.init("adult");
						//广速省外与江西邮政均取广速省外的包裹单号
						int deliverId = 0;
						ResultSet tempRs = null;
						//packageCode = queryPackageCode(tempDbop, deliver);
						if(order.getBuyMode() == 0){ //货到付款 先用自己的ID查
							
							if(deliver == 44){
								packageCode = queryPackageCode(tempDbop, deliver,0);
							}
							else{
								packageCode = queryPackageCode(tempDbop, deliver,1);
							}
							
							if(packageCode == null || "".equals(packageCode)){ //查不到 用9查
								if(deliver != 44 && deliver !=DeliverCorpInfoBean.DELIVER_ID_YT_WX && deliver !=DeliverCorpInfoBean.DELIVER_ID_YT_CD && deliver!=DeliverCorpInfoBean.DELIVER_ID_YD && deliver!=DeliverCorpInfoBean.DELIVER_ID_JD_CD && deliver!=DeliverCorpInfoBean.DELIVER_ID_JD_WX ){ //如果是44和”圆通快递“则不需要再查
									deliverId = 9;
									packageCode = queryPackageCode(tempDbop, deliverId,0);
								}
							}
						} else {//在线支付
							
							packageCode = queryPackageCode(tempDbop, deliver,0);//1代收货到付款 0其他
							
							if(packageCode == null ){
								if(deliver != 44 && deliver !=DeliverCorpInfoBean.DELIVER_ID_YT_WX && deliver !=DeliverCorpInfoBean.DELIVER_ID_YT_CD && deliver!=DeliverCorpInfoBean.DELIVER_ID_YD && deliver!=DeliverCorpInfoBean.DELIVER_ID_JD_CD && deliver!=DeliverCorpInfoBean.DELIVER_ID_JD_WX){
									deliverId = 9;
									packageCode = queryPackageCode(tempDbop, deliverId,0);
								}
							}
						}
						/**
						 * “圆通”、“如风达”、“京东” 获取包裹单方法
						 */
						if(deliver==DeliverCorpInfoBean.DELIVER_ID_YD || deliver==DeliverCorpInfoBean.DELIVER_ID_YT_WX || deliver==DeliverCorpInfoBean.DELIVER_ID_YT_CD || deliver==DeliverCorpInfoBean.DELIVER_ID_RFD_SD || deliver==DeliverCorpInfoBean.DELIVER_ID_JD_WX || deliver==DeliverCorpInfoBean.DELIVER_ID_JD_CD){
							packageCode = queryPackageCodeForRelation(tempDbop, deliver, order.getCode());
						}
						
						if(tempRs != null){
							tempRs.close();
						}
						if(packageCode==null){
							request.setAttribute("tip", order.getDeliverName()+"包裹单号数量不足！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							tempDbop.release();
							return;
						}
						/**
						 * “圆通”、“如风达” 不更新deliver_package_code表
						 */
						if(deliver!=DeliverCorpInfoBean.DELIVER_ID_YD && deliver!=DeliverCorpInfoBean.DELIVER_ID_YT_WX && deliver!=DeliverCorpInfoBean.DELIVER_ID_YT_CD && deliver!=DeliverCorpInfoBean.DELIVER_ID_JD_WX && deliver!=DeliverCorpInfoBean.DELIVER_ID_JD_CD &&  deliver!=DeliverCorpInfoBean.DELIVER_ID_RFD_SD){
							//更新包裹单使用状态
							String upPackageStatus ="update deliver_package_code set used=1 where package_code='"+packageCode+"'";
							if(!tempDbop.executeUpdate(upPackageStatus)){
								request.setAttribute("tip", order.getDeliverName()+"包裹单状态更新失败");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								tempDbop.release();
								return;
							}
						}
						
						tempDbop.release();
						
						//临时去掉提示
//						ResultSet packageCountRs= service.getDbOp().executeQuery("select count(id) from deliver_package_code where deliver=9 and used=0");
//						if(packageCountRs.next()){
//							int count = packageCountRs.getInt("count(id)");
//							if(count<5000){
//								request.setAttribute("swWarning", order.getDeliverName()+"包裹单号数量不足5000！");
//							}
//						}
//						packageCountRs.close();
						
						OrderPackageAction orderPackage = new OrderPackageAction();
						int checkStatus = orderPackage.importPackage(order, packageCode, user, request, db,true);
						if (checkStatus == 4) {
							request.setAttribute("tip", "快递公司错误！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} else if (checkStatus == 5) {
							request.setAttribute("tip", "没有设置结算周期！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} else if (checkStatus == 6) {
							request.setAttribute("tip", "数据异常！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						AuditPackageBean apBean = service.getAuditPackage("order_id=" + order.getId());
//						if (StockServiceImpl.emsSwInterface(order, apBean.getPackageCode(),apBean.getWeight(),province, city, county, street, bean.getStockArea(),deliver) == false) {
							String sqlEms = "INSERT INTO ems_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "'" + ")";
							if (!adminService.getDbOperation().executeUpdate(sqlEms)) {
								request.setAttribute("tip", "对订单" + order.getCode() + order.getDeliverName()+"订单信息操作失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
//						}
					}else if(deliver==11){//新复核流程下的EMS省内订单
						DbOperation tempDbop=new DbOperation();
						tempDbop.init("adult");
						String packageCode=null;
						ResultSet tempRs= tempDbop.executeQuery("select package_code from deliver_package_code where deliver=9 and used=0 limit 1");
						if(tempRs.next()){
							packageCode=tempRs.getString("package_code");
						}
						tempRs.close();
						if(packageCode==null){
							request.setAttribute("tip", "EMS省内包裹单号数量不足！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							tempDbop.release();
							return;
						}
						//更新包裹单使用状态
						String upPackageStatus ="update deliver_package_code set used=1 where package_code='"+packageCode+"'";
						if(!tempDbop.executeUpdate(upPackageStatus)){
							request.setAttribute("tip", "EMS包裹单状态更新失败");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							tempDbop.release();
							return;
						}
						tempDbop.release();
						//临时去掉提示
//						ResultSet packageCountRs= service.getDbOp().executeQuery("select count(id) from deliver_package_code where deliver=11 and used=0");
//						if(packageCountRs.next()){
//							int count = packageCountRs.getInt("count(id)");
//							if(count<5000){
//								request.setAttribute("snWarning", "EMS(省内)包裹单号数量不足5000！");
//							}
//						}
//						packageCountRs.close();
						OrderPackageAction orderPackage = new OrderPackageAction();
						int checkStatus = orderPackage.importPackage(order, packageCode, user, request, db,true);
						if (checkStatus == 4) {
							request.setAttribute("tip", "快递公司错误！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} else if (checkStatus == 5) {
							request.setAttribute("tip", "没有设置结算周期！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} else if (checkStatus == 6) {
							request.setAttribute("tip", "数据异常！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
//						StockServiceImpl stock = new StockServiceImpl();
//						AuditPackageBean apBean = service.getAuditPackage("order_id=" + order.getId());
//						
						int orderType = 0;
						if (order.getBuyMode() == 0) {
							orderType = 2;
						} else {
							orderType = 1;
						}
						//if (stock.emsInterface3(order, apBean.getPackageCode(), province, city, county, street, bean.getStockArea(), orderType) == false) {
							String sqlEms = "INSERT INTO ems_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "'" + ")";
							if (!adminService.getDbOperation().executeUpdate(sqlEms)) {
								request.setAttribute("tip", "对订单" + order.getCode() + "EMS订单信息操作失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						//}
						
					}else {//新复核流程下的落地配
						OrderPackageAction orderPackage=new OrderPackageAction();
						int checkStatus=orderPackage.importPackage(order,order.getCode(),user,request,db,true);
						if(checkStatus==4){
							request.setAttribute("tip", "快递公司错误！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}else if(checkStatus==5){
							request.setAttribute("tip", "没有设置结算周期！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} else if (checkStatus == 6) {
							request.setAttribute("tip", "数据异常！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					}
					if(order.getFlat()==3){//判断是否是亚马逊订单
						String sqlAma = "INSERT INTO amazon_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "')";
						if (!adminService.getDbOperation().executeUpdate(sqlAma)) {
							request.setAttribute("tip", "对亚马逊订单" + order.getCode() + order.getDeliverName()+"订单信息操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					if(order.getFlat()==6){//判断是否是当当网订单
						String sqlAma = "INSERT INTO dangdang_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "')";
						if (!adminService.getDbOperation().executeUpdate(sqlAma)) {
							request.setAttribute("tip", "对当当订单" + order.getCode() + order.getDeliverName()+"订单信息操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					if(order.getFlat()==7){//判断是否是京东（鞋）
						String sqlAma = "INSERT INTO jd_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "')";
						if (!adminService.getDbOperation().executeUpdate(sqlAma)) {
							request.setAttribute("tip", "对京东订单" + order.getCode() + order.getDeliverName()+"订单信息操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					
					if(order.isJdAdultOrder()){//判断是否是京东（成人）
						String sqlAma = "INSERT INTO jd_adult_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "')";
						if (!adminService.getDbOperation().executeUpdate(sqlAma)) {
							request.setAttribute("tip", "对京东（成人）订单" + order.getCode() + order.getDeliverName()+"订单信息操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					//主动营销订单（xs开头的）已发货绑定关系延长
					if (order.getCode().startsWith("XS")) {
						String sqlAma = "update customer_manager_relation cmr,customer_manager_user_order_relational cmuor set cmr.extension_date = cmr.extension_date +10,cmr.freeze_start_time=(CASE WHEN cmr.is_cancel=0 THEN DATE_ADD(cmr.freeze_start_time,INTERVAL 10 DAY) ELSE freeze_start_time  END) where cmr.id=cmuor.customer_manager_relation_id and cmuor.order_id=" + order.getId();
						adminService.getDbOperation().executeUpdate(sqlAma);
					}
//					long time8=System.currentTimeMillis();System.out.println("time8-time7:"+(time8-time7)+" 处理包裹单号");//////////////////////////////////
					/*
					//将订单数据写入发货信息表（财务统计用）----2012-11-22--挪至此位置---
					float dprices = 0.0f;
					int fsId = 0;

					FinanceSellBean fsBean = new FinanceSellBean();
					int deliverType = 0;
					if(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()) != null){
						deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()).toString());
					}
					fsBean.setOrderId(order.getId());
					fsBean.setCode(order.getCode());
					fsBean.setPrice(dprices);
					fsBean.setCarriage(order.getPostage());
					fsBean.setCharge(0);
					fsBean.setBuyMode(order.getBuyMode());
					fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
					fsBean.setDeliverType(deliverType);
					fsBean.setCreateDatetime(DateUtil.getNow());
					fsBean.setPackageNum(order.getPackageNum());
					fsBean.setDataType(0);	//0-销售出库
					fsBean.setCount(1);
					rs = service.getDbOp().executeQuery("SELECT add_id1,add_id2,add_id3 FROM user_order_extend_info WHERE order_code = '" + order.getCode() + "'");
					if(rs.next()){
						fsBean.setAddrSub1(rs.getInt("add_id1"));
						fsBean.setAddrSub2(rs.getInt("add_id2"));
						fsBean.setAddrSub3(rs.getInt("add_id3"));
					}	// 订单三级地址
					frfService.addFinanceSellBean(fsBean);
					
					fsId = db.getLastInsertId();
					
					//将订单商品写入销售商品信息表
					//商品
					String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
						+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
						+ "WHERE h.order_id = ? ";
					db.prepareStatement(sql);
					pstmt = db.getPStmt();
					pstmt.setInt(1, order.getId());
					rs = pstmt.executeQuery();
					while(rs.next()){
						int pId = rs.getInt("id");
						int buyCount = rs.getInt("count");
						float price = rs.getFloat("price");
						float dPrice = rs.getFloat("dPrice");
						int parentId1 = rs.getInt("parent_id1");
						int parentId2 = rs.getInt("parent_id2");
						int parentId3 = rs.getInt("parent_id3");
						float price5 = rs.getFloat("price5");
						int productLine = 0;
						
						FinanceProductBean fProduct = refrfService.getFinanceProductBean("product_id =" + pId);
						
						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
						"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
						db.prepareStatement(sql);
						pstmt = db.getPStmt();
						pstmt.setInt(1,pId);
						rs0 = pstmt.executeQuery();
						if(rs0.next()){
							productLine = rs0.getInt("product_line_id");
						}
						for (int i = 0; i < logList.size(); i++) {
							if(buyCount <= 0){
								break;
							}
							int _buyCount = 0;
							StockBatchLogBean batchLog = (StockBatchLogBean) logList.get(i);
							if(batchLog != null && batchLog.getProductId() == pId){
								int batchCount = batchLog.getBatchCount();
								String batchCode = batchLog.getBatchCode();
								StockBatchBean batch = reservice.getStockBatch("code = '" + batchCode + "'");
								if(buyCount > batchCount){
									_buyCount = batchCount;
									batchLog.setBatchCount(0);
									buyCount -= batchCount;
								}else{
									_buyCount = buyCount;
									batchLog.setBatchCount(batchCount - buyCount);
									buyCount = 0;
								}
//								if(batch.getTicket() == 0){
//									price5 = fProduct.getPriceHasticket();
//								}
//								if(batch.getTicket() == 1){
//									price5 = fProduct.getPriceNoticket();
//								}
								FinanceSellProductBean fspBean = new FinanceSellProductBean();
								fspBean.setOrderId(order.getId());
								fspBean.setProductId(pId);
								fspBean.setBuyCount(_buyCount);
								fspBean.setPrice(price);
								fspBean.setDprice(dPrice);
								fspBean.setPrice5(price5);
								fspBean.setProductLine(productLine);	//财务用产品线
								fspBean.setParentId1(parentId1);
								fspBean.setParentId2(parentId2);
								fspBean.setParentId3(parentId3);
								fspBean.setCreateDatetime(DateUtil.getNow());
								fspBean.setDataType(0);	//0-商品销售出库
								
								fspBean.setBalanceMode(batch.getTicket());	//是否含票
								
								int supplierId = FinanceSellProductBean.querySupplier(db, batchCode);
								fspBean.setSupplierId(supplierId);
								fspBean.setFinanceSellId(fsId);
								
								frfService.addFinanceSellProductBean(fspBean);
							}
						}

						if(order.getCode().startsWith("S")){	//售后退货要算实际销售价
							dprices = Arith.add(dprices, Arith.mul(dPrice, rs.getInt("count")));
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
						int buyCount = rs.getInt("count");
						float price = rs.getFloat("price");
						float dPrice = rs.getFloat("dPrice");
						int parentId1 = rs.getInt("parent_id1");
						int parentId2 = rs.getInt("parent_id2");
						int parentId3 = rs.getInt("parent_id3");
						float price5 = rs.getFloat("price5");
						int productLine = 0;
						
						FinanceProductBean fProduct = refrfService.getFinanceProductBean("product_id =" + pId);
						
						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
						"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
						db.prepareStatement(sql);
						pstmt = db.getPStmt();
						pstmt.setInt(1,pId);
						rs0 = pstmt.executeQuery();
						if(rs0.next()){
							productLine = rs0.getInt("product_line_id");
						}
						for (int i = 0; i < logList.size(); i++) {
							if(buyCount <= 0){
								break;
							}
							int _buyCount = 0;
							StockBatchLogBean batchLog = (StockBatchLogBean) logList.get(i);
							if(batchLog != null && batchLog.getProductId() == pId){
								int batchCount = batchLog.getBatchCount();
								if(batchCount == 0){
									continue;
								}
								String batchCode = batchLog.getBatchCode();
								StockBatchBean batch = reservice.getStockBatch("code = '" + batchCode + "'");
								if(buyCount > batchCount){
									_buyCount = batchCount;
									buyCount -= batchCount;
								}else{
									_buyCount = buyCount;
									buyCount = 0;
								}
//								if(batch.getTicket() == 0){
//									price5 = fProduct.getPriceHasticket();
//								}
//								if(batch.getTicket() == 1){
//									price5 = fProduct.getPriceNoticket();
//								}
								FinanceSellProductBean fspBean = new FinanceSellProductBean();
								fspBean.setOrderId(order.getId());
								fspBean.setProductId(pId);
								fspBean.setBuyCount(_buyCount);
								fspBean.setPrice(price);
								fspBean.setDprice(dPrice);
								fspBean.setPrice5(price5);
								fspBean.setProductLine(productLine);	//财务用产品线
								fspBean.setParentId1(parentId1);
								fspBean.setParentId2(parentId2);
								fspBean.setParentId3(parentId3);
								fspBean.setCreateDatetime(DateUtil.getNow());
								fspBean.setDataType(1);	//1-赠品销售出库
								
								fspBean.setBalanceMode(batch.getTicket());	
								
								int supplierId = FinanceSellProductBean.querySupplier(db, batchCode);
								fspBean.setSupplierId(supplierId);
								fspBean.setFinanceSellId(fsId);
								
								frfService.addFinanceSellProductBean(fspBean);
							}
						}
					}
					if(!order.getCode().startsWith("S")){
						dprices = Arith.round(Arith.sub(order.getDprice(), order.getPostage()), 0);	//实际发生金额（不含运费）
					}
					frfService.updateFinanceSellBean(" price =" + dprices, "id =" + fsId);
					*/
					//物流员工绩效考核表操作 
					CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
					if(csBean == null){
						request.setAttribute("tip", "此账号不是物流员工 !");
						request.setAttribute("result", "failure");
					}
					CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=1 and staff_id=" + csBean.getId() );
					int productCount=0;
					int operCount = 0;
					for(int i=0;i<shList.size();i++){
						OrderStockProductBean ospBean = (OrderStockProductBean)shList.get(i);
						productCount = productCount + ospBean.getStockoutCount();
					}
					if(cspBean != null){
						operCount = cspBean.getOperCount()+1;
						productCount = cspBean.getProductCount() + productCount;
						boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount+ ",product_count=" + productCount, " id=" + cspBean.getId());
						if(!flag){
							request.setAttribute("tip", "物流员工绩效考核更新操作失败 !");
							request.setAttribute("result", "failure");
						}
					}else{
						CargoStaffPerformanceBean cargoStaffPerformanceBean = new CargoStaffPerformanceBean();
						cargoStaffPerformanceBean.setDate(date);
						cargoStaffPerformanceBean.setProductCount(productCount);
						cargoStaffPerformanceBean.setOperCount(1);
						cargoStaffPerformanceBean.setStaffId(csBean.getId());
						cargoStaffPerformanceBean.setType(1);  //1代表复核作业
						boolean flag = cargoService.addCargoStaffPerformance(cargoStaffPerformanceBean);
						if(!flag){
							request.setAttribute("tip", "物流员工绩效考核添加操作失败 !");
							request.setAttribute("result", "failure");
						}
					}
					//emei码日志
					if(!"".equals(StringUtil.convertNull(imeiIds))){
						for(String imeiId : imeiIds.split(",")){
							IMEIBean imei = imeiService.getIMEI("id=" + imeiId);
							IMEIUserOrderBean imeiBean = new IMEIUserOrderBean();
							imeiBean.setImeiCode(imei.getCode());
							imeiBean.setOrderId(bean.getOrderId());
							imeiBean.setProductId(imei.getProductId());
							if(!imeiService.addIMEIUserOrder(imeiBean)){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "添加IMEI码user_order记录失败!");
								request.setAttribute("result", "failure");
								return;
							}
							IMEILogBean imeiLog = new IMEILogBean();
							imeiLog.setContent("订单,复核出库,imei码[" + imei.getCode() +"]由[可出库]变成[已出库]"+",地区：["+ProductStockBean.areaMap.get(bean.getStockArea())+"]");

							imeiLog.setCreateDatetime(DateUtil.getNow());
							imeiLog.setIMEI(imei.getCode());
							imeiLog.setUserId(user.getId());
							imeiLog.setUserName(user.getUsername());
							imeiLog.setOperType(IMEILogBean.OPERTYPE2);
							imeiLog.setOperCode(bean.getOrderCode());
							if(!imeiService.addIMEILog(imeiLog)){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "添加IMEI码日志失败!");
								request.setAttribute("result", "failure");
								return;
							}
							if(!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS3, "id=" + imeiId)){
								service.getDbOp().rollbackTransaction();
								System.out.println("复核IMEI出错：IMEICode【" + imei.getCode() + "】,订单id【" + order.getId() + "】,时间【" + DateUtil.getNow() + "】。");
								request.setAttribute("tip", "更新IMEI码状态失败!");
								request.setAttribute("result", "failure");
								return;
							}
						}
					}
					// ---------liuruilan-----2012-09-03-------
					//订单批次波次状态修改结束
					
					// 估算各项物流成本
//					AuditPackageBean pBean = service.getAuditPackage("order_id =" + mb.getOrderId());
//					Map<String,String> argMap = new HashMap<String, String>();
//					argMap.put("orderId", ""+mb.getOrderId());
//					argMap.put("price", ""+mb.getPrice());
//					argMap.put("weight", ""+pBean.getWeight());
//					argMap.put("express", ""+mb.getBalanceType());
//					argMap.put("balanceArea", ""+pBean.getAreano());
//					argMap.put("destProvince", ""+fsBean.getAddrSub1());
//					argMap.put("destCity", ""+fsBean.getAddrSub2());
//					argMap.put("buyMode", ""+mb.buyMode);
//					
//					
//					try{
//						FinanceCache.addCharge(argMap, service.getDbOp());
//					}catch(Exception e){
//						e.printStackTrace();
//					}
					//当抽取为接口的时候需要注意仓储调用和财务调用为一个方法，需要区分。因仓储有快递公司到财务快递公司的对应。
//					FinanceCache.addCharge(argMap, service.getDbOp());
//					long time9=System.currentTimeMillis();System.out.println("time9-time8:"+(time9-time8)+" 财务相关，员工绩效");//////////////////////////////////
					service.getDbOp().commitTransaction();
					
//					System.out.println("time9-time1： "+(time9-time1)+" 总时间");///////////////////////////////////////////////////////////////////
				} else {
					service.getDbOp().commitTransaction();
				}
			} catch (Exception e) {
				System.out.print(DateUtil.getNow());
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				session.removeAttribute(key);
				service.releaseAll();
				redb.release();
				adminService.close();
			}
		}
	}

	/**
	 * 作者：李北金
	 * 
	 * 创建日期：2007-11-15
	 * 
	 * 说明：删除订单出货
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void deleteOrderStock(HttpServletRequest request, HttpServletResponse response) {
		//log记录
		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if (admin == null) {
			request.setAttribute("tip", "当前没有登录，添加失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = admin.getGroup();
		synchronized(orderStockLock){
			int operId = StringUtil.StringToId(request.getParameter("id"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SortingInfoService sortingService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SecondSortingSplitService secondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
//			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			WareService wareService= new WareService();
			try {
				OrderStockBean bean = service.getOrderStock("id = " + operId);
				voOrder order = wareService.getOrder(bean.getOrderId());
				boolean isSubOrder=false;
				if(order.getBuyMode()!=1 && !order.getCode().startsWith("S")){
					ResultSet res = service.getDbOp().executeQuery("select * from user_order_sub_list where child_id="+order.getId());
					if(res.next()){
						isSubOrder=true;
					}
				}
				if(bean.getStatus() == OrderStockBean.STATUS1){
					service.getDbOp().startTransaction();

					service.deleteOrderStock("id = " + operId);
					service.deleteOrderStockProduct("order_stock_id = " + operId);
					//复合列表中删除订单对分拣批次的操作
					SortingBatchOrderBean orderBean = sortingService.getSortingBatchOrderInfo("delete_status<>1 and order_id="+bean.getOrderId());
					if(orderBean!=null){
						sortingService.updateSortingBatchOrderInfo("delete_status=1", "id=" + orderBean.getId());
						if (orderBean.getSortingGroupId() != 0) {
							// 该波次下完成的订单数量
							int completeOrderCount = sortingService.getSortingBatchOrderCount("sorting_group_id=" + orderBean.getSortingGroupId() + " and delete_status<>1 and status=3");
							// 该波次下未完成的订单数量
							int noCompleteOrderCount = sortingService.getSortingBatchOrderCount("sorting_group_id=" + orderBean.getSortingGroupId() + " and delete_status<>1 and status in(0,1,2)");
							// 如果完成的订单数量大于零，未完成的数量等于零，则该波次状态改为已完成
							if (completeOrderCount > 0 && noCompleteOrderCount == 0) {
								sortingService.updateSortingBatchGroupInfo("status=2,complete_datetime='" + DateUtil.getNow() + "'", "id=" + orderBean.getSortingGroupId());
							}
							// 如果完成的订单数量等于零，未完成的数量等于零，则该波次状态改为已废弃
							else if (completeOrderCount == 0 && noCompleteOrderCount == 0) {
								sortingService.updateSortingBatchGroupInfo("status=3", "id=" + orderBean.getSortingGroupId());
							}
							// 该批次下完成的波次数
							int completeGroupCount = sortingService.getSortingBatchGroupCount("sorting_batch_id=" + orderBean.getSortingBatchId() + " and status=2");
							// 该批次下未完成的波次数
							int noCompletegroupCount = sortingService.getSortingBatchGroupCount("sorting_batch_id=" + orderBean.getSortingBatchId() + " and status in(0,1)");
							// 该批次下完成的订单数量
							int completeCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status=3 and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 该批次下未完成的订单数量
							int noCompleteCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status in(0,1,2) and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 如果完成的波次大于零，未完成的波次等于零，则将批次改为已完成状态
							if (completeGroupCount > 0 && noCompletegroupCount == 0) {
								sortingService.updateSortingBatchInfo("status=4", "id=" + orderBean.getSortingBatchId());
							}
							// 如果完成的波次等于零，未完成的波次等于零，则将批次改为无需处理状态
							else if (completeGroupCount == 0 && noCompletegroupCount == 0 && completeCount == 0 && noCompleteCount == 0) {
								sortingService.updateSortingBatchInfo("status=5", "id=" + orderBean.getSortingBatchId());
							}
						} else {
							// 该批次下完成的订单数量
							int completeCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status=3 and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 该批次下未完成的订单数量
							int noCompleteCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status in(0,1,2) and sorting_batch_id=" + orderBean.getSortingBatchId());
							
						    if (completeCount == 0 && noCompleteCount == 0) {
								sortingService.updateSortingBatchInfo("status=5", "id=" + orderBean.getSortingBatchId());
							}
						    else if(completeCount > 0 && noCompleteCount == 0){
						    	sortingService.updateSortingBatchInfo("status=4", "id=" + orderBean.getSortingBatchId());
						    }
						}
					}
					
					int status =0;
					if(order != null && order.getBuyMode() == 1){
						service.getDbOp().executeUpdate("update user_order set status=3, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
						service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal = 3,deal_datetime = null,admin_id = 0 where id = "+bean.getOrderId());
						status=3;
					} else {
						if(isSubOrder){//子订单 需要变成重复，不能让其在有操作
							service.getDbOp().executeUpdate("update user_order set status=10, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
							status=10;
						}else{
							service.getDbOp().executeUpdate("update user_order set status=2, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
							status=2;
						}
						service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal = 3,deal_datetime = null,admin_id = 0 where id = "+bean.getOrderId());
					}
					if(order!=null){// 如果修改了订单的状态，就记录操作日志
						addUserOrderlog(order, admin, status, 3);
					}
					StockAdminHistoryBean log = new StockAdminHistoryBean();
					log.setAdminId(admin.getId());
					log.setAdminName(admin.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("删除订单出货操作：" + bean.getName());
					log.setType(StockAdminHistoryBean.DELETE);
					service.addStockAdminHistory(log);
					service.updateStockAdminHistory("deleted = 1", "log_type = " + StockAdminHistoryBean.ORDER_STOCK + " and log_id = " + bean.getId());

					if(order.getCode().startsWith("S")){
						if(!group.isFlag(2903)){
							request.setAttribute("tip", "您没有删除S单权限");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					} else {
						if(!group.isFlag(35)){
							request.setAttribute("tip", "您没有删单权限");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}	
					/**
					 * 发送取消订单
					 */
					WayBillApplication wayBillApplication= SpringHandler.getBean("wayBillApplication");
					List<Integer> deliverList = new ArrayList<Integer>();
					deliverList.add(order.getDeliver());
					List<String> orderCodeList = new ArrayList<String>();
					orderCodeList.add(order.getCode());
					String result =wayBillApplication.cancelWayBills(deliverList, orderCodeList);
					//String result =wayBillApplication.cancelDeliverOrder(42, "B150408093738111");
					if(!"success".equals(result) && result!=null){
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					
					service.getDbOp().commitTransaction();
				} else if(bean.getStatus() == OrderStockBean.STATUS2 || bean.getStatus() == OrderStockBean.STATUS6) {
					String condition = "order_stock_id = " + bean.getId() + " and status = " + OrderStockProductBean.DEALED;
					ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");
					service.getDbOp().startTransaction();
					service.updateOrderStock("status = " + OrderStockBean.STATUS4 + ", last_oper_time = now()", "id = " + bean.getId());
					service.deleteAuditPackage("order_id="+order.getId());//删除核对包裹记录
					service.updateOrderStockProduct("status = " + OrderStockProductBean.DELETED, "order_stock_id = " + operId);
					//复合列表中删除订单对分拣批次的操作
					SortingBatchOrderBean orderBean = sortingService.getSortingBatchOrderInfo("delete_status<>1 and order_id="+bean.getOrderId());
					SortingBatchGroupBean sbg=null;//订单所属分拣波次
					if(orderBean!=null){
						sbg=sortingService.getSortingBatchGroupInfo("id="+orderBean.getSortingGroupId());
						sortingService.updateSortingBatchOrderInfo("delete_status=1", "id=" + orderBean.getId());
						secondSortingSplitService.updateSortingBatchOrderProduct("is_delete=1", "sorting_batch_order_id="+orderBean.getId());
						if (orderBean.getSortingGroupId() != 0) {
							// 该波次下完成的订单数量
							int completeOrderCount = sortingService.getSortingBatchOrderCount("sorting_group_id=" + orderBean.getSortingGroupId() + " and delete_status<>1 and status=3");
							// 该波次下未完成的订单数量
							int noCompleteOrderCount = sortingService.getSortingBatchOrderCount("sorting_group_id=" + orderBean.getSortingGroupId() + " and delete_status<>1 and status in(0,1,2)");
							// 如果完成的订单数量大于零，未完成的数量等于零，则该波次状态改为已完成
							if (completeOrderCount > 0 && noCompleteOrderCount == 0) {
								sortingService.updateSortingBatchGroupInfo("status=2,complete_datetime='" + DateUtil.getNow() + "'", "id=" + orderBean.getSortingGroupId());
							}
							// 如果完成的订单数量等于零，未完成的数量等于零，则该波次状态改为已废弃
							else if (completeOrderCount == 0 && noCompleteOrderCount == 0) {
								sortingService.updateSortingBatchGroupInfo("status=3", "id=" + orderBean.getSortingGroupId());
							}
							// 该批次下完成的波次数
							int completeGroupCount = sortingService.getSortingBatchGroupCount("sorting_batch_id=" + orderBean.getSortingBatchId() + " and status=2");
							// 该批次下未完成的波次数
							int noCompletegroupCount = sortingService.getSortingBatchGroupCount("sorting_batch_id=" + orderBean.getSortingBatchId() + " and status in(0,1)");
							// 该批次下完成的订单数量
							int completeCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status=3 and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 该批次下未完成的订单数量
							int noCompleteCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status in(0,1,2) and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 如果完成的波次大于零，未完成的波次等于零，则将批次改为已完成状态
							if (completeGroupCount > 0 && noCompletegroupCount == 0) {
								sortingService.updateSortingBatchInfo("status=4", "id=" + orderBean.getSortingBatchId());
							}
							// 如果完成的波次等于零，未完成的波次等于零，则将批次改为无需处理状态
							else if (completeGroupCount == 0 && noCompletegroupCount == 0 && completeCount == 0 && noCompleteCount == 0) {
								sortingService.updateSortingBatchInfo("status=5", "id=" + orderBean.getSortingBatchId());
							}
						} else {
							// 该批次下完成的订单数量
							int completeCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status=3 and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 该批次下未完成的订单数量
							int noCompleteCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status in(0,1,2) and sorting_batch_id=" + orderBean.getSortingBatchId());
							
						    if (completeCount == 0 && noCompleteCount == 0) {
								sortingService.updateSortingBatchInfo("status=5", "id=" + orderBean.getSortingBatchId());
							}
						    else if(completeCount > 0 && noCompleteCount == 0){
						    	sortingService.updateSortingBatchInfo("status=4", "id=" + orderBean.getSortingBatchId());
						    }
						}
					}
					
					int saId = 0;//分拣异常单id
					boolean isAbnormal=true;//是否需要生成异常单
					if(orderBean!=null&& orderBean.getStatus() == SortingBatchOrderBean.STATUS2 ) {//没有分拣批次订单或分拣批次订单是已分拣
						//如果该订单属于PDA分拣且没有分拣任何商品，则不用生成异常单
						if(sbg!=null&&sbg.getSortingType()==1){
							//该订单分拣量大于0的记录
							SortingBatchOrderProductBean sbop=secondSortingSplitService.getSortingBatchOrderProduct("sorting_batch_order_id="+orderBean.getId()+" and sorting_count>0");
							if(sbop==null){//没有PDA分拣记录，不生成异常单
								isAbnormal=false;
							}
						}
						if(isAbnormal==true){
							SortingAbnormalBean saBean = new SortingAbnormalBean();
							String code = sortingAbnormalDisposeService.getNewSortingAbnormalCode(bean.getStockArea(), sortingAbnormalDisposeService.getDbOp());
							if(code.equals("FAIL") ) {
								request.setAttribute("tip", "订单"+order.getCode()+ "添加撤单异常信息寻找地区时失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							saBean.setAbnormalType(SortingAbnormalBean.ABNORMALTYPE0);
							saBean.setCode(code);
							saBean.setOperCode(bean.getCode());
							saBean.setOperType(SortingAbnormalBean.OPERTYPE0);
							saBean.setStatus(SortingAbnormalBean.STATUS0);
							saBean.setCreateUserId(admin.getId());
							saBean.setCreateUserName(admin.getUsername());
							saBean.setWareArea(bean.getStockArea());
							saBean.setCreateDatetime(DateUtil.getNow());
							if( !sortingAbnormalDisposeService.addSortingAbnormal(saBean) ) {
								request.setAttribute("tip", "订单"+order.getCode()+ "添加撤单异常信息失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							saId = service.getDbOp().getLastInsertId();
						}
					}
					for(int i=0; i<shList.size(); i++){
						OrderStockProductBean sh = (OrderStockProductBean) shList.get(i);
						voProduct product = wareService.getProduct(sh.getProductId());
						ProductStockBean ps = psService.getProductStock("id=" + sh.getStockoutId());
						
						if(orderBean==null|| orderBean.getStatus() != SortingBatchOrderBean.STATUS2 ) {
							String set = "remark = concat(remark , '(操作前库存" + ps.getStock()
							+ "锁定库存" + ps.getLockCount()
							+ ",操作后库存" + (ps.getStock() + sh.getStockoutCount())
							+ "锁定库存" + (ps.getLockCount() - sh.getStockoutCount())
							+ ")'), deal_datetime = now()";
	
							service.updateOrderStockProduct(set, "id = " + sh.getId());
							//psService.updateProductStock("stock=(stock + " + sh.getStockoutCount() + "), lock_count=(lock_count-" + sh.getStockoutCount() + ")", "id=" + ps.getId());
							if(!psService.updateProductStockCount(sh.getStockoutId(), sh.getStockoutCount())){
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							if(!psService.updateProductLockCount(sh.getStockoutId(), -sh.getStockoutCount())){
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							
						}

						if(bean.getStatus() == OrderStockBean.STATUS6){//已申请复核
							//已申请复核，删除出库记录，解锁库存
							List list = service.getOrderStockProductCargoList("order_stock_product_id = "+sh.getId(), -1, -1, "id asc");
							if(list == null || list.size() == 0){
								request.setAttribute("tip", "订单"+order.getCode()+"商品"+product.getCode()+"添加撤单异常信息失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							for(int j=0;j<list.size();j++){
								OrderStockProductCargoBean ospc = (OrderStockProductCargoBean)list.get(j);
								if(orderBean==null|| orderBean.getStatus() != SortingBatchOrderBean.STATUS2 ) {//批次订单状态不是分拣中，可以直接还原库存
									if(!cargoService.updateCargoProductStockCount(ospc.getCargoProductStockId(), ospc.getCount())){
										request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
									if(!cargoService.updateCargoProductStockLockCount(ospc.getCargoProductStockId(), -ospc.getCount())){
										request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
								} else {//批次订单状态是分拣中，不还原库存，添加货位异常单
										//如果是pda分拣，将未分拣数量直接将锁定量还原，分拣数量记录在商品异常单中
										if (sbg!=null&&sbg.getSortingType()==1) {
											CargoInfoBean ci=cargoService.getCargoInfo("whole_code='"+ospc.getCargoWholeCode()+"'");//订单关联货位
											CargoInfoBean operCargo=cargoService.getCargoInfo("area_id="+bean.getStockArea()+" and stock_type=0 and store_type="+CargoInfoBean.STORE_TYPE5);
											SortingBatchOrderProductBean sbop=secondSortingSplitService.getSortingBatchOrderProduct("sorting_batch_order_id="+orderBean.getId()+" and cargo_id="+ci.getId()+" and product_id="+sh.getProductId());
											if (sbop == null) {
												request.setAttribute("tip", "分拣订单商品未找到！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return;
											}
											String set = "remark = concat(remark , '(操作前库存" + ps.getStock()
													+ "锁定库存" + ps.getLockCount()
													+ ",操作后库存" + (ps.getStock() + (sbop.getCount()-sbop.getSortingCount()))
													+ "锁定库存" + (ps.getLockCount() - (sbop.getCount()-sbop.getSortingCount()))
													+ ")'), deal_datetime = now()";
							
											service.updateOrderStockProduct(set, "id = " + sh.getId());
											//psService.updateProductStock("stock=(stock + " + sh.getStockoutCount() + "), lock_count=(lock_count-" + sh.getStockoutCount() + ")", "id=" + ps.getId());
											if(!psService.updateProductStockCount(sh.getStockoutId(), sbop.getCount()-sbop.getSortingCount())){
												request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return;
											}
											if(!psService.updateProductLockCount(sh.getStockoutId(), -(sbop.getCount()-sbop.getSortingCount()))){
												request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return;
											}
											
											if(operCargo == null){//波次是PDA分拣且没有设置对应地区的作业货位
												request.setAttribute("tip", "没有设置该地区作业货位！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return;
											}
											int updateCount = sbop.getCount()-sbop.getSortingCount();
											if (updateCount != 0) {
												if(!cargoService.updateCargoProductStockCount(ospc.getCargoProductStockId(), sbop.getCount()-sbop.getSortingCount())){
													request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
													request.setAttribute("result", "failure");
													service.getDbOp().rollbackTransaction();
													return;
												}
												if(!cargoService.updateCargoProductStockLockCount(ospc.getCargoProductStockId(), -(sbop.getCount()-sbop.getSortingCount()))){
													request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
													request.setAttribute("result", "failure");
													service.getDbOp().rollbackTransaction();
													return;
												}
											}
											if(isAbnormal==true){
												SortingAbnormalProductBean sopBean = new SortingAbnormalProductBean();
												sopBean.setCargoWholeCode(operCargo.getWholeCode());
												sopBean.setCount(ospc.getCount());
												sopBean.setLockCount(sbop.getSortingCount());
												sopBean.setProductCode(sh.getProductCode());
												sopBean.setProductId(sh.getProductId());
												sopBean.setSortingAbnormalId(saId);
												sopBean.setStatus(SortingAbnormalProductBean.STATUS_UNDEAL);
												sopBean.setLastOperDatetime(DateUtil.getNow());
												if( !sortingAbnormalDisposeService.addSortingAbnormalProduct(sopBean) ) {
													request.setAttribute("tip", "订单"+order.getCode()+"商品"+product.getCode() + "添加撤单异常信息失败！");
													request.setAttribute("result", "failure");
													service.getDbOp().rollbackTransaction();
													return;
												}
											}
										} 
										//不是pda分拣，与原来逻辑一样，将所有数量都记录在商品异常单中
										else {
											SortingAbnormalProductBean sopBean = new SortingAbnormalProductBean();
											sopBean.setCargoWholeCode(ospc.getCargoWholeCode());
											sopBean.setCount(ospc.getCount());
											sopBean.setLockCount(ospc.getCount());
											sopBean.setProductCode(sh.getProductCode());
											sopBean.setProductId(sh.getProductId());
											sopBean.setSortingAbnormalId(saId);
											sopBean.setStatus(SortingAbnormalProductBean.STATUS_UNDEAL);
											sopBean.setLastOperDatetime(DateUtil.getNow());
											if( !sortingAbnormalDisposeService.addSortingAbnormalProduct(sopBean) ) {
												request.setAttribute("tip", "订单"+order.getCode()+"商品"+product.getCode() + "添加撤单异常信息失败！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return;
											}
										}
									
								}
							}
						}
					}
					service.getDbOp().executeUpdate("update user_order set stockout = 0 where code = '" + bean.getOrderCode() + "'");
					int status =0;
					if(order != null && order.getBuyMode() == 1){
						service.getDbOp().executeUpdate("update user_order set status=3, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
						service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal = 3,deal_datetime = null,admin_id = 0 where id = "+bean.getOrderId());
						status=3;
					} else {
						if(isSubOrder){//子订单 需要变成重复，不能让其在有操作
							service.getDbOp().executeUpdate("update user_order set status=10, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
							status=10;
						}else{
							service.getDbOp().executeUpdate("update user_order set status=2, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
							status=2;
						}
						service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal = 3,deal_datetime = null,admin_id = 0 where id = "+bean.getOrderId());
					}
					if(order!=null){	// 如果修改了订单的状态，就记录操作日志
						addUserOrderlog(order, admin, status, 3);
					} 
					service.getDbOp().executeUpdate("delete from user_order_product_split where order_id = " + bean.getOrderId());
					service.getDbOp().executeUpdate("delete from user_order_present_split where order_id = " + bean.getOrderId());
					
					StockAdminHistoryBean log = new StockAdminHistoryBean();
					log.setAdminId(admin.getId());
					log.setAdminName(admin.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("删除订单出货操作：" + bean.getName());
					log.setType(StockAdminHistoryBean.DELETE);
					service.addStockAdminHistory(log);
					service.updateStockAdminHistory("deleted = 1", "log_type = "
							+ StockAdminHistoryBean.ORDER_STOCK + " and log_id = "
							+ bean.getId());

					if(order.getCode().startsWith("S")){
						if(!group.isFlag(2903)){
							request.setAttribute("tip", "您没有删除S单权限");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					} else {
						if(!group.isFlag(35)){
							request.setAttribute("tip", "您没有删单权限");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					/**
					 * 发送取消订单
					 */
					WayBillApplication wayBillApplication= SpringHandler.getBean("wayBillApplication");
					List<Integer> deliverList = new ArrayList<Integer>();
					deliverList.add(order.getDeliver());
					List<String> orderCodeList = new ArrayList<String>();
					orderCodeList.add(order.getCode());
					String result =wayBillApplication.cancelWayBills(deliverList, orderCodeList);
					//String result =wayBillApplication.cancelDeliverOrder(42, "B150408093738111");
					if(!"success".equals(result) && result!=null){
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					service.getDbOp().commitTransaction();
				} else {
					request.setAttribute("tip", "该操作已经申请或完成，不能再修改！");
					request.setAttribute("result", "failure");
					return;
				}
				
				wareService.deleteSendMsgAndAutolibrary(order);
				
			} catch(Exception e){
				e.printStackTrace();
			}finally {
				service.releaseAll();
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 删除订单出货单，可以通过传递 dbOp，来实现事物操作
	 * @param request
	 * @param response
	 * @param dbOp
	 */
	public boolean deleteOrderStock(int operId, voUser admin, DbOperation dbOp) {
		boolean connOutSide = false;
		if(dbOp != null){
			connOutSide = true;
		}
		synchronized(orderStockLock){
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SortingInfoService sortingService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			try {
				OrderStockBean bean = service.getOrderStock("id = " + operId);
				voOrder isOlderOrder=null;
				if (bean.getStatus() == OrderStockBean.STATUS1){
					if(!connOutSide){
						service.getDbOp().startTransaction();
					}

					service.deleteOrderStock("id = " + operId);
					service.deleteOrderStockProduct("order_stock_id = " + operId);
//					//复合列表中删除订单对分拣批次的操作
					SortingBatchOrderBean orderBean = sortingService.getSortingBatchOrderInfo("delete_status<>1 and order_id="+bean.getOrderId());
					if(orderBean!=null){
						sortingService.updateSortingBatchOrderInfo("delete_status=1", "id=" + orderBean.getId());
						if (orderBean.getSortingGroupId() != 0) {
							// 该波次下完成的订单数量
							int completeOrderCount = sortingService.getSortingBatchOrderCount("sorting_group_id=" + orderBean.getSortingGroupId() + " and delete_status<>1 and status=3");
							// 该波次下未完成的订单数量
							int noCompleteOrderCount = sortingService.getSortingBatchOrderCount("sorting_group_id=" + orderBean.getSortingGroupId() + " and delete_status<>1 and status in(0,1,2)");
							// 如果完成的订单数量大于零，未完成的数量等于零，则该波次状态改为已完成
							if (completeOrderCount > 0 && noCompleteOrderCount == 0) {
								sortingService.updateSortingBatchGroupInfo("status=2,complete_datetime='" + DateUtil.getNow() + "'", "id=" + orderBean.getSortingGroupId());
							}
							// 如果完成的订单数量等于零，未完成的数量等于零，则该波次状态改为已废弃
							else if (completeOrderCount == 0 && noCompleteOrderCount == 0) {
								sortingService.updateSortingBatchGroupInfo("status=3", "id=" + orderBean.getSortingGroupId());
							}
							// 该批次下完成的波次数
							int completeGroupCount = sortingService.getSortingBatchGroupCount("sorting_batch_id=" + orderBean.getSortingBatchId() + " and status=2");
							// 该批次下未完成的波次数
							int noCompletegroupCount = sortingService.getSortingBatchGroupCount("sorting_batch_id=" + orderBean.getSortingBatchId() + " and status in(0,1)");
							// 该批次下完成的订单数量
							int completeCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status=3 and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 该批次下未完成的订单数量
							int noCompleteCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status in(0,1,2) and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 如果完成的波次大于零，未完成的波次等于零，则将批次改为已完成状态
							if (completeGroupCount > 0 && noCompletegroupCount == 0) {
								sortingService.updateSortingBatchInfo("status=4", "id=" + orderBean.getSortingBatchId());
							}
							// 如果完成的波次等于零，未完成的波次等于零，则将批次改为无需处理状态
							else if (completeGroupCount == 0 && noCompletegroupCount == 0 && completeCount == 0 && noCompleteCount == 0) {
								sortingService.updateSortingBatchInfo("status=5", "id=" + orderBean.getSortingBatchId());
							}
						} else {
							// 该批次下完成的订单数量
							int completeCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status=3 and sorting_batch_id=" + orderBean.getSortingBatchId());
							// 该批次下未完成的订单数量
							int noCompleteCount = sortingService.getSortingBatchOrderCount("delete_status<>1 and status in(0,1,2) and sorting_batch_id=" + orderBean.getSortingBatchId());
							
						    if (completeCount == 0 && noCompleteCount == 0) {
								sortingService.updateSortingBatchInfo("status=5", "id=" + orderBean.getSortingBatchId());
							}
						    else if(completeCount > 0 && noCompleteCount == 0){
						    	sortingService.updateSortingBatchInfo("status=4", "id=" + orderBean.getSortingBatchId());
						    }
						}
					}
					voOrder order = adminService.getOrder(bean.getOrderId());
					isOlderOrder=order;
					if(order != null && order.getBuyMode() == 1){
						service.getDbOp().executeUpdate("update user_order set status=3, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
					} else {
						service.getDbOp().executeUpdate("update user_order set status=2, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
					}

					//log记录
					if (admin == null) {
						//	                    request.setAttribute("tip", "当前没有登录，添加失败！");
						//	                    request.setAttribute("result", "failure");
						return false;
					}
					StockAdminHistoryBean log = new StockAdminHistoryBean();
					log.setAdminId(admin.getId());
					log.setAdminName(admin.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("删除订单出货操作：" + bean.getName());
					log.setType(StockAdminHistoryBean.DELETE);
					service.addStockAdminHistory(log);
					service.updateStockAdminHistory("deleted = 1", "log_type = " + StockAdminHistoryBean.ORDER_STOCK + " and log_id = " + bean.getId());

					// 删除发货单的时候，要特殊处理一下售后换货订单
					if(order.getBuyMode() == Constants.BUY_TYPE_NIFFER  || order.getCode().startsWith("S")){
						/*
	                	// 售后换货订单
	                	// 把订单设置为重复
	                	service.getDbOp().executeUpdate("update user_order set status=10 where id=" + order.getId());
	                	IAfterSalesService ass = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	                	AfterSaleRefundOrderBean asro = ass.getAfterSaleRefundOrder("new_order_code='" + order.getCode() + "'");
	                	ass.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_换货删除, "code='" + asro.getAfterSaleOrderCode() + "'");
	                	ass.updateAfterSaleRefundOrder("status=" + AfterSaleRefundOrderBean.STATUS_DELETED, "id=" + asro.getId());
						 */
						// 售后换货订单，申请发货以后，只能完成发货，不能删除订单发货单
						//	                	request.setAttribute("tip", "售后换货订单，不能删除订单发货单！");
						//	                    request.setAttribute("result", "failure");
						return false;
					}
					if(!connOutSide){
						service.getDbOp().commitTransaction();
					}
				} else if(bean.getStatus() == OrderStockBean.STATUS2 || bean.getStatus() == OrderStockBean.STATUS6) {
					String condition = "order_stock_id = " + bean.getId() + " and status = " + OrderStockProductBean.DEALED;
					ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");
					if(!connOutSide){
						service.getDbOp().startTransaction();
					}
					service.updateOrderStock("status = " + OrderStockBean.STATUS4 + ", last_oper_time = now()", "id = " + bean.getId());

					service.updateOrderStockProduct("status = " + OrderStockProductBean.DELETED, "order_stock_id = " + operId);
					for(int i=0; i<shList.size(); i++){
						OrderStockProductBean sh = (OrderStockProductBean) shList.get(i);
						voProduct product = adminService.getProduct(sh.getProductId(), service.getDbOp());
						ProductStockBean ps = psService.getProductStock("id=" + sh.getStockoutId());

						String set = "remark = concat(remark , '(操作前库存" + ps.getStock()
						+ "锁定库存" + ps.getLockCount()
						+ ",操作后库存" + (ps.getStock() + sh.getStockoutCount())
						+ "锁定库存" + (ps.getLockCount() - sh.getStockoutCount())
						+ ")'), deal_datetime = now()";

						service.updateOrderStockProduct(set, "id = " + sh.getId());
						//psService.updateProductStock("stock=(stock + " + sh.getStockoutCount() + "), lock_count=(lock_count-" + sh.getStockoutCount() + ")", "id=" + ps.getId());
						if(!psService.updateProductStockCount(sh.getStockoutId(), sh.getStockoutCount())){
							//	                		request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
							//		                    request.setAttribute("result", "failure");
							return false;
						}
						if(!psService.updateProductLockCount(sh.getStockoutId(), -sh.getStockoutCount())){
							//							request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
							//		                    request.setAttribute("result", "failure");
							return false;
						}

					}
					service.getDbOp().executeUpdate("update user_order set stockout = 0 where code = '" + bean.getOrderCode() + "'");
					voOrder order = adminService.getOrder(bean.getOrderId());
					isOlderOrder=order;
					if(order != null && order.getBuyMode() == 1){
						service.getDbOp().executeUpdate("update user_order set status=3, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
					} else {
						service.getDbOp().executeUpdate("update user_order set status=2, stockout_deal = 3 where code = '" + bean.getOrderCode() + "'");
					}

					service.getDbOp().executeUpdate("delete from user_order_product_split where order_id = " + bean.getOrderId());
					service.getDbOp().executeUpdate("delete from user_order_present_split where order_id = " + bean.getOrderId());

					//log记录
					//	                voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
					if (admin == null) {
						//	                    request.setAttribute("tip", "当前没有登录，添加失败！");
						//	                    request.setAttribute("result", "failure");
						return false;
					}
					StockAdminHistoryBean log = new StockAdminHistoryBean();
					log.setAdminId(admin.getId());
					log.setAdminName(admin.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("删除订单出货操作：" + bean.getName());
					log.setType(StockAdminHistoryBean.DELETE);
					service.addStockAdminHistory(log);
					service.updateStockAdminHistory("deleted = 1", "log_type = "
							+ StockAdminHistoryBean.ORDER_STOCK + " and log_id = "
							+ bean.getId());

					// 删除发货单的时候，要特殊处理一下售后换货订单
					if(order.getBuyMode() == Constants.BUY_TYPE_NIFFER || order.getCode().startsWith("S")){

						// 售后换货订单
						// 把订单设置为重复
						service.getDbOp().executeUpdate("update user_order set status=7 where id=" + order.getId());
						IAfterSalesService ass = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
						AfterSaleRefundOrderBean asro = ass.getAfterSaleRefundOrder("new_order_code='" + order.getCode() + "'");
						//ass.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_换货删除, "order_code='" + asro.getAfterSaleOrderCode() + "'");
						ass.updateAfterSaleRefundOrder("status=" + AfterSaleRefundOrderBean.STATUS_DELETED, "id=" + asro.getId());

						// 售后换货订单，申请发货以后，只能完成发货，不能删除订单发货单
						//	                	request.setAttribute("tip", "售后换货订单，不能删除订单发货单！");
						//	                    request.setAttribute("result", "failure");
						//	                    return false;
					}

					if(!connOutSide){
						service.getDbOp().commitTransaction();
					}
				} else {
					//	                request.setAttribute("tip", "该操作已经申请或完成，不能再修改！");
					//	                request.setAttribute("result", "failure");
					return false;
				}
			 
				adminService.deleteSendMsgAndAutolibrary(isOlderOrder);
			} finally {
				if(!connOutSide){
					service.releaseAll();
				}
				adminService.close();
			}
			return true;
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-9
	 * 
	 * 说明：
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void collectOrderStock(HttpServletRequest request,
			HttpServletResponse response) {
		String[] ids = request.getParameterValues("ids");
		if (ids == null) {
			request.setAttribute("result", "failure");
			return;
		}

		//订单编号
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
		try {
			//相关记录
			int i, count;
			count = ids.length;
			List operList = new ArrayList();
			OrderStockBean oper = null;
			StringBuffer sb = new StringBuffer();
			for (i = 0; i < count; i++) {
				oper = service.getOrderStock("id = " + ids[i]);
				if (oper != null) {
					operList.add(oper);
					oper.setOrder(adminService.getOrder("code = '" + oper.getOrderCode() + "'"));
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(ids[i]);
				}
			}
			if (sb.length() == 0) {
				request.setAttribute("result", "failure");
				return;
			}

			//产品出库列表
			List shList = new ArrayList();	//库存充足的产品列表
			List outStockShList = new ArrayList();	//库存不足的产品列表
			OrderStockProductBean sh = null;
			Map scMap = new HashMap();
			try {
				String query = "select group_concat(order_stock_id) oper_ids, sum(case when stock_area=0 then stockout_count else 0 end) c, sum(case when stock_area=1 then stockout_count else 0 end) c_gf, sum(case when stock_area=2 then stockout_count else 0 end) c_gs, product_id from order_stock_product sh where sh.order_stock_id in ("
					+ sb.toString()
					+ ") group by sh.product_id order by c desc";
				ResultSet rs = service.getDbOp().executeQuery(query);
				while (rs.next()) {
					sh = new OrderStockProductBean();
					sh.setRemark("," + rs.getString("oper_ids") + ",");
					sh.setProductId(rs.getInt("product_id"));

					int [] sc = new int[3];
					sc[0] = rs.getInt("c");
					sc[1] = rs.getInt("c_gf");
					sc[2] = rs.getInt("c_gs");
					scMap.put(Integer.valueOf(sh.getProductId()), sc);

					sh.setProduct(adminService.getProduct(sh.getProductId()));
					sh.getProduct().setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));

					if(sc[0] > sh.getProduct().getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) || sc[1] > sh.getProduct().getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) || sc[2] > sh.getProduct().getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)){
						outStockShList.add(sh);
					} else {
						shList.add(sh);
					}
				}
				outStockShList.addAll(shList);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			/*
            if (shList.size() == 0) {
                request.setAttribute("result", "failure");
                return;
            }*/
			request.setAttribute("operList", operList);
			request.setAttribute("shList", outStockShList);
			request.setAttribute("scMap", scMap);
		} finally {
			adminService.close();
			service.releaseAll();
			psService.releaseAll();
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-9
	 * 
	 * 说明：修改订单出货操作
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void editOrderStock(HttpServletRequest request, HttpServletResponse response) {
		synchronized(orderStockLock){
			int id = StringUtil.StringToId(request.getParameter("id"));
			String name = StringUtil.dealParam(request.getParameter("name"));
			String remark = StringUtil.dealParam(request.getParameter("remark"));
			String back = StringUtil.dealParam(request.getParameter("back"));
			int area = StringUtil.StringToId(request.getParameter("area"));

			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			try {
				OrderStockBean bean = service.getOrderStock("id = " + id);
				//开始事务
				service.getDbOp().startTransaction();
				service.updateOrderStock("name = '" + name + "'", "id = " + id);
				service.updateOrderStock("remark = '" + remark + "'", "id = " + id);

				//修改地区
				if (area != bean.getStockArea()) {
					//如果是订单出货，而且是“待发货”状态
					if (bean.getStatus() == OrderStockBean.STATUS2) {
						bean.setOrder(adminService.getOrder("code='" + bean.getOrderCode() + "'"));
						List orderProductList = adminService.getOrderProductsSplit(bean.getOrder().getId());
						Iterator iter = orderProductList.listIterator();
						while(iter.hasNext()){
							voOrderProduct vop = (voOrderProduct)iter.next();
							vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						}

						// 检查库存
						int stockStatus = checkStock(orderProductList);
						boolean stockEnough = true;
						// 各地库存都不足
						if (stockStatus == 3) {
							stockEnough = false;
						}
						// 增城 库存不足
						else if ((stockStatus == 2 || stockStatus == 4 || stockStatus == 7) && area == ProductStockBean.AREA_ZC) {
							stockEnough = false;
						}
						// 广分 库存不足
						else if ((stockStatus == 1 || stockStatus == 4 || stockStatus == 6) && area == ProductStockBean.AREA_GF) {
							stockEnough = false;
						} 
						// 广速 库存不足
						else if((stockStatus == 1 || stockStatus == 2 || stockStatus == 5) && area == ProductStockBean.AREA_GS) {
							stockEnough = false;
						}
						if(!stockEnough){
							request.setAttribute("tip", "库存不足，不能修改发货地点！");
							request.setAttribute("result", "failure");
							return;
						}

						// 开始修改
						String condition = "order_stock_id = " + id;
						ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");

						Iterator itr = null;
						OrderStockProductBean sh = null;

						itr = shList.iterator();
						String update = null;
						int stock = 0;
						while (itr.hasNext()) {
							sh = (OrderStockProductBean) itr.next();
							voProduct product = adminService.getProduct(sh.getProductId(), service.getDbOp());
							ProductStockBean psSrc = psService.getProductStock("product_id=" + product.getId() + " and area=" + bean.getStockArea() + " and type=" + bean.getStockType());
							ProductStockBean psTar = psService.getProductStock("product_id=" + product.getId() + " and area=" + area + " and type=" + bean.getStockType());
							// 库存充足，只改库存量
							if (stockEnough) {
								service.updateOrderStockProduct(
										"deal_datetime=now(), stockout_id = "
										+ psTar.getId() + ", stock_area=" + area, "id = "
										+ sh.getId());
							}
							// 库存不足，将状态改为未处理
							else {
								service.updateOrderStockProduct("stockout_id = "
										+ psTar.getId() + ", stock_area=" + area + ", status = "
										+ OrderStockProductBean.UNDEAL, "id = "
										+ sh.getId());
							}
							stock = sh.getStockoutCount();
							// 库存充足，增、减
							if (stockEnough) {
								//还原 源库的库存
								//psService.updateProductStock("stock=(stock + " + sh.getStockoutCount() + "), lock_count=(lock_count - " + sh.getStockoutCount() + ")", "id=" + psSrc.getId());
								if(!psService.updateProductStockCount(psSrc.getId(), sh.getStockoutCount())){
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
								if(!psService.updateProductLockCount(psSrc.getId(), -sh.getStockoutCount())){
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
								//减 目的库的库存
								//psService.updateProductStock("stock=(stock - " + sh.getStockoutCount() + "), lock_count=(lock_count + " + sh.getStockoutCount() + ")", "id=" + psTar.getId());
								if(!psService.updateProductStockCount(psTar.getId(), -sh.getStockoutCount())){
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
								if(!psService.updateProductLockCount(psTar.getId(), sh.getStockoutCount())){
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
							}
							// 库存不足，只增
							else {
								//还原 源库的库存
								//psService.updateProductStock("stock=(stock + " + sh.getStockoutCount() + "), lock_count=(lock_count - " + sh.getStockoutCount() + ")", "id=" + psSrc.getId());
								if(!psService.updateProductStockCount(psSrc.getId(), sh.getStockoutCount())){
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
								if(!psService.updateProductLockCount(psSrc.getId(), -sh.getStockoutCount())){
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
							}
							voProduct product2 = adminService.getProduct(sh.getProductId(), service.getDbOp());
							ProductStockBean psSrc2 = psService.getProductStock("product_id=" + product.getId() + " and area=" + bean.getStockArea() + " and type=" + bean.getStockType());
							ProductStockBean psTar2 = psService.getProductStock("product_id=" + product.getId() + " and area=" + area + " and type=" + bean.getStockType());
							String set = " remark = concat(remark, '(操作前源库库存"
								+ psSrc.getStock() + "源库锁定库存"
								+ psSrc.getLockCount() + ",操作后源库库存"
								+ psSrc2.getStock() + "源库锁定库存"
								+ psSrc2.getLockCount()
								+ ")'), deal_datetime = now()";
							service.updateOrderStockProduct(set, "id = " + sh.getId());
							set = " remark = concat(remark, '(操作前目的库库存"
								+ psTar.getStock() + "目的库锁定库存"
								+ psTar.getLockCount() + ",操作后目的库库存"
								+ psTar2.getStock() + "目的库锁定库存"
								+ psTar2.getLockCount()
								+ ")')";
							service.updateOrderStockProduct(set, "id = " + sh.getId());
						}
						// 库存不足，修改状态为处理中
						if (!stockEnough) {
							service.updateOrderStock("status = "
									+ OrderStockBean.STATUS1
									+ ", status_stock="
									+ OrderStockBean.STATUS1_NO_STOCK,
									"id = " + id);
						}

						service.updateOrderStock("stock_area = " + area, "id = " + id);
						//	                	if(bean.getStockArea() == ProductStockBean.AREA_BJ){
						//	                		service.getDbOp().executeUpdate("update user_order set deliver=7 where code='" + bean.getOrderCode() + "'");
						//	                	} else {
						//	                		service.getDbOp().executeUpdate("update user_order set deliver=8 where code='" + bean.getOrderCode() + "'");
						//		                }

						// log记录
						voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
						if (admin == null) {
							request.setAttribute("tip", "当前没有登录，添加失败！");
							request.setAttribute("result", "failure");
							return;
						}
						StockAdminHistoryBean log = new StockAdminHistoryBean();

						log.setAdminId(admin.getId());
						log.setAdminName(admin.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("订单出货操作：" + bean.getName() + "：修改出货地区");
						log.setType(StockAdminHistoryBean.CHANGE);
						service.addStockAdminHistory(log);
					} else if(bean.getStatus() == OrderStockBean.STATUS1) {
						String condition = "order_stock_id = " + id;
						ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");

						Iterator itr = null;
						OrderStockProductBean sh = null;

						itr = shList.iterator();
						String update = null;
						int stock = 0;
						while (itr.hasNext()) {
							sh = (OrderStockProductBean) itr.next();
							voProduct product = adminService.getProduct(sh.getProductId(), service.getDbOp());
							ProductStockBean psSrc = psService.getProductStock("product_id=" + product.getId() + " and area=" + bean.getStockArea() + " and type=" + bean.getStockType());
							ProductStockBean psTar = psService.getProductStock("product_id=" + product.getId() + " and area=" + area + " and type=" + bean.getStockType());
							service.updateOrderStockProduct("stockout_id = "
									+ psTar.getId() + ", stock_area=" + area, "id = "
									+ sh.getId());
						}

						service.updateOrderStock("stock_area = " + area, "id = " + id);
						//	                	if(bean.getStockArea() == ProductStockBean.AREA_BJ){
						//	                		service.getDbOp().executeUpdate("update user_order set deliver=7 where code='" + bean.getOrderCode() + "'");
						//	                	} else {
						//	                		service.getDbOp().executeUpdate("update user_order set deliver=8 where code='" + bean.getOrderCode() + "'");
						//		                }
					} else {
						request.setAttribute("tip", "库存操作失败，在该状态下不能修改订单发货地点！");
						request.setAttribute("result", "failure");
						return;
					}
				}

				//提交事务
				service.getDbOp().commitTransaction();
				request.setAttribute("back", back + "?id=" + id);
			} finally {
				service.releaseAll();
				adminService.close();
			}
		}
	}


	public void stockAdminHistory(HttpServletRequest request,
			HttpServletResponse response) {
		int operId = StringUtil.StringToId(request.getParameter("operId"));
		String logType = StringUtil.dealParam(request.getParameter("logType"));
		if(logType == null || logType.length() == 0){
			logType = String.valueOf(StockAdminHistoryBean.ORDER_STOCK);
		}
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, null);
		try {
			String condition = "id = " + operId;
			OrderStockBean bean = service.getOrderStock(condition);
			condition = "log_id = " + operId + " and log_type in (2,3,4,10)";
			List list = service.getStockAdminHistoryList(condition, 0, -1, "id");

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
	 * 创建日期：2009-8-22
	 * 
	 * 说明：订单出货，复核操作
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void checkOrderStock(HttpServletRequest request, HttpServletResponse response){
		synchronized(orderStockLock){
			int id = StringUtil.StringToId(request.getParameter("operId"));
			int status = StringUtil.toInt(request.getParameter("status"));

			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IBatchBarcodeService batchService = ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				OrderStockBean bean = service.getOrderStock("id = " + id);
				if(status == OrderStockBean.STATUS6){
					if(bean.getStatus() != OrderStockBean.STATUS2){
						request.setAttribute("tip", "订单出货状态不是代发货，不能申请复核！");
						request.setAttribute("result", "failure");
						return;
					} else {
						voOrder order = adminService.getOrder(bean.getOrderId());
						if(order.getDeliver() == 0 || bean.getProductType() == 0){
							request.setAttribute("tip", "订单还没有设置产品类型和快递公司，不能申请复核！");
							request.setAttribute("result", "failure");
							return;
						}
						//开始事务
						service.getDbOp().startTransaction();
						// 状态设置为 复核
						bean.setStatus(OrderStockBean.STATUS6);
						service.updateOrderStock("status=" + bean.getStatus(), "id=" + bean.getId());

						//锁定货位库存
						CargoInfoAreaBean outArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockArea());
						List outOrderProductList = service.getOrderStockProductList("order_stock_id = "+bean.getId(), -1, -1, "id asc");
						Iterator outIter = outOrderProductList.listIterator();
						while(outIter.hasNext()){
							OrderStockProductBean outOrderProduct = (OrderStockProductBean)outIter.next();
							voProduct product = adminService.getProduct(outOrderProduct.getProductId());
							product.setCargoPSList(cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId(), -1, -1, "cps.id asc"));
							if(outOrderProduct.getStockoutCount() > (product.getCargoStock(CargoInfoBean.STORE_TYPE0)+product.getCargoStock(CargoInfoBean.STORE_TYPE4))){
								request.setAttribute("tip", "订单"+order.getCode()+"商品"+product.getCode()+"散货区和混合区货位库存不足，无法导出");
								request.setAttribute("result", "failure");
								cargoService.getDbOp().rollbackTransaction();
								return;
							}

							List cpsOutList = cargoService.getCargoAndProductStockList(
									"ci.stock_type = 0 and ci.area_id = "+outArea.getId()+" and ci.store_type in ( "+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and cps.product_id = "+product.getId()+" and cps.stock_count > 0", -1, -1, "ci.whole_code asc");
							if(cpsOutList == null||cpsOutList.size() == 0){
								request.setAttribute("tip", "订单"+order.getCode()+"商品"+product.getCode()+"散货区货位库存不足，无法导出");
								request.setAttribute("result", "failure");
								cargoService.getDbOp().rollbackTransaction();
								return;
							}

							//更新货位库存锁定记录
							int totalCount = outOrderProduct.getStockoutCount();
							int index = 0;
							int stockOutCount = 0;
							do {
								CargoProductStockBean cps = (CargoProductStockBean)cpsOutList.get(index);
								if(totalCount>=cps.getStockCount()){
									stockOutCount = cps.getStockCount();
								}else{
									stockOutCount = totalCount;
								}
								totalCount -= cps.getStockCount();
								index++;

								//添加订单出库货位信息记录
								OrderStockProductCargoBean ospc = new OrderStockProductCargoBean();
								ospc.setOrderStockId(bean.getId());
								ospc.setOrderStockProductId(outOrderProduct.getId());
								ospc.setCount(stockOutCount);
								ospc.setCargoProductStockId(cps.getId());
								ospc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
								service.addOrderStockProductCargo(ospc);

								//更新货位库存
								if(!cargoService.updateCargoProductStockCount(cps.getId(), -stockOutCount)){
									request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								if(!cargoService.updateCargoProductStockLockCount(cps.getId(), stockOutCount)){
									request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}

							} while (totalCount>0&&index<cpsOutList.size());

						}

						// log记录
						voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
						if (admin == null) {
							request.setAttribute("tip", "当前没有登录，添加失败！");
							request.setAttribute("result", "failure");
							return;
						}

						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(admin.getId());
						log.setAdminName(admin.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("订单出货操作：" + bean.getName() + "：申请复核");
						log.setType(StockAdminHistoryBean.CHANGE);
						service.addStockAdminHistory(log);

						//添加客户信息
						OrderCustomerBean orderCustomerBean = new OrderCustomerBean();
						orderCustomerBean.setOrderCode(order.getCode());
						orderCustomerBean.setSerialNumber(1);
						orderCustomerBean.setStatus(OrderStockBean.STATUS6);
						orderCustomerBean.setName(order.getName());
						orderCustomerBean.setOrderDate(adultadmin.util.DateUtil.getNow());
						if(batchService.getOrderCustomerBean("order_code='"+order.getCode()+"'")!=null){
							batchService.deleteOrderCustomer("order_code='"+order.getCode()+"'");
						}
						batchService.addOrderCustomer(orderCustomerBean);
						service.getDbOp().commitTransaction();
					}
				} else if (status == OrderStockBean.STATUS3){
					//开始事务
					service.getDbOp().startTransaction();
					// 状态设置为 库存已出货
					bean.setStatus(OrderStockBean.STATUS3);
					service.updateOrderStock("status=" + bean.getStatus(), "id=" + bean.getId());

					// log记录
					voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
					if (admin == null) {
						request.setAttribute("tip", "当前没有登录，添加失败！");
						request.setAttribute("result", "failure");
						return;
					}
					StockAdminHistoryBean log = new StockAdminHistoryBean();

					log.setAdminId(admin.getId());
					log.setAdminName(admin.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("订单出货操作：" + bean.getName() + "：复核完确认出货");
					log.setType(StockAdminHistoryBean.CHANGE);
					service.addStockAdminHistory(log);
					service.getDbOp().commitTransaction();
				} else {
					request.setAttribute("tip", "订单出货状态不是复核，不能进行复核操作！");
					request.setAttribute("result", "failure");
					return;
				}
			} finally {
				service.releaseAll();
				adminService.close();
			}
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-8-22
	 * 
	 * 说明：订单出货，设置 结算状态：实物已退回、实物未退回<br/>
	 * 		如果 订单出货状态 为 status3(已发货)， 则可设置为 实物已退回、实物未退回
	 * 		如果 订单出货状态 为 status7(实物未退回)，则只能设置为 实物已退回
	 * 		如果 订单出货状态 为 status8 或者 status9，则不能修改状态。
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
//	public void returnOrderStock(HttpServletRequest request, HttpServletResponse response){
//		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
//		if (admin == null) {
//			request.setAttribute("tip", "当前没有登录，添加失败！");
//			request.setAttribute("result", "failure");
//			return;
//		}
//
//		synchronized(orderStockLock){
//			int id = StringUtil.StringToId(request.getParameter("operId"));
//			int status = StringUtil.toInt(request.getParameter("status"));
//
//			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
//			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
//			IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
//			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
//			try {
//				OrderStockBean bean = service.getOrderStock("id = " + id);
//				//开始事务
//				service.getDbOp().startTransaction();
//				if(status == OrderStockBean.STATUS8 && (bean.getStatus() == OrderStockBean.STATUS3 || bean.getStatus() == OrderStockBean.STATUS7)){
//					// 设置 订单出货状态 为 实物已经退回
//					bean.setStatus(OrderStockBean.STATUS8);
//					service.updateOrderStock("status=" + bean.getStatus(), "id=" + bean.getId());
//					// 设置 订单状态 为 已退回
//					service.getDbOp().executeUpdate("update user_order set status=11 where id=" + bean.getOrderId());
//
//					// 退回订单中的商品， 同时，添加进销存卡片“结算退货”
//					// 从新的订单出货 体系中 查找 出货记录
//					List operHisList = service.getOrderStockProductList("order_stock_id=" + bean.getId(), -1, -1, null);
//
//					Iterator itr = operHisList.iterator();
//					OrderStockProductBean stockHis = null;
//					voProduct product = null;
//					while (itr.hasNext()) {
//						stockHis = (OrderStockProductBean) itr.next();
//
//						product = adminService.getProduct(stockHis.getProductId());
//						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
//
//						// 需要计算一下 库存价格
//						voOrder order = adminService.getOrder("code='" + bean.getOrderCode() + "'");
//						float price5 = 0;
//						voOrderProduct orderProduct = adminService.getOrderProductSplit(order.getId(), product.getCode());
//						if(orderProduct == null){
//							orderProduct = adminService.getOrderPresentSplit(order.getId(), product.getCode());
//						}
//						if(orderProduct != null){
//							int totalCount = product.getStock(ProductStockBean.AREA_BJ) + product.getStock(ProductStockBean.AREA_GF) + product.getStock(ProductStockBean.AREA_GS) + product.getLockCount(ProductStockBean.AREA_BJ) + product.getLockCount(ProductStockBean.AREA_GF) + product.getLockCount(ProductStockBean.AREA_GS);
//							StockBatchLogBean batchLog = service.getStockBatchLog("code='"+order.getCode()+"' and product_id="+orderProduct.getProductId());
//							if(batchLog == null){
//								price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * (stockHis.stockoutCount))) / (totalCount + stockHis.getStockoutCount()) * 1000))/1000;
//							}
//						}
//
//						// 更新库存
//						ProductStockBean ps = psService.getProductStock("product_id=" + stockHis.getProductId() + " and area=" + bean.getStockArea() + " and type=" + ProductStockBean.STOCKTYPE_RETURN);
//						if(ps == null){
//							request.setAttribute("tip", "没有找到产品库存，操作失败！");
//							request.setAttribute("result", "failure");
//							return;
//						}
//						if(!psService.updateProductStockCount(ps.getId(), (stockHis.getStockoutCount()))){
//							request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
//							request.setAttribute("result", "failure");
//							return;
//						}
//
//						//获取相关批次信息，处理退货库批次
//						int stockinCount = stockHis.getStockoutCount();
//						int batchCount = 0;
//						float stockinPrice = 0;
//						List batchLogList = service.getStockBatchLogList("code='"+order.getCode()+"' and product_id="+stockHis.getProductId()+" and remark = '订单出货'", -1, -1, "id desc");
//						if(batchLogList==null||batchLogList.size()==0){
//
//							String code = "X"+DateUtil.getNow().substring(0,10).replace("-", "");
//							StockBatchBean newBatch;
//							newBatch = service.getStockBatch("code like '" + code + "%' and product_id="+stockHis.getProductId());
//							if(newBatch == null){
//								//当日第一份批次记录，编号最后三位 001
//								code += "001";
//							}else {
//								//获取当日计划编号最大值
//								newBatch = service.getStockBatch("code like '" + code + "%' and product_id="+stockHis.getProductId()+" order by id desc limit 1"); 
//								String _code = newBatch.getCode();
//								int number = Integer.parseInt(_code.substring(_code.length()-3));
//								number++;
//								code += String.format("%03d",new Object[]{new Integer(number)});
//							}
//							int batchid = service.getNumber("id", "stock_batch", "max", "id > 0") + 1;
//							newBatch = new StockBatchBean();
//							newBatch.setId(batchid);
//							newBatch.setCode(code);
//							newBatch.setProductId(stockHis.getProductId());
//							newBatch.setProductStockId(ps.getId());
//							newBatch.setStockArea(bean.getStockArea());
//							newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//							newBatch.setProductStockId(ps.getId());
//							newBatch.setCreateDateTime(DateUtil.getNow());
//							newBatch.setPrice(orderProduct.getPrice3());
//							newBatch.setBatchCount(stockinCount);
//
//							service.addStockBatch(newBatch);
//
//							//添加批次操作记录
//							batchid = service.getNumber("id", "stock_batch_log", "max", "id > 0") + 1;
//							StockBatchLogBean batchLog = new StockBatchLogBean();
//							batchLog.setId(batchid);
//							batchLog.setCode(bean.getOrderCode());
//							batchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//							batchLog.setStockArea(bean.getStockArea());
//							batchLog.setBatchCode(newBatch.getCode());
//							batchLog.setBatchCount(stockinCount);
//							batchLog.setBatchPrice(newBatch.getPrice());
//							batchLog.setProductId(newBatch.getProductId());
//							batchLog.setRemark("退货入库");
//							batchLog.setCreateDatetime(DateUtil.getNow());
//							batchLog.setUserId(admin.getId());
//							service.addStockBatchLog(batchLog);
//
//							stockinPrice = batchLog.getBatchCount()*batchLog.getBatchPrice();
//						}else{
//							Iterator batchIter = batchLogList.listIterator();
//							while(batchIter.hasNext()&&stockinCount>0){
//								StockBatchLogBean batchLog = (StockBatchLogBean)batchIter.next(); 
//								StockBatchBean batch = service.getStockBatch("code = '"+batchLog.getBatchCode()+"' and product_id="+batchLog.getProductId()+" and stock_type="+ProductStockBean.STOCKTYPE_RETURN+" and stock_area="+bean.getStockArea());
//								if(batch!=null){
//									if(stockinCount<=batchLog.getBatchCount()){
//										service.updateStockBatch("batch_count = batch_count+"+stockinCount, "id="+batch.getId());
//										batchCount = stockinCount;
//										stockinCount = 0;
//									}else{
//										service.updateStockBatch("batch_count = batch_count+"+batchLog.getBatchCount(), "id="+batch.getId());
//										stockinCount -= batchLog.getBatchCount();
//										batchCount = batchLog.getBatchCount();
//									}
//
//								}else{
//
//									StockBatchBean newBatch = new StockBatchBean();
//									int batchid = service.getNumber("id", "stock_batch", "max", "id > 0") + 1;
//									newBatch.setId(batchid);
//									newBatch.setCode(batchLog.getBatchCode());
//									newBatch.setProductId(stockHis.getProductId());
//									newBatch.setProductStockId(ps.getId());
//									newBatch.setStockArea(bean.getStockArea());
//									newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//									newBatch.setProductStockId(ps.getId());
//									newBatch.setCreateDateTime(DateUtil.getNow());
//									newBatch.setPrice(batchLog.getBatchPrice());
//
//									if(stockinCount<=batchLog.getBatchCount()){
//										newBatch.setBatchCount(stockinCount);
//										batchCount = stockinCount;
//										stockinCount = 0;
//									}else{
//										newBatch.setBatchCount(batchLog.getBatchCount());
//										stockinCount -= batchLog.getBatchCount();
//										batchCount = batchLog.getBatchCount();
//									}
//
//									service.addStockBatch(newBatch);
//								}
//
//								stockinPrice = stockinPrice + batchLog.getBatchPrice()*batchCount;
//
//								//添加批次操作记录
//								int batchid = service.getNumber("id", "stock_batch_log", "max", "id > 0") + 1;
//								StockBatchLogBean newBatchLog = new StockBatchLogBean();
//								newBatchLog.setId(batchid);
//								newBatchLog.setCode(bean.getOrderCode());
//								newBatchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//								newBatchLog.setStockArea(bean.getStockArea());
//								newBatchLog.setBatchCode(batchLog.getBatchCode());
//								newBatchLog.setBatchCount(batchCount);
//								newBatchLog.setBatchPrice(batchLog.getBatchPrice());
//								newBatchLog.setProductId(batchLog.getProductId());
//								newBatchLog.setRemark("退货入库");
//								newBatchLog.setCreateDatetime(DateUtil.getNow());
//								newBatchLog.setUserId(admin.getId());
//								service.addStockBatchLog(newBatchLog);
//
//							}
//						}
//						int totalCount = product.getStock(ProductStockBean.AREA_BJ) + product.getStock(ProductStockBean.AREA_GF) + product.getStock(ProductStockBean.AREA_GS) + product.getLockCount(ProductStockBean.AREA_BJ) + product.getLockCount(ProductStockBean.AREA_GF) + product.getLockCount(ProductStockBean.AREA_GS);
//						//	                	price5 = ((float)Math.round((product.getPrice5() * totalCount + stockinPrice)/(totalCount + stockHis.getStockoutCount()) * 1000))/1000;
//						price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * (stockHis.stockoutCount))) / (totalCount + stockHis.getStockoutCount()) * 1000))/1000;
//						service.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId());
//
//						// 审核通过，就加 进销存卡片
//						product.setPsList(psService.getProductStockList("product_id=" + stockHis.getProductId(), -1, -1, null));
//
//						// 入库卡片
//						StockCardBean sc = new StockCardBean();
//						sc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//						//sc.setCode(bean.getCode());
//						sc.setCode(order.getCode());
//						sc.setCreateDatetime(DateUtil.getNow());
//						sc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//						sc.setStockArea(bean.getStockArea());
//						sc.setProductId(stockHis.getProductId());
//						sc.setStockId(ps.getId());
//						sc.setStockInCount(stockHis.getStockoutCount());
//						//						sc.setStockInPriceSum(stockinPrice);
//						sc.setStockInPriceSum((new BigDecimal(stockHis.getStockoutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(orderProduct.getPrice3()))).doubleValue());
//						sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
//						sc.setStockAllArea(product.getStock(bean.getStockArea()) + product.getLockCount(bean.getStockArea()));
//						sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
//						sc.setAllStock(product.getStockAll() + product.getLockCountAll());
//						sc.setStockPrice(price5);
//						sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
//						psService.addStockCard(sc);
//					}
//
//
//					// log记录
//					StockAdminHistoryBean log = new StockAdminHistoryBean();
//
//					log.setAdminId(admin.getId());
//					log.setAdminName(admin.getUsername());
//					log.setLogId(bean.getId());
//					log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
//					log.setOperDatetime(DateUtil.getNow());
//					log.setRemark("订单出货操作：" + bean.getName() + "：实物已退回");
//					log.setType(StockAdminHistoryBean.CHANGE);
//					service.addStockAdminHistory(log);
//
//					// 修改 结算数据中的 订单出货状态
//					MailingBalanceBean mbb = baService.getMailingBalance("order_id=" + bean.getOrderId());
//					if(mbb != null){
//						baService.updateMailingBalance("stockout_status=" + bean.getStatus(), "id=" + mbb.getId());
//					}
//				} else if(status == OrderStockBean.STATUS7 && bean.getStatus() == OrderStockBean.STATUS3){
//					// 设置 订单出货状态 为 实物未退回
//					bean.setStatus(OrderStockBean.STATUS7);
//					service.updateOrderStock("status=" + bean.getStatus(), "id=" + bean.getId());
//
//					// log记录
//					StockAdminHistoryBean log = new StockAdminHistoryBean();
//
//					log.setAdminId(admin.getId());
//					log.setAdminName(admin.getUsername());
//					log.setLogId(bean.getId());
//					log.setLogType(StockAdminHistoryBean.ORDER_STOCK);
//					log.setOperDatetime(DateUtil.getNow());
//					log.setRemark("订单出货操作：" + bean.getName() + "：实物未退回");
//					log.setType(StockAdminHistoryBean.CHANGE);
//					service.addStockAdminHistory(log);
//
//					// 修改 结算数据中的 订单出货状态
//					MailingBalanceBean mbb = baService.getMailingBalance("order_id=" + bean.getOrderId());
//					if(mbb != null){
//						baService.updateMailingBalance("stockout_status=" + bean.getStatus(), "id=" + mbb.getId());
//					}
//				} else {
//					request.setAttribute("tip", "订单出货状态不对，不能进行该操作！");
//					request.setAttribute("result", "failure");
//					return;	            	
//				}
//				service.getDbOp().commitTransaction();
//			} finally {
//				service.releaseAll();
//				adminService.close();
//			}
//		}
//	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-8-24
	 * 
	 * 说明：复核 列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void checkOrderStockList(HttpServletRequest request, HttpServletResponse response) {
		int countPerPage = 10;
		String strArea = request.getParameter("area");
		int area = StringUtil.StringToId(strArea);
		String orderCode = StringUtil.dealParam(request.getParameter("orderCode"));
		String orderStockCode = StringUtil.dealParam(request.getParameter("orderStockCode"));
		String startDate = StringUtil.dealParam(request.getParameter("startDate"));
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));
		int allOrderStock = StringUtil.StringToId(request.getParameter("allOrderStock"));
		int deliver = StringUtil.StringToId(request.getParameter("deliver"));

		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
		try {
			StringBuffer buf = new StringBuffer();
			StringBuffer urlBuf = new StringBuffer();
			urlBuf.append("checkOrderStockList.jsp?1=1");
			buf.append(" status = ");
			buf.append(OrderStockBean.STATUS6);
			if(orderCode != null && orderCode.length() > 0){
				buf.append(" and order_code='");
				buf.append(orderCode);
				buf.append("' ");
				urlBuf.append("&orderCode=");
				urlBuf.append(orderCode);
			}
			if(orderStockCode != null && orderStockCode.length() > 0){
				buf.append(" and code='");
				buf.append(orderStockCode);
				buf.append("' ");
				urlBuf.append("&orderStockCode=");
				urlBuf.append(orderStockCode);
			}
			if (strArea != null && area >= 0) {
				buf.append(" and stock_area = ");
				buf.append(area);
				urlBuf.append("&area=");
				urlBuf.append(area);
			}
			if (startDate != null && startDate.length() > 0 && endDate != null && endDate.length() > 0) {
				buf.append(" and create_datetime >='");
				buf.append(startDate);
				buf.append("' and create_datetime <='");
				buf.append(endDate);
				buf.append(" 23:59:59");
				buf.append("' ");
				urlBuf.append("&startDate=");
				urlBuf.append(startDate);
				urlBuf.append("&endDate=");
				urlBuf.append(endDate);
			}
			if(deliver>0){
				buf.append(" and deliver = ");
				buf.append(deliver);
				urlBuf.append("&deliver=");
				urlBuf.append(deliver);
			}
			String condition = buf.toString();
			//总数
			int totalCount = service.getOrderStockCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = null;
			if(allOrderStock == 0){
				list = service.getOrderStockList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "status, id desc");
			} else {
				list = service.getOrderStockList(condition, -1, -1, "status, id desc");
			}
			paging.setPrefixUrl(urlBuf.toString());
			Iterator itr = list.iterator();
			OrderStockBean oper = null;
			List list1 = new ArrayList();
			List list2 = new ArrayList();
			List list3 = new ArrayList();
			List list4 = new ArrayList();
			List list5 = new ArrayList();
			List list6 = new ArrayList();
			List list7 = new ArrayList();
			while (itr.hasNext()) {
				oper = (OrderStockBean) itr.next();
				oper.setOrder(adminService.getOrder("code = '" + oper.getOrderCode() + "'"));
				if (oper.getStatus() == OrderStockBean.STATUS1) {
					List orderProductList = adminService.getOrderProductsSplit(oper.getOrder().getId());
					List orderPresentList = adminService.getOrderPresentsSplit(oper.getOrder().getId());
					orderProductList.addAll(orderPresentList);
					Iterator iter = orderProductList.listIterator();
					while(iter.hasNext()){
						voOrderProduct vop = (voOrderProduct)iter.next();
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
					}
					int ss = checkStock(orderProductList);
					if(ss == 0){
						//三地库存都充足
						oper.setRealStatusStock(OrderStockBean.STATUS1_STOCK); //绿色
						list1.add(oper);
					} else if(ss == 3) {
						oper.setRealStatusStock(OrderStockBean.STATUS1_NO_STOCK);
						list2.add(oper);
					} else if (checkStockInArea(orderProductList, oper.getStockArea())) {
						oper.setRealStatusStock(OrderStockBean.STATUS1_ONE_STOCK);
						list7.add(oper);
					} else if (!checkStockInArea(orderProductList, oper.getStockArea())) {
						oper.setRealStatusStock(OrderStockBean.STATUS1_OTHER_STOCK);
						list6.add(oper);
					}
					if(!checkStockInArea(orderProductList, oper.getStockArea())){
						if (oper.getStatusStock() == OrderStockBean.STATUS1_STOCK) {
							oper.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
							service.updateOrderStock("status_stock=" + OrderStockBean.STATUS1_NO_STOCK, "id=" + oper.getId());
						}
					}
				} else if (oper.getStatus() == OrderStockBean.STATUS2) {
					if (oper.getStatusStock() == OrderStockBean.STATUS2_FROM_NO_STOCK) {
						list3.add(oper);
					} else {
						list4.add(oper);
					}
				} else {
					list5.add(oper);
				}
			}
			list.clear();
			if(allOrderStock == 2){
				list.addAll(list2);
			} else {
				list.addAll(list1);
				list.addAll(list7);
				list.addAll(list6);
				list.addAll(list2);
				list.addAll(list3);
				list.addAll(list4);
				list.addAll(list5);
			}

			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		} finally {
			adminService.close();
			service.releaseAll();
		}
	}
	
	/**
	 * 
	 * 功能:抽离方法 记录删除出库日志
	 * <p>作者 李双 May 16, 2013 3:43:32 PM
	 * @param order
	 * @param admin
	 * @param status
	 * @param stockDeal
	 */
	public  static void addUserOrderlog(voOrder order,voUser admin ,int status, int stockDeal){
		StringBuilder logContent = new StringBuilder();
		logContent.append("[订单状态:");
		logContent.append(order.getStatus());
		logContent.append("->").append(status).append("]");
		logContent.append("[发货状态:");
		logContent.append(order.getStockoutDeal());
		logContent.append("->").append(stockDeal).append("] 删除出库记录");
		OrderAdminLogBean oalog = new OrderAdminLogBean();
		oalog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
		oalog.setUserId(admin.getId());
		oalog.setUsername(admin.getUsername());
		oalog.setOrderId(order.getId());
		oalog.setOrderCode(order.getCode());
		oalog.setCreateDatetime(DateUtil.getNow());
		oalog.setContent(logContent.toString());
		IAdminService adminServide = ServiceFactory.createAdminServiceLBJ();
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
		try{
			logService.addOrderAdminLog(oalog);
			adminServide.addOrderAdminStatusLog(logService, order.getStatus(), status, order.getStockoutDeal(), stockDeal, oalog);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			logService.releaseAll();
			adminServide.close();
		}	
	}
}
