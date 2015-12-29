<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>创建质检装箱单</title>
<% 
ProductStockBean psBean = new ProductStockBean();
String result = request.getParameter("result");
if(result == null){
	result = (String)request.getAttribute("result");
}
String code = request.getParameter("code");
String standard = "";
if(request.getParameter("standard")!=null){
	standard = request.getParameter("standard");
}
String area = (String)request.getSession().getAttribute("area");
int areaId = StringUtil.toInt(area);
String productCode = (String)request.getAttribute("productCode");
String bsCode = (String)request.getAttribute("bsCode");
 %>
<script type="text/javascript">
var xmlHttp;  
function createXMLHttpRequest() {    
    if (window.ActiveXObject) {    
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");    
    }    
    else if (window.XMLHttpRequest) {    
        xmlHttp = new XMLHttpRequest();    
    }   
}    
function okFunc (){   
    if(xmlHttp.readyState == 4) { 
        if(xmlHttp.status == 200) { 
        	document.getElementById("performance").innerHTML=xmlHttp.responseText;
        }    
    }    
}    
function startAjax(){   
    createXMLHttpRequest();    
    if( !xmlHttp){    
        return alert('create failed');    
    }    
    xmlHttp.open("GET", "<%=request.getContextPath()%>/admin/stockOperation.do?method=ajaxCargoStaffPerformance&selectIndex=9", true); 
    xmlHttp.onreadystatechange = okFunc;    
    xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");    
    xmlHttp.send("");    
}    
function checkBSCodeButton(){
	var bsCode = document.getElementById("bsCode").value;
	if(bsCode != "" && bsCode != "扫描入库单号"){
		document.forms[0].submit();
	}else{
		alert("入库单号不能为空！");
		document.getElementById("bsCode").focus();
	}	
}
function checkProductButton(){
	var productCode = document.getElementById("productCode").value;
	if(productCode != "" && productCode != "扫描商品编号"){
		document.forms[0].submit();
	}else{
		alert("商品编号不能为空！");
		document.getElementById("productCode").focus();
	}
}
function checkBSCodeEnter(){
	if(window.event.keyCode == 13){
		var bsCode = document.getElementById("bsCode").value;
		if(bsCode != "" && bsCode != "扫描入库单号"){
			document.forms[0].submit();
		}else{
			alert("入库单号不能为空！");
				document.getElementById("bsCode").focus();
		}	
	}	
}
function checkProductEnter(){
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
function checkCountButton(){
	var productCode = document.getElementById("productCode").value;
	var count = document.getElementById("count").value;
	var reg=/^\d*$/;
	if(productCode != "" && productCode != "扫描商品编号"){
		if(count != "" && count != "输入数量" && count != 0){
			if(reg.exec(count)!=null){
				document.forms[0].submit();
				setTimeout("window.location='<%=request.getContextPath()%>/admin/cargo/soCreateQualityCartonning.jsp';",5000);
			}else{
				alert("数量不合法！");
				document.getElementById("count").value = "";
				document.getElementById("count").focus();
			}
		}else{
			alert("商品数量不能为空！");
			document.getElementById("count").focus();
		}
	}else{
		alert("商品编号不能为空！");
		document.getElementById("productCode").focus();
	}
}
function checkCountEnter(){
	if(window.event.keyCode == 13){
		var productCode = document.getElementById("productCode").value;
		var count = document.getElementById("count").value;
		var reg=/^\d*$/;
		if(productCode != "" && productCode != "扫描商品编号"){
			if(count != "" && count != "输入数量" && count != 0){
				if(reg.exec(count)!=null){
					document.forms[0].submit();
				}else{
					alert("数量不合法！");
					document.getElementById("count").value = "";
					document.getElementById("count").focus();
				}
			}else{
				alert("商品数量不能为空！");
				document.getElementById("count").focus();
			}
		}else{
			alert("商品编号不能为空！");
			document.getElementById("productCode").focus();
		}	
	}
}
function clearCount(){
	var count = document.getElementById("count").value;
	
	if(count == "输入数量" | count == 0){
		document.getElementById("count").value="";
	}
}
function getFocus(){
	startAjax();
	<%
		if(code != null){
	%>  
			var sel = confirm("装箱单是否已打印？");
			if(sel == false){
				window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=delCartonning&flag=quality&cartonningCode=<%=code%>';
			}
	<%
		}
		if(bsCode != null){
			if(productCode != null){
	%>
				document.getElementById("count").focus();
	<%
			}else{
	%>
				document.getElementById("productCode").value = "";
				document.getElementById("productCode").focus();
				document.getElementById("count").value ="输入数量";
	<%
			}
		}else{
	%>
			document.getElementById("bsCode").value = "";
			document.getElementById("bsCode").focus();
			document.getElementById("count").value ="输入数量";
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
		document.getElementById("count").value = "";
		document.getElementById("count").focus();
		return false;
    }
}
function clearProduct(){
	<%if(productCode == null){%>
		document.getElementById("productCode").value ="";
	<%}%>
}
function clearBSCode(){
	<%if(bsCode == null){%>
		document.getElementById("bsCode").value ="";
	<%}%>
}
function promptBSCode(){
	var bsCode = document.getElementById("bsCode").value;
	if(bsCode == ""){
		document.getElementById("bsCode").value ="扫描入库单号";
	}
}
function promptProduct(){
	var productCode = document.getElementById("productCode").value;
	if(productCode == ""){
		document.getElementById("productCode").value ="扫描商品编号";
	}
}
function promptCount(){
	var count = document.getElementById("count").value;
	if(count == ""){
		document.getElementById("count").value ="输入数量";
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()" topmargin="9">
<table width="220" height="220" border="0" cellspacing="0">
<tr >
	<td colspan="2" align="center"><div id="performance" align="center"></div></td>
</tr>
<tr >
	<td colspan="2" align="center"><font size="4" style="font-weight:bold">质检装箱单</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr >
	<td colspan="2" align="center"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr>
	<td colspan="2" align="center"><font size="2"><%=DateUtil.getNow() %></font></td>
</tr>

<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=createQualityCartonning" method="post" >
<% if(result != null){ 
		if(code !=null){%>
			<tr height="28">
				<td colspan="2" align="center"> <textarea cols="26" rows="7" style="overflow-x:hidden;overflow-y:hidden" readonly="true">质检装箱单:<%=code %>创建成功。<%=standard==null?"":"标准装箱量："+standard+"。" %></textarea></td>
			</tr>
       <%}else{%>
 			<tr height="28">
				<td colspan="2" align="center"> <textarea cols="26" rows="7" style="overflow-x:hidden;overflow-y:hidden" readonly="true"><%=result %></textarea></td>
			</tr>
 <%		}
 }else{ %>
	<tr  align="center">
		<td colspan="2" align="center">
			<%if(bsCode != null){ 
				if(productCode != null){ %>
					<textarea cols="26" rows="6" style="overflow-x:hidden;overflow-y:hidden"  readonly="true">现在请输入商品数量!</textarea>
				<%  }else{ %>				
					<textarea cols="26" rows="6" style="overflow-x:hidden;overflow-y:hidden"  readonly="true">请在下方扫描商品编号!</textarea>
				<% } %>
			<% }else{ %>
				<textarea cols="26" rows="6" style="overflow-x:hidden;overflow-y:hidden"  readonly="true">请在下方扫描入库单号!</textarea>
			<% } %>
		</td>
		</tr>
<% } %>
	<tr  align="center">
		<% if(bsCode != null){ %>
		<td align="center" colspan="2">
		<input type="text" id="productCode" name="productCode" onfocus="clearProduct()"onblur="promptProduct()" onkeypress="javascript:return checkProductEnter();"<% if(productCode != null){ %>value="<%=productCode  %>"readonly="true" <% } %> size="16">	
		<input type="hidden" id="bsCode" name="bsCode" value="<%= bsCode %>">
		<% }else{ %>
		<td align="center" colspan="2">
		<input type="text" id="bsCode" name="bsCode" onfocus="clearBSCode()"onblur="promptBSCode()" onkeypress="javascript:return checkBSCodeEnter();" size="16"/>	
		<% } %>
		<input type="text" name="count"  id="count"  onfocus="clearCount()"onblur="promptCount()"onkeypress="javascript:return checkCountEnter();" <% if(productCode == null){ %> disabled="true" <% } %> size="6"/></td>
	</tr>
	<tr align="center">
		<td align="center" colspan="2"><input type="button" value="创建并打印装箱单" style="height:28px;width:130px"<% if(bsCode != null){if(productCode != null){ %> onclick="checkCountButton();"<% }else{ %>onclick="checkProductButton();"<% }}else{ %>onclick="checkBSCodeButton();"<% } %>/>
		<input type="button" value="返  回" style="height: 28px;width:80px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zhuangxiangguanli'" /></td>
	</tr>
</form>
</table>
</body>
</html>