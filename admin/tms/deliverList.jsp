<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css"/>
<title>快递公司列表页面</title>
</head>
<body>
	<div>
		<form id="form" method="post" action="${pageContext.request.contextPath}/deliverController/queryDeliver.mmx">
			<input id="delivers" name="deliverId" editable="false" style="width: 155px;"/>&nbsp;&nbsp;
			<select name="status">
				<option value="-1">全部状态</option>
				<option value="0" ${status==0?'selected':''}>停用</option>
				<option value="1"  ${status==1?'selected':''}>启用中</option>
			</select>
			<input type="submit" value="查询" />&nbsp;&nbsp;&nbsp;&nbsp;
			<a class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="addDeliver();" href="javascript:void(0);">添加快递公司</a>
		</form>
	</div>
	<table class="gridtable">
		<tr>
			<td>快递公司</td>
			<td>配送区域</td>
			<td>添加时间</td>
			<td>状态</td>
			<td>最后操作时间</td>
			<td>操作日志</td>
		</tr>
			<c:forEach var="deliver" items="${deliverList}">
				<tr>
					<td><a href="${pageContext.request.contextPath}/deliverController/editDeliver.mmx?deliverId=${deliver.id}">${deliver.name}</a></td>
					<td>${deliver.deliverArea}</td>
					<td>${deliver.createDatetime}</td>
					<td>${deliver.statusName}</td>
					<td>${deliver.lastOperDatetime}</td>
					<td><a href="${pageContext.request.contextPath}/deliverController/queryDeliverLog.mmx?deliverId=${deliver.id}&type=0" target="_blank">查看</a></td>
				</tr>
			</c:forEach>
	</table>
	<br/>
	<table style="border:1px dashed #000;">
		<c:choose>
			<c:when test="${not empty logList}">
				<tr>
				<td>一周内更新操作日志：</td>
				<td>
					<c:forEach items="${logList}" var="log">
						${log.createDatetime}&nbsp;${log.userName}&nbsp;${log.content}<br/>
					</c:forEach>
				</td>
			</c:when>
			<c:otherwise>
				还没有操作日志
			</c:otherwise>
		</c:choose>
	</table>
</body>
</html>
<script type="text/javascript">
	$(function(){
		$("#delivers").combobox({
			url : '${pageContext.request.contextPath}/Combobox/getDeliverList.mmx',
			valueField:'id',
			textField:'text'
		});
	});
	
	function addDeliver(){
		window.location.href = "${pageContext.request.contextPath}/admin/tms/addDeliver.jsp";
	}
	
</script>
