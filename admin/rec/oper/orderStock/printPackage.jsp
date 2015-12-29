<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%
	String orderCode = null;
	if(request.getParameter("orderCode")!=null){
		orderCode = request.getParameter("orderCode");
	}
%>
<script type="text/javascript">
function check(){
	<%if(orderCode!=null){%>
		document.getElementById('weight').focus();
	<%}else{%>
		document.getElementById('orderCode').focus();
	<%}%>
}

function selectInput(){
	if(event.keyCode == 13){
		flag=1;
		document.getElementById('weight').focus();
		return false;
	}
	return true;

}
function checkSubmit(){
	if(document.getElementById('weight').value!=''&& document.getElementById('orderCode').value!='' && flag==1){
		return true;
	}
	return false;
}
function test(){
	var weight=aiPlug.GetData();
	if(weight != ""){

		document.getElementById("weight").value=weight;
		
		if(checkSubmit())
		{
			clearInterval(tr);
			form1.submit();
			
		}
	}
}
function checkLeave(){
	aiPlug.DisposeRes();
}
function change(){
	flag=1;
	if(checkSubmit())
	{
		clearInterval(tr);
		form1.submit();
		
	}
}
</script>
</head>
<body onunload="checkLeave()">
补打包裹单：<br/>
<form action="${pageContext.request.contextPath}/ScanOrderStockController/printOrderStockPackage.mmx" method="post" onsubmit="return checkSubmit();" id="form1">
	发货单号：
	<input type="text" id="orderCode" name="orderCode" 
		<%if(orderCode!=null){ %>value='<%=orderCode%>' readonly="readonly"<%} %> 
		onkeypress="return selectInput();"/><br/>
	包裹重量：
	<input type="text" id="weight" name="weight"/><br/>
	<input type="hidden" name="scanFlag" value="5">
	<input type="hidden" name="orderstock" value="orderStock">
	<input type="button" value="提交" onclick="change()">
</form>
</body>
<script type="text/javascript">
var flag =0;
<%if(orderCode!=null){ %>
	flag=1
<%}%>
var aiPlug = new ActiveXObject("MMb_JEC.Balance");
aiPlug.Init();
var tr = setInterval("test();", 60);
if(document.getElementById('orderCode').value==''){
	   document.getElementById('orderCode').focus();  
}
</script>
</html>