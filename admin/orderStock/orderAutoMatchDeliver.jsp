<%@ include file="../taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*,adultadmin.util.StringUtil, adultadmin.action.vo.OrderMatchDeliverVO" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>扫描调拨单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
</head>
	<body>
		<p align="center"><b>自动匹配销售订单的快递公司的准确率试验</b></p>
		<form action="<%=request.getContextPath() %>/admin/orderMatchDeliver.do" onsubmit="return checkSubmit();" style="font-size: 12px;">
			订单创建日期：<input type=text name="orderDate" size="20" value="<%=StringUtil.convertNull(request.getParameter("orderDate"))%>" onclick="SelectDate(this,'yyyy-MM-dd');"  />
			 <input type="submit" value="自动匹配快递公司">
			<logic:present name="result" scope="request"> 
			<%
				List result = (List)request.getAttribute("result");
				OrderMatchDeliverVO matchGZ = (OrderMatchDeliverVO)result.get(0);
				OrderMatchDeliverVO matchGW = (OrderMatchDeliverVO)result.get(1);
				OrderMatchDeliverVO matchGS = (OrderMatchDeliverVO)result.get(2);
			%>
			<div>匹配结果：共<%=request.getAttribute("count") %>条记录
				<table border="1" cellpadding="0" cellspacing="0">
					<tr  bgcolor="#4688D6" style="color: #FFFFFF;">
						<td>快递公司</td>
						<td>参照数据（人工指定）</td>
						<td>广宅（系统匹配）</td>
						<td>广外（系统匹配）</td>
						<td>广速（系统匹配）</td>
					</tr>
					<tr>
						<td>广州宅急送</td>
						<td><%=matchGZ.getCountGZ()%></td>
						<td><%=matchGZ.getMatchCountGZ()%></td>
						<td><%=matchGZ.getMatchCountGW()%></td>
						<td><%=matchGZ.getMatchCountGS()%></td>
						<%--<td><%=100*(matchVO.getMatchCountGZ()/(matchVO.getCountGZ()==0?1:matchVO.getCountGZ()))%>%</td> --%>
					</tr>
					<tr>
						<td>广东省外</td>
						<td><%=matchGW.getCountGW()%></td>
						<td><%=matchGW.getMatchCountGZ()%></td>
						<td><%=matchGW.getMatchCountGW()%></td>
						<td><%=matchGW.getMatchCountGS()%></td>
						<%--<td><%=100*(matchVO.getMatchCountGW()/(matchVO.getCountGW()==0?1:matchVO.getCountGW()))%>%</td> --%>
					</tr>	
					<tr>	
						<td>广东省速递局</td>
						<td><%=matchGS.getCountGS()%></td>
						<td><%=matchGS.getMatchCountGZ()%></td>
						<td><%=matchGS.getMatchCountGW()%></td>
						<td><%=matchGS.getMatchCountGS()%></td>
						<%--<td><%=100*(matchVO.getMatchCountGS()/(matchVO.getCountGS()==0?1:matchVO.getCountGS()))%>%</td> --%>
					</tr>	
				</table>
				<br/>
				<br/>
				<b>与人工选择不一致的:</b>
				<table border="1" cellpadding="0" cellspacing="0" width="80%">
					<tr bgcolor="#4688D6" style="color: #FFFFFF;">
					<td>序号</td>
					<td>订单编号</td>
					<td>用户地址</td>
					<td>人工选择</td>
					<td>系统选择</td>
					</tr>
					<logic:iterate id="order" name="resultOrderList" scope="request"  indexId="i">
						<tr>
							<td><bean:write name="i"/></td>
							<td><bean:write name="order" property="code"/></td>
							<td><bean:write name="order" property="address"/></td>
							<td><bean:write name="order" property="deliverName"/></td>
							<td style="color: red;"><bean:write name="order" property="sysDeliver"/></td>
						</tr>
					</logic:iterate>
				</table>
			</div>
			</logic:present>			
		</form>
	</body>
</html>