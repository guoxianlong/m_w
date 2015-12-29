<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.HashMap,adultadmin.bean.stock.ProductStockBean,mmb.tms.model.DeliverKpi,adultadmin.util.StringUtil"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>编辑快递公司页面</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
</head>
<body>
	<c:if test="${!empty tip}">
		<script language="JavaScript">
			$.messager.show({
				msg : '${tip}',
					title : '提示'
			});
		</script>
	</c:if>
<form id="form" method="post">
	<div>
		<input type="hidden" name="deliverId" value="${deliverInfo.id}" />
		<input type="hidden" name="deliverId" value="${deliverInfo.id}" />
		基本信息：&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" id="status" name="status" ${deliverInfo.status==1?'checked':''}/>启用&nbsp;&nbsp;&nbsp;&nbsp;
		是否可修改：<input type="radio" name="changeable" value="0" ${deliverInfo.changeable==0?'checked':''}>不可修改&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="changeable" value="1" ${deliverInfo.changeable==1?'checked':''}/>可以修改<hr/>
		公司简称<font color="red">*</font>：<input type="text" name="name" class="easyui-validatebox" data-options="required:true" value="${deliverInfo.name}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		结算公司<font color="red">*</font>：<input id="balanceCompany" name="balanceCompany" editable="false" required="required" style="width: 155px;" /><br/>
		客服电话：<input type="text" name="phone" class="easyui-validatebox" data-options="required:true" value="${deliverInfo.phone}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		查询订单网址：<input type="text" name="webAddress" class="easyui-validatebox" data-options="required:true" value="${deliverInfo.webAddress}"/><br/>
	</div>
	<br/>
	<div>
		前置分配规则：<hr/>
		订单付款类型<font color="red">*</font>：<input type="radio" name="buyModeType" value="0" ${deliverInfo.buyModeType==0?'checked':''}/>不限&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="buyModeType" value="1" ${deliverInfo.buyModeType==1?'checked':''}/>仅货到付款&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="buyModeType" value="2" ${deliverInfo.buyModeType==2?'checked':''}/>仅非货到付款<br/>
		订单渠道<font color="red">*</font>：<input type="radio" name="channel" value="0" ${deliverInfo.channel==0?'checked':''}/>不限&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="channel" value="1" ${deliverInfo.channel==1?'checked':''}/>普通&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="channel" value="2" ${deliverInfo.channel==2?'checked':''}/>特殊（京东、淘宝、19e）
	</div>
	<br/>
	<div>
		面单规则：<hr/>
		快递类型：
		<select id="formType" name="formType">
			<option value="">请选择</option>
			<option value="0" ${deliverInfo.formType==0?'selected':''}>EMS</option>	
			<option value="1" ${deliverInfo.formType==1?'selected':''}>宅急送</option>	
			<option value="2" ${deliverInfo.formType==2?'selected':''}>落地配</option>		
		</select>
		<input type="radio" name="packageType" value="0" ${deliverInfo.packageType==0?'checked':''}/>使用MMB面单&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="packageType" value="1" ${deliverInfo.packageType==1?'checked':''}/>使用EMS国内面单&nbsp;&nbsp;&nbsp;&nbsp;<br/>
		发货地址：<input type="text" name="address" value="${deliverInfo.address}"/><br/>
		代收货款订单：
		<select name="codPackageType">
			<option value="0" ${deliverIfo.codPackageType==0?'selected':''}>订单号</option>
			<option value="1" ${deliverIfo.codPackageType==1?'selected':''}>EMS经济快递单号</option>
			<option value="2" ${deliverIfo.codPackageType==2?'selected':''}>EMS货到付款单号</option>
			<option value="3" ${deliverIfo.codPackageType==3?'selected':''}>EMS标准快递单号</option>
			<option value="4" ${deliverIfo.codPackageType==4?'selected':''}>快递自己提供的单号</option>
		</select>
		接口账号：<input type="text" name="codAccount" class="easyui-validatebox" data-options="required:true" value="${deliverInfo.codAccount}"/>
		密码：<input type="text"  name="codPassword" class="easyui-validatebox" data-options="required:true" value="${deliverInfo.codPassword}"/><br/>
		已付款订单：
		<select name="paidPackageType">
			<option value="0" ${deliverIfo.paidPackageType==0?'selected':''}>订单号</option>
			<option value="1" ${deliverIfo.paidPackageType==1?'selected':''}>EMS经济快递单号</option>
			<option value="2" ${deliverIfo.paidPackageType==2?'selected':''}>EMS货到付款单号</option>
			<option value="3" ${deliverIfo.paidPackageType==3?'selected':''}>EMS标准快递单号</option>
			<option value="4" ${deliverIfo.paidPackageType==4?'selected':''}>快递自己提供的单号</option>
		</select>
		接口账号：<input type="text" name="paidAccount" class="easyui-validatebox" data-options="required:true" value="${deliverInfo.paidAccount}"/>
		密码：<input type="text"  name="paidPassword" class="easyui-validatebox" data-options="required:true" value="${deliverInfo.paidPassword}" /><br/>
		发货明细接收邮箱：<input name="mail" class="easyui-validatebox" data-options="required:true,validType:'email'" value="${deliverInfo.mail}"/>
		</div>
	<br/>
	<div>
		KPI指标标准值：<hr/>
		妥投率：<input name="deliveryRate" data-options="min:0,max:100,required:true" class="easyui-numberbox" value="${deliverInfo.deliveryRate}">% &nbsp;&nbsp;&nbsp;&nbsp;<br/>
		超时率：<input  name="overtimeRate" class="easyui-numberbox" data-options="min:0,max:100,required:true" value="${deliverInfo.overtimeRate}">%<br/><br/>
		<div style="border-top:1px dashed #cccccc;height: 1px;overflow:hidden"></div><br/>
		<% 
			HashMap<Integer,DeliverKpi> kpiMap = (HashMap<Integer,DeliverKpi>)request.getAttribute("kpiMap");
			HashMap<Integer,String> areaMap = (HashMap<Integer, String>)ProductStockBean.getAreaMap();
			if(areaMap!=null && areaMap.size()>0){
				for(Integer key : areaMap.keySet()){
					String areaName = areaMap.get(key);
		%>
		<%= areaName %>仓&nbsp;&nbsp;
		<%
					if(kpiMap!=null && kpiMap.size()>0){
						if(kpiMap.containsKey(key)){
							DeliverKpi kpi = kpiMap.get(key);
		%>
		 最晚交接时间：<input class="easyui-timespinner" name="lastTransitTime<%=key%>" style="width:80px;" value="<%=StringUtil.convertNull(kpi.getLastestTransitTime()) %>">&nbsp;&nbsp;
		 揽收时效：<input type="text" name="collectTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true" value="<%=StringUtil.convertNull(kpi.getCollectTime())  %>"/>小时&nbsp;&nbsp;
		 到达当地时效：<input type="text" name="arriveTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true" value="<%=StringUtil.convertNull(kpi.getArriveTime())  %>"/>小时&nbsp;&nbsp;
		 投递时效：<input type="text" name="mailingTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true" value="<%=StringUtil.convertNull(kpi.getMailingTime())  %>"/>小时&nbsp;&nbsp;
		 妥投时效：<input type="text" name="sendTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true" value="<%=StringUtil.convertNull(kpi.getSendTime())  %>"/>小时<br/>
		<%
						}else{
			%>	
		 最晚交接时间：<input class="easyui-timespinner" name="lastTransitTime<%=key%>" style="width:80px;" >&nbsp;&nbsp;
		 揽收时效：<input type="text" name="collectTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 到达当地时效：<input type="text" name="arriveTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 投递时效：<input type="text" name="mailingTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 妥投时效：<input type="text" name="sendTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时<br/>
					<%	}
					}else{
		 %>
		 最晚交接时间：<input class="easyui-timespinner" name="lastTransitTime<%=key%>" style="width:80px;" >&nbsp;&nbsp;
		 揽收时效：<input type="text" name="collectTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 到达当地时效：<input type="text" name="arriveTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 投递时效：<input type="text" name="mailingTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 妥投时效：<input type="text" name="sendTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时<br/>
		 <% }}} %>
	</div>
	<br/><hr/>
	<div align="center">
		<a class="easyui-linkbutton" onclick="clearForm();" href="javascript:void(0);">重置</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<a class="easyui-linkbutton" onclick="saveDeliver();" href="javascript:void(0);">保存</a>
	</div>
</form>
</body>
</html>
<script type="text/javascript">
$(function(){
		$("#balanceCompany").combobox({
			url : '${pageContext.request.contextPath}/Combobox/getBalanceCorpInfo.mmx',
			valueField:'id',
			textField:'text'
		});
		
		$("#balanceCompany").combobox("setValue",'${deliverBalanceType.balanceTypeId}');
	});
	
	
	function saveDeliver(){
		if('checked'==$("#status").attr('checked')){
			$("#status").attr("value",1);
		}else{
			$("#status").attr("value",0);
		}
		$("#form").form('submit',{
			url : '${pageContext.request.contextPath}/deliverController/saveDeliver.mmx',
			success : function(data){
				var d = $.parseJSON(data);
				if (d) {
					$.messager.show({
						msg : d.msg,
						title : '提示'
					});
				}
			}
		});
	}
	
	function clearForm(){
		$('#form').form('clear');
	}
</script>
