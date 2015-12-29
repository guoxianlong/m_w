<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
var addReturnProductsForm;
$(function (){
	$('#addWarehousingAbnormalDataGrid').datagrid({
		url : '<%=request.getContextPath()%>/SalesReturnController/readyEditWarehousingAbnormal.mmx',
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		queryParams : {
			abnormalId : '<%=request.getParameter("abnormalId")%>'
		},
		columns : [ [ {
			field : 'code',
			title : '产品编号',
			width : 150,
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
		} ] ],
		onLoadSuccess : function(data) {
			try {
				if (data.result == 'failure') {
					$.messager.alert("提示", data.tip, "info");
				} else {
					//orderinfo
					initOrderInfoDataGrid(data.footer[0].columns);
				    
				    //商品退回
					var addReturnProducts = "";
					
				    $("#addgrid").show();
					initLeftDataGrid(data.rows);
					initRightDataGrid(data.footer[0].rpJson.rows);
					
					$("#editabnormalListForm").find("[id=wareArea]").combobox("setValue", data.footer[0].anormalBean.wareArea);
					$("#editabnormalListForm").find("[id=wareArea]").combobox("disable");
					$("#updateWarehousingAbnormalForm [id=code]").html(data.footer[0].anormalBean.code);
					$("#updateWarehousingAbnormalForm [id=abnormalId]").attr("value", data.footer[0].anormalBean.id);
					$("#updateWarehousingAbnormalForm [id=statusName]").html(data.footer[0].anormalBean.statusName);
					
					if (data.footer[0].canSubmit == 'true') {
						$("#addReturnProductsForm [id=addReturnProducts]").show();
						$("#updateWarehousingAbnormalForm [id=canSubmit]").show();
						$("#updateWarehousingAbnormalForm [id=auditPassed]").hide();
						$("#updateWarehousingAbnormalForm [id=auditNoPassed]").hide();
					} else if (data.footer[0].canAudit == 'true') {
						$("#updateWarehousingAbnormalForm [id=canSubmit]").hide();
						$("#updateWarehousingAbnormalForm [id=auditPassed]").show();
						$("#updateWarehousingAbnormalForm [id=auditNoPassed]").show();
					}
					if ( data.footer[0].anormalBean.status != "0") {
						var rows = $("#rightDataGrid").datagrid("getRows");
						for(var i = 0 ;i < rows.length; i ++){
							$("#rightDataGrid").datagrid("endEdit", i);
						}
					}
				}
			} catch(e) {
				$.messager.alert("提示", "异常", "info");
			}
		}
	});
	
	addReturnProductsForm = $('#addReturnProductsForm').form( {
		url : '<%=request.getContextPath()%>/SalesReturnController/addRealProduct.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			if (!isValid) {
				return isValid;
			} else {
				var ordercode = $("#addReturnProductsForm [id=realCode]").val();
				var rows = $("#rightDataGrid").datagrid("getRows");
				for(var i = 0; i < rows.length; i ++) {
					if (ordercode == rows[i].code) {
						$.messager.alert("提示", "此商品已添加!", "info", function(){realCodeFocus()});
						return false;
					}
				}
				return true;
			}
		},
		success : function(result) {
			try {
				var data = $.parseJSON(result);
				if (data.result == 'failure') {
					$.messager.alert("提示", data.tip, "info", function(){realCodeFocus()});
				} else {
					$('#rightDataGrid').datagrid('appendRow',{
						id : data.rows[0].id,
						code : data.rows[0].code,
						oriname : data.rows[0].oriname,
						name : data.rows[0].name,
						count : data.rows[0].count,
						action : '<a href="javascript:void(0);" class="deleteButton" onclick="deleteRightDataGrid('+data.rows[0].code+')"></a>'
					});
					$(".deleteButton").linkbutton(
						{ 
							text:'删除', 
							plain:true
						}
					);
					$('#rightDataGrid').datagrid('beginEdit', $('#rightDataGrid').datagrid("getRows").length-1);
					realCodeFocus();
					$("#saveabnormal").show();
				}
			} catch(e) {
				$.messager.alert("提示", "异常", "info", function(){realCodeFocus()});
			}
		}
	});
});

function realCodeFocus() {
	$("#addReturnProductsForm [id=realCode]").attr("value", "");
	$("#addReturnProductsForm [id=realCount]").attr("value", "");
	$("#addReturnProductsForm [id=realCode]").focus();
}
function deleteRightDataGrid(code) {
	var rightDataGrid = $('#rightDataGrid');
	rightDataGrid.datagrid("selectRecord", code);
	var row = rightDataGrid.datagrid("getSelected");
	var rowIndex = rightDataGrid.datagrid("getRowIndex", row);
	rightDataGrid.datagrid("deleteRow", rowIndex);
	$("#saveabnormal").show();
}

function initOrderInfoDataGrid(columns) {
	var options = {
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		columns : [columns]
    };
    var dataGrid = $("#orderInfoDataGrid");
    dataGrid.datagrid(options);//根据配置选项，生成datagrid
};

function initLeftDataGrid(rows) {
	var options = {
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		columns : [ [ {
 			field : 'code',
			title : '订单中商品',
			width : 150,
			align : 'center'
		}, {
			field : 'oriname',
			title : '原名称',
			width : 200,
			align : 'center'
		}, {
			field : 'name',
			title : '商品名称',
			width : 200,
			align : 'center'
		}, {
			field : 'count',
			title : '数量',
			width : 80,
			align : 'center'
		} ] ]
    };
    var dataGrid = $("#leftDataGrid");
    dataGrid.datagrid(options);//根据配置选项，生成datagrid
    dataGrid.datagrid("loadData", rows);
};

function initRightDataGrid(rows) {
	var options = {
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		singleSelect : true,
		striped : true,
		idField : 'code',
		nowrap : false,
		columns : [ [ {
 			field : 'id',
			title : 'id',
			align : 'center',
			hidden : true
		},{
 			field : 'code',
			title : '实际退回商品',
			width : 180,
			align : 'center'
		}, {
			field : 'oriname',
			title : '原名称',
			width : 200,
			align : 'center'
		}, {
			field : 'name',
			title : '商品名称',
			width : 200,
			align : 'center'
		}, {
			field : 'count',
			title : '数量',
			width : 80,
			align : 'center',
			editor : {
				type : 'numberbox',
				options : {
					min:1,
					max:999999,
					required:true
				}
			}
		}, {
			field : 'action',
			title : '操作',
			width : 120,
			align : 'center',
			formatter : function(value, row, index) {
				var action = '<a href="javascript:void(0);" class="deleteButton" onclick="deleteRightDataGrid('+row.code+')"></a>'
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
		}
    };
    var dataGrid = $("#rightDataGrid");
    dataGrid.datagrid(options);//根据配置选项，生成datagrid
    dataGrid.datagrid("loadData", rows);
    for(var i = 0 ;i < rows.length; i ++){
		$("#rightDataGrid").datagrid("beginEdit", i);
	  	//绑定onchange事件
		var ed = dataGrid.datagrid('getEditors', i);
		for (var j = 0; j < ed.length; j++)
		{
			var e = ed[j];
			if (e.field == 'count') {
				$(e.target).bind("change", function()
	            {
					$("#saveabnormal").show();
	            });
			}
		}
	}
};

function addabnormalAll() {
	var rows = $("#rightDataGrid").datagrid("getRows");
	if (rows.length <= 0) {
		$.messager.alert("提示", "请输入实际退回商品!", "info", function() {realCodeFocus();});
		return false;
	} else {
		if (!$("#rightDataGridForm").form('validate')) {
			return false;
		}
		for(var i = 0 ;i < rows.length; i ++){
			$("#rightDataGrid").datagrid("endEdit", i);
		}
		var bat = new Array();
		var j = 0;
		for(var i = 0 ;i < rows.length; i ++){
			bat[j]= new Array();
			bat[j][0]=rows[i].id;
			bat[j][1]=rows[i].count;
			bat[j][2]=rows[i].oriname;
			bat[j][3]=rows[i].code;
			bat[j][4]=rows[i].name;
			j ++;
		}
		for(var i = 0 ;i < rows.length; i ++){
			$("#rightDataGrid").datagrid("beginEdit", i);
		  	//绑定onchange事件
			var ed = $("#rightDataGrid").datagrid('getEditors', i);
			for (var j = 0; j < ed.length; j++)
			{
				var e = ed[j];
				if (e.field == 'count') {
					$(e.target).bind("change", function()
		            {
						$("#saveabnormal").show();
		            });
				}
			}
		}
		$(".deleteButton").linkbutton(
			{ 
				text:'删除', 
				plain:true
			}
		);
		$.ajax({
			url : '${pageContext.request.contextPath}/SalesReturnController/updateWarehousingAbnormal.mmx',
			data : "bat="+bat+"&abnormalId="+$("#updateWarehousingAbnormalForm [id=abnormalId]").val(),
			dataType : 'text',
			success : function(data) {
				try {
					var d = $.parseJSON(data);
					if (d.result == 'success') {
						$("#saveabnormal").hide();
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

//确认提交
$('#canSubmit').click(function(){
	if ($("#saveabnormal").css("display") != "none") {
		$.messager.alert("错误", "修改了数据，请先保存！", "info");
		return false;
	}
	$.messager.confirm('确认', '确认提交？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/statusToSubmitted.mmx',
				data : "abnormalId="+$("#updateWarehousingAbnormalForm [id=abnormalId]").val(),
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$("#addReturnProductsForm [id=addReturnProducts]").hide();
							$("#updateWarehousingAbnormalForm [id=canSubmit]").hide();
							if (d.canAudit == 'true') {
								$("#updateWarehousingAbnormalForm [id=auditPassed]").show();
								$("#updateWarehousingAbnormalForm [id=auditNoPassed]").show();
							} else {
								$("#updateWarehousingAbnormalForm [id=auditPassed]").hide();
								$("#updateWarehousingAbnormalForm [id=auditNoPassed]").hide();
							}
							$("#updateWarehousingAbnormalForm [id=statusName]").html(d.statusName);
							
							var rows = $("#rightDataGrid").datagrid("getRows");
							for(var i = 0 ;i < rows.length; i ++){
								$("#rightDataGrid").datagrid("endEdit", i);
							}
						} else {
							$.messager.alert("错误", d.tip, "info");
						}
					} catch (e) {
						$.messager.alert("错误", "异常", "info");
					}
				}
			});
		}
	});
});

//审核不通过
$('#auditNoPassed').click(function(){
	$.messager.confirm('确认', '确认审核不通过？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/statusToUntreated.mmx',
				data : "abnormalId="+$("#updateWarehousingAbnormalForm [id=abnormalId]").val(),
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$("#updateWarehousingAbnormalForm [id=auditPassed]").hide();
							$("#updateWarehousingAbnormalForm [id=auditNoPassed]").hide();
							if (d.canSubmit == 'true') {
								$("#addReturnProductsForm [id=addReturnProducts]").show();
								$("#updateWarehousingAbnormalForm [id=canSubmit]").show();
							} else {
								$("#addReturnProductsForm [id=addReturnProducts]").hide();
								$("#updateWarehousingAbnormalForm [id=canSubmit]").hide();
							}
							$("#updateWarehousingAbnormalForm [id=statusName]").html(d.statusName);
							
							var rows = $("#rightDataGrid").datagrid("getRows");
							for(var i = 0 ;i < rows.length; i ++){
								$("#rightDataGrid").datagrid("beginEdit", i);
							  	//绑定onchange事件
								var ed = $("#rightDataGrid").datagrid('getEditors', i);
								for (var j = 0; j < ed.length; j++)
								{
									var e = ed[j];
									if (e.field == 'count') {
										$(e.target).bind("change", function()
							            {
											$("#saveabnormal").show();
							            });
									}
								}
							}
							$(".deleteButton").linkbutton(
								{ 
									text:'删除', 
									plain:true
								}
							);
						} else {
							$.messager.alert("错误", d.tip, "info");
						}
					} catch (e) {
						$.messager.alert("错误", "异常", "info");
					}
				}
			});
		}
	});
});
//提交上审核
$('#auditPassed').click(function(){
	$.messager.confirm('确认', '确认审核通过？', function(r) {
		if (r) {
			var rows = $("#rightDataGrid").datagrid("getRows");
			var bat = new Array();
			var j = 0;
			for(var i = 0 ;i < rows.length; i ++){
				bat[j]= new Array();
				bat[j][0]=rows[i].id;
				bat[j][1]=rows[i].count;
				bat[j][2]=rows[i].oriname;
				bat[j][3]=rows[i].code;
				bat[j][4]=rows[i].name;
				j ++;
			}
			$.ajax({
				url : '${pageContext.request.contextPath}/SalesReturnController/submitWarehousingAbnormal.mmx',
				data : "bat="+bat+"&abnormalId="+$("#updateWarehousingAbnormalForm [id=abnormalId]").val(),
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$("#updateWarehousingAbnormalForm [id=auditPassed]").hide();
							$("#updateWarehousingAbnormalForm [id=auditNoPassed]").hide();
							$("#addReturnProductsForm [id=addReturnProducts]").hide();
							$("#updateWarehousingAbnormalForm [id=canSubmit]").hide();
							$("#updateWarehousingAbnormalForm [id=statusName]").html(d.statusName);
							$("#closeEditButton").click();
							$.messager.alert("提示", d.bsbyResult, "info");
						} else {
							$.messager.alert("错误", d.tip, "info");
						}
					} catch (e) {
						$.messager.alert("错误", "异常", "info");
					}
				}
			});
		}
	});
});
</script>
<div>
<div id="editabnormalListForm">
	<div style="width: 95%"><h1 align="center">编辑异常入库单</h1></div>
	<div align="right" style="width: 95%;color: red;font-size:14px"> 
		库地区:<input class="easyui-combobox" name='wareArea' id='wareArea' style="width: 80px;" 
				data-options="url:'<%=request.getContextPath()%>/SalesReturnController/getWareAreaJSON.mmx',  
								valueField : 'areaId',   
								textField : 'areaName',
								panelHeight : 'auto',
		   						editable : false"
		   		/>
	</div>
	<hr width="95%" align="left">
	<form id="updateWarehousingAbnormalForm" method="post">
		<font size="3" color="red">
			异常入库单号：<span id="code"></span>&nbsp;&nbsp;
			<input type="hidden" name="abnormalId" id="abnormalId"/>
			状态：<span id="statusName"></span>
		</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="javascript:void(0);" id="canSubmit" style="display:none" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true">确认提交</a>
		<a href="javascript:void(0);" id="auditPassed" style="display:none"  class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true">审核通过</a>
		<a href="javascript:void(0);" id="auditNoPassed" style="display:none"  class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true">审核不通过</a>
		<br/>
		<font size="4" style=""><strong>添加商品：请填入包裹内实际退回商品，确认提交后，将通过报损报溢单对库存数据进行修正</strong></font>
	</form>
</div>
	<table id="addWarehousingAbnormalDataGrid"></table>
<form id="addReturnProductsForm">
	<div id="addReturnProducts" align="center" style="display:none">
		<table class="tableForm" >
			<tr>
			<td>实际退回商品：</td>
			<td><input class="easyui-validatebox" id="realCode" name="realCode" data-options="required:true"/></td>&nbsp;&nbsp;&nbsp;&nbsp;
			<td>商品数量：</td>
			<td><input id="realCount" name="realCount" class="easyui-numberbox" data-options="min:1,max:999999,required:true"></td>&nbsp;&nbsp;&nbsp;&nbsp;
			<td><a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addReturnProductsForm.submit();">确认添加</a></td>
			</tr>
		</table>
	</div>
</form>
<br/>
<div style="height:30px">
	<table id="orderInfoDataGrid"></table>
</div>
<br/>
<div id="addgrid" style="display:none">
<fieldset>
<div data-options="border:false" style="height:200px;width:400px;overflow: hidden;float: left;">
	<table id="leftDataGrid"></table>
</div>
<form id="rightDataGridForm">
	<div data-options="border:false" style="height:200px;width:400px;overflow: hidden;float: right;">
			<table id="rightDataGrid"></table>
	</div>
</form>
<div id="theToolbar"></div>
</fieldset>
</div>
<div id="saveabnormal" style="display:none">
	<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addabnormalAll();">保存</a>
</div>
</div>
