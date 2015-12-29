<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@ page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.util.PageUtil"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="cache.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="mmb.stock.cargo.CargoDeptAreaService"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>

<html>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List orderStockList=(List)request.getAttribute("orderStockList");
List auditPackageList=(List)request.getAttribute("auditPackageList");
List userOrderList=(List)request.getAttribute("userOrderList");
String batchId=(String)request.getParameter("batchId");
String from=(String)request.getParameter("from");
String count=(String)request.getAttribute("count");
String count1=(String)request.getAttribute("count1");
String count2=(String)request.getAttribute("count2");
DecimalFormat dcmFmt = new DecimalFormat("0.00");
PagingBean paging = (PagingBean) request.getAttribute("paging");
voUser adminUser = (voUser)session.getAttribute("userView");
String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
List areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
String startTime = StringUtil.convertNull(request.getParameter("startTime"));
String endTime = StringUtil.convertNull(request.getParameter("endTime"));
String storage = (String)request.getParameter("storage");
if(!catalogIds2.equals("")){
	String[] splits = catalogIds2.split(",");
	for(int i=0;i<splits.length;i++){
		voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
		if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
			catalogIds1 = catalog.getId() + "," + catalogIds1;
		}
	}
	if(catalogIds1.endsWith(",")){
		catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
	}
}
int st = StringUtil.toInt(storage);
String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("storage", "storage", request, st, false,"");
%>
<head>
<title>已复核未出库订单查询</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script type="text/javascript">
		function checksubmit(){
			var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
			var startTime = document.getElementById("startTime").value;
			var endTime = document.getElementById("endTime").value;
			var nDay_ms = 24*60*60*1000;
			var reg = new RegExp("-","g");
			var startDay = new Date(startTime.replace(reg,'/'));
			var endDay = new Date(endTime.replace(reg,'/'));
			var nDifTime = endDay.getTime()- startDay.getTime();
			if(nDifTime < 0){
				alert("起始日期不能大于结束日期！");
		    	return false;
			}
		    var nDifDay=Math.floor(nDifTime/nDay_ms);
		    if(nDifDay > 7){
		    	alert("日期间隔不能大于7天！");
		    	return false;
		    }
			if(startTime.length!=0&&endTime.length==0 ){
				 alert("起始时间,请输入完整时间段")
				 return false;
			}
	        if(startTime.length==0&&endTime.length!=0 ){
	           	 alert("结束时间,请输入完整时间段")
				 return false;
			}
	        if(startTime.length!=0 && endTime.length!=0){
				 if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
				     alert("请正确填写起始时间格式")
					 return false;
				 } 
				
				 if((endTime.length!=0 && endTime.length!=10) || !r.test(endTime)){
					 alert("请正确填写截止时间格式")
					 return false;
				 } 
	        }
			return true;
	   }

<%if(request.getAttribute("tip")!=null){%>
alert("<%=request.getAttribute("tip")%>");
<%}%>
</script>
</head>
<body bgcolor="#FFCC00">
<fieldset>
 <form  method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=orderStockQueryList" onSubmit="return checksubmit();">
   <div align="center">
     <%= wareAreaLable%>
     &nbsp;&nbsp;&nbsp;
    <font size=3px>复核时间：</font> <input type=text name="startTime" style="height:22px;font-size:18px;" size="10"
				    value="<%=startTime%>" onClick="SelectDate(this,'yyyy-MM-dd');"/>
     <font size="4.5">至</font>
     <input type=text name="endTime" size="10" style="height:22px;font-size:18px;" value="<%=endTime%>" 
				    onclick="SelectDate(this,'yyyy-MM-dd');"/>&nbsp;&nbsp;
     <input type="submit" style="height:22px;font-size:18px;" name="button" id="button" value="查询" />
   </div>
</form>
</fieldset>
<form  method="post"  name="sortingActionForm" id="sortingActionForm" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=orderStockQueryList">
<input type="hidden" name="flag" value="1">
<input type="hidden" name="showDivNum" value="" id="showDivNum">
<%if(!"1".equals(from)){ %>
<input type="hidden" name="pageIndex" value="<%=paging.getCurrentPageIndex() %>" />
<%} %>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
	  	<td><div align="center">序号</div></td>
	  	<td><div align="center">快递公司</div></td>
	  	<td><div align="center">订单号</div></td>
	  	<td><div align="center">出库单号</div></td>
	  	<td><div align="center">包裹单号</div></td>
	  	<td><div align="center">重量</div></td>
	  	<td><div align="center">代收金额</div></td>
	  	<td><div align="center">复核时间</div></td>
	  	<td><div align="center">复核人</div></td>
  	</tr>
  <%
  if(orderStockList!=null){
  for(int i=0;i<orderStockList.size();i++){ %>
  	<%
  		OrderStockBean osBean=(OrderStockBean)orderStockList.get(i); 
  		AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
  		voOrder orderBean=(voOrder)userOrderList.get(i);
  	%>
   <tr bgcolor="#EEE9D9">
	  	<td><div align="center"><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %>&nbsp;</div></td>
	  	<td><div align="center"><%=voOrder.deliverMapAll.get(osBean.getDeliver()+"")%>&nbsp;</div></td>
	  	<td><div align="center"><%=StringUtil.convertNull(osBean.getOrderCode())%>&nbsp;</div></td>
	  	<td><div align="center"><%=StringUtil.convertNull(osBean.getCode())%>&nbsp;</div></td>
	  	<td><div align="center"><%=StringUtil.convertNull(apBean.getPackageCode())%>&nbsp;</div></td>
	  	<td><div align="center"><%=apBean.getWeight()/1000%>KG&nbsp;</div></td>
	  	<td><div align="center"><%=orderBean.getDprice() %>元&nbsp;</div></td>
	  	<td><div align="center"><%=StringUtil.convertNull(StringUtil.cutString(apBean.getCheckDatetime(),19))%>&nbsp;</div></td>
	  	<td><div align="center"><%=StringUtil.convertNull(apBean.getCheckUserName())%>&nbsp;</div></td>
  	</tr>
  <%}}%>
</table>
</form>
<script>
function load(){
	 selectOption(document.getElementById("storage") ,"<%=storage%>");
	 selectOption(document.getElementById("endTime") ,"<%=endTime%>");
	 selectOption(document.getElementById("startTime") ,"<%=startTime%>");
}
load();
</script>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
</body>
</html>