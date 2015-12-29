<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>买卖宝后台</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var quoteItem = 0;
var status = ${param.status};
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/spareManagerController/getReplaceNewProductWaitQuoteList.mmx?status=' + status,
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
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
					{field:'id',width:20,hidden:true}
	    ]],
	    columns:[[  
	    	{field:'afterSaleDetectProductCode',title:'售后处理单号',width:30,align:'center'},
	    	{field:'afterSaleOrderCode',title:'售后单号',width:30,align:'center'},
	    	{field:'spareCode',title:'原备用机单号',width:30,align:'center'},
	    	{field:'productCode',title:'商品编号',width:30,align:'center'},
	    	{field:'productOriname',title:'商品原名称',width:30,align:'center'},
	    	{field:'lastOperateUsername',title:'最后操作人',width:30,align:'center'},
	        {field:'lastOperateTime',title:'最后操作时间',width:30,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}
        	}, 
	        {field:'action',title:'操作',width:50,align:'center',
	        	formatter : function(value, row, index) {
	        		if(row.status==1){
	        			return '<a href="javascript:void(0);"  class="replace" onclick="quotePrice('+index+')"></a>'+
	        						'<a href="javascript:void(0);"  class="noGoodsExchange" onclick="noGoodsExchange('+index+')"></a>';
	        		}else if(row.status==3){
	        			return '<a href="javascript:void(0);"  class="replace" onclick="replaceProduct('+index+')"></a>' + 
	        						'<a href="javascript:void(0);"  class="noGoodsExchange" onclick="noGoodsExchange('+index+')"></a>';
	        		}else if(row.status==2){
	        			return '<a href="javascript:void(0);"  class="replaceCode" onclick="replaceCode('+index+')"></a>';
	        		}        		
				}}
	    ]],
	    onLoadSuccess : function(data){
	    	var footer = data.footer[0];
	    	if(footer.quotePriceFlag){
	    		$(".replace").linkbutton({ 
					text:'报价'
				});
				$(".noGoodsExchange").linkbutton({ 
					text:'无商品可更换'
				});
	    	}
	    	if(footer.replaceCodeFlag){
	    		$(".replaceCode").linkbutton({ 
					text:'换新机号码更换'
				});
	    	}
	    	if(status != 5){
	    		$("#datagrid").datagrid('hideColumn', 'spareCode');
	    	}
			if(status==2){
				$("#title").html('换新机待更换列表');
			}else if(status==5){
				$("#title").html('换新机已更换列表');
			}else{
				$("#title").html('用户换新机待报价列表');
			}
			if(data.rows.length==0){
				$.messager.show({
					msg : '没有查询到您所需要的信息，请重新输入查询条件进行查询!',
					title : '提示'
				});
			}
	    }
	}); 
	
	$("#quoteTipDiv").show().dialog({
		title: '提示',
		width: 300,
		height: 150,
		modal : true,
		maximizable : true,
		buttons : [{
			text : '确定',
			handler : function() {
				$.ajax({
					url : '${pageContext.request.contextPath}/spareManagerController/replaceNewProductQuotePrice.mmx',
					type : 'post',
					dataType : 'json',
					data : {recordId:$("#recordId").val(),status:'3'},
					success : function(result){
						$("#recordId").val('');
						if(result.success){
							$("#quoteTipDiv").dialog("close");
						}
						$.messager.alert("提示",result.msg,'info', function(){
								if(result.success){
									$('#datagrid').datagrid("reload");
								}
							}
						);
					}
				});
			}
		}]
	}).dialog('close');
	
	$("#notReplaceDiv").show().dialog({
		title: '提示',
		width: 300,
		height: 150,
		modal : true,
		maximizable : true,
		buttons : [{
			text : '取消',
			handler : function(){
				var d = $(this).closest('.window-body');
				flag = false;
				d.dialog('close');
			}
		},{
			text : '确定',
			handler : function() {
				$.ajax({
					url : '${pageContext.request.contextPath}/spareManagerController/replaceNewProductQuotePrice.mmx',
					type : 'post',
					dataType : 'json',
					data : {recordId:$("#recordId").val(),status:'4'},
					success : function(result){
						if(result.success){
							$("#recordId").val('');
							$("#notReplaceDiv").dialog("close");
						}
						$.messager.alert("提示",result.msg,'info',
							function(){
								if(result.success){
									$('#datagrid').datagrid("reload");
								}
							}
						);
					}
				});
			}
		}]
	}).dialog('close');
	
	$("#quotePriceDiv").show().dialog({
		title: '报价',
		width: 600,
		height: 300,
		modal : true,
		maximizable : true,
		buttons : [{
			text : '取消',
			handler : function(){
				var d = $(this).closest('.window-body');
				flag = false;
				d.dialog('close');
			}
		},{
			text : '确定',
			handler : function() {
				if(checkQuoteItem()){
					$.ajax({
						url : '${pageContext.request.contextPath}/spareManagerController/replaceNewProductQuotePrice.mmx',
						type : 'post',
						dataType : 'json',
						data : {recordId:$("#recordId").val(),
									status:'1',
									'quoteItem' : $("#quoteItem").combobox("getValue"),
									'quote' : $("#quote").numberbox("getValue"),
									'quoteItemadd' : $("#quotePriceTable input[name=quoteItemadd]").val(),
									'quoteadd' : $("#quotePriceTable input[name=quoteadd]").val()
								},
						success : function(result){
							if(result.success){
								$("#recordId").val('');
								$("#parentId1").val('');
								$("#quotePriceDiv").dialog("close");
							}
							$.messager.alert("提示",result.msg,'info', function(){
								if(result.success){
									$('#datagrid').datagrid("reload");
									}
								}
							);
						}
					});
				}
			}
		}]
	}).dialog('close');
	
	$("#replaceCodeDiv").show().dialog({
		title: '提示',
		width: 300,
		height: 150,
		modal : true,
		maximizable : true,
		buttons : [{
			text : '确认',
			handler : function() {
				$.ajax({
					url : '${pageContext.request.contextPath}/spareManagerController/replaceNewProductCode.mmx',
					type : 'post',
					dataType : 'json',
					data : {recordId:$("#recordId").val(),replaceCode:$("#replaceCode").val()},
					success : function(result){
						$("#replaceCode").val('');
						if(result.success){
							$("#recordId").val('');
							$("#replaceCodeDiv").dialog("close");
						}
						$.messager.alert("提示",result.msg,'info', function(){
								if(result.success){
									$('#datagrid').datagrid("reload");
								}
							}
						);
					}
				});
			}
		}]
	}).dialog('close');
});


function searchFun() {
	var result = checkSubmit();
	if(result){
		datagrid.datagrid('load', {
			detectCode : $("#detectCode").val(),
			afterSaleOrderCode : $("#afterSaleOrderCode").val(),
			productCode : $("#productCode").val(),
			startTime : $('#startTime').datebox('getValue'),
			endTime : $('#endTime').datebox('getValue'),
		});
	}
}

function checkSubmit(){
	var flag = false;
	if($.trim($("#detectCode").val())!=""){
		flag = true;
	}
	if($.trim($("#afterSaleOrderCode").val())!=''){
		flag = true;
	}
	if($.trim($("#productCode").val())!=''){
		flag = true;
	}
	var startTime = $("#startTime").datebox('getValue');
	var endTime = $("#endTime").datebox('getValue');
	if($.trim(startTime)!="" && $.trim(endTime)!=""){
		flag = true;
	}
	if(flag){
		var days = getValidateSubDays(endTime,startTime);
		if (days < 0) {
			$.messager.show({
				msg : "查询的开始时间不能早于结束时间!",
				title : '提示'
			});
			return false;
		}else{
			flag = true;
		}
	}
	if(!flag){
		$.messager.show({
			msg : "请输入查询条件!",
			title : '提示'
		});
	}
	return flag;
}

function replaceProduct(index){
	if (index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	$("#recordId").val(row.id);
	$("#quoteTipDiv").dialog("open");
}

function quotePrice(index){
	if (index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	$("#recordId").val(row.id);
	$("#parentId1").val(row.proParentId1);
	initCombobox('quoteItem',6,$("#quotePriceTable input[id=parentId1]").val());
	$("#quotePriceDiv").dialog("open");
}

function checkQuoteItem(){
	if($("#quoteItem").combobox("getValue") == "" && $("#quote").numberbox("getValue") =="") {
		$.messager.show({
			title : '提示',
			msg : '请填写报价项报价!'
		});
		return false;
	}
	if($("#quoteItem").combobox("getValue") == "" && $("#quote").numberbox("getValue") !="") {
		$.messager.show({
			title : '提示',
			msg : '报价项为空，报价也得为空！'
		});
		return false;
	}
	if($("#quoteItem").combobox("getValue") != "" && $("#quote").numberbox("getValue") =="") {
		$.messager.show({
			title : '提示',
			msg : '报价为空，报价项也得为空！'
		});
		return false;
	}
	$("#detectProduct input[name=quoteItemadd]").each(
		function(index) {
			if ($("#detectProduct input[name=quoteItemadd]").eq(index).val() == "" && $("#detectProduct input[name=quoteadd]").eq(index).val() !="") {
				$.messager.show({
					title : '提示',
					msg : '报价项为空，报价也得为空！'
				});
				return false;
			}
			if ($("#detectProduct input[name=quoteItemadd]").eq(index).val()!= "" && $("#detectProduct input[name=quoteadd]").eq(index).val() =="") {
				$.messager.show({
					title : '提示',
					msg : '报价为空，报价项也得为空！'
				});
				return false;
			}
		}
	);
	return true;
}

function initCombobox(inputId,afterSaleDetectTypeId, parentId1) {
	$('#' + inputId).combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetail.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + "&parentId1=" + parentId1,
      	valueField:'id',
		textField:'text',
		delay:500
    });
}

function addQuoteHtml() {
	quoteItem += 1;
	var tr = $("#recordId");
	var addItem = "";
	addItem+=('<tr align="center">');
	addItem+=('<th>报价项：</th>');
	addItem+=('<td align="left" colspan="3">');
	addItem+=('<input id="quoteItemadd'+quoteItem+'" name="quoteItemadd" style="width: 300px;"/>');
	addItem+=('</td>');
	addItem+=('<th>报价：</th>');
	addItem+=('<td align="left">');
	addItem+=('<input id="quoteadd'+quoteItem+'" name="quoteadd" style="width: 116px;" class="numberbox"/>&nbsp;');
	addItem+=('<a class="addItemClass" onclick="removeQuoteHtml('+quoteItem+');" href="javascript:void(0);"></a>');
	addItem+=('</td>');
	addItem+=("</tr>");
	tr.before(addItem);
	$(".addItemClass").linkbutton(
		{ 
			plain:true,
			iconCls:'icon-remove'
		}
	);
	initCombobox('quoteItemadd' + quoteItem,6,$("#quotePriceTable input[id=parentId1]").val());
	$(".numberbox").numberbox (
		{
			precision:2,
			max:99999999.99
		}
	)
}

function removeQuoteHtml(index) {
	$("#quoteItemadd"+index).parent().parent().remove();
}

function noGoodsExchange(index){
	if (index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	$("#recordId").val(row.id);
	$("#notReplaceDiv").dialog("open");
}

function replaceCode(index){
	if (index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	$("#recordId").val(row.id);
	$("#replaceCodeDiv").dialog("open");
}
</script>
</head>
<body>
	<table id="datagrid"></table> 
	<div id="tb" style="height: auto;">
		<fieldset>
			<legend id="title"></legend>
			<table class="" >
				<tr align="center" >
				  <th >售后处理单号</th>
				  <td><input type="text" id="detectCode" name="detectCode" style="width:116px"/></td>
					<th>售后单号</th>
					<td align="left">
						<input id="afterSaleOrderCode" name="afterSaleOrderCode" style="width:116px" /></td>
					<th >商品编号</th>
					<td align="left">
						<input id="productCode" name="productCode" style="width:116px" />
					</td>			
				  <th >最后操作时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" name="startTime"/>
						--&nbsp;<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:121px"/></td>				
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<div id="quoteTipDiv">
		<h4>此售后处理单已有维修费用单，<span style="color:red;">不需再进行报价</span>！请确认是否还提交？</h4>
	</div>
	<div id="quotePriceDiv">
		<table id="quotePriceTable">
			<tr align="center">
				<th>报价项：</th>
				<td align="left" colspan="3">
					<input id="quoteItem" name="quoteItem" style="width: 300px;"/>
				</td>
				<th>报价：</th>
				<td align="left">
					<input id="quote" name="quote" style="width: 116px;" class="easyui-numberbox" data-options="precision:2,max:99999999.99"/>
					<input id="parentId1" name="parentId1" type="hidden"/>
					<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addQuoteHtml();" href="javascript:void(0);"></a>
				</td>
			</tr>
			<input type="hidden" id="recordId" value=""/>
		</table>
	</div>
	<div id="notReplaceDiv">
		<h4>请确认是否无商品可更换!</h4>
	</div>
	<div id="replaceCodeDiv" align="center">
		<h4>请输入或扫描做替换的备用机单号：</h4>
		<input type="text" id="replaceCode" name="replaceCode"/>
	</div>
</body>
</html>