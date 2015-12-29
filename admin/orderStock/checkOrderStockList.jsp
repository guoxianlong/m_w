<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@page import="adultadmin.action.vo.voOrder,mmb.stock.stat.*"%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部	
%>
<%
OrderStockAction action = new OrderStockAction();
action.checkOrderStockList(request, response);
Map deliverMapAll = voOrder.deliverMapAll;
List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");

int i, count;
OrderStockBean bean = null;

String strStatus = request.getParameter("status");
int status = 0;
if(strStatus == null){
	status = -1;
} else {
	status = StringUtil.StringToId(strStatus);
}
String strArea = request.getParameter("area");
int area = 0;
if(strArea == null){
	area = -1;
} else {
	area = StringUtil.StringToId(strArea);
}
int deliver = StringUtil.toInt(request.getParameter("deliver"));
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate="2007-01-01";
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.formatDate(new Date());

String wareAreaLable = ProductWarePropertyService.getWeraAreaAllCustomized("area", "", area, true, "-1");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script type="text/javascript">
function checkAll(name,id) {     
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name) && (el[i].id==id)){
	    el[i].checked = true;         
	}     
    } 
}

function clearAll(name) {
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name)){
	    el[i].checked = false;
	}
    }
} 
</script>
<p align="center">复核列表</p>
<fieldset>
	<legend>查询条件</legend>
	<form method="post" name="searchForm" action="./checkOrderStockList.jsp" >
	订单号：<input type="text" name="orderCode" value="" size="15" />&nbsp;
	出库单号：<input type="text" name="orderStockCode" value="" size="15" />&nbsp;
	快递公司：<select name="deliver">
		<option value="-1"></option>
		<%
		Iterator deliverIter = deliverMapAll.entrySet().iterator();
		while (deliverIter.hasNext()) {
			Map.Entry entry = (Map.Entry) deliverIter.next();
		%>
			<option value=<%=entry.getKey()%>><%=entry.getValue()%></option>
		<%}%>
	</select>
	<script>selectOption(searchForm.deliver, '<%= deliver %>')</script>
	&nbsp;
	出货地区：<%= wareAreaLable%>
	<script>selectOption(searchForm.area, '<%= area %>')</script>
	时间：<input type="text" name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type="text" name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">
	<input type="submit" value=" 查 询 " />
	</form>
</fieldset>
<form method="post" action="collectOrderStock.jsp">
<table width="98%" border="1">
<tr>
  <td>选择</td>
  <td>序号</td>
  <td>名称</td>
  <td>订单编号</td>
  <td width="15%">添加时间</td>
  <td>订单状态</td>
  <td>状态</td>
  <td>库房</td>
  <td>快递公司</td>
  <td>操作</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	bean = (OrderStockBean) list.get(i);
%>
<tr>
  <td><input type="radio" name="id" value="<%=bean.getId()%>" onclick="sp.window.location.href='scanOrderStock.jsp?scanback=orderStock.jsp&id=' + this.value; document.all.d1.style.display='block';" /></td>
  <td><%=(i + 1)%></td>
  <td><a href="scanOrderStock.jsp?id=<%=bean.getId()%>&scanback=checkOrderStockList.jsp"><%=bean.getName()%></a></td>
  <td><a href="../order.do?id=<%=bean.getOrder()!=null?bean.getOrder().getId():""%>&split=1"><%=bean.getOrderCode()%></a></td>
  <td><%=bean.getCreateDatetime().substring(0, 16)%></td>
  <td><%=bean.getOrder()!=null?bean.getOrder().getStatusName():"" %></td>
  <td>
<%
if(bean.getStatus() == OrderStockBean.STATUS1){
	if(bean.getRealStatusStock() == OrderStockBean.STATUS1_STOCK){
%><font color="green"><%=bean.getStatusName()%></font><%
	} else if(bean.getRealStatusStock() == OrderStockBean.STATUS1_ONE_STOCK) {
%><font color="darkorange"><%=bean.getStatusName()%></font><%
	} else if(bean.getRealStatusStock() == OrderStockBean.STATUS1_OTHER_STOCK) {
%><font color="#990066"><%=bean.getStatusName()%></font><%
	} else {
%><font color="red"><%=bean.getStatusName()%></font><%
	}
}else if(bean.getStatus() == OrderStockBean.STATUS2){
	if(bean.getStatusStock() == OrderStockBean.STATUS2_FROM_NO_STOCK){
%><font color="green"><%=bean.getStatusName()%></font><%
	} else {
%><font color="blue"><%=bean.getStatusName()%></font><%
	}
}else{%><%=bean.getStatusName()%><%}%></td>
  <td><%= ProductStockBean.getStockTypeName(bean.stockType) %>(<%= ProductStockBean.getAreaName(bean.getStockArea()) %>)</td>
  <td><%= StringUtil.convertNull((String)voOrder.deliverMapAll.get(String.valueOf(bean.getDeliver())))%></td>
  <td><a href="scanOrderStock.jsp?id=<%=bean.getId()%>&scanback=checkOrderStockList.jsp">编辑</a></td>
</tr>
<%
}
%>
</table>
</form>
<p align="center"><input type="button" value="按产品汇总" onClick="javascript:window.location='collectCheckOrderStock.jsp'"/></p>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<div id=d1 style="display:none">
	<iframe name="sp" width="100%" height="500" align="center" frameborder="0" scrolling="no" ></iframe>
</div>