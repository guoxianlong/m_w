package adultadmin.bean.barcode;

public class ProductBatchBarcodeBean {

	public int id;    //ID
	
	public int productId;   //产品ID
	
	public String productBarcode; //产品条码
	
	public String batchCode;   //产品批次号
	
	public String batchBarcode; //产品批次条码
	
	public String batchBarcodeEndCode;  //产品批次条码最后三位(批次位)
	
	public int batchCount;     //批次数量(用于显示)

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public String getProductBarcode() {
		return productBarcode;
	}

	public void setProductBarcode(String productBarcode) {
		this.productBarcode = productBarcode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getBatchBarcode() {
		return batchBarcode;
	}

	public void setBatchBarcode(String batchBarcode) {
		this.batchBarcode = batchBarcode;
	}

	public String getBatchBarcodeEndCode() {
		return batchBarcodeEndCode;
	}

	public void setBatchBarcodeEndCode(String batchBarcodeEndCode) {
		this.batchBarcodeEndCode = batchBarcodeEndCode;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}
	
}
