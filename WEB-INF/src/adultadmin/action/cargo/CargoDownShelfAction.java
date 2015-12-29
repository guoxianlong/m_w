package adultadmin.action.cargo;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.finance.stat.FinanceStockCardBean;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoStorageBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;


public class CargoDownShelfAction extends DispatchAction{
	
	public static byte[] cargoLock = new byte[0];	
	public Log stockLog = LogFactory.getLog("stock.Log");
	
	/**
	 * 功能:添加下架单
	 * <p>作者 李双 Apr 19, 2011 9:31:22 AM
	 */
	public ActionForward addDownShel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		 
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int cargoOperationId=0;
		String[] cargoProducIds = request.getParameterValues("cargoProducId");
		String area=StringUtil.convertNull(request.getParameter("area"));
		if(area.equals("")){
			area="-1";
		}
		
		StringBuilder sbCargoProducIds = new StringBuilder("id in (");
		if(cargoProducIds==null|| cargoProducIds.length<=0){
			request.setAttribute("tip", "传值错误");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		for(int i=0;i<cargoProducIds.length;i++){
			sbCargoProducIds.append(cargoProducIds[i]);
			if(i!=cargoProducIds.length-1){
				sbCargoProducIds.append(",");
			} 
		}
		sbCargoProducIds.append(") ");
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			service.getDbOp().startTransaction();
			try{
				 
				ArrayList cargoProductList = service.getCargoProductStockList(sbCargoProducIds.toString(),-1,-1,"id desc");
				CargoProductStockBean cargoPBean=(CargoProductStockBean)cargoProductList.get(0); //根据一个id 得到所属仓库
				int cargoInfoId = cargoPBean.getCargoId(); //从cargo_product_stock表中的货位id 再得到仓库id 再再仓库表中得到完整编号
				CargoInfoBean cargoInfoBean=service.getCargoInfo("id="+cargoInfoId);//得到仓库id
				CargoInfoStorageBean cargoInfoSBean=service.getCargoInfoStorage("id="+cargoInfoBean.getStorageId());
				CargoOperationBean cargoOperationBean = new CargoOperationBean();
				cargoOperationBean.setStatus(CargoOperationProcessBean.OPERATION_STATUS10);
				cargoOperationBean.setStockOutType(0);//作业单来源存放类型 0 散货区 1 整件区
				cargoOperationBean.setCreateDatetime(DateUtil.getNow());
				cargoOperationBean.setCreateUserId(user.getId());
				cargoOperationBean.setCreateUserName(user.getUsername());
	  			cargoOperationBean.setStockInType(1); //作业单目的存放类型 0 散货区 1 整件区
				cargoOperationBean.setType(1);//作业类型  1下架单
				cargoOperationBean.setStorageCode(cargoInfoSBean.getWholeCode());// 作业单操作的仓库 完整编号
				String code = service.getCargoOperationMaxIdCode("HWX");//作业表编号 重新生成的
				cargoOperationBean.setCode(code);
				cargoOperationBean.setLastOperateDatetime(DateUtil.getNow());
				cargoOperationBean.setStockOutArea(Integer.parseInt(area));
				cargoOperationBean.setStockInArea(Integer.parseInt(area));
				
				boolean flag = service.addCargoOperation(cargoOperationBean);
				
				if(flag){
					cargoOperationId= service.getDbOp().getLastInsertId();
					
					for(int i=0;i<cargoProductList.size();i++){
						CargoProductStockBean cargoProductStockBean = (CargoProductStockBean)cargoProductList.get(i);
						int productId = cargoProductStockBean.getProductId();
						voProduct product=wareService.getProduct(productId);
						if(cargoProductStockBean.getStockCount()==0){
							request.setAttribute("tip", "存在散货区无货产品，请重新添加！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoInfoBean cargoInfo = service.getCargoInfo("id="+cargoProductStockBean.getCargoId());//从数据库中得到 货位编号
						
						//判断该货位是否有相关调拨单
						String sql="select * from cargo_operation co left join cargo_operation_cargo coc on " +
								"co.id=coc.oper_id where co.status in(28,29,30,31,32,33) and co.type=3 and coc.out_cargo_whole_code='"+
								cargoInfo.getWholeCode()+"';";
						service.getDbOp().prepareStatement(sql);
						PreparedStatement ps = wareService.getDbOp().getPStmt();
						ResultSet rs = ps.executeQuery();
						if(rs.next()){
							request.setAttribute("tip", "货位"+cargoInfo.getWholeCode()+"已生成调拨单，不能再添加！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						
						CargoOperationLogBean logBean=new CargoOperationLogBean();
						logBean.setOperId(cargoOperationId);
						logBean.setOperDatetime(DateUtil.getNow());
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						StringBuilder logRemark=new StringBuilder("制单：商品");
						logRemark.append(product.getCode());
						logRemark.append("，");
						logRemark.append("源货位（");
						logRemark.append(cargoInfo.getWholeCode());
						logRemark.append("）");
						
						CargoOperationCargoBean cargoOperationCargoBean = new CargoOperationCargoBean();
						cargoOperationCargoBean.setOperId(cargoOperationId);
						cargoOperationCargoBean.setProductId(cargoProductStockBean.getProductId());
						cargoOperationCargoBean.setStockCount(cargoProductStockBean.getStockCount()-cargoProductStockBean.getStockLockCount());
						cargoOperationCargoBean.setOutCargoProductStockId(cargoProductStockBean.getId()); //添加源货位id cargoProductStock 表中
						
						cargoOperationCargoBean.setOutCargoWholeCode(cargoInfo.getWholeCode());
						cargoOperationCargoBean.setType(0);
						
//						cargoOperationCargoBean.setInCargoProductStockId(incargoPcs); //通过源货位关联起目的货位
//						cargoOperationCargoBean.setInCargoWholeCode(inCargoInfoStr);
//						
						flag=service.addCargoOperationCargo(cargoOperationCargoBean);
						
						//查看是否有该产品的整件区货位
						List cpsList = service.getCargoAndProductStockList("ci.stock_type = 0 and ci.store_type = 1 and cps.product_id = "+productId+" and ci.area_id="+cargoInfoBean.getAreaId(), -1, -1, "ci.whole_code asc");
						//int incargoPcs = 0 ;String inCargoInfoStr ="";
						if(cpsList!=null && cpsList.size()>0){ 
							Iterator itr = cpsList.iterator();//目的货位
							while(itr.hasNext()){
								CargoProductStockBean inCps = (CargoProductStockBean)itr.next();
								CargoInfoBean inCarogInfo = service.getCargoInfo(" id="+inCps.getCargoId());
								
								CargoOperationCargoBean inCoc = new CargoOperationCargoBean();
								inCoc.setOperId(cargoOperationId);
								inCoc.setProductId(productId);
								inCoc.setStockCount(0);
								inCoc.setOutCargoProductStockId(cargoProductStockBean.getId());
								inCoc.setOutCargoWholeCode(cargoInfo.getWholeCode());
								inCoc.setInCargoProductStockId(inCps.getId());
								inCoc.setInCargoWholeCode(inCarogInfo.getWholeCode());
								inCoc.setUseStatus(1);
								inCoc.setType(1);
								logRemark.append("，目的货位（");
								logRemark.append(inCarogInfo.getWholeCode());
								logRemark.append("）");
								flag=service.addCargoOperationCargo(inCoc);
//								inCargoInfoStr = inCarogInfo.getWholeCode();
//								incargoPcs = inCps.getId();
							}
						}
						logBean.setRemark(logRemark.toString());
						service.addCargoOperationLog(logBean);
					}
				}
// 
//				boolean flag=service.addCargoOperationDownShelf(cargoProductList,cargoOperationBean);
//				
				CargoOperLogBean operLog=new CargoOperLogBean();//员工操作日志
				operLog.setOperId(service.getCargoOperation("code='"+cargoOperationBean.getCode()+"'").getId());
				operLog.setOperCode(cargoOperationBean.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS10);
				operLog.setOperName(process.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(CargoOperLogBean.EFFECT_TIME0);
				operLog.setRemark("");
				operLog.setPreStatusName("无");
				operLog.setNextStatusName(process.getStatusName());
				if(!service.addCargoOperLog(operLog)){
					request.setAttribute("tip", "添加日志数据时发生异常");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				if(!flag){
					request.setAttribute("tip", "添加失败");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}else{
					request.setAttribute("message", "添加成功");
					request.setAttribute("id", Integer.valueOf(cargoOperationId));
				}
				
				service.getDbOp().commitTransaction();
			}catch(Exception e){
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}finally{
				service.releaseAll();
			} 
		}
		return mapping.findForward("success");
	}
	
	
	/**
	 * 功能:下架单详细页
	 * <p>作者 李双 Apr 19, 2011 9:31:22 AM
	 */
	public ActionForward showDownShel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 0", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean inCoc = (CargoOperationCargoBean)iter.next();
				CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
//				if(inCps==null){
//					request.setAttribute("tip", "关键数据损坏或被删除！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
//				}
				//CargoInfoBean ci = service.getCargoInfo("id = "+inCps.getCargoId());
				CargoInfoBean ci = service.getCargoInfo("whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
				
				voProduct product = wareService.getProduct(inCoc.getProductId());
			//	inCps.setCargoInfo(ci);
				//inCps.setProduct(product);
				inCoc.setCargoInfo(ci);
				inCoc.setProduct(product);
				inCoc.setCargoProductStock(inCps);

				List list = service.getCargoOperationCargoList(
						"out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and oper_id = "+inCoc.getOperId()+" and product_id = "+inCoc.getProductId()+" and type = 1", -1, -1, "in_cargo_whole_code asc");
				inCoc.setCocList(list);

				Iterator iter2 = list.listIterator();
				while(iter2.hasNext()){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter2.next();
					CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
					//CargoInfoBean outCi = service.getCargoInfo("id = "+outCoc.getCargoId());
					CargoInfoBean outCi = service.getCargoInfo("whole_code = '"+outCoc.getInCargoWholeCode()+"'");
					product = wareService.getProduct(outCoc.getProductId());
//					outCps.setCargoInfo(outCi);
//					outCps.setProduct(product);
					outCoc.setCargoInfo(outCi);
					outCoc.setProduct(product);
					outCoc.setCargoProductStock(outCps);
				}
			}
			
			CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());
			if(process!=null){
				cargoOperation.setStatusName(process.getStatusName());
			}
			CargoOperationProcessBean nextStatus=null;//下一个状态，传参用
			CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+(cargoOperation.getStatus()+1));//下一个状态
			CargoOperationProcessBean complete=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS16);//完成作业单状态
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS11
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS12
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS13
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS14){
				if(process2.getUseStatus()==1){
					nextStatus=process2;
				}else{
					nextStatus=complete;
				}
			}
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS15){
				nextStatus=complete;
			}
			request.setAttribute("nextStatus", nextStatus);
			String effectTime="";
			Calendar cal1=Calendar.getInstance();
			if(cargoOperation.getLastOperateDatetime()==null){
				cargoOperation.setLastOperateDatetime(cargoOperation.getCreateDatetime());
			}
			cal1.setTime(DateUtil.parseDate(cargoOperation.getLastOperateDatetime(), "yyyy-MM-dd HH:mm:ss"));
			Calendar cal2=Calendar.getInstance();
			cal1.add(Calendar.MINUTE, process.getEffectTime());
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS16){
				effectTime="待复核";
			}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS17){
				effectTime="作业成功";
			}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS18){
				effectTime="作业失败";
			}else{
				if(cal1.before(cal2)){
					effectTime="已超时";
				}else{
					effectTime="进行中";
				}
			}
			
			request.setAttribute("effectTime", effectTime);
			request.setAttribute("cocList", cocList);
			request.setAttribute("cargoOperation", cargoOperation);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("showEditDownShelf");
	}
			
	/**
	 * 编辑下架单 
	 */
	public ActionForward editDownShelf(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("id"));
		String action = StringUtil.convertNull(request.getParameter("action"));
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该下架单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10){
					request.setAttribute("tip", "该下架单已确认，无法编辑！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
	
				service.getDbOp().startTransaction();
				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");//源货位信息列表
				String[] checkedCocIds =null;
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i); //源货位信息
					voProduct product=wareService.getProduct(inCoc.getProductId());//商品
					CargoInfoBean outCiBean=service.getCargoInfo("whole_code='"+inCoc.getOutCargoWholeCode()+"'");//源货位
					CargoOperationLogBean logBean=new CargoOperationLogBean();
					logBean.setOperId(operId);
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark=new StringBuilder("保存编辑：商品");
					logRemark.append(product.getCode());
					logRemark.append("，");
					logRemark.append("源货位（");
					logRemark.append(outCiBean.getWholeCode());
					logRemark.append("）");
					//目的货位信息列表
					List outCocIds = service.getFieldList("id", "cargo_operation_cargo", 
							"oper_id = "+operId+" and type = 1 and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId(), -1, -1, null, null, "String");
					 
					if(request.getParameterValues("upOpertionId"+inCoc.getId())!=null){
						checkedCocIds =request.getParameterValues("upOpertionId"+inCoc.getId());
					}
					
					if(checkedCocIds == null||checkedCocIds.length == 0){
						request.setAttribute("tip", "产品必须至少选择一个货位");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					for(int j=0;j<checkedCocIds.length;j++){
						int refillCount = StringUtil.StringToId(request.getParameter("operationNum"+checkedCocIds[j]));
						if(refillCount<0){
							request.setAttribute("tip", "本次作业量，必须输入大于等于0的整数");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(action.equals("confirm") &&refillCount<=0){
							request.setAttribute("tip", "本次作业量必须大于0");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						String checkedCocId=checkedCocIds[j];
						CargoOperationCargoBean inCocBean=service.getCargoOperationCargo("id="+checkedCocId);
						if(inCocBean.getOutCargoWholeCode().equals(outCiBean.getWholeCode())){
							logRemark.append("，目的货位（");
							logRemark.append(inCocBean.getInCargoWholeCode());
							logRemark.append("），");
							logRemark.append("上架量（");
							logRemark.append(inCocBean.getStockCount());
							logRemark.append("-");
							logRemark.append(refillCount);
							logRemark.append("）");
						}
						service.updateCargoOperationCargo("stock_count = "+refillCount+",use_status = 1", "id = "+checkedCocIds[j]);
						outCocIds.remove(String.valueOf(checkedCocIds[j]));
					}
					logBean.setRemark(logRemark.toString());
					service.addCargoOperationLog(logBean);
					for(int j=0;j<outCocIds.size();j++){
						service.updateCargoOperationCargo("use_status = 0", "id = "+(String)outCocIds.get(j));
					}
//					if(action.equals("confirm")){
//						for(int j=0;j<outCocIds.size();j++){
//							service.deleteCargoOperationCargo("use_status = 0 and type=1 and oper_id="+operId);
//						}
//					}
				}
//				if(cargoOperation.getStatus() == CargoOperationBean.STATUS0){
//					service.updateCargoOperation("status = "+CargoOperationBean.STATUS1, "id = "+cargoOperation.getId());
//				}
				service.getDbOp().commitTransaction();
	
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		
		if(action.equals("confirm")){
			return confirmDownCargo(mapping, form, request, response);
		}else{
			request.setAttribute("tip", "操作成功");
			request.setAttribute("url", "cargoDownShelf.do?method=showDownShel&id="+operId);
			return mapping.findForward("tip");
		}
		}
	}
	
	/**
	 * 
	 * 功能: 确定  下架单 
	 * <p>作者 李双 Apr 15, 2011 5:13:08 PM
	 */
	public ActionForward confirmDownCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("id"));
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该下架单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10){
					request.setAttribute("tip", "该下架单已确认，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
	
				service.getDbOp().startTransaction();
				
				service.updateCargoOperation("status = "+CargoOperationProcessBean.OPERATION_STATUS11+",effect_status = "+CargoOperationBean.EFFECT_STATUS0+",confirm_datetime='"+DateUtil.getNow()+"',confirm_user_name='"+user.getUsername()+"',last_operate_datetime='"+DateUtil.getNow()+"'", "id = "+operId);

				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc"); //in 是源 out 是目标 
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
					CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
					CargoInfoBean inCi = service.getCargoInfo("id = "+inCps.getCargoId());
					CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
					logBean.setOperId(cargoOperation.getId());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setRemark("确认提交");
					service.addCargoOperationLog(logBean);
					List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<outCocList.size();j++){
						CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
						CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
						CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
						if(temp_inCps==null){
							voProduct product = wareService.getProduct(outCoc.getProductId());
							request.setAttribute("tip", "商品"+product.getCode()+"货位"+outCoc.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败!");
	                    	request.setAttribute("result", "failure");
	                    	service.getDbOp().rollbackTransaction();
	        				return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
	 					if(!service.updateCargoProductStockCount(outCps.getId(), -outCoc.getStockCount())){
	                    	request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
	                    	request.setAttribute("result", "failure");
	                    	service.getDbOp().rollbackTransaction();
	        				return mapping.findForward(IConstants.FAILURE_KEY);
	                    }
						if(!service.updateCargoProductStockLockCount(outCps.getId(), outCoc.getStockCount())){
	                    	request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
	                    	request.setAttribute("result", "failure");
	                    	service.getDbOp().rollbackTransaction();
	        				return mapping.findForward(IConstants.FAILURE_KEY);
	                    }
						
						//调整合格库库存
						if(outCi.getAreaId()!=inCi.getAreaId()){
							CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
							ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+inCoc.getProductId());
							if(outProductStock==null){
								request.setAttribute("tip", "合格库库存数据错误！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!psService.updateProductStockCount(outProductStock.getId(),-outCoc.getStockCount())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!psService.updateProductLockCount(outProductStock.getId(),outCoc.getStockCount())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}
					//删除coc无用数据
					service.deleteCargoOperationCargo("oper_id = "+operId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 1 and use_status = 0");
				}
				
				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					CargoOperationProcessBean tempProcess=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//生成作业单
					int effectTime=tempProcess.getEffectTime();//生成阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}
				
				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS10);
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS11);
				operLog.setOperName(process2.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(0);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);
				
				service.getDbOp().commitTransaction();
	
			}catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", "操作成功");
		request.setAttribute("url", "cargoDownShelf.do?method=showDownShel&id="+operId);
		return mapping.findForward("tip");
	}
	
	

	/**
	 *	下架单交接阶段
	 */
	public ActionForward auditingDownCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("id"));
		int nextStatus=StringUtil.toInt(request.getParameter("nextStatus"));//下一个状态
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>=nextStatus){
					request.setAttribute("tip", "该作业单未确认，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
	
				service.getDbOp().startTransaction();
				
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+nextStatus);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
                	request.setAttribute("result", "failure");
    				return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
                	request.setAttribute("result", "failure");
    				return mapping.findForward(IConstants.FAILURE_KEY);
				}
				int handleType=process2.getHandleType();//操作方式，0人工确认，1设备确认
				if(handleType!=0){
					request.setAttribute("tip", "当前操作方式为设备确认！");
                	request.setAttribute("result", "failure");
    				return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}
				
				service.updateCargoOperation("status="+nextStatus+",effect_status = 0,last_operate_datetime='"+DateUtil.getNow()+"'"+(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS11?(",auditing_datetime='"+DateUtil.getNow()+"',auditing_user_id="+user.getId()+",auditing_user_name='"+user.getUsername()+"'"):""), "id="+operId);
				
				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName(process2.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(0);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);
				
				service.getDbOp().commitTransaction();
	
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", "操作成功");
		request.setAttribute("url", "cargoDownShelf.do?method=showDownShel&id="+operId);
		return mapping.findForward("tip");
	}
	
	/**
	 *	下架 单作业完成
	 */
	public ActionForward completeDownCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("id"));
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS16){
					request.setAttribute("tip", "该作业单未审核通过，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
	
				service.getDbOp().startTransaction();
				List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>(); //存放财务接口数据
				
					//完成货位库存量操作
					List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
					for(int i=0;i<inCocList.size();i++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
						CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
						CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
						voProduct product = wareService.getProduct(inCoc.getProductId());
						product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
						int stockOutCount = 0;
						int productStockCount=0;
						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
						for(int j=0;j<outCocList.size();j++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
							CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
							CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
							
							if(!service.updateCargoProductStockCount(temp_inCps.getId(), outCoc.getStockCount())){
		                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
		                    	request.setAttribute("result", "failure");
		                    	service.getDbOp().rollbackTransaction();
		        				return mapping.findForward(IConstants.FAILURE_KEY);
		                    }
							if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
		                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
		                    	request.setAttribute("result", "failure");
		                    	service.getDbOp().rollbackTransaction();
		        				return mapping.findForward(IConstants.FAILURE_KEY);
		                    }
							
							//调整合格库库存
							if(outCi.getAreaId()!=inCi.getAreaId()){
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
								ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+inCoc.getProductId());
								if(outProductStock==null){
									request.setAttribute("tip", "合格库库存数据错误！");
									request.setAttribute("result", "failure");
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if (!psService.updateProductStockCount(outProductStock.getId(),outCoc.getStockCount())) {
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								productStockCount+=outCoc.getStockCount();
								
								ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
								ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
								
								//组装财务接口需要的数据
								BaseProductInfo baseProductInfo = new BaseProductInfo();
								baseProductInfo.setId(inCoc.getProductId());
								baseProductInfo.setProductStockOutId(psOut.getId());
								baseProductInfo.setProductStockId(psIn.getId());
								baseProductInfo.setOutCount(inCoc.getStockCount());
								baseList.add(baseProductInfo);
								
								//批次修改开始
								/**
								//更新批次记录、添加调拨出、入库批次记录
								List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId(), -1, -1, "id asc");
								double stockinPrice = 0;
								double stockoutPrice = 0;
								if(sbList!=null&&sbList.size()!=0){
									int stockExchangeCount = inCoc.getStockCount();
									int index = 0;
									int stockBatchCount = 0;
									
									do {
										//出库
										StockBatchBean batch = (StockBatchBean)sbList.get(index);
										if(stockExchangeCount>=batch.getBatchCount()){
											if(!stockService.deleteStockBatch("id="+batch.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
											stockBatchCount = batch.getBatchCount();
										}else{
											if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
											stockBatchCount = stockExchangeCount;
										}
										
										//添加批次操作记录
										StockBatchLogBean batchLog = new StockBatchLogBean();
										batchLog.setCode(cargoOperation.getCode());
										batchLog.setStockType(batch.getStockType());
										batchLog.setStockArea(batch.getStockArea());
										batchLog.setBatchCode(batch.getCode());
										batchLog.setBatchCount(stockBatchCount);
										batchLog.setBatchPrice(batch.getPrice());
										batchLog.setProductId(batch.getProductId());
										batchLog.setRemark("调拨出库");
										batchLog.setCreateDatetime(DateUtil.getNow());
										batchLog.setUserId(user.getId());
										batchLog.setSupplierId(batch.getSupplierId());
										batchLog.setTax(batch.getTax());
										if(!stockService.addStockBatchLog(batchLog)){
											 request.setAttribute("tip", "添加失败！");
								             request.setAttribute("result", "failure");
								             service.getDbOp().rollbackTransaction();
								             return mapping.findForward(IConstants.FAILURE_KEY);
										}
										
										stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
										
										//入库
										StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
										if(batchBean!=null){
											if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
										}else{
											StockBatchBean newBatch = new StockBatchBean();
											newBatch.setCode(batch.getCode());
											newBatch.setProductId(batch.getProductId());
											newBatch.setPrice(batch.getPrice());
											newBatch.setBatchCount(stockBatchCount);
											newBatch.setProductStockId(psIn.getId());
											newBatch.setStockArea(inCi.getAreaId());
											newBatch.setStockType(psIn.getType());
											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
											newBatch.setSupplierId(batch.getSupplierId());
											newBatch.setTax(batch.getTax());
											newBatch.setNotaxPrice(batch.getNotaxPrice());
											if(!stockService.addStockBatch(newBatch)){
												request.setAttribute("tip", "添加失败！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return mapping.findForward(IConstants.FAILURE_KEY);
											}
										}
										
										//添加批次操作记录
										batchLog = new StockBatchLogBean();
										batchLog.setCode(cargoOperation.getCode());
										batchLog.setStockType(psIn.getType());
										batchLog.setStockArea(inCi.getAreaId());
										batchLog.setBatchCode(batch.getCode());
										batchLog.setBatchCount(stockBatchCount);
										batchLog.setBatchPrice(batch.getPrice());
										batchLog.setProductId(batch.getProductId());
										batchLog.setRemark("调拨入库");
										batchLog.setCreateDatetime(DateUtil.getNow());
										batchLog.setUserId(user.getId());
										batchLog.setSupplierId(batch.getSupplierId());
										batchLog.setTax(batch.getTax());
										if(!stockService.addStockBatchLog(batchLog)){
											request.setAttribute("tip", "添加失败！");
											request.setAttribute("result", "failure");
											service.getDbOp().rollbackTransaction();
											return mapping.findForward(IConstants.FAILURE_KEY);
										}
										
										stockExchangeCount -= batch.getBatchCount();
										index++;
										
										stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
									} while (stockExchangeCount>0&&index<sbList.size());
								}
								*/
								//批次修改结束
								
								//添加进销存卡片开始
								// 入库卡片
								StockCardBean sc = new StockCardBean();
								sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
								sc.setCode(cargoOperation.getCode());

								sc.setCreateDatetime(DateUtil.getNow());
								sc.setStockType(inCi.getStockType());
								sc.setStockArea(inCi.getAreaId());
								sc.setProductId(inCps.getProductId());
								sc.setStockId(psIn.getId());
								sc.setStockInCount(inCoc.getStockCount());
								sc.setStockInPriceSum(0);

								sc.setCurrentStock(product.getStock(inCi.getAreaId(), sc.getStockType())
										+ product.getLockCount(inCi.getStockAreaId(), sc.getStockType()));
								sc.setStockAllArea(product.getStock(inCi.getAreaId())
										+ product.getLockCount(inCi.getAreaId()));
								sc.setStockAllType(product.getStockAllType(sc.getStockType())
										+ product.getLockCountAllType(sc.getStockType()));
								sc.setAllStock(product.getStockAll() + product.getLockCountAll());
								sc.setStockPrice(product.getPrice5());// 新的库存价格
								sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
										new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
								psService.addStockCard(sc);
								
								// 出库卡片
								StockCardBean sc2 = new StockCardBean();
								int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
								sc2.setId(scId);

								sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
								sc2.setCode(cargoOperation.getCode());

								sc2.setCreateDatetime(DateUtil.getNow());
								sc2.setStockType(outCi.getStockType());
								sc2.setStockArea(outCi.getAreaId());
								sc2.setProductId(product.getId());
								sc2.setStockId(psOut.getId());
								sc2.setStockOutCount(inCoc.getStockCount());
//								sc2.setStockOutPriceSum(stockOutPrice);
								sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								sc2.setCurrentStock(product.getStock(outCi.getAreaId(), sc2.getStockType())
										+ product.getLockCount(outCi.getAreaId(), sc2.getStockType()));
								sc2.setStockAllArea(product.getStock(outCi.getAreaId())
										+ product.getLockCount(outCi.getAreaId()));
								sc2.setStockAllType(product.getStockAllType(sc2.getStockType())
										+ product.getLockCountAllType(sc2.getStockType()));
								sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
								sc2.setStockPrice(product.getPrice5());
								sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
										new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
								psService.addStockCard(sc2);
								//添加进销存卡片结束
							}
							
							//货位入库卡片
							temp_inCps = service.getCargoProductStock("id = "+temp_inCps.getId());
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_DOWNSHELFSTOCKIN);
							csc.setCode(cargoOperation.getCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(inCi.getStockType());
							csc.setStockArea(inCi.getAreaId());
							csc.setProductId(product.getId());
							csc.setStockId(temp_inCps.getId());
							csc.setStockInCount(outCoc.getStockCount());
							csc.setStockInPriceSum((new BigDecimal(outCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(temp_inCps.getStockCount()+temp_inCps.getStockLockCount());
							csc.setCargoStoreType(outCi.getStoreType());
							csc.setCargoWholeCode(outCi.getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							service.addCargoStockCard(csc);
							
							stockOutCount = stockOutCount + outCoc.getStockCount();
						}
						
						//调整合格库库存
						CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
						ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+inCoc.getProductId());
						if(outProductStock==null){
							request.setAttribute("tip", "合格库库存数据错误！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if (!psService.updateProductLockCount(outProductStock.getId(),-productStockCount)) {
							request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						

						//货位出库卡片
						inCps = service.getCargoProductStock("id = "+inCps.getId());
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_DOWNSHELFSTOCKOUT);
						csc.setCode(cargoOperation.getCode());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(inCi.getStockType());
						csc.setStockArea(inCi.getAreaId());
						csc.setProductId(product.getId());
						csc.setStockId(inCps.getId());
						csc.setStockOutCount(stockOutCount);
						csc.setStockOutPriceSum((new BigDecimal(stockOutCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(inCps.getStockCount()+inCps.getStockLockCount());
						csc.setCargoStoreType(inCi.getStoreType());
						csc.setCargoWholeCode(inCi.getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
						service.addCargoStockCard(csc);
					}
					
				//调用财务接口
				if(!baseList.isEmpty()){
					FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(baseList, cargoOperation.getCode(), user.getId(), 0, 0);
				}
					
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS16);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
		            request.setAttribute("result", "failure");
		    		return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
		            request.setAttribute("result", "failure");
		    		return mapping.findForward(IConstants.FAILURE_KEY);
				}
					
				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}
					
				service.updateCargoOperation("status="+CargoOperationProcessBean.OPERATION_STATUS16+",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+",complete_datetime='"+DateUtil.getNow()+"',complete_user_id="+user.getId()+",complete_user_name='"+user.getUsername()+"'", "id="+operId);
				
				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName(process.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(2);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);
					
				service.getDbOp().commitTransaction();
	
			}catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", "操作成功");
		request.setAttribute("url", "cargoDownShelf.do?method=showDownShel&id="+operId);
		return mapping.findForward("tip");
	}		
	
	/**
	 *	复核下架单
	 */
	public ActionForward checkDownCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("id"));
		int status = StringUtil.toInt(request.getParameter("status"));
		String remark = StringUtil.convertNull(request.getParameter("remark"));
		String msg="操作成功";
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>CargoOperationProcessBean.OPERATION_STATUS16){
					request.setAttribute("tip", "该作业单状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS10){
					request.setAttribute("tip", "该作业单还未提交，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().startTransaction();
				if(status==3){//作业成功
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS16){
						request.setAttribute("tip", "该作业单未结束，操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					service.updateCargoOperation("effect_status="+CargoOperationBean.EFFECT_STATUS3+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId);
				}else if(status==4){//作业失败
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS16){
						//还原货位库存
						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
						for(int i=0;i<outCocList.size();i++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
							CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());
							
							List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
							for(int j=0;j<inCocList.size();j++){
								CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
								CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
								//CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
								CargoInfoBean inCi = service.getCargoInfo("id = "+inCps.getCargoId());
								
								if(!service.updateCargoProductStockCount(outCps.getId(), inCoc.getStockCount())){
			                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
			                    	request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				return mapping.findForward(IConstants.FAILURE_KEY);
			                    }
								if(!service.updateCargoProductStockLockCount(outCps.getId(), -inCoc.getStockCount())){
			                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
			                    	request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				return mapping.findForward(IConstants.FAILURE_KEY);
			                    }
								
								//调整合格库库存
								if(outCi.getAreaId()!=inCi.getAreaId()){
									CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
									ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+inCoc.getProductId());
									if(outProductStock==null){
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(outProductStock.getId(),inCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductLockCount(outProductStock.getId(),-inCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
							}
						}
					}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS16){
						
					}
					service.updateCargoOperation("effect_status="+CargoOperationBean.EFFECT_STATUS4+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId);
				}
				
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS16);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
	               	request.setAttribute("result", "failure");
	    			return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
	               	request.setAttribute("result", "failure");
	    			return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}
				
				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName("作业复核");
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(status);
				operLog.setRemark(remark);
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);
				
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
				msg="操作失败";
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", msg);
		request.setAttribute("url", "cargoDownShelf.do?method=showDownShel&id="+operId);
		return mapping.findForward("tip");
	}
	
	/**
	 * 下架单列表
	 */
	public ActionForward shelfDownList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int countPerPage=20;
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ArrayList operationList =  new ArrayList();
		try{
			String operationCode = StringUtil.convertNull(request.getParameter("operationCode"));
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));   
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode"));
			String[] statuses = request.getParameterValues("status");
			
			StringBuilder paramBuf = new StringBuilder();
			StringBuilder operationQuery = new StringBuilder("select oo.* from cargo_operation_cargo cc , cargo_operation oo " +
					"where cc.oper_id = oo.id and oo.type=1");
			StringBuilder operationCount = new StringBuilder("select count(dd.num_id) from(select count(oo.id) num_id from cargo_operation_cargo cc , cargo_operation oo " +
					"where cc.oper_id = oo.id  and oo.type=1");
			
			if(!operationCode.trim().equals("")){
				operationQuery.append(" and oo.code='"+operationCode+"'");
				operationCount.append(" and oo.code='"+operationCode+"'");
				 
				paramBuf.append("&operationCode="+operationCode);
			}
			
			if(!productCode.trim().equals("")){
				voProduct product = (voProduct) wareService.getProduct(productCode);
				int productId= 0;
				if(product!=null) productId = product.getId();
					
				operationQuery.append(" and cc.product_id="+productId);
				operationCount.append(" and cc.product_id="+productId);
			 
				paramBuf.append("&productCode="+productCode);
			}
			
			if(!cargoCode.trim().equals("")){
				operationQuery.append(" and cc.out_cargo_whole_code='"+cargoCode+"'");
				operationCount.append(" and cc.out_cargo_whole_code='"+cargoCode+"'");
				paramBuf.append("&cargoCode="+cargoCode);
			}
			if(statuses!=null && statuses.length>0){
				StringBuilder sb_temp = new StringBuilder(" and oo.status in (");
				for(int i=0;i<statuses.length;i++){
					if(statuses[i].equals("12")){
						sb_temp.append("12,13,14,15");
					}else if(statuses[i].equals("16")){
						sb_temp.append("16,17,18");
					}else{
						sb_temp.append(statuses[i]);
					}
					if(i!=statuses.length-1){
						sb_temp.append(",");
					}
					paramBuf.append("&status="+statuses[i]);
				}
				sb_temp.append(")");
				operationQuery.append(sb_temp.toString());
				operationCount.append(sb_temp.toString());
			}
			operationQuery.append(" GROUP by oo.id ");
			operationCount.append(" GROUP by oo.id ) dd");
			operationQuery.append(" order by oo.code desc");
			
			
			int totalCount = service.getTablesCount(operationCount.toString()); //根据条件得到 总数量
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
            operationList = service.getCargoOperationCascade(operationQuery.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage);//获取下架作业单列表
            Iterator itr = operationList.iterator();
            while(itr.hasNext()){
            	CargoOperationBean operBean = (CargoOperationBean)itr.next();
            	CargoOperationProcessBean process=service.getCargoOperationProcess("id="+operBean.getStatus());
            	if(process!=null){
            		operBean.setStatusName(process.getStatusName());
            	}else{
            		operBean.setStatusName("");
            	}
            }
			paging.setPrefixUrl("admin/cargoDownShelf.do?method=shelfDownList"+paramBuf.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("operationList", operationList);
			request.setAttribute("para", paramBuf.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}  
		 
		return mapping.findForward("downShelfList");
	}

	/**
	 *	删除下架作业单
	 */
	public ActionForward delShelfDown(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));//作业单Id
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10){
					request.setAttribute("tip", "该作业单已确认，无法删除！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.deleteCargoOperation("id = "+id);
				service.deleteCargoOperationCargo("oper_id = "+id);
				request.setAttribute("delete","1");
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		return new ActionForward("/admin/cargoDownShelf.do?method=shelfDownList&pageIndex="+request.getParameter("pageIndex"));
	}
	
	
	/**
	 *	删除下架 作业单产品
	 */
	public ActionForward deleteOperProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("cocId"));
		int operId = 0;

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			CargoOperationCargoBean coc = service.getCargoOperationCargo("id = "+id);
			if(coc == null){
				request.setAttribute("tip", "该作业单产品不存在！");
				request.setAttribute("url","cargo/changeOperationUp.jsp");
				return mapping.findForward("tip");
			}
			
			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+coc.getOperId());
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10){
				request.setAttribute("tip", "该作业单已确认，无法删除！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			voProduct product=wareService.getProduct(coc.getProductId());
			CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
			logBean.setOperId(cargoOperation.getId());
			logBean.setOperAdminId(user.getId());
			logBean.setOperAdminName(user.getUsername());
			logBean.setOperDatetime(DateUtil.getNow());
			logBean.setRemark("删除下架单商品，商品"+product.getCode());
			service.addCargoOperationLog(logBean);
			operId = coc.getOperId();
			service.deleteCargoOperationCargo("id = "+coc.getId());
			if(coc.getType() == 0){
				service.deleteCargoOperationCargo("out_cargo_product_stock_id = "+coc.getOutCargoProductStockId()+" and type = 1 and oper_id = "+coc.getOperId());
			}else{
				service.deleteCargoOperationCargo("in_cargo_product_stock_id = "+coc.getInCargoProductStockId()+" and type = 0 and oper_id = "+coc.getOperId());
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return new ActionForward("/admin/cargoDownShelf.do?method=showDownShel&id="+operId);
	}
	
	
	/**
	 * 
	 * @author Administrator
	 * 在散件区货位显示可以下架的商品列表
	 */
	public ActionForward cargoDownShelfList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int countPerPage = 20;
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		int stockCountStart = StringUtil.toInt(request.getParameter("stockCountStart"));
		int stockCountEnd=StringUtil.toInt(request.getParameter("stockCountEnd"));
		String area=request.getParameter("area");//库地区
		if(area==null){
			area="-1";
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StringBuffer bs = new StringBuffer("select ca.id, ca.cargo_id,info.whole_code,ca.product_id, ca.stock_count,ca.stock_lock_count,info.max_stock_count," +
				"info.warn_stock_count,info.type,info.length,info.width,info.high ,info.remark,pr.name ,pr.code from " +
				"cargo_product_stock ca , cargo_info info ,product pr where ca.cargo_id = info.id and ca.product_id=pr.id and info.store_type=0 and info.area_id="+area+" ");
		
		StringBuffer bs_count= new StringBuffer("select count(ca.cargo_id) from cargo_product_stock ca , cargo_info info ,product pr" +
				" where ca.cargo_id = info.id and ca.product_id=pr.id and info.store_type=0  and info.area_id="+area+" ");
		StringBuilder paramBuf = new StringBuilder();
		
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			paramBuf.append("&area="+area);
			if(!cargoCode.trim().equals("")){
				bs.append(" and info.whole_code like '"+cargoCode+"%'");
				bs_count.append(" and info.whole_code like '"+cargoCode+"%'");
				paramBuf.append("&cargoCode="+cargoCode);
			}
			if(!productCode.trim().equals("")){
				voProduct product = (voProduct) wareService.getProduct(productCode);
				int productId= product.getId();
				bs.append(" and ca.product_id="+productId);
				bs_count.append(" and ca.product_id="+productId);
				paramBuf.append("&productCode="+productCode);
			}
			if(stockCountStart!=-1){
				bs.append(" and ca.stock_count>="+stockCountStart);
				bs_count.append(" and ca.stock_count>="+stockCountStart);
			 
				paramBuf.append("&stockCountStart="+stockCountStart);
			}
			if(stockCountEnd!=-1){
				bs.append(" and ca.stock_count<="+stockCountEnd);
				bs_count.append(" and ca.stock_count<="+stockCountEnd);
				paramBuf.append("&stockCountEnd="+stockCountEnd);
			}
			
			//去掉有相关调拨单的货位
			StringBuilder sBuilder=new StringBuilder();
			sBuilder.append("(0,");
			String sql="select * from cargo_operation co, cargo_operation_cargo coc,cargo_info ci" +
					" where co.id=coc.oper_id and co.status in(28,29,30,31,32,33) and co.type=3 and " +
					"ci.whole_code=coc.out_cargo_whole_code;";
			service.getDbOp().prepareStatement(sql);
			PreparedStatement ps = wareService.getDbOp().getPStmt();
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				sBuilder.append(rs.getInt("ci.id"));
				sBuilder.append(",");
			}
			String ids=sBuilder.subSequence(0, sBuilder.length()-1)+")";
			bs.append(" and info.id not in ");
			bs.append(ids);
			
			bs.append(" order by info.whole_code desc");
			bs_count.append(" and info.id not in");
			bs_count.append(ids);
			ArrayList downShelfList =  new ArrayList();
			int totalCount = service.getTablesCount(bs_count.toString()); //根据条件得到 总数量
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			downShelfList = service.getDownShelfList(bs.toString(),paging.getCurrentPageIndex() * countPerPage, countPerPage);
			paging.setPrefixUrl("admin/cargoDownShelf.do?method=cargoDownShelfList"+paramBuf.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("downShelfList", downShelfList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		} 
		 
		return mapping.findForward("success");
	}
	
}
