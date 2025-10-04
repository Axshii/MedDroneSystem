package uk.ac.ed.acp.cw2.ServiceControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ServiceControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // uid() tests
    @Test
    void returnUid() throws Exception {
        mockMvc.perform(get("/uid"))
                .andExpect(status().isOk())
                .andExpect(content().string("s2505201"));
    }

    // distanceTo() tests
    @Test
    void validDistanceTo_returnDistance() throws Exception {
        String json = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                }
                """;
        mockMvc.perform(get("/distanceTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("0.003616000000000952"));
    }

    @Test
    void invalidDistance_returnError() throws Exception {
        String json = """
                {
                    "position1": {
                        "lng": invalid,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                }
                """;
        mockMvc.perform(get("/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // isInRegion() tests
    @Test
    void pointOutsidePolygon_returnFalse() throws Exception {
        String json = """
                {
                    "position": {
                        "lng": 1.234,
                        "lat": 1.222
                    },
                    "region": {
                        "name": "central",
                        "vertices": [
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            }
                        ]
                    }
                }
                """;
        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void pointInsidePolygon_returnTrue() throws Exception {
        String json = """
                {
                    "position": {
                        "lng": -3.19,
                        "lat": 55.944
                    },
                    "region": {
                        "name": "central",
                        "vertices": [
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            }
                        ]
                    }
                }
                """;
        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void invalidVertices() throws Exception {
        String json = """
                {
                    "position": {
                        "lng": 1.234,
                        "lat": 1.222
                    },
                    "region": {
                        "name": "central",
                        "vertices": [
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.946233
                            }
                        ]
                    }
                }
                """;
        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }



}
