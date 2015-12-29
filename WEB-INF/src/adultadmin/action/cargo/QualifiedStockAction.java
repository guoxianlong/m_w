package adultadmin.action.cargo;

import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CargoOperationTodoBean;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.cargo.CargoDeptBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoStorageBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CargoOperationProcessCache;

public class QualifiedStockAction extends DispatchAction {

	public static byte[] cargoLock = new byte[0];
	public static byte[] todoLock = new byte[0];

	//员工档案列表
	public ActionForward staffManagement(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			int countPerPage = 20;
			int pageIndex = 0;
			String condition = StringUtil.convertNull(request.getParameter("condition"));
			String kw = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("kw")));
			if(Encoder.decrypt(kw)==null){//第一次查询，未编码
				kw=Encoder.encrypt(kw);
			}
			String deptId = StringUtil.convertNull(request.getParameter("id"));
			String deptId2 = StringUtil.convertNull(request.getParameter("id2"));
			String deptId3 = StringUtil.convertNull(request.getParameter("id3"));
			String deptId4 = StringUtil.convertNull(request.getParameter("id4"));
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0, countPerPage);
            String para = "";
            if(request.getParameter("para")!=null){
            	para=request.getParameter("para");
            }else{
            	para=(condition.equals("") ? "" : ("&condition=" + condition))
		            	+(kw.equals("") ? "" : ("&kw=" + kw))
		            	+(deptId.equals("") ? "" : ("&id=" + deptId))
		            	+(deptId2.equals("") ? "" : ("&id2=" + deptId2))
		            	+(deptId3.equals("") ? "" : ("&id3=" + deptId3))
		            	+(deptId4.equals("") ? "" : ("&id4=" + deptId4));
            }
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("qualifiedStock.do?method=staffManagement"+para);
            
			List cargoStaffList = new ArrayList();
			List cargoStaffList2 = new ArrayList();
			if(request.getParameter("id") != null){ //按部门查询
				String code = "";
				if( !deptId.equals("")) {
						CargoDeptBean cdb = service.getCargoDept("id=" + deptId);
						if(  cdb!= null ) {
							code += cdb.getCode();
						}
					if(!deptId2.equals("")){
						code = code + service.getCargoDept("id=" + deptId2).getCode();
						if(!deptId3.equals("")){
							code = code + service.getCargoDept("id=" + deptId3).getCode();
							if(!deptId4.equals("")){
								code = code + service.getCargoDept("id=" + deptId4).getCode();
							}
						}
					}
				}
				
				while(cargoStaffList.size()==0&&pageIndex>=0){
					cargoStaffList = service.getCargoStaffList("code like '" + code + "%'", pageIndex * countPerPage, countPerPage, "create_datetime desc");
					if(cargoStaffList.size()==0&&pageIndex>=0){
						pageIndex--;
						paging.setCurrentPageIndex(pageIndex);
					}
				}
				cargoStaffList2 = service.getCargoStaffList("code like '" + code + "%'", -1, -1, "create_datetime desc");
			}else{
				if( "".equals(condition)){ //查询所有
					cargoStaffList = service.getCargoStaffList("id>0", pageIndex * countPerPage, countPerPage, "create_datetime desc");
					cargoStaffList2 = service.getCargoStaffList("id>0", -1, -1, "create_datetime desc");
				}else{ //按指定条件查询
					String conditionStr = "1 = 1";
					kw=Encoder.decrypt(kw);//解码
					if(!"dept_name".equals(condition)){
						if("create_datetime".equals(condition)){
							conditionStr = condition + " like '" + kw + "%'";
						}else{
							conditionStr = condition + "='" + kw +"'";
						}
					}else{
						List staffAllList=service.getCargoStaffList("id>0", -1, -1, null);//所有员工
						String ids="";//符合条件的员工id
						for(int i=0;i<staffAllList.size();i++){//分别拼出部门全称
							CargoStaffBean csb = (CargoStaffBean)staffAllList.get(i);
							CargoDeptBean deptBean = service.getCargoDept("id="+csb.getDeptId());
							String deptName0 = "";
							String deptName1 = "";
							String deptName2 = "";
							String deptName3 = "";
							if (deptBean.getParentId0() != 0) {
								deptName0 = service.getCargoDept("id=" + deptBean.getParentId0()).getName();
								if (deptBean.getParentId1() != 0) {
									deptName1 = service.getCargoDept("id=" + deptBean.getParentId1()).getName();
									if (deptBean.getParentId2() != 0) {
										deptName2 = service.getCargoDept("id=" + deptBean.getParentId2()).getName();
										deptName3 = service.getCargoDept("id=" + deptBean.getId()).getName();
									} else {
										deptName2 = service.getCargoDept("id=" + deptBean.getId()).getName();
									}
								} else {
									deptName1 = service.getCargoDept("id=" + deptBean.getId()).getName();
								}
							} else {
								deptName0 = service.getCargoDept("id=" + deptBean.getId()).getName();
							}
							//}
							String deptName = deptName0 + deptName1 + deptName2 + deptName3;
							if("物流中心".equals(deptName)){
								csb.setDeptName("物流中心直属");
							}
							if(deptName.indexOf(kw)>=0){//部门全程中包含查询条件
								ids+=csb.getId()+",";
							}
						}
						if(ids.length()>0){
							ids=ids.substring(0,ids.length()-1);//去掉最后的","
							conditionStr+=" and id in ("+ids+")";
						}else{
							conditionStr+=" and 1=2";
						}
						
					}
					while(cargoStaffList.size()==0&&pageIndex>=0){
						cargoStaffList = service.getCargoStaffList(conditionStr, pageIndex * countPerPage, countPerPage, "create_datetime desc");
						if(cargoStaffList.size()==0&&pageIndex>=0){
							pageIndex--;
							paging.setCurrentPageIndex(pageIndex);
						}
					}
					
					cargoStaffList2 = service.getCargoStaffList(conditionStr, -1, -1, "create_datetime desc");
					if(cargoStaffList.size() == 0){
						request.setAttribute("tip", "无法找到您查询的内容！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
			}
			if(cargoStaffList != null){
				for(int i = 0; i < cargoStaffList.size(); i++){
					CargoStaffBean csb = (CargoStaffBean)cargoStaffList.get(i);
					CargoDeptBean deptBean = service.getCargoDept("id="+csb.getDeptId());
					String deptName1 = "";
					String deptName2 = "";
					String deptName3 = "";
					String deptName0 = "";
					
					if(deptBean!=null){
						if( deptBean.getParentId0()!=0){
							deptName0 = service.getCargoDept("id="+deptBean.getParentId0()).getName();
							if(deptBean.getParentId1()!=0){
								deptName1 = service.getCargoDept("id="+deptBean.getParentId1()).getName();
								if(deptBean.getParentId2()!=0){
									deptName2 = service.getCargoDept("id="+deptBean.getParentId2()).getName();
									deptName3 = service.getCargoDept("id="+deptBean.getId()).getName();
								}else{
									deptName2 = service.getCargoDept("id="+deptBean.getId()).getName();
								}
							}
							else{
								deptName1 = service.getCargoDept("id="+deptBean.getId()).getName();
							}
						}else{
							deptName0 = service.getCargoDept("id="+deptBean.getId()).getName();
						}
					}
					//}
					
					String deptName = deptName0 + deptName1 + deptName2 + deptName3;
					if("物流中心".equals(deptName)){
						csb.setDeptName("物流中心直属");
					}else{
						csb.setDeptName(deptName);
					}
				}
			}
			
			List deptList=service.getCargoDeptList("parent_id0=0", -1, -1, null);//零级部门列表
			for(int i=0;i<deptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)deptList.get(i);
				List deptList2=service.getCargoDeptList("parent_id0="+cd.getId()+" and parent_id1=0", -1, -1, null);//该部门的一级部门列表
				cd.setJuniorDeptList(deptList2);
				for(int j=0;j<deptList2.size();j++){
					CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
					List deptList3=service.getCargoDeptList("parent_id1="+cd2.getId()+" and parent_id2=0", -1, -1, null);//该部门的二级部门列表
					cd2.setJuniorDeptList(deptList3);
					for(int k=0;k<deptList3.size();k++){
						CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k);
						List deptList4=service.getCargoDeptList("parent_id2="+cd3.getId()+" and parent_id3=0", -1, -1, null);//该部门的三级部门列表
						cd3.setJuniorDeptList(deptList4);
					}
				}
			}
			int totalCount = cargoStaffList2.size();
			int totalPageCount = totalCount % countPerPage == 0 ? totalCount / countPerPage : ((totalCount - totalCount % countPerPage) / countPerPage + 1);
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(totalPageCount);
			request.setAttribute("paging", paging);
			request.setAttribute("para", para);
			request.setAttribute("cargoStaffList", cargoStaffList);
			request.setAttribute("deptList", deptList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("staffManagement");
	}
	
	/**
	 * 添加员工头像图片
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 */
	public void addStaffPhoto(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String imageUrl = "";
		String imageHead = "";
		String accountName = StringUtil.convertNull(request.getParameter("accountName"));
		int id = StringUtil.parstInt(request.getParameter("staffId"));
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(cargoLock) {
				String localPath = Constants.WARE_UPLOAD;
				String serverHead = Constants.STAFF_PHOTO_URL;
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				if( accountName.equals("") ) {
					response.getWriter().write("{status:'fail', tip:'用户没有已保存的后台账户信息！'}");
					return;
				}
				File warePath = new File(localPath + "ware");
				if (!warePath.exists()) {
					warePath.mkdirs();
				}
				File staffPhotoPath = new File(localPath + "ware/" + "staffPhoto");
				if (!staffPhotoPath.exists()) {
					staffPhotoPath.mkdirs();
				}
				String subPath = "ware/staffPhoto/";
				String temp = localPath + subPath; // 上传文件的临时路径
				String real = localPath + subPath; // 上传文件的路径
				String newPath = "";
				final long MAX_SIZE = 80 * 1024; // 文件的小的限定

				String[] allowedExt = new String[] { "jpg", "png"};
				DiskFileItemFactory dfif = new DiskFileItemFactory(); // 硬盘工厂
				dfif.setSizeThreshold(4096); // 设置内存缓冲区
				dfif.setRepository(new File(temp)); // 设置临时路径给
				ServletFileUpload sfu = new ServletFileUpload(dfif); // 用sfu实例工厂产生
				sfu.setSizeMax(MAX_SIZE); // 设置最大文件
				List fileList = null;
				request.setCharacterEncoding("UTF-8"); // 设置得到的数据的编码类型
				try {
					fileList = sfu.parseRequest(request); // 将得到的转成list
				} catch (FileUploadException e) {
					if (e instanceof SizeLimitExceededException) // 防止文件过大
					{
						response.getWriter().write("{status:'fail', tip:'文件不可以大于80K!'}");
						return;
					}
					e.printStackTrace();
				}
				if (fileList == null || fileList.size() == 0) // 未传文件
				{
					response.getWriter().write("{status:'fail', tip:'没有收到上传文件'}");
					return;
				}
				Iterator it = fileList.iterator();

				while (it.hasNext()) // 逐个拿出文件做处理
				{
					FileItem fileItem = null;
					String orName = null;
					long size = 0;
					
					fileItem = (FileItem) it.next();
					if (fileItem == null || fileItem.isFormField()) // 排除其中的form中的部分
					{
						if (fileItem != null && fileItem.isFormField()
								&& fileItem.getName() != null
								&& fileItem.getName().equals("imageUrlStr")) {
						}
						continue;
					}

					orName = fileItem.getName();
					size = fileItem.getSize();
					if (orName.equals("") && size == 0) {
						response.getWriter().write("{status:'fail', tip:'文件类型无法识别!'}");
						return;
					}

					String newName = StringUtil.toSql(accountName);
					String dir = DateUtil.formatDate(new Date(), "yyyy");
					File datePath = new File(real + dir);
					if (!datePath.exists()) {
						datePath.mkdir();
					}
					String _ext = orName.substring(orName.lastIndexOf(".") + 1);
					_ext = _ext.toLowerCase();

					int allowFlag = 0; // 特殊的验证是否是可上传文件
					int allowCount = allowedExt.length;
					for (; allowFlag < allowCount; allowFlag++) {
						if (allowedExt[allowFlag].equals(_ext)) {
							break;
						}
					}
					if (allowFlag == allowCount) {
						response.getWriter().write("{status:'fail', tip:'文件类型不是jpg,png格式的！'}");
						return;
					}
					String newPathWithNoType = real + dir + "/" + newName + ".";
					newPath = real + dir + "/" + newName + "." + _ext;
					String fileName = newName + "." + _ext;
					//添加先删除原有文件的步骤
					if( newPathWithNoType.indexOf("ware/staffPhoto") != -1 ) {
						for( int k = 0; k < allowedExt.length; k++ ) {
							String tempType = newPathWithNoType + allowedExt[k];
							   boolean result = false;
							   File image = new File(tempType);
							   if( image.exists() ) {
								  result = image.delete();
							   } else {
								   result = true;
							   }
							if( !result ) {
								response.getWriter().write("{status:'fail', tip:'原头像图片删除失败!'}");
								return;
							}
						}
					} else {
						response.getWriter().write("{status:'fail', tip:'图片存放路径有误！'}");
						return;
					}
					imageUrl = subPath + dir + "/" + fileName;
					fileItem.write(new File(newPath));
					imageHead = serverHead + "/";
					//如果有id 就实时更新对应的图片
					if( id != 0 ) {
						CargoStaffBean csBean = service.getCargoStaff("id=" + id + " and  user_name='" + StringUtil.toSql(accountName)+"'");
						if( csBean != null ) {
							if( !service.updateCargoStaff("photo_url='" + imageUrl + "'", "id=" + csBean.getId())) {
								response.getWriter().write("{status:'fail', tip:'关联图片的数据库操作失败!'}");
								return;
							}
						} else {
							response.getWriter().write("{status:'fail', tip:'未找到图片应关联的对象!'}");
							return;
						}
					}
				}
			}
			if( !imageUrl.equals("")) {
				response.getWriter().write("{status:'success', url:'" + imageUrl + "', serverHead:'"+imageHead+ "'}");
				return;
			} else {
				response.getWriter().write("{status:'fail', tip:'图片上传出错'}");
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}


	// 添加员工档案页面
	public ActionForward toAddStaff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		try {
			List deptList0=service.getCargoDeptList("parent_id0=0 and parent_id1=0", -1, -1, null);
			request.setAttribute("deptList0", deptList0);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("addStaff");
	}
	
	// 添加员工档案
	public ActionForward addStaff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int id = 0;
		WareService wareService = new WareService();
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(cargoLock) {
				service.getDbOp().startTransaction();
				if (request.getParameter("add") != null) {
					String name = "";
					String code = "";
					String createDatetime = "";
					String phone = "";
					int userId = 0;
					String userName = "";
					String deptCode0 = "00";
					String deptCode1 = "00";
					String deptCode2 = "00";
					String deptCode3 = "00";
					if (!"".equals(request.getParameter("deptCode0"))) {
						deptCode0 = request.getParameter("deptCode0");
					}
					if (!"".equals(request.getParameter("deptCode1"))) {
						deptCode1 = request.getParameter("deptCode1");
					}
					if (!"".equals(request.getParameter("deptCode2"))) {
						deptCode2 = request.getParameter("deptCode2");
					}
					if (!"".equals(request.getParameter("deptCode3"))) {
						deptCode3 = request.getParameter("deptCode3");
					}
					CargoDeptBean cdb0 = service.getCargoDept("parent_id0 = 0 and code =" + deptCode0);
					int deptId =0;
					int id0 = cdb0.getId();
					deptId=cdb0.getId();
					if(!"00".equals(deptCode1)){
						CargoDeptBean cdb1 = service.getCargoDept("parent_id0 = " + id0 + " and parent_id1 = 0 and code =" + deptCode1);
						int id1 = cdb1.getId();
						deptId=cdb1.getId();
						if(!"00".equals(deptCode2)){
							CargoDeptBean cdb2 = service.getCargoDept("parent_id1 =" + id1 + " and parent_id2 = 0 and code =" + deptCode2);
							int id2 = cdb2.getId();
							deptId=cdb2.getId();
							if(!"00".equals(deptCode3)){
								CargoDeptBean cdb3 = service.getCargoDept("parent_id1 =" + id1 + " and parent_id2 =" + id2 + " and parent_id3 = 0 and code =" + deptCode3);
								deptId=cdb3.getId();
							}
						}
					}
					
					if (request.getParameter("nameStr") != null && 
							request.getParameter("nameStr").trim() !="") {
						name = request.getParameter("nameStr").trim();
					}
					createDatetime = DateUtil.getNow();
					if (request.getParameter("phone") != null) {
						phone = "输入电话号码".equals(request.getParameter("phone")) ? "" : request.getParameter("phone").trim();
					}
					if (request.getParameter("userName") != null) {
						String tempUserName = "输入账号...".equals(request.getParameter("userName")) ? "" : request.getParameter("userName").trim();
						if(!"".equals(tempUserName)){
							if(adminService.getAdmin(tempUserName) != null){
								userId = adminService.getAdmin(tempUserName).getId();
								userName = tempUserName;
							}else{
								request.setAttribute("tip", "后台帐户输入不正确！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if( service.getCargoStaff("status=0 and user_name='" + tempUserName + "'") != null ) {
								request.setAttribute("tip", "该后台账户已经被别的物流员工使用了！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							else if( service.getCargoStaff("status=1 and user_name='" + tempUserName + "'") != null ) {
								CargoStaffBean staffBean = service.getCargoStaff("status=1 and user_name='" + tempUserName + "'");
								service.updateCargoStaff("status=0,dept_id="+deptId+",phone='"+phone+"',name='"+name+"'", "user_name='" + tempUserName + "'");
								return new ActionForward("/admin/qualifiedStock.do?method=editStaff&id="+staffBean.getId()+"&type=2");
							}
						}
					}
					// 拼接出合适员工编号
					
					String condition = "code like '" + deptCode0 + deptCode1 + deptCode2 + deptCode3 + "%' and length(code)=12 ";
					String subCode = "0001";	//员工编号末两位
					int intSubCode = 0;
					
					CargoStaffBean staffBean = service.getCargoStaff(condition + "  ORDER BY code DESC LIMIT 1");
					if (staffBean != null) {
						int num = StringUtil.StringToId(staffBean.getCode().substring(8, staffBean.getCode().length()));
						intSubCode = num + 1;
						if (intSubCode < 10) {
							subCode = "000" + intSubCode;
						} else if (10 <= intSubCode && intSubCode <= 99) {
							subCode = "00" + intSubCode;
						} else if (100 <= intSubCode && intSubCode <= 999) {
							subCode = "0" + intSubCode;
						} else {
							subCode = String.valueOf(intSubCode);
						}
					}

					code = deptCode0 + deptCode1 + deptCode2 + deptCode3 + subCode;

					if (!"".equals(name)) {
						CargoStaffBean csb = new CargoStaffBean();
						csb.setName(name);
						csb.setCode(code);
						csb.setDeptId(deptId);
						csb.setCreateDatetime(createDatetime);
						csb.setPhone(phone);
						csb.setUserId(userId);
						csb.setUserName(userName);
						service.addCargoStaff(csb);
					}
				}
				id = service.getDbOp().getLastInsertId();
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
			wareService.releaseAll();
		}
		return new ActionForward("/admin/qualifiedStock.do?method=editStaff&id="+id+"&type=2");
	}
	
	// 添加员工档案的下一步
	public ActionForward addStaffNext(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int id = StringUtil.parstInt(request.getParameter("id"));
		String photoUrl = StringUtil.convertNull(request.getParameter("photoUrl"));
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(cargoLock) {
				if( !photoUrl.equals("") && !photoUrl.equals("null")) {
					service.getDbOp().startTransaction();
					CargoStaffBean csBean = service.getCargoStaff("id=" + id);
					if( csBean == null ) {
						request.setAttribute("tip", "找不到用户信息，操作失败！");
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					} else {
						if( !service.updateCargoStaff("photo_url='" + photoUrl + "'", "id=" + id)) {
							request.setAttribute("tip", "保存图片时，数据库操作失败！");
							service.getDbOp().rollbackTransaction();
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					service.getDbOp().commitTransaction();
					service.getDbOp().getConn().setAutoCommit(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("toStaffManagement");
	}
	
	// 编辑员工档案
	public ActionForward editStaff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int type = StringUtil.parstInt(request.getParameter("type"));
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		try {
			//保存修改后的员工档案
			if (request.getParameter("save") != null && type == 0) {
				synchronized(cargoLock) {
					String photoUrl = StringUtil.convertNull(request.getParameter("photoUrl"));
					service.getDbOp().startTransaction();
					int id = 0;
					String name = StringUtil.convertNull(request.getParameter("nameStr").trim());
					String phone = StringUtil.convertNull(request.getParameter("phone"));
					int userId = 0;
					String userName = StringUtil.convertNull(request.getParameter("userName").trim());
					String deptCode0 = "00";
					String deptCode1 = "00";
					String deptCode2 = "00";
					String deptCode3 = "00";
					
					if(request.getParameter("id") != null){
						id = StringUtil.toInt(request.getParameter("id"));
					}
					if(!"".equals(userName)){
						if(adminService.getAdmin(userName) != null){
							userId = adminService.getAdmin(userName).getId();
						}else{
							request.setAttribute("tip", "后台帐户输入不正确！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoStaffBean csTemp = service.getCargoStaff("status=0 and user_name='" + userName + "'");
						if( csTemp != null && csTemp.getId() != id  ) {
							request.setAttribute("tip", "该后台账户已经被别的物流员工使用了！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					if (!"".equals(request.getParameter("deptCode0"))) {
						deptCode0 = request.getParameter("deptCode0");
					}
					if (!"".equals(request.getParameter("deptCode1"))) {
						deptCode1 = request.getParameter("deptCode1");
					}
					if (!"".equals(request.getParameter("deptCode2"))) {
						deptCode2 = request.getParameter("deptCode2");
					} 
					if (!"".equals(request.getParameter("deptCode3"))) {
						deptCode3 = request.getParameter("deptCode3");
					}
					
					int deptId0 = 0;//员工所属0级部门id
					int deptId1 = 0;//员工所属1级部门id
					int deptId2 = 0;//员工所属2级部门id
					int deptId3 = 0;//员工所属3级部门id
					CargoDeptBean cargoDept0 = service.getCargoDept("code='" + deptCode0 + "' and parent_id0=0 and parent_id1=0 and parent_id2=0 and parent_id3=0");
					if (cargoDept0 != null) {
						deptId0 = cargoDept0.getId();
						CargoDeptBean cargoDept1 = service.getCargoDept("code='" + deptCode1 + "' and parent_id0=" + deptId0 + " and parent_id1=0 and parent_id2=0 and parent_id3=0");
						if (cargoDept1 != null) {
							deptId1 = cargoDept1.getId();
							CargoDeptBean cargoDept2 = service.getCargoDept("code='" + deptCode2 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=0 and parent_id3=0");
							if (cargoDept2 != null) {
								deptId2 = cargoDept2.getId();
								CargoDeptBean cargoDept3 = service.getCargoDept("code='" + deptCode3 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=" + deptId2 + " and parent_id3=0");
								if (cargoDept3 != null) {
									deptId3 = cargoDept3.getId();
								}else if(deptCode3.equals("00")){//如果查询不到3级部门，并且员工3级部门编号为"00"则员工所属部门id为2级部门id
									deptId3 = cargoDept2.getId();
								}
							} else if(deptCode2.equals("00")){//如果查询不到2级部门，并且员工2级部门编号为"00"则员工所属部门id为1级部门id
								deptId3 = cargoDept1.getId();
							}
						} else if(deptCode1.equals("00")){//如果查询不到1级部门，并且员工1级部门编号为"00"则员工所属部门id为0级部门id
							deptId3 = cargoDept0.getId();
						}
					}
					String set = "name ='" + name + "',dept_id =" + deptId3 + ",phone ='" + phone + "',user_id=" + userId + ",user_name='" + userName + "'";
					if( !photoUrl.equals("") && !photoUrl.equals("null") ) {
						set += ", photo_url='" + photoUrl + "'";
					} 
					service.updateCargoStaff(set, " id =" + id);
					service.getDbOp().commitTransaction();
				}
			return mapping.findForward("toStaffManagement");	

			//读出待修改的员工档案
			}else if(request.getParameter("id") != null){ 
				int id = StringUtil.toInt(request.getParameter("id"));
				CargoStaffBean csb = service.getCargoStaff("id =" + id);
				CargoDeptBean deptBean = service.getCargoDept("id="+csb.getDeptId());
				List cargoDeptList = new ArrayList();
				if(deptBean!=null){
					if( deptBean.getParentId0()!=0){
						cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId0()));
						if(deptBean.getParentId1()!=0){
							cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId1()));
							if(deptBean.getParentId2()!=0){
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId2()));
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));
							}
							else{
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
							}
						}else{
							cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
						}
					}else{
						cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
					}
				}
					
				//}
				List deptList0=service.getCargoDeptList("parent_id0 = 0 and parent_id1=0", -1, -1, null);
				request.setAttribute("deptList0", deptList0);
				request.setAttribute("csb", csb);
				request.setAttribute("cargoDeptList", cargoDeptList);
				if(type == 0 ) {
					return mapping.findForward("editStaff");
				} else {
					return mapping.findForward("addStaffNext");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return null;
	}
	
	// 删除员工档案
	public ActionForward delStaff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		String kw=request.getParameter("kw");
		String condition=request.getParameter("condition");
		try {
			synchronized ( cargoLock) {
				service.getDbOp().startTransaction();
				int id =0;
				if(request.getParameter("staffId") != null){
					id = StringUtil.toInt(request.getParameter("staffId"));
				}
				CargoStaffBean csBean = service.getCargoStaff("id=" + id );
				if( csBean == null ) {
					request.setAttribute("tip", "没有找到要删除的用户信息，操作失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					if( csBean.getPhotoUrl() != null && !csBean.getPhotoUrl().equals("")) {
						String localHead = Constants.WARE_UPLOAD;
						String allPath = localHead + csBean.getPhotoUrl();
						if( allPath.indexOf("ware/staffPhoto") != -1 ) {
							   boolean result = false;
							   File image = new File(allPath);
							   if( image.exists() ) {
								  result = image.delete();
							   } else {
								   result = true;
							   }
							if( !result ) {
								request.setAttribute("tip", "头像图片删除失败");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						} else {
							request.setAttribute("tip", "图片路径有错误");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					if( !service.updateCargoStaff("status=1", "id =" + id) ) {
						request.setAttribute("tip", "删除用户信息时，数据库操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		String result="/admin/qualifiedStock.do?method=staffManagement";
		if(kw!=null){
			result+="&kw="+kw;
		}
		if(condition!=null){
			result+="&condition="+condition;
		}
		return new ActionForward(result);	
	}
	// 恢复删除员工
	public ActionForward recoverStaff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		try {
			synchronized ( cargoLock) {
				service.getDbOp().startTransaction();
				int id =StringUtil.toInt(request.getParameter("id"));
				service.updateCargoStaff("status=0", "id="+id);
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		String result="/admin/qualifiedStock.do?method=staffManagement";
		return new ActionForward(result);	
	}
	
	//合格库作业动态明细
	public ActionForward qualifiedStockDetail(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			List qualifiedStockDetailList = new ArrayList(); // 存放主信息
			List cargoOperationCargoList = new ArrayList(); // 存放货位编号的list
			
			String code = StringUtil.convertNull(request.getParameter("code"));
			String _date = StringUtil.convertNull(request.getParameter("date"));
			if(_date.equals("")){
				_date = DateUtil.getNowDateStr();
			}
			
			String con = "confirm_datetime>'"+_date+" 00:00:00' and confirm_datetime<'"+_date+" 23:59:59'";
			if(!code.equals("")){
				con = con + " and code = '" + code + "'";
			}
			
			int pageIndex=StringUtil.StringToId(request.getParameter("pageIndex"));//分页页码
			int countPerPage=20;
			PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);//标签‘全部’的分页
			int totalCount=service.getCargoOperationCount(con);
			paging.setTotalCount(totalCount);
			int totalPageCount=totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1);
			paging.setTotalPageCount(totalPageCount);
			paging.setPrefixUrl("#");
			paging.setJsFunction("shaixuan('0','pageIndex');");
			
			qualifiedStockDetailList = service.getCargoOperationList(con, pageIndex*countPerPage, countPerPage, "confirm_datetime desc");
			if(!code.equals("")&&qualifiedStockDetailList.size()==0){
				request.setAttribute("tip", "没有找到作业单，请核实编号！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			for(int i = 0; i < qualifiedStockDetailList.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)qualifiedStockDetailList.get(i);
				int id = cob.getId();
				int status = cob.getStatus();

				//货位名称
				CargoOperationCargoBean cocb = new CargoOperationCargoBean();
				List cargoOperationCargoList1 = service.getCargoOperationCargoList("out_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList1 != null && cargoOperationCargoList1.size() > 0){
					cocb = (CargoOperationCargoBean)cargoOperationCargoList1.get(0); //源货位bean
				}
				List cargoOperationCargoList2 = service.getCargoOperationCargoList("in_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList2 != null && cargoOperationCargoList2.size() > 0){
					String inCargo = ((CargoOperationCargoBean)cargoOperationCargoList2.get(0)).getInCargoWholeCode();
					cocb.setInCargoWholeCode(inCargo); //目的货位
				}
				cargoOperationCargoList.add(cocb);

				//作业状态名称
				CargoOperationProcessBean copb = service.getCargoOperationProcess("id =" + status); 
				if(copb != null){
					cob.setStatusName(StringUtil.convertNull(copb.getStatusName())); 
				}

			}
			
			List operList1=new ArrayList();//上架单列表
			List operList2=new ArrayList();//下架单列表
			List operList3=new ArrayList();//补货单列表
			List operList4=new ArrayList();//调拨单列表
			List operCocList1=new ArrayList();//上架单列表
			List operCocList2=new ArrayList();//下架单列表
			List operCocList3=new ArrayList();//补货单列表
			List operCocList4=new ArrayList();//调拨单列表
			
			PagingBean paging1 = new PagingBean(pageIndex, 0,countPerPage);//标签‘上架单’的分页
			totalCount=service.getCargoOperationCount(con+" and type = 0");
			paging1.setTotalCount(totalCount);
			totalPageCount=totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1);
			paging1.setTotalPageCount(totalPageCount);
			paging1.setPrefixUrl("#");
			paging1.setJsFunction("shaixuan('4','pageIndex');");
			
			operList1 = service.getCargoOperationList(con+" and type = 0", pageIndex*countPerPage, countPerPage, "confirm_datetime desc");
			for(int i = 0; i < operList1.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList1.get(i);
				int id = cob.getId();
				int status = cob.getStatus();

				//货位名称
				CargoOperationCargoBean cocb = new CargoOperationCargoBean();
				List cargoOperationCargoList1 = service.getCargoOperationCargoList("out_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList1 != null && cargoOperationCargoList1.size() > 0){
					cocb = (CargoOperationCargoBean)cargoOperationCargoList1.get(0); //源货位bean
				}
				List cargoOperationCargoList2 = service.getCargoOperationCargoList("in_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList2 != null && cargoOperationCargoList2.size() > 0){
					String inCargo = ((CargoOperationCargoBean)cargoOperationCargoList2.get(0)).getInCargoWholeCode();
					cocb.setInCargoWholeCode(inCargo); //目的货位
				}
				operCocList1.add(cocb);

				//作业状态名称
				CargoOperationProcessBean copb = service.getCargoOperationProcess("id =" + status); 
				if(copb != null){
					cob.setStatusName(StringUtil.convertNull(copb.getStatusName())); 
				}

			}
			
			PagingBean paging2 = new PagingBean(pageIndex, 0,countPerPage);//标签‘下架单’的分页
			totalCount=service.getCargoOperationCount(con+" and type = 1");
			paging2.setTotalCount(totalCount);
			totalPageCount=totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1);
			paging2.setTotalPageCount(totalPageCount);
			paging2.setPrefixUrl("#");
			paging2.setJsFunction("shaixuan('1','pageIndex');");
			
			operList2 = service.getCargoOperationList(con+" and type = 1", pageIndex*countPerPage, countPerPage, "confirm_datetime desc");
			for(int i = 0; i < operList2.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList2.get(i);
				int id = cob.getId();
				int status = cob.getStatus();

				//货位名称
				CargoOperationCargoBean cocb = new CargoOperationCargoBean();
				List cargoOperationCargoList1 = service.getCargoOperationCargoList("out_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList1 != null && cargoOperationCargoList1.size() > 0){
					cocb = (CargoOperationCargoBean)cargoOperationCargoList1.get(0); //源货位bean
				}
				List cargoOperationCargoList2 = service.getCargoOperationCargoList("in_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList2 != null && cargoOperationCargoList2.size() > 0){
					String inCargo = ((CargoOperationCargoBean)cargoOperationCargoList2.get(0)).getInCargoWholeCode();
					cocb.setInCargoWholeCode(inCargo); //目的货位
				}
				operCocList2.add(cocb);

				//作业状态名称
				CargoOperationProcessBean copb = service.getCargoOperationProcess("id =" + status); 
				if(copb != null){
					cob.setStatusName(StringUtil.convertNull(copb.getStatusName())); 
				}

			}
			
			PagingBean paging3 = new PagingBean(pageIndex, 0,countPerPage);//标签‘补货单单’的分页
			totalCount=service.getCargoOperationCount(con+" and type = 2");
			paging3.setTotalCount(totalCount);
			totalPageCount=totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1);
			paging3.setTotalPageCount(totalPageCount);
			paging3.setPrefixUrl("#");
			paging3.setJsFunction("shaixuan('2','pageIndex');");
			
			operList3 = service.getCargoOperationList(con+" and type = 2", pageIndex*countPerPage, countPerPage, "confirm_datetime desc");
			for(int i = 0; i < operList3.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList3.get(i);
				int id = cob.getId();
				int status = cob.getStatus();

				//货位名称
				CargoOperationCargoBean cocb = new CargoOperationCargoBean();
				List cargoOperationCargoList1 = service.getCargoOperationCargoList("out_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList1 != null && cargoOperationCargoList1.size() > 0){
					cocb = (CargoOperationCargoBean)cargoOperationCargoList1.get(0); //源货位bean
				}
				List cargoOperationCargoList2 = service.getCargoOperationCargoList("in_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList2 != null && cargoOperationCargoList2.size() > 0){
					String inCargo = ((CargoOperationCargoBean)cargoOperationCargoList2.get(0)).getInCargoWholeCode();
					cocb.setInCargoWholeCode(inCargo); //目的货位
				}
				operCocList3.add(cocb);

				//作业状态名称
				CargoOperationProcessBean copb = service.getCargoOperationProcess("id =" + status); 
				if(copb != null){
					cob.setStatusName(StringUtil.convertNull(copb.getStatusName())); 
				}

			}
			
			PagingBean paging4 = new PagingBean(pageIndex, 0,countPerPage);//标签‘货位间调拨单’的分页
			totalCount=service.getCargoOperationCount(con+" and type = 3");
			paging4.setTotalCount(totalCount);
			totalPageCount=totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1);
			paging4.setTotalPageCount(totalPageCount);
			paging4.setPrefixUrl("#");
			paging4.setJsFunction("shaixuan('3','pageIndex');");
			
			operList4 = service.getCargoOperationList(con+" and type = 3", pageIndex*countPerPage, countPerPage, "confirm_datetime desc");
			for(int i = 0; i < operList4.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList4.get(i);
				int id = cob.getId();
				int status = cob.getStatus();

				//货位名称
				CargoOperationCargoBean cocb = new CargoOperationCargoBean();
				List cargoOperationCargoList1 = service.getCargoOperationCargoList("out_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList1 != null && cargoOperationCargoList1.size() > 0){
					cocb = (CargoOperationCargoBean)cargoOperationCargoList1.get(0); //源货位bean
				}
				List cargoOperationCargoList2 = service.getCargoOperationCargoList("in_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList2 != null && cargoOperationCargoList2.size() > 0){
					String inCargo = ((CargoOperationCargoBean)cargoOperationCargoList2.get(0)).getInCargoWholeCode();
					cocb.setInCargoWholeCode(inCargo); //目的货位
				}
				operCocList4.add(cocb);

				//作业状态名称
				CargoOperationProcessBean copb = service.getCargoOperationProcess("id =" + status); 
				if(copb != null){
					cob.setStatusName(StringUtil.convertNull(copb.getStatusName())); 
				}

			}
			
			request.setAttribute("cargoOperationCargoList", cargoOperationCargoList);
			request.setAttribute("qualifiedStockDetailList", qualifiedStockDetailList);
			request.setAttribute("operList1", operList1);
			request.setAttribute("operList2", operList2);
			request.setAttribute("operList3", operList3);
			request.setAttribute("operList4", operList4);
			request.setAttribute("operCocList1", operCocList1);
			request.setAttribute("operCocList2", operCocList2);
			request.setAttribute("operCocList3", operCocList3);
			request.setAttribute("operCocList4", operCocList4);
			request.setAttribute("paging", paging);
			request.setAttribute("paging1", paging1);
			request.setAttribute("paging2", paging2);
			request.setAttribute("paging3", paging3);
			request.setAttribute("paging4", paging4);
			
			//添加图表信息
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE, -29);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			List dateList=new ArrayList();
			List operCountList1=new ArrayList();
			List operCountList2=new ArrayList();
			List operCountList3=new ArrayList();
			for(int i=0;i<30;i++){
				String date=sdf.format(cal.getTime());
				String condition1="create_datetime>'"+sdf.format(cal.getTime())+" 00:00:00'" +
						" and create_datetime<'"+sdf.format(cal.getTime())+" 23:59:59' " +
						"and status in(2,3,4,5,6,11,12,13,14,15,20,21,22,23,24,29,30,31,32,33) and effect_status in (0,1)";//未完成作业单
				String condition2="create_datetime>'"+sdf.format(cal.getTime())+" 00:00:00'" +
						" and create_datetime<'"+sdf.format(cal.getTime())+" 23:59:59' " +
						"and status in (7,8,9,16,17,18,25,26,27,34,35,36) and effect_status in (2,3,4)";//已完成作业单
				String condition3="create_datetime>'"+sdf.format(cal.getTime())+" 00:00:00'" +
						" and create_datetime<'"+sdf.format(cal.getTime())+" 23:59:59' " +
						"and effect_status=4";//作业失败订单
				int operCount1=service.getCargoOperationCount(condition1);
				int operCount2=service.getCargoOperationCount(condition2);
				int operCount3=service.getCargoOperationCount(condition3);
				dateList.add(date);
				operCountList1.add(operCount1+"");
				operCountList2.add(operCount2+"");
				operCountList3.add(operCount3+"");
				
				cal.add(Calendar.DATE, 1);
			}
			request.setAttribute("dateList", dateList);
			request.setAttribute("operCountList1", operCountList1);
			request.setAttribute("operCountList2", operCountList2);
			request.setAttribute("operCountList3", operCountList3);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("qualifiedStockDetail");
	}
	
	//维护组织结构，部门管理
	public ActionForward deptManagement(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String deptId=request.getParameter("deptId");//部门Id
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			List deptList=service.getCargoDeptList("parent_id0=0", -1, -1, null);//零级部门列表
			for(int i=0;i<deptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)deptList.get(i);
				List deptList2=service.getCargoDeptList("parent_id0="+cd.getId()+" and parent_id1=0", -1, -1, null);//该部门的一级部门列表
				cd.setJuniorDeptList(deptList2);
				for(int j=0;j<deptList2.size();j++){
					CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
					List deptList3=service.getCargoDeptList("parent_id1="+cd2.getId()+" and parent_id2=0", -1, -1, null);//该部门的二级部门列表
					cd2.setJuniorDeptList(deptList3);
					for(int k=0;k<deptList3.size();k++){
						CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k);
						List deptList4=service.getCargoDeptList("parent_id2="+cd3.getId()+" and parent_id3=0", -1, -1, null);//该部门的三级部门列表
						cd3.setJuniorDeptList(deptList4);
					}
				}
			}
			
			CargoDeptBean dept=null;
			if(deptId!=null){
				dept=service.getCargoDept("id="+deptId);
				if(dept==null){
					request.setAttribute("tip", "无此部门！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}else{
				dept=service.getCargoDept("parent_id0=0");//零级分类，物流中心
			}
			List detailDeptList=new ArrayList();//右边可更改的部门列表
			if(dept!=null){
				if(dept.getParentId0()==0){//零级分类
					detailDeptList=service.getCargoDeptList("parent_id0="+dept.getId()+" and parent_id1=0", -1, -1, null);
				}else if(dept.getParentId1()==0){//一级分类
					detailDeptList=service.getCargoDeptList("parent_id1="+dept.getId()+" and parent_id2=0", -1, -1, null);
				}else if(dept.getParentId2()==0){//二级分类
					detailDeptList=service.getCargoDeptList("parent_id2="+dept.getId()+" and parent_id3=0", -1, -1, null); 
				}else if(dept.getParentId3()==0){//三级分类
					detailDeptList=service.getCargoDeptList("parent_id3="+dept.getId(), -1, -1, null); 
				}
			}
			request.setAttribute("deptList", deptList);
			request.setAttribute("dept", dept);
			request.setAttribute("detailDeptList", detailDeptList);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("deptManagement");
	}
	
	//修改部门
	public ActionForward updateDept(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String deptId=request.getParameter("deptId");//上级部门Id
		String[] nameList=request.getParameterValues("deptName");//部门名称列表
		String[] codeList=request.getParameterValues("deptCode");//部门代码列表
		
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			List deptList=new ArrayList();
			CargoDeptBean dept=null;//上级部门
			if(deptId==null){//一级分类列表
				dept=service.getCargoDept("parent_id0=0");//零级分类，物流中心
			}else{
				dept=service.getCargoDept("id="+deptId);
			}
			if(dept==null){
				request.setAttribute("tip", "上级部门不存在，无法修改！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(dept.getParentId0()==0){//零级分类
				deptList=service.getCargoDeptList("parent_id0="+dept.getId()+" and parent_id1=0", -1, -1, "id asc");
			}else if(dept.getParentId0()!=0&&dept.getParentId1()==0){//一级分类
				deptList=service.getCargoDeptList("parent_id1="+deptId+" and parent_id2=0", -1, -1, "id asc");
			}else if(dept.getParentId0()!=0&&dept.getParentId1()!=0&&dept.getParentId2()==0){//二级分类
				deptList=service.getCargoDeptList("parent_id2="+deptId+" and parent_id3=0", -1, -1, "id asc");
			}
			
			for(int i=0;i<(nameList.length>deptList.size()?nameList.length:deptList.size());i++){
				String name="";//输入框的中输入的第i个部门名字
				String code="";//输入框的中输入的第i个部门代码
				CargoDeptBean cargoDept=null;//数据库中的第i个部门
				if(nameList.length>=i+1){
					name=nameList[i];
					code=codeList[i];
				}
				if(deptList.size()>=i+1){
					cargoDept=(CargoDeptBean)deptList.get(i);
				}
				if(name.equals("")&&cargoDept!=null){//输入框清空，数据库里有值，应删除数据库中该部门记录
					//删除部门
					int parentId0=cargoDept.getParentId0();
					int parentId1=cargoDept.getParentId1();
					int parentId2=cargoDept.getParentId2();
					int parentId3=cargoDept.getParentId3();
					String staffCode="";
					if(parentId0!=0){//有零级上级部门
						CargoDeptBean dept0=service.getCargoDept("id="+parentId0);
						if(dept0==null){
							request.setAttribute("tip", "零级部门不存在，操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						staffCode+=dept0.getCode();
						if(parentId1!=0){//有一级上级部门
							CargoDeptBean dept1=service.getCargoDept("id="+parentId1);
							if(dept1==null){
								request.setAttribute("tip", "一级部门不存在，操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							staffCode+=dept1.getCode();
							if(parentId2!=0){//有二级上级部门
								CargoDeptBean dept2=service.getCargoDept("id="+parentId2);
								if(dept2==null){
									request.setAttribute("tip", "二级级部门不存在，操作失败！");
									request.setAttribute("result", "failure");
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								staffCode+=dept2.getCode();
								if(parentId3!=0){//有三级上级部门
									CargoDeptBean dept3=service.getCargoDept("id="+parentId3);
									if(dept3==null){
										request.setAttribute("tip", "三级部门不存在，操作失败！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									staffCode+=dept3.getCode();
								}
							}
						}
						staffCode+=cargoDept.getCode();
					}else{
						staffCode+="00";
					}
					int cargoStaffCount=service.getCargoStaffCount("status=0 and code like '"+staffCode+"%'");//
					if(cargoStaffCount!=0){
						request.setAttribute("tip", staffCode+"部门中仍有员工，不能删除");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					service.deleteCargoDept("id="+cargoDept.getId());
				}else if(!name.equals("")&&cargoDept==null){//输入框有值，数据库无记录，应添加部门记录
					//添加部门
					int parentId0=dept.getParentId0();//上级部门的零级上级部门Id
					int parentId1=dept.getParentId1();
					int parentId2=dept.getParentId2();
					int parentId3=dept.getParentId3();
					CargoDeptBean newDept=new CargoDeptBean();//新部门
					newDept.setName(name);
					newDept.setCode(code);
					if(parentId0==0){//上级部门是零级部门
						newDept.setParentId0(dept.getId());
						newDept.setParentId1(0);
						newDept.setParentId2(0);
						newDept.setParentId3(0);
					}else if(parentId1==0){//上级部门是一级部门
						newDept.setParentId0(dept.getParentId0());
						newDept.setParentId1(dept.getId());
						newDept.setParentId2(0);
						newDept.setParentId3(0);
					}else if(parentId2==0){//上级部门是二级部门
						newDept.setParentId0(dept.getParentId0());
						newDept.setParentId1(dept.getParentId1());
						newDept.setParentId2(dept.getId());
						newDept.setParentId3(0);
					}else if(parentId3==0){//上级部门是三级部门
						newDept.setParentId0(dept.getParentId0());
						newDept.setParentId1(dept.getParentId1());
						newDept.setParentId2(dept.getParentId2());
						newDept.setParentId3(dept.getId());
					}
					List tempDeptList=service.getCargoDeptList("1=1", -1, -1, "id desc");
					if(tempDeptList.size()>0){
						CargoDeptBean tempDept=(CargoDeptBean)tempDeptList.get(0);
						newDept.setId(tempDept.getId()+1);
					}
					service.addCargoDept(newDept);
				}else if(!name.equals("")&&cargoDept!=null){//输入框有值，数据库有记录，应修改部门记录
					//修改部门
					List sameDeptList=new ArrayList();
					if(cargoDept.getParentId0()!=0&&cargoDept.getParentId1()==0){//一级分类
						sameDeptList=service.getCargoDeptList("parent_id0="+cargoDept.getParentId0()+" and parent_id1=0 and code='"+code+"' and id!="+cargoDept.getId(), -1, -1, null);
					}else if(cargoDept.getParentId1()!=0&&cargoDept.getParentId2()==0){//二级分类
						sameDeptList=service.getCargoDeptList("parent_id1="+cargoDept.getParentId1()+" and parent_id2=0 and code='"+code+"' and id!="+cargoDept.getId(), -1, -1, null);
					}else if(cargoDept.getParentId2()!=0&&cargoDept.getParentId3()==0){//三级分类
						sameDeptList=service.getCargoDeptList("parent_id2="+cargoDept.getParentId2()+" and parent_id3=0 and code='"+code+"' and id!="+cargoDept.getId(), -1, -1, null);
					}
					if(sameDeptList.size()>0){
						request.setAttribute("tip", "部门编号有重复，修改失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					service.updateCargoDept("name='"+name+"',code='"+code+"'", "id="+cargoDept.getId());
					String tempCode="";//该部门完整编号
					CargoDeptBean tempDept0=service.getCargoDept("id="+cargoDept.getParentId0());//零级部门
					if(tempDept0!=null){
						tempCode+=tempDept0.getCode();
					}
					CargoDeptBean tempDept1=service.getCargoDept("id="+cargoDept.getParentId1());//一级部门
					if(tempDept1!=null){
						tempCode+=tempDept1.getCode();
					}
					CargoDeptBean tempDept2=service.getCargoDept("id="+cargoDept.getParentId2());//二级部门
					if(tempDept2!=null){
						tempCode+=tempDept2.getCode();
					}
					CargoDeptBean tempDept3=service.getCargoDept("id="+cargoDept.getParentId3());//三级部门
					if(tempDept3!=null){
						tempCode+=tempDept3.getCode();
					}
					tempCode+=cargoDept.getCode();
					List staffList=service.getCargoStaffList("status=0 and code like '"+tempCode+"%'", -1, -1, null);//该部门下所有员工
					for(int j=0;j<staffList.size();j++){
						CargoStaffBean staff=(CargoStaffBean)staffList.get(j);
						String staffCode=staff.getCode();
						String code1="";
						String code2="";
						String code3="";
						//1234567890
						if(cargoDept.getParentId0()!=0&&cargoDept.getParentId1()==0){//一级分类
							code1=staffCode.substring(0,2);
							code2=code;
							code3=staffCode.substring(4,10);
						}else if(cargoDept.getParentId1()!=0&&cargoDept.getParentId2()==0){//二级分类
							code1=staffCode.substring(0,4);
							code2=code;
							code3=staffCode.substring(6,10);
						}else if(cargoDept.getParentId2()!=0&&cargoDept.getParentId3()==0){//三级分类
							code1=staffCode.substring(0,6);
							code2=code;
							code3=staffCode.substring(8,10);
						}
						//service.updateCargoStaff("code='"+code1+code2+code3+"'", "id="+staff.getId());
					}
				}
			}
			service.getDbOp().commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return new ActionForward("/admin/qualifiedStock.do?method=deptManagement&id="+deptId);
	}
	
	//删除部门
	public ActionForward deleteDept(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String deptId=request.getParameter("deptId");//待删除部门Id
		
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			CargoDeptBean cargoDept=service.getCargoDept("id="+deptId);
			if(cargoDept==null){
				request.setAttribute("tip", "部门不存在，无法删除！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int parentId0=cargoDept.getParentId0();
			int parentId1=cargoDept.getParentId1();
			int parentId2=cargoDept.getParentId2();
			int parentId3=cargoDept.getParentId3();
			String staffCode="";
			if(parentId0!=0){//有零级上级部门
				CargoDeptBean dept0=service.getCargoDept("id="+parentId0);
				if(dept0==null){
					request.setAttribute("tip", "零级部门不存在，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				staffCode+=dept0.getCode();
				if(parentId1!=0){//有一级上级部门
					CargoDeptBean dept1=service.getCargoDept("id="+parentId1);
					if(dept1==null){
						request.setAttribute("tip", "一级部门不存在，操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					staffCode+=dept1.getCode();
					if(parentId2!=0){//有二级上级部门
						CargoDeptBean dept2=service.getCargoDept("id="+parentId2);
						if(dept2==null){
							request.setAttribute("tip", "二级级部门不存在，操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						staffCode+=dept2.getCode();
						if(parentId3!=0){//有三级上级部门
							CargoDeptBean dept3=service.getCargoDept("id="+parentId3);
							if(dept3==null){
								request.setAttribute("tip", "三级部门不存在，操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							staffCode+=dept3.getCode();
						}
					}
				}
				staffCode+=cargoDept.getCode();
			}else{
				staffCode+="00";
			}
			int cargoStaffCount=service.getCargoStaffCount("code like '"+staffCode+"%'");//
			if(cargoStaffCount!=0){
				request.setAttribute("tip", staffCode+"部门中仍有员工，不能删除");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.deleteCargoDept("parent_id0="+cargoDept.getId());
			service.deleteCargoDept("parent_id1="+cargoDept.getId());
			service.deleteCargoDept("parent_id2="+cargoDept.getId());
			service.deleteCargoDept("parent_id3="+cargoDept.getId());
			service.deleteCargoDept("id="+cargoDept.getId());
			service.getDbOp().commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return new ActionForward("/admin/qualifiedStock.do?method=deptManagement",true);
	}
	
	//作业单操作流程和时效设置列表
	public ActionForward cargoOperationProcessList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			List processListS=service.getCargoOperationProcessList("operation_type=1", -1, -1, null);//上架单流程
			List processListX=service.getCargoOperationProcessList("operation_type=2", -1, -1, null);//下架单流程
			List processListB=service.getCargoOperationProcessList("operation_type=3", -1, -1, null);//补货单流程
			List processListD=service.getCargoOperationProcessList("operation_type=4", -1, -1, null);//调拨单流程
			List storageList=service.getCargoInfoStorageList("1=1", -1, -1, null);//所有仓库列表
			List deptList0 = service.getCargoDeptList("parent_id0 = 0 and parent_id1= 0", -1, -1, null);
			List deptList1=service.getCargoDeptList("parent_id0!=0 and parent_id1=0", -1, -1, null);//一级分类列表
			
			int useStatusCountS=0;//上架单流程交接阶段，useStatus=1的数量
			int useStatusCountX=0;
			int useStatusCountB=0;
			int useStatusCountD=0;
			for(int i=2;i<6;i++){
				CargoOperationProcessBean processS=(CargoOperationProcessBean)processListS.get(i);
				CargoOperationProcessBean processX=(CargoOperationProcessBean)processListX.get(i);
				CargoOperationProcessBean processB=(CargoOperationProcessBean)processListB.get(i);
				CargoOperationProcessBean processD=(CargoOperationProcessBean)processListD.get(i);
				if(processS.getUseStatus()==1){
					useStatusCountS++;
				}
				if(processX.getUseStatus()==1){
					useStatusCountX++;
				}
				if(processB.getUseStatus()==1){
					useStatusCountB++;
				}
				if(processD.getUseStatus()==1){
					useStatusCountD++;
				}
			}
			
			request.setAttribute("processListS", processListS);
			request.setAttribute("processListX", processListX);
			request.setAttribute("processListB", processListB);
			request.setAttribute("processListD", processListD);
			request.setAttribute("storageList", storageList);
			request.setAttribute("deptList0", deptList0);
			request.setAttribute("deptList1", deptList1);
			request.setAttribute("useStatusCountS", useStatusCountS+"");
			request.setAttribute("useStatusCountX", useStatusCountX+"");
			request.setAttribute("useStatusCountB", useStatusCountB+"");
			request.setAttribute("useStatusCountD", useStatusCountD+"");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("cargoOperationProcessList");
	}
	
	//修改作业单操作流程和时效设置
	public ActionForward updateProcess(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String formIndex=request.getParameter("formIndex");//1：生成阶段，2：交接阶段，3：结束阶段
		String operationType=request.getParameter("operationType");//1：上架单，2：下架单，3：补货:4：调拨单
		
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			List processList=service.getCargoOperationProcessList("operation_type="+operationType, -1, -1, null);
			if(formIndex.equals("1")){
				String effectTime1=request.getParameter("effectTime1");
				String effectTime2=request.getParameter("effectTime2");
				CargoOperationProcessBean bean1=(CargoOperationProcessBean)processList.get(0);
				CargoOperationProcessBean bean2=(CargoOperationProcessBean)processList.get(1);
				
				service.updateCargoOperationProcess("effect_time="+effectTime1, "id="+bean1.getId());
				service.updateCargoOperationProcess("effect_time="+effectTime2, "id="+bean2.getId());
			}else if(formIndex.equals("2")){
				int processCount=Integer.parseInt(request.getParameter("processCount"));//交接阶段阶段数量
				CargoOperationProcessBean bean1=(CargoOperationProcessBean)processList.get(2);
				CargoOperationProcessBean bean2=(CargoOperationProcessBean)processList.get(3);
				CargoOperationProcessBean bean3=(CargoOperationProcessBean)processList.get(4);
				CargoOperationProcessBean bean4=(CargoOperationProcessBean)processList.get(5);
				if(processCount>0){//至少一阶
					String operName1=request.getParameter("operName1");
					String statusName1=request.getParameter("statusName1");
					String effectTime3=request.getParameter("effectTime3");
					String deptId1=request.getParameter("deptId1");
					String deptId2=request.getParameter("deptId2");
					String handleType1=request.getParameter("handleType1");
					String storageId1=request.getParameter("storageId1");
					String confirmType1=request.getParameter("confirmType1");
					
					StringBuilder set=new StringBuilder();
					set.append("oper_name='"+operName1+"'");
					set.append(", status_name='"+statusName1+"'");
					set.append(", effect_time="+effectTime3);
					set.append(", dept_id1="+deptId1);
					set.append(", dept_id2="+deptId2);
					set.append(", handle_type="+handleType1);
					set.append(", storage_id="+storageId1);
					set.append(", confirm_type="+confirmType1);
					set.append(", use_status=1");
					service.updateCargoOperationProcess(set.toString(), "id="+bean1.getId());
					//把234阶use_status设为0
					service.updateCargoOperationProcess("use_status=0", "id="+bean2.getId()+" or id="+bean3.getId()+" or id="+bean4.getId());
				}
				if(processCount>1){//至少二阶
					String operName2=request.getParameter("operName2");
					String statusName2=request.getParameter("statusName2");
					String effectTime4=request.getParameter("effectTime4");
					String deptId3=request.getParameter("deptId3");
					String deptId4=request.getParameter("deptId4");
					String handleType2=request.getParameter("handleType2");
					String storageId2=request.getParameter("storageId2");
					String confirmType2=request.getParameter("confirmType2");
					
					StringBuilder set=new StringBuilder();
					set.append("oper_name='"+operName2+"'");
					set.append(", status_name='"+statusName2+"'");
					set.append(", effect_time="+effectTime4);
					set.append(", dept_id1="+deptId3);
					set.append(", dept_id2="+deptId4);
					set.append(", handle_type="+handleType2);
					set.append(", storage_id="+storageId2);
					set.append(", confirm_type="+confirmType2);
					set.append(", use_status=1");
					service.updateCargoOperationProcess(set.toString(), "id="+bean2.getId());
					//把34阶use_status设为0
					service.updateCargoOperationProcess("use_status=0", "id="+bean3.getId()+" or id="+bean4.getId());
				}
				if(processCount>2){//至少三阶
					String operName3=request.getParameter("operName3");
					String statusName3=request.getParameter("statusName3");
					String effectTime5=request.getParameter("effectTime5");
					String deptId5=request.getParameter("deptId5");
					String deptId6=request.getParameter("deptId6");
					String handleType3=request.getParameter("handleType3");
					String storageId3=request.getParameter("storageId3");
					String confirmType3=request.getParameter("confirmType3");
					
					StringBuilder set=new StringBuilder();
					set.append("oper_name='"+operName3+"'");
					set.append(", status_name='"+statusName3+"'");
					set.append(", effect_time="+effectTime5);
					set.append(", dept_id1="+deptId5);
					set.append(", dept_id2="+deptId6);
					set.append(", handle_type="+handleType3);
					set.append(", storage_id="+storageId3);
					set.append(", confirm_type="+confirmType3);
					set.append(", use_status=1");
					service.updateCargoOperationProcess(set.toString(), "id="+bean3.getId());
					//把234阶use_status设为0
					service.updateCargoOperationProcess("use_status=0", "id="+bean4.getId());
				}
				if(processCount>3){//至少四阶
					String operName4=request.getParameter("operName4");
					String statusName4=request.getParameter("statusName4");
					String effectTime6=request.getParameter("effectTime6");
					String deptId7=request.getParameter("deptId7");
					String deptId8=request.getParameter("deptId8");
					String handleType4=request.getParameter("handleType4");
					String storageId4=request.getParameter("storageId4");
					String confirmType4=request.getParameter("confirmType4");
					
					StringBuilder set=new StringBuilder();
					set.append("oper_name='"+operName4+"'");
					set.append(", status_name='"+statusName4+"'");
					set.append(", effect_time="+effectTime6);
					set.append(", dept_id1="+deptId7);
					set.append(", dept_id2="+deptId8);
					set.append(", handle_type="+handleType4);
					set.append(", storage_id="+storageId4);
					set.append(", confirm_type="+confirmType4);
					set.append(", use_status=1");
					service.updateCargoOperationProcess(set.toString(), "id="+bean4.getId());
				}
			}else if(formIndex.equals("3")){
				String effectTime7=request.getParameter("effectTime7");
				CargoOperationProcessBean bean1=(CargoOperationProcessBean)processList.get(6);
				
				service.updateCargoOperationProcess("effect_time="+effectTime7, "id="+bean1.getId());
			}
			
			List storageList=service.getCargoInfoStorageList("1=1", -1, -1, null);//所有仓库列表
			List deptList1=service.getCargoDeptList("parent_id0!=0 and parent_id1=0", -1, -1, null);//一级分类列表
			List processListS=service.getCargoOperationProcessList("operation_type=1", -1, -1, null);//上架单流程
			List processListX=service.getCargoOperationProcessList("operation_type=2", -1, -1, null);//下架单流程
			List processListB=service.getCargoOperationProcessList("operation_type=3", -1, -1, null);//补货单流程
			List processListD=service.getCargoOperationProcessList("operation_type=4", -1, -1, null);//调拨单流程
			
			int useStatusCountS=0;//上架单流程交接阶段，useStatus=1的数量
			int useStatusCountX=0;
			int useStatusCountB=0;
			int useStatusCountD=0;
			for(int i=2;i<6;i++){
				CargoOperationProcessBean processS=(CargoOperationProcessBean)processListS.get(i);
				CargoOperationProcessBean processX=(CargoOperationProcessBean)processListX.get(i);
				CargoOperationProcessBean processB=(CargoOperationProcessBean)processListB.get(i);
				CargoOperationProcessBean processD=(CargoOperationProcessBean)processListD.get(i);
				if(processS.getUseStatus()==1){
					useStatusCountS++;
				}
				if(processX.getUseStatus()==1){
					useStatusCountX++;
				}
				if(processB.getUseStatus()==1){
					useStatusCountB++;
				}
				if(processD.getUseStatus()==1){
					useStatusCountD++;
				}
			}
			
			CargoOperationProcessCache.init();//重置缓存
			
			request.setAttribute("processListS", processListS);
			request.setAttribute("processListX", processListX);
			request.setAttribute("processListB", processListB);
			request.setAttribute("processListD", processListD);
			request.setAttribute("storageList", storageList);
			request.setAttribute("deptList1", deptList1);
			request.setAttribute("useStatusCountS", useStatusCountS+"");
			request.setAttribute("useStatusCountX", useStatusCountX+"");
			request.setAttribute("useStatusCountB", useStatusCountB+"");
			request.setAttribute("useStatusCountD", useStatusCountD+"");
			
			service.getDbOp().commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return new ActionForward("/admin/qualifiedStock.do?method=cargoOperationProcessList");
	}
	
	//ajax级联查询
	public ActionForward selection(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int selectIndex=Integer.parseInt(request.getParameter("selectIndex"));//序号，从1开始
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			if(selectIndex==0){//员工添加页面，选择一级部门后的查询
				String deptCode0=request.getParameter("deptCode0");
				if(deptCode0==null || deptCode0.length()==0){
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoDeptBean dept0=service.getCargoDept("code='"+deptCode0+"' and parent_id0=0 and parent_id1=0");//0级部门
				List deptList1=new ArrayList();
				if(dept0!=null){
					deptList1=service.getCargoDeptList("parent_id0="+dept0.getId()+" and parent_id1=0", -1, -1, null);
				}
				request.setAttribute("deptList1", deptList1);
			} else if (selectIndex==1){//员工添加页面，选择一级部门后的查询
				String deptCode0 = request.getParameter("deptCode0");
				String deptCode1=request.getParameter("deptCode1");
				CargoDeptBean dept0 = service.getCargoDept("code='" +deptCode0 + "' and parent_id0 = 0");
				CargoDeptBean dept1=service.getCargoDept("code='"+deptCode1+"' and parent_id0=" + dept0.getId() + " and parent_id1=0");//一级部门
				List deptList2=new ArrayList();
				if(dept1!=null){
					deptList2=service.getCargoDeptList("parent_id0 = "+ dept0.getId() +" and parent_id1="+dept1.getId()+" and parent_id2=0", -1, -1, null);
				}
				
				request.setAttribute("deptList2", deptList2);
			}else if(selectIndex==2){//员工添加页面，选择二级部门后的查询
				String deptCode0 = request.getParameter("deptCode0");
				CargoDeptBean dept0 = service.getCargoDept("code='" +deptCode0 + "' and parent_id0 = 0");
				String deptCode1=request.getParameter("deptCode1");
				CargoDeptBean dept1=service.getCargoDept("code='"+deptCode1+"' and parent_id0 = " + dept0.getId() + " and parent_id1=0");//一级部门
				if(dept1!=null){
					String deptCode2=request.getParameter("deptCode2");
					CargoDeptBean dept2=service.getCargoDept("code='"+deptCode2+"' and parent_id1="+dept1.getId()+" and parent_id2=0");//二级部门
					if(dept2!=null){
						List deptList3=new ArrayList();
						if(dept2!=null){
							deptList3=service.getCargoDeptList("parent_id0 = "+ dept0.getId() +" and parent_id1=" + dept1.getId() + " and parent_id2="+dept2.getId()+" and parent_id3=0", -1, -1, null);
						}
						request.setAttribute("deptList3", deptList3);
					}
				}
			}else if(selectIndex==3){//时效设置页面，归属部门的查询
				String deptId=request.getParameter("deptId");//一级部门Id
				String name=request.getParameter("name");//二级部门select的name属性
				List deptList=new ArrayList();//二级部门列表
				if(!deptId.equals("")){
					deptList=service.getCargoDeptList("parent_id1="+deptId+" and parent_id2=0", -1, -1, null);
				}
				request.setAttribute("deptList", deptList);
				request.setAttribute("name", name);
			}else if(selectIndex==4){//合格库作业动态明细，每个标签下的筛选查询
				int condition1=StringUtil.StringToId(request.getParameter("condition1"));//作业状态
				int condition2=StringUtil.StringToId(request.getParameter("condition2"));//时效状态
				String date=request.getParameter("date");//日期，yyyy-MM-dd
				int operType=StringUtil.toInt(request.getParameter("operType"));//作业单类型，-1是全部
				int num=StringUtil.toInt(request.getParameter("num"));//标签代号
				
				List qualifiedStockDetailList=new ArrayList();
				String query="";//查询条件

				if(operType==-1){
					query+="type>=0";
				}else{
					query+="type=";
					query+=operType;
				}
				
				if(!date.equals("0")){
					query+=" and confirm_datetime>'";
					query+=date;
					query+=" 00:00:00' and confirm_datetime<'";
					query+=date;
					query+=" 23:59:59'";
				}else{
					query+=" and confirm_datetime>'";
					query+=DateUtil.getNowDateStr();
					query+=" 00:00:00' and confirm_datetime<'";
					query+=DateUtil.getNowDateStr();
					query+=" 23:59:59'";
				}
				if(condition1==1){
					switch (operType) {
					case -1:
						query+=" and status in (1,2,10,11,19,20,28,29)";
						break;
					case 0:
						query+=" and status in (1,2)";
						break;
					case 1:
						query+=" and status in (10,11)";
						break;
					case 2:
						query+=" and status in (19,20)";
						break;
					case 3:
						query+=" and status in (28,29)";
					default:
						break;
					}
					
					if(condition2>0){
						query+=" and effect_status = " + (condition2-1);
					}
				}else if(condition1==2){
					switch (operType) {
					case -1:
						query+=" and status in (3,4,5,6,12,13,14,15,21,22,23,24,30,31,32,33)";
						break;
					case 0:
						query+=" and status in (3,4,5,6)";
						break;
					case 1:
						query+=" and status in (12,13,14,15)";
						break;
					case 2:
						query+=" and status in (21,22,23,24)";
						break;
					case 3:
						query+=" and status in (30,31,32,33)";
					default:
						break;
					}
					
					if(condition2>0){
						query+=" and effect_status = " + (condition2-1);
					}
				}else if(condition1==3){
					switch (operType) {
					case -1:
						query+=" and status in (7,8,9,16,17,18,25,26,27,34,35,36)";
						break;
					case 0:
						query+=" and status in (7,8,9)";
						break;
					case 1:
						query+=" and status in (16,17,18)";
						break;
					case 2:
						query+=" and status in (25,26,27)";
						break;
					case 3:
						query+=" and status in (34,35,36)";
						break;
					default:
						break;
					}
					
					if(condition2>0){
						query+=" and effect_status = " + (condition2-1);
					}
				}
				
				int pageIndex=StringUtil.StringToId(request.getParameter("pageIndex"));//分页页码
				int countPerPage=20;
				PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);//标签‘全部’的分页
				paging.setCurrentPageIndex(pageIndex);
				int totalCount=service.getCargoOperationCount(query.toString());
				paging.setTotalCount(totalCount);
				int totalPageCount=totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1);
				paging.setTotalPageCount(totalPageCount);
				paging.setPrefixUrl("#");
				if(num==0){
					paging.setJsFunction("shaixuan('0','pageIndex');");
				}else if(num==1){
					paging.setJsFunction("shaixuan('1','pageIndex');");
				}else if(num==2){
					paging.setJsFunction("shaixuan('2','pageIndex');");
				}else if(num==3){
					paging.setJsFunction("shaixuan('3','pageIndex');");
				}else if(num==4){
					paging.setJsFunction("shaixuan('4','pageIndex');");
				}
				request.setAttribute("paging", paging);
				
				qualifiedStockDetailList=service.getCargoOperationList(query, pageIndex*countPerPage, countPerPage, "confirm_datetime desc");
				List cargoOperationCargoList=new ArrayList();
				for(int i = 0; i < qualifiedStockDetailList.size(); i++){
					CargoOperationBean cob = (CargoOperationBean)qualifiedStockDetailList.get(i);
					int id = cob.getId();
					int status = cob.getStatus();
					
					//货位名称
					CargoOperationCargoBean cocb = new CargoOperationCargoBean();
					List cargoOperationCargoList1 = service.getCargoOperationCargoList("out_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
					if(cargoOperationCargoList1 != null && cargoOperationCargoList1.size() > 0){
						cocb = (CargoOperationCargoBean)cargoOperationCargoList1.get(0); //源货位bean
					}
					List cargoOperationCargoList2 = service.getCargoOperationCargoList("in_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
					if(cargoOperationCargoList2 != null && cargoOperationCargoList2.size() > 0){
						String inCargo = ((CargoOperationCargoBean)cargoOperationCargoList2.get(0)).getInCargoWholeCode();
						cocb.setInCargoWholeCode(inCargo); //目的货位
					}
					cargoOperationCargoList.add(cocb);
					
					//作业状态名称
					CargoOperationProcessBean copb = service.getCargoOperationProcess("id =" + status); 
					if(copb != null){
						cob.setStatusName(StringUtil.convertNull(copb.getStatusName())); 
					}
				}
				
				request.setAttribute("qualifiedStockDetailList", qualifiedStockDetailList);
				request.setAttribute("cargoOperationCargoList", cargoOperationCargoList);
			}else if(selectIndex==5){//待作业管理
				int num=StringUtil.StringToId(request.getParameter("num"));
				if(num==0){//待补货
					List refillList=refillTodo(mapping, form, request, response, wareService.getDbOp());
					request.setAttribute("refillList", refillList);
				}else if(num==1){//待上架
					List upShelfList=upShelfTodo(mapping, form, request, response, wareService.getDbOp());
					request.setAttribute("upShelfList", upShelfList);
				}else if(num==2){//待下架
					List downShelfList=downShelfTodo(mapping, form, request, response, wareService.getDbOp());
					request.setAttribute("downShelfList", downShelfList);
				}else if(num==3){//待调拨
					List exchangeList=exchangeTodo(mapping, form, request, response, wareService.getDbOp());
					request.setAttribute("exchangeList", exchangeList);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("selection");
	}
	
	//作业日志查询
	public ActionForward cargoOperLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String operCode=request.getParameter("operCode");//作业单编号
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			List operLogList=new ArrayList();
			if(operCode!=null){
				operLogList=service.getCargoOperLogList("oper_code='"+operCode+"'", -1, -1, "id desc");
			}
			request.setAttribute("operLogList", operLogList);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("operLogList");
	}
	
	//作业交接-设备扫描界面
	public ActionForward cargoOperFac(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String operCode=request.getParameter("operCode");//作业单编号
		String staffCode=request.getParameter("staffCode");//员工编号
		
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			if(operCode!=null){//扫描作业单编号的阶段
			CargoOperationBean coBean=service.getCargoOperation("code='"+operCode+"'");
			if(coBean==null){
				request.setAttribute("tip", "没有找到该作业单！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
				
			CargoOperationProcessBean process=service.getCargoOperationProcess("id="+coBean.getStatus());
			if(process==null){
				request.setAttribute("tip", "作业单流程数据错误！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoOperationProcessBean nextProcess=null;
			if(coBean.getType()==0){//上架单
				if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS2&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS3&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS4&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS5){
					request.setAttribute("tip", "作业单不处于交接阶段！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				nextProcess=service.getCargoOperationProcess("id="+(coBean.getStatus()+1));
				if(nextProcess.getUseStatus()==0){
					nextProcess=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS7);
				}
				if(nextProcess.getHandleType()==0){
					request.setAttribute("tip", "该作业单下阶段必须人工确认！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}else if(coBean.getType()==1){//下架单
				if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS11&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS12&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS13&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS14){
					request.setAttribute("tip", "作业单不处于交接阶段！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				nextProcess=service.getCargoOperationProcess("id="+(coBean.getStatus()+1));
				if(nextProcess.getUseStatus()==0){
					nextProcess=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS16);
				}
				if(nextProcess.getHandleType()==0){
					request.setAttribute("tip", "该作业单下阶段必须人工确认！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}else if(coBean.getType()==2){//补货单
				if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS20&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS21&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS22&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS23){
					request.setAttribute("tip", "作业单不处于交接阶段！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				nextProcess=service.getCargoOperationProcess("id="+(coBean.getStatus()+1));
				if(nextProcess.getUseStatus()==0){
					nextProcess=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS25);
				}
				if(nextProcess.getHandleType()==0){
					request.setAttribute("tip", "该作业单下阶段必须人工确认！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}else if(coBean.getType()==3){//调拨单
				if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS29&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS30&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS31&&
						coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS32){
					request.setAttribute("tip", "作业单不处于交接阶段！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				nextProcess=service.getCargoOperationProcess("id="+(coBean.getStatus()+1));
				if(nextProcess.getUseStatus()==0){
					nextProcess=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);
				}
				if(nextProcess.getHandleType()==0){
					request.setAttribute("tip", "该作业单下阶段必须人工确认！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			if(operCode!=null&&staffCode!=null){//扫描员工编号的阶段，操作作业单
				CargoStaffBean staff=service.getCargoStaff("status=0 and code='"+staffCode+"'");
				if(staff==null){
					request.setAttribute("tip", "未找到该员工！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				int deptId1=nextProcess.getDeptId1();//职能归属
				int deptId2=nextProcess.getDeptId2();
				CargoDeptBean dept1=service.getCargoDept("id="+deptId1);
				CargoDeptBean dept2=service.getCargoDept("id="+deptId2);
				if(dept1==null||dept2==null){
					request.setAttribute("tip", "交接阶段职能归属数据错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoDeptBean dept0=service.getCargoDept("id="+dept1.getParentId0());
				if(dept0==null){
					request.setAttribute("tip", "交接阶段职能归属数据错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				String deptCode=dept0.getCode()+dept1.getCode()+dept2.getCode();//部门完整编号
				if(!staffCode.substring(0,6).equals(deptCode)){
					request.setAttribute("tip", " 该员工不属于这个阶段的职能归属！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				int confirmType=nextProcess.getConfirmType();//作业判断
				int storageId=nextProcess.getStorageId();//货位归属
				CargoOperationCargoBean cocBean=null;
				
				if(confirmType==1){//判断源货位
					cocBean=service.getCargoOperationCargo("oper_id="+coBean.getId()+" and out_cargo_whole_code is not null");
					if(cocBean==null){
						request.setAttribute("tip", "作业单源货位数据错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String outCargoCode=cocBean.getOutCargoWholeCode();
					CargoInfoStorageBean storage=service.getCargoInfoStorage("id="+storageId);
					if(storage==null){
						request.setAttribute("tip", "该阶段货位归属数据错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!outCargoCode.substring(0,5).equals(storage.getWholeCode())){
						request.setAttribute("tip", "该作业单的源货位不属于该阶段的货位归属！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}else if(confirmType==2){//判断目的货位
					cocBean=service.getCargoOperationCargo("oper_id="+coBean.getId()+" and in_cargo_whole_code is not null");
					if(cocBean==null){
						request.setAttribute("tip", "作业单源货位数据错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String inCargoCode=cocBean.getInCargoWholeCode();
					CargoInfoStorageBean storage=service.getCargoInfoStorage("id="+storageId);
					if(storage==null){
						request.setAttribute("tip", "该阶段货位归属数据错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!inCargoCode.substring(0,5).equals(storage.getWholeCode())){
						request.setAttribute("tip", "该作业单的源货位不属于该阶段的货位归属！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				service.getDbOp().startTransaction();
				
				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+coBean.getId()+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}
				
				service.updateCargoOperation("status="+nextProcess.getId()+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+coBean.getId());
				
				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(coBean.getId());
				operLog.setOperCode(coBean.getCode());
				operLog.setOperName(nextProcess.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode(staff.getCode());
				operLog.setEffectTime(0);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(nextProcess.getStatusName());
				service.addCargoOperLog(operLog);
				
				service.getDbOp().commitTransaction();
				
				request.setAttribute("tip", "验证成功！");
				request.setAttribute("result", "success");
			}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("cargoOperFac");
	}
	
	//合格库待作业商品列表
	public ActionForward cargoOperationTodo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave2");
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		int type=StringUtil.toInt(request.getParameter("type"));//作业单类型
		try {
			if(type==0){//上架
				List upShelfList=upShelfTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("upShelfList", upShelfList);
			}else if(type==1){//下架
				List downShelfList=downShelfTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("downShelfList", downShelfList);
			}else if(type==2){//补货
				List refillList=refillTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("refillList", refillList);
			}else if(type==3){//调拨
				List exchangeList=exchangeTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("exchangeList", exchangeList);
			}else{//全部
				List upShelfList=upShelfTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("upShelfList", upShelfList);
				List downShelfList=downShelfTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("downShelfList", downShelfList);
				List refillList=refillTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("refillList", refillList);
				List exchangeList=exchangeTodo(mapping,form,request,response ,dbOp);
				request.setAttribute("exchangeList", exchangeList);
			}
			
			//添加图表信息
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE, -29);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			List dateList=new ArrayList();
			List operCountList1=new ArrayList();
			List operCountList2=new ArrayList();
			List operCountList3=new ArrayList();
			for(int i=0;i<30;i++){
				String date=sdf.format(cal.getTime());
				String condition1="create_datetime>'"+sdf.format(cal.getTime())+" 00:00:00'" +
						" and create_datetime<'"+sdf.format(cal.getTime())+" 23:59:59' " +
						"and status in(2,3,4,5,6,11,12,13,14,15,20,21,22,23,24,29,30,31,32,33) and effect_status in (0,1)";//未完成作业单
				String condition2="create_datetime>'"+sdf.format(cal.getTime())+" 00:00:00'" +
						" and create_datetime<'"+sdf.format(cal.getTime())+" 23:59:59' " +
						"and status in (7,8,9,16,17,18,25,26,27,34,35,36) and effect_status in (2,3,4)";//已完成作业单
				String condition3="create_datetime>'"+sdf.format(cal.getTime())+" 00:00:00'" +
						" and create_datetime<'"+sdf.format(cal.getTime())+" 23:59:59' " +
						"and effect_status=4";//作业失败订单
				int operCount1=service.getCargoOperationCount(condition1);
				int operCount2=service.getCargoOperationCount(condition2);
				int operCount3=service.getCargoOperationCount(condition3);
				dateList.add(date);
				operCountList1.add(operCount1+"");
				operCountList2.add(operCount2+"");
				operCountList3.add(operCount3+"");
				
				cal.add(Calendar.DATE, 1);
			}
			request.setAttribute("dateList", dateList);
			request.setAttribute("operCountList1", operCountList1);
			request.setAttribute("operCountList2", operCountList2);
			request.setAttribute("operCountList3", operCountList3);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("cargoOperationTodo");
	}
	
	/**
	 * 待作业管理，待补货列表
	 */
	public List refillTodo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response ,DbOperation dbOp) throws Exception {
		
		WareService wareService = new WareService(dbOp);
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String status=request.getParameter("status");
		
		//散件区库存不足的（没有商品是否是下架的判断）
		String sqlRefill="select distinct cps.product_id from cargo_info ci join cargo_product_stock cps on ci.id=cps.cargo_id " +
				"where ci.area_id=3 and ci.stock_type=0 and ci.store_type=4 " +
				"and cps.stock_count<ci.warn_stock_count";
		ResultSet rs1=dbOp.executeQuery(sqlRefill);
		List refillList=new ArrayList();//应补货商品列表
		while(rs1.next()){
			CargoOperationTodoBean cot=new CargoOperationTodoBean();
			cot.setProductId(rs1.getInt("cps.product_id"));
			cot.setType(2);
			refillList.add(cot);
		}
		rs1.close();
		
		for(int i=0;i<refillList.size();i++){
			CargoOperationTodoBean cot=(CargoOperationTodoBean)refillList.get(i);
			//修改源货位
			String sql1="select cps.id,cargo_id,ci.whole_code from cargo_info ci join cargo_product_stock cps on ci.id=cps.cargo_id "+
				"where ci.area_id=3 and ci.stock_type=0 and ci.store_type=1 and cps.product_id="+cot.getProductId()+
				" and cps.stock_count>0 limit 1";
			ResultSet tempRs=dbOp.executeQuery(sql1);
			boolean hasCargo=false;
			while(tempRs.next()){
				int cpsId=tempRs.getInt("cps.id");
				int cargoId=tempRs.getInt("cargo_id");
				String cargoCode=tempRs.getString("ci.whole_code");
				cot.setCargoProductStockId(cpsId);
				cot.setCargoId(cargoId);
				cot.setCargoCode(cargoCode);
				hasCargo=true;
			}
			tempRs.close();
			if(hasCargo==false){//没有对应的整件区货位
				refillList.remove(i);
				i--;
				continue;
			}
		}
		//整件区有库存但散件区没有对应货位的（没有商品是否是下架的判断）
		String sqlRefill2="select cps.id,cps.product_id,cps.cargo_id,ci.whole_code from cargo_info ci join cargo_product_stock cps on ci.id=cps.cargo_id"+
			" where ci.area_id=3 and ci.stock_type=0 and ci.store_type=1 and cps.stock_count>0"+
			" and not exists"+
			" (select * from cargo_info ci2 join cargo_product_stock cps2 on ci2.id=cps2.cargo_id"+
			" where ci2.area_id=3 and ci2.stock_type=0 and ci2.store_type=0 and cps2.product_id=cps.product_id"+
			" )";
		ResultSet rs2=dbOp.executeQuery(sqlRefill2);
		while(rs2.next()){
			CargoOperationTodoBean cot=new CargoOperationTodoBean();
			cot.setCargoProductStockId(rs2.getInt("cps.id"));
			cot.setProductId(rs2.getInt("cps.product_id"));
			cot.setCargoId(rs2.getInt("cps.cargo_id"));
			cot.setCargoCode(rs2.getString("ci.whole_code"));
			cot.setType(2);
			refillList.add(cot);
		}
		rs2.close();
		
		int refillCount=0;//待补货数量
		for(int i=0;i<refillList.size();i++){
			CargoOperationTodoBean cot=(CargoOperationTodoBean)refillList.get(i);
			
			//是否是下架的商品
			voProduct product=wareService.getProduct(cot.getProductId());
			if(product==null){
				refillList.remove(i);
				i--;
				continue;
			}
			if(product.getStatus()==100){//下架状态
				refillList.remove(i);
				i--;
				continue;
			}else{//非下架状态
				cot.setProductCode(product.getCode());
			}
			
			//查询是否已有未完成的补货单（已经确认提交但没有作业完成）
			String sqlRefill3="select count(co.id) from cargo_operation co join cargo_operation_cargo coc on co.id=coc.oper_id"
				+" where co.type=2 and co.status in(20,21,22,23,24) and co.effect_status in (0,1) and coc.out_cargo_product_stock_id="+cot.getCargoProductStockId();
			ResultSet rs3=dbOp.executeQuery(sqlRefill3);
			int cargoOperCount=0;
			while(rs3.next()){
				cargoOperCount=rs3.getInt(1);
			}
			rs3.close();
			if(cargoOperCount>0){
				refillList.remove(i);
				i--;
				continue;
			}
			
			
			//修改待作业状态和领取人
			CargoOperationTodoBean tempTodo=cService.getCargoOperationTodo("cargo_product_stock_id="+cot.getCargoProductStockId()+" and type=2 and status in (0,1,2)");
			if(tempTodo!=null){
				if(tempTodo.getStatus()==2){
					refillList.remove(i);
					i--;
					continue;
				}
				cot.setStatus(tempTodo.getStatus());
				cot.setStaffId(tempTodo.getStaffId());
				cot.setStaffName(tempTodo.getStaffName());
			}
			refillCount++;
			if(status!=null&&status.equals("0")){//状态是未分配
				if(tempTodo!=null){//已分配
					refillList.remove(i);
					i--;
					continue;
				}
			}else if(status!=null&&status.equals("1")){//状态是已分配
				if(tempTodo==null){//未分配
					refillList.remove(i);
					i--;
					continue;
				}
			}
			
			//查询可用装箱单
			int cartonningCount=0;
			String sqlRefill4="select count(ci.id) from cartonning_info ci join cartonning_product_info cpi on ci.id=cpi.cartonning_id " +
					"where ci.cargo_id="+cot.getCargoId()+" and ci.status<>2 and cpi.product_id="+product.getId();
			ResultSet rs4=dbOp.executeQuery(sqlRefill4);
			while(rs4.next()){
				cartonningCount=rs4.getInt(1);
			}
			rs4.close();
			cot.setCount(cartonningCount);
		}
		request.setAttribute("refillCount", refillCount+"");
		int countPerPage=20;
		int pageIndex=StringUtil.StringToId(request.getParameter("pageIndex"));//分页页码
		PagingBean paging0 = new PagingBean(pageIndex, refillList.size(),countPerPage);//标签‘待补货’的分页
		paging0.setPrefixUrl("#");
		paging0.setJsFunction("shaixuan2('0','pageIndex');");
		request.setAttribute("paging0", paging0);
		
		List refillList2=new ArrayList();
		for(int i=pageIndex*countPerPage;i<(pageIndex+1)*countPerPage;i++){
			if(refillList.size()>i){
				refillList2.add(refillList.get(i));
			}
		}
		return refillList2;
	}
	
	/**
	 * 待作业管理，待上架列表
	 */
	public List upShelfTodo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response ,DbOperation dbOp) throws Exception {
		
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		List upShelfList=new ArrayList();
		String status=request.getParameter("status");
		
		//关联了待验库且没关联作业单的装箱单
		String upSql="select cps.id,c1.id,c1.code,c2.id,c2.whole_code,cpi.product_count"
			+" from cartonning_info c1 join cargo_info c2 on c1.cargo_id=c2.id"
			+" join cartonning_product_info cpi on cpi.cartonning_id=c1.id"
			+" join cargo_product_stock cps on c2.id=cps.cargo_id and cpi.product_id=cps.product_id"
			+" where c2.area_id=3 and c2.stock_type=1 and c1.status<>2 and c1.oper_id=0";
		ResultSet rs=dbOp.executeQuery(upSql);
		while(rs.next()){
			CargoOperationTodoBean cot=new CargoOperationTodoBean();
			cot.setCargoProductStockId(rs.getInt("cps.id"));
			cot.setProductId(rs.getInt("c1.id"));
			cot.setProductCode(rs.getString("c1.code"));
			cot.setCargoId(rs.getInt("c2.id"));
			cot.setCargoCode(rs.getString("c2.whole_code"));
			cot.setCount(rs.getInt("cpi.product_count"));
			cot.setType(0);
			upShelfList.add(cot);
		}
		
		//修改状态和领取人
		int upShelfCount=0;
		for(int i=0;i<upShelfList.size();i++){
			CargoOperationTodoBean cot=(CargoOperationTodoBean)upShelfList.get(i);
			CargoOperationTodoBean tempTodo=cService.getCargoOperationTodo("product_id="+cot.getProductId()+" and type=0 and status in (0,1,2)");
			if(tempTodo!=null){
				if(tempTodo.getStatus()==2){
					upShelfList.remove(i);
					i--;
					continue;
				}
				cot.setStatus(tempTodo.getStatus());
				cot.setStaffId(tempTodo.getStaffId());
				cot.setStaffName(tempTodo.getStaffName());
			}
			upShelfCount++;
			if(status!=null&&status.equals("0")){//状态是未分配
				if(tempTodo!=null){//已分配
					upShelfList.remove(i);
					i--;
					continue;
				}
			}else if(status!=null&&status.equals("1")){//状态是已分配
				if(tempTodo==null){//未分配
					upShelfList.remove(i);
					i--;
					continue;
				}
			}
		}
		
		int countPerPage=20;
		int pageIndex=StringUtil.StringToId(request.getParameter("pageIndex"));//分页页码
		PagingBean paging1 = new PagingBean(pageIndex, upShelfList.size(),countPerPage);//标签‘待补货’的分页
		paging1.setPrefixUrl("#");
		paging1.setJsFunction("shaixuan2('1','pageIndex');");
		request.setAttribute("paging1", paging1);
		request.setAttribute("upShelfCount", upShelfCount+"");
		List upShelfList2=new ArrayList();
		for(int i=pageIndex*countPerPage;i<(pageIndex+1)*countPerPage;i++){
			if(upShelfList.size()>i){
				upShelfList2.add(upShelfList.get(i));
			}
		}
		
		return upShelfList2;
	}
	
	/**
	 * 待作业管理，待下架列表
	 */
	public List downShelfTodo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response ,DbOperation dbOp) throws Exception {
		
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String status=request.getParameter("status");
		
		List downShelfList=new ArrayList();
		String downSql="select cps.id,cps.product_id,p.code,ci.id,ci.whole_code,cps.stock_count from cargo_info ci join cargo_product_stock cps on ci.id=cps.cargo_id"
			+" join product p on p.id=cps.product_id"
			+" where ci.area_id=3 and ci.stock_type=0 and ci.store_type=0 and p.status=100 and cps.stock_count>0";
		ResultSet rs=dbOp.executeQuery(downSql);
		while(rs.next()){
			CargoOperationTodoBean cot=new CargoOperationTodoBean();
			cot.setCargoProductStockId(rs.getInt("cps.id"));
			cot.setProductId(rs.getInt("cps.product_id"));
			cot.setProductCode(rs.getString("p.code"));
			cot.setCargoId(rs.getInt("ci.id"));
			cot.setCargoCode(rs.getString("ci.whole_code"));
			cot.setCount(rs.getInt("cps.stock_count"));
			cot.setType(1);
			downShelfList.add(cot);
		}
		
		//修改状态和领取人
		int downShelfCount=0;
		for(int i=0;i<downShelfList.size();i++){
			CargoOperationTodoBean cot=(CargoOperationTodoBean)downShelfList.get(i);
			CargoOperationTodoBean tempTodo=cService.getCargoOperationTodo("cargo_product_stock_id="+cot.getCargoProductStockId()+" and type=1 and status in (0,1,2)");
			if(tempTodo!=null){
				if(tempTodo.getStatus()==2){
					downShelfList.remove(i);
					i--;
					continue;
				}
				cot.setStatus(tempTodo.getStatus());
				cot.setStaffId(tempTodo.getStaffId());
				cot.setStaffName(tempTodo.getStaffName());
			}
			downShelfCount++;
			if(status!=null&&status.equals("0")){//状态是未分配
				if(tempTodo!=null){//已分配
					downShelfList.remove(i);
					i--;
					continue;
				}
			}else if(status!=null&&status.equals("1")){//状态是已分配
				if(tempTodo==null){//未分配
					downShelfList.remove(i);
					i--;
					continue;
				}
			}
		}
		
		int countPerPage=20;
		int pageIndex=StringUtil.StringToId(request.getParameter("pageIndex"));//分页页码
		PagingBean paging2 = new PagingBean(pageIndex, downShelfList.size(),countPerPage);//标签‘待补货’的分页
		paging2.setPrefixUrl("#");
		paging2.setJsFunction("shaixuan2('2','pageIndex');");
		request.setAttribute("paging2", paging2);
		request.setAttribute("downShelfCount", downShelfCount+"");
		List downShelfList2=new ArrayList();
		for(int i=pageIndex*countPerPage;i<(pageIndex+1)*countPerPage;i++){
			if(downShelfList.size()>i){
				downShelfList2.add(downShelfList.get(i));
			}
		}
		return downShelfList2;
	}
	
	/**
	 * 待作业管理，待调拨列表
	 */
	public List exchangeTodo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response ,DbOperation dbOp) throws Exception {
		
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String status=request.getParameter("status");
		
		List exchangeList=new ArrayList();
		String exchangeSql="select cps.product_id from cargo_product_stock cps"
				+" join cargo_info ci on ci.id=cps.cargo_id"
				+" where ci.area_id=3 and ci.stock_type=0 and ci.store_type in (0,4) and cps.stock_count>0"
				+" group by cps.product_id having count(cps.product_id)>1";
		ResultSet rs=dbOp.executeQuery(exchangeSql);
		while(rs.next()){
			CargoOperationTodoBean cot=new CargoOperationTodoBean();
			int productId=rs.getInt("cps.product_id");
			cot.setProductId(productId);
			cot.setStatus(0);
			cot.setType(3);
			exchangeList.add(cot);
		}
		rs.close();
		
		int exchangeCount=0;
		for(int i=0;i<exchangeList.size();i++){
			CargoOperationTodoBean cot=(CargoOperationTodoBean)exchangeList.get(i);
			String exchangeSql2="select p.code,cps.cargo_id,cps.id,ci.whole_code,ci.whole_code,cps.stock_count" +
				" from cargo_info ci join cargo_product_stock cps on ci.id=cps.cargo_id"+
				" join product p on p.id=cps.product_id"+
				" where ci.area_id=3 and ci.stock_type=0 and ci.store_type in (0,4) and cps.stock_count>0"+
				" and cps.product_id="+cot.getProductId()+" order by cps.stock_count asc limit 1";
			ResultSet rs2=dbOp.executeQuery(exchangeSql2);
			while(rs2.next()){
				cot.setProductCode(rs2.getString("p.code"));
				cot.setCargoId(rs2.getInt("cps.cargo_id"));
				cot.setCargoProductStockId(rs2.getInt("cps.id"));
				cot.setCargoCode(rs2.getString("ci.whole_code"));
				cot.setCount(rs2.getInt("cps.stock_count"));
			}
			rs2.close();
			CargoOperationTodoBean tempTodo=cService.getCargoOperationTodo("cargo_product_stock_id="+cot.getCargoProductStockId()+" and type=3 and status in (0,1,2)");
			if(tempTodo!=null){
				if(tempTodo.getStatus()==2){
					exchangeList.remove(i);
					i--;
					continue;
				}
				cot.setStatus(tempTodo.getStatus());
				cot.setStaffId(tempTodo.getStaffId());
				cot.setStaffName(tempTodo.getStaffName());
			}
			exchangeCount++;
			if(status!=null&&status.equals("0")){//状态是未分配
				if(tempTodo!=null){//已分配
					exchangeList.remove(i);
					i--;
					continue;
				}
			}else if(status!=null&&status.equals("1")){//状态是已分配
				if(tempTodo==null){//未分配
					exchangeList.remove(i);
					i--;
					continue;
				}
			}
		}
		
		int countPerPage=20;
		int pageIndex=StringUtil.StringToId(request.getParameter("pageIndex"));//分页页码
		PagingBean paging3 = new PagingBean(pageIndex, exchangeList.size(),countPerPage);//标签‘待补货’的分页
		paging3.setPrefixUrl("#");
		paging3.setJsFunction("shaixuan2('3','pageIndex');");
		request.setAttribute("paging3", paging3);
		request.setAttribute("exchangeCount", exchangeCount+"");
		List exchangeList2=new ArrayList();
		for(int i=pageIndex*countPerPage;i<(pageIndex+1)*countPerPage;i++){
			if(exchangeList.size()>i){
				exchangeList2.add(exchangeList.get(i));
			}
		}
		return exchangeList2;
	}
	
	/**
	 * 分配并打印已选作业清单
	 */
	public ActionForward submitCargoOperationTodo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String[] todoList=null;
		if(request.getParameterValues("todo0")!=null){
			todoList=request.getParameterValues("todo0");
		}else if(request.getParameterValues("todo1")!=null){
			todoList=request.getParameterValues("todo1");
		}else if(request.getParameterValues("todo2")!=null){
			todoList=request.getParameterValues("todo2");
		}else if(request.getParameterValues("todo3")!=null){
			todoList=request.getParameterValues("todo3");
		}
		int type=StringUtil.toInt(request.getParameter("type"));
		String staffCode=request.getParameter("staffCode");
		
		try {
			dbOp.startTransaction();
			CargoStaffBean staff=service.getCargoStaff("status=0 and code='"+staffCode+"'");
			if(staff==null){
				request.setAttribute("tip", "员工编号错误！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List cotList=new ArrayList();
			if(type==2||type==1||type==3){//待补货
				for(int i=0;i<todoList.length;i++){
					String todo=todoList[i];//cargo_product_stock的id
					if(todo.startsWith("*")){
						todo=todo.substring(1);
					}
					CargoProductStockBean cps=service.getCargoProductStock("id="+todo);
					if(cps==null){
						System.out.println("cargo_product_stock:id="+todo+"未找到");
						continue;
					}
					CargoOperationTodoBean cot=new CargoOperationTodoBean();
					cot.setCargoProductStockId(cps.getId());
					voProduct product=wareService.getProduct(cps.getProductId());
					if(product==null){
						System.out.println("product:id="+cps.getProductId()+"未找到");
						continue;
					}
					cot.setProductId(cps.getProductId());
					cot.setProductCode(product.getCode());
					CargoInfoBean cargoInfo=service.getCargoInfo("id="+cps.getCargoId());
					if(cargoInfo==null){
						System.out.println("cargo_info:id="+cps.getCargoId()+"未找到");
						continue;
					}
					cot.setCargoId(cps.getCargoId());
					cot.setCargoCode(cargoInfo.getWholeCode());
					//查询可用装箱单
					if(type==2){
						int cartonningCount=cService.getCartonningCount("cargo_id="+cot.getCargoId()+" and status<>2");
						cot.setCount(cartonningCount);
					}else if(type==1||type==3){
						cot.setCount(cps.getStockCount());
					}
					
					cot.setStaffId(staff.getId());
					cot.setStaffName(staff.getName());
					cot.setStatus(1);
					cot.setType(type);
					cotList.add(cot);
					
					CargoOperationTodoBean tempCot=cService.getCargoOperationTodo("cargo_product_stock_id="+cot.getCargoProductStockId()+" and type="+type+" and status in (0,1,2)");
					if(tempCot!=null){//作废原来的记录
						cService.updateCargoOperationTodo("status=4", "id="+tempCot.getId());
					}
					cService.addCargoOperationTodo(cot);
				}
			}else if(type==0){//待上架
				for(int i=0;i<todoList.length;i++){
					String todo=todoList[i];//cartonning_info的id
					if(todo.startsWith("*")){
						todo=todo.substring(1);
					}
					CartonningInfoBean cartonning=cService.getCartonningInfo("id="+todo);
					if(cartonning==null){
						System.out.println("cartonning_info:id="+todo+"未找到");
						continue;
					}
					CartonningProductInfoBean cartonningProduct=cService.getCartonningProductInfo("cartonning_id="+cartonning.getId());
					if(cartonningProduct==null){
						System.out.println("cartonning_product_info:cartonning_id="+cartonning.getId()+"未找到");
						continue;
					}
					CargoOperationTodoBean cot=new CargoOperationTodoBean();
					cot.setCargoProductStockId(0);
					voProduct product=wareService.getProduct(cartonningProduct.getProductId());
					if(product==null){
						System.out.println("product:id="+cartonningProduct.getProductId()+"未找到");
						continue;
					}
					cot.setProductId(cartonning.getId());
					cot.setProductCode(cartonning.getCode());
					CargoInfoBean cargoInfo=service.getCargoInfo("id="+cartonning.getCargoId());
					if(cargoInfo==null){
						System.out.println("cargo_info:id="+cartonning.getCargoId()+"未找到");
						continue;
					}
					cot.setCargoId(cartonning.getCargoId());
					cot.setCargoCode(cargoInfo.getWholeCode());
					//查询可用装箱单
					cot.setCount(cartonningProduct.getProductCount());
					
					cot.setStaffId(staff.getId());
					cot.setStaffName(staff.getName());
					cot.setStatus(1);
					cot.setType(type);
					cotList.add(cot);
					
					CargoOperationTodoBean tempCot=cService.getCargoOperationTodo("product_id="+cot.getProductId()+" and type="+type+" and status in (0,1,2)");
					if(tempCot!=null){//作废原来的记录
						cService.updateCargoOperationTodo("status=4", "id="+tempCot.getId());
					}
					cService.addCargoOperationTodo(cot);//添加新的记录
				}
			}
			request.setAttribute("cotList", cotList);
			request.setAttribute("type", type+"");
			dbOp.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("cargoOperationTodoPrint");
	}
	//部门关联地区和库类型页面中的组织结构
	public ActionForward departmentAreaStockType(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String deptId = StringUtil.convertNull(request.getParameter("id"));
		String deptId2 = StringUtil.convertNull(request.getParameter("id2"));
		String deptId3 = StringUtil.convertNull(request.getParameter("id3"));
		String deptId4 = StringUtil.convertNull(request.getParameter("id4"));
		String deptName =new String();
		String deptName2 =new String();
		String deptName3 =new String();
		String deptName4 =new String();
		String deptNameFull =new String();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CargoDeptAreaService cdaService =ServiceFactory.createCargoDeptAreaService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			
			// 查询出各级部门的名称，拼接成最终部门名称
			CargoDeptBean cargoDeptBean =null;
			if( deptId!=null && deptId.length()>0) {
				CargoDeptBean cdb0 = service.getCargoDept("id="+deptId);
				cargoDeptBean = service.getCargoDept("id="+deptId);
				deptName = cdb0.getName();
				if(deptId2!=null && deptId2.length()>0){
					CargoDeptBean cdb1 = service.getCargoDept("id="+deptId2);
					cargoDeptBean = service.getCargoDept("id="+deptId2);
					deptName2 = cdb1.getName();
					if(deptId3!=null && deptId3.length()>0){
						CargoDeptBean cdb2 = service.getCargoDept("id="+deptId3);
						cargoDeptBean = service.getCargoDept("id="+deptId3);
						deptName3 = cdb2.getName();
						if(deptId4!=null && deptId4.length()>0){
							CargoDeptBean cdb3 = service.getCargoDept("id="+deptId4);
							cargoDeptBean = service.getCargoDept("id="+deptId4);
							deptName4 = cdb3.getName();
						}
					}
				}
			}
			
		    deptNameFull = deptName + deptName2 + deptName3 + deptName4;
			List deptList=service.getCargoDeptList("parent_id0=0", -1, -1, null);//零级部门列表
			for(int i=0;i<deptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)deptList.get(i);
				List deptList2=service.getCargoDeptList("parent_id0="+cd.getId()+" and parent_id1=0", -1, -1, null);//该部门的一级部门列表
				cd.setJuniorDeptList(deptList2);
				for(int j=0;j<deptList2.size();j++){
					CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
					List deptList3=service.getCargoDeptList("parent_id1="+cd2.getId()+" and parent_id2=0", -1, -1, null);//该部门的二级部门列表
					cd2.setJuniorDeptList(deptList3);
					for(int k=0;k<deptList3.size();k++){
						CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k);
						List deptList4=service.getCargoDeptList("parent_id2="+cd3.getId()+" and parent_id3=0", -1, -1, null);//该部门的三级部门列表
						cd3.setJuniorDeptList(deptList4);
					}
				}
			}
			HashMap<Integer, List<CargoDeptAreaBean>> map = new HashMap<Integer, List<CargoDeptAreaBean>>();
			List stockList = null;

			if (cargoDeptBean!=null) {
				
				HashMap<Integer,String> areaMap = ProductStockBean.areaMap;
				// 该部门库地区所对应的列表
				for (int key : areaMap.keySet()) {
					List areaList = cdaService.getCargoDeptAreaList("area=" + key + " and dept_id=" + cargoDeptBean.getId(), -1, -1, "id");
					if (areaList != null) {
						stockList = new ArrayList();
						for (int i = 0; i < areaList.size(); i++) {
							CargoDeptAreaBean cdaBean = (CargoDeptAreaBean) areaList.get(i);
							stockList.add(cdaBean.getStockType());
						}
						map.put(key, stockList);
					}
				}
				request.setAttribute("deptId", cargoDeptBean.getId()+"");
			}

			request.setAttribute("deptList", deptList);
			request.setAttribute("deptNameFull", deptNameFull);
		    request.setAttribute("map", map);
			request.setAttribute("deptId1", deptId);
			request.setAttribute("deptId2", deptId2);
			request.setAttribute("deptId3", deptId3);
			request.setAttribute("deptId4", deptId4);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("departmentAreaStockType");
	}
	//为部门分配地区和库类型
	public ActionForward assignAreaStockType(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String deptId = StringUtil.convertNull(request.getParameter("deptId"));
		String id = StringUtil.convertNull(request.getParameter("id"));
		String id2 = StringUtil.convertNull(request.getParameter("id2"));
		String id3 = StringUtil.convertNull(request.getParameter("id3"));
		String id4 = StringUtil.convertNull(request.getParameter("id4"));
		String parm ="";
		if(id!=null&&id.length()>0){
			parm="&id="+id;
		}
		if(id2!=null&&id2.length()>0){
			parm="&id2="+id2;
		}
		if(id3!=null&&id3.length()>0){
			parm="&id3="+id3;
		}
		if(id4!=null&&id4.length()>0){
			parm="&id4="+id4;
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CargoDeptAreaService cdaService =ServiceFactory.createCargoDeptAreaService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			wareService.getDbOp().startTransaction();
			if(deptId!=null && deptId.length()>0){
				cdaService.deleteCargoDeptAreaInfo("dept_id="+deptId);
			}
			
			for (int key : ProductStockBean.areaMap.keySet()) {
				String checkBox[]=(String[])request.getParameterValues("checkBox" + key);
				if (checkBox != null) {
					for (int i = 0; i < checkBox.length; i++) {
						CargoDeptAreaBean bean = new CargoDeptAreaBean();
						bean.setArea(key);
						bean.setDeptId(StringUtil.toInt(deptId));
						bean.setStockType(StringUtil.toInt(checkBox[i]));
						CargoDeptAreaBean cadBean =cdaService.getCargoDeptAreaInfo("area=" + key + " and dept_id="+deptId+" and stock_type="+checkBox[i]);
						if(cadBean==null){
							cdaService.addCargoDeptAreaInfo(bean);
						}
					}
				}
			}
			wareService.getDbOp().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return new ActionForward("/admin/qualifiedStock.do?method=departmentAreaStockType"+parm);
	}
}
