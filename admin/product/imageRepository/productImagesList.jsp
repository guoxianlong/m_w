<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*"%>
<%@ page import="mmb.product.imageRepository.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath() %>/jquery/jquery-1.7.1.js"></script>
</head>
<body style="font-size:10pt" leftmargin=2 topmargin= 2>
	<div align="center" id="menuTree">
		<% 
			String productId = (String)request.getParameter("productId");
			 List imageList = (List)ProductImagesAction.getProductImagesListByProductId(productId);
			 if(imageList != null && imageList.size() > 0){
			 	for(int i = 0 ; i < imageList.size(); i++){
			 		ImageRepositoryDto bean = (ImageRepositoryDto)imageList.get(i);
			 		%>
				 			<div align="center" style="border:1px solid #324234;">
					 			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" 
									codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="100" height="100"> 
									<param name="movie" value="<%=Constants.RESOURCE_PRODUCT_IMAGE%>/swfoto.swf?image=<%=Constants.RESOURCE_PRODUCT_IMAGE%><%= bean.getViewPathDir() %><%= bean.getViewName() %>&imageLink=<%=request.getContextPath()%>/admin/productImages.do?method=selectProductImageByPicId^fst=1^picId=<%=bean.getId()%>&windowOpen=main"> 
									<embed src="<%=Constants.RESOURCE_PRODUCT_IMAGE%>/swfoto.swf?image=<%=Constants.RESOURCE_PRODUCT_IMAGE%><%= bean.getViewPathDir() %><%= bean.getViewName() %>&imageLink=<%=request.getContextPath()%>/admin/productImages.do?method=selectProductImageByPicId^fst=1^picId=<%=bean.getId()%>" width="100" height="100"
 									pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash"></embed> 
								</object> 
					 		</div>
			 		<%
			 	}
			 }else {
			 %>
			 	<script>alert("此商品没有图片!");window.parent.close();</script>
			 <%
			 }
		%>
	</div>
	
	<script type="text/javascript">
		/**
		 * 注册事件
		 */
		try {
			var sssdf = document.getElementById('menuTree');
		   document.getElementById('menuTree').oncontextmenu = oncontextmenu;
		   document.onclick = OnDocumentClick;
		} catch(ex) {
		}
	
	</script>
</body>
</html>
