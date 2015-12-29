<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../../taglibs.jsp"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.buy.*, adultadmin.action.vo.*, adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%
BuyStockinAction action = new BuyStockinAction();
action.buyStockinPrice(request, response);

List productList = (List) request.getAttribute("productList");

List bsipList = (List) request.getAttribute("bsipList");
BuyStockinBean bean = (BuyStockinBean) request.getAttribute("bean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
paging.setPrefixUrl("buyStockinPrice.jsp?id=" + bean.getId());
int i, count;
voProduct product = null;
BuyStockinProductBean bsip = null;
Iterator itr = null;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script type="text/javascript">
<!--
	function changeProxy(varSelect){
		varSelects = getElementsByName_iefix("select", "proxyId");
		var i= 0;
		for(i=0; i<varSelects.length;i++){
			varSelects[i].selectedIndex = varSelect.selectedIndex;
		}
	}
	function confirmSubmit(){
		if(confirm("请先详细核对一遍!核对无误后再点击提交按钮")){
			document.getElementById("submitBut").style.display="inline";
		}
	}
	function checkSubmit(){
		return confirm("确认提交吗？ 提交后可以进行入库操作.");
	}
//-->
</script>
<p align="center">修改采购入库价格</p>
操作名称：<%=bean.getName()%>
<form method="post" action="editBuyStockinPrice.jsp" onsubmit="return checkSubmit();" >
<table width="95%" border="1">
<tr align="right">
	<td colspan="15">产品数量总和：
<%= request.getAttribute("stockinCount") %>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;总金额：<%= StringUtil.convertNull((String)request.getAttribute("totalPrice")) %>
	</td>
</tr>
<tr>
  <td>序号</td>
  <td>产品编号</td>
  <td>原名称</td>
  <td>代理商</td>
  <td>数量</td>
  <td>采购价格</td>
  <td>金额</td>
  <td>&nbsp;</td>
  <td>序号</td>
  <td>产品编号</td>
  <td>原名称</td>
  <td>代理商</td>
  <td>数量</td>
  <td>采购价格</td>
  <td>金额</td>
</tr>
<%
count = bsipList.size();
int half = count/2 + count%2;
int stockinCount = 0;
for(i = 0; i < half; i ++){
	bsip = (BuyStockinProductBean) bsipList.get(i);
	product = bsip.getProduct();
%>
<%
    stockinCount = bsip.getStockInCount();
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>&simple=1"><%=product.getCode()%></a></td>
  <td><input type="text" name="oriname" value="<%= bsip.getOriname() %>" /></td>
  <td><select id="proxyId<%= product.getId() %>" name="proxyId" class="bd" style="width:100" onchange="changeProxy(this)">
	<logic:present name="proxyList" scope="request"><logic:iterate name="proxyList" id="proxyItem" >
		<option value="<bean:write name="proxyItem" property="id" />"><bean:write name="proxyItem" property="name" /></option>
	</logic:iterate></logic:present> 
	</select>
	<script>selectOption(document.getElementById('proxyId<%= product.getId() %>'), '<%= bsip.getProductProxyId() %>')</script>
  </td>
  <td><%=stockinCount %></td>
  <td><input name="price3" value="<%= bsip.getPrice3() %>" size="5" /><input type="hidden" name="productId" value="<%=product.getId() %>" /></td>
  <td><%= bsip.getPrice3() * stockinCount %></td>
  <td>&nbsp;</td>
<%
	if((half + i) < count){
		bsip = (BuyStockinProductBean) bsipList.get(half + i);
		product = bsip.getProduct();
%>
<%
	stockinCount = bsip.getStockInCount();
%>
  <td><%=(half + i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>&simple=1"><%=product.getCode()%></a></td>
  <td><input type="text" name="oriname" value="<%= bsip.getOriname() %>" /></td>
  <td><select id="proxyId<%= product.getId() %>" name="proxyId" class="bd" style="width:100" onchange="changeProxy(this)">
	<logic:present name="proxyList" scope="request"><logic:iterate name="proxyList" id="proxyItem" >
		<option value="<bean:write name="proxyItem" property="id" />"><bean:write name="proxyItem" property="name" /></option>
	</logic:iterate></logic:present> 
	</select>
	<script>selectOption(document.getElementById('proxyId<%= product.getId() %>'), '<%= bsip.getProductProxyId() %>')</script>
  </td>
  <td><%=stockinCount %></td>
  <td><input name="price3" value="<%= bsip.getPrice3() %>" size="5" /><input type="hidden" name="productId" value="<%=product.getId() %>" /></td>
  <td><%= bsip.getPrice3() * stockinCount %></td>
<%
	} else {
%>
  <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
<%
	}
%>
</tr>
<%
}
%>
<tr align="center">
<td colspan="15" >
<input id="submitBut" type="submit" value="确认价格和代理商无误/提交" <%if(bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1){ %>style="display:none;"<%} %> />&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" value="修改" onclick="confirmSubmit();" />&nbsp;&nbsp;&nbsp;&nbsp;
<input type="reset" value="重置" />&nbsp;&nbsp;&nbsp;&nbsp;
</td>
</tr>
</table>
<input type="hidden" name="buyStockinId" value="<%= bean.getId() %>" />
</form>
<p align="center"><a href="./buyStockinListOld.jsp">返回采购入库列表</a></p>