var highlightindex = -1;
var timeoutId;
function count(a, b) {
	a.innerText = b.value.length;
}
$(document).ready(function () {
	var wordInput = $("#word");
	var value = document.all.proxy.value=document.getElementById('proxyx').options[document.getElementById('proxyx').selectedIndex].text;
	wordInput.val(value);
	var wordInputOffset = wordInput.offset();
	$("#auto").hide().css("border","1px black solid").css("left",wordInputOffset.left+"px").width(wordInput.width()+7+"px");;
	$("#word").keyup(function (event) {
		var myEvent = event || window.event;
		var myKeyCode = myEvent.keyCode;
		
		var wordtext = $("#word").val();
	
		if(wordtext!=""||myKeyCode==8||myKeyCode==46)
			{
				if(wordtext!="")
				{
				clearTimeout(timeoutId);
				 timeoutId  = setTimeout(function(){
				$.post("autoProxyName.jsp", {word:wordtext}, function (date)
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
							selectOptions(mproductForm.proxyx, $("#word").val());
							//document.getElementById('proxyx').options[document.getElementById('proxyx').selectedIndex].text="";
							//document.getElementById('proxyx').options[document.getElementById('proxyx').selectedIndex].text=$("#word").val();
						});
					} );
					
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
		if(document.getElementById('proxyx').options[i].text == value){
			select.options[i].selected = true;
			return i;
		}
	}
	return 0;
}
