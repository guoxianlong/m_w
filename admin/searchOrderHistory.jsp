<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,java.lang.*" %>
<%@ page import="adultadmin.action.vo.voProductLine,adultadmin.action.vo.voSelect" %>
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
%>
<html>
<title>小店后台管理 - 搜索产品销量记录</title>
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
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/brandNames2.js"></script>
<script language="JavaScript" src="js/JS_functions.js"></script>
<script type="text/javascript">
	function autoWrite(textName,selectName){
		textName.value = selectName.options[selectName.selectedIndex].text;
	}
</script>
<body>
<%@include file="../header.jsp"%>
<p>
<pre>
规则说明:
1、同一时间段内所有成交订单销量统计
2、时间:按订单出货日期计算;
3、地区:当选全部时，成交量=北京出库量+广东出库量+未出库订单销量；
        选择北京时，成交量=北京出库量；
        选择广东时，成交量=广东出库量；
        成交量=待发货（已到款）+待查款+已发货+已结算+已妥投+待退回
        未出库=待发货（已到款）+待查款
        北京出库量=已发货+已结算+已妥投+待退回 的北京发货的订单
        广东出库量=已发货+已结算+已妥投+待退回 的广东发货的订单
4、分类比例:随地区选择调整,当选择全部时则按所有的成交订单统计,选择北京时则按北京出货订单统计,
   选择广东则按广东出货订单统计，出货订单包括（待出货和已出货订单）
</pre>
</p>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="sForm" method=post action="searchOrderHistory.do">
  <p>
    <%
String code = (String)request.getParameter("code");
if(code == null)code="";
String oriname = (String)request.getParameter("oriname");
if(oriname == null)oriname="";
String name = (String)request.getParameter("name");
if(name == null)name="";
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate="2012-01-01";
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.getNow().substring(0, 10);
String parentId1 = StringUtil.convertNull(request.getParameter("parentId1"));
String area = StringUtil.convertNull(request.getParameter("area"));
HashMap catalogMap = (HashMap)request.getAttribute("catalogMap");
String[] parentId1s=(String[])request.getAttribute("parentId1s");
int areaGroup = StringUtil.StringToId(request.getParameter("areaGroup"));
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

HashMap map = (HashMap)request.getAttribute("map");
int productBrandId = 0;	//品牌id
if(request.getAttribute("productBrandId") != null){
	productBrandId = StringUtil.StringToId(request.getAttribute("productBrandId").toString());	
}
String brandtext = StringUtil.convertNull(request.getParameter("brandtext"));	//品牌名称
String productBrandIds = StringUtil.convertNull((String)request.getAttribute("productBrandIds"));
List productBrandList = new ArrayList();
if(request.getAttribute("productBrandList") != null){
	productBrandList = (List) request.getAttribute("productBrandList");	//品牌信息
}
%>
    产品编号：
  <input type=text name="code" size="20" value="<%=code%>">
  <br>
    原名称：
  <input type=text name="oriname" size="20" value="<%=oriname%>">
    （模糊）<br>
    小店名称：
  <input type=text name="name" size="20" value="<%=name%>">
    （模糊）<br>
    时间：
  <input type=text name="startDate" size="20" value="<%=startDate%>" onClick="SelectDate(this,'yyyy-MM-dd');">
    到
  <input type=text name="endDate" size="20" value="<%=endDate%>" onClick="SelectDate(this,'yyyy-MM-dd');">
  <br>
    状态：
  <select name="status">
    <option value="0">全部</option>
        <option value="2" <%if("2".equals(request.getParameter("status"))){%>selected<%}%>>在架上</option>
    <option value="1" <%if("1".equals(request.getParameter("status"))){%>selected<%}%>>已下架</option>
  </select>
  <br/>
    产品分类：
  <select name="parentId1"  multiple="15" size="15" style="width:180">
             <option value="0">全部</option>
    <%
				HashMap map1 = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map1.get(Integer.valueOf(0));
				Iterator iter1 = list.listIterator();
				while(iter1.hasNext()){
					voCatalog catalog = (voCatalog)iter1.next();
					if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
						continue;
					}
			 %>
    <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
     <%} %>
  </select>
    (按住Ctrl多选)<br/>
  <%if(parentId1!=null&&!parentId1.equals("")){ %>
  <script> //保存上次多选状态
	selectOptionsp(sForm.parentId1, '<%=ss %>');
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
    地区：
  <select name="area">
    <option value="-1">全部</option>
    <option value="0">北京</option>
    <option value="1">广东</option>
  </select>
<br>
    <label><input type="radio" name="areaGroup" value="0" <%=areaGroup==0?"checked":"" %>>全部</label>
    <label><input type="radio" name="areaGroup" value="1" <%=areaGroup==1?"checked":"" %>>非无锡单</label>
    <label><input type="radio" name="areaGroup" value="2" <%=areaGroup==2?"checked":"" %>>无锡单</label><br>
<br/>
      <%if(area!=null&&!area.equals("")){ %>
      <script type="text/javascript">
	selectOption(sForm.area, <%=area%>);
    </script>
      <%} %>
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
	<span id="auto2" style="position: absolute; left: 350px; top: 665px;"></span>
	<input type="text" name="brandtext" id="word2" style="width: 100px; font-size: 10pt; height: 20px;" value=<%=brandtext %>/>
	<input type="hidden" name="condition2" id="condition2" value=" and id in (<%=productBrandIds %>)">
	<span style="width:18px;border:0px solid red; margin-left:-8px; margin-bottom:-6px;">
	<select name="productBrandId" id="brandId" style="margin-left:-100px; width:118px;"
	onChange="autoWrite(document.sForm.brandtext, document.sForm.productBrandId);">
		<option value="0"></option>
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
	</select></span>&nbsp;&nbsp;
 	 <br/><br/>     
      <input type=submit value="查询销量记录">
    </p>
</form>
<script type="text/javascript">
	selectOption(document.getElementById("brandId"), "<%=productBrandId %>");
</script>
</td></tr>
</table>
</td></tr></table>
          <br /><table align="center" width="50%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
          	<tr bgcolor="#4688D6">
              <td align="center" width="10%"><font color="#FFFFFF">一级分类</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">比例</font></td>
            </tr>
<%
if(map != null){
	Iterator iter = map.keySet().iterator();
	Float total = (Float)map.get(Integer.valueOf(0));
	while(iter.hasNext() && total.floatValue() > 0){
		Integer key = (Integer)iter.next();
		Float value = (Float)map.get(key);
		voCatalog p1 = (voCatalog)catalogMap.get(key);
		if(key != null && value != null && p1 != null){
			if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(key.intValue()))){
				continue;
			}
%>
			<tr bgcolor='#F8F8F8'>
				<td align='center'><%= p1.getName() %></td>
				<td align='center'><%= StringUtil.formatFloat((value.floatValue() / total.floatValue()) * 100) %>%</td>
			</tr>
<%
		}
	}
}
%>
          </table><br /><br />
<input type="button" onClick="javascript:exportList();" value="导出列表"/>
          <br><form method=post action="" name="productForm">
          <table width="99%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
              <tr bgcolor="#4688D6">
              <td align="center" width="5%"><font color="#FFFFFF">编号</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">一级分类</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">二级分类</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
              <td align="center" width="5%"><font color="#FFFFFF">价格</font></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td align="center" width="5%"><font color="#FFFFFF">批发价</font></td>
<%}%>
              <td align="center" width="8%"><font color="#FFFFFF">广东出库量</font></td>
			  <td align="center"><font color="#FFFFFF">时间段</font></td>    
            </tr>
<logic:present name="productList" scope="request">
<logic:iterate name="productList" id="item" > 
<%
	adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;
	voCatalog p1 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId1()));
	voCatalog p2 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId2()));
	if(!(voItem.getBuyCount() == 0 && voItem.getBjBuyCount() == 0 && voItem.getGdBuyCount() == 0)){
%>
		<tr bgcolor='#F8F8F8'>		
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" filter="true" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" filter="true" /></a></td>
		<td align='center'><bean:write name="item" property="price" format="#.00" /></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='center'><bean:write name="item" property="price3" format="#.00" /></td>
<%}%>
		<td align='center'><bean:write name="item" property="gdBuyCount" /></td>
		<td align='center'><%=startDate%>至<%=endDate%></td>
		</tr>
<%} %>
</logic:iterate>
</logic:present>
          </table>
          <br>
<input type="button" onClick="javascript:exportList();" value="导出列表"/>
</body>
</html>