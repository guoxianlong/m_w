<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>待再次检测商品列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;

var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getDetectProductgrid.mmx',
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
	    pageList : [ 2, 6, 8, 12, 15, 18, 20, 22, 28, 30 ],
	    frozenColumns : [[
	                      {field:'id',title:'ID',hidden:true},
	          			 {field:'code',title:'售后处理单号',width:$(this).width() * 0.15,align:'center',formatter : function(value, rowData, rowIndex) {  
	          				return  "<a href='${pageContext.request.contextPath}/admin/afStock/afterSaleDetectProductInfo.jsp?id="+rowData.id+"' target='_blank'>" + value +"</a>";  
	          			}}
	  	                 ]],
	    columns:[[ 
			{field:'afterSaleOrderCode',title:'售后单号',width:$(this).width() * 0.15,align:'center', formatter : function(value, rowData, rowIndex) {  
			      return  "<a href='https://sales.ebinf.com/sale/admin/toEdit.mmx?id="+rowData.afterSaleOrderId+"' target='_blank'>" + value +"</a>";  
			}},  
			{field:'cargoWholeCode',title:'货位号',width:$(this).width() * 0.15,align:'center'},
			{field:'productName',title:'小店名称',width:$(this).width() * 0.25,align:'center'}  ,
			{field:'productCode',title:'商品编号',width:$(this).width() * 0.05,align:'center'},
			{field:'createUserName',title:'最后操作人',width:$(this).width() * 0.15,align:'center'},
			{field:'createDatetime',title:'最后操作时间',width:$(this).width() * 0.15,align:'center', formatter : function(value, rowData, rowIndex) {  
				  if(rowData.createDatetime ==null){
				    	return "";
				    }else{ 
				    	return  rowData.createDatetime.substring(0,16);  
				    }    
			} }
		]],
	
	}); 
	applyFrom = $('#applyFrom').form();
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		code : $('#tb input[id=code]').val(),
		productCode : $('#tb input[id=productCode]').val(),
		areaId : $('#areaId').combobox('getValue'),
		afterSaleOrderCode : $('#tb input[id=afterSaleOrderCode]').val(),
	});
}
function excel(){
	var afterSaleDetectCode =  $('#tb input[name=code]').val();
	var productCode =  $('#tb input[name=productCode]').val();
	var afterSaleOrderCode =  $('#tb input[name=afterSaleOrderCode]').val();
	var areaId = $('#areaId').combobox('getValue');
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelAgainDetectProduct.mmx?afterSaleDetectCode=" + 
		afterSaleDetectCode + "&productCode=" + productCode + "&afterSaleOrderCode=" + afterSaleOrderCode + "&areaId=" + areaId;
}
</script>
</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>待再次检测商品列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
				 
					<th >售后处理单号</th>
					<td align="left">
						<input id="code" name="code" /></td>
					<th >商品编号</th>
					<td align="left">
						<input id="productCode" name="productCode"  /></td>
				     <th >售后单号</th>
					<td align="left">
						<input id="afterSaleOrderCode" name="afterSaleOrderCode"  /></td>
					<th >售后地区</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px" /></td>	
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
					</td>
				</tr>
			</table>
		</form>
		</fieldset>
		
	</div>
</body>
</html>