<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="cache.PrinterNameCache"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.bean.balance.MailingBalanceBean"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="cn.mmb.delivery.domain.model.vo.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
String orderCode=request.getAttribute("orderCode")==null?"":request.getAttribute("orderCode").toString();
voOrder order=(voOrder)request.getAttribute("order");
OrderCustomerBean ocBean=(OrderCustomerBean)request.getAttribute("ocBean");
MailingBalanceBean mbBean=(MailingBalanceBean)request.getAttribute("mbBean");
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
OrderStockBean osBean = (OrderStockBean)request.getAttribute("osBean");
String orderTypeName=request.getAttribute("orderTypeName")==null?"":request.getAttribute("orderTypeName").toString();
String addr = request.getAttribute("addr")==null?"":request.getAttribute("addr").toString();
List<DeliverRelationInfoBean> list =null;
String rqcode ="";
String districenter_code="";//(集包地编码)
String districenter_name="";//(集包地名称)
String bigpen_code="";//(大笔编码)
String position="";//(大笔)
String position_no="";//(格口号)
if(request.getAttribute("deliverRelationInfoList")!=null){
	list = (List)request.getAttribute("deliverRelationInfoList");
	for(DeliverRelationInfoBean deliverRelationInfoBean :list){
		int type=deliverRelationInfoBean.getType();
		if(type==1){
			districenter_code =deliverRelationInfoBean.getInfo();
		}
		if(type==2){
			districenter_name =deliverRelationInfoBean.getInfo();
		}
		if(type==3){
			bigpen_code =deliverRelationInfoBean.getInfo();
		}
		if(type==4){
			position =deliverRelationInfoBean.getInfo();
		}
		if(type==5){
			position_no =deliverRelationInfoBean.getInfo();
		}
	}
	rqcode = apBean.getPackageCode()+districenter_code;
}
String ydDqFlag=request.getAttribute("ydDqFlag")==null?"":request.getAttribute("ydDqFlag").toString();

%>
<html>
<head>
<title>韵达快递面单</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
<div id="szzjPackage">
<!-- Save for Web Slices (配送单--标注.jpg) -->
<table id="__01"  border="1" cellpadding="1" cellspacing="1" style="font-family: SimHei;">
    <tr>
      <td style="width: 20mm;height: 0mm;border-style: none;"></td>
      <td style="width: 30mm;height: 0mm;border-style: none;"></td>
      <td style="width: 20mm;height: 0mm;border-style: none;"></td>
      <td style="width: 40mm;height: 0mm;border-style: none;"></td>
      <td style="width: 3mm;height: 0mm;border-style: none;"></td>
    </tr>
    <tr>
      <td colspan="2" style="font-size:12px;border-style: none;">始发网点：<%=order.getDeliverName() %></td>
      <td colspan="2" style="font-size:12px;border-style: none;text-align:right;">订单号：<%=order.getCode() %></td>
      <td style="border-style: none;"></td>
    </tr>
    <tr>
      <td style="font-size:12px;border-style: none;"">寄件人：</td>
      <td colspan="3" style="font-size:12px;border-style: none;"">无锡买卖宝&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：40088-43211<%} %>&nbsp;包裹重量：<font style="font-weight: bold;"><%=apBean==null?"0.0":apBean.getWeight()/1000 %>kg</font></td>
      <td style="border-style: none;"></td>
    </tr>
    <tr>
      <td rowspan="4" style="font-size:20px; font-weight:bold;border-style: none;" >送达<br/>地址</td>
      <td style="font-size:12px;border-style: none;">收件人：</td>
      <td colspan="2" style="font-size:12px;border-style: none;font-weight:bold;"><%=ocBean.getName() %></td>
      <td style="border-style: none;"></td>
    </tr>
    <tr>
      <td style="font-size:12px;border-style: none;">收件人电话：</td>
      <td colspan="2" style="font-size:12px;border-style: none;font-weight:bold;"><%=order.getPhone() %></td>
      <td style="border-style: none;"></td>
    </tr>
    <tr>
      <td style="font-size:12px;border-style: none;height: 46px">收件人地址：</td>
      <td colspan="2" style="font-size:12px;border-style: none;font-weight:bold;text-align:left;height: 46px;width: 200px"><%=order.getAddress() %></td>
      <td style="border-style: none;"></td>
    </tr>
    <tr>
      <td colspan="5" style="border-style: none;">&nbsp;</td>
    </tr>
    <tr>
      <td style="font-size:14px;border-left-style: none;border-right-style: none;border-bottom-style: none; height: 35px">集包地：</td>
      <td colspan="2" style="font-size:12px;border-left-style: none;border-right-style: none;border-bottom-style: none;font-weight:bold;"><%=StringUtil.convertNull(districenter_name) %></td>
      <td align="right" style="border-left-style: none;border-right-style: none;border-bottom-style: none;"></td>
	  <td style="border-style:none;"></td>
    </tr>
    <tr>
      <td style=" text-align:center;border-style:none;"></td>
      <td colspan="2" style="font-size:30px; font-weight:bold;border-style:none;text-align:center;height:90px;" ><%=StringUtil.convertNull(position)%></td>
      <td style="font-size:30px; font-weight:bold;border-style:none;text-align:right;"><%=StringUtil.convertNull(bigpen_code)%><br><%=StringUtil.convertNull(position_no)%></td>
      <td style="border-style: none;"></td>
    </tr>
	<tr>
      <td colspan="3" style="height:80px; font-weight:bold; text-align:center">&nbsp;</td>
      <td style="border-left-style: none;font-size:30px;text-align:right">
      <%if("1".equals(ydDqFlag)){ %>
      <img src="<%=request.getContextPath()%>/image/dQ.jpg" width="110" height="40" />
      <%}else{ %>
       <img src="<%=request.getContextPath()%>/image/logo_cartonning.jpg" width="110" height="40" />
      <%} %>     
      <br><%=order.getDeliver()%></td>
	  <td style="border-style: none;"></td>    
    </tr>
	<tr>
	  <td colspan="3" style="font-size:14px;">收件人/代签人：</td>
	  <td colspan="2" style="font-size:12px;">签收时间:&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;月&nbsp;&nbsp;日</td>
    </tr>
	<tr>
	  <td colspan="3" style="text-align:center"></td>
	  <td colspan="2" style="text-align:center;height: 30px;border-top-style: none;"><img src="<%=request.getContextPath()%>/images/ydlog.jpg" width="110" height="23" /></td>
    </tr>
	<tr>
	  <td colspan="3" style="font-size:12px;border-top-style: none;">寄件人：买卖宝</td>
      <td style="font-size:12px;border-top-style: none;">服务描述：<%=order.getDealDetail() %></td>
      <td style="border-style: none;"></td>
    </tr>
	<tr>
	  <td colspan="3" style="font-size:12px;border-top-style: none;">寄件公司：无锡买卖宝</td>
      <td style="font-size:12px;border-top-style: none;">订单号：<%=order.getCode() %></td>
      <td style="border-style: none;"></td>
    </tr>
	<tr>
	  <td colspan="3" style="font-size:12px;border-top-style: none;">寄件人地址：买卖宝<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓</td>
      <td style="font-size:12px;border-top-style: none;">保险金额：</td>
      <td style="border-style: none;"></td>
    </tr>
	<tr>
	  <td colspan="3" style="border-top-style: none;">收件人:<%=ocBean.getName() %></td>
      <td style="font-size:12px;border-top-style: none;">保险费：</td>
      <td style="border-style: none;"></td>
    </tr>
	<tr>
	  <td colspan="3" style="font-size:12px;border-top-style: none;">收件人电话：<%=order.getPhone() %></td>
      <td style="font-size:12px;border-top-style: none;"><%if(order.getBuyMode()==0){%>到付金额：<font style="font-size: 14px;font-weight: bold;"><%=order.getDprice() %></font>元<%}else{%>已付款<%} %></td>
      <td style="border-style: none;"></td>
    </tr>
	<tr>
	  <td colspan="3" style="font-size:12px;border-top-style: none;">收件人地址：<%=order.getAddress() %></td>
      <td style="border-top-style: none;">包裹重量：<%=apBean==null?"0.0":apBean.getWeight()/1000 %>kg</td>
      <td style="border-style: none;"></td>
    </tr>
  </table>
<!-- End Save for Web Slices -->
</div>

<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:11px;font-family:Microsoft YaHei;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		LODOP.SET_PRINT_PAGESIZE(0,"105mm","150mm","");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.ADD_PRINT_TABLE("3mm","-2mm","105mm","150mm",cssStyle+document.getElementById("szzjPackage").innerHTML);
		LODOP.ADD_PRINT_BARCODE("41mm", "63mm","32mm", "8mm", "128A", "<%=districenter_code %>");
		LODOP.ADD_PRINT_BARCODE("75mm", "5mm","60mm", "15mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("103mm", "8mm","52mm", "7mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("50mm", "2mm","20mm", "20mm", "QRCode", "<%=rqcode%>");
 		//LODOP.PREVIEWB();
		LODOP.PRINTB();
		return true;
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
		return false;
	}
}
</script>

<script type="text/javascript">
var b=initPrint();
<%if(request.getAttribute("forward")!=null&&(request.getAttribute("forward").toString().equals("scanCheckOrderStock2")||request.getAttribute("forward").toString().equals("scanCheckOrderStock3"))){%>
	window.location="<%=request.getContextPath()%>/admin/orderStock/scanCheckOrderStock2.jsp";
<%}else{%>
	if(b==true){
		window.location="printPackage.do?method=printPackage&checkStatus=6&orderCode2=<%=orderCode%>";
	}
<%}%>
</script>

</body>
</html>