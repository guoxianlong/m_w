package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.bean.order.AuditPackageBean;

import mmb.rec.oper.dao.AuditPackageDao;
@Repository
public class AuditPackageMapper extends AbstractDaoSupport implements AuditPackageDao {

	@Override
	public int addAuditPackage(AuditPackageBean auditPackageBean) {
		getSession().insert(auditPackageBean);
		return auditPackageBean.getId();
	}

	@Override
	public AuditPackageBean getAuditPackage(String condition) {
		// TODO Auto-generated method stub
		return getSession().selectOne(condition);
	}

	@Override
	public List<AuditPackageBean> getAuditPackageList(
			Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		return getSession().selectList(paramMap);
	}

	@Override
	public List<AuditPackageBean> getAuditPackageListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
