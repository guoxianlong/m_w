<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<%@ include file="../rec/inc/easyui.jsp" %>
<!DOCTYPE html>
<html>
<head>
<title>订单配送日志查询</title>
<script type="text/javascript">
function searchFun() {
	var deliverCode = $("#deliverCode").val();
	var orderCode = $("#orderCode").val();
	var popOrderCode = $("#popOrderCode").val();
	if ($.trim(deliverCode) == "" && $.trim(orderCode) == ""&& $.trim(popOrderCode) == "") {
		alert("请填写至少一项信息");
		return false;
	}
	document.searchForm.submit();
}
$(function(){
	var tip = '${tip}';
	if(tip!=null&&tip!=""){
		alert(tip);
	}
});

</script>
</head>
<body>
	<fieldset>
		<legend>筛选</legend>
		<form id="searchForm" name="searchForm" action="${path}/OrderStockController/getPOPDeliverInfo.mmx">
			<table >
				<tr>
					<th>包裹单号：</th>
					<td>
						<input name='deliverCode' id='deliverCode' value="${deliverCode}" style="width:152px"/>
					</td>
					<th>mmb订单号：</th>
					<td>
						<input name='orderCode' id='orderCode' value="${orderCode}"  style="width:152px"/>
					</td>
					<th>京东订单号：</th>
					<td>
						<input name='popOrderCode' id='popOrderCode' value="${popOrderCode}"  style="width:152px"/>
					</td>
					<td>
						<input type="button" value="查询" onclick="searchFun();"></input>
					</td>
				</tr>
			</table>
		</form>
	</fieldset>
	 
	<c:if test="${deliverInfo ne null}">
		<table cellspacing="20">
			<tr>
				<td>POP商家:${deliverInfo.getPopName()}</td>
				<td>快递公司:${deliverInfo.getDeliveryName()}</td>
				<td>包裹单号:${deliverInfo.deliverCode}</td>
				<td>mmb订单号:${deliverInfo.orderCode}</td>
				<td>京东订单号:${deliverInfo.popOrderCode}</td>
			</tr>
			<tr>
				<td>当前状态:${deliverInfo.getDeliverStateName()}</td>
				<td>剩余时间: <fmt:formatNumber value="${deliverInfo.effectTime/24-deliverInfo.usedTime/24}" type="currency" pattern="#0.00"/>天</td>
				<td>是否超期:
					<c:choose>
						<c:when test="${deliverInfo.usedTime > deliverInfo.effectTime}"><font color="#ff0000">是</font></c:when>
						<c:otherwise>否</c:otherwise>
					</c:choose>
				</td>
				<td>配送用时/标准时效:
					<fmt:formatNumber value="${deliverInfo.usedTime/24}" type="currency" pattern="#0.00"/>/
				 	<fmt:formatNumber value="${deliverInfo.effectTime/24}" type="currency" pattern="#0.00"/>天
				</td>
			</tr>
			<tr><td colspan="8"><hr></td></tr>
			<c:forEach items="${list}" var="deliverInfo" >
				<tr>
					<td colspan="2">${deliverInfo.time}</td>
					<td colspan="2">${deliverInfo.deliverInfo}</td>
				</tr>
				<tr><td colspan="8"><hr></td></tr>
			</c:forEach>
		</table>
	</c:if>
</body>
</html>