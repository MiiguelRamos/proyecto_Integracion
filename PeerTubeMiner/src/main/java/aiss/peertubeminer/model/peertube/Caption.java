package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Caption {

    @JsonProperty("language")
    private Language language;

    @JsonProperty("captionPath")
    private String captionPath;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getCaptionPath() {
        return captionPath;
    }

    public void setCaptionPath(String captionPath) {
        this.captionPath = captionPath;
    }
}
