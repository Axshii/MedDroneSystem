package uk.ac.ed.acp.cw2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegionCheckRequest {
    @JsonProperty
    Position position;
    @JsonProperty
    Region region;
}
