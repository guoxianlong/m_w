package cn.mmb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("configInfo")
public class ConfigInfo {
	@Value("${http.sale.serviceUrl}")
	private String saleHttpServiceUrl;
	
	@Value("${mmb.qingdan.printer.name}")
	private String mmbQingdanPrinterName;

	public String getSaleHttpServiceUrl() {
		return saleHttpServiceUrl;
	}
	
	public String getMmbQingdanPrinterName() {
		return mmbQingdanPrinterName;
	}
}
