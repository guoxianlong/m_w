<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="mmb.stock.cargo.CartonningInfoBean"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

String code =(String)request.getAttribute("code");
String reason =(String)request.getAttribute("reason");
%>
<html>
<head>
<title>原因条码打印</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<style type="text/css">
<!--
.STYLE19 {
	font-size: x-large;
	font-weight: bold;
}
-->
</style>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
<div id="szzjPackage">
<!-- Save for Web Slices (配送单--标注.jpg) -->
<table width="371" height="141"   border="0" cellpadding="0" cellspacing="0" id="__01" style="font-family: SimHei;">
	
	<tr>
		<td height="50%" align="center" ></td>
	</tr>
	<tr>
	  <td align="center"  ><strong><font size="5" ><%=reason %></font></strong></td>
	</tr>
</table>
<p>
  <!-- End Save for Web Slices -->
</p>
<p>&nbsp; </p>
</div>
<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:12px;font-family:Microsoft YaHei;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		//LODOP.SET_PRINT_PAGESIZE(0,"100mm","280mm","");
		LODOP.ADD_PRINT_TABLE("0mm","0mm","40mm","70mm",cssStyle+document.getElementById("szzjPackage").innerHTML);
		LODOP.ADD_PRINT_BARCODE("5mm", "30mm","71mm", "15mm", "128A", "<%=code%>");
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
		return true;
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
		return false;
	}
}
</script>
<script type="text/javascript">
   initPrint();
   alert("打印成功,确定返回!");
   window.history.back(-1);
</script>
</body>
</html>