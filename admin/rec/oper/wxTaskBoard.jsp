<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="refresh" content="300"> 
<%
	//昨日出库订单
	String beforeOutOrderCount = (String)request.getAttribute("beforeOutOrderCount");
	//当前已复核的订单
	String scanAllOrderCount = (String)request.getAttribute("scanAllOrderCount");
	//已接到订单
	String receiveOrderCount = (String)request.getAttribute("receiveOrderCount");
	//已撤销订单
	String cancelOrderCount = (String)request.getAttribute("cancelOrderCount");
	//已导出订单
	String exportOrderCount = (String)request.getAttribute("exportOrderCount");
	//已复核订单
	String scanOrderCount = (String)request.getAttribute("scanOrderCount");
	//已分播订单
	String secondSplitOrderCount = (String)request.getAttribute("secondSplitOrderCount");
	//已交接订单
	String joinOrderCount = (String)request.getAttribute("joinOrderCount");
	//待导出订单
	String waitExportOrderCount = (String)request.getAttribute("waitExportOrderCount");
	//待打印订单
	String waitPrintOrderCount = (String)request.getAttribute("waitPrintOrderCount");
	//分拣中订单
	String sortingOrderCount = (String)request.getAttribute("sortingOrderCount");
	//待交接订单
	String waitJoinOrderCount = (String)request.getAttribute("waitJoinOrderCount");
	//分拣异常单
	String abnormalOrderCount = (String)request.getAttribute("abnormalOrderCount");
	//分拣组
	List sortingGroup = (List)request.getAttribute("sortingGroup");
	//分播组
	List secondSplit = (List)request.getAttribute("secondSplit");
	//复核组
	List scanOrder = (List)request.getAttribute("scanOrder");
	//出库组
	List outCargo = (List)request.getAttribute("outCargo");
	//上架组
	List upshelfGroup = (List)request.getAttribute("upshelfGroup");
%>
<style type="text/css">
<!--
table tr td{
    font-size:30px;
}
-->
</style>
<script type="text/javascript" charset="UTF-8">
Date.prototype.format = function(format)
{
	 var o = {
	 "M+" : this.getMonth()+1, //month
	 "d+" : this.getDate(),    //day
	 "h+" : this.getHours(),   //hour
	 "m+" : this.getMinutes(), //minute
	 "s+" : this.getSeconds(), //second
	 "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
	 "S" : this.getMilliseconds() //millisecond
	 }
	 if(/(y+)/.test(format)) format=format.replace(RegExp.$1,(this.getFullYear()+"").substr(4 - RegExp.$1.length));
	 for(var k in o) {
		 if(new RegExp("("+ k +")").test(format)) {
	 		format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
		 }
	 }
	 return format;
}
var servertime=new Date();
servertime.setTime(<%=System.currentTimeMillis()%>); 
var timeDifference=new Date()-servertime;
var MyInterval=setInterval("Refresh()",1000);
function Refresh(){
	var a = new Date();
	a.setTime(a.getTime()-timeDifference);
	var elem = document.getElementById("time");
	elem.innerHTML=a.format("hh:mm:ss") ;
}
</script>
</head>
<body>
	<div id="deman">
		<font style="font-weight:bold;font-size:60px;display:block; text-align:center">买卖宝华东仓作业看板&nbsp;&nbsp;&nbsp;&nbsp;<label id="time"></label></font>
		<br/>
		<table align="center" border="1" cellspacing="0" cellpadding="0">
			<tr align="center">
				<td>
					<font style="font-weight:bold;font-size:40px;">订单情况</font>
				</td>
				<td>
					<font style="font-weight:bold;font-size:40px;">产能</font>
				</td>
			</tr>
			<tr>
				<td>
					<font style="font-weight:bold;font-size:14;display:block;margin:0px 0px 0px 50px;">昨天出库订单：<%=beforeOutOrderCount %></font>
					<p style="border-bottom:1px solid;width:90%;margin:10px 0px 10px 50px" align="center"/>
					<p style="font-weight:bold;font-size:14;display:block;margin:0px 0px 0px 50px;">当前已复核订单：<%=scanAllOrderCount %></p>
					<p style="border-bottom:2px dashed;width:90%;margin:30px 0px 10px 50px" align="center"/>
					<table border="1" cellspacing="0" cellpadding="0"  style="width:650px;margin:0px 50px 50px 50px;" >
						<tr>
							<td style="width:200px">已接到订单：</td>
							<td style="width:100px"><%=receiveOrderCount %></td>
							<td style="width:200px">待导出订单：</td>
							<td style="width:100px"><%=waitExportOrderCount %></td>
						</tr>
						<tr>
							<td>已撤销订单：</td>
							<td><%=cancelOrderCount %></td>
							<td>待打印订单：</td>
							<td><%=waitPrintOrderCount %></td>
						</tr>
						<tr>
							<td>已导出订单：</td>
							<td><%=exportOrderCount %></td>
							<td>分拣中订单：</td>
							<td><%=sortingOrderCount %></td>
						</tr>
						<tr>
							<td>已复核订单：</td>
							<td><%=scanOrderCount %></td>
							<td>待交接订单：</td>
							<td><%=waitJoinOrderCount %></td>
						</tr>
						<tr>
							<td>已分播订单：</td>
							<td><%=secondSplitOrderCount %></td>
							<td>分拣异常单：</td>
							<td><%=abnormalOrderCount %></td>
						</tr>
						<tr>
							<td>已交接订单：</td>
							<td><%=joinOrderCount %></td>
							<td></td>
							<td></td>
						</tr>
					</table>
				</td>
				<td>
					<table border="1" cellspacing="0" cellpadding="0"  style="width:500px;margin:0px 50px 50px 50px;">
						<tr>
							<td align="center">组别</td>
							<td align="center">人数</td>
							<td align="center">完成量</td>
							<td align="center">冠军</td>
							<td align="center">完成量</td>
						</tr>
						<tr>
							<td align="center">分拣组</td>
							<td><%=sortingGroup.get(3) %></td>
							<td><%=sortingGroup.get(2) %></td>
							<td><%=sortingGroup.get(0) %></td>
							<td><%=sortingGroup.get(1) %></td>
						</tr>
						<tr>
							<td align="center">分播组</td>
							<td><%=secondSplit.get(3) %></td>
							<td><%=secondSplit.get(2) %></td>
							<td><%=secondSplit.get(0) %></td>
							<td><%=secondSplit.get(1) %></td>
						</tr>
						<tr>
							<td align="center">复核组</td>
							<td><%=scanOrder.get(3) %></td>
							<td><%=scanOrder.get(2) %></td>
							<td><%=scanOrder.get(0) %></td>
							<td><%=scanOrder.get(1) %></td>
						</tr>
						<tr>
							<td align="center">出库组</td>
							<td><%=outCargo.get(3) %></td>
							<td><%=outCargo.get(2) %></td>
							<td><%=outCargo.get(0) %></td>
							<td><%=outCargo.get(1) %></td>
						</tr>
						<tr>
							<td align="center">上架组</td>
							<td><%=upshelfGroup.get(3) %></td>
							<td><%=upshelfGroup.get(2) %></td>
							<td><%=upshelfGroup.get(0) %></td>
							<td><%=upshelfGroup.get(1) %></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>