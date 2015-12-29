<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voProductLine"%>
<%
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>买卖宝后台</title>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/js/jquery.js"></script>
		<link href="<%=request.getContextPath()%>/css/global.css"
			rel="stylesheet" type="text/css">
	</head>
	<body>
	<script type="text/javascript">
 		function checkAll(name) {     
		    var checkChagen =document.getElementsByName(name);
		    var cargoProducId = document.getElementsByName('cargoProducStockId');
		    for(var i=0;i<cargoProducId.length;i++){
		    	cargoProducId[i].checked =checkChagen[0].checked ;
		    }
		}
 		
 		function check(){
			 var cargoProducId = document.getElementsByName('cargoProducStockId');
			 for(var i=0;i<cargoProducId.length;i++){
		    	if(cargoProducId[i].checked ==true){
		    		return true;
		    	}
		    }
		    alert("请选择货位，再生成补货单");
		    return false;
		}

 		function init(){
			var radio = document.getElementsByName('type');
 			for(var i = 0; i < radio.length; i++){
 				if(radio[i].checked){
 					colorChangeRadio(radio[i],'red');
 				}
 			}
 			
 			radio = document.getElementsByName('stockType');
 			for(var i = 0; i < radio.length; i++){
 				if(radio[i].checked){
 					colorChangeRadio(radio[i],'red');
 				}
 			}
 			
		}
 		
 		function changeArea(){
 			var value = 0;
 			var radio = document.getElementsByName('stockType');
 			for(var i = 0; i < radio.length; i++){
 				if(radio[i].checked){
 					value = radio[i].value;
 				}
 			}
 			var value2 = 0;
 			var radio2 = document.getElementsByName('storageId');
 			for(var i = 0; i < radio2.length; i++){
 				if(radio2[i].checked){
 					value2 = radio2[i].value;  
 					colorChangeRadio(radio2[i],'red');
 				}
 			}
 			$.ajax({
 				type: "GET",
 				url: "cargo/autoCargoInventoryInfo.jsp?action=area&stockType="+value+"&storageId="+value2,
 				cache: false,
 				dataType: "html",
 				data: {type: "1"},
 				success: function(msg, reqStatus){
 					document.getElementById("area").innerHTML = msg;
 				}
 			});
 		}
 		function changeStorage(){
 			var value = 0;
 			var radio = document.getElementsByName('stockType');
 			for(var i = 0; i < radio.length; i++){
 				if(radio[i].checked){
 					value = radio[i].value;
 					colorChangeRadio(radio[i],'red');
 				}
 			}
 			$.ajax({
 				type: "GET",
 				url: "cargo/autoCargoInventoryInfo.jsp?action=storage&stockType="+value,
 				cache: false,
 				dataType: "html",
 				data: {type: "1"},
 				success: function(msg, reqStatus){
 					document.getElementById("storage").innerHTML = msg;
 					changeArea();
 				}
 			});
 		}
 		
 	</script>
		<form action="cargoInventory.do" method="post" name="searchAppForm">
			<input type="hidden" name="method" value="addCargoInventory"/>
			<input type="hidden" name="action" value="add"/>
			<fieldset style="width:800px;">
				<legend>添加盘点作业单</legend>
				<font color="red">*</font>作业类型：
				<input type="radio" name="type" value="0" checked="checked"/><span>大盘</span>&nbsp;&nbsp;
				<input type="radio" name="type" value="1"/><span>动碰货位盘点</span>&nbsp;&nbsp;
				<input type="radio" name="type" value="2"/><span>随机盘点</span>&nbsp;&nbsp;
				<div id="stockType1">
				<font color="red">*</font>库存类型：
				<%
					Iterator iter = ProductStockBean.stockTypeMap.entrySet().iterator();
					while(iter.hasNext()){
						Map.Entry entry = (Map.Entry)iter.next();
						Integer value = (Integer)entry.getKey();
						String stockType = (String)entry.getValue();
				%>
					<input type="radio" name="stockType" id="stockType" value="<%=value.intValue() %>" onClick="changeStorage();"<%if(value.intValue() == 0){ %> checked="checked"<%} %>/><span><%=stockType %></span>&nbsp;&nbsp;	
				<%} %>
				<script type="text/javascript">changeStorage()</script>
				</div>
				<br/>
				<div id="storage">
				</div>
				<br/>
				<div id="area">
				</div>
				<br/>
				&nbsp;&nbsp;备注：
				<textarea rows="3" cols="30" name="remark"></textarea>
				（255个字以内）
				<br/>
				&nbsp;&nbsp;<input type="submit" value="提交"/>
			</fieldset>
		</form>
		<script type="text/javascript">init();</script>
	</body>
</html>