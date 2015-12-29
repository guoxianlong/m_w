<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>货位绑定产品</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function checkSubmit(){
	document.getElementById('method').value = 'checkCargoProduct';
	document.cargoProduct.submit();
}
</script>
<%
String productCode=request.getParameter("productCode")==null?"":request.getParameter("productCode");
String cargoCode=request.getParameter("cargoCode")==null?"":request.getParameter("cargoCode");
%>
</head>
<body>
<form name="cargoProduct" id="cargoProduct" action="../admin/cargoInfo.do" method="post">
货位绑定产品<br/>
请输入需要绑定的产品编号和货位号：<br/>
1.产品编号：<input type="text" id="productCode" name="productCode" size="10" maxlength="20" value="<%=productCode %>"/>
<input type="button" value="查询产品" onclick="javascript:window.open('cargoInfo.do?method=selectProduct')"/><br/>
2.货&nbsp;位&nbsp;&nbsp;号：<input type="text" name="cargoCode" size="15" maxlength="20" value="<%=cargoCode %>"/>
<input type="button" value="查询货位" onclick="javascript:window.open('cargoInfo.do?method=selectCargo')"/><br/>
<input type="hidden" name="method" id="method" value="cargoProduct"/>
<input type="button" onclick="return checkSubmit();" value="我要核实"/>&nbsp;
<input type="submit" value="确认提交"/>
</form>
<script type="text/javascript">
document.getElementById("productCode").focus();
</script>
</body>
</html>