<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,cache.*" %>
<%
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();

	response.setContentType("application/vnd.ms-excel;charset=utf-8");
	String fileName = "历史采购入库-"+DateUtil.getNow().replace(" ","-").replace(":","-");
	response.setHeader("Content-disposition","attachment; filename=\"" + new String(fileName.getBytes("GBK"), "iso8859-1") + ".xls\"");
%>
<body>
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
		<td align=left width="60"><bean:write name="item" property="code" /></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><bean:write name="item" property="name" filter="true" /></td>
		<td align='center'><bean:write name="item" property="oriname" filter="true" /></td>
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
			<bean:write name="item" property="createDatetime" format="yyyy-MM-dd kk:mm" />
			<%}else{ %>
			<bean:write name="item" property="createDatetime" format="yyyy-MM-dd kk:mm" />
			<%} %>
		</td>
		</tr>
</logic:iterate>
</logic:present>
          </table>
          <br>
</body>
