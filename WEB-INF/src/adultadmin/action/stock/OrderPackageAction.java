package adultadmin.action.stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.finance.balance.BalanceService;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.msg.TemplateMarker;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderImportLogBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.IProductDiscountService;
import adultadmin.service.infc.ISMSService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;

public class OrderPackageAction extends DispatchAction {
	public static byte[] OrderPackageLock = new byte[0];
	public static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
	/**
	 * 
	 * 扫描包裹单号
	 */
	public ActionForward orderPackage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(OrderPackageLock){
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService balanceService=ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		int checkStatus=0;
		try{
			dbOp.startTransaction();
			String orderCode=request.getParameter("orderCode");
			String packageCode=request.getParameter("packageCode");
			if(orderCode!=null&&packageCode!=null){
				orderCode=orderCode.trim();
				packageCode=packageCode.trim();
				request.setAttribute("orderCode", orderCode);
				request.setAttribute("packageCode", packageCode);
				
				voOrder order=wareService.getOrder("code='"+orderCode+"'");
				AuditPackageBean apBean=stockService.getAuditPackage("order_code='"+orderCode+"'");
				
				//String shengwaiReg="^EC[0-9]{9}CS$";//广东省外货到付款订单，包裹单号格式为：EC+9位数字+CS
				String shengwaiReg="^[0-9]{13}$";//广东省外货到付款订单，包裹单号格式为：13位数字
				String shengneiReg="^EE[0-9]{9}GD$";//广东省速递局货到付款   包裹单号格式为：EE+9位数字+GD
				String zhaijisongReg="^[0-9]{10}$";//宅急送货到付款，包裹单号格式为：10位数字
				//String pingyouReg="^E([A-Z&&[^EC]]){1}[0-9]{9}CS$";//非货到付款的订单，平邮，E+除C和E的其他字母+9位数字+CS ，
				String pingyouReg="^[0-9]{13}$";//非货到付款的订单，平邮，13位数字
				if (order != null) {
					OrderStockBean stockBean = stockService.getOrderStock("order_code='"+order.getCode()+"'");
					if (!CargoDeptAreaService.hasCargoDeptArea(request, stockBean.getStockArea(), stockBean.getStockType())) {
						request.setAttribute("checkStatus", 0+"");
						return mapping.findForward("orderPackage");
					}
				}
				if(order==null||apBean==null){//未分拣
					checkStatus=1;
				}else if(order.getDeliver()!=9){//只能关联EMS省外订单
					checkStatus=4;
				}else if(apBean.getStatus()==1){//已分拣，未复核出库
					checkStatus=2;
				}else if(order.getBuyMode()!=0&&order.getDeliver()!=9&&order.getDeliver()!=11){//非货到付款，只能关联省内和省外
					checkStatus=11;
				}else if(order.getBuyMode()!=0&&!packageCode.matches(pingyouReg)){//非货到付款的包裹单号限制
					checkStatus=10;
				}else if(order.getBuyMode()==0&&order.getDeliver()==9&&!packageCode.matches(shengwaiReg)){//广东省外的包裹单号限制
					checkStatus=10;
				}else if(order.getBuyMode()==0&&order.getDeliver()==11&&!packageCode.matches(shengneiReg)){//广东省速递局的包裹单号限制
					checkStatus=10;
				}else if(order.getDeliver()==10&&!packageCode.matches(zhaijisongReg)){//广东宅急送的包裹单号限制
					checkStatus=10;
				}else if((order.getDeliver()==13||order.getDeliver()==14||order.getDeliver()==15||order.getDeliver()==16
						||order.getDeliver()==17||order.getDeliver()==18||order.getDeliver()==19||order.getDeliver()==20
						||order.getDeliver()==21||order.getDeliver()==22||order.getDeliver()==23||order.getDeliver()==24
						||order.getDeliver()==25||order.getDeliver()==26||order.getDeliver()==27||order.getDeliver()==28
						||order.getDeliver()==29||order.getDeliver()==30)&&!packageCode.equals(orderCode)){//自建物流的的包裹单号限制
					checkStatus=10;
				}else if(apBean.getStatus()==4){//已扫描包裹单
					if(apBean.getPackageCode().equals(packageCode)){//导入的包裹单号和原有包裹单号相同
						checkStatus=3;
						request.setAttribute("checkStatus", checkStatus+"");
						request.setAttribute("oriPackageCode", apBean.getPackageCode());
					}else{//导入了和原来不同的包裹单号
						if(request.getParameter("confirm2")!=null&&request.getParameter("confirm2").equals("1")){//确认重复导入
							//是否有包裹单号重复
							List mBeanList=balanceService.getMailingBalanceList("packagenum='"+packageCode+"' and order_code<>'"+order.getCode()+"'", -1, -1, null);
							if(mBeanList.size()!=0){	//有别的订单的包裹单号与此包裹单号重复
								String confirm=request.getParameter("confirm");//有重复时是否导入
								if(confirm.equals("1")){	//确定导入该包裹单
									//----------开始导入包裹单
									checkStatus=importPackage(order,packageCode,user,request,dbOp,true);
									if(checkStatus!=7){
										request.setAttribute("checkStatus", checkStatus+"");
										return mapping.findForward("orderPackage");
									}
									//-----结束导入包裹单
								}else{	//不导入包裹单，返回提示
									String otherCode="";	//与此包裹单号重复的订单号
									for(int i=0;i<mBeanList.size();i++){
										MailingBalanceBean mBean=(MailingBalanceBean)mBeanList.get(i);
										otherCode+=mBean.getOrderCode();
										if(i!=mBeanList.size()-1){
											otherCode+=",";
										}
									}
									checkStatus=6;
									request.setAttribute("checkStatus", checkStatus+"");
									request.setAttribute("otherCode", otherCode);
								}
							}else{//没有包裹单号重复
								//----------开始导入包裹单
								checkStatus=importPackage(order,packageCode,user,request,dbOp,true);
								if(checkStatus!=7){
									request.setAttribute("checkStatus", checkStatus+"");
									return mapping.findForward("orderPackage");
								}
								//-----结束导入包裹单
							}
						}else{//未确认重复导入
							checkStatus=8;
							request.setAttribute("checkStatus", checkStatus+"");
							request.setAttribute("oriPackageCode", apBean.getPackageCode());
						}
					}
				}else{
					//是否有包裹单号重复
					List mBeanList=balanceService.getMailingBalanceList("packagenum='"+packageCode+"' and order_code<>'"+order.getCode()+"'", -1, -1, null);
					if(mBeanList.size()!=0){	//有别的订单的包裹单号与此包裹单号重复
						String confirm=request.getParameter("confirm");//有重复时是否导入
						if(confirm.equals("1")){	//确定导入该包裹单
							//----------开始导入包裹单
							checkStatus=importPackage(order,packageCode,user,request,dbOp,true);
							if(checkStatus!=7){
								request.setAttribute("checkStatus", checkStatus+"");
								return mapping.findForward("orderPackage");
							}
							//-----结束导入包裹单
						}else{	//不导入包裹单，返回提示
							String otherCode="";	//与此包裹单号重复的订单号
							for(int i=0;i<mBeanList.size();i++){
								MailingBalanceBean mBean=(MailingBalanceBean)mBeanList.get(i);
								otherCode+=mBean.getOrderCode();
								if(i!=mBeanList.size()-1){
									otherCode+=",";
								}
							}
							checkStatus=6;
							request.setAttribute("checkStatus", checkStatus+"");
							request.setAttribute("otherCode", otherCode);
						}
					}else{//没有包裹单号重复
						//----------开始导入包裹单
						checkStatus=importPackage(order,packageCode,user,request,dbOp,true);
						if(checkStatus!=7){
							request.setAttribute("checkStatus", checkStatus+"");
							return mapping.findForward("orderPackage");
						}
						//-----结束导入包裹单
					}
				}
				request.setAttribute("checkStatus", checkStatus+"");
			}
			dbOp.commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
			stockService.getDbOp().rollbackTransaction();
		}finally{
			stockService.releaseAll();
		}
		}
		return mapping.findForward("orderPackage");
	}
	
	/**
	 * 
	 * @param order
	 * @param packageCode
	 * @param user
	 * @param request
	 * @param dbOp
	 * @param msg 是否发送短信
	 * @return
	 */
	public int importPackage(voOrder order,String packageCode,voUser user,HttpServletRequest request,DbOperation dbOp,boolean msg){
		
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBatchBarcodeService batchBarcodeService=ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService balanceService=ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, dbOp);
		BalanceService bService = new BalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		//cxq---
		IProductDiscountService ipds=ServiceFactory.createProductDiscountService(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingInfoService siServicer = new SortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, dbOp);
		int checkStatus=6;
		//----------开始导入包裹单
		// 结算单 相关数据的设置
		//设置 结算单的 包裹单号、结算周期、结算来源
		int deliver = order.getDeliver();
		int balanceType = 0;
		OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId()+" and status!="+OrderStockBean.STATUS4);
		// 根据 快递公司，确定结算来源
		if(voOrder.deliverToBalanceTypeMap.containsKey(deliver+"")){
			balanceType=Integer.parseInt(voOrder.deliverToBalanceTypeMap.get(deliver+"").toString());
		}  else {
			// 如果没有找到 相应的快递公司，就报错
			OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
			order.setSerialNumber(ocBean.getSerialNumber());
			request.setAttribute("order", order);
			AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
			request.setAttribute("apBean", apBean2);
			if(osBean!=null){
				OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
				if(ospBean!=null){
					request.setAttribute("ospBean", ospBean);
				}
			}
			checkStatus=4;
			request.setAttribute("checkStatus", checkStatus+"");
			dbOp.rollbackTransaction();
			return checkStatus;
		}
		
		
		// 根据当前日期 计算 订单所属的结算周期
		MailingBalanceBean mb = balanceService.getMailingBalance("order_id=" + order.getId());
		String stockoutDate = StringUtil.cutString(mb.getStockoutDatetime(), 10);
		StringBuilder qbuf = new StringBuilder();
		qbuf.append("balance_type=").append(balanceType).append(" and ");
		qbuf.append("balance_cycle_start <= '").append(stockoutDate).append("' and balance_cycle_end >= '").append(stockoutDate).append("' ");
		qbuf.delete(0, qbuf.length());
		qbuf.append("balance_status=").append(MailingBalanceBean.BALANCE_STATUS_UNDEAL).append(", ");
		qbuf.append("packagenum='").append(packageCode).append("' ");
		if(!balanceService.updateMailingBalance(qbuf.toString(), "order_id=" + order.getId())){
			checkStatus=6;
//			System.out.println("balanceService.updateMailingBalance");
			request.setAttribute("checkStatus", checkStatus+"");
			dbOp.rollbackTransaction();
			return checkStatus;
		}
		
		// 异常结算数据
		if(!bService.updateFinanceMailingBalanceBean("balance_status = " + MailingBalanceBean.BALANCE_STATUS_UNDEAL + ", packagenum = '" + packageCode + "' ", "order_id=" + order.getId())){
			checkStatus=6;
//			System.out.println("bService.updateFinanceMailingBalanceBean");
			request.setAttribute("checkStatus", checkStatus+"");
			dbOp.rollbackTransaction();
			return checkStatus;
		}
		
		
		// 结算单 相关数据的设置
		order.setPackageNum(packageCode);
		order.setStockoutRemark("");
		if(!wareService.modifyOrder(order)){
			checkStatus=6;
//			System.out.println("wareService.modifyOrder");
			request.setAttribute("checkStatus", checkStatus+"");
			dbOp.rollbackTransaction();
			return checkStatus;
		}
		order.setStockOper(stockService.getStockOperation("type=0 and status=2 and order_code='" + order.getCode() + "'"));
		StringBuilder buf = new StringBuilder();
		order.setProducts(buf.toString());
		if( order.isAmazonOrder() ) {
			order.setAmazonCode(siServicer.getThirdCode(order.getId()));
		} else if( order.isDangdangOrder() ) {
			order.setDangdangCode(siServicer.getThirdCode(order.getId()));
		} else if( order.isJdOrder() ) {
			order.setJdCode(siServicer.getThirdCode(order.getId()));
		} else if( order.isJdOrder() ) {
			order.setJdAdultCode(siServicer.getThirdCode(order.getId()));
		}
		if(msg==true){
			sendSMS(user, order); // 订单已发货，发送短信通知客户
		}
		//添加日志
		OrderImportLogBean log = new OrderImportLogBean();
		log.setContent(order.getCode()+"\t"+packageCode);
		log.setCreateDatetime(DateUtil.getNow());
		log.setUserId(user.getId());
		log.setType(0);
		if(!logService.addOrderImportLog(log)){
			checkStatus=6;
//			System.out.println("logService.addOrderImportLog");
			request.setAttribute("checkStatus", checkStatus+"");
			dbOp.rollbackTransaction();
			return checkStatus;
		}
		/**
		 * 京东特需复核包裹单号加“-1-1-”
		 */
		if(deliver==DeliverCorpInfoBean.DELIVER_ID_JD_WX || deliver==DeliverCorpInfoBean.DELIVER_ID_JD_CD){
			packageCode = packageCode+"-1-1-";
		}	
		if(!stockService.updateAuditPackage("package_code='"+packageCode+"',status=4,audit_package_datetime='"+DateUtil.getNow()+"',audit_package_user_name='"+user.getUsername()+"'", "order_id="+order.getId())){
			checkStatus=6;
//			System.out.println("stockService.updateAuditPackage");
			request.setAttribute("checkStatus", checkStatus+"");
			dbOp.rollbackTransaction();
			return checkStatus;
		}
		
		//推送订单包裹信息
		AuditPackageBean auditPackage = stockService.getAuditPackage("order_id = "+order.getId());
		order.setAuditPakcageBean(auditPackage);
		order.setOrderExtendInfo(wareService.getOrderExtendInfo(order.getId()));
		osBean.setOrder(order);
		if(!wareService.pushDeliverOrder(osBean)){
			checkStatus=6;
//			System.out.println("wareService.pushDeliverOrder");
			request.setAttribute("checkStatus", checkStatus+"");
			dbOp.rollbackTransaction();
			return checkStatus;
		}
		/**
		 * 京东特需财务接口去掉“-1-1-”
		 */
		if(deliver==DeliverCorpInfoBean.DELIVER_ID_JD_WX || deliver==DeliverCorpInfoBean.DELIVER_ID_JD_CD){
			packageCode = packageCode.replaceAll("-1-1-","");
		}
		//修改发货信息表（财务统计用表）包裹单号
		frfService.updateFinanceSellBean("packagenum = '" + StringUtil.toSql(packageCode) + "'", "order_id =" + order.getId());
		checkStatus=7;
		//-----结束导入包裹单
		return checkStatus;
	}
	
	private void sendSMS(voUser user, voOrder order){
		//现有流程只用这一个
		DbOperation dbOp_sms = new DbOperation(DbOperation.DB_SMS);
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp_sms);
		try{
			DeliverCorpInfoBean dciBean = (DeliverCorpInfoBean)voOrder.deliverInfoMapAll.get(order.getDeliver());
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("orderCode", order.getCode());
			paramMap.put("packageNum",order.getPackageNum());
			paramMap.put("totalPrice", df.format(order.getDprice()));
			//如果快递公司为空，则默认快递公司填写成EMS
			if (dciBean == null) {
				paramMap.put("deliverName", "EMS");
				paramMap.put("days", "3-5");
				paramMap.put("phone","11185");
			} else {
				if (dciBean.getNameWap() != null && dciBean.getNameWap().length() > 0) {
					paramMap.put("deliverName", dciBean.getNameWap());
				} else {
					paramMap.put("deliverName", dciBean.getName());
				}
				paramMap.put("days", dciBean.getDays());
				paramMap.put("phone",dciBean.getPhone());
			}
			String templateName="";
			String content = "";
			if( order.isDaqOrder() ) {
				//对于大Q手机 除了用新的模板之外还要用65 通道发送
				templateName=TemplateMarker.SENT_OUT_DAQ_MESSAGE_NAME;
				TemplateMarker tm =TemplateMarker.getMarker();
				content=tm.getOutString(templateName, paramMap);
				SenderSMS3.send(user.getId(), order.getPhone(), content, 65);
			} else if( order.isAmazonOrder()) {
				paramMap.put("amazonCode", order.getAmazonCode());
				templateName=TemplateMarker.SENT_OUT_AMAZON_MESSAGE_NAME;
				TemplateMarker tm =TemplateMarker.getMarker();
				content=tm.getOutString(templateName, paramMap);
				SenderSMS3.send(user.getId(), order.getPhone(), content);
			} else if (order.isTaobaoOrder()) {
				//淘宝订单去掉订单发货短信
				return;
			} else if( order.isDangdangOrder()) {
				paramMap.put("dangdangCode", order.getDangdangCode());
				templateName=TemplateMarker.SENT_OUT_DANGDANG_MESSAGE_NAME;
				TemplateMarker tm =TemplateMarker.getMarker();
				content=tm.getOutString(templateName, paramMap);
				SenderSMS3.send(user.getId(), order.getPhone(), content);
			} else if(order.isJdOrder()) {
				paramMap.put("jdCode", order.getJdCode());
				templateName=TemplateMarker.SENT_OUT_JD_MESSAGE_NAME;
				TemplateMarker tm =TemplateMarker.getMarker();
				content=tm.getOutString(templateName, paramMap);
				SenderSMS3.send(user.getId(), order.getPhone(), content);
			} else if(order.isJdAdultOrder()) {
				paramMap.put("jdAdultCode", order.getJdAdultCode());
				templateName=TemplateMarker.SENT_OUT_JD_ADULT_MESSAGE_NAME;
				TemplateMarker tm =TemplateMarker.getMarker();
				content=tm.getOutString(templateName, paramMap);
				SenderSMS3.send(user.getId(), order.getPhone(), content);
			}  else if (order.isLTOrder()) {
				//兰亭订单去掉订单发货短信
				return;
			}else {
				templateName=TemplateMarker.SENT_OUT_MESSAGE_NAME;
				TemplateMarker tm =TemplateMarker.getMarker();
				content=tm.getOutString(templateName, paramMap);
				SenderSMS3.send(user.getId(), order.getPhone(), content);
			}
			

			SendMessage3Bean sendMessage3 = new SendMessage3Bean();
			sendMessage3.setOrderId(order.getId());
			sendMessage3.setOrderCode(order.getCode());
			sendMessage3.setPackageNum(order.getPackageNum());
			sendMessage3.setMobile(order.getPhone());
			sendMessage3.setSendDatetime(DateUtil.getNow());
			sendMessage3.setSendUserId(user.getId());
			sendMessage3.setSendUsername(user.getUsername());
			sendMessage3.setContent(content);

			smsService.addSendMessage3(sendMessage3);

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			smsService.releaseAll();
		}
	}
	
}
