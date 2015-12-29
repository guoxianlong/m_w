package mmb.stock.fitting.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.easyui.Json;
import mmb.stock.fitting.service.FittingBuyStockinService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.SupplierServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.MyRuntimeException;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


@Controller
@RequestMapping("fittingBuyStockinController")
public class FittingBuyStockinController {

	private byte[] lock = new byte[0];
	
	@RequestMapping("toAddFittingBuyStockin")
	public String toAddFittingBuyStockin(HttpServletRequest request, HttpServletResponse response , Model model) {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean addFittingBuyStockin = group.isFlag(2095);
		if (!addFittingBuyStockin) {
			request.setAttribute("tip", "您没有添加配件采购入库单的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.DB_SLAVE);
		FittingBuyStockinService fittingBuyStockinService = new FittingBuyStockinService(IBaseService.CONN_IN_SERVICE,dbop);
		try {
			String supplierJson = fittingBuyStockinService.constructAllSupplierJson();
			//String brandJson = fittingBuyStockinService.constructAllBrandJson();
			model.addAttribute("supplierJson", supplierJson);
			//model.addAttribute("brandJson", brandJson);
		} catch (MyRuntimeException mre ) {
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			fittingBuyStockinService.releaseAll();
		}
		
		return "forward:/admin/fitting/addFittingBuyStockin.jsp";
	}
	
	@RequestMapping("checkFittingBuyStockinProduct")
	@ResponseBody
	public Map<String,Object> checkFittingBuyStockinProduct( HttpServletRequest request, HttpServletResponse response ) {
		Map<String,Object> result = new HashMap<String,Object>();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.DB_SLAVE);
		FittingBuyStockinService fittingBuyStockinService = new FittingBuyStockinService(IBaseService.CONN_IN_SERVICE,dbop);
		if(user == null){
			result.put("status", "failure");
			result.put("tip", "没有登录不可以操作！");
			return result;
		}
		try {
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));
			int count = StringUtil.parstInt(request.getParameter("count"));
			float price = StringUtil.toFloat(request.getParameter("price"));
			//int supplierId = StringUtil.toInt(request.getParameter("supplierId"));
			//int brandId = StringUtil.parstInt(request.getParameter("brandId"));
			fittingBuyStockinService.checkFittingProductInfoToAdd(productCode, count, price);
			result.put("status", "success");
		} catch (MyRuntimeException mre ) {
			result.put("status", "failure");
			result.put("tip", mre.getMessage());
			return result;
		} catch(Exception e ) {
			e.printStackTrace();
			result.put("status", "failure");
			result.put("tip", "系统异常!");
			return result;
		} finally {
			fittingBuyStockinService.releaseAll();
		}
		return result;
	}
	
	@RequestMapping("getInfoForAll")
	@ResponseBody
	public Map<String,Object> getInfoForAll( HttpServletRequest request, HttpServletResponse response ) {
		Map<String,Object> result = new HashMap<String,Object>();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.DB_SLAVE);
		FittingBuyStockinService fittingBuyStockinService = new FittingBuyStockinService(IBaseService.CONN_IN_SERVICE,dbop);
		if(user == null){
			result.put("status", "failure");
			result.put("tip", "没有登录不可以操作！");
			return result;
		}
		try {
			List<BuyStockinProductBean> infoList = new ArrayList<BuyStockinProductBean>();
			String[] productCodes = request.getParameterValues("productCodes");
			if( productCodes != null )  {
				infoList = fittingBuyStockinService.getInfoForAll(request,productCodes);
				result.put("status", "success");
				result.put("rows", infoList);
				result.put("total", infoList.size());
			} else {
				result.put("status", "success");
				result.put("rows", infoList);
			}
		} catch (MyRuntimeException mre ) {
			result.put("status", "failure");
			result.put("tip", mre.getMessage());
			return result;
		} catch(Exception e ) {
			e.printStackTrace();
			result.put("status", "failure");
			result.put("tip", "系统异常!");
			return result;
		} finally {
			fittingBuyStockinService.releaseAll();
		}
		return result;
	}
	
	@RequestMapping("submitAllProduct")
	@ResponseBody
	public Map<String,Object> submitAllProduct( HttpServletRequest request, HttpServletResponse response ) {
		Map<String,Object> result = new HashMap<String,Object>();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.DB);
		FittingBuyStockinService fittingBuyStockinService = new FittingBuyStockinService(IBaseService.CONN_IN_SERVICE,dbop);
		ISupplierService supplierService = new SupplierServiceImpl(IBaseService.CONN_IN_SERVICE,dbop);
		if(user == null){
			result.put("status", "failure");
			result.put("tip", "没有登录不可以操作！");
			return result;
		}
		UserGroupBean group = user.getGroup();
		boolean addFittingBuyStockin = group.isFlag(2095);
		if (!addFittingBuyStockin) {
			result.put("status", "failure");
			result.put("tip", "您没有添加配件采购入库单的权限！");
			return result;
		}
		try {
			synchronized(lock ) {
				String[] productCodes = request.getParameterValues("productCodes");
				int area = StringUtil.parstInt(request.getParameter("area"));
				int supplierId = StringUtil.toInt(request.getParameter("supplierId"));
				int stockInType = StringUtil.StringToId(request.getParameter("stockInType"));
				SupplierStandardInfoBean ssiBean = supplierService.getSupplierStandardInfo("id="+supplierId);
				if( ssiBean == null ) {
					throw new MyRuntimeException("没有找到对应供应商的信息！");
				}
				if( productCodes != null )  {
					fittingBuyStockinService.addFittingBuyStockin(request,productCodes,area,supplierId,stockInType, user);
					result.put("status", "success");
					result.put("tip", "添加成功！");
				} else {
					result.put("status", "failure");
					result.put("tip", "没有添加任何商品！");
				}
			}
		} catch (MyRuntimeException mre ) {
			result.put("status", "failure");
			result.put("tip", mre.getMessage());
			return result;
		} catch(Exception e ) {
			e.printStackTrace();
			result.put("status", "failure");
			result.put("tip", "系统异常!");
			return result;
		} finally {
			fittingBuyStockinService.releaseAll();
		}
		return result;
	}
	
	@RequestMapping("toEditFittingBuyStockin")
	public String toEditFittingBuyStockin( HttpServletRequest request, HttpServletResponse response,Model model ) {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.DB_SLAVE);
		FittingBuyStockinService fittingBuyStockinService = new FittingBuyStockinService(IBaseService.CONN_IN_SERVICE,dbop);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbop);
		ISupplierService supplierService = new SupplierServiceImpl(IBaseService.CONN_IN_SERVICE,dbop);
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean addFittingBuyStockin = group.isFlag(2095);
		if (!addFittingBuyStockin) {
			request.setAttribute("tip", "您没有添加配件采购入库单的权限, 不可以编辑配件入库单！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		try {
			String supplierJson = fittingBuyStockinService.constructAllSupplierJson();
			//String brandJson = fittingBuyStockinService.constructAllBrandJson();
			model.addAttribute("supplierJson", supplierJson);
			//model.addAttribute("brandJson", brandJson);
			String code = StringUtil.convertNull(request.getParameter("code"));
			int totalCount = 0;
			BuyStockinBean bsBean = service.getBuyStockin("code='"+code+"'");
			if(bsBean.getStatus() != BuyStockinBean.STATUS1 && bsBean.getStatus() != BuyStockinBean.STATUS5 ) {
				throw new MyRuntimeException("入库单状态不是确认未通过或审核未通过，不可以编辑！");
			}
			SupplierStandardInfoBean ssiBean = supplierService.getSupplierStandardInfo("id="+bsBean.getSupplierId());
			if( ssiBean == null ) {
				bsBean.setProxyName("");
			} else {
				bsBean.setProxyName(ssiBean.getName());
			}
			bsBean.setCreateDatetime(bsBean.getCreateDatetime().substring(0,19));
			List<BuyStockinProductBean> bspList = service.getBuyStockinProductList("buy_stockin_id="+bsBean.getId(), -1, -1, "id asc");
			for( BuyStockinProductBean bspBean : bspList ) {
				totalCount += bspBean.getStockInCount();
				bspBean.setProxyName(bsBean.getProxyName());
			}
			bsBean.setBuyStockinProductList(bspList);
			String areaName = ProductStockBean.getAreaName(bsBean.getStockArea());
			model.addAttribute("areaName", areaName);
			model.addAttribute("bsBean", bsBean);
			model.addAttribute("bspList", bspList);
			model.addAttribute("totalCount", totalCount);
		} catch (MyRuntimeException mre ) {
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		} catch(Exception e ) {
			e.printStackTrace();
			request.setAttribute("tip",  "系统异常!");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			fittingBuyStockinService.releaseAll();
		}
		return "forward:/admin/fitting/editFittingBuyStockin.jsp";
	}
	
	@RequestMapping("editAllProduct")
	@ResponseBody
	public Map<String,Object> editAllProduct( HttpServletRequest request, HttpServletResponse response ) {
		Map<String,Object> result = new HashMap<String,Object>();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.DB);
		FittingBuyStockinService fittingBuyStockinService = new FittingBuyStockinService(IBaseService.CONN_IN_SERVICE,dbop);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbop);
		if(user == null){
			result.put("status", "failure");
			result.put("tip", "没有登录不可以操作！");
			return result;
		}
		UserGroupBean group = user.getGroup();
		boolean addFittingBuyStockin = group.isFlag(2095);
		if (!addFittingBuyStockin) {
			result.put("status", "failure");
			result.put("tip", "您没有添加配件采购入库单的权限, 不可以编辑配件入库单！");
			return result;
		}
		try {
			synchronized(lock) {
				int id = StringUtil.parstInt(request.getParameter("id"));
				String[] productCodes = request.getParameterValues("productCodes");
				BuyStockinBean bsBean = service.getBuyStockin("id="+id);
				if(bsBean.getStatus() != BuyStockinBean.STATUS1 && bsBean.getStatus() != BuyStockinBean.STATUS5 ) {
					throw new MyRuntimeException("入库单状态不是确认未通过或审核未通过，不可以编辑！");
				}
				if( productCodes != null )  {
					fittingBuyStockinService.editFittingBuyStockin(request,productCodes,user, id);
					result.put("status", "success");
					result.put("tip", "编辑成功！");
				} else {
					result.put("status", "failure");
					result.put("tip", "没有商品不能保存！");
				}
			}
		} catch (MyRuntimeException mre ) {
			result.put("status", "failure");
			result.put("tip", mre.getMessage());
			return result;
		} catch(Exception e ) {
			e.printStackTrace();
			result.put("status", "failure");
			result.put("tip", "系统异常!");
			return result;
		} finally {
			fittingBuyStockinService.releaseAll();
		}
		return result;
	}
	
	@RequestMapping("auditFittingBuyStockin")
	public String auditEditFittingBuyStockin( HttpServletRequest request, HttpServletResponse response,Model model ) {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		DbOperation dbop = new DbOperation();
		dbop.init(DbOperation.DB);
		FittingBuyStockinService fittingBuyStockinService = new FittingBuyStockinService(IBaseService.CONN_IN_SERVICE,dbop);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbop);
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean addFittingBuyStockin = group.isFlag(2097);
		if (!addFittingBuyStockin) {
			request.setAttribute("tip", "您没有审核配件采购入库单的权限, 不可以审核配件入库单！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		try {
			String code = StringUtil.convertNull(request.getParameter("code"));
			int mark = StringUtil.parstInt(request.getParameter("mark"));
			String remark = StringUtil.convertNull(request.getParameter("remark"));
			BuyStockinBean bsBean = service.getBuyStockin("code='"+code+"'");
			fittingBuyStockinService.auditFittingBuyStockin(bsBean.getId(), mark, remark, user);
		} catch (MyRuntimeException mre ) {
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		} catch(Exception e ) {
			e.printStackTrace();
			request.setAttribute("tip",  "系统异常!");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			fittingBuyStockinService.releaseAll();
		}
		return "forward:/admin/fitting/fittingstockin.jsp";
	}
	
	/**
	 *  获取商品的库存均价
	 * @param productCode
	 * @return
	 */
	@RequestMapping("/getStockPrice")
	@ResponseBody
	public Json getStockPrice(String productCode){
		Json j = new Json();
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		try{
			voProduct product = wareService.getProduct(productCode);
			if( product != null ) {
				if(product.getPrice5()>0){
					j.setObj(product.getPrice5());
					j.setSuccess(true);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return j;
	}
}
