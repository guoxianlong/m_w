package adultadmin.service;

import java.util.ArrayList;

import adultadmin.bean.ProxyBean;
import adultadmin.service.infc.IBaseService;
/**
 * 代理商管理
 * @author 
 *
 */
public interface IProxyManagerService extends IBaseService{
	public boolean addProxy(ProxyBean bean);

    public ProxyBean getProxy(String condition);

    public int getProxyCount(String condition);

    public boolean updateProxy(String set, String condition);

    public boolean deleteProxy(String condition);

    public ArrayList getProxyList(String condition, int index,
            int count, String orderBy);
    
    public int getSeqNumber(String fieldName, String function,
            String condition);
}
