package mmb.tms.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.action.vo.voUser;

import mmb.tms.model.TrunkCorpInfo;

public interface ITrunkLineService {
	
	/**
	 * 添加干线公司
	 * @author ahc
	 */
	public int addTrunk(TrunkCorpInfo t);
	
	/**
	 * 获取干线公司
	 * @author ahc
	 */
	public List<TrunkCorpInfo> getTrunk(Map<String,String> map);
	
	/**
	 * 获取干线公司总数
	 * @author ahc
	 */
	public int getTrunkCount(Map<String,String> map);
	
	
	/**
	 * 逻辑删除干线公司
	 * @author ahc
	 */
	public int upDateTrunkCorpInfo(Map<String,String> map);
	
	/**
	 * 上传干线时效Excel
	 * @author ahc
	 */
	public String uploadTrunkEffectExcel(voUser user,HttpServletRequest request) throws IOException;
	
	/**
	 * 下载干线时效模板Excel
	 * @author ahc
	 */
	public void downloadTrunkEffectExcel(voUser user,HttpServletRequest request,HttpServletResponse response) throws IOException;
	
	
	
}
