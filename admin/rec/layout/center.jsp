<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript" charset="UTF-8">
	var centerTabs;
	function addTabFun(opts) {
		var options = $.extend({
			title : '',
			content : '<iframe src="' + opts.src + '" frameborder="0" style="border:0;width:100%;height:99.2%;"></iframe>',
			closable : true,
			iconCls : ''
		}, opts);
		var tabs = centerTabs.tabs("tabs");
        var length = tabs.length;
		for (var i = 0 ; i < length; i++) {
			var tab = tabs[i];
            var thetabOptions = tab.panel('options');
			if (options.content == thetabOptions.content) {
				var index = centerTabs.tabs('getTabIndex',tab);
				centerTabs.tabs('close',index);
				break;
			}
		}
		centerTabs.tabs('add', options);
	};
	$(function() {
		centerTabs = $('#centerTabs').tabs({
			border : false,
			fit : true
		});
		setTimeout(function() {
			var src = '${pageContext.request.contextPath}/admin/rec/layout/home.jsp';
			centerTabs.tabs('add', {
				title : '首页',
				content : '<iframe src="' + src + '" frameborder="0" style="border:0;width:100%;height:99.2%;"></iframe>',
				closable : true,
				iconCls : ''
			});
		}, 0);
	});
</script>
<div id="centerTabs"></div>