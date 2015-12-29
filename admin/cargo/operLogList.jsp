<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoOperLogBean"%><html>
<head>
<title>作业日志查询</title>
<%
List operLogList=(List)request.getAttribute("operLogList");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function checkCode(){
	var code=trim(document.getElementById("operCode").value);
	if(code==""){
		alert("作业单编号不能为空！");
		return false;
	}
	return true;
}
function click1(obj){
	var code=obj.value;
	if(code=="作业单编号"){
		obj.value=""
	}
}
function click2(obj){
	var code=trim(obj.value);
	if(code==""){
		obj.value="作业单编号"
	}
}
</script>
</head>
<body>
<form action="qualifiedStock.do?method=cargoOperLog" method="post" onsubmit="return checkCode();">
作业日志查询：
<input type="text" id="operCode" name="operCode" value="作业单编号" onfocus="click1(this);" onblur="click2(this);"/>&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" value="精确查询"/>
</form>
<%if(operLogList!=null){%>
	<%if(operLogList.size()==0&&request.getParameter("operCode")!=null){ %>
		<font color="red">没有查询到对应记录！</font>
	<%}else if(operLogList.size()!=0){ %>
		<table border=1>
			<tr>
				<td>操作前作业状态</td>
				<td>作业操作态</td>
				<td>操作后作业状态</td>
				<td>操作时间</td>
				<td>操作人</td>
				<td>执行人</td>
				<td>时效状态</td>
			</tr>
			<%for(int i=0;i<operLogList.size();i++){ %>
				<%CargoOperLogBean log=(CargoOperLogBean)operLogList.get(i); %>
			<tr>
				<td><%=log.getPreStatusName()==null?"":log.getPreStatusName() %></td>
				<td><%=log.getOperName() %></td>
				<td><%=log.getNextStatusName()==null?"":log.getNextStatusName() %></td>
				<td><%=log.getOperDatetime().substring(0,19) %></td>
				<td><%=log.getOperAdminName() %></td>
				<td><%=log.getHandlerCode() %></td>
				<td>
					<%if(log.getEffectTime()==0){ %><font color="green">进行中</font>
					<%}else if(log.getEffectTime()==1){ %><font color="red">超出时效</font>
					<%}else if(log.getEffectTime()==2){ %><font color="black">待复核</font>
					<%}else if(log.getEffectTime()==3){ %><font color="black">作业成功</font>
					<%}else if(log.getEffectTime()==4){ %><font color="black">作业失败</font>
					<%} %>
				</td>
			</tr>
			<%} %>
		</table>
	<%} %>
<%} %>
<script type="text/javascript">
<%if(request.getParameter("operCode")!=null){%>
	document.getElementById("operCode").value='<%=request.getParameter("operCode")%>';
<%}%>
</script>
</body>
</html>