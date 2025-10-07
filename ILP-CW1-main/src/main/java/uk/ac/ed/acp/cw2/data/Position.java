package uk.ac.ed.acp.cw2.data;

public record Position(Double lng, Double lat) {

    public Boolean isValid() {
        return lng != null && lat != null
                &&  !lng.isNaN() && !lat.isNaN()
                && !lng.isInfinite()  && !lat.isInfinite();
    }

}
