/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;
import java.util.Map;

import adultadmin.bean.system.TextDictBean;
import adultadmin.bean.system.TextResBean;

/**
 * 作者：张陶
 * 
 * 创建日期：2007-5-12
 * 
 * 说明：
 */
public interface ISystemService extends IBaseService {
	public ArrayList getTextResList(String condition, int index, int count,
			String orderBy);

	public TextResBean getTextRes(String condition);

	public int getTextResCount(String condition);

	public boolean addTextRes(TextResBean textRes);

	public boolean updateTextRes(String set, String condition);

	public boolean deleteTextRes(String condition);

	public ArrayList getTextDictList(String condition, int index, int count,
			String orderBy);
	public TextDictBean getTextDict(String condition);

	public int getTextDictCount(String condition);

	public boolean addTextDict(TextDictBean textDict);

	public boolean updateTextDict(String set, String condition);

	public boolean deleteTextDict(String condition);
	
	public Map getTextDictNumMap();
}
