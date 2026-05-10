package aiss.dailymotionminer.model.dailymotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subtitle {

    @JsonProperty("id")
    private String id;

    @JsonProperty("language")
    private String language;

    @JsonProperty("url")
    private String url;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
