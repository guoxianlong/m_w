package mmb.system.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mmb.stock.cargo.CargoDeptAreaBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.util.BinaryFlag;
import mmb.util.Secure;
import mmb.ware.WareService;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.framework.PermissionFrk;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class AdminService extends BaseServiceImpl {
    public AdminService(int useConnType, DbOperation dbOp) {
        this.useConnType = useConnType;
        this.dbOp = dbOp;
    }    
    
    public AdminService() {
        this.useConnType = CONN_IN_SERVICE;
        this.dbOp = new DbOperation();
        this.dbOp.init(DbOperation.DB);
    }
    
    // 用于登录，如果用户存在并且密码正确则返回用户bean
    public voUser getAdmin(String username, String password) {
    	voUser vo = getAdmin(username);
    	if(vo == null)
    		return null;
    	if(vo.getPassword().equals(Secure.encryptPwd(password)))
    		return vo;
    	return null;
    }
    
    // 根据用户名获取管理员账号
    public voUser getAdmin(String username) {
		voUser vo = null;
		try {

			ResultSet rs = dbOp.executeQuery("select * from admin_user u join user_permission up on u.id=up.user_id where username='" + StringUtil.toSql(username) + "' and security_level>=5");
			if (rs!=null && rs.next()) {
				vo = new voUser();
				vo.setId(rs.getInt("id"));
				vo.setNick(rs.getString("nick"));
				vo.setSecurityLevel(rs.getInt("security_level"));
				vo.setPermission(rs.getInt("permission"));
				vo.setGroupId(rs.getInt("group_id"));
				vo.setGroupId2(rs.getInt("group_id2"));
				vo.setGroupId3(rs.getInt("group_id3"));
				vo.setGroups(PermissionFrk.string2ints(rs.getString("groups")));
				vo.setBinaryFlag(new BinaryFlag(rs.getBytes("flags")));
				vo.setUsername(username);
				vo.setPassword(rs.getString("password"));
				vo.setOtpTime(rs.getLong("otp_time"));
				vo.setOtpPassword(rs.getString("otp_password"));
				vo.setOtpKey(rs.getString("otp_key"));
				vo.setCookieHash(rs.getString("cookie_hash"));
				vo.setRootIds(rs.getString("root_ids"));
				vo.setIsDisable(rs.getBoolean("is_disable"));
				vo.setAlone(rs.getInt("alone"));
				vo.setCargoStaffBean(getCargoStaff(dbOp, vo));
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return vo;
	}
    
    //得到物流员工及其所属库地区和库类型
    public CargoStaffBean getCargoStaff(DbOperation dbOp,voUser vo){
    	WareService wareService = new WareService(dbOp);
    	ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CargoDeptAreaService cargoDeptAreaService = ServiceFactory.createCargoDeptAreaService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
    	CargoStaffBean cargoStaffBean = service.getCargoStaff("status=0 and user_id=" + vo.getId());
		if (cargoStaffBean != null) {
//			String staffCode = cargoStaffBean.getCode();
//			if (staffCode != null && staffCode.length() > 0) {
//				String dept0 = staffCode.substring(0, 2);//员工所属0级部门编号
//				String dept1 = staffCode.substring(2, 4);//员工所属1级部门编号
//				String dept2 = staffCode.substring(4, 6);//员工所属2级部门编号
//				String dept3 = staffCode.substring(6, 8);//员工所属3级部门编号
//				int deptId0 = 0;//员工所属0级部门id
//				int deptId1 = 0;//员工所属1级部门id
//				int deptId2 = 0;//员工所属2级部门id
//				int deptId3 = 0;//员工所属3级部门id
//				CargoDeptBean cargoDept0 = service.getCargoDept("code='" + dept0 + "' and parent_id0=0 and parent_id1=0 and parent_id2=0 and parent_id3=0");
//				if (cargoDept0 != null) {
//					deptId0 = cargoDept0.getId();
//					CargoDeptBean cargoDept1 = service.getCargoDept("code='" + dept1 + "' and parent_id0=" + deptId0 + " and parent_id1=0 and parent_id2=0 and parent_id3=0");
//					if (cargoDept1 != null) {
//						deptId1 = cargoDept1.getId();
//						CargoDeptBean cargoDept2 = service.getCargoDept("code='" + dept2 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=0 and parent_id3=0");
//						if (cargoDept2 != null) {
//							deptId2 = cargoDept2.getId();
//							CargoDeptBean cargoDept3 = service.getCargoDept("code='" + dept3 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=" + deptId2 + " and parent_id3=0");
//							if (cargoDept3 != null) {
//								deptId3 = cargoDept3.getId();
//							}else if(dept3.equals("00")){//如果查询不到3级部门，并且员工3级部门编号为"00"则员工所属部门id为2级部门id
//								deptId3 = cargoDept2.getId();
//							}
//						} else if(dept2.equals("00")){//如果查询不到2级部门，并且员工2级部门编号为"00"则员工所属部门id为1级部门id
//							deptId3 = cargoDept1.getId();
//						}
//					} else if(dept1.equals("00")){//如果查询不到1级部门，并且员工1级部门编号为"00"则员工所属部门id为0级部门id
//						deptId3 = cargoDept0.getId();
//					}
//				}
				List<?> cargoDeptAreaList = cargoDeptAreaService.getCargoDeptAreaList("dept_id=" + cargoStaffBean.getDeptId(), -1, -1, "id");
				List<?> cargoDeptList = cargoDeptAreaList;
				List<String> cdList = new ArrayList<String>();
				if (cargoDeptList != null && cargoDeptList.size()>0) {
					for (int i = 0; i < cargoDeptList.size(); i++) {
						CargoDeptAreaBean cargoDeptAreaBean = (CargoDeptAreaBean) cargoDeptList.get(i);
						String code = cargoDeptAreaBean.getArea() + "-" + cargoDeptAreaBean.getStockType();
						cdList.add(code);
					}
				}
				cargoStaffBean.setCargoDeptAreaList(cdList);
			//}
		}
    	return cargoStaffBean;
    }
    

    // 根据用户名id管理员账号
    public voUser getAdmin(int id) {
		voUser vo = null;
		DbOperation dbOp = getDbOp(DbOperation.DB_SLAVE);
		try {

			ResultSet rs = dbOp.executeQuery("select * from admin_user u join user_permission up on u.id=up.user_id where u.id=" + id + " and security_level>=5");
			if (rs!=null && rs.next()) {
				vo = new voUser();
				vo.setId(rs.getInt("id"));
				vo.setNick(rs.getString("nick"));
				vo.setSecurityLevel(rs.getInt("security_level"));
				vo.setPermission(rs.getInt("permission"));
				vo.setGroupId(rs.getInt("group_id"));
				vo.setGroupId2(rs.getInt("group_id2"));
				vo.setGroupId3(rs.getInt("group_id3"));
				vo.setGroups(PermissionFrk.string2ints(rs.getString("groups")));
				vo.setBinaryFlag(new BinaryFlag(rs.getBytes("flags")));
				vo.setUsername(rs.getString("u.username"));
				vo.setPassword(rs.getString("password"));
				vo.setOtpTime(rs.getLong("otp_time"));
				vo.setOtpPassword(rs.getString("otp_password"));
				vo.setOtpKey(rs.getString("otp_key"));
				vo.setCookieHash(rs.getString("cookie_hash"));
				vo.setAlone(rs.getInt("alone"));
				vo.setCargoStaffBean(getCargoStaff(dbOp, vo));
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			this.release(dbOp);
		}
		return vo;
	}
}
