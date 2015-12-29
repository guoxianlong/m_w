<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%@ page import="adultadmin.util.Encoder" %>
<%
ProductStockAction action = new ProductStockAction();
action.batchStockExchangeItem(request, response);

String result = (String) request.getAttribute("result");
String tip = (String) request.getAttribute("tip");
String message = (String)request.getAttribute("message");
if(message==null){
	message="";
}
String  productCodes =  (String)request.getAttribute("productCodes");
if(productCodes==null){
	productCodes="";
}
boolean postSubmit=false;
if("failure".equals(result)){
	postSubmit=true;
}else if("noExist".equals(result)){
%>
	<script>
	alert("<%=tip%>");
	history.back(-1);
	</script>
<%
}else{
%>
<script>
alert("添加成功！");
document.location = "stockExchange.jsp?exchangeId=<%=request.getParameter("exchangeId")%>";
</script>
<%}%>
<form name="batchStockMsgForm" id="batchStockMsgForm" method="post" action="stockExchange.jsp">
	<input type="hidden" name="exchangeId" id="exchangeId"/>
	<input type="hidden" name="message" id="message"/>
	<input type="hidden" name="tip" id="tip"/>
	<input type="hidden" name="productCodes" id="productCodes"/>
</form>
 
<%
	if(postSubmit){
	%>
	<script type="text/javascript">
<!--
    document.getElementById('exchangeId').value='<%=request.getParameter("exchangeId")%>';
	document.getElementById('message').value='<%=message%>';
	document.getElementById('tip').value='<%=Encoder.encrypt(tip)%>';
	document.getElementById('productCodes').value='<%=Encoder.encrypt((productCodes))%>';
	document.batchStockMsgForm.submit();
//-->
</script>
	<%
	}
%>