package mmb.rec.stat.controller;

/**
 * zy
 * 商品入库运营和上架效率
 * 2013-09-25
 */
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.stat.bean.CheckStockinStatBean;
import mmb.rec.stat.bean.UpshelfStatBean;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/CheckStockinStatController")
public class CheckStockinStatController {

	@RequestMapping("/getCountDatas")
	@ResponseBody
	public List getCountDatas(HttpServletRequest request,HttpServletResponse response, String searchType, String startTime, String endTime, 
			String wareArea, String productLine, String type) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		String availAreaIds = "100";
		if ("-1".equals(wareArea)) {
			Iterator itr = CargoDeptAreaService.getCargoDeptAreaList(request).iterator();
			for(; itr.hasNext(); ) {
				availAreaIds += "," + itr.next();
			}
		} else {
			availAreaIds = wareArea;
		}
		CheckStockinStatBean cssBean = null;
		List<CheckStockinStatBean> cssList = new ArrayList<CheckStockinStatBean>();
		String[] returntimes = null;
		int[] checkCounts = null;
		int[] upshelfCounts = null;
		try {
			String[] times = DateUtil.getDateTimes(searchType, startTime, endTime);
			StringBuffer sqlbuf = new StringBuffer();
			if("day".equals(searchType)){
				sqlbuf.append("select date datex");
			}else if("week".equals(searchType)){
				sqlbuf.append("select DATE_FORMAT(date,'%X年-第%v周') datex");
			}else if("month".equals(searchType)){
				sqlbuf.append("select DATE_FORMAT(date,'%X年-%m月') datex");
			}
			sqlbuf.append(",sum(cst.check_product_count),sum(cst.check_sku_count),sum(cst.upshelf_product_count),sum(cst.upshelf_sku_count) from check_stockin_stat cst ");
			sqlbuf.append(" where date between '").append(times[0]).append("' and '").append(times[1]).append("' ");
			sqlbuf.append(" and area in (").append(availAreaIds).append(") ");
			if (!"-1".equals(productLine) && !"".equals(productLine)) {
				sqlbuf.append(" and product_line_id in (").append(productLine).append(")");
			}
			sqlbuf.append(" group by datex");
			ResultSet rs = dbOp.executeQuery(sqlbuf.toString());
			while(rs.next()) {
				cssBean = new CheckStockinStatBean();
				cssBean.setDate(rs.getString(1));
				cssBean.setCheckProductCount(rs.getInt(2));
				cssBean.setCheckSkuCount(rs.getInt(3));
				cssBean.setUpshelfProductCount(rs.getInt(4));
				cssBean.setUpshelfSkuCount(rs.getInt(5));
				cssList.add(cssBean);
			}
			rs.close();
			if (cssList.size() != 0) {
				int x = cssList.size();
				returntimes = new String[x];
				checkCounts = new int[x];
				upshelfCounts = new int[x];
			}
			int i = 0;
			for (CheckStockinStatBean cBean : cssList) {
				returntimes[i] = cBean.getDate();
				if ("SKU".equals(type)) {
					checkCounts[i] = cBean.getCheckSkuCount();
					upshelfCounts[i] = cBean.getUpshelfSkuCount();
				} else {
					checkCounts[i] = cBean.getCheckProductCount();
					upshelfCounts[i] = cBean.getUpshelfProductCount();
				}
				i++;
			}
			List returnStrings = new ArrayList();
			returnStrings.add(returntimes);
			returnStrings.add(checkCounts);
			returnStrings.add(upshelfCounts);
			
			return returnStrings;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return null;
	}
	
	@RequestMapping("/getWareArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getWareArea(HttpServletRequest request,HttpServletResponse response
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
				bean.setSelected(true);
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
	
	
	@RequestMapping("/getProductLine")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getProductLine(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			List<voProductLine> list = wareService.getProductLineList("1=1");
			if(list != null && list.size() > 0){
				comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId("-1");
				bean.setText("全部");
				bean.setSelected(true);
				comboBoxList.add(bean);
				for(voProductLine s : list){
					bean = new EasyuiComBoBoxBean();
					bean.setId("" + s.getId());
					bean.setText(s.getName());
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return comboBoxList;
	}
	
	
	@RequestMapping("/getUpshelfStat")
	@ResponseBody
	public List getUpshelfStat(HttpServletRequest request,HttpServletResponse response, String searchType, String startTime, String endTime, 
			String wareArea, String productLine) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		String availAreaIds = "100";
		if ("-1".equals(wareArea)) {
			Iterator itr = CargoDeptAreaService.getCargoDeptAreaList(request).iterator();
			for(; itr.hasNext(); ) {
				availAreaIds += "," + itr.next();
			}
		} else {
			availAreaIds = wareArea;
		}
		UpshelfStatBean usBean = null;
		List<UpshelfStatBean> usList = new ArrayList<UpshelfStatBean>();
		String[] returntimes = null;
		int[] operCounts = null;
		int[] productCounts = null;
		int[] skuCounts = null;
		try {
			String[] times = DateUtil.getDateTimes(searchType, startTime, endTime);
			StringBuffer sqlbuf = new StringBuffer();
			if("day".equals(searchType)){
				sqlbuf.append("select date datex");
			}else if("week".equals(searchType)){
				sqlbuf.append("select DATE_FORMAT(date,'%X年-第%v周') datex");
			}else if("month".equals(searchType)){
				sqlbuf.append("select DATE_FORMAT(date,'%X年-%m月') datex");
			}
			sqlbuf.append(",sum(us.oper_count),sum(us.product_count),sum(us.sku_count) from upshelf_stat us ");
			sqlbuf.append(" where date between '").append(times[0]).append("' and '").append(times[1]).append("' ");
			sqlbuf.append(" and area in (").append(availAreaIds).append(") ");
			if (!"-1".equals(productLine) && !"".equals(productLine)) {
				sqlbuf.append(" and product_line_id in (").append(productLine).append(")");
			}
			sqlbuf.append(" group by datex");
			ResultSet rs = dbOp.executeQuery(sqlbuf.toString());
			while(rs.next()) {
				usBean = new UpshelfStatBean();
				usBean.setDate(rs.getString(1));
				usBean.setOperCount(rs.getInt(2));
				usBean.setProductCount(rs.getInt(3));
				usBean.setSkuCount(rs.getInt(4));
				usList.add(usBean);
			}
			rs.close();
			if (usList.size() != 0) {
				int x = usList.size();
				returntimes = new String[x];
				operCounts = new int[x];
				productCounts = new int[x];
				skuCounts = new int[x];
			}
			int i = 0;
			for (UpshelfStatBean cBean : usList) {
				returntimes[i] = cBean.getDate();
				operCounts[i] = cBean.getOperCount();
				productCounts[i] = cBean.getProductCount();
				skuCounts[i] = cBean.getSkuCount();
				i++;
			}
			List returnStrings = new ArrayList();
			returnStrings.add(returntimes);
			returnStrings.add(operCounts);
			returnStrings.add(productCounts);
			returnStrings.add(skuCounts);
			
			return returnStrings;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return null;
	}
}
