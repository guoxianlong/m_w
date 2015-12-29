<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.*" %>
<%
voUser user = (voUser)request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
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
	var layer = $("#layer2").combobox('getValue');
	var areaId = $("#areaId2").combobox('getValue');
	if (areaId == null || areaId == -1) {
		alert("请选择仓库");
		$('#areaId').focus();
		return;
	}
	var startDate = $("#startDate").datebox('getValue');
	var endDate = $("#endDate").datebox('getValue');
	if (startDate == '' || endDate == '') {
		alert('请选择日期');
		return;
	}
	if(!validateSubDate(endDate,startDate)){		
		$('#startDate').focus();
		return;
	}
	$("#list").datagrid({
		title:"日人均产能",
		idField : 'id',
		iconCls:'icon-ok',
		fitColumns:true,
		width:800,
		pageNumber:1,
		pageSize:31,
		pageList:[31],
		url:'${pageContext.request.contextPath}/BIStoreController/getOrderCountTableList.mmx',
		queryParams:{
			startDate: '' + startDate,
			endDate: '' + endDate,
			areaId: '' + areaId,
			layer: '' + layer
		},
		showFooter:true,
		striped:true,
		collapsible:true,
		loadMsg:'数据加载中...',
		rownumbers:true,
		singleSelect:true,//只选择一行后变色
		pagination:true,
		columns:[[
		        {field:'datetime',title:'日期',align:'center',
		        	formatter: function(value, row, index){
		        		return value.substring(0,10);
		        	}},
		        {field:'orderCount',title:'订单量',align:'center'},
		        {field:'inCount',title:'在职人数',align:'center'},
		        {field:'onCount',title:'在岗人数',align:'center'},
		        {field:'inPerOrderCount',title:'在职人均产能',align:'center'},
		        {field:'onPerOrderCount',title:'在岗人均产能',align:'center'},
		        {field:'onGuradPer',title:'在岗率',align:'center',
		        	formatter: function(value, row, index){
		        		return value + '%';
		        	}}
		]]
	});
}


$(function(){
	$('#layer,#layer2').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getBILayerType.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
	
	$('#areaId').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getBIArea.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});	
	$('#areaId2').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getBIAllArea.mmx',
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
		
		function ajaxFun(url, callbackFun) {
			$.ajax({
				type : "post",
				url : "<%=request.getContextPath()%>/BIStoreController/" + url,
				dataType : "json",
				cache : false,
				data : {
					layer : $('#layer').combobox('getValue'),
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
 
		function showOrderCountChart(json, id){		
			var text = ["" + json.obj.title, "人均产能", "订单量", "订单量", "日在职人均产能", "日在岗人均产能"];
			var yIndex = [1, 0, 0];
			var type = ["column", "spline", "spline"];
			showChart(json, id, text, yIndex, type);			
		}
		
		function showOnGuradPerChart(json){		
			var id = "chart2";
			var text = ["" + json.obj.title, "人数", "在岗位率", "在职人数", "在岗人数", "在岗率"];
			var yIndex = [0, 0, 1];
			var type = ["column", "column", "spline"];
			showChart(json, id, text, yIndex, type);	
		}
		
		
		// 单仓效能
		ajaxFun("getSingleOrderCountChart.mmx", function(json) {
			if (json != null && json.success != null) {
				if (json.success) {
					showOrderCountChart(json, "chart1");
				} else {
					$.messager.show({
						msg : '' + json.msg,
						title : '提示'
					});	
				}	
			}
			
			// 在岗率
			ajaxFun("getOnGuradPerChart.mmx", function(json){
				if (json != null && json.success != null) {
					if (json.success) {
						showOnGuradPerChart(json);
					} else {
						$.messager.show({
							msg : '' + json.msg,
							title : '提示'
						});	
					}	
				}
				
				// 分仓对比
				ajaxFun("getMultiOrderCountChart.mmx", function(json){			
					if (json != null && json.success != null) {
						if (json.success) {
							showOrderCountChart(json, "chart3");
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
});

</script>
<title>整体能效</title>
</head>
<body>
<% if (group.isFlag(2142)) { %>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>查询整体能效图表</legend>
			<form>
				<table>
					<tr>
						<td><span style="font-size: 12px;">层次&nbsp;</span><input id="layer" style="width: 80px;" /></td>
						<td><span style="font-size: 12px;">&nbsp;&nbsp;&nbsp;仓库&nbsp;</span><input id="areaId" style="width: 80px;" /></td>
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
	</div>
	<div style="position:relative; padding:3px; height:240px;">
		<div id="chart1" style="float:left; position:relative; left:0px; right:430px; top:0xp; height:240px;"></div>
		<div id="chart3" style="float:left; position:relative; right:0px; top:0xp; width:380px; height:240px;"></div>
		<div id="chart2" style="float:left; position:relative; left:0px; right:430px; top:0xp; height:240px;"></div>				
	</div>
<% } %>
<% if (group.isFlag(2143)) { %>	
	<div style="float:left; padding:3px; height: auto;">
			<fieldset>
			<legend>查询整体能效表格</legend>
				<form >
				<table>
					<tr>
						<td><span style="font-size: 12px;">层次&nbsp;</span><input id="layer2" style="width: 80px;" /></td>
						<td><span style="font-size: 12px;">&nbsp;&nbsp;&nbsp;仓库&nbsp;</span><input id="areaId2" style="width: 80px;" /></td>
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
<% } %>	
</body>
</html>