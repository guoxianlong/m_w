<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
$(function(){
	$('#deliver').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getDeliver.mmx',
      	valueField:'id',
		textField:'text',
    });
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
    });
	$('#returnType').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getReturnType.mmx',
      	valueField:'id',
		textField:'text' 
    });
	$('#packageCode').bind('keypress',function(event){
        if(event.keyCode == "13"){
        	addFun();
        }
    });
	$('#freight').numberbox({  
	    min:0,  
	    precision:2  
	});
});
function addFun() {
	var returnType = $('#returnType').combobox('getValue');
	var deliver = $('#deliver').combobox('getValue');
	var areaId = $('#areaId').combobox('getValue');
	var freight = $('#freight').numberbox('getValue');
	var packageCode = $('#packageCode').val();
	if(returnType == 1){
		if(freight == null || freight == 0){
			$.messager.show({
				msg : '选择到付是运费金额必填!',
				title : '提示'
			});
			return;
		}
	}else{
		if(freight == ''){
			$('#freight').numberbox('setValue',0);
			freight = 0;
		}
	}
	if(deliver == ''){
		return;
	}
	if(packageCode == ''){
		return;
	}
	if(areaId == ''){
		return;
	}
	if(!isString(packageCode)){
		$('#packageCode').val('');
		return;
	}
	$.ajax({
	    url : "${pageContext.request.contextPath}/admin/AfStock/customerPackageSign.mmx",
		type : "POST",
		dataType : 'json',
		cache: false,
		data : {
			freight : freight,
			returnType : returnType,
			deliverId : deliver,
			packageCode : packageCode,
			areaId : areaId,
		},
		success: function(d){
			if (d) {
				$('#packageCode').val('');
				$('#freight').numberbox('setValue','');
				$.messager.show({
					msg : d.msg,
					title : '提示'
				});
			}
		}
	});
}
//判断输入的字符是否为:a-z,A-Z,0-9    
function isString(str)     
{     
        if(str.length!=0){    
//        	var reg = /\s+/;
//	        reg=/^[a-zA-Z0-9]+$/;     
        	if(/\s/.test(str)){
        		alert("包裹单号不合法");
        		return false;
            }
        }
        return true;
}   
</script>
</head>
<body>
	<div align="center">
		<form  id="form">
			<table class="tableForm" >
				<tr>
					<th>快递公司：</th>
					<td><input id="deliver" name="deliverId" editable="false" required="required" style="width: 155px;" /></td>
				</tr>
				<tr>
					<th>寄回方式：</th>
					<td><input id="returnType" name="returnType" editable="false" required="required"style="width: 155px;"  /></td>
				</tr>
				<tr>
					<th>运费金额：</th>
					<td><input id="freight" name="freight" /></td>
				</tr>
				<tr>
					<th>包裹单号：</th>
					<td><input id="packageCode" name="packageCode" class="easyui-validatebox" required="required"/></td>
				</tr>
				<tr>
					<th>签收地区：</th>
					<td><input id="areaId" name="areaId" class="easyui-validatebox" required="required" style="width: 155px;"/></td>
				</tr>
				<tr align="center">
					<th></th>
					<td ><a class="easyui-linkbutton" onclick="addFun();" href="javascript:void(0);">添加</a></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>