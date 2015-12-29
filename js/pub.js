/* Dreamweaver自带函数开始，包括预载图片、翻转图片效果 */
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_showHideLayers() { //v6.0
  var i,p,v,obj,args=MM_showHideLayers.arguments;
  for (i=0; i<(args.length-2); i+=3) if ((obj=MM_findObj(args[i]))!=null) { v=args[i+2];
    if (obj.style) { obj=obj.style; v=(v=='show')?'visible':(v=='hide')?'hidden':v; }
    obj.visibility=v; }
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
/* Dreamweaver自带函数结束 */

/* leftmenu部分开始 */

//箭头跟踪显示
function cha(num){
	/*
	alert(num);
	alert(arrow[num].src);
	var s=arrow[num].src;
	if(s.indexOf("arrow.gif")<0){
		arrow[num].src='images/arrow.gif';
	}else{
		arrow[num].src='images/noooo.gif';}
	*/
}
//显示隐藏菜单
function show(divid) {
divid.style.display = divid.style.display == "none"?"":"none";
}
//背景色变化
function bg(o){
	if(event.srcElement.tagName == 'TD')
		event.srcElement.className = o;
	if(event.srcElement.tagName == 'A')
		event.srcElement.parentElement.className = o;
}

/* leftmenu部分结束 */

//个人提醒板
function pm(){
//window.showModalDialog("/meip/inc/userdetail.jsp?type=1","个人提醒板","dialogHeight: 145px; dialogWidth: 190px; edge: Raised; center: Yes; help: No; resizable: No; status: No;");
window.open("inc/userdetail.jsp?type=1","_blank","width=240,height=140,directories=no,toolbar=no,resizsable=no,menubar=no,scrollbars=no,left=300,top=50");
}


	function getCookie(Name){
		var search = Name+'=';
		if(document.cookie.length>0){//如果存在本文档的cookies
			offset = document.cookie.indexOf(search);
			if(offset != -1){
				offset += search.length; //设置索引开始位置
				end = document.cookie.indexOf(';',offset); //设置索引结束位置
				if(end == -1)
					end = document.cookie.length;
				return unescape(document.cookie.substring(offset,end));
			}
			else
				return null;
		}
	}
	function setCookie(name,value){
		var argv = setCookie.arguments;
		var argc = setCookie.arguments.length;
		var expires = (argc>2)?argv[2]:null;
		var path=(argc>3)?argv[3]:null;
		var domain = (argc>4)?argv[4]:null;
		var secure = (argc>5)?argv[5]:false;
		//利用所获取的参数设置cookie，并将cookies的名称和值用escape函数编码
		document.cookie=name+"="+escape(value)+";expires=Thursday, 10-Dec-11 12:00:00 GMT"
			+((path==null)?"":(";path="+path))+((domain==null)?"":(";domain="+domain))
			+((secure==true)?";secure":"");
	}
