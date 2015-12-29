<%@page import="adultadmin.util.db.DbOperation"%>
<%@page import="adultadmin.bean.cargo.*"%>
<%@ page language="java"
	import="java.util.*,adultadmin.util.*,adultadmin.service.*,adultadmin.service.impl.*,adultadmin.service.infc.*"
	pageEncoding="GB18030"%>
<%@page import="adultadmin.action.vo.*,cache.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	voUser user = (voUser)request.getSession().getAttribute("userView");
%>
<%String action = StringUtil.convertNull(request.getParameter("action"));%>
<%if(action.equals("storage")){ %>
<%
DbOperation dbOp = new DbOperation();
dbOp.init("adult_slave");
ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
try{
	int stockType = StringUtil.toInt(request.getParameter("stockType"));
	List list = service.getCargoInfoStorageList("id in (select storage_id from cargo_info_stock_area where stock_type = "+stockType+" group by storage_id)",-1,-1,"code asc");
%>
<font color="red">*</font>×÷Òµ²Ö¿â£º
<%
	for(int i=0;i<list.size();i++){
		CargoInfoStorageBean storage = (CargoInfoStorageBean)list.get(i);
%>
<input type="radio" name="storageId" value="<%=storage.getId() %>" onClick="changeArea();"<%if(i == 0){ %> checked="checked"<%} %>/><span><%=storage.getWholeCode() %></span>&nbsp;&nbsp;
<%} %>
<%
}catch(Exception e){
	e.printStackTrace();
}finally{
	service.releaseAll();
}
%>
<%}else if(action.equals("area")){ %>
<%
DbOperation dbOp = new DbOperation();
dbOp.init("adult_slave");
ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
try{
	int stockType = StringUtil.toInt(request.getParameter("stockType"));
	int storageId = StringUtil.toInt(request.getParameter("storageId"));
	List list = service.getCargoInfoStockAreaList("storage_id = "+storageId+" and stock_type = "+stockType,-1,-1,"code asc");
%>
<font color="red">*</font>²Ö¿âÇøÓò£º
<%
	for(int i=0;i<list.size();i++){
		CargoInfoStockAreaBean bean = (CargoInfoStockAreaBean)list.get(i);
%>
<input type="checkbox" name="stockAreaId" value="<%=bean.getId() %>" checked="checked"/><span><%=bean.getCode() %></span>&nbsp;&nbsp;
<%} %>
<%
}catch(Exception e){
	e.printStackTrace();
}finally{
	service.releaseAll();
}
%>
<%}%>