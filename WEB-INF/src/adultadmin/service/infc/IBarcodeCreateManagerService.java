package adultadmin.service.infc;

import java.util.List;
import java.util.Map;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.bean.barcode.BarcodeLogBean;
import adultadmin.bean.barcode.CatalogCodeBean;

/**
 *  <code>IBarcodeCreateManager.java</code>
 *  <p>功能:商品条码Service接口
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-1-6 上午10:58:35	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public interface IBarcodeCreateManagerService extends IBaseService{
	
	/**
	 * 功能:添加产品条形码
	 * <p>作者文齐辉 2010-12-28 下午04:33:03
	 * @param barcodeVO
	 */
	public boolean addProductBarcode(ProductBarcodeVO barcodeVO);

	/**
	 * 功能:根据条件得到产品条码信息
	 * <p>作者文齐辉 2010-12-28 下午05:39:05
	 * @param string 查询条件
	 * @return 
	 */
	public ProductBarcodeVO getProductBarcode(String condition);
	
	
	/**
	 * 功能:根据条件得到产品条码List
	 * <p>作者文齐辉 2011-1-14 下午07:17:36
	 * @param condition
	 * @return
	 */
	public List getProductBarcodeList(String condition, int index, int count,
			String orderBy);

	/**
	 * 功能:修改产品条形码
	 * <p>作者文齐辉 2010-12-29 上午11:39:11
	 * @param barcodeVO
	 */
	public boolean updateProductBarcode(String set,String condition);

	/**
	 * 功能:添加条码修改日志
	 * <p>作者文齐辉 2011-1-18 下午03:12:04
	 * @param barcodeLogBean
	 * @return
	 */
	public boolean addProductBarcodeLog(BarcodeLogBean barcodeLogBean);

	/**
	 * 功能:添加一个分类编号
	 * <p>作者文齐辉 2011-1-6 下午04:51:30
	 * @param catalogCoceBean
	 * @return
	 */
	public boolean addCatalogCode(CatalogCodeBean catalogCodeBean);
	/**
	 * 功能:修改产品分类条码
	 * <p>作者文齐辉 2011-1-6 下午04:18:02
	 * @param set
	 * @param condition
	 * @return
	 */
	public boolean updateCatalogCode(String set,String condition);
	
	/**
	 * 功能:获取分类和分类编号信息
	 * <p>作者文齐辉 2011-1-6 下午04:39:39
	 * @param condition
	 * @return
	 */
	public CatalogCodeBean getCatalogCatalogCode(String condition);
	
	/**
	 * 功能:获取分类编号信息
	 * <p>作者文齐辉 2011-1-6 下午04:39:39
	 * @param condition
	 * @return
	 */
	public CatalogCodeBean getCatalogCode(String condition);
	
	/**
	 * 功能:得到产品条码List
	 * <p>作者文齐辉 2011-1-6 下午04:19:11
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
//	public List getCatalogCodeList(String condition,int index,int count,String orderBy);
	
	
	/**
	 * 功能:得到分类和编码信息List
	 * <p>作者文齐辉 2011-1-6 下午02:37:51
	 * @return
	 */
	public List getCatalogCodeList(String condition);
	
	/**
	 * 功能:得到分类和规格信息List
	 * <p>作者文齐辉 2011-1-6 下午02:37:51
	 * @return
	 */
	public List getCatalogStandardsList(String condition);
	
	/**
	 * 功能:删除无效条码
	 * @return
	 */
	public boolean deleteProductBarcode(String condition);

	/** 
	 * @Description: 查询对应区域的盘点限制时间
	 * @return List<Map<String,String>> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年7月1日 下午3:16:47 
	 */
	public List<Map<String, String>> getDynamicCheckCycle(int area);
}
