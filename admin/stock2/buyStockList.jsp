<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.buy.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@page import="java.util.Iterator"%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<%
	if(request.getAttribute("list")==null){
Stock2Action action = new Stock2Action();
action.buyStockList(request, response);
}
List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");
List supplierList = (List) request.getAttribute("supplierList");

String supplierIds = cache.ProductLinePermissionCache.getProductLineSupplierIds(user);
supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;

int count;
BuyStockBean bean = null;

//审核权限
boolean shenhe = group.isFlag(58);
//查看全部进货单的权限
boolean viewAll = group.isFlag(55);
boolean transform = group.isFlag(112);
boolean bianji = group.isFlag(168);
boolean search = group.isFlag(195);

String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String startTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("startTime")));
String endTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("endTime")));
String supplierId = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("supplierId")));
String productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
String arrivalStartTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("arrivalStartTime")));
String arrivalEndTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("arrivalEndTime")));
productName = Encoder.decrypt(productName);//解码为中文
if(productName==null){//解码失败,表示已经为中文,则返回默认
	productName =StringUtil.dealParam(request.getParameter("productName"));//名称
}
if (productName==null) productName="";
String[] statuss = request.getParameterValues("status");
String status = "";
if(statuss!=null){
	for(int i=0;i<statuss.length;i++){
		status = status + statuss[i]+",";
	}
}
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
List productLineList = (List) request.getAttribute("productLineList");//产品线信息
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
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
function clearAll(name) {
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name)){
	    el[i].checked = false;
	}
    }
} 
function searchCheck(){
	var startTime = document.searchForm.startTime.value;
	var endTime = document.searchForm.endTime.value;
	var arrivalStartTime = document.searchForm.arrivalStartTime.value;
	var arrivalEndTime = document.searchForm.arrivalEndTime.value;
	if((startTime == '' && endTime != '')||(startTime != '' && endTime == '')){
		alert('生成时间段必须填写完整');
		return false;
	}
	
	if((arrivalStartTime == '' && arrivalEndTime != '')||(arrivalStartTime != '' && arrivalEndTime == '')){
		alert('预计到货日期必须填写完整');
		return false;
	}

var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;	
	if(startTime != '' && endTime != ''){
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
	
	if(arrivalStartTime != '' && arrivalEndTime != ''){
		if(!re.test(arrivalStartTime) || !re.test(arrivalEndTime)){
			alert('日期格式不合法');
         	return false;
		}
		if(arrivalStartTime > arrivalEndTime) {
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
</script>
<p align="center">预计到货表操作记录</p>
<form method="post" action="<%=request.getContextPath() %>/admin/stock2/transformBuyOrderList.jsp">
<%if(transform){ %>
<input type="submit" value="转换新的预计到货表"/>
<%} %>
</form>
<%if(search){%>
<fieldset>
   <legend>查询条件</legend>
   <form name="searchForm" action="<%=request.getContextPath()%>/admin/searchBuyStockList.do" method="post" onSubmit="return searchCheck();">
   预计到货表编号：<input type="text" size="14" name="code" value="<%=code %>"/>&nbsp;&nbsp;
  生成时间段：<input type="text" size="14" name="startTime" value="<%=startTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
  至<input type="text" size="14" name="endTime" value="<%=endTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/><br/>
  预计到货表状态：
   <input type="checkbox" name="status" value="0">未处理&nbsp;&nbsp;
   <input type="checkbox" name="status" value="1">处理中&nbsp;&nbsp;
   <input type="checkbox" name="status" value="2">已确认&nbsp;&nbsp;
   <input type="checkbox" name="status" value="6">采购已完成&nbsp;&nbsp;<br/>
   <script>checkboxChecked(document.getElementsByName('status'),'<%=status%>');</script>
  预计到货日期：<input type="text" size="14" name="arrivalStartTime" value="<%=arrivalStartTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
  至<input type="text" size="14" name="arrivalEndTime" value="<%=arrivalEndTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>&nbsp;&nbsp;
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
   		 </select><br/>
  代理商：
  <div id="auto" style="position:absolute; left:100px; top:190px;"></div>
  <input type="text" name="suppliertext" onClick="select()" id="word" style="width:100px;height:20px;font-size:10pt;">
  <input type="hidden" name="condition" id="condition" value="status = 1 and id in (<%=supplierIds %>)">
  <span style="width:18px;border:0px solid red;margin-left:-8px;margin-bottom:-6px;">
  		<select name="supplierId" id="supplierId" onchange="javascript:select();document.all.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text" style="margin-left:-100px;width:118px;">
			<option value="0"></option>
			<%
				Iterator iter = supplierList.listIterator();
				while(iter.hasNext()){
					voSelect select = (voSelect)iter.next();
			%>
  			<option value="<%= select.getId() %>"><%= select.getName() %></option>
			<%} %>
		</select>
  </span>&nbsp;&nbsp;
  <script>document.all.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text;</script>
  <script>selectOption(document.getElementById('supplierId'), '<%=supplierId%>');</script>
产品名称：<input type="text" name="productName" value="<%=productName %>"/>&nbsp;&nbsp;
  <input type="submit" value="查询"/>
   </form>
</fieldset>
<%} %>
<form method="post" action="<%=request.getContextPath() %>/admin/stock2/collectBuyStock.jsp">
<table width="100%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr>
  <!-- <td>选择</td> -->
  <td>序号</td>
  <td>编号</td>
  <td>添加时间</td>
  <td>产品线</td>
  <td>代理商</td>
  <td>地区</td>
  <td>状态</td>
  <td>生成人/确认人</td>
  <td>操作</td>
</tr>
<%
if(list!=null){
count = list.size();
for(int i = 0; i < count; i ++){
	bean = (BuyStockBean) list.get(i);
%>
<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
  <%-- <td><input type="checkbox" name="ids" value="<%=bean.getId()%>" <%if(bean.getStatus() == BuyStockBean.STATUS0){%>id="s0"<%}%><%if(bean.getStatus() == BuyStockBean.STATUS1){%>id="s1"<%}%><%if(bean.getStatus() == BuyStockBean.STATUS2){%>id="s2"<%}%><%if(bean.getStatus() == BuyStockBean.STATUS3){%>id="s3"<%}%><%if(bean.getStatus() == BuyStockBean.STATUS4){%>id="s4"<%}%> /></td> --%>
  <td><%=(i + 1)%></td>
  <td><a href="<%=request.getContextPath()%>/admin/stock2/buyStock.jsp?stockId=<%=bean.getId()%>"><%=bean.getCode()%></a></td>
  <td><%=bean.getCreateDatetime().substring(11, 16)%></td>
  <td><%=bean.getProductType()%></td>
  <td><%=bean.getProxyName()==null?"无":bean.getProxyName()%></td>
  <td><%=ProductStockBean.areaMap.get(bean.getArea())%></td>
  <td>
<%if(bean.getStatus() == BuyStockBean.STATUS0 || bean.getStatus() == BuyStockBean.STATUS1){%>
	<font color="red"><%=bean.getStatusName()%></font>
<%} else {%><%=bean.getStatusName()%><%}%>
  </td>
  <td><%if(bean.getCreatUser()!=null){%><%=bean.getCreatUser().getUsername() %><%}%>/<%if(bean.getAuditingUser()!=null){%><%=bean.getAuditingUser().getUsername()%><%}%></td>
  <td>
<%if(bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS5 || bean.getStatus() == BuyStockBean.STATUS6){%>
	<a href="<%=request.getContextPath() %>/admin/stock2/buyStock.jsp?stockId=<%=bean.getId()%>">查看</a>&nbsp;|<font color="red">已转换成入库单(<%=bean.getTransformCount()%>)</font>
<%} else {%>
	<a href="<%=request.getContextPath() %>/admin/stock2/buyStock.jsp?stockId=<%=bean.getId()%>">编辑</a>
<%} %>
 <%if((bean.getStatus() == BuyStockBean.STATUS0 || bean.getProxyName()==null)&&(bianji||bean.getCreateUserId()==user.getId())){%>
	|<a href="<%=request.getContextPath()%>/admin/stock2/deleteBuyStock.jsp?stockId=<%=bean.getId()%>" onclick="return confirm('确认删除？')">删除</a>
<%}%>
<%if(bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS5 || bean.getStatus() == BuyStockBean.STATUS6){%>
|<a href="<%=request.getContextPath() %>/admin/stock2/buyStockPrint.jsp?stockId=<%=bean.getId()%>" target="_blank" style="color: green;">打印</a>
|<%if(bean.getPrintCount()>0){ %><a href="<%=request.getContextPath() %>/admin/stock2/printLog.jsp?operId=<%=bean.getId()%>&type=<%= PrintLogBean.PRINT_LOG_TYPE_BUYSTOCK %>">打印<%= bean.getPrintCount() %>次<%}else{ %>打印次数<%} %></a>
<%} %>
  </td>
</tr>
<%
}}
%>
</table>
<!-- 
<p align="center">
<input type="button" name="B" onclick="javascript:checkAll('ids','s0');" value="全选未处理"/>
<input type="button" name="B" onclick="javascript:checkAll('ids','s1');" value="全选处理中"/>
<input type="button" name="B" onclick="javascript:checkAll('ids','s2');" value="全选已进货"/>
<input type="button" name="B" onclick="javascript:checkAll('ids','s3');" value="全选已审核"/>
<input type="button" name="B" onclick="javascript:checkAll('ids','s4');" value="全选审核未通过"/>
<input type="button" name="B" onclick="javascript:clearAll('ids');" value="全不选"/>
<input type="submit" name="B" value="汇总"/>
<%-- <input type="submit" name="B" value="汇总"/> --%></p>
 -->
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>