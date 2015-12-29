package mmb.rec.stat.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.Json;
import mmb.stock.cargo.CargoDeptAreaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/SortingStatController")
public class SortingStatController {
	/**
	 * @return 获取分拣统计数据加载highcharts
	 * @author syuf
	 */
	@RequestMapping("/sortingStat")
	@ResponseBody
	public Json sortingStat(HttpServletRequest request,HttpServletResponse response,
			String area,String searchType,String startDate,String endDate) throws ServletException, IOException, SQLException {
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		ResultSet rs = null;
		if("".equals(StringUtil.convertNull(startDate)) || "".equals(StringUtil.convertNull(endDate))){
			j.setMsg("开始时间或结束时间不能为空!");
			return j;
		}
		try {
			Map<String,Object> jMap = new HashMap<String, Object>();
			List<String> dates = new ArrayList<String>();
			List<Integer> sortingCount = new ArrayList<Integer>();
			StringBuffer query = new StringBuffer();
			StringBuffer condition = new StringBuffer();
			if("day".equals(searchType)){
				query.append("select date datex");
			}else if("week".equals(searchType)){
				query.append("select DATE_FORMAT(date,'%X-%v周') datex");
			}else if("month".equals(searchType)){
				query.append("select DATE_FORMAT(date,'%X-%m月') datex");
			}
			condition.append("1=1");
			if("-1".equals(area)){
				List<String> areas = CargoDeptAreaService.getCargoDeptAreaList(request);
				if(areas != null && areas.size() > 0){
					int i = 0;
					for(String areaId : areas){
						if(i == 0){
							condition.append(" and (area=" + areaId);
						}else{
							condition.append(" or area=" + areaId);
						}
						i++;
					}
					condition.append(")");
				}
			}else{
				condition.append(" and area=" + area);
			}
			String[] times = DateUtil.getDateTimes(searchType, startDate, endDate);
			condition.append(" and date BETWEEN '" + times[0] + "' and '" + times[1] + "'");
			query.append(",sum(sorting_count) from sorting_stat where ");
			query.append(condition);
			query.append(" GROUP BY datex");
			rs = dbOp.executeQuery(query.toString());
			while(rs.next()){
				dates.add(rs.getString(1));
				sortingCount.add(rs.getInt(2));
			}
			jMap.put("dates", dates);
			jMap.put("sortingCount", sortingCount);
			j.setObj(jMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dbOp.release();
			if(rs != null){
				rs.close();
			}
		}
		return j;
	}
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
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId("-1");
				bean.setText("全部仓");
				comboBoxList.add(bean);
				for(String s : areaList){
					bean = new EasyuiComBoBoxBean();
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
}
