<%@ page contentType="text/html;charset=utf-8"%><html>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*"%>
<%@ page import="mmb.product.imageRepository.*"%>
<% 
	String productId = (String)request.getAttribute("productId");
 %>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>商品800浏览图后台</title>
</head>
<frameset cols="210,*" framespacing="1" frameborder="yes" border="1" id="mainSet">
<frame src="<%=request.getContextPath()%>/admin/product/imageRepository/productImagesList.jsp?productId=<%=productId%>" name="contents" scrolling="auto" frameborder="yes" target="main">
<frame src="productImages.do?method=selectProductImageByPicId&fst=1&productId=<%=productId%>" id="main" name="main" scrolling="auto" frameborder=no target="_self">
</frameset>
</html>