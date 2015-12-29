<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'returnOrderInfo.jsp' starting page</title>
    
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
			
		}
		function addProductCode() {
			var code =  trim(document.getElementById("productCode").value);
			var number = parseInt(document.getElementById("appraisalNumber").value);
			var code1 = trim(document.getElementById("firstProductCode").value);
			if( number >= 0 && (code1 == null || code1 == "") ) {
				
				document.getElementById("firstProductCode").value=code;
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
		var index = 0;
		var totalIndex = 0;
		var lastCreateCode = "";
		var lastAddCode = "";
		function checkCode() {
			var code = document.getElementById("productCode").value;
			var codesCache = document.getElementById("codes").value;
			if(code == null || code == "" ) {
				alert("没有填写产品编号！");
				document.getElementById("productCode").focus();
				return false;
			}
			
			document.getElementById("productCode").value="";
            document.getElementById('productCode').focus();
			$.ajax({
                        type: "GET", //调用方式  post 还是 get
                        url: "<%=request.getContextPath()%>/admin/returnStorageAction.do?method=checkWholeAreaQualify&productCode="+code, //访问的地址
                        dataType: "html", //返回的数据的形式
                        beforeSend: function(XMLHttpRequest) {          //在访问得到返回之前调用的方法
                        },
                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
                        	if( data == '1' ) {
                        		alert('商品条码不存在！');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	} else if (data == '9') {
                        		alert('商品未入退货库或未质检');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	}else if (data == '2') {
                        		alert('可用库存不足');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	} else if ( data == '8') {
                        		alert('该种商品需要修正货位,暂时不能参与上架');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	} else if( data == '3' ){
                        		alert('货位可用库存不足');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	} else if( data == '4' ){
                        		alert('关联的不是整件区货位');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	} else if (data == '5' ) {
                        		alert('该商品未下架，需调往散件区！');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	} else if (data == '6' ) {
                        		alert('该商品可调往散件区，请修正货位!');
                        		document.getElementById("productCode").value="";
                        		document.getElementById('productCode').focus();
                        	} else if ( data == 7) {
                        		var a = "";
                        		$.ajax({
                        			type: "POST", //调用方式  post 还是 get
                        			url: "<%=request.getContextPath()%>/admin/returnStorageAction.do?method=getCodeAndStock&productCode="+code, //访问的地址
                        			dataType: "text", //返回的数据的形式
                        			beforeSend: function(XMLHttpRequest) {          //在访问得到返回之前调用的方法
                        			},
                        			success: function(data, textStatus ) {
                        				a = eval(data);
                        				var currents = a.split(",");
                        				totalIndex ++;
		                        		var codes = codesCache.split(",");
		                        		var tf = 0;
		                        		for(var i =0; i < codes.length; i++ ) {
		                        			if( codes[i] == code ) {
		                        				tf = 1;
		                        			}
		                        		}
		                        		if( tf != 1 ) {
		                        			codesCache += code +  ',';
		                        			document.getElementById("codes").value=codesCache;
		                        			var input = document.createElement("input");
		                        			input.id="number_" + code;
		                        			input.name="number_" + code;
		                        			input.value="1";
		                        			input.type="hidden";
		                        			var temp = document.getElementById("temp");
		                        			temp.appendChild(input);
		                        			lastCreateCode = code;
		                        			lastAddCode = "";
		                        			index++;
		                        			document.getElementById("message").innerHTML="扫描成功！扫描数量1件,当前扫描商品" + currents[0] + "累积扫描该商品1件,可用库存" + currents[1] + "件,总共扫描了" + totalIndex + "件";
		                        		} else {
		                        			var number = document.getElementById("number_" + code);
		                        			var value = parseInt(number.value) + 1;
		                        			if( value > parseInt(currents[1])) {
		                        				alert("扫描失败！累计扫描数量大于可用库存！");
		                        				return;
		                        			}
		                        			number.value = value;
		                        			lastAddCode = code;
		                        			lastCreateCode = "";
		                        			document.getElementById("message").innerHTML="扫描成功！扫描数量1件,当前扫描商品" + currents[0] + "累积扫描该商品" + value + "件,可用库存" + currents[1] + "件,总共扫描了" + totalIndex + "件";
		                        		} 
		                        		document.getElementById("productCode").value="";
		                        		document.getElementById('productCode').focus();
                        			},
			                        complete: function(XMLHttpRequest, textStatus ) {  //访问结束后调用的方法
			                        },
			                        error: function() {          //如果过程中出错了调用的方法
			                            alert( "获取信息出错！" );
			                        }
                 				 });
                        	}
                        },
                        
                        complete: function(XMLHttpRequest, textStatus ) {  //访问结束后调用的方法
                        },
                        error: function() {          //如果过程中出错了调用的方法
                              alert( "验证出错" );
                        }
                  });
                  return false;
		}
		
		function generateWholeAreaCargoOperation() {
			var url = "<%=request.getContextPath()%>/admin/returnStorageAction.do?method=generateWholeAreaCargoOperation";
			temp.submit();
			document.getElementById("generateConfirm").disabled="true";
		}
		
		
		function resetPage() {
			window.location='<%=request.getContextPath()%>/admin/cargo/generateWholeAreaCargoOperation.jsp';
		}
		
		function jumpToProductCode() {
			if(document.getElementById("buyStockinCode").value != null && document.getElementById("buyStockinCode").value != "") {
				document.getElementById("productCode").focus();
			}
			return false;
		}
		function clear() {
			document.getElementById("codes").value = "";
		}
		//删除上一次录入结果的方法 
		function deleteLastOne() {
			if( lastCreateCode == "" && lastAddCode == "" ) {
				alert("你还没有扫描成功任何商品，或已经取消过一次了。");
				return;
			}
			if( lastCreateCode != "" ) {
				var codesCache = document.getElementById("codes").value;
				var codes = codesCache.split(",");
				var afterCodes = "";
				for(var i =0; i < codes.length; i++ ) {
	        			if( codes[i] == lastCreateCode ) {
	        				var tempOb = document.getElementById("number_" + lastCreateCode);
	        				tempOb.value="0";
	        				tempOb.id="xxx_"+lastCreateCode;
	        				tempOb.name="xxx_" + lastCreateCode;
	        				continue;
	        			}
	        			if( codes[i] == "" ) {
	        				continue;
	        			}
	        			afterCodes += codes[i] + ",";
	        	}
	        	document.getElementById("codes").value = afterCodes;
	        	totalIndex--;
	        	lastCreateCode = "";
	        	lastAddCode = "";
	        	document.getElementById("message").innerHTML="删除上一次扫描商品成功";
			}
			if( lastAddCode != "" ) {
				var valueStr = document.getElementById("number_" + lastAddCode).value;
				var value = parseInt(valueStr) - 1;
				document.getElementById("number_" + lastAddCode).value = value;	
				totalIndex--;
				lastAddCode ="";
				document.getElementById("message").innerHTML="删除上一次扫描商品成功";			
			}
		}
		
		function disp_prompt() {
		    var deleteCode = prompt("请输入商品条码","")
		    if (deleteCode != null && deleteCode != "") {
		      var codesCache = document.getElementById("codes").value;
				var codes = codesCache.split(",");
				var afterCodes = "";
				var has = 0;
				for(var i =0; i < codes.length; i++ ) {
	        			if( codes[i] == deleteCode ) {
	        				var tempOb = document.getElementById("number_" + deleteCode);
	        				totalIndex -= parseInt(tempOb.value);
	        				tempOb.value="0";
	        				tempOb.id="xxx_" + deleteCode;
	        				tempOb.name="xxx_" + deleteCode;
	        				has = 1;
	        				continue;
	        			}
	        			if( codes[i] == "" ) {
	        				continue;
	        			}
	        			afterCodes += codes[i] + ",";
	        	}
	        	document.getElementById("codes").value = afterCodes;
	        	lastCreateCode="";
	        	lastAddCode="";
	        	if( has == 0 ) {
	        		alert("没有在已扫描的商品中找到要删除的商品");
	        	} else if (has == 1) {
	        		document.getElementById("message").innerHTML="删除商品成功";
	        	}
		    } else if (deleteCode == "") {
		    	alert("没有输入商品条码");
		    }
		}
	</script>
  </head>
<body onload="javascript:document.getElementById('productCode').focus();clear();document.getElementById('generateConfirm').enabled='true';">
<div style="margin-left:15px;margin-top:15px;">
  	<table width="400px"><tr align="center"><td><h2>&nbsp;生成退货上架单(对整件区):</h2></td></tr></table>
   	<fieldset style="width:400px;">
   		<div style="background-color:#FFFF93;width:400px;height:280px;border-style:solid;border-width:1px;border-color:#000000;">
   		<div style="margin-left:0px;">
   			<br>
   			<table border="0" cellspacing="12" width="400px">
   			<tr>
   			<td>
   			</td>
   				<td colspan="3" align="center">
   				<div style="background-color:#ECECFF;width:320px;height:50px;border-style:solid;border-width:1px;border-color:#000000;">
   					<span id="message">还没有扫描任何一件商品</span>
   				</div>
   				</td>
   			</tr>
   			<tr>
   			<td>
   			</td>
   			<form action="" method="post" onsubmit="return checkCode();" >
   				<td colspan="3" align="center" >
   					<input type="text" name="productCode" id="productCode" onblur="blurProductCode();" onfocus="focusProductCode();"/>
   				</td>
   			</form>
   			</tr>
   			<tr>
   				<td align="center" colspan="3">
   				<input type="button" value="    重置     " onclick="resetPage();" />
   				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   				<input type="button" value="  删除指定商品 "  onclick="disp_prompt();" />
   				</td>
   			</tr>
   			<tr>
   				<td align="center" colspan="3">
   				<input type="button" value="    取消     " onclick="deleteLastOne();" />
   				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   				<input type="submit" value="生成退货上架单 " id="generateConfirm" onclick="generateWholeAreaCargoOperation();"/>
   				</td>
   			</tr>
   			</div>
   		</div>
   		<form id="temp" action="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=generateWholeAreaCargoOperation" method="post" >
   			<input type="hidden" value="" name="codes" id="codes" />
   		</form>
   		<br>
   			</table>
   		<div style="margin-left:12px;">
   		操作说明：<br>
   		1.请扫描商品条码，并点击"生成退货上架单"按钮生成退货上架单;</br>
   		2.每单扫描商品请勿多于十件;<br/>
   		3.点击取消，将取消上次操作;<br/>
   		4.点击重置，将取消之前所有操作.
   		</div>
   	</fieldset>
   
   	</div>

</body>
</html>
