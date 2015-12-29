<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>订单明细</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$('#orderDetailDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/BIController/orderDetail.mmx',
		queryParams : {
			stockArea:'${param.stockArea}',
			startYear:'${param.startYear}',
			endYear:'${param.endYear}',
			startMonth:'${param.startMonth}',
			endMonth:'${param.endMonth}',
			startTime:'${param.startTime}',
			endTime:'${param.endTime}',
			type:'${param.type}'
	    },
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		loadMsg : '正在努力为您加载..',
		toolbar : '#tb',
		pagination : true,
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
		columns :[[  
			{field:'orderCode',title:'订单编号',width:60,align:'center'},
			{field:'productCode',title:'产品编号',width:60,align:'center'},
			{field:'productName',title:'产品名称',width:120,align:'center'},
			{field:'productLineName',title:'产品线',width:60,align:'center'},
			{field:'address',title:'地址',width:60,align:'center'},
			{field:'statusName',title:'订单状态',width:60,align:'center'},
			{field:'createDatetime',title:'生成时间',width:80,align:'center',
				formatter : function(value, row, index) {
					if (value == '' || value == null || value ==undefined) {
						return '';
					} else {
						return value.substr(0,19);
					}
				}
			},
			{field:'stockAreaName',title:'出货地点',width:60,align:'center'},
			{field:'transitDatetime',title:'出货时间',width:80,align:'center',
				formatter : function(value, row, index) {
					if (value == '' || value == null || value == undefined) {
						return '';
					} else {
						return value.substr(0,19);
					}
				}
			},
			{field:'deliverName',title:'快递公司',width:60,align:'center'},
			{field:'remark',title:'备注',width:200,align:'center'}
	    ] ]
	});
});

function exportFun() {
	var stockArea='${param.stockArea}';
	var startYear='${param.startYear}';
	var endYear='${param.endYear}';
	var startMonth='${param.startMonth}';
	var endMonth='${param.endMonth}';
	var startTime='${param.startTime}';
	var endTime='${param.endTime}';
	var type='${param.type}';
	var params="type=" + type 
		+ "&stockArea=" + stockArea 
		+ "&startYear=" + startYear 
		+ "&endYear=" + endYear 
		+ "&startMonth=" + startMonth 
		+ "&endMonth=" + endMonth 
		+ "&startTime=" + startTime 
		+ "&endTime=" + endTime;
	window.open("${pageContext.request.contextPath}/BIController/exportOrderDetail.mmx?" + params,"_blank");
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;" align="center">
		<h3>订单明细</h3>
		<table width="100%">
			<tr>
				<td align="left">
					订单明细列表：
				</td>
				<td align="right">
					<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">导出数据到Excel表</a>
				</td>
			</tr>
		</table>
	</div>
	<table id="orderDetailDataGrid"></table>
</body>
</html>