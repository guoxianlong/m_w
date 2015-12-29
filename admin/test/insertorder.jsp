<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="adultadmin.test.InsertAction" %>
<%@ page import="adultadmin.action.vo.voOrder" %>
<%@ page import="adultadmin.action.vo.voProduct" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>插入订单</title>
</head>
<body>
<%
if(request.getParameter("count")!=null
		&&request.getParameter("select")!=null
		&&request.getParameter("select").equals("2")
		&&!request.getParameter("selected").equals("")){
	boolean b=new InsertAction().find(request.getParameter("selected"));
	if(b){
		int count=0;
		if(request.getParameter("count")!=null&&request.getParameter("select")!=null){
			InsertAction ia=new InsertAction();
			System.out.println(request.getParameter("selected"));
			count=Integer.parseInt(request.getParameter("count"));
			ia.insert(request,response);
		}
	}else{ %>
		没有该产品！
	<%}
}else if(request.getParameter("count")!=null
		&&request.getParameter("select")!=null
		&&request.getParameter("select").equals("1")){
	int count=0;
	if(!request.getParameter("count").equals("")){
		new InsertAction().insert(request,response);
	}else{ %>
		没有输入数量！
	<%}
}
%>
<form method="post">
输入要插入的订单数量：<input type="text" name="count"/><br/>
随机选择产品<input type="radio" name="select" value="1"></input><br/>
指定产品编号<input type="radio" name="select" value="2"><input type="text" name="selected"/></input>
<%if(request.getParameter("count")!=null&&request.getParameter("select")==null){%>
	没有选择！
<%}%>
<br/>
<input type="submit" value="提交"/>
</form>
<%
if(request.getAttribute("orderList")!=null){
	ArrayList orderList=(ArrayList)request.getAttribute("orderList");
	ArrayList productList=(ArrayList)request.getAttribute("productList");
	for(int i=0;i<(orderList.size()>20?20:orderList.size());i++){%>
		<%=i+1 %>
		<br/>
		<%
		voOrder order=(voOrder)orderList.get(i);%>
		订单编号：<%=order.getCode() %><br/>
		用户姓名：<%=order.getName() %><br/>
		用户电话：<%=order.getPhone() %><br/>
		用户地址：<%=order.getAddress() %><br/>
		订单创建时间：<%=order.getCreateDatetime() %><br/>
		总价：<%=order.getPrice() %><br/>
		商品列表：<br/>
		<%voProduct p=(voProduct)productList.get(i);%>
		商品编号：<%=p.getCode() %><%="\t" %>
		商品名称：<%=p.getName() %><%="\t" %>
		商品单价：<%=p.getPrice() %><%="\t" %>
		<br/><br/>
	<%}
}
%>
</body>
</html>