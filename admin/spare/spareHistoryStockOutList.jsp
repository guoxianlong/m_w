<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>备用机历史出库单列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var flag_one;
var flag_two;
var firstSelect = true;
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/spareManagerController/getHistoryStockList.mmx',
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
					{field:'operateItemId',width:20,hidden:true}
	    ]],
	    columns:[[  
	    	{field:'operateItemCode',title:'返还快递单号',width:30,align:'center'},
	    	{field:'supplierName',title:'供应商名称',width:30,align:'center'},
	    	{field:'productCode',title:'商品编号',width:30,align:'center'},
	    	{field:'productOriname',title:'商品原名称',width:60,align:'center'},
	    	{field:'productName',title:'小店名称',width:60,align:'center'},
	    	{field:'count',title:'数量',width:15,align:'center'},
	    	{field:'areaName',title:'出库地区',width:30,align:'center'},
	        {field:'createDatetime',title:'出库时间',width:50,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}}, 
	        {field:'operateUsername',title:'出库人',width:30,align:'center'},
	        {field:'typeName',title:'出库类型',width:30,align:'center'},  
	        {field:'action',title:'操作',width:30,align:'center',
	        	formatter : function(value, row, index) {
	        		if(row.type==3){
	        			return '<a href="javascript:void(0);"  class="query" onclick="querySpare('+index+',3)"></a>';
	        		}else if(row.type==4){
	        			return '<a href="javascript:void(0);"  class="query" onclick="querySpare('+index+',2)"></a>';
	        		}else if(row.type==6){
	        			return '<a href="javascript:void(0);"  class="query" onclick="querySpare('+index+',5)"></a>';
	        		}
				}}
	    ]],
	    onLoadSuccess : function(data){
	    	$(".query").linkbutton({ 
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
	$("#stockInOutType").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getSpareStockCardType.mmx?flag=out',
		valueField:'id',
		textField:'text',
		delay:500
	});
	
	$('#parentId1').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		editable : false,
		onSelect : function(record){
			flag_one = true;
			$('#parentId2').combobox({
				url : '${pageContext.request.contextPath}/Combobox/getParentId2.mmx?parentId1=' + record.id,
				valueField : 'id',
				textField : 'text',
				editable : false,
				onSelect : function(record){
					flag_two = true;
					$('#parentId3').combobox({
						url : '${pageContext.request.contextPath}/Combobox/getParentId3.mmx?parentId2='+record.id,
						valueField : 'id',
						textField : 'text',
						editable : false
					});
				}
			});
		} 
    });
});


function searchFun() {
	var result = checkSubmit();
	var parentId2;
	var parentId3;
	if(flag_one){
		parentId2 = $('#parentId2').combobox('getValue');
	}
	if(flag_two){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	if(result){
		datagrid.datagrid('load', {
			areaId : $("#areaId").combobox("getValue"),
			operateItemCode : $("#operateItemCode").val(),
			productCode : $("#productCode").val(),
			spareCode : $("#spareCode").val(),
			backSupplierId : $('#backSupplierId').combobox('getValue'),
			startTime :  $('#startTime').datebox('getValue'),
			endTime : $('#endTime').datebox('getValue'),
			parentId1 : $('#parentId1').combobox('getValue'),
			parentId2 : parentId2,
			parentId3 : parentId3,
			stockInOutType : $("#stockInOutType").combobox('getValue'),
			type : '3,4,6'
		});
	}
}

function checkSubmit(){
	var flag = false;
	if($.trim($("#operateItemCode").val())!=""){
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
	if($("#stockInOutType").combobox("getValue")!="-1"){
		flag = true;
	}
	if($("#parentId1").combobox("getValue")!=""){
		flag = true;
	}
	if(flag_one){
		if($("#parentId2").combobox("getValue")!=""){
			flag = true;
		}
	}
	if(flag_two){
		if($("#parentId3").combobox("getValue")!=""){
			flag = true;
		}
	}
	var startTime = $("#startTime").datebox('getValue');
	var endTime = $("#endTime").datebox('getValue');
	if($.trim(startTime)!="" && $.trim(endTime)!=""){
		flag = true;
	}
	if(flag){
		var days = getValidateSubDays(endTime,startTime);
		if (days < 0) {
			$.messager.show({
				msg : "生成入库开始时间不能早于结束时间!",
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

function querySpare(index,type){
	if (index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	window.location.href = "${pageContext.request.contextPath}/admin/spare/spareList.jsp?type="+type+"&operateItemId=" + row.operateItemId;
}

</script>
</head>
<body>
	<table id="datagrid"></table> 
	<div id="tb" style="height: auto;">
		<fieldset>
			<legend>备用机历史出库查询</legend>
			<table class="" >
				<tr align="center" >
				  <th >返还快递单号</th>
				  <td>
				  		<input type="text" id="operateItemCode" name="operateItemCode" style="width:116px"/>
				  		<input type="hidden" id="query" value="true"/>
				  	</td>
					<th align="right">商品分类</th>
					<td colspan="1">
						<input id="parentId1" name="parentId1" style="width:116px"/>
						<input id="parentId2" name="parentId2" style="width:116px"/>
						<input id="parentId3" name="parentId3"  style="width:116px"/>
					</td>	
					<th >商品编号</th>
					<td align="left">
						<input id="productCode" name="productCode" style="width:116px" /></td>
					<th >备用机号</th>
					<td align="left">
						<input id="spareCode" name="spareCode" style="width:116px" /></td>
				</tr>
				<tr align="center" >	
					<th>供应商</th>
					<td align="left" >
						<input type="text" id="backSupplierId" name="backSupplierId"/>
					</td>
				  <th >出库时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" />
						--&nbsp;<input type="text"  id="endTime" class="easyui-datebox" style="width:121px"/></td>
					<th>出库类型</th>
					<td>
						<input type="text" id="stockInOutType" readonly="true" style="width:116px"/>
					</td>
					<th>出库地区</th>
					<td>
						<input type="text" id="areaId" readonly="true" style="width:116px"/>
					</td>
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>