<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@page import="adultadmin.action.admin.ProductStockDownloadAction"%>
<%@ page import="ormap.*" %>
<%
response.setContentType("application/vnd.ms-excel");
response.setHeader("Content-disposition","attachment; filename=jingjiechanpin.xls");
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();

	boolean isSystem = (adminUser.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (adminUser.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (adminUser.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (adminUser.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (adminUser.getPermission() == 7);	//销售部
	boolean isShangpin = (adminUser.getPermission() == 6);	//商品部
	boolean isTuiguang = (adminUser.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (adminUser.getPermission() == 4);	//运营中心
	boolean isKefu = (adminUser.getPermission() == 3);	//客服部	

	ProductStockDownloadAction action = new ProductStockDownloadAction();
	action.execute(request, response);

	String catalogIds = StringUtil.convertNull(request.getParameter("catalogIds"));
	String catalogId2s = StringUtil.convertNull(request.getParameter("catalogId2s"));

	int areaId = StringUtil.toInt(request.getParameter("areaId"));
%>
<html>
<title>买卖宝后台</title>
<body>
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="productStockTable" class="sortable">
          <thead class="sorthead">
              <tr bgcolor="#4688D6">
              <td></td>
              <td width="40" align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td width="40" align="center"><font color="#FFFFFF">批发价</font></td>
<%}%>
<%if(group.isFlag(256)){%>
			  <td width="40" align="center"><font color="#FFFFFF">最低进货税后价</font></td>
<%}%>
              <td width="50" align="center"><font color="#FFFFFF">代理商</font></td>
              <td width="50" align="center"><font color="#FFFFFF">产品状态</font></td>
              <td width="50" align="center"><font color="#FFFFFF">日销量</font></td>
              <td width="50" align="center"><font color="#FFFFFF">上周销量</font></td>
              <td width="50" align="center"><font color="#FFFFFF">处理中<br/>的数量</font></td>
              <td width="50" align="center"><font color="#FFFFFF">待申请<br/>出库的量</font></td>
              <td width="50" align="center"><font color="#FFFFFF">上月销量</font></td>
<%if(areaId == -1 || areaId == 0){%>
              <td width="50" align="center"><font color="#FFFFFF">北京待验库</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京不可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京残次品</font></td>
              <td width="30" align="center"><font color="#FFFFFF">北京<br/>库存<br/>天数</font></td>
              <td width="60" align="center"><font color="#FFFFFF">库存标准<br/>(北京)</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京预计<br/>进货数</font></td>
<%}%>
<%if(areaId == -1 || areaId == 1){%>
              <td width="50" align="center"><font color="#FFFFFF">广东待验库</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东不可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东残次品</font></td>
              <td width="30" align="center"><font color="#FFFFFF">广东库存天数</font></td>
              <td width="60" align="center"><font color="#FFFFFF">库存标准<br/>(广东)</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东预计<br/>进货数</font></td>
<%}%>
			  <td><font color="#FFFFFF">采购计划数</font></td>
			  <td width="50" align="center"><font color="#FFFFFF">在途量</font></td>
              <td width="30" align="center"><font color="#FFFFFF">操作</font></td>
            </tr>
	</thead>
<logic:present name="productList" scope="request">
<%
Map lastWeekSellCount = (Map)request.getAttribute("lastWeekSellCount");
Map lastMonthSellCount = (Map)request.getAttribute("lastMonthSellCount");
//Map lastWeekOrderCount = (Map)request.getAttribute("lastWeekOrderCount");
Map dealingCount = (Map)request.getAttribute("dealingCount");
Map waitingStockOutCount = (Map)request.getAttribute("waitingStockOutCount");
Map lastDaySellGdCount = (Map)request.getAttribute("lastDaySellGdCount");
Map lastWeekSellGdCount = (Map)request.getAttribute("lastWeekSellGdCount");
Map lastMonthSellGdCount = (Map)request.getAttribute("lastMonthSellGdCount");
//Map lastWeekOrderGdCount = (Map)request.getAttribute("lastWeekOrderGdCount");
Map dealingGdCount = (Map)request.getAttribute("dealingGdCount");
Map packageMap = (Map)request.getAttribute("packageMap");
Map packageMap2 = (Map)request.getAttribute("packageMap2");

Map lastWeekSellCountPackage = (Map)request.getAttribute("lastWeekSellCountPackage");
Map lastMonthSellCountPackage = (Map)request.getAttribute("lastMonthSellCountPackage");
Map dealingCountPackage = (Map)request.getAttribute("dealingCountPackage");
Map waitingStockOutCountPackage = (Map)request.getAttribute("waitingStockOutCountPackage");
Map lastDaySellGdCountPackage = (Map)request.getAttribute("lastDaySellGdCountPackage");
Map lastWeekSellGdCountPackage = (Map)request.getAttribute("lastWeekSellGdCountPackage");
Map lastMonthSellGdCountPackage = (Map)request.getAttribute("lastMonthSellGdCountPackage");
Map dealingGdCountPackage = (Map)request.getAttribute("dealingGdCountPackage");

Map buyCountGDMap = (Map)request.getAttribute("buyCountGDMap");
Map minBuyPriceMap = (Map)request.getAttribute("minBuyPriceMap");
%> 
<%String productId = ""; %>
<logic:iterate name="productList" id="item" >
<%
if(item == null){
	continue;
}
Object lwsc = lastWeekSellCount.get(Integer.valueOf(((voProduct)item).getId()));
Object lmsc = lastMonthSellCount.get(Integer.valueOf(((voProduct)item).getId()));
//Object lwoc = lastWeekOrderCount.get(Integer.valueOf(((voProduct)item).getId()));
Object dc = dealingCount.get(Integer.valueOf(((voProduct)item).getId()));
Object wsoc = waitingStockOutCount.get(Integer.valueOf(((voProduct)item).getId()));
Object ldscGd = lastDaySellGdCount.get(Integer.valueOf(((voProduct)item).getId()));
Object lwscGd = lastWeekSellGdCount.get(Integer.valueOf(((voProduct)item).getId()));
Object lmscGd = lastMonthSellGdCount.get(Integer.valueOf(((voProduct)item).getId()));
//Object lwocGd = lastWeekOrderGdCount.get(Integer.valueOf(((voProduct)item).getId()));
Object dcGd = dealingGdCount.get(Integer.valueOf(((voProduct)item).getId()));

Object lwscp = lastWeekSellCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object lmscp = lastMonthSellCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object dcp = dealingCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object wsocp = waitingStockOutCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object ldscGdp = lastDaySellGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object lwscGdp = lastWeekSellGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object lmscGdp = lastMonthSellGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object dcGdp = dealingGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));

Integer buyCountGD = (Integer)buyCountGDMap.get(Integer.valueOf(((voProduct)item).getId()));
String minBuyPrice = (String)minBuyPriceMap.get(String.valueOf(((voProduct)item).getId()));
if(minBuyPrice == null){
	minBuyPrice = "0.0";
}
 
adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;

	voProduct pp = (voProduct)packageMap.get(Integer.valueOf(voItem.getId()));
	voProduct pp2 = (voProduct)packageMap2.get(Integer.valueOf(voItem.getId()));
	int ppCount = 0;
	int ppCount2 = 0;
	int ppCountGd2 = 0;
%>
		<tr bgcolor='#F8F8F8'>
		<td><bean:write name="item" property="id" /></td>
		<td align=left><bean:write name="item" property="code" /></td>
		<td align='center'><bean:write name="item" property="oriname" /></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='right'><bean:write name="item" property="price3" />元</td>
<%}%>
<%if(group.isFlag(256)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='right'><%=minBuyPrice %>元</td>
<%}%>
		<td align='center' ><%= voItem.getProxyName() %></td>
		<td align='center' ><%= voItem.getStatusName() %></td>
		<logic:present parameter="areaId" scope="request">
		<logic:equal parameter="areaId" scope="request" value="0" >
		<td align='right' ><%= NumberUtil.sum(lwsc, lwscp) %></td>
		<td align='right' ><%= NumberUtil.sum(dc, dcp) %></td>
		<td align='right' ><%= NumberUtil.sum(wsoc, wsocp) %></td>
		<td align='right' ><%= NumberUtil.sum(lmsc, lmscp) %></td>
		</logic:equal>
		<logic:equal parameter="areaId" scope="request" value="1" >
		<td align='right' ><%= NumberUtil.sum(ldscGd, ldscGdp) %></td>
		<td align='right' ><%= NumberUtil.sum(lwscGd, lwscGdp) %></td>
		<td align='right' ><%= NumberUtil.sum(dcGd, dcGdp) %></td>
		<td align='right' ><%= NumberUtil.sum(wsoc, wsocp) %></td>
		<td align='right' ><%= NumberUtil.sum(lmscGd, lmscGdp) %></td>
		</logic:equal>
		</logic:present>
		<logic:notPresent parameter="areaId" scope="request">
		<td align='right' ><%= NumberUtil.sum(lwsc, lwscp) %><%= (lwscGd==null && lwscGdp==null)?"":"(" + NumberUtil.sum(lwscGd, lwscGdp) + ")" %></td>
		<td align='right' ><%= NumberUtil.sum(dc, dcp) %><%= (dcGd==null && dcGdp==null)?"":"(" + NumberUtil.sum(dcGd, dcGdp) + ")" %></td>
		<td align='right' ><%= NumberUtil.sum(wsoc, wsocp) %><%= (wsoc==null && wsocp==null)?"":"(" + NumberUtil.sum(wsoc, wsocp) + ")" %></td>
		<td align='right' ><%= NumberUtil.sum(lmsc, lmscp) %><%= (lmscGd==null && lmscGdp==null)?"":"(" + NumberUtil.sum(lmscGd, lmscGdp) + ")" %></td>
		</logic:notPresent>
<%if(areaId == -1 || areaId == 0){ %>
<% if(voItem.getStockStandardBj() >= (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) * 2 && (voItem.getStockStandardBj() != 0)){ %>
		<td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %></font></td>
        <td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) %></font></td>
		<td align=right ><font color="red"><bean:write name="item" property="stockDayBj" /></font></td>
		<td align=right ><font color="red"><bean:write name="item" property="stockStandardBj" /></font></td>
		<td align=right >
			<%
				if(productId.indexOf(voItem.getId())==-1){
					productId += voItem.getId()+";";
				}
			 %>
			<%= voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) %>
			<font color="red"><%= (voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) > 0)?(voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK))):0 %></font>
		</td>
<%} else { %>
		<td align=right ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
        <td align=right ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=right ><bean:write name="item" property="stockDayBj" /></td>
		<td align=right ><bean:write name="item" property="stockStandardBj" /></td>
		<td align=right >
			<%= (voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) > 0)?(voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK))):0 %>
		</td>
<% } %>
<%} %>
<%if(areaId == -1 || areaId == 1){ %>
<% if(voItem.getStockStandardGd() >= (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) * 2 && (voItem.getStockStandardGd() != 0)){ %>
		<td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) %></font></td>
        <td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) %></font></td>
		<td align=right ><font color="red"><bean:write name="item" property="stockDayGd" /></font></td>
		<td align=right ><font color="red"><bean:write name="item" property="stockStandardGd" /></font></td>
		<td align=right >
			<%
				if(productId.indexOf(String.valueOf(voItem.getId()))==-1){
					productId += voItem.getId()+";";
				}
			 %>
			<font color="red"><%= (voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) > 0)?(voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) )):0 %></font>
		</td>
<%} else { %>
		<td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
        <td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=right ><bean:write name="item" property="stockDayGd" /></td>
		<td align=right ><bean:write name="item" property="stockStandardGd" /></td>
		<td align=right >
			<%= (voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) > 0)?(voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) )):0 %>
		</td>
<% } %>
<%} %>
		<td><input type="text" size="2" name="GDBuyPlanCount<bean:write name="item" property="id" />"/></td>
		<td align=right ><%=buyCountGD == null?0:buyCountGD.intValue() %></td>
		<td align="center"></td>
		</tr>
</logic:iterate> 
<input type="hidden" name="productIds" value="<%=(productId.endsWith(";"))?productId.substring(0,productId.length()-1):"0"%>"/>
</logic:present> 
          </table>
          <br />
</body>
</html>