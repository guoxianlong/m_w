<%@page import="adultadmin.bean.cargo.CargoProductStockBean"%>
<%@page import="java.util.*"%>
<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%
	int exchangeId = StringUtil.toInt(request.getParameter("exchangeId"));
	int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
	int stockType = StringUtil.toInt(request.getParameter("stockType"));
	int targetStockArea = StringUtil.toInt(request.getParameter("targetStockArea"));
	int targetStockType = StringUtil.toInt(request.getParameter("targetStockType"));
	int index = 0;
	HashMap cpsListMap = (HashMap)request.getAttribute("cpsListMap");
%>
<html>
<title>小店后台管理 - 搜索产品</title>
<script>
function addItem(){
	var inputs = document.getElementsByTagName("input");
	var selectCount = 0;
	for(var i=0; i<inputs.length; i++){
		if(inputs[i].type == "checkbox" && inputs[i].checked){
			var pId = inputs[i].value;
			var soc = document.getElementById("stockOutCount" + pId);
			if(soc == null || isNaN(parseInt(soc.value)) || parseInt(soc.value) < 1){
				alert("必须输入调拨量");
				return false;
			}
			selectCount++;
		}
	}
	if(selectCount > 0){
		document.forms[0].submit();
	} else {
		alert("必须选择调拨商品");
		return false;
	}
}

function selectProduct(checkObj, productId, stockCount){
	if(checkObj != null){
		if(checkObj.checked){
			var inputObj = document.getElementById("stockOutCount" + productId);
			if(inputObj == null){
				return false;
			} else {
				inputObj.disabled = false;
				inputObj.value = stockCount;
				document.getElementById("cpsOut" + productId).disabled = false;
				document.getElementById("cpsOut" + productId).options[0].selected=true;
			}
		} else {
			var inputObj = document.getElementById("stockOutCount" + productId);
			if(inputObj == null){
				return false;
			} else {
				inputObj.value = 0;
				inputObj.disabled = true;
				document.getElementById("cpsOut" + productId).disabled = true;
			}
		}
	}
}

function selectCargo(checkObj, productId, stockCount){
	if(checkObj != null){
		if(checkObj.checked){
			var inputObj = document.getElementById("stockOutCount" + productId);
			if(inputObj == null){
				return false;
			} else {
				inputObj.disabled = false;
				inputObj.value = stockCount;
				document.getElementById("cpsOut" + productId).disabled = false;
			}
		} else {
			var inputObj = document.getElementById("stockOutCount" + productId);
			if(inputObj == null){
				return false;
			} else {
				inputObj.value = 0;
				inputObj.disabled = true;
				document.getElementById("cpsOut" + productId).disabled = true;
			}
		}
	}
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
          <br/><form method="post" action="./productStock/addStockExchangeItem2.jsp" name="productForm">
          <input type="hidden" name="exchangeId" value="<%= exchangeId %>" />
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr><td colspan="21" align="right"><input type="button" value="确认添加选定产品" onclick="addItem();return false;" /></td></tr>
            <tr bgcolor="#4688D6">
              <td align="center" rowspan="3"><font color="#FFFFFF">选项</font></td>
              <td align="center" rowspan="3"><font color="#FFFFFF">编号</font></td>
              <td rowspan="3"><font color="#FFFFFF">原名称</font></td>
              <td align="center" colspan="16"><font color="#FFFFFF">库存数量</font></td>
              <td align="center" rowspan="3"><font color="#FFFFFF">调拨量</font></td>
              <td align="center" rowspan="3"><font color="#FFFFFF">状态</font></td>
              <%if(cpsListMap!=null){ %>
              <td align="center" rowspan="3"><font color="#FFFFFF">源货位</font></td>
              <%} %>
              <%--
              <td align="center" rowspan="3"><font color="#FFFFFF">操作</font></td>
              --%>
            </tr>
            <tr bgcolor="#4688D6">
              <td align="center" colspan="3"><font color="#FFFFFF">待验库</font></td>
              <td align="center" colspan="3"><font color="#FFFFFF">合格库</font></td>
              <td align="center" colspan="3"><font color="#FFFFFF">退货库</font></td>
              <td align="center" colspan="3"><font color="#FFFFFF">返厂库</font></td>
              <td align="center" colspan="2"><font color="#FFFFFF">维修库</font></td>
              <td align="center" colspan="2"><font color="#FFFFFF">残次品库</font></td>
            </tr>
            <tr bgcolor="#4688D6">
              <td align="center"><font color="#FFFFFF">北京</font></td>
              <td align="center"><font color="#FFFFFF">芳村</font></td>
              <td align="center"><font color="#FFFFFF">增城</font></td>
              <td align="center"><font color="#FFFFFF">芳村</font></td>
              <td align="center"><font color="#FFFFFF">广速</font></td>
              <td align="center"><font color="#FFFFFF">增城</font></td>
              <td align="center"><font color="#FFFFFF">北京</font></td>
              <td align="center"><font color="#FFFFFF">芳村</font></td>
              <td align="center"><font color="#FFFFFF">增城</font></td>
              <td align="center"><font color="#FFFFFF">北京</font></td>
              <td align="center"><font color="#FFFFFF">芳村</font></td>
              <td align="center"><font color="#FFFFFF">增城</font></td>
              <td align="center"><font color="#FFFFFF">芳村</font></td>
              <td align="center"><font color="#FFFFFF">增城</font></td>
              <td align="center"><font color="#FFFFFF">芳村</font></td>
              <td align="center"><font color="#FFFFFF">增城</font></td>
            </tr>
<logic:present name="productList" scope="request"> 
<logic:iterate name="productList" id="item" > 
<%
	adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;
	List cpsList = null;
	if(cpsListMap != null){
		cpsList = (List)cpsListMap.get(String.valueOf(voItem.getId()));
	}
	index++;
%>
		<tr bgcolor="<%= (index % 2 != 0)?"#E9EDEE":"#CDE6F1" %>">
		<td><input type="checkbox" name="productId" value="<%= voItem.getId() %>" onclick="selectProduct(this, <%= voItem.getId() %>, <%= cpsList==null?0:cpsList.size()==0?0:((CargoProductStockBean)cpsList.get(0)).getStockCount() %>);" /></td>
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_BJ && stockType==ProductStockBean.STOCKTYPE_CHECK){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_BJ && targetStockType==ProductStockBean.STOCKTYPE_CHECK){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_GF && stockType==ProductStockBean.STOCKTYPE_CHECK){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_GF && targetStockType==ProductStockBean.STOCKTYPE_CHECK){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_ZC && stockType==ProductStockBean.STOCKTYPE_CHECK){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_ZC && targetStockType==ProductStockBean.STOCKTYPE_CHECK){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_GF && stockType==ProductStockBean.STOCKTYPE_QUALIFIED){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_GF && targetStockType==ProductStockBean.STOCKTYPE_QUALIFIED){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_GS && stockType==ProductStockBean.STOCKTYPE_QUALIFIED){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_GS && targetStockType==ProductStockBean.STOCKTYPE_QUALIFIED){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_ZC && stockType==ProductStockBean.STOCKTYPE_QUALIFIED){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_ZC && targetStockType==ProductStockBean.STOCKTYPE_QUALIFIED){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_BJ && stockType==ProductStockBean.STOCKTYPE_RETURN){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_BJ && targetStockType==ProductStockBean.STOCKTYPE_RETURN){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_GF && stockType==ProductStockBean.STOCKTYPE_RETURN){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_GF && targetStockType==ProductStockBean.STOCKTYPE_RETURN){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_ZC && stockType==ProductStockBean.STOCKTYPE_RETURN){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_ZC && targetStockType==ProductStockBean.STOCKTYPE_RETURN){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_BJ && stockType==ProductStockBean.STOCKTYPE_BACK){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_BJ && targetStockType==ProductStockBean.STOCKTYPE_BACK){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_GF && stockType==ProductStockBean.STOCKTYPE_BACK){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_GF && targetStockType==ProductStockBean.STOCKTYPE_BACK){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_ZC && stockType==ProductStockBean.STOCKTYPE_BACK){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_ZC && targetStockType==ProductStockBean.STOCKTYPE_BACK){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_GF && stockType==ProductStockBean.STOCKTYPE_REPAIR){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_GF && targetStockType==ProductStockBean.STOCKTYPE_REPAIR){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_ZC && stockType==ProductStockBean.STOCKTYPE_REPAIR){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_ZC && targetStockType==ProductStockBean.STOCKTYPE_REPAIR){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_GF && stockType==ProductStockBean.STOCKTYPE_DEFECTIVE){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_GF && targetStockType==ProductStockBean.STOCKTYPE_DEFECTIVE){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=right width="50" <%if(stockArea==ProductStockBean.AREA_ZC && stockType==ProductStockBean.STOCKTYPE_DEFECTIVE){%>style="color:red;"<%}%><%if(targetStockArea==ProductStockBean.AREA_ZC && targetStockType==ProductStockBean.STOCKTYPE_DEFECTIVE){%>style="color:orange;"<%}%> ><%= voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=center width="100"><input type="text" id="stockOutCount<%= voItem.getId() %>" name="stockOutCount<%= voItem.getId() %>" value="0" size="5" disabled="disabled" /></td>
		<td align=right width="40"><bean:write name="item" property="statusName" /></td>
		<%if(cpsListMap!=null){ %>
        <td align=center width="100">
        <select id="cpsOut<%= voItem.getId() %>" name="cpsOut<%= voItem.getId() %>" disabled="disabled">
        <%
        	List list = (List)cpsListMap.get(String.valueOf(voItem.getId()));
        	if(list != null){
        		for(int i=0;i<list.size();i++){
        			CargoProductStockBean cps = (CargoProductStockBean)list.get(i);
        %>
        	<option value="<%=cps.getCargoInfo().getWholeCode() %>"><%=cps.getCargoInfo().getWholeCode() %>(<%=cps.getStockCount() %>)</option>
        <%			
        		}
        	}
        %>
        </select>
        </td>
        <%} %>
		</tr>
</logic:iterate> </logic:present> 
            <tr><td colspan="16" align="right"><input type="button" value="确认添加选定产品" onclick="addItem();return false;" /></td></tr>
          </table>
          </form>
</body>
</html>