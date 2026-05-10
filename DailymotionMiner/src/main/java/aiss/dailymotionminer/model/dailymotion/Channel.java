package aiss.dailymotionminer.model.dailymotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

    @JsonProperty("id")
    private String id;

    @JsonProperty("screenname")
    private String screenname;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_time")
    private Long createdTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getScreenname() { return screenname; }
    public void setScreenname(String screenname) { this.screenname = screenname; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCreatedTime() { return createdTime; }
    public void setCreatedTime(Long createdTime) { this.createdTime = createdTime; }
}
