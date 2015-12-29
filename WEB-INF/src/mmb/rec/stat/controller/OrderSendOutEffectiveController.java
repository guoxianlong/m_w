package mmb.rec.stat.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.stat.bean.TempOrderEffectiveInfoBean;
import mmb.rec.stat.bean.WareSendOutDurationBean;
import mmb.rec.stat.bean.WareSendOutEffectiveBean;
import mmb.rec.stat.service.OrderSendOutEffectiveService;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.framework.IConstants;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


@Controller
@RequestMapping("admin/stat")
public class OrderSendOutEffectiveController {
	
	@RequestMapping("orderSendOutStatistic")
	public String getOrderSendOutStatistic ( HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		/*if(!group.isFlag(111)){
			request.setAttribute("tip", "没有权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}*/
		String[] productLines = request.getParameterValues("productLines");//产品线
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));//地区
		String startDate=StringUtil.convertNull(request.getParameter("startDate"));//查询的时间开始
		String endDate=StringUtil.convertNull(request.getParameter("endDate"));//查询的时间结束
		String searchType=StringUtil.convertNull(request.getParameter("searchType"));//统计周期，1是按天，2按周，3按月
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		OrderSendOutEffectiveService orderSendOutEffectiveService = new OrderSendOutEffectiveService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
			if( startDate == null || startDate.equals("") ) {
				startDate = DateUtil.getBeforOneDay();
			}
			if( endDate == null || endDate.equals("") ) {
				endDate = DateUtil.getBeforOneDay();
			}
			int com = orderSendOutEffectiveService.compareDate(startDate, endDate);
			if( productLines == null || productLines.length == 0 ) {
				productLines = new String[1];
				productLines[0] = "-1";
			}
			if( com < 0 ) {
				request.setAttribute("tip", "开始日期需要大于等于结束日期！");
				return "/admin/error";
			} 
			if( searchType == null || searchType.equals("") ) {
				searchType = "1";
			}
			int during = DateUtil.getDaySub(startDate, endDate);
			int weekDuring = orderSendOutEffectiveService.getWeekDuring(during);
			int monthDuring = DateUtil.getMonthSub(startDate, endDate);
			during += 1;
			weekDuring += 1;
			monthDuring += 1;
			//按照searchType确定30个时间段
			String[][] dateList=new String[31][15];//时间，未发货订单数，已发货订单数，发货频次
			Date sDate = sdf2.parse(startDate);
			if(searchType.equals("1")){
				dateList = new String[during+1][15];
				Calendar cal=Calendar.getInstance();
				cal.setTime(sDate);
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 1);
				}
			}else if(searchType.equals("2")){
				dateList = new String[weekDuring + 1][15];
				Calendar cal=Calendar.getInstance();
				cal.setTime(sDate);
				cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)==1?6:(cal.get(Calendar.DAY_OF_WEEK)-2)));
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.DAY_OF_YEAR, 7);
				}
			}else if(searchType.equals("3")){
				dateList = new String[monthDuring + 1][15];
				Calendar cal=Calendar.getInstance();
				cal.setTime(sDate);
				cal.add(Calendar.DAY_OF_MONTH,-(cal.get(Calendar.DAY_OF_MONTH)-1));
				for(int i=0;i<dateList.length;i++){
					dateList[i][0]=sdf.format(cal.getTime()).substring(0,10);
					cal.add(Calendar.MONTH, 1);
				}
			}
			
			/*for( int i = 0; i < dateList.length ; i++ ) {
				String dd = dateList[i][0];
				System.out.println((i+1)+".  "+dd);
			}*/
			StringBuilder sql = new StringBuilder();
			boolean hasAll = false;
			String productLineString = "";
			for( int i = 0 ; i <  productLines.length; i++ ) {
				String temp = productLines[i];
				if( temp.equals("-1") ) {
					hasAll = true;
					break;
				}
				if( i == (productLines.length-1) ) {
					productLineString += temp;
				} else {
					productLineString += temp + ",";
				}
			}
			//是否是查全部产品线
			if( hasAll ) {
				
			} else {
				sql.append("product_line_id in (" + productLineString + ")");
			}
			if( wareArea == -1 ) {
				
			} else {
				if( sql.length() > 0 ) {
					sql.append(" and area = " + wareArea);
				} else {
					sql.append("area =" + wareArea);
				}
			}
			for(int i=0;i<dateList.length-1;i++){
				String tempDate=dateList[i][0];//开始日期
				String tempDate2=dateList[i+1][0];//结束日期
				String sqlCondition = "";
				if( sql.length() > 0 ) {
					sqlCondition = sql.toString() + " and date >= '"+tempDate+"' and date < '"+tempDate2+"'";
				} else {
					sqlCondition = sql.toString() + "date >= '"+tempDate+"' and  date < '"+tempDate2+"'";
				}
				List<WareSendOutEffectiveBean> wsoeList = orderSendOutEffectiveService.getWareSendOutEffectiveList(sqlCondition, -1, -1, null);
				List<WareSendOutDurationBean> wsodList = orderSendOutEffectiveService.getWareSendOutDurationList(sqlCondition, -1, -1, null);
				//计算3种count， 可都用一个算法
				int totalCount1 = 0;
				int totalCount2 = 0;
				int totalCount3 = 0;
				for( int j = 0; j < wsoeList.size(); j++ ) {
					WareSendOutEffectiveBean wsoeBean = wsoeList.get(j);
					totalCount1 += wsoeBean.getCount1();
					totalCount2 += wsoeBean.getCount2();
					totalCount3 += wsoeBean.getCount3();
				}
				dateList[i][1] = new Integer(totalCount1).toString();
				dateList[i][2] = new Integer(totalCount2).toString();
				dateList[i][3] = new Integer(totalCount3).toString();
				int totalTCount = totalCount1 + totalCount2 + totalCount3;
				//占比
				dateList[i][4] = orderSendOutEffectiveService.calculatePercentage(totalCount1, totalTCount);  
				dateList[i][5] = orderSendOutEffectiveService.calculatePercentage(totalCount2, totalTCount); 
				dateList[i][6] = orderSendOutEffectiveService.calculatePercentage(totalCount3, totalTCount); 
				
				Map<String,Float>  durationMap = orderSendOutEffectiveService.calculateAverageDuration(1, wsodList);
				
				dateList[i][7] = new Float (orderSendOutEffectiveService.getKeepTwoDecimal(durationMap.get("duration1"))).toString();
				dateList[i][8] = new Float (orderSendOutEffectiveService.getKeepTwoDecimal(durationMap.get("duration2"))).toString();
				dateList[i][9] = new Float (orderSendOutEffectiveService.getKeepTwoDecimal(durationMap.get("duration3"))).toString();
				dateList[i][10] = new Float (orderSendOutEffectiveService.getKeepTwoDecimal(durationMap.get("duration4"))).toString();
				float totalTDuration = durationMap.get("duration1") + durationMap.get("duration2") + durationMap.get("duration3") +durationMap.get("duration4");
				//占比
				dateList[i][11] = orderSendOutEffectiveService.calculatePercentage(durationMap.get("duration1"), totalTDuration); 
				dateList[i][12] = orderSendOutEffectiveService.calculatePercentage(durationMap.get("duration2"), totalTDuration); 
				dateList[i][13] = orderSendOutEffectiveService.calculatePercentage(durationMap.get("duration3"), totalTDuration); 
				dateList[i][14] = orderSendOutEffectiveService.calculatePercentage(durationMap.get("duration4"), totalTDuration); 
				//计算4种duration 平均值， 可都用同一个算法
				//计算7种占比， 可都用一个算法
			}
			request.setAttribute("dateList", dateList);
			request.setAttribute("isSearch", "1");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return "forward:/admin/rec/stat/orderSendOutEffective/orderSendOutEffective.jsp";
	}

}
