package mmb.product.imageRepository;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voUser;
import adultadmin.framework.IConstants;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 
 * @author limm
 * <p>
 * create_datetime : 2012-06-20
 * </p>
 * 
 * <p>
 * 根据商品id查询相关联父商品下所有子商品的id集合的800浏览图品集合
 * 以及查找图片定位功能的实现
 * </p>
 */
public class ProductImagesAction extends DispatchAction {
	/**
	 * 商品图品查询页每页显示数量
	 */
	public static final int PRODUCT_PICTURE_PAGE_SIZE = 1;

	/**
	 * 获取商品id，然后判断当前用户状态，返回商品呢id
	 */
	public ActionForward selectProductImagesListByProductId(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("add");
		}
		
		// 商品id
		int productId = StringUtil.StringToId(request.getParameter("productId"));
		
		if (productId == 0) {
			return mapping.findForward(IConstants.SUCCESS_KEY);
		}
		
		DbOperation dbOperation = null;
		try{
			request.setAttribute("productId", productId+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOperation != null ){
				dbOperation.release();
			}
		}
		return mapping.findForward("productImagesList");
	}
	
	/**
	 * 根据图片id快速找到800浏览图片，在页面上显示
	 */
	public ActionForward selectProductImageByPicId(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("add");
		}
		// 图片id
		int picId = StringUtil.StringToId(request.getParameter("picId"));
		// 商品id
		int productId = StringUtil.StringToId(request.getParameter("productId"));
		
		
		
		if (picId == 0 && productId == 0) {
			return mapping.findForward(IConstants.SUCCESS_KEY);
		}
		
		DbOperation dbOperation = null;
		try{
			List productImagesList = null;
			if (productId != 0) {
				dbOperation = new DbOperation();
				dbOperation.init(DbOperation.DB_SLAVE);
				ProductImagesService service = new ProductImagesService(IBaseService.CONN_IN_SERVICE, dbOperation);
				
				// 根据商品id查询相关联父商品下所有子商品的id集合
				productImagesList = service.getProductImagesListByProductId(productId, ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_9, ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_0,
						0, 1);
			} else {
				dbOperation = new DbOperation();
				dbOperation.init(DbOperation.DB_SLAVE);
				ImageRepositoryService service = new ImageRepositoryService(IBaseService.CONN_IN_SERVICE, dbOperation);
	
				
				// 根据图片id查询
				productImagesList = service.getProductImageListById(picId);
			}
			request.setAttribute("productImagesList", productImagesList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOperation != null ){
				dbOperation.release();
			}
		}
		return mapping.findForward("selectProductImageByPicId");
	}
	
	/**
	 * 根据图片编号快速找到图片，在页面上显示
	 */
	public ActionForward selectProductImageByPicCode(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("add");
		}
		
		DbOperation dbOperation = null;
		try{
			dbOperation = new DbOperation();
			dbOperation.init(DbOperation.DB_SLAVE);
			ImageRepositoryService service = new ImageRepositoryService(IBaseService.CONN_IN_SERVICE, dbOperation);

			// 图片编号
			String code = request.getParameter("code");
			String[] codeArray = code.split("/");
			
			List productImagesList = null;
			if(codeArray.length == 1) {
				int picId = StringUtil.toInt(codeArray[0]);
				if(picId == -1) {
					request.setAttribute("error", "编号有错误！");
					return mapping.findForward("selectProductImageByPicCode");
				}
				
				// 根据图片id查询
				productImagesList = service.getProductImageListById(picId);
			} else if(codeArray.length == 2) {
				int productId = StringUtil.toInt(codeArray[0]);
				if(productId == -1) {
					request.setAttribute("error", "编号有错误！");
					return mapping.findForward("selectProductImageByPicCode");
				}
				String picName = codeArray[1];
				
				// 根据商品id和图片名称查询图片
				productImagesList = service.getProductImageListByProductIdAndPicName(productId, picName);
			}
			
			ImageRepositoryDto bean = productImagesList!=null && productImagesList.size()>0 ? (ImageRepositoryDto) productImagesList.get(0) : null;
			
			if(bean == null) {
				request.setAttribute("error", "编号有错误！");
				return mapping.findForward("selectProductImageByPicCode");
			}
			
			int sourceId = 0;
			if(bean.getSourceId() == 0) {
				sourceId = bean.getId();
			}else {
				sourceId = bean.getSourceId();
			}
			
			List viewList = service.getProductImageListByProductId(bean.getProductInfoId(), sourceId);
			
			List sourcePicList = service.getProductImageListById(sourceId);
			viewList.addAll(sourcePicList);
			
			request.setAttribute("viewList", viewList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOperation != null ){
				dbOperation.release();
			}
		}
		return mapping.findForward("selectProductImageByPicCode");
	}
	
	/**
	 * 根据商品id找到所有拥有相同父id的800浏览图片，在页面上显示（左侧树显示）
	 */
	public static List getProductImagesListByProductId(String proId) {
		int productId = StringUtil.StringToId(proId);
		if (productId == 0) {
			return null;
		}
		
		DbOperation dbOperation = null;
		List productImagesList = null;
		try{
			dbOperation = new DbOperation();
			dbOperation.init(DbOperation.DB_SLAVE);
			ProductImagesService service = new ProductImagesService(IBaseService.CONN_IN_SERVICE, dbOperation);

			
			// 根据商品id查询相关联父商品下所有子商品的id集合
			productImagesList = service.getProductImagesListByProductId(productId, ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_9, ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_0,
					0, -1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOperation != null ){
				dbOperation.release();
			}
		}
		return productImagesList;
	}
	
	/**
	 * 根据图片id快速找到图片，在页面上显示
	 */
	public ActionForward selectPictureById(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOperation = null;
		try{
			dbOperation = new DbOperation();
			dbOperation.init();
			ImageRepositoryService service = new ImageRepositoryService(IBaseService.CONN_IN_SERVICE, dbOperation);
			int id = StringUtil.StringToId(request.getParameter("id"));
			List beanList = null;
			beanList = service.getProductImageListById(id);
			if(beanList == null || beanList.size() <= 0){
				request.setAttribute("error", "图片不存在！");
			}
			for(int i=0;i<beanList.size();i++){
				ImageRepositoryDto dto=(ImageRepositoryDto) beanList.get(i);
				dto.setCompresionFactor(dto.getCompresionFactor()*10);
			}
			request.setAttribute("productImageList", beanList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOperation != null ){
				dbOperation.release();
			}
		}
		return mapping.findForward("selectPictureById");
	}
}
