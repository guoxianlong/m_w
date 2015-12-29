<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<html>
<head>
<title>核对包裹</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
voOrder order=(voOrder)request.getAttribute("order");
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
OrderStockProductBean ospBean=(OrderStockProductBean)request.getAttribute("ospBean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
String date=request.getParameter("date");
String time1=request.getParameter("time1");
String time2=request.getParameter("time2");
String areano=request.getParameter("areano");
String[] deliver=request.getParameterValues("deliver");
String searchType=request.getParameter("searchType");
int count1=0;
int count2=0;
int count3=0;
int count4=0;
if(request.getAttribute("count1")!=null){
	count1=Integer.parseInt(request.getAttribute("count1").toString());
}
if(request.getAttribute("count2")!=null){
	count2=Integer.parseInt(request.getAttribute("count2").toString());
}
if(request.getAttribute("count3")!=null){
	count3=Integer.parseInt(request.getAttribute("count3").toString());
}
if(request.getAttribute("count4")!=null){
	count4=Integer.parseInt(request.getAttribute("count4").toString());
}
int checkStatus=-1;//决定提示信息
if(request.getAttribute("checkStatus")!=null){
	checkStatus=Integer.parseInt(request.getAttribute("checkStatus").toString());
}
List auditPackageList=(List)request.getAttribute("auditPackageList");
%>
<script type="text/javascript">
function toExcel(){
	document.getElementById("action").value="export";
	document.forms[1].submit();
}
function showList(){
	document.getElementById("action").value="";
	document.forms[1].submit();
}
function check(){
	var packagediv=document.getElementById("package");
	if(packagediv.style.display=="block"){
		var packageCode=document.getElementById("packageCode");
		if(trim(packageCode.value)==""){
			alert("请先输入包裹单号！");
			if(document.getElementById("order").style.display=="block"){
				document.getElementById("orderCode").focus();
			}else if(document.getElementById("package").style.display=="block"){
				document.getElementById("packageCode").focus();
			}
			return false;
		}
	}
	var orderdiv=document.getElementById("order");
	if(orderdiv.style.display=="block"){
		var orderCode=document.getElementById("orderCode");
		if(trim(orderCode.value)==""){
			alert("请先输入订单号！");
			if(document.getElementById("order").style.display=="block"){
				document.getElementById("orderCode").focus();
			}else if(document.getElementById("package").style.display=="block"){
				document.getElementById("packageCode").focus();
			}
			return false;
		}
	}
	return true;
}
function tips(){
	<%if(checkStatus==0){%>
		document.getElementById("checkStatus").innerHTML="核对成功！";
		document.getElementById("checkStatus").color="green";
		document.getElementById("package").style.display="block";
		document.getElementById("order").style.display="none";
	<%}else if(checkStatus==1){%>
		document.getElementById("checkStatus").innerHTML="核对失败！请先复核出库再核对！";
		document.getElementById("checkStatus").color="red";
	<%}else if(checkStatus==2){%>
		document.getElementById("checkStatus").innerHTML="核对失败！该订单号<%=request.getParameter("orderCode") %>未分拣，请重新核实！";
		document.getElementById("checkStatus").color="red";
	<%}else if(checkStatus==3){%>
		var audit=confirm('该订单<%=order.getCode()%>已核对过。'+'\r'+'如果要导入包裹单号，请单击‘确定’，反之，请单击‘取消’！');
		if(audit){
			document.getElementById("checkStatus").innerHTML="该订单已扫描过，请不要重复核对！";
			document.getElementById("checkStatus").color="blue";
			document.getElementById("package").style.display="block";
			document.getElementById("order").style.display="none";
		}
	<%}else if(checkStatus==4){%>
		<%
		String otherCode=null;
		if(request.getAttribute("otherCode")!=null){
			otherCode=request.getAttribute("otherCode").toString();
		}
		%>
		document.getElementById("package").style.display="block";
		document.getElementById("order").style.display="none";
		document.getElementById("packageCode").value="<%=request.getParameter("packageCode").toString()%>";
		var choose=confirm("订单<%=otherCode%>的包裹单号与当前包裹单号重复。"+"\r"+"如果继续导入，请单击‘确定’，反之，请单击‘取消’！");
		if(choose){
			document.getElementById("packageCode").value="<%=request.getParameter("packageCode")%>";
			document.getElementById("confirmPackage").value="1";
			document.getElementById("confirmPackage2").value="1";
			document.forms[0].submit();
		}else{
			document.getElementById("package").style.display="none";
			document.getElementById("order").style.display="block";
			document.getElementById("packageCode").value="";
		}
	<%}else if(checkStatus==5){%>
		document.getElementById("checkStatus").innerHTML="包裹单号导入成功！";
		document.getElementById("checkStatus").color="green";
	<%}else if(checkStatus==6){%>
		<%String oriOrderCode=request.getAttribute("oriOrderCode").toString();%>
		<%String oriPackageCode=request.getAttribute("oriPackageCode").toString();%>
		document.getElementById("package").style.display="block";
		document.getElementById("order").style.display="none";
		document.getElementById("packageCode").value="<%=request.getParameter("packageCode").toString()%>";
		var choose=confirm("该订单<%=oriOrderCode%>已导入过包裹单号<%=oriPackageCode%>。"+"\r"+"如果确定要重新导入，请单击‘确定’，反之请单击‘取消’！");
		if(choose){
			document.getElementById("confirmPackage").value="1";
			document.forms[0].submit();
		}else{
			document.getElementById("package").style.display="none";
			document.getElementById("order").style.display="block";
			document.getElementById("packageCode").value="";
		}
	<%}else if(checkStatus==7){%>
		alert("该订单没有结算周期，导入包裹单号失败！"+"\r"+"请咨询结算人员，设置结算周期！");
	<%}else if(checkStatus==8){%>
		alert("快递公司异常，导入包裹单号失败！");
	<%}else if(checkStatus==9){%>
		alert("订单编号异常，导入包裹单号失败！");
	<%}else if(checkStatus==10){%>
		alert("该订单状态异常，导入包裹单号失败！");
	<%}else if(checkStatus==11){%>
		alert("前一个时间点必须小于等于后一个时间点！");
	<%}%>
}
function getFocus(){
	if(document.getElementById("order").style.display=="block"){
		document.getElementById("orderCode").focus();
	}else if(document.getElementById("package").style.display=="block"){
		document.getElementById("packageCode").focus();
	}
}
</script>
</head>

<body onload="getFocus();">
&nbsp;&nbsp;核对包裹&nbsp;&nbsp;<font id="checkStatus"></font>
<br/>
<form action="auditPackage.do?method=auditPackage" name="form" method="post" onsubmit="return check();">
	<div id="package" style="display:none">
		包裹单号：<input type="text" id="packageCode" name="packageCode" size="16" maxlength="45"/>
		<input type="hidden" id="confirmPackage" name="confirm" value="0"/>
		<input type="hidden" id="confirmPackage2" name="confirm2" value="0"/>
		<%if(order!=null){ %><input type="hidden" name="orderId" value="<%=order.getId() %>"/><%} %>
		<input type="submit" value="确认" />
	</div>
	<div id="order" style="display:block">
		订单编号：<input type="text" id="orderCode" name="orderCode" size="16" maxlength="45"/>
		<input type="submit" value="确认" />
	</div>
	
</form>
<%if(order!=null&&apBean!=null){ %>
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 10px">
	<tr>
		<td>序列号：<%=order.getSerialNumber() %></td>
		<td>订单编号：<%=order.getCode() %></td>
	</tr>
	<tr>
		<td>客户姓名：<%=order.getName() %></td>
		<td>发货地点：<%=ProductStockBean.getAreaName(ospBean.getStockArea()) %></td>
	</tr>
	<tr>
		<td>分拣时间：<%=apBean.getSortingDatetime().substring(0,19) %></td>
		<td>快递公司：<%=order.getDeliverName() %><%=apBean.getPackageCode().equals("")?"":"("+apBean.getPackageCode()+")" %></td>
	</tr>
	<tr>
		<td>复核出库：<%=apBean.getCheckDatetime()==null?"":apBean.getCheckDatetime().substring(0,19) %>&nbsp;<%=apBean.getCheckUserName() %></td>
		<td>核对包裹：<%=apBean.getAuditPackageDatetime()==null?"":apBean.getAuditPackageDatetime().substring(0,19) %>&nbsp;<%=apBean.getAuditPackageUserName() %></td>
	</tr>
</table>
<%} %>
<br/>
<%if(group.isFlag(381)&&group.isFlag(382)){ %>
&nbsp;&nbsp;查看发货量<br/>
<form action="auditPackage.do?method=auditPackageList" method="post">
	分拣日期：<input type="text" name="date" onclick="SelectDate(this,'yyyy-MM-dd');" <%if(date!=null){ %>value="<%=date%>"<%}else{ %>value="<%=DateUtil.getNowDateStr() %>"<%} %>ReadOnly="readonly" size="10"/>
	<select name="time1" >
		<%for(int i=0;i<=23;i++){ %>
			<option value=<%=i<10?"0"+i:i %> <%if(time1!=null&&time1.equals(i<10?"0"+i:i+"")){ %>selected=selected<%} %>><%=i<10?"0"+i:i %></option>
		<%} %>
	</select>时至<select name="time2" >
		<%for(int i=0;i<=23;i++){ %>
			<option value=<%=i<10?"0"+i:i %> <%if(time2!=null&&time2.equals(i<10?"0"+i:i+"")){ %>selected=selected<%} %>><%=i<10?"0"+i:i %></option>
		<%} %>
	</select>时&nbsp;&nbsp;
	发货地：<select name="areano">
	<option value=""></option>
	<option value="2" <%if(areano!=null&&areano.equals("2")){ %>selected=selected<%} %>>广速</option>
	<option value="1" <%if(areano!=null&&areano.equals("1")){ %>selected=selected<%} %>>芳村</option>
	<option value="0" <%if(areano!=null&&areano.equals("0")){ %>selected=selected<%} %>>北库</option>
	</select>&nbsp;&nbsp;
	快递公司：<input type="checkbox" name="deliver" value="11"/>广东省速递局&nbsp;&nbsp;
	<input type="checkbox" name="deliver" value="10"/>广州宅急送&nbsp;&nbsp;
	<input type="checkbox" name="deliver" value="9"/>广东省外<br/>
	查看&nbsp;<input type="radio" name="searchType" value="1" <%if(searchType!=null&&searchType.equals("1")){ %>checked=checked<%} %>/>未核对包裹&nbsp;&nbsp;
	<input type="radio" name="searchType" value="2" <%if(searchType!=null&&searchType.equals("2")){ %>checked=checked<%} %>/>未导入包裹单号&nbsp;&nbsp;
	<input type="radio" name="searchType" value="3" <%if(searchType==null||searchType.equals("3")){ %>checked=checked<%} %>/>全部订单&nbsp;&nbsp;
	<input type="hidden" id="action" name="action" value=""/>
	<input type="button" onclick="showList();" value="查询"/>&nbsp;
	<input type="button" onclick="toExcel();" value="导出excel文件 "/>
<br/><br/>
<%if(auditPackageList!=null){ %>
<div>
导单分拣：共<%=count1 %>单&nbsp;&nbsp;
复核出库：共<%=count2 %>单&nbsp;&nbsp;
核对包裹：共<%=count3 %>单&nbsp;&nbsp;
导入包裹单号：共<%=count4 %>单
</div>
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
	<tr bgcolor="#4688D6">
		<td align="center"><font color="#FFFFFF">序</font></td>
		<td align="center"><font color="#FFFFFF">订单编号</font></td>
		<td align="center"><font color="#FFFFFF">分拣时间</font></td>
		<td align="center"><font color="#FFFFFF">复核出库时间</font></td>
		<td align="center"><font color="#FFFFFF">核对包裹时间</font></td>
		<td align="center"><font color="#FFFFFF">包裹单号</font></td>
		<td align="center"><font color="#FFFFFF">快递公司</font></td>
	</tr>
	
	<%for(int i=0;i<auditPackageList.size();i++){ %>
	<%AuditPackageBean ap=(AuditPackageBean)auditPackageList.get(i); %>
	<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
		<td align="center"><%=paging.getCountPerPage()*paging.getCurrentPageIndex()+i+1 %></td>
		<td align="center"><%=ap.getOrderCode() %></td>
		<td align="center"><%=ap.getSortingDatetime().substring(0,19)%></td>
		<td align="center"><%=ap.getCheckDatetime()==null?"":ap.getCheckDatetime().substring(0,19) %></td>
		<td align="center"><%=ap.getAuditPackageDatetime()==null?"":ap.getAuditPackageDatetime().substring(0,19) %></td>
		<td align="center"><%=ap.getPackageCode() %></td>
		<td align="center"><%=ap.getDeliverName() %></td>
	</tr>
	<%} %>
</table>
<%} %>
</form>
<%if(paging!=null){ %>
		<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
<script type="text/javascript">
<%if(deliver!=null){%>
	<%for(int i=0;i<deliver.length;i++){%>
		var count=document.getElementsByName("deliver");
		for(var j=0;j<count.length;j++){
			if(count[j].value=="<%=deliver[i]%>"){
				count[j].checked=true;
			}
		}
	<%}%>
<%}%>
<%}%>
</script>

<script type="text/javascript">
tips();
</script>

</body>
</html>