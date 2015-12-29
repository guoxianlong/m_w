<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, java.net.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<%@ page import="mmb.stock.IMEI.*" %>
<%@ page import="adultadmin.util.Encoder,adultadmin.util.StringUtil" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

ProductStockAction action = new ProductStockAction();
action.stockExchange(request, response);

Map productMap = (Map) request.getAttribute("productMap");
List sepList = (List) request.getAttribute("sepList");
List imeiStockExchangeList = (List) request.getAttribute("imeiStockExchangeList");
StockExchangeBean bean = (StockExchangeBean) request.getAttribute("bean");
List reasonList = (List) request.getAttribute("reasonList");
String message =request.getParameter("message");
int i, count;
voProduct product = null;
StockExchangeProductBean sep = null;
IMEIStockExchangeBean imeiStockExchangeBean = null;
Iterator itr = null;

boolean edit = (user.getId() == bean.getCreateUserId());

boolean allStockOut = true;
if(sepList.size() > 0){
itr = sepList.iterator();
while(itr.hasNext()){
	sep = (StockExchangeProductBean) itr.next();
	if(sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL){
		allStockOut = false;
	}
}
} else {
	allStockOut = false;
}
boolean allStockIn = true;
if(sepList.size() > 0){
itr = sepList.iterator();
while(itr.hasNext()){
	sep = (StockExchangeProductBean) itr.next();
	if(sep.getStatus() == StockExchangeProductBean.STOCKIN_UNDEAL){
		allStockIn = false;
	}
}
} else {
	allStockIn = false;
}
boolean checkIsFlag=false;
if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4 || bean.getStatus() == StockExchangeBean.STATUS8 || bean.getStatus()==StockExchangeBean.STATUS3 || bean.getStatus()==StockExchangeBean.STATUS5){
	checkIsFlag=true;
}
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript">
function addproduct(code,pid){
	document.addStockExchangeForm.productCode.value=code;
	var value = 1;
	value = prompt('将调配产品编号为'+code+'的产品，请输入数量，并确认', 1);
	if(value != null && value > 0){
		document.addStockExchangeForm.stockOutCount.value = value;
		document.addStockExchangeForm.submit();
	}
}
function check(){
	if(confirm("确认执行该操作？")){
		return true;
	}
	return false;
}

function complete(){
	if(confirm("确认操作？确认后不能撤销。")){
		return true;
	}
	return false;
}

function changeReason(rId){
	var reasons = document.getElementsByTagName("select");
	for(var i=0; i<reasons.length; i++){
		var reason = reasons[i];
		if(reason.name.indexOf("reason") != -1){
			if(reason.options[reason.selectedIndex].value <= 0){
				selectOption(reason, rId);
			}
		}
	}
}

function batchCommit(){
	if(!checkValidate('stockExchangeProductIds')){
		alert('请选择调拨产品');
		return;
	}
	if(confirm("确认操作？确认后不能撤销。")){
		document.stockExchangeItemForm.action="batchCompleteStockExchange.jsp";
		document.stockExchangeItemForm.submit();
	}
}

function checkBatchSumit(){
	var ids = document.getElementById('productIdsAndCount');
	var values = ids.value;
	if(values.trim()==''){
		alert('请先输入产品编号和调拨量，再添加！');
		return false;
	} 
	return true;
}
</script>
<p align="center">库存调拨操作</p>
<form method="post" action="editStockExchange.jsp">
编号：<%= bean.getCode() %>&nbsp;&nbsp;&nbsp;&nbsp;
<%= ProductStockBean.getStockTypeName(bean.getStockOutType()) %>(<%= ProductStockBean.getAreaName(bean.getStockOutArea()) %>)——&gt;<%= ProductStockBean.getStockTypeName(bean.getStockInType()) %>(<%= ProductStockBean.getAreaName(bean.getStockInArea()) %>)
&nbsp;&nbsp;&nbsp;&nbsp;状态：
<%if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS4 || bean.getStatus() == StockExchangeBean.STATUS7){%>
<font color="red"><%=bean.getStatusName()%></font> 
<%} else { %>
<%=bean.getStatusName()%>
<%} %>&nbsp;&nbsp;&nbsp;&nbsp;紧急程度：<font color="red"><%=bean.getPriorStatusName()%></font> 
<%if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4){%>
<%if(!allStockOut){ %>
<%-- %><a href="completeStockExchange.jsp?exchangeId=<%=bean.getId()%>" onclick="return complete();">全出库</a>--%>
<%} else { %>
<%if(group.isFlag(82) && edit){ %>
<a href="completeStockExchange.jsp?exchangeId=<%=bean.getId()%>&confirm=1" onclick="return complete();">确认提交</a>
<%} %>
<%} %>
<%} else if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5){ %>
<%if(!allStockIn){ %>
<%-- %><a href="completeStockExchange.jsp?exchangeId=<%=bean.getId()%>" onclick="return complete();">全入库</a>--%>
<%} else { %>
<%if(group.isFlag(83)){ %>
<a href="completeStockExchange.jsp?exchangeId=<%=bean.getId()%>&confirm=1" onclick="return complete();">确认入库</a>
<%} %>
<%} %>
<%} else if(bean.getStatus() == StockExchangeBean.STATUS2){%>
<%if(group.isFlag(80)){ %>
出库:&nbsp;&nbsp;
<a href="auditingStockExchange.jsp?exchangeId=<%=bean.getId()%>&mark=1" onclick="return check();">审核通过</a>
<a href="auditingStockExchange.jsp?exchangeId=<%=bean.getId()%>&mark=0" onclick="return check();">审核未通过</a>
<%} %>
<%} else if(bean.getStatus() == StockExchangeBean.STATUS6){%>
<%if(group.isFlag(81)){ %>
入库:&nbsp;&nbsp;
<a href="auditingStockExchange.jsp?exchangeId=<%=bean.getId()%>&mark=1&audintType=audintingIn" onclick="return check();">审核通过</a>
<a href="auditingStockExchange.jsp?exchangeId=<%=bean.getId()%>&mark=0" onclick="return check();">审核未通过</a>
<%} %>
<%} %>
<br/>
备注：<textarea name="remark" cols="40" rows="5"><%=bean.getRemark()%></textarea><input type="submit" value="修改"><br/>
<input type="hidden" name="exchangeId" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="stockExchange.jsp"/>
</form>
<fieldset>
   <legend>操作</legend><%
if((bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && edit){
%><form method="post" name="addStockExchangeForm" action="addStockExchangeItem.jsp" >
<p align="center" style="margin:-10px;">商品编号/IMEI码：<input type="text" id="productCode" name="productCode" value="" size="8"/>&nbsp;调拨量：<input type="text" name="stockOutCount" value="1" size="3"/>&nbsp;货位号：<input type="text" name="cargoCode" size="12"/>&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="添加" /></p>
<script>document.getElementById("productCode").focus()</script>
<input type="hidden" name="exchangeId" value="<%=bean.getId()%>" />
</form>
<%
}
%>
<p align="center"><a href="./stockAdminHistory.jsp?exchangeId=<%=bean.getId()%>&logType=<%= StockAdminHistoryBean.STOCK_EXCHANGE %>" target="_blank">人员操作记录</a>|<a href="stockExchangeList.jsp">返回库存调拨操作记录列表</a><%-- |<a href="stockExchangePrint.jsp?exchangeId=<%=bean.getId()%>" target="_blank">导出列表</a> --%></p>
</fieldset>
<%if((bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && edit && bean.getStockOutType() != ProductStockBean.STOCKTYPE_QUALIFIED){
%>
	<form method="post" name="batchStockExchangeForm" action="batchStockExchangeItem.jsp">
		<table width="40%">
			<tr><td colspan="2">批量操作（仅适用于源库非合格库时）</td></tr>
			<tr>
				<td><textarea rows="10" cols="30" name="productIdsAndCount" id="productIdsAndCount"></textarea></td>
				<td align="left"><font color="red"> 输入格式：<br/>产品编号/IMEI码（空格）调拨量<br/> 产品编号/IMEI码（空格）调拨量<br/>（可从excel复制粘贴）</font><br/><br/><br/><br/><br/>
					<input type="hidden" name="exchangeId" value="<%=bean.getId()%>" />
					<input type="submit" value="添加" onclick="return checkBatchSumit()">
				</td>
			</tr>
		</table>
	</form>
<%}%>
<%if(message!=null || request.getParameter("tip")!=null){
	%><div style="margin-left:15px">
		<%if(!"".equals(message)){%><font color="red">成功添加<%=message.split(",")[0]%>个产品 失败<%=message.split(",")[1]%>个产品</font><%}%>
		<%if(!"".equals(request.getParameter("tip"))){%>&nbsp;&nbsp;&nbsp;<font color="red"><%=Encoder.decrypt(StringUtil.convertNull(request.getParameter("tip")))%></font><%}%><br/>
		<%if(!"".equals(request.getParameter("productCodes"))){%> <font color="red"><%=Encoder.decrypt(StringUtil.convertNull(request.getParameter("productCodes")))%></font><%}%>
	</div><%
}%>
<%
if((bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && edit){
%>
<fieldset style="padding:0px;">
	<legend>产品查询</legend>
	<form method=post action="../searchProductHasStock.do?forward=Exchange&exchangeId=<%= bean.getId() %>" target=sp onsubmit="document.all.d1.style.display='block';return true;">
		<input type="hidden" name="type" value="1" />
		<input type=hidden name="code" value="" size=12>
		<p align="center" style="margin-top:-10px;margin-bottom:-5px;padding:0px;">
			产品名：<input type=text name="name" value="" size=12>
			<input type="hidden" name="stockArea" value="<%= bean.getStockOutArea() %>" />
			<input type="hidden" name="stockType" value="<%= bean.getStockOutType() %>" />
			<input type="hidden" name="targetStockArea" value="<%= bean.getStockInArea() %>" />
			<input type="hidden" name="targetStockType" value="<%= bean.getStockInType() %>" />
			<input type=submit value="查询产品" onclick="if(this.form.name.value==''){alert('必须输入产品名称');return false;} return document.all.d1.style.display='block';">
			<input type="button" value="查询源库商品" onclick="this.form.name.value=''; this.form.submit(); return document.all.d1.style.display='block';">
			<input type=button value="关闭窗口" onclick="document.all.d1.style.display='none';window.close();">
		</p>
	</form>
	<div id=d1 style="margin-left:-20px;margin-right:-10px;width:800px;display:none;">
	<iframe name=sp width=99% height=300 align="center" frameborder="0">
	</iframe>
	</div>
</fieldset>
<%} %>
<form name="stockExchangeItemForm" method="post" action="editStockExchangeItem.jsp">
<table width="95%" border="1">
<tr>
	<td colspan="11" style="text-align:right;">调拨量<%= request.getAttribute("stockOutCount") %>&nbsp;&nbsp;</td>
</tr>
<tr>
  <td><input type="checkbox" name="checkbox11"  <%=checkIsFlag?"":"disabled='disabled'"%> onclick="chageChecked('checkbox11','stockExchangeProductIds')" >序号</td>
  <td>原名称</td>
  <td>产品编号</td>
  <td>调拨量</td>
<%--
  <td>入库量</td>
--%>
  <td>源库库存</td>
  <td>目的库库存</td>
  <td>源货位(库存量)</td>
  <td>目的货位(库存量)</td>
  <td>调拨原因</td>
<%--
  <td>损耗</td>
--%>
  <td>状态及操作</td>
  <td>查进销存</td>
</tr>
<%
count = sepList.size();
int stockOutCount = 0;
int stockInCount = 0;
for(i = 0; i < count; i ++){
	sep = (StockExchangeProductBean) sepList.get(i);
	product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));
%>
<tr>
  <td><input type="checkbox" name="stockExchangeProductIds" <%=checkIsFlag ?sep.getStatus()==StockExchangeProductBean.STOCKOUT_DEALED?"disabled='disabled'":sep.getStatus()==StockExchangeProductBean.STOCKIN_DEALED?"disabled='disabled'":"":"disabled='disabled'"%> value="<%=sep.getId()%>" onclick="firstChangeChecked('checkbox11','stockExchangeProductIds')">&nbsp;<%=(i + 1)%></td>
  <td><%if(product != null){ %><a href="../fproduct.do?id=<%= product.getId()%>"><%=product.getOriname()%></a><%}else{ %>-<%} %></td>
  <td><%if(product != null){ %><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getCode()%></a><%}else{ %>-<%} %></td>
  <td>
  	<input type="text" name="stockOutCount<%=sep.getId()%>" size="5" value="<%=sep.getStockOutCount() %>" <%if((bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS4) || !edit || sep.getStatus() != StockExchangeProductBean.STOCKOUT_UNDEAL){%>readonly<%}%> />
  	<input type="hidden" name="stockInCount<%=sep.getId()%>" size="5" value="<%=sep.getStockInCount()%>" />
  </td>
<%--
  <td><input type="text" name="stockInCount<%=sep.getId()%>" size="5" value="<%=sep.getStockInCount()%>" <%if((bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS7)){%>readonly<%}else if(sep.getStatus() != StockExchangeProductBean.STOCKIN_UNDEAL){%>readonly<%}%>/></td>
--%>
  <td><%= sep.getPsOut().getStock() %></td>
  <td><%= sep.getPsIn().getStock() %></td>
  <td>
  	<%if(sep.getSepcOut()!=null){ %>
  	<%=sep.getSepcOut().getCargoInfo()==null?"":sep.getSepcOut().getCargoInfo().getWholeCode() %>(<%= sep.getSepcOut().getCargoProductStock()==null?0:sep.getSepcOut().getCargoProductStock().getStockCount() %>)
  <%} %>
  </td>
  <td>
  	<%if(sep.getSepcIn()!=null){ %>
  	<%=sep.getSepcIn().getCargoInfo()==null?"":sep.getSepcIn().getCargoInfo().getWholeCode() %>(<%= sep.getSepcIn().getCargoProductStock()==null?0:sep.getSepcIn().getCargoProductStock().getStockCount() %>)
  <%} %>
  </td>
  <td>
<%if((bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL && edit){ %>
		<select name="reason<%=sep.getId()%>" <%if(i==0){ %>onchange="changeReason(this.options[this.selectedIndex].value);"<%} %> >
			<option value="0"></option>
<%for(int j=0; j<reasonList.size(); j++){
	TextResBean tr = (TextResBean)reasonList.get(j);
%>
<option value="<%= tr.getId() %>"><%= tr.getContent() %></option>
<%} %>
		</select>
<script>selectOption(stockExchangeItemForm.reason<%=sep.getId()%>, '<%= sep.getReason() %>')</script>
<%} else { %><%= sep.getReasonText() %>&nbsp;<%} %>
  </td>
<%--
  <td><%= sep.getStockOutCount() - sep.getStockInCount() %></td>
--%>
  <td>
  <%if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS2 || bean.getStatus() == StockExchangeBean.STATUS4){ %>
  <%if(sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL){%><font color="red">未处理</font>
<%if(group.isFlag(82) && edit){ %>
  	<a href="completeStockExchange.jsp?exchangeId=<%=bean.getId()%>&sepId=<%=sep.getId()%>&hisStatus=<%= StockExchangeProductBean.STOCKOUT_DEALED %>" onclick="return complete();">提交</a>
<%} %>
  <%} else {%>已提交<%}%> 
  <%} else if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5 || bean.getStatus() == StockExchangeBean.STATUS6 || bean.getStatus() == StockExchangeBean.STATUS7 || bean.getStatus() == StockExchangeBean.STATUS8){ %>
  <%if(sep.getStatus() == StockExchangeProductBean.STOCKIN_UNDEAL){%><font color="red">未处理</font>
<%if(group.isFlag(83)){%>
  	<a href="completeStockExchange.jsp?exchangeId=<%=bean.getId()%>&sepId=<%=sep.getId()%>&hisStatus=<%= StockExchangeProductBean.STOCKIN_DEALED %>" onclick="return complete();">入库</a>
<%} %>
  <%} else {%>已入库<%}%>
  <%} %>
  <%if((bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS4) && edit){ %>
  <%if(sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL){%><a href="deleteStockExchangeItem.jsp?exchangeId=<%=bean.getId()%>&sepId=<%=sep.getId()%>" onclick="return confirm('确认删除？');">删除</a><%}%>
  <%} %>&nbsp;
  </td>
  <td><%if(product != null){ %><a href="../productStock/stockCardList.jsp?productCode=<%= product.getCode() %>" target="_blank">查</a><%}else{ %>-<%} %></td>
</tr>
<%
}
%>
</table>
<%if((bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && edit){%>
	<p align="center">
		<%if(group.isFlag(82)){ %>
		<input type="hidden" value="<%=StockExchangeProductBean.STOCKOUT_DEALED%>" name="hisStatus" id="hisStatus">
		<input type="button" onclick="batchCommit()" value="选中的产品批量提交"/>
		<%}%>
		&nbsp;&nbsp;&nbsp;<input type="submit" value="修改数量及调拨原因"/>
	</p>
<%}else if((bean.getStatus()==StockExchangeBean.STATUS8||bean.getStatus()==StockExchangeBean.STATUS3|| bean.getStatus()==StockExchangeBean.STATUS5 )&& group.isFlag(83)) {%>
	 <p align="center">
	 	<input type="hidden" value="<%=StockExchangeProductBean.STOCKIN_DEALED%>" name="hisStatus" id="hisStatus">
	 	<input type="button" onclick="batchCommit()" value="选中的产品批量入库"/>
	 </p>  
<%}%>
<input type="hidden" name="exchangeId" value="<%=bean.getId()%>"/>
<table width="95%" border="1">
<tr>
  <td>序号</td>
  <td>产品编号</td>
  <td>IMEI码</td>
  <td>操作</td>
</tr>
<%
count = imeiStockExchangeList.size();
for(i = 0; i < count; i ++){
	imeiStockExchangeBean = (IMEIStockExchangeBean) imeiStockExchangeList.get(i);
	product = (voProduct) productMap.get(Integer.valueOf(imeiStockExchangeBean.getProductId()));
	sep = imeiStockExchangeBean.getStockExchangeProductBean();
	IMEIBean imeiBean = imeiStockExchangeBean.getImeiBean();
%>
<tr>
  <td>&nbsp;<%=(i + 1)%></td>
  <td><%if(product != null){ %><a href="../fproduct.do?id=<%= product.getId()%>"><%=product.getCode()%></a><%}else{ %>-<%} %></td>
  <td><%= imeiBean.getCode() %></td>
  <td>  
  <%if((bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && edit){%>
  		<%if(sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL){%><a href="deleteIMEIProduct.jsp?exchangeId=<%=bean.getId()%>&sepId=<%=sep.getId()%>&imeiStockExchangeId=<%=imeiStockExchangeBean.getId() %>" onclick="return confirm('确认删除？');">删除</a><%} %>
<%}%>
  </td>
</tr>
<%
}
%>
</table>
</form>