<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>售后周期统计</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var isQuery = 0;
$(function(){
	$('#afterSaleType').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterSaleType.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
    $('#resultTabs').tabs({  
	     onSelect:function(title,index){  
	     	if(isQuery ==1){
	     		searchFun();
	     	}
	     }   
	}); 
    
});

function searchFun() {
	isQuery = 1;
	var afterSaleOrderCode = $('#afterSaleOrderCode').val();
	var afterSaleDetectProductCode = $('#afterSaleDetectProductCode').val();
	var startTime =  $('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	var afterSaleType = $('#afterSaleType').combobox('getValue');
	var flag = true;
	if ($.trim(startTime) != "" && $.trim(endTime) != "") {
		var days = getValidateSubDays(endTime, startTime);
		if (days < 0) {
			flag = false;
			$.messager.show({
				msg : "结束时间必须大于开始时间",
				title : '提示'
			});
		}
		if (days>30){
			flag = false;
			$.messager.show({
				msg : "日期时间段不得超过31天,请重新填写！",
				title : '提示'
			});
		}
	} 
	if(flag){
		var tab = $('#resultTabs').tabs('getSelected');
		var index = $('#resultTabs').tabs('getTabIndex',tab);
		if(index>0){
			if(afterSaleOrderCode!='' || afterSaleDetectProductCode!=''){
				$.messager.show({
					msg : "不能查单独一条记录的耗时!",
					title : '提示'
				});
			}else{
				if(index==1){
					loadAfterSaleConsumingDistribution(startTime,endTime,afterSaleType);
				}else if(index==2){
					loadAverageCustomerConsuming(startTime,endTime,afterSaleType);
				}else{
					loadAverageAfterSaleStockConsuming(startTime,endTime,afterSaleType);
				}
			}
		}else{
			loadAfterSaleCycle(afterSaleOrderCode,afterSaleDetectProductCode,startTime,endTime,afterSaleType);
		}
	}
}


function loadAfterSaleCycle(afterSaleOrderCode,afterSaleDetectProductCode,startTime,endTime,afterSaleType){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleCycle.mmx?afterSaleOrderCode='+afterSaleOrderCode
				+ "&afterSaleDetectProductCode=" + afterSaleDetectProductCode + "&startTime=" + startTime + "&endTime=" + endTime 
				+ "&afterSaleType=" + afterSaleType,
		method : 'post',
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
	    pageList : [20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    columns:[[ 
				{field:'afterSaleOrderCode',title:'售后单号',rowspan:2,width:$(this).width() * 0.15,align:'center'},
				{field:'afterSaleDetectProductCode',title:'处理单号',rowspan:2,width:$(this).width() * 0.15,align:'center'},
				{field:'totalConsumingStr',title:'总耗时',rowspan:2,width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'customerReturnConsumingStr',title:'用户寄回耗时',rowspan:2,width:$(this).width() * 0.15,align:'center',sortable:true},
				{title:'客服耗时',colspan:4,width:$(this).width() * 0.6,align:'center'},  
				{title:'售后仓内耗时',colspan:6,width:$(this).width() * 0.6,align:'center'},		
				{field:'financialRefundConsumingStr',title:'财务退款耗时',rowspan:2,width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'repairsConsumingStr',title:'维修耗时',rowspan:2,width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'customerPayMoneyConsumingStr',title:'用户打款耗时',rowspan:2,width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'sShippingConsumingStr',title:'s单发货耗时',rowspan:2,width:$(this).width() * 0.15,align:'center',sortable:true}
			],
			[
				{field:'preAfterSaleConsumingStr',title:'售后前期耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'customerConfirmConsumingStr',title:'与客户确认耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'confirmCostsConsumingStr',title:'重新申请确认费用耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'sApplyDeliveryConsumingStr',title:'s单申请发货耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'qualitySupportConsumingStr',title:'质检支撑耗时耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'matchPackageConsumingStr',title:'匹配包裹耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'detectConsumingStr',title:'检测耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'enterAfterSaleStockConsumingStr',title:'入售后库耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'afterSaleShippingConsumingStr',title:'售后发货耗时',width:$(this).width() * 0.15,align:'center',sortable:true},
				{field:'backSupplierConsumingStr',title:'返厂耗时',width:$(this).width() * 0.15,align:'center',sortable:true}
			]
		]
	});
}

function loadAfterSaleConsumingDistribution(startTime,endTime,afterSaleType){
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/afterSaleConsumingDistributed.mmx',
		type : 'post',
		dataType : 'json',
		cache : 'false',
		data : {
			startTime : startTime,
			endTime : endTime,
			afterSaleType : afterSaleType
		},
		success : function(result){
			if(result.success){
				$("#count").html(result.msg);
				$('#afterSaleConsumingDistribution').highcharts({
			         title: { 
			         	text: '售后耗时分布图' 
			         }, 
			         xAxis: [{ 
			         	categories: ['4小时以内','4小时至8小时','8小时至2天','2天至5天','5天至35天','大于35天'] 
			         }],
			        yAxis: [{ 
				        // Primary yAxis 
				        labels: { 
				       		 style: { 
				       		 	color: '#89A54E' 
				       		 }
				        },
				        title: {
				        	 text: '售后耗时记录数',
				        	 style: { color: '#89A54E' } 
				        }
			         },
			         { // Secondary yAxis		        
			        	labels: {
			        		format: '{value}%',
			        		style: { 
			        			color: '#4572A7' 
			        		}
			        	 },
			        	 opposite: true,
			        	 title: {
				        	 text: '占比',
				        	 style: { color: '#89A54E' } 
				        }
			        }],
			        tooltip: { 
			        	shared: true 
			        },	      
			        series: [{
			        	name:  '售后耗时记录数',
						type: 'column',
						data: result.obj[0]
			        },{
			        	name:  '占比',
						type: 'spline',
			            yAxis: 1,
						data: result.obj[1],
						tooltip: {
			                valueSuffix: '%'
			            }
			        }] 
			    });
			}		
		}
	});
}

function loadAverageCustomerConsuming(startTime,endTime,afterSaleType){
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAverageCustomerConsuming.mmx',
		type : 'post',
		dataType : 'json',
		cache : 'false',
		data : {
			startTime : startTime,
			endTime : endTime,
			afterSaleType : afterSaleType
		},
		success : function(result){
			$('#averageCustomerConsuming').highcharts({
		        title: {
		            text: '客服各节点平均耗时'
		        },
		        xAxis: {
		            categories: ['售后前期','与客户确认','重新申请费用确认','s单申请发货']
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: '耗时(小时)'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			                '<td style="padding:0"><b>{point.y:.1f} 小时</b></td></tr>',
			        footerFormat: '</table>',
			        shared: true,
			        useHTML: true
		        },
		        series: [{
		            name: '平均耗时',
		            data: result.obj[0],
		            type: 'column',
		            dataLabels: {
		                enabled: true,
		                rotation: -90,
		                color: '#FFFFFF',
		                align: 'right',
		                x: 4,
		                y: 10,
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif',
		                    textShadow: '0 0 3px black'
		                }
		            }
		        }]
		    });
		    
		    $('#MaxMinCustomerConsuming').highcharts({
		        title: {
		            text: '客服各节点最长最短耗时比较'
		        },
		        xAxis: {
		            categories: ['售后前期','与客户确认','重新申请费用确认','s单申请发货']
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: '耗时(小时)'
		            },
			        plotOptions: {
			            column: {
			                pointPadding: 0.2,
			                borderWidth: 0
			            }
			        }
			    },
			     tooltip: {
			            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			                '<td style="padding:0"><b>{point.y:.1f} 小时</b></td></tr>',
			            footerFormat: '</table>',
			            shared: true,
			            useHTML: true
			        },
			    series: [{
			        name: '最长耗时',
			        type: 'column',
			        data: result.obj[1],
		            dataLabels: {
		                enabled: true,
		                rotation: -90,
		                color: '#FFFFFF',
		                align: 'right',
		                x: 4,
		                y: 10,
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif',
		                    textShadow: '0 0 3px black'
		                }
		            }
			    },{
			        name: '最短耗时',
			        type: 'column',
			        data: result.obj[2],
		            dataLabels: {
		                enabled: true,
		                rotation: -90,
		                color: '#FFFFFF',
		                align: 'right',
		                x: 4,
		                y: 10,
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif',
		                    textShadow: '0 0 3px black'
		                }
		            }
			    }]
		    });
		}	
	});
}

function loadAverageAfterSaleStockConsuming(startTime,endTime,afterSaleType){
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAverageAfterSaleStockConsuming.mmx',
		type : 'post',
		dataType : 'json',
		cache : 'false',
		data : {
			startTime : startTime,
			endTime : endTime,
			afterSaleType : afterSaleType
		},
		success : function(result){
			$('#averageAfterSaleSotckConsuming').highcharts({
		        title: {
		            text: '仓内各节点平均耗时'
		        },
		        xAxis: {
		            categories: ['质检支撑','匹配包裹','检测','入售后库','售后发货','返厂']
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: '耗时(小时)'
		            }
		        },
		        tooltip: {
		           pointFormat: '平均耗时: <b>{point.y:.1f}小时</b>'
		        },
		        series: [{
		            name: '平均耗时',
		            data: result.obj[0],
		            type: 'column',
		            dataLabels: {
		                enabled: true,
		                rotation: -90,
		                color: '#FFFFFF',
		                align: 'right',
		                x: 4,
		                y: 10,
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif',
		                    textShadow: '0 0 3px black'
		                }
		            }
		        }]
		    });
		    
		    $('#MaxMinAfterSaleSotckConsuming').highcharts({
		        title: {
		            text: '仓内各节点最长最短耗时比较'
		        },
		        xAxis: {
		            categories: ['质检支撑','匹配包裹','检测','入售后库','售后发货','返厂']
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: '耗时(小时)'
		            },
			        plotOptions: {
			            column: {
			                pointPadding: 0.2,
			                borderWidth: 0
			            }
			        }
			    },
			     tooltip: {
			            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			                '<td style="padding:0"><b>{point.y:.1f}小时</b></td></tr>',
			            footerFormat: '</table>',
			            shared: true,
			            useHTML: true
			        },
			    series: [{
			        name: '最长耗时',
			        type: 'column',
			        data: result.obj[1],
		            dataLabels: {
		                enabled: true,
		                rotation: -90,
		                color: '#FFFFFF',
		                align: 'right',
		                x: 4,
		                y: 10,
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif',
		                    textShadow: '0 0 3px black'
		                }
		            }
			    },{
			        name: '最短耗时',
			        type: 'column',
			        data: result.obj[2],
		            dataLabels: {
		                enabled: true,
		                rotation: -90,
		                color: '#FFFFFF',
		                align: 'right',
		                x: 4,
		                y: 10,
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif',
		                    textShadow: '0 0 3px black'
		                }
		            }
			    }]
		    });
		}	
	});
}
</script>
</head>
<body>
		<fieldset>
			<legend>售后周期统计</legend>
					<table class="" >
						<tr align="center" >
							<th >售后单号</th>
							<td align="left">
								<input id="afterSaleOrderCode" name="afterSaleOrderCode" style="width:100px" />
							</td>
							<th >售后处理单号</th>
							<td align="left">
								<input id="afterSaleDetectProductCode" name="afterSaleDetectProductCode" style="width:100px" />
							</td>
							<th >售后单创建时间段：</th>
							<td align="left">
								<input type="text" id="startTime" class="easyui-datebox" style="width:100px" name="startTime" required/>
								--<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:100px" required/>
							</td>
							<th>售后类型：</th>
							<td align="left">
								<input type="text" id="afterSaleType" name="afterSaleType"/>
							</td>
							<td align="right" >
								<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
							</td>
						</tr>
					</table>
		</fieldset>
	<div id="resultTabs" class="easyui-tabs" style="height:600px;">
		<div title="查询结果详情" style="padding:10px">
			<table id="datagrid"></table>
		</div>
		<div title="售后耗时分布" style="padding:10px">
			<h3>售后耗时记录数：<span id="count"></span>个</h3>
			<div id="afterSaleConsumingDistribution" style="min-width:310px; height: 500px; margin: 0 auto"></div>
		</div>
		<div title="客服平均耗时" style="padding:10px">
			<div id="averageCustomerConsuming" style="float:left;width:600px; height: 500px; padding:20px;"></div>
			<div id="MaxMinCustomerConsuming" style="float:left;width:600px; height: 500px; padding:20px;"></div>
		</div>
		<div title="售后仓内平均耗时" style="padding:10px">
			<div id="averageAfterSaleSotckConsuming" style="float:left;width:600px; height: 500px; padding:20px;"></div>
			<div id="MaxMinAfterSaleSotckConsuming" style="float:left;width:600px; height: 500px; padding:20px;"></div>
		</div>
	</div>
</body>
</html>