<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="cache.*"%>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
%>
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
<script language="JavaScript" src="js/JS_functions.js"></script>
<body>
<%@include file="../header.jsp"%>
<pre>
规则说明:
1、时间:订单生成日期
2、订单范围:非重复订单
3、产品状态：全部、在架上和在架下（在架上：普通、促销和热销，在架下：下架和
   隐藏，全部：所有架上和架下产品）
4、查询选项：均为可选项
5、时间段内根据产品分类,产品状态，以及产品进行销量查询，所有订单的销量以产
   品为单元显示
</pre>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="sForm" method=post action="searchOrderProductHistory.do">
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
String parentId1 = StringUtil.convertNull(request.getParameter("parentId1"));
String area = StringUtil.convertNull(request.getParameter("area"));
HashMap catalogMap = (HashMap)request.getAttribute("catalogMap");
int areaGroup = StringUtil.StringToId(request.getParameter("areaGroup"));
String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
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
产品编号：<input type=text name="code" size="20" value="<%=code%>"><br>
原名称：<input type=text name="oriname" size="20" value="<%=oriname%>">（模糊）<br>
小店名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br>
时间：<input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');" readonly="readonly" />到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');" readonly="readonly" /><br>
状态：<select name="status"><option value="-1" <%if("0".equals(request.getParameter("status"))){%>selected<%}%>>全部</option><option value="0" <%if("0".equals(request.getParameter("status"))){%>selected<%}%>>在架上</option><option value="1" <%if("1".equals(request.getParameter("status"))){%>selected<%}%>>已下架</option></select><br/>
产品分类：<select name="parentId1" multiple="15" size="15" style="width:180">
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
</select>(按住Ctrl多选)<br/>
<br>
    <label><input type="radio" name="areaGroup" value="0" <%=areaGroup==0?"checked":"" %>>全部</label>
    <label><input type="radio" name="areaGroup" value="1" <%=areaGroup==1?"checked":"" %>>非无锡单</label>
    <label><input type="radio" name="areaGroup" value="2" <%=areaGroup==2?"checked":"" %>>无锡单</label><br>
<br/>
<%if(parentId1!=null&&!parentId1.equals("")){ %>
<script type="text/javascript">
	selectOption(sForm.parentId1, <%=parentId1%>);
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
 </select>
<br/>
<input type=submit value="查询订单销量记录">
</form>
</td></tr>
</table>
</td></tr></table>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
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
              <td align="center" width="10%"><font color="#FFFFFF">所有订单销量</font></td>
			  <td align="center"><font color="#FFFFFF">时间段</font></td>    
            </tr>
<%
	LinkedHashMap productMap = (LinkedHashMap)request.getAttribute("productMap");
	if(productMap != null){
		Iterator mapIter = productMap.entrySet().iterator();
		while(mapIter.hasNext()){
			Map.Entry entry = (Map.Entry)mapIter.next();
			adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)entry.getValue();
			if(voItem.getBuyCount() > 0){
				voCatalog p1 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId1()));
				voCatalog p2 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId2()));
%>
		<tr bgcolor='#F8F8F8'>		
		<td align=left width="60"><a href="fproduct.do?id=<%=voItem.getId() %>" ><%=voItem.getCode() %></a></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><a href="fproduct.do?id=<%=voItem.getId() %>" ><%=voItem.getName() %></a></td>
		<td align='center'><a href="fproduct.do?id=<%=voItem.getId() %>" ><%=voItem.getOriname() %></a></td>
		<td align='center'><%=df.format(voItem.getPrice()) %></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='center'><%=df.format(voItem.getPrice3()) %></td>
<%}%>
		<td align='center'><%=voItem.getBuyCount() %></td>
		<td align='center'><%=startDate%>至<%=endDate%></td>
		</tr>
<%
  	}
	}
  }
%>
          </table>
          <br>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
</body>
</html>