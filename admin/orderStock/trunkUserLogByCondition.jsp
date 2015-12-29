<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
 <title>用户更新日志</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;

$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/getTrunkUserLog.mmx',
		toolbar : '#toolbar',
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
	    columns:[[  
			{field:'addTime',title:'时间',width:30,align:'center',
				formatter:function(value,row,index){
                    return value; 
	        	}	
			},
			{field:'operationUserName',title:'操作人',width:30,align:'center'},
	        {field:'trunkName',title:'干线公司',width:30,align:'center'},
	        {field:'username',title:'用户名',width:30,align:'center'},
	        {field:'password',title:'密码',width:30,align:'center'},
	        {field:'phone',title:'电话号码',width:30,align:'center'}
	    ]]
	}); 
	
	$('#username').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getDeliverAdminUser.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:false
    });
	
	$('#trunkName').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
    });
});

function searchFun() {
	var trunkId = $('#trunkName').combobox('getValue');
	var username  = $('#username').combobox('getText');
	datagrid.datagrid('load', {
		TrunkId : trunkId,
		username : username,
	});
}

</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
		<table>
			<tr>
				<td>干线公司:</td>
				<td><input id='trunkName' name='trunkName'/>&nbsp;&nbsp;</td>
				<td>用户名:</td>
				<td><input id='username' name='username' type='text' maxlength="30" />&nbsp;&nbsp;</td>
				<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
			</tr>
		</table>
	</div>
	<table id="datagrid"></table> 
</body>
</html>