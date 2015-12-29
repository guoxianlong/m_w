<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>厂家维修更换商品列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getBackSuppilerReplace.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
					{field:'id',title:'ID',width:20,hidden:false,checkbox:true},
					{field:'detectCode',title:'处理单号',width:120,align:'center'}
	    ]],
	    columns:[[  
	        {field:'replaceCode',title:'维修更换单',width:40,align:'center'},  
	        {field:'oldImei',title:'原IMEI',width:30,align:'center'},  
	        {field:'oldProductCode',title:'原商品编号',width:30,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return '<a  href="${pageContext.request.contextPath}/admin/fproduct.do?id=' + rowData.oid + '" target="_blank" >'+  value +'</a>';
	        		}
	    		}},  
	        {field:'newImei',title:'新IMEI',width:30,align:'center'},
	        {field:'newProductCode',title:'新商品编号',width:30,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return '<a href="${pageContext.request.contextPath}/admin/fproduct.do?id=' + rowData.nid + '" target="_blank" >'+  value +'</a>';
	        		}
	    		}},  
	        {field:'supplierName',title:'维修厂家',width:25,align:'center'},  
	        {field:'createDatetime',title:'添加日期',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	        {field:'createUserName',title:'添加人',width:30,align:'center'},  
	        {field:'auditDatetime',title:'审核日期',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	        {field:'auditUserName',title:'审核人',width:30,align:'center'},  
	        {field:'oldPrice',title:'原品入库均价含税(不含税)',width:30,align:'center'},  
	        {field:'newPrice',title:'新品入库均价含税(不含税)',width:30,align:'center'},  
	        {field:'nowPrice',title:'新品实时均价含税(不含税)',width:30,align:'center'},  
	        {field:'statusName',title:'返厂状态',width:30,align:'center'},  
	        {field:'auditStatusName',title:'审核状态',width:20,align:'center'},  
	    ]]
	});
	 
	$('#status').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getBackSupplierStatus.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#supplierId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		startTime : $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		auditStartTime : $('#auditStartTime').datebox('getValue'),
		auditEndTime : $('#auditEndTime').datebox('getValue'),
		detectCode :  $('#tb input[name=detectCode]').val(),
		status : $('#status').combobox('getValue'),
		auditStatus : $('#auditStatus').combobox('getValue'),
		createUserName :  $('#tb input[name=createUserName]').val(),
		auditUserName :  $('#tb input[name=auditUserName]').val(),
		oldImei :  $('#tb input[name=oldImei]').val(),
		oldProductCode :$("#oldProductCode").val(),
		supplierId : $('#supplierId').combobox('getValue'),
		newImei : $("#newImei").val(),
		newProductCode : $("#newProductCode").val(),
	});
}
//导出excel
function exportFun(){
	window.location.href = "${pageContext.request.contextPath}/admin/AfStock/exportBackSuppilerReplace.mmx?"
		+"startTime="+$('#startTime').datebox('getValue')
		+"&endTime="+$('#endTime').datebox('getValue')
		+"&auditStartTime="+$('#auditStartTime').datebox('getValue')
		+"&auditEndTime="+$('#auditEndTime').datebox('getValue')
		+"&detectCode="+$('#tb input[name=detectCode]').val()
		+"&status="+$('#status').combobox('getValue')
		+"&auditStatus="+$('#auditStatus').combobox('getValue')
		+"&createUserName="+$('#tb input[name=createUserName]').val()
		+"&auditUserName="+$('#tb input[name=auditUserName]').val()
		+"&oldImei="+$('#tb input[name=oldImei]').val()
		+"&oldProductCode="+$("#oldProductCode").val()
		+"&supplierId="+$('#supplierId').combobox('getValue')
		+"&newImei="+ $("#newImei").val()
		+"&newProductCode="+$("#newProductCode").val();
}

function auditFun(flag){
	var rows = datagrid.datagrid('getChecked');
	var ids = [];
	if(rows.length == 0){
		$.messager.show({
			title:'提示消息',
			msg : "先选择要审核的更换单!",
			showType:'show',
		});
		return;
	}
	$.each(rows, function(key, val) {
	    ids.push(val.id);
	});
	if(rows != null){
		$.ajax({
			url : "${pageContext.request.contextPath}/admin/AfStock/auditBackSuppilerReplace.mmx",
			type : 'post',
			dataType : 'json',
			cache : false,
			data : {
				flag : flag,
				ids : ids.join(","),
			},
			success : function(r){
				datagrid.datagrid("reload");
				$.messager.show({
					title:'提示消息',
					msg:r.msg,
					showType:'show',
				});
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
					<th align="right">处理单号：</th>
					<td><input id="detectCode" name="detectCode" style="width: 90px;"/></td>
					<th align="right">返厂状态：</th>
					<td><input id="status" name="status" style="width: 95px;"/></td>
					<th align="right">审核状态：</th>
					<td><select id="auditStatus" class="easyui-combobox" name="auditStatus" style="width:95px;">   
					    <option value=""></option>   
					    <option value="1">待审核</option>   
					    <option value="2">通过</option>   
					    <option value="3">未通过</option>   
					</select>  
					</td>
					<th align="right">添加人：</th>
					<td><input id="createUserName" name="createUserName" style="width: 90px;"/></td>
					<th align="right">审核人：</th>
					<td><input id="auditUserName" name="auditUserName" style="width: 90px;"/></td>
				</tr>
				<tr>
					<th >原IMEI：</th>
					<td align="left">
						<input id="oldImei" name="oldImei" style="width:90px" /></td>
					<th align="right">原商品编号：</th>
					<td><input id="oldProductCode" name="oldProductCode"   style="width: 90px;"/></td>
					<th align="right">维修厂家：</th>
					<td><input id="supplierId" name="supplierId"   style="width: 95px;"/></td>
					<th >新IMEI：</th>
					<td align="left">
						<input id="newImei" name="newImei" style="width: 90px" /></td>
					<th align="right">新商品编号：</th>
					<td><input id="newProductCode" name="newProductCode"   style="width: 90px;"/></td>
				</tr>
				<tr>
					<th align="right">添加日期：</th>
					<td colspan="2">
						<input id="startTime" name="startTime" class="easyui-datebox" editable="false" style="width: 88px;" /> -
						<input id="endTime" name="endTime" class="easyui-datebox" editable="false" style="width: 88px;" /></td>
					<td>
					<th align="right">审核日期：</th>
					<td colspan="3">
						<input id="auditStartTime" name="auditStartTime" class="easyui-datebox" editable="false" style="width: 88px;" /> -
						<input id="auditEndTime" name="auditEndTime" class="easyui-datebox" editable="false" style="width: 88px;" /></td>
					<td  colspan="2" align="right">
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a>
						&nbsp;
						<a class="editbutton" onclick="exportFun();" href="javascript:void(0);">导出Excel表格</a>
					</td>
				</tr>
			</table>
		</fieldset>
	<a class="easyui-linkbutton" iconCls="icon-ok" plain="false" onclick="auditFun('2');" href="javascript:void(0);">审核通过</a>
	<a class="easyui-linkbutton" iconCls="icon-no" plain="false" onclick="auditFun('3');" href="javascript:void(0);">审核不通过</a>
	</div>
	<table id="datagrid"></table> 
</body>
</html>