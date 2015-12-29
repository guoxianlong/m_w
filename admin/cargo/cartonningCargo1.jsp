<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html>
<head>
<title>装箱单关联货位</title>
<script type="text/javascript">
function inputCode(){
	var code=document.getElementById("testCode").value;
	document.getElementById("code").value=code;
	document.getElementById("cargoWholeCode").focus();
	return false;
}
function clearS(){
	
	document.getElementById("testCode").value="";
	document.getElementById("cargoWholeCode").value="";
}
function validateSumit(){
	document.getElementById('code').value=document.getElementById('testCode').value;
	document.cartonningForm.submit();
	clearS();
}
<%if(request.getAttribute("complete")!=null&&request.getAttribute("complete").toString().equals("1")){%>
	window.close();
<%}%>
</script>
</head>
<body onLoad="document.getElementById('testCode').focus();">
<h2>装箱单关联货位</h2>
<h4>请输入需要关联的装箱单号和货位号：</h4>
	
	<form action="" onSubmit="return inputCode();" method="post">
	装箱单号:
		<input type="text" id="testCode" name="testCode">
	</form>
<form action="cartonningInfoAction.do?method=cartonningCargo1" name="cartonningForm" method="post" >
货 位 号:
  <input type="text" name="cargoWholeCode" id="cargoWholeCode"/>
<input type="hidden" id="code" name="code" value='<%=request.getParameter("code")==null?"":request.getParameter("code") %>'/><br/><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" onclick="validateSumit()" value="确认提交">
<input type="button" value="取消" onClick="clearS()" />
</form>
</body>
</html>