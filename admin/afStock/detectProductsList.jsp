<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>已检测商品列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#detectProductsDatagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getDetectProductsDatagrid.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50 ],
	    columns:[[  
			{field:'productName',title:'商品名称',width:80,align:'center'},
			{field:'productCode',title:'商品编号',width:60,align:'center'},
			{field:'parentId1Name',title:'一级分类',width:40,align:'center'},
			{field:'code',title:'售后处理单号',width:60,align:'center'},
			{field:'afterSaleOrderCode',title:'售后单号',width:60,align:'center',
				formatter : function(value, row, index) {
        			return '<a href="javascript:void(0);" onclick="openAfterSaleOrder(\'' + row.afterSaleOrderId + '\')">' +value + '</a>';
				}
			},
			{field:'packageCode',title:'包裹单号',width:60,align:'center'},
			{field:'createDatetime',title:'检测时间',width:60,align:'center',
				formatter : function(value, row, index) {
        			return value != null && value != undefined ? value.substring(0, 19) : '';
				}
			},
			{field:'createUserName',title:'检测人',width:40,align:'center'},
			{field:'action',title:'操作',width:60,align:'center',
	        	formatter : function(value, row, index) {
	        		return '<a href="javascript:void(0);" class="printbutton" onclick="afterSaleDetectProductCodePrint(\''+row.code+'\')"></a>';
				}
			}
	    ] ],
		onLoadSuccess : function(data) {
			if (data.footer[0]) {
				$(".printbutton").linkbutton(
					{ 
						text:"打印售后处理单号",
						plain:true,
						iconCls:'icon-print'
					}
				);
			}
		}
	}); 
	$('#parentId1').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		editable : false,
    });
});

function searchFun() {
	datagrid.datagrid("load", {
		packageCode:$("#tb input[id=packageCode]").val(),
		afterSaleOrderCode:$("#tb input[id=afterSaleOrderCode]").val(),
		productCode:$("#tb input[id=productCode]").val(),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		endTime:$("#tb input[id=endTime]").datebox("getValue"),
		createUserName:$("#tb input[id=createUserName]").val(),
		parentId1 : $('#parentId1').combobox('getValue'),
	});
}
function openAfterSaleOrder(id) {
	window.open("https://sales.ebinf.com/sale/admin/toEdit.mmx?id=" + id, "_blank");
}
function afterSaleDetectProductCodePrint(code) {
	window.open('${pageContext.request.contextPath}/admin/AfStock/afterSaleDetectProductCodePrint.mmx?code='+code,"_blank");
}

function excel(){
	var startTime= $('#startTime').datebox('getValue');
	var endTime=$('#endTime').datebox('getValue');
	var afterSaleOrderCode = $("#tb input[id=afterSaleOrderCode]").val();
	var packageCode = $("#tb input[id=packageCode]").val();
	var productCode = $("#tb input[id=productCode]").val();
	var createUserName = $("#tb input[id=createUserName]").val();
	var parentId1 = $('#parentId1').combobox('getValue');
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelDetectProducts.mmx?startTime=" 
			+ startTime + "&endTime=" + endTime + "&afterSaleOrderCode=" + afterSaleOrderCode + "&packageCode=" + packageCode
			+ "&productCode=" + productCode + "&createUserName=" + createUserName + "&parentId1=" + parentId1;
}
</script>
</head>
<body>
	<table id="detectProductsDatagrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<input type="hidden" name="id" value="-1"/>
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>商品编号：</th>
					<td align="left">
						<input id="productCode" name="productCode" style="width: 116px;"/>
					</td>
					<th>售后单号：</th>
					<td align="left">
						<input id="afterSaleOrderCode" name="afterSaleOrderCode" style="width: 116px;"/>
					</td>
					<th>包裹单号：</th>
					<td align="left">
						<input id="packageCode" name="packageCode" style="width: 116px;"/>
					</td>
				</tr>
				<tr>
					<th>检测时间：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
					<th>检测人：</th>
					<td align="left">
						<input id="createUserName" name="createUserName" style="width: 116px;"/>
					</td>
					<th>一级分类：</th>
					<td align="left">
						<input id="parentId1" name="parentId1"  editable="false" style="width: 116px;"/>
					</td>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-search',plain:true" onclick="searchFun();" href="javascript:void(0);">查询</a>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>