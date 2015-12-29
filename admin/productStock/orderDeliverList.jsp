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
<%@page import="java.text.DecimalFormat"%>
<head>
<title>订单配送管理</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE5 {color: #0000FF}
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
PagingBean paging = (PagingBean)request.getAttribute("paging");
String url = (String)request.getAttribute("url");
 String condition1 = StringUtil.convertNull(request.getParameter("condition1"));
if (request.getMethod().equalsIgnoreCase("get")){
	if(Encoder.decrypt(condition1)!=null){
		condition1=Encoder.decrypt(condition1);
	}
}
String select=StringUtil.convertNull(request.getParameter("select"));
if("".equals(select)){
	 select = StringUtil.convertNull((String)request.getAttribute("select"));
}
String select1=StringUtil.convertNull(request.getParameter("select1"));
if("".equals(select)){
	 select = StringUtil.convertNull((String)request.getAttribute("select1"));
}
Map statusMapAll = MailingBatchPackageBean.mailingStatusNameMap;
DbOperation dbOp = new DbOperation();
dbOp.init("adult_slave");
IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
DecimalFormat dcmFmt = new DecimalFormat("0.00");
//try{
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script type="text/javascript">
function date(){
	if(document.getElementById("select").value==3){
		SelectDate(document.getElementById("condition1"),'yyyy-MM-dd');
	}
}
function addSuccess(){
		window.open('productStock/mailingBatchStockIn.jsp','_blank')
}
function returnStock(){
	window.open('mailingBatch.do?method=mailingBalanceOrderReturn','_blank')
}
function distribute(){
	window.open('productStock/packageDeliverDistribute.jsp','_blank')
}
function balanceAcconunt(){
	window.open('mailingBatch.do?method=mailingBalanceAuditingList&balanceType=7','_blank')
}
function addFail(){
		alert('您无权进行此操作');
	    return false; 
}
function checksubmit(){
	var reg = /^[\u4e00-\u9fa5]+$/gi
    var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	var cond = document.getElementById("condition1").value;
	var sel = document.getElementById("select").value;
	var sel1 = document.getElementById("select1").value;
	if(sel==0&&sel1==0){
		alert('请输入查询条件');
			return false;
    }
	if(sel==1 && (cond.substring(0,2)!="sw" || cond.substring(0,2)!="SW") && cond.length!=14){
			alert('查询条件不正确');
 			return false;
	}
	if(sel==3 && (!r.test(cond) || (cond.length!=0 && cond.length!=10))){
		    alert('查询条件不正确');
    	    return false; 
    }
}
function onclick(){
	if(sel==3){
		SelectDate(this,'yyyy-MM-dd');
	}
}
function changePayType(bachId){
	var payType = document.getElementById(bachId).value;
	location = "<%=request.getContextPath()%>/admin/mailingBatch.do?method=changePayType&payType="+ payType + "&bachId=" + bachId +"&select=<%=select%>&select1=<%=select1%>" ;
	
}
</script>
<body bgcolor="#ffcc00">
<form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=changePayType" %> method="post" >
	<input type="hidden" id="hiddenBachId"  />
	<input type="hidden" id="hiddenPayType" />
</form>
<form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=orderDeliverList" %> method="post" onSubmit="return checksubmit();">
<table width="99%" border="0">
  <tr>
    <td><%if(group.isFlag(460)){ %><input type="button" value="新到包裹入库" onclick="addSuccess()">
    <%} %><%if(group.isFlag(469)){ %><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=notSignInPackageList' target="_blank">查看未签收包裹</a><%} %></td>
    <td>
    <%if(group.isFlag(461)){ %>
      <div align="right">
       <input name="condition1" id="condition1" type="text" width="200px" onClick="date()"
						onfocus="if(this.value=='输入关键字...'){this.value=''}"> <input
						name="condition" id="condition" type="hidden"
						<%if (condition1.equals("") || condition1.length() == 0) {%> value="输入关键字..."
						<%} else {%> value="<%=condition1%>" <%}%> />&nbsp;&nbsp;
        <select name="select" id="select" name="select">
        	    <option value=0>查询条件</option>
        	    <option value=1>发货波次号</option>
        	    <option value=2>订单编号</option>
        	    <option value=3>入库日期</option>
        	    <option value=4>投递员姓名</option>
        	    <option value=5>接货人帐号</option>
        	    <option value=6>发货仓库</option>
          </select>&nbsp;&nbsp;
        <input type="submit" name="Submit" value="查询">
      </div>
      <%} %>
      </td>
   </tr>
</table>
 <script type="text/javascript">  
     selectOption(document.getElementById("select") ,"<%=select%>");
 </script>
<hr/><%if(group.isFlag(461)){ %><select name="select1" id="select1">
        	    <option value=0>查询条件</option>
        	    <option value=1>未分配</option>
        	    <option value=2>投递中</option>
        	    <option value=3>已妥投</option>
        	    <option value=4>投递超时</option>
        	    <option value=5>返库中</option>
        	    <option value=7>结算中</option>
          </select>&nbsp;&nbsp;
        <input type="submit" name="Submit" value="筛选"><%} %>
        <%if(group.isFlag(462)){ %><input type="button" name="Submit2" value="投递任务分配" onClick="distribute()"><%} %>
        <%if(group.isFlag(463)){ %><input type="button" name="Submit3" value="结算申请" onClick="balanceAcconunt()"><%} %>
        <%if(group.isFlag(464)){ %><input type="button" name="Submit4" value="返库申请" onClick="returnStock()"><%} %>
        <br>
        <br>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">发货波次号</font></span></div></td>
    <td><div align="center"><font color="#00000">订单编号</font></div></td>
    <td><div align="center"><font color="#00000">入库日期</font></div></td>
    <td><div align="center"><font color="#00000">入库时间</font></div></td>
    <td><div align="center"><font color="#00000">投递员</font></div></td>
    <td><div align="center"><font color="#00000">入库接收人</font></div></td>
    <td><div align="center"><font color="#00000">发货仓库</font></div></td>
    <td><div align="center"><font color="#00000">订单金额</font></div></td>
    <td><div align="center"><font color="#00000">支付方式</font></div></td>
    <td><div align="center"><font color="#00000">配送状态</font></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">修改操作</font></span></div></td>
  </tr>
  <%if(mbList!=null){
		for (int i = 0; i < mbList.size(); i++) {
			String store=null;
			mbBean = (MailingBatchPackageBean) mbList.get(i);
			String query = "select a.store from mailing_batch a left join mailing_batch_parcel b on a.code=b.mailing_batch_code " +
		       "left join mailing_batch_package c on b.code=c.mailing_batch_parcel_code where c.order_code='"+mbBean.getOrderCode()+"';";
            ResultSet rs = service.getDbOp().executeQuery(query);	
            while (rs.next()) {
            	 store = rs.getString("a.store");
            }
	    %>
  <tr  bgcolor="#FFFFCC">
    <td><div align="center"><%=paging.getCountPerPage()*(paging.getCurrentPageIndex())+i+1%></div></td>
    <td><div align="center">
    	<%if(group.isFlag(430)){ %>
    		<a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId=<%=mbBean.getMailingBatchId()%>'><%=mbBean.getMailingBatchCode() %></a>
    	<%}else{ %><%=mbBean.getMailingBatchCode() %><%} %>
    	</div>
    </td>
    <td><div align="center"><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=mbBean.getOrderId()%>" target="_blank"><%=mbBean.getOrderCode()%></a></div></td>
    <td><div align="center"><%=mbBean.getStockInDatetime().substring(0,11)%></div></td>
    <td><div align="center"><%=mbBean.getStockInDatetime().substring(11,mbBean.getCreateDatetime().length()-2) %></div></td>
    <td><div align="center"><%if(mbBean.getPostStaffName()!=null&&mbBean.getPostStaffName().trim().isEmpty()||mbBean.getMailingStatus()==1){%>null<%}else{ %><%=mbBean.getPostStaffName()%><%} %></div></td>
    <td><div align="center"><%if(mbBean.getStockInAdminName()!=null&&mbBean.getStockInAdminName().trim().isEmpty()){%>null<%}else{ %><%=mbBean.getStockInAdminName()%><%} %></div></td>
    <td><div align="center"><%if(store!=null&&store.trim().isEmpty()){%>null<%}else{ %><%=store%><%} %></div></td>
    <td><div align="center"><%=dcmFmt.format(mbBean.getTotalPrice())%></div></td>
    <td><div align="center"><select id="<%=mbBean.getId() %>" name="payType" onchange="changePayType('<%=mbBean.getId() %>')"  
    							<%if(mbBean.getMailingStatus() == 5 || mbBean.getMailingStatus() == 6 ||mbBean.getMailingStatus() == 7 ||mbBean.getMailingStatus() == 8 ||
    									mbBean.getBalanceStatus() == 1 || mbBean.getBalanceStatus() == 2 ||mbBean.getReturnStatus() == 1 ||mbBean.getReturnStatus() == 2 ||mbBean.getReturnStatus() == 3  ){ %>
    							disabled="true" <%} %>>
        	    				<option value=0>支付方式</option>
        	    				<option value=1 <%if(mbBean.getPayType()==1){ %>selected="selected" <%} %>>现金支付</option>
        	    				<option value=2 <%if(mbBean.getPayType()==2){ %>selected="selected" <%} %>>pos机刷卡</option>
          					</select></div></td>
    <td><div align="center">
     <%
	Iterator statusIter = statusMapAll.entrySet().iterator();
	int j = 0;
	while (statusIter.hasNext()) {
		j++;
		Map.Entry entry = (Map.Entry) statusIter.next();
		if(StringUtil.StringToId(entry.getKey().toString())==mbBean.getMailingStatus()){
	%>
	<%if(mbBean.getMailingStatus()==1||mbBean.getMailingStatus()==2||mbBean.getMailingStatus()==4) {%>
    <span class="STYLE6"><%=entry.getValue() %></span><%} %>
    <%if(mbBean.getMailingStatus()==3||mbBean.getMailingStatus()==5||mbBean.getMailingStatus()==7) {%>
    <span class="STYLE7"><%=entry.getValue() %></span><%}}} %>
    </div></td>
    <td><div align="center"><%if(group.isFlag(465)){ %><%if(mbBean.getMailingStatus()==2) {%><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=modifyMailingPackageDeliverStauts&status=3&packageId=<%=mbBean.getId()%>'><span class="STYLE5">已妥投</span></a><%}%><%}%>                   
                            <%if(group.isFlag(466)){ %><%if(mbBean.getMailingStatus()==3) {%><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=modifyMailingPackageDeliverStauts&status=2&packageId=<%=mbBean.getId()%>'><span class="STYLE5">投递中</span></a><%}%><%}%>
                            <%if(group.isFlag(465)){ %><%if(mbBean.getMailingStatus()==4) {%><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=modifyMailingPackageDeliverStauts&status=3&packageId=<%=mbBean.getId()%>'><span class="STYLE5">已妥投</span></a><%}%><%}%>
                            <%if(group.isFlag(467)){ %><%if(mbBean.getMailingStatus()==5) {%><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=modifyMailingPackageDeliverStauts&status=4&packageId=<%=mbBean.getId()%>'><span class="STYLE5">取消反库</span></a><%}%><%}%>
                            </div></td>
  </tr>
  <%} }%>
</table>
 <script type="text/javascript">  
     selectOption(document.getElementById("select1") ,"<%=select1%>");
 </script>
 
</form>

<%if (paging!=null){
	request.getSession().setAttribute("paging",paging);
%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%//} 
//} //catch(Exception e){e.printStackTrace();} finally{
	service.releaseAll();
}%>
</body>
<script type="text/javascript">
		document.getElementById("condition1").value = document.getElementById("condition").value;
</script>
</html>