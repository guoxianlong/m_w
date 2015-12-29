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
    <title>导入订单配送信息页面</title>
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
	</script>
  </head>
  
  <body>
  	<h3 align="center">导入订单配送信息</h3>
  	<table border="1" width=500px>
  		<tr><td>
			<form action="${pageContext.request.contextPath}/uploadDeliverOrderInfoController/upload.mmx" method="post" enctype="multipart/form-data" onsubmit="return checkUpload();"> 
		  		<table clas="gridtable">
		  			<tr>
		  				<td>POP商家：
		  					<select name="pop">
		  						<option value="0">买卖宝</option>
		  						<option value="2">京东</option>
		  					</select>
		  				</td>
		  				<td colspan="2">
		  				</td>
		  			</tr>
		  			<tr>
		  				<td><input type="file" name="attendance" id="f" />&nbsp;&nbsp;&nbsp;&nbsp;(请选择上传Excel)</td>
		  				<td colspan="2"><input type="submit" value="确认提交" /></td>
		  			</tr>
		  		</table>
		  	</form>
  		</td>
  		</tr>
  		  			
  		<tr><td>  		
  			操作信息:
  			<div style="float: right;  position: relative; "><a href="${pageContext.request.contextPath}/uploadDeliverOrderInfoController/download.mmx">下载模板</a></div>
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
