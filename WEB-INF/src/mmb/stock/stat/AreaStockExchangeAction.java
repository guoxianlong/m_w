package mmb.stock.stat;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.PageUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


public class AreaStockExchangeAction extends DispatchAction {
	
	private final Log logger = LogFactory.getLog(AreaStockExchangeAction.class);
	private static final Object lock = new Object();
	public Log stockLog = LogFactory.getLog("stock.Log");
	private static int PREPAGECOUNT = 20;
	
	
	
	//判断是否存在未完成的调拨单
	public ActionForward checkExchange(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String productCode = request.getParameter("productCode");
		String area = request.getParameter("area");
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		response.setCharacterEncoding("utf-8");
		try{
			AreaStockExchangeService asService = new AreaStockExchangeService(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
			voProduct product = wareService.getProduct(productCode);
			String result = asService.checkExchange(area, product.getId());
			response.getWriter().write(result);
		}catch(Exception e){
			e.printStackTrace();
			response.getWriter().write("系统异常，请联系管理员");
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
		return null;
	}
	
	//导出待调入或调出列表
	public ActionForward exportExchangeProduct(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String type = request.getParameter("type");
		String flag = request.getParameter("flag");
		
		if(flag == null || flag.equals("")){
			request.setAttribute("tip", "参数传递错误！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		if(type == null || type.equals("")){
			request.setAttribute("tip", "参数传递错误！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		if(flag.equals("0") && !group.isFlag(729)){
			request.setAttribute("tip", "您没有权限导出待调入商品调列表！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if(flag.equals("1") && !group.isFlag(730)){
			request.setAttribute("tip", "您没有权限导出待调出商品列表！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String[] aseBeanId = request.getParameterValues("waitExchangeId");
		if(aseBeanId == null || aseBeanId.length<=0){
			request.setAttribute("tip", "请选择需要导出的商品！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try{
			AreaStockExchangeService service = new AreaStockExchangeService(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
			HSSFWorkbook hwb = service.exportExchangeProduct(aseBeanId,type,flag);
			response.reset();
			String fileName = null;
			if(type.equals("0")){
				if(flag.equals("0")){
					fileName = "无锡待调入商品列表";
				}else{
					fileName = "无锡待调出商品列表";
				}
			}else{
				if(flag.equals("1")){
					fileName = "增城待调入商品列表";
				}else{
					fileName = "增城待调出商品列表";
				}
			}
			
			String agent = request.getHeader("User-Agent");
			boolean isMSIE = (agent != null && agent.indexOf("MSIE") != -1);

			if (isMSIE) {
			    fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {
			    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
//			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
//			response.setContentType("application/vnd.ms-excel;");
//			response.setCharacterEncoding("utf-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ DateUtil.getNowDateStr() + fileName + ".xls");
			response.setContentType("application/msxls");
			hwb.write(response.getOutputStream());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
		return null;
		
	}
	
	//获取待调入调出列表
	public ActionForward getExchangeList(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
    	}
    	
		String type = request.getParameter("type");
		String flag = request.getParameter("flag");
		if(type == null || type.equals("")){
			request.setAttribute("tip", "参数传递错误！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		if(flag == null || flag.equals("")){
			request.setAttribute("tip", "参数传递错误！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		request.setAttribute("flag", flag);
		request.setAttribute("type", type);
		
		int countPerPage = PREPAGECOUNT;//每页多少条记录
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		PagingBean paging = null;
		try{
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
			
			
			
			int totalCount = 0;
			
			
			AreaStockExchangeService exchangeService = new AreaStockExchangeService(
							BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
			
			totalCount = exchangeService.getWaitExchangeProductCount(type, flag);
			
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			
			if(totalCount == 0){
				String pageLine = PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", countPerPage);
				request.setAttribute("paging", pageLine);
				request.setAttribute("exchangeList", new ArrayList<AreaStockExchangeBean>());
				return mapping.findForward("waitExchangeList");
			}
			
			paging.setPrefixUrl(request.getContextPath()+"/admin/areaStockExchange.do?method=getExchangeList&type="+type+"&flag="+flag);
			
			//获取数据
			List<CargoOperationBean> exchangeList = 
							exchangeService.getWaitExchangeProductList(type, flag, paging.getCurrentPageIndex()*countPerPage, countPerPage);
			request.setAttribute("exchangeList", exchangeList);
			String pageLine = PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", countPerPage);
			request.setAttribute("paging", pageLine);
			request.setAttribute("userName", user.getUsername());
			return mapping.findForward("waitExchangeList");
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method getExchangeList exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	
	
	//产生跨地区调拨单
	public ActionForward generateExchange(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
    	}
    	UserGroupBean group = user.getGroup();

    	synchronized(lock){

	        int stockInArea = StringUtil.toInt(request.getParameter("stockinArea"));
	        int stockOutArea = StringUtil.toInt(request.getParameter("stockOutArea"));
	        if(stockInArea < 0 || stockOutArea < 0){
	        	request.setAttribute("tip", "参数错误，没有库地区！");
	            request.setAttribute("result", "failure");
	            return mapping.findForward(IConstants.FAILURE_KEY);
	        }

	        if(stockInArea == stockOutArea){
	        	request.setAttribute("tip", "不能在同一个库中调配商品！");
	            request.setAttribute("result", "failure");
	            return mapping.findForward(IConstants.FAILURE_KEY);
	        }
	        
	        
	        if(stockInArea == ProductStockBean.AREA_ZC){
	        	boolean zcInExchange = group.isFlag(732);
	        	if(!zcInExchange){
	        		request.setAttribute("tip", "您没有权限向增城调拨商品！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
	        	}
	        }
	        
	        if(stockInArea == ProductStockBean.AREA_WX){
	        	boolean wxInExchange = group.isFlag(731);
	        	if(!wxInExchange){
	        		request.setAttribute("tip", "您没有权限向无锡调拨商品！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
	        	}
	        }

	        
	        String productCode = StringUtil.dealParam(request.getParameter("productCode"));
	        if (StringUtil.convertNull(productCode).equals("")) {
	            request.setAttribute("tip", "参数错误，没有产品编号！");
	            request.setAttribute("result", "failure");
	            return mapping.findForward(IConstants.FAILURE_KEY);
	        }
	        int exchangeCount = StringUtil.StringToId(request.getParameter("exchangeCount"));
	        if (exchangeCount == 0) {
	            request.setAttribute("tip", "参数错误，没有调拨数量！");
	            request.setAttribute("result", "failure");
	            return mapping.findForward(IConstants.FAILURE_KEY);
	        }
	        
	        StringBuilder message = new StringBuilder();
	        WareService wareService = null;
			ICargoService service = null;
	        try {
	        	 
	        	wareService = new WareService();
	        	voProduct product = wareService.getProduct(productCode);
	        	
	        	service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        	
		        //寻找源库货位
		        List<CargoProductStockBean> cargoAndProductList = service.getCargoAndProductStockList("cps.product_id="
		        															+ product.getId() + " and ci.stock_type="
		        															+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and ci.status=0 and ci.area_id="
		        															+ stockOutArea+" and ci.store_type!=" + CargoInfoBean.STORE_TYPE2 + " and cps.stock_count>0", -1, -1, null);
//				boolean flag = false;
		        
		        //寻找满足调拨量的货位,累加货位上库存信息，当库存满足时，找到的所有货位信息都要生成调拨单的货位库存信息
		        int countSum = 0;
		        List<CargoProductStockBean> cargoProductSum = new ArrayList<CargoProductStockBean>();
		        for(CargoProductStockBean cps : cargoAndProductList){
		        	//分配目的货位
					CargoInfoBean inCi = null;
					if(cps.getCargoInfo().getStoreType() == 0||cps.getCargoInfo().getStoreType() == 4){
						List<CargoProductStockBean> inCpsList = service.getCargoAndProductStockList(
								"ci.area_id="+stockInArea+" and ci.stock_type="
								+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="
								+cps.getCargoInfo().getStoreType()+" and ci.space_lock_count=0 and cps.product_id="
								+cps.getProductId(), -1, -1, null);
						if(inCpsList.size()>0){
							CargoProductStockBean cps1 = (CargoProductStockBean)inCpsList.get(0);
							CargoInfoBean cargoInfo = cps1.getCargoInfo();
							inCi=cargoInfo;
						}
						if(inCi==null){//查询一个未使用的货位
							CargoInfoBean tempInCi = service.getCargoInfo(
									"area_id=" + stockInArea + " and stock_type=" 
									+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and store_type="
									+ cps.getCargoInfo().getStoreType()+" and status=1");
							if(tempInCi != null){
								if(!service.updateCargoInfo("status=0", "id="+tempInCi.getId())){
									message.append("更新货位状态失败！");
									wareService.getDbOp().rollbackTransaction();
									continue;
								}
								CargoProductStockBean newCps=new CargoProductStockBean();
								newCps.setCargoId(tempInCi.getId());
								newCps.setProductId(cps.getProductId());
								newCps.setStockCount(0);
								newCps.setStockLockCount(0);
								if(!service.addCargoProductStock(newCps)){
									message.append("添加货位产品信息失败！");
									wareService.getDbOp().rollbackTransaction();
									continue;
								}
								inCi=tempInCi;
							}
						}
					}else if(cps.getCargoInfo().getStoreType() == 1){
						List<CargoProductStockBean> inCpsList=service.getCargoAndProductStockList(
								"ci.area_id="+stockInArea+" and ci.stock_type="
								+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and ci.store_type="
								+ cps.getCargoInfo().getStoreType()
								+ " and ci.space_lock_count=0 and cps.product_id="+cps.getProductId(),-1,-1,null);
						if(inCpsList.size()>0){
							CargoProductStockBean cps1=(CargoProductStockBean)inCpsList.get(0);
							CargoInfoBean cargoInfo=cps1.getCargoInfo();
							inCi=cargoInfo;
						}
						if(inCi==null){ //查询非此SKU整件区货位
							inCpsList=service.getCargoAndProductStockList(
									"ci.area_id=" + stockInArea + " and ci.stock_type="
									+ CargoInfoBean.STOCKTYPE_QUALIFIED +" and ci.store_type=" 
									+ cps.getCargoInfo().getStoreType() 
									+ " and ci.space_lock_count=0", -1, -1, null);
							if(inCpsList.size()>0){
								CargoProductStockBean cps1=(CargoProductStockBean)inCpsList.get(0);
								CargoInfoBean cargoInfo=cps1.getCargoInfo();
								inCi=cargoInfo;
								if(cargoInfo.getStatus()!=0){
									if(!service.updateCargoInfo("status=0", "id="+cargoInfo.getId())){
										message.append("更新货位状态失败！");
										wareService.getDbOp().rollbackTransaction();
										continue;
									}
								}
							}
						}
					}
					if(inCi != null){
						countSum += cps.getStockCount();
						cps.setTargetCargoInfo(inCi);
		        		if(countSum>=exchangeCount){
		        			cps.setStockCount(cps.getStockCount()+exchangeCount-countSum);
		        			cargoProductSum.add(cps);
//		        			flag = true;
		        			break;
		        		}else{
		        			cargoProductSum.add(cps);
		        		}
					}
	        		
		        }
//		        if(!flag){
//	        	 	request.setAttribute("tip", "源货位不存在或者货位库存不足，请调整调拨量！");
//		            request.setAttribute("result", "failure");
//		            return mapping.findForward(IConstants.FAILURE_KEY);
//		        }
		        
		        if(cargoProductSum == null || cargoProductSum.isEmpty()){
		        	request.setAttribute("tip", "没有目的货位！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
		        }
		        
		        int lastOpId = 0;
		        for(CargoProductStockBean cps : cargoProductSum){

		        	String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");
		        	wareService.getDbOp().startTransaction();
					//生成编号
					CargoOperationBean cargoOper = service.getCargoOperation(
										"code like '"+code+"%' order by id desc limit 1");
					if(cargoOper == null){
						code = code + "00001";
					}else{
						//获取当日计划编号最大值
						String _code = cargoOper.getCode();
						int number = Integer.parseInt(_code.substring(_code.length()-5));
						number++;
						code += String.format("%05d",new Object[]{new Integer(number)});
					}

					String storageCode = cps.getCargoInfo().getWholeCode().substring(0,cps.getCargoInfo().getWholeCode().indexOf("-"));
					cargoOper = new CargoOperationBean();
					cargoOper.setCode(code);
					cargoOper.setCreateDatetime(DateUtil.getNow());
					cargoOper.setCreateUserId(user.getId());
					cargoOper.setCreateUserName(user.getUsername());
					cargoOper.setRemark("");
					cargoOper.setSource("");
					cargoOper.setStockInType(cps.getTargetCargoInfo().getStoreType());
					cargoOper.setStockOutType(cps.getCargoInfo().getStoreType());
					cargoOper.setStorageCode(storageCode);
					cargoOper.setType(CargoOperationBean.TYPE3);
					cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS28);
					cargoOper.setLastOperateDatetime(DateUtil.getNow());
					cargoOper.setStockOutArea(stockOutArea);
					cargoOper.setStockInArea(stockInArea);
					//添加cargo_operation
					if(!service.addCargoOperation(cargoOper)){
						wareService.getDbOp().rollbackTransaction();
						continue;
					}

					int cargoOperId = service.getDbOp().getLastInsertId();
					cargoOper.setId(cargoOperId);
					
					CargoOperationCargoBean coc = new CargoOperationCargoBean();
					coc.setOperId(cargoOperId);
					coc.setInCargoProductStockId(0);
					coc.setProductId(cps.getProductId());
					coc.setType(1);
					coc.setOutCargoProductStockId(cps.getId());
					coc.setOutCargoWholeCode(cps.getCargoInfo().getWholeCode());
					coc.setStockCount(cps.getStockCount());
					if(!service.addCargoOperationCargo(coc)){
						message.append("添加货位间调拨单详细信息失败！");
						wareService.getDbOp().rollbackTransaction();
						continue;
					}
					
					CargoOperationLogBean logBean = new CargoOperationLogBean();
					logBean.setOperId(cargoOperId);
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark=new StringBuilder("制单：");
					
					logRemark.append("商品");
					logRemark.append(product.getCode());
					logRemark.append("，");
					logRemark.append("源货位（");
					logRemark.append(cps.getCargoInfo().getWholeCode());
					logRemark.append("）");
					
					///////////////////////////////////////////////////////////
					CargoOperationCargoBean inCoc=new CargoOperationCargoBean();
					inCoc.setOperId(cargoOperId);
					inCoc.setProductId(coc.getProductId());
					inCoc.setOutCargoProductStockId(cps.getId());
					inCoc.setOutCargoWholeCode(cps.getCargoInfo().getWholeCode());
					inCoc.setStockCount(coc.getStockCount());
					inCoc.setType(0);
					inCoc.setUseStatus(1);
					CargoProductStockBean inCps = service.getCargoProductStock(
							"cargo_id=" + cps.getTargetCargoInfo().getId() + " and product_id=" + coc.getProductId());
					if(inCps==null){//如果该货位没有库存记录，则添加新的库存记录
						inCps=new CargoProductStockBean();
						inCps.setCargoId(cps.getTargetCargoInfo().getId());
						inCps.setProductId(coc.getProductId());
						inCps.setStockCount(0);
						inCps.setStockLockCount(0);
						service.addCargoProductStock(inCps);
						inCps.setId(service.getDbOp().getLastInsertId());
					}
					inCoc.setInCargoProductStockId(inCps.getId());
					inCoc.setInCargoWholeCode(cps.getTargetCargoInfo().getWholeCode());
					if(!service.addCargoOperationCargo(inCoc)){
						message.append("添加货位间调拨单详细信息失败！");
						wareService.getDbOp().rollbackTransaction();
						continue;
					}//添加目的货位记录
					
					logRemark.append("，");
					logRemark.append("目的货位（");
					logRemark.append(cps.getTargetCargoInfo().getWholeCode());
					logRemark.append("）");
					

					logBean.setRemark(logRemark.toString());
					service.addCargoOperationLog(logBean);
					
					CargoOperLogBean operLog=new CargoOperLogBean();//员工操作日志
					operLog.setOperId(cargoOper.getId());
					operLog.setOperCode(cargoOper.getCode());
					CargoOperationProcessBean process=service.getCargoOperationProcess(
										"id="+CargoOperationProcessBean.OPERATION_STATUS28);
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
						message.append("添加日志信息失败！");
						wareService.getDbOp().rollbackTransaction();
						continue;
					}
					wareService.getDbOp().commitTransaction();
					lastOpId = cargoOperId;
				}
		        if(lastOpId==0){
		        	
		        	String str = message.toString();
		        	request.setAttribute("tip", str);
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
		        }
	    		ActionForward af = new ActionForward("/admin/cargoOperation.do?method=exchangeCargo&cargoOperId="+lastOpId);
	    		return af;
	        }catch(Exception e){
	        	e.printStackTrace();
	        	if(stockLog.isErrorEnabled()){
	        		stockLog.error("addStockExchange error", e);
	        	}
	        	request.setAttribute("tip", "系统异常，请联系管理员！");
                request.setAttribute("result", "failure");
                wareService.getDbOp().rollbackTransaction();
                return mapping.findForward(IConstants.FAILURE_KEY);
	        }finally {
	           wareService.releaseAll();
	        }
    	}
	}
	
	
	//获取调拨商品列表
	public ActionForward queryExchangeProductList(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		
		String type = request.getParameter("type");
		if(type == null){
			request.setAttribute("tip", "参数错误：type！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if(type.equals("0")&&!group.isFlag(727)){
			request.setAttribute("tip", "您没有权限查看无锡商品调拨列表！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if(type.equals("1")&&!group.isFlag(728)){
			request.setAttribute("tip", "您没有权限查看增城商品调拨列表！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int countPerPage = PREPAGECOUNT;//每页多少条记录
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		PagingBean paging = null;
		try{
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
			
			
			int totalCount = 0;
			
			
			AreaStockExchangeService exchangeService = new AreaStockExchangeService(
							BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
			
			if(type.equals("0")){
				totalCount = exchangeService.getAreaStockExchangeBeanCount(ProductStockBean.AREA_WX);
			}else{
				totalCount = exchangeService.getAreaStockExchangeBeanCount(ProductStockBean.AREA_ZC);
			}
			
			
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			
			if(totalCount==0){
				request.setAttribute("type", type);
				request.setAttribute("exchangeList", new ArrayList<AreaStockExchangeBean>());
				String pageLine = PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", countPerPage);
				request.setAttribute("paging", pageLine);
				return mapping.findForward("exchangeProductList");
			}
			
			//获取数据
			List<AreaStockExchangeBean> exchangeList = 
							exchangeService.getExchangeList(type, paging.getCurrentPageIndex()*countPerPage, countPerPage);
			request.setAttribute("exchangeList", exchangeList);
			
			paging.setPrefixUrl(request.getContextPath()+"/admin/areaStockExchange.do?method=queryExchangeProductList&type="+type);
			request.setAttribute("type", type);
			String pageLine = PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", countPerPage);
			request.setAttribute("paging", pageLine);
			return mapping.findForward("exchangeProductList");
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method queryExchangeProductList exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	
	
	
	//构造待调度商品列表请求地址
	public ActionForward constructExchangeProductUrl(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(727)&&!group.isFlag(728)){
			request.setAttribute("tip", "您没有权限查看待调度商品列表！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		try{
			
			if(group.isFlag(727) && group.isFlag(728)){
				request.setAttribute("defualtUrl", 
						request.getContextPath()+"/admin/areaStockExchange.do?method=queryExchangeProductList&type=0");
				request.setAttribute("hasWX", "1");
				request.setAttribute("hasZC", "1");
				request.setAttribute("typeFlag", "0");//标示待调入和调出列表属于哪个地区
			}else if(group.isFlag(727)){
				request.setAttribute("hasWX", "1");
				request.setAttribute("hasZC", "0");
				request.setAttribute("defualtUrl", request.getContextPath()+"/admin/areaStockExchange.do?method=queryExchangeProductList&type=0");
				request.setAttribute("typeFlag", "0");
			}else{
				request.setAttribute("hasWX", "0");
				request.setAttribute("hasZC", "1");
				request.setAttribute("defualtUrl", request.getContextPath()+"/admin/areaStockExchange.do?method=queryExchangeProductList&type=1");
				request.setAttribute("typeFlag", "1");
			}
			return mapping.findForward("areaExchangeList");
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method constructExchangeProductUrl exception", e);
			}
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
	}
	
}
