package adultadmin.util;

public class MyException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyException (String message) {
		super(message);
	}
	
	public void printStackTrace(){
		System.out.println(DateUtil.getNow());
		super.printStackTrace();
	}

}
