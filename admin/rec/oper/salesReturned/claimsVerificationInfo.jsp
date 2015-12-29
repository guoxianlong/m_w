<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>理赔核销</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$('#ClaimsVerificationListDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/getClaimsVerificationInfo.mmx',
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
			title : '理赔单号',
			width : 130,
			align : 'center',
			formatter : function(value, row, index) {
				return '<a href="javascript:void(0);" onclick="editClaimsVerification('+row.id+')">'+value+'</a>';
			}
		}, {
			field : 'orderCode',
			title : '订单号',
			width : 140,
			align : 'center'
		}, {
			field : 'packageCode',
			title : '包裹单号',
			width : 120,
			align : 'center'
		}, {
			field : 'deliverCompanyName',
			title : '快递公司',
			width : 150,
			align : 'center'
		}, {
			field : 'deliverDate',
			title : '发货时间',
			width : 120,
			align : 'center'
		}, {
			field : 'createUserName',
			title : '添加人',
			width : 100,
			align : 'center'
		}, {
			field : 'createTime',
			title : '添加时间',
			width : 180,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return value.substr(0,19);
				} else {
					return "";
				}
			}
		},  {
			field : 'auditUserName',
			title : '审核人',
			width : 100,
			align : 'center'
		}, {
			field : 'auditTime',
			title : '审核时间',
			width : 180,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return value.substr(0,19);
				} else {
					return "";
				}
			}
		},{
			field : 'wareAreaName',
			title : '地区',
			width : 100,
			align : 'center'
		},{
			field : 'statusName',
			title : '状态',
			width : 100,
			align : 'center'
		}, {
			field : 'receiptsNumberString',
			title : '报损单',
			width : 130,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				if (value != 'undefined' && value != "" && value != null) {
					var receiptsNumberlist = value.split(",");
					var lookupList = row.lookupString.split(",");
					var bsbyIdList = row.bsbyIdString.split(",");
					var ifDelList = row.ifDelString.split(",");
					for (i = 0 ; i < receiptsNumberlist.length ; i ++ ) {
						if (ifDelList[i] == '1') {
							action = action + receiptsNumberlist[i];
						} else {
							if (lookupList[i] == "1") {
								action = action + '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?lookup=1&opid='+bsbyIdList[i]+'" target="_blank">'+receiptsNumberlist[i]+'</a><br/>';
							} else {
								action = action + '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid='+bsbyIdList[i]+'" target="_blank">'+receiptsNumberlist[i]+'</a><br/>';
							}
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
			width : 250,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				if (row.status > '0') {
				} else {
					action = action + '<a href="javascript:void(0);" class="editbutton" onclick="editClaimsVerification('+row.id+')"></a>';
					action = action + '<a href="javascript:void(0);" class="parentdeletebutton" onclick="deleteClaims('+row.id+')"></a>';
				}
				action = action + '<a href="javascript:void(0);" class="printbutton" onclick="printClaims('+row.id+')"></a>';
				return action;
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
		}
	});
	
	$('#wareArea').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getWareAreaAllJSON.mmx',  
		valueField : 'id',   
		textField : 'name',
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
		url:'<%=request.getContextPath()%>/SalesReturnController/getCalimsVeriStatusName.mmx',  
		valueField : 'id',   
		textField : 'text',
		panelHeight : 'auto',
	    editable:false
	}); 
});

//查询理赔核销
function ClaimsVerificationListSearch() {
	$("#ClaimsVerificationListDataGrid").datagrid("load",{
		claimsCode : $('#ClaimsVerificationListForm').find('[name=claimsCode]').val(),
		createTime1 : $('#ClaimsVerificationListForm').find('[id=createTime1]').datebox("getValue"),
		createTime2 : $('#ClaimsVerificationListForm').find('[id=createTime2]').datebox("getValue"),
		productCode : $('#ClaimsVerificationListForm').find('[name=productCode]').val(),
		orderCode : $('#ClaimsVerificationListForm').find('[name=orderCode]').val(),
		packageCode : $('#ClaimsVerificationListForm').find('[name=packageCode]').val(),
		deliver : $('#ClaimsVerificationListForm').find('[id=deliver]').combobox("getValue"),
		status : $('#ClaimsVerificationListForm').find('[id=status]').combobox("getValue"),
		bsCode : $('#ClaimsVerificationListForm').find('[name=bsCode]').val(),
		wareArea : $('#ClaimsVerificationListForm').find('[id=wareArea]').combobox("getValue")
	});
};

function addClaimsVerification() {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/rec/oper/salesReturned/addClaimsVerification.jsp',
		width : 1000,
		height : 800,
		modal : true,
		title : '添加理赔核销单',
		buttons : [ {
			id : 'closeAddButton',
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
				$('#ClaimsVerificationListDataGrid').datagrid("load");
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
		}
	});
};

function editClaimsVerification(id) {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/SalesReturnController/foreEditClaimsVerification.mmx?id='+id,
		width : 1000,
		height : 800,
		modal : true,
		title : '编辑理赔核销单',
		buttons : [ {
			id : 'closeEditButton',
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
				$('#ClaimsVerificationListDataGrid').datagrid("load");
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
		}
	});
};

function printClaims(id) {
	window.location.href="<%= request.getContextPath()%>/SalesReturnController/foreEditClaimsVerification.mmx?id="+id+"&print=1";
	return;
};

function deleteClaims( id ) {
	$.messager.confirm('确认', '你确认要删除这个理赔单？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/deleteClaimsVerification.mmx',
				data : "id="+id,
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$.messager.alert("提示", d.tip, "info", function() {$('#ClaimsVerificationListDataGrid').datagrid("reload");});
						} else {
							$.messager.alert("错误", d.tip, "info", function() {$('#ClaimsVerificationListDataGrid').datagrid("reload");});
						}
					} catch (e) {
						$.messager.alert("错误", "异常", "info", function() {$('#ClaimsVerificationListDataGrid').datagrid("reload");});
					}
				}
			});
		}
	});
}
</script>
</head>
<body>
		<table id="ClaimsVerificationListDataGrid"></table>
		<div id="tb"  style="padding:3px;height: auto;">
			<form id="ClaimsVerificationListForm">
				<fieldset>
				<legend>筛选</legend>
					<span>理赔单号：</span>
					<input name='claimsCode' id='claimsCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>添加时间：</span>
					<input class="easyui-datebox" name='createTime1' id='createTime1' style="width:152px;border:1px solid #ccc"/>&nbsp;到&nbsp;
					<input class="easyui-datebox" name='createTime2' id='createTime2' style="width:152px;border:1px solid #ccc"/>
					<span>产品编号：</span>
					<input name='productCode' id='productCode' style="width:150px;border:1px solid #ccc""/>&nbsp;&nbsp;<br>
					<span>订单号：&nbsp;&nbsp;</span>
					<input name='orderCode' id='orderCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>包裹单号：</span>
					<input name='packageCode' id='packageCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>快递公司：</span>
					<input name='deliver' id='deliver' style="width:152px; border:1px solid #ccc"/>&nbsp;&nbsp;<br>
					<span>状态：&nbsp;&nbsp;&nbsp;&nbsp;</span>
					<input name='status' id='status' style="width:152px; border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>报损单号：</span>
					<input name='bsCode' id='bsCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>库地区：&nbsp;&nbsp;</span>
					<input name='wareArea' id='wareArea' style="width:152px; border:1px solid #ccc"/>&nbsp;&nbsp;
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="ClaimsVerificationListSearch();">查询</a>
				</fieldset>
			</form>
			<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addClaimsVerification();">添加理赔单</a>
		</div>
</body>
</html>