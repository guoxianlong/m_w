<%@ page language="java" pageEncoding="UTF-8"%>
<%@page	import="adultadmin.bean.stock.ProductStockBean"%>
<%@page	import="java.util.Map,adultadmin.action.vo.*,adultadmin.bean.*,adultadmin.bean.cargo.*"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<!DOCTYPE html>
<html>
<head>
<title>进销存记录</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	var columns = "";
	<%if(group.isFlag(182)){ %>
	columns = [ [ {
		field : 'stockTypeName',
		title : '库类型',
		width : 100,
		align : 'left'
	}, {
		field : 'stockAreaName',
		title : '库区域',
		width : 100,
		align : 'left'
	}, {
		field : 'code',
		title : '单据号',
		width : 200,
		align : 'left'
	},{
		field : 'cardTypeName',
		title : '来源',
		width : 150,
		align : 'left'
	},{
		field : 'createDatetime',
		title : '时间',
		width : 250,
		align : 'left',
		formatter : function(value, row, index) {
			return value.substr(0,19);
		}
	},{
		field : 'stockInCount',
		title : '入库数量',
		width : 120,
		align : 'left',
		formatter : function(value, row, index) {
			return parseInt(value)>0 ? value : "-";
		}
	},{
		field : 'stockInPriceSumString',
		title : '入库金额',
		width : 120,
		align : 'left'
	},{
		field : 'stockOutCount',
		title : '出库数量',
		width : 120,
		align : 'left',
		formatter : function(value, row, index) {
			return parseInt(value)>0 ? value : "-";
		}
	},{
		field : 'stockOutPriceSumString',
		title : '出库金额',
		width : 120,
		align : 'left'
	},{
		field : 'currentStock',
		title : '当前结存',
		width : 120,
		align : 'left'
	},{
		field : 'stockAllArea',
		title : '本库区域总结存',
		width : 140,
		align : 'left'
	},{
		field : 'stockAllType',
		title : '本库类总结存',
		width : 140,
		align : 'left'
	},{
		field : 'allStock',
		title : '全库总结存',
		width : 140,
		align : 'left'
	},{
		field : 'stockPrice',
		title : '库存单价',
		width : 120,
		align : 'left'
	},{
		field : 'allStockPriceSumString',
		title : '结存总额',
		width : 160,
		align : 'left'
	}]];
	<%} else {%>
	columns = [ [ {
		field : 'stockTypeName',
		title : '库类型',
		width : 100,
		align : 'left'
	}, {
		field : 'stockAreaName',
		title : '库区域',
		width : 100,
		align : 'left'
	}, {
		field : 'code',
		title : '单据号',
		width : 200,
		align : 'left'
	},{
		field : 'cardTypeName',
		title : '来源',
		width : 150,
		align : 'left'
	},{
		field : 'createDatetime',
		title : '时间',
		width : 250,
		align : 'left',
		formatter : function(value, row, index) {
			return value.substr(0,19);
		}
	},{
		field : 'stockInCount',
		title : '入库数量',
		width : 120,
		align : 'left',
		formatter : function(value, row, index) {
			return parseInt(value)>0 ? value : "-";
		}
	},{
		field : 'stockOutCount',
		title : '出库数量',
		width : 120,
		align : 'left',
		formatter : function(value, row, index) {
			return parseInt(value)>0 ? value : "-";
		}
	},{
		field : 'currentStock',
		title : '当前结存',
		width : 120,
		align : 'left'
	},{
		field : 'stockAllArea',
		title : '本库区域总结存',
		width : 140,
		align : 'left'
	},{
		field : 'stockAllType',
		title : '本库类总结存',
		width : 140,
		align : 'left'
	},{
		field : 'allStock',
		title : '全库总结存',
		width : 140,
		align : 'left'
	}]];
	<%}%>
	$('#stockCardDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/ByBsController/findStockCard.mmx',
		queryParams : {
			pid : <%=request.getParameter("pid")%>
		},
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		pagination : true,
		pageSize : 50,
		pageList : [ 10, 20, 30, 40, 50 ],
		columns : columns
	});
});
</script>
</head>
<body>
	<table id="stockCardDataGrid"></table>
</body>
</html>
