<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="cache.ProductLinePermissionCache" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="cache.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="mmb.stock.stat.*" %>
<%
int searchType=1;
if(request.getParameter("searchType")!=null){
	searchType=Integer.parseInt(request.getParameter("searchType"));
}

String startDate =DateUtil.getBeforOneDay();
if(request.getParameter("startDate")!=null){
	startDate=request.getParameter("startDate");
}
String endDate =DateUtil.getBeforOneDay();
if(request.getParameter("endDate")!=null){
	endDate=request.getParameter("endDate");
}
String[] productLines = request.getParameterValues("productLines");//产品线
Map<String,String> map = new HashMap<String,String>();
boolean productLineAll = false;
if( productLines != null && productLines.length > 0 ) {
	for( int i = 0; i < productLines.length; i ++ ) {
		map.put(productLines[i], "");
	}
}else {
	productLineAll = true;
}
String[][] dateList=(String[][])request.getAttribute("dateList");
int wareArea = -1;
if( request.getParameter("wareArea") != null ) {
	wareArea = StringUtil.toInt(request.getParameter("wareArea"));
}
String wareAreaSelectLableAll = ProductWarePropertyService.getWeraAreaOptionsAll(wareArea);
%>

<%@page import="adultadmin.util.DateUtil"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.order.OrderStockBean"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="mmb.stock.stat.OrderStockTimelyBean"%><html>
<head>
<title>订单发货时效统计</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/grid.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="javascript" type="text/javascript" src="<%= request.getContextPath() %>/js/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/demo/demo.css">
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/locale/easyui-lang-zh_CN.js"></script>
<style type="text/css">
	body {
		padding: 0px;
	}
	form {
		padding: 0px;
		margin-left: 0px;
		margin-right:0px;
	}
</style>
</head>
<body>
	<% 
		if( dateList == null || dateList.length == 0 ) {
	%>
			<script type="text/javascript">
				$.messager.alert("提示", "未查出结果！");
			</script>
	<%
		} else {
	%>
<br/>
<br/>
<div id="orderSendOutEffectiveDiv">
<script type="text/javascript">
var chart1;
$(document).ready(function() {
	chart1 = new Highcharts.Chart({

		chart: {
			renderTo: 'container1',
			defaultSeriesType: 'column'
		},

		title: {
			text: '订单发货实效分布图'
		},

		xAxis: {
			title: {
				text: '日期'
			},
			categories: [	<%
		             		for( int i = 0; i < dateList.length-1; i ++ ) {
				             	%>
					 			<%if( i == (dateList.length -2) ) { %>
				             	'<%= dateList[i][0]%>'
					 			<% } else { %>
				             	'<%= dateList[i][0]%>', 
					 			<%
					 			}
				             	}
					 			%>
			 			]
		},

		yAxis: [
					{ // Primary yAxis
					    labels: {
					        format: '{value}%',
					        style: {
					            color: '#89A54E'
					        }
					    },
					    title: {
					        text: '占比',
					        style: {
					            color: '#89A54E'
					        }
					    },
					    opposite: true
					}, { // Secondary yAxis
					    title: {
					        text: '单量',
					        style: {
					            color: '#4572A7'
					        }
					    },
					    labels: {
					        format: '{value} 个'
					    }
					}
		        ],
		/* tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
				 this.series.name +': '+ this.y +'<br/>';
			}
		}, */

		plotOptions: {
			series: {
				cursor: 'pointer',
				point: {
					events: {
						click: function() {
							//alert("这里可以调用页面的查询");
						}		
					}
				}
			},
			column: {
				stacking: 'normal'
			}
		},

	    series: [
	     	{
			name: '大于24小时',
			color: '#FF6666',
			data: [
					<%
						for( int i = 0; i < dateList.length-1; i ++ ) {
					%>
					<%if( i == (dateList.length -2) ) { %>
					{y:<%= dateList[i][3]%>}
					<% } else { %>
					{y:<%= dateList[i][3]%>}, 
					<%
					}
					}
					%>
				   ],
			stack: 'male',
            yAxis: 1,
            tooltip: {
                valueSuffix: ' 个'
            }
			},
			{
			name: '12到24小时',
			color: '#CCFFFF',
			data: [
						<%
						for( int i = 0; i < dateList.length-1; i ++ ) {
						%>
						<%if( i == (dateList.length -2) ) { %>
						{y:<%= dateList[i][2]%>}
						<% } else { %>
						{y:<%= dateList[i][2]%>}, 
						<%
						}
						}
						%>
					],
			stack: 'male',
            yAxis: 1,
            tooltip: {
                valueSuffix: ' 个'
            }
			},
			{
			name: '小于12小时',
			color: '#336699',
			data: [
						<%
						for( int i = 0; i < dateList.length-1; i ++ ) {
						%>
						<%if( i == (dateList.length -2) ) { %>
						{y:<%= dateList[i][1]%>}
						<% } else { %>
						{y:<%= dateList[i][1]%>}, 
						<%
						}
						}
						%>
				   ],
			stack: 'male',
            yAxis: 1,
            tooltip: {
                valueSuffix: ' 个'
            }
			},
			{
                name: '大于24小时占比',
                color: '#FF6666',
                type: 'spline',
                data: [
						<%
						for( int i = 0; i < dateList.length-1; i ++ ) {
						%>
						<%if( i == (dateList.length -2) ) { %>
						<%= dateList[i][6]%>
						<% } else { %>
						<%= dateList[i][6]%>, 
						<%
						}
						}
						%>
                       ],
                tooltip: {
                    valueSuffix: '%'
                }
    
            },
			{
                name: '12到24小时占比',
                color: '#CCFFFF',
                type: 'spline',
                data: [
						<%
						for( int i = 0; i < dateList.length-1; i ++ ) {
						%>
						<%if( i == (dateList.length -2) ) { %>
						<%= dateList[i][5]%>
						<% } else { %>
						<%= dateList[i][5]%>, 
						<%
						}
						}
						%>
					],
                tooltip: {
                    valueSuffix: '%'
                }
    
            },
			{
                name: '小于12小时占比',
                color: '#336699',
                type: 'spline',
                data: [
							<%
							for( int i = 0; i < dateList.length-1; i ++ ) {
							%>
							<%if( i == (dateList.length -2) ) { %>
							<%= dateList[i][4]%>
							<% } else { %>
							<%= dateList[i][4]%>, 
							<%
							}
							}
							%>
					],
                tooltip: {
                    valueSuffix: '%'
                }
    
            }
			]
	});
	
	
});
var chart2;
$(document).ready(function() {
	chart2 = new Highcharts.Chart({

		chart: {
			renderTo: 'container2',
			defaultSeriesType: 'column'
		},

		title: {
			text: '订单发货实效图'
		},

		xAxis: {
			title: {
				text: '日期'
			},
			categories: [	
			             	<%
			             		for( int i = 0; i < dateList.length -1; i ++ ) {
			             	%>
				 			<%if( i == (dateList.length -2) ) { %>
			             	'<%= dateList[i][0]%>'
				 			<% } else { %>
			             	'<%= dateList[i][0]%>', 
				 			<%
				 			}
			             	}
				 			%>
			 			]
		},

		yAxis: [
					{ // Primary yAxis
					    labels: {
					        style: {
					            color: '#89A54E'
					        }
					    },
					    title: {
					        text: '占比',
					        style: {
					            color: '#89A54E'
					        }
					    },
					    opposite: true
					}, { // Secondary yAxis
					    title: {
					        text: '平均时间H',
					        style: {
					            color: '#4572A7'
					        }
					    }
					}
		        ],
		/* tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
				 this.series.name +': '+ this.y +'<br/>';
			}
		}, */

		plotOptions: {
			series: {
				cursor: 'pointer',
				point: {
					events: {
						click: function() {
							//alert("这里可以调用页面的查询");
						}		
					}
				}
			},
			column: {
				stacking: 'normal'
			}
		},

	    series: [
            {
    			name: '复核到出库',
    			colort: 'green',
    			data: [
						<%
						for( int i = 0; i < dateList.length -1; i ++ ) {
						%>
						<%if( i == (dateList.length -2) ) { %>
						{y:<%= dateList[i][10]%>}
						<% } else { %>
						{y:<%= dateList[i][10]%>}, 
						<%
						}
						}
						%>
    				   ],
    			stack: 'male',
                yAxis: 1,
                tooltip: {
                    valueSuffix: ' 小时'
                }
    			},
			{
			name: '分播到复核',
			color: 'red',
			data: [
					<%
					for( int i = 0; i < dateList.length -1; i ++ ) {
					%>
					<%if( i == (dateList.length -2) ) { %>
					{y:<%= dateList[i][9]%>}
					<% } else { %>
					{y:<%= dateList[i][9]%>}, 
					<%
					}
					}
					%>
					],
			stack: 'male',
            yAxis: 1,
            tooltip: {
                valueSuffix: ' 小时'
            }
			},
			{
			name: '领单到分播',
			color: '#CCFFFF',
			data: [
					<%
					for( int i = 0; i < dateList.length -1 ; i ++ ) {
					%>
					<%if( i == (dateList.length -2) ) { %>
					{y:<%= dateList[i][8]%>}
					<% } else { %>
					{y:<%= dateList[i][8]%>}, 
					<%
					}
					}
					%>
				   ],
			stack: 'male',
            yAxis: 1,
            tooltip: {
                valueSuffix: ' 小时'
            }
			},
			{
			name: '申请出库到领单',
			color: 'blue',
			data: [
					<%
					for( int i = 0; i < dateList.length -1; i ++ ) {
					%>
					<%if( i == (dateList.length -2) ) { %>
					{y:<%= dateList[i][7]%>}
					<% } else { %>
					{y:<%= dateList[i][7]%>}, 
					<%
					}
					}
					%>
				   ],
			stack: 'male',
            yAxis: 1,
            tooltip: {
                valueSuffix: ' 小时'
            }
			},
			{
                name: '复合到出库平均耗时占比',
                color: 'green',
                type: 'spline',
                data: [
						<%
						for( int i = 0; i < dateList.length - 1; i ++ ) {
						%>
						<%if( i == (dateList.length -2) ) { %>
						<%= dateList[i][14]%>
						<% } else { %>
						<%= dateList[i][14]%>, 
						<%
						}
						}
						%>
					],
                tooltip: {
                    valueSuffix: '%'
                }
    
            },
			{
                name: '分播到复合平均耗时占比',
                color: 'red',
                type: 'spline',
                data: [
							<%
							for( int i = 0; i < dateList.length -1; i ++ ) {
							%>
							<%if( i == (dateList.length -2) ) { %>
							<%= dateList[i][13]%>
							<% } else { %>
							<%= dateList[i][13]%>, 
							<%
							}
							}
							%>
						],
                tooltip: {
                    valueSuffix: '%'
                }
    
            },
			{
                name: '领单到分播平均耗时占比',
                color: '#CCEFEF',
                type: 'spline',
                data: [
						<%
						for( int i = 0; i < dateList.length-1; i ++ ) {
						%>
						<%if( i == (dateList.length -2) ) { %>
						<%= dateList[i][12]%>
						<% } else { %>
						<%= dateList[i][12]%>, 
						<%
						}
						}
						%>
                       ],
                tooltip: {
                    valueSuffix: '%'
                }
    
            },
			{
                name: '申请出库到领单平均耗时占比',
                color: 'blue',
                type: 'spline',
                data: [
							<%
							for( int i = 0; i < dateList.length -1; i ++ ) {
							%>
							<%if( i == (dateList.length -2) ) { %>
							<%= dateList[i][11]%>
							<% } else { %>
							<%= dateList[i][11]%>, 
							<%
							}
							}
							%>
						],
                tooltip: {
                    valueSuffix: '%'
                }
    
            }
			]
	});
});
	
</script>
<%
		}
%>

		<div id="strape1" style="width:2%;height:100px;margin:0; float:left;"></div>
		<div id="container1" style="width:46%; height: 400px; margin: 0; float:left;"></div> 
		<div id="strape2" style="width:4%;height:100px;margin:0; float:left;"></div>
		<div id="container2" style="width:46%; height: 400px; margin: 0;float:left;"></div> 

</div>
<br/>
<div style="margin-top:430px;">
	<fieldset style="height:90;border-size:1;">
	<legend><b>订单发货时效查询</b></legend>
<!-- <div class="easyui-panel" data-options="title:'订单发货时效查询',height:95,collapsible:true" > -->
		<%
		List lineList=ProductLinePermissionCache.getAllProductLineList();
		%>
	<form name="form1" action="<%= request.getContextPath()%>/admin/stat/orderSendOutStatistic.mmx" method="post" >
	<div>
		<b>产品线：</b>	<br/>
						&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" value="-1" name="productLines" <% if ( map.containsKey("-1") || productLineAll ){ %> checked="checked" <% } %>/>全部
						<%
						int index = 1;
						for(Iterator i=lineList.iterator();i.hasNext();){
							voProductLine line = (voProductLine)i.next();
							index ++;
						%>	
							&nbsp;&nbsp;<input type="checkbox" value="<%=line.getId()%>" name="productLines" <% if ( map.containsKey(new Integer(line.getId()).toString()) ){ %>checked="checked" <% } %>/><%=line.getName()%>
						
						<%
							if( index == 10 ) {
						%>
								
						<%								
							}
						}
						%>	
	</div>
	<br/>
	<div>
	<b>仓库：</b> <%= wareAreaSelectLableAll %>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<b>时间段：</b><input type="text" name="startDate" onclick="WdatePicker();" value="<%= startDate%>"/>
	到
	<input type="text" name="endDate" onclick="WdatePicker();" value="<%= endDate%>"/> 
	&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="radio" name="searchType" value="1" <% if ( searchType == 1 ){ %>checked="checked" <% } %>/>日
	&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="radio" name="searchType" value="2" <% if ( searchType == 2 ){ %>checked="checked" <% } %>/>周
	&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="radio" name="searchType" value="3" <% if ( searchType == 3 ){ %>checked="checked" <% } %>/>月
	&nbsp;&nbsp;&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;&nbsp;
	<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" href="javascript:submitForm();">查询</a>
	</div>
	</form>
</div>
<!-- </div> -->
	</fieldset>
<script type="text/javascript">
	function submitForm() {
		document.form1.submit();
	}
</script>

</body>
</html>