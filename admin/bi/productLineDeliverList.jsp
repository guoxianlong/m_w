<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
    $('#stockArea').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBIStockArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
    $('#chinaArea').combotree({
		url : '${pageContext.request.contextPath}/Combobox/getChinaArea.mmx',
		valueField : 'id',
		textField : 'text',
		multiple: true, 
		editable : false,
		panelHeight : 'auto'
	});
    $('#provinces').combotree({
		url : '${pageContext.request.contextPath}/Combobox/getProvinces.mmx',
		valueField : 'id',
		textField : 'text',
		multiple: true, 
		editable : false,
		panelHeight : 'auto'
	});
});

function searchFun() {
	if (!
	checkSubmit()) {
			return false;
		}
		loadInfo();
	}

	function loadInfo() {
		var provinces = $("#provinces").combotree("getValues");
		var chinaArea = $("#chinaArea").combotree("getValues");
		$
				.ajax({
					url : '${pageContext.request.contextPath}/BIController/getProductLineDeliverDatagrid.mmx',
					data : {
						stockArea : $("#tb input[id=stockArea]").combobox(
								"getValue"),
						provinces : provinces.join(','),
						chinaArea : chinaArea.join(','),
						startYear : $("#tb input[id=startYear]").val(),
						startMonth : $("#tb input[id=startMonth]").val(),
						startTime : $("#tb input[id=startTime]").datebox(
								"getValue"),
						endTime : $("#tb input[id=endTime]")
								.datebox("getValue")
					},
					dataType : 'json',
					cache : false,
					success : function(result) {
						try {
							var data = result;
							var columns = new Array();
							columns[0] = new Array();
							columns[0].field = 'stockArea';
							columns[0].title = '地区';
							columns[0].width = document.body.clientWidth * 0.2;
							columns[0].align = 'center';
							var x = 1
							for (i in data[0]) {
								(function() {
									var t = i;
									columns[x] = new Array();
									columns[x].field = 'productLineList' + i;
									columns[x].title = data[0][i];
									columns[x].width = document.body.clientWidth * 0.16;
									columns[x].align = 'center';
									columns[x].formatter = function(value, row,
											index) {
										if (row.stockArea == "总量"
												|| row.stockArea == "产品线发货占比") {
											return value;
										} else {
											var stockArea = row.area;
											return '<a href="javascript:void(0);" onclick="toProductLineDeliverDetail(\''
													+ t
													+ '\',\''
													+ stockArea
													+ '\')">' + value + '</a>';
										}
									};
								})();
								x++;
							}
							//处理返回结果，并显示数据表格
							var options = {
								toolbar : '#tb',
								idField : 'id',
								fit : true,
								fitColumns : true,
								striped : true,
								nowrap : true,
								loadMsg : '正在努力为您加载..',
								rownumbers : true,
								singleSelect : true,
								showFooter : true,
								columns : [ columns ]
							};
							datagrid = $("#inStockAgingDatagrid");
							datagrid.datagrid(options);//根据配置选项，生成datagrid
							datagrid.datagrid("loadData", data[1]); //载入本地json格式的数据
							datagrid.datagrid("reloadFooter", data[2]);
						} catch (e) {
						}
					}
				});
	}

	function checkSubmit() {
		var startYear = $("#tb input[id=startYear]").val();
		var startMonth = $("#tb input[id=startMonth]").val();
		var startTime = $("#tb input[id=startTime]").datebox("getValue");
		var endTime = $("#tb input[id=endTime]").datebox("getValue");
		if ($.trim(startYear) != "") {
			return true;
		}
		if ($.trim(startMonth) != "") {
			return true;
		}
		if ($.trim(startTime) != "" && $.trim(endTime) != "") {
			var days = getValidateSubDays(endTime, startTime);
			if (days < 0) {
				$.messager.show({
					msg : "结束时间必须大于开始时间",
					title : '提示'
				});
				return false;
			}
			if (days > 30) {
				$.messager.show({
					msg : "日期时间段不得超过31天,请重新填写！",
					title : '提示'
				});
				return false;
			}
			return true;
		}
		if ($.trim(startYear) != "") {
			return true;
		}
		$.messager.show({
			msg : "请输入时间区间作为查询条件！",
			title : '提示'
		});
		return false;
	}

	function getEod() {
		var date = new Date();
		var i_milliseconds = date.getTime();
		i_milliseconds -= 1000 * 60 * 60 * 24;
		var t_date = new Date();
		t_date.setTime(i_milliseconds);
		var i_year = t_date.getFullYear();
		var i_month = ("0" + (t_date.getMonth() + 1)).slice(-2);
		var i_day = ("0" + t_date.getDate()).slice(-2);
		return i_year + "-" + i_month + "-" + i_day;
	}
	function exportFun() {
		var provinces = $("#provinces").combotree("getValues");
		var chinaArea = $("#chinaArea").combotree("getValues");
		var stockArea = $("#tb input[id=stockArea]").combobox("getValue");
		var startYear = $("#tb input[id=startYear]").val();
		var startMonth = $("#tb input[id=startMonth]").val();
		var startTime = $("#tb input[id=startTime]").datebox("getValue");
		var endTime = $("#tb input[id=endTime]").datebox("getValue");
		var type = $("#tb input[id=type]").val();
		if (!
				checkSubmit()) {
						return false;
					}
		var params = "provinces=" + provinces + "&chinaArea=" + chinaArea
				+ "&stockArea=" + stockArea + "&startYear=" + startYear
				+ "&startMonth=" + startMonth + "&startTime=" + startTime
				+ "&endTime=" + endTime;
		window
				.open(
						"${pageContext.request.contextPath}/BIController/exportProductLineDeliverList.mmx?"
								+ params, "_blank");
	}
	function toProductLineDeliverDetail(productLine, stock) {
		var provinces = $("#provinces").combotree("getValues");
		var chinaArea = $("#chinaArea").combotree("getValues");
		var stockArea = $("#tb input[id=stockArea]").combobox("getValue");
		var startYear = $("#tb input[id=startYear]").val();
		var startMonth = $("#tb input[id=startMonth]").val();
		var startTime = $("#tb input[id=startTime]").datebox("getValue");
		var endTime = $("#tb input[id=endTime]").datebox("getValue");
		var type = $("#tb input[id=type]").val();
		var params = "provinces=" + provinces + "&chinaArea=" + chinaArea
				+ "&stockArea=" + stock + "&startYear=" + startYear
				+ "&startMonth=" + startMonth + "&startTime=" + startTime
				+ "&endTime=" + endTime + "&productLine=" + productLine;
		window.open(
				"${pageContext.request.contextPath}/admin/bi/productLineDeliverDetail.jsp?"
						+ params, "_blank");
	}
</script>
</head>
<body>
	<table id="inStockAgingDatagrid"></table> 
	<div id="tb"  style="height: auto;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>仓库：</th>
					<td align="left">
						<input id="stockArea" name="stockArea" style="width: 116px;"/>
					</td>
					<th>年：</th>
					<td align="left"  colspan="3">
						<input id="startYear" name="startYear" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy'})" class="Wdate"/>
					</td>
					<th>年月：</th>
					<td align="left"  colspan="3">
						<input id="startMonth" name="startMonth" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy-MM'})" class="Wdate"/>
					</td>
					<th>日期：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
					<td>
						<mmb:permit value="2126">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFun();">查询</a>
						</mmb:permit>
					</td>
				</tr>
				<tr align="center" >
					<th>收货地区</th>
					<td align="left">
						<input id="chinaArea" name="chinaArea" style="width: 116px;"/>
					</td>
					<th>选择省份</th>
					<td align="left"  colspan="3">
						<input id="provinces" name="provinces" style="width: 180px;"/>
					</td>
					<th></th>
					<td align="left"  colspan="3">
					</td>
					<th></th>
					<td align="left"  colspan="3">
					</td>
					<td>
						<mmb:permit value="2126">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">导出数据到Excel表</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>