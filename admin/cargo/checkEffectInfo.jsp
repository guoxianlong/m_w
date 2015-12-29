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
    
    <title>商品质检分类与效率</title>
    
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
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
   		
   		function toAddPage() {
   			window.location="<%= request.getContextPath()%>/admin/cargo/addCheckEffect.jsp";
   		}
   		
   		function deleteColum(id, name){
			if(!confirm("您确定要删除该条目么？")){
				return;
			}
			window.location="productWarePropertyAction.do?method=deleteCheckEffect&id="+id + "&name="+ name;
			return;
		}
	</script>

  </head>
  <body>
  	<div align="center">
  		<table align="center" width="40%" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px">
  			<tr align="center">
  				<td bgcolor="#4DFFFF">
  				<a href="productWarePropertyAction.do?method=getCheckEffectInfo">商品质检分类与效率</a>
  				</td>
  				<td bgcolor="#FFFFFF">
  					<a href="productWarePropertyAction.do?method=getCheckStaffWorkPlanInfo">质检排班计划</a>
  				</td>
  			</tr>
  		</table>
  	</div>
  <div align="center">
  <h2>商品质检分类与效率</h2>
  	<table align="center" width="60%" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px" >
		<tr bgcolor="#95CACA" >
			<td align="center">
			商品质检分类
			</td>
			<td align="center">
			产品线
			</td>
			<td align="center">
			效率（件/小时）
			</td>
			<td align="center">
			操作
			</td>
		</tr>
		<% if( list != null && list.size() != 0 ) {
			 for( int i = 0; i < list.size(); i++ ) {
			 CheckEffectBean cfb = (CheckEffectBean) list.get(i);
		 %>
			<tr bgcolor="#FFFFFF" >
			<td align="center">
			<%= cfb.getName()%>
			</td>
			<td align="center">
			<%= cfb.getProductLines()%>
			</td>
			<td align="center">
			<%=  cfb.getEffect()%>
			</td>
			<td align="center">
			<a href="<%= request.getContextPath() %>/admin/cargo/editCheckEffect.jsp?id=<%= cfb.getId() %>&name=<%= cfb.getName()%>&effect=<%= cfb.getEffect()%>">编辑</a>&nbsp;|&nbsp;<a href="javascript:deleteColum( <%=cfb.getId()%>, '<%=cfb.getName()%>');" >删除</a>
			</td>
		</tr>
		<%
		 }
		 }else {%>
			<tr bgcolor="#FFFF93" >
			<td align="center" colspan="4">
				没有商品质检分类记录
			</td>
		</tr>
		<% }%>
	</table>
  	<button onclick="toAddPage();">添加</button>
  	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
  </div>
  
</body>
</html>
