<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>客户寄回包裹列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var indexId= -1;
var datagrid;
var matchFailDialog;
var failPackageProductsDataGrid;
var flag= false;
$(function(){
	datagrid = $('#sendBackDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSendBackDatagrid.mmx',
	    queryParams : {
	    	status : -1
	    },
	    toolbar : '#tb',
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
			{field:'packageCode',title:'包裹单号',width:60,align:'center'},
			{field:'createDatetime',title:'签收时间',width:100,align:'center',
	        	formatter : function(value, row, index) {
	        		return value.substring(0,19);
				}
			},
			{field:'createUserName',title:'签收人',width:80,align:'center'},
			{field:'statusName',title:'状态',width:80,align:'center'},
			{field:'afterSaleOrderIds',title:'售后单id',width:80,align:'center',hidden:true},
			{field:'afterSaleOrderCodes',title:'售后单号',width:80,align:'center',
	        	formatter : function(value, row, index) {
	        		return replaceAllStr(value, ",","<br\>");
				}
			},
			{field:'orderCodes',title:'订单号',width:80,align:'center',
	        	formatter : function(value, row, index) {
	        		return replaceAllStr(value, ",","<br\>");
				}
			},
	        {field:'action',title:'操作',width:120,align:'center',
	        	formatter : function(value, row, index) {
	        		if (row.status == 0 || row.status == 2) {
        				return '<a href="javascript:void(0);" class="matchAfterSale" onclick="matchAfterSale('+index+')"></a>' + 
        							'<a href="javascript:void(0);" class="modifyMatchProducts" onclick="modifyMatchProducts('+index+')" disabled=disabled></a>';
	        		} else if(row.status==1){
	        			return '<a href="javascript:void(0);" class="matchAfterSale" onclick="matchAfterSale('+index+')" disabled=disabled></a>' +
	        						'<a href="javascript:void(0);" class="modifyMatchProducts" onclick="modifyMatchProducts('+index+')"></a>';
	        		}else{
	        			return '<a href="javascript:void(0);" class="matchAfterSale" onclick="matchAfterSale('+index+')" disabled=disabled></a>' + 
        							'<a href="javascript:void(0);" class="modifyMatchProducts" onclick="modifyMatchProducts('+index+')" disabled=disabled></a>';
	        		}
				}
			}
	    ] ],
		onLoadSuccess : function(data) {
			//改变datagrid中按钮的class
			$(".matchAfterSale").linkbutton(
				{ 
					text:'匹配售后单'
				}
			);
			$(".modifyMatchProducts").linkbutton(
				{ 
					text:'修改已匹配商品'
				}
			);
		}
	}); 
	$('#status').combobox({
      	url : '${pageContext.request.contextPath}/admin/AfStock/getDetectPackageStatus.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	matchFailDialog = $('#matchFailDialog').show().dialog({
		width : 1000,
		height : 520,
		modal : true,
		maximizable : true,
		title:'匹配失败',
		buttons : [{
			text : '关闭',
			handler : function() {
				var d = $(this).closest('.window-body');
				flag = false;
				d.dialog('destroy');
			}
		}]
	}).dialog('close');
	
});

function replaceAllStr(theStr, replaceStrA, replaceStrB) 
{ 
   var re=new RegExp(replaceStrA, "g"); 
   var newstart = theStr.replace(re, replaceStrB); 
   return newstart;
} 

function searchFun() {
	datagrid.datagrid("load", {
		packageCode:$("#tb input[id=packageCode]").val(),
		orderCode:$("#tb input[id=orderCode]").val(),
		afterSaleCode:$("#tb input[id=afterSaleCode]").val(),
		createUserName:$("#tb input[id=createUserName]").val(),
		senderName:$("#tb input[id=senderName]").val(),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		endTime:$("#tb input[id=endTime]").datebox("getValue"),
		areaId : $('#areaId').combobox('getValue'),
		status:$("#tb input[id=status]").combobox("getValue")
	});
}

function matchAfterSale(index) {
	if (index != undefined) {
		$('#sendBackDataGrid').datagrid('selectRow', index);
	}
	var row = $('#sendBackDataGrid').datagrid('getSelected');
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/matchAfterSaleManager.jsp',
		width : 1000,
		height : 520,
		modal : true,
		maximizable : true,
		resizable : true,
		title : '匹配售后单',
		buttons : [ {
			id:"matchClose",
			text:"关闭",
			handler : function() {
				var d = $(this).closest('.window-body');
				flag = false;
				d.dialog('destroy');
			}
		} ],
		onClose : function() {
			flag = false;
			$(this).dialog('destroy');
		},
		onLoad : function() {
			$("#afterSaletb input[id=id]").val(row.id);
			$("#tb input[id=packageId]").val(row.id);
			$("#afterSaletb label[id=packageCode]").html(row.packageCode);
			$("#afterSaleForm").form({
				url : '${pageContext.request.contextPath}/admin/AfStock/saveSenderInfo.mmx'
			});
			initAfterSaleDataGrid();
		}
	});
}

function modifyMatchProducts(index){
	if (index != undefined) {
		$('#sendBackDataGrid').datagrid('selectRow', index);
	}
	var row = $('#sendBackDataGrid').datagrid('getSelected');
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/addAfterSaleDetectProducts.jsp',
		width : 900,
		height : 520,
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
                data : "flag=true&afterSaleOrderIds="+row.afterSaleOrderIds,
                dataType: "text", //返回的数据的形式
                success: function(data) { 
                	$("#afterSaleDetectPackageId").val(row.id);
                	$('#afterSaleDetectPackageCode').val(row.packageCode);
                	$("#afterSaleOrderProducts").html(data);
                }
			});
		}
	});
}

function splitCount(value, theCount, tip) {
	var str = value.split("\n");
	var numlen = str.length;
	var count=0;
	if(numlen>theCount){
		for(var i=0;i<numlen;i+=1){
			if(str[i] && $.trim(str[i]).length>0)
				count++;
		}
	}
	if(count>theCount){
		$.messager.show({
			msg : tip,
			title : '提示'
		});
		return false;
	}
	return true;
}

function saveFun() {
	flag = true;
	if ($.trim($("#afterSaleForm textarea[id=orderCode]").val()) == ""  && $.trim($("#afterSaleForm textarea[id=phone]").val()) == "" && 
			$.trim($("#afterSaleForm input[id=senderName]").val()) == "" && $.trim($("#afterSaleForm input[id=senderAddress]").val()) == ""){
		$.messager.show({
			title : '提示',
			msg : '至少需要填写一个条件！'
		});
		return false;
	}
	var orderCode = $("#afterSaleForm textarea[id=orderCode]").val();
	if(orderCode!=""){
		if (!splitCount(orderCode, 20, "最多输入20个订单号！")) {
			return false;
		}
	}
	var phone = $("#afterSaleForm textarea[id=phone]").val();
	if(phone!=""){
		if (!splitCount(phone, 20, "最多输入20个手机号！")) {
			return false;
		}
	}
	$("#afterSaleForm").submit();
	$('#afterSaleDataGrid').datagrid("load", {
		orderCode:$.trim($("#afterSaleForm textarea[id=orderCode]").val()),
		phone:$.trim($("#afterSaleForm textarea[id=phone]").val()),
		senderName:$.trim($("#afterSaleForm input[id=senderName]").val()),
		senderAddress:$.trim($("#afterSaleForm input[id=senderAddress]").val())
	});
}

function initAfterSaleDataGrid() {
	$('#afterSaleDataGrid').datagrid({
		    url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDatagrid.mmx',
		    toolbar : '#afterSaletb',
		    idField : 'id',
		    fit : true,
		    fitColumns : true,
		    striped : true,
		    nowrap : true,
		    loadMsg : '正在努力为您加载..',
		    columns:[[  
				{field:'checkbox',checkbox:true,width:20,align:'center'},
				{field:'afterSaleOrderCode',title:'售后单号',width:80,align:'center',
		        	formatter : function(value, row, index) {
		        		return '<a href="javascript:void(0);" onclick="openAfterSaleOrder(\'' + row.id + '\')">' +value + '</a>';
					}
				},
				{field:'statusName',title:'售后单状态',width:60,align:'center'},
				{field:'orderCode',title:'订单号',width:80,align:'center'},
				{field:'productNames',title:'小店名称',width:80,align:'center',
		        	formatter : function(value, row, index) {
		        		return replaceAllStr(value, ",","<br\>");
					}
				},
				{field:'productCodes',title:'商品编号',width:80,align:'center',
		        	formatter : function(value, row, index) {
		        		return replaceAllStr(value, ",","<br\>");
					}
				},
				{field:'buyModeName',title:'购买方式',width:50,align:'center'},
				{field:'flatName',title:'订单来源',width:50,align:'center'},
				{field:'questionDis',title:'问题描述',width:80,align:'center'},
				{field:'createTime',title:'生成时间',width:100,align:'center',
		        	formatter : function(value, row, index) {
		        		return value.substring(0,19);
					}
				}
		    ] ],
			onLoadSuccess : function(data) {
				hasData(flag);
			}
		}); 
}

function hasData(flag) {
	if (flag) {
		if ($('#afterSaleDataGrid').datagrid("getRows").length <=0) {
			$.messager.show({
				title : '提示',
				msg : '没有符合条件的售后单！'
			});
			return false;
		}
	}
}

function openAfterSaleOrder(id) {
	window.open("https://sales.ebinf.com/sale/admin/toEdit.mmx?id=" + id, "_blank");
}

function matchFailFun() {
	var packageId = $('#tb input[id=packageId]').val();
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/failMatchAfterSale.jsp',
		width : 900,
		height : 520,
		modal : true,
		maximizable : true,
		title : '匹配商品',
		buttons : [ {
			id:"addMatchFailProductsClose",
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
			$("#failPackageProductsDataGrid").datagrid({
				toolbar:"failPackageProductsToolbar",
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
			    	{field:'packageId',width:60,align:'center',hidden:true},
					{field:'productId',width:60,align:'center',hidden:true},
					{field:'code',width:60,align:'center',hidden:true},
					{field:'productName',title:'小店名称',width:60,align:'center'},
					{field:'productCode',title:'商品编号',width:100,align:'center'},
					{field:'remark',title:'备注',width:80,align:'center',
			        	editor: {
			        		type: 'text'
			        	}
					},
					{field:'action',title:'操作',width:40,align:'center',
						formatter : function(value, row, index) {
		          			return '<a href="javascript:void(0);" class="deleteDetectProduct" onclick="deleteDetectProduct('+row.id+',\'failPackageProductsDataGrid\');" ></a>';
		  				}
					}
			    ] ],
				onLoadSuccess : function(data) {
					//改变datagrid中按钮的class
					$(".deleteDetectProduct").linkbutton(
						{ 
							iconCls:'icon-remove',
							plain:true
						}
					);
					$(".addDetectProductsClass").linkbutton(
						{ 
							plain : true,
							iconCls : 'icon-ok'
						}
					);
				}
			});
		}
	});
	$('#matchFailDialog input[id=id]').val($("#afterSaletb input[id=id]").val());
}

function addAfterSaleDetectProducts() {
	var checkedrows = $("#afterSaleDataGrid").datagrid("getChecked");
	var length = checkedrows.length;
	if (length <= 0 ) {
		$.messager.show({
			title : '提示',
			msg : '至少需要勾选一个售后单！'
		});
		return false;
	}
	var afterSaleOrderIds = new Array();
	for (var i = 0; i < length ;i ++) {
		afterSaleOrderIds[i] = checkedrows[i].id;
	}
	var afterSaleDetectPackageId = $("#afterSaletb input[id=id]").val();
	var packageCode = $("#afterSaletb label[id=packageCode]").html();
	$("#matchClose").click();
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/addAfterSaleDetectProducts.jsp',
		width : 900,
		height : 520,
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
                data : "afterSaleOrderIds="+afterSaleOrderIds,
                dataType: "text", //返回的数据的形式
                success: function(data) { 
                	$("#afterSaleDetectPackageId").val(afterSaleDetectPackageId);
                	$("#afterSaleDetectPackageCode").val(packageCode);
                	$("#afterSaleOrderProducts").html(data);
                }
			});
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
		async: false,
		type:"post",
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

function initPackageProducts() {
	var packageCode = $('#afterSaleDetectPackageCode').val();
	var afterSaleOrderCode = $().val();
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
          			return '<a href="javascript:void(0);" class="deleteDetectProduct" onclick="deleteDetectProduct('+row.id+',\'packageProductsDataGrid\');" ></a>';
  				}
			}
	    ] ],
		onLoadSuccess : function(data) {
			//改变datagrid中按钮的class
			$(".deleteDetectProduct").linkbutton(
				{ 
					iconCls:'icon-remove',
					plain:true
				}
			);
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
					indexId--;
					$('#packageProductsDataGrid').datagrid("appendRow", {
						id: indexId,
						afterSaleOrderId : d.obj.afterSaleOrderId,
						productId : d.obj.productId,
						inBuyOrder : d.obj.inBuyOrder,
						inUserOrder: d.obj.inUserOrder,
						productName: d.obj.productName,
						productCode: "<a href='${pageContext.request.contextPath}/admin/fproduct.do?id="+ d.obj.productId +"' target=\"_blank\" > "+ d.obj.productCode +"</a>",
						inBuyOrderName: d.obj.inBuyOrderName,
						inUserOrderName: d.obj.inUserOrderName,
						remark: d.obj.remark,
						action:'<a href="javascript:void(0);" class="deleteDetectProduct" onclick="deleteDetectProduct(\''+indexId+',\'packageProductsDataGrid\'\');" ></a>'
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

function openProductDetail(productId) {
	window.open("${pageContext.request.contextPath}/admin/fproduct.do?id="+productId ,"_blank");
}

function deleteDetectProduct(indexId,id) {
	$("#"+id).datagrid("selectRecord", indexId)
	var deleteRow = $("#"+id).datagrid("getSelected");
	var deleteRowIndex = $("#"+id).datagrid("getRowIndex",deleteRow);
	$("#"+id).datagrid("deleteRow",deleteRowIndex);
}

function addDetectProducts() {
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
			j ++;
		}
		for(var i = 0 ;i < rows.length; i ++){
			$("#packageProductsDataGrid").datagrid("beginEdit", i);
		}
		$.ajax({
			url : '${pageContext.request.contextPath}/admin/AfStock/addDetectProducts.mmx',
			data : "afterSaleDetectProducts="+bat + "&afterSaleDetectPackageId=" + $("#afterSaleDetectPackageId").val(),
			dataType : 'text',
			type:"post",
			success : function(data) {
				try {
					var d = $.parseJSON(data);
					if (d.success) {
						$("#addProductsClose").click();
						datagrid.datagrid("reload");
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
			data : "afterSaleDetectProducts="+bat + "&afterSaleDetectPackageId=" + $("#afterSaleDetectPackageId").val(),
			dataType : 'text',
			type:"post",
			success : function(data) {
				try {
					var d = $.parseJSON(data);
					if (d.success) {
						$("#addProductsClose").click();
						datagrid.datagrid("reload");
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

function addMatchFailFun(){
	var productCode = $.trim($("#productCode").val());
	if(productCode==''){
		$.messager.show({
			title : '提示',
			msg : '请输入产品编号'
		});
		return;
	}else{
		$.ajax({
			url : '${pageContext.request.contextPath}/admin/AfStock/getProduct.mmx',
			data : 'productCode='+productCode,
			dataType : 'json',
			type:"post",
			success : function(d) {
				try {
					if (d.success) {
						indexId++;
						$('#failPackageProductsDataGrid').datagrid("appendRow", {
							id: indexId,
							packageId : d.obj.packageId,
							productId : d.obj.productId,
							code : d.obj.productCode,
							productName: d.obj.productName,
							productCode: "<a href='${pageContext.request.contextPath}/admin/fproduct.do?id="+ d.obj.productId +"' target=\"_blank\" > "+ d.obj.productCode +"</a>",
							remark: d.obj.remark,
							action:'<a href="javascript:void(0);" class="deleteDetectProduct" onclick="deleteDetectProduct(\''+indexId+',\'failPackageProductsDataGrid\'\');" ></a>'
						});
						$(".deleteDetectProduct").linkbutton(
							{ 
								iconCls:'icon-remove',
								plain:true
							}
						);
						var rowIndex = $("#failPackageProductsDataGrid").datagrid("getRows").length - 1;
						$("#failPackageProductsDataGrid").datagrid("beginEdit", rowIndex);
					} 

				} catch (e) {
					$.messager.alert("错误", "异常", "info");
				}
			}
		});
	}
}
function addMatchFailDetectProducts(){
	var rows = $("#failPackageProductsDataGrid").datagrid("getRows");
	var packageId = $("#tb input[id=packageId]").val();
	if (rows.length <= 0) {
		$.messager.show({
			title : '提示',
			msg : '没有签收任何商品，不能提交！'
		});
		return false;
	} else {
		for(var i = 0 ;i < rows.length; i ++){
			$("#failPackageProductsDataGrid").datagrid("endEdit", i);
		}
		var parames = new Array();
		var j = 0;
		for(var i = 0 ;i < rows.length; i ++){
			parames[j]= new Array();
			var mark = filter(rows[i].remark);
			var pname = filter(rows[i].productName);
			parames[j][0]= mark;
			parames[j][1]=rows[i].productId;
			parames[j][2]=rows[i].code;
			parames[j][3]=pname;
			j ++;
		}
		for(var i = 0 ;i < rows.length; i ++){
			$("#failPackageProductsDataGrid").datagrid("beginEdit", i);
		}
		$.ajax({
			url : '${pageContext.request.contextPath}/admin/AfStock/addMatchFailDetectProducts.mmx',
			data : "afterSaleMatchFailPackageProducts="+parames + "&afterSaleDetectPackageId=" + packageId,
			dataType : 'text',
			success : function(data) {
				try {
					var d = $.parseJSON(data);
					if (d.success) {
						$("#addMatchFailProductsClose").click();
						$("#matchClose").click();
						datagrid.datagrid("reload");
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
	<table id="sendBackDataGrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>包裹单号：</th>
					<td align="left">
						<input id="packageCode" name="packageCode" style="width: 116px;"/>
						<input id="packageId" type="hidden"/>
					</td>
					<th>签收人：</th>
					<td align="left">
						<input id="createUserName" name="createUserName" style="width: 116px;"/>
					</td>
					<th>寄件人：</th>
					<td align="left">
						<input id="senderName" name="senderName" style="width: 116px;"/>
					</td>
					<th>订单号：</th>
					<td align="left">
						<input id="orderCode" name="orderCode" style="width: 116px;"/>
					</td>
					<td></td>
				</tr>
				<tr align="center">
					<th>状态：</th>
					<td align="left" >
						<input id="status" name="status" style="width:121px"/>
					</td>
					<th>售后单号：</th>
					<td align="left" >
						<input id="afterSaleCode" name="afterSaleCode" style="width:116px"/>
					</td>
					<th>售后地区：</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px;"/>
					</td>
					<th>签收时间：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-search',plain:true" onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>