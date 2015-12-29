package mmb.rec.oper.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.AbnormalCargoCheckBean;
import mmb.stock.stat.AbnormalCargoCheckProductBean;
import mmb.stock.stat.ProductLineCatalogBean;
import mmb.stock.stat.SortingAbnormalBean;
import mmb.stock.stat.SortingAbnormalDisposeService;
import mmb.stock.stat.SortingAbnormalProductBean;
import mmb.stock.stat.StatService;
import mmb.stock.stat.StockinUnqualifiedService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cache.ProductLinePermissionCache;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/AbnormalCargoCheckController")
public class AbnormalCargoCheckController {
	public static byte[] cargoLock = new byte[0];
	/**
	 * @return 权限遮罩库地区
	 * @author syuf
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/getDeptAreaComboBox")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeptAreaComboBox(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			if(areaList != null && areaList.size() > 0){
				ProductStockBean psBean = new ProductStockBean();
				comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
				for(String s : areaList){
					EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
					bean.setId(s);
					bean.setText(StringUtil.convertNull(psBean.getAreaName(StringUtil.toInt(s))));
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 生成报损报溢单
	 * @author syuf
	 */
	@RequestMapping("/generatedBsby")
	@ResponseBody
	public Json generatedBsby(HttpServletRequest request,HttpServletResponse response,String accId) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			synchronized (cargoLock) {
				sortingAbnormalDisposeService.getDbOp().startTransaction();
				String result = sortingAbnormalDisposeService.generateBsBy(StringUtil.toInt(accId), user);
				String suc = null;
				if( !result.startsWith("success") ) {
					sortingAbnormalDisposeService.getDbOp().rollbackTransaction();
					j.setMsg(result);
					return j;
				} else {
					sortingAbnormalDisposeService.getDbOp().commitTransaction();
					sortingAbnormalDisposeService.getDbOp().getConn().setAutoCommit(true);
					if( result.equals("success1") ) {
						//就是说所有的锁定量都对不上不能进行报损报溢操作了。
						suc = "没有锁定量对的上的货位，报损报溢操作未进行！";
					} else if ( result.equals("success2")) {
						//就是有锁定量相同的 但是盘点量和实际量都对上了
						suc = "操作成功,不需要生成调拨单和报损报溢单，货位数量都正确！";
					} else if ( result.equals("success3")) {
						//都通过 调拨的方式改掉了
						suc = "操作成功，有调拨单生成！";
					} else if ( result.equals("success4")) {
						//都通过 报损报溢的方式改掉了
						suc = "操作成功， 有报损报溢生成！";
					} else if ( result.equals("success5")) {
						//生成了调拨单 也生成了报损报溢单的情况
						suc = "操作成功， 有调拨单和报损报溢单生成！";
					} else if ( result.equals("success5")) {
						//生成了调拨单 也生成了报损报溢单的情况
						suc = "操作有异常，请联系管理员！";
					}
				}
				j.setSuccess(true);
				j.setMsg(suc);
			}
		} catch ( Exception e ) {
			try {
				if( !sortingAbnormalDisposeService.getDbOp().getConn().getAutoCommit() ) {
					sortingAbnormalDisposeService.getDbOp().rollbackTransaction();
				}
			} catch (SQLException e1) {}
			e.printStackTrace();
		} finally {
			dbOp.release();
			sortingAbnormalDisposeService.releaseAll();
		}
		return j;
	}
	/**
	 * @return 生成异常货位盘点计划
	 * @author syuf
	 */
	@RequestMapping("/generateCheck")
	@ResponseBody
	public Json generateCheck (HttpServletRequest request,HttpServletResponse response,String areaId) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized (cargoLock) {
			try {
				UserGroupBean group = user.getGroup();
				if(!group.isFlag(781)){
					j.setMsg("没有生成盘点计划权限!");
					return j;
				}
				//System.out.println("库地区==+"+areaId);
				AbnormalCargoCheckBean abnormalCargoCheck = abnormalService.getAbnormalCargoCheck("status <>" + AbnormalCargoCheckBean.STATUS5+" and area="+areaId);
				if(abnormalCargoCheck != null){
					j.setMsg("有未完成盘点计划 暂不能生成新的盘点计划!");
					return j;
				}
				String code=abnormalService.getNewAbnormalCargoCheckCode(Integer.parseInt(areaId),wareService.getDbOp());
				if("FAIL".equals(code)){
					j.setMsg("添加异常货位盘点编号失败!");
					return j;
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
						j.setMsg("数据库操作失败!");
						return j;
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
								j.setMsg("数据库操作失败!");
								return j;
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
										j.setMsg("更新货位异常商品状态为【盘点中】失败!");
										return j;
									}
								}
							}
						}
					}
					if(number == 0){
						wareService.getDbOp().rollbackTransaction();
						j.setMsg("没有可生成的盘点计划商品!");
						return j;
					}
					wareService.getDbOp().commitTransaction();
				}
				j.setSuccess(true);
				j.setMsg("操作成功!");
			} catch (Exception e) {
				wareService.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
				dbOp.release();
			}
		}
		return j;
	}
	/**
	 * @return 异常货位盘点计划列表
	 * @author syuf
	 */
	@RequestMapping("/getAbnormalCargoChecDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getAbnormalCargoChecDatagrid (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,String area,String status,String code) throws ServletException, IOException{
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		synchronized (cargoLock) {
			try {
				StringBuffer str = new StringBuffer();
				if(!"".equals(StringUtil.checkNull(area))){
					str.append(" and area="+area);
				}
				if(!"".equals(StringUtil.checkNull(status))){
					str.append(" and status="+status);
				}
				if(!"".equals(StringUtil.checkNull(code))){
					str.append(" and code='"+code+"'");
				}
				int totalCount = abnormalService.getAbnormalCargoCheckCount("id>0 "+str);
				datagridJson.setTotal((long)totalCount);
				List<AbnormalCargoCheckBean> list = abnormalService.getAbnormalCargoCheckList("id>0 "+str, (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "create_datetime desc") ;
				if(list != null && list.size() > 0){
					for(AbnormalCargoCheckBean bean : list){
						bean.setAreaName(ProductStockBean.getAreaName(bean.getArea()));
					}
				}
				datagridJson.setRows(list);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbOp.release();
			}
		}
		return datagridJson;
	}
	/**
	 * @return 异常货位盘点计划明细
	 * @author syuf
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/getAbnormalCargoChecDetailDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getAbnormalCargoChecDetailDatagrid (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,String productCode,String status,String cargoCode,String accId) throws ServletException, IOException{
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService=new WareService(dbOp);
		ICargoService cargoService=new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if(!"".equals(StringUtil.checkNull(accId))){
				String condition="abnormal_cargo_check_id=" + accId;
				if(!"".equals(StringUtil.checkNull(productCode))){
					voProduct product=wareService.getProduct(productCode);
					if(product!=null){
						condition+=" and product_id="+product.getId();
					}else{
						condition+=" and product_id=0";
					}
				}
				if(!"".equals(StringUtil.checkNull(cargoCode))){
					condition+=" and cargo_whole_code='"+cargoCode+"'";
				}
				if(!"".equals(StringUtil.checkNull(status))){
					condition+=" and status="+status;
				}
				int totalCount = sortingAbnormalDisposeService.getAbnormalCargoCheckProductCount(condition);
				datagridJson.setTotal((long)totalCount);
				List<AbnormalCargoCheckProductBean> accpBeanList =new ArrayList<AbnormalCargoCheckProductBean>();
				accpBeanList = sortingAbnormalDisposeService.getAbnormalCargoCheckProductList(condition, (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), null);
				if(accpBeanList != null && accpBeanList.size() > 0){
					for(AbnormalCargoCheckProductBean accpBean : accpBeanList){
						accpBean.setStatusName(accpBean.getStatusName(accpBean.getStatus()));
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
								accpBean.setStockCount(cpsBean.getStockCount());
								accpBean.setStockLockCount(cpsBean.getStockLockCount());
							}
						}
						if( accpBean.getBsbyId() != 0 ) {
							BsbyOperationnoteBean bsbyBean = bsbyService.getBsbyOperationnoteBean("id=" + accpBean.getBsbyId() );
							if( bsbyBean != null ) {
								@SuppressWarnings("unchecked")
								List<BsbyProductBean> bsbyProductBeans = bsbyService.getBsbyProductList("operation_id=" + bsbyBean.getId(), -1, -1, null);
								if(bsbyProductBeans != null && bsbyProductBeans.size() > 0){
									if(bsbyBean.getType() == 0){
										accpBean.setBsCount(bsbyProductBeans.get(0).getBsby_count());
									}else if(bsbyBean.getType() == 1){
										accpBean.setByCount(bsbyProductBeans.get(0).getBsby_count());
									}
								}
								String receipts_number = "";
								if( bsbyBean.getIf_del() == 1 ) {
									receipts_number = bsbyBean.getReceipts_number();
								} else {
									int type = bsbyBean.getCurrent_type();
									if((type==0||type==1||type==2||type==5)&&(user.getId()==bsbyBean.getOperator_id()||group.isFlag(413))){
										receipts_number = "<a href=\"" + request.getContextPath() + "/ByBsController/getByOpid.mmx?opid=" + bsbyBean.getId() + "\" target=\"_blank\">" + bsbyBean.getReceipts_number() + "</a>";
									}else if(type==6&&(user.getId()==bsbyBean.getOperator_id()||group.isFlag(229))){
										receipts_number = "<a href=\"" + request.getContextPath() + "/ByBsController/getByOpid.mmx?opid=" + bsbyBean.getId() + "\" target=\"_blank\">" + bsbyBean.getReceipts_number() + "</a>";
									}else{
										receipts_number = "<a href=\"" + request.getContextPath() + "/ByBsController/getByOpid.mmx?opid=" + bsbyBean.getId() + "&lookup=1\" target=\"_blank\">" + bsbyBean.getReceipts_number() + "</a>";
									}
									accpBean.setReceipts_number(receipts_number);
								}
							}
						}
					}
				}
				datagridJson.setRows(accpBeanList);
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagridJson;
	}
	/**
	 * @return 导出异常货位盘点计划明细
	 * @author syuf
	 */
	@RequestMapping("/exportAbnormalCargoChecDetail")
	public String exportAbnormalCargoChecDetail(HttpServletRequest request,HttpServletResponse response,
			String productCode,String status,String cargoCode,String accId) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService=new WareService(dbOp);
		ICargoService cargoService=new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			AbnormalCargoCheckBean accBean = sortingAbnormalDisposeService.getAbnormalCargoCheck("id=" + accId);
			if( accBean == null ) {
				request.setAttribute("msg", "盘点计划单错误!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			if(!"".equals(StringUtil.checkNull(accId))){
				String condition="abnormal_cargo_check_id=" + accId;
				if(!"".equals(StringUtil.checkNull(productCode))){
					voProduct product=wareService.getProduct(productCode);
					if(product!=null){
						condition+=" and product_id="+product.getId();
					}else{
						condition+=" and product_id=0";
					}
				}
				if(!"".equals(StringUtil.checkNull(cargoCode))){
					condition+=" and cargo_whole_code='"+cargoCode+"'";
				}
				if(!"".equals(StringUtil.checkNull(status))){
					condition+=" and status="+status;
				}
				List<AbnormalCargoCheckProductBean> accpBeanList =new ArrayList<AbnormalCargoCheckProductBean>();
				accpBeanList = sortingAbnormalDisposeService.getAbnormalCargoCheckProductList(condition, -1, -1, null);
				if(accpBeanList != null && accpBeanList.size() > 0){
					for(AbnormalCargoCheckProductBean accpBean : accpBeanList){
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
								@SuppressWarnings("unchecked")
								List<BsbyProductBean> bsbyProductBeans = bsbyService.getBsbyProductList("operation_id=" + bsbyBean.getId(), -1, -1, null);
								bsbyBean.setBsbyProductBeans(bsbyProductBeans);
							}
							accpBean.setBsbyBean(bsbyBean);
						}
					}
				}
				request.setAttribute("accpBeanList", accpBeanList);
				request.setAttribute("accBean", accBean);
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return "admin/cargo/abnormalCargoCheckDetailExport";
	}
	
	/**
	 * 跳转到根据SKU 生成异常货位盘点计划的准备数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/toGenerateBySKU")
	public String toGenerateBySKU(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try {
			// 得到产品线列表
			String productLinePermission = ProductLinePermissionCache
					.getProductLineIds(user);
			productLinePermission = "product_line.id in ("
					+ productLinePermission + ")";
			List productLineList = wareService
					.getProductLineList(productLinePermission);
			request.setAttribute("productLineList", productLineList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return "forward:/admin/rec/oper/abnormalCargo/generateBySKU.jsp";
	}
	
	/**
	 * 
	 * 查询以sku为单位的异常处理单的异常单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getSortingAbnormalInfoForSKU")
	@ResponseBody
	public Map<String,Object> getSortingAbnormalInfoForSKU(HttpServletRequest request, HttpServletResponse response ) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			resultMap.put("status", "fail");
			resultMap.put("tip", "当前没有登录，操作失败！");
			return resultMap;
		}
		/*UserGroupBean group = user.getGroup();
		if( !group.isFlag(745) ) {
			request.setAttribute("tip", "您没有理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}*/
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String productBarCode = StringUtil.convertNull(request.getParameter("productBarCode"));
		int productLineId = StringUtil.toInt(request.getParameter("productLine"));
		int pageIndex = 0;
		if( request.getParameter("page") != null ) {
			pageIndex = StringUtil.StringToId(request.getParameter("page"));
			pageIndex = pageIndex - 1;
		}
		int countPerPage = 50;
		if( request.getParameter("rows") != null ) {
			countPerPage = StringUtil.toInt(request.getParameter("rows"));
		}
		List sortingAbnormalList = new ArrayList();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbOperation = new DbOperation();
		dbOperation.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOperation);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOperation);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOperation);
		StockinUnqualifiedService sus = new StockinUnqualifiedService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		StringBuilder parentId1 = new StringBuilder();
		StringBuilder parentId2 = new StringBuilder();
		try {
			PagingBean paging = null;
			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int t = cdaList.size();
			for(int i = 0; i < t; i++ ) {
				availAreaIds += "," + cdaList.get(i);
			}
			if( productLineId != -1  && productLineId != 0 ) {
				sql.append("select sap.*, sum(lock_count) total_lock from sorting_abnormal sa, sorting_abnormal_product sap, product p where sa.id = sap.sorting_abnormal_id and p.id = sap.product_id and sap.status=" + SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
				sqlCount.append("select count(distinct(sap.product_id)) from sorting_abnormal sa, sorting_abnormal_product sap, product p where sa.id = sap.sorting_abnormal_id and sap.product_id = p.id and sap.status=" +SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
			} else {
				sql.append("select sap.*, sum(lock_count) total_lock from sorting_abnormal sa, sorting_abnormal_product sap where sa.id = sap.sorting_abnormal_id and sap.status=" + SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
				sqlCount.append("select count(distinct(sap.product_id)) from sorting_abnormal sa, sorting_abnormal_product sap where sa.id = sap.sorting_abnormal_id and sap.status=" +SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
			}
 			
			voProduct product1 = null;
			voProduct product2 = null;
			if( productCode != null && !productCode.equals("")) {
				product1 = wareService.getProduct(productCode);
				if( product1 ==  null ) {
					return resultMap;
				}
			}
			
			//产品线id
			if (productLineId != -1  && productLineId != 0) {
				List productLineList2 = sus.getProductLineCatalogList(
						"product_line_id = " + productLineId,
						-1, -1, null);
				for (int i = 0; i < productLineList2.size(); i++) {
					ProductLineCatalogBean plcb = (ProductLineCatalogBean) productLineList2
							.get(i);
					if (plcb.getCatalogType() == 1) {
						parentId1.append(plcb.getCatalogId()).append(",");
					}
					if (plcb.getCatalogType() == 2) {
						parentId2.append(plcb.getCatalogId()).append(",");
					}
				}
			}

			if (parentId1.length() > 0 || parentId2.length() > 0) {
				String sParentId1 = "";
				String sParentId2 = "";
				if (parentId1.length() > 0) {
					sParentId1 = parentId1.toString().substring(0,
							parentId1.length() - 1);
				}
				if (parentId2.length() > 0) {
					sParentId2 = parentId2.toString().substring(0,
							parentId2.length() - 1);
				}
				String parentIdCondition = "";
				if (sParentId1.length() > 0) {
					parentIdCondition += " and (p.parent_id1 in (" + sParentId1
							+ ")";
				}
				if (sParentId2.length() > 0) {
					if (parentIdCondition.length() > 0) {
						parentIdCondition += " or p.parent_id2 in ("
								+ sParentId2 + ")";
					} else {
						parentIdCondition += " and (p.parent_id2 in ("
								+ sParentId2 + ")";
					}
				}
				if (parentIdCondition.length() > 0) {
					parentIdCondition += ")";
				}
				if (parentIdCondition.length() != 0) {
					sql.append(parentIdCondition);
					sqlCount.append(parentIdCondition);
				}

			}
 			//产品编号， 商品条码
			if( productBarCode != null && !productBarCode.equals("") ) {
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productBarCode)+"'");
				if( bBean == null ) {
					return resultMap;
				} else {
					product2 = wareService.getProduct(bBean.getProductId());
					if( product2 == null ) {
						return resultMap;
					}
				}
			}
			if( product1 != null && product2 != null ) {
				if( product1.getId() != product2.getId() ) {
					resultMap.put("status", "fail");
					resultMap.put("tip", "所填产品编号和商品条码不匹配！");
					return resultMap;
				} else {
					sql.append(" and sap.product_id = " + product1.getId() );
					sqlCount.append(" and sap.product_id = " + product1.getId());
				}
			} else if ( product1 != null ) {
				sql.append(" and sap.product_id = " + product1.getId() );
				sqlCount.append(" and sap.product_id = " + product1.getId() );
			} else if( product2 != null ) {
				sql.append(" and sap.product_id = " + product2.getId() );
				sqlCount.append(" and sap.product_id = " + product2.getId());
			}	
			
			//地区
			if( wareArea != -1 ) {
			    sql.append(" and sa.ware_area = " + wareArea );
			    sqlCount.append(" and sa.ware_area = " + wareArea );
			} else {
			    sql.append(" and sa.ware_area = 1000" );
			    sqlCount.append(" and sa.ware_area = 1000" );
			}
			
			sql.append(" group by sap.product_id");
			int totalCount = sortingAbnormalDisposeService.getSortingAbnormalCount2(sqlCount.toString());
			sortingAbnormalList = sortingAbnormalDisposeService.getSortingAbnormalProductList2(sql.toString(), pageIndex*countPerPage, countPerPage, "total_lock desc");
			int x = sortingAbnormalList.size();
			for ( int i = 0; i < x; i++) {
				SortingAbnormalProductBean sapBean = (SortingAbnormalProductBean) sortingAbnormalList.get(i);
				
				String cargoWholeCodes = sortingAbnormalDisposeService.getCargoWholeCodes(sapBean.getProductId(), wareArea);
				
				Map<String,String> map = new HashMap<String,String>();
				map.put("product_code", sapBean.getProductCode());
				map.put("product_id", new Integer(sapBean.getProductId()).toString());
				ProductBarcodeVO bBean= bService.getProductBarcode("product_id="+ sapBean.getProductId());
				if( bBean != null && bBean.getBarcode() != null &&  !bBean.getBarcode().equals("") ) {
					map.put("product_bar_code", bBean.getBarcode());
				} else {
					map.put("product_bar_code", "");
				}
				map.put("id", new Integer(sapBean.getId()).toString());
				map.put("lock_count", new Integer(sapBean.getLockCount()).toString());
				if( cargoWholeCodes.length() != 0 ) {
					map.put("cargo_whole_code", cargoWholeCodes);
				} else {
					map.put("cargo_whole_code", sapBean.getCargoWholeCode());
				}
				resultList.add(map);
			}
			resultMap.put("rows", resultList);
			resultMap.put("total", totalCount);
		} catch(Exception e ) {
			resultMap.put("status", "fail");
			resultMap.put("tip", "系统异常！");
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return resultMap;
	}
	
	/**
	 * 生成异常货位盘点任务 根据SKU
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("generateBySKU")
	@ResponseBody
	public Map<String,String> generateBySKU(HttpServletRequest request, HttpServletResponse response ) {
		Map<String,String> resultMap = new HashMap<String,String>();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			resultMap.put("status", "fail");
			resultMap.put("tip", "未登录，操作失败！");
			return resultMap;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized (cargoLock) {
			try {
				Map<Integer,Integer> productIdMap = new HashMap<Integer,Integer>();
				String[] productIds = request.getParameterValues("productIds");
				//拼装 productId过滤用的map
				if( productIds != null ) {
					int x = productIds.length;
					for( int i = 0 ; i < x; i++ ) {
						int productId = StringUtil.toInt(productIds[i]);
						productIdMap.put(new Integer(productId), new Integer(0));
					}
				}
				int areaId = StringUtil.toInt(request.getParameter("areaId"));
				//首先获得要用来生成的sku 的id， 之后的尽量沿用原来的生成异常货位盘点计划的代码
				UserGroupBean group = user.getGroup();
				if(!group.isFlag(781)){
					resultMap.put("status", "fail");
					resultMap.put("tip", "没有生成盘点计划权限!");
					return resultMap;
				}
				//System.out.println("库地区==》》》》"+areaId);
				AbnormalCargoCheckBean abnormalCargoCheck = abnormalService.getAbnormalCargoCheck("status <>" + AbnormalCargoCheckBean.STATUS5+" and area="+areaId);
				if(abnormalCargoCheck != null){
					resultMap.put("status", "fail");
					resultMap.put("tip", "有未完成盘点计划 暂不能生成新的盘点计划!");
					return resultMap;
				}
				String code=abnormalService.getNewAbnormalCargoCheckCode(areaId,wareService.getDbOp());
				if("FAIL".equals(code)){
					resultMap.put("status", "fail");
					resultMap.put("tip", "添加异常货位盘点编号失败!");
					return resultMap;
				}else{
					wareService.getDbOp().startTransaction();
					//生成异常货位盘点计划单
					AbnormalCargoCheckBean accBean = new AbnormalCargoCheckBean();
					accBean.setCode(code);
					accBean.setCreateDatetime(DateUtil.getNow());
					accBean.setCreateUserId(user.getId());
					accBean.setCreateUserName(user.getUsername());
					accBean.setArea(areaId);
					accBean.setStatus(AbnormalCargoCheckBean.STATUS0);
					if(abnormalService.addAbnormalCargoCheck(accBean)==false){
						wareService.getDbOp().rollbackTransaction();
						resultMap.put("status", "fail");
						resultMap.put("tip", "数据库操作失败!");
						return resultMap;
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
							if(saBean==null||saBean.getWareArea()!=areaId){
								continue;
							}
							if( !productIdMap.containsKey(new Integer(bean.getProductId()))) {
								continue;
							}
							String newCode = bean.getProductCode()+bean.getCargoWholeCode();
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
					String tips = "";
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
								resultMap.put("status", "fail");
								resultMap.put("tip", "数据库操作失败!");
								return resultMap;
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
										resultMap.put("status", "fail");
										resultMap.put("tip", "更新货位异常商品状态为【盘点中】失败!");
										return resultMap;
									}
								}
							}
						} else {
							//tips += "由于异常量和货位锁定量不匹配，商品"+ entry.getValue().getProductCode() + "在货位" + entry.getValue().getCargoWholeCode() +"上的量没有参与盘点！";
							/*
							System.out.println("由于异常量和货位锁定量不匹配，商品"+ entry.getValue().getProductCode() + "在货位" + entry.getValue().getCargoWholeCode() +"上的量没有参与盘点！");
							System.out.println("货位锁定量："+cpsBean.getStockLockCount() + "id:" + cpsBean.getId());
							System.out.println("总共锁定量：" + entry.getValue().getLockCount());
							*/
						}
					}
					if(number == 0){
						wareService.getDbOp().rollbackTransaction();
						resultMap.put("status", "fail");
						resultMap.put("tip", "没有可生成的盘点计划商品!");
						return resultMap;
					}
					wareService.getDbOp().commitTransaction();
				}
				resultMap.put("status", "success");
				resultMap.put("tip","操作成功!");
			} catch (Exception e) {
				boolean isAuto = true;
				try {isAuto = wareService.getDbOp().getConn().getAutoCommit();} catch (SQLException e1) {}
				if( !isAuto ) {
					wareService.getDbOp().rollbackTransaction();
				}
				resultMap.put("status", "fail");
				resultMap.put("tip", "系统异常！");
				e.printStackTrace();
			} finally {
				dbOp.release();
			}
		}
		return resultMap;
	}
}
