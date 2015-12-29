package mmb.product.imageRepository;

import java.io.Serializable;

public class ImageRepositoryDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public int productInfoId;//主商品id，为0表示不属于任何一个主商品。（保留为0）
	public int type;//图片的类型0:原图 9:浏览图，1:wap1.0 ，2: wap2.0，3:wap 3.0其他值保留
	public int sourceId;//原图Id，0表示原图
	// 图片的子类型 0：与版本无关的图，1：列表页图,2：详细页图,3：搜索结果页图,4：高清图 5：高级编辑图 其他值保留
	public int subType;
	public int displayOrder;//图片的显示排序，大的在前
	public String intro;//图片的介绍
	public int innerOrder;//内部排序（对type，sub_type相同的图片的内部序号，由1开始）
	public String pathDir;//保存路径的目录部分，相对路径，且不以”/”开头，但以”/”结尾。目录名目前使用主商品ID作为命名。
	public String name; //文件名，不包含目录。文件名是按照一定规则生成的。生成规则另附如下。
	public int size;//以B为单位的图片的高度
	public int width;//以px为单位的图片的宽度
	public int height;//以px为单位的图片的高度
	public float compresionFactor;//压缩系数，1.0-10.0的一个数
	public int userId;//操作员ID
	public String userName;//操作员名字
	public String lastModified;//最后修改日期
	// 商品名称
	private String productName;
	// 图片数量
	private int count;
	// 图片type 类别名称
	private String typeName;
	// 图片sub_type 类别名称
	private String subTypeName;
	// 图片显示路径
	private String viewPathDir;
	// 图片显示名称
	private String viewName; 
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductInfoId() {
		return productInfoId;
	}
	public void setProductInfoId(int productInfoId) {
		this.productInfoId = productInfoId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	public int getSubType() {
		return subType;
	}
	public void setSubType(int subType) {
		this.subType = subType;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public int getInnerOrder() {
		return innerOrder;
	}
	public void setInnerOrder(int innerOrder) {
		this.innerOrder = innerOrder;
	}
	public String getPathDir() {
		return pathDir;
	}
	public void setPathDir(String pathDir) {
		this.pathDir = pathDir;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public float getCompresionFactor() {
		return compresionFactor;
	}
	public void setCompresionFactor(float compresionFactor) {
		this.compresionFactor = compresionFactor;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getSubTypeName() {
		return subTypeName;
	}
	public void setSubTypeName(String subTypeName) {
		this.subTypeName = subTypeName;
	}
	public String getViewPathDir() {
		return viewPathDir;
	}
	public void setViewPathDir(String viewPathDir) {
		this.viewPathDir = viewPathDir;
	}
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	

}
