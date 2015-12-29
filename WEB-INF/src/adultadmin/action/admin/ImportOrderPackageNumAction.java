/**
 * 
 */
package adultadmin.action.admin;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.stock.OrderPackageAction;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderImportLogBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISMSService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;

/**
 * @author Bomb
 *
 */
public class ImportOrderPackageNumAction  extends BaseAction{

	public static byte[] lock = new byte[0];
	
	public static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
	
	/**
	 *  
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		int type = StringUtil.StringToId(request.getParameter("type"));
		String content = StringUtil.dealParam(request.getParameter("content"));

		voUser user = (voUser) request.getSession().getAttribute("userView");

		if(type < 0 || content== null || content.length() == 0){
			return mapping.findForward(IConstants.SUCCESS_KEY);
		}
		
		synchronized(lock){
			String splitKey = "\t";
			WareService wareService = new WareService();
			IAdminLogService logService = ServiceFactory.createAdminLogService();
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			//cxq---
			try {
				String line = null;
				BufferedReader br = new BufferedReader(new StringReader(content));

				StringBuffer error = new StringBuffer();
				if(type == 0){ // 导入订单包裹单号
					
					while((line = br.readLine()) != null ){
						String[] lines = line.split(splitKey);
						if(lines != null && lines.length == 2){
							String orderCode = lines[0].trim();
							String packageNum = lines[1].trim();
							voOrder order = wareService.getOrder("code = '" + orderCode + "' and status in (6,11,12,13,14)");
							if(order != null){
								OrderStockBean orderStock=stockService.getOrderStock("order_id="+order.getId()+" and status!=3");
								if(orderStock==null){
									error.append(line).append("\t订单出库信息错误");
									error.append("\r\n");
									continue;
								}
								//该员工可操作的区域列表
					        	List areaList = CargoDeptAreaService. getCargoDeptAreaList(request);
					        	if(!areaList.contains(orderStock.getStockArea()+"")){
					        		error.append(line).append("\t只能修改用户所属的库地区的订单记录的包裹单号");
									error.append("\r\n");
									continue;
					        	}
					        	wareService.getDbOp().startTransaction();
								//----------开始导入包裹单
								OrderPackageAction orderPackageAction=new OrderPackageAction();
								int checkStatus=orderPackageAction.importPackage(order,packageNum,user,request,wareService.getDbOp(),false);
								if(checkStatus==4){
									error.append(line).append("\t快递公司设置有误");
									error.append("\r\n");
									continue;
								}else if(checkStatus!=7){
									error.append(line).append("\t导入失败");
									error.append("\r\n");
									continue;
								}
								//-----结束导入包裹单
								wareService.getDbOp().commitTransaction();
							} else {
								error.append(line).append("\t没有找到订单");
								error.append("\r\n");
							}
						} else {
							error.append(line).append("\t数据格式有误");
							error.append("\r\n");
						}
					}
					
				}else if(type == 19){ // 导入EMS订单包裹单号
					while((line = br.readLine()) != null ){
						String[] lines = line.split(splitKey);
						if(lines != null && lines.length == 2){
							String orderCode = lines[0].trim();
							String packageNum = lines[1].trim();
							voOrder order = wareService.getOrder("code = '" + orderCode + "' and status in (6,11,12,13,14)");
				//			if(order != null && !packageNum.equals(order.getPackageNum())){
							if(order != null){
								int deliver = order.getDeliver();
								if(deliver!=9&&deliver!=11){
									error.append(line).append("\t非EMS订单");
									error.append("\r\n");
									continue;
								}
								OrderStockBean orderStock=stockService.getOrderStock("order_id="+order.getId()+" and status!=3");
								if(orderStock==null){
									error.append(line).append("\t订单出库信息错误");
									error.append("\r\n");
									continue;
								}
								//该员工可操作的区域列表
					        	List areaList = CargoDeptAreaService. getCargoDeptAreaList(request);
					        	if(!areaList.contains(orderStock.getStockArea()+"")){
					        		error.append(line).append("\t只能修改用户所属的库地区的订单记录的包裹单号");
									error.append("\r\n");
									continue;
					        	}
					        	wareService.getDbOp().startTransaction();
								//----------开始导入包裹单
								OrderPackageAction orderPackageAction=new OrderPackageAction();
								int checkStatus=orderPackageAction.importPackage(order,packageNum,user,request,wareService.getDbOp(),false);
								if(checkStatus==4){
									error.append(line).append("\t快递公司设置有误");
									error.append("\r\n");
									continue;
								}else if(checkStatus!=7){
									error.append(line).append("\t导入失败");
									error.append("\r\n");
									continue;
								}
								//-----结束导入包裹单
								wareService.getDbOp().commitTransaction();
							} else {
								error.append(line).append("\t没有找到订单");
								error.append("\r\n");
							}
						} else {
							error.append(line).append("\t数据格式有误");
							error.append("\r\n");
						}
					}
				} else {
					String tip = "对不起，暂时不提供该种操作方式！";
					request.setAttribute("tip", tip);
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				OrderImportLogBean log = new OrderImportLogBean();
				log.setContent(content);
				log.setCreateDatetime(DateUtil.getNow());
				log.setUserId(user.getId());
				log.setType(type);
				logService.addOrderImportLog(log);

				String tip = null;
				if(error.length() > 0){
					tip = "操作完成，但是有错误.";
				} else {
					tip = "操作完成.";
				}

				request.setAttribute("tip", tip);
				request.setAttribute("error", error);
			} catch(Exception e){
				e.printStackTrace();
				String tip = "对不起，操作失败，请重试！";
				request.setAttribute("tip", tip);
				return mapping.findForward(IConstants.FAILURE_KEY);
			} finally {
				logService.releaseAll();
				stockService.releaseAll();
			}
		}
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
	//含有优惠券的短信发送
	private void sendSMS(voUser user, voOrder order,Map ticketsInfo){
		//为表感谢，我们随包裹赠送XXX元代金券，收取包裹后即可在下次购物时使用。感谢信赖买卖宝！网址http://mmb.cn
		String content = "为表感谢，我们随包裹赠送%cashTicketNum%元代金券，收取包裹后即可在下次购物时使用。感谢信赖买卖宝！网址http://mmb.cn";
		int count=StringUtil.StringToId((String)ticketsInfo.get("count"));
		content = content.replace("%cashTicketNum%", count*10+"");
		SenderSMS3.send(user.getId(), order.getPhone(), content);
	}
	private void sendSMS(voUser user, voOrder order){
		String content = "您的买卖宝订单%orderCode%于%datetime%从%area%发货,3-5天送达.包裹单号%packageNum%。";
		if(order.getPackageNum().length() == 10 && StringUtil.isNumeric(order.getPackageNum())){  //广宅
			content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过宅急送发货,预计2-4天送达,快递单号%packageNum%," +
			  		  "您可拨打4006789000了解送达详情.感谢信赖买卖宝!网址http://mmb.cn";
		} else if(order.getDeliver()==13){  //深圳自建
			content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过深圳自建发货,预计1-3天送达,快递单号%packageNum%," +
	  		  "您可拨打4008111111了解送达详情.感谢信赖买卖宝!网址http://mmb.cn";
		} else if(order.getPackageNum().length() == 12 && StringUtil.isNumeric(order.getPackageNum())){  //顺丰
			content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过顺丰发货,预计3-5天送达,快递单号%packageNum%," +
	  		  "您可拨打4008111111了解送达详情.感谢信赖买卖宝!网址http://mmb.cn";
		} else if(order.getPackageNum().endsWith("CN")){
			content = "您的产品已发出,包裹单%packageNum%,3-5天左右送达.恶劣天气可能会延误,拨11185可查详情";
		} else if(order.getPackageNum().endsWith("GD")) {  //EMS省内
			content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过EMS发货,预计1-2天送达,快递单号%packageNum%," +
			  "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址http://mmb.cn";
		} else if(order.getPackageNum().startsWith("6") || order.getPackageNum().startsWith("16")){
			content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。拨4006789000查包裹详情";
		} else if(order.getPackageNum().startsWith("EC")){  //EMS省外
			content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过EMS发货,预计3-5天送达,快递单号%packageNum%," +
					  "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址http://mmb.cn";
		} else if(order.getDeliver()==14){
			content = "您订购的%productName%（订单号%orderCode%）已通过通路速递发货,预计3-5天送达,快递单号%packageNum%," +
			  "您可拨打02034013149了解送达详情.感谢信赖买卖宝!网址http://mmb.cn";
		} else if(order.getDeliver()==15||order.getDeliver()==17||order.getDeliver()==18){//赛澳递江苏，赛澳递上海
			content = "您订购的%productName%（订单号%orderCode%）已通过赛奥递发货,预计3-5天送达,快递单号%packageNum%," +
			  "您可拨打02034013149了解送达详情.感谢信赖买卖宝!网址http://mmb.cn";
		} else if(order.getDeliver()==16){//如风达
			content = "您订购的%productName%（订单号%orderCode%）已通过如风达发货,预计3-5天送达,快递单号%packageNum%," +
			  "您可拨打02034013149了解送达详情.感谢信赖买卖宝!网址http://mmb.cn";
		} else {
//			content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。";
			if(order.getAddress().startsWith("广东省")){//客户地址是广东省开头
				content="您订购的%productName%（订单号%orderCode%）已通过EMS发货，预计3-5天送达,快递单号%packageNum%," +
						"您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址http://mmb.cn";
			}else{//客户地址不是广东省开头
				content="您订购的%productName%（订单号%orderCode%）已通过EMS发货，预计3-5天送达,快递单号%packageNum%," +
						"您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址http://mmb.cn";
			}
		}
		
		WareService wareService = new WareService();
		DbOperation dbOp = new DbOperation();
		dbOp.init("sms");
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			content = content.replace("%name%", order.getName());
			content = content.replace("%orderCode%", order.getCode());
			if(order.getStockOper() != null){
				content = content.replace("%datetime%", order.getStockOper().getLastOperTime().substring(5, 10));
				if(order.getStockOper().getArea() == 0){
					content = content.replace("%area%", "北京");
				} else if(order.getStockOper().getArea() == 1){
					content = content.replace("%area%", "广州");
				}
			} else {
				content = content.replace("%datetime%", DateUtil.getNow());
				content = content.replace("%area%", "北京");
			}
			content = content.replace("%price%", String.valueOf(order.getProductPrice()));
			content = content.replace("%packageNum%", order.getPackageNum());
			content = content.replace("%totalPrice%", df.format(order.getDprice()));
			if(content.indexOf("%productName%")>0){
				String productName = "";
				List orderProductList = wareService.getOrderProducts("a.order_id = "+order.getId()+" order by b.price desc");
				voOrderProduct orderProduct = (voOrderProduct)orderProductList.get(0);
				voProduct product = wareService.getProduct(orderProduct.getProductId());
				
				productName = product.getName();
				if(productName.length() > 13){
					productName = productName.substring(0, 13);
				}
				if(orderProductList.size() > 1){
					productName = productName + "等";
				}
				content = content.replace("%productName%", productName);
				if(content.length() > 128){
					content = content.replace("感谢信赖买卖宝！", "买卖宝");
				}
			}
			SenderSMS3.send(user.getId(), order.getPhone(), content);



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
			wareService.releaseAll();
		}
	}

	private void addLogContent(StringBuilder logContent,int oriStatus, voOrder vo, voUser loginUser){
        if(logContent.length() > 0){
        	IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
        	try{
        		OrderAdminLogBean log = new OrderAdminLogBean();
        		log.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
        		log.setUserId(loginUser.getId());
        		log.setUsername(loginUser.getUsername());
        		log.setOrderId(vo.getId());
        		log.setOrderCode(vo.getCode());
        		log.setCreateDatetime(DateUtil.getNow());
        		log.setContent(logContent.toString());
        		logService.addOrderAdminStatusLog(oriStatus, vo.getStatus(), -1, -1, log);
        		logService.addOrderAdminLog(log);
        	} catch(Exception e) {
        		e.printStackTrace();
        	} finally {
        		logService.releaseAll();
        	}
        }
	}
}