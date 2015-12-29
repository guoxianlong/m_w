<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,mmb.stock.stat.*" %>
<%
	ClaimsVerificationBean cvBean = (ClaimsVerificationBean) request.getAttribute("claimsVerificationBean");
	voOrder vorder = (voOrder) request.getAttribute("userOrder");
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request,cvBean.getWareArea());
%>
<!DOCTYPE HTML>
<html>
  <head>
    
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>编辑理赔核销单</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<style type="text/css">
		form{margin:0px;display: inline}
	</style>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}
   		
   		function backUp() {
   			var productCount = document.getElementById("productCount").value;
   			var count = parseInt(productCount, 10);
   			if( count > 0) {
   				alert("请在删除完理赔核销单的现有商品后，再更改订单！");
   				return;
   			}
   		}
   		
   		function getOrderInfo() {
   			var code = document.getElementById("orderCode").value;
   			if( code == null || code == "" ) {
   				alert("没有填写订单号");
   				return false;
   			}
   			
   			
   				$.ajax({
                        type: "GET", //调用方式  post 还是 get
                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=getOrderInfoByCode&orderCode="+code, //访问的地址
                        dataType: "text", //返回的数据的形式
                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
                        	var json = eval('(' + data + ')');
                        	if( json['status'] == "fail" ) {
                        		alert(json['tip']);
                        	} else if ( json['status'] == "success" ) {
                        		var list = json['productList'];
                        		var x = list.length;
                        		if( x== 0 ) {
                        			alert("没有找到订单商品！");
                        			return false;
                        		} else {
                        			document.getElementById("hasOrder").value="1";
                        		}
                        		var shtml = "";
                        		shtml+="<input type='hidden' name='orderId' id='orderIdp' value='" + json["order_id"] + "' />";
                        		shtml+="<input type='hidden' name='orderStockId' id='orderStockIdp' value='" + json["order_stock_id"] + "' />";
                        		shtml += "<table align='center' width='94%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >";
								shtml += "<tbody>";
								shtml += "<tr bgcolor='#484891' >";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>序号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>产品编号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>产品原名称</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>小店名称</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>数量</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>添加数量</font>";
                        			shtml += "</td>";
                        			shtml += "</tr>";
                        		for( var i = 0; i < x; i++ ) {
                        			shtml += "<tr bgcolor='#FFFFFF' >";
                        			shtml += "<td align='center'>";
                        			shtml += "<input type='checkbox' name='productIds' id='id_" + list[i]['productId'] + "' value='" + list[i]['productId'] + "'  />";
                        			shtml += i+1;
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += list[i]['productCode'];
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += list[i]['oriName'];
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += list[i]['name'];
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += list[i]['count'];
                        			shtml += "<input type='hidden' name='order_count_"+list[i]["productId"]+"' id='order_count_"+list[i]["productId"]+"' value='" + list[i]['count'] + "'";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<input type='text' name='count_" + list[i]['productId'] + "' id='count_" + list[i]['productId'] +"'size='6' value='0'/>";
                        			shtml += "</td>";
                        			shtml += "</tr>";
                        		}
                        		shtml+="</tbody>";
                        		shtml+="</table>";
                        		document.getElementById("tableDiv").innerHTML=shtml;
                        		document.getElementById("addProductCode").select();
                        		return false;
                        	}
                        	
                        },
                        error: function() {          //如果过程中出错了调用的方法
                             alert("验证出错");
                             return false;
                        }
                  });
                  return false;
   			
   		}	
   		
   		function test() {
   			alert(1);
   		}
   		
   		
   		
   		function addProductOrCount () {
   			var productCode =document.getElementById("addProductCode").value;
   			var hasOrder = document.getElementById("hasOrder").value;
   			if( hasOrder == "0" ) {
   				alert("还没有订单信息！");
   				document.getElementById("addProductCode").value="";
   				return  false;
   			}
   			if(  productCode != null && productCode != "" ) {
   				$.ajax({
	                        type: "GET", //调用方式  post 还是 get
	                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=getProductIdByCode&productCode="+productCode, //访问的地址
	                        dataType: "text", //返回的数据的形式
	                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
	                        	var json = eval('(' + data + ')');
	                        	if( json['status'] == "fail" ) {
	                        		alert(json['tip']);
	                        		return false;
	                        	} else if ( json['status'] == "success" ) {
	                        		var productId = json['productId'];
	                        		var productObj = document.getElementById("id_" + productId);
	                        		if( productObj == null) {
	                        			alert('订单中没有这个商品!');
	                        		} else {
	                        			productObj.checked="true";
		                        		var orderNumber = parseInt(document.getElementById("order_count_" + productId).value,10);
		                        		var addNumbers = document.getElementById("count_" + productId).value;
		                        		if( addNumbers != null && addNumbers != "" ) {
		                        			var addNumber = parseInt(addNumbers, 10);
		                        			if( addNumber >= 0 ) {
		                        				addNumber++;
		                        				document.getElementById("count_" + productId).value=addNumber;
		                        			} else {
		                        				alert("当前商品数量异常！");
		                        			}
		                        		}
	                        		}
	                        		
	                        		document.getElementById("addProductCode").value="";
	                        		giveUnsaveTip();
	                        		return false;
	                        	}
	                        },
	                        error: function() {          //如果过程中出错了调用的方法
	                             alert("验证出错");
	                             return false;
	                        }
	                  });
   			} else {
   				alert("没有填写商品编号/条码!");
   				return false;
   			}
   			return false;
   		}
   		
   		function getChecks() {
   			var hasOrder = document.getElementById("hasOrder").value;
   			if( hasOrder == "0" ) {
   				alert("还没有相应的订单信息！");
   				return;
   			} else {
   				
   				var params = "";
   				var abc = document.getElementsByName("productIds");
   				if( abc == null ) {
   					alert("没有要提交的商品");
   					return;
   				} 
   				var hasAddOrder = document.getElementById("hasAddOrder").value;
	   			if( hasAddOrder != "0" ) {
	   				var currentId = document.getElementById("currentOrderId").value;
	   				var id = document.getElementById("orderIdp").value;
	   				if( id != currentId) {
	   					alert("当前添加的订单与  已添加商品的订单 不同！！ ");
	   					return;
	   				}
	   			}
   				
   				var currentIds = document.getElementsByName("currentProductIds");
   				if( currentIds != null && currentIds.length > 0) {
   					for ( var i = 0; i < currentIds.length; i++ ) {
   						var temp = currentIds[i].value;
   						var count2 = document.getElementById("current_count_" + temp).value;
   						params +="&currentProductIds=" + temp;
   						params += "&current_count_"+temp+"=" + count2;
   					}
   				}
	   			
	   			for( var i = 0 ; i < abc.length; i++ ) {
	   				if( abc[i].checked) {
	   					var temp = abc[i].value;
	   					var count = document.getElementById("count_" + temp).value;
	   					if( count == null || count == "" ) {
	   						alert("有提交商品没有填写数量！");
	   						return;
	   					}
	   					var number = parseInt(count, 10);
	   					if( number == 0 ) {
	   						alert("有提交商品的数量为0！");
	   						return;
	   					}
	   					if( number < 0 ) {
	   						alert("提交商品的填写数量有误！");
	   						return;
	   					}
	   					params += "&productIds="+abc[i].value+"&count_"+temp+"="+count;
	   				}
	   			}
	   			
	   			if( params == "" ) {
	   				alert("没有勾选任何的商品！");
	   			} else {
	   				params += "&orderId="+ document.getElementById("orderIdp").value;
	   				params += "&orderStockId="+ document.getElementById("orderStockIdp").value;
	   				$.ajax({
	                        type: "GET", //调用方式  post 还是 get
	                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=checkClaimsVerificationProduct"+params, //访问的地址
	                        dataType: "text", //返回的数据的形式
	                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
	                        	var json = eval('(' + data + ')');
	                        	if( json['status'] == "fail" ) {
	                        		alert(json['tip']);
	                        	} else if ( json['status'] == "success" ) {
	                        		var list = json['productList'];
	                        		var x = list.length;
	                        		var shtml = "";
	                        		shtml += "<input type='hidden' name='currentOrderId' id='currentOrderId' value='" + json['currentOrderId'] + "' />";
	                        		shtml+="<input type='hidden' name='currentOrderStockId' id='currentOrderStockId' value='" + json["orderStockId"] + "' />";
	                        		shtml += "<table align='center' width='82%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >";
									shtml += "<tbody>";
									shtml += "<tr bgcolor='#484891' >";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>订单号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>包裹单号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>快递公司</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>原名称</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>产品线</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>产品编号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>数量</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>有无实物</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>理赔方式</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>操作</font>";
                        			shtml += "</td>";
                        			shtml += "</tr>";
                        			shtml += "<tr bgcolor='#FFFFFF' >";
                        			shtml += "<td align='center' rowspan='" + x + "'>";
                        			shtml += json['currentOrderCode'];
                        			shtml += "</td>";
                        			shtml += "<td align='center' rowspan='" + x + "'>";
                        			shtml += json['packageCode'];
                        			shtml += "</td>";
                        			shtml += "<td align='center' rowspan='" + x + "'>";
                        			shtml += json['deliverCompany'];
                        			shtml += "</td>";
	                        		for( var i = 0; i < x; i++ ) {
	                        			if( i == 0 ) {
	                        				
	                        			} else {
	                        				shtml += "<tr bgcolor='#FFFFFF' >";
	                        			}
	                        			shtml += "<td align='center'>";
	                        			shtml += list[i]['oriName'];
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += list[i]['productLine'];
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += list[i]['productCode'];
	                        			shtml += "<input type='hidden' name='currentProductIds' id='current_id_" + list[i]['productId'] + "' value='" + list[i]['productId'] + "' />";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<input type='text' name='current_count_" + list[i]['productId'] + "' id='current_count_" + list[i]['productId'] +"' size='6' value='" + list[i]['count'] + "' onchange='giveUnsaveTip();'/>";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<select name='exist_"+list[i]['productId']+"' id='exist_"+list[i]['productId']+"' onchange='giveUnsaveTip();'>";
	                        			shtml += "<option value='1'>";
	                        			shtml += "有</option>";
	                        			shtml += "<option value='0'>";
	                        			shtml += "无</option>";
	                        			shtml += "</select>";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<select name='claims_type_"+list[i]['productId']+"' id='claims_type_"+list[i]['productId']+"' onchange='changeClaimsType(this);' class='claimsTypeSelect'>";
	                        			shtml += "<option value='0'>";
	                        			shtml += "整单理赔</option>";
	                        			shtml += "<option value='1'>";
	                        			shtml += "按sku理赔</option>";
	                        			shtml += "<option value='2'>";
	                        			shtml += "按三倍运费理赔</option>";
	                        			shtml += "<option value='3'>";
	                        			shtml += "包装理赔</option>";
	                        			shtml += "</select>";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<a href='javascript:deleteProduct(" + list[i]['productId'] + ");'>删除</a>";
	                        			shtml += "</td>";
	                        			shtml += "</tr>";
	                        		}
	                        		shtml+="</tbody>";
	                        		shtml+="</table>";
	                        		
	                        		shtml += "<br/><br/><div style='border:1 blue dashed;width:70%;height:60px;'>";
	                        		shtml += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;理赔原因:";
	                        		shtml += "<select name='reasonType' id='reasonType'>";
                        			shtml += "<option value='-1' <%= cvBean.getReasonType() == -1 ? "selected" : "" %>>";
                        			shtml += "---请选择---</option>";
                        			shtml += "<option value='0' <%= cvBean.getReasonType() == 0 ? "selected" : "" %>>";
                        			shtml += "包装破损，可换</option>";
                        			shtml += "<option value='1' <%= cvBean.getReasonType() == 1 ? "selected" : "" %>>";
                        			shtml += "包装破损，不可换</option>";
                        			shtml += "<option value='2' <%= cvBean.getReasonType() == 2 ? "selected" : "" %>>";
                        			shtml += "内件物品溢漏</option>";
                        			shtml += "<option value='3' <%= cvBean.getReasonType() == 3 ? "selected" : "" %>>";
                        			shtml += "被溢漏商品污染</option>";
                        			shtml += "<option value='4' <%= cvBean.getReasonType() == 4 ? "selected" : "" %>>";
                        			shtml += "内件物品破损</option>";
                        			shtml += "<option value='5' <%= cvBean.getReasonType() == 5 ? "selected" : "" %>>";
                        			shtml += "内件物品丢失</option>";
                        			shtml += "<option value='6' <%= cvBean.getReasonType() == 6 ? "selected" : "" %>>";
                        			shtml += "透明外膜被撕</option>";
                        			shtml += "<option value='7' <%= cvBean.getReasonType() == 7 ? "selected" : "" %>>";
                        			shtml += "内存卡丢失</option>";
                        			shtml += "<option value='8' <%= cvBean.getReasonType() == 8 ? "selected" : "" %>>";
                        			shtml += "3C礼包丢失耳塞</option>";
                        			shtml += "<option value='9' <%= cvBean.getReasonType() == 9 ? "selected" : "" %>>";
                        			shtml += "丢失手机</option>";
                        			shtml += "<option value='10' <%= cvBean.getReasonType() == 10 ? "selected" : "" %>>";
                        			shtml += "商品被掉包</option>";
                        			shtml += "<option value='11' <%= cvBean.getReasonType() == 11 ? "selected" : "" %>>";
                        			shtml += "其他</option>";
                        			shtml += "</select>";
                        			shtml += "&nbsp;&nbsp;&nbsp;&nbsp;<input type='text' style='width:300px;' name='reasonRemark' id='reasonRemark' value='<%= cvBean.getReasonRemark()%>'>(备注)";
	                        		shtml += "</div>";
	                        		
	                        		shtml+="<button onclick='editClaims();' >&nbsp;保存&nbsp;</button>";
	                        		document.getElementById("formDiv").innerHTML=shtml;
	                        		document.getElementById("hasAddOrder").value="1";
	                        		giveUnsaveTip();
	                        	}
	                        },
	                        error: function() {          //如果过程中出错了调用的方法
	                             alert("验证出错");
	                        }
	                  });
	   			}
   			}
   		}
   		
   		function deleteProduct(pId) {
   		
   				var hasAddOrder = document.getElementById("hasAddOrder").value;
	   			if( hasAddOrder == "0" ) {
	   				alert("还没有添加订单中的商品，无法删除！");
	   				return;
	   			}
   				var currentIds = document.getElementsByName("currentProductIds");
   				if( currentIds != null ) {
   					var params = "";
   					for ( var i = 0; i < currentIds.length; i++ ) {
   						var temp = currentIds[i].value;
   						var count2 = document.getElementById("current_count_" + temp).value;
   						params +="&currentProductIds=" + temp;
   						params += "&current_count_"+temp+"=" + count2;
   					}
   					params += "&deleteProductId="+pId;
   					params += "&orderId="+ document.getElementById("currentOrderId").value;
	   				params += "&orderStockId="+ document.getElementById("currentOrderStockId").value;
	   				$.ajax({
	                        type: "GET", //调用方式  post 还是 get
	                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=deleteCurrentOrderProduct"+params, //访问的地址
	                        dataType: "text", //返回的数据的形式
	                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
	                        	var json = eval('(' + data + ')');
	                        	if( json['status'] == "fail" ) {
	                        		alert(json['tip']);
	                        	} else if ( json['status'] == "clear" ) {
	                        		document.getElementById("hasAddOrder").value="0";
	                        		document.getElementById("formDiv").innerHTML="<button onclick='editClaims();' >&nbsp;保存&nbsp;</button>";
	                        	} else if ( json['status'] == "success" ) {
	                        		var list = json['productList'];
	                        		var x = list.length;
	                        		var shtml = "";
	                        		shtml += "<input type='hidden' name='currentOrderId' id='currentOrderId' value='" + json['currentOrderId'] + "' />";
	                        		shtml+="<input type='hidden' name='currentOrderStockId' id='currentOrderStockId' value='" + json["orderStockId"] + "' />";
	                        		shtml += "<table align='center' width='82%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >";
									shtml += "<tbody>";
									shtml += "<tr bgcolor='#484891' >";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>订单号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>包裹单号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>快递公司</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>原名称</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>产品线</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>产品编号</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>数量</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>有无实物</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>理赔方式</font>";
                        			shtml += "</td>";
                        			shtml += "<td align='center'>";
                        			shtml += "<font color='#FFFFFF'>操作</font>";
                        			shtml += "</td>";
                        			shtml += "</tr>";
                        			shtml += "<tr bgcolor='#FFFFFF' >";
                        			shtml += "<td align='center' rowspan='" + x + "'>";
                        			shtml += json['currentOrderCode'];
                        			shtml += "</td>";
                        			shtml += "<td align='center' rowspan='" + x + "'>";
                        			shtml += json['packageCode'];
                        			shtml += "</td>";
                        			shtml += "<td align='center' rowspan='" + x + "'>";
                        			shtml += json['deliverCompany'];
                        			shtml += "</td>";
	                        		for( var i = 0; i < x; i++ ) {
	                        			if( i == 0 ) {
	                        				
	                        			} else {
	                        				shtml += "<tr bgcolor='#FFFFFF' >";
	                        			}
	                        			shtml += "<td align='center'>";
	                        			shtml += list[i]['oriName'];
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += list[i]['productLine'];
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += list[i]['productCode'];
	                        			shtml += "<input type='hidden' name='currentProductIds' id='current_id_" + list[i]['productId'] + "' value='" + list[i]['productId'] + "' />";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<input type='text' name='current_count_" + list[i]['productId'] + "' id='current_count_" + list[i]['productId'] +"' size='6' value='" + list[i]['count'] + "' onchange='giveUnsaveTip();'/>";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<select name='exist_"+list[i]['productId']+"' id='exist_"+list[i]['productId']+"' onchange='giveUnsaveTip();'>";
	                        			shtml += "<option value='1'>";
	                        			shtml += "有</option>";
	                        			shtml += "<option value='0'>";
	                        			shtml += "无</option>";
	                        			shtml += "</select>";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<select name='claims_type_"+list[i]['productId']+"' id='claims_type_"+list[i]['productId']+"' onchange='changeClaimsType(this);' class='claimsTypeSelect'>";
	                        			shtml += "<option value='0'>";
	                        			shtml += "整单理赔</option>";
	                        			shtml += "<option value='1'>";
	                        			shtml += "按sku理赔</option>";
	                        			shtml += "<option value='2'>";
	                        			shtml += "按三倍运费理赔</option>";
	                        			shtml += "<option value='3'>";
	                        			shtml += "包装理赔</option>";
	                        			shtml += "</select>";
	                        			shtml += "</td>";
	                        			shtml += "<td align='center'>";
	                        			shtml += "<a href='javascript:deleteProduct(" + list[i]['productId'] + ");'>删除</a>";
	                        			shtml += "</td>";
	                        			shtml += "</tr>";
	                        		}
	                        		shtml+="</tbody>";
	                        		shtml+="</table>";
	                        		
	                        		shtml += "<br/><br/><div style='border:1 blue dashed;width:70%;height:60px;'>";
	                        		shtml += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;理赔原因:";
	                        		shtml += "<select name='reasonType' id='reasonType'>";
                        			shtml += "<option value='-1' <%= cvBean.getReasonType() == -1 ? "selected" : "" %>>";
                        			shtml += "---请选择---</option>";
                        			shtml += "<option value='0' <%= cvBean.getReasonType() == 0 ? "selected" : "" %>>";
                        			shtml += "包装破损，可换</option>";
                        			shtml += "<option value='1' <%= cvBean.getReasonType() == 1 ? "selected" : "" %>>";
                        			shtml += "包装破损，不可换</option>";
                        			shtml += "<option value='2' <%= cvBean.getReasonType() == 2 ? "selected" : "" %>>";
                        			shtml += "内件物品溢漏</option>";
                        			shtml += "<option value='3' <%= cvBean.getReasonType() == 3 ? "selected" : "" %>>";
                        			shtml += "被溢漏商品污染</option>";
                        			shtml += "<option value='4' <%= cvBean.getReasonType() == 4 ? "selected" : "" %>>";
                        			shtml += "内件物品破损</option>";
                        			shtml += "<option value='5' <%= cvBean.getReasonType() == 5 ? "selected" : "" %>>";
                        			shtml += "内件物品丢失</option>";
                        			shtml += "<option value='6' <%= cvBean.getReasonType() == 6 ? "selected" : "" %>>";
                        			shtml += "透明外膜被撕</option>";
                        			shtml += "<option value='7' <%= cvBean.getReasonType() == 7 ? "selected" : "" %>>";
                        			shtml += "内存卡丢失</option>";
                        			shtml += "<option value='8' <%= cvBean.getReasonType() == 8 ? "selected" : "" %>>";
                        			shtml += "3C礼包丢失耳塞</option>";
                        			shtml += "<option value='9' <%= cvBean.getReasonType() == 9 ? "selected" : "" %>>";
                        			shtml += "丢失手机</option>";
                        			shtml += "<option value='10' <%= cvBean.getReasonType() == 10 ? "selected" : "" %>>";
                        			shtml += "商品被掉包</option>";
                        			shtml += "<option value='11' <%= cvBean.getReasonType() == 11 ? "selected" : "" %>>";
                        			shtml += "其他</option>";
                        			shtml += "</select>";
                        			shtml += "&nbsp;&nbsp;&nbsp;&nbsp;<input type='text' style='width:300px;' name='reasonRemark' id='reasonRemark' value='<%= cvBean.getReasonRemark()%>'>(备注)";
	                        		shtml += "</div>";
	                        		
	                        		shtml+="<button onclick='editClaims();' >&nbsp;保存&nbsp;</button>";
	                        		document.getElementById("formDiv").innerHTML=shtml;
	                        		
	                        	}
	                        	giveUnsaveTip();
	                        },
	                        error: function() {          //如果过程中出错了调用的方法
	                             alert("验证出错");
	                        }
	                  });
   					
   					
   				} else {
   					alert("已添加列表为空！");
   					return;
   				}
			
   		}
   		
   		
   		function addToAddForm(obj) {
   			var productId = obj.value;
   			var input = document.createElement("input" );
			input.id= "productId_" + productId;
			input.name= "productIds";
			input.value= productId;
			input.type= "hidden";
			 var temp = document.getElementById("addForm" );
			temp.appendChild(input);
   		}
   		
   		function editClaims () {
   			var reasonRemark = document.getElementById("reasonRemark").value;
   			reasonRemark = reasonRemark.trim();
   			if( reasonRemark.length > 50 ) {
   				alert("理赔原因备注最多可填写50字！");
   				return;
   			}	
   			var currentIds = document.getElementsByName("currentProductIds");
   				if( currentIds != null && currentIds.length > 0 ) {
   					var params = "";
   					params += "wareArea=" + document.getElementById("wareArea").value;
   					for ( var i = 0; i < currentIds.length; i++ ) {
   						var temp = currentIds[i].value;
   						var count2 = document.getElementById("current_count_" + temp).value;
   						var exist2 = document.getElementById("exist_"+temp).value;
   						var claimsType = document.getElementById("claims_type_"+temp).value;
   						params +="&currentProductIds=" + temp;
   						params += "&current_count_"+temp+"=" + count2;
   						params += "&exist_"+temp+"="+exist2;
   						params += "&claims_type_"+temp+"="+claimsType;
   						
   					}
   					params += "&currentOrderId="+ document.getElementById("currentOrderId").value;
	   				params += "&currentOrderStockId="+ document.getElementById("currentOrderStockId").value;
	   				params += "&reasonType="+ document.getElementById("reasonType").value;
	   				params += "&reasonRemark="+ reasonRemark;
	   				params += "&id="+document.getElementById("id_id").value;
	   				$.ajax({
	                        type: "POST", //调用方式  post 还是 get
	                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=editClaimsVerification", //访问的地址
	                        data: params,
	                        dataType: "text", //返回的数据的形式
	                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
	                        	var json = eval('(' + data + ')');
	                        	if( json['status'] == "fail" ) {
	                        		alert(json['tip']);
	                        	} else if ( json['status'] == "success" ) {
	                        		alert(json['tip']);
	                        		window.location=json['url'];
	                        	}
	                        },
	                        error: function() {          //如果过程中出错了调用的方法
	                             alert("验证出错");
	                        }
	                  });
   					
   				} else {
   					var params = "";
   					params += "&wareArea=" + document.getElementById("wareArea").value;
   					params += "&id="+document.getElementById("id_id").value;
	   				$.ajax({
	                        type: "POST", //调用方式  post 还是 get
	                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=editClaimsVerification"+params, //访问的地址
	                        dataType: "text", //返回的数据的形式
	                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
	                        	var json = eval('(' + data + ')');
	                        	if( json['status'] == "fail" ) {
	                        		alert(json['tip']);
	                        	} else if ( json['status'] == "success" ) {
	                        		alert(json['tip']);
	                        		window.location=json['url'];
	                        	}
	                        },
	                        error: function() {          //如果过程中出错了调用的方法
	                             alert("验证出错");
	                        }
	                  });
   				}
   		}
   		
   		function goAudit(id) {
   			var tip = document.getElementById("tipSpan").innerHTML;
   				if( tip == null || tip == "" ) {
   					if( window.confirm("你确认要提交理赔单么？")) {
	   					window.location="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=confirmClaimsVerification&id="+id;
	   					return;
		   			} else {
		   				return;
		   			}
   				} else {
					alert("当前存在为保存的编辑！");
					return; 				
   				}
   			
   			
   		}
   		
   		function giveUnsaveTip() {
   			document.getElementById("tipSpan").innerHTML="<font color='red'>当前有未保存的编辑!</font>";
   			return;
   		}
   		
   		function changeClaimsType(obj) {
   			var value = obj.value;
   			if( value == "0" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					sel.value="0"
   				}
   			} else if ( value == "1" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					if( sel.value == "0" || sel.value == "2" ) {
   						sel.value = "1";
   					}
   				}
   			} else if ( value == "2" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					sel.value="2"
   				}
   			} else if ( value == "3" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					if( sel.value == "0" || sel.value == "2" ) {
   						sel.value = "3";
   					}
   				}
   			}
   			document.getElementById("tipSpan").innerHTML="<font color='red'>当前有未保存的编辑!</font>";
   			return;
   		}
   		
   		
	</script>

  </head>
  <body>
  <br/>
  	<input type="hidden" name="hasOrder" id="hasOrder" value="0" />
  	<input type="hidden" name="id" id="id_id" value="<%= cvBean.getId()%>" />
  	<div style="width:80%;margin-left:10%;">
  	理赔单号： <%= cvBean.getCode()%>
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	状态：<%= cvBean.getStatusName()%> <button onclick="goAudit(<%= cvBean.getId()%>);">提交审核</button>
  	</div>
  	<br/>
  	<div style="width:80%;margin-left:10%;font-size:14px;"><b>添加商品：</b></div>
  	<center><fieldset style="width:80%;">
<div style="margin-left:10px;position:relative;"> 
订单号：
<form action="" method="post" onsubmit="return getOrderInfo();" >
<input type="text" name="orderCode" id="orderCode" size="15" value='' > 
</form>
<button onclick="getOrderInfo();">查看订单</button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

<form action="" method="post" onsubmit="return addProductOrCount();" >
商品编号/商品条码：
<input type="text" name="addProductCode" id="addProductCode" size="15" value=''> 
</form>
<button onclick="getChecks();">添加商品</button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<div id="tableDiv">
	<table align="center" width="94%" border="0" cellspacing="1px" bgcolor="#D8D8D5" cellpadding="1px" id="orderInfoTable" >
		<tbody>
		<tr bgcolor="#484891" >
			<td align="center">
			<font color="#FFFFFF">序号</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">产品编号</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">产品原名称</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">小店名称</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">数量</font>
			</td>
		</tr>
		<tr bgcolor="#FFFFFF" >
			<td align="center" colspan="5">
			还没有订单信息可查看
			</td>
		</tr>
		</tbody>
		</table>
</div>

</div>
</fieldset></center>

<br/>
<div align="center"><span id="tipSpan"></span></div>
<br/>
			<center>库地区:<%= wareAreaSelectLable%></center>
	<%
		List list = cvBean.getClaimsVerificationProductList();
		int x = list.size();
		if( x > 0 ){
	%>

	<input type="hidden" name="hasAddOrder" id="hasAddOrder" value="1" />
	<div id="formDiv" align="center">
			<input type='hidden' name='currentOrderId' id='currentOrderId' value='<%= vorder.getId()%>' />
			<input type='hidden' name='currentOrderStockId' id='currentOrderStockId' value='' />
			<table align='center' width='82%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
			<tbody>
			<tr bgcolor='#484891' >
			<td align='center'>
			<font color='#FFFFFF'>订单号</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>包裹单号</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>快递公司</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>原名称</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>产品线</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>产品编号</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>数量</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>有无实物</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>理赔方式</font>
			</td>
			<td align='center'>
			<font color='#FFFFFF'>操作</font>
			</td>
			</tr>
			<tr bgcolor='#FFFFFF' >
			<td align='center' rowspan='<%= x%>'>
			<%= vorder.getCode()%>
			</td>
			<td align='center' rowspan='<%= x%>'>
			<%= cvBean.getPackageCode()%>
			</td>
			<td align='center' rowspan='<%= x%>'>
			<%= voOrder.deliverMapAll.get(""+vorder.getDeliver())%>
			</td>
			<%
				for( int i = 0; i < x; i++ ) {
				ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) list.get(i);
				if( i == 0 ) {
					
				} else {
			
			%>
			<tr bgcolor='#FFFFFF' >
			<%
				}
			%>
				<td align='center'>
				<%= cvpBean.getProduct().getOriname()%>
				</td>
				<td align='center'>
				<%= cvpBean.getProductLineName()%>
				</td>
				<td align='center'>
				<%= cvpBean.getProduct().getCode()%>
				<input type='hidden' name='currentProductIds' id='current_id_<%= cvpBean.getProduct().getId()%>' value='<%= cvpBean.getProduct().getId()%>' />
				</td>
				<td align='center'>
				<input type='text' name='current_count_<%= cvpBean.getProduct().getId()%>' id='current_count_<%= cvpBean.getProduct().getId()%>' size='6' value='<%= cvpBean.getCount()%>' onchange='giveUnsaveTip();'/>
				</td>
				<td align='center'>
				<select name='exist_<%= cvpBean.getProduct().getId()%>' id='exist_<%= cvpBean.getProduct().getId()%>' onchange='giveUnsaveTip();'>
				<option value='1' <%= cvpBean.getExist() == 1 ? "selected" : ""%>>
				有</option>
				<option value='0' <%= cvpBean.getExist() == 0 ? "selected" : ""%>>
				无</option>
				</select>
				</td>
				<td align='center'>
				<select name='claims_type_<%= cvpBean.getProduct().getId()%>' id='claims_type_<%= cvpBean.getProduct().getId()%>' onchange='changeClaimsType(this);' class='claimsTypeSelect'>
				<option value='0' <%= cvpBean.getClaimsType() == 0 ? "selected" : ""%>>
				整单理赔</option>
				<option value='1' <%= cvpBean.getClaimsType() == 1 ? "selected" : ""%>>
				按sku理赔</option>
				<option value='2' <%= cvpBean.getClaimsType() == 2 ? "selected" : ""%>>
				按三倍运费理赔</option>
				<option value='3' <%= cvpBean.getClaimsType() == 3 ? "selected" : ""%>>
				包装理赔</option>
				</select>
				</td>
				<td align='center'>
				<a href='javascript:deleteProduct(<%= cvpBean.getProduct().getId()%>);'>删除</a>
				</td>
				</tr>
			<%
				}
			%>
			</tbody>
			</table>
			<br/><br/><div style='border:1 blue dashed;width:70%;height:60px;'>
	        <br/>&nbsp;&nbsp;&nbsp;&nbsp;理赔原因:
			<select name='reasonType' id='reasonType'>
				<option value='-1' <%= cvBean.getReasonType() == -1 ? "selected" : "" %>>---请选择---</option>
				<option value='0' <%= cvBean.getReasonType() == 0 ? "selected" : "" %>>包装破损，可换</option>
				<option value='1' <%= cvBean.getReasonType() == 1 ? "selected" : "" %>>包装破损，不可换</option>
				<option value='2' <%= cvBean.getReasonType() == 2 ? "selected" : "" %>>内件物品溢漏</option>
				<option value='3' <%= cvBean.getReasonType() == 3 ? "selected" : "" %>>被溢漏商品污染</option>
				<option value='4' <%= cvBean.getReasonType() == 4 ? "selected" : "" %>>内件物品破损</option>
				<option value='5' <%= cvBean.getReasonType() == 5 ? "selected" : "" %>>内件物品丢失</option>
				<option value='6' <%= cvBean.getReasonType() == 6 ? "selected" : "" %>>透明外膜被撕</option>
				<option value='7' <%= cvBean.getReasonType() == 7 ? "selected" : "" %>>内存卡丢失</option>
				<option value='8' <%= cvBean.getReasonType() == 8 ? "selected" : "" %>>3C礼包丢失耳塞</option>
				<option value='9' <%= cvBean.getReasonType() == 9 ? "selected" : "" %>>丢失手机</option>
				<option value='10' <%= cvBean.getReasonType() == 10 ? "selected" : "" %>>商品被掉包</option>
				<option value='11' <%= cvBean.getReasonType() == 11 ? "selected" : "" %>>其他</option>
			</select> &nbsp;&nbsp;&nbsp;&nbsp;<input type='text' style='width:300px;' name='reasonRemark' id='reasonRemark' value='<%= cvBean.getReasonRemark()%>'>(备注)
	   </div>
			<button onclick='editClaims();' >&nbsp;保存&nbsp;</button>
		</div>
	<%
		} else {
	%>
	<input type="hidden" name="hasAddOrder" id="hasAddOrder" value="0" />
	<div id="formDiv" align="center">
	</div>
	<%
		}
	%>
</body>
</html>