<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var date;
//var status = ${param.status};
$(function(){
	$('#deliverId').combobox({
		url : '${pageContext.request.contextPath}/OrderStockController/getDeliverComboBox.mmx',
		valueField : 'id',
		textField : 'name',
		editable : false,
		panelHeight : 'auto'
	}
	);
	$('#deliver').combobox({
		url : '${pageContext.request.contextPath}/OrderStockController/getDeliverComboBox.mmx',
		valueField : 'id',
		textField : 'name',
		editable : false,
		panelHeight : 'auto'
	}
	);
	$('#deliverUpData').combobox({
		url : '${pageContext.request.contextPath}/OrderStockController/getDeliverComboBox.mmx',
		valueField : 'id',
		textField : 'name',
		editable : false,
		panelHeight : 'auto'
	}
	);
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TPcontroller/getdAdminUserInfo.mmx',
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
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
	    ]],
	    columns:[[  
	        {field:'name',title:'快递公司',width:50,align:'center'},
	        {field:'username',title:'用户名',width:50,align:'center'},  
	        {field:'phone',title:'手机号',width:40,align:'center'}, 
	        {field:'pv_limit',title:'日访问量限制',width:40,align:'center'}, 
	        {field:'ave',title:'日均访问次数',width:40,align:'center',
	        	formatter : function(value, row, index) {
					if(row.pv_limit==null){
						return '0';
					}else{
						return row.ave;
					}
				}		
	        }, 
	        {field:'all_search_count',title:'累计访问次数',width:40,align:'center'}, 
	        {field:'createDatetime',title:'添加时间',width:50,align:'center'}, 
	        {field:'lastModifyDate',title:'最后修改时间',width:50,align:'center'},
	        {
				field : 'action',
				title : '操作',
				align :'center',
				width : 15,
				formatter : function(value, row, index) {
					if(row.status==0){
						return '<a href="javascript:void(0);" class="editbutton" onclick="changStatus('+row.id+','+row.status+')">启用</a>';
					}else{
						return '<a href="javascript:void(0);" class="editbutton" onclick="changStatus('+row.id+','+row.status+')">停用</a>';
					}
				}
			},{
				field : 'modif',
				title : '修改',
				align :'center',
				width : 15,
				formatter : function(value, row, index) {
						return '<a href="javascript:void(0);" class="editbutton" onclick="modifyUser(\''+row.id+'\',\''+row.deliverId+'\',\''+row.username+'\',\''+row.pv_limit+'\',\''+row.phone+'\')">修改</a>';
				}
			},{
				field : 'query',
				title : '查看访问',
				align :'center',
				width : 30,
				formatter : function(value, row, index) {
						return '<a href="javascript:void(0);" class="editbutton" onclick="queryUser('+row.id+')">查看访问</a>';
				}
			}
	    ]]
	}); 
});
function changStatus(id,status){
	$.ajax({
		url : '${pageContext.request.contextPath}/TPcontroller/changeUserStatus.mmx',
		data:{
			id:id,
			status:status
		},
		dataType : 'json',
		success : function(data){
			//console.info(data.msg);//页面控制台输出
			if(data.success){
				$.messager.show({
					msg : data.msg,
					title : '提示'
				});
				datagrid.datagrid('reload');//重置表格数据
			}else{
				$.messager.show({
					msg : data.msg,
					title : '提示'
				});
			}
		}
	});
}
function searchFun() {
	datagrid.datagrid('load', {
		deliver : $('#deliver').combobox('getValue'),
		userCode : $('#toolbar input[name=userCode]').val(),
		phone : $('#toolbar input[name=phone]').val(),
		startDate : $('#startDate').datebox("getValue"),
		endDate : $('#endDate').datebox("getValue")
	});
}
function addUserInfo(){
    $('#addUserInfo').panel('open');
}
function addAdminUser(){
	var username =$("#username").val();
	var pwd =$("#pwd").val();
	var deliverId = $('#deliverId').combobox('getValue');
	var pvLimit =$("#pvLimit").val();
	var userphone =$("#userphone").val();
	var status =document.getElementById("status");
	var st;
	if(status.checked){
		st=1;
	}else{
		st=0;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/TPcontroller/addAdminUser.mmx',
		data:{
			username:username,
			pwd:pwd,
			deliverId:deliverId,
			pvLimit:pvLimit,
			status:st,
			userphone:userphone
		},
		dataType : 'json',
		success : function(data){
			//console.info(data.msg);//页面控制台输出
			if(data.success){
				$.messager.show({
					msg : data.msg,
					title : '提示'
				});
				$('#addUserInfo').window('close');//关闭添加框
				datagrid.datagrid('reload');//重置表格数据
				$('#addProductFrom').form('clear');//清空表单数据
			}else{
				$.messager.show({
					msg : data.msg,
					title : '提示'
				});
			}
		}
	});
}
function modifyUser(id,deliverId,username,limit,phone){
	 $('#modifyUserInfo').panel('open');
	 $('#userId').val(id);
	 $('#usernameUpdata').val(username);//改变文本框的值
	 $('#pvLimitUpData').val(limit);//改变文本框的值
	 $('#userphoneUpData').val(phone);//改变文本框的值
	 $('#deliverUpData').combobox("setValue",deliverId);//改变下拉框的值
}
function queryUser(id){
    window.open("userMonitoring.jsp?userId="+id);   
}
function confirmModify(){
	 $.ajax({
			url : '${pageContext.request.contextPath}/TPcontroller/updataUserInfo.mmx',
			data:{
				id:$('#userId').val(),
				pvLimit: $('#pvLimitUpData').val(),
				phone: $('#userphoneUpData').val(),
				deliver:$('#deliverUpData').combobox('getValue')
			},
			dataType : 'json',
			success : function(data){
				//console.info(data.msg);//页面控制台输出
				if(data.success){
					$.messager.show({
						msg : data.msg,
						title : '提示'
					});
					$('#modifyUserInfo').window('close');//关闭添加框
					$('#modifyUserFrom').form('clear');//清空表单数据
					datagrid.datagrid('reload');//重置表格数据
				}else{
					$.messager.show({
						msg : data.msg,
						title : '提示'
					});
				}
			}
		});
}
</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
		<fieldset>
			<legend>筛选</legend>
			<table>
				<tr>
					<th>快递公司:</th>
					<td>
						<input name='deliver' id='deliver' style="width:100px"/>
					</td>
					
					<th>账号</th>
					<td>
						<input id="userCode" name="userCode" style="width: 100px;"/>
					</td>
					
					<th>手机号</th>
					<td>
						<input id="phone" name="phone" style="width: 100px;"/>
					</td>
					<th>添加日期：</th>
					<td>
						<input id="startDate" name="startDate"   class="easyui-datebox" editable="false" style="width: 120px;" />
						到
						<input id="endDate" name="endDate"   class="easyui-datebox" editable="false" style="width: 120px;" />
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						<mmb:permit value="3004">
							<a class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addUserInfo();" href="javascript:void(0);">添加用户</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<div id="addUserInfo" class="easyui-window" title="添加新用户" style="width:320px;height:250px"   
        data-options="iconCls:'icon-save',modal:true,closed:true"> 
	<form id='addProductFrom' method="post"> 
					<table class="form">
						<tr>
							<th></th>
					        <td>&nbsp;
					        </td>
						</tr>
						<tr>
							<th></th>
					        <td>&nbsp;
					        </td>
						</tr>
						<tr>
							<th>用户名:</th>
					        <td>
								<input type=text name="username"  id="username" size="20"  />
					        </td>
						</tr>
						<tr>
							<th>密码:</th>
					        <td>
								<input type='password' name="pwd"  id="pwd" size="20"  />
					        </td>
						</tr>
						<tr>
							<th>电话号码:</th>
					        <td>
								<input type=text name="userphone"  id="userphone" size="20"  />
					        </td>
						</tr>
						<tr>
							<th>快递公司:</th>
					        <td>
								<input name='deliverId' id='deliverId' style="width:152px"/>
					        </td>
						</tr>
						<tr>
							<th>日访问量限制:</th>
					        <td>
								<input type=text class="easyui-numberbox" data-options="min:0,precision:0" name="pvLimit" id="pvLimit" size="20"  />&nbsp;次/天
					        </td>
						</tr>
						<tr>
							<th></th>
					        <td>
								<input type="checkbox" value="1" name="status" id="status" size="20"  />&nbsp;启用
					        </td>
						</tr>
					</table>
					<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="addAdminUser();">确认添加</a>
	</form>
	</div>
	<div id="modifyUserInfo" class="easyui-window" title="修改用户" style="width:320px;height:250px" data-options="iconCls:'icon-save',modal:true,closed:true"> 
	<form id='modifyUserFrom' method="post"> 
					<table class="form">
						<tr>
							<th></th>
					        <td>&nbsp;
					        </td>
						</tr>
						<tr>
							<th></th>
					        <td>&nbsp;
					        	<input type=text name="userId"  id="userId" size="20" hidden="hidden"  />
					        </td>
						</tr>
						<tr>
							<th>用户名:</th>
					        <td>
								<input type=text name="usernameUpdata"  id="usernameUpdata" size="20" disabled="disabled" />
					        </td>
						</tr>
						<tr>
							<th>电话号码:</th>
					        <td>
								<input type=text name="userphoneUpData"  id="userphoneUpData" size="20"  />
					        </td>
						</tr>
						<tr>
							<th>快递公司:</th>
					        <td>
								<input name='deliverUpData' id='deliverUpData' style="width:152px"/>
					        </td>
						</tr>
						<tr>
							<th>日访问量限制:</th>
					        <td>
								<input type=text class="easyui-numberbox" data-options="min:0,precision:0" name="pvLimitUpData" id="pvLimitUpData" size="20"  />&nbsp;次/天
					        </td>
						</tr>
					</table>
					<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="confirmModify();">确认修改</a>
	</form>
	</div>
	<table id="datagrid"></table> 
</body>
</html>