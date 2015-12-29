<%@page import="adultadmin.util.StringUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<%
	Object resultObj = request.getAttribute("result");
	Object msgObj = request.getAttribute("msg");
	String result = null;
	String msg = null;
	if(resultObj != null){
		result = (String) resultObj;
	}
	if(msgObj != null){
		msg = (String) msgObj;
	}
	if("".equals(msg))
		msg = null;
	if("".equals(result))
		result = null;
%>
<!DOCTYPE html>
<html>
	<head>
	<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>
   	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>批量导入配送时效</title>
	<script type="text/javascript">
		function checkUpload(){
			var value = $("#f").val();
			if(value.length <= 0){
				alert("请您选中文件后,点击上传按钮");
				return false;
			}else{
				if(value.indexOf(".") == -1){
					alert("请您上传excel文件,后缀名为.xlsx或者.xls");
					return false;
				}else{
					var valStack = new Array();
					valStack = value.split(".");
					var lastValue = valStack[valStack.length - 1];
					if (lastValue == "xlsx" || lastValue == "xls") {
						return true;
					}
					alert("请您上传excel文件,后缀名为.xlsx或者.xls");
					return false;					
				}
				return true;
			}
			return false;
		}
		$(function() {
			$("#areaId").combobox({
				url : '${pageContext.request.contextPath}/Combobox/getStockoutAvailableArea.mmx',
				valueField : 'id',
				textField : 'text',
				editable : false,
				panelHeight : 'auto'
			});
		});
	</script>
  </head>
  
  <body>
  	<h3 align="center">批量导入配送跟进信息</h3>
  	<table align="center" border="1" width='600px' >
  		<tr><td>
			<form action="${pageContext.request.contextPath}/EffectDeliverController/uploadEffectPostInfo.mmx" method="post" enctype="multipart/form-data" onsubmit="return checkUpload();"> 
		  		<table class="gridtable">
		  			<tr>
		  				<td><input type="file" name="attendance" id="f" />&nbsp;&nbsp;&nbsp;&nbsp;(请选择上传Excel)</td>
		  				<mmb:permit value="3063">
		  				<td colspan="2"><input type="submit" value="确认提交" /></td>
		  				</mmb:permit>
		  			</tr>
		  		</table>
		  	</form>
  		</td>
  		</tr>
  		  			
  		<tr><td>  		
  			操作信息
  			<div style="float: right;  position: relative; "><a href="${pageContext.request.contextPath}/EffectDeliverController/downloadEffectPostInfo.mmx">下载模板</a></div>
  		</td>
  		</tr>
  		<tr><td>
  			<% if(result != null){ %>
  				<div style="color: blue;"> <%=result %> </div>
  			<%} %>
  			<% if(msg != null){ %>
  				<div style="color: red;"> <%=msg %> </div>
  			<%} %>
  			<% if(msg == null && result == null){ %>
  				&nbsp;
  			<%} %>
  		</td>
  		</tr>
  	</table>

  	<div>
  	</div>
  </body>
</html>
