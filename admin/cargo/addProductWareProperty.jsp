<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	List checkEffectList = (ArrayList) request.getAttribute("checkEffectList");
	List productWareTypeList = (ArrayList) request.getAttribute("productWareTypeList");
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean addProductWarePropertyRight = group.isFlag(725);
%>
<html>
  <head>
    
    <title>添加商品物流属性</title>
    
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
   		
   		function checkLengthNumber( obj ) {
   			var pattern = /^[0-9]{1,3}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于3位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}
   		
   		function checkWeightNumber( obj ) {
   			var pattern = /^[0-9]{1,6}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于6位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}
   		function checkBinningNumber(obj){
   			var pattern2 = /^[1-9][0-9]{0,}$/;
   			var number = obj.value;
   			if(!pattern2.exec(number)){
   				alert("请填入整数！！");
   				obj.value="";
   				obj.focus();
   			}
   		}
   		function checkQualify( obj ) {
   			var pattern1 = /^[0-9a-zA-Z]{1,10}$/; //只输入数字和字母的正则
            var pattern2 = /^[\u4e00-\u9fa5]{1,5}$/; //只输入汉字的正则
            var pattern3 = /^[0-9a-zA-Z]{1}$/;
            var pattern4 = /^[\u4e00-\u9fa5]{1}$/;
            var valLength = 0;
             var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]" );
		       var rs = "";
		       var s = obj.value;
		       var name = s.trim();
		       //在去掉空格符号后 看是否为空
		      if ( name == "" ) {
                  alert( "可辨识信息不可以为空" );
                  obj.value="" ;
                  obj.focus();
                  return false ;
		      }
		       //验证 name中是否有 特殊符号
		      for (var i = 0; i < s.length; i++) {
	             if( pattern.exec(s.substr(i,1))) {
	                  alert( "请勿输入特殊字符" );
	                  obj.value="" ;
	                  obj.focus();
	                  return false ;
	            } else if ( pattern3.exec(s.substr(i,1))) {
	            	valLength += 1;
	            } else if ( pattern4.exec(s.substr(i,1))) {
	            	valLength += 2;
	            } else {
	            	alert("无法识别的字符！");
	            	obj.value="";
	            	obj.focus();
	            	return false;
	            }
		      }
   			if( name == null || name == "" ) {
   				alert("请填入可辨识信息值！");
   				obj.value="";
   				obj.focus();
   				return false;
   			}
   			if( valLength > 10 ) {
   				alert("汉字按两位算，字母数字按一位算，字符长度超过了10，请修改！");
   				obj.value="";
   				obj.focus();
   				return false;
   			} else {
   				return true;
   			}
   		}
   		
   		function autoFillWithProductCode() {
   			var code = document.getElementById("productCode").value;
   			if( document.getElementById("productBarCode").value == "" && document.getElementById("productCode").value != ""  ) {
   				$.ajax({
                        type: "GET", //调用方式  post 还是 get
                        url: "<%=request.getContextPath()%>/admin/productWarePropertyAction.do?method=getProductInfoByCode&productCode="+code, //访问的地址
                        dataType: "text", //返回的数据的形式
                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
                        	var json = eval('(' + data + ')');
                        	if( json['status'] == "fail" ) {
                        		alert(json['tip']);
                        	} else if ( json['status'] == "success" ) {
                        		$("#productBarCode").attr('value',json['productBarCode']);
                        		//$("#wareType").attr('value',json['wareType']);
                        		//$("#standardCount").attr('value', json['standardCount']);
                        	}
                        	
                        },
                        error: function() {          //如果过程中出错了调用的方法
                             alert("验证出错");
                        }
                  });
   			}
   		}	
   		
   		function autoFillWithProductBarCode() {
   			var barCode = document.getElementById("productBarCode").value;
   			if( document.getElementById("productCode").value == "" && document.getElementById("productBarCode").value != "" ) {
	   				$.ajax({
	                        type: "GET", //调用方式  post 还是 get
	                        url: "<%=request.getContextPath()%>/admin/productWarePropertyAction.do?method=getProductInfoByBarCode&productBarCode="+barCode, //访问的地址
	                        dataType: "text", //返回的数据的形式
	                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
	                        	var json = eval('(' + data + ')');
	                        	if( json['status'] == "fail" ) {
	                        		alert(json['tip']);
	                        	} else if ( json['status'] == "success" ) {
	                        		$("#productCode").attr('value',json['productCode']);
	                        		//$("#wareType").attr('value',json['wareType']);
	                        		//$("#standardCount").attr('value', json['standardCount']);
	                        	
	                        	}
	                        	
	                        },
	                        error: function() {          //如果过程中出错了调用的方法
	                             alert("验证出错");
	                        }
	                  });
                  }
   		}
   		
   		var openOther = 0;
   		
   		function openAddProductWareType() {
   			if( openOther == 0 ) {
   				
   				openOther = 1;
   			}
   			
   			window.open("<%= request.getContextPath()%>/admin/cargo/addProductWareType.jsp", "_blank");
   		}
   		
   		function check() {
   			var productCode = $("#productCode").val();
   			var productBarCode = $("#productBarCode").val();
   			var checkEffect = $("#checkEffect").val();
   			var length = $("#length").val();
   			var width = $("#width").val();
   			var height = $("#height").val();
   			var weight = $("#weight").val();
   			var wareType = $("#wareType").val();
   			var identityInfo = $("#identityInfo").val();
   			
   			if( (productCode == null || productCode == "") && ( productBarCode == null || productBarCode == "" )) {
   				alert("请至少填写商品编号，和商品条码其中的一个!");
   				return false;
   			}
   			if( checkEffect == null || checkEffect == "" || parseInt(checkEffect) == -1 ) {
   				alert("请选择质检分类！");
   				return false;
   			}
   			if( length == null || length == "" || parseInt(length) <= 0 ) {
   				alert("请填写包裹长度！");
   				return false;
   			}
   			if( width == null || width == "" || parseInt(width) <= 0 ) {
   				alert("请填写包裹宽度！");
   				return false;
   			}
   			if( height == null || height == "" || parseInt(height) <= 0 ) {
   				alert("请填写包裹高度！");
   				return false;
   			}
   			if( weight == null || weight == "" || parseInt(weight) <= 0 ) {
   				alert("请填写重量！");
   				return false;
   			}
   			if( identityInfo == null || identityInfo == "" ) {
   				alert("请填写可识别信息！");
   				$("#identityInfo").focus();
   				return false;
   			}
   			if( wareType == null || wareType == "" || parseInt(wareType) == -1 ) {
   				alert("请选择商品物流分类");
   				return false;
   			}
   			document.getElementById("submitConfirm").disabled="true";
   			return true;
   		}
   		
	</script>

  </head>
  <body onload="$('#submitConfirm').removeAttr('disabled');">
  <div style="margin-left:15px;margin-top:15px;">

  	<h2>添加商品物流属性:</h2>
   		<div style="width:400px;height:381px;border-style:solid;border-width:1px;border-color:#000000;">
   			<form action="<%= request.getContextPath()%>/admin/productWarePropertyAction.do?method=addProductWareProperty" method="post" onsubmit="return check();">
   					产品编号：<input type="text" size="11" name="productCode" id="productCode" onblur="autoFillWithProductCode();"/>
   					商品条码：<input type="text" size="11" name="productBarCode" id="productBarCode" onblur="autoFillWithProductBarCode();" />
   					<br/>
   					<h5>入库属性：</h5>
   					质检分类：<select name="checkEffect" id="checkEffect">
   							<%  if (checkEffectList.size() == 0 ) { %>
   								<option value="-1">没有质检分类可用</option>
   							<%
   								} else {
   								%>
   								<option value="-1">请选择分类</option>
   								<%
   								int x = checkEffectList.size();
   								for( int i = 0; i < x ; i++ ) { 
   								CheckEffectBean cfb = (CheckEffectBean) checkEffectList.get(i);
   							%>
   								<option value="<%= cfb.getId()%>"><%= cfb.getName()%></option>
   							<%
   								}
   								}
   							%>
   							 </select>
   							 &nbsp;&nbsp;&nbsp;&nbsp;标准装箱量：<input type="text" name="binning" id="binning" onchange="checkBinningNumber(this);"/>
   					<br/>
   					<h5>仓储属性：</h5>
   					最小包装尺寸：长<input type="text" size="6" name="length" id="length" onchange="checkLengthNumber(this);"/>cm 
				   			     宽<input type="text" size="6" name="width" id="width" onchange="checkLengthNumber(this);"/>cm 
				   				 高<input type="text" size="6" name="height" id="height" onchange="checkLengthNumber(this);"/>cm
   					<br/>
   					<br/>
   					重量： <input type="text" size="6" name="weight" id="weight" onchange="checkWeightNumber(this);" />g
   					<br/>
   					<br/>
   					可辨识信息：<input type="text" size="13" name="identityInfo" id="identityInfo" onchange="checkQualify(this);" />
   					<h5>出库属性：</h5>
   					商品物流分类：<select name="wareType" id="wareType">
   									<%  if (productWareTypeList.size() == 0 ) { %>
   								<option value="-1">没有质检分类可用</option>
   							<%
   								} else {
   								%>
   								<option value="-1">请选择分类</option>
   								<%
   								int x = productWareTypeList.size();
   								for( int i = 0; i < x ; i++ ) { 
   								ProductWareTypeBean pwtBean = (ProductWareTypeBean) productWareTypeList.get(i);
   							%>
   								<option value="<%= pwtBean.getId()%>"><%= pwtBean.getName()%></option>
   							<%
   								}
   								}
   							%>
   							 </select> &nbsp;&nbsp; 
   							 <% 
   							 	if( addProductWarePropertyRight ) {
   							 %>
   							 <a href="javascript:openAddProductWareType();">添加新商品物流分类</a>
   							 
   							 <%
   							 	}
   							 %>
   					<div style="left:260px;top:390px;width:120px;position:absolute;">
   						<input type="submit" id="submitConfirm" value="确认提交" />
   					</div>
   			</form>
   		</div>
   		<br>
   	</div>
</body>
</html>