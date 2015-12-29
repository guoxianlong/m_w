<%@page import="mmb.stock.cargo.CartonningInfoBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.ProductBarcodeVO"%>
<%
	CargoOperationBean cargoOperation = (CargoOperationBean)request.getAttribute("cargoOperation");
	int index = 0;
	TreeMap printMap=(TreeMap)request.getAttribute("printMap");
	List productBarList=(List)request.getAttribute("productBarList");
	List cisList=(List)request.getAttribute("cisList");
	List countList=(List)request.getAttribute("countList");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="mmb.stock.cargo.CartonningProductInfoBean"%><html>
	<head>
		<title>物流内部货位间调拨单打印</title>
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
	</head>
	<body>
 <div align="center" id="printDiv">
			<table cellpadding="0" cellspacing="0"   border="1" width="720">
				<tr>
					<td colspan="4"  style="border-right:0;height:60px; text-align:center; vertical-align: middle;"><h2>物流货位间调拨单</h2></td>
					<td height="60" align="right" colspan="3" id="barcodeID" style="border-left: 0;"><div id="barcodeImage"><%=cargoOperation.getCode() %></div></td>
				</tr>
				<tr height="18">
					<td colspan="4" >页码：<%=index+1%> &nbsp;仓库：<%=StringUtil.convertNull((String)request.getAttribute("stockAreaName"))+StringUtil.convertNull((String)request.getAttribute("stockTypeName")) %></td>
					<td colspan="3" align="left">制单人及日期：<%=cargoOperation.getCreateUserName()+" "+StringUtil.getString(cargoOperation.getCreateDatetime(),10)  %></td>
				</tr>
				<tr height="34">
				  <td width="20"><div align="center">序</div></td>
				  <td width="90"><div align="center">商品编号</div></td>
				  <td width="100"><div align="center">装箱单号</div></td>
				  <td width="220"><div align="center">原名称</div></td>
				  <td width="130"><div align="center">源货位</div></td>
				  <td width="50" align="center"><div align="center">数量</div></td>
				  <td width="130"><div align="center">目的货位</div></td>
				</tr>
 <%
if(printMap!=null){ 
int count = 0;
int totalStockCount = 0; 
int countx =0;
Iterator iter=printMap.keySet().iterator();
while(iter.hasNext()){
	int productCount=0;
	String key=iter.next().toString();
	CargoOperationCargoBean outCoc=(CargoOperationCargoBean)printMap.get(key);
	if(outCoc.getCartonningList().size()>0){
	%>
	<tr>
	<td rowspan="<%=outCoc.getCartonningList().size()%>"><div align="center"><%=count+1 %></div></td>
	<td rowspan="<%=outCoc.getCartonningList().size()%>" align="center"><div align="center"><%=((CartonningProductInfoBean)((CartonningInfoBean)(outCoc.getCartonningList().get(0))).getProductBean()).getProductCode() %></div></td>
	<%for(int i=0;i<1;i++){ %>
		<%CartonningInfoBean cartonningInfo=(CartonningInfoBean)(outCoc.getCartonningList().get(i)); %>
		<td align="center"><%=cartonningInfo.getCode() %></td>
	<%} %>
	<%for(int i=0;i<outCoc.getCartonningList().size();i++){ %>
		<%CartonningInfoBean cartonningInfo=(CartonningInfoBean)(outCoc.getCartonningList().get(i)); %>
		<%productCount+=cartonningInfo.getProductBean().getProductCount(); %>
	<%} %>
	<td style="font-size: 10px;" rowspan="<%=outCoc.getCartonningList().size()%>"><div align="center"><%=((CartonningProductInfoBean)((CartonningInfoBean)(outCoc.getCartonningList().get(0))).getProductBean()).getProductName() %></div></td>
	<td rowspan="<%=outCoc.getCartonningList().size()%>"><div align="center"><b><%=outCoc.getOutCargoWholeCode() %></b></div></td>
	<td align="center" rowspan="<%=outCoc.getCartonningList().size()%>"><div align="center"><b><%=productCount %></b></div></td>
	<td rowspan="<%=outCoc.getCartonningList().size()%>"><div align="center"><b><%=((CartonningInfoBean)(outCoc.getCartonningList().get(0))).getCargoWholeCode() %></b></div></td>
	</tr>
	<%for(int i=1;i<outCoc.getCartonningList().size();i++){ %>
		<%CartonningInfoBean cartonningInfo=(CartonningInfoBean)(outCoc.getCartonningList().get(i)); %>
		<tr><td align="center"><%=cartonningInfo.getCode() %></td></tr>
	<%} %>
	<%}else{ %>
	<%productCount = outCoc.getStockCount(); %>
	<td rowspan="1"><div align="center"><%=count+1 %></div></td>
	<td rowspan="1" align="center"><div align="center"><%=outCoc.getProduct().getCode() %></div></td>
	<td align="center"></td>
	<td style="font-size: 10px;" rowspan="1"><div align="center"><%=outCoc.getProduct().getOriname() %></div></td>
	<td rowspan="1"><div align="center"><b><%=outCoc.getOutCargoWholeCode() %></b></div></td>
	<td align="center" rowspan="1"><div align="center"><b><%=outCoc.getStockCount() %></b></div></td>
	<td rowspan="1"><div align="center"><b><%=outCoc.getInCargoWholeCode() %></b></div></td>
	</tr>
	<%} %>
	<%count++; %>
	<%
	totalStockCount = totalStockCount + productCount;
	 %>	
<%} %>
	<tr>
		<td>&nbsp;</td>
		<td align="center">合计</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="center"><b><%=totalStockCount %></b></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4">审核人及日期：<%if(cargoOperation.getAuditingUserName()!=null && !cargoOperation.getAuditingUserName().equals("")){ %><%=cargoOperation.getAuditingUserName()+" "+StringUtil.getString(cargoOperation.getAuditingDatetime(),10)  %><%} %></td>
		<td colspan="3">作业人签字及日期：</td>
	</tr>
</table>
</div><br/><br/>
<%} %>
<p align="center"><input type="button" value="打 印" onClick="printOrder();"/>&nbsp;&nbsp;&nbsp;<input type="button" value="关 闭" onClick="window.close();"/></p>

<script type="text/javascript">
	function printOrder(){
		cssStyle = "<style>table{font-size:14px;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		LODOP.PRINT_INIT("物流内部货位间调拨单打印");
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
		var barImage = null;
		var barcodeTD = null;
		var barcodeHId = null;
		var top=158;
		var barcodeWidth=48.7;
		var barcodeLeft=125;
		<%int next=0;%>
		var indexT=<%=index-1%>;
		var code="<%=cargoOperation.getCode()%>";

		
			barImage= document.getElementById("barcodeImage");
			barcodeTD= document.getElementById("barcodeID");
			barcodeTD.removeChild(barImage);
			LODOP.ADD_PRINT_TABLE("0.5cm","0.1cm","50.50cm","26cm",cssStyle+document.getElementById("printDiv").innerHTML);
			LODOP.ADD_PRINT_BARCODE("7mm",barcodeLeft+"mm",barcodeWidth+"mm","13.2mm","128A",code);
			
			
			barcodeTD.appendChild(barImage);
			LODOP.NEWPAGE();

		
		//}
		//LODOP.PREVIEWB();
		//LODOP.PRINT_DESIGN();
		LODOP.PRINTB();
	}

</script>
<script type="text/javascript">printOrder();</script>
	</body>
</html>