
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>

<%
List list = (List) request.getAttribute("list");
List areaList = (List) request.getAttribute("areaList");
String code=StringUtil.convertNull(request.getParameter("code"));
String storage=request.getParameter("storage");
String status=request.getParameter("status");
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
int hour =24;
int m =5;
String tip=(String)request.getAttribute("tip");
int ordercount = StringUtil.parstInt(request.getAttribute("ordercount")+"");
if(tip!=null && tip.length()>0){
  %><script>alert('<%= tip %>');</script><%
  }
PagingBean paging = (PagingBean) request.getAttribute("paging");
int st = StringUtil.toInt(storage);
String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("storage", "storage", request, st, true, "-1");
%>
<html>
<head>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>

<title>异常货位盘点计划列表</title>
<style type="text/css">
td,th {
color: #FFF;
}
.STYLE3 {
	font-size: 18px;
	font-weight: bold;
	color: #FF0000;
}
</style>
<script type="text/javascript">

</script>
</head>
<body bgcolor="#ffcc00">

<table width="99%">
	<tr>
	    <td width="46%"><span class="STYLE3">&nbsp;&nbsp;&nbsp;&nbsp;异常货位盘点计划列表：</span></td>
	    <%if(group.isFlag(781)){ %>
		<td width="16%">
	    <a href="<%= request.getContextPath()%>/AbnormalCargoCheckController/toGenerateBySKU.mmx">按SKU生成异常货位盘点计划</a>
		</td>
		<td width="20%">
			<%--
			<%if(areaList.contains("3")){ 
				
			%>
			<form  action="<%=request.getContextPath()%>/admin/abnormalCargoCheck.do">
				<div align="right">
				  <input type="submit" value="生成增城异常货位盘点计划">
				  <input type="hidden" name='method' value='addAbnormalCargoCheck'>
				  <input type="hidden" name=areaId value='3'>
		      </div>
	  		</form>
	  		<%} %>
	  		 --%>
	  	</td>
		<td width="18%">
			<%--
			<%if(areaList.contains("4")){ %>
			<form  action="<%=request.getContextPath()%>/admin/abnormalCargoCheck.do">
				<div align="right">
				  <input type="submit" value="生成无锡异常货位盘点计划">
				  <input type="hidden" name='method' value='addAbnormalCargoCheck'>
				  <input type="hidden" name=areaId value='4'>
		      </div>
	  		</form>	
	  		<%} %>	
	  		--%>
	  	</td>
	  	<%} %>
	</tr>
</table>


<form id="form2" name="form2" method="post" action="<%=request.getContextPath()%>/admin/abnormalCargoCheck.do?method=abnormalCargoCheckList">
  <table width="60%" border="0">
    <tr>
      <td width="30%">
        <%= wareAreaLable%>
        &nbsp;&nbsp;&nbsp;
        <select name="status" id="status">
         <option value="0">未开始</option>
         <option value="1">一盘中</option>
         <option value="2">二盘中</option>
         <option value="3">终盘中</option>
        </select>&nbsp;&nbsp;&nbsp;
     <input type="text" name="code" id="code" value="<%=code %>" />&nbsp;&nbsp;&nbsp;
     <input type="submit" name="button2" id="button2" value="查询" />      </td>
    </tr>
  </table>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><strong><font color="#00000">异常货位盘点计划号</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">创建时间</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">创建人</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">报损报溢单生成人</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">库地区</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">状态</font></strong></div></td>
  </tr>
<%if(list!=null){
for(int i=0;i<list.size();i++){
	AbnormalCargoCheckBean accBean = (AbnormalCargoCheckBean)list.get(i);
%>
  <tr <%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
    <td style="color: #000"><div align="center"><a href="<%=request.getContextPath()%>/admin/abnormalCargoCheck.do?method=getAbnormalCargoCheckDetail&id=<%= accBean.getId()%>"><%=accBean.getCode() %></a></div></td>
    <td style="color: #000"><div align="center"><%=StringUtil.cutString(accBean.getCreateDatetime(),19) %></div></td>
    <td style="color: #000"><div align="center"><%=accBean.getCreateUserName() %></div></td>
    <td style="color: #000"><div align="center"><%=StringUtil.convertNull(accBean.getOperCreateUserName() )%>&nbsp;</div></td>
    <td style="color: #000"><div align="center"><%=ProductStockBean.getAreaName(accBean.getArea()) %></div></td>
    <td style="color: #000"><div align="center"><%=accBean.getStatusName()%></div></td>
  </tr>
<%}}%>
</table>
</form>
<script>
function load(){
 selectOption(document.getElementById("storage") ,"<%=storage%>");
 selectOption(document.getElementById("status") ,"<%=status%>");
}
load();
</script>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</body>
</html>