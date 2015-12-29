package adultadmin.service.impl;

import java.util.List;

import adultadmin.bean.cargo.WareJobResourceBean;
import adultadmin.service.infc.IWareJobResourceService;
import adultadmin.util.db.DbOperation;

public class WareJobResourceServiceImpl extends BaseServiceImpl implements IWareJobResourceService {

	public WareJobResourceServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType=useConnType;
		this.dbOp=dbOp;
	}
	
	public WareJobResourceServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<WareJobResourceBean> getWareJobResourceList(String condition,int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "ware_job_resource", "adultadmin.bean.cargo.WareJobResourceBean");
	}

}
