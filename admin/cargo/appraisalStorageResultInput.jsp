<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
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
	<script type="text/javascript">
		function focusProductCode(){
			var productCode=trim(document.getElementById("productCode").value);
			if(productCode=="商品条码扫描"){
				document.getElementById("productCode").value="";
				document.getElementById("productCode").style.color="#000000";
			}
		}
		function blurProductCode(){
			var productCode=trim(document.getElementById("productCode").value);
			if(productCode==""){
				document.getElementById("productCode").value="商品条码扫描";
				document.getElementById("productCode").style.color="#cccccc";
			} else {
				document.getElementById("productCode").style.color="#000000";
			}
		}
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		function check(){
	
			var billCode = trim(document.getElementById("buyStockCode").value);
			var numbers = document.getElementById("appraisalNumber").value;
			var number = document.getElementById("appraisalNumber").value;
			var fCode = trim(document.getElementById("firstProductCode").value);
			var reasonEle = document.getElementById("reasonForUnqualify");
			if( billCode == null || billCode == "" ) {
				alert("请输入预计单号!");
				document.getElementById("buyStockCode").focus();
				return false;
			} else {
				document.getElementById("buyStockCode2").value=billCode;
			} 
			if( number == "0" ) {
				alert("请确保至少有一个质检量!");
				document.getElementById("productCode").focus();
				return false;
			} else if(fCode == "" && trim(document.getElementById("productCode").value) != "" && trim(document.getElementById("productCode").value) != "商品条码扫描") {
				document.getElementById("productCode2").value=trim(document.getElementById("productCode").value);
				document.getElementById("appraisalNumber2").value=numbers;
			} else {
				document.getElementById("productCode2").value=fCode;
				document.getElementById("appraisalNumber2").value=numbers;
			}
			
			var submitCode = document.getElementById("productCode2").value;
			if( submitCode == null || submitCode == "" ) {
				alert("商品编号必须要填写！");
				document.getElementById("productCode").focus();
				return false;
			}
			
			var reason = $("#reasonForUnqualify").val();
			reason = reason.trim();
			if( reason == "") {
				alert("不合格原因为必填!");
				document.getElementById("reasonForUnqualify").focus();
				return false;
			} else if( reason.length > 200) {
				alert("不合格原因字数需要小于200字!");
				document.getElementById("reasonForUnqualify").focus();
				return false;
			}
			
			//添加 访问 确认质检该数量后 是否有完成订单的需要
			$.ajax({
				type:"POST",
				url:"<%= request.getContextPath()%>/admin/checkStockinMissionAction.do?method=judgeAutoComplete&buyStockCode="+billCode+"&appraisalNumber="+numbers+"&productCode="+submitCode,
				dataType: "text",
				success: function(data, textStatus) {
					if ( data == null || data == "" ) {
						alert("查询对应的预计单和 订单状态是发生错误！");
					} else if( data == "0" ) {
							openConfirm();
					} else if ( data == "1" ) {
						 document.getElementById("submitConfirm").disabled="true";
   						 document.getElementById("form1").submit();
					}  else {
						alert(data);
					}
				},
				error: function() {
					alert("验证订单完成状态出错！");
					result = false;
				}
			});
			//alert("over");
		}
		
		function addProductCode() {
			var code =  trim(document.getElementById("productCode").value);
			var numberObj = document.getElementById("appraisalNumber");
			if( code == null || code == "" ) {
				return false;
			}
			var pattern = /^[0-9]{1,9}$/;
			var pattern2 = /^[0-9]{1,}$/;
   			var numberTest = numberObj.value;
   			if( numberTest != "" ) {
	   			if(pattern.exec(numberTest)) {
	    
	   			} else if (pattern2.exec(numberTest)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
		   			alert(false);
	   				numberObj.value="";
	   				numberObj.focus();
	    			alert("请填入整数！！");
	    			return false;
	   			}
   			}
			var number = parseInt(document.getElementById("appraisalNumber").value);
			var code1 = trim(document.getElementById("firstProductCode").value);
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
			window.location='<%=request.getContextPath()%>/admin/cargo/appraisalStorageResultInput.jsp';
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
   			document.getElementById("form1").submit();
   			return true;
   		}
   		
   		function confirmNotComplete() {
   			document.getElementById("complete").value="1";
   			document.getElementById("askComplete").style.display="none";
   			document.getElementById("submitConfirm").disabled="true";
   			document.getElementById("form1").submit();
   			return true;
   		}
   		
   		function openConfirm() {
   			document.getElementById("askComplete").style.display="block";
   		}
   				
	</script>

  </head>
<body onload="document.getElementById('forecastBillCode').focus();blurProductCode();$('#submitConfirm').removeAttr('disabled')">
  	<div style="margin-left:15px;margin-top:15px;">

  	<table width="500px"><tr align="center"><td><h2>&nbsp;质检结果录入</h2></td></tr></table>
   	<fieldset style="width:500px;">
   		<div style="background-color:#FFFF93;width:500px;height:300px;border-style:solid;border-width:1px;border-color:#000000;">
   		<div style="margin-left:12px;">
   			<br>
   			
   			
   			<table border="0" cellspacing="12">
   			<tr>
   			<td>
   				<b>预计单号：</b>
   			</td>
   			<form action="" method="post" onsubmit="return jumpToProductCode();" >
   				<td colspan="3">
   					<input name="buyStockCode" id="buyStockCode" value=""/>
   				</td>
   			</form>
   			</tr>
   			<tr>
   				<form action="" method="post" onsubmit="return addProductCode();">
   				<td>商品条码：</td>
   				<td><input type="text" id="productCode" name="productCode" onblur="blurProductCode();" onfocus="focusProductCode();" /><span id="currentCode" style="color:#d0d0d0;" align="center"></span></td>
   				</form>
   				
   				<input type="hidden" id="firstProductCode" name="firstProductCode" />
   				<td align="right">数&nbsp;&nbsp;&nbsp;&nbsp;量：</td>
   				<td align="left"><input type="text" size="3" id="appraisalNumber" name="appraisalNumber" value="0" onblur="checkNumber(this);" />
   				</td>
   			</tr>
   			
   			<tr>
   				<td colspan="4" >
					质检结果：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<select>
						<option>不合格</option>
					</select>
   				</td>
   			</tr>
   			<form id="form1" action="<%=request.getContextPath()%>/admin/checkStockinMissionAction.do?method=appraisalStorageUnqualiyInput" method="post" >
   			<tr>
   				<td >不合格原因说明：</td>
   				<td colspan="3"><textarea name="reasonForUnqualify" id="reasonForUnqualify" rows="5" cols="30"></textarea><span style="color:red">*必填项</span></td>
   			</tr>
   			<tr>
   				<td></td>
   				<td align="left" ><input type="button" id="submitConfirm" value="    提交质检结果    " onclick="check();" /></td>
   				<td align="center" colspan="2"><input type="button" value="    取消     " onclick="resetPage();" /></td>
   			</tr>
   			<input type="hidden" name="buyStockCode2" id="buyStockCode2" />
   			<input type="hidden" name="appraisalNumber2" id="appraisalNumber2" />
   			<input type="hidden" name="productCode2" id="productCode2" />
   			<input type="hidden" name="complete" id="complete" value="1" />
   			</table>
   			</form>
   			</div>
   		</div>
   		<br>
   		<div style="margin-left:12px;">
   		操作说明：<br/>
   		1.扫描商品条码，并输入商品数量，点击“提交质检结果”按钮，商品入待验库;</br>
   		2.生成并打印一张至返厂库的调拨单。
   		</div>
   	</fieldset>
   	</div>
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
  </body>
</html>
