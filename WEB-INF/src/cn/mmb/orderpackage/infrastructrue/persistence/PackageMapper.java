/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年8月12日 下午12:42:38 
 * @version V1.0   
 */
package cn.mmb.orderpackage.infrastructrue.persistence;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

/** 
 * @ClassName: PackageMapper 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2015年8月12日 下午12:42:38  
 */
@Repository
public class PackageMapper extends AbstractDaoSupport {

	/** 
	 * @Description: 更新核对包裹签收时间
	 * @return int 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月7日 下午4:41:10 
	 */
	public int updateAuditPackage(String sql) {
		return this.getJdbcTemplate().update(sql);
	}
}
