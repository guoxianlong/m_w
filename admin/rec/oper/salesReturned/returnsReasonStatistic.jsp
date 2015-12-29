<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>销售退货原因统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
function cellStyler(value,row,index){
	return 'color:red;';
}
</script>
</head>
<body>
	<div class="easyui-tabs" style="width:800px;height:500px;">
	    <div title="按天统计" data-options="href:'${pageContext.request.contextPath}/SalesReturnController/returnsReasonStatistic.mmx?flag=day'"></div>
	    <div title="按月统计" data-options="href:'<%=request.getContextPath()%>/SalesReturnController/returnsReasonStatistic.mmx?flag=moon'"></div>
	    <div title="统计明细" data-options="href:'<%=request.getContextPath()%>/SalesReturnController/returnsReasonStatistic.mmx?flag=detail'"></div>
	</div>
</body>
</html>