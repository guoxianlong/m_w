<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.buy.*, adultadmin.action.vo.*,adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.PageUtil" %>
<%
	Stock2Action action = new Stock2Action();
action.transformBuyStockList(request, response);

String supplierIds = cache.ProductLinePermissionCache.getProductLineSupplierIds(user);
supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;

PagingBean paging = (PagingBean) request.getAttribute("paging");
List buyStockList = (List) request.getAttribute("buyStockList");
List buyStockProductList = (List) request.getAttribute("buyStockProductList");
String proxyName = (String)request.getAttribute("proxyName");
BuyStockBean stock = (BuyStockBean)request.getAttribute("stock");
String stockCode = StringUtil.convertNull(request.getParameter("stockCode"));
String startDate = StringUtil.convertNull(request.getParameter("startDate"));
String endDate = StringUtil.convertNull(request.getParameter("endDate"));
String productCode = StringUtil.convertNull(request.getParameter("productCode"));
String oriName = StringUtil.convertNull(request.getParameter("oriName"));
String createUser = StringUtil.convertNull(request.getParameter("createUser"));
String affirmUser = StringUtil.convertNull(request.getParameter("affirmUser"));
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));
List supplierList = (List) request.getAttribute("supplierList");//供货商信息
List productLineList = (List) request.getAttribute("productLineList");//产品线信息
oriName = Encoder.decrypt(oriName);
if(oriName == null) {
	oriName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("oriName")));
}
int i, count;
voProduct product = null;
BuyStockProductBean bpp = null;

//审核权限
boolean shenhe = group.isFlag(57);
//查看全部的计划
boolean viewAll = group.isFlag(54);
boolean assign = group.isFlag(59);

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
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
<script language="JavaScript">
function check(){
	var names = document.getElementsByName("buyStockProductId");
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
	return true;
}
function submitSearch() {
	var startDate = document.searchForm.startDate.value;
	var endDate = document.searchForm.endDate.value;
	if((startDate == '' && endDate != '')||(startDate != '' && endDate == '')) {
		alert('添加日期必须填写完整');
		return false;
	}

	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;	
	if(startDate != '' && endDate != '') {
	    if (!re.test(startDate) || !re.test(endDate)) {
	         alert('日期格式不合法');
	         return false;
	    }
	    if(startDate > endDate) {
			alert('开始时间不能大于结束时间');
			return false;
		}
	}
	if($("#word").val().length==0){document.getElementById('supplierId').value=0}
	return true;
}
function select(){
	document.getElementById('supplierId').value = document.getElementById('supplierId').value;
}
</script>
<p align="center">转换新的采购入库单</p>
	<form action="transformBuyStockList.jsp" method="post" name="searchForm" onsubmit="return submitSearch()" >
		<fieldset style="width: 97%;">
			<legend>
				查询条件
			</legend>
			<input type="hidden" name="isBTwoC" value="${isBTwoC }" />
			采购预计到货单编号：<input type="text" name="stockCode" size="15" value="<%=stockCode %>" />&nbsp;&nbsp;
			预计到货日期：<input type="text" name="startDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=startDate %>" />
			至<input type="text" name="endDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=endDate %>" />&nbsp;&nbsp;
			产品编号：<input type="text" name="productCode" size="14" value="<%=productCode %>" />&nbsp;&nbsp;
			产品原名称：<input type="text" name="oriName" size="14" value="<%=oriName %>" />模糊<br/>
			生成人：<input type="text" name="createUser" size="10" value="<%=createUser %>" />模糊&nbsp;&nbsp;
			确认人：<input type="text" name="affirmUser" size="10" value="<%=affirmUser %>" />&nbsp;&nbsp;
			产品线：<select name="productLine">
					<option value="0">全部</option>
				<%
				for (int p = 0; productLineList != null && p < productLineList.size(); p++) {
					voProductLine proLineBean = (voProductLine) productLineList.get(p);
				%>
					<option value="<%= proLineBean.getId() %>" <%if(productLine == proLineBean.getId()) {%> selected <%} %>><%= proLineBean.getName() %></option>
				<%
				}
				%>
			</select>&nbsp;
			供应商：<div id="auto" style="position: absolute; left: 100px; top: 110px;"></div>
				<input type="text" name="suppliertext" id="word" style="width: 100px; font-size: 10pt;height:20px;" />
				<input type="hidden" name="condition" id="condition" value="status = 1 and id in (<%=supplierIds %>)">
				<span style="width:18px;border:0px solid red;margin-left:-8px;margin-bottom:-6px;">
				<select name="supplierId" id="supplierId" onChange="javascript:select();document.all.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text" style="margin-left:-100px;width:118px;">
					<option value="0"></option>
					<%
						for(int k = 0; supplierList != null && k < supplierList.size(); k++) {
							voSelect ssInfoBean = (voSelect) supplierList.get(k);
					%>
					 		<option value="<%= ssInfoBean.getId() %>" <%if(supplierId == ssInfoBean.getId()) {%> selected <%} %>><%= ssInfoBean.getName() %></option>
					<%
						} 
					%>
				</select>
			</span>&nbsp;
			<script>document.searchForm.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text;</script>
			<script>selectOption(document.getElementById('supplierId'), '<%=supplierId%>');</script>
			<input type="submit" name="searchStockin" value="查询" />&nbsp;&nbsp;<a href="buyStockinList.jsp?isBTwoC=${isBTwoC }">返回采购入库列表</a>
		</fieldset>
	</form>
<form>
所有待入库的预计到货表：
<table width="95%" border="2" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr>
  <td>序号</td>
  <td>编号</td>
  <td>添加时间</td>
  <td>产品线</td>
  <td>代理商</td>
  <td>地区</td>
  <td>状态</td>
  <td>生成人/确认人</td>
</tr>
<%
if(buyStockList != null){
count = buyStockList.size();
for(i = 0; i < count; i ++){
	BuyStockBean bean = (BuyStockBean) buyStockList.get(i);
%>
<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
  <td><%=(i + 1)%></td>
  <td><a href="transformBuyStockList.jsp?&isBTwoC=${isBTwoC }&stockId=<%=bean.getId() %>&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex")) %>"><%=bean.getCode()%></a></td>
  <td><%=bean.getCreateDatetime().substring(11, 16)%></td>
  <td><%=bean.getProductType() %></td>
  <td><%=bean.getProxyName() %></td>
  <td><%=ProductStockBean.areaMap.get(bean.getArea()) %></td>
  <td>
<%if(bean.getStatus() == BuyPlanBean.STATUS0 || bean.getStatus() == BuyPlanBean.STATUS1 || bean.getStatus() == BuyPlanBean.STATUS4){%>
	<font color="red"><%=bean.getStatusName()%></font>
<%}else{%><%=bean.getStatusName()%><%}%>
  </td>
  <td><%if(bean.getCreatUser()!=null){%><%=bean.getCreatUser().getUsername() %><%}%>/<%if(bean.getAuditingUser()!=null){%><%=bean.getAuditingUser().getUsername()%><%}%></td>
</tr>
<%
}
%>
</table>
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%}%>
<%if(stock!=null){%>

<form method="post" action="addBuyStockin.jsp" onsubmit="return check();">
编号：<%=stock.getCode()%><br/>
<table width="95%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr align="left">
	<td colspan="13">
		代理商：<%=proxyName %>&nbsp;&nbsp;&nbsp;地区：<%=ProductStockBean.areaMap.get(stock.getArea()) %>
	</td>
</tr>
<tr>
  <td>选择</td>
  <td>序号</td>
  <td>产品线</td>
  <td>产品编号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>预计进货量(已入库量)</td>
  <td>进货前库存</td>
</tr>
<%
count = buyStockProductList.size();
int planCount = 0;
for(i = 0; i < count; i ++){
	bpp = (BuyStockProductBean) buyStockProductList.get(i);
	product = bpp.getProduct();
%>
<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
  <td><input type="checkbox" checked name="buyStockProductId" value="<%=bpp.getId()%>"/></td>
  <td><%=(i + 1)%></td>
  <td><%=bpp.getProductLineName() %></td>
  <td><a href="../fproduct.do?id=<%=bpp.getProduct().getId()%>" target="_blank"><%=bpp.getProduct().getCode()%></a></td>
  <td><a href="../fproduct.do?id=<%=bpp.getProduct().getId()%>" target="_blank"><%=bpp.getProduct().getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=bpp.getProduct().getId()%>" target="_blank"><%=bpp.getProduct().getOriname()%></a></td>
  <td><%=bpp.getBuyCount() %>(<%=bpp.getStockinCount() %>)</td>
  <td>
  	<%=bpp.getProduct().getStock(stock.getArea(),ProductStockBean.STOCKTYPE_QUALIFIED)+bpp.getProduct().getLockCount(stock.getArea(),ProductStockBean.STOCKTYPE_QUALIFIED)%>
  </td>
</tr>
<%
}
%>
<tr>
	<td colspan="12">
		<input type="button" value="反选" onclick="reserveCheck('buyStockProductId')"/>
		&nbsp;&nbsp;
		<input type="submit" value="对选中的产品生成采购入库单"/>
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
<input type="hidden" name="stockId" value="<%=stock.getId()%>"/>
<input type="hidden" name="buyOrderId" value="<%=stock.getBuyOrderId()%>"/>
<input type="hidden" name="isBTwoC" value="${isBTwoC }" />
<%}%>
</form>
<%} %>
