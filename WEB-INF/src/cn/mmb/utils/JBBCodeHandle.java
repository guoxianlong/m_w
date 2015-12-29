package cn.mmb.utils;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.jbarcodebean.JBarcodeBean;
import net.sourceforge.jbarcodebean.model.Code128;

public class JBBCodeHandle {
	
	public void createJBBCode(String content,HttpServletResponse response){
		
		JBarcodeBean jBarcodeBean = new JBarcodeBean();
		jBarcodeBean.setCodeType(new Code128());
		jBarcodeBean.setLabelPosition(JBarcodeBean.LABEL_BOTTOM);
		try {
			jBarcodeBean.setCode(content);
			BufferedImage image = new BufferedImage(175, 69, BufferedImage.TYPE_INT_RGB);
			image = jBarcodeBean.draw(image);
			ImageIO.write(image, "jpg", response.getOutputStream());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void createJBBCode(String content,OutputStream outputStream){
		
		JBarcodeBean jBarcodeBean = new JBarcodeBean();
		jBarcodeBean.setCodeType(new Code128());
		jBarcodeBean.setLabelPosition(JBarcodeBean.LABEL_BOTTOM);
		try {
			jBarcodeBean.setCode(content);
			BufferedImage image = new BufferedImage(175, 69, BufferedImage.TYPE_INT_RGB);
			image = jBarcodeBean.draw(image);
			ImageIO.write(image, "jpeg", outputStream);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
