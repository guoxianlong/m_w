<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>货位列表</title>
<script type="text/javascript">
	var datagrid;
	var firstSelect = true;
	$(function() {
		datagrid = $('#datagrid').datagrid({
			url : '${pageContext.request.contextPath}/spareManagerController/getSpareCargoProductStockList.mmx',
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
			pageList : [ 10, 20, 30, 40, 50 ],
			frozenColumns : [ [ {
				field : 'id',
				width : 20,
				hidden : true
			} ] ],
			columns : [ [ {
				field : 'cargoCode',
				title : '货位号',
				width : 20,
				align : 'center'
			}, {
				field : 'spareCode',
				title : '备用机号',
				width : 20,
				align : 'center'
			}, {
				field : 'productCode',
				title : '商品编号',
				width : 20,
				align : 'center'
			}, {
				field : 'productOriName',
				title : '商品原名称',
				width : 20,
				align : 'center'
			}, {
				field : 'areaName',
				title : '库地区',
				width : 10,
				align : 'center'
			}, {
				field : 'stockCount',
				title : '当前货位库存',
				width : 10,
				align : 'center'
			} ] ],
			onLoadSuccess : function(data) {
				if (data.rows.length == 0) {
					if (firstSelect) {
						firstSelect = false;
						return;
					}
					$.messager.show({
						msg : '没有查询到您所需要的信息，请重新输入查询条件进行查询!',
						title : '提示'
					});
				}
			}
		});

		$("#areaId").combobox({
			url : '${pageContext.request.contextPath}/Combobox/getSpareArea.mmx',
			valueField : 'id',
			textField : 'text'
		});
	});

	function checkSubmit() {
		var result = false;
		if ($.trim($("#cargoCode").val()) != '') {
			result = true;
		}
		if ($.trim($("#spareCode").val()) != '') {
			result = true;
		}
		if ($.trim($("#productCode").val()) != '') {
			result = true;
		}
		if (!result) {
			$.messager.show({
				msg : '请输入库地区以外的查询条件!',
				title : '提示'
			});
		}
		return result;
	}

	function searchFun() {
		if (checkSubmit()) {
			var areaId = $("#areaId").combobox("getValue");
			var productCode = $.trim($("#productCode").val());
			var cargoCode = $.trim($("#cargoCode").val());
			var spareCode = $.trim($("#spareCode").val());
			datagrid.datagrid('load', {
				cargoCode : cargoCode + '',
				spareCode : spareCode + '',
				productCode : productCode + '',
				areaId : areaId + ''
			});
		}
	}
</script>
</head>
<body>
	<table id="datagrid"></table>
	<div id="tb" style="height: auto;">
		<fieldset>
			<legend>货位列表</legend>
			<table>
				<tr>
					<td>货位号： <input id="cargoCode" name="cargoCode" style="width: 121px;" /></td>
					<td>备用机号：<input type="text" id="spareCode" name="spareCode" /></td>
					<td>商品编号：<input id="productCode" name="productCode" style="width: 116px" /></td>
					<td>库地区：<input id="areaId" name="areaId" style="width: 116px" /></td>
					<td><a class="easyui-linkbutton" iconCls="icon-search" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>