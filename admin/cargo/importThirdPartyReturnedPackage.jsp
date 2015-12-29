<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*,mmb.stock.stat.*"%>
<%
	String successTip = request.getParameter("successTip");
	String errorMsg = StringUtil.convertNull(request.getParameter("errorMsg"));
	String totalCount = (String)request.getAttribute("totalCount");
	String successCount = (String)request.getAttribute("successCount");
	String failCount = (String)request.getAttribute("failCount");
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
%>
<html>
  <head>
    
    <title>导入第三方物流退单</title>
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
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		//String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		function check(){
			
		}
		function importInfos() {
			var infos = document.getElementById("importInfo").value;
   			if( infos == null || infos == "" ) {
   				alert("不能提交空值！");
   				return false;
   			}
   			alert(infos);
   				$.ajax({
                        type: "GET", //调用方式  post 还是 get
                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=importThirdPartyReturnedPackage&importInfo="+infos+"&changeMark="+Math.random(), //访问的地址
                        dataType: "text", //返回的数据的形式
                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
                        	alert("success");
                        },
                        error: function() {          //如果过程中出错了调用的方法
                             alert("验证出错");
                        }
                  });
		
			return false;
		}
		
		function resetPage() {
		}
		
		
	</script>
  </head>
<body>
<input type="hidden" id="current_condition" value="0" />
<div style="margin-left:15px;margin-top:15px;">
  	<table width="400px"><tr align="center"><td><h2>&nbsp;导入第三方物流退单</h2></td></tr></table>
   	<fieldset style="width:400px;">
   		<div style="background-color:#CCFF80;width:400px;height:420px;border-style:solid;border-width:1px;border-color:#000000;">
   		<div style="margin-left:0px;">
   			<br>
   			<table border="0" cellspacing="12" width="400px">
   			<form action="<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=importThirdPartyReturnedPackage" method="post" >
   			<tr>
   			<td>
   			</td>
   				<td colspan="3" align="center">
   				<textarea rows="10" cols="50" id="importInfo" name="importInfo"></textarea>
   				</td>
   			</tr>
   			<tr>
   				<td></td>
   				<td colspan="3" align="right">
   					<%= wareAreaSelectLable %>
   					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   					<input type="submit" value="  添加  " height="20px"  />&nbsp;&nbsp;&nbsp;&nbsp;
   				</td>
   			</tr>
   			</form>
   			<tr>
   			<td>
   			</td>
   			
   				<td colspan="3" align="center" >
   					<textarea  rows="10" cols="50" id="tipArea" style="color:red;"><%= errorMsg%></textarea>
   				</td>
   			</tr>
   			</div>
   		</div>
   		<br>
   			</table>
   	</fieldset>
   	 &nbsp;&nbsp;&nbsp;&nbsp;导入数:<font color="red"> <%= totalCount == null ? "0" : totalCount %> </font> &nbsp;&nbsp;&nbsp;&nbsp;
   	 成功数：<font color="red"><%= successCount == null ? "0" : successCount%></font> &nbsp;&nbsp;&nbsp;&nbsp;
   	 失败数：<font color="red"><%= failCount == null ? "0" : failCount %></font>
   	</div>

</body>
</html>
