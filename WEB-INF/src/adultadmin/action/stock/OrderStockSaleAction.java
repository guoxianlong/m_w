/*
 * Created on 2009-5-8
 *
 */
package adultadmin.action.stock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.hessian.ware.OrderStockService;
import mmb.stock.stat.SecondSortingSplitService;
import adultadmin.action.stock.CreateOrderUtil;
import mmb.stock.stat.SortingAbnormalBean;
import mmb.stock.stat.SortingAbnormalDisposeService;
import mmb.stock.stat.SortingAbnormalProductBean;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingBatchOrderProductBean;
import mmb.stock.stat.SortingInfoService;
import mmb.util.New;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.ProductPresentBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.UserOrderProductHistoryBean;
import adultadmin.bean.UserOrderProductSplitHistoryBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.afterSales.AfterSaleRefundOrderBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.order.UserOrderPackageTypeBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.IUserOrderService;
import adultadmin.util.Arith;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.SmsSender;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class OrderStockSaleAction {

	public static byte[] orderStockLock = new byte[0];
	public Log stockLog = LogFactory.getLog("stock.Log");
	public Log debugLog = LogFactory.getLog("debug.Log");
	public static String CHANGE_STOCK_ARER_TIME = "2012-05-20 00:00:00"; 
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
			dbOpSMS.init(DbOperation.DB_SMS);
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
			IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IUserOrderService userOrderService = ServiceFactory.createUserOrderService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			DbOperation dbOpSlave = new DbOperation();
			dbOpSlave.init(DbOperation.DB_SLAVE);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
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
				if(StringUtil.isEmpty(order.getName())){
					request.setAttribute("tip", "客户姓名为空，无法申请出库！");
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
				if(orderProductList.size()==0 && orderPresentList.size()==0){
					request.setAttribute("tip", "订单内没有商品，不能申请出货！");// 
					request.setAttribute("result", "failure");
					return;
				}
				
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
							tempVOP.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
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
							tempVOP.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						productList.add(vop);
					}
				}

				int orderStockArea = ProductStockBean.AREA_GF;
				orderStockArea=checkStock2(productList,order);

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
				bean.setProductCount(0);
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
				
				//文齐辉修改2011/05/27添加订单出库时自动分配订单的产品分类
				//只计算订单产品，不计赠品 ,如果分类==0
				int productType=getProductType(orderProductList, service.getDbOp());//订单产品分类
				bean.setProductType(productType);
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
				int deliver=0;
				deliver = getDeliver(order, bean, adminService.getDbOperation());
				//RPC获取快递公司
//				deliver = OrderStockService.getDeliver(order.getBuyMode(), order.getAddress(), bean.getStockArea());
//				if(deliver == 0){
//					debugLog.info("0 hessian failed :"+order.getCode());
//					deliver = getDeliver(order, bean, adminService.getDbOperation());
//				}
				adminService.modifyOrder("deliver="+deliver,"id="+order.getId());
				bean.setDeliver(deliver);
            	
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
				if(id > CreateOrderUtil.orderLimitCount){
					String strId = String.valueOf(id);
					newCode = strId.substring(strId.length()- CreateOrderUtil.orderCodeLength, strId.length());
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
            	int skuCount=getProductCount(order,adminService.getDbOperation());
            	if(!service.updateOrderStock("product_count="+skuCount, "id="+bean.getId())){
					request.setAttribute("tip", "商品数量修改失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
            	adminService.modifyOrder("product_count="+skuCount,"id="+order.getId());
            	
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
					vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
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
						this.logOrderLackStock(service.getDbOp(), order, sh.getProductId(), sh.getStockoutCount(), 3);
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
						this.logOrderLackStock(service.getDbOp(), order, sh.getProductId(), sh.getStockoutCount(), 3);
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					if (!psService.updateProductLockCount(sh.getStockoutId(),
							sh.getStockoutCount())) {
						this.logOrderLackStock(service.getDbOp(), order, sh.getProductId(), sh.getStockoutCount(), 3);
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					if (product.getParentId1() == 123 || product.getParentId1() == 143
							|| product.getParentId1() == 316 || product.getParentId1() == 317
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
							|| product.getParentId1() == 1385 || product.getParentId1() == 1425
							|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222) {
						psService.checkProductStatus(sh.getProductId(),user);
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

				//生成订单评论码
				boolean cuocc=service.createUserOrderCommentCode(order,service.getDbOp());
				if(cuocc==false){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "生成订单评论码失败!");
					request.setAttribute("result", "failure");
					return;
				}
				
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
				WareService.deleteSendMsgAndAutolibrary(order);
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
						WareService.addOrderAdminStatusLog(logService, -1, -1, order.getStockoutDeal(), stockoutStatus, adminLog);
						logService.addOrderAdminLog(adminLog);
					} catch(Exception e) {
						e.printStackTrace();
					} finally {
						logService.releaseAll();
					}
				}

				service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal="+stockoutStatus+" where id=" + order.getId());
				service.getDbOp().commitTransaction();
				//删除短信回复池里面数据 如果有的话
				service.getDbOp().executeUpdate("delete from order_dist_pool where id = "+order.getId());
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
				dbOpSlave.release();
			}
		}
	}

	
	/**
	 * 得到订单商品分类
	 * @return
	 */
	public int getProductType(List orderProductList,DbOperation dbOp){
		IUserOrderService userOrderService = ServiceFactory.createUserOrderService(IBaseService.CONN_IN_SERVICE, dbOp);
		Iterator tmpIter = orderProductList.iterator();
		float tmpPrice=0f;
		int orderPType=0;
		while(tmpIter.hasNext()){
			voOrderProduct vop = (voOrderProduct)tmpIter.next();
			UserOrderPackageTypeBean uopType = userOrderService.getUserOrderPackageType("product_catalog = "+vop.getParentId1());
			if(uopType!=null){
				float productPrice = vop.getCount()*vop.getDiscountPrice();
				if(productPrice>tmpPrice){
					tmpPrice = productPrice;
					orderPType = uopType.getTypeId();
				}
			}
		}
		return orderPType;
	}
	
	/**
	 * 得到订单商品数量
	 */
	public int getProductCount(voOrder order,DbOperation dbOp){
		String dmSql ="select id from product_ware_type where name ='印刷品'";
    	ResultSet rsDm=dbOp.executeQuery(dmSql);
    	String isDm = "";
    	int skuCount = 0;
    	try {
			if (rsDm.next()) {
				isDm = " AND (e.product_type_id<>"+rsDm.getInt("id") +" or e.product_type_id is null)";
			}
			rsDm.close();
			String skuCountSql ="SELECT COUNT(d.id) AS skuCount FROM user_order a " +
		            			"JOIN order_stock b ON a.id=b.order_id " +
		            			"JOIN order_stock_product c ON b.id=c.order_stock_id " +
		            			"JOIN product d ON c.product_id=d.id " +
	    	                    "LEFT JOIN product_ware_property e ON d.id=e.product_id " +
	    	                    "WHERE b.status<>3 AND a.id="+ order.getId()+isDm;
	    	ResultSet rsSkuCount=dbOp.executeQuery(skuCountSql);
	    	
	    	if (rsSkuCount.next()) {
	    		skuCount = rsSkuCount.getInt("skuCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return skuCount;
	}
	
	/**
	 * 分配快递公司
	 * @param order 订单
	 * @param bean 出库单
	 * @param dbOp 连接
	 * @return
	 */
	public int getDeliver(voOrder order,OrderStockBean orderStock,DbOperation dbOp){
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
		int deliver=0;
		int jdFr=67501;//京东订单友链ID
		int tbFr=67510;//淘宝订单友链ID
		int neFr=67509;//19e订单友链ID
		try{
			if(orderStock.getStockArea()==3){//增城分配快递公司
					//自动分配快递公司  只有货到付款的才自动分配快递公司 start
					//取消自动分配快递公司时是否货到付款的规则 2013-7-26
					//京东订单，淘宝订单，19e订单，当申请出库分配快递公司时，必须指定EMS来配送
					if(order.getFr()==jdFr||order.getFr()==tbFr||order.getFr()==neFr){
						if(order.getAddress().substring(0,4).contains("广东")){//广东省速递局
							deliver=11;
						}else{//广速省外
							deliver=9;
						}
					}else if(order.getAddress().substring(0,4).contains("福建省")){//通路速递
						deliver=14;
					}else if(order.getAddress().substring(0,4).contains("江苏")){//赛澳递江苏
						deliver=17;
					}else if(order.getAddress().substring(0,4).contains("上海")){//赛澳递上海
						deliver=18;
					}else if(order.getAddress().substring(0,4).contains("北京")
							||order.getAddress().substring(0,4).contains("天津")){//北京小红帽  2014-02-20
						deliver=41;
					}else if (order.getAddress().substring(0,4).contains("山东")
						&&!order.getAddress().contains("山东省烟台市长岛")){//山东海虹 2014-2-13
						deliver=42;
					}else if (order.getAddress().substring(0,4).contains("浙江")){//通路速递浙江，取消分配给如风达浙江2014-2-13
						deliver=20;
					}else if (order.getAddress().substring(0,4).contains("广东")){//银捷速递，通路速递广东
						//银捷速递已发货数量
						int yjCount= service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=21 and status<>3");
						if(yjCount<300){//银捷速递每天限量300(2014-02-24,liangkun)
							deliver=21;
						}
						if(deliver == 0){
							int emsCount= service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=11 and status<>3");
							if(emsCount<900){//ems省内每天限量900(2014-02-24,liangkun)
								deliver=11;
							}else{
								deliver=19;//通路速递广东
							}
						}
						if(deliver == 0){
							//广东省内EMS已发货数量
							int tlCount=service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=11 and status<>3");
							if(tlCount<400){//广东省内EMS每天限量400(2013-04-22,zhaolin)
								deliver=11;
							}
						}
					}else if(order.getAddress().substring(0,4).contains("广西")){//广西邮政
						deliver=25;
					}else if(order.getAddress().substring(0,4).contains("四川")){
						deliver=26;//宅急送四川，快优达暂停2014-1-6
					}else if(order.getAddress().substring(0,4).contains("重庆")){//宅急送重庆
						deliver=27;
					}else if(order.getAddress().substring(0,4).contains("江西")){//江西邮政
						deliver=28;
					}else if(order.getAddress().substring(0,4).contains("湖南")){//湖南邮政
						deliver=31;
					}else if(order.getAddress().substring(0,4).contains("湖北")){
						deliver=32;//湖北邮政，湖北星速取消，2014-2-11
					}else if(order.getAddress().substring(0,4).contains("河北")){//全通物流 2013-9-5取消吉林省
						deliver=33;
					}else if(order.getAddress().substring(0,4).contains("陕西")){//陕西邮政
						deliver=34;
					}else if(order.getAddress().substring(0,4).contains("贵州")){//贵州邮政
						deliver=35;
					}else if(order.getAddress().substring(0,4).contains("河南")){//河南大河速递
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
					if(deliver==0){//没有分配快递公司，广东省内分配通路速递广东，省外分配给EMS
						if(order.getAddress().substring(0,4).contains("广东")){//通路速递广东(2013-04-22 zhaolin EMS->通路)
							deliver=19;
						}else{//广速省外
							deliver=9;
						}
					}
			}else if(orderStock.getStockArea()==4){//无锡分配快递公司
					if(order.getFr()==jdFr||order.getFr()==tbFr||order.getFr()==neFr){//无锡邮政
						deliver=37;
					}else if(order.getAddress().substring(0,4).contains("北京")
							||order.getAddress().substring(0,4).contains("天津")){//北京小红帽  2014-02-20
						deliver=41;
					}else if (order.getAddress().substring(0,4).contains("山东")
						&&!order.getAddress().contains("山东省烟台市长岛")){//山东海虹 2014-2-13
						deliver=42;
					}else if(order.getAddress().substring(0,4).contains("江苏")){//赛澳递江苏
						deliver=17;
					}else if(order.getAddress().substring(0,4).contains("上海")){//赛澳递上海
						deliver=18;
					}else if(order.getAddress().substring(0,4).contains("浙江")){//通路速递浙江，取消分配给如风达浙江2014-2-13
						deliver=20;
					}else if(order.getAddress().substring(0,4).contains("河北")){//全通物流 2013-9-5取消吉林省
						deliver=33;
					}else if(order.getAddress().substring(0,4).contains("陕西")){//陕西邮政
						deliver=34;
					}else if(order.getAddress().substring(0,4).contains("河南")){//河南大河速递
						deliver=36;
					}else if(order.getAddress().substring(0,4).contains("辽宁")){//辽宁邮政 2014-2-24
						deliver=43;
					}else if(order.getAddress().substring(0,4).contains("黑龙江")
							||order.getAddress().substring(0,4).contains("吉林")){//辽宁邮政省外 2014-3-6
						deliver=44;
					}else{//非无锡配送范围，分配给上海无疆
						int shwjCount=service.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 00:00:00' and '" + DateUtil.getNow()+"' and deliver=29 and status<>3");
						if(shwjCount<800){//上海无疆限量800单，2014-2-27 
							deliver=29;
						}else{
							deliver=37;//无锡邮政
						}
					}
			}
		} catch (Exception e){
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		}
		return deliver;
	}
	
	public String addOrderStock1(int orderId) {
		voUser user = null;
		user=new voUser();
		user.setId(0);
		user.setUsername("自动申请出库");
		synchronized(orderStockLock){
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
			IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			DbOperation dbOpSlave = new DbOperation();
			dbOpSlave.init(DbOperation.DB_SLAVE);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
			try {
				
				//sale start
				voOrder order = adminService.getOrder("a.id = " + orderId);
				if (order == null) {
					adminService.close();
					return "failed";
				}
				if (order.getStatus() != 3) {
					adminService.close();
					return "failed";
				}
				if (order.getStockout() == 1) {
					adminService.close();
					return "failed";
				}
				if(StringUtil.isEmpty(order.getName())){
					adminService.close();
					return "failed";
				}

				List orderProductList = adminService.getOrderProducts(order.getId());
				List orderPresentList = adminService.getOrderPresents(order.getId());

				List productList = new ArrayList();

				Iterator productIter = orderProductList.listIterator();
				while(productIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)productIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						if(ppList == null || ppList.size() == 0){
							return "failed";
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
							tempVOP.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
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
							return "failed";
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
							tempVOP.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							productList.add(tempVOP);
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						productList.add(vop);
					}
				}
				
				OrderStockBean oper = service.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and order_code = '" + order.getCode() + "'");
				if (oper != null) {
					return "failed";
				}

				int orderStockArea = ProductStockBean.AREA_GF;
				orderStockArea=checkStock2(productList,order);  //可以改为接口

				if(orderStockArea==0){//缺货
					// 提示库存不足
					if(order.getStockoutDeal() == 7){
						service.getDbOp().executeUpdate("update user_order set stockout_deal=4 where id=" + order.getId());
						service.getDbOp().executeUpdate("INSERT INTO user_order_lack_deal(id,lack_datetime,stockout_deal) values("+order.getId()+",'"+DateUtil.getNow()+"',4) ON DUPLICATE KEY UPDATE lack_datetime='"+DateUtil.getNow()+"',stockout_deal = 4");
					}
				}

				String name = order.getCode() + "_" + DateUtil.getNow().substring(0, 10) + "_出货";
				service.getDbOp().startTransaction();

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
				
				OrderStockBean bean = new OrderStockBean();
				bean.setCreateDatetime(DateUtil.getNow());
				bean.setName(name);
				bean.setOrderCode("");
				bean.setRemark("");
				bean.setStatus(OrderStockBean.STATUS1);
				bean.setOrderCode(order.getCode());
				bean.setOrderId(order.getId());
//				bean.setCode(CodeUtil.getOrderStockCode());
				bean.setCode("CK" + sdf.format(cal.getTime()));//此处设定order_stock的初始编号
				bean.setStockArea(orderStockArea);
				bean.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
				bean.setProductCount(0);
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
				int productType=getProductType(orderProductList, service.getDbOp());//订单产品分类
				bean.setProductType(productType);
				bean.setLastOperTime(DateUtil.getNow());
				//检查订单中商品的库存情况
				if (checkStockInArea(orderProductList, bean.getStockArea())) {
					bean.setStatusStock(OrderStockBean.STATUS1_STOCK);
				} else {
					bean.setStatusStock(OrderStockBean.STATUS1_NO_STOCK);
				}
				bean.setCreateUserId(0);
				
				//根据地址分配快递公司
				int deliver=0;
				deliver = getDeliver(order, bean, adminService.getDbOperation());
				//RPC获取快递公司
//				deliver = OrderStockService.getDeliver(order.getBuyMode(), order.getAddress(), bean.getStockArea());
//				if(deliver == 0){
//					debugLog.info("1 hessian failed :"+order.getCode());
//					deliver = getDeliver(order, bean, adminService.getDbOperation());
//				}
				adminService.modifyOrder("deliver="+deliver,"id="+order.getId());
				bean.setDeliver(deliver);
				
				if (!service.addOrderStock(bean)) {
//					request.setAttribute("tip", "添加失败！");
					service.getDbOp().rollbackTransaction();
					return "failed";
				}
				int id = service.getDbOp().getLastInsertId();
				bean.setId(id);

				//此处修改order_stock.code
				String newCode = null;
				if(id > CreateOrderUtil.orderLimitCount){
					String strId = String.valueOf(id);
					newCode = strId.substring(strId.length()- CreateOrderUtil.orderCodeLength, strId.length());
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
									return "failed";
								}
							} else {
								//如果还没有这个商品，则添加
								if(!adminService.addOrderProductSplit(bean.getOrderId(), tempVOP.getCode(), tempVOP.getCount())){
//									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									service.getDbOp().rollbackTransaction();
									return "failed";
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
									return "failed";
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
								return "failed";
							}

						} else {
							if(!adminService.addOrderProductSplit(bean.getOrderId(), op.getCode(), op.getCount())){
//								request.setAttribute("tip", "数据库操作失败，请稍后再试！");
								service.getDbOp().rollbackTransaction();
								return "failed";
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
								return "failed";
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
									return "failed";
								}
							} else {
								if(!adminService.addOrderPresentSplit(bean.getOrderId(), tempVOP.getCode(), tempVOP.getCount())){
//									request.setAttribute("tip", "数据库操作失败，请稍后再试！");
									service.getDbOp().rollbackTransaction();
									return "failed";
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
									return "failed";
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
								return "failed";
							}
						} else {
							if(!adminService.addOrderPresentSplit(bean.getOrderId(), op.getCode(), op.getCount())){
								service.getDbOp().rollbackTransaction();
								return "failed";
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
								return "failed";
							}
						}
					}

				}
            	
            	//计算订单中的sku数量，印刷品除外
            	int skuCount=getProductCount(order,adminService.getDbOperation());
            	if(!service.updateOrderStock("product_count="+skuCount, "id="+bean.getId())){
//					request.setAttribute("tip", "商品数量修改失败！");
//					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return "failed";
				}
            	adminService.modifyOrder("product_count="+skuCount,"id="+order.getId());
            	
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
					return "failed";
				}
				orderProductList = adminService.getOrderProductsSplit(bean.getOrder().getId());
				Iterator iter = orderProductList.listIterator();
				while (iter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) iter.next();
					vop.setPsList(psService.getProductStockList("product_id="
							+ vop.getProductId(), -1, -1, null));
					vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
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
						this.logOrderLackStock(service.getDbOp(), order, sh.getProductId(), sh.getStockoutCount(), 3);
//						request.setAttribute("tip", product.getName()+ "的库存不足！");
						service.getDbOp().rollbackTransaction();
						return "failed";
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
						this.logOrderLackStock(service.getDbOp(), order, sh.getProductId(), sh.getStockoutCount(), 3);
//						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						service.getDbOp().rollbackTransaction();
						return "failed";
					}
					if (!psService.updateProductLockCount(sh.getStockoutId(),
							sh.getStockoutCount())) {
						this.logOrderLackStock(service.getDbOp(), order, sh.getProductId(), sh.getStockoutCount(), 3);
//						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						service.getDbOp().rollbackTransaction();
						return "failed";
					}
					if (product.getParentId1() == 123 || product.getParentId1() == 143
							|| product.getParentId1() == 316 || product.getParentId1() == 317
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
							|| product.getParentId1() == 1385 || product.getParentId1() == 1425
							|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222) {
						psService.checkProductStatus(sh.getProductId(),user);
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

				//生成订单评论码
				boolean cuocc=service.createUserOrderCommentCode(order,service.getDbOp());
				if(cuocc==false){
					service.getDbOp().rollbackTransaction();
//					request.setAttribute("tip", "生成订单评论码失败!");
//					request.setAttribute("result", "failure");
					return "failed";
				}
				
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
						WareService.addOrderAdminStatusLog(logService, -1, -1, order.getStockoutDeal(), stockoutStatus, adminLog);
						logService.addOrderAdminLog(adminLog);
					} catch(Exception e) {
						e.printStackTrace();
					} finally {
						logService.releaseAll();
					}
				}

				service.getDbOp().executeUpdate("update user_order_lack_deal set stockout_deal="+stockoutStatus+" where id=" + order.getId());
				service.getDbOp().commitTransaction();
//				if(request!=null){
//					request.setAttribute("soId", String.valueOf(id));
////					request.setAttribute("stockStatus", String.valueOf(stockStatus));
//				}
			} catch (Exception e){
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				adminService.close();
				dbOpSlave.release();
			}
		}
		
		return "success";
	}
	
	/**
	 * 按照地址和库存确认发货地区
	 * @param orderProductList
	 * @return 0代表缺货，3代表增城，4代表无锡
	 */
	public static int checkStock2(List orderProductList,voOrder order) {
		boolean zc = true;
		boolean wx = true;
		String address=order.getAddress();//订单地址
		int area=0;//属于哪个仓库的配送区域
		
		if(address.substring(0,4).contains("山东")
				||address.substring(0,4).contains("江苏")
				||address.substring(0,4).contains("上海")
				||address.substring(0,4).contains("安徽")
				||address.substring(0,4).contains("北京")
				||address.substring(0,4).contains("天津")
				||address.substring(0,4).contains("辽宁")
				||address.substring(0,4).contains("吉林")
				||address.substring(0,4).contains("黑龙江")
				||address.substring(0,4).contains("河北")
				||address.substring(0,4).contains("河南")
				||address.substring(0,4).contains("山西")
				||address.substring(0,4).contains("陕西")
				||address.substring(0,4).contains("内蒙古")
				||address.substring(0,4).contains("甘肃")
				||address.substring(0,4).contains("浙江")
				||address.substring(0,4).contains("新疆")
				||address.substring(0,4).contains("宁夏")
				||address.substring(0,4).contains("青海")){//无锡发货范围
			area=4;
		}else{//增城发货范围
			area=3;
		}
		
		if(area==4){//无锡发货范围
			Iterator itr = orderProductList.iterator();
			voOrderProduct op = null;
			while (itr.hasNext()) {
				op = (voOrderProduct) itr.next();
				if (op.getCargoStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {//判断货位是否足量
					wx = false;
				}else if (op.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {//如果库存是否足量 这个是多余的判断?
					wx = false;
				}
			}
			if(wx==false){//无锡缺货
				Iterator itr2 = orderProductList.iterator();
				voOrderProduct op2 = null;
				while (itr2.hasNext()) {
					op2 = (voOrderProduct) itr2.next();
					if (op2.getCargoStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op2.getCount()) {
						zc = false;
					}else if (op2.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op2.getCount()) {
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
				if (op.getCargoStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
					zc = false;
				}else if (op.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
					zc = false;
				} 
			}
			if(zc==false){//增城缺货
				Iterator itr2 = orderProductList.iterator();
				voOrderProduct op2 = null;
				while (itr2.hasNext()) {
					op2 = (voOrderProduct) itr2.next();
					if (op2.getCargoStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) < op2.getCount()) {
						wx = false;
					}else if (op2.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) < op2.getCount()) {
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
			if (op.getCargoStock(area, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				result = false;
				return result;
			}else if (op.getStock(area, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
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
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			try {
				OrderStockBean bean = service.getOrderStock("id = " + operId);
				voOrder order = adminService.getOrder(bean.getOrderId());
				boolean isSubOrder=false;
				if(order.getBuyMode()!=1 && !order.getCode().startsWith("S")){
					ResultSet res = service.getDbOp().executeQuery("select * from user_order_sub_list where child_id="+order.getId());
					if(res.next()){
						isSubOrder=true;
					}
				}
				if (bean.getStatus() == OrderStockBean.STATUS1){
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
						request.setAttribute("tip", "售后换货订单，不能删除订单发货单！");
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
						voProduct product = adminService.getProduct(sh.getProductId(), service.getDbOp());
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
											SortingBatchOrderProductBean sbop=secondSortingSplitService.getSortingBatchOrderProduct("sorting_batch_order_id="+orderBean.getId()+" and cargo_id="+ci.getId());
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

					// 删除发货单的时候，要特殊处理一下售后换货订单
					if((order.getBuyMode() == Constants.BUY_TYPE_NIFFER || order.getCode().startsWith("S")) && !group.isFlag(0)){

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
						request.setAttribute("tip", "售后换货订单，不能删除订单发货单！");
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
				
				WareService.deleteSendMsgAndAutolibrary(order);
			} catch(Exception e){
				e.printStackTrace();
			}finally {
				service.releaseAll();
				adminService.close();
			}
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
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
		try{
			logService.addOrderAdminLog(oalog);
			WareService.addOrderAdminStatusLog(logService, order.getStatus(), status, order.getStockoutDeal(), stockDeal, oalog);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			logService.releaseAll();
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
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SecondSortingSplitService secondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
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
					service.deleteAuditPackage("order_id="+bean.getOrderId());//删除核对包裹记录
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
								return false;
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
								return false;
							}
							saId = service.getDbOp().getLastInsertId();
						}
					}
					for(int i=0; i<shList.size(); i++){
						OrderStockProductBean sh = (OrderStockProductBean) shList.get(i);
						voProduct product = adminService.getProduct(sh.getProductId(), service.getDbOp());
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
								return false;
							}
							if(!psService.updateProductLockCount(sh.getStockoutId(), -sh.getStockoutCount())){
								return false;
							}
						}

						if(bean.getStatus() == OrderStockBean.STATUS6){//已申请复核
							//已申请复核，删除出库记录，解锁库存
							List list = service.getOrderStockProductCargoList("order_stock_product_id = "+sh.getId(), -1, -1, "id asc");
							if(list == null || list.size() == 0){
								return false;
							}
							for(int j=0;j<list.size();j++){
								OrderStockProductCargoBean ospc = (OrderStockProductCargoBean)list.get(j);
								if(orderBean==null|| orderBean.getStatus() != SortingBatchOrderBean.STATUS2 ) {//批次订单状态不是分拣中，可以直接还原库存
									if(!cargoService.updateCargoProductStockCount(ospc.getCargoProductStockId(), ospc.getCount())){
										return false;
									}
									if(!cargoService.updateCargoProductStockLockCount(ospc.getCargoProductStockId(), -ospc.getCount())){
										return false;
									}
								} else {//批次订单状态是分拣中，不还原库存，添加货位异常单
										//如果是pda分拣，将未分拣数量直接将锁定量还原，分拣数量记录在商品异常单中
										if (sbg!=null&&sbg.getSortingType()==1) {
											CargoInfoBean ci=cargoService.getCargoInfo("whole_code='"+ospc.getCargoWholeCode()+"'");//订单关联货位
											CargoInfoBean operCargo=cargoService.getCargoInfo("area_id="+bean.getStockArea()+" and stock_type=0 and store_type="+CargoInfoBean.STORE_TYPE5);
											SortingBatchOrderProductBean sbop=secondSortingSplitService.getSortingBatchOrderProduct("sorting_batch_order_id="+orderBean.getId()+" and cargo_id="+ci.getId());
											if (sbop == null) {
												return false;
											}
											String set = "remark = concat(remark , '(操作前库存" + ps.getStock()
													+ "锁定库存" + ps.getLockCount()
													+ ",操作后库存" + (ps.getStock() + (sbop.getCount()-sbop.getSortingCount()))
													+ "锁定库存" + (ps.getLockCount() - (sbop.getCount()-sbop.getSortingCount()))
													+ ")'), deal_datetime = now()";
							
											service.updateOrderStockProduct(set, "id = " + sh.getId());
											//psService.updateProductStock("stock=(stock + " + sh.getStockoutCount() + "), lock_count=(lock_count-" + sh.getStockoutCount() + ")", "id=" + ps.getId());
											if(!psService.updateProductStockCount(sh.getStockoutId(), sbop.getCount()-sbop.getSortingCount())){
												return false;
											}
											if(!psService.updateProductLockCount(sh.getStockoutId(), -(sbop.getCount()-sbop.getSortingCount()))){
												return false;
											}
											
											if(operCargo == null){//波次是PDA分拣且没有设置对应地区的作业货位
												return false;
											}
											if(!cargoService.updateCargoProductStockCount(ospc.getCargoProductStockId(), sbop.getCount()-sbop.getSortingCount())){
												return false;
											}
											if(!cargoService.updateCargoProductStockLockCount(ospc.getCargoProductStockId(), -(sbop.getCount()-sbop.getSortingCount()))){
												return false;
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
													return false;
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
												return false;
											}
										}
									
								}
							}
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
//						ass.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_换货删除, "order_code='" + asro.getAfterSaleOrderCode() + "'");
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
			 
				WareService.deleteSendMsgAndAutolibrary(isOlderOrder);
			} finally {
				if(!connOutSide){
					service.releaseAll();
				}
				adminService.close();
			}
			return true;
		}
	}

	public boolean logOrderLackStock(DbOperation dbOp,voOrder order,int productId, int count, int type){
		boolean result = false;
		
		try{
			dbOp.executeUpdate(
					"insert into "+DbOperation.SCHEMA_WARE+"user_order_lack_stock(order_id,order_code,product_id,count,create_datetime,type) "+
			        "values("+order.getId()+",'"+order.getCode()+"',"+productId+","+count+",'"+DateUtil.getNow()+"',"+type+")");
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		
		return result;
	}
	
		 
	/**
	 * 无锡，增城
	 */
	public static String[] splitStore ={"W","Z"};
	
	
	static String splitMsg="尊敬的客户，您的订单#因商品不在同一个仓库，系统已拆分成多个子订单分开向您发货，您无需承担额外运费，由此给您带来不便，敬请谅解！";

	/**
	 * 
	 * 功能:拆单发货。
	 * <p>作者 李双 May 13, 2013 2:44:01 PM
	 * @param request
	 * @param response
	 */
	public void addSplitDelivery(HttpServletRequest request, HttpServletResponse response) {
		voUser user =(voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，添加失败！");
			request.setAttribute("result", "failure");
			return;
		}
		 
		String orderCode=StringUtil.dealParam(request.getParameter("oldOrderCode"));
		if (StringUtil.convertNull(orderCode).equals("")) {
			request.setAttribute("tip", "请填写订单编号！");
			request.setAttribute("result", "failure");
			return;
		} 
		if(isSubOrder(orderCode,splitStore)){
			request.setAttribute("tip", "已经是子订单，不能再拆单！");
			request.setAttribute("result", "failure");
			return;
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init(DbOperation.DB_SLAVE);
		
		IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
		IUserOrderService userOrderService = ServiceFactory.createUserOrderService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
		
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, psService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOperation);
		try{
			voOrder order = adminService.getOrder("a.code='" + orderCode+"'");
			voOrderExtendInfo orderExtendInfo = adminService.getOrderExtendInfo(order.getId());
			List<voOrderProduct> orderProductList = adminService.getOrderProducts(order.getId());
			List<voOrderProduct> orderPresentList = adminService.getOrderPresents(order.getId());
			List<voOrderProduct> productList = new ArrayList<voOrderProduct>();
			List<voOrderProduct> orderPresentListBack = new ArrayList();//备份赠品列表用于 <10元子单分配
			List<voOrderProduct> orderPresentListBack2 = new ArrayList();//备份赠品列表用于 <10元子单找到商品后的分配
			orderPresentListBack.addAll(orderPresentList);
			orderPresentListBack2.addAll(orderPresentList);

			if(addStockToProductList(orderProductList, productList, adminService, ppService, psService, cargoService, request)==1){
				return;
			}
			if(addStockToProductList(orderPresentList, productList, adminService, ppService, psService, cargoService, request)==1){
				return;
			}
//			if(userOrderService.getUserOrderProductHistoryCount("order_id=" + order.getId()) == 0){//如果无商品 则进行商品、赠品的处理
//				userOrderService.logUserOrderProduct(order, UserOrderProductHistoryBean.TYPE_NORMAL);
//			}
			
			/**仓库：商品|赠品 */
			Map<String,List<voOrderProduct>> map  = New.map();
			map.put("WP", new ArrayList<voOrderProduct>());
			map.put("ZP", new ArrayList<voOrderProduct>());
			
			List<voOrderProduct> zcOrderProducts = new ArrayList<voOrderProduct>();
			List<voOrderProduct> wxOrderProducts = new ArrayList<voOrderProduct>();
			
			//商品拆单规则
			for(int i=0;i<orderProductList.size();i++){
				voOrderProduct pro =orderProductList.get(i);
				List<voOrderProduct> recordList = New.list();
				//按照拆单规则 归置商品
				List<voOrderProduct> list = getSplitRule(pro, adminService, orderPresentList,recordList);
				if(list==null){
					request.setAttribute("tip", "套装产品信息异常，拆单失败！");
					request.setAttribute("result", "failure");
					return;
				}
				//检查优先发货仓库位置 3 增城 4 无锡
				int area = checkStock2(list, order);
				if(list.size()>1){//商品数量大于1时 可能包含赠品，需要剔除掉赠品，有赠品列表添加
					if(recordList.size()>0){
						Set<Integer> set = changeVPListToSet(recordList);
						for(int jj= list.size()-1;jj>-1;jj--){
							int key = list.get(jj).getProductId();
							if(set.contains(key)){
								list.remove(jj);
								set.remove(key);//删除避免赠品和商品一样。 
							}
						}
					}
				}
				if(area==3){
					zcOrderProducts.addAll(list);
					map.get("ZP").addAll(recordList);
				}else if(area==4){
					wxOrderProducts.addAll(list);
					map.get("WP").addAll(recordList);
				}else if(area==0){
					request.setAttribute("tip", pro.getCode()+"商品不存合格库库存，拆单失败！");
					request.setAttribute("result", "failure");
					return;
				}
			}
			
			map.put("W", wxOrderProducts);
			map.put("Z", zcOrderProducts);
			
			//赠品拆单规则
			for(voOrderProduct vo :orderPresentList){
				List<voOrderProduct> list = New.list();
				list.add(vo);
				int area = checkStock2(list, order);
				if(area==3){
					map.get("ZP").add(vo);
				}else if(area==4){
					map.get("WP").add(vo);
				}else if(area==0){
					request.setAttribute("tip", vo.getCode()+"商品不存合格库库存，拆单失败！");
					request.setAttribute("result", "failure");
					return;
				}
			}
			float zcSumDprice = Math.round(getRealPrice(adminService, userOrderService, order, zcOrderProducts));
			float wxSumDprice = order.getDprice()-zcSumDprice;
			float[] price= {wxSumDprice,zcSumDprice}; 
			for(int i=0; i<price.length; i++){
				float p = price[i];
				if(p<10){
					int tenStock;//小于10元子单所在仓库
					if(i==0){
						tenStock = ProductStockBean.AREA_WX;
					}else{
						tenStock = ProductStockBean.AREA_ZC;
					}
					if(!isfindProductForTen(adminService, userOrderService
							, order, orderProductList
							, tenStock, wxOrderProducts
							, p
							, zcOrderProducts
							, orderPresentListBack
							, orderPresentListBack2
							,  wxSumDprice, zcSumDprice
							, map)){
						request.setAttribute("tip", "拆单后会出现订单小于10子订单，拆单失败！");
						request.setAttribute("result", "failure");
						return;
						
					}else{
						//重新计算各子单总价
						zcSumDprice = Math.round(OrderStockSaleAction.getRealPrice(adminService, userOrderService, order, map.get("Z")));
						wxSumDprice = order.getDprice()-zcSumDprice;
						price[0] = wxSumDprice;
						price[1] = zcSumDprice;
					}
				}	
			}
			
			StringBuilder codeBuilder =new StringBuilder();
			StringBuilder orderIds = new StringBuilder();
			synchronized (orderStockLock) {
				int status = adminService.getOrderStatus(order.getId());
				if(status == 10){//重复订单，不能拆单
					request.setAttribute("tip", "重复订单，不能拆单！");
					request.setAttribute("result", "failure");
					return;
				}
				adminService.startTransaction();
				for(String stockCode : splitStore ){
					voOrder subOrder = new voOrder();
					createOrder(order, subOrder, 0,order.getCode()+stockCode);
					boolean flag=adminService.addSubOrder(subOrder);
					
					codeBuilder.append(subOrder.getCode()).append(",");
					orderIds.append(subOrder.getId()).append(",");
					
					Map alreadyCopyProductIdMap = new HashMap();
					for(voOrderProduct vo: map.get(stockCode)){
						int productId = 0;
						if(vo.getPacageType() == 1){
							productId = vo.getParentId1();
						}else{
							productId = vo.getProductId();
						}
						if(alreadyCopyProductIdMap.get(productId)==null){//说明还没有copy
							adminService.copyUserOrderProductToSubOrder(subOrder.getId(), order.getId(),productId);//订单商品
							adminService.copyUserOrderPromotionToSubOrder(subOrder.getId(), order.getId(),productId);//订单活动信息
							alreadyCopyProductIdMap.put(productId, productId);
						}
					}
					for(voOrderProduct vo: map.get(stockCode+"P")){
						adminService.addOrderPresent(subOrder.getId(), vo.getCode(), vo.getCount());
					}
					
					voOrderExtendInfo extendInfo = createOrderExtendInfo(orderExtendInfo, subOrder); //扩展信息
					
					flag &=adminService.addOrderExtendInfo(extendInfo);
					
					StringBuilder insert = new StringBuilder("insert into user_order_sub_list values (");//拆分订单关联
					insert.append(order.getId()).append(",").append(subOrder.getId()).append(",'").append(subOrder.getCode()).append("')");
					adminService.getDbOperation().executeUpdate(insert.toString());
					
					if(!flag){
						request.setAttribute("tip", "数据库异常。子订单添加失败！");
						request.setAttribute("result", "failure");
						adminService.rollbackTransaction();
						return;
					}
				}
	
				orderIds.deleteCharAt(orderIds.length()-1);
				int index=0;
				for(String orderId : orderIds.toString().split(",")){
					adminService.modifyOrderPrice(StringUtil.parstInt(orderId), price[index], price[index++]);
				}
				adminService.modifyOrder(" status = 10 , stockout_deal=3 ", "id="+order.getId());
				adminService.commitTransaction();
			}
			
			copyUserOrderHistoryValues(orderIds.toString(), order, adminService, userOrderService, wxSumDprice, map);
			
			IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
        	try{
        		OrderAdminLogBean log = new OrderAdminLogBean();
        		log.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
        		log.setUserId(user.getId());
        		log.setUsername(user.getUsername());
        		log.setOrderId(order.getId());
        		log.setOrderCode(order.getCode());
        		log.setCreateDatetime(DateUtil.getNow());
        		log.setContent("订单成功拆分成子订单："+codeBuilder);
        		logService.addOrderAdminLog(log);
        	} catch(Exception e) {
        		e.printStackTrace();
        	} finally {
        		logService.releaseAll();
        	}
			
			codeBuilder.deleteCharAt(codeBuilder.length()-1);
			request.setAttribute("orderCodes", codeBuilder.toString());
			
			String msg = splitMsg.replaceAll("#", order.getCode());
			SmsSender.sendSMS(order.getPhone(),msg);
			
//			for(int i=0;i<zcHistoryBean.size();i++){
//				UserOrderProductHistoryBean bean = zcHistoryBean.get(i);
//				bean.setOrderId(subOrder1.getId());
//				if(userOrderService.addUserOrderProductHistory(bean)){
//					adminService.rollbackTransaction();
//	            	request.setAttribute("tip", "添加失败！");
//	            	request.setAttribute("result", "failure");
//	            	return;
//				}
//			}
//			for(int i=0;i<wxHistoryBean.size();i++){
//				UserOrderProductHistoryBean bean = wxHistoryBean.get(i);
//				bean.setOrderId(subOrder2.getId());
//				if(userOrderService.addUserOrderProductHistory(bean)){
//					adminService.rollbackTransaction();
//	            	request.setAttribute("tip", "添加失败！");
//	            	request.setAttribute("result", "failure");
//	            	return;
//				}
//			}
//			adminService.commitTransaction();
		 
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			adminService.close();
			dbOperation.release();
		}
	}
	/**
	 * 功能 ： 根据订单code判断订单是否已经是子订单
	 * @param orderCode 
	 * @return
	 */
	public static boolean isSubOrder(String orderCode,String[] splitStore){
		boolean flag = false;
		if(orderCode==null){
			return false;
		}
		for(String stockCode : splitStore ){
			String store = orderCode.substring(orderCode.length()-1);
			if(stockCode.equals(store)){
				return true;
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * @param adminService
	 * @param userOrderService
	 * @param order   原订单
	 * @param orderProductList 原订单商品 
	 * @param tenStock 小于10元单所在仓
	 * @param wxOrderProducts 无锡仓订单商品
	 * @param p	小于10元单的总价
	 * @param zcOrderProducts 增城仓订单商品
	 * @param orderPresentListBack 原订单赠品
	 * @param orderPresentListBack2 原订单赠品
	 * @param wxSumDprice 无锡仓订单总价
	 * @param zcSumDprice 增城仓订单总价
	 * @param map
	 * @return
	 */
	public static boolean isfindProductForTen(IAdminService adminService,IUserOrderService userOrderService
					,voOrder order,List orderProductList
					,int tenStock,List wxOrderProducts
					,float p
					,List zcOrderProducts
					,List orderPresentListBack
					,List orderPresentListBack2
					, float wxSumDprice,float zcSumDprice
					,Map map
					){
		
		//判断原单品数量cnt，如果cnt<3, 拆单失败
		if(orderProductList!=null&&orderProductList.size()<3){
			return false;
		}else{
			List subOrderProducts = null;//<10元子订单的所有拆分后的单品
			int currentStock = -1; //<10元子订单所在仓库
			int otherStock = -1;//除去<10元库以外的库
			float findP = 0.0f;//为<10元单找商品的最大价格
			float sumDprice = 0.0f;//为<10元单找商品的子单总价
			if(tenStock == ProductStockBean.AREA_WX){ //无锡子单<10
				subOrderProducts = wxOrderProducts;
				currentStock = ProductStockBean.AREA_WX;
				otherStock = ProductStockBean.AREA_ZC;
				findP = zcSumDprice - 10;
				sumDprice = zcSumDprice;
			}else{//增城子单<10
				subOrderProducts = zcOrderProducts;
				currentStock = ProductStockBean.AREA_ZC;
				otherStock = ProductStockBean.AREA_WX;
				findP = wxSumDprice - 10;
				sumDprice = wxSumDprice;
			}
			//获取其他（除去本拆单后的原品)原品
			List otherProductsList = getOtherProducts(orderProductList,subOrderProducts);
			if(otherProductsList.size()<=1){//如果其他子订单中的商品数量<=1,不可拆单
				return false;
			}else{
				//查出其他子订单中的原品在<10子单所在库有库存的原品
				List otherProductsHasStock = getOtherProductsHasStock( adminService,userOrderService,order,otherProductsList, orderPresentListBack , currentStock);
				if(otherProductsHasStock==null||otherProductsHasStock.size()==0){//说明另一个库没有可以调配的原品
					return false;
				}else{
					float needP = 10-p;
					//尝试获取一个或多个原品（需单独写个方法）
					List findProductList = findProductForTen(needP, findP, sumDprice, otherProductsHasStock);
					if(findProductList!=null&&findProductList.size()>0){//找到了
						//循环找到的原品，给<10元子单加上，把其他仓去掉
				
						//根据otherStock
						//根据原品找到商品及绑定的赠品，
						//分别把其他仓去掉商品和绑定的赠品
						//把<10元单加上商品和绑定的赠品
						
						for(int m=0; m<findProductList.size(); m++){
							List recordList = new ArrayList();//要调配的商品所绑定的赠品
							voOrderProduct findProduct = (voOrderProduct)findProductList.get(m);//要调配的商品
							List<voOrderProduct> list = getSplitRule(findProduct, adminService, orderPresentListBack2,recordList);
							if(list.size()>1){//商品数量大于1时 可能包含赠品，需要剔除掉赠品
								if(recordList.size()>0){
									Set<Integer> set = changeVPListToSet(recordList);
									for(int jj= list.size()-1;jj>-1;jj--){
										int key = list.get(jj).getProductId();
										if(set.contains(key)){
											list.remove(jj);
											set.remove(key);//删除避免赠品和商品一样。 
										}
									}
								}
							}
							List findOrderProductsList = null;//调配的子单中的商品
							List findOrderPresentsList = null;//调配的子单中的赠品
							
							List needOrderProductsList = null;//<10元子单中的商品
							List needOrderPresentsList = null;//<10元子单中的赠品
							if(otherStock==ProductStockBean.AREA_ZC){
								findOrderProductsList = (List)map.get("Z");
								findOrderPresentsList = (List)map.get("ZP");
								
								needOrderProductsList = (List)map.get("W");
								needOrderPresentsList = (List)map.get("WP");
							}else if(otherStock==ProductStockBean.AREA_WX){
								findOrderProductsList = (List)map.get("W");
								findOrderPresentsList = (List)map.get("WP");
								
								needOrderProductsList = (List)map.get("Z");
								needOrderPresentsList = (List)map.get("ZP");
							}
							//needOrderPrductsList加上调配的商品
							needOrderProductsList.addAll(list);
							needOrderPresentsList.addAll(recordList);
							//findOrderProductsList去掉调配的商品
							for(int t=findOrderProductsList.size()-1 ; t>=0; t--){
								voOrderProduct product = (voOrderProduct)findOrderProductsList.get(t);
								for(int tt = 0; tt<list.size(); tt++){
									voOrderProduct delProduct = (voOrderProduct)list.get(tt);
									if(delProduct.getProductId() == product.getProductId()){//商品相同
										if(delProduct.getCount() == product.getCount()){//数量相同
											findOrderProductsList.remove(t);//去掉
										}
									}
								}
							}
							
							//findOrderPresentsList去掉调配的赠品
							for(int t=findOrderPresentsList.size()-1 ; t>=0; t--){
								voOrderProduct product = (voOrderProduct)findOrderPresentsList.get(t);
								for(int tt = 0; tt<recordList.size(); tt++){
									voOrderProduct delProduct = (voOrderProduct)recordList.get(tt);
									if(delProduct.getProductId() == product.getProductId()){//商品相同
										if(delProduct.getCount() == product.getCount()){//数量相同
											findOrderPresentsList.remove(t);//去掉
										}
									}
								}
							}
							
							if(otherStock==ProductStockBean.AREA_ZC){
								map.put("Z", findOrderProductsList);
								map.put("ZP", findOrderPresentsList);
								
								map.put("W", needOrderProductsList);
								map.put("WP", needOrderPresentsList);
							}else if(otherStock==ProductStockBean.AREA_WX){
								map.put("W", findOrderProductsList);
								map.put("WP", findOrderPresentsList);
								
								map.put("Z", needOrderProductsList);
								map.put("ZP", needOrderPresentsList);
							}
						}
					}else{//没找到，不能拆单
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 尝试为<10元子订单获取一个或多个原品（需单独写个方法）
	 * @param needP  目标（即<10元单）子订单如果满足>10元需要的金额
	 * @param findP  查找子订单中可以分的商品的总价最大值
	 * @param sumDprice 查找子订单总价
	 * @param otherProductsHasStock 查找子订单中在目标子订单所在库可以发货的原品
	 * @return 返回找到的为<10元单分配的原品， 如果size=0说明没找到
	 */
	public static List findProductForTen(float needP,float findP,float sumDprice,List otherProductsHasStock){
 		List findProduct = new ArrayList();
		//float needP = 10-p;
		boolean isFind = false;
		//获取算法:首先获取 findP>原品总价格>(10-p) 的原品,从大到小找
		for(int n = otherProductsHasStock.size()-1; n >=0; n--){
			voOrderProduct ohterProduct = (voOrderProduct)otherProductsHasStock.get(n);
			if(ohterProduct.getPrice() < findP && ohterProduct.getPrice() > needP){//找到了
				findProduct.add(ohterProduct);
				isFind = true;
				break;
			}
		}
		if(!isFind){
			float priceTemp = 0.0f;//尝试分配的总价
			for(int k=0; k<otherProductsHasStock.size(); k++){
				voOrderProduct ohterProduct = (voOrderProduct)otherProductsHasStock.get(k);
				priceTemp += ohterProduct.getPrice();
				sumDprice -= ohterProduct.getPrice();
				if(sumDprice < 10){//说明没找到
					findProduct = new ArrayList();
					break;
				}else{
					findProduct.add(ohterProduct);
					if(priceTemp > needP){//如果分到的价格>needP了，说明找到了
						break;
					}
				}
			}
		}
		return findProduct;
	}
	
	/**
	 * 功能:查出其他子订单中的原品在<10子单所在库有库存的原品，用于往<10元子单中分
	 * 
	 * @param otherProducts 除去<10子单中原品后其他的原品
	 * @param currentStock <10子单所在库
	 * @return
	 */
	public static  List  getOtherProductsHasStock(IAdminService adminService,IUserOrderService userOrderService,voOrder order,List otherProductList,List orderPresentList ,int currentStock){
		List otherProductsHasStockList = new ArrayList();
		if(otherProductList == null ||otherProductList.size()<=0){
			return otherProductsHasStockList;
		}
		for(int i=0; i<otherProductList.size(); i++){//循环所有原品
			boolean hasStock = false;
			voOrderProduct ohterProduct = (voOrderProduct)otherProductList.get(i);
			List recordList = new ArrayList();
			List<voOrderProduct> list = getSplitRule(ohterProduct, adminService, orderPresentList,recordList);//获取该原品拆分后的子品，包括单品，绑定的赠品 或套装商品的拆开
			voOrderProduct op = null;
			for(int j=0; j<list.size(); j++){
				op = list.get(j);
				if (op.getStock(currentStock, ProductStockBean.STOCKTYPE_QUALIFIED) >= op.getCount()) {
					hasStock = true;
				}else{//无库存， 如果有一个子商品无库存，那么就不可以在该仓发货
					hasStock = false;
					break;
				}
			}
			if(hasStock){
				//去掉绑定的赠品后计算出该原品的总价
				//1:去掉绑定赠品
				if(list.size()>1){//商品数量大于1时 可能包含赠品，需要剔除掉赠品
					if(recordList.size()>0){
						Set<Integer> set = changeVPListToSet(recordList);
						for(int jj= list.size()-1;jj>-1;jj--){
							int key = list.get(jj).getProductId();
							if(set.contains(key)){
								list.remove(jj);
								set.remove(key);//删除避免赠品和商品一样。 
							}
						}
					}
				}
				//2:计算该原品总价
				float sumDprice = Math.round(getRealPrice(adminService, userOrderService, order, list)); 
				ohterProduct.setPrice(sumDprice);//暂时用该字段存放原品实际销售总价,应该不会影响其他功能
				otherProductsHasStockList.add(ohterProduct);
			}
		}
		List otherProductsHasStockListTemp = new ArrayList();//用于排序
		otherProductsHasStockListTemp.addAll(otherProductsHasStockList);
		int [] sortIndex = new int[otherProductsHasStockListTemp.size()];
		int sort = 0;
		//根据价格从小到大排序，方便分配
		for(int i=0; i<otherProductsHasStockListTemp.size(); i++){
			
			if(otherProductsHasStockListTemp.get(i)==null){
				continue;
			}
			int index = i;//作为最小索引
			voOrderProduct op = (voOrderProduct)otherProductsHasStockListTemp.get(i);//拿出一个作为比较对象,作为最小价格对象
			
			for(int j=0; j<otherProductsHasStockListTemp.size(); j++){
				if(otherProductsHasStockListTemp.get(j)==null){
					continue;
				}
				voOrderProduct opTemp = (voOrderProduct)otherProductsHasStockListTemp.get(j);
				if(op.getPrice()>=opTemp.getPrice()){
					op = opTemp;
					index = j;
				}
			}
			otherProductsHasStockListTemp.set(index, null);
			sortIndex[sort] = index;
			sort++;
			
			i=0;
		}
		
		for(int i=0;i<sortIndex.length;i++){
			otherProductsHasStockListTemp.set(i, otherProductsHasStockList.get(sortIndex[i]));
		}
//	3：然后找出这些其他所有单品中可以在该仓库发货的（写个方法）{
//		list otherProductList;
//
//		取得当前仓库
//		循环所有的单品
//		调用如下方法
//		List<voOrderProduct> list = getSplitRule(pro, adminService, orderPresentList,recordList);
//
//		list里面的数据是 （单品 及 绑定的赠品）  或 （套装拆分后的单品）
//
//		
//			判断当前商品list是否在本库存可以发货（有合格库存）
//			如果有 把当前单品 记录进otherProductList;
//		
//	}
		return otherProductsHasStockListTemp;
	}
	
	/**
	 * 功能：找出该小于10元子单中商品以外的所有单品
	 * 
	 * 作者:zhangjie
	 * 
	 * @param orderProductList 所有原品
	 * @param subOrderProducts 小于10元子单中的拆分后的商品
	 * @return
	 */
	public static List  getOtherProducts(List originalOrderProductList,List subOrderProducts){
		List otherProducts = new ArrayList();
		otherProducts.addAll(originalOrderProductList);
//		2：方法拆分 ，获取除 本商品外，其他在该发货区能发货的原单品{
//	
		for(int i=0; i<subOrderProducts.size(); i++){
			voOrderProduct subProduct = (voOrderProduct)subOrderProducts.get(i);
			int originalProductId;//记录<10元单中原品id,用来从所有原品中去除这些，来获得其他原品
			//判断是否是从套装拆过来 的
			int packageType = subProduct.getPacageType();
			if(packageType == 1){//套装拆过来的
				//找到原品
				originalProductId =  subProduct.getParentId1();
			}else{
				originalProductId = subProduct.getId();
			}
			//循环所有原品，去除<10元中原品
			for(int j = otherProducts.size()-1; j>=0;j--){
				voOrderProduct originalProduct = (voOrderProduct)otherProducts.get(j);
				if(originalProduct.getId() == originalProductId){
					otherProducts.remove(j);
				}
			}
			
		}
//		 	需要在 voOrderProduct 中记录一下 这个拆分后的所有商品是从套装过来的，还是单品 考虑增加一个属性
//	
//	
//			如果是从套装过来的，需要根据parent_id,在product_package查到[原单品]
//	
//			然后 从[所有原单品]中去掉 上面查询到的该单（小于10元）的所有单品
//			（orderProductList中去掉）
//			
//			然后找出这些其他所有单品中可以在该仓库发货的（写个方法）
//	
//				
//			
//		}
		return otherProducts;
	}	
	
	/**
	 * 
	 * 功能:将库存数量放入voOrderProduct
	 * <p>作者 李双 May 13, 2013 10:47:34 AM
	 * @param orderProductList
	 * @param productList
	 * @param adminService
	 * @param ppService
	 * @param psService
	 * @param request
	 * @return
	 */
	int addStockToProductList(List<voOrderProduct> orderProductList,List<voOrderProduct> productList ,IAdminService adminService 
			,IProductPackageService ppService ,IProductStockService psService, ICargoService cargoService, HttpServletRequest request){
		Iterator<voOrderProduct> productIter = orderProductList.listIterator();
		while(productIter.hasNext()){
			voOrderProduct vop = (voOrderProduct)productIter.next();
			vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
			vop.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
			productList.add(vop);
		
		}
		return 2;
	}
	
	
	/**
	 * 生成订单扩展信息对象
	 *@author 李宁
	 *@date 2013-5-10 下午1:35:22
	 * @param orderExtendInfo
	 * @param subOrder1
	 * @return
	 */
	private voOrderExtendInfo createOrderExtendInfo(voOrderExtendInfo orderExtendInfo, voOrder subOrder1) {
		voOrderExtendInfo extendInfo = new voOrderExtendInfo();
		extendInfo.setId(subOrder1.getId());
		extendInfo.setOrderCode(subOrder1.getCode());
		extendInfo.setOrderPrice(subOrder1.getDprice());
		extendInfo.setPayMode(subOrder1.getBuyMode());
		extendInfo.setAddId1(orderExtendInfo.getAddId1());
		extendInfo.setAddId2(orderExtendInfo.getAddId2());
		extendInfo.setAddId3(orderExtendInfo.getAddId3());
		extendInfo.setAddId4(orderExtendInfo.getAddId4());
		extendInfo.setAdd5(orderExtendInfo.getAdd5());
		return extendInfo;
	}

	/**
	 * 生成子订单
	 *@author 李宁
	 *@date 2013-5-10 下午1:34:04
	 * @param order
	 * @param subOrder1
	 * @param sumDprice
	 */
	private void createOrder(voOrder order, voOrder subOrder, float sumDprice,String code) {
		subOrder.setDprice(sumDprice);
		subOrder.setCode(code);
		subOrder.setName(order.getName());
		subOrder.setPhone(order.getPhone());
		subOrder.setAddress(order.getAddress());
		subOrder.setPostcode(order.getPostcode());
		subOrder.setRemark(order.getRemark());
		subOrder.setStatus(order.getStatus());
		subOrder.setUserId(order.getUserId());
		subOrder.setDiscount(order.getDiscount());
		subOrder.setBuyMode(order.getBuyMode());
		subOrder.setRemitType(order.getRemitType());
		subOrder.setDeliverType(order.getDeliverType());
		subOrder.setPrice(order.getPrice());
		subOrder.setPhone2(order.getPhone2());
		subOrder.setPrepayDeliver(order.getPrepayDeliver());
		subOrder.setOperator(order.getOperator());
		subOrder.setPostage(order.getPostage());
		subOrder.setAreano(order.getAreano());
		subOrder.setPrePayType(order.getPrePayType());
		subOrder.setIsOlduser(order.getIsOlduser());
		subOrder.setSuffix(order.getSuffix());
		subOrder.setFlat(order.getFlat());
		subOrder.setOriginOrderId(order.getOriginOrderId());
		subOrder.setNewOrderId(order.getNewOrderId());
		subOrder.setSellerId(order.getSellerId());
		subOrder.setConsigner(order.getConsigner());
		subOrder.setGender(order.getGender());
//			subOrder.setLastOperTime(order.getLastOperTime());
		subOrder.setFr(order.getFr());
		subOrder.setWebRemark(order.getWebRemark());
		subOrder.setEmail(order.getEmail());
		subOrder.setDealDetail(order.getDealDetail());
		subOrder.setCreateDatetime(order.getCreateDatetime());
		subOrder.setOrderType(order.getOrderType());
	}
	
	/**
	 * 封装所需要的数据
	 *@author 李宁
	 *@date 2013-5-10 下午1:34:21
	 * @param adminService
	 * @param userOrderService
	 * @param order
	 * @param zcProducts
	 * @param subOrderProducts
	 * @param historys
	 * @param sumDprice
	 * @return
	 */
	public static float getRealPrice(IAdminService adminService, IUserOrderService userOrderService, voOrder order, List<voOrderProduct> products) {
		float sumDprice =0f;
		for(int i=0;i<products.size();i++){
			voOrderProduct pro = products.get(i);
			if(pro.getPacageType() == 1){//套装拆分后的
				UserOrderProductSplitHistoryBean bean = userOrderService.getUserOrderProductSplitHistory("order_id=" + order.getId() + " and product_id=" + pro.getProductId());
				if(bean==null){
					sumDprice += adminService.getProduct(pro.getProductId()).getPrice()*pro.getCount();
				}else{
					sumDprice += Arith.mul(bean.getDprice(),bean.getCount());	
				}
			}else{
				UserOrderProductHistoryBean bean = userOrderService.getUserOrderProductHistory("order_id=" + order.getId() + " and product_id=" + pro.getProductId());
				if(bean==null){
					sumDprice += adminService.getProduct(pro.getProductId()).getPrice()*pro.getCount();
				}else{
					sumDprice += Arith.mul(bean.getDprice(),bean.getCount());	
				}
			}
		}
		return sumDprice;
	}
	
	
	/**
	 * 
	 * 功能:拆分时产品规则。。
	 * <p>作者 李双 Jun 6, 2013 5:59:41 PM
	 * @param pro
	 * @param service
	 * @param presentList
	 * @return
	 */
	public static List<voOrderProduct> getSplitRule(voOrderProduct pro,IAdminService service,List<voOrderProduct> presentList,List<voOrderProduct> recordList){
		List<voOrderProduct> list = New.list();
		voProduct vpt = service.getProduct(pro.getProductId());
		if(vpt.getHasPresent()==1 && presentList.size()>0){//查找绑定的赠品
			IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOperation());
    		Set<Integer> hasPresent = changePPListToSet( ppService.getProductPresentList("parent_id=" + vpt.getId(), -1, -1, null));
    		
    		for(int i=presentList.size()-1;i>-1;i--){
    			voOrderProduct vopp = presentList.get(i);
    			if(hasPresent.contains(vopp.getProductId())){
    				list.add(vopp);//拆单不分开发货.
    				presentList.remove(i);
    				if(recordList!=null) recordList.add(vopp);
    			}
    		}
		}
		if(vpt.getIsPackage()==1){//套装商品也需要绑定在一块  从一个地方出库
			IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOperation());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOperation());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOperation());
			
			List<ProductPackageBean> ppList = ppService.getProductPackageList("parent_id=" + vpt.getId(), -1, -1, null);
			if(ppList == null || ppList.size() == 0){//套装产品信息异常，无法申请出库
				return null;
			}
			Iterator<ProductPackageBean> ppIter = ppList.listIterator();
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = ppIter.next();
				voOrderProduct tempVOP = new voOrderProduct();
				tempVOP.setCount(pro.getCount() * ppBean.getProductCount());
				tempVOP.setProductId(ppBean.getProductId());
				tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
				tempVOP.setCargoPSList(cargoService.getCargoAndProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
				tempVOP.setPacageType(1);//套装拆分后的
				tempVOP.setParentId1(vpt.getId());//设置原品id
				list.add(tempVOP);
			}
		}else{
			list.add(pro);
		}
		
		return list;
	}
	
	
	static Set<Integer> changePPListToSet(List<ProductPresentBean> ppList){
		Set<Integer> set = new HashSet<Integer>();
		if(ppList ==null || ppList.size()==0) return set;
		for(ProductPresentBean bean :ppList){
			set.add( bean.getProductId());
		}
		return set;
	}
	public static Set<Integer> changeVPListToSet(List<voOrderProduct> ppList){
		Set<Integer> set = new HashSet<Integer>();
		if(ppList ==null || ppList.size()==0) return set;
		for(voOrderProduct bean :ppList){
			set.add( bean.getProductId());
		}
		return set;
	}
	
	/**
	 * 
	 * 功能:更加父订单实际销售金额 计算出子订单实际销售金额
	 * 算法： 原来商品的不变，最后一个商品(子订单商品)= 原订单金额-前面商品的实际销售总和
	 * <p>作者 李双 May 13, 2013 4:57:47 PM
	 * @param subOrders  0 无锡 1 增城
	 * @param order
	 * @param adminService
	 * @param userOrderService
	 * @param map w Z
	 */
	void copyUserOrderHistoryValues(String subOrders, voOrder order,IAdminService adminService,IUserOrderService userOrderService,
			float wxSumDprice , Map<String,List<voOrderProduct>> map){
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, userOrderService.getDbOp()); 
		String[] cods = subOrders.split(",");
		
		float sumPrice =0f , sumSplitPrice=0f;
		for(int i=0;i<splitStore.length;i++){
			List<voOrderProduct> list = map.get(splitStore[i]);
			Map alreadyCopyProductIdMap = new HashMap();
			for(int j=0;j<list.size();j++){
				voOrderProduct vo = list.get(j);
				int productId = 0;
				if(vo.getPacageType() == 1){
					productId = vo.getParentId1();
				}else{
					productId = vo.getProductId();
				}
				int newOrderId =StringUtil.parstInt(cods[i]); 
				if(alreadyCopyProductIdMap.get(productId)==null){//说明还没有copy
					UserOrderProductHistoryBean bean = userOrderService.getUserOrderProductHistory("order_id=" + order.getId() 
							+ " and product_id=" + productId);
					if(bean != null){
						bean.setId(0);
						bean.setOrderId(newOrderId);
						if(i==1 && j==list.size()-1){//最后一个商品的实际销售价
							bean.setDprice(Arith.div(Arith.sub(order.getDprice(), sumPrice), bean.getCount(),4));//zhangyanhui 2014.1.16 修改售价分摊公式 从bean.setDprice(Arith.sub(order.getDprice(), sumPrice)) 更改为bean.setDprice(Arith.div(Arith.sub(order.getDprice(), sumPrice), bean.getCount(),4));
						}else{
							sumPrice = Arith.add(sumPrice, bean.getDprice()*bean.getCount());//zhangyanhui 2014.1.16 修改售价分摊公式 从sumPrice = Arith.add(sumPrice, bean.getDprice()) 修改为sumPrice = Arith.add(sumPrice, bean.getDprice()*bean.getCount())
						}
						
						userOrderService.addUserOrderProductHistory(bean);
						alreadyCopyProductIdMap.put(productId, productId);
					}
				}
				
				//插入 user_order_split_history 表
				voProduct product = adminService.getProduct(vo.getProductId());
				int lastProductId=0;
				int lastProductCount=0;
				if(product.getIsPackage()==1){
					List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
						UserOrderProductSplitHistoryBean upshb= userOrderService.getUserOrderProductSplitHistory("order_id=" + order.getId() 
						+ " and product_id=" + tempProduct.getId());
						upshb.setId(0);
						upshb.setOrderId(newOrderId);
						if(i!=1 || j!=list.size()-1){
							sumSplitPrice+=upshb.getDprice()*upshb.getCount();//zhangyanhui 2014.1.16 修改售价分摊公式 从sumSplitPrice+=upshb.getDprice() 修改为sumSplitPrice+=upshb.getDprice()*upshb.getCount()
						}else{//zhangyanhui 记录 (i==1 && j==list.size()-1)条件下  产品的数量
							lastProductCount=upshb.getCount();
						}
						userOrderService.addUserOrderProductSplitHistory(upshb);
						lastProductId= tempProduct.getId();
					}
				}else{
					UserOrderProductSplitHistoryBean upshb= userOrderService.getUserOrderProductSplitHistory("order_id=" + order.getId() 
							+ " and product_id=" + product.getId());
					upshb.setId(0);
					upshb.setOrderId(newOrderId);
					if(i!=1 || j!=list.size()-1){
						sumSplitPrice+=upshb.getDprice()*upshb.getCount();//zhangyanhui 2014.1.16  修改售价分摊公式 从sumSplitPrice+=upshb.getDprice() 修改为sumSplitPrice+=upshb.getDprice()*upshb.getCount()
					}else{//zhangyanhui 记录 (i==1 && j==list.size()-1)条件下  产品的数量
						lastProductCount=upshb.getCount();
					}
					lastProductId=product.getId();
					userOrderService.addUserOrderProductSplitHistory(upshb);
				}
				if(i==1 && j==list.size()-1){
					float temp_dbprice=0f;
					temp_dbprice=Arith.div(Arith.sub(order.getDprice(),sumSplitPrice), lastProductCount,4);//zhangyanhui 2014.1.16  修改售价分摊公式 从Arith.sub(order.getDprice(),sumSplitPrice) 修改为temp_dbprice
					userOrderService.updateUserOrderProductSplitHistory("dprice="+temp_dbprice, "order_id=" + newOrderId 
							+ " and product_id=" + lastProductId);
				}
			}
			
			/*zhangyanhui 2014.1.26  增加赠品信息  *************************************************************************************/
			List<voOrderProduct> plist = map.get(splitStore[i]+"P");
			for(int j=0;j<plist.size();j++){
				voOrderProduct vo = plist.get(j);
				UserOrderProductHistoryBean pbean = userOrderService.getUserOrderPresentHistory("order_id=" + order.getId() 
						+ " and product_id=" + vo.getProductId());
				int newOrderId =StringUtil.parstInt(cods[i]); 
				if(pbean!=null){
					pbean.setId(0);
					pbean.setOrderId(newOrderId);
					userOrderService.addUserOrderPresentHistory(pbean);
				}
				//插入 user_order_present_split_history 表
				voProduct product = adminService.getProduct(vo.getProductId());
				if(product.getIsPackage()==1){//套装
					List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
						UserOrderProductSplitHistoryBean upshb= userOrderService.getUserOrderProductSplitHistory("order_id=" + order.getId() 
						+ " and product_id=" + tempProduct.getId());
						upshb.setId(0);
						upshb.setOrderId(newOrderId);
						userOrderService.addUserOrderProductSplitHistory(upshb);
					}
				}else{
					UserOrderProductSplitHistoryBean pupshb= userOrderService.getUserOrderPresentSplitHistory("order_id=" + order.getId() 
							+ " and product_id=" + product.getId());
					pupshb.setId(0);
					pupshb.setOrderId(newOrderId);
					userOrderService.addUserOrderPresentSplitHistory(pupshb);
				}
			}
			/******************************************************************************************************************/
		}
	}
}
