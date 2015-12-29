<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*" %>
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
action.cancelStockin(request, response);

List productList = (List) request.getAttribute("productList");
List inList = (List) request.getAttribute("inList");
StockOperationBean bean = (StockOperationBean) request.getAttribute("bean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
HashMap batchMap = (HashMap)request.getAttribute("batchMap");
int i, count;
voProduct product = null;
StockHistoryBean sh = null;
Iterator itr = null;

boolean check = false;
if(request.getParameter("check") != null){
	check = true;
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
	if(confirm("确认出入库？确认后不能撤销。")){
		return true;
	}
	return false;
}
function addproduct(code,pid){
	document.addCancelExchangeForm.productCode.value=code;
	var value = 1;
	value = prompt('将添加产品编号为'+code+'的产品，请输入数量，并确认', 1);
	if(value != null && value > 0){
			document.addCancelExchangeForm.count.value = value;
		document.addCancelExchangeForm.submit();
	}
}
</script>
<p align="center"><%if(bean.getType()==StockOperationBean.CANCEL_EXCHANGE){ %>退换货操作<%}else{ %>退货入库操作<%} %></p>
<form method="post" action="editStockOperation.jsp">
操作名称：<input type="text" name="name" size="50" value="<%=bean.getName()%>" />状态：
<%if(bean.getStatus() == StockOperationBean.STATUS1){%>
<font color="red"><%=bean.getStatusName()%></font> 
<%if(check){%>
<a href="completeCancelStockin.jsp?operId=<%=bean.getId()%>" onclick="return complete();">确认入库</a>
<%}else{%>
<a href="cancelStockin.jsp?id=<%=bean.getId()%>&check=1" onclick="return check();">确认入库</a>
<%}
} else if(bean.getStatus() == StockOperationBean.STATUS2){%>
&nbsp;&nbsp;&nbsp;<input type="button" value="换货提交" onclick="if(confirm('确认要提交更换的商品？')){window.location.href='completeCancelExchange.jsp?operId=<%=bean.getId() %>';return true;}else {return false;}" />
<%} else{%><%=bean.getStatusName()%><%}%><br/>
<%
if(bean.getType()==StockOperationBean.BAD_EXCHANGE){
	String remarkXS = "";
	String remarkKC = "";
	if(bean.getRemark().indexOf("_split_")!=-1){
		String[] remarks = bean.getRemark().split("_split_");
		remarkXS = remarks[0];
		remarkKC = remarks[1];
	} 
%>
销售部备注：<textarea name="remarkXS" cols="50" rows="5" <%=  /*(isXiaoshou || isSystem)*/(group.isFlag(32))?"":"readonly=\"readonly\"" %> ><%= remarkXS %></textarea><br/>
库存部备注：<textarea name="remarkKC" cols="50" rows="5" <%=  /*(isShangpin || isSystem)*/(group.isFlag(33))?"":"readonly=\"readonly\"" %> ><%= remarkKC %></textarea>&nbsp;&nbsp;<input type="submit" value="修改"><br/>
<%} else {%>
备注：<textarea name="remark" cols="50" rows="5"><%=bean.getRemark()%></textarea><%if(bean.getType() == StockOperationBean.CANCEL_EXCHANGE){ %>地区：<%if(!(bean.getStatus() == StockOperationBean.STATUS1)){ %><%= ProductStockBean.getAreaName(bean.getArea()) %><input type="hidden" name="area" value="<%= bean.getArea() %>" /><%}else { %><select name="area"><option value="0" <%if(bean.getArea() == 0){%>selected<%}%>>北库</option><option value="1" <%if(bean.getArea() == 1){%>selected<%}%>>芳村</option><option value="2" <%if(bean.getArea() == 2){%>selected<%}%>>广速</option><option value="2" <%if(bean.getArea() == 3){%>selected<%}%>>增城</option></select><%} %><%} else { %><input type="hidden" name="area" value="<%= bean.getArea() %>" /><%} %>&nbsp;&nbsp;<input type="submit" value="修改"><br/>
<%} %>
<input type="hidden" name="id" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="cancelStockin.jsp"/>
</form>
<form method="post" action="editCancelStockin.jsp">
<table width="95%" border="1">
<tr align="right">
	<td colspan="12">产品数量总和：
<%
if(bean.getArea() == 0){
%>
<%= request.getAttribute("productCountBj") %>
<%} else { %>
<%= request.getAttribute("productCountGd") %>
<%} %>
	</td>
</tr>
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>产品编号</td>
<%
if(bean.getArea() == 0){
%>
  <td>北库入库量</td>
<%
}
else if(bean.getArea() == 1){
%>
  <td>芳村入库量</td>
<%}else if(bean.getArea()==3){
%>
  <td>增城入库量</td>
<%} else {
%>
  <td>广速入库量</td>
<%} %>
  <td>订单内数量</td>
  <td>北库库存</td>
  <td>芳村库存</td>
  <td>广速库存</td>
  <td>增城库存</td>
  <td>批次号（退货量）<%if(group.isFlag(290)){ %>/批次价<%} %></td>
  <td>状态及操作</td>
</tr>
<%
count = productList.size();
int stockInBj = 0;
int stockInGd = 0;
StockHistoryBean inSh = null;
for(i = 0; i < count; i ++){
	product = (voProduct) productList.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="productStockHistory.jsp?id=<%=product.getId()%>"><%=product.getName()%></a></td>
  <td><a href="productStockHistory.jsp?id=<%=product.getId()%>"><%=product.getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getCode()%></a></td>
<%
    stockInBj = 0;
    stockInGd = 0;
	inSh = null;
	itr = inList.iterator();
    while(itr.hasNext()){
		sh = (StockHistoryBean) itr.next();
		if(sh.getProductId() == product.getId()){
			stockInBj = sh.getStockBj();
			stockInGd = sh.getStockGd();
			inSh = sh;
		}
	}
%>
  <td><input type="text" name="stockInBj<%=product.getId()%>" size="5" value="<%=(stockInBj == -1)?"":""+stockInBj%>" /></td>
  <td><%=product.getBuyCount()%></td>
  <td><%=product.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN)%></td>
  <td><%=product.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN)%></td>
  <td><%=product.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN)%></td>
  <td><%=product.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)%></td>
  <td><%=batchMap.get(Integer.valueOf(product.getId()))%></td>
  <td><%if(inSh != null){ if(inSh.getStatus() == StockHistoryBean.UNDEAL){%><font color="red">未入库</font>
  <%if(!check){%><a href="cancelStockin.jsp?id=<%=bean.getId()%>&check=1" onclick="return check();">入库</a><%} else {%><a href="completeCancelStockin.jsp?operId=<%=bean.getId()%>&productId=<%=product.getId()%>" onclick="return complete();">入库</a><%}%>
  <%} else {%>已入库<%} }%>
  <%if(inSh == null || inSh.getStatus() == StockHistoryBean.UNDEAL){%><a href="deleteCancelStockinItem.jsp?operId=<%=bean.getId()%>&productId=<%=product.getId()%>" onclick="return confirm('确认删除？');">删除</a><%}%>
  </td>
</tr>
<%
}
%>
</table>
<%if(bean.getStatus() == StockOperationBean.STATUS1 && !check){%><p align="center"><input type="submit" value="修改"/></p><%}%>
<input type="hidden" name="operId" value="<%=bean.getId()%>"/>
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>

<%-- 退换货 操作， 添加更换产品 部分 --%>
<%
String exchangeProductCount = (String)request.getAttribute("exchangeProductCount");
List exchangeProductList = (List) request.getAttribute("exchangeProductList");
%>
<%if(bean.getType()==StockOperationBean.CANCEL_EXCHANGE || bean.getType() == StockOperationBean.BAD_EXCHANGE){ %>
<form method="post" action="editCancelExchangeItem.jsp">
<table width="95%" border="1">
<tr align="right">
	<td align="left">新添加产品</td>
	<td colspan="8 ">产品数量总和：<%= exchangeProductCount %></td>
</tr>
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>产品编号</td>
  <td>数量</td>
  <td>北库库存</td>
  <td>芳村库存</td>
  <td>广速库存</td>
  <td>增城库存</td>
  <td>操作</td>
</tr>
<%
count = exchangeProductList.size();
product = null;
for(i = 0; i < count; i ++){
	product = (voProduct) exchangeProductList.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="productStockHistory.jsp?id=<%=product.getId()%>"><%=product.getName()%></a></td>
  <td><a href="productStockHistory.jsp?id=<%=product.getId()%>"><%=product.getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getCode()%></a></td>
  <td><input type="text" name="count" size="5" value="<%=product.getBuyCount()%>" <%if(bean.getStatus() == StockOperationBean.STATUS3 || bean.getStatus() == StockOperationBean.STATUS4){%>readonly<%}%>/></td>
  <td><%=product.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + product.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><%=product.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + product.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><%=product.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + product.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><%=product.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + product.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><% if(bean.getStatus() == StockOperationBean.STATUS1 || bean.getStatus() == StockOperationBean.STATUS2){%><a href="deleteCancelExchangeItem.jsp?operId=<%=bean.getId()%>&productId=<%=product.getId()%>" onclick="return confirm('确认删除？');">删除</a><%}%>&nbsp;</td>
</tr>
<input type="hidden" name="productId" value="<%= product.getId() %>" />
<%
}
%>
</table>
<p align="center">
<% if(bean.getStatus() == StockOperationBean.STATUS2){ %><input type="button" value="换货提交" onclick="if(confirm('确认要提交更换的商品？')){window.location.href='completeCancelExchange.jsp?operId=<%=bean.getId() %>';return true;}else {return false;}" />&nbsp;&nbsp;<%} %>
<%if(bean.getStatus() == StockOperationBean.STATUS1 || bean.getStatus() == StockOperationBean.STATUS2){%><input type="submit" value="修改"/><%}%>
</p>
<input type="hidden" name="operId" value="<%=bean.getId()%>"/>
</form>
<%
if(bean.getStatus() == StockOperationBean.STATUS1 || bean.getStatus() == StockOperationBean.STATUS2){
%>
<fieldset>
   <legend>操作</legend>
<form method="post" name="addCancelExchangeForm" action="addCancelExchangeItem.jsp">
<p align="center">
产品编号：<input type="text" name="productCode" value="" size="8"/>&nbsp;&nbsp;数量：<input type="text" name="count" value="1" size="3"/><input type="submit" value="添加"><br/>
<input type="hidden" name="operId" value="<%=bean.getId()%>"/>
</p>
</form>
<%--
<p align="center"><a href="stockAdminHistory.jsp?operId=<%=bean.getId()%>" target="_blank">人员操作记录</a>|<a href="cancelStockinList.jsp">返回退货入库操作记录列表</a>|<a href="cancelStockinPrint.jsp?id=<%=bean.getId()%>" target="_blank">导出列表</a></p>
--%>
</fieldset>
<fieldset>
	<legend>产品查询</legend>
	<form method=post action="../isearchproduct.do" target=sp onsubmit="document.all.d1.style.display='block';return true;">
		<input type="hidden" name="type" value="1" />
		<input type=hidden name="code" value="" size=12>
		<p align="center">
			产品名：<input type=text name="name" value="" size=12>
			<input type=submit value="查询产品" onclick="return document.all.d1.style.display='block';">
			<input type=button value="关闭窗口" onclick="document.all.d1.style.display='none';window.close();">
		</p>
	</form>
	<div id=d1 style="display:none">
	<iframe name=sp width=90% height=300 align=center frameborder=0>
	</iframe>
	</div>
</fieldset>
<%
}
if((bean.getStatus() == StockOperationBean.STATUS3 || bean.getStatus() == StockOperationBean.STATUS4) && bean.getOrder().getNewOrder() != null) {
	voOrder newOrder = bean.getOrder().getNewOrder();
%>
<table width="95%" border="1">
	<tr>
	  <td>编号</td>
	  <td>姓名</td>
	  <td>电话</td>
	  <td>地址</td>
	  <td>产品名称</td>
	  <td>总价</td>
	  <td>配送费</td>
	  <td>已到款</td>
	  <td>生成时间</td>
	  <td>购买方式</td>
	</tr>
	<tr>
	  <td><a href="../order.do?id=<%= newOrder.getId() %>"><%= newOrder.getCode() %></a></td>
	  <td><%= StringUtil.toWml(newOrder.getName()) %></td>
	  <td><%= StringUtil.toWml(newOrder.getPhone()) %></td>
	  <td><%= StringUtil.toWml(newOrder.getAddress()) %></td>
	  <td><%= StringUtil.toWml(newOrder.getProducts()) %></td>
	  <td><%= StringUtil.formatFloat(newOrder.getPrice()) %></td>
	  <td><%= StringUtil.formatFloat(newOrder.getPostage()) %></td>
	  <td><%= StringUtil.formatFloat(newOrder.getRealPay()) %></td>
	  <td><%= newOrder.getCreateDatetime() %></td>
	  <td>
	  <%switch(newOrder.getBuyMode()) {
		case 0:%>货到付款<%break;
		case 1:%>邮购<%break;
		case 2:%>上门自取<%break;
		}%>
	  </td>
	</tr>
</table>
<%} %>
<%}%>
<%-- END 退换货 操作， 添加更换产品 部分 END --%>
<p align="center"><a href="stockAdminHistory.jsp?operId=<%=bean.getId()%>&logType=11" target="_blank">人员操作记录</a>|<a href="cancelStockinPrint2.jsp?id=<%=bean.getId()%>" target="_blank">导出列表</a></p>