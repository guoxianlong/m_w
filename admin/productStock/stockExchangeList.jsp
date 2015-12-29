<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
ProductStockAction action = new ProductStockAction();
String tips = request.getParameter("tips");
action.stockExchangeList(request, response);
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
StockExchangeBean bean = null;

int stockOutType = StringUtil.toInt(request.getParameter("stockOutType"));
int stockInType = StringUtil.toInt(request.getParameter("stockInType"));
int stockOutArea = StringUtil.toInt(request.getParameter("stockOutArea"));
int stockInArea = StringUtil.toInt(request.getParameter("stockInArea"));
int status = StringUtil.toInt(request.getParameter("status"));
String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String stockOutOper = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("stockOutOper")));
String stockInOper = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("stockInOper")));
String stockOutAudit = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("stockOutAudit")));
String stockInAudit = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("stockInAudit")));
String createDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("createDate")));
String dealDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("dealDate")));
String addPriorStatus = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("addPriorStatus")));
int priorStatus = StringUtil.toInt(request.getParameter("priorStatus"));
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript">
function checkSubmit(addForm){
	with(addForm){
		if(stockOutType.options[stockOutType.selectedIndex].value == -1){
			alert("请选择正确的源库类型");
			return false;
		}
		if(stockOutArea.options[stockOutArea.selectedIndex].value == -1){
			alert("请选择正确的源库地址");
			return false;
		}
		if(stockInType.options[stockInType.selectedIndex].value == -1){
			alert("请选择正确的目的库类型");
			return false;
		}
		if(stockInArea.options[stockInArea.selectedIndex].value == -1){
			alert("请选择正确的目的库地址");
			return false;
		}
		
	}
}

function setStockArea2(sptSrc, sptObj){
	x = sptSrc.selectedIndex;
	for (var m = sptObj.options.length - 1; m > 0; m --){
		sptObj.options[m] = null;
	}
	sptObj.options[0]=new Option("全部", "-1");
	for (i = 0; i < ps_spts[x].length; i ++){
		sptObj.options[i + 1]=new Option(ps_spts[x][i].text, ps_spts[x][i].value);
	}
	sptObj.options[0].selected=true;
}
function batchInStorage(){
	var values= checkValidateAndValus('checkbox');
	document.getElementById('batchInHidden1').value=values;
	document.batchInForm1.submit();
}
function batchInStorage1(){
	var values= checkValidateAndValus('checkbox');
	document.getElementById('batchInHidden2').value=values;
	document.batchInForm2.submit();
}
function batchInStorage2(){
	var values= checkValidateAndValus('checkbox');
	document.getElementById('batchInHidden3').value=values;
	document.batchInForm3.submit();
}

	$(function(){
		//源库库类型,源库地点
		if(<%=stockOutType%>!=-1){
			$("#stockOutType").val(<%=stockOutType%>);
			loadStockArea($("#stockOutType").val(),"stockOutArea",<%=stockOutArea%>);
		}
		$("#stockOutType").change(function(){
			loadStockArea($("#stockOutType").val(),"stockOutArea",-1);
		});
		//目的库库类型,目的库地点
		if(<%=stockInType%>!=-1){
			$("#stockInType").val(<%=stockInType%>);
			loadStockArea($("#stockInType").val(),"stockInArea",<%=stockInArea%>);
		}
		$("#stockInType").change(function(){
			loadStockArea($("#stockInType").val(),"stockInArea",-1);
		});
		//源库
<%-- 		if(<%=stockOutType%>!=-1){ --%>
<%-- 			$("#stockOutType2").val(<%=stockOutType%>); --%>
<%-- 			loadStockArea($("#stockOutType2").val(),"stockOutArea2",<%=stockOutArea%>); --%>
// 		}
		$("#stockOutType2").change(function(){
			loadStockArea($("#stockOutType2").val(),"stockOutArea2",-1);
		});
		//目的库
<%-- 		if(<%=stockInType%>!=-1){ --%>
<%-- 			$("#stockInType2").val(<%=stockInType%>); --%>
<%-- 			loadStockArea($("#stockInType2").val(),"stockInArea2",<%=stockInArea%>); --%>
// 		}
		$("#stockInType2").change(function(){
			loadStockArea($("#stockInType2").val(),"stockInArea2",-1);
		});
		
	});
	function loadStockArea(typeId,area_id,areaValue){
		$.ajax({
			url:'${pageContext.request.contextPath}/CargoController/querySelectStockAreaAccess.mmx?stockType='+typeId,
			cache:false,
			dataType:'text',
			type:'post',
			success:function(dd){
				$("#"+area_id).empty();
				var arr = eval('('+dd+')');
				for(var i=0;i<arr.length;i++){
					var opt = $("<option>").text(arr[i].text).val(arr[i].id);
					$("#"+area_id).append(opt);
				}
				$("#"+area_id).val(areaValue);
			}
		});
	}
	/*
	$(function(){
		loadStockOutArea($("#stockOutType").val(),<%=stockOutArea%>);
		$("#stockOutType").change(function(){
			$("#stockOutTarArea").val(-1);
			loadStockOutArea($("#stockOutType").val(),-1);
		});
	});
	//动态加载库地区
	function loadStockOutArea(stockTypeId,tag){
		$.ajax({
			url:'${pageContext.request.contextPath}/CargoController/querySelectStockAreaAccess.mmx?stockType='+stockTypeId,
			cache:false,
			dataType:'text',
			type:'post',
			success:function(dd){
				$("#stockOutTarArea").empty();
				var arr = eval('('+dd+')');
				for(var i=0;i<arr.length;i++){
					var opt = $("<option>").text(arr[i].text).val(arr[i].id);
					$("#stockOutTarArea").append(opt);
				}
				if(tag!=-1){
					//查询后库地区默认选中
					$("#stockOutTarArea").val(tag);
				}
			}
		});
	}
	*/
</script>
<form name="searchForm" method="get" action="stockExchangeList.jsp">
<fieldset style="width:810px;"><legend>查询栏</legend>
源库库类型：<select name="stockOutType" id="stockOutType">
<option value="-1">全部</option>
<%
	HashMap stockMap = ProductStockBean.stockTypeMap;
	Iterator stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER){
			continue;
		}
%>
	<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
<%} %>
</select>&nbsp;&nbsp;
源库地点：<select name="stockOutArea" id="stockOutArea">
<option value="-1">全部</option>
</select>&nbsp;&nbsp;
目的库库类型：<select name="stockInType" id="stockInType">
<option value="-1">全部</option>
<%
	stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER){
			continue;
		}
%>
	<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
<%} %>
</select>&nbsp;&nbsp;
目的库地点：<select name="stockInArea" id="stockInArea">
<option value="-1">全部</option>
</select>&nbsp;&nbsp;
状态：<select name="status">
<option value="-1">全部</option>
<option value="0">未处理</option>
<option value="1">出库处理中</option>
<option value="2">出库审核中</option>
<option value="3">已审核待入库</option>
<option value="4">出库审核未通过</option>
<option value="5">入库处理中</option>
<option value="6">入库审核中</option>
<option value="7">调拨完成</option>
<option value="8">入库审核未通过</option>
</select><br/>
调拨单号：<input name="code" value="<%= code %>" size="12" />&nbsp;&nbsp;
出库操作人：<input name="stockOutOper" value="<%= stockOutOper %>" size="8" />&nbsp;&nbsp;
出库审核人：<input name="stockOutAudit" value="<%= stockOutAudit %>" size="8" />&nbsp;&nbsp;
入库操作人：<input name="stockInOper" value="<%= stockInOper %>" size="8" />&nbsp;&nbsp;
入库审核人：<input name="stockInAudit" value="<%= stockInAudit %>" size="8" /><br/>
创建时间：<input name="createDate" value="<%= createDate %>" size="10" readonly="readonly" onClick="SelectDate(this,'yyyy-MM-dd');" />&nbsp;&nbsp;
完成时间：<input name="dealDate" value="<%= dealDate %>" size="10" readonly="readonly" onClick="SelectDate(this,'yyyy-MM-dd');" />&nbsp;&nbsp;&nbsp;&nbsp;
紧急程度：<select name="priorStatus" >
        <option value="-1">全部</option>
		<option value="0">一般</option>
		<option value="1">紧急</option>
	</select>&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" value="调拨单查询" />&nbsp;&nbsp;
<input type="button" value="查看全部调拨单" onClick="window.location.href='stockExchangeList.jsp';" />
</fieldset>
</form>

<table width="200" border="0">
  <tr>
  <%if(group.isFlag(602)){ %>
    <td><form method="post" name='batchInForm1' action="batchInStorage.jsp?type=1">
    	<input type='hidden' name='batchInHidden1' id='batchInHidden1'/>
    	<input type='button' name='batchInStorage1' value='批量入库' onclick='batchInStorage()'>
    </form>
    </td><%} %>
     <%if(group.isFlag(604)){ %>
    <td><form method="post" name='batchInForm2' action="batchInStorage.jsp?type=2">
    	<input type='hidden' name='batchInHidden2' id='batchInHidden2'/>
   		<input type='button' name='batchAffirmInStorage' value='批量确认入库' onclick='batchInStorage1()'>
    </form>
    </td>
    <%} %>
     <%if(group.isFlag(603)){ %>
    <td><form method="post" name='batchInForm3' action="batchInStorage.jsp?type=3">
    <input type='hidden' name='batchInHidden3' id='batchInHidden3'/>
    	<input type='button' name='batchVerify' value='批量审核' onclick='batchInStorage2()'>
    </form>
    </td><%} %>
  </tr>
</table>

<script type="text/javascript"><div align="center">
<!--
selectOption(document.forms[0].stockOutType, '<%= stockOutType %>');
setStockArea2(document.forms[0].stockOutType, document.forms[0].stockOutArea);
selectOption(document.forms[0].stockInType, '<%= stockInType %>');
setStockArea2(document.forms[0].stockInType, document.forms[0].stockInArea);
selectOption(document.forms[0].stockOutArea, '<%= stockOutArea %>');
selectOption(document.forms[0].stockInArea, '<%= stockInArea %>');
selectOption(document.forms[0].status, '<%= status %>');
selectOption(document.forms[0].priorStatus, '<%= priorStatus %>');
//-->
</div></script>
<form method="post" action="addStockExchange.jsp" onSubmit="return checkSubmit(this);">
<input type="hidden" name="name" size="50" value="<%=DateUtil.getNow().substring(0, 10)%>库存调拨"/>
<fieldset style="width:310px;"><legend>源库</legend>
库类型：<select name="stockOutType" id="stockOutType2">
<option value="-1"></option>
<%
	stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER){
			continue;
		}
%>
	<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
<%} %>
</select>&nbsp;
源库地点：<select name="stockOutArea" id="stockOutArea2">
<option value="-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
</select></fieldset>
<fieldset style="width:310px;"><legend>目的库</legend>
库类型：<select name="stockInType" id="stockInType2">
<option value="-1"></option>
<%
	stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER ){
			continue;
		}
%>
	<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
<%} %>
</select>&nbsp;
目的库地点：<select name="stockInArea" id="stockInArea2">
<option value="-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
</select></fieldset>
<fieldset style="width:300px;">
	紧急程度：<select name="addPriorStatus" >
		<option value="0">一般</option>
		<option value="1">紧急</option>
	</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="submit" value="添加库存调拨单"/><br/>
</fieldset>
</form>
<table width="100%" border="1">
<tr>
  <td>序号</td>
  <td>编号</td>
  <td>源库</td>
  <td>目的库</td>
  <td>出库操作</td>
  <td>出库审核</td>
  <td>入库操作</td>
  <td>入库审核</td>
  <td>状态</td>
  <td>紧急程度</td>
  <td>操作</td>
  <td>创建时间</td>
  <td>完成时间</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	bean = (StockExchangeBean) list.get(i);
	boolean edit = (user.getId() == bean.getCreateUserId())||group.isFlag(0);
%>
<tr>
  <td><input name='checkbox' type='checkbox' value='<%=bean.getId()%>'><%=(i + 1)%></td>
  <td><%if (bean.isAfterSaleFlag()) { %><a href="<%=request.getContextPath()%>/admin/afStock/afterSaleStockExchange.jsp?stockExchangeId=<%=bean.getId()%>"><%=bean.getCode()%></a><%} else {%><a href="stockExchange.jsp?exchangeId=<%=bean.getId()%>"><%=bean.getCode()%></a><%} %></td>
  <td><%= ProductStockBean.getStockTypeName(bean.getStockOutType()) %>(<%= ProductStockBean.getAreaName(bean.getStockOutArea()) %>)</td>
  <td><%= ProductStockBean.getStockTypeName(bean.getStockInType()) %>(<%= ProductStockBean.getAreaName(bean.getStockInArea()) %>)</td>
  
  <td><%=(!bean.getCreateUserName().equals(""))?bean.getCreateUserName():(bean.getCreateUser()!=null?bean.getCreateUser().getUsername():"&nbsp") %></td>
  <td><%=(!bean.getAuditingUserName().equals(""))?bean.getAuditingUserName():(bean.getAuditingUser()!=null?bean.getAuditingUser().getUsername():"&nbsp") %></td>
  <td><%=(!bean.getStockInOperName().equals(""))?bean.getStockInOperName():(bean.getStockInOperUser()!=null?bean.getStockInOperUser().getUsername():"&nbsp") %></td>
  <td><%=(!bean.getAuditingUserName2().equals(""))?bean.getAuditingUserName2():(bean.getAuditingUser2()!=null?bean.getAuditingUser2().getUsername():"&nbsp") %></td>
  <td><%if(bean.getStatus() == StockExchangeBean.STATUS0){%><font color="red"><%=bean.getStatusName()%></font><%}else{%><%=bean.getStatusName()%><%}%></td>
   <td><%=bean.getPriorStatusName()%></td>
  <td><%if (bean.isAfterSaleFlag()) { %><a href="<%=request.getContextPath()%>/admin/afStock/afterSaleStockExchange.jsp?stockExchangeId=<%=bean.getId()%>">编辑</a><%} else {%><a href="stockExchange.jsp?exchangeId=<%=bean.getId()%>">编辑</a><%} %>
  <%if(bean.getStatus() >= StockExchangeBean.STATUS3){%>
  |<a href="stockChangePrint.jsp?exchangeId=<%=bean.getId()%>&outUser=<%=(!bean.getCreateUserName().equals(""))?bean.getCreateUserName():(bean.getCreateUser()!=null?bean.getCreateUser().getUsername():"&nbsp")  %>" target="_blank">导出列表</a>
  |<a href="<%=request.getContextPath() %>/admin/barcodeManager/stockChangePrintLine.jsp?exchangeId=<%=bean.getId()%>&outUser=<%=(bean.getCreateUser() != null)?bean.getCreateUser().getUsername():"&nbsp;"  %>" target="_blank">打印</a>
  <%} %>
  <%if((bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS1) && edit){%>|<a href="deleteStockExchange.jsp?exchangeId=<%=bean.getId()%>" onClick="return confirm('确认删除？')">删除</a><%} else if (bean.isAfterSaleFlag() && bean.getStatus() == StockExchangeBean.STATUS4 && edit){%>|<a href="<%=request.getContextPath()%>/admin/AfStock/deleteStockExchange.mmx?exchangeId=<%=bean.getId()%>" onClick="return confirm('确认删除？')">删除</a><%} %></td>
  <td><%=StringUtil.convertNull(StringUtil.cutString(bean.getCreateDatetime(), 10, 16))%>&nbsp;</td>
  <td><%if(bean.getStatus() == StockExchangeBean.STATUS7){ %><%=StringUtil.convertNull(StringUtil.cutString(bean.getConfirmDatetime(), 16))%><%} %>&nbsp;</td>
</tr>
<%
}
%>
</table>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>