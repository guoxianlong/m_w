<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>出库作业明细</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE4 {color: #009933}
-->
</style>
</head>
<%
String startTime = StringUtil.convertNull((String)request.getAttribute("startTime"));
String total = (String)request.getAttribute("total");
String outCount = (String)request.getAttribute("outCount");
String inCount = (String)request.getAttribute("inCount");
%>
<LINK media=screen href="<%=request.getContextPath()%>/css/huaDongMen.css" type=text/css rel=stylesheet>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/adult-admin/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<SCRIPT type=text/javascript>
function tabs(o,o2,n,onod,o2nod){
	var m_n = document.getElementById(o).getElementsByTagName(onod);
	var c_n = document.getElementById(o2).getElementsByTagName(o2nod);
	for(i=0;i<m_n.length;i++){
		if(onod=="a"){
			m_n[i].parentElement.className=i==n?"s":"nos";
 			 c_n[i].className=i==n?"dis":"undis";	
		}else{
			 m_n[i].className=i==n?"s":"nos";
 			 c_n[i].className=i==n?"dis":"undis";
		}
	}
}
function submitForm(){
	document.forms[0].submit();
}
</SCRIPT>
<body bgcolor="#ffcc00">
<form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=outWarehouseDetail" %> method="post" >
<table width=1002 border="0">
  <tr >
    <td width="70%"><h2>出库作业明细</h2></td>
    <td width="6%" align="left">日期查询:</td>
    <td width="5%" align="left"><input type=text name="startTime" id='startTime' size="10" value="<%=startTime%>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){this.blur();}});"
		 onChange="submitForm()" /></td>
   </tr>
</table>
</form>
<TABLE cellSpacing=0 cellPadding=0 width=194 border=0>
  <TBODY>
  <TR>
    <TD align=left>
      <TABLE cellSpacing=0 cellPadding=0 width=235 border=0>
        <TBODY>
        <TR>
          <TD height=19>
            <TABLE id=paihang height=19 cellSpacing=0 cellPadding=0 width=235 
            background=img/bgpaihang.gif border=0>
              <TBODY>
              <TR>
                <TD class=s onMouseOver="tabs('paihang','tab_l_4',0,'td','div')" 
                width=78><A href="<%=request.getContextPath()%>/admin/mailingBatch.do?method=deliverOrderDetail&flag=check&startTime=<%= startTime %>" 
                  target=_blank>已完成复核订单<%=total %></A></TD>
                <TD class=nos 
                onmouseover="tabs('paihang','tab_l_4',1,'td','div')" 
                align=middle width=78><A href="<%=request.getContextPath()%>/admin/mailingBatch.do?method=deliverOrderDetail&flag=in&startTime=<%= startTime %>" 
                  target=_blank>未出库作业订单<%= inCount %></A></TD>
                <TD class=nos 
                onmouseover="tabs('paihang','tab_l_4',2,'td','div')" 
                  width=79><A href="<%=request.getContextPath()%>/admin/mailingBatch.do?method=deliverOrderDetail&flag=out&startTime=<%= startTime %>" 
                  target=_blank>已出库作业订单<%= outCount %></A></TD>
                  </TR></TBODY></TABLE></TD></TR>
        <TR>
          <TD class=paihangbang id=tab_l_4 align=middle height=487>
            <DIV class=dis><IFRAME name=day align=middle marginWidth=0 
            marginHeight=0 src="<%=request.getContextPath()%>/admin/mailingBatch.do?method=deliverOrderDetail&flag=check&startTime=<%= startTime %>" frameBorder=0 
            width=1002 scrolling=no height=441></IFRAME></DIV>
            <DIV class=undis><IFRAME name=day align=middle marginWidth=0 
            marginHeight=0 src="<%=request.getContextPath()%>/admin/mailingBatch.do?method=deliverOrderDetail&flag=in&startTime=<%= startTime %>" frameBorder=0 
            width=1002 scrolling=no height=441></IFRAME></DIV>
            <DIV class=undis><IFRAME name=day align=middle marginWidth=0 
            marginHeight=0 src="<%=request.getContextPath()%>/admin/mailingBatch.do?method=deliverOrderDetail&flag=out&startTime=<%= startTime %>" frameBorder=0 
            width=1002 scrolling=no  height=441></IFRAME></DIV></TD></TR></TBODY>
	</TABLE>
	</TD>
</TR>
</TBODY>
</TABLE>
</html>