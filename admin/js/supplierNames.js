var highlightindex = -1;
var timeoutId;
function count(a, b) {
	a.innerText = b.value.length;
}
$(document).ready(function () {
	var wordInput = $("#word");
	if(document.getElementById('supplierId')==null){
		return;
	}
	var value = document.all.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text;
	wordInput.val(value);
	var wordInputOffset = wordInput.offset();
	$("#auto").hide().css("border","1px black solid").css("left",wordInputOffset.left+"px").width(wordInput.width()+7+"px").css("background-color","white").css("z-index","10");
	$("#word").keyup(function (event) {
		var myEvent = event || window.event;
		var myKeyCode = myEvent.keyCode;
		
		var wordtext = $("#word").val();
		var conditionObj = document.getElementById('condition');
		var conditionText = "";
		if(conditionObj)conditionText = conditionObj.value;
		if(wordtext!=""||myKeyCode==8||myKeyCode==46)
			{
				if(wordtext!="")
				{
				clearTimeout(timeoutId);
				 timeoutId  = setTimeout(function(){
				
				$.post("/adult-admin/admin/supplier/autoSupplierName.jsp", {word:wordtext,condition:conditionText}, function (date)
				 {
					var org = $(date);
					var wordnodes = org.find("word");
					var autonode = $("#auto");
					autonode.html("");
					wordnodes.each(function (i) 
					{
						var wordnode = $(this);
						var newDivNode = $("<div>").attr("id",i);

						newDivNode.html(wordnode.text()).appendTo(autonode);
						newDivNode.mouseover(function ()
						{
							if(highlightindex!=-1)
							{ 
								$("#auto").children("div").eq(highlightindex).css("background-color","white");
							}
							highlightindex = $(this).attr("id");
							$(this).css("background-color","red");	
						});
						newDivNode.mouseout(function ()
						{$(this).css("background-color","white");
						});
						newDivNode.click(function()
						{
							var textInputText =$(this).text();
							//alert($(this).attr("id"));
							//selectOption(mproductForm.proxyx, $(this).attr("id"));
							highlightindex = -1;
							$("#word").val(textInputText);	
							$("#auto").hide();
							selectOptions(document.getElementById('supplierId'), $("#word").val());
							//document.getElementById('proxyId').value = document.getElementById('proxy').value;
							//document.getElementById('proxyx').options[document.getElementById('proxyx').selectedIndex].text="";
							//document.getElementById('proxyx').options[document.getElementById('proxyx').selectedIndex].text=$("#word").val();
							select();
						});
					} );
					autonode.append('<iframe src="JavaScript:false" style="position:absolute; visibility:inherit; top:0px; left:0px; width:100px; height:200px; z-index:-1; filter=progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0);"></iframe>');
					
					if (wordnodes.length > 0) {
						autonode.show();
					}else
					{
					    autonode.hide();
					    highlightindex=-1;
					}
				},"xml");
				},100);
				
				
			}
			else
			{
				$("#auto").hide();
				highlightindex=-1;
			}
			
	}else
	{
		$("#auto").hide();
	}
	}
	
	);
});

function selectOptions(select,value){
	for(var i = 0; i < select.length; i++){
		if(document.getElementById('supplierId').options[i].text == value){
			select.options[i].selected = true;
			bankAccount();
			return i;
		}
	}
	return 0;
}
