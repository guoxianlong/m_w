<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%><%@ page import="adultadmin.bean.order.*"%><%@ page import="adultadmin.bean.*"%><%@ page import="java.text.*"%><%@ page import="adultadmin.bean.stock.*"%><%@ page import="adultadmin.util.*"%><%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.framework.*" %>
<%@ page import="java.util.*" %>
<%!
static SimpleDateFormat sdf = new SimpleDateFormat("M月d H:mm");
%><%
    //判断有没有权限
    if(!PermissionFrk.hasPermission(request, PermissionFrk.ORDER_ADMIN)){
		return;
    }
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

PagingBean paging = (PagingBean) request.getAttribute("paging");

int stockoutStatus = StringUtil.StringToId(request.getParameter("stockoutStatus"));
int orderType = StringUtil.StringToId(request.getParameter("orderType"));
int orderType2 = StringUtil.toInt(request.getParameter("orderType2"));
voOrder vo = null;
int index = 0;
List orderTypeList = OrderTypeFrk.getOrderTypeList();
%>
<html>
<title>买卖宝后台</title>
<script>
function collect()
{
		document.orderForm.action="collect3.do";
		return document.orderForm.submit();
}
function collect4()
{
		document.orderForm.action="collect4.do";
		return document.orderForm.submit();
}
function collect5()
{
		document.orderForm.action="collect5.do";
		return document.orderForm.submit();
}
function reload(time){
	var contactTime = time.options[time.selectedIndex].value;
	if(contactTime == 0){
		window.location.href='orders.do?orderBy=contact_time&contactTime=0';
	} else if(contactTime == 1){
		window.location.href='orders.do?orderBy=contact_time&contactTime=1';
	} else if(contactTime == 2){
		window.location.href='orders.do?orderBy=contact_time&contactTime=2';
	} else if(contactTime == 3){
		window.location.href='orders.do?orderBy=contact_time&contactTime=3';
	} else if(contactTime == 4){
		window.location.href='orders.do?orderBy=contact_time&contactTime=4';
	}
}
function uniteOrder(){
	var uniteBox = document.getElementsByName("select");
	var i = 0;
	var url = "uniteOrder.do?1=1";
	if(confirm("确认要合并选中的订单吗？")){
		for(i=0; i<uniteBox.length; i++){
			if(uniteBox[i].checked){
				url += "&uniteOrderId=" + uniteBox[i].value;
			}
		}
		window.location.href=url;
	}
}
function searchOrderType(){
	var obj = document.getElementById("orderType");
	var orderType2 = <%=orderType2%>;
	var op = "" ;
	if(orderType2>-1){
		op="&orderType2="+orderType2;
	}
	window.location.href="stockoutOrders.do?stockoutStatus=4&orderType=" + obj.value+op;
}
</script>
<script type="text/javascript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%if(stockoutStatus == 4){ %>
按订单产品分类筛选：<select name="orderType" id="orderType">
	<option value="0">显示全部</option>
<%
	for(int i=0; i<orderTypeList.size(); i++){
		OrderTypeBean ot = (OrderTypeBean) orderTypeList.get(i);
%><option value="<%= ot.getTypeId() %>"><%= ot.getName() %></option>
<%} // for() %>
	<option value="9">其他订单</option>
</select>&nbsp;<input type="button" value="确定" onclick="searchOrderType();" /><br />
<script>selectOption(document.getElementById("orderType"), '<%= orderType %>')</script>
<%} // stockoutStatus == 4 %>
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%>
          <br><form method=post action="" name="orderForm" target="_blank">
          <input type=hidden name=flag value="<%=request.getParameter("flag")%>">
          <table width="99%" cellpadding="2" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="15" align="center"><font color="#FFFFFF"><%if(stockoutStatus != 4){ %>选<% } else { %>序号<%} %></font></td>
              <td align="center"><font color="#FFFFFF">订单号</font></td>
<%if(stockoutStatus == 4){ %><td align="center"><font color="#FFFFFF">订单产品分类</font></td><%} %>
              <td align="center"><font color="#FFFFFF">姓名</font></td>
              <td align="center"><font color="#FFFFFF">电话</font></td>
              <td align="center"><font color="#FFFFFF">地址</font></td>
              <td width="100" align="center"><font color="#FFFFFF">产品名称</font></td>
			  <td width="60" align="center"><font color="#FFFFFF">折扣前</font></td>
              <td width="60" align="center"><font color="#FFFFFF">折扣后</font></td>
			  <td align="center"><font color="#FFFFFF">已到款</font></td>
              <td width="55" align="center"><font color="#FFFFFF">生成<br>时间</font></td>
			  <td align="center"><font color="#FFFFFF">出货地点</font></td>
			  <td width="60" align="center"><font color="#FFFFFF">出货时间</font></td>
			  <td width="80" align="center"><font color="#FFFFFF">备注</font></td>
<%if(group.isFlag(36)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
              <td width="40" align="center"><font color="#FFFFFF">操作</font></td>
<%}%>
            </tr>           
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" > 
<%
	vo = (voOrder) item;
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center' width="10" style="padding:0px">
<%if(stockoutStatus != 4){ %>
			<input type=checkbox name="select" value="<bean:write name="item" property="id" />">
<%} else { %><%= ++index %><%} %>
		</td>
		<td align='center'><a href="order.do?id=<bean:write name="item" property="id" />" ><%if(vo.getFlat() == 1){%><font color="red"><%} else {%><font color="blue"><%}%><bean:write name="item" property="code" /></font></a></td>
<%if(stockoutStatus == 4){ %>
		<td align=left><%= OrderTypeFrk.getOrderTypeName(vo.getOrderType()) %></td>
<%} %>
		<td align=left><bean:write name="item" property="name" /><%= (((voOrder)item).getAgent()==1)?"(代理)":"" %></td>
		<td align=left><%= StringUtil.cutString(vo.getPhone(), 7) %><%if(vo.getPhone() != null && vo.getPhone().length() > 7){ %>****<%} %></td>
		<td align=left><%= StringUtil.cutString(vo.getAddress(), 4) %><%if(vo.getAddress() != null && vo.getAddress().length() > 4){ %>...<%} %></td>
		<td align=left width="100"><bean:write name="item" property="products" /></td>
		<td align=left><bean:write name="item" property="price" format="0.00"/></td>
		<td align=left><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align="center"><bean:write name="item" property="realPay" format="0.00"/></td>
		<td align=left width="55"><%=sdf.format(new java.util.Date(vo.getCreateDatetime().getTime()))%></td>
<%
		//出货记录
		OrderStockBean oper = vo.getOrderStock();
	    if(oper != null && oper.getStatus() == 2){
			String areaName = "<font color=blue>" + ProductStockBean.getAreaName(oper.getStockArea()) + "</font>";
%>
		<td align="center"><%=areaName%></td>
		<td width="60" align="center"><%=oper.getLastOperTime().substring(5, 16)%></td>
<%
		}
        else {
%>
        <td width="25" align="center">无</td>
		<td width="80" align="center">无</td>
<%
		}
%>
		<td width="80" align="center"><%= StringUtil.convertNull(vo.getRemark()) %></td>
<%if(group.isFlag(36)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
		<td align=left width="40"><a href="forder.do?id=<bean:write name="item" property="id" />" >修改</a>
		</td>
<%}%>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0" align=center>
            <tr>
              <td height="35">
<%if(stockoutStatus != 4){ %>
            <input type=checkbox onclick="setAllCheck(orderForm,'select',this.checked)">全选
<%} %>
              </td>
              <td height="35">
<%if(group.isFlag(61)){ %>
<%if(stockoutStatus != 4){ %>
            <input type=button onclick="return collect()" value="对选中的订单进行汇总">
<%} else { %>
            <input type=button onclick="return collect4()" value="全库之和可发货订单汇总">
            <input type=button onclick="return collect5()" value="全库缺货订单汇总">
<%} %>
<%} %>
              </td>
            </tr>
          </table>
          <br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%>
</body>
</html>