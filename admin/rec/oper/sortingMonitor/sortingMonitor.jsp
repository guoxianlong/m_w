<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<!DOCTYPE html>
<html>
<head>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/dark-blue.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="http://www.jeasyui.com/easyui/datagrid-detailview.js"></script>
<%
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<script type="text/javascript" charset="UTF-8">
$(function(){
	$('#gcDatagrid').datagrid({
		url : '${pageContext.request.contextPath}/SortingMonitorController/getSortingCountDatagrid.mmx',
		toolbar : "#tb",
		nowrap　: false,
		border : false,
		idField : "id",
		rownumbers : true,
		singleSelect : false,
		fit : true,
		fitColumns : false,
		view: detailview,    
	    detailFormatter:function(index,row){    
	        return '<div style="padding:1px"><table id="ddv-' + index + '"></table></div>';    
	    },    
	    onExpandRow: function(index,row){    
	        $('#ddv-'+index).datagrid({    
	            url:'${pageContext.request.contextPath}/SortingMonitorController/getSortingCountChildDatagrid.mmx?index='+row.batchNumber,    
	            fitColumns : true,
	            singleSelect:true,    
	            rownumbers:true, 
	            showFooter : true,
	            loadMsg:'',    
	            height:'auto',
	            idField : "id",
	            columns:[[    
	                {field:'staffName',title:'姓名',width:70,align : 'center'},    
	                {field:'staffCode',title:'员工号',width:80,align : 'center'},    
	                {field:'overTimeOrderCount',title:'分拣超时订单',width:50,align : 'center',
	                	formatter : function(value, rowData, rowIndex) {
							if(value != '0'){
								if(rowData.staffCode != '-'){
									return "<a href=\"${pageContext.request.contextPath}/admin/sortingAction.do?method=sortingOvertimeOrderList&staffCode="+rowData.staffCode +"\" target=\"_blank\"><font color=\"red\">"+ value + "</font></a>";
								}else{
									return "<font color=\'red\'>"+ value + "</font>";
								}
							}else{
								return "<font color=\'red\'>"+ value + "</font>";
							}
						}},    
	                {field:'begindatetime',title:'作业开始时间',width:100,align : 'center',
	                	formatter : function(value, rowData, rowIndex) {
	                		if(value != '-'){
	                			return value.substring(10,16);
	                		}else{
	                			return value;
	                		}
						}} ,   
	                {field:'finallReceiveOrderTime',title:'最后领单时间',width:100,align : 'center',
	                	formatter : function(value, rowData, rowIndex) {
	                		if(value != '-'){
	                			return value.substring(10,16);
	                		}else{
	                			return value;
	                		}
						}},    
	                {field:'groupCount',title:'完成波次',width:50,align : 'center'},    
	                {field:'completeOrderCount',title:'完成订单',width:50,align : 'center'},    
	                {field:'noCompleteOrderCount',title:'未完成订单',width:50,align : 'center',
	                	formatter : function(value, rowData, rowIndex) {
	                		return "<font color=\'red\'>"+ value + "</font>";
	                	}},    
	                {field:'skuCount',title:'完成SKU',width:50,align : 'center'},    
	                {field:'productCount',title:'完成商品',width:50,align : 'center'},    
	                {field:'passageCount',title:'巷道数',width:50,align : 'center'},    
	            ]],    
	            onResize:function(){    
	                $('#gcDatagrid').datagrid('fixDetailRowHeight',index);    
	            },    
	            onLoadSuccess:function(){    
	                setTimeout(function(){    
	                    $('#gcDatagrid').datagrid('fixDetailRowHeight',index);    
	                },0);    
	            }    
	        });    
	        $('#gcDatagrid').datagrid('fixDetailRowHeight',index);    
	    },
		onLoadSuccess : function(data){
			$('#staffCount').append(data.footer[0].staffCount);
			$('#overTimeOrderCount').append(data.footer[0].overTimeOrderCount);
			$('#groupCount').append(data.footer[0].groupCount);
			$('#completeOrderCount').append(data.footer[0].completeOrderCount);
			$('#noCompleteOrderCount').append(data.footer[0].noCompleteOrderCount);
			$('#skuCount').append(data.footer[0].skuCount);
			$('#productCount').append(data.footer[0].productCount);
			$('#passageCount').append(data.footer[0].passageCount);
			$.ajax({
				url : '${pageContext.request.contextPath}/SortingMonitorController/getHighchartData.mmx',
				type : 'post',
				dataType : 'json',
				cache : 'fasle',
				success : function(data){
					var chart;
					for(var i=0;i<data.indexs.length;i++){
						$('#hc_p').append("<a href=\"javascript:showPackage(" + i + ");\">" + data.titles[i] + "</a><div id=\""+ i + "\" style=\"overflow-x:hidden;\"></div>");
						chart = new Highcharts.Chart({
							chart: {
								renderTo: data.indexs[i] + '',
								defaultSeriesType: 'column'
							},
							title: {
								text: ''
							},
							xAxis: {
								categories: data.staffNames[i]
							},
							yAxis: {
								allowDecimals: false,
								min: 0,
								title: {
									text: '作业量'
								}
							},
							tooltip: {
								formatter: function() {
									return '<b>'+ this.x +'</b><br/>'+
										 this.series.name +': '+ this.y +'<br/>'+
										 'Total: '+ this.point.stackTotal;
								}
							},
							plotOptions: {
								series: {
									cursor: 'pointer',
									point: {
										events: {
											click: function() {
												if(this.series.name == '超时订单'){
													location.href='${pageContext.request.contextPath}/admin/sortingAction.do?method=sortingOvertimeOrderList&staffCode=' + data.staffCodes[i];
												}
											}		
										}
									}
								},
								column: {
									stacking: 'normal',
								    pointWidth:25
								}
							},
						    series: [{
								name: '完成订单',
								data: data.completeOrderCounts[i],
								stack: 'male'
								},{
								name: '作业中订单',
								data: data.noCompleteOrderCounts[i],
								stack: 'male'
								},{
								name: '超时订单',
								data: data.overTimeOrderCounts[i],
								stack: 'male'
							}],
							  dataLabels: {
					              enabled: true,
					              rotation: -90,
					              color: '#FFFFFF',
					              align: 'right',
					              x: -3,
					              y: 10,
					              formatter: function() {
					                  return this.y;
					              },
					              style: {
					                  fontSize: '13px',
					                  fontFamily: 'Verdana, sans-serif'
					              }
							  }
						});
					}
				}
			});
		},
		columns:[[  
		           {field:'batchNumber',title:'班次',width:510,align : 'center'},  
		           {field:'sortingTimes',title:'领单时间',width:720,align : 'center'},  
		       ]]
	});
	$.ajax({
		url : '${pageContext.request.contextPath}/SortingMonitorController/getSortingCountInfo.mmx',
		dataType : 'json',
		type : 'post',
		cache : 'false',
		success : function(data){
			$('#completeGroupCount').append(data.completeGroupCount);
			$('#complOrderCount').append(data.completeOrderCount);
			$('#noReceiveGroupCount').append(data.noReceiveGroupCount);
			$('#noReceiveOrderCount').append(data.noReceiveOrderCount);
			$('#overOrderCount').append("<a href=\"${pageContext.request.contextPath}/admin/sortingAction.do?method=sortingOvertimeOrderList\" target=\"_blank\"><font color=\"red\">"+data.overTimeOrderCount+ "</font></a>");
		}
	});
});
function sortingMonitorExcel(){
	window.open("${pageContext.request.contextPath}/SortingMonitorController/sortingMonitorExcel.mmx","_blank");
}
function showPackage(id){
	$("#"+id).slideToggle();
}
</script>
</head>
<body>
<div id="data_p" class="easyui-panel" title="分拣监控数据" style="overflow-y:auto;height:590px;padding:10px;background:#fafafa;"  
        iconCls="icon-sum"  closable="false" collapsible="true" minimizable="false" maximizable=true>  
    <div id="tb" style="padding:3px;height: auto;">
		<fieldset>
			<legend>信息&总计</legend>
			已完成: <label id="completeGroupCount" style="color: red"></label> 波次
			(<label id="complOrderCount" style="color: red"></label>单)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			未领取: <label id="noReceiveGroupCount" style="color: red"></label> 波次
			(<label id="noReceiveOrderCount" style="color: red"></label>单) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			分拣超时: <label id="overOrderCount" style="color: red"></label> 单<br><br>
			人数: <label id="staffCount" style="color: red"></label> 人 &nbsp;
			分拣超时订单: <label id="overTimeOrderCount" style="color: red"></label>&nbsp;
			 完成波次: <label id="groupCount" style="color: red"></label>&nbsp;
			完成订单: <label id="completeOrderCount" style="color: red"></label>&nbsp;
			未完成订单: <label id="noCompleteOrderCount" style="color: red"></label>&nbsp;
			完成SKU: <label id="skuCount" style="color: red"></label>&nbsp;  
			完成商品: <label id="productCount" style="color: red"></label>&nbsp;
			巷道数: <label id="passageCount" style="color: red"></label>&nbsp;
		</fieldset>
    	<a href="#" class="easyui-linkbutton" iconCls="icon-redo" plain="true" onclick="sortingMonitorExcel()">监控数据导出</a>
    	    
	</div>
	<div id="gcDatagrid"></div>
</div>
<div id="hc_p" class="easyui-panel" title="分拣监控图表" style="overflow-y:auto; overflow-x:hidden; height:590px;padding:10px;background:#fafafa;"  
        iconCls="icon-sum"  closable="false" collapsible="true" minimizable="false" maximizable=true>  
</div>  
</body>
</html>