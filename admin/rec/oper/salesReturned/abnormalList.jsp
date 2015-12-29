<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>异常入库单列表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$('#abnormalListDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/selectAbnormalList.mmx',
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
		pagination : true,
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
		columns : [ [ {
			field : 'code',
			title : '异常入库单',
			width : 130,
			align : 'center',
			formatter : function(value, row, index) {
				return '<a href="javascript:void(0);" onclick="editAbnormal('+row.id+')">'+value+'</a>';
			}
		}, {
			field : 'orderCode',
			title : '订单号/出库单',
			width : 140,
			align : 'center'
		}, {
			field : 'packageCode',
			title : '包裹单',
			width : 80,
			align : 'center'
		}, {
			field : 'deliverName',
			title : '快递公司',
			width : 150,
			align : 'center'
		}, {
			field : 'sortingDatetime',
			title : '发货日期',
			width : 160,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return value.substr(0,19);
				} else {
					return "";
				}
			}
		}, {
			field : 'operatorName',
			title : '添加人',
			width : 120,
			align : 'center'
		}, {
			field : 'createTime',
			title : '添加时间',
			width : 160,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return value.substr(0,19);
				} else {
					return "";
				}
			}
		}, {
			field : 'statusName',
			title : '状态',
			width : 150,
			align : 'center'
		}, {
			field : 'receiptsNumberString',
			title : '报损报溢单',
			width : 130,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				if (value != 'undefined' && value != "" && value != null) {
					var receiptsNumberlist = value.split(",");
					var lookupList = row.lookupString.split(",");
					var bsbyIdList = row.bsbyIdString.split(",");
					for (i = 0 ; i < receiptsNumberlist.length ; i ++ ) {
						if (lookupList[i] == "1") {
							action = action + '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?lookup=1&opid='+bsbyIdList[i]+'" target="_blank">'+receiptsNumberlist[i]+'</a><br/>';
						} else {
							action = action + '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid='+bsbyIdList[i]+'" target="_blank">'+receiptsNumberlist[i]+'</a><br/>';
						}
					}
					return action;
				} else {
					return "";
				}
			}
		}, {
			field : 'action',
			title : '操作',
			width : 200,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				if (row.status == '0') {
					if (row.isEditAndDel == "true") {
						action = action + '<a href="javascript:void(0);" class="editbutton" onclick="editAbnormal('+row.id+')"></a>';
						action = action + '<a href="javascript:void(0);" class="parentdeletebutton" onclick="deleteAbnormal('+row.id+')"></a>';
						return action;
					} else {
						return "编辑&nbsp;删除";
					}
				} else if (row.status == '1') {
					if (row.isAudit == "true") {
						action = action + '<a href="javascript:void(0);" class="auditbutton" onclick="editAbnormal('+row.id+')"></a>';
						return action;
					} else {
						return "审核";
					}
				} else {
					return '<a href="javascript:void(0);" class="printbutton" onclick="printAbnormal('+row.id+','+row.bsbyId+')"></a>';
				}
			}
		} ] ],
		onLoadSuccess : function(data) {
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
					text:'打印', 
					plain:true, 
					iconCls:'icon-print' 
				}
			);
			$(".parentdeletebutton").linkbutton(
				{ 
					text:'删除', 
					plain:true, 
					iconCls:'icon-cancel' 
				}
			);
			$(".auditbutton").linkbutton(
				{ 
					text:'审核', 
					plain:true, 
					iconCls:'icon-ok' 
				}
			);
		}
	});
	
	$('#wareArea').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getReturnPackageWareAreaJSON.mmx',  
		valueField : 'areaId',   
		textField : 'areaName',
		panelHeight:'auto',
	    editable:false
	}); 
	
	$('#deliver').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getDeliverJSON.mmx',  
		valueField : 'id',   
		textField : 'name',
	    editable:false
	}); 
	
	$('#status').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getAbnormalStatusName.mmx',  
		valueField : 'id',   
		textField : 'text',
		panelHeight : 'auto',
	    editable:false
	}); 
});

function abnormalListSearch() {
	$("#abnormalListDataGrid").datagrid("load",{
		abnormalCode : $('#abnormalListForm').find('[name=abnormalCode]').val(),
		startTime : $('#abnormalListForm').find('[id=startTime]').datebox("getValue"),
		endTime : $('#abnormalListForm').find('[id=endTime]').datebox("getValue"),
		deliver : $('#abnormalListForm').find('[id=deliver]').combobox("getValue"),
		orderCode : $('#abnormalListForm').find('[name=orderCode]').val(),
		packageCode : $('#abnormalListForm').find('[name=packageCode]').val(),
		status : $('#abnormalListForm').find('[id=status]').combobox("getValue"),
		operator : $('#abnormalListForm').find('[name=operator]').val(),
		wareArea : $('#abnormalListForm').find('[id=wareArea]').combobox("getValue")
	});
};

//添加异常入库单
function addAbnormal() {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/rec/oper/salesReturned/addWarehousingAbnormal.jsp',
		width : 1000,
		height : 600,
		modal : true,
		title : '添加异常入库单',
		buttons : [ {
			id : 'closeAddButton',
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
				$('#abnormalListDataGrid').datagrid("reload");
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
			$('#abnormalListDataGrid').datagrid("reload");
		}
	});
};


//编辑异常入库单
function editAbnormal(abnormalId) {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/rec/oper/salesReturned/editWarehousingAbnormal.jsp?abnormalId='+abnormalId,
		width : 1000,
		height : 600,
		modal : true,
		title : '编辑异常入库单',
		buttons : [ {
			id : 'closeEditButton',
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
				$('#abnormalListDataGrid').datagrid("reload");
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
			$('#abnormalListDataGrid').datagrid("reload");
		}
	});
};

function deleteAbnormal(abnormalId) {
	$.messager.confirm('确认', '确认删除？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/delWarehousingAbnormal.mmx',
				data : "abnormalId="+abnormalId,
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$.messager.alert("提示", d.tip, "info", function() {$('#abnormalListDataGrid').datagrid("reload");});
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
};

function printAbnormal(abnormalId, bsbyId) {
	window.location.href = '${pageContext.request.contextPath}/SalesReturnController/printWarehousingAbnormal.mmx?abnormalId='+abnormalId+'&bsbyId='+bsbyId;
}
</script>
</head>
<body>
	<table id="abnormalListDataGrid"></table>
	<div id="tb" style="padding:3px;height: auto;">
		<form id="abnormalListForm">
			<fieldset>
				<legend>筛选</legend>
				<span>异常入库单：</span>
				<input name='abnormalCode' id='abnormalCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>添加时间：</span>
				<input class="easyui-datebox" name='startTime' id='startTime' style="width:152px;border:1px solid #ccc"/>&nbsp;到&nbsp;<input class="easyui-datebox" name='endTime' id='endTime' style="width:152px;border:1px solid #ccc"/>
				<span>快递公司：</span>
				<input name='deliver' id='deliver' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;<br>
				<span>订单号：&nbsp;&nbsp;&nbsp;&nbsp;</span>
				<input name='orderCode' id='orderCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>包裹单号：</span>
				<input name='packageCode' id='packageCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>异常单状态：</span>
				<input name='status' id='status' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;<br>
				<span>添加人：&nbsp;&nbsp;&nbsp;&nbsp;</span>
				<input name='operator' id='operator' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>库地区：&nbsp;&nbsp;</span>
				<input name='wareArea' id='wareArea' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="abnormalListSearch();">查询</a>
			</fieldset>
		</form>
		<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addAbnormal();">增加异常入库单</a>
	</div>
	<input type="hidden" id="hiddenabnormalId"/>
</body>
</html>