<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<html>
<head>
<title>打印包裹单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function checkCode(){
	var orderCode=document.getElementById("orderCode");
	var orderCode2=document.getElementById("orderCode2");
	orderCode.value=orderCode2.value;
	if(trim(orderCode.value)==""){
		document.getElementById("tip").style.color="red";
		document.getElementById("tip").innerHTML="请扫描或输入发货单编号！";
		document.getElementById("orderCode2").focus();
		return false;
	}else{
		document.getElementById("tip").innerHTML="";
		var weight=trim(document.getElementById("weight").value);
		if(weight==""){
			var b=confirm("未获取重量！");
			document.getElementById("weight").focus();
			return b;
		}
		if(weight.length > 14){
			if(weight.substring(0,5) != "ST,GS"){
				alert("该电子称输出模式非稳定毛重模式，请检查电子称是否已调试好！");
				document.getElementById("weight").value = "";
				document.getElementById("weight").focus();
				return false;
			}
		}else{
			var reg=/^\d{0,2}\.?\d*$/;
			if(reg.exec(weight)==null){
				alert("包裹重量输入格式错误！");
				document.getElementById("weight").value = "";
				document.getElementById("weight").focus();
				return false;
			}
		}
		return true;
	}
}
function submitOrderCode(){
	var orderCode2=document.getElementById("orderCode2");
	var orderCode=document.getElementById("orderCode");
	orderCode.value=orderCode2.value;
	document.getElementById("weight").focus();
	return false;
}
</script>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
String orderCode=request.getAttribute("orderCode")==null?"":request.getAttribute("orderCode").toString();
String weight=request.getAttribute("weight")==null?"":request.getAttribute("weight").toString();
voOrder order=(voOrder)request.getAttribute("order");
OrderStockBean osBean=(OrderStockBean)request.getAttribute("osBean");
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
%>
<script type="text/javascript">
<%if(request.getAttribute("checkStatus")!=null&&request.getAttribute("checkStatus").toString().equals("6")&&group.isFlag(392)){%>
	window.location="orderPackage.do?method=orderPackage&fromPrintPackage=1&orderCode=<%=orderCode%>";
<%}%>
</script>
</head>
<body>
&nbsp;&nbsp;&nbsp;打印包裹单&nbsp;&nbsp;&nbsp;&nbsp;<span id="tip"></span>
<form action="" onsubmit="return submitOrderCode();">
订单编号：<input type="text" id="orderCode2"/>
</form>
<form action="printPackage.do?method=printPackage" onsubmit="return checkCode();" method="post">
<input type="hidden" id="orderCode" name="orderCode" />
包裹重量：<input type="text" id="weight" name="weight" />kg<br/>
<input type="submit" value="确认" />
</form>

<script type="text/javascript">
<%
if(request.getAttribute("checkStatus")!=null){
	String checkStatus=request.getAttribute("checkStatus").toString();
	if(checkStatus.equals("1")){%>
		alert("发货单<%=orderCode%>未分拣，打印失败！");
	<%}else if(checkStatus.equals("2")){%>
		alert("发货单<%=orderCode%>未复核，打印失败！");
	<%}else if(checkStatus.equals("3")){%>
		var check=confirm("发货单<%=orderCode%>已打印过包裹单，确定重新打印？");
		if(check){
			window.location="printPackage.do?method=printPackage&confirm=1&orderCode=<%=orderCode%>&weight=<%=weight%>";
		}
	<%}else if(checkStatus.equals("4")){%>
		alert("非EMS订单，不允许在此打印包裹单！");
	<%}else if(checkStatus.equals("5")){%>
		alert("发货单<%=orderCode%>，无法辨别是省内单还是国内单。"+"\r"+"请联系导单人员！");
	<%}else if(checkStatus.equals("6")){%>
		document.getElementById("tip").style.color="green";
		document.getElementById("tip").innerHTML="发货单<%=orderCode%>，打印包裹单成功！";
	<%}else if(checkStatus.equals("7")){%>
		alert("系统暂时仅支持货到付款的发货单的打印！");
	<%}else if(checkStatus.equals("8")){%>
		document.getElementById("tip").style.color="red";
		document.getElementById("tip").innerHTML="未找到打印机，打印失败！";
		document.getElementById("orderCode").focus();
	<%}else if(checkStatus.equals("9")){%>
		document.getElementById("tip").style.color="red";
		document.getElementById("tip").innerHTML="发货单<%=orderCode%>已打印过包裹单，无权再打印！";
		document.getElementById("orderCode").focus();
	<%}
}
if(request.getParameter("fromOrderPackage")!=null&&request.getParameter("fromOrderPackage").equals("1")){
	String orderCode2=request.getParameter("orderCode2")==null?"":request.getParameter("orderCode2");
	String packageCode=request.getParameter("packageCode")==null?"":request.getParameter("packageCode");%>
	document.getElementById("tip").style.color="green";
	document.getElementById("tip").innerHTML="发货单<%=orderCode2%>，包裹单号<%=packageCode%>扫描成功！";
<%}
%>
document.getElementById("orderCode2").focus();
</script>
</body>
</html>