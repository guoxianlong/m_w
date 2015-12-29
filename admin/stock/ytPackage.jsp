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
String position="";
if(request.getAttribute("deliverRelationInfoList")!=null){
	list = (List)request.getAttribute("deliverRelationInfoList");
	if(!list.isEmpty()){
		DeliverRelationInfoBean deliverRelationInfoBean =list.get(0);
		position = deliverRelationInfoBean.getInfo();
	}
}
String DqFlag=request.getAttribute("DqFlag")==null?"":request.getAttribute("DqFlag").toString();

%>
<html>
<head>
<title>圆通快递面单</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
<div id="szzjPackage">
<!-- Save for Web Slices (配送单--标注.jpg) -->
<table id="__01"  border="1" cellpadding="0" cellspacing="0" style="font-family: SimHei;">
    <tr>
      <td style="width: 18mm;height: 0mm;border-style: none;"></td>
      <td style="width: 30mm;height: 0mm;border-style: none;"></td>
      <td style="width: 30mm;height: 0mm;border-style: none;"></td>
      <td style="width: 22mm;height: 0mm;border-style: none;"></td>
    </tr>
    <tr>
      <td style="height: 10mm;" nowrap><font style="font-size: 16px;">代收货款</font></td>
	  <td colspan="2" style="height: 10mm;border-left-style: none;"><font style="font-size: 16px;">金额：¥&nbsp;&nbsp;&nbsp;&nbsp;<font style="font-size: 16px;font-weight: bold;"><%if(order.getBuyMode()==0){%><%=order.getDprice() %><%}else {%>0<%}%></font>元</font></td>
	  <td style="height: 10mm;border-left-style: none;">
	  	<%if("1".equals(DqFlag)){ %>
      <img src="<%=request.getContextPath()%>/image/dQ.jpg" width="100" height="40" />
      <%}else{ %>
       <img src="<%=request.getContextPath()%>/image/logo_cartonning.jpg" width="100" height="40" />
      <%} %>
	  </td>
    </tr>
	<tr>
      <td colspan="4" style="height: 10mm; "><font style="font-size: 25px;font-weight: bold;">&nbsp;&nbsp;&nbsp;&nbsp;<%=StringUtil.convertNull(position)%></font></td>
	</tr>
	<tr>
      <td colspan="4" style="height: 10mm; "></td>
	</tr>
	<tr>
	  <td style="height: 8mm; " nowrap><font style="font-size: 16px;">收件人：</font></td>
	  <td style="height: 8mm;border-left-style: none;" colspan="3"><font style="font-size: 16px;"><%=order.getName() %>&nbsp;&nbsp;<%=order.getPhone() %></font></td>
	</tr>
	<tr>
	  <td style="height: 9mm;border-top-style: none;"></td>
	  <td colspan="3" style="height: 16mm;width:80mm;border-left-style: none;border-top-style: none;"><font style="font-size: 17px;"><%=order.getAddress() %></font></td>
	</tr>
	<tr>
	  <td style="height: 9mm;" nowrap><font style="font-size: 16px;">寄件人：</font></td>
	  <td style="height: 9mm;border-left-style: none;"><font style="font-size: 16px;">无锡买卖宝</font></td>
	  <td style="height: 9mm;border-left-style: none;" colspan="2"><font style="font-size: 16px;font-weight: bold;">买卖宝<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓</font></td>
	</tr>
	<tr>
	  <td style="height: 7.5mm;border-top-style: none;"></td>
	  <td colspan="3" style="height: 7.5mm;width: 80mm;border-left-style: none;border-top-style: none;"><font style="font-size: 16px;"><%if (!order.isTaobaoOrder()) { %>电话：40088-43211<%} %></font></td>
	</tr>
	<tr>
      <td colspan="2" style="height: 8mm;">收件人/代收人：</td>
	  <td colspan="2" style="height: 8mm;border-left-style: none;">签收时间：</td>
	</tr>
	<tr>
	  <td colspan="2" style="height: 8mm;border-top-style: none;"></td>
	  <td colspan="2" style="height: 8mm;border-left-style: none;border-top-style: none;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;月&nbsp;&nbsp;日</td>
	 </tr>
	 <tr>
      <td colspan="2" style="height: 10mm;"> <img src="<%=request.getContextPath()%>/image/yto.jpg" width="100" height="35" /></td>
	  <td colspan="2" style="height: 10mm;border-left-style: none;"></td>
	</tr>
	 <tr>	  
	  <td rowspan="2" style="height: 4mm;">寄件方：</td>
	  <td colspan="2" style="height: 4mm;">地址：买卖宝<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓</td>
	  <td rowspan="2" style="height: 4mm;"><font style="font-size: 16px;font-weight: bold;">&nbsp;&nbsp;&nbsp;<%=order.getDeliver() %></font></td>
	 </tr>
	 <tr>
       <td colspan="2" style="height: 4mm;border-top-style: none;">发货人：无锡买卖宝</td>
      </tr>
	 <tr>
	   <td colspan="2" rowspan="2" style="height: 8mm;">订单号：<%=order.getCode()%></td>
       <td colspan="2" style="height: 4mm;">快递公司：<%=order.getDeliverName() %></td>
	 </tr>
	 <tr>
	   <td colspan="2" style="height: 4mm;border-top-style: none;">品类：<%=orderTypeName %></td>
      </tr>
	 <tr>
	  <td rowspan="3" style="height: 5mm;">客户信息</td>
      <td colspan="3" style="height: 10mm;width: 83mm;">地址：<%=order.getAddress() %></td>
	  </tr>
	 <tr>
       <td colspan="2" style="height: 4mm;">姓名：<%=ocBean.getName() %>&nbsp;&nbsp;邮编：<%=order.getPostcode() %></td>
       <td style="height: 4mm;"><font style="font-size: 8px">订单金额：<%=order.getDprice() %>元</font></td>
	 </tr>
	 <tr>
	   <td colspan="2" style="height: 4mm;border-top-style: none;">电话：<%=order.getPhone() %></td>
	   <td style="height: 4mm;border-top-style: none;"><font style="font-size: 8px">包裹重量：<%=apBean==null?"0.0":apBean.getWeight()/1000 %>kg</font></td>
      </tr>
	  <tr>
      <td colspan="4" style="height: 10mm; width:100mm;">【温馨提示】保留此包裹单底联，代表您已签收并认可我们配送的商品及发货明细单背面规定的内容，如有任何疑问，可以通过以下方式联系mmb客服中心【电话：40088-43211】</td>
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
		LODOP.ADD_PRINT_BARCODE("28mm", "5mm","67mm", "9mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("93mm", "32mm","60mm", "8mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
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