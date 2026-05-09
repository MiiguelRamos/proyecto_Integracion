package aiss.peertubeminer.model.videominer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VMUser {

    @JsonProperty("name")
    private String name;

    @JsonProperty("user_link")
    private String userLink;

    @JsonProperty("picture_link")
    private String pictureLink;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUserLink() { return userLink; }
    public void setUserLink(String userLink) { this.userLink = userLink; }
    public String getPictureLink() { return pictureLink; }
    public void setPictureLink(String pictureLink) { this.pictureLink = pictureLink; }
}
