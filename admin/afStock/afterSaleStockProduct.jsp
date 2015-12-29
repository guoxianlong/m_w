<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>售后库商品列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var flag_one;
var flag_two;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleProductDatagrid.mmx',
	    queryParams : {
	    	stockType : '9',
	    },
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
	    checkOnSelect : false,
		selectOnCheck : false,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
	         		{field:'checkId',checkbox:true,width:40,align:'center',value:"afterSaleDetectCode"},
					{field:'id',title:'ID',width:20,hidden:true},
					{field:'productCode',title:'商品编号',width:80,align:'center'}
	    ]],
	    columns:[[
	        {field:'productName',title:'小店名称',width:40,align:'center'},  
	        {field:'productOriName',title:'原名称',width:40,align:'center'},
	        {field:'mainProductStatus',title:'商品质量',width:15,align:'center'},  
	        {field:'questionDescription',title:'问题分类',width:20,align:'center'},
	        {field:'faultDescription',title:'故障描述',width:20,align:'center'},
	        {field:'sellTypeName',title:'销售属性',width:20,align:'center'},  
	        {field:'wholeCode',title:'货位编号',width:30,align:'center'},  
	        {field:'afterSaleDetectCode',title:'售后处理单号',width:30,align:'center'},  
	        {field:'afterSaleCode',title:'售后单号',width:30,align:'center'},  
	        {field:'createDatetime',title:'入库日期',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	        {field:'afterSaleStockinTypeName',title:'入库类型',width:20,align:'center'},  
	    ]]
	}); 
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#sellType').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSellTypeSelect.mmx',
      	valueField:'id',
		textField:'text',
		editable : false,
	});
	$('#mainProductStatus').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getMainProductStatusSelect.mmx',
      	valueField:'id',
		textField:'text',
		editable : false,
	});
	$('#afterSaleStockinType').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterSaleStockinTypeSelect.mmx',
      	valueField:'id',
		textField:'text',
		editable : false,
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
	var parentId2;
	var parentId3;
	if(flag_one){
		parentId2 = $('#parentId2').combobox('getValue');
	}
	if(flag_two){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	datagrid.datagrid('load', {
		stockType: 9,
		startTime : $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		afterSaleCode :  $('#tb input[name=afterSaleCode]').val(),
		afterSaleDetectCode :  $('#tb input[name=afterSaleDetectCode]').val(),
		areaId : $('#areaId').combobox('getValue'),
		parentId1 : $('#parentId1').combobox('getValue'),
		parentId2 : parentId2,
		parentId3 : parentId3,
		productCode :  $('#tb input[name=productCode]').val(),
		productName :  $('#tb input[name=productName]').val(),
		sellType:$('#sellType').combobox('getValue'),
		mainProductStatus:$('#mainProductStatus').combobox('getValue'),
		afterSaleStockinType:$('#afterSaleStockinType').combobox('getValue')
	});
	
}
function clearFun() {
	$('#tb input[name=productCode]').val('');
	$('#tb input[name=productName]').val('');
	$('#tb input[name=afterSaleCode]').val('');
	$('#tb input[name=afterSaleDetectCode]').val('');
	$('#areaId').combobox('setValue','');
	$('#startTime').datebox('setValue','');
	$('#endTime').datebox('setValue','');
	$('#parentId1').combobox('setValue','');
	if(flag_one){
		$('#parentId2').combobox('setValue','');
	}
	if(flag_two){
		$('#parentId3').combobox('setValue','');
	}
	$('#sellType').combobox('setValue',-1);
	$('#mainProductStatus').combobox('setValue','');
	$('#areaId').combobox('setValue','');
	$('#afterSaleStockinType').combobox('setValue',-1);
	datagrid.datagrid('load', {stockType:9});
}

function exchange(){
	var selectrows = $("#datagrid").datagrid("getChecked");
	var selectlength = selectrows.length;
	if (selectlength <= 0) {
		$.messager.show({
			title : '提示',
			msg : "没有选择要调拨的商品！"
		});
		return  false;
	}
	
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/afterSaleStockExchangeProduct.jsp',
		width : 400,
		height : 200,
		modal : true,
		title : '选择目的库类型 ',
		onClose : function() {
			$(this).dialog('destroy');
		},
		buttons : [ {
			id : 'closeButton',
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
			}
		} ],
		onLoad : function(){
			getAreaType();
		}
	});
}

function getAreaType(){
	$("#toolbar input[id='areaType']").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getAllStockArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false,
		required:true,
		onSelect : function(record){
			$('#toolbar input[id="stockType"]').combobox({
				url : '${pageContext.request.contextPath}/Combobox/getStockTypeByStockArea.mmx?stockArea='+record.id,
				valueField : 'id',
				textField : 'text',
				editable : false,
				required:true
			}).combobox('clear');
		}
	});
	
	$('#toolbar input[id="stockType"]').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getStockTypeByStockArea.mmx?stockArea=-1',
		valueField : 'id',
		textField : 'text',
		editable : false,
		required:true
	});
}
function exchangeProduct(){
	var selectrows = $("#datagrid").datagrid("getChecked");
	var selectlength = selectrows.length;
	if (selectlength <= 0) {
		$.messager.show({
			title : '提示',
			msg : "没有选择要调拨的商品！"
		});
		return  false;
	}
	
	var areaType = $('#toolbar input[id="areaType"]').combobox('getValue');
	if(areaType == null || areaType == -1){
		$.messager.show({
			title : '提示',
			msg : "请选择库区！"
		});
		return  false;
	}
	
	var stockType = $('#toolbar input[id="stockType"]').combobox('getValue');
	if(stockType == null || stockType == -1){
		$.messager.show({
			title : '提示',
			msg : "请选择库类型！"
		});
		return  false;
	}
	
	
	var codes ="";
	for (var i = 0 ; i < selectlength ; i ++) {
		codes += selectrows[i].afterSaleDetectCode + ",";
	}
	var len = codes.length;
	if(len > 0){
		codes = codes.substring(0,len-1);
	}
	$.ajax({
        type: "post", //调用方式  post 还是 get
        url: "${pageContext.request.contextPath}/admin/AfStock/createAfterSaleAllot.mmx",
        data : {
			"areaType" : areaType,
			"stockType" : stockType,
			"codes" : codes
		},
        dataType: "text", //返回的数据的形式
        success: function(data) { 
        	try {
				var r = $.parseJSON(data);
				if(r.success){
					$("#closeButton").click();
				}
				$.messager.show({
					title : '提示',
					msg : r.msg
				});
			} catch (e) {
				$.messager.alert('提示', data);
			}
        }
	})
}
function excel(){
	var startTime = $('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	var productCode =  $('#tb input[name=productCode]').val();
	var afterSaleCode =  $('#tb input[name=afterSaleCode]').val();
	var productName = $('#tb input[name=productName]').val();
	var afterSaleDetectCode =  $('#tb input[name=afterSaleDetectCode]').val();
	var areaId = $('#areaId').combobox('getValue');
	var parentId1 = $('#parentId1').combobox('getValue');
	var parentId2 = '';
	var parentId3 = '';
	if(flag_one){
		parentId2 = $('#parentId2').combobox('getValue');
	}
	if(flag_two){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	var mainProductStatus = $('#mainProductStatus').combobox('getValue');
	var afterSaleStockinType = $('#afterSaleStockinType').combobox('getValue');
	var saleType = $("#saleType").val();
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelStockProduct.mmx?startTime=" + 
		startTime + "&endTime=" + endTime + "&productCode=" + productCode + "&afterSaleDetectCode=" + afterSaleDetectCode + "&afterSaleCode=" 
			+ afterSaleCode + "&areaId=" + areaId + "&parentId1=" + parentId1 + "&parentId2=" + parentId2 + "&parentId3=" + parentId3 
			+ "&productName=" + productName + "&mainProductStatus=" + mainProductStatus + "&afterSaleStockinType=" + afterSaleStockinType
			 + "&saleType=" + saleType + "&stockType=9";
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table>
				<tr>
					<th align="right">入库日期：</th>
					<td colspan="3">
						<input id="startTime" name="startTime" class="easyui-datebox" editable="false" style="width: 121px;" /> -
						<input id="endTime" name="endTime" class="easyui-datebox" editable="false" style="width: 121px;" />
					<th align="right">售后单号：</th>
					<td><input id="afterSaleCode" name="afterSaleCode" style="width: 116px;"/></td>
					<th align="right">售后处理单号：</th>
					<td><input id="afterSaleDetectCode" name="afterSaleDetectCode" style="width: 116px;"/></td>
				</tr>
				<tr>
					<th align="right">商品分类：</th>
					<td colspan="3">
						<input id="parentId1" name="parentId1" style="width: 121px;"/>
						<input id="parentId2" name="parentId2" style="width: 121px;"/>
						<input id="parentId3" name="parentId3"  style="width: 121px;"/>
					</td>
					<th align="right">商品编号：</th>
					<td><input id="productCode" name="productCode" style="width: 116px;"/></td>
					<th align="right">商品名称：</th>
					<td><input id="productName" name="productName" editable="false" style="width: 116px;"/></td>
				</tr>
				<tr>
					<th align="right">销售属性：</th>
					<td><input id="sellType" name="sellType"  editable="false" style="width: 121px;"/></td>
					<th >售后地区</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px" /></td>					
					<th align="right">商品质量：</th>
					<td><input id="mainProductStatus" name="mainProductStatus"  editable="false" style="width: 121px;"/></td>
					<th align="right">入库类型：</th>
					<td><input id="afterSaleStockinType" name="afterSaleStockinType" editable="false" style="width: 121px;"/></td>
					<td>
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a>
						<a class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();" href="javascript:void(0);">重置</a>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
					</td>
				</tr>
			</table>
		</fieldset>
	  <a href="javascript:exchange();" class="easyui-linkbutton"  iconCls="icon-ok" plain="true">批量调拨</a>
	</div>
	<table id="datagrid">
	</table> 
</body>
</html>