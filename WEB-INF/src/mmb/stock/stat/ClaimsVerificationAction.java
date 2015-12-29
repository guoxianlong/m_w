package mmb.stock.stat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.easyui.Json;
import mmb.rec.oper.bean.ProductSellPropertyBean;
import mmb.rec.oper.service.ConsignmentService;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.EasyuiPageBean;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.IMEI.IMEIUserOrderBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.ReturnsReasonBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.MyException;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
@Controller
@RequestMapping("/claims")
public class ClaimsVerificationAction extends DispatchAction{
	
	private static byte[] lock = {};

	
	
	/**
	 * 
	 * 查询理赔核销单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward toAddClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(746) ) {
			request.setAttribute("tip", "您没有添加理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		return new ActionForward("/admin/cargo/addClaimsVerification.jsp");
	}
	
	/**
	 * 
	 * 根据订单号获得订单商品信息
	 * @return
	 */
	public void getOrderInfoByCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		String orderCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("orderCode")));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null){
				response.getWriter().write("{status:'fail', tip:'没有登录不可查询!'}");
				return;
			}
			if( orderCode.equals("")) {
				response.getWriter().write("{status:'fail', tip:'传入的订单号为空！'}");
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
					response.getWriter().write("{status:'fail', tip:'没有根据包裹单号找到订单信息！'}");
					return;
				} else {
					orderStockBean = istockService.getOrderStock("order_id="+vorder.getId()+" and status!="+OrderStockBean.STATUS4);
					if( orderStockBean == null ) {
						response.getWriter().write("{status:'fail', tip:'没有找到出库单信息！'}");
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
						response.getWriter().write("{status:'fail', tip:'没有找到订单信息！'}");
						return;
					} else {
						orderStockBean = istockService.getOrderStock("order_id="+vorder.getId()+" and status!="+OrderStockBean.STATUS4);
					}
				}
			}
			
			
			
			if (vorder == null ) {
				response.getWriter().write("{status:'fail', tip:'没有找到完整订单信息!'}");
				return;
			} else {
				//List list = istockService.getOrderStockProductList("order_stock_id="+orderStockBean.getId(), -1, -1, "id asc");
				if(vorder.getStatus() != 11 ) {
					response.getWriter().write("{status:'fail', tip:'当前订单不是已退回状态，不能操作！'}");
					return;
				}
				//得到该订单的所有出库商品...
				List<OrderStockProductBean> list = claimsVerificationService.getAllProductsByOrder(vorder.getId());
				Map<Integer,OrderStockProductBean> map = claimsVerificationService.getMergMap(list);
				list =  claimsVerificationService.mergeOrderStockProducts(map);
				request.getSession().setAttribute("OrderStockProductsMap_" + vorder.getId(), map);
				if( list != null && list.size() != 0 ) {
					int x = list.size();
					String result = "{status:'success', order_id:'" + vorder.getId() + "', order_stock_id:'', productList:[";
					for( int i = 0; i < x; i++ ) {
						OrderStockProductBean ospBean = list.get(i);
						voProduct product = wareService.getProduct(ospBean.getProductId());
						if(i == x-1) {
							result += "{productId:'" + product.getId() + "',productCode:'"+ product.getCode()+"',oriName:'" + claimsVerificationService.changeStringForJson(product.getOriname())+ "',name:'" + claimsVerificationService.changeStringForJson(product.getName()) + "', count:'" + ospBean.getStockoutCount() + "'}";
						} else {
							result += "{productId:'" + product.getId() + "',productCode:'"+ product.getCode()+"',oriName:'" + claimsVerificationService.changeStringForJson(product.getOriname()) + "',name:'" + claimsVerificationService.changeStringForJson(product.getName()) + "', count:'" + ospBean.getStockoutCount() + "'},";
						}
					}
					result +="]}";
					response.getWriter().write(result);
					return;
				} else {
					response.getWriter().write("{status:'fail', tip:'没有找到订单商品信息!'}");
					return;
				}
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		
	}
	
	/**
	 * 根据 商品 条码 或者商品编号  得到商品Id
	 *
	 */
	public void getProductIdByCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		voProduct product = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			if( !productCode.equals("")) {
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode)+"'");
				if( bBean == null || bBean.getBarcode() == null ) {
					product = wareService.getProduct(StringUtil.toSql(productCode));
				} else {
					product = wareService.getProduct(bBean.getProductId());
				}
			}	
			
			if( product == null ) {
				response.getWriter().write("{status:'fail', tip:'根据编号或条码 没有找到对应商品信息！'}");
			} else {
				response.getWriter().write("{status:'success', productId:'" + product.getId() + "'}");
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 
	 * 核准验证 添加理赔核销单 商品
	 * @return
	 */
	public void checkClaimsVerificationProduct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		String[] productIds = request.getParameterValues("productIds");
		String[] currentProductIds = request.getParameterValues("currentProductIds");
		int orderId = StringUtil.parstInt(request.getParameter("orderId"));
		int orderStockId = StringUtil.parstInt(request.getParameter("orderStockId"));
		
		Map map = new HashMap();
		voOrder vorder = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null) {
				response.getWriter().write("{status:'fail', tip:'没有登录不可查询!'}");
				return;
			}
			if( productIds == null || productIds.length == 0) {
				response.getWriter().write("{status:'fail', tip:'没有选择任何商品！'}");
				return;
			}
			
			if( currentProductIds != null && currentProductIds.length > 0 ) {
				for ( int i = 0 ; i < currentProductIds.length; i++ ) {
					String temp = currentProductIds[i];
					String count = request.getParameter("current_count_" + currentProductIds[i]);
					map.put(currentProductIds[i], count);
					for( int j = 0; j < productIds.length; j ++ ) {
						if( temp.equals(productIds[j])) {
							response.getWriter().write("{status:'fail', tip:'勾选商品有已经添加过的！'}");
							return;
						} else {
							String count2 = request.getParameter("count_" + productIds[j]);
							map.put(productIds[j], count2);
						}
					}
				}
			} else {
				for(int i = 0; i < productIds.length; i++ ) {
					String count = request.getParameter("count_" + productIds[i]);
					map.put(productIds[i], count);
				}
			}
			
			if ( orderId != 0 ){
				vorder = wareService.getOrder("id="+orderId);
				if( vorder == null ) {
					response.getWriter().write("{status:'fail', tip:'没有找到订单信息！'}");
					return;
				}
				if(vorder.getStatus() != 11 ) {
					response.getWriter().write("{status:'fail', tip:'当前订单不是已退回状态，不能操作！'}");
					return;
				}
			}
			//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
			ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + "," + ClaimsVerificationBean.CLAIMS_COMPLETE + ")" );
			if( cvBean2 != null ) {
				response.getWriter().write("{status:'fail', tip:'当前订单已经有已提交或已审核的理赔核销单了！'}");
				return;
			}
			String packageCode = "";
			List auditPackageList = istockService
			.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
			if( auditPackageList == null || auditPackageList.size() == 0 ) {
				response.getWriter().write("{status:'fail', tip:'没有找到对应的包裹单信息！'}");
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
				response.getWriter().write("{status:'fail', tip:'该订单未入库，不能添加理赔单！'}");
				return;
			}
			String deliverCompany = "";
			if( voOrder.deliverMapAll.containsKey(""+vorder.getDeliver())) {
				deliverCompany = (String)voOrder.deliverMapAll.get(""+vorder.getDeliver());
			}
			
			String result = "{status:'success', currentOrderCode:'" + vorder.getCode() + "', packageCode:'" + packageCode + "', orderStockId:'', deliverCompany:'" + deliverCompany + "', currentOrderId:'" + vorder.getId() + "', productList:[";
			Iterator itr = map.keySet().iterator();
			int x = map.size();
			for( int i = 0;itr.hasNext(); i ++ ) {
				String tempId = (String)itr.next();
				int productId = StringUtil.parstInt((String)tempId);
				int count = StringUtil.parstInt((String)map.get(tempId));
				if( count <= 0 ) {
					response.getWriter().write("{status:'fail', tip:'传入商品数量有误！'}");
					return;
				}
				voProduct product = wareService.getProduct(productId);
				if( product == null ) {
					response.getWriter().write("{status:'fail', tip:'所传商品信息有误！'}");
					return;
				}
				//确认商品是否属于一个订单....  根据出库单来判断
				/*List<OrderStockProductBean> list = claimsVerificationService.getAllProductsByOrder(vorder.getId());
				Map<Integer,OrderStockProductBean> productsMap = claimsVerificationService.getMergMap(list);*/
				Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
				if( productsMap == null ) {
					response.getWriter().write("{status:'fail', tip:'订单信息有误，请重新扫描订单！'}");
					return;
				}
				if( !productsMap.containsKey(product.getId()) ) {
					response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "不属于这个订单！'}");
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
				
				if(i == x-1) {
					result += "{productId:'" + product.getId() + "',productCode:'"+ product.getCode()+"',oriName:'" + claimsVerificationService.changeStringForJson(product.getOriname()) + "',productLine:'" + productLine + "', count:'" + count + "'}";
				} else {
					result += "{productId:'" + product.getId() + "',productCode:'"+ product.getCode()+"',oriName:'" + claimsVerificationService.changeStringForJson(product.getOriname()) + "',productLine:'" + productLine + "', count:'" + count + "'},";
				}
					
			}
			result +="]}";
			response.getWriter().write(result);
			return;
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 
	 * 删除理赔核销单 商品
	 * @return
	 */
	public void deleteCurrentOrderProduct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		
		String deleteProductId = StringUtil.convertNull(request.getParameter("deleteProductId"));
		String[] currentProductIds = request.getParameterValues("currentProductIds");
		int orderId = StringUtil.parstInt(request.getParameter("orderId"));
		int orderStockId = StringUtil.parstInt(request.getParameter("orderStockId"));
		
		Map map = new HashMap();
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null) {
				response.getWriter().write("{status:'fail', tip:'没有登录不可查询!'}");
				return;
			}
			if( deleteProductId.equals("")) {
				response.getWriter().write("{status:'fail', tip:'所传删除商品id有误!'}");
				return;
			}
			
			if( currentProductIds != null && currentProductIds.length > 0 ) {
				for ( int i = 0 ; i < currentProductIds.length; i++ ) {
					String temp = currentProductIds[i];
					if( temp.equals(deleteProductId)) {
						
					} else {
						String count = request.getParameter("current_count_" + currentProductIds[i]);
						map.put(currentProductIds[i], count);
					}
				}
			} 
			
			if ( orderId != 0 ){
				vorder = wareService.getOrder("id="+orderId);
				if( vorder == null ) {
					response.getWriter().write("{status:'fail', tip:'没有找到订单信息！'}");
					return;
				}
				if(vorder.getStatus() != 11 ) {
					response.getWriter().write("{status:'fail', tip:'当前订单不是已退回状态，不能操作！'}");
					return;
				}
			} 
			
			String packageCode = "";
			List auditPackageList = istockService
			.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
			if( auditPackageList == null || auditPackageList.size() == 0 ) {
				response.getWriter().write("{status:'fail', tip:'没有找到对应的包裹单信息！'}");
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
				response.getWriter().write("{status:'fail', tip:'该订单未入库，不能添加理赔单！'}");
				return;
			}
			
			String deliverCompany = "";
			if( voOrder.deliverMapAll.containsKey(""+vorder.getDeliver())) {
				deliverCompany = (String)voOrder.deliverMapAll.get(""+vorder.getDeliver());
			}
			
			String result = "{status:'success', currentOrderCode:'" + vorder.getCode() + "', packageCode:'"+ packageCode+"', orderStockId:'',deliverCompany:'" + deliverCompany + "', currentOrderId:'" + vorder.getId() + "', productList:[";
			Iterator itr = map.keySet().iterator();
			int x = map.size();
			if( x == 0 ) {
				response.getWriter().write("{status:'clear'}");
				return;
			}
			for( int i = 0;itr.hasNext(); i ++ ) {
				String tempId = (String)itr.next();
				int productId = StringUtil.parstInt((String)tempId);
				int count = StringUtil.parstInt((String)map.get(tempId));
				if( count <= 0 ) {
					response.getWriter().write("{status:'fail', tip:'传入商品数量有误！'}");
					return;
				}
				voProduct product = wareService.getProduct(productId);
				if( product == null ) {
					response.getWriter().write("{status:'fail', tip:'所传商品信息有误！'}");
					return;
				}
				//确认商品是否属于一个订单....  根据出库单来判断
				/*List<OrderStockProductBean> list = claimsVerificationService.getAllProductsByOrder(vorder.getId());
				Map<Integer,OrderStockProductBean> productsMap = claimsVerificationService.getMergMap(list);*/
				Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
				if( productsMap == null ) {
					response.getWriter().write("{status:'fail', tip:'订单信息有误，请重新扫描订单！'}");
					return;
				}
				if( !productsMap.containsKey(product.getId()) ) {
					response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "不属于这个订单！'}");
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
				
				if(i == x-1) {
					result += "{productId:'" + product.getId() + "',productCode:'"+ product.getCode()+"',oriName:'" + claimsVerificationService.changeStringForJson(product.getOriname()) + "',productLine:'" + productLine + "', count:'" + count + "'}";
				} else {
					result += "{productId:'" + product.getId() + "',productCode:'"+ product.getCode()+"',oriName:'" + claimsVerificationService.changeStringForJson(product.getOriname()) + "',productLine:'" + productLine + "', count:'" + count + "'},";
				}
				
			}
			result +="]}";
			response.getWriter().write(result);
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
	public void addClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		int orderId = StringUtil.parstInt(request.getParameter("currentOrderId"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		int reasonType = ProductWarePropertyService.toInt(request.getParameter("reasonType"));
		String reasonRemark = StringUtil.convertNull(request.getParameter("reasonRemark"));
		String[] productIds = request.getParameterValues("currentProductIds");
		
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.STAT_SLAVE);
		ClaimsVerificationStatService claimsVerficationStatService = new ClaimsVerificationStatService(IBaseService.CONN_IN_SERVICE,dbop);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				statService.getDbOp().startTransaction();
				// 检验 订单是否存在 
				// 检验 订单 是否有 退货入库记录
				voUser user = (voUser)request.getSession().getAttribute("userView");
				if(user == null) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'没有登录不可添加!'}");
					return;
				}
				UserGroupBean group = user.getGroup();
				if( !group.isFlag(746) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'您没有添加理赔核销权限!'}");
					return;
				}
				if( wareArea == -1 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'未选择库地区!'}");
					return;
				}
				if(!CargoDeptAreaService. hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN)){
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'你没有对应地区的退货库操作权限!'}");
					return;
				}
				if( productIds == null || productIds.length == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'没有添加任何商品!'}");
					return;
				}
				
				if ( orderId != 0 ){
					vorder = wareService.getOrder("id="+orderId);
					if( vorder == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'没有找到订单信息！'}");
						return;
					}
				}
				
				if(vorder.getStatus() != 11 ) {
					response.getWriter().write("{status:'fail', tip:'当前订单不是已退回状态，不能操作！'}");
					return;
				}
				Map<String,Float> priceMap = claimsVerificationService.calculateClaimsPrice(vorder.getId(),claimsVerficationStatService);
				//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
				ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + "," + ClaimsVerificationBean.CLAIMS_COMPLETE + ")" );
				if( cvBean2 != null ) {
					response.getWriter().write("{status:'fail', tip:'当前订单已经有已提交或已审核的理赔核销单了！'}");
					return;
				}
				
				String packageCode = "";
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'没有找到对应的包裹单信息！'}");
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
					response.getWriter().write("{status:'fail', tip:'该订单未入库，不能添加理赔单！'}");
					return;
				}
				ReturnedPackageBean rpBean2 = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "' and area="+wareArea);
				if( rpBean2 == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'该订单入的不是所选库地区，不能添加理赔单！'}");
					return;
				}
				if( reasonType == -1 ) {
					reasonType = 11;
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
				cvBean.setPrice(0);
				cvBean.setIsTicket(ClaimsVerificationBean.TICKET_HAS);
				cvBean.setReasonType(reasonType);
				cvBean.setReasonRemark(reasonRemark);
				if( !claimsVerificationService.addClaimsVerification(cvBean) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'添加理赔单时，数据库操作失败！'}");
					return;
				}
				
				int id = statService.getDbOp().getLastInsertId();
				
				if (!packageLog.addReturnPackageLog("添加理赔单", user, vorder.getCode())) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'添加退货日志失败！'}");
					return;
				}
				
				int x = productIds.length;
				boolean allPrice = false;
				boolean threeMailPrice = false;
				boolean skuPrice = false;
				boolean packagePrice = false;
				boolean hasGift = false;
				for(int i = 0; i < x; i++ ) {
					String productId = productIds[i];
					int count = StringUtil.parstInt(request.getParameter("current_count_"+productId));
					int exist = ProductWarePropertyService.toInt(request.getParameter("exist_" + productId));
					int claimsType = ProductWarePropertyService.toInt(request.getParameter("claims_type_" + productId));
					if( claimsType == 0 ) {
						allPrice = true;
						if( priceMap.containsKey("SKU_"+productId) ) {
							float price = priceMap.get("SKU_"+productId);
							if( Float.compare(price, 0.00f) == 0 ) {
								hasGift = true;
							}
						}
					} else if ( claimsType == 1 ) {
						skuPrice = true;
						if( priceMap.containsKey("SKU_"+productId) ) {
							float price = priceMap.get("SKU_"+productId);
							if( Float.compare(price, 0.00f) == 0 ) {
								hasGift = true;
							}
						}
					} else if ( claimsType == 2 ) {
						threeMailPrice = true;
					}  else if ( claimsType == 3 ) {
						packagePrice = true;
					}
					int iProductId = StringUtil.parstInt(productId);
					voProduct product = wareService.getProduct(iProductId);
					if( product == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'商品信息有误！'}");
						return;
					}
					// 检验 商品是否 属于订单
					Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
					if( productsMap == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'订单信息有误，请重新扫描订单！'}");
						return;
					}
					if( !productsMap.containsKey(product.getId()) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "不属于这个订单！'}");
						return;
					}
					OrderStockProductBean ospBean = productsMap.get(product.getId());
					if( count > ospBean.getStockoutCount() ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "数量大于了订单中的量！'}");
						return;
					}
					
					if( exist == -1 ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "的有无参数有错误！'}");
						return;
					}
					
					if( exist == 0) {
						// 无实物的处理
						//库存
						boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
						if( !stockCheck ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，添加理赔单失败！'}");
							return;
						}
						//货位库存
						boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
						if( !cargoStockCheck ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，添加理赔单失败！'}");
							return;
						}
					} else if ( exist == 1) {
						
					}
					ClaimsVerificationProductBean cvpBean = new ClaimsVerificationProductBean();
					cvpBean.setCount(count);
					cvpBean.setExist(exist);
					cvpBean.setClaimsVerificationId(id);
					cvpBean.setProductCode(product.getCode());
					cvpBean.setClaimsType(claimsType);
					if( !claimsVerificationService.addClaimsVerificationProduct(cvpBean)) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品加入理赔单时，数据库操作失败！'}");
						return;
					}
				}
				//修改理赔核销单的类型 hp
				int type=1;
				if(allPrice && !threeMailPrice && !packagePrice && !skuPrice){
					type=0;
				} else if(skuPrice && !threeMailPrice && !packagePrice && !allPrice){
					type=1;
				} else if(threeMailPrice && !skuPrice && !packagePrice && !allPrice){
					type=2;
				}else if(packagePrice && !skuPrice && !threeMailPrice && !allPrice){
					type=3;
				}else if(packagePrice && skuPrice && !threeMailPrice && !allPrice ){
					type=4;
				}else{
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'该理赔核销单组合不被允许！'}");
					return;
				}
				if( hasGift ) {
					if(!claimsVerificationService.updateClaimsVerification(" type="+type+", has_gift="+ClaimsVerificationBean.HAS_GIFT_YES, "id="+id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'数据库操作失败！'}");
						return;
					}
				}
				if( (allPrice && (threeMailPrice || skuPrice || packagePrice )) || (threeMailPrice && (allPrice || skuPrice || packagePrice)) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'理赔方式在整单理赔和3倍运费理赔所有商品选择要一致！'}");
					return;
				}
				// 检验商品 在退货库 数量 是否有 （在有实物的时候）
				// 检验
				
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
				request.getSession().setAttribute("OrderStockProductsMap_"+vorder.getId(), null);
				response.getWriter().write("{status:'success', tip:'添加理赔单成功！', url:'" + request.getContextPath() + "/admin/claimsVerificationAction.do?method=getClaimsVerificationInfo'}");
				return;
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
			claimsVerficationStatService.releaseAll();
		}
		
	}
	
	/**
	 * 
	 * 查询理赔核销单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward getClaimsVerificationInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(745) ) {
			request.setAttribute("tip", "您没有理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String claimsCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("claimsCode")));
		String createTime1 = StringUtil.toSql(StringUtil.convertNull(request.getParameter("createTime1")));
		String createTime2 = StringUtil.toSql(StringUtil.convertNull(request.getParameter("createTime2")));
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		String orderCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("orderCode")));
		String packageCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("packageCode")));
		int deliver = ProductWarePropertyService.toInt(request.getParameter("deliver"));
		int status = ProductWarePropertyService.toInt(request.getParameter("status"));
		int hasGift = ProductWarePropertyService.toInt(request.getParameter("hasGift"));
		int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
		String bsCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("bsCode")));
		
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		int countPerPage = 20;
		List claimsVerificationList = new ArrayList();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationProduct claimsVerificationProduct = new ClaimsVerificationProduct(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			PagingBean paging = null;
			
			if( productCode != null && !productCode.equals("")) {
				sql.append("select cv.* from claims_verification cv, claims_verification_product cvp where cvp.claims_verification_id = cv.id");
				sqlCount.append("select count(cv.id) from claims_verification cv, claims_verification_product cvp where cvp.claims_verification_id = cv.id");
			} else {
				sql.append("select cv.* from claims_verification cv where cv.id <> 0");//SELECT * FROM claims_verification cv LEFT JOIN claims_verification_product cvp ON cv.id=cvp.claims_verification_id where cv.id <> 0
				sqlCount.append("select count(cv.id) from claims_verification cv where cv.id <> 0");
			}
			
			if( !claimsCode.equals("") ) {
				params.append("&");
				params.append("claimsCode=" + claimsCode);
				
			    sql.append(" and cv.code = '" + claimsCode + "'");
			    sqlCount.append(" and cv.code = '" + claimsCode + "'");
			}
			
			if( !bsCode.equals("") ) {
				params.append("&");
				params.append("bsCode=" + bsCode);
				
				sql.append(" and cv.bsby_codes like '%" + bsCode + "%'");
				sqlCount.append(" and cv.bsby_codes like '%" + bsCode + "%'");
			}
			
			if( wareArea != -1 ) {
				params.append("&");
				params.append("wareArea=" + wareArea);
				
			    sql.append(" and cv.ware_area = '" + wareArea + "'");
			    sqlCount.append(" and cv.ware_area = '" + wareArea + "'");
			}
			
			if( !orderCode.equals("") ) {
				params.append("&");
				params.append("orderCode=" + orderCode);
				
				sql.append(" and cv.order_code = '" + orderCode + "'");
				sqlCount.append(" and cv.order_code = '" + orderCode + "'");
			}
			
			if( !packageCode.equals("") ) {
				params.append("&");
				params.append("packageCode=" + packageCode);
				
				sql.append(" and cv.package_code = '" + packageCode + "'");
				sqlCount.append(" and cv.package_code = '" + packageCode + "'");
			}
			
			if( !createTime1.equals("") && !createTime2.equals("")) {
				params.append("&");
				params.append("createTime1=" + createTime1);
				params.append("&");
				params.append("createTime2=" + createTime2);
				
				String createTimeStart = createTime1 + " 00:00:00";
				String createTimeEnd = createTime2 + " 23:59:59";
	        	sql.append(" and").append(" cv.create_time between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
	        	sqlCount.append(" and").append(" cv.create_time between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
			}
 			
			if( deliver != -1 ) {
				params.append("&");
				params.append("deliver=" + deliver);
				
				sql.append(" and cv.deliver = " + deliver);
				sqlCount.append(" and cv.deliver = " + deliver);
			}
			
			if( status != -1 ) {
				params.append("&");
				params.append("status=" + status);
				
				sql.append(" and cv.status = " + status);
				sqlCount.append(" and cv.status = " + status);
			}
			if( hasGift != -1 ) {
				params.append("&");
				params.append("has_gift=" + hasGift);
				
				sql.append(" and cv.has_gift = " + hasGift);
				sqlCount.append(" and cv.has_gift = " + hasGift);
			}
			
			if ( !productCode.equals("")) {
				params.append("&");
				params.append("productCode=" + productCode);
				
				sql.append(" and cvp.product_code = '" + productCode + "'");
				sqlCount.append(" and cvp.product_code = '" + productCode + "'");
			}
			
			int totalCount = claimsVerificationService.getClaimsVerificationCount2(sqlCount.toString());
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			claimsVerificationList = claimsVerificationService.getClaimsVerificationList2(sql.toString(), paging.getCurrentPageIndex()*countPerPage, countPerPage, "cv.create_time desc");
			int x = claimsVerificationList.size();
			for ( int i = 0; i < x; i++) {
				ClaimsVerificationBean cvBean = (ClaimsVerificationBean) claimsVerificationList.get(i);
				//获取理赔单中理赔商品集合
				List claimsVerificationProductList = claimsVerificationProduct.getClaimsVerificationProductClaimsType("SELECT * FROM claims_verification_product where claims_verification_id="+cvBean.getId());
				//用来显示页面“单品理赔（含包装）”的功能
				int whole =0;//整单理赔
				int singleProduct=0;//sku理赔变量
				int postage=0;//三倍理赔
				int packing=0;//包装理赔
				for(int j = 0 ; j < claimsVerificationProductList.size();j++){
					
					ClaimsVerificationProductBean cvp = (ClaimsVerificationProductBean) claimsVerificationProductList.get(j);
					if(claimsVerificationProductList.size()<=1){
						cvBean.setType(cvp.getClaimsType());
					}else{
						if(cvp.getClaimsType()==1){
							singleProduct++;
						}
						if(cvp.getClaimsType()==2){
							postage++;
						}
						if(cvp.getClaimsType()==3){
							packing++;
						}
						//根据4中理赔方式的组合方式，在页面上显示对应的信息
						if(singleProduct>0 && packing>0){
							cvBean.setType(4);
						}else if(singleProduct>0){
							cvBean.setType(1);
						}else if(postage>0){
							cvBean.setType(2);
						}else if(packing>0){
							cvBean.setType(3);
						}
					}
				}
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
								list.add(bsbyBean);
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
				cvBean.setBsbyList(list);
				cvBean.setDeliverCompanyName((String)voOrder.deliverMapAll.get(""+cvBean.getDeliver()));
			}
			
			paging.setPrefixUrl("claimsVerificationAction.do?method=getClaimsVerificationInfo" + params.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("recordNum", totalCount+"");
			request.setAttribute("list", claimsVerificationList);	
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("claimsVerificationInfo");
	}
	
	
	/**
	 * 进入编辑页前， 得到理赔单数据
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward foreEditClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("tip", "没有这个理赔核销单！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
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
					request.setAttribute("tip", "没有找到理赔单对应 的订单信息！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(vorder.getStatus() != 11 ) {
					request.setAttribute("tip", "理赔单对应的订单不已是已退回状态，不能操作！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
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
				return mapping.findForward("printClaimsVerification");
			}
			if( cvBean.getStatus() > 0 ) {
				MailingBalanceBean mbBean = baService.getMailingBalance("order_id=" + vorder.getId());
				Map<Integer, String> moneyMap = claimsVerificationService.calculateAvailablePrice(cvBean, vorder, mbBean);
				request.setAttribute("orderPrice", moneyMap.get(0));
				request.setAttribute("skuPrice", moneyMap.get(1));
				request.setAttribute("mailPrice", moneyMap.get(2));
				request.setAttribute("packPrice", moneyMap.get(3));
				return mapping.findForward("auditClaimsVerification");
			}
			
		} catch ( Exception e ) {
			//statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("editClaimsVerification");
	}
	
	
	/**
	 * 
	 * 添加理赔结算信息
	 * @return
	 */
	public ActionForward addClaimsInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		//int type = StringUtil.toInt(request.getParameter("cvType"));
		int isTicket = StringUtil.toInt(request.getParameter("cvIsTicket"));
		float price = StringUtil.toFloat(request.getParameter("cvPrice"));
		int id = StringUtil.toInt(request.getParameter("id"));
		if( isTicket == -1 || isTicket > 1 ) {
			request.setAttribute("tip", "开具发票参数类型错误！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( id <= 0 ) {
			request.setAttribute("tip", "找不到对应的理赔单!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( price < 0 ) {
			request.setAttribute("tip", "输入价格有误！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService(); 
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationProduct claimsVerificationProduct = new ClaimsVerificationProduct(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				claimsVerificationService.getDbOp().startTransaction();
				
				ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "找不到对应的理赔单!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				} else if( cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_COMPLETE) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "理赔单状态是已完成，不可以修改结算信息了！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( !claimsVerificationService.updateClaimsVerification("price="+ price+ ", is_ticket=" + isTicket, "id=" + id)) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "修改理赔结算信息时，数据库操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//添加退货日志
				if (!packageLog.addReturnPackageLog("修改理赔信息", user, cvBean.getOrderCode())) {
					claimsVerificationService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加退货日志失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
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
		request.setAttribute("tip", "修改理赔单结算数据成功！");
		request.setAttribute("url", request.getContextPath()+"/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id="+id);
		return mapping.findForward("tip");
		
	}
	
	
	/**
	 * 
	 * 编辑理赔核销单
	 * @return
	 */
	public void editClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		int id = StringUtil.parstInt(request.getParameter("id"));
		int orderId = StringUtil.parstInt(request.getParameter("currentOrderId"));
		String[] productIds = request.getParameterValues("currentProductIds");
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		int reasonType = ProductWarePropertyService.toInt(request.getParameter("reasonType"));
		String reasonRemark =StringUtil.convertNull(request.getParameter("reasonRemark"));
		voOrder vorder = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.STAT_SLAVE);
		ClaimsVerificationStatService claimsVerficationStatService = new ClaimsVerificationStatService(IBaseService.CONN_IN_SERVICE,dbop);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		boolean allPrice = false;
		boolean threeMailPrice = false;
		boolean skuPrice = false;
		boolean packagePrice = false;
		boolean hasGift = false;
		int type=1;
		try {
			synchronized (lock) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				statService.getDbOp().startTransaction();
				// 检验 订单是否存在 
				// 检验 订单 是否有 退货入库记录
				voUser user = (voUser)request.getSession().getAttribute("userView");
				if(user == null) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'没有登录不可添加!'}");
					return;
				}
				UserGroupBean group = user.getGroup();
				if( !group.isFlag(747) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'您没有编辑理赔核销权限!'}");
					return;
				}
				if(!CargoDeptAreaService. hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN)){
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'你没有对应地区的退货库操作权限!'}");
					return;
				}
				ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'没有这个理赔单!'}");
					return;
				}
				//验证状态， 只有为 未处理的可以继续进行编辑的。
				if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_UNDEAL) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'理赔单状态不是未处理，不可以进行编辑！'}");
					return;
				}
				if( reasonType == -1 ) {
					reasonType = 11;
				}
				
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				cvBean.setClaimsVerificationProductList(cvpList);
				
				if( (productIds == null || productIds.length == 0) && cvpList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'当前没有任何修改！'}");
					return;
				}
				//添加退货日志
				if (!packageLog.addReturnPackageLog("修改理赔单", user, cvBean.getOrderCode())) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'添加退货日志失败！'}");
					return;
				}
				if( (productIds == null || productIds.length == 0) && cvpList.size() > 0 ) {
					//这种就是删除了所有的商品后的情况
					if( !claimsVerificationService.deleteClaimsVerificationProduct("claims_verification_id=" + cvBean.getId())) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'在删除理赔单商品时，数据库操作失败！'}");
						return;
					}
					statService.getDbOp().commitTransaction();
					statService.getDbOp().getConn().setAutoCommit(true);
					response.getWriter().write("{status:'success', tip:'修改理赔单成功！', url:'" + request.getContextPath() + "/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=" + id + "'}");
					return;
				}
				
				if ( orderId != 0 ){
					vorder = wareService.getOrder("id="+orderId);
					if( vorder == null ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'没有找到订单信息！'}");
						return;
					}
					if(vorder.getStatus() != 11 ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'当前订单不是已退回状态，不能操作！'}");
						return;
					}
				} 
				Map<String,Float> priceMap = claimsVerificationService.calculateClaimsPrice(vorder.getId(),claimsVerficationStatService);
				
				String packageCode = "";
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'没有找到对应的包裹单信息！'}");
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
					response.getWriter().write("{status:'fail', tip:'该订单入的不是所选库地区，不能添加理赔单！'}");
					return;
				}
				
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
				if( rpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'该订单未入库，不能添加理赔单！'}");
					return;
				}
				
				if( vorder.getCode().equals(cvBean.getOrderCode()) ) {
					// 需要对比 商品数量后 决定
					Map currentMap = new HashMap();
					Map submitMap = new HashMap();
					int x = productIds.length;
					int y = cvpList.size();
					for( int i = 0; i < x; i++ ) {
						int tempPid = ProductWarePropertyService.toInt(productIds[i]);
						voProduct product = wareService.getProduct(tempPid);
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
						//实现编辑
						if( currentMap.containsKey(productCode)) {
							voProduct product = (voProduct) currentMap.get(productCode);
							int count = StringUtil.parstInt(request.getParameter("current_count_"+product.getId()));
							int exist = ProductWarePropertyService.toInt(request.getParameter("exist_" + product.getId()));
							int claimsType = ProductWarePropertyService.toInt(request.getParameter("claims_type_"+product.getId()));
							if( claimsType == 0 ) {
								allPrice = true;
								if( priceMap.containsKey("SKU_"+product.getId()) ) {
									float price = priceMap.get("SKU_"+product.getId());
									if( Float.compare(price, 0.00f) == 0 ) {
										hasGift = true;
									}
								}
							} else if ( claimsType == 1 ) {
								skuPrice = true;
								if( priceMap.containsKey("SKU_"+product.getId()) ) {
									float price = priceMap.get("SKU_"+product.getId());
									if( Float.compare(price, 0.00f) == 0 ) {
										hasGift = true;
									}
								}
							} else if ( claimsType == 2 ) {
								threeMailPrice = true;
							}  else if ( claimsType == 3 ) {
								packagePrice = true;
							}

							if( exist == -1 ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "的有无参数有错误！'}");
								return;
							}
							Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
							if( productsMap == null ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'订单信息有误，请重新扫描订单！'}");
								return;
							}
							if(!productsMap.containsKey( product.getId())) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "不属于这个订单！'}");
								return;
							}
							OrderStockProductBean ospBean = productsMap.get(product.getId());
							if( count > ospBean.getStockoutCount() ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "数量大于了订单中的量！'}");
								return;
							}
							if( exist == 0) {
								// 无实物的处理
								boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
								if( !stockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，添加理赔单失败！'}");
									return;
								}
								//货位库存
								boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
								if( !cargoStockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，添加理赔单失败！'}");
									return;
								}
							} else if ( exist == 1) {
								//库存
							}
							
							if( !claimsVerificationService.updateClaimsVerificationProduct("count="+ count + ", exist="+ exist + ", claims_type="+claimsType, "claims_verification_id="+id + " and product_code='"+ product.getCode() +"'")) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品在修改数据时， 数据库操作失败！'}");
								return;
							}
						} else {
							//原来没有需要添加
							//add
							voProduct product = (voProduct)submitMap.get(productCode);
							Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
							if( productsMap == null ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'订单信息有误，请重新扫描订单！'}");
								return;
							}
							if(!productsMap.containsKey( product.getId())) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "不属于这个订单！'}");
								return;
							}
							
							int count = StringUtil.parstInt(request.getParameter("current_count_"+product.getId()));
							int claimsType = ProductWarePropertyService.toInt(request.getParameter("claims_type_"+product.getId()));
							if( claimsType == 0 ) {
								allPrice = true;
								if( priceMap.containsKey("SKU_"+product.getId()) ) {
									float price = priceMap.get("SKU_"+product.getId());
									if( Float.compare(price, 0.00f) == 0 ) {
										hasGift = true;
									}
								}
							} else if ( claimsType == 1 ) {
								skuPrice = true;
								if( priceMap.containsKey("SKU_"+product.getId()) ) {
									float price = priceMap.get("SKU_"+product.getId());
									if( Float.compare(price, 0.00f) == 0 ) {
										hasGift = true;
									}
								}
							} else if ( claimsType == 2 ) {
								threeMailPrice = true;
							}  else if ( claimsType == 3 ) {
								packagePrice = true;
							}
							
						
							OrderStockProductBean ospBean = productsMap.get(product.getId());
							if( count > ospBean.getStockoutCount() ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "数量大于了订单中的量！'}");
								return;
							}
							int exist = ProductWarePropertyService.toInt(request.getParameter("exist_" + product.getId()));
							if( exist == -1 ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "的有无参数有错误！'}");
								return;
							}
							if( exist == 0) {
								// 无实物的处理
								//库存
								boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
								if( !stockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，修改理赔单失败！'}");
									return;
								}
								//货位库存
								boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
								if( !cargoStockCheck ) {
									statService.getDbOp().rollbackTransaction();
									response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，修改理赔单失败！'}");
									return;
								}
							} else if ( exist == 1) {
							}
							ClaimsVerificationProductBean cvpBean = new ClaimsVerificationProductBean();
							cvpBean.setCount(count);
							cvpBean.setExist(exist);
							cvpBean.setClaimsVerificationId(id);
							cvpBean.setProductCode(product.getCode());
							
							cvpBean.setClaimsType(claimsType);
							if( !claimsVerificationService.addClaimsVerificationProduct(cvpBean)) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品加入理赔单时，数据库操作失败！'}");
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
								response.getWriter().write("{status:'fail', tip:'在删除理赔单商品时，数据库操作失败！'}");
								return;
							}
						}
					}
					
				} else {
					// 首先要删除所有原来关联的 商品
					if( cvpList.size() != 0 ) {
						if( !claimsVerificationService.deleteClaimsVerificationProduct("claims_verification_id="+ id)) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'在删除原来理赔单商品时，数据库操作失败！'}");
							return;
						}
					}
					//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
					ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + ")" );
					if( cvBean2 != null ) {
						response.getWriter().write("{status:'fail', tip:'当前关联订单已经有已提交或已审核的理赔核销单了！'}");
						return;
					}
					// 跟添加流程差不多了
					//修改理赔核销单的类型 hp
					
					if(allPrice && !threeMailPrice && !packagePrice && !skuPrice){
						type=0;
					} else if(skuPrice && !threeMailPrice && !packagePrice && !allPrice){
						type=1;
					} else if(threeMailPrice && !skuPrice && !packagePrice && !allPrice){
						type=2;
					}else if(packagePrice && !skuPrice && !threeMailPrice && !allPrice){
						type=3;
					}else if(packagePrice && skuPrice && !threeMailPrice && !allPrice ){
						type=4;
					}else{
						response.getWriter().write("{status:'fail', tip:'该理赔核销单组合不被允许！'}");
						return;
					}
					
					//修改原有的理赔单 关联的  订单
					if( !claimsVerificationService.updateClaimsVerification("order_code='" + vorder.getCode() + "', type="+type+", deliver="+ vorder.getDeliver()+", package_code='" + packageCode +"'", "id=" +id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'更新理赔单时，数据库操作失败！'}");
						return;
					}
					// 添加理赔单的商品
					int x = productIds.length;
					for(int i = 0; i < x; i++ ) {
						String productId = productIds[i];
						int count = StringUtil.parstInt(request.getParameter("current_count_"+productId));
						int exist = ProductWarePropertyService.toInt(request.getParameter("exist_" + productId));
						int claimsType = ProductWarePropertyService.toInt(request.getParameter("claims_type_"+productId));
						if( claimsType == 0 ) {
							allPrice = true;
							if( priceMap.containsKey("SKU_"+productId) ) {
								float price = priceMap.get("SKU_"+productId);
								if( Float.compare(price, 0.00f) == 0 ) {
									hasGift = true;
								}
							}
						} else if ( claimsType == 1 ) {
							skuPrice = true;
							if( priceMap.containsKey("SKU_"+productId) ) {
								float price = priceMap.get("SKU_"+productId);
								if( Float.compare(price, 0.00f) == 0 ) {
									hasGift = true;
								}
							}
						} else if ( claimsType == 2 ) {
							threeMailPrice = true;
						}  else if ( claimsType == 3 ) {
							packagePrice = true;
						}
						int iProductId = StringUtil.parstInt(productId);
						voProduct product = wareService.getProduct(iProductId);
						if( product == null ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'商品信息有误！'}");
							return;
						}
						// 检验 商品是否 属于订单
						Map<Integer,OrderStockProductBean> productsMap = (Map<Integer,OrderStockProductBean>)request.getSession().getAttribute("OrderStockProductsMap_" + vorder.getId());
						if( productsMap == null ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'订单信息有误，请重新扫描订单！'}");
							return;
						}
						if(!productsMap.containsKey( product.getId())) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "不属于这个订单！'}");
							return;
						}
						OrderStockProductBean ospBean = productsMap.get(product.getId());
						if( count > ospBean.getStockoutCount() ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "数量大于了订单中的量！'}");
							return;
						}
						if( exist == -1 ) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'商品：" + product.getCode() +  "的有无参数有错误！'}");
							return;
						}
						if( exist == 0) {
							// 无实物的处理
							//库存
							boolean stockCheck = claimsVerificationService.checkProductStock(wareArea, ProductStockBean.STOCKTYPE_RETURN, product, count, service);
							if( !stockCheck ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，修改理赔单失败！'}");
								return;
							}
							//货位库存
							boolean cargoStockCheck = claimsVerificationService.checkCargoStock(wareArea, ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, count, cargoService);
							if( !cargoStockCheck ) {
								statService.getDbOp().rollbackTransaction();
								response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品已出库，修改理赔单失败！'}");
								return;
							}
						} else if ( exist == 1) {
							
						}
						ClaimsVerificationProductBean cvpBean = new ClaimsVerificationProductBean();
						cvpBean.setCount(count);
						cvpBean.setExist(exist);
						cvpBean.setClaimsVerificationId(id);
						cvpBean.setProductCode(product.getCode());
						cvpBean.setClaimsType(claimsType);
						if( !claimsVerificationService.addClaimsVerificationProduct(cvpBean)) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("{status:'fail', tip:'" + product.getCode() +  "商品加入理赔单时，数据库操作失败！'}");
							return;
						}
					}
				}
				// 检验商品 在退货库 数量 是否有 （在有实物的时候）
				if( cvBean.getWareArea() != wareArea ) {
					if( !claimsVerificationService.updateClaimsVerification(" type="+type+", ware_area="+wareArea, "id=" +id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'更新理赔单时，数据库操作失败！'}");
						return;
					}
				}
				if( cvBean.getReasonType() != reasonType ) {
					if( !claimsVerificationService.updateClaimsVerification(" type="+type+", reason_type="+reasonType, "id=" +id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'更新理赔单时，数据库操作失败！'}");
						return;
					}
				}
				if( !cvBean.getReasonRemark().equals(reasonRemark) ) {
					if( !claimsVerificationService.updateClaimsVerification(" type="+type+", reason_remark='"+reasonRemark+"'", "id=" +id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'更新理赔单时，数据库操作失败！'}");
						return;
					}
				}
				if( hasGift ) {
					if(!claimsVerificationService.updateClaimsVerification(" type="+type+", has_gift="+ClaimsVerificationBean.HAS_GIFT_YES, "id="+id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'数据库操作失败！'}");
						return;
					}
				} else {
					if(!claimsVerificationService.updateClaimsVerification(" type="+type+",  has_gift="+ClaimsVerificationBean.HAS_GIFT_NO, "id="+id) ) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("{status:'fail', tip:'数据库操作失败！'}");
						return;
					}
				}
				if( (allPrice && (threeMailPrice || skuPrice || packagePrice )) || (threeMailPrice && (allPrice || skuPrice || packagePrice)) ) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("{status:'fail', tip:'理赔方式在整单理赔和3倍运费理赔所有商品选择要一致！'}");
					return;
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
				request.getSession().setAttribute("OrderStockProductsMap_"+vorder.getId(), null);
				response.getWriter().write("{status:'success', tip:'修改理赔单成功！', url:'" + request.getContextPath() + "/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=" + id + "'}");
				return;
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
			claimsVerficationStatService.releaseAll();
		}
		
	}
	
	
	/**
	 * 
	 * 删除理赔核销单
	 * @return
	 */
	public ActionForward deleteClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(747) ) {
			request.setAttribute("tip", "您没有编辑理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int id = StringUtil.parstInt(request.getParameter("id"));
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id="+id);
				if( cvBean == null ) {
					request.setAttribute("tip", "没有这个理赔核销单！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_UNDEAL ) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "理赔单状态不是未处理，不可以删除!");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
					if( !claimsVerificationService.deleteClaimsVerification("id =" + id)) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "在删除理赔单时，数据库操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if( cvpList == null || cvpList.size() == 0 ) {
					
					} else {
						if( !claimsVerificationService.deleteClaimsVerificationProduct("claims_verification_id = " + cvBean.getId()) ) {
							statService.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "在删除理赔单商品时，数据库操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
				}
				if (!packageLog.addReturnPackageLog("删除理赔单", user, cvBean.getOrderCode())) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加退货日志失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
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
		ActionForward af = new ActionForward("/admin/claimsVerificationAction.do?method=getClaimsVerificationInfo");
		return af;
	}
	
	/**
	 * 
	 * 提交理赔核销单
	 * @return
	 */
	public ActionForward confirmClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(748) ) {
			request.setAttribute("tip", "您没有提交理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.STAT_SLAVE);
		ClaimsVerificationStatService claimsVerficationStatService = new ClaimsVerificationStatService(IBaseService.CONN_IN_SERVICE,dbop);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int type=1;
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				//检验 要提交的 理赔单是否存在
				cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					request.setAttribute("tip", "没有这个理赔单！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//验证状态， 只有为 未处理的可以继续进行编辑的。
				if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_UNDEAL) {
					request.setAttribute("tip", "理赔单状态不是未处理，不可以提交！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				// 看订单是否存在
				vorder = wareService.getOrder("code='"+ cvBean.getOrderCode() +"'");
				if( vorder == null ) {
					request.setAttribute("tip", "没有找到理赔单对应的订单信息！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( vorder.getStatus() != 11) {
					request.setAttribute("tip", "理赔单对应的订单不是已退回状态，不能操作！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				//验证是否已经有 对应该订单的理赔核销单 是已确认， 已审核通过， 已完成的状态  如果有 不允许继续提交了
				ClaimsVerificationBean cvBean2 = claimsVerificationService.getClaimsVerification("order_code='"+ vorder.getCode()+"' and status in(" + ClaimsVerificationBean.CLAIMS_CONFIRM + "," + ClaimsVerificationBean.CLAIMS_AUDIT + ")");
				if( cvBean2 != null ) {
					request.setAttribute("tip", "当前理赔单对应订单已经有已提交或已审核的理赔核销单！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				//看订单包裹是否可以查到
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					request.setAttribute("tip", "没有找到对应的包裹单信息！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//看订单是否如果 退货库
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() +"'");
				if( rpBean == null ) {
					request.setAttribute("tip", "理赔单对应的订单并未入过返厂库，请核对！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				ReturnedPackageBean rpBean2 = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "' and area="+ cvBean.getWareArea());
				if( rpBean2 == null ) {
					request.setAttribute("tip", "理赔单对应的订单入的不是所选地区的返厂库，请核对！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(rpBean2.getClaimsVerificationId() != 0 ) {
					request.setAttribute("tip", "理赔单对应的退货包裹已经有处理中或已审核的理赔核销单！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				// 获取理赔商品列表  如果列表大小为0 就不允许提交
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				cvBean.setClaimsVerificationProductList(cvpList);
				if( cvpList == null || cvpList.size() == 0 ) {
					request.setAttribute("tip", "理赔单中并没有添加任何商品，不可以提交!");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
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
					Map<String,Float> priceMap = claimsVerificationService.calculateClaimsPrice(vorder.getId(),claimsVerficationStatService);
					boolean allPrice = false;
					boolean threeMailPrice = false;
					boolean skuPrice = false;
					boolean packagePrice = false;
					for( int i = 0; i < cvpList.size(); i ++ ) {
						ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) cvpList.get(i);
						voProduct product = wareService.getProduct(cvpBean.getProductCode());
						cvpBean.setProduct(product);
						if( cvpBean.getExist() == 0 ) {
							if( cvpBean.getCount() > 0 ){
								//如果无实物 要看看库存够不够
								boolean stockCheck = claimsVerificationService.checkProductStock(cvBean.getWareArea(), ProductStockBean.STOCKTYPE_RETURN, product, cvpBean.getCount(), service);
								if( !stockCheck ) {
									request.setAttribute("tip", product.getCode() +  "商品已出库，提交理赔单失败");
									request.setAttribute("result", "failure");
									statService.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								//货位库存
								boolean cargoStockCheck = claimsVerificationService.checkCargoStock(cvBean.getWareArea(), ProductStockBean.STOCKTYPE_RETURN,CargoInfoBean.STORE_TYPE2, product, cvpBean.getCount(), cargoService);
								if( !cargoStockCheck ) {
									request.setAttribute("tip", product.getCode() +  "商品已出库，修改理赔单失败！");
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
						int claimsType = cvpBean.getClaimsType();
						if( claimsType == 0 ) {
							allPrice = true;
						} else if ( claimsType == 1 ) {
							skuPrice = true;
						} else if ( claimsType == 2 ) {
							threeMailPrice = true;
						}  else if ( claimsType == 3 ) {
							packagePrice = true;
						}
					}
                  //修改理赔核销单的类型 hp
					
					if(allPrice && !threeMailPrice && !packagePrice && !skuPrice){
						type=0;
					} else if(skuPrice && !threeMailPrice && !packagePrice && !allPrice){
						type=1;
					} else if(threeMailPrice && !skuPrice && !packagePrice && !allPrice){
						type=2;
					}else if(packagePrice && !skuPrice && !threeMailPrice && !allPrice){
						type=3;
					}else if(packagePrice && skuPrice && !threeMailPrice && !allPrice ){
						type=4;
					}else{
						request.setAttribute("tip", "该理赔核销单组合不被允许！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
				    }
					float totalPrice = claimsVerificationService.calculateTotalClaimsPrice(cvpList,priceMap,vorder);
					if( !claimsVerificationService.updateClaimsVerification(" type="+type+", price="+ totalPrice, "id=" + id)) {
						claimsVerificationService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "修改理赔结算信息时，数据库操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String set = " type="+type+", status="+ClaimsVerificationBean.CLAIMS_CONFIRM +", confirm_user_name='" + user.getUsername() + "', confirm_user_id=" + user.getId() + ", confirm_time='" + DateUtil.getNow() + "'";
					if( !claimsVerificationService.updateClaimsVerification(set, "id=" + cvBean.getId())) {
						request.setAttribute("tip", "修改理赔单状态时，数据库操作失败！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if( !statService.updateReturnedPackage(" claims_verification_id=" + id, "id=" + rpBean.getId())) {
						request.setAttribute("tip", "修改退货包裹时，数据库操作失败！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if (!packageLog.addReturnPackageLog("提交理赔单", user, vorder.getCode())) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "添加退货日志失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if( (allPrice && (threeMailPrice || skuPrice || packagePrice )) || (threeMailPrice && (allPrice || skuPrice || packagePrice)) ) {
						request.setAttribute("tip", "理赔方式在整单理赔和3倍运费理赔所有商品选择要一致！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//这里加写回
					
				}
				//statService.getDbOp().rollbackTransaction();
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch ( MyException me ) {
			request.setAttribute("tip", me.getMessage());
			request.setAttribute("result", "failure");
			statService.getDbOp().rollbackTransaction();
			return mapping.findForward(IConstants.FAILURE_KEY);
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
			claimsVerficationStatService.releaseAll();
		}
		request.setAttribute("tip", "提交理赔单成功！");
		request.setAttribute("url", request.getContextPath()+"/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=" + id);
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 提交理赔核销单价格
	 * @return
	 */
	public ActionForward recalculateClaimsPrice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		WareService wareService = new WareService();
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.STAT_SLAVE);
		ClaimsVerificationStatService claimsVerficationStatService = new ClaimsVerificationStatService(IBaseService.CONN_IN_SERVICE,dbop);
		int id = ProductWarePropertyService.toInt(request.getParameter("id"));
		int type=1;
		boolean allPrice = false;
		boolean threeMailPrice = false;
		boolean skuPrice = false;
		boolean packagePrice = false;
		try {
			wareService.getDbOp().startTransaction();
			ClaimsVerificationBean cvBean = claimsVerificationService.getClaimsVerification("id="+id);
			if( cvBean == null ) {
				throw new MyException("没有找到对应的理赔核销单！");
			}
			List<ClaimsVerificationProductBean> list = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id="+id, -1, -1, "id asc");
			for(ClaimsVerificationProductBean cvpBean : list ) {
				voProduct product = wareService.getProduct(cvpBean.getProductCode());
				cvpBean.setProduct(product);
				int claimsType = ProductWarePropertyService.toInt(request.getParameter("claims_type_"+ cvpBean.getProduct().getId()));
				if( claimsType == -1 ) {
					throw new MyException("参数错误！");
				}
				if( !claimsVerificationService.updateClaimsVerificationProduct("claims_type="+claimsType, "id="+cvpBean.getId())) {
					throw new MyException("修改数据库操作失败！");
				}
				cvpBean.setClaimsType(claimsType);
				if( claimsType == 0 ) {
					allPrice = true;
				} else if ( claimsType == 1 ) {
					skuPrice = true;
				} else if ( claimsType == 2 ) {
					threeMailPrice = true;
				}  else if ( claimsType == 3 ) {
					packagePrice = true;
				}
			}
			voOrder vorder = wareService.getOrder("code='"+cvBean.getOrderCode()+"'");
			Map<String,Float> priceMap = claimsVerificationService.calculateClaimsPrice(vorder.getId(),claimsVerficationStatService);
			float totalPrice = claimsVerificationService.calculateTotalClaimsPrice(list,priceMap,vorder);
			//修改理赔核销单的类型 hp
			
		
		
			
			if(allPrice && !threeMailPrice && !packagePrice && !skuPrice){
				type=0;
			} else if(skuPrice && !threeMailPrice && !packagePrice && !allPrice){
				type=1;
			} else if(threeMailPrice && !skuPrice && !packagePrice && !allPrice){
				type=2;
			}else if(packagePrice && !skuPrice && !threeMailPrice && !allPrice){
				type=3;
			}else if(packagePrice && skuPrice && !threeMailPrice && !allPrice ){
				type=4;
			}else{
				request.setAttribute("tip", "该理赔核销单组合不被允许！");
				request.setAttribute("result", "failure");
				wareService.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
				
			}
			
			if( !claimsVerificationService.updateClaimsVerification("type="+type+",price="+totalPrice, "id="+id) ) {
				throw new MyException("数据库操作失败！");
			}
			wareService.getDbOp().commitTransaction();
			request.setAttribute("tip", "保存成功，理赔金额已经重新计算！");
			request.setAttribute("url", request.getContextPath()+"/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=" + id);
		}catch ( MyException me ) {
			request.setAttribute("tip", me.getMessage());
			request.setAttribute("result", "failure");
			wareService.getDbOp().rollbackTransaction();
			return mapping.findForward(IConstants.FAILURE_KEY);
		} catch ( Exception e ) {
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "系统异常!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			wareService.releaseAll();
			claimsVerficationStatService.releaseAll();
		}
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 审核理赔核销单
	 * @return
	 */
	public ActionForward auditClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(749) ) {
			request.setAttribute("tip", "您没有审核理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int id = StringUtil.parstInt(request.getParameter("id"));
		int yesno = StringUtil.parstInt(request.getParameter("yesno"));
		WareService wareService = new WareService(); 
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				wareService.getDbOp().startTransaction();
				claimsVerificationService.auditClaimsVerification(id, yesno, user);
				
				wareService.getDbOp().commitTransaction();
				wareService.getDbOp().getConn().setAutoCommit(true);
				request.setAttribute("tip", "审核理赔单通过操作成功！");
			}
		} catch (MyException me ) {
			request.setAttribute("tip", me.getMessage());
			request.setAttribute("result", "failure");
			mapping.findForward(IConstants.FAILURE_KEY);
		}catch ( Exception e ) {
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		request.setAttribute("url", request.getContextPath()+"/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=" + id);
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 批量审核理赔核销单
	 * @return
	 */
	public ActionForward auditClaimsVerificationBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(749) ) {
			request.setAttribute("tip", "您没有审核理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String[] ids = request.getParameterValues("auditClaimsId");
		if( ids == null || ids.length == 0 ) {
			request.setAttribute("tip", "请至少勾选一个理赔单！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int yesno = StringUtil.parstInt(request.getParameter("yesno"));
		WareService wareService = new WareService(); 
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				wareService.getDbOp().startTransaction();
				for( int i = 0 ; i < ids.length; i ++ ) {
					String sId = ids[i];
					int id = ProductWarePropertyService.toInt(sId);
					claimsVerificationService.auditClaimsVerification(id, yesno,user);
				}
				wareService.getDbOp().commitTransaction();
				wareService.getDbOp().getConn().setAutoCommit(true);
				request.setAttribute("tip", "审核理赔单通过操作成功！");
			}
		} catch (MyException me ) {
			request.setAttribute("tip", me.getMessage());
			request.setAttribute("result", "failure");
			mapping.findForward(IConstants.FAILURE_KEY);
		} catch ( Exception e ) {
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		request.setAttribute("url", request.getContextPath()+"/admin/claimsVerificationAction.do?method=getClaimsVerificationInfo");
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 完成理赔核销单
	 * @return
	 */
	public ActionForward completeClaimsVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(759) ) {
			request.setAttribute("tip", "您没有确认完成理赔单的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				//检验 要提交的 理赔单是否存在
				cvBean = claimsVerificationService.getClaimsVerification("id=" + id);
				if( cvBean == null ) {
					request.setAttribute("tip", "没有这个理赔单！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//验证状态， 只有审核通过的可以完成
				if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_AUDIT) {
					request.setAttribute("tip", "理赔单状态不是审核通过，不可以完成！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				// 看订单是否存在
				vorder = wareService.getOrder("code='"+ cvBean.getOrderCode() +"'");
				if( vorder == null ) {
					request.setAttribute("tip", "没有找到理赔单对应的订单信息！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( vorder.getStatus() != 11) {
					request.setAttribute("tip", "理赔单对应的订单不是已退回状态，不能操作！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//看订单包裹是否可以查到
				List auditPackageList = istockService
				.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
				if( auditPackageList == null || auditPackageList.size() == 0 ) {
					request.setAttribute("tip", "没有找到对应的包裹单信息！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//看订单是否入过 退货库
				ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
				if( rpBean == null ) {
					request.setAttribute("tip", "该订单并未入过返厂库，请核对！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				// 获取理赔商品列表  如果列表大小为0 就不允许提交
				List cvpList = claimsVerificationService.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
				cvBean.setClaimsVerificationProductList(cvpList);
				if( cvpList == null || cvpList.size() == 0 ) {
					request.setAttribute("tip", "理赔单中并没有添加任何商品，不可以完成!");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					//hp
					if( !claimsVerificationService.updateClaimsVerification("status=" + ClaimsVerificationBean.CLAIMS_COMPLETE+", complete_user_name='" + user.getUsername() + "', complete_user_id="+user.getId() + ", complete_time='" + DateUtil.getNow()+"'", "id=" + id)) {
						request.setAttribute("tip", "理赔单修改状态时，数据库操作失败！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				if (!packageLog.addReturnPackageLog("理赔单确认完成", user, vorder.getCode())) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加退货日志失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
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
		request.setAttribute("tip", "完成理赔单成功！");
		request.setAttribute("url", request.getContextPath()+"/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=" + id);
		return mapping.findForward("tip");
	}
	/**
	 * 获得登陆账号是否可以操作传入的库地区id
	 * 是 为 true  否 false
	 * @param request
	 * @return 库地区列表，其中元素为代表地区的数字
	 */
	public boolean getCargoDeptAreaListByVoUser(voUser vo,String area) {
		boolean result=false;
		//System.out.println("传入库地区-hp4-4--"+area);
		if(vo==null ||vo.getCargoStaffBean()==null){
			//System.out.println("用户的库地区为空");
			return false;
		}
		CargoStaffBean cargoStaffBean = vo.getCargoStaffBean();
		List<String> areaList = new ArrayList<String>();
		try{
			if (cargoStaffBean != null) {
			//	System.out.println("cargoStaffBean != null");
				List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
				
				for (int i = 0; i < cargoDeptAreaList.size(); i++) {
					String code = (String) cargoDeptAreaList.get(i);
					String sessionArea = code.split("-")[0];
					//System.out.println("code="+code);
					//System.out.println("sessionArea="+sessionArea);
					if(!areaList.contains(sessionArea)){
						areaList.add(sessionArea);
					}
				}
			}else{
				System.out.println("cargoStaffBean == null");
				AdminService adminService = new AdminService();
				try{
					//重新查询物流员工及其相关库地区和库类型
					cargoStaffBean = adminService.getCargoStaff(adminService.getDbOp(), vo);
					if (cargoStaffBean != null) {
						vo.setCargoStaffBean(cargoStaffBean);
						
						
						List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
						for (int i = 0; i < cargoDeptAreaList.size(); i++) {
							String code = (String) cargoDeptAreaList.get(i);
							String sessionArea = code.split("-")[0];
							if(!areaList.contains(sessionArea)){
								areaList.add(sessionArea);
							}
						}
						
					}
				
				}catch (Exception e) {
					return result;
				}finally{
					adminService.releaseAll();
				}
			}
			if(areaList.size()>0){
				//System.out.println("传入库地区-h-"+area);
				for (String areastr : areaList) {
					//System.out.println("你拥有的库地区---"+areastr);
					if(areastr.equals(area)){
						return true;
					}
				}
			}else{
				result=false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 检查包裹
	 * @return
	 */
	public void checkPackageAutomatic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		String code = StringUtil.convertNull(request.getParameter("code"));
		String tip = "";
		String status = "";
		String consignment = "0";
		String obib = "normal";
		voOrder vorder = null;
		OrderStockBean orderStockBean = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IMEIService imeiSercive = new IMEIService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		try {
			synchronized ( lock ) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				
				if(user == null) {
					tip = "没有登录，不可以继续操作。";
					status = "shujuyichang";
				}
				if( code.equals("") ) {
					tip = "提交值为空。";
					status = "shujuyichang";
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
								status = "shujuyichang";
							} else if ( vorder.getStatus() != 11 ) {
								tip = "包裹单对应订单状态不是已退回。";
								status = "shujuyichang";
							} else {
								
								orderStockBean = istockService.getOrderStock("order_id="+vorder.getId()+ " and status!="+OrderStockBean.STATUS4);
								if( orderStockBean == null ) {
									tip = "包裹单号对应的出库单未找到。";
									status = "shujuyichang";
								}else {
									List<OrderStockProductBean> ospList = (List<OrderStockProductBean>)istockService.getOrderStockProductList("order_stock_id=" + orderStockBean.getId(), -1, -1, "id asc");
									if( ospList == null || ospList.size() == 0 ) {
										tip = "没有找到该包裹中对应商品信息。";
										status = "shujuyichang";
									} else {
										OrderBillInfoBean obiBean = claimsVerificationService.getOrderStockOutInfo(vorder.getId());
										if( obiBean == null || obiBean.getOrderList().size() == 0 ) {
											tip = "没有找到对应订单的电子档发货清单。";
											status = "shujuyichang";
										} else {
											ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
											if( rpBean == null ) {
												tip = "没有找到退货包裹记录。";
												status = "shujuyichang";
											}else if(!getCargoDeptAreaListByVoUser(user,rpBean.getArea()+"")){
												tip = "您没有该订单库地区的操作权限";//hepeng
											}else {
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
												status = "success";
											}
										}
									}
								}
							}
							
						} else {
							tip = "该包裹单号不存在。";
							obib = "clear";
							status = "shujuyichang";
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
								status = "shujuyichang";
							} else {
								tip = "扫描成功，订单号" + code;
								status = "success";
								if( orderStockBean.getOrderId() != vorder2.getId() ) { 
									tip += "r-n"+ "订单号和包裹单号不对应！";
									status = "shujuyichang";
								}
								//检查 商品数量和扫描数量 是否有差别
								List<OrderStockProductBean> list = orderStockBean.getOrderStockProductList(); 
								String temp = claimsVerificationService.checkPackageProductIntegrity(orderStockBean,list, scanMap, map);
								tip += temp;
								if( !claimsVerificationService.checkPackageProductIntegrityBoolean(orderStockBean,list, scanMap, map) ) {
									status = "shujuyichang";
								}
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
									status = "shujuyichang";
								}
								statService.getDbOp().commitTransaction();
								statService.getDbOp().getConn().setAutoCommit(true);
								
								if (!this.insertBiSaleRefundCount(orderStockBean.getStockArea(), orderStockBean.getCode(), scanMap, statService.getDbOp())) {
									status = "shujuyichang";
								}
								Object result = claimsVerificationService.addReturnPackageCheckInfo(scanMap,map,orderStockBean, list, apBean, rpBean, order,user);
								if( result instanceof String ) {
									tip += "r-n" + result;
									status = "shujuyichang";
								}
								tip += "r-n"+"包裹信息缓存已清空。";
							}
							
						} else if( code.startsWith("CK") ) {
							//出库单号
							OrderStockBean orderStockBean2 = istockService.getOrderStock("code='"+code + "'"+ " and status!="+OrderStockBean.STATUS4);
							if( orderStockBean2 == null ) {
								tip = "该出库单号不存在！";
								status = "shujuyichang";
							} else {
								tip = "扫描成功，出库单号" + code;
								status = "success";
								if( orderStockBean.getId() != orderStockBean2.getId() ) { 
									tip += "r-n"+"出库单号和包裹单号不对应！";
									status = "shujuyichang";
								}
								//检查 商品数量与扫描数量 是否有差别
								List<OrderStockProductBean> list = orderStockBean.getOrderStockProductList(); 
								String temp = claimsVerificationService.checkPackageProductIntegrity(orderStockBean,list, scanMap, map);
								tip += temp;
								if( !claimsVerificationService.checkPackageProductIntegrityBoolean(orderStockBean,list, scanMap, map) ) {
									status = "shujuyichang";
								}
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
									status = "shujuyichang";
								}
								statService.getDbOp().commitTransaction();
								statService.getDbOp().getConn().setAutoCommit(true);
								
								if (!this.insertBiSaleRefundCount(orderStockBean.getStockArea(), orderStockBean.getCode(), scanMap, statService.getDbOp())) {
									status = "shujuyichang";
								}
								Object result = claimsVerificationService.addReturnPackageCheckInfo(scanMap,map,orderStockBean, list, apBean, rpBean, order,user);
								if( result instanceof String ) {
									tip += "r-n" + result;
									status = "shujuyichang";
								}
								tip += "r-n"+"包裹信息缓存已清空。";
							}
							
						} else if( code.matches("[0-9]{1,20}")) {
							//商品编号
							
							voProduct product = claimsVerificationService.getProuctByAnyCode(code, bService,wareService);
							IMEIUserOrderBean imeiUserOrderBean =imeiSercive.getIMEIUserOrder("imei_code = '"+ code +"' order by id desc limit 1 " );
							if( product == null && imeiUserOrderBean ==null) {
								tip = "该商品编号不存在！";
								status = "shujuyichang";
							}else if(imeiSercive.isProductMMBMobile(product).equals("YES")){
								tip = "请扫描IMEI码！";
								status = "shujuyichang";
							}else {
								if ( imeiUserOrderBean != null ) {
									IMEIUserOrderBean imeiUserOrderBeanTemp = imeiSercive.getIMEIUserOrder("imei_code = '"+ code +"' and order_id="+rpBean.getOrderId());
									if( imeiUserOrderBeanTemp == null ) {
										tip = "该IMEI码未关联该订单！";
										status = "shujuyichang";
									} else {
										product =wareService.getProduct(imeiUserOrderBean.getProductId());
									}
								}
								if( product != null ) {
									tip = "扫描成功！当前商品" + product.getCode();
									status = "success";
									if( consignmentService.isProductConsignment(product.getId()) ) {
										consignment = "1";
									}
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
										status = "shujuyichang";
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
							}
						} else {
							tip = "当前缓存中已有包裹信息，请先取消当前包裹！";
							status = "shujuyichang";
						}
					}
					
				}
				String result = "{'tip': '"+ tip + "', 'OBIB': '" + obib + "', 'status':'"+status+"','consignment':'"+consignment+"'}";
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
	
	// 记录最后一次包裹核查时商品数量
	private boolean insertBiSaleRefundCount (int areaId, String ckCode, Map<Integer, Integer> scanMap, DbOperation db) {
		int count = 0;
		for (Map.Entry<Integer,Integer> map : scanMap.entrySet()) {
			count += map.getValue().intValue();
		}
		String date = DateUtil.getNowDateStr();
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE bi_sale_refund_count SET `count` = ").append( count );
		sb.append(" WHERE area_id =  ").append(areaId);
		sb.append(" AND datetime = '").append(date).append("' ");
		sb.append(" AND code = '").append(ckCode).append("' ");
		
		if (!db.executeUpdate(sb.toString())) {
			sb.setLength(0);
			sb.append(" INSERT INTO bi_sale_refund_count ( `area_id`, `datetime`, `code`, `count` ) ");
			sb.append(" VALUES ( ").append(areaId);
			sb.append(" , '").append(date).append("' ");
			sb.append(" , '").append(ckCode).append("' ");
			sb.append(" , ").append(count);
			sb.append("  ) "); 
			
			if (!db.executeUpdate(sb.toString())) {
				return false;
			}			
		}
		return true;
	}
	
	/**
	 * 
	 * 清空缓存包裹信息
	 * @return
	 */
	public void clearPackageCache(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
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
	 * 导入第三方退货包裹
	 * @return
	 */
	public ActionForward importThirdPartyReturnedPackage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		//System.out.println("传入的库地区--"+wareArea);
		String info = StringUtil.dealParam(request.getParameter("importInfo"));
		String tip = "";
		voOrder vorder = null;
		int successCount = 0;
		int failCount = 0;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(lock) {
				if( wareArea == -1 ) {
					tip += "\r\n" + "没有选择地区!";
					return new ActionForward("/admin/cargo/importThirdPartyReturnedPackage.jsp?errorMsg="+tip);
				}
				if( info == null || info.equals("")) {
					tip += "\r\n" + "没有导入信息！";
					return new ActionForward("/admin/cargo/importThirdPartyReturnedPackage.jsp?errorMsg="+tip);
				} else {
					String line = null;
					BufferedReader br = new BufferedReader(new StringReader(info));
					int count = 0;
					while ((line = br.readLine()) != null) {
						statService.getDbOp().startTransaction();
						count ++;
						line.replace(" ", "");
						String[] cols = line.split("\t");
						if( cols != null && (cols.length == 1 || cols.length == 2)) {
							if( cols.length == 1 ) {
								// 这唯一的一列就是  订单号 或者包裹单号  只用修改对应的订单的状态
								Object temp = claimsVerificationService.getOrderByPcodeOrOcodeConsiderArea(cols[0],wareService, istockService, wareArea);
								if( temp instanceof String ) {
									tip += "\r\n" + "订单号或包裹单号" + cols[0] + temp;
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
									ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
									if(vorder.getStatus() == 11 ) {
										//11 是 已退回
										if( isPCode ) {
											tip += "\r\n" + "包裹单"+ cols[0] + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "\r\n" + "订单"+ vorder.getCode() + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									} else if ( rpBean != null ) {
										//说明是未处理的包裹单
										if (rpBean.getStatus() == 0) {
											boolean result=false;
											Date importTime=new Date();
											result = claimsVerificationService
													.updateXXX("import_time='"+DateUtil.formatTime(importTime)+"',area="+wareArea, " id="+rpBean.getId(), "returned_package");
											if( !result ) {
												tip += "\r\n" + "包裹单" + cols[0] + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}else{
												tip += "\r\n" + "订单" + vorder.getCode() + "添加退货包裹单成功！";
												claimsVerificationService.getDbOp().commitTransaction();
											    continue;
											}
										}
										if( isPCode && rpBean.getStatus() != 0) {
											tip += "\r\n" + "包裹单" + cols[0] + "已导入，请勿重复导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else if( rpBean.getStatus() != 0) {
											tip += "\r\n" + "订单" + vorder.getCode() + "已导入，请勿重复导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									} else {
										String addRet = claimsVerificationService.addReturnedPackageInfoBref(user,vorder, -1, wareArea, wareService);
										if( !addRet.equals("SUCCESS") ) {
											if( isPCode ) {
												tip += "\r\n" + "包裹单" + cols[0] + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											} else {
												tip += "\r\n" + "订单" + vorder.getCode() + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}
										} else {
											if (!packageLog.addReturnPackageLog("导入第三方物流交接单", user, vorder.getCode())) {
												if( isPCode ) {
													tip += "\r\n" + "包裹单" + cols[0] + "添加退货日志失败！";
													statService.getDbOp().rollbackTransaction();
													failCount += 1;
													continue;
												} else {
													tip += "\r\n" + "订单" + vorder.getCode() + "添加退货日志失败！";
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
								Object temp = claimsVerificationService.getOrderByPcodeOrOcodeConsiderArea(cols[0],wareService, istockService, wareArea);
								if( temp instanceof String ) {
									tip += "\r\n" + "订单号或包裹单号" + cols[0] + temp;
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
									ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
									if(vorder.getStatus() == 11 ) {
										//11 是 已退回
										if( isPCode ) {
											tip += "\r\n" + "包裹单"+ cols[0] + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else {
											tip += "\r\n" + "订单"+ vorder.getCode() + "已经入库，无需再导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										}
									} else if ( rpBean != null ) {
										//说明是未处理的包裹单
										if (rpBean.getStatus() == 0) {
											boolean result=false;
											Date importTime=new Date();
											result = claimsVerificationService
													.updateXXX("import_time='"+DateUtil.formatTime(importTime)+"',area="+wareArea, " id="+rpBean.getId(), "returned_package");
											if( !result ) {
												tip += "\r\n" + "包裹单" + cols[0] + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}else{
												tip += "\r\n" + "订单" + vorder.getCode() + "添加退货包裹单成功！";
												claimsVerificationService.getDbOp().commitTransaction();
												continue;
											}
										}
										
										
										
										if( isPCode&&rpBean.getStatus() != 0 ) {
											tip += "\r\n" + "包裹单" + cols[0] + "已导入，请勿重复导入！";
											statService.getDbOp().rollbackTransaction();
											failCount += 1;
											continue;
										} else if(rpBean.getStatus() != 0){
											tip += "\r\n" + "订单" + vorder.getCode() + "已导入，请勿重复导入！";
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
												tip +="\r\n" +  "包裹单" + cols[0] + "的导入退货原因条码有误！";
											} else {
												tip +="\r\n" +  "订单" + vorder.getCode() + "的导入退货原因条码有误！";
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
												tip += "\r\n" + "包裹单" + cols[0] + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											} else {
												tip += "\r\n" + "订单" + vorder.getCode() + "添加退货包裹单时失败！";
												statService.getDbOp().rollbackTransaction();
												failCount += 1;
												continue;
											}
										} else {
											if (!packageLog.addReturnPackageLog("导入第三方物流交接单", user, vorder.getCode())) {
												if( isPCode ) {
													tip += "\r\n" + "包裹单" + cols[0] + "添加退货日志失败！";
													statService.getDbOp().rollbackTransaction();
													failCount += 1;
													continue;
												} else {
													tip += "\r\n" + "订单" + vorder.getCode() + "添加退货日志失败！";
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
							tip += "\r\n" + "第" + count + "导入格式不正确！";
							statService.getDbOp().rollbackTransaction();
							failCount += 1;
							continue;
						}
						statService.getDbOp().commitTransaction();
						statService.getDbOp().getConn().setAutoCommit(true);
					}
					request.setAttribute("totalCount", count+"");
					request.setAttribute("failCount", failCount+"");
					request.setAttribute("successCount", successCount+"");
					if( tip.length() == 0 ) {
						request.setAttribute("tip", "提交修改已全部操作成功！ ");
						request.setAttribute("url", request.getContextPath()+"/admin/cargo/importThirdPartyReturnedPackage.jsp");
						return mapping.findForward("tip");
					} else {
						return new ActionForward("/admin/cargo/importThirdPartyReturnedPackage.jsp?errorMsg="+tip);
					}
				}
			}
		} catch ( Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return new ActionForward("/admin/cargo/importThirdPartyReturnedPackage.jsp?errorMsg="+tip);
	}
	
	
	/**
	 * 
	 * 查询包裹核查单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward getReturnPackageCheckInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
		String orderCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("orderCode")));
		String packageCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("packageCode")));
		String userName = StringUtil.toSql(StringUtil.convertNull(request.getParameter("userName")));
		String checkTime = StringUtil.toSql(StringUtil.convertNull(request.getParameter("checkTime")));
		int type = StringUtil.toInt(request.getParameter("resultType"));
		int status = ProductWarePropertyService.toInt(request.getParameter("status"));
		
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		int countPerPage = 20;
		Iterator itr = ProductStockBean.areaMap.keySet().iterator();
		String availAreaIds = "100";
		for(; itr.hasNext(); ) {
			availAreaIds += "," + itr.next();
		}
		List returnPackageCheckList = new ArrayList();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			PagingBean paging = null;
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
				params.append("&packageCode=" + packageCode);
			}
			if(orderCode != null && !orderCode.equals("")){
				sql.append(" and order_code='");
				sql.append(orderCode);
				sql.append("'");
				sqlCount.append(" and order_code='");
				sqlCount.append(orderCode);
				sqlCount.append("'");
				params.append("&orderCode=" + orderCode);
			}
			if (userName != null && !userName.equals("")) {
				sql.append(" and check_user_name='");
				sql.append(userName);
				sql.append("'");
				sqlCount.append(" and check_user_name='");
				sqlCount.append(userName);
				sqlCount.append("'");
				params.append("&userName=" + userName);
			}
			 
			if( wareArea == -1 ) {
				sql.append(" and area in (" + availAreaIds + ")");
				sqlCount.append(" and area in (" + availAreaIds + ")");
			} else {
				sql.append(" and area = " + wareArea);
				sqlCount.append(" and area = " + wareArea);
				params.append("&wareArea="+wareArea);
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
				params.append("&checkTime=" + checkTime );
			}
			
			if (status != -1 ) {
				sql.append(" and status=");
				sql.append(status);
				sqlCount.append(" and status=");
				sqlCount.append(status);
				params.append("&status=" + status);
			}
			
			if (type != -1 ) {
				sql.append(" and type=");
				sql.append(type);
				sqlCount.append(" and type=");
				sqlCount.append(type);
				params.append("&type=" + type);
			}
			
			
			
			int totalCount = claimsVerificationService.getReturnPackageCheckCount(sqlCount.toString());
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			returnPackageCheckList = claimsVerificationService.getReturnPackageCheckList(sql.toString(), paging.getCurrentPageIndex()*countPerPage, countPerPage, "id desc");
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
				/*for( int j = 0; j < orderStockProductList.size(); j ++  ) {
					OrderStockProductBean ospBean = (OrderStockProductBean) orderStockProductList.get(j);
					voProduct product = claimsVerificationService.getProductWithOriName(ospBean.getProductId());
					ospBean.setProduct(product);
				}*/
			}
			
			paging.setPrefixUrl("claimsVerificationAction.do?method=getReturnPackageCheckInfo" + params.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("list", returnPackageCheckList);	
			request.setAttribute("recordNum", totalCount+"");
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("returnPackageCheckInfo");
	}
	
	/**
	 * 
	 * 检查包裹
	 * @return
	 */
	public ActionForward dealReturnPackageCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int targetStatus = StringUtil.toInt(request.getParameter("targetStatus"));
		int targetId = StringUtil.toInt(request.getParameter("targetId"));
		try {
			synchronized ( lock ) {
				ReturnPackageCheckBean rpcBean = claimsVerificationService.getReturnPackageCheck("id=" + targetId);
				if( rpcBean != null ) {
					if( targetStatus == ReturnPackageCheckBean.STATUS_DEALING ) {
						//改为处理中
						if( !group.isFlag(771) ) {
							request.setAttribute("tip", "您没有异常处理操作--处理权限！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_UNDEAL) {
							request.setAttribute("tip", "当前包裹不是未处理状态，不能改为已处理！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_DEALING, "id=" + rpcBean.getId())) {
								request.setAttribute("tip", "更新状态时，数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						} 
					} else if( targetStatus == ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS ) {
						//审核通过
						if( !group.isFlag(772) ) {
							request.setAttribute("tip", "您没有异常处理操作--审核权限！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_DEALING) {
							request.setAttribute("tip", "当前包裹不是处理中状态，不能改为已审核！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS, "id=" + rpcBean.getId())) {
								request.setAttribute("tip", "更新状态时，数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					} else if ( targetStatus == ReturnPackageCheckBean.STATUS_AUDIT_FAIL ) {
						//审核不通过
						if( !group.isFlag(772) ) {
							request.setAttribute("tip", "您没有异常处理操作--审核权限！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_DEALING) {
							request.setAttribute("tip", "当前包裹不是处理中状态，不能改为审核不通过！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_AUDIT_FAIL, "id=" + rpcBean.getId())) {
								request.setAttribute("tip", "更新状态时，数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					} else if ( targetStatus == ReturnPackageCheckBean.STATUS_COMPLETE ) {
						//完成
						if( !group.isFlag(773) ) {
							request.setAttribute("tip", "您没有异常处理操作--确认完成权限！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( rpcBean.getStatus() != ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS) {
							request.setAttribute("tip", "当前包裹不是已审核状态，不能改为已完成！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							if( !claimsVerificationService.updateReturnPackageCheck("status=" + ReturnPackageCheckBean.STATUS_COMPLETE, "id=" + rpcBean.getId())) {
								request.setAttribute("tip", "更新状态时，数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}			
				} else {
					request.setAttribute("tip", "没有找到对应的包裹核查记录！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return new ActionForward("/admin/claimsVerificationAction.do?method=getReturnPackageCheckInfo");
	}
	
	/***
	 * @Describe 获取包装理赔设置列表
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getClaimsExpenses")
	@ResponseBody
	public EasyuiDataGridJson getClaimsExpenses(HttpServletRequest request,HttpServletResponse response,EasyuiPageBean page){
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		List<ClaimsPackagePriceBean> list = new ArrayList<ClaimsPackagePriceBean>();
		Map<String,String> map = new HashMap<String,String>();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(2058) ) {
			datagrid.setRows(list);
			return datagrid;
		}
		WareService wareService = new WareService(); 
		ClaimsPackagePriceService claimsPackagePriceService = new ClaimsPackagePriceService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());		
		try {
			
			map.put("start", (page.getPage()-1) * page.getRows() + "");
			map.put("count", page.getRows() + "");
			list = claimsPackagePriceService.getclaimsPackagePriceList(map);
			int rowCount = claimsPackagePriceService.getClaimsExpensesCount();
			datagrid.setTotal((long)rowCount);
			datagrid.setRows(list);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			claimsPackagePriceService.releaseAll();
		}		
		return datagrid;
	}
	
	/***
	 * @Describe 保存包装理赔设置
	 * @param request
	 * @param response
	 */
	@RequestMapping("/saveclaimsPackagePrice")
	@ResponseBody
	public Json saveclaimsPackagePrice(HttpServletRequest request,HttpServletResponse response,int id,double price){
		Json j = new Json();	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			j.setSuccess(false);
			return j;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(2058) ) {
			j.setMsg("您没有包装理赔设置的权限！");
			j.setSuccess(false);
			return j;
		}
		WareService wareService = new WareService(); 
		ClaimsPackagePriceService claimsPackagePriceService = new ClaimsPackagePriceService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		try {			
			if(claimsPackagePriceService.findDuplicate(id)){
				j.setMsg("您已经进行过此产品线的设置，请删除后重新添加!");
				j.setSuccess(false);
				return j;
			}
			ClaimsPackagePriceBean cpp = new ClaimsPackagePriceBean();
			cpp.setProductLineId(id);
			cpp.setCreateUserName(user.getUsername());
			cpp.setCreateUserId(user.getId());
			cpp.setPrice(price);			
			cpp.setCreateDateTime(DateUtil.getNow());
			if(claimsPackagePriceService.saveclaimsPackagePrice(cpp)){
				j.setMsg("保存成功!");
				j.setSuccess(true);
			}else{
				j.setMsg("保存失败!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			claimsPackagePriceService.releaseAll();
		}		
		return j;
	}
	
	/***
	 * @Describe 删除包装理赔设置
	 * @param request
	 * @param response
	 */
	@RequestMapping("/deleteclaimsPackagePrice")
	@ResponseBody
	public Json deleteclaimsPackagePrice(HttpServletRequest request,HttpServletResponse response,int id){
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			j.setSuccess(false);
			return j;
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(2058) ) {
			j.setMsg("您没有包装理赔设置的权限！");
			j.setSuccess(false);
			return j;
		}
		WareService wareService = new WareService(); 
		ClaimsPackagePriceService claimsPackagePriceService = new ClaimsPackagePriceService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			
		try {
			if(claimsPackagePriceService.deleteclaimsPackagePrice("id="+id,"claims_package_price")){
				j.setMsg("删除成功!");
				j.setSuccess(true);
			}else{
				j.setMsg("删除失败!");
				j.setSuccess(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			claimsPackagePriceService.releaseAll();
		}		
		return j;
	}
	
}
