package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.order.AuditPackageBean;

public interface AuditPackageDao {
	
	/**
	 * 添加AuditPackage
	 * @param auditPackagBean
	 * @return
	 */
	public int addAuditPackage(AuditPackageBean auditPackageBean);
	/**
	 * 查AuditPackage
	 * @param condition
	 * @return
	 */
	public AuditPackageBean getAuditPackage(String condition);
	
	/**
	 * 查询符合条件的AuditPackage的List
	 * @param paramMap
	 * @return
	 */
	public List<AuditPackageBean> getAuditPackageList(Map<String,String> paramMap);
	/**
	 * 查询符合条件的AuditPackage的List
	 * @param paramMap
	 * @return
	 */
	public List<AuditPackageBean> getAuditPackageListSlave(Map<String,String> paramMap);

}
