package adultadmin.action.stock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.chinasms.sms.SenderSMS3;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderImportLogBean;
import adultadmin.bean.PagingBean;
import adultadmin.bean.balance.BalanceCycleBean;
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
import adultadmin.service.infc.ISMSService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class AuditPackageAction extends DispatchAction{
	public static byte[] AuditPackageLock = new byte[0];
	/**
	 * 
	 * 扫描订单和核对包裹
	 */
	public ActionForward auditPackage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(AuditPackageLock){
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp); 
		IBatchBarcodeService batchBarcodeService=ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE,dbOp);
		IBalanceService balanceService=ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			stockService.getDbOp().startTransaction();
			int checkStatus=-1;
			String orderCode=StringUtil.dealParam(StringUtil.convertNull(request.getParameter("orderCode")));//订单编号
			if(!orderCode.equals("")){
				voOrder order=wareService.getOrder("code='"+orderCode+"'");
				AuditPackageBean apBean=stockService.getAuditPackage("order_code='"+orderCode+"'");
				OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+orderCode+"'");
				if(order==null||apBean==null){//没有数据
					checkStatus=2;
				}else if(apBean.getStatus()==1){//已分拣，未复核出库
					checkStatus=1;
				}else if(apBean.getStatus()==2){//已复核出库，未核对包裹
					checkStatus=0;
					//核对包裹
					stockService.updateAuditPackage("status=3,audit_package_datetime='"+DateUtil.getNow()+"',audit_package_user_name='"+user.getUsername()+"'", "order_id="+order.getId());
				}else if(apBean.getStatus()==3){//已核对包裹，未导入包裹单
					checkStatus=3;
				}else if(apBean.getStatus()==4){//已导入包裹单
					if(apBean.getAuditPackageDatetime()==null){//未核对包裹
						checkStatus=0;
						stockService.updateAuditPackage("audit_package_datetime='"+DateUtil.getNow()+"',audit_package_user_name='"+user.getUsername()+"'", "order_id="+order.getId());
					}else{//已核对包裹
						checkStatus=3;
					}
				}
				
				if(order!=null){
					if(ocBean!=null){
						order.setSerialNumber(ocBean.getSerialNumber());
					}
					request.setAttribute("order", order);
					OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
					if(osBean!=null){
						OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
						if(ospBean!=null){
							request.setAttribute("ospBean", ospBean);
						}
					}
				}
				AuditPackageBean apBean2=stockService.getAuditPackage("order_code='"+orderCode+"'");//最新核对包裹记录
				if(apBean2!=null){
					request.setAttribute("apBean", apBean2);
				}
				request.setAttribute("checkStatus", checkStatus+"");
			}
			
			String packageCode=StringUtil.dealParam(StringUtil.convertNull(request.getParameter("packageCode")));//包裹单号
			String orderId=StringUtil.dealParam(StringUtil.convertNull(request.getParameter("orderId")));//刚刚扫描的订单id
			if(!packageCode.equals("")){
				voOrder order=wareService.getOrder(Integer.parseInt(orderId));
				if(order==null){
					checkStatus=9;
					request.setAttribute("checkStatus", checkStatus+"");
					return mapping.findForward("auditPackage");
				}
				if(!(order.getStatusName().equals("已发货")||
						order.getStatusName().equals("已妥投")||
						order.getStatusName().equals("已结算")||
						order.getStatusName().equals("待退回")||
						order.getStatusName().equals("已退回"))){
					checkStatus=10;
					request.setAttribute("checkStatus", checkStatus+"");
					return mapping.findForward("auditPackage");
				}
				
				AuditPackageBean apBean=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
				if(apBean.getStatus()==4){	//该订单已导入包裹单
					String confirm=request.getParameter("confirm");
					if(confirm.equals("1")){	//确定导入该包裹单
						//是否有包裹单号重复
						List mBeanList=balanceService.getMailingBalanceList("packagenum='"+packageCode+"' and order_code<>'"+order.getCode()+"'", -1, -1, null);
						if(mBeanList.size()!=0){	//有别的订单的包裹单号与此包裹单号重复
							String confirm2=request.getParameter("confirm2");//有重复时是否导入
							if(confirm2.equals("1")){	//确定导入该包裹单
								//----------开始导入包裹单
								// 结算单 相关数据的设置
								//设置 结算单的 包裹单号、结算周期、结算来源
								int deliver = order.getDeliver();
								int balanceType = 0;
								// 根据 快递公司，确定结算来源
								if(deliver == 7 || deliver == 4){
									// 北速
									balanceType = 1;
								} else if(deliver == 8){
									//广东省内
									balanceType = 2;
								} else if(deliver == 9){
									//广东省外
									balanceType = 4;
								} else if(deliver == 10){
									//广州宅急送
									balanceType = 3;
								}  else if(deliver == 11){
									//广东省速递局
									balanceType = 5;
								} else {
									// 如果没有找到 相应的快递公司，就报错
									OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
									order.setSerialNumber(ocBean.getSerialNumber());
									request.setAttribute("order", order);
									AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
									request.setAttribute("apBean", apBean2);
									OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
									if(osBean!=null){
										OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
										if(ospBean!=null){
											request.setAttribute("ospBean", ospBean);
										}
									}
									checkStatus=8;
									request.setAttribute("checkStatus", checkStatus+"");
									return mapping.findForward("auditPackage");
								}
								
								// 结算单 相关数据的设置
								order.setPackageNum(packageCode);
								order.setStockoutRemark("");
								wareService.modifyOrder(order);
								order.setStockOper(stockService.getStockOperation("type=0 and status=2 and order_code='" + order.getCode() + "'"));
								StringBuilder buf = new StringBuilder();
								List pList = wareService.getOrderProducts(order.getId());
								Iterator iter = pList.listIterator();
								while(iter.hasNext()){
									voOrderProduct vo = (voOrderProduct)iter.next();
									buf.append(vo.getName());
									buf.append("(");
									buf.append(vo.getCount());
									buf.append(")");
								}
								pList = wareService.getOrderPresents(order.getId());
								iter = pList.listIterator();
								while(iter.hasNext()){
									voOrderProduct vo = (voOrderProduct)iter.next();
									buf.append(vo.getName());
									buf.append("(");
									buf.append(vo.getCount());
									buf.append(")");
								}
								order.setProducts(buf.toString());
								sendSMS(user, order); // 订单已发货，发送短信通知客户
								
								// 根据当前日期 计算 订单所属的结算周期
								MailingBalanceBean mb = balanceService.getMailingBalance("order_id=" + order.getId());
								String stockoutDate = StringUtil.cutString(mb.getStockoutDatetime(), 10);
								StringBuilder qbuf = new StringBuilder();
								qbuf.append("balance_type=").append(balanceType).append(" and ");
								qbuf.append("balance_cycle_start <= '").append(stockoutDate).append("' and balance_cycle_end >= '").append(stockoutDate).append("' ");
								BalanceCycleBean bcb = balanceService.getBalanceCycle(qbuf.toString());
								if(bcb == null){
									OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
									order.setSerialNumber(ocBean.getSerialNumber());
									request.setAttribute("order", order);
									AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
									request.setAttribute("apBean", apBean2);
									OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
									if(osBean!=null){
										OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
										if(ospBean!=null){
											request.setAttribute("ospBean", ospBean);
										}
									}
									checkStatus=7;
									request.setAttribute("checkStatus", checkStatus+"");
									return mapping.findForward("auditPackage");
								}
								qbuf.delete(0, qbuf.length());
								qbuf.append("balance_status=").append(MailingBalanceBean.BALANCE_STATUS_UNDEAL).append(", ");
								qbuf.append("packagenum='").append(packageCode).append("', ");
								qbuf.append("balance_type=").append(balanceType).append(", ");
								qbuf.append("balance_cycle_start='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("', ");
								qbuf.append("balance_cycle_end='").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("', ");
								qbuf.append("balance_cycle='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("~").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("' ");
								balanceService.updateMailingBalance(qbuf.toString(), "order_id=" + order.getId());
								
								//添加日志
								OrderImportLogBean log = new OrderImportLogBean();
								log.setContent(order.getCode()+"\t"+packageCode);
								log.setCreateDatetime(DateUtil.getNow());
								log.setUserId(user.getId());
								log.setType(0);
								logService.addOrderImportLog(log);
									
								stockService.updateAuditPackage("package_code='"+packageCode+"',status=4", "order_id="+order.getId());
								checkStatus=5;
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
								checkStatus=4;
								request.setAttribute("otherCode", otherCode);
							}
						}else{//没有包裹单号重复
							//----------开始导入包裹单
							// 结算单 相关数据的设置
							//设置 结算单的 包裹单号、结算周期、结算来源
							int deliver = order.getDeliver();
							int balanceType = 0;
							// 根据 快递公司，确定结算来源
							if(deliver == 7 || deliver == 4){
								// 北速
								balanceType = 1;
							} else if(deliver == 8){
								//广东省内
								balanceType = 2;
							} else if(deliver == 9){
								//广东省外
								balanceType = 4;
							} else if(deliver == 10){
								//广州宅急送
								balanceType = 3;
							}  else if(deliver == 11){
								//广东省速递局
								balanceType = 5;
							} else {
								// 如果没有找到 相应的快递公司，就报错
								OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
								order.setSerialNumber(ocBean.getSerialNumber());
								request.setAttribute("order", order);
								AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
								request.setAttribute("apBean", apBean2);
								OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
								if(osBean!=null){
									OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
									if(ospBean!=null){
										request.setAttribute("ospBean", ospBean);
									}
								}
								checkStatus=8;
								request.setAttribute("checkStatus", checkStatus+"");
								return mapping.findForward("auditPackage");
							}
							
							// 结算单 相关数据的设置
							order.setPackageNum(packageCode);
							order.setStockoutRemark("");
							wareService.modifyOrder(order);
							order.setStockOper(stockService.getStockOperation("type=0 and status=2 and order_code='" + order.getCode() + "'"));
							StringBuilder buf = new StringBuilder();
							List pList = wareService.getOrderProducts(order.getId());
							Iterator iter = pList.listIterator();
							while(iter.hasNext()){
								voOrderProduct vo = (voOrderProduct)iter.next();
								buf.append(vo.getName());
								buf.append("(");
								buf.append(vo.getCount());
								buf.append(")");
							}
							pList = wareService.getOrderPresents(order.getId());
							iter = pList.listIterator();
							while(iter.hasNext()){
								voOrderProduct vo = (voOrderProduct)iter.next();
								buf.append(vo.getName());
								buf.append("(");
								buf.append(vo.getCount());
								buf.append(")");
							}
							order.setProducts(buf.toString());
							sendSMS(user, order); // 订单已发货，发送短信通知客户
							
							// 根据当前日期 计算 订单所属的结算周期
							MailingBalanceBean mb = balanceService.getMailingBalance("order_id=" + order.getId());
							String stockoutDate = StringUtil.cutString(mb.getStockoutDatetime(), 10);
							StringBuilder qbuf = new StringBuilder();
							qbuf.append("balance_type=").append(balanceType).append(" and ");
							qbuf.append("balance_cycle_start <= '").append(stockoutDate).append("' and balance_cycle_end >= '").append(stockoutDate).append("' ");
							BalanceCycleBean bcb = balanceService.getBalanceCycle(qbuf.toString());
							if(bcb == null){
								OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
								order.setSerialNumber(ocBean.getSerialNumber());
								request.setAttribute("order", order);
								AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
								request.setAttribute("apBean", apBean2);
								OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
								if(osBean!=null){
									OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
									if(ospBean!=null){
										request.setAttribute("ospBean", ospBean);
									}
								}
								checkStatus=7;
								request.setAttribute("checkStatus", checkStatus+"");
								return mapping.findForward("auditPackage");
							}
							qbuf.delete(0, qbuf.length());
							qbuf.append("balance_status=").append(MailingBalanceBean.BALANCE_STATUS_UNDEAL).append(", ");
							qbuf.append("packagenum='").append(packageCode).append("', ");
							qbuf.append("balance_type=").append(balanceType).append(", ");
							qbuf.append("balance_cycle_start='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("', ");
							qbuf.append("balance_cycle_end='").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("', ");
							qbuf.append("balance_cycle='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("~").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("' ");
							balanceService.updateMailingBalance(qbuf.toString(), "order_id=" + order.getId());
							
							//添加日志
							OrderImportLogBean log = new OrderImportLogBean();
							log.setContent(order.getCode()+"\t"+packageCode);
							log.setCreateDatetime(DateUtil.getNow());
							log.setUserId(user.getId());
							log.setType(0);
							logService.addOrderImportLog(log);
								
							stockService.updateAuditPackage("package_code='"+packageCode+"',status=4", "order_id="+order.getId());
							checkStatus=5;
							//-----结束导入包裹单
						}
					}else{//未确定导入包裹单
						checkStatus=6;
						request.setAttribute("oriOrderCode", order.getCode());
						request.setAttribute("oriPackageCode", apBean.getPackageCode());
					}
				}else{	//该订单未导入包裹单
					//是否有包裹单号重复
					List mBeanList=balanceService.getMailingBalanceList("packagenum='"+packageCode+"' and order_code<>'"+order.getCode()+"'", -1, -1, null);
					if(mBeanList.size()!=0){	//有包裹单号重复
						if(request.getParameter("confirm2").equals("1")){	//确定导入该包裹单
							//----------开始导入包裹单
							// 结算单 相关数据的设置
							//设置 结算单的 包裹单号、结算周期、结算来源
							int deliver = order.getDeliver();
							int balanceType = 0;
							// 根据 快递公司，确定结算来源
							if(deliver == 7 || deliver == 4){
								// 北速
								balanceType = 1;
							} else if(deliver == 8){
								//广东省内
								balanceType = 2;
							} else if(deliver == 9){
								//广东省外
								balanceType = 4;
							} else if(deliver == 10){
								//广州宅急送
								balanceType = 3;
							}  else if(deliver == 11){
								//广东省速递局
								balanceType = 5;
							} else {
								// 如果没有找到 相应的快递公司，就报错
								OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
								order.setSerialNumber(ocBean.getSerialNumber());
								request.setAttribute("order", order);
								AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
								request.setAttribute("apBean", apBean2);
								OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
								if(osBean!=null){
									OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
									if(ospBean!=null){
										request.setAttribute("ospBean", ospBean);
									}
								}
								checkStatus=8;
								request.setAttribute("checkStatus", checkStatus+"");
								return mapping.findForward("auditPackage");
							}
							
							// 结算单 相关数据的设置
							order.setPackageNum(packageCode);
							order.setStockoutRemark("");
							wareService.modifyOrder(order);
							order.setStockOper(stockService.getStockOperation("type=0 and status=2 and order_code='" + order.getCode() + "'"));
							StringBuilder buf = new StringBuilder();
							List pList = wareService.getOrderProducts(order.getId());
							Iterator iter = pList.listIterator();
							while(iter.hasNext()){
								voOrderProduct vo = (voOrderProduct)iter.next();
								buf.append(vo.getName());
								buf.append("(");
								buf.append(vo.getCount());
								buf.append(")");
							}
							pList = wareService.getOrderPresents(order.getId());
							iter = pList.listIterator();
							while(iter.hasNext()){
								voOrderProduct vo = (voOrderProduct)iter.next();
								buf.append(vo.getName());
								buf.append("(");
								buf.append(vo.getCount());
								buf.append(")");
							}
							order.setProducts(buf.toString());
							sendSMS(user, order); // 订单已发货，发送短信通知客户
							
							// 根据当前日期 计算 订单所属的结算周期
							MailingBalanceBean mb = balanceService.getMailingBalance("order_id=" + order.getId());
							String stockoutDate = StringUtil.cutString(mb.getStockoutDatetime(), 10);
							StringBuilder qbuf = new StringBuilder();
							qbuf.append("balance_type=").append(balanceType).append(" and ");
							qbuf.append("balance_cycle_start <= '").append(stockoutDate).append("' and balance_cycle_end >= '").append(stockoutDate).append("' ");
							BalanceCycleBean bcb = balanceService.getBalanceCycle(qbuf.toString());
							if(bcb == null){
								OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
								order.setSerialNumber(ocBean.getSerialNumber());
								request.setAttribute("order", order);
								AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
								request.setAttribute("apBean", apBean2);
								OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
								if(osBean!=null){
									OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
									if(ospBean!=null){
										request.setAttribute("ospBean", ospBean);
									}
								}
								checkStatus=7;
								request.setAttribute("checkStatus", checkStatus+"");
								return mapping.findForward("auditPackage");
							}
							qbuf.delete(0, qbuf.length());
							qbuf.append("balance_status=").append(MailingBalanceBean.BALANCE_STATUS_UNDEAL).append(", ");
							qbuf.append("packagenum='").append(packageCode).append("', ");
							qbuf.append("balance_type=").append(balanceType).append(", ");
							qbuf.append("balance_cycle_start='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("', ");
							qbuf.append("balance_cycle_end='").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("', ");
							qbuf.append("balance_cycle='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("~").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("' ");
							balanceService.updateMailingBalance(qbuf.toString(), "order_id=" + order.getId());
							
							//添加日志
							OrderImportLogBean log = new OrderImportLogBean();
							log.setContent(order.getCode()+"\t"+packageCode);
							log.setCreateDatetime(DateUtil.getNow());
							log.setUserId(user.getId());
							log.setType(0);
							logService.addOrderImportLog(log);
								
							stockService.updateAuditPackage("package_code='"+packageCode+"',status=4", "order_id="+order.getId());
							checkStatus=5;
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
							request.setAttribute("otherCode", otherCode);
							checkStatus=4;
						}
					}else{//没有包裹单重复,导入包裹单
						//----------开始导入包裹单
						// 结算单 相关数据的设置
						//设置 结算单的 包裹单号、结算周期、结算来源
						int deliver = order.getDeliver();
						int balanceType = 0;
						// 根据 快递公司，确定结算来源
						if(deliver == 7 || deliver == 4){
							// 北速
							balanceType = 1;
						} else if(deliver == 8){
							//广东省内
							balanceType = 2;
						} else if(deliver == 9){
							//广东省外
							balanceType = 4;
						} else if(deliver == 10){
							//广州宅急送
							balanceType = 3;
						}  else if(deliver == 11){
							//广东省速递局
							balanceType = 5;
						} else {
							// 如果没有找到 相应的快递公司，就报错
							OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
							order.setSerialNumber(ocBean.getSerialNumber());
							request.setAttribute("order", order);
							AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
							request.setAttribute("apBean", apBean2);
							OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
							if(osBean!=null){
								OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
								if(ospBean!=null){
									request.setAttribute("ospBean", ospBean);
								}
							}
							checkStatus=8;
							request.setAttribute("checkStatus", checkStatus+"");
							return mapping.findForward("auditPackage");
						}
						
						// 结算单 相关数据的设置
						order.setPackageNum(packageCode);
						order.setStockoutRemark("");
						wareService.modifyOrder(order);
						order.setStockOper(stockService.getStockOperation("type=0 and status=2 and order_code='" + order.getCode() + "'"));
						StringBuilder buf = new StringBuilder();
						List pList = wareService.getOrderProducts(order.getId());
						Iterator iter = pList.listIterator();
						while(iter.hasNext()){
							voOrderProduct vo = (voOrderProduct)iter.next();
							buf.append(vo.getName());
							buf.append("(");
							buf.append(vo.getCount());
							buf.append(")");
						}
						pList = wareService.getOrderPresents(order.getId());
						iter = pList.listIterator();
						while(iter.hasNext()){
							voOrderProduct vo = (voOrderProduct)iter.next();
							buf.append(vo.getName());
							buf.append("(");
							buf.append(vo.getCount());
							buf.append(")");
						}
						order.setProducts(buf.toString());
						sendSMS(user, order); // 订单已发货，发送短信通知客户
						
						// 根据当前日期 计算 订单所属的结算周期
						MailingBalanceBean mb = balanceService.getMailingBalance("order_id=" + order.getId());
						String stockoutDate = StringUtil.cutString(mb.getStockoutDatetime(), 10);
						StringBuilder qbuf = new StringBuilder();
						qbuf.append("balance_type=").append(balanceType).append(" and ");
						qbuf.append("balance_cycle_start <= '").append(stockoutDate).append("' and balance_cycle_end >= '").append(stockoutDate).append("' ");
						BalanceCycleBean bcb = balanceService.getBalanceCycle(qbuf.toString());
						if(bcb == null){
							OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
							order.setSerialNumber(ocBean.getSerialNumber());
							request.setAttribute("order", order);
							AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
							request.setAttribute("apBean", apBean2);
							OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
							if(osBean!=null){
								OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
								if(ospBean!=null){
									request.setAttribute("ospBean", ospBean);
								}
							}
							checkStatus=7;
							request.setAttribute("checkStatus", checkStatus+"");
							return mapping.findForward("auditPackage");
						}
						qbuf.delete(0, qbuf.length());
						qbuf.append("balance_status=").append(MailingBalanceBean.BALANCE_STATUS_UNDEAL).append(", ");
						qbuf.append("packagenum='").append(packageCode).append("', ");
						qbuf.append("balance_type=").append(balanceType).append(", ");
						qbuf.append("balance_cycle_start='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("', ");
						qbuf.append("balance_cycle_end='").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("', ");
						qbuf.append("balance_cycle='").append(StringUtil.cutString(bcb.getBalanceCycleStart(), 10)).append("~").append(StringUtil.cutString(bcb.getBalanceCycleEnd(), 10)).append("' ");
						balanceService.updateMailingBalance(qbuf.toString(), "order_id=" + order.getId());
						
						//添加日志
						OrderImportLogBean log = new OrderImportLogBean();
						log.setContent(order.getCode()+"\t"+packageCode);
						log.setCreateDatetime(DateUtil.getNow());
						log.setUserId(user.getId());
						log.setType(0);
						logService.addOrderImportLog(log);
							
						stockService.updateAuditPackage("package_code='"+packageCode+"',status=4", "order_id="+order.getId());
						checkStatus=5;
						//-----结束导入包裹单
					}
				}
				request.setAttribute("checkStatus", checkStatus+"");
				OrderCustomerBean ocBean=batchBarcodeService.getOrderCustomerBean("order_code='"+order.getCode()+"'");
				order.setSerialNumber(ocBean.getSerialNumber());
				request.setAttribute("order", order);
				AuditPackageBean apBean2=stockService.getAuditPackage("order_id="+order.getId());//核对包裹记录
				request.setAttribute("apBean", apBean2);
				OrderStockBean osBean=stockService.getOrderStock("order_id="+order.getId());
				if(osBean!=null){
					OrderStockProductBean ospBean=stockService.getOrderStockProduct("order_stock_id="+osBean.getId());
					if(ospBean!=null){
						request.setAttribute("ospBean", ospBean);
					}
				}
			}
			stockService.getDbOp().commitTransaction();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			stockService.getDbOp().rollbackTransaction();
		}finally{
			stockService.releaseAll();
		}
		}
		return mapping.findForward("auditPackage");
	}
	
	private void sendSMS(voUser user, voOrder order){
		String content = "您的买卖宝订单%orderCode%于%datetime%从%area%发货,3-5天送达.包裹单号%packageNum%。";
		if(order.getPackageNum().length() == 10 && StringUtil.isNumeric(order.getPackageNum())){
			content = "您的产品已发出,包裹单%packageNum%,3-5天左右送达.恶劣天气可能会延误,拨4006789000可查详情";
		} else if(order.getPackageNum().endsWith("CN")){
			content = "您的产品已发出,包裹单%packageNum%,3-5天左右送达.恶劣天气可能会延误,拨11185可查详情";
		} else if(order.getPackageNum().endsWith("GD")) {
			content = "您的产品已发出,包裹单%packageNum%,1-2天左右送达.恶劣天气可能会延误,拨11185可查详情";
		} else if(order.getPackageNum().startsWith("6") || order.getPackageNum().startsWith("16")){
			content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。拨4006789000查包裹详情";
		} else if(order.getPackageNum().startsWith("EC")){
			content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。拨11185可查包裹详情";
		} else {
			content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。";
		}
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
		content = content.replace("%productName%", order.getProducts());
		content = content.replace("%price%", String.valueOf(order.getProductPrice()));
		content = content.replace("%packageNum%", order.getPackageNum());
		content = content.replace("%totalPrice%", String.valueOf(order.getDprice()));
		SenderSMS3.send(user.getId(), order.getPhone(), content);
		
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, null);
		try{
			
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
	
	/**
	 * 
	 * 查看发货量及其导出
	 */
	public ActionForward auditPackageList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB); //adult连接池
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp); 
		String action =StringUtil.convertNull(request.getParameter("action"));//空为显示列表，export为导出
		try{
			String date=StringUtil.convertNull(request.getParameter("date"));//日期
			String time1=StringUtil.convertNull(request.getParameter("time1"));//起始时间
			String time2=StringUtil.convertNull(request.getParameter("time2"));//结束时间
			String areano=StringUtil.convertNull(request.getParameter("areano"));//发货地点
			String[] deliver=request.getParameterValues("deliver");//快递公司
			String searchType=StringUtil.convertNull(request.getParameter("searchType"));//查询方式
			
			if(time1.equals("")){
				time1="00";
			}
			if(time2.equals("")){
				time2="00";
			}
			if(Integer.parseInt(time1)>Integer.parseInt(time2)){
				request.setAttribute("checkStatus", "11");
				return mapping.findForward("auditPackage");
			}
			int countPerPage=50;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
            String para="";
            paging.setCurrentPageIndex(pageIndex);
           
			List auditPackageList=new ArrayList();//核对包裹bean列表
			List auditPackageList2=new ArrayList();//未分页的核对包裹列表，用于导出
			String sql="";
			if(date.equals("")){
				sql+="sorting_datetime>'";
				sql+=DateUtil.getNowDateStr();
				sql+=" ";
				sql+=time1;
				sql+=":00:00'";
				sql+=" and sorting_datetime<'";
				sql+=DateUtil.getNowDateStr();
				sql+=" ";
				sql+=time1.equals("00")&&time2.equals("00")?"23":time2;
				sql+=":00:00'";
			}else{
				sql+="sorting_datetime>'";
				sql+=date;
				sql+=" ";
				sql+=time1;
				sql+=":00:00'";
				sql+=" and sorting_datetime<'";
				sql+=date;
				sql+=" ";
				sql+=time1.equals("00")&&time2.equals("00")?"23":time2;
				sql+=":00:00'";
				para+="&date=";
				para+=date;
			}
			para+="&time1=";
			para+=time1;
			para+="&time2=";
			para+=time2;
			if(!areano.equals("")){
				sql+=" and areano=";
				sql+=areano;
				para+="&areano=";
				para+=areano;
			}
			if(deliver!=null&&deliver.length>0){
				sql+=" and deliver in(";
				for(int i=0;i<deliver.length;i++){
					sql+=deliver[i];
					if(i!=deliver.length-1){
						sql+=",";
					}
					para+="&deliver=";
					para+=deliver[i];
				}
				sql+=")";
			}
			if(searchType.equals("")){
				searchType="3";
			}
			if(searchType.equals("1")){//未核对的包裹
				sql+=" and status in (1,2)";
			}else if(searchType.equals("2")){//未导入包裹单号
				sql+=" and status in (1,2,3)";
			}else if(searchType.equals("3")){//全部订单
				sql+=" and status in (1,2,3,4)";
			}
			para+="&searchType=";
			para+=searchType;
			paging.setPrefixUrl("auditPackage.do?method=auditPackageList"+para);
			
			auditPackageList=stockService.getAuditPackageList(sql, paging.getCurrentPageIndex() * countPerPage, countPerPage, "sorting_datetime");
			auditPackageList2=stockService.getAuditPackageList(sql, -1, -1, "sorting_datetime");
			if(action.equals("export")){//导出
				request.setAttribute("auditPackageList", auditPackageList2);
			}else{//显示列表
				request.setAttribute("auditPackageList", auditPackageList);
			}
			int count1=stockService.getAuditPackageCount(sql+" and status in (1,2,3,4)");//导单分拣
			int count2=stockService.getAuditPackageCount(sql+" and status in (2,3,4)");//复核出库
			int count3=stockService.getAuditPackageCount(sql+" and status in (3,4)");//核对包裹
			int count4=stockService.getAuditPackageCount(sql+" and status in (4)");//导入包裹单
			request.setAttribute("count1", count1+"");
			request.setAttribute("count2", count2+"");
			request.setAttribute("count3", count3+"");
			request.setAttribute("count4", count4+"");
			paging.setTotalCount(count1);
			paging.setCurrentPageIndex(pageIndex);
			paging.setTotalPageCount(count1%paging.getCountPerPage()==0?count1/paging.getCountPerPage():count1/paging.getCountPerPage()+1);
			request.setAttribute("paging", paging);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			stockService.releaseAll();
		}
		if(action.equals("export")){
			return mapping.findForward("exportPackage");
		}
		return mapping.findForward("auditPackage");
	}
}
