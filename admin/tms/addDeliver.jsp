<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.*,adultadmin.bean.stock.ProductStockBean"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>添加快递公司页面</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
</head>
<body>
<form id="form" method="post">
	<div>
		基本信息：&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" id="status" name="status"  checked="checked"/>启用&nbsp;&nbsp;&nbsp;&nbsp;
		是否可修改：<input type="radio" name="changeable" value="0" checked>不可修改&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="changeable" value="1"/>可以修改<hr/>
		公司简称<font color="red">*</font>：<input type="text" name="name" class="easyui-validatebox" data-options="required:true"/>&nbsp;&nbsp;&nbsp;&nbsp;
		结算公司<font color="red">*</font>：<input id="balanceCompany" name="balanceCompany" editable="false" required="required" style="width: 155px;" /><br/>
		客服电话：<input type="text" name="phone" class="easyui-validatebox" data-options="required:true"/>&nbsp;&nbsp;&nbsp;&nbsp;
		查询订单网址：<input type="text" name="webAddress" class="easyui-validatebox" data-options="required:true"/><br/>
	</div>
	<br/>
	<div>
		前置分配规则：<hr/>
		订单付款类型<font color="red">*</font>：<input type="radio" name="buyModeType" value="0" checked/>不限&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="buyModeType" value="1"/>仅货到付款&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="buyModeType" value="2"/>仅非货到付款<br/>
		订单渠道<font color="red">*</font>：<input type="radio" name="channel" value="0" />不限&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="channel" value="1" checked/>普通&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="channel" value="2"/>特殊（京东、淘宝、19e）
	</div>
	<br/>
	<div>
		面单规则：<hr/>
		快递类型：
		<select id="formType" name="formType">
			<option value="">请选择</option>
			<option value="0">EMS</option>	
			<option value="1">宅急送</option>	
			<option value="2">落地配</option>		
		</select>
		<input type="radio" name="packageType" value="0"/>使用MMB面单&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio" name="packageType" value="1"/>使用EMS国内面单&nbsp;&nbsp;&nbsp;&nbsp;<br/>
		发货地址：<input type="text" name="address"/><br/>
		代收货款订单：
		<select name="codPackageType">
			<option value="0">订单号</option>
			<option value="1">EMS经济快递单号</option>
			<option value="2">EMS货到付款单号</option>
			<option value="3">EMS标准快递单号</option>
			<option value="4">快递自己提供的单号</option>
		</select>
		接口账号：<input type="text" name="codAccount" class="easyui-validatebox" data-options="required:true"/>
		密码：<input type="text"  name="codPassword" class="easyui-validatebox" data-options="required:true"/><br/>
		已付款订单：
		<select name="paidPackageType">
			<option value="0">订单号</option>
			<option value="1">EMS经济快递单号</option>
			<option value="2">EMS货到付款单号</option>
			<option value="3">EMS标准快递单号</option>
			<option value="4">快递自己提供的单号</option>
		</select>
		接口账号：<input type="text" name="paidAccount" class="easyui-validatebox" data-options="required:true"/>
		密码：<input type="text"  name="paidPassword" class="easyui-validatebox" data-options="required:true"/><br/>
		发货明细接收邮箱：<input name="mail" class="easyui-validatebox" data-options="required:true,validType:'email'"/>
		</div>
	<br/>
	<div>
		KPI指标标准值：<hr/>
		妥投率：<input name="deliveryRate" data-options="min:0,max:100,required:true" class="easyui-numberbox">% &nbsp;&nbsp;&nbsp;&nbsp;<br/>
		超时率：<input  name="overtimeRate" class="easyui-numberbox" data-options="min:0,max:100,required:true">%<br/><br/>
		<div style="border-top:1px dashed #cccccc;height: 1px;overflow:hidden"></div><br/>
		<% 
			HashMap<Integer,String> areaMap = (HashMap<Integer, String>)ProductStockBean.getAreaMap();
			if(areaMap!=null && areaMap.size()>0){
				for(Integer key : areaMap.keySet()){
					String areaName = areaMap.get(key);
		 %>
		 <%= areaName %>仓&nbsp;&nbsp;
		 最晚交接时间：<input class="easyui-timespinner" name="lastTransitTime<%=key%>" style="width:80px;" >&nbsp;&nbsp;
		 揽收时效：<input type="text" name="collectTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 到达当地时效：<input type="text" name="arriveTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 投递时效：<input type="text" name="mailingTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时&nbsp;&nbsp;
		 妥投时效：<input type="text" name="sendTime<%=key%>" style="width:80px;" class="easyui-validatebox" data-options="required:true"/>小时<br/>
		 <% } }%>
	</div>
	<br/><hr/>
	<div align="center">
		<a class="easyui-linkbutton" onclick="clearForm();" href="javascript:void(0);">重置</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<a class="easyui-linkbutton" onclick="addDeliver();" href="javascript:void(0);">保存</a>
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
	});
	function addDeliver(){
		if('checked'==$("#status").attr('checked')){
			$("#status").attr("value",1);
		}else{
			$("#status").attr("value",0);
		}
		$("#form").form('submit',{
			url : '${pageContext.request.contextPath}/deliverController/addDeliver.mmx',
			success : function(data){
				var d = $.parseJSON(data);
				if (d) {
					$.messager.show({
						msg : d.msg,
						title : '提示'
					});
				}
				window.location.href = "${pageContext.request.contextPath}/admin/tms/addDeliver.jsp";
			}
		});
	}
	
	function clearForm(){
		$('#form').form('clear');
	}
</script>
