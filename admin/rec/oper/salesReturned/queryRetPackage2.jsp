<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>退货包裹列表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
	var queryPackageForm;
	$(function() {
		$('#queryPackageDataGrid').datagrid({
			url : '${pageContext.request.contextPath}/SalesReturnController/queryPackage.mmx',
			fit : true,
			fitColumns : true,
			border : true,
			rownumbers : true,
			singleSelect : true,
			pagination : true,
			striped : true,
			idField : 'id',
			pageSize : 20,
			pageList : [ 10, 20, 30, 40, 50 ],
			nowrap : false,
			toolbar : '#tb',
			columns : [ [ {
				field : 'orderCode',
				title : '订单编号',
				width : 80,
				align : 'center',
				formatter : function (value, row, index) {
					return '<a href="<%=request.getContextPath()%>/admin/order.do?id='+row.orderId+'">'+value+'</a>';
				}
			}, {
				field : 'packageCode',
				title : '包裹单号',
				width : 80,
				align : 'center'
			}, {
				field : 'deliverName',
				title : '快递公司',
				width : 150,
				align : 'center'
			}, {
				field : 'areaName',
				title : '入库地区',
				width : 80,
				align : 'center'
			}, {
				field : 'operatorName',
				title : '操作人',
				width : 80,
				align : 'center'
			}, {
				field : 'returnedPackageStatusName',
				title : '退货包裹状态',
				width : 80,
				align : 'center'
			}, {
				field : 'importTime',
				title : '导入时间',
				width : 160,
				align : 'center'
			}, {
				field : 'storageTime',
				title : '入库时间',
				width : 160,
				align : 'center'
			}, {
				field : 'claimsVerificationCode',
				title : '理赔单',
				width : 100,
				align : 'center',
				formatter : function (value , row, index) {
					return '<a href="javascript:void(0);" onclick="editClaimsVerification('+row.claimsVerificationId+')">'+value+'</a>';
				}
			}, {
				field : 'claimsVerificationStatusName',
				title : '理赔状态',
				width : 100,
				align : 'center'
			}, {
				field : 'returnedReason',
				title : '退回原因',
				width : 100,
				align : 'center'
			} ] ]
		});
		
		$('#returnedPackageStatus').combobox({   
			url:'<%=request.getContextPath()%>/SalesReturnController/getReturnedPackageStatusJSON.mmx',  
			valueField : 'id',   
			textField : 'name',
			panelHeight:'auto',
		    editable:false
		}); 
		
		$('#cvStatus').combobox({   
			url:'<%=request.getContextPath()%>/SalesReturnController/getCvStatusJSON.mmx',  
			valueField : 'id',   
			textField : 'name',
			panelHeight:'auto',
		    editable:false
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
		
		queryPackageForm = $('#queryPackageForm').form({
			url : '<%=request.getContextPath()%>/SalesReturnController/exportPackage.mmx'
		});
	});

	function queryPackageSearchFun() {
		$('#queryPackageDataGrid').datagrid('load', {
			returnedPackageStatus :$('#returnedPackageStatus').combobox("getValue"),
			orderCode : $('#queryPackageForm').find('[name=orderCode]').val(),
			packageCode : $('#queryPackageForm').find('[name=packageCode]').val(),
			cvStatus : $('#cvStatus').combobox("getValue"),
			deliver : $('#deliver').combobox("getValue"),
			storageTime : $('#storageTime').datebox("getValue"),
			wareArea : $('#wareArea').combobox("getValue")
		});
	}
	function queryPackageExportFun() {
		queryPackageForm.submit();
	}
	
	function queryPackageCleanFun() {
		$('#returnedPackageStatus').combobox("setValue", "-1");
		$('#orderCode').attr("value", "");
		$('#packageCode').attr("value", "");
		$('#cvStatus').combobox("setValue", "-1"),
		$('#storageTime').datebox("setValue", "");
		$('#wareArea').combobox("setValue", "-1");
		$('#deliver').combobox("setValue", "-1");
		$('#queryPackageDataGrid').datagrid('load', {
			returnedPackageStatus : '-1',
			cvStatus : '-1',
			deliver : '-1',
			wareArea : '-1'
		});
	}
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
				}
			} ],
			onClose : function() {
				$(this).dialog('destroy');
			}
		});
	};
</script>
</head>
<body>
	<div id="tb"  style="padding:3px;height: auto;">
		<form id="queryPackageForm">
			<fieldset>
				<legend>筛选</legend>
				<span>退货包裹状态：</span>
				<input name='returnedPackageStatus' id='returnedPackageStatus' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>&nbsp;&nbsp;订单号：</span>
				<input size="13" name="orderCode" id="orderCode"  style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>包裹单号：</span>
				<input type="text" size="13" name="packageCode" id="packageCode"  style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>理赔单状态：</span>
				<input name='cvStatus' id='cvStatus' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;<br>
				<span>&nbsp;&nbsp;&nbsp;&nbsp;快递公司：</span>
				<input name='deliver' id='deliver' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>入库日期：</span>
				<input class="easyui-datebox" name="storageTime" id="storageTime"  style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
				<span>&nbsp;&nbsp;库地区：</span>
				<input name='wareArea' id='wareArea' style="width:152px;border:1px solid #ccc""/>&nbsp;&nbsp;
				<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="queryPackageSearchFun();">查询</a> 
				<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="queryPackageExportFun();">导出包裹单列表</a>
				<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true" onclick="queryPackageCleanFun();">清空条件</a>
			</fieldset> 
		</form>
	</div>
	<table id="queryPackageDataGrid"></table>
</body>
</html>