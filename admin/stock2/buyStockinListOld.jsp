<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.buy.*, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部	
%>
<%
BuyStockinOldAction action = new BuyStockinOldAction();
action.buyStockinList(request, response);

List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");

int i, count;
BuyStockinBean bean = null;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function checkAll(name,id) {     
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name) && (el[i].id==id)){
	    el[i].checked = true;         
	}     
    } 
}

function clearAll(name) {
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name)){
	    el[i].checked = false;
	}
    }
} 
</script>
<p align="center">采购入库操作记录</p>

<form method="post" action="addBuyStockin2Old.jsp">
操作名称：<input type="text" name="name" size="50" value="<%=DateUtil.getNow().substring(0, 10)%>采购入库"/>地区：<select name="area"><option value="0">北京</option><option value="1">广东</option></select><input type="submit" value="添加采购入库记录"/><br/>
</form>

<form method="post" action="collectBuyStockin.jsp">
<table width="100%" border="1">
<tr>
  <td>选择</td>
  <td>序号</td>
  <td>名称</td>
  <td>编号</td>
<%--
  <td>来源于</td>
--%>
  <td>添加时间</td>
  <td>状态</td>
  <td>库房</td>
  <td>操作</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	bean = (BuyStockinBean) list.get(i);
%>
<tr>
  <td><input type="checkbox" name="ids" value="<%=bean.getId()%>" <%if(bean.getStatus() != BuyStockinBean.STATUS4){%>id="s1"<%}%><%if(bean.getStatus() == BuyStockinBean.STATUS4){%>id="s2"<%}%> /></td>
  <td><%=(i + 1)%></td>
  <td><a href="buyStockinOld.jsp?id=<%=bean.getId()%>"><%= StringUtil.convertNull(bean.getName()) %></a>&nbsp;</td>
  <td><a href="buyStockinOld.jsp?id=<%=bean.getId()%>"><%=StringUtil.convertNull(bean.getCode())%></a></td>
<%--
  <td><%if(bean.getBuyStock() != null){ %><a href="../stock2/buyStock.jsp?stockId=<%=bean.getBuyStockId()%>"><%=bean.getBuyStock().getName()%></a><%} else { %>无<%} %></td>
--%>
  <td><%=bean.getCreateDatetime().substring(0, 16)%></td>
  <td><%if(bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1 || bean.getStatus() == BuyStockinBean.STATUS2){%><font color="red"><%=bean.getStatusName()%></font><%}else{%><%=bean.getStatusName()%><%}%></td>
  <td><%= ProductStockBean.getAreaName(bean.getStockArea()) %></td>
  <td><a href="buyStockinOld.jsp?id=<%=bean.getId()%>">编辑</a><%if(bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1){%>|<a href="deleteBuyStockinOld.jsp?buyStockinId=<%=bean.getId()%>" onclick="return confirm('确认删除？')">删除</a><%}%><%if(group.isFlag(31)){ %><%if(group.isFlag(53)){ %>|<a href="buyStockinPrice.jsp?id=<%=bean.getId()%>" <%if(bean.getStatus() != BuyStockinBean.STATUS0 && bean.getStatus() != BuyStockinBean.STATUS1){ %>style="color: green;"<%}%> >编辑价格及代理商</a><%} %>|<%if(bean.getStatus()==BuyStockinBean.STATUS4){ %><a href="buyStockinOldPrint.jsp?id=<%=bean.getId()%>" target="_blank" style="color: green;">打印</a><%} else { %>打印<%} %>|<%if(bean.getPrintCount()>0){ %><a href="printLog.jsp?operId=<%=bean.getId()%>&type=<%= PrintLogBean.PRINT_LOG_TYPE_BUYSTOCKIN %>">打印<%= bean.getPrintCount() %>次<%}else{ %>打印次数<%} %></a><%} %>
  </td>
</tr>
<%
}
%>
</table>
<p align="center"><input type="button" name="B" onclick="javascript:checkAll('ids','s1');" value="全选处理中"/><input type="button" name="B" onclick="javascript:checkAll('ids','s2');" value="全选已完成"/><input type="button" name="B" onclick="javascript:clearAll('ids');" value="全不选"/><input type="submit" name="B" value="汇总"/></p>
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>