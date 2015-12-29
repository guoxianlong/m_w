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
PagingBean paging = (PagingBean) request.getAttribute("paging");
List cargoStaffList = new ArrayList();
if(request.getAttribute("cargoStaffList") != null){
	cargoStaffList = (List)request.getAttribute("cargoStaffList");
}
List deptList = new ArrayList();
if(request.getAttribute("deptList") != null){
	deptList = (List)request.getAttribute("deptList");
}
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
<script language="JavaScript"
	src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/js/jquery.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css"
	rel="stylesheet" type="text/css">
<script type="text/javascript">
	function check(frm){
		if(frm.kw.value == "关键字" || frm.condition.value == ""){
			alert("您没有指定查询条件！");
			return false;
		}
		return true;
	}
	 function checksubmit(){
		 	if(confirm("如果确认修改,请单击'确定',反之请单击'取消'！"))
		 	{
		 	   return true;
		 	}
		 	else
		 	{
		 	   return false;
		 	}

		 	return true;
		 }
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
		<td style="text-decoration:none;">
		<%for(int i=0;i<deptList.size();i++){ 
			CargoDeptBean cd=(CargoDeptBean)deptList.get(i); 
			int id = cd.getId();%>
			<a style="text-decoration: none" href="qualifiedStock.do?method=departmentAreaStockType&id=<%=id%>"><%=cd.getName() %></a>&nbsp;<br/>
			<%List deptList2=cd.getJuniorDeptList(); 
			for(int j=0;j<deptList2.size();j++){ %>
				&nbsp;&nbsp;&nbsp;
				<%CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
				int id2 = cd2.getId();%>
				<a style="text-decoration: none" href="qualifiedStock.do?method=departmentAreaStockType&id=<%=id%>&id2=<%=id2%>"><%=cd2.getName() %></a>&nbsp;<br/>
				<%List deptList3=cd2.getJuniorDeptList(); 
					for(int k=0;k<deptList3.size();k++){ %>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<%CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k); 
						int id3 = cd3.getId();%>
						<a style="text-decoration: none" href="qualifiedStock.do?method=departmentAreaStockType&id=<%=id%>&id2=<%=id2%>&id3=<%=id3%>"><%=cd3.getName() %></a>&nbsp;<br/>
						<%List deptList4=cd3.getJuniorDeptList();  
						  for(int l=0;l<deptList4.size();l++){ %>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<%CargoDeptBean cd4=(CargoDeptBean)deptList4.get(l); 
							int id4 = cd4.getId();%>
							<a style="text-decoration: none" href="qualifiedStock.do?method=departmentAreaStockType&id=<%=id%>&id2=<%=id2%>&id3=<%=id3%>&id4=<%=id4%>"><%=cd4.getName() %></a>&nbsp;<br/>
						<%} %>
					<%} %>
				<%} %>
			<%} %>
	
</table>
</td>
<td>
<form action="qualifiedStock.do?method=assignAreaStockType" method="post" onsubmit="return checksubmit();">
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
<input name="submit" type=submit  value="提交"  >
</form>
</td>
</tr>
</table>
</body>
</html>