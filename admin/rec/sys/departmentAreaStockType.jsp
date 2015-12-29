<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.bean.cargo.CargoDeptBean"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="mmb.stock.cargo.CargoDeptAreaBean"%>
<%@page import="adultadmin.util.Encoder"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%@ page import="java.util.HashMap" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
String kw=request.getParameter("kw");
if(Encoder.decrypt(kw)!=null){
	kw=Encoder.decrypt(kw);
}
HashMap<Integer, List<CargoDeptAreaBean>> map = (HashMap<Integer, List<CargoDeptAreaBean>>)request.getAttribute("map");
String deptNameFull = (String)request.getAttribute("deptNameFull");
String deptId = (String)request.getAttribute("deptId");
String parmId = (String)request.getAttribute("deptId1");
String parmId2 = (String)request.getAttribute("deptId2");
String parmId3 = (String)request.getAttribute("deptId3");
String parmId4 = (String)request.getAttribute("deptId4");
%>

<html>
<head>
<title>物流员工管理</title>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css"
	rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>	
<script type="text/javascript">
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		$("#sub").click(function(){
		 	$.messager.confirm('提示','如果确认修改,请单击确定,反之请单击取消！',function(boo){
		 		if(boo){
		 			$("#form").submit();
		 		}else{
		 			return false;
		 		}
		 	});
		 	return false;
		});
	});
</script>
<style>
td{
	font-family: 微软雅黑; 
	font-size: 13px; 
	font-weight: normal; 
	font-style: normal; 
	text-decoration: none; 
	color: #333333;
 }

</style>
</head>
<body>

<table>
    
	<tr>
		<td valign="top">
			<table border="1" cellpadding="3" border=1 style="border-collapse: collapse;"
	bordercolor="#D8D8D5" align="left" width="200px">
	<tr bgcolor="#cccccc">
		<td colspan="7" align="center" height="50px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;">组织结构</td>
	</tr>
	<tr>
		<td><ul id="ul"></ul></td>
	</tr>
</table>
</td>
<td>
<form action="<%=request.getContextPath() %>/CargoController/assignAreaStockType.mmx" method="post" id="form">
<table>
	<tr bgcolor="#cccccc" align="center" height="50px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"><%=deptNameFull %></tr>
	<%int x = 0; int t = 0;%>
	<% for (int mapKey : ProductStockBean.areaMap.keySet())  {%>
	<% if (x%6 == 0) {%>
	<tr>
	<%t=x+6;} %>
		<td>
			<table border="1" cellpadding="3" border=1 style="border-collapse: collapse;"
				bordercolor="#D8D8D5">
				<%
					List stockList = map.get(mapKey);
					HashMap stockTypeMap = ProductStockBean.stockTypeMap;
   					Iterator iter = stockTypeMap.entrySet().iterator();
   					int i=0;
 					while (iter.hasNext()) {
 						i++;
	   					Map.Entry entry = (Map.Entry) iter.next();
	   					Object key = entry.getKey();
	   					Object val = entry.getValue();
				%>
				<tr >
				<%if(i==1){ %>
					<td width="123" rowspan="<%=stockTypeMap.size()%>"><div align="center"><%=ProductStockBean.getAreaName(mapKey) %></div></td><%} %>
					<td width="225"><div align="left"><input name="checkBox<%=mapKey %>" value="<%=key%>" type="checkbox" <%if(stockList!=null && stockList.contains(key)){ %> checked="checked" <%} %>/><%=val %></div></td>
				</tr>
				<%} %>
			</table>
		</td>
		<% if (x==t) {%>
	</tr>
	<%} %>
	<%x++; %>
	<%} %>
</table>
<input name="deptId" type="hidden" value="<%=deptId%>"/>
<input name="id" type="hidden" value="<%=parmId%>"/>
<input name="id2" type="hidden" value="<%=parmId2%>"/>
<input name="id3" type="hidden" value="<%=parmId3%>"/>
<input name="id4" type="hidden" value="<%=parmId4%>"/>
<a class="easyui-linkbutton" data-options="iconCls:'icon-reload'" id="sub">提交</a>
</form>
</td>
</tr>
</table>
</body>
<script type="text/javascript">
	$(function(){
		$.ajax({
			url:'<%=request.getContextPath()%>/CargoController/staffManagement.mmx',
			cache:false,
			data:{address:'departmentAreaStockType'},
			dataType:'text',
			success:function(result){
				var re = eval('('+result+')');
				if(re['result']=="failure"){
					$.messager.show({
						title:'结果提示',
						msg:re['tip'],
						showType:'slide'
					});
					return;
				}
				if(re['result']=="success"){
					$("#ul").tree({
						data:re['tip']
					});
				}
			}
		});
		$("#ul").tree({
			lines:true,
			onClick:function(node){
				window.location.href=node.attributes.url;
			}
		});
	});
</script>
</html>