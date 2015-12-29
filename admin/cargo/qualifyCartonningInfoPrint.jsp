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

CartonningInfoBean bean=(CartonningInfoBean)request.getAttribute("bean");
int missionId = Integer.parseInt((String)request.getAttribute("missionId"));
int batchId = Integer.parseInt((String)request.getAttribute("batchId"));

%>
<html>
<head>
<title>质检入库装箱单</title>
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
<table width="371" height="141"   border="1" cellpadding="0" cellspacing="0" id="__01" style="font-family: SimHei;">
	
	<tr>
		<td height="38" colspan="3"  align="center" style="vertical-align: bottom;"><img src="<%=request.getContextPath()%>/image/logo_cartonning.jpg" width="96" height="36" align="left"/>
	      <font size="4">装箱记录</font></td>
	    <td colspan="3" align="center" ><div align="left"></div></td>
	</tr>
	<tr>
	  <td width="62" height="32"  align="center" ><strong>产品编号:</strong></td>
      <td colspan="2" align="center" >&nbsp;</td>
      <td colspan="2" align="center"  ><strong>数量:</strong></td>
	  <td width="69" align="center"  ><%=bean.getProductBean().getProductCount() %></td>
	</tr>
	<tr>
	  <td height="20"  align="center" ><strong>产品名称:</strong></td>
      <td colspan="5" align="center"  ><%=bean.getProductBean().getProductName() %></td>
    </tr>
	<tr>
	  <td height="28"  align="center" ><strong>装箱原因:</strong></td>
      <td width="52" align="center"><%=bean.getCauseName()%></td>
      <td width="68" align="center"><strong>装箱时间:</strong></td>
      <td width="60" align="center"><%=bean.getCreateTime().substring(0, 19) %></td>
	  <td width="46" align="center"><strong>责任人:</strong></td>
	  <td align="center">&nbsp;</td>
	</tr>
</table>
<p>
  <!-- End Save for Web Slices -->
</p>
<p>&nbsp; </p>
</div>

<div id="szzjPackage1">
<!-- Save for Web Slices (配送单--标注.jpg) -->
<table width="331" height="135"   border="2" cellpadding="0" cellspacing="0" id="__01" style="font-family: SimHei;">
	
	<tr>
		<td height="38" colspan="3"  align="center" style="vertical-align: bottom;"><img src="<%=request.getContextPath()%>/image/logo_cartonning.jpg" width="90" height="30" align="left"/>
	      <font size="2">装箱记录</font></td>
	    <td colspan="3" align="center" ><div align="left"></div></td>
	</tr>
	<tr>
	  <td width="62" height="32"  align="center" ><strong>产品编号:</strong></td>
      <td colspan="2" align="center" >&nbsp;</td>
      <td colspan="2" align="center"  ><strong><font size="-1">数量:</font></strong></td>
	  <td width="69" align="center"  ><%=bean.getProductBean().getProductCount() %></td>
	</tr>
	<tr>
	  <td height="20"  align="center" width="35" ><strong><font size="-1">产品名称:</font></strong></td>
      <td colspan="5" align="center"  ><font size="-1"><%=bean.getProductBean().getProductName() %></font></td>
    </tr>
	<tr>
	  <td height="20" width="35"  align="center" ><strong><font size="-1">装箱原因:</font></strong></td>
      <td width="52" align="center"><font size="-1"><%=bean.getCauseName()%></font></td>
      <td width="35" align="center"><strong><font size="-1">装箱时间:</font></strong></td>
      <td width="70" align="center"><%=bean.getCreateTime().substring(0, 19) %></td>
	  <td width="30" align="center"><strong><font size="-1">责任人:</font></strong></td>
	  <td align="center">&nbsp;</td>
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
		LODOP.ADD_PRINT_TABLE("0mm","-1.5mm","35mm","70mm",cssStyle+document.getElementById("szzjPackage1").innerHTML);
		LODOP.ADD_PRINT_BARCODE("13.5mm", "15.5mm","36mm", "8mm", "code93", "<%=bean.getProductBean().getProductCode()%>");
		LODOP.ADD_PRINT_BARCODE("1.5mm", "50.5mm","12mm", "9mm", "code93", "<%=bean.getCode()%>");
        //LODOP.SET_PRINTER_INDEX(-1);        
		LODOP.PREVIEWB();
        //window.location="qualifyPacking.mmx?batchId="+<%=batchId%>+"&missionId="+<%=missionId%>;
		//LODOP.PRINTB();
		return true;
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
		return false;
	}
}
</script>
<script type="text/javascript">
   initPrint();
   window.close();
</script>
</body>
</html>