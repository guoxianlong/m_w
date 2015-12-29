<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	$('#stockExchangeProductGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSessionAfterSaleStockExchangeInfo.mmx',
	    queryParams: {
	    	flag : 1	
	    },
	    toolbar : '#tb',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    singleSelect : true,
	    loadMsg : '正在努力为您加载..',
	    columns:[[  
  			{field:'productName',title:'商品名称',width:60,align:'center'},
  			{field:'productCode',title:'商品编号',width:100,align:'center'},
  			{field:'afterSaleDetectProductCode',title:'售后处理单号',width:100,align:'center'},
  			{field:'imei',title:'IMEI码',width:100,align:'center'},
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
			$('#amount').html($('#stockExchangeProductGrid').datagrid("getRows").length);
			$('#stockExchangeProductGrid').datagrid("selectRow",0);
		}
	});
	
	$("#stockArea").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getAllStockArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		required:true,
		onSelect : function(record){
			$('#stockType').combobox({
				url : '${pageContext.request.contextPath}/Combobox/getStockTypeByStockArea.mmx?stockArea='+record.id,
				valueField : 'id',
				textField : 'text',
				editable : false,
				required:true
			}).combobox('clear');
		}
	});
	
	$('#stockType').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getStockTypeByStockArea.mmx?stockArea=-1',
		valueField : 'id',
		textField : 'text',
		editable : false,
		required:true
	});
	
	$('#saveForm').form({
		url : '${pageContext.request.contextPath}/admin/AfStock/afterSaleStockExchangeInfo.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			if (!isValid) {
				return false;
			}
			var codes = $('#saveForm textarea[id=codes]').val();
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
						$('#stockExchangeProductGrid').datagrid('load',{});
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
});

function deleteStockExchangeProduct(index){
	if (index != undefined) {
		$('#stockExchangeProductGrid').datagrid('selectRow', index);
	}
	var row = $('#stockExchangeProductGrid').datagrid('getSelected');
	
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/removeSessionAfterSaleStockExchangeInfo.mmx',
		data:{'code':row.afterSaleDetectProductCode},
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

function createAfterSaleStockExchange(){
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

function save(){
	var ev=arguments.callee.caller.arguments[0] || window.event;  //火狐浏览器拿到event的大法！
	if(ev.keyCode==13) {  //回车事件
		$('#saveForm').submit();
	}
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;">
		<fieldset>
			<form id="saveForm" method="post">
				<table class="tableForm">
					<tr align="center" >
						<th>目的库：</th>
						<td>库区：</td>
						<td align="left">
							<input id="stockArea" name="stockArea" style="width: 116px;"/>
						</td>
						<td>库类型：</td>
						<td align="left">
							<input id="stockType" name="stockType" style="width: 116px;"/>
						</td>
					</tr>
					<tr>
						<td colspan="4">添加处理单号</td>
					</tr>
					<tr>
						<td colspan="4">
							<textarea name="codes" id="codes" onkeypress="save()"/></textarea>
						</td>
					</tr>
					<tr>
						<td>
							<button onclick="save()">添加</button>
						</td>
					</tr>
				</table>
			</form>
		</fieldset>
		<h3>已添加选项：</h3>
		<div id="toolbar">
			<a href="javascript:createAfterSaleStockExchange();" class="easyui-linkbutton" iconCls="icon-ok" plain="true">提交审核</a>
			&nbsp;&nbsp;&nbsp;&nbsp;已输入<span id="amount">0</span>件
		</div>
	</div>
	<table id="stockExchangeProductGrid"></table>
</body>
</html>