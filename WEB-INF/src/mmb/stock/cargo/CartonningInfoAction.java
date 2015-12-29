package mmb.stock.cargo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.stat.BuyStockinUpshelfBean;
import mmb.stock.stat.CheckStockinMissionService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class CartonningInfoAction  extends DispatchAction{
	public static byte[] cargoLock = new byte[0];
	/**
	 * 
	 * 装箱管理  暂时不用
	 */
	public ActionForward cartonningInfo_bak(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String select = StringUtil.convertNull(request.getParameter("select"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String userName = StringUtil.convertNull(request.getParameter("userName"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String[] status=request.getParameterValues("status");
		if(status==null){
			status=new String[]{"0","1"};
		}
		try {
			StringBuilder url = new StringBuilder();
			url.append("cartonningInfoAction.do?method=cartonningInfo");
			StringBuilder sql = new StringBuilder();
			sql.append("ci.id>0");
			sql.append(" and (");
			for(int i=0;i<status.length;i++){
				if(i!=0){
					sql.append(" or ");
				}
				sql.append(" ci.status="+status[i]);
				url.append("&status=" +status[i]);
			}
			sql.append(") ");
			if(select.length()>0||select!=""){
				sql.append(" and ci.code="+"'"+select+"'");
				url.append("&select=" + Encoder.encrypt(select));
			}
			if(!"".equals(productCode)){
				sql.append(" and cpi.product_code="+"'"+productCode+"'");
				url.append("&productCode=" + productCode);
			}
			if(!"".equals(userName)){
				sql.append(" and ci.name="+"'"+userName+"'");
				url.append("&userName=" + userName);
			}
			if(!"".equals(startTime)){
				if("".equals(endTime)){
					endTime = DateUtil.getNowDateStr();
				}
				sql.append(" and left(ci.create_time,10) between '" + startTime + "' and '" + endTime + "'");
				url.append("&startTime=" + startTime + "&endTime=" + endTime);
			}
			int totalCount = service.getCartonningAndProductListCount(sql.toString(),wareService.getDbOp());
			int countPerPage = 20;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = service.getCartonningAndProductList(sql.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "ci.id desc",wareService.getDbOp());
			if(!sql.toString().equals("id>0")&&list.size()==0){
				request.setAttribute("tip", "无法找到装箱记录");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("cartonningList", list);
			request.setAttribute("url", url.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("status", status);
			paging.setPrefixUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("cartonningInfos");
	}
	
	/**
	 * 
	 * 装箱管理 
	 */
	public ActionForward cartonningInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String select = StringUtil.convertNull(request.getParameter("select"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String userName = StringUtil.convertNull(request.getParameter("userName"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String[] status=request.getParameterValues("status");
		int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
		int countPerPage = 20;
		
		if(status==null){
			status=new String[]{"0","1"};
		}
		try {
			StringBuilder url = new StringBuilder();
			url.append("cartonningInfoAction.do?method=cartonningInfo");
			StringBuilder sql = new StringBuilder();
			sql.append("ci.id>0");
			sql.append(" and (");
			for(int i=0;i<status.length;i++){
				if(i!=0){
					sql.append(" or ");
				}
				sql.append(" ci.status="+status[i]);
				url.append("&status=" +status[i]);
			}
			sql.append(") ");
			if(select.length()>0||select!=""){
				sql.append(" and ci.code="+"'"+select+"'");
				url.append("&select=" + Encoder.encrypt(select));
			}
			if(!"".equals(userName)){
				sql.append(" and ci.name="+"'"+userName+"'");
				url.append("&userName=" + userName);
			}
			if(!"".equals(startTime)){
				if("".equals(endTime)){
					endTime = DateUtil.getNowDateStr();
				}
				sql.append(" and left(ci.create_time,10) between '" + startTime + "' and '" + endTime + "'");
				url.append("&startTime=" + startTime + "&endTime=" + endTime);
			}
			
			StringBuilder countSql=new StringBuilder();
			countSql.append("select count(ci.id) from cartonning_info ci ");
			StringBuilder listSql=new StringBuilder();
			listSql.append("select ci.id,ci.code,ci.create_time,ci.status,ci.name,ci.cargo_id from cartonning_info ci ");
			
			if(!"".equals(productCode)){
				sql.append(" and cpi.product_code="+"'"+productCode+"'");
				url.append("&productCode=" + productCode);
				countSql.append(" left join cartonning_product_info cpi on cpi.cartonning_id=ci.id ");
				listSql.append(" left join cartonning_product_info cpi on cpi.cartonning_id=ci.id ");
			}
			countSql.append(" where ").append(sql);
			ResultSet countRs=service.getDbOp().executeQuery(countSql.toString());
			int totalCount = 0;
			if(countRs.next()){
				totalCount=countRs.getInt(1);
			}
			countRs.close();
			
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			
			listSql.append(" where ").append(sql);
			listSql.append(" order by ci.id desc");
			
			ResultSet listRs=service.getDbOp().executeQuery(DbOperation.getPagingQuery(listSql.toString(), pageIndex * countPerPage, countPerPage));
			List list=new ArrayList();
			while(listRs.next()){
				CartonningInfoBean ciBean = new CartonningInfoBean();
				ciBean.setId(listRs.getInt(1));
				ciBean.setCode(listRs.getString(2));
				ciBean.setCreateTime(listRs.getString(3));
				ciBean.setStatus(listRs.getInt(4));
				ciBean.setName(listRs.getString(5));
				ciBean.setCargoId(listRs.getInt(6));
				list.add(ciBean);
			}
			listRs.close();
			
			if(!sql.toString().equals("id>0")&&list.size()==0){
				request.setAttribute("tip", "无法找到装箱记录");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			for(int i=0;i<list.size();i++){
				CartonningInfoBean ciBean=(CartonningInfoBean)list.get(i);
				CartonningProductInfoBean cipBean=service.getCartonningProductInfo("cartonning_id="+ciBean.getId());
				if(cipBean!=null){
					ciBean.setProductBean(cipBean);
				}else{
					ciBean.setProductBean(new CartonningProductInfoBean());
				}
				CargoInfoBean cargo=cargoService.getCargoInfo("id="+ciBean.getCargoId());
				if(cargo!=null){
					ciBean.setCargoWholeCode(cargo.getWholeCode());
				}
			}
			
			request.setAttribute("cartonningList", list);
			request.setAttribute("url", url.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("status", status);
			paging.setPrefixUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("cartonningInfos");
	}
	
	/**
	 *
	 * 创建装箱记录
	 */
	public ActionForward createCartonningInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		int productCount = StringUtil.StringToId(request.getParameter("count"));
		int cause = StringUtil.StringToId(request.getParameter("cause"));
		WareService wareService = new WareService();
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		synchronized(cargoLock){
			try {  
				voProduct pBean = wareService.getProduct2("code="+"'"+productCode+"'");
				if(pBean==null){
					ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCode+"'");
					if(bBean==null){
						request.setAttribute("tip", "没有找到此商品");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}else{
						pBean = wareService.getProduct2("a.id="+bBean.getProductId());
					}
				}
				String code = service.getZXCodeForToday();
				CartonningInfoBean bean =new CartonningInfoBean();
				bean.setCode(code);
				bean.setCause(cause);
				bean.setCreateTime(DateUtil.getNow());
				bean.setStatus(0);
				bean.setName(user.getUsername());
				CartonningProductInfoBean productBean=new CartonningProductInfoBean();
				productBean.setProductCount(productCount);
				productBean.setProductCode(pBean.getCode());
				productBean.setProductName(pBean.getName());
				productBean.setProductId(pBean.getId());
				bean.setProductBean(productBean);
				service.getDbOp().startTransaction();
				if( !service.addCartonningInfo(bean) ) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加装箱单失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( !service.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("result", "添加装箱单失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				code = bean.getCode();
				CartonningInfoBean bean1 = service.getCartonningInfo("code="+"'"+code+"'");
				productBean.setCartonningId(bean1.getId());
				if ( !service.addCartonningProductInfo(productBean) ) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加装箱单失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				boolean isAuto = service.getDbOp().getConn().getAutoCommit();
				if( !isAuto ) {
					service.getDbOp().rollbackTransaction();
				}
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		String url="/admin/cartonningInfoAction.do?method=cartonningInfo";
		ActionForward aUrl = new ActionForward(url);
		aUrl.setRedirect(true);
		return aUrl;
	}
	/**
	 * 
	 * 作废装箱记录
	 */
	public ActionForward cancelCartonningInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String id = StringUtil.convertNull(request.getParameter("id"));
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		synchronized(cargoLock){
			try {  
				CartonningInfoBean cartonningInfo=service.getCartonningInfo("id="+id);
				CargoOperationBean cargoOperation = cargoService.getCargoOperation("id=" + cartonningInfo.getOperId());
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
				service.getDbOp().startTransaction();
				service.updateCartonningInfo("status=2", "id="+id);
				BuyStockinUpshelfBean bsuBean = checkStockinMissionService.getBuyStockinUpshelf("cartonning_info_id=" + id );
				if(bsuBean != null ) {
					if( !checkStockinMissionService.deleteBuyStockinUpshelf("id=" + bsuBean.getId()) ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "删除采购上架关联失败！");
						return mapping.findForward("soDownProduct");
					}
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		String url="/admin/cartonningInfoAction.do?method=cartonningInfo";
		return new ActionForward(url);
	}
	/**
	 * 
	 * 打印装箱记录
	 */
	public ActionForward PrintCartonningInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int id = StringUtil.StringToId(request.getParameter("id"));
		WareService wareService = new WareService();
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		synchronized(cargoLock){
			try {  
				CartonningInfoBean bean = service.getCartonningInfo("id="+id);
				CartonningProductInfoBean pBean = service.getCartonningProductInfo("cartonning_id="+id);
				service.updateCartonningInfo("status=1", "id="+id);
				bean.setProductBean(pBean);
				request.setAttribute("bean", bean);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
		}
		return mapping.findForward("cartonningInfoPrint");
	}

	/**
	 * 
	 * 装箱单关联货位
	 */
	public ActionForward cartonningCargo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String code=request.getParameter("code");//装箱单号
		String cargoWholeCode=request.getParameter("cargoWholeCode");//货位编号
		String flag = request.getParameter("flag");//用来判断是第一次关联货位，还是修改或为，flag为NULL表示第一次关联货位
		WareService wareService = new WareService();
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized(cargoLock){
			try {  
				if(code!=null){
					CartonningInfoBean bean = service.getCartonningInfo("code='"+code+"'");
					if(bean==null){
						request.setAttribute("tip", "该装箱单不存在！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(flag==null && bean.getCargoId()!=0){
						request.setAttribute("tip", "该装箱单已经关联了货位！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(bean.getStatus()==2){
						request.setAttribute("tip", "该装箱单已作废！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(code!=null&&cargoWholeCode!=null){//需要绑定
						CartonningProductInfoBean cartonningProduct=service.getCartonningProductInfo("cartonning_id="+bean.getId());
						if(cartonningProduct==null){
							request.setAttribute("tip", "该货位号无效！请检查并输入正确的货位号！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoInfoBean cargoInfo=cargoService.getCargoInfo("whole_code='"+cargoWholeCode+"'");



						if(cargoInfo==null){
							request.setAttribute("tip", "该货位号无效！请检查并输入正确的货位号！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						List areaList = CargoDeptAreaService. getCargoDeptAreaList(request);
						if (areaList != null && areaList.contains(String.valueOf(cargoInfo.getAreaId()))) {
							CargoProductStockBean cps = cargoService.getCargoProductStock("cargo_id=" + cargoInfo.getId() + " and product_id=" + cartonningProduct.getProductId());
							if (cps == null) {
								request.setAttribute("tip", "该货位未绑定该商品！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							service.updateCartonningInfo("cargo_id=" + cargoInfo.getId(), "id=" + bean.getId());
							request.setAttribute("complete", "1");
						}else{
							request.setAttribute("tip", "用户没有操作该货位的权限");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						service.updateCartonningInfo("cargo_id="+cargoInfo.getId(), "id="+bean.getId());
						request.setAttribute("reload", "reload");
						request.setAttribute("complete", "1");
					}
					if(cargoWholeCode!=null){
					}
				}
				request.setAttribute("flag", flag);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		return mapping.findForward("cartonningCargo");
	}
	/**
	 * 
	 * 装箱单关联货位
	 */
	public ActionForward cartonningCargo1(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String code=request.getParameter("code");//装箱单号
		String cargoWholeCode=request.getParameter("cargoWholeCode");//货位编号
		WareService wareService = new WareService();
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized(cargoLock){
			try {  
				if(code!=null){
					CartonningInfoBean bean = service.getCartonningInfo("code='"+code+"'");
					if(bean==null){
						request.setAttribute("tip", "该装箱单不存在！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(bean.getCargoId()!=0){
						request.setAttribute("tip", "该装箱单已经关联了货位！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(bean.getStatus()==2){
						request.setAttribute("tip", "该装箱单已作废！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(code!=null&&cargoWholeCode!=null){//需要绑定
						CartonningProductInfoBean cartonningProduct=service.getCartonningProductInfo("cartonning_id="+bean.getId());
						if(cartonningProduct==null){
							request.setAttribute("tip", "该货位号无效！请检查并输入正确的货位号！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoInfoBean cargoInfo=cargoService.getCargoInfo("whole_code='"+cargoWholeCode+"'");
						if(cargoInfo==null){
							request.setAttribute("tip", "该货位号无效！请检查并输入正确的货位号！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						List areaList = CargoDeptAreaService. getCargoDeptAreaList(request);
						if (areaList != null && areaList.contains(String.valueOf(cargoInfo.getAreaId()))) {
							CargoProductStockBean cps = cargoService.getCargoProductStock("cargo_id=" + cargoInfo.getId() + " and product_id=" + cartonningProduct.getProductId());
							if (cps == null) {
								request.setAttribute("tip", "该货位未绑定该商品！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							service.updateCartonningInfo("cargo_id=" + cargoInfo.getId(), "id=" + bean.getId());
							request.setAttribute("complete", "1");
						}else{
							request.setAttribute("tip", "用户没有操作该货位的权限");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		return mapping.findForward("cartonningCargo1");
	}
}
