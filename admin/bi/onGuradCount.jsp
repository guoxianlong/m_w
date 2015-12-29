<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.*" %>
<%
voUser user = (voUser)request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript">
$.extend($.fn.validatebox.defaults.rules, {
	INT : {
		validator : function(value, param) {
			return /^(0|[1-9][0-9]*)$/.test(value);
		},
		message : '数字格式不正确'
	},
	FLOAT : {
		validator : function(value, param) {
			return /^(0|[1-9][0-9]*)(.[0-9]{0,2})?$/.test(value);
		},
		message : '数字格式不正确'
	}
});

function checkAddBean(){
	var areaId = $("#areaId").combobox('getValue');
	if (areaId == null || areaId == -1) {
		alert("请选择仓库");
		return false;
	}
	
	var type = $("#type").combobox('getValue');
	if (type == null || type == -1) {
		alert("请选择人员类型");
		return false;
	}
	
	// 0 作业人员
	if (type == 0) {
		var operType = $("#operType").combobox('getValue');
		if (operType == null || operType == -1) {
			alert("请选择作业环节");
			return false;
		}
	} else {
		var department = $("#department").combobox('getValue');
		if (department == null || department == -1) {
			alert("请选择部门");
			return false;
		}
	}
	
	return true;
}

function addBean() {
	if(!checkAddBean())
		return;
	
	$('#addForm').form('submit',{
		url : '${pageContext.request.contextPath}/BIStoreController/addBIOnGuradCountBean.mmx',		
		success : function(data) {
			var d = (data != null && data.success != null) ? data : $.parseJSON(data);
			if (d) {
				if (d.success) {
					var areaId = $("#areaId").combobox('getValue'); 
					var type = $("#type").combobox('getValue');
					var datetime = $("#datetime").datebox('getValue'); 

		    		var $form = $("#addForm");
		    		var arr = ["turnOut", "onGuradTimeLength", "supportCount", "supportTimeLength", "beSupportCount", "beSupportTimeLength", "tempCount"];
		    		$.each(arr, function(){
		    			$('input[name="' + this + '"]', $form).val('');
		    		});
		    		$("#department").combobox('setValue', -1);
		    		$("#operType").combobox('setValue', -1);
					$("#areaId2").combobox('setValue', areaId);
					$("#type2").combobox('setValue', type);
					$("#datetime2").datebox('setValue', datetime);
					$("#getList").click();
				}
				$.messager.show({
					msg : d.msg,
					title : '提示'
				});
			}
		}
	});
}

var datagrid;
function getList(){	
	var areaId = $("#areaId2").combobox('getValue');
	if (areaId == null || areaId == -1) {
		alert("请选择仓库");
		$('#areaId2').focus();
		return;
	}
	var type = $("#type2").combobox('getValue');
	if (type == null || type == -1) {
		alert("请选择人员类型");
		$('#type2').focus();
		return;
	}
	var datetime = $("#datetime2").datebox('getValue');
	if (datetime == null || datetime == '') {
		alert('请选择日期');
		return;
	}

	var title = '';
	var columns = null;
	// 0作业人员
	if (type == 0) {
		title = '作业人员在岗人力基础数据';
		columns = [[			        
					{field:'operTypeName',title:'作业环节',align:'center',
						formatter : function(value,row,index){
							if(value == '')
								return '总&nbsp;&nbsp;计';
							return value;
						}},
			        {field:'onGuradCount',title:'实际在岗人数',align:'center'},
			        {field:'turnOut',title:'出勤人数',align:'center'},
			        {field:'onGuradTimeLength',title:'在岗总时长',align:'center'},
			        {field:'supportCount',title:'外派支援人数',align:'center'},
			        {field:'supportTimeLength',title:'外派支援工时',align:'center'},
			        {field:'beSupportCount',title:'接受支援人数',align:'center'},
			        {field:'beSupportTimeLength',title:'接受支援工时',align:'center'},
			        {field:'tempCount',title:'临时工人数',align:'center'},
			        {field:'status',title:'操作状态',align:'center',
			        	formatter : function(value,row,index){
			        		if(value == -1)
			        			return '';
			        		
			        		var temp = '';
			        		if(value == 0) {
			        			<% if (group.isFlag(2136)) { %>			        		
			        				temp += '<a href="javascript:void(0);" onclick="checkCount('+index+')">审核</a>&nbsp;&nbsp;';
		        				<% } %>
	        					temp += '<a href="javascript:void(0);" onclick="updateCount('+index+')">修改</a>';		        			
			        		} else {
				        		temp += '生效&nbsp;&nbsp;'; 
			        			<% if (group.isFlag(2137)) { %>			        		
			        				temp += '<a href="javascript:void(0);" onclick="deleteCount('+index+')">作废</a>';
	        					<% } %>				        		
			        		}      
			        		return temp;     
			        	}
			        }]];
	} else {
		title = '职能管理人员在岗人力基础数据';
		columns = [[			        
					{field:'departmentName',title:'部门',align:'center',
						formatter : function(value,row,index){
							if(value == '')
								return '总&nbsp;&nbsp;计';
							return value;
						}},
			        {field:'onGuradCount',title:'实际在岗人数',align:'center'},
			        {field:'turnOut',title:'出勤人数',align:'center'},
			        {field:'onGuradTimeLength',title:'在岗总时长',align:'center'},			       
			        {field:'status',title:'操作状态',align:'center',
			        	formatter : function(value,row,index){
			        		if(value == -1)
			        			return '';
			        		var temp = '';
			        		if(value == 0) {
			        			<% if (group.isFlag(2136)) { %>			        		
			        				temp += '<a href="javascript:void(0);" onclick="checkCount('+index+')">审核</a>&nbsp;&nbsp;';
		        				<% } %>
	        					temp += '<a href="javascript:void(0);" onclick="updateCount('+index+')">修改</a>';		        			
			        		} else {
				        		temp += '生效&nbsp;&nbsp;'; 
			        			<% if (group.isFlag(2137)) { %>			        		
			        				temp += '<a href="javascript:void(0);" onclick="deleteCount('+index+')">作废</a>';
	        					<% } %>				        		
			        		}      
			        		return temp;     
			        	}
			        }]];
	}
	
	getListByType(type, areaId, datetime, title, columns);
}

function getListByType(type, areaId, datetime, title, columns){
	datagrid = $("#onGuradCountList").datagrid({
		title: '' + title,
		idField : 'id',
		iconCls:'icon-ok',
		fitColumns:true,
		height:500,
		pageNumber:1,
		pageSize:20,
		pageList:[5,10,15,20],
		url:'${pageContext.request.contextPath}/BIStoreController/getBIOnGuradCountList.mmx',
		queryParams:{
			type: '' + type,
			datetime: '' + datetime,
			areaId: '' + areaId
		},
		showFooter:true,
		striped:true,
		collapsible:true,
		loadMsg:'数据加载中...',
		rownumbers:true,
		singleSelect:true,//只选择一行后变色
		pagination:true,
		columns:columns
	});
}

function ajaxOper(index, eMsg, harfUrl){
	if (index == null)
		return;		
	$('#onGuradCountList').datagrid('selectRow', index);
	var row = $('#onGuradCountList').datagrid('getSelected');
	if(row == null)
		return;
	$.ajax({
		type : "post",
		url : "<%=request.getContextPath()%>/BIStoreController/" + harfUrl + "",
		dataType : "json",
		cache : false,
		data : {
			id : row.id,
			datetime : row.datetime,
			areaId : row.areaId,
			updateTime : row.updateTime
		},
		error : function(x, s, e) {
			$.messager.show({
				msg : '' + eMsg,
				title : '提示失败'
			});
		},
		success : function(json) {
			if (json != null) {
				if (json.success) {
					$("#onGuradCountList").datagrid('reload');
				}
				$.messager.show({
					msg : json.msg,
					title : '提示'
				});
			}
		}
	});
}

function checkCount(index) {
	ajaxOper(index, '审核失败', 'checkBIOnGuradCountBean.mmx');
}

function updateCount(index) {
	if (index == null)
		return;		
	$('#onGuradCountList').datagrid('selectRow', index);
	var row = $('#onGuradCountList').datagrid('getSelected');
	if (row == null)
		return;
	var $form = $("#dialog [name='updateForm']");
	$("[name='id']", $form).val(row.id);
	$("[name='type']", $form).val(row.type);
	$("[name='operTypeName']", $form).val(row.operTypeName);
	$("[name='turnOut']", $form).val(row.turnOut);
	$("[name='onGuradTimeLength']", $form).val(row.onGuradTimeLength);
	$("[name='supportCount']", $form).val(row.supportCount);
	$("[name='supportTimeLength']", $form).val(row.supportTimeLength);
	$("[name='beSupportCount']", $form).val(row.beSupportCount);
	$("[name='beSupportTimeLength']", $form).val(row.beSupportTimeLength);
	$("[name='tempCount']", $form).val(row.tempCount);
	 
	$('#dialog').dialog('open');
}

function updateCount1(index) {
	if (index == null)
		return;		
	$('#onGuradCountList').datagrid('selectRow', index);
	var row = $('#onGuradCountList').datagrid('getSelected');
	if (row == null)
		return;
	
	var $form = $("#dialog1 [name='updateForm']");
	$("[name='id']", $form).val(row.id);		
	$("[name='type']", $form).val(row.type);		
	$("[name='departmentName']", $form).val(row.departmentName);
	$("[name='turnOut']", $form).val(row.turnOut);
	 
	$('#dialog1').dialog('open');
}

function deleteCount(index) {
	$.messager.confirm('作废确认','您是否要作废该人力数据?',function(r){
	    if (r){
	    	ajaxOper(index, '作废失败', 'deleteBIOnGuradCountBean.mmx');
	    }
	});
}

function updateBean(dialog) {
	$('#' + dialog + ' [name="updateForm"]').form('submit',{
		url : '${pageContext.request.contextPath}/BIStoreController/updateBIOnGuradCountBean.mmx',	
		success : function(data) {
			var d = (data != null && data.success != null) ? data : $.parseJSON(data);
			if (d) {
				if (d.success) {
					$('#' + dialog).dialog('close');
					$("#onGuradCountList").datagrid('reload');
				}
				$.messager.show({
					msg : d.msg,
					title : '提示'
				});
			}
		}
	});
}

$(function(){
	$('#areaId,#areaId2').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getBIArea.mmx',
	    valueField:'id',
	    textField:'text'
	});

	$('#department').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getDepartList.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
	
	$('#operType').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getOperTypeList.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
	
	$('#type2').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getStaffType.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
	
	$('#type').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getStaffType.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false,
	    onSelect:function(record) {
	    	$("#department").combobox("enable");
	    	$("#operType").combobox("enable");
	    	$("#department").combobox("setValue", -1);
	    	$("#operType").combobox("setValue", -1);
	    	
	    	// 1 职能管理人员
	    	if (record.id == 1) {
	    		$("#operType").combobox("disable");
	    		var $form = $("#addForm");
	    		var arr = ["onGuradTimeLength", "supportCount", "supportTimeLength", "beSupportCount", "beSupportTimeLength", "tempCount"];
	    		$.each(arr, function(){
	    			$e = $('input[name="' + this + '"]', $form);
	    			$e.attr('class', '');
	    			$e.val('0');
	    			$e.attr("disabled","disabled");
	    		});
	    	} else {
	    		if (record.id == 0) {
	    			$("#department").combobox("disable");
	    		}	    			
	    		var $form = $("#addForm");
	    		var arr = ["onGuradTimeLength", "supportCount", "supportTimeLength", "beSupportCount", "beSupportTimeLength", "tempCount"];
	    		$.each(arr, function(){
	    			$e = $('input[name="' + this + '"]', $form);
	    			$e.removeAttr('disabled');
	    			$e.val('');
	    			$e.attr('class', 'easyui-validatebox');
	    		});
	    	}
	    }
	});
	
});
</script>
<title>在岗人力</title>
</head>
<body>
<% if (group.isFlag(2135)) { %>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>录入在岗人力</legend>
			<form id="addForm" method="post">
				<table>
					<tr>
						<td><span style="font-size: 12px;">仓库&nbsp;&nbsp;&nbsp;</span><input id="areaId" name="areaId" style="width: 120px;" /></td>
						<td><span style="font-size: 12px;">人员类型&nbsp;</span><input id="type" name="type" style="width: 110px;" /></td>
						<td><div id = "divDepartment"><span style="font-size: 12px;">部门&nbsp;&nbsp;&nbsp;</span><input id="department" name="department" style="width: 120px;" /></div></td>
						<td><div id = "divOperType"><span style="font-size: 12px;">作业环节&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input id="operType" name="operType" style="width: 120px;" /></div></td>						
					</tr>
					<tr>
						<td colspan="5"><span style="font-size: 12px;">日期&nbsp;&nbsp;&nbsp;</span><input id="datetime" name="datetime"  class="easyui-datebox" editable="false" id="datetime" style="width: 120px;" data-options="required:true">
					</tr>
					<tr>
						<td><span style="font-size: 12px;">出勤人数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input validType="INT" type="text" name="turnOut" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">在岗总工时&nbsp;&nbsp;&nbsp;</span><input validType="FLOAT" type="text" name="onGuradTimeLength" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">外派支援人数&nbsp;</span><input validType="INT" type="text" name="supportCount" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">外派支援工时&nbsp;</span><input validType="FLOAT" type="text" name="supportTimeLength" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>										
					</tr>
					<tr>
						<td><span style="font-size: 12px; ">接受支援人数&nbsp;</span><input validType="INT" type="text" name="beSupportCount" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">接受支援工时&nbsp;</span><input validType="INT" type="text" name="beSupportTimeLength" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">临时工&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input validType="INT" type="text" name="tempCount" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>						
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td colspan="5"><a onclick="javascript:addBean();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" href="javascript:void(0);" >保存</a></td>					
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
			</form>
		</fieldset>		
	<div>
<% } %>	
	<div style="padding:3px;height: auto;">
		<table>
				<tr>
					<td><span style="font-size: 12px;">仓库&nbsp;&nbsp;&nbsp;</span><input id="areaId2" style="width: 120px;" /></td>
					<td><span style="font-size: 12px;">人员类型&nbsp;</span><input id="type2" style="width: 110px;" /></td>
					<td><span style="font-size: 12px;">日期&nbsp;</span><input class="easyui-datebox" editable="false" id="datetime2"></td>
					<td><a id="getList" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" onclick="javascript:getList();" >查询</a></td>
				</tr>
			</table>
			<table id="onGuradCountList"></table>
	<div>
	<div id="dialog" class="easyui-dialog" title="修改作业人员在岗人力基础数据" style="width:820px;height:210px;"
        data-options="modal:true,closed:true">
    		<form name="updateForm" method="post">
				<table>
					<tr>
						<td>
						<input type="hidden" name="id" />
						<input type="hidden" name="type" />						
						<span style="font-size: 12px;">作业环节&nbsp;</span><input name="operTypeName" readonly="readonly"/></td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td><span style="font-size: 12px;">出勤人数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input validType="INT" type="text" name="turnOut" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">在岗总工时&nbsp;&nbsp;&nbsp;</span><input validType="FLOAT" type="text" name="onGuradTimeLength" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">外派支援人数&nbsp;</span><input validType="INT" type="text" name="supportCount" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">外派支援工时&nbsp;</span><input validType="FLOAT" type="text" name="supportTimeLength" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>										
					</tr>
					<tr>
						<td><span style="font-size: 12px; ">接受支援人数&nbsp;</span><input validType="INT" type="text" name="beSupportCount" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">接受支援工时&nbsp;</span><input validType="INT" type="text" name="beSupportTimeLength" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">临时工&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input validType="INT" type="text" name="tempCount" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>						
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td colspan="5"><a onclick="javascript:updateBean('dialog');" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" href="javascript:void(0);" >保存</a></td>					
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
			</form>
	</div>
	<div id="dialog1" class="easyui-dialog" title="修改职能管理人员在岗人力基础数据" style="width:720px;height:210px;"
        data-options="modal:true,closed:true">
    		<form name="updateForm" method="post">
				<table>
					<tr>
						<td>
						<input type="hidden" name="id" />
						<input type="hidden" name="type" />			
						<span style="font-size: 12px;">部门&nbsp;</span><input name="departmentName" readonly="readonly"/></td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td><span style="font-size: 12px;">出勤人数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input validType="INT" type="text" name="turnOut" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td colspan="5"><a onclick="javascript:updateBean('dialog1');" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" href="javascript:void(0);" >保存</a></td>					
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
			</form>
	</div>
</body>
</html>