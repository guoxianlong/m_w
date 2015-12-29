<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<html>
<head>
<title>发货波次</title>
<%
ProductStockBean psBean = new ProductStockBean();
ArrayList MBPlist = (ArrayList)request.getAttribute("MBPlist");
String batchCode = (String)request.getAttribute("batchCode");
String area = (String)request.getSession().getAttribute("area");
int areaId = StringUtil.toInt(area);
String result = (String)request.getAttribute("result");
%>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript">
function submitForm(){
	$('#but').attr('disabled','disabled');
	window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=createParcel&batchCode=<%=batchCode%>';
}
function getFocus(){
	document.getElementById("but").focus();
}
</script>
</head>

<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()"topmargin="2">
<div style="position:absolute; height:260px; overflow-y:auto">
<table width="220" height="220" border="0" cellspacing="0">
<tr>
	<td colspan="2" align="center"><font size="4"style="font-weight:bold">发货波次</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr>
	<td colspan="2" align="center">
	<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr >
	<td colspan="2" align="center"><%=DateUtil.getNow() %></td>
</tr>
<tr >
	<td colspan="2" align="center">当前波次：<%=batchCode%></td>
</tr>
<tr>
	<td colspan="2" align="left"><input id="but"type="button" value=" 创建邮包 " style="height:23px;width:110px;" onclick="this.disabled=disabled;submitForm();"/>
	<input type="button" value=" 返   回 " style="height:23px;width:70px;" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=dingdanchuku';"/></td>

</tr>
		<tr >
			<td  align="center">邮包编号</td>
			<td  align="center">订单数量</td>
		</tr>
<% if(MBPlist.size()>0){
		MailingBatchParcelBean MBPbean = new MailingBatchParcelBean();
		for(int i = 0;i < MBPlist.size();i++){
			MBPbean = (MailingBatchParcelBean)MBPlist.get(i);%>
			<tr>
				<td align="center"><a href="<%=request.getContextPath()%>/admin/cargo/soAddParcel.jsp?parcelCode=<%= MBPbean.getCode() %> "><%= MBPbean.getCode() %></a></td>	
				<td align="center"><%= MBPbean.getPackageCount() %></td>
			</tr>
<%		}
	}else{
%>
	<tr >
		<td  align="center">无</td>
		<td  align="center">无</td>
	</tr>
<% } %>


</table>
</div>
</body>
</html>