/**
 * 功能说明：品牌信息下拉自动提示，因为之前brandNames.js文件
 * 
 * 和supplierNames.js调用id有冲突，故重建，此js仅适用于/admin/productSrock.jsp
 * 
 * 刘瑞兰 2012-7-5
 * 
 */
var highlightindex = -1;
var timeoutId;
function count(a, b) {
	a.innerText = b.value.length;
}
function f(){
	var wordInput = $("#word2");
	var value = document.all.brandtext.value=document.getElementById('brandId').options[document.getElementById('brandId').selectedIndex].text;
	wordInput.val(value);
	var wordInputOffset = wordInput.offset();
	$("#auto2").hide().css("border","1px black solid").css("left",wordInputOffset.left+"px").width(wordInput.width()+7+"px").css("background-color","white").css("z-index","10");
	$("#word2").keyup(function (event) {
		var myEvent = event || window.event;
		var myKeyCode = myEvent.keyCode;
		var wordtext = $("#word2").val();
		var conditionObj = document.getElementById('condition2');
		//var productLine=document.getElementById('productLine').value;
		var conditionText = "";
		if(conditionObj)conditionText = conditionObj.value;
		if(wordtext!=""||myKeyCode==8||myKeyCode==46){
				if(wordtext!="")
				{
				clearTimeout(timeoutId);
				 timeoutId  = setTimeout(function(){
				
				$.post("/adult-admin/admin/autoBrandName2.jsp?action=word2", {word2:wordtext,condition2:conditionText}, function (date)
						//$.post("/adult-admin/admin/autoBrandName.jsp?action=word&productLineId="+productLine, {word:wordtext,condition:conditionText}, function (date)
				 {
					var org = $(date);
					var wordnodes = org.find("word");
					var autonode = $("#auto2");
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
								$("#auto2").children("div").eq(highlightindex).css("background-color","white");
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
							highlightindex = -1;
							$("#word2").val(textInputText);	
							$("#auto2").hide();
							selectOptions(document.getElementById('brandId'), $("#word2").val());
							//autoWrite1();
						});
					} );
					autonode.append('<iframe src="JavaScript:false" style="position:absolute; visibility:inherit; top:0px; left:0px; width:100px; height:200px; z-index:-1; filter=progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0);"></iframe>');
					
					if (wordnodes.length > 0) {
						autonode.show();
					}else{
					    autonode.hide();
					    highlightindex=-1;
					}
				},"xml");
				},100);
				
				
			}
			else
			{
				$("#auto2").hide();
				highlightindex=-1;
			}
			
		}else{
			$("#auto2").hide();
		}
	}
	
	);

}
$(document).ready(function () {
	f();
});

function selectOptions(select,value){
	for(var i = 0; i < select.length; i++){
		if(document.getElementById('brandId').options[i].text == value){
			select.options[i].selected = true;
			//bankAccount();
			return i;
		}
	}
	return 0;
}
