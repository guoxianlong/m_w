package mmb.cargo.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.cargo.dao.ReturnedProductDirectDao;
import mmb.cargo.model.ReturnedProductDirect;
import mmb.cargo.model.ReturnedProductDirectCatalog;
import mmb.cargo.model.ReturnedProductDirectFloor;
import mmb.cargo.model.ReturnedProductDirectLog;
import mmb.cargo.model.ReturnedProductDirectPassage;
import mmb.cargo.model.ReturnedProductDirectRequestBean;
import mmb.cargo.model.ReturnedProductVirtualRequestBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository("returnedProductDirectDao")
public class ReturnedProductDirectMapper extends AbstractDaoSupport implements ReturnedProductDirectDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return ((Integer) getSession().delete(id)).intValue();
	}

	@Override
	public int insert(ReturnedProductDirect record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSelective(ReturnedProductDirect record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public ReturnedProductDirect selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(ReturnedProductDirect record) {
		return getSession().update(record);
	}

	@Override
	public int updateByPrimaryKey(ReturnedProductDirect record) {
		return getSession().update(record);
	}

	@Override
	public int insertCatalog(ReturnedProductDirectCatalog record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertFloor(ReturnedProductDirectFloor record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertLog(ReturnedProductDirectLog record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertPassage(ReturnedProductDirectPassage record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public String getMaxDirectCode(String directCode) {
		return getSession(DynamicDataSource.SLAVE).selectOne(directCode);
	}

	@Override
	public String getMaxFloorNum(String passage) {
		List<Object> list = getSession(DynamicDataSource.SLAVE).selectList(passage);
		if(list!=null&&list.size()>0){
			return (String)list.get(0);
		}else{
			return "0";
		}
	}

	@Override
	public List<Map<String, String>> getDirectList(ReturnedProductDirectRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectList(requestBean);
	}

	@Override
	public Long getDirectListCount(ReturnedProductDirectRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectOne(requestBean);
	}

	@Override
	public int deleteCatalogBydirectId(Integer directId) {
		return ((Integer) getSession().delete(directId)).intValue();
	}

	@Override
	public int deleteFloorBydirectId(Integer directId) {
		return ((Integer) getSession().delete(directId)).intValue();
	}

	@Override
	public int deletePassageBydirectId(Integer directId) {
		return ((Integer) getSession().delete(directId)).intValue();
	}

	@Override
	public List<Map<String, String>> getPassageDetailLs(ReturnedProductDirectRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectList(requestBean);
	}

	@Override
	public Long getPassageDetailCount(ReturnedProductDirectRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectOne(requestBean);
	}

	@Override
	public List<Map<String, String>> getDirectLogLs(ReturnedProductDirectRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectList(requestBean);
	}

	@Override
	public Long getDirectLogCount(ReturnedProductDirectRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectOne(requestBean);
	}

	@Override
	public List<Map<String, String>> getVirtualList(ReturnedProductVirtualRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectList(requestBean);
	}

	@Override
	public Long getVirtualListCount(ReturnedProductVirtualRequestBean requestBean) {
		return getSession(DynamicDataSource.SLAVE).selectOne(requestBean);
	}

	@Override
	public int deleteVirtualBatch(List<String> list) {
		return ((Integer) getSession().delete(list)).intValue();
	}

	@Override
	public int cleanVirtual() {
		return ((Integer) getSession().delete(null)).intValue();
	}

}
