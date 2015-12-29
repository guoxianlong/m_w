<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoDeptBean"%><html>
<head>
<%
List deptList0 = (List) request.getAttribute("deptList0");
%>
<title>物流员工管理</title>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/js/ajaxfileupload.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css"
	rel="stylesheet" type="text/css"/>
<script type="text/javascript">
function check(frm){
	if(trim(frm.nameStr.value)=="" ){
		alert("员工姓名不能为空！");
		return false;
	}else{
		var nameStr = trim(frm.nameStr.value);
		var ischinese=true;
		for(var i = 0; i < nameStr.length; i++){
			ischinese = ischinese && (nameStr.charCodeAt(i)>=10000);
		}
		if(!ischinese){
			alert("员工姓名只能为汉字！");
			return false;
		}
		if(nameStr.length > 4){
			alert("员工姓名不能超过4个字！");
			return false;
		}
		
		var phone = frm.phone.value;
		if(phone.length > 13){  
			alert("号码长度不能大于13位！");
			return false;
		}
		for(var i = 0; i < phone.length; i++){
			if(phone != "输入电话号码" && (phone.charAt(i)<'0' || phone.charAt(i)>'9')){
				alert("电话号码只能是数字！");
				return false;
			}
		}
		
		var userName = trim(frm.userName.value);
		if( userName == null || userName == "输入账号..." || userName == "" ) {
			alert("请填写员工后台账号！");
			return false;
		}
		if(frm.deptCode0.value == ""){
			alert("一级部门不能为空");
			return false;
		}
		for(var i = 0; i < userName.length; i++){
			if(userName != "输入账号..." && userName.charCodeAt(i) >= 10000){
				alert("后台账号不能为汉字！");
				return false;
			}
		}
	}
	return true;
}

function selectdept0() {
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=0&deptCode0="+document.getElementById("deptCode0").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("dept1").innerHTML = msg;
		}
	});
}

function selectdept1(){
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=1&deptCode0="+document.getElementById("deptCode0").value+"&deptCode1="+document.getElementById("deptCode1").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("dept2").innerHTML = msg;
		}
	});
}
function selectdept2(){
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=2&deptCode2="+document.getElementById("deptCode2").value+"&deptCode0="+document.getElementById("deptCode0").value+"&deptCode1="+document.getElementById("deptCode1").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("dept3").innerHTML = msg;
		}
	});
}

</script>
<style>
td{
	font-family: 微软雅黑; 
	font-size: 13px; 
	font-weight: bold; 
	font-style: normal; 
	text-decoration: none; 
	color: #333333;
 }
</style>
</head>
<body>
<table cellpadding="3" border=1 style="border-collapse: collapse;"
	bordercolor="#D8D8D5">
	<tr>
		<td>
		<span style="font-family: 微软雅黑; font-size: 16px; font-weight: bold; font-style: normal; text-decoration: none; color: #0000FF;">添加员工资料</span>
		<form name="addFrm" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=addStaff&add=1" method="post" onsubmit="return check(this)">
		<table cellpadding="3">
			<tr>
				<td>姓&nbsp;&nbsp;&nbsp;&nbsp;名：</td>
				<td><input name="nameStr" id="nameStr" type="text" value="输入文字..." style="color:#cccccc;" 
				onfocus="if(this.value=='输入文字...'){this.value='';this.style.color='#000000';}" 
				onblur="if(this.value==''){this.value='输入文字...';this.style.color='#cccccc';}"/> <font color="red">*</font> 必填</td>
			</tr>
			<tr>
				<td>电&nbsp;&nbsp;&nbsp;&nbsp;话：</td>
				<td><input name="phone" type="text" value="输入电话号码" style="color:#cccccc;" 
				onfocus="if(this.value=='输入电话号码'){this.value='';this.style.color='#000000';}"
				onblur="if(this.value==''){this.value='输入电话号码';this.style.color='#cccccc';}"/></td>
			</tr>
			<tr>
				<td>后台账户：</td>
				<td><input name="userName" type="text" value="输入账号..." style="color:#cccccc;" 
				onfocus="if(this.value=='输入账号...'){this.value='';this.style.color='#000000';}"
				onblur="if(this.value==''){this.value='输入账号...';this.style.color='#cccccc';}"/><font color="red">*</font> 必填</td>
			</tr>
			<tr>
				<td>归属部门：</td>
				<td>
					<select id="deptCode0" name="deptCode0" onchange="selectdept0();">
						<option selected value="">选择所属地区</option>
						<%for(int i=0;i<deptList0.size();i++){%>
						<%CargoDeptBean dept0=(CargoDeptBean)deptList0.get(i); %>
						<option value="<%=dept0.getCode() %>"><%=dept0.getName() %></option>
					<%} %>
					</select>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td id="dept1">
					<SELECT name="deptCode1">
						<OPTION selected value="">选择一级部门</OPTION>
					</SELECT>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td id="dept2">
					<SELECT name="deptCode2">
						<OPTION selected value="">选择二级部门</OPTION>
					</SELECT>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td id="dept3">
					<SELECT name="deptCode3">
						<OPTION selected value="">选择三级部门</OPTION>
					</SELECT>
				</td>
			</tr>
			<tr>
				<td><INPUT name="save" type="submit"  value="下一步" /></td>
				<td><input type="button" value="取消" onclick="window.history.back(-1);"/></td>
			</tr>
		</table>
		</form>
		</td>
	</tr>
</table>
</body>
</html>