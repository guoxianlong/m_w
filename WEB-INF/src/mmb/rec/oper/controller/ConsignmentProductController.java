package mmb.rec.oper.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.oper.service.ConsignmentService;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.ware.WareService;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.framework.IConstants;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/consignmentProductController")
public class ConsignmentProductController {
	
	private byte[] consignmentLock = new byte[0];
	
	@RequestMapping("/getReturnConsignmentProduct")
	@ResponseBody
	public EasyuiDataGridJson getReturnConsignmentProduct(HttpServletRequest request, HttpServletResponse response,
			@Param("wareArea") String wareArea,
			@Param("productCode") String productCode
	) {
		EasyuiDataGridJson result = new EasyuiDataGridJson();
		List<ProductStockBean> list = new ArrayList<ProductStockBean>();
		result.setRows(list);
		result.setTotal(0L);
		int pageIndex = 0;
		int countPerPage = 10;
		if( request.getParameter("rows") != null ) {
			countPerPage = StringUtil.toInt(request.getParameter("rows"));
		}
		if( request.getParameter("page") != null ) {
			pageIndex = StringUtil.StringToId(request.getParameter("page"));
			pageIndex = pageIndex - 1;
		}
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			result.setTip("没有登录！");
			return result;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(1462)) {
			result.setTip("你没有快销商品退货库库存功能权限，不能查看或操作！");
			return result;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			voProduct product = null;
			if( productCode != null && !productCode.equals("")) {
				product = wareService.getProduct(productCode);
			}
			
			StringBuilder sql = new StringBuilder("select cia.old_id, ci.area_id,ci.whole_code,cps.stock_count,cps.stock_lock_count,p.name,p.code,p.id,cps.id,ci.id from cargo_info ci, cargo_product_stock cps, product_sell_property psp, product p,cargo_info_area cia where cps.product_id = psp.product_id and ci.area_id = cia.id and p.id = cps.product_id and cps.cargo_id = ci.id and (cps.stock_count + cps.stock_lock_count) > 0 and ci.stock_type=");
			StringBuilder sqlCount = new StringBuilder("select count(cps.id) from cargo_info ci, cargo_product_stock cps, product_sell_property psp, product p, cargo_info_area cia where cps.product_id = psp.product_id and ci.area_id = cia.id and p.id = cps.product_id and cps.cargo_id = ci.id and (cps.stock_count + cps.stock_lock_count) > 0 and ci.stock_type=");
			sql.append(ProductStockBean.STOCKTYPE_RETURN);
			sql.append(" and psp.type=1");
			sqlCount.append(ProductStockBean.STOCKTYPE_RETURN);
			sqlCount.append(" and psp.type=1");
			if( product != null ) {
				sql.append(" and cps.product_id=").append(product.getId());
				sqlCount.append(" and cps.product_id=").append(product.getId());
			} else if( product == null && productCode != null && !productCode.equals("") ) {
				sql.append(" and cps.product_id = -1 ");
				sqlCount.append(" and cps.product_id = -1 ");
			}
			
			int wareAreaId = StringUtil.toInt(wareArea);
			if( wareAreaId != -1 ) {
				CargoInfoAreaBean ciaBean = cargoService.getCargoInfoArea("old_id="+wareAreaId);
				if(ciaBean != null ) {
					sql.append(" and cia.old_id=").append(ciaBean.getId());
					sqlCount.append(" and cia.old_id=").append(ciaBean.getId());
				} else {
					sql.append(" and cps.id = 0");
					sqlCount.append(" and cps.id = 0");
				}
			}
			list = consignmentService.getReturnConsignmentCargoProductStockList(sql.toString(), pageIndex * countPerPage, countPerPage, null);
			int totalCount = consignmentService.getReturnConsignmentProductStockCount(sqlCount.toString());
			result.setTotal((long)totalCount);
			result.setRows(list);
		} catch (Exception e) {
			e.printStackTrace();
			result.setTip("查询发生了异常！");
		} finally {
			wareService.releaseAll();
		}
		return result;
	}
	
	/**
	 * 根据在退货库快销商品勾选的提交 生成调拨单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createStockExchange")
	@ResponseBody
	public Map<String,String> createStockExchange(HttpServletRequest request, HttpServletResponse response) {
		
		
		Map<String,String> result = new HashMap<String,String>();
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			result.put("status", "fail");
			result.put("tip", "没有登录！");
			return result;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(1462)) {
			result.put("status", "fail");
			result.put("tip", "你没有快销商品退货库库存功能权限，不能查看或操作！");
			return result;
		}
		String[] cargoProductStockIds = request.getParameterValues("cargoProductStockIds");
		String wareArea = request.getParameter("wareArea");
		int wareAreaId = -1;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			if( cargoProductStockIds == null ) {
				result.put("status", "fail");
				result.put("tip", "没有勾选条目！");
				return result;
			}
			if( wareArea != null && !wareArea.equals("") ) {
				wareAreaId = StringUtil.toInt(wareArea);
			} else {
				result.put("status", "fail");
				result.put("tip", "地区参数错误！");
				return result;
			}
			if( wareAreaId == -1 ) {
				result.put("status", "fail");
				result.put("tip", "地区参数错误！");
				return result;
			}
			synchronized (consignmentLock) {
				List<String> cpsIds = new ArrayList<String>();
				for( int i = 0 ; i < cargoProductStockIds.length; i++ ) {
					String cargoProductStockId = cargoProductStockIds[i];
					cpsIds.add(cargoProductStockId);
				}
				wareService.getDbOp().startTransaction();
				//添加 基本的调拨单----------注意状态的变化
				Object result1 = consignmentService.createStockExchange(user,wareAreaId,wareAreaId,ProductStockBean.STOCKTYPE_RETURN, ProductStockBean.STOCKTYPE_BACK);
				if( result1 instanceof StockExchangeBean ) {
					StockExchangeBean seBean = (StockExchangeBean)result1;
					//添加商品到调拨单
					String result2 = consignmentService.addProductToStockExchange(user,seBean,cpsIds);
					if ( !result2.equals("SUCCESS") ) {
						wareService.getDbOp().rollbackTransaction();
						result.put("status", "fail");
						result.put("tip", result2);
						return result;
					}
					//确认整个调拨单
					String result3 = consignmentService.confirmStockExchange(user, seBean.getId());
					if( !result3.equals("SUCCESS") ) {
						wareService.getDbOp().rollbackTransaction();
						result.put("status", "fail");
						result.put("tip", result3);
						return result;
					}
					//修改调拨单为已出库待审核
					String result4 = consignmentService.completeStockOut(user, seBean.getId());
					if( !result3.equals("SUCCESS") ) {
						wareService.getDbOp().rollbackTransaction();
						result.put("status", "fail");
						result.put("tip", result4);
						return result;
					}
					result.put("status", "success");
					result.put("url", request.getContextPath()+"/admin/productStock/stockExchange.jsp?exchangeId=" + seBean.getId());
					wareService.getDbOp().commitTransaction();
				} else {
					String tip = (String) result1;
					wareService.getDbOp().rollbackTransaction();
					result.put("status", "fail");
					result.put("tip", tip);
					return result;
				}
			}
		} catch(Exception e ) {
			e.printStackTrace();
			boolean isAuto = false;
			try {
				isAuto = wareService.getDbOp().getConn().getAutoCommit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if( !isAuto ) {
				wareService.getDbOp().rollbackTransaction();
			}
		} finally {
			wareService.releaseAll();
		}
		return result;
	}

}
