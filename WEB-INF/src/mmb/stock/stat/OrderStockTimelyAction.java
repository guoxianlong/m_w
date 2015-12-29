package mmb.stock.stat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.framework.IConstants;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class OrderStockTimelyAction  extends DispatchAction {
	
	/**
	 * 发货及时率统计
	 */
	public ActionForward orderStockTimely(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("tip", "没有权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String searchType=StringUtil.convertNull(request.getParameter("searchType"));//统计周期，1是按天，2按周，3按月
		String pageType=StringUtil.convertNull(request.getParameter("pageType"));//1是发货成功率统计，2是发货及时率统计
		
		String orderType=StringUtil.convertNull(request.getParameter("orderType"));//订单状态，-1是全部，1是待发货，2是已复核
		String date=StringUtil.convertNull(request.getParameter("date"));//查询的时间
		String orderCode=StringUtil.convertNull(request.getParameter("orderCode"));//订单号
		String orderStockCount=StringUtil.convertNull(request.getParameter("orderStockCount"));//申请发货次数
		//String orderStockCount=StringUtil.convertNull(request.getParameter("orderStockCount"));//订单申请出库数量
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			//按照searchType确定30个时间段
			String[][] dateList=new String[31][4];//时间，未发货订单数，已发货订单数，发货频次
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
			if(searchType.equals("1")){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, -(dateList.length-1));
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 1);
				}
			}else if(searchType.equals("2")){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)==1?6:(cal.get(Calendar.DAY_OF_WEEK)-2)));
				cal.add(Calendar.DAY_OF_YEAR, -(dateList.length-1)*7);
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 7);
				}
			}else if(searchType.equals("3")){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH,-(cal.get(Calendar.DAY_OF_MONTH)-1));
				cal.add(Calendar.MONTH, -30);
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.MONTH, 1);
				}
			}
			
//			//查询每个时间段内的当日未发货订单数，当日已发货订单数
			for(int i=0;i<dateList.length-1;i++){
				int noStockOutCount=0;//当日未发货的订单数
				int stockOutCount=0;//当日已发货的订单数，申请多次
				int stockOutCount2=0;//当日已发货的订单数，申请一次
				
				String tempDate=dateList[i][0];//开始日期
				String tempDate2=dateList[i+1][0];//结束日期
				Calendar tempCal=Calendar.getInstance();//开始日期0点
				tempCal.setTime(sdf.parse(tempDate+" 00:00:00"));
				Calendar tempCal2=Calendar.getInstance();//结束日期0点
				tempCal2.setTime(sdf.parse(tempDate2+" 00:00:00"));
				
				tempCal.add(Calendar.HOUR, -6);//昨天19点
				tempCal2.add(Calendar.HOUR, -6);
				while(tempCal.before(tempCal2)){
					String time1=sdf.format(tempCal.getTime());
					tempCal.add(Calendar.DAY_OF_YEAR, 1);
					String time2=sdf.format(tempCal.getTime());//今天18点
					
					//当日未发货的订单数
					int tempNoStockOutCount=statService.getOrderStockTimelyCount("date='"+sdf2.format(tempCal.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"' and stock_out_user_id=0");
					noStockOutCount+=tempNoStockOutCount;
					//当日已发货的订单数，申请多次
					int tempStockOutCount=statService.getOrderStockTimelyCount("date='"+sdf2.format(tempCal.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"' and stock_out_user_id>0 and order_stock_count>1");
					stockOutCount+=tempStockOutCount;
					//当日已发货的订单数，申请一次
					int tempStockOutCount2=statService.getOrderStockTimelyCount("date='"+sdf2.format(tempCal.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"' and stock_out_user_id>0 and order_stock_count=1");
					stockOutCount2+=tempStockOutCount2;
				}
				dateList[i][1]=noStockOutCount+"";
				dateList[i][2]=stockOutCount+"";
				dateList[i][3]=stockOutCount2+"";
			}
			request.setAttribute("dateList", dateList);
			
			if((!orderType.equals("")&&!orderType.equals("-1"))||(!date.equals(""))||(!orderCode.equals(""))||!(orderStockCount.equals(""))){//输入了查询条件，查询相应的订单列表
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
				if(!orderStockCount.equals("")){
					condition+=" and order_stock_count>="+orderStockCount;
				}
				if(!orderCode.equals("")){
					condition+=" and order_code='"+orderCode+"'";
				}
				
				String search=request.getParameter("search");//如果为search，表示是点击查询，只查一天的数据
				Calendar tempCal=Calendar.getInstance();//开始时间
				tempCal.setTime(sdf.parse(date+" 00:00:00"));
				tempCal.add(Calendar.HOUR, -6);
				
				Calendar tempCal2=Calendar.getInstance();//结束时间
				tempCal2.setTime(sdf.parse(date+" 00:00:00"));
				if(search!=null&&search.equals("search")){
					tempCal2.add(Calendar.DAY_OF_YEAR, 1);
				}else{
					if(searchType.equals("1")){
						tempCal2.add(Calendar.DAY_OF_YEAR, 1);
					}else if(searchType.equals("2")){
						tempCal2.add(Calendar.WEEK_OF_YEAR, 1);
					}else if(searchType.equals("3")){
						tempCal2.add(Calendar.MONTH, 1);
					}
				}
				List orderStockTimelyList=new ArrayList();
				while(tempCal.before(tempCal2)){
					String time1=sdf.format(tempCal.getTime());//昨天18点
					tempCal.add(Calendar.DAY_OF_YEAR, 1);
					String time2=sdf.format(tempCal.getTime());//今天18点
					
					List tempList=statService.getOrderStockTimelyList(condition+" and date='"+sdf2.format(tempCal.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"'", -1, -1, null);
					orderStockTimelyList.addAll(tempList);
					
					tempCal.add(Calendar.DAY_OF_YEAR, 1);
				}
				request.setAttribute("orderStockTimelyList", orderStockTimelyList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
	    return mapping.findForward("orderStockTimely");
	}
	
	
	
	/**
	 * 发货成功率统计
	 */
	public ActionForward orderStockTimely2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("tip", "没有权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String searchType=StringUtil.convertNull(request.getParameter("searchType"));//统计周期，1是按天，2按周，3按月
		String pageType=StringUtil.convertNull(request.getParameter("pageType"));//1是发货成功率统计，2是发货及时率统计
		
		String orderType=StringUtil.convertNull(request.getParameter("orderType"));//订单状态，-1是全部，1是待发货，2是已复核
		String date=StringUtil.convertNull(request.getParameter("date"));//查询的时间
		String orderCode=StringUtil.convertNull(request.getParameter("orderCode"));//订单号
		//String orderStockCount=StringUtil.convertNull(request.getParameter("orderStockCount"));//订单申请出库数量
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		try {
			//按照searchType确定30个时间段
			String[][] dateList=new String[31][4];//时间，未发货订单数，已发货订单数，发货频次
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
			if(searchType.equals("1")){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, -(dateList.length-1));
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 1);
				}
			}else if(searchType.equals("2")){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)==1?6:(cal.get(Calendar.DAY_OF_WEEK)-2)));
				cal.add(Calendar.DAY_OF_YEAR, -(dateList.length-1)*7);
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 7);
				}
			}else if(searchType.equals("3")){
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH,-(cal.get(Calendar.DAY_OF_MONTH)-1));
				cal.add(Calendar.MONTH, -30);
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.MONTH, 1);
				}
			}
			
//			//查询每个时间段内的当日应发货订单数，当日实际发货订单数
			for(int i=0;i<dateList.length-1;i++){
				int stockOutCount1=0;//当日实际发货数
				int stockOutCount2=0;//当日应发货数
				
				String tempDate=dateList[i][0];//开始日期
				String tempDate2=dateList[i+1][0];//结束日期
				
				Calendar cal1=Calendar.getInstance();
				cal1.setTime(sdf.parse(tempDate+" 00:00:00"));
				cal1.add(Calendar.HOUR, -6);
				Calendar cal2=Calendar.getInstance();
				cal2.setTime(sdf.parse(tempDate2+" 00:00:00"));
				cal2.add(Calendar.HOUR, -6);
				
//				int tempStockOutCount1=statService.getOrderStockTimelyCount("date >= '"+tempDate+"' and date < '"+tempDate2
//						+"' and first_order_stock_datetime>'"+sdf.format(cal1.getTime())+"' and first_order_stock_datetime<'"
//						+sdf.format(cal2.getTime())+"'");
//				stockOutCount1+=tempStockOutCount1;
//				
//				int tempStockOutCount2=statService.getOrderStockTimelyCount("date='"+sdf2.format(cal1.getTime())+"' and stock_out_user_id=0");
//				stockOutCount1+=tempStockOutCount2;
//				
//				int tempStockOutCount3=statService.getOrderStockTimelyCount("date >='"+tempDate+"' and date <'"+tempDate2
//						+"' and stock_out_datetime>'"+tempDate+" 08:00:00' and stock_out_datetime<'"+tempDate2+" 08:00:00'");
//				stockOutCount2+=tempStockOutCount3;
				
				stockOutCount1=statService.getOrderStockTimelyCount("date >= '"+tempDate+"' and date < '"+tempDate2+"' and stock_out_user_id>0");
				stockOutCount2=statService.getOrderStockTimelyCount("date >= '"+tempDate+"' and date < '"+tempDate2+"' and stock_out_user_id=0");
				dateList[i][1]=stockOutCount1+"";
				dateList[i][2]=stockOutCount2+"";
			}
			request.setAttribute("dateList", dateList);
			
			if((!orderType.equals("")&&!orderType.equals("-1"))||(!date.equals(""))||(!orderCode.equals(""))){//输入了查询条件，查询相应的订单列表
				String condition="1=1";
				
				if(!orderType.equals("")){
					if(orderType.equals("1")){
						condition+=" and stock_out_user_id=0";
					}else if(orderType.equals("2")){
						condition+=" and stock_out_user_id>0";
					}
				}
				if(!orderCode.equals("")){
					condition+=" and order_code='"+orderCode+"'";
				}
				
				String search=request.getParameter("search");//如果为search，表示是点击查询，只查一天的数据
				Calendar tempCal=Calendar.getInstance();//开始时间
				tempCal.setTime(sdf.parse(date+" 00:00:00"));
				//tempCal.add(Calendar.HOUR, -5);
				
				Calendar tempCal2=Calendar.getInstance();//结束时间
				tempCal2.setTime(sdf.parse(date+" 00:00:00"));
				if(search!=null&&search.equals("search")){
					tempCal2.add(Calendar.DAY_OF_YEAR, 1);
				}else{
					if(searchType.equals("1")){
						tempCal2.add(Calendar.DAY_OF_YEAR, 1);
					}else if(searchType.equals("2")){
						tempCal2.add(Calendar.WEEK_OF_YEAR, 1);
					}else if(searchType.equals("3")){
						tempCal2.add(Calendar.MONTH, 1);
					}
				}
				List orderStockTimelyList=new ArrayList();
//				while(tempCal.before(tempCal2)){
//					String time1=sdf.format(tempCal.getTime());//昨天18点
//					tempCal.add(Calendar.DAY_OF_YEAR, 1);
//					String time2=sdf.format(tempCal.getTime());//今天18点
//					String a=condition+" and date='"+sdf2.format(tempCal.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"'";
//					List tempList=statService.getOrderStockTimelyList(condition+" and date='"+sdf2.format(tempCal.getTime())+"' and first_order_stock_datetime>='"+time1+"' and first_order_stock_datetime<'"+time2+"'", -1, -1, null);
//					orderStockTimelyList.addAll(tempList);
//					
//					tempCal.add(Calendar.DAY_OF_YEAR, 1);
//				}
				condition+=" and date>='"+sdf2.format(tempCal.getTime())+"' and date<'"+sdf2.format(tempCal2.getTime())+"'";
				orderStockTimelyList=statService.getOrderStockTimelyList(condition, -1, -1, null);
				request.setAttribute("orderStockTimelyList", orderStockTimelyList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
	    return mapping.findForward("orderStockTimely");
	}
	
	
	/**
	 * 发货及时率统计
	 */
	public ActionForward orderStockTimelyExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(578)){
			request.setAttribute("tip", "没有权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String searchType=StringUtil.convertNull(request.getParameter("searchType"));//统计周期，1是按天，2按周，3按月
		String pageType=StringUtil.convertNull(request.getParameter("pageType"));//1是发货成功率统计，2是发货及时率统计
		
		String orderType=StringUtil.convertNull(request.getParameter("orderType"));//订单状态，-1是全部，1是待发货，2是已复核
		String date=StringUtil.convertNull(request.getParameter("date"));//查询的时间
		String orderCode=StringUtil.convertNull(request.getParameter("orderCode"));//订单号
		String orderStockCount=StringUtil.convertNull(request.getParameter("orderStockCount"));//订单申请出库数量
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		try {
			if((!orderType.equals(""))||(!date.equals(""))||(!orderCode.equals(""))){//输入了查询条件，查询相应的订单列表
				String condition="1=1";
				if(!orderType.equals("")){
					if(orderType.equals("1")){
						condition+=" and stock_out_user_id=0";
					}else if(orderType.equals("2")){
						if(pageType.equals("1")){
							condition+=" and stock_out_user_id>0";
						}else if(pageType.equals("2")){
							condition+=" and stock_out_user_id>0 and order_stock_count>1";
						}
					}else if(orderType.equals("3")){
						condition+=" and stock_out_user_id>0 and order_stock_count=1";
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
					if(searchType.equals("1")){
						cal.add(Calendar.DAY_OF_YEAR, 1);
					}else if(searchType.equals("2")){
						cal.add(Calendar.WEEK_OF_YEAR, 1);
					}else if(searchType.equals("3")){
						cal.add(Calendar.MONTH, 1);
					}
					String date2=sdf2.format(cal.getTime());
					condition+=" and date<'"+date2+"'";
					cal.add(Calendar.HOUR_OF_DAY, -4);
					String time2=sdf.format(cal.getTime());//申请发货结束时间
					if(pageType.equals("2")){//及时率统计
						condition+=" and first_order_stock_datetime>='";
						condition+=time1;
						condition+="' and first_order_stock_datetime< '";
						condition+=time2;
						condition+="'";
					}
				}
				if(!orderCode.equals("")){
					condition+=" and order_code='"+orderCode+"'";
				}
				if(!orderStockCount.equals("")){
					condition+=" and order_stock_count>="+orderStockCount;
				}
				List orderStockTimelyList=statService.getOrderStockTimelyList(condition, -1, -1, null);
				request.setAttribute("orderStockTimelyList", orderStockTimelyList);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
	    return mapping.findForward("orderStockTimelyExcel");
	}
	
	
}
