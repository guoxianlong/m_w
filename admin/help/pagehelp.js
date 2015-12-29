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