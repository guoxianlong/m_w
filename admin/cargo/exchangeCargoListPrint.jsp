<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.ProductBarcodeVO"%>
<%
	List cargoOperationList = (List)request.getAttribute("cargoOperationList");
	List cocListList = (List)request.getAttribute("cocListList");
	List copyList = new ArrayList();
	int index = 0;
	int index1 = 0;
	List printMapList=(List)request.getAttribute("printMapList");
	List productBarLists=(List)request.getAttribute("productBarList");
	List operationBarList=(List)request.getAttribute("operationBarList");
	List errorList = (List)request.getAttribute("errorList");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>物流内部货位间调拨单打印</title>
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
	</head>
	<body>
<script type="text/javascript">
<%if(errorList!=null && errorList.size()>0){
	String error="";
	for(Iterator i=errorList.iterator();i.hasNext();){
		error+=i.next()+";";
	}
	%>
	window.opener.document.getElementById('list').value='作业单编号输入错误：<%=error%>';
	<%
}%>
</script>
 <%
 if(cargoOperationList!=null){
 for(int i=0;i<cargoOperationList.size();i++){
    List cocList = (List)cocListList.get(i);
	copyList.addAll(cocList);
	TreeMap printMap=(TreeMap)printMapList.get(i);
	CargoOperationBean cargoOperation = (CargoOperationBean)cargoOperationList.get(i);

if(printMap!=null){ 
int count = 0;
int totalStockCount = 0; 
Iterator iter=printMap.keySet().iterator();
while(iter.hasNext()){
	String key=iter.next().toString();
	CargoOperationCargoBean printCoc=(CargoOperationCargoBean)printMap.get(key);
	if(count%5==0){
		count = 0;
	%>
		<div align="center" id="printDiv<%=index%>">
			<table cellpadding="0" cellspacing="0"   border="1" width="720">
				<tr>
					<td colspan="3"  style="border-right:0;height:60px; text-align:center; vertical-align: middle;"><h2>物流货位间调拨单</h2></td>
					<td height="60" align="right" colspan="3" id="barcodeID<%=index %>" style="border-left: 0;"><div id="barcodeImage<%=index%>"><%=cargoOperation.getCode() %></div></td>
				</tr>
				<tr height="18">
					<td colspan="3" >页码：<%=index+1%> &nbsp;仓库：<%=StringUtil.convertNull((String)request.getAttribute("stockAreaName"))+StringUtil.convertNull((String)request.getAttribute("stockTypeName")) %></td>
					<td colspan="3" align="left">制单人及日期：<%=cargoOperation.getCreateUserName()+" "+StringUtil.getString(cargoOperation.getCreateDatetime(),10)  %></td>
				</tr>
				<tr height="34">
				  <td width="20"><div align="center">序</div></td>
				  <td width="260"><div align="center">编号</div></td>
				  <td width="150"><div align="center">原名称</div></td>
				  <td width="120"><div align="center">源货位</div></td>
				  <td width="50" align="center"><div align="center">数量</div></td>
				  <td width="120"><div align="center">目的货位</div></td>
				</tr>
	<%	index++;
	} %>
	<tr> 
	  <td height="50"><%=count+1 %></td>
		<td align="center" valign="top"><div align="left">商品编号:<%=printCoc.getProduct().getCode() %></div></td>
		<td style="font-size: 10px;"><%=printCoc.getProduct().getOriname() %></td>
		<td><b><%=printCoc.getOutCargoWholeCode() %></b></td>
		<td align="center"><b><%=printCoc.getStockCount() %></b></td>
		<td><b><%=printCoc.getCargoInfo().getWholeCode() %></b></td>
	</tr>
	<%
	count++;
	totalStockCount = totalStockCount + printCoc.getStockCount();
	if(count%5==0&&index>0){ %>		
		<tr>
			<td>&nbsp;</td>
			<td align="center">小计</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td align="center"><b><%=totalStockCount %></b></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">审核人及日期：<%if(cargoOperation.getAuditingUserName()!=null && !cargoOperation.getAuditingUserName().equals("")){ %><%=cargoOperation.getAuditingUserName()+" "+StringUtil.getString(cargoOperation.getAuditingDatetime(),10)  %><%} %></td>
			<td colspan="3">作业人签字及日期：</td>
		</tr>
	</table>
</div><br/><br/>
<%
	}	
}
	
if(count > 0 && count < 5){
	do{
		count++;			
%>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
<%
	}while(count < 5);
}
if(count%5==0&&index>0){ %>		
	<tr>
		<td>&nbsp;</td>
		<td align="center">合计</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="center"><b><%=totalStockCount %></b></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan="3">审核人及日期：<%if(cargoOperation.getAuditingUserName()!=null && !cargoOperation.getAuditingUserName().equals("")){ %><%=cargoOperation.getAuditingUserName()+" "+StringUtil.getString(cargoOperation.getAuditingDatetime(),10)  %><%} %></td>
		<td colspan="3">作业人签字及日期：</td>
	</tr>
</table>
</div><br/><br/>
<%
} 
%>
			
<%}}} %>




 <%
 if(cargoOperationList!=null){
 for(int i=0;i<cargoOperationList.size();i++){
    List cocList = (List)cocListList.get(i);
	copyList.addAll(cocList);
	TreeMap printMap=(TreeMap)printMapList.get(i);
	CargoOperationBean cargoOperation = (CargoOperationBean)cargoOperationList.get(i);

if(printMap!=null){ 
int count = 0;
int totalStockCount = 0; 
Iterator iter=printMap.keySet().iterator();
while(iter.hasNext()){
	String key=iter.next().toString();
	CargoOperationCargoBean printCoc=(CargoOperationCargoBean)printMap.get(key);
	if(count%5==0){
		count = 0;
	%>
		<div align="center" id="printDivm<%=index1%>">
			<table cellpadding="0" cellspacing="0"   border="1" width="720">
				<tr>
					<td colspan="3"  style="border-right:0;height:60px; text-align:center; vertical-align: middle;"><h2>物流货位间调拨单</h2></td>
					<td height="60" align="right" colspan="3" id="barcodeID1<%=index1 %>" style="border-left: 0;"><div id="barcodeImage1<%=index1%>"><%=cargoOperation.getCode() %></div></td>
				</tr>
				<tr height="18">
					<td colspan="3" >页码：<%=index1+1%> &nbsp;仓库：<%=StringUtil.convertNull((String)request.getAttribute("stockAreaName"))+StringUtil.convertNull((String)request.getAttribute("stockTypeName")) %></td>
					<td colspan="3" align="left">制单人及日期：<%=cargoOperation.getCreateUserName()+" "+StringUtil.getString(cargoOperation.getCreateDatetime(),10)  %></td>
				</tr>
				<tr height="34">
				  <td width="20"><div align="center">序</div></td>
				  <td width="260"><div align="center">编号</div></td>
				  <td width="150"><div align="center">原名称</div></td>
				  <td width="120"><div align="center">源货位</div></td>
				  <td width="50" align="center"><div align="center">数量</div></td>
				  <td width="120"><div align="center">目的货位</div></td>
				</tr>
	<%	index1++;
	} %>
	<tr height="50"> 
	  <td><%=count+1 %></td>
	<td align="center" valign="top"><div align="left">商品编号:<%=printCoc.getProduct().getCode() %></div></td>
		<td style="font-size: 10px;"><%=printCoc.getProduct().getOriname() %></td>
		<td><b><%=printCoc.getOutCargoWholeCode() %></b></td>
		<td align="center"><b><%=printCoc.getStockCount() %></b></td>
		<td><b><%=printCoc.getCargoInfo().getWholeCode() %></b></td>
	</tr>
	<%
	count++;
	totalStockCount = totalStockCount + printCoc.getStockCount();
	if(count%5==0&&index1>0){ %>		
		<tr>
			<td>&nbsp;</td>
			<td align="center">小计</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td align="center"><b><%=totalStockCount %></b></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">审核人及日期：<%if(cargoOperation.getAuditingUserName()!=null && !cargoOperation.getAuditingUserName().equals("")){ %><%=cargoOperation.getAuditingUserName()+" "+StringUtil.getString(cargoOperation.getAuditingDatetime(),10)  %><%} %></td>
			<td colspan="3">作业人签字及日期：</td>
		</tr>
	</table>
</div><br/><br/>
<%
	}	
}
	
if(count > 0 && count < 5){
	do{
		count++;			
%>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
<%
	}while(count < 5);
}
if(count%5==0&&index1>0){ %>		
	<tr>
		<td>&nbsp;</td>
		<td align="center">合计</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="center"><b><%=totalStockCount %></b></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan="3">审核人及日期：<%if(cargoOperation.getAuditingUserName()!=null && !cargoOperation.getAuditingUserName().equals("")){ %><%=cargoOperation.getAuditingUserName()+" "+StringUtil.getString(cargoOperation.getAuditingDatetime(),10)  %><%} %></td>
		<td colspan="3">作业人签字及日期：</td>
	</tr>
</table>
</div><br/><br/>
<%
} 
%>
			
<%}}} %>





<p align="center"><input type="button" value="打 印" onClick="printOrder();"/>&nbsp;&nbsp;&nbsp;<input type="button" value="关 闭" onClick="window.close();"/></p>


<script type="text/javascript">
	function printOrder(){
	
		cssStyle = "<style>table{font-size:14px;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	
			LODOP.PRINT_INIT("物流内部货位间调拨单打印");
		//LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
		var barImage = null;
		var barcodeTD = null;
		var barImage1 = null;
		var barcodeTD1= null;
		var barcodeHId = null;
		var top=158;
		var barcodeWidth=48.7;
		var barcodeLeft=125;
		<%CargoOperationBean cargoOperation=null; %>
		<%if(productBarLists!=null){
				 for(int i=0;i<index;i++){
					int next=0;
					List productBarList =  (List)productBarLists.get(i);%>
					barImage= document.getElementById("barcodeImage"+<%=i%>);
					barcodeTD= document.getElementById("barcodeID"+<%=i%>);
					barImage1= document.getElementById("barcodeImage1"+<%=i%>);
					barcodeTD1= document.getElementById("barcodeID1"+<%=i%>);
					barcodeTD.removeChild(barImage);
					barcodeTD1.removeChild(barImage1);
					LODOP.ADD_PRINT_TABLE("2.5cm","0.1cm","50.50cm","11.27cm",cssStyle+document.getElementById("printDiv<%=i%>").innerHTML);
					LODOP.ADD_PRINT_TABLE("14.7cm","0.1cm","50.50cm","11.27cm",cssStyle+document.getElementById("printDivm<%=i%>").innerHTML);
					<%if(operationBarList!=null){%>
						LODOP.ADD_PRINT_BARCODE("27mm",barcodeLeft+"mm",barcodeWidth+"mm","13.2mm","128A","<%=operationBarList.get(i)%>");
						LODOP.ADD_PRINT_BARCODE("150mm",barcodeLeft+"mm",barcodeWidth+"mm","13.2mm","128A","<%=operationBarList.get(i)%>");
					<%}%>
					<%
					for(int j=0;j<productBarList.size();j++){
						ProductBarcodeVO productBarbean=(ProductBarcodeVO)productBarList.get(j);
						if(j!=0&&j%5==0){
							next=0;
							
							break;
						}%>
						<%if(productBarbean!=null){%>
							LODOP.ADD_PRINT_BARCODE(59.9+<%=next%>+"mm",12+"mm","30mm","9mm","128A","<%=productBarbean.getBarcode()%>");
							LODOP.ADD_PRINT_BARCODE(181.9+<%=next%>+"mm",12+"mm","30mm","9mm","128A","<%=productBarbean.getBarcode()%>");
						<%}%>
						<%next=next+13;%>
					<%}%>
					barcodeTD.appendChild(barImage);
					barcodeTD1.appendChild(barImage1);
					LODOP.NEWPAGE();
				<%}
		}%>
		//LODOP.PREVIEWB();
		//LODOP.PRINT_DESIGN();
		LODOP.PRINTB();
	}

</script>
<script type="text/javascript">
	printOrder();
	
</script>
</body>
</html>