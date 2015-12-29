<%@page import="adultadmin.bean.cargo.CargoDeptBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
CargoStaffBean csb = null;
CargoDeptBean d =null;
List cargoDeptList = new ArrayList();

String deptCode0 = "";
String deptCode1 = "";
String deptCode2 = "";
String deptCode3 = "";
String deptName0 = "";
String deptName1 = "";
String deptName2 = "";
String deptName3 = "";
if(request.getAttribute("csb") != null){
	csb = (CargoStaffBean)request.getAttribute("csb");
}
if(request.getAttribute("cargoDeptList") != null){
	cargoDeptList = (List)request.getAttribute("cargoDeptList");
}
if(cargoDeptList.size()>0){
	deptCode0 = ((CargoDeptBean)cargoDeptList.get(0)).getCode();
	deptName0 = ((CargoDeptBean)cargoDeptList.get(0)).getName();
}
if(cargoDeptList.size()>1){
	deptCode1 = ((CargoDeptBean)cargoDeptList.get(1)).getCode();
	deptName1 = ((CargoDeptBean)cargoDeptList.get(1)).getName();
}
if(cargoDeptList.size()>2){
	deptCode2 = ((CargoDeptBean)cargoDeptList.get(2)).getCode();
	deptName2 = ((CargoDeptBean)cargoDeptList.get(2)).getName();
}
if(cargoDeptList.size()>3){
	deptCode3 = ((CargoDeptBean)cargoDeptList.get(3)).getCode();
	deptName3 = ((CargoDeptBean)cargoDeptList.get(3)).getName();
}
List deptList0=(List)request.getAttribute("deptList0");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META HTTP-EQUIV="pragma" CONTENT="no-cache"> 
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate"> 
<META HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT">
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
		for(var i=0;i<nameStr.length;i++){
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
		for(var i=0; i<phone.length; i++){
			if(phone.charAt(i)<'0' || phone.charAt(i)>'9'){
				alert("电话号码只能是数字！");
				return false;
			}
		}
		
		var userName = trim(frm.userName.value);
		if( userName == null || userName == "输入账号..." || userName == "" || userName="null") {
			alert("请填写员工后台账号！");
			return false;
		}
		for(var i = 0; i < userName.length; i++){
			if(userName.charCodeAt(i) >= 10000){
				alert("后台账号不能为汉字！");
				return false;
			}
		}
	}
	var code = frm.code.value.substr(0,8);
	var deptCode0 = frm.deptCode0.value == "" ? "00" : frm.deptCode0.value;
	var deptCode1 = frm.deptCode1.value == "" ? "00" : frm.deptCode1.value;
	var deptCode2 = frm.deptCode2.value == "" ? "00" : frm.deptCode2.value;
	var deptCode3 = frm.deptCode3.value == "" ? "00" : frm.deptCode3.value;
	var deptCode = deptCode0 + deptCode1 + deptCode2 + deptCode3;
	if(deptCode != code){
		if(!confirm("新资料会导致员工编号发生改变！")){
			return false;
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
			selectOption(document.getElementById('deptCode1'), '<%=deptCode1%>');
			$.ajax({
				type: "GET",
				url: "qualifiedStock.do?method=selection&selectIndex=1&deptCode1="+document.getElementById("deptCode1").value+"&deptCode0="+document.getElementById("deptCode0").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("dept2").innerHTML = msg;
					selectOption(document.getElementById('deptCode2'), '<%=deptCode2%>');
					$.ajax({
						type: "GET",
						url: "qualifiedStock.do?method=selection&selectIndex=2&deptCode2="+document.getElementById("deptCode2").value+"&deptCode1="+document.getElementById("deptCode1").value+"&deptCode0="+document.getElementById("deptCode0").value,
						cache: false,
						dataType: "html",
						data: {type: "1"},
						success: function(msg, reqStatus){
							document.getElementById("dept3").innerHTML = msg;
							selectOption(document.getElementById('deptCode3'), '<%=deptCode3%>');
						}
					});
				}
			});
		}
	});
}

function selectdept1(){
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=1&deptCode1="+document.getElementById("deptCode1").value+"&deptCode0="+document.getElementById("deptCode0").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("dept2").innerHTML = msg;
			selectOption(document.getElementById('deptCode2'), '<%=deptCode2%>');
			$.ajax({
				type: "GET",
				url: "qualifiedStock.do?method=selection&selectIndex=2&deptCode2="+document.getElementById("deptCode2").value+"&deptCode1="+document.getElementById("deptCode1").value+"&deptCode0="+document.getElementById("deptCode0").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("dept3").innerHTML = msg;
					selectOption(document.getElementById('deptCode3'), '<%=deptCode3%>');
				}
			});
		}
	});
}
function selectdept2(){
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=2&deptCode2="+document.getElementById("deptCode2").value+"&deptCode1="+document.getElementById("deptCode1").value+"&deptCode0="+document.getElementById("deptCode0").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("dept3").innerHTML = msg;
		}
	});
}

function saveAndChangePhoto()     { 
		
	        document.getElementById("uploadTip").innerHTML="<font color='red'>请等待图片上传完成！</font>";
	        document.getElementById("cancel").disabled="true"; 
	        document.getElementById("confirm").disabled="true";
	        $.ajaxFileUpload({ 
	                url:'<%= request.getContextPath()%>/admin/qualifiedStock.do?method=addStaffPhoto&accountName='+document.getElementById("accountName").value+"&staffId="+document.getElementById("id").value+"&changeMark="+Math.random(),                 
					secureuri:false, 
	                fileElementId:'photo',                 
					dataType: 'text',    
					data: {type: "1"},             
					success: function (data, status)                 
					{ 
						var json = eval('(' + data + ')');
		                if(json['status'] == "fail")                     
						{ 
							alert(json['tip']);
						} else if( json ['status'] == "success" ) {
							var image = document.getElementById("image");
							if( image == null ) {
								$("#photoDiv").html("<img src='"+ json['serverHead'] +json['url'] + "?"+Math.random()+"' id='image' width='83px' height='100px' />");
							} else {
								image.src=json['serverHead'] +json['url'] + "?"+Math.random();
							}
							document.getElementById("photoUrl").value=json['url'];
						}
						$("#cancel").removeAttr("disabled");  
	        			$("#confirm").removeAttr("disabled"); 
						document.getElementById("uploadTip").innerHTML="";                
					}, 
	                error: function (data, status, e) { 
	                    alert("错误!"); 
						$("#cancel").removeAttr("disabled");  
	        			$("#confirm").removeAttr("disabled"); 
						document.getElementById("uploadTip").innerHTML="";
					}             
			});
		
        return false;     
}
function idCheck() {
	var photoUrl = document.getElementById("photoUrl").value;
	var id = document.getElementById("id").value;
	if( id == null || id == "" || id == "0" ) {
		alert("当前员工id存在问题！");
		return false;
	}
	if( photoUrl == null || photoUrl == "" || photoUrl == "null" ) {
		if( window.confirm("当前没有头像，确认保存？")) {
			return true;
		} else {
			return false;
		}
	}
	return true;
}

function deleteInfo() {
	var id = document.getElementById("id").value;
	if( id == null || id == "" || id == "0" ) {
		alert("当前员工id存在问题！");
		return;
	} else {
		window.location="<%= request.getContextPath()%>/admin/qualifiedStock.do?method=delStaff&staffId="+id;
		return;
	}
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
<style>
/*file容器样式*/
a.files {
    width:90px;
    height:18px;
    overflow:hidden;
    display:block;
    border:1px solid #BEBEBE;
    outline:none;
/*这里改成你的图片地址*/
    background:url(<%= request.getContextPath()%>/image/staffPhoto1.png) center top no-repeat;
    text-decoration:none;
}
/*file设为透明，并覆盖整个触发面*/
a.files input {
    margin-left:-400px;
    font-size:30px;/*可以通过font-size来设置高度。*/
    cursor:pointer;
    filter:alpha(opacity=0);
    opacity:0;
    border:none;
}
 
</style>
</head>
<body>
<table cellpadding="3" border=1 style="border-collapse: collapse;"
	bordercolor="#D8D8D5" width="263px" >
	<tr>
		<td>
		<span style="font-family: 微软雅黑; font-size: 16px; font-weight: bold; font-style: normal; text-decoration: none; color: #0000FF;">添加员工头像</span>
		<span id="uploadTip" align="center" ></span>
		<div align="center" id="photoDiv" style="margin-left:80px;border-style:solid;border-color:#000000;border-width:1px;height:100px;width:83px;">
		</div>
			<div style="margin-left:73px;border-style:solid;border-width:6px;border-color:#FFFFFF;"><a class="files" href="javascript:void(0);"><input type="file" name="photo" id="photo" onchange="saveAndChangePhoto();" ></a></div>
		<table cellpadding="3">
			<tr>
				<td>姓&nbsp;&nbsp;&nbsp;&nbsp;名：</td>
				<td>
				<input name="code" type="hidden" value="<%=csb.getCode()%>"/>
				<%=csb.getName()%>
				</td>
			</tr>
			<tr>
				<td>电&nbsp;&nbsp;&nbsp;&nbsp;话：</td>
				<td><%=csb.getPhone() %></td>
			</tr>
			<tr>
				<td>后台账户：</td>
				<td>
				<%=csb.getUserName()%>
				<input type="hidden" name="accountName" id="accountName" value="<%= csb.getUserName()%>" />
				</td>
				
			</tr>
			<tr>
				<td>归属部门：</td>
				<td>
					<%= deptName0%>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td id="dept1">
					<%= deptName1%>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td id="dept2">
					<%= deptName2%>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td id="dept3">
					<%= deptName3%>
				</td>
			</tr>
		<form name="editFrm" action="qualifiedStock.do?method=addStaffNext" method="post" onsubmit="return idCheck();">	
			<input type="hidden" id="photoUrl" name="photoUrl" value="" />
			<input name="id" type="hidden" id="id" value="<%=csb.getId()%>"/>
			<tr>
				<td><INPUT name="save" type="submit" id="confirm" value="保存"/></td>
				<td><input type="button" value="取消" id="cancel" onclick="deleteInfo();"/></td>
			</tr>
			</form>
		</table>
		</td>
	</tr>
</table>
</body>
</html>