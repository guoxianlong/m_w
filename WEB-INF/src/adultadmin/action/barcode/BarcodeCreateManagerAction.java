package adultadmin.action.barcode;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.barcode.CatalogCodeBean;
import adultadmin.framework.BaseAction;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;

/**
 *  <code>BarcodeCreateManagerAction.java</code>
 *  <p>功能:条码生成管理Action
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-1-6 上午11:56:49	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class BarcodeCreateManagerAction extends BaseAction{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			request.setAttribute("tip", "对不起，你还没有登陆不能进行此操作。");
			return mapping.findForward("failure");
		}
		UserGroupBean group = user.getGroup();
	 	if(!group.isFlag(297)){
	 		request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
            request.setAttribute("result", "failure");
	 	}
		String action = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("action")));
		int catalogId = StringUtil.StringToId(request.getParameter("catalogId"));
		int catalogIdsub = StringUtil.StringToId(request.getParameter("catalogIdsub"));
		int catalogCodeId = StringUtil.StringToId(request.getParameter("catalogCodeId"));
		int codeFlag = StringUtil.StringToId(request.getParameter("codeFlag"));
		String catalogCode = StringUtil.dealParam(request.getParameter("catalogCode"));
		int standardsId = StringUtil.StringToId(request.getParameter("standardsName"));
		
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, null);
		int flagAction=0;
		try{
			// 查找所有产品分类
			if("catalogs".equals(action)){
				flagAction=0;
			/*}else if("catalog".equals(action)){         // 根据id查找单个产品分类
				CatalogCodeBean codeBean = bcmService.getCatalogCatalogCode("c.id="+catalogId);
				if(codeBean==null){
					request.setAttribute("tip", "对不起，没有找到该分类。");
					return mapping.findForward("failure");
				}
				request.setAttribute("codeBean", codeBean);
				return mapping.findForward("catalog");*/
			}else if("updateCatalog".equals(action)){        // 修改产品分类编号
				if(catalogCode==null || catalogCode.length()==0){
					request.setAttribute("tip", "必须输入2位的分类编号！");
					return mapping.findForward("failure");
				}
				//检查编号是否重复
				if(bcmService.getCatalogCode("code_flag="+codeFlag+" and catalog_code="+catalogCode)==null){
					// 开始事务
					bcmService.getDbOp().startTransaction();     
					if(catalogCodeId==0){
						CatalogCodeBean catalogCodeBean = new CatalogCodeBean();
						catalogCodeBean.setId(bcmService.getNumber("id", "catalog_code", "max", "id>0")+1);
						catalogCodeBean.setCatalogCode(catalogCode);
						catalogCodeBean.setCatalogId(catalogId);
						catalogCodeBean.setCodeFlag(codeFlag);
						bcmService.addCatalogCode(catalogCodeBean);
						flagAction=0;
					}else{
						if(bcmService.updateCatalogCode(" catalog_code='"+catalogCode+"'", "id="+catalogCodeId)){
							flagAction=0;
						}else{
							request.setAttribute("tip", "修改分类编号失败！");
							// 回滚事务
							bcmService.getDbOp().rollbackTransaction();    
							return mapping.findForward("failure");
						}
					}
					// 提交事务
					bcmService.getDbOp().commitTransaction();     
				}else{
					request.setAttribute("tip", "与其他产品分类的编号重复，请重新填写！");
					return mapping.findForward("failure");
				}
			}else if("catalogsStandards".equals(action)){           //根据id查找二级分类和规格
				flagAction=1;
			}else if("ucatalogStandards".equals(action)){       //修改编号和规格
				//检查编号是否重复
				if(bcmService.getCatalogCode("code_flag="+codeFlag+" and catalog_code="+catalogCode)==null){
					// 开始事务
					bcmService.getDbOp().startTransaction();     
					if(catalogCodeId==0){
						CatalogCodeBean catalogCodeBean = new CatalogCodeBean();
						catalogCodeBean.setId(bcmService.getNumber("id", "catalog_code", "max", "id>0")+1);
						catalogCodeBean.setCatalogCode(catalogCode);
						catalogCodeBean.setCatalogId(catalogIdsub);
						catalogCodeBean.setStandardsId(standardsId);
						catalogCodeBean.setCodeFlag(codeFlag);
						bcmService.addCatalogCode(catalogCodeBean);
					}else{
						String set = "catalog_code='"+catalogCode+"',standards_id="+standardsId+",code_flag="+codeFlag;
						bcmService.updateCatalogCode(set,"id="+catalogCodeId);
					}
					flagAction=1;
					// 提交事务
					bcmService.getDbOp().commitTransaction();
				}else{
					bcmService.getDbOp().startTransaction();     
					String set ="standards_id="+standardsId+",code_flag="+codeFlag;
					bcmService.updateCatalogCode(set,"id="+catalogCodeId);
					flagAction=1;
					// 提交事务
					bcmService.getDbOp().commitTransaction();
					//request.setAttribute("tip", "与其他产品分类的编号重复，请重新填写！");
					//return mapping.findForward("failure");
				}
			}
			
			if(flagAction==0){
				// 查找所有一级分类
				List list = bcmService.getCatalogCodeList("parent_id=0");
				request.setAttribute("list", list);
				return mapping.findForward("catalogs");
			}else{
				CatalogCodeBean codeBean = bcmService.getCatalogCatalogCode("c.id="+catalogId);
				if(codeBean==null){
					request.setAttribute("tip", "对不起，没有找到该分类。");
					return mapping.findForward("failure");
				}
				List list = bcmService.getCatalogStandardsList("parent_id="+catalogId);
				request.setAttribute("codeBean", codeBean);
				request.setAttribute("list", list);
				return mapping.findForward("catalogStand");
			}
		}catch(Exception e){
			e.printStackTrace();
			// 回滚事务
			bcmService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", "对不起，程序异常。");
			return mapping.findForward("failure");
		}finally{
			bcmService.releaseAll();
		}
	}
	
}
