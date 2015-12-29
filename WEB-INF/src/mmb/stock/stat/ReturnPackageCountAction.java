package mmb.stock.stat;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import net.sf.json.JSONArray;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.easyui.EasyuiComBoBoxBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ReturnPackageCountAction extends DispatchAction{
	public ActionForward toReturnPackagePage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//根据人查询他能查看哪些地方的内容
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
		request.setAttribute("cdaList", cdaList);
		Map<String,String> map = new HashMap<String,String>();
		for ( int i = 0; i < cdaList.size(); i ++ ) {
			Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
			map.put(cdaList.get(i),ProductStockBean.areaMap.get(areaId));
		}
		request.setAttribute("map", map);
		String toPage = (String)request.getParameter("path");
		if("returnpackage".equals(toPage))
			return mapping.findForward("returnpackagecount");
		else{
			return mapping.findForward("checkpackagecount");
		}
	}
	public String returnProductUpStatistics(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
		}
		response.setContentType("text/html; charset=utf-8");
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		String time = StringUtil.convertNull(request.getParameter("time")).trim();
		int productLine = StringUtil.toInt(request.getParameter("productLine"));
		String userId = StringUtil.convertNull(request.getParameter("userId")).trim();
		String productCode = StringUtil.convertNull(request.getParameter("productCode")).trim();
 		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		CargoServiceImpl cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuilder condition = new StringBuilder();
			if(time == null || "".equals(time)){
				time = DateUtil.getNowDateStr();
			}
//			condition.append("left(co.create_datetime,10)='" + StringUtil.toSql(time) + "' and coc.type=0");
			condition.append(" co.create_datetime >='"+StringUtil.toSql(time)+" 00:00:00' and co.create_datetime <'"+StringUtil.toSql(time)+" 23:59:59' ");
			condition.append(" and coc.type=0 ");
			if(areaId != -1){
				condition.append(" and co.stock_in_area=" + areaId);
			}
			if(productLine != -1){
				condition.append(" and plc.product_line_id=" + productLine);
			}
			if(!"".equals(userId)){
				if(userId.matches("[0-9]*")){
					CargoStaffBean staff = cargoService.getCargoStaff("code='" + userId + "'");
					if(staff == null){
						response.getWriter().write("物流员工编号不正确！");
						return null;
					}
					condition.append(" and co.create_user_id=" + staff.getUserId());
				}else{
					condition.append(" and co.create_user_name='" + userId + "'");
				}
			}
			if(!"".equals(productCode) && !"-1".equals(productCode)){
				condition.append(" and p.code='" + StringUtil.toSql(productCode) + "'");
			}
			//构建查询语句
	        String query = 	"select count(distinct co.id),count(distinct coc.product_id),sum(coc.stock_count) c from cargo_operation co " +
	        				"join cargo_operation_cargo coc on co.id = coc.oper_id join product p on p.id=coc.product_id " +
	        				"join product_line_catalog plc on plc.catalog_id=p.parent_id1 or p.parent_id2=plc.catalog_id " +
	        				"or p.parent_id3=plc.catalog_id";
	        query += " where " + condition;
	        ResultSet rs = null;
	        rs = dbOp.executeQuery(query);
	        String result = null;
	        if(rs.next()){
	        	result = time + "共生成" + rs.getInt(1) + "张退货上架单，商品总计:" + rs.getInt(2) + "个sku," + rs.getInt(3) + "个商品";
			}else{
				result = time + "查询结果异常";
			}
	        if(rs != null){
	        	rs.close();
	        }
	        StringBuilder str = new StringBuilder("<div style=margin-top:10px; text-align:left;>");
	        str.append(result);
			str.append("</div>");
			str.append("<div><input type=button onclick=hiddenSelf(); value=隐藏></div>");
			response.getWriter().write(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return null;
	}
	public String returnProductUpShelfStatistics(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
		}
		response.setContentType("text/html; charset=utf-8");
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		String time = StringUtil.convertNull(request.getParameter("time"));
		int productLine = StringUtil.toInt(request.getParameter("productLine"));
		String userName = StringUtil.convertNull(request.getParameter("userId"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		CargoServiceImpl cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuilder condition = new StringBuilder();
			if(time == null || "".equals(time)){
				time = DateUtil.getNowDateStr();
			}
			condition.append("left(rus.create_datetime,10)='" + StringUtil.toSql(time) + "'  and coc.type=0");
			if(areaId != -1){
				condition.append(" and co.stock_in_area=" + areaId);
			}
			if(productLine != -1){
				condition.append(" and plc.product_line_id=" + productLine);
			}
			if(!"".equals(userName)){
				condition.append(" and rus.create_user_name='" + userName + "'");
			}
			if(!"".equals(productCode) && !"-1".equals(productCode)){
				condition.append(" and p.code='" + StringUtil.toSql(productCode) + "'");
			}
			//构建查询语句
	        String query = 	"select count(distinct rus.code),count(distinct coc.product_id),sum(coc.stock_count) c from returned_up_shelf rus " +
	        				"join cargo_operation co on rus.code=co.source " +
	        				"join cargo_operation_cargo coc on co.id = coc.oper_id join product p on p.id=coc.product_id " +
	        				"join product_line_catalog plc on plc.catalog_id=p.parent_id1 or p.parent_id2=plc.catalog_id " +
	        				"or p.parent_id3=plc.catalog_id";
	        query += " where " + condition;
	        ResultSet rs = null;
	        rs = dbOp.executeQuery(query);
	        String result = null;
	        if(rs.next()){
	        	result = time + "共生成" + rs.getInt(1) + "张退货上架汇总单，商品总计:" + rs.getInt(2) + "个sku," + rs.getInt(3) + "个商品";
			}else{
				result = time + "查询结果异常";
			}
	        if(rs != null){
	        	rs.close();
	        }
	        StringBuilder str = new StringBuilder("<div style=margin-top:10px; text-align:left;>");
	        str.append(result);
			str.append("</div>");
			str.append("<div><input type=button onclick=hiddenSelf(); value=隐藏></div>");
			response.getWriter().write(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return null;
	}
	public ActionForward getCountResult(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		String queryTime = (String)request.getParameter("startTime");
		String operationId = request.getParameter("operationid");
		String productCode = request.getParameter("productCode");
		String areaId = request.getParameter("areaId");
		ReturnPackageCountService rpcs = new ReturnPackageCountService();
		String toPage = request.getParameter("path");
		String result = "";
		try{
			if("returnpackage".equals(toPage))
				result = rpcs.getCountResult(queryTime, operationId, areaId, productCode);
			else
				result = rpcs.getCountResult(queryTime, operationId, areaId);

			request.setAttribute("result", result);
			request.setAttribute("querytime", queryTime);
			request.setAttribute("operationId", operationId);
			request.setAttribute("areaId", areaId);
			request.setAttribute("productCode", productCode);

			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			request.setAttribute("cdaList", cdaList);
			Map<String,String> map = new HashMap<String,String>();
			for ( int i = 0; i < cdaList.size(); i ++ ) {
				Integer areaid = new Integer(Integer.parseInt(cdaList.get(i)));
				map.put(cdaList.get(i),ProductStockBean.areaMap.get(areaid));
			}
			request.setAttribute("map", map);
			if("returnpackage".equals(toPage))
				return mapping.findForward("returnpackagecount");
			else{
				return mapping.findForward("checkpackagecount");
			}
		}finally{
			rpcs.releaseAll();
		}
	}
	public void getLimitArea(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<String, String> areaMap = ProductWarePropertyService.getWeraAreaMap(request);
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		for(Map.Entry<String, String> entry : areaMap.entrySet()){
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId(entry.getKey());
			bean.setText(entry.getValue());
			list.add(bean);
		}
		response.getWriter().write(JSONArray.fromObject(list).toString());
	}
	public void getProductLine(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		ResultSet rs = null;
		try {
			//构建查询语句
	        String query = 	"select id,name from product_line";
	        rs = dbOp.executeQuery(query);
	        List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
	        while(rs.next()){
	        	EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
	        	bean.setId(rs.getInt(1) + "");
	        	bean.setText(rs.getString(2));
	        	list.add(bean);
			}
	        response.getWriter().write(JSONArray.fromObject(list).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
}
