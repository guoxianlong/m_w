<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.bean.*,java.lang.*" %>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="cache.*"%>
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
	int productBrandId = 0;	//品牌id
    if(request.getAttribute("productBrandId") != null){
    productBrandId = StringUtil.StringToId(request.getAttribute("productBrandId").toString());	
     }
 %>
<html>
<title>小店后台管理 - 无销量商品查询</title>
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
	clipboardData.setData('text',getTblText(listTable));
	alert("列表内容已复制到剪贴板，粘贴到excel文件中即可。");
}
window.onload = function() {
	selectOption(document.getElementById("brandId"), "<%=productBrandId %>");
}
function autoWrite(textName,selectName){
	textName.value = selectName.options[selectName.selectedIndex].text;
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/brandNames2.js"></script>
<body>
<%@include file="../header.jsp"%>
<p>
<pre>
<b><font size=5> 无销量商品查询</font></b>
  规则说明:
1、某一时间段内成交量为0，但有库存的所有产品的查询统计
2、时间:用于筛选订单的生成日期
3、发货量，是指当前查看时间段内，发货出库的订单内商品的个数
4、商品结存量，此处是指合格库可发货量和待验库库存量之和。
</pre>
</p>
<table  cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table  cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="searchForm" method=post action="searchNodealProduct.do">
<%
String brandtext = StringUtil.convertNull(request.getParameter("brandtext"));	//品牌名称
String productBrandIds = StringUtil.convertNull((String)request.getAttribute("productBrandIds"));
List productBrandList = new ArrayList();
	if(request.getAttribute("productBrandList") != null){
		productBrandList = (List) request.getAttribute("productBrandList");	//品牌信息
	}
	
String code = (String)request.getParameter("code");
if(code == null)code="";
String oriname = (String)request.getParameter("oriname");
if(oriname == null)oriname="";
String name = (String)request.getParameter("name");
if(name == null)name="";
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate=DateUtil.getTimeBeforeMinutes(-60*24*7).substring(0, 10);
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.getNow().substring(0, 10);
String parentId1 = StringUtil.convertNull(request.getParameter("parentId1"));
HashMap catalogMap = (HashMap)request.getAttribute("catalogMap");
String[] parentId1s=(String[])request.getAttribute("parentId1s");
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
String ss="";
if(parentId1s!=null){
	StringBuffer sb=new StringBuffer();
	for(int s=0;s<parentId1s.length;s++){
	sb.append(parentId1s[s]+",");
	}
	ss=sb.toString();
}

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
int stockcount = 0;
 if(request.getAttribute("stockcount") != null){
  stockcount = StringUtil.StringToId(request.getAttribute("stockcount").toString());	
   }
HashMap map = (HashMap)request.getAttribute("map");
%>
产品编号：<input type=text name="code" size="10" value="<%=code%>">
原名称：<input type=text name="oriname" size="20" value="<%=oriname%>">（模糊）
小店名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br/><br/>
产品状态：<select name="status">
	<option value="0">全部</option>
<option value="2" <%if("2".equals(request.getParameter("status"))){%>selected<%}%>>在架上</option><option value="1" <%if("1".equals(request.getParameter("status"))){%>selected<%}%>>已下架</option></select>
时间：<input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');"><br/><br/>
产品分类：<select name="parentId1"  multiple="15" size="15" style="width:180">
	<option value="0">全部</option>
			<%
				HashMap map1 = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map1.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					voCatalog catalog = (voCatalog)iter.next();
					if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
						continue;
					}
			 %>
			 <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
			<%} %>
</select>(按住Ctrl多选)<br/>
<%if(parentId1!=null&&!parentId1.equals("")){ %>
<script> //保存上次多选状态
	selectOptionsp(searchForm.parentId1, '<%=ss %>');
	function selectOptionsp(select,value){
	var ps=value.split(","); 
		for(var i = 0; i < select.length; i++){
			for(var t = 0; t < ps.length; t++){
				if(select.options[i].value == ps[t]){
					select.options[i].selected = true;
				}					
			}
		}
	return 0;
}
</script>
<%} %>
<br/>
<br/>
产品线:
 	<%List productLineList = (List)request.getAttribute("productLineList") ;
 	%>
 	 <select name="productLine" id="productLine">
 		<option value="">请选择</option>	
 		<%if(productLineList!=null && productLineList.size()>0){
 			for(int i=0;i<productLineList.size();i++){
				voProductLine voProudct = (voProductLine)productLineList.get(i);
 				%>
 		<option value="<%=voProudct.getId()%>" <%=productLine==voProudct.getId()?"selected='selected'":"" %>><%=voProudct.getName()%></option>		
 				<%
 			}
 		 }%>
 	 </select>&nbsp;&nbsp;
 	 品牌：
	<div id="auto2" style="position: absolute; left: 100px; top: 550px;"></div>
	<input type="text" name="brandtext" id="word2" style="width: 100px; font-size: 10pt; height: 20px;" value=<%=brandtext %>/>
	<input type="hidden" name="condition2" id="condition2" value=" and id in (<%=productBrandIds %>)">
	<span style="width:18px;border:0px solid red; margin-left:-8px; margin-bottom:-6px;">
	<select name="productBrandId" id="brandId" style="margin-left:-100px; width:118px;"
	onChange="autoWrite(document.searchForm.brandtext, document.searchForm.productBrandId);">
		<option value="0">--请选择--</option>
		<%
		if (productBrandList != null && productBrandList.size() > 0) {
			for(int i = 0; i < productBrandList.size(); i++) {
				voSelect bean = (voSelect) productBrandList.get(i);
				if(bean != null){
	%>
	 			<option value="<%= bean.getId() %>" ><%= bean.getName() %></option>
	<%
				}
			} 
		}
	%>
	</select></span>&nbsp;&nbsp;商品结存量：大于
	<input type="text" name="stockcount" id="stockcount" style="width: 100px; font-size: 10pt; height: 20px;" value="<%=stockcount %>" />
 	 <br/>
<input type=submit value="查询产品成交量">
</form>
</td></tr>
</table>
</td></tr></table>
          <br />
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
          <br><form method=post action="" name="productForm">
          <table width="99%" cellpadding="3" cellspacing="1" id="listTable">
              <tr bgcolor="#4688D6">
              <td align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">一级分类</font></td>
              <td align="center"><font color="#FFFFFF">二级分类</font></td>
              <td align="center"><font color="#FFFFFF">三级分类</font></td>
               <td align="center"><font color="#FFFFFF">品牌</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
               <td align="center"><font color="#FFFFFF">供应商</font></td>
              <td align="center"><font color="#FFFFFF">价格</font></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td align="center"><font color="#FFFFFF">批发价</font></td>
<%}%>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td align="center"><font color="#FFFFFF">库存价格</font></td>
<%}%>
             <td align="center"><font color="#FFFFFF">合格库可发货量</font></td>
              <td align="center"><font color="#FFFFFF">待验库库存量</font></td>
			  <td align="center"><font color="#FFFFFF">时间段</font></td>    
            </tr>
<%int row=0;//行号 %>
<logic:present name="productList" scope="request">
<logic:iterate name="productList" id="item" > 
<%
	adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;
	voCatalog p1 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId1()));
	voCatalog p2 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId2()));
	voCatalog p3 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId3()));
%>
		<tr <%if(row%2==0){%> bgcolor="#EEE9D9"<%}%>>		
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><%= (p3!=null)?StringUtil.toWml(p3.getName()):"" %></td>
		<td align='center'><bean:write name="item" property="intro" /></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" filter="true" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" filter="true" /></a></td>
		<td align='center'><bean:write name="item" property="proxyName" /></td>
		<td align='center'><bean:write name="item" property="price" format="#.00" /></td>
<%if(group.isFlag(41)){%>
		<td align='center'><bean:write name="item" property="price3" format="#.00" /></td>
<%}%>
<%if(group.isFlag(41)){%>
		<td align='center'><%if(voItem.getPrice5()>0){%><bean:write name="item" property="price5" format="#.00" /><%}else{ %>0.00<%} %></td>
<%}%>
		<td align='center'><%= voItem.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align='center'><%= voItem.getStockAllType(ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCountAllType(ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align='center'><%=startDate%>至<%=endDate%></td>
		</tr>
<%row++;%>
</logic:iterate>
</logic:present>
          </table>
          <br>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
</body>
</html>