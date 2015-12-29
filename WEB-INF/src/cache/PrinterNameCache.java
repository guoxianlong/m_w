package cache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import mmb.ware.WareService;

public class PrinterNameCache {
	public static HashMap printerNameMap;
	static{
		init();
	}
	public static void init(){
		WareService service = new WareService();
		printerNameMap=new HashMap();
		String sql="select * from printer_name order by type";
		service.getDbOp().prepareStatement(sql);
		PreparedStatement ps = service.getDbOp().getPStmt();
		try {
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				int buyMode=rs.getInt("buy_mode");
				int deliver=rs.getInt("deliver");
				String printerName=rs.getString("printer_name");
				if(printerNameMap.get(buyMode+"-"+deliver)==null){
					ArrayList list=new ArrayList();
					list.add(printerName);
					printerNameMap.put(buyMode+"-"+deliver, list);
				}else{
					ArrayList list=(ArrayList)printerNameMap.get(buyMode+"-"+deliver);
					list.add(printerName);
				}
				//printerNameMap.put(buyMode+"-"+deliver, printerName);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			service.releaseAll();
		}
	}
}
