<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
$(function() {
	var columns = "";
	columns = [ [ {
		field : 'productCode',
		title : '产品编号',
		width : 150,
		align : 'center'
	}, {
		field : 'productOriName',
		title : '原名称',
		width : 150,
		align : 'center'
	}, {
		field : 'outAreaStockCount',
		title : '无锡结存',
		width : 150,
		align : 'center'
	}, {
		field : 'outSaleCount',
		title : '无锡日均发货量',
		width : 200,
		align : 'center'
	}, {
		field : 'buyStockCode',
		title : '无锡7日内预计到货单',
		width : 230,
		align : 'center'
	}, {
		field : 'stockinCount',
		title : '无锡7日内预计到货量',
		width : 230,
		align : 'center'
	}, {
		field : 'exchangeOutCount',
		title : '无锡跨区调出量',
		width : 200,
		align : 'center'
	}, {
		field : 'exchangeInCount',
		title : '无锡跨区调入量',
		width : 200,
		align : 'center'
	}, {
		field : 'needExchangeCount',
		title : '<font color="red">需无锡调拨量</red>',
		width : 200,
		align : 'center',
		styler: function(value,row,index){
			return 'color:red';
		}
	}, {
		field : 'inAreaStockCount',
		title : '增城结存',
		width : 150,
		align : 'center'
	} , {
		field : 'saleCount',
		title : '增城日均发货量',
		width : 200,
		align : 'center'
	}, {
		field : 'action',
		title : '<font color="red">操作</red>',
		width : 150,
		align : 'center',
		formatter : function (value, row, index) {
			if (row.needExchangeCount > 0) {
				return '<a target="_blank" style="cursor:pointer" onclick="checkExistzc('+row.productCode+','+row.area+','+row.outArea+','+row.needExchangeCount+');">调往增城</a>';
			} else {
				return '';
			}
		}
	}] ];
	$('#zcareaExpDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/AreaStockExchangeController/queryExchangeProductList.mmx',
		queryParams : {
			type : "1"
		},
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		pagination :true,
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
		columns : columns
	});
});

function checkExistzc(pcode,area,outArea,needExchangeCount){
	jQuery.post("${pageContext.request.contextPath}/AreaStockExchangeController/checkExchange.mmx?productCode="+pcode+"&area="+area,function(result){
		if(result=="1"){
			$.messager.confirm('确认', '该sku存在未完成的跨区调拨单，确认要再次生成调拨单？', function(r) {
				if (r) {
					window.open("${pageContext.request.contextPath}/AreaStockExchangeController/generateExchange.mmx?stockinArea="+area+"&stockOutArea="+outArea+"&productCode="+pcode+"&exchangeCount="+needExchangeCount,'newwindow','top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=yes,location=no, status=no');
				}
			});
		}else if(result=="0"){
			window.open("${pageContext.request.contextPath}/AreaStockExchangeController/generateExchange.mmx?stockinArea="+area+"&stockOutArea="+outArea+"&productCode="+pcode+"&exchangeCount="+needExchangeCount,'newwindow','top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=yes,location=no, status=no');
		}else{
			$.messager.alert("错误", "系统异常，请联系管理员", "info");
		}
	});
}
</script>
<table id="zcareaExpDataGrid"></table>
