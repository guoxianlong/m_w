<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="adultadmin.action.vo.*,mmb.rec.stat.bean.StockShareBean, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<html>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<head>
<title>库存统计占比</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
</head>
<%
List productLineList = (List)request.getAttribute("productLineList");
List stocksharelist = (List)request.getAttribute("stocksharelist");
String startTime = (String)request.getAttribute("startTime");
String endTime = (String)request.getAttribute("endTime");

int area = (Integer)request.getAttribute("area");
String timegroup = (String)request.getAttribute("timegroup");
List lines = (List)request.getAttribute("lines");
%>
<script type="text/javascript">
function fillTime(){
	$("#startTime").datebox('setValue',"<%=startTime%>");
	$("#endTime").datebox('setValue',"<%=endTime%>");
}
function checkAllProduct() {
	if ($("#all").attr("checked")){
       	$("[name = productLine]:checkbox").attr("checked", true);
	} else {
		$("[name = productLine]:checkbox").attr("checked", false);
	}
}
</script>
<body onload="fillTime();">
<div id="container" style="min-width: 310px; height: 550px; margin: 0 auto"></div>
<fieldset style="border : 1px solid #ff9900;text-align:left;COLOR:#ff9900;FONT-SIZE: 12px;font-family: Verdana">
	<form method="post" id="form" action="<%=request.getContextPath()%>/stockShareController/stockShareDetails.mmx" >&nbsp;&nbsp;&nbsp;&nbsp;
		<table>
			<tr>
				<td>
					<span>产品线：</span>
				</td>
				<td colspan="3">
				<%if(productLineList!=null&&productLineList.size()>0){
					%>
					<input type="checkbox" name="all" onclick="checkAllProduct();" id="all"/><label for="all">全选</label>
					<%
					for(int i=0;i<productLineList.size();i++){
						voProductLine proLineBean = (voProductLine)productLineList.get(i);
					%>
					<input type="checkbox" name="productLine" <%if(lines.contains(proLineBean.getId())){%>checked="chedked"<%} %> value="<%=proLineBean.getId()%>" id="<%=proLineBean.getId()%>"/><label for="<%=proLineBean.getId()%>"><%=proLineBean.getName() %></label>
					<%
					}
				}
				%>
				</td>
			</tr>
			<tr>
				<td><input name="area" style="width:80px;" class="easyui-combobox" editable="false" id="area"></td>
				<td><input name="startTime" id="startTime" type="text" editable="false" class="easyui-datebox">
				   --<input name="endTime" id="endTime" type="text" editable="false" class="easyui-datebox"></td>
				<td><input type="radio" name="timegroup" value="1" <%if(!"2".equals(timegroup)||!"3".equals(timegroup)){%> checked="true"<%}%>>日
					<input type="radio" name="timegroup" value="2" <%if("2".equals(timegroup)){%> checked="true"<%}%>>周
					<input type="radio" name="timegroup" value="3" <%if("3".equals(timegroup)){%> checked="true"<%}%>>月</td>
				<td><a id="query" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a></td>
			</tr>
		</table>
	</form>
</fieldset>

<script type="text/javascript">
    var chart;
  //chart.redraw();
    $(document).ready(function() {
//     	$.ajax({
//     		url : '${pageContext.request.contextPath}/SendOutOrderStatController/getProductLines.mmx',
//     		type : 'post',
//     		dataType : 'json',
//     		cache : false,
//     		success : function(d){
//     			$('#productLines').append(d.obj);
//     		}
//     	});
    	$("#query").click(function(){
    		if($("input[type='checkbox'][name='productLine']:checked").length==0){
    			$.messager.show({
    				title:'提示',
    				msg:'请选择至少一个产品线！',
    				timeout:3000,
    				showType:'slide'
    			});
    			return false;
    		}
    		$("#form").submit();
    	});
        $("#area").combobox({
        	url:'<%=request.getContextPath()%>/stockShareController/getDeptAreaComboBox.mmx',
        	valueField:'id',
			textField:'text',
			onLoadSuccess:function(record){
				$("#area").combobox('select',"<%=area%>");
			}
        });
    	
        $('#container').highcharts({
        	chart:{
//         		marginLeft:50,
//         		marginRight:200
        	},
            title: {
                text: '库存占比',
                x: -20 //center
            },
//             subtitle: {
<%--                 text: '(<%=staffName%>)', --%>
//                 x: -20
//             },
            <%if(stocksharelist!=null){%>
            xAxis: {
                categories: [<%for(int i=0;i<stocksharelist.size();i++){StockShareBean bean = (StockShareBean)stocksharelist.get(i);%>'<%=bean.getDate()%>',<%}%>]
            },
            <%}%>
            yAxis: {
                title: {
                    text: '库存百分比'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        this.x +': '+ this.y +'%';
                }
            },
//             legend: {
//                 layout: 'vertical',
//                 align: 'right',
//                 verticalAlign: 'top',
//                 x: -10,
//                 y: 100,
//                 borderWidth: 0
//             },
            legend: {
		            layout: 'horizontal',
		            align: 'center',
		            borderWidth: 0
		     },
            <%if(stocksharelist!=null){%>
            series: [{
                name: '金额',
                data: [<%for(int i=0;i<stocksharelist.size();i++){StockShareBean bean = (StockShareBean)stocksharelist.get(i);%><%=bean.getPrice()%>,<%}%>]
            }, {
                name: 'SKU',
                data: [<%for(int i=0;i<stocksharelist.size();i++){StockShareBean bean = (StockShareBean)stocksharelist.get(i);%><%=bean.getSkuSum()%>,<%}%>]
            }, {
                name: '件数',
                data: [<%for(int i=0;i<stocksharelist.size();i++){StockShareBean bean = (StockShareBean)stocksharelist.get(i);%><%=bean.getProductSum()%>,<%}%>]
            }]
            <%}%>
        });
    });
    
</script>
<br>
</body>
</html>