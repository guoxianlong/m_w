<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<title>客户配件出库查询</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var flag = false;
var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/fittingStockCardController/getOutStockCardListForCustomer.mmx',
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
	    pageList : [ 20, 30, 40, 50 ],
	    frozenColumns : [[
	                    ]],
	    columns:[[ 
					{field:'datetime',title:'出库时间',width: 10 ,align:'center',
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
					{field:'fittingName',title:'配件名称',width: 20 ,align:'center'},
					{field:'billType',title:'相关单据类型',width: 20 ,align:'center'},					
					{field:'billCode',title:'相关单据编号',width: 20 ,align:'center'}, 
					{field:'username',title:'出库人',width: 10 ,align:'center'},
					{field:'inOutType',title:'出库类型',width: 10 ,align:'center'},
					{field:'count',title:'出库数量',width: 10 ,align:'center'}
		]],
	}); 
	applyFrom = $('#applyFrom').form();
	$('#parentId2').combobox({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleCatalogNames.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		onSelect : function(record){
			flag = true;
			$('#parentId3').combobox({
				url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleCatalogNames.mmx?perentId='+record.id,
				valueField : 'id',
				textField : 'text',
				editable : false
			});
		}
	
	});
});
function searchFun() {
	var startDate = $("#startDate").datebox('getValue');
	var endDate = $("#endDate").datebox('getValue');
	if (startDate != '' && endDate == '') {
		alert('请选择结束日期');
		return;
	}
	if (startDate == '' && endDate != '') {
		alert('请选择开始日期');
		return;
	}
	if (startDate != '' && endDate != '') {
		if(!validateDate(endDate, startDate)){		
			alert("结束时间必须大于开始时间");
			return;
		}
	}
	var parentId2 = $('#parentId2').combobox('getValue');
	var parentId3 = -1;
	if(flag){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	if(parentId2 == null || parentId2 == '')
		parentId2 = -1;
	if(parentId3 == null || parentId3 == '')
		parentId3 = -1;	
	//alert(parentId3);
	datagrid.datagrid('load', {
		fittingName : $('#tb input[id=fittingName]').val(),
	    parentId2 : parentId2,
		parentId3 : parentId3,
		code : $('#tb input[id=code]').val(),
		productName : $('#tb input[id=productName]').val(),
		startDate : startDate,
		endDate : endDate
	 });
}
</script>

</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>客户配件出库列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
					<th >配件分类</th>
					<td align="left">
		         		<input id="parentId2" name="parentId2" class="easyui-combobox" style="width: 120px;"/>&nbsp;&nbsp;
						<input id="parentId3" name="parentId3" class="easyui-combobox"  style="width: 120px;"/></td>
					<th >&nbsp;&nbsp;&nbsp;&nbsp;配件名称</th>
					<td align="left">
						<input id="fittingName" name="fittingName" style="width: 116px "/></td>
					<th >单据编号</th>
					<td align="left">
						<input id="code" name="code" style="width: 116px "/></td>
			
				</tr>
				<tr align="center" >
					<th >出库时间</th>
					<td align="left"> 
						<input class="easyui-datebox" editable="false" id="startDate" style="width: 120px;">--
						<input class="easyui-datebox" editable="false" id="endDate" style="width: 120px;"></td>						
					<th >适用商品名称</th>
					<td align="left">
						<input id="productName" name="productName" style="width: 116px "/></td>					
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
				
			</table>
		</form>
		</fieldset>
		
	</div>
</body>
</html>