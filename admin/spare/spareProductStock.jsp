<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>备用机库存列表</title>
<script type="text/javascript">
	var datagrid;
	var flag_one;
	var flag_two;
	var firstSelect = true;
	$(function() {
		datagrid = $('#datagrid').datagrid({
			url : '${pageContext.request.contextPath}/spareManagerController/getSpareProductStockList.mmx',
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
				field : 'productCode',
				title : '商品编号',
				width : 15,
				align : 'center'
			}, {
				field : 'productOriName',
				title : '商品原名称',
				width : 25,
				align : 'center'
			}, {
				field : 'productName',
				title : '商品小店名称',
				width : 20,
				align : 'center'
			}, {
				field : 'supplierName',
				title : '供应商名称',
				width : 20,
				align : 'center'
			}, {
				field : 'szStock',
				title : '库存数量(深圳)',
				width : 20,
				align : 'center'
			} ] ],
			onLoadSuccess : function (data){
				if(data.rows.length == 0){
					if(firstSelect){
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
		$("#backSupplierId").combobox({
			url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
			valueField : 'id',
			textField : 'text',
			delay : 500
		});

		$('#parentId1').combobox({
			url : '${pageContext.request.contextPath}/Combobox/getParentId1.mmx',
			valueField : 'id',
			textField : 'text',
			editable : false,
			onSelect : function(record) {
				flag_one = true;
				$('#parentId2').combobox({
					url : '${pageContext.request.contextPath}/Combobox/getParentId2.mmx?parentId1=' + record.id,
					valueField : 'id',
					textField : 'text',
					editable : false,
					onSelect : function(record) {
						flag_two = true;
						$('#parentId3').combobox({
							url : '${pageContext.request.contextPath}/Combobox/getParentId3.mmx?parentId2=' + record.id,
							valueField : 'id',
							textField : 'text',
							editable : false
						});
					}
				});
			}
		});

	});

	function checkSubmit() {
		var result = false;
		if ($('#parentId1').combobox('getValue') != '') {
			result = true;
		}
		if (flag_one) {
			if ($('#parentId2').combobox('getValue') != '') {
				result = true;
			}
		}
		if (flag_two) {
			if ($('#parentId3').combobox('getValue') != '') {
				result = true;
			}
		}
		if ($("#backSupplierId").combobox("getValue") != "-1") {
			result = true;
		}
		if ($.trim($("#productCode").val()) != '') {
			result = true;
		}

		if (!result) {
			$.messager.show({
				msg : '请输入查询条件!',
				title : '提示'
			});
		}
		return result;
	}

	function searchFun() {
		if (checkSubmit()) {
			var parentId1 = $('#parentId1').combobox('getValue');
			var parentId3 = '';
			var parentId3 = '';
			if (flag_one) {
				parentId2 = $('#parentId2').combobox('getValue');
			}
			if (flag_two) {
				parentId3 = $('#parentId3').combobox('getValue');
			}
			var supplierId = $("#backSupplierId").combobox("getValue");
			var productCode = $.trim($("#productCode").val());
			datagrid.datagrid('load', {
				parentId1 : parentId1 + '',
				parentId2 : parentId2 + '',
				parentId3 : parentId3 + '',
				supplierId : supplierId + '',
				productCode : productCode + ''
			});
		}
	}
</script>
</head>
<body>	
	<table id="datagrid"></table>
	<div id="tb" style="height: auto;">
		<fieldset>
			<legend>备用机库存列表</legend>
			<table>
				<tr>
					<td>商品分类： <input id="parentId1" name="parentId1" style="width: 121px;" /> <input id="parentId2" name="parentId2" style="width: 121px;" /> <input id="parentId3" name="parentId3" style="width: 121px;" />
					</td>
					<td>供应商名称：<input type="text" id="backSupplierId" name="backSupplierId" /></td>
					<td>商品编号：<input id="productCode" name="productCode" style="width: 116px" /></td>
					<td><a class="easyui-linkbutton" iconCls="icon-search" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>
			</table>
		</fieldset>	
	</div>
</body>
</html>