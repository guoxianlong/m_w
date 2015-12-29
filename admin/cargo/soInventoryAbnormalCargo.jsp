<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="mmb.stock.stat.AbnormalCargoCheckProductBean"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>异常货位盘点</title>
<%
ProductStockBean psBean = new ProductStockBean();
List<AbnormalCargoCheckProductBean> accpBeanList = (ArrayList<AbnormalCargoCheckProductBean> )request.getAttribute("accpBeanList");
int areaId = StringUtil.toInt((String)request.getSession().getAttribute("area"));
String error = (String)request.getAttribute("error");
%>
<style>
.font{font-size:12px}
</style>
<script type="text/javascript">
<%if(error != null){%>
	alert("<%=error%>");
<%}%>
function getFocus(){
	document.getElementById("count").value="";
	document.getElementById("count").focus();
}
function submit1(){
	if(window.event.keyCode == 13){
		if(document.getElementById("count").value != null){
			document.forms[1].submit();
		}
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()">
<div style="width:220px;height:270px;border-style:solid;border-width:0px;border-color:#000000;margin-top:-10px;">
<table width="220px" height="240px" border="1" tyle="overflow:scroll;"  class="font">
	<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=InventoryAbnormalCargoList" method="post">
	<tr height="10%">
		<td align="left" colspan="3" width="60%"><font size="3" style="font-weight:bold">异常货位盘点</font><font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td></td>
		<td align="right"  colspan="2" width="50%"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
		[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
	</tr>
	<tr height="10%">
		<td align="left" colspan="4"><input type="text" id="abnormalCargoCheckCode" disabled="disabled" name="abnormalCargoCheckCode" value="" size="22"/></td>
		<td align="right"  colspan="1"><input type="submit" disabled="disabled" value="查  询" size="9"/></td>
	</tr>
	</form>
	<% if(accpBeanList != null && accpBeanList.size() > 0){%>
	<tr height="10%">
		<td align="center" colspan="2" >商品编号</td>
		<td align="center" colspan="2" >货	位</td>
		<td align="center" width="25%" >盘点量</td>
	</tr>
	<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=InventoryAbnormalCargo" method="post">
	<tr height="10%">
		<%AbnormalCargoCheckProductBean accpBean = accpBeanList.get(0);%>
		<input type="hidden" id="accpId" name="accpId" value="<%=accpBean.getId()%>"/>
		<input type="hidden" id="productCode" name="productCode" value="<%=accpBean.getProductCode()%>"/>
		<td align="center" colspan="2"><%=accpBean.getProductCode()%></td>
		<td align="center" colspan="2"><%=accpBean.getCargoWholeCode() %><input type="hidden" id="wholeCode" name="wholeCode" value="<%=accpBean.getCargoWholeCode() %>"/></td>
		<td align="center" colspan="1"><input type="text" id="count" name="count" value="" size="1" onkeypress="javascript:return submit1()"/> </td>
	</tr>
	<tr height="10%" width="100%">
		<td align="left" colspan="5"><%=accpBean.getProductName() %></td>
	</tr>
	</form>
	<tr height="10%" width="100%" >
		<td align="left" colspan="5">下几个盘点货位:</td>
	</tr height="10%">
			<%for(int i=1;i<accpBeanList.size();i++){ 
				accpBean = accpBeanList.get(i);
			%>
	<tr>
		<td align="center" colspan="3"><%=accpBean.getCargoWholeCode() %></td>
		<td align="center" colspan="2"><%=accpBean.getProductCode()%></td>
	</tr>
			<% } 
	}else{ %>
	<tr height="90%">
		<td colspan="5">
			
		</td>
	</tr>
	<%} %>
</table>
<div align="right" style="width:220px ">
	<input type="button" value="返回" style="height:20px" size="10" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=inventoryChoose';"/>
	</div>
</div>
</body>
</html>