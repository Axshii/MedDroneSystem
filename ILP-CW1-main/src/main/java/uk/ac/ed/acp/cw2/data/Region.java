package uk.ac.ed.acp.cw2.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public record Region(String name, List<Position> vertices) {

    public Boolean isValid() {
        return name != null && !name.isEmpty() && !name.isBlank()
                && vertices != null && !vertices.isEmpty();
    }
}
