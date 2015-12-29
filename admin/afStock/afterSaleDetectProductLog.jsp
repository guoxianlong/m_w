<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>处理单日志</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleLogDatagrid.mmx',
	    queryParams : {
	    	detectCode :  ${param.detectCode},
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
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    columns:[[  
	        {field:'createDatetime',title:'操作时间',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	        {field:'createUserName',title:'操作人',width:20,align:'center'},  
	        {field:'content',title:'内容',width:55,align:'center'},  
	    ]]
	}); 
});
</script>
</head>
<body>
	<table id="datagrid"></table> 
</body>
</html>