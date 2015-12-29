package mmb.stock.fitting.service;

import java.util.HashMap;
import java.util.List;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.fitting.dao.FittingStockCardListDao;
import mmb.stock.fitting.model.FittingBuyStockInBean;
import mmb.stock.fitting.model.FittingStockCard;
import mmb.stock.fitting.model.FittingStockCardPostBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.bean.stock.StockCardBean;
import adultadmin.util.StringUtil;


@Service
public class FittingStockCardService {

	@Autowired
	private FittingStockCardListDao stockCardListDao;

	public EasyuiDataGridJson getOutStockCardList(FittingStockCardPostBean postBean, EasyuiDataGrid page) {
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		String condition = postBean.buildCondition();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("condition", condition);
		map.put("index", ((page.getPage() - 1) * page.getRows()) + "");
		map.put("count", page.getRows() + "");
		map.put("orderBy", " sc.id ASC ");

		json.setTotal(Long.valueOf(this.stockCardListDao.getOutStockCardCount(condition)));

		List<FittingStockCard> list = this.stockCardListDao.getOutStockCardList(map);

		getUsername(list);

		json.setRows(list);

		return json;
	}

	public EasyuiDataGridJson getInStockCardList(FittingStockCardPostBean postBean, EasyuiDataGrid page,String stockInType) {
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		String condition = postBean.buildCondition();

		if(!"-1".equals(StringUtil.checkNull(stockInType)) && !"".equals(StringUtil.checkNull(stockInType))){
			if(stockInType.equals(String.valueOf(FittingBuyStockInBean.TYPE1))){
				condition += " and sc.card_type=" + StockCardBean.CARDTYPE_AFTERSALE_FITTING_BUYSTOCKIN;
			}else if(stockInType.equals(String.valueOf(FittingBuyStockInBean.TYPE2))){
				condition += " and sc.card_type=" + StockCardBean.CARDTYPE_AFTERSALE_FITTING_REPAIR_RETURN;
			}
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("condition", condition);
		map.put("index", ((page.getPage() - 1) * page.getRows()) + "");
		map.put("count", page.getRows() + "");
		map.put("orderBy", " sc.id ASC ");

		json.setTotal(Long.valueOf(this.stockCardListDao.getInStockCardCount(condition)));

		List<FittingStockCard> list = this.stockCardListDao.getInStockCardList(map);

		getUsername(list);

		json.setRows(list);

		return json;
	}

	private void getUsername(List<FittingStockCard> list) {
		if (list != null && list.size() != 0) {
			String select = "";
			String username = "";
			for (FittingStockCard data : list) {
				
				switch (data.getCardType()) {
				// 检测
				case 20:
				case 21:
					select = " l.user_name FROM after_sale_detect_product AS p, after_sale_detect_log AS l "; 
				    select += " WHERE p.id = l.after_sale_detect_product_id AND p.`code` = '" + data.getBillCode() +"' ";
					data.setBillType("处理单");
					break;
				// 寄回用户
				case 27:
					select = " upa.user_name FROM after_sale_detect_product AS p, after_sale_back_user_product AS up, after_sale_back_user_package AS upa ";
					select += " WHERE p.id = up.after_sale_detect_product_id AND up.package_id = upa.id AND p.`code` = '" + data.getBillCode() +"' ";
					data.setBillType("处理单");
					break;
				// 未妥投
				case 28:			
					select = " upa.receive_user_name FROM after_sale_detect_product AS p, after_sale_back_user_product AS up, after_sale_back_user_package AS upa ";
					select += " WHERE p.id = up.after_sale_detect_product_id AND up.package_id = upa.id AND p.`code` = '" + data.getBillCode() +"' ";
					data.setBillType("处理单");
					break;
					
				// 领用单号
				case 23:
				case 24:
				case 25:
				case 26:
					select = " complete_user_name FROM after_sale_receive_fitting WHERE `code` = '" + data.getBillCode() +"' ";
					data.setBillType("领用单");
					break;

				// 售后配件采购入库
				case 22:
					select = " au.username FROM buy_stockin AS bs, admin_user AS au WHERE bs.auditing_user_id = au.id AND bs.`code` = '" + data.getBillCode() +"' ";
					data.setBillType("入库单");
					break;
					// 售后配件维修返还入库
				case 31:
					select = " au.username FROM buy_stockin AS bs, admin_user AS au WHERE bs.auditing_user_id = au.id AND bs.`code` = '" + data.getBillCode() +"' ";
					data.setBillType("入库单");
					break;
				default:
					select = null;
					break;
				}

				if(select == null)
					continue;
				
				username = this.stockCardListDao.getString(select);
				data.setUsername(username);
			}
		}
	}
}
