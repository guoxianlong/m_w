<%@page import="adultadmin.util.StringUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="mmb.dcheck.model.DynamicCheckCargoBean"%>
<%@ page import="java.text.DecimalFormat"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>导出盘点明细</title>
</head>
<%
	
	List dynamicCheckCargoBeans = (List) request.getAttribute("dynamicCheckCargoBeans");
%>
<%
	response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	String fileName = "盘点明细";
	response.setHeader("Content-disposition", "attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%> 
<body>
	<table width="99%" border="0" cellpadding="3" cellspacing="1"
		bgcolor="#4c6e92" align="center">
	   <tr bgcolor="#e8e8e8">
			<td><div align="center">
					<strong>货位号</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>产品编号</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>产品原名称</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>一盘差异数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>一盘人</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>二盘差异数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>二盘人</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>终盘差异数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>终盘人</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>盘点次数</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>盘点结果</strong>
				</div>
			</td>
			<td><div align="center">
					<strong>状态</strong>
				</div>
			</td>
		</tr>
		<%
			if (dynamicCheckCargoBeans != null) {
				for (int i = 0; i < dynamicCheckCargoBeans.size(); i++) {
					DynamicCheckCargoBean bean = (DynamicCheckCargoBean) dynamicCheckCargoBeans.get(i);
		%>
		<tr bgcolor="#e8e8e8">
			<td><div align="center"><%=bean.getCargoWholeCode()%></div>
			</td>
			<td><div align="center">&nbsp;<%=bean.getProductCode()%></div>
			</td>
			<td><div align="center"><%=bean.getProductName()%></div>
			</td>
			<td><div align="center"><%=bean.getDifference1()%></div>
			</td>
			<td><div align="center"><%=bean.getCheckUsername1()%></div>
			</td>
			<td><div align="center"><%=bean.getDifference2()%></div>
			</td>
			<td><div align="center"><%=bean.getCheckUsername2()%></div>
			</td>
			<td><div align="center"><%=bean.getDifference3()%></div>
			</td>
			<td><div align="center"><%=bean.getCheckUsername3()%></div>
			</td>
			<td><div align="center"><%=bean.getEndCheckTimes()%></div>
			</td>
					<%  
			String result_="";
			if(bean.getCheckResult()==-1){
				result_="未盘点";
			}
			if(bean.getCheckResult()==0){
				result_="待盘点";
			}
			if(bean.getCheckResult()==1){
				result_="无差异";
			}
			if(bean.getCheckResult()==2){
				result_="盘盈";
			}
			if(bean.getCheckResult()==3){
				result_="盘亏";
			}
			%>
			<td><div align="center"><%=result_%></div>
			</td>
			<%  
			String status_="";
			if(bean.getStatus()==-1){
				status_="未盘点";
			}
			if(bean.getStatus()==0){
				status_="待盘点";
			}
			if(bean.getStatus()==1){
				status_="待二盘";
			}
			if(bean.getStatus()==2){
				status_="待终盘";
			}
			if(bean.getStatus()==3){
				status_="盘点完成";
			}
			%>
			<td><div align="center"><%=status_%></div>
			</td>
		</tr>
		<%
			}
			}
		%>
	</table>
</body>
</html>