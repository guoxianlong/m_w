<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>售后配件关联商品</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var flag = false;
var index;
var datagrid;
var flag_add = false;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleFittingDatagrid.mmx',
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
	    pageSize : 30,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    idField : 'fittingCode',
	    frozenColumns : [[
					{field:'fittingName',title:'配件名称',width:200,align:'center'}
	    ]],
	    columns:[[  
	        {field:'fittingCode',title:'配件编号',width:10,align:'center'},  
	        {field:'productName',title:'适用商品',width:70,align:'center'},  
	        {field:'cz',title:'操作',width:5,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		return "<a class=\"easyui-linkbutton\" iconCls=\"icon-edit\" onclick=\"loadMatchProduct(\'" +  rowIndex + "\');\" href=\"javascript:void(0);\">匹配</a>";
        		}},  
	    ]]
	}); 
	$('#parentId2').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getParentId2.mmx?parentId1=1536',
		valueField : 'id',
		textField : 'text',
		editable : false,
		onSelect : function(record){
			flag = true;
			$('#parentId3').combobox({
				url : '${pageContext.request.contextPath}/Combobox/getParentId3.mmx?parentId2='+record.id,
				valueField : 'id',
				textField : 'text',
				editable : false
			});
		}
	});
	$('#dialog').dialog('close');
	$('#importDialog').dialog('close');
});
function searchMatchProduct(){
	flag_add = true;
	datagrid.datagrid('selectRow',index);
	var row = datagrid.datagrid('getSelected');
	var searchContent = $('#fittingProduct').combotree('getText');
	$('#fittingProduct').combotree({    
	    url:'${pageContext.request.contextPath}/admin/AfStock/searchMatchProduct.mmx?searchContent=' + searchContent + '&oldIds=' + row.productId,    
	    editable : true,
	    multiple:true,
	    valueField:'id',    
	    textField:'text'  
	});
	$('#fittingProduct').combotree('showPanel');
	$('#fittingProduct').combotree('setText',searchContent);
}
function addMatchProduct(){
	datagrid.datagrid('selectRow',index);
	var row = datagrid.datagrid('getSelected');
	var ids = $('#fittingProduct').combotree('getValues');
	if(ids == null || ids == ''){
		$.messager.alert('提示消息','请先选择需要关联的商品,再保存!','info');
		return;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/addMatchProduct.mmx',
		dataType : 'json',
		type : 'post',
		cache : 'false',
		data : {
			ids : ids.join(','),
			fid : row.fid
		},
		success : function(r){
			if(r){
				$.messager.show({msg : r.msg , title : '提示'});
				datagrid.datagrid('reload');
				$('#dialog').dialog('close');
			}
		}
	});
}
function editMatchProduct(){
	datagrid.datagrid('selectRow',index);
	var row = datagrid.datagrid('getSelected');
	var ids = $('#fittingProduct').combotree('getValues');
	if(row.productId == null || row.productId == ""){
		return;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/editMatchProduct.mmx',
		dataType : 'json',
		type : 'post',
		cache : 'false',
		data : {
			ids : ids.join(','),
			oldIds : row.productId,
			fittingId : row.fittingId
		},
		success : function(r){
			datagrid.datagrid('reload');
			$('#dialog').dialog('close');
			$.messager.show({msg : r.msg , title : '提示'});
		}
	});
}
function loadMatchProduct(index){
	flag_add = false;
	this.index = index;
	datagrid.datagrid('selectRow',index);
	var row = datagrid.datagrid('getSelected');
	$('#fittingProduct').combotree({    
	    url:'${pageContext.request.contextPath}/admin/AfStock/loadMatchProduct.mmx?fittingCode=' + row.fittingCode,    
	    editable : true,
	    multiple:true,
	    valueField:'id',    
	    textField:'text'  
	});  
	$('#fittingName').val(row.fittingName);
	$('#fittingProduct').combotree('setValues', row.productId.split(","));
	$('#dialog').dialog('open');
	$('#fittingProduct').combotree('showPanel');
	$('#fittingProduct').combotree('setText','');
}
function importFun(){
	 $('#fittingInfos').val('');
	$('#importDialog').dialog('open');
}
function searchFun(){
	var parentId3;
	if(flag){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	datagrid.datagrid('load',{
		productName : $('#productName').val(),
		fittingName : $('#fittingNameQ').val(),
		parentId2 : $('#parentId2').combobox('getValue'),
		parentId3 : parentId3,
	});
}
function importMatchProduct(){
	var fittingInfos = $('#fittingInfos').val();
	if(fittingInfos.trim() == ''){
		return;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/importFittingBatch.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		data : 'fittingInfos=' + fittingInfos,
		success: function(r){
			datagrid.datagrid('reload');
			$('#importDialog').dialog('close');
			$.messager.show({msg : r.msg , title : '提示'});
		}
	});
}
</script>
</head>
<body>
	<table id="sendBackDataGrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr>
					<th align="right">配件分类：</th>
					<td colspan="1">
						<input id="parentId2" name="parentId2" style="width: 120px;"/>
						<input id="parentId3" name="parentId3"  style="width: 120px;"/></td>
					<th align="right">小店名称：</th>
					<td>
						<input id="productName" name="productName" style="width: 120px;"/></td>
					<th align="right">配件名称：</th>
					<td>
						<input id="fittingNameQ" name="fittingName" style="width: 120px;"/></td>
					<th align="right"></th>
					<td>
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a>
						<a class="easyui-linkbutton" iconCls="icon-undo" plain="true" onclick="importFun();" href="javascript:void(0);">批量导入</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
	<div id="dialog" class="easyui-dialog" title="为配件匹配商品" style="width:450px;height:300px;"   
        data-options="resizable:true,modal:true,
        	buttons:[{
				text:'保存',
				handler:function(){
					if(flag_add){
						addMatchProduct();
					} else {
						editMatchProduct();
					}
				}
			}]">   
    	<table class="tableForm">
			<tr>
				<th align="right">配件名称：</th>
				<td>
					<input id="fittingName" name="fittingName" readonly="readonly" style="width: 200px;"/></td>
			</tr>
			<tr>
				<th align="right">适用商品小店名称：</th>
				<td>
					<input id="fittingProduct" name="fittingProduct" style="width: 200px;"/>
					<a class="easyui-linkbutton" plain="true" onclick="searchMatchProduct();" href="javascript:void(0);">搜</a></td>
			</tr>
		</table>   
	</div>
	<div id="importDialog" class="easyui-dialog" title="批量导入配件" style="width:500px;height:300px;"   
        data-options="resizable:true,modal:true,
        	buttons:[{
				text:'保存',
				handler:function(){
					importMatchProduct();
				}
			}]">   
    	配件信息:<textarea id="fittingInfos" style="width: 400px;height: 200px"></textarea>
	</div>
</body>
</html>