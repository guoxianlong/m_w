<%@page pageEncoding="utf-8" %>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager" %>
<%@include file="../rec/inc/easyui.jsp" %>
 
<table id="mainList" class="dataTable" border="1">
<thead>
	<tr><td colspan="15" align="right">总共有${dataLength}条记录</td></tr>
  	<tr class="title">
  		<td>序号</td>
  	  	<td>POP商家</td>
  	  	<td>发货仓</td>
  	  	<td>快递公司</td>
  	  	<td>省</td>
  	  	<td>市</td>
  	  	<td>区</td>
  	  	<td>乡镇</td>
  	  	<td>包裹单号</td>
  	  	<td>mmb订单号</td>
  	  	<td>京东订单号</td>
  	  	<td>发货时间</td>
  	  	<td>状态</td>
  	  	<td>节点时间</td>
  	  	<td>配送信息</td>
  	</tr>
  	<c:forEach var="deliverInfo" items="${deliverInfoList }" varStatus="vs">
  		<tr align="center">
  			<td>${vs.index+1}</td>
  			<td>${deliverInfo.getPopName()}</td>
  			<td>${deliverInfo.getStorageName()}</td>
  			<td>${deliverInfo.getDeliveryName()}</td>
  			<td>${deliverInfo.province}</td>
  			<td>${deliverInfo.city}</td>
  			<td>${deliverInfo.district}</td>
  			<td></td>
  			<td align="left">${deliverInfo.deliverCode}</td>
  			<td align="left">${deliverInfo.orderCode}</td>
  			<td align="left">${deliverInfo.popOrderCode}</td>
  			<td>${deliverInfo.deliverTime}</td>
  			<td>${deliverInfo.getDeliverStateName()}</td>
  			<td>${deliverInfo.time}</td>
  			<td align="left" width="200px">
  				<div title="${deliverInfo.deliverInfo}" style="white-space: nowrap; text-overflow: ellipsis; overflow: hidden;width:200px;">
   					 ${deliverInfo.deliverInfo}
   				</div>	
  			</td>
  		</tr>
  	</c:forEach>
</thead>
<tbody>	
</tbody>
</table>

<script>
$(function(){
	
	var tip = '${tip}';
	if(tip!=null&&tip!=""){
		alert(tip);
	}
	
	var notPOPCode = '${notPOPCode}';
	if(notPOPCode != null&&notPOPCode!=""){
		alert(notPOPCode+"不是POP商家的订单");
	}
});
</script>
 