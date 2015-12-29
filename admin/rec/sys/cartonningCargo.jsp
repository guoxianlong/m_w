<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html>
<head>
<title>装箱单关联货位</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/demo/demo.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
<%
String code = request.getParameter("code");
String flag = (String)request.getAttribute("flag");
%>
</head>
<form id="form" method="post">
<h2>装箱单关联货位</h2>
<h4>请输入需要关联的装箱单号和货位号：</h4>
	<table>
		<tr>
			<td align="center"><span>装箱单号：</span></td>
			<td><input type="text" readonly="readonly" value="<%=code %>" name="code" id="code"/></td>
		</tr>
		<tr>
			<td align="center"><span> 货位号：</span></td>
			<td><input type="text" name="cargoWholeCode" class="easyui-validatebox" data-options="required:true" id="cargoWholeCode" />
				<input type="hidden" name="flag" value="<%=flag%>"/></td>
		</tr>
		<tr>
			<td align="center"><a class="easyui-linkbutton" id="submit" data-options="iconCls:'icon-ok'">确认提交</a></td>
			<td align="center"><a class="easyui-linkbutton" id="cancel" data-options="iconCls:'icon-cancel'">取消</a></td>
		</tr>
	</table>
</form>
</body>
<script type="text/javascript">
	$.fn.validatebox.defaults = {missingMessage:"此字段不能为空"};
	$(function(){
		$("#cancel").click(function(){
			window.close();
		});
		$("#submit").click(function(){
			$("#form").form('submit',({
				url:'<%=request.getContextPath()%>/CargoController/cartonningCargo.mmx',
				onSubmit:function(){
					var isValid = $(this).form('validate');
					return isValid;
				},
				success:function(data){
					var json = eval('('+data+')');
					if(json['result']=='success'){
						window.opener.location.reload();
						window.close();
					}
					$.messager.show({
						title:'提示',
						msg:json['tip'],
						timeout:3000,
						showType:'slide'
					});
				}
			}));
		});
	});
</script>
</html>