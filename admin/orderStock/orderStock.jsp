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
	request.setAttribute("url", request.getHeader("Referer")) ;
	
OrderStockAction action = new OrderStockAction();
action.orderStock(request, response);

List outList = (List) request.getAttribute("outList");
OrderStockBean bean = (OrderStockBean) request.getAttribute("bean");
String gfExchange = (String) request.getAttribute("gfExchange");

int i;
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
<p align="center">订单出货操作</p>
<form method="post" action="editOrderStock.jsp">
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
else if(bean.getStatus() == OrderStockBean.STATUS2){%>
<font color="blue"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(34)) /*if(!isXiaoshou && !isKefu)*/{ %>
<a href="reCheckOrderStock.jsp?operId=<%=bean.getId()%>&status=<%= OrderStockBean.STATUS6 %>&back=scanOrderStock.jsp">申请复核</a>
<%} %>
<%
}
//复核状态
else if(bean.getStatus() == OrderStockBean.STATUS6){%>
<font color="blue"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(133)) /*if(!isXiaoshou && !isKefu)*/{ %>
<a href="completeOrderStock.jsp?operId=<%=bean.getId()%>&act=confirm" onclick="return complete();">复核完毕确认出货</a>
<%} %>
<%
}
//复核通过
else if(bean.getStatus() == OrderStockBean.STATUS3){%>
<font color="blue"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(34)&&false) /*if(!isXiaoshou && !isKefu)*/{ %>
<a href="returnOrderStock.jsp?operId=<%=bean.getId()%>&status=<%= OrderStockBean.STATUS7 %>" onclick="return complete();">实物未退回</a>&nbsp;&nbsp;
<a href="returnOrderStock.jsp?operId=<%=bean.getId()%>&status=<%= OrderStockBean.STATUS8 %>" onclick="return complete();">实物已退回</a>
<%} %>
<%
}
//复核通过
else if(bean.getStatus() == OrderStockBean.STATUS7){%>
<font color="blue"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(34)) /*if(!isXiaoshou && !isKefu)*/{ %>
<a href="returnOrderStock.jsp?operId=<%=bean.getId()%>&status=<%= OrderStockBean.STATUS8 %>" onclick="return complete();">实物已退回</a>
<%} %>
<%
}
else{%><%=bean.getStatusName()%><%}%>&nbsp;<br/>
备注：<textarea name="remark" cols="30" rows="5"><%=bean.getRemark()%></textarea>
地区：
<%if(bean.getStockArea()==1){ %>芳村
<%}else if(bean.getStockArea()==2){ %>广速
<%}else if(bean.getStockArea()==3){ %>增城<%} %>
<input type="button" value="修改" <%if(bean.getStatus() == OrderStockBean.STATUS2){%>onclick="javascript:confirm1(document.forms[0], <%=bean.getStockArea()%>)"<%} else {%>onclick="javascript:confirm1(document.forms[0], -1)"<%}%>>&nbsp;&nbsp;
<%if(gfExchange != null){ %><a href="../productStock/createStockExchange.jsp?type=2&srcId=<%= bean.getId() %>&stockInArea=1&stockInType=0&stockOutArea=2&stockOutType=0&forward=auto" target="_blank" >生成调拨单(广速-芳村)</a><%}%><br/>
<p align="right">订单地址:<%= bean.getOrder().getAddress() %>&nbsp;&nbsp;&nbsp;&nbsp;</p>
<%if(!changeArea){ %>
<input type="hidden" name="area" value="<%=bean.getStockArea()%>"/>
<%} %>
<input type="hidden" name="id" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="orderStock.jsp"/>
</form>

<form method="post" action="editOrderStock.jsp">
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
<p align="center"><a href="stockAdminHistory.jsp?operId=<%=bean.getId()%>&logType=10" target="_blank">人员操作记录</a>|<a href="orderStockList.jsp">返回订单出货操作记录列表</a>|<a href="orderStockPrint.jsp?id=<%=bean.getId()%>" target="_blank">导出列表</a></p>