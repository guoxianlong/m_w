<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>货位绑定产品</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/demo/demo.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
<%
String productCode=request.getParameter("productCode")==null?"":request.getParameter("productCode");
String cargoCode=request.getParameter("cargoCode")==null?"":request.getParameter("cargoCode");
%>
</head>
<body>
	<div style="background:#fafafa;padding:10px;width:300px;height:300px;">
		<form id="form" method="post" action="<%=request.getContextPath()%>/CargoController/checkCargoProduct.mmx">
			<table>
				<tr>
					<td align="right">1.产品编号：</td>
					<td><input type="text" id="productCode" class="easyui-validatebox" data-options="required:true" name="productCode" size="10" maxlength="20" value="<%=productCode%>"/></td>
				</tr>
				<tr>
					<td align="right">2. 货  位  号：</td>
					<td><input type="text" name="cargoCode" id="cargoCode" class="easyui-validatebox" data-options="required:true" size="15" maxlength="20" value="<%=cargoCode%>"/></td>
				</tr>
				<tr>
					<td><a href="#" id="check" class="easyui-linkbutton" data-options="iconCls:'icon-search'">我要核实</a></td>
					<td><a href="#" id="submit" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" id="submit">确认提交</a>
						</td>
				</tr>
			</table>
		</form>
	</div>
</body>
<script type="text/javascript">
	$.fn.validatebox.defaults = {missingMessage:"此字段不能为空"};
	$(function(){
		$("#check").click(function(){
			var isValid = $("#form").form('validate');
			if(!isValid){
				return isValid;
			}
			$("#form").submit();
		});
		$("#submit").click(function(){
			var isValid = $("#form").form('validate');
			if(!isValid){
				return isValid;
			}else{
				$("#form").attr("action","<%=request.getContextPath()%>/CargoController/cargoProduct.mmx");
				$("#form").submit();
			}
		});
	});
	function check(){
		var isValid = $("#form").form('validate');
		return isValid;	
	}
</script>
</html>