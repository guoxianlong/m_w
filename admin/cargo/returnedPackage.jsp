<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoOperationCargoBean"%>
<%@page import="adultadmin.bean.cargo.CargoOperationBean"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="mmb.stock.stat.*"%>
<%
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ReturnedPackage</title>
<script type="text/javascript" src="../../js/jquery-1.6.1.js"></script>
<script type="text/javascript">
	$.post("../../admin/returnStorageAction.do?method=checkStorageAuthority",function(result){
		if(result=="0"){
			alert("您没有权限进行此操作！");
			window.location.href='../../login.do';
		}
	});
	document.onkeydown = _keydown;
	function _keydown(e){ 
        if(document.activeElement.tagName.toUpperCase()=="INPUT"){ 
            if(event.keyCode   ==   13){ 
                   switch(document.activeElement.id){ 
                           case "orderCodeId" : 
                               var orderCode = document.getElementById("orderCodeId").value;
                        	   $.post("../../admin/returnStorageAction.do?method=checkOrderCode&orderCode="+orderCode,function(result){
                        		    var flag = "0";
                       				if(result=="0"){
                        				alert("订单已经入库，不允许重新入库！");
                        				$("#orderCodeId").val("");
                        				$("#orderCodeId").focus();
                        				flag = "1";
                        			}else if(result=="2"){
                        				alert("该订单不存在！");
                        				$("#orderCodeId").val("");
                        				$("#orderCodeId").focus();
                        				flag = "1";
                            		}else if(result=="3"){
                            			alert("系统异常，请联系管理员！");
                        				$("#orderCodeId").val("");
                        				$("#orderCodeId").focus();
                        				flag = "1";
                                	}else if(result=="4"){
                            			alert("订单编号不能为空！");
                        				$("#orderCodeId").val("");
                        				$("#orderCodeId").focus();
                        				flag = "1";
                                	}
                        			if(flag=="0"){
                                	  	 document.getElementById("Button1").click(); 
                               		}
                                    event.returnValue   =   0; 
                        		});
                               break; 
                           case "packageCodeId" : 
                                document.getElementById("productBCodeId").focus(); 
                                event.returnValue   =   0; 
                                break;
                           default : 
                                event.returnValue   =   0; 
                                break;                                                                                                             
                   }   
            } 
        }else if(document.activeElement.tagName.toUpperCase()=="TEXTAREA"){
        }else if(event.keyCode == 13){ 
            document.getElementById( "Button1").click(); 
        } 
	}
	function storagePackage(intype){
		var remindtxt = "";
		var flag = true;
		if(intype=="exp"){
			remindtxt = "请确认缺失商品条码录入完整";
			flag = confirm(remindtxt);
		}
		
		if(flag==true){
		var exceptionDivDis = document.getElementById("exceptionDiv").style.display;
		var type="0";
		if(exceptionDivDis == "none"){
			var pcodeValue = $("#packageCodeId").val();
			if(pcodeValue == ""){
				alert("该包裹单号不能为空！");
				$("#packageCodeId").focus();
				return;
			}
			var pcodeValue = $("#orderCodeId").val();
			if(pcodeValue == ""){
				alert("该订单编号不能为空！");
				$("#orderCodeId").focus();
				return;
			}
			var pcodeValue = document.getElementById("productBCodeId").innerText;
			if(pcodeValue == ""){
				if( document.getElementById("productBCodeId").value == null || document.getElementById("productBCodeId").value == "") {
					alert("该商品编号不能为空！");
					$("#productBCodeId").focus();
					return;
				}
			}
		}else{
			var lostpcodeValue = document.getElementById("lostProductCodeId").innerText;
			type="2";
			if(lostpcodeValue == ""){
				alert("缺失商品编号不能为空！");
				$("#lostProductCodeId").focus();
				return;
			}
			
		}
		
		var wareArea = document.getElementById("wareArea").value;
		if( wareArea == null || wareArea == "" || wareArea == "-1" ) {
			alert("没有选择对应的库地区！");
			$("#wareArea").focus();
			return;
		}
		
		//result表示，0：成功，1：包裹单号与订单号不匹配，2：订单号与商品编号不匹配，3：继续录入缺失商品,4:订单号不存在
		//5：包裹号不存在，6：商品条码不存，7：商品不属于订单，8：缺失商品录入完成
		$.post("../../admin/returnedPackageAction.do?method=storagePackage&type="+type+"&wareArea="+wareArea, 
				$("#packageform").serialize(),function(result){
				switch(result){
					case "0":
						$("#packageCodeId").val("");
						$("#orderCodeId").val("");
						$("#productBCodeId").val("");
						break;
					case "1":
						var r=confirm("包裹单与订单号不对应！确认商品入库？");
						if(r==true){
							$.post("../../admin/returnedPackageAction.do?method=storagePackage&type=1"+"&wareArea="+wareArea, //表示包裹号与订单号不对应，异常入库
								$("#packageform").serialize(),function(result){
									if(result=="success"){
										$("#packageCodeId").val("");
										$("#orderCodeId").val("");
										$("#productBCodeId").val("");
									}else{
										alert(result);
									}
								})
						}else{}
						break;
					case "2":
						document.getElementById("normalDiv").style.display="none";
						document.getElementById("exceptionDiv").style.display="block";
						break;
					case "3":
						alert("包裹已经入库，不允许重新入库！");
						$("#packageCodeId").val("");
						$("#orderCodeId").val("");
						$("#productBCodeId").val("");
						break;
					case "4":
						alert("该订单编号不存在！");
						$("#orderCodeId").val("");
						break;
					case "5":
						alert("该包裹单号不存在！");
						$("#packageCodeId").val("");
						break;
					case "6":
						alert("该商品编号不存在！");
						break;
					case "7":
						alert("该商品不属于订单！");
						break;
					case "8":
						$("#lostProductCodeId").val("");
						$("#packageCodeId").val("");
						$("#orderCodeId").val("");
						$("#productBCodeId").val("");
						document.getElementById("normalDiv").style.display="block";
						document.getElementById("exceptionDiv").style.display="none";
						break;
					case "9":
						alert("存在未输入的缺失商品编号！");
						$("#lostProductCodeId").focus();
						break;
					case "10":
						alert("商品编号不是缺失的编号！");
						$("#lostProductCodeId").focus();
						break;
					case "11":
						alert("请输入产品编号！");
						$("#lostProductCodeId").focus();
						break;
					default :
						alert(result);
						break;
				}
		});
		}
}
	
	function cancelSExcpetionPackage(){
		document.getElementById("normalDiv").style.display="block";
		document.getElementById("exceptionDiv").style.display="none";
	}
</script>
</head>
<body>
<div style="width:260px; float:left">
<label><font style=" font-weight:blob" size="4" color="black">包裹入退货库</font></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="../stock/quickCancelStock.jsp">快速退货</a>
	<form id="packageform">
		<div id="normalDiv" style="display:block">
			<div style="margin-top:5px">
			包裹单号：<input name="packageCode" id="packageCodeId"></input>
			</div>
			<div style="margin-top:5px">
			商品条码：<textarea name="productBarCode" rows="" cols="" id="productBCodeId" style="height: 59px; width: 149px"></textarea>
			</div>
			<div style="margin-top:5px">
			订单编号：<input name="orderCode" id="orderCodeId"></input>
			<br><br>
			<div style="margin-top:5px">
			  &nbsp;&nbsp;库地区:
					<%= wareAreaSelectLable%>
			</div>
			<input type="button" id="Button1" style="float:right;" value="确认入库" onclick="storagePackage('nor')"/>
			</div>
		</div>
		<div id="exceptionDiv" style="display:none">
			<h2>订单号与商品条码不匹配，请输入以下信息：</h2>
			<div style="">
			缺失商品编号：<textarea name="exceptionPCode" id="lostProductCodeId"></textarea>
			是否已索赔：<input type="radio" id="payId" name="payFlag" checked="checked" value="1" />是
  					   <input type="radio" id="payId" name="payFlag" value="0" />否
			</div>
			<div style="">
			<input type="button" id="exceptionButtion" value="确认入库" onclick="storagePackage('exp')"/>
			<input type="button" id="cancelId" value="取消" onclick="cancelSExcpetionPackage()"/>
			</div>
		</div>
	</form>
	<br></br>
<p style="font-size:12px">
说明：<br>
1.请依次扫描包裹单号、商品条码、订单编号；
2.输入模式下请点击“确认入库”将包裹入库。
</p>
</div>
</body>
</html>