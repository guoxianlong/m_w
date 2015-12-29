/**
 * 
 */
package adultadmin.service;

/**
 * @author Bomb
 * 
 */
public interface IServiceFactory {

	/**
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */

	public IAdminService createAdminService() throws IllegalAccessException,
			InstantiationException;

}
