<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@page import="adultadmin.action.vo.*,adultadmin.bean.*,adultadmin.framework.*"%>
<html>
<title>买卖宝后台</title>
<script language="JavaScript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<logic:notEmpty name="tip">
<script language="JavaScript">
alert('<bean:write name="tip" />');
</script>
</logic:notEmpty>
<body>
<%@include file="../header.jsp"%>
<div style="display: block; float: left;">
<a href="orderImportLogs.do" >查看操作日志</a>
</div><br/>
<%
voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
UserGroupBean group = admin.getGroup();
%>
<form method=post action="importOrderPackageNum.do" name="importForm">
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">操作类型</td>
		<td >
			<select name="type" class="bd">
			      <option value="0" selected="selected">导入销售订单包裹单号</option>
			      <%if(group.isFlag(0)){ %>
			      <option value="16">导入售后包裹单号(返厂)</option>
			      <option value="17">导入售后包裹单号(返用户)</option>
			      <option value="18">导入其它包裹单号</option>
			      <option value="2">导入包裹单号(根据姓名手机号)</option>
			      <option value="1">设置订单状态(已结算)</option>
			      <option value="3">设置订单状态(已结算)(根据姓名手机号)</option>
			      <option value="4">设置订单状态(待退回)</option>
			      <option value="5">设置订单状态(待退回)(根据姓名手机号)</option>
			      <option value="6">设置订单状态(已妥投)</option>
			      <option value="7">设置订单状态(已妥投)(根据姓名手机号)</option>
			      <%-- <option value="8">导入退回未结算（按订单号）</option>
			      <option value="9">导入退回未结算（按姓名和手机号）</option>
			      <option value="10">导入退回已结算（按订单号）</option>
			      <option value="11">导入退回已结算（按姓名和手机号）</option>--%>
			      <option value="12">导入妥投已结算（按订单号）</option>
			      <option value="13">导入妥投已结算（按姓名和手机号）</option>
			      <%} %>
			</select>
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="left" >
			导入内容<br /><br/>
			“导入包裹单号”使用以下格式：<br/>
			<span style="color: red;">
			订单号(制表符)包裹单号<br/>
			订单号(制表符)	包裹单号<br/>
			……<br/>
			</span><br/>
			“导入包裹单号(根据姓名手机号)”使用以下格式：<br/>
			<span style="color: red;">
			姓名(制表符)手机号(制表符)包裹单号<br/>
			姓名(制表符)手机号(制表符)包裹单号<br/>
			……<br/>
			</span><br/>
			“设置订单状态(已结算)”使用以下格式：<br/>
			<span style="color: red;">
			订单号(制表符)包裹单号<br/>
			订单号(制表符)包裹单号<br/>
			……<br/>
			</span><br/>
		</td>
		<td>
			<textarea name="content" cols="70" rows="20"></textarea>
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center" colspan=2><input type=submit value=" 提 交 "></td>
	</tr>
</table>
</form>
<br />
<logic:notEmpty name="error" scope="request">
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
<tr><td>
<span style="color: red;">有错误，提示内容：</span><br/>
<textarea name="content" cols="80" rows="10"><bean:write name="error" scope="request" /></textarea>
</td></tr>
</table>
</logic:notEmpty>
</body>
</html>