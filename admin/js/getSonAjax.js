  var XMLHttpReq,pid;                                                             //创建XMLHttpRequest对象
  function createXMLHttpRequest()
  {
  	if(window.XMLHttpRequest)
  	{                               
  		//Mozilla浏览器
        XMLHttpReq = new XMLHttpRequest();
    }else if(window.ActiveXObject)
    {                           
    	//IE浏览器
        try
        {
            XMLHttpReq = new ActiveXObject( "Msxm12.XMLHTTP" );
        }catch(e)
        {
            try
            {
               XMLHttpReq = new ActiveXObject( "Microsoft.XMLHTTP" );
            }catch(e)
            {}
         }
      }
  }  
  //发送Ajax请求
  function sendAjaxRequest(url)
  {
     createXMLHttpRequest();                         //创建XMLHttpRequest对象
     XMLHttpReq.open("post",url,false);
     XMLHttpReq.onreadystatechange = processResponse;//指定响应函数
     XMLHttpReq.send(null);
  }

  //回调函数processResponse
  function processResponse()
  {
      if(XMLHttpReq.readyState == 4)
      {
      	 if(XMLHttpReq.status == 200)
      	 {
            var allSons = XMLHttpReq.responseXML.getElementsByTagName("son");
            var grandfather = document.getElementById("my");
            var father = document.getElementById("son");
            var son = document.getElementById("grandchild");
            if(pid == 0)
            {
            	grandfather.options.length = 0;
                father.options.length = 0;
                son.options.length = 0;
                addOption(grandfather,allSons);
                father.add(new Option("请选择",""));
                son.add(new Option("请选择",""));
                return false;
            }
            else if(pid == 1)
            {
             	father.options.length = 0;
             	son.options.length = 0;
                addOption(father,allSons);
                son.add(new Option("请选择",""));
                return false;
            }
             else(pid == 2)
            {
              	son.options.length = 0;
                addOption(son,allSons);
                return false;
            }
         }else
         {    
         	  //响应未交互成功时，页面中的代码
              // "正在加载数据......"
         }
     }else
     {                                                  
     	//响应未加载成功时，页面中的代码
        // "正在验证用户名......"
     }
  }
  
  function addOption(selectObject,allSons)
  {
 	 for( var i = 0; i < allSons.length; i=i+1 ) 
 	 {
	     var allSonsi = allSons[i];
	     var sonId = allSonsi.getElementsByTagName( "sonId" ).item(0).firstChild.nodeValue;
	     var sonName = allSonsi.getElementsByTagName( "sonName" ).item(0).firstChild.nodeValue;
	     if(i == 0)
	     {
	    	 selectObject.add(new Option("请选择",0));
		     selectObject.add(new Option(sonName,sonId));
	     }else
	     {
	    	 selectObject.add(new Option(sonName,sonId));
	     }	      
	 }
  }
  
  function getMy(father_id)
  {
	  pid = 0;
	  var url = "../afterSales/getFittingsCatalog.jsp?level=1&parentId="+father_id;
	  //var url = "Link?father_id="+father_id;
	  sendAjaxRequest(url);
	  //window.alert("gff");
  } 
  function getSon(father_id)
  {
  	  var father = document.getElementById("son");
      var son = document.getElementById("grandchild");
      if(father_id == 0)
  	  {
  	  	 //alert(father_id.value);
  	  	 father.options.length = 0;
         son.options.length = 0;
         father.add(new Option("请选择",""));
         son.add(new Option("请选择",""));
  	  }
  	  else
  	  {
  	  	pid = 1;
	  	var url = "../afterSales/getFittingsCatalog.jsp?level=2&parentId="+father_id;
	  	//var url = "ThreeLink?father_id="+father_id;
	 	sendAjaxRequest(url);
  	  }
	  
	  //window.alert("gff");
  } 
  function getGrandchild(father_id)
  {
  	  var son = document.getElementById("grandchild");
  	  if(father_id == 0)
  	  {
  	  	  son.options.length = 0;
  	  	  son.add(new Option("请选择",""));
  	  }
  	  else
  	  {
  	  	 pid = 2;
	  	 var url = "../afterSales/getFittingsCatalog.jsp?level=3&parentId="+father_id;
	  	 //var url = "ThreeLink?father_id="+father_id;
	 	 sendAjaxRequest(url);
  	  }
	 
	  //window.alert("gff");
  } 

  
  
  
  
  
  
  
  
  
  
  
  
  
 