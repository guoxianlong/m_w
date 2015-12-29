<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>包裹核查表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/getReturnPackageCheckInfo.mmx',
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
		pagination : true,
		toolbar : '#tb',
		columns : [ [ {
			field : 'packageCode',
			title : '包裹单号',
			width : 200,
			align : 'center'
		}, {
			field : 'orderCode',
			title : '订单号',
			width : 150,
			align : 'center'
		}, {
			field : 'checkUserName',
			title : '核查人',
			width : 150,
			align : 'center'
		}, {
			field : 'checkTime',
			title : '核查时间',
			width : 300,
			align : 'center',
			formatter : function(value, row, index) {
				if (value == '' || value == null || value == 'undefined') {
					return '';
				} else {
					return value.substr(0,19);
				}
			}
		}, {
			field : 'checkResultName',
			title : '核查结果',
			width : 150,
			align : 'center'
		}, {
			field : 'typeName',
			title : '异常类型',
			width : 150,
			align : 'center'
		}, {
			field : 'detailException',
			title : '异常详情',
			width : 150,
			align : 'center',
			formatter : function(value, row, index) {
				if (row.checkResult == '0') {
					return '';
				} else {
					return '<a href="javascript:void(0);" class="thebutton" onclick="queryDetail(\''+row.id+'\', \''+row.checkResult+'\', \''+row.orderCode+'\')">查看</a>';
				}
			}
		}, {
			field : 'statusName',
			title : '异常处理状态',
			width : 150,
			align : 'center',
			formatter : function(value, row, index) {
				if (row.checkResult == '0') {
					return '';
				} else {
					return value;
				}
			}
		}, {
			field : 'dealException',
			title : '异常处理',
			width : 150,
			align : 'center',
			formatter : function(value, row, index) {
				if (row.checkResult == '0') {
					return '';
				} else {
					if (row.status == 0) {
						return '<a href="javascript:void(0);" class="thebutton" onclick="dealReturnPackageCheck(\''+row.id+'\', \'1\')">处理</a>';
					} else if (row.status == 1) {
						return '<a href="javascript:void(0);" class="thebutton" onclick="auditReturnPackageCheck(\''+row.id+'\', \'2\')">审核</a><a href="javascript:void(0);" class="thebutton" onclick="auditReturnPackageCheck(\''+row.id+'\', \'3\')">审核不通过</a>';
					} else if (row.status == 2) {
						return '<a href="javascript:void(0);" class="thebutton" onclick="completeReturnPackageCheck(\''+row.id+'\', \'4\')">完成</a>';
					} else {
						return '';
					}
				}
			}
		}, {
			field : 'action',
			title : '查看日志',
			width : 150,
			align : 'center',
			formatter : function(value, row, index) {
				return '<a href="javascript:void(0);" class="thebutton" onclick="returnPackageLogList(\''+row.orderCode+'\')">查看日志</a>';
			}
		} ] ],
		onLoadSuccess : function(data) {
			$(".thebutton").linkbutton(
				{ 
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
	
	$('#resultType').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getTypeName.mmx',  
		valueField : 'id',   
		textField : 'text',
		panelHeight:'auto',
	    editable:false
	});
	
	$('#status').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getStatusName.mmx',  
		valueField : 'id',   
		textField : 'text',
		panelHeight:'auto',
	    editable:false
	});
});

function searchFunByCon() {
	$('#datagrid').datagrid('load', {
		wareArea : $("#wareArea").combobox("getValue"),
		orderCode : $('#searchForm').find('[name=orderCode]').val(),
		packageCode : $("#searchForm").find("[name=packageCode]").val(),
		userName : $("#searchForm").find("[name=userName]").val(),
		checkTime : $("#checkTime").datebox("getValue"),
		resultType : $("#resultType").combobox("getValue"),
		status : $("#status").combobox("getValue")
	});
};

//查看日志
function returnPackageLogList(orderCode) {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/rec/oper/salesReturned/queryReturnPackageLogList.jsp',
		width : 800,
		height : 600,
		modal : true,
		title : '退货操作日志',
		buttons : [ {
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
		},
		onLoad : function() {
			$('#queryReturnPackageLogForm').form('load', {
				orderCode : orderCode
			});
			queryReturnPackageLog();
		}
	});
};

//查看异常详情
function queryDetail(checkId, checkResult, orderCode) {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/rec/oper/salesReturned/getReturnPackageCheckInfoDetail.jsp',
		width : 800,
		height : 600,
		modal : true,
		title : '异常详情',
		buttons : [ {
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
		},
		onLoad : function() {
			getReturnPackageCheckInfoDetail(checkId, checkResult, orderCode );
		}
	});
};

//处理
function dealReturnPackageCheck(id, status) {
	$.messager.confirm('确认', '确认开始处理？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/dealReturnPackageCheck.mmx',
				data : "targetId="+id+"&targetStatus="+status,
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$.messager.alert("提示", d.tip, "info", function(){searchFunByCon();});
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

//审核
function auditReturnPackageCheck(id, status) {
	$.messager.confirm('确认', '确认开始审核？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/dealReturnPackageCheck.mmx',
				data : "targetId="+id+"&targetStatus="+status,
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$.messager.alert("提示", d.tip, "info", function(){searchFunByCon();});
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

//完成
function completeReturnPackageCheck(id, status) {
	$.messager.confirm('确认', '确认开始完成？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/dealReturnPackageCheck.mmx',
				data : "targetId="+id+"&targetStatus="+status,
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$.messager.alert("提示", d.tip, "info", function(){searchFunByCon();});
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
	<table id="datagrid"></table>
	<div id="tb" style="padding:3px;height: auto;">
		<form id="searchForm">
			<fieldset>
				<legend>筛选</legend>
				<span>&nbsp;&nbsp;库地区：</span>
				<input name='wareArea' id='wareArea' style="width:152px;border:1px solid #ccc"/>
				<span>&nbsp;&nbsp;订单号：</span>
				<input name="orderCode" id="orderCode" style="width:150px;border:1px solid #ccc"/>
				<span>&nbsp;&nbsp;&nbsp;&nbsp;包裹单号：</span>
				<input name='packageCode' id='packageCode' style="width:150px;border:1px solid #ccc"/>
				<span>核查人：</span>
				<input name="userName" id="userName" style="width:150px;border:1px solid #ccc"/><br>
				<span>核查时间：</span>
				<input class="easyui-datebox" name='checkTime'  id='checkTime' style="width:152px;border:1px solid #ccc"/>
				<span>异常类型：</span>
				<input name="resultType" id="resultType" style="width:152px;border:1px solid #ccc"/>
				<span>异常处理状态：</span>
				<input name='status' id='status' style="width:152px;border:1px solid #ccc"/>
				<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFunByCon();">查询</a>
			</fieldset>
		</form>
	</div>
</body>
</html>