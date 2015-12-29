<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<title>仓内作业合格时间设置</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getQualifiedTimeList.mmx',
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
	    pageList : [ 10, 20, 30],
	    frozenColumns : [[
					{field:'id',title:'',width:20,hidden:true}
	    ]],
	    columns:[[  
	        {field:'jobName',title:'作业名称',width:30,align:'center'},  
	        {field:'qualifiedTimeStr',title:'合格时间',width:30,align:'center'},
	        {field:'remark',title:'说明',width:120,align:'center'},  
	        {field:'action',title:'操作',width:30,align:'center',
	        	formatter : function(value, row, index) {
        			return '<a href="javascript:void(0);"  class="edit" onclick="editQualifiedTime('+index+')"></a>'
        						+'<a href="javascript:void(0);"  class="queryLog" onclick="queryLog('+index+')"></a>';
				}}
	    ]],
	    onLoadSuccess : function(data){
	    	$(".edit").linkbutton({ 
					text:'编辑'
				}
			);
			$(".queryLog").linkbutton({ 
					text:'操作记录'
				}
			);
	    }
	 });
});

function editQualifiedTime(index){
	if (index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	$("#qualifiedTimeId").val(row.id);
	$('#qualifiedTimeDiv').dialog("open");
}

function saveAualifiedTime(){
	var qualifiedTimeId = $("#qualifiedTimeId").val();
	var day = $("#day").val();
	var hour = $("#hour").val();
	var second = $("#second").val();
	$('#qualifiedTimeForm').form('submit',{
		url : '${pageContext.request.contextPath}/admin/AfStock/editQualifiedTime.mmx',
		onSubmit : function(){
			if(day==''&& hour=='' && second==''){
				$.messager.show({
					title : '提示',
					msg : '请输入合格时间!'
				});
				return false;
			}
			if(day=='0'&& hour=='0' && second=='0'){
				$.messager.show({
					title : '提示',
					msg : '请输入合格时间!'
				});
				return false;
			}
		},
		success : function(data){
			var r = $.parseJSON(data);
			$.messager.show({
				title : '提示',
				msg : r.msg
			});
			if(r.success){
				$('#qualifiedTimeDiv').dialog('close');
				$('#datagrid').datagrid('reload');
				formReset('qualifiedTimeForm');
			}
		}
	});
}

function cancel(){
	$('#qualifiedTimeDiv').dialog('close');
	formReset('qualifiedTimeForm');
}

function queryLog(index){
	if (index != undefined) {
		$('#datagrid').datagrid('selectRow', index);
	}
	var row = $('#datagrid').datagrid('getSelected');
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/afStock/afterSaleWareJobQuafiliedTimeLog.jsp',
		width : 1000,
		height : 520,
		modal : true,
		maximizable : true,
		resizable : true,
		title : '操作日志',
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
			$("#qualifiedTimeLogDatagrid").datagrid({
				url:'${pageContext.request.contextPath}/admin/AfStock/getQualifiedTimeLog.mmx?qualifiedTimeId='+row.id,
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
					{field:'operateTime',title:'操作时间',width:60,align:'center'},
					{field:'username',title:'操作人',width:100,align:'center'},
					{field:'remark',title:'操作描述',width:80,align:'center'},
				 ] ]
			});
		}
	});
}

function formReset(id){
	$(':input','#' + id)  
	 .not(':button, :submit, :reset')  
	 .val('')  
	 .removeAttr('checked')  
	 .removeAttr('selected'); 
}
</script>
</head>
<body>
	<table id="datagrid"></table> 
	<div id="qualifiedTimeDiv" class="easyui-dialog" title="编辑仓内作业合格时间" closed="true" style="width:500px;height:300px;">
		<form id="qualifiedTimeForm" method="post">
			<table class="tableForm" align="center">
				<tr>
					<td>
						<input type="text" id="day"  name="day" class="easyui-numberbox" data-options="min:0" style="width:50px;"/>天
						<input type="text" id="hour" name="hour"  class="easyui-numberbox" data-options="min:0,max:23" style="width:50px;"/>小时
						<input type="text" id="second" name="second" class="easyui-numberbox" data-options="min:0,max:59" style="width:50px;"/>分钟
						<input type="hidden" id="qualifiedTimeId" name="qualifiedTimeId">
					</td>
				</tr>
				<tr>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton"  onclick="saveAualifiedTime();">保存</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" onclick="cancel();">取消</a>
					</td>
				</tr>
			</table> 
		</form>
	</div>
</body>
</html>