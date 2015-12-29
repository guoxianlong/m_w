package mmb.stock.stat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class AbnormalCargoCheckAction extends DispatchAction {
	public static byte[] cargoLock = new byte[0];

	public ActionForward addAbnormalCargoCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String areaId = StringUtil.convertNull(request.getParameter("areaId"));
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized (cargoLock) {
			try {
				UserGroupBean group = user.getGroup();
				if(!group.isFlag(781)){
					request.setAttribute("tip", "没有生成盘点计划权限");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				AbnormalCargoCheckBean abnormalCargoCheck = abnormalService.getAbnormalCargoCheck("status <>" + AbnormalCargoCheckBean.STATUS5+" and area="+areaId);
				if(abnormalCargoCheck != null){
					request.setAttribute("tip", "有未完成盘点计划 暂不能生成新的盘点计划！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				String code=abnormalService.getNewAbnormalCargoCheckCode(Integer.parseInt(areaId),wareService.getDbOp());
				if("FAIL".equals(code)){
					request.setAttribute("tip", "添加异常货位盘点编号失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}else{
					wareService.getDbOp().startTransaction();
					//生成异常货位盘点计划单
					AbnormalCargoCheckBean accBean = new AbnormalCargoCheckBean();
					accBean.setCode(code);
					accBean.setCreateDatetime(DateUtil.getNow());
					accBean.setCreateUserId(user.getId());
					accBean.setCreateUserName(user.getUsername());
					accBean.setArea(Integer.parseInt(areaId));
					accBean.setStatus(AbnormalCargoCheckBean.STATUS0);
					if(abnormalService.addAbnormalCargoCheck(accBean)==false){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "数据库操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					int accId = dbOp.getLastInsertId();//得到刚刚插入的盘点计划单ID
					//查找异常产品列表：异常单状态为带盘点的下面的所有商品，相同货位并且相同产品编号的商品锁定量要加在一起作为一条数据
					Map<String,SortingAbnormalProductBean> sapMap = new HashMap<String, SortingAbnormalProductBean>();
					List<SortingAbnormalProductBean> sapBeanList = abnormalService.getSortingAbnormalProductList("status=" + SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK, -1, -1, null);
					//将商品和货位相同的异常单整合
					if(sapBeanList != null ){
						for(SortingAbnormalProductBean bean : sapBeanList){
							//商品code+货位code作为key 异常单对象作为值
							SortingAbnormalBean saBean=abnormalService.getSortingAbnormal("id="+bean.getSortingAbnormalId());
							if(saBean==null||saBean.getWareArea()!=Integer.parseInt(areaId)){
								continue;
							}
							String newCode = bean.getProductCode()+bean.getCargoWholeCode();
							newCode=newCode.toUpperCase();
							if(sapMap.size()>0){
								if(sapMap.containsKey(newCode)){//入库map中已经存在key 则认为此条数据为 需要整合数据
									SortingAbnormalProductBean b = sapMap.get(newCode);
									int lockCount = b.getLockCount() + bean.getLockCount();
									b.setLockCount(lockCount);
									sapMap.put(newCode, b);
								}else{
									sapMap.put(newCode, bean);
								}
							}else{
								sapMap.put(newCode, bean);
							}
						}
					}
					int number = 0;//标识 有没有真的生成盘点计划商品
					for(Map.Entry<String, SortingAbnormalProductBean> entry : sapMap.entrySet()){//将map中的数据生成盘点计划商品
						CargoInfoBean ciBean = cargoService.getCargoInfo("whole_code='" + entry.getValue().getCargoWholeCode() + "'");
						CargoProductStockBean cpsBean = cargoService.getCargoProductStock("product_id=" + entry.getValue().getProductId() + " and cargo_id=" + ciBean.getId());
						if(cpsBean == null){//如果在货位商品库存中查不到 说明数据异常 直接略过
							continue;
						}
						if(cpsBean.getStockLockCount() == entry.getValue().getLockCount()){ //如果货位库存+锁定两=异常单中的锁定两则符合条件 生成盘点计划商品
							//生成异常货位盘点计划商品
							AbnormalCargoCheckProductBean accpBean = new AbnormalCargoCheckProductBean();
							accpBean.setProductId(cpsBean.getProductId());
							accpBean.setCargoWholeCode(ciBean.getWholeCode());
							accpBean.setLockCount(cpsBean.getStockLockCount());
							accpBean.setAbnormalCargoCheckId(accId);
							accpBean.setStatus(AbnormalCargoCheckProductBean.STATUS_WAIT_FIRST_CHECK);
							if(!abnormalService.addAbnormalCargoCheckProduct(accpBean)){
								wareService.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							number++;
							//ciBean = cargoService.getCargoInfo("whole_code='" + entry.getValue().getCargoWholeCode() + "'");
							//找出刚刚生成盘点计划商品 在异常单中的所有记录 
							List<SortingAbnormalProductBean> list = abnormalService.getSortingAbnormalProductList("product_id="
									+ cpsBean.getProductId() + " and cargo_whole_code='" + ciBean.getWholeCode() + "' and status="+SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK, -1, -1, null);
							if(list != null && list.size() > 0){
								for(SortingAbnormalProductBean bean : list){
									if(!abnormalService.updateSortingAbnormalProduct("status=" + SortingAbnormalProductBean.STATUS_CHECKING, "id=" + bean.getId())){
										wareService.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "更新货位异常商品状态为【盘点中】失败！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
							}
						}
					}
					if(number == 0){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有可生成的盘点计划商品！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					wareService.getDbOp().commitTransaction();
				}
				
			} catch (Exception e) {
				wareService.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
				dbOp.release();
			}
		}
		return mapping.findForward("addAbnormalCargoCheck");
	}
	public ActionForward abnormalCargoCheckList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String storage = request.getParameter("storage");
		String status = request.getParameter("status");
		String code = request.getParameter("code");
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			// 该员工可操作的区域列表
			List areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			StringBuilder url = new StringBuilder();
			url.append("abnormalCargoCheck.do?method=abnormalCargoCheckList");
			StringBuffer str = new StringBuffer();
			if(storage!=null && storage.length()>0){
				str.append(" and area="+storage);
				url.append("&storage=" + storage);
			}
			if(status!=null && status.length()>0){
				str.append(" and status="+status);
				url.append("&status=" + status);
			}
			if(code!=null && code.length()>0){
				str.append(" and code='"+code+"'");
				url.append("&code=" + code);
			}
			int totalCount = abnormalService.getAbnormalCargoCheckCount("id>0 "+str);
			int countPerPage = 50;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = abnormalService.getAbnormalCargoCheckList("id>0 "+str, paging.getCurrentPageIndex() * countPerPage, countPerPage, "create_datetime desc") ;
			request.setAttribute("paging", paging);
			paging.setPrefixUrl(url.toString());
			request.setAttribute("list", list);
			request.setAttribute("areaList", areaList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return mapping.findForward("abnormalCargoCheckList");
	}
	
	/**
	 * 进入货位盘点的详情页
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward getAbnormalCargoCheckDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int accId = StringUtil.parstInt(request.getParameter("id"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode")).trim();
		String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode")).trim();
		int status = StringUtil.toInt(request.getParameter("status"));
		int export = StringUtil.toInt(request.getParameter("export"));//是否导出
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		WareService wareService=new WareService(dbOp);
		ICargoService cargoService=new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if(accId == 0){
				request.setAttribute("tip", "盘点计划单错误！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			AbnormalCargoCheckBean accBean = sortingAbnormalDisposeService.getAbnormalCargoCheck("id=" + accId);
			if( accBean == null ) {
				request.setAttribute("tip", "盘点计划单错误！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			StringBuilder url = new StringBuilder();
			url.append("abnormalCargoCheck.do?method=getAbnormalCargoCheckDetail&id=" + accId);
			String condition="abnormal_cargo_check_id=" + accId;
			if(!"".equals(productCode)){
				url.append("&productCode="+productCode);
				voProduct product=wareService.getProduct(productCode);
				if(product!=null){
					condition+=" and product_id="+product.getId();
				}else{
					condition+=" and product_id=0";
				}
			}
			if(!"".equals(cargoCode)){
				url.append("&cargoCode="+cargoCode);
				condition+=" and cargo_whole_code='"+cargoCode+"'";
			}
			if(status!=-1){
				url.append("&status="+status);
				condition+=" and status="+status;
			}
			int totalCount = sortingAbnormalDisposeService.getAbnormalCargoCheckProductCount(condition);
			int countPerPage = 50;//每页行数
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List<AbnormalCargoCheckProductBean> accpBeanList =new ArrayList();
			if(export==1){
				accpBeanList = sortingAbnormalDisposeService.getAbnormalCargoCheckProductList(condition, -1, -1, null);
			}else{
				accpBeanList = sortingAbnormalDisposeService.getAbnormalCargoCheckProductList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, null);
			}
			for(int i=0;i<accpBeanList.size();i++){
				AbnormalCargoCheckProductBean accpBean=accpBeanList.get(i);
				voProduct product=wareService.getProduct(accpBean.getProductId());
				if(product!=null){
					accpBean.setProductCode(product.getCode());
				}else{
					accpBean.setProductCode("");
				}
				CargoInfoBean ciBean=cargoService.getCargoInfo("whole_code='"+accpBean.getCargoWholeCode()+"'");
				if(ciBean!=null){
					CargoProductStockBean cpsBean=cargoService.getCargoProductStock("product_id="+accpBean.getProductId()+" and cargo_id="+ciBean.getId());
					if(cpsBean!=null){
						accpBean.setCargoProductStockBean(cpsBean);
					}
				}
				if( accpBean.getBsbyId() != 0 ) {
					BsbyOperationnoteBean bsbyBean = bsbyService.getBsbyOperationnoteBean("id=" + accpBean.getBsbyId() );
					if( bsbyBean != null ) {
						List<BsbyProductBean> bsbyProductBeans = bsbyService.getBsbyProductList("operation_id=" + bsbyBean.getId(), -1, -1, null);
						bsbyBean.setBsbyProductBeans(bsbyProductBeans);
					}
					accpBean.setBsbyBean(bsbyBean);
				}
			}
			paging.setPrefixUrl(url.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("accpBeanList", accpBeanList);
			request.setAttribute("accBean", accBean);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		if(export==1){
			return mapping.findForward("abnormalCargoCheckDetailExport");
		}else{
			return mapping.findForward("abnormalCargoCheckDetail");
		}
	}
	
	
	/**
	 * 
	 * 生成对应的报损报溢单
	 * @return
	 */
	public ActionForward generatedBsby(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int id = StringUtil.parstInt(request.getParameter("id"));
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			synchronized (cargoLock) {
				sortingAbnormalDisposeService.getDbOp().startTransaction();
				String result = sortingAbnormalDisposeService.generateBsBy(id, user);
				if( !result.startsWith("success") ) {
					sortingAbnormalDisposeService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", result);
				} else {
					sortingAbnormalDisposeService.getDbOp().commitTransaction();
					sortingAbnormalDisposeService.getDbOp().getConn().setAutoCommit(true);
					if( result.equals("success1") ) {
						//就是说所有的锁定量都对不上不能进行报损报溢操作了。
						request.setAttribute("tip", "没有锁定量对的上的货位，报损报溢操作未进行！");
					} else if ( result.equals("success2")) {
						//就是有锁定量相同的 但是盘点量和实际量都对上了
						request.setAttribute("tip", "操作成功,不需要生成调拨单和报损报溢单，货位数量都正确！");
					} else if ( result.equals("success3")) {
						//都通过 调拨的方式改掉了
						request.setAttribute("tip", "操作成功，有调拨单生成！");
					} else if ( result.equals("success4")) {
						//都通过 报损报溢的方式改掉了
						request.setAttribute("tip", "操作成功， 有报损报溢生成！");
					} else if ( result.equals("success5")) {
						//生成了调拨单 也生成了报损报溢单的情况
						request.setAttribute("tip", "操作成功， 有调拨单和报损报溢单生成！");
					} else if ( result.equals("success5")) {
						//生成了调拨单 也生成了报损报溢单的情况
						request.setAttribute("tip", "操作有异常，请联系管理员！");
					}

				}
				request.setAttribute("url", request.getContextPath()+"/admin/abnormalCargoCheck.do?method=getAbnormalCargoCheckDetail&id="+id);
				return mapping.findForward("tip");
			}
		} catch ( Exception e ) {
			try {
				if( !sortingAbnormalDisposeService.getDbOp().getConn().getAutoCommit() ) {
					sortingAbnormalDisposeService.getDbOp().rollbackTransaction();
				}
			} catch (SQLException e1) {}
			e.printStackTrace();
		} finally {
			sortingAbnormalDisposeService.releaseAll();
		}
		return null;
	}
	
}
