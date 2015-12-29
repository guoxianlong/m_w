<%@ page contentType="text/html;charset=utf-8"%>
<%@ page
	import="adultadmin.action.stock.*,java.util.*,adultadmin.bean.buy.*,adultadmin.bean.stock.*,adultadmin.action.vo.*,adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%><%@ page
	import="adultadmin.framework.*"%><%@ page import="adultadmin.bean.*"%>
<%@ page import="ormap.ProductLineMap"%>
<%!static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");%>
<%
	voUser user = (voUser) session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.util.PageUtil"%>
<%
	Stock2Action action = new Stock2Action();
	action.buyStock(request, response);

	List buyStockProductList = (List) request
			.getAttribute("buyStockProductList");
	String totalPurchasePrice = (String) request
			.getAttribute("totalPurchasePrice");
	Double taxPoint = (Double) request.getAttribute("taxPoint");
	BuyStockBean bean = (BuyStockBean) request.getAttribute("bean");
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	List proxyList = (List) request.getAttribute("proxyList");
	ArrayList errorProductList = (ArrayList) request
			.getAttribute("errorProductList");
	int i, count;
	voProduct product = null;
	BuyStockProductBean bpp = null;
	voSelect proxy = null;
	Iterator iter = null;

	boolean editPriceCheck = false;
	if (request.getAttribute("editPriceCheck") != null) {
		editPriceCheck = true;
	}
	//审核权限
	boolean shenhe = group.isFlag(58);
	boolean bianji = group.isFlag(168);
	//查看全部的计划
	boolean viewAll = group.isFlag(55);
	boolean assign = group.isFlag(60);
	boolean complete = group.isFlag(113);

	String result = (String) request.getAttribute("result");
	if ("failure".equals(result)) {
		String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
document.location = "buyStockList.jsp";

</script>
<%
	return;
	} else {
%>
<link href="<%=request.getContextPath()%>/css/global.css"
	rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../../js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script>
function complete(){
	if(confirm("确认预计到货？确认后不能撤销。")){
		return true;
	}
	return false;
}
function finish(){
	if(confirm("完成此操作后，该采购计划单将不能再操作，确定继续吗？")){
		return true;
	}
	return false;
}
function addproduct(code,pid, price, proxyId){
	document.addBuyStockForm.productCode.value=code;
	var value = 1;
	value = prompt('将添加产品编号为'+code+'的产品，请输入数量，并确认', 1);
	price = prompt('将添加产品编号为'+code+'的产品，请输入进货价，并确认', price);
	if(value != null && value > 0){
		document.addBuyStockForm.buyCount.value = value;
		document.addBuyStockForm.buyCount.purchasePrice = price;
		document.addBuyStockForm.buyCount.productProxyId = proxyId;
		document.addBuyStockForm.submit();
	}
}

function assignBuyStock(hrefObj){
	var aui = document.getElementById('assignUserId');
	var uid = aui.options[aui.selectedIndex].value;
	window.location.href = hrefObj.href + "&assignUserId=" + uid;
	return false;
}
function select(){
	document.getElementById('proxyId').value = document.getElementById('proxy').value;
}
function check(){
	var strDate = document.getElementById('signDateTime').value;
	if(strDate.length>=1){
		if(strDate.length!=10){
	   	alert("日期格式错误，请重新填写！");  
       	return false;
		}
	
 	//var reg=/^([1-2]\d{3})[\/|\-](0?[1-9]|10|11|12)[\/|\-]([1-2]?[0-9]|0[1-9]|30|31)$/ig;
 	var reg=/((^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(10|12|0?[13578])([-\/\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(11|0?[469])([-\/\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\/\._])(0?2)([-\/\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\/\._])(0?2)([-\/\._])(29)$)|(^([3579][26]00)([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][0][48])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][0][48])([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][2468][048])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][2468][048])([-\/\._])(0?2)([-\/\._])(29)$)|(^([1][89][13579][26])([-\/\._])(0?2)([-\/\._])(29)$)|(^([2-9][0-9][13579][26])([-\/\._])(0?2)([-\/\._])(29)$))/ig;
 	
 	
  	if(!reg.test(strDate)){  
        alert("日期格式错误，请重新填写！");  
        return false;  
  	}  
  	}
      return true;  
   }
		
           

</script>
<p align="center">
	预计到货表操作
</p>
<form method="post" action="editBuyStock.jsp" >
	编号：<%=bean.getCode()%>&nbsp;&nbsp;&nbsp; 状态：
	<%
	if (bean.getStatus() == BuyStockBean.STATUS0
				|| bean.getStatus() == BuyStockBean.STATUS1
				|| bean.getStatus() == BuyStockBean.STATUS4) {
%>
	<font color="red"><%=bean.getStatusName()%></font>
	<a href="completeBuyStock.jsp?stockId=<%=bean.getId()%>"
		onclick="return complete();">确认进货</a>
	<%
		} else {
	%><%=bean.getStatusName()%>
	<%
		}
	%>
	<%
		if (bean.getStatus() == BuyStockBean.STATUS3
					|| bean.getStatus() == BuyStockBean.STATUS5) {
				voUser cu = PermissionFrk.getAdminUser("id="
						+ bean.getAssignUserId());
	%>
	<%
		if (cu != null) {
	%>&nbsp;当前指派给(<%=cu.getUsername()%>)<%
		}
	%>
	<%
		if (assign) {
					List uList = PermissionFrk
							.getUserPermissionList("group_id in (10,11,18,19) order by id");
	%>&nbsp;
	<a href="assignBuyStock.jsp?stockId=<%=bean.getId()%>"
		onclick="return assignBuyStock(this);">指派给</a>
	<select name="assignUserId" id="assignUserId">
		<%
			for (i = 0; i < uList.size(); i++) {
							UserPermissionBean p = (UserPermissionBean) uList
									.get(i);
							voUser u = PermissionFrk.getAdminUser("id="
									+ p.getUserId());
		%><option value="<%=p.getUserId()%>"><%=u.getUsername()%></option>
		<%
			}
		%>
	</select>
	<%
		}
	%>
	<%
		}
	%>
	<br />
	来源于：
	<a href="buyOrder.jsp?orderId=<%=request.getAttribute("orderId")%>"><%=request.getAttribute("buyOrderCode")%></a>&nbsp;&nbsp;&nbsp;
	<a href="buyPlan.jsp?planId=<%=request.getAttribute("planId")%>"><%=request.getAttribute("buyPlanCode") == null
						? ""
						: request.getAttribute("buyPlanCode")%></a>
	<br />
	<%
		if (request.getAttribute("buyStockinList") != null) {
	%>
	已转换成的采购入库单有：
	<%
		List buyStockinList = (List) request
						.getAttribute("buyStockinList");
				for (int j = 0; j < buyStockinList.size(); j++) {
					BuyStockinBean stockin = (BuyStockinBean) buyStockinList
							.get(j);
	%>
	<a href="buyStockin.jsp?id=<%=stockin.getId()%>"><%=stockin.getCode()%></a>&nbsp;&nbsp;
	<%
		}
			}
	%>
	<br />
	备注：
	<input type="text" name="remark" size=50 value="<%=bean.getRemark()%>" />
	&nbsp;&nbsp;&nbsp;
	<input type="submit" value="修改" />
	<input type="hidden" name="stockId" value="<%=bean.getId()%>" />
	<input type="hidden" name="back" value="buyStock.jsp" />
</form>
<fieldset>
	<legend>
		操作
	</legend>
	<p align="center">
		<%
			if (group.isFlag(183)) {
		%><a
			href="buyAdminHistory.jsp?logId=<%=bean.getId()%>&logType=<%=BuyAdminHistoryBean.LOGTYPE_BUY_STOCK%>"
			target="_blank">人员操作记录</a>|<%
			}
		%><a href="buyStockList.jsp">返回预计到货表列表</a>
		<%
			if (group.isFlag(31)) {
		%>|
		<a href="buyStockPrint.jsp?stockId=<%=bean.getId()%>" target="_blank">导出列表</a>
		<%
			}
		%>
	</p>
</fieldset>
	<style type=text/css>
.redfont {
	color: red
}
</style>
<form method="post" action="editBuyStockProduct.jsp" onsubmit="return check();"/>

	<div id="auto" style="position: absolute; left: 100px; top: 222px;"></div>
	<table width="95%" border="1" style="border-collapse: collapse;"
		bordercolor="#D8D8D5">
		<tr>
			<td colspan="8" align="left">
				统一使用代理商：
				<script>document.all.proxytext.value=document.getElementById('proxy').options[document.getElementById('proxy').selectedIndex].text;</script>
				<%-- <input type="text" name="proxytext" onClick="select()"  id="word" style="width:100px;height:21px;font-size:10pt;"<%if(bean.getStatus() == BuyStockBean.STATUS6 || bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3){%> readonly<%}%>> --%>
				<%-- <span style="width:18px;border:0px solid red;"><select name="proxy" id="proxy" onchange="javascript:select();document.all.proxytext.value=document.getElementById('proxy').options[document.getElementById('proxy').selectedIndex].text" id="proxy"<%if((bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS6)&&(bianji||bean.getCreateUserId()==user.getId())){%> disabled="disabled"<%}%> style="margin-left:-100px;width:118px;"> --%>
				<input type="text" name="proxytext" onClick="select()" id="word"
					style="width: 100px; height: 20px; font-size: 10pt;" readonly />
				<span
					style="width: 18px; border: 0px solid red; margin-left: -8px; margin-bottom: -6px;"><select
						name="proxy" id="proxy"
						onchange="javascript:select();document.all.proxytext.value=document.getElementById('proxy').options[document.getElementById('proxy').selectedIndex].text"
						id="proxy" disabled="disabled"
						style="margin-left: -100px; width: 118px;">
						<option value="0"></option>
						<%
							iter = proxyList.listIterator();
								while (iter.hasNext()) {
									proxy = (voSelect) iter.next();
						%>
						<option value="<%=proxy.getId()%>"><%=proxy.getName()%></option>
						<%
							}
						%>
					</select> </span>
				<input type="hidden" name="proxyId" id="proxyId"
					value="<%=request.getAttribute("proxyId")%>" />
				<%
					if (request.getAttribute("proxyId") != null) {
				%>
				<script>selectOption(document.getElementById('proxy'), '<%=request.getAttribute("proxyId")%>');</script>
				<%
					}
				%>
				&nbsp;&nbsp;
				<%-- <%if((bean.getStatus() == BuyStockBean.STATUS0 || bean.getStatus() == BuyStockBean.STATUS1 || bean.getStatus() == BuyStockBean.STATUS4)&&(bianji||bean.getCreateUserId()==user.getId())){%><input type="submit" value="修改"/>&nbsp;&nbsp;&nbsp;<%}%> --%>
				地区：
				<select name="stockArea" <%if (bean.getStatus() > 1) {%>
					disabled="disabled" <%}%>>
					<option value="0"
						<%if (bean.getArea() == ProductStockBean.AREA_BJ) {%>
						selected=selected <%}%>>
						请选择
					</option>
					<option value="1"
						<%if (bean.getArea() == ProductStockBean.AREA_GF) {%>
						selected=selected <%}%>>
						芳村
					</option>
					<option value="3"
						<%if (bean.getArea() == ProductStockBean.AREA_ZC) {%>
						selected=selected <%}%>>
						增城
					</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				序号
			</td>
			<td>
				产品线
			</td>
			<td>
				产品编号
			</td>
			<td>
				产品名称
			</td>
			<td>
				原名称
			</td>
			<td>
				预计进货量(已入库量)
			</td>
			<!-- <%
				if (bianji || bean.getCreateUserId() == user.getId()) {
			%>
			<td>
				预计到货税前价（税后价）
			</td>
			<td>
				预计到货税前金额（税后金额）
			</td>
			<%
				}
			%> -->
			<td>
				进货前库存
			</td>
			<td>
				操作
			</td>
		</tr>
		<%
			count = buyStockProductList.size();
				int planCount = 0;
				float stockPrice = 0;
				for (i = 0; i < count; i++) {
					bpp = (BuyStockProductBean) buyStockProductList.get(i);
					product = bpp.getProduct();
		%>
		<tr <%if (i % 2 == 0) {%> bgcolor="#EEE9D9" <%}%>>
			<td
				<%if (errorProductList != null) {
						if (errorProductList.contains(Integer.valueOf(bpp
								.getProductId()))) {%>
				class=redfont <%}
					}%>><%=(i + 1)%></td>
			<td><%=bpp.getProductLineName()%></td>
			<td>
				<a href="../fproduct.do?id=<%=bpp.getProduct().getId()%>"
					target="_blank"><%=bpp.getProduct().getCode()%></a>
			</td>
			<td>
				<a href="../fproduct.do?id=<%=bpp.getProduct().getId()%>"
					target="_blank"><%=bpp.getProduct().getName()%></a>
			</td>
			<td>
				<a href="../fproduct.do?id=<%=bpp.getProduct().getId()%>"
					target="_blank"><%=bpp.getProduct().getOriname()%></a>
			</td>
			<td>
				<input type="text"
					<%if (errorProductList != null) {
						if (errorProductList.contains(Integer.valueOf(bpp
								.getProductId()))) {%>
					class=redfont <%}
					}%> name="buyCount<%=bpp.getProduct().getId()%>"
					size="5" value="<%=bpp.getBuyCount()%>"
					<%if ((bean.getStatus() == BuyStockBean.STATUS2
							|| bean.getStatus() == BuyStockBean.STATUS3 || bean
							.getStatus() == BuyStockBean.STATUS6)
							&& (bianji || bean.getCreateUserId() == user
									.getId())) {%>
					readonly <%}%> />
				(<%=bpp.getStockinCount()%>)
			</td>
			<!-- <%
				if (bianji || bean.getCreateUserId() == user.getId()) {
			%>
			<td>
				<input type="text" name="purchasePrice<%=bpp.getProduct().getId()%>"
					size="7" value="<%=df.format(bpp.getPurchasePrice())%>"
					<%if ((bean.getStatus() == BuyStockBean.STATUS2
								|| bean.getStatus() == BuyStockBean.STATUS3
								|| bean.getStatus() == BuyStockBean.STATUS6 || editPriceCheck)
								&& (bianji || bean.getCreateUserId() == user
										.getId())) {%>
					readonly <%}%> />
				(<%=df.format(Arith.mul(bpp.getPurchasePrice(),
										Arith.add(1, taxPoint.doubleValue())))%>)
			</td>
			<td><%=df.format(bpp.getPurchasePrice()
										* bpp.getBuyCount())%>(<%=df.format(Arith.mul(bpp.getPurchasePrice(),
										Arith.mul(bpp.getBuyCount(), Arith.add(
												1, taxPoint.doubleValue()))))%>)
				<%
				stockPrice = stockPrice + bpp.getPurchasePrice()
									* bpp.getBuyCount();
			%>
				<%
					}
				%>
				 -->
				<td>
					<%
						if (bean.getArea() == 0) {
					%>
					<%=bpp.getProduct().getStock(
										ProductStockBean.AREA_BJ,
										ProductStockBean.STOCKTYPE_QUALIFIED)
								+ bpp.getProduct().getLockCount(
										ProductStockBean.AREA_BJ,
										ProductStockBean.STOCKTYPE_QUALIFIED)%>
					<%
						} else if (bean.getArea() == 1) {
					%>
					<%=bpp.getProduct().getStock(
										ProductStockBean.AREA_GF,
										ProductStockBean.STOCKTYPE_QUALIFIED)
								+ bpp.getProduct().getLockCount(
										ProductStockBean.AREA_GF,
										ProductStockBean.STOCKTYPE_QUALIFIED)
								+ bpp.getProduct().getStock(
										ProductStockBean.AREA_GS,
										ProductStockBean.STOCKTYPE_QUALIFIED)
								+ bpp.getProduct().getLockCount(
										ProductStockBean.AREA_GS,
										ProductStockBean.STOCKTYPE_QUALIFIED)%>
					<%
						}
					%>
				</td>
				<td>
					<%
						if ((bean.getStatus() == BuyStockBean.STATUS0
										|| bean.getStatus() == BuyStockBean.STATUS1 || bean
										.getStatus() == BuyPlanBean.STATUS4)
										&& (bianji || bean.getCreateUserId() == user
												.getId())) {
					%><a
						href="deleteBuyStockProduct.jsp?stockId=<%=bean.getId()%>&productId=<%=bpp.getProduct().getId()%>"
						onclick="return confirm('确认删除？');">删除</a>
					<%
						}
					%>
				</td>
		</tr>
		<%
			}
		%>
		<tr>
			<td></td>
			<td>
				合计
			</td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<!-- <td></td>
			<td><%=df.format(stockPrice)%>(<%=df.format(Arith.mul(stockPrice, Arith.add(1, taxPoint
								.doubleValue())))%>)
			</td> -->
			<td></td>
			<td></td>
		</tr>
	</table>
	<br />
	物流公司：
	<select name="expressCompany" id="expressCompany" onchange="select()">
		<option value=""></option>
		<option value="德邦物流">
			德邦物流
		</option>
		<option value="富奎物流">
			富奎物流
		</option>
		<option value="建达物流">
			建达物流
		</option>
		<option value="市场取货">
			市场取货
		</option>
		<option value="其他">
			其他
		</option>
	</select>
	<%
		if (bean.getExpressCompany() != null) {
	%>
	<script>selectOption(document.getElementById('expressCompany'), '<%=bean.getExpressCompany()%>');</script>
	<%
		}
	%>&nbsp; 物流单号：
	<input type="text" name="expressCode" size=20
		value="<%=StringUtil.convertNull(bean.getExpressCode())%>" />
	&nbsp; 预计运费：
	<input type="text" name="portage" value="<%=bean.getPortage()%>"
		size="4" />
	&nbsp; 预计到货时间：
	<input type="text" name="expectArrivalDatetime"
		value="<%=StringUtil.convertNull(StringUtil.cutString(bean
								.getExpectArrivalDatetime(), 10))%>"
		readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');" />
	
	<%
		if (group.isFlag(565)) {
		String sign=StringUtil.convertNull(StringUtil.cutString(bean
									.getSignDateTime(), 10));
		
	%>
	签收时间:
	<input type="text" name="signDateTime" id='signDateTime'
		value="<%=sign%>"
		 onclick="SelectDate(this,'yyyy-MM-dd');" />
	<select name="hour" id="hour">
		<option value="00">00
		</option>
		<option value="01">01
		</option>
		<option value="02">02
		</option>
		<option value="03">03
		</option>
		<option value="04">04
		</option>
		<option value="05">05
		</option>
		<option value="06">06
		</option>
		<option value="07">07
		</option>
		<option value="08">08
		</option>
		<option value="09">09
		</option>
		<option value="10">10
		</option>
		<option value="11">11
		</option>
		<option value="12">12
		</option>
		<option value="13">13
		</option>
		<option value="14">14
		</option>
		<option value="15">15
		</option>
		<option value="16">16
		</option>
		<option value="17">17
		</option>
		<option value="18">18
		</option>
		<option value="19">19
		</option>
		<option value="20">20</option>
		<option value="21">21</option>
		<option value="22">22</option>
		<option value="23">23</option>
	</select>
	时
	<select name="minute" id="minute">
		<option value="00">
			00
		</option>
		<option value="01">
			01
		</option>
		<option value="02">
			02
		</option>
		<option value="03">
			03
		</option>
		<option value="04">
			04
		</option>
		<option value="05">
			05
		</option>
		<option value="06">
			06
		</option>
		<option value="07">
			07
		</option>
		<option value="08">
			08
		</option>
		<option value="09">
			09
		</option>
		<option value="10">
			10
		</option>
		<option value="11">
			11
		</option>
		<option value="12">
			12
		</option>
		<option value="13">
			13
		</option>
		<option value="14">
			14
		</option>
		<option value="15">
			15
		</option>
		<option value="16">
			16
		</option>
		<option value="17">
			17
		</option>
		<option value="18">
			18
		</option>
		<option value="19">
			19
		</option>
		<option value="20">
			20
		</option>
		<option value="21">
			21
		</option>
		<option value="22">
			22
		</option>
		<option value="23">
			23
		</option>
		<option value="24">
			24
		</option>
		<option value="25">
			25
		</option>
		<option value="26">
			26
		</option>
		<option value="27">
			27
		</option>
		<option value="28">
			28
		</option>
		<option value="29">
			29
		</option>
		<option value="30">
			30
		</option>
		<option value="31">
			31
		</option>
		<option value="32">
			32
		</option>
		<option value="33">
			33
		</option>
		<option value="34">
			34
		</option>
		<option value="35">
			35
		</option>
		<option value="36">
			36
		</option>
		<option value="37">
			37
		</option>
		<option value="38">
			38
		</option>
		<option value="39">
			39
		</option>
		<option value="40">
			40
		</option>
		<option value="41">
			41
		</option>
		<option value="42">
			42
		</option>
		<option value="43">
			43
		</option>
		<option value="44">
			44
		</option>
		<option value="45">
			45
		</option>
		<option value="46">
			46
		</option>
		<option value="47">
			47
		</option>
		<option value="48">
			48
		</option>
		<option value="49">
			49
		</option>
		<option value="50">
			50
		</option>
		<option value="51">
			51
		</option>
		<option value="52">
			52
		</option>
		<option value="53">
			53
		</option>
		<option value="54">
			54
		</option>
		<option value="55">
			55
		</option>
		<option value="56">
			56
		</option>
		<option value="57">
			57
		</option>
		<option value="58">
			58
		</option>
		<option value="59">
			59
		</option>

	</select>
	分
	<%
		String hour = "";
				String minute = "";
				if (StringUtil.convertNull(bean.getSignDateTime()).length() == 16) {
					String date = StringUtil.convertNull(bean
							.getSignDateTime());
					hour = date.substring(11, 13);
					minute = date.substring(14, 16);
	%>
	<script>selectOption(document.getElementById('hour'), '<%=hour%>');</script>
	<script>selectOption(document.getElementById('minute'), '<%=minute%>');</script>
	<%
		}
			}else{
			%>
			签收时间:<%= StringUtil.convertNull(bean.getSignDateTime()).equals("")?"无":StringUtil.convertNull(bean.getSignDateTime())%></p>
			<%
			}
	%>

	<%
		if (bianji || bean.getCreateUserId() == user.getId()) {
	%><p align="center">
		<input type="submit" value="修改" />
	</p>
	<%
		}
	%>
	<input type="hidden" name="stockId" value="<%=bean.getId()%>" />
	<input type="hidden" name="pageIndex"
		value="<%=paging.getCurrentPageIndex()%>" />
</form>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;",
								"pageIndex", 10)%></p>
<%
	}
%>
<%
	session.removeAttribute("errorProductList");
%>