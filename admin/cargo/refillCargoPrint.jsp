<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%
	CargoOperationBean cargoOperation = (CargoOperationBean)request.getAttribute("cargoOperation");
	List cocList = (List)request.getAttribute("cocList");
	List copyList = new ArrayList();
	copyList.addAll(cocList);
	int index = 0;
	TreeMap printMap=(TreeMap)request.getAttribute("printMap");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>物流内部补货单打印</title>
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
	</head>
	<body>
	<%--
	<%
	if(cocList!=null){
	int count = 0;
	int totalStockCount = 0;
	for(int i=0;i<cocList.size();i++){ 
		CargoOperationCargoBean inCoc = (CargoOperationCargoBean)cocList.get(i);
		List outCocList=(List)inCoc.getCocList(); 
		for(int j=0;j<outCocList.size();j++){
			CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
				if(count%10==0){
					count = 0;
					
			%>
				<div align="center" id="printDiv<%=index%>">
				<table cellpadding="0" cellspacing="0" width="570" height="400" border="1" >
				<tr>
					<td colspan="3"  style="border-right:0;height:60px; text-align:center; vertical-align: middle;"><h2>物流内部补货单</h2></td>
					<td height="60" align="right" colspan="3" id="barcodeID<%=index %>" style="border-left: 0;"><div id="barcodeImage<%=index%>"><%=cargoOperation.getCode() %></div></td>
				</tr>
				<tr>
					<td colspan="3">页码：<%=index+1%> &nbsp;仓库：<%=StringUtil.convertNull((String)request.getAttribute("stockAreaName"))+StringUtil.convertNull((String)request.getAttribute("stockTypeName")) %></td>
					<td colspan="3" align="left">制单人及日期：<%=cargoOperation.getCreateUserName()+" "+StringUtil.getString(cargoOperation.getCreateDatetime(),10)  %></td>
				</tr>
				<tr>
					<td>序</td>
					<td>编号</td>
					<td>原名称</td>
					<td>源货位</td>
					<td align="center">数量</td>
					<td>目的货位</td>
				</tr>
			<%	
				index++;
				} %>
				<tr>
					<td><%=count+1 %></td>
					<td><%=inCoc.getProduct().getCode() %></td>
					<td style="font-size: 10px;"><%=StringUtil.getString(inCoc.getProduct().getOriname(),50) %></td>
					<td><b><%=outCoc.getCargoInfo().getWholeCode() %></b></td>
					<td align="center"><b><%=outCoc.getStockCount() %></b></td>
					<td><b><%=inCoc.getCargoInfo().getWholeCode() %></b></td>
				</tr>
			<%
			count++;
			totalStockCount = totalStockCount + outCoc.getStockCount();
			
			if(count%10==0){
				%>		
					<tr>
						<td>&nbsp;</td>
						<td align="center">小计</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td align="center"><b><%=totalStockCount %></b></td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">审核人及日期：<%if(!StringUtil.convertNull(cargoOperation.getAuditingUserName()).equals("")){ %><%=cargoOperation.getAuditingUserName()+" "+StringUtil.getString(cargoOperation.getAuditingDatetime(),10)  %><%} %></td>
						<td colspan="3">作业人签字及日期：</td>
					</tr>
					</table>
					</div>
					<br/>
					<br/>
					<%
						
					}
			}
		}
	%>
	<%	
				if(count > 0 && count < 10){
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
			<%			}while(count < 10);
					}
				if(count%10==0){
			%>		
				<tr>
					<td>&nbsp;</td>
					<td align="center">合计</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td align="center"><b><%=totalStockCount %></b></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3">审核人及日期：<%if(!StringUtil.convertNull(cargoOperation.getAuditingUserName()).equals("")){ %><%=cargoOperation.getAuditingUserName()+" "+StringUtil.getString(cargoOperation.getAuditingDatetime(),10)  %><%} %></td>
					<td colspan="3">作业人签字及日期：</td>
				</tr>
				</table>
				</div>
				<br/>
				<br/>
				<%
					
				} 
				%>
			<%	} %>
<p align="center"><input type="button" value="打 印" onclick="printOrder();"/>&nbsp;&nbsp;&nbsp;<input type="button" value="关 闭" onclick="window.close();"/></p>
 --%>
 <%
if(printMap!=null){ 
int count = 0;
int totalStockCount = 0; 
Iterator iter=printMap.keySet().iterator();
while(iter.hasNext()){
	String key=iter.next().toString();
	CargoOperationCargoBean printCoc=(CargoOperationCargoBean)printMap.get(key);
	if(count%10==0){
		count = 0;
	%>
		<div align="center" id="printDiv<%=index%>">
			<table cellpadding="0" cellspacing="0" width="570" height="400" border="1" >
				<tr>
					<td colspan="3"  style="border-right:0;height:60px; text-align:center; vertical-align: middle;"><h2>物流内部补货单</h2></td>
					<td height="60" align="right" colspan="3" id="barcodeID<%=index %>" style="border-left: 0;"><div id="barcodeImage<%=index%>"><%=cargoOperation.getCode() %></div></td>
				</tr>
				<tr>
					<td colspan="3">页码：<%=index+1%> &nbsp;仓库：<%=StringUtil.convertNull((String)request.getAttribute("stockAreaName"))+StringUtil.convertNull((String)request.getAttribute("stockTypeName")) %></td>
					<td colspan="3" align="left">制单人及日期：<%=cargoOperation.getCreateUserName()+" "+StringUtil.getString(cargoOperation.getCreateDatetime(),10)  %></td>
				</tr>
				<tr>
					<td>序</td>
					<td>编号</td>
					<td>原名称</td>
					<td>源货位</td>
					<td align="center">数量</td>
					<td>目的货位</td>
				</tr>
	<%	index++;
	} %>
	<tr>
		<td><%=count+1 %></td>
		<td><%=printCoc.getProduct().getCode() %></td>
		<td style="font-size: 10px;"><%=printCoc.getProduct().getOriname() %></td>
		<td><b><%=printCoc.getOutCargoWholeCode() %></b></td>
		<td align="center"><b><%=printCoc.getStockCount() %></b></td>
		<td><b><%=printCoc.getInCargoWholeCode() %></b></td>
	</tr>
	<%
	count++;
	totalStockCount = totalStockCount + printCoc.getStockCount();
	if(count%10==0&&index>0){ %>		
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
	
if(count > 0 && count < 10){
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
	}while(count < 10);
}
if(count%10==0&&index>0){ %>		
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
			
<%} %>
<p align="center"><input type="button" value="打 印" onclick="printOrder();"/>&nbsp;&nbsp;&nbsp;<input type="button" value="关 闭" onclick="window.close();"/></p>
 
<script type="text/javascript">
	function printOrder(){
		cssStyle = "<style>table{font-size:14px;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		LODOP.PRINT_INIT("物流内部补货单打印");
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
		var barImage = null;
		var barcodeTD = null;
		var barcodeHId = null;
		var top=158;
		var barcodeWidth=48.7;
		var barcodeLeft=127;
		var indexT=<%=index-1%>;
		var code="<%=cargoOperation.getCode()%>";
		for(var i=0;i<=indexT;i+=1){
			barImage= document.getElementById("barcodeImage"+i);
			barcodeTD= document.getElementById("barcodeID"+i);
			barcodeTD.removeChild(barImage);
			LODOP.ADD_PRINT_TABLE("2cm","2.62cm","15.50cm","11.27cm",cssStyle+document.getElementById("printDiv"+i).innerHTML);
			LODOP.ADD_PRINT_BARCODE("21.7mm",barcodeLeft+"mm",barcodeWidth+"mm","13.2mm","128A",code);
			barcodeTD.appendChild(barImage);
			LODOP.NEWPAGE();
		}
		//LODOP.PREVIEWB();
		//LODOP.PRINT_DESIGN();
		LODOP.PRINTB();
	}

</script>
	</body>
</html>