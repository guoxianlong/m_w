<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>销售退货原因列表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$('#returnsReasonDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/returnsReasonList.mmx',
		title : '销售退货原因设置',
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		toolbar : '#tb',
		frozenColumns : [ [ {
			field : 'id',
			title : '编号',
			hidden : true
		}, {
			field : 'isAdd',
			title : '添加或编辑权限',
			hidden : true
		}, {
			field : 'isDel',
			title : '删除权限',
			hidden : true
		} ] ],
		columns : [ [ {
			field : 'code',
			title : '退货原因条码',
			width : 150,
			align : 'center'
		}, {
			field : 'reason',
			title : '销售退货原因',
			width : 200,
			align : 'center'
		}, {
			field : 'action',
			title : '操作',
			width : 200,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				if (row.isDel == 'true') {
					action = action + '<a href="javascript:void(0);" class="editbutton" onclick="editReturnsReason('+row.id+')"></a>';
				}
				action = action + '<a href="javascript:void(0);" class="printbutton" onclick="returnsReasonPrint('+row.id+')"></a>';
				if (row.isAdd == 'true') {
					action = action + '<a href="javascript:void(0);" class="deletebutton" onclick="returnsReasonDelete('+row.id+')"></a>';
				}
				return action;
			}
		} ] ],
		onLoadSuccess : function(data) {
			try {
				if (data.rows[0].isAdd != "true") {
					$("#tb").html('');
				}
			} catch(e) {
				$("#tb").html('');
			};
			//改变datagrid中按钮的class
			$(".editbutton").linkbutton(
				{ 
					text:'编辑', 
					plain:true, 
					iconCls:'icon-edit' 
				}
			);
			$(".printbutton").linkbutton(
				{ 
					text:'打印原因条码', 
					plain:true, 
					iconCls:'icon-print' 
				}
			);
			$(".deletebutton").linkbutton(
				{ 
					text:'删除', 
					plain:true, 
					iconCls:'icon-cancel' 
				}
			);
		}
	});
});
//添加销售退货原因
function addReturnsReason() {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/rec/oper/salesReturned/returnsReasonAdd.jsp',
		width : 400,
		height : 100,
		modal : true,
		title : '添加销售退货原因',
		buttons : [ {
			id : 'addReason',
			text : '增加',
			iconCls : 'icon-add',
			handler : function() {
				var d = $(this).closest('.window-body');
				$('#returnsReasonAddForm').form('submit', {
					url : '${pageContext.request.contextPath}/SalesReturnController/returnsReasonAdd.mmx',
					success : function(result) {
						try {
							var r = $.parseJSON(result);
							if (r.result == 'success') {
								$('#returnsReasonDataGrid').datagrid('reload');
								d.dialog('destroy');
							} else {
								$.messager.alert('提示', r.tip, 'info');
							}
						} catch (e) {
							$.messager.alert('提示', '异常', 'info');
						}
					}
				});
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
		}
	});
};

//修改销售退货原因
function editReturnsReason(id) {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/rec/oper/salesReturned/returnsReasonEdit.jsp',
		width : 400,
		height : 150,
		modal : true,
		title : '修改销售退货原因',
		buttons : [ {
			id : 'editReason',
			text : '修改',
			iconCls : 'icon-save',
			handler : function() {
				var d = $(this).closest('.window-body');
				$('#returnsReasonEditForm').form('submit', {
					url : '${pageContext.request.contextPath}/SalesReturnController/returnsReasonEdit.mmx',
					success : function(result) {
						try {
							var r = $.parseJSON(result);
							if (r.result == 'success') {
								$('#returnsReasonDataGrid').datagrid('reload');
								d.dialog('destroy');
							} else {
								$.messager.alert('提示', r.tip, 'info');
							}
						} catch (e) {
							$.messager.alert('提示', '异常', 'info');
						}
					}
				});
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
		},
		onLoad : function() {
			var index = $('#returnsReasonDataGrid').datagrid('getRowIndex', id);
			var rows = $('#returnsReasonDataGrid').datagrid('getRows');
			var o = rows[index];
			$('#returnsReasonEditForm').form('load', o);
		}
	});
}
//打印退货原因条码
function returnsReasonPrint(id) {
	window.location.href = '${pageContext.request.contextPath}/SalesReturnController/returnsReasonPrint.mmx?id='+id;
}

//删除退货原因
function returnsReasonDelete(id) {
	$.messager.confirm('确认', '您确定要删除退货原因？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/returnsReasonDel.mmx',
				data : "id="+id,
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$('#returnsReasonDataGrid').datagrid('load');
						} else {
							$.messager.alert("错误", d.tip, "info");
						}
					} catch (e) {
						$.messager.alert("错误", "异常", "info");
					}
				}
			});
		}
	});
}
</script>
</head>
<body>
	<table id="returnsReasonDataGrid"></table>
	<div id="tb" class="datagrid-toolbar">
		<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addReturnsReason()">增加</a>
	</div>
</body>
</html>