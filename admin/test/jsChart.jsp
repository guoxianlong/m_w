<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE HTML> 
<html> 
	<head> 
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"> 
		<title>Highcharts Example</title> 
		
		
		<!-- 1. Add these JavaScript inclusions in the head of your page --> 
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script> 
		<script type="text/javascript" src="../js/highcharts.js"></script> 
		<script type="text/javascript" src="../js/modules/exporting.js"></script> 
		<script type="text/javascript"> 
		
			var chart;
			$(document).ready(function() {
				chart = new Highcharts.Chart({
			
					chart: {
						renderTo: 'container',
						defaultSeriesType: 'column'
					},
			
					title: {
						text: 'Total fruit consumtion, grouped by gender'
					},
			
					xAxis: {
						categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
					},
			
					yAxis: {
						allowDecimals: false,
						min: 0,
						title: {
							text: 'Number of fruits'
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
										location.href = this.options.url;
									}		
								}
							}
						},
						column: {
							stacking: 'normal'
						}
					},
			
				    series: [{
						name: 'John',
						data: [{y:5,url: 'http://google.com/'}, {y:3,url: 'http://baidu.com/'}, {y:4,url: 'http://baidu.com/'}, {y:7,url: 'http://baidu.com/'}, {y:2,url: 'http://baidu.com/'}],
						stack: 'male'
					}, {
						name: 'Joe',
						data: [{y:3,url: 'http://google.com/'}, {y:4,url: 'http://google.com/'}, {y:4,url: 'http://google.com/'}, {y:2,url: 'http://google.com/'}, {y:5,url: 'http://google.com/'}],
						stack: 'male'
					}]
				});
				
				
			});
				
		</script> 
		
	</head> 
	<body> 
		
		<!-- 3. Add the container --> 
		<div id="container" style="width: 800px; height: 400px; margin: 0 auto"></div> 
		
				
	</body> 
</html> 