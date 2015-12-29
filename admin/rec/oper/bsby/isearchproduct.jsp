<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*" %>
<%
	int productId = StringUtil.toInt(request.getParameter("productId"));
	int stockType = StringUtil.toInt(request.getParameter("stockType"));
	int stockAreaId = StringUtil.toInt(request.getParameter("stockAreaId"));
	int operType = StringUtil.toInt(request.getParameter("operType"));
%>
<!DOCTYPE html>
<html>
<head>
<title>报损报溢搜索产品</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	initProductDataGrid ();
});

function initProductDataGrid () {
	$('#productDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/ByBsController/searchProductList.mmx',
		queryParams : {
			name : '<%=request.getParameter("name")%>',
			stockType : '<%=stockType%>',
			stockAreaId : '<%=stockAreaId%>',
			operType : '<%=operType%>'
		},
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		columns : [ [ {
			field : 'code',
			title : '编号',
			width : 130,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'" >'+value+'</a>'; 
			}
		}, {
			field : 'name',
			title : '小店名称',
			width : 140,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'" >'+value+'</a>'; 
			}
		}, {
			field : 'oriname',
			title : '原名称',
			width : 160,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'" >'+value+'</a>'; 
			}
		}, {
			field : 'price',
			title : '价格',
			width : 100,
			halign : 'center',
			align : 'right',
			formatter : function(value, row, index) {
				return value + '元';
			}
		}, {
			field : 'proxyName',
			title : '代理商',
			width : 120,
			halign : 'center',
			align : 'left'
		}, {
			field : 'statusName',
			title : '状态',
			width : 100,
			align : 'center'
		},{
			field : 'fcStock',
			title : '芳村库存',
			width : 100,
			halign : 'center',
			align : 'right'
		}, {
			field : 'zcStock',
			title : '增城库存',
			width : 100,
			halign : 'center',
			align : 'right'
		},  {
			field : 'cargoCode',
			title : '源货位',
			width : 250,
			align : 'center',
			editor : {
				type : 'combobox',
				options:{
					valueField : 'id',
					textField : 'text',
					panelHeight : 'auto',
					editable : false
				}
			}
		}, {
			field : 'action',
			title : '操作',
			width : 150,
			align : 'center',
			formatter : function(value, row, index) {
				return '<a href="#" class="addProductButton" onclick="addProductMethod('+index+');">添加产品</a>';
			}
		} ] ],
		onLoadSuccess : function(data) {
			//改变datagrid中按钮的class
			$(".addProductButton").linkbutton(
				{ 
					text:'添加产品', 
					plain:true, 
					iconCls:'icon-add' 
				}
			);
			var productDataGrid = $("#productDataGrid").datagrid("getRows");
			var length = productDataGrid.length;
			for (var i = 0 ; i < length; i ++) {
				$("#productDataGrid").datagrid("beginEdit", i);
				var row = productDataGrid[i];
				var ed = $("#productDataGrid").datagrid("getEditor", {index: i, field:"cargoCode"});
				var data = $.parseJSON(row.productCargoCode);
				$(ed.target).combobox("loadData", data);
			}
		}
	});
};

function addProductMethod(index) {
	$("#productDataGrid").datagrid("endEdit", index);
	var productDataGrid = $("#productDataGrid").datagrid("getRows")[index];
	var code = productDataGrid.code;
	var cargoCode = productDataGrid.cargoCode;
	$("#productDataGrid").datagrid("beginEdit", index);
	$(".addProductButton").linkbutton(
		{ 
			text:'添加产品', 
			plain:true, 
			iconCls:'icon-add' 
		}
	);
	var productDataGrid = $("#productDataGrid").datagrid("getRows");
	var row = productDataGrid[index];
	var ed = $("#productDataGrid").datagrid("getEditor", {index: index, field:"cargoCode"});
	var data = $.parseJSON(row.productCargoCode);
	$(ed.target).combobox("loadData", data);
	parent.addproduct(code,cargoCode);
	return true;
}
</script>
</head>
<body>
	<table id="productDataGrid"></table>
</body>
</html>