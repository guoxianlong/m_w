<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
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
	clipboardData.setData('text',getTblText(listTable));
	alert("列表内容已复制到剪贴板，粘贴到excel文件中即可。");
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<body>
<%@include file="../header.jsp"%>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form method=post action="searchProductStockinHistory.do">
<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String oriname = (String)request.getParameter("oriname");
if(oriname == null)oriname="";
String name = (String)request.getParameter("name");
if(name == null)name="";
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate="2007-01-01";
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.getNow().substring(0, 10);
String price = (String)request.getParameter("price");
if(price == null)price = "";
String stockCount = (String)request.getParameter("stockCount");
if(stockCount == null)stockCount = "";

HashMap catalogMap = (HashMap)request.getAttribute("catalogMap");
HashMap map = (HashMap)request.getAttribute("map");
HashMap mapBj = (HashMap)request.getAttribute("mapBj");
HashMap mapGd = (HashMap)request.getAttribute("mapGd");
%>
产品编号：<input type=text name="code" size="20" value="<%=code%>"><br>
原名称：<input type=text name="oriname" size="20" value="<%=oriname%>">（模糊）<br>
小店名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br>
<%--
小店价格：<input type=text name="price" size="20" value="<%=price%>"><br>
入库量：<input type=text name="stockCount" size="20" value="<%=stockCount%>"><br>
--%>
时间：<input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');"><br>
地点：<select name="area">
	<option value="" >全部</option>
	<option value="0" <%if("0".equals(request.getParameter("area"))){%>selected<%}%>>北京</option><option value="1" <%if("1".equals(request.getParameter("area"))){%>selected<%}%>>广东</option></select><br/>
<input type=submit value="查询入库记录">
</form>
</td></tr>
</table>
</td></tr></table>
<%--
          <br /><table align="center" width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
          	<tr bgcolor="#4688D6">
              <td align="center" width="10%"><font color="#FFFFFF">一级分类</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">比例</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">金额</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">北京比例</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">北京金额</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">广东比例</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">广东金额</font></td>
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
%>
			<tr bgcolor='#F8F8F8'>
				<td align='center'><%= p1.getName() %></td>
				<td align='center'><%= StringUtil.formatFloat((value.floatValue() / total.floatValue()) * 100) %>%</td>
				<td align='center'><%= StringUtil.formatFloat(value.floatValue()) %></td>
				<td align='center'><%= StringUtil.formatFloat((valueBj.floatValue() / total.floatValue()) * 100) %>%</td>
				<td align='center'><%= StringUtil.formatFloat(valueBj.floatValue()) %></td>
				<td align='center'><%= StringUtil.formatFloat((valueGd.floatValue() / total.floatValue()) * 100) %>%</td>
				<td align='center'><%= StringUtil.formatFloat(valueGd.floatValue()) %></td>
			</tr>
<%
		}
	}
}
%>
          </table><br /><br />
--%>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
          <br><form method=post action="" name="productForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
            <tr bgcolor="#4688D6">
              <td align="center" width="5%"><font color="#FFFFFF">日期</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">北京入库量</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">广东入库量</font></td>
            </tr>
<logic:present name="productList" scope="request">
<logic:iterate name="productList" id="item" > 
	<tr bgcolor='#F8F8F8'>		
		<td align='center'><bean:write name="item" property="name" /></td>
		<td align='center'><bean:write name="item" property="stock" /></td>
		<td align='center'><bean:write name="item" property="stockGd" /></td>
	</tr>
</logic:iterate>
</logic:present>
          </table>
          <br>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
</body>
</html>