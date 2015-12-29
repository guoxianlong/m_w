<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript" charset="UTF-8">
	function logout(b) {
		if( b ) {
			window.location="<%= request.getContextPath()%>/admin/rec/logout.jsp";
		} else {
			window.location="<%= request.getContextPath()%>/admin/rec/logout.jsp";
		}
	}
</script>
<div style="position: absolute; right: 0px; bottom: 0px; ">
	<a href="javascript:void(0);" class="easyui-menubutton" menu="#layout_north_kzmbMenu" iconCls="icon-help">控制面板</a><a href="javascript:void(0);" class="easyui-menubutton" menu="#layout_north_zxMenu" iconCls="icon-back">注销</a>
</div>
<div id="layout_north_kzmbMenu" style="width: 100px; display: none;">
	<div>
		<span>更换主题</span>
		<div style="width: 100px;">
			<div onclick="sy.changeTheme('default');">default</div>
			<div onclick="sy.changeTheme('gray');">gray</div>
			<div onclick="sy.changeTheme('dark-hive');">dark</div>
			<div onclick="sy.changeTheme('cupertino');">cupertino</div>
			<div onclick="sy.changeTheme('metro');">metro</div>
			<div onclick="sy.changeTheme('pepper-grinder');">grinder</div>
			<div onclick="sy.changeTheme('sunny');">sunny</div>
		</div>
	</div>
</div>
<div id="layout_north_zxMenu" style="width: 100px; display: none;">
	<div onclick="logout();">重新登录</div>
	<div onclick="logout(true);">退出系统</div>
</div>