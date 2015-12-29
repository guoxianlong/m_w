<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	var stockArea='${param.stockArea}';
	var startYear='${param.startYear}';
	var startMonth='${param.startMonth}';
	var startTime='${param.startTime}';
	var endTime='${param.endTime}';
	var productLine='${param.productLine}';
	var chinaArea='${param.chinaArea}';
	var provinces='${param.provinces}';
	datagrid = $('#productLineDeliverDetail').datagrid({
	    url:'${pageContext.request.contextPath}/BIController/getProductLineDeliverDetail.mmx',
	    queryParams : {
			stockArea:stockArea,
			startYear:startYear,
			startMonth:startMonth,
			startTime:startTime,
			endTime:endTime,
			productLine:productLine,
			chinaArea:chinaArea,
			provinces:provinces
	    },
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    showFooter: true,
	    pagination : true,
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
	    columns:[[  
			{field:'pcode',title:'产品编号',width:60,align:'center'},
			{field:'pname',title:'产品名称',width:60,align:'center'},
			{field:'count',title:'发货量',width:60,align:'center'}
	    ] ],
	    onLoadSuccess : function(data) {
			try {
				if (data.footer[0].time != 'undefined' && data.footer[0].time !=null && data.footer[0].time != "") {
					$("#time").html("<font color='blue'>时间:"+data.footer[0].time+"</font>");
				} else {
					$("#time").html("");
				}
				if (data.footer[0].stockName != 'undefined' && data.footer[0].stockName !=null && data.footer[0].stockName != "") {
					$("#stockName").html("<font color='blue'>仓库:"+data.footer[0].stockName+"</font>");
				} else {
					$("#stockName").html("");
				}
				if (data.footer[0].productLine != 'undefined' && data.footer[0].productLine !=null && data.footer[0].productLine != "") {
					$("#productLine").html("<font color='blue'>产品线:"+data.footer[0].productLine+"</font>");
				} else {
					$("#productLine").html("");
				}
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		},
	}); 
});

function searchFun() {
	datagrid.datagrid("load", {
		stockArea:$("#tb input[id=stockArea]").combobox("getValue"),
		startYear:$("#tb input[id=startYear]").val(),
		endYear:$("#tb input[id=endYear]").val(),
		startMonth:$("#tb input[id=startMonth]").val(),
		endMonth:$("#tb input[id=endMonth]").val(),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		endTime:$("#tb input[id=endTime]").datebox("getValue")
	});
}

function getEod(){  
    var date=new Date();  
    var i_milliseconds=date.getTime();  
    i_milliseconds-=1000*60*60*24;  
    var t_date = new Date();  
    t_date.setTime(i_milliseconds);  
    var i_year = t_date.getFullYear();  
    var i_month = ("0"+(t_date.getMonth()+1)).slice(-2);  
    var i_day = ("0"+t_date.getDate()).slice(-2);  
    return i_year+"-"+i_month+"-"+i_day;  
}  
function exportFun() {
	var stockArea='${param.stockArea}';
	var startYear='${param.startYear}';
	var startMonth='${param.startMonth}';
	var startTime='${param.startTime}';
	var endTime='${param.endTime}';
	var productLine='${param.productLine}';
	var chinaArea='${param.chinaArea}';
	var provinces='${param.provinces}';
	var params="provinces=" + provinces 
		+"&productLine=" + productLine
		+ "&chinaArea=" + chinaArea 
		+ "&stockArea=" + stockArea 
		+ "&startYear=" + startYear 
		+ "&startMonth=" + startMonth 
		+ "&startTime=" + startTime 
		+ "&endTime=" + endTime;
	window.open("${pageContext.request.contextPath}/BIController/exportProductLineDeliverDetail.mmx?" + params,"_blank");
}
</script>
</head>
<body>
<div id="tb" style="height:auto">
<h4 id="time" ></h4>
<h4 id="stockName" ></h4>
<h4 id="productLine" ></h4>
<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">导出数据到Excel表</a>
</div>
<table id="productLineDeliverDetail"></table> 
</body>
</html>