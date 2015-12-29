package mmb.stock.spare.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.spare.model.ImeiSpareStockinBean;

public interface ImeiSpareStockinBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ImeiSpareStockinBean record);

    int insertSelective(ImeiSpareStockinBean record);

    ImeiSpareStockinBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ImeiSpareStockinBean record);

    int updateByPrimaryKey(ImeiSpareStockinBean record);
    
    int batchInsertBean(List<ImeiSpareStockinBean> list);
    
    int updateIMEISpareStockinByCondition(Map<String,String> map);
}