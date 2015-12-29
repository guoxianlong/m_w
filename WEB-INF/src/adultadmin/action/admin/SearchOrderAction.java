/**
 * 
 */
package adultadmin.action.admin;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.framework.OrderTypeFrk;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StatUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 *  
 */
public class SearchOrderAction extends BaseAction {

	//	static private String NEARLY_SAME = " between -0.0001 and 0.0001";
	/**
	 *  
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();

		String isExportList = StringUtil.convertNull(request.getParameter("isExportList"));	//是否“导出列表”
		String action = StringUtil.convertNull(request.getParameter("action"));

		boolean isSlow = false;
		String useOrderIndex = "createTimeIdx";

		//订单id字符串
		String idStr = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("idStr")));
		//订单编号
		String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
		//包裹单号码
		String packageNum = StringUtil.convertNull(StringUtil.dealParam(request
				.getParameter("packageNum")));
		//出库单号
		String orderStockCode = StringUtil.convertNull(request.getParameter("orderStockCode"));
		//用户名字
		String name = StringUtil.dealParam(request.getParameter("name"));
		//电话
		String phone = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("phone")));
		//产品名称
		String product = StringUtil.dealParam(request.getParameter("product"));
		//产品编号
		String productCode = StringUtil.dealParam(request.getParameter("productCode"));
		//订单状态
		String orderStatusStr = StringUtil.dealParam(request
				.getParameter("orderStatus"));

		String createDatetime = StringUtil.dealParam(request
				.getParameter("createDatetime"));
		String deliver = StringUtil.dealParam(request.getParameter("deliver")) ;


		int codeAllLike = StringUtil.toInt(request.getParameter("codeAllLike"));
		int nameAllLike = StringUtil.toInt(request.getParameter("nameAllLike"));
		int phoneAllLike = StringUtil.toInt(request.getParameter("phoneAllLike"));
		int orderStatus = StringUtil.StringToId(orderStatusStr);

		//所属产品线
		String[] orderTypes = request.getParameterValues("orderType");
		//购买方式
		int buymode = -1;
		if (request.getParameter("buymode") != null) {
			buymode = StringUtil.toInt(request.getParameter("buymode"));
		}
		//发货订单处理状态
		int stockoutDeal = StringUtil.toInt(request.getParameter("stockoutDeal"));

		String startDate = StringUtil.convertNull(request.getParameter("startDate")).trim();
		if(request.getParameter("startDate") != null && startDate.equals("")){
			startDate=DateUtil.formatDate(DateUtil.rollDate(-2));
			request.setAttribute("startDate", startDate);
		}
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));
		String startDateHour = StringUtil.convertNull(request.getParameter("startDateHour"));
		String endDateHour = StringUtil.convertNull(request.getParameter("endDateHour"));
		String stockoutStartDate = StringUtil.dealParam(request.getParameter("stockoutStartDate"));
		String stockoutEndDate = StringUtil.dealParam(request.getParameter("stockoutEndDate"));

		int sortType = StringUtil.StringToId(request.getParameter("sortType"));

		//根据库存操作查询的条件
		String stockCondition = null;
		String stockDate = StringUtil.dealParam(request
				.getParameter("stockDate"));

		String checkOutStartDate = StringUtil.convertNull(request.getParameter("checkOutStartDate"));
		String checkOutEndDate = StringUtil.convertNull(request.getParameter("checkOutEndDate"));
		
		List orderTypeList = OrderTypeFrk.getOrderTypeList();
		request.setAttribute("orderTypeList", orderTypeList);


		if (stockDate != null) { //*****
//			DbOperation dbOp = new DbOperation();
//
//			dbOp.init("adult_slave");
//
//			String sql = "select order_id from order_stock where last_oper_time >= '"
//				+ stockDate + "' and last_oper_time <= '"+stockDate+" 23:59:59' and status in (2,4,6,7,8) ";
//			int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
//			if (stockArea >= 0) {
//				sql += " and stock_area = " + stockArea;
//			}
//			if(deliver!=null && !deliver.trim().equals("")){
//				sql += " and deliver = " + deliver;
//			}
////			ResultSet rs = dbOp.executeQuery(sql + " order by order_id asc limit 1");
////			int orderId = 0;
////			if (rs.next()) {
////				orderId = rs.getInt(1);
////			}
////			rs.close();
////			dbOp.release();
//
////			if (orderId > 0) {
//			stockCondition = " a.id in (" +  + ")";
			stockCondition = " os.last_oper_time >= '"+ stockDate + "' and os.last_oper_time <= '"+stockDate+" 23:59:59' and os.status in (2,4,6,7,8)";
			int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
			if (stockArea >= 0) {
				stockCondition += " and os.stock_area = " + stockArea;
			}
			if(deliver!=null && !deliver.trim().equals("")){
				stockCondition += " and os.deliver = " + deliver;
			}
			useOrderIndex = "";
//			}
			
			isSlow = true;
		}

		String condition = null;
		StringBuilder buf = new StringBuilder(256);

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IAdminService service = ServiceFactory.createAdminService(dbOp);
		try{
			//根据库存操作查询
			if (stockCondition != null) {
				buf.append(" and ");
				buf.append(stockCondition);
			}
			boolean hasCode=false;//如果 根据编号查询订单 并满足长度为8 不是模糊查询 侧去掉时间查询条件  ls
			if (code != null && code.length() > 0) {
				if (code.indexOf(",") != -1) {
					String[] codes = code.split(",");
					if (codes != null && codes.length > 0) {
						buf.append(" and (");
						for (int i = 0; i < codes.length; i++) {
							if (i != 0) {
								buf.append(" or ");
							}
							if(codeAllLike == 1){
								buf.append(" a.code like '%");
							} else {
								buf.append(" a.code like '");
							}
							buf.append(codes[i]);
							buf.append("%' ");
						}
						buf.append(") ");
					}
				} else {
					if(codeAllLike == 1){
						buf.append(" and a.code like '%");
					} else {
						buf.append(" and a.code like '");
						if(code.length()>=8){
							hasCode=true;
						}
					}
					buf.append(code);
					buf.append("%' ");
				}
				useOrderIndex = "";
			}

			if (packageNum != null && packageNum.length() > 0) {
				if (packageNum.indexOf(",") != -1) {
					String[] packageNums = packageNum.split(",");
					if (packageNums != null && packageNums.length > 0) {
						buf.append(" and (");
						for (int i = 0; i < packageNums.length; i++) {
							if (i != 0) {
								buf.append(" or ");
							}
							buf.append(" mb.packagenum = '");
							buf.append(packageNums[i]);
							buf.append("' ");
						}
						buf.append(") ");
					}
				} else {
					buf.append(" and mb.packagenum = '");
					buf.append(packageNum);
					buf.append("' ");
				}
				useOrderIndex = "";
			}
			
			if (orderStockCode != null && orderStockCode.length() > 0) {
				if (orderStockCode.indexOf(",") != -1) {
					String[] orderStockCodes = orderStockCode.split(",");
					if (orderStockCodes != null && orderStockCodes.length > 0) {
						buf.append(" and (");
						for (int i = 0; i < orderStockCodes.length; i++) {
							if (i != 0) {
								buf.append(" or ");
							}
							buf.append(" os.code = '");
							buf.append(orderStockCodes[i]);
							buf.append("' ");
						}
						buf.append(") ");
					}
				} else {
					buf.append(" and os.code = '");
					buf.append(orderStockCode);
					buf.append("' ");
				}
				useOrderIndex = "";
			}
			
			if(orderTypes != null && orderTypes.length > 0){
				int orderType = StringUtil.StringToId(orderTypes[0]);
				if(orderType > 0){
					String types = "";
					for(int i=0;i<orderTypes.length;i++){
						types = types + orderTypes[i] + ",";
					}
					if(types.endsWith(",")){
						types = types.substring(0,types.length()-1);
					}
					buf.append(" and a.order_type in (");
					buf.append(types);
					buf.append(")");
				}
			}
			if (phone != null && phone.length() > 0) {
				if (phone.indexOf(",") != -1) {
					String[] phones = phone.split(",");
					if (phones != null && phones.length > 0) {
						buf.append(" and (");
						for (int i = 0; i < phones.length; i++) {
							if (i != 0) {
								buf.append(" or ");
							}
							if(phoneAllLike == 1){
								buf.append(" a.phone like '%");
							} else {
								buf.append(" a.phone = '");
							}
							buf.append(phones[i]);
							buf.append("' ");
						}
						buf.append(") ");
					}
				} else {
					if(phoneAllLike == 1){
						buf.append(" and a.phone like '%");
					} else {
						buf.append(" and a.phone = '");
					}
					buf.append(phone);
					buf.append("' ");
				} 
				useOrderIndex = "";
			}
			if (name != null && name.length() > 0) {
				if (name.indexOf(",") != -1) {
					String[] names = name.split(",");
					if (names != null && names.length > 0) {
						buf.append(" and (");
						for (int i = 0; i < names.length; i++) {
							if (i != 0) {
								buf.append(" or ");
							}
							if(nameAllLike == 1){
								buf.append(" a.name like '%");
							} else {
								buf.append(" a.name like '");
							}
							buf.append(names[i]);
							buf.append("%' ");
						}
						buf.append(") ");
					}
				} else {
					if(nameAllLike == 1){
						buf.append(" and a.name like '%");
					} else {
						buf.append(" and a.name like '");
					}
					buf.append(name);
					buf.append("%' ");
				}
				isSlow = true;
			}

			if (createDatetime != null && createDatetime.length() >= 10) {
				buf.append(" and a.create_datetime >= '");
				buf.append(createDatetime);
				buf.append("' and a.create_datetime <= '");
				buf.append(createDatetime);
				buf.append(" 23:59:59'");
			}
			if (orderStatusStr != null && orderStatus >= 0) {
				//condition += " and status = " + orderStatus;
				buf.append(" and a.status = ");
				buf.append(orderStatus);
			}
			if (startDate != null && startDate.length() > 0 && !hasCode) {
				buf.append(" and a.create_datetime >= '");
				buf.append(startDate);
				if(startDateHour.equals("")){
					buf.append(" 00:00:00'");
				}else{
					buf.append(" "+startDateHour);
					buf.append(":00:00' ");
				}
			}
			if (endDate != null && endDate.length() > 0) {
				buf.append(" and a.create_datetime <= '");
				buf.append(endDate);
				if(endDateHour.equals("")){
					buf.append(" 23:59:59'");
				}else{
					buf.append(" "+ endDateHour);
					buf.append(":59:59' ");
				}
			}
			//根据时间间隔长短， 确定是否为慢查询
			if((startDate != null && startDate.length() > 0)&&(endDate != null && endDate.length() > 0)){
				if(DateUtil.getDaySub(startDate, endDate)>5 && phone.equals("") && code.equals("")){
					isSlow = true;
				}
			}
			if((stockoutStartDate != null && stockoutStartDate.length() > 0) || (stockoutEndDate != null && stockoutEndDate.length() > 0)){
				buf.append(" and os.status <> 3");
				if (stockoutStartDate != null && stockoutStartDate.length() > 0) {
					buf.append(" and os.create_datetime >= '");
					buf.append(stockoutStartDate);
					buf.append("' ");
				}
				if (stockoutEndDate != null && stockoutEndDate.length() > 0) {
					buf.append(" and os.create_datetime <= '");
					buf.append(stockoutEndDate);
					buf.append(" 23:59:59' ");
				}
				useOrderIndex = "";
			}
			//根据时间间隔长短， 确定是否为慢查询
			if((stockoutStartDate != null && stockoutStartDate.length() > 0)&&(stockoutEndDate != null && stockoutEndDate.length() > 0)){
				if(DateUtil.getDaySub(stockoutStartDate, stockoutEndDate)>5 && phone.equals("") && code.equals("")){
					isSlow = true;
				}
			}
			if (stockoutDeal >= 0){
				buf.append(" and a.stockout_deal=" + stockoutDeal);
			}

			if (product != null && product.length() > 0) {
				ResultSet rs = service.getDbOperation().executeQuery("select group_concat(id) s from product where name like '%"+product+"%' or oriname like '%"+product+"%'");
				String productIds = "";
				if(rs.next()){
					productIds = rs.getString(1);
				}
				if(productIds.equals("")){
					return mapping.findForward(IConstants.SUCCESS_KEY);
				}

				buf.append(" and (a.id in (select uop.order_id from user_order_product uop,product p where uop.product_id=p.id and (p.id in (");
				buf.append(productIds);
				buf.append(") )) or a.id in (select uop.order_id from user_order_present uop,product p where uop.product_id=p.id and (p.id in (");
				buf.append(productIds);
				buf.append(") )) )");
				isSlow = true;
			}
			if (productCode != null && productCode.length() > 0) {
				ResultSet rs = service.getDbOperation().executeQuery("select id from product where code = '"+productCode+"'");
				int productId = 0;
				if(rs.next()){
					productId = rs.getInt(1);
				}
				if(productId == 0){
					return mapping.findForward(IConstants.SUCCESS_KEY);
				}

				buf.append(" and (a.id in (select uop.order_id from user_order_product uop,product p where uop.product_id=p.id and (p.id = ");
				buf.append(productId);
				buf.append(" )) or a.id in (select uop.order_id from user_order_present uop,product p where uop.product_id=p.id and (p.id = ");
				buf.append(productId);
				buf.append(" )) )");
			}

			if(request.getParameter("idStr") != null){	//根据id查询用来导出到excel
				buf.append(" and a.id in (");
				buf.append(idStr);
				buf.append(")");
				useOrderIndex = "";
			}
			if(!checkOutStartDate.equals("") && !checkOutEndDate.equals("")){
				buf.append(" and mb.stockout_datetime between '").append(checkOutStartDate).
				append(" 00:00:00' and '").append(checkOutEndDate).append(" 23:59:59' ");
				useOrderIndex = "";
			}

			if (buf.length() == 0)
				return mapping.findForward(IConstants.SUCCESS_KEY);

			buf.append(" group by a.id ");

			if(action.equals("exportStock")){
				buf.append(" order by a.consigner desc, a.id desc");
			}else{
				if (sortType == 0) {
					//condition += " order by a.id desc";
					buf.append(" order by a.id desc");
				} else if(sortType == 1) {
					//condition += " order by CONVERT( a.name USING gbk ) asc, a.id
					// desc";
					buf.append(" order by CONVERT( a.name USING gbk ) asc, a.id desc");
				} else if(sortType == 2) {
					buf.append(" order by a.last_deal_time asc, a.id desc");
				} else if(sortType == 3) {
					buf.append(" order by a.last_deal_time desc, a.id desc");
				} 
			}
			condition = buf.toString();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}

		//根据条件判断索引使用
//		if(!phone.trim().equals("") || !code.trim().equals("") || !packageNum.trim().equals("")){
//			useOrderIndex = "";
//		}
		
		dbOp = new DbOperation();
		if(isSlow){
			dbOp.init("adult_slave2");
		}else{
			dbOp.init("adult_slave");
		}
		service = ServiceFactory.createAdminService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,service.getDbOperation());
		try {

			List list = null;

			if(action.equals("exportStock")){
				if(group.isFlag(303)){
					list = service.searchOrder2(condition + " limit 20000 ",useOrderIndex);
				} else {
					list = service.searchOrder2(condition + " limit 2000 ",useOrderIndex);
				}
				if(phone!=null && !phone.equals("")){
					list.addAll(service.searchOrder2(" and uopi.phone='"+phone+"'"));
				}
			}else{
				if(group.isFlag(303)){
					list = service.searchOrder(condition + " limit 20000 ",useOrderIndex);
				} else {
					list = service.searchOrder(condition + " limit 2000 ",useOrderIndex);
				}
				if(phone!=null && !phone.equals("")){
					list.addAll(service.searchOrder(" and uopi.phone='"+phone+"'"));
				}
				//库存状态
				Iterator itr = list.iterator();
				voOrder order = null;
				while (itr.hasNext()) {
					order = (voOrder) itr.next();
					order.setOrderStock(stockService.getOrderStock("status <> " + OrderStockBean.STATUS4 + " and order_code='" + order.getCode() + "'"));
				}
			}

			for(int i=0;i<list.size();i++){
				voOrder order=(voOrder)list.get(i);
				AuditPackageBean apBean=stockService.getAuditPackage("order_id="+order.getId());
				order.setAuditPakcageBean(apBean);
			}

			request.setAttribute("orderList", list);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbOp.release();
		}

		if(action.equals("exportStock")){
			return mapping.findForward("exportStock");
		}
		if(isExportList.equals("1")){
			return mapping.findForward("searchOrderExportList");
		}
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
    
}