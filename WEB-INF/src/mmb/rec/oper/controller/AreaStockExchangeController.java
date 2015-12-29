package mmb.rec.oper.controller;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.stock.stat.AreaStockExchangeAction;
import mmb.stock.stat.AreaStockExchangeBean;
import mmb.stock.stat.AreaStockExchangeService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 说明：分区库存储量调整
 * 
 * @author 张晔
 *
 * 时间：2012.09.03
 */
@Controller
@RequestMapping("/AreaStockExchangeController")
public class AreaStockExchangeController {
	private final Log logger = LogFactory.getLog(AreaStockExchangeAction.class);
	private static final Object lock = new Object();
	public Log stockLog = LogFactory.getLog("stock.Log");
	public static String noSession = "/admin/rec/oper/salesReturned/noSession.jsp";
	
	
	//构造待调度商品列表请求地址
	@RequestMapping("/constructExchangeProductUrl")
	public void constructExchangeProductUrl(
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(727)&&!group.isFlag(728)){
			request.setAttribute("msg", "您没有权限查看待调度商品列表！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		
		try{
			
			if(group.isFlag(727) && group.isFlag(728)){
				request.setAttribute("hasWX", "1");
				request.setAttribute("hasZC", "1");
			}else if(group.isFlag(727)){
				request.setAttribute("hasWX", "1");
				request.setAttribute("hasZC", "0");
			}else{
				request.setAttribute("hasWX", "0");
				request.setAttribute("hasZC", "1");
			}
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/oper/areaStockExchange/areaExchangeList.jsp").forward(request, response);
			return;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method constructExchangeProductUrl exception", e);
			}
			e.printStackTrace();
			request.setAttribute("msg", "系统异常，请联系管理员！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
	}
	
	//跳转到调拨商品列表页面
	@RequestMapping("/toQueryExchangeProductList")
	public String toQueryExchangeProductList(
			HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		
		UserGroupBean group = user.getGroup();
		
		String type = request.getParameter("type");
		if(type == null){
			request.setAttribute("msg", "参数错误：type！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		if(type.equals("0")&&!group.isFlag(727)){
			request.setAttribute("msg", "您没有权限查看无锡商品调拨列表！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		if(type.equals("1")&&!group.isFlag(728)){
			request.setAttribute("msg", "您没有权限查看增城商品调拨列表！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		if (type.equals("0")) {
			return "/admin/rec/oper/areaStockExchange/wxareaExpList";
		}
		if (type.equals("1")) {
			return "/admin/rec/oper/areaStockExchange/zcareaExpList";
		}
		return null;
	}
	
	//获取调拨商品列表
	@RequestMapping("/queryExchangeProductList")
	@ResponseBody
	public EasyuiDataGridJson queryExchangeProductList(
			HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		
		UserGroupBean group = user.getGroup();
		
		String type = request.getParameter("type");
		if(type == null){
			request.setAttribute("msg", "参数错误：type！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		if(type.equals("0")&&!group.isFlag(727)){
			request.setAttribute("msg", "您没有权限查看无锡商品调拨列表！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		if(type.equals("1")&&!group.isFlag(728)){
			request.setAttribute("msg", "您没有权限查看增城商品调拨列表！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try{
			
			int totalCount = 0;
			
			AreaStockExchangeService exchangeService = new AreaStockExchangeService(
							BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
			
			if(type.equals("0")){
				totalCount = exchangeService.getAreaStockExchangeBeanCount(ProductStockBean.AREA_WX);
			}else{
				totalCount = exchangeService.getAreaStockExchangeBeanCount(ProductStockBean.AREA_ZC);
			}
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			if(totalCount==0){
				easyuiDataGridJson.setTotal((long) totalCount);
				easyuiDataGridJson.setRows(new ArrayList<AreaStockExchangeBean>());
				return easyuiDataGridJson;
			}
			
			//获取数据
			List<AreaStockExchangeBean> exchangeList = 
							exchangeService.getExchangeList(type, (easyuiDataGrid.getPage()-1)*easyuiDataGrid.getRows(), easyuiDataGrid.getRows());
			easyuiDataGridJson.setTotal((long) totalCount);
			easyuiDataGridJson.setRows(exchangeList);
			return easyuiDataGridJson;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method queryExchangeProductList exception", e);
			}
			request.setAttribute("msg", "系统异常，请联系管理员！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	
	//跳转到待调入调出列表
	@RequestMapping("/toGetExchangeList")
	public String toGetExchangeList(
			HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
            request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
    	}
    	
		String type = request.getParameter("type");
		String flag = request.getParameter("flag");
		if(type == null || type.equals("")){
            request.setAttribute("msg", "参数传递错误！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		
		if(flag == null || flag.equals("")){
            request.setAttribute("msg", "参数传递错误！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		
		if (type.equals("0") && flag.equals("0")) {
			return "/admin/rec/oper/areaStockExchange/wxinwaitExchangeList";
		}
		if (type.equals("0") && flag.equals("1")) {
			return "/admin/rec/oper/areaStockExchange/wxoutwaitExchangeList";
		}
		if (type.equals("1") && flag.equals("0")) {
			return "/admin/rec/oper/areaStockExchange/zcinwaitExchangeList";
		}
		if (type.equals("1") && flag.equals("1")) {
			return "/admin/rec/oper/areaStockExchange/zcoutwaitExchangeList";
		}
		return null;
	}
	
	//获取待调入调出列表
	@RequestMapping("/getExchangeList")
	@ResponseBody
	public EasyuiDataGridJson getExchangeList( 
			HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
            request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
    	}
    	
		String type = request.getParameter("type");
		String flag = request.getParameter("flag");
		if(type == null || type.equals("")){
            request.setAttribute("msg", "参数传递错误！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		
		if(flag == null || flag.equals("")){
            request.setAttribute("msg", "参数传递错误！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try{
			int totalCount = 0;
			AreaStockExchangeService exchangeService = new AreaStockExchangeService(
							BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
			
			totalCount = exchangeService.getWaitExchangeProductCount(type, flag);
			
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			
			if(totalCount == 0){
				easyuiDataGridJson.setTotal((long)totalCount);
				easyuiDataGridJson.setRows(new ArrayList<AreaStockExchangeBean>());
				return easyuiDataGridJson;
			}
			
			
			//获取数据
			List<CargoOperationBean> exchangeList = 
							exchangeService.getWaitExchangeProductList(type, flag, (easyuiDataGrid.getPage()-1)* easyuiDataGrid.getRows(), easyuiDataGrid.getRows());
			request.setAttribute("userName", user.getUsername());
			easyuiDataGridJson.setTotal((long)totalCount);
			easyuiDataGridJson.setRows(exchangeList);
			return easyuiDataGridJson;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method getExchangeList exception", e);
			}
            request.setAttribute("msg", "系统异常，请联系管理员！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	
	/**
	 *	打印货位间调拨单
	 */
	@RequestMapping("/printExchangeCargo")
	public String printExchangeCargo(
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}

		int id = StringUtil.StringToId(request.getParameter("id"));

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try{
			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);//查找调拨单
			if(cargoOperation == null){
				request.setAttribute("msg", "该作业单不存在！");
				request.getRequestDispatcher(noSession).forward(request, response);
				return null;
			}

			TreeMap printMap=new TreeMap();
			//查找调拨单货位信息
			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 0 and use_status = 1", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter.next();//源货位
				voProduct product = wareService.getProduct(outCoc.getProductId());
				outCoc.setProduct(product);
				if(printMap.get(outCoc.getProductId()+"")!=null){//该产品已添加
					continue;
				}else{
					CargoOperationCargoBean inCoc=service.getCargoOperationCargo("oper_id="+id+" and type=0 and product_id="+outCoc.getProductId());
					CargoInfoBean cargoInfo=service.getCargoInfo("whole_code='"+inCoc.getInCargoWholeCode()+"'");

					if(cargoInfo==null){
						request.setAttribute("msg", "货位错误！");
						request.getRequestDispatcher(noSession).forward(request, response);
						return null;
					}
					List cartonningList=cartonningService.getCartonningList("oper_id="+id+" and cargo_id="+cargoInfo.getId(), -1, -1, null);
					List cartonningList2=new ArrayList();//要传的list
					for(int i=0;i<cartonningList.size();i++){
						CartonningInfoBean cartonningInfo=(CartonningInfoBean)cartonningList.get(i);
						CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
						if(cartonningProduct!=null&&cartonningProduct.getProductId()==outCoc.getProductId()){
							cartonningInfo.setProductBean(cartonningProduct);
							cartonningInfo.setCargoWholeCode(service.getCargoInfo("id="+cartonningInfo.getCargoId()).getWholeCode());
							cartonningList2.add(cartonningInfo);
						}
					}
					outCoc.setCartonningList(cartonningList2);
					printMap.put(outCoc.getProductId()+"", outCoc);
				}
			}
			service.updateCargoOperation("print_count = print_count+1", "id = "+id);
			request.setAttribute("cargoOperation", cargoOperation);
			request.setAttribute("printMap", printMap);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "/admin/rec/oper/areaStockExchange/exchangeCargoPrint";
	}
	
	//导出待调入或调出列表
	@RequestMapping("/exportExchangeProduct")
	public void exportExchangeProduct(
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String type = request.getParameter("type");
		String flag = request.getParameter("flag");
		
		if(flag == null || flag.equals("")){
			request.setAttribute("msg", "参数传递错误！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		
		if(type == null || type.equals("")){
			request.setAttribute("msg", "参数传递错误！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		
		UserGroupBean group = user.getGroup();
		if(flag.equals("0") && !group.isFlag(729)){
			request.setAttribute("msg", "您没有权限导出待调入商品调列表！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		if(flag.equals("1") && !group.isFlag(730)){
			request.setAttribute("msg", "您没有权限导出待调出商品列表！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		String aseBeanIdString = StringUtil.convertNull(request.getParameter("aseBeanId"));
		if(aseBeanIdString.equals("")){
			request.setAttribute("msg", "请选择需要导出的商品！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return;
		}
		
		String[] aseBeanId = aseBeanIdString.split(",");
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
				if(flag.equals("0")){
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
//				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
//				response.setContentType("application/vnd.ms-excel;");
//				response.setCharacterEncoding("utf-8");
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
		return;
		
	}
	
	//判断是否存在未完成的调拨单
	@RequestMapping("/checkExchange")
	public String checkExchange(
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
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
	
	//产生跨地区调拨单
	@RequestMapping("/generateExchange")
	public void generateExchange(
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("msg", "当前没有登录，操作失败！");
            request.getRequestDispatcher(noSession).forward(request, response);
            return;
    	}
    	UserGroupBean group = user.getGroup();

    	synchronized(lock){

	        int stockInArea = StringUtil.toInt(request.getParameter("stockinArea"));
	        int stockOutArea = StringUtil.toInt(request.getParameter("stockOutArea"));
	        if(stockInArea < 0 || stockOutArea < 0){
	            request.setAttribute("msg", "参数错误，没有库地区！");
	            request.getRequestDispatcher(noSession).forward(request, response);
	            return;
	        }

	        if(stockInArea == stockOutArea){
	            request.setAttribute("msg", "不能在同一个库中调配商品！");
	            request.getRequestDispatcher(noSession).forward(request, response);
	            return;
	        }
	        
	        
	        if(stockInArea == ProductStockBean.AREA_ZC){
	        	boolean zcInExchange = group.isFlag(732);
	        	if(!zcInExchange){
		            request.setAttribute("msg", "您没有权限向增城调拨商品！");
		            request.getRequestDispatcher(noSession).forward(request, response);
		            return;
	        	}
	        }
	        
	        if(stockInArea == ProductStockBean.AREA_WX){
	        	boolean wxInExchange = group.isFlag(731);
	        	if(!wxInExchange){
		            request.setAttribute("msg", "您没有权限向无锡调拨商品！");
		            request.getRequestDispatcher(noSession).forward(request, response);
		            return;
	        	}
	        }

	        
	        String productCode = StringUtil.dealParam(request.getParameter("productCode"));
	        if (StringUtil.convertNull(productCode).equals("")) {
	            request.setAttribute("msg", "参数错误，没有产品编号！");
	            request.getRequestDispatcher(noSession).forward(request, response);
	            return;
	        }
	        int exchangeCount = StringUtil.StringToId(request.getParameter("exchangeCount"));
	        if (exchangeCount == 0) {
	            request.setAttribute("msg", "参数错误，没有调拨数量！");
	            request.getRequestDispatcher(noSession).forward(request, response);
	            return;
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
//					boolean flag = false;
		        
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
//			        			flag = true;
		        			break;
		        		}else{
		        			cargoProductSum.add(cps);
		        		}
					}
	        		
		        }
//			        if(!flag){
//		        	 	request.setAttribute("tip", "源货位不存在或者货位库存不足，请调整调拨量！");
//			            request.setAttribute("result", "failure");
//			            return mapping.findForward(IConstants.FAILURE_KEY);
//			        }
		        
		        if(cargoProductSum == null || cargoProductSum.isEmpty()){
		            request.setAttribute("msg", "没有目的货位！");
		            request.getRequestDispatcher(noSession).forward(request, response);
		            return;
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
		            request.setAttribute("msg", str);
		            request.getRequestDispatcher(noSession).forward(request, response);
		            return;
		        }

	            request.getRequestDispatcher("/admin/cargoOperation.do?method=exchangeCargo&cargoOperId="+lastOpId).forward(request, response);
	            return;
	        }catch(Exception e){
	        	e.printStackTrace();
	        	if(stockLog.isErrorEnabled()){
	        		stockLog.error("addStockExchange error", e);
	        	}
                wareService.getDbOp().rollbackTransaction();
	            request.setAttribute("msg", "系统异常，请联系管理员！");
	            request.getRequestDispatcher(noSession).forward(request, response);
	            return;
	        }finally {
	           wareService.releaseAll();
	        }
    	}
	}
	
}
