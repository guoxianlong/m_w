package mmb.rec.oper.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.stat.OrderStockTimelyBean;
import mmb.stock.stat.StatService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/OrderStockTimelyController")
public class OrderStockTimelyController {
	/**
	 * 说明:导出发货成功率统计明细
	 * @param easyuiPage 分页bean
	 * @param orderStockCount 申请次数
	 * @param searchType 查看类型
	 * @param orderType 订单状态
	 * @param xid x轴id
	 * @param date 搜索日期
	 * @param orderCode 搜索订单号
	 * @author syuf
	 */
	@RequestMapping("/excelSucDetail")
	public String excelSucDetail (HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiPage,String orderStockCount,
			String searchType,String orderType,String xid,String date,String orderCode) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if(searchType == null || "null".equals(searchType)){
				searchType = "";
			}
			if(orderType == null || "null".equals(orderType)){
				orderType = "";
			}
			if(xid == null || "null".equals(xid)){
				xid = "";
			}
			if(date == null || "null".equals(date)){
				date = "";
			}
			if(orderCode == null || "null".equals(orderCode)){
				orderCode = "";
			}
			if(orderStockCount == null || "null".equals(orderStockCount)){
				orderStockCount = "";
			}
			if((!orderType.equals(""))||(!date.equals(""))||(!orderCode.equals(""))){//输入了查询条件，查询相应的订单列表
				String condition="1=1";
				if(!orderType.equals("")){
					if(orderType.equals("1")){
						condition+=" and stock_out_user_id=0";
					}else if(orderType.equals("2")){
						condition+=" and stock_out_user_id>0";
					}else if(orderType.equals("3")){
						condition+=" and stock_out_user_id>0 and order_stock_count=1";
					}
				}
				if(!"".equals(searchType) && !"".equals(xid)){
					if("".equals(date)){
						String times[] = getDateTimes(searchType);
						date = times[StringUtil.toInt(xid)] + "";
					}
				}
				if(!date.equals("")){
					condition+=" and date>='"+date+"'";
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal=Calendar.getInstance();
					cal.setTime(dateFormat.parse(date));
					cal.add(Calendar.HOUR_OF_DAY, -6);
					cal.add(Calendar.HOUR_OF_DAY, 4);
					if(searchType.equals("day")){
						cal.add(Calendar.DAY_OF_YEAR, 1);
					}else if(searchType.equals("week")){
						cal.add(Calendar.WEEK_OF_YEAR, 1);
					}else if(searchType.equals("month")){
						cal.add(Calendar.MONTH, 1);
					}
					String date2=dateFormat.format(cal.getTime());
					condition+=" and date<'"+date2+"'";
					cal.add(Calendar.HOUR_OF_DAY, -4);
				}
				if(!orderCode.equals("")){
					condition+=" and order_code='"+orderCode+"'";
				}
				if(!orderStockCount.equals("")){
					condition+=" and order_stock_count>="+orderStockCount;
				}
				@SuppressWarnings("unchecked")
				List<OrderStockTimelyBean> orderStockTimelyList = statService.getOrderStockTimelyList(condition, -1, -1, null);
				request.setAttribute("orderStockTimelyList", orderStockTimelyList);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return "admin/orderStock/orderStockTimelyExcel";
	}
	/**
	 * 说明:导出发货及时率统计明细
	 * @param easyuiPage 分页bean
	 * @param orderStockCount 申请次数
	 * @param searchType 查看类型
	 * @param orderType 订单状态
	 * @param xid x轴id
	 * @param date 搜索日期
	 * @param orderCode 搜索订单号
	 * @author syuf
	 */
	@RequestMapping("/excelTimDetail")
	public String excelTimDetail (HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiPage,String orderStockCount,
			String searchType,String orderType,String xid,String date,String orderCode) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败!");
			return "admin/rec/err";
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if(searchType == null || "null".equals(searchType)){
				searchType = "";
			}
			if(orderType == null || "null".equals(orderType)){
				orderType = "";
			}
			if(xid == null || "null".equals(xid)){
				xid = "";
			}
			if(date == null || "null".equals(date)){
				date = "";
			}
			if(orderCode == null || "null".equals(orderCode)){
				orderCode = "";
			}
			if(orderStockCount == null || "null".equals(orderStockCount)){
				orderStockCount = "";
			}
			if((!orderType.equals(""))||(!date.equals(""))||(!orderCode.equals(""))){//输入了查询条件，查询相应的订单列表
				String condition="1=1";
				if(!orderType.equals("")){
					if(orderType.equals("1")){
						condition+=" and stock_out_user_id=0";
					}else if(orderType.equals("2")){
						condition+=" and stock_out_user_id>0 and order_stock_count>1";
					}else if(orderType.equals("3")){
						condition+=" and stock_out_user_id>0 and order_stock_count=1";
					}
				}
				if(!"".equals(searchType) && !"".equals(xid)){
					if("".equals(date)){
						String times[] = getDateTimes(searchType);
						date = times[StringUtil.toInt(xid)] + "";
					}
				}
				if(!date.equals("")){
					condition+=" and date>='"+date+"'";
					SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal=Calendar.getInstance();
					cal.setTime(sdf2.parse(date));
					cal.add(Calendar.HOUR_OF_DAY, -6);
					String time1=sdf.format(cal.getTime());//申请发货开始时间
					cal.add(Calendar.HOUR_OF_DAY, 4);
					if(searchType.equals("day")){
						cal.add(Calendar.DAY_OF_YEAR, 1);
					}else if(searchType.equals("week")){
						cal.add(Calendar.WEEK_OF_YEAR, 1);
					}else if(searchType.equals("month")){
						cal.add(Calendar.MONTH, 1);
					}
					String date2=sdf2.format(cal.getTime());
					condition+=" and date<'"+date2+"'";
					cal.add(Calendar.HOUR_OF_DAY, -4);
					String time2=sdf.format(cal.getTime());//申请发货结束时间
					condition+=" and first_order_stock_datetime>='";
					condition+=time1;
					condition+="' and first_order_stock_datetime< '";
					condition+=time2;
					condition+="'";
				}
				if(!orderCode.equals("")){
					condition+=" and order_code='"+orderCode+"'";
				}
				if(!orderStockCount.equals("")){
					condition+=" and order_stock_count>="+orderStockCount;
				}
				@SuppressWarnings("unchecked")
				List<OrderStockTimelyBean> orderStockTimelyList=statService.getOrderStockTimelyList(condition, -1, -1, null);
				request.setAttribute("orderStockTimelyList", orderStockTimelyList);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
	    return "admin/orderStock/orderStockTimelyExcel";
	}
	/**
	 * @param easyuiPage 分页bean
	 * @param orderStockCount 申请发货次数
	 * @param searchType 查看类型
	 * @param orderType	订单状态 
	 * @param xid	x轴id
	 * @param dateTime 搜索时间
	 * @param orderCode 搜索订单号
	 * @return 生成发货成功率datagrid
	 * @author syuf
	 */
	@RequestMapping("/getTimelyDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getTimelyDatagrid (HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiPage,String orderStockCount,
			String searchType,String orderType,String xid,String dateTime,String orderCode) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if(searchType == null || "null".equals(searchType)){
				searchType = "";
			}
			if(orderType == null || "null".equals(orderType)){
				orderType = "";
			}
			if(xid == null || "null".equals(xid)){
				xid = "";
			}
			if(dateTime == null || "null".equals(dateTime)){
				dateTime = "";
			}
			if(orderCode == null || "null".equals(orderCode)){
				orderCode = "";
			}
			if(orderStockCount == null || "null".equals(orderStockCount)){
				orderStockCount = "";
			}
			if(!"".equals(orderType) || !"".equals(dateTime) || !"".equals(orderCode) || !"".equals(searchType) || !"".equals(orderStockCount)){
				SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
				String date = null;
				String condition="1=1";
				if(!"".equals(orderType)){
					if(orderType.equals("1")){
						condition+=" and stock_out_user_id=0";
					}else if(orderType.equals("2")){
						condition+=" and stock_out_user_id>0 and order_stock_count>1";
					}else if(orderType.equals("3")){
						condition+=" and stock_out_user_id>0 and order_stock_count=1";
					}
				}
				if(!"".equals(orderStockCount)){
					condition+=" and order_stock_count>="+ orderStockCount;
				}
				if(!"".equals(orderCode)){
					condition+=" and order_code='"+ StringUtil.toSql(orderCode) + "'";
				}
				if(!"".equals(dateTime)){
					date = dateTime;
				}else if(!"".equals(searchType)&& !"".equals(xid)){
					String times[] = getDateTimes(searchType);
					date = times[StringUtil.toInt(xid)] + "";
				}
				Calendar tempCal=Calendar.getInstance();//开始时间
				tempCal.setTime(timeFormat.parse(date+" 00:00:00"));
				tempCal.add(Calendar.HOUR, -6);
				
				Calendar tempCal2=Calendar.getInstance();//结束时间
				tempCal2.setTime(timeFormat.parse(date+" 00:00:00"));
				if(!"".equals(dateTime)){
					tempCal2.add(Calendar.DAY_OF_YEAR, 1);
				}else{
					if(searchType.equals("day")){
						tempCal2.add(Calendar.DAY_OF_YEAR, 1);
					}else if(searchType.equals("week")){
						tempCal2.add(Calendar.WEEK_OF_YEAR, 1);
					}else if(searchType.equals("month")){
						tempCal2.add(Calendar.MONTH, 1);
					}
				}
				List<OrderStockTimelyBean> orderStockTimelyList=new ArrayList<OrderStockTimelyBean>();
				while(tempCal.before(tempCal2)){
					String time1=timeFormat.format(tempCal.getTime());//昨天18点
					tempCal.add(Calendar.DAY_OF_YEAR, 1);
					String time2=timeFormat.format(tempCal.getTime());//今天18点
					
					@SuppressWarnings("unchecked")
					List<OrderStockTimelyBean> tempList=statService.getOrderStockTimelyList(condition+" and date='"+dateFormat.format(tempCal.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"'", -1, -1, null);
					orderStockTimelyList.addAll(tempList);
					
					tempCal.add(Calendar.DAY_OF_YEAR, 1);
				}
				datagridJson.setRows(orderStockTimelyList);
				datagridJson.setFooter(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datagridJson;
	}
	/**
	 * 说明:获取及时率统计的申请多次发货订单
	 * @param request
	 * @param searchType 查看类型
	 * @author syuf 
	 */
	@RequestMapping("/getTimelySendApplys")
	@ResponseBody
	public int[] getTimelySendApplys (HttpServletRequest request,HttpServletResponse response, String searchType) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		int datas[] = new int[31];
		try {
			String times[] = getDateTimes(searchType);
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			//查询每个时间段内的当日未发货订单数，当日已发货订单数
			for(int i=0;i<times.length-1;i++){
				int stockOutCount = 0;//当日未发货的订单数
				
				String startDate = times[i];//开始日期
				String endDate = times[i+1];//结束日期
				Calendar startTime = Calendar.getInstance();//开始日期0点
				startTime.setTime(timeFormat.parse(startDate+" 00:00:00"));
				Calendar endTime = Calendar.getInstance();//结束日期0点
				endTime.setTime(timeFormat.parse(endDate+" 00:00:00"));
				
				startTime.add(Calendar.HOUR, -6);//昨天19点
				endTime.add(Calendar.HOUR, -6);
				while(startTime.before(endTime)){
					String time1 = timeFormat.format(startTime.getTime());
					startTime.add(Calendar.DAY_OF_YEAR, 1);
					String time2 = timeFormat.format(startTime.getTime());//今天18点
					//当日已发货的订单数，申请多次
					int tempStockOutCount=statService.getOrderStockTimelyCount("date='"+dateFormat.format(startTime.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"' and stock_out_user_id>0 and order_stock_count>1");
					stockOutCount+=tempStockOutCount;
				}
				datas[i]=stockOutCount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datas;
	}
	/**
	 * 说明:获取及时率统计的申请一次发货订单
	 * @param request
	 * @param searchType 查看类型
	 * @author syuf
	 */
	@RequestMapping("/getTimelySendApply")
	@ResponseBody
	public int[] getTimelySendApply (HttpServletRequest request,HttpServletResponse response,String searchType) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		int datas[] = new int[31];
		try {
			String times[] = getDateTimes(searchType);
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			//查询每个时间段内的当日未发货订单数，当日已发货订单数
			for(int i=0;i<times.length-1;i++){
				int stockOutCount = 0;//当日未发货的订单数
				
				String startDate = times[i];//开始日期
				String endDate = times[i+1];//结束日期
				Calendar startTime = Calendar.getInstance();//开始日期0点
				startTime.setTime(timeFormat.parse(startDate+" 00:00:00"));
				Calendar endTime = Calendar.getInstance();//结束日期0点
				endTime.setTime(timeFormat.parse(endDate+" 00:00:00"));
				
				startTime.add(Calendar.HOUR, -6);//昨天19点
				endTime.add(Calendar.HOUR, -6);
				while(startTime.before(endTime)){
					String time1 = timeFormat.format(startTime.getTime());
					startTime.add(Calendar.DAY_OF_YEAR, 1);
					String time2 = timeFormat.format(startTime.getTime());//今天18点
					//当日已发货的订单数，申请一次
					int tempStockOutCount = statService.getOrderStockTimelyCount("date='"+dateFormat.format(startTime.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"' and stock_out_user_id>0 and order_stock_count=1");
					stockOutCount+=tempStockOutCount;
				}
				datas[i]=stockOutCount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datas;
	}
	/**
	 * 说明:获取及时率统计的未发货订单
	 * @param request
	 * @param searchType 查看类型
	 * @author syuf
	 */
	@RequestMapping("/getTimelySendNo")
	@ResponseBody
	public int[] getTimelySendNo (HttpServletRequest request,HttpServletResponse response, String searchType) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		int datas[] = new int[31];
		try {
			String times[] = getDateTimes(searchType);
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			//查询每个时间段内的当日未发货订单数，当日已发货订单数
			for(int i=0;i<times.length-1;i++){
				int noStockOutCount = 0;//当日未发货的订单数
				
				String startDate = times[i];//开始日期
				String endDate = times[i+1];//结束日期
				Calendar startTime = Calendar.getInstance();//开始日期0点
				startTime.setTime(timeFormat.parse(startDate+" 00:00:00"));
				Calendar endTime = Calendar.getInstance();//结束日期0点
				endTime.setTime(timeFormat.parse(endDate+" 00:00:00"));
				
				startTime.add(Calendar.HOUR, -6);//昨天19点
				endTime.add(Calendar.HOUR, -6);
				while(startTime.before(endTime)){
					String time1 = timeFormat.format(startTime.getTime());
					startTime.add(Calendar.DAY_OF_YEAR, 1);
					String time2 = timeFormat.format(startTime.getTime());//今天18点
					//当日未发货的订单数
					int tempNoStockOutCount=statService.getOrderStockTimelyCount("date='"+dateFormat.format(startTime.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"' and stock_out_user_id=0");
					noStockOutCount+=tempNoStockOutCount;
				}
				datas[i]=noStockOutCount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datas;
	}
	/**
	 * 说明:初始化按条件搜索的数据表格
	 * @param request
	 * @param searchType 查看类型
	 * @param type 订单搜索类型
	 * @param id x轴id
	 * @param dataTime 时间
	 * @param easyuiPage 分页bean
	 * @param orderCode 订单编号
	 * @author syuf
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getSucDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getSucDatagrid (HttpServletRequest request,HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String searchType,String orderType,String xid,String dateTime,String orderCode) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if(searchType == null || "null".equals(searchType)){
				searchType = "";
			}
			if(orderType == null || "null".equals(orderType)){
				orderType = "";
			}
			if(xid == null || "null".equals(xid)){
				xid = "";
			}
			if(dateTime == null || "null".equals(dateTime)){
				dateTime = "";
			}
			if(orderCode == null || "null".equals(orderCode)){
				orderCode = "";
			}
			if(!"".equals(orderType) || !"".equals(dateTime) || !"".equals(orderCode) || !"".equals(searchType)){
				SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
				String condition="1=1";
				if(!"".equals(orderType)){
					if("1".equals(orderType)){
						condition+=" and stock_out_user_id=0";
					}else if("2".equals(orderType)){
						condition+=" and stock_out_user_id>0";
					}
				}
				if(!"".equals(orderCode)){
					condition+=" and order_code='"+orderCode+"'";
				}
				Calendar startTime=Calendar.getInstance();//开始时间
				Calendar endTime=Calendar.getInstance();//结束时间
				if(!"".equals(searchType)&& !"".equals(xid)){
					String times[] = getDateTimes(searchType);
					String date = times[StringUtil.toInt(xid)] + "";
					startTime.setTime(timeFormat.parse(date+" 00:00:00"));
					endTime.setTime(timeFormat.parse(date+" 00:00:00"));
					if("day".equals(searchType)){
						endTime.add(Calendar.DAY_OF_YEAR, 1);
					}else if("week".equals(searchType)){
						endTime.add(Calendar.WEEK_OF_YEAR, 1);
					}else if("month".equals(searchType)){
						endTime.add(Calendar.MONTH, 1);
					}
					condition+=" and date>='"+dateFormat.format(startTime.getTime())+"' and date<'"+dateFormat.format(endTime.getTime())+"'";
				}
				if(!"".equals(dateTime)){
					startTime.setTime(timeFormat.parse(dateTime+" 00:00:00"));
					endTime.setTime(timeFormat.parse(dateTime+" 00:00:00"));
					endTime.add(Calendar.DAY_OF_YEAR, 1);
					condition+=" and date>='"+dateFormat.format(startTime.getTime())+"' and date<'"+dateFormat.format(endTime.getTime())+"'";
					
				}
				int totalCount = statService.getOrderStockTimelyCount(condition);
				datagridJson.setTotal(Long.parseLong(totalCount+""));
				List<OrderStockTimelyBean> orderStockTimelyList = statService.getOrderStockTimelyList(condition,(easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), null);
				datagridJson.setRows(orderStockTimelyList);
				datagridJson.setFooter(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datagridJson;
	}
	/**
	 * 说明:获取应发出数据
	 * @param request
	 * @param searchType 查看类型
	 * @author syuf
	 */
	@RequestMapping("/getSucSendNo")
	@ResponseBody
	public int[] getSucSendNo (HttpServletRequest request,HttpServletResponse response, String searchType) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		int datas[] = new int[31];
		try {
			String times[] = getDateTimes(searchType);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//查询每个时间段内的当日应发货订单数，当日实际发货订单数
			for(int i=0;i<times.length-1;i++){
				int stockOutCount=0;//当日应发货数
				
				String startTime=times[i];//开始日期
				String endTime=times[i+1];//结束日期
				
				Calendar cal1=Calendar.getInstance();
				cal1.setTime(sdf.parse(startTime+" 00:00:00"));
				cal1.add(Calendar.HOUR, -6);
				Calendar cal2=Calendar.getInstance();
				cal2.setTime(sdf.parse(endTime+" 00:00:00"));
				cal2.add(Calendar.HOUR, -6);
				
				
				stockOutCount=statService.getOrderStockTimelyCount("date >= '"+startTime+"' and date < '"+endTime+"' and stock_out_user_id=0");
				datas[i]=stockOutCount;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datas;
	}
	/**
	 * 说明:获取实际发出数据
	 * @param request
	 * @param searchType 查看类型
	 * @author syuf
	 */
	@RequestMapping("/getSucSendOk")
	@ResponseBody
	public int[] getSucSendOk(HttpServletRequest request,HttpServletResponse response,String searchType) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("msg", "没有权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		int datas[] = new int[31];
		try {
			String times[] = getDateTimes(searchType);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//查询每个时间段内的当日应发货订单数，当日实际发货订单数
			for(int i=0;i<times.length-1;i++){
				int stockOutCount=0;//当日实际发货数
				String startTime=times[i];//开始日期
				String endTime=times[i+1];//结束日期
				
				Calendar cal1=Calendar.getInstance();
				cal1.setTime(sdf.parse(startTime+" 00:00:00"));
				cal1.add(Calendar.HOUR, -6);
				Calendar cal2=Calendar.getInstance();
				cal2.setTime(sdf.parse(endTime+" 00:00:00"));
				cal2.add(Calendar.HOUR, -6);
				stockOutCount=statService.getOrderStockTimelyCount("date >= '"+startTime+"' and date < '"+endTime+"' and stock_out_user_id>0");
				datas[i] = stockOutCount;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datas;
	}
	/**
	 * 说明：根据查看类型和x轴id获取当前点击的日期 
	 * @param request
	 * @param searchType 查看类型
	 * @param xid x轴id
	 * @author syuf
	 */
	@RequestMapping("/getDateTime")
	@ResponseBody
	public String[] getDateTime (HttpServletRequest request,String searchType,String xid){
		String time[] = new String[1];
		time[0] = getDateTimes(searchType)[StringUtil.toInt(xid)];
		return time;
	}
	/**
	 * 说明：根据产看类型初始化x轴数据
	 * @param request
	 * @param searchType 查看类型
	 * @author syuf
	 */
	@RequestMapping("/getTimes")
	@ResponseBody
	public String[] getTimes (HttpServletRequest request,String searchType){
		String times[] = new String[31];
		try {
			String temp[] = getDateTimes(searchType);
			for(int i=0;i<times.length;i++){
				times[i] = temp[i].substring(5,10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return times;
	}
	/**
	 * 说明：按查看类型获取时间数组
	 * @param searchType 查看类型
	 * @author syuf
	 */
	public String[] getDateTimes(String searchType){
		String times[] = new String[31];
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if("day".equals(searchType)){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, -(times.length-1));
				for(int i=0;i<times.length;i++){
					times[i]=dateFormat.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 1);
				}
			}else if("week".equals(searchType)){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)==1?6:(cal.get(Calendar.DAY_OF_WEEK)-2)));
				cal.add(Calendar.DAY_OF_YEAR, -(times.length-1)*7);
				for(int i=0;i<times.length;i++){
					times[i]=dateFormat.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 7);
				}
			}else if("month".equals(searchType)){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH,-(cal.get(Calendar.DAY_OF_MONTH)-1));
				cal.add(Calendar.MONTH, -30);
				for(int i=0;i<times.length;i++){
					times[i]=dateFormat.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.MONTH, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return times;
	}
}
