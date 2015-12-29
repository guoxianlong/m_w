<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="adultadmin.util.db.DbOperation"%>
<%@page import="adultadmin.util.*" %>
<%
String tableName = StringUtil.convertNull(request.getParameter("tableName"));//操作的表名
DbOperation dbOper = null;
String tableSource = "";
int type = -1;
int length = 0;
boolean update = false;
if (!StringUtil.isNull(tableName)) {
	if (tableName.equals("buy_plan")) {
		tableSource = "采购订单，来源采购计划：";
		type = 3;
		length = 13;
	} else if (tableName.equals("buy_order")) {
		tableSource = "预计到货表，来源采购订单：";
		type = 2;
		length = 12;
	} else if (tableName.equals("buy_stock")) {
		tableSource = "采购入库单，来源预计到货表：";
		type = 4;
		length = 12;
	} else {
	
%>
<script type="text/javascript">
<!--
	alert("表名错误！");
	window.history.back(-1);
//-->
</script>
<%
	}
	try {
		dbOper = new DbOperation();
		dbOper.init();
		String bahSql = "select count(bah.id) counts, right(bah.remark, "+length+") code from buy_admin_history bah "
			+ "where bah.remark like '转换成" + tableSource + "%' and bah.log_type = "+type+" and bah.type = 1 group by code";
		String updateSql = "update " + tableName + " a, (" + bahSql + ") b set "
					+ "a.transform_count = b.counts where a.code = b.code";
		update = dbOper.executeUpdate(updateSql);
	} finally {
		dbOper.release();
	}
	if (update) {
	%>
		<script type="text/javascript">
		<!--
		alert("更新<%=tableName %>表完成！");
		//-->
		</script>
	<%
	} else {
	%>
		<script type="text/javascript">
		<!--
		alert("更新<%=tableName %>表出错！");
		//-->
		</script>
	<%
	}
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>买卖宝后台</title>
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
  </head>
  <body align="center">
	<%@include file="../../header.jsp"%>
	<fieldset style="width:80%">
		<legend>操作说明</legend>
		本功能是为根据历史操作记表(buy_admin_history)中的记录，更新《采购计划表(buy_plan)》、《采购订单表(buy_order)》、《预计到货表(buy_stock)》中转换次数transform_count字段的信息
	</fieldset>
	<form name="updateForm" action="" method="post">
		<table >
			<tr>
				<td colspan="2">
					
				</td>
			</tr>
			<tr>
				<td>
					表名：
				</td>
				<td>
					<input type="text" name="tableName" value="" />&nbsp;&nbsp;
					<input type="submit" value="提交" />
				</td>
			</tr>
		</table>
	</form>
  </body>
</html>
