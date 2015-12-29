<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>检测</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
<style type="text/css">  
<!--  
h3 {display : inline}  
-->  
</style>  
<script type="text/javascript" charset="UTF-8">
var printDialog;
var indexId= 0;
var quoteItem = 0;
var fittingDialog;
$(function(){
	$("#tb [id=packageCode]").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
            searchFun();
            return false;
        }
    });
	$("#tb [id=packageCode]").focus();
	
	printDialog = $('#printDetectProductCodeDiv').show().dialog({
		modal : true,
		title:'打印',
	}).dialog('close');
});

function initPackageAfterSaleData() {
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/packageAfterSaleDatagrid.mmx',
		data : {'packageCode':$.trim($("#tb input[id=packageCode]").val())},
		type : 'post',
		success : function(result){
			try {
				var r = eval(result);
				if (r.success) {
					$("#packageAfterSaleDataGrid").html(r.obj);
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				var r = $.parseJSON(result);
				if (r.success) {
					$("#packageAfterSaleDataGrid").html(r.obj);
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			}
		}
	});
}

function searchFun() {
	var packageCode = $.trim($("#tb input[id=packageCode]").val());
	if (packageCode == '') {
		$.messager.show({
			title : '提示',
			msg : '必须填写包裹单号！'
		});
		return false;
	}
	initPackageAfterSaleData();
}

function getAfterSaleRadio(id) {
	$("#packageAfterSaleIndex").val(id);
	$("#afterSaleProducts").show();	
	$("#detectProduct").hide();
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/isShowModify.mmx',
		type : 'post',
		data : {
			packageCode : $.trim($("#tb input[id=packageCode]").val()),
			id : id	
			},
		dateType : 'json',
		success : function(result){
			var r = $.parseJSON(result);
			if(r.success){
				$('#btn').show();
				$('#packageId').val(r.obj.id);
			}else{
				$('#btn').hide();
			}
			initDetectProductDatagrid(id);
		}
	});
}

function initDetectProductDatagrid(id) {
	$('#detectProductDatagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getDetectProductDatagrid.mmx',
	    queryParams: {
	    	id : id
	    },
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    singleSelect : true,
	    columns:[[  
			{field:'action',title:'选择',width:60,align:'center',
				formatter : function(value, row, index) {
					if (row.status != 0 && row.status != 1) {
		        		return '<input type="radio" id="id'+index+'" name="detectProductsd" value="'+index+'" onclick="getRadio('+row.parentId1+','+row.afterSaleDetectPackageId+','+row.afterSaleOrderId+','+row.id+','+index+')" disabled=disabled>';
					} else {
						return '<input type="radio" id="id'+index+'" name="detectProductsd" value="'+index+'" onclick="getRadio('+row.parentId1+','+row.afterSaleDetectPackageId+','+row.afterSaleOrderId+','+row.id+','+index+')">';
					}
				}
			},
			{field:'productCode',title:'商品编号',width:60,align:'center',
				formatter : function(value, row, index) {
					return "<a href='${pageContext.request.contextPath}/admin/fproduct.do?id="+ row.productId +"' target=\"_blank\" > "+ value +"</a>";
				}
			},
			{field:'productName',title:'商品名称',width:100,align:'center'},
			{field:'parentId1Name',title:'商品一级分类',width:80,align:'center'},
			{field:'price5',title:'商品金额',width:80,align:'center'},
			{field:'inBuyOrderName',title:'是否为售后单商品',width:80,align:'center'},
			{field:'inUserOrderName',title:'是否为订单商品',width:80,align:'center'},
			{field:'flatName',title:'订单来源',width:50,align:'center'},
			{field:'sellTypeName',title:'销售属性',width:80,align:'center'},
			{field:'remark',title:'签收备注',width:80,align:'center'}
	    ] ],
		onLoadSuccess : function(data) {
		}
	}); 
}

function getRadio(parentId1,afterSaleDetectPackageId,afterSaleOrderId,id,index){
	$("#detectProduct input[id=afterSaleDetectPackageId]").val(afterSaleDetectPackageId);
	$("#detectProduct input[id=afterSaleOrderId]").val(afterSaleOrderId);
	$("#detectProduct input[id=id]").val(id);
	$("#detectProduct").show();
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
	initCombobox('quoteItem',6,parentId1);
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
			data : {id:id},
			dateType : 'json',
			success : function(result){
				try{
					if(result!=null){
						var data = jQuery.parseJSON(result);
						if(data.footer!=null){
							var fittingsDiv = $("#fittingsDiv");
							var fittings = "<table id=\"fittingsTable\" class=\"tableForm\" align=\"center\">";
							fittings += "<tr><th>完好配件：</th>";
							for(var i=0;i< data.footer.length;i++){
								fittings += "<th>"+ data.footer[i].fittingName +"<input type='hidden' name='fittingsNames' value='" + data.footer[i].fittingName + "'/></th>";
								fittings += "<td><input type='text' name='intactFittingsCounts' size='3' value='0'/><input type='hidden' name='fittingsIds' value='"+ data.footer[i].fittingId +"'/></td>";
							}
							fittings += "</tr>";
							fittings += "<tr><th>损坏配件：</th>";
							for(var i=0;i< data.footer.length;i++){
								fittings += "<th>"+ data.footer[i].fittingName + "</th>";
								fittings += "<td><input type='text' name='badFittingsCounts' size='3' value='0'/></td>";
							}
							fittings += "</tr>";
							fittings += "</table>";
							fittingsDiv.html(fittings);
						}else{
							$.messager.alert("提示", data.tip ,"info");
						}
					}
				}catch(e) {
					$.messager.alert("提示", "错误" ,"info");
				}
			}
		});
	
	fittingDialog = $("#fittingDialog").show().dialog({
		width : 600,
		height : 300,
		modal : true,
		minimizable : true,
		title : '寄回配件信息',
		buttons : [{
			text : '确定',
			handler : function() {
				$("#fittingDialog").dialog("close");
			}
		}]
	}).dialog('close');
	
	$("#detectProduct input[id=parentId1]").val(parentId1);
	quoteItem = 0;
	$("#detectProduct input[name=quoteadd]").parent().parent().remove();
	$("#detectProduct input[id=quote]").numberbox("setValue", "");
	$("#detectProduct input[id=IMEI]").val("");
	$("#detectProduct input[id=faultCode]").val("");
	$("#detectProduct textarea[id=remark]").val("");
	$("#detectProduct input[id=exceptionReason]").combobox("disable", true);
	$("#detectProduct input[id=exceptionReason2]").combobox("disable", true);
	$("#detectProduct input[id=exceptionReason3]").combobox("disable", true);
	$("#detectProduct input[id=detectException]").removeAttr("checked");
	$("#detectProduct input[id=index]").val(index);
	$("#detectProduct input[id=afterSaleDetectProductCode]").val("");
	$("#detectProductForm").form({
		url : '${pageContext.request.contextPath}/admin/AfStock/addDetectProduct.mmx',
		success : function(result) {
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					printDialog.dialog("open");
					$("#printDetectProductCodeDiv label[id=code]").html(r.obj);
					$("#printDetectProductCodeDiv input[id=code]").val(r.obj);
					disabledRadio();
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

function disabledRadio() {
	var packageAfterSaleFlag = false;
	var index = $("#index").val();
	$("#id" + index).attr("disabled", "disabled");
	$("input[name=detectProductsd]").each(
		function(index) {
			if (!$("input[name=detectProductsd]").eq(index).attr("disabled")) {
				packageAfterSaleFlag = true;
				return false;
			}
		}
	);
	if (!packageAfterSaleFlag) {
		$("#afterSaleId" + $("#packageAfterSaleIndex").val()).attr("disabled", "disabled");
	}
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


function addQuoteHtml() {
	quoteItem += 1;
	var tr = $("#detectProduct textarea[id=remark]").parent().parent();
	var addItem = "";
	addItem+=('<tr align="center">');
	addItem+=('<th>报价项：</th>');
	addItem+=('<td align="left" colspan="3">');
	addItem+=('<input id="quoteItemadd'+quoteItem+'" name="quoteItemadd" style="width: 300px;"/>');
	addItem+=('</td>');
	addItem+=('<th>报价：</th>');
	addItem+=('<td align="left">');
	addItem+=('<input id="quoteadd'+quoteItem+'" name="quoteadd" style="width: 75px;" class="numberbox"/>&nbsp;');
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
	initCombobox('quoteItemadd' + quoteItem,6,$("#detectProduct input[id=parentId1]").val());
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

function exeGetChecked() {
	if ($("#detectProduct input[id=detectException]").attr("checked")) {
		$("#detectProduct input[id=exceptionReason]").combobox("disable", false);
		initCombobox('exceptionReason',7,$("#detectProduct input[id=parentId1]").val(),false,false);
	} else {
		initCombobox('exceptionReason',7,$("#detectProduct input[id=parentId1]").val(),false,false);
		$("#detectProduct input[id=exceptionReason]").combobox("disable", true);
	}
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
		url : '${pageContext.request.contextPath}/admin/AfStock/addDetectProduct.mmx',
		type: "post",
		data :  {
				'afterSaleDetectProductCode' : $("#detectProduct input[id=afterSaleDetectProductCode]").val(),
				'parentId1' : $("#detectProduct input[id=parentId1]").val(),
				'id' : $("#detectProduct input[id=id]").val(),
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
				'quoteItem' : $("#quoteItem").combobox("getText"),
				'quote' : $("#quote").numberbox("getValue"),
				'debitNote' : $("#debitNote").combobox("getText"),
				'quoteItemadd' : quoteItemadds,
				'quoteadd' : quoteadds,
				'fittingsNames' : fittingsNames,
				'fittingsIds' : fittingsIds,
				'intactFittingsCounts' :  intactFittingsCounts,
				'badFittingsCounts' :  badFittingsCounts
			},
			success : function(result) {
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					printDialog.dialog("open");
					$("#printDetectProductCodeDiv label[id=code]").html(r.obj);
					$("#printDetectProductCodeDiv input[id=code]").val(r.obj);
					disabledRadio();
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
	if($("#detectProduct input[id=afterSaleDetectProductCode]").val() == "") {
		$.messager.show({
			title : '提示',
			msg : '必须填写售后处理单号！'
		});
		return false;
	}
	if($("#questionDescription").combobox("getValue") == "") {
		$.messager.show({
			title : '提示',
			msg : '必须选择问题分类！'
		});
		return false;
	}
	if($("#faultDescription").combobox("getValue") == "") {
		$.messager.show({
			title : '提示',
			msg : '必须选择故障描述！'
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
			if($("#exceptionReason").combobox("getText") == "请选择") {
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

function closeFun() {
	printDialog.dialog("close");
}

function openOrder(orderId) {
	window.open("${pageContext.request.contextPath}/admin/order.do?id=" + orderId, "_blank");
}

function openAfterSaleOrder(id) {
	window.open("https://sales.ebinf.com/sale/admin/toEdit.mmx?id=" + id, "_blank");
}

function showFittingDiv(){
	fittingDialog.dialog('open');
}

function modifySignProducts(){
	var afterSaleOrderId = $.trim($("input[name=afterSaleOrder]:checked").val());
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/addAfterSaleDetectProducts.jsp',
		width : 900,
		height : 500,
		modal : true,
		maximizable : true,
		title : '匹配商品',
		buttons : [ {
			id:"addProductsClose",
			text:"关闭",
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
		},
		onLoad : function() {
			$.ajax({
                type: "post", //调用方式  post 还是 get
                url: '${pageContext.request.contextPath}/admin/AfStock/addAfterSaleDetectProducts.mmx',
                async:false,
                data : "flag=true&afterSaleOrderIds="+afterSaleOrderId,
                dataType: "text", //返回的数据的形式
                success: function(data) { 
                	$("#afterSaleDetectPackageId").val(afterSaleDetectPackageId);
                	$("#afterSaleOrderProducts").html(data);
                }
			});
		}
	});
}

function initPackageProducts() {
	var packageCode = $.trim($('#packageCode').val());
	$('#packageProductsDataGrid').datagrid({
		toolbar:"packageProductsToolbar",
		url:'${pageContext.request.contextPath}/admin/AfStock/getMatchedProducts.mmx?packageCode=' + packageCode,
	    fit : true,
	    fitColumns : true,
	    idField:'id',
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    columns:[[  
	        {field:'id',width:60,align:'center',hidden:true},
	        {field:'afterSaleDetectProductId',width:60,align:'center',hidden:true},
			{field:'afterSaleOrderId',width:60,align:'center',hidden:true},
			{field:'productId',width:60,align:'center',hidden:true},
			{field:'inBuyOrder',width:60,align:'center',hidden:true},
			{field:'inUserOrder',width:60,align:'center',hidden:true},
			{field:'productName',title:'小店名称',width:60,align:'center'},
			{field:'productCode',title:'商品编号',width:100,align:'center'},
			{field:'inBuyOrderName',title:'是否为售后单商品',width:80,align:'center'},
			{field:'inUserOrderName',title:'是否为订单商品',width:80,align:'center'},
			{field:'remark',title:'备注',width:80,align:'center',
	        	editor: {
	        		type: 'text'
	        	}
			},
			{field:'action',title:'操作',width:40,align:'center',
				formatter : function(value, row, index) {
          			return '<a href="javascript:void(0);" class="deleteDetectProduct" onclick="deleteDetectProduct('+row.id+');" ></a>';
  				}
			}
	    ] ],
		onLoadSuccess : function(data) {
			//改变datagrid中按钮的class
			$(".deleteDetectProduct").linkbutton({ 
					iconCls:'icon-remove',
					plain:true
			});
			$(".addDetectProductsClass").linkbutton({ 
				plain : true,
				iconCls : 'icon-ok'
			});
		}
	}); 
}

function addProduct(tableId, index) {
	var addRow = $("#" + tableId).datagrid("getRows")[index];
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/validatePackageProduct.mmx',
		data : "afterSaleOrderId="+addRow.afterSaleOrderId + "&productId=" + addRow.productId + "&id=" +addRow.id ,
		dataType : 'text',
		type:"post",
		async: false,
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.success) {
					$('#packageProductsDataGrid').datagrid("appendRow", {
						id: 0,
						afterSaleOrderId : d.obj.afterSaleOrderId,
						productId : d.obj.productId,
						inBuyOrder : d.obj.inBuyOrder,
						inUserOrder: d.obj.inUserOrder,
						productName: d.obj.productName,
						productCode: "<a href='${pageContext.request.contextPath}/admin/fproduct.do?id="+ d.obj.productId +"' target=\"_blank\" > "+ d.obj.productCode +"</a>",
						inBuyOrderName: d.obj.inBuyOrderName,
						inUserOrderName: d.obj.inUserOrderName,
						remark: d.obj.remark,
						action:'<a href="javascript:void(0);" class="deleteDetectProduct" onclick="deleteDetectProduct(\''+indexId+'\');" ></a>'
					});
					$(".deleteDetectProduct").linkbutton(
						{ 
							iconCls:'icon-remove',
							plain:true
						}
					);
					var rowIndex = $("#packageProductsDataGrid").datagrid("getRows").length - 1;
					$("#packageProductsDataGrid").datagrid("beginEdit", rowIndex);
				} else {
					$.messager.show({
						title : '提示',
						msg : d.msg
					});
				}
			} catch (e) {
				$.messager.alert("错误", "异常", "info");
			}
		}
	});
}
	
	function addLastRow(tableId) {
		$('#' + tableId).datagrid('appendRow',{
			action : '',
			productName : '<a href="javascript:void(0);" class="addOwnProduct" onclick="addOwnProduct(\''+tableId+'\')"></a>',
			productCode : ''
		});
		var rowIndex = $('#' + tableId).datagrid("getRows").length-1;
		$('#' + tableId).datagrid('beginEdit', rowIndex);
		$(".addOwnProduct").linkbutton(
			{ 
				text:'添加商品', 
			}
		);
	}
	
function addOwnProduct(tableId) {
	var rows = $('#' + tableId).datagrid("getRows");
	var rowIndex = rows.length-1;
	if (!$('#' + tableId).datagrid('validateRow', rowIndex)) {
		return false;
	}
	$('#' + tableId).datagrid('endEdit', rowIndex);
	var productCode = $.trim($('#' + tableId).datagrid("getRows")[rowIndex].productCode);
	//判断是否已添加
	for (var i = 0; i < rowIndex ;i ++) {
		if (productCode == rows[i].productCode || productCode == rows[i].barcode) {
			$('#' + tableId).datagrid('beginEdit', rowIndex);
			$(".addOwnProduct").linkbutton(
				{ 
					text:'添加商品', 
				}
			);
			$.messager.show({
				title : '提示',
				msg : '该商品已存在！'
			});
			return false;
		}
	}
	
	//判断是否为正常商品
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/validateAddOwnProduct.mmx',
		data : "productCode="+productCode + "&afterSaleOrderId=" +$('#' + tableId).datagrid("getRows")[0].afterSaleOrderId ,
		dataType : 'text',
		type:"post",
		async: false,
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.success) {
					$('#' + tableId).datagrid('insertRow',{
						index: rowIndex,
						row: {
							action : '<a href="javascript:void(0);" class="addProducts" onclick="addProduct('+tableId+','+rowIndex+')"></a>',
							productName : d.obj.productName,
							productCode : d.obj.productCode,
							productId : d.obj.productId,
							barcode : d.obj.barcode,
							afterSaleOrderId: d.obj.afterSaleOrderId,
							id: d.obj.id
						}
					});
					$('#' + tableId).datagrid('getRows')[rowIndex + 1].productCode = '';
					$('#' + tableId).datagrid('beginEdit', rowIndex + 1);
					$('.addProducts').linkbutton(
						{
							plain:true,
							iconCls:'icon-add'
						}
					);
				} else {
					$.messager.show({
						title : '提示',
						msg : d.msg
					});
					$('#' + tableId).datagrid('beginEdit', rowIndex);
				}
				$(".addOwnProduct").linkbutton(
					{ 
						text:'添加商品'
					}
				);
			} catch (e) {
				$.messager.alert("错误", "异常", "info");
			}
		}
	});
}

function deleteDetectProduct(id) {
	$("#packageProductsDataGrid").datagrid("selectRecord", id)
	var deleteRow = $("#packageProductsDataGrid").datagrid("getSelected");
	var deleteRowIndex = $("#packageProductsDataGrid").datagrid("getRowIndex",deleteRow);
	$("#packageProductsDataGrid").datagrid("deleteRow",deleteRowIndex);
}

function modifyDetectProducts() {
	var rows = $("#packageProductsDataGrid").datagrid("getRows");
	if (rows.length <= 0) {
		$.messager.show({
			title : '提示',
			msg : '没有签收任何商品，不能提交！'
		});
		return false;
	} else {
		for(var i = 0 ;i < rows.length; i ++){
			$("#packageProductsDataGrid").datagrid("endEdit", i);
		}
		var bat = new Array();
		var j = 0;
		for(var i = 0 ;i < rows.length; i ++){
			bat[j]= new Array();
			var mark = filter(rows[i].remark);
			bat[j][0]=mark;
			bat[j][1]=rows[i].afterSaleOrderId;
			bat[j][2]=rows[i].productId;
			bat[j][3]=rows[i].inBuyOrder;
			bat[j][4]=rows[i].inUserOrder;
			bat[j][5]=rows[i].id;
			j ++;
		}
		for(var i = 0 ;i < rows.length; i ++){
			$("#packageProductsDataGrid").datagrid("beginEdit", i);
		}
		$.ajax({
			url : '${pageContext.request.contextPath}/admin/AfStock/modifyDetectProducts.mmx',
			data : "afterSaleDetectProducts="+bat + "&afterSaleDetectPackageId=" + $("#packageId").val(),
			dataType : 'text',
			type:"post",
			success : function(data) {
				try {
					var d = $.parseJSON(data);
					if (d.success) {
						$("#addProductsClose").click();
					}else{
						$(".deleteDetectProduct").linkbutton({ 
							iconCls:'icon-remove',
							plain:true
						});
					} 
					$.messager.show({
						title : '提示',
						msg : d.msg
					});
				} catch (e) {
					$.messager.alert("错误", "异常", "info");
				}
			}
		});
	}
}
function filter(str){
	str = str.replace(/\+/g,"%2B");
	str = str.replace(/\&/g,"%26");
	return str;
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;">
		<fieldset>
			<legend>扫描包裹单号</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>包裹单号：</th>
					<td align="left">
						<input id="packageCode" name="packageCode" style="width: 116px;"/>
					</td>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="searchFun();" href="javascript:void(0);">提交</a>
					</td>
					<td>
						扫描包裹单号，或者手动输入包裹单号，然后单击‘提交’按钮。
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<input type="hidden" id="packageAfterSaleIndex"/>
	<div id="packageAfterSaleDataGrid">
	</div>
	<br/>
	<div id="afterSaleProducts" style="height:520px;display: none;">
		<table>
			<tr>
				<td><h3>售后签收商品</h3><input type="hidden" id="packageId"/></td>
				<td id="btn" style="display: none"><a class="easyui-linkbutton"  data-options="iconCls:'icon-edit',plain:true" href="javascript:void(0);" onclick="modifySignProducts();" >修改签收商品</a></td>
			</tr>
		</table>
		<div style="height:250px">
			<table id="detectProductDatagrid"></table>
		</div>
		<br/>
		<div id="detectProduct"  style="height: auto;display: none;">
			<fieldset>
			<form id="detectProductForm" method="post">
				<h3>检测记录</h3>
				<hr/>
				<input type="hidden" id="index"/>
				<input type="hidden" id="afterSaleDetectPackageId" name="afterSaleDetectPackageId"/>
				<input type="hidden" id="afterSaleOrderId" name="afterSaleOrderId"/>
				<input type="hidden" id="id" name="id"/>
				<table id="table" class="tableForm">
					<tr align="center">
						<th>问题分类：</th>
						<td align="left">
							<input id="questionDescription" name="questionDescription" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="questionDescription2" name="questionDescription2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="questionDescription3" name="questionDescription3" style="width: 116px;"/>
						</td>
						<th>故障描述：</th>
						<td align="left">
							<input id="faultDescription" name="faultDescription" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="faultDescription2" name="faultDescription2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="faultDescription3" name="faultDescription3" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center" >
						<th>包装：</th>
						<td align="left">
							<input id="damaged" name="damaged" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="damaged2" name="damaged2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="damaged3" name="damaged3" style="width: 116px;"/>
						</td>
						<th>赠品：</th>
						<td align="left">
							<input id="giftAll" name="giftAll" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="giftAll2" name="giftAll2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="giftAll3" name="giftAll3" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center" >
						
						<th>申报状态：</th>
						<td align="left">
							<input id="reportStatus" name="reportStatus" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="reportStatus2" name="reportStatus2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="reportStatus3" name="reportStatus3" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center" >
						<th>故障代码：</th>
						<td align="left">
							<input id="faultCode" name="faultCode" style="width: 116px;"/>
						</td>
						<th>IMEI码：</th>
						<td align="left">
							<input id="IMEI" name="IMEI" style="width: 116px;"/>
						</td>
						<th>发票：</th>
						<td align="left">
							<input id="debitNote" name="debitNote" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center">
						<th><input type="checkbox" id="detectException" name="detectException" onclick="exeGetChecked();" value="1"/>检测异常</th>
						<th>异常原因：</th>
						<td align="left">
							<input id="exceptionReason" name="exceptionReason" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="exceptionReason2" name="exceptionReason2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="exceptionReason3" name="exceptionReason3" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center">
						<th>主商品</th>
						<td align="left"><div id="productStatusDiv"></div></td>
					</tr>
					<tr>
						<td colspan="2"><a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="showFittingDiv();" href="javascript:void(0);">记录配件信息</a></td>
					</tr>
					<tr align="center">
						<th>报价项：</th>
						<td align="left" colspan="3">
							<input id="quoteItem" name="quoteItem" style="width: 300px;"/>
						</td>
						<th>报价：</th>
						<td align="left">
							<input id="quote" name="quote" style="width: 75px;" class="easyui-numberbox" data-options="precision:2,max:99999999.99"/>
							<input id="parentId1" name="parentId1" type="hidden"/>
							<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addQuoteHtml();" href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr align="center">
						<th>备注：</th>
						<td align="left" colspan="5">
							<textarea id="remark" name="remark" cols="50" rows="4"></textarea>
						</td>
					</tr>
				</table>
				<hr/>
				<table  class="tableForm" align="center">
					<tr align="center" >
						<th>处理意见：</th>
						<td>
							<input id="handle" name="handle" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center" >
						<th>售后处理单号：</th>
						<td>
							<input id="afterSaleDetectProductCode" name="afterSaleDetectProductCode" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center">
						<td colspan="2">
							<a class="easyui-linkbutton"  onclick="detectProductFun();" href="javascript:void(0);">添加本条检测记录</a>
						</td>
					</tr>
				</table>
				<div id="fittingDialog" style="display:none">
					<div id="fittingsDiv">
					</div>
				</div>
			</form>
			</fieldset>
		</div>
	</div>
	<div id="printDetectProductCodeDiv" style="display:none">
		检测记录已添加，售后处理号为：<label id=code></label>
		<input id=code type="hidden"/>
		<a class="easyui-linkbutton"  onclick="closeFun();" href="javascript:void(0);">确定</a>
	</div>
</body>
</html>