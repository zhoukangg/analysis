package bupt.edu.cn.web.pojo;

public class StoryItem {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column story_item.id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column story_item.diagram_id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    private String diagramId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column story_item.description
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    private String description;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column story_item.story_id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    private String storyId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column story_item.id
     *
     * @return the value of story_item.id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column story_item.id
     *
     * @param id the value for story_item.id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column story_item.diagram_id
     *
     * @return the value of story_item.diagram_id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public String getDiagramId() {
        return diagramId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column story_item.diagram_id
     *
     * @param diagramId the value for story_item.diagram_id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public void setDiagramId(String diagramId) {
        this.diagramId = diagramId == null ? null : diagramId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column story_item.description
     *
     * @return the value of story_item.description
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column story_item.description
     *
     * @param description the value for story_item.description
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column story_item.story_id
     *
     * @return the value of story_item.story_id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public String getStoryId() {
        return storyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column story_item.story_id
     *
     * @param storyId the value for story_item.story_id
     *
     * @mbggenerated Wed Mar 13 10:43:18 CST 2019
     */
    public void setStoryId(String storyId) {
        this.storyId = storyId == null ? null : storyId.trim();
    }
}