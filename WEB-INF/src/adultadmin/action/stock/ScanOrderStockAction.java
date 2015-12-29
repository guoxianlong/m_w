/*
 * Created on 2009-5-8
 *
 */
package adultadmin.action.stock;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.mmb.delivery.domain.model.vo.DeliverRelationInfoBean;

import cache.FinanceCache;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.supplier.SupplierCityBean;
import adultadmin.framework.BaseAction;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

/**
 *  <code>ScanOrderStockAction.java</code>
 *  <p>功能:根据条码复核订单
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-1-13 下午02:05:25	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class ScanOrderStockAction extends BaseAction{
	
	private String date = DateUtil.formatDate(new Date());
public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			return mapping.findForward("failure");
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(294)){
	 		request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
            return mapping.findForward("failure");
	 	}
		int scanFlag= StringUtil.StringToId(request.getParameter("scanFlag"));
	    String orderCode = StringUtil.dealParam(request.getParameter("orderCode"));
	    String checkType = StringUtil.checkNull(request.getParameter("checkType"));
	    String orderstock = StringUtil.dealParam(request.getParameter("orderstock"));
	    IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
	    ISupplierService supplyService = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	    IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	    IBatchBarcodeService batchService = ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	    WareService wareService = new WareService(service.getDbOp());
	    IBatchBarcodeService batchBarcodeService = ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE,service.getDbOp());
	    IBalanceService balanceService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
	    SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	    try {
	    	//下面的if语句功能：ajax加载物流员工作业效率排名 
	    	if(StringUtil.convertNull(request.getParameter("selectIndex")).equals("11")){
	    		String firstCount = "";
				String oneselfCount = "";
				String ranking ="";
				String productCount="";
				String photoUrl = Constants.STAFF_PHOTO_URL + "/";
				int n = 1;
				ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
				CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
				if(csBean == null){
					request.setAttribute("tip", "此账号不是物流员工 !");
					return mapping.findForward("failure");
				}
				CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=1");
				List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=1", -1, -1, " oper_count DESC");
				if(cspBean != null){
					for(int i = 0;i < cspList.size();i++){
						CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
						bean = cspList.get(i);
						if(i==0){
							firstCount = bean.getOperCount() + "";
							CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
							if(winBean.getPhotoUrl()==null||winBean.getPhotoUrl().length()==0){//无照片时不显示头像
								photoUrl=null;
							}else{
								photoUrl += winBean.getPhotoUrl();//获取冠军头像的URl
							}
							if(cspBean.getOperCount() >= bean.getOperCount()){
								productCount = cspBean.getProductCount()+"";
								ranking = "排名第" + n ;
								oneselfCount = cspBean.getOperCount()+"";
								break;
							}else{
								n++;
							}
						}else{
							if(cspBean.getOperCount() >= bean.getOperCount()){
								ranking = "排名第" + n ;
								productCount = cspBean.getProductCount()+"";
								oneselfCount = cspBean.getOperCount()+"";
								break;
							}else{
								n++;
							}
						}
					}
				}else{
					if(cspList != null && cspList.size() > 0){
						CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
						bean = cspList.get(0);
						CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
						if(winBean.getPhotoUrl()==null||winBean.getPhotoUrl().length()==0){//无照片时不显示头像
							photoUrl=null;
						}else{
							photoUrl += winBean.getPhotoUrl();//获取冠军头像的URl
						}
						firstCount = bean.getOperCount() + "";
						ranking = "尚无名次";
						oneselfCount = "0";
						productCount = "0";
					}else{
						firstCount = "0";
						photoUrl = null;
						ranking = "尚无名次";
						oneselfCount = "0";
						productCount = "0";
					}
				}
				request.setAttribute("photoUrl", photoUrl);
				request.setAttribute("firstCount", firstCount);
				request.setAttribute("productCount", productCount);
				request.setAttribute("oneselfCount", oneselfCount);
				request.setAttribute("ranking", ranking);
				return mapping.findForward("selection");
	    	}
	        if(orderCode == null || orderCode.length() == 0){
	        	request.setAttribute("tip", "订单编号错误，请重新输入！");
	        	return mapping.findForward("failure");
	        }
	        String codeType="orderCode";
	        if(orderCode.length()>=2&&orderCode.substring(0,2).equals("CK")){//判断输入编号的类型
	        	codeType="orderStockCode";
	        }
	        StringBuffer buf = new StringBuffer(); 
        	if(orderstock!=null && orderstock.equals("orderStock")){
        		if(scanFlag==0){
	        		buf.append(" status = ");
	        		buf.append(OrderStockBean.STATUS6);
					if (codeType.equals("orderCode")) {
						buf.append(" and order_code='");
						buf.append(orderCode);
						buf.append("' ");
					}else{
						buf.append(" and code='");
						buf.append(orderCode);
						buf.append("' ");
					}
	        		String condition = buf.toString();
	        		OrderStockBean bean = service.getOrderStock(condition);
	        		if (bean == null) {
	        			buf = new StringBuffer();
	        			buf.append(" status = ");
	        			buf.append(OrderStockBean.STATUS3);
	        			if (codeType.equals("orderCode")) {
							buf.append(" and order_code='");
							buf.append(orderCode);
							buf.append("' ");
						}else{
							buf.append(" and code='");
							buf.append(orderCode);
							buf.append("' ");
						}
	        			bean = service.getOrderStock(buf.toString());
	        			if(bean!=null){
	        				request.setAttribute("tip", bean.getOrderCode()+" 您扫描的订单已复核过！");
	        				return mapping.findForward("failure");
	        			}
	        			request.setAttribute("tip", "在等待复核的订单列表中，没有您扫描的订单！");
	        			return mapping.findForward("failure");
	        		}
	        		//            condition = "order_stock_id = " + bean.getId();
	        		//ArrayList orderStockProc = service.getOrderStockProductList(condition, 0, -1, "id");
	        		// 跳转到订单出货操作页面
	        		if(!CargoDeptAreaService. hasCargoDeptArea(request, bean.getStockArea(), bean.getStockType())){
	    				request.setAttribute("tip", "只能扫描‘用户所属的库地区’的订单");
	    				return mapping.findForward("failure");
	    			}
	        		if(bean.getDeliver()!=9){
	        			request.setAttribute("tip", "只能复核EMS省外订单！");
        				return mapping.findForward("failure");
	        		}
	        		response.sendRedirect("orderStock/scanOrderStock.jsp?id="+bean.getId());
	        		return null;
        		}else if(scanFlag==1){   // 打印客户信息扫描
        			OrderStockBean orderStockBean = service.getOrderStock("code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
        			voOrder order = null;
        			if(orderStockBean != null){
        				order = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
        			}else{
        				order = wareService.getOrder("code='"+orderCode+"'");
        			}
        			if(order==null){
        				request.setAttribute("tip", "订单信息异常！");
	        			return mapping.findForward("failure");
        			}
	        		OrderCustomerBean orderCustomerBean = batchService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
	        		//voOrder order=adminService.getOrder("a.code='"+orderCode+"'");//对应的订单，用于提取快递公司
	        		if(orderCustomerBean==null){
	        			request.setAttribute("tip", "请先分拣该发货清单后，再打印客户信息！");
	        			return mapping.findForward("failure");
//	        		}else if(orderCustomerBean.getStatus()!=OrderStockBean.STATUS3){
//	        			request.setAttribute("tip", "该发货单请先复核，再打印客户信息！");
//	        			return mapping.findForward("failure");         // 去掉已出货状态判断
	        		}else{
	        			if(order==null){
		        			request.setAttribute("tip", "订单信息异常！");
		        			return mapping.findForward("failure");
		        		}
	        			String deliverName=order.getDeliverName()==null?"":order.getDeliverName();//快递公司名
	        			request.setAttribute("deliverName", deliverName);
	        			request.setAttribute("orderCustomer", orderCustomerBean);
	        			return mapping.findForward("printCusInfo");
	        		}
        		}else if(scanFlag==2){   // 打印包裹单信息扫描  
	        		buf.append(" order_code='");
	        		buf.append(orderCode);
	        		buf.append("' ");
	        		String condition = buf.toString();
	        		OrderStockBean bean = service.getOrderStock(condition);
	        		if(bean==null){
	        			request.setAttribute("tip", "发货单编号不存在！");
	        			return mapping.findForward("failure");
	        		}else if(OrderStockBean.STATUS3!=bean.getStatus()){
	        		 	request.setAttribute("tip", "该发货单请先复核，再打印包裹单！");
		        		return mapping.findForward("failure");
        			}else{
        				bean.setOrder(wareService.getOrder(bean.getOrderId()));
        				if(bean.getOrder()==null){
        				 	request.setAttribute("tip", "没有找到对应的用户订单，请扫描其他订单编号。");
    		        		return mapping.findForward("failure");
        				}
        				request.setAttribute("orderStock", bean);
	        			return mapping.findForward("printPackageInfo");
        			}
        		}else if(scanFlag==3){   // 扫描导入包裹单号  
	        		buf.append(" order_code='");
	        		buf.append(orderCode);
	        		buf.append("' ");
	        		String condition = buf.toString();
	        		OrderStockBean bean = service.getOrderStock(condition);
	        		if(bean==null){
	        			request.setAttribute("tip", "发货单编号不存在！");
	        			return mapping.findForward("failure");
	        		}else if(OrderStockBean.STATUS3!=bean.getStatus()){
	        		 	request.setAttribute("tip", "该发货单请先复核，再打印包裹单！");
		        		return mapping.findForward("failure");
        			}else{
        				request.setAttribute("orderStock", bean);
	        			return mapping.findForward("scanPackageInfo");
        			}
        		}else if(scanFlag==4){//新的复核入口进入复核详细页
        			OrderStockBean orderStockBean = service.getOrderStock("code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
        			voOrder order = null;
        			if(orderStockBean != null){
        				order = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
        			}else{
        				order = wareService.getOrder("code='"+orderCode+"'");
        			}
        			if(order==null){
        				request.setAttribute("tip", "订单号不存在！");
		        		return mapping.findForward("failure");
        			}
        			//判断是否扫描的是大Q订单
        			if("dq".equals(checkType)){
        				if(!order.isDaqOrder()){
        					request.setAttribute("tip", "非大Q订单，请到相应发货清单页面重新扫描！");
    		        		return mapping.findForward("failure");
        				}
        			} else if("lt".equals(checkType)){
        				if(!order.isLTOrder()){
        					request.setAttribute("tip", "非兰亭订单，请到相应发货清单页面重新扫描！");
    		        		return mapping.findForward("failure");
        				}
        			} else {
        				if(order.isDaqOrder() || order.isLTOrder()){
        					request.setAttribute("tip", "非普通订单，请到相应发货清单页面重新扫描！");
    		        		return mapping.findForward("failure");
        				}
        			}
        			buf.append(" status = ");
	        		buf.append(OrderStockBean.STATUS6);
					if (codeType.equals("orderCode")) {
						buf.append(" and order_code='");
						buf.append(order.getCode());
						buf.append("' ");
					}else{
						buf.append(" and code='");
						buf.append(orderCode);
						buf.append("' ");
					}
	        		String condition = buf.toString();
	        		OrderStockBean bean = service.getOrderStock(condition);
	        		if (bean == null) {
	        			buf = new StringBuffer();
	        			buf.append(" status = ");
	        			buf.append(OrderStockBean.STATUS3);
	        			if (codeType.equals("orderCode")) {
							buf.append(" and order_code='");
							buf.append(order.getCode());
							buf.append("' ");
						}else{
							buf.append(" and code='");
							buf.append(orderCode);
							buf.append("' ");
						}
	        			bean = service.getOrderStock(buf.toString());
	        			if(bean!=null){
	        				request.setAttribute("tip", bean.getOrderCode()+" 您扫描的订单已复核过！");
	        				return mapping.findForward("failure");
	        			}
	        			request.setAttribute("tip", "在等待复核的订单列表中，没有您扫描的订单！");
	        			return mapping.findForward("failure");
	        		}
	        		// 跳转到订单出货操作页面
	        		if(!CargoDeptAreaService. hasCargoDeptArea(request, bean.getStockArea(), bean.getStockType())){
	    				request.setAttribute("tip", "只能扫描‘用户所属的库地区’的订单");
	    				return mapping.findForward("failure");
	    			}
	        		AuditPackageBean apBean=service.getAuditPackage("order_code='"+order.getCode()+"'");
	        		if(apBean==null){
	        			request.setAttribute("tip", "订单出库信息错误！");
	        			return mapping.findForward("failure");
	        		}
	        		//判断是否存在第一次扫描CK单号时间
	        		if (apBean.getFirstCompleteOrderStockDatetime() == null) {
	        			if (!service.updateAuditPackage("first_complete_order_stock_datetime='" + DateUtil.getNow() + "',last_complete_order_stock_datetime='" + DateUtil.getNow() + "'", "id=" + apBean.getId())) {
	        				request.setAttribute("tip", "更新扫描CK单号时间出错！");
		        			return mapping.findForward("failure");
	        			}
	        		} else {
	        			if (!service.updateAuditPackage("last_complete_order_stock_datetime='" + DateUtil.getNow() + "'", "id=" + apBean.getId())) {
	        				request.setAttribute("tip", "更新扫描CK单号时间出错！");
		        			return mapping.findForward("failure");
	        			}
	        		}
	        		response.sendRedirect("orderStock/scanOrderStock2.jsp?id="+bean.getId());
	        		return null;
        		}else if(scanFlag==5){//打印落地配包裹单
        			
        			float weight=0;
        			String tempWeight=request.getParameter("weight");
        			if(tempWeight!=null && tempWeight.endsWith("kg")){
        				tempWeight = tempWeight.substring(0, tempWeight.length()-2).trim();
        			}
        			String weightReg="^\\s+\\d+\\s+\\d{1,5}\\.\\d{1,3}$";//电子秤输出格式
        			String weightReg2="^\\d{1,5}\\.\\d{1,3}$";//人工输出格式
        			
        			if(tempWeight.matches(weightReg)){//电子秤输出格式
        				String weightReg3="\\s+\\d+\\s+";
        				if(tempWeight.split(weightReg3).length<2){
        					request.setAttribute("tip", "重量格式错误！");
    		        		return mapping.findForward("printpackage");
        				}else{
        					weight=StringUtil.toFloat(tempWeight.split(weightReg3)[1]);
        				}
        			}else if (tempWeight.matches(weightReg2)){//人工输出格式
        				weight=StringUtil.toFloat(tempWeight);
        			}else{
        				request.setAttribute("tip", "重量格式错误！");
        				return mapping.findForward("printpackage");
        			}
        			weight*=1000;
        			
        			OrderStockBean orderStockBean = service.getOrderStock("code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
        			voOrder order = null;
        			if(orderStockBean != null){
        				order = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
        			}else{
        				order = wareService.getOrder("code='"+orderCode+"'");
        			}
        			if(order==null){
        				request.setAttribute("tip", "订单号错误！");
        				return mapping.findForward("printpackage");
        			}
        			voOrderExtendInfo orderExtInfo = service.getOrderExtendInfo("order_code='"+order.getCode()+"'");
        			if(orderExtInfo == null){
        				request.setAttribute("city", "");
        			}else{
        				SupplierCityBean cityBean = supplyService.getSupplierCityInfo("id="+orderExtInfo.getAddId2());
        				if(cityBean == null){
        					request.setAttribute("city","");
        				}else{
        					request.setAttribute("city",cityBean.getCity());
        				}
        			}
//        			order.setBuyMode(1);
					//voOrder order = adminService.getOrder("code='" + orderCode+ "'");
					OrderStockBean osBean = service
							.getOrderStock("order_code='" + order.getCode() + "' and status!=3");
					AuditPackageBean apBean = service
							.getAuditPackage("order_code='" + order.getCode() + "'");
					if(apBean==null){
						request.setAttribute("tip", "订单出库信息错误！");
						return mapping.findForward("printpackage");
					}
					apBean.setWeight(weight);
					OrderCustomerBean ocBean = batchBarcodeService
							.getOrderCustomerBean("order_code='" + order.getCode()
									+ "'");
					if (order != null) {
						if(order.getStatus()==3){
							request.setAttribute("tip", "该订单未复核，不允许打印包裹单！");
							return mapping.findForward("printpackage");
						}

						request.setAttribute("osBean", osBean);
						request.setAttribute("apBean", apBean);
						request.setAttribute("ocBean", ocBean);
						
						request.setAttribute("orderCode", apBean.getPackageCode());
						MailingBalanceBean mbBean =balanceService.getMailingBalance("order_code='"+ order.getCode() + "'");
						request.setAttribute("mbBean", mbBean);
						if(order.getDeliver()==9||order.getDeliver()==11){
							request.setAttribute("forward", "scanCheckOrderStock2");
						}else{
							request.setAttribute("forward", "scanCheckOrderStock2");
						}
						
						
						// 查询订单中产品是否包含电池
						boolean hasBattery = false;
//						if (osBean != null) {
//							List ospList = service
//									.getOrderStockProductList("order_stock_id="
//											+ osBean.getId(), -1, -1, null);
//							for (int i = 0; i < ospList.size(); i++) {
//								OrderStockProductBean osp = (OrderStockProductBean) ospList
//										.get(i);
//								int productId = osp.getProductId();
//								voProductProperty productProperty = wareService.getProductProperty("product_id="+ productId);
//								if (productProperty != null
//										&& productProperty.getMailingType() == 2) {// 1是无电池，2是有电池
//									hasBattery = true;
//								}
//							}
//						}
						// 得到产品分类名称
						int orderType = 0;
						SortingBatchOrderBean sboBean=siService.getSortingBatchOrderInfo("order_id="+order.getId()+" and delete_status=0");
						if(sboBean!=null){
							orderType=sboBean.getOrderType();
						}
						String sql2 = "select name from user_order_package_type where type_id=?";
						service.getDbOp().prepareStatement(sql2);
						PreparedStatement ps2 = service.getDbOp().getPStmt();
						ps2.setInt(1, orderType);
						ResultSet rs2 = ps2.executeQuery();
						String orderTypeName = "";
						while (rs2.next()) {
							orderTypeName = rs2.getString("name");
						}
						if (hasBattery) {
							orderTypeName += "#";
						}
						if(orderTypeName.equals("护肤品")||orderTypeName.equals("保健品")
								||orderTypeName.equals("香水")||orderTypeName.equals("礼品")){
							request.setAttribute("color", "red");
						}else if(orderTypeName.equals("电子")||orderTypeName.equals("手机")
								||orderTypeName.equals("电脑")||orderTypeName.equals("玩具")){
							request.setAttribute("color", "green");
						}
						request.setAttribute("orderTypeName", orderTypeName);
						
						if(!service.updateAuditPackage("weight="+weight, "id="+apBean.getId())){
							request.setAttribute("tip", "修改订单出库信息时发生异常！");
							return mapping.findForward("printpackage");
						}
						
						PrintPackageAction ppa = new PrintPackageAction();
						request.setAttribute("dprice", ppa.toUpperNumber(order.getDprice()+""));
						
						//计算系统生成物流成本
						PrintPackageAction ppa1 = new PrintPackageAction();
						ppa1.calLogisticsCost(service, order, apBean);
						int dealDetail = StringUtil.toInt(order.getDealDetail());
						String sqlDealDetail = "select content from sys_dict where id=" + dealDetail;
						ResultSet rsD = service.getDbOp().executeQuery(sqlDealDetail);
						if (rsD.next()) {
							order.setDealDetail(rsD.getString(1));
						} else {
							order.setDealDetail("");
						}
						rsD.close();
						request.setAttribute("order", order);
						if (apBean.getFirstPrintPackageDatetime() == null || apBean.getFirstPrintPackageDatetime().equals("")) {
							if (!service.updateAuditPackage("first_print_package_datetime='" + DateUtil.getNow() + "'", "id=" + apBean.getId())) {
		        				request.setAttribute("tip", "更新第一次打单时间出错！");
		        				return mapping.findForward("printpackage");
		        			}
						}
						//判断是否扫描的是大Q订单
        				if(!order.isDaqOrder()){
        					if((order.getDeliver()==9||order.getDeliver()==29||order.getDeliver()==37||order.getDeliver()==43||order.getDeliver()==44||order.getDeliver()==52 )&&order.getBuyMode()==0){//EMS省外和无锡邮政货到付款
    							return mapping.findForward("gssw");
    						}else if((order.getDeliver()==9||order.getDeliver()==29||order.getDeliver()==37||order.getDeliver()==43||order.getDeliver()==44||order.getDeliver()==52)&&order.getBuyMode()!=0){//EMS省外和无锡邮政非货到付款
    							return mapping.findForward("gssw2");
    						}else if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_JD_CD || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_JD_WX){
    							return mapping.findForward("jd");
    						}else if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YD || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_WX || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_CD){
    							List<DeliverRelationInfoBean> list = new ArrayList<DeliverRelationInfoBean>();
    							DeliverRelationInfoBean dri = null;
            					ResultSet rs = service.getDbOp().executeQuery("SELECT type,info from deliver_relation_info WHERE deliver_id = "+order.getDeliver()+" and order_code ='"+order.getCode()+"'");
            					while (rs.next()) {
            						dri = new DeliverRelationInfoBean();
            						int type = rs.getInt(1);
            						String info = rs.getString(2) == null ? "" :rs.getString(2);
            						dri.setType(type);
            						dri.setInfo(info);
            						list.add(dri);
            					}
            					rs.close();
            					if(!list.isEmpty()){
            						request.setAttribute("deliverRelationInfoList", list);
            					}
            					if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YD){
            						return mapping.findForward("yd");
            					}
            					if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_WX || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_CD){
            						return mapping.findForward("yt");
            					}
    						}else if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_WX || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_CD){//新模板打印圆通的面单
    							/**
    							List<DeliverRelationInfoBean> list = new ArrayList<DeliverRelationInfoBean>();
    							DeliverRelationInfoBean dri = null;
    							ResultSet rs = service.getDbOp().executeQuery("SELECT type,info from deliver_relation_info WHERE deliver_id = "+order.getDeliver()+" and order_code ='"+order.getCode()+"'");
            					while (rs.next()) {
            						dri = new DeliverRelationInfoBean();
            						int type = rs.getInt(1);
            						String info = rs.getString(2) == null ? "" :rs.getString(2);
            						dri.setType(type);
            						dri.setInfo(info);
            						list.add(dri);
            					}
            					rs.close();
        						WayBill wb = new YtWayBill();
        						if(order.getBuyMode()==0){//货到付款
        							wb.setItemsValue(order.getDprice()+"");//代收货款
        						}else{
        							wb.setItemsValue("");
        						}
        						wb.setdPrice(order.getDprice()+"");//订单金额
        						if(!list.isEmpty()){
        							DeliverRelationInfoBean deliverRelationInfoBean =list.get(3);
        							wb.setPosition(deliverRelationInfoBean.getInfo());//大笔
        						}else{
        							wb.setPosition("");
        						}
        						
        						String packageCode="";
        						ResultSet rs3 = service.getDbOp().executeQuery("select package_code from deliver_relation WHERE deliver_id = "+order.getDeliver()+" and order_code ='"+order.getCode()+"'");
            					if (rs3.next()) {
            						packageCode = rs3.getString(1) == null ? "" :rs3.getString(1);
            					}
            					rs3.close();
            					wb.setMailNo(packageCode);//运单号
            					wb.setName(order.getName());//收件人
            					wb.setMobile(order.getPhone());//收件人电话
            					wb.setAddress(order.getAddress());//收件人地址
            					wb.setSender("无锡买卖宝");//寄件人姓名
            					wb.setPostCode(order.getPostcode());
            					wb.setSenderAddress("买卖宝"+ProductStockBean.areaMap.get(osBean.getStockArea())+"仓");//寄件人地址
            					if (!order.isTaobaoOrder()) {
            						wb.setSenderMobile("4008843211");//寄件人电话
            					}else{
            						wb.setSenderMobile("");
            					}
            					wb.setOrderCode(order.getCode());//买卖宝订单号
            					wb.setDeliverName(order.getDeliverName());//快递公司
            					wb.setDeliverId(order.getDeliver());//快递公司Id
            					wb.setOrderTypeName(orderTypeName);//商品类型
            					wb.setWeight(apBean.getWeight()/1000);//包裹重量
            					
            					wb.setReportPath(this.getClass().getResource("/").toURI().resolve("../config").getPath() + "/" + "bq_report.jasper");//打印大q模板
            					wb.setMmbImgPath(request.getSession().getServletContext().getRealPath("/")+"/image/dQ.jpg");
            					
            					WayBillServiceFactory serviceFactory =SpringHandler.getBean(WayBillServiceFactory.class);
            					WayBillService wayBillService =serviceFactory.create(DeliverCorpInfoBean.DELIVER_ID_YT);
            					wayBillService.printWayBill(wb);
            					return null;
            					*/
        					}
        					String addr = "";
        					ResultSet rs = service.getDbOp().executeQuery("select pc.city,ca.area from user_order_extend_info uoei left join province_city pc on uoei.add_id2=pc.id left join city_area ca on uoei.add_id3=ca.id where uoei.order_code = '" + order.getCode() + "'");
        					if (rs.next()) {
        						addr = rs.getString(1) == null ? "" :rs.getString(1);
        						addr += rs.getString(2) == null ? "" :rs.getString(2);
        					}
        					rs.close();
        					request.setAttribute("addr", addr);
    						return mapping.findForward("szzj");//广宅+落地配
    						
        				} else {
        					if((order.getDeliver()==9||order.getDeliver()==29||order.getDeliver()==37||order.getDeliver()==43||order.getDeliver()==44||order.getDeliver()==52 )&&order.getBuyMode()==0){//EMS省外和无锡邮政货到付款
    							return mapping.findForward("gsswDaq");
    						}else if((order.getDeliver()==9||order.getDeliver()==29||order.getDeliver()==37||order.getDeliver()==43||order.getDeliver()==44||order.getDeliver()==52 )&&order.getBuyMode()!=0){//EMS省外和无锡邮政非货到付款
    							return mapping.findForward("gsswDaq2");
    						}else if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YD || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_WX || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_CD){
    							List<DeliverRelationInfoBean> list = new ArrayList<DeliverRelationInfoBean>();
    							DeliverRelationInfoBean dri = null;
            					ResultSet rs = service.getDbOp().executeQuery("SELECT type,info from deliver_relation_info WHERE deliver_id = "+order.getDeliver()+" and order_code ='"+order.getCode()+"'");
            					while (rs.next()) {
            						dri = new DeliverRelationInfoBean();
            						int type = rs.getInt(1);
            						String info = rs.getString(2) == null ? "" :rs.getString(2);
            						dri.setType(type);
            						dri.setInfo(info);
            						list.add(dri);
            					}
            					rs.close();
            					request.setAttribute("deliverRelationInfoList", list);
            					request.setAttribute("DqFlag", "1");
            					if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YD){
            						return mapping.findForward("yd");
            					}
            					if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_WX || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_CD){
            						return mapping.findForward("yt");
            					}
    						}else if(order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_WX || order.getDeliver()==DeliverCorpInfoBean.DELIVER_ID_YT_CD){//新模板打印圆通的大Q面单
    							/**
    							List<DeliverRelationInfoBean> list = new ArrayList<DeliverRelationInfoBean>();
    							DeliverRelationInfoBean dri = null;
    							ResultSet rs = service.getDbOp().executeQuery("SELECT type,info from deliver_relation_info WHERE deliver_id = "+order.getDeliver()+" and order_code ='"+order.getCode()+"'");
            					while (rs.next()) {
            						dri = new DeliverRelationInfoBean();
            						int type = rs.getInt(1);
            						String info = rs.getString(2) == null ? "" :rs.getString(2);
            						dri.setType(type);
            						dri.setInfo(info);
            						list.add(dri);
            					}
            					rs.close();
        						WayBill wb = new YtWayBill();
        						if(order.getBuyMode()==0){//货到付款
        							wb.setItemsValue(order.getDprice()+"");//代收货款
        						}else{
        							wb.setItemsValue("");
        						}
        						wb.setdPrice(order.getDprice()+"");//订单金额
        						if(!list.isEmpty()){
        							DeliverRelationInfoBean deliverRelationInfoBean =list.get(3);
        							wb.setPosition(deliverRelationInfoBean.getInfo());//大笔
        						}else{
        							wb.setPosition("");
        						}
        						
        						String packageCode="";
        						ResultSet rs3 = service.getDbOp().executeQuery("select package_code from deliver_relation WHERE deliver_id = "+order.getDeliver()+" and order_code ='"+order.getCode()+"'");
            					if (rs3.next()) {
            						packageCode = rs3.getString(1) == null ? "" :rs3.getString(1);
            					}
            					rs3.close();
            					wb.setMailNo(packageCode);//运单号
            					wb.setName(order.getName());//收件人
            					wb.setMobile(order.getPhone());//收件人电话
            					wb.setAddress(order.getAddress());//收件人地址
            					wb.setSender("无锡买卖宝");//寄件人姓名
            					wb.setPostCode(order.getPostcode());
            					wb.setSenderAddress("买卖宝"+ProductStockBean.areaMap.get(osBean.getStockArea())+"仓");//寄件人地址
            					if (!order.isTaobaoOrder()) {
            						wb.setSenderMobile("4008843211");//寄件人电话
            					}else{
            						wb.setSenderMobile("");
            					}
            					wb.setOrderCode(order.getCode());//买卖宝订单号
            					wb.setDeliverName(order.getDeliverName());//快递公司
            					wb.setDeliverId(order.getDeliver());//快递公司Id
            					wb.setOrderTypeName(orderTypeName);//商品类型
            					wb.setWeight(apBean.getWeight()/1000);//包裹重量
            					
            					wb.setReportPath(this.getClass().getResource("/").toURI().resolve("../config").getPath() + "/" + "bq_report.jasper");//打印大q模板
            					wb.setMmbImgPath(request.getSession().getServletContext().getRealPath("/")+"/image/dQ.jpg");
            					
            					WayBillServiceFactory serviceFactory =SpringHandler.getBean(WayBillServiceFactory.class);
            					WayBillService wayBillService =serviceFactory.create(DeliverCorpInfoBean.DELIVER_ID_YT);
            					wayBillService.printWayBillDq(wb);
            					return null;
            					*/
        					}
        					String addr = "";
        					ResultSet rs = service.getDbOp().executeQuery("select pc.city,ca.area from user_order_extend_info uoei left join province_city pc on uoei.add_id2=pc.id left join city_area ca on uoei.add_id3=ca.id where uoei.order_code = '" + order.getCode() + "'");
        					if (rs.next()) {
        						addr = rs.getString(1) == null ? "" :rs.getString(1);
        						addr += rs.getString(2) == null ? "" :rs.getString(2);
        					}
        					rs.close();
        					request.setAttribute("addr", addr);
    						return mapping.findForward("szzjDaq");//广宅+落地配
        				}
					}
        		}
        		
        	}
        	buf.append("code='").append(orderCode).append("'");
        	StockExchangeBean stockBean = productService.getStockExchange(buf.toString());
        	  if (stockBean == null) {
	                request.setAttribute("tip", "在等待复核的订单列表中，没有您扫描的调拨单！");
	                return mapping.findForward("failure");
	            }
        	  // 跳转到调拨单出货操作页面
        	  if(scanFlag==0)
        		  response.sendRedirect("productStock/scanStockExchange.jsp?scanFlag=1&exchangeId="+stockBean.getId());
        	  else if(scanFlag==1)
        		  response.sendRedirect("productStock/scanInStockExchange.jsp?scanFlag=2&exchangeId="+stockBean.getId());
            return null;
	    } finally {
	        service.releaseAll();
	    }
	}
}
 