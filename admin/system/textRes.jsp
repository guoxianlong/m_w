<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.system.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<%@ page import="adultadmin.util.*" %>
<%

TextResAction action = new TextResAction();
action.textRes(request, response);

TextResBean bean = (TextResBean) request.getAttribute("bean");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<!--

function checkSubmit(){
	with(textResForm){
		if(content.value==""){
			alert('内容不能为空');
			return false;
		}
		if(content.value.length > 20){
			alert('内容不能超过20个字');
			return false;
		}
	}
	return true;
}

//-->
</script>
<p align="center">文本资源</p>
<%if(bean != null){ %>
<form name="textResForm" method="post" action="editTextRes.jsp" onsubmit="return checkSubmit();">
类型：<select name="type">
	<option value="1">调拨原因</option>
</select><br/>
内容：<textarea name="content" cols="40" rows="5"><%=bean.getContent()%></textarea>
<input type="submit" value="修改">
<input type="reset" value="重置">
<input type="button" value="返回列表" onclick="window.location.href='textResList.jsp?type=<%= StringUtil.convertNull(request.getParameter("type")) %>';">
<br/>
<input type="hidden" name="textResId" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="textRes.jsp?textResId=<%= bean.getId() %>"/>
</form>
<%} else {%>
<form name="textResForm" method="post" action="addTextRes.jsp" onsubmit="return checkSubmit();">
类型：<select name="type">
	<option value="1">调拨原因</option>
</select><br/>
内容：<textarea name="content" cols="40" rows="5"></textarea><input type="submit" value="添加">
<input type="reset" value="重置">
<input type="button" value="返回列表" onclick="window.location.href='textResList.jsp?type=<%= StringUtil.convertNull(request.getParameter("type")) %>';">
<br/>
<input type="hidden" name="back" value="textRes.jsp"/>
</form>
<%}%>