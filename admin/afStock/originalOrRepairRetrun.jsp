<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<title>原品&维修商品寄回</title>
<script type="text/javascript">
$(function(){
	$('#deliver').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getDeliver.mmx',
      	valueField:'id',
		textField:'text'
    });
    $("#detectCodes").blur(function(){
		var flag = checkDetectCode();
		if(flag){
			var detectCodes = $("#detectCodes").val();
			var str = detectCodes.split("\n");
			var code = '';
			for(var i=0;i<str.length;i+=1){
				if(str[i].length>0){
					code = str[i];
					break;
				}
			}
			$.ajax({
				url : '${pageContext.request.contextPath}/admin/AfStock/getUserPhoneAddress.mmx',
				data : {detectCode:code},
				type : 'post',
				dataType:'json',
				success:function(r){
					if(r.success == true){
						$("#userPhone").val(r.obj.userPhone);
						$("#phone").val(r.obj.phone).attr("readOnly",true);
						$("#userAddress").val(r.obj.address).attr("readOnly",true);
						$("#customerName").val(r.obj.name).attr("readOnly",true);
						$("#postCode").val(r.obj.postCode);
						if(r.msg != null){
							$.messager.confirm('确认',r.msg,function(o){    
							    if (o){    
										$("#detectCodes").val(r.obj.detectCodeAll);
							    }    
							});
						}
					}else{
						$.messager.show({
							msg :r.msg,
							title : '提示'
						});
					}
				}
			});
		}
	});
});
function checkDetectCode(){
	var detectCodes = $("#detectCodes").val();
	if(detectCodes==''){
		$.messager.show({
			msg : '售后处理单号不能为空!',
			title : '提示'
		});
		return false;
	}
	var str = detectCodes.split("\n");
	var numlen = str.length;
	var count=0;
	if(numlen>300){
		for(var i=0;i<numlen;i+=1){
			if(str[i] && trim(str[i]).length>0)
				count++;
		}
	}
	if(count>300){
		$.messager.show({
			msg : '售后处理单号至多允许输入300个\n已输入'+count+'个!',
			title : '提示'
		});
		return false;
	}
	return true;
}
function checkForm(){
	var detectCodes = $("#detectCodes").val();
	if(detectCodes==''){
		$.messager.show({
			msg : '售后处理单号不能为空!',
			title : '提示'
		});
		return false;
	}
	var customerName  = $("#customerName").val();
	if(customerName==''){
		$.messager.show({
			msg : '客户姓名不能为空!',
			title : '提示'
		});
		return false;
	}
	var phone = $("#phone").val();
	if(phone==''){
		$.messager.show({
			msg : '用户手机号不能为空!',
			title : '提示'
		});
		return false;
	}
	var userAddress = $("#userAddress").val();
	if(userAddress==''){
		$.messager.show({
			msg : '用户地址不能为空!',
			title : '提示'
		});
		return false;
	}
	var weight = $("#weight").val();
	if(weight==''){
		$.messager.show({
			msg : '包裹重量不能为空!',
			title : '提示'
		});
		return false;
	}
	return true;
}
function addPackage(){
	//window.open('${pageContext.request.contextPath}/admin/afStock/printAfterSalePackage.jsp?packageCode=2600010025&deliverName=顺丰快运&address=北京朝阳区红军营路按时打算还12312123123&weight=1.2&phoneNumber=13800138000&customerName=' + 
	//'张三&printTime=2014-08-10&afterSaleCode=SH20221005255'); 
	if(checkForm()){
		$("#form").form('submit',{
			url : '${pageContext.request.contextPath}/admin/AfStock/addOriginalOrRepairBackPackage.mmx',
			success : function(data) {
				var d = $.parseJSON(data);
				if (d) {
					if(d.success){
						window.open('${pageContext.request.contextPath}/admin/AfStock/printAfterSalePackageInfo.mmx?packageCode='+d.obj.packageCode+'&postCode=' + d.obj.postCode);
						window.location.href="${pageContext.request.contextPath}/admin/afStock/originalOrRepairRetrun.jsp";
					}else{
						$.messager.show({
							msg : d.msg,
							title : '提示'
						});
					}
				}
			}
		});
	}
}
function test(){
	var weight=aiPlug.GetData();
	if(weight != ""){
		document.getElementById("weight").value=weight;
		document.getElementById("weight").readOnly = true;
	}
}
function checkLeave(){
	aiPlug.DisposeRes();
}
</script>
</head>
<body onunload="checkLeave()">
	<fieldset style="background-color: #dedede;padding:8px;">
		<form id="form" method="post">
			<table align="center">
				<tr>
					<td align="right">快递公司：</td>
					<td><input id="deliver" name="deliverId" editable="false" required="required" style="width: 155px;" /></td>
				</tr>
				<tr>
					<td align="right">运费金额：</td>
					<td><input type="text" name="freight" id="freight"/>元</td>
				</tr>
				<tr>
					<td align="right">扫描商品上所贴售后处理单号或原备用机号：</td>
					<td>
						<textarea id="detectCodes" name="detectCodes" required="required" ></textarea>&nbsp;&nbsp;扫描售后处理单号，或者手动输入售后处理单号或原备用机号。
					</td>
				</tr>
				<tr>
					<td align="right">用户姓名：</td>
					<td><input type="text" id="customerName" name="customerName" required="required" /></td>
				</tr>
				<tr>
					<td align="right">用户手机号：</td>
					<td><input type="text" id="phone" name="phone" required="required" />
						<input type="hidden" id="userPhone" name="userPhone"/>
					</td>
				</tr>
				<tr>
					<td align="right">用户收货地址：</td>
					<td><input style="width:500px;" type="text" id="userAddress" name="userAddress" required="required" />
						<input  type="hidden" id="postCode" name="postCode" />
					</td>
				</tr>
				<tr>
					<td align="right">包裹重量：</td>
					<td><input style="" type="text" id="weight" name="weight" required="required" /></td>
				</tr>
				<tr>
					<td align="right">备注：</td>
					<td><textarea style="width:500px;" rows="3" id="remark" name="remark"></textarea></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><a class="easyui-linkbutton" onclick="addPackage();" href="javascript:void(0);">完成</a></td>
				</tr>
			</table>
		</form>
	</fieldset>
</body>
<script type="text/javascript">
var aiPlug = new ActiveXObject("MMb_JEC.Balance");
aiPlug.Init();
var tr = setInterval("test();", 60);
</script>
</html>
