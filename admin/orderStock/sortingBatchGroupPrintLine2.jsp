<%@page import="com.mmb.framework.support.SpringHandler"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager" %>
<%@page isELIgnored="false" %>
<%@page isELIgnored="false" %>
<%@ page import="java.util.*"%>
<%@ page import="cn.mmb.config.ConfigInfo"%>
<%@ page import="com.mmb.framework.support.SpringHandler"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*,adultadmin.util.StringUtil"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.SortingBatchBean"%>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<!-- 说明：此页面与sortingBatchGroupPrintLine.jsp是同一个功能，由于购物清单打印布局调整大，重新建了此页面 -->
<%
	//int qingdanPrinter = Integer.parseInt(request.getAttribute("qingdanPrinter")+"");
	//int huizongPrinter = Integer.parseInt(request.getAttribute("huizongPrinter")+"");
	int line =0;//当前的字符串行数
	int SKUIndex=0;//SKU循环到的行号
	int SKUIndex1 = 0;//印刷品SKU的行号
	int pageNum=0;//当前页数
	int MAXLINE1=37;//每页最大37行字符串
	int charNumPerLine=20;//每行最多35个字符串，超出会换行
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
	String sortingType = (String)request.getAttribute("type");
	String staffCode = (String)request.getAttribute("staffCode");
	int neFr=67509;//19e订单友链ID
	int nmtFr=135011;//糯米团订单友链ID
	int jhFr=136101;//财付通聚惠的友链ID
	int hzFr=136102;//杭州电视台电视购物友链ID
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
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<title>打印销售出货单</title>
<script language="javascript" src="${path}/admin/barcodeManager/LodopFuncs.js"></script>
<script type="text/javascript" src="${path}/easyui/jquery-easyui-1.3.4/jquery.min.js" charset="utf-8"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed> </object>
</head>
<body style="background-color: #FFFFFF;">
	<div id="orderList" style="width: 250px;margin: 0 auto;">
		<div style="background-color: #FFFFFF;">
		<c:forEach items="${orderList}" var="order" varStatus="index">
			<div style="margin-top: 30px;margin-bottom: 30px;">
				<table style="width: 250px;font-family:SimSong;">
					<tr style="height: 28px;"><td style="text-align: center;">
						<font style="font-size: 25px;">
						<strong>购&nbsp;物&nbsp;清&nbsp;单&nbsp;<c:if test="${order.flat == 3}">(亚马逊)</c:if></strong>
						</font>
					</td></tr>
					<tr style="height: 30px;"><td style="text-align: center;">
						<img alt="" src="${path}/SortingController/generateBarCode.mmx?code=${order.code}">
					</td></tr>
					<tr style="height: 30px;"><td style="text-align: center;">
						<img alt="" src="${path}/SortingController/generateBarCode.mmx?code=${order.orderStock.code}">
					</td></tr>
					<tr><td style="text-align: center;">
						<font style="font-size: 30px;">
						<strong>${order.groupCode}</strong>
						</font>
					</td></tr>
				</table>
				<table style="font-size: 11px;font-family:SimSong;">
					<tr>
						<td>
							<c:if test="${order.flat == 3}">亚马逊</c:if>订单号：
							<c:choose>
								<c:when test="${order.flat == 3}">
									${order.amazonCode}
								</c:when>
								<c:otherwise>
									${order.code}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							客户姓名：${order.name}
						</td>
					</tr>
					<tr>
						<td style="width: 150px;">
							订单时间：${fn:substring(order.createDatetime,0,16)}
						</td>
						<td>
							序号：${order.batchNum}-${order.serialNumber}
						</td>
					</tr>
					<tr>
						<td>
							快递公司：${order.deliverName}
						</td>
						<td>
							付款方式：${order.buyModeName}
						</td>
					</tr>
				</table>
				<div style="border-top: 1px solid; margin-top: 5px; margin-bottom: 5px;"></div>
				<c:forEach items="${order.productList}" var="product" varStatus="index">
					<table style="margin-top: 3px;font-size: 11px;font-family:SimSong;">
						<tr>
							<td>货位编号：</td>
							<td>${product.cargoPSList[0]}</td>
						</tr>
						<tr>
							<td>商品编号：</td>
							<td>${product.code}</td>
						</tr>
						<tr>
							<td style="width: 62px;vertical-align: top;">商品名称：</td>
							<td>${product.name}</td>
						</tr>
					</table>
					<table style="margin-bottom: 3px;font-size: 11px;font-family:SimSong;">
						<tr>
							<td style="width: 100px;">套装：
								<c:choose>
									<c:when test="${product.isPackage == 0}">——</c:when>
									<c:otherwise>
										${product.ParentName}
									</c:otherwise>
								</c:choose>
							</td>
							<td colspan="2">套装价(数量)：
								<c:choose>
									<c:when test="${product.isPackage == 0}">——</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${product.price}" type='currency' pattern='#,##0.00'/>(${product.parentCount})
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td colspan="3">单价(元)：
								<c:choose>
									<c:when test="${product.isPackage == 0}">
										<fmt:formatNumber value="${product.price}" type='currency' pattern='#,##0.00'/>
									</c:when>
									<c:otherwise>
										0.00
									</c:otherwise>
								</c:choose>
							&nbsp;
							数量：${product.buyCount}&nbsp;
							金额(元)：
								<c:choose>
									<c:when test="${product.isPackage == 0}">
										<fmt:formatNumber value="${product.price*product.buyCount}" type='currency' pattern='#,##0.00'/>
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${product.price*product.parentCount}" type='currency' pattern='#,##0.00'/>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</table>
				</c:forEach>
				<div style="border-top: 1px solid; margin-top: 5px; margin-bottom: 5px;"></div>
				<table style="font-size: 11px;font-family:SimSong;">
					<tr>
						<td>商品总数：${order.totalProductCount}</td>
						<c:choose>
							<c:when test="${order.flat != 5 and order.flat != 10}">
								<td style="padding-left: 15px;">商品总金额：<fmt:formatNumber value="${order.totalPrice}" type='currency' pattern='#,##0.00'/></td>
							</c:when>
							<c:otherwise>
								<td></td>
							</c:otherwise>
						</c:choose>
					</tr>
					<c:if test="${order.flat != 5 and order.flat != 10}">
						<tr>
							<td>优惠：
								<c:choose>
									<c:when test="${(order.totalPrice - order.dprice + order.postage) > 0}">
										<fmt:formatNumber value="${order.totalPrice - order.dprice + order.postage}" type='currency' pattern='#,##0.00'/>
									</c:when>
									<c:otherwise>
										0.00
									</c:otherwise>
								</c:choose>
							</td>
							<td style="padding-left: 15px;">运费：<fmt:formatNumber value="${order.postage}" type='currency' pattern='#,##0.00'/></td>
						</tr>
						<tr>
							<td>已付：
								<c:choose>
									<c:when test="${order.buyMode == 0}">
										0.00
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${order.dprice}" type='currency' pattern='#,##0.00'/>
									</c:otherwise>
								</c:choose>
							</td>
							<td></td>
						</tr>
						<tr>
							<td></td>
							<td style="padding-left: 15px;font-size: 13px;font-family:SimHei;"><font style="font-family:SimHei;">应付(元)：</font>
								<c:choose>
									<c:when test="${order.buyMode == 0}">
										<fmt:formatNumber value="${order.dprice}" type='currency' pattern='#,##0.00'/>
									</c:when>
									<c:otherwise>
										0.00
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
				</table>
				<div style="border-top: 1px solid; margin-top: 5px; margin-bottom: 5px;"></div>
				<table style="font-size: 11px;font-family:SimSong;">
					<c:if test="${order.flat != 5 and order.flat != 10 and order.flat != 12}">
						<tr>
							<td style="width: 90px;height: 90px;">
								<img src="${path}/image/maimaibaomall.png" width="88px" height="88px" align="middle" />
							</td>
							<td>
								语音购物好管家&nbsp;&nbsp;让网购不再孤单<br>
								全网底价&nbsp;&nbsp;100%正品<br>
								货到付款&nbsp;&nbsp;无理由退换<br>
								<br>
								客服热线：400-886-9499<br>
								退换货事宜：详见买卖宝网页
							</td>
						</tr>
						<tr>
							<td style="height: 60px;text-align: center;" colspan="2">
								<img src="${path}/image/logo_fhd2.png" height="50px" align="middle" />
							</td>
						</tr>
					</c:if>
					<tr>
						<td style="text-align: center;" colspan="2">
							温馨提醒：退换货时请将此购物清单随商品一起寄回
						</td>
					</tr>
				</table>
			</div>
		</c:forEach>
		</div>
	</div>
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
		
		<%for(int i=0;i<MAXLINE1;){//每页最多打印MAXLINE行的字符串
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
								<td style='background-color:<%=i%2==0?"#FFFFFF;" :"#EBF2FA;" %>;font-size:20px; font-weight:bold ;text-align:center;'><%=huizongNum1.get(huizong1.getKey())%></td>
								<td><%if(productNameMap1.get(huizong1.getKey()).toString().length()>11){%><%=productNameMap1.get(huizong1.getKey()).toString().substring(0,11)%><%}else{%><%=productNameMap1.get(huizong1.getKey())%><%} %></td>
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
function initPrintOrder(){
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印

		var qingdanPrinter = -1;
		var printerName = null;
		<%ConfigInfo conf = SpringHandler.getBean("configInfo");%>
		printerName = '<%=conf.getMmbQingdanPrinterName()%>';
		var printCount = LODOP.GET_PRINTER_COUNT();
		for (var i = 0;i<printCount;i++) {
			if (LODOP.GET_PRINTER_NAME(i) == printerName) {
				qingdanPrinter = i;
				break;
			}
		}
		LODOP.SET_PRINT_PAGESIZE(3,"79mm","0","");
		LODOP.ADD_PRINT_HTML("0cm","0cm","7.5cm","100%",$("#orderList").html())
		LODOP.SET_PRINTER_INDEX(qingdanPrinter);
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
	if(LODOP.PRINT_INIT("")){ //mmb汇总单
		var printCount = LODOP.GET_PRINTER_COUNT();
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
	<%if("sortingBatchOrderReceive".equals(pageFrom)){ %>//跳转到领单页面
		window.location.href="sortingAction.do?method=sortingBatchOrderReceiveInfo&success=1&staffCode=<%=staffCode%>&type=<%=sortingType%>&sortingBatchGroupId=<%=sortingBatchGroupInfo.getId()%>";
	<%}else if("sortingBatchGroupQueryList".equals(pageFrom)){%>//跳转到波次查询页面
		window.close();
	<%}else{%>//跳转到波次明细页面
		window.location.href="sortingAction.do?method=sortingBatchGroupDetail&batchId=<%=sortingBatchGroupInfo.getSortingBatchId()%>&pageIndex=<%=StringUtil.StringToId(""+request.getAttribute("pageIndex"))%>";
	<%}%>
</script>
</body>