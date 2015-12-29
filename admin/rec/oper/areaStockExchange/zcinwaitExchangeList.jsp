<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
$(function() {
	var columns = "";
	columns = [ [ {
		field : 'ck',
		checkbox : true
	},{
		field : 'productCode',
		title : '产品编号',
		width : 150,
		align : 'center'
	}, {
		field : 'productOriName',
		title : '原名称',
		width : 400,
		align : 'center'
	}, {
		field : 'outAreaStockCount',
		title : '无锡结存',
		width : 100,
		align : 'center'
	},{
		field : 'inAreaStockCount',
		title : '增城结存',
		width : 100,
		align : 'center'
	}, {
		field : 'code',
		title : '调拨单',
		width : 150,
		align : 'center',
		formatter : function(value, row ,index) {
			return '<a href="${pageContext.request.contextPath}/admin/cargoOperation.do?method=exchangeCargo&cargoOperId='+row.id+'" target="_blank">'+row.code+'</a>';
		}
	}, {
		field : 'statusName',
		title : '状态',
		width : 120,
		align : 'center'
	} , {
		field : 'exchangeCount',
		title : '调拨量',
		width : 100,
		align : 'center'
	}, {
		field : 'action',
		title : '操作',
		width : 150,
		align : 'center',
		formatter : function (value, row, index) {
			return '<a href="${pageContext.request.contextPath}/AreaStockExchangeController/printExchangeCargo.mmx?id='+row.id+'" target="_blank">打印调拨单</a>';
		}
	}] ];
	$('#zcinwaitExchangeDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/AreaStockExchangeController/getExchangeList.mmx',
		queryParams : {
			type : "1",
			flag : "0"
		},
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		checkOnSelect : false,
		selectOnCheck : false,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		pagination :true,
		toolbar : [{
			text : "导出选中商品列表",
			iconCls : "icon-save",
			handler: function(){
				var checkedrows = $("#zcinwaitExchangeDataGrid").datagrid("getChecked");
				if (checkedrows.length <= 0) {
					$.messager.alert("提示", "请选择需要导出的商品！", "info");
					return false;
				} else {
					var ids = new Array();
					for (var i = 0 ; i < checkedrows.length; i ++) {
						ids[i] = checkedrows[i].id;
					}
					var zcinform = $('#zcinform').form({
						url : '${pageContext.request.contextPath}/AreaStockExchangeController/exportExchangeProduct.mmx'
					});
					$("#zcinform [id=type]").attr("value", "1");
					$("#zcinform [id=flag]").attr("value", "0");
					$("#zcinform [id=aseBeanId]").attr("value", ids);
					zcinform.submit();
				}
			}
		}],
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
		columns : columns
	});
});
</script>
<table id="zcinwaitExchangeDataGrid"></table>
<form id = "zcinform">
<input type="hidden" id="type" name="type"/>
<input type="hidden" id="flag" name="flag"/>
<input type="hidden" id="aseBeanId" name="aseBeanId"/>
</form>
