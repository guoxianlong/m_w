<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8">
<title>创建作业装箱单</title>
<% 
ProductStockBean psBean = new ProductStockBean();
String result = request.getParameter("result");
if(result == null){
	result = (String)request.getAttribute("result");
}
String area = (String)request.getSession().getAttribute("area");
int areaId = StringUtil.toInt(area);
String code = request.getParameter("code");
String wholeCode = (String)request.getAttribute("wholeCode");
%>
<script type="text/javascript">
function submitProductEnter(){
	if(window.event.keyCode == 13){
		var productCode = document.getElementById("productCode").value;
		if(productCode != "" && productCode != "扫描商品编号"){
			document.forms[0].submit();
		}else{
			alert("商品编号不能为空！");
			document.getElementById("productCode").focus();
		}
	}
}
function submitProductButton(){
	var productCode = document.getElementById("productCode").value;
	if(productCode != "" && productCode != "扫描商品编号"){
		document.forms[0].submit();
		setTimeout("window.location='<%=request.getContextPath()%>/admin/cargo/soCreateCartonning.jsp';",5000);
	}else{
		alert("商品编号不能为空！");
		document.getElementById("productCode").focus();
	}
}
function submitWholeEnter(){
	if(window.event.keyCode == 13){
		//alert("13");
		var wholeCode = document.getElementById("wholeCode").value;
		if(wholeCode != "" && wholeCode != "扫描货位编号"){
			document.forms[0].submit();
		}else{
			alert("货位编号不能为空！");
			document.getElementById("wholeCode").focus();
		}
	}
}
function submitWholeButton(){
	var wholeCode = document.getElementById("wholeCode").value;
	if(wholeCode != "" && wholeCode != "扫描货位编号"){
		document.forms[0].submit();
	}else{
		alert("货位编号不能为空！");
		document.getElementById("wholeCode").focus();
	}
}
function clearProduct(){
	document.getElementById("productCode").value="";
}
function getFocus(){
	<%
		if(code != null){
	%>  	var sel = confirm("装箱单是否已打印？")
			if(sel == false){
				window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=delCartonning&flag=operation&cartonningCode=<%=code%>'; 
			}
	<%
		}
		if(wholeCode != null){
	%>
			document.getElementById("productCode").focus();
	<%
		}else{
	%>
			document.getElementById("wholeCode").value = "";
			document.getElementById("wholeCode").focus();
			document.getElementById("productCode").value ="扫描商品编号";
	<%
		}
		if(area==null){
	%>
			alert("登陆超时,请重新登录!");
			window.location='stockOperation.do?method=logout';
	<%
		}
	%>
	
}
function getCountfocus(){
	if(window.event.keyCode == 13){
		document.getElementById("wholeCode").value = "";
		document.getElementById("wholeCode").focus();
		return false;
    }
}
function clearWhole(){
	<%
		if(wholeCode == null){
	%>
			document.getElementById("wholeCode").value = "";
	<%
		}
	%>
}
function promptProduct(){
	var productCode = document.getElementById("productCode").value;
	if(productCode == ""){
		document.getElementById("productCode").value ="扫描商品编号";
	}
}
function promptWhole(){
	var wholeCode = document.getElementById("wholeCode").value;
	if(wholeCode == ""){
		document.getElementById("wholeCode").value ="扫描货位编号";
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()" topmargin="9">
<table width="220" height="220" border="0" cellspacing="0">
<tr >
	<td colspan="2" align="center"><font size="4"style="font-weight:bold">作业装箱单</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr >
	<td colspan="2" align="center">
	<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr>
	<td colspan="2" align="center"><font size="2"><%=DateUtil.getNow() %></font></td>
</tr>

<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=createCartonning" method="post" >
<% if(result != null){ 
		if(code !=null){%>
			<tr height="28">
				<td colspan="2" align="center"> 
				<textarea cols="26" rows="6" style="overflow-x:hidden;overflow-y:hidden" readonly="true">作业装箱单:<%=code %>创建成功</textarea></td>
			</tr>
       <%}else{%>
 			<tr height="28">
				<td colspan="2" align="center"> 
				<textarea cols="26" rows="6" style="overflow-x:hidden;overflow-y:hidden" readonly="true"><%=result %></textarea></td>
			</tr>
 <%		}
 }else{ %>
	<tr  height="28">
		<td colspan="2" align="center">
			<% if(wholeCode == null){ %>
						<textarea cols="26" rows="6" style="overflow-x:hidden;overflow-y:hidden"  readonly="true">请扫描货位条码!</textarea>
			<%  }else{ %>				
				<textarea cols="26" rows="6" style="overflow-x:hidden;overflow-y:hidden"  readonly="true">请扫描商品条码!</textarea>
			<% } %>
		</td>
		</tr>
<% } %>
	<tr  height="28">
		<td colspan="2" align="center">
		<input type="text"  STYLE = "ime-mode:disabled" id="wholeCode" name="wholeCode" onblur="promptWhole()"onfocus="clearWhole()" onkeypress="javascript:return submitWholeEnter();"<%if(wholeCode != null){%>  value="<%=wholeCode%>" readonly="true"<%}%>  size="32"></td>
	</tr>
	<tr>
		<td colspan="2" align="center">
		<input type="text" id="productCode" name="productCode" onblur="promptProduct()" onfocus="clearProduct()" onkeypress="javascript:return submitProductEnter();"<%if(wholeCode == null){%> disabled="true" value=""<%}%> size="32"></td>
	</tr>
	<tr align="left">
		<td align="center">
		<input type="button" value="创建并打印装箱单" style="height: 25px;width:130px" <% if(wholeCode != null){ %>onclick="submitProductButton();"<% }else{ %>onclick="submitWholeButton();"<% } %>/>
		<input type="button" value="返  回" style="height: 25px;width:80px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zhuangxiangguanli'" /></td>
	</tr>
</form>
</table>
</body>
</html>