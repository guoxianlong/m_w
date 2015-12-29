package mmb.stock.fitting.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能：采购配件入库单相关信息
 * @author lining
 *
 */
public class FittingBuyStockInBean {
    public int id;
    public int buyStockinId;
    public int type;//入库类型
    public int fittingType;//配件类别
    
    /**
     * 入库类型map
     */
    public static Map<Byte,String> typeMap = new HashMap<Byte, String>();
    
    /**
     * 配件类别map
     */
    public static Map<Byte,String> fittingTypeMap = new HashMap<Byte, String>();
    
    /**
     * 入库类型：采购入库
     */
    public final static byte TYPE1 = 1;
    
    /**
     *入库类型：维修返还入库
     */
    public final static byte TYPE2 = 2;
    
    /**
     * 配件类别：良品
     */
    public final static byte FITTING_TYPE1 = 1;
    
    /**
     * 配件类别：保修机残次
     */
    public final static byte FITTING_TYPE2 = 2;
    
    /**
     * 配件类别：非保修机残次
     */
    public final static byte FITTING_TYPE3 = 3;
    
    static{
    	typeMap.put(TYPE1, "采购入库");
    	typeMap.put(TYPE2, "维修返还入库");
    	
    	fittingTypeMap.put(FITTING_TYPE1, "良品");
    	fittingTypeMap.put(FITTING_TYPE2, "保修机残次");
    	fittingTypeMap.put(FITTING_TYPE3, "非保修机残次");
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuyStockinId() {
		return buyStockinId;
	}

	public void setBuyStockinId(int buyStockinId) {
		this.buyStockinId = buyStockinId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFittingType() {
		return fittingType;
	}

	public void setFittingType(int fittingType) {
		this.fittingType = fittingType;
	}	

}