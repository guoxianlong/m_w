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
<head>
<title>订单配送管理</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE4 {color: #009933}
-->
</style>
</head>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List mbList = (List)request.getAttribute("mbList");
MailingBatchPackageBean mbBean = null;
Map deliverMapAll = voOrder.deliverMapAll;
String condition1 = StringUtil.convertNull(request.getParameter("condition1"));
String select=StringUtil.convertNull(request.getParameter("select"));
Map statusMapAll = MailingBatchPackageBean.mailingStatusNameMap;
DbOperation dbOp = new DbOperation();
dbOp.init("adult_slave");
IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
String store=null;
String deliver=null;
String creater=null;
String carrier=null;
String transitAdminName=null;
response.setContentType("application/vnd.ms-excel");
String filename = new String("未签收包裹列表".getBytes("GB2312"),"ISO8859-1");
response.setHeader("Content-disposition","attachment; filename=\"" + filename + ".xls\"");
try{
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<body bgcolor="#ffcc00">
 <table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">发货波次号</font></span></div></td>
    <td><div align="center"><font color="#00000">订单编号</font></div></td>
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
			String query = "select a.store,a.transit_admin_name,a.deliver,a.create_admin_name,a.carrier from mailing_batch a left join mailing_batch_parcel b on a.code=b.mailing_batch_code " +
		       "left join mailing_batch_package c on b.code=c.mailing_batch_parcel_code where c.order_code='"+mbBean.getOrderCode()+"';";
            ResultSet rs = service.getDbOp().executeQuery(query);	
            while (rs.next()) {
            	 store = rs.getString("a.store");
            	 deliver = rs.getString("a.deliver");
            	 creater = rs.getString("a.create_admin_name");
            	 carrier = rs.getString("a.carrier");
            	 transitAdminName =rs.getString("a.transit_admin_name");
            }
	    %><%if(voOrder.deliverMapAll.containsKey(mbBean.getDeliver()+"")&&mbBean.getDeliver()!=10&&mbBean.getDeliver()!=12){ %>
  <tr  bgcolor="#FFFFCC">
    <td><div align="center"><%=i+1%></div></td>
    <td><div align="center">
    		<%=mbBean.getMailingBatchCode() %>
    	</div>
    </td>
    <td><div align="center"><%=mbBean.getOrderCode()%></div></td>
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
  <%}} }%>
</table>
<% } catch(Exception e){e.printStackTrace();} finally{
	service.releaseAll();
}%>
</body>

</html>