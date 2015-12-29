package mmb.stock.cargo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.stat.AbnormalCargoCheckBean;
import mmb.stock.stat.AbnormalCargoCheckProductBean;
import mmb.stock.stat.BuyStockinUpshelfBean;
import mmb.stock.stat.CheckStockinMissionService;
import mmb.stock.stat.ProductWarePropertyBean;
import mmb.stock.stat.ReturnedPackageService;
import mmb.stock.stat.ReturnedPackageServiceImpl;
import mmb.stock.stat.SortingAbnormalBean;
import mmb.stock.stat.SortingAbnormalDisposeService;
import mmb.stock.stat.SortingAbnormalProductBean;
import mmb.stock.stat.SortingAgainBean;
import mmb.stock.stat.SortingBatchBean;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingBatchOrderProductBean;
import mmb.stock.stat.SortingInfoService;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.MailingBatchBean;
import adultadmin.bean.stock.MailingBatchPackageBean;
import adultadmin.bean.stock.MailingBatchParcelBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;


public class StockOperationAction  extends DispatchAction{
	public static byte[] cargoLock = new byte[0];
	private String date = DateUtil.formatDate(new Date());
	/**
	 * 移动设备功能列表页面
	 */
	public ActionForward pdaOperation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		request.getSession().removeAttribute("area");//选择的仓库，芳村1，增城3
		request.getSession().removeAttribute("operType");//本地还是跨仓，本地1，跨仓2
		request.getSession().removeAttribute("storeType");//散件区还是整件区,散件0，整件1，混合区4
		request.getSession().removeAttribute("type");//作业单类型，3是调拨单
		
		return mapping.findForward("pdaOperation");
	}
	/**
	 * 注销
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward logout(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		request.getSession().removeAttribute("userView");
		
		return mapping.findForward("soLogin");
	}
	/**
	 * 选择了地区，到仓内作业选择页面
	 */
	public ActionForward stockOperation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		//清除补货Session
		request.getSession().removeAttribute("refillCode");
		request.getSession().removeAttribute("refillCargoCount");
		request.getSession().removeAttribute("refillCargo");
		request.getSession().removeAttribute("refillProductList");
		request.getSession().removeAttribute("productCount");
		request.getSession().removeAttribute("product");
		
		String area=request.getParameter("area");
		if(area != null && !"".equals(area)){
			request.getSession().setAttribute("area", area);
		}
		String toPage=request.getParameter("toPage");
		if(toPage.equals("cangneizuoye")){
			return mapping.findForward("cangneizuoye");
		}
		if(toPage.equals("zhuangxiangguanli")){
			return mapping.findForward("zhuangxiangguanli");
		}
		if(toPage.equals("zuoyejiaojie")){
			return mapping.findForward("zuoyejiaojie");
		}
		if(toPage.equals("huoweiyichang")){
			String flag = StringUtil.convertNull(request.getParameter("flag"));
			if("sortingAgain".equals(flag)){
				request.getSession().removeAttribute("osBean");
				request.getSession().removeAttribute("cargoInfo");
				request.getSession().removeAttribute("saBeanList");
			}
			if( "forDelete".equals(flag) ) {
				request.getSession().removeAttribute("osBean");
				request.getSession().removeAttribute("cargoInfo");
				request.getSession().removeAttribute("saBeanList");
				request.getSession().removeAttribute("EXSortingAbnormalInfo");
				request.getSession().removeAttribute("EXOrderStockInfo");
				request.getSession().removeAttribute("EXCurrentCargoInfos");
			}
			return mapping.findForward("huoweiyichang");
		}
		if(toPage.equals("dingdanchuku")){
			return mapping.findForward("dingdanchuku");
		}
		if(toPage.equals("shangpinchaxun")){
			return mapping.findForward("shangpinchaxun");
		}
		return null;
	}
	/**
	 * 装箱管理主页
	 */
	public ActionForward operationPageJump(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String toPage = StringUtil.convertNull(request.getParameter("toPage")).trim();
		if(toPage.equals("createQualityCartonning")){
			return mapping.findForward("soCreateQualityCartonning");
		}
		if(toPage.equals("creatCartonning")){
			return mapping.findForward("soCreateCartonning");
		}
		if(toPage.equals("inventoryAbnormalCargo")){
			String inventoryFlag = StringUtil.convertNull(request.getParameter("inventoryFlag"));
			UserGroupBean group = user.getGroup();
			if(inventoryFlag != null && !"".equals(inventoryFlag)){
				if("1".equals(inventoryFlag)){
					if(!group.isFlag(778)){
						request.setAttribute("error", "对不起,你没有货位异常-盘点-初盘权限!");
						return mapping.findForward("soInventoryChoose");
					}
				}else if("2".equals(inventoryFlag)){
					if(!group.isFlag(779)){
						request.setAttribute("error", "对不起,你没有货位异常-盘点-二盘权限!");
						return mapping.findForward("soInventoryChoose");
					}
				}else if("3".equals(inventoryFlag)){
					if(!group.isFlag(780)){
						request.setAttribute("error", "对不起,你没有货位异常-盘点-终盘权限!");
						return mapping.findForward("soInventoryChoose");
					}
				}else{
					request.setAttribute("error", "没有获取到盘点阶段被选择的标识!");
					return mapping.findForward("soInventoryChoose");
				}
				request.getSession().setAttribute("inventoryFlag", inventoryFlag);
			}
			return mapping.findForward("soInventoryAbnormalCargoList");
		}
		if(toPage.equals("dropCartonning")){
			return mapping.findForward("soDropCartonning");
		}
		if(toPage.equals("inventoryChoose")){
			request.getSession().removeAttribute("inventoryFlag");
			return mapping.findForward("soInventoryChoose");
		}
		if(toPage.equals("upProduct")){
			return mapping.findForward("soUpProduct");
		}
		if(toPage.equals("downProduct")){
			return mapping.findForward("soDownProduct");
		}
		if(toPage.equals("addProduct")){
			return mapping.findForward("soAddProduct");
		}
		if(toPage.equals("deployProduct")){
			return mapping.findForward("soDeployProduct");
		}
		if(toPage.equals("printCartonning")){
			return mapping.findForward("soPrintCartonning");
		}
		if(toPage.equals("deployCross")){
			request.getSession().setAttribute("operationType", "KC");
			return mapping.findForward("soDeployOperationArea");
		}
		if(toPage.equals("deployLocal")){
			request.getSession().setAttribute("operationType", "BD");
			return mapping.findForward("soDeployOperationArea");
		}
		if(toPage.equals("abnormalProductHadle")){
			return mapping.findForward("soAbnormalProductHandle");
		}
		if(toPage.equals("operationAudit")){
			return mapping.findForward("soOperationAudit");
		}
		if(toPage.equals("operationComplete")){
			return mapping.findForward("soOperationComplete");
		}
		if(toPage.equals("soConfirmComplete")){
			request.getSession().removeAttribute(Constants.CACHECAROINO);
			request.getSession().removeAttribute("coBean");
			return mapping.findForward("soConfirmComplete");
		}
		if(toPage.equals("soGenerateRetShelf")){
			if(request.getSession().getAttribute("productCodeMap")!=null){
				request.getSession().removeAttribute("productCodeMap");
			}
			return mapping.findForward("soGenerateRetShelf");
		}
		if(toPage.equals("deployOperationScattered")){
			request.getSession().setAttribute("operationArea", "SJ");
			return mapping.findForward("soCreateDeploy");
		}
		if(toPage.equals("deployOperationMix")){
			request.getSession().setAttribute("operationArea", "HH");
			return mapping.findForward("soCreateDeploy");
		}
		if(toPage.equals("generateWholeArea")) {
			return mapping.findForward("soGenerateWholeArea");
		}
		//再次分拣
		if(toPage.equals("sortingAgain")) {
			request.getSession().removeAttribute("osBean");
			request.getSession().removeAttribute("cargoInfo");
			request.getSession().removeAttribute("saBeanList");
			return mapping.findForward("soSortingAgain");
		}
		if(toPage.equals("dealDeleteOS")) {
			request.getSession().removeAttribute("EXSortingAbnormalInfo");
			request.getSession().removeAttribute("EXOrderStockInfo");
			request.getSession().removeAttribute("EXCurrentCargoInfos");
			request.getSession().removeAttribute("osBean");
			request.getSession().removeAttribute("cargoInfo");
			request.getSession().removeAttribute("saBeanList");
			return mapping.findForward("soDealDeleteOS");
		}
		if(toPage.equals("deployOperationWhole")){
			request.getSession().setAttribute("operationArea", "ZJ");
			return mapping.findForward("soCreateDeploy");
		}else{
			return null;
		}
	}
	/**
	 * PDA波次里的邮包列表
	 */
	public ActionForward batchParcelList (ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String batchCode = StringUtil.convertNull(request.getParameter("batchCode"));
		int area = StringUtil.toInt((String)request.getSession().getAttribute("area"));
		try {
			MailingBatchBean mbBean = service.getMailingBatch(" code='" + StringUtil.toSql(batchCode) + "'");
			if(mbBean == null){
				request.setAttribute("result", "未找到此发货波次！");
				return mapping.findForward("dingdanchuku");
			}
			if(area != mbBean.getArea()){
				request.setAttribute("result", "发货波次与选择的仓库不一致！");
				return mapping.findForward("dingdanchuku");
			}
			if(mbBean.getStatus()==1){
				request.setAttribute("result", "发货波次已出库！");
				return mapping.findForward("dingdanchuku");
			}
			if(mbBean.getStatus()==2){
				request.setAttribute("result", "发货波次已完成交接！");
				return mapping.findForward("dingdanchuku");
			}
			request.getSession().setAttribute("mbBean", mbBean);
			ArrayList MBPlist = service.getMailingBatchParcelList(" mailing_batch_code='" + batchCode +"'", -1, -1,"id DESC");
			for(int i=0;i<MBPlist.size();i++){
				MailingBatchParcelBean pBean=(MailingBatchParcelBean)MBPlist.get(i);
				pBean.setPackageCount(service.getMailingBatchPackageCount("mailing_batch_parcel_id="+pBean.getId()));
			}
			request.setAttribute("batchCode", batchCode);
			request.setAttribute("MBPlist", MBPlist);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("soBatchParcelList");
	}
	/**
	 * PDA创建邮包
	 */
	public ActionForward createParcel (ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String batchCode = StringUtil.convertNull(request.getParameter("batchCode"));
		int area = StringUtil.toInt((String)request.getSession().getAttribute("area"));
		String parcelCode = "";//邮包code
		try{
			MailingBatchBean MBbean = service.getMailingBatch(" code='" + StringUtil.toSql(batchCode) + "'");
			if(MBbean == null){
				request.setAttribute("result", "未找到此发货波次！");
				return mapping.findForward("dingdanchuku");
			}
			if(area != MBbean.getArea()){
				request.setAttribute("result", "发货波次与选择的仓库不一致！");
				return mapping.findForward("dingdanchuku");
			}
			if(MBbean.getStatus()==1){
				request.setAttribute("result", "发货波次已出库！");
				return mapping.findForward("dingdanchuku");
			}
			if(MBbean.getStatus()==2){
				request.setAttribute("result", "发货波次已完成交接！");
				return mapping.findForward("dingdanchuku");
			}
			MailingBatchParcelBean parcel = new MailingBatchParcelBean();
			parcel.setMailingBatchId(MBbean.getId());
			parcel.setMailingBatchCode(MBbean.getCode());
			
			List parcelList=service.getMailingBatchParcelList("mailing_batch_code='"+ StringUtil.toSql(batchCode) + "'", -1, -1, "id desc");
			if(parcelList.size()>0){
				MailingBatchParcelBean parcelBean=(MailingBatchParcelBean)parcelList.get(0);
				if(parcelBean.getCode().substring(14,16).equals("99")){
					request.setAttribute("result", "邮包最多添加99个！");
					return new ActionForward("/admin/stockOperation.do?method=batchParcelList&batchCode=" + batchCode);
				}
			}
			if(parcelList.size()==0){
				parcel.setCode(MBbean.getCode()+"01");
			}else{
				MailingBatchParcelBean firstParcel=(MailingBatchParcelBean)parcelList.get(0);
				String code = firstParcel.getCode();
				int parcelNum=Integer.parseInt(code.substring(code.length()-2,code.length()))+1;
				parcel.setCode(MBbean.getCode()+(parcelNum<10?"0":"")+parcelNum);
			}
			service.addMailingBatchParcel(parcel);
			parcelCode = parcel.getCode();
			request.setAttribute("parcelCode", parcelCode);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("soAddParcel");
	}
	/**
	 * PDA往邮包里添加包裹 
	 */
	public ActionForward addParcel (ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		DbOperation dbOpSlave = new DbOperation();
		dbOp.init("adult");
		dbOpSlave.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IStockService stockSlaveService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
		String parcelCode = StringUtil.convertNull(request.getParameter("parcelCode"));//发货邮包code
		String packageCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("packageCode")));//包裹单编号
		try{
			AuditPackageBean apBean = stockSlaveService.getAuditPackage("package_code='" + packageCode+"'");
			if(apBean==null){
				request.setAttribute("result", "包裹信息错误！");
				request.setAttribute("color", "red");
				return mapping.findForward("soAddParcel");
			}
			
			MailingBatchParcelBean mbpBean=stockService.getMailingBatchParcel("code='" + parcelCode + "'");
			if(mbpBean==null){
				request.setAttribute("result", "该发货邮包不存在！");
				request.setAttribute("color", "red");
				return mapping.findForward("soAddParcel");
			}
			
			MailingBatchBean MBbean = stockService.getMailingBatch("id="+mbpBean.getMailingBatchId());
			int area = StringUtil.toInt((String)request.getSession().getAttribute("area"));
			if(MBbean == null){
				request.setAttribute("result", "未找到此发货波次！");
				request.setAttribute("color", "red");
				return mapping.findForward("dingdanchuku");
			}
			if(area != MBbean.getArea()){
				request.setAttribute("result", "发货波次的仓库与选择的仓库不一致！");
				request.setAttribute("color", "red");
				return mapping.findForward("dingdanchuku");
			}
			
			if(MBbean.getStatus()==1){
				request.setAttribute("result", "发货波次已出库！");
				request.setAttribute("color", "red");
				return mapping.findForward("dingdanchuku");
			}
			if(MBbean.getStatus()==2){
				request.setAttribute("result", "发货波次已完成交接！");
				request.setAttribute("color", "red");
				return mapping.findForward("dingdanchuku");
			}
			voOrder order=wareService.getOrder("code='" + apBean.getOrderCode() +"'");
			if(order==null){
				request.setAttribute("result", "无此订单！");
				request.setAttribute("color", "red");
				return mapping.findForward("soAddParcel");
			}
			
			if(order.getDeliver() != MBbean.getDeliver()){
				request.setAttribute("result", "该订单快递公司与波次物流不匹配！");
				request.setAttribute("color", "red");
				return mapping.findForward("soAddParcel");
			}
			int packageCount=stockService.getMailingBatchPackageCount("order_code='"+apBean.getOrderCode()+"' and package_code='"+packageCode+"' and balance_status<>3");
			if(packageCount!=0){
				request.setAttribute("result", "该包裹已被添加！");
				request.setAttribute("color", "red");
				return mapping.findForward("soAddParcel");
			}
			MailingBatchPackageBean packageBean=new MailingBatchPackageBean();
			packageBean.setMailingBatchId(MBbean.getId());
			packageBean.setMailingBatchCode(MBbean.getCode());
			packageBean.setMailingBatchParcelId(mbpBean.getId());
			packageBean.setMailingBatchParcelCode(mbpBean.getCode());
			packageBean.setOrderCode(apBean.getOrderCode() );
			packageBean.setPackageCode(packageCode);
			packageBean.setCreateDatetime(DateUtil.getNow());
			packageBean.setAddress(order.getAddress());
			packageBean.setWeight(apBean.getWeight());
			packageBean.setTotalPrice(order.getDprice());
			packageBean.setDeliver(order.getDeliver());
			packageBean.setOrderId(order.getId());
			packageBean.setStockInDatetime("1111-11-11 11:11:11");
			packageBean.setAssignTime("1111-11-11 11:11:11");
			packageBean.setPostStaffId(0);
			packageBean.setPostStaffName("");
			packageBean.setStockInAdminId(0);
			packageBean.setStockInAdminName("");
			packageBean.setMailingStatus(0);
			packageBean.setReturnStatus(0);
			packageBean.setBalanceStatus(0);
			if(stockService.addMailingBatchPackage(packageBean)){
				int count=stockService.getMailingBatchPackageCount("mailing_batch_parcel_code='"+parcelCode+"'");
				request.setAttribute("parcelCode",parcelCode);
				request.setAttribute("result", "成功：包裹数量"+ count);
			}else{
				dbOp.rollbackTransaction();
				request.setAttribute("parcelCode", parcelCode);
				request.setAttribute("color", "red");
				request.setAttribute("result", "失败：包裹单"+ packageCode + "未添加成功！");
			}
			
		}catch (Exception e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			dbOp.release();
			dbOpSlave.release();
		}
		return mapping.findForward("soAddParcel");
	}
	/**
	 * 按装箱单号查询
	 */
	public ActionForward findCartonningInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		String code = StringUtil.convertNull(request.getParameter("cartonningCode"));
		try {
			CartonningInfoBean cartonningBean = service.getCartonningInfo("code='" + code + "'");
			if(cartonningBean == null){
				request.setAttribute("result", "无法找到装箱记录");
				return mapping.findForward("zhuangxiangguanli");
			}
			int area = StringUtil.toInt((String)request.getSession().getAttribute("area"));
			CargoInfoBean cargoInfoBean = cargoService.getCargoInfo("id=" + cartonningBean.getCargoId());
			if(cargoInfoBean == null){
				request.setAttribute("result", "无法找到关联货位信息!");
				return mapping.findForward("zhuangxiangguanli");
			}
			if(area != cargoInfoBean.getAreaId()){
				request.setAttribute("result", "装箱单所关联货位的仓库与选择的仓库不一致!");
				return mapping.findForward("zhuangxiangguanli");
			}
			CartonningProductInfoBean cartonningProductBean = service.getCartonningProductInfo("cartonning_id='"+cartonningBean.getId()+"'");
			cartonningBean.setProductBean(cartonningProductBean);
			request.setAttribute("cartonningBean", cartonningBean);
			CargoOperationBean cargoOperationBean = cargoService.getCargoOperation("id=" + cartonningBean.getOperId());
			if(cargoOperationBean != null){
				request.setAttribute("cargoOperationBean", cargoOperationBean);
				CargoOperationCargoBean cargoOperationCargoBean = cargoService.getCargoOperationCargo("in_cargo_whole_code <> '' and oper_id=" + cargoOperationBean.getId());
				if(cargoOperationCargoBean != null){
					request.setAttribute("cargoOperationCargoBean", cargoOperationCargoBean);
				}
			}
			request.setAttribute("cargoInfoBean", cargoInfoBean);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("zhuangxiangguanli");
	}
	/**
	 * 创建&打印质检装箱单
	 */
	public ActionForward createQualityCartonning(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService iStockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		int productCount = StringUtil.StringToId(request.getParameter("count"));
		String bsCode = StringUtil.convertNull(request.getParameter("bsCode"));
		int areaId = StringUtil.toInt((String)request.getSession().getAttribute("area"));
		try {  
			synchronized (CartonningInfoAction.cargoLock) {
				if(!"".equals(bsCode)&&"".equals(productCode)){
					BuyStockinBean bsBean = iStockService.getBuyStockin("code='" + StringUtil.toSql(bsCode) + "'");
					if(bsBean == null){
						request.setAttribute("result", "入库单不存在！");
						return mapping.findForward("soCreateQualityCartonning");
					}
					if(bsBean.getStatus()!= 4 && bsBean.getStatus()!= 6 && bsBean.getStatus()!= 7){
						request.setAttribute("result", "入库单状态不正确！");
						return mapping.findForward("soCreateQualityCartonning");
					}
					request.setAttribute("bsCode", bsBean.getCode());
					return mapping.findForward("soCreateQualityCartonning");
				}
				if(!"".equals(bsCode)&&!"".equals(productCode)&&productCount == 0){
					BuyStockinBean bsBean = iStockService.getBuyStockin("code='" + StringUtil.toSql(bsCode) + "'");
					voProduct pBean = wareService.getProduct2("code="+"'"+productCode+"'");
					if(pBean==null){
						ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCode+"'");
						if(bBean==null){
							request.setAttribute("result", "没有找到此商品");
							request.setAttribute("bsCode", bsBean.getCode());
							return mapping.findForward("soCreateQualityCartonning");
						}else{
							pBean = wareService.getProduct(bBean.getProductId());
							if(pBean==null){
								request.setAttribute("result", "没有找到此商品");
								request.setAttribute("bsCode", bsBean.getCode());
								return mapping.findForward("soCreateQualityCartonning");
							}
						}
					}
					BuyStockinProductBean bspBean = iStockService.getBuyStockinProduct(" buy_stockin_id=" + bsBean.getId() +" and product_id=" + pBean.getId());
					if(bspBean == null){
						request.setAttribute("result", "该商品与入库单中商品不一致!");
						request.setAttribute("bsCode", bsBean.getCode());
						return mapping.findForward("soCreateQualityCartonning");
					}
					request.setAttribute("productCode", pBean.getCode());
					request.setAttribute("bsCode", bsBean.getCode());
					return mapping.findForward("soCreateQualityCartonning");
				}
				CargoInfoBean cargoInfo = cargoService.getCargoInfo("stock_type=1 and area_id=" + areaId);
				if(cargoInfo == null){
					request.setAttribute("result", "未找到待验库货位!");
					return mapping.findForward("soCreateQualityCartonning");
				}
				voProduct pBean = wareService.getProduct2("code="+"'"+productCode+"'");
				CargoProductStockBean cps=cargoService.getCargoProductStock("cargo_id="+cargoInfo.getId()+" and product_id="+pBean.getId());
				if(cps==null){
					request.setAttribute("result", "没有货位库存记录!");
					return mapping.findForward("soCreateQualityCartonning");
				}
				if(cps.getStockCount()<productCount){
					request.setAttribute("result", "货位可用库存不足!");
					return mapping.findForward("soCreateQualityCartonning");
				}
				BuyStockinBean bsBean = iStockService.getBuyStockin("code='" + StringUtil.toSql(bsCode) + "'");
				BuyStockinProductBean bspBean = iStockService.getBuyStockinProduct(" buy_stockin_id=" + bsBean.getId() +" and product_id=" + pBean.getId());
				List cartonningList = service.getCartonningList("buy_stockin_id=" + bsBean.getId() + " and status<>2 ", -1, -1, null);
				if(cartonningList.size()>0 ){
					int count =0;
					for(int i=0;i<cartonningList.size();i++){
						CartonningInfoBean ciBean = (CartonningInfoBean)cartonningList.get(i);
						CartonningProductInfoBean cipBean = service.getCartonningProductInfo("cartonning_id=" + ciBean.getId());
						count += cipBean.getProductCount();
					}
					if((count+ productCount)> bspBean.getStockInCount()){
						request.setAttribute("result", "该入库单累计装箱数量多于入库单中商品数量！");
						return mapping.findForward("soCreateQualityCartonning");
					}
				}else{
					if(productCount > bspBean.getStockInCount()){
						request.setAttribute("result", "该入库单累计装箱数量多于入库单中商品数量！");
						return mapping.findForward("soCreateQualityCartonning");
					}
				}
				String code = service.getZXCodeForToday();
				CartonningInfoBean bean =new CartonningInfoBean();
				bean.setCode(code);
				bean.setCreateTime(DateUtil.getNow());
				bean.setStatus(1);
				bean.setName(user.getUsername());
				bean.setCargoId(cargoInfo.getId());
				bean.setBuyStockInId(bsBean.getId());
				CartonningProductInfoBean productBean = new CartonningProductInfoBean();
				productBean.setProductCount(productCount);
				productBean.setProductCode(productCode);
				productBean.setProductName(pBean.getOriname());
				productBean.setProductId(pBean.getId());
				bean.setProductBean(productBean);
				service.getDbOp().startTransaction();  //开启事务
				if(!service.addCartonningInfo(bean)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "添加装箱单，数据库操作失败！");
					return mapping.findForward("soCreateQualityCartonning");
				}
				if( !service.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "创建装箱单，数据库操作失败!");
					return mapping.findForward("soCreateQualityCartonning");
				}
				code = bean.getCode();
				CartonningInfoBean bean2 = service.getCartonningInfo("code="+"'"+code+"'");
				//添加质检上架关联的地方
				BuyStockinUpshelfBean bsuBean = new BuyStockinUpshelfBean();
				bsuBean.setBuyStockinId(bsBean.getId());
				bsuBean.setBuyStockinDatetime(bsBean.getConfirmDatetime());
				bsuBean.setProductId(pBean.getId());
				bsuBean.setProductCode(pBean.getCode());
				bsuBean.setCartonningInfoId(bean2.getId());
				bsuBean.setCartonningInfoName(bean2.getName());
				bsuBean.setCargoOperationId(0);
				bsuBean.setWareArea(areaId);
				if( !checkStockinMissionService.addBuyStockinUpshelf(bsuBean)) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "创建入库单和装箱单关联失败!");
					return mapping.findForward("soCreateQualityCartonning");
				}
				productBean.setCartonningId(bean2.getId());
				if(!service.addCartonningProductInfo(productBean)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "添加装箱单产品信息，数据库操作失败！");
					return mapping.findForward("soCreateQualityCartonning");
				}
			
				bean = service.getCartonningInfo("code="+"'"+code+"'");
				productBean = service.getCartonningProductInfo("cartonning_id="+bean.getId());
				bean.setProductBean(productBean);
				voProduct product = wareService.getProduct(productBean.getProductId());
				if(product == null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "找不到商品信息!");
					return mapping.findForward("soCreateQualityCartonning");
				}
				
				//处理装箱单历史装箱平均值
				CartonningStandardCountBean csc=service.getCartonningStandardCount("product_id="+product.getId());
				if(csc!=null){
					String lastOperDatetime=csc.getLastOperDatetime();
					String sql="select cpi.product_count from cartonning_info ci join cartonning_product_info cpi on ci.id=cpi.cartonning_id " +
							"where cpi.product_id="+product.getId()+" and ci.create_time>'"+lastOperDatetime.substring(0,19)+"' and ci.status<>2 and cause=0";
					ResultSet rs=service.getDbOp().executeQuery(sql);
					float standard=0;
					float cartonningCount=0;
					while(rs.next()){
						standard+=rs.getInt(1);
						cartonningCount++;
					}
					if(cartonningCount>=3){//加上这次添加的装箱单达到3个，修改标准装箱量
						int newStandard=Math.round(standard/cartonningCount);
						if(!service.updateCartonningStandardCount("standard="+newStandard+",last_oper_datetime='"+DateUtil.getNow()+"'", "id="+csc.getId()))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return mapping.findForward(IConstants.FAILURE_KEY);
						}
						csc.setStandard(newStandard);
					}
				}else{
					CartonningStandardCountBean cBean=new CartonningStandardCountBean();
					cBean.setLastOperDatetime(DateUtil.getNow());
					cBean.setOperId(0);
					cBean.setOperName("");
					cBean.setProductId(product.getId());
					cBean.setStandard(productBean.getProductCount());
					
					if(!service.addCartonningStandardCount(cBean))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return mapping.findForward(IConstants.FAILURE_KEY);
					}
					csc=cBean;
				}
				
				voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id=" + product.getParentId1()+" or product_line_catalog.catalog_id=" + product.getParentId2());
				String result = "'质检装箱单:" + code + "创建并打印成功！";
				if(csc!=null){
					result+="标准装箱量："+csc.getStandard()+"。";
				}
				result+="'";
				String url = "soCreateQualityCartonning.jsp?result="+result+"&code=" + code+"&standard="+csc.getStandard();
				//物流员工绩效考核操作
				CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
				if(csBean == null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "此账号不是物流员工 !");
					request.setAttribute("result", "failure");
					return mapping.findForward("soCreateQualityCartonning");
				}
        		CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=2 and staff_id=" + csBean.getId() );
        		int operCount = 1;
        		int performanceProductCount = productCount;
        		if(cspBean != null){
        			performanceProductCount = performanceProductCount + cspBean.getProductCount();
        			operCount = operCount + cspBean.getOperCount();
					boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
					if(!flag){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "物流员工绩效考核更新操作失败 !");
						return mapping.findForward("soCreateQualityCartonning");
					}
				}else{
					CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
					newBean.setDate(date);
					newBean.setProductCount(performanceProductCount);
					newBean.setOperCount(operCount);
					newBean.setStaffId(csBean.getId());
					newBean.setType(2);  //2代表质检装箱作业
					boolean flag = cargoService.addCargoStaffPerformance(newBean);
					if(!flag){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "物流员工绩效考核添加操作失败 !");
						return mapping.findForward("soCreateQualityCartonning");
					}
				}
				service.getDbOp().commitTransaction(); //提交事务
				request.setAttribute("lineName", productLine.getName());
				request.setAttribute("bean", bean);
				request.setAttribute("url",url);
			}
		} catch (Exception e) {
			e.printStackTrace();
			boolean isAuto = service.getDbOp().getConn().getAutoCommit();
			if( !isAuto ) {
				service.getDbOp().rollbackTransaction();
			}
			request.setAttribute("result", "程序异常,质检装箱单创建失败!");
			return mapping.findForward("soCreateQualityCartonning");
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("soCartonningInfoPrint");
	}
	/**
	 * 创建&打印作业装箱单
	 */
	public ActionForward createCartonning(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		//获取货位编号和商品编号
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String wholeCode = StringUtil.convertNull(request.getParameter("wholeCode"));
		WareService wareService = new WareService();
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int  areaId = StringUtil.toInt((String) request.getSession().getAttribute("area")); //仓库id
		try {
			synchronized (CartonningInfoAction.cargoLock) {
			CargoInfoBean cargoInfo = cargoService.getCargoInfo("whole_code='"+wholeCode+"'");
			if(productCode.equals("")||productCode.equals("扫描商品编号")){
				if(cargoInfo == null){
					request.setAttribute("result", "货位号不正确！");
					return mapping.findForward("soCreateCartonning");
				}
				if(cargoInfo.getAreaId() != areaId){
					request.setAttribute("result", "此货位不属于这个仓！");
					return mapping.findForward("soCreateCartonning");
				}
				if(cargoInfo.getStatus() != 0){
					request.setAttribute("result", "此货位未被使用！");
					return mapping.findForward("soCreateCartonning");
				}
				if(cargoInfo.getStoreType() != 0&&cargoInfo.getStoreType() != 4){
					request.setAttribute("result", "此货位不是散件区或混合区货位！");
					return mapping.findForward("soCreateCartonning");
				}
				if(cargoInfo.getStockType() != 0){
					request.setAttribute("result", "此货位不是合格库货位！");
					return mapping.findForward("soCreateCartonning");
				}
				request.setAttribute("wholeCode", wholeCode);
				return mapping.findForward("soCreateCartonning");
			}
			voProduct pBean = wareService.getProduct2("code='"+productCode+"'");
			if(pBean==null){
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCode+"'");
				if(bBean==null){
					request.setAttribute("result", "没有找到此商品");
					return mapping.findForward("soCreateCartonning");
				}else{
					pBean = wareService.getProduct2("a.id="+bBean.getProductId());
					if (pBean == null) {
						request.setAttribute("result", "没有找到此商品");
						return mapping.findForward("soCreateCartonning");
					}
				}
			}
			
			String code = service.getZXCodeForToday();
			
			CargoProductStockBean cargoProductStockBean = cargoService.getCargoProductStock("product_id="+pBean.getId()+" and cargo_id="+ cargoInfo.getId());
			if(cargoProductStockBean == null){
				request.setAttribute("result", "此商品不属于该货位!");
				return mapping.findForward("soCreateCartonning");
			}
			if(cargoProductStockBean.getStockCount() <= 0){
				request.setAttribute("result", "存货为0不允许装箱!");
				return mapping.findForward("soCreateCartonning");
			}
			CartonningInfoBean bean =new CartonningInfoBean();
			bean.setCode(code);
			bean.setCreateTime(DateUtil.getNow());
			bean.setStatus(1);
			bean.setName(user.getUsername());
			bean.setCargoId(cargoProductStockBean.getCargoId());
			CartonningProductInfoBean productBean=new CartonningProductInfoBean();
		    productBean.setProductCount(cargoProductStockBean.getStockCount());
			productBean.setProductCode(pBean.getCode());
			productBean.setProductName(pBean.getOriname());
			productBean.setProductId(pBean.getId());
			bean.setProductBean(productBean);
			
			service.getDbOp().startTransaction();  //开启事务
			if(!service.addCartonningInfo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "创建装箱单，数据库操作失败!");
				return mapping.findForward("soCreateCartonning");
			}
			if( !service.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "创建装箱单，数据库操作失败!");
				return mapping.findForward("soCreateCartonning");
			}
			code = bean.getCode();
			CartonningInfoBean bean2 = service.getCartonningInfo("code="+"'"+code+"'");
			productBean.setCartonningId(bean2.getId());
			if(!service.addCartonningProductInfo(productBean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "创建装箱单，数据库操作失败!");
				return mapping.findForward("soCreateCartonning");
			}
			
			bean = service.getCartonningInfo("code="+"'"+code+"'");
			productBean = service.getCartonningProductInfo("cartonning_id="+bean.getId());
			bean.setProductBean(productBean);
			voProduct product = wareService.getProduct(productBean.getProductId());
			if(product == null){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "找不到商品信息!");
				return mapping.findForward("soCreateCartonning");
			}
			voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id=" + product.getParentId1()+" or product_line_catalog.catalog_id=" + product.getParentId2());
			if(productLine == null){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "查询不到商品线信息!");
			return mapping.findForward("soCreateCartonning");
			}
			service.getDbOp().commitTransaction(); //提交事务
			request.setAttribute("lineName", productLine.getName());
			String result = "作业装箱单:" + code + "创建并打印成功！";
			String url = "soCreateCartonning.jsp?result="+result+"&code=" + code;
			request.setAttribute("bean", bean);
			request.setAttribute("url",url);
			request.getSession().removeAttribute("wholeCode");
			}
		} catch (Exception e) {
			boolean isAuto = service.getDbOp().getConn().getAutoCommit();
			if( !isAuto ) {
				service.getDbOp().rollbackTransaction();
			}
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("soCartonningInfoPrint");
	}
	/**
	 * 打印装箱单
	 */
	public ActionForward printCartonning(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		String code = StringUtil.convertNull(request.getParameter("cartonningCode"));
		try{
			synchronized (cargoLock) {
				CartonningInfoBean bean = service.getCartonningInfo("code='" + code + "'");
				if(bean != null){
					int area = StringUtil.toInt((String)request.getSession().getAttribute("area"));
					CargoInfoBean cargoInfoBean = cargoService.getCargoInfo("id=" + bean.getCargoId());
					if(cargoInfoBean!=null&&area != cargoInfoBean.getAreaId()){
						request.setAttribute("result", "装箱单所关联货位的仓库与选择的仓库不一致!");
						return mapping.findForward("soPrintCartonning");
					}
					if(bean.getStatus()==CartonningInfoBean.STATUS2){
						request.setAttribute("result", "该装箱单已作废!");
						return mapping.findForward("soPrintCartonning");
					}
					CartonningProductInfoBean cpib = service.getCartonningProductInfo("cartonning_id=" + bean.getId());
					voProduct product = wareService.getProduct(cpib.getProductId());
					if(product == null){
						request.setAttribute("result", "找不到商品信息,无法打印!");
						return mapping.findForward("soPrintCartonning");
					}
					voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id=" + product.getParentId1()+" or product_line_catalog.catalog_id=" + product.getParentId2());
					if(productLine == null){
						request.setAttribute("result", "查询不到商品线信息,无法打印!");
					return mapping.findForward("soPrintCartonning");
					}
					CartonningProductInfoBean printProductBean = service.getCartonningProductInfo("cartonning_id=" + bean.getId());
					service.updateCartonningInfo("status=1", "id=" + bean.getId());
					bean.setProductBean(printProductBean);
					request.setAttribute("lineName", productLine.getName());
					request.setAttribute("bean", bean);
					String url = "soPrintCartonning.jsp?result=" + code;
					request.setAttribute("url", url);
				}else{
					request.setAttribute("result", "没有找到此装箱单,无法打印！");
					return mapping.findForward("soPrintCartonning");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("result", "装箱单:" + code + "打印失败！");
			return mapping.findForward("soPrintCartonning");
		} finally {
			service.releaseAll();
			wareService.releaseAll();
		}
		return mapping.findForward("soCartonningInfoPrint");
	}
	/**
	 * 
	 * 删除装箱记录
	 */
	public ActionForward delCartonning(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String code = StringUtil.convertNull(request.getParameter("cartonningCode"));
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,null);
		WareService wareService = new WareService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {  
			CartonningInfoBean bean = service.getCartonningInfo("code='" + code + "'");
			if(bean != null){
				int area = StringUtil.toInt((String)request.getSession().getAttribute("area"));
				CargoInfoBean cargoInfoBean = cargoService.getCargoInfo("id=" + bean.getCargoId());
				if(cargoInfoBean!=null&&area != cargoInfoBean.getAreaId()){
					request.setAttribute("result", "装箱单所关联货位的仓库与选择的仓库不一致!");
					if(flag.equals("quality")){
						return mapping.findForward("soCreateQualityCartonning");
					}else{
						return mapping.findForward("soCreateCartonning");
					}
				}
				CartonningInfoBean ciBean = service.getCartonningInfo("code='"+ code + "'");
				if(ciBean == null){
					request.setAttribute("result", "装箱单[" + code + "不存在!" );
					if(flag.equals("quality")){
						return mapping.findForward("soCreateQualityCartonning");
					}else{
						return mapping.findForward("soCreateCartonning");
					}
				}
				service.getDbOp().startTransaction();//开启事务
				
				if(service.deleteCartonningInfo("id="+ ciBean.getId())){
					if(!service.deleteCartonningProductInfo("cartonning_id=" + ciBean.getId())){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "删除装箱单商品失败 !");
						if(flag.equals("quality")){
							return mapping.findForward("soCreateQualityCartonning");
						}else{
							return mapping.findForward("soCreateCartonning");
						}
					}
					//物流员工绩效考核操作
					CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
					if(csBean == null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "此账号不是物流员工 !");
						if(flag.equals("quality")){
							return mapping.findForward("soCreateQualityCartonning");
						}else{
							return mapping.findForward("soCreateCartonning");
						}
					}
					CartonningProductInfoBean cpiBean = service.getCartonningProductInfo(" cartonning_id=" + bean.getId());
					if(cpiBean != null){
						CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=2 and staff_id=" + csBean.getId() );
						if(cspBean != null){
							int operCount = cspBean.getOperCount() - 1;
							int performanceProductCount = cspBean.getProductCount() - cpiBean.getProductCount();
							boolean flagDel = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
							if(!flagDel){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("result", "物流员工绩效考核更新操作失败 !");
								if(flag.equals("quality")){
									return mapping.findForward("soCreateQualityCartonning");
								}else{
									return mapping.findForward("soCreateCartonning");
								}
							}
						}
					}
				}else{
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "装箱单:" + code + "删除失败!");
					if(flag.equals("quality")){
						return mapping.findForward("soCreateQualityCartonning");
					}else{
						return mapping.findForward("soCreateCartonning");
					}
				}
				service.getDbOp().commitTransaction(); //提交事务
				request.setAttribute("result", "装箱单:" + code + "已成功删除!");
			}else{
				request.setAttribute("result", "没有找到此装箱单！");
				if(flag.equals("quality")){
					return mapping.findForward("soCreateQualityCartonning");
				}else{
					return mapping.findForward("soCreateCartonning");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		 	service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
			wareService.releaseAll();
		}
		if(flag.equals("quality")){
			return mapping.findForward("soCreateQualityCartonning");
		}else{
			return mapping.findForward("soCreateCartonning");
		}
	}
	/**
	 * 
	 * 作废装箱记录
	 */
	public ActionForward dropCartonning(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("result", "当前没有登录，操作失败！");
			return mapping.findForward("soDropCartonning");
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("soDropCartonning");
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String code = StringUtil.convertNull(request.getParameter("cartonningCode"));
		CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+code+"'");
		if(cartonningInfo==null){
			request.setAttribute("result", code+",没有找到该装箱单！");
			return mapping.findForward("soDropCartonning");
		}
		int area = StringUtil.toInt((String)request.getSession().getAttribute("area"));
		CargoInfoBean cargoInfoBean = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
		if(cargoInfoBean!=null&&area != cargoInfoBean.getAreaId()){
			request.setAttribute("result", "装箱单所关联货位的仓库与选择的仓库不一致!");
			return mapping.findForward("soDropCartonning");
		}
		CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
		if(cargoOperation != null){
			if(cargoOperation.getStatus()!= 7 
				&& cargoOperation.getStatus()!= 8 
				&& cargoOperation.getStatus()!= 9 
				&& cargoOperation.getStatus()!= 16 
				&& cargoOperation.getStatus()!= 17
				&& cargoOperation.getStatus()!= 18 
				&& cargoOperation.getStatus()!= 25 
				&& cargoOperation.getStatus()!= 26 
				&& cargoOperation.getStatus()!= 27 
				&& cargoOperation.getStatus()!= 34 
				&& cargoOperation.getStatus()!= 35 
				&& cargoOperation.getStatus()!= 36){
				request.setAttribute("result", "此装箱单关联的作业单为未完成状态！");
				return mapping.findForward("soDownProduct");
			}
		}
		try {  
			synchronized (cargoLock) {
			CartonningInfoBean bean = cartonningService.getCartonningInfo("code='" + code + "'");
			if(bean != null){
				cartonningService.updateCartonningInfo("status=2", "code='"+ code + "'");
				 request.setAttribute("result", "装箱单：" + code + "已作废!");
			}else{
				request.setAttribute("result", "没有找到此装箱单！");
				return mapping.findForward("soDropCartonning");
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("soDropCartonning");
	}
	/**
	 * 上架作业
	 * liubo
	 * 增加回滚代码
	 * 
	 */
	public ActionForward upProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode"));//装箱单条码
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE,dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			synchronized (cargoLock) {
			service.getDbOp().startTransaction();    //开启事务
			CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
			if(cartonningInfo==null){
				request.setAttribute("result", "未找到该装箱单!");
				return mapping.findForward("soUpProduct");
			}
			if(cartonningInfo.getStatus()==CartonningInfoBean.STATUS2){
				request.setAttribute("result", "该装箱单已作废!");
				return mapping.findForward("soUpProduct");
			}
			CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
			if(cargoOperation != null){
				if(cargoOperation.getStatus()!= 8 && cargoOperation.getStatus()!= 17 && cargoOperation.getStatus()!= 26 && cargoOperation.getStatus()!= 35
						&& cargoOperation.getStatus()!= 9 && cargoOperation.getStatus()!= 18 && cargoOperation.getStatus()!= 27 && cargoOperation.getStatus()!= 36
						&& cargoOperation.getEffectStatus()!=3 && cargoOperation.getEffectStatus()!=4){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "此装箱单关联的作业单为未完成状态!");
					return mapping.findForward("soUpProduct");
				}
			}
			CargoInfoBean cargoInfo = service.getCargoInfo("id="+cartonningInfo.getCargoId());
			if(cargoInfo==null){
			    service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "未找到装箱单关联货位!");
				return mapping.findForward("soUpProduct");
			}

			if(cargoInfo.getStoreType()==1){
			    service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "装箱单关联货位为整件区，不能生成上架单!");
				return mapping.findForward("soUpProduct");
			}
			int areaId=Integer.parseInt((String)request.getSession().getAttribute("area"));
			CargoInfoAreaBean ciaBean = service.getCargoInfoArea("old_id = " + areaId);
			if(cargoInfo.getAreaId()!= ciaBean.getId()){
			    service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "关联货位不属于这个仓库!");
				return mapping.findForward("soUpProduct");
			}
			CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
			if(cartonningProduct==null){
			    service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "找不到作业量相关信息!");
				return mapping.findForward("soUpProduct");
			}
			ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id=" + cartonningProduct.getProductId());
			if( pwpBean == null ) {
			    service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "作业单中商品"+ cartonningProduct.getProductCode()+ "没有商品物流属性！");
				return mapping.findForward("soUpProduct");
			}
			
			//新采购计划编号：CGJIH20090601001
			String code = "HWS"+DateUtil.getNow().substring(2,10).replace("-", "");   
			//生成编号
			CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
			if(cargoOper == null){
				code = code + "00001";
			}else{//获取当日计划编号最大值
				String _code = cargoOper.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-5));
				number++;
				code += String.format("%05d",new Object[]{new Integer(number)});
			}
			//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
			String storageCode = "";
			if(areaId == ProductStockBean.AREA_GF){
				storageCode ="GZF";
			}else if(areaId == ProductStockBean.AREA_ZC){
				storageCode ="GZZ";
			} else if ( areaId == ProductStockBean.AREA_WX) {
				storageCode = "JSW";
			} else if( areaId == ProductStockBean.AREA_GS) {
				storageCode = "GZS";
			} else if( areaId == ProductStockBean.AREA_BJ) {
				storageCode = "BJA";
			}
			//------------上面对应的 无锡库的 StorageCode 是什么
			//添加作业单
			cargoOper = new CargoOperationBean();
			cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS3);
			cargoOper.setCreateDatetime(DateUtil.getNow());
			cargoOper.setRemark("");
			cargoOper.setCreateUserId(user.getId());
			cargoOper.setAuditingDatetime(DateUtil.getNow());
			cargoOper.setAuditingUserId(user.getId());
			cargoOper.setCode(code);
			cargoOper.setSource("");
			cargoOper.setStorageCode(storageCode);
			cargoOper.setStockInType(CargoInfoBean.STORE_TYPE4);//111
			cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE1);
			cargoOper.setCreateUserName(user.getUsername());
			cargoOper.setAuditingUserName(user.getUsername());
			cargoOper.setType(CargoOperationBean.TYPE0);
			cargoOper.setLastOperateDatetime(DateUtil.getNow());
			cargoOper.setConfirmDatetime(DateUtil.getNow());
			
			
			if(!service.addCargoOperation(cargoOper)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加上架单失败!");
				return mapping.findForward("soUpProduct");
			}
			int operId = service.getDbOp().getLastInsertId();
			//在根据入库单生成上架单的时候 如果找到了在生成质检装箱单的单据时  修改当中的上架单关联字段， 确保关联的是最后一个上架单信息
			BuyStockinUpshelfBean bsuBean = checkStockinMissionService.getBuyStockinUpshelf("cartonning_info_id=" + cartonningInfo.getId());
			if( bsuBean != null ) {
				if( !checkStockinMissionService.updateBuyStockinUpshelf("cargo_operation_id=" + operId, "id=" + bsuBean.getId()) ) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "关联装箱单和上架单失败！");
					return mapping.findForward("soUpProduct");
				} 
			}
			
			String inCargoWholeCode = "";
			List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=" + CargoInfoBean.STORE_TYPE4 + 
					" and ci.stock_type=0 and cps.product_id=" +cartonningProduct.getProductId()+" and ci.area_id="+ ciaBean.getId() + 
					" and ci.status="+CargoInfoBean.STATUS0+" and ci.id!="+cartonningInfo.getCargoId()+" and ci.whole_code not like 'GZZ01-C%'", 
					-1, -1, "stock_count DESC");//相同sku 库存最多的货位
			voProduct product = wareService.getProduct(cartonningProduct.getProductId());
			//123
			List<CargoInfoBean> relatedAvailList = new ArrayList<CargoInfoBean>();
			
			//查询合适的目的货位
			for(int i=0;i<cargoAndProductStockList.size();i++){
				CargoProductStockBean cpsBean = (CargoProductStockBean)cargoAndProductStockList.get(i);
				relatedAvailList.add(cpsBean.getCargoInfo());
			}
			//修改目的货位选择方式
			if(relatedAvailList.size()>0){//相同商品
				Random ran=new Random();
				inCargoWholeCode=relatedAvailList.get(ran.nextInt(relatedAvailList.size())).getWholeCode();
			}
			if(inCargoWholeCode.length()==0){//相同产品线
				voProductLine productLine=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
				if(productLine==null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "产品："+product.getCode()+"产品线未知!");
					return mapping.findForward("soUpProduct");
				}
				List cargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and product_line_id=" + productLine.getId()+" and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId()+" and whole_code not like 'GZZ01-C%'", -1, -1, null);
				if(cargoList.size()>0){
					Random ran=new Random();
					inCargoWholeCode=((CargoInfoBean)(cargoList.get(ran.nextInt(cargoList.size())))).getWholeCode();
				}
			}
			if(inCargoWholeCode.length()==0){//全仓随机
				List cargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId()+" and whole_code not like 'GZZ01-C%'", -1, -1, null);
				if(cargoList.size()>0){
					Random ran=new Random();
					inCargoWholeCode=((CargoInfoBean)(cargoList.get(ran.nextInt(cargoList.size())))).getWholeCode();
				}
			}
			//目的货位选择结束
			
			if(inCargoWholeCode.length()==0){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "没有货位可用！");
				return mapping.findForward("soUpProduct");
			}
			CargoInfoBean inCargoInfo = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
			CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
			CargoProductStockBean inCargoProductStock = service.getCargoProductStock("cargo_id=" + inCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
			if(inCargoProductStock==null){
				inCargoProductStock = new CargoProductStockBean();
				inCargoProductStock.setCargoId(inCargoInfo.getId());
				inCargoProductStock.setProductId(cartonningProduct.getProductId());
				inCargoProductStock.setStockCount(0);
				inCargoProductStock.setStockLockCount(0);
				if(!service.addCargoProductStock(inCargoProductStock)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "数据库操作失败!");
					return mapping.findForward("soUpProduct");
				}
				inCargoProductStock.setId(dbOp.getLastInsertId());
			}
			CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
			CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(0);
			bean.setInCargoWholeCode("");
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(1);
			bean.setUseStatus(0);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加货位详细信息失败！");
				return mapping.findForward("soUpProduct");
			}
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(inCargoProductStock.getId());
			bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(0);
			bean.setUseStatus(1);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加货位详细信息失败！");
				return mapping.findForward("soUpProduct");
			}
			if(!cartonningService.updateCartonningInfo("oper_id=" + operId, "code='" + cartonningCode + "'")){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "更新装箱单，数据库操作失败！");
				return mapping.findForward("soUpProduct");
			}
			//锁库存
			if(!service.updateCargoProductStockCount(outCargoProductStock.getId(), -cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soUpProduct");
			}
			if(!service.updateCargoProductStockLockCount(outCargoProductStock.getId(), cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soUpProduct");
			}
			if(inCargoInfo.getAreaId() != outCargoInfo.getAreaId() || inCargoInfo.getStockType() != outCargoInfo.getStockType()){
				CargoInfoAreaBean area=service.getCargoInfoArea("id="+outCargoInfo.getAreaId());
				ProductStockBean productStock = productService.getProductStock("product_id=" + cartonningProduct.getProductId()+" and area="+area.getOldId()+" and type="+outCargoInfo.getStockType());
				if(productStock == null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "找不到商品库存信息！");
					return mapping.findForward("soUpProduct");
				}
				if(!productService.updateProductLockCount(productStock.getId(), cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库冻结库存不足！");
					return mapping.findForward("soUpProduct");
				}
				if(!productService.updateProductStockCount(productStock.getId(), -cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库库存不足！");
					return mapping.findForward("soUpProduct");
				}
			}
			
			//合格库待作业任务处理
			CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("product_id="+cartonningInfo.getId()+" and status in(0,1,2) and type=0");
			if(cot!=null){
				if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "更新待作业任务状态，数据库操作失败！");
					return mapping.findForward("soUpProduct");
				}
			}
			//物流员工绩效考核操作
			CargoStaffBean csBean = service.getCargoStaff(" user_id=" + user.getId());
			if(csBean == null){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "此账号不是物流员工 !");
				request.setAttribute("result", "failure");
				return mapping.findForward("soUpProduct");
			}
    		CargoStaffPerformanceBean cspBean = service.getCargoStaffPerformance(" date='" + date + "' and type=3 and staff_id=" + csBean.getId() );
    		int operCount = 1;
    		int performanceProductCount = cartonningProduct.getProductCount();
    		if(cspBean != null){
    			performanceProductCount = performanceProductCount + cspBean.getProductCount();
    			operCount = operCount + cspBean.getOperCount();
				boolean flag = service.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
				if(!flag){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "物流员工绩效考核更新操作失败 !");
					return mapping.findForward("soUpProduct");
				}
			}else{
				CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
				newBean.setDate(date);
				newBean.setProductCount(performanceProductCount);
				newBean.setOperCount(operCount);
				newBean.setStaffId(csBean.getId());
				newBean.setType(3);  //3代表上架作业
				boolean flag = service.addCargoStaffPerformance(newBean);
				if(!flag){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "物流员工绩效考核添加操作失败 !");
					return mapping.findForward("soUpProduct");
				}
			}
			service.getDbOp().commitTransaction();  //提交事务
			request.setAttribute("result", "上架单:" + code + "已创建成功!");
			}
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			request.setAttribute("result", "操作失败,系统异常！");
			return mapping.findForward("soUpProduct");
		}finally{
			service.releaseAll();
			productService.releaseAll();
			wareService.releaseAll();
		}
		return mapping.findForward("soUpProduct");
	}
	/**
	 * 下架作业
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward downProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode"));//装箱单条码
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService();
		IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			synchronized (cargoLock) {
			CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
			if(cartonningInfo==null){
				request.setAttribute("result", "未找到该装箱单！");
				return mapping.findForward("soDownProduct");
			}
			if(cartonningInfo.getStatus()==CartonningInfoBean.STATUS2){
				request.setAttribute("result", "该装箱单已作废！");
				return mapping.findForward("soDownProduct");
			}
			CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
			if(cargoOperation != null){
				if(cargoOperation.getStatus()!= 8 && cargoOperation.getStatus()!= 17 && cargoOperation.getStatus()!= 26 && cargoOperation.getStatus()!= 35
						&& cargoOperation.getStatus()!= 9 && cargoOperation.getStatus()!= 18 && cargoOperation.getStatus()!= 27 && cargoOperation.getStatus()!= 36
						&& cargoOperation.getEffectStatus()!=3 && cargoOperation.getEffectStatus()!=4){
					request.setAttribute("result", "此装箱单关联的作业单为未完成状态！");
					return mapping.findForward("soDownProduct");
				}
			}
 			CargoInfoBean cargoInfo = service.getCargoInfo("id="+cartonningInfo.getCargoId());
			if(cargoInfo==null){
				request.setAttribute("result", "未找到装箱单关联货位！");
				return mapping.findForward("soDownProduct");
			}
			int areaId=Integer.parseInt(request.getSession().getAttribute("area").toString());
			if(cargoInfo.getAreaId()!=areaId){
				request.setAttribute("result", "关联货位不属于这个仓库！");
				return mapping.findForward("soDownProduct");
			}
			CartonningProductInfoBean cartonningProduct = cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
			if(cartonningProduct==null){
				request.setAttribute("result", "根据此装箱单号找不到库存信息");
				return mapping.findForward("soDownProduct");
			}
			//新采购计划编号：CGJIH20090601001
			String code = "HWX"+DateUtil.getNow().substring(2,10).replace("-", "");   
			//生成编号
			CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
			if(cargoOper == null){
				code = code + "00001";
			}else{//获取当日计划编号最大值
				String _code = cargoOper.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-5));
				number++;
				code += String.format("%05d",new Object[]{new Integer(number)});
			}
			//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
			String storageCode = "";
			if(areaId == 1){
				storageCode ="GZF";
			}else if(areaId == 3){
				storageCode ="GZZ";
			}
			//添加作业单
			cargoOper = new CargoOperationBean();
			cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS12);
			cargoOper.setCreateDatetime(DateUtil.getNow());
			cargoOper.setRemark("");
			cargoOper.setCreateUserId(user.getId());
			cargoOper.setAuditingDatetime(DateUtil.getNow());
			cargoOper.setAuditingUserId(user.getId());
			cargoOper.setCode(code);
			cargoOper.setSource("");
			cargoOper.setStorageCode(storageCode);
			cargoOper.setStockInType(CargoInfoBean.STORE_TYPE1);
			cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE0);
			cargoOper.setCreateUserName(user.getUsername());
			cargoOper.setAuditingUserName(user.getUsername());
			cargoOper.setType(CargoOperationBean.TYPE1);
			cargoOper.setLastOperateDatetime(DateUtil.getNow());
			cargoOper.setConfirmDatetime(DateUtil.getNow());
			
			service.getDbOp().startTransaction();      //开启事务 
			if(!service.addCargoOperation(cargoOper)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加下架单，数据库操作失败！");
				return mapping.findForward("soDownProduct");
			}
			int operId = service.getDbOp().getLastInsertId();
			String inCargoWholeCode = "";
			//目的货位选择条件为 目的货位为地区合格库缓存区唯一货位
			List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=2 and ci.stock_type=0 and ci.area_id="+cargoInfo.getAreaId(), -1, -1,null);
			if(cargoAndProductStockList.size() > 0){
				inCargoWholeCode = ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo().getWholeCode();
			}else{
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "没有货位可用！");
				return mapping.findForward("soDownProduct");
			}
			CargoInfoBean inCargoInfo = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
			CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
			CargoProductStockBean inCargoProductStock = service.getCargoProductStock("cargo_id=" + inCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
			CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
			if(outCargoProductStock.getStockCount() <cartonningProduct.getProductCount()){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "可用库存量不足,不允许下架!");
				return mapping.findForward("soDownProduct");
			}
			CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(0);
			bean.setInCargoWholeCode("");
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(0);
			bean.setUseStatus(0);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加下架单详细信息，数据库操作失败!");
				return mapping.findForward("soDownProduct");
			}
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(inCargoProductStock.getId());
			bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(1);
			bean.setUseStatus(1);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加下架单详细信息，数据库操作失败!");
				return mapping.findForward("soDownProduct");
			}
			if(!cartonningService.updateCartonningInfo("oper_id=" + operId, "code='" + cartonningCode + "'")){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "更新装箱单，数据库操作失败!");
				return mapping.findForward("soDownProduct");
			}
			//锁库存
			if(!service.updateCargoProductStockCount(outCargoProductStock.getId(), -cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soDownProduct");
			}
			if(!service.updateCargoProductStockLockCount(outCargoProductStock.getId(), cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soDownProduct");
			}
			if(inCargoInfo.getAreaId() != outCargoInfo.getAreaId() || inCargoInfo.getStockType() != outCargoInfo.getStockType()){
				CargoInfoAreaBean area=service.getCargoInfoArea("id="+outCargoInfo.getAreaId());
				ProductStockBean productStock = productService.getProductStock("product_id=" + cartonningProduct.getProductId()+" and area="+area.getOldId()+" and type="+outCargoInfo.getStockType());
				if(productStock == null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "找不到商品库存信息！");
					return mapping.findForward("soDownProduct");
				}
				if(!productService.updateProductLockCount(productStock.getId(), cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库冻结库存不足！");
					return mapping.findForward("soDownProduct");
				}
				if(!productService.updateProductStockCount(productStock.getId(), -cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库库存不足！");
					return mapping.findForward("soDownProduct");
				}
			}
			
			//合格库待作业任务处理
			CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=1");
			if(cot!=null){
				if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			service.getDbOp().commitTransaction();// 提交事务
			request.setAttribute("result", "下架单:" + code + "已创建成功!");
			}
		}catch(Exception e){
			service.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("result", "操作失败,系统异常！");
			return mapping.findForward("soDownProduct");
		}finally{
			service.releaseAll();
			wareService.releaseAll();
			productService.releaseAll();
		}
		return mapping.findForward("soDownProduct");
	}
	/**
	 * 补货作业
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward addProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode"));//装箱单条码
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService();
		IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			synchronized (cargoLock) {
			CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
			if(cartonningInfo==null){
				request.setAttribute("result", "未找到该装箱单！");
				return mapping.findForward("soAddProduct");
			}
			if(cartonningInfo.getStatus()==CartonningInfoBean.STATUS2){
				request.setAttribute("result", "该装箱单已作废！");
				return mapping.findForward("soAddProduct");
			}
			CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
			if(cargoOperation != null){
				if(cargoOperation.getStatus()!= 8 && cargoOperation.getStatus()!= 17 && cargoOperation.getStatus()!= 26 && cargoOperation.getStatus()!= 35
						&& cargoOperation.getStatus()!= 9 && cargoOperation.getStatus()!= 18 && cargoOperation.getStatus()!= 27 && cargoOperation.getStatus()!= 36
						&& cargoOperation.getEffectStatus()!=3 && cargoOperation.getEffectStatus()!=4){
					request.setAttribute("result", "此装箱单关联的作业单为未完成状态！");
					return mapping.findForward("soAddProduct");
				}
			}
			CargoInfoBean cargoInfo=service.getCargoInfo("id="+cartonningInfo.getCargoId());
			if(cargoInfo==null){
				request.setAttribute("result", "未找到装箱单关联货位！");
				return mapping.findForward("soAddProduct");
			}
			int areaId=Integer.parseInt(request.getSession().getAttribute("area").toString());
			if(cargoInfo.getAreaId()!=areaId){
				request.setAttribute("result", "关联货位不属于这个仓库！");
				return mapping.findForward("soAddProduct");
			}
			CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
			if(cartonningProduct==null){
				request.setAttribute("result", "根据此装箱单号找不到库存信息");
				return mapping.findForward("soAddProduct");
			}
			ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id=" + cartonningProduct.getProductId());
			if( pwpBean == null ) {
				request.setAttribute("result", "装箱单中商品"+ cartonningProduct.getProductCode()+ "没有商品物流属性！");
				return mapping.findForward("soUpProduct");
			}
			
			//新采购计划编号：CGJIH20090601001
			String code = "HWB"+DateUtil.getNow().substring(2,10).replace("-", "");   
			//生成编号
			CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
			if(cargoOper == null){
				code = code + "00001";
			}else{//获取当日计划编号最大值
				String _code = cargoOper.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-5));
				number++;
				code += String.format("%05d",new Object[]{new Integer(number)});
			}
			//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
			String storageCode = "";
			if(areaId == ProductStockBean.AREA_GF){
				storageCode ="GZF";
			}else if(areaId == ProductStockBean.AREA_ZC){
				storageCode ="GZZ";
			} else if ( areaId == ProductStockBean.AREA_WX) {
				storageCode = "JSW";
			} else if( areaId == ProductStockBean.AREA_GS) {
				storageCode = "GZS";
			} else if( areaId == ProductStockBean.AREA_BJ) {
				storageCode = "BJA";
			}
			//添加作业单
			cargoOper = new CargoOperationBean();
			cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS21);
			cargoOper.setCreateDatetime(DateUtil.getNow());
			cargoOper.setRemark("");
			cargoOper.setCreateUserId(user.getId());
			cargoOper.setAuditingDatetime(DateUtil.getNow());
			cargoOper.setAuditingUserId(user.getId());
			cargoOper.setCode(code);
			cargoOper.setSource("");
			cargoOper.setStorageCode(storageCode);
			cargoOper.setStockInType(CargoInfoBean.STORE_TYPE4);
			cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE1);
			cargoOper.setCreateUserName(user.getUsername());
			cargoOper.setAuditingUserName(user.getUsername());
			cargoOper.setType(CargoOperationBean.TYPE2);
			cargoOper.setLastOperateDatetime(DateUtil.getNow());
			cargoOper.setConfirmDatetime(DateUtil.getNow());

			service.getDbOp().startTransaction(); //开启事务
			if(!service.addCargoOperation(cargoOper)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加补货单，数据库操作失败！");
				return mapping.findForward("soAddProduct");
			}
			int operId = service.getDbOp().getLastInsertId();
			String inCargoWholeCode = "";
			List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=" + CargoInfoBean.STORE_TYPE4 + " and ci.stock_type=0 and  cps.product_id=" +cartonningProduct.getProductId()+" and ci.area_id="+cargoInfo.getAreaId() + " and ci.status=" + CargoInfoBean.STATUS0+" and ci.id!="+cartonningInfo.getCargoId(), -1, -1, "stock_count DESC");
			voProduct product = wareService.getProduct(cartonningProduct.getProductId());
			//123
			List<CargoInfoBean> relatedAvailList = new ArrayList<CargoInfoBean>();
			for(int i=0;i<cargoAndProductStockList.size();i++){
				CargoProductStockBean cpsBean = (CargoProductStockBean)cargoAndProductStockList.get(i);
				relatedAvailList.add(cpsBean.getCargoInfo());
			}
			
			//修改目的货位选择方式
			if(relatedAvailList.size()>0){//相同商品
				Random ran=new Random();
				inCargoWholeCode=relatedAvailList.get(ran.nextInt(relatedAvailList.size())).getWholeCode();
			}
			if(inCargoWholeCode.length()==0){//相同产品线
				voProductLine productLine=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
				if(productLine==null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "产品："+product.getCode()+"产品线未知!");
					return mapping.findForward("soUpProduct");
				}
				List cargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and product_line_id=" + productLine.getId()+" and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId(), -1, -1, null);
				if(cargoList.size()>0){
					Random ran=new Random();
					inCargoWholeCode=((CargoInfoBean)(cargoList.get(ran.nextInt(cargoList.size())))).getWholeCode();
				}
			}
			if(inCargoWholeCode.length()==0){//全仓随机
				List cargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId(), -1, -1, null);
				if(cargoList.size()>0){
					Random ran=new Random();
					inCargoWholeCode=((CargoInfoBean)(cargoList.get(ran.nextInt(cargoList.size())))).getWholeCode();
				}
			}
			//目的货位选择结束
			
			
			if(inCargoWholeCode.length()==0){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "没有货位可用！");
				return mapping.findForward("soUpProduct");
			}
			CargoInfoBean inCargoInfo = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
			CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
			CargoProductStockBean inCargoProductStock = service.getCargoProductStock("cargo_id=" + inCargoInfo.getId()+" and product_id="+product.getId());
			if(inCargoProductStock==null){
				inCargoProductStock = new CargoProductStockBean();
				inCargoProductStock.setCargoId(inCargoInfo.getId());
				inCargoProductStock.setProductId(cartonningProduct.getProductId());
				inCargoProductStock.setStockCount(0);
				inCargoProductStock.setStockLockCount(0);
				if(!service.addCargoProductStock(inCargoProductStock)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "数据库操作失败!");
					return mapping.findForward("soUpProduct");
				}
				inCargoProductStock.setId(dbOp.getLastInsertId());
			}
			CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+product.getId());
				
			CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(inCargoProductStock.getId());
			bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
			bean.setOutCargoProductStockId(0);
			bean.setOutCargoWholeCode("");
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(0);
			bean.setUseStatus(0);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加补货单详细信息，数据库操作失败！");
				return mapping.findForward("soAddProduct");
			}
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(inCargoProductStock.getId());
			bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(1);
			bean.setUseStatus(1);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加补货单详细信息，数据库操作失败！");
				return mapping.findForward("soAddProduct");
			}
			
			int maxStockCount = (int) (cartonningProduct.getProductCount()*1.1);
			int warStockCount = (int) (maxStockCount*0.2);
			if(maxStockCount > cargoInfo.getMaxStockCount()){
				if(!service.updateCargoInfo("max_stock_count=" + maxStockCount + " and warn_stock_count=" + warStockCount , "id=" + cargoInfo.getId()))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			if(!cartonningService.updateCartonningInfo("oper_id=" + operId, "code='" + cartonningCode + "'"))
			{
			  service.getDbOp().rollbackTransaction();
			  request.setAttribute("tip", "数据库操作失败");
			  request.setAttribute("result", "failure");
			  return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//锁库存
			if(!service.updateCargoProductStockCount(outCargoProductStock.getId(), -cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soAddProduct");
			}
			if(!service.updateCargoProductStockLockCount(outCargoProductStock.getId(), cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soAddProduct");
			}
			if(inCargoInfo.getAreaId() != outCargoInfo.getAreaId() || inCargoInfo.getStockType() != outCargoInfo.getStockType()){
				CargoInfoAreaBean area=service.getCargoInfoArea("id="+outCargoInfo.getAreaId());
				ProductStockBean productStock = productService.getProductStock("product_id=" + cartonningProduct.getProductId()+" and area="+area.getOldId()+" and type="+outCargoInfo.getStockType());
				if(productStock == null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "找不到商品库存信息！");
					return mapping.findForward("soAddProduct");
				}
				if(!productService.updateProductLockCount(productStock.getId(), cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库冻结库存不足！");
					return mapping.findForward("soAddProduct");
				}
				if(!productService.updateProductStockCount(productStock.getId(), -cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库库存不足！");
					return mapping.findForward("soAddProduct");
				}
			}
			
			//合格库待作业任务处理
			CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=2");
			if(cot!=null){
				if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			
			service.getDbOp().commitTransaction();   //提交事务
			request.setAttribute("result", "补货单:" + code + "已创建成功!");
			}
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			request.setAttribute("result", "操作失败,系统异常！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			service.releaseAll();
			wareService.releaseAll();
			productService.releaseAll();
		}
		return mapping.findForward("soAddProduct");
	}
	/**
	 * 调拨作业
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward createDeploy(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode"));//装箱单条码
		String operationArea = StringUtil.convertNull((String)request.getSession().getAttribute("operationArea"));//作业区域 SJ为散件区作业ZJ为整件区作业
		String operationType = StringUtil.convertNull((String)request.getSession().getAttribute("operationType"));//作业类型KC为跨仓作业BD为本地作业
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService();
		IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			synchronized (cargoLock) {
			CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
			if(cartonningInfo==null){
				request.setAttribute("result", "未找到该装箱单！");
				return mapping.findForward("soCreateDeploy");
			}
			if(cartonningInfo.getStatus()==CartonningInfoBean.STATUS2){
				request.setAttribute("result", "该装箱单已作废！");
				return mapping.findForward("soCreateDeploy");
			}
			CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
			if(cargoOperation != null){
				if(cargoOperation.getStatus()!= 8 && cargoOperation.getStatus()!= 17 && cargoOperation.getStatus()!= 26 && cargoOperation.getStatus()!= 35
						&& cargoOperation.getStatus()!= 9 && cargoOperation.getStatus()!= 18 && cargoOperation.getStatus()!= 27 && cargoOperation.getStatus()!= 36
						&& cargoOperation.getEffectStatus()!=3 && cargoOperation.getEffectStatus()!=4){
					request.setAttribute("result", "此装箱单关联的作业单为未完成状态！");
					return mapping.findForward("soCreateDeploy");
				}
			}
			CargoInfoBean cargoInfo=service.getCargoInfo("id="+cartonningInfo.getCargoId());
			if(cargoInfo==null){
				request.setAttribute("result", "未找到装箱单关联货位！");
				return mapping.findForward("soCreateDeploy");
			}
			int areaId = Integer.parseInt(request.getSession().getAttribute("area").toString());
			if(cargoInfo.getAreaId()!=areaId){
				request.setAttribute("result", "关联货位不属于这个仓库！");
				return mapping.findForward("soCreateDeploy");
			}
			CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
			if(cartonningProduct==null){
				request.setAttribute("result", "根据此装箱单号找不到库存信息");
				return mapping.findForward("soCreateDeploy");
			}
			voProduct product = wareService.getProduct(cartonningProduct.getProductId());
			if(product == null){
				request.setAttribute("result", "找不到商品的相关信息!");
				return mapping.findForward("soCreateDeploy");
			}
			//新采购计划编号：CGJIH20090601001
			String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");   
			//生成编号
			CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
			if(cargoOper == null){
				code = code + "00001";
			}else{//获取当日计划编号最大值
				String _code = cargoOper.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-5));
				number++;
				code += String.format("%05d",new Object[]{new Integer(number)});
			}
			//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
			String storageCode = "";
			if(areaId == 1){
				storageCode ="GZF";
			}else if(areaId == 3){
				storageCode ="GZZ";
			}
			//添加作业单
			cargoOper = new CargoOperationBean();
			cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS30);
			cargoOper.setCreateDatetime(DateUtil.getNow());
			cargoOper.setRemark("");
			cargoOper.setCreateUserId(user.getId());
			cargoOper.setAuditingDatetime(DateUtil.getNow());
			cargoOper.setAuditingUserId(user.getId());
			cargoOper.setCode(code);
			cargoOper.setSource("");
			cargoOper.setStorageCode(storageCode);
			if("ZJ".equals(operationArea)){
				cargoOper.setStockInType(CargoInfoBean.STORE_TYPE1);
				cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE1);
			}
			if("SJ".equals(operationArea)){
				cargoOper.setStockInType(CargoInfoBean.STORE_TYPE0);
				cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE0);
			}
			if("HH".equals(operationArea)){
				cargoOper.setStockInType(CargoInfoBean.STORE_TYPE4);
				cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE4);
			}
			cargoOper.setCreateUserName(user.getUsername());
			cargoOper.setAuditingUserName(user.getUsername());
			cargoOper.setType(CargoOperationBean.TYPE3);
			cargoOper.setLastOperateDatetime(DateUtil.getNow());
			cargoOper.setConfirmDatetime(DateUtil.getNow());
			
			service.getDbOp().startTransaction();   //开启事务
			if(!service.addCargoOperation(cargoOper)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加调拨单，数据库操作失败！");
				return mapping.findForward("soCreateDeploy");
			}
			int operId = service.getDbOp().getLastInsertId();
			String inCargoWholeCode = "";
			if("BD".equals(operationType)){//本地调拨
				if("ZJ".equals(operationArea)){//整件区调拨
					if(cargoInfo.getStoreType() != 1){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "此装箱单货位不是整件区货位!");
						return mapping.findForward("soCreateDeploy");
					}
					//选择合适的目的货位
					List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=1  and ci.status=0 and ci.stock_type=0 and ci.id != "+ cargoInfo.getId() +" and cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + areaId, -1, -1, "stock_count DESC");
					if(cargoAndProductStockList.size() > 0){
						inCargoWholeCode = ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo().getWholeCode();
					}else{
						voProductLine productLine=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
						if(productLine==null){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "产品："+product.getId()+"产品线未知");
							return mapping.findForward("soAddProduct");
						}
						List cargoList = service.getCargoInfoList("store_type=1 and stock_type=0 and product_line_id=" + productLine.getId()+" and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=0", -1, -1, null);
						if(cargoList.size() > 0){
							inCargoWholeCode = ((CargoInfoBean)cargoList.get(0)).getWholeCode();
						}else{//没有对应产品线的货位，全仓随机选取货位
							List allCargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE1 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId(), -1, -1, null);
							if(allCargoList.size()>0){
								Random ran=new Random();
								inCargoWholeCode=((CargoInfoBean)(allCargoList.get(ran.nextInt(allCargoList.size())))).getWholeCode();
							}else{
								service.getDbOp().rollbackTransaction();
								request.setAttribute("result", "没有找到合适的货位！");
								return mapping.findForward("soCreateDeploy");
							}
						}
					}
				}else if("SJ".equals(operationArea)){//散件区调拨
					if(cargoInfo.getStoreType() != 0){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "此装箱单货位不是散件区货位!");
						return mapping.findForward("soCreateDeploy");
					}
					List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=0 and ci.status=0 and ci.stock_type=0 and ci.id != "+ cargoInfo.getId() +" and  cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + areaId, -1, -1, "stock_count DESC");
					if(cargoAndProductStockList.size() > 0){
						inCargoWholeCode = ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo().getWholeCode();
						//合格库待作业任务处理
						CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
						CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
						CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=3");
						if(cot!=null){
							if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}else{
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "没有找到合适的散件区目的货位！");
						return mapping.findForward("soCreateDeploy");
					}
				}else if("HH".equals(operationArea)){//混合区调拨
					if(cargoInfo.getStoreType() != 4){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "此装箱单货位不是混合区货位!");
						return mapping.findForward("soCreateDeploy");
					}
					List cargoInfoList = service.getCargoInfoBeanist("ci.store_type=4 and ci.status=0 and ci.stock_type=0  and ci.id != "+ cargoInfo.getId() +" and cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + areaId, -1, -1, "stock_count DESC");
					if(cargoInfoList.size() > 0){
						//找一个容积差最 小 的货位
						ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id=" + cartonningProduct.getProductId());
						if( pwpBean == null ) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "装箱单中商品"+ cartonningProduct.getProductCode()+ "没有商品物流属性！");
							return mapping.findForward("soCreateDeploy");
						}
						CargoInfoBean ciBean = cartonningService.getMinLeftVolumeCargo(cargoInfoList, (pwpBean.calculateVolume() * cartonningProduct.getProductCount() ));
						if( ciBean == null ) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "没有找到合适的货位！");
							return mapping.findForward("soCreateDeploy");
						}
						inCargoWholeCode = ciBean.getWholeCode();
						
						//合格库待作业任务处理
						CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
						CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
						CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=3");
						if(cot!=null){
							if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}else{//全仓随机选取货位
						List allCargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId(), -1, -1, null);
						if(allCargoList.size()>0){
							Random ran=new Random();
							inCargoWholeCode=((CargoInfoBean)(allCargoList.get(ran.nextInt(allCargoList.size())))).getWholeCode();
						}else{
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "没有找到合适的货位！");
							return mapping.findForward("soCreateDeploy");
						}
					}
				}
					
			}else if("KC".equals(operationType)){//跨仓调拨
				int KCArea = Integer.parseInt(request.getSession().getAttribute("area").toString());
				//3是增城4是无锡 如果选择的是增城 那就把目的仓库设成无锡 反之
				int inArea = 0;
				if(KCArea == 3 ){
					inArea = 4;
				}else if(KCArea == 4){
					inArea = 3;
				}else{
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "目前只支持增城与无锡之间的跨仓调拨!");
					return mapping.findForward("soCreateDeploy");
				}
				if("ZJ".equals(operationArea)){//整件区调拨
					if(cargoInfo.getStoreType() != 1){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "此装箱单货位不是整件区货位!");
						return mapping.findForward("soCreateDeploy");
					}
					List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=1 and ci.status=0 and ci.stock_type=0 and cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + inArea, -1, -1, "stock_count DESC");
					if(cargoAndProductStockList.size() > 0){
						inCargoWholeCode = ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo().getWholeCode();
					}else{
						voProductLine productLine=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
						if(productLine==null){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "产品："+product.getId()+"产品线未知");
							return mapping.findForward("soAddProduct");
						}
						List cargoList = service.getCargoInfoList("store_type=1 and stock_type=0 and product_line_id=" + productLine.getId()+" and area_id="+inArea+" and stock_type=0 and status=0", -1, -1, null);
						if(cargoList.size() > 0){
							inCargoWholeCode = ((CargoInfoBean)cargoList.get(0)).getWholeCode();
						}else{//没有对应产品线的货位，全仓随机选择货位
							List allCargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE1 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId(), -1, -1, null);
							if(allCargoList.size()>0){
								Random ran=new Random();
								inCargoWholeCode=((CargoInfoBean)(allCargoList.get(ran.nextInt(allCargoList.size())))).getWholeCode();
							}else{
								service.getDbOp().rollbackTransaction();
								request.setAttribute("result", "没有找到合适的货位！");
								return mapping.findForward("soCreateDeploy");
							}
						}
					}
				}else if("SJ".equals(operationArea)){//散件区调拨
					if(cargoInfo.getStoreType() != 0){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "此装箱单货位不是散件区货位!");
						return mapping.findForward("soCreateDeploy");
					}
					List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=0 and ci.status=0 and ci.stock_type=0 and cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + inArea, -1, -1, "stock_count DESC");
					if(cargoAndProductStockList.size() > 0){
						inCargoWholeCode = ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo().getWholeCode();
						
						//合格库待作业任务处理
						CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
						CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
						CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=3");
						if(cot!=null){
							if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}else{
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "没有找到合适的散件区目的货位！");
						return mapping.findForward("soCreateDeploy");
					}
				}else if("HH".equals(operationArea)){//混合区调拨
					if(cargoInfo.getStoreType() != 4){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "此装箱单货位不是混合区货位!");
						return mapping.findForward("soCreateDeploy");
					}
					List cargoInfoList = service.getCargoInfoBeanist("ci.store_type=4 and ci.status=0 and ci.stock_type=0 and cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + inArea, -1, -1, "stock_count DESC");
					if(cargoInfoList.size() > 0){
						//找一个容积差最小的货位
						ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id=" + cartonningProduct.getProductId());
						if( pwpBean == null ) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "装箱单中商品"+ cartonningProduct.getProductCode()+ "没有商品物流属性！");
							return mapping.findForward("soCreateDeploy");
						}
						CargoInfoBean ciBean = cartonningService.getMinLeftVolumeCargo(cargoInfoList, (pwpBean.calculateVolume() * cartonningProduct.getProductCount() ));
						if( ciBean == null ) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "没有找到合适的货位！");
							return mapping.findForward("soCreateDeploy");
						}
						inCargoWholeCode = ciBean.getWholeCode();
						
						//合格库待作业任务处理
						CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
						CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
						CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=3");
						if(cot!=null){
							if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}else{//全仓随机选择货位
						List allCargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId(), -1, -1, null);
						if(allCargoList.size()>0){
							Random ran=new Random();
							inCargoWholeCode=((CargoInfoBean)(allCargoList.get(ran.nextInt(allCargoList.size())))).getWholeCode();
						}else{
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "没有找到合适的货位！");
							return mapping.findForward("soCreateDeploy");
						}
					}
				}
			}
			CargoInfoBean inCargoInfo = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
			CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
			CargoProductStockBean inCargoProductStock = service.getCargoProductStock("cargo_id=" + inCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
			if(inCargoProductStock==null){
				CargoInfoBean icf = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
				inCargoProductStock = new CargoProductStockBean();
				inCargoProductStock.setCargoId(icf.getId());
				inCargoProductStock.setProductId(cartonningProduct.getProductId());
				inCargoProductStock.setStockCount(0);
				inCargoProductStock.setStockLockCount(0);
				if(!service.addCargoProductStock(inCargoProductStock)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "关联货位信息，数据库操作失败!");
					return mapping.findForward("soCreateDeploy");
				}
				inCargoProductStock.setId(dbOp.getLastInsertId());
			}
			CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
				
			CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(0);
			bean.setInCargoWholeCode("");
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(1);
			bean.setUseStatus(0);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加补货单详细信息，数据库操作失败!");
				return mapping.findForward("soCreateDeploy");
			}
			bean.setOperId(operId);
			bean.setProductId(cartonningProduct.getProductId());
			bean.setInCargoProductStockId(inCargoProductStock.getId());
			bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
			bean.setStockCount(cartonningProduct.getProductCount());
			bean.setType(0);
			bean.setUseStatus(1);
			if(!service.addCargoOperationCargo(bean)){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "添加补货单详细信息，数据库操作失败!");
				return mapping.findForward("soCreateDeploy");
			}
			if(!cartonningService.updateCartonningInfo("oper_id=" + operId, "code='" + cartonningCode + "'")){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "关联装箱单和调拨单，数据库操作失败!");
				return mapping.findForward("soCreateDeploy");
			}
			//锁库存
			if(!service.updateCargoProductStockCount(outCargoProductStock.getId(), -cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soCreateDeploy");
			}
			if(!service.updateCargoProductStockLockCount(outCargoProductStock.getId(), cartonningProduct.getProductCount())){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "操作失败，货位冻结库存不足！");
				return mapping.findForward("soCreateDeploy");
			}
			if(inCargoInfo.getAreaId() != outCargoInfo.getAreaId() || inCargoInfo.getStockType() != outCargoInfo.getStockType()){
				CargoInfoAreaBean area=service.getCargoInfoArea("id="+outCargoInfo.getAreaId());
				ProductStockBean productStock = productService.getProductStock("product_id=" + cartonningProduct.getProductId()+" and area="+area.getOldId()+" and type="+outCargoInfo.getStockType());
				if(productStock == null){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "找不到商品库存信息！");
					return mapping.findForward("soCreateDeploy");
				}
				if(!productService.updateProductLockCount(productStock.getId(), cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库冻结库存不足！");
					return mapping.findForward("soCreateDeploy");
				}
				if(!productService.updateProductStockCount(productStock.getId(), -cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "操作失败，合格库库存不足！");
					return mapping.findForward("soCreateDeploy");
				}
			}
			service.getDbOp().commitTransaction();  //提交事务
			request.setAttribute("result", "调拨单:" + code + "已创建成功!");
			}
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			request.setAttribute("result", "操作失败,系统异常！");
			return mapping.findForward("soCreateDeploy");
		}finally{
			service.releaseAll();
			wareService.releaseAll();
			productService.releaseAll();
		}
		return mapping.findForward("soCreateDeploy");
	}
	/**
	 * 交接作业完成
	 */
	public ActionForward operationComplete(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response)
	throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		CartonningInfoService CIService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService prudoctService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode"));
		String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
		int area = StringUtil.toInt(request.getSession().getAttribute("area").toString());
		synchronized (cargoLock) {
			try{
				service.getDbOp().startTransaction();
				CartonningInfoBean cartonningInfo=null;
				CargoOperationBean cargoOperation=null;
				if(!cartonningCode.equals("")&&cargoCode.equals("")){//只扫描了装箱单号
					cartonningInfo=CIService.getCartonningInfo("code='"+cartonningCode+"'");
					if(cartonningInfo==null){
						request.setAttribute("result", "装箱单号不正确！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					CargoInfoBean cargoInfo=service.getCargoInfo("id="+cartonningInfo.getCargoId());
					if(cargoInfo==null){
						request.setAttribute("result", "装箱单未关联货位！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					if(area != cargoInfo.getAreaId()){
						request.setAttribute("result", "装箱单关联货位不属于选择的仓库！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					cargoOperation=service.getCargoOperation("id="+cartonningInfo.getOperId());
					if(cargoOperation==null){
						request.setAttribute("result", "装箱单未关联作业单！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+cartonningInfo.getOperId()+" and in_cargo_product_stock_id>0");
					if(coc==null){
						request.setAttribute("result", "作业单信息错误！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					request.setAttribute("inCiCode", coc.getInCargoWholeCode());
					request.setAttribute("result", "装箱单号"+cartonningInfo.getCode());
					request.setAttribute("next", "cargoCode");//页面应该扫描装箱单号
					return mapping.findForward("soOperationComplete");
				}else if(!cartonningCode.equals("")&&!cargoCode.equals("")){//扫描了装箱单号和货位号
					cartonningInfo=CIService.getCartonningInfo("code='"+cartonningCode+"'");//扫描的装箱单
					if(cartonningInfo==null){
						request.setAttribute("result", "装箱单号不正确！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					CargoInfoBean cargoInfo=service.getCargoInfo("whole_code='"+cargoCode+"'");//扫描的目的货位
					if(cargoInfo==null){
						request.setAttribute("result", cargoCode+",该货位不存在！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					if(area != cargoInfo.getAreaId()){
						request.setAttribute("result", cargoCode+",该货位不属于选择的仓库！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描货位号
						return mapping.findForward("soOperationComplete");
					}
					//判断货位与装箱单关联作业单的目的货位是否一致
					cargoOperation=service.getCargoOperation("id="+cartonningInfo.getOperId());//作业单
					if(cargoOperation==null){
						request.setAttribute("result", "装箱单未关联作业单！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+cartonningInfo.getOperId()+" and in_cargo_product_stock_id>0");
					if(coc==null){
						request.setAttribute("result", "作业单信息错误！");
						request.setAttribute("next", "cartonningCode");//页面应该扫描装箱单号
						return mapping.findForward("soOperationComplete");
					}
					if(!cargoCode.equals(coc.getInCargoWholeCode())){//扫描的货位号和作业单目的货位号不同
						CargoProductStockBean inCps=service.getCargoProductStock("cargo_id="+cargoInfo.getId()+" and product_id="+coc.getProductId());
						if(inCps==null){//目的货位上没有该商品库存记录，则添加库存记录
							CargoProductStockBean newCps=new CargoProductStockBean();
							newCps.setCargoId(cargoInfo.getId());
							newCps.setProductId(coc.getProductId());
							newCps.setStockCount(0);
							newCps.setStockLockCount(0);
							service.addCargoProductStock(newCps);
							inCps=newCps;
							inCps.setId(service.getDbOp().getLastInsertId());
							if(cargoInfo.getStatus()!=0){
								service.updateCargoInfo("status=0", "id="+cargoInfo.getId());
							}
						}
						service.updateCargoOperationCargo("in_cargo_product_stock_id="+inCps.getId()+",in_cargo_whole_code='"+cargoInfo.getWholeCode()+"'", "oper_id="+cartonningInfo.getOperId()+" and in_cargo_product_stock_id>0");
					}
				}
				int status = cargoOperation.getStatus();
				if(status == 1 || status == 2 || status == 7 ||status == 8 || status == 9
						|| status == 10 || status == 11 || status == 16 || status == 17 || status == 18 
							||status == 19 || status == 20 || status == 25 || status == 26 || status == 27 
								|| status == 28 || status == 29 || status == 34 || status == 35 || status == 36){
					request.setAttribute("result", "作业单状态不正确！");
					return mapping.findForward("soOperationComplete");
				}
				
				List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>(); //存放财务接口数据
				
				int type = cargoOperation.getType();
				int operId = cargoOperation.getId();
				if(type == 0){	//上架
					if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS7){
						request.setAttribute("result", "该作业单状态已修改，操作失败！");
						return mapping.findForward("soOperationComplete");
					}
					
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
									request.setAttribute("result", "商品"+product.getCode()+"货位"+outCoc.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败!");
			                    	return mapping.findForward("soOperationComplete");
								}
								CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
								if(!service.updateCargoProductStockCount(temp_inCps.getId(), outCoc.getStockCount())){
			                    	service.getDbOp().rollbackTransaction();
									request.setAttribute("result", "货位库存操作失败，货位冻结库存不足！");
			                    	return mapping.findForward("soOperationComplete");
			                    }
								if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
			                    	service.getDbOp().rollbackTransaction();
									request.setAttribute("result", "货位库存操作失败，货位冻结库存不足！");
			                    	return mapping.findForward("soOperationComplete");
			                    }
								//调整合格库库存
								if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){
									CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+outCi.getAreaId());//目的货位地区
									CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+inCi.getAreaId());//源货位地区
									
									ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+outCi.getStockType());
									ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+inCi.getStockType());
									
									if(psIn==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if(psOut==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									
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
									List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId(), -1, -1, "id asc");
									double stockinPrice = 0;
									double stockoutPrice = 0;
									if(sbList!=null&&sbList.size()!=0){
										int stockExchangeCount = inCoc.getStockCount();
										int index = 0;
										int stockBatchCount = 0;
										do {//出库
											StockBatchBean batch = (StockBatchBean)sbList.get(index);
											if(stockExchangeCount>=batch.getBatchCount()){
												if(!stockService.deleteStockBatch("id="+batch.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
												stockBatchCount = batch.getBatchCount();
											}else{
												if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
												stockBatchCount = stockExchangeCount;
											}
											StockBatchLogBean batchLog = new StockBatchLogBean();//添加批次操作记录
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
											StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId());
											if(batchBean!=null){
												if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
											}else{
												StockBatchBean newBatch = new StockBatchBean();
												newBatch.setCode(batch.getCode());
												newBatch.setProductId(batch.getProductId());
												newBatch.setPrice(batch.getPrice());
												newBatch.setBatchCount(stockBatchCount);
												newBatch.setProductStockId(psIn.getId());
												newBatch.setStockArea(outCi.getAreaId());
												newBatch.setStockType(psIn.getType());
												newBatch.setSupplierId(batch.getSupplierId());
												newBatch.setTax(batch.getTax());
												newBatch.setNotaxPrice(batch.getNotaxPrice());
												newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
												if(!stockService.addStockBatch(newBatch)){
													request.setAttribute("tip", "添加失败！");
													request.setAttribute("result", "failure");
													service.getDbOp().rollbackTransaction();
													return mapping.findForward(IConstants.FAILURE_KEY);
												}
											}
											batchLog = new StockBatchLogBean();//添加批次操作记录
											batchLog.setCode(cargoOperation.getCode());
											batchLog.setStockType(psIn.getType());
											batchLog.setStockArea(outCi.getAreaId());
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
									
									StockCardBean sc = new StockCardBean();// 入库卡片\添加进销存卡片开始\批次修改结束
									sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
									sc.setCode(cargoOperation.getCode());
									sc.setCreateDatetime(DateUtil.getNow());
									sc.setStockType(outCi.getStockType());
									sc.setStockArea(outCi.getAreaId());
									sc.setProductId(inCps.getProductId());
									sc.setStockId(psIn.getId());
									sc.setStockInCount(inCoc.getStockCount());
									sc.setStockInPriceSum(product.getPrice5());
									sc.setCurrentStock(product.getStock(outCi.getAreaId(), sc.getStockType())+ product.getLockCount(outCi.getStockAreaId(), sc.getStockType()));
									sc.setStockAllArea(product.getStock(outCi.getAreaId())+ product.getLockCount(outCi.getAreaId()));
									sc.setStockAllType(product.getStockAllType(sc.getStockType())+ product.getLockCountAllType(sc.getStockType()));
									sc.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc.setStockPrice(product.getPrice5());// 新的库存价格
									sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
											new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									psService.addStockCard(sc);
									
									StockCardBean sc2 = new StockCardBean();// 出库卡片
									sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									sc2.setCode(cargoOperation.getCode());
									sc2.setCreateDatetime(DateUtil.getNow());
									sc2.setStockType(inCi.getStockType());
									sc2.setStockArea(inCi.getAreaId());
									sc2.setProductId(product.getId());
									sc2.setStockId(psOut.getId());
									sc2.setStockOutCount(inCoc.getStockCount());
									sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									sc2.setCurrentStock(product.getStock(inCi.getAreaId(), sc2.getStockType())+ product.getLockCount(inCi.getAreaId(), sc2.getStockType()));
									sc2.setStockAllArea(product.getStock(inCi.getAreaId())+ product.getLockCount(inCi.getAreaId()));
									sc2.setStockAllType(product.getStockAllType(sc2.getStockType())+ product.getLockCountAllType(sc2.getStockType()));
									sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc2.setStockPrice(product.getPrice5());
									sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
											new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
									psService.addStockCard(sc2);
									//添加进销存卡片结束
								}
								temp_inCps = service.getCargoProductStock("id = "+temp_inCps.getId());	//货位入库卡片
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELFSTOCKIN);
								csc.setCode(cargoOperation.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(outCi.getStockType());
								csc.setStockArea(outCi.getAreaId());
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
								
								//合格库待作业任务处理
								List cartonningList=CIService.getCartonningList("oper_id="+operId+" and status<>2", -1, -1, null);
								for(int c=0;c<cartonningList.size();c++){
									CartonningInfoBean cartonning=(CartonningInfoBean)cartonningList.get(c);
									CartonningProductInfoBean cartonningProduct=CIService.getCartonningProductInfo("cartonning_id="+cartonning.getId());
									if(cartonningProduct==null){
										continue;
									}
									if(cartonningProduct.getProductId()==outCoc.getProductId()){
										CargoOperationTodoBean cot=CIService.getCargoOperationTodo("product_id="+cartonning.getId()+" and status in(0,1,2) and type=0");
										if(cot!=null){
											CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
										}
									}
								}
								
							}
							inCps = service.getCargoProductStock("id = "+inCps.getId());//货位出库卡片
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
						}
					CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
					CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS7);//下个阶段
					if(process==null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作业单流程信息错误！");
		               	request.setAttribute("result", "failure");
		    			return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(process2==null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作业单流程信息错误！");
		               	request.setAttribute("result", "failure");
		    			return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//修改上一操作日志的时效
					CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
					if(lastLog != null && lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
						int effectTime=process.getEffectTime();//上阶段时效
						String lastOperateTime=lastLog.getOperDatetime();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long date1=sdf.parse(lastOperateTime).getTime();
						long date2=sdf.parse(DateUtil.getNow()).getTime();
						if(date1+effectTime*60*1000<date2){//已超时
							service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId());
						}
					}
					if(!service.updateCargoOperation(
							"status="+CargoOperationProcessBean.OPERATION_STATUS8+"" +
									",effect_status="+CargoOperationBean.EFFECT_STATUS2+"" +
											",last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
													",complete_datetime='"+DateUtil.getNow()+"'" +
															",complete_user_id="+user.getId()+"" +
																	",complete_user_name='"+user.getUsername()+"'", "id="+operId)){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "更新上架单状态失败！");
		               	request.setAttribute("result", "failure");
		    			return mapping.findForward(IConstants.FAILURE_KEY);
					}
					CargoOperLogBean operLog=new CargoOperLogBean();
					operLog.setOperName(process2.getOperName());
					operLog.setOperDatetime(DateUtil.getNow());
					operLog.setOperAdminId(user.getId());
					operLog.setOperAdminName(user.getUsername());
					operLog.setHandlerCode("");
					operLog.setEffectTime(2);
					operLog.setRemark("");
					operLog.setOperCode(cargoOperation.getCode());
					operLog.setPreStatusName(process.getStatusName());
					operLog.setNextStatusName(process2.getStatusName());
					service.addCargoOperLog(operLog);
					
					//修改相关调拨单关联的货位，改为关联目的货位
					CartonningInfoBean cartonning=CIService.getCartonningInfo("oper_id="+operId);
					if(cartonning!=null){
						CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+operId+" and in_cargo_product_stock_id>0");
						CargoInfoBean cargo=service.getCargoInfo("whole_code='"+coc.getInCargoWholeCode()+"'");
						if(cargo!=null){
							if(!CIService.updateCartonningInfo("cargo_id="+cargo.getId(), "id="+cartonning.getId())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
				               	request.setAttribute("result", "failure");
				    			return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}
					//作废此装箱单
					if(!CIService.updateCartonningInfo(" status=2", " id=" + cartonningInfo.getId())){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作废装箱单操作失败 !");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//物流员工绩效考核操作
					CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
					CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
					ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
					CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
					if(csBean == null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "此账号不是物流员工 !");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
		    		CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=5 and staff_id=" + csBean.getId() );
		    		int operCount = 1;
		    		int performanceProductCount = cartonningProduct.getProductCount();
		    		if(cspBean != null){
		    			performanceProductCount = performanceProductCount + cspBean.getProductCount();
		    			operCount = operCount + cspBean.getOperCount();
						boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
						if(!flag){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "物流员工绩效考核更新操作失败 !");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}else{
						CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
						newBean.setDate(date);
						newBean.setProductCount(performanceProductCount);
						newBean.setOperCount(operCount);
						newBean.setStaffId(csBean.getId());
						newBean.setType(5);  //5代表完成上架作业
						boolean flag = cargoService.addCargoStaffPerformance(newBean);
						if(!flag){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "物流员工绩效考核添加操作失败 !");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
				//下架
				}else if(type == 1){
					if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS16){
						request.setAttribute("tip", "该作业单未审核通过，操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
						List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
						for(int i=0;i<inCocList.size();i++){
							CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
							CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
							CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
							voProduct product = wareService.getProduct(inCoc.getProductId());
							product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
							int stockOutCount = 0;
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
								if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){//调整合格库库存
									CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+outCi.getAreaId());//目的货位地区
									CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+inCi.getAreaId());//源货位地区
									
									ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+outCi.getStockType());
									ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+inCi.getStockType());
									
									if(psIn==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if(psOut==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									
									//组装财务接口需要的数据
									BaseProductInfo baseProductInfo = new BaseProductInfo();
									baseProductInfo.setId(inCoc.getProductId());
									baseProductInfo.setProductStockOutId(psOut.getId());
									baseProductInfo.setProductStockId(psIn.getId());
									baseProductInfo.setOutCount(inCoc.getStockCount());
									baseList.add(baseProductInfo);
									
									//更新批次记录、添加调拨出、入库批次记录 //批次修改开始
									/**
									List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId(), -1, -1, "id asc");
									double stockinPrice = 0;
									double stockoutPrice = 0;
									if(sbList!=null&&sbList.size()!=0){
										int stockExchangeCount = inCoc.getStockCount();
										int index = 0;
										int stockBatchCount = 0;
										do {//出库
											StockBatchBean batch = (StockBatchBean)sbList.get(index);
											if(stockExchangeCount>=batch.getBatchCount()){
												if(!stockService.deleteStockBatch("id="+batch.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
												stockBatchCount = batch.getBatchCount();
											}else{
												if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
												stockBatchCount = stockExchangeCount;
											}
											StockBatchLogBean batchLog = new StockBatchLogBean();	//添加批次操作记录
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
											stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();//入库
											StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId());
											if(batchBean!=null){
												if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
											}else{
												StockBatchBean newBatch = new StockBatchBean();
												newBatch.setCode(batch.getCode());
												newBatch.setProductId(batch.getProductId());
												newBatch.setPrice(batch.getPrice());
												newBatch.setBatchCount(stockBatchCount);
												newBatch.setProductStockId(psIn.getId());
												newBatch.setStockArea(outCi.getAreaId());
												newBatch.setStockType(psIn.getType());
												newBatch.setSupplierId(batch.getSupplierId());
												newBatch.setTax(batch.getTax());
												newBatch.setNotaxPrice(batch.getNotaxPrice());
												newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
												if(!stockService.addStockBatch(newBatch)){
													request.setAttribute("tip", "添加失败！");
													request.setAttribute("result", "failure");
													service.getDbOp().rollbackTransaction();
													return mapping.findForward(IConstants.FAILURE_KEY);
												}
											}
											batchLog = new StockBatchLogBean();//添加批次操作记录
											batchLog.setCode(cargoOperation.getCode());
											batchLog.setStockType(psIn.getType());
											batchLog.setStockArea(outCi.getAreaId());
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
									
									StockCardBean sc = new StockCardBean();//批次修改结束//添加进销存卡片开始	// 入库卡片
									sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
									sc.setCode(cargoOperation.getCode());
									sc.setCreateDatetime(DateUtil.getNow());
									sc.setStockType(outCi.getStockType());
									sc.setStockArea(outCi.getAreaId());
									sc.setProductId(inCps.getProductId());
									sc.setStockId(psIn.getId());
									sc.setStockInCount(inCoc.getStockCount());
									sc.setStockInPriceSum(0);
									sc.setCurrentStock(product.getStock(outCi.getAreaId(), sc.getStockType())
											+ product.getLockCount(outCi.getStockAreaId(), sc.getStockType()));
									sc.setStockAllArea(product.getStock(outCi.getAreaId())
											+ product.getLockCount(outCi.getAreaId()));
									sc.setStockAllType(product.getStockAllType(sc.getStockType())
											+ product.getLockCountAllType(sc.getStockType()));
									sc.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc.setStockPrice(product.getPrice5());// 新的库存价格
									sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
											new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									psService.addStockCard(sc);
									StockCardBean sc2 = new StockCardBean();	// 出库卡片
									sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									sc2.setCode(cargoOperation.getCode());
									sc2.setCreateDatetime(DateUtil.getNow());
									sc2.setStockType(inCi.getStockType());
									sc2.setStockArea(inCi.getAreaId());
									sc2.setProductId(product.getId());
									sc2.setStockId(psOut.getId());
									sc2.setStockOutCount(inCoc.getStockCount());
									sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
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
									psService.addStockCard(sc2);//添加进销存卡片结束
								}
								temp_inCps = service.getCargoProductStock("id = "+temp_inCps.getId());	//货位入库卡片
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_DOWNSHELFSTOCKIN);
								csc.setCode(cargoOperation.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(outCi.getStockType());
								csc.setStockArea(outCi.getAreaId());
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
								
								//合格库待作业任务处理
								CargoOperationTodoBean cot=CIService.getCargoOperationTodo("cargo_product_stock_id="+outCps.getId()+" and status in(0,1,2) and type=1");
								if(cot!=null){
									CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
								}
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
					CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
					CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS16);//下个阶段
					if(process==null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作业单流程信息错误！");
			            request.setAttribute("result", "failure");
			    		return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(process2==null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作业单流程信息错误！");
			            request.setAttribute("result", "failure");
			    		return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//修改上一操作日志的时效
					CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
					if(lastLog != null && lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
						int effectTime=process.getEffectTime();//上阶段时效
						String lastOperateTime=lastLog.getOperDatetime();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long date1=sdf.parse(lastOperateTime).getTime();
						long date2=sdf.parse(DateUtil.getNow()).getTime();
						if(date1+effectTime*60*1000<date2){//已超时
							service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
						}
					}
					if(!service.updateCargoOperation(
							"status="+CargoOperationProcessBean.OPERATION_STATUS17+"" +
									",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
											",complete_datetime='"+DateUtil.getNow()+"'" +
													",complete_user_id="+user.getId()+"" +
															",complete_user_name='"+user.getUsername()+"'", "id="+operId)){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "更新下架单状态，数据库操作失败！");
			            request.setAttribute("result", "failure");
			    		return mapping.findForward(IConstants.FAILURE_KEY);
					}
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
					
					//修改相关调拨单关联的货位，改为关联目的货位
					CartonningInfoBean cartonning=CIService.getCartonningInfo("oper_id="+operId);
					if(cartonning!=null){
						CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+operId+" and in_cargo_product_stock_id>0");
						CargoInfoBean cargo=service.getCargoInfo("whole_code='"+coc.getInCargoWholeCode()+"'");
						if(cargo!=null){
							if(!CIService.updateCartonningInfo("cargo_id="+cargo.getId(), "id="+cartonning.getId())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
					            request.setAttribute("result", "failure");
					    		return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}
				}else if(type == 2){//补货
					if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS25){
						request.setAttribute("tip", "该作业单状态已修改，操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
					for(int i=0;i<inCocList.size();i++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
						CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
						CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
						voProduct product = wareService.getProduct(inCoc.getProductId());
						product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
						int stockInCount = 0;
						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
						for(int j=0;j<outCocList.size();j++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
							CargoInfoBean outCi = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
							if(!service.updateCargoProductStockCount(inCps.getId(), outCoc.getStockCount())){
								request.setAttribute("tip", "操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
								request.setAttribute("tip", "操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){	//调整合格库库存
								//CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
								CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+inCi.getAreaId());//目的货位地区
								CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+outCi.getAreaId());//源货位地区
								
								ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+inCi.getStockType());
								ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+outCi.getStockType());
								
								if(psIn==null){
									service.getDbOp().rollbackTransaction();
									request.setAttribute("tip", "合格库库存数据错误！");
									request.setAttribute("result", "failure");
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if(psOut==null){
									request.setAttribute("tip", "合格库库存数据错误！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								
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
									do {//出库
										StockBatchBean batch = (StockBatchBean)sbList.get(index);
										if(stockExchangeCount>=batch.getBatchCount()){
											if(!stockService.deleteStockBatch("id="+batch.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                service.getDbOp().rollbackTransaction();
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
											stockBatchCount = batch.getBatchCount();
										}else{
											if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                service.getDbOp().rollbackTransaction();
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
											stockBatchCount = stockExchangeCount;
										}	//添加批次操作记录
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
								                service.getDbOp().rollbackTransaction();
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
											newBatch.setSupplierId(batch.getSupplierId());
											newBatch.setTax(batch.getTax());
											newBatch.setNotaxPrice(batch.getNotaxPrice());
											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
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
								sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
								sc2.setCode(cargoOperation.getCode());
								sc2.setCreateDatetime(DateUtil.getNow());
								sc2.setStockType(outCi.getStockType());
								sc2.setStockArea(outCi.getAreaId());
								sc2.setProductId(product.getId());
								sc2.setStockId(psOut.getId());
								sc2.setStockOutCount(inCoc.getStockCount());
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
							//货位出库卡片
							outCps = service.getCargoProductStock("id = "+outCps.getId());
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_REFILLSTOCKOUT);
							csc.setCode(cargoOperation.getCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(inCi.getStockType());
							csc.setStockArea(inCi.getAreaId());
							csc.setProductId(product.getId());
							csc.setStockId(outCps.getId());
							csc.setStockOutCount(outCoc.getStockCount());
							csc.setStockOutPriceSum((new BigDecimal(outCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(outCps.getStockCount()+outCps.getStockLockCount());
							csc.setCargoStoreType(outCi.getStoreType());
							csc.setCargoWholeCode(outCi.getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());

							stockInCount = stockInCount + outCoc.getStockCount();
							if(outCi.getAreaId()!=inCi.getAreaId()){
								//更新订单缺货状态
								this.updateLackOrder(outCoc.getProductId());
							}
							
							//合格库待作业任务处理
							CargoOperationTodoBean cot=CIService.getCargoOperationTodo("cargo_product_stock_id="+outCps.getId()+" and status in(0,1,2) and type=2");
							if(cot!=null){
								CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
							}
							
						}
						//货位入库卡片
						inCps = service.getCargoProductStock("id = "+inCps.getId());
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_REFILLSTOCKIN);
						csc.setCode(cargoOperation.getCode());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(inCi.getStockType());
						csc.setStockArea(inCi.getAreaId());
						csc.setProductId(product.getId());
						csc.setStockId(inCps.getId());
						csc.setStockInCount(stockInCount);
						csc.setStockInPriceSum((new BigDecimal(stockInCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(inCps.getStockCount()+inCps.getStockLockCount());
						csc.setCargoStoreType(inCi.getStoreType());
						csc.setCargoWholeCode(inCi.getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
						service.addCargoStockCard(csc);
					}
					CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
					CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS25);//下个阶段
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
					//修改上一操作日志的时效
					CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
					if(lastLog != null && lastLog.getEffectTime()==1){//如果不是进行中，不需要再改时效
						int effectTime=process.getEffectTime();//上阶段时效
						String lastOperateTime=lastLog.getOperDatetime();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long date1=sdf.parse(lastOperateTime).getTime();
						long date2=sdf.parse(DateUtil.getNow()).getTime();
						if(date1+effectTime*60*1000<date2){//已超时
							service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId());
						}
					}
					if(!service.updateCargoOperation(
							"status="+CargoOperationProcessBean.OPERATION_STATUS26+"" +
									",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
											",complete_datetime='"+DateUtil.getNow()+"'" +
													",complete_user_id="+user.getId()+"" +
															",complete_user_name='"+user.getUsername()+"'", 
															"id="+operId)){
						request.setAttribute("tip", "更新补货单状态，数据库操作失败！");
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
					service.addCargoOperLog(operLog);
					CartonningInfoBean cib = CIService.getCartonningInfo("oper_id=" + cargoOperation.getId());
					if(cib!=null){
						if(!CIService.updateCartonningInfo("status=2", "id=" + cib.getId())){
							request.setAttribute("tip", "更新装箱单状态，数据库操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					request.setAttribute("cargoOperation", cargoOperation);
				}else if(type == 3){//调拨
					if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS34){
						request.setAttribute("tip", "该作业单状态已被更新，操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					service.getDbOp().startTransaction();//完成货位库存量操作
					List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
					for(int i=0;i<outCocList.size();i++){
						CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
						CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
						CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());
						voProduct product = wareService.getProduct(outCoc.getProductId());
						product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
						int stockOutCount = 0;
						List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
						for(int j=0;j<inCocList.size();j++){
							CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
							CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
							CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
							if(inCps!=null&&outCps!=null){
								if(!service.updateCargoProductStockCount(inCps.getId(), inCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位冻结库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if(!service.updateCargoProductStockLockCount(outCps.getId(), -inCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位冻结库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								//调整合格库库存，修改批次，添加进销存卡片
								if(inCi.getAreaId()!=outCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){
									CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+inCi.getAreaId());//目的货位地区
									CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+outCi.getAreaId());//源货位地区
									
									ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+inCi.getStockType());
									ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+outCi.getStockType());
									
									if(psIn==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if(psOut==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									
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
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
												stockBatchCount = batch.getBatchCount();
											}else{
												if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
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
									                service.getDbOp().rollbackTransaction();
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
												newBatch.setSupplierId(batch.getSupplierId());
												newBatch.setTax(batch.getTax());
												newBatch.setNotaxPrice(batch.getNotaxPrice());
												newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
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
									sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									sc2.setCode(cargoOperation.getCode());
									sc2.setCreateDatetime(DateUtil.getNow());
									sc2.setStockType(outCi.getStockType());
									sc2.setStockArea(outCi.getAreaId());
									sc2.setProductId(product.getId());
									sc2.setStockId(psOut.getId());
									sc2.setStockOutCount(inCoc.getStockCount());
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
								inCps = service.getCargoProductStock("id = "+inCps.getId());
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKIN);
								csc.setCode(cargoOperation.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(inCi.getStockType());
								csc.setStockArea(inCi.getAreaId());
								csc.setProductId(product.getId());
								csc.setStockId(inCps.getId());
								csc.setStockInCount(inCoc.getStockCount());
								csc.setStockInPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
								csc.setAllStock(product.getStockAll() + product.getLockCountAll());
								csc.setCurrentCargoStock(inCps.getStockCount()+inCps.getStockLockCount());
								csc.setCargoStoreType(inCi.getStoreType());
								csc.setCargoWholeCode(inCi.getWholeCode());
								csc.setStockPrice(product.getPrice5());
								csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
								service.addCargoStockCard(csc);

								stockOutCount = stockOutCount + inCoc.getStockCount();
							}else{
								request.setAttribute("tip", "库存错误，无法提交！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							if(outCi.getAreaId()!=inCi.getAreaId()){
								//更新订单缺货状态
								this.updateLackOrder(outCoc.getProductId());
							}
							
							//合格库待作业任务处理
							CargoOperationTodoBean cot=CIService.getCargoOperationTodo("cargo_product_stock_id="+outCps.getId()+" and status in(0,1,2) and type=3");
							if(cot!=null){
								CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
							}
						}

						//货位出库卡片
						outCps = service.getCargoProductStock("id = "+outCps.getId());
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKOUT);
						csc.setCode(cargoOperation.getCode());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(outCi.getStockType());
						csc.setStockArea(outCi.getAreaId());
						csc.setProductId(product.getId());
						csc.setStockId(outCps.getId());
						csc.setStockOutCount(stockOutCount);
						csc.setStockOutPriceSum((new BigDecimal(stockOutCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(outCps.getStockCount()+outCps.getStockLockCount());
						csc.setCargoStoreType(outCi.getStoreType());
						csc.setCargoWholeCode(outCi.getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
						service.addCargoStockCard(csc);
					}
					if(!service.updateCargoOperation(
							"status="+CargoOperationProcessBean.OPERATION_STATUS35+"" +
									",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
											",complete_datetime='"+DateUtil.getNow()+"'" +
													",complete_user_id="+user.getId()+"" +
															",complete_user_name='"+user.getUsername()+"'", 
															"id="+operId)){
						request.setAttribute("tip", "更新调拨单状态，数据库操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//作废此装箱单
					if(!CIService.updateCartonningInfo(" status=2", " id=" + cartonningInfo.getId())){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作废装箱单操作失败 !");
						request.setAttribute("result", "failure");
					}
					if(cargoOperation.getStockOutType()!=CargoInfoBean.STORE_TYPE0){
						//整件区调拨，修改装箱单相关装箱单关联货位
						//修改相关调拨单关联的货位，改为关联目的货位
						CartonningInfoBean cartonning=CIService.getCartonningInfo("oper_id="+operId);
						if(cartonning!=null){
							CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+operId+" and in_cargo_product_stock_id>0");
							CargoInfoBean cargo=service.getCargoInfo("whole_code='"+coc.getInCargoWholeCode()+"'");
							if(cargo!=null){
								CIService.updateCartonningInfo("cargo_id="+cargo.getId(), "id="+cartonning.getId());
							}
						}
					}
					
					
					CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
					CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
					if(process==null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作业单流程信息错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(process2==null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "作业单流程信息错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//修改上一操作日志的时效
					CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
					if(lastLog!=null && lastLog.getEffectTime()==1){//如果不是进行中，不需要再改时效
						int effectTime=process.getEffectTime();//上阶段时效
						String lastOperateTime=lastLog.getOperDatetime();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long date1=sdf.parse(lastOperateTime).getTime();
						long date2=sdf.parse(DateUtil.getNow()).getTime();
						if(date1+effectTime*60*1000<date2){//已超时
							service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId());
						}
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
					service.addCargoOperLog(operLog);
				}
				
				//调用财务接口
				if(!baseList.isEmpty()){
					FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(baseList, cargoOperation.getCode(), user.getId(), 0, 0);
				}
				
				request.setAttribute("result", "作业单确认完成成功！");
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				request.setAttribute("result", "作业单确认完成失败！");
				e.printStackTrace();
			}finally{
				service.releaseAll();
				wareService.releaseAll();
			}
		}
		return mapping.findForward("soOperationComplete");
	}
	public void updateLackOrder(int productId){
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		DbOperation dbOp2 = new DbOperation();
		dbOp2.init();
		WareService wareService = new WareService(dbOp);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String productIds = productId+"";
			//查询父商品
			List ppList = ppService.getProductPackageList("product_id=" + productId, -1, -1, null);
			Iterator ppIter = ppList.listIterator();
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
				productIds = productIds + "," + ppBean.getParentId();
			}

			List lackOrders = wareService.getOrdersByProducts("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null);
			lackOrders.addAll(wareService.getOrdersByPresents("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null));
			Iterator iter = lackOrders.listIterator();
			while(iter.hasNext()){
				voOrder order = (voOrder)iter.next();

				// 判断订单中商品的库存是否满足，根据库存状态，设置订单发货状态
				List orderProductList = wareService.getOrderProducts(order.getId());
				List orderPresentList = wareService.getOrderPresents(order.getId());
				orderProductList.addAll(orderPresentList);

				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(service.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							detailList.add(tempVOP);
						}
					} else {
						vop.setPsList(service.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						detailList.add(vop);
					}
				}
				orderProductList = detailList;

				if (checkStock(orderProductList,ProductStockBean.AREA_GF) || checkStock(orderProductList,ProductStockBean.AREA_ZC)) {
					updateOrderLackStatu(dbOp2,order.getId());
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
			dbOp2.release();
		}

	}
	/**
	 * 功能:缺货补货 时间判断 
	 * @param dbOp
	 * @param orderId
	 */
	public static void updateOrderLackStatu(DbOperation dbOp ,int orderId){
		dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
				"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 3600 and uo.is_olduser=1 and uo.id = "+orderId);
		dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
				"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 7200 and uo.id = "+orderId);
		dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 7,uold.stockout_deal = 7,uold.next_deal_datetime = null " +
				"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') > 7200 and uo.id = "+orderId);
	}
	public boolean checkStock(List orderProductList,int area) {
		if (orderProductList == null) {
			return false;
		}
		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.STOCKTYPE_QUALIFIED,area) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}
	/**
	 * 作业审核
	 */
	public ActionForward operationAudit(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		ReturnedPackageService service = new ReturnedPackageServiceImpl();
		try {
			String retShelfCode = request.getParameter("operationCode");
			if(retShelfCode == null || retShelfCode.equals("")){
				request.setAttribute("result", "该汇总单号不存在！");
				return mapping.findForward("soOperationAudit");
			}
			int area = StringUtil.toInt(request.getSession().getAttribute("area").toString());
			if(service.pdaCheckCollectBill(request, retShelfCode, area)){
				request.setAttribute("result", "上架单的货位归属与选择的仓库不一致！");
				return mapping.findForward("soOperationAudit");
			}
			String result = service.confirmRetShelf(retShelfCode,user);
			if(result.equals("1")){
				request.setAttribute("result", "该退货上架汇总单确认失败，请重新扫描确认！");
				return mapping.findForward("soOperationAudit");
			}else if(result.equals("2")){
				request.setAttribute("result", "该退货上架汇总单已审核过！");
				return mapping.findForward("soOperationAudit");
			}else if(result.equals("3")){
				request.setAttribute("result", "退货上架汇总单条码不能为空！");
				return mapping.findForward("soOperationAudit");
			}else if(result.equals("4")){
				request.setAttribute("result", "该退货上架汇总单不存在！");
				return mapping.findForward("soOperationAudit");
			}else if(result.equals("5")){
				request.setAttribute("result", "该退货上架汇总单已作业完成！");
				return mapping.findForward("soOperationAudit");
			}
			request.setAttribute("result", "该退货上架汇总单审核成功！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("soOperationAudit");
	}
	public ActionForward clearRetShelfSession(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)throws Exception{
		request.getSession().removeAttribute(Constants.CACHECAROINO);
		request.getSession().removeAttribute("coBean");
		return mapping.findForward("soConfirmComplete");
	}
	/**
	 * 作业完成
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ActionForward pdaConfirmOpShelf(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setCharacterEncoding("UTF-8");
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(618)){
			request.setAttribute("tip", "您没有操作该功能的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String operationCode = request.getParameter("operationCode");
		if(operationCode == null || operationCode.trim().equals("")){
			request.setAttribute("result", "条码不能为空！");
			return mapping.findForward("soConfirmComplete");
		}
		
		WareService wareService = new WareService();
		IProductStockService productservice = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService service = null;
		try {
			service = ServiceFactory.createCargoService(
					IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			CargoOperationBean coBean = (CargoOperationBean)request.getSession().getAttribute("coBean");
			int area = StringUtil.toInt(request.getSession().getAttribute("area").toString());
			List<CargoOperationCargoBean> cocBeanList = null;
			if (coBean == null) {//扫描的退货上架单 
				//判断是否为退货上架单
				coBean = service.getCargoOperation("code='" + StringUtil.toSql(operationCode) + "'");
				if(coBean == null){
					request.setAttribute("result", "该上架单不存在！");
					return mapping.findForward("soConfirmComplete");
				}
				if(!coBean.getCode().startsWith("HWTS")){
					request.setAttribute("result", "该上架单不是退货上架单！");
					return mapping.findForward("soConfirmComplete");
				}
				if((coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS2 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS3 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS4 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS5 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS6 )||(
						coBean.getEffectStatus() != CargoOperationBean.EFFECT_STATUS0 &&
						coBean.getEffectStatus() != CargoOperationBean.EFFECT_STATUS1 )){
					//特殊原因 这块暂时写死
					String errStatus = "";
					if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS1){
						errStatus = "未处理";
					}else if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS7){
						errStatus = "已完成"; //7这是个时效状态 提示改成已完成
					}else if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS8){
						errStatus = "作业成功";
					}else if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS9){
						errStatus = "作业失败";
					}else if(coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS2){
						errStatus = "复核中(时效)";
					}else if(coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS3){
						errStatus = "作业成功(时效)";
					}else if(coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){
						errStatus = "作业失败(时效)";
					}
					request.setAttribute("result", "该退货上架单的状态[" + errStatus + "]不正确！");
					return mapping.findForward("soConfirmComplete");
				}
				cocBeanList = cargoService.getCargoOperationCargoList("oper_id=" + coBean.getId() + " and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, -1, -1, null);
				if(cocBeanList == null || cocBeanList.isEmpty()){
					request.setAttribute("result", "找不到该退货上架单的目的货位信息！");
					return mapping.findForward("soConfirmComplete");
				}	
				if(!pdaCheckWholeOperation(cargoService, cocBeanList, area)){
					request.setAttribute("result", "退货上架单的目的货位归属与选择的仓库不一致！");
					return mapping.findForward("soConfirmComplete");
				}
				request.getSession().setAttribute("coBean", coBean);
				request.setAttribute("result", "退货上架单[" + operationCode + "]扫描成功！");
				return mapping.findForward("soConfirmComplete");
			}
			cocBeanList = cargoService.getCargoOperationCargoList("oper_id=" + coBean.getId() + " and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, -1, -1, null);
			//判断货位是否是该退货装箱单的目的货位
			if(!pdaCheckInWholeOperation(cocBeanList, operationCode)){
				request.setAttribute("result", "扫描的货位与退货装箱单的目的货位不一致！");
				return mapping.findForward("soConfirmComplete");
			}
			List<voProduct> retProductList = new ArrayList<voProduct>();
			for(CargoOperationCargoBean bean : cocBeanList){
				voProduct voProduct = wareService.getProduct(bean.getProductId());
				voProduct.setCount(bean.getStockCount());
				retProductList.add(voProduct);
			}	
			wareService.getDbOp().startTransaction(); //开启事务
			StringBuilder strPId = new StringBuilder();
			strPId.append("(");
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
					"oper_id ="+ coBean.getId() + 
					" and product_id in " + strPId.toString()+
					" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
			}
			// 更新货位库存完成商品数量，减少源货位锁定量，目的货位空间锁定量，目的货位库存，减少库存锁定量
			ReturnedPackageService rpService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
			String strFlag = rpService.updateStockInfoByProductList(retProductList, service, wareService, productservice, cargoOpList, user);
			
			if (strFlag != null) {
				wareService.getDbOp().rollbackTransaction();
				request.setAttribute("result", strFlag);
			   return mapping.findForward("soConfirmComplete");
			}
			
			cargoOpList = service.getCargoOperationCargoList(
					"oper_id ="+ coBean.getId() + 
					" and product_id in " + strPId.toString()+
					" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
			// 检测商品完成数量和库存数量是否一致，如果一致那么修改作业单状态为已完成
			for(int k=0; k<cargoOpList.size(); k++){
				CargoOperationCargoBean cocBean = (CargoOperationCargoBean) cargoOpList.get(k);
				//检测商品完成数量和库存数量是否一致，如果一致那么修改作业单状态为已完成
				if(cocBean.getCompleteCount()==cocBean.getStockCount()){
					if(!service.updateCargoOperation(
							"status=" + CargoOperationProcessBean.OPERATION_STATUS7
							+ ",complete_datetime='" + DateUtil.getNow() + "'"
							+ ",complete_user_name='" + user.getUsername() + "'"
							+ ",complete_user_id=" + user.getId(), 
							"id=" + cocBean.getOperId())){
						wareService.getDbOp().rollbackTransaction();
						request.getSession().removeAttribute(Constants.CACHECAROINO);
						request.setAttribute("result","更新作业完成状态失败！");
						return mapping.findForward("soConfirmComplete");
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
						request.setAttribute("result","添加操作记录失败！");
						return mapping.findForward("soConfirmComplete");
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
						request.setAttribute("result","添加操作记录失败！");
						return mapping.findForward("soConfirmComplete");
					}
				}
			}
			request.getSession().removeAttribute("coBean");
			request.setAttribute("result","退货上架单完成！");
			wareService.getDbOp().commitTransaction(); //提交事务
			return mapping.findForward("soConfirmComplete");
	} catch (Exception e) {
		e.printStackTrace();  
		wareService.getDbOp().rollbackTransaction();
		request.setAttribute("result", "系统异常，请联系管理员！");
		return mapping.findForward("soConfirmComplete");
	} finally {
		wareService.releaseAll();
	}

}	/**
 	 * 说明：物流员工作业效率排名_ajax请求
	 * 
	 * 日期：2013-2-23
	 *
	 * 作者：石远飞
	 */
	public ActionForward ajaxCargoStaffPerformance(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
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
			CargoStaffPerformanceBean cspBean = null;
			List<CargoStaffPerformanceBean> cspList = null;
			CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
			if(csBean == null){
				request.setAttribute("tip", "此账号不是物流员工！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(StringUtil.convertNull(request.getParameter("selectIndex")).equals("9")){
				cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=2");
				cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=2", -1, -1, " oper_count DESC");
			}else if(StringUtil.convertNull(request.getParameter("selectIndex")).equals("10")){
				cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=3");
				cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=3", -1, -1, " oper_count DESC");
			}else if(StringUtil.convertNull(request.getParameter("selectIndex")).equals("12")){
				cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=5");
				cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=5", -1, -1, " oper_count DESC");
			}
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
					firstProductCount = "0";
				}
			}
			request.setAttribute("firstCount", firstCount);
			request.setAttribute("productCount", productCount);
			request.setAttribute("oneselfCount", oneselfCount);
			request.setAttribute("ranking", ranking);
			request.setAttribute("firstProductCount", firstProductCount);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("selection");
	}
	/**
	 * 分拣异常商品处理1:输入波次号或出库单号,根据输入的商品编号得到推荐货位号
	 */
	public ActionForward abnormalProductHandle(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String code = StringUtil.convertNull(request.getParameter("code"));//波次号或者出库单号
		int areaId = StringUtil.toInt(request.getSession().getAttribute("area").toString());//地区id
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));//SKU编号
		String cargoWholeCode ="";//推荐货位编号
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService sortingService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		try{
			if(code!=null && code.length()>0){
				//判断传过来的编号是波次号还是订单编号
				if("FJ".equals(code.substring(0, 2))){
					SortingBatchGroupBean groupBean = sortingService.getSortingBatchGroupInfo("storage="+areaId+" and code='"+code+"'");
					if(groupBean==null){
						request.setAttribute("tip", "错误的分拣波次号");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				else{
					OrderStockBean osBean = stockService.getOrderStock("stock_area="+areaId+" and code='"+code+"'");
					if(osBean==null){
						request.setAttribute("tip", "仓库中找不到此出库单号");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					SortingAbnormalBean abBean = abnormalService.getSortingAbnormal("oper_code='"+code+"' and (status=0 or status=1)");
					if(abBean!=null){
						request.setAttribute("tip", "存在未处理的异常单");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
			}
			voProduct productBean = null;
			ProductBarcodeVO tempVO = bcmService.getProductBarcode(productCode);
			if(tempVO!=null){
				productBean = wareService.getProduct(tempVO.getProductId());
			}else{
				productBean = wareService.getProduct(productCode);
			}
			if(productBean==null){
				request.setAttribute("tip", "该商品不存在");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//查找该SKU存在异常数据的货位,必须是异常单状态是未完成的异常商品
			String str = "select b.cargo_whole_code from sorting_abnormal a join sorting_abnormal_product b on a.id=b.sorting_abnormal_id  where a.status<>5 and b.product_code='"+productCode+"' and b.cargo_whole_code  <>'' limit 1";
			ResultSet rs = abnormalService.getDbOp().executeQuery(str);
			if (rs.next()) {
				cargoWholeCode = rs.getString("b.cargo_whole_code");
			}
			rs.close();
			if(cargoWholeCode.length()==0){//如果没有异常数据的货位，则查找货位库存最大的货位
				CargoProductStockBean cpsBean = cargoService.getCargoAndProductStock(" ci.area_id = " + areaId + " AND ci.stock_type=0 AND ci.store_type IN (" + CargoInfoBean.STORE_TYPE0 + "," + CargoInfoBean.STORE_TYPE4 + ") AND cps.product_id = " + productBean.getId()+" ORDER BY cps.stock_count DESC LIMIT 1");
				//得到推荐货位编号
				if(cpsBean!=null){
					CargoInfoBean ciBean = cargoService.getCargoInfo("id="+cpsBean.getCargoId());
					cargoWholeCode=ciBean.getWholeCode();
				}else{
					request.setAttribute("tip", "该商品找不到推荐货位");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			request.setAttribute("cargoWholeCode", cargoWholeCode);
			request.setAttribute("productCode", productCode);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		ActionForward actionForward = new ActionForward("/admin/cargo/soAbnormalProductHandleNext.jsp?code="+code+"&productCode="+productCode+"&cargoWholeCode="+cargoWholeCode);
		actionForward.setRedirect(true);
		return actionForward;
	}
	/**
	 * 分拣异常商品处理2:用户扫描了推荐的货位号之后生成异常处理单,添加异常单商品
	 */
	public ActionForward soAbnormalProductHandleNext(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String code = StringUtil.convertNull(request.getParameter("code"));//波次号或者出库单号
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));//SKU编号
		int areaId = StringUtil.toInt(request.getSession().getAttribute("area").toString());//地区id
		String cwCode = StringUtil.convertNull(request.getParameter("cwCode"));//实际输入的货位编号
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized (cargoLock) {
			try{
				CargoInfoBean ciBean = cargoService.getCargoInfo("status = 0 and whole_code='"+cwCode+"'");
				if(ciBean==null){
					request.setAttribute("tip", "找不到此货位!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				voProduct productBean = wareService.getProduct(productCode);
				if(productBean==null){
					request.setAttribute("tip", "该商品不存在");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoProductStockBean cpsBean = cargoService.getCargoProductStock("cargo_id="+ciBean.getId()+" and product_id="+productBean.getId());
				if(cpsBean==null){
					request.setAttribute("tip", "该货位没有关联该商品");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//添加异常处理单编号
				SortingAbnormalBean saBean = new SortingAbnormalBean();
				String saCode=abnormalService.getNewSortingAbnormalCode(areaId,wareService.getDbOp());
				if("FAIL".equals(saCode)){
					request.setAttribute("tip", "添加异常处理单编号失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}else{
					wareService.getDbOp().startTransaction();
					saBean.setCode(saCode);
					saBean.setOperCode(code);
					saBean.setStatus(SortingAbnormalBean.STATUS3);
					saBean.setAbnormalType(SortingAbnormalBean.ABNORMALTYPE2);
					saBean.setWareArea(areaId);
					saBean.setCreateDatetime(DateUtil.getNow());
					saBean.setCreateUserId(user.getId());
					saBean.setCreateUserName(user.getUsername());
					if("FJ".equals(code.substring(0, 2))){
						saBean.setOperType(1);
					}else{
						saBean.setOperType(0);
					}
					if(abnormalService.addSortingAbnormal(saBean)==false){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "数据库操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
	
					}
					int saId=dbOp.getLastInsertId();
					saBean.setId(saId);
					SortingAbnormalProductBean sapBean = new SortingAbnormalProductBean();
					sapBean.setSortingAbnormalId(saId);
					sapBean.setProductId(productBean.getId());
					sapBean.setProductCode(productBean.getCode());
					sapBean.setCargoWholeCode(cwCode.toUpperCase());
					sapBean.setCount(1);
					sapBean.setLockCount(0);
					sapBean.setStatus(SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
					sapBean.setLastOperDatetime(DateUtil.getNow());
					if(abnormalService.addSortingAbnormalProduct(sapBean)==false){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "数据库操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					wareService.getDbOp().commitTransaction();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
			} finally {
				wareService.releaseAll();
			}
		}
		ActionForward actionForward = new ActionForward("/admin/cargo/soAbnormalProductHandle.jsp");
		actionForward.setRedirect(true);
		return actionForward;
	}
	/**
	 * 说明：验证该退货上架单的目的货位是否和选择的库地区匹配
	 * 
	 * 日期：2013-5-20
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public boolean pdaCheckWholeOperation(ICargoService cargoService, List<CargoOperationCargoBean> cocBeanList, int area){
		boolean result = true;
		for(CargoOperationCargoBean bean : cocBeanList){
			List<CargoInfoBean> ciBeanList = cargoService.getCargoInfoList("whole_code='"+bean.getInCargoWholeCode()+"'", -1, -1, null);
			if(ciBeanList != null && !ciBeanList.isEmpty()){
				for(CargoInfoBean ciBean : ciBeanList){
					if(area != ciBean.getAreaId()){
						result = false;
						break;
					}
				}
			}
		}
		
		return result;
	}
	/**
	 * 产品查询
	 */
	public ActionForward searchProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String code = StringUtil.convertNull(request.getParameter("code"));//商品编号或条码，或者货位号
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int area = StringUtil.toInt(request.getSession().getAttribute("area").toString());//地区id
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			if(!code.equals("")){
				String result="";//在页面显示的文本信息
				CargoInfoBean ciBean=cargoService.getCargoInfo("whole_code='"+code+"'");
				if(ciBean!=null){//扫描的编号是货位号
					if(ciBean.getAreaId()!=area){
						result="货位地区错误";
						request.setAttribute("result", result);
						return mapping.findForward("searchProduct");
					}
					List cpsList=cargoService.getCargoProductStockList("cargo_id="+ciBean.getId()+" and (stock_count>0 or stock_lock_count>0)", -1, -1, null);
					for(int i=0;i<cpsList.size();i++){
						CargoProductStockBean cpsBean=(CargoProductStockBean)cpsList.get(i);
						int productId=cpsBean.getProductId();
						voProduct product=wareService.getProduct(productId);
						if(product!=null){
							result+="商品";
							result+=product.getCode();
							result+="，可用量";
							result+=cpsBean.getStockCount();
							result+="，锁定量";
							result+=cpsBean.getStockLockCount();
							result+=";\r\n";
						}
					}
				}else{//查询商品
					voProduct product=null;
					ProductBarcodeVO bBean = bService.getProductBarcode("barcode="+"'"+code+"'");
					if(bBean == null){
						product = wareService.getProduct(code);
					}else{
						product = wareService.getProduct(bBean.getProductId());
					}
					if(product==null){
						result="未查询到相关信息";
						request.setAttribute("result", result);
						return mapping.findForward("searchProduct");
					}
					List cpsList=cargoService.getCargoAndProductStockList("cps.product_id="+product.getId()+" and ci.area_id="+area+" and ci.stock_type=0 and (cps.stock_count>0 or cps.stock_lock_count>0)", -1, -1, null);
					for(int i=0;i<cpsList.size();i++){
						CargoProductStockBean cpsBean=(CargoProductStockBean)cpsList.get(i);
						CargoInfoBean tempCi=cpsBean.getCargoInfo();
						if(tempCi!=null){
							result+="货位";
							result+=tempCi.getWholeCode();
							result+="，可用量";
							result+=cpsBean.getStockCount();
							result+="，锁定量";
							result+=cpsBean.getStockLockCount();
							result+=";\r\n";
						}
					}
				}
				if(result.equals("")){
					result="未查询到相关信息";
				}
				request.setAttribute("result", result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return mapping.findForward("searchProduct");
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-5-20
	 * 
	 * 说明：扫描的货位是否和验证该退货上架单的目的货位匹配
	 */
	public boolean pdaCheckInWholeOperation(List<CargoOperationCargoBean> cocBeanList, String operationCode){
		boolean result = false;
		for(CargoOperationCargoBean bean : cocBeanList){
			if(bean.getInCargoWholeCode().equals(operationCode)){
				result = true;
				break;
			}
		}
		return result;
	}
	/**
	 * 说明：分拣异常异常订单处理--再次分拣
	 * 
	 * 日期：2013-6-07
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public ActionForward sortingAgain(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		SortingAbnormalDisposeService service = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE,dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoServie = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingInfoService updateSortingInfoService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		int areaId = StringUtil.toInt(request.getSession().getAttribute("area").toString());//地区id
		String code = StringUtil.convertNull(request.getParameter("code")).trim();
		OrderStockBean osBean = (OrderStockBean)request.getSession().getAttribute("osBean");
		List<SortingAgainBean> saBeanList = (List<SortingAgainBean>)request.getSession().getAttribute("saBeanList");
		SortingBatchOrderBean sboBean = null;
		List<SortingBatchOrderProductBean> sbopBeanList = null;
		SortingBatchGroupBean sbgBean = null;
		SortingBatchBean sbBean = null;
		try {
			synchronized (cargoLock) {
				if(code == null || "".equals(code)){
					request.setAttribute("error", "输入不能为空！");
					return mapping.findForward("soSortingAgain");
				}
				//如果osBean==null说明是第一次扫描CK号
				if(osBean == null){
					osBean = stockService.getOrderStock("code='" + StringUtil.toSql(code) + "' and stock_area=" + areaId);
					if(osBean == null){
						request.setAttribute("error", "请扫描选择的库地区,正确出库单编号！");
						return  mapping.findForward("soDealDeleteOS");
					}
					sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
					if(sboBean == null){
						request.setAttribute("error", "找不到出库单相关联的分拣波次！");
						return  mapping.findForward("soDealDeleteOS");
					}
					sbgBean = service.getSortingBatchGroupBean("id=" + sboBean.getSortingGroupId());
					if(sbgBean == null){
						request.setAttribute("error", "查不到该出库单的二次分拣状态  无法继续进行！");
						return  mapping.findForward("soDealDeleteOS");
					}
					if(sbgBean.getStatus2()!=2){
						request.setAttribute("error", "此出库单未结批！");
						return  mapping.findForward("soDealDeleteOS");
					}
					sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
					if(sbBean == null){
						request.setAttribute("error", "分拣批次订单匹配不到分拣批次！");
						return  mapping.findForward("soDealDeleteOS");
					}
					ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
					int sortingType = 0;
					if (results.next()) {
						sortingType = results.getInt(1);
					}
					results.close();
						sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
						if(sbopBeanList == null || sbopBeanList.size() <= 0){
							request.setAttribute("error", "找不到出库单相关联分拣波次商品信息！");
							return  mapping.findForward("soDealDeleteOS");
						}
						//查此出库单异常数据
						saBeanList = service.getSortingAgainList(sbopBeanList, osBean,dbOp,areaId,sortingType);
					if(saBeanList == null || saBeanList.size() <= 0){
						request.setAttribute("error", "此出库单没有异常数据 请扫描其它Ck号！");
						return  mapping.findForward("soDealDeleteOS");
					}
					request.getSession().setAttribute("osBean",osBean);
					request.getSession().setAttribute("saBeanList",saBeanList);
					return mapping.findForward("soSortingAgain");
				}
				//判断扫描的是不是Ck号
				if(code.toLowerCase().indexOf("ck") == 0){
					//判断是不是再次扫描CK号 退出操作
					if(osBean.getCode().equals(code)){
						sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
						sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
						sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
						ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
						int sortingType = 0;
						if (results.next()) {
							sortingType = results.getInt(1);
						}
						results.close();
						//查此出库单异常数据
						saBeanList = service.getSortingAgainList(sbopBeanList, osBean,dbOp,areaId,sortingType);
						//如果还有没有完成的异常处理则提示
						if(saBeanList != null && saBeanList.size() > 0){
							request.setAttribute("error", "库存异常订单请线下处理！");
						}
						request.getSession().removeAttribute("osBean");
						request.getSession().removeAttribute("cargoInfo");
						request.getSession().removeAttribute("saBeanList");
						return mapping.findForward("soSortingAgain");
					}else{
						request.setAttribute("error", "请扫描相同的CK号,来结束操作！");
						return mapping.findForward("soSortingAgain");
					}
				}
				//判断是不是再次扫描的货位号 如果是那么记录最新扫描的货位
				if(code.indexOf("-")>0){
					CargoInfoBean ciBean = cargoServie.getCargoInfo("whole_code='" + StringUtil.toSql(code) + "' and stock_type=" 
						+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and area_id=" +areaId + " and (store_type=0 or store_type=4)");
					if(ciBean == null){
						request.setAttribute("error", "请扫描有效的货位编号！");
						return mapping.findForward("soSortingAgain");
					}
					request.getSession().setAttribute("cargoInfo",ciBean);
					return mapping.findForward("soSortingAgain");
				}
				CargoInfoBean cargoInfo = (CargoInfoBean)request.getSession().getAttribute("cargoInfo");
				//判断是不是第一次扫描货位 
				if(cargoInfo == null){
					request.setAttribute("error", "先扫货位,再扫商品！");
					return mapping.findForward("soSortingAgain");
				}
				//扫描了商品
				ProductBarcodeVO tempVO = bcmService.getProductBarcode("barcode='"+StringUtil.toSql(code)+"'");
				voProduct product = null;
				if(tempVO == null){
					product = service.getProductByCode(StringUtil.toSql(code));
				}else{
					product = service.getProductById(tempVO.getProductId());
				}
				if(product == null){
					request.setAttribute("error", "请扫描正确的商品条码或编号！");
					return mapping.findForward("soSortingAgain");
				}
				if(saBeanList == null || saBeanList.size() <= 0){
					request.setAttribute("error", "操作超时 可以返回上级重新开始！");
					return mapping.findForward("soSortingAgain");
				}
				if(!service.checkProduct(saBeanList, product.getCode())){
					request.setAttribute("error", "此商品不在分拣异常商品列表中！");
					return mapping.findForward("soSortingAgain");
				}
				dbOp.startTransaction(); //开启事务
				//判断扫的是本货位还是推荐货位 
				int sortingBatchOrderProductId = service.checkWholeAndProduct(saBeanList, cargoInfo.getWholeCode(),product.getCode());
				SortingBatchOrderProductBean sbopBean = service.getSortingBatchOrderProductt("id=" + sortingBatchOrderProductId);
				if(sortingBatchOrderProductId >= 0){
					sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
					sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
					ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
					int sortingType = 0;
					if (results.next()) {
						sortingType = results.getInt(1);
					}
					results.close();
					if (sortingType == 1) {
						//更新库存
						if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1) + ",sorting_count=" + (sbopBean.getSortingCount()+1), "id=" + sortingBatchOrderProductId)){
							dbOp.rollbackTransaction();
							request.setAttribute("error", "更新分拣批次订单商品完成数量失败！");
							return mapping.findForward("soSortingAgain");
						}
						CargoProductStockBean outCargoProductStock = cargoServie.getCargoAndProductStock("cargo_id = "+cargoInfo.getId() +" and product_id = "+product.getId());
						if (outCargoProductStock == null) {
							dbOp.rollbackTransaction();
							request.setAttribute("error", "没有找到源货位！");
							return mapping.findForward("soSortingAgain");
						}
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
								null));
						if ((sbopBean.getSortingCount() + 1) == sbopBean.getCount()) {
							//状态更新为正常
							if (!updateSortingInfoService.updateSortingBatchGroupExceptionInfo("is_exception=0", "sorting_batch_group_id ="+sbopBean.getSortingBatchGroupId()+" and product_id="+product.getId()+" and cargo_id="+outCargoProductStock.getCargoId())) {
								dbOp.rollbackTransaction();
								request.setAttribute("error", "更新分拣货位商品异常表报错！");
								return mapping.findForward("soSortingAgain");
							}
						}
						//减少源锁定量非作业区
						if(!cargoServie.updateCargoProductStockLockCount(outCargoProductStock.getId(), -1)){
							dbOp.rollbackTransaction();
							request.setAttribute("error", "货位库存操作失败，源货位冻结库存不足！");
							return mapping.findForward("soSortingAgain");
						}
						CargoInfoBean inCargoInfo = cargoServie.getCargoInfo("stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+areaId+" and store_type = "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
						if (inCargoInfo == null) {
							dbOp.rollbackTransaction();
							request.setAttribute("error", "没有找到目的货位！");
							return mapping.findForward("soSortingAgain");
						}
						CargoProductStockBean inCargoProductStock = cargoServie.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
						if (inCargoProductStock == null) {
							CargoProductStockBean cpStockBean = new CargoProductStockBean();
							cpStockBean.setCargoId(inCargoInfo.getId());
							cpStockBean.setProductId(product.getId());
							cpStockBean.setStockCount(0);
							cpStockBean.setStockLockCount(0);
							if (!cargoServie.addCargoProductStock(cpStockBean)) {
								dbOp.rollbackTransaction();
								request.setAttribute("error", "添加目的货位库存记录失败！");
								return mapping.findForward("soSortingAgain");
							}
							inCargoProductStock = cargoServie.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
						}
						//增加目的锁定量作业区
						if(!cargoServie.updateCargoProductStockLockCount(inCargoProductStock.getId(), 1)){
							dbOp.rollbackTransaction();
							request.setAttribute("error", "货位库存操作失败，目的货位冻结库存不足！");
							return mapping.findForward("soSortingAgain");
						}
						
						//添加货位进销存卡片
						//货位出库卡片
						CargoStockCardBean outcsc = new CargoStockCardBean();
						outcsc.setCardType(CargoStockCardBean.CARDTYPE_EXECOUTPRODUCT);
						outcsc.setCode(osBean.getCode());
						outcsc.setCreateDatetime(DateUtil.getNow());
						outcsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
						outcsc.setStockArea(areaId);
						outcsc.setProductId(product.getId());
						outcsc.setStockId(outCargoProductStock.getId());
						outcsc.setStockOutCount(1);
						outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						outcsc.setCurrentStock(product.getStock(outcsc.getStockArea(), outcsc.getStockType()) + product.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
						outcsc.setAllStock(product.getStockAll() + product.getLockCountAll());
						outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount()+outCargoProductStock.getStockLockCount());
						outcsc.setCargoStoreType(outCargoProductStock.getCargoInfo().getStoreType());
						outcsc.setCargoWholeCode(outCargoProductStock.getCargoInfo().getWholeCode());
						outcsc.setStockPrice(product.getPrice5());
						outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
						if(!cargoServie.addCargoStockCard(outcsc)){
							dbOp.rollbackTransaction();
							request.setAttribute("error", "货位进销存记录添加失败，请重新尝试操作！");
							return mapping.findForward("soSortingAgain");
						}
						//货位入库卡片
						CargoStockCardBean incsc = new CargoStockCardBean();
						incsc.setCardType(CargoStockCardBean.CARDTYPE_EXECINPRODUCT);
						incsc.setCode(osBean.getCode());
						incsc.setCreateDatetime(DateUtil.getNow());
						incsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
						incsc.setStockArea(areaId);
						incsc.setProductId(product.getId());
						incsc.setStockId(inCargoProductStock.getId());
						incsc.setStockInCount(1);
						incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						incsc.setCurrentStock(product.getStock(incsc.getStockArea(), incsc.getStockType()) + product.getLockCount(incsc.getStockArea(), incsc.getStockType()));
						incsc.setAllStock(product.getStockAll() + product.getLockCountAll());
						incsc.setCurrentCargoStock(inCargoProductStock.getStockCount()+inCargoProductStock.getStockLockCount());
						incsc.setCargoStoreType(inCargoProductStock.getCargoInfo().getStoreType());
						incsc.setCargoWholeCode(inCargoProductStock.getCargoInfo().getWholeCode());
						incsc.setStockPrice(product.getPrice5());
						incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
						if(!cargoServie.addCargoStockCard(incsc)){
							dbOp.rollbackTransaction();
							request.setAttribute("error", "货位进销存记录添加失败，请重新尝试操作！");
							return mapping.findForward("soSortingAgain");
						}
					} else {
						//更新库存
						if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1) , "id=" + sortingBatchOrderProductId)){
							dbOp.rollbackTransaction();
							request.setAttribute("error", "更新分拣批次订单商品完成数量失败！");
							return mapping.findForward("soSortingAgain");
						}
					}
					sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
					sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
					saBeanList = service.getSortingAgainList(sbopBeanList, osBean,dbOp,areaId,sortingType);
					request.getSession().removeAttribute("cargoInfo");
					dbOp.commitTransaction();//提交事务
					//判断是不是这个出库单下的所有任务都完成
					if(saBeanList != null && saBeanList.size() > 0){
						request.getSession().setAttribute("saBeanList", saBeanList);
					}else{
						request.getSession().removeAttribute("osBean");
						request.getSession().removeAttribute("saBeanList");
						request.setAttribute("error", "所有订单都已处理完毕,辛苦！请扫描下一出库单");
						return  mapping.findForward("soDealDeleteOS");
					}
					return mapping.findForward("soSortingAgain");
				}else{
					CargoProductStockBean cpsBean = cargoServie.getCargoAndProductStock("cargo_id=" + cargoInfo.getId() + " and product_id=" + product.getId()+" and stock_count>0");
					if(cpsBean == null){
						dbOp.rollbackTransaction();
						request.setAttribute("error", "商品[" + product.getCode()+ "]不在货位[" + cargoInfo.getWholeCode() +"]");
						return mapping.findForward("soSortingAgain");
					}
					String inCargoWholeCode = service.getInCargoWholeCode(saBeanList, product.getCode());
					//生成调拨单
					String result = service.generateDeploy(dbOp, areaId, user, inCargoWholeCode, cargoInfo.getWholeCode(), product.getId());
					if(!"success".equals(result)){
						dbOp.rollbackTransaction();
						request.setAttribute("error", result);
						return mapping.findForward("soSortingAgain");
					}
					//生成异常单
					String sortingAbnormalCode = service.getNewSortingAbnormalCode(osBean.getStockArea(),dbOp);//生成货位异常单编号
					if("FAIL".equals(sortingAbnormalCode)){
						dbOp.rollbackTransaction();
						request.setAttribute("error", "生成分拣异常单号失败！");
						return mapping.findForward("soSortingAgain");
					}
					SortingAbnormalBean sortingAbnormalBean = new SortingAbnormalBean();
					sortingAbnormalBean.setCode(sortingAbnormalCode);
					sortingAbnormalBean.setOperCode(osBean.getCode());
					sortingAbnormalBean.setOperType(SortingAbnormalBean.OPERTYPE0);
					sortingAbnormalBean.setAbnormalType(SortingAbnormalBean.ABNORMALTYPE1);
					sortingAbnormalBean.setStatus(SortingAbnormalBean.STATUS3);
					sortingAbnormalBean.setCreateDatetime(DateUtil.getNow());
					sortingAbnormalBean.setCreateUserId(user.getId());
					sortingAbnormalBean.setCreateUserName(user.getUsername());
					sortingAbnormalBean.setWareArea(areaId);
					if(!service.addSortingAbnormal(sortingAbnormalBean)){
						dbOp.rollbackTransaction();
						request.setAttribute("error", "生成异分拣常单失败！");
						return mapping.findForward("soSortingAgain");
					}
					//获取刚刚插入的异常单ID
					int sortingAbnormalId = dbOp.getLastInsertId();
					//SortingAgainBean saBean = service.getSortingAgainBean(saBeanList, product.getCode());
					SortingAbnormalProductBean sapBean = new SortingAbnormalProductBean();
					sapBean.setCargoWholeCode(inCargoWholeCode);
					sapBean.setCount(1);
					sapBean.setLockCount(1);
					sapBean.setSortingAbnormalId(sortingAbnormalId);
					sapBean.setProductCode(product.getCode());
					sapBean.setProductId(product.getId());
					sapBean.setStatus(SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
					sapBean.setLastOperDatetime(DateUtil.getNow());
					if(!service.addSortingAbnormalProduct(sapBean)){
						dbOp.rollbackTransaction();
						request.setAttribute("error", "生成分拣异常单商品失败！");
						return mapping.findForward("soSortingAgain");
					}
					sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
					sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
						//获取本货位code
						String wholeCode = service.getInCargoWholeCode(saBeanList, product.getCode());
						//根据商品code和货位code获取异常商品ID
						sortingBatchOrderProductId = service.checkWholeAndProduct(saBeanList, wholeCode, product.getCode());
						sbopBean = service.getSortingBatchOrderProductt("id=" + sortingBatchOrderProductId);
						ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
						int sortingType = 0;
						if (results.next()) {
							sortingType = results.getInt(1);
						}
						results.close();
						if (sortingType == 1) {
							if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1)+", sorting_count="+ (sbopBean.getSortingCount()+1), "id=" + sortingBatchOrderProductId)){
								dbOp.rollbackTransaction();
								request.setAttribute("error", "更新分拣批次订单商品完成数量失败！");
								return mapping.findForward("soSortingAgain");
							}
							CargoInfoBean outCargoInfo = cargoServie.getCargoInfo("whole_code='"+inCargoWholeCode+"' and stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+areaId+" and store_type <> "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
							if (outCargoInfo == null) {
								dbOp.rollbackTransaction();
								request.setAttribute("error", "没有找到源货位！");
								return mapping.findForward("soSortingAgain");
							}
							if ((sbopBean.getSortingCount() + 1) == sbopBean.getCount()) {
								//状态更新为正常
								if (!updateSortingInfoService.updateSortingBatchGroupExceptionInfo("is_exception=0", "sorting_batch_group_id ="+sbopBean.getSortingBatchGroupId()+" and product_id="+product.getId()+" and cargo_id="+outCargoInfo.getId())) {
									dbOp.rollbackTransaction();
									request.setAttribute("error", "更新分拣货位商品异常表报错！");
									return mapping.findForward("soSortingAgain");
								}
							}
							product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
									null));
							CargoProductStockBean outCargoProductStock = cargoServie.getCargoAndProductStock("cargo_id = "+outCargoInfo.getId() +" and product_id = "+product.getId());
							if (outCargoProductStock == null) {
								dbOp.rollbackTransaction();
								request.setAttribute("error", "没有源货位库存记录！");
								return mapping.findForward("soSortingAgain");
							}
							//减少源锁定量非作业区
							if(!cargoServie.updateCargoProductStockLockCount(outCargoProductStock.getId(), -1)){
								dbOp.rollbackTransaction();
								request.setAttribute("error", "货位库存操作失败，源货位冻结库存不足！");
								return mapping.findForward("soSortingAgain");
							}
							CargoInfoBean inCargoInfo = cargoServie.getCargoInfo("stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+areaId+" and store_type = "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
							if (inCargoInfo == null) {
								dbOp.rollbackTransaction();
								request.setAttribute("error", "没有找到目的货位！");
								return mapping.findForward("soSortingAgain");
							}
							CargoProductStockBean inCargoProductStock = cargoServie.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
							if (inCargoProductStock == null) {
								CargoProductStockBean cpStockBean = new CargoProductStockBean();
								cpStockBean.setCargoId(inCargoInfo.getId());
								cpStockBean.setProductId(product.getId());
								cpStockBean.setStockCount(0);
								cpStockBean.setStockLockCount(0);
								if (!cargoServie.addCargoProductStock(cpStockBean)) {
									dbOp.rollbackTransaction();
									request.setAttribute("error", "添加目的货位库存记录失败！");
									return mapping.findForward("soSortingAgain");
								}
								inCargoProductStock = cargoServie.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
							}
							//增加目的锁定量作业区
							if(!cargoServie.updateCargoProductStockLockCount(inCargoProductStock.getId(), 1)){
								dbOp.rollbackTransaction();
								request.setAttribute("error", "货位库存操作失败，目的货位冻结库存不足！");
								return mapping.findForward("soSortingAgain");
							}
							
							//添加货位进销存卡片
							//货位出库卡片
							CargoStockCardBean outcsc = new CargoStockCardBean();
							outcsc.setCardType(CargoStockCardBean.CARDTYPE_EXECOUTPRODUCT);
							outcsc.setCode(osBean.getCode());
							outcsc.setCreateDatetime(DateUtil.getNow());
							outcsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
							outcsc.setStockArea(areaId);
							outcsc.setProductId(product.getId());
							outcsc.setStockId(outCargoProductStock.getId());
							outcsc.setStockOutCount(1);
							outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							outcsc.setCurrentStock(product.getStock(outcsc.getStockArea(), outcsc.getStockType()) + product.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
							outcsc.setAllStock(product.getStockAll() + product.getLockCountAll());
							outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount()+outCargoProductStock.getStockLockCount());
							outcsc.setCargoStoreType(outCargoProductStock.getCargoInfo().getStoreType());
							outcsc.setCargoWholeCode(outCargoProductStock.getCargoInfo().getWholeCode());
							outcsc.setStockPrice(product.getPrice5());
							outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
							if(!cargoServie.addCargoStockCard(outcsc)){
								dbOp.rollbackTransaction();
								request.setAttribute("error", "货位进销存记录添加失败，请重新尝试操作！");
								return mapping.findForward("soSortingAgain");
							}
							//货位入库卡片
							CargoStockCardBean incsc = new CargoStockCardBean();
							incsc.setCardType(CargoStockCardBean.CARDTYPE_EXECINPRODUCT);
							incsc.setCode(osBean.getCode());
							incsc.setCreateDatetime(DateUtil.getNow());
							incsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
							incsc.setStockArea(areaId);
							incsc.setProductId(product.getId());
							incsc.setStockId(inCargoProductStock.getId());
							incsc.setStockInCount(1);
							incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							incsc.setCurrentStock(product.getStock(incsc.getStockArea(), incsc.getStockType()) + product.getLockCount(incsc.getStockArea(), incsc.getStockType()));
							incsc.setAllStock(product.getStockAll() + product.getLockCountAll());
							incsc.setCurrentCargoStock(inCargoProductStock.getStockCount()+inCargoProductStock.getStockLockCount());
							incsc.setCargoStoreType(inCargoProductStock.getCargoInfo().getStoreType());
							incsc.setCargoWholeCode(inCargoProductStock.getCargoInfo().getWholeCode());
							incsc.setStockPrice(product.getPrice5());
							incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
							if(!cargoServie.addCargoStockCard(incsc)){
								dbOp.rollbackTransaction();
								request.setAttribute("error", "货位进销存记录添加失败，请重新尝试操作！");
								return mapping.findForward("soSortingAgain");
							}
						} else {
							if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1) , "id=" + sortingBatchOrderProductId)){
								dbOp.rollbackTransaction();
								request.setAttribute("error", "更新分拣批次订单商品完成数量失败！");
								return mapping.findForward("soSortingAgain");
							}
						}
						sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
						saBeanList = service.getSortingAgainList(sbopBeanList,osBean,dbOp,areaId,sortingType);
					request.getSession().removeAttribute("cargoInfo");
					dbOp.commitTransaction();//提交事务
					if(saBeanList != null && saBeanList.size() > 0){
						request.getSession().setAttribute("saBeanList", saBeanList);
					}else{
						request.getSession().removeAttribute("osBean");
						request.getSession().removeAttribute("saBeanList");
						request.setAttribute("error", "所有订单都已处理完毕,辛苦啦！请扫描下一出库单");
						return  mapping.findForward("soDealDeleteOS");
					}
					return mapping.findForward("soSortingAgain");
				}
				
			}
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return mapping.findForward("soSortingAgain");
	}
	/**
	 * 说明：异常货位盘点--查询列表
	 * 
	 * 日期：2013-6-09
	 * 
	 * 作者：石远飞
	 */
	public ActionForward InventoryAbnormalCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		SortingAbnormalDisposeService service = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE,dbOp);
		String abnormalCargoCheckCode = StringUtil.convertNull(request.getParameter("abnormalCargoCheckCode"));
		int areaId = StringUtil.toInt(request.getSession().getAttribute("area").toString());//地区id
		int inventoryFlag = StringUtil.toInt(request.getSession().getAttribute("inventoryFlag").toString());//1为一盘中，2为二盘中，3为三盘中
		List<AbnormalCargoCheckProductBean> accpBeanList = null;
		try {
			synchronized (cargoLock) {
//				CargoStaffBean staff = cargoService.getCargoStaff("user_id=" + user.getId());
//				if(staff == null){
//					request.setAttribute("error", "此账号不是物流员工!");
//					return mapping.findForward("soInventoryChoose");
//				}
				if(!"".equals(abnormalCargoCheckCode)){
					AbnormalCargoCheckBean accBean = service.getAbnormalCargoCheck("code='" + abnormalCargoCheckCode + "' and area=" + areaId + " and status=" + inventoryFlag);
					if(accBean == null){
						request.setAttribute("error", "请输入有效的计划单号或选择合适的盘点阶段!");
						return mapping.findForward("soInventoryChoose");
					}
				}
				dbOp.startTransaction();
				Map<String,List<AbnormalCargoCheckProductBean>>  map = service.getaccpMap(null,dbOp,inventoryFlag,user,areaId);
				for(Map.Entry<String, List<AbnormalCargoCheckProductBean>> entry : map.entrySet()){
					if(!"success".equals(entry.getKey())){
						dbOp.rollbackTransaction();
						request.setAttribute("error",entry.getKey());
						return mapping.findForward("soInventoryChoose");
					}else{
						accpBeanList = entry.getValue();
					}
				}
				if(accpBeanList != null && accpBeanList.size()>0){
					request.setAttribute("accpBeanList", accpBeanList);
				}else{
					dbOp.rollbackTransaction();
					request.setAttribute("error", "找不到计划相关联的盘点数据!");
					return mapping.findForward("soInventoryChoose");
				}
				dbOp.commitTransaction();
			}
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return mapping.findForward("soInventoryAbnormalCargo");
	}
	/**
	 * 说明：异常货位盘点--记录
	 * 
	 * 日期：2013-6-09
	 * 
	 * 作者：石远飞
	 */
	public ActionForward InventoryAbnormalCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if( request.getSession().getAttribute("area") == null ) {
			request.setAttribute("tip", "当前没有地区信息，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		SortingAbnormalDisposeService service = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		int areaId = StringUtil.toInt(request.getSession().getAttribute("area").toString());//地区id
		String wholeCode = StringUtil.convertNull(request.getParameter("wholeCode"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		int inventoryFlag = StringUtil.toInt(request.getSession().getAttribute("inventoryFlag").toString());//盘点阶段标识
		int accpId = StringUtil.toInt(request.getParameter("accpId"));//id
		int count = StringUtil.toInt(request.getParameter("count"));//盘点数量
		List<AbnormalCargoCheckProductBean> accpBeanList = null;
		try {
			synchronized (cargoLock) {
//				CargoStaffBean staff = cargoService.getCargoStaff("user_id=" + user.getId());
//				if(staff == null){
//					request.setAttribute("error", "此账号不是物流员工!");
//					return mapping.findForward("soInventoryChoose");
//				}
				if(count == -1){
					request.setAttribute("error", "盘点数量错误！");
					return mapping.findForward("soInventoryChoose");
				}
				AbnormalCargoCheckProductBean accpBean = service.getAbnormalCargoCheckProduct("id=" + accpId);
				if(accpBean == null){
					request.setAttribute("error", "盘点任务不存在！");
					return mapping.findForward("soInventoryChoose");
				}
				AbnormalCargoCheckBean accBean = service.getAbnormalCargoCheck("id=" + accpBean.getAbnormalCargoCheckId());
				if (accBean == null) {
					request.setAttribute("error", "盘点计划不存在!");
					return mapping.findForward("soInventoryChoose");					
				}
				if (accBean.getStatus() != AbnormalCargoCheckBean.STATUS0 
						&& accBean.getStatus() != AbnormalCargoCheckBean.STATUS1
						&& accBean.getStatus() != AbnormalCargoCheckBean.STATUS2
						&& accBean.getStatus() != AbnormalCargoCheckBean.STATUS3) {					
					request.setAttribute("error", "盘点计划已盘点完毕!");
					return mapping.findForward("soInventoryChoose");
				}
				
				voProduct product = service.getProductByCode(StringUtil.toSql(productCode));
				if(product == null){
					request.setAttribute("error", "此商品信息不存在！");
					return mapping.findForward("soInventoryChoose");
				}
				CargoInfoBean ciBean = cargoService.getCargoInfo("whole_code='" + StringUtil.toSql(wholeCode) + "'");
				if(ciBean == null){
					request.setAttribute("error", "此货位信息不存在！");
					return mapping.findForward("soInventoryChoose");
				}
				CargoProductStockBean cpsBean = cargoService.getCargoProductStock("product_id=" + product.getId() + " and cargo_id=" + ciBean.getId());
				if(cpsBean == null){
					request.setAttribute("error", "商品货位库存信息不存在！");
					return mapping.findForward("soInventoryChoose");
				}
				StringBuffer set = new StringBuffer();
				StringBuffer accBuff = new StringBuffer();
				dbOp.startTransaction(); //开启事务
				if(inventoryFlag == 1){
					if((cpsBean.getStockCount() + cpsBean.getStockLockCount()) == count){
						set.append("first_check_count=" + count + ",first_check_user_id=" + user.getId() + ",first_check_user_name='" 
									+ user.getUsername() + "',status=" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED + ",final_check_count=" + count);
					}else{
						set.append("first_check_count=" + count + ",first_check_user_id=" + user.getId() + ",first_check_user_name='" 
									+ user.getUsername()+ "',status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_SECOND_CHECK);
					}
					accBuff.append("status=" +  AbnormalCargoCheckBean.STATUS1);
				}else if(inventoryFlag == 2){
					if(accpBean.getFirstCheckCount() == count){
						set.append("second_check_count=" + count + ",second_check_user_id=" + user.getId() + ",second_check_user_name='" 
									 + user.getUsername() + "',status=" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED + ",final_check_count=" + count);
					}else{
						set.append("second_check_count=" + count + ",second_check_user_id=" + user.getId() + ",second_check_user_name='"
									 + user.getUsername()+ "',status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_THRID_CHECK);
					}
					accBuff.append("status=" +  AbnormalCargoCheckBean.STATUS2);
				}else if(inventoryFlag == 3){
					set.append("third_check_count=" + count + ",third_check_user_id=" + user.getId() + ",third_check_user_name='" 
								+ user.getUsername() + "',status=" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED + ",final_check_count=" + count);
					accBuff.append("status=" +  AbnormalCargoCheckBean.STATUS3);
				}
				if(!service.updateAbnormalCargoCheckProduct(set.toString(), "id=" + accpId)){
					dbOp.rollbackTransaction();
					request.setAttribute("error", "盘点计划商品更新失败！");
					return mapping.findForward("soInventoryChoose");
				}
				if(!service.updateAbnormalCargoCheck(accBuff.toString(), "id="+ accpBean.getAbnormalCargoCheckId())){
					dbOp.rollbackTransaction();
					request.setAttribute("error", "盘点计划单更新失败！");
					return mapping.findForward("soInventoryChoose");
				}
				//更新余下未盘点的货位 先按绑定当前物流员工的查 查不到说明领到的单子 都已经完成了 				
				Map<String,List<AbnormalCargoCheckProductBean>>  map = service.getaccpMap(accBean,dbOp,inventoryFlag,user,areaId);
				for(Map.Entry<String, List<AbnormalCargoCheckProductBean>> entry : map.entrySet()){
					if(!"success".equals(entry.getKey())){
						dbOp.rollbackTransaction();
						request.setAttribute("error",entry.getKey());
						return mapping.findForward("soInventoryChoose");
					}else{
						accpBeanList = entry.getValue();
					}
				}
				//如果整个计划单的货位都盘点完成那么 查找下一计划单 
				if(accpBeanList != null && accpBeanList.size()>0){
					request.setAttribute("accpBeanList", accpBeanList);
				}else{
					List<AbnormalCargoCheckProductBean> accpList = service.getAbnormalCargoCheckProductList("abnormal_cargo_check_id=" + accpBean.getAbnormalCargoCheckId() +" and status<>" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED, -1, -1, null);
					if(accpList == null || accpList.size() == 0){
						if(!service.updateAbnormalCargoCheck("status=" + AbnormalCargoCheckBean.STATUS4, "id="+ accpBean.getAbnormalCargoCheckId())){
							dbOp.rollbackTransaction();
							request.setAttribute("error", "盘点计划状态【盘点已完成】更新失败！");
							return mapping.findForward("soInventoryChoose");
						}
					}
					dbOp.commitTransaction();
					return mapping.findForward("soInventoryAbnormalCargoList");
				}
				dbOp.commitTransaction();
			}
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return mapping.findForward("soInventoryAbnormalCargo");
	}
	/**
	 * @name 撤单异常处理
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward orderStockDealForDelete(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) {
		
		//第一步  一进入这个方法 先找操作者信息
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		/*UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(742);  //  异常处理权限
		
		if( !permission ) {
			request.setAttribute("tip", "您没有分播墙操作的权限！");
			request.setAttribute("result", "failure");
		}*/
		
		String code = StringUtil.toSql(StringUtil.convertNull(request.getParameter("code")));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		
		//如果找到了 是物流员工  查看 是否有 他正在分拣的 波次
		DbOperation dbOperation = new DbOperation();
		dbOperation.init(DbOperation.DB);
		DbOperation dbOperationSlave = new DbOperation();
		dbOperationSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOperation);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOperation);
		SortingInfoService sortingInfoService = new SortingInfoService(IBaseService.CONN_IN_SERVICE, dbOperation);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOperation);
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,dbOperation);
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbOperation);
		HttpSession session = request.getSession();
		try {
			synchronized ( cargoLock ) {
				//首先得到code之后 检查code类型
				if( code.startsWith("CK") ) {
					//如果是ck开头的话 
					//判断session中当前是否有缓存的 SortingAbnormal的信息  key是EXSortingAbnormalInfo
					SortingAbnormalBean saBean = (SortingAbnormalBean)session.getAttribute("EXSortingAbnormalInfo");
					OrderStockBean osBean = (OrderStockBean) session.getAttribute("EXOrderStockInfo");
					if( saBean != null ) {
						// 作为结束信号
						if( osBean != null ) {
							code = code.trim();
							if( !osBean.getCode().equals(code) ) {
								request.setAttribute("tip", "出库单号不匹配，不能结束！");
								request.setAttribute("result", "failure");
								return  mapping.findForward("soDealDeleteOS");
							}
						}
						//X--------------------需要加上 改状态等一系列的操作
						sortingAbnormalDisposeService.getDbOp().startTransaction();
						//首先验证这个异常单到底是什么 ，异常情况，
						//对应的修改每个商品记录的状态，，， 
						
						int abnormalCount = sortingAbnormalDisposeService.getSortingAbnormalProductCount("sorting_abnormal_id=" + saBean.getId() + " and status !=" + SortingAbnormalProductBean.STATUS_NORMAL);
						if(abnormalCount > 0 ) {
							if( !sortingAbnormalDisposeService.updateSortingAbnormal("status = " + SortingAbnormalBean.STATUS3, "id=" + saBean.getId()) ) {
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								istockService.getDbOp().rollbackTransaction();
								return  mapping.findForward("soDealDeleteOS");
							}
							if( !sortingAbnormalDisposeService.updateSortingAbnormalProduct("status = " + SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK, "sorting_abnormal_id=" + saBean.getId() + " and status !=" + SortingAbnormalProductBean.STATUS_NORMAL ) ) {
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								istockService.getDbOp().rollbackTransaction();
								return  mapping.findForward("soDealDeleteOS");
							}
						} else {
							if( !sortingAbnormalDisposeService.updateSortingAbnormal("status = " + SortingAbnormalBean.STATUS2, "id=" + saBean.getId()) ) {
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								istockService.getDbOp().rollbackTransaction();
								return  mapping.findForward("soDealDeleteOS");
							}
						}
						
						sortingAbnormalDisposeService.getDbOp().commitTransaction();
						session.setAttribute("EXSortingAbnormalInfo", null);
						session.setAttribute("EXOrderStockInfo", null);
						session.setAttribute("EXCurrentCargoInfos", null);
						return  mapping.findForward("soDealDeleteOS");
					} else {
						// 作为新开始 信号查询并初始化SortingAbnormal信息放入session
						osBean = istockService.getOrderStock("code='" + code + "'");
						if( osBean != null ) {
							if( osBean.getStatus() != OrderStockBean.STATUS4) {
								return new ActionForward("/admin/stockOperation.do?method=sortingAgain&code="+code);
							}
							if( osBean.getStockArea() != wareArea ) {
								request.setAttribute("tip", "出库单信息与操作地区不匹配！");
								request.setAttribute("result", "failure");
								return  mapping.findForward("soDealDeleteOS");
							}
						} else {
							request.setAttribute("tip", "没有找到对应的出库单信息！");
							request.setAttribute("result", "failure");
							return  mapping.findForward("soDealDeleteOS");
						}
						
						Object result = sortingAbnormalDisposeService.getSortingAbnormalAllInfo(code);
						if( result instanceof String ) {
							request.setAttribute("tip", result);
							request.setAttribute("result", "failure");
							return  mapping.findForward("soDealDeleteOS");
						} else if( result instanceof SortingAbnormalBean ) {
							SortingAbnormalBean sab = (SortingAbnormalBean) result;
							if( sab.getStatus() != SortingAbnormalBean.STATUS0 && sab.getStatus() != SortingAbnormalBean.STATUS1 ) {
								request.setAttribute("tip", "异常单不是未处理，或处理中状态，不可以再操作！");
								request.setAttribute("result", "failure");
								return  mapping.findForward("soDealDeleteOS");
							}
							boolean hasStatusChecking = sortingAbnormalDisposeService.hasStatusChecking(sab.getSortingAbnormalProductList());
							if( hasStatusChecking ) {
								request.setAttribute("tip", "存在异常商品已经是盘点中，或者盘点完成了，不能再操作这个异常单了，请结束！");
								request.setAttribute("result", "failure");
								return  mapping.findForward("soDealDeleteOS");
							}
							if( !sortingAbnormalDisposeService.updateSortingAbnormal("status=" + SortingAbnormalBean.STATUS1, "id=" + sab.getId()) ) {
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								return  mapping.findForward("soDealDeleteOS");
							}
							ResultSet results = dbOperationSlave.executeQuery("select sbg.sorting_type  from sorting_batch_group sbg,sorting_batch_order sbo where sbg.id=sbo.sorting_group_id and sbo.order_stock_id="+osBean.getId());
							int sortingType = 0;
							if (results.next()) {
								sortingType = results.getInt(1);
							}
							results.close();
							if (sortingType == 1) {
								List<SortingAbnormalProductBean> list = sab.getSortingAbnormalProductList();
								if (list != null && list.size() != 0) {
									int x = list.size();
									for (int i = 0; i < x; i ++) {
										SortingAbnormalProductBean sapBean = list.get(i);
										ResultSet rs = sortingAbnormalDisposeService.getDbOp().executeQuery("select ospc.cargo_whole_code  from order_stock_product_cargo ospc,order_stock_product osp, order_stock os , sorting_abnormal sa where sa.oper_code = os.code and osp.order_stock_id = os.id and ospc.order_stock_product_id=osp.id and sa.oper_code='"+sab.getOperCode()+"' and osp.product_id="+sapBean.getProductId());
										if (rs.next()) {
											sapBean.setCargoWholeCode(rs.getString(1));
										}
										rs.close();
									}
								}
							}
							session.setAttribute("EXSortingAbnormalInfo", sab );
							session.setAttribute("EXOrderStockInfo", osBean);
							return  mapping.findForward("soDealDeleteOS");
						}
					}
					
				} else if( code.matches("[0-9]{1,20}") ) {
					
					//按照产品编号 或者条码 
					voProduct product = null;
					ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(code)+"'");
					if( bBean == null || bBean.getBarcode() == null ) {
						product = wareService.getProduct(StringUtil.toSql(code));
					} else {
						product = wareService.getProduct(bBean.getProductId());
					}
					
					if( product == null ) {
						request.setAttribute("tip", "没有找到商品信息!");
						request.setAttribute("result", "failure");
						return  mapping.findForward("soDealDeleteOS");
					}
					// 首先还是要检查 session中是否缓存有对应的 SortingAbnormal的信息 验证商品是否在其中有记录
					SortingAbnormalBean saBean = (SortingAbnormalBean)session.getAttribute("EXSortingAbnormalInfo");
					List<SortingAbnormalProductBean> list = (List<SortingAbnormalProductBean>) session.getAttribute("EXCurrentCargoInfos");
					if( saBean == null ) {
						request.setAttribute("tip", "当前还没有异常单信息缓存，请先扫描CK号!");
						request.setAttribute("result", "failure");
						return  mapping.findForward("soDealDeleteOS");
					}
					OrderStockBean osBean = (OrderStockBean) session.getAttribute("EXOrderStockInfo");
					if( osBean != null ) {
						if( osBean.getStockArea() != wareArea ) {
							request.setAttribute("tip", "出库单信息与操作地区不匹配！");
							request.setAttribute("result", "failure");
							return  mapping.findForward("soDealDeleteOS");
						}
					}
					boolean hasStatusChecking = sortingAbnormalDisposeService.hasStatusChecking(saBean.getSortingAbnormalProductList());
					if( hasStatusChecking ) {
						request.setAttribute("tip", "存在异常商品已经是盘点中，或者盘点完成了，不能再操作这个异常单了，请结束！");
						request.setAttribute("result", "failure");
						return  mapping.findForward("soDealDeleteOS");
					}
					//然后找到 是否 当前有货位信息在session 中 
					if( list == null ) {
						request.setAttribute("tip", "当前还没有货位缓存信息，请先扫描货位号！");
						request.setAttribute("result", "failure");
						return  mapping.findForward("soDealDeleteOS");
					}
					//整个异常单中根本就没有这个商品的  属于特殊异常了
					SortingAbnormalProductBean sapBean1 = sortingAbnormalDisposeService.getIsProductOnCargo(saBean.getSortingAbnormalProductList(), product.getId());
					if( sapBean1 == null ) {
						request.setAttribute("tip", "请到‘分拣异常商品’模块处理该SKU");
						request.setAttribute("result", "failure");
						session.setAttribute("EXCurrentCargoInfos", null);
						return  mapping.findForward("soDealDeleteOS");
					}
					
					//找是否有 对应货位  且对应 商品信息的  条目
					SortingAbnormalProductBean sapBean = sortingAbnormalDisposeService.getIsProductOnCargo(list, product.getId());
					if( sapBean == null ) {
						request.setAttribute("tip", "SKU不正确！");
						request.setAttribute("result", "failure");
						session.setAttribute("EXCurrentCargoInfos", null);
						return  mapping.findForward("soDealDeleteOS");
					} else {
						if( sapBean.getLockCount() > 0 ) {
							// 真正实际操作库存的地方了 找到对应的库存记录，货位库存记录  记得操作完成要替换session中的 信息 让session中 和数据库中保持一致
							istockService.getDbOp().startTransaction();
							
							ProductStockBean psBean = null;
							CargoProductStockBean cpsBean = null;
							//寻找库存信息
							psBean = psService.getProductStock("product_id=" + sapBean.getProductId() + " and type=" + osBean.getStockType() + " and area=" + osBean.getStockArea());
							if( psBean == null ) {
								request.setAttribute("tip", "没有找到要操作的库存信息！");
								request.setAttribute("result", "failure");
								istockService.getDbOp().rollbackTransaction();
								return  mapping.findForward("soDealDeleteOS");
							}
							ResultSet results = dbOperationSlave.executeQuery("select sbg.sorting_type  from sorting_batch_group sbg,sorting_batch_order sbo where sbg.id=sbo.sorting_group_id and sbo.order_stock_id="+osBean.getId());
							int sortingType = 0;
							if (results.next()) {
								sortingType = results.getInt(1);
							}
							results.close();
							if (sortingType == 1) {
								product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
										null));
								CargoInfoBean ciBean = cargoService.getCargoInfo("stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+osBean.getStockArea()+" and store_type = "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
								//寻找货位库存信息
								if( ciBean == null ) {
									request.setAttribute("tip", "没有找到要操作的货位信息！");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								} else {
									cpsBean = cargoService.getCargoAndProductStock("cargo_id=" + ciBean.getId() + " and product_id =" + sapBean.getProductId());
									if( cpsBean == null ) {
										request.setAttribute("tip", "没有找到要操作的货位库存信息！");
										request.setAttribute("result", "failure");
										istockService.getDbOp().rollbackTransaction();
										return  mapping.findForward("soDealDeleteOS");
									}
								}
								
								//减少源锁定量
								if(!cargoService.updateCargoProductStockLockCount(cpsBean.getId(), -1)){
									request.setAttribute("tip", "货位库存操作失败，源货位冻结库存不足！");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								}
								CargoInfoBean inCargoInfo = cargoService.getCargoInfo("whole_code = '" + sapBean.getCargoWholeCode()+"'");
								if (inCargoInfo == null) {
									request.setAttribute("tip", "没有找到目的货位！");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								}
								CargoProductStockBean inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+sapBean.getProductId());
								if (inCargoProductStock == null) {
									CargoProductStockBean cpStockBean = new CargoProductStockBean();
									cpStockBean.setCargoId(inCargoInfo.getId());
									cpStockBean.setProductId(product.getId());
									cpStockBean.setStockCount(0);
									cpStockBean.setStockLockCount(0);
									if (!cargoService.addCargoProductStock(cpStockBean)) {
										istockService.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "添加目的货位库存记录失败！");
										request.setAttribute("result", "failure");
										return mapping.findForward("soDealDeleteOS");
									}
									inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+sapBean.getProductId());
								}
								//增加目的锁定量
								if(!cargoService.updateCargoProductStockLockCount(inCargoProductStock.getId(), 1)){
									request.setAttribute("tip", "货位库存操作失败，目的货位冻结库存不足！");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								}
								
								//添加货位进销存卡片
								//货位出库卡片
								CargoStockCardBean outcsc = new CargoStockCardBean();
								outcsc.setCardType(CargoStockCardBean.CARDTYPE_EXECOUTPRODUCT);
								outcsc.setCode(osBean.getCode());
								outcsc.setCreateDatetime(DateUtil.getNow());
								outcsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
								outcsc.setStockArea(osBean.getStockArea());
								outcsc.setProductId(product.getId());
								outcsc.setStockId(cpsBean.getId());
								outcsc.setStockOutCount(1);
								outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								outcsc.setCurrentStock(product.getStock(outcsc.getStockArea(), outcsc.getStockType()) + product.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
								outcsc.setAllStock(product.getStockAll() + product.getLockCountAll());
								outcsc.setCurrentCargoStock(cpsBean.getStockCount()+cpsBean.getStockLockCount());
								outcsc.setCargoStoreType(cpsBean.getCargoInfo().getStoreType());
								outcsc.setCargoWholeCode(cpsBean.getCargoInfo().getWholeCode());
								outcsc.setStockPrice(product.getPrice5());
								outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
								if(!cargoService.addCargoStockCard(outcsc)){
									request.setAttribute("tip", "货位进销存记录添加失败，请重新尝试操作！");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								}
								//货位入库卡片
								CargoStockCardBean incsc = new CargoStockCardBean();
								incsc.setCardType(CargoStockCardBean.CARDTYPE_EXECINPRODUCT);
								incsc.setCode(osBean.getCode());
								incsc.setCreateDatetime(DateUtil.getNow());
								incsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
								incsc.setStockArea(osBean.getStockArea());
								incsc.setProductId(product.getId());
								incsc.setStockId(inCargoProductStock.getId());
								incsc.setStockInCount(1);
								incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								incsc.setCurrentStock(product.getStock(incsc.getStockArea(), incsc.getStockType()) + product.getLockCount(incsc.getStockArea(), incsc.getStockType()));
								incsc.setAllStock(product.getStockAll() + product.getLockCountAll());
								incsc.setCurrentCargoStock(inCargoProductStock.getStockCount()+inCargoProductStock.getStockLockCount());
								incsc.setCargoStoreType(inCargoProductStock.getCargoInfo().getStoreType());
								incsc.setCargoWholeCode(inCargoProductStock.getCargoInfo().getWholeCode());
								incsc.setStockPrice(product.getPrice5());
								incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
								if(!cargoService.addCargoStockCard(incsc)){
									request.setAttribute("tip", "货位进销存记录添加失败，请重新尝试操作！");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								}
								//更改库存
								boolean rslt = sortingAbnormalDisposeService.rollStockBackForOne(psBean, inCargoProductStock);
								if( !rslt ) {
									request.setAttribute("tip", "库存操作失败，可能是库存冻结量不足!");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								}
							} else {
								//寻找货位库存信息
								CargoInfoBean ciBean = cargoService.getCargoInfo("whole_code='" + sapBean.getCargoWholeCode() + "'");
								if( ciBean == null ) {
									request.setAttribute("tip", "没有找到要操作的货位信息！");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								} else {
									cpsBean = cargoService.getCargoProductStock("cargo_id=" + ciBean.getId() + " and product_id =" + sapBean.getProductId());
									if( cpsBean == null ) {
										request.setAttribute("tip", "没有找到要操作的货位库存信息！");
										request.setAttribute("result", "failure");
										istockService.getDbOp().rollbackTransaction();
										return  mapping.findForward("soDealDeleteOS");
									}
								}
								//更改库存
								boolean rslt = sortingAbnormalDisposeService.rollStockBackForOne(psBean, cpsBean);
								if( !rslt ) {
									request.setAttribute("tip", "库存操作失败，可能是库存冻结量不足!");
									request.setAttribute("result", "failure");
									istockService.getDbOp().rollbackTransaction();
									return  mapping.findForward("soDealDeleteOS");
								}
							}
							//更改异常商品状态
							boolean updateResult = sortingAbnormalDisposeService.updateSortingAbnormalStatus(sapBean);
							if( !updateResult ) {
								request.setAttribute("tip", "修改异常单时， 数据库操作失败！");
								request.setAttribute("result", "failure");
								istockService.getDbOp().rollbackTransaction();
								return  mapping.findForward("soDealDeleteOS");
							}
							istockService.getDbOp().commitTransaction();
							Object result = sortingAbnormalDisposeService.getSortingAbnormalAllInfo(osBean.getCode());
							SortingAbnormalBean sortingAbnormalBean = (SortingAbnormalBean) result;
							if (sortingType == 1) {
								List<SortingAbnormalProductBean> SortingAbnormalProductBeanlist = sortingAbnormalBean.getSortingAbnormalProductList();
								if (SortingAbnormalProductBeanlist != null && SortingAbnormalProductBeanlist.size() != 0) {
									int x = SortingAbnormalProductBeanlist.size();
									for (int i = 0; i < x; i ++) {
										SortingAbnormalProductBean saProductBean = SortingAbnormalProductBeanlist.get(i);
										ResultSet rs = sortingAbnormalDisposeService.getDbOp().executeQuery("select ospc.cargo_whole_code  from order_stock_product_cargo ospc,order_stock_product osp, order_stock os , sorting_abnormal sa where sa.oper_code = os.code and osp.order_stock_id = os.id and ospc.order_stock_product_id=osp.id and sa.oper_code='"+sortingAbnormalBean.getOperCode()+"' and osp.product_id="+saProductBean.getProductId());
										if (rs.next()) {
											saProductBean.setCargoWholeCode(rs.getString(1));
										}
										rs.close();
									}
								}
							}
							session.setAttribute("EXSortingAbnormalInfo", sortingAbnormalBean);
							session.setAttribute("EXCurrentCargoInfos", null);
							return  mapping.findForward("soDealDeleteOS");
						} else {
							request.setAttribute("tip", "请到‘分拣异常商品’模块处理该SKU");
							request.setAttribute("result", "failure");
							return  mapping.findForward("soDealDeleteOS");
						}
					}
					//如果有对应 货位的缓存信息  在找这个货位上是否有这个商品的库存。 如果有 就进行库存操作。
					
					
				} else if ( code.matches("[A-Z]{3}[0-9]{2}-[A-Z]{1}[0-9]{5,8}") ) {
					//就当做 货位号来办
					// 验证是否有SortingAbnormal信息的缓存
					session.setAttribute("EXCurrentCargoInfos", null);
					SortingAbnormalBean saBean = (SortingAbnormalBean)session.getAttribute("EXSortingAbnormalInfo");
					OrderStockBean osBean = (OrderStockBean) session.getAttribute("EXOrderStockInfo");
					if( saBean == null ) {
						request.setAttribute("tip", "当前还没有异常单信息缓存，请先扫描CK号!");
						request.setAttribute("result", "failure");
						return  mapping.findForward("soDealDeleteOS");
					} else {
						if( osBean != null ) {
							if( osBean.getStockArea() != wareArea ) {
								request.setAttribute("tip", "出库单信息与操作地区不匹配！");
								request.setAttribute("result", "failure");
								return  mapping.findForward("soDealDeleteOS");
							}
						}
						//String storageCode = "";
						/*if( osBean.getStockArea() == ProductStockBean.AREA_ZC ) {
							storageCode = "GZZ01";
						} else if ( osBean.getStockArea() == ProductStockBean.AREA_WX ) {
							storageCode = "JSW01";
						} else {
							request.setAttribute("tip", "货位地区获取异常！");
							request.setAttribute("result", "failure");
							return  mapping.findForward("soDealDeleteOS");
						}*/
						//String cargoWholeCode = storageCode + "-" + code;
						boolean hasStatusChecking = sortingAbnormalDisposeService.hasStatusChecking(saBean.getSortingAbnormalProductList());
						if( hasStatusChecking ) {
							request.setAttribute("tip", "存在异常商品已经是盘点中，或者盘点完成了，不能再操作这个异常单了，请结束！");
							request.setAttribute("result", "failure");
							return  mapping.findForward("soDealDeleteOS");
						}
						String cargoWholeCode = code;
						//根据货位号  找到所有的 符合商品货位信息 放倒session里
						List<SortingAbnormalProductBean> list = sortingAbnormalDisposeService.getIsCargoInList(saBean.getSortingAbnormalProductList(), cargoWholeCode);
						if( list.size() == 0 ) {
							request.setAttribute("tip", "货位不正确！");
							request.setAttribute("result", "failure");
							return  mapping.findForward("soDealDeleteOS");
						} else {
							session.setAttribute("EXCurrentCargoInfos", list);
							return mapping.findForward("soDealDeleteOS");
						}
					}
					//验证是否有对应的货位的信息，如果有  在session 中加入一条 当前记录货位信息的 条目  key ： EXCurrentCargoInfo
				} else {
					//code错误
					request.setAttribute("tip", "输入的号码无法识别！");
					request.setAttribute("result", "failure");
					return  mapping.findForward("soDealDeleteOS");
				}
			}
		} catch (Exception e) {
			try {
				if( !sortingAbnormalDisposeService.getDbOp().getConn().getAutoCommit() ) {
					sortingAbnormalDisposeService.getDbOp().rollbackTransaction();
				}
				e.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			sortingInfoService.releaseAll();
			dbOperationSlave.release();
		}
		return  mapping.findForward("soDealDeleteOS");
	}
}
	

