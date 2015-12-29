<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>作业确认完成</title>
<%
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
	ProductStockBean psBean = new ProductStockBean();
	String cartonningCode=request.getParameter("cartonningCode");
	String cargoCode=request.getParameter("cargoCode");
	String next=null;
	if(request.getAttribute("next")!=null){
		next=request.getAttribute("next").toString();
	}
	String inCiCode="";
	if(request.getAttribute("inCiCode")!=null){
		inCiCode=request.getAttribute("inCiCode").toString();
	}
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
        	$("#code").focus();
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
function checkText(){
	var operationCode = document.getElementById("code").value;
	if(operationCode != "" && operationCode != "扫描装箱单编号" && operationCode != "扫描货位号"){
		<%if(!"".equals(inCiCode)){%>
			if(operationCode!="<%=inCiCode%>"){
				var re=confirm("货位不一致");
				if(re==false){
					return false;
				}
			}
		<%}%>
		//document.forms[0].submit();
		setTimeout("window.location='<%=request.getContextPath()%>/admin/cargo/soOperationComplete.jsp';",5000);
	}else{
		alert("不能为空!");
		document.getElementById("code").focus();
	}
}
function gosearch(){
    if(window.event.keyCode == 13){
    	var operationCode = document.getElementById("operationCode").value;
		if(operationCode != "" && operationCode != "扫描作业单编号"){
			document.forms[0].submit();
		}else{
			alert("作业单号不能为空!");
			document.getElementById("operationCode").focus();
			return false;    
		}
    }
}
function getFocus(){
	startAjax();
	<%
		if(area==null){
	%>
			alert("登陆超时,请重新登录!");
			window.location='stockOperation.do?method=logout';
	<%
		}
	%>
	document.getElementById("code").value = "";
	document.getElementById("code").focus();
}
function clearText(){
	document.getElementById("operationCode").value="";
}
function prompt(){
	var operationCode = document.getElementById("operationCode").value;
	if(operationCode == ""){
		document.getElementById("operationCode").value ="扫描作业单编号";
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg" onload="getFocus()" style="overflow:hidden" topmargin="9">
<div id="performance" style="margin-left:0px;"></div>
<table width="220" height="220" border="0" cellspacing="0">
<tr >
	<td colspan="2" align="center"><font size="4" style="font-weight:bold">作业完成</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr ><td colspan="2" align="center">
	<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr >
	<td colspan="2" align="center"><font size="2"><%=DateUtil.getNow() %></font></td>
</tr>
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=operationComplete" method="post" onsubmit="return checkText();">
<% if(result != null){ %>
	<tr >
		<td colspan="2" align="center"> 
		<textarea cols="26" rows="6" readonly="true" style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
	</tr>
<% }else{ %>
	<tr >
		<%if(next==null||next.equals("cartonningCode")){ %>
			<td colspan="2" align="center"> <textarea cols="26" rows="6" readonly="true" style="overflow-x:hidden;overflow-y:hidden">请扫描装箱单号</textarea></td>
		<%}else if(next.equals("cargoCode")){ %>
		 	<td colspan="2" align="center"> <textarea cols="26" rows="6" readonly="true" style="overflow-x:hidden;overflow-y:hidden">请扫描货位号</textarea></td>
		<%}else{ %>
		 	<td colspan="2" align="center"> <textarea cols="26" rows="6" readonly="true" style="overflow-x:hidden;overflow-y:hidden">请扫描装箱单号</textarea></td>
		<%} %> 
	</tr>
<% } %>
	<tr>
		<td colspan="2" align="center">
			<%--
			<input type="text"style="width=195px;"id="operationCode"name="operationCode"onblur="prompt()"onfocus="clearText()" onkeypress="javascript:return gosearch();">
			 --%>
			 <%if(next==null||next.equals("cartonningCode")){ %>
			 	<input type="text" id="code" size="32" name="cartonningCode" value="扫描装箱单号" onfocus="javascript:if(this.value='扫描装箱单号'){this.value='';}" onblur="javascript:if(this.value==''){this.value='扫描装箱单号';}"/>
			 <%}else if(next.equals("cargoCode")){ %>
			 	<input type="hidden" name="cartonningCode" value="<%=cartonningCode %>" />
			 	<input type="text" id="code" size="32" name="cargoCode" value="扫描货位号" onfocus="javascript:if(this.value=='扫描货位号'){this.value='';}" onblur="javascript:if(this.value==''){this.value='扫描货位号';}"/>
			 <%}else{ %>
			 	<input type="text" id="code" size="32" name="cartonningCode" value="扫描装箱单号" onfocus="javascript:if(this.value=='扫描装箱单号'){this.value='';}" onblur="javascript:if(this.value==''){this.value='扫描装箱单号';}"/>
			 <%} %> 
		</td>
	</tr>
	<tr  align="center">
		<td><input type="submit" value="完  成" style="height:26px;width:130px;" />
		<input type="button" value="返  回" style="height:26px;width:80px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zuoyejiaojie'"/></td>
	</tr>
</form>
</table>
<script type="text/javascript">document.getElementById("code").focus();</script>
</body>
</html>