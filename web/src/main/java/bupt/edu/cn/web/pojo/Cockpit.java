package bupt.edu.cn.web.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Cockpit {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.id
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.diagramIDs
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    private String diagramids;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.name
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.user_id
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    private String userId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.info
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    private String info;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.realtime
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    private Boolean realtime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.layoutConf
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    private String layoutconf;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cockpit.updatetime
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    private Date updatetime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.id
     *
     * @return the value of cockpit.id
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.id
     *
     * @param id the value for cockpit.id
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.diagramIDs
     *
     * @return the value of cockpit.diagramIDs
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public String getDiagramids() {
        return diagramids;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.diagramIDs
     *
     * @param diagramids the value for cockpit.diagramIDs
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setDiagramids(String diagramids) {
        this.diagramids = diagramids == null ? null : diagramids.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.name
     *
     * @return the value of cockpit.name
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.name
     *
     * @param name the value for cockpit.name
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.user_id
     *
     * @return the value of cockpit.user_id
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public String getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.user_id
     *
     * @param userId the value for cockpit.user_id
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.info
     *
     * @return the value of cockpit.info
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public String getInfo() {
        return info;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.info
     *
     * @param info the value for cockpit.info
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.realtime
     *
     * @return the value of cockpit.realtime
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public Boolean getRealtime() {
        return realtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.realtime
     *
     * @param realtime the value for cockpit.realtime
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setRealtime(Boolean realtime) {
        this.realtime = realtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.layoutConf
     *
     * @return the value of cockpit.layoutConf
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public String getLayoutconf() {
        return layoutconf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.layoutConf
     *
     * @param layoutconf the value for cockpit.layoutConf
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setLayoutconf(String layoutconf) {
        this.layoutconf = layoutconf == null ? null : layoutconf.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cockpit.updatetime
     *
     * @return the value of cockpit.updatetime
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cockpit.updatetime
     *
     * @param updatetime the value for cockpit.updatetime
     *
     * @mbggenerated Sat Jun 22 20:58:16 CST 2019
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}