package uk.ac.ed.acp.cw2.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ed.acp.cw2.data.*;

import java.net.URL;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    public double distanceTo(@RequestBody LngLatPairRequest request, HttpServletResponse response) {

        if (request == null || !request.position1().isValid() || !request.position2().isValid()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        Position position1 = request.position1();
        Position position2 = request.position2();

        double pos1_lat = position1.lat();
        double pos1_lng = position1.lng();
        double pos2_lat = position2.lat();
        double pos2_lng = position2.lng();

        if ((pos1_lng > 0) || (pos2_lng > 0) || (pos1_lat < 0) || (pos2_lat < 0)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid lattitude/ longitude values");
        }

        double euclideanDistance = sqrt(pow((pos1_lng - pos2_lng), 2) + pow((pos1_lat - pos2_lat), 2));
        response.setStatus(HttpServletResponse.SC_OK);
        return euclideanDistance;
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@RequestBody LngLatPairRequest request, HttpServletResponse response) {

        if (request == null || !request.position1().isValid() || !request.position2().isValid()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        assert request != null;
        Position position1 = request.position1();
        Position position2 = request.position2();

        double pos1_lat = position1.lat();
        double pos1_lng = position1.lng();
        double pos2_lat = position2.lat();
        double pos2_lng = position2.lng();

        // Longitude will always be negative and lattitude will always be positive
        if (pos1_lng > 0 || pos2_lng > 0 || pos1_lat < 0 || pos2_lat < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid lattitude/ longitude values");
        }

        double difference_lng = pos1_lng - pos2_lng;
        double difference_lat = pos1_lat - pos2_lat;

        double euclideanDistance = sqrt(pow(difference_lng, 2) + pow(difference_lat, 2));

        if (euclideanDistance < 0.00015) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

    }

    @PostMapping("/nextPosition")
    public Position nextPosition(@RequestBody StartRequest request, HttpServletResponse response) {

        double STEP_DEGREE = 0.00015;

        if (request == null || !request.isValid()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        Position start = request.start();
        int angle = request.angle();

        // Angle can only be one of the 16 angles
        if ((angle % 22.5) != 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Angle is not one of the 16 angles");
        }

        double lattitude = start.lat();
        double longitude = start.lng();

        double bearing_radians = Math.toRadians(angle);

        double degrees_lattitude = STEP_DEGREE * Math.cos(bearing_radians);
        double degrees_longitude = (STEP_DEGREE * Math.sin(bearing_radians)) / Math.cos(Math.toRadians(lattitude));

        double newLat = lattitude + degrees_lattitude;
        double newLng = longitude + degrees_longitude;
        Position newPosition = new Position(newLat, newLng);

        response.setStatus(HttpServletResponse.SC_OK);
        return newPosition;

    }

    @PostMapping("/isInRegion")
    public Boolean isInRegion(@RequestBody RegionCheckRequest request, HttpServletResponse response) {

        if (request == null || !request.isValid()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        Position position = request.position();
        Region region = request.region();

        if (!position.isValid() || !region.isValid()) {
            throw new ResponseStatusException(BAD_REQUEST);
        }

        double lattitude_pos = position.lat();
        double longitude_pos = position.lng();
        String name = region.name();
        List<Position> vertices = region.vertices();

        // Region not closed by data points - invalid data
        Position first = vertices.getFirst();
        Position last = vertices.getLast();
        if (!first.equals(last)) {
            throw new ResponseStatusException(BAD_REQUEST);
        }

        boolean inside = false;
        int n = vertices.size();
        for (int i = 0, j = n-1; i < n; j= i++) {
            double xi = vertices.get(i).lng();
            double yi = vertices.get(i).lat();
            double xj = vertices.get(j).lng();
            double yj = vertices.get(j).lat();

            System.out.println(xi + " " + yi + " " + xj + " " + yj);

            boolean intersect = ((yi > lattitude_pos) != (yj > lattitude_pos)) &&
                    (longitude_pos < (xj - xi) * (lattitude_pos - yi) / (yj - yi) + xi);

            if (intersect) {
                inside = true;
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return inside;
    }

}
