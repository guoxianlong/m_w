<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var dialog;
var editDetectTypeDetail;
$(function(){
	datagrid = $('#detectTypeDetailManagerDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/admin/AfStock/getDetectTypeDetailDatagrid.mmx',
		toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    selectOnCheck : false,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50 ],
	    queryParams : {
	    	afterSaleDetectTypeId:0,
			parentId1:0,
			detectTypeParentId1:0,
			detectTypeParentId2:0
	    },
		onBeforeEdit:function(index,row){  
	        row.editing = true;  
	        $('#detectTypeDetailManagerDataGrid').datagrid('refreshRow', index);  
	    },  
	    onAfterEdit:function(index,row){  
	        if(row!=null){
	        	$.ajax({
					url : '${pageContext.request.contextPath}/admin/AfStock/saveDetectTypeDetail.mmx',
					data : row,
					type : 'post',
					cache : false,
					dataType : "json",
					success : function(result) {
						try {
							if (result.success) {
								$.messager.show({
									title : '提示',
									msg : result.msg
								});
								datagrid.datagrid('reload');
								row.editing = false;
								editDetectTypeDetail = undefined;
							}else{
								$.messager.show({
									title : '提示',
									msg : result.msg
								});
								row.editing = true;
								datagrid.datagrid('beginEdit',index);
							}
						} catch (e) {
							$.messager.alert('提示', result);
						}
					}
				});
	        }  
	    },
	    columns : [[
			{field:'id',title:'ID',width:20,hidden:false,checkbox:true},
			{field:'afterSaleDetectTypeName',title:'选项  <input id="afterSaleDetectTypeId1"/>',width:60,align:'center'},
			{field:'parentId1Name',title:'商品一级分类  <input id=\"parentId1\"/>',width:100,align:'center'},
			{field:'content',title:'内容一级分类',width:80,align:'center',editor : {type : 'text'}},
			{field:'action',title:'操作',width:60,align:'center',
				formatter : function(value, row, index) { 
					var e='';
					if(row.editing){
						e = '<a href=\"javascript:void(0);\" class=\"ope-save\" onclick=\"saveRow('+index+')\">保存</a>';
					}else{
 						e = '<a href=\"javascript:void(0);\" class=\"ope-edit\" onclick=\"editRow('+index+')\">编辑</a>';
 					}
					var c = ' <a href=\"javascript:void(0);\" class=\"ope-query\" onclick=\"queryDetectTypeDetail('+index+',2)\">查看</a>'; 
					var d = ' <a href=\"javascript:void(0);\" class=\"ope-remove\" onclick=\"deleteRow('+index+')\">删除</a>';
					return e+c+d;
				}
			}            
	    ]]
	}); 
	
	$('#afterSaleDetectTypeId').combobox({
      	url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectType.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		required:true
    });
	
	$('#afterSaleDetectTypeId1').combobox({
      	url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectType.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		onSelect : function (n) {
			$('#detectTypeDetailManagerDataGrid').datagrid('load', {
				afterSaleDetectTypeId:n.id,
				parentId1:$('#parentId1').combobox("getValue"),
				detectTypeParentId1 : 0,
				detectTypeParentId2 : 0
			});
		}
    });
	$('#parentId1Name').combobox({
      	url : '${pageContext.request.contextPath}/admin/AfStock/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		multiple:true,
		required:true
    });
	$('#parentId1').combobox({
      	url : '${pageContext.request.contextPath}/admin/AfStock/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		onSelect : function (n) {
			$('#detectTypeDetailManagerDataGrid').datagrid('load', {
				afterSaleDetectTypeId:$('#afterSaleDetectTypeId1').combobox("getValue"),
				parentId1:n.id,
				detectTypeParentId1 : 0,
				detectTypeParentId2 : 0
			});
		}
    });
	
	dialog = $("#addContentDiv").show().dialog({
		width : 600,
		height : 500,
		modal : true,
		minimizable : true,
		title : '添加内容一级分类', 
		onClose : function(){
			//$('#addContentForm')[0].reset();
		},
		buttons : [{
			text : '确定',
			handler : function() {
				$("#addContentForm").form('submit', {
					url : '${pageContext.request.contextPath}/admin/AfStock/addDetectTypeDetail.mmx',
					success : function(result) {
						var result = $.parseJSON(result);
						try {
							if (result.success) {
								dialog.dialog("close");
								datagrid.datagrid('reload');
							}
							console.info(result.msg);
							$.messager.show({
								title : '提示',
								msg : result.msg
							});
						} catch (e) {
							$.messager.alert('提示', result);
						}
					}
				});
			}
		},
		{
			text : '取消',
			handler : function() {
				dialog.dialog('close');
			}
		}]
	}).dialog('close');
	
	$('#detectTypeId').combobox({
      	url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectType.mmx?',
      	valueField:'id',
		textField:'text',
		editable:false,
		required:true
    });
	
	$('#productParentId1').combobox({
      	url : '${pageContext.request.contextPath}/admin/AfStock/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		multiple:true
    });
});

function queryDetectTypeDetail(index,level){
	if (index != undefined) {
		datagrid.datagrid('selectRow', index);
	}
	var row = datagrid.datagrid('getSelected');
	if(level == 2){
		window.location.href = '${pageContext.request.contextPath}/admin/afStock/afterSaleDetectTypeDetailSecondManager.jsp?afterSaleDetectTypeId='
				+row.afterSaleDetectTypeId+"&parentId1="+row.parentId1+"&detectTypeParentId1="+row.id 
				+"&detectTypeParentId2="+row.detectTypeParentId2+"&detectTypeDetailId="+row.id;
	}
}

function replaceAllStr(theStr, replaceStrA, replaceStrB){ 
   var re=new RegExp(replaceStrA, "g"); 
   var newstart = theStr.replace(re, replaceStrB); 
   return newstart;
} 

function addDetectTypeDetail(){
	dialog.dialog('open');
}

function batchDeleteDetectTypeDetail(){
	var rows = datagrid.datagrid('getChecked');
	var ids = [];
	if(rows.length == 0){
		$.messager.show({
			title:'提示消息',
			msg : "先选择要删除的内容分类!",
			showType:'show',
		});
		return;
	}
	$.each(rows, function(key, val) {
	    ids.push(val.id);
	});
	if(rows != null){
		$.ajax({
			url : "${pageContext.request.contextPath}/admin/AfStock/batchDeleteDetectTypeDetail.mmx",
			type : 'post',
			dataType : 'json',
			cache : false,
			data : {
				ids : ids.join(",")
			},
			success : function(r){
				if(r.success){
					datagrid.datagrid("reload");
				}
				$.messager.show({
					title:'提示消息',
					msg:r.msg,
					showType:'show',
				});
			}
		});
	}
}

function deleteRow(index){
	if (index != undefined) {
		datagrid.datagrid('selectRow', index);
	}
	var row = datagrid.datagrid('getSelected');
	if(row != null){
		$.ajax({
			url : "${pageContext.request.contextPath}/admin/AfStock/batchDeleteDetectTypeDetail.mmx",
			type : 'post',
			dataType : 'json',
			cache : false,
			data : {
				ids : row.id
			},
			success : function(r){
				if(r.success){
					datagrid.datagrid("reload");
				}
				$.messager.show({
					title:'提示消息',
					msg:r.msg,
					showType:'show',
				});
			}
		});
	}
}

function editRow(index){
	if (index != undefined) {
		datagrid.datagrid('selectRow', index);
	}
	if(editDetectTypeDetail){
		$.messager.show({
			title:'提示消息',
			msg : "请先保存之前编辑的内容分类!",
			showType:'show',
		});
	}else{
		datagrid.datagrid('beginEdit',index);
		editDetectTypeDetail = datagrid.datagrid('getSelected');
	}
}

function saveRow(index){
	if (index != undefined) {
		datagrid.datagrid('selectRow', index);
	}
	if(editDetectTypeDetail){
		datagrid.datagrid('endEdit',index);
		editDetectTypeDetail = datagrid.datagrid('getSelected');
	}
}
</script>
</head>
<body>
	<table id="detectTypeDetailManagerDataGrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<div align="right">
			<input  type="button"  id="add" onclick="addDetectTypeDetail();" value="添加内容一级分类"/>
			<input  type="button" id="batchDelete" onclick="batchDeleteDetectTypeDetail();" value="批量删除" />
		</div>
	</div>
	
	<div id="addContentDiv">
		<form id="addContentForm" method="post">
			<table id="addContentTable" class="tableForm" >
				<tr>
					<td>选项：<input type="text" id="detectTypeId" name="detectTypeId"/></td>
				</tr>
				<tr>
					<td>商品一级分类：<input  type="text"  id="productParentId1" name="productParentId1"/></td>
				</tr>
				<tr>
					<td>
						添加内容一级分类：<textarea id='content' name='content' rows='10' cols='60'></textarea>
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>