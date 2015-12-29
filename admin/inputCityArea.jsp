<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导入区域接到信息</title>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function isSubmit(){
	var fileObj = document.getElementById("infileId");
	var fileName = fileObj.value;
	if(fileObj.value==""){
		alert("文件名不能为空！");
		return false;
	}else if(fileName.indexOf(".xls")==-1){
		alert("文件格式只能是Excel(.xls)格式！");
		return false;
	}
	return true;
	
}

</script>
</head>
<body>
<form action="cityAreaOrder.do" method="post" enctype="multipart/form-data" name="cityAreaForm" onsubmit="return isSubmit();">
<div>导入区域接到信息，文件格式只能是excel文件且要符合规定的格式。</div>
文件名：<input type="file" name="inFile" id="infileId"/>
<input type="hidden" name="actionFlag" value="0"/>
<br/><input type="submit" value="导 入"/>
</form>
</body>
</html>