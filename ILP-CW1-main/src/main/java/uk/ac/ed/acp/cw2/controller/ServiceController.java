package uk.ac.ed.acp.cw2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.data.LngLatPairRequest;
import uk.ac.ed.acp.cw2.data.Position;
import uk.ac.ed.acp.cw2.data.Region;
import uk.ac.ed.acp.cw2.data.StartRequest;

import java.net.URL;
import java.util.List;

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
                "<h4>ILP-REST-Service-URL:</h4> <a href=\"" + serviceUrl + "\" target=\"_blank\"> " + serviceUrl + " </a>" +
                "</body></html>";
    }

    @GetMapping("actuator/health")
    public void health() { }

    @GetMapping("/uid")
    public String uid() {
        return "s2505201";
    }

    @PostMapping("/distanceTo")
    public double distanceTo(@RequestBody LngLatPairRequest request) {
        Position position1 = request.getPosition1();
        Position position2 = request.getPosition2();
        double euclideanDistance = sqrt(pow((position1.lng() - position2.lng()), 2) + pow((position1.lat() - position2.lat()), 2));
        return euclideanDistance;
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@RequestBody LngLatPairRequest request) {
        Position position1 = request.getPosition1();
        Position position2 = request.getPosition2();

        // Longitude will always be negative
        if (position1.lng() > 0 || position2.lng() > 0) {
            ResponseEntity.badRequest();
        }
        // Lattitude will always be positive
        else if (position1.lat() < 0 || position2.lat() < 0) {
            ResponseEntity.badRequest();
        }

        double difference_lng = position1.lng() - position2.lng();
        double difference_lat = position1.lat() - position2.lat();

        double euclideanDistance = sqrt(pow(difference_lng, 2) + pow(difference_lat, 2));

        if (euclideanDistance < 0.00015) {
            return true;
        } else {
            return false;
        }
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<Position> nextPosition(@RequestBody StartRequest request) {

        double STEP_DEGREE = 0.00015;

        Position start = request.getStart();
        int angle = request.getAngle();

        if ((angle % 22.5) != 0) {
            return ResponseEntity.badRequest().build();
        }

        double lattitude = start.lat();
        double longitude = start.lng();

        double bearing_radians = Math.toRadians(angle);

        double degrees_lattitude = STEP_DEGREE * Math.cos(bearing_radians);
        double degrees_longitude = (STEP_DEGREE * Math.sin(bearing_radians)) / Math.cos(Math.toRadians(lattitude));

        double newLat = lattitude + degrees_lattitude;
        double newLng = longitude + degrees_longitude;
        Position newPosition = new Position(newLat, newLng);

        return ResponseEntity.ok(newPosition);

    }

    @PostMapping("/isInRegion")
    public ResponseEntity<Object> isInRegion(@RequestBody Region region) {
        String name = region.name();
        List<Position> vertices = region.vertices();

        // Region not closed by data points - invalid data
        if (vertices.getFirst() != vertices.getLast()) {
            return ResponseEntity.badRequest().build();
        }


        return null;
    }

}
