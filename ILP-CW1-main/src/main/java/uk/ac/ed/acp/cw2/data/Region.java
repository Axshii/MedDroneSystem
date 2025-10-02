package uk.ac.ed.acp.cw2.data;

import lombok.Builder;

import java.util.List;

@Builder
public record Region(String name, List<Position> vertices) {
}
