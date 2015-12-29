package mmb.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.cargo.model.ReturnedProductDirect;
import mmb.cargo.model.ReturnedProductDirectCatalog;
import mmb.cargo.model.ReturnedProductDirectFloor;
import mmb.cargo.model.ReturnedProductDirectLog;
import mmb.cargo.model.ReturnedProductDirectPassage;
import mmb.cargo.model.ReturnedProductDirectRequestBean;
import mmb.cargo.model.ReturnedProductVirtualRequestBean;

public interface ReturnedProductDirectDao {
    public int deleteByPrimaryKey(Integer id);

    public int insert(ReturnedProductDirect record);

    public int insertSelective(ReturnedProductDirect record);

    public ReturnedProductDirect selectByPrimaryKey(Integer id);

    public int updateByPrimaryKeySelective(ReturnedProductDirect record);

    public int updateByPrimaryKey(ReturnedProductDirect record);
    
    public int insertCatalog(ReturnedProductDirectCatalog record);
    
    public int insertFloor(ReturnedProductDirectFloor record);
    
    public int insertLog(ReturnedProductDirectLog record);
    
    public int insertPassage(ReturnedProductDirectPassage record);
    
    public String getMaxDirectCode(String directCode);
    
    public String getMaxFloorNum(String passage);
    
    public List<Map<String,String>> getDirectList(ReturnedProductDirectRequestBean requestBean);
    
    public Long getDirectListCount(ReturnedProductDirectRequestBean requestBean);
    
    public int deleteCatalogBydirectId(Integer directId);
    
    public int deleteFloorBydirectId(Integer directId);
    
    public int deletePassageBydirectId(Integer directId);
    
    public List<Map<String,String>> getPassageDetailLs(ReturnedProductDirectRequestBean requestBean);
    
    public Long getPassageDetailCount(ReturnedProductDirectRequestBean requestBean);
    
    public List<Map<String,String>> getDirectLogLs(ReturnedProductDirectRequestBean requestBean);
    
    public Long getDirectLogCount(ReturnedProductDirectRequestBean requestBean);
    
    public List<Map<String,String>> getVirtualList(ReturnedProductVirtualRequestBean requestBean);
    
    public Long getVirtualListCount(ReturnedProductVirtualRequestBean requestBean);
    
    public int deleteVirtualBatch(List<String> list);
    
    public int cleanVirtual();
    
}
