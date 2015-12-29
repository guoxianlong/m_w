/*
 * Created on 2007-11-14
 *
 */
package adultadmin.bean.stock;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：
 */
public class ProductGroupBean {
    public int id;

    public String name;

    public String createDatetime;

    /**
     * @return Returns the createDatetime.
     */
    public String getCreateDatetime() {
        return createDatetime;
    }
    /**
     * @param createDatetime The createDatetime to set.
     */
    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }
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
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
