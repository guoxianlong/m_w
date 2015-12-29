/**
 *
 */
package adultadmin.action.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleRefundOrderBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.UserOrderCommonPropertiesBean;
import adultadmin.bean.sms.OrderDealBean;
import adultadmin.bean.sms.OrderMessageBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.SMSServiceImpl;
import adultadmin.service.impl.UserOrderServiceImpl;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.ISMSService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.IUserOrderService;
import adultadmin.util.DateUtil;
import adultadmin.util.ForderUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 *
 */
public class FOrderAction  extends BaseAction{

	public static int SAME_PHONE_MAX_DAY = 60;

	public static int OLDUSER_SAME_PHONE_MAX_DAY = 365;
	/**
	 *
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		voUser loginUser = (voUser)request.getSession().getAttribute("userView");
		UserGroupBean group = loginUser.getGroup();

		int id = StringUtil.StringToId(request.getParameter("id"));
		int split = StringUtil.StringToId(request.getParameter("split"));
		String needStockout = request.getParameter("needStockout");

		Integer forderId = (Integer)request.getSession().getAttribute("forderId");
		if(forderId != null && id == 0) {
			id = forderId.intValue();
		}
		if(id > 0){
			request.getSession().setAttribute("forderId", Integer.valueOf(id));
		}

		WareService service = new WareService();
		DbOperation dbSMS = new DbOperation();
		dbSMS.init(DbOperation.DB_SMS);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IAfterSalesService afService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
        ISMSService smsService = new SMSServiceImpl(IBaseService.CONN_IN_SERVICE,dbSMS);
        IUserOrderService userOrderService = new UserOrderServiceImpl(IBaseService.CONN_IN_SERVICE,service.getDbOp());

		try {
			List statusList = service.getSelects("user_order_status", "where visible=1 order by sec asc, id asc");
			request.setAttribute("statusList", statusList);

			List list = null;
			List list1 = null;
			List presentList = null;
			if(split == 1){
				list = service.getOrderProductsSplit(id);
				presentList = service.getOrderPresentsSplit(id);
			} else {
				list = service.getOrderProducts(id);//user_order_product
				list1 = service.getOrderProducts1(id);//user_order_promotion_product
				presentList = service.getOrderPresents(id);
				list = ForderUtil.getOrderPorducts(service.getDbOp(), list, list1, id);
				if(list == null){
					list = service.getOrderProducts1(id);
				}
			}
			voOrder vo = service.getOrder(id);
			if(vo == null){
	            String tip = "对不起，找不到该订单!";
	            request.setAttribute("tip", tip);
	            return mapping.findForward(IConstants.FAILURE_KEY);
			}

			if(vo.getCode().startsWith("T") && !group.isFlag(190)){
				String tip = "对不起，你没有权限修改团购订单!";
	            request.setAttribute("tip", tip);
	            return mapping.findForward(IConstants.FAILURE_KEY);
			}

            Iterator iter = list.listIterator();
//            String now = DateUtil.getNow();
//            Map discountProductMap = new HashMap();
//            Map perferenceProductMap = new HashMap();
//            Map promotionProductMap = new HashMap();
//            Map groupRateMap = new HashMap();
//            Map buyGiveMap = new HashMap();
//            PackageRuleBean ruleBean = null;
//            ProductDiscountPriceBean pdpBean = null;
//            ProductPreferenceBean ppBean = null;
//            String grpPrice = "";
            while(iter.hasNext()){
            	voOrderProduct product = null;
            	product = (voOrderProduct) iter.next();
            	product.setPsList(psService.getProductStockList("product_id=" + product.getProductId(), -1, -1, null));
            }
            iter = presentList.listIterator();
            while(iter.hasNext()){
            	voOrderProduct product = null;
            	product = (voOrderProduct) iter.next();
            	product.setPsList(psService.getProductStockList("product_id=" + product.getProductId(), -1, -1, null));
            }

			String condition = "";
			if(vo.getUserId() > 0){
			    condition += " and user_id = " + vo.getUserId();
			}
			if(vo.getPhone() != null && vo.getPhone().length() > 0){
				condition += " and phone='" + vo.getPhone() + "' ";
			}
			List cardList = null;

            vo.setOrderStock(stockService.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and order_code='" + vo.getCode() + "'"));

            if(vo.getOrderStock() == null && needStockout != null){
            	request.setAttribute("needStockout", needStockout);
            }

            condition = "order_code ='"+vo.getCode()+"' and type = "+AfterSaleRefundOrderBean.TYPE_REFUND ;
            List refundList =afService.getAfterSaleRefundOrderList(condition, -1, -1, "id desc");
            condition = "order_code ='"+vo.getCode()+"' and type = "+AfterSaleRefundOrderBean.TYPE_NIFFER ;
            List nifferList =afService.getAfterSaleRefundOrderList(condition, -1, -1, "id desc");
            request.setAttribute("nifferList", nifferList);
            request.setAttribute("refundList", refundList);
            UserOrderCommonPropertiesBean uocpBean = userOrderService.getUserOrderCommonProperties("order_id ="+id);
            
            if(vo.getStockoutDeal() >= 4){
            	List allProductList = new ArrayList();
            	allProductList.addAll(list);
            	allProductList.addAll(presentList);
            	Map map = new HashMap();
            	for(Iterator it= allProductList.iterator();it.hasNext();){
            		voOrderProduct orderProduct = (voOrderProduct)it.next();
            		if(orderProduct.getStock(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_QUALIFIED)==0){
            			map.put(String.valueOf(orderProduct.getProductId()),getLackProductInfo(service,orderProduct));
            		}
            	}
            	 request.setAttribute("lackMap", map);
            }
            
            request.setAttribute("cardList", cardList);
			request.setAttribute("opList", list);
			request.setAttribute("presentList", presentList);
//			request.setAttribute("pdMap", discountProductMap);
//			request.setAttribute("promotionProductMap", promotionProductMap);
//			request.setAttribute("perferenceProductMap", perferenceProductMap);
//			request.setAttribute("groupRateMap", groupRateMap);
//			request.setAttribute("buyGiveMap", buyGiveMap);
			request.setAttribute("uocpBean", uocpBean);

			List orderDealList = smsService.getOrderDealList("order_id=" + vo.getId() + " and type in (0, 1,3)", -1, -1, "id desc");
			request.setAttribute("orderDealList", orderDealList);
			String cond = "order_id=" + vo.getId() + " and type = 2";
			OrderDealBean od = smsService.getOrderDeal(cond);
			OrderMessageBean om = smsService.getOrderMessage(cond);
			request.setAttribute("orderMessage", om);
			request.setAttribute("orderDeal", od);

        	float orderDprice = vo.getDprice() - vo.getPostage();
        	request.setAttribute("orderDprice", StringUtil.formatFloat(orderDprice));

	        request.setAttribute("order", vo);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			service.releaseAll();
			smsService.releaseAll();
		}
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
	
	/**
	 * 1、增城待验库库存量
		2、在途量
		3、预计到货时间
		4、采购负责人
		5广州进货周期
	 * 功能:
 	 * @param adminService
	 * @param orderProduct
	 * @return
	 */
	public static String[] getLackProductInfo(WareService wareService,voOrderProduct orderProduct){
		String[] temp = new String[5];
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int buyCountGD = 0;
		String condition = "product_id="+orderProduct.getProductId()+" and buy_order_id in (select id from buy_order where " +
						"status = "+BuyOrderBean.STATUS3+" or status ="+BuyOrderBean.STATUS5+")";
		ArrayList bopList = service.getBuyOrderProductList(condition, -1, -1, null);
		Iterator bopIterator = bopList.listIterator();
		while(bopIterator.hasNext()){
			BuyOrderProductBean bop = (BuyOrderProductBean)bopIterator.next();
			buyCountGD += (bop.getOrderCountGD()-bop.getStockinCountGD()-bop.getStockinCountBJ())>0?(bop.getOrderCountGD()-bop.getStockinCountGD()-bop.getStockinCountBJ()):0;
		}
		
		//如果在途量大于0，需要查找  该采购产品的         预计到货时间 及 采购负责人
		String expectArrivalDatetime = "";
		String createUserName = "";
		if(buyCountGD>0){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<bopList.size();i++){
				BuyOrderProductBean bop = (BuyOrderProductBean)(bopList.get(i));
				sb.append(bop.getBuyOrderId()+",");
			}
			String buyOrderIds = null;
			if(sb.length()>0){
				buyOrderIds = sb.substring(0, sb.length()-1);
			}
			if(buyOrderIds!=null&&buyOrderIds.trim().length()>0){
				BuyStockBean buyStock =  service.getBuyStock(" buy_order_id in ("+buyOrderIds +") and expect_arrival_datetime >= '" + DateUtil.getNow()+"' and status != "+BuyStockBean.STATUS8+" and status != "+BuyStockBean.STATUS6+ " order by expect_arrival_datetime asc limit 0,1 ");
				if(buyStock==null){
					buyStock =  service.getBuyStock(" buy_order_id in ("+buyOrderIds +") and status != "+BuyStockBean.STATUS8+" and status != "+BuyStockBean.STATUS6+ " order by expect_arrival_datetime desc limit 0,1 ");
				}
				if(buyStock != null){
					expectArrivalDatetime = buyStock.getExpectArrivalDatetime();
					if(expectArrivalDatetime!=null&&expectArrivalDatetime.trim().length()>0){
						expectArrivalDatetime = expectArrivalDatetime.substring(0,10);
					}else{
						expectArrivalDatetime = "";
					}
					//操作人
					voUser creatUser = adminService.getAdmin(buyStock.getCreateUserId());
					if(creatUser!=null){
						createUserName = creatUser.getUsername();
					}
				}
			}
		}
		temp[0] = String.valueOf(orderProduct.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK));
		temp[1]=String.valueOf(buyCountGD);
		temp[2]=expectArrivalDatetime;
		temp[3]=createUserName;
		temp[4]=orderProduct.getGdStockin();
		return temp;
	}
}
