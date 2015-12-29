<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>添加厂商报价</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
<script  type="text/javascript">
	var quoteItem = 0;
	
	function addQuoteHtml() {
	quoteItem += 1;
	var tr = $("#btn").parent().parent();
	var addItem = "";
	addItem+=('<tr>');
	addItem+=('<th aligh="right">报价项：</th>');
	addItem+=('<td>');
	addItem+=('<input id="quoteItemadd'+quoteItem+'" name="quoteItemadd" style="width: 300px;"/>');
	addItem+=('</td>');
	addItem+=('<th  align="right">报价：</th>');
	addItem+=('<td>');
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
	initCombobox('quoteItemadd' + quoteItem,6,$("#parentId1").val());
	$(".numberbox").numberbox (
		{
			precision:2,
			max:99999999.99
		}
	)
}

function initCombobox(inputId,afterSaleDetectTypeId, parentId1) {
	$('#' + inputId).combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetail.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + "&parentId1=" + parentId1,
      	valueField:'id',
		textField:'text',
		delay:500
    });
}

function removeQuoteHtml(index) {
	$("#quoteItemadd"+index).parent().parent().remove();
}

function addQuotePriceFun(){
		if (!checkSubmit()) {
		return false;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/repairQuote.mmx',
		type : 'post',
		data :  {
				'flag' : '2',
				'afterSaleDetectProductCode' : $("#afterSaleDetectProductCode").val(),
				'imei' : $("#IMEI").val(),
				'quoteItem' : $("#quoteItem").combobox("getValue"),
				'quote' : $("#quote").numberbox("getValue"),
				'quoteItemadd' : $("#quotePriceDiv input[name=quoteItemadd]").val(),
				'quoteadd' : $("#quotePriceDiv input[name=quoteadd]").val(),
			},
			dataType : 'json',
			success : function(result) {
			try {
				$.messager.show({
					title : '提示',
					msg : result.msg
				});
				if(result.success){
					window.location.href="${pageContext.request.contextPath}/admin/afStock/addSupplierQuotePrice.jsp";
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function checkSubmit(){
	if($("#afterSaleDetectProductCode").val() == "") {
		$.messager.show({
			title : '提示',
			msg : '必须填写售后处理单号！'
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
	$("#quotePriceDiv input[name=quoteItemadd]").each(
		function(index) {
			if ($("#quotePriceDiv input[name=quoteItemadd]").eq(index).val() == "" && $("#quotePriceDiv input[name=quoteadd]").eq(index).val() !="") {
				$.messager.show({
					title : '提示',
					msg : '报价项为空，报价也得为空！'
				});
				return false;
			}
			if ($("#quotePriceDiv input[name=quoteItemadd]").eq(index).val()!= "" && $("#quotePriceDiv input[name=quoteadd]").eq(index).val() =="") {
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

function getDetectProductInfo(flag){
	if(flag=="2"){
		if($("#afterSaleDetectProductCode").val() == "") {
			$.messager.show({
				title : '提示',
				msg : '必须填写售后处理单号！'
			});
			return false;
		}
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getDetectProductInfo.mmx',
		data :  {'afterSaleDetectProductCode' : $("#afterSaleDetectProductCode").val(),
					'flag' : flag,
					'imei' : $("#imei").val(),
					'operate' : '1'},
		type : 'post',
		dataType : 'json',
		success : function(result){
			if (result.success) {
				if(flag=='1'){
					$("#afterSaleDetectProductCode").val(result.obj.code);
				}
				$("#productOriname").html(result.obj.productOriname);
				$("#parentId1").val(result.obj.parentId1);
				initCombobox('quoteItem',6,result.obj.parentId1);
			}else{
				$.messager.show({
					title : '提示',
					msg : result.msg
				});
			}
		}
	});
}
</script>
</head>
<body>
		<div id="quotePriceDiv">
				<table id="quotePriceTable" align="center" class="tableForm">
					<tr>
						<th align="right">IMEI码：</th>
						<td align="left"><input type="text" id="imei" name="imei" onblur="getDetectProductInfo(1);"/></td>
					</tr>
					<tr>
						<th align="right">售后处理单号：</th>
						<td align="left"><input id="afterSaleDetectProductCode" name="afterSaleDetectProductCode" onblur="getDetectProductInfo(2);"/></td>
					</tr>
					<tr>
						<th align="right">商品原名称：</th>
						<td align="left" colspan="3"><span id="productOriname"></span></td>
					</tr>
					<tr>
						<th align="right">报价项：</th>
						<td align="left">
							<input id="quoteItem" name="quoteItem" style="width: 300px;"/>
						</td>
						<th align="right">报价：</th>
						<td align="left">
							<input id="quote" name="quote" style="width: 116px;" class="easyui-numberbox" data-options="precision:2,max:99999999.99"/>
							<input id="parentId1" name="parentId1" type="hidden"/>
							<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addQuoteHtml();" href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr>
						<td></td>
						<td align="center"><a id="btn" class="easyui-linkbutton"  onclick="addQuotePriceFun();" href="javascript:void(0);">提交</a></td>
					</tr>
				</table>
		</div>
</body>