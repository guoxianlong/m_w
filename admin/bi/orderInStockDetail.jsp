<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<title>订单各环节时间节点信息</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript">
var datagrid;
$(function() {
	$('#stockArea').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBIStockArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	var stockArea='${param.stockArea}';
	var startYear='${param.startYear}';
	var endYear='${param.endYear}';
	var startMonth='${param.startMonth}';
	var endMonth='${param.endMonth}';
	var startTime='${param.startTime}';
	var endTime='${param.endTime}';
	var type='${param.type}';
	$("#tb input[id=stockArea]").combobox("setValue", stockArea);
	$("#tb input[id=startYear]").val(startYear);
	$("#tb input[id=endYear]").val(endYear);
	$("#tb input[id=startMonth]").val(startMonth);
	$("#tb input[id=endMonth]").val(endMonth);
	$("#tb input[id=startTime]").datebox("setValue", startTime);
	$("#tb input[id=endTime]").datebox("setValue", endTime);
	$("#tb input[id=stockArea1]").val($("#tb input[id=stockArea]").combobox("getValue"));
	$("#tb input[id=startYear1]").val($("#tb input[id=startYear]").val());
	$("#tb input[id=endYear1]").val($("#tb input[id=endYear]").val());
	$("#tb input[id=startMonth1]").val($("#tb input[id=startMonth]").val());
	$("#tb input[id=endMonth1]").val($("#tb input[id=endMonth]").val());
	$("#tb input[id=startTime1]").val($("#tb input[id=startTime]").datebox("getValue"));
	$("#tb input[id=endTime1]").val($("#tb input[id=endTime]").datebox("getValue"));
	$("#tb input[id=type]").val(type);
	datagrid = $('#orderInStockDetailDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/BIController/orderInStockDetail.mmx',
		queryParams : {
			stockArea:stockArea,
			startYear:startYear,
			endYear:endYear,
			startMonth:startMonth,
			endMonth:endMonth,
			startTime:startTime,
			endTime:endTime,
			type:type
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
			{field:'ckCode',title:'CK单号',width:60,align:'center'},
			{field:'sortingStart',title:'分拣开始时间',width:120,align:'center'},
			{field:'sortingEnd',title:'分拣完成时间',width:60,align:'center'},
			{field:'allocateStart',title:'分播开始时间',width:60,align:'center'},
			{field:'allocateEnd',title:'分播结束时间',width:60,align:'center'},
			{field:'reviewTime',title:'复核完成时间',width:60,align:'center'},
			{field:'associateTime',title:'交接出库时间',width:60,align:'center'}
	    ] ]
	});
});

function exportFun() {
	var stockArea=$("#tb input[id=stockArea1]").val();
	var startYear=$("#tb input[id=startYear1]").val();
	var endYear=$("#tb input[id=endYear1]").val();
	var startMonth=$("#tb input[id=startMonth1]").val();
	var endMonth=$("#tb input[id=endMonth1]").val();
	var startTime=$("#tb input[id=startTime1]").val();
	var endTime=$("#tb input[id=endTime1]").val();
	var type=$("#tb input[id=type]").val();
	var params="type=" + type 
		+ "&stockArea=" + stockArea 
		+ "&startYear=" + startYear 
		+ "&endYear=" + endYear 
		+ "&startMonth=" + startMonth 
		+ "&endMonth=" + endMonth 
		+ "&startTime=" + startTime 
		+ "&endTime=" + endTime;
	window.open("${pageContext.request.contextPath}/BIController/exportOrderInStockDetail.mmx?" + params,"_blank");
}

function searchFun() {
	if (!checkSubmit()) {
		return false;
	}
	$("#tb input[id=stockArea1]").val($("#tb input[id=stockArea]").combobox("getValue"));
	$("#tb input[id=startYear1]").val($("#tb input[id=startYear]").val());
	$("#tb input[id=endYear1]").val($("#tb input[id=endYear]").val());
	$("#tb input[id=startMonth1]").val($("#tb input[id=startMonth]").val());
	$("#tb input[id=endMonth1]").val($("#tb input[id=endMonth]").val());
	$("#tb input[id=startTime1]").val($("#tb input[id=startTime]").datebox("getValue"));
	$("#tb input[id=endTime1]").val($("#tb input[id=endTime]").datebox("getValue"));
	datagrid.datagrid("load", {
		stockArea:$("#tb input[id=stockArea]").combobox("getValue"),
		startYear:$("#tb input[id=startYear]").val(),
		endYear:$("#tb input[id=endYear]").val(),
		startMonth:$("#tb input[id=startMonth]").val(),
		endMonth:$("#tb input[id=endMonth]").val(),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		endTime:$("#tb input[id=endTime]").datebox("getValue"),
		type:	$("#tb input[id=type]").val()
	});
}

function checkSubmit() {
	var startYear=$("#tb input[id=startYear]").val();
	var endYear=$("#tb input[id=endYear]").val();
	var startMonth=$("#tb input[id=startMonth]").val();
	var endMonth=$("#tb input[id=endMonth]").val();
	var startTime=$("#tb input[id=startTime]").datebox("getValue");
	var endTime=$("#tb input[id=endTime]").datebox("getValue");
	if ($.trim(startTime) != "" && $.trim(endTime) != "") {
		var days = getValidateSubDays(endTime, startTime);
		if (days < 0) {
			$.messager.show({
				msg : "结束时间必须大于开始时间",
				title : '提示'
			});
			return false;
		}
		if (days>30){
			$.messager.show({
				msg : "日期时间段不得超过31天,请重新填写！",
				title : '提示'
			});
			return false;
		}
		return true;
	} 
	if ($.trim(startMonth) != "" && $.trim(endMonth) != "") {
		var days = getValidateSubDays(endMonth + "-01", startMonth + "-01");
		if (days < 0) {
			$.messager.show({
				msg : "结束年月必须大于开始年月",
				title : '提示'
			});
			return false;
		}
		if (days/30 >12) {
			$.messager.show({
				msg : "最多只能查12个月的数据",
				title : '提示'
			});
			return false;
		}
		return true;
	}
	if ($.trim(startYear) != "" && $.trim(endYear) != "") {
		var years = endYear - startYear;
		if (years < 0) {
			$.messager.show({
				msg : "结束年必须大于开始年",
				title : '提示'
			});
			return false;
		}
		if (years > 5) {
			$.messager.show({
				msg : "最多只能查5年的数据",
				title : '提示'
			});
			return false;
		}
		return true;
	}
	$.messager.show({
		msg : "请输入时间区间作为查询条件！",
		title : '提示'
	});
	return false;
}
</script>
</head>
<body>
	<table id="orderInStockDetailDataGrid"></table> 
	<div id="tb"  style="height: auto;display: none;" align="center">
		<input id="stockArea1" name="stockArea1"  type="hidden" />
		<input id="startYear1" name="startYear1" type="hidden" />
		<input id="endYear1" name="endYear1"  type="hidden" />
		<input id="startMonth1" name="startMonth1"  type="hidden" />
		<input id="endMonth1" name="endMonth1"  type="hidden" />
		<input id="startTime1" name="startTime1"  type="hidden"/>
		<input id="endTime1" name="endTime1"  type="hidden"/>
		<input id="type" name="type"  type="hidden"/>
		<fieldset>
		<h3>订单各环节时间节点信息</h3>
			<table class="tableForm">
				<tr align="center" >
					<th>仓库：</th>
					<td align="left">
						<input id="stockArea" name="stockArea" style="width: 116px;"/>
					</td>
					<th>年：</th>
					<td align="left"  colspan="3">
						<input id="startYear" name="startYear" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy'})" class="Wdate"/>
						--
						<input id="endYear" name="endYear" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy'})" class="Wdate"/>
					</td>
					<th>年月：</th>
					<td align="left"  colspan="3">
						<input id="startMonth" name="startMonth" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy-MM'})" class="Wdate"/>
						--
						<input id="endMonth" name="endMonth" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy-MM'})" class="Wdate"/>
					</td>
					<th>日期：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
				</tr>
			</table>
			<table align="right">
				<tr>
					<td>
						<mmb:permit value="2124">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFun();">查询</a>
						</mmb:permit>
					</td>
					<td>
						<mmb:permit value="2125">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">导出数据到Excel表</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>