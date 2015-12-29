<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%
OrderStockAction action = new OrderStockAction();
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
Map scMap = (Map) request.getAttribute("scMap");
OrderStockBean oper = null;
OrderStockProductBean sh = null;

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
	oper = (OrderStockBean) operList.get(i);
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
  <td>北库出库总量</td>
  <td>北库缺货量</td>
  <td>芳村出库总量</td>
  <td>芳村缺货量</td>
  <td>广速出库总量</td>
  <td>广速缺货量</td>
  <td>订单</td>
</tr>
<%
count = shList.size();
for(i = 0; i < count; i ++){
	sh = (OrderStockProductBean) shList.get(i);
	int[] sc = (int[])scMap.get(Integer.valueOf(sh.getProductId()));
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getCode()%></a></td>  
  <td><%= sc[0] %></td>
<%if(sc[0] - (sh.getProduct().getStock(ProductStockBean.AREA_BJ, sh.getStockType()) + sh.getProduct().getLockCount(ProductStockBean.AREA_BJ, sh.getStockType())) > 0){ %>
  <td style="color:red;"><%=sc[0] - (sh.getProduct().getStock(ProductStockBean.AREA_BJ, sh.getStockType()) + sh.getProduct().getLockCount(ProductStockBean.AREA_BJ, sh.getStockType()))%></td>
<%} else { %>
  <td>0</td>
<%} %>
  <td><%= sc[1] %></td>
<%if(sc[1] - (sh.getProduct().getStock(ProductStockBean.AREA_GF, sh.getStockType()) + sh.getProduct().getLockCount(ProductStockBean.AREA_GF, sh.getStockType())) > 0){ %>
  <td style="color:red;"><%=sc[1] - (sh.getProduct().getStock(ProductStockBean.AREA_GF, sh.getStockType()) + sh.getProduct().getLockCount(ProductStockBean.AREA_GF, sh.getStockType()))%></td>
<%} else { %>
  <td>0</td>
<%} %>
  <td><%= sc[2] %></td>
<%if(sc[2] - (sh.getProduct().getStock(ProductStockBean.AREA_GS, sh.getStockType()) + sh.getProduct().getLockCount(ProductStockBean.AREA_GS, sh.getStockType())) > 0){ %>
  <td style="color:red;"><%=sc[2] - (sh.getProduct().getStock(ProductStockBean.AREA_GS, sh.getStockType()) + sh.getProduct().getLockCount(ProductStockBean.AREA_GS, sh.getStockType()))%></td>
<%} else { %>
  <td>0</td>
<%} %>
  <td><%
int oCount = 0;
for(int j = 0; j < operList.size(); j++){
	oper = (OrderStockBean) operList.get(j);
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