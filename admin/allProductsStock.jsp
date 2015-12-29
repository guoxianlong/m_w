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
	
try{
%>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pts.js?permission=true"></script>
<body>
<%@include file="../header.jsp"%>
<form method=post action="allProductsStockSub.do">
<table cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" ><tr><td>
<table cellpadding="3" cellspacing="1" bgcolor="#F8F8F8"align="left" >

<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String name = (String)request.getParameter("name");
name = Encoder.decrypt(name);//对提交参数进行中文解码
if(name==null){//解码失败,表示已经为中文,则返回默认
	name =(String)request.getParameter("name");//名称
}
if(name == null)name="";
int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));
%>
	<tr>
		<td>产品编号：<input type=text name="code" size="20" value="<%=code %>" /></td>
		<td>产品名称：<input type=text name="name" size="20" value="<%=name %>" />（模糊）</td>
		<td>状态：<select name="status">
			<option value="0">全部</option>
		<option value="5" <%if("5".equals(request.getParameter("status"))){%>selected<%}%>>促销</option>
		<option value="10" <%if("10".equals(request.getParameter("status"))){%>selected<%}%>>热销</option>
		<option value="50" <%if("50".equals(request.getParameter("status"))){%>selected<%}%>>普通</option>
		<option value="100" <%if("100".equals(request.getParameter("status"))){%>selected<%}%>>下架</option>
		<option value="120" <%if("120".equals(request.getParameter("status"))){%>selected<%}%>>隐藏</option>
		<option value="130" <%if("130".equals(request.getParameter("status"))){%>selected<%}%>>缺货</option>
		</select></td>
	</tr>
	<tr>
		<td>一级分类：<select  name="parentId1" class="bd" style="width:140" onChange="sredirect(this.options.selectedIndex - 1);">
			<option value="0">全部</option>
			 <%
				HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					voCatalog catalog = (voCatalog)iter.next();
					if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
						continue;
					}
			 %>
			 <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
			<%} %>
				</select></td>
		<td>二级分类：<select name="parentId2" class="bd" style="width:140" onChange="tredirect(this.options.selectedIndex-1);">
			<option value="0">全部</option>
		</select></td>
		<td>三级分类：<select name="parentId3" class="bd" style="width:140">
			<option value="0">全部</option>
		</select></td>
	<%if(group.isFlag(87)){ %>
		<td>代理商：<select name="proxy" style="width:130" >
		    <option value="0">所有</option>
		<%
			rs = st.executeQuery("select id, name from supplier_standard_info where status=1 order by id");
		    while(rs.next()){
		%>
		    <option value="<%=rs.getInt(1)%>" <%if(rs.getInt(1) == proxy){%>selected<%}%>><%=rs.getString(2)%></option>
		<%
			}
		    rs.close();
		%>
		</select>
		</td>
	<%} %>
	</tr>
<script>
var spt = document.forms[0].parentId2
var parentId1 = document.forms[0].parentId1
function sredirect(x){
 if(x<=0){
		  document.forms[0].parentId2.options[0].selected=true;
		  document.forms[0].parentId3.length=1;
		  document.forms[0].parentId3.options[0].selected=true;
	  }
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null;
  if(x < 0){
	return;
  }
  spt.options[0]=new Option("全部", "0");
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i + 1]=new Option(spts[x][i].text, spts[x][i].value)
  }
  spt.options[0].selected=true
  tredirect(0);
}
var tpt = document.forms[0].parentId3
function tredirect(y){
  x = document.forms[0].parentId1.options.selectedIndex-1;
  for (m = tpt.options.length - 1; m >= 0; m --)
      tpt.options[m] = null
  tpt.options[0]=new Option("全部", "0");
  for (i =0; i < tpts[x][y].length; i ++){
      tpt.options[i+1]=new Option(tpts[x][y][i].text, tpts[x][y][i].value)
  }
  tpt.options[0].selected=true;
}
selectOption(document.forms[0].parentId1, '<%= parentId1 %>');
sredirect(document.forms[0].parentId1.selectedIndex - 1);
selectOption(document.forms[0].parentId2, '<%= parentId2 %>');
tredirect(document.forms[0].parentId2.selectedIndex - 1);
selectOption(document.forms[0].parentId3, '<%= parentId3 %>');
</script>
	<tr></tr>
	<tr>
		<td><input type=submit value="查询">(默认查询为全部)</td>
		<td><input type="button" value="查询全部" onclick="location.href='allProductsStockSub.do'" /></td>
	</tr>
	
</table>
</td></tr></table>
</form>
<!-- ============================= 数据显示 ============================= -->
<c:if test="${paging!=null}">
<div align="right"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></div>
</c:if>
<logic:present name="totalList" scope="request">
<logic:iterate name="totalList" id="item" type="adultadmin.action.vo.voProduct" > 
<%
if((item.getStockAll() + item.getLockCountAll())>0){ 
	totalCount++;} %>
</logic:iterate> 
</logic:present> 
 
一共有: <%=totalCount %> 种产品
<%String ul= "allProductsStockSub.do?toExcel=to&"+ request.getAttribute("date"); %>
<input type="button" onclick="location.href='<%=ul %>'"   value="导出列表"/> 
<%float total=0;//库存产品的总金额%>
<logic:present name="totalList" scope="request">
<logic:iterate name="totalList" id="item" type="adultadmin.action.vo.voProduct" > 
<%total=total+(item.getStockAll() + item.getLockCountAll())*item.getPrice5();%>
</logic:iterate> 
</logic:present> 
<%if(group.isFlag(86)){ %>
总金额:<%=total %>
<%} %>


<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
  	<tr bgcolor="#4688D6">              
		<td align="center" rowspan="3"><font color="#FFFFFF">编号</font></td>
		<td align="center" rowspan="3"><font color="#FFFFFF">名称</font></td>
		<td align="center" colspan="25"><font color="#FFFFFF">库存数量</font></td>
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
        <td align="center" colspan="3"><font color="#FFFFFF">残次品库</font></td>
        <td align="center" colspan="3"><font color="#FFFFFF">样品库</font></td>
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
        <!--样品库  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <td align="center"><font color="#FFFFFF">增城</font></td>
        <!--售后库  -->
        <td align="center"><font color="#FFFFFF">芳村</font></td>
        <!--标准库存  -->
        <td align="center"><font color="#FFFFFF">北京</font></td>
        <td align="center"><font color="#FFFFFF">芳村</font></td>        
  	</tr>


<logic:present name="productList" scope="request">
<logic:iterate name="productList" id="item" type="adultadmin.action.vo.voProduct" > 
<%if((item.getStockAll() + item.getLockCountAll())>0){ 
%>
	<tr bgcolor='#F8F8F8'>	
	    <!--编号  -->
		<td align="left"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<!--名称  -->
		<td align='center' width="150"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a></td>
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
		<!--样品库  -->
		<td align='center'><%= item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
		<td align='center'><%= item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) %></td>		
		<td align='center'><%= item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) %></td>		
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
		<td align="center"><a href="productStock/stockCardList.jsp?productCode=<bean:write name="item" property="code" />" target="_blank">查</a></td>
	</tr>
<%} %>
</logic:iterate> 
</logic:present> 

</table>
<br/>
<%
	st.close();
} catch(Exception e){
	e.printStackTrace();
} finally{
conn.close();
}
%>  
</body>
<c:if test="${paging!=null}">
<div align="right"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></div>
</c:if>
</html>
