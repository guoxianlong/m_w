<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>发货批次管理</title>
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
MailingBatchBean mbBean = null;
Map deliverMapAll = voOrder.deliverMapAll;
PagingBean paging = (PagingBean) request.getAttribute("paging");
String url = (String)request.getAttribute("url");
String flag = (String)request.getAttribute("flag");
 String condition1 = StringUtil.convertNull(request.getParameter("condition1"));
if (request.getMethod().equalsIgnoreCase("get")){
	if(Encoder.decrypt(condition1)!=null){
		condition1=Encoder.decrypt(condition1);
	}
}
String select=StringUtil.convertNull(request.getParameter("select"));
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
		window.open('productStock/addMailingBatch.jsp','_blank')
}
function addFail(){
		alert('非物流部员工不能进行此操作！');
	    return false; 
}
function checksubmit(){
	var reg = /^[\u4e00-\u9fa5]+$/gi
    var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	var cond = trim(document.getElementById("condition1").value);
	var sel = document.getElementById("select").value;
	if(sel==0){
		alert('请输入查询条件');
			return false;
    }
	if(sel==1 && (cond.substring(0,2)!="sw" || cond.substring(0,2)!="SW") && cond.length!=14){
			alert('查询条件不正确');
 			return false;
	}
	if(sel==3 && !reg.test(cond)){
		    alert('查询条件不正确');
 	    	return false; 
	}
	if(sel==4 && !reg.test(cond)){
		    alert('查询条件不正确');
	    	return false; 
	}
	if(sel==5 && (!r.test(cond) || (cond.length!=0 && cond.length!=10))){
		    alert('查询条件不正确');
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
<form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=mailingBatchList" %> method="post" onsubmit="return checksubmit();">
<table width="99%" border="0">
  <tr>
    <td><%if(group.isFlag(428)){ %><input type="button" value="新增发货波次" <%if(flag.equals("true")){ %>onclick="addSuccess()"<%}else{ %>onclick="addFail()"<%} %>><%} %></td>
    <td>
    <%if(group.isFlag(429)){ %>
      <div align="right">
       <input name="condition1" id="condition1" type="text" width="200px" onclick="date()"
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
 </script>
<hr/><br></br>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">发货波次号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">创建日期</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">创建时间</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">配送渠道</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">承运商</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">创建人</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">交接人</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">发货仓库</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">发货状态</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">操作</font></span></div></td>
  </tr>
  <%if(mbList!=null){
		for (int i = 0; i < mbList.size(); i++) {
			mbBean = (MailingBatchBean) mbList.get(i);
	    %>
  <tr  bgcolor="#FFFFCC">
    <td><div align="center"><%=paging.getCountPerPage()*(paging.getCurrentPageIndex())+i+1%></div></td>
    <td><div align="center">
    	<%if(group.isFlag(430)){ %>
    		<a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId=<%=mbBean.getId()%>'><%=mbBean.getCode() %></a>
    	<%}else{ %><%=mbBean.getCode() %><%} %>
    	</div>
    </td>
    <td><div align="center"><%=mbBean.getCreateDatetime().substring(0,11)%></div></td>
    <td><div align="center"><%=mbBean.getCreateDatetime().substring(11,mbBean.getCreateDatetime().length()-2) %></div></td>
    <td><div align="center">
    <%
	Iterator deliverIter = deliverMapAll.entrySet().iterator();
	int j = 0;
	while (deliverIter.hasNext()) {
		j++;
		Map.Entry entry = (Map.Entry) deliverIter.next();
		if(StringUtil.StringToId(entry.getKey().toString())==mbBean.getDeliver()){
	%>
    <%=entry.getValue() %><%}} %>
    </div></td>
    <td><div align="center"><%if(mbBean.getCarrier()!=null&&mbBean.getCarrier().trim().isEmpty()){%>null<%}else{ %><%=mbBean.getCarrier() %><%} %></div></td>
    <td><div align="center"><%=mbBean.getCreateAdminName() %></div></td>
    <td><div align="center"><%=mbBean.getTransitAdminName() %></div></td>
    <td><div align="center"><%=mbBean.getStore() %></div></td>
    <td><div align="center"><%if(mbBean.getStatus()==0){ %><span class="STYLE3">待出库</span><%} else{%><span class="STYLE4">已出库</span><%} %></div></td>
    <td><div align="center">
    	<%if(group.isFlag(430)){ %>
    		<a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId=<%=mbBean.getId()%>'>查看</a>&nbsp;
    	<%} %>
    	<%if(group.isFlag(431)){ %>
    		<a href='productStock/mailingBatchPrintLine.jsp?code=<%=mbBean.getCode()%>' target="_blank">打印条码</a>&nbsp;
    	<%} %>
    	<%if(mbBean.getStatus()==0&&group.isFlag(432)){ %>
    		<a href="mailingBatch.do?method=deleteMailBatch&id=<%=mbBean.getId() %>&condition1=<%=Encoder.encrypt(condition1) %>&select=<%=select %>" onclick="return confirm('如果确认删除,请单击确定,反之请单击取消!');">删除</a>
    	<%} %></div>
    </td>
  </tr>
  <%} }%>
</table>
</form>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</body>
<script type="text/javascript">
		document.getElementById("condition1").value = document.getElementById("condition").value;
</script>
</html>