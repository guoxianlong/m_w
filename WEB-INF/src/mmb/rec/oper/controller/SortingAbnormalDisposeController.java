package mmb.rec.oper.controller;

import java.io.IOException;
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
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.SortingAbnormalBean;
import mmb.stock.stat.SortingAbnormalDisposeService;
import mmb.stock.stat.SortingAbnormalProductBean;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/SortingAbnormalDisposeController")
public class SortingAbnormalDisposeController {
	/**
	 * @return 加载权限遮罩库地区comboBox
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
	 * @param easyuiPage 分页bean
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param area 库地区
	 * @return 生成datagrid
	 * @author syuf
	 */
	@RequestMapping("/getSortingAbnormalDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getSortingAbnormalDatagrid (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,String startTime,String endTime,String area,String type,String status,String code) throws ServletException, IOException{
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int t = cdaList.size();
			for(int i = 0; i < t; i++ ) {
				availAreaIds += "," + cdaList.get(i);
			}
			
			if(!"".equals(StringUtil.checkNull(code))) {
				sql.append("select sa.* from sorting_abnormal sa, sorting_abnormal_product sap where sa.id = sap.sorting_abnormal_id");
				sqlCount.append("select count(sa.id) from sorting_abnormal sa, sorting_abnormal_product sap where sa.id = sap.sorting_abnormal_id");
			} else {
				sql.append("select sa.* from sorting_abnormal sa where sa.id > 0");
				sqlCount.append("select count(sa.id) from sorting_abnormal sa where sa.id > 0");
			}
			
			if(!"".equals(StringUtil.checkNull(area))) {
				params.append("&");
				params.append("wareArea=" + area);
				
			    sql.append(" and sa.ware_area = " + area );
			    sqlCount.append(" and sa.ware_area = " + area );
			} else {
			    sql.append(" and sa.ware_area in (" + availAreaIds + ")" );
			    sqlCount.append(" and sa.ware_area in (" + availAreaIds + ")" );
			}
			
			if(!"".equals(StringUtil.checkNull(startTime))&& !"".equals(StringUtil.checkNull(endTime))) {
				params.append("&");
				params.append("startTime=" + startTime);
				params.append("&");
				params.append("endTime=" + endTime);
				
				String createTimeStart = startTime + " 00:00:00";
				String createTimeEnd = endTime + " 23:59:59";
	        	sql.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
	        	sqlCount.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
			} else {
				String currentTime = DateUtil.getNow();
				currentTime = currentTime.substring(0,10);
				params.append("&");
				params.append("startTime=" + currentTime);
				params.append("&");
				params.append("endTime=" + currentTime);
				
				String createTimeStart = currentTime + " 00:00:00";
				String createTimeEnd = currentTime + " 23:59:59";
	        	sql.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
	        	sqlCount.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
			}
 			
			if(!"".equals(StringUtil.checkNull(status))) {
				params.append("&");
				params.append("status=" + status);
				
				sql.append(" and sa.status = " + status);
				sqlCount.append(" and sa.status = " + status);
			} else {
				params.append("&");
				params.append("status=" + SortingAbnormalBean.STATUS0);
				
				sql.append(" and sa.status = " + SortingAbnormalBean.STATUS0);
				sqlCount.append(" and sa.status = " + SortingAbnormalBean.STATUS0);
			}
			if(!"".equals(StringUtil.checkNull(type))) {
				params.append("&");
				params.append("type=" + type);
				
				sql.append(" and sa.abnormal_type = " + type);
				sqlCount.append(" and sa.abnormal_type = " + type);
			}
			
			if (!"".equals(StringUtil.checkNull(code))) {
				params.append("&");
				params.append("code=" + code);
				code = code.trim();
				if (code.matches("[0-9]{1,20}")) {
					sql.append(" and sap.product_code = '" + code + "'");
					sqlCount.append(" and sap.product_code = '" + code + "'");
				} else if (code.matches("[A-Z]{3}[0-9]{2}-[A-Z]{1}[0-9]{5,8}")) {
					sql.append(" and sap.cargo_whole_code = '" + code + "'");
					sqlCount.append(" and sap.cargo_whole_code = '" + code
							+ "'");
				} else if (code.matches("[A-Z]{1}[0-9]{5,8}")) {
					sql.append(" and sap.cargo_whole_code like '%" + code + "'");
					sqlCount.append(" and sap.cargo_whole_code like '%" + code
							+ "'");
				} 
			}
			int totalCount = sortingAbnormalDisposeService.getSortingAbnormalCount2(sqlCount.toString());
			datagridJson.setTotal((long)totalCount);
			@SuppressWarnings("unchecked")
			List<SortingAbnormalBean> sortingAbnormalList = sortingAbnormalDisposeService.getSortingAbnormal2(sql.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "create_datetime desc");
			Map<String,List<SortingAbnormalProductBean>> sapsMap = new HashMap<String, List<SortingAbnormalProductBean>>();
			if(sortingAbnormalList != null && sortingAbnormalList.size() > 0){
				for(SortingAbnormalBean saBean : sortingAbnormalList){
					List<SortingAbnormalProductBean> sortingAbnormalProductList = sortingAbnormalDisposeService.getSortingAbnormalProductList("sorting_abnormal_id = " + saBean.getId(), -1, -1, "lock_count asc");
					if(sortingAbnormalProductList != null && sortingAbnormalProductList.size() > 0){
						sapsMap.put(saBean.getId() + "", sortingAbnormalProductList);
					}
				}
			}
			request.getSession().setAttribute("sapsMap", sapsMap);
			datagridJson.setRows(sortingAbnormalList);
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagridJson;
	}
	@RequestMapping("/getSortingAbnormalProductDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getSortingAbnormalProductDatagrid (HttpServletRequest request,HttpServletResponse response,
			String saId) throws ServletException, IOException{
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			@SuppressWarnings("unchecked")
			Map<String,List<SortingAbnormalProductBean>> sapsMap  = (Map<String, List<SortingAbnormalProductBean>>) request.getSession().getAttribute("sapsMap");
			List<SortingAbnormalProductBean> sortingAbnormalProductList = new ArrayList<SortingAbnormalProductBean>();
			if(sapsMap != null && sapsMap.size() > 0){
				sortingAbnormalProductList = sapsMap.get(StringUtil.checkNull(saId));
			}
			datagridJson.setRows(sortingAbnormalProductList);
		} catch(Exception e ) {
			e.printStackTrace();
		} 
		return datagridJson;
	}
}
