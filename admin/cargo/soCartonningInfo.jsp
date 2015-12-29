<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<html>
<head>
<title>装箱管理</title>
<%
ProductStockBean psBean = new ProductStockBean();
CartonningInfoBean cartonningBean = (CartonningInfoBean)request.getAttribute("cartonningBean");
CargoOperationBean cargoOperationBean = (CargoOperationBean)request.getAttribute("cargoOperationBean");
CargoOperationCargoBean cargoOperationCargoBean = (CargoOperationCargoBean)request.getAttribute("cargoOperationCargoBean");
CargoInfoBean cargoInfoBean = (CargoInfoBean)request.getAttribute("cargoInfoBean");
String area = (String)request.getSession().getAttribute("area");
int areaId = StringUtil.toInt(area);
String result = (String)request.getAttribute("result");
%>
<script type="text/javascript">
function checkText(){
	var cartonningCode = document.getElementById("cartonningCode").value;
	if(cartonningCode != "" && cartonningCode != "扫描装箱单编号"){
		document.forms[0].submit();
	}else{
		alert("装箱单编号不能为空!");
		document.getElementById("cartonningCode").focus();
		return false;
	}
}
function checkEnter(){
	if(window.event.keyCode == 13){
		var cartonningCode = document.getElementById("cartonningCode").value;
		if(cartonningCode != "" && cartonningCode != "扫描装箱单编号"){
			document.forms[0].submit();
		}else{
			alert("装箱单编号不能为空!");
			document.getElementById("cartonningCode").focus();
			return false;
		}
	}
}
function getFocus(){
	<%
		if(area==null){
	%>
			alert("登陆超时,请重新登录!");
			window.location='stockOperation.do?method=logout';
	<%
		}
	%>
	document.getElementById("cartonningCode").value = "";
	document.getElementById("cartonningCode").focus();
}
function clearText(){
	document.getElementById("cartonningCode").value="";
}
function prompt(){
	var operationCode = document.getElementById("cartonningCode").value;
	if(operationCode == ""){
		document.getElementById("cartonningCode").value ="扫描装箱单编号";
	}
}
</script>
</head>

<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()"topmargin="2">
<table width="220" height="200" border="0" cellspacing="0">
<tr>
	<td colspan="2" align="center"><font size="4"style="font-weight:bold">装箱单管理</font>
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

<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=findCartonningInfo" method="post">
	<tr>
		<td colspan="2" align="center"><input type="text"  size="19" id="cartonningCode" name="cartonningCode" onblur="prompt()" onkeypress="javascript:return checkEnter()" onfocus="clearText()" /> <input type="button" value="查  询" onclick="checkText()"></td>
	</tr>
<% if(cartonningBean != null && cargoInfoBean != null){ %>
	<td colspan="2"> <textarea cols="26" rows="5" style="overflow-x:hidden;overflow-y:hidden" style= "font-size:10pt;   color:#FF0000 "  readonly="true">SKU:<%=cartonningBean.getProductBean().getProductCode()%> 数量:<%=cartonningBean.getProductBean().getProductCount()%>&#13;&#10;作业单:<% if(cargoOperationBean != null){ %><%=cargoOperationBean.getCode()%><% }else{ %>未关联作业单<% } %>&#13;&#10;关联货位:<%=cargoInfoBean.getWholeCode()%>&#13;&#10;目的货位:<% if(cargoOperationBean != null){ %> <%=cargoOperationCargoBean.getInCargoWholeCode()%><% }else{ %>未关联目的货位<% } %>&#13;&#10;作业状态:<% if(cargoOperationBean != null){ if(cargoOperationBean.getStatus()==6){%>完成<%}else{%>未完成<%} }else{ %>未知<% } %></textarea></td>
	</tr>
<% }else{ 
		if(result != null){%>
	<tr >
		<td colspan="2" align="center"> <textarea cols="26" rows="5" style="overflow-x:hidden;overflow-y:hidden" readonly="true"><%=result  %></textarea></td>
	</tr>
	  <%}else{%>
	  		<tr >
				<td colspan="2" align="center"> <textarea cols="26" rows="5" style="overflow-x:hidden;overflow-y:hidden" readonly="true"></textarea></td>
			</tr>
<%		} 
} %>	
	<tr>
		<td align="center"><input type="button" value="创质检装箱" onclick="window.location='stockOperation.do?method=operationPageJump&toPage=createQualityCartonning'"/></td>
		<td align="center"><input type="button" value="创作业装箱" onclick="window.location='stockOperation.do?method=operationPageJump&toPage=creatCartonning'"/></td>
	</tr>
	<tr>
		<td align="center"><input type="button" value="作废装箱单" onclick="window.location='stockOperation.do?method=operationPageJump&toPage=dropCartonning'"/></td>
		<td align="center"><input type="button" value="打印装箱单" onclick="window.location='stockOperation.do?method=operationPageJump&toPage=printCartonning'"/></td>
	</tr>
	<tr >
		<td align="center" colspan="2"> <input type="button" value=" 返   回 " style="height=23px;width=100px;" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=pdaOperation';"/></td>
	</tr>
</form>
</table>

</body>
</html>