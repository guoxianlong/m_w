<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,mmb.stock.stat.*" %>
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
action.orderStockList(request, response);

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
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate=DateUtil.getBackFromDate(DateUtil.getNowDateStr(), 3);
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.formatDate(new Date());
int allOrderStock = StringUtil.StringToId(request.getParameter("allOrderStock"));

String wareAreaLable = ProductWarePropertyService.getWeraAreaAllCustomized("area", "", area, true,"-1");
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
function checkdate(){
	var startDate=document.getElementById("startDate").value;
	var endDate=document.getElementById("endDate").value;
var s1 = startDate.replace(/-/g, "/");
var s2 = endDate.replace(/-/g, "/");
d1 = new Date(s1);
d2 = new Date(s2);

var time= d2.getTime() - d1.getTime();
if(time<0){
alert("开始日期不能大于结束日期!");
return false;	
}
var days = parseInt(time / (1000 * 60 * 60 * 24));
  if(days>30){
  alert("相差天数超过30天!");
  return false;
  }
  return true;
}
</script>
<p align="center">订单出货操作记录</p>
<form method="post" action="addOrderStock.jsp">
订单编号：<input type="text" name="orderCode" size="50" value=""/><input type="submit" value="添加订单出货记录"/><br/>
</form>
<fieldset>
	<legend>查询条件</legend>
	<form method="post" name="searchForm" action="./orderStockList.jsp" onsubmit="return checkdate()" >
	订单号：<input type="text" name="orderCode" value="" size="15" />
	出货地区：
	<%= wareAreaLable%>
	<script>selectOption(searchForm.area, '<%= area %>')</script>
	订单状态：<select name="status">
		<option value="-1"></option>
		<option value="<%= OrderStockBean.STATUS1 %>">处理中</option>
		<option value="<%= OrderStockBean.STATUS2 %>">待出货</option>
		<option value="<%= OrderStockBean.STATUS6 %>">复核</option>
		<option value="<%= OrderStockBean.STATUS3 %>">已出货</option>
		<%-- <option value="<%= OrderStockBean.STATUS7 %>">实物未退回</option>
		<option value="<%= OrderStockBean.STATUS8 %>">实物已退货</option>--%>
		<option value="<%= OrderStockBean.STATUS9 %>">用户退货</option>
	</select>
	<script>selectOption(searchForm.status, '<%= status %>')</script>
	时间：<input type="text" name="startDate" id="startDate"  size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type="text" name="endDate" id="endDate"  size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">
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
  <td>操作</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	bean = (OrderStockBean) list.get(i);
%>
<tr>
  <td><%if(bean.getStatus() == OrderStockBean.STATUS1 || bean.getStatus() == OrderStockBean.STATUS2){%><input type="checkbox" name="ids" value="<%=bean.getId()%>" <%if(bean.getStatus() == OrderStockBean.STATUS1){%>id="s1"<%}%><%if(bean.getStatus() == OrderStockBean.STATUS2){%>id="s2"<%}%>><%} else {%>.<%}%></td>
  <td><%=(i + 1)%></td>
  <td><a href="orderStock.jsp?id=<%=bean.getId()%>"><%=bean.getName()%></a></td>
  <td><a href="../order.do?id=<%= ((bean.getOrder() != null)?bean.getOrder().getId() : 0) %>&split=1"><%=bean.getOrderCode()%></a></td>
  <td><%=bean.getCreateDatetime().substring(0, 16)%></td>
  <td><%= ((bean.getOrder() != null)?bean.getOrder().getStatusName():"") %></td>
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
  <td><a href="orderStock.jsp?id=<%=bean.getId()%>">编辑</a>
<%if(bean.getStatus() == OrderStockBean.STATUS1 || bean.getStatus() == OrderStockBean.STATUS2 || bean.getStatus() == OrderStockBean.STATUS6){%>
<%if(bean.getStatus() == OrderStockBean.STATUS2 || bean.getStatus() == OrderStockBean.STATUS6){ %>
<%if(group.isFlag(35)) /*if(!(isXiaoshou && isAdmin) && !(isKefu && isAdmin))*/{ %>
  	|<a href="deleteOrderStock.jsp?id=<%=bean.getId()%>&pageIndex=<%= paging.getCurrentPageIndex() %>" onclick="return confirm('确认删除？')">删除</a>
<%} %>
<%}else { %>
  	|<a href="deleteOrderStock.jsp?id=<%=bean.getId()%>&pageIndex=<%= paging.getCurrentPageIndex() %>" onclick="return confirm('确认删除？')">删除</a>
<%} %>
<%} %>
  </td>
</tr>
<%
}
%>
</table>
<p align="center"><input type="button" name="B" onclick="javascript:checkAll('ids','s1');" value="全选处理中"/><input type="button" name="B" onclick="javascript:checkAll('ids','s2');" value="全选待发货"/><input type="button" name="B" onclick="javascript:clearAll('ids');" value="全不选"/><input type="submit" name="B" value="汇总"/>&nbsp;&nbsp;&nbsp;&nbsp;<a href="orderStockList.jsp?status=0&allOrderStock=1&area=<%= area %>" target="_blank">显示全部处理中</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="orderStockList.jsp?status=0&allOrderStock=2&area=<%= area %>" target="_blank">显示红色处理中</a>
</p>
</form>
<%if(allOrderStock == 0){ %>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%}%>