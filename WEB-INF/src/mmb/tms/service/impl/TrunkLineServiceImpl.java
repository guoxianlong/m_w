package mmb.tms.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.CommonDao;
import mmb.tms.dao.DeliverAdminUserDao;
import mmb.tms.dao.TrunkEffectDao;
import mmb.tms.dao.TrunkLineDao;
import mmb.tms.model.TrunkCorpInfo;
import mmb.tms.model.TrunkEffect;
import mmb.tms.model.TrunkEffectForExcel;
import mmb.tms.model.TrunkEffectLog;
import mmb.tms.service.ITrunkEffectLogService;
import mmb.tms.service.ITrunkLineService;
import mmb.util.ExcelUtil;
import mmb.util.excel.AbstractExcel;
import mmb.util.excel.ExportExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Service
public class TrunkLineServiceImpl implements ITrunkLineService {
	
	@Autowired
	private TrunkLineDao trunkLineDao;
	@Autowired
	private DeliverAdminUserDao deliverAdminUserDao;
	@Autowired
	public CommonDao commonMapper;
	@Autowired
	public  TrunkEffectDao trunkEffectDao;
	@Autowired
	private ITrunkEffectLogService iTrunkEffectLogService;
	
	@Override
	public int addTrunk(TrunkCorpInfo t) {
		// TODO Auto-generated method stub
		return trunkLineDao.addTrunk(t);
	}
	@Override
	public List<TrunkCorpInfo> getTrunk(Map<String, String> map) {
		// TODO Auto-generated method stub
		return trunkLineDao.getTrunkCorpInfo(map);
	}
	
	@Override
	public int getTrunkCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return trunkLineDao.getTrunkCorpInfoCount(map);
	}
	@Override
	public int upDateTrunkCorpInfo(Map<String, String> map) {
		// TODO Auto-generated method stub
		return trunkLineDao.upDateTrunkCorpInfo(map);
	}
	@Override
	public String uploadTrunkEffectExcel(voUser user,HttpServletRequest request) throws IOException {
		user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "");
			request.setAttribute("msg", "您还没有登录");
			return "admin/orderStock/uploadTrunkEffect";
		}
		 // 转型为MultipartHttpRequest：   
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        // 获得文件：   
        MultipartFile file = multipartRequest.getFile("attendance");
        // 获得输入流：   
        InputStream input = file.getInputStream();   
        // 获得文件名：   
        String attendanceFileName = file.getOriginalFilename();
		int index = attendanceFileName.lastIndexOf(".");
		String suffix = attendanceFileName.substring(index + 1, attendanceFileName.length());
		StringBuilder msg = new StringBuilder();
		List<String[]> rows = ExcelUtil.rosolveFile(input, suffix, 1);
		String result = null;
		
		if (rows != null  && rows.size() > 0) {
			int total = 0;
			int success = 0;
			TrunkEffectForExcel gen = null;
			boolean empty = true;
			List<TrunkEffectForExcel> list = new ArrayList<TrunkEffectForExcel>();
			for (int i = 0; i < rows.size(); i++) {			
				String[] row = rows.get(i);
				if (row == null || row.length == 0) {
					continue;
				}
				empty = true;
				for (int j = 0; j < row.length; j++) {
					if(!"".equals(row[j]))
						empty = false;
				}
				// 全部列为空不处理
				if(empty){
					continue;
				}				
				try {			
					total++;	
					if(row.length < 6){
						msg.append("第").append(i + 1).append("行, 数据列数不合法（至少6列）<br/>");
						continue;
					}
					gen = new TrunkEffectForExcel();
					gen.setTrunkName(row[0].trim());
					gen.setDeliverAdminName(row[1].trim());
					gen.setStockArea(row[2].trim());
					gen.setDeliverName(row[3].trim());
					gen.setMode(row[4].trim());
					gen.setTime(row[5].trim());
				} catch (NullPointerException e) {
					msg.append("第").append(i + 1).append("行, 数据为空<br/>");
					continue;
				} catch (Exception e) {
					msg.append("第").append(i + 1).append("行, ").append(e.getMessage()).append("<br/>");
					continue;
				}
				
				gen.setIndex(i);
				list.add(gen);				
			}

			int rowIndex = 0;
			for (int i = 0; i < list.size(); i++) {
				gen = list.get(i);
				rowIndex = gen.getIndex();
				int trunkId =0;
				int deliverAdminId =0;
				int stockAreaId=0;
				int deliverCorpInfoId =0;
				int mode =0;
				int time=0;
				try {
					Map<String,String> map = new HashMap<String,String>();
					map.put("column", "id");
					map.put("table", "trunk_corp_info");
					map.put("condition", " name = '"+gen.getTrunkName()+"' and status =0");
					List trunkCorpInfos =commonMapper.getCommonInfo(map);
					HashMap m = new HashMap();
					if(trunkCorpInfos.size()>0){
						m =(HashMap) trunkCorpInfos.get(0);
						trunkId =(Integer) m.get("id");
					}else{
						msg.append("第").append(i + 1).append("行,没有这个干线公司<br/>");
					}
					
					map.put("column", "id");
					map.put("table", "deliver_admin_user");
					map.put("condition", " username = '"+gen.getDeliverAdminName()+"' and type= 1");
					List deliverAdminUsers =commonMapper.getCommonInfo(map);
					if(deliverAdminUsers.size()>0){
						m =(HashMap) deliverAdminUsers.get(0);
						deliverAdminId =Integer.valueOf(m.get("id")+"");
					}else{
						msg.append("第").append(i + 1).append("行,没有这个用户名<br/>");
					}
					
					map.put("column", "id");
					map.put("table", "stock_area");
					map.put("condition", " name = '"+gen.getStockArea()+"' and type= 1");
					List stockAreas =commonMapper.getCommonInfo(map);
					if(stockAreas.size()>0){
						m =(HashMap) stockAreas.get(0);
						stockAreaId =Integer.valueOf(m.get("id")+"");
					}else{
						msg.append("第").append(i + 1).append("行,没有这个发货仓<br/>");
					}
					
					map.put("column", "id");
					map.put("table", "deliver_corp_info");
					map.put("condition", " name = '"+gen.getDeliverName()+"'");
					List deliverCorpInfos =commonMapper.getCommonInfo(map);
					if(deliverCorpInfos.size()>0){
						m =(HashMap)deliverCorpInfos.get(0);
						deliverCorpInfoId = Integer.valueOf(m.get("id")+"");
					}else{
						msg.append("第").append(i + 1).append("行,没有这个目的地<br/>");
					}
					
					if("公路".equals(gen.getMode().trim())){
						mode =1;
					}
					if("铁路".equals(gen.getMode().trim())){
						mode =2;
					}
					if("空运".equals(gen.getMode().trim())){
						mode =3;
					}
					
					if(gen.getTime().trim().length()>=4){
						msg.append("第").append(i + 1).append("行,时效不能超过3位数<br/>");
					}else if(!StringUtil.isNumeric(gen.getTime().trim())){
						msg.append("第").append(i + 1).append("行,时效必须为数字<br/>");
					}else if(mode==0){
						msg.append("第").append(i + 1).append("行,配送方式应为[公路],[铁路],[空运]<br/>");
					}else{
						if(trunkId!=0 && deliverAdminId!=0 && stockAreaId!=0 && deliverCorpInfoId!=0){
							map.put("column", "id");
							map.put("table", "trunk_effect");
							map.put("condition", " trunk_id ="+trunkId+" and deliver_admin_id = "+deliverAdminId+" and stock_area_id="+stockAreaId+" and deliver_id="+deliverCorpInfoId+" and mode="+mode+" and time="+gen.getTime().trim()+" and status =0");
							List deliverEffects =commonMapper.getCommonInfo(map);
							if(deliverEffects.size()>0){
								msg.append("第").append(i + 1).append("行,数据已存在，请使用修改功能<br/>");
							}else{
								map.put("condition", " trunk_id ="+trunkId+" and deliver_admin_id = "+deliverAdminId+" and stock_area_id="+stockAreaId+" and deliver_id="+deliverCorpInfoId+" and status =0");
								List<Map<String,String>> trunkEffectList =trunkEffectDao.getTrunkEffect(map);
								if(trunkEffectList.size()>0){
									TrunkEffect trunkEffect =(TrunkEffect) trunkEffectList.get(0);
									int findId =trunkEffect.getId();
									if(findId>0){
										map.put("table", "trunk_effect");
										map.put("set", " mode ="+mode+", time ="+gen.getTime());
										map.put("condition", " id="+findId);
										int r =commonMapper.updateCommon(map);
										if(r>0){
											success++;
											
											TrunkEffectLog log = new TrunkEffectLog();
											log.setOperationUserId(user.getId());
											log.setOperationUserName(user.getUsername());
											log.setTrunkId(trunkId);
											log.setDeliverAdminId(deliverAdminId);
											log.setStockArea(stockAreaId);
											log.setDeliverId(deliverCorpInfoId);
											log.setAddTime(DateUtil.getNow());
											log.setMode(mode);
											log.setTime(Integer.parseInt(gen.getTime().trim()));
											int result2 =iTrunkEffectLogService.addTrunkEffectLog(log);
											if(result2<0){
												msg.append("第").append(i + 1).append("行,添加干线日志失败！<br/>");
											}
										}
									}
								}else{
									msg.append("第").append(i + 1).append("行,没有找到相应记录，可能是该数据曾被删除过！<br/>");
								}
							}
						}
					}
					
					/*
					 String r = trunkLineDao.save(gen,user);
					if (!"OK".equals(r)) {
						msg.append("第").append(rowIndex + 1).append("行, ").append(r).append("<br/>");
					} else {
						success++;
					}
					*/
				} catch (Exception e) {
					e.printStackTrace();
					msg.append("第").append(rowIndex + 1).append("行, ").append(e.getMessage()).append("<br/>");
				}
			}
			
			result = "共" + total + "行,成功" + success + "行,失败" + (total - success) + "行.";
		} else {
			result = "Excel内容为空";
			msg.append("Excel内容为空");
		}
		
		request.setAttribute("result", result);
		request.setAttribute("msg", msg.toString());
		
		return "admin/orderStock/uploadTrunkEffect";
	}
	@Override
	public void downloadTrunkEffectExcel(voUser user,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			ExportExcel excel = new ExportExcel(AbstractExcel.HSSF);
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header = new ArrayList<String>();
			header.add("干线公司");
			header.add("用户名");
			header.add("发货仓");
			header.add("目的地");
			header.add("配送方式");
			header.add("时效H");
			 
			headers.add(header);
			
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			excel.setMayMergeColumn(mayMergeColumn);
			
			List<Integer> mayMergeRow = new ArrayList<Integer>();
			excel.setMayMergeRow(mayMergeRow);
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();	 
			excel.setColMergeCount(headers.get(0).size());			
			List<Integer> row  = new ArrayList<Integer>();
			row.add(bodies.size()-1);
			row.add(bodies.size()-3);
			row.add(bodies.size()-5);			
			List<Integer> col  = new ArrayList<Integer>();			
			excel.setRow(row);
			excel.setCol(col);			
			excel.buildListHeader(headers);
			excel.setHeader(false);
			
			excel.buildListBody(bodies);
			
			response.reset();
			response.addHeader(
					"Content-Disposition",
					"attachment;filename=import.xls");
			response.setContentType("application/vnd.ms-excel");
			response.setCharacterEncoding("utf-8");
			excel.getWorkbook().write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
