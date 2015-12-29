<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.buy.*, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="mmb.stock.cargo.*,mmb.stock.stat.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page isELIgnored="false" %>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<%
	int total = 0;
	String inTime = StringUtil.convertNull(request.getParameter("inTime"));
	if(inTime==null||inTime.equals("")){
		inTime = DateUtil.getNowDateStr();
	}
	request.setAttribute("inTime", inTime);
	String area = StringUtil.convertNull(request.getParameter("area"));
	boolean stockform = group.isFlag(884);
	int areaInt = StringUtil.toInt(area);
%>
<%
BuyStockinAction action = new BuyStockinAction();
String search = StringUtil.convertNull(request.getParameter("search"));
action.buyStockinList(request, response);

String supplierIds = cache.ProductLinePermissionCache.getProductLineSupplierIds(user);
supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;

List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");
String stockinCode = StringUtil.convertNull(request.getParameter("stockinCode"));
String buyStockCode = StringUtil.convertNull(request.getParameter("buyStockCode"));
String productCode = StringUtil.convertNull(request.getParameter("productCode"));
String productName = StringUtil.convertNull(request.getParameter("productName"));
productName = Encoder.decrypt(productName);
if(productName == null) {
	productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
}
String sCreateDate = StringUtil.convertNull(request.getParameter("sCreateDate"));
String eCreateDate = StringUtil.convertNull(request.getParameter("eCreateDate"));
String createUser = StringUtil.convertNull(request.getParameter("createUser"));
String auditUser = StringUtil.convertNull(request.getParameter("auditUser"));
String sBuyDate = StringUtil.convertNull(request.getParameter("sBuyDate"));
String eBuyDate = StringUtil.convertNull(request.getParameter("eBuyDate"));
String[] statuss = request.getParameterValues("status");
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));// 供应商id
int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));

int qarea = -1;
if(request.getParameter("qarea")!=null){
	qarea = StringUtil.StringToId(request.getParameter("qarea"));
}
List supplierList = (List) request.getAttribute("supplierList");//供货商信息
List productLineList = (List) request.getAttribute("productLineList");//产品线信息
String status = "";
if(statuss!=null){
	for(int i=0;i<statuss.length;i++){
		status = status + statuss[i]+",";
	}
}

PagingBean pagebean = (PagingBean)request.getAttribute("paging");
if(pagebean!=null){
	total= pagebean.getTotalCount();
}

int i, count;
BuyStockinBean bean = null;

//查看全部计划权限
boolean viewAll = group.isFlag(56);
boolean transform = group.isFlag(114);
boolean bianji = group.isFlag(169);
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
document.location = "buyOrderList.jsp";
</script>
<%
	return;
} else { 
%>

<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function checkAll(name,id) {     
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name) && (el[i].id==id)){
	    el[i].checked = true;         
	}     
    } 
}

function clearAll(name) {
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name)){
	    el[i].checked = false;
	}
    }
}
function checkboxChecked(checkbox,value){
	values = value.split(",");
	for(var j = 0; j < values.length; j++){
		for(var i = 0; i < checkbox.length; i++){
			if(checkbox[i].value == values[j]){
				checkbox[i].checked = true;
			}
		}
	}
}
function submitSearch() {
	var sCreateDate = document.searchForm.sCreateDate.value;
	var eCreateDate = document.searchForm.eCreateDate.value;
	var sBuyDate = document.searchForm.sBuyDate.value;
	var eBuyDate = document.searchForm.eBuyDate.value;
	if((sCreateDate == '' && eCreateDate != '')||(sCreateDate != '' && eCreateDate == '')) {
		alert('添加日期必须填写完整');
		return false;
	}
	if((sBuyDate == '' && eBuyDate != '')||(sBuyDate != '' && eBuyDate == '')) {
		alert('采购完成日期必须填写完整');
		return false;
	}
	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;	
	if(sCreateDate != '' && eCreateDate != '') {
	    if (!re.test(sCreateDate) || !re.test(eCreateDate)) {
	         alert('日期格式不合法');
	         return false;
	    }
	    if(sCreateDate > eCreateDate) {
			alert('开始时间不能大于结束时间');
			return false;
		}
	}
	if(sBuyDate != '' && eBuyDate != '') {
		if(!re.test(sBuyDate) || !re.test(eBuyDate)) {
			alert('日期格式不合法');
         	return false;
		}
		if(sBuyDate > eBuyDate) {
			alert('开始日期不能大于结束日期');
			return false;
		}
	}
	if($("#word").val().length==0){document.getElementById('supplierId').value=0}
	return true;
}
function select(){
	document.getElementById('supplierId').value = document.getElementById('supplierId').value;
}
// 入库统计
	$(function(){
		$("#result").hide();
		$("#inCount").click(function(){
			var inTime = $.trim($("#inTime").val());
			var username = $.trim($("#username").val());
			var stockCode = $.trim($("#stockCode").val());
			var productCode = $.trim($("#productCode").val());
			var area = $.trim($("#area").val());
			var isBTwoC = "${isBTwoC}";
			if(inTime==""){
				alert("请选择时间");
				return false;
			}
			$.post('<%= request.getContextPath()%>/admin/stockInStoreCount.mmx',{inTime:inTime,username:username,stockCode:stockCode,productCode:productCode,area:area,isBTwoC:isBTwoC},function(data){
						var json = eval('('+data+')');
						if(json['status']=="success"){
							$("#result").val(json['result']);
							$("#result").show();
						}else if(json['status']=="failure"){
							alert(json['result']);
						}else if(json['status']=="needLogin"){
							alert(json['result']);
						}
			});
		});	
	});

</script>
<p align="center">采购入库操作记录</p>
<table style="width:100%;">
	<tr>
		<td align="right" style="padding-right:40px;">共有(<%=total %>)条记录</td><!-- 这里记录查询后的总条数 -->
	</tr>
</table>
<form action="buyStockinList.jsp" method="post" name="searchForm" onsubmit="return submitSearch()">
<fieldset>
	<legend>
		查询条件
	</legend>
	<input type="hidden" name="isBTwoC" value="${isBTwoC }" />
	采购入库单编号：<input type="text" name="stockinCode" size="15" value="<%=stockinCode %>" />&nbsp;&nbsp;
	添加日期：<input type="text" name="sCreateDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=sCreateDate %>" />
	至<input type="text" name="eCreateDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eCreateDate %>" /><br/>
	生成人：<input type="text" name="createUser" size="10" value="<%=createUser %>" />&nbsp;&nbsp;
	审核人：<input type="text" name="auditUser" size="10" value="<%=auditUser %>" />&nbsp;&nbsp;
	采购完成时间：<input type="text" name="sBuyDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=sBuyDate %>" />
	至<input type="text" name="eBuyDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eBuyDate %>" /><br/>
	
	采购入库单状态：<input type="checkbox" name="status" value="0">未处理&nbsp;<input type="checkbox" name="status" value="3">入库处理中&nbsp;
	<input type="checkbox" name="status" value="6">已审核&nbsp;<input type="checkbox" name="status" value="5">审核未通过&nbsp;
	<input type="checkbox" name="status" value="4">入库已完成&nbsp;
	<input type="checkbox" name="status" value="7">已关闭<br/>
	
	采购预计到货单编号：<input type="text" name="buyStockCode" value="<%=buyStockCode %>"/>&nbsp;&nbsp;产品编号：<input type="text" name="productCode" value="<%=productCode %>"/>&nbsp;&nbsp;产品原名称：<input type="text" name="productName" value="<%=productName %>"/>模糊&nbsp;&nbsp;<br/>
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
	供应商：<div id="auto" style="position: absolute; left: 100px; top: 150px;"></div>
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
	</span>
	&nbsp;&nbsp;地区：
	<c:if test="${isBTwoC == 1 }">
		<select name="qarea" id="query_area">
			<option value="-1"></option>
				<c:forEach items="${areaList }" var="area_">
				<c:if test="${area_.id == qarea }">selected</c:if>
					<option value="${area_.id }" <c:if test="${area_.id == qarea }">selected</c:if>>${area_.areaName }</option>
				</c:forEach>
		</select>
	</c:if>
		<%if(isBTwoC == 0){ %>
		<% 
			String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("qarea", "query_area", request, qarea, false,"");
		%>
		<%= wareAreaLable%>
		<%} %>
	<script>document.searchForm.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text;</script>
	<script>selectOption(document.getElementById('supplierId'), '<%=supplierId%>');</script>
	<script>checkboxChecked(document.getElementsByName('status'),'<%=status%>');</script>
	<input type="hidden" name="search" value="search" />
	<input type="submit" name="searchStockin" value="查询" />
</fieldset>
</form>
<form method="post" action="transformBuyStockList.jsp?isBTwoC=${isBTwoC }">
<%if(transform){ %>
<input type="submit" value="转换新的采购入库单"/><br/>
<%} %>
</form>
<%if(stockform){%>
<form action="" method="post">
	<fieldset>
		<legend>入库统计</legend>
		<input type="hidden" name="isBTwoC" value="${isBTwoC }" />
		入库时间：<input type="text" name="inTime" id="inTime" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="${inTime}"/>&nbsp;&nbsp;
		&nbsp;&nbsp;入库操作人：<input type="text" name="usename" id="username" size="20" value="${username }"/><br>
		预计到货单：<input type="text" name="stockCode" id="stockCode" size="" value="${stockCode }"/>&nbsp;&nbsp;产品编号：<input type="text" name="productCode" id="productCode" size="" value="${productCode }"/>
		&nbsp;&nbsp;地区：
		<c:if test="${isBTwoC == 1 }">
		<select name="qarea" id="query_area">
		<option value="-1"></option>
			<c:forEach items="${areaList }" var="area_">
			<c:if test="${area_.id == qarea }">selected</c:if>
				<option value="${area_.id }" <c:if test="${area_.id == qarea }">selected</c:if>>${area_.areaName }</option>
			</c:forEach>
		</select>
		</c:if>
		<%if(isBTwoC == 0){ %>
			<% 
				String wareAreaLable2 = ProductWarePropertyService.getWeraAreaOptionsCustomized("area", "area", request, areaInt, false,"");
			%>
			<%= wareAreaLable2%>
		<%} %>
		&nbsp;&nbsp;<input type="button" id="inCount" value="统计">
	</fieldset>
</form>
<%}%>
&nbsp;&nbsp;&nbsp;<input type="text" size="100%" id="result"  value=""/>

<form method="post" action="collectBuyStockin.jsp">
<table width="100%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr>
  <!-- <td>选择</td> -->
  <td>序号</td>
  <td>编号</td>
  <td>添加时间</td>
  <td>产品线</td>
  <td>代理商</td>
  <td>预计到货单编号</td>
  <td>地区</td>
  <td>状态</td>
  <td>生成人/审核人</td>
  <td>操作</td>
</tr>
<%
if(list!=null){
count = list.size();
for(i = 0; i < count; i ++){
	bean = (BuyStockinBean) list.get(i);
%>
<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
  <!-- <td><input type="checkbox" name="ids" value="<%=bean.getId()%>" <%if(bean.getStatus() != BuyStockinBean.STATUS4){%>id="s1"<%}%><%if(bean.getStatus() == BuyStockinBean.STATUS4){%>id="s2"<%}%> /></td> -->
  <td><%=(i + 1)%></td>
  <td><a href="buyStockin.jsp?isBTwoC=${isBTwoC }&id=<%=bean.getId()%>"><%=bean.getCode()%></a></td>
  <td><%=bean.getCreateDatetime().substring(11, 16)%></td>
  <td><%=bean.getProductType() %></td>
  <td><%=bean.getProxyName()==null?"无": bean.getProxyName()%></td>
  <td><%=bean.getBuyStock().getCode() %></td>
  <td><%=ProductStockBean.areaMap.get(bean.getStockArea())%></td>
  <td><%if(bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1 || bean.getStatus() == BuyStockinBean.STATUS2){%><font color="red"><%=bean.getStatusName()%></font><%}else{%><%=bean.getStatusName()%><%}%></td>
  <td><%if(bean.getCreatUser()!=null){%><%=bean.getCreatUser().getUsername() %><%}%>/<%if(bean.getAuditingUser()!=null){%><%=bean.getAuditingUser().getUsername()%><%}%></td>
  <td><a href="buyStockin.jsp?isBTwoC=${isBTwoC }&id=<%=bean.getId()%>">编辑</a>
  <%if((bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS3 || bean.getProxyName() == null)&&(bianji||bean.getCreateUserId()==user.getId())){%>|<a href="deleteBuyStockin.jsp?isBTwoC=${isBTwoC }&buyStockinId=<%=bean.getId()%>" onclick="return confirm('确认删除？')">删除</a><%}%>
  <%if(group.isFlag(181) && (bean.getStatus() == BuyStockinBean.STATUS6 || bean.getStatus() == BuyStockinBean.STATUS4)){%><a href="batchPrice.jsp?id=<%=bean.getId()%>">|查看入库价</a><%}%><%if(group.isFlag(31)){ %>
  <%if(bean.getStatus()==BuyStockinBean.STATUS6||bean.getStatus()==BuyStockinBean.STATUS4){ %>|<a href="buyStockinPrint.jsp?stockinId=<%=bean.getId()%>" target="_blank" style="color: green;">打印</a><%if(bean.getPrintCount()>0){ %><a href="printLog.jsp?operId=<%=bean.getId()%>&type=<%= PrintLogBean.PRINT_LOG_TYPE_BUYSTOCKIN %>">|打印<%= bean.getPrintCount() %>次<%}else{ %>|打印次数<%} %></a><%} %><%}%>
  </td>
</tr>
<%
}}
%>
</table>
<!-- 
<p align="center"><input type="button" name="B" onclick="javascript:checkAll('ids','s1');" value="全选处理中"/><input type="button" name="B" onclick="javascript:checkAll('ids','s2');" value="全选已完成"/><input type="button" name="B" onclick="javascript:clearAll('ids');" value="全不选"/><input type="submit" name="B" value="汇总"/></p>
 -->
</form>
<p align="center"><%if(paging!=null){%><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%><%} %></p>
<%}%>