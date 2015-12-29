<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<title>备用机列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var operateItemId = ${param.operateItemId};
var type = ${param.type};
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/spareManagerController/getSpareList.mmx?operateItemId='+ operateItemId +"&type="+type,
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    frozenColumns : [[
					{field:'id',hidden:true}
	    ]],
	    columns:[[  
	    	{field:'code',title:'备用机号',width:50,align:'center'},
	    	{field:'imei',title:'IMEI码',width:50,align:'center'},
	    	{field:'cargoWholeCode',title:'货位号',width:50,align:'center'}
	    ]]
	}); 
});
</script>
</head>
<body>
	<table id="datagrid"></table> 
</body>
</html>