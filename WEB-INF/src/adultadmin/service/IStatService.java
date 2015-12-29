/**
 * 
 */
package adultadmin.service;


/**
 * @author Bomb
 * 
 */

public interface IStatService {

	public void close();
	
    public int getNumber(int id);
    public void setNumber(int id, int number);
	
}
