<%@page import="adultadmin.bean.cargo.*"%>
<%@page import="java.util.*"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%
List list=null;
if(request.getAttribute("cargoInfoAreaList")!=null){
	list = (List)request.getAttribute("cargoInfoAreaList");	%>
	<select id="areaId" name="areaId" onchange="selectarea();">
	<option value="">请选择</option>
	<%
		for(int i=0;i<list.size();i++){
			CargoInfoAreaBean bean = (CargoInfoAreaBean)list.get(i);
	%>
	<option value="<%=bean.getId()%>" ><%=bean.getCode()%>--<%=bean.getName() %></option>
	<%}%>
	</select>
<%}%>
<%
if(request.getAttribute("cargoInfoStorageList")!=null){
	list=(List)request.getAttribute("cargoInfoStorageList");%>
	<select id="storageId" name="storageId" onchange="selectstorage();">
	<option value="">请选择</option>
	<%
		for(int i=0;i<list.size();i++){
			CargoInfoStorageBean bean = (CargoInfoStorageBean)list.get(i);
	%>
	<option value="<%=bean.getId()%>"><%=bean.getCode() %>--<%=bean.getName() %></option>
	<%}%>
	</select>

<%}%>
<%
if(request.getAttribute("cargoInfoStockAreaList")!=null){
	list=(List)request.getAttribute("cargoInfoStockAreaList");%>
	<select id="stockAreaId" name="stockAreaId" onchange="selectstockarea();">
	<option value="">请选择</option>
	<%
		for(int i=0;i<list.size();i++){
			CargoInfoStockAreaBean bean = (CargoInfoStockAreaBean)list.get(i);
	%>
	<option value="<%=bean.getId()%>"><%=bean.getCode() %>--<%=bean.getName() %></option>
	<%}%>
	</select>

<%}%>
<%
if(request.getAttribute("cargoInfoStockAreaList2")!=null){
	list=(List)request.getAttribute("cargoInfoStockAreaList2");%>
	<select id="stockAreaId2" name="stockAreaId" onchange="selectstockarea2();">
	<option value="">请选择</option>
	<%
		for(int i=0;i<list.size();i++){
			CargoInfoStockAreaBean bean = (CargoInfoStockAreaBean)list.get(i);
	%>
	<option value="<%=bean.getId()%>"><%=bean.getCode() %>--<%=bean.getName() %></option>
	<%}%>
	</select>

<%}%>
<%
if(request.getAttribute("cargoInfoPassageList")!=null){
	list=(List)request.getAttribute("cargoInfoPassageList");%>
	<select id="passageId" name="passageId" onchange="selectpassage();">
	<option value="">请选择</option>
	<%
		for(int i=0;i<list.size();i++){
			CargoInfoPassageBean bean = (CargoInfoPassageBean)list.get(i);
	%>
	<option value="<%=bean.getId()%>"><%=bean.getCode() %></option>
	<%}%>
	</select>

<%}%>
<%
if(request.getAttribute("cargoInfoShelfList")!=null){
	list=(List)request.getAttribute("cargoInfoShelfList");%>
	<select id="shelfId" name="shelfId" onchange="selectshelf();">
	<option value="">请选择</option>
	<%
		for(int i=0;i<list.size();i++){
			CargoInfoShelfBean bean = (CargoInfoShelfBean)list.get(i);
	%>
	<option value="<%=bean.getId()%>"><%=bean.getCode() %></option>
	<%}%>
	</select>

<%}%>
<%
if(request.getAttribute("floorCount")!=null){
	int floorCount=Integer.parseInt(request.getAttribute("floorCount").toString());%>
	<select id="floorNum" name="floorNum">
	<option value="">请选择</option>
	<%
		for(int i=0;i<floorCount;i++){
	%>
	<option value="<%=i+1%>">第<%=i+1 %>层</option>
	<%}%>
	</select>

<%}%>
