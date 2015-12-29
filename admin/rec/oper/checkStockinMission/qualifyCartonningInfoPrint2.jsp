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

CartonningInfoBean bean1=(CartonningInfoBean)request.getAttribute("bean");
int missionId = Integer.parseInt((String)request.getAttribute("missionId"));
int batchId = Integer.parseInt((String)request.getAttribute("batchId"));
String productLine = (String)request.getAttribute("productLine");
List<CartonningInfoBean> list = (List<CartonningInfoBean>)request.getAttribute("beanList");
StringBuffer sb = new StringBuffer();
for(int t = 0;t < list.size();t++){
	sb.append(list.get(t).getCode()+",");
}
sb=sb.deleteCharAt(sb.length()-1);
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
<% 
	for(int i=0;i<list.size();i++){
		CartonningInfoBean bean=list.get(i);
 %>
	<div id="szzjPackage<%=i %>">
	<!-- Save for Web Slices (配送单--标注.jpg) -->
	<table width="280" height="135"   border="2" cellpadding="0" cellspacing="0" id="__01" style="font-family: SimHei;">
		<tr>
			<td align="center" width="62" height="32">装箱记录</td>
			<td colspan="3"></td>
		</tr>
		<tr>
		  <td width="62" height="32"  align="center" >产品编号</td>
	      <td  align="center" colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td width="62" height="18"  align="center" >装箱数量</td>
	      	<td  align="center" width="50"><%=bean.getProductBean().getProductCount() %></td>
	      	<td width="62"  align="left" >装箱人</td>
	      	<td  align="center" ><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %></td>
		</tr>
		<tr>
		  <td height="18"  align="center" >装箱类型</td>
	      <td  align="center"  ><%=bean.getCauseName()%></td>
	      <td width="62"   align="left" >产品线</td>
	      	<td  align="center" ><%=productLine %></td>
	    </tr>
		<tr>
	      <td  align="center" height="18">装箱时间</td>
	      <td colspan="3" align="center"><%=bean.getCreateTime().substring(0, 19) %></td>
		</tr>
	</table>
	<p>
	  <!-- End Save for Web Slices -->
	</p>
	<p>&nbsp; </p>
	</div>
<% 
	}
 %>

<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:12px;font-family:Microsoft YaHei;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		var temp = <%=list.size() %>;
		var codelist = "<%=sb%>";
		var list = codelist.split(",");
		for(var i=0;i<temp;i++){
			LODOP.ADD_PRINT_TABLE("0mm","-1.5mm","35mm","74mm",cssStyle+document.getElementById("szzjPackage"+i).innerHTML);
			LODOP.ADD_PRINT_BARCODE("10.5mm", "18.5mm","36mm", "8mm", "128B", "<%=bean1.getProductBean().getProductCode()%>");
			LODOP.ADD_PRINT_BARCODE("1mm", "18.5mm","39mm", "9mm", "128B",  list[i]);
	        LODOP.SET_PRINTER_INDEX(-1);        
			//LODOP.PREVIEWB();
	        //window.location="qualifyPacking.mmx?batchId="+<%=batchId%>+"&missionId="+<%=missionId%>;
			LODOP.PRINTB();			
		}
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