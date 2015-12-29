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
	
	function addBean(){
		var areaId = $("#areaId").combobox('getValue');
		if (areaId == null || areaId == -1) {
			alert("请选择仓库");
			return;
		}
		var operType = $("#operType").combobox('getValue');
		if (operType == null || operType == -1) {
			alert("请选择作业环节");
			return;
		}
		$('#addForm').form('submit',{
			url : '${pageContext.request.contextPath}/BIStoreController/addBIStandardCapacityBean.mmx',
			success : function(data) {
				var d = (data != null && data.success != null) ? data : $.parseJSON(data);
				if (d) {
					if (d.success) {
						$('#addForm input[name="standardCapacity"]').val('');
						$("#areaId2").combobox('setValue', areaId);
						$("#operType2").combobox('setValue', -1);
						$("#status").combobox('setValue', -1);						
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
	function getList() {
		var areaId = $("#areaId2").combobox('getValue');
		if (areaId == null || areaId == -1) {
			alert("请选择仓库");
			$('#areaId').focus();
			return;
		}
		var operType = $("#operType2").combobox('getValue'); 
		var status = $("#status").combobox('getValue');
		
		datagrid = $("#list").datagrid({
			title:"标准产能列表",
			idField : 'id',
			iconCls:'icon-ok',
			fitColumns:true,
			height:500,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'${pageContext.request.contextPath}/BIStoreController/getBIStandardCapacityList.mmx',
			queryParams:{
				status: '' + status,
				operType: '' + operType,
				areaId: '' + areaId
			},
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			columns:[[
					{field:'operTypeName',title:'作业环节',align:'center'},
					{field:'standardCapacity',title:'标准产能',align:'center'},
			        {field:'startTime',title:'生效日期',align:'center',
			        	formatter: function (value, row, index) {
			        		if(value == null || value == '')
			        			return "";
			        		return value.substring(0,10);
			        	}},
				    {field:'stopTime',title:'停用日期',align:'center',
				        	formatter: function (value, row, index) {
				        		if(value == null || value == '')
				        			return "";
				        		return value.substring(0,10);
				        	}},
			        {field:'statusName',title:'数据状态',align:'center',
					        	formatter: function (value, row, index) {
					        		var current = new Date();
					        		var start = null;
					        		var stop = null;
					        		if(row.startTime != null && row.startTime != ''){
					        			start = new Date(row.startTime.substring(0,19).replace(/\-/g, "\/"));
					        		}
					        		if(row.stopTime != null && row.stopTime != ''){
					        			stop = new Date(row.stopTime.substring(0,19).replace(/\-/g, "\/"));
					        		}

					        		if (start != null) {
						        		if (start > current && (stop == null || stop > start )) {
						        			return "未生效";
						        		}
						        		if (start <= current && ( stop == null || stop > current )) {
						        			return "已生效";
						        		}
					        		}
									return "已停用";
					        }}
			]]
		});
	}
	
	$(function(){
		$('#areaId,#areaId2').combobox({
		    url:'${pageContext.request.contextPath}/BIStoreController/getBIArea.mmx',
		    valueField:'id',
		    textField:'text',
		    editable:false
		});
		$('#operType,#operType2').combobox({
		    url:'${pageContext.request.contextPath}/BIStoreController/getEnableSCOperTypeList.mmx',
		    valueField:'id',
		    textField:'text',
		    editable:false
		});
		$('#status').combobox({
		    url:'${pageContext.request.contextPath}/BIStoreController/getBISCStatus.mmx',
		    valueField:'id',
		    textField:'text',
		    editable:false
		});
	});	
</script>

<title>标准产能</title>
</head>
<body>
<% if (group.isFlag(2140)) { %>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>录入标准产能</legend>
			<form id="addForm" method="post">
				<table>		 
					<tr>
						<td><span style="font-size: 12px;">仓库&nbsp;&nbsp;&nbsp;</span><input id="areaId" name="areaId" style="width: 80px;" /></td>
						<td><span style="font-size: 12px;">作业环节&nbsp;&nbsp;&nbsp;</span><input id="operType" name="operType" style="width: 80px;" /></td>						
						<td><span style="font-size: 12px;">生效日期&nbsp;&nbsp;&nbsp;</span><input name="startTime"  class="easyui-datebox" editable="false" id="datetime" style="width: 110px;" data-options="required:true">
						<td><span style="font-size: 12px;">标准产能&nbsp;</span><input validType="FLOAT" type="text" name="standardCapacity" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><a onclick="javascript:addBean();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" href="javascript:void(0);" >保存</a></td>					
					</tr>
				</table>
			</form>
		</fieldset>		
	<div>
<% } %>	
	<div style="padding:3px;height: auto;">
			<table>
				<tr>
					<td><span style="font-size: 12px;">&nbsp;&nbsp;仓库&nbsp;&nbsp;&nbsp;</span><input id="areaId2" style="width: 80px;" /></td>
					<td><span style="font-size: 12px;">作业环节&nbsp;&nbsp;&nbsp;</span><input id="operType2" style="width: 80px;" /></td>
					<td><span style="font-size: 12px;">数据状态&nbsp;&nbsp;&nbsp;</span><input id="status" style="width: 80px;" /></td>
					<td><a id="getList" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" onclick="javascript:getList();" >查询</a></td>
				</tr>
			</table>
			<table id="list"></table>
	<div>
</body>
</html>