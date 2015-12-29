<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>编辑报损报溢原因</title>
<link href="${pageContext.request.contextPath}/jquery/themes/base/jquery.ui.all.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-1.7.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/ui/jquery-ui-1.8.17.custom.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-msg.js"></script>
<script type="text/javascript">

function reasonDiv(type){
	var title;
	if(type == "0"){
	title="添加报损原因";
	}else{
	title="添加报溢原因";	
	
	}
	
	$('#reasonDiv').dialog({  
	modal:true,
	bgiframe:true,
	width:330,
	height:260,
	title:title,
	open: function(event, ui) {
		/**
		if(type != "0"){ //编辑
			var d = new Date();
			$.ajax({
				type: "post",
				url: "${pageContext.request.contextPath}/admin/BsbyReason/toeditBsbyReason.mmx?id="+type+"&millons="+d.getMilliseconds(),
				data:$('#reasonForm').serialize(),
				dataType:'json', //接收数据格式 
				success: function(msg){
					
				  if(msg.msg == "success"){
					  $("#id").val(msg.obj.id);
					  $(".type").val(msg.obj.type); 
                      $("#reason").val(msg.obj.reason);
                      $("#tempReason").val(msg.obj.reason);
                      
					// alert($("#reason").val());
				  }
				}
			});
		}
		*/
		//初始下拉框值
		if(type == "0"){
			$("#reason").val("");
			$("#type").empty();//清空
		 $("<option value='0'>报损</option>").appendTo("#type");//添加下拉框的option
         }else{
        	$("#reason").val("");
        	 $("#type").empty();//清空
			$("<option value='1'>报溢</option>").appendTo("#type");//添加下拉框的option
		 }
	},
	buttons: {
		"确定":function(){
			var reason=$("#reason").val();
			//if(type == "0"){
			if($("#reason")!=null&&$("#reason").val()=='请输入报损报溢原因'){
				alert('请输入报损报溢原因');
				return false;
			}
			 if(($("#reason").val()).length >20 ){
				alert('输入文本不符合规则');
				$("#reason").select();
				return false;
			}
			$(this).dialog('close');
			var d = new Date();
			$.ajax({
				type: "post",
				url: "${pageContext.request.contextPath}/admin/BsbyReason/addBsbyReason.mmx?millons="+d.getMilliseconds(),
				cache: false,
				data:$('#reasonForm').serialize(),
				dataType:'', //接收数据格式 
				success: function(msg){
				 if(msg == "success"){
					 $.messager.show(0,'添加成功!',3000);
					 var MyInterval=setInterval("Refresh()",2000);
				
				 }else if(msg=="failed"){
					 $.messager.show(0,reason+',已存在,不可重复添加',3000);
					
					  return false;
				}else if(msg=="nologin"){
					 	alert('请重新登陆!');
					 	window.location.href='${pageContext.request.contextPath}/login.do';
					 	return false;
				}else if(msg=="error"){
					 $.messager.show(0,title+"失败",3000);
					
					  return false;
				}
			    },
			  error: function(a) {
				  $.messager.show(0,title+"失败",3000);
						return false;
			   }	
			});
			/**
		   }else{ //编辑
			   if($("#reason")!=null&&$("#reason").val()=='请输入报损报溢原因'){
					alert('请输入报损报溢原因');
					return false;
			   }
		    // alert($("#tempReason").val());
		       if($("#reason").val() == $("#tempReason").val()){
		    	   alert('请编辑报损报溢原因');
				  return false;
		       }
		       if(($("#reason").val()).length >20 ){
					alert('输入文本不符合规则');
					$("#reason").select();
					return false;
				}
			   $(this).dialog('close');
				var d = new Date();
				$.ajax({
					type: "post",
					url: "${pageContext.request.contextPath}/admin/BsbyReason/editBsbyReason.mmx?millons="+d.getMilliseconds(),
					cache: false,
					data:$('#reasonForm').serialize(),
					dataType:'', //接收数据格式 
					success: function(msg){
					 if(msg == "success"){
					   alert(title+"成功");
					   window.location.href="${pageContext.request.contextPath}/admin/BsbyReason/queryBsbyReasons.mmx";
					 }else if(msg=="failed"){
						  alert("盘点原因已存在,不可重复编辑");
						  return false;
					}else if(msg=="nologin"){
						 	alert('请重新登陆!');
						 	window.location.href='${pageContext.request.contextPath}/login.do';
					}else if(msg=="error"){
						 alert(title+"失败");
						  return false;
					}
					}
				});
		   
		   }
			*/
		},
		"取消":function(){
		$(this).dialog('close');
		return false;
	     }
	   }
    });
	
}

function showReason(type){
	if(type =="1"){
	$(".showby").show();	
	}else{
		$(".showbs").show();		
	}
	
} 
function hiddenReason(type){
	if(type =="1"){
	$(".showby").hide();	
	}else{
		$(".showbs").hide();			
	}
	
} 
function deleteReason(type){
	if(type =="1"){
	   var chk_value =[];    
	  $('input[name="byids"]:checked').each(function(){    
	   chk_value.push($(this).val());    
	  });   
	  if(chk_value.length==0){
		  alert('你还没有选择任何内容！');
		  return false;
	  }else{
		  
		  if(confirm('确定要删除么?')){
			  var d = new Date();
				$.ajax({
					type: "post",
					url: "${pageContext.request.contextPath}/admin/BsbyReason/deleteBsbyReason.mmx?id="+type+"&millons="+d.getMilliseconds(),
					data:{ids:chk_value.join(",")},
					dataType:'json', //接收数据格式 
					success: function(msg){
						if(msg.msg == "success"){
							 $.messager.show("提示",'删除成功!');
							 var MyInterval=setInterval("Refresh()",2000);
						}else if(msg.msg=="nologin"){
						 	alert('请重新登陆!');
						 	window.location.href='${pageContext.request.contextPath}/login.do';
					    }
					}
				});
		  }
	  }
	  
	 
	}else{
		var chk_value =[];    
		  $('input[name="bsids"]:checked').each(function(){    
		   chk_value.push($(this).val());    
		  });    
		  if(chk_value.length==0){
			  alert('你还没有选择任何内容！');
			  return false;
		  }else{
			  
			  if(confirm('确定要删除么?')){
				  var d = new Date();
					$.ajax({
						type: "post",
						url: "${pageContext.request.contextPath}/admin/BsbyReason/deleteBsbyReason.mmx?id="+type+"&millons="+d.getMilliseconds(),
						data:{ids:chk_value.join(",")},
						dataType:'json', //接收数据格式 
						success: function(msg){
							if(msg.msg == "success"){
							$.messager.show("提示",'删除成功!');
							var MyInterval=setInterval("Refresh()",2000);
							
						    }else if(msg.msg=="nologin"){
							 	alert('请重新登陆!');
							 	window.location.href='${pageContext.request.contextPath}/login.do';
						    }
						}
					});
			  }  	
	}
  } 
}



function Refresh(){
	 window.location.href="${pageContext.request.contextPath}/admin/BsbyReason/queryBsbyReasons.mmx";
}

function showReasonLog(){
window.location.href='${pageContext.request.contextPath}/admin/BsbyReason/queryBsbyReasonLogs.mmx';	
}

</script>
</head>
<body>
<div>
<button name="addbut" style="width:120px;" onclick="showReasonLog()">修改日志</button></div>
<table width="400"  cellspacing="0" cellpadding="0" bordercolordark="#FFFFFF" 
bordercolorlight="#000000" align="left" > 


<tr>
<td colspan="3">
<h3>编辑报损原因</h3>
</td>
</tr>
<tr>
<td colspan="1" align="left" style="width:125px;" >
<button name="addbut" onclick="reasonDiv(0)">添加</button>
</td>
<td style="width:125px;">
<button name="addbut" style="width:110px;" onclick="showReason(0)">删除</button>
</td>
</tr>

<c:if test="${(bsbyReasons)!= null && fn:length(bsbyReasons) > 0}">
<c:forEach items="${bsbyReasons}" var="bsbyReason">
<c:choose>
<c:when test="${(bsbyReason.type) == 0}" >
<tr colspan="1">
<td><input type="checkbox" style="display:none;" class="showbs"  name="bsids" value="${bsbyReason.id}">
<c:out value="${bsbyReason.reason}" /></td>
<!-- 
<td>
<a href="javascript:reasonDiv(<c:out value="${bsbyReason.id}" />)">编辑</a>
</td>
-->
</tr>
</c:when> 
<c:otherwise></c:otherwise>
</c:choose>

</c:forEach>
</c:if>
<tr>
<td colspan="1" align="left" style="width:125px;">
<button name="addbut" onclick="deleteReason(0)">确定</button>
</td>
<td>
<button name="addbut" onclick="hiddenReason(0)">取消</button>
</td>
</tr>
</table>

<table width="400"  cellspacing="0" cellpadding="0" bordercolordark="#FFFFFF" 
bordercolorlight="#000000" align="center"> 

<tr>
<td colspan="2">
<h3>编辑报溢原因</h3>
</td>
</tr>
<tr>
<td colspan="1"  style="width:125px;" >
<button name="addbut" onclick="reasonDiv(1)">添加</button>
</td>
<td>
<button name="addbut" style="width:110px;" onclick="showReason(1)">删除</button>
</td>
</tr>

<c:if test="${(bsbyReasons)!= null && fn:length(bsbyReasons) > 0}">
<c:forEach items="${bsbyReasons}" var="bsbyReason">
<c:choose>
<c:when test="${(bsbyReason.type) == 1}" >
<tr colspan="1">
<td><input type="checkbox" style="display:none;" class="showby"  name="byids" value="${bsbyReason.id}">
<c:out value="${bsbyReason.reason}" /></td>
<!--  
<td>
<a href="javascript:reasonDiv(<c:out value="${bsbyReason.id}" />)">编辑</a>
</td>
-->
</tr>
</c:when> 
<c:otherwise></c:otherwise>
</c:choose>

</c:forEach>
</c:if>
<tr>
<td colspan="1" align="left" style="width:125px;">
<button name="addbut" onclick="deleteReason(1)">确定</button>
</td>
<td>
<button name="addbut" onclick="hiddenReason(1)">取消</button>
</td>
</tr>
</table>







<div id='reasonDiv' class='refundDiv' style="display:none;width:300px;height:220px;">
	<form id="reasonForm" action="<%=basePath%>/admin/BsbyReason/addBsbyReason.mmx" method="post">
		<input type="hidden" name="id" id="id" value=""> 
		<input type="hidden" name="tempReason" id="tempReason" value=""> 
	    <table align="center">
	        <tr>
	        <td>类型</td>
			<td>
			<select name="type" id="type" class="type">
			
			</select>
			<td>				
			<tr>
			<tr>
			<td>原因</td>
			<td>
			<input type="text" name="reason" size="20"  onblur="script:if($.trim($(this).val())==''){$(this).val('请输入报损报溢原因')}" onfocus="script:if($.trim($(this).val())=='请输入报损报溢原因'){$(this).val('')}" id="reason" value="请输入报损报溢原因"><br><br>
			<td>				
			<tr>
				<td>
					<div style="width:80;height:10;overflow-y:auto;overflow-x:hidden;">									
					</div>
				</td> 
			</tr>
		</table>
	</form>
</div>

</body>
</html>