package adultadmin.service;

import java.util.ArrayList;
import java.util.List;

import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.bybs.BsbyReason;
import adultadmin.bean.stock.ProductGroupBean;
import adultadmin.service.infc.IBaseService;

public interface IBsByServiceManagerService extends IBaseService {
	public int getByBsOperationnoteCount(String condition);

	public ArrayList getByBsOperationnoteList(String condition, int index,
			int count, String orderBy);

	public BsbyOperationnoteBean getBuycode(String condition);

	public boolean addBsbyOperationnoteBean(BsbyOperationnoteBean bean);

	public BsbyOperationnoteBean getBsbyOperationnoteBean(String condition);

	public boolean addBsbyOperationRecord(BsbyOperationRecordBean bean);

	public boolean updateBsbyOperationnoteBean(String set, String condition);

	public BsbyProductBean getBsbyProductBean(String condition);

	public boolean addBsbyProduct(BsbyProductBean bean);

	public ArrayList getBsbyOperationRecordList(String condition, int index,
			int count, String orderBy);
	
	public ArrayList getBsbyProductList(String condition, int index,
			int count, String orderBy);
	
	public boolean deleteBsbyProduct(String condition);
	
	public boolean deleteBsbyOperationnote(String condition);
	
	public boolean deleteBsbyOperationRecord(String condition);
	public boolean updateBsbyProductBean(String set, String condition);
	
	//bsby_product_cargo
    public boolean addBsbyProductCargo(BsbyProductCargoBean bean);

    public BsbyProductCargoBean getBsbyProductCargo(String condition);

    public int getBsbyProductCargoCount(String condition);

    public boolean updateBsbyProductCargo(String set, String condition);

    public boolean deleteBsbyProductCargo(String condition);

    public ArrayList getBsbyProductCargoList(String condition, int index,
            int count, String orderBy);
    public List<BsbyReason> getBsbyReasonList(int type);
	public List<BsbyReason> getBsbyReasonListDistinct();
    public float returnFinanceProductPrice(int productId);
    public String returnCargoCode(int id);
	public BsbyReason getBsbyReasonByCondition(String condition);
}
