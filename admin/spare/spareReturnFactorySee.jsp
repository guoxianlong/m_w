<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>返还厂商备用机包裹查看</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#dd').datagrid({
	    url:'${pageContext.request.contextPath}/spareManagerController/see.mmx',
	    queryParams:{id:${param.id}},
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : false,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50 ],
	    frozenColumns : [[
	  					{field:'id',width:20,hidden:true}
	  	]],
	    columns:[[ 
				{field:'code',title:'备用机单号',width:60,align:'center'},
				{field:'imei',title:'IMEI码',width:80,align:'center'},
				{field:'productOriname',title:'商品原名称',width:40,align:'center'}
	    ] ]
	});
});

</script>
</head>
<body>
	<table id="dd"></table> 
</body>
</html>