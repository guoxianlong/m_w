<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %><%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.order.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部

voOrder vo = null;
	String now = DateUtil.getNowDateStr();
	
	List dealGroupList= (List)request.getAttribute("dealGroupList");
	List backGroupList= (List)request.getAttribute("backGroupList");
%>
<html>
<title>买卖宝后台</title>
<script type="text/javascript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/My97DatePicker/WdatePicker.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script>
function del()
{
	if(confirm('确认要标记选中的订单为已处理吗?')) {
		document.orderForm.action="morder.do";
		return document.orderForm.submit();
	}
}
function SelectDate(obj,strFormat)
{
    var date = new Date();
    var by = date.getFullYear()-1;  //最小值 → 50 年前
    var ey = date.getFullYear();  //最大值 → 50 年后
    //cal = new Calendar(by, ey,1,strFormat);    //初始化英文版，0 为中文版
    cal = (cal==null) ? new Calendar(by, ey, 0) : cal;    //不用每次都初始化 2006-12-03 修正
    cal.dateFormatStyle = strFormat;
    cal.show(obj);
}
function collect()
{
		document.orderForm.action="collect3.do";
		return document.orderForm.submit();
}
  
  function   getTblText(objTbl)   
  {   
  if(!objTbl)   return   "";   
  if(objTbl.tagName   !=   "TABLE")   return   "";   
  var   returnText   =   "";   
  for(var   r=0;   r<objTbl.rows.length;   r++)   
  {   
  for(var   c=0;   c<objTbl.rows[r].cells.length;   c++)
  {   
  returnText   +=   objTbl.rows[r].cells[c].innerText   +   "\t";   
  }   
  returnText   +=   "\n";   
  }   
  return   returnText;   
  }  
  
  window.onload =function(){
  	  document.getElementById('buttonSearchOrder').disabled=false;
  }
  
  function checkForm(){
	  var phone = document.getElementById("phone");
	  var packageNum = document.getElementById("packageNum");
	   
	  document.getElementById("action").value = 'search';
	  
	  var startDate = document.getElementById("startDate");
	  var endDate = document.getElementById("endDate");
	  var startDateHour = document.getElementById("startDateHour");
	  var endDateHour = document.getElementById("endDateHour");
  	  var startTime = startDate.value+startDateHour.value;
	  var endTime = endDate.value+endDateHour.value;
	  if(startTime>endTime){
	  	alert("提示：订单创建时间的起始时间不能大于截止时间！");
	  	return false;
	  }
	  
	  // 检验订单创建时间
	  if(startDate.value.length == 0 || endDate.value.length == 0){
		  document.getElementById("dateError").innerHTML = "<font color='red'>请选择订单创建时间后，再查询</font>";
		  if(startDate.value.length == 0 ){
			startDate.focus();
		  }	else{
			endDate.focus();	  
		  }
		  return false;
	  }else{
		  document.getElementById("dateError").innerHTML = "";		  
	  }
	  var reg=/^([1-2]\d{3})[\/|\-](0?[1-9]|10|11|12)[\/|\-]([1-2]?[0-9]|0[1-9]|30|31)$/ig;
	  if(startDate.value!="" && startDate.value.length>10 && !reg.test(startDate.value)){  
	     document.getElementById("dateError").innerHTML = "<font color='red'>开始日期格式错误，请重新选择</font>";     
	     return false;  
	  }
	  if(endDate.value!="" && endDate.value.length>10 && !reg.test(endDate.value)){  
	     document.getElementById("dateError").innerHTML = "<font color='red'>截止日期格式错误，请重新选择</font>";     
	     return false; 
	  }  
	  if(phone.value.length>15){
			alert('只允许输入一个电话号码！');
			phone.focus();
			return false;
	 }
	 if(packageNum.value.indexOf(",")>0){
		 	alert('只允许输入一个包裹单号！');
		 	packageNum.focus();
			return false;
	 }
	 return true;
  }
  
  function isTelephone(Telephone) {
	  var regexpMobile = /((\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)/;
	  return regexpMobile.test(Telephone);
  }
  function checkForm2(){
	  // 折扣 - 开始
	  var startDiscount = document.getElementById("startDiscount");
	  // 折扣 - 结束
	  var endDiscount = document.getElementById("endDiscount");
	  
	  document.getElementById("action").value = 'exportStock';
	  
	  // 如果 折扣项非空
	  if(startDiscount.value.length > 0 || endDiscount.value.length > 0){
		  var st = /^\d+(\.\d{0,2})?$/.test(startDiscount.value);
		  var et = /^\d+(\.\d{0,2})?$/.test(endDiscount.value);
		  if(st == false || et == false){ 
			document.getElementById("discountError").innerHTML = "<font color='red'>请填写整数或者带有二位小数的实数</font>";
			if(st == false){
				startDiscount.focus();
			}else{
				endDiscount.focus();
			}
			return  false;
		  }else{
			document.getElementById("discountError").innerHTML = "";
		  }
		  // 检验订单创建时间
		  var startDate = document.getElementById("startDate");
		  var endDate = document.getElementById("endDate");
		  if(startDate.value.length == 0 || endDate.value.length == 0){
			  document.getElementById("dateError").innerHTML = "<font color='red'>请选择订单创建时间后，再查询</font>";
			  if(startDate.value.length == 0 ){
				startDate.focus();
			  }	else{
				endDate.focus();	  
			  }
			  return false;
		  }else{
			  document.getElementById("dateError").innerHTML = "";		  
		  }
	  }
	  // 校验价格区间
	  if(checkPrice() == false){
	  	return false;
	  }
	  document.searchOrderForm.submit();
	  return true;
  }
  
	function changeValue(i){
		if(checkForm()){
			if(i == 1){	//为1，则“导出列表”
				document.searchOrderForm.isExportList.value = 1;
				//document.searchOrderForm.target="_blank";
			}else{	//其它，则“查询订单”
				document.searchOrderForm.isExportList.value = 0;
				document.searchOrderForm.target="";
			}
			document.searchOrderForm.submit();
		}else{
			document.getElementById('buttonSearchOrder').disabled=false;
		}
		return true;
	}
	function changecreatetime(day){
		var today = new Date();
		var startDate = document.getElementById("startDate");
		var endDate = document.getElementById("endDate");
		endDate.value=today.format('yyyy-MM-dd');
		today.setTime(today.getTime()-24*60*60*1000*(day-1));
		startDate.value=today.format('yyyy-MM-dd');
	}
	function exportText(){
		document.textForm.submit();
	}
	$(document).ready(function(){	// 文档加载
		$('input:radio[name="isOldUser"]').dblclick(function(){
			$('input:radio[name="isOldUser"]').attr('checked',false);
		});
		$('input:radio[name="isVistor"]').dblclick(function(){
			$('input:radio[name="isVistor"]').attr('checked',false);
		});  
	});
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<%@include file="../header.jsp"%>
<table width="90%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<%
String stockDate = (String)request.getParameter("stockDate");
String createDatetime = (String)request.getParameter("createDatetime");
String code = (String)request.getParameter("code");
String orderStockCode = (String)request.getParameter("orderStockCode");
if(orderStockCode == null)orderStockCode="";
if(code == null)code="";
// 价格
String price = (String)request.getParameter("price");
if(price == null)price="";
//价格区间
String startPrice = (String)request.getParameter("startPrice");
if(startPrice == null)startPrice="";
String endPrice = (String)request.getParameter("endPrice");
if(endPrice == null)endPrice="";
//折扣
String startDiscount = request.getParameter("startDiscount");
if(startDiscount==null)startDiscount="";
String endDiscount = request.getParameter("endDiscount");
if(endDiscount==null)endDiscount="";

String name = (String)request.getParameter("name");
if(name == null)name="";
String phone = (String)request.getParameter("phone");
if(phone == null)phone="";else phone=phone.trim();
String product = (String)request.getParameter("product");
String linkId= StringUtil.convertNull(request.getParameter("linkId"));
String productCode = (String)request.getParameter("productCode");
if(product == null)product="";
if(productCode == null)productCode="";
String operator = (String)request.getParameter("operator");
if(operator == null)operator="";
String prepayDeliver = (String)request.getParameter("prepayDeliver");
if(prepayDeliver == null)prepayDeliver="";
String orderStatusStr = request.getParameter("orderStatus");
int orderStatus = StringUtil.StringToId(orderStatusStr);
String bankIdStr = request.getParameter("bankId");
int bankId = StringUtil.StringToId(bankIdStr);
String address = (String)request.getParameter("address");
if(address == null)address="";
String userId = (String)request.getParameter("userId");
if(userId == null)userId="";
String packageNum = (String)request.getParameter("packageNum");
if(packageNum == null)packageNum="";
String isOrderStr = request.getParameter("isOrder");
int isOrder = 0;
if(isOrderStr == null){
	isOrder = 2;
}
else {
    isOrder = StringUtil.StringToId(isOrderStr);
}
String buymodeStr = request.getParameter("buymode");
int buymode = -1;
if(buymodeStr == null){
	buymode = -1;
}
else {
	buymode = StringUtil.toInt(buymodeStr);
}
String flatStr = request.getParameter("flat");
int flat = 0;
if(flatStr == null){
	flat = 2;
}
else {
    flat = StringUtil.StringToId(flatStr);
}
int sortType = StringUtil.StringToId(request.getParameter("sortType"));
String startDate = StringUtil.convertNull(request.getParameter("startDate"));
String startDateHour = StringUtil.convertNull(request.getParameter("startDateHour"));
String endDateHour = StringUtil.convertNull(request.getParameter("endDateHour"));
if(StringUtil.isNull(startDate)){
	if(request.getAttribute("startDate")!=null){
		startDate=String.valueOf(request.getAttribute("startDate"));
	}else
		startDate = now;
}
String endDate = StringUtil.convertNull(request.getParameter("endDate"));
if(StringUtil.isNull(endDate)){
	endDate = now;
}
String stockoutStartDate = StringUtil.convertNull(request.getParameter("stockoutStartDate"));
String stockoutEndDate = StringUtil.convertNull(request.getParameter("stockoutEndDate"));
String status = StringUtil.convertNull(request.getParameter("status"));
if(status.equals("14")){
	status="6,11,12,13,14";
}
String[] orderTypes = request.getParameterValues("orderType");
String orderType = "";
if(orderTypes!=null){
	for(int i=0;i<orderTypes.length;i++){
		orderType = orderType + orderTypes[i]+",";
	}
}
String consigner = StringUtil.convertNull(request.getParameter("consigner"));
List orderList = (List)request.getAttribute("orderList");
int stockoutDeal = StringUtil.toInt(request.getParameter("stockoutDeal"));
String lastDealStartDate = StringUtil.convertNull(request.getParameter("lastDealStartDate"));
String lastDealEndDate = StringUtil.convertNull(request.getParameter("lastDealEndDate"));

int guanDongWai= StringUtil.parstBackMinus(request.getParameter("guanDongWai")); //老用户订单新增搜索条件
int isVistor= StringUtil.parstBackMinus(request.getParameter("isVistor"));
int isOldUser= StringUtil.parstBackMinus(request.getParameter("isOldUser"));
String checkOutStartDate = StringUtil.convertNull(request.getParameter("checkOutStartDate"));
String checkOutEndDate = StringUtil.convertNull(request.getParameter("checkOutEndDate"));
int consignerGroup= StringUtil.parstInt(request.getParameter("consignerGroup"));
int operatorGroup= StringUtil.parstInt(request.getParameter("operatorGroup"));
%>
<form name="searchOrderForm" method=post action="searchorder.do" onSubmit="return checkForm();">
<input type="hidden" name="action" id="action" value="search"/>
<input type="hidden" name="isExportList" value="0" />
<table><tr><td>
<% if(stockDate!= null){ %>
<input type="hidden" name="stockDate" value="<%=stockDate%>" />
<% } %>
</td><td></td></tr>

<tr><td>
订单编号：</td><td><input type=text name="code" size="20" value="<%=code%>" />
</td></tr>
<tr><td>
出库单编号：</td><td><input type=text name="orderStockCode" size="20" value="<%=orderStockCode%>" />
</td></tr>
<tr><td>
包裹单号码：</td><td><input type=text name="packageNum" id="packageNum" size="20" value="<%=packageNum%>">
</td></tr>
<tr><td>
用户名字：</td><td><input type=text name="name" size="20" value="<%=name%>">&nbsp;&nbsp;<input type="checkbox" id="nameAllLike" name="nameAllLike" value="1" />如勾选搜索时为左右模糊匹配，不勾选则是左模糊匹配
</td></tr>
<tr><td>
电话：</td><td><input type=text name="phone" id="phone" size="20" value="<%=phone%>">
</td></tr>
<tr><td>
产品名称：</td><td><input type=text name="product" size="20" value="<%=product%>"<%if(!group.isFlag(559)){ %> readonly="readonly"<%} %>>
</td></tr>
<tr><td>
产品编号：</td><td><input type=text name="productCode" size="20" value="<%=productCode%>" >
</td></tr>
<tr><td>
订单状态：</td><td>
<select name="orderStatus">
	<option value="-1" <%= (orderStatusStr != null && orderStatus==-1)?"selected=\"selected\"":"" %> ></option>
	<option value="3" <%= (orderStatusStr != null && orderStatus==3)?"selected=\"selected\"":"" %> >已到款</option>
	<option value="6" <%= (orderStatusStr != null && orderStatus==6)?"selected=\"selected\"":"" %> >已发货</option>
	<option value="11" <%= (orderStatusStr != null && orderStatus==11)?"selected=\"selected\"":"" %> >已退回</option>
	<option value="14" <%= (orderStatusStr != null && orderStatus==14)?"selected=\"selected\"":"" %> >已妥投</option>
</select>
</td></tr>
<tr><td>
地址：</td>
	<td>
		<input type=text name="address" size="20" value="<%=address%>">
		<input type="checkbox" name="guanDongWai" <%=guanDongWai==1?"checked='checked'":"" %> value="1">广东省外
	</td>
</tr>
<tr><td>
<tr><td>
订单发货时间</td><td>
<input type=text name="checkOutStartDate" size="20" value="<%=checkOutStartDate%>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})">
到<input type=text name="checkOutEndDate" size="20" value="<%=checkOutEndDate%>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})"> 
</td></tr>
<tr><td>
所属产品线：</td><td>
<%int i=0; %>
<logic:iterate id="item" name="orderTypeList" scope="request">
<%i++; %>
    <input type="checkbox" name="orderType" value="<bean:write name="item" property="typeId" />"><bean:write name="item" property="name" />&nbsp;&nbsp;
<%if(i == 6){ %><br/><%} %>
</logic:iterate>
<input name="orderType" value="9" type="checkbox"/>其他&nbsp;&nbsp;
<script>checkboxChecked(document.getElementsByName('orderType'),'<%=orderType%>');</script>
</td></tr>
<tr><td>
购买方式：</td><td><select name="buymode">
    <option value="2" <%= (buymode == 2)?"selected=\"selected\"":"" %> >银行汇款</option>
	<option value="0" <%= (buymode == 0)?"selected=\"selected\"":"" %> >货到付款</option>
	<option value="1" <%= (buymode == 1)?"selected=\"selected\"":"" %> >钱包支付</option>
	<option value="-1" <%= (buymode == -1)?"selected=\"selected\"":"" %> >不限</option>	
</select>
</td></tr>
<%
if(group.isFlag(8)||group.isFlag(567))
{
%>
<tr><td>
发货订单处理状态：</td><td><select name="stockoutDeal">
    <option value="-1" <%= (stockoutDeal == -1)?"selected=\"selected\"":"" %> ></option>
<%if(group.isFlag(130)||group.isFlag(567)){ %>
	<option value="0" <%= (stockoutDeal == 0)?"selected=\"selected\"":"" %> >发货未处理</option>
	<option value="3" <%= (stockoutDeal == 3)?"selected=\"selected\"":"" %> >发货空白</option>
	<option value="4" <%= (stockoutDeal == 4)?"selected=\"selected\"":"" %> >缺货未处理</option>
	<option value="5" <%= (stockoutDeal == 5)?"selected=\"selected\"":"" %> >缺货电话失败</option>
	<option value="6" <%= (stockoutDeal == 6)?"selected=\"selected\"":"" %> >缺货电话成功</option>
	<option value="7" <%= (stockoutDeal == 7)?"selected=\"selected\"":"" %> >缺货已补货</option>
<%} %>
	<option value="1" <%= (stockoutDeal == 1)?"selected=\"selected\"":"" %> >发货失败</option>
	<option value="2" <%= (stockoutDeal == 2)?"selected=\"selected\"":"" %> >发货成功</option>
</select>
</td></tr>
<%} %>
<tr><td>
订单创建时间：</td><td>
最近<select onchange="changecreatetime(this.value)"><option value="1">1天</option><option value="3">3天</option><option value="7">7天</option><option value="30">30天</option></select> - 
<input type=text name="startDate" id="startDate" size="<%=startDate.length()%>" value="<%=startDate%>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly">
<select name="startDateHour" id="startDateHour">
	<%for(int ii = -1; ii < 24; ii ++){ String iStr = ""+ii;if(ii == -1){iStr="";}if(ii < 10&&ii>=0){ iStr = "0"+ii;}%>
	<option value="<%=iStr%>"<%=startDateHour.equals(iStr)?"selected=\"selected\"":"" %> ><%=iStr%></option>
	<%}%>
</select>时 到 <input type=text name="endDate" id="endDate" size="<%=endDate.length()%>" value="<%=endDate%>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly">
<select name="endDateHour" id="endDateHour">
	<%for(int ii = -1; ii < 24; ii ++){ String iStr = ""+ii;if(ii == -1){iStr="";}if(ii < 10&&ii>=0){ iStr = "0"+ii;}%>
	<option value="<%=iStr%>" <%=endDateHour.equals(iStr)?"selected=\"selected\"":"" %> ><%=iStr%></option>
	<%}%>
</select>时
<span id="dateError"></span>
</td></tr>
<tr><td>
订单待出货时间：</td><td>
<input type=text name="stockoutStartDate" size="20" value="<%=stockoutStartDate%>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})">到<input type=text name="stockoutEndDate" size="20" value="<%=stockoutEndDate%>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})">
</td></tr>
</table>
<input type="submit" value="查询订单" id="buttonSearchOrder" disabled="disabled" onclick="this.disabled=true;return changeValue(0);">&nbsp;&nbsp;<%if(group.isFlag(401)){ %><input type="button" value="导出跟单表" onclick="return checkForm2();"/><%} %><br><br>
注意：<font color="red">如需汇总，请将进货订单与退货订单分别汇总。汇总本身不区分是进货还是退货订单</font>。
</form>
</td></tr>
</table>
</td></tr></table>
          <br><form method=post action="" name="orderForm" target="_blank">
          <input type=hidden name=flag value="<%=request.getParameter("flag")%>">
<%if(orderList!=null){%>
          <br/>搜索结果 <%=orderList.size()%> 条<%if(orderList.size() >= 20000){ %>，<font color="red">所查询订单数量太大，只显示前2万条，建议增加查询条件，缩小查询范围</font><%} %><br/>
<%}%>
          <table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
              <tr bgcolor="#4688D6">
			  <td width="20" align="center"><font color="#FFFFFF">选</font></td>
              <td width="100" align="center"><font color="#FFFFFF">订单号</font></td>
              <td width="60" align="center"><font color="#FFFFFF">姓名</font></td>
              <td width="100" align="center"><font color="#FFFFFF">电话</font></td>
              <td align="center"><font color="#FFFFFF">地址</font></td>
              <td width="150" align="center"><font color="#FFFFFF">产品名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">折扣前</font></td>
              <td width="60" align="center"><font color="#FFFFFF">折扣后</font></td>
			  <td align="center"><font color="#FFFFFF">配送费</font></td>
<%if(group.isFlag(66)){ %>
			  <td align="center"><font color="#FFFFFF">库存价格</font></td>
<%} %>
			  <td align="center"><font color="#FFFFFF">已到款</font></td>
			  <td align="center"><font color="#FFFFFF">类型</font></td>
              <td width="60" align="center"><font color="#FFFFFF">汇款方式</font></td>
              <td width="60" align="center"><font color="#FFFFFF">购买方式</font></td>
              <td width="60" align="center"><font color="#FFFFFF">包裹单号</font></td>
              <td width="60" align="center"><font color="#FFFFFF">包裹重量</font></td>
              <td width="50" align="center"><font color="#FFFFFF">订单状态</font></td>
              <td width="80" align="center"><font color="#FFFFFF">生成时间</font></td>
              <td width="40" align="center"><font color="#FFFFFF">友链id</font></td>
              <td width="40" align="center"><font color="#FFFFFF">处理人</font></td>
              <td width="40" align="center"><font color="#FFFFFF">发货人</font></td>
			  <td width="80" align="center"><font color="#FFFFFF">出货地点</font></td>
			  <td width="80" align="center"><font color="#FFFFFF">出货时间</font></td>
			  <td width="40" align="center"><font color="#FFFFFF">快递公司</font></td>
			  <td width="80" align="center"><font color="#FFFFFF">备注</font></td>
<%if(group.isFlag(36)||group.isFlag(704)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
              <td width="60" align="center"><font color="#FFFFFF">操作</font></td>
<%}%>
            </tr>           
            <%StringBuilder sb = new StringBuilder(1024);	//订单id字符串，用于导出excel %>
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" >
<%if((((voOrder)item).getStatus() != 0) || group.isFlag(29) || (!StringUtil.isNull(phone) && phone.trim().length() >= 7)){  //只有高级管理员才能看 未处理订单%>
<%
	vo = (voOrder) item;
	if(sb.length() > 0){
		sb.append(",");
	}
	sb.append(vo.getId());
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center' width="20"><input type=checkbox name="select" value="<bean:write name="item" property="id" />" ></td>
		<td align='center' width="100"><a href="order.do?id=<bean:write name="item" property="id" />" ><%if(vo.getFlat() == 1){%><font color="red"><%} else {%><font color="blue"><%}%><bean:write name="item" property="code" /></font></a></td>
		<td align=left width="60"><%=StringUtil.toSecurityHtml(vo.getName()) %></td>
		<td align=left width="100">
			<%
				if(vo.getPhone() != null){
					if(vo.getPhone().length()>=11){
			%>
				<%=vo.getPhone().substring(0, 3)+"****"+vo.getPhone().substring(7) %>
			<%			
					}else{
			%>
					<%=vo.getPhone() %>
			<%		
					}
				}
			%>
		</td>
		<td align=left><%= StringUtil.cutString(vo.getAddress(), 4) %><%if(vo.getAddress() != null && vo.getAddress().length() > 4){ %>...<%} %></td>
		<td align=left width="150"><bean:write name="item" property="products" /></td>
		<td align=left><bean:write name="item" property="price" format="0.00"/></td>
		<td align=left><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align="center">
<%
	if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_HUODAOFUKUAN && ((voOrder)item).getAreano() == Constants.AREA_NO_QITA && ((voOrder)item).getIsOlduser() == 0){ 
%>
			<bean:write name="item" property="prepayDeliver" format="0.00"/>
<%
	} else {
%>
			<bean:write name="item" property="postage" format="0.00"/>
<%}%>
		</td>
<%if(group.isFlag(66)){ %>
		<td align="center"><bean:write name="item" property="price3" format="0.000"/></td>
<%} %>
		<td align="center"><bean:write name="item" property="realPay" format="0.00"/></td>
		<td align=left><logic:equal name="item" property="agent" value="1"><logic:equal name="item" property="isOrder" value="0"><font color="red">代理进货</font></logic:equal><logic:equal name="item" property="isOrder" value="1"><font color="red">代理退货</font></logic:equal></logic:equal><logic:equal name="item" property="agent" value="0">普通</logic:equal></td>
		<td align=center width="60">
<%
if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_HUODAOFUKUAN){
%>
<%--
	if(((voOrder)item).getAreano() == Constants.AREA_NO_QITA && ((voOrder)item).getIsOlduser() == 0){
		switch(((voOrder)item).getRemitType()) {
		case 0:%>工商银行<%break;
		case 1:%>建设银行<%break;
		case 2:%>招商银行<%break;
		case 3:%>广发银行<%break;
		case 4:%>中国银行<%break;
		case 5:%>农业银行<%break;
		case 6:%>邮政储蓄<%break;
		}
	} else {
--%>无<%--
	}--%>
<%
} else if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_SHANGMENZIQU) {
%>无<%
} else {
	switch(((voOrder)item).getRemitType()) {
	case 0:%>工商银行<%break;
	case 1:%>建设银行<%break;
	case 2:%>招商银行<%break;
	case 3:%>广发银行<%break;
	case 4:%>中国银行<%break;
	case 5:%>农业银行<%break;
	case 6:%>邮政储蓄<%break;
	}
} %>
		</td>
		<td align=center width="60">
<%switch(((voOrder)item).getBuyMode()) {
case 0:%>货到付款<%break;
case 1:%>钱包支付<%break;
case 2:%>银行汇款<%break;
}%>
		</td>
		<td align=center width="60"><%=((voOrder)item).getAuditPakcageBean()!=null?((voOrder)item).getAuditPakcageBean().getPackageCode():"" %></td>
		<td align=center width="60"><%= ((voOrder)item).getAuditPakcageBean()!=null?((voOrder)item).getAuditPakcageBean().getWeight()/1000+"KG":"" %></td>
		<td align=center width="50">
<% if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_HUODAOFUKUAN){ %>
			<logic:equal name="item" property="statusName" value="已到款">待发货</logic:equal>
			<logic:notEqual name="item" property="statusName" value="已到款"><bean:write name="item" property="statusName" /></logic:notEqual>
<% } else { %>
			<bean:write name="item" property="statusName" />
<% } %>
		</td>
		<td align=left width="80"><bean:write name="item" property="createDatetime" /></td>
		<td align=left width="40"><bean:write name="item" property="fr" /></td>
		<td align=left width="40"><bean:write name="item" property="operator" /></td>
		<td align=left width="40"><bean:write name="item" property="consigner" /></td>
<%
		//出货记录
		//StockOperationBean oper = vo.getStockOper();
		OrderStockBean oper = vo.getOrderStock();
	    if(oper != null && oper.getStatus() != 3){
			String areaName = "<font color=blue>" + ProductStockBean.getAreaName(oper.getStockArea()) + "</font>";
%>
		<td width="80" align="center"><%=areaName%></td>
		<td width="80" align="center"><%=oper.getLastOperTime().substring(0, 16)%></td>
<%
		}
        else {
%>
        <td width="80" align="center">无</td>
        <td width="80" align="center">无</td>
<%
		}
%>
		<td width="40" align="center"><%=vo.deliverMapAll.get(String.valueOf(vo.getDeliver()))!=null?vo.deliverMapAll.get(String.valueOf(vo.getDeliver())):""%></td>
		<td width="80" align="center"><%= StringUtil.toSecurityHtml(vo.getRemark())+"  "+StringUtil.convertNull(vo.getStockoutRemark()) %></td>
	<%if(group.isFlag(36)||group.isFlag(704)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{
		voOrder order =(voOrder)item;
	%>
		<td align=left width="60">
		<%if(group.isFlag(704)){ %><a target="_blank" href="orderAdminLogs.do?orderId=<bean:write name="item" property="id" />"><font color=red>查看日志</font></a><%}%>
		<%
			if(group.isFlag(36)){
				if(order.getCode().startsWith("T")){
				 	if(group.isFlag(190)){
		%>
							<a href="forder.do?id=<bean:write name="item" property="id" />">修改</a>
		<%  	 	}
				}else { 
		%>
			 	 	<a href="forder.do?id=<bean:write name="item" property="id" />">修改</a>
		<%  	} %>
		<%
				if(order.getOrderStock() == null){ 
					if(order.getStockout() == 0 && order.getStatus() == 3){
		%>
						<a href="orderStock/addOrderStock.jsp?orderCode=<bean:write name="item" property="code" />"  onclick="return confirm('确认要出库？');"  target="_blank"><font color=red>申请出货</font></a>
		<%			}else{%>
						&nbsp;
		<%  		}
				}else{ 
					if(order.getOrderStock().getStatus()==OrderStockBean.STATUS3){ 
		%>
						库存<%=order.getOrderStock().getStatusName()%>
		<% 
					}else{ 
		%>
						<a href="orderStock/orderStock.jsp?id=<%=order.getOrderStock().getId()%>" target="_blank"><font color=blue>库存<%=order.getOrderStock().getStatusName()%></font></a>
		<%  		}
				}
			}
		%>
		</td>
	<%}%>
		</tr>
<%} //只有高级管理员才能看 未处理订单 %>
</logic:iterate> </logic:present> 
          </table>
<input type=checkbox onclick="setAllCheck(orderForm,'select',this.checked)">全选
<%if(group.isFlag(61)){ %>
            <input type=button onclick="return collect()" value=" 对选中的订单进行产品统计 ">
<%} %>
            <input type=button onclick="window.location.href='searchorder.do?sortType=1&createDatetime=<%= createDatetime %>'" value=" 按用户名 A-Z 显示订单 ">
            <input type="button" onclick="exportText();" value="导出列表"/>
          </form>
          <br>  
 <!-- 以下内容用于“导出列表（测试）” --> 
 <div  style="display:none;">
 <form  name="textForm" action="searchorder.do" method="post">
	<input name="isExportList" type="hidden" value="1"/>
	<input name="idStr" type="hidden" value="<%=sb.toString() %>"/>
 </form>
 </div>
 </body>
</html>