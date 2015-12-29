<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<title>厂商管理</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var modifyDialog;
var modifyForm;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSupplierList.mmx',
	    queryParams : {
	    },
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
	        {field:'code',title:'厂商编号',width:20,align:'center'},  
	        {field:'name',title:'厂商名称',width:100,align:'center'},  
	        {field:'type',title:'厂商类型',width:20,align:'center',
	        	formatter : function(value, row, index) {
					if(row.type=="0" ){
						return "维修商";
					}
					if(row.type=="1" ){
						return "配件供应商";
					}
				}	
	        }, 
	        {field:'address',title:'厂商地址',width:100,align:'center'},   
	        {
				field : 'action',
				title : '操作',
				align :'center',
				width : 20,
				formatter : function(value, row, index) {
							return '<a href="javascript:void(0);" class="editbutton" onclick="modifySupplier('+row.id+','+row.type+',\''+row.name+'\',\''+ checkNull(row.address) +'\')">编辑</a>';
				}
	        }
	    ]],
	}); 
	
	$('#supplierType').combobox({   
		url:'<%=request.getContextPath()%>/Combobox/getSupplierType.mmx',
		valueField : 'id',   
		textField : 'text',
		panelHeight:'auto',
		width:'150'
	});
	
	
	$('#supplierType').combobox('setValue',-1);
	
	$('#modifyType').combobox({   
		url:'<%=request.getContextPath()%>/Combobox/getSupplierType.mmx',
		valueField : 'id',   
		textField : 'text',
		panelHeight:'auto',
		width:'150'
	});
	
	modifyForm = $('#modifyForm').form();
	modifyDialog = $('#modifyDialog').show().dialog({
		modal : true,
		minimizable : true,
		title : '修改供应商信息',
		buttons : [{
			text : '确定',
			handler : function() {
				modifyForm.form('submit', {
					url : '${pageContext.request.contextPath}/admin/AfStock/modifySupplier.mmx',
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
function addSupplier(){
	$('#addform').form('submit', {
		url : '${pageContext.request.contextPath}/admin/AfStock/addSupplier.mmx',
		type:'post',
		contentType: 'application/x-www-form-urlencoded;charset=utf-8',
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

function modifySupplier(id,type,name,address){
	$('#modifyType').combobox('setValue',type);
	modifyDialog.dialog('open');
	$('#modifyName').val(name);
	$('#supplierId').val(id);
	$("#modifyAddress").val(address);
}

function checkNull(str){
	if(str==null || str=='null' || str==undefined || str=='undefined'){
		return '';
	}
}
</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar"  style="height: auto;display: none;">
		<form id="addform" method="post">
				<div align="center">
				  <table class="tableForm">
				    <tr>
				      <td>
				        厂商类型：<select name="supplierType" id="supplierType" class="easyui-validatebox" required="required" panelHeight="auto" style="width:100px">
				          </select>
				        </td>
				      </tr>
				    <tr>
				      <td>
				        厂商名称： <input type="text" name="supplierName" class="easyui-validatebox" required="required" id="text" value="" >
				        </td>
				      </tr>
				      <tr>
					   	<td>
				        		厂商地址： <input type="text" name="address" id="address" class="easyui-validatebox" required="required">
				        		 <input type="button" name="button3" id="button3" onclick="addSupplier()" value="添加" />
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
				        		厂商类型：<select name="modifyType" id="modifyType" class="easyui-validatebox" required="required" panelHeight="auto" style="width:100px">
				        		</select>
				        </td>
					</tr>
					<tr>
					   	<td>
				        		厂商名称： <input type="text" name="modifyName" id="modifyName" class="easyui-validatebox" required="required"  value="" >
									    <input type="hidden" name="supplierId" id="supplierId"  value="" >
						</td>
					</tr>
					<tr>
					   	<td>
				        		厂商地址： <input type="text" name="modifyAddress" id="modifyAddress"  class="easyui-validatebox" required="required">
						</td>
					</tr>
				</table>
		</form>
	</div>
<table id="datagrid"></table>
</body>
</html>