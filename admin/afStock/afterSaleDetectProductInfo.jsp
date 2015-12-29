<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="UTF-8">
var modifyDialog;
var datagrid;
var parentId1;
var printDialog;
var afterSaleDetectProductId;
var addForm;
var quoteItem = 0;
var repairQuoteDialog;
var repairQuoteForm;
var backSupplierProductId;
var detectCode;
var id = ${param.id};
var afterSaleDetectPackageId;
var afterSaleOrderId;
$(function() {
	loadInfo(${param.id});
	printDialog = $('#printDetectProductCodeDiv').show().dialog({
		modal : true,
		title:'打印',
	}).dialog('close');
});
function loadInfo(id){
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectProductDetail.mmx?id='+id,
		type : 'post',
		cache : false,
		dataType : 'json',
		success : function(data) {
			try {
				detectCode = data.footer[0].afterSaleDetectProductCode;
				if (data.footer[0].repairQuote) {
					$("#repairQuoteButton").show();
				}
				if (data.footer[0].canRepair) {
					$("#canRepairButton").show();
				}
				if (data.footer[0].checkIng) {
					$("#addCheckButton").show();
				}
				if (data.footer[0].returnButton) {
					$("#exchangeButton").show();
					$("#returnButton").show();
				}
				parentId1=data.footer[0].parent_id1;
				afterSaleDetectProductId=data.footer[0].afterSaleDetectProductId;
				afterSaleDetectPackageId=data.footer[0].afterSaleDetectPackageId;
				afterSaleOrderId=data.footer[0].afterSaleOrderId;
				if (data.footer[0].afterSaleDetectProductCode != 'undefined' && data.footer[0].afterSaleDetectProductCode !=null && data.footer[0].afterSaleDetectProductCode != "") {
					$("#afterSaleDetectProductCode").html("<font>售后处理单号:"+data.footer[0].afterSaleDetectProductCode+"</font>");
				} else {
					$("#afterSaleDetectProductCode").html("售后处理单号:");
				}
				if (data.footer[0].afterSaleOrderCode != 'undefined' && data.footer[0].afterSaleOrderCode !=null && data.footer[0].afterSaleOrderCode != "") {
					$("#afterSaleOrderCode").html("<font>售后单号:<a href=\"javascript:void(0);\" onclick=\"openAfterSaleOrder('" + afterSaleOrderId + "')\">" +data.footer[0].afterSaleOrderCode+ "</a></font>");
				} else {
					$("#afterSaleOrderCode").html("售后单号:");
				}
				if (data.footer[0].orderCode != 'undefined' && data.footer[0].orderCode !=null && data.footer[0].orderCode != "") {
					$("#orderCode").html("订单号:<a href=\"javascript:void(0);\" onclick=\"orderInfo("+data.footer[0].orderId+")\">"+data.footer[0].orderCode+"</a>");
				} else {
					$("#orderCode").html("订单号:");
				}
				if (data.footer[0].AfterSaleDetectProductStatusName != 'undefined' && data.footer[0].AfterSaleDetectProductStatusName !=null && data.footer[0].AfterSaleDetectProductStatusName != "") {
					$("#AfterSaleDetectProductStatusName").html("<font>售后处理单状态:"+data.footer[0].AfterSaleDetectProductStatusName+"</font>");
				} else {
					$("#AfterSaleDetectProductStatusName").html("售后处理单状态:");
				}
				if (data.footer[0].productCode != 'undefined' && data.footer[0].productCode !=null && data.footer[0].productCode != "") {
					$("#productCode").html("<font>商品编号:"+data.footer[0].productCode+"</font>");
				} else {
					$("#productCode").html("商品编号:");
				}
				if (data.footer[0].productName != 'undefined' && data.footer[0].productName !=null && data.footer[0].productName != "") {
					$("#productName").html("<font>商品名称:"+data.footer[0].productName+"</font>");
				} else {
					$("#productName").html("商品名称:");
				}
				if (data.footer[0].CargoWholeCode != 'undefined' && data.footer[0].CargoWholeCode !=null && data.footer[0].CargoWholeCode != "") {
					$("#CargoWholeCode").html("<font>货位号:"+data.footer[0].CargoWholeCode+"</font>");
				} else {
					$("#CargoWholeCode").html("货位号:");
				}
				if(data.footer[0].backSupplierProductStatus == '1'){
					backSupplierProductId = data.footer[0].backSupplierProductId;
					$("#but1").show();
					$("#but2").show();
				}else{
					$("#but1").hide();
					$("#but2").hide();
				}
				$("#repairQuoteDialog input[id=parentId1]").val(parentId1);
				$("#repairQuoteDialog input[id=detectProductId]").val(afterSaleDetectProductId);
				$('#quoteItem').combobox({
			      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetailForOneGrade.mmx?afterSaleDetectTypeId=6&parentId1=' + parentId1,
			      	valueField:'id',
					textField:'text',
					editable:false
			    });
				repairQuoteForm = $('#repairQuoteForm').form();
				repairQuoteDialog = $('#repairQuoteDialog').show().dialog({
					modal : true,
					minimizable : true,
					title : '添加维修报价',
					buttons : [{
						text : '添加维修报价',
						handler : function() {
							repairQuoteForm.form('submit', {
								url : '${pageContext.request.contextPath}/admin/AfStock/repairQuote.mmx?flag=1',
								success : function(data) {
									var d = $.parseJSON(data);
									if (d) {
										if (d.success) {
											repairQuoteDialog.dialog('close');
											reloadPage();
										}
										$.messager.show({
											msg : d.msg,
											title : '提示'
										});
									}
								}
							});
						}
					}],
					onClose : function() {
						quoteItem = 0;
						$("#repairQuoteDialog input[name=quoteadd]").parent().parent().remove();
						$("#repairQuoteDialog input[id=quoteItem]").combobox("setValue", "");
						$("#repairQuoteDialog input[id=quote]").numberbox("setValue", "");
					}
				}).dialog('close');
				$('#dd').dialog({    
				    title: '请选择不合格原因',    
				    width: 800,    
				    height: 120,    
				    closed: false,    
				    modal: true,
				    buttons:[{
						text:'确定',
						handler:function(){
							var faultDescription = $('#faultDescriptId').combobox('getText')+"/"+$('#faultDescriptId2').combobox('getText')+"/"+$('#faultDescriptId3').combobox('getText');
							if(faultDescription == null || faultDescription == ''){
								return;
							}
							$.ajax({
							    url : "${pageContext.request.contextPath}/admin/AfStock/detectAfterSaleProduct.mmx",
								type : "POST",
								dataType : 'json',
								cache: false,
								data : {
									flag : '2',
									backSupplierProductId : backSupplierProductId,		
									faultDescription : faultDescription
								},
								success: function(d){
									if (d) {
										$('#dd').dialog().dialog('close');
										$.messager.show({
											msg : d.msg,
											title : '提示'
										});
									}
								}
							});
						}
					}]
				}).dialog('close');
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		}
	}
);
	
	
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectPackageInfo.mmx?id='+id,
		type : 'post',
		cache : false,
		dataType : 'json',
		success : function(data) {
			try {
				if (data.footer[0].deliverName != 'undefined' && data.footer[0].deliverName !=null && data.footer[0].deliverName != "") {
					$("#deliverName").html("<font>快递公司:"+data.footer[0].deliverName+"</font>");
				} else {
					$("#deliverName").html("快递公司: ");
				}
				if (data.footer[0].packageCode != 'undefined' && data.footer[0].packageCode !=null && data.footer[0].packageCode != "") {
					$("#packageCode").html("<font>快递单号:"+data.footer[0].packageCode+"</font>");
				} else {
					$("#packageCode").html("快递单号: ");
				}
				if (data.footer[0].freight != 'undefined' && data.footer[0].freight !=null && data.footer[0].freight != "") {
					$("#freight").html("<font>运费金额:"+data.footer[0].freight+"</font>");
				} else {
					$("#freight").html("运费金额: ");
				}
				if (data.footer[0].senderName != 'undefined' && data.footer[0].senderName !=null && data.footer[0].senderName != "") {
					$("#senderName").html("<font>包裹发货人:"+data.footer[0].senderName+"</font>");
				} else {
					$("#senderName").html("包裹发货人: ");
				}
				if (data.footer[0].createDatetime != 'undefined' && data.footer[0].createDatetime !=null && data.footer[0].createDatetime != "") {
					$("#createDatetime").html("<font>包裹发货日期:"+data.footer[0].createDatetime.substring(0,19)+"</font>");
				} else {
					$("#createDatetime").html("包裹发货日期: ");
				}
				if (data.footer[0].remark != 'undefined' && data.footer[0].remark !=null && data.footer[0].remark != "") {
					$("#remark").html("<font>备注:"+data.footer[0].remark+"</font>");
				} else {
					$("#remark").html("备注: ");
				}
				if(data.footer[0].packageId>0){
					$('#but3').show();
				}
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		}
	});
	
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectLogDetail.mmx?id='+id,
		type : 'post',
		cache : false,
		success : function(data) {
			 	$('#checkDiv').empty();
	    		$('#checkDiv').append(data);
		}
	});
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/getUnqualifiedReasonLog.mmx?id='+id,
		type : 'post',
		dataType : 'json',
		cache : false,
		success : function(r) {
			$('#unqualifiedDiv').empty();
			if(r.success == true){
				var txt = "	<div  id=\"unqualified\" style=\"background-color: #E6E6FA;width: 70%;\"> " +
									"<h2 style=\"color: #FF3030;\">不合格记录</h2>" + 
							  "</div>";
				$('#unqualifiedDiv').append(txt);
				$.each(r.obj,function(index,value){
					var unqualifiedReasonName = value.unqualifiedReasonName;
					var unqualifiedReasonName2 = value.unqualifiedReasonName2;
					var unqualifiedReasonName3 = value.unqualifiedReasonName3;
					if(unqualifiedReasonName != null && unqualifiedReasonName != '' && unqualifiedReasonName2 != null && unqualifiedReasonName2 != ''){
						unqualifiedReasonName += "/"+unqualifiedReasonName2;
						if(unqualifiedReasonName3 != null && unqualifiedReasonName3 != ''){
							unqualifiedReasonName += "/"+unqualifiedReasonName3;
						}
					}
					var data = "<table style=\"width: 100%;text-align: left;background-color: azure;\"> " + 
										"<tr>" + 
											"<th style=\"color: #242424;width: 70px;\">故障描述:</th>" + 
											"<td  width=\"22%\"> " +
												"<label style=\"color: darkslateblue\">" + unqualifiedReasonName + "</label></td> " + 
											"<th style=\"color: #242424;width: 70px;text-align: right;\">返厂时间:</th> " + 
											"<td width=\"25%\"> " + 
												"<label  style=\"color: darkslateblue\">" + value.sendDatetime + "</label></td> " + 
											"<th  style=\"color: #242424;width: 70px;text-align: right;\">操作人:</th> " + 
											"<td width=\"22%\"> " + 
												"<label  style=\"color: darkslateblue\">" + value.senderName + "</label></td> " + 
										"</tr> " + 
								   "</table><br>";
					$('#unqualified').append(data);
				});
			}
		}
	});
}
function addcheck(){
	addDialog.dialog('open');
}
function openAfterSaleOrder(id) {
	window.open("https://sales.ebinf.com/sale/admin/toEdit.mmx?id=" + id, "_blank");
}
function repairQuote() {
	repairQuoteDialog.dialog('open');
}
function getInputValueByName(items){
	var info = '';
	for (var i = 0; i < items.length; i++) {
     // 如果i+1等于选项长度则取值后添加空字符串，否则为逗号
     info = (info + items.get(i).value) + (((i + 1)== items.length) ? '':',');
	}
	return info;
}
function getInputTextByName(items){
	var info = '';
	if(quoteItem > 0 ){
		for(var i = 1; i <= quoteItem;i++){
			if(info != ''){
				info += ",";
			}
			var str = $("#quoteItemadd"+i).combobox("getText");
			info += str;
		}
	}
	return info;
}
function detectProductFun() {
	if (!checkSubmit()) {
		return false;
	}
	var fittingsNames = getInputValueByName($("input[name='fittingsNames']"));
	var fittingsIds  = getInputValueByName($("input[name='fittingsIds']"));
	var intactFittingsCounts = getInputValueByName($("input[name='intactFittingsCounts']"));
	var badFittingsCounts = getInputValueByName($("input[name='badFittingsCounts']"));
	var quoteItemadds = getInputTextByName($("input[name=quoteItemadd]"));
	var quoteadds = getInputValueByName($("input[name=quoteadd]"));
	
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/addAfterSaleDetectLog.mmx',
		type: "post",
		data :  {
				'id' : $("#detectProduct input[id=id]").val(),
				'afterSaleDetectPackageId' : $("#detectProduct input[id=afterSaleDetectPackageId]").val(),
				'afterSaleOrderId' : $("#detectProduct input[id=afterSaleOrderId]").val(),
				'parentId1' : $("#detectProduct input[id=parentId1]").val(),
				'afterSaleDetectPackageId' : $("#detectProduct input[id=afterSaleDetectPackageId]").val(),
				'afterSaleOrderId' : $("#detectProduct input[id=afterSaleOrderId]").val(),
				'mainProductStatus' :  $('input:radio[name="mainProductStatus"]:checked').val(),
				'handle' : $("#handle").combobox("getValue"),
				'IMEI' : $("#detectProduct input[id=IMEI]").val(),
				'remark' : $("#detectProduct textarea[id=remark]").val(),
				'questionDescription' : $("#questionDescription").combobox("getText")+"/"+$("#questionDescription2").combobox("getText")+"/"+$("#questionDescription3").combobox("getText"),
				'damaged' : $("#damaged").combobox("getText")+"/"+$("#damaged2").combobox("getText")+"/"+$("#damaged3").combobox("getText"),
				'giftAll' : $("#giftAll").combobox("getText")+"/"+$("#giftAll2").combobox("getText")+"/"+$("#giftAll3").combobox("getText"),
				'reportStatus' : $("#reportStatus").combobox("getText")+"/"+$("#reportStatus2").combobox("getText")+"/"+$("#reportStatus3").combobox("getText"),
				'faultCode' : $("#detectProduct input[id=faultCode]").val(),
				'faultDescription' : $("#faultDescription").combobox("getValue")+"/"+$("#faultDescription2").combobox("getValue")+"/"+$("#faultDescription3").combobox("getValue"),
				'detectException' : $("#detectProduct input[name=detectException]:checked").val(),
				'exceptionReason' : $("#exceptionReason").combobox("getText")+"/"+$("#exceptionReason2").combobox("getText")+"/"+$("#exceptionReason3").combobox("getText"),
				'quoteItem1' :  $("#quoteItem1").combobox("getText"),
				'quote1' : $("#quote1").numberbox("getValue"),
				'debitNote' : $("#debitNote").combobox("getText"),
				'quoteItemadd' : quoteItemadds,
				'quoteadd' : quoteadds
			},
			success : function(result) {
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					printDialog.dialog("open");
					$("#printDetectProductCodeDiv label[id=code]").html(r.obj);
					$("#printDetectProductCodeDiv input[id=code]").val(r.obj);
				}
				$.messager.show({
					title : '提示',
					msg : decodeURI(r.msg)
				});
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}
function checkSubmit() {
	if($("#questionDescription").combobox("getValue") == "") {
		$.messager.show({
			title : '提示',
			msg : '必须选择问题分类！'
		});
		return false;
	}
	if($("#damaged").combobox("getValue") == "") {
		$.messager.show({
			title : '提示',
			msg : '必须选择包装！'
		});
		return false;
	}
	if($("#giftAll").combobox("getValue") == "") {
		$.messager.show({
			title : '提示',
			msg : '必须选择赠品！'
		});
		return false;
	}
	if($("#handle").combobox("getValue") == "") {
		$.messager.show({
			title : '提示',
			msg : '必须选择处理意见！'
		});
		return false;
	}
	var list= $('input:radio[name="mainProductStatus"]:checked').val();
	if(list==null){
		$.messager.show({
			title : '提示',
			msg : '主商品状态必须选！'
		});
		return false;
	}
	if($("#handle").combobox("getValue") == "检测异常" ) {
		if ($("#detectProduct input[id=detectException]").attr("checked")) {
			if($("#exceptionReason").combobox("getValue") == "请选择") {
				$.messager.show({
					title : '提示',
					msg : '必须选择异常原因！'
				});
				return false;
			}
		} else {
			$.messager.show({
				title : '提示',
				msg : '检测异常必须勾选！'
			});
			return false;
		}
	} else {
		if ($("#detectProduct input[id=detectException]").attr("checked")) {
			$.messager.show({
				title : '提示',
				msg : '处理意见不是检测异常，不能勾选检测异常！'
			});
			return false;
		} else {
			if($("#exceptionReason").combobox("getValue") != "") {
				$.messager.show({
					title : '提示',
					msg : '异常原因必须为空！'
				});
				return false;
			}
		}
	}
	if($("#quoteItem1").combobox("getValue") == "" && $("#quote1").numberbox("getValue") !="") {
		$.messager.show({
			title : '提示',
			msg : '报价项为空，报价也得为空！'
		});
		return false;
	}
	if($("#quoteItem1").combobox("getValue") != "" && $("#quote1").numberbox("getValue") =="") {
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
function addQuoteHtml(flag) {
	quoteItem += 1;
	var tr = $("#repairQuoteDialog input[id=detectProductId]").parent().parent();
	if (flag) {
		tr = $("#detectProduct textarea[id=remark]").parent().parent();
	}
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
	initCombobox('quoteItemadd' + quoteItem,6,$("#repairQuoteDialog input[id=parentId1]").val());
	$(".numberbox").numberbox (
		{
			precision:2,
			max:99999999.99
		}
	)
}

function initCombobox(inputId,afterSaleDetectTypeId, parentId1,disabled,editable) {
	$('#' + inputId).combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetailForOneGrade.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + '&parentId1=' + parentId1,
      	valueField:'id',
		textField:'text',
		delay:500,
		disabled:disabled,
		editable:editable,
		onSelect : function(record){
			if(record.id==""){
				$('#' + inputId +'2').combobox('setText','请选择');
				$('#' + inputId +'2').combobox('setValue','');
				$('#' + inputId +'3').combobox('setText','请选择');
				$('#' + inputId +'3').combobox('setValue','');
				$('#' + inputId +'2').combobox('disable');
				$('#' + inputId +'3').combobox('disable');
				return false;
			}
			$('#' + inputId +'2').combobox({
		      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetailForTwoGrade.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + '&id=' + record.id,
		      	valueField:'id',
				textField:'text',
				delay:500,
				disabled:disabled,
				editable:editable,
				onSelect : function(record){
					if(record.id==""){
						$('#' + inputId +'3').combobox('setText','请选择');
						$('#' + inputId +'3').combobox('setValue','');
						$('#' + inputId +'3').combobox('disable');
						return false;
					}
					$('#' + inputId +'3').combobox({
				      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetailForThreeGrade.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + '&id=' + record.id,
				      	valueField:'id',
						textField:'text',
						delay:500,
						disabled:disabled,
						editable:editable
				    });
				}
		    });
		}
    });
}

function removeQuoteHtml(index) {
	$("#quoteItemadd"+index).parent().parent().remove();
}

function checkSubmitQuote() {
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
	$("#repairQuoteDialog input[name=quoteItemadd]").each(
		function(index) {
			if ($("#repairQuoteDialog input[name=quoteItemadd]").eq(index).val() == "" && $("#repairQuoteDialog input[name=quoteadd]").eq(index).val() !="") {
				$.messager.show({
					title : '提示',
					msg : '报价项为空，报价也得为空！'
				});
				return false;
			}
			if ($("#repairQuoteDialog input[name=quoteItemadd]").eq(index).val()!= "" && $("#repairQuoteDialog input[name=quoteadd]").eq(index).val() =="") {
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

function canRepair() {
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/canRepair.mmx',
		type : 'post',
		cache : false,
		dataType : 'text',
		data:"detectProductId=" + afterSaleDetectProductId,
		success : function(data) {
			try {
				var r = $.parseJSON(data);
				$.messager.show({
					title : '提示',
					msg : decodeURI(r.msg)
				});
				if (r.success) {
					reloadPage();
				}
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		}
	});
}
function canntRepair(type) {
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/canntRepair.mmx?type='+type,
		type : 'post',
		cache : false,
		dataType : 'text',
		data:"detectProductId=" + afterSaleDetectProductId,
		success : function(data) {
			try {
				var r = $.parseJSON(data);
				$.messager.show({
					title : '提示',
					msg : decodeURI(r.msg)
				});
				if (r.success) {
					//reloadPage();
					loadInfo(${param.id});
				}
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		}
	});
}
function reloadPage() {
	loadInfo(${param.id});
}
function returnButton() {
	window.location.href="${pageContext.request.contextPath}/admin/afStock/canReturn.mmx?id="+$("#afterSaleDetectProductId").val();
}
function detectFun(flag){
    initCombobox('faultDescriptId',4,parentId1,false,false);
    initCombobox('faultDescriptId2',4,-1,true,false);
    initCombobox('faultDescriptId3',4,-1,true,false);
	if(flag == '2'){
		$('#dd').dialog().dialog('show');;
	} else {
		$.ajax({
		    url : "${pageContext.request.contextPath}/admin/AfStock/detectAfterSaleProduct.mmx",
			type : "POST",
			dataType : 'json',
			cache: false,
			data : {
				flag : flag,
				backSupplierProductId : backSupplierProductId,
				detectCode : detectCode		
			},
			success: function(d){
				if (d) {
					$.messager.show({
						msg : d.msg,
						title : '提示'
					});
				}
			}
		});
	}
}
function detectDialog() {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/detectProductInfo.jsp',
		width : 1200,
		height : 600,
		modal : true,
		title : '检测商品',
		buttons : [ {
			id:"detectProductsClose",
			text:"关闭",
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
				quoteItem=0;
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
			quoteItem=0;
		},
		onLoad : function() {
			getRadio(parentId1,afterSaleDetectProductId,afterSaleDetectPackageId,afterSaleOrderId);
		}
	});
	
}
function getRadio(parentId1,afterSaleDetectProductId,afterSaleDetectPackageId,afterSaleOrderId){
	$("#detectProduct input[id=afterSaleDetectPackageId]").val(afterSaleDetectPackageId);
	$("#detectProduct input[id=afterSaleOrderId]").val(afterSaleOrderId);
	$("#detectProduct input[id=id]").val(afterSaleDetectProductId);
	initCombobox('questionDescription',1,parentId1,false,false);
	initCombobox('questionDescription2',1,-1,true,false);
	initCombobox('questionDescription3',1,-1,true,false);
	initCombobox('damaged',2,parentId1,false,false);
	initCombobox('damaged2',2,-1,true,false);
	initCombobox('damaged3',2,-1,true,false);
	initCombobox('giftAll',3,parentId1,false,false);
	initCombobox('giftAll2',3,-1,true,false);
	initCombobox('giftAll3',3,-1,true,false);
	initCombobox('faultDescription',4,parentId1,false,false);
	initCombobox('faultDescription2',4,-1,true,false);
	initCombobox('faultDescription3',4,-1,true,false);
	initCombobox('reportStatus',5,parentId1,false,false);
	initCombobox('reportStatus2',5,-1,true,false);
	initCombobox('reportStatus3',5,-1,true,false);
	initCombobox('quoteItem1',6,parentId1);
	initCombobox('exceptionReason',7,parentId1,false,false);
	initCombobox('exceptionReason2',7,-1,true,false);
	initCombobox('exceptionReason3',7,-1,true,false);
	$('#handle').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getHandles.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#debitNote').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getDebitNoteSelect.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$.ajax({
		url : '${pageContext.request.contextPath}/Combobox/getMainProductStatus.mmx',
		type : 'post',
		success : function(result){
			$("#productStatusDiv").html(result);
		}
	});
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleFittings.mmx',
		type : 'post',
		data : {id:afterSaleDetectProductId,flag:'2'},
		success : function(result){
			$("#badFittingDiv").html(result);
		}
	});
	$("#detectProduct input[id=parentId1]").val(parentId1);
	quoteItem = 0;
	$('#detectProductDiv').hide();
	$('#detectProduct').show();
	$("#detectProduct input[name=quoteadd]").parent().parent().remove();
	$("#detectProduct input[id=quote]").numberbox("setValue", "");
	$("#detectProduct input[id=IMEI]").val("");
	$("#detectProduct input[id=faultCode]").val("");
	$("#detectProduct textarea[id=remark]").val("");
	$("#detectProduct input[id=exceptionReason]").combobox("disable", true);
	$("#detectProduct input[id=exceptionReason2]").combobox("disable", true);
	$("#detectProduct input[id=exceptionReason3]").combobox("disable", true);
	$("#detectProduct input[id=detectException]").removeAttr("checked");
	$("#detectProductForm").form({
		url : '${pageContext.request.contextPath}/admin/AfStock/addAfterSaleDetectLog.mmx',
		success : function(result) {
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					printDialog.dialog("open");
					$("#printDetectProductCodeDiv label[id=code]").html(r.obj);
					$("#printDetectProductCodeDiv input[id=code]").val(r.obj);
					$("#detectProductsClose").click();
				}
				$.messager.show({
					title : '提示',
					msg : decodeURI(r.msg)
				});
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}
function exeGetChecked() {
	if ($("#detectProduct input[id=detectException]").attr("checked")) {
		$("#detectProduct input[id=exceptionReason]").combobox("disable", false);
		initCombobox('exceptionReason',7,$("#detectProduct input[id=parentId1]").val(),false,false);
	} else {
		initCombobox('exceptionReason',7,$("#detectProduct input[id=parentId1]").val(),false,false);
		$("#detectProduct input[id=exceptionReason]").combobox("disable", true);
	}
}
function closeFun() {
	printDialog.dialog("close");
}
function afterSaleDetectProductCodePrint() {
	closeFun();
	$("#detectProduct").hide();
	window.open('${pageContext.request.contextPath}/admin/AfStock/afterSaleDetectProductCodePrint.mmx?code='+$("#printDetectProductCodeDiv input[id=code]").val(),"_blank");
}
function orderInfo(id){ 
	window.location.href = '${pageContext.request.contextPath}/admin/order.do?id='+id;
}
function openLog(){
	window.open('${pageContext.request.contextPath}/admin/afStock/afterSaleDetectProductLog.jsp?detectCode=' + detectCode,"_blank");
}
function sendMessage(){
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/sendMessage.mmx',
		type : 'post',
		data : {id:${param.id}},
		dataType : 'json',
		success : function(data){
			$.messager.show({
				title : '提示',
				msg : data.msg
			});
		}
	});
}
</script>
</head>
<body>
	<div id="dd" style="text-align: center;height: 200px;margin-top: 5px">
		故障描述:<input id="faultDescriptId" required="required"  style="width: 156px" >
				<input id="faultDescriptId2"  style="width: 156px" >
				<input id="faultDescriptId3"  style="width: 156px" >
	</div>
	<div id="repairQuoteDialog" style="overflow-y:auto; width:600px;height:300px; overflow-x:auto; display: none;" >
		<form id="repairQuoteForm"  method="post">
				<table>
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
					<tr>
						<td>
							<input id="detectProductId" name="detectProductId" type="hidden"/>
						</td>
					</tr>
				</table>
		</form>
	</div>
<font size="4" color="blue"><strong>售后处理单</strong></font> &nbsp;&nbsp;<a href="javascript:openLog()"><font size="3" color="blue"><strong>操作记录</strong> </font></a>
			<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
				<tr>
					<td width="25%"><h4 id="afterSaleDetectProductCode" name="afterSaleDetectProductCode"></h4></td>
					<td width="25%"><h4 id="afterSaleOrderCode" name="afterSaleOrderCode"></h4></td>
					<td width="25%"><h4 id="orderCode" name="orderCode"></h4></td>
					<td width="25%"><h4 id="AfterSaleDetectProductStatusName" name="AfterSaleDetectProductStatusName"></h4></td>
				</tr>
				<tr>
					<td><h4 id="productCode" name="productCode"></h4></td>
					<td  colspan="2"><h4 id="productName" name="productName"></h4></td>
					<td><h4 id="CargoWholeCode" name="CargoWholeCode"></h4></td>
				</tr>
			</table>
			<br/>
			<div id="checkDiv"></div>
			<div id="unqualifiedDiv"></div>
			<font size="4" color="blue"><strong>寄回包裹信息</strong></font>
			<button  id="but3" style="display:none"  onclick="sendMessage()">发送快递短信</button>&nbsp;&nbsp;&nbsp;&nbsp;
			<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
				<tr align="LEFT" >
					<td width="20%"><h4 id="deliverName" name="deliverName"></h4></td>
					<td width="20%"><h4 id="packageCode" name="packageCode"></h4></td>
					<td width="20%"><h4 id="freight" name="freight"></h4></td>
					<td width="20%"><h4 id="senderName" name="senderName"></h4></td>
					<td width="20%"><h4 id="createDatetime" name="createDatetime"></h4></td>
				</tr>
				<tr align="LEFT" >
					<td width="25%" colspan="5"><h4 id="remark" name="remark"></h4></td>
				</tr>
			</table>
			<br>
&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" name="addCheckButton" id="addCheckButton" onclick="detectDialog()" style="display:none" value="添加检测记录" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" name="repairQuoteButton" id="repairQuoteButton" onclick="repairQuote();" style="display:none" value="添加厂商报价" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" name="canRepairButton" id="canRepairButton" onclick="canRepair();"  style="display:none" value="可以维修" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" name="exchangeButton" id="exchangeButton" onclick="canntRepair(9)"  style="display:none" value="无法维修建议换货" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" name="returnButton" id="returnButton" onclick="canntRepair(8)"  style="display:none" value="无法维修建议退货" />&nbsp;&nbsp;&nbsp;&nbsp;
						<button  id="but1" style="display:none"  onclick="detectFun('1')">检测合格</button>&nbsp;&nbsp;&nbsp;&nbsp;
						<button  id="but2" style="display:none" onclick="detectFun('2')">检测不合格</button>&nbsp;&nbsp;&nbsp;&nbsp;
	<div id="printDetectProductCodeDiv" style="display:none">
		检测记录已添加，售后处理号为：<label id=code></label>
		<input id=code type="hidden"/>
		<a class="easyui-linkbutton" onclick="afterSaleDetectProductCodePrint();" href="javascript:void(0);">打印售后处理单号</a>
		<a class="easyui-linkbutton"  onclick="closeFun();" href="javascript:void(0);">已打印    关闭</a>
	</div>
</body>
</html>