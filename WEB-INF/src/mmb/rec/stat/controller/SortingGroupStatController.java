package mmb.rec.stat.controller;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchGroupStatBean;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/sortingGroupStatController")
public class SortingGroupStatController {
	public static String areaAll="0,1,2,3,4";
	@RequestMapping("/sortingGroupStatByDateJsp")
	public String sortingGroupStatByDateJsp(HttpServletRequest request,HttpServletResponse response){
		Calendar now=Calendar.getInstance();
		String year=now.get(Calendar.YEAR)+"";
		String month=(now.get(Calendar.MONTH)+1)+"";
		request.setAttribute("month", month);
		request.setAttribute("year", year);
		return "forward:/admin/rec/stat/sortingGroupStatByDate.jsp";
	}
	/**
	 * 日期分拨量统计
	 * 2013-9-5
	 * 朱爱林	
	 */
	@RequestMapping("/sortingGroupStatByDate")
	public String sortingGroupStatByDate(HttpServletRequest request,HttpServletResponse response)
		throws Exception{

		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return null;
		}
		
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
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		Map<String,Object> resultMap = new HashMap<String, Object>();
//		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
//		Map<String,String> map = null;
		try{
			Calendar now=Calendar.getInstance();
			if("".equals(year)){
				year=now.get(Calendar.YEAR)+"";
			}
			if("".equals(month)){
				month=(now.get(Calendar.MONTH)+1)+"";
			}
			if(Integer.parseInt(year)==now.get(Calendar.YEAR)&&Integer.parseInt(month)>(now.get(Calendar.MONTH)+1)){
//				request.setAttribute("tip", "请选择本月及以前的日期！");
//				request.setAttribute("result", "failure");
//				return "/admin/tip";
				response.getWriter().write("{\'result\':\'failure\',\'tip\':\'请选择本月及以前的日期！\'}");
				return null;
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
			List<SortingBatchGroupStatBean> sortingBatchGroupStatList=new ArrayList<SortingBatchGroupStatBean>();
			//auditPackageList=stockService.getAuditPackageList("audit_package_datetime>='"+sqlDate1+"' and audit_package_datetime<'"+sqlDate2+"' and audit_package_datetime<'"+sqlDate3+"'", -1, -1, null);
			//将所有数量改为 实际分播完成的数量 并加入统计还未结批的分播
			String sbgSql="select sbg.staff_id2, sbg.id, sbo.id, sbop.complete_count, sbg.complete_datetime2 from sorting_batch_group sbg, sorting_batch_order sbo, sorting_batch_order_product sbop where sbo.sorting_group_id = sbg.id and sbo.id = sbop.sorting_batch_order_id" +
			" and sbg.complete_datetime2>='"+sqlDate1+"' and sbg.complete_datetime2 <'"+sqlDate2+"' and sbg.complete_datetime2<'"+sqlDate3+"'"+
			" and sbg.status2 in ( " + SortingBatchGroupBean.SORTING_STATUS1 + "," + SortingBatchGroupBean.SORTING_STATUS2 + ") and sbg.storage in (" + area + ")";
			ResultSet rs=dbOp.executeQuery(sbgSql);
			while(rs.next()){
				SortingBatchGroupStatBean sbgsBean = new SortingBatchGroupStatBean();
				sbgsBean.setStaffId(rs.getInt(1));
				sbgsBean.setSortingBatchGroupId(rs.getInt(2));
				sbgsBean.setSortingBatchOrderId(rs.getInt(3));
				sbgsBean.setProductCount(rs.getInt(4));
				sbgsBean.setSortingBatchCompleteTime(rs.getString(5));
				sortingBatchGroupStatList.add(sbgsBean);
			}
			rs.close();
			List<String> staffList = new ArrayList<String>();
			HashMap statMap=new HashMap();//统计表，key:name，value:一维数组，长度dayCount
			Map<String, Map<String,String>> groupOrderMap = new HashMap<String, Map<String, String>>();
			Map<String, Map<String,String>> staffGroupMap = new HashMap<String, Map<String, String>>();
			int userCount=0;//人数
			int x = sortingBatchGroupStatList.size();
			for(int i=0;i<x;i++){
				SortingBatchGroupStatBean sbgsBean = sortingBatchGroupStatList.get(i);
				int staffId = sbgsBean.getStaffId();//贴单人id
				String sortingGroupDatetime = sbgsBean.getSortingBatchCompleteTime();//贴单时间
				String sboId=sbgsBean.getSortingBatchOrderId() + "";//订单编号
				String batchGroupId = sbgsBean.getSortingBatchGroupId() + "";//波次id
				int productCount=sbgsBean.getProductCount();//该订单中的商品数量
				
				//该订单商品列表
				
				int day=Integer.parseInt(sortingGroupDatetime.substring(8,10));//贴单时间中的天
				
				int[] array1=new int[dayCount];
				int[] array2 = new int[dayCount];
				int[] array3=new int[dayCount];
				Map<String,String> orderMap = new HashMap<String,String>();
				Map<String, String> groupMap = new HashMap<String, String>();
				if(statMap.containsKey(staffId + "")){//已有此人数据
					array1=(int[])statMap.get(staffId + "");
					array2 = (int[]) statMap.get(staffId + "A");
					array3=(int[])statMap.get(staffId+"B");
					orderMap = groupOrderMap.get(staffId + "");
					groupMap = staffGroupMap.get(staffId + "");
				}else{//无此人数据，初始化数组
					for(int j=0;j<array1.length;j++){
						array1[j]=0;
					}
					for(int j=0;j<array2.length;j++){
						array2[j]=0;
					}
					for(int j=0;j<array3.length;j++){
						array3[j]=0;
					}
					userCount++;
					staffList.add(staffId+"");
				}
				array3[day-1]+=productCount;
				if(!orderMap.containsKey(sboId)) {
					array2[day-1]++;
					orderMap.put(sboId, "");
				}
				if( !groupMap.containsKey(batchGroupId) ) {
					array1[day-1]++;
					groupMap.put(batchGroupId, "");
				}
				statMap.put(staffId+"", array1);
				statMap.put(staffId+"A", array2);
				statMap.put(staffId+"B", array3);
				groupOrderMap.put(staffId+"", orderMap);
				staffGroupMap.put(staffId+"", groupMap);
			}
			int secondCount = userCount * 3;
			String[][] statArray=new String[dayCount+3][secondCount+4];//最终表
			Calendar showCal=Calendar.getInstance();//用于表格第一列的日期显示
			showCal.setTime(dateStart.getTime());
			for(int i=2;i<statArray.length-1;i++){
				statArray[i][0]=sdf.format(showCal.getTime()).substring(0,10);
				showCal.add(Calendar.DAY_OF_YEAR, 1);
			}
			statArray[0][0]=" ";
			statArray[1][0]="分播日期";
			statArray[statArray.length-1][0]="总计";
			Iterator iter=staffList.iterator();
			int arrayIndex=0;//最终表第二坐标
			while(iter.hasNext()){
				String key=iter.next().toString();
					CargoStaffBean csBean = service.getCargoStaff("id=" + Integer.parseInt(key));
					String userName = "员工信息已删除";
					if( csBean != null ) {
						userName = csBean.getUserName();
					}
					statArray[0][arrayIndex+1]=userName;
					statArray[1][arrayIndex+1]="波次数";
					statArray[1][arrayIndex+2]="订单数";
					statArray[1][arrayIndex+3]="商品件数";
					int[] array1=(int[])statMap.get(key);
					int[] array2=(int[])statMap.get(key+"A");
					int[] array3=(int[])statMap.get(key+"B");
					for(int i=0;i<dayCount;i++){
						statArray[i+2][arrayIndex+1]=array1[i]+"";
						statArray[i+2][arrayIndex+2]=array2[i]+"";
						statArray[i+2][arrayIndex+3]=array3[i]+"";
						
						//总计
						if(statArray[i+2][statArray[0].length-3]==null){
							statArray[i+2][statArray[0].length-3]="0";
						}
						int sum1=Integer.parseInt(statArray[i+2][statArray[0].length-3]);//右边订波次总计
						sum1+=array1[i];
						statArray[i+2][statArray[0].length-3]=sum1+"";

						if(statArray[i+2][statArray[0].length-2]==null){
							statArray[i+2][statArray[0].length-2]="0";
						}
						int sum2=Integer.parseInt(statArray[i+2][statArray[0].length-2]);//右边订单个数总计
						sum2+=array2[i];
						statArray[i+2][statArray[0].length-2]=sum2+"";
						
						if(statArray[i+2][statArray[0].length-1]==null){
							statArray[i+2][statArray[0].length-1]="0";
						}
						int sum3=Integer.parseInt(statArray[i+2][statArray[0].length-1]);//右边商品个数总计
						sum3+=array3[i];
						statArray[i+2][statArray[0].length-1]=sum3+"";
						
						//---
						if(statArray[statArray.length-1][arrayIndex+1]==null){
							statArray[statArray.length-1][arrayIndex+1]="0";
						}
						int sum4=Integer.parseInt(statArray[statArray.length-1][arrayIndex+1]);//下边波次数总计
						sum4+=array1[i];
						statArray[statArray.length-1][arrayIndex+1]=sum4+"";
						if(statArray[statArray.length-1][arrayIndex+2]==null){
							statArray[statArray.length-1][arrayIndex+2]="0";
						}
						int sum5=Integer.parseInt(statArray[statArray.length-1][arrayIndex+2]);   // 下边订单数
						sum5+=array2[i];
						statArray[statArray.length-1][arrayIndex+2]=sum5+"";
						
						if(statArray[statArray.length-1][arrayIndex+3]==null){
							statArray[statArray.length-1][arrayIndex+3]="0";
						}
						int sum6=Integer.parseInt(statArray[statArray.length-1][arrayIndex+3]);  //下边商品数量
						sum6+=array3[i];
						statArray[statArray.length-1][arrayIndex+3]=sum6+"";
						
						
						
						//----
						if(statArray[statArray.length-1][statArray[0].length-3]==null){
							statArray[statArray.length-1][statArray[0].length-3]="0";
						}
						
						int sum7=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-3]);//右下角波次数总计
						sum7+=array1[i];
						statArray[statArray.length-1][statArray[0].length-3]=sum7+"";
						
						if(statArray[statArray.length-1][statArray[0].length-2]==null){
							statArray[statArray.length-1][statArray[0].length-2]="0";
						}
						int sum8=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-2]);   //右下角订单数总计
						sum8+=array2[i];
						statArray[statArray.length-1][statArray[0].length-2]=sum8+"";
						
						if(statArray[statArray.length-1][statArray[0].length-1]==null){
							statArray[statArray.length-1][statArray[0].length-1]="0";
						}
						int sum9=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);   //右下角 商品数总计
						sum9+=array3[i];
						statArray[statArray.length-1][statArray[0].length-1]=sum9+"";
					}
					arrayIndex+=3;
				
			}
			statArray[0][arrayIndex+1]="总计";
			statArray[1][arrayIndex+1]="波次数";
			statArray[1][arrayIndex+2]="订单数";
			statArray[1][arrayIndex+3]="商品件数";
			
			/*
			 * 测试easyui动态生成列
			 */
			String[] arrs = new String[]{"sorting_batch_batchtimes","sorting_batch_orders","sorting_batch_counts"};
			StringBuilder sb = new StringBuilder();//列
			StringBuilder ss = new StringBuilder();//行
			sb.append("[[");
			ss.append("[");
			String root = request.getContextPath();
			StringBuilder param = new StringBuilder();
			for(int i=0,z=0,t=0;i<statArray.length;i++){
				if(i!=0&&i!=1){
					ss.append("{");
				}
				for(int j=0;j<statArray[i].length;j++){
					if(i==0){
						if(j==0){
							sb.append("{field:'',title:\'\',colspan:1},");
						}
						if((j-1)%3==0){
							//处理第一行
							//method=sortingGroupStatByName&userName=<%=statArray[i][j]==null?"":statArray[i][j] %>
							//&dateStart=<%=year %>-<%=month<10?"0"+month:month %>-01
							//&dateEnd=<%=year %>-<%=month<10?"0"+month:month %>-<%=dayCount<10?"0"+dayCount:dayCount %>" target="_blank" >
							if(j==statArray[i].length-3){
								sb.append("{field:'',title:\'"+statArray[i][j]+"\',width:100,align:\'center\', colspan:3},");
							}else{
								param.delete(0, param.length());
								param.append("?status=1&userName=").append(statArray[i][j]==null?"":statArray[i][j])
								.append("&dateStart=").append(year).append("-").append(Integer.parseInt(month)<10?"0"+month:month)
								.append("-01").append("&dateEnd=").append(year).append("-").append(Integer.parseInt(month)<10?"0"+month:month)
								.append("-").append(dayCount<10?"0"+dayCount:dayCount);
								sb.append("{field:'',title:\'<a href=\""+root+"/sortingGroupStatController/sortingGroupStatByNameJsp.mmx"+param.toString()+"\" target=\"_blank\">"+statArray[i][j]+"</a>\',width:100,align:\'center\', colspan:3},");
							}
						}
						if(j==statArray[i].length-1){
							sb.deleteCharAt(sb.length()-1);
							sb.append("],[");
						}
					}else if(i==1){
						if(j==0){ 
							sb.append("{field:\'sorting_batch_time\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'},");
						}else{
							sb.append("{field:\'"+arrs[t++]+j+"\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'}");
							if(j==statArray[i].length-1){
								sb.append("]]");
							}else{
								sb.append(",");
							}
							t = t%3;
						}
					}else{
						if(j==0){
							//method=sortingGroupStatByHour&date=<%=statArray[i][j]==null?"0":statArray[i][j] %>" target="_blank"
							if(i==statArray.length-1){
								ss.append("\'sorting_batch_time\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
							}else{
								param.delete(0, param.length());
								param.append("?date=").append(statArray[i][j]==null?"0":statArray[i][j]);
								ss.append("\'sorting_batch_time\':\'<a href=\""+root+"/sortingGroupStatController/sortingGroupStatByHourJsp.mmx"+param.toString()+"\" target=\"_blank\">").append(statArray[i][j]==null?"0":statArray[i][j]).append("</a>\',");
							}
						}else{
							ss.append("\'"+arrs[z++]+j+"\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\'");
							if(j==statArray[i].length-1){
								ss.append("},");
							}else{
								ss.append(",");
							}
							z = z%3;
						}
					}
				}
			}
			ss.deleteCharAt(ss.length()-1);
			ss.append("]");
//			resultMap.put("rows", ss.toString());
//			resultMap.put("columns", sb.toString());
			request.setAttribute("dayCount", dayCount+"");
			request.setAttribute("statArray", statArray);
			request.setAttribute("month", month);
			request.setAttribute("year", year);
			request.setAttribute("thisYear", now.get(Calendar.YEAR)+"");//用于确定今年年份
			String re = "{\'rows\':"+ss.toString()+",\'columns\':"+sb.toString()+"}";
			response.getWriter().write(re);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stockService.releaseAll();
		}
		if(request.getParameter("isExport")!=null&&request.getParameter("isExport").equals("1")){
			return "forward:/admin/rec/stat/sortingGroupStatByDateExcel.jsp";
		}
		return null;
	}
	/**
	 * 获取库地区下拉选
	 * 2013-9-5
	 * 朱爱林
	 */
	@RequestMapping("/querySelectStoreArea")
	@ResponseBody
	public Object querySelectStockType(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("id", "");
		map.put("text", "全部地区");
		map.put("selected", "true");
		HashMap areaMap=ProductStockBean.areaMap;
		Iterator iter=areaMap.keySet().iterator();
		list.add(map);
		while(iter.hasNext()){
			String area=iter.next().toString();
			String areaName=areaMap.get(Integer.parseInt(area)).toString();
			map = new HashMap<String, String>();
			map.put("id",area);
			map.put("text", areaName);
			list.add(map);
		}
		return list;
	}
	@RequestMapping("/sortingGroupStatByHourJsp")
	public String sortingGroupStatByHourJsp(HttpServletRequest request,HttpServletResponse response){
		String date=StringUtil.convertNull(request.getParameter("date")).trim();
		if("".equals(date)){
			date=DateUtil.getNowDateStr();
		}
		request.setAttribute("date", date);
		return "forward:/admin/rec/stat/sortingGroupStatByHour.jsp";
	}
	/**
	 * 每小时分播统计
	 * 2013-9-9
	 * 朱爱林	
	 */
	@RequestMapping("/sortingGroupStatByHour")
	public String sortingGroupStatByHour(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return null;
		}
		UserGroupBean group = user.getGroup();
		String date=StringUtil.convertNull(request.getParameter("date")).trim();//查询日期
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		if(area.equals("")){
			area=areaAll;
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
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			int yearDif=now.get(Calendar.YEAR)-Integer.parseInt(date.substring(0,4));//当前年与查询年的差
			if(yearDif!=0&&yearDif!=1){
//				request.setAttribute("tip", "只能查询今年和去年的数据！");
//				request.setAttribute("result", "failure");
				response.getWriter().write("{\'result\':\'failure\',\'tip\':\'只能查询今年和去年的数据！\'}");
				return null;
			}
			if(cal.after(now)){
//				request.setAttribute("tip", "请选择今天及以前的日期！");
//				request.setAttribute("result", "failure");
//				return "/admin/tip";
				response.getWriter().write("{\'result\':\'failure\',\'tip\':\'请选择今天及以前的日期！\'}");
				return null;
			}
			HashMap statMap=new HashMap();//统计表，key:name1，value:一维数组，长度dayCount
			Map<String, Map<String,String>> groupOrderMap = new HashMap<String, Map<String, String>>();
			Map<String, Map<String,String>> staffGroupMap = new HashMap<String, Map<String, String>>();
			int userCount=0;//人数
			String sql="";
			
			String sqlDate3=sdf.format(now.getTime());
			//sql="audit_package_datetime>='"+date+" 00:00:00' and audit_package_datetime<='"+date+" 23:59:59' and audit_package_datetime<='"+sqlDate3+"'";
			//List auditPackageList=stockService.getAuditPackageList(sql, -1, -1, null);
			
			/*String apSql="select ap.order_id,ap.audit_package_user_name,ap.audit_package_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
			" where audit_package_datetime>='"+date+" 00:00:00' and audit_package_datetime<='"+date+" 23:59:59' and audit_package_datetime<='"+sqlDate3+"'"+
			" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
			ResultSet rs=dbOp.executeQuery(apSql);
			while(rs.next()){
				AuditPackageBean apBean=new AuditPackageBean();
				apBean.setOrderId(rs.getInt(1));
				apBean.setAuditPackageUserName(rs.getString(2));
				apBean.setAuditPackageDatetime(rs.getString(3));
				auditPackageList.add(apBean);
			}
			rs.close();*/
			List<SortingBatchGroupStatBean> sortingBatchGroupStatList=new ArrayList<SortingBatchGroupStatBean>();
			//将所有数量改为 实际分播完成的数量 并加入统计还未结批的分播
			String sbgSql="select sbg.staff_id2, sbg.id, sbo.id, sbop.complete_count, sbg.complete_datetime2 from sorting_batch_group sbg, sorting_batch_order sbo, sorting_batch_order_product sbop where sbo.sorting_group_id = sbg.id and sbo.id = sbop.sorting_batch_order_id" +
			" and sbg.complete_datetime2>='"+date+" 00:00:00' and sbg.complete_datetime2 <'"+date+" 23:59:59' and sbg.complete_datetime2<'"+sqlDate3+"'"+
			" and sbg.status2 in ( " + SortingBatchGroupBean.SORTING_STATUS1 + "," + SortingBatchGroupBean.SORTING_STATUS2 + ") and sbg.storage in (" + area + ")";
			ResultSet rs=dbOp.executeQuery(sbgSql);
			while(rs.next()){
				SortingBatchGroupStatBean sbgsBean = new SortingBatchGroupStatBean();
				sbgsBean.setStaffId(rs.getInt(1));
				sbgsBean.setSortingBatchGroupId(rs.getInt(2));
				sbgsBean.setSortingBatchOrderId(rs.getInt(3));
				sbgsBean.setProductCount(rs.getInt(4));
				sbgsBean.setSortingBatchCompleteTime(rs.getString(5));
				sortingBatchGroupStatList.add(sbgsBean);
			}
			rs.close();
			List<String> staffList = new ArrayList<String>();
			int hourCount=0;
			if(now.get(Calendar.YEAR)==Integer.parseInt(date.substring(0,4))&&
					now.get(Calendar.MONTH)+1==Integer.parseInt(date.substring(5,7))&&
					now.get(Calendar.DAY_OF_MONTH)==Integer.parseInt(date.substring(8,10))
					){//查询今天，到现在的时间
					hourCount=now.get(Calendar.HOUR_OF_DAY)+1;
				}else{
					hourCount=24;
				}
			int x = sortingBatchGroupStatList.size();
			for(int i=0;i< x;i++){
				SortingBatchGroupStatBean sbgsBean = sortingBatchGroupStatList.get(i);
				int staffId = sbgsBean.getStaffId();//贴单人id
				String sortingGroupDatetime = sbgsBean.getSortingBatchCompleteTime();//贴单时间
				String sboId=sbgsBean.getSortingBatchOrderId() + "";//订单编号
				String batchGroupId = sbgsBean.getSortingBatchGroupId() + "";//波次id
				int productCount=sbgsBean.getProductCount();//该订单中的商品数量
				
//				int productCount=0;//该订单中的商品数量
//				OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
//				if(orderStock==null){
//					continue;
//				}
//				//该订单商品列表
//				List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
//				for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
//					OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
//					int count=orderProduct.getStockoutCount();
//					productCount+=count;
//				}
				int hour=Integer.parseInt(sortingGroupDatetime.substring(11,13));//贴单时间中的 小时
				
				/*int[] array1=new int[hourCount];
//				int[] array2=new int[hourCount];
				if(statMap.containsKey(staffId + "")){//已有此人数据
					array1=(int[])statMap.get(auditPackageUserName);
//					array2=(int[])statMap.get(auditPackageUserName+"2");
				}else{//无此人数据，初始化数组
					for(int j=0;j<array1.length;j++){
						array1[j]=0;
					}
//					for(int j=0;j<array2.length;j++){
//						array2[j]=0;
//					}
					userCount++;
				}
				array1[hour]++;
//				array2[hour]+=productCount;
				statMap.put(auditPackageUserName, array1);
//				statMap.put(auditPackageUserName+"2", array2);
*/	
			
				int[] array1=new int[hourCount];
				int[] array2 = new int[hourCount];
				int[] array3=new int[hourCount];
				Map<String,String> orderMap = new HashMap<String,String>();
				Map<String, String> groupMap = new HashMap<String, String>();
				if(statMap.containsKey(staffId + "")){//已有此人数据
					array1=(int[])statMap.get(staffId + "");
					array2 = (int[]) statMap.get(staffId + "A");
					array3=(int[])statMap.get(staffId+"B");
					orderMap = groupOrderMap.get(staffId + "");
					groupMap = staffGroupMap.get(staffId + "");
				}else{//无此人数据，初始化数组
					for(int j=0;j<array1.length;j++){
						array1[j]=0;
					}
					for(int j=0;j<array2.length;j++){
						array2[j]=0;
					}
					for(int j=0;j<array3.length;j++){
						array3[j]=0;
					}
					userCount++;
					staffList.add(staffId+"");
				}
				array3[hour]+=productCount;
				if(!orderMap.containsKey(sboId)) {
					array2[hour]++;
					orderMap.put(sboId, "");
				}
				if( !groupMap.containsKey(batchGroupId) ) {
					array1[hour]++;
					groupMap.put(batchGroupId, "");
				}
				statMap.put(staffId+"", array1);
				statMap.put(staffId+"A", array2);
				statMap.put(staffId+"B", array3);
				groupOrderMap.put(staffId+"", orderMap);
				staffGroupMap.put(staffId+"", groupMap);
				
			}
			int secondCount = userCount * 3;
			String[][] statArray=new String[hourCount+3][secondCount+5];//最终表
			statArray[0][0] = "分播时间段";
			statArray[1][0]="起始";
			statArray[1][1]="截止";
//			statArray[1][0]="起始";
//			statArray[1][1]="截止";
			//第一列日期显示
			for(int i=2;i<statArray.length-1;i++){
				statArray[i][0]=(i-2)+":00";
				statArray[i][1]=(i-2)+":59";
			}
			statArray[statArray.length-1][0]="总计";
			Iterator iter=staffList.iterator();
			int arrayIndex=1;//最终表第二坐标
			while(iter.hasNext()){
				String key=iter.next().toString();
					CargoStaffBean csBean = service.getCargoStaff("id=" + Integer.parseInt(key));
					String userName = "员工信息已删除";
					if( csBean != null ) {
						userName = csBean.getName();
					}
					statArray[0][arrayIndex+1]=userName;
					statArray[1][arrayIndex+1]="波次数";
					statArray[1][arrayIndex+2]="订单数";
					statArray[1][arrayIndex+3]="商品件数";
					int[] array1=(int[])statMap.get(key);
					int[] array2=(int[])statMap.get(key+"A");
					int[] array3=(int[])statMap.get(key+"B");
					for(int i=0;i<hourCount;i++){
						statArray[i+2][arrayIndex+1]=array1[i]+"";
						statArray[i+2][arrayIndex+2]=array2[i]+"";
						statArray[i+2][arrayIndex+3]=array3[i]+"";
						
						//总计
						if(statArray[i+2][statArray[0].length-3]==null){
							statArray[i+2][statArray[0].length-3]="0";
						}
						int sum1=Integer.parseInt(statArray[i+2][statArray[0].length-3]);//右边订波次总计
						sum1+=array1[i];
						statArray[i+2][statArray[0].length-3]=sum1+"";

						if(statArray[i+2][statArray[0].length-2]==null){
							statArray[i+2][statArray[0].length-2]="0";
						}
						int sum2=Integer.parseInt(statArray[i+2][statArray[0].length-2]);//右边订单个数总计
						sum2+=array2[i];
						statArray[i+2][statArray[0].length-2]=sum2+"";
						
						if(statArray[i+2][statArray[0].length-1]==null){
							statArray[i+2][statArray[0].length-1]="0";
						}
						int sum3=Integer.parseInt(statArray[i+2][statArray[0].length-1]);//右边商品个数总计
						sum3+=array3[i];
						statArray[i+2][statArray[0].length-1]=sum3+"";
						
						//---
						if(statArray[statArray.length-1][arrayIndex+1]==null){
							statArray[statArray.length-1][arrayIndex+1]="0";
						}
						int sum4=Integer.parseInt(statArray[statArray.length-1][arrayIndex+1]);//下边波次数总计
						sum4+=array1[i];
						statArray[statArray.length-1][arrayIndex+1]=sum4+"";
						if(statArray[statArray.length-1][arrayIndex+2]==null){
							statArray[statArray.length-1][arrayIndex+2]="0";
						}
						int sum5=Integer.parseInt(statArray[statArray.length-1][arrayIndex+2]);   // 下边订单数
						sum5+=array2[i];
						statArray[statArray.length-1][arrayIndex+2]=sum5+"";
						
						if(statArray[statArray.length-1][arrayIndex+3]==null){
							statArray[statArray.length-1][arrayIndex+3]="0";
						}
						int sum6=Integer.parseInt(statArray[statArray.length-1][arrayIndex+3]);  //下边商品数量
						sum6+=array3[i];
						statArray[statArray.length-1][arrayIndex+3]=sum6+"";
						
						
						
						//----
						if(statArray[statArray.length-1][statArray[0].length-3]==null){
							statArray[statArray.length-1][statArray[0].length-3]="0";
						}
						
						int sum7=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-3]);//右下角波次数总计
						sum7+=array1[i];
						statArray[statArray.length-1][statArray[0].length-3]=sum7+"";
						
						if(statArray[statArray.length-1][statArray[0].length-2]==null){
							statArray[statArray.length-1][statArray[0].length-2]="0";
						}
						int sum8=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-2]);   //右下角订单数总计
						sum8+=array2[i];
						statArray[statArray.length-1][statArray[0].length-2]=sum8+"";
						
						if(statArray[statArray.length-1][statArray[0].length-1]==null){
							statArray[statArray.length-1][statArray[0].length-1]="0";
						}
						int sum9=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);   //右下角 商品数总计
						sum9+=array3[i];
						statArray[statArray.length-1][statArray[0].length-1]=sum9+"";
					}
					arrayIndex+=3;
			}
			statArray[0][arrayIndex+1]="总计";
			statArray[1][arrayIndex+1]="波次数";
			statArray[1][arrayIndex+2]="订单数";
			statArray[1][arrayIndex+3]="商品件数";
			String[] arrs = new String[]{"sorting_batch_batchtimes","sorting_batch_orders","sorting_batch_counts"};
			StringBuilder sb = new StringBuilder();//列
			StringBuilder ss = new StringBuilder();//行
			sb.append("[[");
			ss.append("[");
			
			for(int i=0,z=0,t=0;i<statArray.length;i++){
				if(i!=0&&i!=1){
					ss.append("{");
				}
				for(int j=0;j<statArray[i].length;j++){
					if(i==0){
						if(j==0){
							sb.append("{field:'',title:\'分播时间段\',colspan:2,align:\'center\'},");
						}
						if((j-2)%3==0){
							//处理第一行
							sb.append("{field:'',title:\'"+statArray[i][j]+"\',width:100,align:\'center\', colspan:3},");
						}
						if(j==statArray[i].length-1){
							sb.deleteCharAt(sb.length()-1);
							sb.append("],[");
						}
					}else if(i==1){
						if(j==0){ 
							sb.append("{field:\'start\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'},");
						}else if(j==1){
							sb.append("{field:\'end\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'},");
						}else{
							sb.append("{field:\'"+arrs[t++]+j+"\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'}");
							if(j==statArray[i].length-1){
								sb.append("]]");
							}else{
								sb.append(",");
							}
							t = t%3;
						}
					}else{
						if(j==0){
							ss.append("\'start\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
						}else if(j==1){
							ss.append("\'end\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
						}
						else{
							ss.append("\'"+arrs[z++]+j+"\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\'");
							if(j==statArray[i].length-1){
								ss.append("},");
							}else{
								ss.append(",");
							}
							z = z%3;
						}
					}
				}
			}
			ss.deleteCharAt(ss.length()-1);
			ss.append("]");
//			resultMap.put("rows", ss.toString());
//			resultMap.put("columns", sb.toString());
			request.setAttribute("date", date);
			request.setAttribute("statArray", statArray);
			String re = "{\'rows\':"+ss.toString()+",\'columns\':"+sb.toString()+"}";
			response.getWriter().write(re);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stockService.releaseAll();
		}
		return null;
	}
	@RequestMapping("/sortingGroupStatByNameJsp")
	public String sortingGroupStatByNameJsp(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		String userName=StringUtil.convertNull(request.getParameter("userName")).trim().toLowerCase();
		
		userName = new String(userName.getBytes("ISO8859-1"),"UTF-8");
		String dateStart=StringUtil.convertNull(request.getParameter("dateStart")).trim();
		String dateEnd=StringUtil.convertNull(request.getParameter("dateEnd")).trim();
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		String status=StringUtil.convertNull(request.getParameter("status"));//库地区
		request.setAttribute("userName", userName);
		request.setAttribute("dateStart", dateStart);
		request.setAttribute("dateEnd", dateEnd);
		request.setAttribute("area", area);
		request.setAttribute("status", status);
		
		return "forward:/admin/rec/stat/sortingGroupStatByName.jsp";
	}
	/**
	 * 个人分播量统计
	 * 2013-9-9
	 * 朱爱林	
	 */
	@RequestMapping("/sortingGroupStatByName")
	public String sortingGroupStatByName(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return null;
		}
		UserGroupBean group = user.getGroup();
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String userName=StringUtil.convertNull(request.getParameter("userName")).trim().toLowerCase();
		userName = new String(userName.getBytes("ISO8859-1"),"UTF-8");
		String dateStart=StringUtil.convertNull(request.getParameter("dateStart")).trim();
		String dateEnd=StringUtil.convertNull(request.getParameter("dateEnd")).trim();
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		if(area.equals("")){
			area=areaAll;
		}
		if(!userName.equals("")){
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp()); 
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
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
//					request.setAttribute("tip", "起始日期必须早于结束日期！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'起始日期必须早于结束日期！\'}");
					return null;
				}
				if(cal2.get(Calendar.YEAR)>now.get(Calendar.YEAR)||
						(cal2.get(Calendar.YEAR)==now.get(Calendar.YEAR)&&cal2.get(Calendar.MONTH)>now.get(Calendar.MONTH))||
						(cal2.get(Calendar.YEAR)==now.get(Calendar.YEAR)&&cal2.get(Calendar.MONTH)==now.get(Calendar.MONTH)&&cal2.get(Calendar.DAY_OF_MONTH)>now.get(Calendar.DAY_OF_MONTH))
				){
//					request.setAttribute("tip", "请选择今天及以前的日期！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'请选择今天及以前的日期！\'}");
					return null;
				}
				int yearCount=now.get(Calendar.YEAR)-cal1.get(Calendar.YEAR);
				if(yearCount!=0&&yearCount!=1){
//					request.setAttribute("tip", "只能查询今年和去年的数据！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'只能查询今年和去年的数据！\'}");
					return null;
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
//					request.setAttribute("tip", "查看天数不能大于31天！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'查看天数不能大于31天！\'}");
					return null;
				}
				int secondCount = dayCount * 3;
				String[][] statArray=new String[hourCount+3][secondCount+5];//最终表
				statArray[0][0]="分播时间段";
//				statArray[0][1]="截止时间";
				statArray[1][0]="起始";
				statArray[1][1]="截止";
				
				List auditPackageList=new ArrayList();
				CargoStaffBean csBean = service.getCargoStaff("user_name='" + userName + "'");
				if( csBean == null ) {
//					request.setAttribute("tip", "没有找到物流员工信息！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'没有找到物流员工信息！\'}");
					return null;
				}
				List<SortingBatchGroupStatBean> sortingBatchGroupStatList=new ArrayList<SortingBatchGroupStatBean>();
				//将所有数量改为 实际分播完成的数量 并加入统计还未结批的分播
				String apSql="select sbg.id, sbo.id, sbop.complete_count, sbg.complete_datetime2 from sorting_batch_group sbg, sorting_batch_order sbo, sorting_batch_order_product sbop where sbo.sorting_group_id = sbg.id and sbo.id = sbop.sorting_batch_order_id" +
				" and sbg.complete_datetime2>='"+sdf.format(cal1.getTime())+
						"' and sbg.complete_datetime2<='"+sdf.format(cal2.getTime())+
						"' and sbg.complete_datetime2<='"+sdf.format(now.getTime())+
						"' and sbg.staff_id2="+csBean.getId()+
						" and sbg.status2 in ( " + SortingBatchGroupBean.SORTING_STATUS1 + "," + SortingBatchGroupBean.SORTING_STATUS2 + ") and sbg.storage in (" + area + ")";
				ResultSet rs=dbOp.executeQuery(apSql);
				while(rs.next()){
					SortingBatchGroupStatBean sbgsBean = new SortingBatchGroupStatBean();
					sbgsBean.setSortingBatchGroupId(rs.getInt(1));
					sbgsBean.setSortingBatchOrderId(rs.getInt(2));
					sbgsBean.setProductCount(rs.getInt(3));
					sbgsBean.setSortingBatchCompleteTime(rs.getString(4));
					sortingBatchGroupStatList.add(sbgsBean);
				}
				rs.close();
				
				for(int i=2;i<secondCount+5;i+=3){
					statArray[0][i]=sdf.format(cal1.getTime()).substring(0,10);
					cal1.add(Calendar.DAY_OF_YEAR, 1);
					statArray[1][i]="波次数";
					statArray[1][i+1]="订单数";
					statArray[1][i+2]="商品件数";
				}
				statArray[0][statArray[0].length-3]="总计";
				statArray[1][statArray[0].length-3]="波次数";
				statArray[1][statArray[0].length-2]="订单数";
				statArray[1][statArray[0].length-1]="商品件数";
				for(int i=2;i<statArray.length-1;i++){
					statArray[i][0]=(i-2)+":00";
					statArray[i][1]=(i-2)+":59";
				}
//				if(statArray.length<27){
//					statArray[statArray.length-2][1]=sdf.format(now.getTime()).substring(11,16);
//				}
				statArray[statArray.length-1][0]="总计";
				Map<String,String> orderMap = new HashMap<String,String>();
				Map<String, String> groupMap = new HashMap<String, String>();
				int x = sortingBatchGroupStatList.size();
				for(int i=0;i<x;i++){
					/*AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
					int orderId=apBean.getOrderId();//订单编号
					String auditPackageDatetime=apBean.getAuditPackageDatetime();//贴单时间
					String date=auditPackageDatetime.substring(0,10);//贴单日期
					int hour=Integer.parseInt(auditPackageDatetime.substring(11,13));//小时数
*/					SortingBatchGroupStatBean sbgsBean = sortingBatchGroupStatList.get(i);
					String sortingGroupDatetime = sbgsBean.getSortingBatchCompleteTime();//贴单时间
					String date = sortingGroupDatetime.substring(0,10);
					int hour = Integer.parseInt(sortingGroupDatetime.substring(11, 13));
					String sboId=sbgsBean.getSortingBatchOrderId() + "";//订单编号
					String batchGroupId = sbgsBean.getSortingBatchGroupId() + "";//波次id
					int productCount=sbgsBean.getProductCount();//该订单中的商品数量
//					int productCount=0;//该订单中的商品数量
//					OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
//					if(orderStock==null){
//						continue;
//					}
//					//该订单商品列表
//					List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
//					for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
//						OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
//						int count=orderProduct.getStockoutCount();
//						productCount+=count;
//					}
					
					for(int j=2;j<secondCount+5;j+=3){
						if(statArray[0][j].equals(date)){
							//波次数 需校准
							if(statArray[hour+2][j]==null){//波次数
								statArray[hour+2][j]="0";
							}
							
							//订单数需校准
							if(statArray[hour+2][j+1]==null){//订单数
								statArray[hour+2][j+1]="0";
							}
								
							
							if(statArray[hour+2][j+2]==null){//产品个数
								statArray[hour+2][j+2]="0";
							}
							
							//波次数需校准
							if(statArray[hour+2][statArray[0].length-3]==null){//横排总计波次数
								statArray[hour+2][statArray[0].length-3]="0";
							}
								
							//订单数需校准
							if(statArray[hour+2][statArray[0].length-2]==null){//横排总计订单数
								statArray[hour+2][statArray[0].length-2]="0";
							}
							
							
							if(statArray[hour+2][statArray[0].length-1]==null){//横排总计产品个数
								statArray[hour+2][statArray[0].length-1]="0";
							}
								
							
							if(statArray[statArray.length-1][j]==null){//纵排总计波次数
								statArray[statArray.length-1][j]="0";
							}
							
							if(statArray[statArray.length-1][j+1]==null){//纵排总计订单数
								statArray[statArray.length-1][j+1]="0";
							}
							
							
							if(statArray[statArray.length-1][j+2]==null){//纵排总计产品个数
								statArray[statArray.length-1][j+2]="0";
							}
							
							
							if(statArray[statArray.length-1][statArray[0].length-3]==null){//总计波次数
								statArray[statArray.length-1][statArray[0].length-3]="0";
							}
							
							if(statArray[statArray.length-1][statArray[0].length-2]==null){//总计订单数
								statArray[statArray.length-1][statArray[0].length-2]="0";
							}
							
							if( !groupMap.containsKey(batchGroupId+"")) {
								
								int count=Integer.parseInt(statArray[hour+2][j]);
								count++;
								statArray[hour+2][j]=count+"";  //当行
								
								int count2=Integer.parseInt(statArray[statArray.length-1][j]);
								count2++;
								statArray[statArray.length-1][j]=count2+"";   //列总
								
								int count3=Integer.parseInt(statArray[hour+2][statArray[0].length-3]);
								count3++;
								statArray[hour+2][statArray[0].length-3]=count3+"";  //行总
								
								int count4=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-3]);
								count4++;
								statArray[statArray.length-1][statArray[0].length-3]=count4+""; //总总
								
								groupMap.put(batchGroupId+"", "");
							}
							
							
							if( !orderMap.containsKey(sboId+"")) {
								int count=Integer.parseInt(statArray[hour+2][j+1]);
								count++;
								statArray[hour+2][j+1]=count+"";   //单行
								
								int count2=Integer.parseInt(statArray[statArray.length-1][j+1]);   //列总
								count2++;
								statArray[statArray.length-1][j+1]=count2+"";
								
								int count3=Integer.parseInt(statArray[hour+2][statArray[0].length-2]);  //行总
								count3++;
								statArray[hour+2][statArray[0].length-2]=count3+"";
								
								
								int count4=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-2]);
								count4++;
								statArray[statArray.length-1][statArray[0].length-2]=count4+"";  //总总
								
								orderMap.put(sboId+"", "");
								
							}
							
							if(statArray[statArray.length-1][statArray[0].length-1]==null){//总计产品个数
								statArray[statArray.length-1][statArray[0].length-1]="0";
							}
							int count=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
							count+=productCount;
							statArray[statArray.length-1][statArray[0].length-1]=count+"";
							
							
							int count2=Integer.parseInt(statArray[statArray.length-1][j+2]);
							count2+=productCount;
							statArray[statArray.length-1][j+2]=count2+"";
							
							int count3=Integer.parseInt(statArray[hour+2][statArray[0].length-1]);
							count3+=productCount;
							statArray[hour+2][statArray[0].length-1]=count3+"";
							
							int count4=Integer.parseInt(statArray[hour+2][j+2]);
							count4+=productCount;
							statArray[hour+2][j+2]=count4+"";
						}
					}
				}
				String[] arrs = new String[]{"sorting_batch_batchtimes","sorting_batch_orders","sorting_batch_counts"};
				StringBuilder sb = new StringBuilder();//列
				StringBuilder ss = new StringBuilder();//行
				sb.append("[[");
				ss.append("[");
				
				for(int i=0,z=0,t=0;i<statArray.length;i++){
					if(i!=0&&i!=1){
						ss.append("{");
					}
					for(int j=0;j<statArray[i].length;j++){
						if(i==0){
							if(j==0){
								sb.append("{field:'',title:\'分播时间段\',colspan:2,align:\'center\'},");
							}
							if((j-2)%3==0){
								//处理第一行
								sb.append("{field:'',title:\'"+statArray[i][j]+"\',width:100,align:\'center\', colspan:3,align:\'center\'},");
							}
							if(j==statArray[i].length-1){
								sb.deleteCharAt(sb.length()-1);
								sb.append("],[");
							}
						}else if(i==1){
							if(j==0){ 
								sb.append("{field:\'start\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'},");
							}else if(j==1){
								sb.append("{field:\'end\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'},");
							}else{
								sb.append("{field:\'"+arrs[t++]+j+"\',title:\'"+statArray[i][j]+"\',width:80,align:\'center\'}");
								if(j==statArray[i].length-1){
									sb.append("]]");
								}else{
									sb.append(",");
								}
								t = t%3;
							}
						}else{
							if(j==0){
								ss.append("\'start\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
							}else if(j==1){
								ss.append("\'end\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
							}
							else{
								ss.append("\'"+arrs[z++]+j+"\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\'");
								if(j==statArray[i].length-1){
									ss.append("},");
								}else{
									ss.append(",");
								}
								z = z%3;
							}
						}
					}
				}
				ss.deleteCharAt(ss.length()-1);
				ss.append("]");
//				resultMap.put("rows", ss.toString());
//				resultMap.put("columns", sb.toString());
				request.setAttribute("dateStart", dateStart);
				request.setAttribute("dateEnd", dateEnd);
				request.setAttribute("statArray", statArray);
				String re = "{\'rows\':"+ss.toString()+",\'columns\':"+sb.toString()+"}";
				response.getWriter().write(re);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				stockService.releaseAll();
			}
		}
		return null;
	}
	@RequestMapping("/auditPackageStatByDateJsp")
	public String auditPackageStatByDateJsp(HttpServletRequest request,HttpServletResponse response){
		Calendar now=Calendar.getInstance();
		String year=now.get(Calendar.YEAR)+"";
		String month=(now.get(Calendar.MONTH)+1)+"";
		request.setAttribute("month", month);
		request.setAttribute("year", year);
		return "forward:/admin/rec/stat/auditPackageStatByDate.jsp";
	}
	/**
	 * 每日贴单量统计
	 * 2013-9-10
	 * 朱爱林	
	 */
	@RequestMapping("/auditPackageStatByDate")
	public String auditPackageStatByDate(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return null;
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
//				request.setAttribute("tip", "请选择本月及以前的日期！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
				response.getWriter().write("{\'result\':\'failure\',\'tip\':\'请选择本月及以前的日期！\'}");
				return null;
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
			//auditPackageList=stockService.getAuditPackageList("audit_package_datetime>='"+sqlDate1+"' and audit_package_datetime<'"+sqlDate2+"' and audit_package_datetime<'"+sqlDate3+"'", -1, -1, null);
			
			String apSql="select ap.order_id,ap.audit_package_user_name,ap.audit_package_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
			" where audit_package_datetime>='"+sqlDate1+"' and audit_package_datetime<'"+sqlDate2+"' and audit_package_datetime<'"+sqlDate3+"'"+
			" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
			ResultSet rs=dbOp.executeQuery(apSql);
			while(rs.next()){
				AuditPackageBean apBean=new AuditPackageBean();
				apBean.setOrderId(rs.getInt(1));
				apBean.setAuditPackageUserName(rs.getString(2));
				apBean.setAuditPackageDatetime(rs.getString(3));
				auditPackageList.add(apBean);
			}
			rs.close();
			
			HashMap statMap=new HashMap();//统计表，key:name，value:一维数组，长度dayCount
			int userCount=0;//人数
			for(int i=0;i<auditPackageList.size();i++){
				AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
				String auditPackageUserName=apBean.getAuditPackageUserName().toLowerCase();//贴单人姓名
				String auditPackageDatetime=apBean.getAuditPackageDatetime();//贴单时间
				
//				int orderId=apBean.getOrderId();//订单编号
//				int productCount=0;//该订单中的商品数量
//				OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
//				if(orderStock==null){
//					continue;
//				}
//				//该订单商品列表
//				List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
//				for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
//					OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
//					int count=orderProduct.getStockoutCount();
//					productCount+=count;
//				}
				int day=Integer.parseInt(auditPackageDatetime.substring(8,10));//贴单时间中的天
				
				int[] array1=new int[dayCount];
				//int[] array2=new int[dayCount];
				if(statMap.containsKey(auditPackageUserName)){//已有此人数据
					array1=(int[])statMap.get(auditPackageUserName);
					//array2=(int[])statMap.get(auditPackageUserName+"2");
				}else{//无此人数据，初始化数组
					for(int j=0;j<array1.length;j++){
						array1[j]=0;
					}
//					for(int j=0;j<array2.length;j++){
//						array2[j]=0;
//					}
					userCount++;
				}
				array1[day-1]++;
				//array2[day-1]+=productCount;
				statMap.put(auditPackageUserName, array1);
				//statMap.put(auditPackageUserName+"2", array2);
			}
			
			String[][] statArray=new String[dayCount+2][userCount+2];//最终表
			Calendar showCal=Calendar.getInstance();//用于表格第一列的日期显示
			showCal.setTime(dateStart.getTime());
			for(int i=1;i<statArray.length-1;i++){
				statArray[i][0]=sdf.format(showCal.getTime()).substring(0,10);
				showCal.add(Calendar.DAY_OF_YEAR, 1);
			}
			statArray[0][0]="贴单日期";
			statArray[statArray.length-1][0]="总计";
			Iterator iter=statMap.keySet().iterator();
			int arrayIndex=0;//最终表第二坐标
			while(iter.hasNext()){
				String key=iter.next().toString();
				//if(key.endsWith("1")){
					statArray[0][arrayIndex+1]=key;
//					statArray[1][arrayIndex+1]="订单数";
//					statArray[1][arrayIndex+2]="产品个数";
					int[] array1=(int[])statMap.get(key);
//					int[] array2=(int[])statMap.get(key.substring(0,key.length()-1)+"2");
					for(int i=0;i<dayCount;i++){
						statArray[i+1][arrayIndex+1]=array1[i]+"";
						//statArray[i+2][arrayIndex+2]=array2[i]+"";
						
						//总计
						if(statArray[i+1][statArray[0].length-1]==null){
							statArray[i+1][statArray[0].length-1]="0";
						}
						int sum1=Integer.parseInt(statArray[i+1][statArray[0].length-1]);//右边订单数总计
						sum1+=array1[i];
						statArray[i+1][statArray[0].length-1]=sum1+"";
//						if(statArray[i+1][statArray[0].length-1]==null){
//							statArray[i+2][statArray[0].length-1]="0";
//						}
//						int sum2=Integer.parseInt(statArray[i+2][statArray[0].length-1]);//右边商品个数总计
//						sum2+=array2[i];
//						statArray[i+2][statArray[0].length-1]=sum2+"";
						if(statArray[statArray.length-1][arrayIndex+1]==null){
							statArray[statArray.length-1][arrayIndex+1]="0";
						}
						int sum3=Integer.parseInt(statArray[statArray.length-1][arrayIndex+1]);//下边订单数总计
						sum3+=array1[i];
						statArray[statArray.length-1][arrayIndex+1]=sum3+"";
//						if(statArray[statArray.length-1][arrayIndex+2]==null){
//							statArray[statArray.length-1][arrayIndex+2]="0";
//						}
//						int sum4=Integer.parseInt(statArray[statArray.length-1][arrayIndex+2]);
//						sum4+=array2[i];
//						statArray[statArray.length-1][arrayIndex+2]=sum4+"";
						
						if(statArray[statArray.length-1][statArray[0].length-1]==null){
							statArray[statArray.length-1][statArray[0].length-1]="0";
						}
						int sum5=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);//右下角订单数总计
						sum5+=array1[i];
						statArray[statArray.length-1][statArray[0].length-1]=sum5+"";
//						if(statArray[statArray.length-1][statArray[0].length-1]==null){
//							statArray[statArray.length-1][statArray[0].length-1]="0";
//						}
//						int sum6=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
//						sum6+=array2[i];
//						statArray[statArray.length-1][statArray[0].length-1]=sum6+"";
					}
					arrayIndex+=1;
				//}
			}
			statArray[0][arrayIndex+1]="总计";
//			statArray[1][arrayIndex+1]="订单数";
//			statArray[1][arrayIndex+2]="产品个数";
			
			String[] arrs = new String[]{"sorting_batch_batchtimes"};
			StringBuilder sb = new StringBuilder();//列
			StringBuilder ss = new StringBuilder();//行
			sb.append("[[");
			ss.append("[");
			String root = request.getContextPath();
			StringBuilder param = new StringBuilder();
			for(int i=0;i<statArray.length;i++){
				if(i!=0){
					ss.append("{");
				}
				for(int j=0;j<statArray[i].length;j++){
					if(i==0){
						if(j==0){
							sb.append("{field:'sorting_batch_time',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'},");
						}else if(j==statArray[i].length-1){
							sb.append("{field:'"+arrs[0]+j+"',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'}]]");
						}else{
							//处理第一行
							//method=sortingGroupStatByName&userName=<%=statArray[i][j]==null?"":statArray[i][j] %>
							//&dateStart=<%=year %>-<%=month<10?"0"+month:month %>-01
							//&dateEnd=<%=year %>-<%=month<10?"0"+month:month %>-<%=dayCount<10?"0"+dayCount:dayCount %>" target="_blank" >
							param.delete(0, param.length());
							param.append("?status=1&userName=").append(statArray[i][j]==null?"":statArray[i][j])
								.append("&dateStart=").append(year).append("-").append(Integer.parseInt(month)<10?"0"+month:month)
								.append("-01").append("&dateEnd=").append(year).append("-").append(Integer.parseInt(month)<10?"0"+month:month)
								.append("-").append(dayCount<10?"0"+dayCount:dayCount);
							sb.append("{field:'"+arrs[0]+j+"',title:\'<a href=\""+root+"/sortingGroupStatController/auditPackageStatByNameJsp.mmx"+param.toString()+"\" target=\"_blank\">"+statArray[i][j]+"</a>\',width:100,align:\'center\', colspan:1},");
						}
					}else{
						if(j==0){
							//method=sortingGroupStatByHour&date=<%=statArray[i][j]==null?"0":statArray[i][j] %>" target="_blank"
							if(i==statArray.length-1){
								ss.append("\'sorting_batch_time\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
							}else{
								param.delete(0, param.length());
								param.append("?date=").append(statArray[i][j]==null?"0":statArray[i][j]);
								ss.append("\'sorting_batch_time\':\'<a href=\""+root+"/sortingGroupStatController/auditPackageStatByHourJsp.mmx"+param.toString()+"\" target=\"_blank\">").append(statArray[i][j]==null?"0":statArray[i][j]).append("</a>\',");
							}
						}else{
							ss.append("\'"+arrs[0]+j+"\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\'");
							if(j==statArray[i].length-1){
								ss.append("},");
							}else{
								ss.append(",");
							}
						}
					}
				}
			}
			ss.deleteCharAt(ss.length()-1);
			ss.append("]");
//			resultMap.put("rows", ss.toString());
//			resultMap.put("columns", sb.toString());
			request.setAttribute("dayCount", dayCount+"");
			request.setAttribute("statArray", statArray);
			request.setAttribute("month", month);
			request.setAttribute("year", year);
			request.setAttribute("thisYear", now.get(Calendar.YEAR)+"");//用于确定今年年份
			String re = "{\'rows\':"+ss.toString()+",\'columns\':"+sb.toString()+"}";
			response.getWriter().write(re);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stockService.releaseAll();
		}
		if(request.getParameter("isExport")!=null&&request.getParameter("isExport").equals("1")){
			return "forward:/admin/rec/stat/auditPackageStatByDateExcel.jsp";
		}
		return null;
	}
	@RequestMapping("/auditPackageStatByHourJsp")
	public String auditPackageStatByHourJsp(HttpServletRequest request,HttpServletResponse response){
		String date=StringUtil.convertNull(request.getParameter("date")).trim();
		if("".equals(date)){
			date=DateUtil.getNowDateStr();
		}
		request.setAttribute("date", date);
		return "forward:/admin/rec/stat/auditPackageStatByHour.jsp";
	}
	/**
	 * 每小时贴单量统计
	 * 2013-9-10
	 * 朱爱林
	 */
	@RequestMapping("/auditPackageStatByHour")
	public String auditPackageStatByHour(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return null;
		}
		UserGroupBean group = user.getGroup();
		
		String date=StringUtil.convertNull(request.getParameter("date")).trim();//查询日期
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		if(area.equals("")){
			area=areaAll;
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
//				request.setAttribute("tip", "只能查询今年和去年的数据！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
				response.getWriter().write("{\'result\':\'failure\',\'tip\':\'只能查询今年和去年的数据！\'}");
				return null;
			}
			if(cal.after(now)){
//				request.setAttribute("tip", "请选择今天及以前的日期！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
				response.getWriter().write("{\'result\':\'failure\',\'tip\':\'请选择今天及以前的日期！\'}");
				return null;
			}
			HashMap statMap=new HashMap();//统计表，key:name1，value:一维数组，长度dayCount
			int userCount=0;//人数
			String sql="";
			
			String sqlDate3=sdf.format(now.getTime());
			sql="audit_package_datetime>='"+date+" 00:00:00' and audit_package_datetime<='"+date+" 23:59:59' and audit_package_datetime<='"+sqlDate3+"'";
//			List auditPackageList=stockService.getAuditPackageList(sql, -1, -1, null);
			List auditPackageList=new ArrayList();
			
			String apSql="select ap.order_id,ap.audit_package_user_name,ap.audit_package_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
			" where audit_package_datetime>='"+date+" 00:00:00' and audit_package_datetime<='"+date+" 23:59:59' and audit_package_datetime<='"+sqlDate3+"'"+
			" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
			ResultSet rs=dbOp.executeQuery(apSql);
			while(rs.next()){
				AuditPackageBean apBean=new AuditPackageBean();
				apBean.setOrderId(rs.getInt(1));
				apBean.setAuditPackageUserName(rs.getString(2));
				apBean.setAuditPackageDatetime(rs.getString(3));
				auditPackageList.add(apBean);
			}
			rs.close();
			
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
				String auditPackageUserName=apBean.getAuditPackageUserName().toLowerCase();//贴单人姓名
				String auditPackageDatetime=apBean.getAuditPackageDatetime();//贴单时间
				
//				int productCount=0;//该订单中的商品数量
//				OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
//				if(orderStock==null){
//					continue;
//				}
//				//该订单商品列表
//				List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
//				for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
//					OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
//					int count=orderProduct.getStockoutCount();
//					productCount+=count;
//				}
				int hour=Integer.parseInt(auditPackageDatetime.substring(11,13));//贴单时间中的 小时
				
				int[] array1=new int[hourCount];
//				int[] array2=new int[hourCount];
				if(statMap.containsKey(auditPackageUserName)){//已有此人数据
					array1=(int[])statMap.get(auditPackageUserName);
//					array2=(int[])statMap.get(auditPackageUserName+"2");
				}else{//无此人数据，初始化数组
					for(int j=0;j<array1.length;j++){
						array1[j]=0;
					}
//					for(int j=0;j<array2.length;j++){
//						array2[j]=0;
//					}
					userCount++;
				}
				array1[hour]++;
//				array2[hour]+=productCount;
				statMap.put(auditPackageUserName, array1);
//				statMap.put(auditPackageUserName+"2", array2);
			}
			
			String[][] statArray=new String[hourCount+2][userCount+3];//最终表
			statArray[0][0]="起始时间";
			statArray[0][1]="截止时间";
//			statArray[1][0]="起始";
//			statArray[1][1]="截止";
			//第一列日期显示
			for(int i=1;i<statArray.length-1;i++){
				statArray[i][0]=(i-1)+":00";
				statArray[i][1]=(i-1)+":59";
			}
			statArray[statArray.length-1][0]="总计";
			Iterator iter=statMap.keySet().iterator();
			int arrayIndex=1;//最终表第二坐标
			while(iter.hasNext()){
				String key=iter.next().toString();
//				if(key.endsWith("1")){
					statArray[0][arrayIndex+1]=key;
//					statArray[1][arrayIndex+1]="订单数";
//					statArray[1][arrayIndex+2]="产品个数";
					int[] array1=(int[])statMap.get(key);
//					int[] array2=(int[])statMap.get(key.substring(0,key.length()-1)+"2");
					for(int i=0;i<hourCount;i++){
						statArray[i+1][arrayIndex+1]=array1[i]+"";
//						statArray[i+2][arrayIndex+2]=array2[i]+"";
						
						//总计
						if(statArray[i+1][statArray[0].length-1]==null){
							statArray[i+1][statArray[0].length-1]="0";
						}
						int sum1=Integer.parseInt(statArray[i+1][statArray[0].length-1]);
						sum1+=array1[i];
						statArray[i+1][statArray[0].length-1]=sum1+"";
						
//						if(statArray[i+2][statArray[0].length-1]==null){
//							statArray[i+2][statArray[0].length-1]="0";
//						}
//						int sum2=Integer.parseInt(statArray[i+2][statArray[0].length-1]);
//						sum2+=array2[i];
//						statArray[i+2][statArray[0].length-1]=sum2+"";
						
						if(statArray[statArray.length-1][arrayIndex+1]==null){
							statArray[statArray.length-1][arrayIndex+1]="0";
						}
						int sum3=Integer.parseInt(statArray[statArray.length-1][arrayIndex+1]);
						sum3+=array1[i];
						statArray[statArray.length-1][arrayIndex+1]=sum3+"";
						
//						if(statArray[statArray.length-1][arrayIndex+2]==null){
//							statArray[statArray.length-1][arrayIndex+2]="0";
//						}
//						int sum4=Integer.parseInt(statArray[statArray.length-1][arrayIndex+2]);
//						sum4+=array2[i];
//						statArray[statArray.length-1][arrayIndex+2]=sum4+"";
						
						if(statArray[statArray.length-1][statArray[0].length-1]==null){
							statArray[statArray.length-1][statArray[0].length-1]="0";
						}
						int sum5=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
						sum5+=array1[i];
						statArray[statArray.length-1][statArray[0].length-1]=sum5+"";
						
//						if(statArray[statArray.length-1][statArray[0].length-1]==null){
//							statArray[statArray.length-1][statArray[0].length-1]="0";
//						}
//						int sum6=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
//						sum6+=array2[i];
//						statArray[statArray.length-1][statArray[0].length-1]=sum6+"";
					}
					arrayIndex+=1;
//				}
			}
			statArray[0][arrayIndex+1]="总计";
//			statArray[1][arrayIndex+1]="订单数";
//			statArray[1][arrayIndex+2]="产品个数";
			String[] arrs = new String[]{"sorting_batch_batchtimes"};
			StringBuilder sb = new StringBuilder();//列
			StringBuilder ss = new StringBuilder();//行
			sb.append("[[");
			ss.append("[");
			for(int i=0;i<statArray.length;i++){
				if(i!=0){
					ss.append("{");
				}
				for(int j=0;j<statArray[i].length;j++){
					if(i==0){
						if(j==0){
							sb.append("{field:'start_time',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'},");
						}else if(j==1){
							sb.append("{field:'end_time',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'},");
						}else {
							if(j==statArray[i].length-1){
								sb.append("{field:'"+arrs[0]+j+"',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'}]]");
							}else{
								sb.append("{field:'"+arrs[0]+j+"',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'},");
							}
						}
					}else{
						if(j==0){
							//method=sortingGroupStatByHour&date=<%=statArray[i][j]==null?"0":statArray[i][j] %>" target="_blank"
							ss.append("\'start_time\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
						}else if(j==1){
							ss.append("\'end_time\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
						}else{
							ss.append("\'"+arrs[0]+j+"\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\'");
							if(j==statArray[i].length-1){
								ss.append("},");
							}else{
								ss.append(",");
							}
						}
					}
				}
			}
			ss.deleteCharAt(ss.length()-1);
			ss.append("]");
//			resultMap.put("rows", ss.toString());
//			resultMap.put("columns", sb.toString());
			request.setAttribute("date", date);
			request.setAttribute("statArray", statArray);
			String re = "{\'rows\':"+ss.toString()+",\'columns\':"+sb.toString()+"}";
			response.getWriter().write(re);
			request.setAttribute("date", date);
			request.setAttribute("statArray", statArray);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stockService.releaseAll();
		}
		return null;
	}
	@RequestMapping("/auditPackageStatByNameJsp")
	public String auditPackageStatByNameJsp(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		String userName=StringUtil.convertNull(request.getParameter("userName")).trim().toLowerCase();
		
		userName = new String(userName.getBytes("ISO8859-1"),"UTF-8");
		String dateStart=StringUtil.convertNull(request.getParameter("dateStart")).trim();
		String dateEnd=StringUtil.convertNull(request.getParameter("dateEnd")).trim();
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		String status=StringUtil.convertNull(request.getParameter("status"));//库地区
		request.setAttribute("userName", userName);
		request.setAttribute("dateStart", dateStart);
		request.setAttribute("dateEnd", dateEnd);
		request.setAttribute("area", area);
		request.setAttribute("status", status);
		
		return "forward:/admin/rec/stat/auditPackageStatByName.jsp";
	}
	/**
	 * 个人贴单量统计
	 * 2013-9-10
	 * 朱爱林	
	 */
	@RequestMapping("/auditPackageStatByName")
	public String auditPackageStatByName(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return null;
		}
		UserGroupBean group = user.getGroup();
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String userName=StringUtil.convertNull(request.getParameter("userName")).trim().toLowerCase();
		String dateStart=StringUtil.convertNull(request.getParameter("dateStart")).trim();
		String dateEnd=StringUtil.convertNull(request.getParameter("dateEnd")).trim();
		String area=StringUtil.convertNull(request.getParameter("area"));//库地区
		if(area.equals("")){
			area=areaAll;
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
//					request.setAttribute("tip", "起始日期必须早于结束日期！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'起始日期必须早于结束日期！\'}");
					return null;
				}
				if(cal2.get(Calendar.YEAR)>now.get(Calendar.YEAR)||
						(cal2.get(Calendar.YEAR)==now.get(Calendar.YEAR)&&cal2.get(Calendar.MONTH)>now.get(Calendar.MONTH))||
						(cal2.get(Calendar.YEAR)==now.get(Calendar.YEAR)&&cal2.get(Calendar.MONTH)==now.get(Calendar.MONTH)&&cal2.get(Calendar.DAY_OF_MONTH)>now.get(Calendar.DAY_OF_MONTH))
				){
//					request.setAttribute("tip", "请选择今天及以前的日期！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'请选择今天及以前的日期！\'}");
					return null;
				}
				int yearCount=now.get(Calendar.YEAR)-cal1.get(Calendar.YEAR);
				if(yearCount!=0&&yearCount!=1){
//					request.setAttribute("tip", "只能查询今年和去年的数据！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'只能查询今年和去年的数据！\'}");
					return null;
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
//					request.setAttribute("tip", "查看天数不能大于31天！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					response.getWriter().write("{\'result\':\'failure\',\'tip\':\'查看天数不能大于31天！\'}");
					return null;
				}
				String[][] statArray=new String[hourCount+2][dayCount+3];//最终表
				statArray[0][0]="起始时间";
				statArray[0][1]="截止时间";
//				statArray[1][0]="起始";
//				statArray[1][1]="截止";
				
				List auditPackageList=new ArrayList();
				
				String apSql="select ap.order_id,ap.audit_package_user_name,ap.audit_package_datetime from audit_package ap join order_stock os on os.order_id=ap.order_id" +
				" where audit_package_datetime>='"+sdf.format(cal1.getTime())+
						"' and audit_package_datetime<='"+sdf.format(cal2.getTime())+
						"' and audit_package_datetime<='"+sdf.format(now.getTime())+
						"' and audit_package_user_name='"+userName+"'"+
				" and os.status!="+OrderStockBean.STATUS4+" and os.stock_area in("+area+")";
				ResultSet rs=dbOp.executeQuery(apSql);
				while(rs.next()){
					AuditPackageBean apBean=new AuditPackageBean();
					apBean.setOrderId(rs.getInt(1));
					apBean.setAuditPackageUserName(rs.getString(2));
					apBean.setAuditPackageDatetime(rs.getString(3));
					auditPackageList.add(apBean);
				}
				rs.close();
				
				for(int i=2;i<statArray[0].length;i++){
					statArray[0][i]=sdf.format(cal1.getTime()).substring(0,10);
					cal1.add(Calendar.DAY_OF_YEAR, 1);
//					statArray[1][i]="订单数";
//					statArray[1][i+1]="产品个数";
				}
//				statArray[1][statArray[0].length-2]="订单数";
//				statArray[1][statArray[0].length-1]="产品个数";
				statArray[0][statArray[0].length-1]="总计";
				for(int i=1;i<statArray.length-1;i++){
					statArray[i][0]=(i-1)+":00";
					statArray[i][1]=(i-1)+":59";
				}
//				if(statArray.length<27){
//					statArray[statArray.length-2][1]=sdf.format(now.getTime()).substring(11,16);
//				}
				statArray[statArray.length-1][0]="总计";
				
				for(int i=0;i<auditPackageList.size();i++){
					AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
					int orderId=apBean.getOrderId();//订单编号
					String auditPackageDatetime=apBean.getAuditPackageDatetime();//贴单时间
					String date=auditPackageDatetime.substring(0,10);//贴单日期
					int hour=Integer.parseInt(auditPackageDatetime.substring(11,13));//小时数
					
//					int productCount=0;//该订单中的商品数量
//					OrderStockBean orderStock=stockService.getOrderStock("order_id="+orderId+" and status!=3");//出库记录
//					if(orderStock==null){
//						continue;
//					}
//					//该订单商品列表
//					List orderStockProductList=stockService.getOrderStockProductList("order_stock_id="+orderStock.getId(), -1, -1, null);
//					for(int j=0;j<orderStockProductList.size();j++){//计算该订单中商品数
//						OrderStockProductBean orderProduct=(OrderStockProductBean)orderStockProductList.get(j);
//						int count=orderProduct.getStockoutCount();
//						productCount+=count;
//					}
					
					for(int j=0;j<dayCount;j++){
						if(statArray[0][j+2].equals(date)){
							if(statArray[hour+1][j+2]==null){//订单数
								statArray[hour+1][j+2]="1";
							}else{
								int count=Integer.parseInt(statArray[hour+1][j+2]);
								count++;
								statArray[hour+1][j+2]=count+"";
							}
							
//							if(statArray[hour+2][2*(j+1)+1]==null){//产品个数
//								statArray[hour+2][2*(j+1)+1]=productCount+"";
//							}else{
//								int count=Integer.parseInt(statArray[hour+2][2*(j+1)+1]);
//								count+=productCount;
//								statArray[hour+2][2*(j+1)+1]=count+"";
//							}
							
							if(statArray[hour+1][statArray[0].length-1]==null){//横排总计订单数
								statArray[hour+1][statArray[0].length-1]="1";
							}else{
								int count=Integer.parseInt(statArray[hour+1][statArray[0].length-1]);
								count++;
								statArray[hour+1][statArray[0].length-1]=count+"";
							}
							
//							if(statArray[hour+2][statArray[0].length-1]==null){//横排总计产品个数
//								statArray[hour+2][statArray[0].length-1]=productCount+"";
//							}else{
//								int count=Integer.parseInt(statArray[hour+2][statArray[0].length-1]);
//								count+=productCount;
//								statArray[hour+2][statArray[0].length-1]=count+"";
//							}
							
							if(statArray[statArray.length-1][j+2]==null){//纵排总计订单数
								statArray[statArray.length-1][j+2]="1";
							}else{
								int count=Integer.parseInt(statArray[statArray.length-1][j+2]);
								count++;
								statArray[statArray.length-1][j+2]=count+"";
							}
							
//							if(statArray[statArray.length-1][2*(j+1)+1]==null){//纵排总计产品个数
//								statArray[statArray.length-1][2*(j+1)+1]=productCount+"";
//							}else{
//								int count=Integer.parseInt(statArray[statArray.length-1][2*(j+1)+1]);
//								count+=productCount;
//								statArray[statArray.length-1][2*(j+1)+1]=count+"";
//							}
							
							if(statArray[statArray.length-1][statArray[0].length-1]==null){//总计订单数
								statArray[statArray.length-1][statArray[0].length-1]="1";
							}else{
								int count=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
								count++;
								statArray[statArray.length-1][statArray[0].length-1]=count+"";
							}
							
//							if(statArray[statArray.length-1][statArray[0].length-1]==null){//总计产品个数
//								statArray[statArray.length-1][statArray[0].length-1]=productCount+"";
//							}else{
//								int count=Integer.parseInt(statArray[statArray.length-1][statArray[0].length-1]);
//								count+=productCount;
//								statArray[statArray.length-1][statArray[0].length-1]=count+"";
//							}
						}
					}
				}
				String[] arrs = new String[]{"sorting_batch_batchtimes"};
				StringBuilder sb = new StringBuilder();//列
				StringBuilder ss = new StringBuilder();//行
				sb.append("[[");
				ss.append("[");
				for(int i=0;i<statArray.length;i++){
					if(i!=0){
						ss.append("{");
					}
					for(int j=0;j<statArray[i].length;j++){
						if(i==0){
							if(j==0){
								sb.append("{field:'start_time',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'},");
							}else if(j==1){
								sb.append("{field:'end_time',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'},");
							}else {
								if(j==statArray[i].length-1){
									sb.append("{field:'"+arrs[0]+j+"',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'}]]");
								}else{
									sb.append("{field:'"+arrs[0]+j+"',title:\'"+statArray[i][j]+"\',colspan:1,align:\'center\'},");
								}
							}
						}else{
							if(j==0){
								//method=sortingGroupStatByHour&date=<%=statArray[i][j]==null?"0":statArray[i][j] %>" target="_blank"
								ss.append("\'start_time\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
							}else if(j==1){
								ss.append("\'end_time\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\',");
							}else{
								ss.append("\'"+arrs[0]+j+"\':\'").append(statArray[i][j]==null?"0":statArray[i][j]).append("\'");
								if(j==statArray[i].length-1){
									ss.append("},");
								}else{
									ss.append(",");
								}
							}
						}
					}
				}
				ss.deleteCharAt(ss.length()-1);
				ss.append("]");
//				resultMap.put("rows", ss.toString());
//				resultMap.put("columns", sb.toString());
				request.setAttribute("dateStart", dateStart);
				request.setAttribute("dateEnd", dateEnd);
				request.setAttribute("statArray", statArray);
				String re = "{\'rows\':"+ss.toString()+",\'columns\':"+sb.toString()+"}";
				response.getWriter().write(re);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				stockService.releaseAll();
			}
		}
		return null;
	}
}
