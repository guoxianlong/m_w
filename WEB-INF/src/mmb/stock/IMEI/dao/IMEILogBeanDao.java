package mmb.stock.IMEI.dao;

import java.util.List;

import mmb.stock.IMEI.IMEILogBean;


public interface IMEILogBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(IMEILogBean record);

    IMEILogBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IMEILogBean record);

    int updateByPrimaryKey(IMEILogBean record);
    
    int batchInsertIMEILog(List<IMEILogBean> list);
}