package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.ProductPackageDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.bean.ProductPackageBean;
@Repository
public class ProductPackageMapper extends AbstractDaoSupport implements ProductPackageDao {

	@Override
	public int addProductPackage(ProductPackageBean productPackageBean) {
		getSession().insert(productPackageBean);
		return 0;
	}

	@Override
	public ProductPackageBean getProductPackage(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<ProductPackageBean> getProductPackageList(
			Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<ProductPackageBean> getProductPackageListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}
	
	

}
