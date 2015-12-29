<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	var warehouseType = $('#warehouseType').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getAfBsbyStockType.mmx',
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false
	});
	
	var type = $('#type').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getAfBsbyType.mmx',
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false
	});
	
	$('#currentType').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBsbyCurrentType.mmx',                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false
	});
	
	//初始化datagrid
	initbsbyListDataGrid();
});

function initbsbyListDataGrid () {
	$('#bsbyListDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfBsByList.mmx',
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
			{field:'receipts_number',title:'单据编号',width:100,align:'center'},
			{field:'typeName',title:'单据类型',width:40,align:'center'},
			{field:'warehouse_type_name',title:'库类型',width:60,align:'center'},
			{field:'productCode',title:'商品编号',width:60,align:'center'},
			{field:'counts',title:'数量',width:60,align:'center'},
			{field:'price',title:'单价含税',width:40,align:'center'},
			{field:'priceNotOfTax',title:'单价不含税',width:40,align:'center'},
			{field:'allPrice',title:'总额含税',width:40,align:'center'},
			{field:'allPriceNotOfTax',title:'总额不含税',width:40,align:'center'},
			{field:'oriname',title:'商品原名称',width:60,align:'center'},
			{field:'current_type_name',title:'状态',width:60,align:'center'},
			{field:'add_time',title:'生成时间',width:100,align:'center',
				formatter : function(value, row, index) {
        			return value != null && value != undefined ? value.substring(0, 19) : '';
				}
			},
			{field:'operator_name',title:'制作人',width:80,align:'center'},
			/**{field:'end_oper_name',title:'运营审核人',width:80,align:'center'},
			{field:'end_time',title:'运营审核时间',width:80,align:'center'},
			{field:'finAuditName',title:'财务审核人',width:80,align:'center'},
			{field:'finAuditDatetime',title:'财务审核时间',width:80,align:'center',
				formatter : function(value, row, index) {
        			return value != null && value != undefined ? value.substring(0, 19) : '';
				}
			},**/
			{field:'remark',title:'报损报溢原因',width:80,align:'center'},
			{field:'action',title:'操作',width:120,align:'center',
	        	formatter : function(value, row, index) {
	        		var action = "";
					var current_type = row.current_type;
					var type = row.type;
					var warehouseType = row.warehouse_type;
					if(current_type==2 || current_type==5){
						if(type==1 && warehouseType==9){
							action = action + '<a href="#" class="deletebutton" onclick="deleteDialog('+row.id+')"></a>';
						}else{
							action = action + '<a href="#" class="editbutton" onclick="editDialog('+row.id+')"></a>';
							action = action + '<a href="#" class="deletebutton" onclick="deleteDialog('+row.id+')"></a>';
						}
					} else if (current_type==1) {
						action = action + '<a href="#" class="finAuditbutton" onclick="auditDialog('+row.id+')"></a>';
					} else if (current_type == 6) {
						action = action + '<a href="#" class="auditbutton" onclick="auditDialog('+row.id+')"></a>';	
					}
					action = action + '<a href="#" class="lookbutton" onclick="auditDialog('+row.id+',1)"></a>';	
	        		return action;
				}
			}
	    ] ],
		onLoadSuccess : function(data) {
			var footer = data.footer[0];
			for(var i = 0 ; i<data.rows.length ; i++ ){	
				var rowspan = 1;
				for(var j = i+1 ; j<data.rows.length ; j++){
					if(data.rows[i].receipts_number==data.rows[j].receipts_number){
						rowspan++;
					}
				}
			/**
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'receipts_number',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'typeName',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'warehouse_type_name',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'current_type_name',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'add_time',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'operator_name',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'finAuditName',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'finAuditDatetime',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'remark',
                rowspan: rowspan
         	});
			$('#bsbyListDataGrid').datagrid('mergeCells', {
                index: i,
                field: 'action',
                rowspan: rowspan
         	});
			rowspan = 1;
			*/
		}
			if (footer.editFlag) {
				$(".editbutton").linkbutton(
					{ 
						text:'编辑', 
						plain:true, 
						iconCls:'icon-edit' 
					}
				);
				$(".deletebutton").linkbutton(
					{ 
						text:'删除', 
						plain:true, 
						iconCls:'icon-cancel' 
					}
				);
			}
			if (footer.auditFlag) {
				$(".auditbutton").linkbutton(
					{ 
						text:'财务审核',
						plain:true, 
						iconCls:'icon-ok' 
					}
				);
			}
			if (footer.finAuditFlag) {
				$(".finAuditbutton").linkbutton(
					{ 
						text:'运营审核',
						plain:true, 
						iconCls:'icon-ok' 
					}
				);
			}
			$(".lookbutton").linkbutton(
				{ 
					text:'查看', 
					plain:true, 
					iconCls:'icon-search' 
				}
			);
		}
	}); 
};

function auditDialog(opid, lookup) {
	if (lookup != "" && lookup != "undefined" && lookup != null) {
		window.location.href = '${pageContext.request.contextPath}/admin/afStock/bsbyAudit.jsp?lookup=1&opid='+opid;
	} else {
		window.location.href = '${pageContext.request.contextPath}/admin/afStock/bsbyAudit.jsp?opid='+opid;
	}
}

function editDialog(opid) {
	window.location.href = '${pageContext.request.contextPath}/admin/AfStock/beforeUpdateAfterSaleBsby.mmx?strId='+opid;
}

function searchFun() {
	$("#bsbyListDataGrid").datagrid("load", {
		type:$("#tb input[id=type]").combobox("getValue"),
		warehouseType:$("#tb input[id=warehouseType]").combobox("getValue"),
		currentType:$("#tb input[id=currentType]").combobox("getValue"),
		code:$("#tb input[id=code]").val(),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		endTime:$("#tb input[id=endTime]").datebox("getValue")
	});
}

function deleteDialog(id) {
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/deleteBsby.mmx',
		data : {'id':id},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					$('#bsbyListDataGrid').datagrid("reload");
				} else {
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
</script>
</head>
<body>
		<table id="bsbyListDataGrid"></table>
		<div id="tb" style="padding:3px;height: auto;">
			<fieldset>
		   		<legend>查询条件</legend>
		   		<table class="tableForm">
		   			<tr align="center" >
						<th>单据类型：</th>
						<td align="left">
							<input id="type" name="type" style="width: 116px;"/>
						</td>
						<th>库类型：</th>
						<td align="left">
							<input id="warehouseType" name="warehouseType" style="width: 116px;"/>
						</td>
						<th>状态：</th>
						<td align="left">
							<input id="currentType" name="currentType" style="width: 116px;"/>
						</td>
						<th>单据号：</th>
						<td align="left">
							<input id="code" name="code" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center">
						<th>单据生成时间：</th>
						<td align="left" colspan="6">
							<input id="startTime" name="startTime" style="width: 116px;" class="easyui-datebox"/>
							--
							<input id="endTime" name="endTime" style="width: 116px;" class="easyui-datebox"/>
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