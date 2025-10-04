package uk.ac.ed.acp.cw2.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.data.*;

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
    public double distanceTo(@RequestBody LngLatPairRequest request, HttpServletResponse response) {
        Position position1 = request.getPosition1();
        Position position2 = request.getPosition2();

        double pos1_lat = position1.getLat();
        double pos1_lng = position1.getLng();
        double pos2_lat = position2.getLat();
        double pos2_lng = position2.getLng();

        if ((pos1_lng > 0) || (pos2_lng > 0)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if ((pos1_lat < 0) || (pos2_lat < 0)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        double euclideanDistance = sqrt(pow((pos1_lng - pos2_lng), 2) + pow((pos1_lat - pos2_lat), 2));
        response.setStatus(HttpServletResponse.SC_OK);
        return euclideanDistance;
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@RequestBody LngLatPairRequest request, HttpServletResponse response) {
        Position position1 = request.getPosition1();
        Position position2 = request.getPosition2();

        double pos1_lat = position1.getLat();
        double pos1_lng = position1.getLng();
        double pos2_lat = position2.getLat();
        double pos2_lng = position2.getLng();

        // Longitude will always be negative
        if (pos1_lng > 0 || pos2_lng > 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        // Lattitude will always be positive
        else if (pos1_lat < 0 || pos2_lat < 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

        Position start = request.start();
        int angle = request.angle();

        // Angle can only be one of the 16 angles
        if ((angle % 22.5) != 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        double lattitude = start.getLat();
        double longitude = start.getLng();

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
        Position position = request.getPosition();
        Region region = request.getRegion();
        
        double lattitude_pos = position.getLat();
        double longitude_pos = position.getLng();
        String name = region.getName();
        List<Position> vertices = region.getVertices();

        // Region not closed by data points - invalid data
        Position first = vertices.getFirst();
        Position last = vertices.getLast();
        if (!first.equals(last)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        boolean inside = false;
        int n = vertices.size();
        for (int i = 0, j = n-1; i < n; j= i++) {
            double xi = vertices.get(i).getLng();
            double yi = vertices.get(i).getLat();
            double xj = vertices.get(j).getLng();
            double yj = vertices.get(j).getLat();

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
