<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>配件领用单编辑</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<%
String r = request.getParameter("receiveId");
%>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var count = 1;
var removeNumber = []; 
var receiveId;
//初始化
$(function(){
	 initCombobox();
	$.ajax({
		url :'${pageContext.request.contextPath}/fittingController/getReceiveFittingEdit.mmx',
		type : 'post',
		dataType : 'json',
		data : {receiveId : '<%=r%>'},
		cache : false,
		success : function(r){
			if(r.success == true){
				initDatagrid();
				receiveId = r.obj.receiveId;
				$('#receiveCode').append(r.obj.receiveCode);
				$('#areaId').combobox('setValue',r.obj.areaId)
				$('#target').combobox('setValue',r.obj.target)
			} else {
				$.messager.alert('警告',r.msg,'info');
				return;
			}
		}
	});
});
function initCombobox(){
	//加载combobox
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterSaleArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		onChange: function(newValue, oldValue){
			if(oldValue != '' && oldValue != -1){//onchange事件 主要是清空数据输入框等和重置已添加数组
				clearFun();
				datagrid.datagrid('load',{
					flag : 'empty'
				});
			}
		}
    });
	//加载combobox
	$('#target').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getFittingTarget.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		onChange: function(newValue, oldValue){
			if(oldValue != '' && oldValue != -1){//onchange事件 主要是清空数据输入框等和重置已添加数组
				clearFun();
				datagrid.datagrid('load',{
					flag : 'empty'
				});
			}
			//根据用途选项 确定是否禁用处理单输入框及是否必填属性
			if(newValue == 1){
				$('#detectCode').val('');
				$('#detectCode').attr({ disabled: 'true' });
			} else {
				$('#detectCode').removeAttr('disabled');
			}
			if(newValue == 2 || newValue == 3) {
				$('#detectCode').validatebox({    
				    required: true,
				}); 
			} else {
				$('#detectCode').validatebox({    
				    required: false,
				}); 
			}
		}
    });
}
function initDatagrid(){
	datagrid = $('#datagrid').datagrid({
		url :'${pageContext.request.contextPath}/fittingController/getCacheFitting.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    columns:[[  
	        {field:'detectCode',title:'处理单号',width:30,align:'center'},  
	        {field:'productOriName',title:'主商品名称',width:30,align:'center'},  
	        {field:'fittingName',title:'配件名称',width:30,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		return value + "(" + rowData.fittingCount + ")";
	        	}},
	        {field:'fittingId',title:'配件ID',width:25,align:'center',hidden:true},  
	        {field:'fittingCode',title:'配件编号',width:25,align:'center',hidden:true},  
	        {field:'fittingCount',title:'数量',width:25,align:'center',hidden:true},  
	        {field:'operaction',title:'操作',width:10,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		return '<a class=\"linkButton_del\" onclick=\"delDatagridFun(' + rowData.detectCode +',' + rowData.fittingCode +');\" href=\"javascript:void(0);\"></a>';
        		}},  
	    ]],
	    onLoadSuccess : function(data){
	    	$(".linkButton_del").linkbutton({ 
	    		plain:false,
	    		iconCls:'icon-cancel'
	    	});
	    }
	});
}
//删除
function delDatagridFun(detectCode,fittingCode) {
	$.ajax({
		url :'${pageContext.request.contextPath}/fittingController/delCacheFitting.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		data : {
			fittingCode : fittingCode,
			detectCode :  detectCode,
		},
		success : function(r){
			datagrid.datagrid('load',{});
		}
	});
}
//清空、重置数据
function clearFun() {
	$('#detectCode').val('');
	for(var i = 0; i < count; i++){
		if(i != 0){
			$("#tr_" + i).remove();
		} else {
			$('#fittingCode_' + i).val('');
			$('#count_' + i).val('');
		}
	}
	count = 1;
	removeNumber = [];
}
//添加多个配件
function addDivFun(){
	var tr = $("#addTr");
	var content = '<tr id=tr_' + count + '>' + 
							'<th align=\"right\">配件编号：</th>' +
							'<td><input id=\"fittingCode_' + count +'\" class=\"validate\"  style=\"width: 151px;\"/></td>' +
							'<th align=\"right\">数量：</th>' + 
							'<td><input id=\"count_' + count + '\" class=\"numberBox\"   style=\"width: 50px;\"/>&nbsp;' + 
								'<a class=\"linkButton\" onclick=\"removeDivFun(' + count +');\" href=\"javascript:void(0);\"></a>' +
							'</td>' +
						'</tr>';
	tr.before(content);
	count ++;
	$(".linkButton").linkbutton({ 
		plain:true,
		iconCls:'icon-remove'
	});
	$('.numberBox').numberbox({    
	    min:0,    
	   max:999    
	});  
	$('.validate').validatebox({    
	    required: true,    
	}); 
	$('.numberBox').validatebox({    
	    required: true,    
	}); 
}
//删除配件输入框
function removeDivFun(number){
	$("#tr_" + number).remove();
	removeNumber[removeNumber.length] = number;
}
//保存
function saveFun(){
	if(!checkForm()){
		return;
	}
	//过滤掉删除的索引
	var fittingCodes = [];
	var fittingCounts = [];
	var detectCode = $('#detectCode' ).val();
	var flag = false;
	for(var i = 0; i < count; i++){
		if(removeNumber.length > 0){
			for(var j = 0; j < removeNumber.length; j++){
				if(i == removeNumber[j]){
					flag = true;
				}
			}
		}
		if(!flag){
			fittingCodes[fittingCodes.length] = $('#fittingCode_' + i).val();
			fittingCounts[fittingCounts.length] = $('#count_' + i).numberbox('getValue');
			flag = false
		}
	}
	$.ajax({
		url :'${pageContext.request.contextPath}/fittingController/addCacheFitting.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		data : {
			fittingCodes : fittingCodes.join(","),
			detectCode : detectCode,
			fittingCounts : fittingCounts.join(","),
			target : $('#target').combobox('getValue'),
		},
		success : function(r){
			if(r.success == true){
				datagrid.datagrid('load',{});
				clearFun();
			} else {
				$.messager.alert('警告',r.msg,'info');
				return;
			}
		}
	});
}
//校验
function checkForm(){
	var areaId = $('#areaId').combobox('getValue');
	if(areaId == -1){
		$.messager.alert('警告','库地区不能为空！');
		return false;
	}
	var target = $('#target').combobox('getValue');
	if(target == -1){
		$.messager.alert('警告','用途不能为空！','info');
		return false;
	}
	if(target == 2 || target == 3 || target == 4){
		if($('#detectCode').val() == ''){
			$.messager.alert('警告','用途为“更换用户商品”“补齐用户商品”“补齐售后商品”时，处理单号为必填！','info');
			return false;
		}
	}
	var flag = false;
	for(var i = 0; i < count; i++){
		if(removeNumber.length > 0){
			for(var j = 0; j < removeNumber.length; j++){
				if(i == removeNumber[j]){
					flag = true;
				}
			}
		}
		if(!flag){
			if(!$('#fittingCode_' + i).validatebox('isValid') || !$('#count_' + i).validatebox('isValid')){
				return false;
			}
		}
		flag = false
	}
	return true;
}
//提交审核
function submitFun(){
	if(receiveId == ''){
		$.messager.alert('警告',"程序异常,重新操作!",'info');
		return;
	}
	var data = datagrid.datagrid('getRows');
	var fittingId = [];
	var fittingCount = [];
	var detectCode = [];
	if(data != ''){
		for(var i = 0; i < data.length; i++){
			fittingId[fittingId.length] = data[i].fittingId;
			fittingCount[fittingCount.length] = data[i].fittingCount;
			if(data[i].detectCode != '' && data[i].detectCode != undefined){
				detectCode[detectCode.length] = data[i].detectCode;
			}
		}
		var detectCodes;
		if(detectCode.length > 0){
			detectCodes = detectCode.join(',');
		}
		$.ajax({
			url :'${pageContext.request.contextPath}/fittingController/editReceiveFitting.mmx',
			type : 'post',
			dataType : 'json',
			cache : false,
			data : {
					receiveId : receiveId,
					target : $('#target' ).combobox('getValue'),
				 	areaId : $('#areaId' ).combobox('getValue'),
					fittingIds : fittingId.join(','),
					detectCodes : detectCodes ,
					fittingCounts : fittingCount.join(','),
			},
			success : function(r){
				if(r.success == true){
					location.href = '${pageContext.request.contextPath}/admin/fitting/afterSaleReceiveFittingList.jsp';
				} else {
					$.messager.alert('警告',r.msg,'info');
					return;
				}
			}
		});
	}else {
		$.ajax({
			url :'${pageContext.request.contextPath}/fittingController/editReceiveFitting.mmx',
			type : 'post',
			dataType : 'json',
			cache : false,
			data : {
					receiveId : receiveId,
					fittingIds : '',
			},
			success : function(r){
				if(r.success == true){
					location.href = '${pageContext.request.contextPath}/admin/fitting/afterSaleReceiveFittingList.jsp';
				} else {
					$.messager.alert('警告',r.msg,'info');
					return;
				}
			}
		});
	}
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table border="0">
				<tr>
					<th align="right"><font color="red"></font>领用单编号：</th>
					<td><label id="receiveCode" style="width: 156px; color: red;"></label> </td>
				</tr>
				<tr>
					<th align="right"><font color="red">*</font>库区：</th>
					<td><input id="areaId" name="areaId" style="width: 156px;"/></td>
				</tr>
				<tr>
					<th align="right"><font color="red">*</font>用途：</th>
					<td><input id="target" name="target"   style="width: 156px;"/></td>
				</tr>
				<tr>
					<th align="right">相关处理单号：</th>
					<td><input id="detectCode" name="detectCode"   style="width: 151px;"/></td>
				</tr>
				<tr>
					<th align="right">配件编号：</th>
					<td><input id="fittingCode_0" name="fittingCode"   class="easyui-validatebox" data-options="required:true" style="width: 151px;"/></td>
					<th align="right">数量：</th>
					<td><input id="count_0" name="count"   class="easyui-numberbox" data-options="min:0,max:999,required:true"    style="width: 50px;"/>
						<a class="easyui-linkbutton" iconCls="icon-add"  plain="true"  onclick="addDivFun();" href="javascript:void(0);"></a>
						</td>
				</tr>
				<tr id="addTr">
					<th align="right"></th>
					<td></td>
					<th align="right"></th>
					<td></td>
					<th align="right"></th>
					<td><a class="easyui-linkbutton" iconCls="icon-save"  onclick="saveFun();" href="javascript:void(0);">保存</a></td>
				</tr>
			</table>
		</fieldset>
		<div align="right"><a class="easyui-linkbutton" iconCls="icon-ok" onclick="submitFun();" href="javascript:void(0);">提交审核</a></div>
	</div>
	<table id="datagrid"></table> 
</body>
</html>