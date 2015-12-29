<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<html>
<head>
<title>货位进销存卡片</title>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");

int i, count;
CargoStockCardBean bean = null;
voProduct product = (voProduct) request.getAttribute("product");

String stockType = StringUtil.convertNull(request.getParameter("stockType"));
String stockArea = StringUtil.convertNull(request.getParameter("stockArea"));
int area = StringUtil.toInt(stockArea);
String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String productCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productCode")));
String productOriName="";
if(Encoder.decrypt(StringUtil.dealParam(request.getParameter("productOriName")))!=null){
	productOriName =Encoder.decrypt(StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productOriName"))));
}else{
	productOriName=StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productOriName")));
}
String productName = "";
if(Encoder.decrypt(StringUtil.dealParam(request.getParameter("productName")))!=null){
	productName =Encoder.decrypt(StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName"))));
}else{
	productName=StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
}
String storeType = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("cargoStoreType")));
String cargoWholeCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("cargoWholeCode")));

String startDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("startDate")));
String endDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("endDate")));
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript">
$(function () {
	loadArea($("#type").val(),<%= area%>);
		$("#type").change(function(){
			$("#tarArea").val(-1);
			loadArea($("#type").val(),-1);
		});
});
function loadArea(stockTypeId,tag){
		$.ajax({
			url:'${pageContext.request.contextPath}/CargoController/querySelectStockAreaAccess.mmx?stockType='+stockTypeId,
			cache:false,
			dataType:'text',
			type:'post',
			success:function(dd){
				$("#tarArea").empty();
				var arr = eval('('+dd+')');
				for(var i=0;i<arr.length;i++){
					var opt = $("<option>").text(arr[i].text).val(arr[i].id);
					$("#tarArea").append(opt);
				}
				if(tag!=-1){
					//查询后库地区默认选中
					$("#tarArea").val(tag);
				}
			}
		});
	}

function checkSubmit(){
	var i = trim(document.forms[0].productCode.value).length;
	var j = trim(document.forms[0].productName.value).length;
	var k = trim(document.forms[0].productOriName.value).length;
	if(i == 0 && j == 0 && k == 0){
		alert("产品编号、小店名称、原名称必须填写一项");
		return false;
	}
}
</script>
</head>
<body>
货位进销存
<form method="post" action="../admin/cargoInfo.do?method=cargoStockCard" onsubmit="return checkSubmit();">
<fieldset style="width:780px;"><legend>查询栏</legend>
库类型：<select name="stockType" id="type">
<option value="">全部</option>
<%
	HashMap stockMap = ProductStockBean.stockTypeMap;
	Iterator stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER)
			continue;
%>
	<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
<%} %>
</select>&nbsp;&nbsp;
库区域：<select name="stockArea" id="tarArea">
<option value="">全部</option>
</select>&nbsp;&nbsp;
起始时间：<input name="startDate" value="<%= startDate %>" size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');" />&nbsp;&nbsp;
截止时间：<input name="endDate" value="<%= endDate %>" size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');" /><br/>
单据号：<input name="code" value="<%= code %>" size="12" />&nbsp;&nbsp;
产品编号：<input name="productCode" value="<%= productCode %>" size="8" />&nbsp;&nbsp;
小店名称：<input name="productName" value="<%= productName %>" size="8" />&nbsp;&nbsp;
原名称：<input name="productOriName" value="<%= productOriName %>" size="8" /><br/>
货位存放类型：<select name="cargoStoreType">
<option value="">请选择</option>
<option value="0" <%if(storeType!=null&&storeType.equals("0")){ %>selected=selected<%} %>>散件区</option>
<option value="1" <%if(storeType!=null&&storeType.equals("1")){ %>selected=selected<%} %>>整件区</option>
<option value="2" <%if(storeType!=null&&storeType.equals("2")){ %>selected=selected<%} %>>缓存区</option>
<option value="4" <%if(storeType!=null&&storeType.equals("4")){ %>selected=selected<%} %>>混合区</option>
<option value="5" <%if(storeType!=null&&storeType.equals("5")){ %>selected=selected<%} %>>作业区</option>
</select>&nbsp;&nbsp;
货位号：<input type="text" name="cargoWholeCode" <%if(cargoWholeCode!=null){ %>value="<%=cargoWholeCode%>"<%} %>/>&nbsp;&nbsp;
<input type="submit" value="进销存查询" />
</fieldset>
</form>
<script type="text/javascript">
<!--
selectOption(document.forms[0].stockType, '<%= stockType %>');
setStockArea2(document.forms[0].stockType, document.forms[0].stockArea);
selectOption(document.forms[0].stockArea, '<%= stockArea %>');
//-->
</script>
<table width="100%" border="1" border="1" bordercolor="#D8D8D5" style="border-collapse:collapse;">
<%if(product != null){ %>
<tr><td colspan="16" style="color:FF0000;">
产品编号：<%= product.getCode() %>&nbsp;&nbsp;
小店名称：<%= StringUtil.toWml(product.getName()) %>&nbsp;&nbsp;
原名称：<%= StringUtil.toWml(product.getOriname()) %>&nbsp;&nbsp;
状态：<%= product.getStatusName() %>
</td></tr>
<%} %>
<tr style="background-color:4688D6; color:white;">
  <td>库类型</td>
  <td>库区域</td>
  <td>单据号</td>
  <td>卡片类型</td>
  <td>创建时间</td>
  <td>入库量</td>
  <%if(group.isFlag(182)){ %>
  <td>入库金额</td>
  <%} %>
  <td>出库量</td>
  <%if(group.isFlag(182)){ %>
  <td>出库金额</td>
  <%} %>
  <td width="100">货位号</td>
  <td>货位存放类型</td>
  <td>当前货位库存</td>
  <td>本区域本库类总库存</td>
  <td>全库总库存</td>
  <%if(group.isFlag(182)){ %>
  <td>库存单价</td>
  <td>结存总额</td>
  <%} %>
</tr>
<%
if(list != null){
count = list.size();
for(i = count-1; i >= 0; i --){
	bean = (CargoStockCardBean) list.get(i);
%>
<tr style="<%= (i%2==0)?"background-color:EEE9D9;":"" %>" >
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getStockTypeName(bean.getStockType()) %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getAreaName(bean.getStockArea()) %></td>
  <td><%= bean.getCode() %></td>
  <td><%= bean.getCardTypeName() %></td>
  <td><%= StringUtil.cutString(bean.getCreateDatetime(), 19) %></td>
  <td><%= (bean.getStockInCount() > 0)?String.valueOf(bean.getStockInCount()):"-" %></td>
  <%if(group.isFlag(182)){ %>
  <td><%= (bean.getStockInPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockInPriceSum()):"-" %></td>
  <%} %>
  <td><%= (bean.getStockOutCount() > 0)?String.valueOf(bean.getStockOutCount()):"-" %></td>
  <%if(group.isFlag(182)){ %>
  <td><%= (bean.getStockOutPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockOutPriceSum()):"-" %></td>
  <%} %>
  <td><%=bean.getCargoWholeCode() %></td>
  <td><%=bean.getCargoStoreTypeName() %></td>
  <td><%=bean.getCurrentCargoStock() %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getCurrentStock() %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getAllStock() %></td>
  <%if(group.isFlag(182)){ %>
  <td><%= bean.getStockPrice() %></td>
  <td><%= StringUtil.formatDouble2(bean.getAllStockPriceSum()) %></td>
  <%} %>
</tr>
<%
}
%>
</table>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", paging.getCountPerPage())%></p>
<%}%>
</body>
</html>