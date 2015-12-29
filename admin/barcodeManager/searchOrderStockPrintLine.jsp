<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*,adultadmin.util.StringUtil"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="java.util.*"%>
<%
	String tip = (String) request.getAttribute("tip");
	Map stockMap = new HashMap();
	int count = 0;
	if (((List) request.getAttribute("orderList")) != null) {
		count = ((List) request.getAttribute("orderList")).size();//发货清单数
	}
	//Map indexMap = (Map) request.getAttribute("indexMap");//发货单序号map，在页面中用不到
	if (null != tip) {
	%>
		<script>alert("<%=tip%>");window.close();</script>
	<%
		return;
	}
	voOrder vo = null;
	int bianjie = 0;
	String areano = StringUtil.convertNull(request.getParameter("areano"));
	String buymode = StringUtil.convertNull(request.getParameter("buymode"));
	String stockState = StringUtil.convertNull(request.getParameter("stockState"));
	String action = StringUtil.convertNull(request.getParameter("action"));
	int printType = StringUtil.StringToId(request.getParameter("printType"));
	int flag = -1;
	if (request.getParameter("flag") != null) {
		flag = StringUtil.StringToId(request.getParameter("flag"));
	}

	List orderList = (List) request.getAttribute("orderList");
	String ids = "";
	if (orderList != null) {
		for (int i = 0; i < orderList.size(); i++) {
			voOrder tempOrder = (voOrder) orderList.get(i);
			ids += "&id=";
			ids += tempOrder.getId();
		}
	}
	String errors = StringUtil.convertNull((String)request.getAttribute("errors"));
	if(orderList.size() == 0){
		errors = errors + "<br/>没有符合条件的订单";
	}
%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>打印销售出货单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<style type="text/css">
body {
	font-size: 12px;
}
</style>
</head>
<body>
<div align="center">
<p><font color="red"><%=errors %></font></p>
<%
	if ((areano.equals("0") || areano.equals("1") || areano.equals("2") || areano.equals("3") || areano.equals("-1"))&& printType == 1) {
		Map productMap = (Map) request.getAttribute("productMap");
		int orderIndex = count;
		//int pageIndex = 0;
		String date = DateUtil.getNow().substring(0, 10);
		int totalSum = 0;
		float totalPrice = 0;
%> <logic:present name="orderList" scope="request">
	<logic:iterate name="orderList" id="item">
		<%
		vo = (voOrder) item;
		List productList = (List) productMap.get(Integer.valueOf(vo.getId()));
		int index = 0;
		int totalCount = 0;
		int totalCargo=0;//产品货位总数
		if (productList != null) {
			totalPrice = 0;
			totalSum = 0;
			//重新整理productList 2011-08-11
			HashMap checkMap = new HashMap();
			for (int n = 0; n < productList.size(); n++) {
				voOrderProduct op = (voOrderProduct) productList.get(n);
				if (checkMap.get(String.valueOf(op.getProductId())) != null) {
					productList.remove(n);
					n--;
				} else {
					checkMap.put(String.valueOf(op.getProductId()), op);
				}
			}
			Iterator iter = productList.listIterator();
			while (iter.hasNext()) {//各种总和
				voOrderProduct vop = (voOrderProduct) iter.next();
				totalSum += vop.getCount();
				totalPrice = totalPrice + vop.getCount()* vop.getPrice();
				List ospcList = vop.getOrderStockProduct().getOspcList();
				totalCargo+=ospcList.size();
			}
			Iterator iter2 = productList.listIterator();
			checkMap = new HashMap();
			while (iter2.hasNext()) {
				voOrderProduct op = (voOrderProduct) iter2.next();
				if (checkMap.get(String.valueOf(op.getProductId())) != null) {
					continue;
				} else {
					checkMap.put(String.valueOf(op.getProductId()), op);
				}
				List ospcList = op.getOrderStockProduct().getOspcList();
				if (ospcList == null) {//无货位信息，该部分无修改
					if (index % 5 == 0) {
						//pageIndex++;
		%>
		<div id="tableDiv<%=bianjie%>">
		<table cellpadding="0" cellspacing="0" width="570" height="400"
			border="1" style="border: 1px solid; border-collapse: collapse;">
			<tr>
				<td colspan="4"
					style="border: none; height: 60px; text-align: center; vertical-align: middle;">
				<h2>买卖宝（mmb）发货清单</h2>
				</td>
				<td id="barcodeID<%=bianjie%>" colspan="3"
					style="height: 60px; border: none; text-align: center;"><img
					id="barcodeImage<%=bianjie%>"
					src="<%=request
																.getContextPath()%>/barcodeServlet?msg=<bean:write name="item" property="code" />&fmt=jpg&type=code39"
					width="150" height="55" border="0" /></td>
			</tr>
			<tr>
				<td align='left' style="font-size: 14px;">序</td>
				<td align="left" colspan="3">订单时间：<%=vo.getCreateDatetime()
														.toString().substring(
																0, 16)%></td>
				<td align='left'>客户姓名：<strong style="font-size: 13px;"><%=StringUtil.getString(vo
														.getName(), 8)%></strong></td>
				<td align='left' colspan="2">快递公司：<%=vo.getDeliverName()%></td>
			</tr>
			<tr>
				<td align='center' valign="top" rowspan="12">序号：<strong><%=flag != 2? orderIndex: (vo.getSerialNumber() == 0? "&nbsp;": vo.getSerialNumber()+ "")%></strong></td>
				<td align='center'>商品序号</td>
				<td align='center'>&nbsp;&nbsp;</td>
				<td align='center'>货号</td>
				<td align='center'>数量</td>
				<td align='center'>单价</td>
				<td align='center'>金额</td>
			</tr>
			<tr>
				<td><%=index + 1%></td>
				<td>-</td>
				<td><%=op.getCode()%></td>
				<td align="center"><strong style="font-size: 13px;"><%=op.getCount()%></strong></td>
				<td style="font-size: 10px; text-align: right;"><%=NumberUtil.price(op
														.getPrice())%></td>
				<td style="font-size: 10px; text-align: right;"><%=NumberUtil.price(op
														.getCount()
														* op.getPrice())%></td>
				<%
					totalCount = totalCount + op.getCount();
				%>
			</tr>
			<tr>
				<td></td>
				<td align="left" colspan="5"><%=op.getOriname()%></td>
			</tr>
			<%
				} else {
			%>

			<tr>
				<td><%=index + 1%></td>
				<td><strong>-</strong></td>
				<td><%=op.getCode()%></td>
				<td align="center"><strong style="font-size: 13px;"><%=op.getCount()%></strong></td>
				<td style="font-size: 10px; text-align: right;"><%=NumberUtil.price(op
														.getPrice())%></td>
				<td style="font-size: 10px; text-align: right;"><%=NumberUtil.price(op
														.getCount()
														* op.getPrice())%></td>
				<%
					totalCount = totalCount + op.getCount();
				%>
			</tr>
			<tr>
				<td></td>
				<td align="left" colspan="5"><%=op.getOriname()%></td>
			</tr>
			<%
				}
										index++;
			%>
			<%
				if (index % 5 == 0) {
											//pageIndex--;
											if (index != productList.size()) {
												bianjie++;
												stockMap.put(bianjie + "", vo
														.getCode());
			%>
			<tr>
				<td colspan="6" align="left">小计：<%=totalCount%>&nbsp;&nbsp;总数量：
				<%=totalSum%>&nbsp;商品总金额：<%=NumberUtil
															.price(totalPrice)%>&nbsp;&nbsp;运费：<%=(int) vo.getPostage()%>元</td>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="7" align="center">7*24小时客服电话（免长话费）：400-886-9499&nbsp;买卖宝站点
				mmb.cn</td>
			</tr>
		</table>
		</div>
		<br />
		<%
			totalCount = 0;
										}
									}
		%>
		<%
			} else {
				for (int i = 0; i < ospcList.size(); i++) {
					OrderStockProductCargoBean ospc = (OrderStockProductCargoBean) ospcList.get(i);
					if (index % 6 == 0) {//每个发货清单的开头
						totalCount = 0;
		%>
		<div id="tableDiv<%=bianjie%>">
		<table cellpadding="0" cellspacing="0" width="700" height="450"border="1" style="border: 1px solid; border-collapse: collapse; font-size: 12px;">
			<tr height="60px">
				<td colspan="6">
					<table width="670" cellpadding="1" style="border: none; border-collapse: collapse;">
						<tr>
							<td rowspan="2" style="width: 180px; height: 90px; text-align: center; vertical-align: middle;">
								<img src="<%=request.getContextPath()%>/image/logo_fhd.bmp" width="150px" height="63px"/>
							</td>
							<td style="width: 40%; height: 30px; text-align: center; vertical-align: middle;">
								<font style="font-size: 28px;"><strong>发&nbsp;货&nbsp;清&nbsp;单&nbsp;</strong></font>
							</td>
							<td id="barcodeID<%=bianjie%>" rowspan="2" align="right" style="height: 60px; text-align: center;">
								<div id="barcodeImage<%=bianjie%>"><bean:write name="item" property="code" /></div>
							</td>
						</tr>
						<tr>
							<td style="width: 40%; height: 30px; text-align: center; vertical-align: middle; font-size: 15px;">
								<i>欢迎使用<strong>买卖宝</strong>&nbsp;&nbsp;手机购物<strong>我最好</strong></i>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align='left' style="font-size: 13px;">
					序号：<strong><%=flag != 2? vo.getBatchNum()+"-"+orderIndex: (vo.getSerialNumber() == 0? "&nbsp;": vo.getBatchNum()+"-"+vo.getSerialNumber())%></strong>
				</td>
				<td align="left" colspan="2" width="300px">订单时间：<%=vo.getCreateDatetime().toString().substring(0, 16)%></td>
				<td align='left' width="15%">姓名：<strong
					style="font-size: 13px;"><%=StringUtil.getString(vo.getName(), 8)%></strong></td>
				<td align='left' colspan="2">快递公司：<%=vo.getDeliverName()%></td>
			</tr>
			<tr>
				<td align='left'>商品序号</td>
				<td align='center'>&nbsp;&nbsp;</td>
				<td align='left'>买卖宝编号</td>
				<td align='center'>数量</td>
				<td align='center'>单价</td>
				<td align='center'>金额</td>
			</tr>
			<tr>
				<td align="left"><%=index + 1%></td>
				<td align="left"><strong style="font-size: 13px;"><%=ospc.getCargoWholeCode()%></strong></td>
				<td align="left"><%=op.getCode()%></td>
				<td align="center"><strong style="font-size: 13px;"><%=ospc.getCount()%></strong></td>
				<td style="text-align: center;"><%=NumberUtil.price(op.getPrice())%></td>
				<td style="text-align: center;"><%=NumberUtil.price(ospc.getCount()* op.getPrice())%></td>
				<%totalCount = totalCount+ ospc.getCount();%>
			</tr>
			<tr>
				<td></td>
				<td align="left" style="font-size: 13px;" colspan="5"><%=op.getOriname()%></td>
			</tr>
			<%
					} else {//非一个发货单的开头
			%>
			<tr>
				<td align='left'><%=index + 1%></td>
				<td align="left"><strong style="font-size: 13px;"><%=ospc.getCargoWholeCode()%></strong></td>
				<td align="left"><%=op.getCode()%></td>
				<td align="center"><strong style="font-size: 13px;"><%=ospc.getCount()%></strong></td>
				<td style="text-align: center;"><%=NumberUtil.price(op.getPrice())%></td>
				<td style="text-align: center;"><%=NumberUtil.price(ospc.getCount()* op.getPrice())%></td>
				<%totalCount = totalCount+ ospc.getCount();%>
			</tr>
			<tr>
				<td></td>
				<td align="left" style="font-size: 13px;" colspan="5"><%=op.getOriname()%></td>
			</tr>
			<%	}
				index++;
				//index%6==0表示：该货位的该产品列出后该页已显示6个产品，需要添加发货清单结尾
				if(index%6==0&&ospcList.size()!=0){%>
				<%bianjie++; %>
				<%stockMap.put(bianjie + "", vo.getCode()); %>
				<tr>
					<td colspan="2" align="left"><%if(totalCargo>6){ %>小计：<%=totalCount%>&nbsp;<%} %>商品总数：<%=totalSum%></td>
					<td align='left'>运费：<%=(int) vo.getPostage()%>元</td>
					<td colspan="2" align='left'>付款方式：<%switch (vo.getBuyMode()) {case 0 :%>货到付款<%break;case 1 :%>邮购<%break;case 2 :%>上门自取<%break;}%></td>
					<td align='left'>总金额：<%=NumberUtil.price(totalPrice)%>元</td>
				</tr>
				<tr height="25px">
					<td colspan="6" align="center" style="border: none;"><strong
							style="font-size: 13px;">全场保真！全国范围货到付款！30天包退换！无风险网购第一站：买卖宝</strong>
					</td>
				</tr>
				<tr height="25px">
					<td colspan="6" align="center" style="border: none;">
						<strong style="font-size: 13px;">全国售后服务专线（免长话费）：40088-43211&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手机访问&nbsp;mmb.cn</strong>
					</td>
				</tr>
			</table>
			</div>
		<br />
				<%} 
				
			}
			
			if (index % 6 != 0&&index==totalCargo) {//产品列表结束但未到一页结尾
				for (; index % 6 != 0; index++) {%>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td colspan="5">&nbsp;</td>
			</tr>
			<%	}//此时index%6应为0，需要添加发货清单结尾
				if(index%6==0&&ospcList.size()!=0){%>
				<%bianjie++; %>
				<%stockMap.put(bianjie + "", vo.getCode()); %>
				<tr>
					<td colspan="2" align="left">商品总数：<%=totalSum%></td>
					<td align='left'>运费：<%=(int) vo.getPostage()%>元</td>
					<td colspan="2" align='left'>付款方式：<%switch (vo.getBuyMode()) {case 0 :%>货到付款<%break;case 1 :%>邮购<%break;case 2 :%>上门自取<%break;}%></td>
					<td align='left'>总金额：<%=NumberUtil.price(totalPrice)%>元</td>
				</tr>
				<tr height="25px">
					<td colspan="6" align="center" style="border: none;"><strong
							style="font-size: 13px;">全场保真！全国范围货到付款！30天包退换！无风险网购第一站：买卖宝</strong>
					</td>
				</tr>
				<tr height="25px">
					<td colspan="6" align="center" style="border: none;">
						<strong style="font-size: 13px;">全国售后服务专线（免长话费）：40088-43211&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手机访问&nbsp;mmb.cn</strong>
					</td>
				</tr>
			</table>
			</div>
		<br />
				<%} 
			}
			
		}
	}
}
orderIndex--;
%>
</logic:iterate>
</logic:present>
<%} %>
<script type="text/javascript">
//CheckLodop();
function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		var barImage = null;
		var barcodeTD = null;
		var barcodeHId = null;
		var top=90;
		var i=0;
		var indexT=<%=bianjie%>;
		 <%
		 //List tmpOrderList = (List) request.getAttribute("orderList");
		for (int j = bianjie; j > 0; j -= 1) {
			String tmpcode = stockMap.get(j + "").toString();%>
			LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
			var barcodeWidth=42;
			var barcodeLeft=135;
			<%if (tmpcode.length() == 12) {%>
				barcodeWidth+=2.2;barcodeLeft-=2.2;
			<%} else if (tmpcode.length() == 13) {%>
				barcodeWidth+=2.2;barcodeLeft-=2.2;
			<%} else if (tmpcode.length() == 14) {%>
				barcodeWidth+=4.5;barcodeLeft-=4.5;
			<%} else if (tmpcode.length() == 15) {%>
				barcodeWidth+=9;barcodeLeft-=9;
			<%}%>
			i=<%=j%>;
			indexT--;
			barImage= document.getElementById("barcodeImage"+indexT);
			barcodeTD= document.getElementById("barcodeID"+indexT);
			barcodeTD.removeChild(barImage);
			<%if((bianjie-j)%2==0){%>
				LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","18.2cm","12.8cm",cssStyle+document.getElementById("tableDiv"+indexT).innerHTML);
				LODOP.ADD_PRINT_BARCODE("12mm",barcodeLeft+"mm",barcodeWidth+"mm","17mm","128A","<%=tmpcode%>");
				barcodeTD.appendChild(barImage);
			<%}else{%>
				LODOP.ADD_PRINT_TABLE("15.63cm","0.6cm","18.2cm","12.8cm",cssStyle+document.getElementById("tableDiv"+indexT).innerHTML);
				LODOP.ADD_PRINT_BARCODE("160mm",barcodeLeft+"mm",barcodeWidth+"mm","17mm","128A","<%=tmpcode%>");
				barcodeTD.appendChild(barImage);
				if(indexT>=1)LODOP.NEWPAGE();
			<%}%>
			//*****
			if(indexT%100 == 0){
//				LODOP.SET_PREVIEW_WINDOW(1,1,0,0,00,"打印发货清单.打印");	
				//LODOP.PREVIEWB();
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.PRINTB();
				//	alert(LODOP.SET_PRINT_MODE("PRINT_START_PAGE",2));
				//	alert(LODOP.SET_PRINT_MODE("PRINT_END_PAGE",1));
					//LODOP.PRINT_DESIGN();
			}
		<%}%>
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
}

function initPrint2(){
	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		var barImage = null;
		var barcodeTD = null;
		var barcodeHId = null;
		var top=90;
		var i=0;
		var indexT=<%=bianjie%>;
		 <%
		 //List tmpOrderList = (List) request.getAttribute("orderList");
		for (int j = bianjie; j > 0; j -= 1) {
			String tmpcode = stockMap.get(j + "").toString();%>
			LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
			var barcodeWidth=42;
			var barcodeLeft=135;
			<%if (tmpcode.length() == 12) {%>
				barcodeWidth+=2.2;barcodeLeft-=2.2;
			<%} else if (tmpcode.length() == 13) {%>
				barcodeWidth+=2.2;barcodeLeft-=2.2;
			<%} else if (tmpcode.length() == 14) {%>
				barcodeWidth+=4.5;barcodeLeft-=4.5;
			<%} else if (tmpcode.length() == 15) {%>
				barcodeWidth+=9;barcodeLeft-=9;
			<%}%>
			i=<%=j%>;
			indexT--;
			barImage= document.getElementById("barcodeImage"+indexT);
			barcodeTD= document.getElementById("barcodeID"+indexT);
			barcodeTD.removeChild(barImage);
			LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","18.2cm","12.8cm",cssStyle+document.getElementById("tableDiv"+indexT).innerHTML);
			LODOP.ADD_PRINT_BARCODE("12mm",barcodeLeft+"mm",barcodeWidth+"mm","17mm","128A","<%=tmpcode%>");
			barcodeTD.appendChild(barImage);
			if(indexT>=1)LODOP.NEWPAGE();
			//*****
			if(indexT%100 == 0){
//				LODOP.SET_PREVIEW_WINDOW(1,1,0,0,00,"打印发货清单.打印");	
				//LODOP.PREVIEWB();
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.PRINTB();
				//	alert(LODOP.SET_PRINT_MODE("PRINT_START_PAGE",2));
				//	alert(LODOP.SET_PRINT_MODE("PRINT_END_PAGE",1));
					//LODOP.PRINT_DESIGN();
			}
		<%}%>
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
}
</script>
<form action="searchOrderStock.do" name="exportForm" method="post">
<%
	if (orderList != null) {
		for (int i = 0; i < orderList.size(); i++) {
			voOrder tempOrder = (voOrder) orderList.get(i);
%> 
	<input type="hidden" name="id" value="<%=tempOrder.getId()%>" /> 
<%		}
	}%> 
	<input type="hidden" name="fromPage" value="1" /> 
	<input type="hidden" name="buymode" value="<%=buymode%>" /> 
	<input type="hidden" name="areano" value='<%=areano.equals("")?"3":areano %>' /> 
	<input type="hidden" name="stockState" value="<%=stockState%>" /> 
	<input type="hidden" name="printAction"value="print" /> 
	<input type="hidden" name="flag" value="<%=flag%>" />
	<input type="hidden" name="printType" value="0" />
</form>
<%if(orderList.size() > 0){ %>
	<input type="button" value="A5打印" onclick="initPrint2();" />
	<input type="button" value="按地址导出excel文件" onclick="document.exportForm.submit();" />
	<input type="button" value="A4打印" onclick="initPrint();" />
<%} %>
<br />

</div>
</body>
</html>