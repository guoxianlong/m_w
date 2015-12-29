package mmb.stock.stat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.stock.cargo.CargoDeptAreaService;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stat.AbnormalRealProductBean;
import adultadmin.bean.stat.BsByAbnormalBean;
import adultadmin.bean.stat.WarehousingAbnormalBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

/**
 * 作者：石远飞
 * 
 * 日期：2013-4-3
 * 
 * 说明：入库异常单Service
 */
public class WarehousingAbnormalService extends BaseServiceImpl {

	public WarehousingAbnormalService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	public WarehousingAbnormalService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}
	//添加异常入库单（warehousing_abnormal表）
	public boolean addWarehousingAbnormal(WarehousingAbnormalBean bean){
		return addXXX(bean, "warehousing_abnormal");
	}
	//删除异常入库单（warehousing_abnormal表）
	public boolean delWarehousingAbnormal(String condition){
		return deleteXXX(condition, "warehousing_abnormal");
	}
	//更新异常入库单（warehousing_abnormal表）
	public boolean updateWarehousingAbnormal(String set,String condition){
		return updateXXX(set, condition, "warehousing_abnormal");
	}
	//保存实际退回商品（abnormal_real_products表）
	public boolean addAbnormalRealProduct(AbnormalRealProductBean bean){
		return addXXX(bean, "abnormal_real_products");
	}
	//删除实际退回商品（abnormal_real_products表）
	public boolean delAbnormalRealProduct(String condition){
		return deleteXXX(condition,"abnormal_real_products");
	}
	//更新实际退回商品（abnormal_real_products表）
	public boolean updateAbnormalRealProduct(String set,String condition){
		return updateXXX(set, condition, "abnormal_real_products");
	}
	//获取实际退回商品列表（abnormal_real_products表）
	@SuppressWarnings("rawtypes")
	public ArrayList getAbnormalRealProductList(String condition,int index,int count,String orderBy){
		return getXXXList(condition, index, count, orderBy, "abnormal_real_products", "adultadmin.bean.stat.AbnormalRealProductBean");
	}
	//异常入库单与报损报溢单关联表（bsby_abnormal表）
	public boolean addBsByAbnormal(BsByAbnormalBean bean){
		return addXXX(bean, "bsby_abnormal");
	}
	//查询异常入库单（warehousing_abnormal表）
	public WarehousingAbnormalBean getWarehousingAbnormal(String condition){
		return (WarehousingAbnormalBean) getXXX(condition, "warehousing_abnormal", "adultadmin.bean.stat.WarehousingAbnormalBean");
	}
	//获取异常入库单列表（warehousing_abnormal表）
	@SuppressWarnings("rawtypes")
	public ArrayList getWarehousingAbnormalList(String condition,int index,int count,String orderBy){
		return getXXXList(condition, index, count, orderBy, "warehousing_abnormal", "adultadmin.bean.stat.WarehousingAbnormalBean");
	}
	//查询订单信息（Order_Stock表）
	public OrderStockBean getOrderStock(String condition){
		return (OrderStockBean) getXXX(condition, "order_stock", "adultadmin.bean.order.OrderStockBean");
	}
	//查询订单信息(audit_package表)
	public AuditPackageBean getAuditPackage(String condition){
		return (AuditPackageBean)getXXX(condition,"audit_package","adultadmin.bean.order.AuditPackageBean");
	}
	//查询商品信息(product表)
	public voProduct getVOProduct(String condition){
		return (voProduct)getXXX(condition,"product","adultadmin.action.vo.voProduct");
	}
	public static String getWeraAreaOptions(HttpServletRequest request) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		String selectLable = "<select name='wareArea' id='wareArea' >";
		if( cdaList.size() == 0 ) { 
			selectLable +="<option value='-2'>无地区权限</option>";
  		}else{ 
  			selectLable +="<option value='-1'>请选择</option>";
  			for ( int i = 0; i < cdaList.size(); i ++ ) {
  				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
  				selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			}
		}
		selectLable += "</select>";
		return selectLable;
	}
	public static String getWeraAreaOptions(HttpServletRequest request, int needToSelect,boolean flag) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		String selectLable = null;
		if(flag){
			selectLable = "<select name='wareArea' id='wareArea' disabled='disabled'>";
		}else{
			selectLable = "<select name='wareArea' id='wareArea' >";
		}
		if( cdaList.size() == 0 ) { 
			selectLable +="<option value='-2'>无地区权限</option>";
		}else{ 
			selectLable +="<option value='-1'>请选择</option>";
			for ( int i = 0; i < cdaList.size(); i ++ ) {
				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
				if( needToSelect == areaId) {
					selectLable += "<option value='"+areaId+"' selected='selected' >"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				} else {
					selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				}
			}
		}
		selectLable += "</select>";
		return selectLable;
	}
}
