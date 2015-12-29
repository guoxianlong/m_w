<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
var code;
var odCode;
var operId;
$(function(){
	$('#orderCode').focus();
	$('#dd').hide();
	
});
function findOrder(){
	var orderCode = $('#orderCode').val().trim();
	if(orderCode == ''){
		$.messager.show({msg : "请输入出库单或订单编号!",title : '提示'});
		clearScanCode();
		return;
	}
	$.ajax({
		url:'${pageContext.request.contextPath}/ScanOrderStockController/getOrderStockBean.mmx',
		dataType : 'json',
		data : {orderCode : orderCode},
		type : 'post',
		cache : false,
		success : function(r){
			odCode = r.orderCode;
			code = r.code;
			operId = r.id;
			$.post('${pageContext.request.contextPath}/ScanOrderStockController/getCargoStaffPerformance.mmx',
	               {},
	               function(data){
	                	$('#performance').append(data);
	               },"text");
			$.post('${pageContext.request.contextPath}/ScanOrderStockController/getOrderStockStatus.mmx',
	               {operId : operId},
	               function(data){
	                	$('#ckStatus').append(data);
	               },"text");
			$('#ckCode').append(r.code);
			$('#address').append(r.orderAddress);
			var orderCode = "<a href='${pageContext.request.contextPath}/admin/order.do?id="+ r.orderId + "&split=1'>" + r.orderCode + "</a>"; 
			$('#odCode').append(orderCode);
			$('#datagrid').datagrid({ 
				toolbar : '#tb',
			    url:'${pageContext.request.contextPath}/ScanOrderStockController/getScanOrderStockDatagrid.mmx',
			    queryParams : {osId : r.id},
			    idField : 'id',
			    fit : true,
			    fitColumns : true,
			    striped : true,
			    rownumbers : true,
			    rowStyler:function(index,row){    
			        if (row.stockOutCount == row.count){
			        	return 'background-color:#00FF00;color:red;font-weight:bold;';
			        } 
			    },
			    columns:[[  
			        {field:'productName',title:'产品名称',width:55,align:'center'},  
			        {field:'productOriname',title:'产品原名称',width:55,align:'center',
			        	formatter : function(value, rowData, rowIndex) {
							return "<a href=\"${pageContext.request.contextPath}/admin/fproduct.do?id="+ rowData.productId + "\" target=\"_blank\">" + rowData.productOriname + "</a>";
			        }},  
			        {field:'productCode',title:'产品编号',width:55,align:'center'}, 
			        {field:'productBarcode',title:'产品条码',width:55,align:'center'}, 
			        {field:'stockOutCount',title:'出库量',width:55,align:'center'}, 
			        {field:'count',title:'复核量',width:55,align:'right',align:'center'}, 
			        {field:'stockCountBJ',title:'当前北库库存',width:55,align:'center'}, 
			        {field:'stockCountGF',title:'当前广分库存',width:55,align:'center'}, 
			        {field:'stockCountGS',title:'当前广速库存',width:55,align:'center'}, 
			        {field:'ew',title:'查进销存',width:55,align:'center',
			        	formatter : function(value, rowData, rowIndex) {
						return "<a href=\"${pageContext.request.contextPath}/admin/productStock/stockCardList.jsp?productCode="+ rowData.productCode + "\" target=\"_blank\">查看</a>";
					}}, 
			    ]]  
			}); 
			$('#dd').show();
			$('#d').hide();
			$('#scanCode').focus();
		}
	});
}
function scanOrder(){
	var scanCode = $('#scanCode').val().trim();
	if(scanCode == ''){
		$.messager.show({msg : "请输入要复核的商品编号或条码!",title : '提示'});
		clearScanCode();
		return;
	}
	if(scanCode == code || scanCode == odCode){
		$.ajax({
			url : '${pageContext.request.contextPath}/ScanOrderStockController/completeOrderStock.mmx',
			type : 'post',
			dataType : 'json',
			cache : false,
			data : {operId : operId},
			success : function(r) {
				$.messager.show({
					title : '提示',
					msg : r.msg,
				});
				if(r.success){
					document.location = "${pageContext.request.contextPath}/admin/rec/oper/orderStock/printPackage.jsp?orderCode=" + odCode;
				}
			}
		});
	}else{
		$('#datagrid').datagrid('load',{
			scanCode : scanCode,
		});
	}
	clearScanCode();
}
function submitScanOrder(){
	$.ajax({
		url : '${pageContext.request.contextPath}/ScanOrderStockController/completeOrderStock.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		data : {operId : operId},
		success : function(r) {
			$.messager.show({
				title : '提示',
				msg : r.msg,
			});
			if(r.success){
				document.location = "${pageContext.request.contextPath}/admin/rec/oper/orderStock/printPackage.jsp?orderCode=" + odCode;
			}
		}
	});
}
function stockAdminHistory(){
	window.open("${pageContext.request.contextPath}/admin/rec/oper/orderStock/stockAdminHistory.jsp?operId=" + operId,"_blank");
}
function returnOrderStock(){
	document.location = "${pageContext.request.contextPath}/admin/rec/oper/orderStock/orderStock.jsp";
}
function exportOrderStock(){
	window.open("${pageContext.request.contextPath}/admin/rec/oper/orderStock/orderStockPrint.jsp?id=" + operId,"_blank");
}
function reScanOrder(){
	$('#datagrid').datagrid('load',{
		scanCode : '-1',
	});
}
function clearScanCode(){
	$('#scanCode').val('');
	$('#scanCode').focus();
}
function findOrderEnter(e){
	if(e.keyCode==13){
		findOrder();
	}
}
function scanOrderEnter(e){
	if(e.keyCode==13){
		scanOrder();
	}
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;">
		<div id="d">
			<fieldset>
			<legend></legend>
				<div align="left">
				订单号：<input id="orderCode" name="orderCode" class="easyui-validatebox" required="required" onkeyup="findOrderEnter(event);"/>
				<a href="javascript:void(0);" class="easyui-linkbutton"  plain="false" onclick="findOrder()">确认 </a>
			</div>
			</fieldset>
		</div>
		<div id="dd">
			<fieldset>
			<legend> </legend>
				<div align="left" id="performance" style="color: #FF0000"></div><br>
				<div align="left">
					订单：<label id="odCode" style="color: #000099"></label>&nbsp;&nbsp;
					编号：<label id="ckCode" style="color: #000099"></label>&nbsp;&nbsp;
					状态：<label id="ckStatus"></label>
					订单地址:<label id="address" style="color: #000099"></label>&nbsp;&nbsp;
				<a href="javascript:void(0);" class="easyui-linkbutton"  plain="false" onclick="submitScanOrder()">复核完毕确认出货 </a>
				</div><br>
				<div align="left">
					扫描产品条码或输入产品编号：<input id="scanCode" name="scanCode" onkeyup="scanOrderEnter(event)">  
					<a href="javascript:void(0);" class="easyui-linkbutton"  plain="false" onclick="scanOrder()">复核确认 </a>
					<a href="javascript:void(0);" class="easyui-linkbutton"  plain="false" onclick="reScanOrder()">重新扫描 </a>    
					<a href="${pageContext.request.contextPath}/admin/barcodeManager/ScanOrderHelp.jsp">扫描帮助 </a>
				</div>
			</fieldset>
			<div align="right">
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-undo" plain="true" onclick="stockAdminHistory()">人员操作记录 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-sum" plain="true" onclick="returnOrderStock()">返回订单出货操作记录列表 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-redo" plain="true" onclick="exportOrderStock()">导出列表 </a>
			</div>
		</div>
	</div>
	<table id="datagrid"></table>  
</body>
</html>