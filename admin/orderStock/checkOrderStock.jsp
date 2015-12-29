<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.action.vo.*, adultadmin.util.*" %>
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

OrderStockAction action = new OrderStockAction();
action.orderStock(request, response);

List productList = (List) request.getAttribute("productList");
List outList = (List) request.getAttribute("outList");
OrderStockBean bean = (OrderStockBean) request.getAttribute("bean");

int i, count;
voProduct product = null;
OrderStockProductBean sh = null;
Iterator itr = null;

int stockStatus = StringUtil.StringToId((String) request.getAttribute("stockStatus"));

boolean changeArea = false;
if(bean.getStatus() == OrderStockBean.STATUS1){
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
<form method="post" action="editOrderStock.jsp">
<p style="padding-left: 10px;">
<a href="../order.do?id=<%= bean.getOrder().getId() %>&split=1">订单:<%= bean.getOrderCode() %></a><br/>
操作名称：<input type="text" name="name" size="40" value="<%=bean.getName()%>" />&nbsp;&nbsp;编号：<%= bean.getCode() %>&nbsp;&nbsp;状态：
<%if(bean.getStatus() == OrderStockBean.STATUS1){%>
<font color="red"><%=bean.getStatusName()%></font> 
<%if(stockStatus == 0){%><%= OrderStockBean.getStockStatusName(stockStatus) %><%} else {%><font color="red"><%= OrderStockBean.getStockStatusName(stockStatus) %></font><%}%>
<input type="button" name="btnStockout" value="确认申请出货" onclick="if(confirm('确定？')){this.disabled=true;window.location.href='completeOrderStock.jsp?operId=<%=bean.getId()%>&act=stockout';}" />
<%--
<a href="completeOrderStock.jsp?operId=<%=bean.getId()%>&act=stockout" onclick="return complete();">确认申请出货</a>
--%>
<%
}
//待出货状态
else if(bean.getStatus() == OrderStockBean.STATUS6){%>
<font color="blue"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(34)) /*if(!isXiaoshou && !isKefu)*/{ %>
<a href="completeOrderStock.jsp?operId=<%=bean.getId()%>&act=confirm" onclick="return complete();">复核完毕确认出货</a>
<%} %>
<%
}
else{%><%=bean.getStatusName()%><%}%>&nbsp;<br/>
备注：<textarea name="remark" cols="50" rows="5"><%=bean.getRemark()%></textarea>地区：<select name="area" <%= (!changeArea)?"disabled=\"disabled\"":"" %> ><option value="1" <%if(bean.getStockArea() == 1){%>selected<%}%>>芳村</option><option value="2" <%if(bean.getStockArea() == 2){%>selected<%}%>>广速</option></select>注：在已完成状态下地区将不被更改。<br/>
<p align="right">订单地址:<%= bean.getOrder().getAddress() %>&nbsp;&nbsp;&nbsp;&nbsp;
</p>
<%if(!changeArea){ %>
<input type="hidden" name="area" value="<%=bean.getStockArea()%>"/>
<%} %>
<input type="hidden" name="id" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="orderStock.jsp"/>
</p>
</form>
<form method="post" action="editBjGd.jsp">
<table width="100%" border="1">
<tr>
  <td colspan="9" align="left">快递公司：<%= StringUtil.convertNull((String)voOrder.deliverMapAll.get(String.valueOf(bean.getDeliver())))%></td>
</tr>
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>产品原名称</td>
  <td>产品编号</td>
  <td>出库量</td>
  <td>当前北库库存</td>
  <td>当前芳村库存</td>
  <td>当前广速库存</td>
  <td>查进销存</td>
</tr>
<%
count = productList.size();
int stockOutCount = 0;
OrderStockProductBean outSh = null;
itr = outList.iterator();
i = 0;
while(itr.hasNext()){
	i++;
	sh = (OrderStockProductBean) itr.next();
	stockOutCount = sh.getStockoutCount();
	outSh = sh;
%>
<tr>
  <td><%= i %></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getCode()%></a></td>
  <td><input type="text" name="stockOutCount<%=sh.getProduct().getId()%>" size="5" value="<%=stockOutCount%>" readonly/></td>
  <td><%=sh.getProduct().getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><%=sh.getProduct().getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><%=sh.getProduct().getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>  
  <td><a href="../productStock/stockCardList.jsp?productCode=<%= sh.getProduct().getCode() %>" target="_blank">查</a></td>
</tr>
<%
}
%>
</table>
<input type="hidden" name="operId" value="<%=bean.getId()%>"/>
</form>