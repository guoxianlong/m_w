<%@ page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@ page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.util.Encoder"%>
<%@ page import ="adultadmin.util.db.DbOperation"%>
<%@ page import="adultadmin.service.infc.IStockService"%>
<%@ page import="adultadmin.service.ServiceFactory"%>
<%@ page import="adultadmin.service.infc.IBaseService"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head><title>订单反库管理</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE4 {color: #009933}
.STYLE6 {color: #FF0000}
.STYLE7 {color: #00CC00}

-->
</style>
</head>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List mbList = (List)request.getAttribute("mbList");
MailingBatchPackageBean mbBean = null;
Map deliverMapAll = voOrder.deliverMapAll;
PagingBean paging = (PagingBean) request.getAttribute("paging");
String url = (String)request.getAttribute("url");
 String condition1 = StringUtil.convertNull(request.getParameter("condition1"));
if (request.getMethod().equalsIgnoreCase("get")){
	if(Encoder.decrypt(condition1)!=null){
		condition1=Encoder.decrypt(condition1);
	}
}
String select=StringUtil.convertNull(request.getParameter("select"));
String returnStatus=StringUtil.convertNull(request.getParameter("returnStatus"));
Map statusMapAll = MailingBatchPackageBean.mailingStatusNameMap;
Map returnStatusMapAll = MailingBatchPackageBean.returnStatusNameMap;
DbOperation dbOp = new DbOperation();
dbOp.init("adult_slave");
IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
String store=null;
String deliver=null;
String creater=null;
String carrier=null;
String transitAdminName=null;
try{
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script type="text/javascript">
function date(){
	if(document.getElementById("select").value==5){
		SelectDate(document.getElementById("condition1"),'yyyy-MM-dd');
	}
}
function addSuccess(){
		window.open('productStock/mailingBatchStockIn.jsp','_blank')
}
function addFail(){
		alert('您无权进行此操作');
	    return false; 
}
function checksubmit(){
	var cond = document.getElementById("condition1").value;
	var sel = document.getElementById("select").value;
	var returnStatus = document.getElementById("returnStatus").value;
	if(sel==0 && returnStatus==-1){
		alert('请输入查询条件');
			return false;
    }
}
function onclick(){
	if(sel==5){
		SelectDate(this,'yyyy-MM-dd');
	}
}
</script>
<body bgcolor="#ffcc00">
<form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=mailingBalanceOrderReturn" %> method="post" onSubmit="return checksubmit();">
  <table width="99%" border="0">
  <tr>
    <td>&nbsp;</td>
    <td>
    <%if(group.isFlag(471)){ %>
      <div align="right">
       <input name="condition1" id="condition1" type="text" width="200px" onClick="date()"
						onfocus="if(this.value=='输入关键字...'){this.value=''}"> <input
						name="condition" id="condition" type="hidden"
						<%if (condition1.equals("") || condition1.length() == 0) {%> value="输入关键字..."
						<%} else {%> value="<%=condition1%>" <%}%> />&nbsp;&nbsp;
        <select name="select" id="select">
        	    <option value=0>查询条件</option>
        	    <option value=1>发货波次号</option>
        	    <option value=2>订单编号</option>
        	    <option value=3>配送渠道</option>
        	    <option value=4>发货仓库</option>
        	    <option value=5>创建日期</option>
          </select>&nbsp;&nbsp;
        <input type="submit" name="Submit" value="查询">
      </div>
      <%} %>
      </td>
   </tr>
</table>
 <script type="text/javascript">  
     selectOption(document.getElementById("select") ,"<%=select%>");
 </script>&nbsp;&nbsp;
 <hr/>
 <select name="returnStatus" id="returnStatus">
   <option value=-1>返库状态筛选</option>
   <option value=0>未返库</option>
   <option value=1>返库中</option>
   <option value=2>已返库</option>
 </select>
  <script type="text/javascript">  
     selectOption(document.getElementById("returnStatus") ,"<%=returnStatus%>");
 </script>&nbsp;&nbsp;
 <div align="right">
          <table>
            <tr>
              <td> <%if(group.isFlag(473)){ %><div align="right"><a href="<%=request.getContextPath()%>/admin/mailingBatch.do?method=confirmMailingBalanceOrderReturn">订单包裹反库确认</a><%} %></div></td>
    </tr>
          </table>
  </div>
  </form>
  <form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=mailingBalanceOrderReturnOperate" %> method="post">
        <table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">发货波次号</font></span></div></td>
    <td><div align="center"><font color="#00000">订单编号</font></div></td>
    <td><div align="center"><font color="#00000">返库状态</font></div></td>
    <td><div align="center"><font color="#00000">创建日期</font></div></td>
    <td><div align="center"><font color="#00000">创建时间</font></div></td>
    <td><div align="center"><font color="#00000">配送渠道</font></div></td>
    <td><div align="center"><font color="#00000">承运商</font></div></td>
    <td><div align="center"><font color="#00000">创建人</font></div></td>
    <td><div align="center"><font color="#00000">接收人</font></div></td>
    <td><div align="center"><font color="#00000">发货仓库</font></div></td>
  </tr>
  <%if(mbList!=null){
		for (int i = 0; i < mbList.size(); i++) {
			mbBean = (MailingBatchPackageBean) mbList.get(i);
			String query = "select a.transit_admin_name,a.store,a.deliver,a.create_admin_name,a.carrier from mailing_batch a left join mailing_batch_parcel b on a.code=b.mailing_batch_code " +
		       "left join mailing_batch_package c on b.code=c.mailing_batch_parcel_code where c.order_code='"+mbBean.getOrderCode()+"';";
            ResultSet rs = service.getDbOp().executeQuery(query);	
            while (rs.next()) {
            	 store = rs.getString("a.store");
            	 deliver = rs.getString("a.deliver");
            	 creater = rs.getString("a.create_admin_name");
            	 carrier = rs.getString("a.carrier");
            	 transitAdminName =rs.getString("a.transit_admin_name");
            }
	    %>
  <tr  bgcolor="#FFFFCC">
    <td><div align="center"><%=paging.getCountPerPage()*(paging.getCurrentPageIndex())+i+1%><%if(mbBean.getReturnStatus()==0){ %><input name="checkbox" type="checkbox"  name="checkbox" value="<%=mbBean.getId()%>"><%} %></div></td>
    <td><div align="center">
    	<%if(group.isFlag(430)){ %>
    		<a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId=<%=mbBean.getMailingBatchId()%>'><%=mbBean.getMailingBatchCode() %></a>
    	<%}else{ %><%=mbBean.getMailingBatchCode() %><%} %>
    	</div>
    </td>
    <td><div align="center"><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=mbBean.getOrderId()%>" target="_blank"><%=mbBean.getOrderCode()%></a></div></td>
     <td><div align="center">
     <%
	Iterator statusIter1 = returnStatusMapAll.entrySet().iterator();
	while (statusIter1.hasNext()) {
		Map.Entry entry = (Map.Entry) statusIter1.next();
		if(StringUtil.StringToId(entry.getKey().toString())==mbBean.getReturnStatus()){
	%>
	<%if(mbBean.getReturnStatus()==0){ %>
    <span class="STYLE6"><%=entry.getValue() %></span><%}%>
    <%if(mbBean.getReturnStatus()==1||mbBean.getReturnStatus()==2){ %>
   <span class="STYLE7"><%=entry.getValue() %></span><%}%>
    <%}} %>
    </div></td>
    <td><div align="center"><%=mbBean.getCreateDatetime().substring(0,11)%></div></td>
    <td><div align="center"><%=mbBean.getCreateDatetime().substring(11,mbBean.getCreateDatetime().length()-2) %></div></td>
    <td><div align="center"> <%
	Iterator deliverIter = deliverMapAll.entrySet().iterator();
	int j = 0;
	while (deliverIter.hasNext()) {
		j++;
		Map.Entry entry = (Map.Entry) deliverIter.next();
		if(StringUtil.StringToId(entry.getKey().toString())==mbBean.getDeliver()){
	%>
    <%=entry.getValue() %><%}} %></div></td>
    <td><div align="center"><%if(carrier!=null&&carrier.trim().isEmpty()){%>null<%}else{ %><%=carrier%><%} %></div></td>
    <td><div align="center"><%if(creater!=null&&creater.trim().isEmpty()){%>null<%}else{ %><%=creater%><%} %></div></td>
    <td><div align="center"><%if(transitAdminName!=null&&transitAdminName.trim().isEmpty()){%>null<%}else{ %><%=transitAdminName%><%} %></div></td>
    <td><div align="center"><%if(store!=null&&store.trim().isEmpty()){%>null<%}else{ %><%=store%><%} %></div></td>
  </tr>
  <%} }%>
</table>
<br>
 <%if(group.isFlag(472)){ %>
<input type="submit" value="对当页选中订单申请返库">
<%} %>
</form>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} } catch(Exception e){e.printStackTrace();} finally{
	service.releaseAll();
}%>
</body>
<script type="text/javascript">
		document.getElementById("condition1").value = document.getElementById("condition").value;
</script>
</html>