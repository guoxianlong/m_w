package mmb.stock.cargo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import mmb.stock.stat.ProductWarePropertyBean;
import mmb.stock.stat.StatService;
import mmb.system.admin.AdminService;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

	public class CartonningInfoService extends BaseServiceImpl{
		public CartonningInfoService(int useConnType, DbOperation dbOp) {
			this.useConnType = useConnType;
			this.dbOp = dbOp;
		}

		public CartonningInfoService() {
			this.useConnType = CONN_IN_SERVICE;
		}
		// 装箱基本信息
		public boolean addCartonningInfo(CartonningInfoBean bean) {
			return addXXX(bean, "cartonning_info");
		}

		public ArrayList getCartonningList(String condition, int index, int count, String orderBy) {
			return getXXXList(condition, index, count, orderBy, "cartonning_info", "mmb.stock.cargo.CartonningInfoBean");
		}
		
		public ArrayList getCartonningAndProductList(String condition, int index, int count, String orderBy,DbOperation dbOp) {
			ArrayList<CartonningInfoBean> list = new ArrayList<CartonningInfoBean>();
			try {
				String query = "select distinct cpi.id,cpi.product_id,cpi.product_code,cpi.product_count,cpi.product_name," +
						"ci.id,ci.code,ci.create_time,ci.status,ci.name,ci.cargo_id,c.whole_code " +
						"from cartonning_info ci left join cartonning_product_info cpi on ci.id=cpi.cartonning_id " +
						"left join cargo_info c on ci.cargo_id=c.id ";
				if (condition != null) {
		            query += " where " + condition;
		        }
		        if (orderBy != null) {
		            query += " order by " + orderBy;
		        }
		        query = DbOperation.getPagingQuery(query, index, count);

				ResultSet rs = dbOp.executeQuery(query);
				while(rs.next()){
					CartonningInfoBean ciBean = new CartonningInfoBean();
					CartonningProductInfoBean cpiBean = new CartonningProductInfoBean();
					cpiBean.setId(rs.getInt(1));
					cpiBean.setProductId(rs.getInt(2));
					cpiBean.setProductCode(rs.getString(3));
					cpiBean.setProductCount(rs.getInt(4));
					cpiBean.setProductName(rs.getString(5));
					ciBean.setId(rs.getInt(6));
					ciBean.setCode(rs.getString(7));
					ciBean.setCreateTime(rs.getString(8));
					ciBean.setStatus(rs.getInt(9));
					ciBean.setName(rs.getString(10));
					ciBean.setCargoId(rs.getInt(11));
					ciBean.setCargoWholeCode(rs.getString(12));
					ciBean.setProductBean(cpiBean);
					list.add(ciBean);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return list;
		}
		/*
		 * 装箱记录列表的总数，将原来的distinct count(ci.id)中的distinct去掉，
		 * 因为由于totalcount的问题，可能会导致原来分页中因为少了几条数据而不显示最后一页
		 */
		public int getCartonningAndProductListCount(String condition,DbOperation dbOp) {
			int count = 0;
			try {
				String query = "select count(ci.id) from cartonning_info ci left join cartonning_product_info cpi on ci.id=cpi.cartonning_id " +
						"left join cargo_info c on ci.cargo_id=c.id ";
				if (condition != null) {
		            query += " where " + condition;
		        }
				ResultSet rs = dbOp.executeQuery(query);
				if(rs.next()){
					count = rs.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return count;
		}
		public int getCartonningCount(String condition) {
			return getXXXCount(condition, "cartonning_info", "id");
		}

		public CartonningInfoBean getCartonningInfo(String condition) {
			return (CartonningInfoBean) getXXX(condition, "cartonning_info",
			"mmb.stock.cargo.CartonningInfoBean");
		}

		public boolean updateCartonningInfo(String set, String condition) {
			return updateXXX(set, condition, "cartonning_info");
		}

		public boolean deleteCartonningInfo(String condition) {
			return deleteXXX(condition, "cartonning_info");
		}

		
		//装箱产品信息
		public boolean addCartonningProductInfo(CartonningProductInfoBean bean) {
			return addXXX(bean, "cartonning_product_info");
		}

		public ArrayList getCartonningProductList(String condition, int index, int count, String orderBy) {
			return getXXXList(condition, index, count, orderBy, "cartonning_product_info", "mmb.stock.cargo.CartonningProductInfoBean");
		}

		public int getCartonningProductCount(String condition) {
			return getXXXCount(condition, "cartonning_product_info", "id");
		}

		public CartonningProductInfoBean getCartonningProductInfo(String condition) {
			return (CartonningProductInfoBean) getXXX(condition, "cartonning_product_info",
			"mmb.stock.cargo.CartonningProductInfoBean");
		}

		public boolean updateCartonningProductInfo(String set, String condition) {
			return updateXXX(set, condition, "cartonning_product_info");
		}

		public boolean deleteCartonningProductInfo(String condition) {
			return deleteXXX(condition, "cartonning_product_info");
		}
		
		//装箱单标准装箱量
		public boolean addCartonningStandardCount(CartonningStandardCountBean bean) {
			return addXXX(bean, "cartonning_standard_count");
		}

		public ArrayList getCartonningStandardCountList(String condition, int index, int count, String orderBy) {
			return getXXXList(condition, index, count, orderBy, "cartonning_standard_count", "mmb.stock.cargo.CartonningStandardCountBean");
		}

		public int getCartonningStandardCountCount(String condition) {
			return getXXXCount(condition, "cartonning_standard_count", "id");
		}

		public CartonningStandardCountBean getCartonningStandardCount(String condition) {
			return (CartonningStandardCountBean) getXXX(condition, "cartonning_standard_count",
			"mmb.stock.cargo.CartonningStandardCountBean");
		}

		public boolean updateCartonningStandardCount(String set, String condition) {
			return updateXXX(set, condition, "cartonning_standard_count");
		}

		public boolean deleteCartonningStandardCount(String condition) {
			return deleteXXX(condition, "cartonning_standard_count");
		}
		
		//合格库待作业管理
		public boolean addCargoOperationTodo(CargoOperationTodoBean bean) {
			return addXXX(bean, "cargo_operation_todo");
		}

		public ArrayList getCargoOperationTodoList(String condition, int index, int count, String orderBy) {
			return getXXXList(condition, index, count, orderBy, "cargo_operation_todo", "mmb.stock.cargo.CargoOperationTodoBean");
		}

		public int getCargoOperationTodoCount(String condition) {
			return getXXXCount(condition, "cargo_operation_todo", "id");
		}

		public CargoOperationTodoBean getCargoOperationTodo(String condition) {
			return (CargoOperationTodoBean) getXXX(condition, "cargo_operation_todo",
			"mmb.stock.cargo.CargoOperationTodoBean");
		}

		public boolean updateCargoOperationTodo(String set, String condition) {
			return updateXXX(set, condition, "cargo_operation_todo");
		}

		public boolean deleteCargoOperationTodo(String condition) {
			return deleteXXX(condition, "cargo_operation_todo");
		}

		/**
		 * 获取最大装箱单号，为以后可能出现的同步问题做铺垫
		 * @return
		 *//*
		public String getMaxCartonningCode() {
			
			String code = "ZX" + DateUtil.getNow().substring(2, 4)+DateUtil.getNow().substring(5, 7)+DateUtil.getNow().substring(8, 10);
			int maxid = this.getNumber("id", "cartonning_info", "max", "id > 0");
			CartonningInfoBean lastCode = this.getCartonningInfo("code like '" + code + "%'");
			if(lastCode == null){
				//当日第一份单据，编号最后四位 0001
				code += "0001";
			}else {
				//获取当日计划编号最大值
				lastCode = this.getCartonningInfo("id =" + maxid); 
				String _code = lastCode.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-4));
					if(number==9999){
						return "-1";
					}
				number++;
				code+= String.format("%04d",new Object[]{new Integer(number)});
			}
			return code;
		}*/

		/**
		 * 找出 传来的货位中 剩余体积最大的那一个
		 * @param commonAvailList
		 * @return
		 */
		/*public CargoInfoBean getMostLeftVolumeCargo(
				List<CargoInfoBean> commonAvailList) {
			if( commonAvailList == null ) {
				return null;
			}
			int x = commonAvailList.size();
			if( x != 0 ) {
				// 计算货位剩余体积 返回数组
				CargoInfoBean[] cargos = calculateVolumeArray(commonAvailList);
				// 对有剩余体积后的 cargo数组排序
				cargos = rankCargoArrayByLeftVolume(cargos);
				return cargos[0];
			} else {
				return null;
			}
		}*/

		/**
		 * 排序 按照剩余体积 从大到小排列
		 * @param cargos
		 * @return
		 */
		private CargoInfoBean[] rankCargoArrayByLeftVolume(
				CargoInfoBean[] cargos) {
			int x = cargos.length;
			for( int i = 0; i < x; i++ ) {
				for( int j = x - 1; j > i; j --) {
					if( cargos[j].getLeftVolume() > cargos[j - 1 ].getLeftVolume() ) {
						CargoInfoBean temp = cargos[j];
						cargos[j] = cargos[j-1];
						cargos[j-1] = temp;
					}
				}
			}
			return cargos;
		}

		/**
		 * 给以个 ArrayList 的Cargo 计算 体积和剩余体积 并且返回 数组
		 * @param commonAvailList
		 * @return
		 */
		private CargoInfoBean[] calculateVolumeArray(List<CargoInfoBean> commonAvailList) {
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
			int x = commonAvailList.size();
			CargoInfoBean[] cargos = new CargoInfoBean[x];
			for (int i = 0; i < x; i++ ) {
				CargoInfoBean ciBean = (CargoInfoBean)commonAvailList.get(i);
				List cpsList = cargoService.getCargoProductStockList("cargo_id="+ciBean.getId()+ " and stock_count > 0", -1, -1, "id asc");
				long cargoVolume = ciBean.calculateVolume();
				long usedVolume = getUsedVolume(cpsList);
				long leftVolume = cargoVolume - usedVolume;
				ciBean.setCargoVolume(cargoVolume);
				ciBean.setLeftVolume(leftVolume);
				cargos[i] = ciBean;
			}
			return cargos;
		}

		/**
		 *  对一个货位 计算 其上所有结存商品的总体积（可用+冻结）
		 * @param cpsList
		 * @return
		 */
		private long getUsedVolume(List cpsList) {
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.dbOp);
			int x = cpsList.size();
			long usedVolume = 0;
			for( int i = 0 ; i < x; i++ ) {
				CargoProductStockBean cpsBean = (CargoProductStockBean) cpsList.get(i);
				ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id = " + cpsBean.getProductId());
				//找不到商品物流属性的话  商品的体积按照0 算
				long productVolume = 0;
				if( pwpBean != null ) {
					productVolume = pwpBean.calculateVolume();
				}
				long totalProductVolume = (cpsBean.getStockCount() + cpsBean.getStockLockCount()) * productVolume;
				usedVolume += totalProductVolume;
			}
			return usedVolume;
		}

		public List<CargoInfoBean> choseCartonningNumber(List<CargoInfoBean> availList,
				int number) throws Exception {
			
			List<CargoInfoBean> list = new ArrayList<CargoInfoBean>();
			if( availList == null ) {
				return list;
			}
			int x = availList.size();
			if(  x == 0 ) {
				return list;
			} else {
				for( int i = 0; i < x; i++ ) {
					CargoInfoBean ciBean = availList.get(i);
					int cartonningCount=getCartonningCount("cargo_id='"+ciBean.getId()+"' and status="+CartonningInfoBean.STATUS1);
					//以该货位为目标货位的上架单数
					int operCount=0;
					String tempSql="select count(distinct co.id) from cargo_operation co "+
						"join cargo_operation_cargo coc on co.id=coc.oper_id "+
						"where co.status in (1,2,3,4,5,6) "+
						"and co.effect_status in (0,1) "+
						"and co.type=0 "+
						"and coc.in_cargo_whole_code='"+ciBean.getWholeCode()+"'";
					ResultSet tempRs=this.dbOp.executeQuery(tempSql);
					if(tempRs.next()){
						operCount=tempRs.getInt(1);
					}
					tempRs.close();
					if(cartonningCount+operCount>=number){//装箱单数加上架单数大于30，选择另外的货位
						continue;
					}else{
						list.add(ciBean);
					}
				}
				return list;
			}
		}

		public CargoInfoBean getMinLeftVolumeCargo(
				List<CargoInfoBean> commonAvailList, long need) {
			if( commonAvailList == null ) {
				return null;
			}
			int x = commonAvailList.size();
			if( x != 0 ) {
				// 计算货位剩余体积 返回数组
				CargoInfoBean[] cargos = calculateVolumeArray(commonAvailList);
				// 对有剩余体积后的 cargo数组排序
				cargos = rankCargoArrayByLeftVolume2(cargos);
				if( cargos == null ) {
					return null;
				} else {
					for( int i = 0 ; i < cargos.length; i++ ) {
						CargoInfoBean cargoInfo = cargos[i];
						//一遇到 最小的 符合的 就返回
						if( ( cargoInfo.getLeftVolume() - need ) >=  0 )  {
							return cargoInfo;
						} else {
							continue;
						}
					}
					//如果 最终也没有返回  就返回 null
					return null;
				}
			} else {
				return null;
			}
		}
		
		/**
		 * 排序 按照剩余体积 从小到大排
		 * @param cargos
		 * @return
		 */
		private CargoInfoBean[] rankCargoArrayByLeftVolume2(
				CargoInfoBean[] cargos) {
			int x = cargos.length;
			for( int i = 0; i < x; i++ ) {
				for( int j = x - 1; j > i; j --) {
					if( cargos[j].getLeftVolume() < cargos[j - 1 ].getLeftVolume() ) {
						CargoInfoBean temp = cargos[j];
						cargos[j] = cargos[j-1];
						cargos[j-1] = temp;
					}
				}
			}
			return cargos;
		}
		
		public String getLastInsertCode(String code){
			String code1 = "";
			DbOperation dbop = getDbOp();
			ResultSet rs = null;
			String sql = "SELECT  t.code FROM cartonning_info t WHERE t.code like '"+code+"%' order by t.id desc LIMIT 1" ;
			rs = dbop.executeQuery(sql);
			try{
			if(rs.next()){
				code1 = rs.getString(1);
			}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				release(dbop);
			}
			return code1;
			
		}
		
		/**
		 * 根据dbOp的上一个插入的id来修改code的后五位后缀为id 的后五位不满五位的补零。
		 * @param dbOp
		 * @param brefCode
		 * @return
		 */
		public String getFixedCartonningInfoCode(String brefCode, int id){
			String totalCode = null;
			
			//将刚添加的装箱单的id的后五位截取，并添到日期code之后
			String newCode = null;
			if(id > 99999){
				String strId = String.valueOf(id);
				newCode = strId.substring(strId.length()- 5, strId.length());
			} else {
				DecimalFormat df2 = new DecimalFormat("00000");
				newCode = df2.format(id);
			}
			totalCode = brefCode + newCode;
			
			return totalCode;
		}
		
		/**
		 * 根据传来的装箱单的bean和对应的dbop得到最后的 修改数据库中装箱单的编号后缀为 id 后五位并付给bean的code属性，
		 * @param brefCode
		 * @param dbOp
		 * @param bean
		 * @return
		 */
		public boolean fixCartonningInfoCode(String brefCode,DbOperation dbOp, CartonningInfoBean bean) {
			if( brefCode == null ) {
				return false;
			}
			CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
			try {
				int id = dbOp.getLastInsertId();
				String totalCode = this.getFixedCartonningInfoCode(brefCode, id);
				if( totalCode == null ) {
					return false;
				}
				StringBuilder updateBuf = new StringBuilder();
				updateBuf.append("update cartonning_info set code='" + totalCode + "' where id=").append(id);
				if( !service.getDbOp().executeUpdate(updateBuf.toString())) {
					return false;
				}
				bean.setCode(totalCode);
			} catch(Exception e ) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		/**
		 * 获得  ZX‘YYMMdd’这样的code的方法
		 * @return
		 */
		public String getZXCodeForToday(){
			String code = "ZX" + DateUtil.getNow().substring(2, 4)+DateUtil.getNow().substring(5, 7)+DateUtil.getNow().substring(8, 10);
			return code;
		}
}
