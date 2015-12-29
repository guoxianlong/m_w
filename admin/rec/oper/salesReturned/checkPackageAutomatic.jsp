<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>检查包裹</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	productCodeFocus();
	
	$("#productCode").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
        	$("#confirm").click(); 
        	return false;
        }
    });
});
String.prototype.replaceAll = function (s1, s2) { 
	return this.replace(new RegExp(s1,"gm"),s2);
}
function productCodeFocus() {
	$('#productCode').focus();
	clearPackageNoAlert();
};

function clearPackageNoAlert() {
	$.ajax({
            type: "GET", //调用方式  post 还是 get
            url: "<%=request.getContextPath()%>/SalesReturnController/clearPackageCache.mmx",
            cache : false,
            dataType: "text", //返回的数据的形式
            success: function(data) { 
            	$("#OBIB").html("");
            },
            error: function() {          //如果过程中出错了调用的方法
            }
      });
}

function clearPackage() {
	$.ajax({
          type: "GET", //调用方式  post 还是 get
          url: "<%=request.getContextPath()%>/SalesReturnController/clearPackageCache.mmx",
          cache : false,
          success: function(data) {   
        	 	$("#current_condition").attr("value" , "0");
        	 	$("#infoArea").attr("value" , "");
        	  	$("#productCode").attr("value" , "");
          		$.messager.alert("提示",data, "info", function (){$("#OBIB").html("");});
          },
          error: function() {          //如果过程中出错了调用的方法
        	  $.messager.alert("提示","验证出错","info");
          }
    });
}

function focusCode(){
	var productCode=$.trim($("#productCode").val());
	var condition = $("#current_condition").val();
	if( condition == "0" ) {
		if(productCode=="请扫描包裹单号"){
			$("#productCode").attr("value", "");
			$("#productCode").css("color", "#000000");
		}
	} else if( condition == "1" ) {
		if(productCode=="请扫描商品编号或订单号或出库单号"){
			$("#productCode").attr("value", "");
			$("#productCode").css("color", "#000000");
		}
	} 
}

function blurCode(){
	var productCode=$.trim($("#productCode").val());
	var condition = $("#current_condition").val();
	if( condition == "0" ) {
		if(productCode==""){
			$("#productCode").attr("value", "请扫描包裹单号");
			$("#productCode").css("color", "#cccccc");
		} else {
			$("#productCode").css("color", "#000000");
		}
	} else if( condition == "1" ) {
		if(productCode==""){
			$("#productCode").attr("value", "请扫描商品编号或订单号或出库单号");
			$("#productCode").css("color", "#cccccc");
		} else {
			$("#productCode").css("color", "#000000");
		}
	}
}
		
function checkCode() {
	var code = $.trim($("#productCode").val());
	if( code == null || code == "" ) {
		$.messager.alert("提示", "不能提交空值！" , "info");
		return false;
	}
	
	$.ajax({
	    type: "GET", //调用方式  post 还是 get
	    url: "<%=request.getContextPath()%>/SalesReturnController/checkPackageAutomatic.mmx",
	    data : "code="+code,
	    cache : false,
	    dataType: "text", //返回的数据的形式
	    success: function(data) { 
			var json = eval('(' + data + ')');
			var x = json['tip'].indexOf("。");
			if( x != -1 ) {
				$("#current_condition").attr("value", "0");
			} else {
				$("#current_condition").attr("value", "1");
			}
			var tip = $("#infoArea").val();
			if( tip == null ) {
				if( json['tip'] == "包裹单对应订单状态不是已退回。" ) {
					$.messager.alert("提示", json['tip'], "info");
				}
				tip = json['tip'].replaceAll("r-n", "\r\n");
			} else {
				if( json['tip'] == "包裹单对应订单状态不是已退回。" ) {
					$.messager.alert("提示", json['tip'], "info");
				}
				tip += '\r'+'\n'+ json['tip'].replaceAll("r-n", "\r\n");
			}
			$("#infoArea").attr("value", tip);
			$("#productCode").attr("value", "");
			document.getElementById('infoArea').scrollTop = document.getElementById('infoArea').scrollHeight
			if( json['OBIB'] == "normal" ) {
			} else if( json['OBIB'] == "clear" ) {
				$("#OBIB").html("");
			} else {
				$("#OBIB").html(json['OBIB']);
			}
	    },
	    error: function() {          //如果过程中出错了调用的方法
	    	$.messager.alert("提示", "验证出错", "info");
	    }
	});

	return false;
}
</script>
 </head>
<body>
<input type="hidden" id="current_condition" value="0" />
<div style="margin-left:15px;margin-top:15px;">
  	<table width="400px"><tr align="center"><td><h2>&nbsp;核查包裹:</h2></td></tr></table>
   	<fieldset style="width:400px;">
   		<div style="background-color:#CCFF80;width:400px;height:400px;border-style:solid;border-width:1px;border-color:#000000;">
   		<div style="margin-left:0px;">
   			<br/>
   			<table border="0" cellspacing="12" width="400px">
	   			<tr>
	   				<td colspan="3" align="center">
	   					<textarea rows="8" cols="50" style="width:300px" id="infoArea"></textarea>
	   				</td>
	   			</tr>
	   			<tr>
	   				<td colspan="3" align="center" >
	   					<input name="productCode" id="productCode" onblur="blurCode();" onfocus="focusCode();"/>
	   				</td>
	   			</tr>
	   			<tr>
	   				<td align="center" colspan="3">
	   				<a href="javascript:void(0);" class="easyui-linkbutton"  data-options="iconCls:'icon-cancel',plain:true"  onclick="clearPackage();" >取消当前包裹检查</a>
	   				<a href="javascript:void(0);" id="confirm" class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true"  onclick="checkCode();">确定</a>
	   				</td>
	   			</tr>
   			</table>
   			<br/>
   			</div>
   		<div style="margin-left:12px;font-size:14px;">
   		说明：<br>
   		1.依次扫描包裹单号，商品条码，订单编号（或出库单号）;</br>
   		2.输入模式下请点击“确认”，以核对包裹内商品信息。<br/>
   		</div>
   		</div>
   	</fieldset>
   	</div>

<div id="OBIB"></div>
</body>
</html>
