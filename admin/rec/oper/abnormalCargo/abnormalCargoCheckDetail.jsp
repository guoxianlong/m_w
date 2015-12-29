<%@page import="adultadmin.util.StringUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<%
	String accId = request.getParameter("accId");
	String code = request.getParameter("code");
%>
<script type="text/javascript" charset="UTF-8">
function searchFun(){
	$('#datagrid').datagrid('load',{
		cargoCode : $('#tb input[name=cargoCode]').val(),
		status : $('#tb input[name=status]').val(),
		productCode : $('#tb input[name=productCode]').val(),
	});
}
function clearFun(){
	$('#tb input').val('');
	$('#datagrid').datagrid('load',{});
}
function generatedBsby(){
	$.ajax({
		url : '${pageContext.request.contextPath}/AbnormalCargoCheckController/generatedBsby.mmx',
		type : 'post',
		data : 'accId=<%=accId%>',
		dataType : 'json',
		cache : false,
		success : function(r){
			searchFun();
			$.messager.show({
				title : '提示',
				msg : r.msg
			});
		}
	});
}
function exporDetail(){
	var cargoCode = $('#tb input[name=cargoCode]').val();
	var status = $('#tb input[name=status]').val();
	var productCode = $('#tb input[name=productCode]').val();
	location.href = "${pageContext.request.contextPath}/AbnormalCargoCheckController/exportAbnormalCargoChecDetail.mmx?accId=<%=accId%>&cargoCode="+cargoCode + "&status=" + status + "&productCode=" + productCode;
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;">
		<fieldset>
		<legend>筛选</legend>
			<div align="left">
				 盘点计划：<font color="red"><%=StringUtil.checkNull(code) %></font><br><br>
				sku: <input id="productCode" name="productCode" >
				货位: <input id="cargoCode" name="cargoCode" >
				状态： <select id="status" name="status" class="easyui-combobox" editable="false"> 
						<option value=""></option>
			            <option value="0" >待一盘</option>
			            <option value="1" >待二盘</option>
			            <option value="2" >待终盘</option>
			            <option value="3" >盘点已完成</option>
			            <option value="4" >无效盘点</option>
				     </select>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun()">查询 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun()">清空 </a>
			</div>
		</fieldset>
		<div>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-undo" plain="true" onclick="generatedBsby()">生成报损报溢单 </a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-redo" plain="true" onclick="exporDetail()">导出盘点计划</a>
		</div>
	</div>    
    <table id="datagrid" class="easyui-datagrid" style="height:auto;width:auto; display: none;"
	url="${pageContext.request.contextPath}/AbnormalCargoCheckController/getAbnormalCargoChecDetailDatagrid.mmx?accId=<%=accId %>"  
	nowrap="false" border="false" idField="id" fit="true" fitColumns="true" title=""
	pageSize ="50"pageList="[ 50, 100, 150, 200, 250, 300, 350, 400, 450, 500 ]"
	toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true"> 
	<thead>
	<tr>
		<th field="id" width="20" align="center" hidden="true" checkbox="false">ID</th>
		<th field="productCode" width="90"  align="center">sku</th>
		<th field="cargoWholeCode" width="100"  align="center">货位</th>
		<th field="stockCount" width="50"  align="center">货位可用库存</th>
		<th field="stockLockCount" width="50"  align="center">货位冻结量</th>
		<th field="lockCount" width="50"  align="center">异常处理单冻结量</th>
		<th field="firstCheckCount" width="50"  align="center">一盘</th>
		<th field="firstCheckUserName" width="70"  align="center"data-options="
				formatter : function(value, rowData, rowIndex) 
					{if(value == null){return '-'}else{return value}}">一盘操作人</th>
		<th field="secondCheckCount" width="50"  align="center">二盘</th>
		<th field="secondCheckUserName" width="70"  align="center"data-options="
				formatter : function(value, rowData, rowIndex) 
					{if(value == null){return '-'}else{return value}}">二盘操作人</th>
		<th field="thirdCheckCount" width="50"  align="center">终盘</th>
		<th field="thirdCheckUserName" width="70"  align="center"data-options="
				formatter : function(value, rowData, rowIndex) 
					{if(value == null){return '-'}else{return value}}">终盘操作人</th>
		<th field="bsCount" width="50"  align="center" data-options="
				formatter : function(value, rowData, rowIndex) 
					{if(value == null){return '-'}else{return value}}">报损</th>
		<th field="byCount" width="50"  align="center"data-options="
				formatter : function(value, rowData, rowIndex) 
					{if(value == null){return '-'}else{return value}}">报溢</th>
		<th field="receipts_number" width="100"  align="center" data-options="
				formatter : function(value, rowData, rowIndex) 
					{if(value == null || value ==''){return '-'}else{return value}}">报损报溢单</th>
		<th field="statusName" width="70"  align="center">状态</th>
	</tr>
	</thead>
</table>
</body>
</html>