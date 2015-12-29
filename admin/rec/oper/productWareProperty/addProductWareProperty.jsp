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
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	$.messager.alert("提示","请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	   				$.messager.alert("提示","请填入整数！！");
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
		   			$.messager.alert("提示","请不要输入大于3位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	   				$.messager.alert("提示","请填入整数！！");
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
		   			$.messager.alert("提示","请不要输入大于6位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	   				$.messager.alert("提示","请填入整数！！");
	   			}
   			}
   		}
   		function checkBinningNumber(obj){
   			var pattern2 = /^[1-9][0-9]{0,}$/;
   			var number = obj.value;
   			if(!pattern2.exec(number)){
   				$.messager.alert("提示","请填入整数！！");
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
		    	  $.messager.alert("提示","可辨识信息不可以为空" );
                  obj.value="" ;
                  obj.focus();
                  return false ;
		      }
		       //验证 name中是否有 特殊符号
		      for (var i = 0; i < s.length; i++) {
	             if( pattern.exec(s.substr(i,1))) {
	            	  $.messager.alert("提示","请勿输入特殊字符" );
	                  obj.value="" ;
	                  obj.focus();
	                  return false ;
	            } else if ( pattern3.exec(s.substr(i,1))) {
	            	valLength += 1;
	            } else if ( pattern4.exec(s.substr(i,1))) {
	            	valLength += 2;
	            } else {
	            	$.messager.alert("提示","无法识别的字符！");
	            	obj.value="";
	            	obj.focus();
	            	return false;
	            }
		      }
   			if( name == null || name == "" ) {
   				$.messager.alert("提示","请填入可辨识信息值！");
   				obj.value="";
   				obj.focus();
   				return false;
   			}
   			if( valLength > 10 ) {
   				$.messager.alert("提示","汉字按两位算，字母数字按一位算，字符长度超过了10，请修改！");
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
                        		$.messager.alert("提示",json['tip']);
                        	} else if ( json['status'] == "success" ) {
                        		$("#productBarCode").attr('value',json['productBarCode']);
                        		//$("#wareType").attr('value',json['wareType']);
                        		//$("#standardCount").attr('value', json['standardCount']);
                        	}
                        	
                        },
                        error: function() {          //如果过程中出错了调用的方法
                        	$.messager.alert("提示","验证出错");
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
	                        		$.messager.alert("提示",json['tip']);
	                        	} else if ( json['status'] == "success" ) {
	                        		$("#productCode").attr('value',json['productCode']);
	                        		//$("#wareType").attr('value',json['wareType']);
	                        		//$("#standardCount").attr('value', json['standardCount']);
	                        	
	                        	}
	                        	
	                        },
	                        error: function() {          //如果过程中出错了调用的方法
	                        	$.messager.alert("提示","验证出错");
	                        }
	                  });
                  }
   		}
   		
   		var openOther = 0;
   		
   		function openAddProductWareType() {
   			if( openOther == 0 ) {
   				openOther = 1;
   			}
   			
   			window.open("<%= request.getContextPath()%>/admin/rec/oper/productWareType/addProductWareType.jsp", "_blank");
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
   				$.messager.alert("提示","请至少填写商品编号，和商品条码其中的一个!");
   				return;
   			}
   			if( checkEffect == null || checkEffect == "" || parseInt(checkEffect) == -1 ) {
   				$.messager.alert("提示","请选择质检分类！");
   				return;
   			}
   			if( length == null || length == "" || parseInt(length) <= 0 ) {
   				$.messager.alert("提示","请填写包裹长度！");
   				return;
   			}
   			if( width == null || width == "" || parseInt(width) <= 0 ) {
   				$.messager.alert("提示","请填写包裹宽度！");
   				return;
   			}
   			if( height == null || height == "" || parseInt(height) <= 0 ) {
   				$.messager.alert("提示","请填写包裹高度！");
   				return;
   			}
   			if( weight == null || weight == "" || parseInt(weight) <= 0 ) {
   				$.messager.alert("提示","请填写重量！");
   				return;
   			}
   			if( identityInfo == null || identityInfo == "" ) {
   				$.messager.alert("提示","请填写可识别信息！");
   				$("#identityInfo").focus();
   				return;
   			}
   			if( wareType == null || wareType == "" || parseInt(wareType) == -1 ) {
   				$.messager.alert("提示","请选择商品物流分类");
   				return;
   			}
   			document.getElementById("submitConfirm").disabled="true";
   			document.addForm.submit();
   			return;
   		}
   		
	</script>

  </head>
  <body onload="$('#submitConfirm').removeAttr('disabled');">
  <div style="width:430px;height:490px;" class="easyui-panel" data-options="title:'添加商品物流属性'">
   		<div style="width:400px;height:421px;">
   			<form action="<%= request.getContextPath()%>/admin/addProductWareProperty.mmx" name="addForm" method="post">
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
   					<br/>
   					<br/>
   					标准装箱量：<input type="text" name="binning" id="binning" onchange="checkBinningNumber(this);"/>
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
   							 <a href="javascript:openAddProductWareType();" class="easyui-linkbutton" data-options="iconCls:'icon-add'">添加新商品物流分类</a>
   							 
   							 <%
   							 	}
   							 %>
   						<br/>
   						<br/>
   						<br/>
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						<a href="javascript:check();" id="submitConfirm" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">确认提交</a>
   			</form>
   		</div>
   		<br>
   	</div>
</body>
</html>