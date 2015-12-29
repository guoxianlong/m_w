package adultadmin.service.infc;

import java.util.List;

import adultadmin.bean.cargo.WareJobResourceBean;

public interface IWareJobResourceService extends IBaseService {
	public List<WareJobResourceBean> getWareJobResourceList(String condition,int index,int count,String orderBy);
}
