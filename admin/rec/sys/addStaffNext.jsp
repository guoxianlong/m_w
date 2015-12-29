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
	deptName0 = ((CargoDeptBean)cargoDeptList.get(0)).getName();
}
if(cargoDeptList.size()>1){
	deptName1 = ((CargoDeptBean)cargoDeptList.get(1)).getName();
}
if(cargoDeptList.size()>2){
	deptName2 = ((CargoDeptBean)cargoDeptList.get(2)).getName();
}
if(cargoDeptList.size()>3){
	deptName3 = ((CargoDeptBean)cargoDeptList.get(3)).getName();
}
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
<link href="<%=request.getContextPath()%>/css/global.css"
	rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/demo/demo.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/ajaxfileupload.js"></script>
<style>
td{
	font-family: 微软雅黑; 
	font-size: 13px; 
/* 	font-weight: bold;  */
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
<div id=add data-options="iconCls:'icon-save'" style="width:300px;height:400px;left:100px;top:80px;padding:10px">
		<table cellpadding="3">
			<tr>
				<td>
					<span id="uploadTip" align="center" ></span>
					<div align="center" id="photoDiv" style="margin-left:80px;border-style:solid;border-color:#000000;
						border-width:1px;height:100px;width:83px;">
					</div>
					<div style="margin-left:73px;border-style:solid;border-width:6px;border-color:#FFFFFF;">
						<a class="files" href="javascript:void(0);">
							<input type="file" name="photo" id="photo" onchange="saveAndChangePhoto();" >
						</a>
					</div>
					<form id="form" method="post">
						<table>
							<tr>
								<td>姓&nbsp;&nbsp;&nbsp;&nbsp;名：</td>
								<td>
								<input type="hidden" id="photoUrl" name="photoUrl" value="" />
								<input name="id" type="hidden" id="id" value="<%=csb.getId()%>"/>
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
						</table>
					</form>
				</td>
			</tr>
		</table>
	</div>
</body>
<script type="text/javascript">
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	var st = true;
	function saveAndChangePhoto(){ 
		        document.getElementById("uploadTip").innerHTML="<font color='red'>请等待图片上传完成！</font>";
		        st = false;
		        $.ajaxFileUpload({ 
		                url:'<%= request.getContextPath()%>/admin/qualifiedStock.do?method=addStaffPhoto&accountName='+document.getElementById("accountName").value+"&staffId="+document.getElementById("id").value+"&changeMark="+Math.random(),                 
						secureuri:false, 
		                fileElementId:'photo',                 
						dataType: 'text',    
						data: {type: "1"},             
						success: function (data, status){ 
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
							st = true; 
							$("#uploadTip").html("");                
						}, 
		                error: function (data, status, e) { 
		                    alert("错误!"); 
							st = true; 
							$("#uploadTip").html("");
						}             
				});
			
	        return false;     
	}
	$(function(){
		$("#add").dialog({
			title:'添加员工资料',
			buttons:[{
				text:'保存',
				iconCls:'icon-save',
				handler:function(){
					if(st){
						$("#form").form('submit',{
							url:'<%=request.getContextPath()%>/CargoController/addStaffNext.mmx',
							onSubmit:function(){
								var photoUrl = document.getElementById("photoUrl").value;
								var id = document.getElementById("id").value;
								if( id == null || id == "" || id == "0" ) {
									$.messager.show({
										title:'提示',
										msg:"当前员工id存在问题！",
										timeout:3000,
										showType:'slide'
									});
									return false;
								}
								if( photoUrl == null || photoUrl == "" || photoUrl == "null" ) {
									if( window.confirm("当前没有头像，确认保存？")) {
										return true;
									} else {
										return false;
									}
								}
							},
							success:function(data){
								var json = eval('('+data+')');
								if(json['result']=='success'){
									//window.opener.location.reload();
									//window.close();
									window.location.href="<%=request.getContextPath()%>/admin/rec/sys/staffManagement.jsp";
								}
								$.messager.show({
									title:'提示',
									msg:json['tip'],
									timeout:3000,
									showType:'slide'
								});
							}
						});
					}
				}
			},{
				text:'取消',
				iconCls:'icon-cancel',
				handler:function(){
					if(st){
						window.history.back(-1);
					}
				}
			}]
		});
	});
</script>
</html>