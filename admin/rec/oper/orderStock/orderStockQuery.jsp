<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
$(function(){
	$('#area').combobox({
      	url : '${pageContext.request.contextPath}/OrderStockController/getDeptAreaComboBox.mmx',
      	valueField:'id',
		textField:'text' 
    });
});
function searchFun(){
	if(!checksubmit()){
		return;
	}
	$('#datagrid').datagrid('load',{
		startTime : $('#tb input[name=startTime]').val(),
		endTime : $('#tb input[name=endTime]').val(),
		area : $('#tb input[name=area]').val(),
	});
}
function checksubmit(){
	var startTime = $('#tb input[name=startTime]').val()
	var endTime =  $('#tb input[name=endTime]').val();
	var nDay_ms = 24*60*60*1000;
	var reg = new RegExp("-","g");
	var startDay = new Date(startTime.replace(reg,'/'));
	var endDay = new Date(endTime.replace(reg,'/'));
	var nDifTime = endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		$.messager.alert('提示', '起始日期不能大于结束日期!', 'error');
    	return false;
	}
    var nDifDay=Math.floor(nDifTime/nDay_ms);
    if(nDifDay > 7){
    	$.messager.alert('提示', '日期间隔不能大于7天!', 'error');
    	return false;
    }
	return true;
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;">
		<fieldset>
		<legend>筛选</legend>
			<div align="left">
				库地区: <input id="area" name="area" editable="false">
				复核时间：<input name="startTime" class="easyui-datebox" editable="false">-<input name="endTime" class="easyui-datebox" editable="false">
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun()">查 询 </a>
			</div>
		</fieldset>
	</div>    
    <table id="datagrid" class="easyui-datagrid" style="height:auto;width:auto; display: none;"
	url="${pageContext.request.contextPath}/OrderStockController/getOrderStockQueryDatagrid.mmx"  
	nowrap="false" border="false" idField="id" fit="true" fitColumns="true" title=""
	pageSize ="20"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
	toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true"> 
	<thead>
	<tr>
		<th field="id" width="20" align="center" hidden="true" checkbox="false">ID</th>
		<th field="deliverName" width="70"  align="center">快递公司</th>
		<th field="orderCode" width="100"  align="center">订单号</th>
		<th field="orderStockCode" width="100"  align="center">出库单号</th>
		<th field="packageCode" width="100"  align="center">包裹单号</th>
		<th field=weight width="70" align="center"
			data-options="formatter : function(value, rowData, rowIndex) {
			return rowData.weight/1000 + 'kg'}">重量</th>
			<th field="dDprice" width="70"  align="center"
			data-options="formatter : function(value, rowData, rowIndex) {
			return rowData.dDprice + '元'}">代收金额</th>
			
		<th field="checkDatetime" width="100" align="center" 
			data-options="formatter : function(value, rowData, rowIndex) {
			if(rowData.checkDatetime != null){return rowData.checkDatetime.substring(0,19);}}">复核时间</th>
		<th field="checkUserName" width="70" align="center">复核人</th>
	</tr>
	</thead>
</table>
</body>
</html>