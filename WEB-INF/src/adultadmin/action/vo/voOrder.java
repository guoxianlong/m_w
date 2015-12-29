/**
 * 
 */
package adultadmin.action.vo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.ware.WareService;
import adultadmin.bean.OrderStatusBean;
import adultadmin.bean.Postage;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.StockOperationBean;
import adultadmin.util.PostageUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 *  
 */
public class voOrder implements Serializable {
	/**已到款*/
	public static final int STATUS3 = 3;
	/**已发货*/
	public static final int STATUS6 = 6;
	/**已退回*/
	public static final int STATUS11 = 11;
	/**已妥投*/
	public static final int STATUS14 = 14;
	
    public static int WEB = 1;

    public static int WAP = 0;

    private int id;

    private int userId;

    private int buyMode;

    private int deliverType;

    private int remitType;

    private String name;

    private String phone;

    private String phone2; // 第二个联系电话

    private String address;

    private String postcode;

    private int flag;
  //预约处理时间（发货处理状态为：发货失败，延迟发货，缺货已补货时可以设置  存于 user_order_pretreat_time表中）
    private String pretreatTime;

    private Timestamp createDatetime;

    private Timestamp confirmDatetime; // 一次确认时间

    private int status; // 状态，0 初始订单 1 正在确认 2 已经确认 3 正在汇总 4 已经汇总 5 订单完结

    private String code;

    private String statusName;

    private String operator;

    private String remark;

    private int admin; // 处理的人员

    private int sellerId; //下该订单的 销售人员的 ID

    private float price;

    private float discount; // 折扣

    private float dprice; // 打折后的价格

    private float productPrice; //	商品价格

    private float dproductPrice; //	商品折扣价格

    private float prepayDeliver; // 预付配送费

    private String cp; // 真实手机号

    private String phoneStatus; // 电话状态 *这四个是从remark用&&&分离得到

    private String remitDatetime; // 到款日期

    private String products; // 包含的产品名字

    private String packageNum; // 包裹单子

    private String deliverPrice; // 邮寄价格

    private int stockout; // 是否已经出货

    private int fr; // 提交订单时，该次登录的友链id

    private int agent; //	是否代理商

    //private float agentDiscount; // 代理商的折扣

    private String agentMark; //代理说明

    private String agentRemark; //代理留言、备注

    private int isOrderReimburse; //是否已经计算退货款

    private int isReimburse; //是否已经计算返还款

    private float realPay; //实际已到款

    private float postage; //邮费

    private int isOrder; //是进货订单还是退货订单，0代表进货订单，1代表退货订单

    private String images;
    
    private float price3;
    
    private String amazonCode;
    
    private String dangdangCode;
    
    private String jdCode;
    
    private String jdAdultCode;
    
    /**
     * 发货人
     */
    private String consigner;

    /**
     * 订单分类：
     * 0 - 北京订单
     * 9 - 无锡订单
     */
    private int areano;

    /**
     * 如果 改订单 的订单出货记录 在 待出货的时候被删除过，则该属性为 true
     */
    private boolean stockDeleted;

    private int prePayType;

    /**
     * 是否老用户 <br/>0. 否 <br/>1. 是 <br/>
     */
    private int isOlduser;

    /**
     * 原订单ID <br/>退换货使用
     */
    private int originOrderId;

    /**
     * 新订单ID <br/>退换货使用
     */
    private int newOrderId;

    /**
     * 原订单 <br/>退换货使用
     */
    private voOrder originOrder;

    /**
     * 新订单 <br/>退换货使用
     */
    private voOrder newOrder;

    /**
     * 已合并的订单，默认为空串
     */
    private String unitedOrders;

    private StockOperationBean stockOper;

    private OrderStockBean orderStock;

    private String dealDetail; //处理明细

    private int cpaStatus; //0表示还未能提成，1表示预计可以提成，2表示确实可以提成。

    private int cpaPay; //0表示未支付提成，1表示已支付提成。

    private int cpaBonus; //分成金额

    /**
     * 0 表示 未与客户电话联系确认发货
     * 1 表示 已经与客户电话联系确认发货
     */
    private int sellerCheckStatus;

    private String stockoutRemark;

    /**
     * <pre>
     * 1-手机
     * 2-数码
     * 3-衣服
     * 4-鞋
     * 5-食品
     * 6-塑料
     * 7-衣物
     * 8-香水
     * </pre>
     */
    private int productType;
    /**
     * <pre>
     * 1-邮局
     * 2-昱达
     * 3-宅急送
     * 4-圆通
     * </pre>
     */
    private int deliver;

    private int baozhuangzhongliang;

	/**
	 * <pre>
	 * 订单发货状态：
	 * 0 - 发货未处理
	 * 1 - 发货失败
	 * 2 - 发货成功
	 * 3 - 空白
	 * 4 - 缺货
	 * </pre>
	 */
    private int stockoutDeal;

    private int balanceStatus;

    private String lastOperTime;

    /**
     * 订单客户信息序号
     */
    private int serialNumber; 
    
    /**
     * 订单批次号
     */
    private int batchNum;
    /**
     * 系统匹配的快递公司
     */
    public String sysDeliver;
    
    /**
     * 缺货订单下次处理时间
     */
    private String nextLackDealDatetime;
    
    private int lackDealAdminId;
    
    private voUser lackDealAdmin;
    
    /**
     * 存放订单商品
     */
    private List productList;
    
    /**
     * 订单总价
     */
    private float totalPrice;
    
    /**
     * 商品总数
     */
    private int totalProductCount;
    
    public AuditPackageBean auditPakcageBean;
    
    public voOrderExtendInfo orderExtendInfo;
    
    public String userOrderCommentCode;

    //该订单在波次中的序号
    public String group_num;
    //分播墙编号
    public String groupCode;
    
    /**
	 * 订单状态
	 */
	public static HashMap<Integer,String> userOrderStatusMap = new LinkedHashMap<Integer,String>();
	
	public static void setStockCardTypeMap(WareService wareService) {
		List<OrderStatusBean> orderStatus = wareService.getOrderStatusList();
		for( OrderStatusBean osb : orderStatus ) {
			userOrderStatusMap.put(osb.getId(), osb.getName());
		}
	}
	
	static {
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		try{
			//加载所有的进销存卡片类型
			setStockCardTypeMap(wareService);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
	}
	
    public AuditPackageBean getAuditPakcageBean() {
		return auditPakcageBean;
	}

	public void setAuditPakcageBean(AuditPackageBean auditPakcageBean) {
		this.auditPakcageBean = auditPakcageBean;
	}

	public voOrderExtendInfo getOrderExtendInfo() {
		return orderExtendInfo;
	}

	public void setOrderExtendInfo(voOrderExtendInfo orderExtendInfo) {
		this.orderExtendInfo = orderExtendInfo;
	}

	public int getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(int batchNum) {
		this.batchNum = batchNum;
	}

	public String getSysDeliver() {
		return sysDeliver;
	}

	public void setSysDeliver(String sysDeliver) {
		this.sysDeliver = sysDeliver;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
     * @return Returns the cpaBonus.
     */
    public int getCpaBonus() {
        return cpaBonus;
    }

    /**
     * @param cpaBonus
     *            The cpaBonus to set.
     */
    public void setCpaBonus(int cpaBonus) {
        this.cpaBonus = cpaBonus;
    }

    /**
     * @return Returns the cpaPay.
     */
    public int getCpaPay() {
        return cpaPay;
    }

    /**
     * @param cpaPay
     *            The cpaPay to set.
     */
    public void setCpaPay(int cpaPay) {
        this.cpaPay = cpaPay;
    }

    /**
     * @return Returns the cpaStatus.
     */
    public int getCpaStatus() {
        return cpaStatus;
    }

    /**
     * @param cpaStatus
     *            The cpaStatus to set.
     */
    public void setCpaStatus(int cpaStatus) {
        this.cpaStatus = cpaStatus;
    }

    /**
     * @return Returns the dealDetail.
     */
    public String getDealDetail() {
        return dealDetail;
    }

    /**
     * @param dealDetail
     *            The dealDetail to set.
     */
    public void setDealDetail(String dealDetail) {
        this.dealDetail = dealDetail;
    }

    /**
     * @return Returns the stockOper.
     */
    public StockOperationBean getStockOper() {
        return stockOper;
    }

    /**
     * @param stockOper
     *            The stockOper to set.
     */
    public void setStockOper(StockOperationBean stockOper) {
        this.stockOper = stockOper;
    }

    /**
     * 订单的随机尾数，生成订单的时候添加，不能修改。
     */
    private float suffix;

    /**
     * 用户方便联系的时间 <br/>0. 随时、默认 <br/>1. 9点-18点 <br/>2. 18点-24点 <br/>3. 24点以后
     * <br/>
     */
    private int contactTime;

    private voUser user;

    //会员积分登记相关
    public int hasAddPoint; //是否已经根据订单给用户加上积分

    /**
     * 性别：0-未识别、1-男、2-女
     */
    public int gender; //性别

    public int flat; //平台，WEB/WAP

    /** WAP订单 **/
    public static int FLAT0 = 0;
    /** WEB订单 **/
    public static int FLAT1 = 1;
    /** 大Q官网订单 **/
    public static int FLAT2 = 2;
    /** 亚马逊订单 **/
    public static int FLAT3 = 3;
    /** 淘宝订单 **/
    public static int FLAT5 = 5;
    /**
     * 当当订单
     */
    public static final int FLAT6 = 6;
    /**
     * 京东订单
     */
    public static final int FLAT7 = 7;
    /** 兰亭订单 **/
    public static int FLAT8 = 8;
    /** 淘宝 3C **/
    public static int FLAT10 = 10;
    /** 苏州兰亭订单 **/
    public static int FLAT11 = 11;
    /** 京东（成人）订单 **/
    public static int FLAT12 = 12;
    
    public static Map<Integer, String> flatMap = new HashMap<Integer, String>();
    static {
    	flatMap.put(FLAT0, "WAP订单");
    	flatMap.put(FLAT1, "WEB订单");
    	flatMap.put(FLAT2, "大Q官网订单");
    	flatMap.put(FLAT3, "亚马逊订单");
    	flatMap.put(FLAT5, "淘宝订单");
    	flatMap.put(FLAT6, "当当订单");
    	flatMap.put(FLAT7, "京东订单");
    	flatMap.put(FLAT8, "兰亭订单");
    	flatMap.put(FLAT10, "淘宝 3C订单");
    	flatMap.put(FLAT12, "京东（成人）订单");
    	flatMap.put(FLAT11, "苏州兰亭订单");
    }
    public String webRemark; //备注

    public String email; //邮箱

    /**
     * 订单的种类
     * 根据订单中的商品，区分订单的种类
     * order_type
     */
    public int orderType;

    /**
     * @return Returns the hasAddPoint.
     */
    public int getHasAddPoint() {
        return hasAddPoint;
    }

    /**
     * @param hasAddPoint
     *            The hasAddPoint to set.
     */
    public void setHasAddPoint(int hasAddPoint) {
        this.hasAddPoint = hasAddPoint;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        //防止为空加入数据库
        if (email == null) {
            return "";
        }
        return email;
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the flat.
     */
    public int getFlat() {
        return flat;
    }

    /**
     * @param flat
     *            The flat to set.
     */
    public void setFlat(int flat) {
        this.flat = flat;
    }

    /**
     * @return Returns the gender.
     */
    public int getGender() {
        return gender;
    }

    /**
     * @param gender
     *            The gender to set.
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * @return Returns the webRemark.
     */
    public String getWebRemark() {
        //防止为空加入数据库
        if (webRemark == null) {
            return "";
        }
        return webRemark;
    }

    /**
     * @param webRemark
     *            The webRemark to set.
     */
    public void setWebRemark(String webRemark) {
        this.webRemark = webRemark;
    }

    /**
     * @return Returns the user.
     */
//    public voUser getUser() {
//        if (user != null) {
//            return user;
//        }
//        if (userId <= 0) {
//            return new voUser();
//        }
//
//        IUserService service = ServiceFactory.createUserService();
//        user = service.getUser("id = " + userId);
//        return user;
//    }

    /**
     * @param user
     *            The user to set.
     */
    public void setUser(voUser user) {
        this.user = user;
    }

    /**
     * @return Returns the agentMark.
     */
    public String getAgentMark() {
        return agentMark;
    }

    /**
     * @param agentMark
     *            The agentMark to set.
     */
    public void setAgentMark(String agentMark) {
        this.agentMark = agentMark;
    }

    /**
     * @return Returns the agentRemark.
     */
    public String getAgentRemark() {
        return agentRemark;
    }

    /**
     * @param agentRemark
     *            The agentRemark to set.
     */
    public void setAgentRemark(String agentRemark) {
        this.agentRemark = agentRemark;
    }

    /**
     * @return Returns the isOrder.
     */
    public int getIsOrder() {
        return isOrder;
    }

    /**
     * @param isOrder
     *            The isOrder to set.
     */
    public void setIsOrder(int isOrder) {
        this.isOrder = isOrder;
    }

    /**
     * @return Returns the isOrderReimburse.
     */
    public int getIsOrderReimburse() {
        return isOrderReimburse;
    }

    /**
     * @param isOrderReimburse
     *            The isOrderReimburse to set.
     */
    public void setIsOrderReimburse(int isOrderReimburse) {
        this.isOrderReimburse = isOrderReimburse;
    }

    /**
     * @return Returns the isReimburse.
     */
    public int getIsReimburse() {
        return isReimburse;
    }

    /**
     * @param isReimburse
     *            The isReimburse to set.
     */
    public void setIsReimburse(int isReimburse) {
        this.isReimburse = isReimburse;
    }

    /**
     * @return Returns the postage.
     */
    public float getPostage() {
        return postage;
    }

    /**
     * @param postage
     *            The postage to set.
     */
    public void setPostage(float postage) {
        this.postage = postage;
    }

    /**
     * @return Returns the realPay.
     */
    public float getRealPay() {
        return realPay;
    }

    /**
     * @param realPay
     *            The realPay to set.
     */
    public void setRealPay(float realPay) {
        this.realPay = realPay;
    }

    /**
     * @return Returns the products.
     */
    public String getProducts() {
        return products;
    }

    /**
     * @param products
     *            The products to set.
     */
    public void setProducts(String products) {
        this.products = products;
    }

    /**
     * @return Returns the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *            The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return Returns the buymode.
     */
    public int getBuyMode() {
        return buyMode;
    }
    public String getBuyModeName() {
        return (String) buyModeMap.get(buyMode+"");
    }

    /**
     * @param buymode
     *            The buymode to set.
     */
    public void setBuyMode(int buymode) {
        this.buyMode = buymode;
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
    

    public String getPretreatTime() {
		return pretreatTime;
	}

	public void setPretreatTime(String pretreatTime) {
		this.pretreatTime = pretreatTime;
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
     * @return Returns the phone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            The phone to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return Returns the postcode.
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @param postcode
     *            The postcode to set.
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * @return Returns the userId.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return Returns the confirmDatetime.
     */
    public Timestamp getConfirmDatetime() {
        return confirmDatetime;
    }

    /**
     * @param confirmDatetime
     *            The confirmDatetime to set.
     */
    public void setConfirmDatetime(Timestamp confirmDatetime) {
        this.confirmDatetime = confirmDatetime;
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
     * @return Returns the statusName.
     */
    public String getStatusName() {
        return statusName;
    }

    /**
     * @param statusName
     *            The statusName to set.
     */
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    /**
     * @return Returns the remark.
     */
    public String getRemark() {
        return remark;
    }

    public String getRemarks() {
        StringBuffer sb = new StringBuffer(256);
        if(remark == null){
        	sb.append("");
        } else {
        	sb.append(remark);
        }
        sb.append("&&&");
        if(phoneStatus == null){
        	sb.append("");
        } else {
        	sb.append(phoneStatus);
        }
        sb.append("&&&");
        if(remitDatetime == null){
        	sb.append("");
        } else {
        	sb.append(remitDatetime);
        }
        sb.append("&&&");
        if(packageNum == null){
        	sb.append("");
        } else {
        	sb.append(packageNum);
        }
        sb.append("&&&");
        if(deliverPrice == null){
        	sb.append("");
        } else {
        	sb.append(deliverPrice);
        }
        return sb.toString();
    }

    /**
     * @param remark
     *            The remark to set.
     */
    public void setRemark(String remark) {
        if (remark == null) {
            this.remark = "";
            return;
        }
        String[] s = remark.split("&&&");
        if (s.length > 0)
            this.remark = s[0];
        else
            this.remark = "";

        if (s.length > 1)
            phoneStatus = s[1];
        else
            phoneStatus = "";

        if (s.length > 2)
            remitDatetime = s[2];
        else
            remitDatetime = "";

        if (s.length > 3)
            packageNum = s[3];
        else
            packageNum = "";

        if (s.length > 4)
            deliverPrice = s[4];
        else
            deliverPrice = "";
    }

    public void setRemark(String remark, String phoneStatus,
            String remitDatetime, String packageNum, String deliverPrice) {
        this.remark = remark;
        this.phoneStatus = phoneStatus;
        this.remitDatetime = remitDatetime;
        this.packageNum = packageNum;
        this.deliverPrice = deliverPrice;
    }

    /**
     * @return Returns the admin.
     */
    public int getAdmin() {
        return admin;
    }

    /**
     * @param admin
     *            The admin to set.
     */
    public void setAdmin(int admin) {
        this.admin = admin;
    }

    /**
     * @return Returns the price.
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
     * @return Returns the discount.
     */
    public float getDiscount() {
        return discount;
    }

    /**
     * @param discount
     *            The discount to set.
     */
    public void setDiscount(float discount) {
        this.discount = discount;
    }

    /**
     * @return Returns the dprice.
     */
    public float getDprice() {
//        if (dprice == 0) {
//            float tail = price - (int) (price / 1);
//            dprice = ((int) (price / 1) - postage) * discount;
//            if (dprice - (int) (dprice / 1) < 0.5) {
//                dprice = (int) (dprice / 1);
//            } else {
//                dprice = (int) (dprice / 1 + 1);
//            }
//            dprice += postage + tail;
//        }
        return dprice;
    }

    /**
     * @param dprice
     *            The dprice to set.
     */
    public void setDprice(float dprice) {
        this.dprice = dprice;
    }

    /**
     * @return Returns the deliverType.
     */
    public int getDeliverType() {
        return deliverType;
    }

    /**
     * @param deliverType
     *            The deliverType to set.
     */
    public void setDeliverType(int deliverType) {
        this.deliverType = deliverType;
    }

    /**
     * @return Returns the remitType.
     */
    public int getRemitType() {
        return remitType;
    }

    /**
     * @param remitType
     *            The remitType to set.
     */
    public void setRemitType(int remitType) {
        this.remitType = remitType;
    }

    /**
     * @return Returns the cp.
     */
    public String getCp() {
        return cp;
    }

    /**
     * @param cp
     *            The cp to set.
     */
    public void setCp(String cp) {
        this.cp = cp;
    }

    /**
     * @return Returns the phoneStatus.
     */
    public String getPhoneStatus() {
        return phoneStatus;
    }

    /**
     * @param phoneStatus
     *            The phoneStatus to set.
     */
    public void setPhoneStatus(String phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    /**
     * @return Returns the remitDatetime.
     */
    public String getRemitDatetime() {
        return remitDatetime;
    }

    /**
     * @param remitDatetime
     *            The remitDatetime to set.
     */
    public void setRemitDatetime(String remitDatetime) {
        this.remitDatetime = remitDatetime;
    }

    /**
     * @return Returns the packageNum.
     */
    public String getPackageNum() {
        return packageNum;
    }

    /**
     * @param packageNum
     *            The packageNum to set.
     */
    public void setPackageNum(String packageNum) {
        this.packageNum = packageNum;
    }

    /**
     * @return Returns the deliverPrice.
     */
    public String getDeliverPrice() {
        return deliverPrice;
    }

    /**
     * @param deliverPrice
     *            The deliverPrice to set.
     */
    public void setDeliverPrice(String deliverPrice) {
        this.deliverPrice = deliverPrice;
    }

    /**
     * @return Returns the stockout.
     */
    public int getStockout() {
        return stockout;
    }

    /**
     * @param stockout
     *            The stockout to set.
     */
    public void setStockout(int stockout) {
        this.stockout = stockout;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public float getPrepayDeliver() {
        return prepayDeliver;
    }

    public void setPrepayDeliver(float prepayDeliver) {
        this.prepayDeliver = prepayDeliver;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getFr() {
        return fr;
    }

    public void setFr(int fr) {
        this.fr = fr;
    }

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public int getAreano() {
        return areano;
    }

    public void setAreano(int areano) {
        this.areano = areano;
    }

    public int getPrePayType() {
        return prePayType;
    }

    public void setPrePayType(int prePayType) {
        this.prePayType = prePayType;
    }

    public int getIsOlduser() {
        return isOlduser;
    }

    public void setIsOlduser(int isOlduser) {
        this.isOlduser = isOlduser;
    }

    public float getProductPrice() {
        if (productPrice == 0) {
            productPrice = ((int) (price / 1) - postage);
        }
        return productPrice;
    }

    public void setProductPrice(float productPrice) {
        this.productPrice = productPrice;
    }

    public float getDproductPrice() {
        if (dproductPrice == 0) {
            dproductPrice = ((int) (price / 1) - postage) * discount;
            if (dproductPrice - (int) (dproductPrice / 1) < 0.5) {
                dproductPrice = (int) (dproductPrice / 1);
            } else {
                dproductPrice = (int) (dproductPrice / 1 + 1);
            }
        }
        return dproductPrice;
    }

    public void setDproductPrice(float dproductPrice) {
        this.dproductPrice = dproductPrice;
    }

    public float getSuffix() {
        return suffix;
    }

    public void setSuffix(float suffix) {
        this.suffix = suffix;
    }

    public int getContactTime() {
        return contactTime;
    }

    public void setContactTime(int contactTime) {
        this.contactTime = contactTime;
    }

    public String getUnitedOrders() {
        return unitedOrders;
    }

    public void setUnitedOrders(String unitedOrders) {
        this.unitedOrders = unitedOrders;
    }

    public int getNewOrderId() {
        return newOrderId;
    }

    public void setNewOrderId(int newOrderId) {
        this.newOrderId = newOrderId;
    }

    public int getOriginOrderId() {
        return originOrderId;
    }

    public void setOriginOrderId(int originOrderId) {
        this.originOrderId = originOrderId;
    }

    public voOrder getNewOrder() {
        return newOrder;
    }

    public void setNewOrder(voOrder newOrder) {
        this.newOrder = newOrder;
    }

    public voOrder getOriginOrder() {
        return originOrder;
    }

    public void setOriginOrder(voOrder originOrder) {
        this.originOrder = originOrder;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

	public int getSellerCheckStatus() {
		return sellerCheckStatus;
	}

	public void setSellerCheckStatus(int sellerCheckStatus) {
		this.sellerCheckStatus = sellerCheckStatus;
	}

	public String getAreaName(){
		String areaName = null;
		switch(this.areano){
			case 0:
				areaName = "北京";
				break;
			case 9:
				areaName = "无锡";
				break;
			default:
				areaName = "北京";
		}
		return areaName;
	}

	public boolean isStockDeleted() {
		return stockDeleted;
	}

	public void setStockDeleted(boolean stockDeleted) {
		this.stockDeleted = stockDeleted;
	}

	public String getStockoutRemark() {
		return stockoutRemark;
	}

	public void setStockoutRemark(String stockoutRemark) {
		this.stockoutRemark = stockoutRemark;
	}

	public String getStockoutRemarkPrint() {
		StringBuilder sb = new StringBuilder(this.stockoutRemark);
		String temp = "\r\n";
		for(int i=0; i<sb.length(); i++){
			if(i!=0 && i%6==0){
				sb.insert(i, temp);
				i += temp.length();
			}
		}
		return sb.toString();
	}

	public String getConsigner() {
		return consigner;
	}

	public void setConsigner(String consigner) {
		this.consigner = consigner;
	}

	public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	/**
	 * 产品分类信息
	 */
	public static Map productTypeMap = new LinkedHashMap();
	static{
		productTypeMap.put(String.valueOf(1), "手机");
		productTypeMap.put(String.valueOf(2), "保健品");
		productTypeMap.put(String.valueOf(3), "服装");
		productTypeMap.put(String.valueOf(4), "鞋");
		productTypeMap.put(String.valueOf(5), "玩具");
		productTypeMap.put(String.valueOf(6), "塑料");
		productTypeMap.put(String.valueOf(7), "饰品");
		productTypeMap.put(String.valueOf(8), "香水");
		productTypeMap.put(String.valueOf(9), "礼品");
		productTypeMap.put(String.valueOf(10), "电子");
		productTypeMap.put(String.valueOf(11), "电脑");
		productTypeMap.put(String.valueOf(12), "包");
		productTypeMap.put(String.valueOf(13), "护肤品");
		productTypeMap.put(String.valueOf(14), "家居日用");
		productTypeMap.put(String.valueOf(15), "食品");
		productTypeMap.put(String.valueOf(16), "户外用品");
	}
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-12-3
	 * 
	 * 说明：获取产品分类的名称
     * 1-手机
     * 2-数码
     * 3-衣服
     * 4-鞋
     * 5-食品
     * 6-塑料
     * 7-衣物
     * 8-香水
	 * 
	 * 参数及返回值说明：
	 * 
	 * @return
	 */
	public String getProductTypeName(){
		String name = (String)productTypeMap.get(String.valueOf(this.productType));
		if(name == null){
			name = "";
		}
		return name;
	}

	/**
	 * 所有快递公司map
	 */
	public static Map deliverMapAll = new LinkedHashMap();

	/**
	 * 所有快递公司信息map
	 */
	public static Map<Integer,DeliverCorpInfoBean> deliverInfoMapAll = new LinkedHashMap<Integer,DeliverCorpInfoBean>();
	
	/**
	 * 初始化所有快递公司
	 */
	static{
		if(deliverMapAll.size()==0){
			initDeliverMapAll();
		}
	}
	
	/**
	 * 初始化所有快递公司
	 */
	public static void initDeliverMapAll(){
		deliverMapAll.clear();
		deliverMapAll.put(String.valueOf(-1), "未选择");
		deliverInfoMapAll.clear();
		
		DbOperation dbOp=new DbOperation();
		dbOp.init("adult_slave");
		try{
			String deliverMapsql="select id,name,changeable,pinyin,name_wap,phone,token,days,address,isems from deliver_corp_info";
			ResultSet rs=dbOp.executeQuery(deliverMapsql);
			while(rs.next()){
				int id=rs.getInt("id");
				String name=rs.getString("name");
				int changeable = rs.getInt("changeable");
				String pinyin = rs.getString("pinyin");
				String nameWap = rs.getString("name_wap");
				String phone = rs.getString("phone");
				String token = rs.getString("token");
				String days = rs.getString("days");
				String address = rs.getString("address");
				int isems = rs.getInt("isems");
				deliverMapAll.put(String.valueOf(id), name);
				
				
				DeliverCorpInfoBean deliver = new DeliverCorpInfoBean();
				deliver.setId(id);
				deliver.setName(name);
				deliver.setNameWap(nameWap);
				deliver.setChangeable(changeable);
				deliver.setPhone(phone);
				deliver.setPinyin(pinyin);
				deliver.setToken(token);
				deliver.setDays(days);
				deliver.setAddress(address);
				deliver.setIsems(isems);
				deliverInfoMapAll.put(id, deliver);
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	/**
	 * 可修改快递公司map
	 */
	public static Map deliverChangeMap = new LinkedHashMap();
	
	/**
	 * 初始化可修改快递公司
	 */
	static{
		if(deliverChangeMap.size()==0){
			initDeliverChangeMap();
		}
	}
	
	/**
	 * 初始化可修改快递公司
	 */
	public static void initDeliverChangeMap(){
		deliverChangeMap.clear();
		deliverChangeMap.put(String.valueOf(-1), "未选择");
		DbOperation dbOp=new DbOperation();
		dbOp.init("adult_slave");
		try{
			String deliverMapsql="select id,name from deliver_corp_info where changeable=1";
			ResultSet rs=dbOp.executeQuery(deliverMapsql);
			while(rs.next()){
				int id=rs.getInt(1);
				String name=rs.getString(2);
				deliverChangeMap.put(String.valueOf(id), name);
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	public static Map buyModeMap = new LinkedHashMap();
	static{
		buyModeMap.put(String.valueOf(0), "货到付款");
		buyModeMap.put(String.valueOf(1), "在线支付");
		buyModeMap.put(String.valueOf(2), "银行汇款");
		buyModeMap.put(String.valueOf(3), "售后换货");
	}
	
	/**
	 * 将deliver转化为对应的MailingBalanceBean里的balanceType值,添加快递公司时请同步更新此数据!
	 */
	public static Map deliverToBalanceTypeMap = new LinkedHashMap();

	/**
	 * 初始化快递公司和结算公司转换关系
	 */
	static{
		if(deliverToBalanceTypeMap.size()==0){
			initDeliverToBalanceTypeMap();
		}
	}
	
	/**
	 * 初始化快递公司和结算公司转换关系
	 */
	public static void initDeliverToBalanceTypeMap(){
		deliverToBalanceTypeMap.clear();
		DbOperation dbOp=new DbOperation();
		dbOp.init("adult_slave");
		try{
			String deliverMapsql="select deliver_id,balance_type_id from deliver_balance_type";
			ResultSet rs=dbOp.executeQuery(deliverMapsql);
			while(rs.next()){
				int deliverId=rs.getInt(1);
				int balanceTypeId=rs.getInt(2);
				deliverToBalanceTypeMap.put(String.valueOf(deliverId), String.valueOf(balanceTypeId));
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-25
	 * 
	 * 说明：根据 快递公司 查找 订单的发货地点
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param deliver
	 * @return
	 */
	public int checkDeliverArea(int deliver){
		int result = 0;
		switch(deliver){
			case 8:
			case 9:
			case 11:
				result = 2;
				break;
			case 10:
				result = 1;
				break;
			case 3:
			case 4:
			case 7:
				result = 0;
				break;
			default:
				result = -1;
		}
		return result;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-12-3
	 * 
	 * 说明：获取该订单快递的名字
     * 1-邮局
     * 2-昱达
     * 3-宅急送
     * 4-圆通
	 * 
	 * 参数及返回值说明：
	 * 
	 * @return
	 */
	public String getDeliverName(){
		String name = (String)deliverMapAll.get(String.valueOf(this.deliver));
		if(name == null){
			name = (String)deliverMapAll.get(String.valueOf(this.deliver));
		}
		if(name == null){
			name = "";
		}
		return name;
	}

	public Map getProductTypeMap(){
		return productTypeMap;
	}

//	public Map getDeliverMap(){
//		return deliverMap;
//	}
//
//	public Map getDeliverGdMap(){
//		return deliverGdMap;
//	}

	public Map getDeliverMapAll(){
		return deliverMapAll;
	}
	
	public Map<Integer,DeliverCorpInfoBean> getDeliverInfoMapAll(){
		return deliverInfoMapAll;
	}

	public int getBaozhuangzhongliang() {
		return this.baozhuangzhongliang;
	}

	public void setBaozhuangzhongliang(int baozhuangzhongliang) {
		this.baozhuangzhongliang = baozhuangzhongliang;
	}

	public int getBZZL() {
		int bzzl = 0;
		if(this.baozhuangzhongliang < 450){
			bzzl = 50;
		} else if(this.baozhuangzhongliang > 450){
			bzzl = (int)(this.baozhuangzhongliang * 0.1);
		}
		return baozhuangzhongliang + bzzl;
	}

	public String getDestination(){
		String dest = "";
		String shi = "";
		String bigCity="";
		String tb="";
		if(address != null && address.length() > 2){
			int shengIndex = address.indexOf("省");
			int shengIndex1 = address.indexOf("自治区");
			//江苏省宿迁市沭阳县
			if(address.length()>=2) bigCity = address.substring(0, 2).trim();//北京,上海,重庆,天津,海外截取	
			if(address.length()>=7) tb=address.substring(0, 7).trim();//港澳截取
			int shiIndex = address.indexOf("市");
			int shiIndex1 = address.indexOf("自治州");
			int shiIndex2 = address.indexOf("地区");
			//一级:省
			if(shengIndex > 0){
				if(shiIndex > 0 && shiIndex > shengIndex){//省市
					shi = address.substring(shengIndex + 1, shiIndex);
					dest = shi + "市";
				}else if(shiIndex1>0&& shiIndex1 > shengIndex){//省自治州
					shi = address.substring(shengIndex + 1, shiIndex1);
					dest = shi + "自治州";
				}else if(shiIndex2>0&& shiIndex2 > shengIndex){//省地区
					shi = address.substring(shengIndex + 1, shiIndex2);
					dest = shi + "地区";
				}else{
					dest=address.substring(0,shengIndex + 1);
				}
			}
			//一级:自治区
			if(shengIndex1 > 0){
				if(shiIndex > 0 && shiIndex > shengIndex1){//省市
					shi = address.substring(shengIndex1 + 3, shiIndex);
					dest = shi + "市";
				}else if(shiIndex1>0&& shiIndex1 > shengIndex1){//省自治州
					shi = address.substring(shengIndex1 + 3, shiIndex1);
					dest = shi + "自治州";
				}else if(shiIndex2>0&& shiIndex2 > shengIndex1){//省地区
					shi = address.substring(shengIndex1 + 3, shiIndex2);
					dest = shi + "地区";
				}else{
					dest=address.substring(0,shengIndex1 + 3);
				}
			}
			
			//如果是北京,上海,重庆,天津,海外
			 if(bigCity.equals("天津")||bigCity.equals("北京")||bigCity.equals("上海")||bigCity.equals("重庆")||bigCity.equals("海外")){
				 dest=bigCity;		 
			 }
			//港澳行政区
			 if(tb.equals("澳门特别行政区")||bigCity.equals("香港特别行政区")){
				 dest=tb.substring(0, 2);
			 }
		}
		//System.out.println("===>"+dest);
		return dest;
	}

	public float getPostageBj(){
		float postage = 0;
		Postage p = PostageUtil.getPostage(0, this.getDestination());
		if(p != null){
			Postage pBean = (Postage)p.clone();
			pBean.setWeight(this.getBZZL());
			pBean.setPrice(this.getDprice());
			postage = pBean.getPostage();
		}
		return postage;
	}

	public float getPostageGd(){
		float postage = 0;
		Postage p = PostageUtil.getPostage(1, this.getDestination());
		if(p != null){
			Postage pBean = (Postage)p.clone();
			pBean.setWeight(this.getBZZL());
			pBean.setPrice(this.getDprice());
			postage = pBean.getPostage();
		}
		return postage;
	}

	public float getPostageDif(){
		return this.getPostageBj() - this.getPostageGd();
	}

	public float getPostageSavePercent(){
		//float maxPostage = Math.max(this.getPostageBj(), this.getPostageGd());
		float maxPostage = this.getPostageBj();
		if(maxPostage == 0){
			return 0;
		} else {
			return (this.getPostageDif() / maxPostage) * 100;
		}
	}

	public int getNextStatus(){
		int status = 0;
		switch(this.status){
			case 0:
				status = 1;
				break;
			case 1:
				status = 2;
				break;
			case 3:
				status = 3;
				break;
			default:
				status = this.status;
		}
		return status;
	}

	public int getStockoutDeal() {
		return stockoutDeal;
	}

	public void setStockoutDeal(int stockoutDeal) {
		this.stockoutDeal = stockoutDeal;
	}

	public float getPrice3() {
		return price3;
	}

	public void setPrice3(float price3) {
		this.price3 = price3;
	}

	public String getLastOperTime() {
		return lastOperTime;
	}

	public void setLastOperTime(String lastOperTime) {
		this.lastOperTime = lastOperTime;
	}

	public OrderStockBean getOrderStock() {
		return orderStock;
	}

	public void setOrderStock(OrderStockBean orderStock) {
		this.orderStock = orderStock;
	}

	public int getBalanceStatus() {
		return balanceStatus;
	}

	public void setBalanceStatus(int balanceStatus) {
		this.balanceStatus = balanceStatus;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public String getNextLackDealDatetime() {
		return nextLackDealDatetime;
	}

	public void setNextLackDealDatetime(String nextLackDealDatetime) {
		this.nextLackDealDatetime = nextLackDealDatetime;
	}

	public int getLackDealAdminId() {
		return lackDealAdminId;
	}

	public void setLackDealAdminId(int lackDealAdminId) {
		this.lackDealAdminId = lackDealAdminId;
	}

	public voUser getLackDealAdmin() {
		return lackDealAdmin;
	}

	public void setLackDealAdmin(voUser lackDealAdmin) {
		this.lackDealAdmin = lackDealAdmin;
	}

	public String getGroup_num() {
		return group_num;
	}

	public void setGroup_num(String group_num) {
		this.group_num = group_num;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getUserOrderCommentCode() {
		return userOrderCommentCode;
	}

	public void setUserOrderCommentCode(String userOrderCommentCode) {
		this.userOrderCommentCode = userOrderCommentCode;
	}

	/**
	 * 判断当前订单 是否是 大q订单
	 * @return true 是大q订单，false 不是
	 */
	public boolean isDaqOrder() {
		return ( this.flat == voOrder.FLAT2 ) ;
	}
	/**
	 * 判断当前订单 是否是 兰亭订单
	 * @return true 是兰亭订单，false 不是
	 */
	public boolean isLTOrder() {
		return ( this.flat == voOrder.FLAT8 || this.flat == voOrder.FLAT11) ;
	}
	
	/**
	 * 判断订单是否是 源自Amazon
	 * @return true 是 亚马逊订单， false 不是亚马逊订单
	 */
	public boolean isAmazonOrder() {
		return (this.flat == voOrder.FLAT3);
	}
	/**
	 * 判断订单是否是 源自Amazon
	 * @return true 是 亚马逊订单， false 不是亚马逊订单
	 */
	public boolean isDangdangOrder() {
		return (this.flat == voOrder.FLAT6);
	}
	/**
	 * 判断订单是否是 源自Amazon
	 * @return true 是 亚马逊订单， false 不是亚马逊订单
	 */
	public boolean isJdOrder() {
		return (this.flat == voOrder.FLAT7);
	}
	
	/**
	 * 判断订单是否是 源自京东（成人）订单
	 */
	public boolean isJdAdultOrder() {
		return (this.flat == voOrder.FLAT12);
	}
	
	/**
	 * 判断订单是否是 源自淘宝
	 * @return true 是 淘宝订单， false 不是淘宝订单
	 */
	public boolean isTaobaoOrder() {
		return (this.flat == voOrder.FLAT5 || this.flat == voOrder.FLAT10);
	}

	public String getAmazonCode() {
		return amazonCode;
	}

	public void setAmazonCode(String amazonCode) {
		this.amazonCode = amazonCode;
	}

	public String getDangdangCode() {
		return dangdangCode;
	}

	public void setDangdangCode(String dangdangCode) {
		this.dangdangCode = dangdangCode;
	}

	public String getJdCode() {
		return jdCode;
	}

	public void setJdCode(String jdCode) {
		this.jdCode = jdCode;
	}

	public String getJdAdultCode() {
		return jdAdultCode;
	}

	public void setJdAdultCode(String jdAdultCode) {
		this.jdAdultCode = jdAdultCode;
	}

	/**
	 * @return the productList
	 */
	public List getProductList() {
		return productList;
	}

	/**
	 * @param productList the productList to set
	 */
	public void setProductList(List productList) {
		this.productList = productList;
	}

	/**
	 * @return the totalPrice
	 */
	public float getTotalPrice() {
		return totalPrice;
	}

	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	/**
	 * @return the totalProductCount
	 */
	public int getTotalProductCount() {
		return totalProductCount;
	}

	/**
	 * @param totalProductCount the totalProductCount to set
	 */
	public void setTotalProductCount(int totalProductCount) {
		this.totalProductCount = totalProductCount;
	}

	
}
