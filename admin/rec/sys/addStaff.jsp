<%@page import="adultadmin.bean.cargo.CargoDeptBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	src="<%=request.getContextPath()%>/js/ajaxfileupload.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/demo/demo.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
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
    background:url(<%= request.getContextPath()%>/image/staffPhoto2.png) center top no-repeat;
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
<%
	String deptList0Select=(String)request.getAttribute("deptList0Select");
%>
</head>
<body>
	<div id="edit" data-options="iconCls:'icon-save'" style="width:400px;height:500px;left:100px;top:80px;padding:10px">
		<table cellpadding="3">
			<tr>
				<td>
					<form id="form" method="post">
						<table>
							<tr>
								<td>姓&nbsp;&nbsp;&nbsp;&nbsp;名：</td>
								<td>
								<input name="id" type="hidden" id="id" />
								<input type="hidden" id="photoUrl" name="photoUrl" />
								<input name="nameStr" id="nameStr" type="text" class="easyui-validatebox" data-options="required:true"/> 
								<font color="red">*</font> 必填</td>
							</tr>
							<tr>
								<td>电&nbsp;&nbsp;&nbsp;&nbsp;话：</td>
								<td><input name="phone" type="text" id="phone" /></td>
							</tr>
							<tr>
								<td>后台账户：</td>
								<td><input name="userName" id="username" type="text" class="easyui-validatebox" data-options="required:true"/><font color="red">*</font> 必填
									<input type="hidden" name="accountName" id="accountName" />
								</td>
							</tr>
							<tr>
								<td>归属部门：</td>
								<td>
									<input type="text" class="easyui-combobox" name="deptCode0" id="deptCode0" style="width: 120px;" editable="false">
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td id="dept1">
									<input type="text" class="easyui-combobox" editable="false" name="deptCode1" id="deptCode1" style="width: 120px;">
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td id="dept2">
									<input type="text" class="easyui-combobox" editable="false" name="deptCode2" id="deptCode2" style="width: 120px;">
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td id="dept3">
									<input type="text" class="easyui-combobox" editable="false" name="deptCode3" id="deptCode3" style="width: 120px;">
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
	$.fn.validatebox.defaults = {missingMessage:"此字段不能为空"};
	//ajax文件上传qualifiedStock
	$(function(){
		var dd = "<%=deptList0Select%>";
		var jj = eval('('+dd+')');
		$("#edit").dialog({
			title:'添加员工资料',
			buttons:[{
				text:'下一步',
				iconCls:'icon-redo',
				handler:function(){
					$("#form").form('submit',({
						url:'<%=request.getContextPath()%>/CargoController/addStaff.mmx',
						onSubmit:function(){
							var isValid = $(this).form('validate');
							if(!isValid){
								return false;
							}
							var nameStr = $.trim($("#nameStr").val());
							var ischinese=true;
							for(var i=0;i<nameStr.length;i++){
								ischinese = ischinese && (nameStr.charCodeAt(i)>=10000);
							}
							if(!ischinese){
								$.messager.show({
									title:'提示',
									msg:'员工姓名只能为汉字！',
									timeout:3000,
									showType:'slide'
								});
								return false;
							}
							if(nameStr.length > 4){
								$.messager.show({
									title:'提示',
									msg:'员工姓名不能超过4个字！',
									timeout:3000,
									showType:'slide'
								});
								return false;
							}
							
							var phone = $("#phone").val();
							if(phone.length > 13){
								$.messager.show({
									title:'提示',
									msg:'号码长度不能大于13位！',
									timeout:3000,
									showType:'slide'
								});
								return false;
							}
							for(var i=0; i<phone.length; i++){
								if(phone.charAt(i)<'0' || phone.charAt(i)>'9'){
									$.messager.show({
										title:'提示',
										msg:'电话号码只能是数字！',
										timeout:3000,
										showType:'slide'
									});
									return false;
								}
							}
							
							var userName = $.trim($("#username").val());
							if( userName == null || userName == "输入账号..." || userName == "" ) {
								$.messager.show({
									title:'提示',
									msg:'请填写员工后台账号！',
									timeout:3000,
									showType:'slide'
								});
								return false;
							}
							for(var i = 0; i < userName.length; i++){
								if(userName.charCodeAt(i) >= 10000){
									$.messager.show({
										title:'提示',
										msg:'后台账号不能为汉字！',
										timeout:3000,
										showType:'slide'
									});
									return false;
								}
							}
							if($("#deptCode0").combobox('getValue') == ""){
								$.messager.show({
									title:'提示',
									msg:'一级部门不能为空！',
									timeout:3000,
									showType:'slide'
								});
								return false;
							}
						},
						success:function(data){
							var json = eval('('+data+')');
							if(json['result']=='success'){
								//window.opener.location.reload();
								//window.close();
								window.location.href="<%=request.getContextPath()%>/CargoController/addStaffNextJsp.mmx?id="+json['tip'];
								return ;
							}
							$.messager.show({
								title:'提示',
								msg:json['tip'],
								timeout:3000,
								showType:'slide'
							});
						}
					}));
				}
			},{
				text:'取消',
				iconCls:'icon-cancel',
				handler:function(){
					window.history.back(-1);
				}
			}]
		});
		$("#deptCode0").combobox({
			valueField:'id',
			textField:'text',
			data:jj,
			onChange:function(rec){
				//rec是combobox的值
				$("#deptCode1").combobox({
					url:'<%=request.getContextPath()%>/CargoController/selectDeptcode.mmx?selectIndex=0&deptCode0='+rec,
					valueField:'id',
					textField:'text',
					onChange:function(rec2){
						$("#deptCode2").combobox({
							url:'<%=request.getContextPath()%>/CargoController/selectDeptcode.mmx?selectIndex=1&deptCode0='+rec+'&deptCode1='+rec2,
							valueField:'id',
							textField:'text',
							onChange:function(rec3){
								$("#deptCode3").combobox({
									url:'<%=request.getContextPath()%>/CargoController/selectDeptcode.mmx?selectIndex=2&deptCode0='+rec+'&deptCode1='+rec2+'&deptCode2='+rec3,
									valueField:'id',
									textField:'text'
								});
							}
						});
					}
				});
			}
		});
	});
</script>
</html>