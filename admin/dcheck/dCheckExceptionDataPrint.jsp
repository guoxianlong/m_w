<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*"%>
<% 
	List<HashMap<String,Object>> rsLst=(List<HashMap<String,Object>>)request.getAttribute("rsLst");
	int pageNum=0;
%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>打印盘点异常单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<style type="text/css">
body {
	font-size: 12px;
}
</style>
</head>
<body>
	<%if(rsLst!=null&&rsLst.size()>0){%>
	<div align="center">
		<% 
			pageNum=0;
			String stockAreaId="";
			int lineNumPerPage=42,charNumPerLine=20,lineNum=1,existRowSpan=0;
			int curLineNumPerPage=lineNumPerPage;
			for(int i=0;i<rsLst.size();i++){
				HashMap<String,Object> perMap = rsLst.get(i);
				if(!stockAreaId.equals(String.valueOf(perMap.get("stockAreaId")))){
					lineNum=1;
					curLineNumPerPage=lineNumPerPage;
					existRowSpan=0;
					pageNum++;
				}else if(lineNum>curLineNumPerPage){
					lineNum=1;
					curLineNumPerPage=lineNumPerPage+4;	
					existRowSpan=0;
					pageNum++;
				}
				stockAreaId=String.valueOf(perMap.get("stockAreaId"));
				boolean newPage=false;
				if(lineNum==1){
					newPage=true;
				}
				//同一货位商品时须合并单元格
				int rowSpan=1;
				String cargoId=String.valueOf(perMap.get("cargoId"));
				if(existRowSpan==0){
					for(int j=i+1;j<=i+curLineNumPerPage-lineNum&&j<rsLst.size();j++){
						HashMap<String,Object> afterMap = rsLst.get(j);
						if(cargoId.equals(String.valueOf(afterMap.get("cargoId")))){
							rowSpan++;
							//如果文字超出charNumPerLine，则换行
							String trueProductName=String.valueOf(afterMap.get("trueProductName"));
							String productName=String.valueOf(afterMap.get("productName"));
							if(trueProductName.length()>charNumPerLine||productName.length()>charNumPerLine){
								rowSpan++;
							}
						}else{
							break;
						}
					}
				}
				//如果文字超出charNumPerLine，则换行
				String trueProductName=String.valueOf(perMap.get("trueProductName"));
				String productName=String.valueOf(perMap.get("productName"));
				boolean outLineFlg=false;
				if(trueProductName.length()>charNumPerLine||productName.length()>charNumPerLine){
					lineNum++;
					rowSpan++;
					outLineFlg=true;
				}
				if(rowSpan>1){
					existRowSpan=rowSpan;
				}
		%>
		<%if(newPage){ %>
		<% if(pageNum>1){%>
			</table>
			</div>
			</br>
		<%} %>
		<div id="printArea<%=pageNum%>">
			<table table-layout="fixed" cellpadding="0" cellspacing="0" rules="rows" width="730" border="1" frame="below" style="border-bottom: 1px solid #000000; border-collapse: collapse; font-size: 12px; ">
		<%} %>	
				<%
				//同一区域有抬头
				if(newPage){%>
				<tr height="60px" style="border-bottom:#000000 solid 1px;">
					<td colspan="6">
						<table width="700" cellpadding="1" style="border: none; border-collapse: collapse;">
							<tr>
								<td style="text-align:center;width:500px;" colspan="2"><strong><font style="font-size:30">一盘组差异列表</font></strong></td>
								<td style="width:200px;"  colspan="2"><strong>Page：</strong><%=pageNum %></td>
							</tr>
							<%
							//同一区域第1页有抬头
							if(curLineNumPerPage==lineNumPerPage){%>	
							<tr>
								<td style="text-align:right;width:120px;"><strong>仓库：</strong></td><td style="text-align:left;width:350px;"><%=perMap.get("areaName") %></td><td><strong>日期：</strong></td><td>&nbsp;</td>
							</tr>
							<tr>								
								<td style="text-align:right;width:120px;"><strong>盘点计划号：</strong></td><td style="text-align:left;width:350px;"><%=perMap.get("dCheckCode") %></td><td><strong>盘点人员签名：</strong></td><td>&nbsp;</td>
							</tr>
							<tr>								
								<td style="text-align:right;width:120px;"><strong>区：</strong></td><td style="text-align:left;width:350px;"><strong><font style="font-size:40"><%=perMap.get("stockAreaCode") %></font></strong></td><td><strong>监盘人员签名：</strong></td><td>&nbsp;</td>
							</tr>
							<%}%>
						</table>
					</td>
				</tr>
				<%}
				//同一区域第1页有抬头
				if(newPage&&curLineNumPerPage==lineNumPerPage){%>				
				<tr style="border-bottom:#000000 solid 1px;">
					<td width="30" height="20"><div align="center" style="white-space:nowrap;"><strong>巷道</strong></div></td>
					<td width="110" height="20"><div align="center" style="white-space:nowrap;"><strong>库位</strong></div></td>
					<td width="80" height="20"><div align="center" style="white-space:nowrap;"><strong>商品编码</strong></div></td>
					<td width="230" height="20"><div align="center" style="white-space:nowrap;"><strong>商品名称</strong></div></td>
					<td width="230" height="20"><div align="center" style="white-space:nowrap;"><strong>商品原名称</strong></div></td>
					<td width="30" height="20"><div align="center" style="white-space:nowrap;"><strong>检查</strong></div></td>
				</tr>
				<%} %>
				<tr style="border-bottom:#000000 solid 1px;">
					<%if(existRowSpan>0){ 
						if(existRowSpan>1&&existRowSpan==rowSpan){
					%>
					<td height="20" rowspan="<%=rowSpan%>"><div align="center"><%=perMap.get("passageCode") %></div></td>
					<td height="20" rowspan="<%=rowSpan%>"><div align="center"><%=perMap.get("cargoWholeCode") %></div></td>
					<%	}
						existRowSpan--;
					%>
					<%}else if(existRowSpan==0){ 
					%>
					<td height="20"><div align="center"><%=perMap.get("passageCode") %></div></td>
					<td height="20"><div align="center"><%=perMap.get("cargoWholeCode") %></div></td>
					<%} %>
					<td height="20"><div align="center"><%=perMap.get("productCode") %></div></td>
					<td height="20">
					<div align="center">
						<%
							if(trueProductName.length()>charNumPerLine){
						%>
							<%=trueProductName.substring(0, charNumPerLine)%>
						<%
							}else{
						%>
							<%=trueProductName%>
						<%
							}
						%>
					</div></td>
					<td height="20">
					<div align="center">
						<%
							if(productName.length()>charNumPerLine){
						%>
							<%=productName.substring(0, charNumPerLine)%>
						<%
							}else{
						%>
							<%=productName%>
						<%
							}
						%>
					</div></td>					
					<td height="20"><div align="center">&nbsp;</div></td>
				</tr>
				<%if(outLineFlg){
					existRowSpan--;
				%>
				<tr style="border-bottom:#000000 solid 1px;">
					<td height="20"><div align="center">&nbsp;</div></td>
					<td height="20"><div align="center">
					<%
						if(trueProductName.length()>charNumPerLine){
							if(trueProductName.length()>charNumPerLine*2){
					%>
						<%=trueProductName.substring(charNumPerLine,charNumPerLine*2)%>
					<%
							}else{
					%>
						<%=trueProductName.substring(charNumPerLine)%>
					<%
							}
					%>											
					<%
						}
					%>
					</div></td>
					<td height="20"><div align="center">
					<%
						if(productName.length()>charNumPerLine){
							if(productName.length()>charNumPerLine*2){
					%>
						<%=productName.substring(charNumPerLine,charNumPerLine*2)%>
					<%
							}else{
					%>
						<%=productName.substring(charNumPerLine)%>
					<%
							}
					%>											
					<%
						}
					%>
					</div></td>			
					<td height="20"><div align="center">&nbsp;</div></td>					
				</tr>
				<%} %>

		<% 
				lineNum++;
			}
		%>
			</table>
		</div>
	<%}else{ %>
	<div style="text-align:center">无异常数据!</div>
	<%} %>
	<input type="hidden" id="pageNum" value="<%=pageNum%>">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width="0" height="0">
 <embed id="LODOP_EM" type="application/x-print-lodop" width="0" height="0"
		pluginspage="install_lodop.exe"></embed> </object>	
<script type="text/javascript">
	function initPrintData(){
		var pageNum=document.getElementById('pageNum').value;
		if(pageNum>0){
			var cssStyle = "<style>table{font-size:12px;}</style>";
			LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
			if(LODOP.PRINT_INIT("")){ 
				LODOP.SET_PRINT_PAGESIZE(0,"0","0","A4");
				for(var i=pageNum;i>=1;i--){
					LODOP.ADD_PRINT_TABLE("0.1cm","0.2cm","18.2cm","28.2cm",cssStyle+document.getElementById("printArea"+i).innerHTML);
					LODOP.NEWPAGE();
				}
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.PRINTB();
				window.close();
			}else{
				alert("打印控件初始化失败，请重新刷新后再试。");
			}
		}
	}
	initPrintData();
</script>
</body>
</html>