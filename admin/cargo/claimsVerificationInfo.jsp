<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*,adultadmin.bean.stock.*,adultadmin.bean.bybs.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	
	String claimsCode = StringUtil.convertNull(request.getParameter("claimsCode"));
	String createTime1 = StringUtil.convertNull(request.getParameter("createTime1"));
	String createTime2 = StringUtil.convertNull(request.getParameter("createTime2"));
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
	String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
	String packageCode = StringUtil.convertNull(request.getParameter("packageCode"));
	String recordNum = (String)request.getAttribute("recordNum");
	int deliver = ProductWarePropertyService.toInt(request.getParameter("deliver"));
	int status = ProductWarePropertyService.toInt(request.getParameter("status"));
	int hasGift = ProductWarePropertyService.toInt(request.getParameter("hasGift"));
	int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
	String bsCode = StringUtil.convertNull(request.getParameter("bsCode"));
	List list = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptionsAll(wareArea);
	voUser user = (voUser)request.getSession().getAttribute("userView");
 	int userid = user.getId();
 	UserGroupBean group = user.getGroup();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>理赔核销单列表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		function printTarget() {
		
		}
		function deleteClaimsAsk( id ) {
			if( !window.confirm("你确定要删除这个理赔单？") ) {
				return;
			}
			window.location="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=deleteClaimsVerification&id="+id;
			return;
		}
		function printClaims(id) {
			window.open("<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id="+id+"&print=1");
			return;
		}
		function checkIsBs(obj) {
			var code = obj.value;
			
			var pattern = /^BS{1}[0-9]{12}$/;
   			if( code != null && code != "" ) {
   				if( pattern.exec(code) ) {
   				} else {
	   				alert("您所填写的报损单号，不正确或不完整！");
	   				obj.value="";
			   		obj.focus();
   				}
   			}
		}
		
		function auditClaims(yesno){
				var list = $(".auditClaims");
				var hasGift = 0;
				var params= "";
				for( var i = 0; i < list.length; i++ ) {
					var checked = $(".auditClaims").eq(i).attr("checked");
					if( checked ) {
						var id = $(".auditClaims").eq(i).val();
						var status = $("#cv_status_"+id).val();
						if( status != "1") {
							alert("存在已勾选的理赔单的状态不是已提交审核,请重新勾选！");
							return;
						}
						var gift = $("#cv_gift_"+id).val();
						if( gift == "1" ) {
							hasGift = 1;
						}
						params+="&auditClaimsId="+id;
					}
				}
				if( params.length == 0 ) {
					alert("没有勾选任何理赔单！");
					return;
				}
				params += "&yesno="+yesno;
				if ( hasGift == 1 ) {
					if( window.confirm("勾选的理赔核销单中含有包含赠品的单子，您确认要审核理赔核销单吗？")) {
		   				window.location= "<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=auditClaimsVerificationBatch"+params;
		   				return;
		   			} else {
		   				return;
		   			}
				} else {
					if( window.confirm("你确认要审核这些理赔单么？")) {
		   				window.location= "<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=auditClaimsVerificationBatch"+params;
		   				return;
		   			} else {
		   				return;
		   			}
				}
		}
		
		function checkAll() {
			var list = $(".auditClaims");
			var checked = $("#allCheck").attr("checked");
			for( var i = 0; i < list.length; i++ ) {
				
				if( checked ) {
					$(".auditClaims").eq(i).attr({"checked": true});
				} else {
					$(".auditClaims").eq(i).removeAttr("checked");
				}
			}
		}
	</script>
	
</head>
<body>
<div align="center">
<h2>理赔核销</h2>
</div>
<div style="float:right">共有（<%= recordNum %>）条记录&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </div>
<br/>
<div align="center">
<div style="border-style:solid;border-color:#000000;border-width:1px;width:80%">
	<form action="claimsVerificationAction.do?method=getClaimsVerificationInfo" method="post">
	<table>
	
		<tr><td align="left">
	理赔单号：
	<input type="text" size="13" name="claimsCode" id="claimsCode" value="<%= claimsCode%>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	添加时间：
	<input type="text" size="10" name="createTime1" id="createTime1" value="<%= createTime1%>" onclick="WdatePicker();"/> &nbsp;到 &nbsp;
	<input type="text" size="10" name="createTime2" id="createTime2" value="<%= createTime2%>" onclick="WdatePicker();"/> 
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	
		
	产品编号：
		<input type="text" size="13" name="productCode" id="productCode" value="<%= productCode%>" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	</tr>
	<tr>
	<td>
	订单号：
		<input type="text" size="13" name="orderCode" id="orderCode" value="<%= orderCode%>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;
	包裹单号：
		<input type="text" name="packageCode" id="packageCode" size="13"  value="<%= packageCode%>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		快递公司：
		<select name="deliver" >
			<option value='-1'>请选择</option>
			<%
				Iterator itr = voOrder.deliverMapAll.keySet().iterator();
				for( ; itr.hasNext() ; ) {
					String key = (String) itr.next();
			%>
			<option value="<%= key%>" <%= deliver == Integer.parseInt(key) ? "selected" : ""%>>
			<%= voOrder.deliverMapAll.get(""+key)%>
			</option>
			<%
				}
			%>
		</select>
		</td></tr>
		<tr><td align="left">
	状态：
		<select name="status" >
			<option value="-1" <%= status == -1 ? "selected" : ""%>>请选择</option>
			<option value="0" <%= status == 0 ? "selected" : ""%>>未处理</option>
			<option value="1" <%= status == 1 ? "selected" : ""%>>已提交</option>
			<option value="2" <%= status == 2 ? "selected" : ""%>>审核不通过</option>
			<option value="3" <%= status == 3 ? "selected" : ""%>>审核通过</option>
			<option value="4" <%= status == 4 ? "selected" : ""%>>已完成</option>
			
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	报损单号：   
	<input type="text" name="bsCode" id="bsCode" size="11" value="<%= bsCode%>" onchange="checkIsBs(this);"/>	
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    库地区：
    	<%= wareAreaSelectLable%>
		&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;
		有无赠品：
		<select name="hasGift">
			<option value="-1"  <%= hasGift == -1 ? "selected" : ""%> >请选择</option>
			<option value="1"  <%= hasGift == 1 ? "selected" : ""%> >有赠品</option>
			<option value="0"  <%= hasGift == 0 ? "selected" : ""%>>无赠品</option>
		</select>
		<input type="submit"  value="  查 询   " />
		</td></tr>
		</table>
		</form>
	</div>
	<br/>
	<div align="center"><button onclick="javascript:window.location='<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=toAddClaimsVerification';">添加理赔单</button></div>
	<br/>
	<button onclick='auditClaims(1);'>审核通过</button>&nbsp;&nbsp;&nbsp;<button onclick='auditClaims(0);'>审核不通过</button>
	<br/>
	<table align="center" width="98%" border="0" cellspacing="1px" bgcolor="#D8D8D5" cellpadding="1px" >
		<tbody>
		<tr bgcolor="#484891" >
			<td align="center">
			<font color="#FFFFFF">全选<input type="checkbox" id="allCheck" onClick="checkAll();" /></font>
			</td>
			<td align="center">
			<font color="#FFFFFF">序号</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">理赔单号</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">订单号</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">包裹单号</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">快递公司</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">发货时间</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">添加人</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">添加时间</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">理赔方式</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">理赔金额</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">有无赠品</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">地区</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">状态</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">报损单</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">操作</font>
			</td>
		</tr>
		<% if( list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++ ) {
			
				ClaimsVerificationBean cvBean = (ClaimsVerificationBean) list.get(i);
		%>
			<tr bgcolor="<%= i%2 == 0 ? "#EEE9D9" : "#FFFFFF"%>" >
			<td align="center">
				<input type="checkbox" class="auditClaims" id="checkCV_<%= cvBean.getId() %>" name="auditClaimsId" value="<%=cvBean.getId() %>" />
				<input type="hidden" id="cv_status_<%= cvBean.getId() %>" value="<%=cvBean.getStatus() %>" />
				<input type="hidden" id="cv_gift_<%= cvBean.getId() %>" value="<%=cvBean.getHasGift() %>" />
			</td>
			<td align="center">
				<%= i + 1 %>
			</td>
			<td align="center">
				<a href="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=<%= cvBean.getId()%>"  target="_black"><%= cvBean.getCode()%> </a>
			</td>
			<td align="center">
			<%= cvBean.getOrderCode()%>
			</td>
			<td align="center">
			<%= cvBean.getPackageCode()%>
			</td>
			<td align="center">
			<%= cvBean.getDeliverCompanyName()%>
			</td>
			<td align="center">
			<%= cvBean.getDeliverDate()%>
			</td>
			<td align="center">
			<%= cvBean.getCreateUserName()%>
			</td>
			<td align="center">
			<%= StringUtil.convertNull(StringUtil.cutString(cvBean.getCreateTime(),19))%>
			</td>
			<td align="center">
			<%= cvBean.getType() == 0 ? "整单理赔" : cvBean.getType() == 1 ? "按sku理赔" : cvBean.getType() == 2 ? "运费3倍理赔" : cvBean.getType() == 3 ? "包装理赔" : cvBean.getType() == 4 ? "单品理赔（含包装）" :""%>
			</td>
			<td align="center" id="price<%=cvBean.getId() %>" name="price">
			<%= cvBean.getPrice()%>
			</td>
			<td align="center" id="hasGift<%=cvBean.getId() %>" name="hasGift">
			<%= cvBean.getHasGift()==0 ? "无" : cvBean.getHasGift()==1 ? "有":""%>
			</td>
			<td align="center">
			<%= StringUtil.convertNull((String)(ProductStockBean.areaMap.get(new Integer(cvBean.getWareArea()))))%>
			</td>
			<td align="center" id="status<%=cvBean.getId() %>" name="status">
			<%= cvBean.getStatusName()%>
			</td>
			<td>
			<%
				List<BsbyOperationnoteBean> bsbyList = cvBean.getBsbyList();
				for ( int j =0; j < bsbyList.size(); j ++ ) { 
					BsbyOperationnoteBean boBean = bsbyList.get(j);
				if( boBean.getIf_del() == 1 ) {
			%>
				<%= boBean.getReceipts_number()%>
			<% 
				} else {
			%>
				<%int type = boBean.getCurrent_type();
						if((type==0||type==1||type==2||type==5)&&(userid==boBean.getOperator_id()||group.isFlag(413))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}else if(type==6&&(userid==boBean.getOperator_id()||group.isFlag(229))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}else {%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}%>
			<%
				}
				}
			%>
			</td>
			<td align="center">
			<% if( cvBean.getStatus() > 0 ) 
				{
			%> 
			
			<%
				} else {
			%>
				<a href="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=foreEditClaimsVerification&id=<%= cvBean.getId()%>">编辑</a> |
				<a href="javascript:deleteClaimsAsk(<%= cvBean.getId()%>);" >删除</a> |
			<%
				}
			%>
		    <a href="javascript:printClaims(<%= cvBean.getId()%>);" >打印</a>
			</td>
		<%
				}
			} else { 
		%>
		<tr bgcolor="#FFFFFF" >
			<td align="center" colspan="14">
				没有理赔核销记录或没有符合查询条件的记录
			</td>
		</tr>
		<%
			}
		%>
		</form>
		</tbody>
	</table>

	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</div>
</body>
</html>
