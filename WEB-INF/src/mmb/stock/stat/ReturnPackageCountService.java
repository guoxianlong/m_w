package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class ReturnPackageCountService extends BaseServiceImpl{
	
	public ReturnPackageCountService(int useConnType, DbOperation dbOp){
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}
	
	public ReturnPackageCountService(){
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public String getCountResult(String time,String userId,String areaId,String produceCode){
		String querySql="select count(distinct t.package_code),sum(s.stockout_count) ,count(distinct s.product_code) from returned_package t join order_stock o on t.order_code=o.order_code join order_stock_product s on o.id=s.order_stock_id join product_barcode pb on pb.product_id = s.product_id where t.status=1 and left(t.storage_time,10)='"+time+"'";
		if(userId!=null && !"".equals(userId)){
			querySql += " and t.operator_name='"+userId+"'";
		}
		if(areaId!=null && !"".equals(areaId)){
			querySql += " and t.area='"+areaId+"'";
		}
		if(produceCode!=null && !"".equals(produceCode)){
			querySql += " and (s.product_code='"+produceCode+"' or pb.barcode='"+produceCode+"')";
		}
		DbOperation dbOp = getDbOp();
		dbOp.init("adult_slave");
		ResultSet rs = dbOp.executeQuery(querySql);
		String result = "";
		try {
			while(rs.next()){
				String rs1 = rs.getString(1)==null?"0":rs.getString(1);
				String rs2 = rs.getString(2)==null?"0":rs.getString(2);
				String rs3 = rs.getString(3)==null?"0":rs.getString(3);
				result = result + rs1+","+rs2+","+rs3;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String getCountResult(String time,String userId,String areaId){
		String querySql="select count(distinct t.package_code) ,sum(pc.count) ,count(distinct pc.product_code)from return_package_check t  join return_package_check_product pc on t.id=pc.return_package_check_id where left(t.check_time,10)='"+time+"'";
		if(userId!=null && !"".equals(userId)){
			querySql += " and t.check_user_name='"+userId+"'";
		}
		if(areaId!=null && !"".equals(areaId)){
			querySql += " and t.area='"+areaId+"'";
		}
		DbOperation dbOp = getDbOp();
		dbOp.init("adult_slave");
		ResultSet rs = dbOp.executeQuery(querySql);
		String result = "";
		//System.out.println(querySql);
		try {
			while(rs.next()){
				String rs1 = rs.getString(1)==null?"0":rs.getString(1);
				String rs2 = rs.getString(2)==null?"0":rs.getString(2);
				String rs3 = rs.getString(3)==null?"0":rs.getString(3);
				result = result + rs1+","+rs2+","+rs3;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
