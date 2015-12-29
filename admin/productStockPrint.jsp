<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="ormap.*" %>
<%
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

	String catalogIds = StringUtil.convertNull(request.getParameter("catalogIds"));
	String catalogId2s = StringUtil.convertNull(request.getParameter("catalogId2s"));
	String productLineName = StringUtil.convertNull(request.getParameter("productLineName"));
	int areaId = StringUtil.toInt(request.getParameter("areaId"));
	//String GDBuyPlanCount = StringUtil.convertNull(request.getParameter("GDBuyPlanCount<bean:write name="item" property="id" />"));
%>
 <%
response.setContentType("application/vnd.ms-excel");
String filename = new String("警戒产品".getBytes("GB2312"),"ISO8859-1");
    if(productLineName.equals("beijingchengrenbaojianpin")){
	  filename = new String("北京成人保健品警戒产品".getBytes("GB2312"),"ISO8859-1");
    }
    if(productLineName.equals("baojianpin")){
    	filename = new String("保健品警戒产品".getBytes("GB2312"),"ISO8859-1");
    }
	if(productLineName.equals("chengrenriyongpin")){
		filename = new String("成人日用品警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("shoujishuma")){
		filename = new String("手机数码警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("hanghuoshouji")){
		filename = new String("行货手机警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("shoujishumapeijian")){
		filename = new String("手机数码配件警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("diannao")){
		filename = new String("电脑警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("fuzhuang")){
		filename = new String("服装警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("xiezi")){
		filename = new String("鞋子警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("xiepeijian")){
		filename = new String("鞋配件警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("hufupin")){
		filename = new String("护肤品警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("lipin")){
		filename = new String("礼品警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("xinqite")){
		filename = new String("新奇特警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("xiaojiadian")){
		filename = new String("小家电警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("shipin")){
		filename = new String("饰品警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("bao")){
		filename = new String("包警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("shoubiao")){
		filename = new String("手表警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("riyongbaihuo")){
		filename = new String("日用百货警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
	if(productLineName.equals("food")){
		filename = new String("食品警戒产品".getBytes("GB2312"),"ISO8859-1");
	}
response.setHeader("Content-disposition","attachment; filename=\"" + filename + ".xls\"");
%>
<html>
<title>买卖宝后台</title>
<body>

<form method="post" action="" name="allForm" target="_blank">
	<input type="hidden" id="updateType" name="updateType" value="" />
	<input type="hidden" id="updateCatalogIds" name="catalogIds" value="<%= catalogIds %>" />
	<input type="hidden" id="updateCatalogId2s" name="catalogId2s" value="<%= catalogId2s %>" />
	<input type="hidden" id="securityLine" name="securityLine" value="<%=request.getAttribute("securityLine") %>"/>
          <br />
          <table width="95%" cellpadding="3" cellspacing="1" border="1" id="productStockTable" class="sortable">
          <thead class="sorthead">
              <tr>
              <td width="40" align="center"> 编号 </td>
              <td align="center"> 原名称 </td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td width="40" align="center"> 批发价 </td>
<%}%>
<%if(group.isFlag(256)){%>
			  <td width="40" align="center"> 最低进货税后价 </td>
<%}%>
			  <td width="40" align="center"> 买卖宝价 </td>
			  <td width="40" align="center"> 市场价 </td>
              <td align="center"> 代理商 </td>
              <td width="50" align="center"> 产品状态 </td>
              <td width="50" align="center"> 日销量 </td>
              <td width="50" align="center"> 上周销量 </td>
              <td width="50" align="center"> 处理中<br/>的数量 </td>
              <%-- <td width="50" align="center"> 待申请<br/>出库的量 </td> --%>
              <td width="50" align="center"> 上月销量 </td>
<%if(areaId == -1 || areaId == 0){%>
              <td width="50" align="center"> 北京待验库 </td>
              <td width="50" align="center"> 北京可发货 </td>
              <td width="50" align="center"> 北京不可发货 </td>
              <td width="50" align="center"> 北京残次品 </td>
              <td width="30" align="center"> 北京<br/>库存<br/>天数 </td>
              <td width="60" align="center"> 库存标准<br/>(北京) </td>
              <td width="50" align="center"> 北京预计<br/>进货数 </td>
<%}%>
<%if(areaId == -1 || areaId == 1){%>
              <td width="50" align="center"> 广东待验库 </td>
              <td width="50" align="center"> 广东可发货 </td>
              <td width="50" align="center"> 广东不可发货 </td>
              <td width="50" align="center"> 广东残次品 </td>
              <td width="30" align="center"> 广东库存天数 </td>
              <td width="60" align="center"> 库存标准<br/>(广东) </td>
              <td width="50" align="center"> 广东预计<br/>进货数 </td>
<%}%>
			  <td> 采购计划数 </td>
			  <td width="50" align="center"> 在途量 </td>
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
<form action="addk" method="post">
<%String productId = ""; %>
<logic:iterate name="productList" id="item" >
<input type="hidden" name="productId" value="<bean:write name="item" property="id" />" />
<%
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
		<td align=left><bean:write name="item" property="code" /></td>
		<td align='center'><bean:write name="item" property="oriname" /></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='right'><bean:write name="item" property="price3" />元</td>
<%}%>
<%if(group.isFlag(256)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='right'><%=minBuyPrice %>元</td>
<%}%>
		<td align='right'><bean:write name="item" property="price" />元</td>
		<td align='right'><bean:write name="item" property="price2" />元</td>
		<td align='center' ><%=StringUtil.convertNull(voItem.getProxyName())%></td>
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
		<%-- <td align='right' ><%= NumberUtil.sum(wsoc, wsocp) %></td> --%>
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
		<td align=right > <%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %> </td>
        <td align=right> <%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) %> </td>
        <td align=right> <%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %> </td>
        <td align=right > <%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) %> </td>
		<td align=right > <bean:write name="item" property="stockDayBj" /> </td>
		<td align=right > <bean:write name="item" property="stockStandardBj" /> </td>
		<td align=right >
			<%
				if(productId.indexOf(voItem.getId())==-1){
					productId += voItem.getId()+";";
				}
			 %>
			<input type="hidden" name="bjStockNeed<bean:write name="item" property="id" />" value="<%= voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) %>" />
			 <%= (voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) > 0)?(voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK))):0 %> 
		</td>
<%} else { %>
		<td align=right ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
        <td align=right ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=right ><bean:write name="item" property="stockDayBj" /></td>
		<td align=right ><bean:write name="item" property="stockStandardBj" /></td>
		<td align=right >
			<input type="hidden" name="bjStockNeed" value="<%= voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) %>" />
			<%= (voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) > 0)?(voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK))):0 %>
		</td>
<% } %>
<%} %>
<%if(areaId == -1 || areaId == 1){ %>
<% if(voItem.getStockStandardGd() >= (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) * 2 && (voItem.getStockStandardGd() != 0)){ %>
		<td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) %> </td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)  + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) %> </td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE)  + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE)%> </td>
        <td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) %> </td>
		<td align=right ><bean:write name="item" property="stockDayGd" /> </td>
		<td align=right ><bean:write name="item" property="stockStandardGd" /> </td>
		<td align=right >
			<%
				if(productId.indexOf(String.valueOf(voItem.getId()))==-1){
					productId += voItem.getId()+";";
				}
			 %>
			<input type="hidden" name="gdStockNeed<bean:write name="item" property="id" />" value="<%= voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) %>" />
			 <%= (voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) > 0)?(voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) )):0 %> 
		</td>
<%} else { %>
		<td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
        <td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=right ><bean:write name="item" property="stockDayGd" /></td>
		<td align=right ><bean:write name="item" property="stockStandardGd" /></td>
		<td align=right >
			<input type="hidden" name="gdStockNeed" value="<%= voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) %>" />
			<%= (voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) > 0)?(voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) )):0 %>
		</td>
<% } %>
<%} %>
		<td><%=StringUtil.convertNull(request.getParameter("GDBuyPlanCount"+voItem.getId()))%></td>
		<td align=right ><%=buyCountGD == null?0:buyCountGD.intValue() %></td>
		</tr>
</logic:iterate> 
<input type="hidden" name="productIds" value="<%=(productId.endsWith(";"))?productId.substring(0,productId.length()-1):"0"%>"/>
</form>
</logic:present> 
          </table>
</form>
<form action="./updateProductStockStardard.do" method="post" name="updateProductStockStandard" target="_blank">
	<input type="hidden" id="productIdUpdate" name="productId" value="" />
	<input type="hidden" id="stockDayGdUpdate" name="stockDayGd" value="" />
	<input type="hidden" id="stockDayBjUpdate" name="stockDayBj" value="" />
</form>
          <br />
</body>
</html>