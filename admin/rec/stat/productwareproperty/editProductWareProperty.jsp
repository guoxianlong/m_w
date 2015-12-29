<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	String id = request.getParameter("id");
%>
<html>
  <head>
    
    <title>添加商品物流属性</title>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#test").form('load',{url:'getPropertyDetail.mmx?id=67',
			success:function(data){
				alert(data);
			}
			});
		});
	</script>
	

  </head>
  <body>
  
   	<form id="test" name="test">
   		<input name="cartonningStandardCount" id="cartonningStandardCount">
   	</form>
</body>
</html>