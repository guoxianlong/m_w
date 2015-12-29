<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*, adultadmin.util.*" %>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="java.util.*"%>
<%
	voUser user = (voUser)request.getSession().getAttribute("userView");
	int userid = user.getId();
	UserGroupBean group = user.getGroup();
%>
<!DOCTYPE html>
<html>
<head>
<title>报损报溢</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
var bsbyListFrom;
$(function(){
	var warehouseType = $('#warehouseType').combobox({
		url : '${pageContext.request.contextPath}/ByBsController/getWarehouseTypeName.mmx',
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false,
		onSelect : function(record){
			//刷新数据，重新读取省份下的城市，并清空当前输入的值
			warehouseArea.combobox({
				url:'${pageContext.request.contextPath}/ByBsController/getWareAreaName.mmx?warehouseType='+record.id,
				valueField:'id',
				textField:'text',
				panelHeight : 'auto',
				editable : false
			}).combobox('clear');
		}
	});
	var warehouseArea = $('#warehouseArea').combobox({
		url : '${pageContext.request.contextPath}/ByBsController/getWareAreaName.mmx?warehouseType=',
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false
	});
	
	var type = $('#type').combobox({
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false,
		data : [{
			id : "",
			text : "全部"
		} , {
			id : "0",
			text : "报损"
		} , {
			id : "1",
			text : "报溢"
		}]
	});
	
	var warehouse_type = $('#warehouse_type').combobox({
		url : '${pageContext.request.contextPath}/ByBsController/getWarehouseTypeName.mmx',
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false,
		onSelect : function(record){
			//刷新数据，重新读取省份下的城市，并清空当前输入的值
			warehouse_area.combobox({
				url:'${pageContext.request.contextPath}/ByBsController/getWareAreaName.mmx?warehouseType='+record.id,
				valueField:'id',
				textField:'text',
				panelHeight : 'auto',
				editable : false
			}).combobox('clear');
		}
	});
	var warehouse_area = $('#warehouse_area').combobox({
		url : '${pageContext.request.contextPath}/ByBsController/getWareAreaName.mmx?warehouseType=',
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false
	});
	
	var operationnoteType = $('#operationnoteType').combobox({
		valueField : 'id',
		textField : 'text',
		panelHeight : 'auto',
		editable : false,
		data : [ {
			id : "0",
			text : "报损"
		} , {
			id : "1",
			text : "报溢"
		}]
	});
	
	operationnoteType.combobox("setValue", "0");
	
	//初始化datagrid
	initbsbyListDataGrid();
	
	bsbyListFrom = $("#searchForm").form({
		url : '${pageContext.request.contextPath}/ByBsController/searchBybsList.mmx',
	});
});

function initbsbyListDataGrid () {
	$('#bsbyListDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/ByBsController/searchBybsList.mmx',
		queryParams : {
			firstTime : 'first'
		},
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		toolbar : '#tb',
		pagination : true,
		pageSize : 50,
		pageList : [ 10, 20, 30, 40, 50 ],
		columns : [ [ {
			field : 'receipts_number',
			title : '单据号',
			width : 130,
			align : 'center',
			formatter : function(value, row, index) {
				var type = row.current_type;
				var userid = "<%=userid%>";
				if((type==0||type==1||type==2||type==5)&&(userid==row.operator_id||<%=group.isFlag(413)%>)){
					return '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid='+row.id+'">'+value+'</a>';
				}else if(type==6&&(userid==row.operator_id||<%=group.isFlag(229)%>)){
					return '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid='+row.id+'">'+value+'</a>';
				}else {
					return '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?lookup=1&opid='+row.id+'">'+value+'</a>';
				}
			}
		}, {
			field : 'sourceCode',
			title : '相关盘点作业单',
			width : 140,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != null && value != "" && value != "undefined") {
					return '<a href="<%=request.getContextPath()%>/admin/cargoInventory.do?method=cargoInventory&id='+row.source+'%>" target="_blank">'+value+'</a>';
				} else {
					return "";
				}
			}
		}, {
			field : 'warehouse_type_name',
			title : '库类型',
			width : 120,
			align : 'center'
		}, {
			field : 'warehouse_area_name',
			title : '库区域',
			width : 150,
			align : 'center'
		}, {
			field : 'productCode',
			title : '产品编号',
			width : 120,
			align : 'center'
		}, {
			field : 'oriname',
			title : '原名称',
			width : 100,
			align : 'center'
		}, {
			field : 'add_time',
			title : '添加时间',
			width : 100,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return value.substr(11,5);
				} else {
					return "";
				}
			}
		},  {
			field : 'operator_name',
			title : '添加人',
			width : 100,
			align : 'center'
		}, {
			field : 'current_type_name',
			title : '状态',
			width : 100,
			align : 'center'
		},{
			field : 'finAuditDatetime',
			title : '财务审核人/时间',
			width : 180,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return row.finAuditName+"/"+value.substr(0,19);
				} else {
					return "";
				}
			}
		},{
			field : 'end_oper_name',
			title : '审核人',
			width : 100,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return value;
				} else {
					return "";
				}
			}
		}, {
			field : 'end_time',
			title : '完成时间',
			width : 100,
			align : 'center',
			formatter : function(value, row, index) {
				if (value != 'undefined' && value != "" && value != null) {
					return value.substr(0,19);
				} else {
					return "";
				}
			}
		}, {
			field : 'action',
			title : '操作',
			width : 250,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				var type = row.current_type;
				var userid = "<%=userid%>";
				//没有完成以前都可以编辑和删除单据，如果提交审核后，只有有权限的人才能修改单据内的具体参数，当单据被审核打回时，添加人能修改单据内的具体参数同时能删除单据
				if((type==0||type==1||type==2||type==5)&&(userid==row.operator_id||<%=group.isFlag(413)%>)){
					action = action + '<a href="#" class="editbutton" onclick="editDialog('+row.id+',0)">编辑</a>';
					if(type!=1){//提交审核后不能删除 
						action = action + '<a href="#" class="deletebutton" onclick="deletebsby(\''+row.id+'\',\''+row.receipts_number+'\');">删除</a>';
					}
				}else if(type==6&&(userid==row.operator_id||<%=group.isFlag(229)%>)){
					action = action + '<a href="#" class="editbutton" onclick="editDialog('+row.id+',0)">编辑</a>';
				}else if(type==3||type==4){//已经完成的单据
					action = action + '<a href="#" class="lookbutton" onclick="editDialog('+row.id+',1)">查看</a>';
					action = action + '<a href="#" class="printbutton" onclick="printByBs(\''+row.id+'\',\''+row.receipts_number+'\')">打印</a>';
					action = action + '<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/printRecord.jsp?opid='+row.id+'" class="printbutton"  target="_blank">打印'+row.print_sum+'次</a>';
				}else{
					action = action + '<a href="#" class="lookbutton" onclick="editDialog('+row.id+',1)">查看</a>';
				}
				return action;
			}
		} ] ],
		onLoadSuccess : function(data) {
			//改变datagrid中按钮的class
			$(".editbutton").linkbutton(
				{ 
					text:'编辑', 
					plain:true, 
					iconCls:'icon-edit' 
				}
			);
			$(".printbutton").linkbutton(
				{ 
					plain:true, 
					iconCls:'icon-print' 
				}
			);
			$(".deletebutton").linkbutton(
				{ 
					text:'删除', 
					plain:true, 
					iconCls:'icon-cancel' 
				}
			);
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

//查询
function bsbyListSearch() {
	var startTime = $('#searchForm').find('[id=startTime]').datebox("getValue");
	var endTime = $('#searchForm').find('[id=endTime]').datebox("getValue");
	if ((startTime != "" && endTime == "") || (startTime != "" && endTime == "")) {
		$.messager.alert("提示", "生成时间段必须填写完整", "info");
		return false;
	}
	if (startTime != "" && endTime != "") {
		var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;
		if (!re.test(startTime) || !re.test(endTime)){
			$.messager.alert("提示", '日期格式不合法', "info");
			return false;
		}
		if(startTime > endTime){
			$.messager.alert("提示", '开始时间不能大于结束时间', "info");
			return false;
		}
	}
	$("#bsbyListDataGrid").datagrid("load",{
		code : $('#searchForm').find('[name=code]').val(),
		sourceCode : $('#searchForm').find('[name=sourceCode]').val(),
		startTime : $('#searchForm').find('[id=startTime]').datebox("getValue"),
		endTime : $('#searchForm').find('[id=endTime]').datebox("getValue"),
		warehouseType : $('#searchForm').find('[id=warehouseType]').combobox("getValue"),
		warehouseArea : $('#searchForm').find('[id=warehouseArea]').combobox("getValue"),
		type : $('#searchForm').find('[id=type]').combobox("getValue"),
		status : getCheckBoxValue(),
		productCode : $('#searchForm').find('[name=productCode]').val(),
		productName : $('#searchForm').find('[name=productName]').val()
	});
}

//获取checkbox值
function getCheckBoxValue() {
    var str="";
    $("#searchForm [name='status']:checkbox").each(function(){ 
        if($(this).attr("checked")){
            str += $(this).val()+","
        }
    })
    return str.substring(0, str.length-1);
};

function bsbyListExport() {
	bsbyListFrom.submit();
};

//添加报损报溢单
function addBsByInfo() {
	var warehouse_area = $("#warehouse_area").combobox("getValue");
	var warehouse_type = $("#warehouse_type").combobox("getValue");
	if(warehouse_type == "")
	{
	 	$.messager.alert("提示", "请选择库类型!", "info");
	 	return false;
	}
	if(warehouse_area == "")
	{
		$.messager.alert("提示", "请选择库区域!", "info");
	 	return false;
	}
	
	$.ajax({
		url : '${pageContext.request.contextPath}/ByBsController/add.mmx',
		data : "warehouse_area="+warehouse_area+"&warehouse_type="+warehouse_type+"&operationnoteType="+$("#operationnoteType").combobox("getValue"),
		dataType : 'text',
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.result == 'success') {
					$.messager.alert("提示", d.tip, "info", function() {editDialog(d.opid);});
				} else {
					$.messager.alert("错误", d.tip, "info", function() {});
				}
			} catch (e) {
				$.messager.alert("错误", "异常", "info", function() {});
			}
		}
	});
}

function editDialog(opid, lookup) {
	if (lookup != "" && lookup != "undefined" && lookup != null) {
		window.location.href = '${pageContext.request.contextPath}/ByBsController/getByOpid.mmx?lookup=1&opid='+opid;
	} else {
		window.location.href = '${pageContext.request.contextPath}/ByBsController/getByOpid.mmx?opid='+opid;
	}
}

function deletebsby(id, code){
	$.messager.confirm('确认', '确认删除？', function(r) {
		if (r) {
			$.ajax({
				url : '${pageContext.request.contextPath}/ByBsController/delBybsOpre.mmx',
				data : "opid="+id+"&code="+code,
				dataType : 'text',
				success : function(data) {
					try {
						var d = $.parseJSON(data);
						if (d.result == 'success') {
							$.messager.alert("提示", d.tip, "info", function() {$('#bsbyListDataGrid').datagrid("reload");});
						} else {
							$.messager.alert("错误", d.tip, "info", function() {$('#bsbyListDataGrid').datagrid("reload");});
						}
					} catch (e) {
						$.messager.alert("错误", "异常", "info", function() {$('#bsbyListDataGrid').datagrid("reload");});
					}
				}
			});
		}
	});
};

function printByBs(id, code) {
	window.location.href = "<%=request.getContextPath()%>/admin/rec/oper/bsby/bsbyPrint.jsp?opid="+id+"&opcode="+code;
};

</script>
</head>
<body>
		<table id="bsbyListDataGrid"></table>
		<div id="tb" style="padding:3px;height: auto;">
			<center>
				<strong>报损报溢操作记录</strong>
			</center>
			<fieldset>
		   		<legend>查询条件</legend>
		   		<form id="searchForm">
					<span>单据号：&nbsp;&nbsp;</span>
					<input name='code' id='code' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>盘点作业单编号：</span>
					<input name='sourceCode' id='sourceCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>生成时间段：</span>
					<input class="easyui-datebox" name='startTime' id='startTime' style="width:152px;border:1px solid #ccc"/>&nbsp;到&nbsp;
					<input class="easyui-datebox" name='endTime' id='endTime' style="width:152px;border:1px solid #ccc"/><br>
					<span>库类型：&nbsp;&nbsp;</span>
					<input name='warehouseType' id='warehouseType' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>库地区：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
					<input name='warehouseArea' id='warehouseArea' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>单据类型：&nbsp;&nbsp;</span>
					<input name='type' id='type' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;<br>
					<span>状态：&nbsp;&nbsp;&nbsp;&nbsp;</span>
					<input type="checkbox" name="status" value="0"/>处理中&nbsp;&nbsp;
					<input type="checkbox" name="status" value="1"/>审核中&nbsp;&nbsp;
					<input type="checkbox" name="status" value="6"/>财务审核通过&nbsp;&nbsp;
					<input type="checkbox" name="status" value="5"/>财务审核不通过&nbsp;&nbsp;
					<input type="checkbox" name="status" value="2"/>审核未通过&nbsp;&nbsp;
					<input type="checkbox" name="status" value="4"/>已完成&nbsp;&nbsp;<br>
					<span>产品编号：</span>
					<input name='productCode' id='productCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>产品原名称：&nbsp;&nbsp;&nbsp;&nbsp;</span>
					<input name='productName' id='productName' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<input type="hidden" id="excel" name="excel" value="1"/>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="bsbyListSearch();">查询</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="bsbyListExport();">导出excel</a>
				</form>
			</fieldset>
			<table>
				<tr>
					<th>单据类型：</th>
					<td>
						<input name='operationnoteType' id='operationnoteType' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					</td>
					<th>库类型：</th>
					<td>
						<input name='warehouse_type' id='warehouse_type' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					</td>
					<th>库区域：</th>
					<td>
						<input name='warehouse_area' id='warehouse_area' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					</td>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addBsByInfo();">添加报损报溢单</a>
					</td>
				</tr>
			</table>
		</div>
</body>
</html>