<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.util.Encoder"%><html>
<head>
<title>未申请结算订单列表</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List packageList=(List)request.getAttribute("packageList");
PagingBean pageBean=(PagingBean)request.getAttribute("paging");

%>
<script type="text/javascript">
function focusKeyword(){
	var keyword=document.getElementById("keyword");
	if(keyword.value=="输入关键字..."){
		keyword.value="";
	}
	var select=document.getElementById("select");
	if(select.value=="3"){
		SelectDate(keyword,'yyyy-MM-dd');
	}
}
function blurKeyword(){
	var keyword=document.getElementById("keyword");
	if(keyword.value==""){
		keyword.value="输入关键字...";
	}
}
function check(){
	var select=document.getElementById("select").value;
	var keyword=document.getElementById("keyword").value;
	if(select==""){
		alert("请选择查询条件！");
		return false;
	}
	if(keyword==""||keyword=="输入关键字..."){
		alert("请输入查询条件！");
		return false;
	}
	return true;
}
</script>
</head>
<body bgcolor="#FFCC00">
<%if(group.isFlag(485)){ %>
<div align="right">
	<form action="mailingBatch.do?method=unBalanceOrderList" method="post">
		<input type="text" id="keyword" name="keyword" value="输入关键字..." onfocus="focusKeyword();" onblur="blurKeyword();">
		<select id="select" name="select">
			<option value="">查询条件</option>
			<option value="1">发货波次号</option>
			<option value="2">订单编号</option>
			<option value="3">入库日期</option>
			<option value="4">投递员姓名</option>
			<option value="5">接货人账号</option>
			<option value="6">发货仓</option>
		</select>
		<input type="submit" value="查询" onclick="return check();"/>
	</form>
</div>
<%if(request.getParameter("keyword")!=null){ %>
	<script type="text/javascript">
		document.getElementById("keyword").value='<%=Encoder.decrypt(request.getParameter("keyword"))==null?request.getParameter("keyword"):Encoder.decrypt(request.getParameter("keyword"))%>';
	</script>
<%} %>
<%if(request.getParameter("select")!=null){ %>
	<script type="text/javascript">
		selectOption(document.getElementById("select"),'<%=request.getParameter("select")%>');
	</script>
<%} %>
<%} %>
<hr/>
<form action="<%=request.getContextPath()%>/admin/mailingBatch.do?method=addMailingBalanceAudit" method="post">
<table border="1">
	<tr>
		<td>序号</td>
		<td>发货波次号</td>
		<td>订单编号</td>
		<td>入库日期</td>
		<td>入库时间</td>
		<td>投递员</td>
		<td>入库接收人</td>
		<td>发货仓库</td>
		<td>支付方式</td>
		<td>订单金额</td>
	</tr>
	<%for(int i=0;i<packageList.size();i++){ %>
		<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i); %>
	<tr>
		<td><%=i+1 %><input type="checkbox" name="packageId" value="<%=packageBean.getId() %>"/></td>
		<td><%=packageBean.getMailingBatchCode() %></td>
		<td><%=packageBean.getOrderCode() %></td>
		<td><%=packageBean.getStockInDatetime().substring(0,10) %></td>
		<td><%=packageBean.getStockInDatetime().substring(11,19) %></td>
		<td><%=packageBean.getPostStaffName() %></td>
		<td><%=packageBean.getStockInAdminName() %></td>
		<td><%=packageBean.getMailingBatchBean()!=null?packageBean.getMailingBatchBean().getStore():"" %></td>
		<td><div align="center">
			<select id="payType" name="<%=packageBean.getId() %>" >
        	    	<option value=0>支付方式</option>
        			<option value=1 <%if(packageBean.getPayType()==1){ %>selected="selected" <%} %>>现金支付</option>
        		    <option value=2 <%if(packageBean.getPayType()==2){ %>selected="selected" <%} %>>pos机刷卡</option>
          	</select></div></td>
		<td><%=packageBean.getTotalPrice() %></td>
	</tr>
	<%} %>
</table>
<%if(group.isFlag(484)){ %>
<input type="submit" value="提交选中订单至结算申请"/>
<%} %>
<%if(pageBean!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
</form>
</body>
</html>