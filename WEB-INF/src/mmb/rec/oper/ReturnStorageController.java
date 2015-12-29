package mmb.rec.oper;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.cargo.model.ReturnedProductVirtual;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.ReturnedPackageService;
import mmb.stock.stat.ReturnedPackageServiceImpl;
import mmb.stock.stat.ReturnedUpShelfBean;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoModelBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.Constants;
import adultadmin.util.CookieUtil;
import adultadmin.util.DateUtil;
import adultadmin.util.PageUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/admin")
public class ReturnStorageController {

	private static Object lock = new Object();
	
	private String date = DateUtil.formatDate(new Date());
	
	private static final String RETUPSHELFCODE = "retUpShelfCode";
	
	private final Log logger = LogFactory.getLog("stock.Log");
	
	/**
	 * 扫描生成退货上架汇总单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("statisticQualifiedRetProduct")
	public String statisticQualifiedRetProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			request.setAttribute("tip", "用户没有登录！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(616)) {
			request.setAttribute("tip", "您没有生成退货上架单的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		if(request.getSession().getAttribute("productCodeMap")!=null){
			request.getSession().removeAttribute("productCodeMap");
		}
		return "forward:/admin/rec/oper/returnShelf/retScanGenerateUpShelf.jsp";
		
	}
	
	/**
	 * 	作者： 石远飞
	 * 
	 *	日期：2013-2-26
	 *	
	 *	说明：退货上架汇总单页面的物流员工作业效率排名_ajax请求
	 *
	 */
	@RequestMapping("ajaxCargoStaffPerformance")
	public String ajaxCargoStaffPerformance(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String firstCount = "";
			String oneselfCount = "";
			String ranking ="";
			String productCount="";
			String firstProductCount="";
			int n = 1;
			CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
			if(csBean == null){
				request.setAttribute("tip", "此账号不是物流员工 !");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=6");
			List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=6", -1, -1, " oper_count DESC");
			if(cspBean != null){
				for(int i = 0;i < cspList.size();i++){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(i);
					if(i==0){
						firstCount = bean.getOperCount() + "";
						firstProductCount = bean.getProductCount() + "";
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
					firstCount = bean.getOperCount() + "";
					firstProductCount = bean.getProductCount()+"";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}else{
					firstCount = "0";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}
			}
			request.setAttribute("firstCount", firstCount);
			request.setAttribute("productCount", productCount);
			request.setAttribute("oneselfCount", oneselfCount);
			request.setAttribute("firstProductCount", firstProductCount);
			request.setAttribute("ranking", ranking);
		}catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}finally{
			wareService.releaseAll();
		}
		return "forward:/admin/cargo/selection.jsp";
	}
	
	/**
	 * 清空缓存中所有上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("clearCachedProductCode")
	public void clearCachedProductCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		try{
			request.getSession().removeAttribute(RETUPSHELFCODE);
			pw.write("0");
			return;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageConroller.clearCachedProductCode exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return;
		}
	}
	
	
	/**
	 * 删除缓存中指定商品条码
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("deleteCacheUpShelf")
	public void deleteCacheUpShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		String retUpShelfCode = request.getParameter("retUpShelfCode");
		if(retUpShelfCode == null){
			pw.write("上架单条码不能为空！");
			return;
		}
		try{
			
			Map retUpShelfMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
			if(retUpShelfMap == null || retUpShelfMap.isEmpty()){
				pw.write("目前缓存中没有任何上架单！");
				return;
			}
			
			if(!retUpShelfMap.containsKey(retUpShelfCode)){
				pw.write("该上架单不存在或者已经删除！");
				return;
			}
			
			retUpShelfMap.remove(retUpShelfCode);
			
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageController.deleteCacheUpShelf exception:", e);
			}
			pw.write("系统异常请联系管理员！");
			return;
		}
		pw.write("该上架单删除成功！");
		return;
	}
	
	
	/**
	 * 取消刚刚扫描的上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("cancelCacheProduct")
	public void cancelCacheProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		String retUpShelfCode = request.getParameter("preProductCode");
		try{
			
			Map productCodeMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
			if(productCodeMap == null || productCodeMap.isEmpty()){
				pw.write("当前没有扫描任何上架单！");
				return;
			}
			
			if(!productCodeMap.containsKey(retUpShelfCode)){
				pw.write("该上架单已经被清除！");
				return;
			}
			productCodeMap.remove(retUpShelfCode);
			pw.write("0");
			return;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageController.cancelCacheProduct exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return;
		}
	}
	
	
	/**
	 * 缓存扫描的上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("cacheProductCode")
	public void cacheProductCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		
		//获取扫描的上架单条码
		String retUpShelfCode = request.getParameter("productCode");
		if(retUpShelfCode == null){
			pw.write("上架单条码不能为空！");
			return;
		}
		if(!retUpShelfCode.contains("HWTS")){
			pw.write("该条码不是退货上架单条码！");
			return;
		}
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		if(wareArea == -1){
			pw.write("您没有选择地区或者您没有权限生成退货上架汇总单！");
			return;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		Map retUpShelfMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
		
		try{
			
			CargoOperationBean cocBean = cargoService.getCargoOperation("code='"+StringUtil.toSql(retUpShelfCode)+"'");
			if(cocBean == null){
				pw.write("该退货上架单不存在！");
				return;
			}
			
			CargoOperationCargoBean cargoOpBean = cargoService.getCargoOperationCargo("oper_id="+cocBean.getId()+" and type=0");
			CargoInfoBean inCi = cargoService.getCargoInfo("whole_code='"+cargoOpBean.getInCargoWholeCode()+"'");
			CargoInfoAreaBean ciaBean = cargoService.getCargoInfoArea("old_id="+wareArea);
			if(inCi.getAreaId()!=ciaBean.getId()){
				pw.write("您汇总的退货上架单(目的)不属于该地区！");
				return;
			}
			
			CargoInfoBean outCi = cargoService.getCargoInfo("whole_code='"+cargoOpBean.getOutCargoWholeCode()+"'");
			if(outCi.getAreaId()!=ciaBean.getId()){
				pw.write("您汇总的退货上架单(源)不属于该地区！");
				return;
			}
			
			if(cocBean.getEffectStatus()==CargoOperationBean.EFFECT_STATUS4){
				pw.write("该退货上架单已作业失败！");
				return;
			}
			
			if(cocBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS2){
				
				if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1){
					pw.write("该退货上架单还没有确认提交！");
					return;
				}
				
				if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS3
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS4
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS5
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS6){
					pw.write("该退货上架单已汇总！");
					return;
				}
				
				if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS7
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS8
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS9){
					pw.write("该退货上架单已完成！");
					return;
				}
			}
			
			
			if(retUpShelfMap == null){
				retUpShelfMap = new HashMap();
				retUpShelfMap.put(retUpShelfCode, retUpShelfCode);
				request.getSession().setAttribute(RETUPSHELFCODE, retUpShelfMap);
			}else{
				if(!retUpShelfMap.isEmpty() && retUpShelfMap.size()==30){
					pw.write("汇总单上最多允许30个上架单，目前已经30个，请生成汇总单后再扫描！");
					return;
				}
				
				if(!retUpShelfMap.containsKey(retUpShelfCode)){
					retUpShelfMap.put(retUpShelfCode, retUpShelfCode);
				}else{
					pw.write("该退货上架单："+retUpShelfCode+"，已经扫描过！");
					return;
				}
			}
			
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("扫描成功！\r\n");
			strBuilder.append("当前扫描上架单号");
			strBuilder.append(retUpShelfCode);
			strBuilder.append(";0");
			pw.write(strBuilder.toString());
			return;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageController.cacheProductCode exception:", e);
			}
			pw.write("扫描失败！当前扫描上架单号:"+retUpShelfCode+"，请联系管理员！");
			return;
		}finally{
			wareService.releaseAll();
		}
		
	}
	
	
	/**
	 * 生成退货上架汇总单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */	
	@RequestMapping("generateRetUpShelf")
	public void generateRetUpShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		WareService wareService = new WareService();
		String upShelfCode = null;
		try{
			synchronized(lock){
				ReturnedPackageService pService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
				voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
				if(user == null){
					pw.write("用户没有登录！");
					return;
				}
				
				UserGroupBean group = user.getGroup();
				if( !group.isFlag(616)) {
					pw.write("您没有生成退货上架汇总单的权限！");
					return;
				}
				
				Map retUpShelfMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
				if(retUpShelfMap == null || retUpShelfMap.isEmpty()){
					pw.write("没有可以汇总的上架单！");
					return;
				}
				if(retUpShelfMap.size()<5){
					pw.write("汇总单中至少要包含5个退货上架单！");
					return;
				}
				if(retUpShelfMap.size()>30){
					pw.write("汇总单中最多包含30个退货上架单！");
					return;
				}
				upShelfCode = pService.generateUpShelf(retUpShelfMap, user, date);
				if(upShelfCode != null && !upShelfCode.equals("") && !upShelfCode.contains("HWTS")){
					pw.write(upShelfCode);
					return;
				}
				request.getSession().removeAttribute(RETUPSHELFCODE);
				pw.write(upShelfCode);
				return;
			}
			
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStoreageController.generateRetUpShelf exception", e);
			}
//			adminService.rollbackTransaction();
			pw.write("系统异常，请联系管理员！");
			return;
		}finally{
			wareService.releaseAll();
		}
		
	}
	
	/**
	 * 检测是否有退货上架单作业确认权限
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("checkConfirmOpAuthority")
	public void checkConfirmOpAuthority(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		PrintWriter pw = response.getWriter();  
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(618)){
			pw.write("0");
			return;
		}
		return;
	}
	
	/**
	 * 检测退货上架单确认是否有权限
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("checkRetShelfAuthority")
	public void checkRetShelfAuthority(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		UserGroupBean group = user.getGroup();
		PrintWriter pw = response.getWriter();  
		if(!group.isFlag(617)){
			pw.write("0");
			return;
		}
		return;
	}
	
	/**
	 * 审核退货上架汇总单，状态变为已审核
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("confirmRetShelf")
	public void confirmRetShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter();  
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			pw.write("用户没有登录");
			return;
		}
		
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(617)){
			pw.write("您没有权限进行此操作！");
			return;
		}
		String retShelfCode = request.getParameter("retshelfCode");
		if(retShelfCode == null || retShelfCode.equals("")){
			pw.write("2");
			return;
		}
		ReturnedPackageService service = new ReturnedPackageServiceImpl();
		try{
			pw.write(service.confirmRetShelf(retShelfCode,user));
			return;
		}catch(Exception e){
			pw.write("系统异常，请联系管理员");
			return;
		}
	}
	
	/**
	 * 退货上架作业确认,状态改为已完成
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("confirmOp")
	public void confirmOp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();  
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			pw.write("9");
			return;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(618)){
			pw.write("您没有权限进行此操作！");
			return;
		}
		
		String barCode = request.getParameter("barCode");
		if(barCode == null || barCode.equals("")){
			pw.write("0");//条码不能为空
		}
		CargoInfoModelBean cargoInfoModel = (CargoInfoModelBean) request.getSession().getAttribute(Constants.CACHECAROINO);
		WareService wareService = new WareService();
		wareService.getDbOp().startTransaction();//需要连接检查
		StatService statService = null;
		ICargoService service = null;
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService productservice = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ReturnedPackageService repService = new ReturnedPackageServiceImpl(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			
			service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			
			//判断作业单是否存在
			if(cargoInfoModel == null){
				
				//校验权限，判断汇总单下上架单是否属于用户所在部门
				boolean result = repService.checkCollectBill(request, barCode);
				if(!result){
					pw.write("您没有权限完成该退货上架汇总单！");//汇总单不存在
					return;
				}
				
				ReturnedUpShelfBean rufBean = statService.getReturnedUpShelf(
						"code='"+StringUtil.toSql(barCode)+"'");
				if(rufBean == null){
					pw.write("该汇总单不存在！");//汇总单不存在
					return;
				}
				if(rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS38){
					pw.write("-2");//作业单没有被审核
					return;
				}
				if(rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS43
						|| rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS45
						|| rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS46){
					pw.write("-1");//作业单已经完成
					return;
				}
				cargoInfoModel = new CargoInfoModelBean();
				cargoInfoModel.setRetUpShelfCode(barCode);
				List cargoOperationList = service.getCargoOperationList(
						"source='"+StringUtil.toSql(barCode)+"'"
						+" and effect_status!="+CargoOperationBean.EFFECT_STATUS4
						+" and status!="+CargoOperationProcessBean.OPERATION_STATUS7
						+" and status!="+CargoOperationProcessBean.OPERATION_STATUS9, 0, -1, null);
				StringBuilder operIds = new StringBuilder();
				CargoOperationBean coBean = null;
				if(cargoOperationList == null || cargoOperationList.isEmpty()){
					pw.write("汇总单下所有上架单已经作业结束！");
					return;
				}
				for(int i=0; i<cargoOperationList.size(); i++){
					coBean = (CargoOperationBean) cargoOperationList.get(i);
					if(operIds.length()>0){
						operIds.append(",");
					}
					operIds.append(coBean.getId());
				}
				cargoInfoModel.setCargoOperationIds(operIds.toString());
				request.getSession().setAttribute(Constants.CACHECAROINO, cargoInfoModel);
				pw.write("2");//作业单正常
				return;
			}
			
			//判断商品是否存在，并判断商品是否属于作业单,如果不是商品需要看是不是货位编号
			CargoInfoBean cargoInfo = null;
			voProduct product = null;
			ProductBarcodeVO bBean = bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(barCode)+"'");
			if(bBean == null){
				product = wareService.getProduct(StringUtil.toSql(barCode));
			}else{
				product = wareService.getProduct(bBean.getProductId());
			}
			
			if(product == null ){
				if(cargoInfoModel.getProductBeanList().isEmpty()){
					pw.write("16");//商品条码不存在
					return;
				}else{
					cargoInfo = service.getCargoInfo(
							"whole_code='" + barCode + "' and stock_type=" + CargoInfoBean.STOCKTYPE_QUALIFIED 
							+" and status=" + CargoInfoBean.STATUS0);
					if(cargoInfo == null){
						if(StringUtil.isNumeric(barCode)){
							pw.write("16");//商品条码不存在
						}else{
							pw.write("4");//货位条码不存在
						}
						return;
					}else{
						
						wareService.getDbOp().startTransaction();
						StringBuilder strPId = new StringBuilder();
						strPId.append("(");
						List retProductList = cargoInfoModel.getProductBeanList();
						voProduct rp = null;
						for(int j=0; j<retProductList.size(); j++){
							rp = (voProduct) retProductList.get(j);
							if(j>0){
								strPId.append(",");
							}
							strPId.append(rp.getId());
						}
						strPId.append(")");
						List cargoOpList = new ArrayList();
						if(strPId.length()>0){
							cargoOpList = service.getCargoOperationCargoList(
								"oper_id in("+cargoInfoModel.getCargoOperationIds() + 
								") and product_id in " + strPId.toString()+
								" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
						}
						
						rp = (voProduct) retProductList.get(0);
						CargoOperationCargoBean cocBean = null;
						if(rp.getInCargoWholeCode()==null 
								|| !rp.getInCargoWholeCode().equals(cargoInfo.getWholeCode())){
							pw.write("21");
							return;
						}
						
						
						//更新货位库存完成商品数量，减少源货位锁定量，目的货位空间锁定量，目的货位库存，减少库存锁定量
						ReturnedPackageService rpService = new ReturnedPackageServiceImpl();
						List productList = cargoInfoModel.getProductBeanList();
						List newProductList = new ArrayList();
						voProduct vproduct = null;
						voProduct temProduct = null;
						for(int i=0; i<productList.size(); i++){
							vproduct = (voProduct)productList.get(i);
							if(newProductList.isEmpty()){
								vproduct.setCount(1); 
								newProductList.add(vproduct);
							}else if(newProductList.contains(vproduct)){
								temProduct = (voProduct) newProductList.get(newProductList.indexOf(vproduct));
								temProduct.setCount(temProduct.getCount()+1);
							}else{
								newProductList.add(vproduct);
							}
						}
						cargoInfoModel.setProductBeanList(newProductList);
						//作业完成更新退货库，合格库库存信息
						String strFlag = rpService.updateStockInfo(
								cargoInfoModel, service, wareService, productservice, cargoOpList, user);
						if(strFlag != null){
							wareService.getDbOp().rollbackTransaction();
							request.getSession().removeAttribute(Constants.CACHECAROINO);
							pw.write(strFlag);
							return;
						}
						cargoOpList = service.getCargoOperationCargoList(
								"oper_id in("+cargoInfoModel.getCargoOperationIds() + 
								") and product_id in " + strPId.toString()+
								" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
					
						for(int k=0; k<cargoOpList.size(); k++){
							cocBean = (CargoOperationCargoBean) cargoOpList.get(k);
							//检测商品完成数量和库存数量是否一致，如果一致那么修改上架单状态为已完成
							if(cocBean.getCompleteCount()==cocBean.getStockCount()){
								if(!service.updateCargoOperation(
										"status=" + CargoOperationProcessBean.OPERATION_STATUS7
										+ ",complete_datetime='" + DateUtil.getNow() + "'"
										+ ",complete_user_name='" + user.getUsername() + "'"
										+ ",complete_user_id=" + user.getId(), 
										"id=" + cocBean.getOperId())){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("更新作业完成数量失败！");
									return;
								}
								CargoOperationLogBean logBean = new CargoOperationLogBean();
								logBean.setOperId(cocBean.getOperId());
								logBean.setOperDatetime(DateUtil.getNow());
								logBean.setOperAdminId(user.getId());
								logBean.setOperAdminName(user.getUsername());
								logBean.setRemark("作业单操作确认完成");
								if(!service.addCargoOperationLog(logBean)){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("添加操作记录失败！");
									return;
								}
							}else{
								CargoOperationLogBean logBean = new CargoOperationLogBean();
								logBean.setOperId(cocBean.getOperId());
								logBean.setOperDatetime(DateUtil.getNow());
								logBean.setOperAdminId(user.getId());
								logBean.setOperAdminName(user.getUsername());
								logBean.setRemark("作业单操作确认进行中");
								if(!service.addCargoOperationLog(logBean)){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("添加操作记录失败！");
									return;
								}
							}
						}
						
						//查询汇总单下是否存在时效状态不等于作业失败，并且状态为已审核的上架单
						int count = service.getCargoOperationCount(
								"source='"+cargoInfoModel.getRetUpShelfCode()+"'" +
										" and status="+CargoOperationProcessBean.OPERATION_STATUS3 +
										" and effect_status!="+CargoOperationBean.EFFECT_STATUS4);
						
						if(count==0){
								if(!statService.updateReturnedUpShelf(
										"status="+ReturnedUpShelfBean.OPERATION_STATUS46+
										",complete_datetime='"+DateUtil.getNow()+"'"+
										",complete_user_name='"+user.getUsername()+"'"+
										",complete_user_id="+user.getId(), "code='"+cargoInfoModel.getRetUpShelfCode()+"'")){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("更新汇总单失败！");
									return;
								}
						}
						request.getSession().removeAttribute(Constants.CACHECAROINO);
						pw.write("8");//作业单完成
						wareService.getDbOp().commitTransaction();
						return;
					}
				}
			}else{
				
				//查看商品是否属于上架单
				CargoOperationCargoBean cargoOper = service.getCargoOperationCargo(
						"oper_id in(" + cargoInfoModel.getCargoOperationIds() +
						") and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE +
						" and use_status="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_STATUS +
						" and product_id=" + product.getId());
				CargoOperationBean coBean = null;
				if(cargoOper == null){
					pw.write("6");//商品不属于上架单
					return;
				}else{
					coBean = service.getCargoOperation("id="+cargoOper.getOperId());
					if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS7){
						if(cargoOper.getStockCount()==cargoOper.getCompleteCount()){
							if(cargoOper.getStockCount()==0){
								
							}else{
								pw.write("24");//该商品已经作业完成
								request.getSession().removeAttribute(Constants.CACHECAROINO);
								return;
							}
						}
						if(cargoInfoModel.getProductBeanList().isEmpty()){
							product.setInCargoWholeCode(cargoOper.getInCargoWholeCode());
							cargoInfoModel.getProductBeanList().add(product);
							pw.write("11");
							return;
						}else{
							voProduct retProductBean = 
									(voProduct) cargoInfoModel.getProductBeanList().get(0);
							if(retProductBean.getInCargoWholeCode().equals(cargoOper.getInCargoWholeCode())){
								if(cargoInfoModel.getProductBeanList().size()>=(cargoOper.getStockCount()-cargoOper.getCompleteCount())){
									pw.write("22");//未上架量不足
									return;
								}
								cargoInfoModel.getProductBeanList().add(product);
								pw.write("11");
								return;
							}else{
								pw.write("7");//商品不属于货位
								return;
							}
						}
					}else{
						pw.write("24");//该商品已经作业完成
						request.getSession().removeAttribute(Constants.CACHECAROINO);
						return;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			request.getSession().removeAttribute(Constants.CACHECAROINO);
			wareService.getDbOp().rollbackTransaction();
			pw.write("系统异常，请联系管理员");
			return;
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	
	/**
	 * 清楚session中缓存的相关上架汇总单的信息 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("clearOpSession")
	public void clearOpSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute(Constants.CACHECAROINO);
		return;
	}
	
	/**
	 * 退货上架作业确认页面的取消，重新扫描
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("resetConfirm")
	public String resetConfirm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute(Constants.CACHECAROINO);
		return "forward:/admin/rec/oper/returnShelf/retShelfOpConfirm.html";
	}
	
	/**
	 * 说明：物流员工作业效率排名
	 * 
	 * 日期：2013-2-22
	 * 
	 * 作者：石远飞
	 * 
	 */
	@RequestMapping("returnedProductAppraisal")
	public String returnedProductAppraisal(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			String firstCount = "";
			String oneselfCount = "";
			String ranking ="";
			String productCount="";
			String firstProductCount="";
			int n = 1;
			CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
			if(csBean == null){
				request.setAttribute("tip", "此账号不是物流员工 !");
				request.setAttribute("result", "failure");
			}
			CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=4");
			List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=4", -1, -1, " oper_count DESC");
			if(cspBean != null){
				for(int i = 0;i < cspList.size();i++){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(i);
					if(i==0){
						firstCount = bean.getOperCount() + "";
						firstProductCount = bean.getProductCount()+"";
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
					firstCount = bean.getOperCount() + "";
					firstProductCount = bean.getProductCount()+"";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}else{
					firstCount = "0";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}
			}
			request.setAttribute("firstCount", firstCount);
			request.setAttribute("productCount", productCount);
			request.setAttribute("oneselfCount", oneselfCount);
			request.setAttribute("firstProductCount", firstProductCount);
			request.setAttribute("ranking", ranking);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		
		return "forward:/admin/rec/oper/returnShelf/returnedProductAppraisal.jsp";
	}
	
	
	/**
	 * 单个商品质检
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("appraisalReturnedProduct")
	public String appraisalReturnedProduct(HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		
		CookieUtil cu = new CookieUtil(request, response);
		String returnedProductCode = StringUtil.convertNull(request.getParameter("returnedProductCode2"));
		String appraisalNumber = StringUtil.convertNull(request.getParameter("appraisalNumber2"));
		String appraisalResult = StringUtil.convertNull(request.getParameter("appraisalResult2"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea2"));
		int appraisalNumberInt = -1;
		int appraisalResultInt = -1;
		List cargoInfoList = new ArrayList();
		String forward = "";
		try {
			if(!CargoDeptAreaService.hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN)) {
				request.setAttribute("tip", "你没有对应地区的退货库的操作权限！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			if( !appraisalNumber.equals("")) {
				appraisalNumberInt = Integer.parseInt(appraisalNumber);
			} else {
				request.setAttribute("tip", "未传入质检个数！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			if( appraisalNumberInt <= 0 ) {
				request.setAttribute("tip", "传入的质检个数有误!");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			if( wareArea < 0 ) {
				request.setAttribute("tip", "未选择操作的库地区！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			
			if( !appraisalResult.equals("")) {
				appraisalResultInt = Integer.parseInt(appraisalResult);
			}
		} catch(Exception e ) {
			request.setAttribute("tip", "所传质检结果或商品个数格式有错误!");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		WareService wareService = new WareService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnedPackageService rpService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,statService.getDbOp());
		try {
			synchronized(Constants.LOCK){
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(returnedProductCode)+"'");
				voProduct product = null;
				if( bBean == null || bBean.getBarcode() == null ) {
					product = wareService.getProduct(returnedProductCode);
				} else {
					product = wareService.getProduct(bBean.getProductId());
				}
				if( product == null ) {
					request.setAttribute("tip", "条码有误");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				//验证是否已有退货库库存
				ProductStockBean psb = service.getProductStock("stock > 0 and area = " + wareArea + " and type = " + ProductStockBean.STOCKTYPE_RETURN + " and product_id = " + product.getId());
				if( psb == null ) {
					request.setAttribute("tip", "该商品还没有入退货库库存");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				if( appraisalResultInt == 0 ) {
					String tip = "";
					if(product.getStatus() == 100 ) {
						tip += "该商品已下架";
					}
					service.getDbOp().startTransaction();
					CargoInfoBean cargoInfo = cargoService.getTargetCargoInfo(product,appraisalNumberInt,wareArea);
					if( (cargoInfo == null || cargoInfo.getWholeCode() == null) && product.getStatus() == 100 ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "该产品已下架且没有整件区滞销货位可选了");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}else if( (cargoInfo == null || cargoInfo.getWholeCode() == null) && product.getStatus() != 100 ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有散件区货位可选了");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
					service.getDbOp().startTransaction();
					boolean targetCargoAvail = rpService.dealTargetCargoAndProduct(cargoInfo, product, cargoService, service);
					
					CargoOperationCargoBean cargoOperationCargo = null;
					if( targetCargoAvail == false ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "分配的目的货位在与商品关联时失败");
						request.setAttribute("result", "failure");
						return "/admin/error";
					} else {
						//生成上架单
						cargoOperationCargo = rpService.createUpShelfBill(user, product, appraisalNumberInt, cargoInfo, wareService, service, statService, cargoService,wareArea);
						//添加退货上架临时表数据
						ReturnedProductVirtual returnedProductVirtual = new ReturnedProductVirtual();
						returnedProductVirtual.setOperId(cargoOperationCargo.getOperId());
						returnedProductVirtual.setCargoId(cargoInfo.getId());
						returnedProductVirtual.setProductId(product.getId());
						cargoService.addReturnedProductVirtual(returnedProductVirtual);
					}
					//临时用这个remark来放一下上架单编号
					service.getDbOp().commitTransaction();
					service.getDbOp().getConn().setAutoCommit(true);
					request.setAttribute("tip", tip);
					//需要传过去的信息有， cargoWholeCode， 上架单编号.
					cargoInfoList.add(cargoOperationCargo);
					request.setAttribute("cargoInfoList", cargoInfoList);
					request.setAttribute("url", request.getContextPath()+"/admin/returnedProductAppraisal.mmx");
					forward = "forward:/admin/rec/oper/returnShelf/cargoCodePrint2.jsp";
				} else if ( appraisalResultInt == 1 ) {
					forward = "forward:/admin/returnedProductAppraisal.mmx";
				} else {
					request.setAttribute("tip", "所传质检结果参数有错误");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				//报错提是 下架的东西已经放到了对应的商品列表中
				cu.setCookie("Appraisal_result_mark_" + user.getId(), appraisalResult, 60*60*24*30);
				cu.setCookie("Appraisal_area_mark_" + user.getId(), ""+wareArea, 60*60*24*30);
				//物流员工绩效考核操作
				CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
				if(csBean == null){
					request.setAttribute("tip", "此账号不是物流员工 !");
					request.setAttribute("result", "failure");
				}
        		CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=4 and staff_id=" + csBean.getId() );
        		int operCount = 1;
        		int productCount = appraisalNumberInt;
				if(cspBean != null){
					operCount = operCount + cspBean.getOperCount();
					productCount = productCount + cspBean.getProductCount();
					boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + productCount, " id=" + cspBean.getId());
					if(!flag){
						request.setAttribute("tip", "物流员工绩效考核更新操作失败 !");
						request.setAttribute("result", "failure");
					}
				}else{
					CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
					newBean.setDate(date);
					newBean.setProductCount(productCount);
					newBean.setOperCount(operCount);
					newBean.setStaffId(csBean.getId());
					newBean.setType(4);  //4代表退货上架作业
					boolean flag = cargoService.addCargoStaffPerformance(newBean);
					if(!flag){
						request.setAttribute("tip", "物流员工绩效考核添加操作失败 !");
						request.setAttribute("result", "failure");
					}
				}
				service.getDbOp().commitTransaction();
			}
		} catch(Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.appraisalReturnedProduct exception", e);
			}
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", e.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		}finally {
			statService.releaseAll();
		}
		return forward;
	}
	
	/**
	 * 展示退货上架汇总单列表
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("retShelfList")
	@ResponseBody
	public Map<String,Object> retShelfList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,String>> resultList= new ArrayList<Map<String,String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			resultMap.put("tip", "当前没有登陆，操作失败！");
			return resultMap;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ReturnedPackageService retService = new ReturnedPackageServiceImpl(BaseServiceImpl.CONN_IN_SERVICE,wareService.getDbOp());
		try{
			String cargoOpCode = request.getParameter("cargoOperationCode");//汇总单号
			String[] cargoOpStatus = request.getParameterValues("cargoOpStatus");
			String cargoCode = request.getParameter("cargoCode");
			String productCode = request.getParameter("productCode");
			String createUser = request.getParameter("createUser");
			int pageIndex=0;
			if(request.getParameter("page")!=null){
				pageIndex = StringUtil.parstInt(request.getParameter("page"));
				pageIndex = pageIndex - 1;
			}
			int countPerPage= StringUtil.parstInt(StringUtil.convertNull(request.getParameter("rows")));
			
			int count = 0;
			count = retService.getRetShelfCount(cargoOpStatus,cargoOpCode,cargoCode,productCode,createUser);
			
			if(count == 0){
				request.setAttribute("cargoOperationList", new ArrayList());
				request.setAttribute("cargoOperationCode", cargoOpCode);
				request.setAttribute("cargoOpStatus", cargoOpStatus);
				request.setAttribute("productCode", productCode);
				request.setAttribute("cargoCode", cargoCode);
				request.setAttribute("createUser", createUser);
				return resultMap;
			}
			
			List retUpShelfList = retService.getRetShelfList(cargoOpStatus,cargoOpCode,cargoCode,productCode, pageIndex*countPerPage, countPerPage, createUser, "create_datetime desc");
			assignStatusName(retUpShelfList, null);
			int x = retUpShelfList.size();
			for( int i = 0 ; i < x; i++ ) {
				ReturnedUpShelfBean rusBean = (ReturnedUpShelfBean) retUpShelfList.get(i);
				Map<String, String> map = new HashMap<String,String>();
				map.put("count", new Integer(i+1).toString());
				map.put("code", "<a href='../admin/showRetShelf.mmx?upShelfCode="+rusBean.getCode()+"'>"+rusBean.getCode()+"</a>");
				map.put("create_datetime", rusBean.getCreateDatetime());
				map.put("create_username", rusBean.getCreateUserName());
				map.put("complete_datetime", StringUtil.convertNull(rusBean.getCompleteDatetime()));
				map.put("operation_username", StringUtil.convertNull(rusBean.getCompleteUsername()));
				map.put("ware_num", rusBean.getPassageWholeCode());
				map.put("status", rusBean.getStatusName());
				map.put("sum", new Integer(rusBean.getProductCount()).toString());
				map.put("return_shelf_code", rusBean.getCode());
				String management = "<a href='../admin/showRetShelf.mmx?upShelfCode="+rusBean.getCode()+"'>编辑</a><br/>";
				if( rusBean.getStatus() == 37 ) {
					
				}
				management += "<a href='../admin/printRetShelf.mmx?upShelfCode=" + rusBean.getCode() + "' target='_black'>打印汇总单</a>";
				
				map.put("management", management);
				resultList.add(map);
			}
			resultMap.put("total", count);
			resultMap.put("rows", resultList);
			return resultMap;
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			request.setAttribute("result", "failure");
			e.printStackTrace();
			return resultMap;
		}finally{
			wareService.releaseAll();
		}
	}
	
	
	/**
	 * 展示退货上架汇总单列表
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("toRetShelfList")
	public String toRetShelfList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "forward:/admin/rec/oper/returnShelf/retShelfList.htm";
	}
	
	
	private void assignStatusName(List upShelfList, ICargoService service) {
		
		ReturnedUpShelfBean rufBean = null;
		for(int i=0; i<upShelfList.size(); i++){
			rufBean = (ReturnedUpShelfBean) upShelfList.get(i);
			rufBean.setStatusName(ReturnedUpShelfBean.statusMap.get(Integer.valueOf(rufBean.getStatus()))+"");
			if(rufBean.getCreateDatetime() != null){
				rufBean.setCreateDatetime(rufBean.getCreateDatetime().substring(0,19));
			}
			if(rufBean.getCompleteDatetime() != null){
				rufBean.setCompleteDatetime(rufBean.getCompleteDatetime().substring(0,19));
			}
			if(rufBean.getAuditingDatetime() != null){
				rufBean.setAuditingDatetime(rufBean.getAuditingDatetime().substring(0,19));
			}
		}
	}
	
	/**
	 * 展示退货汇总上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("showRetShelf")
	public String showRetShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null){
				request.setAttribute("tip", "用户没有登录");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			String filteredProductCode = request.getParameter("filteredProductCode");
			if(filteredProductCode == null || filteredProductCode.equals("")){
				request.setAttribute("filteredProductCode", "");
			}else{
				request.setAttribute("filteredProductCode", filteredProductCode);
			}
			
			String editFlag = request.getParameter("editFlag");
			if(editFlag == null || editFlag.equals("")){
				request.setAttribute("editFlag", "");
			}else{
				request.setAttribute("editFlag", editFlag);
			}
			
			UserGroupBean group = user.getGroup();
			
			
//			int cargoOperationId = 0;
//			if(request.getParameter("cargoOperationId")!=null 
//					&& !request.getParameter("cargoOperationId").equals("")){
//				cargoOperationId = Integer.valueOf(request.getParameter("cargoOperationId")).intValue();
//			}
//			if(cargoOperationId <= 0){
//				request.setAttribute("tip", "生成退货上架单Id不能为空");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
			if(request.getParameter("upShelfCode")==null 
					|| request.getParameter("upShelfCode").equals("")){
				request.setAttribute("tip", "生成退货汇总上架单编号不能为空");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			
			ReturnedPackageService retPService = new ReturnedPackageServiceImpl();
			CargoInfoModelBean cargoModelBean = retPService.getCargInfoModelByUpShelfCode(request.getParameter("upShelfCode"));
//			CargoInfoModelBean cargoModelBean = retPService.getCargInfoModelById(cargoOperationId);
			if(cargoModelBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS38){
				if(!group.isFlag(617)){
					request.setAttribute("showAuditButton", "0");
				}else{
					request.setAttribute("showAuditButton", "1");
				}
			}else if(cargoModelBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS39){
				if(!group.isFlag(620)){
					request.setAttribute("showAuditButton", "0");
				}else{
					request.setAttribute("showAuditButton", "1");
				}
			}else if(cargoModelBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS37){
				request.setAttribute("showAuditButton", "1");
			}else{
				request.setAttribute("showAuditButton", "0");
			}
			request.setAttribute("cargoModelBean", cargoModelBean);
			List operationCodeList = cargoModelBean.getPassageCode();
			if(operationCodeList == null || operationCodeList.isEmpty()){
				request.setAttribute("tip", "该汇总单下没有上架单，请联系管理员");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
//			initRetShelfPageInfo(request, cargoModelBean, operationCodeList);
			request.setAttribute("cargoModelBean", cargoModelBean);
			return "forward:/admin/rec/oper/returnShelf/retShelf.htm";
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			request.setAttribute("result", "failure");
			e.printStackTrace();
			return "/admin/error";
		}
	}
	
	/**
	 * 强制作业完成，将未完成上架单修改为作业失败，失效状态为失败
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("completeRetShelf")
	public void completeRetShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter();  
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			pw.write("用户没有登录");
			return;
		}
		
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(618)){
			pw.write("您没有权限进行此操作！");
			return;
		}
		
		
		String upShelfCode = request.getParameter("upShelfCode");
		if(upShelfCode == null || upShelfCode.equals("")){
			pw.write("该退货上架汇总单编号不能为空！");
			return;
		}
		
		
		WareService wareService = new WareService();
		
		StatService statService = null;
		try{
			
			
			statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ReturnedPackageService retService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			
			wareService.getDbOp().startTransaction();
			ReturnedUpShelfBean rufBean = statService.getReturnedUpShelf("code='"+StringUtil.toSql(upShelfCode)+"'");
			if(rufBean == null){
				pw.write("该汇总单不存在！");
				return;
			}
			
			//完成退货上架单
			String result = retService.completeRetShelf(rufBean, user, wareService);
			if(result != null){
				wareService.getDbOp().rollbackTransaction();
				pw.write(result);
				return;
			}
			wareService.getDbOp().commitTransaction();
			pw.write("8");//作业单完成
		}catch(Exception e){
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
			pw.write("系统异常，请联系管理员");
			return;
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
		return;
	}
		
	/**
	 * 打印退货上架单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("printRetShelf")
	public String printRetShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String upShelfCode = request.getParameter("upShelfCode");
		if(upShelfCode == null || upShelfCode.equals("")){
			request.setAttribute("tip", "退货上架汇总单编号不能为空！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnedPackageService retPService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			CargoInfoModelBean cargoModelBean = null;
			synchronized(lock){
				cargoModelBean = retPService.getCargInfoModelByUpShelfCode(upShelfCode);
				if(cargoModelBean == null){
					request.setAttribute("tip", "退货上架汇总单不存在！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
			}
			List passageCodeList = cargoModelBean.getPassageCode();
			String formatPassCode = formatPassCode(passageCodeList);
			cargoModelBean.setPstrCode(formatPassCode);
			
			List operationCodeList = cargoModelBean.getPassageCode();
			if(operationCodeList == null || operationCodeList.isEmpty()){
				return "/admin/error";
			}
			
//				initRetShelfPageInfo(request, cargoModelBean, operationCodeList);
			request.setAttribute("cargoModelBean", cargoModelBean);
			
			return "forward:/admin/rec/oper/returnShelf/printRetShelf.htm";
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}finally{
			service.releaseAll();
		}
	}
	
	/**
	 * printRetShelf 用到的一个方法
	 * @param passageCodeList
	 * @return
	 */
	private String formatPassCode(List passageCodeList) {
		StringBuilder strBuilder = new StringBuilder();
		for(int i=0; i<passageCodeList.size();i++){
			if(strBuilder.length()>0){
				strBuilder.append("-");
			}
			strBuilder.append(passageCodeList.get(i));
		}
		return strBuilder.toString();
	}
	
	/**
	 * 确认编辑退货上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("confirmEditShelf")
	public String confirmEditShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		WareService wareService = new WareService();
		try{
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			StatService statSevice = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null){
				request.setAttribute("tip", "用户没有登录");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			
//			int cargoOperationId = StringUtil.parstInt(request.getParameter("cargoOperationId"));
			String upShelfCode = request.getParameter("upShelfCode");
			if(upShelfCode == null || upShelfCode.equals("")){
				request.setAttribute("tip", "作业单号不能为空！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			
			ReturnedUpShelfBean rufBean = statSevice.getReturnedUpShelf(
					"code='"+StringUtil.toSql(upShelfCode)+"'");
			if(rufBean == null){
				request.setAttribute("tip", "该汇总单不存在！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			
			
			List cargoOperationList = cargoService.getCargoOperationList(
					"source='"+StringUtil.toSql(upShelfCode)+"' and status="+CargoOperationProcessBean.OPERATION_STATUS1, 0, -1, null);
		
			wareService.getDbOp().startTransaction();
			CargoOperationBean coBean = null;
			for(int i=0; i<cargoOperationList.size(); i++){
				coBean = (CargoOperationBean) cargoOperationList.get(i);
				List cocList = cargoService.getCargoOperationCargoList(
									"oper_id="+coBean.getId()+" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, 0, -1, null);
				if(cocList ==null || cocList.isEmpty()){
					request.setAttribute("tip", "该任务下没有任务详细信息");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				CargoOperationCargoBean  cocBean =  null; 
				int tempValue = -1;
				voProduct product = null;
				int tempCount = 0;
				for(int j=0; j<cocList.size(); j++){
					cocBean = (CargoOperationCargoBean) cocList.get(j);
					tempCount = StringUtil.parstInt(request.getParameter("pcount_"+cocBean.getId()));
					tempValue = getMinStockCount(cargoService, statSevice,
							psService, cocBean);
					product = wareService.getProduct(cocBean.getProductId());
					if(tempCount>tempValue){
						request.setAttribute("tip", "产品："+product.getCode()+"，编辑数量不能大于货位库存量或库存量！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return "/admin/error";
					}
					if(!cargoService.updateCargoOperationCargo("stock_count="+tempCount, "oper_id="+cocBean.getOperId())){
						request.setAttribute("tip", "更新退货任务库存失败！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return "/admin/error";
					}
					
					CargoOperationLogBean logBean=new CargoOperationLogBean();
					logBean.setOperId(coBean.getId());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark = new StringBuilder("编辑商品："+cocBean.getProductId());
					logRemark.append(wareService.getProductCode(cocBean.getProductId()+""));
					logRemark.append("，");
					logRemark.append("源货位（");
					logRemark.append(cocBean.getOutCargoWholeCode());
					logRemark.append("）");
					logRemark.append("，目的货位（");
					logRemark.append(cocBean.getInCargoWholeCode());
					logRemark.append("），");
					logRemark.append("上架量（");
					logRemark.append(tempCount);
					logRemark.append("）");
					logBean.setRemark(logRemark.toString());
					if(!cargoService.addCargoOperationLog(logBean)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "添加人员操作记录失败！");
						return "/admin/error";
					}
				}
			}
			wareService.getDbOp().commitTransaction();
			return "forward:/admin/showRetShelf.mmx?upShelfCode=" + rufBean.getCode() + "&editFlag=1"; 
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			return "/admin/error";
		}finally{
			wareService.releaseAll();
		}
	}
	/**
	 * confirmEditShelf用到的一个方法
	 * @param cargoService
	 * @param statSevice
	 * @param psService
	 * @param cocBean
	 * @return
	 */
	private int getMinStockCount(ICargoService cargoService,
			StatService statSevice, IProductStockService psService,
			CargoOperationCargoBean cocBean) {
		CargoProductStockBean cpsBean;
		ProductStockBean psBean;
		int tempValue;
		cpsBean = cargoService.getCargoAndProductStock(
				"ci.stock_type=4 and ci.area_id=3 and cps.product_id="+cocBean.getProductId());
		psBean = psService.getProductStock("product_id="+cocBean.getProductId()+" and area=3 and type=4");
		tempValue = cpsBean.getStockCount();
		if(tempValue>psBean.getStock()){
			tempValue=psBean.getStock();
		}
		return tempValue;
	}
	
	
	/**
	 * 确认提交退货上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("confirmSubmitShelf")
	public String confirmSubmitShelf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		WareService wareService = new WareService();
		try{
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ReturnedPackageService retService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			StatService statSevice = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			if(user == null){
				request.setAttribute("tip", "用户没有登录");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
//			int cargoOperationId = StringUtil.parstInt(request.getParameter("cargoOperationId"));
			String upShelfCode = request.getParameter("upShelfCode");
			if(upShelfCode == null || upShelfCode.equals("")){
				request.setAttribute("tip", "作业单号不能为空！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			
			ReturnedUpShelfBean rufBean = statSevice.getReturnedUpShelf(
					"code='"+StringUtil.toSql(upShelfCode)+"'");
			if(rufBean == null){
				request.setAttribute("tip", "该汇总单不存在！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			List cargoOperationList = cargoService.getCargoOperationList(
					"source='"+StringUtil.toSql(upShelfCode)+"' and status="+CargoOperationProcessBean.OPERATION_STATUS1, 0, -1, null);
		
			wareService.getDbOp().startTransaction();
			CargoOperationBean coBean = null;
			for(int i=0; i<cargoOperationList.size(); i++){
				coBean = (CargoOperationBean) cargoOperationList.get(i);
				List cocList = cargoService.getCargoOperationCargoList(
						"oper_id="+coBean.getId()+" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, 0, -1, null);
				if(cocList ==null || cocList.isEmpty()){
					request.setAttribute("tip", "该任务下没有任务详细信息");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				CargoOperationCargoBean  cocBean =  null; 
				CargoInfoModelBean comBean = null;
				CargoInfoBean ciBean = null;
				int tempValue = 0;
				int tempCount = 0;
				voProduct product = null;
				for(int j=0; j<cocList.size(); j++){
					cocBean = (CargoOperationCargoBean) cocList.get(j);
					tempCount = StringUtil.parstInt(request.getParameter("pcount_"+cocBean.getId()));
					//获取合格量，退货库库存，货位库存最小值
					tempValue = getMinStockCount(cargoService, statSevice, psService, cocBean);
					if(tempCount>tempValue){
						product = wareService.getProduct(cocBean.getProductId());
						request.setAttribute("tip", "产品："+product.getCode()+"，编辑数量不能大于货位库存量或库存量！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return "/admin/error";
					}
					if(!cargoService.updateCargoOperationCargo("stock_count="+tempCount, "oper_id="+cocBean.getOperId())){
						request.setAttribute("tip", "更新退货任务库存失败！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return "/admin/error";
					}
					ciBean = cargoService.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
					comBean = new CargoInfoModelBean();
					comBean.setCount(tempCount);
					comBean.setProductId(cocBean.getProductId());
					comBean.setId(ciBean.getId());
					comBean.setCargoStockId(cocBean.getOutCargoProductStockId());
					List preList = new ArrayList();
					preList.add(comBean);
					
//					//更新退货商品表合格商品数
					
					//更新退货库库存
					if(!retService.lockProductStock(retService.getDbOp(), preList, null, null)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "更新退货商品表失败，库存锁定量不足！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
		
					//更新退货(源货位)货位库存
					if(!retService.lockCargoProductStock(preList, null, null, cargoService)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "更新源货位库存失败，库存锁定量不足！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
					
					//更新目标货位空间锁定量
//					if(!retService.lockCargoProductSpaceStock(preList, null, null, cargoService)){
//						adminService.rollbackTransaction();
//						request.setAttribute("tip", "更新目标货位空间锁定量失败，空间锁定量不足！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
//					}
					
					CargoOperationLogBean logBean=new CargoOperationLogBean();
					logBean.setOperId(coBean.getId());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark = new StringBuilder("编辑商品："+cocBean.getProductId());
					logRemark.append(wareService.getProductCode(cocBean.getProductId()+""));
					logRemark.append("，");
					logRemark.append("源货位（");
					logRemark.append(cocBean.getOutCargoWholeCode());
					logRemark.append("）");
					logRemark.append("，目的货位（");
					logRemark.append(cocBean.getInCargoWholeCode());
					logRemark.append("），");
					logRemark.append("上架量（");
					logRemark.append(tempCount);
					logRemark.append("）");
					logBean.setRemark(logRemark.toString());
					if(!cargoService.addCargoOperationLog(logBean)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "添加人员操作记录失败！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
				}
				//更新退货任务状态为已提交
				if(!cargoService.updateCargoOperation(
						"status="+CargoOperationProcessBean.OPERATION_STATUS2
						+",confirm_datetime='"+DateUtil.getNow()+"'"
						+",confirm_user_name='"+user.getUsername()+"'"
						+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+coBean.getId())){
					wareService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "更新退货上架单状态失败！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
			}
			if(!statSevice.updateReturnedUpShelf(
					"status="+ReturnedUpShelfBean.OPERATION_STATUS38
					+",confirm_datetime='"+DateUtil.getNow()+"'"
					+",confirm_user_name='"+user.getUsername()+"'", "id="+rufBean.getId())){
				wareService.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "更新汇总单状态失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			
			wareService.getDbOp().commitTransaction();
			ActionForward actionForward = new ActionForward(); 
			actionForward.setPath("/admin/showRetShelfAction.do?method=showRetShelf&upShelfCode="+rufBean.getCode()); 
			return "forward:/admin/showRetShelf.mmx?upShelfCode="+rufBean.getCode(); 
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			return "/admin/error";
		}finally{
			wareService.releaseAll();
		}
	}
	
	/**
	 * 作业失败
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("rollbackRetShelf")
	public void rollbackRetShelf( HttpServletRequest request, HttpServletResponse response) throws Exception {
		WareService wareService = new WareService();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		ReturnedPackageService service = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			if(user == null){
				pw.write("用户没有登录！");
				return;
			}
			
			String upShelfCode = request.getParameter("upShelfCode");
			if(upShelfCode == null || upShelfCode.equals("")){
				pw.write("该退货上架汇总单编号不能为空！");
				return;
			}
			
			String flag = service.rollbackRetShelf(upShelfCode, user, wareService);
			pw.write(flag);
			return;
		}catch(Exception e){
			e.printStackTrace();
			pw.write("系统异常，请联系管理员！");
			return;
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	
}
