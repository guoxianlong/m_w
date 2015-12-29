package adultadmin.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voCatalog;
import adultadmin.bean.barcode.BarcodeLogBean;
import adultadmin.bean.barcode.CatalogCodeBean;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.util.db.DbOperation;

/**
 *  <code>BarcodeCreateManagerImpl.java</code>
 *  <p>功能:条码生成规则管理Service实现类
 *
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-1-6 上午11:04:54
 *  @version 1.0
 *  </br>最后修改人 无
 */
public class BarcodeCreateManagerServiceImpl extends BaseServiceImpl implements IBarcodeCreateManagerService {

	public BarcodeCreateManagerServiceImpl(int useConnType,DbOperation dbOp){
		this.useConnType=useConnType;
		this.dbOp=dbOp;
	}

//	public BarcodeCreateManagerServiceImpl(){
//		this.useConnType=CONN_IN_SERVICE;
//	}

	public boolean addCatalogCode(CatalogCodeBean catalogCodeBean){
		return addXXX(catalogCodeBean, "catalog_code");
	}

	public boolean updateCatalogCode(String set,String condition){
		return updateXXX(set, condition, "catalog_code");
	}
	public CatalogCodeBean getCatalogCode(String condition){
		return (CatalogCodeBean) getXXX(condition, "catalog_code", "adultadmin.bean.barcode.CatalogCodeBean");
	}
	public boolean deleteProductBarcode(String condition) {
		return deleteXXX(condition, "product_barcode");
	}

	public CatalogCodeBean getCatalogCatalogCode(String condition){
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.name,cc.id as cid,cc.catalog_code ");
		sql.append("from catalog c join catalog_code cc on c.id=cc.catalog_id ");
		//// by hdy：修改为新分类表
		///sql.append("from spi_product_catalog c join catalog_code cc on c.id=cc.catalog_id ");
		if(condition!=null){
			sql.append("where "+condition);
		}
		ResultSet rs = null;
		try{
			rs = this.getDbOp().executeQuery(sql.toString());
			List catalogList= new ArrayList();
			CatalogCodeBean codeBean = null;
			if(rs.next()){
				codeBean = new CatalogCodeBean();
				voCatalog catalog = new voCatalog();
				catalog.setId(rs.getInt("id"));
				catalog.setName(rs.getString("name"));
				codeBean.setId(rs.getInt("cid"));
				codeBean.setCatalogCode(rs.getString("catalog_code"));
				codeBean.setVocatalog(catalog);
				catalogList.add(codeBean);
			}
			rs.close();
			return codeBean;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			this.release(dbOp);
		}
	}
	public List getCatalogCodeList(String condition){
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.name,cc.id as cid,cc.catalog_code ");
		sql.append("from catalog c left join catalog_code cc on c.id=cc.catalog_id ");
	//// by hdy：修改为新分类表
		//sql.append("from spi_product_catalog c left join catalog_code cc on c.id=cc.catalog_id ");
		if(condition!=null){
			sql.append("where "+condition);
		}
		ResultSet rs = null;
		try{
			rs = this.getDbOp().executeQuery(sql.toString());
			List catalogList= new ArrayList();
			while(rs.next()){
				CatalogCodeBean codeBean = new CatalogCodeBean();
				voCatalog catalog = new voCatalog();
				catalog.setId(rs.getInt("id"));
				catalog.setName(rs.getString("name"));
				codeBean.setId(rs.getInt("cid"));
				codeBean.setCatalogCode(rs.getString("catalog_code"));
				codeBean.setVocatalog(catalog);
				catalogList.add(codeBean);
			}
			rs.close();
			return catalogList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			this.release(dbOp);
		}
	}
	//////update by hdy:将原来对catalog表的查询，修改为对新的分类表spi_product_catalog
	///再次修改回去为catalog表
	public List getCatalogStandardsList(String condition){
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.name,cc.id as cid,cc.standards_id,cc.standards_id,cc.catalog_code,cc.standards_name from (select * from catalog  ");
		if(condition!=null){
			sql.append("where "+condition);
		}
		sql.append(") as c left join (select cc.id,cc.catalog_id,cc.standards_id,cc.catalog_code,p.standards_name from catalog_code cc ");
		sql.append("join product_standards p on cc.standards_id=p.id) as cc on c.id=cc.catalog_id");
//		System.out.println(sql);
		ResultSet rs = null;
		try{
			rs = this.getDbOp().executeQuery(sql.toString());
			List catalogList= new ArrayList();
			while(rs.next()){
				CatalogCodeBean codeBean = new CatalogCodeBean();
				voCatalog catalog = new voCatalog();
				catalog.setId(rs.getInt("id"));
				catalog.setName(rs.getString("name"));
				codeBean.setId(rs.getInt("cid"));
				codeBean.setStandardsId(rs.getInt("standards_id"));
				codeBean.setCatalogCode(rs.getString("catalog_code"));
				codeBean.setStandardsName(rs.getString("standards_name"));
				codeBean.setVocatalog(catalog);
				catalogList.add(codeBean);
			}
			rs.close();
			return catalogList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			this.release(dbOp);
		}
	}
	public boolean addProductBarcode(ProductBarcodeVO barcodeVO){
		return addXXX(barcodeVO, "product_barcode");
	}

	public ProductBarcodeVO getProductBarcode(String condition){
		return (ProductBarcodeVO)getXXX(condition, "product_barcode", "adultadmin.action.vo.ProductBarcodeVO");
	}

	public List getProductBarcodeList(String condition, int index, int count,
			String orderBy){
		condition += " and barcode_status not in(2)";
		return getXXXList(condition, index, count, orderBy, "product_barcode", "adultadmin.action.vo.ProductBarcodeVO");
	}

	public boolean updateProductBarcode(String set,String condition){
		return updateXXX(set, condition, "product_barcode");
	}

	public boolean addProductBarcodeLog(BarcodeLogBean barcodeLogBean){
		return addXXX(barcodeLogBean,"barcode_log");
	}
	/*public voProduct findProductBarcode(String condition){
		StringBuffer sql = new StringBuffer();
		sql.append("select p.code,p.name,p.oriname,p.price,p.price2,p.proxys_name,id,product_id,barcode,barcode_source");
		if(condition!=null){
			sql+="where "+condition;
		}
		Statement st = null;
		ResultSet rs = null;
		ProductBarcodeVO barcodeVO = null;
		try{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()){
				barcodeVO = new ProductBarcodeVO();
				barcodeVO.setId(rs.getInt("id"));
				barcodeVO.setProductId(rs.getInt("product_id"));
				barcodeVO.setBarcode(rs.getString("barcode"));
				barcodeVO.setBarcodeSource(rs.getInt("barcode_source"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeStatement(st);
			closeResultSet(rs);
		}
		return barcodeVO;
	}*/

	@Override
	public List<Map<String, String>> getDynamicCheckCycle(int area) {
		List<Map<String, String>> rst = new ArrayList<Map<String, String>>();
		ResultSet rs = null;
		String sql = "select check_date_rang,check_time_rang from dynamic_check_cycle where area_id="+area;
		try{
			rs = this.getDbOp().executeQuery(sql);
			if(rs.next()){
				Map<String, String> map = new HashMap<String, String>();
				map.put("check_date_rang", rs.getString("check_date_rang"));
				map.put("check_time_rang", rs.getString("check_time_rang"));
				rst.add(map);
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			this.release(dbOp);
		}
		return rst;
	}
}
