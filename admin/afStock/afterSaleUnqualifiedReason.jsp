<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<title>不合格原因管理</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var dialog;
var editDialog;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getUnqualifiedReasonDatagrid.mmx',
	    toolbar : '#toolbar',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    idField : 'id',//记住翻页之后已选中相
	    rownumbers : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    columns:[[  
			{field:'id',title:'序号',width:20,hidden:true},
	        {field:'name',title:'内容',width:20,align:'center'},  
	        {field : 'action',title : '操作',align :'center',width : 20,
				formatter : function(value, row, index) {
							return '<a href="javascript:void(0);" class="editbutton" onclick="editFun(\''+row.id+'\',\'' + row.name  + '\')">编辑</a>' + 
										'<a href="javascript:void(0);" class="delbutton" onclick="delFun(\''+row.id+'\')">删除</a>' ;
			}}
	    ]],
	    onLoadSuccess : function(data){
	    	$('.editbutton').linkbutton({    
	    		iconCls:'icon-edit',
				plain:true,
	    	});          
	    	$('.delbutton').linkbutton({  
	    		iconCls:'icon-cancel',
				plain:true,
	    	}); 
	    }
	}); 
	modifyForm = $('#modifyForm').form();
	modifyDialog = $('#modifyDialog').show().dialog({
		modal : true,
		minimizable : true,
		title : '修改不合格原因',
		buttons : [{
			text : '确定',
			handler : function() {
				modifyForm.form('submit', {
					url : '${pageContext.request.contextPath}/admin/AfStock/editUnqualifiedReason.mmx',
					success : function(data) {
						var d = $.parseJSON(data);
						if (d) {
							modifyDialog.dialog('close');
							$.messager.show({
								msg : d.msg,
								title : '提示'
							});
							datagrid.datagrid('reload');
						}
					}
				});
			}
		}]
	}).dialog('close');
});
function addFun(){
	$('#addform').form('submit', {
		url : '${pageContext.request.contextPath}/admin/AfStock/addUnqualifiedReason.mmx',
		success : function(data) {
			var d = $.parseJSON(data);
			if (d) {
				$('#datagrid').datagrid('reload');
				$.messager.show({
					msg : d.msg,
					title : '提示'
				});
			}
		}
	});
}

function editFun(id,name){
	$('#modifyDialog').find("input[name=id]").val(id);
	$('#modifyDialog').find("input[name=name]").val(name);
	modifyDialog.dialog('open');
}
function delFun(id){
	$.ajax({
		url :  '${pageContext.request.contextPath}/admin/AfStock/delUnqualifiedReason.mmx',
		data : {
			reasonId : id,
		},
		dataType : 'json',
		type : 'post',
		success : function(r){
			if (r) {
				$('#datagrid').datagrid('reload');
				$.messager.show({
					msg : r.msg,
					title : '提示'
				});
			}
		}
	});
}
</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar"  style="height: auto;display: none;">
		<form id="addform" method="post">
				<div align="left">
				  <table class="tableForm">
				    <tr>
				      <td>
				       		原因名称: <input type="text" name="name" class="easyui-validatebox" required="required"s id="text" value="" >
									      <a class="easyui-linkbutton" iconCls="icon-save" plain="false" onclick="addFun();" href="javascript:void(0);">添加</a>
				        </td>
				      </tr>
				    </table>
  </div>
</form>
</div>
<div id="modifyDialog" style="overflow-y:auto; overflow-x:auto; display: none;">
		<form id="modifyForm" method="post">
				<table class="modifyForm">
					<tr>
				        <td>
				        		内容:<input type="text" name="name" id="name" class="easyui-validatebox" required="required"  value="" >
				        </td>
					</tr>
					<tr>
					   	<td>
								<input type="hidden" name="id" id="id"  value="" >
						</td>
					</tr>
				</table>
		</form>
	</div>
<table id="datagrid"></table>
</body>
</html>