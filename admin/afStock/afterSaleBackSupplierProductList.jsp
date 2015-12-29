<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>返厂清单列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getBackSupplierProductList.mmx',
	    toolbar : '#tb',
	    idField : 'packageCode',
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
					{field:'packageCode',title:'运输单号',width:20,hidden:true}
	    ]],
	    columns:[[  
	        {field:'sendTime',title:'返厂时间',width:30,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	        {field:'supplierName',title:'返厂厂商名称',width:30,align:'center'},
	        {field:'sendName',title:'返厂人员',width:30,align:'center'},  
	        {field:'productCount',title:'清单包含商品数量',width:15,align:'center'},
	        {field:'action',title:'操作',width:15,align:'center',
	        	formatter : function(value, row, index) {
        			return '<a href="javascript:void(0);"  class="querySupplierProduct" onclick="querySupplierProduct('+index+')"></a>';
				}}
	    ]],
	    onLoadSuccess : function(data){
	    	$(".querySupplierProduct").linkbutton(
					{ 
						text:'查看返厂清单'
					}
				);
	    }
	}); 
	$('#supplierQ').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
      	valueField:'id',
		textField:'text' 
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		startTime : $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		supplierId : $('#supplierQ').combobox('getValue'),
		senderName : $('#senderName').val()
	});
}

function querySupplierProduct(index){
	if(index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	window.open('${pageContext.request.contextPath}/admin/AfStock/printRepairListByPackageCode.mmx?mark=1&packageCode=' + row.packageCode);
}
</script>
</head>
<body>
	<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>返厂清单列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
				  <th >厂商寄回时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" name="startTime"/>
						--<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:121px"/></td>
					<th>返厂人员</th>
					<td align="left">
						<input id="senderName" name="senderName" style="width:116px" /></td>
					<th >返厂厂商</th>
					<td align="left">
						<input id="supplierQ" name="supplierId" style="width:116px" /></td>
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查找</a>
					</td>
				</tr>
			</table>
		</form>
		</fieldset>
	</div>
</body>
</html>