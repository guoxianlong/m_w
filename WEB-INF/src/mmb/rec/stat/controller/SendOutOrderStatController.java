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
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProductLine;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/SendOutOrderStatController")
public class SendOutOrderStatController {
	/**
	 * @return 获取发货订单统计数据加载highcharts
	 * @author syuf
	 * @throws SQLException 
	 */
	@RequestMapping("/sendOutOrderStat")
	@ResponseBody
	public Json sendOutOrderStat(HttpServletRequest request,HttpServletResponse response,
			String area,String productLines,String searchType,String startDate,String endDate) throws ServletException, IOException, SQLException {
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		ResultSet rs = null;
		if("".equals(StringUtil.convertNull(startDate)) || "".equals(StringUtil.convertNull(endDate))){
			j.setMsg("开始时间或结束时间不能为空!");
			return j;
		}
		try {
			Map<String,Object> jMap = new HashMap<String, Object>();
			List<String> dates = new ArrayList<String>();
			List<Integer> productCount = new ArrayList<Integer>();
			List<Integer> orderCount = new ArrayList<Integer>();
			List<Integer> skuCount = new ArrayList<Integer>();
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
			if(productLines != null && productLines.length() > 0){
				int i = 0;
				for(String productLineId : productLines.split(",")){
					if(i == 0){
						condition.append(" and (product_line_id=" + productLineId);
					}else{
						condition.append(" or product_line_id=" + productLineId);
					}
					i++;
				}
				condition.append(")");
			}
			String[] times = DateUtil.getDateTimes(searchType, startDate, endDate);
			condition.append(" and date BETWEEN '" + times[0] + "' and '" + times[1] + "'");
			query.append(",sum(order_count),sum(product_count),sum(sku_count) from send_out_order_stat where ");
			query.append(condition);
			query.append(" GROUP BY datex");
			rs = dbOp.executeQuery(query.toString());
			while(rs.next()){
				dates.add(rs.getString(1));
				orderCount.add(rs.getInt(2));
				productCount.add(rs.getInt(3));
				skuCount.add(rs.getInt(4));
			}
			jMap.put("dates", dates);
			jMap.put("productCount", productCount);
			jMap.put("orderCount", orderCount);
			jMap.put("skuCount", skuCount);
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
	 * @return 加载产品线
	 * @author syuf
	 */
	@RequestMapping("/getProductLines")
	@ResponseBody
	public Json getProductLines(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		try {
			@SuppressWarnings("unchecked")
			List<voProductLine> productLines = wareService.getProductLineList("1=1");
			StringBuffer sb = new StringBuffer();
			if(productLines != null && productLines.size() > 0){
				sb.append("产品线:<input id=\"checkAll\" name=\"checkAll\" onclick=\"checkAll()\" type=\"checkbox\">全部 ");
				for(voProductLine line : productLines){
					sb.append("<input type=\"checkbox\" name=\"productLine\" value=\"" + line.getId() + "\" />" + line.getName() + " ");
				}
			}
			j.setObj(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dbOp.release();
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
