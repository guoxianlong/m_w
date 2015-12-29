<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();

	boolean isSystem = (adminUser.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (adminUser.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (adminUser.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (adminUser.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (adminUser.getPermission() == 7);	//销售部
	boolean isShangpin = (adminUser.getPermission() == 6);	//商品部
	boolean isTuiguang = (adminUser.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (adminUser.getPermission() == 4);	//运营中心
	boolean isKefu = (adminUser.getPermission() == 3);	//客服部	
%>
<html>
<title>买卖宝后台</title>
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
  returnText   +=   objTbl.rows[r].cells[c].innerText   +   "\t";   
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
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
共有<bean:write name="count"/>件 <input type="button" onclick="javascript:exportList();" value="导出列表"/><br/>
          <table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
              <tr bgcolor="#4688D6">              
              <td align="center"><font color="#FFFFFF">编号</font></td>
			  <td align="center"><font color="#FFFFFF">一级分类</font></td>
			  <td align="center"><font color="#FFFFFF">二级分类</font></td>
			  <td align="center"><font color="#FFFFFF">原名称</font></td>
			  <td align="center"><font color="#FFFFFF">小店名称</font></td>
			  <td align="center"><font color="#FFFFFF">库存（北京）</font></td>
			  <td align="center"><font color="#FFFFFF">库存标准（北京）</font></td>
			  <td align="center"><font color="#FFFFFF">警戒线（北京）</font></td>
			  <td align="center"><font color="#FFFFFF">库存（广东）</font></td>
			  <td align="center"><font color="#FFFFFF">库存标准（广东）</font></td>
			  <td align="center"><font color="#FFFFFF">警戒线（广东）</font></td>
			  <td align="center"><font color="#FFFFFF">价格</font></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
			  <td align="center"><font color="#FFFFFF">批发价</font></td>
<%}%>
              <td align="center"><font color="#FFFFFF">等级</font></td>
              <td align="center"><font color="#FFFFFF">包装重量</font></td>
              <td align="center"><font color="#FFFFFF">产品重量</font></td>
              <td align="center"><font color="#FFFFFF">代理商</font></td>
              <td align="center"><font color="#FFFFFF">状态</font></td>
			  <td align="center"><font color="#FFFFFF">距上次<br />出货天数</font></td>
			  <td align="center"><font color="#FFFFFF">库存记录</font></td>
            </tr>
<logic:present name="productList" scope="request"> 
<logic:iterate name="productList" id="item" > 
<%adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;%>
		<tr bgcolor='#F8F8F8'>		
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<%
		adultadmin.action.vo.voCatalog c1 = voItem.getParent1();
		adultadmin.action.vo.voCatalog c2 = voItem.getParent2();
		%>
		<td align='center'><%=(c1 == null ? "无" : c1.getName())%></td>
		<td align='center'><%=(c2 == null ? "无" : c2.getName())%></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" /></a></td>
		<td align='center'><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align='center'><bean:write name="item" property="stockStandardBj" /></td>
		<td align='center'><bean:write name="item" property="stockLineBj" /></td>
		<td align='center'><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align='center'><bean:write name="item" property="stockStandardGd" /></td>
		<td align='center'><bean:write name="item" property="stockLineGd" /></td>
		<td align='center'><bean:write name="item" property="price" /></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='center'><bean:write name="item" property="price3" /></td>
<%}%>
		<td align='center'><bean:write name="item" property="rank" /></td>
		<td align='right'><bean:write name="item" property="baozhuangzhongliang" /></td>
		<td align='right'><bean:write name="item" property="chanpinzhongliang" /></td>
		<td align=right width="40"><bean:write name="item" property="proxyName" /></td>
		<td align=right width="40"><bean:write name="item" property="statusName" /></td>
		<td align=right width="40"><% if(voItem.getTopOrder()==-1){ %>&#8734;<%}else{ %><%= voItem.getTopOrder() %><%} %></td>
		<td align="center"><a href="stock/productStockHistory.jsp?id=<bean:write name="item" property="id" />">查</a></td>
		</tr>
</logic:iterate> </logic:present> 
          </table>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
          <br>   
        </td>
    </tr>
  </table>
</body>
</html>