<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<title>货物运输交接单</title>
<style type="text/css">
<!--
.STYLE1 {
	font-size: large;
	font-weight: bold;
}
-->
</style>
</head>
<%
	List mbList = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	String code = (String) request.getAttribute("code");
	String carrier = (String) request.getAttribute("carrier");
	MailingBatchParcelBean mbBean = null;
	MailingBatchParcelBean bean = null;
	int count = mbList.size();
	int pageNum=count%20==0?count/20:((count-count%20)/20+1);
%>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<script type="text/javascript">
//CheckLodop();
function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	<%for (int  a = 0; a < pageNum; a++){%>
	if(LODOP.PRINT_INIT("")){
		        LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
		        LODOP.ADD_PRINT_TABLE("0.3cm","0.6cm","18.2cm","20cm",cssStyle+document.getElementById("tableDiv<%=a%>").innerHTML);
		        LODOP.ADD_PRINT_TABLE("14.8cm","0.6cm","18.2cm","20cm",cssStyle+document.getElementById("tableDiv1<%=a%>").innerHTML);
				LODOP.ADD_PRINT_BARCODE("5mm","145mm","10mm","20mm","CODE93","<%=code%>");
				LODOP.ADD_PRINT_BARCODE("150mm","145mm","10mm","20mm","CODE93","<%=code%>");
				LODOP.NEWPAGE();
				//LODOP.PREVIEWB();
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.PRINTB();
	}
	<%}%>
	
	
}
</script>
<body>
<%
	for (int a = 0; a < pageNum; a++){
%>
<div id="tableDiv<%=a%>" align="center">
    <table  border="1" width='700' height="200" bordercolor="#000000" cellpadding="0" cellspacing="0">
      <tr>
        <td colspan="6" height="85"><h2 align="center">货物运输交接单</h2></td>
    </tr>
      <tr>
        <td colspan="6"><div align="right">日期：<%=DateUtil.getNow().substring(0, 4)%> 年<%=DateUtil.getNow().substring(5, 7)%> 月<%=DateUtil.getNow().substring(8, 11)%> 日</div></td>
    </tr>
      
      <tr>
        <td colspan="2">委托人:</td>
      <td colspan="2">承运人:<%=carrier %></td>
      <td colspan="2">波次编号:<%=code%></td>
    </tr>
      
      <tr>
      <td><div align="center">序号</div></td>
      <td><div align="center">邮包编号</div></td>
      <td><div align="center">物流渠道</div></td>
      <td><div align="center">件数</div></td>
      <td><div align="center">重量(kg)</div></td>
      <td><div align="center">备注</div></td>
    </tr>
    
      <%
        int PackageCount =0;
        float TotalWeight = 0;
      	if (mbList != null) {
      		for (int i = 0; i < 20; i++) {
      			if(i+a*20>=count){%>
      			<tr>
      			  <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      </tr>
      			<%continue;}
      			mbBean = (MailingBatchParcelBean) mbList.get(i+a*20);
      			PackageCount+=mbBean.getPackageCount();
      			TotalWeight+=mbBean.getTotalWeight();
      			
      %>
      <tr>
      <td><div align="center"><%=a*20+i+1 %></div></td>
      <td><div align="center"><%=mbBean.getCode()%></div></td>
      <td><div align="center"><%=mbBean.getMbb().getStore()%></div></td>
      <td><div align="center"><%=mbBean.getPackageCount()%></div></td>
      <td><div align="center"><%=mbBean.getTotalWeight()/1000%></div></td>
      <td><div align="center">&nbsp;</div></td>
    </tr>
      <%
      	}
      	}
      %>
    <tr>
      <td><div align="center">合计</div></td>
      <td><div align="center">&nbsp;</div></td>
      <td><div align="center">&nbsp;</div></td>
      <td><div align="center"><%=PackageCount %></div></td>
      <td><div align="center"><%=TotalWeight/1000%></div></td>
      <td><div align="center">&nbsp;</div></td>
    </tr>
    <tr>
      <td colspan="6">承运人:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;制单:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;复核:</td>
    </tr>
    </table>
  </div>
  <%} %>
  <%
	for (int a = 0; a < pageNum; a++){
%>
<div id="tableDiv1<%=a%>" align="center">
    <table  border="1" width='700' height="200" bordercolor="#000000" cellpadding="0" cellspacing="0">
      <tr>
        <td colspan="6" height="85"><h2 align="center">货物运输交接单</h2></td>
    </tr>
      <tr>
        <td colspan="6"><div align="right">日期：<%=DateUtil.getNow().substring(0, 4)%> 年<%=DateUtil.getNow().substring(5, 7)%> 月<%=DateUtil.getNow().substring(8, 11)%> 日</div></td>
    </tr>
      
      <tr>
        <td colspan="2">委托人:</td>
      <td colspan="2">承运人:<%=carrier %></td>
      <td colspan="2">波次编号:<%=code%></td>
    </tr>
      
      <tr>
      <td><div align="center">序号</div></td>
      <td><div align="center">邮包编号</div></td>
      <td><div align="center">物流渠道</div></td>
      <td><div align="center">件数</div></td>
      <td><div align="center">重量(kg)</div></td>
      <td><div align="center">备注</div></td>
    </tr>
    
      <%
        int PackageCount =0;
        float TotalWeight = 0;
      	if (mbList != null) {
      		for (int i = 0; i < 20; i++) {
      			if(i+a*20>=count){%>
      			<tr>
      			  <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      </tr>
      			<%continue;}
      			mbBean = (MailingBatchParcelBean) mbList.get(i+a*20);
      			PackageCount+=mbBean.getPackageCount();
      			TotalWeight+=mbBean.getTotalWeight();
      			
      %>
      <tr>
      <td><div align="center"><%=a*20+i+1 %></div></td>
      <td><div align="center"><%=mbBean.getCode()%></div></td>
      <td><div align="center"><%=mbBean.getMbb().getStore()%></div></td>
      <td><div align="center"><%=mbBean.getPackageCount()%></div></td>
      <td><div align="center"><%=mbBean.getTotalWeight()/1000%></div></td>
      <td><div align="center">&nbsp;</div></td>
    </tr>
      <%
      	}
      	}
      %>
    <tr>
      <td><div align="center">合计</div></td>
      <td><div align="center">&nbsp;</div></td>
      <td><div align="center">&nbsp;</div></td>
      <td><div align="center"><%=PackageCount %></div></td>
      <td><div align="center"><%=TotalWeight/1000 %></div></td>
      <td><div align="center">&nbsp;</div></td>
    </tr>
    <tr>
      <td colspan="6">承运人:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;制单:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;复核:</td>
    </tr>
    </table>
  </div>
  <%} %>
</body>	
<script type="text/javascript">
initPrint();
window.close();
</script>
</html>
