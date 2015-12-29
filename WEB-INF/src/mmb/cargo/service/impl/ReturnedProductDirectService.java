package mmb.cargo.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.cargo.dao.ReturnedProductDirectDao;
import mmb.cargo.model.ReturnedProductDirect;
import mmb.cargo.model.ReturnedProductDirectCatalog;
import mmb.cargo.model.ReturnedProductDirectFloor;
import mmb.cargo.model.ReturnedProductDirectLog;
import mmb.cargo.model.ReturnedProductDirectPassage;
import mmb.cargo.model.ReturnedProductDirectRequestBean;
import mmb.cargo.model.ReturnedProductVirtualRequestBean;
import mmb.cargo.service.IReturnedProductDirectService;
import mmb.rec.sys.easyui.EasyuiDataGridJson;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.util.DateUtil;

@Service("returnedProductDirect")
public class ReturnedProductDirectService implements IReturnedProductDirectService {
	@Resource
	private ReturnedProductDirectDao returnedProductDirectDao;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean createDirect(ReturnedProductDirectRequestBean requetBean, voUser adminUser) {
		// 主表
		ReturnedProductDirect bean = new ReturnedProductDirect();
		Date currentDate = Calendar.getInstance().getTime();
		//指向code
		bean.setDirectCode(this.getDirectCode());
		//仓库
		bean.setStorageId(new Integer(requetBean.getStorage()));
		//仓库区域
		bean.setStockAreaId(new Integer(requetBean.getStorageArea()));
		//默认仓库区域
		if (!"".equals(requetBean.getDefaultStorageArea())) {
			bean.setDefaultStockAreaId(new Integer(requetBean.getDefaultStorageArea()));
		}
		//状态新增时为已生效
		bean.setStatus("1");
		bean.setOperatorId(adminUser.getId());
		bean.setCreateDatetime(currentDate);
		//主表信息插入
		int directId = returnedProductDirectDao.insert(bean);
		//一级分类信息插入
		for (String catalog : requetBean.getFirstCatalog()) {
			if (!"".equals(catalog)) {
				ReturnedProductDirectCatalog catalogBean = new ReturnedProductDirectCatalog();
				catalogBean.setCatalogLevel("1");
				catalogBean.setCatalogId(Integer.valueOf(catalog));
				catalogBean.setDirectId(directId);
				returnedProductDirectDao.insertCatalog(catalogBean);
			}
		}
		//二级分类信息插入
		for (String catalog : requetBean.getSecondCatalog()) {
			if (!"".equals(catalog)) {
				ReturnedProductDirectCatalog catalogBean = new ReturnedProductDirectCatalog();
				catalogBean.setCatalogLevel("2");
				catalogBean.setCatalogId(Integer.valueOf(catalog));
				catalogBean.setDirectId(directId);
				returnedProductDirectDao.insertCatalog(catalogBean);
			}
		}
		//三级分类信息插入
		for (String catalog : requetBean.getThirdCatalog()) {
			if (!"".equals(catalog)) {
				ReturnedProductDirectCatalog catalogBean = new ReturnedProductDirectCatalog();
				catalogBean.setCatalogLevel("3");
				catalogBean.setCatalogId(Integer.valueOf(catalog));
				catalogBean.setDirectId(directId);
				returnedProductDirectDao.insertCatalog(catalogBean);
			}
		}
		//巷道信息插入
		for (String passage : requetBean.getPassage()) {
			if (!"".equals(passage)) {
				ReturnedProductDirectPassage passageBean = new ReturnedProductDirectPassage();
				passageBean.setPassageId(Integer.valueOf(passage));
				passageBean.setDirectId(directId);
				returnedProductDirectDao.insertPassage(passageBean);
			}
		}
		//层数信息插入
		StringBuilder floorStr = new StringBuilder("");
		for (String floor : requetBean.getFloorNum()) {
			if (!"".equals(floor)) {
				ReturnedProductDirectFloor floorBean = new ReturnedProductDirectFloor();
				floorBean.setFloorNum(Integer.valueOf(floor));
				floorBean.setDirectId(directId);
				returnedProductDirectDao.insertFloor(floorBean);
				floorStr.append("," + floor);
			}
		}
		//操作记录
		ReturnedProductDirectLog logBean = new ReturnedProductDirectLog();
		logBean.setActivityType("1");
		logBean.setActivityDetail("创建退货上架指向");
		logBean.setDirectId(directId);
		logBean.setOperatorId(adminUser.getId());
		logBean.setCreateDatetime(currentDate);
		StringBuilder sb = new StringBuilder("新建退货上架指向    ");
		sb.append("所选仓库:" + requetBean.getStorageName());
		sb.append(";区域:" + requetBean.getStorageAreaName());
		sb.append(";巷道:" + requetBean.getPassageName());
		sb.append(";层:" + floorStr.substring(1));
		sb.append(";指向分类  一级分类选择:" + requetBean.getFirstCatalogName());
		if (!"".equals(requetBean.getSecondCatalogName())) {
			sb.append(";二级分类选择:" + requetBean.getSecondCatalogName());
		}
		if (!"".equals(requetBean.getThirdCatalogName())) {
			sb.append(";三级分类选择:" + requetBean.getThirdCatalogName());
		}
		if (!"".equals(requetBean.getDefaultStorageAreaName())) {
			sb.append(";默认区域:" + requetBean.getDefaultStorageAreaName());
		}
		logBean.setContent(sb.toString());
		returnedProductDirectDao.insertLog(logBean);
		return true;
	}

	@Override
	public boolean updateDirect(ReturnedProductDirectRequestBean requetBean, voUser adminUser) {
		// 主表
		ReturnedProductDirect bean = new ReturnedProductDirect();
		Date currentDate = Calendar.getInstance().getTime();
		//主键
		bean.setId(requetBean.getDirectId());
		//仓库
		bean.setStorageId(new Integer(requetBean.getStorage()));
		//仓库区域
		bean.setStockAreaId(new Integer(requetBean.getStorageArea()));
		//默认仓库区域
		if (!"".equals(requetBean.getDefaultStorageArea())) {
			bean.setDefaultStockAreaId(new Integer(requetBean.getDefaultStorageArea()));
		}else{
			bean.setDefaultStockAreaId(null);
		}
		bean.setOperatorId(adminUser.getId());
		bean.setCreateDatetime(currentDate);
		returnedProductDirectDao.updateByPrimaryKeySelective(bean);
		//子表数据先删后插
		returnedProductDirectDao.deleteCatalogBydirectId(requetBean.getDirectId());
		returnedProductDirectDao.deleteFloorBydirectId(requetBean.getDirectId());
		returnedProductDirectDao.deletePassageBydirectId(requetBean.getDirectId());
		//一级分类信息插入
		for (String catalog : requetBean.getFirstCatalog()) {
			if (!"".equals(catalog)) {
				ReturnedProductDirectCatalog catalogBean = new ReturnedProductDirectCatalog();
				catalogBean.setCatalogLevel("1");
				catalogBean.setCatalogId(Integer.valueOf(catalog));
				catalogBean.setDirectId(requetBean.getDirectId());
				returnedProductDirectDao.insertCatalog(catalogBean);
			}
		}
		//二级分类信息插入
		for (String catalog : requetBean.getSecondCatalog()) {
			if (!"".equals(catalog)) {
				ReturnedProductDirectCatalog catalogBean = new ReturnedProductDirectCatalog();
				catalogBean.setCatalogLevel("2");
				catalogBean.setCatalogId(Integer.valueOf(catalog));
				catalogBean.setDirectId(requetBean.getDirectId());
				returnedProductDirectDao.insertCatalog(catalogBean);
			}
		}
		//三级分类信息插入
		for (String catalog : requetBean.getThirdCatalog()) {
			if (!"".equals(catalog)) {
				ReturnedProductDirectCatalog catalogBean = new ReturnedProductDirectCatalog();
				catalogBean.setCatalogLevel("3");
				catalogBean.setCatalogId(Integer.valueOf(catalog));
				catalogBean.setDirectId(requetBean.getDirectId());
				returnedProductDirectDao.insertCatalog(catalogBean);
			}
		}
		//巷道信息插入
		for (String passage : requetBean.getPassage()) {
			if (!"".equals(passage)) {
				ReturnedProductDirectPassage passageBean = new ReturnedProductDirectPassage();
				passageBean.setPassageId(Integer.valueOf(passage));
				passageBean.setDirectId(requetBean.getDirectId());
				returnedProductDirectDao.insertPassage(passageBean);
			}
		}
		//层数信息插入
		StringBuilder floorStr = new StringBuilder("");
		for (String floor : requetBean.getFloorNum()) {
			if (!"".equals(floor)) {
				ReturnedProductDirectFloor floorBean = new ReturnedProductDirectFloor();
				floorBean.setFloorNum(Integer.valueOf(floor));
				floorBean.setDirectId(requetBean.getDirectId());
				returnedProductDirectDao.insertFloor(floorBean);
				floorStr.append("," + floor);
			}
		}
		//操作记录
		ReturnedProductDirectLog logBean = new ReturnedProductDirectLog();
		logBean.setActivityType("1");
		logBean.setActivityDetail("修改提交");
		logBean.setDirectId(requetBean.getDirectId());
		logBean.setOperatorId(adminUser.getId());
		logBean.setCreateDatetime(currentDate);
		StringBuilder sb = new StringBuilder("修改退货上架指向    ");
		sb.append("所选仓库:" + requetBean.getStorageName());
		sb.append(";区域:" + requetBean.getStorageAreaName());
		sb.append(";巷道:" + requetBean.getPassageName());
		sb.append(";层:" + floorStr.substring(1));
		sb.append(";指向分类  一级分类选择:" + requetBean.getFirstCatalogName());
		if (!"".equals(requetBean.getSecondCatalogName())) {
			sb.append(";二级分类选择:" + requetBean.getSecondCatalogName());
		}
		if (!"".equals(requetBean.getThirdCatalogName())) {
			sb.append(";三级分类选择:" + requetBean.getThirdCatalogName());
		}
		if (!"".equals(requetBean.getDefaultStorageAreaName())) {
			sb.append(";默认区域:" + requetBean.getDefaultStorageAreaName());
		}
		logBean.setContent(sb.toString());
		returnedProductDirectDao.insertLog(logBean);
		return true;
	}
	
	@Override
	public String getMaxFloorNum(String passage) {
		return returnedProductDirectDao.getMaxFloorNum(passage);
	}

	@Override
	public EasyuiDataGridJson getDirectData(ReturnedProductDirectRequestBean requestBean) {
		//分页查询
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		requestBean.setStartRow((requestBean.getPage() - 1) * requestBean.getRows());
		List<Map<String,String>> list = returnedProductDirectDao.getDirectList(requestBean);
		//获取库类型
		for(Map<String,String> map:list){
			map.put("stockTypeName", String.valueOf((CargoInfoBean.stockTypeMap.get(Integer.valueOf(map.get("stockType"))))));
		}
		json.setRows(list);
		//查询总数
		json.setTotal(returnedProductDirectDao.getDirectListCount(requestBean));
		return json;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean cancelDirect(Integer directId, voUser adminUser) {
		Date currentDate = Calendar.getInstance().getTime();
		ReturnedProductDirect bean = new ReturnedProductDirect();
		bean.setId(directId);
		//状态为已作废
		bean.setStatus("0");
		bean.setOperatorId(adminUser.getId());
		bean.setCreateDatetime(currentDate);

		ReturnedProductDirectLog logBean = new ReturnedProductDirectLog();
		logBean.setActivityType("3");
		logBean.setActivityDetail("作废");
		logBean.setDirectId(directId);
		logBean.setOperatorId(adminUser.getId());
		logBean.setCreateDatetime(currentDate);
		logBean.setContent("作废退货上架指向    退货上架指向状态由【已生效】变更为【已作废】");
		
		returnedProductDirectDao.updateByPrimaryKeySelective(bean);
		returnedProductDirectDao.insertLog(logBean);
		return true;
	}

	@Override
	public EasyuiDataGridJson getPassageDetailLs(ReturnedProductDirectRequestBean requestBean) {
		//分页查询
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		requestBean.setStartRow((requestBean.getPage() - 1) * requestBean.getRows());
		List<Map<String,String>> list = returnedProductDirectDao.getPassageDetailLs(requestBean);
		//获取库类型
		for(Map<String,String> map:list){
			map.put("stockTypeName", String.valueOf((CargoInfoBean.stockTypeMap.get(Integer.valueOf(map.get("stockType"))))));
		}
		json.setRows(list);
		//查询总数
		json.setTotal(returnedProductDirectDao.getPassageDetailCount(requestBean));
		return json;
	}
	
	@Override
	public EasyuiDataGridJson getDirectLogLs(ReturnedProductDirectRequestBean requestBean) {
		//分页查询
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		requestBean.setStartRow((requestBean.getPage() - 1) * requestBean.getRows());
		List<Map<String,String>> list = returnedProductDirectDao.getDirectLogLs(requestBean);
		json.setRows(list);
		//查询总数
		json.setTotal(returnedProductDirectDao.getDirectLogCount(requestBean));
		return json;
	}
	
	@Override
	public EasyuiDataGridJson getVirtualData(ReturnedProductVirtualRequestBean requestBean) {
		//清理退货上架临时表（退货上架单为结束状态）
		returnedProductDirectDao.cleanVirtual();
		//分页查询
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		int startRow=(requestBean.getPage() - 1) * requestBean.getRows();
		requestBean.setStartRow(startRow);
		List<Map<String,String>> rsList=returnedProductDirectDao.getVirtualList(requestBean);
		if(rsList!=null&&rsList.size()>0){
			startRow=startRow+1;
			for(int i=0;i<rsList.size();i++){
				Map<String,String> map = rsList.get(i);
				map.put("rownum", String.valueOf(startRow+i));
			}
		}
		json.setRows(rsList);
		//查询总数
		json.setTotal(returnedProductDirectDao.getVirtualListCount(requestBean));
		return json;
	}
	
	@Override
	public boolean deleteVirtualBatch(String virtualId) {
		//转成list
		String[] arr = virtualId.split(",");
		List<String> list=new ArrayList<String>();
		for(String str:arr){
			list.add(str);
		}
		returnedProductDirectDao.deleteVirtualBatch(list);
		return true;
	}
	
	/**
	 * 
	 * @descripion 获取指向编号
	 * @author 刘仁华
	 * @time 2015年2月4日
	 */
	private String getDirectCode() {
		Date date = Calendar.getInstance().getTime();
		String curDirectCode = returnedProductDirectDao.getMaxDirectCode("TZX" + DateUtil.formatDate(date, "yyMMdd"));
		if (curDirectCode == null || "".equals(curDirectCode)) {
			return "TZX" + DateUtil.formatDate(date, "yyMMdd") + "00001";
		} else {
			String code = String.valueOf(Integer.parseInt(curDirectCode.substring(9)) + 1);
			while (code.length() < 5) {
				code = "0" + code;
			}
			return curDirectCode.substring(0, 9) + code;
		}
	}
}
