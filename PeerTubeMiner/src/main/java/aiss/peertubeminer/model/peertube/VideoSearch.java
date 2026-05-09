package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoSearch {

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("data")
    private List<Video> data;

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public List<Video> getData() { return data; }
    public void setData(List<Video> data) { this.data = data; }
}
