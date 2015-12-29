<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="adultadmin.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<%=request.getContextPath() %>/admin/js/autoHideAlert.js"></script>
<title>Insert title here</title>
<%
String soundSrcSJYC = Constants.WARE_SOUND_SJYC;
	String orderCode = null;
	if(request.getParameter("orderCode")!=null){
		orderCode = request.getParameter("orderCode");
	}
	String tip = "";
	if (request.getAttribute("tip")!= null) {
	  tip = request.getAttribute("tip") + ""; 
	}
%>
<script type="text/javascript">
function  Play(sound) {
	 if(navigator.appName == "Microsoft Internet Explorer")
	{
 		 var snd = document.createElement("bgsound");
       document.getElementsByTagName("body")[0].appendChild(snd);
       snd.src = sound;
	}
	else
	{
      var obj = document.createElement("object");
      obj.width="0px";
      obj.height="0px";
      obj.type = "audio/x-wav";
      obj.data = sound;            
      var body = document.getElementsByTagName("body")[0];
      body.appendChild(obj);
	}
}
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
<div></div>
<center>
<div style="border: 1px solid;width:500px;height:200px" align="center">
<div style="width:320px;height:200px" align="left">
<form action="<%=request.getContextPath() %>/admin/scanOrderStock.do" method="post" onsubmit="return checkSubmit();" id="form1" >
<br/>补打包裹单：<br/><br/>
	发货单号：
	<input type="text" id="orderCode" name="orderCode" 
		<%if(orderCode!=null){ %>value='<%=orderCode%>' readonly="readonly"<%} %> 
		onkeypress="return selectInput();"/><br/>
	包裹重量：
	<input type="text" id="weight" name="weight"/><br/>
	<br/>
	<input type="hidden" name="scanFlag" value="5">
	<input type="hidden" name="orderstock" value="orderStock">
	<input type="button" value="提交" onclick="change()">
</form>
</div>
</div>
</center>
</body>
<script type="text/javascript">
var flag =0;
<%if(orderCode!=null){ %>
	flag=1
<%}%>
if ("<%=tip%>" != "") {
	document.body.style.background="#ff0000";
	customPRINTAlert("<%=tip%>");
}
var aiPlug = new ActiveXObject("MMb_JEC.Balance");
aiPlug.Init();
var tr = setInterval("test();", 60);
if(document.getElementById('orderCode').value==''){
	   document.getElementById('orderCode').focus();  
}
function errorPlay() {
	Play('<%= soundSrcSJYC%>');
}
function closeerroralert() {
	closeprintwin();
	document.getElementById('weight').focus();  
}

</script>
</html>