<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="cache.ProductLinePermissionCache"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="adultadmin.action.vo.voCatalog"%>
<%@page import="cache.CatalogCache"%>
<%@page import="java.io.File"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%@page import="java.sql.*,java.util.*,java.text.SimpleDateFormat"%>

<%!static DecimalFormat df = new DecimalFormat("#,##0.##"); %>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
String[] catalogIds = ProductLinePermissionCache.getCatalogIds(user).split(",");
%>
<html>
<head>
<title>买卖宝后台</title>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">

// 改变选中复选框文本颜色
function changeCheckboxColor(checkBox, span){
	var cb = document.getElementById(checkBox);
	if(cb.checked == true){
		document.getElementById(span).style.color = 'red';
	}else{
		document.getElementById(span).style.color = '#666666';
	}
}
</script>
<style type="text/css">
td{
	white-space: nowrap;
	text-align: center;
}
</style>
</head>
<body>
<form name="exportForm" action="searchProductStockAging.do" method="post">
	<table width="100%" cellpadding="3" cellspacing="1" border="0" >
	<tr><td align="left" colspan="3">
	<div style="font-size:18px; font-weight:bold; text-align: left;">&nbsp;商品库龄详情查询</div>
	</td>
	<tr><td width="10%" style="vertical-align:bottom;">产品分类:<br/>&nbsp;</td>
		<td width="10%" style="text-align: left; vertical-align:bottom;">
			<select multiple name="catalog" style="width:200px;height:130px;">  
		        <option  value="0">全部</option>  
		        <%
	        	for(int i = 0; i < catalogIds.length; i++){
					voCatalog catalog = CatalogCache.getCatalog(StringUtil.parstInt(catalogIds[i]));
					if(catalog != null && catalog.getId() != -1){
				 %>
				 <option value="<%=catalog.getId() %>" ><%=catalog.getName() %></option>
				<% } } %>
   			 </select> 
		</td>
		<td width="80%" style="text-align: left; vertical-align:bottom;">
   			（按住Ctrl多选）<br/>&nbsp;
		</td>
	</tr>
	<tr><td width="10%" >库存区域:</td>
		<td colspan="2" style="text-align: left;">
		<%
		Map areaMap = ProductStockBean.areaMap;
		Iterator areaIter = areaMap.keySet().iterator();
		int i = 0;
		while(areaIter.hasNext()){
			int key = (Integer)areaIter.next();
		%>
		<input type="checkbox" id="area<%=i%>" name="area" value="<%=key %>" onclick="changeCheckboxColor('area<%=i%>', 'area_sp<%=i%>');"/><span id="area_sp<%=i%>"><%=areaMap.get(key) %></span>&nbsp;
		<%
			i++;
		} 
		%>
		</td>
	</tr>
	<tr><td  width="10%" >库存类型:</td>
		<td colspan="2" style="text-align: left;">
		<%
		Map typeMap = ProductStockBean.stockTypeMap;
		Iterator typeIter = typeMap.keySet().iterator();
		int j = 0;
		while(typeIter.hasNext()){
			int key = (Integer)typeIter.next();
		%>
		<input type="checkbox" id="type<%=j %>" name="type" value="<%=key %>" onclick="changeCheckboxColor('type<%=j%>', 'type_sp<%=j%>');"/><span id="type_sp<%=j %>"><%=typeMap.get(key) %></span>&nbsp;
		<%
			j++;
		} 
		%>
		</td>
	</tr>
	<tr><td  width="10%" >截止日期:</td>
		<td colspan="2" style="text-align: left;">
		<input type="text" name="searchDate" size="15" style="background-color:#CCFFFF;" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
		</td>
	</tr>
	<tr>
		<td  colspan="3" style="text-align: left;">&nbsp;
		<button style="width: 100px; height:23px; padding:2px 3px 1px;  font-size:12px;" onclick="document.exportForm.submit();">导出excel</button>
		</td>
	</tr>
	</table>
</form>
</body>
</html>
