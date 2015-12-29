package mmb.product.imageRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;
public class ImageRepositoryService extends  BaseServiceImpl{
	protected Connection conn = null;
	
	public ImageRepositoryService(int useConnType, DbOperation dbOp){
		this.useConnType = useConnType;
		this.dbOp = dbOp;
		this.conn = dbOp.getConn();
	}
	public ImageRepositoryService(){
		this.useConnType = CONN_IN_SERVICE;
	}
	public boolean addImageRepositoryBean(ImageRepositoryBean bean){
		
		return addXXX(bean, "image_repository");
	}
	public ImageRepositoryBean getImageRepositoryBean(String condition){
		return (ImageRepositoryBean) getXXX(condition, "image_repository", "mmb.product.imageRepository.ImageRepositoryBean");
	}
	public List getImageRepositoryBeanlist(String condition,int index,int count,String orderBy){
		return getXXXList(condition, index, count, orderBy,"image_repository", "mmb.product.imageRepository.ImageRepositoryBean");
	}
	public boolean updateImageRepositoryBean(String set,String condition){
		return updateXXX(set, condition, "image_repository");
	}
	public boolean deleteImageRepositoryBean(String condition){
		return deleteXXX(condition, "image_repository");
	}
	
	public int getImageRepositoryBeanCount(String condition) {
		return getXXXCount(condition, "image_repository", "id");
	}
    //查询 最大的内部排序（对type，sub_type相同的图片的内部序号，由1开始）
	public int getMaxInnerOrder(int productInfoId,int type,int subType){
		int maxInnerOrder=0;
		String query="SELECT max(inner_order) mi FROM image_repository i where product_info_id="+productInfoId+" and i.type="+type+" and i.sub_type="+subType;
		try {
			ResultSet rs=dbOp.executeQuery(query);
			if(rs.next()){
				maxInnerOrder=rs.getInt("mi");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			release(dbOp);
		}
		return maxInnerOrder;
	}
	//查询子商品的简单信息
	
	public Map getCheckedSubProduct(String sql){
		Map ms=new HashMap();
		try {
			ResultSet rs=dbOp.executeQuery(sql);
			while(rs.next()){
				int id=rs.getInt("id");
				String pic2=rs.getString("pic2");
				ms.put(id+"", pic2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return ms;
	}
	
	/**
	 * 查询所有主商品图片相册的个数
	 * @return 所有主商品图片相册的个数
	 */
	public int getImageRepositoryCount() {
		int count = 0;
		Statement st = null;
		ResultSet rs = null;
		String sql = "select count(distinct(a.product_info_id)) as count from image_repository a join spi_product_info b on a.product_info_id = b.id order by a.product_info_id";
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				count = rs.getInt("count");
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return count;
	}

	/**
	 * 根据商品id查询商品图片主相册
	 * @param productId   商品id
	 * @param currentPage 当前页数
	 * @return   		     商品图片的list
	 */
	public List getProductImagesList(ImageRepositoryBean bean, int currentPage) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			st = conn.createStatement();
			StringBuffer buf = new StringBuffer();
			buf.append("select a.product_info_id as disId, a.*, count(a.product_info_id) as count, max(a.id) as maxId, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id");
			if(bean == null) {
				int limitFrom = currentPage * ImageRepositoryConstant.PRODUCT_PICTURE_PAGE_SIZE;
				rs = st.executeQuery("select a.product_info_id as disId, max(a.id) as maxId from image_repository a group by  a.product_info_id order by maxId desc limit "+limitFrom+", "+ImageRepositoryConstant.PRODUCT_PICTURE_PAGE_SIZE);
				String productIds = "";
				while(rs.next()){
					if(productIds.length()>0){
						productIds = productIds+","+rs.getInt("disId");
					}else{
						productIds = String.valueOf(rs.getInt("disId"));
					}
				}
				rs.close();
				rs = null;
				buf.append(" where a.product_info_id in(").append(productIds).append(")");
				buf.append(" group by ").append(" a.product_info_id order by maxId desc ").append(" limit ").append(limitFrom).append(", ").append(ImageRepositoryConstant.PRODUCT_PICTURE_PAGE_SIZE);//.append(" limit 0, ").append(ImageRepositoryConstant.PRODUCT_PICTURE_PAGE_SIZE);
			}else {
				if(bean.getProductInfoId() > 0) {
					buf.append(" where a.product_info_id = ").append(bean.getProductInfoId())
						.append(" group by ").append(" a.product_info_id").append(" limit 0, 1");
				}
			}
		
			rs = st.executeQuery(buf.toString());
			ImageRepositoryDto dto = null;
			while (rs.next()) {
				if(rs.getInt("id") == 0) {
					break;
				}
				// 封装ImageRepositoryDto 的javaBean
				dto = this.wrapperImageRepositoryDto(rs);
				// 优化：显示每个商品中图片大小最小的图 start
				getProductImageMinSizeByProductId(rs.getInt("product_info_id"), rs.getInt("id"), dto);
				// 优化：显示每个商品中图片大小最小的图 end
				
				// 集合list中添加对象
				list.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
//			close();
		}
		return list;
	}
	
	/**
	 * 根据商品id查询商品所有图片,并对每个分类以及分类下的子类进行封装
	 * @param productId 商品id
	 * @return   		商品图片的list
	 */
	public List getProductAllImagesList(ImageRepositoryBean bean) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select a.*, 1 as count, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id ");
		if(bean == null) {
			return null;
		}else {
			if(bean.getProductInfoId() > 0) {
				buf.append(" where a.product_info_id = ").append(bean.getProductInfoId())
					.append(" order by a.type, a.sub_type, a.display_order asc");
			}
		}
		try {
			st = conn.createStatement();
			rs = st.executeQuery(buf.toString());
			ImageRepositoryDto dto = null;
			//封装各类和各分类的map
			Map map = new HashMap();
			List originalList = null;
			Map wap1Map = null;
			List wap1SubType1List = null,wap1SubType2List = null,wap1SubType3List = null,wap1SubType4List = null,wap1SubType5List = null;
			Map wap2LittleMap = null;
			List wap2LittleSubType1List = null,wap2LittleSubType2List = null,wap2LittleSubType3List = null,
				wap2LittleSubType4List = null,wap2LittleSubType5List = null;
			Map wap2BigMap = null;
			List wap2BigSubType1List = null,wap2BigSubType2List = null,wap2BigSubType3List = null,
				wap2BigSubType4List = null,wap2BigSubType5List = null;
			List browseList = null;
			// 主商品名称
			String productName = null;
			while (rs.next()) {
				if(rs.getInt("id") == 0) {
					break;
				}
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
				} else {
					dto = this.wrapperImageRepositoryDto(rs);
				}
				if(productName == null) {
					productName = dto.getProductName();	
				}
					if(dto.getType() == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_0) {
						if(originalList == null) {
							originalList = new ArrayList();
						}
						originalList.add(dto);
					} else if(dto.getType() == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_1) {
						if(wap1Map == null) {
							wap1Map = new HashMap();
						}
						if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_1){
							if(wap1SubType1List == null) {
								wap1SubType1List = new ArrayList();
							}
							wap1SubType1List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_2){
							if(wap1SubType2List == null) {
								wap1SubType2List = new ArrayList();
							}
							wap1SubType2List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_3){
							if(wap1SubType3List == null) {
								wap1SubType3List = new ArrayList();
							}
							wap1SubType3List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_4){
							if(wap1SubType4List == null) {
								wap1SubType4List = new ArrayList();
							}
							wap1SubType4List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_5){
							if(wap1SubType5List == null) {
								wap1SubType5List = new ArrayList();
							}
							wap1SubType5List.add(dto);
						}
					} else if(dto.getType() == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_2) {
						if(wap2LittleMap == null) {
							wap2LittleMap = new HashMap();
						}
						if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_1){
							if(wap2LittleSubType1List == null) {
								wap2LittleSubType1List = new ArrayList();
							}
							wap2LittleSubType1List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_2){
							if(wap2LittleSubType2List == null) {
								wap2LittleSubType2List = new ArrayList();
							}
							wap2LittleSubType2List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_3){
							if(wap2LittleSubType3List == null) {
								wap2LittleSubType3List = new ArrayList();
							}
							wap2LittleSubType3List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_4){
							if(wap2LittleSubType4List == null) {
								wap2LittleSubType4List = new ArrayList();
							}
							wap2LittleSubType4List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_5){
							if(wap2LittleSubType5List == null) {
								wap2LittleSubType5List = new ArrayList();
							}
							wap2LittleSubType5List.add(dto);
						}
					} else if(dto.getType() == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_3) {
						if(wap2BigMap == null) {
							wap2BigMap = new HashMap();
						}
						if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_1){
							if(wap2BigSubType1List == null) {
								wap2BigSubType1List = new ArrayList();
							}
							wap2BigSubType1List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_2){
							if(wap2BigSubType2List == null) {
								wap2BigSubType2List = new ArrayList();
							}
							wap2BigSubType2List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_3){
							if(wap2BigSubType3List == null) {
								wap2BigSubType3List = new ArrayList();
							}
							wap2BigSubType3List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_4){
							if(wap2BigSubType4List == null) {
								wap2BigSubType4List = new ArrayList();
							}
							wap2BigSubType4List.add(dto);
						}else if(dto.getSubType() == ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_5){
							if(wap2BigSubType5List == null) {
								wap2BigSubType5List = new ArrayList();
							}
							wap2BigSubType5List.add(dto);
						}
					} else if(dto.getType() == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_9) {
						if(browseList == null) {
							browseList = new ArrayList();
						}
						browseList.add(dto);
					} 
					
			}
			map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_0, originalList);
			if(wap1Map != null) {
				wap1Map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_1, wap1SubType1List);
				wap1Map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_2, wap1SubType2List);
				wap1Map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_3, wap1SubType3List);
				wap1Map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_4, wap1SubType4List);
				wap1Map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_5, wap1SubType5List);
			}
			if(wap2LittleMap != null) {
				wap2LittleMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_1, wap2LittleSubType1List);
				wap2LittleMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_2, wap2LittleSubType2List);
				wap2LittleMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_3, wap2LittleSubType3List);
				wap2LittleMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_4, wap2LittleSubType4List);
				wap2LittleMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_5, wap2LittleSubType5List);
			}
			if(wap2BigMap != null) {
				wap2BigMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_1, wap2BigSubType1List);
				wap2BigMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_2, wap2BigSubType2List);
				wap2BigMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_3, wap2BigSubType3List);
				wap2BigMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_4, wap2BigSubType4List);
				wap2BigMap.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_SUBTYPE_5, wap2BigSubType5List);
			}
			map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_1, wap1Map);
			map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_2, wap2LittleMap);
			map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_3, wap2BigMap);
			map.put(""+ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_9, browseList);
			list.add(map);
			list.add(productName);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
//			close();
		}
		return list;
	}
	
	/**
	 * 根据商品id查询商品图片的各种分类，每种分类取一条数据
	 * @param productId 商品id
	 * @return   商品图片各种分类的list
	 */
	public List getProductImageTypesListByProductId(int productId) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select distinct(a.type) as disType, count(a.type) as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.product_info_id=").append(productId).append(" group by a.type");
		try {
			st = conn.createStatement();
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
				} else {
					dto = this.wrapperImageRepositoryDto(rs);
				}
				// 集合list中添加对象
				list.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
//			close();
		}
		return list;
	}
	
	/**
	 * 根据商品id查询商品图片的各种分类子类，每种分类取一条数据
	 * @param productId 商品id
	 * @return   商品图片各种分类的list
	 */
	public List getProductImageSubTypesListByProductId(int productId, int type) {//SELECT distinct(type),image_repository.* FROM `shop`.`image_repository` where product_info_id=53732 group by type ;
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		if(type == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_0 || 
				type == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_9) {
			buf.append("select 1 as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.product_info_id=").append(productId).append(" and a.type=").append(type);
		}else {
			buf.append("select distinct(a.sub_type) as disType, count(a.sub_type) as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.product_info_id=").append(productId).append(" and a.type=").append(type).append(" group by a.sub_type");
		}
		try {
			st = conn.createStatement();
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
				} else {
					dto = this.wrapperImageRepositoryDto(rs);
				}
				// 集合list中添加对象
				list.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
//			close();
		}
		return list;
	}
	
	/**
	 * 根据商品id查询商品图片的各种分类子类下的图片集合
	 * @param productId 商品id
	 * @return   商品图片各种分类的list
	 */
	public List getProductImageViewListByProductId(int productId, int type, int subType) {//SELECT distinct(type),image_repository.* FROM `shop`.`image_repository` where product_info_id=53732 group by type ;
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select 1 as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.product_info_id=").append(productId).append(" and a.type=").append(type).append(" and a.sub_type=").append(subType).append(" order by a.display_order asc");
		try {
			st = conn.createStatement();
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
				} else {
					dto = this.wrapperImageRepositoryDto(rs);
				}
				// 集合list中添加对象
				list.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
//			close();
		}
		return list;
	}
	
	/**
	 * 根据id查询商品图片的信息
	 * @param id 商品图片id
	 * @return   商品图片的list
	 */
	public List getProductImageListById(int id) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select 1 as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.id = ").append(id);
		try {
			st = conn.createStatement();
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
		} finally {
			closeStatement(st);
			closeResultSet(rs);
//			close();
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
			st = conn.createStatement();
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
	
	/**
	 * 根据商品id和sourceId查询商品图片的图片list
	 * @param productId  int： 商品id
	 * @param sourceId   int： 原图id
	 * @return   商品图片各种分类的list
	 */
	public List getProductImageListByProductId(int productId, int sourceId) {
		dbOp = this.getDbOp();
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select 1 as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.product_info_id=").append(productId).append(" and a.source_id=").append(sourceId).append(" order by a.size asc");
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
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			release(dbOp);
		}
		return list;
	}
	
	/**
	 * 根据id查询商品原图的信息
	 * @param id 商品图片id
	 * @return   商品图片的list
	 */
	public List getViewMinSizeProductSourceImageListById(int id) {
		dbOp = this.getDbOp();
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select 1 as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.id = ").append(id);
		try {
			st = dbOp.getConn().createStatement();
			rs = st.executeQuery(buf.toString());
			ImageRepositoryDto dto = null;
			while (rs.next()) {
				// 封装ImageRepositoryDto 的javaBean
				dto = this.wrapperImageRepositoryDto(rs);
				if(rs.getInt("type") == ImageRepositoryConstant.PRODUCT_PICTURE_TYPE_0) {
					// 优化：显示每个商品中图片大小最小的图 start
					getProductImageMinSizeByProductId(rs.getInt("product_info_id"), rs.getInt("id"), dto);
					// 优化：显示每个商品中图片大小最小的图 end
				} 
				// 集合list中添加对象
				list.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			release(dbOp);
//			close();
		}
		return list;
	}
	
	/**
	 * 根据id查询商品原图的信息
	 * @param id 商品图片id
	 * @return   商品图片的list
	 */
	public List getProductImageListByProductIdAndPicName(int productId, String name) {
		dbOp = this.getDbOp();
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select 1 as count, a.*, b.name as productName from image_repository a join spi_product_info b on a.product_info_id = b.id where a.product_info_id = ")
			.append(productId).append(" and a.name = '").append(name).append("'");
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
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			release(dbOp);
		}
		return list;
	}
	
	/**
	 * 根据主商品id查询所有子商品的id集合
	 * @param productId   int: 主商品id
	 * @return   主商品id下所有子商品的id集合list
	 */
	public List getProductImagesProductIds(int productInfoId) {
		dbOp = this.getDbOp();
		
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select sub_product_id from spi_sub_product_info where product_info_id = ").append(productInfoId);
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
	 * 根据商品id查询主商品的id
	 * @param productId   int: 商品id
	 * @return   主商品的id
	 */
	public int getProductInfoId(int productId) {
		dbOp = this.getDbOp();
		
		Statement st = null;
		ResultSet rs = null;
		// 主商品的id
		int productInfoId = 0;
		StringBuffer buf = new StringBuffer();
		buf.append("select product_info_id from spi_sub_product_info where sub_product_id = ").append(productId);
		try {
			st = dbOp.getConn().createStatement();
			rs = st.executeQuery(buf.toString());
			while (rs.next()) {
				// 集合list中添加对象
				productInfoId = rs.getInt("product_info_id");
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return -1;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			this.release(dbOp);
		}
		return productInfoId;
	}
	
	private void closeStatement(Statement st) {
		DbUtil.closeStatement(st);
	}

	private void closeResultSet(ResultSet rs) {
		DbUtil.closeResultSet(rs);
	}

	private void close() {
		DbUtil.closeConnection(conn);
	}
	public void updateBatch(String string) {
		if(string==null||"".equals(string)) {
			return ;
		}
		Statement statement = null;
		try {
			statement = getDbOp().getConn().createStatement();
			String[] strs = string.split(";");
			for(int i=0; i<strs.length; i++) {
				statement.addBatch(strs[i]);
			}
			statement.executeBatch();
			statement.clearBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(statement);
			release(getDbOp());
		}
	}
	
}
