<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, java.net.*" %><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.bean.*,adultadmin.util.DateUtil" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
response.setContentType("application/vnd.ms-excel");

ProductStockAction action = new ProductStockAction();
action.stockExchange(request, response);

Map productMap = (Map) request.getAttribute("productMap");
List sepList ;
sepList= (ArrayList) request.getAttribute("sepList");
StockExchangeBean bean = (StockExchangeBean) request.getAttribute("bean");

int i, count,totalcount=0;
count = sepList.size();
voProduct product = null;
StockExchangeProductBean sep = null;

String now = DateUtil.getNow().substring(0, 10);
String fileName = now;
response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>

<%int b=1,c=0;
if(count>20&&count%20!=0) b=(count/20)+1;
if(count%20==0) b=(count/20);
for(int a=0;a<b;a++) {

%>
<table border="1" cellspacing="0">
<tr><td colspan="8">调拨编号：<%=bean.getCode() %> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;调拨入库单</td></tr>
<tr><td colspan="3">源库类型：<%=ProductStockBean.getStockTypeName(bean.getStockOutType())%></td><td colspan="3">源库地点：<%= ProductStockBean.getAreaName(bean.getStockOutArea()) %></td><td colspan="2">出库审核人：&nbsp;&nbsp;&nbsp;&nbsp;_______________</td></tr>
<tr><td colspan="3">目的库类型：<%=ProductStockBean.getStockTypeName(bean.getStockInType())%></td><td colspan="3">目的库地点：<%=ProductStockBean.getAreaName(bean.getStockInArea()) %></td><td colspan="2">出库接收人：&nbsp;&nbsp;&nbsp;&nbsp;_______________</td></tr>
<tr><td>序号</td><td>编号</td><td>原名称</td><td>数量</td><td>序号</td><td>编号</td><td>原名称</td><td>数量</td></tr>
<% //System.out.println(count-20*(c-1));%>
<%if((count-20*c)<=10){
	for(i = 0; i < (count-20*c); i ++){
	sep = (StockExchangeProductBean) sepList.get(i+20*c);
	product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));%>
<tr>
	  <td><%=(i + 1)+20*c%></td>	  
	  <td><%=product.getCode()%></td>
	  <td><%=new StringUtil().getString(product.getOriname(),24)%></td>
	  <td><%=sep.getStockOutCount() %></td><%totalcount=totalcount+sep.getStockOutCount(); %>
	  <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
</tr>
	<%} %>
		<%for(int t=0;t<=9-(count-20*c);t++){ //行数不够的自动补足%>
<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
		<%} }%>

<%if((count-20*c)>10){
	for(i = 0; i <= 9; i ++){
	//System.out.println("1==>"+(i+20*c));
	sep = (StockExchangeProductBean) sepList.get(i+20*c);//A横向索引0 1..
	product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));%>
<tr>
	  <td><%=(i + 1)+20*c%></td>
	  <td><%=product.getCode()%></td>
	  <td><%=new StringUtil().getString(product.getOriname(),24)%></td>
	  <td><%=sep.getStockOutCount() %></td><%totalcount=totalcount+sep.getStockOutCount(); %>
<%
	int col=i+10;
	if((i+10)>(count-20*c-1)) col=(count-20*c-1);
	//System.out.println("2==>"+(col+20*c));
	sep = (StockExchangeProductBean) sepList.get(col+20*c);//B横向索引10 11..
	product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));
	if((i+10)<=(count-20*c-1)){
 %>  
	  <td colspan="4"><table border="1" cellspacing="0"><tr>
	  	<td><%=(col+ 1)+20*c%></td>
	    <td><%=product.getCode()%></td>
	    <td><%=new StringUtil().getString(product.getOriname(),24)%></td>
	    <td><%=sep.getStockOutCount() %></td><%totalcount=totalcount+sep.getStockOutCount(); %>
	    </tr></table>
	  </td><%}else {%>
	  <td colspan="4"></td>
</tr>
<%}}
}%>
	
 	
<tr>
	<td colspan="2" align="left">合计：</td>
	<td colspan="3" align="left"><%=totalcount %><%totalcount=0; %></td>
	<td colspan="3">生成人：<%=(request.getParameter("outUser") !=null)? request.getParameter("outUser"):""%></td>
</tr>
<tr>
	<td colspan="4">调拨出库方盖章：</td>
	<td colspan="4">调拨入库方盖章：</td>
</tr>
<tr>
	<td colspan="4">调拨出库方签字：</td>
	<td colspan="4">调拨入库方签字：</td>
</tr>
</table>
<table><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr></table>
<%
c++;//序号
}
%>