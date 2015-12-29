package adultadmin.action.stat;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 复核量统计
 * @author Administrator
 *
 */
public class CheckOrderStatAction  extends DispatchAction{
	public static String areaAll="0,1,2,3,4";
	/**
	 * 每天发货复核量统计
	 */
	public ActionForward checkOrderStatByDate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		
		String year=StringUtil.convertNull(request.getParameter("year")).trim();//年份
		String month=StringUtil.convertNull(request.getParameter("month")).trim();//月份
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		if(area.equals("")){
			area=areaAll;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp()); 
		try{
			Calendar now=Calendar.getInstance();
			if("".equals(year)){
				year=now.get(Calendar.YEAR)+"";
			}
			if("".equals(month)){
				month=(now.get(Calendar.MONTH)+1)+"";
			}
			if(Integer.parseInt(year)==now.get(Calendar.YEAR)&&Integer.parseInt(month)>(now.get(Calendar.MONTH)+1)){
				request.setAttribute("tip", "请选择本月及以前的日期！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			Calendar dateStart=Calendar.getInstance();//该月开始时间
			Calendar dateEnd=Calendar.getInstance();//该月结束时间
			dateStart.setTime(sdf.parse(year+"-"+month+"-01 00:00:00"));
			dateEnd.setTime(sdf.parse(year+"-"+month+"-01 00:00:00"));
			dateEnd.add(Calendar.MONTH, 1);
			int dayCount=0;//应记录的该月天数
			long mili=dateEnd.getTimeInMillis()-dateStart.getTimeInMillis();
			if(mili%(1000*60*60*24)==0){
				dayCount=(int)(mili/(1000*60*60*24));
			}else{
				dayCount=(int)((mili-mili%(1000*60*60*24))/(1000*60*60*24))+1;
			}
			
			if(dateEnd.after(now)){
				long mili2=now.getTimeInMillis()-dateStart.getTimeInMillis();
				if(mili2%(1000*60*60*24)==0){
					dayCount=(int)(mili2/(1000*60*60*24));
				}else{
					dayCount=(int)((mili2-mili2%(1000*60*60*24))/(1000*60*60*24))+1;
				}
			}
			dateEnd.add(Calendar.SECOND, 1);

//			Calendar date1=Calendar.getInstance();
//			Calendar date2=Calendar.getInstance();
//			date1.setTime(dateStart.getTime());
//			date2.setTime(dateStart.getTime());
//			date2.add(Calendar.DAY_OF_YEAR, 1);
			
			String sqlDate1=sdf.format(dateStart.getTime());
			String sqlDate2=sdf.format(dateEnd.getTime());
			String sqlDate3=sdf.format(now.getTime());
			//核对包裹列表
			List auditPackageList=new ArrayList();
			if(group.isFlag(397)){
				String apSql="select ap.order_id,ap.check_user_name,ap.check_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
						" where ap.check_datetime>='"+sqlDate1+"' and ap.check_datetime<'"+sqlDate2+"' and ap.check_datetime<'"+sqlDate3+"'"+
						" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
				ResultSet rs=dbOp.executeQuery(apSql);
				while(rs.next()){
					AuditPackageBean apBean=new AuditPackageBean();
					apBean.setOrderId(rs.getInt(1));
					apBean.setCheckUserName(rs.getString(2));
					apBean.setCheckDatetime(rs.getString(3));
					auditPackageList.add(apBean);
				}
				rs.close();
			}else{
				String apSql="select ap.order_id,ap.check_user_name,ap.check_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
				" where ap.check_datetime>='"+sqlDate1+"' and ap.check_datetime<'"+sqlDate2+"' and ap.check_datetime<'"+sqlDate3+"'"+"' and check_user_name='"+user.getUsername()+"'"+
				" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
				ResultSet rs=dbOp.executeQuery(apSql);
				while(rs.next()){
					AuditPackageBean apBean=new AuditPackageBean();
					apBean.setOrderId(rs.getInt(1));
					apBean.setCheckUserName(rs.getString(2));
					apBean.setCheckDatetime(rs.getString(3));
					auditPackageList.add(apBean);
				}
				rs.close();
			}
			
			HashMap statMap=new HashMap();//统计表，key:name1,name2，value:一维数组，长度dayCount
			int userCount=0;//人数
			for(int i=0;i<auditPackageList.size();i++){
				AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
				int orderId=apBean.getOrderId();//订单编号
				String checkUserName=apBean.getCheckUserName().toLowerCase();//复核人姓名
				String checkDatetime=apBean.getCheckDatetime();//复核时间
				
				int productCount=0;//该订单中的商品数量
				OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
				if(orderStock==null){
					continue;
				}
				//该订单商品列表
				List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
				for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
					OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
					int count=orderProduct.getStockoutCount();
					productCount+=count;
				}
				int day=Integer.parseInt(checkDatetime.substring(8,10));//复核时间中的天
				
				int[] array1=new int[dayCount];
				int[] array2=new int[dayCount];
				if(statMap.containsKey(checkUserName+"1")){//已有此人数据
					array1=(int[])statMap.get(checkUserName+"1");
					array2=(int[])statMap.get(checkUserName+"2");
				}else{//无此人数据，初始化数组
					for(int j=0;j<array1.length;j++){
						array1[j]=0;
					}
					for(int j=0;j<array2.length;j++){
						array2[j]=0;
					}
					userCount++;
				}
				array1[day-1]++;
				array2[day-1]+=productCount;
				statMap.put(checkUserName+"1", array1);
				statMap.put(checkUserName+"2", array2);
			}
			
			String[][] statArray=new String[dayCount+3][userCount*2+3];//最终表
			Calendar showCal=Calendar.getInstance();//用于表格第一列的日期显示
			showCal.setTime(dateStart.getTime());
			for(int i=2;i<statArray.length-1;i++){
				statArray[i][0]=sdf.format(showCal.getTime()).substring(0,10);
				showCal.add(Calendar.DAY_OF_YEAR, 1);
			}
			statArray[1][0]="复核日期";
			statArray[statArray.length-1][0]="总计";
			Iterator iter=statMap.keySet().iterator();
			int arrayIndex=0;//最终表第二坐标
			while(iter.hasNext()){
				String key=iter.next().toString();
				if(key.endsWith("1")){
					statArray[0][arrayIndex+1]=key.substring(0,key.length()-1);
					statArray[1][arrayIndex+1]="订单数";
					statArray[1][arrayIndex+2]="产品个数";
					int[] array1=(int[])statMap.get(key);
					int[] array2=(int[])statMap.get(key.substring(0,key.length()-1)+"2");
					for(int i=0;i<dayCount;i++){
						statArray[i+2][arrayIndex+1]=array1[i]+"";
						statArray[i+2][arrayIndex+2]=array2[i]+"";
						
						//总计
						if(statArray[i+2][statArray[0].length-2]==null){
							statArray[i+2][statArray[0].length-2]="0";
						}
						int sum1=Integer.parseInt(statArray[i+2][statArray[0].length-2]);
						sum1+=array1[i];
						statArray[i+2][statArray[0].length-2]=sum1+"";
						if(statArray[i+2][statArray[0].length-1]==null){
							statArray[i+2][statArray[0].length-1]="0";
						}
						int sum2=Integer.parseInt(statArray[i+2][statArray[0].length-1]);
						sum2+=array2[i];
						statArray[i+2][statArray[0].length-1]=sum2+"";
						if(statArray[statArray.length-1][arrayIndex+1]==null){
							statArray[statArray.length-1][arrayIndex+1]="0";
						}
						int sum3=Integer.parseInt(statArray[statArray.length-1][arrayIndex+1]);
						sum3+=array1[i];
						statArray[statArray.length-1][arrayIndex+1]=sum3+"";
						if(statArray[statArray.length-1][arrayIndex+2]==null){
							statArray[statArray.length-1][arrayIndex+2]="0";
						}
						int sum4=Integer.parseInt(statArray[statArray.length-1][arrayIndex+2]);
						sum4+=array2[i];
						statArray[statArray.length-1][arrayIndex+2]=sum4+"";
						
						if(statArray[statArray.length-1][statArray[0].length-2]==null){
							statArray[statArray.length-1][statArray[0].length-2]="0";
						}
						int sum5=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-2]);
						sum5+=array1[i];
						statArray[statArray.length-1][statArray[0].length-2]=sum5+"";
						if(statArray[statArray.length-1][statArray[0].length-1]==null){
							statArray[statArray.length-1][statArray[0].length-1]="0";
						}
						int sum6=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
						sum6+=array2[i];
						statArray[statArray.length-1][statArray[0].length-1]=sum6+"";
					}
					arrayIndex+=2;
				}
			}
			statArray[0][arrayIndex+1]="总计";
			statArray[1][arrayIndex+1]="订单数";
			statArray[1][arrayIndex+2]="产品个数";
			
			request.setAttribute("dayCount", dayCount+"");
			request.setAttribute("statArray", statArray);
			request.setAttribute("month", month);
			request.setAttribute("year", year);
			request.setAttribute("thisYear", now.get(Calendar.YEAR)+"");//用于确定今年年份
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stockService.releaseAll();
		}
		if(request.getParameter("isExport")!=null&&request.getParameter("isExport").equals("1")){
			return mapping.findForward("dateExcel");
		}
		return mapping.findForward("date");
	}
	
	/**
	 * 每小时发货复核量统计
	 */
	public ActionForward checkOrderStatByHour(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		
		String date=StringUtil.convertNull(request.getParameter("date")).trim();//查询日期
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		if(area.equals("")){
			area="-1";
		}
		Calendar now=Calendar.getInstance();
		Calendar cal=Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if("".equals(date)){
			date=DateUtil.getNowDateStr();
		}
		cal.setTime(sdf.parse(date+" 00:00:00"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp()); 
		try{
			int yearDif=now.get(Calendar.YEAR)-Integer.parseInt(date.substring(0,4));//当前年与查询年的差
			if(yearDif!=0&&yearDif!=1){
				request.setAttribute("tip", "只能查询今年和去年的数据！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(cal.after(now)){
				request.setAttribute("tip", "请选择今天及以前的日期！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			HashMap statMap=new HashMap();//统计表，key:name1,name2，value:一维数组，长度dayCount
			int userCount=0;//人数
			String sql="";
			
			String sqlDate3=sdf.format(now.getTime());
//			if(group.isFlag(397)){
//				sql="check_datetime>='"+date+" 00:00:00' and check_datetime<='"+date+" 23:59:59' and check_datetime<='"+sqlDate3+"'";
//			}else{
//				sql="check_datetime>='"+date+" 00:00:00' and check_datetime<='"+date+" 23:59:59' and check_datetime<='"+sqlDate3+"'"+" and check_user_name='"+user.getUsername()+"'";
//			}
//			
			//List auditPackageList=stockService.getAuditPackageList(sql, -1, -1, null);
			
			List auditPackageList=new ArrayList();
			if(group.isFlag(397)){
				String apSql="select ap.order_id,ap.check_user_name,ap.check_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
						" where check_datetime>='"+date+" 00:00:00' and check_datetime<='"+date+" 23:59:59' and check_datetime<='"+sqlDate3+"'"+
						" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
				ResultSet rs=dbOp.executeQuery(apSql);
				while(rs.next()){
					AuditPackageBean apBean=new AuditPackageBean();
					apBean.setOrderId(rs.getInt(1));
					apBean.setCheckUserName(rs.getString(2));
					apBean.setCheckDatetime(rs.getString(3));
					auditPackageList.add(apBean);
				}
				rs.close();
			}else{
				String apSql="select ap.order_id,ap.check_user_name,ap.check_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
				" where check_datetime>='"+date+" 00:00:00' and check_datetime<='"+date+" 23:59:59' and check_datetime<='"+sqlDate3+"'"+" and check_user_name='"+user.getUsername()+"'"+
				" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
				ResultSet rs=dbOp.executeQuery(apSql);
				while(rs.next()){
					AuditPackageBean apBean=new AuditPackageBean();
					apBean.setOrderId(rs.getInt(1));
					apBean.setCheckUserName(rs.getString(2));
					apBean.setCheckDatetime(rs.getString(3));
					auditPackageList.add(apBean);
				}
				rs.close();
			}
			
			int hourCount=0;
			if(now.get(Calendar.YEAR)==Integer.parseInt(date.substring(0,4))&&
					now.get(Calendar.MONTH)+1==Integer.parseInt(date.substring(5,7))&&
					now.get(Calendar.DAY_OF_MONTH)==Integer.parseInt(date.substring(8,10))
					){//查询今天，到现在的时间
					hourCount=now.get(Calendar.HOUR_OF_DAY)+1;
				}else{
					hourCount=24;
				}
			for(int i=0;i<auditPackageList.size();i++){
				AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
				int orderId=apBean.getOrderId();//订单编号
				String checkUserName=apBean.getCheckUserName().toLowerCase();//复核人姓名
				String checkDatetime=apBean.getCheckDatetime();//复核时间
				
				int productCount=0;//该订单中的商品数量
				OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
				if(orderStock==null){
					continue;
				}
				//该订单商品列表
				List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
				for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
					OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
					int count=orderProduct.getStockoutCount();
					productCount+=count;
				}
				int hour=Integer.parseInt(checkDatetime.substring(11,13));//复核时间中的 小时
				
				int[] array1=new int[hourCount];
				int[] array2=new int[hourCount];
				if(statMap.containsKey(checkUserName+"1")){//已有此人数据
					array1=(int[])statMap.get(checkUserName+"1");
					array2=(int[])statMap.get(checkUserName+"2");
				}else{//无此人数据，初始化数组
					for(int j=0;j<array1.length;j++){
						array1[j]=0;
					}
					for(int j=0;j<array2.length;j++){
						array2[j]=0;
					}
					userCount++;
				}
				array1[hour]++;
				array2[hour]+=productCount;
				statMap.put(checkUserName+"1", array1);
				statMap.put(checkUserName+"2", array2);
			}
			
			String[][] statArray=new String[hourCount+3][userCount*2+4];//最终表
			statArray[0][0]="复核时间段";
			statArray[1][0]="起始";
			statArray[1][1]="截止";
			//第一列日期显示
			for(int i=2;i<statArray.length-1;i++){
				statArray[i][0]=(i-2)+":00";
				statArray[i][1]=(i-2)+":59";
			}
			statArray[statArray.length-1][0]="总计";
			Iterator iter=statMap.keySet().iterator();
			int arrayIndex=1;//最终表第二坐标
			while(iter.hasNext()){
				String key=iter.next().toString();
				if(key.endsWith("1")){
					statArray[0][arrayIndex+1]=key.substring(0,key.length()-1);
					statArray[1][arrayIndex+1]="订单数";
					statArray[1][arrayIndex+2]="产品个数";
					int[] array1=(int[])statMap.get(key);
					int[] array2=(int[])statMap.get(key.substring(0,key.length()-1)+"2");
					for(int i=0;i<hourCount;i++){
						statArray[i+2][arrayIndex+1]=array1[i]+"";
						statArray[i+2][arrayIndex+2]=array2[i]+"";
						
						//总计
						if(statArray[i+2][statArray[0].length-2]==null){
							statArray[i+2][statArray[0].length-2]="0";
						}
						int sum1=Integer.parseInt(statArray[i+2][statArray[0].length-2]);
						sum1+=array1[i];
						statArray[i+2][statArray[0].length-2]=sum1+"";
						
						if(statArray[i+2][statArray[0].length-1]==null){
							statArray[i+2][statArray[0].length-1]="0";
						}
						int sum2=Integer.parseInt(statArray[i+2][statArray[0].length-1]);
						sum2+=array2[i];
						statArray[i+2][statArray[0].length-1]=sum2+"";
						
						if(statArray[statArray.length-1][arrayIndex+1]==null){
							statArray[statArray.length-1][arrayIndex+1]="0";
						}
						int sum3=Integer.parseInt(statArray[statArray.length-1][arrayIndex+1]);
						sum3+=array1[i];
						statArray[statArray.length-1][arrayIndex+1]=sum3+"";
						
						if(statArray[statArray.length-1][arrayIndex+2]==null){
							statArray[statArray.length-1][arrayIndex+2]="0";
						}
						int sum4=Integer.parseInt(statArray[statArray.length-1][arrayIndex+2]);
						sum4+=array2[i];
						statArray[statArray.length-1][arrayIndex+2]=sum4+"";
						
						if(statArray[statArray.length-1][statArray[0].length-2]==null){
							statArray[statArray.length-1][statArray[0].length-2]="0";
						}
						int sum5=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-2]);
						sum5+=array1[i];
						statArray[statArray.length-1][statArray[0].length-2]=sum5+"";
						
						if(statArray[statArray.length-1][statArray[0].length-1]==null){
							statArray[statArray.length-1][statArray[0].length-1]="0";
						}
						int sum6=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
						sum6+=array2[i];
						statArray[statArray.length-1][statArray[0].length-1]=sum6+"";
					}
					arrayIndex+=2;
				}
			}
			statArray[0][arrayIndex+1]="总计";
			statArray[1][arrayIndex+1]="订单数";
			statArray[1][arrayIndex+2]="产品个数";
			
			request.setAttribute("date", date);
			request.setAttribute("statArray", statArray);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stockService.releaseAll();
		}
		return mapping.findForward("hour");
	}
	
	/**
	 * 个人发货复核量统计
	 */
	public ActionForward checkOrderStatByName(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String userName=StringUtil.toSql(StringUtil.convertNull(request.getParameter("userName")).trim().toLowerCase());
		String dateStart=StringUtil.convertNull(request.getParameter("dateStart")).trim();
		String dateEnd=StringUtil.convertNull(request.getParameter("dateEnd")).trim();
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		if(area.equals("")){
			area="-1";
		}
		if(!userName.equals("")){
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp()); 
			try{
				Calendar now=Calendar.getInstance();//现在时间
				Calendar cal1=Calendar.getInstance();//查询开始时间
				Calendar cal2=Calendar.getInstance();//查询结束时间
				if(dateStart.equals("")&&dateEnd.equals("")){//没填日期
					cal1.add(Calendar.DAY_OF_YEAR, -6);
				}else{//填了日期
					cal1.setTime(sdf.parse(dateStart+" 00:00:00"));
					cal2.setTime(sdf.parse(dateEnd+" 23:59:59"));
				}
				if(cal1.after(cal2)){
					request.setAttribute("tip", "起始日期必须早于结束日期！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cal2.get(Calendar.YEAR)>now.get(Calendar.YEAR)||
						(cal2.get(Calendar.YEAR)==now.get(Calendar.YEAR)&&cal2.get(Calendar.MONTH)>now.get(Calendar.MONTH))||
						(cal2.get(Calendar.YEAR)==now.get(Calendar.YEAR)&&cal2.get(Calendar.MONTH)==now.get(Calendar.MONTH)&&cal2.get(Calendar.DAY_OF_MONTH)>now.get(Calendar.DAY_OF_MONTH))
				){
					request.setAttribute("tip", "请选择今天及以前的日期！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				int yearCount=now.get(Calendar.YEAR)-cal1.get(Calendar.YEAR);
				if(yearCount!=0&&yearCount!=1){
					request.setAttribute("tip", "只能查询今年和去年的数据！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				int hourCount=0;//列表的小时数
				if(sdf.format(cal1.getTime()).substring(0,10).equals(sdf.format(now.getTime()).substring(0,10))&&sdf.format(cal2.getTime()).substring(0,10).equals(sdf.format(now.getTime()).substring(0,10))){//查询今天
					hourCount=now.get(Calendar.HOUR_OF_DAY)+1;
				}else{//不查询今天或不止查询今天
					hourCount=24;
				}
				int dayCount=0;//列表的天数
				long mili=cal2.getTimeInMillis()-cal1.getTimeInMillis();
				if(mili%(1000*60*60*24)==0){
					dayCount=(int)mili/(1000*60*60*24)+1;
				}else{
					dayCount=(int)((mili-mili%(1000*60*60*24))/(1000*60*60*24))+1;
				}
				if(dayCount>31){
					request.setAttribute("tip", "查看天数不能大于31天！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				String[][] statArray=new String[hourCount+3][dayCount*2+4];//最终表
				statArray[0][0]="复核时间段";
				statArray[1][0]="起始";
				statArray[1][1]="截止";
				
				if((!group.isFlag(397))&&(!userName.equals(user.getUsername()))){
					request.setAttribute("tip", "您无权查看该账号！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List auditPackageList=new ArrayList();
				String apSql="select ap.order_id,ap.check_user_name,ap.check_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
				" where check_datetime>='"+sdf.format(cal1.getTime())+"' and check_datetime<='"+sdf.format(cal2.getTime())+"' and check_datetime<='"+sdf.format(now.getTime())+"' and check_user_name='"+userName+"'"+
				" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
				ResultSet rs=dbOp.executeQuery(apSql);
				while(rs.next()){
					AuditPackageBean apBean=new AuditPackageBean();
					apBean.setOrderId(rs.getInt(1));
					apBean.setCheckUserName(rs.getString(2));
					apBean.setCheckDatetime(rs.getString(3));
					auditPackageList.add(apBean);
				}
				rs.close();
				
				for(int i=2;i<statArray[0].length-2;i+=2){
					statArray[0][i]=sdf.format(cal1.getTime()).substring(0,10);
					cal1.add(Calendar.DAY_OF_YEAR, 1);
					statArray[1][i]="订单数";
					statArray[1][i+1]="产品个数";
				}
				statArray[1][statArray[0].length-2]="订单数";
				statArray[1][statArray[0].length-1]="产品个数";
				statArray[0][statArray[0].length-2]="总计";
				for(int i=2;i<statArray.length-1;i++){
					statArray[i][0]=(i-2)+":00";
					statArray[i][1]=(i-2)+":59";
				}
				if(statArray.length<27){
					statArray[statArray.length-2][1]=sdf.format(now.getTime()).substring(11,16);
				}
				statArray[statArray.length-1][0]="总计";
				
				for(int i=0;i<auditPackageList.size();i++){
					AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
					int orderId=apBean.getOrderId();//订单编号
					String checkDatetime=apBean.getCheckDatetime();//复核时间
					String date=checkDatetime.substring(0,10);//复核日期
					int hour=Integer.parseInt(checkDatetime.substring(11,13));//小时数
					
					int productCount=0;//该订单中的商品数量
					OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
					if(orderStock==null){
						continue;
					}
					//该订单商品列表
					List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
					for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
						OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
						int count=orderProduct.getStockoutCount();
						productCount+=count;
					}
					
					for(int j=0;j<dayCount;j++){
						if(statArray[0][2*(j+1)].equals(date)){
							if(statArray[hour+2][2*(j+1)]==null){//订单数
								statArray[hour+2][2*(j+1)]="1";
							}else{
								int count=Integer.parseInt(statArray[hour+2][2*(j+1)]);
								count++;
								statArray[hour+2][2*(j+1)]=count+"";
							}
							
							if(statArray[hour+2][2*(j+1)+1]==null){//产品个数
								statArray[hour+2][2*(j+1)+1]=productCount+"";
							}else{
								int count=Integer.parseInt(statArray[hour+2][2*(j+1)+1]);
								count+=productCount;
								statArray[hour+2][2*(j+1)+1]=count+"";
							}
							
							if(statArray[hour+2][statArray[0].length-2]==null){//横排总计订单数
								statArray[hour+2][statArray[0].length-2]="1";
							}else{
								int count=Integer.parseInt(statArray[hour+2][statArray[0].length-2]);
								count++;
								statArray[hour+2][statArray[0].length-2]=count+"";
							}
							
							if(statArray[hour+2][statArray[0].length-1]==null){//横排总计产品个数
								statArray[hour+2][statArray[0].length-1]=productCount+"";
							}else{
								int count=Integer.parseInt(statArray[hour+2][statArray[0].length-1]);
								count+=productCount;
								statArray[hour+2][statArray[0].length-1]=count+"";
							}
							
							if(statArray[statArray.length-1][2*(j+1)]==null){//纵排总计订单数
								statArray[statArray.length-1][2*(j+1)]="1";
							}else{
								int count=Integer.parseInt(statArray[statArray.length-1][2*(j+1)]);
								count++;
								statArray[statArray.length-1][2*(j+1)]=count+"";
							}
							
							if(statArray[statArray.length-1][2*(j+1)+1]==null){//纵排总计产品个数
								statArray[statArray.length-1][2*(j+1)+1]=productCount+"";
							}else{
								int count=Integer.parseInt(statArray[statArray.length-1][2*(j+1)+1]);
								count+=productCount;
								statArray[statArray.length-1][2*(j+1)+1]=count+"";
							}
							
							if(statArray[statArray.length-1][statArray[0].length-2]==null){//总计订单数
								statArray[statArray.length-1][statArray[0].length-2]="1";
							}else{
								int count=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-2]);
								count++;
								statArray[statArray.length-1][statArray[0].length-2]=count+"";
							}
							
							if(statArray[statArray.length-1][statArray[0].length-1]==null){//总计产品个数
								statArray[statArray.length-1][statArray[0].length-1]=productCount+"";
							}else{
								int count=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
								count+=productCount;
								statArray[statArray.length-1][statArray[0].length-1]=count+"";
							}
						}
					}
				}
				request.setAttribute("dateStart", dateStart);
				request.setAttribute("dateEnd", dateEnd);
				request.setAttribute("statArray", statArray);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				stockService.releaseAll();
			}
		}
		return mapping.findForward("name");
	}
	
}
