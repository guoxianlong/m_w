<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>销售退货原因统计</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE4 {color: #009933}
-->
</style>
</head>
<%

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
</script>
<body bgcolor="#ffcc00">
<form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=outWarehouseDetail" %> method="post" >
<table width=1002 border="0">
  <tr >
    <td width="70%"><h2>销售退货原因统计</h2></td>
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
                width=78><A href="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatisticChild&flag=day" 
                  target=_blank>按天统计</A></TD>
                <TD class=nos 
                onmouseover="tabs('paihang','tab_l_4',1,'td','div')" 
                align=middle width=78><A href="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatisticChild&flag=moon" 
                  target=_blank>按月统计</A></TD>
                <TD class=nos 
                onmouseover="tabs('paihang','tab_l_4',2,'td','div')" 
                  width=79><A href="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatistic&flag=detail" 
                  target=_blank>统计明细</A></TD>
                  </TR></TBODY></TABLE></TD></TR>
        <TR>
          <TD class=paihangbang id=tab_l_4 align=middle height=417>
            <DIV class=dis><IFRAME name=day align=middle marginWidth=0 scrolling="auto"
            marginHeight=0 src="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatisticChild&flag=day" frameBorder=0 
            width=1039 scrolling=no height=401></IFRAME></DIV>
            <DIV class=undis><IFRAME name=day align=middle marginWidth=0 scrolling="auto"
            marginHeight=0 src="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatisticChild&flag=moon" frameBorder=0 
            width=1039 scrolling=no height=401></IFRAME></DIV>
            <DIV class=undis><IFRAME name=day align=middle marginWidth=0 scrolling="auto"  
            marginHeight=0 src="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatistic&flag=detail" frameBorder=0 
            width=1039 scrolling=no  height=401></IFRAME></DIV></TD></TR></TBODY>
	</TABLE>
	</TD>
</TR>
</TBODY>
</TABLE>
</html>