<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="mmb.stock.stat.*"%>
<%
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
%>
<html>
  <head>
    <title>添加收货暂存号</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../js/jquery.js"></script>
	<script type="text/javascript">

		function checkText(){
			var tepNum = document.getElementById("tempNumId").value;
			if(tepNum==""){
				alert("请输入暂存号！");
				document.getElementById("tempNumId").focus;
				return false;
			}
			var seobj = document.getElementById("wareArea");
			var wareArea = seobj.options[seobj.selectedIndex].value;
			$.post("../checkStockinMissionAction.do?method=addTemproraryNum",{tempNum:tepNum, wareArea:wareArea},function(result){
					alert(result);
					document.getElementById("tempNumId").value="";
					document.getElementById("tempNumId").focus;
				}
			);
		}
	</script>
</head>
<body>
<div style="margin:10px;border-style:solid;border-color:#000000;border-width:1px;">
<h5 style="margin-left: 10px;">添加收货暂存号</h5>
<div style="margin-left: 10px;">
	<form>	
		收货暂存号：<input style="" type="text" name="tempNum" id="tempNumId"/>&nbsp;&nbsp;
		地区：<%= wareAreaSelectLable %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input style="" value="确认" type="button" id="Button1" onclick="checkText();"></input>
	</form>
</div>
</div>
</body>
</html>
