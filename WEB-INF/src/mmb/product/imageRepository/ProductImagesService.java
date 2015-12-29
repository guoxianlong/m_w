package mmb.product.imageRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

/**
 * 
 * @author limm
 * <p>
 * create_datetime : 2012-06-20
 * </p>
 * 
 * <p>
 * 根据商品id查询相关联父商品下所有子商品的id集合的800浏览图品集合的相应操作
 * </p>
 */
public class ProductImagesService extends  BaseServiceImpl{
	
	public ProductImagesService(int useConnType, DbOperation dbOp){
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}
	public ProductImagesService(){
		this.useConnType = CONN_IN_SERVICE;
	}
	
	/**
	 * 根据商品id查询商品图片的各种分类子类下的图片集合
	 * @param productId   int: 商品id
	 * @param type        int: 分类
	 * @param subType     int：子分类
	 * @return   商品图片各种分类的list
	 */
	public List getProductImageViewListByProductId(int productId, int type, int subType) {
		dbOp = this.getDbOp();
		
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select 1 as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.product_info_id=").append(productId).append(" and a.type=").append(type).append(" and a.sub_type=").append(subType).append(" order by a.display_order asc");
		try {
			st = dbOp.getConn().createStatement();
			rs = st.executeQuery(buf.toString());
			ImageRepositoryDto dto = null;
			while (rs.next()) {
				// 封装ImageRepositoryDto 的javaBean
				dto = this.wrapperImageRepositoryDto(rs);
				// 集合list中添加对象
				list.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return null;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			this.release(dbOp);
		}
		return list;
	}
	
	
	/**
	 * 封装ImageRepositoryDto对象
	 * @param rs 查询结果
	 * @return ImageRepositoryDto对象
	 * @throws SQLException
	 */
	private ImageRepositoryDto wrapperImageRepositoryDto(ResultSet rs) throws SQLException{
		ImageRepositoryDto dto = new ImageRepositoryDto();
		dto.setId(rs.getInt("id"));
		dto.setProductInfoId(rs.getInt("product_info_id"));
		dto.setType(rs.getInt("type"));
		dto.setSourceId(rs.getInt("source_id"));
		dto.setSubType(rs.getInt("sub_type"));
		dto.setDisplayOrder(rs.getInt("display_order"));
		dto.setIntro(rs.getString("intro"));
		dto.setInnerOrder(rs.getInt("inner_order"));
		dto.setPathDir(rs.getString("path_dir"));
		dto.setName(rs.getString("name"));
		dto.setSize(rs.getInt("size"));
		dto.setWidth(rs.getInt("width"));
		dto.setHeight(rs.getInt("height"));
		dto.setCompresionFactor(rs.getFloat("compresion_factor"));
		dto.setUserId(rs.getInt("user_id"));
		dto.setUserName(rs.getString("user_name"));
		dto.setLastModified(rs.getString("last_modified"));
		dto.setCount(rs.getInt("count"));
		if(rs.getString("productName") != null) {
			dto.setProductName(rs.getString("productName"));
		}
		dto.setTypeName((String) ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_MAP.get(rs.getString("type")));
		dto.setSubTypeName((String) ImageRepositoryConstant.PRODUCT_PICTURE_SUB_TYPE_MAP.get(rs.getString("sub_type")));
		return dto;
	}
	
	/**
	 * 根据商品id查询相关联父商品下所有子商品的id集合
	 * @param productId   int: 商品id
	 * @param type        int: 分类
	 * @param subType     int：子分类
	 * @return   相关联父商品下所有子商品的id集合list
	 */
	public List getProductImagesProductIds(int productId) {
		dbOp = this.getDbOp();
		
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select sub_product_id from spi_sub_product_info where product_info_id = (select product_info_id from spi_sub_product_info where sub_product_id = ").append(productId);
		try {
			st = dbOp.getConn().createStatement();
			rs = st.executeQuery(buf.toString());
			while (rs.next()) {
				// 集合list中添加对象
				list.add(String.valueOf(rs.getInt("sub_product_id")));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return null;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			this.release(dbOp);
		}
		return list;
	}
	
	/**
	 * 根据商品id查询相关联父商品下所有子商品的id集合的800浏览图品集合
	 * @param productId    int: 商品id
	 * @param type         int: 分类
	 * @param subType      int：子分类
	 * @param limit        int：分页起始位置
	 * @param perCountPage int：每页数量，当为-1时表示不分页
	 * @return   商品图片各种分类的list
	 */
	public List getProductImagesListByProductId(int productId, int type, int subType, int limit, int perCountPage) {
		dbOp = this.getDbOp();
		
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		if(perCountPage == -1) {
			buf.append("(select 1 as count, a.*, NULL as productName from image_repository a join (select sub_product_id as productId from spi_sub_product_info where product_info_id = (select product_info_id from spi_sub_product_info where sub_product_id = ")
			.append(productId).append(")) b on a.product_info_id = b.productId where a.type=").append(type).append(" and a.sub_type=").append(subType).append(" order by a.product_info_id asc ) ");
			buf.append(" UNION (select 1 as count,b.*,NULL as productName from image_repository a LEFT JOIN image_repository b ON a.sku_id = b.sku_id ")
			.append("where a.sku_id = ").append(productId).append(" and a.type = 1 and a.sub_type = 2 and a.source_id = b.source_id and b.type = 10 and b.sub_type = 760)");
		} else {
			buf.append("select 1 as count, a.*, NULL as productName from image_repository a join (select sub_product_id as productId from spi_sub_product_info where product_info_id = (select product_info_id from spi_sub_product_info where sub_product_id = ")
				.append(productId).append(")) b on a.product_info_id = b.productId where a.type=").append(type).append(" and a.sub_type=").append(subType).append(" order by a.product_info_id asc limit ").append(limit).append(", ").append(perCountPage);
		}
		try {
			st = dbOp.getConn().createStatement();
			rs = st.executeQuery(buf.toString());
			ImageRepositoryDto dto = null;
			while (rs.next()) {
				// 封装ImageRepositoryDto 的javaBean
				dto = this.wrapperImageRepositoryDto(rs);
				if(rs.getInt("type") == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_0 ||
						rs.getInt("type") == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_9) {
					// 优化：显示每个商品中图片大小最小的图 start
					if(rs.getInt("type") == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_0) {
						getProductImageMinSizeByProductId(rs.getInt("product_info_id"), rs.getInt("id"), dto);
					}else {
						getProductImageMinSizeByProductId(rs.getInt("product_info_id"), rs.getInt("source_id"), dto);
					}
					// 优化：显示每个商品中图片大小最小的图 end
				}
				// 集合list中添加对象
				list.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return null;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			this.release(dbOp);
		}
		return list;
	}
	
	/**
	 * 根据商品id查询相关联父商品下所有子商品的id集合的800浏览图品集合数量
	 * @param productId   int: 商品id
	 * @param type        int: 分类
	 * @param subType     int：子分类
	 * @return   商品图片各种分类的list
	 */
	public int getProductImagesCountByProductId(int productId, int type, int subType) {
		dbOp = this.getDbOp();
		
		Statement st = null;
		ResultSet rs = null;
		int count = 0;
		StringBuffer buf = new StringBuffer();
		buf.append("select count(*) as num from image_repository a join (select sub_product_id as productId from spi_sub_product_info where product_info_id = (select product_info_id from spi_sub_product_info where sub_product_id = ")
			.append(productId).append(")) b on a.product_info_id = b.productId where a.type=").append(type).append(" and a.sub_type=").append(subType).append(" order by a.product_info_id asc");
		try {
			st = dbOp.getConn().createStatement();
			rs = st.executeQuery(buf.toString());
			ImageRepositoryDto dto = null;
			while (rs.next()) {
				count = rs.getInt("num");
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return -1;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			this.release(dbOp);
		}
		return count;
	}
	
	/**
	 * 根据商品id和图片sourceId查询商品图片大小最小的数据
	 * @param productId 商品id
	 * @return   图片大小最小的javaBean
	 */
	public ImageRepositoryDto getProductImageMinSizeByProductId(int productId, int sourceId, ImageRepositoryDto dto) {
		Statement st = null;
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		buf.append("select 1 as count, a.*, NULL as productName from image_repository a where a.product_info_id=").append(productId).append(" and a.source_id=").append(sourceId).append(" order by a.size asc limit 0,1");
		try {
			st = dbOp.getConn().createStatement();
			rs = st.executeQuery(buf.toString());
			while (rs.next()) {
				// 封装ImageRepositoryDto 的javaBean
				if(dto == null) {
					dto = this.wrapperImageRepositoryDto(rs);
				} else {
					dto.setViewPathDir(rs.getString("path_dir"));
					dto.setViewName(rs.getString("name"));
				}
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
//			close();
		}
		return dto;
	}
	
	private void closeStatement(Statement st) {
		DbUtil.closeStatement(st);
	}

	private void closeResultSet(ResultSet rs) {
		DbUtil.closeResultSet(rs);
	}
}
