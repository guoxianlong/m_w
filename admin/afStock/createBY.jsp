<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新建报溢单</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
var lineCount=1;
$(function(){
	$('#bsbyEditTab').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSessionBYinfo.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    columns:[[  
	  			{field:'productName',title:'商品名称',width:60,align:'center'},
	  			{field:'productCode',title:'商品编号',width:100,align:'center'},
	  			{field:'imei',title:'IMEI码 ',width:100,align:'center'},
	  			{field:'count',title:'数量 ',width:100,align:'center'},
	  	        {field:'action',title:'操作',width:60,align:'center',
	  	        	formatter : function(value, row, index) {
	          			return '<a href="javascript:delSessionLine('+index+');" class="editDetectTypeDetail" iconCls="icon-remove"></a>';
	  				}
	  			}
	  	    ] ],
			onLoadSuccess : function(data) {
				//改变datagrid中按钮的class
				$(".editDetectTypeDetail").linkbutton(
					{ 
						text:'删除'
					}
				);
			}
	});
	
	$("#stockArea").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getByArea.mmx',
	    valueField:'id',
		textField:'text',
		editable:false,
		required:true
	});
	
	$("#reason").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getReason.mmx?type=1',
	    valueField:'text',
		textField:'text',
		editable:false,
		required:true
	});
	
});



function appendRow(object){
	var $allProductCode = $("[name=productCode]");
	var $allCount = $("[name=count]");
	var temp =false;
	$allProductCode.each(function(index) {
		if($allProductCode.eq(index).val()==''){
			$.messager.show({
				title:'提示',
				msg:'请填写商品编号',
			});
			temp=true;
			return false;
		}
	});
	$allCount.each(function(index) {
		if($allCount.eq(index).val()==''){
			$.messager.show({
				title:'提示',
				msg:'数量不能为空',
			});
			temp=true;
			return false;
		}
	});
	if(temp==true){
		return;
	}
	
   var tr = $("#tr").clone(true);//克隆一行   
   tr.find("td").find(":input").val('');
   tr.find("td").find(":input").removeAttr('readonly');
   tr.find("td").find(":input").removeAttr('disabled');
   tr.find("td:last").html("<a class='addItemClass' href='javascript:void(0);' onclick='delLine($(this).parent().parent())'></a>");    
   tr.insertBefore("#table tr:last");//把这行加到表格的倒数第二行，表格添加完毕   

   lineCount++;
   $(".addItemClass").linkbutton(
		{ 
			plain:true,
			iconCls:'icon-remove'
		}
	);
} 

function isImei(object){   
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/isImei.mmx',
		data:{productCode:object.val()},
		cache:false,
		type:"post",
		dataType:'Json',
		success:function(data){
			if(data.success==true){
				$.messager.show({
					title:'提示',
					msg:data.msg
				});
				if(data.msg=='此商品需要填写IMEI码！'){
					if(object.parent().next().find('input').val()==''){
						$('#addButton').attr('disabled',true);
						$('#addLine').linkbutton('disable');
						$('#add').attr('disabled',true);
						return;
					}else{
						object.parent().next().find('input').removeAttr('readonly');
						return ;
					}
					
				}
				if(data.msg=='没有该商品'){
					object.parent().parent().find('input').val('')
					return;
				}
				if(data.msg=='不能添加套装产品！'){
					$('#addButton').attr('disabled',true);
					$('#addLine').linkbutton('disable');
					$('#add').attr('disabled',true);
					return;
				}
			}
			if(object.val()!=''){
				object.parent().next().find('input').val('-');
				object.parent().next().find('input').attr('readonly',true)
			}
			$('#addButton').removeAttr('disabled');
			$('#addLine').linkbutton('enable');
			$('#add').removeAttr('disabled');
		}
	});
} 

function isNumber(value){
	if(isNaN(value)){
		$.messager.show({
			title:'提示',
			msg:'数量必须是数字'
		});
		$("#table tr").eq(lineCount).find('td').eq(2).find('input').val('');
	}
}

function setCount(value){ 
	if(value.val()!='-' && value.val()!=''){
		value.parent().next().find('input').val('1');
		value.parent().next().find('input').attr('readonly',true);
		$('#addButton').removeAttr('disabled');
		$('#addLine').linkbutton('enable');
		$('#add').removeAttr('disabled');
	}else{
		value.parent().next().find('input').removeAttr('readonly');
	}
} 

function delLine(object){
	object.remove();
	lineCount--;
}

function delSessionLine(index){
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/delSessionBYinfo.mmx',
		data:{index:index},
		cache:false,
		type:"post",
		dataType:'Json',
		success:function(data){
			if(data.success==true){
				$('#bsbyEditTab').datagrid('reload');
			}
		}
	});
}

function saveAdd(){
	
	var lastLine = $("#table tr").eq(lineCount).find('td').eq(0).find('input');
	if(lastLine.val()==''){
		delLine(lastLine.parent().parent());
	}
	$('#saveForm').form('submit',{
		url:'${pageContext.request.contextPath}/admin/AfStock/saveAdd.mmx',
		dataType:'Json',
		success:function(result){ 
			var data = $.parseJSON(result);
			if(data.success==true){
				if(data.msg=='数量不能为空！'){
					$.messager.show({
						title:'提示',
						msg:data.msg
					});
				}
				return;
			}else{
				$('#bsbyEditTab').datagrid('load');
				//window.location.reload();
			}
		}
	});
}

function auditAdd(){
	$('#auditForm').form('submit',{
		url:'${pageContext.request.contextPath}/admin/AfStock/addAfterSaleByOperationRecord.mmx',
		dataType:'Json',
		success:function(result){ 
			var data = $.parseJSON(result);
			$.messager.show({
				title:'提示',
				msg:data.msg
			});
			if(data.success){
				window.location.href = '${pageContext.request.contextPath}/admin/afStock/createBY.jsp';
			}
		}
	});
}
</script>
</head>
<body>
<div id="tb" style="height: auto;">

<fieldset>
	<legend>新建报溢单</legend>
		<form id="auditForm" method="post">
		<table class="tableForm">
		 <tr>
		  <td>单据类型</td>
		  <td>
		   <select id="bsbyType" class="easyui-combobox" name="bsbyType" style="width:80px;">   
		    <option value="1">报溢</option>   
			</select>
		  </td>
		  <td>库类型</td>
		  <td>
		  <select id="stockType" class="easyui-combobox" name="stockType" style="width:80px;">   
		    <option value="9">售后库</option>   
			</select>
		  </td>
		   <td>库地区</td>
		  <td>
		   <input id="stockArea" name="areaId" style="width: 116px;"/>
		  </td>
		 </tr>
		 <tr>
		  <td><font color="red">*</font>报溢原因</td>
		  <td colspan="3">
		  <select id="reason" class="easyui-combobox" name="reason" style="width:200px;">   
			</select>
		  </td>
		 </tr>
		 </table>
		 </form>
</fieldset>
	<form id="saveForm" method="post">
	<table id="table">
	 <tr>
	  <td colspan="4"><h3>添加商品信息</h3></td>
	 </tr>
	 <tr id="tr">
	  <td>
	   商品编号：<input id="productCode" name="productCode" rows="4" cols="40" onblur="isImei($(this))"/>
	  </td>
	  <td>
	  IMEI码：<input id="imei" name="imei" rows="4" cols="40" onblur="setCount($(this))"/>
	  </td>
	  <td>
	   数量：<input id="count" name="count" rows="4" cols="40" onblur="isNumber($(this).val())" maxlength="4"/>
	  </td>
	  <td align="center">
	 	 <a class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true" onclick="appendRow($(this))" id="addLine"></a>
	  </td>
	 </tr>
	 <tr>
	 <td></td>
	 <td></td>
	 <td></td>
	 <td align="center">
	 	 <a href="javascript:saveAdd();" class="easyui-linkbutton" iconCls="icon-ok" plain="true"  id="add">添加</a>
	  </td>
	 </tr>
	</table>
	</form>
<h3>已添加：</h3>
<div id="toolbar">
	 <a href="javascript:auditAdd();" class="easyui-linkbutton" iconCls="icon-ok" plain="true" id="Audit" >提交审核</a>
</div>
</div>
<table id="bsbyEditTab"></table>
</body>
</html>