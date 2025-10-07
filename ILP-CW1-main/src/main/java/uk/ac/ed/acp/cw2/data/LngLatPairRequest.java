package uk.ac.ed.acp.cw2.data;

public record LngLatPairRequest(Position position1, Position position2) {

    public Boolean isValid() {
        return position1.isValid() && position2.isValid();
    }
}
