<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	int type = StringUtil.toInt(request.getParameter("type"));
	String boxCode = (String)request.getAttribute("boxCode");
	String tips = (String)request.getAttribute("tips");
	int lightStep = StringUtil.toInt(request.getParameter("lightStep"));
	Map<String,Integer> map = (Map<String,Integer>) request.getAttribute("colorMap");
	int targetNumber = 100;
	int needCount = 0;
	int completeCount = 0;
	int orderCount = 0;
	Object needCountO = request.getAttribute("needCount");
	Object completeCountO = request.getAttribute("completeCount");
	Object orderCountO = request.getAttribute("orderCount");
	if( needCountO != null ) {
		needCount = ((Integer)needCountO).intValue();
	}
	if( completeCountO != null ) {
		completeCount = ((Integer)completeCountO).intValue();
	}
	if( orderCountO != null ) {
		orderCount = ((Integer)orderCountO).intValue();
	}
	String allBoxPCount = (String) request.getAttribute("allBoxPCount");
	String productName = StringUtil.convertNull((String)request.getAttribute("productName"));
	String greenIndexs = (String) request.getAttribute("greenIndexs");
	String redOrangeIndexs = (String) request.getAttribute("redOrangeIndexs");
	SortingBatchGroupBean sbgBean = (SortingBatchGroupBean)request.getAttribute("SortingBatchGroupBean");
%>
<html>
  <head>
    
    <title>分播</title>
    
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
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
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
   		
   		
   		function check1() {
   			var code = document.getElementById("sortingGroupCode").value;
   			if( code == null || code == "" ) {
   				alert("没有填写波次号！");
   				return false;
   			}
   		}
   		
   		
   		function finishNow(code, type, status) {
   			if(window.confirm("你确定要结批这个波次？") ) {
   				if( type == "3" && status == "2" ) {
   					alert("这个波次已经结批，不可以再操作！");
   					return;
   				} else {
	   				window.location="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup2&finishNow=1&sortingGroupCode2=" + code;
   					aiPlug.CloseSocket();
   					return;
   				}
   			} else {
   			
   			}
   		}
   		
   		function resetNow(code, type, status) {
   			if(window.confirm("你确定要重置这个波次？") ) {
   			if( (type == "3" && status == "2")||(type=='2' && status=='1') ) {
   					window.location="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup2&resetNow=1&sortingGroupCode2=" + code;   					
   					aiPlug.CloseSocket();
   					return;
   				} else {
   					alert("这个波次已经结批，不可以再操作！");
   					return;
   				}
   			} else {
   			
   			}
   		}
   		function cancelFinish(code, type, status){
   			if(window.confirm("你确定要取消结批这个波次？") ) {
   				if( status == "2" ) {
   					window.location="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup2&cancelFinish=1&sortingGroupCode2=" + code;
   					aiPlug.CloseSocket();
   					return;
   				} else {
	   				//window.location="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup&cancelFinish=1&sortingGroupCode2=" + code;
	   				alert("这个波次还没有结批，无法取消结批！");
   					return;
   				}
   			} else {
   			
   			}
   		}
   		function focusType(type) {
   			if( type == "1" ) {
   				document.getElementById("sortingGroupCode").focus();
   			} else if ( type == "2") {
   				document.getElementById("sortingGroupCode").disabled=true;
   				document.getElementById("productCode").focus();
   			} else if ( type == "3" ) {
   				 //结批后的波次号 可以编辑
   				document.getElementById("sortingGroupCode").disabled=false;
   				//document.getElementById("sortingGroupCode").focus();
   				document.getElementById("sortingGroupCode").select();
   				document.getElementById("productCode").disabled=true;
   			}
   		}
   		
   		//这部分是操作灯的部分。。。。。
   		var aiPlug = null;

        function openSocket() {
            aiPlug = new ActiveXObject("AiPlug.Bridge");
            // ip port firstAddress, rows, columns 
            aiPlug.OpenSocket("192.168.202.210", 5003, 5, 6, false);
        }
   		//让所有的的灯 亮起来
   		function initLights(totalBoxCount,allBoxPCount) {
            aiPlug.InitLights(totalBoxCount,allBoxPCount);
        }
       var changeNum = function (indexNumber, pCount) {
        	//三个参数， 第一个是  对应的led的序号， 第二个参数是 对应的要显示的数字， 第三个未知。
            aiPlug.OpenLed(indexNumber, pCount, false);
            // 查询灯的状态
            setTimeout("getState(" + indexNumber + ")", 20);
        }
		
		//循环查询灯的状态
        var getState = function (targetIndex) {
            var code = document.getElementById("productCode").value;
            var index = aiPlug.LightIsClosed();
            if (index > -1) {
            	if( index == targetIndex ) {
        			window.location="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup2&productCode=" + code + "&submitType=1";
        			aiPlug.CloseSocket();
        			return;
            	} else {
            		alert('系统异常，请务必联系管理员！');
            		//document.getElementById("productCode").value="";
        			//$("#productCode").removeAttr("disabled");
        			//submitMark = 0;
            		return;
            	}
            }

            setTimeout("getState(" + targetIndex + ")", 20);
        }
        
        var a = 0;
        
        var getNumberOver = function(targetNumber) {
        	var code = document.getElementById("productCode").value;
        	a += 1;
        	var b = 100;
       		if( a > 1000) {
        		if( targetNumber == b ) {
        		alert('差不多了!');
        		window.location="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup2&productCode=" + code + "&submitType=1";
        		submitMark = 0;
        		return;
       		} else {
       			alert("好像不对");
        			document.getElementById("productCode").value="";
        			$("#productCode").removeAttr("disabled");
        			submitMark = 0;
        			return;
        		}
        	}
			setTimeout("getNumberOver(" + targetNumber+ ")", 30);        	
        }
        
        var submitMark = 0;
        function check2() {
   			var code = document.getElementById("productCode").value;
   			if( code == null || code == "" ) {
   				alert("没有填写商品编号/条码！");
   				return false;
   			} else {
   				if( submitMark != 1) {
   					$.ajax({
                        type: "POST",
                        url: "<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=justGetBoxCode&productCode=" + code + "&submitType=1",
                        dataType: "text",
                        success: function(data, textStatus) {
                              var json = eval('(' + data + ')');
                              if( json['status'] == "success" ) {
                              	document.getElementById("boxCodeSpan").innerHTML="<font style='font-size:100pt' color='GREEN'>&nbsp;<b>" + json['boxCode'] + "</b></font>";
                              	document.getElementById("productName").value=json['productName'];
                              	document.getElementById("productCode").disabled='true';
                              	//changeNum(json['index'], json['boxPCount']);
                              	//setTimeout("getNumberOver('" + json['index'] + "')", 30);
                              	//setTimeout("getNumberOver(100)", 30);
                              	setTimeout("changeNum('" + json['index'] + "', '" + json['boxPCount'] + "')", 30);
                              	submitMark = 1;
                              } else {
                              	alert(json['tip']);
                              	if( json['boxCode'] != "" ) {
                              		document.getElementById("boxCodeSpan").innerHTML="<font style='font-size:100pt' color='RED'>&nbsp;<b>" + json['boxCode'] + "</b></font>";
                              	} else {
                              		document.getElementById("boxCodeSpan").innerHTML="<font style='font-size:100pt' color='RED'>&nbsp;<b>" + json['boxCode'] + "</b></font>";
                              	}
                              }
                        },
                        error: function () {
                        	alert("通讯错误！");
                        	return false;
                        }
                  });
   				}
   			}
   			return false;
   		}
   		
   		function Jiepi(allBoxPCount, green, redOrange ) {
            aiPlug.SetLightState(allBoxPCount, green, redOrange);
        }
	</script>
	<script>
		 aiPlug = new ActiveXObject("AiPlug.Bridge");
            // ip port firstAddress, rows, columns 
            aiPlug.OpenSocket("192.168.202.210", 5003, 5, 6, false);
	<%
		if( type == 2 ) {
	%>
           
   		//让所有的的灯 亮起来
            aiPlug.InitLights('<%= orderCount%>','<%= allBoxPCount%>');
	<%
		}
	%>
	
	<%
		if( lightStep == 3 ) {
		
	%>
			aiPlug.SetLightState('<%= allBoxPCount%>', '<%= greenIndexs%>', '<%= redOrangeIndexs%>');
	<%
		}
	%>
	</script>
	
  </head>
  <body onload="focusType(<%= type%>);">
  <div style="margin-left:15px;margin-top:15px;">
	<form action="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup2" method="post" onsubmit="return check1();">
   		&nbsp;&nbsp;&nbsp;&nbsp;
   		分拣波次号：<input type="text" size="22" name="sortingGroupCode" id="sortingGroupCode" value="<%= sbgBean == null ? "" : sbgBean.getCode()%>"/>
   			 <input type="hidden" name="submitType" value="1" />
   		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   		订单数：<font size="6px"><%= orderCount %></font>  
   	</form>
   	<% 
   	if ( type == 2 || type == 3 ) {
   	
   	%>
   	&nbsp;&nbsp;波次商品总件数: <font size="6px" color="blue"><%= needCount%></font>  
   	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
   	&nbsp;&nbsp;已扫描商品件数：<font size="6px" color="blue"><%= completeCount%></font>  
   	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
   	&nbsp;&nbsp;未扫描商品件数：<font size="6px" color="red"><%= needCount - completeCount%></font>
   	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	<br/>
	<br/>
  	<form action="<%= request.getContextPath()%>/admin/secondSortingSplitAction.do?method=splitGroup2" method="post" onsubmit="return check2();">
   		商品编号/条码：<input type="text" size="22" name="productCode" id="productCode" onblur="autoFillWithProductCode();"/>
   					  <input type="hidden" name="submitType" value="1" />
   	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
   	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
   	</form>
   	&nbsp;&nbsp;商品名称：<input type="text" size="38" name="productName" id="productName" value="<%= productName%>" disabled="true"/>
   	<br/>
   	</div>
   	<div style="margin-left:20px;width:90%;height:120px;border-style:solid;border-width:1px;border-color:#FFFFFF;">
   	<table align='left' width='62%' height="100%" border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
   		<tr bgcolor='#FFFFFF' height="100%">
   		<td align='center' width="100%" style="padding: 1mm;">
   		<span id="boxCodeSpan">
   		
   		</span>
   		</td>
   		</tr>
   	</table>
   	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   	<button onclick="finishNow('<%= sbgBean == null ? "" : sbgBean.getCode()%>', <%= type %>, <%= sbgBean == null ? "" : sbgBean.getStatus2() %>);" ><font size="6px" color="#000000">结批</font></button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% if(group.isFlag(767)){ %>
<button onclick="cancelFinish('<%= sbgBean == null ? "" : sbgBean.getCode()%>', <%= type %>, <%= sbgBean == null ? "" : sbgBean.getStatus2() %>);" ><font size="6px" color="#000000">取消结批</font></button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% } %>
   	<button onclick="resetNow('<%= sbgBean == null ? "" : sbgBean.getCode()%>', <%= type %>, <%= sbgBean == null ? "" : sbgBean.getStatus2() %>);" ><font size="6px" color="#000000">重置</font></button>
   	</div>
   	<div style="width:99%;height:361px;border-style:solid;border-width:1px;border-color:#000000;">
   		<br/>
   		<br/>
   		<table align='center' width='96%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
   			<%
   				for(int i = 0 ; i < 5; i++ ) {
   			%>
   			<tr bgcolor='#FFFFFF' >
   				<%
   					for( int j = 0; j < 6; j++ ) {
   					int x = 0;
   					if( map == null ) {
   					} else {
	   					if( map.containsKey(SortingBatchOrderProductBean.getCodeByLocation(i,j))) {
		   					 x = map.get(SortingBatchOrderProductBean.getCodeByLocation(i,j));
	   					}
   					}
   				%>
			<td align='center' width="16%" bgcolor="<%= x == 0 ? "gray" : x == 1 ? "red" : x == 2 ? "orange" : x == 3 ? "green" : "gray" %>">
			<font size="8px" color="#FFFFFF"><%= SortingBatchOrderProductBean.getCodeByLocation(i,j)%></font>
			</td>
				<%
					}
				%>
			</tr>
   			<%
   				}	
   			%>
   		</table>
   	</div>
   	<%
   		}
   	%>
   	<script type="text/javascript">
   		<%
   			if( tips != null && !tips.equals("")){
   		%>
   			alert('<%= tips%>');
   		<%
   			}
   		%>
   	</script>
</body>
</html>