package uk.ac.ed.acp.cw2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Position {
    @JsonProperty
    double lng;
    @JsonProperty
    double lat;

    public Position(double newLat, double newLng) {
        this.lng = newLng;
        this.lat = newLat;
    }

    @Override public String toString() {
        return "{ lng: " + lng + ", lat: " + lat + "}";
    }
}
