<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<html>
<head>
<title>扫描包裹单号</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
int checkStatus=-1;//决定提示信息
if(request.getAttribute("checkStatus")!=null){
	checkStatus=Integer.parseInt(request.getAttribute("checkStatus").toString());
}
String orderCode=request.getAttribute("orderCode")==null?"":request.getAttribute("orderCode").toString();
String packageCode=request.getAttribute("packageCode")==null?"":request.getAttribute("packageCode").toString();
String oriPackageCode=request.getAttribute("oriPackageCode")==null?"":request.getAttribute("oriPackageCode").toString();
String otherCode=request.getAttribute("otherCode")==null?"":request.getAttribute("otherCode").toString();
String fromPrintPackage=request.getParameter("fromPrintPackage")==null?"":request.getParameter("fromPrintPackage");
%>
<script type="text/javascript">
<%if(checkStatus==7&&group.isFlag(392)){%>
	window.location="printPackage.do?method=printPackage&fromOrderPackage=1&orderCode2=<%=orderCode%>&packageCode=<%=packageCode%>";
<%}%>
</script>
<script type="text/javascript">
function checkOrderCode(){
	document.getElementById("packageCode").focus();
	return false;
}
function submitPackage(){
	var orderCode=document.getElementById("orderCode").value;
	if(trim(orderCode)==""){
		document.getElementById("tip").style.color="red";
		document.getElementById("tip").innerHTML="请扫描或输入发货单编号！";
		document.getElementById("packageCode").value="";
		document.getElementById("orderCode").focus();
		return false;
	}
	var packageCode=document.getElementById("packageCode").value;
	if(trim(packageCode)==""){
		document.getElementById("tip").style.color="red";
		document.getElementById("tip").innerHTML="请扫描或输入包裹单号！";
		document.getElementById("packageCode").focus();
		return false;
	}
//	if(trim(orderCode)==trim(packageCode)){
//		alert("包裹单号"+packageCode+"异常，扫描失败！");
//		document.getElementById("orderCode").value="";
//		document.getElementById("packageCode").value="";
//		document.getElementById("orderCode").focus();
//		return false;
//	}
	var orderCode2=document.getElementById("orderCode2");
	orderCode2.value=orderCode;
	return true;
}
function tips(){
	<%if(checkStatus==1){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("发货单<%=orderCode%>未分拣，扫描失败！");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==2){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("发货单<%=orderCode%>未复核，扫描失败！");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==0){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("只能操作‘用户所属的库地区和库类型’的入库单。");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==3){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("发货单<%=orderCode%>已扫描过包裹单号<%=oriPackageCode%>");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==4){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("发货单<%=orderCode%>的快递公司异常，扫描失败！");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==5){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("发货单<%=orderCode%>没有结算周期，扫描失败！"+"\r"+"请咨询结算人员，设置结算周期！");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==6){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		var conf=confirm("发货单<%=orderCode%>的包裹单号与发货单<%=otherCode%>的包裹单号重复。"+"\r"+"确定要继续当前操作吗？");
		if(conf){
			document.getElementById("packageCode").value="<%=packageCode%>";
			document.getElementById("orderCode2").value="<%=orderCode%>";
			document.getElementById("confirm").value="1";
			document.getElementById("confirm2").value="1";
			document.forms[1].submit();
		}else{
			document.getElementById("orderCode").value="";
			document.getElementById("packageCode").value="";
		}
	<%}else if(checkStatus==7){%>
		document.getElementById("tip").style.color="green";
		document.getElementById("tip").innerHTML="发货单<%=orderCode%>，包裹单号<%=packageCode%>扫描成功！";
	<%}else if(checkStatus==8){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		var conf=confirm("发货单<%=orderCode%>已扫描过包裹单号<%=oriPackageCode%>"+"\r"+"确定要继续当前操作吗？");
		if(conf){
			document.getElementById("packageCode").value="<%=packageCode%>";
			document.getElementById("orderCode2").value="<%=orderCode%>";
			document.getElementById("confirm2").value="1";
			document.forms[1].submit();
		}else{
			document.getElementById("orderCode").value="";
			document.getElementById("packageCode").value="";
		}
	<%}else if(checkStatus==9){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("包裹单号<%=packageCode%>异常，扫描失败！");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==10){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("包裹单号格式错误，请重新扫描！");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}else if(checkStatus==11){%>
		document.getElementById("orderCode").value="<%=orderCode%>";
		document.getElementById("packageCode").value="<%=packageCode%>";
		alert("非货到付款订单只能关联广东省速递局或广东省外的包裹单，请重新扫描！");
		document.getElementById("orderCode").value="";
		document.getElementById("packageCode").value="";
	<%}%>
}
</script>
</head>
<body>
&nbsp;&nbsp;&nbsp;扫描包裹单号&nbsp;&nbsp;&nbsp;&nbsp;<span id="tip"></span>
<form action="" onsubmit="return checkOrderCode();">
订单编号&nbsp;&nbsp;&nbsp;：<input type="text" id="orderCode" name="orderCode" />
</form>
<form action="orderPackage.do?method=orderPackage" onsubmit="return submitPackage();" method="post">
包裹单编号：<input type="text" id="packageCode" name="packageCode" />
<input type="hidden" id="orderCode2" name="orderCode" value=""/>
<input type="hidden" id="confirm" name="confirm" value=""/>
<input type="hidden" id="confirm2" name="confirm2" value=""/><br/>
<input type="submit" value="确认" />
</form>
<script type="text/javascript">
tips();
<%if(fromPrintPackage.equals("1")){%>
	document.getElementById("orderCode").value="<%=request.getParameter("orderCode")%>"
	document.getElementById("tip").style.color="green";
	document.getElementById("tip").innerHTML="发货单<%=request.getParameter("orderCode")==null?"":request.getParameter("orderCode")%>，打印包裹单成功！";
	document.getElementById("packageCode").focus();
<%}else{%>
	document.getElementById("orderCode").focus();
<%}%>
</script>
</body>
</html>