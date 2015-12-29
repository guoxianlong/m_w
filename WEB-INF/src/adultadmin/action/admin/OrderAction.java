/**
 *
 */
package adultadmin.action.admin;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleRefundOrderBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.UserServiceImpl;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.ForderUtil;
import adultadmin.util.StringUtil;

/**
 * @author Bomb
 *
 */
public class OrderAction extends BaseAction {

    /**
     *
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	voUser loginUser = (voUser)request.getSession().getAttribute("userView");
    	UserGroupBean group = loginUser.getGroup();
    	if(!group.isFlag(441)){
    		request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
    	}
        int id = StringUtil.StringToId(request.getParameter("id"));
		int split = StringUtil.StringToId(request.getParameter("split"));

		WareService service = new WareService();
        IUserService userService = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IAfterSalesService afService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
        IUserService uService = new UserServiceImpl(IBaseService.CONN_IN_SERVICE,stockService.getDbOp());
        String code = request.getParameter("code");
        try {

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
				list1 = ForderUtil.getOrderPorducts(service.getDbOp(), list, list1, id);
				if(list1 == null){
					list1 = service.getOrderProducts1(id);
				}
			}

            voOrder vo = service.getOrder(id);
            if(!StringUtil.isNull(code)){
            	vo = service.getOrder("code = '" + StringUtil.toSql(code) +"'");
            	id = vo.getId();
            }
            if( vo == null ) {
            	request.setAttribute("tip", "没有找到订单信息！");
    			request.setAttribute("result", "failure");
    			return mapping.findForward(IConstants.FAILURE_KEY);
            }

            Iterator iter = list.listIterator();
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
            vo.setOrderStock(stockService.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and order_code='" + vo.getCode() + "'"));

            condition = "order_code ='"+vo.getCode()+"' and type = "+AfterSaleRefundOrderBean.TYPE_REFUND ;
            List refundList =afService.getAfterSaleRefundOrderList(condition, -1, -1, "id desc");
            condition = "order_code ='"+vo.getCode()+"' and type = "+AfterSaleRefundOrderBean.TYPE_NIFFER ;
            List nifferList =afService.getAfterSaleRefundOrderList(condition, -1, -1, "id desc");

            request.setAttribute("nifferList", nifferList);
            request.setAttribute("refundList", refundList);
            request.setAttribute("order", vo);
            request.setAttribute("opList", list);
            request.setAttribute("persentList", presentList);


        }catch(Exception e){
        	e.printStackTrace();
        }finally {
            service.releaseAll();
        }
        return mapping.findForward(IConstants.SUCCESS_KEY);
    }
}
