<%@page contentType="text/html;charset=utf-8"%><%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.bean.*, adultadmin.util.*" %>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.bean.bybs.*"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.action.bybs.ByBsAction"%>
<%! int x = 0; %>
<%voUser user = (voUser)request.getSession().getAttribute("userView");
 int userid = user.getId();
 UserGroupBean group = user.getGroup();
	Map stockTypeMap = ProductStockBean.stockTypeMap;
	Map areaMap = ProductStockBean.areaMap;
	
	String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String startTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("startTime")));
String endTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("endTime")));
String _type = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("type")));
String warehouseArea = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("warehouseArea")));
String warehouseType = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("warehouseType")));
String sourceCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("sourceCode")));
String productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
String productCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productCode")));
List list=(List)request.getAttribute("alist");
productName = Encoder.decrypt(productName);//解码为中文
if(productName==null){//解码失败,表示已经为中文,则返回默认
	productName =StringUtil.dealParam(request.getParameter("productName"));//名称
}
if (productName==null) productName="";
String[] statuss = request.getParameterValues("status");
String status = "";
if(statuss!=null){
	for(int i=0;i<statuss.length;i++){
		status = status + statuss[i]+",";
	}
}
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	<%
	response.setContentType("application/vnd.ms-excel;charset=gb2312");
	String now = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
	String filename =  "报损报溢导出"+now;
	response.setHeader("Content-disposition","attachment; filename=\"" + new String(filename.getBytes("GBK"), "iso8859-1") + ".xls\"");
%>
		<title>My JSP 'list.jsp' starting page</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
		</head>
		<body>
		
		<table cellpadding="3" cellspacing="1" border="1" style="border-collapse:collapse;" width="100%">
			<tr>
				<td align="center">
					序号
				</td>
				<td align="center">
					单据号
				</td>
				
				<td align="center">
					库类型
				</td>
				<td align="center">
					库区域
				</td>
				<td align="center">
				 	产品编号
				</td>
				<td align="center">
				 	原名称
				</td>
				<td align="center">
					报损数量
				</td>
				<td align="center">
					报溢数量
				</td>
				<td align="center">
					货位号
				</td>
				<td align="center">
					添加人
				</td>
				<td align="center">
					状态
				</td>
				
			</tr>
			<%if(list!=null&&list.size()>0){
			Map map=null;
				for(int i=0;i<list.size();i++){
					map=(Map)list.get(i);
					%>
					<tr>
						<td><%=i+1 %></td>
						<td><%=map.get("billCode").toString() %></td>
						<td><%=stockTypeMap.get(Integer.valueOf(map.get("stockType").toString())) %></td>
						<td><%=areaMap.get(Integer.valueOf(map.get("stockArea").toString())) %></td>
						<td><%if(ByBsAction.getProductByOperationId(StringUtil.StringToId(map.get("id").toString()))!=null){%><%=ByBsAction.getProductByOperationId(StringUtil.StringToId(map.get("id").toString())).getProduct_code() %><%} %></td>
						<td><%if(ByBsAction.getProductByOperationId(StringUtil.StringToId(map.get("id").toString()))!=null){%><%=ByBsAction.getProductByOperationId(StringUtil.StringToId(map.get("id").toString())).getOriname() %><%} %></td>
						<%if(map.get("type").toString().equals("0")){
						%>
						<td><%=StringUtil.toInt(map.get("count").toString()) %></td>
						<td></td>		
						<%
						}else{
						%>
						<td></td>
						<td><%=StringUtil.toInt(map.get("count").toString()) %></td>		
						<%
						} %>
						<td><%=map.get("wholeCode").toString() %></td>
						<td><%=map.get("userName").toString() %></td>
						<td><%=BsbyOperationnoteBean.current_typeMap.get(Integer.valueOf(map.get("currentType").toString())) %></td>
					</tr>
					<%
				}
			
			} %>
			
				
			
			
		</table>
		
	</body>
</html>
