package uk.ac.ed.acp.cw2.data;


public record RegionCheckRequest(Position position, Region region) {

    public Boolean isValid() {
        return position.isValid() && region.isValid();
    }
}
