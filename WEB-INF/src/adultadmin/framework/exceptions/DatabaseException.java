/**
 * 
 */
package adultadmin.framework.exceptions;

/**
 * @author Bomb
 *
 */
public class DatabaseException extends BaseException {
	
	public DatabaseException(){
		super();
	}
	
	public DatabaseException(String message){
		super(message);
	}
	
	public DatabaseException(String message, Throwable cause){
		super(message,cause);
	}
	
	public DatabaseException(Throwable cause){
		super(cause);
	} 
}
