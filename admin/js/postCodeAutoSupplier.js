/*
	自动匹配邮编
*/
function codeAuto(){
	//1.获取文本框中的值
	//var text = $("#_fmm.d._0.c").val();
	var text1=document.getElementById('sheng').options[document.getElementById('sheng').selectedIndex].text//省
	var text=document.getElementById('shi').options[document.getElementById('shi').selectedIndex].text//市
	//alert(text1+'--'+text);
	//2.将文本框中的值发给服务器
	text1 = text1.replace(/^\s+| $/g,"");
	text = text.replace(/^\s+| $/g,"");
	$.post("../postCodeAuto.jsp", {sheng:text1,word:text}, function(responsedata) {
		//当与服务器端的交互结束，数据成功返回之后，这个方法会被执行
		//3.接收服务器的响应结果，responsedata中可以获得服务器端返回的数据
		//4.将服务器的返回结果显示在页面上
		//alert(responsedata);
		document.all.postcode.value=responsedata;
	});		
}


