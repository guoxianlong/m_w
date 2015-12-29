<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%
StockAction action = new StockAction();
action.collectOrderStock(request, response);

String result = (String) request.getAttribute("result");
if("failure".equals(result)){
%>
<script>
alert("参数有误！");
history.back(-1);
</script>
<%
	return;
}
List operList = (List) request.getAttribute("operList");
List shList = (List) request.getAttribute("shList");
StockOperationBean oper = null;
StockHistoryBean sh = null;

int i, count;
Iterator itr = null;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script   language="JavaScript">   
  function   getTblText(objTbl)   
  {   
  if(!objTbl)   return   "";   
  if(objTbl.tagName   !=   "TABLE")   return   "";   
  var   returnText   =   "";   
  for(var   r=0;   r<objTbl.rows.length;   r++)   
  {   
  for(var   c=0;   c<objTbl.rows[r].cells.length;   c++)   
  {
  returnText   +=  objTbl.rows[r].cells[c].innerText.replace(/\r\n/g,';');
  if(r != 0 && c == objTbl.rows[r].cells.length - 1){
  	returnText += ";";
  }
  returnText += "\t";   
  }   
  returnText   +=   "\n";   
  }   
  return   returnText;
  }   
</script>
<script>
function exportList(){
	clipboardData.setData('text',getTblText(listTable));
	alert("列表内容已复制到剪贴板，粘贴到excel文件中即可。");
}
</script>
<p align="center">待出货订单汇总 <input type="button" onclick="javascript:exportList();" value="导出列表"/></p>
<table width="100%" border="1" id="listTable">
<%-- %>
<tr>
  <td colspan="9" style="word-break:break-all;word-wrap: break-word;">订单编号：<%
count = operList.size();
for(i = 0; i < count; i ++){
	oper = (StockOperationBean) operList.get(i);
	if(i > 0){
		out.print(", ");
	}
%><a href="../order.do?id=<%=oper.getOrder().getId()%>"><%=oper.getOrderCode()%></a><%
}
%>  
  </td>  
</tr>
--%>
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>产品编号</td>
  <td>北京出库总量</td>
  <td>北京缺货量</td>
  <td>广东出库总量</td>
  <td>广东缺货量</td>
  <td>订单</td>
</tr>
<%
count = shList.size();
for(i = 0; i < count; i ++){
	sh = (StockHistoryBean) shList.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getCode()%></a></td>  
  <td><%=sh.getStockBj()%></td>
<%if(sh.getStockBj() - sh.getProduct().getStock() > 0){ %>
  <td style="color:red;"><%=sh.getStockBj() - sh.getProduct().getStock()%></td>
<%} else { %>
  <td>0</td>
<%} %>
  <td><%=sh.getStockGd()%></td>
<%if(sh.getStockGd() - sh.getProduct().getStockGd() > 0){ %>
  <td style="color:red;"><%=sh.getStockGd() - sh.getProduct().getStockGd()%></td>
<%} else { %>
  <td>0</td>
<%} %>
  <td><%
int oCount = 0;
for(int j = 0; j < operList.size(); j++){
	oper = (StockOperationBean) operList.get(j);
	if(sh.getRemark() != null && sh.getRemark().indexOf("," + oper.getId() + ",") != -1){
		if(oCount > 0){
			//out.print(", ");
		}
		oCount ++;
%><a href="../order.do?id=<%=oper.getOrder().getId()%>"><%=oper.getOrderCode()%></a><br/><%
	}
}
%></td>
</tr>
<%
}
%>
</table>

<p align="center"><a href="orderStockList.jsp">返回订单出货操作记录列表</a></p>