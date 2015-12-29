<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
function exportFun() {
	var params="date=" + '${param.date}'
		+ "&stockArea=" + ${param.stockArea} 
		+ "&type=" + ${param.type};
	window.open("${pageContext.request.contextPath}/BIController/exportSpiltOrderInfoDetail.mmx?" + params,"_blank");
}
var datagrid;
$(function(){
	datagrid = $('#spiltOrderInfoDetailDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/BIController/getSpiltOrderInfoDetail.mmx',
	    queryParams : {
	    	stockArea:${param.stockArea},
	    	date:'${param.date}',
	    	type:${param.type},
	    },
	    fitColumns : true,
	    border : true,
	    rownumbers : true,
	    singleSelect : true,
	    striped : true,
	    idField : 'id',
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    toolbar : '#tb',
	    pagination : true,
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
	    columns:[[
			{field:'ordercode',title:'订单号',width:60,align:'center'},
			{field:'stockname',title:'发货仓量',width:60,align:'center'},
			{field:'productcode',title:'产品编号',width:60,align:'center'},
			{field:'productname',title:'产品名称',width:60,align:'center'},
			{field:'pcount',title:'产品数量',width:60,align:'center'},
			{field:'plname',title:'产品线',width:60,align:'center'}
	    ] ],
	    onLoadSuccess : function(data) {
			try {
				if (data.footer[0].date != 'undefined' && data.footer[0].date !=null && data.footer[0].date != "") {
					$("#date").html("<font color='blue'>时间:"+data.footer[0].date+"</font>");
				} else {
					$("#date").html("");
				}
				if (data.footer[0].stockName != 'undefined' && data.footer[0].stockName !=null && data.footer[0].stockName != "") {
					$("#stockName").html("<font color='blue'>仓库:"+data.footer[0].stockName+"</font>");
				} else {
					$("#stockName").html("");
				}
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		},
	})}); 
</script>
</head>
<body>
<div id="tb">
<h4 id="date" ></h4>
<h4 id="stockName" ></h4>
<h4 >拆单量统计/越仓量发货统计</h4>
</div>
<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">导出数据到Excel表</a>
	<table id="spiltOrderInfoDetailDataGrid"></table> 
</body>
</html>