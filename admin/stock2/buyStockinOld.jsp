<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.buy.*, adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@page import="adultadmin.util.StringUtil"%>
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
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.PageUtil" %>
<%
BuyStockinOldAction action = new BuyStockinOldAction();
action.buyStockin(request, response);

List productList = (List) request.getAttribute("productList");
List bsipList = (List) request.getAttribute("bsipList");
BuyStockinBean bean = (BuyStockinBean) request.getAttribute("bean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
List proxyList = (List) request.getAttribute("proxyList");
Map psMap = (Map) request.getAttribute("psMap");
ArrayList errorProductList = (ArrayList)request.getAttribute("errorProductList");

int i, count;
voProduct product = null;
BuyStockinProductBean bsip = null;
Iterator itr = null;
voSelect proxy = null;

boolean check = false;
if(request.getParameter("check") != null){
	check = true;
}

Boolean hasDifObj = (Boolean) request.getAttribute("hasDif");
boolean hasDif = false;
if(hasDifObj != null){
	hasDif = hasDifObj.booleanValue();
}

Map difMap = (Map) request.getAttribute("difMap");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script>
function check(allCheck, singleCheck){
	if(allCheck){
		if(confirm("入库数量与进货数量存在不一致的情况，要继续入库吗？")){
			if(confirm("请先详细核对一遍！")){
				return true;
			}
		}
	} else if(singleCheck){
		if(confirm("该商品入库数量与进货数量存在不一致的情况，要继续入库吗？")){
			if(confirm("请先详细核对一遍！")){
				return true;
			}
		}
	} else {
		if(confirm("请先详细核对一遍!核对无误后再点击一次确认入库链接")){
			return true;
		}
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
	document.addBuyStockinForm.productCode.value=code;
	var value = 1;
	value = prompt('将添加产品编号为'+code+'的产品，请输入数量，并确认', 1);
	if(value != null && value > 0){
		document.addBuyStockinForm.stockinCount.value = value;
		document.addBuyStockinForm.submit();
	}
}
</script>
<p align="center">采购入库操作</p>
<form method="post" action="editBuyStockin.jsp">
操作名称：<input type="text" name="name" size="40" value="<%=StringUtil.convertNull(bean.getName())%>" />&nbsp;&nbsp;
编号：<%= bean.getCode() %>&nbsp;&nbsp;状态：
<%if(bean.getStatus() != BuyStockinBean.STATUS4){%>
<font color="red"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(53)){ %>
<%if(bean.getStatus() == BuyStockinBean.STATUS2){ %>
<%if(check){%>
<a href="completeBuyStockinOld.jsp?buyStockinId=<%=bean.getId()%>" onclick="return complete();">确认入库</a>
<%}else{%>
<a href="buyStockinOld.jsp?id=<%=bean.getId()%>&check=1" onclick="return check(<%= (hasDif)?"true":"false" %>, false);">确认入库</a>
<%}%>
<%} // end bean.getStatus() == BuyStockinBean.STATUS2 %>
<%} // end group.isFlag(53) %>
<%
}else{%><%=bean.getStatusName()%><%}%><br/>
备注：<textarea name="remark" cols="50" rows="5"><%=bean.getRemark()%></textarea>地区：<select name="area"><option value="0" <%if(bean.getStockArea() == 0){%>selected<%}%>>北京</option><option value="1" <%if(bean.getStockArea() == 1){%>selected<%}%>>广东</option></select><%if(bean.getStatus() != BuyStockinBean.STATUS4 && bean.getStatus() != BuyStockinBean.STATUS6 && !check){ %><input type="submit" value="修改" /><%} %>注：在已完成状态下地区将不被更改。<br/>
<input type="hidden" name="buyStockinId" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="buyStockin.jsp"/>
</form>
<fieldset>
   <legend>操作</legend>
<%
if((bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1) && bean.getBuyStockId() == 0){
%>
<form method="post" name="addBuyStockinForm" action="addBuyStockinItem.jsp">
<p align="center">
产品编号：<input type="text" name="productCode" value="" size="8"/>&nbsp;&nbsp;入库量：<input type="text" name="stockinCount" value="1" size="3"/><input type="submit" value="添加"><br/>
<input type="hidden" name="buyStockinId" value="<%=bean.getId()%>"/>
</p>
</form>
<%
}
%>
<p align="center"><%if(group.isFlag(183)){ %><a href="buyAdminHistory.jsp?logId=<%=bean.getId()%>&logType=<%= BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN %>" target="_blank">人员操作记录</a>|<%} %><a href="buyStockinListOld.jsp">返回采购入库操作记录列表</a><%if(group.isFlag(31)) /*if(isSystem || (isShangpin && isGaojiAdmin))*/{ %>|<%if(bean.getStatus()==BuyStockinBean.STATUS4){ %><a href="buyStockinOldPrint.jsp?id=<%=bean.getId()%>" target="_blank">导出列表</a><%} else{ %>导出列表<%}%><%} %></p>
</fieldset>
<%if(bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1){ %>
<%if(bean.getBuyStockId() == 0){ %>
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
<%} %>
<%} // end psBean == null %>
<form method="post" action="editBuyStockinItemOld.jsp">
<style type=text/css>   
  .redfont{color:red}
</style>
<table width="95%" border="1">
<tr align="right">
	<td colspan="14">产品数量总和：
<%= request.getAttribute("stockinCount") %>
	</td>
</tr>
<tr>
  <td rowspan="2">序号</td>
  <td rowspan="2">产品名称</td>
  <td rowspan="2">原名称</td>
  <td rowspan="2">产品编号</td>
  <td rowspan="2">入库量</td>
  <td colspan="4">北京库房</td>
  <td colspan="4">广东库房</td>
  <td rowspan="2">状态及操作</td>
  <td rowspan="2">查进销存</td>
</tr>
<tr>
	<td>待检库</td>
	<td>可发货</td>
	<td>不可发货</td>
	<td>残次品库</td>
	<td>待检库</td>
	<td>可发货</td>
	<td>不可发货</td>
	<td>残次品库</td>
</tr>
<%
count = bsipList.size();
int stockinCount = 0;
int stockInGd = 0;
for(i = 0; i < count; i ++){
	bsip = (BuyStockinProductBean) bsipList.get(i);
	product = bsip.getProduct();
%>
<tr>
  <td<%if(errorProductList!=null){if(errorProductList.contains(Integer.valueOf(bsip.getProductId()))){%> class=redfont<%}}%>><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>" target="_blank"><%=product.getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>" target="_blank"><%=product.getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>" target="_blank"><%=product.getCode()%></a></td>
<%
    stockinCount = 0;
	stockinCount = bsip.getStockInCount();
    Object dif = difMap.get(Integer.valueOf(bsip.getId()));
    boolean singleDif = false;
    if(dif != null){
    	singleDif = dif.equals("hasDif");
    }
%>
  <td><input type="text" name="stockinCount<%=product.getId()%>" size="5" value="<%=stockinCount%>" <%if(!group.isFlag(72) || (bean.getStatus() == BuyStockinBean.STATUS4) || check){%>readonly<%}else if(bsip.getStatus() == BuyStockinProductBean.BUYSTOCKIN_DEALED){%>readonly<%}%>/></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A0T1")).getStock() %></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A0T0")).getStock() %></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A0T2")).getStock() + ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A0T3")).getStock() + ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A0T4")).getStock() %></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A0T5")).getStock() %></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A1T1")).getStock() %></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A1T0")).getStock() %></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A1T2")).getStock() + ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A1T3")).getStock() + ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A1T4")).getStock() %></td>
  <td><%= ((ProductStockBean)psMap.get("ps" + bsip.getProductId() + "A1T5")).getStock() %></td>
  <td><%if(bsip.getStatus() == BuyStockinProductBean.BUYSTOCKIN_UNDEAL){%><font color="red">未入库</font>
  <%if(group.isFlag(53)){ %>
  <%if(bean.getStatus() != BuyStockinBean.STATUS0 && bean.getStatus() != BuyStockinBean.STATUS1){ %>
  <%if(!check){%><a href="buyStockinOld.jsp?id=<%=bean.getId()%>&check=1" onclick="return check(false, <%= (singleDif)?"true":"false" %>);">入库</a><%} else {%><a href="completeBuyStockinOld.jsp?buyStockinId=<%=bean.getId()%>&productId=<%=product.getId()%>" onclick="return complete();">入库</a><%}%>
  <%} // end psBean != null %>
  <%} // end bean.getStatus() != BuyStockinBean.STATUS0 && bean.getStatus() != BuyStockinBean.STATUS1 %>
  <%} else {%>已入库<%}%>
  <%if(bsip.getStatus() == BuyStockinProductBean.BUYSTOCKIN_UNDEAL){%><a href="deleteBuyStockinItemOld.jsp?buyStockinId=<%=bean.getId()%>&productId=<%=product.getId()%>" onclick="return confirm('确认删除？');">删除</a><%}%>
  </td>
  <td><a href="../productStock/stockCardList.jsp?productCode=<%= bsip.getProductCode() %>" target="_blank">查</a></td>
</tr>
<%
}
%>
</table>
<%if(group.isFlag(72)){ %>
<%if((bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1 || bean.getStatus() == BuyStockinBean.STATUS2 || bean.getStatus() == BuyStockinBean.STATUS3) && !check){%><p align="center"><input type="submit" value="修改"/></p><%}%>
<%} // end group.isFlag(72) %>
<input type="hidden" name="buyStockinId" value="<%=bean.getId()%>"/>
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%
	BuyStockBean plan = (BuyStockBean) request.getAttribute("buyStock");
	List buyStockProductList = (List) request.getAttribute("buyStockProductList");
if(plan != null && buyStockProductList != null){
%>
<fieldset>
   <legend><a href="../stock2/buyStock.jsp?stockId=<%= plan.getId() %>">采购进货单——<%= plan.getCode() %></a></legend>
<table width="95%" border="1">
<tr align="right">
	<td colspan="10">产品数量总和：<%= request.getAttribute("stockCount") %>&nbsp;&nbsp;产品价格总和：<%= request.getAttribute("totalStockPurchasePrice") %></td>
</tr>
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>产品编号</td>
  <td>进货量</td>
  <td>预计进货价</td>
  <td>代理商</td>
  <td>北京库存</td>
  <td>广东库存</td>
</tr>
<%
count = buyStockProductList.size();
for(i = 0; i < count; i ++){
	BuyStockProductBean bpp2 = (BuyStockProductBean) buyStockProductList.get(i);
	product = bpp2.getProduct();
	itr = proxyList.listIterator();
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../stock2/productBuyStockHistory.jsp?productId=<%=bpp2.getProduct().getId()%>"><%=bpp2.getProduct().getName()%></a></td>
  <td><a href="../stock2/productBuyStockHistory.jsp?productId=<%=bpp2.getProduct().getId()%>"><%=bpp2.getProduct().getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=bpp2.getProduct().getId()%>"><%=bpp2.getProduct().getCode()%></a></td>
  <td><%=bpp2.getBuyCount() %></td>
  <td><%=bpp2.getPurchasePrice() %></td>
  <td>
  	<select id="stockProductProxyId<%=bpp2.getProduct().getId()%>" disabled="true">
<%
	while(itr.hasNext()){
		proxy = (voSelect)itr.next();
%>
  		<option value="<%= proxy.getId() %>"><%= proxy.getName() %></option>
<%} %>
  	</select>
  	<script>selectOption(document.getElementById('stockProductProxyId<%=bpp2.getProduct().getId()%>'), '<%= bpp2.getProductProxyId() %>');</script>
  </td>
  <td><%=bpp2.getProduct().getStock()%></td>
  <td><%=bpp2.getProduct().getStockGd()%></td>
</tr>
<%
}
%>
</table>
</fieldset>
<%}%>