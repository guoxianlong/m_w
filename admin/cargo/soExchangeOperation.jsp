<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>作业交接</title>
<% String area = (String)request.getSession().getAttribute("area"); 
	int areaId = StringUtil.toInt(area);
	ProductStockBean psBean = new ProductStockBean();
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean flag = true;
	if(!group.isFlag(618)){
		flag = false;
	}
	
	boolean generateRetUpShelfFlag = true;
	if(!group.isFlag(616)){
		generateRetUpShelfFlag = false;
	}
%>

<script type="text/javascript">
function getFocus(){
	document.getElementById("firstButton").focus();
	<%
		if(area==null){
	%>
			alert("登陆超时,请重新登录!");
			window.location='stockOperation.do?method=logout';
	<%
		}
	%>
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()">
<table width="220" height="220" border="0" cellspacing="0">
<tr >
	<td colspan="2" align="center"><font size="4" style="font-weight:bold">作业交接</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr>
	<td colspan="2" align="center"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr>
	<td colspan="2" align="center"><%=DateUtil.getNow() %></td>
</tr>
<tr align="center">
	<td><input id="firstButton"type="button" style="height=30px;width=140px;font-weight:bolder" value="作业审核" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=operationAudit'"/></td>
</tr>
<tr align="center">
	<td><input type="button" style="height=30px;width=140px;font-weight:bolder" value="作业完成" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=operationComplete'"/></td>
</tr>
<%if(flag){%>
<tr align="center">
	<td><input type="button" style="height=30px;width=140px;font-weight:bolder" value="退货上架单完成" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=soConfirmComplete'"/></td>
</tr>
<% } %>

<%if(generateRetUpShelfFlag){%>
<tr align="center">
	<td><input type="button" style="height=30px;width=140px;font-weight:bolder" value="生成退货上架单(整件)" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=generateWholeArea'"/></td>
</tr>
<tr align="center">
	<td><input type="button" style="height=30px;width=140px;font-weight:bolder" value="生成退货上架单" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=soGenerateRetShelf'"/></td>
</tr>
<% } %>
<tr align="center">
	<td><input type="button" style="height=26px;width=140px;" value="返 回" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=pdaOperation'"/></td>
</tr>
</table>
</body>
</html>