<%@ page contentType="text/html;charset=utf-8" %><%@ page import="adultadmin.util.StringUtil,adultadmin.action.vo.voUser,adultadmin.service.infc.*,adultadmin.service.ServiceFactory" %><%@ page import="adultadmin.framework.*,mmb.user.secure.*" %><%
	CustomAction action = new CustomAction(request);
	voUser user = (voUser)session.getAttribute("userView");
	if(!action.isMethodGet()) {
		String old = request.getParameter("old");
		String pwd = request.getParameter("pwd");
		String pwd2 = request.getParameter("pwd2");
		
		int loginCheck = LoginUtil.loginCheck(user.getUsername(), request);
        if(loginCheck != 0){
        	LoginUtil.log2Db(request, user.getUsername(), old,loginCheck);
        	String msg = "";
        	if(loginCheck==1){
        		msg = "你的账号旧密码尝试次数过多，请十分钟后再试。";
        	}
        	if(loginCheck==2){
        		msg = "你的IP地址不允许访问，请联系技术人员。";
        	}
        	if(loginCheck==3){
        		msg = "你的IP："+adultadmin.util.IP.getRemoteAddr(request)+" 尝试失败次数过多，请一分钟后再试。";
        	}
        	action.tip("tip",msg);
        }else{		
			if(old==null||!old.equals(user.getPassword()) && !mmb.util.Secure.encryptPwd(old).equals(user.getPassword())){
				LoginUtil.recordError(user.getUsername(), request);
				LoginUtil.log2Db(request, user.getUsername(), old,-1); ///-1表示 密码或者用户名错误
				
				action.tip("tip","老密码不正确!");
			} else if(pwd == null || pwd.length()<6) {
				action.tip("tip","新密码至少6位!");
			} else if(!pwd.equals(pwd2)) {
				action.tip("tip","两次输入的新密码不匹配!");
			} else {
				IUserService service = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, null);
	            service.getDbOp().init();
	            service.updateAdminUser(" password='"+mmb.util.Secure.encryptPwd(pwd)+"'", "id="+user.getId());	// 加密后的字符串一定不包含单引号等异常字符，所以不需要tosql
				service.releaseAll();
				user.setPassword(pwd);
				action.tip("tip","密码修改成功");
				
				///记录一次修改成功
				LoginUtil.log2Db(request, user.getUsername(), pwd,10);
				LoginUtil.recordUserNameSuccess(user.getUsername(),request);

			}
        }
	}
%>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body style="margin-left:12px;">
<%if(action.isResult("tip")){%><font color=red><%=action.getTip()%></font><br/><%}%>
修改密码<br/>
<form method=post action="set.jsp">
原密码:<input type=password name=old><br/>
新密码:<input type=password name=pwd><br/>
新密码:<input type=password name=pwd2><br/>
<input type=submit value="确认修改"><br/>
</form>
</body>
</html>