<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, adultadmin.bean.*, adultadmin.bean.buy.*" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	List list=(List)request.getAttribute("list");
	List productLineNameList=(List)request.getAttribute("productLineNameList");
	List operCountList=(List)request.getAttribute("operCountList");
	List operList=(List)request.getAttribute("operList");
	List statusList=(List)request.getAttribute("statusList");
	String hasOperList="0";
	if(request.getAttribute("hasOperList")!=null){
		hasOperList=request.getAttribute("hasOperList").toString();
	}
	HashMap proxyMap = (HashMap)request.getAttribute("proxyMap");
	BuyOrderProductBean bop = null;
	
	response.setContentType("application/vnd.ms-excel");
	String fileName = DateUtil.getNow().replace(" ","-").replace(":","-");
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
<table width="100%" cellpadding="3" cellspacing="1" border="1">
    <tr>
    	<td>货位号</td>
    	<td>货位产品线</td>
    	<td>产品编号</td>
    	<td>产品原名称</td>
    	<td>当前货位库存（其中冻结量）</td>
    	<td>货位空间冻结</td>
    	<td>货位警戒线</td>
    	<td>货位最大容量</td>
    	<td>存放类型</td>
    	<td>库存类型</td>
    	<td>货位类型</td>
    	<td>货位状态</td>
    	<td>备注</td>
    	<td>未完成作业单数</td>
    	<%if(hasOperList.equals("1")){ %>
    	<td>作业单状态</td>
    	<td>作业单号</td>
    	<%} %>
    </tr>
    <%for(int i=0;i<list.size();i++){ %>
    	<%CargoProductStockBean bean=(CargoProductStockBean)list.get(i);%>
    	<%List operCodeList=(List)operList.get(i); %>
    	<%List statusNameList=(List)statusList.get(i); %>
    	<tr>
    		<td><%=bean.getCargoInfo().getWholeCode() %></td>
    		<td><%=productLineNameList.get(i) %></td>
    		<td><%if(bean.getProduct().getCode()!=null){ %><%=bean.getProduct().getCode() %><%}else{ %>-<%} %></td>
			<td><%if(bean.getProduct().getOriname()!=null){ %><%=bean.getProduct().getOriname() %><%}else{ %>-<%} %></td>
    		<td><%=bean.getStockCount()+bean.getStockLockCount() %>(<%=bean.getStockLockCount() %>)</td>
    		<td><%=bean.getCargoInfo().getSpaceLockCount() %></td>
    		<td><%=bean.getCargoInfo().getWarnStockCount() %></td>
    		<td><%=bean.getCargoInfo().getMaxStockCount() %></td>
    		<td><%=bean.getCargoInfo().getStoreTypeName() %></td>
    		<td><%=bean.getCargoInfo().getStockTypeName() %></td>
    		<td><%=bean.getCargoInfo().getTypeName() %></td>
    		<td><%=bean.getCargoInfo().getStatusName() %></td>
    		<td><%if(bean.getCargoInfo().getRemark().length()>3){ %>
						<%=bean.getCargoInfo().getRemark().substring(0,3) %>...
					<%}else{ %>
						<%=bean.getCargoInfo().getRemark() %>
					<%} %>
			</td>
			<td><%=operCodeList.size() %></td>
			<%if(hasOperList.equals("1")){ %>
				<td>
				<%for(int j=0;j<statusNameList.size();j++){ %>
					<%=statusNameList.get(j).toString() %>
					<%if(j!=statusNameList.size()-1){ %><br/><%} %>
				<%} %>
    			</td>
    			<td>
				<%for(int j=0;j<operCodeList.size();j++){ %>
					<%=operCodeList.get(j).toString() %>
					<%if(j!=operCodeList.size()-1){ %><br/><%} %>
				<%} %>
				</td>
    		<%} %>
    	</tr>
    <%} %>
</table>