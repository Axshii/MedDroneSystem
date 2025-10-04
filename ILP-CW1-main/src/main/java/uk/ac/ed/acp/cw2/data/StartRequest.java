package uk.ac.ed.acp.cw2.data;

import lombok.Builder;

@Builder
public record StartRequest(Position start, int angle) { }
