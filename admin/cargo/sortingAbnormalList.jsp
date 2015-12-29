<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="mmb.stock.stat.formbean.ReturnedPackageFBean" %>
<%
List list = (ArrayList) request.getAttribute("list");
String pageLine = (String) request.getAttribute("pageLine");
int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
int status = ProductWarePropertyService.toInt(request.getParameter("status"));
String startTime = StringUtil.convertNull(request.getParameter("startTime"));
String endTime = StringUtil.convertNull(request.getParameter("endTime"));
int type = ProductWarePropertyService.toInt(request.getParameter("type"));
String code = StringUtil.convertNull(request.getParameter("code"));
PagingBean paging = (PagingBean)request.getAttribute("paging");
String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request,wareArea);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>异常单列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<style type="text/css" >
	#submitButton {
		width:80px;
	}
</style>
<script type="text/javascript" >

	function compareDate() {
	   var time1 = document.getElementById("startTime");
	   var time2 = document.getElementById("endTime");
	   if( (time1.value == "" && time2.value != "") || (time1.value != "" && time2.value == "")) {
			document.getElementById("time_span_compare").innerHTML="<font color='red'>开始时间和结束时间需要成对填写</font>";
			return false;
	   }else {
		   	if ( time1.value != "" && time2.value != "" && time2.value >= time1.value) {
		    	return true;
		   	} else if( time1.value != "" && time2.value != "" && time1.value > time2.value ) {
		   		document.getElementById("time_span_compare").innerHTML="<font color='red'>后面的日期需要大于或等于前面的日期</font>";
		   		return false;
	   		}
		}
 	}
 	function clearTimeSpan() {
 		document.getElementById("time_span_compare").innerHTML="";
 	}
</script>
</head>
<body style="text-align:center;">
<div align="center">
<form action="<%= request.getContextPath()%>/admin/sortingAbnormalDispose.do?method=getSortingAbnormalInfo" method="post" onsubmit="return compareDate();">
<%= wareAreaSelectLable %>  
&nbsp;&nbsp;&nbsp; 
<select name="status" >
	<option value="0" <%= status == 0 ? "selected" : ""%>>未处理</option>
	<option value="1" <%= status == 1 ? "selected" : ""%>>处理中</option>
	<option value="2" <%= status == 2 ? "selected" : ""%>>无异常</option>
	<option value="3" <%= status == 3 ? "selected" : ""%>>待盘点</option>
	<option value="4" <%= status == 4 ? "selected" : ""%>>盘点中</option>
	<option value="5" <%= status == 5 ? "selected" : ""%>>已盘点</option>
</select>
&nbsp;&nbsp;&nbsp; 
<input type="text" name="startTime" id="startTime" value="<%= startTime%>" onclick="WdatePicker();" onfocus="clearTimeSpan();"/>
至
<input type="text" name="endTime" id="endTime" value="<%= endTime%>" onclick="WdatePicker();" onfocus="clearTimeSpan();"/>
<span id="time_span_compare"></span>

&nbsp;&nbsp;&nbsp; 
<select name="type" >
	<option value="-1" <%= type == -1 ? "selected" : ""%>>类型</option>
	<option value="0" <%= type == 0 ? "selected" : ""%>>撤单</option>
	<option value="1" <%= type == 1 ? "selected" : ""%>>分拣货位异常</option>
	<option value="2" <%= type == 2 ? "selected" : ""%>>分拣SKU错误</option>
</select>
&nbsp;&nbsp;&nbsp; 
<input type="text" name="code" id="code" <%if(code!=null&&code.length()>0) {%>value="<%= code %>"<%}else{%>value="货位号/商品编号"/<%} %> onfocus="if(this.value=='货位号/商品编号'){this.value=''}"/>
&nbsp;&nbsp;&nbsp; 
&nbsp;&nbsp;&nbsp; 
&nbsp;&nbsp;&nbsp; 
&nbsp;&nbsp;&nbsp; 
<input type="submit" value="查询" id="submitButton" />
</form>
</div>
<br/>
<br/>
<br/>
<div align="center">
	<%
		if( list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++ ) {
			
				SortingAbnormalBean saBean = (SortingAbnormalBean) list.get(i);
	%>
	<table align= "center" width= "92%" border= "0" cellspacing= "1px" bgcolor="#D8D8D5" cellpadding="1px" >
             <tbody>
             <tr bgcolor= "#484891" >
                   <td align= "center" colspan="2">
                   <font color= "#FFFFFF">异常处理单号</font>
                   </td>
                   <td align= "center">
                   <font color= "#FFFFFF">作业单号</font>
                   </td>
                   <td align= "center">
                   <font color= "#FFFFFF">作业单类型</font>
                   </td>
                   <td align= "center">
                   <font color= "#FFFFFF">异常类型</font>
                   </td>
                   <td align= "center">
                   <font color= "#FFFFFF">异常处理状态</font>
                   </td>
             </tr>
             <tr bgcolor= "#FFFFFF" >
                   <td align= "center" colspan="2">
                   <%= StringUtil.convertNull(saBean.getCode()) %>
                   </td>
                   <td align= "center">
                  <%= StringUtil.convertNull(saBean.getOperCode())%>
                   </td>
                   <td align= "center">
                   <%= saBean.getOperTypeName()%>
                   </td>
                   <td align= "center">
                   <%= saBean.getAbnormalTypeName()%>
                   </td>
                   <td align= "center">
                   <%= saBean.getStatusName()%>
                   </td>
             </tr>
             <tr bgcolor= "#FFFFFF" >
                   <td align= "center">
                   产品编号
                   </td>
                   <td align= "center">
                   货位
                   </td>
                   <td align= "center">
                   未处理数
                   </td>
                   <td align= "center">
                   总数
                   </td>
                   <td align= "center">
                   冻结量
                   </td>
                   <td align= "center">
                   状态
                   </td>
             </tr>
             <%
             	List<SortingAbnormalProductBean> sortingAbnormalProducts = saBean.getSortingAbnormalProductList();
             	if( sortingAbnormalProducts != null && sortingAbnormalProducts.size() != 0 ) {
             		int y = sortingAbnormalProducts.size();
             		for( int j = 0; j < y; j++ ) {
             			SortingAbnormalProductBean sapBean = sortingAbnormalProducts.get(j);
             %>
              <tr bgcolor= "#FFFFFF" >
                   <td align= "center">
                   <%= sapBean.getProductCode()%>
                   </td>
                   <td align= "center">
                   <%= sapBean.getCargoWholeCode()%>
                   </td>
                   <td align= "center">
                   <%= sapBean.getLockCount()%>
                   </td>
                   <td align= "center">
                   <%= sapBean.getCount()%>
                   </td>
                   <td align= "center">
                   <%= sapBean.getLockCount()%>
                   </td>
                   <td align= "center">
                   <%= sapBean.getStatusName()%>
                   </td>
             </tr>
             <%
             	}
             	} else {
             %>
             <tr bgcolor= "#FFFFFF" >
               <td align= "center" colspan="6">
              	该异常单没有商品信息
               </td>
             </tr>
             <%
             	}
             %>
             </tbody>
   </table>
   <br/>
   <%
   	}
   	} else {
   %>
   	暂时没有异常单信息或没有符合条件的异常单信息
   <%
   	}
   %>
	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</div>

</body>
</html>