<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>备用机入库单列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var firstSelect = true;
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/spareManagerController/getSpareStockInList.mmx',
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
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
					{field:'id',width:20,hidden:true}
	    ]],
	    columns:[[  
	    	{field:'code',title:'入库单号',width:30,align:'center'},
	    	{field:'supplierName',title:'供应商名称',width:30,align:'center'},
	    	{field:'productCode',title:'商品编号',width:30,align:'center'},
	    	{field:'productOriname',title:'商品原名称',width:60,align:'center'},
	    	{field:'count',title:'数量',width:15,align:'center'},
	    	{field:'areaName',title:'入库地区',width:30,align:'center'},
	    	{field:'statusName',title:'状态',width:30,align:'center'},
	        {field:'createDatetime',title:'生成时间',width:50,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}}, 
        	 {field:'auditDatetime',title:'审核时间',width:50,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},   
	        {field:'createUserName',title:'生成人',width:30,align:'center'},
	        {field:'auditUserName',title:'审核人',width:30,align:'center'},  
	        {field:'auditRemark',title:'审核意见',width:60,align:'center'},
	        {field:'action',title:'操作',width:30,align:'center',
	        	formatter : function(value, row, index) {
	        		if(row.status==0){
	        			return '<a href="javascript:void(0);"  class="querySupplierProduct" onclick="auditStockIn('+row.id+')"></a>';
	        		}else{
	        			return '<a href="javascript:void(0);"  class="querySupplierProduct" onclick="queryStockIn('+row.id+')"></a>';
	        		}
        			
				}}
	    ]],
	    onLoadSuccess : function(data){
	    	$(".querySupplierProduct").linkbutton({ 
				text:'查看'
			});
			if(data.rows.length==0){
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
		valueField:'id',
		textField:'text',
		delay:500
	});
	
	$("#areaId").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getSpareArea.mmx',
		valueField:'id',
		textField:'text',
		delay:500
	});
});


function searchFun() {
	var result = checkSubmit();
	if(result){
		datagrid.datagrid('load', {
			areaId : $("#areaId").combobox("getValue"),
			stockInCode : $("#stockInCode").val(),
			productCode : $("#productCode").val(),
			spareCode : $("#spareCode").val(),
			backSupplierId : $('#backSupplierId').combobox('getValue'),
			status : $("#status").val(),
			createStartTime : $('#createStartTime').datebox('getValue'),
			createEndTime : $('#createEndTime').datebox('getValue'),
			auditStartTime : $('#auditStartTime').datebox('getValue'),
			auditEndTime : $('#auditEndTime').datebox('getValue')
		});
	}
}

function checkSubmit(){
	var flag = false;
	if($.trim($("#stockInCode").val())!=""){
		flag = true;
	}
	if($.trim($("#productCode").val())!=''){
		flag = true;
	}
	if($.trim($("#spareCode").val())!=''){
		flag = true;
	}
	if($("#backSupplierId").combobox("getValue")!="-1"){
		flag = true;
	}
	var status = checkValidateAndValus("status");
	if(status!=""){
		$("#status").val(status.substr(0,status.length-1));
		flag = true;
	}
	var createStartTime = $("#createStartTime").datebox('getValue');
	var createEndTime = $("#createEndTime").datebox('getValue');
	if($.trim(createStartTime)!="" && $.trim(createEndTime)!=""){
		flag = true;
	}
	var auditStartTime = $("#auditStartTime").datebox('getValue');
	var auditEndTime = $("#auditEndTime").datebox('getValue');
	if($.trim(auditStartTime)!="" && $.trim(auditEndTime)!=""){
		flag = true;
	}
	if(flag){
		var days = getValidateSubDays(createEndTime, createStartTime);
		if (days < 0) {
			$.messager.show({
				msg : "生成入库单查询的开始时间不能早于结束时间!",
				title : '提示'
			});
			return false;
		}else{
			flag = true;
		}
		days = getValidateSubDays(auditEndTime,auditStartTime);
		if (days < 0) {
			$.messager.show({
				msg : "审核入库单查询的开始时间不能早于结束时间!",
				title : '提示'
			});
			return false;
		}else{
			flag = true;
		}
	}else{
		$.messager.show({
			msg : '请选择出入库地区以外的其他查询条件!',
			title : '提示'
		});
		return flag;
	}
	return flag;
}

function queryStockIn(spareStockinId){
	window.location.href = "${pageContext.request.contextPath}/admin/spare/spareStockinProductList.jsp?audit=false&stockinId=" + spareStockinId;
}
function auditStockIn(spareStockinId){
	window.location.href = "${pageContext.request.contextPath}/admin/spare/spareStockinProductList.jsp?audit=true&stockinId=" + spareStockinId;
}
</script>
</head>
<body>
	<table id="datagrid"></table> 
	<div id="tb" style="height: auto;">
		<fieldset>
			<legend>备用机入库单列表</legend>
			<table class="" >
				<tr align="center" >
				  <th >入库地区</th>
				  <td align="left"><input type="text" id="areaId" name="areaId" style="width:116px"/></td>
					<th>入库单号</th>
					<td align="left">
						<input id="stockInCode" name="stockInCode" style="width:116px" /></td>
					<th >商品编号</th>
					<td align="left">
						<input id="productCode" name="productCode" style="width:116px" /></td>
				</tr>
				<tr align="center" >
					<th >备用机号</th>
					<td align="left">
						<input id="spareCode" name="spareCode" style="width:116px" /></td>
					<th>供应商</th>
					<td align="left" >
						<input type="text" id="backSupplierId" name="backSupplierId"/>
					</td>
					<th>入库单状态</th>
					<td align="left">
						<input type="checkbox" name="status" value="0"/>待审核
						<input type="checkbox" name="status" value="2"/>审核未通过
						<input type="checkbox" name="status" value="1"/>已完成
						<input type="hidden" id="status" value=""/>
					</td>
				</tr>
				<tr>
					<th >入库单生成时间</th>
					<td align="left">
						<input type="text" id="createStartTime" class="easyui-datebox" style="width:121px" />
						--&nbsp;<input type="text"  id="createEndTime" class="easyui-datebox" style="width:121px"/></td>
					<th>入库单审核时间</th>
					<td align="left">
							<input type="text" id="auditStartTime" class="easyui-datebox" style="width:121px"/>
						--&nbsp;<input type="text"  id="auditEndTime" class="easyui-datebox" style="width:121px"/></td>
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>