<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ page isELIgnored="false" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,java.util.*,java.text.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.util.*" %>
<%
String fileName = "库存查询导出_"+DateUtil.getNowDateStr()+".xls";
response.setContentType("application/msexcel"); 
response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes("GBK"), "iso8859-1"));
%>  
<html xmlns:o="urn:schemas-microsoft-com:office:office" 
xmlns:x="urn:schemas-microsoft-com:office:excel" 
xmlns="http://www.w3.org/TR/REC-html40"> 

<head> 
<meta http-equiv=Content-Type content="text/html; charset=utf-8"> 
<meta name=ProgId content=Excel.Sheet> 
<meta name=Generator content="Microsoft Excel 11"> 
</head>

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
	int proxy = StringUtil.toInt(request.getParameter("proxy"));
%>
<html>
<title>导出数据</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pts.js"></script>
<body>
<%@include file="../header.jsp"%>
<!-- ============================= 数据显示 ============================= -->
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
  	<tr bgcolor="#4688D6">              
		<td align="center" rowspan="3"><font color="#FFFFFF">编号</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">名称</font></td>
		<td align="center" colspan="26"><font color="#FFFFFF">库存数量</font></td>
		<td align="center" rowspan="2" colspan="2"><font color="#FFFFFF">库存标准</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">状态</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">一级分类</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">二级分类</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">三级分类</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">等级</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">库存记录</font></td>
  	</tr>
  	<tr bgcolor="#4688D6">
       <td align="center" rowspan="2"><font color="#FFFFFF">库存总数</font></td>
        <td align="center" colspan="3"><font color="#FFFFFF">待验库</font></td>
        <td align="center" colspan="4"><font color="#FFFFFF">合格库</font></td>       
        <td align="center" colspan="4"><font color="#FFFFFF">退货库</font></td>
        <td align="center" colspan="3"><font color="#FFFFFF">返厂库</font></td>
        <td align="center" colspan="3"><font color="#FFFFFF">维修库</font></td>
        <td align="center" colspan="4"><font color="#FFFFFF">残次品库</font></td>
        <td align="center" colspan="4"><font color="#FFFFFF">样品库</font></td>
        <td align="center"><font color="#FFFFFF">售后库</font></td>
  	</tr>
  	<tr bgcolor="#4688D6">
        <!--待验库  -->
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <td align="center"><font color="#FFFFFF">无锡</font></td>
        <!--合格库  -->
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">广速</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <td align="center"><font color="#FFFFFF">无锡</font></td>
        <!--退货库  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <td align="center"><font color="#FFFFFF">无锡</font></td>
        <!--返厂库  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <!--维修库  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <!--残次品库  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <td align="center"><font color="#FFFFFF">无锡</font></td>
        <!--样品库  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <td align="center"><font color="#FFFFFF">无锡</font></td>
        <!--售后库  -->
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <!--标准库存  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>      
  	</tr>

<c:if test="${count!=0}">
<logic:present name="totalList" scope="request">
<logic:iterate name="totalList" id="item" type="adultadmin.action.vo.voProduct" > 
<%if((item.getStockAll() + item.getLockCountAll())>0){ %>
	<tr bgcolor='#F8F8F8'>	
		<!--编号  -->
		<td align="left">><bean:write name="item" property="code" /></td>
		<!--名称  -->
		<td align='center' width="150"><bean:write name="item" property="oriname" /></td>
		<!--库存总数  -->
		<td align='center' width="25"><%= item.getStockAll() + item.getLockCountAll() %></td>
		<!--待验库  -->
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_CHECK) %></td>
		<!--合格库  -->
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<!--退货库  -->
		<td align='center'><%= item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) %></td>	
		<td align='center'><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)  %></td>	
		<td align='center'><%= item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_RETURN)  %></td>	
		<!--返厂库  -->		
		<td align='center'><%= item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_BACK) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) %></td>
		<!--维修库  -->
		<td align='center' width="25"><%= item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) %></td>
		<td align='center' width="25"><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_REPAIR) %></td>		
		<td align='center' width="25"><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) %></td>		
        <!--残次品库  -->
		<td align='center' width="25"><%= item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE)  %></td>
		<td align='center' width="25"><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align='center' width="25"><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align='center' width="25"><%= item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<!--样品库  -->
		<td align='center'><%= item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) %></td>		
		<td align='center'><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) %></td>		
		<td align='center'><%= item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_SAMPLE) %></td>		
		<!-- 售后库 -->
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_AFTER_SALE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_AFTER_SALE) %></td>	
		<!--标准库存  -->
		<td align='center'><bean:write name="item" property="stockStandardBj" /></td>
		<td align='center'><bean:write name="item" property="stockStandardGd" /></td>
		<!--状态  -->
		<td align="right"><bean:write name="item" property="statusName" /></td>
		<!--分类  -->
		<td align='center' width="50"><%=(item.getParentId1()==0? "无" : item.getParent1().getName())%></td>
		<td align='center' width="50"><%=(item.getParentId2()==0? "无" : item.getParent2().getName())%></td>
		<td align='center' width="50"><%=(item.getParentId3()==0? "无" : item.getParent3().getName())%></td>
		<!--等级  -->
		<td align='center'><bean:write name="item" property="rank" /></td>
		<!--操作  -->
		<td align="center">查</td>
	</tr>
<%} %>
</logic:iterate> 
</logic:present> 
</c:if>
          </table>
</body>
</html>
