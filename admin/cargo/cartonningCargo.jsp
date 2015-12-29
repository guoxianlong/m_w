<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html>
<head>
<title>装箱单关联货位</title>
<%
String reload = (String)request.getAttribute("reload");
%>
<script type="text/javascript">
function inputCode(){
	var code=document.getElementById("testCode").value;
	document.getElementById("code").value=code;
	document.getElementById("cargoWholeCode").focus();
	return false;
}

function reload(){
	<%if(reload!=null&&reload.equals("reload")){%>
	   window.opener.location.reload();
	<%}%>
}
</script>
</head>
<body onLoad="document.getElementById('cargoWholeCode').focus();">
<h2>装箱单关联货位</h2>
<h4>请输入需要关联的装箱单号和货位号：</h4>

	装箱单号：<%=request.getParameter("code") %>

<form action="cartonningInfoAction.do?method=cartonningCargo" method="post">
货 位 号:
  <input type="text" name="cargoWholeCode" id="cargoWholeCode" />
  
<input type="hidden" id="code" name="code" value='<%=request.getParameter("code")==null?"":request.getParameter("code") %>'/><br/><br/>
<input type="hidden" id="flag" name="flag" value='<%=request.getParameter("flag")==null?"":request.getAttribute("flag") %>'/><br/><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="确认提交">
<input type="button" value="取消" onClick="javascript:window.close();"/>
</form>
<script>
reload();
<%if(request.getAttribute("complete")!=null&&request.getAttribute("complete").toString().equals("1")){%>
window.close();
<%}%>
</script>
</body>
</html>