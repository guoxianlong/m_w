<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<script type="text/javascript">

function getList() {	
	var areaId = $("#areaId2").combobox('getValue');
	if (areaId == -1) {
		alert("请选择仓库");
		$('#areaId').focus();
		return;
	}
	var operType = $("#operType2").combobox('getValue');
	if (operType == -1) {
		alert("请选择作业环节");
		$('#areaId').focus();
		return;
	}
	var startDate = $("#startDate").datebox('getValue');
	var endDate = $("#endDate").datebox('getValue');
	if (startDate == '' || endDate == '') {
		alert('请选择日期');
		return;
	}
	if(!validateSubDate(endDate, startDate)){		
		$('#startDate').focus();
		return;
	}
	
	function ajaxFun(title, columns) {
		$("#list").datagrid({
			title: "" + title,
			idField : 'id',
			iconCls:'icon-ok',
			fitColumns:true,
			width:880,
			pageNumber:1,
			pageSize:31,
			pageList:[31],
			url:'${pageContext.request.contextPath}/BIStoreController/getOperTypeTableList.mmx',
			queryParams:{
				startDate: '' + startDate,
				endDate: '' + endDate,
				areaId: '' + areaId,
				operType: '' + operType
			},
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			columns: columns
		});
	}
	
	function getGridColumns() {
		var col = null;
		// 0 采购入库\r\n1 上架\r\n2 退货入库\r\n
		// 3 订单分拣\r\n4 订单分播\r\n5 订单复核\r\n6 订单交接\r\n7 异常处理
		if (operType == 0) {
			col = [[
			        {field:'datetime',title:'日期',align:'center',
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
			        {field:'operCount',title:'采购入库作业量',align:'center'},
			        {field:'count1',title:'调拨入库量',align:'center'},
			        {field:'count2',title:'合格品入库量',align:'center'},
			        {field:'count3',title:'不合格品量',align:'center'},
			        {field:'onGuradTimeLength',title:'当日在岗总时长',align:'center'},
			        {field:'onGuradCount',title:'实际在岗人数',align:'center'},
			        {field:'onGuradPerCount',title:'日在岗人均产能',align:'center'},
			        {field:'standardCapacity',title:'标准人均产能',align:'center'}			        
			 ]];
		} else if (operType == 1) {
			col = [[
			        {field:'datetime',title:'日期',align:'center',
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
			        {field:'operCount',title:'上架作业量',align:'center'},
			        {field:'count1',title:'退货上架量',align:'center'},
			        {field:'count2',title:'采购上架量',align:'center'},			        
			        {field:'onGuradTimeLength',title:'当日在岗总时长',align:'center'},
			        {field:'onGuradCount',title:'实际在岗人数',align:'center'},
			        {field:'onGuradPerCount',title:'日在岗人均产能',align:'center'},
			        {field:'standardCapacity',title:'标准人均产能',align:'center'}			        
			 ]];
		} else if (operType == 2) {
			col = [[
			        {field:'datetime',title:'日期',align:'center',
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
			        {field:'operCount',title:'退货入库量',align:'center'},
			        {field:'count1',title:'调拨入库量',align:'center'},
			        {field:'count2',title:'销售退货量',align:'center'},			        
			        {field:'onGuradTimeLength',title:'当日在岗总时长',align:'center'},
			        {field:'onGuradCount',title:'实际在岗人数',align:'center'},
			        {field:'onGuradPerCount',title:'日在岗人均产能',align:'center'},
			        {field:'standardCapacity',title:'标准人均产能',align:'center'}			        
			 ]];
		} else if (operType >= 3 && operType <= 6) {
			var title = "";
			if (operType == 3) {
				title = "分拣作业量";
			} else if (operType == 4) {
				title = "分播作业量";
			} else if (operType == 5) {
				title = "复核作业量";
			} else {
				title = "交接包裹量";
			}
			col = [[
			        {field:'datetime',title:'日期',align:'center',
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
			        {field:'operCount',title:'' + title,align:'center'},
			        {field:'onGuradTimeLength',title:'当日在岗总时长',align:'center'},
			        {field:'onGuradCount',title:'实际在岗人数',align:'center'},
			        {field:'onGuradPerCount',title:'日在岗人均产能',align:'center'},
			        {field:'standardCapacity',title:'标准人均产能',align:'center'}			        
			 ]];
		} else if (operType == 7) {
			col = [[
			        {field:'datetime',title:'日期',align:'center',
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
			        {field:'operCount',title:'异常订单处理量',align:'center'},
			        {field:'onGuradTimeLength',title:'当日在岗总时长',align:'center'},
			        {field:'onGuradCount',title:'实际在岗人数',align:'center'},
			        {field:'onGuradPerCount',title:'日在岗人均产能',align:'center'}
			 ]];
		}
		
		return col;
	}
	
	function getTitle() {
		var title = "";
		// 0 采购入库\r\n1 上架\r\n2 退货入库\r\n
		// 3 订单分拣\r\n4 订单分播\r\n5 订单复核\r\n6 订单交接\r\n7 异常处理
		switch (operType) {
		case "0":
			title = "采购入库";
			break;
		case "1":
			title = "上架";
			break;
		case "2":
			title = "退货入库";
			break;
		case "3":
			title = "分拣";
			break;
		case "4":
			title = "分播";
			break;
		case "5":
			title = "复核";
			break;
		case "6":
			title = "交接";
			break;
		case "7":
			title = "异常订单处理";
			break;			
		} 
		title = title + "日人均产能表格";
		return title;
	}
	
	ajaxFun(getTitle(), getGridColumns());
}


$(function(){ 
	$('#areaId,#areaId2').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getBIArea.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
	$('#operType,#operType2').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getEnableOperTypeList.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
	
	$('#beginYear,#endYear').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getYearList.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});

	$('#beginMonth,#endMonth').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getMonthList.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
	
	$('#beginDay,#endDay').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getDayList.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});	
 
	
	$("#getChart").click(function(){		
		var areaId = $('#areaId').combobox('getValue');
		if (areaId == -1) {
			$.messager.alert('提示','请选择仓库');
			return;
		}
		
		var operType = $('#operType').combobox('getValue');
		if (operType == -1) {
			$.messager.alert('提示','请选择作业环节');
			return;
		}
		var beginYear = $('#beginYear').combobox('getValue');
		var beginMonth = $('#beginMonth').combobox('getValue');
		var beginDay = $('#beginDay').combobox('getValue');
		var endYear = $('#endYear').combobox('getValue');
		var endMonth = $('#endMonth').combobox('getValue');
		var endDay = $('#endDay').combobox('getValue');
		
		if (beginYear == -1) {
			$.messager.alert('提示','请选择起始[年]');
			return;
		}	
		if (beginMonth == -1 && beginDay != -1) {
			$.messager.alert('提示','请选择起始[月]');
			return;
		}	
		if (endYear == -1 && endMonth != -1) {
			$.messager.alert('提示','请选择结束[年]');
			return;
		}
		if (endMonth == -1 && endDay != -1) {
			$.messager.alert('提示','请选择结束[月]');
			return;
		}
		if(beginDay != -1 && endDay == -1){
			$.messager.alert('提示','请选择结束[日]');
			return;
		}	
		if (endDay != -1 && beginDay == -1) {
			$.messager.alert('提示','请选择起始[日]');
			return;
		}
		if (endMonth != -1 && beginMonth == -1) {
			$.messager.alert('提示','请选择起始[月]');
			return;
		}
		
		if (beginYear != -1 && beginMonth != -1 && endYear != -1 && endMonth == -1) {
			$.messager.alert('提示','请选择结束[月]');
			return;
		}
		
		function showChart(json, id, text, yIndex, type){			
			$('#' + id).highcharts({
	            chart: {
	                zoomType: 'xy'
	            },
	            title: {
	            	text: '' + text[0]
	            },
	            tooltip: {
	    			formatter: function() {
	    				return '<b>'+ this.x +'</b><br/>'+
	    					 this.series.name +': '+ this.y ;
	    			}
	    		},
		        xAxis: [{
		            categories: json.obj.catList
		        }],
		        yAxis: [{ 
		            title: {
		                text: '' + text[1]
		            }
		        }, { 
		            title: {
		                text: '' + text[2]
		            },
		            opposite: true
		        }],
		        series: [{
		            name: '' + text[3],
		            type: '' + type[0],
		            yAxis: yIndex[0],
		            data: json.obj.data1
		        }, {
		            name: '' + text[4],
		            type: '' + type[1],
		            yAxis: yIndex[1],
		            data: json.obj.data2
		        }, {
		            name: '' + text[5],
		            type: '' + type[2],
		            yAxis: yIndex[2],
		            data: json.obj.data3
		        }]
	        });
		}
		
		function showChart2(json, id, text, yIndex, type){
			$('#' + id).highcharts({
	            chart: {
	                zoomType: 'xy'
	            },
	            title: {
	            	text: '' + text[0]
	            },
	            tooltip: {
	    			formatter: function() {
	    				return '<b>'+ this.x +'</b><br/>'+
	    					 this.series.name +': '+ this.y ;
	    			}
	    		},
		        xAxis: [{
		            categories: json.obj.catList
		        }],
		        yAxis: [{ 
		            title: {
		                text: '' + text[1]
		            }
		        }, { 
		            title: {
		                text: '' + text[2]
		            },
		            opposite: true
		        }],
		        series: [{
		            name: '' + text[3],
		            type: '' + type[0],
		            yAxis: yIndex[0],
		            data: json.obj.data1
		        }, {
		            name: '' + text[4],
		            type: '' + type[1],
		            yAxis: yIndex[1],
		            data: json.obj.data2
		        }]
	        });
		}
		
		function ajaxFun(url, callbackFun) {
			$.ajax({
				type : "post",
				url : "<%=request.getContextPath()%>/BIStoreController/" + url,
				dataType : "json",
				cache : false,
				data : {
					operType : operType,
					areaId : areaId,				
					beginYear : beginYear,
					beginMonth : beginMonth,
					beginDay : beginDay,
					endYear : endYear,
					endMonth : endMonth,
					endDay : endDay
				},
				error : function(x, s, e) {
					$.messager.show({
						msg : '' + eMsg,
						title : '提示'
					});
					callbackFun(null);
				},
				success : callbackFun
			});
		}
 
		function showChartByJson(json, id){
			var text = ["" + json.obj.title, "人均产能", "作业量", "作业量", "日在岗人均产能", "标准人均产能"];
			var yIndex = [1, 0, 0];
			var type = ["column", "spline", "spline"];
			showChart(json, id, text, yIndex, type);	
		}
		
		// 单仓效能
		ajaxFun("getSingleOperTypeChart.mmx", function(json) {
			if (json != null && json.success != null) {
				if (json.success) {
					if (operType == 7) {
						var text = ["" + json.obj.title, "人均产能", "作业量", "作业量", "日在岗人均产能"];
						var yIndex = [1, 0];
						var type = ["column", "spline"];
						showChart2(json, "chart1", text, yIndex, type);
					} else {
						showChartByJson(json, "chart1");	
					}
				} else {
					$.messager.show({
						msg : '' + json.msg,
						title : '提示'
					});	
				}	
			}
			
			// 分仓对比
			ajaxFun("getMultiOperTypeChart.mmx", function(json){			
				if (json != null && json.success != null) {
					if (json.success) {
						if (operType == 7) {
							var text = ["" + json.obj.title, "人均产能", "作业量", "作业量", "日在岗人均产能"];
							var yIndex = [1, 0];
							var type = ["column", "spline"];
							showChart2(json, "chart2", text, yIndex, type);							
						} else {
							showChartByJson(json, "chart2");	
						}
					} else {
						$.messager.show({
							msg : '' + json.msg,
							title : '提示'
						});	
					}	
				}			
			}); 
		});
		
		
	});
});

</script>
<title>作业环节能效</title>
</head>
<body>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>查询作业环节能效图表</legend>
			<form>
				<table>
					<tr>						
						<td><span style="font-size: 12px;">仓库&nbsp;</span><input id="areaId" style="width: 80px;" /></td>
						<td><span style="font-size: 12px;">&nbsp;&nbsp;&nbsp;作业环节&nbsp;</span><input id="operType" style="width: 80px;" /></td>
						<td><span style="font-size: 12px;">&nbsp;&nbsp;&nbsp;日期&nbsp;</span>
						<input id="beginYear" style="width: 60px;" />年
						<input id="beginMonth" style="width: 60px;" />月
						<input id="beginDay" style="width: 60px;" />日--
						</td>
						<td>
						<input id="endYear" style="width: 60px;" />年
						<input id="endMonth" style="width: 60px;" />月
						<input id="endDay" style="width: 60px;" />日
						</td>
						<td>&nbsp;</td>
						<td><a id="getChart" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" >查询</a></td>
					</tr>				 
				</table>
			</form>
		</fieldset>		
	<div>
	
	<div style="padding:3px; height:240px;">
		<div id="chart1" style="width: 95%; height:240px;"></div>					
	</div>
	<div style="padding:3px; height:240px;">
		<div id="chart2" style="width: 95%; height:240px;"></div>				
	</div>
	
	<div style="padding:3px; height: auto;">
			<fieldset>
			<legend>查询作业环节能效表格</legend>
				<form >
				<table>
					<tr>
						<td><span style="font-size: 12px;">仓库&nbsp;</span><input id="areaId2" style="width: 80px;" /></td>
						<td><span style="font-size: 12px;">&nbsp;&nbsp;&nbsp;作业环节&nbsp;</span><input id="operType2" style="width: 80px;" /></td>
						<td><span style="font-size: 12px;">&nbsp;&nbsp;&nbsp;日期&nbsp;</span><input class="easyui-datebox" editable="false" id="startDate">--</td>
						<td><input class="easyui-datebox" editable="false" id="endDate"></td>					
						</td>
						<td>&nbsp;</td>
						<td><a class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" onclick="javascript:getList();" >查询</a></td>
					</tr>				 
				</table>
			</form>
		</fieldset>
		<table id="list"></table>	 	
	</div>
</body>
</html>