<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

ProductStockAction action = new ProductStockAction();
action.stockCardList(request, response);

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
}

List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");

int i, count;
StockCardBean bean = null;
voProduct product = (voProduct) request.getAttribute("product");

int stockType = StringUtil.toInt(request.getParameter("stockType"));
int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
int stockCardType = StringUtil.toInt(request.getParameter("stockCardType"));
String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String productCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productCode")));
String productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
String productOriName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productOriName")));

productName = Encoder.decrypt(productName);//解码为中文
if(productName==null){//解码失败,表示已经为中文,则返回默认
	productName =StringUtil.dealParam(request.getParameter("productName"));//名称
}
if (productName==null) productName="";

productOriName = Encoder.decrypt(productOriName);//解码为中文
if(productOriName==null){//解码失败,表示已经为中文,则返回默认
	productOriName =StringUtil.dealParam(request.getParameter("productOriName"));//名称
}
if (productOriName==null) productOriName="";

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
	loadArea($("#type").val(),<%= stockArea%>);
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
	
	document.forms[0].action = 'stockCardList.jsp';
}
function checkexport(){
	var i = trim(document.forms[0].productCode.value).length;
	var j = trim(document.forms[0].productName.value).length;
	var k = trim(document.forms[0].productOriName.value).length;
	var startDate = trim(document.forms[0].startDate.value);
	var endDate = trim(document.forms[0].endDate.value);
	if( startDate != null && startDate != "" && endDate != null && endDate != "" ) {
		if( endDate < startDate ) {
			alert("开始时间不能小于结束时间！");
			return false;
		}
	}
	if(i == 0 && j == 0 && k == 0){
		alert("产品编号、小店名称、原名称必须填写一项");
		return false;
	}
	
	document.forms[0].action = '<%= request.getContextPath()%>/admin/productStock/stockCardExport.mmx';
	document.forms[0].submit();
}
</script>
<form name="searchForm" method="post" action="stockCardList.jsp" onsubmit="return checkSubmit();">
<fieldset style="width:820px;"><legend>查询栏</legend>
库类型：<select name="stockType" id="type">
<option value="-1">全部</option>
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
<option value="-1">全部</option>
</select>
&nbsp;&nbsp;
&nbsp;&nbsp;
卡片类型：<select name="stockCardType" id="stockCardType" >
	<option value="-1">全部</option>
	<%
	Map<Integer,String> stockCardTypeMap = StockCardBean.stockCardTypeMap;
	Iterator stockCardKeyIter = stockCardTypeMap.keySet().iterator();
	while(stockCardKeyIter.hasNext()){
		Integer key = (Integer)stockCardKeyIter.next();
	%>
	<option value="<%= key.intValue() %>" <%= key.intValue() == stockCardType ? "selected" : "" %>><%= StockCardBean.stockCardTypeMap.get(key.intValue()) %></option>
<%} %>
</select>
&nbsp;&nbsp;
起始时间：<input name="startDate" value="<%= startDate %>" size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');" />&nbsp;&nbsp;
截止时间：<input name="endDate" value="<%= endDate %>" size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');" /><br/>
单据号：<input name="code" value="<%= code %>" size="12" />&nbsp;&nbsp;
产品编号：<input name="productCode" value="<%= productCode %>" size="8" />&nbsp;&nbsp;
小店名称：<input name="productName" value="<%= productName %>" size="8" />&nbsp;&nbsp;
原名称：<input name="productOriName" value="<%= productOriName %>" size="8" /><br/>
<input type="submit" value="进销存查询" />
&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" value="进销存导出" onClick="checkexport();"/>
</fieldset>
</form>
<script type="text/javascript">
selectOption(document.forms[0].stockType, '<%= stockType %>');
selectOption(document.forms[0].stockArea, '<%= stockArea %>');
</script>
<table width="100%" border="1" border="1" bordercolor="#D8D8D5" style="border-collapse:collapse;">
<%if(product != null){ %>
<tr><td colspan="15" style="color:FF0000;">
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
  <td>来源</td>
  <td>时间</td>
  <td>入库数量</td>
<%if(group.isFlag(182)){ %>
  <td>入库金额</td>
<%} %>
  <td>出库数量</td>
<%if(group.isFlag(182)){ %>
  <td>出库金额</td>
<%} %>
  <td>当前结存</td>
  <td>本库区域总结存</td>
  <td>本库类总结存</td>
  <td>全库总结存</td>
<%if(group.isFlag(182)){ %>
  <td>库存单价</td>
  <td>结存总额</td>
<%} %>
</tr>
<%
if(list != null){
count = list.size();
for(i = 0; i < count; i ++){
	bean = (StockCardBean) list.get(i);
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
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getCurrentStock() %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getStockAllArea() %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getStockAllType() %></td>
  <td><%= bean.getAllStock() %></td>
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