<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*"%>
<%@ page import="mmb.product.imageRepository.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath() %>/jquery/jquery-1.7.1.js"></script>

    
    <style type="text/css">
		.body{font-size:14px;}
		.picBorder{width:200px;height:220px;border:1px solid #324234;margin:3px;}
		.divStyle1{width:200px;}
		.divStyle2{width:200px;height:160px}
	</style>
</head>
<body>
	<div align="center">
		<table>
		<% 
			 List imageList = (List)request.getAttribute("productImagesList");
			 if(imageList != null && imageList.size() > 0){
			 	for(int i = 0 ; i < imageList.size(); i++){
			 		ImageRepositoryDto bean = (ImageRepositoryDto)imageList.get(i);
			 		if(i%5 == 0) {
			 		%>
			 		<tr>
			 		<%
			 		}
			 		%>
			 		<td>
			 			<div align="center">
				 			<div id="front_<%=i%>" align="center">
				 				<font color="red">图片编号：</font><%= bean.getId() %> &nbsp;&nbsp;<font color="red">或者：</font><%= bean.getProductInfoId()%>/<%= bean.getName()%>
				 			</div>
				 			<div align="center" style="border:1px solid #324234;">
					 			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" 
									codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="<%= bean.getWidth() %>" height="<%= bean.getHeight() %>"> 
									<param name="movie" value="<%=Constants.RESOURCE_PRODUCT_IMAGE%>/swfoto.swf?image=<%=Constants.RESOURCE_PRODUCT_IMAGE%><%= bean.getPathDir() %><%= bean.getName() %>"> 
									<embed src="<%=Constants.RESOURCE_PRODUCT_IMAGE%>/swfoto.swf?image=<%=Constants.RESOURCE_PRODUCT_IMAGE%><%= bean.getPathDir() %><%= bean.getName() %>" width="<%= bean.getWidth() %>" height="<%= bean.getHeight() %>"
 									pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash"></embed> 
								</object> 
					 		</div>
				 		</div>
			 		</td>
			 		<%
			 		if(i%5 == 4) {
			 		%>
			 		</tr>
			 		<%
			 		}
			 	}
			 }
		%>
		<table>
	</div>
</body>
</html>
