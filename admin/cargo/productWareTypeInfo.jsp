<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	List list = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
%>
<html>
  <head>
    
    <title>商品物流分类优先级调整</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,10}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于10位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
   		
   		function deleteColum(id, name){
			if(!confirm("您确定要删除该条目么？")){
				return;
			}
			window.location="productWarePropertyAction.do?method=deleteCheckEffect&id="+id + "&name="+ name;
			return;
		}
		function addToChange(tail) {
			$("#change_"+tail).removeAttr("disabled");
		} 
		
		function check() {
			var x = "";
			<%
			 if( list != null && list.size() != 0 ) {
				 for( int i = 0; i < list.size(); i++ ) {
				 	ProductWareTypeBean pwtBean = (ProductWareTypeBean) list.get(i);
			%>
				x = $("#sequence_<%= pwtBean.getId()%>").val();
				if( x == null || x == "" ) {
					alert("有为空的优先级，不能提交！");
					$("#sequence_<%= pwtBean.getId()%>").focus();
					return false;
				}
			<%
				}
			}
			%>
			return true;
		}
		
	</script>

  </head>
  <body>
  <div align="center">
  <h2>商品物流分类优先级调整</h2>
  <form action="productWarePropertyAction.do?method=editProductWareTypeSequence" method="post" onsubmit="return check();">
  	<table align="center" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px" >
		<tr bgcolor="#95CACA" >
			<td align="left">
			商品物流分类
			</td>
			<td align="center">
			优先级
			</td>
		</tr>
		
		<% if( list != null && list.size() != 0 ) {
			 for( int i = 0; i < list.size(); i++ ) {
			 ProductWareTypeBean pwtBean = (ProductWareTypeBean) list.get(i);
		 %>
			<tr bgcolor="#FFFFFF" >
			<td align="left">
			<%= pwtBean.getName()%>
			</td>
			<td align="center" width="60px">
			<input type="text" size="6" name="sequence_<%= pwtBean.getId()%>" id="sequence_<%= pwtBean.getId()%>" value="<%= pwtBean.getSequence()%>" onblur="checkNumber(this);" onchange="addToChange(<%= pwtBean.getId()%>);" />
			<input type="hidden" name="change" id="change_<%= pwtBean.getId()%>" value="<%= pwtBean.getId()%>" disabled="true"/>
			</td>
		</tr>
		<%
		 }
		 }else {%>
			<tr bgcolor="#FFFF93" >
			<td align="center" colspan="2">
				没有商品物流分类记录
			</td>
		</tr>
		<% }%>
		
		
	</table>
	<br/>
	<div align="center">
				<input type="submit" value="  提交  " />
		</div>
		</form>
  	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
  </div>
  
</body>
</html>
