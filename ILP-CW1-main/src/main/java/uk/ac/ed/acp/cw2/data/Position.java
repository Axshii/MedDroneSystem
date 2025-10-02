package uk.ac.ed.acp.cw2.data;

import lombok.Builder;

@Builder
public record Position(double lng, double lat) {
    @Override public String toString() {
        return "{ lng: " + lng + ", lat: " + lat + "}";
    }
}
