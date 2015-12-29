package adultadmin.action.cargo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
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
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
 

public class CargoInfoOperationAction extends DispatchAction{

	public static byte[] cargoLock = new byte[0];	
	public Log stockLog = LogFactory.getLog("stock.Log");
	
	/**
	 * 保存编辑上架单 
	 */
	public ActionForward editCargoOperation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operationId").trim());
		String action = StringUtil.convertNull(request.getParameter("action"));
		String msg ="操作成功";
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
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){
					request.setAttribute("tip", "该作业单已确认，无法编辑！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
	
				service.getDbOp().startTransaction();
				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");//源货位信息表
				if(inCocList==null || inCocList.size()<=0){
					request.setAttribute("tip", "没有需要提交的信息，操作失败");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				String[] checkedCocIds =null;
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);//源货位信息
					CargoInfoBean inCargoBean=service.getCargoInfo("whole_code='"+inCoc.getOutCargoWholeCode()+"'");//源货位
					voProduct product=wareService.getProduct(inCoc.getProductId());
					CargoOperationLogBean logBean=new CargoOperationLogBean();//作业单操作记录
					logBean.setOperId(operId);
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark=new StringBuilder("保存编辑：");
					logRemark.append("商品");
					logRemark.append(product.getCode());
					logRemark.append("，源货位（");
					logRemark.append(inCargoBean.getWholeCode());
					logRemark.append("），");
					//目的货位
					List outCocIds = service.getFieldList("id", "cargo_operation_cargo",
							"oper_id = "+operId+" and type = 0 and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId(), -1, -1, null, null, "String");//获取该订单的所有目的货位
					 
					if(request.getParameterValues("upOpertionId"+inCoc.getId())!=null){
						checkedCocIds =request.getParameterValues("upOpertionId"+inCoc.getId());
					}
					
					if(checkedCocIds!=null && checkedCocIds.length>0){
						for(int j=0;j<checkedCocIds.length;j++){
							int refillCount = StringUtil.StringToId(request.getParameter("operationNum"+checkedCocIds[j]));
							
							if(refillCount<0){
								request.setAttribute("tip", "本次作业量，必须输入大于等于0的整数");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if(action.equals("confirm") && refillCount<=0){
								request.setAttribute("tip", "本次作业量必须大于0");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							int checkedCocId=Integer.parseInt(checkedCocIds[j]);
							CargoOperationCargoBean coc=service.getCargoOperationCargo("id="+checkedCocId);
							CargoInfoBean cargoInfo=service.getCargoInfo("whole_code='"+coc.getInCargoWholeCode()+"'");
							CargoProductStockBean cpsBean=service.getCargoProductStock("id="+coc.getInCargoProductStockId());
							//该货位可上架量
							int count=cargoInfo.getMaxStockCount()-cargoInfo.getSpaceLockCount()-cpsBean.getStockCount()-cpsBean.getStockLockCount();
							if(cargoInfo.getStoreType()==CargoInfoBean.STORE_TYPE0&&refillCount>count){
								request.setAttribute("tip", product.getOriname()+"产品的目的货位"+cargoInfo.getWholeCode()+"容量已满，请重新分配货位！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							logRemark.append("目的货位（");
							logRemark.append(coc.getInCargoWholeCode());
							logRemark.append("）");
							logRemark.append("，");
							logRemark.append("上架量（");
							logRemark.append(coc.getStockCount());
							logRemark.append("-");
							logRemark.append(refillCount);
							logRemark.append("）");
							if(j!=checkedCocIds.length-1){
								logRemark.append("，");
							}
							service.updateCargoOperationCargo("stock_count = "+refillCount+",use_status = 1", "id = "+checkedCocIds[j]);
							outCocIds.remove(String.valueOf(checkedCocIds[j]));
						}
					}else{
						if(!action.equals("submitEdit")){ //如果是进入分货位页面。 checkedCocIds 可以为空
							request.setAttribute("tip", "产品必须至少选择一个货位");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					logBean.setRemark(logRemark.toString());
					service.addCargoOperationLog(logBean);
					for(int j=0;j<outCocIds.size();j++){
						service.updateCargoOperationCargo("use_status = 0", "id = "+(String)outCocIds.get(j));
					}
				}
//				}
				service.getDbOp().commitTransaction();
	
			}catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				msg=e.getMessage();
				System.out.print(DateUtil.getNow());e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		if(action.equals("confirm")){
			return confirmUpCargo(mapping, form, request, response);
		}else if(action.equals("submitEdit")){ //先保存 再 进入分配货位页面
			String outCpsId = StringUtil.convertNull(request.getParameter("outCpsId"));
			ActionForward actionForward = new ActionForward();
			actionForward.setPath("/admin/cargoOperation.do?method=usefulCargo&operId="+operId+"&outCpsId="+outCpsId+"&otherPagePass=upShelf");
			actionForward.setRedirect(true);
			return actionForward;
			
		}else{
			request.setAttribute("tip", msg);
			request.setAttribute("url", "cargoOper.do?method=showEditCargoOperation&operationId="+operId);
			return mapping.findForward("tip");
		}
		}
	}
	
	/**
	 * 
	 * 功能: 确认上架单 
	 * <p>作者 李双 Apr 15, 2011 5:13:08 PM
	 */
	public ActionForward confirmUpCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String msg ="操作成功";
		int operId = StringUtil.StringToId(request.getParameter("operationId"));
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService prudoctService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){
					request.setAttribute("tip", "该作业单已确认，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				StockExchangeBean seb = prudoctService.getStockExchange(" code ='"+cargoOperation.getSource()+"'");	
				service.getDbOp().startTransaction();
				
				service.updateCargoOperation("status = "+CargoOperationProcessBean.OPERATION_STATUS2+",effect_status = "+CargoOperationBean.EFFECT_STATUS0+",confirm_datetime='"+DateUtil.getNow()+"',confirm_user_name='"+user.getUsername()+"',last_operate_datetime='"+DateUtil.getNow()+"'", "id = "+operId);
				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)inCocList.get(i);
					CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
					CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
					logBean.setOperId(cargoOperation.getId());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setRemark("确认提交");
					service.addCargoOperationLog(logBean);
					List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<outCocList.size();j++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)outCocList.get(j);
						 
						CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());//原货位商品cps
						CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId()); //目的货位商品cps
						if(temp_inCps==null){
							voProduct product = wareService.getProduct(inCoc.getProductId());
							request.setAttribute("tip", "商品"+product.getCode()+"货位"+inCoc.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败!");
	                    	request.setAttribute("result", "failure");
	                    	service.getDbOp().rollbackTransaction();
	        				return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId()); //目标货位
	 					if(!service.updateCargoProductStockCount(inCps.getId(), -inCoc.getStockCount())){
	                    	request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
	                    	request.setAttribute("result", "failure");
	                    	service.getDbOp().rollbackTransaction();
	        				return mapping.findForward(IConstants.FAILURE_KEY);
	                    }
						if(!service.updateCargoProductStockLockCount(inCps.getId(), inCoc.getStockCount())){
	                    	request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
	                    	request.setAttribute("result", "failure");
	                    	service.getDbOp().rollbackTransaction();
	        				return mapping.findForward(IConstants.FAILURE_KEY);
	                    }
						//改变调拨单的冻结数量
						if(seb!=null){
							StockExchangeProductBean sepBean = prudoctService.getStockExchangeProduct("stock_exchange_id ="+seb.getId()+" and product_id="+temp_inCps.getProductId());
							 // 未上架量 少于 将要上架量 则上架失败
							if((sepBean.getNoUpCargoCount()-inCoc.getStockCount())<0 || (sepBean.getNoUpCargoCount()-sepBean.getUpCargoLockCount()-inCoc.getStockCount())< 0){
								request.setAttribute("tip", "货位库存操作失败,调拨单的数量不足！");
		                    	request.setAttribute("result", "failure");
		                    	service.getDbOp().rollbackTransaction();
		        				return mapping.findForward(IConstants.FAILURE_KEY);
							}
							
							service.updateProductStockLockCount(sepBean.getId(),inCoc.getStockCount());
						}
						
						
						//调整合格库库存
						if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){
							CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
							ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+inCi.getStockType()+" and product_id="+inCoc.getProductId());
							if(outProductStock==null){
								request.setAttribute("tip", "合格库库存数据错误！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!psService.updateProductStockCount(outProductStock.getId(),-inCoc.getStockCount())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!psService.updateProductLockCount(outProductStock.getId(),inCoc.getStockCount())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
						
					}
					//删除coc无用数据
					service.deleteCargoOperationCargo("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 0");
				}
				
				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
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
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS1);
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS2);
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
				msg="操作失败";
				System.out.print(DateUtil.getNow());e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", msg);
		request.setAttribute("url", "cargoOper.do?method=showEditCargoOperation&operationId="+operId);
		return mapping.findForward("tip");
	}
	
	

	/**
	 *	上架单交接阶段
	 */
	public ActionForward auditingUpCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operationId"));
		int nextStatus=StringUtil.toInt(request.getParameter("nextStatus"));//下一个状态
		String msg="操作成功";
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
					request.setAttribute("tip", "该作业单状态已被更新，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().startTransaction();
				if(cargoOperation.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){
					request.setAttribute("tip", "作业单已作业失败！");
                	request.setAttribute("result", "failure");
    				return mapping.findForward(IConstants.FAILURE_KEY);
				}
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
				
				service.updateCargoOperation("status="+nextStatus+",effect_status="+CargoOperationBean.EFFECT_STATUS0+",last_operate_datetime='"+DateUtil.getNow()+"'"+(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS2?(",auditing_datetime='"+DateUtil.getNow()+"',auditing_user_id="+user.getId()+",auditing_user_name='"+user.getUsername()+"'"):""), "id="+operId);
				
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
				service.getDbOp().rollbackTransaction();
				System.out.print(DateUtil.getNow());e.printStackTrace();
				msg="操作失败";
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", msg);
		request.setAttribute("url", "cargoOper.do?method=showEditCargoOperation&operationId="+operId);
		return mapping.findForward("tip");
	}
	
	/**
	 *	上架单作业完成
	 */
	public ActionForward completeUpCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operationId"));
		
		String msg="操作成功";
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService prudoctService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS7){
					request.setAttribute("tip", "该作业单状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List areaList=CargoDeptAreaService.getCargoDeptAreaList(request);
				StockExchangeBean seb = prudoctService.getStockExchange(" code ='"+cargoOperation.getSource()+"'");	
				
				//财务基础数据
				List<BaseProductInfo> financeBaseData = new ArrayList<BaseProductInfo>();
				service.getDbOp().startTransaction();
				//完成货位库存量操作
				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
					CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
					CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
					voProduct product = wareService.getProduct(inCoc.getProductId());
					product.setPsList(prudoctService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
					int stockOutCount = 0;//货位库存变动
					List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<outCocList.size();j++){
						CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
						CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
						CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
						if(temp_inCps==null){
							request.setAttribute("tip", "商品"+product.getCode()+"货位"+outCoc.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败!");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
						//添加判断操作用户是否有 目的货位的地区权限
						int stockAreaId = service.getOldAreaIdOfCargo(outCi);
						boolean isUserHasRight = CargoDeptAreaService.hasStockAreaRight(areaList,stockAreaId);
						if( !isUserHasRight ) {
							request.setAttribute("tip", "你没有目的货位："+outCi.getWholeCode()+"的地区权限,不可以操作！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//更新完成量
						if(!service.updateCargoOperationCargo("complete_count="+outCoc.getStockCount(), "id="+inCoc.getId())){
							request.setAttribute("tip", "更新上架单完成量失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(!service.updateCargoOperationCargo("complete_count="+outCoc.getStockCount(), "id="+outCoc.getId())){
							request.setAttribute("tip", "更新上架单完成量失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(!service.updateCargoProductStockCount(temp_inCps.getId(), outCoc.getStockCount()-outCoc.getCompleteCount())){
							request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(!service.updateCargoProductStockLockCount(outCps.getId(), -(outCoc.getStockCount()-outCoc.getCompleteCount()))){
							request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(seb!=null){
							StockExchangeProductBean sepBean = prudoctService.getStockExchangeProduct("stock_exchange_id ="+seb.getId()+" and product_id="+temp_inCps.getProductId());
							// 未上架量 少于 将要上架量 则上架失败
							if((sepBean.getNoUpCargoCount()-(outCoc.getStockCount()-outCoc.getCompleteCount()))<0 ){
								request.setAttribute("tip", "货位库存操作失败,调拨单的数量不足！！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							if(!service.updateProductStockLockCount(sepBean.getId(),-(outCoc.getStockCount()-outCoc.getCompleteCount()))){
								request.setAttribute("tip", "货位库存操作失败，调拨单冻结数量不足！！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							if(!service.updateProductStockCount(sepBean.getId(), -(outCoc.getStockCount()-outCoc.getCompleteCount()))){
								request.setAttribute("tip", "操作调拨单的未架数量出错！！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}

						//调整合格库库存
						if(outCi.getAreaId()!=inCi.getAreaId()||outCi.stockType!=inCi.stockType){
							CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
							//目的库库存
							ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+outCi.getStockType()+" and product_id="+inCoc.getProductId());
							if(outProductStock==null){
								request.setAttribute("tip", "合格库库存数据错误！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!psService.updateProductStockCount(outProductStock.getId(),outCoc.getStockCount()-outCoc.getCompleteCount())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							CargoInfoAreaBean outCargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
							//源库库存
							ProductStockBean inProductStock=psService.getProductStock("area="+outCargoInfoArea.getOldId()+" and type="+inCi.getStockType()+" and product_id="+inCoc.getProductId());
							if(inProductStock==null){
								request.setAttribute("tip", "合格库库存数据错误！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!psService.updateProductLockCount(inProductStock.getId(),-(outCoc.getStockCount()-outCoc.getCompleteCount()))) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							//出库
							ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
							//入库
							ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
							
							//财务基础数据
							BaseProductInfo base = new BaseProductInfo();
							base.setId(outCoc.getProductId());
							base.setProductStockId(psOut.getId());
							base.setProductStockOutId(psIn.getId());
							base.setOutCount(inCoc.getStockCount()-inCoc.getCompleteCount());
							financeBaseData.add(base);
							//批次修改开始
							//更新批次记录、添加调拨出、入库批次记录
//							List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId(), -1, -1, "id asc");
//							double stockinPrice = 0;
//							double stockoutPrice = 0;
//							if(sbList!=null&&sbList.size()!=0){
//								int stockExchangeCount = inCoc.getStockCount()-inCoc.getCompleteCount();
//								int index = 0;
//								int stockBatchCount = 0;
//
//								do {
//									//出库
//									StockBatchBean batch = (StockBatchBean)sbList.get(index);
//									if(stockExchangeCount>=batch.getBatchCount()){
//										if(!stockService.deleteStockBatch("id="+batch.getId())){
//											request.setAttribute("tip", "数据库操作失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//										stockBatchCount = batch.getBatchCount();
//
//
//										//财务进销存卡片
//										FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//										int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batch.getProductId(), batch.getTicket());
//										product.setPsList(psService.getProductStockList("product_id=" +batch.getProductId(), -1, -1, null));
//										int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), inCi.getAreaId(), inCi.getStockType(), batch.getTicket(), batch.getProductId());
//										int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, inCi.getStockType(), batch.getTicket(),batch.getProductId());
//										int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), inCi.getAreaId(), -1,batch.getTicket(),batch.getProductId());
//										FinanceStockCardBean fsc = new FinanceStockCardBean();
//										fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//										fsc.setCode(cargoOperation.getCode());
//										fsc.setCreateDatetime(DateUtil.getNow());
//										fsc.setStockType(inCi.getStockType());
//										fsc.setStockArea(inCi.getAreaId());
//										fsc.setProductId(batch.getProductId());
//										fsc.setStockId(psIn.getId());
//										fsc.setStockInCount(stockBatchCount);	
//										fsc.setCurrentStock(currentStock);	//只记录分库总库存
//										fsc.setStockAllArea(stockAllArea);
//										fsc.setStockAllType(stockAllType);
//										fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//										fsc.setStockPrice(product.getPrice5());
//										fsc.setType(fsc.getCardType());
//										fsc.setIsTicket(batch.getTicket());
//										fsc.setStockBatchCode(batch.getCode());
//										fsc.setBalanceModeStockCount(_count-stockBatchCount);
//										if(batch.getTicket()==0){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//										}
//										if(batch.getTicket()==1){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//										}
//										if(batch.getTicket()==0){
//											fsc.setBalanceModeStockPrice(fProduct.getPriceHasticket());
//										}
//										if(batch.getTicket()==1){
//											fsc.setBalanceModeStockPrice(fProduct.getPriceNoticket());
//										}
//										//	fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//
//										double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());	//因为StockInPriceSum是负号，故用sub
//										fsc.setAllStockPriceSum(tmpPrice);
//										if(!frfService.addFinanceStockCardBean(fsc)){
//											request.setAttribute("tip", "添加财务卡片失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//
//
//									}else{
//										if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//											request.setAttribute("tip", "数据库操作失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//										stockBatchCount = stockExchangeCount;
//
//										//财务进销存卡片
//										FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//										int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batch.getProductId(), batch.getTicket());
//										product.setPsList(psService.getProductStockList("product_id=" +batch.getProductId(), -1, -1, null));
//										int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), inCi.getAreaId(), inCi.getStockType(), batch.getTicket(), batch.getProductId());
//										int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, inCi.getStockType(), batch.getTicket(),batch.getProductId());
//										int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), inCi.getAreaId(), -1,batch.getTicket(),batch.getProductId());
//										FinanceStockCardBean fsc = new FinanceStockCardBean();
//										fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//										fsc.setCode(cargoOperation.getCode());
//										fsc.setCreateDatetime(DateUtil.getNow());
//										fsc.setStockType(inCi.getStockType());
//										fsc.setStockArea(inCi.getAreaId());
//										fsc.setProductId(batch.getProductId());
//										fsc.setStockId(psIn.getId());
//										fsc.setStockInCount(stockBatchCount);	
//										fsc.setCurrentStock(currentStock);	//只记录分库总库存
//										fsc.setStockAllArea(stockAllArea);
//										fsc.setStockAllType(stockAllType);
//										fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//										fsc.setStockPrice(product.getPrice5());
//										fsc.setType(fsc.getCardType());
//										fsc.setIsTicket(batch.getTicket());
//										fsc.setStockBatchCode(batch.getCode());
//										fsc.setBalanceModeStockCount( _count-stockBatchCount);
//										if(batch.getTicket()==0){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//										}
//										if(batch.getTicket()==1){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//										}
//										if(batch.getTicket()==0){
//											fsc.setBalanceModeStockPrice(fProduct.getPriceHasticket());
//										}
//										if(batch.getTicket()==1){
//											fsc.setBalanceModeStockPrice(fProduct.getPriceNoticket());
//										}
//
//										double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());	//因为StockInPriceSum是负号，故用sub
//										fsc.setAllStockPriceSum(tmpPrice);
//										if(!frfService.addFinanceStockCardBean(fsc)){
//											request.setAttribute("tip", "添加财务卡片失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//
//
//									}
//
//									//添加批次操作记录
//									StockBatchLogBean batchLog = new StockBatchLogBean();
//									batchLog.setCode(cargoOperation.getCode());
//									batchLog.setStockType(batch.getStockType());
//									batchLog.setStockArea(batch.getStockArea());
//									batchLog.setBatchCode(batch.getCode());
//									batchLog.setBatchCount(stockBatchCount);
//									batchLog.setBatchPrice(batch.getPrice());
//									batchLog.setProductId(batch.getProductId());
//									batchLog.setRemark("调拨出库");
//									batchLog.setCreateDatetime(DateUtil.getNow());
//									batchLog.setUserId(user.getId());
//									if(!stockService.addStockBatchLog(batchLog)){
//										request.setAttribute("tip", "添加失败！");
//										request.setAttribute("result", "failure");
//										service.getDbOp().rollbackTransaction();
//										return mapping.findForward(IConstants.FAILURE_KEY);
//									}
//
//									stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//
//									//入库
//									StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId());
//									if(batchBean!=null){
//										if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//											request.setAttribute("tip", "数据库操作失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//
//										//财务进销存卡片
//										FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batchBean.getProductId());
//
//
//										int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batchBean.getProductId(), batchBean.getTicket());
//										product.setPsList(psService.getProductStockList("product_id=" +batch.getProductId(), -1, -1, null));
//										int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), outCi.getAreaId(), outCi.getStockType(), batchBean.getTicket(), batchBean.getProductId());
//										int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, outCi.getStockType(), batchBean.getTicket(),batchBean.getProductId());
//										int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), outCi.getAreaId(), -1,batchBean.getTicket(),batchBean.getProductId());
//										FinanceStockCardBean fsc = new FinanceStockCardBean();
//										fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//										fsc.setCode(cargoOperation.getCode());
//										fsc.setCreateDatetime(DateUtil.getNow());
//										fsc.setStockType(outCi.getStockType());
//										fsc.setStockArea(outCi.getAreaId());
//										fsc.setProductId(batch.getProductId());
//										fsc.setStockId(psOut.getId());
//										fsc.setStockInCount(stockBatchCount);	
//										fsc.setCurrentStock(currentStock);	//只记录分库总库存
//										fsc.setStockAllArea(stockAllArea);
//										fsc.setStockAllType(stockAllType);
//										fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//										fsc.setStockPrice(product.getPrice5());
//										fsc.setType(fsc.getCardType());
//										fsc.setIsTicket(batchBean.getTicket());
//										fsc.setStockBatchCode(batchLog.getBatchCode());
//										fsc.setBalanceModeStockCount( _count-stockBatchCount);
//										if(batchBean.getTicket()==0){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//										}
//										if(batchBean.getTicket()==1){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//										}
//										if(batchBean.getTicket()==0){
//											fsc.setBalanceModeStockPrice(fProduct.getPriceHasticket());
//										}
//										if(batchBean.getTicket()==1){
//											fsc.setBalanceModeStockPrice(fProduct.getPriceNoticket());
//										}
//										//	fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//
//										double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());	//因为StockInPriceSum是负号，故用sub
//										fsc.setAllStockPriceSum(tmpPrice);
//										if(!frfService.addFinanceStockCardBean(fsc)){
//											request.setAttribute("tip", "添加财务卡片失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//
//
//									}else{
//										int ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
//										StockBatchBean newBatch = new StockBatchBean();
//										newBatch.setCode(batch.getCode());
//										newBatch.setProductId(batch.getProductId());
//										newBatch.setPrice(batch.getPrice());
//										newBatch.setBatchCount(stockBatchCount);
//										newBatch.setProductStockId(psOut.getId());
//										newBatch.setStockArea(outCi.getAreaId());
//										newBatch.setStockType(psOut.getType());
//										newBatch.setTicket(ticket);
//										newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//										if(!stockService.addStockBatch(newBatch)){
//											request.setAttribute("tip", "添加失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//
//										FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + newBatch.getProductId());
//										int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), newBatch.getProductId(), newBatch.getTicket());
//										product.setPsList(psService.getProductStockList("product_id=" +newBatch.getProductId(), -1, -1, null));
//										int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatch.getStockArea(), newBatch.getStockType(), newBatch.getTicket(), newBatch.getProductId());
//										int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, newBatch.getStockType(), newBatch.getTicket(),newBatch.getProductId());
//										int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatch.getStockArea(), -1,newBatch.getTicket(),newBatch.getProductId());
//										FinanceStockCardBean fsc = new FinanceStockCardBean();
//										fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//										fsc.setCode(cargoOperation.getCode());
//										fsc.setCreateDatetime(DateUtil.getNow());
//										fsc.setStockType(outCi.getStockType());
//										fsc.setStockArea(outCi.getAreaId());;
//										fsc.setProductId(batch.getProductId());
//										fsc.setStockId(psOut.getId());
//										fsc.setStockInCount(stockBatchCount);	
//										fsc.setCurrentStock(currentStock);	//只记录分库总库存
//										fsc.setStockAllArea(stockAllArea);
//										fsc.setStockAllType(stockAllType);
//										fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//										fsc.setStockPrice(product.getPrice5());
//										fsc.setType(fsc.getCardType());
//										fsc.setIsTicket(newBatch.getTicket());
//										fsc.setStockBatchCode(newBatch.getCode());
//										fsc.setBalanceModeStockCount( _count-stockBatchCount);
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//										fsc.setBalanceModeStockPrice(fProduct.getPriceHasticket());
//
//
//										//	fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//
//										double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());	//因为StockInPriceSum是负号，故用sub
//										fsc.setAllStockPriceSum(tmpPrice);
//										if(!frfService.addFinanceStockCardBean(fsc)){
//											request.setAttribute("tip", "添加财务卡片失败！");
//											request.setAttribute("result", "failure");
//											service.getDbOp().rollbackTransaction();
//											return mapping.findForward(IConstants.FAILURE_KEY);
//										}
//
//
//
//									}
//
//									//添加批次操作记录
//									batchLog = new StockBatchLogBean();
//									batchLog.setCode(cargoOperation.getCode());
//									batchLog.setStockType(psOut.getType());
//									batchLog.setStockArea(outCi.getAreaId());
//									batchLog.setBatchCode(batch.getCode());
//									batchLog.setBatchCount(stockBatchCount);
//									batchLog.setBatchPrice(batch.getPrice());
//									batchLog.setProductId(batch.getProductId());
//									batchLog.setRemark("调拨入库");
//									batchLog.setCreateDatetime(DateUtil.getNow());
//									batchLog.setUserId(user.getId());
//									if(!stockService.addStockBatchLog(batchLog)){
//										request.setAttribute("tip", "添加批次日志失败！");
//										request.setAttribute("result", "failure");
//										service.getDbOp().rollbackTransaction();
//										return mapping.findForward(IConstants.FAILURE_KEY);
//									}
//
//									stockExchangeCount -= batch.getBatchCount();
//									index++;
//
//									stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//								} while (stockExchangeCount>0&&index<sbList.size());
//							}
							//批次修改结束

							//添加进销存卡片开始
							// 入库卡片
							StockCardBean sc = new StockCardBean();
							sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
							sc.setCode(cargoOperation.getCode());

							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(outCi.getStockType());
							sc.setStockArea(outCi.getAreaId());
							sc.setProductId(outCps.getProductId());
							sc.setStockId(psOut.getId());
							sc.setStockInCount(outCoc.getStockCount()-outCoc.getCompleteCount());
							sc.setStockInPriceSum(product.getPrice5());

							sc.setCurrentStock(product.getStock(outCi.getAreaId(), sc.getStockType())
									+ product.getLockCount(outCi.getAreaId(), sc.getStockType()));
							sc.setStockAllArea(product.getStock(outCi.getAreaId())
									+ product.getLockCount(outCi.getAreaId()));
							sc.setStockAllType(product.getStockAllType(sc.getStockType())
									+ product.getLockCountAllType(sc.getStockType()));
							sc.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());// 新的库存价格
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
									new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!psService.addStockCard(sc)){
								request.setAttribute("tip", "添加入库卡片失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							StockCardBean sc2 = new StockCardBean();

							sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
							sc2.setCode(cargoOperation.getCode());

							sc2.setCreateDatetime(DateUtil.getNow());
							sc2.setStockType(inCi.getStockType());
							sc2.setStockArea(inCi.getAreaId());
							sc2.setProductId(product.getId());
							sc2.setStockId(psIn.getId());
							sc2.setStockOutCount(outCoc.getStockCount()-outCoc.getCompleteCount());
							//								sc2.setStockOutPriceSum(stockOutPrice);
							sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount()-inCoc.getCompleteCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							sc2.setCurrentStock(product.getStock(inCi.getAreaId(), sc2.getStockType())
									+ product.getLockCount(inCi.getAreaId(), sc2.getStockType()));
							sc2.setStockAllArea(product.getStock(inCi.getAreaId())
									+ product.getLockCount(inCi.getAreaId()));
							sc2.setStockAllType(product.getStockAllType(sc2.getStockType())
									+ product.getLockCountAllType(sc2.getStockType()));
							sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc2.setStockPrice(product.getPrice5());
							sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
									new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
							if(!psService.addStockCard(sc2)){
								request.setAttribute("tip", "添加出库卡片失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							//添加进销存卡片结束
						}

						//货位入库卡片
						temp_inCps = service.getCargoProductStock("id = "+temp_inCps.getId());
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELFSTOCKIN);
						csc.setCode(cargoOperation.getCode());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(outCi.getStockType());
						csc.setStockArea(outCi.getAreaId());
						csc.setProductId(product.getId());
						csc.setStockId(temp_inCps.getId());
						csc.setStockInCount(outCoc.getStockCount()-outCoc.getCompleteCount());
						csc.setStockInPriceSum((new BigDecimal(outCoc.getStockCount()-outCoc.getCompleteCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(temp_inCps.getStockCount()+temp_inCps.getStockLockCount());
						csc.setCargoStoreType(outCi.getStoreType());
						csc.setCargoWholeCode(outCi.getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
						service.addCargoStockCard(csc);

						stockOutCount = stockOutCount + outCoc.getStockCount()-outCoc.getCompleteCount();
						if(!service.updateCargoOperationCargo("complete_count="+outCoc.getStockCount(), "id="+outCoc.getId())){
							request.setAttribute("tip", "添加货位入库卡片失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}


					//货位出库卡片
					inCps = service.getCargoProductStock("id = "+inCps.getId());
					CargoStockCardBean csc = new CargoStockCardBean();
					csc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELFSTOCKOUT);
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

					if(!service.updateCargoOperationCargo("complete_count="+inCoc.getStockCount(), "id="+inCoc.getId())){
						request.setAttribute("tip", "添加货位出库卡片失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				
				
				//财务数据
				if(financeBaseData != null && financeBaseData.size() > 0){
					FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(financeBaseData, cargoOperation.getCode(), user.getId(), 0, 0);
				}
				
				//				}
				//				else if(status == 1){
				//					service.updateCargoOperation("status = "+CargoOperationBean.STATUS7, "id = "+operId);
				//					CargoOperationLogBean log = new CargoOperationLogBean();
				//					log.setOperId(cargoOperation.getId());
				//					log.setOperAdminId(user.getId());
				//					log.setOperAdminName(user.getUsername());
				//					log.setOperDatetime(DateUtil.getNow());
				//					log.setRemark("上架失败，备注（"+remark+"）");
				//					service.addCargoOperationLog(log);
				//					List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
				//					for(int i=0;i<inCocList.size();i++){
				//						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
				//						
				//						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
				//						for(int j=0;j<outCocList.size();j++){
				//							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
				//							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
				//							CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
				//							CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
				//							
				//							if(!service.updateCargoProductStockCount(outCps.getId(), outCoc.getStockCount())){
				//		                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
				//		                    	request.setAttribute("result", "failure");
				//		                    	service.getDbOp().rollbackTransaction();
				//		        				return mapping.findForward(IConstants.FAILURE_KEY);
				//		                    }
				//							if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
				//		                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
				//		                    	request.setAttribute("result", "failure");
				//		                    	service.getDbOp().rollbackTransaction();
				//		        				return mapping.findForward(IConstants.FAILURE_KEY);
				//		                    }
				//							if(!service.updateCargoSpaceLockCount(outCi.getId(), -outCoc.getStockCount())){
				//		                    	request.setAttribute("tip", "货位库存操作失败，货位冻结空间不足！");
				//		                    	request.setAttribute("result", "failure");
				//		                    	service.getDbOp().rollbackTransaction();
				//		        				return mapping.findForward(IConstants.FAILURE_KEY);
				//		                    }
				//							//改变调拨单的冻结数量
				//							StockExchangeProductBean sepBean = prudoctService.getStockExchangeProduct("stock_exchange_id ="+seb.getId()+" and product_id="+temp_inCps.getProductId());
				//							if(!service.updateProductStockLockCount(sepBean.getId(),-outCoc.getStockCount())){
				//								request.setAttribute("tip", "解除调拨单的冻结数量出错！！");
				//		                    	request.setAttribute("result", "failure");
				//		                    	service.getDbOp().rollbackTransaction();
				//		        				return mapping.findForward(IConstants.FAILURE_KEY);
				//							}
				//						}
				//					}
				//				}
				//				service.updateCargoOperation("complete_datetime = '"+DateUtil.getNow()+"',complete_user_id = "+user.getId()+",complete_user_name = '"+user.getUsername()+"',remark = '"+remark+"'", "id = "+operId);

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS7);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//				int handleType=process2.getHandleType();//操作方式，0人工确认，1设备确认
				//				if(handleType!=0){
				//					request.setAttribute("tip", "当前操作方式为设备确认！");
				//	               	request.setAttribute("result", "failure");
				//	    			return mapping.findForward(IConstants.FAILURE_KEY);
				//				}
				//				int confirmType=process2.getConfirmType();//作业判断，0不做判断，1源货位，2目的货位，人工确认不需要判断该条件
				//				int deptId1=process2.getDeptId1();//职能归属，一级部门，人工确认不需要判断该条件
				//				int deptId2=process2.getDeptId2();//职能归属，二级部门，人工确认不需要判断该条件
				//				int storageId=process2.getStorageId();//所属仓库，人工确认不需要判断该条件

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog !=null){
					if(lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
						int effectTime=process.getEffectTime();//上阶段时效
						String lastOperateTime=lastLog.getOperDatetime();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long date1=sdf.parse(lastOperateTime).getTime();
						long date2=sdf.parse(DateUtil.getNow()).getTime();
						if(date1+effectTime*60*1000<date2){//已超时
							if(!service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId())){
								request.setAttribute("tip", "修改上一个日志时效失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}
				}

				if(!service.updateCargoOperation(
						"status="+CargoOperationProcessBean.OPERATION_STATUS7+",effect_status="+CargoOperationBean.EFFECT_STATUS2+",last_operate_datetime='"+DateUtil.getNow()+"'"+",complete_datetime='"+DateUtil.getNow()+"',complete_user_id="+user.getId()+",complete_user_name='"+user.getUsername()+"'", "id="+operId)){
					request.setAttribute("tip", "更新上架单状态为作业结束！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName(process2.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(2);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				if(!service.addCargoOperLog(operLog)){
					request.setAttribute("tip", "添加上架单日志失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				service.getDbOp().commitTransaction();

				if(seb!=null){
					//检查对应调拨单是否上架完成
					if(checkStockExchangeUpShelf(seb.getId())){
						prudoctService.getDbOp().getConn().setAutoCommit(true);
						prudoctService.updateStockExchange("up_shelf_status = 2", "id = "+seb.getId());
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				System.out.print(DateUtil.getNow());e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
				msg="操作失败";
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", msg);
		request.setAttribute("url", "cargoOper.do?method=showEditCargoOperation&operationId="+operId);
		return mapping.findForward("tip");
	}
	
	/**
	 *	复核上架单
	 */
	public ActionForward checkUpCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operationId"));
		int status = StringUtil.toInt(request.getParameter("status"));
		String remark = StringUtil.convertNull(request.getParameter("remark"));
		if(status==-1){
			request.setAttribute("tip", "没有选择作业成功或作业失败");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String msg="操作成功";
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService prudoctService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>CargoOperationProcessBean.OPERATION_STATUS7){
					request.setAttribute("tip", "该作业单状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getEffectStatus()==CargoOperationBean.EFFECT_STATUS3||cargoOperation.getEffectStatus()==CargoOperationBean.EFFECT_STATUS4){
					request.setAttribute("tip", "该作业单时效状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1){
					request.setAttribute("tip", "该作业单还未提交，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().startTransaction();
				if(status==3){//作业成功
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS7){
						request.setAttribute("tip", "该作业单未结束，操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					service.updateCargoOperation("effect_status="+CargoOperationBean.EFFECT_STATUS3+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId);
				}else if(status==4){//作业失败
					
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS7){
						//还原货位库存
						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
						for(int i=0;i<outCocList.size();i++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
							CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());
							List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
							for(int j=0;j<inCocList.size();j++){
								CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
								//CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
								CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
								
								if(!service.updateCargoProductStockCount(outCps.getId(), inCoc.getStockCount()-inCoc.getCompleteCount())){
			                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
			                    	request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				return mapping.findForward(IConstants.FAILURE_KEY);
			                    }
								if(!service.updateCargoProductStockLockCount(outCps.getId(), -(inCoc.getStockCount()-inCoc.getCompleteCount()))){
			                    	request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
			                    	request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				return mapping.findForward(IConstants.FAILURE_KEY);
			                    }
								//改变调拨单的冻结数量
								StockExchangeBean seb = prudoctService.getStockExchange(" code ='"+cargoOperation.getSource()+"'");	
								if(seb!=null){
									StockExchangeProductBean sepBean = prudoctService.getStockExchangeProduct("stock_exchange_id ="+seb.getId()+" and product_id="+outCoc.getProductId());
									if(!service.updateProductStockLockCount(sepBean.getId(),-(inCoc.getStockCount()-inCoc.getCompleteCount()))){
										request.setAttribute("tip", "解除调拨单的冻结数量出错！！");
				                    	request.setAttribute("result", "failure");
				                    	service.getDbOp().rollbackTransaction();
				        				return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
								
								//调整合格库库存
								if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){
									CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
									ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+outCi.getStockType()+" and product_id="+outCoc.getProductId());
									if(outProductStock==null){
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(outProductStock.getId(),inCoc.getStockCount()-inCoc.getCompleteCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();	
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductLockCount(outProductStock.getId(),-(inCoc.getStockCount()-inCoc.getCompleteCount()))) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
							}
						}
					}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS7){
						
					}
					service.updateCargoOperation("effect_status="+CargoOperationBean.EFFECT_STATUS4+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId);
						
				}
				
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS7);//下个阶段
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
				System.out.print(DateUtil.getNow());e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
				msg="操作失败";
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", msg);
		request.setAttribute("url", "cargoOper.do?method=showEditCargoOperation&operationId="+operId);
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 功能:上架单详细页
	 * <p>作者 李双 Apr 15, 2011 5:13:08 PM
	 */
	public ActionForward showEditCargoOperation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int id = StringUtil.StringToId(request.getParameter("operationId"));

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 1", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			List currentStockCountList=new ArrayList();//目的货位当前库存列表
			Map otherCargoMap=new HashMap();//是否有其他货位链接
			while(iter.hasNext()){
				CargoOperationCargoBean inCoc = (CargoOperationCargoBean)iter.next();
				CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
				CargoInfoBean ci = service.getCargoInfo(" whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
				voProduct product = wareService.getProduct(inCoc.getProductId());
				inCoc.setCargoInfo(ci);
				inCoc.setProduct(product);
				inCoc.setCargoProductStock(inCps);

				List list = service.getCargoOperationCargoList(
						"out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and oper_id = "+inCoc.getOperId()+" and product_id = "+inCoc.getProductId()+" and type = 0", -1, -1, "in_cargo_whole_code asc");
				inCoc.setCocList(list);

				Iterator iter2 = list.listIterator();
				List currentStockCountList2=new ArrayList();//某源货位的所有目的货位库存列表
				while(iter2.hasNext()){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter2.next();
					CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
					CargoInfoBean outCi = service.getCargoInfo(" whole_code = '"+outCoc.getInCargoWholeCode()+"'");
					if(outCi==null){
						continue;
					}
					product = wareService.getProduct(outCoc.getProductId());
					outCoc.setCargoInfo(outCi);
					outCoc.setProduct(product);
					outCoc.setCargoProductStock(outCps);
					List cpsList=service.getCargoProductStockList("cargo_id="+outCi.getId(), -1, -1, "id asc");//该目的货位所有库存信息列表
					int currentStockCount=0;//该货位当前库存总量
					for(int i=0;i<cpsList.size();i++){
						CargoProductStockBean cpsBean=(CargoProductStockBean)cpsList.get(i);
						currentStockCount+=cpsBean.getStockCount();
						currentStockCount+=cpsBean.getStockLockCount();
					}
					currentStockCountList2.add(""+currentStockCount);
				}
				currentStockCountList.add(currentStockCountList2);
				
				if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1){
					ArrayList cspList =service.getCargoAndProductStockList("ci.status=0 and ci.store_type="+cargoOperation.getStockInType()+" and ci.stock_type=0 and cps.product_id="+inCoc.getProductId()+" and ci.area_id = "+ci.getAreaId()+" and ci.whole_code like '"+cargoOperation.getStorageCode()+"%'", 0, -1, " ci.whole_code asc ");
					if(cspList.size()>list.size()){
						otherCargoMap.put(inCoc.getId()+"", "1");
					}else{
						otherCargoMap.put(inCoc.getId()+"", "0");
					}
				}
			}
			CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());
			if(process!=null){
				cargoOperation.setStatusName(process.getStatusName());
			}
			CargoOperationProcessBean nextStatus=null;//下一个状态，传参用
			CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+(cargoOperation.getStatus()+1));//下一个状态
			CargoOperationProcessBean complete=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS7);//完成作业单状态
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS2
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS3
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS4
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS5){
				if(process2.getUseStatus()==1){
					nextStatus=process2;
				}else{
					nextStatus=complete;
				}
			}
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS6){
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
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS7){
				effectTime="待复核";
			}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS8){
				effectTime="作业成功";
			}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS9){
				effectTime="作业失败";
			}else{
				if(cal1.before(cal2)){
					effectTime="已超时";
				}else{
					effectTime="进行中";
				}
			}
			request.setAttribute("effectTime", effectTime);
			String storageCode=cargoOperation.getStorageCode();
			CargoInfoStorageBean storageBean=service.getCargoInfoStorage("whole_code='"+storageCode+"'");
			request.setAttribute("storageBean", storageBean);
			request.setAttribute("otherCargoMap", otherCargoMap);
			request.setAttribute("cocList", cocList);
			request.setAttribute("cargoOperation", cargoOperation);
			request.setAttribute("currentStockCountList", currentStockCountList);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		
		return mapping.findForward("showEdit");
	}
	
	/**
	 *	删除作业单
	 */
	public ActionForward delCargoOperation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("operationId"));//作业单Id
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
				if(cargoOperation.getCreateUserId()!=user.getId()){
					request.setAttribute("tip", "只能删除自己创建的上架单！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){
					request.setAttribute("tip", "该作业单已确认，无法删除！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
	
				service.deleteCargoOperation("id = "+id);
				service.deleteCargoOperationCargo("oper_id = "+id);
				request.setAttribute("delete","1");
			}catch (Exception e) {
				// TODO: handle exception
				System.out.print(DateUtil.getNow());e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		return new ActionForward("/admin/cargoUpOper.do?method=shelfUpList&pageIndex="+request.getParameter("pageIndex"));
	}
	
	
	/**
	 *	删除上架作业单产品
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
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
	
				CargoOperationCargoBean coc = service.getCargoOperationCargo("id = "+id);
				if( coc == null ) {
					request.setAttribute("tip", "未找到作业商品信息！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+coc.getOperId());
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){
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
				logBean.setRemark("删除上架单商品，商品"+product.getCode());
				service.addCargoOperationLog(logBean);
				operId = coc.getOperId();
				service.deleteCargoOperationCargo("id = "+coc.getId());
				 
				service.deleteCargoOperationCargo("out_cargo_product_stock_id = "+coc.getOutCargoProductStockId()+" and type = 0 and oper_id = "+coc.getOperId());
				 
			}catch (Exception e) {
				// TODO: handle exception
				System.out.print(DateUtil.getNow());e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		return new ActionForward("/admin/cargoOper.do?method=showEditCargoOperation&operationId="+operId);
	}
	
	public boolean checkStockExchangeUpShelf(int id){
		boolean result = true;
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		try{
			List sepList = service.getStockExchangeProductList("stock_exchange_id = "+id, -1, -1, null);
			Iterator iter = sepList.listIterator();
			while(iter.hasNext()){
				StockExchangeProductBean sep = (StockExchangeProductBean)iter.next();
				if(sep.getNoUpCargoCount() != 0 || sep.getUpCargoLockCount() != 0){
					result = false;
				}
			}
		}catch(Exception e){
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return result;
	}
}
