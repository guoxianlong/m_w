<%@page import="adultadmin.util.StringUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="java.text.DecimalFormat"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>分拣量统计</title>
</head>
<%
	SortingBatchGroupBean totalBean = (SortingBatchGroupBean) request.getAttribute("totalBean");
	List staffList = (List) request.getAttribute("staffList");
%>
<%
	response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	String fileName = "分拣量统计";
	response.setHeader("Content-disposition", "attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%> 
<body>
	<table width="99%" border="0" cellpadding="3" cellspacing="1"
		bgcolor="#4c6e92" align="center">
		<%
			if (totalBean != null) {
		%>
		<tr bgcolor="Yellow">
			<td><div align="center">总数:</div>
			</td>
			<td><div align="center"><%=totalBean.getStaffCount()%></div>
			</td>
			<td><div align="center"><%=totalBean.getAttendanceCount()%></div>
			</td>
			<td><div align="center"><%=totalBean.getGroupCount()%></div>
			</td>
			<td><div align="center"><%=totalBean.getOrderCount()%></div>
			</td>
			<td><div align="center"><%=totalBean.getSkuCount()%></div>
			</td>
			<td><div align="center"><%=totalBean.getProductCount()%></div>
			</td>
			<td><div align="center"><%=totalBean.getPassageCount()%></div>
			<td><div align="center"><%=totalBean.getCancelOrderCount()%></div></td>
			</td>
		</tr>
		<%
			}
		%>
		<tr bgcolor="#e8e8e8">
			<td><div align="center">
					<strong>姓名</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>员工号</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>出勤天数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>波次数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>订单数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>SKU数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>商品个数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>巷道数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>撤单量</strong>
				</div>
			</td>
		</tr>
		<%
			if (staffList != null) {
				for (int i = 0; i < staffList.size(); i++) {
					SortingBatchGroupBean bean = (SortingBatchGroupBean) staffList.get(i);
		%>
		<tr bgcolor="#e8e8e8">
			<td><div align="center"><%=bean.getStaffName()%></div>
			</td>
			<td><div align="center">&nbsp;<%=bean.getStaffCode()%></div>
			</td>
			<td><div align="center"><%=bean.getAttendanceCount()%></div>
			</td>
			<td><div align="center"><%=bean.getGroupCount()%></div>
			</td>
			<td><div align="center"><%=bean.getOrderCount()%></div>
			</td>
			<td><div align="center"><%=bean.getSkuCount()%></div>
			</td>
			<td><div align="center"><%=bean.getProductCount()%></div>
			</td>
			<td><div align="center"><%=bean.getPassageCount()%></div>
			</td>
			<td><div align="center"><%=bean.getCancelOrderCount()%></div>
			</td>
		</tr>
		<%
			}
			}
		%>
	</table>
</body>
</html>