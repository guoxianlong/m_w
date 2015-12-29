<%@page import="cache.ProductLinePermissionCache"%>
<%@page import="cache.CatalogCache"%>
<%@page import="adultadmin.action.vo.voCatalog"%><%@ include
	file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.bean.supplier.*"%>
<%@ page import="java.util.*"%>
<%@page import="adultadmin.action.vo.*"%>
<%
	voUser adminUser = (voUser) session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();

	boolean isSystem = (adminUser.getSecurityLevel() == 10); //系统管理员
	boolean isGaojiAdmin = (adminUser.getSecurityLevel() == 9); //高级管理员
	boolean isAdmin = (adminUser.getSecurityLevel() == 5); //普通管理员

	boolean isPingtaiyunwei = (adminUser.getPermission() == 8); //平台运维部
	boolean isXiaoshou = (adminUser.getPermission() == 7); //销售部
	boolean isShangpin = (adminUser.getPermission() == 6); //商品部
	boolean isTuiguang = (adminUser.getPermission() == 5); //推广部
	boolean isYunyingzhongxin = (adminUser.getPermission() == 4); //运营中心
	boolean isKefu = (adminUser.getPermission() == 3); //客服部	

	adultadmin.action.vo.voProduct product = (adultadmin.action.vo.voProduct) request
			.getAttribute("product");
	List psList = (List) request.getAttribute("psList");
	List clothesSizeList = (List) request
			.getAttribute("clothesSizeList");//服装尺码
	List shoesSizeList = (List) request.getAttribute("shoesSizeList");//鞋号码
	int i = 0;
	int simple = StringUtil.StringToId(request.getParameter("simple"));
	String columnURL = StringUtil.convertNull((String) request
			.getAttribute("previewURL"));//预览产品页面的URL地址
	SupplierStandardInfoBean supplier = (SupplierStandardInfoBean)request.getAttribute("supplier");
%>

<%@page import="adultadmin.framework.IConstants"%><html>
<title>买卖宝后台</title>
<link href="../css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="../js/jquery.js"></script>
<script language="JavaScript" src="js/pts.js"></script>
<script language="JavaScript" src="js/count.js"></script>
<script language="JavaScript">
function selectRadio(name,value) {
	 var radioobject = document.getElementsByName(name);
	 if(value.length==0)
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

//查看历史销量
function queryBuyCount(){
	$.post("<%=request.getContextPath()%>/admin/buyCount.do",
		{"id":<%= product.getId() %>},
		function(data){
			var historyBuyCount = eval(data);
			$("#buyCount").val(historyBuyCount);
			$("#buyCount").css("display","inline-block");
		}
	);
}
</script>
<%
	if (simple == 1) {
%>
<body>
<%@include file="../header.jsp"%>
<script>
//检查优惠价格
function checkPrice(obj){
	var procode=$("#procode").val();
	var p=$(obj).val();
	var price=eval("("+p+")");
	$.ajax({
		url: 'completeProductInfo.do?method=getDiscountPrice',
		 data: {"productCode":procode},
		 type: "POST",
		 success: function(response) {
			 var disprice=eval("("+response+")");
			 if(disprice>=price&&disprice>0){
				 alert("更新失败，优惠活动商品买卖宝价要大于优惠价！");
				 $(obj).focus().select();
				 return;
			 }
		}, error: function(a) {
			   alert("更新失败");
		}
   });
}

//获取优惠价格
function checkSubPrice(){
	var disprice=-1;  //优惠价格
	var procode=$("#procode").val();
	$.ajax({
		url: 'completeProductInfo.do?method=getDiscountPrice',
		 data: {"productCode":procode},
		 type: "POST",
		 success: function(response) {
			 disprice=response;
			 var disp=eval("("+disprice+")");
			 var price=eval("("+$("#proprice").val()+")");
			if(disp>=price&&disp>0){
				alert("更新失败，优惠活动商品买卖宝价要大于优惠价！");
				return;
			}else{
				document.forms.mproductForm.submit();
			}
		}, error: function(a) {
			   alert("更新失败");
		}
 });
	
}
</script>
<form name="mproductForm" method="post" action="mproductstatus.do">
<input type=hidden name=id
	value="<bean:write name="product" property="id"/>">
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8">
	<tr>
		<td>
		<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">编号：</td>
				<td bgcolor="#F8F8F8"><input type="text" size="20" id="procode"
					maxlength="16" disabled
					value="<bean:write name="product" property="code"/>">
				&nbsp;条形码：<input type="text" size="20" maxlength="15" disabled
					value="<logic:present name="barcodeVO"><bean:write name="barcodeVO" property="barcode"/></logic:present>">
				<%
					if (group.isFlag(293)) {
				%><logic:present name="barcodeVO">&nbsp;<input
						type="button" value="打印条码"
						onclick="onPrintSubmit('<bean:write name="product" property="code"/>','<bean:write name="product" property="oriname"/>','<bean:write name="barcodeVO" property="barcode"/>')" />
				</logic:present>
				<%
					}
				%>
				</td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">原名称：</td>
				<td bgcolor="#F8F8F8">
				<%
					if (group.isFlag(306)) {
				%> <textarea name="oriname" rows="2"
					cols="30"><bean:write name="product" property="oriname" /></textarea>
				<%
					} else {
				%> <bean:write name="product" property="oriname" /> <input
					name="oriname" type="hidden"
					value="<bean:write name="product" property="oriname"/>" /> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">添加时间：</td>
				<td bgcolor="#F8F8F8"><bean:write name="product"
					property="createDatetime" format="yyyy-MM-dd kk:mm:ss" /></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">库存标准(北京)：</td>
				<td bgcolor="#F8F8F8"><input name="stockStandardBj" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockStandardBj"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">警戒线(北京)：</td>
				<td bgcolor="#F8F8F8"><input name="stockLineBj" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockLineBj"/>"></td>
			</tr>
			<%--
				<tr> 
					<td height="30" align="center" bgcolor="#F8F8F8">库存(广东)：</td>
					<td bgcolor="#F8F8F8"><input name="stockGd" type="text" maxlength="16" size="20" readonly value="<bean:write name="product" property="stockGd"/>" ></td>
				</tr>
--%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">库存标准(广东)：</td>
				<td bgcolor="#F8F8F8"><input name="stockStandardGd" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockStandardGd"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">警戒线(广东)：</td>
				<td bgcolor="#F8F8F8"><input name="stockLineGd" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockLineGd"/>"></td>
			</tr>
			<%
				for (int j = 0; j < psList.size(); j++) {
						ProductStockBean ps = (ProductStockBean) psList.get(j);
						if (ps.getType() == ProductStockBean.STOCKTYPE_NIFFER
								|| ps.getType() == ProductStockBean.STOCKTYPE_QUALITYTESTING) {
							continue;
						}
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8"><%=ProductStockBean.getAreaName(ps.getArea())%><%=ProductStockBean.getStockTypeName(ps.getType())%>库存：</td>
				<td bgcolor="#F8F8F8"><%=ps.getStock()%>
				<%
					if (ps.getLockCount() > 0) {
				%>(<a
					href="lockOrderInfo.do?productId=<%=product.getId()%>&area=<%=ps.getArea()%>&type=<%=ps.getType()%>"><%=ps.getLockCount()%></a>)<%
					}
				%>
				</td>
			</tr>
			<%
				}
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">小店名称：</td>
				<td bgcolor="#F8F8F8"><input name="name" type="text" size="20"
					maxlength="25" value="<bean:write name="product" property="name"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">买卖宝价：</td>
				<td bgcolor="#F8F8F8"><input name="price" type="text" size="20" onchange="checkPrice(this)"
					maxlength="16" id="proprice"
					value="<bean:write name="product" property="price"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">原价：</td>
				<td bgcolor="#F8F8F8"><input name="price4" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price4"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">团购价：</td>
				<td bgcolor="#F8F8F8"><input name="groupBuyPrice" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="groupBuyPrice"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">市场价：</td>
				<td bgcolor="#F8F8F8"><input name="price2" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price2"/>"></td>
			</tr>
			<%
				if (group.isFlag(41)&&ProductLinePermissionCache.hasProductPermission(adminUser,product)) /*if(isSystem || (isShangpin && isGaojiAdmin) || isYunyingzhongxin)*/{
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">批发价：</td>
				<td bgcolor="#F8F8F8"><input name="price3" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price3"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">库存价格：</td>
				<td bgcolor="#F8F8F8"><input name="price5" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price5"/>"></td>
			</tr>
			<%
				} else {
					
			%>
			<input name="price3" type="hidden"
				value="<bean:write name="product" property="price3"/>">
			<input name="price5" type="hidden"
				value="<bean:write name="product" property="price5"/>">
			<%
				}
			%>
			<%if(group.isFlag(561)){ %>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">历史销量：</td>
				<td bgcolor="#F8F8F8"><input name="buyCount" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="buyCount"/>" readonly></td>
			</tr>
			<%} %>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">产品等级：</td>
				<td bgcolor="#F8F8F8"><input name="rank" type="text" size="20"
					maxlength="16" value="<bean:write name="product" property="rank"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">显示顺序：</td>
				<td bgcolor="#F8F8F8"><input name="displayOrder" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="displayOrder"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">TOP100 显示顺序：</td>
				<td bgcolor="#F8F8F8"><input name="topOrder" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="topOrder"/>"></td>
			</tr>
			<%-- 添加分类 2009-12-18 lee--%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">分类：</td>
				<td bgcolor="#F8F8F8">
				<table>
					<tr>
						<td width="20%" height="25" align="right" class=forumRow><span><span><font
							color="#FF0000">*</font></span></span>第一层产品类别：</td>
						<td width="80%" height="25" class=forumRow><select
							name="parentId1" class="bd" style="width: 180"
							onChange="sredirect(this.options.selectedIndex);isTipInfo();">
							<%
								HashMap map = (HashMap) CatalogCache.catalogLevelList.get(0);
									List list = (List) map.get(Integer.valueOf(0));
									Iterator iter = list.listIterator();
									while (iter.hasNext()) {
										voCatalog catalog = (voCatalog) iter.next();
							%>
							<option value="<%=catalog.getId()%>"><%=catalog.getName()%></option>
							<%
								}
							%>
						</select></td>
					</tr>
					<tr>
						<td width="30%" height="25" align="right" class=forumRow>
						第二层产品类别：</td>
						<td height="25" class=forumRow><select name="parentId2"
							class="bd" style="width: 180"
							onChange="tredirect(this.options.selectedIndex);isTipInfo();"></select></td>
					</tr>
					<tr>
						<td width="30%" height="25" align="right" class=forumRow>
						第三层产品类别：</td>
						<td height="25" class=forumRow><select name="parentId3"
							class="bd" style="width: 180"></select></td>
					</tr>
				</table>
				<script>
<!-- 此处的二三级分类名称都保存在pts.js的三维数组中,根据对应的顶级父类索引依次循环得出-->
var flag=<%=request.getParameter("flag")%>;
var spt = document.forms[0].parentId2
function sredirect(x){
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i]=new Option(spts[x][i].text, spts[x][i].value)
     // alert(x+'--'+spts[x][i].text+'--'+spts[x][i].value);
  }
  spt.options[0].selected=true
  tredirect(0);
}

var tpt = document.forms[0].parentId3
function tredirect(y){
  x = document.forms[0].parentId1.options.selectedIndex
  for (m = tpt.options.length - 1; m >= 0; m --)
      tpt.options[m] = null
  for (i = 0; i < tpts[x][y].length; i ++){
      tpt.options[i]=new Option(tpts[x][y][i].text, tpts[x][y][i].value)
  }
  tpt.options[0].selected=true;
}

with(document.forms[0].parentId1)
	for(var i = 0; i < length; i++){
		if(options[i].value == '<bean:write name="product" property="parentId1"/>'){
			options[i].selected = true;
			break;
		}
	} 
sredirect(i);
with(document.forms[0].parentId2)
	for(var i = 0; i < length; i++){
		if(options[i].value == '<bean:write name="product" property="parentId2"/>'){
			options[i].selected = true;
			break;
		}
	}
tredirect(i);
with(document.forms[0].parentId3)
	for(var i = 0; i < length; i++){
		if(options[i].value == '<bean:write name="product" property="parentId3"/>'){
			options[i].selected = true;
			break;
		}
	}  
//修改分类提示
function isTipInfo(){
	if(flag==0)
		alert("当前条形码是由系统生成，涉及一层产品类别和二层产品类别，若修改产品类别，请重新生成条形码！");
}	
</script>
					</td>
                  </tr><%--添加分类结束 --%>
				<tr> 
					<td height="30" align="center" bgcolor="#F8F8F8">进货状态：</td>
					<td bgcolor="#F8F8F8">
						<select name="stockStatus" class="bd">
						      <option value="0">未进货</option>
						      <option value="1">进货中</option>
						</select>
					</td>
					<script>selectOption(mproductForm.stockStatus, '<bean:write name="product" property="stockStatus"/>')</script>
				</tr>
				<tr> 
					<td height="30" align="center" bgcolor="#F8F8F8">产品状态：</td>
					<td bgcolor="#F8F8F8">
						<select name="status" class="bd" style="width:180">
						<logic:present name="statusList" scope="request"><logic:iterate name="statusList" id="statusItem" >
						<option value="<bean:write name="statusItem" property="id" />"><bean:write name="statusItem" property="name" /></option>
						</logic:iterate></logic:present> 
						</select>
						<script>selectOption(mproductForm.status, '<bean:write name="product" property="status"/>')</script>
					</td>
				</tr>
<%if(group.isFlag(74)){%>
				<tr> 
					<td height="35" colspan="2" align="center" bgcolor="#F8F8F8">
						<input type="button" value=" 确 定 " onclick="checkSubPrice()">
						<input type="reset" value=" 清 除 ">
					</td>
				</tr>
<%}%>
			</table></td>
		</tr>
	</table>
</form>
</body>
<%
	} else {
%>
<body>
<%
	if (request.getAttribute("tip") != null) {
%>
<script type="text/javascript">
window.alert('<%=request.getAttribute("tip")%>');
</script>
<%
	}
%>
<script>
//检查优惠价格
function checkPrice(){
	var procode=$("#procode").val();
	var p=$("#proprice").val();
	var price=eval("("+p+")");
	$.ajax({
		url: 'completeProductInfo.do?method=getDiscountPrice',
		 data: {"productCode":procode},
		 type: "POST",
		 success: function(response) {
			 var disprice=eval("("+response+")");
			 if(disprice>=price&&disprice>0){
				 alert("更新失败，优惠活动商品买卖宝价要大于优惠价！");
				 $("#proprice").focus().select();
				 return;
			 }
		}, error: function(a) {
			   alert("更新失败");
		}
 });
}
//获取优惠价格
function checkSubPrice(){
	var disprice=-1;  //优惠价格
	var procode=$("#procode").val();

	$.ajax({
		url: 'completeProductInfo.do?method=getDiscountPrice',
		 data: {"productCode":procode},
		 type: "POST",
		 success: function(response) {
			 disprice=response;
			 var disp=eval("("+disprice+")");
			 var price=eval("("+$("#proprice").val()+")");
			if(disp>=price&&disp>0){
				alert("更新失败，优惠活动商品买卖宝价要大于优惠价！");
				return;
			}else if(checksubmit()==true){
				document.forms.mproductForm.submit();
			}
		}, error: function(a) {
			   alert("更新失败");
		}
 });
	
}
var flag=<%=request.getParameter("flag")%>;
function checksubmit()
{   
	//if($("#word").val().length==0){alert("代理商不能为空!");return false;}
	//if($("#url3").val().length==0){alert("页面地址不能为空!");return false;}
	with(mproductForm){
		if(name.value.length==0){
			alert("名称不能为空！");
			return false;
		}
		if(price.value.length==0){
			alert("价格不能为空！");
			return false;
		}
		if(price2.value.length==0){
			alert("市场价不能为空！");
			return false;
		}
		/*
		if(intro.value.length==0){
			alert("介绍不能为空！");
			return false;
		}
		if(chanpinzhongliang.value.length==0){
			alert("产品重量不能为空！");
			return false;
		}
		if(baozhuangzhongliang.value.length==0){
			alert("产品包装重量不能为空！");
			return false;
		}
		*/
	}
	if(flag==0){
		isTipInfo();
	}
	return true;
}

function display(module, field){
	var B = document.getElementById(module);
	var C = document.getElementById(field);
	if(B.style.display == 'none'){
		B.style.display = 'block';
		C.rows = 8;
	}
	else {
		B.style.display = 'none';
		if(field == "guanggao" || field == "fuwuchengnuo" || field == "baozhuangzhongliang" || field == "baozhuangdaxiao" || field == "changshang" || field == "baozhiqi" || field == "chucangfangfa" || field == "pizhunwenhao"){
		    C.rows = 1;
		}
		else if(field == "shiyongrenqun" || field == "zhuyishixiang" || field == "tebietishi" || field == "intro2" || field == "chanpinchengfen" || field == "changshangjieshao"){
			C.rows = 3;
		}
		else if(field == "intro" || field == "gongxiao" || field == "shiyongfangfa"){
			C.rows = 5;
		}
	}	
}

function checkSubmit(mForm){
	<logic:equal name="product" property="isPackage" value="1">
	var isProduct = mForm.isPackage.value;
	if(isProduct != 1){
		return confirm("产品状态已经从“套装产品”改为“普通产品”，如果继续操作，将删除原有的套装产品信息；继续吗？");
	} else {
		return true;
	}
	</logic:equal>
	<logic:equal name="product" property="isPackage" value="0">
	return true;
	</logic:equal>
}

function checkLength(which) {
	document.getElementById("introLength").innerHTML = which.value.length;
}

</script>
<%@include file="../header.jsp"%>
<form name="mproductForm" method="post" action="mproduct.do"
	ENCTYPE="multipart/form-data" onsubmit="return checkSubmit(this);">
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8">
	<tr>
		<td>
		<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">编号：</td>
				<td bgcolor="#F8F8F8"><input type="text" size="20" id="procode"
					maxlength="16" disabled
					value="<bean:write name="product" property="code"/>">
				&nbsp;条形码：<input type="text" size="20" maxlength="15" disabled
					value="<logic:present name="barcodeVO"><bean:write name="barcodeVO" property="barcode"/></logic:present>">
				<%
					if (group.isFlag(293)) {
				%><logic:present name="barcodeVO">&nbsp;<input
						type="button" value="打印条码"
						onclick="onPrintSubmit('<bean:write name="product" property="code"/>','<bean:write name="product" property="oriname"/>','<bean:write name="barcodeVO" property="barcode"/>');" />
				</logic:present>
				<%
					}
				%> &nbsp; &nbsp;<a
					href="productLog.jsp?id=<%=product.getId()%>&code=<%=product.getCode()%>"
					target="_blank"> <font size="3">人员操作记录</font> </a></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">原名称：</td>
				<td bgcolor="#F8F8F8">
				<%
					if (group.isFlag(306)) {
				%> <textarea name="oriname" rows="2"
					cols="30"><bean:write name="product" property="oriname" /></textarea>
				<%
					} else {
				%> <bean:write name="product" property="oriname" /> <input
					name="oriname" type="hidden"
					value="<bean:write name="product" property="oriname"/>" /> <%
 	}
 %>
				&nbsp; &nbsp;<%
 	if (group.isFlag(302)) {
 %><a
					href="makeProductPage.jsp?id=<%=product.getId()%>"><font
					size="3"> 产品页面生成</font></a>
				<%
					}
				%><a href="http://www.mmb.cn/wap/shop/product.do?fst=1&id=<%=product.getId()%>" target="_blank"><font size="3">预览商品</font></a>
				<a href="productImages.do?method=selectProductImagesListByProductId&fst=1&productId=<%=product.getId()%>" target="_blank"><font size="3">商品图库</font></a></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">添加时间：</td>
				<td bgcolor="#F8F8F8"><bean:write name="product"
					property="createDatetime" format="yyyy-MM-dd kk:mm:ss" /></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">可订购量：</td>
				<td bgcolor="#F8F8F8"><input name="qcbs" type="text" size="5"
					maxlength="5" value="<%=request.getAttribute("qcbs")%>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">库存标准(北京)：</td>
				<td bgcolor="#F8F8F8"><input name="stockStandardBj" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockStandardBj"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">警戒线(北京)：</td>
				<td bgcolor="#F8F8F8"><input name="stockLineBj" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockLineBj"/>"></td>
			</tr>
			<%--
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">库存(广东)：</td>
                    <td bgcolor="#F8F8F8"><input name="stockGd" type="text" size="20" maxlength="16" 
                    value="<bean:write name="product" property="stockGd"/>" readonly></td>
                  </tr>
--%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">库存标准(广东)：</td>
				<td bgcolor="#F8F8F8"><input name="stockStandardGd" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockStandardGd"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">警戒线(广东)：</td>
				<td bgcolor="#F8F8F8"><input name="stockLineGd" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="stockLineGd"/>"></td>
			</tr>
			<%
				for (int j = 0; j < psList.size(); j++) {
						ProductStockBean ps = (ProductStockBean) psList.get(j);
						if (ps.getType() == ProductStockBean.STOCKTYPE_NIFFER
								|| ps.getType() == ProductStockBean.STOCKTYPE_QUALITYTESTING) {
							continue;
						}
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8"><%=ProductStockBean.getAreaName(ps.getArea())%><%=ProductStockBean.getStockTypeName(ps.getType())%>库存：</td>
				<td bgcolor="#F8F8F8"><%=ps.getStock()%>
				<%
					if (ps.getLockCount() > 0) {
				%>(<a
					href="lockOrderInfo.do?productId=<%=product.getId()%>&area=<%=ps.getArea()%>&type=<%=ps.getType()%>"><%=ps.getLockCount()%></a>)<%
					}
				%>
				</td>
			</tr>
			<%
				}
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">订货在途量：</td>
				<td bgcolor="#F8F8F8"><%=((Integer) request.getAttribute("buyCountGD"))
						.intValue()%></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">小店名称：</td>
				<td bgcolor="#F8F8F8"><input name="name" type="text" size="20"
					maxlength="25" value="<bean:write name="product" property="name"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">买卖宝价：</td>
				<td bgcolor="#F8F8F8"><input name="price" type="text" size="20" id="proprice"
					maxlength="16" onchange="checkPrice(this);"
					value="<bean:write name="product" property="price" />"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">原价：</td>
				<td bgcolor="#F8F8F8"><input name="price4" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price4"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">团购价：</td>
				<td bgcolor="#F8F8F8"><input name="groupBuyPrice" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="groupBuyPrice"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">市场价：</td>
				<td bgcolor="#F8F8F8"><input name="price2" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price2"/>"></td>
			</tr>
			<%
				if (group.isFlag(41)&&ProductLinePermissionCache.hasProductPermission(adminUser,product)) /*if(isSystem || (isShangpin && isGaojiAdmin) || isYunyingzhongxin)*/{
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">批发价：</td>
				<td bgcolor="#F8F8F8"><input name="price3" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price3"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">库存价格：</td>
				<td bgcolor="#F8F8F8"><input name="price5" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="price5"/>"></td>
			</tr>
			<%
				} else {
			%>
			<input name="price3" type="hidden"
				value="<bean:write name="product" property="price3"/>">
			<input name="price5" type="hidden"
				value="<bean:write name="product" property="price5"/>">
			<%
				}
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">历史销量：</td>
				<td bgcolor="#F8F8F8">
					<a href="#" onclick="queryBuyCount()">查看</a>
					<input id="buyCount" name="buyCount" type="text" size="16" maxlength="5" style="display:none;width:100px" readonly/>
				</td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">产品等级：</td>
				<td bgcolor="#F8F8F8"><input name="rank" type="text" size="20"
					maxlength="16" value="<bean:write name="product" property="rank"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">显示顺序：</td>
				<td bgcolor="#F8F8F8"><input name="displayOrder" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="displayOrder"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">TOP100 显示顺序：</td>
				<td bgcolor="#F8F8F8"><input name="topOrder" type="text"
					size="20" maxlength="16"
					value="<bean:write name="product" property="topOrder"/>"></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">进货状态：</td>
				<td bgcolor="#F8F8F8"><select name="stockStatus" class="bd">
					<option value="0">未进货</option>
					<option value="1">进货中</option>
				</select></td>
				<script>selectOption(mproductForm.stockStatus, '<bean:write name="product" property="stockStatus"/>')</script>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">是否套装：</td>
				<td bgcolor="#F8F8F8"><logic:equal name="inPackage"
					value="false" scope="request">
					<select name="isPackage" class="bd">
						<option value="0">不是套装</option>
						<option value="1">是套装</option>
					</select>
					<script>selectOption(mproductForm.isPackage, '<bean:write name="product" property="isPackage"/>')</script>
				</logic:equal> <logic:equal name="inPackage" value="true" scope="request">
					<logic:equal name="product" property="isPackage" value="1">是套装</logic:equal>
					<logic:equal name="product" property="isPackage" value="0">不是套装</logic:equal>&nbsp;&nbsp;(该商品包含在套装内，不能再设置为套装)
							<input type="hidden" name="isPackage"
						value="<bean:write name="product" property="isPackage"/>" />
				</logic:equal> <logic:equal name="product" property="isPackage" value="1">
					<input type="button" value="编辑套装内的产品"
						onclick="window.location.href='productPackage.do?parentId=<bean:write name="product" property="id"/>';" />
				</logic:equal></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">是否显示礼包商品：</td>
				<td bgcolor="#F8F8F8"><select name="showPackage" class="bd">
					<option value="1">显示</option>
					<option value="0">不显示</option>
				</select></td>
				<script>selectOption(mproductForm.showPackage, '<bean:write name="product" property="showPackage"/>')</script>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">是否有赠品：</td>
				<td bgcolor="#F8F8F8"><select name="hasPresent" class="bd">
					<option value="0">没有赠品</option>
					<option value="1">有赠品</option>
				</select> <logic:equal name="product" property="hasPresent" value="1">
					<input type="button" value="编辑赠品包内的产品"
						onclick="window.location.href='productPresent.do?parentId=<bean:write name="product" property="id"/>';" />
				</logic:equal></td>
				<script>selectOption(mproductForm.hasPresent, '<bean:write name="product" property="hasPresent"/>')</script>
			</tr>
			<%
				if (group.isFlag(19)) {
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">父子关系：</td>
				<td bgcolor="#F8F8F8"><input type="button" value="编辑商品的父子关系"
					onclick="window.location.href='productRelation.do?parentId=<bean:write name="product" property="id"/>';" />
				</td>
			</tr>
			<%
				}
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">单位：</td>
				<td bgcolor="#F8F8F8"><select name="unit" class="bd">
					<option value="件">件</option>
					<option value="套">套</option>
					<option value="个" selected>个</option>
					<option value="盒">盒</option>
					<option value="瓶">瓶</option>
					<option value="支">支</option>
				</select></td>
				<script>selectOption(mproductForm.unit, '<bean:write name="product" property="unit"/>')</script>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">北京进货周期：</td>
				<td bgcolor="#F8F8F8"><input type="text" name="bjStockin"
					value="<bean:write name="product" property="bjStockin" />" /></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">广州进货周期：</td>
				<td bgcolor="#F8F8F8"><input type="text" name="gdStockin"
					value="<bean:write name="product" property="gdStockin" />" /></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">分类：</td>
				<td bgcolor="#F8F8F8">
				<table>
					<tr>
						<td width="20%" height="25" align="right" class=forumRow><span><span><font
							color="#FF0000">*</font></span></span>第一层产品类别：</td>
						<td width="80%" height="25" class=forumRow><select
							name="parentId1" class="bd" style="width: 180"
							onChange="sredirect(this.options.selectedIndex);changeProductProperty(this.options[this.options.selectedIndex].value)">
							<%
								HashMap map = (HashMap) CatalogCache.catalogLevelList.get(0);
									List list = (List) map.get(Integer.valueOf(0));
									Iterator iter = list.listIterator();
									while (iter.hasNext()) {
										voCatalog catalog = (voCatalog) iter.next();
							%>
							<option value="<%=catalog.getId()%>"><%=catalog.getName()%></option>
							<%
								}
							%>
						</select></td>
					</tr>
					<tr>
						<td width="30%" height="25" align="right" class=forumRow>
						第二层产品类别：</td>
						<td height="25" class=forumRow><select name="parentId2"
							class="bd" style="width: 180"
							onChange="tredirect(this.options.selectedIndex);"></select></td>
					</tr>
					<tr>
						<td width="30%" height="25" align="right" class=forumRow>
						第三层产品类别：</td>
						<td height="25" class=forumRow><select name="parentId3"
							class="bd" style="width: 180"></select></td>
					</tr>
				</table>
				<script>
<!-- 此处的二三级分类名称都保存在pts.js的三维数组中,根据对应的顶级父类索引依次循环得出-->
var spt = document.forms[0].parentId2
function sredirect(x){
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i]=new Option(spts[x][i].text, spts[x][i].value)
     // alert(x+'--'+spts[x][i].text+'--'+spts[x][i].value);
  }
  spt.options[0].selected=true
  tredirect(0);
}

var tpt = document.forms[0].parentId3
function tredirect(y){
  x = document.forms[0].parentId1.options.selectedIndex
  for (m = tpt.options.length - 1; m >= 0; m --)
      tpt.options[m] = null
  for (i = 0; i < tpts[x][y].length; i ++){
      tpt.options[i]=new Option(tpts[x][y][i].text, tpts[x][y][i].value)
  }
  tpt.options[0].selected=true;
}

with(document.forms[0].parentId1)
	for(var i = 0; i < length; i++){
		if(options[i].value == '<bean:write name="product" property="parentId1"/>'){
			options[i].selected = true;
			break;
		}
	} 
sredirect(i);
with(document.forms[0].parentId2)
	for(var i = 0; i < length; i++){
		if(options[i].value == '<bean:write name="product" property="parentId2"/>'){
			options[i].selected = true;
			break;
		}
	}
tredirect(i);
with(document.forms[0].parentId3)
	for(var i = 0; i < length; i++){
		if(options[i].value == '<bean:write name="product" property="parentId3"/>'){
			options[i].selected = true;
			break;
		}
	}  
</script></td>
			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">品牌：</td>
				<td bgcolor="#F8F8F8"><select name="brand" class="bd"
					style="width: 180">
					<option value="0">其他</option>
					<logic:present name="brandList" scope="request">
						<logic:iterate name="brandList" id="brandItem">
							<option value="<bean:write name="brandItem" property="id" />"><bean:write
								name="brandItem" property="name" /></option>
						</logic:iterate>
					</logic:present>
				</select> <script>selectOption(mproductForm.brand, '<bean:write name="product" property="brand"/>')</script>
				</td>
			</tr>
			<tr>
				<!-- <script>function checkProxyName(){if($("#word").val().length==0){alert("代理商不能为空!");return false;}}</script> -->
				<td height="30" align="center" bgcolor="#F8F8F8" >代理商：</td>
				<td bgcolor="#F8F8F8">
					<bean:write name="product" property="proxyName"/>
					<input type="hidden" name="supplierId" value="<%=product.getProxyId() %>">
					<%--<input type="text" name="proxy"
					onClick="select()" id="word" onblur="checkProxyName()"
					style="width: 100px; height: 21px; font-size: 10pt;"><span
					style="width: 18px; border: 0px solid red;"> <select
					name="proxyx" class="bd" id="proxyx"
					style="margin-left: -100px; width: 118px;"
					onChange="document.all.proxy.value=document.getElementById('proxyx').options[document.getElementById('proxyx').selectedIndex].text">
					<logic:present name="proxyList" scope="request">
						<logic:iterate name="proxyList" id="proxyItem">
							<option value="<bean:write name="proxyItem" property="id" />"><bean:write
								name="proxyItem" property="name" /></option>
						</logic:iterate>
					</logic:present>
				</select><script>selectOption(mproductForm.proxyx, '<bean:write name="product" property="proxyId"/>')</script>
				</span>
				<div id="auto"></div>
				 --%>
				</td>
			</tr>
			<%--
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">代理商候选：</td>
				<td bgcolor="#F8F8F8"><select name="proxys" class="bd"
					style="width: 180" multiple size="13">
					<logic:present name="proxyList" scope="request">
						<logic:iterate name="proxyList" id="proxyItem">
							<option value="<bean:write name="proxyItem" property="name" />"><bean:write
								name="proxyItem" property="name" /></option>
						</logic:iterate>
					</logic:present>
				</select> <script>
var proxys='<bean:write name="product" property="proxysName"/>';
var proxys2 = proxys.split(',');
for(i = 0;i < proxys2.length;i++)
	selectOption(mproductForm.proxys,proxys2[i])
</script></td>
			</tr>--%>
			<tr>
				<td align="center" bgcolor="#F8F8F8">备注：</td>
				<td bgcolor="#F8F8F8" valign="top"><textarea name="remark" cols="30"
					rows="5" wrap="VIRTUAL"></textarea>&nbsp;&nbsp;备注记录：<textarea name="remark" cols="50"
					rows="5" wrap="VIRTUAL" readonly="true"><bean:write name="product"
					property="remark"/></textarea>
					<br>
				</td>
			</tr>
			<%
					boolean flagShangJia = ((product.getFlag() & 1) == 1);
					boolean flagXiajia = ((product.getFlag() & 1) == 0);
					boolean flagparent = ((product.getFlag() & 2) == 0);
					boolean flagchild = ((product.getFlag() & 2) == 2);
			%>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">产品上架：</td>
				<td height="30" bgcolor="#F8F8F8"><input name="productShangjia"
					type="radio" <%=flagShangJia ? "checked='checked'" : ""%> value="1">&nbsp;制作完成&nbsp;<input
					name="productShangjia" type="radio" value="0"
					<%=flagXiajia ? "checked='checked'" : ""%>> &nbsp;未制作完成</td>

			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8"></td>
				<td height="30" bgcolor="#F8F8F8"><input name="productParent"
					type="radio" <%=flagparent ? "checked='checked'" : ""%> value="0">&nbsp;父商品&nbsp;<input
					name="productParent" type="radio" value="1"
					<%=flagchild ? "checked='checked'" : ""%>> &nbsp;子商品</td>

			</tr>
			<tr>
				<td height="30" align="center" bgcolor="#F8F8F8">产品状态：</td>
				<td bgcolor="#F8F8F8"><select name="status" class="bd"
					style="width: 180">
					<logic:present name="statusList" scope="request">
						<logic:iterate name="statusList" id="statusItem">
							<option value="<bean:write name="statusItem" property="id" />"><bean:write
								name="statusItem" property="name" /></option>
						</logic:iterate>
					</logic:present>
				</select> <script>selectOption(mproductForm.status, '<bean:write name="product" property="status"/>')</script>
				</td>
			</tr>
			<%
				if (group.isFlag(74)) {
			%>
			<tr>
				<td height="35" colspan="2" align="center" bgcolor="#F8F8F8"><input
					type="button" value=" 确 定 " onclick="checkSubPrice();">
				<input type="reset" value=" 清 除 "></td>
			</tr>
			<%
				}
			%>
		</table>
		</td>
	</tr>
</table>
<br>
<input type="hidden" name="id"
	value="<bean:write name="product" property="id"/>" /> <input
	type="hidden" name="pic"
	value="<bean:write name="product" property="pic"/>" /> <input
	type=hidden name=pic2
	value="<bean:write name="product" property="pic2"/>" /> <input
	type=hidden name=pic3
	value="<bean:write name="product" property="pic3"/>" /></form>
<%@include file="../footer.jsp"%>
</body>
<SCRIPT LANGUAGE="JavaScript">
	var _insertText0 = function(A){
		var B = document.getElementById('content');
		B.focus();
		var C = document.selection.createRange();
		if(C){
			if(C.text.length > 0) C.text += A;
			else C.text= A;
            C.select();
		}
		checkLength(B);
	}

	var _insertLink0 = function(){
		var url = document.forms[0].linkUrl.value;
		var title = document.forms[0].linkTitle.value;
		if(url == '' || title == ''){
			return;
		}
		var A = 'TAGSTARTa href="' + url + '"TAGEND' + title + 'TAGSTART/aTAGEND';
		var B = document.getElementById('content');
		B.focus();
		var C = document.selection.createRange();
		if(C){
			if(C.text.length > 0) C.text += A;
			else C.text= A;
            C.select();
		}
		checkLength(B);
	}

    checkLength(document.getElementById('content'));

	var _insertText = function(field, A){
		var B = document.getElementById(field);
		B.focus();
		var C = document.selection.createRange();
		if(C){
			if(C.text.length > 0) C.text += A;
			else C.text= A;
            C.select();
		}
	}

	var _insertLink = function(field, urlField, titleField){
		var url = document.getElementById(urlField).value;
		var title = document.getElementById(titleField).value;
		if(url == '' || title == ''){
			return;
		}
		var A = 'TAGSTARTa href="' + url + '"TAGEND' + title + 'TAGSTART/aTAGEND';
		var B = document.getElementById(field);
		B.focus();
		var C = document.selection.createRange();
		if(C){
			if(C.text.length > 0) C.text += A;
			else C.text= A;
            C.select();
		}
	}
</SCRIPT>
<%
	}
%>
<script type="text/javascript">
//提交表单打印条码
function onPrintSubmit(code,oriname,barcode){
	document.getElementById("codeId").value=code;
	document.getElementById("orinameId").value=oriname;
	document.getElementById("barcodeId").value=barcode;
	document.getElementById("printBarocdeFormId").submit();
}
</script>
<form name="printBarocdeForm" id="printBarocdeFormId" action="barcodeManager/printProcBarcode.jsp" method="post"  target="_blank">
   		<input id="codeId" type="hidden"  name="code"/>
   		<input id="orinameId" type="hidden"  name="oriname"/>
   		<input id="barcodeId" type="hidden"  name="barcode"/>
   		<input type="hidden" name="pageTitle" value="打印产品条码"> 
   </form>
<%--} --%>
</html>