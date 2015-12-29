/*
 * Created on 2007-12-24
 *
 */
package adultadmin.action.vo;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-12-24
 * 
 * 说明：原user表由于保存了自动注册的用户，数据量非常大，不再做改动。
 * 
 * 而user_info表用于保存user的一些user表里的字段没有的信息。
 * 
 * 特别注意：user表里有记录，user_info表不一定有。
 */
public class UserInfoBean {
    public int id;

    public String email;

    /**
     * 0: 男<br />
     * 1: 女<br />
     */
    public int gender;

    public String phone2;

    public int country;

    public int province;

    public int city;

    public int town;

    public int point; //当前积分

    public int totalPoint; //获得过的总积分

    public int rank; //等级

    public String lastOperTime; //最后操作时间

    public int adminId; //对应的客服人员的ID

    public String adminName; //对应的客服人员的用户名

    /**
     * @return Returns the adminName.
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * @param adminName
     *            The adminName to set.
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * @return Returns the adminId.
     */
    public int getAdminId() {
        return adminId;
    }

    /**
     * @param adminId
     *            The adminId to set.
     */
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    /**
     * @return Returns the lastOperTime.
     */
    public String getLastOperTime() {
        return lastOperTime;
    }

    /**
     * @param lastOperTime
     *            The lastOperTime to set.
     */
    public void setLastOperTime(String lastOperTime) {
        this.lastOperTime = lastOperTime;
    }

    /**
     * @return Returns the city.
     */
    public int getCity() {
        return city;
    }

    /**
     * @param city
     *            The city to set.
     */
    public void setCity(int city) {
        this.city = city;
    }

    /**
     * @return Returns the country.
     */
    public int getCountry() {
        return country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(int country) {
        this.country = country;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the gender.
     */
    public int getGender() {
        return gender;
    }

    /**
     * @param gender
     *            The gender to set.
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the phone2.
     */
    public String getPhone2() {
        return phone2;
    }

    /**
     * @param phone2
     *            The phone2 to set.
     */
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * @return Returns the point.
     */
    public int getPoint() {
        return point;
    }

    /**
     * @param point
     *            The point to set.
     */
    public void setPoint(int point) {
        this.point = point;
    }

    /**
     * @return Returns the province.
     */
    public int getProvince() {
        return province;
    }

    /**
     * @param province
     *            The province to set.
     */
    public void setProvince(int province) {
        this.province = province;
    }

    /**
     * @return Returns the rank.
     */
    public int getRank() {
        return rank;
    }

    /**
     * @param rank
     *            The rank to set.
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @return Returns the totalPoint.
     */
    public int getTotalPoint() {
        return totalPoint;
    }

    /**
     * @param totalPoint
     *            The totalPoint to set.
     */
    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    /**
     * @return Returns the town.
     */
    public int getTown() {
        return town;
    }

    /**
     * @param town
     *            The town to set.
     */
    public void setTown(int town) {
        this.town = town;
    }

    public String getGenderName(){
    	if(this.gender == 1){
    		return "女";
    	} else {
    		return "男";
    	}
    }
}
