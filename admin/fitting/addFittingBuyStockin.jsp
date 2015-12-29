<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,mmb.stock.stat.*" %>
<html>
<head>
<title>添加配件入库单</title>
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
<center>
入库类型:<input id="stockInType" name="stockInType" editable="false"/>
入库库区: <input id="area" name="area" editable="false"/>
&nbsp;&nbsp;&nbsp;
供应商:<input type="text" id="supplierId" name="supplierId"/>
<br/>
<br/>
			<div id="mainPanel" style="width:1200px;height:100px;float:lfet;" align="center" class="easyui-panel" data-options="title:'入库配件'">
			<br/>
			<form style="display:inline;" name= "form1" id= "form1" method= "post">
			配件类别:<input size="13" id="fittingType" name="fittingType" editable="false"/>
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
			<div style="display:block;float:left;margin-left:0.4%;">
				<table align="center" id="info_table" >
				</table>
			</div>
<center>
			<div style="width:100%;float:left;height:20px;"></div>
<a href="javascript:submitAll();" id="submitConfirm" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">确认提交</a>
</center>
<form id="allForm" action="" method="post" >

</form>
</body>
<script type="text/javascript">
var bodyWidth = document.body.clientWidth;
var tableWidth = bodyWidth*0.98;
var skuCount = 0;
var fittingTypeCache = 0;
$("#mainPanel").panel({width:tableWidth});
function checkProductAndAdd() {
    $('#form1').form('submit' ,{
        onSubmit: function(){
            var isValid = $(this ).form('validate');
                 if (!isValid){
                      $.messager.progress( 'close');
                } else {
                      var productCode = $("#productCode").val();
                      var count = $("#count").val();
                      var price = $("#price").val();
                      var fittingType = $("#fittingType").combobox("getValue");
                      if(fittingTypeCache==0){
                      		fittingTypeCache = fittingType;
                      }
                      if(fittingTypeCache!=fittingType){
	                      	$.messager.alert('提示' ,"必须添加相同配件类别的配件！",'error', function () {
	                        });
	                        return false;
                      }
                      //var brandId = $("#brandId").combobox("getValue");
                      if( productCode != null && productCode != "" ) {
                    	  var temp = $("#"+productCode);
                    	  if( temp.html() != null  ) {
                    		  $.messager.alert('提示' ,"该商品已经添加过 不要重复添加！",'error', function () {
                              });
                        	  return false;
                    	  }
                      }
                     /*  if( brandId  == null || brandId == "" || brandId == "0") {
                    	  $.messager.alert('提示' ,"品牌选择错误！",'error', function () {
                          });
                    	  return false;
                      } */
                      var params = "" ;
                       if( productCode!= null && productCode != "" && count != null && count !="" && count != "0" && price != null && price > 0 && fittingType!=null && fittingType>0) {
                            params += "?productCode="+productCode +"&count="+count+"&price="+price+"&fittingType=" + fittingType;
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
		                                	    shtml+="<input type='hidden' name='fittingType_"+productCode+"' value='"+fittingType+"' />";
		                                	   //shtml+="<input type='hidden' name='brandId_"+productCode+"' value='"+brandId+"' />";
		                                	   shtml+="</span>";
		                                        $("#allForm").append(shtml);
		                                        skuCount ++;
		                                        goSearch();
		                                        $("#productCode").val('');
		                                        $("#count").val('');
		                                        $("#price").val('');
		                                        $("#price").attr("readonly",false);
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
function submitAll() {
	if( skuCount <= 0 ) {
		$.messager.alert('提示' ,'没有添加商品不可以提交！','error', function () {
          	//$('#info_table' ).datagrid('reload');
        });
		return;
	}
	var stockInType = $("#stockInType").combobox("getValue");
	if( stockInType == null || stockInType == "0" || stockInType == "") {
		$.messager.alert('提示' ,'请选择入库类型！','error', function () {
        });
		return;
	}
	var area = $("#area").combobox("getValue");
	if( area == null || area == "0" || area == "") {
		$.messager.alert('提示' ,'请选择地区！','error', function () {
        });
		return;
	}
	var supplierId = $("#supplierId").combobox("getValue");
	if(  supplierId == null || supplierId == "0" || supplierId == "") {
		$.messager.alert('提示' ,'请选择供应商！','error', function () {
        });
		return;
	}
	var params = $("#allForm").serialize();
	if( params == null || params.length == 0 ) {
		$.messager.alert('提示' ,'没有添加商品不可以提交！','error', function () {
          	//$('#info_table' ).datagrid('reload');
        });
		return;
	} else {
		params+="&area="+area+"&supplierId="+supplierId +"&stockInType="+stockInType;
		$.post(
				'<%=request.getContextPath()%>/fittingBuyStockinController/submitAllProduct.mmx?'+params,
               {
                     x:1
               },
                function (data) {
                       var json = data;
                        if( json[ 'status'] == "success" ) {
                        	 $.messager.alert('提示' ,"添加成功！",'tip', function () {
                        	window.location="<%=request.getContextPath()%>/fittingBuyStockinController/toAddFittingBuyStockin.mmx";
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
                {field:'fittingTypeName',title:'配件类别',width:200,align:'center'},
                {field:'price3',title:' 采购价',width:100,align:'center',sortable:true},   
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
	$("#productCode").blur(function(){
		var productCode = $("#productCode").val();
		if(productCode!=null && productCode!=''){
			$.ajax({
				url : '${pageContext.request.contextPath}/fittingBuyStockinController/getStockPrice.mmx',
				type : 'post',
				data : {productCode : productCode},
				dataType : 'json',
				success : function(result){
					if(result.success){
						$("#price").val(result.obj);
						$("#price").attr("readonly",true);
					}
				}
			});
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
    		var pattern2 = /^[0-9]{1,}(\.[0-9]*)?$/;
          if( pattern2.exec(value) ) {
          	return true;
          } else {
          	return false;
          }
    	},
    	message: '需要填入整数或带小数的值！'
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
	if(skuCount<=0){
		fittingTypeCache = 0;
	}
	goSearch();
}

$('#area').combobox({
	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
  	valueField:'id',
	textField:'text',
	delay:500
});

$('#stockInType').combobox({
	url : '${pageContext.request.contextPath}/Combobox/getFittingBuyStockInType.mmx',
  	valueField:'id',
	textField:'text',
	delay:500
});

$('#fittingType').combobox({
	url : '${pageContext.request.contextPath}/Combobox/getFittingBuyStockInProductType.mmx',
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
</html>