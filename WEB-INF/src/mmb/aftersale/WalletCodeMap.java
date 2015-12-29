package mmb.aftersale;

import java.util.HashMap;

/**
 * 作者：曹续
 *
 * 错误代码
 *
 * 创建日期：2009-9-8
 * 
 * WalletCodeMap.java
 *
 */
/**
 * 作者：曹续
 *
 * 创建日期：2009-9-8
 * 
 * WalletCodeMap.java
 *
 */
/**
 * 作者：曹续
 *
 * 创建日期：2009-9-8
 * 
 * WalletCodeMap.java
 *
 */
public class WalletCodeMap {
	public static final int SUCCESS = 100; // 操作成功
	public static final int HAVE_WALLTE_PASSWORD = 101; // 登陆用户具有支付密码
	
	/**
	 *  特殊错误码
	 */
	public static final int NOT_HAVE_WALLTE_PASSWORD = -1; // 登陆用户没有设置支付密码
	public static final int TWO_PASSWORD_NOT_SAME = -2; // 两次输入的密码不相同
	public static final int PASSWORD_ERROR = -3; // 输入密码错误
	public static final int CARD_AMOUNT_ERROR = -4; // 输入充值卡金额错误
	public static final int CARD_ID_NULL = -5; // 充值卡号为空
	public static final int CARD_PASSWORD_NULL = -6; // 充值卡密码为空
	public static final int CARD_INFO_ERROR = -7; // 充值卡号或充值卡密码错误
	public static final int CARD_TYPE_ERROR = -8; // 充值卡种类错误
	public static final int UNVERIFIED_CARD = -9;  //未验证卡
	public static final int NOT_HAVE_DNA_INFO = -10; // 没有之前DNA订单的信息
	
	/**
	 *  全局错误码
	 */
	public static final int WARNING = -90 ; //存在一些警告信息
	public static final int INPUT_NULL =-95 ; // 输入信息为空
	public static final int ORDER_DONE = -96; // 已处理订单
	public static final int BALANCE_LESS = -97; // 余额不足
	public static final int DATA_BASE_ERROR = -98; // 连接数据错误
	public static final int FAILURE = -99; // 操作失败
	public static final int NOT_LOGIN = -100; // 没有用户登陆
	public static final int NOT_PERMIT = -101 ; //没有权限
 
	/**
	 *  入账类型
	 */
	public static final int IN_ORDER_TYPE_CARD = 0; //入账类型 充值卡
	public static final int IN_ORDER_TYPE_DNA = 1 ; //入账类型 银联
	public static final int IN_ORDER_TYPE_ICBC = 2 ; //入账类型 工商银行
	public static final int IN_ORDER_TYPE_CCB = 3 ; //入账类型 建设银行
	public static final int IN_ORDER_TYPE_POINT = 4 ; //入账类型 积分兑换
	public static final int IN_ORDER_TYPE_ADMIN = 9 ; //入账类型 后台添加
	public static final int IN_ORDER_TYPE_LOTTERY = 5;	//彩票，普通单买订单兑奖
	public static final int IN_ORDER_TYPE_LOTTERY_HM_YJ = 6;	//彩票，普通合买订单佣金
	public static final int IN_ORDER_TYPE_LOTTERY_HM = 7;	//彩票，普通合买订单奖金分享
	public static final int IN_ORDER_TYPE_CAIFUTONG = 10;	//财付通
	public static final int IN_ORDER_TYPE_ZHIFUBAO = 11;	//支付宝
	public static final int IN_ORDER_TYPE_GROUP_RATE_RECOMMEND = 12;	//团购：团购推荐奖励
	public static final int IN_ORDER_TYPE_JFS = 13 ; //入账类型 捷付士
	
	/**
	 *  出账类型
	 */
	public static final int OUT_ORDER_TYPE_MMB = 3; //买卖宝
	public static final int OUT_ORDER_TYPE_CP = 2; //彩票
	public static final int OUT_ORDER_TYPE_17K = 1; //17K书城
	public static final int OUT_ORDER_TYPE_REFUND =-1 ;//退款
	public static final int OUT_ORDER_TYPE_LOTTERY = 4;	//彩票，普通投注
	public static final int OUT_ORDER_TYPE_LOTTERY_HM = 5;	//彩票，合买认购
	public static final int OUT_ORDER_TYPE_LOTTERY_HMBD = 6;	//彩票，合买保底
	public static final int OUT_ORDER_TYPE_HFZC = 7 ; //话费直充
	public static final int OUT_ORDER_TYPE_MMB_MERGE_F = -3 ;  //买卖宝订单合并 废除订单
	public static final int OUT_ORDER_TYPE_MMB_MERGE = 33 ; // 买卖宝订单合并 新订单
	public static final int OUT_ORDER_TYPE_ADMIN = 9 ;  //后台扣除
	public static final int OUT_ORDER_TYPE_LOTTERY_TX = 8 ; //钱包(彩票)提现
	public static final int OUT_ORDER_TYPE_QB = 10 ; //老顾客购买Q币
	public static final int OUT_ORDER_TYPE_KAMI = 11 ; //购买点卡卡密
	public static final int OUT_ORDER_TYPE_ZHICHONG = 12 ; //购买点卡直冲
	public static final int OUT_ORDER_TYPE_VP = 13 ; //购买点卡直冲
	
	/**
	 *  订单类型
	 */
	public static final int ORDER_ACT_TYPE_IN = 0 ; //订单种类 入账
	public static final int ORDER_ACT_TYPE_OUT =1 ; //订单种类 出账
	public static final int ORDER_ACT_TYPE_REFUND = -1 ; //订单种类 退款
	/**
	 *  订单状态
	 */
	public static final int ORDER_STATUS_DOING = 0 ; //订单状态 处理中
	public static final int ORDER_STATUS_SUCCESS = 1; //订单状态 成功
	public static final int ORDER_STATUS_FREEZEING = 2 ; // 订单状态 资金冻结中
	public static final int ORDER_STATUS_FAILURE = -1; //订单状态 失败
	
	/**
	 *  日志操作类型 
	 */
	public final static int OPER_LOG_ACT_TYPE_IN = 0; // 操作类型入账
	public final static int OPER_LOG_ACT_TYPE_OUT = 1; // 操作类型出账
	public final static int OPER_LOG_ACT_TYPE_FREEZE = 2; // 操作类型冻结
	public final static int OPER_LOG_ACT_TYPE_OUTFREEZE = 3; // 操作类型划账 冻结->支付
	public final static int OPER_LOG_ACT_TYPE_UNFREEZE = 4; // 操作类型解冻 冻结->资金回滚
	public final static int OPER_LOG_ACT_TYPE_REFUND = -1 ; //操作类型退款
	
	/**
	 *  积分（麦穗）增减类型
	 */
	public static final int POINT_ACT_TYPE_MMB_IN = 0 ; //支付买卖宝订单，增加
	public static final int POINT_ACT_TYPE_EXCHAGE_OUT =1 ; //麦穗兑换，减少
	public static final int POINT_ACT_TYPE_MMB_REFUND = -1 ; //买卖宝订单退款，减少
	public static final int POINT_ACT_TYPE_CHOUJIANG_OUT = 2 ; //幸运大转盘，消耗麦穗
	public static final int POINT_ACT_TYPE_CHOUJIANG_IN = 3 ; //幸运大转盘，获得麦穗
	
	/**
	 *  入账类型MAP 替代数据库查询
	 */
	public static HashMap IN_ORDER_TYPE_MAP = null;
	static{
		IN_ORDER_TYPE_MAP = new HashMap();
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_CARD),"充值卡");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_DNA),"银联");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_POINT),"麦穗兑换");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_ADMIN),"后台增加");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_LOTTERY),"单买兑奖");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_LOTTERY_HM_YJ),"合买兑奖_佣金");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_LOTTERY_HM),"合买兑奖");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_CAIFUTONG),"财付通");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_ZHIFUBAO),"支付宝");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_GROUP_RATE_RECOMMEND),"团购推荐奖励");
		IN_ORDER_TYPE_MAP.put(Integer.valueOf(IN_ORDER_TYPE_JFS),"捷付士");
	}
	
	/**
	 *  出账类型MAP 替代数据库查询
	 */
	public static HashMap OUT_ORDER_TYPE_MAP = null;
	static{
		OUT_ORDER_TYPE_MAP = new HashMap();
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_MMB),"买卖宝");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_17K),"17K");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_LOTTERY),"彩票普通投注");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_LOTTERY_HM),"彩票合买认购");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_LOTTERY_HMBD),"彩票合买保底");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_REFUND), "退款");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_MMB_MERGE), "买卖宝（合并）");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_MMB_MERGE_F), "买卖宝（合并废除）");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_ADMIN), "后台减少");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_HFZC), "话费直充");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_LOTTERY_TX), "彩票提现");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_QB), "老顾客购买Q币");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_KAMI), "购买点卡卡密");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_ZHICHONG), "购买点卡直冲");
		OUT_ORDER_TYPE_MAP.put(Integer.valueOf(OUT_ORDER_TYPE_VP), "虚拟商品");
	}
	
	/**
	 * 订单状态Map
	 */
	public static HashMap ORDER_STATUS_MAP = null;
	static{
		ORDER_STATUS_MAP = new HashMap();
		ORDER_STATUS_MAP.put(Integer.valueOf(ORDER_STATUS_DOING),"处理中");
		ORDER_STATUS_MAP.put(Integer.valueOf(ORDER_STATUS_SUCCESS),"成功");
		ORDER_STATUS_MAP.put(Integer.valueOf(ORDER_STATUS_FAILURE),"失败");
	}
	
	/**
	 * 流水账单类别 出账 入账
	 */
	public static HashMap ORDER_ACT_TYPE_MAP =null;
	static{
		ORDER_ACT_TYPE_MAP = new HashMap();
		ORDER_ACT_TYPE_MAP.put(Integer.valueOf(ORDER_ACT_TYPE_IN),"入账");
		ORDER_ACT_TYPE_MAP.put(Integer.valueOf(ORDER_ACT_TYPE_OUT),"出账");
		ORDER_ACT_TYPE_MAP.put(Integer.valueOf(ORDER_ACT_TYPE_REFUND),"退款");
	}
	
	/**
	 * 积分账单类别Map
	 */
	public static HashMap POINT_ACCOUNT_TYPE_MAP =null;
	static{
		POINT_ACCOUNT_TYPE_MAP = new HashMap();
		POINT_ACCOUNT_TYPE_MAP.put(Integer.valueOf(POINT_ACT_TYPE_MMB_IN),"支付增加");
		POINT_ACCOUNT_TYPE_MAP.put(Integer.valueOf(POINT_ACT_TYPE_EXCHAGE_OUT),"麦穗兑换");
		POINT_ACCOUNT_TYPE_MAP.put(Integer.valueOf(POINT_ACT_TYPE_MMB_REFUND),"退款扣除");
		POINT_ACCOUNT_TYPE_MAP.put(Integer.valueOf(POINT_ACT_TYPE_CHOUJIANG_OUT),"幸运大转盘消耗");
		POINT_ACCOUNT_TYPE_MAP.put(Integer.valueOf(POINT_ACT_TYPE_CHOUJIANG_IN),"幸运大转盘获得");
	}
}

