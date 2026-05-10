package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// usuario (=Account en PeerTube)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("url")
    private String url;

    @JsonProperty("avatars")
    private List<Pictures> avatars;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public List<Pictures> getAvatars() { return avatars; }
    public void setAvatars(List<Pictures> avatars) { this.avatars = avatars; }
}
