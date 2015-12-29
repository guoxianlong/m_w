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
	var datagrid;	
	var getListFun = function () {		 
		var name = $("#name").val();
		var number = $("#number").val();
		var department = -1;
		if($("#department").combobox != null){
			department = $("#department").combobox('getValue');	
		}		
		if(department == '')
			department = -1;
		
		var title = -1;
		if($("#title").combobox != null){
			title = $("#title").combobox('getValue');	
		}
		if(title == '')
			title = -1;
		var startDate = $("#startDate").datebox('getValue');
		var endDate = $("#endDate").datebox('getValue'); 		
		if (startDate != '' && endDate == '') {
			if(!validateDate(endDate,startDate)){
				alert('开始日期必须小于结束日期');
				$('#startDate').focus();
				return;
			}
 		}
		var status = '';
		$("input[name='status']:checked").each(function(){
			if (status.length > 0)
				status += ',';
			status += $(this).attr('value');
		});
		
		datagrid = $("#list").datagrid({
			url:'${pageContext.request.contextPath}/BIStoreController/getBISmsNumberList.mmx',
			title:"定时短信号码列表",
		    queryParams : {
				name : name,
				number : number,
				department : department,
				title : title,
				startDate : startDate,
				endDate : endDate,
				statusList : status
		    },
		    toolbar : '#tb',
		    idField : 'id',
		    fit : true,
		    fitColumns : true,
		    striped : true,
		    nowrap : false,
		    loadMsg : '正在努力为您加载..',
		    pagination : true,
		    rownumbers : true,
		    singleSelect : true,
		    checkOnSelect : false,
			selectOnCheck : false,
		    pageSize : 20,
		    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],	
		    frozenColumns : [[
		  	         		{field:'checkId',checkbox:true,width:40,align:'center',value:"id"},
		  					{field:'id',title:'ID',width:20,hidden:true}
		  	    ]],
			columns:[[
			        {field:'name',title:'姓名',align:'center', width:7},
			        {field:'number',title:'手机号',align:'center', width:14},
			        {field:'departmentName',title:'部门',align:'center', width:7},
			        {field:'titleName',title:'职称',align:'center', width:7},
			        {field:'createTime',title:'添加时间',align:'center', width:11,
			        	formatter: function(value, row, index){
			        		if(value == null || value.length < 19)
			        			return '';
			        		return value.substring(0, 19);
			        	}},
			        {field:'createUsername',title:'添加人',align:'center', width:7},
			        {field:'statusName',title:'状态',align:'center', width:7},
			        {field:'checkTime',title:'审核时间',align:'center', width:11,
			        	formatter: function(value, row, index){
			        		if(value == null || value.length < 19)
			        			return '';
			        		return value.substring(0, 19);
			        	}},
			        {field:'checkUsername',title:'审核人',align:'center', width:7},
			        
			        {field:'status',title:'操作状态',align:'center', width:22,
			        	formatter : function(value,row,index){
			        		var result = '';			        		
			        		if (value == 0) {
			        			<% if (group.isFlag(2146)) { %>			        		
			        				result += '<a href="javascript:void(0);" onclick="updateCount('+index+')">修改</a>';
			        			<% } %>
			        		}
			        		<% if (group.isFlag(2147)) { %>
			        			result += '&nbsp;&nbsp;<a href="javascript:void(0);" onclick="deleteCount('+index+')">删除</a>';
			        		<% } %>
			        		if (value == 0) {
			        			<% if (group.isFlag(2149)) { %>
			        				result += '&nbsp;&nbsp;<a href="javascript:void(0);" onclick="checkCount('+index+')">审核通过</a>';
			        			<% } %>	
			        			<% if (group.isFlag(2150)) { %>
			        				result += '&nbsp;&nbsp;<a href="javascript:void(0);" onclick="unCheckCount('+index+')">审核未通过</a>';
			        			<% } %>	
			        		}			        		
			        		return result; 
			        	}
			        }
			]]
		});
	};

	function addBean(){
		var id = $("#id").val();
		var name = $("#name").val();
		var number = $("#number").val();
		var department = $("#department").combobox('getValue');
		var title = $("#title").combobox('getValue');		
		if (name == '') {
			$.messager.show({
				msg : '请输入姓名',
				title : '提示'
			});
			return;
		}		
		if (number == '') {
			$.messager.show({
				msg : '请输入手机号',
				title : '提示'
			});
			return;
		}		
		if (!/^1\d{10}$/.test(number)) {
			$.messager.show({
				msg : '请输入正确的手机号',
				title : '提示'
			});
			return;
		}		
		if (department == -1) {
			$.messager.show({
				msg : '请选择部门',
				title : '提示'
			});
			return;
		}		
		if (title == -1) {
			$.messager.show({
				msg : '请选择职称',
				title : '提示'
			});
			return;
		}
		
		$.ajax({
			type : "post",
			url : '${pageContext.request.contextPath}/BIStoreController/saveBISmsNumberBean.mmx',
			dataType : "json",
			cache : false,
			data : {
				id : id,
				name : name,
				number : number,
				department : department,
				title : title				
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
						resetForm();
						$("#list").datagrid('reload');
					}
					$.messager.show({
						msg : json.msg,
						title : '提示'
					});
				}
			}
		});		
	}
	
	function resetForm() {
		$("#id").val(0);
		$("#name").val('');
		$("#number").val('');
		$("#department").combobox('setValue', -1);
		$("#title").combobox('setValue', -1);	
	}
 
	
	function ajaxOper(id, eMsg, harfUrl, status){
		$.ajax({
			type : "post",
			url : "<%=request.getContextPath()%>/BIStoreController/" + harfUrl + "",
			dataType : "json",
			cache : false,
			data : {
				id : id,
				status : status
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
						$("#list").datagrid('reload');
					}
					$.messager.show({
						msg : json.msg,
						title : '提示'
					});
				}
			}
		});
	}
	// 审核通过
	function checkCount(index) {
		if (index == null)
			return;		
		$('#list').datagrid('selectRow', index);
		var row = $('#list').datagrid('getSelected');
		if(row == null)
			return;
		ajaxOper(row.id, '审核失败', 'checkBISmsNumberBean.mmx', 1);
	}
	// 审核未通过
	function unCheckCount(index) {
		if (index == null)
			return;		
		$('#list').datagrid('selectRow', index);
		var row = $('#list').datagrid('getSelected');
		if(row == null)
			return;
		ajaxOper(row.id, '审核失败', 'checkBISmsNumberBean.mmx', 2);
	}	
	
	function updateCount(index) {
		if (index == null)
			return;		
		$('#list').datagrid('selectRow', index);
		var row = $('#list').datagrid('getSelected');
		if (row == null)
			return;
		$("#id").val(row.id);		
		$("#name").val(row.name);
		$("#number").val(row.number);		
		$("#department").combobox('setValue', row.department);
		$("#title").combobox('setValue', row.title);
	}

	function deleteCount(index) {
		if (index == null)
			return;		
		$('#list').datagrid('selectRow', index);
		var row = $('#list').datagrid('getSelected');
		if(row == null)
			return;
		$.messager.confirm('删除确认','您是否要删除该号码?',function(r){
		    if (r){
		    	ajaxOper(row.id, '删除失败', 'deleteBISmsNumberBean.mmx', -1);
		    }
		});		
	}
	
	function checkAll() {
		var ids = getIds();
		if (ids.length == 0) {
			$.messager.show({
				msg : '请选择您要操作的电话号码',
				title : '提示'
			});
			return;
		}
		$.messager.confirm('审核确认','您是否要[审核通过]选中的电话号码?',function(r){
		    if (r){
		    	ajaxOper(ids, '审核失败', 'checkBISmsNumberBean.mmx', 1);
		    }
		});			
	}
	
	function unCheckAll() {
		var ids = getIds();
		if (ids.length == 0) {
			$.messager.show({
				msg : '请选择您要操作的电话号码',
				title : '提示'
			});
			return;
		}
		$.messager.confirm('审核确认','您是否要[审核不通过]选中的电话号码?',function(r){
		    if (r){
		    	ajaxOper(ids, '审核失败', 'checkBISmsNumberBean.mmx', 2);
		    }
		});	
	}
	
	function deleteAll() {
		var ids = getIds();
		if (ids.length == 0) {
			$.messager.show({
				msg : '请选择您要操作的电话号码',
				title : '提示'
			});
			return;
		}
		$.messager.confirm('删除确认','您是否要[删除]选中的电话号码?',function(r){
		    if (r){
		    	ajaxOper(ids, '删除失败', 'deleteBISmsNumberBean.mmx', -1);
		    }
		});		
	}
	
	function getIds() {
		var selectrows = $("#list").datagrid("getChecked");
		var selectlength = selectrows.length;		
		var ids ="";
		for (var i = 0 ; i < selectlength ; i ++) {
			if(ids.length > 0)
				ids += ',';
			ids += selectrows[i].id;
		}
		return ids;
	}
	
	$(function(){
		$('#department').combobox({
		    url:'${pageContext.request.contextPath}/BIStoreController/getDepartList.mmx',
		    valueField:'id',
		    textField:'text',
		    editable:false
		});
		$('#title').combobox({
		    url:'${pageContext.request.contextPath}/BIStoreController/getETitleType.mmx',
		    valueField:'id',
		    textField:'text',
		    editable:false
		});
		getListFun();
	});	
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>定时短信发送人员信息管理</title>
</head>
<body>
	<div id="tb" style="height: auto;display: none;">
		<fieldset>			
			<form id="addForm" method="post">
				<table> 
					<tr>
						<th>姓&nbsp;&nbsp;&nbsp;&nbsp;名</th><td><input type="text" id="name" style="width: 120px;" data-options="required:true" maxlength=10/></td>
					</tr>
					<tr>
						<th>手&nbsp;机&nbsp;号</th><td><input type="text" id="number" style="width: 120px;" data-options="required:true" maxlength=11/></td>
					</tr>
					<tr>
						<th>所属部门</th><td><input type="text" id="department" style="width: 126px;" data-options="required:true" /></td>
					</tr>
					<tr>
						<th>职&nbsp;&nbsp;&nbsp;&nbsp;称</th><td><input type="text" id="title" style="width: 126px;" data-options="required:true"/></td>
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr>
						<th>开始日期</th><td><input class="easyui-datebox" editable="false" id="startDate">&nbsp;&nbsp;说明：开始日期 (查询添加的时间范围的开始日期)，添加修改数据无需选择</td>
					</tr>
					<tr>
						<th>结束日期</th><td><input class="easyui-datebox" editable="false" id="endDate">&nbsp;&nbsp;说明：结束日期 (查询添加的时间范围的结束日期)，添加修改数据无需选择</td>
					</tr>
					<tr>
					<th></th>
						<td>
						待审核<input type="checkbox" name="status" value="0" />
						&nbsp;&nbsp;审核通过<input type="checkbox" name="status" value="1" />
						&nbsp;&nbsp;待审未通过<input type="checkbox" name="status" value="2" />&nbsp;&nbsp;说明：状态(查询的数据状态)，添加修改数据无需选择 
						</td>
					</tr>
					<tr><td>&nbsp;<input id="id" type="hidden" value='0' /></td></tr>
					<tr>					
					<th></th>
						<td><a onclick="javascript:getListFun();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" href="javascript:void(0);" >查询</a>
						&nbsp;&nbsp;&nbsp;&nbsp;<a id="saveButton" onclick="javascript:addBean();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" href="javascript:void(0);" >保存</a>
						<% if (group.isFlag(2148)) { %>
							&nbsp;&nbsp;&nbsp;&nbsp;<a onclick="javascript:resetForm();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-back'" href="javascript:void(0);" >重置</a></td>
						<% } %>					
					</tr>
				</table>
			</form>
		</fieldset>
		<% if (group.isFlag(2149)) { %>
			<a onclick="javascript:checkAll();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-ok'" href="javascript:void(0);" >批量审核通过</a>
		<% } %>
		<% if (group.isFlag(2150)) { %>
			&nbsp;&nbsp;&nbsp;&nbsp;<a onclick="javascript:unCheckAll();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cancel'" href="javascript:void(0);" >批量审核未通过</a>
		<% } %>	
		<% if (group.isFlag(2147)) { %>
			&nbsp;&nbsp;&nbsp;&nbsp;<a onclick="javascript:deleteAll();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cancel'" href="javascript:void(0);" >批量删除</a>
		<% } %>
	</div>
	
	<table id="list"></table>
</body>
</html>