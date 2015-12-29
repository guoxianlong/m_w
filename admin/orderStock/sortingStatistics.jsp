<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.*"%>
<html>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<head>
<title>分拣量统计</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/dark-blue.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<script language="JavaScript" src="/adult-admin/js/My97DatePicker/WdatePicker.js"></script>
</head>
<%
String startTime = StringUtil.convertNull((String)request.getAttribute("startTime"));
String endTime = StringUtil.convertNull((String)request.getAttribute("endTime"));
String staffCode = StringUtil.convertNull((String)request.getAttribute("staffCode"));
String staffName = StringUtil.convertNull((String)request.getAttribute("staffName"));
SortingBatchGroupBean totalBean = (SortingBatchGroupBean)request.getAttribute("totalBean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
List staffList = (List)request.getAttribute("staffList");
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
<script type="text/javascript">
function fillCode(){
	var code = document.getElementById("staffName").value;
	document.getElementById("staffCode").value = code;
}
function fillTime(){
	document.getElementById("startTime").value="<%=startTime%>";
	document.getElementById("endTime").value="<%=endTime%>";
}
function ajaxUpdateName(){
	var startTime = document.getElementById("startTime").value;
	var endTime = document.getElementById("endTime").value;
	var nDay_ms=24*60*60*1000;
	var reg=new RegExp("-","g");
	var startDay=new Date(startTime.replace(reg,'/'));
	var endDay=new Date(endTime.replace(reg,'/'));
	var nDifTime=endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		alert("起始日期不能大于或等于结束日期！");
    	return false;
	}
    var nDifDay=Math.floor(nDifTime/nDay_ms);
    if(nDifDay > 30){
    	alert("日期间隔不能大于31天！");
    	return false;
    }
	startTime = startTime.replace(new RegExp("/","g"),'-');
	endTime = endTime.replace(new RegExp("/","g"),'-');
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingAjaxUpdateName&selectIndex=6&startTime="+ startTime + "&endTime="+endTime,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			$("#staffName").empty();
			$("#staffName").html(msg);
		}
	});
}
function ajaxUpdateNameByCode(){
	var staffCode = $("#staffCode").val();
	if(staffCode != ""){
		var startTime = document.getElementById("startTime").value;
		var endTime = document.getElementById("endTime").value;
		var nDay_ms=24*60*60*1000;
		var reg=new RegExp("-","g");
		var startDay=new Date(startTime.replace(reg,'/'));
		var endDay=new Date(endTime.replace(reg,'/'));
		var nDifTime=endDay.getTime()- startDay.getTime();
		if(nDifTime < 0){
			alert("起始日期不能大于或等于结束日期！");
    		return false;
		}
   	 	var nDifDay=Math.floor(nDifTime/nDay_ms);
    	if(nDifDay > 30){
    		alert("日期间隔不能大于31天！");
    		return false;
   	 	}
		startTime = startTime.replace(new RegExp("/","g"),'-');
		endTime = endTime.replace(new RegExp("/","g"),'-');
		$.ajax({
			type: "GET",
			url: "<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingAjaxUpdateNameByCode&staffCode="+ staffCode +"&selectIndex=7&startTime="+ startTime + "&endTime="+endTime,
			cache: false,
			dataType: "html",
			data: {type: "1"},
			success: function(msg, reqStatus){
				$("#staffName").empty();
				$("#staffName").html(msg);
				
			}
		});
	}
}
function checksubmit(){
	var startTime = document.getElementById("startTime").value;
	var endTime = document.getElementById("endTime").value;
	var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	if(startTime.length!=0&&endTime.length==0 ){
		alert("请输入起始时间")
		return false;
	}
	if(startTime.length==0&&endTime.length!=0 ){
	    alert("请输入截止时间")
		return false;
	}
	if(startTime.length!=0 && endTime.length!=0){
		if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
			  alert("请正确填写起始时间格式")
			 return false;
		} 
		if((endTime.length!=0 && endTime.length!=10) || !r.test(endTime)){
			alert("请正确填写截止时间格式")
			return false;
		} 
	}
	var nDay_ms=24*60*60*1000;
	var reg=new RegExp("-","g");
	var startDay=new Date(startTime.replace(reg,'/'));
	var endDay=new Date(endTime.replace(reg,'/'));
	var nDifTime=endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		alert("起始日期不能大于或等于结束日期！");
    	return false;
	}
    var nDifDay=Math.floor(nDifTime/nDay_ms);
    if(nDifDay > 30){
    	alert("日期间隔不能大于31天！");
    	return false;
    }
	return true;
}
</script>
<body onload="fillTime()">
	<form method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingStatisticsList" onSubmit="return checksubmit();">&nbsp;&nbsp;&nbsp;&nbsp;
		<input type=text name="startTime" id='startTime' size="10" value="" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){this.blur();}});"  onChange="ajaxUpdateName()" />至
		<input type=text name="endTime" id='endTime' size="10" value="" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){this.blur();}});"
		 onChange="ajaxUpdateName()" />&nbsp;&nbsp;&nbsp;&nbsp;
		
		姓名:
		<select name="staffName" id="staffName" onChange="fillCode()">
			<% if(!"".equals(staffName)){ %>
				<option value="<%=staffCode%>"><%=staffName%></option>
				<option value="">全部</option>
			<% }else{ %>
				<option value="">全部</option>
			<%if(staffList!=null) {		
			for(int i=0;i<staffList.size();i++){
						SortingBatchGroupBean bean = (SortingBatchGroupBean)staffList.get(i);
			%>
						<option value="<%=bean.getStaffCode()%>"><%=bean.getStaffName()%></option>
			<% 		} }
			   }%>	
		</select> 
		员工号:<input type='text'id="staffCode"  name='staffCode' onblur="javascript:return ajaxUpdateNameByCode()" value='<%=staffCode  %>'/>&nbsp;&nbsp;
		<input type='submit' value='查询' onclick="javascript:return checksubmit()"/><br><br>
		<table  width="99%" border="0" cellpadding="3" cellspacing="1" bgcolor="#4c6e92" align="center">
			<%if(totalBean!=null){%>
			 <tr bgcolor="Yellow">
				<td><div align="center">总数:</div></td>
				<td><div align="center"><%=totalBean.getStaffCount() %></div></td>
				<td><div align="center"><%=totalBean.getAttendanceCount() %></div></td>
				<td><div align="center"><%=totalBean.getGroupCount() %></div></td>
				<td><div align="center"><%=totalBean.getOrderCount() %></div></td>
				<td><div align="center"><%=totalBean.getSkuCount() %></div></td>
				<td><div align="center"><%=totalBean.getProductCount() %></div></td>
				<td><div align="center"><%=totalBean.getPassageCount() %></div></td>
				<td><div align="center"><%=totalBean.getSkuRowCount() %></div></td>
				<td>&nbsp;</td>
			</tr><%}%>
			<tr bgcolor="#e8e8e8">
				<td><div align="center"><strong>姓名</strong></div></td>
				<td><div align="center"><strong>员工号</strong></div></td>
				<td><div align="center"><strong>出勤天数</strong></div></td>
				<td><div align="center"><strong>波次数</strong></div></td>
				<td><div align="center"><strong>订单数</strong></div></td>
				<td><div align="center"><strong>SKU数</strong></div></td>
				<td><div align="center"><strong>商品个数</strong></div></td>
				<td><div align="center"><strong>巷道数</strong></div></td>
				<td><div align="center"><strong>SKU行</strong></div></td>
				<td><div align="center"><strong>操作</strong></div></td>
			</tr>
			<%if(staffList!=null){
			for(int i=0;i<staffList.size();i++){
				SortingBatchGroupBean bean = (SortingBatchGroupBean)staffList.get(i);
			%>
			<tr bgcolor="#e8e8e8">
				<td><div align="center"><%=bean.getStaffName() %></div></td>
				<td><div align="center"><%=bean.getStaffCode() %></div></td>
				<td><div align="center"><%=bean.getAttendanceCount() %></div></td>
				<td><div align="center"><%=bean.getGroupCount() %></div></td>
				<td><div align="center"><%=bean.getOrderCount() %></div></td>
				<td><div align="center"><%=bean.getSkuCount() %></div></td>
				<td><div align="center"><%=bean.getProductCount() %></div></td>
				<td><div align="center"><%=bean.getPassageCount() %></div></td>
				<td><div align="center"><%=bean.getSkuRowCount() %></div></td>
				<td><div align="center"> <% if(group.isFlag(645)){ %><a href="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingStatisticalDetailed&staffCode=<%=bean.getStaffCode()%>&startTime=<%=startTime%>&endTime=<%=endTime%>&staffName=<%=bean.getStaffName() %>">明细</a><%} %></div></td>
			</tr>
			<%}}%>
		</table><br>
	</form>
	<form method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingStatisticsListExcel&startTime=<%=startTime%>&endTime=<%=endTime%>&staffName=<%=staffName%>&staffCode=<%=staffCode%>">&nbsp;&nbsp;&nbsp;&nbsp;
		<input type='submit' value='导出'/>
	</form>
</body>
<%if (paging!=null){%>
	<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</html>