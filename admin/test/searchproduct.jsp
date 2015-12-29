<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="cache.CatalogCache"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.service.*,adultadmin.service.infc.*,adultadmin.service.impl.*"%>
<%@ page import="java.util.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
<html>
<title>小店后台管理 - 搜索产品</title>
<script>
function excel_product()
{
	document.productForm.action="searchproduct.do?forward=2Excel";
	return document.productForm.submit();
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="../js/pts.js"></script>
<script language="JavaScript">
function selectRadio(name,value) {
	 var radioobject = document.getElementsByName(name);
	 if(value == "")
	 {
	 	radioobject[0].checked = true;
	  	return;
	 }
	 for (var i = 0; i < radioobject.length; i++) 
	 {
	 	if(radioobject[i].value == value)
	 	{
	    	radioobject[i].checked = true;
	   		break;
	  	}
	 }
}
</script>
<body>
<%@include file="../../header.jsp"%>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<%
	String action = StringUtil.convertNull(request.getParameter("action"));
String code = StringUtil.convertNull(request.getParameter("code"));
String name = StringUtil.convertNull(request.getParameter("name"));
name = StringUtil.toWml(name);
int brand = StringUtil.StringToId(request.getParameter("brand"));
int drove = StringUtil.toInt(request.getParameter("drove"));
int size = StringUtil.toInt(request.getParameter("size"));
int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));

        if (name == null)
            name = "";
        if (code == null)
            code = "";

        String forward = StringUtil.dealParam(request.getParameter("forward"));

        String[] strProductIds = request.getParameterValues("id");

		List brandList = new ArrayList();
		List productList = new ArrayList();
		String[] droveXie = {"男","女"};
		String[] droveFu = {"男士","女士","情侣","所有人"};
		String[] sizeFu = {"S","M","L","XL","XXL","XXXL","均码"};
        IAdminService service = ServiceFactory.createAdminServiceLBJ();
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
        try {
        
        	brandList = service.getSelects("product_brand","order by id desc");

	if(!action.equals("")){
        	StringBuilder buf = new StringBuilder();
        	if(strProductIds == null || strProductIds.length == 0){
	        	if(!StringUtil.isNull(name)){
	            	name = StringUtil.toSqlLike(request.getParameter("name"));
	        		buf.append(" (a.name like '%");
	        		buf.append(name);
	        		buf.append("%' or a.oriname like '%");
	        		buf.append(name);
	        		buf.append("%') ");
	        	}
	        	if (!StringUtil.isNull(code)){
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	                buf.append(" a.code='");
	        		buf.append(code);
	        		buf.append("' ");
	        	}
	            if (brand > 0) {
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" a.brand = ");
	            	buf.append(brand);
	            }
	            if(parentId1 > 0){
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" a.parent_id1=");
	            	buf.append(parentId1);
	            }
	            if(parentId2 > 0){
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" a.parent_id2=");
	            	buf.append(parentId2);
	            }
	
	            if(!group.isFlag(89)){
	            	if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" a.status <> 100");
	            }
        	} else {
        		buf.append("a.id in (");
        		for(int i=0; i<strProductIds.length; i++){
        			if(i != 0){
        				buf.append(",");
        			}
        			buf.append(strProductIds[i]);
        		}
        		buf.append(")");
        	}

            productList = service.searchProduct(buf.toString(), 0, 0, "a.brand desc");
            if(parentId1==123||parentId1==119){
            for(int i=0;i<productList.size();i++){
            	voProduct product = (voProduct)productList.get(i);
            	voProductProperty property = service.searchProductPropertyByProductId(product.getId());
            	if(drove>=0||size>=0){
            		if(property == null){
            			productList.remove(i);
            			i--;
            		}else{
            			if(drove>=0 && property.getDrove()!=drove){
            				productList.remove(i);
            				i--;	
            			}else if(size>=0 && property.getSize()!=size){
            				productList.remove(i);
            				i--;	
            			}
            		}
            	}
            }
            }
            Map psMap = new HashMap();
            Iterator iter = productList.listIterator();
            while(iter.hasNext()){
            	voProduct product = (voProduct)iter.next();
            	List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
            	product.setPsList(psList);
            }
            request.setAttribute("productList",productList);
	}
%>
<form method=post action="searchproduct.jsp">
<input name="action" type="hidden" value="search"/>
产品编号：<input type=text name="code" size="20" value="<%=code%>"><br>
产品名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br>
<%if(true)  {%>
一级分类：<select name="parentId1" id="parentId1" class="bd" style="width:180" onChange="sredirect(this.options.selectedIndex - 1);changeProductProperty(this.options[this.options.selectedIndex].value)">
			<option value="0"></option>								  
			 <%
				HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					voCatalog catalog = (voCatalog)iter.next();
			 %>
			 <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
			<%} %>
		</select><br/>
		<script type="text/javascript">selectOption(document.getElementById('parentId1'), '<%=parentId1 %>');</script>
二级分类：<select name="parentId2" id="parentId2" class="bd" style="width:180">
	<option value="0"></option>
</select><br/>
<script type="text/javascript">selectOption(document.getElementById('parentId2'), '<%=parentId2 %>');</script>
产品品牌：<select name="brand" id="brand">
	<option value="0">全部</option>
<%
	for(int i=0;i<brandList.size();i++){
		voSelect vo = (voSelect)brandList.get(i); 
%>
	<option value="<%=vo.getId() %>"><%=vo.getName() %></option>
<%} %>
</select>
<script type="text/javascript">selectOption(document.getElementById('brand'), '<%=brand %>');</script>
<br/>
<div id="fzdrove">
适用人群：<input type="radio" name="drove" id="fzdrove2" value="0">男士   
<input type="radio" name="drove" id="fzdrove2" value="1">女士  
<input type="radio" name="drove" id="fzdrove2" value="2">情侣  
<input type="radio" name="drove" id="fzdrove2" value="3">所有人  
</div>
<script type="text/javascript">selectRadio("fzdrove2",<%=drove %>);</script>
<div id="fzsize">
号码：<input type="radio" name="size" id="fzsize2" value="6">均码
<input type="radio" name="size" id="fzsize2" value="0">S  
<input type="radio" name="size" id="fzsize2" value="1">M  
<input type="radio" name="size" id="fzsize2" value="2">L  
<input type="radio" name="size" id="fzsize2" value="3" >XL  
<input type="radio" name="size" id="fzsize2" value="4">XXL  
<input type="radio" name="size" id="fzsize2" value="5">XXXL  
</div>
<script type="text/javascript">selectRadio("fzsize2",<%=size %>);</script>
<div id="xdrove">
适用人群：<input type="radio" name="drove" id="xdrove2" value="0">男鞋  
<input type="radio" name="drove" id="xdrove2" value="1">女鞋
</div>
<script type="text/javascript">selectRadio('xdrove2',<%=drove%>);</script>
<div id="xsize">
号码：<select name="size" id="xsize2">
	<option value='-1'>全部</option>
	<option value='34'>34码</option>
    <option value='35'>35码</option>
    <option value='36'>36码</option>
    <option value='37'>37码</option>
    <option value='38'>38码</option>
    <option value='39'>39码</option>
    <option value='40'>40码</option>
    <option value='41'>41码</option>
    <option value='42'>42码</option>
    <option value='43'>43码</option>
    <option value='44'>44码</option>
    <option value='45'>45码</option>
    <option value='46'>46码</option>
</select>
</div>
<script type="text/javascript">selectOption(document.getElementById('xsize2'), '<%=size %>');</script>
<script type="text/javascript">
changeProductProperty(<%=parentId1 %>);
function changeProductProperty(id){
	if(id == 123){
		document.getElementById('xdrove').style.display="block";
		document.getElementById('xsize').style.display="block";
		document.getElementById('fzdrove').style.display="none";
		document.getElementById('fzsize').style.display="none";
	}else if(id == 119){
		document.getElementById('fzdrove').style.display="block";
		document.getElementById('fzsize').style.display="block";
		document.getElementById('xdrove').style.display="none";
		document.getElementById('xsize').style.display="none";
	}else{
		document.getElementById('fzdrove').style.display="none";
		document.getElementById('fzsize').style.display="none";
		document.getElementById('xdrove').style.display="none";
		document.getElementById('xsize').style.display="none";
	}
}
</script>
<script>
<!--
var spt = document.forms[0].parentId2
function sredirect(x){
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null;
  if(x < 0){
	return;
  }
  spt.options[0]=new Option("", "0");
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i + 1]=new Option(spts[x][i].text, spts[x][i].value)
       //alert(x+'--'+spts[x][i].text+'--'+spts[x][i].value);
  }
  spt.options[0].selected=true
}

selectOption(document.forms[0].parentId1, '<%= parentId1 %>');
sredirect(document.forms[0].parentId1.selectedIndex - 1);
selectOption(document.forms[0].parentId2, '<%= parentId2 %>');
</script>
<%} %>
<input type=submit value="查询产品">
</form>
</td></tr>
</table>
</td></tr></table>

          <br><form method=post action="" name="productForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="40" align="center"><font color="#FFFFFF">选项</font><br/><input type="checkbox" onclick="setAllCheck(this.form, 'id', this.checked )" /></td>
              <td width="60" align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
              <td width="60" align="center"><font color="#FFFFFF">团购价格</font></td>
              <td width="60" align="center"><font color="#FFFFFF">市场价</font></td>
              <td width="100" align="center"><font color="#FFFFFF">代理商</font></td>
              <td width="40" align="center"><font color="#FFFFFF">状态</font></td>
              <td width="100" align="center"><font color="#FFFFFF">品牌</font></td>
              <%if(parentId1==123||parentId1==119){ %>
              <td width="50" align="center"><font color="#FFFFFF">适用人群</font></td>
              <td width="50" align="center"><font color="#FFFFFF">号码</font></td>
              <%} %>
              <td width="50" align="center"><font color="#FFFFFF">可发货总数</font></td>
              <td width="50" align="center"><font color="#FFFFFF">库存总数</font></td>
            </tr>           
<logic:present name="productList" scope="request"> 
<logic:iterate name="productList" id="item" >
<%
	adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;
	voSelect voBrand = service.getSelect("product_brand","where id = "+voItem.getBrand());
	voProductProperty property = service.searchProductPropertyByProductId(voItem.getId());
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><input type='checkbox' name='id' value='<bean:write name="item" property="id" />'></td>
		<td align=left width="60"><a href="../fproduct.do?id=<bean:write name="item" property="id" />&simple=1" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><a href="../fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" /></a></td>
		<td align='center'><a href="../fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a></td>
		<td align=right width="60"><bean:write name="item" property="price" />元</td>
		<td align=right width="60"><bean:write name="item" property="groupBuyPrice" />元</td>
		<td align=right width="60"><bean:write name="item" property="price2" />元</td>
		<td align=left width="100"><bean:write name="item" property="proxyName" />(<bean:write name="item" property="proxysName" />)</td>
		<td align=right width="40"><bean:write name="item" property="statusName" /></td>
		<td align=center width="100"><%=voBrand==null?"-":voBrand.getName() %></td>
		<%if(parentId1==123){ %>
        <td width="50" align="center"><%=property==null?"-":droveXie[property.getDrove()] %></td>
        <td width="50" align="center"><%=property==null?"-":property.getSize() %>码</td>
        <%} %>
        <%if(parentId1==119){ %>
        <td width="50" align="center"><%=property==null?"-":droveFu[property.getDrove()] %></td>
        <td width="50" align="center"><%=property==null?"-":sizeFu[property.getSize()] %></td>
         <%} %>
		<td width="50" align="center"><%= voItem.getStock(0, 0) + voItem.getStock(1, 0) + voItem.getStock(2, 0) %></td>
		<td width="50" align="center"><%= voItem.getStock(0) + voItem.getStock(1) + voItem.getStock(2) + voItem.getLockCount(0) + voItem.getLockCount(1) + voItem.getLockCount(2) %></td>
		</tr>
</logic:iterate>
</logic:present>
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0">
            <tr>
              <td height="35">
              <input type="button" value="导出已选项" onClick="return excel_product()">
              </td>
            </tr>
          </table>
          <br> 
<%
		} catch(Exception e){
			e.printStackTrace();
		} finally {
            service.close();
            psService.releaseAll();
        }
 %>  
</body>
</html>