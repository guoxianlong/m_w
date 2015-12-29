<%@page import="cache.ProductLinePermissionCache"%>
<%@page import="java.net.URLEncoder"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ page isELIgnored="false" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,adultadmin.framework.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.util.*,java.net.*,adultadmin.util.db.DbUtil" %>
<%@ page import="java.util.*" %>
<%@page import="cache.CatalogCache"%>
<%@page import="adultadmin.action.vo.voCatalog"%>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
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
	
	Connection conn = DbUtil.getConnection("adult_slave");
	Statement st = conn.createStatement();
	 int totalCount=0;//总数
	ResultSet rs = null;
	int proxy = StringUtil.toInt(request.getParameter("proxy"));
	PagingBean paging = (PagingBean) request.getAttribute("paging");
 
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
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pts.js?permission=true"></script>
<body>
productLinePermissionId:<%=catalogIds1+"  "+catalogIds2 %>
</html>
