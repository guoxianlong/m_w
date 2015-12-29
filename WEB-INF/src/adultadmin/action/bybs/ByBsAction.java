package adultadmin.action.bybs;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.StockOperationAction;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInventoryBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockCardComparator;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

/**
 * 
 * @author 李青 2010-02-20
 * 
 */
public class ByBsAction extends DispatchAction {
	public static byte[] stockLock = new byte[0];
	public static byte[] bybsLock = new byte[0];
	public static Object lock = new Object();

	/**
	 * 从树形页面点击进入 2010-02-20
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward begin(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 查询报损报益的列表 `bsby_operationnote`
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("add");
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(230)) {
			request.setAttribute("tip", "当前没有权限，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("add");
		}
		int countPerPage = 50;
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		WareService wareService = new WareService(dbOp);
		String condition = "if_del=0 ";
		// 分页显示所有的报溢报损的单据 状态是if_del=0 就是没有被删除的单据
		try {
			int totalCount = service.getByBsOperationnoteCount(condition);
			// 页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = service.getByBsOperationnoteList(condition, paging.getCurrentPageIndex() * countPerPage,
					countPerPage, "add_time desc");
			
			for(int i=0;i<list.size();i++){
				BsbyOperationnoteBean bean = (BsbyOperationnoteBean)list.get(i);
				HashMap pmap = getProductListByOperationId(bean.getId(),bean.getCurrent_type());
				bean.setProductCode(pmap.get("pCode").toString());
				bean.setBsbyCount(pmap.get("pCount").toString());
				bean.setPrice(pmap.get("ptax").toString());
				bean.setAllPrice(pmap.get("pSumTax").toString());
				bean.setOriname(pmap.get("oriName").toString());
				bean.setCargoCode(pmap.get("cargoCode").toString());
				bean.setPriceNotOfTax(pmap.get("pNotax").toString());
				bean.setAllPriceNotOfTax(pmap.get("pSumNoTax").toString());
				bean.setParentName1(pmap.get("parentName1").toString());
				bean.setParentName2(pmap.get("parentName2").toString());
				bean.setParentName3(pmap.get("parentName3").toString());
				bean.setProductLine(pmap.get("productLine").toString());
				CargoInventoryBean inventory = cargoService.getCargoInventory("id = "+bean.getSource());
				if(inventory != null){
					bean.setSourceCode(inventory.getCode());
				}
			}
			
			paging.setPrefixUrl("bybs.do?method=begin");	
			request.setAttribute("list", list);
			request.setAttribute("paging", paging);
			
			List bsbyReasonList = null;
			bsbyReasonList= service.getBsbyReasonListDistinct();
			request.setAttribute("bsbyReasonList", bsbyReasonList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("list");

	}

	/**
	 * 点击“添加报损报溢单”，直接进入编辑和操作页，同时列表页生成一条报损或报溢单据记录，单子的初始状态为“处理中” 2010-02-20
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		synchronized(lock){
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(230)) {
				request.setAttribute("tip", "当前没有权限，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			int operationnoteType = StringUtil.StringToId(request.getParameter("operationnoteType"));
			int warehouse_type = StringUtil.StringToId(request.getParameter("warehouse_type"));
			int warehouse_area = StringUtil.StringToId(request.getParameter("warehouse_area"));
			if(warehouse_type==-1)
			{
				request.setAttribute("tip", "请选择库类型！");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			if(warehouse_area==-1)
			{
				request.setAttribute("tip", "请选择库区域！");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			if(warehouse_type == ProductStockBean.STOCKTYPE_AFTER_SALE){
				request.setAttribute("tip", "库类型是“售后库”的不能新建报损报溢单");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			if(warehouse_type == ProductStockBean.STOCKTYPE_CUSTOMER){
				request.setAttribute("tip", "库类型是“客户库”的不能新建报损报溢单");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			if(warehouse_type == ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING){
				request.setAttribute("tip", "库类型是“配件售后库”的不能新建报损报溢单");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			if(warehouse_type == ProductStockBean.STOCKTYPE_CUSTOMER_FITTING){
				request.setAttribute("tip", "库类型是“配件客户库”的不能新建报损报溢单");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			if(warehouse_type == ProductStockBean.STOCKTYPE_SPARE){
				request.setAttribute("tip", "库类型是“备用机库”的不能新建报损报溢单");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}			
			if(!CargoDeptAreaService. hasCargoDeptArea(request, warehouse_area, warehouse_type)){
				request.setAttribute("tip", "用户只能添加自己所属库地区和库类型的报损报溢单");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			String receipts_number = "";
			String title = "";// 日志的内容
			int typeString = 0;
			if (operationnoteType == 0) {
				// 报损
				String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
				receipts_number = createCode(code);// BS+年月日+3位自动增长数
				title = "创建新的报损表" + receipts_number;
				typeString = 0;
			} else {
				String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
				receipts_number = createCode(code);// BY+年月日+3位自动增长数
				title = "创建新的报溢表" + receipts_number;
				typeString = 1;
			}
			String nowTime = DateUtil.getNow();
			BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
			bsbyOperationnoteBean.setAdd_time(nowTime);
			bsbyOperationnoteBean.setCurrent_type(0);
			bsbyOperationnoteBean.setOperator_id(user.getId());
			bsbyOperationnoteBean.setOperator_name(user.getUsername());
			bsbyOperationnoteBean.setReceipts_number(receipts_number);
			bsbyOperationnoteBean.setWarehouse_area(warehouse_area);
			bsbyOperationnoteBean.setWarehouse_type(warehouse_type);
			bsbyOperationnoteBean.setType(typeString);
			bsbyOperationnoteBean.setIf_del(0);
			bsbyOperationnoteBean.setFinAuditId(0);
			bsbyOperationnoteBean.setFinAuditName("");
			bsbyOperationnoteBean.setFinAuditRemark("");
			IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(
					IBaseService.CONN_IN_SERVICE, null);
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
			bsbyOperationnoteBean.setId(maxid + 1);
			try {
				service.getDbOp().startTransaction();
				boolean falg = service.addBsbyOperationnoteBean(bsbyOperationnoteBean);
				if (falg) {
					request.setAttribute("opid", Integer.valueOf(bsbyOperationnoteBean.getId()));// 添加成功将id传到下个页面
					// 添加操作日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(nowTime);
					bsbyOperationRecordBean.setInformation(title);
					bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
					service.addBsbyOperationRecord(bsbyOperationRecordBean);
	
				} else {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward("add");
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		return mapping.findForward("add");
	}

	// 产生报损或者报溢的编号
	public static String createCode(String code) {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0 and receipts_number like '" + code + "%'");
			BsbyOperationnoteBean plan;
			plan = service.getBuycode("receipts_number like '" + code + "%'");
			if (plan == null) {
				// 当日第一份计划，编号最后三位 001
				code += "0001";
			} else {
				// 获取当日计划编号最大值
				plan = service.getBuycode("id =" + maxid);
				String _code = plan.getReceipts_number();
				int number = Integer.parseInt(_code.substring(_code.length() - 4));
				number++;
				code += String.format("%04d", new Object[] { new Integer(number) });
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

		return code;
	}

	/**
	 * 添加成功后跳转后添加页面 将具体的信息传到具体添加页面 2010-02-20
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward getByOpid(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int opid = StringUtil.StringToId(request.getParameter("opid"));
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(
				IBaseService.CONN_IN_SERVICE, null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		WareService wareService = new WareService(service.getDbOp());
		try {
			BsbyOperationnoteBean bsbyOperationnoteBean = service.getBsbyOperationnoteBean("id=" + opid);
			if(bsbyOperationnoteBean == null){
				request.setAttribute("tip", "该单据不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward("add");
			}
			request.setAttribute("bsbyOperationnoteBean", bsbyOperationnoteBean);
			//如果是售后库或客户库的报损报溢单则在新的功能里去审核编辑2014-11-27 lining
			if(bsbyOperationnoteBean.getWarehouse_type()==ProductStockBean.STOCKTYPE_AFTER_SALE  || bsbyOperationnoteBean.getWarehouse_type()==ProductStockBean.STOCKTYPE_CUSTOMER){
				request.setAttribute("tip", "售后库的报损报溢审核请到售后仓内管理--报损报溢--报损报溢列表查找"+bsbyOperationnoteBean.getReceipts_number()+"的报损报溢单进行审核操作!");
				request.setAttribute("result", "failure");
				return mapping.findForward("failure");
			}
			// 单子的初始状态为“处理中”，对应操作为“提交审核”，提交后状态改为“审核中”，“审核中”的单据对应操作“通过审核”和“未通过审核”，未通过审核状态改为“审核未通过”，对应的操作为“提交审核”，通过审核状态改为“已完成”
			int current_type = bsbyOperationnoteBean.getCurrent_type();
			String buttonString = "";
			String buttonString1 = "";
			if (current_type == 0 || current_type == 2 || current_type == 5) {
				// 处理中
				buttonString = "提交审核";
				request.setAttribute("buttonString", buttonString);
				request.setAttribute("type", Integer.valueOf(1));
			} else if (current_type == 6) {
				buttonString = "通过财务审核";
				buttonString1 = "未通过财务审核";
				request.setAttribute("buttonString", buttonString);
				request.setAttribute("type", Integer.valueOf(3));
				request.setAttribute("buttonString1", buttonString1);
				request.setAttribute("type1", Integer.valueOf(2));
			}else if(current_type==1){
				buttonString = "通过运营审核";
				buttonString1 = "未通过运营审核";
				request.setAttribute("buttonString", buttonString);
				request.setAttribute("type", Integer.valueOf(6));
				request.setAttribute("buttonString1", buttonString1);
				request.setAttribute("type1", Integer.valueOf(5));
			}
			List bsbyReasonList = null;
			if (bsbyOperationnoteBean.getType() == 0) {
				bsbyReasonList= service.getBsbyReasonList(0);
				request.setAttribute("title", "报损");
			} else {
				bsbyReasonList= service.getBsbyReasonList(1);
				request.setAttribute("title", "报溢");
			}
			request.setAttribute("bsbyReasonList", bsbyReasonList);
			// 查询这个单据的所有产品
			List list = service.getBsbyProductList("operation_id=" + opid, -1, -1, null);
			for(int i=0;i<list.size();i++){
				BsbyProductBean bsbyProduct = (BsbyProductBean)list.get(i);
				voProduct product = wareService.getProduct(bsbyProduct.getProduct_id());
				if(product!=null){
					voCatalog catalog1 = (voCatalog) CatalogCache.catalogs.get(product.getParentId1());
					voCatalog catalog2 = (voCatalog) CatalogCache.catalogs.get(product.getParentId2());
					voCatalog catalog3 = (voCatalog) CatalogCache.catalogs.get(product.getParentId3());
					if(catalog1!=null){
						bsbyProduct.setParentName1(catalog1.getName());
					}else{
						bsbyProduct.setParentName1("");
					}
					if(catalog2!=null){
						bsbyProduct.setParentName2(catalog2.getName());
					}else{
						bsbyProduct.setParentName2("");
					}
					if(catalog3!=null){
						bsbyProduct.setParentName3(catalog3.getName());
					}else{
						bsbyProduct.setParentName3("");
					}
					voProductLine productLineBean = wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
					if(productLineBean!=null){
						bsbyProduct.setProductLine(productLineBean.getName());
					}else{
						bsbyProduct.setProductLine("");
					}
				}
				BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProduct.getId());
				if (bsbyOperationnoteBean.getCurrent_type() == 0) {
					float price5 = product.getPrice5();//含税金额
					//不含税金额
					float notaxProductPrice = service.returnFinanceProductPrice(bsbyProduct.getProduct_id());
					bsbyProduct.setPrice(price5);
					bsbyProduct.setNotaxPrice(notaxProductPrice);
				} else {
					bsbyProduct.setPrice(bsbyProduct.getPrice());
					bsbyProduct.setNotaxPrice(bsbyProduct.getNotaxPrice());
				}
				if(bsbyCargo != null){
					CargoProductStockBean cps = cargoService.getCargoProductStock("id = "+bsbyCargo.getCargoProductStockId());
					CargoInfoBean ci = cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId());
					bsbyCargo.setCps(cps);
					bsbyCargo.setCargoInfo(ci);
					bsbyProduct.setBsbyCargo(bsbyCargo);
				}
				
			}
			
			CargoInventoryBean inventory = cargoService.getCargoInventory("id = "+bsbyOperationnoteBean.getSource());
			if(inventory != null){
				bsbyOperationnoteBean.setSourceCode(inventory.getCode());
			}
			
			CargoInfoAreaBean area = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
			request.setAttribute("bsbyProductList", list);
			request.setAttribute("listCount", Integer.valueOf(list.size()));
			request.setAttribute("stockArea", area);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		String lookup = StringUtil.dealParam(request.getParameter("lookup"));
		if (lookup != null) {
			return mapping.findForward("lookup");
		} else {
			return mapping.findForward("edit");
		}

	}
	/**
	 * 删除单据
	 * 2010-02-25
	 * 
	 * @param request
	 * @param response
	 */
	public void delBybsOpre(HttpServletRequest request, HttpServletResponse response) {
		String opid = StringUtil.dealParam(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail&&bean.getCurrent_type()!=BsbyOperationnoteBean.fin_audit_Fail){
				request.setAttribute("tip", "单据已提交审核，无法删除！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			// boolean flag = service.deleteBsbyOperationnote("id=" + opid);
			// 删除单据只是修改单据的status=1
			boolean flag = service.updateBsbyOperationnoteBean("if_del=1", "id=" + opid);
			if (flag) {
				// 删除单据 日志 对于的产品
				// service.deleteBsbyProduct("operation_id=" + opid);
				// service.deleteBsbyOperationRecord("operation_id=" + opid);
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("删除" + StringUtil.dealParam(request.getParameter("code")));
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(opid));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);
			} else {
				request.setAttribute("tip", "删除失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}
	}

	/**
	 * 修改审核意见
	 * 2010-03-29
	 * 李青
	 * @param request
	 * @param response
	 */
	public void updateExamineSuggestion(HttpServletRequest request, HttpServletResponse response)
	{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		String examineSuggestion = StringUtil.dealParam(request.getParameter("examineSuggestion"));
		String id = StringUtil.dealParam(request.getParameter("opid"));

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			//examineSuggestion=(new String(examineSuggestion.getBytes("iso8859-1"),"utf-8"));
			boolean falg = service.updateBsbyOperationnoteBean("examineSuggestion='" + examineSuggestion + "'", "id=" + id);
			if (falg) {
				request.setAttribute("opid", id);
				// 添加日志
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);

				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的审核意见为:" + examineSuggestion);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);

			} else {
				request.setAttribute("tip", "修改失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}
	
	/**
	 * 修改财务审核意见
	 * @param request
	 * @param response
	 */
	public void updateFinAuditRemark(HttpServletRequest request, HttpServletResponse response)
	{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		String finAuditRemark = StringUtil.dealParam(request.getParameter("finAuditRemark"));
		String id = StringUtil.dealParam(request.getParameter("opid"));

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			//examineSuggestion=(new String(examineSuggestion.getBytes("iso8859-1"),"utf-8"));
			boolean falg = service.updateBsbyOperationnoteBean("fin_audit_remark='" + finAuditRemark + "'", "id=" + id);
			if (falg) {
				request.setAttribute("opid", id);
				// 添加日志
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);

				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的运营审核意见为:" + finAuditRemark);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return;
				}
			} else {
				request.setAttribute("tip", "修改失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}
	
	/**
	 * 修改备注 2010-02-21
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public void editRemark(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		String remark = StringUtil.dealParam(request.getParameter("remark"));
		String id = StringUtil.dealParam(request.getParameter("id"));
		int   biaodantype = StringUtil.StringToId(request.getParameter("biaodantype"));
		String title = null;
		if(biaodantype==0)
		{
			title = "报损";
		}else {
			title = "报溢";
		}
		if ("".equals(remark)) {
			request.setAttribute("tip", title+"原因不能为空！");
			request.setAttribute("result", "failure");
			return;
		}		

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			boolean falg = service.updateBsbyOperationnoteBean("remark='" + remark + "'", "id=" + id);
			if (falg) {
				request.setAttribute("opid", id);
				// 添加日志
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);

				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的"+title+"原因为:" + remark);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);

			} else {
				request.setAttribute("tip", "修改失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}

	/**
	 * 更改状态 2010-02-22
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	public void updateCurrentType(HttpServletRequest request, HttpServletResponse response)
	throws UnsupportedEncodingException {
		synchronized (bybsLock) {
			
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		int biaodantype = StringUtil.StringToId(request.getParameter("biaodantype"));
		int type = StringUtil.toInt(request.getParameter("type"));
		int type1 = StringUtil.toInt(request.getParameter("type1"));
		String id = StringUtil.dealParam(request.getParameter("opid"));
		if (type == -1 && type1 == -1) {
			// 没有点击更改状态

		} else {

			/*取消页面判断 只进行数据库判断 (陈丽华意见)2010-04-14 李青
			 * String remark = StringUtil.dealParam(request.getParameter("remark"));
			remark  = new String(remark.getBytes("ISO-8859-1"),"utf-8");


			if ("".equals(remark)) {
				request.setAttribute("tip", title+"原因不能为空！");
				request.setAttribute("result", "failure");
				return;
			}*/
			IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, null);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			WareService wareService = new WareService(service.getDbOp());
			try {
				List list = service.getBsbyProductList("operation_id=" + id, -1, -1, null);
				if(list.size()==0)
				{
					request.setAttribute("tip", "您还没有添加商品！");
					request.setAttribute("result", "failure");
					return;
				}
				boolean falg = false;
				String alert = null;
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);
				if (bean.getType()==0) {
					alert="报损原因不能为空";
				}else {
					alert="报溢原因不能为空";
				}
				if(bean.getRemark()==null)
				{
					request.setAttribute("tip", alert);
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getCurrent_type()==type)
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(type))+"!");
					request.setAttribute("result", "failure");
					return;
				}
				else if((bean.getCurrent_type()==2&&type1==3)||(bean.getCurrent_type()==3&&type1==2))
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(bean.getCurrent_type()))+"!");
					request.setAttribute("result", "failure");
					return;
				}
				else if(bean.getCurrent_type()==type1)
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(type1))+"!");
					request.setAttribute("result", "failure");
					return;
				}
				service.getDbOp().startTransaction();
				String zhuangtai = "";
				if (type != -1) {
					if (type==3) {//审核通过
						
						//添加审核人信息 不是最后的完成人 审核
//						service.updateBsbyOperationnoteBean("current_type=3 , end_time='"
//						+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
//						+ user.getUsername() + "'", "id=" + id);
//
//						BsbyProductBean bpb = service.getBsbyProductBean("operation_id="+id);
//
//						list = service.getBsbyProductList("operation_id=" + id, -1, -1, null);
//						if (list.size() != 0 && bean.getType() == 0) {
//							Iterator it = list.iterator();
//							for (; it.hasNext();) {
//								BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
//								int productId = bsbyProductBean.getProduct_id();
//								// 每一个单据中的产品 依次进行修改库存操作
//								String titleString = "";
//
//								voProduct product = adminService.getProduct(productId);
//								// 得到这个产品的所有库存的列表
//								product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
//
//								// 出库 报损就是出库
//
//
//								// 如果出库的量大于 商品所在库的库存 就提示
//								if (bsbyProductBean.getBsby_count() > product.getStock(bean.getWarehouse_area(), bean
//										.getWarehouse_type())) {
//									request.setAttribute("tip", "可用库存不足,操作失败！");
//									request.setAttribute("result", "failure");
//									return;
//								}
//							}
//						}

						if (bean!=null) {
							if(bean.getCurrent_type()==4)
							{
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "此单据已经完成！");
								request.setAttribute("result", "failure");
								return;
							}
							else if(bean.getExamineSuggestion()==null||bean.getExamineSuggestion().equals("")){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "财务审核意见不能为空！");
								request.setAttribute("result", "failure");
								return;
							}
							else {
								
								// 如果是改为已完成 就要添加审核人的信息
								if(!service.updateBsbyOperationnoteBean("current_type=4 , end_time='"
										+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
										+ user.getUsername() + "'", "id=" + id))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  return;
								}
								/**
								 * 单据状态改为完成 就要改变库存 如果是报损就要剪掉库存 如果是报溢就要添加批次 如果没有调整的产品
								 * 就不执行这个方法
								 */
								BsbyProductBean bsbyProductBean =  service.getBsbyProductBean("operation_id="+id);
								int beforeChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());

								if(!updateStock(bean, request, response, service.getDbOp())){
									request.setAttribute("tip", "数据库操作失败");
									request.setAttribute("result", "failure");
									return;
								}

								/**
								 * 更改为完成后,要将最后的库存和改变后的库存的量记录
								 */
								int afterChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());


								if(!service.updateBsbyProductBean("before_change="+beforeChangeProductCount+", after_change="+afterChangeProductCount, "id="+bsbyProductBean.getId()))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  return;
								}
								// 添加日志
								BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
								bsbyOperationRecordBean.setOperator_id(user.getId());
								bsbyOperationRecordBean.setOperator_name(user.getUsername());
								bsbyOperationRecordBean.setTime(DateUtil.getNow());
								bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:已完成");
								bsbyOperationRecordBean.setOperation_id(bean.getId());
								if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  return;
								}

							}

						}
						request.setAttribute("look", "look");

					}else if (type==6) {//财务审核通过
						
						if (bean!=null) {
							if(bean.getCurrent_type()==4)
							{   
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "此单据已经完成！");
								request.setAttribute("result", "failure");
								return;
							}
//							else if(bean.getFinAuditRemark()==null||bean.getFinAuditRemark().equals("")){
//								service.getDbOp().rollbackTransaction();
//								request.setAttribute("tip", "运营审核意见不能为空！");
//								request.setAttribute("result", "failure");
//								return;
//							}
							else {
								if(!service.updateBsbyOperationnoteBean("current_type=6,fin_audit_datetime='"+DateUtil.getNow()+"',fin_audit_id="+user.getId()+",fin_audit_name='"+user.getUsername()+"'", "id=" + id))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  return;
								}
								// 添加日志
								BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
								bsbyOperationRecordBean.setOperator_id(user.getId());
								bsbyOperationRecordBean.setOperator_name(user.getUsername());
								bsbyOperationRecordBean.setTime(DateUtil.getNow());
								bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:审核通过");
								bsbyOperationRecordBean.setOperation_id(bean.getId());
								if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  return;
								}

							}

						}
					}else{
						if (type == 1) {//报损单提交审核，锁定库存量
							//报损单中的所有产品
							List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
							Iterator it = bsbyList.iterator();
							if(bean.getType() == 0){
								for (; it.hasNext();) {
									BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
									BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
									if(bsbyCargo == null){
										request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}
									voProduct product = wareService.getProduct(bsbyProductBean.getProduct_id());
									float price5 = product.getPrice5();//含税金额
									//不含税金额
									float notaxProductPrice = service.returnFinanceProductPrice(bsbyProductBean.getProduct_id());
									if(!service.updateBsbyProductBean("price="+price5+",notax_price="+notaxProductPrice, "id="+bsbyProductBean.getId())){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "更新报损报溢商品信息失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}
									String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
									+ "area = " + bean.getWarehouse_area() + " and type = "
									+ bean.getWarehouse_type();
									ProductStockBean psBean = psService.getProductStock(sql);
									//减少库存
									if(!psService.updateProductStockCount(psBean.getId(), -bsbyProductBean.getBsby_count())){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}
									//增加库存锁定量
									if (!psService.updateProductLockCount(psBean.getId(), bsbyProductBean.getBsby_count())) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}

									//锁定货位库存
									//出库
									if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
										request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
									if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
										request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
									
									
								}
							}else if(bean.getType() == 1){
								for (; it.hasNext();) {
									BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
									BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
									if(bsbyCargo == null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}
									voProduct product = wareService.getProduct(bsbyProductBean.getProduct_id());
									float price5 = product.getPrice5();//含税金额
									//不含税金额
									float notaxProductPrice = service.returnFinanceProductPrice(bsbyProductBean.getProduct_id());
									if(!service.updateBsbyProductBean("price="+price5+",notax_price="+notaxProductPrice, "id="+bsbyProductBean.getId())){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "更新报损报溢商品信息失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}
									//锁定货位空间
									if(cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId()+" and status = 0")==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "目的货位不存在或已被清空，操作失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}
									if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count + "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
										request.setAttribute("tip", "操作失败");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
								}
							}
						}
						falg = service.updateBsbyOperationnoteBean("current_type=" + type, "id=" + id);
						if(!falg)
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type));
					}
				}else if(type1==2){
					falg = service.updateBsbyOperationnoteBean("current_type=2 , end_time='"
							+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
							+ user.getUsername() + "'", "id=" + id);
					if(!falg)
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type1));
					if (bean.getType() == 0) {//报损单审核未通过，操作库存锁定量
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	                    return;
							}
							String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
										+ "area = " + bean.getWarehouse_area() + " and type = "
										+ bean.getWarehouse_type();
							ProductStockBean psBean = psService.getProductStock(sql);
							//增加库存
							if(!psService.updateProductStockCount(psBean.getId(), bsbyProductBean.getBsby_count())){
								service.getDbOp().rollbackTransaction();
	                        	request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	                    return;
	                        }
							//减去库存锁定量
							if (!psService.updateProductLockCount(psBean.getId(), -bsbyProductBean.getBsby_count())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			                    return;
							}
							
							//解锁货位库存
							if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}

							if(bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED){
								//更新订单缺货状态
								this.updateLackOrder(bsbyProductBean.getProduct_id());
							}
						}
					}else if(bean.getType() == 1){//报溢单审核未通过，解锁空间锁定值
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	                    return;
							}
							
							if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count - "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
								request.setAttribute("tip", "操作失败");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
					}
				}else if(type1==5){
					falg = service.updateBsbyOperationnoteBean("current_type=5 , fin_audit_datetime='"
							+ DateUtil.getNow() + "' , fin_audit_id=" + user.getId() + " , fin_audit_name='"
							+ user.getUsername() + "'", "id=" + id);
					if(!falg)
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type1));
					if (bean.getType() == 0) {//报损单财务审核未通过，操作库存锁定量
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	                    return;
							}
							String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
										+ "area = " + bean.getWarehouse_area() + " and type = "
										+ bean.getWarehouse_type();
							ProductStockBean psBean = psService.getProductStock(sql);
							//增加库存
							if(!psService.updateProductStockCount(psBean.getId(), bsbyProductBean.getBsby_count())){
								service.getDbOp().rollbackTransaction();
	                        	request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	                    return;
	                        }
							//减去库存锁定量
							if (!psService.updateProductLockCount(psBean.getId(), -bsbyProductBean.getBsby_count())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			                    return;
							}
							
							//解锁货位库存
							if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							
							if(bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED){
								//更新订单缺货状态
								this.updateLackOrder(bsbyProductBean.getProduct_id());
							}
						}
					}else if(bean.getType() == 1){//报溢单财务审核未通过，解锁空间锁定值
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	                    return;
							}
							
							if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count - "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
								request.setAttribute("tip", "操作失败");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
						}
					}
				}else {
					falg = service.updateBsbyOperationnoteBean("current_type=" + type1, "id=" + id);
					if(!falg)
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type1));
				}
				if (falg) {

					/*// 如果备注也被修改了 那就连同备注一起更新 
					if (bean.getRemark() != null && bean.getRemark().equals(remark)) { } else {
					  service.updateBsbyOperationnoteBean("remark='" + remark + "'", "id=" + id); // 添加日志
					  int logId = service.getNumber("id",
					  "bsby_operation_record", "max", "id > 0") + 1;
					  BsbyOperationRecordBean bsbyOperationRecordBean = new
					  BsbyOperationRecordBean();
					  bsbyOperationRecordBean.setId(logId);
					  bsbyOperationRecordBean.setOperator_id(user.getId());
					  bsbyOperationRecordBean.setOperator_name(user.getUsername());
					  bsbyOperationRecordBean.setTime(DateUtil.getNow());
					  bsbyOperationRecordBean.setInformation("修改单据:" +
					  bean.getReceipts_number() + "的备注为:" + remark);
					  bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
					  service.addBsbyOperationRecord(bsbyOperationRecordBean); }
					 */
					request.setAttribute("opid", id);
					// 添加日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:" + zhuangtai);
					bsbyOperationRecordBean.setOperation_id(bean.getId());
					if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		}
	}

	/**
	 * 根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 2010-02-22
	 * 
	 * @param productCode
	 * @param area
	 * @param type
	 * @return
	 */
	public static int getProductCount(int productid, int area, int type) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service
				.getDbOp());
		int x = 0;
		try {
			voProduct product = wareService.getProduct(productid);
			product.setPsList(psService.getProductStockList("product_id=" + productid, -1, -1, null));
//			x = product.getStock(area, type) + product.getLockCount(area, type);
			x = product.getStock(area, type);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return x;

	}

	/**
	 * 得到报损或者报溢后的产品的数量 2010-02-22
	 * 
	 * @param x
	 * @param Type
	 * @return
	 */
	public static int updateProductCount(int x, int type, int count) {
		int result = 0;
		if (type == 0) {
			// 报损
			result = x - count;
		} else {
			result = x + count;
		}
		return result;
	}

	/**
	 * 删除报损报溢的商品 2010-02-22
	 * 
	 * @param request
	 * @param response
	 */
	public void delByBsProduct(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		String bsbypid = StringUtil.dealParam(request.getParameter("bsbypid"));
		String pid = StringUtil.dealParam(request.getParameter("pid"));
		String opid = StringUtil.dealParam(request.getParameter("opid"));
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail&& bean.getCurrent_type()!=BsbyOperationnoteBean.fin_audit_Fail){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "单据已提交审核，无法删除！");
				request.setAttribute("result", "failure");
				return;
			}
			boolean flag = service.deleteBsbyProduct("id=" + bsbypid);
			if(!service.deleteBsbyProductCargo("bsby_product_id=" + bsbypid))
			{
			  service.getDbOp().rollbackTransaction();
			  request.setAttribute("tip", "数据库操作失败");
			  request.setAttribute("result", "failure");
			  return;
			}
			if (flag) {
				// 添加日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "删除产品:" + pid);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(opid));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);
			} else {
				request.setAttribute("tip", "删除失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}
	}

	/**
	 * 添加报损报溢的商品 2010-02-21
	 * 
	 * @param request
	 * @param response
	 */
	public void addByBsProduct(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(230);
		if (!viewAll) {
			request.setAttribute("tip", "你无权操作");
			request.setAttribute("result", "failure");
			return;
		}
		synchronized (stockLock) {
			String productCode = StringUtil.dealParam(request.getParameter("productCode"));
			String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
			if (StringUtil.convertNull(productCode).equals("")) {
				request.setAttribute("tip", "请输入产品编号！");
				request.setAttribute("result", "failure");
				return;
			}
			String planCountGD = request.getParameter("planCountGD");
			int opid = StringUtil.StringToId(request.getParameter("opid"));
			if (StringUtil.convertNull(planCountGD).equals("")) {
				request.setAttribute("tip", "请输入产品数量！");
				request.setAttribute("result", "failure");
				return;
			}
			if(cargoCode.equals("")){
				request.setAttribute("tip", "货位号不能为空！");
				request.setAttribute("result", "failure");
				return;
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE);
			WareService wareService = new WareService(dbOp);
			voProduct product = wareService.getProduct(productCode);
			wareService.releaseAll();

			if (product == null) {
				request.setAttribute("tip", "不存在这个编号的产品！");
				request.setAttribute("result", "failure");
				return;
			}
			if (product.getParentId1() == 106) {
				request.setAttribute("tip", "该商品为新商品，请先修改该产品的分类");
				request.setAttribute("result", "failure");
				return;
			}
			if (product.getIsPackage() == 1) {
				request.setAttribute("tip", "该产品为套装产品，不能添加！");
				request.setAttribute("result", "failure");
				return;
			}
			IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,null);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				service.getDbOp().startTransaction();
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
				if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail){
					request.setAttribute("tip", "单据已提交审核，无法修改！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				if (service.getBsbyProductBean("operation_id = " + opid + " and product_code = " + productCode) != null) {
					request.setAttribute("tip", "该产品已经添加，直接修改即可，不用重复添加！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				/**
				 * 打开两个页面,先后提交,每次提交时都要检查是否已经添加过商品了;
				 */
				if (service.getBsbyProductBean("operation_id = " + opid ) != null) {
					request.setAttribute("tip", "每个单据只能添加一个商品！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				BsbyOperationnoteBean ben = service.getBsbyOperationnoteBean("id=" + opid);
				int x = getProductCount(product.getId(), ben.getWarehouse_area(), ben.getWarehouse_type());
				int result = updateProductCount(x, ben.getType(), StringUtil.toInt(planCountGD));
				if (result < 0 ) {
					request.setAttribute("tip", "您所添加商品的库存不足！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				//新货位管理判断
				CargoProductStockBean cps = null;
				if(ben.getType()==0){
			        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
			        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+ben.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	request.setAttribute("tip", "货位号"+cargoCode+"无效，请重新输入！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        cps = (CargoProductStockBean)cpsOutList.get(0);
			        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cps.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
			        	request.setAttribute("tip", "合格库缓存区暂时不能进行报损报溢操作！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        if(StringUtil.toInt(planCountGD) > cps.getStockCount()){
			        	request.setAttribute("tip", "该货位"+cargoCode+"库存为" + cps.getStockCount() + "，库存不足！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
				}else{
					CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
					CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ben.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+cargoCode+"' and status <> "+CargoInfoBean.STATUS3);
			        if(cargo == null){
			        	request.setAttribute("tip", "货位号"+cargoCode+"无效，请重新输入！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        if(cargo.getStatus() == CargoInfoBean.STATUS2){
			        	request.setAttribute("tip", "货位"+cargoCode+"未开通，请重新输入！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cargo.getStoreType() == CargoInfoBean.STORE_TYPE2){
			        	request.setAttribute("tip", "合格库缓存区暂时不能进行报损报溢操作！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	if(cargo.getStatus() == CargoInfoBean.STATUS0 && (cargo.getStoreType() == CargoInfoBean.STORE_TYPE0||cargo.getStoreType() == CargoInfoBean.STORE_TYPE4)){
			        		request.setAttribute("tip", "货位"+cargoCode+"被其他商品使用中，添加失败！");
				            request.setAttribute("result", "failure");
				            service.getDbOp().rollbackTransaction();
				            return;
			        	}
			        	cps = new CargoProductStockBean();
			        	cps.setCargoId(cargo.getId());
			        	cps.setProductId(product.getId());
			        	cps.setStockCount(0);
			        	cps.setStockLockCount(0);
			        	if(!cargoService.addCargoProductStock(cps))
			        	{
			        	  service.getDbOp().rollbackTransaction();
			        	  request.setAttribute("tip", "数据库操作失败");
			        	  request.setAttribute("result", "failure");
			        	  return;
			        	}
			        	cps.setId(cargoService.getDbOp().getLastInsertId());
			        	
			        	if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId()))
			        	{
			        	  service.getDbOp().rollbackTransaction();
			        	  request.setAttribute("tip", "数据库操作失败");
			        	  request.setAttribute("result", "failure");
			        	  return;
			        	}
			        }else{
			        	cps = (CargoProductStockBean)cpsOutList.get(0);
			        }
				}
		        

				BsbyProductBean bsbyProductBean = new BsbyProductBean();
				bsbyProductBean.setBsby_count(StringUtil.toInt(planCountGD));
				bsbyProductBean.setOperation_id(opid);
				bsbyProductBean.setProduct_code(productCode);
				bsbyProductBean.setProduct_id(product.getId());
				bsbyProductBean.setProduct_name(product.getName());
				bsbyProductBean.setOriname(product.getOriname());
				bsbyProductBean.setAfter_change(result);
				bsbyProductBean.setBefore_change(x);
				float price5 = product.getPrice5();//含税金额
				//不含税金额
				float notaxProductPrice = service.returnFinanceProductPrice(bsbyProductBean.getProduct_id());
				bsbyProductBean.setPrice(price5);
				bsbyProductBean.setNotaxPrice(notaxProductPrice);
				boolean falg = service.addBsbyProduct(bsbyProductBean);
				if (!falg) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
				bsbyCargo.setBsbyOperId(ben.getId());
				bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
				bsbyCargo.setCount(StringUtil.toInt(planCountGD));
				bsbyCargo.setCargoProductStockId(cps.getId());
				bsbyCargo.setCargoId(cps.getCargoId());
				if(!service.addBsbyProductCargo(bsbyCargo))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return;
				}
				// 添加日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("给单据:" + StringUtil.dealParam(request.getParameter("opcode"))
						+ "添加商品:" + productCode + "数量：" + planCountGD);
				bsbyOperationRecordBean.setOperation_id(opid);
				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return;
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			} finally {
				service.releaseAll();
			}

		}
	}

	/**
	 * 查看打印记录 2010-02-23
	 * 
	 * @param request
	 * @param response
	 */
	public void getOperationPrintRecord(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		/*
		 * boolean viewAll = group.isFlag(230); if (!viewAll) {
		 * request.setAttribute("tip", "你无权操作"); request.setAttribute("result",
		 * "failure"); return; }
		 */
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			String opid = StringUtil.dealParam(request.getParameter("opid"));
			List list = service.getBsbyOperationRecordList("operation_id=" + opid + " and log_type=1", -1, -1,
			"time desc");
			request.setAttribute("list", list);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

	}

	/**
	 * 操作人员日志记录 2010-02-22
	 * 
	 * @param request
	 * @param response
	 */
	public void getOperationRecord(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			String opid = StringUtil.dealParam(request.getParameter("opid"));
			List list = service.getBsbyOperationRecordList("operation_id=" + opid + " and log_type=0", -1, -1,
			"time asc");
			request.setAttribute("list", list);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

	}

	/**
	 * 导出列表 2010-02-22
	 * 
	 * @param request
	 * @param response
	 */
	public void printBsBy(HttpServletRequest request, HttpServletResponse response) {
		int opid = StringUtil.StringToId(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(
				IBaseService.CONN_IN_SERVICE, null);
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bsbyOperationnoteBean = service.getBsbyOperationnoteBean("id=" + opid);
			request.setAttribute("bsbyOperationnoteBean", bsbyOperationnoteBean);
			if (bsbyOperationnoteBean.getCurrent_type() != 4) {
				request.setAttribute("tip", "表单还未确认，不能够导出");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			if (bsbyOperationnoteBean.getType() == 0) {
				request.setAttribute("title", "报损");
			} else {
				request.setAttribute("title", "报溢");
			}
			// 查询这个单据的所有产品
			List list = service.getBsbyProductList("operation_id=" + opid, -1, -1, null);
			request.setAttribute("bsbyProductList", list);
			// 添加日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(user.getId());
			bsbyOperationRecordBean.setOperator_name(user.getUsername());
			bsbyOperationRecordBean.setTime(DateUtil.getNow());
			bsbyOperationRecordBean.setInformation("打印单据:" + StringUtil.dealParam(request.getParameter("opcode")));
			bsbyOperationRecordBean.setOperation_id(opid);
			bsbyOperationRecordBean.setLog_type(1);
			service.addBsbyOperationRecord(bsbyOperationRecordBean);
			int printSum = bsbyOperationnoteBean.getPrint_sum() + 1;
			if(!service.updateBsbyOperationnoteBean("print_sum=" + printSum, "id=" + opid))
			{
			  service.getDbOp().rollbackTransaction();
			  request.setAttribute("tip", "数据库操作失败");
			  request.setAttribute("result", "failure");
			  return;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}

	public void updateBsbyProductCount(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		String opid = StringUtil.dealParam(request.getParameter("opid"));
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail && bean.getCurrent_type()!=BsbyOperationnoteBean.fin_audit_Fail){
				request.setAttribute("tip", "单据已提交审核，无法修改！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			List list = service.getBsbyProductList("operation_id=" + opid, -1, -1, null);
			Iterator it = list.iterator();
			for (; it.hasNext();) {
				BsbyProductBean bpb = (BsbyProductBean) it.next();
				BsbyProductCargoBean pcb = service.getBsbyProductCargo("bsby_product_id = "+bpb.getId());
				int id = bpb.getId();
				String count = StringUtil.dealParam(request.getParameter("count" + id));
				BsbyOperationnoteBean bsbyOperationnoteBean = service.getBsbyOperationnoteBean("id=" + opid);
				int x = getProductCount(bpb.getProduct_id(), bsbyOperationnoteBean.getWarehouse_area(),
						bsbyOperationnoteBean.getWarehouse_type());
				int result = updateProductCount(x, bsbyOperationnoteBean.getType(), StringUtil.toInt(count));
				if (result < 0 ) {
					request.setAttribute("tip", "您所添加商品的库存不足！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				boolean flag = service.updateBsbyProductBean("bsby_count=" + count + " , before_change=" + x
						+ " , after_change=" + result, "id=" + id);
				if(!service.updateBsbyProductCargo("count=" + count , "id=" + pcb.getId()))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return;
				}
				if (flag) {
					request.setAttribute("opid", opid);
					// 添加日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:"
							+ StringUtil.dealParam(request.getParameter("opcode")) + "的商品" + bpb.getProduct_code()
							+ "的数量为：" + count);
					bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(opid));
					bsbyOperationRecordBean.setLog_type(0);
					service.addBsbyOperationRecord(bsbyOperationRecordBean);

				} else {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "修改失败！");
					request.setAttribute("result", "failure");
					return;
				}

			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}
	}

	/**
	 * 当审核完毕后 就要根据报损和报溢 变化库存
	 * 
	 * @param bean
	 */
	public static boolean updateStock(BsbyOperationnoteBean bean, HttpServletRequest request, HttpServletResponse response, DbOperation dbOp) {


			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return false;
			}
			WareService wareService = new WareService(dbOp);
			IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, bsbyservice.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,
					service.getDbOp());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
			try {

				if (bean.getType() == 3) {
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return false;
				}


				int bybs_type = bean.getType();// 单据类型

				// 得到这个单据中的所有的要修改库存的商品
				List list = bsbyservice.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
				if (list.size() != 0) {
					Iterator it = list.iterator();
					for (; it.hasNext();) {
						BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
						BsbyProductCargoBean bsbyCargo = bsbyservice.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
						if (bsbyCargo == null) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "货位信息异常，操作失败，请联系管理员！");
							request.setAttribute("result", "failure");
							return false;
						}
						int productId = bsbyProductBean.getProduct_id();
						// 每一个单据中的产品 依次进行修改库存操作
						String titleString = "";

						// 开始事务
//						service.getDbOp().startTransaction();

						voProduct product = wareService.getProduct(productId);
						// 得到这个产品的所有库存的列表
						product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));

						// 出库 报损就是出库
						if (bybs_type == 0) {
							titleString = "报损";

							BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

							bsbyOperationRecordBean.setOperator_id(user.getId());
							bsbyOperationRecordBean.setOperator_name(user.getUsername());
							bsbyOperationRecordBean.setTime(DateUtil.getNow());
							bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存"
									+ product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
							bsbyOperationRecordBean.setOperation_id(bean.getId());
							bsbyOperationRecordBean.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}

							// 更新指定库的库存

							ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area="
									+ bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
							product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
									null));

							if (ps == null) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "没有找到产品库存，操作失败！");
								request.setAttribute("result", "failure");
								return false;
							}
							/*if (!psService.updateProductStockCount(ps.getId(), -bsbyProductBean.getBsby_count())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								return false;
							}*/
							//审核完成，清除库存锁定量
							if (!psService.updateProductLockCount(ps.getId(), -bsbyProductBean.getBsby_count())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							//审核完成，减货位库存锁定量
							if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							
							


							BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();

							bsbyOperationRecordBean1.setOperator_id(user.getId());
							bsbyOperationRecordBean1.setOperator_name(user.getUsername());
							bsbyOperationRecordBean1.setTime(DateUtil.getNow());
							bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品"
									+ bsbyProductBean.getProduct_code() + "出库" + bsbyProductBean.getBsby_count());
							bsbyOperationRecordBean1.setOperation_id(bean.getId());
							bsbyOperationRecordBean1.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}

							
							//财务基础数据列表
							BaseProductInfo base = new BaseProductInfo();
							base.setId(bsbyProductBean.getProduct_id());
							base.setProductStockId(ps.getId());
							base.setOutCount(bsbyProductBean.getBsby_count());
							base.setOutPrice(bsbyProductBean.getBsby_price());
							base.setPrice(bsbyProductBean.getPrice());
							base.setNotaxPrice(bsbyProductBean.getNotaxPrice());
							baseList.add(base);
							
							// 更新库存价格
//							int totalCount = product.getStock(ProductStockBean.AREA_BJ)
//							+ product.getStock(ProductStockBean.AREA_GF)
//							+ product.getStock(ProductStockBean.AREA_GS)
//							+ product.getLockCount(ProductStockBean.AREA_BJ)
//							+ product.getLockCount(ProductStockBean.AREA_GF)
//							+ product.getLockCount(ProductStockBean.AREA_GS);
//							float price5 = ((float) Math.round((product.getPrice5() * totalCount - stockOutPrice)
//									/ (totalCount - bsbyProductBean.getBsby_count()) * 1000)) / 1000;
//							if (totalCount - bsbyProductBean.getBsby_count() == 0) {
//								price5 = 0;
//							}
//							service.getDbOp().executeUpdate(
//									"update product set price5=" + price5 + " where id = " + product.getId());

							//**
							if(!bsbyservice.updateBsbyProductBean("bsby_price = "+product.getPrice5(), "id = "+bsbyProductBean.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}
							// 审核通过，就加 进销存卡片
							product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
									null));
							CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = "+bsbyCargo.getCargoProductStockId());

							// 出库卡片
							StockCardBean sc = new StockCardBean();
//							int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
//							sc.setId(scId);

							sc.setCardType(StockCardBean.CARDTYPE_LOSE);// 出库就是报损
							sc.setCode(bean.getReceipts_number());

							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getWarehouse_type());
							sc.setStockArea(bean.getWarehouse_area());
							sc.setProductId(productId);
							sc.setStockId(ps.getId());
							sc.setStockOutCount(bsbyProductBean.getBsby_count());
//							sc.setStockOutPriceSum(stockOutPrice);
							sc.setStockOutPriceSum((new BigDecimal(bsbyProductBean.getBsby_count())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType())
									+ product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
							sc.setStockAllArea(product.getStock(bean.getWarehouse_area())
									+ product.getLockCount(bean.getWarehouse_area()));
							sc.setStockAllType(product.getStockAllType(sc.getStockType())
									+ product.getLockCountAllType(sc.getStockType()));
							sc.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
									new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!psService.addStockCard(sc)){
								request.setAttribute("tip", "进销存记录添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							
							//货位出库卡片
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_LOSE);
							csc.setCode(bean.getReceipts_number());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(bean.getWarehouse_type());
							csc.setStockArea(bean.getWarehouse_area());
							csc.setProductId(productId);
							csc.setStockId(cps.getId());
							csc.setStockOutCount(bsbyProductBean.getBsby_count());
							csc.setStockOutPriceSum((new BigDecimal(bsbyProductBean.getBsby_count())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
							csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
							csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							if(!cargoService.addCargoStockCard(csc)){
								request.setAttribute("tip", "货位进销存记录添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}

						}
						// 入库
						else {
							titleString = "报溢";

							BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

							bsbyOperationRecordBean.setOperator_id(user.getId());
							bsbyOperationRecordBean.setOperator_name(user.getUsername());
							bsbyOperationRecordBean.setTime(DateUtil.getNow());
							bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存"
									+ product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
							bsbyOperationRecordBean.setOperation_id(bean.getId());
							bsbyOperationRecordBean.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}
							product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
//							int totalCount = product.getStock(ProductStockBean.AREA_BJ)
//							+ product.getStock(ProductStockBean.AREA_GF)
//							+ product.getStock(ProductStockBean.AREA_GS)
//							+ product.getLockCount(ProductStockBean.AREA_BJ)
//							+ product.getLockCount(ProductStockBean.AREA_GF)
//							+ product.getLockCount(ProductStockBean.AREA_GS);

							ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area="
									+ bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
							if (ps == null) {
								request.setAttribute("tip", "没有找到产品库存，操作失败！");
								request.setAttribute("result", "failure");
								return false;
							}
							if (!psService.updateProductStockCount(ps.getId(), bsbyProductBean.getBsby_count())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								return false;
							}
							
							//对于针对退货库的报溢， 要加未质检量进去
							/*if( bean.getWarehouse_area() == ProductStockBean.AREA_ZC && bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_RETURN ) {
								if( !psService.addUnappraisalNumberOrReturnedProduct(product.getId(), product.getCode(), product.getName(), bsbyProductBean.getBsby_count())) {
									request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，在加退货库未质检量时出了问题");
				                    request.setAttribute("result", "failure");
				                    service.getDbOp().rollbackTransaction();
				                    return false;
								}
							}*/

							//审核完成，增加货位库存量
							if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count-"+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结空间不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}

//							// log记录
//							if (user == null) {
//								request.setAttribute("tip", "当前没有登录，添加失败！");
//								request.setAttribute("result", "failure");
//								return false;
//							}

							BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();
							bsbyOperationRecordBean1.setOperator_id(user.getId());
							bsbyOperationRecordBean1.setOperator_name(user.getUsername());
							bsbyOperationRecordBean1.setTime(DateUtil.getNow());
							bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品"
									+ bsbyProductBean.getProduct_code() + "入库"+bsbyProductBean.getBsby_count());
							bsbyOperationRecordBean1.setOperation_id(bean.getId());
							bsbyOperationRecordBean1.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}
							
							
							//财务基础数据
							BaseProductInfo base = new BaseProductInfo();
							base.setId(productId);
							base.setInCount(bsbyProductBean.getBsby_count());
							base.setInPrice(product.getPrice5());
							base.setProductStockId(ps.getId());
							base.setPrice(bsbyProductBean.getPrice());
							base.setNotaxPrice(bsbyProductBean.getNotaxPrice());
							baseList.add(base);
							
							// 审核通过，就加 进销存卡片

//							float price5 = ((float) Math.round((product.getPrice5() * totalCount)
//									/ (totalCount + bsbyProductBean.getBsby_count()) * 1000)) / 1000;
//							service.getDbOp().executeUpdate(
//									"update product set price5=" + price5 + " where id = " + product.getId());

							product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
							CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = "+bsbyCargo.getCargoProductStockId());

							// 入库卡片
							StockCardBean sc = new StockCardBean();
							sc.setCardType(StockCardBean.CARDTYPE_GET);
							sc.setCode(bean.getReceipts_number());

							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getWarehouse_type());
							sc.setStockArea(bean.getWarehouse_area());
							sc.setProductId(productId);
							sc.setStockId(ps.getId());
							sc.setStockInCount(bsbyProductBean.getBsby_count());
							sc.setStockInPriceSum(product.getPrice5()*bsbyProductBean.getBsby_count());

							sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType())
									+ product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
							sc.setStockAllArea(product.getStock(bean.getWarehouse_area())
									+ product.getLockCount(bean.getWarehouse_area()));
							sc.setStockAllType(product.getStockAllType(sc.getStockType())
									+ product.getLockCountAllType(sc.getStockType()));
							sc.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());// 新的库存价格
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
									new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!psService.addStockCard(sc)){
								request.setAttribute("tip", "进销存记录添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							
							//货位入库卡片
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_GET);
							csc.setCode(bean.getReceipts_number());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(bean.getWarehouse_type());
							csc.setStockArea(bean.getWarehouse_area());
							csc.setProductId(productId);
							csc.setStockId(cps.getId());
							csc.setStockInCount(bsbyProductBean.getBsby_count());
							csc.setStockInPriceSum((new BigDecimal(bsbyProductBean.getBsby_count())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
							csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
							csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!cargoService.addCargoStockCard(csc)){
								request.setAttribute("tip", "货位进销存添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
					
							if(bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED){
								//更新订单缺货状态
								updateLackOrder(bsbyProductBean.getProduct_id());
							}
						}
					}
					
					//报损报溢出调用财务接口	
					if(baseList.size() > 0){
						if(bybs_type == 0){
							//报损
							FinanceBaseDataService bsBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_LOSE, service.getDbOp().getConn());
							bsBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
						}else{
							//报溢
							FinanceBaseDataService byBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_GET, service.getDbOp().getConn());
							byBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
						}
					}
					
					
					
					// 操作完成记录 bsby
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "完成更改库存操作");
					bsbyOperationRecordBean.setOperation_id(bean.getId());
					bsbyOperationRecordBean.setLog_type(0);
					if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return false;
					}
					// 提交事务
//					service.getDbOp().commitTransaction();
					
				}
			} catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				return false;
			} finally {
//				service.releaseAll();
//				bsbyservice.releaseAll();
			}

			return true;
		

	}

	/**
	 * 根据产品id查看进销存卡片 2010-02-24
	 * 
	 * @param request
	 * @param response
	 */
	public void findStockCard(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");

		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		String pid = StringUtil.dealParam(request.getParameter("pid"));
		if (pid != null) {
			request.setAttribute("pid", pid);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			int countPerPage = 50;
			// 总数
			int totalCount = service.getStockCardCount("product_id=" + pid);
			// 页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = service.getStockCardList("product_id=" + pid, paging.getCurrentPageIndex() * countPerPage,
					countPerPage, "create_datetime desc");
			Collections.sort(list, new StockCardComparator());
			paging.setPrefixUrl("stockCardList.jsp?pid=" + pid);

			request.setAttribute("paging", paging);
			request.setAttribute("list", list);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

	}
	public static BsbyProductBean getProductByOperationId(int opid)
	{	
		IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService();
		try {
			BsbyProductBean bsbyProductBean = bsbyservice.getBsbyProductBean("operation_id="+opid);
			if (bsbyProductBean!=null) {
				return bsbyProductBean;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			bsbyservice.releaseAll();
		}
		return null;
	}
	public static HashMap<String, String> getProductListByOperationId(int opid, int currentType)
	{	
		IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService();
		WareService wareService = new WareService(bsbyservice.getDbOp());
		HashMap<String, String> bsbyPMap = new HashMap<String, String>();
		try {
			StringBuffer pCode = new StringBuffer();//商品编号
			StringBuffer pCount = new StringBuffer();//商品数量
			StringBuffer ptax= new StringBuffer();//单价含税
			StringBuffer pNotax= new StringBuffer();//单价不含税
			StringBuffer pSumTax = new StringBuffer();//总额含税
			StringBuffer pSumNoTax = new StringBuffer();//总额不含税
			StringBuffer oriName = new StringBuffer();//商品原名称
			StringBuffer cargoCode = new StringBuffer();//货位编号
			StringBuffer parentName1 = new StringBuffer();//一级分类
			StringBuffer parentName2 = new StringBuffer();//二级分类
			StringBuffer parentName3 = new StringBuffer();//三级分类
			StringBuffer productLine = new StringBuffer();//产品线
			List<BsbyProductBean> bsbyProductList = bsbyservice.getBsbyProductList("operation_id="+opid, -1, -1, "id");
			if(bsbyProductList!=null && bsbyProductList.size()>0){
				for (int i = 0;i<bsbyProductList.size();i++) {
					BsbyProductBean bean = (BsbyProductBean)bsbyProductList.get(i);
					voProduct product = wareService.getProduct(bean.getProduct_id());
					if(product!=null){
						voCatalog catalog1 = (voCatalog) CatalogCache.catalogs.get(product.getParentId1());
						voCatalog catalog2 = (voCatalog) CatalogCache.catalogs.get(product.getParentId2());
						voCatalog catalog3 = (voCatalog) CatalogCache.catalogs.get(product.getParentId3());
						if(catalog1!=null){
							parentName1.append(catalog1.getName()+"<br>");
						}else{
							parentName1.append("<br>");
						}
						if(catalog2!=null){
							parentName2.append(catalog2.getName()+"<br>");
						}else{
							parentName2.append("<br>");
						}
						if(catalog3!=null){
							parentName3.append(catalog3.getName()+"<br>");
						}else{
							parentName3.append("<br>");
						}
						voProductLine productLineBean = wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
						if(productLineBean!=null){
							productLine.append(productLineBean.getName()+"<br>");
						}
					}
					pCode.append(bean.getProduct_code()+"<br>");
					pCount.append(bean.getBsby_count()+"<br>");
					if (currentType == 0) {
						float price5 = product.getPrice5();//含税金额
						//不含税金额
						float notaxProductPrice = bsbyservice.returnFinanceProductPrice(bean.getProduct_id());
						ptax.append(price5+"<br>");
						pNotax.append(notaxProductPrice+"<br>");
						pSumTax.append(price5*bean.getBsby_count()+"<br>");
						pSumNoTax.append(notaxProductPrice*bean.getBsby_count()+"<br>");
					} else {
						ptax.append(bean.getPrice()+"<br>");
						pNotax.append(bean.getNotaxPrice()+"<br>");
						pSumTax.append(bean.getPrice()*bean.getBsby_count()+"<br>");
						pSumNoTax.append(bean.getNotaxPrice()*bean.getBsby_count()+"<br>");
					}
					oriName.append(bean.getOriname()+"<br>");
					cargoCode.append(bsbyservice.returnCargoCode(opid)+"<br>");
				}
			}
			bsbyPMap.put("pCode", pCode.toString());
			bsbyPMap.put("pCount", pCount.toString());
			bsbyPMap.put("ptax", ptax.toString());
			bsbyPMap.put("pNotax", pNotax.toString());
			bsbyPMap.put("pSumTax", pSumTax.toString());
			bsbyPMap.put("pSumNoTax", pSumNoTax.toString());
			bsbyPMap.put("oriName", oriName.toString());
			bsbyPMap.put("cargoCode", cargoCode.toString());
			bsbyPMap.put("parentName1", parentName1.toString());
			bsbyPMap.put("parentName2", parentName2.toString());
			bsbyPMap.put("parentName3", parentName3.toString());
			bsbyPMap.put("productLine", productLine.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			bsbyservice.releaseAll();
		}
		return bsbyPMap;
	}
	/**
	 * 将单据改为已完成
	 * 2010-03-31
	 * 李青
	 * @param request
	 * @param response
	 */
	public static void updateToEnd(HttpServletRequest request, HttpServletResponse response)
	{
		String id = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("id")));
		request.setAttribute("opid", id);
		WareService wareService = new WareService();
		IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, bsbyservice.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,
				service.getDbOp());
		try {

			service.getDbOp().startTransaction();
			
			BsbyOperationnoteBean ben = bsbyservice.getBsbyOperationnoteBean("id=" + id);
			BsbyProductBean bpb = bsbyservice.getBsbyProductBean("operation_id="+id);

			List list = bsbyservice.getBsbyProductList("operation_id=" + id, -1, -1, null);
			if (list.size() != 0 && ben.getType() == 0) {
				Iterator it = list.iterator();
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					int productId = bsbyProductBean.getProduct_id();
					// 每一个单据中的产品 依次进行修改库存操作
					String titleString = "";

					voProduct product = wareService.getProduct(productId);
					// 得到这个产品的所有库存的列表
					product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));

					// 出库 报损就是出库


					// 如果出库的量大于 商品所在库的库存 就提示
					if (bsbyProductBean.getBsby_count() > product.getStock(ben.getWarehouse_area(), ben
							.getWarehouse_type())) {
						request.setAttribute("tip", "可用库存不足,操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
				}
			}

			BsbyOperationnoteBean bsbyOperationnoteBean = bsbyservice.getBsbyOperationnoteBean("id="+id);
			if (bsbyOperationnoteBean!=null) {
				if(bsbyOperationnoteBean.getCurrent_type()==4)
				{
					request.setAttribute("tip", "此单据已经完成！");
					request.setAttribute("result", "failure");
					return;
				}
				else {
					boolean flag = bsbyservice.updateBsbyOperationnoteBean("current_type=4", "id="+id);
					if (!flag) {
						request.setAttribute("tip", "操作失败！");
						request.setAttribute("result", "failure");
						return;
					}else {
						// 如果是改为已完成 就要添加最后修改人的信息
						voUser user = (voUser) request.getSession().getAttribute("userView");
						BsbyOperationnoteBean bean = bsbyservice.getBsbyOperationnoteBean("id=" + id);
						if(!bsbyservice.updateBsbyOperationnoteBean("current_type=4 ", "id=" + id))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						/**
						 * 单据状态改为完成 就要改变库存 如果是报损就要剪掉库存 如果是报溢就要添加批次 如果没有调整的产品
						 * 就不执行这个方法
						 */
						BsbyProductBean bsbyProductBean =  bsbyservice.getBsbyProductBean("operation_id="+id);
						int beforeChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());

						if(!updateStock(bean, request, response, service.getDbOp()))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						/**
						 * 更改为完成后,要将最后的库存和改变后的库存的量记录
						 */
						int afterChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());


						if(!bsbyservice.updateBsbyProductBean("before_change="+beforeChangeProductCount+", after_change="+afterChangeProductCount, "id="+bsbyProductBean.getId()))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						// 添加日志
						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(DateUtil.getNow());
						bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:已完成");
						bsbyOperationRecordBean.setOperation_id(bean.getId());
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
					}
				}

			}

			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			service.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

	}
	

    public static void updateLackOrder(int productId){
    	DbOperation dbOp = new DbOperation();
    	dbOp.init("adult_slave");
    	DbOperation dbOp2 = new DbOperation();
    	dbOp2.init();
    	WareService wareService = new WareService(dbOp);
    	IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
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
//					dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
//							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 3600 and uo.is_olduser=1 and uo.id = "+order.getId());
//					dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
//							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 7200 and uo.id = "+order.getId());
//					dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 7,uold.stockout_deal = 7,uold.next_deal_datetime = null " +
//							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') > 7200 and uo.id = "+order.getId());
					StockOperationAction.updateOrderLackStatu(dbOp2,order.getId());
				}
			
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
			dbOp.release();
			dbOp2.release();
		}
    	
    }
    
    public static boolean checkStock(List orderProductList,int area) {
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
     * 批量审核报损报溢单
     */
    public ActionForward allComplete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(-1)){
			request.setAttribute("tip", "没有权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		String bsbyOrder=request.getParameter("bsbyOrder");
		DbOperation dbOp = new DbOperation();
    	dbOp.init("adult");
    	IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		synchronized (stockLock) {
		try{
    		if(bsbyOrder!=null){
    			dbOp.startTransaction();
    			bsbyOrder=bsbyOrder.trim();
    			String tip="";
    			String[] a=bsbyOrder.split("\r\n");
    			int total=0;
        		for(int i=0;i<a.length;i++){
        			a[i]=a[i].trim();
        			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("receipts_number='" + a[i]+"'");
        			if(bean==null){
        				tip+=a[i];
        				tip+="，未找到<br/>";
        				continue;
        			}
        			if(bean.getCurrent_type()==BsbyOperationnoteBean.dispose||bean.getCurrent_type()==BsbyOperationnoteBean.fin_audit_Fail||bean.getCurrent_type()==BsbyOperationnoteBean.audit_Fail){
        				tip+=a[i];
        				tip+="，未提交审核<br/>";
        				continue;
        			}
        			if(bean.getCurrent_type()==4){
        				tip+=a[i];
        				tip+="，已经审核完成<br/>";
        				continue;
					}
        			if(!updateStock(bean, request, response, service.getDbOp()))
        			{
        			  service.getDbOp().rollbackTransaction();
        			  request.setAttribute("tip", a[i]+"数据库操作失败");
        			  request.setAttribute("result", "failure");
        			  return mapping.findForward("failure");
        			}
					// 如果是改为已完成 就要添加审核人的信息
					if(!service.updateBsbyOperationnoteBean("current_type=4 , end_time='"
							+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
							+ user.getUsername() + "',fin_audit_datetime='"+DateUtil.getNow()
							+"',fin_audit_name='"+user.getUsername()+"',fin_audit_remark='财务邮件确认',fin_audit_id="+user.getId()
							+",examineSuggestion='总经理邮件确认'", "id=" + bean.getId()))
        			{
        			  service.getDbOp().rollbackTransaction();
        			  request.setAttribute("tip", a[i]+"数据库操作失败");
        			  request.setAttribute("result", "failure");
        			  return mapping.findForward("failure");
        			}
					/**
					 * 单据状态改为完成 就要改变库存 如果是报损就要剪掉库存 如果是报溢就要添加批次 如果没有调整的产品
					 * 就不执行这个方法
					 */
					BsbyProductBean bsbyProductBean =  service.getBsbyProductBean("operation_id="+bean.getId());
					int beforeChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());


					/**
					 * 更改为完成后,要将最后的库存和改变后的库存的量记录
					 */
					int afterChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());
					if(!service.updateBsbyProductBean("before_change="+beforeChangeProductCount+", after_change="+afterChangeProductCount, "id="+bsbyProductBean.getId()))
        			{
        			  service.getDbOp().rollbackTransaction();
        			  request.setAttribute("tip", a[i]+"数据库操作失败");
        			  request.setAttribute("result", "failure");
        			  return mapping.findForward("failure");
        			}
					// 添加日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:已完成");
					bsbyOperationRecordBean.setOperation_id(bean.getId());
					if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", a[i]+"数据库操作失败");
					  request.setAttribute("result", "failure");
					  return mapping.findForward("failure");
					}
					total++;
        		}
        	dbOp.commitTransaction();
        	request.setAttribute("tip", tip);
        	request.setAttribute("total", total+"");
    		}
    	}catch (Exception e) {
    		service.getDbOp().rollbackTransaction();
    		e.printStackTrace();
		}finally{
			dbOp.release();
		}
		}
		return mapping.findForward("allComplete");
    }
    
    /**
     * 批量添加报损报溢单
     */
    public ActionForward allAdd(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(584)){
			request.setAttribute("tip", "没有权限");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		String bsbyOrder=request.getParameter("bsbyOrder");
		DbOperation dbOp = new DbOperation();
    	dbOp.init("adult");
    	IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
    	IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
    	ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		synchronized (stockLock) {
		try{
			if(bsbyOrder!=null){
    		
    		String tip="";//反馈
    		int total=0;
    		List orderCodeList=new ArrayList();
    		String[] orderStr=bsbyOrder.split("\r\n");//每条数据
    		for(int i=0;i<orderStr.length;i++){
    			dbOp.startTransaction();
    			String str=orderStr[i];
    			String[] a=str.split("\t");
    			if(a.length==0||a.length==1){
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			String productCode=a[0].trim();//商品编号
    			String cargoCode=a[1].trim();//货位号
    			String countCode=a[2].trim();//数量
    			int count=0;//数量
    			if(countCode.matches("(-)?[0-9]+")){
    				count=Integer.parseInt(countCode);
    			}else{
    				tip+=(a[0]+","+a[1]+","+a[2]+",数字错误");
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			
    			//生成报损报溢单
    			int operationnoteType = 0;//作业单类型
    			if(count>0){//报溢
    				operationnoteType=1;
    			}else if(count<0){//报损
    				count=-count;
    				operationnoteType=0;
    			}else{
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			CargoInfoBean cargoInfo=cargoService.getCargoInfo("whole_code='"+cargoCode+"'");
    			if(cargoInfo==null){
    				tip+=(a[0]+","+a[1]+","+a[2]+",货位号错误");
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			CargoInfoAreaBean areaBean=cargoService.getCargoInfoArea("id="+cargoInfo.getAreaId());
    			if(areaBean==null){
    				tip+=(a[0]+","+a[1]+","+a[2]+",货位号错误");
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			int warehouse_type = cargoInfo.getStockType();//库类型
    			int warehouse_area = areaBean.getOldId();//地区
    			String receipts_number = "";
    			String title = "";// 日志的内容
    			int typeString = 0;
    			if (operationnoteType == 0) {
    				// 报损
    				String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
    				receipts_number = createCode(code);// BS+年月日+3位自动增长数
    				title = "创建新的报损表" + receipts_number;
    				typeString = 0;
    			} else {
    				String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
    				receipts_number = createCode(code);// BY+年月日+3位自动增长数
    				title = "创建新的报溢表" + receipts_number;
    				typeString = 1;
    			}
    			String nowTime = DateUtil.getNow();
    			BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
    			bsbyOperationnoteBean.setAdd_time(nowTime);
    			bsbyOperationnoteBean.setCurrent_type(0);
    			bsbyOperationnoteBean.setOperator_id(user.getId());
    			bsbyOperationnoteBean.setOperator_name(user.getUsername());
    			bsbyOperationnoteBean.setReceipts_number(receipts_number);
    			bsbyOperationnoteBean.setWarehouse_area(warehouse_area);
    			bsbyOperationnoteBean.setWarehouse_type(warehouse_type);
    			bsbyOperationnoteBean.setType(typeString);
    			bsbyOperationnoteBean.setIf_del(0);
    			bsbyOperationnoteBean.setFinAuditId(0);
    			bsbyOperationnoteBean.setFinAuditName("");
    			bsbyOperationnoteBean.setFinAuditRemark("");
    			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
    			bsbyOperationnoteBean.setId(maxid + 1);
    			boolean falg = service.addBsbyOperationnoteBean(bsbyOperationnoteBean);
    			if (falg) {
    				request.setAttribute("opid", Integer.valueOf(bsbyOperationnoteBean.getId()));// 添加成功将id传到下个页面
    				// 添加操作日志
    				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
    				bsbyOperationRecordBean.setOperator_id(user.getId());
    				bsbyOperationRecordBean.setOperator_name(user.getUsername());
    				bsbyOperationRecordBean.setTime(nowTime);
    				bsbyOperationRecordBean.setInformation(title);
    				bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
    				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
    				{
    				  service.getDbOp().rollbackTransaction();
    				  request.setAttribute("tip", "数据库操作失败");
    				  request.setAttribute("result", "failure");
    				  return mapping.findForward("failure");
    				}
    			} else {
    				dbOp.rollbackTransaction();
   					continue;
   				}
    			
    			//添加商品
    			DbOperation tempDbOp = new DbOperation();
    			tempDbOp.init(DbOperation.DB_SLAVE);
    			WareService wareService = new WareService(tempDbOp);
    			voProduct product = wareService.getProduct(productCode);
    			wareService.releaseAll();

    			if (product == null) {
    				tip+=(a[0]+","+a[1]+","+a[2]+",");
    				tip+="商品不存在";
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			if (product.getParentId1() == 106) {
    				tip+=(a[0]+","+a[1]+","+a[2]+",");
    				tip+="该商品为新商品";
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			if (product.getIsPackage() == 1) {
    				tip+=(a[0]+","+a[1]+","+a[2]+",");
    				tip+="该商品为套装";
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			int x = getProductCount(product.getId(), bsbyOperationnoteBean.getWarehouse_area(), bsbyOperationnoteBean.getWarehouse_type());
    			int result = updateProductCount(x, bsbyOperationnoteBean.getType(), count);
    			if (result < 0 ) {
    				tip+=(a[0]+","+a[1]+","+a[2]+",");
    				tip+="库存不足";
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			//新货位管理判断
    			CargoProductStockBean cps = null;
    			if(bsbyOperationnoteBean.getType()==0){
    				CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
    			    List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bsbyOperationnoteBean.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
    			    if(cpsOutList == null || cpsOutList.size()==0){
    			    	tip+=(a[0]+","+a[1]+","+a[2]+",");
    			        tip+="货位商品不符";
    			        tip+="<br/>";
    			        dbOp.rollbackTransaction();
    			        continue;
    			    }
    			    cps = (CargoProductStockBean)cpsOutList.get(0);
    			    if(bsbyOperationnoteBean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cps.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
    			    	tip+=(a[0]+","+a[1]+","+a[2]+",");
    			    	tip+="合格库缓存区暂时不能进行报损报溢操作！";
    			    	tip+="<br/>";
    			    	dbOp.rollbackTransaction();
    			        continue;
    			    }	
    			    if(count > cps.getStockCount()){
    			    	tip+=(a[0]+","+a[1]+","+a[2]+",");
    			    	tip+="库存不足";
    			    	tip+="<br/>";
    			    	dbOp.rollbackTransaction();
    			    	continue;
    			    }
    			}else{
    				CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
    				CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+bsbyOperationnoteBean.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+cargoCode+"' and status <> "+CargoInfoBean.STATUS3);
    			    if(cargo == null){
    			    	tip+=(a[0]+","+a[1]+","+a[2]+",");
    			        tip+="货位号无效";
    			        tip+="<br/>";
    			        dbOp.rollbackTransaction();
    			        continue;
    			    }
    			    if(cargo.getStatus() == CargoInfoBean.STATUS2){
    			    	tip+=(a[0]+","+a[1]+","+a[2]+",");
    			        tip+="货位未开通";
    			        tip+="<br/>";
    			        dbOp.rollbackTransaction();
    			        continue;
    			    }
    			    if(bsbyOperationnoteBean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cargo.getStoreType() == CargoInfoBean.STORE_TYPE2){
    			    	tip+=(a[0]+","+a[1]+","+a[2]+",");
    			        tip+="合格库缓存区暂时不能进行报损报溢操作";
    			        tip+="<br/>";
    			        dbOp.rollbackTransaction();
    			        continue;
    			    }
    			    List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
    			    if(cpsOutList == null || cpsOutList.size()==0){
    			        if(cargo.getStatus() == CargoInfoBean.STATUS0 && (cargo.getStoreType() == CargoInfoBean.STORE_TYPE0||cargo.getStoreType() == CargoInfoBean.STORE_TYPE4)){
    			        	tip+=(a[0]+","+a[1]+","+a[2]+",");
    			        	tip+="货位商品不符";
    			        	tip+="<br/>";
    			        	dbOp.rollbackTransaction();
    				        continue;
    			        }
    			        cps = new CargoProductStockBean();
    			        cps.setCargoId(cargo.getId());
    			        cps.setProductId(product.getId());
    			        cps.setStockCount(0);
    			        cps.setStockLockCount(0);
    			        cargoService.addCargoProductStock(cps);
    			        cps.setId(cargoService.getDbOp().getLastInsertId());
    			        if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId()))
        				{
        				  service.getDbOp().rollbackTransaction();
        				  request.setAttribute("tip", "数据库操作失败");
        				  request.setAttribute("result", "failure");
        				  return mapping.findForward("failure");
        				}
    			    }else{
    			        cps = (CargoProductStockBean)cpsOutList.get(0);
    			    }
    			}
    		    BsbyProductBean bsbyProductBean = new BsbyProductBean();
    			bsbyProductBean.setBsby_count(count);
    			bsbyProductBean.setOperation_id(bsbyOperationnoteBean.getId());
    			bsbyProductBean.setProduct_code(productCode);
    			bsbyProductBean.setProduct_id(product.getId());
    			bsbyProductBean.setProduct_name(product.getName());
    			bsbyProductBean.setOriname(product.getOriname());
    			bsbyProductBean.setAfter_change(result);
    			bsbyProductBean.setBefore_change(x);
    			boolean flag = service.addBsbyProduct(bsbyProductBean);
    			if (!flag) {
    				tip+=(a[0]+","+a[1]+","+a[2]+",");
    				tip+="添加失败";
    				tip+="<br/>";
    				dbOp.rollbackTransaction();
    				continue;
    			}
    			BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
    			bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());
    			bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
    			bsbyCargo.setCount(count);
    			bsbyCargo.setCargoProductStockId(cps.getId());
    			bsbyCargo.setCargoId(cps.getCargoId());
    			service.addBsbyProductCargo(bsbyCargo);
    			// 添加日志
    			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
    			bsbyOperationRecordBean.setOperator_id(user.getId());
    			bsbyOperationRecordBean.setOperator_name(user.getUsername());
    			bsbyOperationRecordBean.setTime(DateUtil.getNow());
    			bsbyOperationRecordBean.setInformation("给单据:" + StringUtil.dealParam(request.getParameter("opcode"))
    					+ "添加商品:" + productCode + "数量：" + count);
    			bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
    			if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return mapping.findForward("failure");
				}
    			//作业单提交审核
				List bsbyList = service.getBsbyProductList("operation_id=" + bsbyOperationnoteBean.getId(), -1, -1, null);
				Iterator it = bsbyList.iterator();
				if(bsbyOperationnoteBean.getType() == 0){
					String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
						+ "area = " + bsbyOperationnoteBean.getWarehouse_area() + " and type = "
						+ bsbyOperationnoteBean.getWarehouse_type();
					ProductStockBean psBean = psService.getProductStock(sql);
						//减少库存
					if(!psService.updateProductStockCount(psBean.getId(), -bsbyProductBean.getBsby_count())){
						tip+=(a[0]+","+a[1]+","+a[2]+",");
						tip+="库存不足";
						tip+="<br/>";
						dbOp.rollbackTransaction();
						continue;
					}
					//增加库存锁定量
					if (!psService.updateProductLockCount(psBean.getId(), bsbyProductBean.getBsby_count())) {
						tip+=(a[0]+","+a[1]+","+a[2]+",");
						tip+="库存操作失败";
						tip+="<br/>";
						dbOp.rollbackTransaction();
						continue;
					}

					//锁定货位库存
					//出库
					if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
						tip+=(a[0]+","+a[1]+","+a[2]+",");
						tip+="库存操作失败";
						tip+="<br/>";
						dbOp.rollbackTransaction();
						continue;
					}
					if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
						tip+=(a[0]+","+a[1]+","+a[2]+",");
						tip+="库存操作失败";
						tip+="<br/>";
						dbOp.rollbackTransaction();
						continue;
					}
					
				}else if(bsbyOperationnoteBean.getType() == 1){
						//锁定货位空间
					if(cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId()+" and status = 0")==null){
						tip+=(a[0]+","+a[1]+","+a[2]+",");
						tip+="库存操作失败";
						tip+="<br/>";
						dbOp.rollbackTransaction();
						continue;
					}
					if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count + "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
						tip+=(a[0]+","+a[1]+","+a[2]+",");
						tip+="库存操作失败";
						tip+="<br/>";
						dbOp.rollbackTransaction();
						continue;
					}
				}
				int type=1;
				falg = service.updateBsbyOperationnoteBean("current_type=" + type+",remark='物流部审核通过'", "id=" + bsbyOperationnoteBean.getId());
				String zhuangtai = (String) bsbyOperationnoteBean.current_typeMap.get(Integer.valueOf(type));
				if (falg) {
					// 添加日志
					BsbyOperationRecordBean bsbyOperationRecordBean2 = new BsbyOperationRecordBean();
					bsbyOperationRecordBean2.setOperator_id(user.getId());
					bsbyOperationRecordBean2.setOperator_name(user.getUsername());
					bsbyOperationRecordBean2.setTime(DateUtil.getNow());
					bsbyOperationRecordBean2.setInformation("修改单据:" + bsbyOperationnoteBean.getReceipts_number() + "的状态为:" + zhuangtai);
					bsbyOperationRecordBean2.setOperation_id(bsbyOperationnoteBean.getId());
					if(!service.addBsbyOperationRecord(bsbyOperationRecordBean2))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return mapping.findForward("failure");
					}
				}
				else{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return mapping.findForward("failure");
				}
				total++;
				
				if(!orderCodeList.add(bsbyOperationnoteBean.getReceipts_number()))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  return mapping.findForward("failure");
				}
				dbOp.commitTransaction();
    		}
    		request.setAttribute("tip", tip);
    		request.setAttribute("total", total+"");
    		request.setAttribute("orderCodeList", orderCodeList);
			}
    	}catch (Exception e) {
    		service.getDbOp().rollbackTransaction();
    		e.printStackTrace();
		}finally{
			dbOp.release();
		}
		
		}
		return mapping.findForward("allAdd");
    }
    
    /**
     * 批量财务审核报损报溢单
     */
    public ActionForward allFinanceAudit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(-1)){
			request.setAttribute("tip", "没有权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		String bsbyOrder=request.getParameter("bsbyOrder");
		DbOperation dbOp = new DbOperation();
    	dbOp.init("adult");
    	IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		synchronized (stockLock) {
		try{
    		if(bsbyOrder!=null){
    			dbOp.startTransaction();
    			bsbyOrder=bsbyOrder.trim();
    			String tip="";
    			String[] a=bsbyOrder.split("\r\n");
    			int total=0;
        		for(int i=0;i<a.length;i++){
        			a[i]=a[i].trim();
        			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("receipts_number='" + a[i]+"'");
        			if(bean==null){
        				tip+=a[i];
        				tip+="，未找到<br/>";
        				continue;
        			}
        			if(bean.getCurrent_type() != 1){
        				tip+=a[i];
        				tip+="，状态异常，无法审核<br/>";
        				continue;
        			}
        			
        			//信息改为郑会鹏
        			if(!service.updateBsbyOperationnoteBean("current_type=6,fin_audit_datetime='"+DateUtil.getNow()+"',fin_audit_id=18105230,fin_audit_name='zhenghuipeng'", "id=" + bean.getId()))
    				{
    				  service.getDbOp().rollbackTransaction();
    				  request.setAttribute("tip", "数据库操作失败");
    				  request.setAttribute("result", "failure");
    				  return mapping.findForward("failure");
    				}
					// 添加日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(18105230);
					bsbyOperationRecordBean.setOperator_name("zhenghuipeng");
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:财务审核通过");
					bsbyOperationRecordBean.setOperation_id(bean.getId());
					if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
    				{
    				  service.getDbOp().rollbackTransaction();
    				  request.setAttribute("tip", "数据库操作失败");
    				  request.setAttribute("result", "failure");
    				  return mapping.findForward("failure");
    				}
					BsbyProductBean bsbyProductBean =  service.getBsbyProductBean("operation_id="+bean.getId());
					
					tip+=a[i];
    				tip+="\t"+bsbyProductBean.getBsby_count()+"<br/>";
        		}
        	dbOp.commitTransaction();
        	request.setAttribute("tip", tip);
        	request.setAttribute("total", total+"");
    		}
    	}catch (Exception e) {
    		service.getDbOp().rollbackTransaction();
    		e.printStackTrace();
		}finally{
			dbOp.release();
		}
		}
		return mapping.findForward("allFinanceAudit");
    }
}
