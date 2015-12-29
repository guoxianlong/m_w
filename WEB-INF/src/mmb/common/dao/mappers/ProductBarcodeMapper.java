package mmb.common.dao.mappers;

import mmb.common.dao.ProductBarcodeDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.action.vo.ProductBarcodeVO;
@Repository
public class ProductBarcodeMapper extends AbstractDaoSupport implements ProductBarcodeDao {

	@Override
	public ProductBarcodeVO getProductBarcode(String condition) {
		return getSession().selectOne(condition);
	}

}
