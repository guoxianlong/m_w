<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/ui/jquery-ui-1.8.17.custom.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/ui/jquery.bgiframe-2.1.2.js"></script>
<link href="<%=request.getContextPath()%>/jquery/themes/base/jquery.ui.all.css" rel="stylesheet" type="text/css">

<script type="text/javascript">
function showDiv(val){
	$.ajax({
		   type: "POST",
		   cache : true,
		   url: "../HelpContext/getHelpContext.mmx",
		   data: {code : val},
		   dataType : 'json',
		   success: function(msg){
		    
		    $('#help').append(msg.obj.context);
		   }
		});
	
	$('#help').dialog({  
   	   	title: "功能说明:",
		   	modal: false,
		    bgiframe: true,
		    width:400,
		    height:300,
		    close: function(event, ui) {			    	
		    	 $('#help').empty();
		    }
    });				
}
</script>