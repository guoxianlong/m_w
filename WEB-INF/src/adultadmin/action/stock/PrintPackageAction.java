package adultadmin.action.stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import cache.FinanceCache;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voUser;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockPrintLogBean;
import adultadmin.bean.stock.MailingBatchPackageBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class PrintPackageAction extends DispatchAction {
	public static byte[] PrintPackageLock = new byte[0];
	public Log debugLog = LogFactory.getLog("debug.Log");

	/**
	 * 
	 * 打印包裹单
	 */
	public ActionForward printPackage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Calendar printStartTime=Calendar.getInstance();//打印开始时间，用于输出包裹单打印时间
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized (PrintPackageLock) {
			DbOperation dbOp = new DbOperation(DbOperation.DB);
			WareService wareService = new WareService();
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbOp);
			IBatchBarcodeService batchBarcodeService = ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE,dbOp);
			DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
			IStockService stockSlaveService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
			try {
				dbOp.startTransaction();
				int checkStatus = 0;// 返回的状态
				if (request.getParameter("checkStatus") != null) {
					checkStatus = Integer.parseInt(request
							.getParameter("checkStatus"));
				}
				String orderCode = request.getParameter("orderCode");// 订单编号
				String weightStr = request.getParameter("weight");
				if (weightStr != null && weightStr.length() >= 14) {
					weightStr = weightStr.substring(7, 14);
				}
				float weight = StringUtil.toFloat(weightStr);// 包裹重量
				weight *= 1000;// 重量转化为克
				if (orderCode != null) {
					voOrder order = wareService.getOrder("code='" + orderCode
							+ "'");
					OrderStockBean osBean = stockService
							.getOrderStock("order_code='" + orderCode + "'");
					AuditPackageBean apBean = stockService
							.getAuditPackage("order_code='" + orderCode + "'");
					if(apBean!=null){
						apBean.setWeight(weight);
					}
//					OrderStockPrintLogBean ospLog = stockSlaveService
//							.getOrderStockPrintLog("remark='" + orderCode
//									+ "' and type=3");
					OrderCustomerBean ocBean = batchBarcodeService
							.getOrderCustomerBean("order_code='" + orderCode
									+ "'");
					if (order != null) {
						// 查询订单中产品是否包含电池
						boolean hasBattery = false;
//						if (osBean != null) {
//							List ospList = stockService
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
						String sql1 = "select product_type from user_order where code='"
								+ orderCode + "'";
						dbOp.prepareStatement(sql1);
						PreparedStatement ps1 = dbOp.getPStmt();
						// ps1.setString(1, orderCode);
						ResultSet rs1 = ps1.executeQuery();
						while (rs1.next()) {
							orderType = rs1.getInt("product_type");
						}

						String sql2 = "select name from user_order_package_type where type_id=?";
						dbOp.prepareStatement(sql2);
						PreparedStatement ps2 = dbOp.getPStmt();
						ps2.setInt(1, orderType);
						ResultSet rs2 = ps2.executeQuery();
						String orderTypeName = "";
						while (rs2.next()) {
							orderTypeName = rs2.getString("name");
						}
						if (hasBattery) {
							orderTypeName += "#";
						}
						request.setAttribute("orderTypeName", orderTypeName);

						// 得到产品价格大写及小写显示格式
						String price = order.getDprice() + "";
						String priceUpper = "";// 大写数字
						String priceDown = "";// 小写数字
						if (price.endsWith(".00")) {
							priceDown = price.substring(0, price.length() - 3);
						} else if (price.endsWith(".0")) {
							priceDown = price.substring(0, price.length() - 2);
						}
						priceUpper = toUpperNumber(priceDown);
						request.setAttribute("priceDown", priceDown);
						request.setAttribute("priceUpper", priceUpper);
						
						//得到订单三级地址编码
						String postCode= "";
						if(StringUtil.convertNull(order.getPostcode()).length()>5){
							order.getPostcode().substring(0,4);//三级地址编码，如果没有记录则用邮编前4位
						}
						voOrderExtendInfo extendInfo=wareService.getOrderExtendInfo(order.getId());
						if(extendInfo!=null){
							int addId3=extendInfo.getAddId3();
							String postCodeQuery="select code from city_area where id="+addId3;
							ResultSet rs=dbOp.executeQuery(postCodeQuery);
							if(rs.next()){
								if(rs.getString("code")!=null){
									postCode=rs.getString("code");
								}
							}
							rs.close();
						}
						request.setAttribute("postCode", postCode);
					}
					
					String citySql="select pc.city from user_order_extend_info uoei join province_city pc on pc.id=uoei.add_id2 where uoei.order_code='"+orderCode+"'";
					ResultSet cityRs = dbOp.executeQuery(citySql);
					if(cityRs.next()){
						String city=cityRs.getString(1);
						request.setAttribute("city", city);
					}
					cityRs.close();
					
					request.setAttribute("order", order);
					request.setAttribute("osBean", osBean);
					request.setAttribute("apBean", apBean);
					request.setAttribute("ocBean", ocBean);

					int deliver = 0;
					String address = "";
					if (order != null) {
						deliver = order.getDeliver();// 快递公司
						address = order.getAddress();// 地址
					}
					if (order == null || osBean == null || apBean == null
							|| apBean.getStatus() == 0) {
						checkStatus = 1;// 未分拣
					} else if (order.getBuyMode() != 0 && deliver !=9
							&& deliver!=11) {
						checkStatus = 7;// 不是货到付款的订单
					} else if (apBean.getStatus() == 1) {
						checkStatus = 2;// 未复核
					} else if (deliver != 9) {
						checkStatus = 4;// 快递公司错误
					} else if ((deliver == 9 && address.trim().startsWith("广东省"))
							|| (deliver == 11 && !address.trim().startsWith("广东省"))) {
						checkStatus = 5;// 无法辨别省内省外
					} else if (apBean.getPackageCode() != null && !apBean.getPackageCode().equals("")) {
						if (user.getGroup().isFlag(388)) {// 有重复打印权限
							checkStatus = 3;// 已打印过包裹单
							if (request.getParameter("confirm") != null
									&& request.getParameter("confirm").equals(
											"1")) {
								checkStatus = 6;
								OrderStockPrintLogBean logBean = new OrderStockPrintLogBean();
								logBean.setBatch(0);
								logBean.setType(3);
								logBean.setUserId(user.getId());
								logBean.setUserName(user.getUsername());
								logBean.setTime(DateUtil.getNow());
								logBean.setRemark(orderCode);
								stockService.addOrderStockPrintLog(logBean);// 添加日志
								stockService.updateAuditPackage("weight="
										+ weight, "order_code='" + orderCode
										+ "'");// 修改包裹重量
								MailingBatchPackageBean mailingBatchPackage = stockService
										.getMailingBatchPackage("order_code='"
												+ orderCode
												+ "' and balance_status!=3");
								if (mailingBatchPackage != null) {
									stockService
											.updateMailingBatchPackage(
													"weight=" + weight,
													"order_code='"
															+ orderCode
															+ "' and balance_status!=3");
								}
								AuditPackageBean apBean2 = stockService
										.getAuditPackage("order_code='"
												+ orderCode + "'");// 重量是最新的数据
								
								
								//系统自动计算物流成本
								calLogisticsCost(stockService, order, apBean);
								
								
								request.setAttribute("apBean", apBean2);
								if (deliver == 9) {
									request.setAttribute("orderCode", orderCode);
									dbOp.commitTransaction();
									if(order.getBuyMode() != 0){
										return mapping.findForward("pingyou");
									}else{
										return mapping.findForward("inCountryPackage");
									}
									

								} else if (deliver == 11) {
									request.setAttribute("orderCode", orderCode);
									dbOp.commitTransaction();
									if(order.getBuyMode() != 0){
										return mapping.findForward("pingyou");
									}else{
										return mapping.findForward("inProPackage");
									}
								} 
//								else if (deliver == 13||deliver == 14||deliver == 15||deliver == 16||deliver == 17
//										||deliver == 18||deliver == 19||deliver == 20||deliver == 21||deliver == 22) {
//									// 深圳自建，通路速递福建，赛澳递，赛澳递江苏，赛澳递上海，如风达，通路速递广东，通路速递浙江，银捷速递，如风达浙江
//									request.setAttribute("orderCode", orderCode);
//									MailingBalanceBean mbBean = balanceService
//											.getMailingBalance("order_code='"
//													+ orderCode + "'");
//									request.setAttribute("mbBean", mbBean);
//									adminService.getDbOperation()
//											.commitTransaction();
//									return mapping.findForward("szzj");
//								}
//								else if(deliver==10){
//									request.setAttribute("orderCode", orderCode);
//									MailingBalanceBean mbBean = balanceService.getMailingBalance("order_code='"+ orderCode + "'");
//									request.setAttribute("mbBean", mbBean);
//									return mapping.findForward("gzzjs");
//								}
								
							}
						} else {
							checkStatus = 9;// 不能重复打印
						}

					} else {// 可以打印包裹单
						checkStatus = 6;
						OrderStockPrintLogBean logBean = new OrderStockPrintLogBean();
						logBean.setBatch(0);
						logBean.setType(3);
						logBean.setUserId(user.getId());
						logBean.setUserName(user.getUsername());
						logBean.setTime(DateUtil.getNow());
						logBean.setRemark(orderCode);
						stockService.addOrderStockPrintLog(logBean);// 添加日志
						stockService.updateAuditPackage("weight=" + weight,
								"order_code='" + orderCode + "'");// 修改包裹重量
						
						//系统自动计算物流成本
						calLogisticsCost(stockService, order, apBean);

						if (deliver == 9) {
							request.setAttribute("orderCode", orderCode);
							dbOp.commitTransaction();
							Calendar printEndTime=Calendar.getInstance();//打印时间，用于输出包裹单打印时间
							long printTime=printEndTime.getTimeInMillis()-printStartTime.getTimeInMillis();
//							System.out.println("包裹单打印：订单号"+orderCode+"，打印时间"+printTime+"毫秒");
							if(order.getBuyMode() != 0){
								return mapping.findForward("pingyou");
							}else{
								return mapping
								.findForward("inCountryPackage");
							}
						} else if (deliver == 11) {
							request.setAttribute("orderCode", orderCode);
							dbOp.commitTransaction();
							Calendar printEndTime=Calendar.getInstance();//打印时间，用于输出包裹单打印时间
							long printTime=printEndTime.getTimeInMillis()-printStartTime.getTimeInMillis();
//							System.out.println("包裹单打印：订单号"+orderCode+"，打印时间"+printTime+"毫秒");
							if(order.getBuyMode() != 0){
								return mapping.findForward("pingyou");
							}else{
								return mapping.findForward("inProPackage");
							}
						}
//						else if (deliver == 13 || deliver == 14 || deliver == 15 || deliver == 16 || deliver == 17 || deliver == 18 || deliver == 19 || deliver == 20 || deliver == 21 || deliver == 22) {
//							// 深圳自建，通路速递，赛奥递，如风达，通路速递广东，通路速递浙江，银捷速递，如风达浙江
//							request.setAttribute("orderCode", orderCode);
//							MailingBalanceBean mbBean = balanceService
//									.getMailingBalance("order_code='"
//											+ orderCode + "'");
//							request.setAttribute("mbBean", mbBean);
//							AuditPackageBean apBean2 = stockService
//									.getAuditPackage("order_code='" + orderCode
//											+ "'");// 重量是最新的数据
//							request.setAttribute("apBean", apBean2);
//							adminService.getDbOperation().commitTransaction();
//							Calendar printEndTime=Calendar.getInstance();//打印时间，用于输出包裹单打印时间
//							long printTime=printEndTime.getTimeInMillis()-printStartTime.getTimeInMillis();
////							System.out.println("包裹单打印：订单号"+orderCode+"，打印时间"+printTime+"毫秒");
//							return mapping.findForward("szzj");
//						}
//						else if(deliver==10){
//							request.setAttribute("orderCode", orderCode);
//							MailingBalanceBean mbBean = balanceService.getMailingBalance("order_code='"+ orderCode + "'");
//							request.setAttribute("mbBean", mbBean);
//							return mapping.findForward("gzzjs");
//						}
						
					}
				}
				request.setAttribute("checkStatus", checkStatus + "");
				if (request.getParameter("orderCode2") != null) {
					request.setAttribute("orderCode",
							request.getParameter("orderCode2"));
				} else {
					if (request.getParameter("checkStatus") != null
							&& request.getParameter("checkStatus").equals("8")) {
						request.setAttribute("checkStatus", "8");
					}
					request.setAttribute("orderCode", orderCode);
				}
				request.setAttribute("weight", (weight / 1000) + "");
				dbOp.commitTransaction();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				stockService.releaseAll();
				stockSlaveService.releaseAll();
				wareService.releaseAll();
			}
		}
		return mapping.findForward("printPackage");
	}

	public String toUpperNumber(String downNumber) {
		String[] upperArray = downNumber.split("\\.");// 区分元和角分
		String[] upperNumber = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌",
				"玖" };
		String[] upper = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾",
				"佰", "仟" };
		String upperPrice = "";// 大写金额字符串
		if (upperArray.length > 0) {
			int len = upperArray[0].length();// 整数元的位数
			for (int i = 0; i < len; i++) {
				int number = Integer.parseInt(upperArray[0].substring(
						upperArray[0].length() - i - 1, upperArray[0].length()
								- i));// i位上的数字
				upperPrice = upperNumber[number] + upper[i] + upperPrice;
			}
		}
		if (upperArray.length > 1) {// 有角分的情况
			int number1 = Integer.parseInt(upperArray[1].substring(0, 1));// 角
			upperPrice = upperPrice + upperNumber[number1] + "角";
			if (upperArray[1].length() > 1) {
				int number2 = Integer.parseInt(upperArray[1].substring(1, 2));// 分
				upperPrice = upperPrice + upperNumber[number2] + "分";
			}
		}
		upperPrice = upperPrice.replaceAll("零角", "");
		upperPrice = upperPrice.replaceAll("零分", "");
		upperPrice = upperPrice.replaceAll("零拾", "零");
		upperPrice = upperPrice.replaceAll("零佰", "零");
		upperPrice = upperPrice.replaceAll("零仟", "零");
		upperPrice = upperPrice.replaceAll("零+", "零");
		upperPrice = upperPrice.replaceAll("零元", "元");
		upperPrice = upperPrice.replaceAll("零万", "万");
		upperPrice = upperPrice.replaceAll("零亿", "亿");
		upperPrice = upperPrice.replaceAll("亿万", "亿");
		if (upperPrice.endsWith("元")) {
			upperPrice = upperPrice + "整";
		}
		return upperPrice;
	}
	
	public String calLogisticsCost(IStockService stockService, voOrder order,
			AuditPackageBean apBean) {
		//系统生成物流成本
		try{
			
			int deliver1 = order.getDeliver();
			int balanceType = 0;
			// 根据 快递公司，确定结算来源
			if(voOrder.deliverToBalanceTypeMap.containsKey(deliver1+"")){
				balanceType=Integer.parseInt(voOrder.deliverToBalanceTypeMap.get(deliver1+"").toString());
			}
			stockService.getDbOp().prepareStatement("SELECT add_id1,add_id2,add_id3 FROM user_order_extend_info WHERE order_code = '"+ order.getCode() + "'");
			PreparedStatement ps3 = stockService.getDbOp().getPStmt();
			ResultSet rs3 = ps3.executeQuery();
			String destProvince = null;
			String destCity = null;
			String destDistrict = null;
			while(rs3.next()){
				destProvince = rs3.getString("add_id1");
				destCity = rs3.getString("add_id2");
				destDistrict = rs3.getString("add_id3");
			}
			ps3.close();
			rs3.close();
			
			
			Map<String,String> argMap = new HashMap<String, String>();
			argMap.put("orderId", ""+order.getId());
			argMap.put("price", ""+order.getDprice());
			argMap.put("weight", ""+apBean.getWeight());
			argMap.put("express", ""+balanceType);
			argMap.put("balanceArea", ""+apBean.getAreano());
			argMap.put("destProvince", destProvince);
			argMap.put("destCity", destCity);
			argMap.put("buyMode", ""+order.getBuyMode());
			argMap.put("destArea", destDistrict);
			if(debugLog.isInfoEnabled()){
				debugLog.info("orderId:"+order.getId());
				debugLog.info("price:"+order.getDprice());
				debugLog.info("weight:"+apBean.getWeight());
				debugLog.info("express:"+balanceType);
				debugLog.info("balanceArea:"+apBean.getAreano());
				debugLog.info("destProvince:"+ destProvince);
				debugLog.info("destCity:"+destCity);
				debugLog.info("buyMode:"+order.getBuyMode());
				debugLog.info("destArea:"+destDistrict);
				debugLog.info("------------------------------");
			}
			FinanceCache.addCharge(argMap, stockService.getDbOp());
		}catch(Exception e){
			System.out.println("系统生成物流成本失败");
			e.printStackTrace();
			return "生成物流成本异常！";
		}
		return null;
	}
}
