/*
 * Created on 2005-11-15
 *
 */
package adultadmin.action.vo;

/**
 * @author bomb
 *  
 */
public class voConfig {
	private int id;
	private String uploadImage;		// 上传产品图片路径
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return Returns the uploadImage.
	 */
	public String getUploadImage() {
		return uploadImage;
	}
	/**
	 * @param uploadImage The uploadImage to set.
	 */
	public void setUploadImage(String uploadImage) {
		this.uploadImage = uploadImage;
	}
	
}
