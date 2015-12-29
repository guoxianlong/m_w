<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="adultadmin.action.barcode.ProductBarcodeAction"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加发货单客户信息打印</title>
<%
ProductBarcodeAction pba = new ProductBarcodeAction ();
pba.addConsigPringLog(request,response);
String printType = request.getParameter("printType");
printType=printType==null?"":printType;
String error = (String)request.getAttribute("tip");
if(error!=null){
	%><script>alert(<%=error%>);</script><%
}else if("0".equals(printType)){
	%><script>location.href="scanCheckCustomerInfo.jsp"</script><%
}else if("1".equals(printType)){
	%><script>location.href="../orderStock/scanCheckOrderStock.jsp"</script><%
}
%>
</head>
<body>

</body>
</html>