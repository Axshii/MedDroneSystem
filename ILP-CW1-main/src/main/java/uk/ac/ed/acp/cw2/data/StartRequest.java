package uk.ac.ed.acp.cw2.data;

import lombok.Data;

@Data
public class StartRequest {
    private Position start;
    private int angle;
}
