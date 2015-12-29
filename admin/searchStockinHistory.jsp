<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,cache.*" %>
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
%>
<html>
<title>小店后台管理 - 搜索产品购买入库记录</title>
<script   language="JavaScript">   
  function   getTblText(objTbl)   
  {   
  if(!objTbl)   return   "";   
  if(objTbl.tagName   !=   "TABLE")   return   "";   
  var   returnText   =   "";   
  for(var   r=0;   r<objTbl.rows.length;   r++)   
  {   
  for(var   c=0;   c<objTbl.rows[r].cells.length;   c++)   
  {   
  returnText   +=   objTbl.rows[r].cells[c].innerText   +   "\t";   
  }   
  returnText   +=   "\n";   
  }   
  return   returnText;   
  }   
</script>
<script>
function exportList(){
	document.searchStockinHistoryForm.forward.value = 'export';
	document.searchStockinHistoryForm.submit();
}
function search(){
	document.searchStockinHistoryForm.forward.value = '';
	return true;
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<body>
<%@include file="../header.jsp"%>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="searchStockinHistoryForm" method=post action="searchStockinHistory.do" onSubmit="return search();">
<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String oriname = (String)request.getParameter("oriname");
if(oriname == null)oriname="";
String name = (String)request.getParameter("name");
if(name == null)name="";
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate="2011-01-01";
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.getNow().substring(0, 10);
String price = (String)request.getParameter("price");
if(price == null)price = "";
String stockCount = (String)request.getParameter("stockCount");
if(stockCount == null)stockCount = "";
String operationName = StringUtil.convertNull(request.getParameter("operationName"));
int productProxyId = StringUtil.toInt(request.getParameter("productProxyId"));

HashMap catalogMap = (HashMap)request.getAttribute("catalogMap");
HashMap map = (HashMap)request.getAttribute("map");
HashMap mapBj = (HashMap)request.getAttribute("mapBj");
HashMap mapGd = (HashMap)request.getAttribute("mapGd");

String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
if(!catalogIds2.equals("")){
	String[] splits = catalogIds2.split(",");
	for(int i=0;i<splits.length;i++){
		voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
		if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
			catalogIds1 = catalog.getId() + "," + catalogIds1;
		}
	}
	if(catalogIds1.endsWith(",")){
		catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
	}
}
%>
入库操作名称：<input type=text name="operationName" size="20" value="<%=operationName%>"><br>
产品编号：<input type=text name="code" size="20" value="<%=code%>"><br>
原名称：<input type=text name="oriname" size="20" value="<%=oriname%>">（模糊）<br>
小店名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br>
小店价格：<input type=text name="price" size="20" value="<%=price%>"><br>
入库量：<input type=text name="stockCount" size="20" value="<%=stockCount%>"><br>
时间：<input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');"><br>
地点：<select name="area">
	<option value="" >全部</option>
	<option value="0" <%if("0".equals(request.getParameter("area"))){%>selected<%}%>>北京</option><option value="1" <%if("1".equals(request.getParameter("area"))){%>selected<%}%>>广东</option></select><br/>
供货商：<select name="productProxyId">
	<option value="-1"></option>
	<logic:present name="proxyList" scope="request"><logic:iterate name="proxyList" id="proxyItem" >
	<option value="<bean:write name="proxyItem" property="id" />"><bean:write name="proxyItem" property="name" /></option>
	</logic:iterate></logic:present> 
</select><br/>
<script>
selectOption(searchStockinHistoryForm.productProxyId, '<%= productProxyId %>');
</script>
<input type="hidden" name="forward" id="forward" value=""/>
<input type=submit value="查询入库记录">
</form>
</td></tr>
</table>
</td></tr></table>
          <br /><table align="center" width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
          	<tr bgcolor="#4688D6">
              <td align="center" width="10%"><font color="#FFFFFF">一级分类</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">比例</font></td>
              <%if(group.isFlag(31)){%>
              <td align="center" width="10%"><font color="#FFFFFF">金额</font></td>
              <%} %>
              <td align="center" width="10%"><font color="#FFFFFF">北京比例</font></td>
              <%if(group.isFlag(31)){%>
              <td align="center" width="10%"><font color="#FFFFFF">北京金额</font></td>
              <%} %>
              <td align="center" width="10%"><font color="#FFFFFF">广东比例</font></td>
              <%if(group.isFlag(31)){%>
              <td align="center" width="10%"><font color="#FFFFFF">广东金额</font></td>
              <%} %>
            </tr>
<%
if(map != null){
	Iterator iter = map.keySet().iterator();
	Float total = (Float)map.get(Integer.valueOf(0));
	while(iter.hasNext() && total.floatValue() > 0){
		Integer key = (Integer)iter.next();
		Float value = (Float)map.get(key);
		Float valueBj = (Float)mapBj.get(key);
		Float valueGd = (Float)mapGd.get(key);
		voCatalog p1 = (voCatalog)catalogMap.get(key);
		if(key != null && value != null && p1 != null){
			if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(key.intValue()))){
				continue;
			}
%>
			<tr bgcolor='#F8F8F8'>
				<td align='center'><%= p1.getName() %></td>
				<td align='center'><%= StringUtil.formatFloat((value.floatValue() / total.floatValue()) * 100) %>%</td>
				<%if(group.isFlag(31)){%>
				<td align='center'><%= StringUtil.formatFloat(value.floatValue()) %></td>
				<%} %>
				<td align='center'><%= StringUtil.formatFloat((valueBj.floatValue() / total.floatValue()) * 100) %>%</td>
				<%if(group.isFlag(31)){%>
				<td align='center'><%= StringUtil.formatFloat(valueBj.floatValue()) %></td>
				<%} %>
				<td align='center'><%= StringUtil.formatFloat((valueGd.floatValue() / total.floatValue()) * 100) %>%</td>
				<%if(group.isFlag(31)){%>
				<td align='center'><%= StringUtil.formatFloat(valueGd.floatValue()) %></td>
				<%} %>
			</tr>
<%
		}
	}
}
%>
          </table><br /><br />
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
          <br><form method=post action="" name="productForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
              <tr bgcolor="#4688D6">
              <td align="center" width="5%"><font color="#FFFFFF">编号</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">一级分类</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">二级分类</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
              <td align="center" width="5%"><font color="#FFFFFF">价格</font></td>
<%--if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td align="center" width="5%"><font color="#FFFFFF">批发价</font></td>
<%}--%>
<%if(group.isFlag(31)){%>
			  <td align="center" width="8%"><font color="#FFFFFF">采购价格</font></td>
<%}%>
              <td align="center" width="10%"><font color="#FFFFFF">北京入库量</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">广东入库量</font></td>
			  <td align="center"><font color="#FFFFFF">时间</font></td>    
            </tr>
<logic:present name="productList" scope="request">
<logic:iterate name="productList" id="item" > 
<%
	adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;
	voCatalog p1 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId1()));
	voCatalog p2 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId2()));
%>
		<tr bgcolor='#F8F8F8'>		
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" filter="true" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" filter="true" /></a></td>
		<td align='center'><%=voItem.getPrice() %></td>
<%--if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='center'><bean:write name="item" property="price3" format="#" /></td>
<%}--%>
<%if(group.isFlag(31)){%>
		<td align='center'><%=voItem.getPrice5() %></td>
<%}%>
		<td align='center'><bean:write name="item" property="bjBuyCount" /></td>
		<td align='center'><bean:write name="item" property="gdBuyCount" /></td>
		<td align='center'>
			<%
				String id =  voItem.getRemark().split(",")[0];
				String stockinCode =  voItem.getRemark().split(",")[1];
			 %>
			 <%if(stockinCode.startsWith("RK")){ %>
			<a href="./stock2/buyStockinListOld.jsp?id=<%= id %>"><bean:write name="item" property="createDatetime" format="yyyy-MM-dd kk:mm" /><%--=startDate%>至<%=endDate--%></a>
			<%}else{ %>
			<a href="./stock2/buyStockinList.jsp?id=<%= id %>"><bean:write name="item" property="createDatetime" format="yyyy-MM-dd kk:mm" /><%--=startDate%>至<%=endDate--%></a>
			<%} %>
		</td>
		</tr>
</logic:iterate>
</logic:present>
          </table>
          <br>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
</body>
</html>