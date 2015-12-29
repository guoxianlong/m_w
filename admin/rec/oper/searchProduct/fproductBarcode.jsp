<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	voUser loginUser = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = loginUser.getGroup();
%>
<!DOCTYPE html>
<html>
<head>
<title>批量查询产品条形码</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	document.getElementById("barcodes").focus();
	initProductDataGrid ();
});

function initProductDataGrid () {
	$('#productDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/FProductBarcodeController/seracheProductBarcodes.mmx',
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		toolbar : '#tb',
		columns : [ [ {
			width : 40,
			checkbox: true
		}, {
			field : 'code',
			title : '编号',
			width : 80,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'&simple=1" >'+value+'</a>'; 
			}
		}, {
			field : 'barcode',
			title : '产品条码',
			width : 80,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				if (row.productBarcodeVO != null && row.productBarcodeVO.barcode != null) {
					return row.productBarcodeVO.barcode;
				} else {
					return "";
				}
			}
		},{
			field : 'name',
			title : '小店名称',
			width : 100,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'" >'+value+'</a>'; 
			}
		}, {
			field : 'oriname',
			title : '原名称',
			width : 100,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'" >'+value+'</a>'; 
			}
		}, {
			field : 'price',
			title : '价格',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return value+'元'; 
			}
		},{
			field : 'groupBuyPrice',
			title : '团购价格',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return value+'元'; 
			}
		}, {
			field : 'price2',
			title : '市场价',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return value+'元'; 
			}
		}, {
			field : 'statusName',
			title : '状态',
			width : 60,
			align : 'center'
		}, {
			field : 'commentCount',
			title : '查看评论',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/comments.do?productId='+row.id+'" >查看('+value+')</a>'; 
			}
		}, {
			field : 'chakan',
			title : '多图',
			width : 50,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/productpics.do?productId='+row.id+'" >查看</a>'; 
			}
		}, {
			field : 'pic2',
			title : '大图',
			width : 50,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				if (row.pic2 == null || row.pic2 == 'undefined' || row.pic2.length < 3 ){
					return "无";
				} else {
					return "<a href='"+row.fullPic2+"' ><image border=0 src='"+row.fullPic2+"' width=20 height=20></a>"; 
				}
			}
		}, {
			field : 'canBeShipStock',
			title : '可发货总数',
			width : 50,
			align : 'center'
		}, {
			field : 'allStockCount',
			title : '库存总数',
			width : 50,
			align : 'center'
		}, {
			field : 'action',
			title : '操作',
			width : 50,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				<%if(group.isFlag(51)){ %>
					action = action + '<a href="<%=request.getContextPath() %>/admin/fProductMark.do?id='+row.id+'">编辑坏货/已返厂</a>';
				<%}%>
				<%if(group.isFlag(293)){ %> 
					if (row.productBarcodeVO != null && row.productBarcodeVO.barcode != null) {
						action = action + '|<a href="javascript:void(0);" onclick="onPrintSubmit(\''+row.code+'\',\''+row.oriname+'\',\''+row.productBarcodeVO.barcode+'\'); return false;">打印条码</a>';
					}
				<%} %>
				return action;
			}
		} ] ]
	});
};

function productGridSearch() {
	if (checksubmit()) {
		$("#productDataGrid").datagrid("load",{
			barcodes : $('#searchProductForm').find('[name=barcodes]').val(),
		});
	}
};

function checksubmit(){
	var reg=new RegExp("^(\\d)|(\\n)$");
	var barcodes = document.getElementById("barcodes");
	if(barcodes.value==""){
		$.messager.alert("提示", "产品条码不能为空！", "info", function() {barcodes.focus();});
		return false;
	}
	var str = barcodes.value.split("\n");
	var numlen = str.length;
	var count=0;
	if(numlen>30){
		for(var i=0;i<numlen;i+=1){
			if(str[i] && trim(str[i]).length>0)
				count++;
		}
	}
	if(count>30){
		$.messager.alert("提示", "一次最多可扫描30个条码！", "info", function() {barcodes.focus();});
		return false;
	}
	return true;
}

//去掉前后空格
function trim(str) {
	return str.replace(/(^\s*)|(\s*$)/g, "");
}

//提交表单打印条码
function onPrintSubmit(code,oriname,barcode){
	$("#codeId").attr("value", code);
	$("#orinameId").attr("value", oriname);
	$("#barcodeId").attr("value", barcode);
	$("#printBarocdeFormId").submit();
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;">
		<form id="searchProductForm">
			<fieldset>
				<legend>产品查询（批量条码）</legend>
					<span>产品条形码：</span>
					<textarea cols="50" rows="4" id="barcodes" name="barcodes"></textarea>&nbsp;&nbsp;
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="productGridSearch();">查询</a>
					<br/>注：可连续扫描多个产品条码
			</fieldset>
		</form>
	</div>
	<table id="productDataGrid"></table>
	<form name="printBarocdeForm" id="printBarocdeFormId" action="${pageContext.request.contextPath}/admin/barcodeManager/printProcBarcode.jsp" method="post"  target="_blank">
   		<input id="codeId" type="hidden"  name="code"/>
   		<input id="orinameId" type="hidden"  name="oriname"/>
   		<input id="barcodeId" type="hidden"  name="barcode"/>
   		<input type="hidden" name="pageTitle" value="打印产品条码">
   </form>
</body>
</html>