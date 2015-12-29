<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*,adultadmin.util.StringUtil"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.SortingBatchBean"%>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>


<%
int line =0;//当前的字符串行数
int SKUIndex=0;//SKU循环到的行号
int SKUIndex1 = 0;//印刷品SKU的行号
int pageNum=0;//当前页数
int MAXLINE=37;//每页最大37行字符串
int charNumPerLine = 32;//每行最多35个字符串，超出会换行
	String tip = (String) request.getAttribute("tip");
	
	SortingBatchGroupBean sortingBatchGroupInfo = (SortingBatchGroupBean)request.getAttribute("sortingBatchGroupInfo"); //分拣波次,里面放了员工信息
	String printType = (String)request.getAttribute("printType"); //打印类型：printType（value--all/huizong/fahuoqingdan/selected）
	List huizongList1=new ArrayList();
	if(request.getAttribute("huizongList1")!=null){
		huizongList1 = (List)request.getAttribute("huizongList1");
	}
	List orderList = (List)request.getAttribute("orderList");//订单列表
	Map productMap = (HashMap)request.getAttribute("productMap");//productMap.put(order.getOrderStock().getId()+"", orderProductList);//以申请出库的订单ID为key把该订单里的产品放到Map中
	List huizongList = (List)request.getAttribute("huizongList");
	Map productMap1 = (HashMap)request.getAttribute("productMap1");
	Map productNameMap = (HashMap)request.getAttribute("productNameMap");
	Map productNameMap1 = (HashMap)request.getAttribute("productNameMap1");
	String pageFrom = (String)request.getAttribute("pageFrom");//页面来源
	String staffCode = (String)request.getAttribute("staffCode");
	if (null != tip) {
	%>
		<script>
		alert("<%=tip%>");
		window.close();
		</script>
	<%
		return;
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
	int bianjie  = 0;
	Map stockMap = new HashMap();
	int  orderNum = 0;//标记是第几个订单
	Iterator orderListIter= orderList.listIterator();
	while (orderListIter.hasNext()) {//循环订单
		voOrder order = (voOrder) orderListIter.next();
		
		int index = 0;
		int totalCount = 0;//本单商品数
		int totalSum = 0;//商品总数
		float totalPrice = 0;//总金额
		
		List productList = (List) productMap.get(order.getOrderStock().getId()+"");
		List productList1 = (List) productMap1.get(order.getOrderStock().getId()+"");
		productList.addAll(productList1);
		if (productList != null) {
			orderNum++;
			Iterator iter= productList.listIterator();
			while (iter.hasNext()) {//循环订单中的产品
				voProduct orderProduct = (voProduct) iter.next();
				
				totalCount += orderProduct.getBuyCount();
				totalPrice += orderProduct.getBuyCount()* orderProduct.getPrice();
				
				if (index % 6 == 0) {//每个发货清单的开头
					totalCount = 0;
				%>
		<div id="tableDiv<%=bianjie%>">
			<table cellpadding="0" cellspacing="0" width="700" height="450" border="1" style="border: 1px solid; border-collapse: collapse; font-size: 12px;">
				<tr height="60px">
						<td colspan="6">
							<table width="730" cellpadding="1" style="border: none; border-collapse: collapse;">
								<tr>
									<td rowspan="2" style="width: 180px; height: 90px; text-align: center; vertical-align: middle;">
										<img src="<%=request.getContextPath()%>/image/logo_fhd.bmp" width="150px" height="63px"/>
									</td>
									<td style="width: 27%; height: 30px; text-align: center; vertical-align: middle;">
										<font style="font-size: 28px;"><strong>发&nbsp;货&nbsp;清&nbsp;单&nbsp;</strong></font>
										<br><font style="font-size: 24px;"><%=order.getCode()%></font>
									</td>
									<td id="barcodeID<%=bianjie%>" width=170px rowspan="2" align="right" style="height: 60px; text-align: center;">
										<div id="barcodeImage<%=bianjie%>"><%=order.getOrderStock().getCode()%></div>
									</td>
									<td> 
										<font style="font-size: 30px;"><b><%=order.getGroupCode()%></b></font>
										<span style="font-size:15px;font-weight:bold;"><%=index/6>0?"续":"" %></span>
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
							序号：<strong><%=order.getBatchNum()%>-<%=order.getSerialNumber() %></strong>
						</td>
						<td align="left" colspan="2" width="300px">订单时间：<%=order.getCreateDatetime().toString().substring(0, 16)%></td>
						<td align='left' width="15%">姓名：<strong
							style="font-size: 13px;"><%if(order.getName()!=null){%><%=StringUtil.getString(order.getName(), 8)%><%}else{ %>&nbsp;<%} %></strong></td>
						<td align='left' colspan="2">快递公司：<%=order.getDeliverName()%></td>
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
					<td align="left"><strong style="font-size: 13px;"><%=orderProduct.getCargoPSList().get(0).toString()%></strong></td>
					<td align="left"><%=orderProduct.getCode()%></td>
					<td align="center"><strong style="font-size: 13px;"><%=orderProduct.getBuyCount()%></strong></td>
					<td style="text-align: center;"><%=NumberUtil.price(orderProduct.getPrice())%></td>
					<td style="text-align: center;"><%=NumberUtil.price(orderProduct.getBuyCount()*orderProduct.getPrice())%></td>
					
					<%totalSum += orderProduct.getBuyCount();%>
				</tr>
				<tr>
					<td></td>
					<td align="left" style="font-size: 13px;" colspan="5"><%=orderProduct.getName()%></td>
				</tr>
				<%
				}else{ //非一个发货单的开头
				%>
				<tr>
					<td align="left"><%=index + 1%></td>
					<td align="left"><strong style="font-size: 13px;"><%=orderProduct.getCargoPSList().get(0).toString()%></strong></td>
					<td align="left"><%=orderProduct.getCode()%></td>
					<td align="center"><strong style="font-size: 13px;"><%=orderProduct.getBuyCount()%></strong></td>
					<td style="text-align: center;"><%=NumberUtil.price(orderProduct.getPrice())%></td>
					<td style="text-align: center;"><%=NumberUtil.price(orderProduct.getBuyCount()*orderProduct.getPrice())%></td>
					
					<%totalSum += orderProduct.getBuyCount();%>
				</tr>
				<tr>
					<td></td>
					<td align="left" style="font-size: 13px;" colspan="5"><%=orderProduct.getName()%></td>
				</tr>
				<%}index++;
					if(index%6==0){ 
					bianjie++; 
				    stockMap.put(bianjie + "", order.getOrderStock().getCode()); %>
				<tr>
					<td colspan="2" align="left"><%if(productList.size()>6){ %>小计：<%=totalCount%>&nbsp;<%} %>商品总数：<%=totalSum%></td>
					<td align='left'>运费：<%=(int) order.getPostage()%>元</td>
					<td colspan="2" align='left'>付款方式：<%switch (order.getBuyMode()) {case 0 :%>货到付款<%break;case 1 :%>邮购<%break;case 2 :%>上门自取<%break;}%></td>
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
				<%
					}
					if (index % 6 != 0&&index==productList.size()) {//产品列表结束但未到一页结尾
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
						if(index%6==0&&productList.size()!=0){ %>
						<%bianjie++; %>
						<%stockMap.put(bianjie + "", order.getOrderStock().getCode()); %>
						<tr>
							<td colspan="2" align="left">商品总数：<%=totalSum%></td>
							<td align='left'>运费：<%=(int) order.getPostage()%>元</td>
							<td colspan="2" align='left'>付款方式：<%switch (order.getBuyMode()) {case 0 :%>货到付款<%break;case 1 :%>邮购<%break;case 2 :%>上门自取<%break;}%></td>
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
						<% } 
					}
			}
		}
	}%>
	



 <%
	Map huizongNum = (HashMap)request.getAttribute("huizongNum");
	Map productCodeMap = (HashMap)request.getAttribute("productCodeMap");
	Map huizongNum1 = (HashMap)request.getAttribute("huizongNum1");
	Map productCodeMap1 = (HashMap)request.getAttribute("productCodeMap1");
	StringBuffer sb = new StringBuffer();
	while(true){
	//输出头部+标题栏 if(line!=0)+续
	%>
	<div id="huizongdan_<%=pageNum%>" align="center" >
	 <table >
	   <tr>
	     <td>
			<table  width="740" >
				<tr>
					<td colspan="4" style="TEXT-ALIGN: center">
						<SPAN style="TEXT-ALIGN: center;FONT-STYLE: normal;  FONT-SIZE: 28px; FONT-WEIGHT: bold;">
						分拣波次汇总单</SPAN></td>
					<td id="batchGroupCodeTD_<%=pageNum%>" width="300" rowspan="3"><div id="batchGroupCodeImage_<%=pageNum%>" > <%=sortingBatchGroupInfo.getCode() %></div><%=line==0?"":"续" %></td>
				</tr>
				<tr>
					<td>作业仓:<%=sortingBatchGroupInfo.getStorageName()%></td>
					<td >订单数:<%=orderList.size()%></td>
					<td >时间:<%=sortingBatchGroupInfo.getCreateDatetime().toString().substring(0, 16)%></td>
				</tr>
				<tr>
					<td>分拣员姓名:<%if(sortingBatchGroupInfo.getCargoStaff()!=null&&sortingBatchGroupInfo.getCargoStaff().getName()!=null){%><%=sortingBatchGroupInfo.getCargoStaff().getName()%><%}else{ %>&nbsp;<%} %></td>
					<td colspan=3>员工号:<%if(sortingBatchGroupInfo.getCargoStaff()!=null &&sortingBatchGroupInfo.getCargoStaff().getCode()!=null){%><%=sortingBatchGroupInfo.getCargoStaff().getCode()%><%}else{ %>&nbsp;<%} %></td>
				</tr>
			</table>
		  </td>
		</tr>
		<tr>
		  <td>
			<table width="740" height="850"  cellpadding="0" cellspacing="0"  border="1" style="border: 1px ;  table-layout:fixed">
				<tr>
					<td style="font-size:12px;width:20px;">&nbsp;</td>
					<td style="font-size:12px;width:120px;text-align:center;">货位</td>
					<td style="font-size:12px;width:60px;text-align:center;">产品编号</td>
					<td style="font-size:12px;width:35px;text-align:center;">总数</td>
					<td style="font-size:12px;width:140px;text-align:center;">产品名称</td>
					<td style="font-size:12px;">&nbsp;</td>
				</tr>
		
		<%for(int i=0;i<MAXLINE;){//每页最多打印MAXLINE行的字符串
			if(SKUIndex<huizongList.size()){
				if(productCodeMap!=null && productCodeMap.size()!=0 && huizongNum!=null && huizongNum.size()!=0 && huizongList!=null && huizongList.size()!=0){
					Map.Entry huizong = (Map.Entry)huizongList.get(SKUIndex);
					//输出该SKU
					%>
					
					<tr>
						<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:20px;'><%=SKUIndex+1%></td>
						<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:20px; font-weight:bold ;text-align:center;' nowrap>
					<%
						String code = huizong.getKey().toString().split("-")[1];
						sb.delete(0,sb.length());
						sb.append(code.substring(0,3)).append("-").append(code.substring(3,5)).append("-").append(code.substring(5,6)).append("-").append(code.substring(6));
					%>
					<%=sb.toString().substring(0,11)%>
						</td>
						<td><%=productCodeMap.get(huizong.getKey()) %></td>
						<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:20px; font-weight:bold ;text-align:center;'><%=huizongNum.get(huizong.getKey())%></td>
						<td><%if(productNameMap.get(huizong.getKey()).toString().length()>11){%><%=productNameMap.get(huizong.getKey()).toString().substring(0,11)%><%}else{%><%=productNameMap.get(huizong.getKey())%><%} %></td>
						<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:18px; font-weight:bold ;word-wrap:break-word;'><%=huizong.getValue()%></td>
					 </tr>
					
					<%
					i+=huizong.getValue().toString().length()/charNumPerLine;
					if(huizong.getValue().toString().length()%charNumPerLine !=0 ){
						i++;
					}
					line+=i;
					SKUIndex++;
				}
			}else{	
					
					if(huizongList1.size() != 0 && SKUIndex1 < huizongList1.size()){
						//for(int j=0;j<huizongList1.size();j++){
						
							Map.Entry huizong1 = (Map.Entry)huizongList1.get(SKUIndex1);
							%>
							<tr>
							<%if(SKUIndex1==0) {%>
					           <td colspan="5" height="30" align="center"><font size='6'>印刷品</font></td>
					           <%} %>
					        </tr>
							<tr>
								<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:20px;'><%=SKUIndex+SKUIndex1+1%></td>
								<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:20px; font-weight:bold ;text-align:center;' nowrap>
							<%
								String code = huizong1.getKey().toString().split("-")[1];
								sb.delete(0,sb.length());
								sb.append(code.substring(0,3)).append("-").append(code.substring(3,5)).append("-").append(code.substring(5,6)).append("-").append(code.substring(6));
							%>
							<%=sb.toString().substring(0,11)%>
								</td>
								<td><%=productCodeMap1.get(huizong1.getKey()) %></td>
								<td><%if(productNameMap1.get(huizong1.getKey()).toString().length()>11){%><%=productNameMap1.get(huizong1.getKey()).toString().substring(0,11)%><%}else{%><%=productNameMap1.get(huizong1.getKey())%><%} %></td>
								<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:20px; font-weight:bold ;text-align:center;'><%=huizongNum1.get(huizong1.getKey())%></td>
								<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:18px; font-weight:bold ;word-wrap:break-word;'><%=huizong1.getValue()%></td>
							 </tr>
							
						<%
							i+=huizong1.getValue().toString().length()/charNumPerLine;
							if(huizong1.getValue().toString().length()%charNumPerLine !=0 ){
							i++;
						}
							line+=i;
							SKUIndex1++;
						
						//}
					}else{
					//输出空的tr td
				%>
				<tr>
					<td style='font-size:20px;'>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>			
				<%
					i++;
					line+=i;
				}
				
			}

			
		}
		pageNum++;
		//输出尾部
		%>
				</table>
			 </td>
		   </tr>
		 </table>
	 </div>
	<%
	if(SKUIndex>=huizongList.size() &&  SKUIndex1>=huizongList1.size()) break;}%>

<script type="text/javascript">
//CheckLodop();
function initPrintOrder(){
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
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
		 <%
		for (int j = bianjie; j > 0; j -= 1) {
			String tmpcode = stockMap.get(j + "").toString();
			%>
			
			var barcodeWidth=42;
			var barcodeLeft=128;
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
				if(indexT>=1){LODOP.NEWPAGE()}
			<%}%>
			
		<%} %>
		LODOP.SET_PRINTER_INDEX(-1);
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
	} else{
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
}


function initPrintHuizong(){
	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		<% for(int i=0;i<pageNum;i++){%>
			var barImage= document.getElementById("batchGroupCodeImage_<%=i%>");
			var barcodeTD= document.getElementById("batchGroupCodeTD_<%=i%>");
			barcodeTD.removeChild(barImage);
			//LODOP.SET_PRINT_PAGESIZE(0,"257mm","364mm","");
			
			LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
			LODOP.ADD_PRINT_TABLE("0.0cm","-0.2cm","21.0cm","29.2cm",cssStyle+document.getElementById("huizongdan_<%=i%>").innerHTML);
			LODOP.ADD_PRINT_BARCODE("3mm","132mm","52mm","20mm","128A","<%=sortingBatchGroupInfo.getCode()%>");
			barcodeTD.appendChild(barImage);
			<% if(pageNum>=1){%>
				LODOP.NEWPAGE();
			<%}
			} %>
		LODOP.SET_PRINTER_INDEX(-1);
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
		
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
}

<%if(orderList.size() > 0 && (( huizongList.size()>0&& huizongNum.size()>0) || (huizongList1.size()>0&& huizongNum1.size()>0))){ 
	if("all".equals(printType)){
		//打印汇总单、发货清单%>
		initPrintHuizong();
		initPrintOrder();
		
	<%}else if("huizong".equals(printType)){
		//只打印汇总单%>
		initPrintHuizong();
	<%}else if("buda".equals(printType)){
		//打印汇总单、发货清单%>
		initPrintHuizong();
		initPrintOrder();
	<%}else if ("fahuoqingdan".equals(printType)||"selected".equals(printType)){
		//只打印发货清单%>
		initPrintOrder();
		
	<%}else{
	    //弹出对话框提示打印类型参数错误%>
		alert("打印类型参数错误");
		
	<%}
	} else{ %>
	alert("SKU数为0，无需分拣！");
	<%}%>
	<%if("sortingBatchOrderReceive".equals(pageFrom)&&!"buda".equals(printType)){ %>//跳转到领单页面
		window.location.href="${pageContext.request.contextPath}/SortingController/sortingBatchOrderReceiveInfo.mmx?success=1&staffCode=<%=staffCode%>&sortingBatchGroupId=<%=sortingBatchGroupInfo.getId()%>";
	<%}else if("sortingBatchOrderReceive".equals(pageFrom)&&"buda".equals(printType)){%>
	window.location.href="${pageContext.request.contextPath}/SortingController/sortingBatchOrderReceiveInfo.mmx?sortingBatchGroupId=<%=sortingBatchGroupInfo.getId()%>";
	<%}
		else if("query".equals(pageFrom)){%>//跳转到波次查询页面
	window.location.href="${pageContext.request.contextPath}/admin/rec/oper/sorting/sortingBatchGroupQueryList.jsp";
	<%}else{%>//跳转到波次明细页面
	window.location.href="${pageContext.request.contextPath}/admin/rec/oper/sorting/sortingBatchGroupList.jsp?batchId=<%=sortingBatchGroupInfo.getSortingBatchId()%>&pageIndex=<%=StringUtil.StringToId(""+request.getAttribute("pageIndex"))%>";
	<%}%>
</script>


</body>
</html>