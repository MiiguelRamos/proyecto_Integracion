package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptionSearch {

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("data")
    private List<Caption> data;

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public List<Caption> getData() { return data; }
    public void setData(List<Caption> data) { this.data = data; }
}
