package mmb.rec.stat.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.stat.bean.QualifiedStockVolumeShareBean;
import mmb.rec.stat.bean.StockShareBean;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.Json;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
//库存运营
@Controller
@RequestMapping("/stockShareController")
public class StockShareController {
	@RequestMapping("/stockShareJsp")
	public String stockShareJsp(HttpServletRequest request,HttpServletResponse response){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp(DbOperation.DB_SLAVE));
		try{
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			String endTime = sdf.format(c.getTime());//今天
			c.add(Calendar.MONTH, -1);
			String startTime = sdf.format(c.getTime());//上一个月
			//product_line_id in() and area= and date>'' and date<'';
			/*
			 * 	条件：在一个时间段
			 *	1.先查询产品线 
			 *  2.根据产品线查询时间段下所有的数据
			 */
			List productLineList=wareService.getProductLineList("1=1");//产品线
			List<StockShareBean> stocksharelist = new ArrayList<StockShareBean>();//所有产品线在这个时间段每天的总和
			String timecondition = " and date>='"+startTime+"' and date <='"+endTime+"'";
			ResultSet rs2 = service.getDbOp().executeQuery("select date, sum(price),sum(product_count),sum(sku_count) from stock_share where area in(3,4) "
														+timecondition+" group by date order by date");
			/*
			 * 查询当天库存数量总和
			 */
			while(rs2.next()){
				StockShareBean bean = new StockShareBean();
				bean.setDate(rs2.getString(1));
				bean.setPriceSum(rs2.getDouble(2));//记录金额总和
				bean.setProductSum(rs2.getDouble(3));//记录库存总和
				bean.setSkuSum(rs2.getDouble(4));//记录sku总和
				stocksharelist.add(bean);
			}
			List<StockShareBean> stocksharelistSub = new ArrayList<StockShareBean>();//每个时间点下的总和
				//按时间分组获取每个时间点的金额数、库存总数、sku数
			String condition = "select date, sum(price),sum(product_count),sum(sku_count) from stock_share where area in(3,4)"
							+timecondition+" group by date order by date";
			ResultSet rs = service.getDbOp().executeQuery(condition);
			BigDecimal bd = new BigDecimal(100);
			int i=0;
			while(rs.next()){//每一条就是每一天
				StockShareBean bean2 = stocksharelist.get(i++);
				boolean priceboo = true;
				if(bean2.getPriceSum()<=0){
					priceboo = false;
				}
				BigDecimal pricesum = new BigDecimal(bean2.getPriceSum());
				boolean productboo = true;
				if(bean2.getProductSum()<=0){
					productboo = false;
				}
				BigDecimal productsum = new BigDecimal(bean2.getProductSum());
				boolean skuboo = true;
				if(bean2.getSkuSum()<=0){
					skuboo = false;
				}
				BigDecimal skusum = new BigDecimal(bean2.getSkuSum());
				
				StockShareBean bean = new StockShareBean();
				String date = rs.getString(1);//日期
				bean.setDate(date);
				BigDecimal price = new BigDecimal(rs.getFloat(2));//金额
				if(priceboo){
					bean.setPrice(price.divide(pricesum, 4, BigDecimal.ROUND_HALF_DOWN).multiply(bd).floatValue());
				}
				
				BigDecimal productCount = new BigDecimal(rs.getDouble(3));//件数
				bean.setProductCount(productCount.intValue());
				if(productboo){
					bean.setProductSum(productCount.divide(productsum,4, BigDecimal.ROUND_HALF_DOWN).multiply(bd).doubleValue());
				}
				
				BigDecimal skuCount = new BigDecimal(rs.getDouble(4));//sku
				bean.setSkuCount(skuCount.intValue());
				if(skuboo){
					bean.setSkuSum(skuCount.divide(skusum, 4, BigDecimal.ROUND_HALF_DOWN).multiply(bd).doubleValue());
				}
				
				stocksharelistSub.add(bean);
			}
			
			request.setAttribute("stocksharelist", stocksharelistSub);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("startTime", startTime);
			request.setAttribute("endTime", endTime);
			request.setAttribute("area", -1);
			request.setAttribute("timegroup", "0");
			request.setAttribute("lines", new ArrayList<Integer>());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return "forward:/admin/rec/stat/sortingStatisticalDetailed.jsp";
	}
	
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
	/**
	 * 库存占比图表
	 * 2013-9-26
	 * 朱爱林	
	 */
	@RequestMapping("/stockShareDetails")
	public String stockShareDetails(HttpServletRequest request,HttpServletResponse response){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp(DbOperation.DB_SLAVE));
		try{
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			//1 day,2 week,3 month
			String timegroup = StringUtil.convertNull(request.getParameter("timegroup"));
			String startTime = StringUtil.convertNull(request.getParameter("startTime"));
			String endTime = StringUtil.convertNull(request.getParameter("endTime"));
			int area = StringUtil.parstInt(StringUtil.convertNull(request.getParameter("area")));
			StringBuilder timecondition = new StringBuilder();
			StringBuilder timegroupcondition = new StringBuilder();//时间分组
			String timecondition2 = "";
			String[] productlines = request.getParameterValues("productLine");
			List<Integer> lines = new ArrayList<Integer>();
			if(productlines!=null&&productlines.length>0){
				timecondition.append(" and product_line_id in(");
				for(int i=0;i<productlines.length;i++){
					lines.add(StringUtil.parstInt(productlines[i]));
					if(i==productlines.length-1){
						timecondition.append(productlines[i]+") ");
					}else{
						timecondition.append(productlines[i]+",");
					}
				}
			}
			if(area!=0){
				timecondition.append(" and area = "+area);
				timecondition2 += " and area = "+area;
			}else{
				timecondition.append(" and area in(3,4) ");
				timecondition2 += " and area in(3,4) ";
			}
			String[] result = new String[2];
			if("2".equals(timegroup)){
				timegroupcondition.append(" DATE_FORMAT(date,'%X年-第%v周') ");//周一开始  %V是从周日开始
//				dateformatUtil("week",startTime,endTime);
				result = DateUtil.getDateTimes("week", startTime, endTime);
			}else if("3".equals(timegroup)){
				timegroupcondition.append(" DATE_FORMAT(date,'%X年-%m月') ");//01 格式
//				dateformatUtil("month",startTime,endTime);
				result = DateUtil.getDateTimes("month", startTime, endTime);
			}else{//默认与按天的
				timegroupcondition.append(" date ");
				result[0] = startTime;
				result[1] = endTime;
			}
			
			
			
			if(!"".equals(startTime)){
				timecondition.append(" and date >= '").append(StringUtil.toSql(result[0])+"' ");
				timecondition2+= " and date >= '"+StringUtil.toSql(result[0])+"' ";
			}
			if(!"".equals(endTime)){
				timecondition.append(" and date <= '").append(StringUtil.toSql(result[1])+"' ");
				timecondition2+= " and date <= '"+StringUtil.toSql(result[1])+"' ";
			}
			//product_line_id in() and area= and date>'' and date<'';
			/*
			 * 	条件：在一个时间段
			 *	1.先查询产品线 
			 *  2.根据产品线查询时间段下所有的数据
			 */
			List productLineList=wareService.getProductLineList("1=1");//产品线
			List<StockShareBean> stocksharelist = new ArrayList<StockShareBean>();//所有产品线在这个时间段每天的总和
//			String timecondition = " and date>='"+startTime+"' and date <='"+endTime+"'";
			ResultSet rs2 = service.getDbOp().executeQuery("select "+timegroupcondition.toString()
															+" dd, sum(price),sum(product_count),sum(sku_count) from stock_share where 1=1 "
															+timecondition2+" group by dd ");
			//这里记录的是此条产品线下的总数
			while(rs2.next()){
				StockShareBean bean = new StockShareBean();
				bean.setDate(rs2.getString(1));
				bean.setPriceSum(rs2.getDouble(2));//记录金额总和
				bean.setProductSum(rs2.getDouble(3));//记录库存总和
				bean.setSkuSum(rs2.getDouble(4));//记录sku总和
				stocksharelist.add(bean);
			}
			List<StockShareBean> stocksharelistSub = new ArrayList<StockShareBean>();//每个时间点下的总和
			//stocksharelist只有1条
				
				//按时间分组获取每个时间点的金额数、库存总数、sku数
				String condition = "select "+timegroupcondition.toString()+" dd, sum(price),sum(product_count),sum(sku_count) from stock_share where 1=1"
						+timecondition+" group by dd order by dd";
				ResultSet rs = service.getDbOp().executeQuery(condition);
				BigDecimal bd = new BigDecimal(100);
				int i=0;
				while(rs.next()){//每一条就是每一天
					
					StockShareBean bean2 = stocksharelist.get(i++);
					boolean priceboo = true;
					if(bean2.getPriceSum()<=0){
						priceboo = false;
					}
					BigDecimal pricesum = new BigDecimal(bean2.getPriceSum());
					boolean productboo = true;
					if(bean2.getProductSum()<=0){
						productboo = false;
					}
					BigDecimal productsum = new BigDecimal(bean2.getProductSum());
					boolean skuboo = true;
					if(bean2.getSkuSum()<=0){
						skuboo = false;
					}
					BigDecimal skusum = new BigDecimal(bean2.getSkuSum());
					
					StockShareBean bean = new StockShareBean();
					String date = rs.getString(1);//日期
					bean.setDate(date);
					BigDecimal price = new BigDecimal(rs.getDouble(2));//金额
					if(priceboo){
						bean.setPrice(price.divide(pricesum, 4, BigDecimal.ROUND_HALF_DOWN).multiply(bd).floatValue());
					}
					
					BigDecimal productCount = new BigDecimal(rs.getDouble(3));//件数
					bean.setProductCount(productCount.intValue());
					if(productboo){
						bean.setProductSum(productCount.divide(productsum,4, BigDecimal.ROUND_HALF_DOWN).multiply(bd).doubleValue());
					}
					
					BigDecimal skuCount = new BigDecimal(rs.getDouble(4));//sku
					bean.setSkuCount(skuCount.intValue());
					if(skuboo){
						bean.setSkuSum(skuCount.divide(skusum, 4, BigDecimal.ROUND_HALF_DOWN).multiply(bd).doubleValue());
					}
					
					stocksharelistSub.add(bean);
				}
			request.setAttribute("stocksharelist", stocksharelistSub);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("startTime", startTime);
			request.setAttribute("endTime", endTime);
			request.setAttribute("area", area==0?-1:area);
			request.setAttribute("timegroup", timegroup);
			request.setAttribute("lines", lines);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return "forward:/admin/rec/stat/sortingStatisticalDetailed.jsp";
	}
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
				for(voProductLine line : productLines){
					sb.append("<input type=\"checkbox\" name=\"productLine\" id="+line.getId()+" value=\"" + line.getId() + "\" >" 
							+ "<label for="+line.getId()+" > "+line.getName() + "</label> ");
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
	@RequestMapping("/qualifiedStockVolumeShareDetails")
	@ResponseBody
	public Object QualifiedStockVolumeShareDetails(HttpServletRequest request,HttpServletResponse response){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp(DbOperation.DB_SLAVE));
		Json json = new Json();
		try{
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			StringBuilder timecondition = new StringBuilder();
			StringBuilder searchTypeCondition = new StringBuilder();//时间分组
			String productline = StringUtil.convertNull(request.getParameter("productLine"));//产品线
			String[] productlines = null;
			if(!"".equals(productline)){
				productlines = productline.split(",");
			}
			int area = StringUtil.toInt(StringUtil.convertNull(request.getParameter("area")));//地区
			String dateStart = StringUtil.convertNull(request.getParameter("dateStart"));
			String dateEnd = StringUtil.convertNull(request.getParameter("dateEnd"));
			String searchType = StringUtil.convertNull(request.getParameter("searchType"));//1日,2周,3月
			Map<Integer,Map<String,QualifiedStockVolumeShareBean>> chartMap = new LinkedHashMap<Integer,Map<String,QualifiedStockVolumeShareBean>>();
			
			List<String> productNameList = new ArrayList<String>();//页面上横轴的值
			List productLines = wareService.getProductLineList("1=1");//获取产品线
			Map<String,String> productMap = new HashMap<String, String>();// id : name
			for(int i=0;i<productLines.size();i++){
				voProductLine productLine = (voProductLine) productLines.get(i);
				productMap.put(productLine.getId()+"", productLine.getName());
			}
			if(productlines!=null&&productlines.length>0){
				timecondition.append(" and product_line_id in(");
				for(int i=0;i<productlines.length;i++){
					chartMap.put(Integer.parseInt(productlines[i]), new LinkedHashMap<String,QualifiedStockVolumeShareBean>());
					if(i==productlines.length-1){
						timecondition.append(productlines[i]+") ");
					}else{
						timecondition.append(productlines[i]+",");
					}
					String name = productMap.get(Integer.parseInt(productlines[i])+"");
					productNameList.add(name);
				}
			}else{
//				chartMap.put(3, new LinkedHashMap<String,QualifiedStockVolumeShareBean>());
//				chartMap.put(4, new LinkedHashMap<String,QualifiedStockVolumeShareBean>());
				//默认全部
				for(int i=0;i<productLines.size();i++){
					voProductLine productLine = (voProductLine) productLines.get(i);
					chartMap.put(productLine.getId(), new LinkedHashMap<String,QualifiedStockVolumeShareBean>());
					
					String name = productMap.get(productLine.getId()+"");
					productNameList.add(name);
				}
			}
			if(area!=-1){
				timecondition.append(" and area = "+area);
			}else{
				timecondition.append(" and area in(3,4) ");
			}
			String[] result = new String[2];
			List<String> timeList = null;
			if("2".equals(searchType)){
				searchTypeCondition.append(" DATE_FORMAT(date,'%X-%v') ");//周一开始  %V是从周日开始
//				dateformatUtil("week",startTime,endTime);
				result = DateUtil.getDateTimes("week", dateStart, dateEnd);
				timeList = queryTimezoneValues("week",result[0],result[1]);
				addChartMapValue(chartMap, timeList);//存放每个产品线下的所有时间点
				
				
			}else if("3".equals(searchType)){
				searchTypeCondition.append(" DATE_FORMAT(date,'%X-%m') ");//01 格式
//				dateformatUtil("month",startTime,endTime);
				result = DateUtil.getDateTimes("month", dateStart, dateEnd);
				timeList = queryTimezoneValues("month",result[0],result[1]);
				addChartMapValue(chartMap, timeList);//存放每个产品线下的所有时间点
			}else{//默认与按天的
				searchTypeCondition.append(" date ");
				result[0] = dateStart;
				result[1] = dateEnd;
				timeList = queryTimezoneValues("",result[0],result[1]);
				addChartMapValue(chartMap, timeList);//存放每个产品线下的所有时间点
			}
			
			if(!"".equals(dateStart)){
				timecondition.append(" and date >= '").append(StringUtil.toSql(result[0])+"' ");
			}
			if(!"".equals(dateEnd)){
				timecondition.append(" and date <= '").append(StringUtil.toSql(result[1])+"' ");
			}
			
			
			String condition = "select product_line_id pl,"+searchTypeCondition.toString()
					+" dd, sum(product_line_product_volume),sum(area_stock_volume) from qualified_stock_volume_share where 1=1"
					+timecondition+" group by pl,dd order by pl,dd";
			ResultSet rs = service.getDbOp().executeQuery(condition);
			
			Map<String,QualifiedStockVolumeShareBean> maps = null;
			QualifiedStockVolumeShareBean bean = null;
			
			while(rs.next()){
				int productLine = rs.getInt(1);//产品线
//				String name = productMap.get(productLine+"");
//				if(!productNameList.contains(name)){
//					productNameList.add(name);
//				}
				String date = rs.getString(2);//时间
				double productLineVolume = rs.getDouble(3);//产品线下商品体积
				double areaStockVolume = rs.getDouble(4);//产品线下商品体积
				//Map<Integer,Map<String,QualifiedStockVolumeShareBean>> chartMap
				if(chartMap.containsKey(productLine)){//如果是同一个产品线，则相加
					maps = chartMap.get(productLine);
					if(maps.containsKey(date)){//如果是同一个日期下，则相加
						bean = maps.get(date);
						bean.setAreaStockVolume(bean.getAreaStockVolume()+areaStockVolume);
						bean.setProductLineProductVolume(bean.getProductLineProductVolume()+productLineVolume);
						bean.setProductLineId(productLine);
						bean.setDate(date);
						bean.setProductLineName(productMap.get(bean.getProductLineId()+""));//设置产品线名称
					}
				}
			}
			maps = null;
			if(chartMap!=null){
				Iterator<Integer> ii = chartMap.keySet().iterator();
				while(ii.hasNext()){
					maps = chartMap.get(ii.next());
					Iterator<String> iter = maps.keySet().iterator();
					while(iter.hasNext()){
						bean = maps.get(iter.next());
						BigDecimal bd1 = new BigDecimal(bean.getAreaStockVolume());
						BigDecimal bd2 = new BigDecimal(bean.getProductLineProductVolume());
						if(bd1.doubleValue()==0||bd2.doubleValue()==0){
							bean.setShare(0);//没有就为0
						}else{
							bean.setShare(bd2.divide(bd1, 4, BigDecimal.ROUND_HALF_DOWN).floatValue()*100);
						}
						
					}
				}
			}
			Map<String,Object> resultMap = new HashMap<String, Object>();
			resultMap.put("productNameList", productNameList);
			resultMap.put("xAxis", timeList);
			resultMap.put("yAxis", chartMap);
			json.setObj(resultMap);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return json;
	}
	/**
	 * 存放每个产品线下的所有时间点
	 * 2013-9-26
	 * 朱爱林
	 */
	private void addChartMapValue(
			Map<Integer, Map<String, QualifiedStockVolumeShareBean>> chartMap,
			List<String> timeList) {
		Iterator<Integer> ite = chartMap.keySet().iterator();
		while(ite.hasNext()){
			Integer key1 = ite.next();
			Map<String, QualifiedStockVolumeShareBean> mm = chartMap.get(key1);
			for(int i=0;i<timeList.size();i++){
				mm.put(timeList.get(i), new QualifiedStockVolumeShareBean());//key:时间，value:存储容积比率的bean
			}
		}
	}
	public static void main(String[] args) throws Exception {
		System.out.println(queryTimezoneValues("month", "2012-10-02", "2013-09-29"));
	}
	private static List<String> queryTimezoneValues(String timeType,String startTime,String endTime) throws Exception{
		
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date start = sdf.parse(startTime);
		Date end = sdf.parse(endTime);
		calendar.setTime(sdf.parse(startTime));
		Calendar c_begin = new GregorianCalendar();
	    Calendar c_end = new GregorianCalendar();
	    c_begin.setTime(start);
	    c_end.setTime(end);
	    c_end.add(Calendar.DAY_OF_YEAR, 1);//这样可以输出截止时间
		
		List<String> list = new ArrayList<String>();
		int week = 0;
		if("week".equalsIgnoreCase(timeType)){
			while(c_begin.before(c_end)){
				if(c_begin.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {
					week = c_begin.get(Calendar.WEEK_OF_YEAR);
					list.add(c_begin.get(Calendar.YEAR)+"-"+(week<10?("0"+week):week));
				}
				c_begin.add(Calendar.DAY_OF_YEAR, 1);
			}
		}else if("month".equalsIgnoreCase(timeType)){
			while(c_begin.before(c_end)){
				if(!list.contains(c_begin.get(Calendar.YEAR)+"-"+c_begin.get(Calendar.MONTH))){
					list.add(c_begin.get(Calendar.YEAR)+"-"+c_begin.get(Calendar.MONTH));
				}
				c_begin.add(Calendar.DAY_OF_YEAR, 1);
			}
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<list.size();i++){
				sb.delete(0, sb.length());
				sb.append(list.get(i));
				if(Integer.parseInt(sb.substring(5))<9){
					list.set(i, sb.substring(0, 5)+"0"+(Integer.parseInt(sb.substring(sb.length()-1))+1));
				}else if(Integer.parseInt(sb.substring(5))==9){
					list.set(i, sb.substring(0, 5)+(Integer.parseInt(sb.substring(sb.length()-1))+1));
				}else{
					list.set(i, sb.substring(0, 6)+(Integer.parseInt(sb.substring(sb.length()-1))+1));
				}
			}
		}else{
			//计算每一天
		    while(c_begin.before(c_end)){
//		    	System.out.println(sdf.format(c_begin.getTime()));
		    	list.add(sdf.format(c_begin.getTime()));//每一天
		    	c_begin.add(Calendar.DAY_OF_YEAR, 1);
		    }
		}
		
		return list;
	}
	/**
	 * 
	 * 2013-9-25
	 * 朱爱林
	 * @param string  week,month
	 * @param startTime 获取当天所在周/月的第一天 
	 * @param endTime 获取当天所在周/月的最后一天
	 * @throws ParseException 
	 */
	private String[] dateformatUtil(String type, String startTime, String endTime) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String[] ss = new String[2];
		if("week".equals(type)){
			if(!"".equals(startTime)){
				Date dd = sdf.parse(startTime);
				c.setTime(dd);
				int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
				if(dayofweek == 0){
					dayofweek = 7;
				}
				c.add(Calendar.DATE, -dayofweek + 1);
				System.out.println(sdf.format(c.getTime()));
				startTime = sdf.format(c.getTime());
				ss[0] = startTime;
			}
			if(!"".equals(endTime)){
				Date dd = sdf.parse(endTime);
				c.setTime(dd);
				int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
				if(dayofweek == 0){
					dayofweek = 7;
				}
				c.add(Calendar.DATE, -dayofweek + 7);
				endTime = sdf.format(c.getTime());
				ss[1] = endTime;
			}
		}else if("month".equals(type)){
			if(!"".equals(startTime)){
				Date dd = sdf.parse(startTime);
				c.setTime(dd);
				int days = c.getActualMinimum(Calendar.DAY_OF_MONTH);
				c.set(Calendar.DAY_OF_MONTH, days);
				startTime = sdf.format(c.getTime());
				ss[0] = startTime;
			}
			if(!"".equals(endTime)){
				Date dd = sdf.parse(endTime);
				c.setTime(dd);
				int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				c.set(Calendar.DAY_OF_MONTH, days);
				startTime = sdf.format(c.getTime());
				ss[1] = endTime;
			}
		}
		return ss;
	}
	
}
