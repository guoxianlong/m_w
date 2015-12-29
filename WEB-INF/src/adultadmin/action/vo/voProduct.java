/**
 * 
 */
package adultadmin.action.vo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import adultadmin.bean.XMLTransferable;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;
import cache.CatalogCache;
import cache.ProductCache;

/**
 * @author Bomb
 *  
 */
public class voProduct implements Serializable, XMLTransferable {
    private int id;

    private String name;
    
    private String inCargoWholeCode;//商品所属目的货位，为模型层使用
    
    private int count;//产品数量
    
    private String barcode;//商品条码

    private int stockoutCount;
    
    private int addCount;
    
    private int isExist;
    
    private String productCargoCode;
    
    private int fcStock;
    
    private int zcStock;
    
    private boolean price5Flag;
    //可发货总量
    private int canBeShipStock;
    //库存总数
    private int allStockCount;
  //记录每个产品对应所有库类型、库地区的库存总数，按一定顺序
    private List<Integer> stockCountList;
    public List<Integer> getStockCountList() {
		return stockCountList;
	}

	public void setStockCountList(List<Integer> stockCountList) {
		this.stockCountList = stockCountList;
	}
	//记录库类型、库区域下所有的地区数量与金额、总数量与总金额
	private List<Object> stockCountAndPriceList;
	public List<Object> getStockCountAndPriceList() {
		return stockCountAndPriceList;
	}

	public void setStockCountAndPriceList(List<Object> stockCountAndPriceList) {
		this.stockCountAndPriceList = stockCountAndPriceList;
	}

	public int getAllStockCount() {
		return allStockCount;
	}

	public void setAllStockCount(int allStockCount) {
		this.allStockCount = allStockCount;
	}

	public int getCanBeShipStock() {
		return canBeShipStock;
	}

	public void setCanBeShipStock(int canBeShipStock) {
		this.canBeShipStock = canBeShipStock;
	}

	public boolean isPrice5Flag() {
		return price5Flag;
	}

	public void setPrice5Flag(boolean price5Flag) {
		this.price5Flag = price5Flag;
	}

	public int getFcStock() {
		return fcStock;
	}

	public void setFcStock(int fcStock) {
		this.fcStock = fcStock;
	}

	public int getZcStock() {
		return zcStock;
	}

	public void setZcStock(int zcStock) {
		this.zcStock = zcStock;
	}

	public String getProductCargoCode() {
		return productCargoCode;
	}

	public void setProductCargoCode(String productCargoCode) {
		this.productCargoCode = productCargoCode;
	}

	public int getIsExist() {
		return isExist;
	}

	public void setIsExist(int isExist) {
		this.isExist = isExist;
	}

	public int getStockoutCount() {
		return stockoutCount;
	}

	public void setStockoutCount(int stockoutCount) {
		this.stockoutCount = stockoutCount;
	}

	public int getAddCount() {
		return addCount;
	}

	public void setAddCount(int addCount) {
		this.addCount = addCount;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
     * 小店价格
     */
    private float price;

    /**
     * 市场价
     */
    private float price2; // 市场价

    /**
     * 批发价
     */
    private float price3; // 批发价

    /**
     * 原价
     */
    private float price4;

    /**
     * 库存价格（每次入库都要计算）
     */
    private float price5;

    private float deputizePrice; // 代理价格

    /**
     * 团购价格<br/>
     * 由管理员手工设置<br/>
     * 如果团购价格为0，则表示该商品不是团购商品<br/>
     */
    private float groupBuyPrice;

    private int parentId1;

    private int parentId2;

    private int parentId3;

    private int clickCount;

    private int buyCount;

    private int bjBuyCount;

    private int gdBuyCount;
    
    private int fhuoCount;
    
    private float dealMoney;
    
    private String brandName;
    
    private String supplierName;
    
    public float getDealMoney() {
		return dealMoney;
	}

	public void setDealMoney(float dealMoney) {
		this.dealMoney = dealMoney;
	}

	public int getFhuoCount() {
		return fhuoCount;
	}

	public void setFhuoCount(int fhuoCount) {
		this.fhuoCount = fhuoCount;
	}

	private int commentCount;

    private Timestamp createDatetime;

    private String createTime;

    /**
     * 第一位：商品制作完成标志：完成-1 | 未完成-0<br/>
     * 第二位：父、子商品标志：父-0 | 子-1<br/>
     */
    private int flag;

    private String intro;

    private String intro2;

    private String pic;

    private String pic2;

    private String pic3; // / 小小图，用于列表

    private int status; // 100表示正常，200表示缺货

    private String statusName;

    private int proxyId;

    private String proxyName;
    
    private String productLineName;  //产品线

    private String code; // 产品编号

    private String proxysName; // 候选代理

    private String remark; // 备注

    private String unit; // 计量单位

    private String oriname; // 产批原名（和供货商联系）

    private int stock; // 库存

    private int stockGd; //库存，广东

    public String images;

    public String[] imageFiles;

    private String guanggao; // 广告语

    private String gongxiao; // 产品功效

    private String shiyongrenqun; // 适用人群

    private String shiyongfangfa; // 使用方法

    private String zhuyishixiang; // 注意事项

    private String tebietishi; // 特别提示

    private String baozhuangzhongliang; // 包装重量

    private String baozhuangdaxiao; // 包装大小

    private String chanpinzhongliang; // 产品重量

    private String chanpinchicun; // 产品尺寸

    private String chanpinchengfen; // 产品成分

    private String changshang; // 生产厂商

    private String baozhiqi; // 保质期

    private String chucangfangfa; // 储藏方法

    private String pizhunwenhao; // 批准文号

    private String changshangjieshao; // 厂商介绍

    private String fuwuchengnuo; // 服务承诺

    private int rank; // 产品等级

    private int displayOrder; // 显示顺序

    private int topOrder; // TOP100显示顺序

    private int stockStandardBj;

    private int stockStandardGd;

    private int stockLineBj;

    private int stockLineGd;

    private int stockStatus;

    /**
     * 产品品牌
     */
    private int brand;

    /**
     * 北京库存天数
     */
    private int stockDayBj;
    /**
     * 广东库存天数
     */
    private int stockDayGd;

    /**
     * 北京 烂货 数量
     */
    private int stockBjBad;
    /**
     * 北京 返修 数量
     */
    private int stockBjRepair;
    /**
     * 广东 烂货 数量
     */
    private int stockGdBad;
    /**
     * 广东 返修 数量
     */
    private int stockGdRepair;

    /**
     * 该产品的库存列表，所有地区的，所有库的<br/>
     * 需要单独查询出来，放在这个voProduct实例中
     */
    private List psList;
    
    private List cargoPSList;

    public voOrderProduct orderProduct;
    
    //最后一次销售时间
    private String lastSalesTime;
    //最后一次采购时间
    private String lastStockTime;
    
    
    public String getLastSalesTime() {
		return lastSalesTime;
	}

	public void setLastSalesTime(String lastSalesTime) {
		this.lastSalesTime = lastSalesTime;
	}

	public String getLastStockTime() {
		return lastStockTime;
	}

	public void setLastStockTime(String lastStockTime) {
		this.lastStockTime = lastStockTime;
	}

    /**
     * 说明：产品附加属性
     */
    private voProductProperty productProperty;

    public voProductProperty getProductProperty() {
		return productProperty;
	}

	public void setProductProperty(voProductProperty productProperty) {
		this.productProperty = productProperty;
	}

	/**
     * 是否套装。默认为0，不是；1为是
     */
    private int isPackage;

    /**
     * 是否显示套装内容。 默认为1，显示； 0为不显示
     */
    private int showPackage;

    /**
     * 是否有赠品。默认为0，没有；1为有
     */
    private int hasPresent;
    /**
     * 北京进货周期
     */
    private String bjStockin;
    /**
     * 广东进货周期
     */
    private String gdStockin;

    /**
     * 货位系统中 已申请的货位最大存储量（北京）
     */
    private int maxStockCountBj;
    /**
     * 货位系统中 已申请的货位最大存储量（广东）
     */
    private int maxStockCountGd;
    /**
     * 货位系统中 已申请的货位实际储量（北京）
     */
    private int stockCountBj;
    /**
     * 货位系统中 已申请的货位实际储量（广东）
     */
    private int stockCountGd;
    
    /**
     * 该商品的条形码
     */
    private ProductBarcodeVO productBarcodeVO;
    
    //套装名称
    private String parentName;
    
    //套装数量
    private int parentCount;
    
    //套装ID
    private String parentId;

    public ProductBarcodeVO getProductBarcodeVO() {
		return productBarcodeVO;
	}

	public void setProductBarcodeVO(ProductBarcodeVO productBarcodeVO) {
		this.productBarcodeVO = productBarcodeVO;
	}

	public voCatalog getParent1() {
        return CatalogCache.getCatalog(parentId1);
    }

    public voCatalog getParent2() {
        return CatalogCache.getCatalog(parentId2);
    }
    
    public voCatalog getParent3() {
        return CatalogCache.getCatalog(parentId3);
    }

    public int getTopOrder() {
        return topOrder;
    }

    public void setTopOrder(int topOrder) {
        this.topOrder = topOrder;
    }

    /**
     * @return Returns the fuwuchengnuo.
     */
    public String getFuwuchengnuo() {
        return fuwuchengnuo;
    }

    /**
     * @param fuwuchengnuo
     *            The fuwuchengnuo to set.
     */
    public void setFuwuchengnuo(String fuwuchengnuo) {
        this.fuwuchengnuo = fuwuchengnuo;
    }

    /**
     * @return Returns the baozhuangdaxiao.
     */
    public String getBaozhuangdaxiao() {
        return baozhuangdaxiao;
    }

    /**
     * @param baozhuangdaxiao
     *            The baozhuangdaxiao to set.
     */
    public void setBaozhuangdaxiao(String baozhuangdaxiao) {
        this.baozhuangdaxiao = baozhuangdaxiao;
    }

    /**
     * @return Returns the baozhiqi.
     */
    public String getBaozhiqi() {
        return baozhiqi;
    }

    /**
     * @param baozhiqi
     *            The baozhiqi to set.
     */
    public void setBaozhiqi(String baozhiqi) {
        this.baozhiqi = baozhiqi;
    }

    /**
     * @return Returns the baozhuangzhongliang.
     */
    public String getBaozhuangzhongliang() {
        return baozhuangzhongliang;
    }

    /**
     * @param baozhuangzhongliang
     *            The baozhuangzhongliang to set.
     */
    public void setBaozhuangzhongliang(String baozhuangzhongliang) {
        this.baozhuangzhongliang = baozhuangzhongliang;
    }

    /**
     * @return Returns the changshang.
     */
    public String getChangshang() {
        return changshang;
    }

    /**
     * @param changshang
     *            The changshang to set.
     */
    public void setChangshang(String changshang) {
        this.changshang = changshang;
    }

    /**
     * @return Returns the changshangjieshao.
     */
    public String getChangshangjieshao() {
        return changshangjieshao;
    }

    /**
     * @param changshangjieshao
     *            The changshangjieshao to set.
     */
    public void setChangshangjieshao(String changshangjieshao) {
        this.changshangjieshao = changshangjieshao;
    }

    /**
     * @return Returns the chanpinchengfen.
     */
    public String getChanpinchengfen() {
        return chanpinchengfen;
    }

    /**
     * @param chanpinchengfen
     *            The chanpinchengfen to set.
     */
    public void setChanpinchengfen(String chanpinchengfen) {
        this.chanpinchengfen = chanpinchengfen;
    }

    /**
     * @return Returns the chucangfangfa.
     */
    public String getChucangfangfa() {
        return chucangfangfa;
    }

    /**
     * @param chucangfangfa
     *            The chucangfangfa to set.
     */
    public void setChucangfangfa(String chucangfangfa) {
        this.chucangfangfa = chucangfangfa;
    }

    /**
     * @return Returns the gongxiao.
     */
    public String getGongxiao() {
        return gongxiao;
    }

    /**
     * @param gongxiao
     *            The gongxiao to set.
     */
    public void setGongxiao(String gongxiao) {
        this.gongxiao = gongxiao;
    }

    /**
     * @return Returns the guanggao.
     */
    public String getGuanggao() {
        return guanggao;
    }

    /**
     * @param guanggao
     *            The guanggao to set.
     */
    public void setGuanggao(String guanggao) {
        this.guanggao = guanggao;
    }

    /**
     * @return Returns the pizhunwenhao.
     */
    public String getPizhunwenhao() {
        return pizhunwenhao;
    }

    /**
     * @param pizhunwenhao
     *            The pizhunwenhao to set.
     */
    public void setPizhunwenhao(String pizhunwenhao) {
        this.pizhunwenhao = pizhunwenhao;
    }

    /**
     * @return Returns the shiyongfangfa.
     */
    public String getShiyongfangfa() {
        return shiyongfangfa;
    }

    /**
     * @param shiyongfangfa
     *            The shiyongfangfa to set.
     */
    public void setShiyongfangfa(String shiyongfangfa) {
        this.shiyongfangfa = shiyongfangfa;
    }

    /**
     * @return Returns the shiyongrenqun.
     */
    public String getShiyongrenqun() {
        return shiyongrenqun;
    }

    /**
     * @param shiyongrenqun
     *            The shiyongrenqun to set.
     */
    public void setShiyongrenqun(String shiyongrenqun) {
        this.shiyongrenqun = shiyongrenqun;
    }

    /**
     * @return Returns the tebietishi.
     */
    public String getTebietishi() {
        return tebietishi;
    }

    /**
     * @param tebietishi
     *            The tebietishi to set.
     */
    public void setTebietishi(String tebietishi) {
        this.tebietishi = tebietishi;
    }

    /**
     * @return Returns the zhuyishixiang.
     */
    public String getZhuyishixiang() {
        return zhuyishixiang;
    }

    /**
     * @param zhuyishixiang
     *            The zhuyishixiang to set.
     */
    public void setZhuyishixiang(String zhuyishixiang) {
        this.zhuyishixiang = zhuyishixiang;
    }

    public String getFullPic() {
        return IConstants.RESOURCE_PRODUCT_IMAGE + pic;
    }

    public String getFullPic2() {
        return IConstants.RESOURCE_PRODUCT_IMAGE + pic2;
    }

    public String getFullPic3() {
        return IConstants.RESOURCE_PRODUCT_IMAGE + pic3;
    }

    /**
     * @return Returns the stock.
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock
     *            The stock to set.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return Returns the buyCount.
     */
    public int getBuyCount() {
        return buyCount;
    }

    /**
     * @param buyCount
     *            The buyCount to set.
     */
    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    /**
     * @return Returns the clickCount.
     */
    public int getClickCount() {
        return clickCount;
    }

    /**
     * @param clickCount
     *            The clickCount to set.
     */
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    /**
     * @return Returns the commentCount.
     */
    public int getCommentCount() {
        return commentCount;
    }

    /**
     * @param commentCount
     *            The commentCount to set.
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * @return Returns the createDatetime.
     */
    public Timestamp getCreateDatetime() {
        return createDatetime;
    }

    /**
     * @param createDatetime
     *            The createDatetime to set.
     */
    public void setCreateDatetime(Timestamp createDatetime) {
        this.createDatetime = createDatetime;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the parentId1.
     */
    public int getParentId1() {
        return parentId1;
    }

    /**
     * @param parentId1
     *            The parentId1 to set.
     */
    public void setParentId1(int parentId1) {
        this.parentId1 = parentId1;
    }

    /**
     * @return Returns the parentId2.
     */
    public int getParentId2() {
        return parentId2;
    }

    /**
     * @param parentId2
     *            The parentId2 to set.
     */
    public void setParentId2(int parentId2) {
        this.parentId2 = parentId2;
    }

    /**
     * @return Returns the parentId3.
     */
    public int getParentId3() {
        return parentId3;
    }

    /**
     * @param parentId3
     *            The parentId3 to set.
     */
    public void setParentId3(int parentId3) {
        this.parentId3 = parentId3;
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2007-11-23
     * 
     * 说明：获取小店价格
     * 
     * 参数及返回值说明：
     * 
     * @return
     */
    public float getPrice() {
        return price;
    }

    /**
     * @param price
     *            The price to set.
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * @return Returns the flag.
     */
    public int getFlag() {
        return flag;
    }

    /**
     * @param flag
     *            The flag to set.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * @return Returns the intro.
     */
    public String getIntro() {
        return intro;
    }

    /**
     * @param intro
     *            The intro to set.
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /**
     * @return Returns the pic.
     */
    public String getPic() {
        return pic;
    }

    /**
     * @param pic
     *            The pic to set.
     */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     * @return Returns the pic2.
     */
    public String getPic2() {
        return pic2;
    }

    /**
     * @param pic2
     *            The pic2 to set.
     */
    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return Returns the statusName.
     */
    public String getStatusName() {
    	if(StringUtil.convertNull(this.statusName).equals("")){
    		this.statusName = ProductCache.getProductStatusName(status);
    	}
        return this.statusName;
    }

    /**
     * @param statusName
     *            The statusName to set.
     */
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    /**
     * @return Returns the proxyId.
     */
    public int getProxyId() {
        return proxyId;
    }

    /**
     * @param proxyId
     *            The proxyId to set.
     */
    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }

    /**
     * @return Returns the proxyName.
     */
    public String getProxyName() {
        return proxyName;
    }

    /**
     * @param proxyName
     *            The proxyName to set.
     */
    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2007-11-23
     * 
     * 说明：获取市场价
     * 
     * 参数及返回值说明：
     * 
     * @return
     */
    public float getPrice2() {
        return price2;
    }

    /**
     * @param price2
     *            The price2 to set.
     */
    public void setPrice2(float price2) {
        this.price2 = price2;
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2007-11-23
     * 
     * 说明：获取 批发价
     * 
     * 参数及返回值说明：
     * 
     * @return
     */
    public float getPrice3() {
        return price3;
    }

    /**
     * @param price3
     *            The price3 to set.
     */
    public void setPrice3(float price3) {
        this.price3 = price3;
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return Returns the proxysName.
     */
    public String getProxysName() {
        return proxysName;
    }

    /**
     * @param proxysName
     *            The proxysName to set.
     */
    public void setProxysName(String proxysName) {
        this.proxysName = proxysName;
    }

    /**
     * @return Returns the remark.
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     *            The remark to set.
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     *            The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return Returns the intro2.
     */
    public String getIntro2() {
        return intro2;
    }

    /**
     * @param intro2
     *            The intro2 to set.
     */
    public void setIntro2(String intro2) {
        this.intro2 = intro2;
    }

    /**
     * @return Returns the oriname.
     */
    public String getOriname() {
        return oriname;
    }

    /**
     * @param oriname
     *            The oriname to set.
     */
    public void setOriname(String oriname) {
        this.oriname = oriname;
    }

    /**
     * @return Returns the pic3.
     */
    public String getPic3() {
        return pic3;
    }

    /**
     * @param pic3
     *            The pic3 to set.
     */
    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public float getDeputizePrice() {
        return deputizePrice;
    }

    public void setDeputizePrice(float deputizePrice) {
        this.deputizePrice = deputizePrice;
    }

    public String getImageUrl(String fileName) {
        return IConstants.RESOURCE_PRODUCT_IMAGE + fileName;
    }

    /**
     * @param images
     *            The images to set.
     */
    public void setImages(String images) {
        this.images = images;
    }

    /**
     * @return Returns the images.
     */
    public String getImages() {
        return images;
    }

    /**
     * @param imageFiles
     *            The imageFiles to set.
     */
    public void setImageFiles(String[] imageFiles) {
        this.imageFiles = imageFiles;
    }

    /**
     * @return Returns the imageFiles.
     */
    public String[] getImageFiles() {
        if (imageFiles != null) {
            return imageFiles;
        }
        if(images!=null){
	        imageFiles = images.split(";");
        } 
	    return imageFiles;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getStockGd() {
        return stockGd;
    }

    public void setStockGd(int stockGd) {
        this.stockGd = stockGd;
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2007-11-23
     * 
     * 说明：获取 原价
     * 
     * 参数及返回值说明：
     * 
     * @return
     */
    public float getPrice4() {
        return price4;
    }

    public void setPrice4(float price4) {
        this.price4 = price4;
    }

    public int getStockLineBj() {
        return stockLineBj;
    }

    public void setStockLineBj(int stockLineBj) {
        this.stockLineBj = stockLineBj;
    }

    public int getStockLineGd() {
        return stockLineGd;
    }

    public void setStockLineGd(int stockLineGd) {
        this.stockLineGd = stockLineGd;
    }

    public int getStockStandardBj() {
        return stockStandardBj;
    }

    public void setStockStandardBj(int stockStandardBj) {
        this.stockStandardBj = stockStandardBj;
    }

    public int getStockStandardGd() {
        return stockStandardGd;
    }

    public void setStockStandardGd(int stockStandardGd) {
        this.stockStandardGd = stockStandardGd;
    }

    public int getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(int stockStatus) {
        this.stockStatus = stockStatus;
    }

    public int getIsPackage() {
        return isPackage;
    }

    public void setIsPackage(int isPackage) {
        this.isPackage = isPackage;
    }

    public int getBjBuyCount() {
        return bjBuyCount;
    }

    public void setBjBuyCount(int bjBuyCount) {
        this.bjBuyCount = bjBuyCount;
    }

    public int getGdBuyCount() {
        return gdBuyCount;
    }

    public void setGdBuyCount(int gdBuyCount) {
        this.gdBuyCount = gdBuyCount;
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2008-1-24
     * 
     * 说明：获取产品品牌
     * 
     * 参数及返回值说明：
     * 
     * @return
     */
	public int getBrand() {
		return brand;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-1-24
	 * 
	 * 说明：设置产品品牌
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param brand
	 */
	public void setBrand(int brand) {
		this.brand = brand;
	}

	public int getShowPackage() {
		return showPackage;
	}

	public void setShowPackage(int showPackage) {
		this.showPackage = showPackage;
	}

	public String getChanpinchicun() {
		return chanpinchicun;
	}

	public void setChanpinchicun(String chanpinchicun) {
		this.chanpinchicun = chanpinchicun;
	}

	public String getChanpinzhongliang() {
		return chanpinzhongliang;
	}

	public void setChanpinzhongliang(String chanpinzhongliang) {
		this.chanpinzhongliang = chanpinzhongliang;
	}

	public String getBjStockin() {
		return bjStockin;
	}

	public void setBjStockin(String bjStockin) {
		this.bjStockin = bjStockin;
	}

	public String getGdStockin() {
		return gdStockin;
	}

	public void setGdStockin(String gdStockin) {
		this.gdStockin = gdStockin;
	}

	public int getHasPresent() {
		return hasPresent;
	}

	public void setHasPresent(int hasPresent) {
		this.hasPresent = hasPresent;
	}


	public int getStockDayBj() {
		return stockDayBj;
	}

	public void setStockDayBj(int stockDayBj) {
		this.stockDayBj = stockDayBj;
	}

	public int getStockDayGd() {
		return stockDayGd;
	}

	public void setStockDayGd(int stockDayGd) {
		this.stockDayGd = stockDayGd;
	}

	public int getStockBjBad() {
		return stockBjBad;
	}

	public void setStockBjBad(int stockBjBad) {
		this.stockBjBad = stockBjBad;
	}

	public int getStockBjRepair() {
		return stockBjRepair;
	}

	public void setStockBjRepair(int stockBjRepair) {
		this.stockBjRepair = stockBjRepair;
	}

	public int getStockGdBad() {
		return stockGdBad;
	}

	public void setStockGdBad(int stockGdBad) {
		this.stockGdBad = stockGdBad;
	}

	public int getStockGdRepair() {
		return stockGdRepair;
	}

	public void setStockGdRepair(int stockGdRepair) {
		this.stockGdRepair = stockGdRepair;
	}

	public int getMaxStockCountBj() {
		return maxStockCountBj;
	}

	public void setMaxStockCountBj(int maxStockCountBj) {
		this.maxStockCountBj = maxStockCountBj;
	}

	public int getMaxStockCountGd() {
		return maxStockCountGd;
	}

	public void setMaxStockCountGd(int maxStockCountGd) {
		this.maxStockCountGd = maxStockCountGd;
	}

	public int getStockCountBj() {
		return stockCountBj;
	}

	public void setStockCountBj(int stockCountBj) {
		this.stockCountBj = stockCountBj;
	}

	public int getStockCountGd() {
		return stockCountGd;
	}

	public void setStockCountGd(int stockCountGd) {
		this.stockCountGd = stockCountGd;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getProductLineName() {
		return productLineName;
	}

	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-2-26
	 * 
	 * 说明：获取包装重量，数字
	 * 
	 * 参数及返回值说明：
	 * 
	 * @return
	 */
	public int getBzzhongliang(){
		int weight = 0;
		try{
			weight = Integer.parseInt(this.baozhuangzhongliang.replaceAll("g", ""));
		} catch (Exception e){
			weight = 0;
		}
		return weight;
	}

	public float getPrice5() {
		return price5;
	}

	public void setPrice5(float price5) {
		this.price5 = price5;
	}

	public String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<product>");
		buf.append("<id>");
		buf.append(this.id);
		buf.append("</id>");
		buf.append("<name>");
		try {
			buf.append(URLEncoder.encode(this.name, "UTF-8").replaceAll("[+]", "%20"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		buf.append("</name>");
		buf.append("<price>");
		buf.append(NumberUtil.price(this.price));
		buf.append("</price>");
		buf.append("</product>");
		return buf.toString();
	}

	public List getPsList() {
		return psList;
	}

	public void setPsList(List psList) {
		this.psList = psList;
	}

	public int getStockAll(){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
					&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
					&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
					&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
				result += ps.getStock();
			}
		}
		return result;
	}

	public int getStock(int area){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area){
				if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
						&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
						&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
						&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
					result += ps.getStock();
				}
			}
		}
		return result;
	}

	public int getStockAllType(int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getType() == type){
				if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
						&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
						&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
						&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
					result += ps.getStock();
				}
			}
		}
		return result;
	}

	public int getStock(int area, int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area && ps.getType() == type){
				if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
						&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
						&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
						&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
					result += ps.getStock();
				}
			}
		}
		return result;
	}



	public int getLockCountAll(){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
					&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
					&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
					&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
				result += ps.getLockCount();
			}
		}
		return result;
	}

	public int getLockCount(int area){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area){
				if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
						&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
						&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
						&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
					result += ps.getLockCount();
				}
			}
		}
		return result;
	}

	public int getLockCountAllType(int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getType() == type){
				if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
						&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
						&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
						&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
					result += ps.getLockCount();
				}
			}
		}
		return result;
	}

	public int getLockCount(int area, int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area && ps.getType() == type){
				if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
						&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
						&& ps.getType() != ProductStockBean.STOCKTYPE_CUSTOMER
						&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
					result += ps.getLockCount();
				}
			}
		}
		return result;
	}

	public float getGroupBuyPrice() {
		return groupBuyPrice;
	}

	public void setGroupBuyPrice(float groupBuyPrice) {
		this.groupBuyPrice = groupBuyPrice;
	}
	
	public List getCargoPSList() {
		return cargoPSList;
	}

	public void setCargoPSList(List cargoPSList) {
		this.cargoPSList = cargoPSList;
	}
	
	public int getCargoStock(int storeType){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			if(cps.getCargoInfo().getStoreType() == storeType){
				result += cps.getStockCount();
			}
		}
		return result;
	}
	
	public int getCargoStock(int storeType,int areaId){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			if(cps.getCargoInfo().getStoreType() == storeType && cps.getCargoInfo().getAreaId() == areaId){
				result += cps.getStockCount();
			}
		}
		return result;
	}
	
	public int getCargoStockByType(int storeType,int stockType){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			if(cps.getCargoInfo().getStoreType() == storeType && cps.getCargoInfo().getStockType() == stockType){
				result += cps.getStockCount();
			}
		}
		return result;
	}
	
	public int getCargoLockStockByType(int storeType,int stockType){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			if(cps.getCargoInfo().getStoreType() == storeType && cps.getCargoInfo().getStockType() == stockType){
				result += cps.getStockLockCount();
			}
		}
		return result;
	}
	
	public int getCargoLockStock(int storeType){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			if(cps.getCargoInfo().getStoreType() == storeType){
				result += cps.getStockLockCount();
			}
		}
		return result;
	}
	
	public int getCargoStockAll(){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			result += cps.getStockCount();
		}
		return result;
	}
	
	public int getCargoLockStockAll(){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			result += cps.getStockLockCount();
		}
		return result;
	}

	public voOrderProduct getOrderProduct() {
		return orderProduct;
	}

	public void setOrderProduct(voOrderProduct orderProduct) {
		this.orderProduct = orderProduct;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}


	public String getInCargoWholeCode() {
		return inCargoWholeCode;
	}

	public void setInCargoWholeCode(String inCargoWholeCode) {
		this.inCargoWholeCode = inCargoWholeCode;
	}
	
	
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;
		if (otherObject == null)
			return false;
		if (getClass() != otherObject.getClass())
			return false;
		voProduct other = (voProduct) otherObject;
		return id==other.getId();
	}

	public int hashCode() {
		int result = 17;
		int idValue = id == 0 ? 0 : Integer.valueOf(id).hashCode();
		result = result * 37 + idValue;
		return result;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public int getParentCount() {
		return parentCount;
	}

	public void setParentCount(int parentCount) {
		this.parentCount = parentCount;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
 
}
