package uk.ac.ed.acp.cw2.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class Region {
    String name;
    List<Position> vertices;
}
