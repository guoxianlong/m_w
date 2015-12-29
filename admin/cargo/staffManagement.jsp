<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoDeptBean"%>
<%@page import="adultadmin.util.Encoder"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
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
%>

<html>
<head>
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
<title>物流员工管理</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css"rel="stylesheet" type="text/css">

<%@include file="../help/pagehelp.jsp" %><%--帮助文档页面逻辑js --%>

<script type="text/javascript">
	function check(frm){
		if(frm.kw.value == "关键字" || frm.condition.value == ""){
			alert("您没有指定查询条件！");
			return false;
		}
		return true;
	}
</script>

</head>
<body>

<table width="100%">
	<tr>
		<td valign="top">
			<table border="1" cellpadding="3" border=1 style="border-collapse: collapse;"
	bordercolor="#D8D8D5" align="center" width="95%">
	<tr bgcolor="#cccccc">
		<td colspan="7" align="center" height="50px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;">组织结构</td>
	</tr>
	<tr>
		<td style="text-decoration:none;">
		<%for(int i=0;i<deptList.size();i++){ 
			CargoDeptBean cd=(CargoDeptBean)deptList.get(i); 
			int id = cd.getId();%>
			<a style="text-decoration: none" href="qualifiedStock.do?method=staffManagement&id=<%=id%>"><%=cd.getName() %></a>&nbsp;<br/>
			<%List deptList2=cd.getJuniorDeptList(); 
			for(int j=0;j<deptList2.size();j++){ %>
				&nbsp;&nbsp;&nbsp;
				<%CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
				int id2 = cd2.getId();%>
				<a style="text-decoration: none" href="qualifiedStock.do?method=staffManagement&id=<%=id%>&id2=<%=id2%>"><%=cd2.getName() %></a>&nbsp;<br/>
				<%List deptList3=cd2.getJuniorDeptList(); 
					for(int k=0;k<deptList3.size();k++){ %>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<%CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k); 
						int id3 = cd3.getId();%>
						<a style="text-decoration: none" href="qualifiedStock.do?method=staffManagement&id=<%=id%>&id2=<%=id2%>&id3=<%=id3%>"><%=cd3.getName() %></a>&nbsp;<br/>
						<%List deptList4=cd3.getJuniorDeptList();  
						  for(int l=0;l<deptList4.size();l++){ %>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<%CargoDeptBean cd4=(CargoDeptBean)deptList4.get(l); 
							int id4 = cd4.getId();%>
							<a style="text-decoration: none" href="qualifiedStock.do?method=staffManagement&id=<%=id%>&id2=<%=id2%>&id3=<%=id3%>&id4=<%=id4%>"><%=cd4.getName() %></a>&nbsp;<br/>
						<%} %>
					<%} %>
				<%} %>
			<%} %>
			<br/>
			<%if(group.isFlag(417)){ %>
				<a href="qualifiedStock.do?method=toAddStaff">添加员工档案</a>
				<br/>
			<%} %>
			<a href="qualifiedStock.do?method=staffManagement">全部档案列表</a>
			<br/>
			<%if(group.isFlag(416)){ %>
				<a href="qualifiedStock.do?method=deptManagement">维护组织结构</a>
				<br/>
				<a href="qualifiedStock.do?method=departmentAreaStockType">部门地区库类型</a>
			<%} %>
		</td>
	</tr>
</table>
		</td>
		<td valign="top" align="center">
			<table cellpadding="3" border=1 style="border-collapse: collapse;"
	bordercolor="#D8D8D5" align="center" width="95%">
	<tr bgcolor="#cccccc">
		<td colspan="7" align="center" height="50px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
		<form action="qualifiedStock.do?method=staffManagement" method="post" onsubmit="return check(this);">
		员工档案列表 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input name="kw" type="text" value="关键字" 
		style="color:#cccccc;" 
		onfocus="if(this.value=='关键字'){this.value='';this.style.color='#000000';}"
		onblur="if(this.value==''){this.value='关键字';this.style.color='#cccccc';}"
		/>
		<SELECT name="condition">
			<OPTION selected value="">指定条件</OPTION>
			<OPTION  value="name">姓名</OPTION>
			<OPTION  value="user_name">后台帐号</OPTION>
			<OPTION  value="code">员工编号</OPTION>
			<OPTION  value="create_datetime">加入日期</OPTION>
			<!--  <OPTION  value="dept_name">所属部门</OPTION>-->
			<OPTION  value="phone">电话</OPTION>
		</SELECT>
		<INPUT name="search" type=submit  value="查询"  >
		</form>
		</td>
	</tr>
	<tr bgcolor="#00ccff" >
		<td align="center" style="font-weight:bold;color:#000000;">姓名</td>
		<td align="center" style="font-weight:bold;color:#000000;">后台账号</td>
		<td align="center" style="font-weight:bold;color:#000000;">员工编号</td>
		<td align="center" style="font-weight:bold;color:#000000;">创建时间</td>
		<td align="center" style="font-weight:bold;color:#000000;">归属部门</td>
		<td align="center" style="font-weight:bold;color:#000000;">电话</td>
		<td align="center" style="font-weight:bold;color:#000000;">操作</td>
	</tr>
	<%
	int j = 1;
	for(int i = 0; i < cargoStaffList.size(); i++){
		CargoStaffBean csb = (CargoStaffBean)cargoStaffList.get(i);	
	%>
		<tr <%if(j%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td align="center"><%=csb.getName()%></td>
			<td align="center"><%=csb.getUserName()%></td>
			<td align="center"><%=csb.getCode()%></td>
			<td align="center"><%=csb.getCreateDatetime().substring(0,10)%></td>
			<td align="center"><%=csb.getDeptName()%></td>
			<td align="center"><%=csb.getPhone()%></td>
			<td align="center">
			<%if(csb.getStatus()==0){ %>
			<%if(group.isFlag(419)){ %>
				<a href="qualifiedStock.do?method=editStaff&id=<%=csb.getId()%>"><font color="#0000ff">编辑</font></a>&nbsp;|&nbsp;
			<%} %>
			
			<%if(group.isFlag(418)){ %>
			<a href="qualifiedStock.do?method=delStaff&staffId=<%=csb.getId()%>
				<%=request.getParameter("condition")==null?"":"&condition="+request.getParameter("condition") %>
				<%=request.getParameter("kw")==null?"":"&kw="+Encoder.encrypt(kw) %>
				<%=request.getParameter("id2")==null?"":"&id2="+request.getParameter("id2") %>
				<%=request.getParameter("id3")==null?"":"&id3="+request.getParameter("id3") %>
				<%=request.getParameter("id4")==null?"":"&id4="+request.getParameter("id4") %>
				<%=request.getParameter("pageIndex")==null?"":"&pageIndex="+request.getParameter("pageIndex") %>
				<%=request.getParameter("id")==null?"":"&id="+request.getParameter("id") %>" onclick="return window.confirm('确认该员工【已离职】并【删除】吗？')">
				<font color="#0000ff">删除</font></a>&nbsp;|&nbsp;<%} %>
			<%if(group.isFlag(420)){ %>
				<a href="<%=request.getContextPath()%>/admin/cargo/staffCodePrint.jsp?code=<%=csb.getCode()%>" target="_blank"><font color="#0000ff">条码打印</font></a>
			<%} %><%}else{ %><a href="qualifiedStock.do?method=recoverStaff&id=<%=csb.getId()%>"><font color="#0000ff">恢复</font></a><%} %>
			</td>
		</tr>
		
	<%j++;} %>
	
</table>
<input type="button" value="帮助文档说明"  onclick="showDiv('443aa62a')"><%--帮助文档参数为“code”字段 --%>
<div id="help" style="display: none;"></div>
		<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%>
		</td>
	</tr>
	
</table>

</body>
</html>