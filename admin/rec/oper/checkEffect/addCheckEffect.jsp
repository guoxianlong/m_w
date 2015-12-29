<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%

%>
<html>
  <head>
    
    <title>商品之间分类与效率</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
   		
   		function toAddPage() {
   			window.location="";
   		}
   		
   		function checkName(obj) {
			var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
			var rs = ""; 
			var s = obj.value;
			for (var i = 0; i < s.length; i++) { 
				if( pattern.exec(s.substr(i,1))) {
					alert("请勿输入文本外的字符");
					document.getElementById("name").value="";
					document.getElementById("name").focus();
					break;
				}
			} 
   		}
   		
   		$.extend($.fn.validatebox.defaults.rules, {  
   	        equals: {  
   	            validator: function(value,param){  
   	                return value == param[0];  
   	            },  
   	            message: '所填内容与需要内容不匹配！'  
   	        },
   	        checkName: {
   	        	validator: function (value) {
   	        		var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
   	 				var rs = ""; 
   	 				var s = value;
   	 				for (var i = 0; i < s.length; i++) { 
   	 					if( pattern.exec(s.substr(i,1))) {
   	 						document.getElementById("name").focus();
   	 						return false;
   	 					}
   	 				}
   	 				return true;
   	        	},
   	        	message:'请勿输入文本外的字符!'
   	        },
   	        checkNumber: {
   	        	validator: function (value) {
   	        		var pattern = /^[0-9]{1,9}$/;
   	    			var pattern2 = /^[0-9]{1,}$/;
   	    			var number = value;
   	    			if( number != "" ) {
	   	 	   			if(pattern.exec(number)) {
	   	 	   				return true;
	   	 	   			} else if (pattern2.exec(number)) {
	   	 		   		 	return false;
	   	 		   		} else {
	   	 	    			return false;
	   	 	   			}
   	    			} else {
   	    				return false;
   	    			}
   	        	},
   	        	message:'请填入大于0位小于9位的数字！'
   	        }
   	    });
   		
   		
   		function check() {
   			var name = $("#name").val();
   			var effect = $("#effect").val();
   			
   			if( name == null || name == "") {
   				alert("请填写名称");
   				document.getElementById("name").value="";
   				document.getElementById("name").focus();
   				return false;
   			} 
   			name = name.trim();
   			if( name == "" ) {
   				alert("名称不可以全为空格");
   				document.getElementById("name").value="";
   				document.getElementById("name").focus();
   				return false;
   			}
   			if( name.length > 20 ) {
				alert("输入的字符请勿大于20个");
				return false;
			}
   			if( effect == null || effect == "" ) {
   				alert("请填写效率");
   				document.getElementById("effect").value="";
   				document.getElementById("effect").focus();
   				return false;
   			}
   			
   			if( effect != "" ) {
	   			if(pattern.exec(effect)) {
	    			return true;
	   			} else if (pattern2.exec(effect)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		 	return false;
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	    			return false;
	   			}
   			}
   		}
   		
   		function goSubmit() {
			jQuery.messager.progress();	// display the progress bar
			$('#ff').form('submit', {
				url: '<%= request.getContextPath()%>/admin/addCheckEffect.mmx',
				onSubmit: function(){
					var isValid = $(this).form('validate');
					if (!isValid){
						jQuery.messager.progress('close');	// hide progress bar while the form is invalid
						return false;
					}
					var vname = $("#name").val();
					var veffect = $("#effect").val();
					jQuery.post("<%= request.getContextPath()%>/admin/addCheckEffect.mmx",{name:vname,effect:veffect},function(result){
							if(result=="添加成功！"){
								jQuery.messager.alert("提示","添加成功！");
								$("#name").val("");
								$("#effect").val("");
							}else{
								jQuery.messager.alert("提示",result);
							}
					});
					jQuery.messager.progress('close');
					return false;
				},
				success: function(){
					jQuery.messager.progress('close');	// hide progress bar while submit successfully
				}
			});
		}
   		
	</script>

  </head>
  <body>
<center>
<h5 style="margin-left: 10px;">添加新的分类</h5>
<div style="width:350px;height:200px;padding:10px;" class="easyui-panel" title="添加新的分类">
<center>
	<form id="ff" method="post">  
	<table align="center" cellspacing="12">
	   				<tr>
	   					<td align="left"><label for="name">商品质检分类:</label></td>
	   					<td align="left">
	   					<input class="easyui-validatebox" type="text" name="name" id="name" data-options="required:true,validType:'checkName'"  value=""/>
	   					</td>
	   				</tr>
	   				<tr>
	   					<td align="left"><label for="name">效率(件/小时):</label></td>
	   					<td align="left">
	   					<input class="easyui-validatebox" type="text" name="effect" id="effect" data-options="required:true,validType:'checkNumber'"  value=""/>
	   					</td>
	   				</tr>
	   				<tr>
	   					<td align="left"></td>
	   					<td align="left">
	   					<a href="javascript:goSubmit();" class="easyui-linkbutton" iconCls="icon-ok" >添加</a>
	   					</td>
	   				</tr>
   	</table>
	</form>
</center>
</div>
</center>
</body>
</html>
