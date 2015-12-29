<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;
$(function(){
	var myDate = new Date();
	var year = myDate.getFullYear();   
	var month = myDate.getMonth() + 1; 
	var day = myDate.getDate();
	$('#endTime').datebox("setValue",year + "-" + month + "-" + day);
	myDate.setDate(myDate.getDate() - 15);
	$('#startTime').datebox("setValue", myDate.getFullYear() + "-" + (myDate.getMonth() + 1) + "-" + myDate.getDate());
	$('#stockArea').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getStockoutAvailableArea2.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	}
	);
	$('#deliver').combobox({
		url : '${pageContext.request.contextPath}/SalesReturnController/getDeliverJSON.mmx',
		valueField : 'id',
		textField : 'name',
		editable : false
	}
	);
	$('#productLine').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getOrderType.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	}
	);
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/EffectController/getCommonTypeList.mmx?type=3',
		queryParams: {
			startTime:$('#startTime').datebox("getValue"),
			endTime:$('#endTime').datebox("getValue"),
			date : 1
		},
		toolbar : '#toolbar',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 60,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    columns:[[  
	        {field:'stock',title:'发货仓',width:30,align:'center'},
	        {field:'deliver',title:'快递公司',width:30,align:'center'},
	        {field:'sheng',title:'省',width:30,align:'center'},
	        {field:'city',title:'市',width:30,align:'center'},
	        {field:'area',title:'区',width:30,align:'center'},
	        {field:'street',title:'地址级别（县、乡、村）',width:30,align:'center'},
	        {field:'chanpinxian',title:'产品线',width:30,align:'center'}, 
	        {field:'zhengti',title:'整体客诉率',width:20,align:'center'},  
	        {field:'chaoshi',title:'超时客诉率',width:30,align:'center'}, 
	        {field:'taidu',title:'态度客诉率',width:20,align:'center'}, 
	        {field:'yuanze',title:'原则客诉率',width:20,align:'center'}, 
	        //{field:'kesu',title:'客诉平均处理时长',width:25,align:'center'}
	    ]]
	}); 
});
function searchFun() {
	datagrid.datagrid('load', {
		param : $("input[name='param']:checked").map(function () {return $(this).val();}).get().join(','),
		startTime : $('#startTime').datebox("getValue"),
		endTime : $('#endTime').datebox("getValue")
	});
}
function exportFun() {
	$("#searchForm").submit();
}
</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
			<form id="searchForm" action="${pageContext.request.contextPath}/EffectController/portExcel.mmx?type=3" method="post">
			<table>
				<tr>
					<td>
						仓:<input name='param' id='condition' value='d.name' type="checkbox"/>&nbsp;
						快递公司:<input name='param' id='condition' value='c.name' type="checkbox"/>&nbsp;
						省:<input name='param' id='condition' value='f.name' type="checkbox"/>&nbsp;
						市:<input name='param' id='condition' value='g.city' type="checkbox"/>&nbsp;
						区:<input name='param' id='condition' value='h.area' type="checkbox"/>&nbsp;
						乡/镇/街:<input name='param' id='condition' value='i.street' type="checkbox"/>&nbsp;
						产品线:  <input name='param' id='condition' value='k.name' type="checkbox"/>&nbsp;
						出库时间:<input id="startTime" name="startTime"   class="easyui-datebox" editable="false" style="width: 120px;" />至
							  <input id="endTime" name="endTime"   class="easyui-datebox" editable="false" style="width: 120px;" />
						&nbsp;<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						<mmb:permit value="3069">
						&nbsp;<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">Excel导出</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
			</form>
	</div>
	<table id="datagrid"></table> 
</body>
</html>