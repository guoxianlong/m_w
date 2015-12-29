<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<html>
<head>
<title>货位修改</title>
<%
CargoProductStockBean bean=null;
CargoInfoBean ciBean=new CargoInfoBean();
String cargoAddress="";
List productLineList=null;
if(request.getAttribute("cargoProductStockBean")!=null){ 
	bean=(CargoProductStockBean)request.getAttribute("cargoProductStockBean");
}
if(request.getAttribute("cargoAddress")!=null){ 
	cargoAddress=request.getAttribute("cargoAddress").toString();
}
if(request.getAttribute("productLineList")!=null){ 
	productLineList=(List)request.getAttribute("productLineList");
}
if(request.getAttribute("ciBean")!=null){ 
	ciBean=(CargoInfoBean)request.getAttribute("ciBean");
}
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
</head>
<body>
<form>货位修改页&nbsp;&nbsp;&nbsp;&nbsp;<a href="cargoInfo.do?method=cargoLog&cargoId=<%=ciBean.getId() %>" target="_blank">人员操作记录</a></form>
<form action="../admin/cargoInfo.do?method=updateCargo&cargoId=<%=ciBean.getId()%>" method="post">
货位号：<b><%=ciBean.getWholeCode() %></b>（<%=cargoAddress %>）<br/>
货位状态：<%=ciBean.getStatusName() %>
<%if(bean!=null){ %>
上架产品：<%=bean.getProduct().getOriname() %>（<%=bean.getStockCount()+bean.getStockLockCount() %>）
<%} %><br/>
库存类型：<%=ciBean.getStockTypeName() %>	
存放类型：<%if(ciBean.getStatus()==0){ %>
			<%=ciBean.getStoreTypeName() %>
		<%}else{ %>
			<select name="storeType">
				<option value="0" <%if(ciBean.getStoreType()==0){ %>selected=selected<%} %>>散件区</option>
				<option value="1" <%if(ciBean.getStoreType()==1){ %>selected=selected<%} %>>整件区</option>
				<option value="2" <%if(ciBean.getStoreType()==2){ %>selected=selected<%} %>>缓存区</option>
				<option value="4" <%if(ciBean.getStoreType()==4){ %>selected=selected<%} %>>混合区</option>
			</select>
		<%} %>
产品线：<select name="productLineId">
			<option value="">请选择</option>
			<%for(int i=0;i<productLineList.size();i++){ %>
				<%voProductLine productLine=(voProductLine)productLineList.get(i); %>
				<option value="<%=productLine.getId() %>" <%if(ciBean.getProductLineId()==productLine.getId()){ %>selected=selected<%} %>><%=productLine.getName() %></option>
			<%} %>
		</select>
货位类型：<select id="type" name="type">
			<option value=0 <%if(ciBean.getType()==0){ %>selected=selected<%} %>>普通</option>
			<option value=1 <%if(ciBean.getType()==1){ %>selected=selected<%} %>>热销</option>
			<option value=2 <%if(ciBean.getType()==2){ %>selected=selected<%} %>>滞销</option>
			<option value=3 <%if(ciBean.getType()==3){ %>selected=selected<%} %>>完好</option>
			<option value=4 <%if(ciBean.getType()==4){ %>selected=selected<%} %>>保修机残次</option>
			<option value=5 <%if(ciBean.getType()==5){ %>selected=selected<%} %>>非保修机残次</option>
		</select><br/>
货位尺寸：长<input type="text" size=5  maxlength="3" name="length" value="<%=ciBean.getLength() %>"/>cm&nbsp;
		宽<input type="text" size=5  maxlength="3" name="width" value="<%=ciBean.getWidth() %>"/>cm&nbsp;
		高<input type="text" size=5  maxlength="3" name="high" value="<%=ciBean.getHigh() %>"/>cm&nbsp;
警戒线：<input type="text" size=5  maxlength="3" name="warnStockCount" value="<%=ciBean.getWarnStockCount() %>"/>
货位最大容量：<input type="text" size=5 maxlength="3" name="maxStockCount" value="<%=ciBean.getMaxStockCount() %>"/><br/>
备注：
<textarea cols='20' rows='3' name="remark"><%=ciBean.getRemark() %></textarea><br/>
<%if(bean!=null){ %>
<input type="hidden" name="cargoProductStockId" value="<%=bean.getId() %>" />
<%} %>
<input type="submit" value="修改"/>
</form>
</body>
</html>