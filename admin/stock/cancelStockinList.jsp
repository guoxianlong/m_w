<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%
StockAction action = new StockAction();
action.cancelStockinList(request, response);

List list = (List) request.getAttribute("list");
List areaList = (List) request.getAttribute("areaList");
PagingBean paging = (PagingBean) request.getAttribute("paging");

int i, count;
StockOperationBean bean = null;

String orderCode = request.getParameter("orderCode");
if(orderCode == null) orderCode = "";

%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
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

function checkExport(){
	var startTime = document.exportForm.startTime.value;
	var endTime = document.exportForm.endTime.value;
	var startHour = document.getElementsByName("startHour")[0].value;
	var endHour = document.getElementsByName("endHour")[0].value;
	var startMin = document.getElementsByName("startMin")[0].value;
	var endMin = document.getElementsByName("endMin")[0].value;
	
	if(startTime == ''|| endTime == ''){
		alert('时间不能为空');
		return false;
	}
	
	if(startTime != '' && endTime != ''){
	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;
    if (!re.test(startTime) || !re.test(endTime))
     {
         alert('时间段格式不正确，必须是xxxx-xx-xx');
         return false;
     }
     
     if(startTime > endTime){
		alert('起始时间不得大于截止时间');
		return false;
	}else if(startTime==endTime){
		if(startHour>endHour&&endHour!=""){
			alert('起始时间不得大于截止时间');
			return false;
		}else if(startHour==endHour&&startHour!=""){
			if(startMin>endMin){
				alert('起始时间不得大于截止时间');
				return false;
			}
		}
	}
	
	var day = DateDiff(startTime,endTime);
	if(day>29){
		alert('输入的日期天数差必须小于等于30天');
		return false;
	}
	}
	
	return true;
}
 function DateDiff(beginDate,endDate)                    //计算天数
     {
            var arrbeginDate,Date1,Date2,arrendDate,iDays; 
            arrbeginDate= beginDate.split("-") 
            Date1= new Date(arrbeginDate[1]+'-'+arrbeginDate[2]+'-'+arrbeginDate[0])      //转换为2007-8-10格式
            arrendDate= endDate.split("-") 
            Date2= new Date(arrendDate[1]+'-'+arrendDate[2]+'-'+arrendDate[0]) 
            iDays = parseInt(Math.abs(Date1-Date2)/1000/60/60/24)                         //转换为天数 
            return iDays;     
     }
function searchPage(){
	document.getElementById("exportForm").action="<%=request.getContextPath()%>/admin/stock/showCancelStockinList.jsp";
	document.getElementById("exportForm").target="_blank";
	if(checkExport()){
		document.getElementById("exportForm").submit();
	}
	
}
function exportPage(){
	document.getElementById("exportForm").action="<%=request.getContextPath()%>/admin/stock/printCancelStockinList.jsp";
}
</script>
<script type="text/javascript">
<!--
	function filter(){
		var selectCancelType = document.getElementById('cancelType');
		window.location.href='./cancelStockinList.jsp?cancelType=' + selectCancelType.options[selectCancelType.selectedIndex].value;
	}
//-->
</script>
<p align="center">退货入库操作记录</p>
<form method="post" action="addCancelStockin.jsp">
订单编号：<input type="text" name="orderCode" size="20" value=""/>&nbsp;&nbsp;
<select name="type">
	<option value="1">退货</option>
	<option value="2">退换货</option>
	<option value="3">烂货退换</option>
</select>&nbsp;&nbsp;
<select name="areaId">
<%if(areaList!=null&&areaList.size()>0){
for(int j=0;j<areaList.size();j++){%>
	<option value="<%=areaList.get(j)%>"><%=ProductStockBean.areaMap.get(Integer.valueOf(areaList.get(j).toString()))%></option>
<%}}%>
</select>
<input type="submit" value="添加退货入库记录"/>&nbsp;&nbsp;
<a href="quickCancelStock.jsp">快速退货</a>
<br/>
</form>
<form method="post" action="collectCancel.jsp">
<table width="100%" border="1">
<tr>
  <td colspan="8" align="right"><select id="cancelType" name="cancelType" onchange="filter();">
  		<option value="0">全部</option>
  		<option value="1">退货</option>
  		<option value="2">退换货</option>
  		<option value="3">烂货退换</option>
  	</select>
  	<script type="text/javascript">
	<!--
		selectOption(document.getElementById('cancelType'), '<%= StringUtil.convertNull(request.getParameter("cancelType")) %>');
  	//-->
	</script>
  </td>
</tr>
<tr>
  <td>选择</td>
  <td>序号</td>
  <td>名称</td>
  <td>姓名</td>
  <td>添加时间</td>
  <td>状态</td>
  <td>新订单状态</td>
  <td>库房</td>
  <td>操作</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	bean = (StockOperationBean) list.get(i);
%>
<tr>
  <td><input type="checkbox" name="ids" value="<%=bean.getId()%>" <%if(bean.getStatus() == StockOperationBean.STATUS1){%>id="s1"<%}%><%if(bean.getStatus() == StockOperationBean.STATUS2){%>id="s2"<%}%> /></td>
  <td><%=(i + 1)%></td>
  <td><a href="cancelStockin.jsp?id=<%=bean.getId()%>" style="<%= (bean.isOperationState(StockOperationBean.STOCK_OPERATION_STATE_DELETED) || bean.isOperationState(StockOperationBean.STOCK_OPERATION_STATE_EDITED))?"color:green;":"" %>" ><%=bean.getName()%></a></td>
  <td><%= (bean.getOrder()!= null)?bean.getOrder().getName():"&nbsp;" %></td>
  <td><%=bean.getCreateDatetime().substring(0, 16)%></td>
  <td>
<%if(bean.getType() == StockOperationBean.CANCEL_STOCKIN){ %>
  	<%if(bean.getStatus() == StockOperationBean.STATUS1){%><font color="red"><%=bean.getStatusName()%></font><%}else{%><%=bean.getStatusName()%><%}%>
<%}else{ %>
	<%if(bean.getStatus() == StockOperationBean.STATUS1 || bean.getStatus() == StockOperationBean.STATUS2 || bean.getStatus() == StockOperationBean.STATUS3){%><font color="#CC0000"><%=bean.getStatusName()%></font><%}else{%><%=bean.getStatusName()%><%}%>
<%} %>
  </td>
  <td><%if(bean.getType() == StockOperationBean.CANCEL_EXCHANGE && bean.getOrder().getNewOrder() != null){ %><%= bean.getOrder().getNewOrder().getStatusName() %><%} else {%>&nbsp;<%} %></td>
  <td><%if(bean.getArea() == 0){%>北京<%} else {%>广东<%}%></td>
  <td><a href="cancelStockin.jsp?id=<%=bean.getId()%>">编辑</a><%if(bean.getStatus() == StockOperationBean.STATUS1){%>|<a href="deleteCancelStockin.jsp?id=<%=bean.getId()%>" onclick="return confirm('确认删除？')">删除</a><%}%></td>
</tr>
<%
}
%>
</table>
<p align="center"><input type="button" name="B" onclick="javascript:checkAll('ids','s1');" value="全选处理中"/><input type="button" name="B" onclick="javascript:checkAll('ids','s2');" value="全选已完成"/><input type="button" name="B" onclick="javascript:clearAll('ids');" value="全不选"/><input type="submit" name="B" value="汇总"/></p>
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<fieldset>
	<legend>退货入库查询</legend>
	<form method="get" action="./cancelStockinList.jsp">
		<p align="center">
			订单编号：<input type=text name="orderCode" value="<%= orderCode %>" size="30">
			<input type="submit" value="查询" />
			<input type="reset" value="复位" />
		</p>
	</form>
</fieldset>
<br/>
<br/>
<fieldset>
	<legend>退货信息导出</legend>
	<form method="get" action="<%=request.getContextPath()%>/admin/stock/printCancelStockinList.jsp" onSubmit="return checkExport();" name="exportForm" id="exportForm">
		<p align="left">
			退/换货时间：<input type="text" size=14 name="startTime" value="" onclick="SelectDate(this,'yyyy-MM-dd');"/>
			<select name="startHour">
				<option value=""></option>
				<%for(int startHour=0;startHour<24;startHour++){ %>
					<option value="<%=startHour<10?"0"+startHour:startHour %>"><%=startHour<10?"0"+startHour:startHour %></option>
				<%} %>
			</select>时
			<select name="startMin">
				<option value=""></option>
				<%for(int startMin=0;startMin<60;startMin++){ %>
					<option value="<%=startMin<10?"0"+startMin:startMin %>"><%=startMin<10?"0"+startMin:startMin %></option>
				<%} %>
			</select>分
			至<input type="text" size=14 name="endTime" value="" onclick="SelectDate(this,'yyyy-MM-dd');"/>&nbsp;&nbsp;
			<select name="endHour">
				<option value=""></option>
				<%for(int endHour=0;endHour<24;endHour++){ %>
					<option value="<%=endHour<10?"0"+endHour:endHour %>"><%=endHour<10?"0"+endHour:endHour %></option>
				<%} %>
			</select>时
			<select name="endMin">
				<option value=""></option>
				<%for(int endMin=0;endMin<60;endMin++){ %>
					<option value="<%=endMin<10?"0"+endMin:endMin %>"><%=endMin<10?"0"+endMin:endMin %></option>
				<%} %>
			</select>分
			快递公司：
			<select name="deliver">
				<option value="0">全部</option>
				<option value="11">广东省速递局</option>
				<option value="9">广东省外</option>
				<option value="10">广州宅急送</option>
				<option value="12">广州顺丰</option>
				<option value="13">深圳自建</option>
			</select>
			&nbsp;&nbsp;
			退货入库操作人：<input type="text" name="cancelUserName" size="10" />&nbsp;&nbsp;
			<input type="submit" value="导出" onclick="exportPage();" />&nbsp;&nbsp;<input type="button" onclick="searchPage();" value="查询" />
		</p>
	</form>
</fieldset>