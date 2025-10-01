package uk.ac.ed.acp.cw2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.classes.Position;
import uk.ac.ed.acp.cw2.classes.RequestPositions;
import uk.ac.ed.acp.cw2.classes.RequestStart;

import java.net.URL;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Controller class that handles various HTTP endpoints for the application.
 * Provides functionality for serving the index page, retrieving a static UUID,
 * and managing key-value pairs through POST requests.
 */
@RestController()
@RequestMapping("/api/v1")
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Value("${ilp.service.url}")
    public URL serviceUrl;


    @GetMapping("/")
    public String index() {
        return "<html><body>" +
                "<h1>Welcome from ILP</h1>" +
                "<h4>ILP-REST-Service-URL:</h4> <a href=\"" + serviceUrl + "\" target=\"_blank\"> " + serviceUrl+ " </a>" +
                "</body></html>";
    }

    @GetMapping("actuator/health")
    public String health() {
        return "test";
    }

    @GetMapping("/uid")
    public String uid() {
        return "s2505201";
    }

    @PostMapping("/distanceTo")
    public double distanceTo(@RequestBody RequestPositions request) {
        Position position1 = request.getPosition1();
        Position position2 = request.getPosition2();
        double euclideanDistance = sqrt(pow((position1.getLng()-position2.getLng()),2)+pow((position1.getLat()-position2.getLat()),2));
        return euclideanDistance;
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@RequestBody RequestPositions request) {
        Position position1 = request.getPosition1();
        Position position2 = request.getPosition2();
        double euclideanDistance = sqrt(pow((position1.getLng()-position2.getLng()),2)+pow((position1.getLat()-position2.getLat()),2));
        if (euclideanDistance < 0.00015) {
            return true;
        } else {
            return false;
        }
    }

//    @PostMapping("/nextPosition")
//    public String nextPosition(@RequestBody RequestStart request) {
//
//    }


}
