<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="mmb.stock.aftersale.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>封箱商品清单</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
</head>
<body>
	<%
	int count = 0;
	List resultList = (List)request.getAttribute("resultList");//需要打印的清单列表
	if(resultList!=null && resultList.size()>0){
		count = resultList.size();//封箱清单的个数
		for(int i=0;i<resultList.size();i++){
			Map item = (Map)resultList.get(i);
			if(item!=null){
				if(item.containsKey("afterSaleSeal") && item.containsKey("afterSaleSealProductList")){
					AfterSaleSeal sealBean = (AfterSaleSeal)item.get("afterSaleSeal");
					List<AfterSaleSealProduct> products = (List<AfterSaleSealProduct>)item.get("afterSaleSealProductList");
	%>
		 <div id="sealInventoryDiv<%=i%>">
		 	<table class="gridtable">
		 			<tr>
		 				<th colspan="8" align="center">封箱商品清单</th>
		 			</tr>
		 			<% 
		 				if(sealBean!=null){
		 			%>
		 			<tr>
		 				<td colspan="3">操作人：<%= StringUtil.convertNull(sealBean.getUserName()) %></td>
		 				<td colspan="3">封箱日期：<%= StringUtil.convertNull(sealBean.getCreateDatetime().substring(0,10)) %></td>
		 				<td colspan="2">封箱编号：<%= StringUtil.convertNull(sealBean.getCode()) %></td>
		 			</tr>
		 			<%	}
		 			 %>
		 			<tr>
		 				<th colspan="8" align="left">封箱商品明细</th>
		 			</tr>
		 			<tr>
			 			<th>序号</th>
			 			<th>商品名称</th>
			 			<th>型号</th>
			 			<th>IMEI</th>
			 			<th>售后单号</th>
			 			<th>封箱时售后单状态</th>
			 			<th>售后处理单号</th>
			 			<th>封箱时售后处理单状态</th>
			 		</tr>
			 		<% 
			 			if(products!=null && products.size()>0){
			 				for(int j=0;j<products.size();j++){
			 					AfterSaleSealProduct sealProduct = products.get(j);
			 		%>
			 				<tr>
			 					<td><%=j+1 %></td>
			 					<td><%=StringUtil.convertNull(sealProduct.getProductName()) %></td>
			 					<td><%=StringUtil.convertNull(sealProduct.getProductOriname()) %></td>
			 					<td><%=StringUtil.convertNull(sealProduct.getImei()) %></td>
			 					<td><%=StringUtil.convertNull(sealProduct.getAfterSaleOrderCode()) %></td>
			 					<td><%=StringUtil.convertNull(sealProduct.getAfterSaleOrderStatusName()) %></td>
			 					<td><%=StringUtil.convertNull(sealProduct.getAfterSaleDetectProductCode()) %></td>
			 					<td><%=StringUtil.convertNull(sealProduct.getAfterSaleOrderDetectProductStatusName())%></td>
			 				</tr>
			 		<%		}
			 			}
			 		 %>
			 		<tr>
						<td colspan="8" align="right">
			 				封箱人签字：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			 				日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			 			</td>
		 			</tr>
			 	</table>
		 </div>
	<%					
				}
			}
		}
	}
  %>
</body>
</html>
<script>
	function initPrintSealInventory(){
		var LODOP;
		cssStyle = "<style>table{font-size:13px;border-width: 1px;border-color: #666666;border-collapse: collapse;} table th{border-width: 1px;border-style: solid;} table td{border-width: 1px;border-style: solid;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		var index = <%=count%>;
		if(index>0){
			if(LODOP.PRINT_INIT("")){
				LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
				<%
					for(int k=0;k<count;k++){
				%>
					LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","18.2cm","12.8cm",cssStyle+document.getElementById("sealInventoryDiv"+<%=k%>).innerHTML);
					index--;
					if(index>=1){LODOP.NEWPAGE()}
				<%
					}
				%>
					//LODOP.SET_PRINTER_INDEX(-1);//设置成默认打印机
					//LODOP.PREVIEWB();//打印预览
					LODOP.PRINTB();
					window.close();
			}else{
				alert("打印控件初始化失败，请重新刷新后再试。");
			}
		}else{
			alert("封箱单里没有封箱状态的产品,封箱单已废弃!");
			window.close();
		}
	}

	window.onload = initPrintSealInventory;
</script>