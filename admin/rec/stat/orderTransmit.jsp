<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="mmb.rec.orderTransmit.OrderTransmitJobBean"%>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<html>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<head>
<title>出库交接统计</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/dark-blue.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<jsp:include page="../inc/easyui.jsp"></jsp:include>


</head>
<%
String startTime = StringUtil.convertNull((String)request.getAttribute("startTime"));
String endTime = StringUtil.convertNull((String)request.getAttribute("endTime"));
String storage = StringUtil.convertNull((String)request.getAttribute("storage"));
String time = StringUtil.convertNull((String)request.getAttribute("time"));
List detailedList = (List)request.getAttribute("list");
%>
<script type="text/javascript" charset="UTF-8">
$(function() {
	$('#storage').combobox({   
	url:'<%=request.getContextPath()%>/CheckOrderStatJobController/getStorageList.mmx',
	valueField : 'id',   
	textField : 'storageName',
	panelHeight:'auto'
}); 
	<%if("".equals(storage)){%>
		$('#storage').combobox('setValue','-1');
	<%}else{%>
		$('#storage').combobox('setValue','<%=storage%>');
	<%}%>
		 $('#startTime').datebox({ 
				required:true,
				editable:false
			}); 
		    $('#endTime').datebox({ 
		    	required:true,
		    	editable:false
			}); 
});
function checksubmit(){
	var startTime = $('#startTime').datebox("getValue");
	var endTime = $('#endTime').datebox("getValue");
	var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	if(startTime.length!=0&&endTime.length==0 ){
	    alert("请输入截止时间")
		return false;
	}
	if(startTime.length==0&&endTime.length!=0 ){
		alert("请输入起始时间")
		return false;
	}
	var nDay_ms=24*60*60*1000;
	var reg=new RegExp("-","g");
	var startDay=new Date(startTime.replace(reg,'/'));
	var endDay=new Date(endTime.replace(reg,'/'));
	var nDifTime=endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		alert("起始日期不能大于或等于结束日期！");
    	return false;
	}
	return true;
}
	</script>
<body>
  
<form id="searchForm" onSubmit="return checksubmit();">&nbsp;&nbsp;&nbsp;&nbsp;
		<div id="container" style="min-width: 1120px; height: 400px; margin: 0 auto;overflow:auto;">
		<script type="text/javascript">
  		var chart;
    	$(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                type: 'spline',
                marginRight: 130,
                marginBottom: 25,
                backgroundColor:'#FFFFFF'
            },
            title: {
                text: '出库交接',
                x: -20 //center
            },
            subtitle: {
                x: -20
            },
            <%if(detailedList!=null){%>
            xAxis: {
                categories: [<%for(int i=0;i<detailedList.size();i++){OrderTransmitJobBean bean = (OrderTransmitJobBean)detailedList.get(i);%>'<%=bean.getDate().substring(5,10)%>',<%}%>]
            },
            <%}%>
            yAxis: {
                title: {
                    text: '出库交接订单数量'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#red'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        this.x +': '+ this.y +'';
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
            <%if(detailedList!=null){%>
            series: [{
                name: '订单数',
                data: [<%for(int i=0;i<detailedList.size();i++){OrderTransmitJobBean bean = (OrderTransmitJobBean)detailedList.get(i);%><%=bean.getOrderCount()%>,<%}%>]
            }]
            <%}%>
        });
    });
    function queryData(x){
    	if(checksubmit()){
	    	var startTime = $('#startTime').datebox("getValue");
			var endTime =$('#endTime').datebox("getValue");
			var storage = $("#storage").combobox("getValue"); 
			var time = document.getElementsByName("time");
			var values="";
			for(var i = 0;i<time.length;i++){
				if(time[i].checked==true){
					values=time[i].value;			
				}		
			}
			location.href = '${pageContext.request.contextPath}/OrderTransmitJobController/queryList.mmx?startTime='+startTime+'&endTime='+endTime+'&storage='+storage+'&time='+values+'&flag='+x;;
    	}
    }
</script>
</div>
	</form>
	  <form id="re">
				<table  class="tableForm">
					
					<tr>
				        <td>
				        	<select name="storage" id="storage" class="easyui-combobox" panelHeight="auto" style="width:100px"></select>
				        </td>
				        <td>
                                <input type=text  id='startTime' name='startTime' value="<%=startTime %>" size="10" value="" />至
		                        <input type=text  id='endTime' name='endTime'  size="10" value="<%=endTime %>" />				        
				        </td>
				        <td><input type="radio" name="time" id="time" value="day" <%if("day".equals(time)){%>checked="checked"<%} %>/>日&nbsp;&nbsp;</td>
				        <td><input type="radio" name="time" id="time" value="week" <%if("week".equals(time)){%>checked="checked"<%} %>/>周&nbsp;&nbsp;</td>
				        <td><input type="radio" name="time" id="time" value="month" <%if("month".equals(time)){%>checked="checked"<%} %>/>月&nbsp;&nbsp;</td>
				        <td><a id="btn" onclick='queryData("query")' class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a> </td>
				        <td><a id="dc" onclick='queryData("excel")' class="easyui-linkbutton" data-options="iconCls:'icon-save'">绩效导出</a> </td>
				</table>
</form>
</body>
</html>