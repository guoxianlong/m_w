<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../echarts/echarts_inc.jsp"></jsp:include>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
$(function() {
	var count;
	var time;
		$('#deliver').combobox({
			url : '${pageContext.request.contextPath}/OrderStockController/getDeliverComboBox.mmx',
			valueField : 'id',
			textField : 'name',
			editable : false,
			panelHeight : 'auto'
		});
		datagrid = $('#datagrid').datagrid({
			url : '${pageContext.request.contextPath}/TPcontroller/getStaticInfo.mmx',
			queryParams: {
				userId:'${param.userId}'
			},
			toolbar : '#toolbar',
		    idField : 'id',
		    fit : true,
		    fitColumns : true,
		    striped : true,
		    nowrap : true,
		    loadMsg : '正在努力为您加载..',
		    pagination : true,
		    rownumbers : true,
		    singleSelect : true,
		    pageSize : 20,
		    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
		    frozenColumns : [[
		    ]],
		    columns:[[  
		        {field:'deliverName',title:'快递公司',width:50,align:'center'},
		        {field:'userName',title:'用户名',width:50,align:'center'},  
		        {field:'createDateTime',title:'查询时间',width:60,align:'center'}, 
		        {field:'ip',title:'访问IP',width:40,align:'center'}, 
		        {field:'packageCode',title:'包裹单号',width:40,align:'center'}, 
		        {field:'orderCode',title:'订单号',width:40,align:'center'}, 
		        {field:'orderTypeName',title:'订单品类',width:50,align:'center'}, 
		        {field:'buyModeName',title:'付款方式',width:50,align:'center'},
		        {field:'dprice',title:'订单金额',width:50,align:'center'},
		        {field:'orderStatusName',title:'订单投递状态',width:50,align:'center'}
		       
		    ]],
		    onLoadSuccess : function(data) {
				try {
					loadEchartsChart(data.footer[0].count,data.footer[0].time);
				} catch(e) {
					$.messager.alert("提示", "错误" ,"info");
				}
			}
		}); 
	
});
function loadEchartsChart(count,time) {
	var onechart = $("#oneChart");
	var myChart = echarts.init(onechart[0]);
	var option = {
		title : {
	        text: '访问数量',
	        x:"center"
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
            data:['日查询量'],
            y:"bottom"
	    },
	    xAxis : [
	        {
	            type : 'category',
	            boundaryGap : false,
	            data:time.split(",")
	        }
	    ],
	    yAxis : [
	        {
                name : '日查询量',
                type : 'value'
	        }
	    ],
	    series : [
	        {
	            name:'日查询量',
	            type:'line',
	            data:count.split(",")
	        }
	    ]
	};
	myChart.setOption(option);
}
function searchFun() {
	datagrid.datagrid('load', {
		deliver : $('#deliver').combobox('getValue'),
		startDate : $('#startDate').datebox("getValue"),
		endDate : $('#endDate').datebox("getValue")
	});
}
function exportExcel(){
	var deliver = $('#deliver').combobox('getValue');
	var startDate = $('#startDate').datebox("getValue");
	var endDate = $('#endDate').datebox("getValue");
	var params = "deliver=" + deliver + "&startDate=" + startDate
	+ "&endDate=" + endDate +"&userId="+<%=request.getParameter("userId")%>;
	window.open("${pageContext.request.contextPath}/TPcontroller/exportUserLogList.mmx?"+ params, "_blank");
}
</script>
</head>
<body>
	<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
			<fieldset>
				<legend>筛选</legend>
				<table>
					<tr>
						<th>快递公司:</th>
						<td>
							<input name='deliver' id='deliver' style="width:100px"/>
						</td>
						
						<th>查询日期：</th>
						<td>
							<input id="startDate" name="startDate"   class="easyui-datebox" editable="false" style="width: 120px;" />
							到
							<input id="endDate" name="endDate"   class="easyui-datebox" editable="false" style="width: 120px;" />
							<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
							<mmb:permit value="3003">
								<a class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="exportExcel();" href="javascript:void(0);">导出查询结果</a>
							</mmb:permit>
						</td>
					</tr>
				</table>
			</fieldset>
		<div id="oneChart" style="height:245px;border:1px solid #ccc;padding:10px;">></div>
	</div>
		<table id="datagrid"></table> 
</body>
</html>