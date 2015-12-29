<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.buy.*, adultadmin.action.vo.*,adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.framework.*" %><%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.PageUtil" %>
<%
	if(request.getAttribute("buyOrderList")==null){
Stock2Action action = new Stock2Action();
action.transformBuyOrderList(request, response);
}
String supplierIds = cache.ProductLinePermissionCache.getProductLineSupplierIds(user);
supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
PagingBean paging = (PagingBean) request.getAttribute("paging");
List buyOrderList = (List) request.getAttribute("buyOrderList");
List buyOrderProductList = (List) request.getAttribute("buyOrderProductList");
List supplierList = (List) request.getAttribute("supplierList");
HashMap supplierMap = (HashMap)request.getAttribute("supplierMap");
Integer supplier_id = (Integer)request.getAttribute("supplier_id");
BuyOrderBean order = (BuyOrderBean)request.getAttribute("order");
String params = (String)request.getAttribute("params");

String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String status = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("status")));
String payStatus = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("payStatus")));
String startTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("startTime")));
String endTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("endTime")));
String supplierId = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("supplierId")));
int transformCount = StringUtil.StringToId(StringUtil.convertNull(StringUtil.dealParam(request.getParameter("transformCount"))));
String productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
String productCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productCode")));
List productLineList = (List) request.getAttribute("productLineList");
productName = Encoder.decrypt(productName);//解码为中文
if(productName==null){//解码失败,表示已经为中文,则返回默认
	productName =StringUtil.dealParam(request.getParameter("productName"));//名称
}
if (productName==null) productName="";

int i, count;
voProduct product = null;
BuyOrderProductBean bpp = null;
Iterator itr = null;
voSelect select = null;
Iterator iter = null;

//审核权限
boolean shenhe = group.isFlag(57);
//查看全部的计划
boolean viewAll = group.isFlag(54);
boolean assign = group.isFlag(59);
boolean search = group.isFlag(194);

boolean check = false;
if(request.getParameter("check") != null){
	check = true;
}



String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
history.back(-1);
</script>
<%
	return;
} else { 
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
<script language="JavaScript">
function check(){
	var names = document.getElementsByName("buyOrderProductId");
	var area = document.getElementById("area").value;
	var i;
	var check = false;
	for(i=0; i<names.length; i++){
		if(names[i].checked){
			check = true;
		}
		
	}
	if(!check){
		window.alert("未选择任何产品");
		return false;
	}
	if(area == -1){
		window.alert("地区不能为空");
		return false;
	}
	return true;
}
function searchCheck(){
	var startTime = document.searchForm.startTime.value;
	var endTime = document.searchForm.endTime.value;
	var transformCount = document.searchForm.transformCount.value;
	if((startTime == '' && endTime != '')||(startTime != '' && endTime == '')){
		alert('生成时间段必须填写完整');
		return false;
	}
	if(startTime != '' && endTime != ''){
	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;
    if (!re.test(startTime) || !re.test(endTime))
     {
         alert('日期格式不合法');
         return false;
     }
     
     if(startTime > endTime){
		alert('开始时间不能大于结束时间');
		return false;
	}
	}
	if(transformCount<0){
		alert('已转换成预计到货表次数不能小于0');
		return false;
	}
	
	if($("#word").val().length==0){document.getElementById('supplierId').value=0}
	return true;
}
function select(){
	document.getElementById('supplierId').value = document.getElementById('supplierId').value;
}
</script>
<p align="center">转换新的采购入库价调整单</p>
<%if(search){ %>
<fieldset>
   <legend>查询条件</legend>
   <form name="searchForm" action="<%=request.getContextPath()%>/admin/searchTransformBuyOrderList.do" method="post" onSubmit="return searchCheck();">
   采购订单编号：<input type="text" size="14" name="code" value="<%=code %>"/>&nbsp;&nbsp;
   <script>selectOption(document.getElementById('status'), '<%=status%>');</script>
   打款状态：
   <select name="payStatus">
   		<option value="">全部</option>
   		<option value="0">未打款</option>
   		<option value="1" style="color:red">已打款</option>
   		<option value="2,3" style="color:green">已打款</option>
   </select>&nbsp;&nbsp;
   <script>selectOption(document.getElementById('payStatus'), '<%=payStatus%>');</script>
  添加采购订单日期：<input type="text" size="14" name="startTime" value="<%=startTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
  到<input type="text" size="14" name="endTime" value="<%=endTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
  产品线：<select name="productLine" id="productLine">
   		 	<option value="">全部</option>
   		 	<%
   				itr = productLineList.listIterator();
				while (itr.hasNext()) {
					voProductLine proLineBean = (voProductLine)itr.next();
   		 	%>
   		 	<option value="<%= proLineBean.getId() %>" ><%= proLineBean.getName() %></option>
			<%
				} 
			%>
   		 </select><br/>
   		 <script>selectOption(document.getElementById('productLine'), '<%=productLine%>');</script>
  产品编号：<input type="text" name="productCode" id="productCode" value="<%=productCode %>" size="14" />&nbsp;&nbsp;
  代理商：
  <div id="auto" style="position:absolute; left:100px; top:125px;"></div>
  <script>document.all.proxytext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text;</script>
  <input type="text" name="suppliertext" onClick="select()" id="word" style="width:100px;height:20px;font-size:10pt;">
  <input type="hidden" name="condition" id="condition" value="status = 1 and id in (<%=supplierIds %>)">
  <span style="width:18px;border:0px solid red;margin-left:-8px;margin-bottom:-6px;">
  		<select name="supplierId" id="supplierId" onchange="javascript:select();document.all.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text" style="margin-left:-100px;width:118px;">
			<option value="0"></option>
			<%
			if (supplierList!=null && supplierList.size()>0) {
				iter = supplierList.listIterator();
				while(iter.hasNext()){
					select = (voSelect)iter.next();
			%>
  			<option value="<%= select.getId() %>"><%= select.getName() %></option>
			<%} 
			}%>
		</select>
		</span>&nbsp;&nbsp;
		<script>selectOption(document.getElementById('supplierId'), '<%=supplierId%>');</script>
		<input type="hidden" name="from" value="stockBatch"/>
		<input type="hidden" name="search" value="search"/>
产品名称：<input type="text" name="productName" value="<%=productName %>"/>&nbsp;&nbsp;
  <input type="submit" value="查询"/>
   </form>
</fieldset>
<%} %>
<form>
所有可转换的采购订单：

<table width="95%" border="2" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr>
  <td>序号</td>
  <td>编号</td>
  <td>添加时间</td>
  <td>产品线</td>
  <td>代理商</td>
  <td>状态</td>
  <td>生成人/审核人</td>
  <td>预计进货总额</td>
  <td>已打款</td>
</tr>
<%
if(buyOrderList != null){
count = buyOrderList.size();
for(i = 0; i < count; i ++){
	BuyOrderBean bean = (BuyOrderBean) buyOrderList.get(i);
%>
<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
  <td><%=(i + 1)%></td>
  <td>
  	<a href="<%=request.getContextPath() %>/admin/searchTransformBuyOrderList.do<%=params==null||params.equals("")?"?":params+"&" %>orderId=<%=bean.getId() %>&orderCode=<%=bean.getCode() %>&from=stockBatch&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex")) %>"><%=bean.getCode()%></a>
  </td>
  <td><%=bean.getCreateDatetime().substring(11, 16)%></td>
  <td><%=bean.getProductType() %></td>
  <td>
  <%
  	select = (voSelect)supplierMap.get(Integer.valueOf(bean.getProxyId())); 
  %>
  <%=select==null?"无":select.getName() %>
  </td>
  <td>
<%if(bean.getStatus() == BuyPlanBean.STATUS0 || (bean.isOvertime() && bean.getStatus() == BuyPlanBean.STATUS3)){%>
	<font color="red"><%=bean.getStatusName()%></font>
<%} else if(bean.getStatus() == BuyPlanBean.STATUS2 || bean.getStatus() == BuyPlanBean.STATUS4){%>
	<font color="green"><%=bean.getStatusName()%></font>
<%}else{%><%=bean.getStatusName()%><%}%>
  </td>
  <td><%if(bean.getCreatUser()!=null){%><%=bean.getCreatUser().getUsername() %><%}%>/<%if(bean.getAuditingUser()!=null){%><%=bean.getAuditingUser().getUsername()%><%}%></td>
  <td><%=bean.getTotalPurchasePrice()%></td>
  <td><%=bean.getMoney() %></td>
</tr>
<%
}
%>
</table>
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%}%>
<%if(order!=null){%>

<form method="post" action="<%=request.getContextPath() %>/admin/stock2/addStockBatchPrice.jsp" onsubmit="return check();">
<style type=text/css>   
  .redfont{color:red}
</style>
编号：<%=order.getCode()%><br/>
<table width="95%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr align="left">
	<td colspan="12">
		代理商：
  <%
  	select = (voSelect)supplierMap.get(supplier_id); 
  %>
  <%=select==null?"无":select.getName() %>
  &nbsp;&nbsp;&nbsp;&nbsp;
  税点：<%=order.getTaxPoint() %>
	</td>
</tr>
<tr>
  <td>选择</td>
  <td width="3%">序号</td>
  <td width="6%">产品线</td>
  <td>产品编号</td>
  <td width="10%">产品名称</td>
  <td width="10%">原名称</td>
  <%-- <td width="12%">北京预计采购量<br/>(已进货量)(已入库量)</td> --%>
  <td width="12%">广东预计采购量<br/>(已进货量)(已入库量)</td>
  <td>总采购量</td>
  <td>预计进货税前价（税后价）</td>
  <td>预计进货税前金额（税后金额）</td>
  <td width="5%">已入库的货款金额</td>
</tr>
<%
count = buyOrderProductList.size();
float totalPrice = 0;
float totalStockinPrice = 0;
for(i = 0; i < count; i ++){
	bpp = (BuyOrderProductBean) buyOrderProductList.get(i);
	product = bpp.getProduct();
%>
<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
  <td><input type="checkbox" checked name="buyOrderProductId" value="<%=bpp.getId()%>"/></td>
  <td><%=(i + 1)%></td>
  <td><%=bpp.getProductLineName() %></td>
  <td><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=bpp.getProduct().getId()%>" target="_blank"><%=bpp.getProduct().getCode()%></a></td>
  <td><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=bpp.getProduct().getId()%>" target="_blank"><%=bpp.getProduct().getName()%></a></td>
  <td><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=bpp.getProduct().getId()%>" target="_blank"><%=bpp.getProduct().getOriname()%></a></td>
  <td><input type="text" name="orderCountGD<%=bpp.getProduct().getId()%>" size="5" value="<%=bpp.getOrderCountGD()%>" readonly/>(<%=bpp.getStockCountGD() %>)(<%=bpp.getStockinCountGD() %>)</td>
  <td><%=bpp.getOrderCountBJ()+bpp.getOrderCountGD() %></td>
  <td><input type="text" name="purchasePrice<%=bpp.getProduct().getId()%>" size="7" value="<%=bpp.getPurchasePrice() %>" readonly/>(<%=df.format(Arith.mul(bpp.getPurchasePrice(),Arith.add(1,order.getTaxPoint()))) %>)</td>
  <td>
  	<%=(bpp.getOrderCountBJ()+bpp.getOrderCountGD())*bpp.getPurchasePrice() %>(<%=df.format(Arith.mul(bpp.getOrderCountGD(),Arith.mul(bpp.getPurchasePrice(),Arith.add(1,order.getTaxPoint()))))%>)
  	<%totalPrice += (bpp.getOrderCountBJ()+bpp.getOrderCountGD())*bpp.getPurchasePrice();%>
  </td>
  <td>
  	<%=bpp.getStockinTotalPrice() %>
  	<%totalStockinPrice += bpp.getStockinTotalPrice(); %>
  </td>
</tr>
<%}%>
<tr>
  <td></td>
  <td>合计</td>
  <td></td>
  <td></td>
  <td></td>
  <td></td>
  <td></td>
  <td></td>
  <td></td>
  <td><%=totalPrice %>(<%=df.format(Arith.mul(totalPrice,Arith.add(1,order.getTaxPoint())))%>)</td>
  <td><%=totalStockinPrice %></td>
  <td></td>
</tr>
<tr>
	<td colspan="13">
		<input type="button" value="反选" onclick="reserveCheck('buyOrderProductId')"/>
		&nbsp;&nbsp;
		<input type="submit" value="对选中的产品生成采购入库价调整单"/>
	</td>
</tr>
</table>
<script type="text/javascript">
function reserveCheck(name){
  var names=document.getElementsByName(name);
  var len=names.length;
 if(len>0){
 	var i=0;
    for(i=0;i<len;i++){
     if(names[i].checked)
     names[i].checked=false;
     else
     names[i].checked=true;
    }
 } 
}
</script>
<input type="hidden" name="orderId" value="<%=order.getId()%>"/>
<%} %>
</form>
<%} %>
