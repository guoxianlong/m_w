<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="mmb.stock.cargo.CartonningInfoBean"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 

"http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

CartonningInfoBean bean=(CartonningInfoBean)request.getAttribute("bean");
String url = (String)request.getAttribute("url");
String lineName = (String)request.getAttribute("lineName");
String zx_type = "";
if(url.indexOf("Quality")>0){
	zx_type = "质检装箱";
}else{
	zx_type = "作业装箱";
}
%>
<html>
<head>
<title>打印装箱单</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<style type="text/css">
<!--
.STYLE19 {
	font-size: x-large;
	font-weight: bold;
}
-->
</style>
<object
       id="CEActiveXDemoID"
       classid="clsid:A6E45A9F-6CF7-4566-A9B0-23C820FA1574"
       name= "printActiveX" 
       >
 </object> 
</head>
<body>
<script type="text/javascript">

CEActiveXDemo();
function CEActiveXDemo(){
    try{ 
      CEActiveXDemoID.doPrint("备用参数","<%=bean.getProductBean().getProductCode()%>","<%=bean.getCode()%>",
      "<%=bean.getProductBean().getProductCount() %>","<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>",
      "<%=bean.getCreateTime().substring(0, 19) %>","<%=zx_type%>","<%=lineName  %>");
    }
    catch(ex){
        alert("打印异常" + ex.description);
    } 
	window.location="<%=request.getContextPath()%>/admin/cargo/<%=url%>";
}
</script>
</body>
</html>