<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
var addabnormalListForm;
var addReturnProductsForm;
//存放显示的数据
var theData;
$(function (){
	$("#addabnormalListForm [id=code]").focus();
	$("#addabnormalListForm [id=code]").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
        	$("#addabnormalListForm").find("[id=searchOrder]").click(); 
            return false;
        }
    });
	addabnormalListForm = $('#addabnormalListForm').form( {
		url : '<%=request.getContextPath()%>/SalesReturnController/findProductInfoList.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			return isValid;
		},
		success : function(data) {
			try {
				var data = $.parseJSON(data);
				if (data.result == 'failure') {
					$.messager.alert("提示", data.tip, "info", function(){focusCode();});
				} else {
					//订单存在
					initAddWarehousingAbnormalDataGrid(data.data.rows);
					theData = data.data.rows;
					
					//orderinfo
					initOrderInfoDataGrid(data.columns);
				    
				    //商品退回
					var addReturnProducts = "";
					
					$("#addReturnProductsForm").find("[id=addReturnProducts]").css("display", "inline");
					$("#saveabnormal").css("display", "inline");
					$("#addgrid").css("display", "inline");
					initLeftDataGrid();
					initRightDataGrid();
					
					$("#addabnormalListForm").find("[id=code]").attr("disabled", "disabled");
					$("#addabnormalListForm").find("[id=wareArea]").combobox("disable");
					$("#addabnormalListForm").find("[id=searchOrder]").linkbutton('disable');;
				}
			} catch(e) {
				$.messager.alert("提示", "异常", "info", function(){focusCode();});
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
						$.messager.alert("提示", "此商品已添加!", "info");
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
					var rowIndex = $('#rightDataGrid').datagrid("getRows").length-1;
					$('#rightDataGrid').datagrid('beginEdit', rowIndex);
					realCodeFocus();
				}
			} catch(e) {
				$.messager.alert("提示", "异常", "info", function(){realCodeFocus()});
			}
		}
	});
});

function realCodeFocus() {
	$("#addReturnProductsForm [id=realCode]").attr("value", "");
	$("#addReturnProductsForm [id=realCount]").numberbox("setValue", "");
	$("#addReturnProductsForm [id=realCode]").focus();
}
function deleteRightDataGrid(code) {
	var rightDataGrid = $('#rightDataGrid');
	rightDataGrid.datagrid("selectRecord", code);
	var row = rightDataGrid.datagrid("getSelected");
	var rowIndex = rightDataGrid.datagrid("getRowIndex", row);
	rightDataGrid.datagrid("deleteRow", rowIndex);
}

//点击查看订单时判断是否成功，成功显示其他信息
function addWarehousingAbnormalSearch() {
	//查看订单
	addabnormalListForm.submit();
};

function initAddWarehousingAbnormalDataGrid(rows) {
	$('#addWarehousingAbnormalDataGrid').datagrid({
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		toolbar: [{
			text : "确认添加",
			iconCls: 'icon-add',
			handler: function(){
				$("#leftDataGrid").datagrid("loadData", theData);
			}
		}],
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
		} ] ]
	});
	$('#addWarehousingAbnormalDataGrid').datagrid("loadData", rows);
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

function initLeftDataGrid() {
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
};

function initRightDataGrid() {
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
			align : 'center'
		} ] ]
    };
    var dataGrid = $("#rightDataGrid");
    dataGrid.datagrid(options);//根据配置选项，生成datagrid
};

function focusCode() {
	$("#addabnormalListForm").find("[id=code]").attr("value", "");
	$("#addabnormalListForm").find("[id=code]").focus();
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
		}
		$(".deleteButton").linkbutton(
			{ 
				text:'删除', 
				plain:true
			}
		);
		$.ajax({
			url : '${pageContext.request.contextPath}/SalesReturnController/addWarehousingAbnormal.mmx',
			data : "bat="+bat,
			dataType : 'text',
			success : function(data) {
				try {
					var d = $.parseJSON(data);
					if (d.result == 'success') {
						$("#closeAddButton").click();
						editAbnormal(d.abnormalId);
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
<div style="width: 95%"><h1 align="center">添加异常入库单</h1></div>
<form id="addabnormalListForm"  method="post">
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
	<font size="4" style=""><strong>添加商品：请填入包裹内实际退回商品，确认提交后，将通过报损报溢单对库存数据进行修正</strong></font>
	<table class="tableForm"  >
		<tr>
			<th>订单号/包裹单号：</th>
			<td>
				<input class="easyui-validatebox" name='code' id='code' style="width: 150px;" data-options="required:true"/>&nbsp;&nbsp;
			</td>
			<td>
				<a id="searchOrder" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="addWarehousingAbnormalSearch();">查看订单</a>
			</td>
		</tr>
	</table>
</form>
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
<div data-options="border:false" style="height:100px;width:400px;overflow: hidden;float: left;">
	<table id="leftDataGrid"></table>
</div>
<form id="rightDataGridForm">
	<div data-options="border:false" style="height:100px;width:400px;overflow: hidden;float: right;">
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
