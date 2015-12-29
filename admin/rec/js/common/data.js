/**
 * 
 * 请不要格式化此文件
 * 
 * demo示例的配置;
 * 每个分类（category）有多个组件(comps)
 * 每个组件(component)对应多个示例(samples);
 * component 说明:
 *      1) name: 组件名,会显示在左边的navTree中
 *      2) samples :
 *          3.1) title : 该示例的名字,将出现在demo框架的右边
 *          3.2) height : 该示例的大小,该值将决定装载示例iframe的高度.如果没配置该项,则使用默认高度(400px);iframe的宽度是固定的(600px)
 *          3.3) url : 该示例的路径,相对于/demos/index.html
 */
 
var pathName = window.location.pathname.substring(1);

var webName =(pathName==""?"":pathName.substring(0,pathName.indexOf('/')));

var url_path= window.location.protocol+'//'+window.location.host+'/'+webName;


var omUiDemos =  {components:[]};

//Widgets分类
omUiDemos.components[0]={
    category: '用户管理',
    comps: [{name: '后台用户', samples: [
    			{title: '添加用户', url:url_path+'/admin/user/addUser.jsp',height: 500,java: 'servlet/OmGridServlet.java'},
    			{title: '用户列表', url: url_path+'/userAction.do?method=getUserList',height: 500,java: 'servlet/OmGridServlet.java'},
    			{title: 'easyUI', url: url_path+'/admin/user/easyUI.jsp',height: 500,java: 'servlet/OmGridServlet.java'}
            ]}, {name: '前台用户', samples: [
                {title: '基本功能', url: 'tree/simple.'}, 
                {title: '自定义树节点图标',isNew:true, height: 530, url: 'tree/node-icons.'}
            ]}
    ]
};

//Window分类
omUiDemos.components[1]={
    category: '商品管理',
    comps: [{name: '品牌管理', samples: [
                {title: '品牌列表', url: url_path+'/admin/product/brand/productBrandList.jsp'},
                {title: '添加品牌', url: 'messagebox/default.'},
                {title: '品牌信息', url: 'messagebox/alert_icon.'},
                {title: '标题和内容可以用', url: 'messagebox/defined.'}
            ]},{name: '商品分类管理', samples: [
                {title : '分类信息', url: 'admin/productCatalog.do?method=catalogList'},
                {title: '商品属性管理', url: url_path+'/admin/product/property/index.jsp'}
            ]},{name: '商品信息', samples: [
                {title: '添加商品', url: 'messagetip/default.'}, 
                {title : '商品显示页面', url: 'messagetip/icons.'},
                {title : '库存信息', url: 'messagetip/outOfIframe.'}
            ]}
    ]
};

omUiDemos.components[2]={
    category: '订单管理',
    comps: [{name: '商品信息', samples: [
                {title: '基本功能', url: 'messagebox/default.'},
                {title: 'Alert各种图标', url: 'messagebox/alert_icon.'},
                {title: '标题和内容可以用', url: 'messagebox/defined.'}
            ]}
    ]
};

omUiDemos.components[3]={
    category: '系统管理',
    comps: [{name: '字典', samples: [
                {title: '添加字典', url: 'messagebox/default.'},
                {title: '字典信息', url: 'messagebox/alert_icon.'},
                {title: '标题和内容可以用', url: 'messagebox/defined.'}
            ]},{name: '商品缓存', samples: [
                {title: '清空缓存', url: 'dialog/modal.'} 
            ]}
    ]
};



