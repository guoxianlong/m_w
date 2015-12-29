/*
 * Created on 2007-11-14
 *
 */
package adultadmin.bean.stock;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.buy.BuyStockBean;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：
 */
public class StockOperationBean {

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

	/**
	 * type 订单出库
	 */
	public static int ORDER_STOCK = 0; //type 订单出库

	/**
	 * type 采购入库
	 */
    public static int BUY_STOCKIN = 1; //type 采购入库

    /**
     * type 两地调货
     */
    public static int BJ_GD = 2; //type 两地调货

    /**
     * type 产品组合
     */
    public static int REGROUP = 3; //type 产品组合

    /**
     * type 产品套装
     */
    public static int PRODUCT_PACKAGE = 4; //type 产品套装

    /**
     * type 其他出入库
     */
    public static int OTHERS = 5; //type 其他出入库

    /**
     * type 退货入库
     */
    public static int CANCEL_STOCKIN = 6; //type 退货入库

    /**
     * type 退换货
     */
    public static int CANCEL_EXCHANGE = 7; //type 退换货

    /**
     * type 烂货退换
     */
    public static int BAD_EXCHANGE = 8; //type 烂货退换

    /**
     * type 产品检测
     */
    public static int STOCK_CHECK = 9; //type 产品检测

    /**
     * type 产品维修
     */
    public static int STOCK_REPAIR = 10; //type 产品维修

    /**
     * type 产品返厂
     */
    public static int STOCK_BACK = 11; //type 产品返厂

    /**
     * type 产品坏了
     */
    public static int STOCK_BAD = 12; //type 产品坏了

    /**
     * status 处理中
     */
    public static int STATUS1 = 0; //status 处理中

    /**
     * <pre>
     * status 
     * 订单出库是待出货，退换货是已入库，其他是已完成
     * 产品维修:已确认待审核
     * 产品返厂:
     * 产品检测:已确认待审核
     * 产品坏了:
     * </pre>
     */
    public static int STATUS2 = 1; //status 订单出库是待出货，其他是已完成

    /**
     * <pre>
     * status 订单出库是已确认，退换货是以换货，两地调货是已完成但有损耗
     * 产品维修:审核通过维修中
     * 产品返厂:
     * 产品检测:审核通过检测中
     * 产品坏了:
     * </pre>
     */
    public static int STATUS3 = 2; //status 订单出库是已确认，两地调货是已完成但有损耗

    /**
     * <pre>
     * status 订单出库是已删除，退换货是已完成，其他无用
     * 产品维修:出库审核未通过
     * 产品返厂:
     * 产品检测:出库审核未通过
     * 产品坏了:
     * </pre>
     */
    public static int STATUS4 = 3; //status 订单出库是已删除，其他无用

    /**
     * <pre>
     * status 订单出库是退货删除，其他无用
     * 产品维修:已维修待审核
     * 产品返厂:
     * 产品检测:已检测待审核
     * 产品坏了:
     * </pre>
     */
    public static int STATUS5 = 4; //status 订单出库是退货删除，其他无用

    /**
     * <pre>
     * 产品维修:已审核维修完毕
     * 产品返厂:
     * 产品检测:已审核检测完毕
     * 产品坏了:
     * </pre>
     */
    public static int STATUS6 = 5;

    /**
     * <pre>
     * 产品维修:审核未通过维修中
     * 产品返厂:
     * 产品检测:审核未通过检测中
     * 产品坏了:
     * </pre>
     */
    public static int STATUS7 = 6;

    /**
     * 北京——北库
     */
    public static int BJ = 0;

    /**
     * 广东——广分
     */
    public static int GD = 1;

    /**
     * 广速
     */
    public static int GS = 2;

    /**
     * 订单处理中，而且库存充足
     */
    public static int STATUS1_STOCK = 1;

    /**
     * 订单处理中，但是缺货
     */
    public static int STATUS1_NO_STOCK = 2;

    /**
     * 当前地区库存满足，另一个地方不满足
     */
    public static int STATUS1_ONE_STOCK = 5;

    /**
     * 当前地区库存不满足，另一地区库存满足
     */
    public static int STATUS1_OTHER_STOCK = 6;

    /**
     * 订单出货是待出货；以前的出现过库存不足情况。
     */
    public static int STATUS2_FROM_NO_STOCK = 3;

    /**
     * 订单出货是待出货；库存一直充足。
     */
    public static int STATUS2_FROM_STOCK = 4;

    public int id;

    public String name;

    public int type;

    public int status;

    public String remark;

    public String createDatetime;

    public String orderCode;

    public int groupId; //产品组合是产品组ID，产品套装是套装产品ID

    public voOrder order;

    /**
     * 商品库所在地<br/>
     * 具体分类参考 ProductStockBean 中的 area 定义
     */
    public int area;

    public int statusStock;

    public int realStatusStock;

    public String lastOperTime; //最后操作时间

    public int printCount;

    /**
     * 删除了库存操作中某些元素的操作
     */
    public static int STOCK_OPERATION_STATE_DELETED = 1;

    public static int STOCK_OPERATION_STATE_EDITED = 2;

    public int state;

    public int buyPlanId;
    public int buyStockId;
    public int buyOrderId;

    /**
     * <pre>
     * 上级“单据”的ID
     * 增加商品检测、返厂、维修 功能是添加的属性
     * 记录某个操作单从哪里来的
     * eg: 商品检测单据 ID=123； 从该单据生成的返厂单的parentId=123；表示该返厂操作是针对这个检测单生成的。
     * </pre>
     */
    public int parentId;

    /**
     * 创建人 ID
     */
    public int createUserId;
    /**
     * 审核人 ID
     */
    public int auditingUserId;

    /**
     * 第二个审核人，做完成确认的审核
     * 
     */
    public int auditing2UserId;

    /**
     * 商品库类型<br/>
     * 类型参考 ProductStockBean中的 type 定义
     */
    public int stockType;

    public String code;

    public BuyStockBean buyStock;
    
    /**
     * 操作人Id
     */
    public int userId;
    
    /**
     * 操作人姓名
     */
    public String userName;
    /**
     * @return Returns the lastOperTime.
     */
    public String getLastOperTime() {
        return lastOperTime;
    }

    /**
     * @param lastOperTime
     *            The lastOperTime to set.
     */
    public void setLastOperTime(String lastOperTime) {
        this.lastOperTime = lastOperTime;
    }

    /**
     * @return Returns the area.
     */
    public int getArea() {
        return area;
    }

    /**
     * @param area
     *            The area to set.
     */
    public void setArea(int area) {
        this.area = area;
    }

    /**
     * @return Returns the order.
     */
    public voOrder getOrder() {
        return order;
    }

    /**
     * @param order
     *            The order to set.
     */
    public void setOrder(voOrder order) {
        this.order = order;
    }

    /**
     * @return Returns the groupId.
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     *            The groupId to set.
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * @return Returns the createDatetime.
     */
    public String getCreateDatetime() {
        return createDatetime;
    }

    /**
     * @param createDatetime
     *            The createDatetime to set.
     */
    public void setCreateDatetime(String createDatetime) {
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
     * @return Returns the orderCode.
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * @param orderCode
     *            The orderCode to set.
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
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
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    public String getStatusName() {
        if (type == StockOperationBean.ORDER_STOCK) {
            if (status == StockOperationBean.STATUS1) {
                return "处理中";
            }
            if (status == StockOperationBean.STATUS2) {
                return "待出货";
            }
            if (status == StockOperationBean.STATUS3) {
                return "已出货";
            }
            if (status == StockOperationBean.STATUS4) {
                return "已删除";
            }
            if (status == StockOperationBean.STATUS5) {
                return "退单删除";
            }
        }
        if (type == StockOperationBean.BJ_GD) {
            if (status == StockOperationBean.STATUS1) {
                return "处理中";
            }
            if (status == StockOperationBean.STATUS2) {
                return "已完成";
            }
            if (status == StockOperationBean.STATUS3) {
                return "已完成但有损耗";
            }
        }
        if(type == StockOperationBean.CANCEL_EXCHANGE || type == StockOperationBean.BAD_EXCHANGE) {
        	if (status == StockOperationBean.STATUS1) {
                return "处理中";
            }
            if (status == StockOperationBean.STATUS2) {
                return "入库已完成 换货处理中";
            }
            if (status == StockOperationBean.STATUS3) {
                return "入库已完成 换货已完成";
            }
            if (status == StockOperationBean.STATUS4) {
                return "已完成";
            }
        }
        if(type == StockOperationBean.STOCK_CHECK) {
        	if (status == StockOperationBean.STATUS1) {
                return "处理中";
            }
            if (status == StockOperationBean.STATUS2) {
                return "第一步已确认 待审核";
            }
            if (status == StockOperationBean.STATUS3) {
                return "第一步已审核 检测中";
            }
            if (status == StockOperationBean.STATUS4) {
                return "第一步审核未通过 处理中";
            }
            if (status == StockOperationBean.STATUS5) {
                return "第二步已确认 待审核";
            }
            if (status == StockOperationBean.STATUS6) {
                return "第二步已审核 检测完成";
            }
            if (status == StockOperationBean.STATUS7) {
                return "第二步审核未通过 检测中";
            }
        }
        if(type == StockOperationBean.STOCK_REPAIR) {
        	if (status == StockOperationBean.STATUS1) {
                return "处理中";
            }
            if (status == StockOperationBean.STATUS2) {
                return "第一步已确认 待审核";
            }
            if (status == StockOperationBean.STATUS3) {
                return "第一步已审核 维修中";
            }
            if (status == StockOperationBean.STATUS4) {
                return "第一步审核未通过 处理中";
            }
            if (status == StockOperationBean.STATUS5) {
                return "第二步已确认 待审核";
            }
            if (status == StockOperationBean.STATUS6) {
                return "第二步已审核 维修完成";
            }
            if (status == StockOperationBean.STATUS7) {
                return "第二步审核未通过 维修中";
            }
        }
        if(type == StockOperationBean.STOCK_BACK) {
        	if (status == StockOperationBean.STATUS1) {
                return "处理中";
            }
            if (status == StockOperationBean.STATUS2) {
                return "第一步已确认 待审核";
            }
            if (status == StockOperationBean.STATUS3) {
                return "第一步已审核 返厂中";
            }
            if (status == StockOperationBean.STATUS4) {
                return "第一步审核未通过 处理中";
            }
            if (status == StockOperationBean.STATUS5) {
                return "第二步已确认 待审核";
            }
            if (status == StockOperationBean.STATUS6) {
                return "第二步已审核 返厂完成";
            }
            if (status == StockOperationBean.STATUS7) {
                return "第二步审核未通过 返厂中";
            }
        }
        if (status == StockOperationBean.STATUS1) {
            return "处理中";
        }
        if (status == StockOperationBean.STATUS2) {
            return "已完成";
        }

        return null;
    }

    public int getStatusStock() {
        return statusStock;
    }

    public void setStatusStock(int statusStock) {
        this.statusStock = statusStock;
    }

    public int getRealStatusStock() {
        return realStatusStock;
    }

    public void setRealStatusStock(int realStatusStock) {
        this.realStatusStock = realStatusStock;
    }

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isOperationState(int state) {
		return (this.state & state) > 0;
	}

	public int getBuyPlanId() {
		return buyPlanId;
	}

	public void setBuyPlanId(int buyPlanId) {
		this.buyPlanId = buyPlanId;
	}

	public int getBuyStockId() {
		return buyStockId;
	}

	public void setBuyStockId(int buyStockId) {
		this.buyStockId = buyStockId;
	}

	public BuyStockBean getBuyStock() {
		return buyStock;
	}

	public void setBuyStock(BuyStockBean buyStock) {
		this.buyStock = buyStock;
	}

	public int getAuditingUserId() {
		return auditingUserId;
	}

	public void setAuditingUserId(int auditingUserId) {
		this.auditingUserId = auditingUserId;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getAuditing2UserId() {
		return auditing2UserId;
	}

	public void setAuditing2UserId(int auditing2UserId) {
		this.auditing2UserId = auditing2UserId;
	}

	public int getBuyOrderId() {
		return buyOrderId;
	}

	public void setBuyOrderId(int buyOrderId) {
		this.buyOrderId = buyOrderId;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
}
