<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%
	String redirect = StringUtil.convertNull(request.getParameter("redirect"));
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>质检入库不合格</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
	jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
		function focusProductCode(){
			var productCode=document.getElementById("productCode").value;
			productCode = productCode.trim();
			if(productCode=="商品条码扫描"){
				document.getElementById("productCode").value="";
				document.getElementById("productCode").style.color="#000000";
			}
		}
		function blurProductCode(){
			var productCode=document.getElementById("productCode").value;
			productCode = productCode.trim();
			if(productCode==""){
				document.getElementById("productCode").value="商品条码扫描";
				document.getElementById("productCode").style.color="#cccccc";
			} else {
				document.getElementById("productCode").style.color="#000000";
			}
		}
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		function check(){
	
			var billCode = document.getElementById("buyStockCode").value;
			billCode = billCode.trim();
			var numbers = document.getElementById("appraisalNumber").value;
			var number = document.getElementById("appraisalNumber").value;
			var fCode = document.getElementById("firstProductCode").value;
			fCode = fCode.trim();
			var temCode = document.getElementById("productCode").value;
			temCode = temCode.trim();
			var reasonEle = document.getElementById("reasonForUnqualify");
			if( billCode == null || billCode == "" ) {
				$.messager.alert("提示","请输入预计单号!");
				document.getElementById("buyStockCode").focus();
				return;
			} else {
				document.getElementById("buyStockCode2").value=billCode;
			} 
			if( number == "0" ) {
				$.messager.alert("提示","请确保至少有一个质检量!");
				document.getElementById("productCode").focus();
				return;
			} else if(fCode == "" && temCode != "" && temCode != "商品条码扫描") {
				
				document.getElementById("productCode2").value= temCode;
				document.getElementById("appraisalNumber2").value=numbers;
			} else {
				document.getElementById("productCode2").value=fCode;
				document.getElementById("appraisalNumber2").value=numbers;
			}
			
			var submitCode = document.getElementById("productCode2").value;
			if( submitCode == null || submitCode == "" ) {
				$.messager.alert("提示","商品编号必须要填写！");
				document.getElementById("productCode").focus();
				return;
			}
			
			var reason = $("#reasonForUnqualify").val();
			reason = reason.trim();
			if( reason == "") {
				$.messager.alert("提示","不合格原因为必填!");
				document.getElementById("reasonForUnqualify").focus();
				return;
			} else if( reason.length > 200) {
				$.messager.alert("提示","不合格原因字数需要小于200字!");
				document.getElementById("reasonForUnqualify").focus();
				return;
			}
			
			//添加 访问 确认质检该数量后 是否有完成订单的需要
			$.ajax({
				type:"POST",
				url:"<%= request.getContextPath()%>/admin/judgeAutoComplete.mmx?buyStockCode="+billCode+"&appraisalNumber="+numbers+"&productCode="+submitCode,
				dataType: "text",
				success: function(data, textStatus) {
					if ( data == null || data == "" ) {
						$.messager.alert("提示","查询对应的预计单和 订单状态是发生错误！");
					} else if( data == "0" ) {
							openConfirm();
					} else if ( data == "1" ) {
						 document.getElementById("submitConfirm").disabled="true";
   						 //document.getElementById("form1").submit();
   						 goSubmit();
					}  else {
						alert(data);
					}
				},
				error: function() {
					$.messager.alert("提示","验证订单完成状态出错！");
					result = false;
				}
			});
			//alert("over");
		}
		
		function addProductCode() {
			var code =  $("#productCode").val();
			code = code.trim();
			if( code == null || code == "" ) {
				return false;
			}
			var pattern = /^[0-9]{1,9}$/;
			var pattern2 = /^[0-9]{1,}$/;
   			var numberTest = $("#appraisalNumber").val();
   			if( numberTest != "" ) {
	   			if(pattern.exec(numberTest)) {
	   			} else if (pattern2.exec(numberTest)) {
	   				$("#appraisalNumber").val("");
	   				$("#appraisalNumber").focus();
	   				$.messager.alert("提示","请不要输入大于9位的数字!");
		   		 	return false;
		   		} else {
		   			$("#appraisalNumber").val("");
		   			$("#appraisalNumber").focus();
		   			$.messager.alert("提示","请填入整数！！");
	    			return false;
	   			}
   			}
			var number = parseInt(document.getElementById("appraisalNumber").value);
			var code1 = document.getElementById("firstProductCode").value;
			code1 = code1.trim();
			if( number >= 0 && (code1 == null || code1 == "") ) {
				
				document.getElementById("firstProductCode").value=code;
				$("#currentCode").html("商品： " + code);
				var temp = number + 1;
				document.getElementById("appraisalNumber").value=temp;
				document.getElementById("productCode").value="";
				document.getElementById("productCode").focus();
			} else {
				if( code == code1 && code1 != "") {
					var temp = number + 1;
					document.getElementById("appraisalNumber").value=temp;
					document.getElementById("productCode").value="";
					document.getElementById("productCode").focus();
				} else {
					alert("前后扫描的产品条码不一致");
					document.getElementById("productCode").value="";
					document.getElementById("productCode").focus();
				}
			}
			return false;
		}
		function resetPage() {
			window.location='<%=request.getContextPath()%>/admin/rec/oper/checkStockinMission/appraisalStorageResultInput.jsp';
		}
		
		function jumpToProductCode() {
			if(document.getElementById("buyStockCode").value != null && document.getElementById("buyStockCode").value != "") {
				document.getElementById("productCode").focus();
			}
			return false;
		}
		
		
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}
   		
   		function showsome() {
   			//alert(x);
					
   		}
   		
   		function confirmComplete() {
   			document.getElementById("complete").value="0";
   			document.getElementById("askComplete").style.display="none";
   			document.getElementById("submitConfirm").disabled="true";
   			goSubmit();
   			return;
   		}
   		
   		function confirmNotComplete() {
   			document.getElementById("complete").value="1";
   			document.getElementById("askComplete").style.display="none";
   			document.getElementById("submitConfirm").disabled="true";
   			goSubmit();
   			return;
   		}
   		
   		function openConfirm() {
   			document.getElementById("askComplete").style.display="block";
   		}
   		
   		$.extend($.fn.validatebox.defaults.rules, {  
   	        equals: {  
   	            validator: function(value,param){  
   	                return value == param[0];  
   	            },  
   	            message: '所填内容与需要内容不匹配！'  
   	        },
   	        checkName: {
   	        	validator: function (value) {
   	        		var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
   	 				var rs = ""; 
   	 				var s = value;
   	 				for (var i = 0; i < s.length; i++) { 
   	 					if( pattern.exec(s.substr(i,1))) {
   	 						document.getElementById("name").focus();
   	 						return false;
   	 					}
   	 				}
   	 				return true;
   	        	},
   	        	message:'请勿输入文本外的字符!'
   	        },
   	        checkNumber: {
   	        	validator: function (value) {
   	        		var pattern = /^[0-9]{1,9}$/;
   	    			var pattern2 = /^[0-9]{1,}$/;
   	    			var number = value;
   	    			if( number != "" ) {
	   	 	   			if(pattern.exec(number)) {
	   	 	   				return true;
	   	 	   			} else if (pattern2.exec(number)) {
	   	 		   		 	return false;
	   	 		   		} else {
	   	 	    			return false;
	   	 	   			}
   	    			} else {
   	    				return false;
   	    			}
   	        	},
   	        	message:'请填入大于0位小于9位的数字！'
   	        }
   	    });
   		
   		function goSubmit() {
			jQuery.messager.progress();	// display the progress bar
			$('#ff').form('submit', {
				url: '<%= request.getContextPath()%>/admin/appraisalStorageUnqualiyInput.mmx',
				onSubmit: function(){
					var isValid = $(this).form('validate');
					if (!isValid){
						jQuery.messager.progress('close');	// hide progress bar while the form is invalid
						return false;
					}
					jQuery.messager.progress('close');
					return true;
				},
				success: function(){
					jQuery.messager.progress('close');	// hide progress bar while submit successfully
					resetAll();
				}
			});
		}
   		function resetAll() {
   			$("#buyStockCode").val("");
   			$("#productCode").val("");
   			$("#firstProductCode").val("");
   			$("#appraisalNumber").val("0");
   			$("#currentCode").html("");
   			$("#reasonForUnqualify").val("");
   			$("#buyStockCode2").val("");
   			$("#appraisalNumber2").val("");
   			$("#productCode2").val("");
   			$("#complete").val("1");
   			document.getElementById('buyStockCode').focus();
   			blurProductCode();
   			$('#submitConfirm').removeAttr('disabled');
   		}
   		
	</script>

  </head>
<body onload="resetAll();">
   <div style="left:270px;top:220px;width:280px;height:150px;position:absolute;background-color:#d0d0d0;border-style:solid;border-width:1px;border-color:#000000;padding:3px;display:none;" id="askComplete">
   		<div style="width:272px;height:142px;background-color:#FFFFFF;;border-style:solid;border-width:1px;border-color:#000000;padding:3px;">
   		<div align="center" style="margin-top:40px;"><b>请确认是否存在其他未完成的入库单！</b></div>
   		
   		<div style="margin-top:30px;margin-left:70px;">
   		<button onclick="confirmNotComplete();">&nbsp;有&nbsp;</button>
   		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   		<button onclick="confirmComplete();">&nbsp;无&nbsp;</button>
   		</div>
   		</div>
   </div>
   
   <center>
<h2 >质检结果录入</h2>
<div style="width:600px;height:350px;padding:10px;" class="easyui-panel" data-options="cache:false" title="质检结果录入">
<center>

   	<table border="0" cellspacing="12">
   			<tr>
   			<td>
   				<label for="name">预计单号：</label></b>
   			</td>
   			<form id="form1" action="" method="post" onsubmit="return jumpToProductCode();" >
   				<td colspan="3">
   					<input class="easyui-validatebox" name="buyStockCode" id="buyStockCode" data-options="required:true" value=""/>
   				</td>
   			</form>
   			</tr>
   			<tr>
   				<form id="form2" action="" method="post" onsubmit="return addProductCode();">
   				<td><label for="name">商品条码：</label></td>
   				<td><input type="text" class="easyui-validatebox" id="productCode" name="productCode" onblur="blurProductCode();" onfocus="focusProductCode();" /><span id="currentCode" style="color:#d0d0d0;" align="center"></span></td>
   				</form>
   				
   				<input type="hidden" id="firstProductCode" name="firstProductCode" />
   				<td align="right"><label for="name">数&nbsp;&nbsp;&nbsp;&nbsp;量：</label></td>
   				<td align="left"><input class="easyui-validatebox" data-options="required:true" type="text" size="3" id="appraisalNumber" name="appraisalNumber" value="0" onblur="checkNumber(this);" />
   				</td>
   			</tr>
   			
   			<tr>
   				<td colspan="4" >
					<label for="name">质检结果：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
					<select>
						<option>不合格</option>
					</select>
   				</td>
   			</tr>
   			<!--  <form id="form1" action="<%= request.getContextPath()%>/admin/checkStockinMissionAction.do?method=appraisalStorageUnqualiyInput" method="post" > -->
   			<form id="ff" method="post">
   			<tr>
   				<td ><label for="name">不合格原因说明：</label></td>
   				<td colspan="3"><textarea class="easyui-validatebox" data-options="required:true" name="reasonForUnqualify" id="reasonForUnqualify" rows="5" cols="30"></textarea><span style="color:red">*必填项</span></td>
   			</tr>
   			<tr>
   				<td></td>
   				<td align="left" >
   				<a href="javascript:check();" id="submitConfirm" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交质检结果</a>
   				</td>
   				<td align="center" colspan="2">
   				<a href="javascript:resetPage();" class="easyui-linkbutton" data-options="iconCls:'icon-remove'">取消</a>
   				</td>
   			</tr>
   			<input type="hidden" name="buyStockCode2" id="buyStockCode2" />
   			<input type="hidden" name="appraisalNumber2" id="appraisalNumber2" />
   			<input type="hidden" name="productCode2" id="productCode2" />
   			<input type="hidden" name="complete" id="complete" value="1" />
   			</table>
   			</form>
</center>
<div style="margin-left:12px;">
   		操作说明：<br/>
   		1.扫描商品条码，并输入商品数量，点击“提交质检结果”按钮，商品入待验库;</br>
   		2.生成并打印一张至返厂库的调拨单。
   		</div>
</div>
</center>
</body>
</html>
