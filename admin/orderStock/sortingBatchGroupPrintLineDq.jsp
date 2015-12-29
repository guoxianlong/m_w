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
	//int printer = Integer.parseInt(request.getAttribute("huizongPrinter")+"");//购物清单改版后大Q打印方式不变，与汇总单用同一个打印机
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
			int bianjie  = 0;//用来标识是第几个DIV
			Map stockMap = new HashMap();//存储出库单号
			Map orderMap = new HashMap();//存储订单号
		    int orderNum = 0;//标记是第几个订单
			Iterator orderListIter= orderList.listIterator();
			while (orderListIter.hasNext()) {//循环订单
				voOrder order = (voOrder) orderListIter.next();
				boolean fr=false;//是否特殊外链订单
				if(order.getFlat() == 10||order.getFr()==neFr||order.getFr()==nmtFr||order.getFr()==jhFr||order.getFr()==hzFr){
					fr=true;
				}
				int MAXLINE=10;//每页最大行数
				int  i = 0 ; //第几个SKU
				int index = 0;//本页字符的行数
				int totalCount = 0;//本单商品数
				int totalSum = 0;//商品总数
				int pages =0;//订单分页时的当前页码
				float totalPrice = 0;//商品总金额（不包括负数的商品,例如代金卷）
				int charNumPerLineX=13,extendNumA=5,extendNumB=5;
				if(!order.isTaobaoOrder()){
					charNumPerLineX=8;
					extendNumA=10;
					extendNumB=13;
				}
				List productList = (List) productMap.get(order.getOrderStock().getId()+"");
				List productList1 = (List) productMap1.get(order.getOrderStock().getId()+"");
				productList.addAll(productList1);
				String parentId="";
				if (productList != null) {
					orderNum++;
					//前页清单产品未打印完整
					voProduct oldOrderProduct = null;
					int colFlag=0;
					Iterator iter= productList.listIterator();
					while (iter.hasNext()) {//循环订单中的产品
						voProduct orderProduct = (voProduct) iter.next();
						totalCount += orderProduct.getBuyCount();
						if(orderProduct.getPrice()>0){
							if(orderProduct.getIsPackage()==0){
								totalPrice += orderProduct.getBuyCount()* orderProduct.getPrice();
							}else{
								if(!parentId.equals(orderProduct.getParentId())){
									totalPrice += orderProduct.getParentCount()* orderProduct.getPrice();
								}
							}
						}
					
						if (index % MAXLINE == 0 || index>MAXLINE) {//每个发货清单的开头
							totalCount = 0;
							if(index>0){
								pages++;
							}
							index =0;
							parentId=orderProduct.getParentId();
						%>
							<div id="tableDiv<%=bianjie%>">
								<table table-layout="fixed" cellpadding="0" cellspacing="0" rules="rows" width="700" height="425" border="1" style="border: 1px solid; border-collapse: collapse; font-size: 12px; ">
									<tr height="60px" bordercolor="#00000">
										<td colspan="9">
											<table width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
												<tr>
													<td rowspan="3" style="width: 180px; height: 90px; text-align: left; vertical-align: middle;">
														<font style="font-size: 12px;"></font>
													</td>
													<td colspan="2" style="width: 270px; height: 30px; text-align: center; vertical-align: top;">
														<font style="font-size: 28px;"><strong>购&nbsp;物&nbsp;清&nbsp;单&nbsp;</strong></font>
														<br>
													    <font style="font-size: 24px;"></font>
													</td>
													<td rowspan="3" valign="bottom" style="width: 180px; height: 90px; text-align: right; vertical-align: middle;">
														<br><br><br><br>
														<div align="center"><font style="font-size: 30px;"><b><%=order.getGroupCode()%></b></font></div>
														<span style="font-size:16px;font-weight:bold;"><%=pages>0?"续":"&nbsp;" %></span>
													</td>
												</tr>
												<tr>
												    <td style="width: 135px; height: 30px; text-align: center; vertical-align: top;">
												    	<div align="left"><font size='1'>订单号：<%=order.getCode()%></font><br></div>
												    	<div align="left"><font size='1'>订单时间：<%=order.getCreateDatetime().toString().substring(0, 16)%></font><br></div>
												    	<div align="left"><font size='1'>快递公司：<%=order.getDeliverName()%></font></div>
												    </td>
												    <td style="width: 135px; height: 30px; text-align: center; vertical-align: top;">
												      	<div align="left"><font size='1'>客户姓名：<%if(order.getName()!=null){%><%=StringUtil.getString(order.getName(), 8)%><%}else{ %>&nbsp;<%} %></font></div>
												      	<div align="left"><font size='1'>序号：<%=order.getBatchNum()%>-<%=order.getSerialNumber() %></font></div>
												      	<div align="left"><font size='1'>付款方式：<%=voOrder.buyModeMap.get(order.getBuyMode()+"")%></font></div>
												    </td>
										        </tr>
											</table>
										</td>
										<td width="17">
										<hr>
										</td>
									</tr>
									<tr bordercolor="#000000">
									<%if(!order.isTaobaoOrder()){ %>
											<td width="30" height="20"><div align="center" style="white-space:nowrap;">序号</div></td>
											<td width="115" height="20"><div align="center" style="white-space:nowrap;">货位编号</div></td>
											<td width="120" height="20"><div align="center" style="white-space:nowrap;">套装</div></td>
											<td width="85" height="20"><div align="center" style="white-space:nowrap;">套装价(数量)</div></td>
											<td width="60" height="20"><div align="center" style="white-space:nowrap;">商品编号</div></td>
											<td width="120" height="20"><div align="left" style="white-space:nowrap;">商品名称</div></td>
											<td width="68" height="20"><div align="center" style="white-space:nowrap;">单价(元)</div></td>
											<td width="30" height="20"><div align="center" style="white-space:nowrap;">数量</div></td>
											<td width="68" height="20"><div align="center" style="white-space:nowrap;">金额(元)</div></td>
									<%}else{%>
											<td width="40" height="20"><div align="center" style="white-space:nowrap;">序号</div></td>
											<td width="130" height="20"><div align="center" style="white-space:nowrap;">货位编号</div></td>
											<td width="170" height="20"><div align="center" style="white-space:nowrap;">套装</div></td>
											<td width="100" height="20"><div align="center" style="white-space:nowrap;">商品编号</div></td>
											<td width="170" height="20"><div align="left" style="white-space:nowrap;">商品名称</div></td>
											<td width="50" height="20"><div align="center" style="white-space:nowrap;">数量</div></td>
									<%} %>
									</tr>
									<%if(oldOrderProduct!=null){ 
										if(colFlag==2){
									%>
								<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX||
										oldOrderProduct.getName().toString().length()>charNumPerLineX) {
											index++;
										int colspanA=2,colspanB=2;
										if(!order.isTaobaoOrder()){
											colspanA=3;
											colspanB=4;
										}
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX,charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX,charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else if(oldOrderProduct.getName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA||
										oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB) {
											index++;
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*3+extendNumA*2){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA,charNumPerLineX*3+extendNumA*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(oldOrderProduct.getName().toString().length()>charNumPerLineX*3+extendNumB*2){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB,charNumPerLineX*3+extendNumB*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								  <%}%>	
								<%}%>											
									<%}else if(colFlag==3){%>
								<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA||
										oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB) {
											index++;
										int colspanA=2,colspanB=2;
										if(!order.isTaobaoOrder()){
											colspanA=3;
											colspanB=4;
										}
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*3+extendNumA*2){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA,charNumPerLineX*3+extendNumA*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(oldOrderProduct.getName().toString().length()>charNumPerLineX*3+extendNumB*2){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB,charNumPerLineX*3+extendNumB*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								  <%}%>									
									<%}
										oldOrderProduct=null;
										colFlag=0;		
									}%>
									<tr>
										<td height="20"><div align="center"><%=i + 1%></div></td>
										<td height="20"><div align="center" style="white-space:nowrap;"><%=orderProduct.getCargoPSList().get(0).toString()%></div></td>
										<td height="20">
										<% if(orderProduct.getIsPackage()==0){%>
											<div align="center">——</div>
										<% }else{%>
											<div align="left" style="white-space:nowrap;">
										<%
											if(orderProduct.getParentName().toString().length()>charNumPerLineX){
										%>
											<%=orderProduct.getParentName().substring(0, charNumPerLineX)%>
										<% }else{%>
											<%=orderProduct.getParentName()%>
										<% }%>
											</div>
										<% }%>
										</td>
										<%if(!order.isTaobaoOrder()){ %>
										<td height="20">
										<% if(orderProduct.getIsPackage()==0){%>
											<div align="center">——</div>
										<% }else{%>
											<div align="center">
											<%if(fr==false){%>
											<%=NumberUtil.priceOrderZero(orderProduct.getPrice())%>(<%=orderProduct.getParentCount()%>)
											<%}%>
											</div>
										<% }%>
										</td>
										<%} %>
										<td height="20"><div align="center" style="white-space:nowrap;"><%=orderProduct.getCode()%></div></td>
										<%if(orderProduct.getName().toString().length()>charNumPerLineX) {%>
											<td height="20"><div align="left" style="white-space:nowrap;">
											<%=orderProduct.getName().substring(0, charNumPerLineX)%>
											</div></td>
										<%}else{ %>
											<td height="20">
											<div align="left" style="white-space:nowrap;">
											<%=orderProduct.getName()%>
											</div></td>
										<%} %>
										<%if(!order.isTaobaoOrder()){ %>
											<td height="20"><div align="center">
											<%if(fr==false){%>
												<% if(orderProduct.getIsPackage()==0){%>
													<%=NumberUtil.priceOrderZero(orderProduct.getPrice())%>
												<%}else{%>
													0.00
												<%} %>
											<%} %></div></td>
										<%} %>
										<td height="20"><div align="center"><strong style="font-size: 13px;"><%=orderProduct.getBuyCount()%></strong></div></td>
										<%if(!order.isTaobaoOrder()){ %>
											<td height="20"><div align="center">
											<%if(fr==false){%>
												<% if(orderProduct.getIsPackage()==0){%>
													<%=NumberUtil.priceOrderZero(orderProduct.getBuyCount()*orderProduct.getPrice())%>
												<%}else{%>
													<%=NumberUtil.priceOrderZero(orderProduct.getParentCount()*orderProduct.getPrice())%>
												<%} %>
											<%} %></div></td>
										<%} %>
										<%totalSum += orderProduct.getBuyCount();%>
									</tr>
								<%if(orderProduct.getParentName().toString().length()>charNumPerLineX||
										orderProduct.getName().toString().length()>charNumPerLineX) {
											index++;
										int colspanA=2,colspanB=2;
										if(!order.isTaobaoOrder()){
											colspanA=3;
											colspanB=4;
										}
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(orderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX,charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else if(orderProduct.getParentName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(orderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX,charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else if(orderProduct.getName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
									<%if(orderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA||
										orderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB) {
											index++;
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(orderProduct.getParentName().toString().length()>charNumPerLineX*3+extendNumA*2){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX*2+extendNumA,charNumPerLineX*3+extendNumA*2)%>
											</div></td>
										<%}else if(orderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(orderProduct.getName().toString().length()>charNumPerLineX*3+extendNumB*2){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX*2+extendNumB,charNumPerLineX*3+extendNumB*2)%>
											</div></td>
										<%}else if(orderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								  <%}%>	
								<%}%>								
					<%
						}else{ //非一个发货单的开头
					%>
									<%if(index!=MAXLINE-1){ %>
										<tr>
									<%}else{ %>
										<tr bordercolor="#000000">
									<%} %>
										<td height="20"><div align="center"><%= i + 1%></div></td>
										<td height="20"><div align="center" style="white-space:nowrap;"><%=orderProduct.getCargoPSList().get(0).toString()%></div></td>
										<td height="20">
										<% if(orderProduct.getIsPackage()==0){%>
											<div align="center">——</div>
										<% }else{%>
											<div align="left" style="white-space:nowrap;">
										<%
											if(orderProduct.getParentName().toString().length()>charNumPerLineX){
										%>
											<%=orderProduct.getParentName().substring(0, charNumPerLineX)%>
										<% }else{%>
											<%=orderProduct.getParentName()%>
										<% }%>
											</div>
										<% }%>
										</td>
										<%if(!order.isTaobaoOrder()){ %>
										<td height="20">
										<% if(orderProduct.getIsPackage()==0){%>
											<div align="center">——</div>
										<% }else{%>
											<div align="center">
											<%if(fr==false){%>
												<%if(parentId.equals(orderProduct.getParentId())){ %>
													0.00
												<%}else{%>
													<%=NumberUtil.priceOrderZero(orderProduct.getPrice())%>(<%=orderProduct.getParentCount()%>)
												<%} %>
											<%}%>
											</div>
										<% }%>
										</td>
										<%} %>										
										<td height="20"><div align="center" style="white-space:nowrap;"><%=orderProduct.getCode()%></div></td>
										<%if(orderProduct.getName().toString().length()>charNumPerLineX) {%>
											<td height="20"><div align="left" style="white-space:nowrap;">
											<%=orderProduct.getName().substring(0, charNumPerLineX)%>
											</div></td>
										<%}else{ %>
											<td height="20">
											<div align="left" style="white-space:nowrap;">
											<%=orderProduct.getName()%>
											</div></td>
										<%} %>
										<%if(!order.isTaobaoOrder()){ %>					
											<td height="20"><div align="center">
											<%if(fr==false){%>
												<% if(orderProduct.getIsPackage()==0){%>
													<%=NumberUtil.priceOrderZero(orderProduct.getPrice())%>
												<%}else{%>
													0.00
												<%} %>
											<%} %></div></td>
										<%} %>
										<td height="20"><div align="center"><strong style="font-size: 13px;"><%=orderProduct.getBuyCount()%></strong></div></td>
										<%if(!order.isTaobaoOrder()){ %>
											<td height="20"><div align="center">
											<%if(fr==false){%>
												<% if(orderProduct.getIsPackage()==0){%>
													<%=NumberUtil.priceOrderZero(orderProduct.getBuyCount()*orderProduct.getPrice())%>
												<%}else{%>
													<%if(parentId.equals(orderProduct.getParentId())){ %>
														0.00
													<%}else{ parentId=orderProduct.getParentId(); %>
														<%=NumberUtil.priceOrderZero(orderProduct.getParentCount()*orderProduct.getPrice())%>
													<%} %>
												<%} %>
											<%} %></div></td>
										<%} %>
										<%totalSum += orderProduct.getBuyCount();%>
									</tr>
								<%if(orderProduct.getParentName().toString().length()>charNumPerLineX||
										orderProduct.getName().toString().length()>charNumPerLineX) {
									if(index<MAXLINE-1){
									index++;
									int colspanA=2,colspanB=2;
									if(!order.isTaobaoOrder()){
										colspanA=3;
										colspanB=4;
									}
									%>
									<%if(index!=MAXLINE-1){ %>
										<tr>
									<%}else{ %>
										<tr bordercolor="#000000">
									<%} %>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(orderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX,charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else if(orderProduct.getParentName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(orderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX,charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else if(orderProduct.getName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								  <%if(orderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA||
										orderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB) {
									if(index<MAXLINE-1){
									index++;
									%>
									<%if(index!=MAXLINE-1){ %>
										<tr>
									<%}else{ %>
										<tr bordercolor="#000000">
									<%} %>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(orderProduct.getParentName().toString().length()>charNumPerLineX*3+extendNumA*2){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX*2+extendNumA,charNumPerLineX*3+extendNumA*2)%>
											</div></td>
										<%}else if(orderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=orderProduct.getParentName().substring(charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(orderProduct.getName().toString().length()>charNumPerLineX*3+extendNumB*2){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX*2+extendNumB,charNumPerLineX*3+extendNumB*2)%>
											</div></td>
										<%}else if(orderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=orderProduct.getName().substring(charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
									<%}else{
										oldOrderProduct=orderProduct;
										colFlag=3;
									  }%>
								  <%}%>
								  <%}else{ 
									  oldOrderProduct=orderProduct;
									  colFlag=2;
								  }
								  %>
								<%}%>						
						  <%}
								index++;
								i++;
									if(index>=MAXLINE || (i==productList.size()&&oldOrderProduct==null)){
									   	stockMap.put(bianjie + "", order.getOrderStock().getCode());
									   	orderMap.put(bianjie + "", order.getCode()); 
									   	bianjie++;
									}
							%>
							<%		
									
									if(i==productList.size()&&oldOrderProduct==null){
										for (; index <MAXLINE; index++) {
											if(index!=MAXLINE-1){
							%>
												<tr>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<%if(!order.isTaobaoOrder()){ %>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
													<%} %>
												</tr>
										  <%}else{ %>
												<tr bordercolor="#000000">
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<%if(!order.isTaobaoOrder()){ %>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
													<%} %>
												</tr>
											   <%}
								    	}
									}
											
					 				if (i==productList.size()&&oldOrderProduct==null) { 
					 					int colspan=9;
					 					if(!order.isTaobaoOrder()){
					 						colspan=2;
					 					}
					 				%>
										<tr bordercolor="#000000">
											<td colspan="<%=colspan%>" height="20"><div align="center" style="white-space:nowrap;">商品总数</div></td>
											<%if(!order.isTaobaoOrder()){ %>	
											<td  height="20"><div align="center" style="white-space:nowrap;">商品总金额(元)</div></td>
											<td height="20"><div align="center" style="white-space:nowrap;">优惠(元)</div></td>
											<td height="20"><div align="center" style="white-space:nowrap;">运费（元）</div></td>
											<td colspan="2" height="20"><div align="center" style="white-space:nowrap;">已付（元）</div></td>
											<td colspan="2" height="20"><div align="center" style="white-space:nowrap;">应付（元）</div></td>
											<%} %>
										</tr>
										<tr bordercolor="#000000">
											<td colspan="<%=colspan%>" height="20"><div align="center"><%=totalSum%></div></td>
											<%if(!order.isTaobaoOrder()){ %>
											<td height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(totalPrice)%><%} %></div></td>
											<%if(totalPrice-order.getDprice()+order.getPostage()>0){ %>
												<td height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(totalPrice-order.getDprice()+order.getPostage())%><%} %></div></td>
											<%}else{ %>
												<td height="20"><div align="center">0.00</div></td>
											<%} %>
											<td height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(order.getPostage())%><%} %></div></td>
											<%if(order.getBuyMode()==0) {%>
												<td colspan="2" height="20"><div align="center">0.00</div></td>
												<td colspan="2" height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(order.getDprice())%><%} %></div></td>
											<%}else{ %>
												<td colspan="2" height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(order.getDprice())%><%} %></div></td>
												<td colspan="2" height="20"><div align="center">0.00</div></td>
											<%} %>
											<%} %>
										</tr>
									<%} %>
									<%if((i>=productList.size()&&oldOrderProduct==null) || index>=MAXLINE) { %>
										<tr>
											<td colspan=9>
												<table  width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
													<tr>
														<td width="139" rowspan="3">
															<%if(!order.isJdAdultOrder()){ %>
															<div align="center">
																<img src="<%=request.getContextPath()%>/image/dQ.jpg" width="218px" height="61px" align="middle" />
															</div>
															<%} %>
														</td>
														<td width="222" rowspan="4" align="center" valign="baseline">
															<p align="left">&nbsp;</p>
														</td>
														<%if(!order.isTaobaoOrder()&&!order.isJdAdultOrder()){ %>
														<td colspan="4">
															<div align="left">客服热线:4008864966</div>
														</td>
														<%} %>
					                				</tr>
					                				<%if(!order.isTaobaoOrder()&&!order.isJdAdultOrder()){ %>
									          		<tr bordercolor="#000000">
									          			<td colspan="4">
									          				<div align="left">售后邮箱：dqsh@ebinf.com</div>
									          			</td>
													</tr>
									          		<tr bordercolor="#000000">
									          			<td height="20" colspan="4">
									          				<div align="left">退换货事宜：www.daq.cn</div>
									          			</td>
													</tr>
									          		<tr bordercolor="#000000">
									          			<td height="20" colspan="2"><div  class="divcss5" align="left">温馨提醒：退换货时请将此购物清单随商品一起寄回</div></td>
									          			<td width="351" colspan="4"><div align="left">手机访问：www.daq.cn</div></td>
													</tr>
													<%}else{ %>
													<tr bordercolor="#000000">
									          			<td colspan="4">
									          			</td>
													</tr>
													<tr bordercolor="#000000">
									          			<td colspan="4">
									          			</td>
													</tr>
													<tr bordercolor="#000000">
									          			<td height="20" colspan="2"><div  class="divcss5" align="left">温馨提醒：退换货时请将此购物清单随商品一起寄回</div></td>
									          			<td width="203" colspan="4"><div align="left"></div></td>
													</tr>
													<%} %>
												</table>
											</td>
										</tr>
									</table>
	</div>
							<br />
						<% } 
					 }
			        %>
			        <%
			        	if(oldOrderProduct!=null){
			        		index =0;
			        %>
							<div id="tableDiv<%=bianjie%>">
								<table table-layout="fixed" cellpadding="0" cellspacing="0" rules="rows" width="700" height="425" border="1" style="border: 1px solid; border-collapse: collapse; font-size: 12px; ">
									<tr height="60px" bordercolor="#00000">
										<td colspan="9">
											<table width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
												<tr>
													<td rowspan="3" style="width: 180px; height: 90px; text-align: left; vertical-align: middle;">
														<font style="font-size: 12px;"></font>
													</td>
													<td colspan="2" style="width: 270px; height: 30px; text-align: center; vertical-align: top;">
														<font style="font-size: 28px;"><strong>购&nbsp;物&nbsp;清&nbsp;单&nbsp;<%= order.isAmazonOrder() ? "(亚马逊)" : "" %></strong></font>
														<br>
													    <font style="font-size: 24px;"></font>
													</td>
													<td rowspan="3" valign="bottom" style="width: 180px; height: 90px; text-align: right; vertical-align: middle;">
														<br><br><br><br>
														<div align="center"><font style="font-size: 30px;"><b><%=order.getGroupCode()%></b></font></div>
														<span style="font-size:16px;font-weight:bold;">续</span>
													</td>
												</tr>
												<tr>
												    <td style="width: 135px; height: 30px; text-align: center; vertical-align: top;">
												    	<div align="left"><font size='1'><%= order.isAmazonOrder() ? "亚马逊" : ""%>订单号：<%= order.isAmazonOrder() ? order.getAmazonCode() : order.getCode()%></font><br></div>
												    	<div align="left"><font size='1'>订单时间：<%=order.getCreateDatetime().toString().substring(0, 16)%></font><br></div>
												    	<div align="left"><font size='1'>快递公司：<%=order.getDeliverName()%></font></div>
												    </td>
												    <td style="width: 135px; height: 30px; text-align: center; vertical-align: top;">
												      	<div align="left"><font size='1'>客户姓名：<%if(order.getName()!=null){%><%=StringUtil.getString(order.getName(), 8)%><%}else{ %>&nbsp;<%} %></font></div>
												      	<div align="left"><font size='1'>序号：<%=order.getBatchNum()%>-<%=order.getSerialNumber() %></font></div>
												      	<div align="left"><font size='1'>付款方式：<%=voOrder.buyModeMap.get(order.getBuyMode()+"")%></font></div>
												    </td>
										        </tr>
											</table>
										</td>
										<td width="17">
										<hr>
										</td>
									</tr>
									<tr bordercolor="#000000">
									<%if(!order.isTaobaoOrder()){ %>
											<td width="30" height="20"><div align="center" style="white-space:nowrap;">序号</div></td>
											<td width="115" height="20"><div align="center" style="white-space:nowrap;">货位编号</div></td>
											<td width="120" height="20"><div align="center" style="white-space:nowrap;">套装</div></td>
											<td width="85" height="20"><div align="center" style="white-space:nowrap;">套装价(数量)</div></td>
											<td width="60" height="20"><div align="center" style="white-space:nowrap;">商品编号</div></td>
											<td width="120" height="20"><div align="left" style="white-space:nowrap;">商品名称</div></td>
											<td width="68" height="20"><div align="center" style="white-space:nowrap;">单价(元)</div></td>
											<td width="30" height="20"><div align="center" style="white-space:nowrap;">数量</div></td>
											<td width="68" height="20"><div align="center" style="white-space:nowrap;">金额(元)</div></td>
									<%}else{%>
											<td width="40" height="20"><div align="center" style="white-space:nowrap;">序号</div></td>
											<td width="130" height="20"><div align="center" style="white-space:nowrap;">货位编号</div></td>
											<td width="170" height="20"><div align="center" style="white-space:nowrap;">套装</div></td>
											<td width="100" height="20"><div align="center" style="white-space:nowrap;">商品编号</div></td>
											<td width="170" height="20"><div align="left" style="white-space:nowrap;">商品名称</div></td>
											<td width="50" height="20"><div align="center" style="white-space:nowrap;">数量</div></td>
									<%} %>
									</tr>
									<%if(oldOrderProduct!=null){ 
										if(colFlag==2){
									%>
								<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX||
										oldOrderProduct.getName().toString().length()>charNumPerLineX) {
											index++;
										int colspanA=2,colspanB=2;
										if(!order.isTaobaoOrder()){
											colspanA=3;
											colspanB=4;
										}
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX,charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX,charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else if(oldOrderProduct.getName().toString().length()>charNumPerLineX){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA||
										oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB) {
											index++;
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*3+extendNumA*2){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA,charNumPerLineX*3+extendNumA*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(oldOrderProduct.getName().toString().length()>charNumPerLineX*3+extendNumB*2){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB,charNumPerLineX*3+extendNumB*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								  <%}%>	
								<%}%>											
									<%}else if(colFlag==3){%>
								<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA||
										oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB) {
											index++;
										int colspanA=2,colspanB=2;
										if(!order.isTaobaoOrder()){
											colspanA=3;
											colspanB=4;
										}
										%>
									<tr>
										<td height="20"><div align="center">&nbsp;</div></td>
										<td height="20"><div align="center">&nbsp;</div></td>
										<%if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*3+extendNumA*2){%>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA,charNumPerLineX*3+extendNumA*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getParentName().toString().length()>charNumPerLineX*2+extendNumA){ %>
											<td height="20" colspan="<%=colspanA%>">
											<div align="left">
											<%=oldOrderProduct.getParentName().substring(charNumPerLineX*2+extendNumA)%>
											</div></td>
										<%}else{%>
											<td height="20" colspan="<%=colspanA%>"><div align="center">&nbsp;</div></td>
										<%} %>
										<%if(oldOrderProduct.getName().toString().length()>charNumPerLineX*3+extendNumB*2){%>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB,charNumPerLineX*3+extendNumB*2)%>
											</div></td>
										<%}else if(oldOrderProduct.getName().toString().length()>charNumPerLineX*2+extendNumB){ %>
											<td height="20" colspan="<%=colspanB%>">
											<div align="left">
											<%=oldOrderProduct.getName().substring(charNumPerLineX*2+extendNumB)%>
											</div></td>
										<%}else{ %>
											<td height="20" colspan="<%=colspanB%>"><div align="center">&nbsp;</div></td>
										<%} %>	
									</tr>
								  <%}%>									
									<%}}
									for (; index <MAXLINE; index++) {
										if(index!=MAXLINE-1){
									%>									
										<tr>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<%if(!order.isTaobaoOrder()){ %>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
													<%} %>
												</tr>
										  <%}else{ %>
												<tr bordercolor="#000000">
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<td height="20">&nbsp;</td>
													<%if(!order.isTaobaoOrder()){ %>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
														<td height="20">&nbsp;</td>
													<%} %>
												</tr>
									<%}} 
				 					int colspan=9;
				 					if(!order.isTaobaoOrder()){
				 						colspan=2;
				 					}
									%>
										<tr bordercolor="#000000">
											<td colspan="<%=colspan%>" height="20"><div align="center" style="white-space:nowrap;">商品总数</div></td>
											<%if(!order.isTaobaoOrder()){ %>	
											<td  height="20"><div align="center" style="white-space:nowrap;">商品总金额(元)</div></td>
											<td height="20"><div align="center" style="white-space:nowrap;">优惠(元)</div></td>
											<td height="20"><div align="center" style="white-space:nowrap;">运费（元）</div></td>
											<td colspan="2" height="20"><div align="center" style="white-space:nowrap;">已付（元）</div></td>
											<td colspan="2" height="20"><div align="center" style="white-space:nowrap;">应付（元）</div></td>
											<%} %>
										</tr>
										<tr bordercolor="#000000">
											<td colspan="<%=colspan%>" height="20"><div align="center"><%=totalSum%></div></td>
											<%if(!order.isTaobaoOrder()){ %>
											<td height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(totalPrice)%><%} %></div></td>
											<%if(totalPrice-order.getDprice()+order.getPostage()>0){ %>
												<td height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(totalPrice-order.getDprice()+order.getPostage())%><%} %></div></td>
											<%}else{ %>
												<td height="20"><div align="center">0.00</div></td>
											<%} %>
											<td height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(order.getPostage())%><%} %></div></td>
											<%if(order.getBuyMode()==0) {%>
												<td colspan="2" height="20"><div align="center">0.00</div></td>
												<td colspan="2" height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(order.getDprice())%><%} %></div></td>
											<%}else{ %>
												<td colspan="2" height="20"><div align="center"><%if(fr==false){%><%=NumberUtil.priceOrderZero(order.getDprice())%><%} %></div></td>
												<td colspan="2" height="20"><div align="center">0.00</div></td>
											<%} %>
											<%} %>
										</tr>
										<tr>
											<td colspan=9>
												<table  width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
													<tr>
														<td width="139" rowspan="3">
															<%if(!order.isJdAdultOrder()){ %>
															<div align="center">
																<img src="<%=request.getContextPath()%>/image/dQ.jpg" width="218px" height="61px" align="middle" />
															</div>
															<%} %>
														</td>
														<td width="222" rowspan="4" align="center" valign="baseline">
															<p align="left">&nbsp;</p>
														</td>
														<%if(!order.isTaobaoOrder()&&!order.isJdAdultOrder()){ %>
														<td colspan="4">
															<div align="left">客服热线:4008864966</div>
														</td>
														<%} %>
					                				</tr>
					                				<%if(!order.isTaobaoOrder()&&!order.isJdAdultOrder()){ %>
									          		<tr bordercolor="#000000">
									          			<td colspan="4">
									          				<div align="left">售后邮箱：dqsh@ebinf.com</div>
									          			</td>
													</tr>
									          		<tr bordercolor="#000000">
									          			<td height="20" colspan="4">
									          				<div align="left">退换货事宜：www.daq.cn</div>
									          			</td>
													</tr>
									          		<tr bordercolor="#000000">
									          			<td height="20" colspan="2"><div  class="divcss5" align="left">温馨提醒：退换货时请将此购物清单随商品一起寄回</div></td>
									          			<td width="351" colspan="4"><div align="left">手机访问：www.daq.cn</div></td>
													</tr>
													<%}else{ %>
													<tr bordercolor="#000000">
									          			<td colspan="4">
									          			</td>
													</tr>
													<tr bordercolor="#000000">
									          			<td colspan="4">
									          			</td>
													</tr>
													<tr bordercolor="#000000">
									          			<td height="20" colspan="2"><div  class="divcss5" align="left">温馨提醒：退换货时请将此购物清单随商品一起寄回</div></td>
									          			<td width="203" colspan="4"><div align="left"></div></td>
													</tr>
													<%} %>
												</table>
											</td>
										</tr>																		
									</table>
								</div>
							<br />					        
			        <%		
			       		oldOrderProduct=null;
						colFlag=0;	
				   		stockMap.put(bianjie + "", order.getOrderStock().getCode());
				   		orderMap.put(bianjie + "", order.getCode()); 
				   		bianjie++;
			        	}
			        %>
			        <%
					
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
			    分拣波次汇总单(大Q)</SPAN></td>
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
		

		LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
		 <%
		for (int j = bianjie-1; j >-1; j--) {
			String tmpcode = stockMap.get(j + "").toString();
			String tmpOrdercode = orderMap.get(j + "").toString();
			%>
			
			var barcodeWidth=15;
			var barcodeLeft=146;
			var barcodeLeft1=6;
			<%if (tmpcode.length() == 12) {%>
				barcodeWidth+=2.2;barcodeLeft-=2.2;
			<%} else if (tmpcode.length() == 13) {%>
				barcodeWidth+=2.2;barcodeLeft-=2.2;
			<%} else if (tmpcode.length() == 14) {%>
				barcodeWidth+=4.5;barcodeLeft-=4.5;
			<%} else if (tmpcode.length() == 15) {%>
				barcodeWidth+=9;barcodeLeft-=9;
			<%}%>
			
			//alert("j="+i);
			
			//alert("indexT="+indexT);
			//barImage= document.getElementById("barcodeImage"+indexT);
			//barcodeTD= document.getElementById("barcodeID"+indexT);
			//barcodeTD.removeChild(barImage);
			<%if((bianjie-j)%2==1){%>
				LODOP.ADD_PRINT_TABLE("0.1cm","0.2cm","18.2cm","13.8cm",cssStyle+document.getElementById("tableDiv<%=j%>").innerHTML);
				LODOP.ADD_PRINT_BARCODE("0.5cm",barcodeLeft+"mm",barcodeWidth+"mm","13mm","128A","<%=tmpcode%>");
				LODOP.ADD_PRINT_BARCODE("0.5cm",barcodeLeft1+"mm",barcodeWidth+"mm","13mm","128A","<%=tmpOrdercode%>");
				//LODOP.ADD_PRINT_RECT("11.8cm", "0.45cm", "7.6cm", "0.7cm","2", "0");
				//LODOP.ADD_PRINT_LINE("10cm", "0.5cm","10cm","19.9cm","0", "1");
				//LODOP.ADD_PRINT_BARCODE("10.1cm", "10cm", "2.7cm",  "2.7cm","QRCode", "http://weixin.qq.com/r/WnUtNV7ElgPprRpW9yBI")
				//LODOP.SET_PRINT_STYLEA(0,"QRCodeVersion",5);
				//barcodeTD.app endChild(barImage);
			<%}else{%>
				LODOP.ADD_PRINT_TABLE("14.9cm","0.2cm","18.2cm","13.8cm",cssStyle+document.getElementById("tableDiv<%=j%>").innerHTML);
				LODOP.ADD_PRINT_BARCODE("15.3cm",barcodeLeft+"mm",barcodeWidth+"mm","13mm","128A","<%=tmpcode%>");
				LODOP.ADD_PRINT_BARCODE("15.3cm",barcodeLeft1+"mm",barcodeWidth+"mm","13mm","128A","<%=tmpOrdercode%>");
				//LODOP.ADD_PRINT_RECT("26.6cm", "0.45cm", "7.6cm", "0.7cm","2", "0");
				//LODOP.ADD_PRINT_LINE("24.8cm", "0.5cm","24.8cm","19.9cm","0", "1");
				//LODOP.ADD_PRINT_BARCODE("26.2cm", "10cm", "2.7cm", "2.7cm", "QRCode", "http://weixin.qq.com/r/WnUtNV7ElgPprRpW9yBI")
				//LODOP.SET_PRINT_STYLEA(0,"QRCodeVersion",5);
				//barcodeTD.appendChild(barImage);
				<%if(j >0 ){%>
					LODOP.NEWPAGE();
				<%}%>
				
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
	if(LODOP.PRINT_INIT("")){ //mmb汇总单
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
</html>