<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%@ page import="adultadmin.bean.*" %>
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

StockAction action = new StockAction();
action.orderStock(request, response);

List productList = (List) request.getAttribute("productList");
List outList = (List) request.getAttribute("outList");
StockOperationBean bean = (StockOperationBean) request.getAttribute("bean");

int i, count;
voProduct product = null;
StockHistoryBean sh = null;
Iterator itr = null;

int stockStatus = StringUtil.StringToId((String) request.getAttribute("stockStatus"));

boolean changeArea = false;
if(bean.getStatus() == StockOperationBean.STATUS1){
	changeArea = true;
} else {
	if(group.isFlag(33)){ //isShangpin){
		changeArea = true;
	}
}
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>
function check(){
	if(confirm("请先详细核对一遍！")){
		return true;
	}
	return false;
}

function complete(){
	if(confirm("确认？")){		
		return true;
	}
	return false;
}

function confirm1(f, a){
	if(a == -1){
		f.submit();
	}
	else {
		if(a != f.area.value){
	        if(confirm("确认修改地区？库存将被更改！")){		
		        f.submit();
			}
	    }
		else {
			f.submit();
		}
	}
}
</script>
<p align="center">订单出货操作</p>
<form method="post" action="editStockOperation.jsp">
<a href="../order.do?id=<%= bean.getOrder().getId() %>">订单:<%= bean.getOrderCode() %></a><br/>
操作名称：<input type="text" name="name" size="50" value="<%=bean.getName()%>" />状态：
<%if(bean.getStatus() == StockOperationBean.STATUS1){%>
<font color="red"><%=bean.getStatusName()%></font> <%if(stockStatus == 0){%>货源充足<%} else if(stockStatus == 1){%><font color="red">广东缺货</font><%} else if(stockStatus == 2){%><font color="red">北京缺货</font><%} else if(stockStatus == 3){%><font color="red">两地缺货</font><%}%>
<input type="button" name="btnStockout" value="确认申请出货" onclick="if(confirm('确定？')){this.disabled=true;window.location.href='completeOrderStock.jsp?operId=<%=bean.getId()%>&act=stockout';}" />
<%--
<a href="completeOrderStock.jsp?operId=<%=bean.getId()%>&act=stockout" onclick="return complete();">确认申请出货</a>
--%>
<%
}
//待出货状态
else if(bean.getStatus() == StockOperationBean.STATUS2){%>
<font color="blue"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(34)) /*if(!isXiaoshou && !isKefu)*/{ %>
<a href="completeOrderStock.jsp?operId=<%=bean.getId()%>&act=confirm" onclick="return complete();">确认出货</a>
<%} %>
<%
}
else{%><%=bean.getStatusName()%><%}%>&nbsp;广东已发货<%= request.getAttribute("stockoutCountGD") %>单<br/>
备注：<textarea name="remark" cols="50" rows="5"><%=bean.getRemark()%></textarea>地区：<select name="area" <%= (!changeArea)?"disabled=\"disabled\"":"" %> ><option value="0" <%if(bean.getArea() == 0){%>selected<%}%>>北京</option><option value="1" <%if(bean.getArea() == 1){%>selected<%}%>>广东</option></select><input type="button" value="修改" <%if(bean.getStatus() == StockOperationBean.STATUS2){%>onclick="javascript:confirm1(document.forms[0], <%=bean.getArea()%>)"<%} else {%>onclick="javascript:confirm1(document.forms[0], -1)"<%}%>>注：在已完成状态下地区将不被更改。<br/>
<p align="right">订单地址:<%= bean.getOrder().getAddress() %>&nbsp;&nbsp;&nbsp;&nbsp;
<%if(bean.getOrder().getPostageBj() > 0 && bean.getOrder().getPostageGd() > 0){ %>提示:此订单北京发货邮资:<%= bean.getOrder().getPostageBj() %>元,广东发货邮资:<%= bean.getOrder().getPostageGd() %>元,从<%= (bean.getOrder().getPostageDif() > 0)?"广东":"北京" %>发货邮资将节约<%= Math.abs(bean.getOrder().getPostageDif()) %>元,节约比例<%= NumberUtil.price(bean.getOrder().getPostageSavePercent()) %>%<%} %>
</p>
<%if(!changeArea){ %>
<input type="hidden" name="area" value="<%=bean.getArea()%>"/>
<%} %>
<input type="hidden" name="id" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="orderStock.jsp"/>
</form>

<form method="post" action="editBjGd.jsp">
<table width="100%" border="1">
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>产品原名称</td>
  <td>产品编号</td>
<%
if(bean.getArea() == 0){
%>
  <td>北京出库量</td>
<%
}
else {
%>
  <td>广东出库量</td>
<%
}
%>
  <td>当前北京库存</td>
  <td>当前广东库存</td>
</tr>
<%
count = productList.size();
int stockOutBj = 0;
int stockOutGd = 0;
StockHistoryBean outSh = null;
itr = outList.iterator();
i = 0;
while(itr.hasNext()){
	i++;
	sh = (StockHistoryBean) itr.next();
	stockOutBj = sh.getStockBj();
	stockOutGd = sh.getStockGd();
	outSh = sh;
%>
<tr>
  <td><%= i %></td>
  <td><a href="productStockHistory.jsp?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getName()%></a></td>
  <td><a href="productStockHistory.jsp?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getCode()%></a></td>
<%
if(bean.getArea() == 0){
%>
  <td><input type="text" name="stockOutBj<%=sh.getProduct().getId()%>" size="5" value="<%=stockOutBj%>" readonly/></td>
<%
}
else {
%>
  <td><input type="text" name="stockOutGd<%=sh.getProduct().getId()%>" size="5" value="<%=stockOutGd%>" readonly/></td> 
<%
}
%>
  <td><%=sh.getProduct().getStock()%></td>
  <td><%=sh.getProduct().getStockGd()%></td>  
</tr>
<%
}
%>
</table>
<input type="hidden" name="operId" value="<%=bean.getId()%>"/>
</form>

<p align="center"><a href="stockAdminHistory.jsp?operId=<%=bean.getId()%>&logType=10" target="_blank">人员操作记录</a>|<a href="orderStockList.jsp">返回订单出货操作记录列表</a>|<a href="orderStockPrint.jsp?id=<%=bean.getId()%>" target="_blank">导出列表</a></p>