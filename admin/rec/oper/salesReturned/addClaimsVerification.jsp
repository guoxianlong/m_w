<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
var searchOrderForm;
$(function() {
	searchOrderForm = $("#searchOrderForm").form( {
		url : '<%=request.getContextPath()%>/SalesReturnController/getOrderInfoByCode.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			return isValid;
		},
		success : function(data) {
			try {
				var data = $.parseJSON(data);
				if (data.result == 'failure') {
					$.messager.alert("提示", data.tip, "info", function(){focusOrderCode();});
				} else {
					$("#orderIdp").attr("value", data.orderId);
					$("#orderStockIdp").attr("value", data.orderStockId);
					var options = {
						collapsible:true,
						fit : true,
						fitColumns : true,
						rownumbers : true,
						border : true,
						striped : true,
						checkOnSelect : false,
						selectOnCheck : false,
						nowrap : false,
						toolbar : '#addtb',
						columns : [ [ {
				 			field : 'id',
							title : 'id',
							align : 'center',
							hidden : true
						}, {
				 			field : 'ok',
							checkbox : true
						},{
				 			field : 'code',
							title : '产品编号',
							width : 180,
							align : 'center'
						}, {
							field : 'oriname',
							title : '产品原名称',
							width : 200,
							align : 'center'
						}, {
							field : 'name',
							title : '小店名称',
							width : 200,
							align : 'center'
						}, {
							field : 'stockoutCount',
							title : '数量',
							width : 80,
							align : 'center'
						}, {
							field : 'addCount',
							title : '添加数量',
							width : 80,
							align : 'center',
							editor : {
								type : 'numberbox',
								options : {
									min:0,
									max:999999999,
									required:true
								}
							}
						} ] ]
				    };
					$("#addClaimsVerificationDataGrid").datagrid(options);
					$("#addClaimsVerificationDataGrid").datagrid("loadData", data.rows);
					
					var rows = $("#addClaimsVerificationDataGrid").datagrid("getRows");
					for(var i = 0 ;i < rows.length; i ++){
						$("#addClaimsVerificationDataGrid").datagrid("beginEdit", i);
					}
					$("#addProductCode").attr("value", "");$("#addProductCode").focus();
				}
			} catch(e) {
				$.messager.alert("提示", "异常", "info", function(){focusOrderCode();});
			}
		}
	});
	
	initAddClaimsVerificationDataGrid();
	$("#currentDiv").hide();
	$("#addClaims").hide();
	initCurrentClaimsVerificationDataGrid();
	$("#searchOrderForm [id=addOrderCode]").focus();
	$("#searchOrderForm [id=addOrderCode]").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
            $("#searchOrderButton").click(); 
            return false;
        }
    });
	$("#searchOrderForm [id=addProductCode]").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
        	addProductCount();
            return false;
        }
    });
});

function focusOrderCode() {
	$("#searchOrderForm").find("[id=addOrderCode]").attr("value", "");
	$("#searchOrderForm").find("[id=addOrderCode]").focus();
};

function initAddClaimsVerificationDataGrid() {
	var options = {
		collapsible:true,
		fit : true,
		rownumbers : true,
		border : true,
		singleSelect : true,
		striped : true,
		nowrap : false,
		toolbar : '#addtb',
		columns : [ [ {
 			field : 'id',
			title : 'id',
			align : 'center',
			hidden : true
		},{
 			field : 'code',
			title : '产品编号',
			width : 180,
			align : 'center'
		}, {
			field : 'oriname',
			title : '产品原名称',
			width : 200,
			align : 'center'
		}, {
			field : 'name',
			title : '小店名称',
			width : 200,
			align : 'center'
		}, {
			field : 'count',
			title : '数量',
			width : 80,
			align : 'center'
		} ] ]
    };
    var addClaimsVerificationDataGrid = $("#addClaimsVerificationDataGrid");
    addClaimsVerificationDataGrid.datagrid(options);//根据配置选项，生成datagrid
};

function initCurrentClaimsVerificationDataGrid() {
	var options = {
   			collapsible:true,
   			fit : true,
   			fitColumns : true,
   			rownumbers : true,
   			border : true,
   			idField :'productId',
   			singleSelect : true,
   			striped : true,
   			nowrap : false,
   			columns : [ [{
   	 			field : 'productId',
   				title : '产品id',
   				align : 'center',
   				hidden : true
   			}, {
   	 			field : 'currentOrderCode',
   				title : '订单号',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'packageCode',
   				title : '包裹单号',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'deliverCompany',
   				title : '快递公司',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'oriName',
   				title : '原名称',
   				width : 200,
   				align : 'center'
   			}, {
   				field : 'productLine',
   				title : '产品线',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'productCode',
   				title : '产品编号',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'count',
   				title : '数量',
   				width : 80,
   				align : 'center',
   				editor : {
   					type : 'numberbox',
   					options : {
   						min:0,
   						max:999999999,
   						required:true
   					}
   				}
   			}, {
   				field : 'isExist',
   				title : '有无实物',
   				width : 80,
   				align : 'center',
   				editor : {
   					type : 'combobox',
   					options:{
						valueField : 'id',
						textField : 'text',
						panelHeight : 'auto',
						editable : false,
						data: [{
							id: '1',
							text: '有'
						},{
							id: '0',
							text: '无'
						}]
					}
   				}
   			}, {
   				field : 'action',
   				title : '操作',
   				width : 100,
   				align : 'center',
   				formatter : function(value, row, index) {
   					var action = '<a href="javascript:void(0);" class="deleteButton" onclick="deletecurrentClaimsVerificationDataGrid('+row.productId+')"></a>'
   					return action;
   				}
   			} ] ],
   			onLoadSuccess : function(data) {
   				$(".deleteButton").linkbutton(
   					{ 
   						text:'删除', 
   						plain:true
   					}
   				);
   				
//    				var fields = [{
//    					field : 'currentOrderCode'
//    				},{
//    					field : 'packageCode'
//    				},{
//    					field : 'deliverCompany'
//    				}]
//   					var merges = [{
//   						index: 0,
//   						rowspan: data.rows.length
//   					}];
//   					for(var i=0; i<fields.length; i++){
//   						$(this).datagrid('mergeCells',{
//   							index: merges[0].index,
//   							field: fields[i].field,
//   							rowspan: merges[0].rowspan
//   						});
//   					}
   			}
    	}
	$("#currentClaimsVerificationDataGrid").datagrid(options);
}

function submitSearchOrderForm() {
	searchOrderForm.submit();
};

function addProductCount() {
	var addProductCode = $("#addProductCode").val();
	if (addProductCode == "undefined" || addProductCode == null || addProductCode == "" || $.trim(addProductCode) == "") {
		$.messager.alert("提示", "没有填写商品编号/条码！", "info", function (){$("#addProductCode").attr("value", "");$("#addProductCode").focus();});
		return  false;
	}
	addProductCode = $.trim(addProductCode);
	var rows = $("#addClaimsVerificationDataGrid").datagrid("getRows");
	var length = rows.length;
	if (length <= 0) {
		$.messager.alert("提示", "还没有订单信息！", "info", function() {focusOrderCode();$("#addProductCode").attr("value", "");});
		return  false;
	} else {
		var flag = false;
		for (var i = 0; i < length; i++) {
			if (addProductCode == rows[i].code || addProductCode == rows[i].barcode) {
				$('#addClaimsVerificationDataGrid').datagrid('checkRow',i);
				var ed = $('#addClaimsVerificationDataGrid').datagrid('getEditor', {index:i,field:'addCount'});
				$(ed.target).numberbox('setValue', parseInt($(ed.target).numberbox('getValue'))+1);
				flag = true;
				break;
			}
		}
		if (flag == false) {
			$.messager.alert("提示", "订单中没有这个商品！", "info", function (){$("#addProductCode").attr("value", "");$("#addProductCode").focus();});
			return  false;
		}else {
			$("#addProductCode").attr("value", "");$("#addProductCode").focus();
		}
	}
};

function addOrderProducts() {
	var addClaimsVerificationDataGridlength = $("#addClaimsVerificationDataGrid").datagrid("getRows").length;
	for (var i = 0 ; i < addClaimsVerificationDataGridlength; i++) {
		$("#addClaimsVerificationDataGrid").datagrid("endEdit", i);
	} 
	var rows = $("#addClaimsVerificationDataGrid").datagrid("getRows");
	var length = rows.length;
	if (length <= 0) {
		$.messager.alert("提示", "还没有订单信息！", "info", function() {focusOrderCode();$("#addProductCode").attr("value", "");});
		return  false;
	} else {
		var selectrows = $("#addClaimsVerificationDataGrid").datagrid("getChecked");
		var selectlength = selectrows.length;
		if (selectlength <= 0) {
			for (var i = 0 ; i < length; i++) {
				$("#addClaimsVerificationDataGrid").datagrid("beginEdit", i);
			} 
			$.messager.alert("提示", "没有要提交的商品！", "info");
			return  false;
		} else {
			var params = "";
			var hasAddOrder = $("#hasAddOrder").val();
   			if( hasAddOrder != "0" ) {
   				var currentId = $("#currentOrderId").val();
   				var id = $("#orderIdp").val();
   				if( id != currentId) {
   					for (var i = 0 ; i < length; i++) {
   						$("#addClaimsVerificationDataGrid").datagrid("beginEdit", i);
   					}
   					$.messager.alert("提示", "当前添加的订单  与  已添加商品的订单 不同！ ", "info");
   					return false;
   				}
   			}
   			
   			//校验选中的每行添加数量是否有值
			for (var i = 0 ; i < selectlength ; i ++) {
				if (!$("#addClaimsVerificationDataGrid").datagrid("validateRow", i)) {
					for (var i = 0 ; i < length; i++) {
						$("#addClaimsVerificationDataGrid").datagrid("beginEdit", i);
					}
					$.messager.alert("提示", "有选中行数量没有！ ", "info");
					return false;
				}
				if (selectrows[i].addCount == 0) {
					for (var i = 0 ; i < length; i++) {
						$("#addClaimsVerificationDataGrid").datagrid("beginEdit", i);
					}
					$.messager.alert("提示", "有选中行数量为0！ ", "info");
					return false;
				}
			} 
   			
			var products = new Array();
			//校验选中的每行添加数量是否有值
			var j = 0;
			for (var i = 0 ; i < selectlength ; i ++) {
				products[j] = new Array();
				products[j][0]=selectrows[i].id;
				products[j][1]=selectrows[i].addCount;
				j ++;
			} 
			var currentProducts = new Array();
			if($("#hasAddOrder").val() == "1") {
				var currentClaimsVerificationDataGridlength = $("#currentClaimsVerificationDataGrid").datagrid("getRows").length;
				for (var i = 0 ; i < currentClaimsVerificationDataGridlength; i++) {
    				$("#currentClaimsVerificationDataGrid").datagrid("endEdit", i);
    			} 
				var currentRows = $("#currentClaimsVerificationDataGrid").datagrid("getRows");
				var currentLength = currentRows.length;
				var j = 0;
				for (var i = 0 ; i < currentLength ; i ++) {
					currentProducts[j] = new Array();
					currentProducts[j][0]=currentRows[i].productId;
					currentProducts[j][1]=currentRows[i].count;
					currentProducts[j][2]=currentRows[i].isExist;
					j ++;
				}
				
				for (var i = 0 ; i < currentLength; i++) {
					currentClaimsVerificationDataGrid.datagrid("beginEdit", i);
				} 
				$(".deleteButton").linkbutton(
   					{ 
   						text:'删除', 
   						plain:true
   					}
   				);
			}
			$.ajax({
                type: "GET", //调用方式  post 还是 get
                url: "<%=request.getContextPath()%>/SalesReturnController/checkClaimsVerificationProduct.mmx",
                data : "orderId="+ $("#orderIdp").val()+"&orderStockId="+ $("#orderStockIdp").val()+"&products="+products+"&currentProducts="+currentProducts,
                dataType: "text", //返回的数据的形式
                success: function(data) { 
                	var addClaimsVerificationDataGridlength = $("#addClaimsVerificationDataGrid").datagrid("getRows").length;
                	for (var i = 0 ; i < addClaimsVerificationDataGridlength; i++) {
        				$("#addClaimsVerificationDataGrid").datagrid("beginEdit", i);
        			}
                	try {
    					var d = $.parseJSON(data);
    					if (d.result == 'success') {
    						$("#currentDiv").show();
    						$("#addClaims").show();
    						var currentClaimsVerificationDataGrid = $("#currentClaimsVerificationDataGrid");
    	                	currentClaimsVerificationDataGrid.datagrid("loadData", d.rows);
    	                	var rows = currentClaimsVerificationDataGrid.datagrid("getRows");
    	                	var length = rows.length;
							for (var i = 0 ; i < length; i++) {
								currentClaimsVerificationDataGrid.datagrid("beginEdit", i);
							}    	                	
    	                	$("#hasAddOrder").attr("value", "1");
    	                	$("#currentOrderStockId").attr("value", d.orderStockId);
    	                	$("#currentOrderId").attr("value", d.currentOrderId);
    					} else {
    						$.messager.alert("错误", d.tip, "info");
    					}
    				} catch (e) {
    					$.messager.alert("错误", "异常", "info");
    				}
                }
          });
		}
	}
};

function deletecurrentClaimsVerificationDataGrid(id) {
	var rightDataGrid = $('#currentClaimsVerificationDataGrid');
	rightDataGrid.datagrid("selectRecord",  id);
	var row = rightDataGrid.datagrid("getSelected");
	var rowIndex = rightDataGrid.datagrid("getRowIndex", row);
	$("#currentClaimsVerificationDataGrid").datagrid("deleteRow", rowIndex);
	if ($("#currentClaimsVerificationDataGrid").datagrid("getRows").length <= 0) {
		$("#currentDiv").hide();
		$("#addClaims").hide();
		$("#hasAddOrder").attr("value", "0");
		$("#currentOrderId").attr("value", "");
	}
}
//保存
function addClaims() {
	var hasAddOrder = $("#hasAddOrder").val();
	if( hasAddOrder == "0" ) {
		$.messager.alert("错误", "还没有添加订单中的商品，无法添加理赔单！", "info");
		return;
	}
	if($("#hasAddOrder").val() == "1") {
		var currentClaimsVerificationDataGridlength = $("#currentClaimsVerificationDataGrid").datagrid("getRows").length;
		if (currentClaimsVerificationDataGridlength <= 0) {
			$.messager.alert("错误", "没有添加商品！", "info");
			return false;
		}
		for (var i = 0 ; i < currentClaimsVerificationDataGridlength; i++) {
			$("#currentClaimsVerificationDataGrid").datagrid("endEdit", i);
		} 
		var currentProducts = new Array();
		var currentRows = $("#currentClaimsVerificationDataGrid").datagrid("getRows");
		var currentLength = currentRows.length;
		var j = 0;
		for (var i = 0 ; i < currentLength ; i ++) {
			currentProducts[j] = new Array();
			currentProducts[j][0]=currentRows[i].productId;
			currentProducts[j][1]=currentRows[i].count;
			currentProducts[j][2]=currentRows[i].isExist;
			j ++;
		}
		
		for (var i = 0 ; i < currentLength; i++) {
			$("#currentClaimsVerificationDataGrid").datagrid("beginEdit", i);
		} 
		$(".deleteButton").linkbutton(
			{ 
				text:'删除', 
				plain:true
			}
		);
		
		$.ajax({
            type: "POST", //调用方式  post 还是 get
            url: "<%=request.getContextPath()%>/SalesReturnController/addClaimsVerification.mmx", 
            data : "wareArea="+$("#wareAreaAdd [id=wareArea]").combobox("getValue")+"&currentOrderId="+$("#currentOrderId").val()+"&currentOrderStockId="+$("#currentOrderStockId").val()+"&currentProducts="+currentProducts,
            dataType: "text", //返回的数据的形式
            success: function(data) {   
            	try {
					var d = $.parseJSON(data);
					if (d.result == 'success') {
						$.messager.alert("提示", d.tip, "info", function(){$("#closeAddButton").click();});
					} else {
						$.messager.alert("错误", d.tip, "info");
					}
				} catch (e) {
					$.messager.alert("错误", "异常", "info");
				}
            }
      });
	}
}
</script>
<div>
<div style="width: 95%"><h1 align="center">添加理赔核销单</h1></div>
<hr width="95%" align="left">
<div style="width:80%;margin-left:10%;font-size:14px;"><b>添加商品：</b></div>
<center>
	<fieldset style="width:80%;">
		<div style="margin-left:10px;position:relative;"> 
			<form id="searchOrderForm">
				<table class="tableForm">
					<tr>
						<th>订单号：</th>
						<td><input class="easyui-validatebox" name="addOrderCode" id="addOrderCode" data-options="required:true" /></td> 
						<td><a href="javascript:void(0)" class="easyui-linkbutton"  data-options="iconCls:'icon-search',plain:true"  id="searchOrderButton" onclick="submitSearchOrderForm();">查看订单</a></td>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<td></td>
						<th>商品编号/商品条码：</th>
						<td><input name="addProductCode" id="addProductCode" /></td> 
					</tr>
				</table>
			</form>
		</div>
	</fieldset>
</center>
<div data-options="border:false" style="height:160px;overflow: hidden;">
	<table id="addClaimsVerificationDataGrid"></table>
</div>
<br/>
<center>
	<div id="wareAreaAdd"> 
		库地区:<input class="easyui-combobox" name='wareArea' id='wareArea' style="width: 80px;" 
				data-options="url:'<%=request.getContextPath()%>/SalesReturnController/getWareAreaJSON.mmx',  
								valueField : 'areaId',   
								textField : 'areaName',
								panelHeight : 'auto',
		   						editable : false"
		   		/>
	</div>
</center>
<div id="currentDiv" data-options="border:false" style="height:160px;width:100%;overflow: hidden;">
	<table id="currentClaimsVerificationDataGrid"></table>
</div>
<div id="addClaims">
	<a href="javascript:void(0)" class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true"  onclick="addClaims();">保存</a>
</div>
<div id="addtb" class="datagrid-toolbar" align="left">
	<a href="javascript:void(0)" class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true"  onclick="addOrderProducts();">添加商品</a>
</div>
<input type='hidden' name='orderId' id='orderIdp' />
<input type='hidden' name='orderStockId' id='orderStockIdp'/>
<input type="hidden" name="hasAddOrder" id="hasAddOrder" value="0"/>
<input type="hidden" name="currentOrderId" id="currentOrderId"/>
<input type="hidden" name="currentOrderStockId" id="currentOrderStockId"/>
</div>