package mmb.rec.pda.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.pda.bean.JsonModel;
import net.sf.json.JSONObject;

public class ReceiveJson {
	public static JsonModel receiveJson(HttpServletRequest request) {
		JsonModel jsonModel = null;
		StringBuilder sb = null;
		BufferedReader br = null;
		String line = null;
		try {
			sb = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			while((line=br.readLine()) != null) {
				sb.append(line);
			}
			if(sb != null && !"".equals(sb.toString())){
				JSONObject jsonObject = JSONObject.fromObject(sb.toString());
				//Map<String, Class> classMap = new HashMap<String, Class>(); 
				//Class obj = Class.forName(className);
				//classMap.put(className.substring(className.lastIndexOf(".")), obj);
				jsonModel = (JsonModel) JSONObject.toBean(jsonObject, JsonModel.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return jsonModel;
	}
}
