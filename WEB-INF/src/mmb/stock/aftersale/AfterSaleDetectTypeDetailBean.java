package mmb.stock.aftersale;

import java.util.LinkedHashMap;
import java.util.Map;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.db.DbOperation;

//检测选项设置内容
public class AfterSaleDetectTypeDetailBean {
	public int id;
	public int afterSaleDetectTypeId; //检测选项id
	public String afterSaleDetectTypeName;
	public int parentId1; //商品一级分类
	public String parentId1Name;
	public String content; //内容（添加的每行都是一条记录）
	public int detectTypeParentId1;//检测选项内容一级分类id
	public int detectTypeParentId2;//检测选项内容二级分类id
	private String detectTypeParentId1Name;
	private String detectTypeParentId2Name;
	
	public String getDetectTypeParentId1Name() {
		return detectTypeParentId1Name;
	}
	public void setDetectTypeParentId1Name(String detectTypeParentId1Name) {
		this.detectTypeParentId1Name = detectTypeParentId1Name;
	}
	public String getDetectTypeParentId2Name() {
		return detectTypeParentId2Name;
	}
	public void setDetectTypeParentId2Name(String detectTypeParentId2Name) {
		this.detectTypeParentId2Name = detectTypeParentId2Name;
	}
	public static Map<String,String> contentMap = new LinkedHashMap<String, String>();
	
	public static void initContentMap(int afterSaleDetectTypeId,int parentId1) {
		contentMap.clear();
		DbOperation dbOp=new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			AfterSaleDetectTypeDetailBean asBean = afService.getAfterSaleDetectTypeDetail(" after_sale_detect_type_id=" + afterSaleDetectTypeId + " and parent_id1=" + parentId1);
			if (asBean != null) {
				String[] content = asBean.getContent().split("\n");
				for(String con : content){
					contentMap.put(con, con);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAfterSaleDetectTypeId() {
		return afterSaleDetectTypeId;
	}
	public void setAfterSaleDetectTypeId(int afterSaleDetectTypeId) {
		this.afterSaleDetectTypeId = afterSaleDetectTypeId;
	}
	public int getParentId1() {
		return parentId1;
	}
	public void setParentId1(int parentId1) {
		this.parentId1 = parentId1;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAfterSaleDetectTypeName() {
		return afterSaleDetectTypeName;
	}
	public void setAfterSaleDetectTypeName(String afterSaleDetectTypeName) {
		this.afterSaleDetectTypeName = afterSaleDetectTypeName;
	}
	public String getParentId1Name() {
		return parentId1Name;
	}
	public void setParentId1Name(String parentId1Name) {
		this.parentId1Name = parentId1Name;
	}
	public int getDetectTypeParentId1() {
		return detectTypeParentId1;
	}
	public void setDetectTypeParentId1(int detectTypeParentId1) {
		this.detectTypeParentId1 = detectTypeParentId1;
	}
	public int getDetectTypeParentId2() {
		return detectTypeParentId2;
	}
	public void setDetectTypeParentId2(int detectTypeParentId2) {
		this.detectTypeParentId2 = detectTypeParentId2;
	}

}
