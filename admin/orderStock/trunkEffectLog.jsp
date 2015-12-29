<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
 <title>查看干线时效日志</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;

$(function(){
	var trunkId =${param.trunkId};
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/getTrunkEffectLog.mmx',
		toolbar : '#toolbar',
		queryParams: {
			TrunkId: trunkId
		},
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
	        {field:'deliverAdminName',title:'用户名',width:30,align:'center'},
	        {field:'stockAreaName',title:'发货仓',width:30,align:'center'},
	        {field:'deliverName',title:'目的地',width:30,align:'center'},
	        {field:'mode',title:'配送方式',width:30,align:'center',
	        	formatter:function(value,row,index){
	        		if(row.mode=='1'){
	        			return '公路'; 
	        		}
	        		if(row.mode=='2'){
	        			return '铁路'; 
	        		}
	        		if(row.mode=='3'){
	        			return '空运'; 
	        		}
	        	}
	        },
	        {field:'time',title:'时效H',width:30,align:'center'}
	    ]]
	});
});


</script>
</head>
<body>
	<table id="datagrid"></table> 
</body>
</html>