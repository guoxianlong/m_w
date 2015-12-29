<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="mmb.stock.stat.ProductWarePropertyService" %>
<%@ page import="adultadmin.util.StringUtil" %>
<%
	int wareArea = request.getParameter("wareArea")==null?-1:Integer.valueOf(request.getParameter("wareArea")).intValue();
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptionsAllWithRight(request, wareArea);
	//制单人
	String createUserName = StringUtil.convertNull(request.getParameter("createUserName"));
	//制单开始时间
	String createDateStart = StringUtil.convertNull(request.getParameter("createDateStart"));
	//制单结束时间
	String createDateEnd = StringUtil.convertNull(request.getParameter("createDateEnd"));
	//操作人
	String userName = StringUtil.convertNull(request.getParameter("userName"));
	//确认开始时间
	String completeDateStart = StringUtil.convertNull(request.getParameter("completeDateStart"));
	//确认结束时间
	String completeDateEnd = StringUtil.convertNull(request.getParameter("completeDateEnd"));
	//产品编号
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
	
	//查询结果
	List<Map<String,String>> list = request.getAttribute("list")==null?null:(List<Map<String,String>>)request.getAttribute("list");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>退货上架统计</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>
<script type="text/javascript">
$(function($) {
	$("#queryBtn").click(
		function(){
			if(checkQryData()){
				return;
			}
			$("#qryForm").attr("action","cargoOperation.do?method=retShelfStatics");
			$("#qryForm").submit();
		}	
	);
	$("#exportBtn").click(
			function(){
				if(checkQryData()){
					return;
				}
				$("#qryForm").attr("action","cargoOperation.do?method=retShelfStaticsExport");
				$("#qryForm").submit();
			}	
	);		
});
function checkQryData(){
	if($("#wareArea").val()=="-1"){
		alert("请填写库地区！");
		return true;
	}
	if($.trim($("#createUserName").val())==""&&$.trim($("#userName").val())==""){
		alert("请填写制单人或操作人！");
		return true;
	}
	if($.trim($("#createUserName").val())!=""&&$.trim($("#userName").val())!=""){
		alert("制单人和制单时间，操作人和确认完成时间，只可填写一组！");
		return true;
	}
	if($.trim($("#createUserName").val())!=""&&$.trim($("#userName").val())==""){
		if($("#createDateStart").val()==""||$("#createDateEnd").val()==""){
			alert("请填写制单人、制单时间！");
			return true;
		}else if($("#completeDateStart").val()!=""||$("#completeDateEnd").val()!=""){
			alert("制单人和制单时间，操作人和确认完成时间，只可填写一组！");
			return true;
		}
		var dif=compareDateDif($("#createDateStart").val(),$("#createDateEnd").val());
		if(dif<0){
			alert("制单时间，起始日期不能大于截止日期！");
			return true;
		}
		if(dif>30){
			alert("制单时间，两个日期之差不得大于31！");
			return true;
		}		
	}
	if($.trim($("#createUserName").val())==""&&$.trim($("#userName").val())!=""){
		if($("#completeDateStart").val()==""||$("#completeDateEnd").val()==""){
			alert("请填写操作人，确认完成时间！");
			return true;
		}else if($("#createDateStart").val()!=""||$("#createDateEnd").val()!=""){
			alert("制单人和制单时间，操作人和确认完成时间，只可填写一组！");
			return true;
		}
		var dif=compareDateDif($("#completeDateStart").val(),$("#completeDateEnd").val());
		if(dif<0){
			alert("确认完成时间，起始日期不能大于截止日期！");
			return true;
		}
		if(dif>30){
			alert("确认完成时间，两个日期之差不得大于31！");
			return true;
		}		
	}
	return false;
}
//比较日期间隔
function compareDateDif(date1,date2){
	var data1 = Date.parse(date1.replace(/-/g,"/"));  
    var data2 = Date.parse(date2.replace(/-/g,"/"));  
    return (data2-data1)/(1000*60*60*24)
}
</script>
</head>
<body>
  	<form id="qryForm" action="" method="post"> 
  		<input type="hidden" value="1" name="qryFlag">
	  	<fieldset style="width:850px;"><legend>查询栏</legend>
	  	    库地区：<%=wareAreaSelectLable%>&nbsp;&nbsp;
	  	    制单人：<input type="text" size="15" name="createUserName" id="createUserName" value="<%=createUserName%>"/>&nbsp;&nbsp;
	  	   制单时间：<input type="text" readonly name="createDateStart" id="createDateStart" size="10" 
	  	   			onClick="WdatePicker();" value="<%=createDateStart%>"/>&nbsp;-&nbsp;
	  	   		<input type="text" readonly name="createDateEnd" id="createDateEnd" size="10" 
	  	   			onClick="WdatePicker();" value="<%=createDateEnd%>"/>&nbsp;&nbsp;&nbsp;&nbsp;<input id="queryBtn" type="button" value="统计"/>
	  	 </br></br>
	  	    操作人：<input type="text" size="15" name="userName" id="userName" value="<%=userName%>"/>&nbsp;&nbsp;
	  	   确认完成时间：<input type="text" readonly name="completeDateStart" id="completeDateStart" size="10" 
	  	   			onClick="WdatePicker();" value="<%=completeDateStart%>"/>&nbsp;-&nbsp;
	  	   		<input type="text" readonly name="completeDateEnd" id="completeDateEnd" size="10" 
	  	   			onClick="WdatePicker();" value="<%=completeDateEnd%>"/>&nbsp;&nbsp;	  	  
	  	   产品编号：<input type="text" size="15" name="productCode" id="productCode" value="<%=productCode%>"/>
	  	</fieldset>	
	</form>
		
	<table cellpadding="3" border=1 style="border-collapse:collapse;margin-left:20px;" bordercolor="#D8D8D5" width="95%" >
		<tr><td align="right" colspan="9"><input id="exportBtn" type="button" value="导出"/></td></tr>
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center"><font color="#FFFFFF">库地区</font></td>
			<td align="center"><font color="#FFFFFF">制单人</font></td>
			<td align="center"><font color="#FFFFFF">制单时间</font></td>
			<td align="center"><font color="#FFFFFF">操作人</font></td>
			<td align="center"><font color="#FFFFFF">确认完成时间</font></td>
			<td align="center"><font color="#FFFFFF">上架单数量</font></td>
			<td align="center"><font color="#FFFFFF">SKU数量</font></td>
			<td align="center"><font color="#FFFFFF">商品件数</font></td>									
		</tr>
		<%if(list!=null && list.size()>0){ 
			int upSelfNumSum = 0,skuNumSum = 0,productNum = 0;
			for(int i=0;i<list.size();i++){
				Map<String,String> map = list.get(i);
				upSelfNumSum+=Integer.valueOf(map.get("upSelfNum")).intValue();
				skuNumSum+=Integer.valueOf(map.get("skuNum")).intValue();
				productNum+=Integer.valueOf(map.get("productNum")).intValue();
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=(i + 1)%></td>
				<td align="center"><%=map.get("areaName")%></td>
				<td align="center"><%=map.get("create_user_name")%></td>
				<td align="center"><%=map.get("create_datetime")%></td>
				<td align="center"><%=map.get("complete_user_name")%></td>
				<td align="center"><%=map.get("complete_datetime")%></td>
				<td align="right"><%=map.get("upSelfNum")%></td>
				<td align="right"><%=map.get("skuNum")%></td>
				<td align="right"><%=map.get("productNum")%></td>			
			</tr>
		<% } %>
		<tr bgcolor="#ffffff">
			   <td align="right" colspan="6">合计：</td>
			   <td align="right"><%=upSelfNumSum%></td>
			   <td align="right"><%=skuNumSum%></td>
			   <td align="right"><%=productNum%></td>	
		</tr>
		<%} %>
	</table>	
</body> 