<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.buy.*" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,mmb.stock.stat.*" %>
<%
	List<BuyStockinProductBean> bspList = (List<BuyStockinProductBean>)request.getAttribute("bspList");
%>
<html>
<head>
<title>编辑配件入库单</title>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-1.3.1/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
</head>
<body>
<br/>
<br/>

			<div id="infoPanel" style="width:1180px;height:70px;float:lfet;" class="easyui-panel" >
			<br/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;
			<b>入库单编号： ${bsBean.code }</b>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<b>入库库区：${areaName }</b>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<b>配件总量：${totalCount }</b>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<b>生成时间：${bsBean.createDatetime }</b>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<b>状态：${bsBean.fittingStatusName }</b>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<b>供应商：${bsBean.proxyName }</b><br/><br/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;
			<b>处理意见：${bsBean.remark }</b>
			</div>
<center>
			<!-- 供应商:<input type="text" id="supplierId" name="supplierId"/> -->
			&nbsp;&nbsp;&nbsp;
			<div id="mainPanel" style="width:1180px;height:100px;float:lfet;" align="center" class="easyui-panel" data-options="title:'入库配件'">
			<br/>
			<form style="display:inline;" name= "form1" id= "form1" method= "post">
			配件编号:<input type="text" size="13" id="productCode" name="productCode" class="easyui-validatebox" data-options="required:true,validType:'productCodeValid'"/>
			&nbsp;&nbsp;&nbsp;
			数量: <input type="text" size="8" id="count" name="count"  class="easyui-validatebox" data-options="required:true,validType:'intNumber'"/>
			&nbsp;&nbsp;&nbsp;
			采购价:<input type="text" size="10" id="price" name="price" class="easyui-validatebox" data-options="required:true,validType:'floatNumber'"/>
			&nbsp;&nbsp;&nbsp;
			<!-- 品牌:<input type="text" id="brandId" name="brandId"/> -->
			&nbsp;&nbsp;&nbsp;
			</form>
			<a href="javascript:checkProductAndAdd();" id="submitConfirm" class="easyui-linkbutton" data-options="iconCls:'icon-add'">添加</a>
			</div>
</center>
			<div style="display:block;float:left;margin-left:0.2%;">
				<table align="center" id="info_table" >
				</table>
			</div>
<center>
			<div style="width:100%;float:left;height:20px;"></div>
<a href="javascript:submitAllEdit();" id="submitConfirm" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">确认提交</a>
</center>
<form id="allForm" action="" method="post" >
		<%
			if( bspList != null ) {
			for( BuyStockinProductBean bspBean : bspList ) {
		%>
		<span id='<%= bspBean.getProductCode()%>'>
		<input type='hidden' name='productCodes' value='<%= bspBean.getProductCode()%>' />
		<input type='hidden' name='count_<%= bspBean.getProductCode()%>' value='<%= bspBean.getStockInCount()%>' />
		<input type='hidden' name='price_<%= bspBean.getProductCode()%>' value='<%= bspBean.getPrice3()%>' />
		<input type='hidden' name='proxyName_<%= bspBean.getProductCode()%>' value='${bsBean.proxyName }' />
		<%-- <input type='hidden' name='brandId_<%= bspBean.getProductCode()%>' value='<%= bspBean.getProductProxyId()%>' /> --%>
		</span>
		<%		
			}
			}
		%>
</form>

<%-- <a href="<%= request.getContextPath()%>/fittingBuyStockinController/auditFittingBuyStockin.mmx?code=${bsBean.code}">审核测试</a> --%>
</body>
<script type="text/javascript">
var bodyWidth = document.body.clientWidth;
var tableWidth = bodyWidth*0.97;
var skuCount = <%= bspList.size() %>;
$("#mainPanel").panel({width:tableWidth});
function checkProductAndAdd() {
    $( '#form1').form('submit' ,{
        onSubmit: function(){
            var isValid = $(this ).form('validate');
                 if (!isValid){
                      $.messager.progress( 'close');
                } else {
                      var productCode = $("#productCode").val();
                      var count = $("#count").val();
                      var price = $("#price").val();
                      //var brandId = $("#brandId").combobox("getValue");
                      if( productCode != null && productCode != "" ) {
                    	  var temp = $("#"+productCode);
                    	  if( temp.html() != null  ) {
                    		  $.messager.alert('提示' ,"该商品已经添加过 不要重复添加！",'error', function () {
                              });
                        	  return false;
                    	  }
                      }
                      var params = "" ;
                       if( productCode!= null && productCode != "" && count != null && count !="" && count != "0" && price != null && price > 0 ) {
                            params += "?productCode="+productCode +"&count="+count+"&price="+price;
                       } else {
                             return false ;
                      }
                      $.post(
                                   '<%= request.getContextPath()%>/fittingBuyStockinController/checkFittingBuyStockinProduct.mmx'+params,
                                  {
                                        x:1
                                  },
                                   function (data) {
                                          var json = data;
		                                   if( json[ 'status'] == "success" ) {
		                                	   
		                                	   var shtml="<span id='"+productCode+"'>";
		                                	   //var shtml="";
		                                	   shtml+="<input type='hidden' name='productCodes' value='"+productCode+"' />";
		                                	   shtml+="<input type='hidden' name='count_"+productCode+"' value='"+count+"' />";
		                                	   shtml+="<input type='hidden' name='price_"+productCode+"' value='"+price+"' />";
		                                	   shtml+="<input type='hidden' name='proxyName_"+productCode+"' value='${bsBean.proxyName}' />";
		                                	   /* shtml+="<input type='hidden' name='brandId_"+productCode+"' value='"+brandId+"' />"; */
		                                	   shtml+="</span>";
		                                        $("#allForm").append(shtml);
		                                        skuCount ++;
		                                        goSearch();
		                                  } else {
		                                	  $.messager.alert('提示' ,json['tip'],'error', function () {
                                              	//$('#info_table' ).datagrid('reload');
                                            });
		                                  } 
                                  },
                                   "json"
                            );
                }
                return false ;
        }
    });
}

function goSearch() {
	var params = $("#allForm").serialize();
	if( params == null || params.length == 0 ) {
		$('#info_table').datagrid({'url': '<%=request.getContextPath()%>/fittingBuyStockinController/getInfoForAll.mmx'});
	} else {
		$('#info_table').datagrid({'url': '<%=request.getContextPath()%>/fittingBuyStockinController/getInfoForAll.mmx?'+params});
	}
}

function firstSearch() {
	var params = $("#allForm").serialize();
	$('#info_table').datagrid({'url': '<%=request.getContextPath()%>/fittingBuyStockinController/getInfoForAll.mmx?'+params});
}


function submitAllEdit() {
	if( skuCount <= 0 ) {
		$.messager.alert('提示' ,'没有添加商品不可以提交！','error', function () {
          	//$('#info_table' ).datagrid('reload');
        });
		return;
	}
	/* var supplierId = $("#supplierId").combobox("getValue");
	if(  supplierId == null || supplierId == "0" || supplierId == "") {
		$.messager.alert('提示' ,'请选择供应商！','error', function () {
        });
		return;
	} */
	var params = $("#allForm").serialize();
	if( params == null || params.length == 0 ) {
		$.messager.alert('提示' ,'没有添加商品不可以提交！','error', function () {
          	//$('#info_table' ).datagrid('reload');
        });
		return;
	} else {
		params += "&id=${bsBean.id}";
		//params+="&supplier_id="+supplierId;
		$.post(
				'<%=request.getContextPath()%>/fittingBuyStockinController/editAllProduct.mmx?'+params,
               {
                     x:1
               },
                function (data) {
                       var json = data;
                        if( json[ 'status'] == "success" ) {
                        	$.messager.alert('提示' ,"编辑成功！",'tip', function () {
                            	window.location="<%=request.getContextPath()%>/fittingBuyStockinController/toEditFittingBuyStockin.mmx?code="+${bsBean.code};
                                 });
                       } else {
                     	  $.messager.alert('提示' ,json['tip'],'error', function () {
                         });
                       } 
               },
                "json"
         );
	}
}

$(function(){
   $('#info_table').datagrid({
    fitColumns:true,
    //fit:true,
    width: tableWidth,
    height: 400,
	border : true,
    //pageNumber:1,  
   // pageSize:200,    
   // pageList:[100,200,300,500], 
   align:'center',
   title:'已添加商品',
    nowrap:false,  
    striped: true,
    collapsible:true,
    url:'<%=request.getContextPath()%>/fittingBuyStockinController/getInfoForAll.mmx', 
    loadMsg:'数据装载中......', 
    remoteSort:false, 
    //singleSelect:true,
    columns:[[   
                {title:'配件编号',field:'productCode',width:100,align:'center',sortable:true},   
                {field:'oriname',title:'配件名称',width:300,align:'center',sortable:true},   
                {field:'stockInCount',title:'配件数量',width:200,align:'center',sortable:true},
                {field:'price3',title:' 采购价',width:100,align:'center',sortable:true},   
                {field:'proxyName',title:' 供应商',width:300,align:'center',sortable:true},   
                {field:'confirmDatetime',title:'操作',width:100,align:'center',sortable:true}
                ]],
    rownumbers:true, //是否有行号。
    //selectOnCheck:true,
    //checkOnSelect:false,
    onLoadSuccess: function(data) {
     if( data['tip'] != null ) {
    		jQuery.messager.alert("提示", data['tip']);
    	}
    }
   });
   });

$.extend($.fn.validatebox.defaults.rules, {
    intNumber: {
          validator : function (value) {
          	var pattern1 = /^[0-9]{1,5}$/;
              if( pattern1.exec(value) ) {
              	return true;
              } else {
              	return false;
              }
          },
          message: '此处需要填写整数！'
    },
    floatNumber: {
    	validator : function (value) {
    		var pattern2 = /^[0-9]{1,}(\.[0-9]{1,2})?$/;
          if( pattern2.exec(value) ) {
          	return true;
          } else {
          	return false;
          }
    	},
    	message: '需要填入整数或带一或两位小数的值！'
    },
    productCodeValid: {
    	validator : function (value) {
    		var pattern2 = /^[0-9a-z]{1,20}$/;
          if( pattern2.exec(value) ) {
          	return true;
          } else {
          	return false;
          }
    	},
    	message: '需要填入字母,数字,或组合！'
    }
});

function deleteProduct(productCode ) {
	var $temp = $("#"+productCode);
	$temp.remove();
	skuCount --;
	goSearch();
	
}

$('#area').combobox({
	url : '${pageContext.request.contextPath}/OrderStockController/getAreaComboBox.mmx',
  	valueField:'id',
	textField:'text',
	delay:500
});
var supplierInfo = ${supplierJson};
//var supplierJson = eval('('+ supplierInfo +')');
$('#supplierId').combobox({
  	data:supplierInfo,
	valueField:'id',
	textField:'text',
	delay:100
});
/* $('#brandId').combobox({
  	data:brandInfo,
	valueField:'id',
	textField:'text',
	delay:100
}); */

</script>
<script>
$(document).ready(function(){
	firstSearch();
});
</script>
</html>