<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>干线订单详细</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<div>干线单号${param.code}订单详情</div>
<br>
<div><table id="datagrid"></table></div>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function($) {
	$('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/qryTrunkOrderDetail.mmx?mailingBatchId=${param.mailingBatchId}',
		width : 800,
		border: true,
		fitColumns : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    collapsible:true,
	    pageSize : 30,
	    pageList : [10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[]
	    ],
	    columns:[[
	        {field:'orderCode',title:'订单号',width:150,align:'center'},
	        {field:'totalPrice',title:'金额',width:150,align:'center'},  
	        {field:'stockAreaName',title:'发货仓',width:150,align:'center'}, 
	        {field:'address',title:'收件人地址',width:150,align:'center'},
	        {field:'deliverName',title:'目的地',width:150,align:'center'}
	    ]]
	});	
});

</script>
</body>