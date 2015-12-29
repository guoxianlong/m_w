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
<title>小店后台管理 - 搜索产品退/换货入库记录</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="js/JS_functions.js"></script>
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
function SelectDate(obj,strFormat)
{
    var date = new Date();
    var by = date.getFullYear()-1;  //最小值 → 1 年前
    var ey = date.getFullYear();  //最大值 → 本年
    //cal = new Calendar(by, ey,1,strFormat);    //初始化英文版，0 为中文版
    cal = (cal==null) ? new Calendar(by, ey, 0) : cal;    //不用每次都初始化 2006-12-03 修正
    cal.dateFormatStyle = strFormat;
    cal.show(obj);
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="sForm" method=post action="searchCancelStockinHistory.do">
<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String username = (String)request.getParameter("username");
if(username == null)username="";
String orderCode = (String)request.getParameter("orderCode");
if(orderCode == null)orderCode="";
String oriname = (String)request.getParameter("oriname");
if(oriname == null)oriname="";
String name = (String)request.getParameter("name");
if(name == null)name="";
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate="2012-01-01";
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.getNow().substring(0, 10);
String price = (String)request.getParameter("price");
if(price == null)price = "";
String stockCount = (String)request.getParameter("stockCount");
if(stockCount == null)stockCount = "";
String type = StringUtil.convertNull(request.getParameter("type"));

HashMap catalogMap = (HashMap)request.getAttribute("catalogMap");
%>
订单编号：<input type=text name="orderCode" size="20" value="<%=orderCode%>"><br>
姓名：<input type=text name="username" size="20" value="<%=username%>"><br>
产品编号：<input type=text name="code" size="20" value="<%=code%>"><br>
原名称：<input type=text name="oriname" size="20" value="<%=oriname%>">（模糊）<br>
小店名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br>
小店价格：<input type=text name="price" size="20" value="<%=price%>"><br>
入库量：<input type=text name="stockCount" size="20" value="<%=stockCount%>"><br>
时间：<input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');" readonly="readonly">到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');" readonly="readonly"><br>
类型：<select name="type">
	<option value="1">退/换货</option>
	<option value="2">烂货退换</option>
</select><br/>
<script type="text/javascript">
	selectOption(sForm.type, <%=type%>);
</script>
<%--
地点：<select name="area">
	<option value="" >全部</option>
	<option value="0" <%if("0".equals(request.getParameter("area"))){%>selected<%}%>>北京</option><option value="1" <%if("1".equals(request.getParameter("area"))){%>selected<%}%>>广东</option></select><br/>
--%>
<input type=submit value="查询退/换货入库记录">
</form>
</td></tr>
</table>
</td></tr></table>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
          <br><form method=post action="" name="productForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
              <tr bgcolor="#4688D6">
              <td align="center" width="5%"><font color="#FFFFFF">编号</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">一级分类</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">二级分类</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
<%--
              <td align="center" width="5%"><font color="#FFFFFF">价格</font></td>
--%>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td align="center" width="5%"><font color="#FFFFFF">批发价</font></td>
<%}%>
              <td align="center" width="10%"><font color="#FFFFFF">增城入库量</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">芳村入库量</font></td>
              <td align="center" width="10%"><font color="#FFFFFF">广速入库量</font></td>
			  <td align="center"><font color="#FFFFFF">时间段</font></td>    
            </tr>
<logic:present name="productList" scope="request">
<%
	adultadmin.action.vo.voProduct voItemOld = null;
	int zcBuyCount = 0;
	int gfBuyCount = 0;
	int gsBuyCount = 0;
%>
<logic:iterate name="productList" id="item" > 
<%
	adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;
%>
<%
	if(voItemOld == null){
		voItemOld = voItem;
		zcBuyCount = 0;
		gfBuyCount = 0;
		gsBuyCount = 0;
		if(voItemOld.getGdBuyCount() == 3){
			zcBuyCount += voItemOld.getBjBuyCount();
		} else if(voItemOld.getGdBuyCount() == 1){
			gfBuyCount += voItemOld.getBjBuyCount();
		} else if(voItemOld.getGdBuyCount() == 2){
			gsBuyCount += voItemOld.getBjBuyCount();
		}
	} else if(voItemOld.getId() != voItem.getId()){
		voCatalog p1 = (voCatalog)catalogMap.get(Integer.valueOf(voItemOld.getParentId1()));
		voCatalog p2 = (voCatalog)catalogMap.get(Integer.valueOf(voItemOld.getParentId2()));
%>
		<tr bgcolor='#F8F8F8'>
		<td align=left width="60"><a href="fproduct.do?id=<%= voItemOld.getId() %>" ><%= voItemOld.getCode() %></a></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><a href="fproduct.do?id=<%= voItemOld.getId() %>" ><%= voItemOld.getName() %></a></td>
		<td align='center'><a href="fproduct.do?id=<%= voItemOld.getId() %>" ><%= voItemOld.getOriname() %></a></td>
<%--
		<td align='center'><%= StringUtil.formatFloat(voItemOld.getPrice()) %></td>
--%>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='center'><%= StringUtil.formatFloat(voItemOld.getPrice3()) %></td>
<%}%>
		<td align='center'><%= zcBuyCount %></td>
		<td align='center'><%= gfBuyCount %></td>
		<td align='center'><%= gsBuyCount %></td>
		<td align='center'><%=startDate%>至<%=endDate%></td>
		</tr>
<%
		voItemOld = voItem;
		zcBuyCount = 0;
		gfBuyCount = 0;
		gsBuyCount = 0;
		if(voItemOld.getGdBuyCount() == 3){
			zcBuyCount += voItemOld.getBjBuyCount();
		} else if(voItemOld.getGdBuyCount() == 1){
			gfBuyCount += voItemOld.getBjBuyCount();
		} else if(voItemOld.getGdBuyCount() == 2){
			gsBuyCount += voItemOld.getBjBuyCount();
		}
	} else {
		if(voItem.getGdBuyCount() == 3){
			zcBuyCount += voItem.getBjBuyCount();
		} else if(voItem.getGdBuyCount() == 1){
			gfBuyCount += voItem.getBjBuyCount();
		} else if(voItem.getGdBuyCount() == 2){
			gsBuyCount += voItem.getBjBuyCount();
		}
	}
%>
</logic:iterate>
<%
	if(voItemOld != null){
		voCatalog p1 = (voCatalog)catalogMap.get(Integer.valueOf(voItemOld.getParentId1()));
		voCatalog p2 = (voCatalog)catalogMap.get(Integer.valueOf(voItemOld.getParentId2()));
%>
		<tr bgcolor='#F8F8F8'>
		<td align=left width="60"><a href="fproduct.do?id=<%= voItemOld.getId() %>" ><%= voItemOld.getCode() %></a></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><a href="fproduct.do?id=<%= voItemOld.getId() %>" ><%= voItemOld.getName() %></a></td>
		<td align='center'><a href="fproduct.do?id=<%= voItemOld.getId() %>" ><%= voItemOld.getOriname() %></a></td>
<%--
		<td align='center'><%= StringUtil.formatFloat(voItemOld.getPrice()) %></td>
--%>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='center'><%= StringUtil.formatFloat(voItemOld.getPrice3()) %></td>
<%}%>
		<td align='center'><%= zcBuyCount %></td>
		<td align='center'><%= gfBuyCount %></td>
		<td align='center'><%= gsBuyCount %></td>
		<td align='center'><%=startDate%>至<%=endDate%></td>
		</tr>
<%
	}
%>
</logic:present>
          </table>
          <br>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
</body>
</html>