package adultadmin.action.bybs;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class BsbyForm  extends ActionForm{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content; 
	  public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}

	private FormFile file;
}
