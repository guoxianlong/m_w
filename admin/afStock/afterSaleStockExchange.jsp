<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	var stockExchangeId = '${param.stockExchangeId}';
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getStockExchangeInfo.mmx',
		data : {'stockExchangeId':stockExchangeId},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					if (r.obj[0] == 'edit') {
						$("#inStockInfo").html(r.obj[1]);
						setSessionInfo();
						$("#stockExchangeProductGrid").show();
					} else if (r.obj[0] == 'audit') {
						$("#stockInfo").html(r.obj[1]);
						initStockExchangeDataGrid(stockExchangeId);
						$("#stockExchangeDataGrid").show();
					}
					auditPower(stockExchangeId);
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
});

function setSessionInfo() {
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/setStockExchangeProductSessionInfo.mmx',
		data:{"stockExchangeId":$('#tb input[id=stockExchangeId]').val()},
		cache:false,
		type:"post",
		dataType:'text',
		success:function(result){
			var r = $.parseJSON(result);
			if(r.success){
				initStockExchangeProductGrid();
			}
		}
	});
}

function initStockExchangeProductGrid(stockExchangeId) {
	$('#stockExchangeProductGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSessionAfterSaleStockExchangeInfo.mmx',
	    toolbar : '#tb',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    columns:[[  
  			{field:'productName',title:'商品名称',width:60,align:'center'},
  			{field:'productCode',title:'商品编号',width:100,align:'center'},
  			{field:'afterSaleDetectProductCode',title:'售后处理单号',width:100,align:'center'},
  			{field:'imei',title:'IMEI码 ',width:100,align:'center'},
  			{field:'wholeCode',title:'货位号',width:100,align:'center'},
  			{field:'mainStatus',title:'商品质量',width:100,align:'center'},
  			{field:'sellType',title:'销售属性',width:100,align:'center'},
  	        {field:'action',title:'操作',width:60,align:'center',
  	        	formatter : function(value, row, index) {
          			return '<a href="javascript:void(0);" class="deleteStockExchangeButton" onclick="deleteStockExchangeProduct('+index+');" ></a>';
  				}
  			}
  	    ] ],
		onLoadSuccess : function(data) {
			//改变datagrid中按钮的class
			$(".deleteStockExchangeButton").linkbutton(
				{ 
					text:'删除',
					iconCls:'icon-remove',
					plain:true
				}
			);
		}
	});
}

function deleteStockExchangeProduct(index){
	if (index != undefined) {
		$('#stockExchangeProductGrid').datagrid('selectRow', index);
	}
	var row = $('#stockExchangeProductGrid').datagrid('getSelected');
	
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/removeSessionAfterSaleStockExchangeInfo.mmx',
		data:{code:row.afterSaleDetectProductCode},
		cache:false,
		type:"post",
		dataType:'text',
		success:function(result){
			var r = $.parseJSON(result);
			if(r.success){
				$('#stockExchangeProductGrid').datagrid('reload');
			}
			$.messager.show({
				title:'提示',
				msg:r.msg,
				timeout:3000,
				showType:'slide'
			});
		}
	});
}

function saveFun() {
	$('#saveForm').form('submit' , {
		url : '${pageContext.request.contextPath}/admin/AfStock/afterSaleStockExchangeInfo.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			if (!isValid) {
				return false;
			}
			var codes = $('#saveForm textarea[name=codes]').val();
			if ($.trim(codes) == "") {
				$.messager.show({
					title : '提示',
					msg : "处理单号不能为空！"
				});
				return false;
			}
		},
		success : function(result) {
			try {
				if (result != null ) {
					var r = $.parseJSON(result);
					if (r.success) {
						$('#saveForm textarea[id=codes]').val("");
						$('#stockExchangeProductGrid').datagrid('reload');
					}
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function createAfterSaleStockExchange(){
	var id = $("#stockExchangetb input[id=exchangeId]").val();
	$('#saveForm').form('submit' , {
		url : '${pageContext.request.contextPath}/admin/AfStock/createAfterSaleStockExchange.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			if (!isValid) {
				return false;
			}
		},
		success : function(result) {
			try {
				if (result != null ) {
					var r = $.parseJSON(result);
					if (r.success) {
						reloadPage(id);
					}
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function initStockExchangeDataGrid(stockExchangeId) {
	$('#stockExchangeDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfStockExchangeProductCargoList.mmx',
	    queryParams: {
	    	id : stockExchangeId
	    },
	    toolbar : '#stockExchangetb',
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
			{field:'oriName',title:'商品原名称',width:100,align:'center'},
			{field:'productCode',title:'商品编号',width:60,align:'center'},
			{field:'stockCount',title:'调拨量',width:60,align:'center'},
			{field:'stock',title:'目的库库存',width:60,align:'center'},
			{field:'inWholeCode',title:'目的货位（库存量）',width:60,align:'center'},
	    ] ],
		onLoadSuccess : function(data) {
		}
	}); 
}

function auditPower(id) {
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getStockExchangePower.mmx',
		data : {'id':id},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					if (r.obj.inConfirmFlag) {
						$("#inConfirmDiv").show();
					} else if (r.obj.outConfirmFlag) {
						$("#outConfirmDiv").show();
					} else if (r.obj.inAuditFlag){
						$("#inAuditDiv").show();
					} else if (r.obj.outAuditFlag) {
						$("#outAuditDiv").show();
					}
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function auditFun(mark,audintType) {
	var id = $("#stockExchangetb input[id=exchangeId]").val();
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/auditingStockExchange.mmx',
		data : {'exchangeId':id,"audintType":audintType,"mark":mark},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					reloadPage(id);
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function confirmFun() {
	var id = $("#stockExchangetb input[id=exchangeId]").val();
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/completeStockExchange.mmx',
		data : {'exchangeId':id,"confirm":1},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					reloadPage(id);
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}
function reloadPage(opid) {
	window.location.href = '${pageContext.request.contextPath}/admin/afStock/afterSaleStockExchange.jsp?stockExchangeId='+opid;
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display:none">
		<fieldset>
			<form id="saveForm" method="post">
				<table class="tableForm">
					<tr align="center" >
						<div id="inStockInfo"></div>
					</tr>
					<tr>
						<td colspan="4">添加处理单号</td>
					</tr>
					<tr>
						<td colspan="4">
							<textarea id="codes" name="codes" rows="4" cols="40"></textarea>
						</td>
					</tr>
					<tr>
						<td>
							<a class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true" onclick="saveFun();" href="javascript:void(0);">添加</a>
						</td>
					</tr>
				</table>
			</form>
		</fieldset>
		<h3>已添加选项：</h3>
		<div id="outConfirmDiv">
			<a href="javascript:createAfterSaleStockExchange();" class="easyui-linkbutton" iconCls="icon-ok" plain="true">提交审核</a>
		</div>
	</div>
	<table id="stockExchangeProductGrid" style="display:none"></table>
	<table id="stockExchangeDataGrid" style="display:none"></table>
	<div id="stockExchangetb" style="padding:3px;height: auto;display:none">
		<input type="hidden" id="exchangeId" value="${param.stockExchangeId }"/>
		<div id="stockInfo"></div>
		<div id="inAuditDiv" style="display:none">
			<table>
				<tr>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="auditFun(1,'audintingIn');" href="javascript:void(0);">审核通过</a>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-cancel',plain:true" onclick="auditFun(0,'');" href="javascript:void(0);">审核不通过</a>
					</td>
				</tr>
			</table>
		</div>
		<div id="outAuditDiv" style="display:none">
			<table>
				<tr>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="auditFun(1,'');" href="javascript:void(0);">审核通过</a>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-cancel',plain:true" onclick="auditFun(0,'');" href="javascript:void(0);">审核不通过</a>
					</td>
				</tr>
			</table>
		</div>
		<div id="inConfirmDiv" style="display:none">
			<table>
				<tr>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="confirmFun();" href="javascript:void(0);">确认入库</a>
					</td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>