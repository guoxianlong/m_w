<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
</script>
<div class="easyui-tabs" fit="true">
    <div title="增城地区" data-options="href:'${pageContext.request.contextPath}/AreaStockExchangeController/toQueryExchangeProductList.mmx?type=1'"></div>
    <div title="待调入列表" data-options="href:'${pageContext.request.contextPath}/AreaStockExchangeController/toGetExchangeList.mmx?type=1&flag=0'"></div>
    <div title="待调出列表" data-options="href:'${pageContext.request.contextPath}/AreaStockExchangeController/toGetExchangeList.mmx?type=1&flag=1'"></div>
</div>
